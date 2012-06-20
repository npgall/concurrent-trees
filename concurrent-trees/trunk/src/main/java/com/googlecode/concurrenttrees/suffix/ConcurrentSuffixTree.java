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
 * @author Niall Gallagher
 */
public class ConcurrentSuffixTree<O> implements SuffixTree<O>, PrettyPrintable {

    class ConcurrentSuffixTreeImpl<V> extends ConcurrentRadixTree<V> {

        public ConcurrentSuffixTreeImpl(NodeFactory nodeFactory) {
            super(nodeFactory);
        }

        public ConcurrentSuffixTreeImpl(NodeFactory nodeFactory, boolean restrictConcurrency) {
            //noinspection deprecation
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
        radixTree.acquireWriteLock();
        try {
            // We convert to string (for now) due to lack of equals() and hashCode() support in CharSequence.
            // TODO: optimize/avoid converting to string. Although if already a string, this is a no-op...
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
            // We convert to string (for now) due to lack of equals() and hashCode() support in CharSequence.
            // TODO: optimize/avoid converting to string. Although if already a string, this is a no-op...
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
            // We convert to string (for now) due to lack of equals() and hashCode() support in CharSequence.
            // TODO: optimize/avoid converting to string. Although if already a string, this is a no-op...
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
                originalKeyRefs = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
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
     * {@inheritDoc}
     */
    @Override
    public O getValueForExactKey(CharSequence key) {
        // We convert to string (for now) due to lack of equals() and hashCode() support in CharSequence.
        // TODO: optimize/avoid converting to string. Although if already a string, this is a no-op...
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
    public Collection<O> getValuesForKeysEndingWith(CharSequence suffix) {
        Set<String> originalKeys = radixTree.getValueForExactKey(suffix);
        if (originalKeys == null) {
            return Collections.emptySet();
        }
        List<O> results = new ArrayList<O>(originalKeys.size());
        for (String originalKey : originalKeys) {
            O value = valueMap.get(originalKey);
            if (value != null) {
                results.add(value);
            }
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
            if (value != null) {
                results.add(new ConcurrentRadixTree.KeyValuePairImpl<O>(originalKey, value));
            }
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
        List<O> results = new LinkedList<O>();
        for (Set<String> originalKeySet : originalKeysSets) {
            for (String originalKey : originalKeySet) {
                O value = valueMap.get(originalKey);
                if (value != null) {
                    results.add(value);
                }
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
                if (value != null) {
                    results.add(new ConcurrentRadixTree.KeyValuePairImpl<O>(originalKey, value));
                }
                // else race condition, key/value was removed while iterating, skip KeyValuePair for that key
            }
        }
        return results;
    }

    @Override
    public Node getNode() {
        return radixTree.getNode();
    }
}
