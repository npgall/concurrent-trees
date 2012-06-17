package com.googlecode.concurrenttrees.reverseradix;

import com.googlecode.concurrenttrees.common.CharSequenceUtil;
import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;

import java.util.*;

/**
 * @author Niall Gallagher
 */
public class ConcurrentReverseRadixTree<O> implements ReverseRadixTree<O>, PrettyPrintable {

    class ConcurrentReverseRadixTreeImpl<O> extends ConcurrentRadixTree<O> {

        public ConcurrentReverseRadixTreeImpl(NodeFactory nodeFactory) {
            super(nodeFactory);
        }

        public ConcurrentReverseRadixTreeImpl(NodeFactory nodeFactory, boolean restrictConcurrency) {
            super(nodeFactory, restrictConcurrency);
        }

        // Override this hook method to reverse the order of keys about to be returned to the application,
        // because ReverseRadixTree will store trees in reverse order...
        @Override
        protected CharSequence transformKeyForResult(CharSequence rawKey) {
            return CharSequenceUtil.reverse(rawKey);
        }

    }
    private final ConcurrentRadixTree<O> radixTree;

    /**
     * Creates a new {@link ConcurrentReverseRadixTree} which will use the given {@link NodeFactory} to create nodes.
     *
     * @param nodeFactory An object which creates {@link Node} objects on-demand, and which might return node
     * implementations optimized for storing the values supplied to it for the creation of each node
     */
    public ConcurrentReverseRadixTree(NodeFactory nodeFactory) {
        this.radixTree = new ConcurrentReverseRadixTreeImpl<O>(nodeFactory);
    }

    /**
     * Creates a new {@link ConcurrentReverseRadixTree} which will use the given {@link NodeFactory} to create nodes.
     * <p/>
     * As a temporary measure, allows the concurrency of {@link ConcurrentReverseRadixTree} to be restricted in the face of
     * writes, for safety until the algorithms in {@link ConcurrentReverseRadixTree} which support multi-threaded
     * reads while writes are ongoing can be more thoroughly tested.
     *
     * @param nodeFactory An object which creates {@link Node} objects on-demand, and which might return node
     * implementations optimized for storing the values supplied to it for the creation of each node
     * @param restrictConcurrency If true, configures use of a {@link java.util.concurrent.locks.ReadWriteLock} allowing
     * concurrent reads, except when writes are being performed by other threads, in which case writes block all reads;
     * if false, configures lock-free reads; allows concurrent non-blocking reads, even if writes are being performed
     * by other threads
     * @deprecated This method allowing concurrency to be restricted will be removed in future.
     */
    @Deprecated
    public ConcurrentReverseRadixTree(NodeFactory nodeFactory, boolean restrictConcurrency) {
        this.radixTree = new ConcurrentReverseRadixTreeImpl<O>(nodeFactory, restrictConcurrency);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public O get(CharSequence key) {
        return radixTree.get(CharSequenceUtil.reverse(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public O put(CharSequence key, O value) {
        return radixTree.put(CharSequenceUtil.reverse(key), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public O putIfAbsent(CharSequence key, O value) {
        return radixTree.putIfAbsent(CharSequenceUtil.reverse(key), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<CharSequence> getKeysForPostfix(CharSequence key) {
        return radixTree.getKeysForPrefix(CharSequenceUtil.reverse(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<O> getValuesForPostfix(CharSequence key) {
        return radixTree.getValuesForPrefix(CharSequenceUtil.reverse(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<KeyValuePair<O>> getKeyValuePairsForPostfix(CharSequence key) {
        return radixTree.getKeyValuePairsForPrefix(CharSequenceUtil.reverse(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(CharSequence key) {
        return radixTree.remove(CharSequenceUtil.reverse(key));
    }

    @Override
    public Node getNode() {
        return radixTree.getNode();
    }
}
