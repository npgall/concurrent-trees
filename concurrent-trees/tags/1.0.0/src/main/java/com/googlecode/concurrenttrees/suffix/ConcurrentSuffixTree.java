/**
 * Copyright 2012 Niall Gallagher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.concurrenttrees.suffix;

import com.googlecode.concurrenttrees.common.CharSequenceUtil;
import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * An implementation of {@link SuffixTree} which supports lock-free concurrent reads, and allows items to be
 * added to and to be removed from the tree <i>atomically</i> by background thread(s), without blocking reads.
 * <p/>
 * This implementation is based on {@link ConcurrentRadixTree}.
 *
 * @author Niall Gallagher
 */
public class ConcurrentSuffixTree<O> implements SuffixTree<O>, PrettyPrintable {

    class ConcurrentSuffixTreeImpl<V> extends ConcurrentRadixTree<V> {

        public ConcurrentSuffixTreeImpl(NodeFactory nodeFactory) {
            super(nodeFactory);
        }

        public ConcurrentSuffixTreeImpl(NodeFactory nodeFactory, boolean restrictConcurrency) {
            super(nodeFactory, restrictConcurrency);
        }

        @Override
        protected void acquireWriteLock() {
            super.acquireWriteLock();
        }

        @Override
        protected void releaseWriteLock() {
            super.releaseWriteLock();
        }
    }

    private final ConcurrentSuffixTreeImpl<Set<String>> radixTree;
    private final ConcurrentMap<String, O> valueMap;

    /**
     * Creates a new {@link ConcurrentSuffixTree} which will use the given {@link NodeFactory} to create nodes.
     *
     * @param nodeFactory An object which creates {@link com.googlecode.concurrenttrees.radix.node.Node} objects
     * on-demand, and which might return node implementations optimized for storing the values supplied to it for
     * the creation of each node
     */
    public ConcurrentSuffixTree(NodeFactory nodeFactory) {
        this.radixTree = new ConcurrentSuffixTreeImpl<Set<String>>(nodeFactory);
        this.valueMap = new ConcurrentHashMap<String, O>();
    }

