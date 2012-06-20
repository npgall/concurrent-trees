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

    private final SuffixAnnotationFactory<CharSequence> suffixAnnotationFactory;
    // Associate the original document with
    private final ConcurrentSuffixTreeImpl<SuffixAnnotation<CharSequence>> radixTree;
    private final ConcurrentMap<String, O> valueMap;

    /**
     * Creates a new {@link ConcurrentSuffixTree} which will use the given {@link NodeFactory} to create nodes.
     *
     * @param nodeFactory An object which creates {@link com.googlecode.concurrenttrees.radix.node.Node} objects
     * on-demand, and which might return node implementations optimized for storing the values supplied to it for
     * the creation of each node
     * @param suffixAnnotationFactory An object which creates {@link com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotation} objects
     * on-demand, and which might return {@link com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotation} object implementations optimized for storing the values
     * supplied to it for the creation of each object
     */
    public ConcurrentSuffixTree(NodeFactory nodeFactory, SuffixAnnotationFactory<CharSequence> suffixAnnotationFactory) {
        this.suffixAnnotationFactory = suffixAnnotationFactory;
        this.radixTree = new ConcurrentSuffixTreeImpl<SuffixAnnotation<CharSequence>>(nodeFactory);
        this.valueMap = new ConcurrentHashMap<String, O>();
    }

    /**
     * Creates a new {@link ConcurrentSuffixTree} which will use the given {@link NodeFactory} to create nodes.
     * <p/>
     * As a temporary measure, allows the concurrency of {@link ConcurrentSuffixTree} to be restricted in the face of
     * writes, for safety until the algorithms in {@link ConcurrentSuffixTree} which support multi-threaded
     * reads while writes are ongoing can be more thoroughly tested.
     *
     *
     * @param nodeFactory An object which creates {@link com.googlecode.concurrenttrees.radix.node.Node} objects
     * on-demand, and which might return node implementations optimized for storing the values supplied to it for the
     * creation of each node
     * @param suffixAnnotationFactory An object which creates {@link com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotation} objects
     * on-demand, and which might return {@link com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotation} object implementations optimized for storing the values
     * supplied to it for the creation of each object
     * @param restrictConcurrency If true, configures use of a {@link java.util.concurrent.locks.ReadWriteLock} allowing
     * concurrent reads, except when writes are being performed by other threads, in which case writes block all reads;
     * if false, configures lock-free reads; allows concurrent non-blocking reads, even if writes are being performed
     * by other threads
     * @deprecated This method allowing concurrency to be restricted will be removed in future.
     */
    @Deprecated
    public ConcurrentSuffixTree(NodeFactory nodeFactory, SuffixAnnotationFactory<CharSequence> suffixAnnotationFactory, boolean restrictConcurrency) {
        this.suffixAnnotationFactory = suffixAnnotationFactory;
        //noinspection deprecation
        this.radixTree = new ConcurrentSuffixTreeImpl<SuffixAnnotation<CharSequence>>(nodeFactory, restrictConcurrency);
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
            // Create string version of char sequence for use as key in value map.
            // TODO: optimize/avoid converting to string. Although if already a string, this is a no-op...
            String keyString = CharSequenceUtil.toString(key);
            O previousValue = valueMap.get(keyString);

            addSuffixesToRadixTree(key);
            valueMap.put(keyString, value);
            return previousValue;
        }
        finally {
            radixTree.releaseWriteLock();
        }
    }

    @Override
    public O putIfAbsent(CharSequence key, O value) {
        radixTree.acquireWriteLock();
        try {
            // Create string version of char sequence for use as key in value map.
            // TODO: optimize/avoid converting to string. Although if already a string, this is a no-op...
            String keyString = CharSequenceUtil.toString(key);
            O previousValue = valueMap.get(keyString);
            if (previousValue != null) {
                return previousValue;
            }
            addSuffixesToRadixTree(key);
            valueMap.put(keyString, value);
            return previousValue; // should be null
        }
        finally {
            radixTree.releaseWriteLock();
        }
    }

    void addSuffixesToRadixTree(CharSequence key) {
        Iterable<CharSequence> suffixes = CharSequenceUtil.generateSuffixes(key);
        for (Iterator<CharSequence> iterator = suffixes.iterator(); iterator.hasNext(); ) {
            CharSequence suffix = iterator.next();
            boolean isExactMatchForKey = !iterator.hasNext();
            SuffixAnnotation<CharSequence> suffixAnnotation = radixTree.get(suffix);
            if (suffixAnnotation == null) {
                // Create new metadata...
                List<CharSequence> documentsEndingWithSuffix = Arrays.asList(suffix);
                CharSequence originalKeyEqualToSuffix = isExactMatchForKey ? suffix : null;
                suffixAnnotation = suffixAnnotationFactory.createSuffixAnnotation(documentsEndingWithSuffix, originalKeyEqualToSuffix);
                radixTree.put(suffix, suffixAnnotation);
            }
            else {
                // Update existing metadata...
                // TODO: optimize for specific implementation of DefaultSuffixAnnotation - avoid recreating lists?
                Collection<CharSequence> existingKeysEndingWithSuffix = suffixAnnotation.getOriginalKeys();
                List<CharSequence> newKeysEndingWithSuffix = new ArrayList<CharSequence>(existingKeysEndingWithSuffix.size() + 1);
                newKeysEndingWithSuffix.addAll(existingKeysEndingWithSuffix);
                newKeysEndingWithSuffix.add(suffix);

                CharSequence originalKeyEqualToSuffix = suffixAnnotation.getOriginalKeyEqualToSuffix();
                if (isExactMatchForKey) {
                    originalKeyEqualToSuffix = suffix;
                }
                suffixAnnotation = suffixAnnotationFactory.createSuffixAnnotation(newKeysEndingWithSuffix, originalKeyEqualToSuffix);
                // Replace the existing SuffixAnnotation with the new one containing the additions...
                radixTree.put(suffix, suffixAnnotation);
            }
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
    public boolean remove(CharSequence key) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Node getNode() {
        return radixTree.getNode();
    }
}
