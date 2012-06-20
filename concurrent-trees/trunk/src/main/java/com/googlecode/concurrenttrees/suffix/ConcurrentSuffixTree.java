package com.googlecode.concurrenttrees.suffix;

import com.googlecode.concurrenttrees.common.CharSequenceUtil;
import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;
import com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotation;
import com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotationFactory;

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
     * <p/>
     * As a temporary measure, allows the concurrency of {@link ConcurrentSuffixTree} to be restricted in the face of
     * writes, for safety until the algorithms in {@link ConcurrentSuffixTree} which support multi-threaded
     * reads while writes are ongoing can be more thoroughly tested.
     *
     * @param nodeFactory An object which creates {@link com.googlecode.concurrenttrees.radix.node.Node} objects
     * on-demand, and which might return node implementations optimized for storing the values supplied to it for the
     * creation of each node
     * @param restrictConcurrency If true, configures use of a {@link java.util.concurrent.locks.ReadWriteLock} allowing
     * concurrent reads, except when writes are being performed by other threads, in which case writes block all reads;
     * if false, configures lock-free reads; allows concurrent non-blocking reads, even if writes are being performed
     * by other threads
     * @deprecated This method allowing concurrency to be restricted will be removed in future.
     */
    @Deprecated
    public ConcurrentSuffixTree(NodeFactory nodeFactory, boolean restrictConcurrency) {
        //noinspection deprecation
        this.radixTree = new ConcurrentSuffixTreeImpl<Set<String>>(nodeFactory, restrictConcurrency);
        this.valueMap = new ConcurrentHashMap<String, O>();
    }

    @Override
    public O get(CharSequence key) {
        String keyString = CharSequenceUtil.toString(key);
        return valueMap.get(keyString);
    }

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
            Set<String> originalKeyRefs = radixTree.get(suffix);
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
            Set<String> originalKeyRefs = radixTree.get(suffix);
            originalKeyRefs.remove(keyAsString);

            if (originalKeyRefs.isEmpty()) {
                // We just removed the last original key which shares this suffix.
                // Remove the suffix from the tree entirely...
                radixTree.remove(suffix);
            }
            // else leave the suffix in the tree, as it is a common suffix of another key.
        }
    }

    @Override
    public Set<CharSequence> getKeysStartingWith(CharSequence prefix) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Collection<O> getValuesForKeysStartingWith(CharSequence prefix) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Set<KeyValuePair<O>> getKeyValuePairsForKeysStartingWith(CharSequence prefix) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Set<CharSequence> getKeysEndingWith(CharSequence suffix) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Collection<O> getValuesForKeysEndingWith(CharSequence suffix) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Set<KeyValuePair<O>> getKeyValuePairsForKeysEndingWith(CharSequence suffix) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Set<CharSequence> getKeysContaining(CharSequence prefix) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Collection<O> getValuesForKeysContaining(CharSequence prefix) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Set<KeyValuePair<O>> getKeyValuePairsForKeysContaining(CharSequence prefix) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Node getNode() {
        return radixTree.getNode();
    }
}
