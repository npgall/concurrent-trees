package com.googlecode.concurrenttrees.solver;

import com.googlecode.concurrenttrees.common.CharSequenceUtil;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Niall Gallagher
 */
public class LCSubstringSolver {

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

        // Override to make accessible to outer class...
        @Override
        protected void traverseDescendants(CharSequence startKey, Node startNode, NodeKeyPairHandler nodeHandler) {
            super.traverseDescendants(startKey, startNode, nodeHandler);
        }
    }

    private final ConcurrentSuffixTreeImpl<Set<String>> suffixTree;
    private final Set<String> originalDocuments;

    /**
     * Creates a new {@link LCSubstringSolver} which will use the given {@link NodeFactory} to create nodes.
     *
     * @param nodeFactory An object which creates {@link com.googlecode.concurrenttrees.radix.node.Node} objects
     * on-demand, and which might return node implementations optimized for storing the values supplied to it for
     * the creation of each node
     */
    public LCSubstringSolver(NodeFactory nodeFactory) {
        this.suffixTree = new ConcurrentSuffixTreeImpl<Set<String>>(nodeFactory);
        this.originalDocuments = createSetForOriginalKeys();
    }

    /**
     * Creates a new {@link LCSubstringSolver} which will use the given {@link NodeFactory} to create nodes.
     *
     * @param nodeFactory An object which creates {@link com.googlecode.concurrenttrees.radix.node.Node} objects
     * on-demand, and which might return node implementations optimized for storing the values supplied to it for the
     * creation of each node
     * @param restrictConcurrency If true, configures use of a {@link java.util.concurrent.locks.ReadWriteLock} allowing
     * concurrent reads, except when writes are being performed by other threads, in which case writes block all reads;
     * if false, configures lock-free reads; allows concurrent non-blocking reads, even if writes are being performed
     * by other threads
     */
    public LCSubstringSolver(NodeFactory nodeFactory, boolean restrictConcurrency) {
        this.suffixTree = new ConcurrentSuffixTreeImpl<Set<String>>(nodeFactory, restrictConcurrency);
        this.originalDocuments = createSetForOriginalKeys();
    }

    /**
     * Adds a {@link CharSequence} document to the suffix tree.
     *
     * @param document The {@link CharSequence} to add to the suffix tree
     * @return True if the document was added, false if it was not because it had been added previously
     */
    public boolean add(CharSequence document) {
        if (document == null) {
            throw new IllegalArgumentException("The document argument was null");
        }
        if (document.length() == 0) {
            throw new IllegalArgumentException("The document argument was zero-length");
        }
        suffixTree.acquireWriteLock();
        try {
            // We convert to string (for now) due to lack of equals() and hashCode() support in CharSequence...
            String documentString = CharSequenceUtil.toString(document);

            // Put/replace value in set before we add suffixes to the tree...
            boolean addedNew = originalDocuments.add(documentString);
            if (!addedNew) {
                // Key was not added as was already contained, no need to do anything, return false...
                return false;
            }
            // Kew was added to set, now add to tree...
            addSuffixesToRadixTree(documentString);
            return true;
        }
        finally {
            suffixTree.releaseWriteLock();
        }
    }

    void addSuffixesToRadixTree(String keyAsString) {
        Iterable<CharSequence> suffixes = CharSequenceUtil.generateSuffixes(keyAsString);
        for (CharSequence suffix : suffixes) {
            Set<String> originalKeyRefs = suffixTree.getValueForExactKey(suffix);
            if (originalKeyRefs == null) {
                originalKeyRefs = createSetForOriginalKeys();
                suffixTree.put(suffix, originalKeyRefs);
            }
            originalKeyRefs.add(keyAsString);
        }
    }

    public CharSequence getLongestCommonSubstring() {
        throw new UnsupportedOperationException("Not implemented");
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
}