    /**
     * Creates a new {@link ConcurrentSuffixTree} which will use the given {@link NodeFactory} to create nodes.
     *
     * @param nodeFactory An object which creates {@link com.googlecode.concurrenttrees.radix.node.Node} objects
     * on-demand, and which might return node implementations optimized for storing the values supplied to it for the
     * creation of each node
     * @param restrictConcurrency If true, configures use of a {@link java.util.concurrent.locks.ReadWriteLock} allowing
     * concurrent reads, except when writes are being performed by other threads, in which case writes block all reads;
     * if false, configures lock-free reads; allows concurrent non-blocking reads, even if writes are being performed
     * by other threads
     */
    public ConcurrentSuffixTree(NodeFactory nodeFactory, boolean restrictConcurrency) {
        this.radixTree = new ConcurrentSuffixTreeImpl<Set<String>>(nodeFactory, restrictConcurrency);
        this.valueMap = new ConcurrentHashMap<String, O>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public O put(CharSequence key, O value) {
        if (key == null) {
            throw new IllegalArgumentException("The key argument was null");
        }
        if (key.length() == 0) {
            throw new IllegalArgumentException("The key argument was zero-length");
        }
        if (value == null) {
            throw new IllegalArgumentException("The value argument was null");
        }
        radixTree.acquireWriteLock();
        try {
            // We convert to string (for now) due to lack of equals() and hashCode() support in CharSequence...
            String keyString = CharSequenceUtil.toString(key);

            // Put/replace value in map before we add suffixes to the tree
            // (prevents reading threads finding suffixes with no value)...
            final O replacedValue = valueMap.put(keyString, value);

            // We only need to modify the tree if we have not added this key before...
            if (replacedValue == null) {
                addSuffixesToRadixTree(keyString);
            }
            return replacedValue; // might be null
        }
        finally {
            radixTree.releaseWriteLock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public O putIfAbsent(CharSequence key, O value) {
        radixTree.acquireWriteLock();
        try {
            // We convert to string (for now) due to lack of equals() and hashCode() support in CharSequence...
            String keyString = CharSequenceUtil.toString(key);

            // Put/replace value in map only if key is absent, before we add suffixes to the tree
            // (prevents reading threads finding suffixes with no value)...
            final O existingValue = valueMap.putIfAbsent(keyString, value);

            // We only need to modify the tree if we have not added this key before...
            if (existingValue == null) {
                // Key is not already in tree, add it now...
                addSuffixesToRadixTree(keyString);
            }
            // else we have not made any changes

            return existingValue; // might be null
        }
        finally {
            radixTree.releaseWriteLock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(CharSequence key) {
        radixTree.acquireWriteLock();
        try {
            // We convert to string (for now) due to lack of equals() and hashCode() support in CharSequence...
            String keyString = CharSequenceUtil.toString(key);
            O value = valueMap.get(keyString);

            if (value == null) {
                // Key was not stored, no need to do anything, return false...
                return false;
            }
            // Remove suffixes from the tree...
            removeSuffixesFromRadixTree(keyString);
            valueMap.remove(keyString);
            return true;
        }
        finally {
            radixTree.releaseWriteLock();
        }
    }

    void addSuffixesToRadixTree(String keyAsString) {
        Iterable<CharSequence> suffixes = CharSequenceUtil.generateSuffixes(keyAsString);
        for (CharSequence suffix : suffixes) {
            Set<String> originalKeyRefs = radixTree.getValueForExactKey(suffix);
            if (originalKeyRefs == null) {
                originalKeyRefs = createSetForOriginalKeys();
                radixTree.put(suffix, originalKeyRefs);
            }
            originalKeyRefs.add(keyAsString);
        }
    }

    void removeSuffixesFromRadixTree(String keyAsString) {
        Iterable<CharSequence> suffixes = CharSequenceUtil.generateSuffixes(keyAsString);
        for (CharSequence suffix : suffixes) {
            Set<String> originalKeyRefs = radixTree.getValueForExactKey(suffix);
            originalKeyRefs.remove(keyAsString);

            if (originalKeyRefs.isEmpty()) {
                // We just removed the last original key which shares this suffix.
                // Remove the suffix from the tree entirely...
                radixTree.remove(suffix);
            }
            // else leave the suffix in the tree, as it is a common suffix of another key.
        }
    }

    /**
     * Creates a new {@link Set} in which original keys from which a suffix was generated can be stored.
     * <p/>
     * By default this method creates a new concurrent set based on {@link ConcurrentHashMap}.
     * <p/>
     * Subclasses could override this method to create an alternative set.
     * <p/>
     * Specifically it is expected that this would be useful in unit tests,
     * where sets with consistent iteration order would be useful.
     *
     * @return A new {@link Set} in which original keys from which a suffix was generated can be stored
     */
    protected Set<String> createSetForOriginalKeys() {
        return Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public O getValueForExactKey(CharSequence key) {
        // We convert to string (for now) due to lack of equals() and hashCode() support in CharSequence...
        String keyString = CharSequenceUtil.toString(key);
        return valueMap.get(keyString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<CharSequence> getKeysEndingWith(CharSequence suffix) {
        Set<? extends CharSequence> originalKeys = radixTree.getValueForExactKey(suffix);
        if (originalKeys == null) {
            return Collections.emptySet();
        }
        // Cast to Set<CharSequence>, as we have internally implemented tree with strings...
        @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
        Set<CharSequence> results = (Set<CharSequence>) originalKeys;
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<O> getValuesForKeysEndingWith(CharSequence suffix) {
        Set<String> originalKeys = radixTree.getValueForExactKey(suffix);
        if (originalKeys == null) {
            return Collections.emptySet();
        }
        Set<O> results = new LinkedHashSet<O>();
        for (String originalKey : originalKeys) {
            O value = valueMap.get(originalKey);
            // Delegate to helper method to facilitate unit testing...
            addIfNotNull(value, results);
            // else race condition, key/value was removed while iterating, skip value for that key
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<KeyValuePair<O>> getKeyValuePairsForKeysEndingWith(CharSequence suffix) {
        Set<String> originalKeys = radixTree.getValueForExactKey(suffix);
        if (originalKeys == null) {
            return Collections.emptySet();
        }
        Set<KeyValuePair<O>> results = new HashSet<KeyValuePair<O>>(originalKeys.size());
        for (String originalKey : originalKeys) {
            O value = valueMap.get(originalKey);
            // Delegate to helper method to facilitate unit testing...
            addIfNotNull(originalKey, value, results);
            // else race condition, key/value was removed while iterating, skip KeyValuePair for that key
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<CharSequence> getKeysContaining(CharSequence fragment) {
        Collection<Set<String>> originalKeysSets = radixTree.getValuesForKeysStartingWith(fragment);
        Set<CharSequence> results = new LinkedHashSet<CharSequence>();
        for (Set<String> originalKeySet : originalKeysSets) {
            for (String originalKey : originalKeySet) {
                results.add(originalKey);
            }
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<O> getValuesForKeysContaining(CharSequence fragment) {
        Collection<Set<String>> originalKeysSets = radixTree.getValuesForKeysStartingWith(fragment);
        Set<O> results = new LinkedHashSet<O>();
        for (Set<String> originalKeySet : originalKeysSets) {
            for (String originalKey : originalKeySet) {
                O value = valueMap.get(originalKey);
                // Delegate to helper method to facilitate unit testing...
                addIfNotNull(value, results);
                // else race condition, key/value was removed while iterating, skip value for that key
            }
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<KeyValuePair<O>> getKeyValuePairsForKeysContaining(CharSequence fragment) {
        Collection<Set<String>> originalKeysSets = radixTree.getValuesForKeysStartingWith(fragment);
        Set<KeyValuePair<O>> results = new LinkedHashSet<KeyValuePair<O>>();
        for (Set<String> originalKeySet : originalKeysSets) {
            for (String originalKey : originalKeySet) {
                O value = valueMap.get(originalKey);
                // Delegate to helper method to facilitate unit testing...
                addIfNotNull(originalKey, value, results);
                // else race condition, key/value was removed while iterating, skip KeyValuePair for that key
            }
        }
        return results;
    }
    
    /**
     * Utility method to add a value to the set if the value is not null.
     * Logic is factored out to this method to support unit testing where value is only null if race conditions occur.
     */
    @SuppressWarnings({"JavaDoc"})
    static <O> void addIfNotNull(O value, Collection<O> results) {
        if (value != null) {
            results.add(value);
        }
    }

    /**
     * Utility method to add a {@link KeyValuePair} to the set if the value is not null.
     * Logic is factored out to this method to support unit testing where value is only null if race conditions occur.
     */
    @SuppressWarnings({"JavaDoc"})
    static <O> void addIfNotNull(String key, O value, Collection<KeyValuePair<O>> results) {
        if (value != null) {
            results.add(new ConcurrentRadixTree.KeyValuePairImpl<O>(key, value));
        }
    }

    @Override
    public Node getNode() {
        return radixTree.getNode();
    }
}
