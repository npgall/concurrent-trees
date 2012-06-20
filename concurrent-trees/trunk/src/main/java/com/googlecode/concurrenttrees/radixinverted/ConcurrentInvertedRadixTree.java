package com.googlecode.concurrenttrees.radixinverted;

import com.googlecode.concurrenttrees.common.CharSequenceUtil;
import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * An implementation of {@link InvertedRadixTree} which supports lock-free concurrent reads, and allows items to be
 * added to and to be removed from the tree <i>atomically</i> by background thread(s), without blocking reads.
 * <p/>
 * This implementation is based on {@link ConcurrentRadixTree}.
 *
 * @author Niall Gallagher
 */
public class ConcurrentInvertedRadixTree<O> implements InvertedRadixTree<O>, PrettyPrintable {

    static class ConcurrentInvertedRadixTreeImpl<O> extends ConcurrentRadixTree<O> {

        public ConcurrentInvertedRadixTreeImpl(NodeFactory nodeFactory) {
            super(nodeFactory);
        }

        public ConcurrentInvertedRadixTreeImpl(NodeFactory nodeFactory, boolean restrictConcurrency) {
            super(nodeFactory, restrictConcurrency);
        }

        /**
         * Traverses the tree based on characters in the given input, and for each node traversed which encodes a key
         * in the tree, invokes the given {@link KeyValueHandler} supplying it the key which matched that node and
         * the value from the node.
         *
         * @param input A sequence of characters which controls traversal of the tree
         * @param keyValueHandler An object which will be notified of every key and value encountered in the input
         */
        protected void scanForKeysAtStartOfInput(CharSequence input, KeyValueHandler keyValueHandler) {
            Node currentNode = super.root;
            int charsMatched = 0;

            final int documentLength = input.length();
            outer_loop: while (charsMatched < documentLength) {
                Node nextNode = currentNode.getOutgoingEdge(input.charAt(charsMatched));
                if (nextNode == null) {
                    // Next node is a dead end...
                    //noinspection UnnecessaryLabelOnBreakStatement
                    break outer_loop;
                }

                currentNode = nextNode;
                CharSequence currentNodeEdgeCharacters = currentNode.getIncomingEdge();
                int charsMatchedThisEdge = 0;
                for (int i = 0, j = Math.min(currentNodeEdgeCharacters.length(), documentLength - charsMatched); i < j; i++) {
                    if (currentNodeEdgeCharacters.charAt(i) != input.charAt(charsMatched + i)) {
                        // Found a difference in chars between character in key and a character in current node.
                        // Current node is the deepest match (inexact match)....
                        break outer_loop;
                    }
                    charsMatchedThisEdge++;
                }
                if (charsMatchedThisEdge == currentNodeEdgeCharacters.length()) {
                    // All characters in the current edge matched, add this number to total chars matched...
                    charsMatched += charsMatchedThisEdge;
                }
                if (currentNode.getValue() != null) {
                    keyValueHandler.handle(input.subSequence(0, charsMatched), currentNode.getValue());
                }
            }
        }
        interface KeyValueHandler {
            void handle(CharSequence key, Object value);
        }
    }
    private final ConcurrentInvertedRadixTreeImpl<O> radixTree;

    /**
     * Creates a new {@link ConcurrentInvertedRadixTree} which will use the given {@link NodeFactory} to create nodes.
     *
     * @param nodeFactory An object which creates {@link Node} objects on-demand, and which might return node
     * implementations optimized for storing the values supplied to it for the creation of each node
     */
    public ConcurrentInvertedRadixTree(NodeFactory nodeFactory) {
        this.radixTree = new ConcurrentInvertedRadixTreeImpl<O>(nodeFactory);
    }

    /**
     * Creates a new {@link ConcurrentInvertedRadixTree} which will use the given {@link NodeFactory} to create nodes.
     *
     * @param nodeFactory An object which creates {@link Node} objects on-demand, and which might return node
     * implementations optimized for storing the values supplied to it for the creation of each node
     * @param restrictConcurrency If true, configures use of a {@link java.util.concurrent.locks.ReadWriteLock} allowing
     * concurrent reads, except when writes are being performed by other threads, in which case writes block all reads;
     * if false, configures lock-free reads; allows concurrent non-blocking reads, even if writes are being performed
     * by other threads
     */
    public ConcurrentInvertedRadixTree(NodeFactory nodeFactory, boolean restrictConcurrency) {
        this.radixTree = new ConcurrentInvertedRadixTreeImpl<O>(nodeFactory, restrictConcurrency);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public O put(CharSequence key, O value) {
        return radixTree.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public O putIfAbsent(CharSequence key, O value) {
        return radixTree.putIfAbsent(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(CharSequence key) {
        return radixTree.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public O getValueForExactKey(CharSequence key) {
        return radixTree.getValueForExactKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<CharSequence> getKeysContainedIn(CharSequence document) {
        Iterable<CharSequence> documentSuffixes = CharSequenceUtil.generateSuffixes(document);
        final Set<CharSequence> results = new LinkedHashSet<CharSequence>();
        for (CharSequence documentSuffix : documentSuffixes) {
            radixTree.scanForKeysAtStartOfInput(documentSuffix, new ConcurrentInvertedRadixTreeImpl.KeyValueHandler() {
                @Override
                public void handle(CharSequence key, Object value) {
                    String keyString = CharSequenceUtil.toString(key);
                    results.add(keyString);
                }
            });
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<O> getValuesForKeysContainedIn(CharSequence document) {
        Iterable<CharSequence> documentSuffixes = CharSequenceUtil.generateSuffixes(document);
        final Set<O> results = new LinkedHashSet<O>();
        for (CharSequence documentSuffix : documentSuffixes) {
            radixTree.scanForKeysAtStartOfInput(documentSuffix, new ConcurrentInvertedRadixTreeImpl.KeyValueHandler() {
                @Override
                public void handle(CharSequence key, Object value) {
                    @SuppressWarnings({"unchecked"})
                    O valueTyped = (O)value;
                    results.add(valueTyped);
                }
            });
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<KeyValuePair<O>> getKeyValuePairsForKeysContainedIn(CharSequence document) {
        Iterable<CharSequence> documentSuffixes = CharSequenceUtil.generateSuffixes(document);
        final Set<KeyValuePair<O>> results = new LinkedHashSet<KeyValuePair<O>>();
        for (CharSequence documentSuffix : documentSuffixes) {
            radixTree.scanForKeysAtStartOfInput(documentSuffix, new ConcurrentInvertedRadixTreeImpl.KeyValueHandler() {
                @Override
                public void handle(CharSequence key, Object value) {
                    @SuppressWarnings({"unchecked"})
                    O valueTyped = (O)value;
                    String keyString = CharSequenceUtil.toString(key);
                    results.add(new ConcurrentRadixTree.KeyValuePairImpl<O>(keyString, valueTyped));
                }
            });
        }
        return results;
    }

    @Override
    public Node getNode() {
        return radixTree.getNode();
    }
}
