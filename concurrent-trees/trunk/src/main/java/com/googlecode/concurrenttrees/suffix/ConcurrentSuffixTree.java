package com.googlecode.concurrenttrees.suffix;

import com.googlecode.concurrenttrees.common.CharSequenceUtil;
import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.suffix.metadata.SuffixMetadata;
import com.googlecode.concurrenttrees.suffix.metadata.SuffixMetadataFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Niall Gallagher
 */
public class ConcurrentSuffixTree<O> implements SuffixTree<O> {

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

    private final SuffixMetadataFactory<CharSequence> suffixMetadataFactory;
    // Associate the original document with
    private final ConcurrentSuffixTreeImpl<SuffixMetadata<CharSequence>> radixTree;
    private final ConcurrentMap<String, O> valueMap;

    /**
     * Creates a new {@link ConcurrentSuffixTree} which will use the given {@link NodeFactory} to create nodes.
     *
     * @param nodeFactory An object which creates {@link com.googlecode.concurrenttrees.radix.node.Node} objects
     * on-demand, and which might return node implementations optimized for storing the values supplied to it for
     * the creation of each node
     * @param suffixMetadataFactory An object which creates {@link SuffixMetadata} objects
     * on-demand, and which might return {@link SuffixMetadata} object implementations optimized for storing the values
     * supplied to it for the creation of each object
     */
    public ConcurrentSuffixTree(NodeFactory nodeFactory, SuffixMetadataFactory<CharSequence> suffixMetadataFactory) {
        this.suffixMetadataFactory = suffixMetadataFactory;
        this.radixTree = new ConcurrentSuffixTreeImpl<SuffixMetadata<CharSequence>>(nodeFactory);
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
     * @param suffixMetadataFactory An object which creates {@link SuffixMetadata} objects
     * on-demand, and which might return {@link SuffixMetadata} object implementations optimized for storing the values
     * supplied to it for the creation of each object
     * @param restrictConcurrency If true, configures use of a {@link java.util.concurrent.locks.ReadWriteLock} allowing
     * concurrent reads, except when writes are being performed by other threads, in which case writes block all reads;
     * if false, configures lock-free reads; allows concurrent non-blocking reads, even if writes are being performed
     * by other threads
     * @deprecated This method allowing concurrency to be restricted will be removed in future.
     */
    @Deprecated
    public ConcurrentSuffixTree(NodeFactory nodeFactory, SuffixMetadataFactory<CharSequence> suffixMetadataFactory, boolean restrictConcurrency) {
        this.suffixMetadataFactory = suffixMetadataFactory;
        //noinspection deprecation
        this.radixTree = new ConcurrentSuffixTreeImpl<SuffixMetadata<CharSequence>>(nodeFactory, restrictConcurrency);
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
            boolean isExactMatchForDocument = !iterator.hasNext();
            SuffixMetadata<CharSequence> suffixMetadata = radixTree.get(suffix);
            if (suffixMetadata == null) {
                // Create new metadata...
                List<CharSequence> documentsEndingWithSuffix = Arrays.asList(suffix);
                List<CharSequence> documentsExactlyMatchingSuffix = isExactMatchForDocument ? Arrays.asList(suffix) : Collections.<CharSequence>emptyList();
                suffixMetadata = suffixMetadataFactory.createSuffixMetadata(documentsEndingWithSuffix, documentsExactlyMatchingSuffix);
                radixTree.put(suffix, suffixMetadata);
            }
            else {
                // Update existing metadata...
                // TODO: optimize for specific implementation of DefaultSuffixMetadata - avoid recreating lists?
                List<CharSequence> existingDocumentsEndingWithSuffix = suffixMetadata.getDocumentsEndingWithSuffix();
                List<CharSequence> newDocumentsEndingWithSuffix = new ArrayList<CharSequence>(existingDocumentsEndingWithSuffix.size() + 1);
                newDocumentsEndingWithSuffix.addAll(existingDocumentsEndingWithSuffix);
                newDocumentsEndingWithSuffix.add(suffix);

                List<CharSequence> existingDocumentsExactlyMatchingSuffix = suffixMetadata.getDocumentsExactlyMatchingSuffix();
                List<CharSequence> newDocumentsExactlyMatchingSuffix;
                if (isExactMatchForDocument) {
                    newDocumentsExactlyMatchingSuffix = new ArrayList<CharSequence>(existingDocumentsExactlyMatchingSuffix.size() + 1);
                    newDocumentsExactlyMatchingSuffix.addAll(existingDocumentsExactlyMatchingSuffix);
                    newDocumentsExactlyMatchingSuffix.add(suffix);
                }
                else {
                    newDocumentsExactlyMatchingSuffix = existingDocumentsExactlyMatchingSuffix;
                }
                suffixMetadata = suffixMetadataFactory.createSuffixMetadata(newDocumentsEndingWithSuffix, newDocumentsExactlyMatchingSuffix);
                // Replace the existing SuffixMetadata with the new one containing the additions...
                radixTree.put(suffix, suffixMetadata);
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
}
