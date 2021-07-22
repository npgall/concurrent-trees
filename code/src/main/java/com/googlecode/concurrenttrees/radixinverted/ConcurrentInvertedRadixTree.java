/**
 * Copyright 2012-2013 Niall Gallagher
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
package com.googlecode.concurrenttrees.radixinverted;

import com.googlecode.concurrenttrees.common.CharSequences;
import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.common.LazyIterator;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;

/**
 * An implementation of {@link InvertedRadixTree} which supports lock-free concurrent reads, and allows items to be
 * added to and to be removed from the tree <i>atomically</i> by background thread(s), without blocking reads.
 * <p/>
 * This implementation is based on {@link ConcurrentRadixTree}.
 *
 * @author Niall Gallagher
 */
public class ConcurrentInvertedRadixTree<O> implements InvertedRadixTree<O>, PrettyPrintable, Serializable {

    static class ConcurrentInvertedRadixTreeImpl<O> extends ConcurrentRadixTree<O> {

        public ConcurrentInvertedRadixTreeImpl(NodeFactory nodeFactory) {
            super(nodeFactory);
        }

        /**
         * Lazily traverses the tree based on characters in the given input, and returns from the tree the next node
         * and its value where the key associated with the node matches the characters from the input. More than
         * one matching keyword can be found for the same input, if there are keys in the tree which are prefixes of
         * each other.
         * <p/>
         * Example:<br/>
         * Given two keywords in the tree: "Ford" and "Ford Focus"<br/>
         * Given a document: "I am shopping for a Ford Focus car"<br/>
         * Where the given input in this instance is the suffix of the document: "Ford Focus car"<br/>
         * ...then this method will return both "Ford" and "Ford Focus".<br/>
         * The caller can invoke this method repeatedly for each suffix of the document.<br/>
         *
         * @param input A sequence of characters which controls traversal of the tree
         * @return An iterable which will search for the next node in the tree matching the input
         */
        protected Iterable<KeyValuePair<O>> scanForKeysAtStartOfInput(final CharSequence input) {
            return new Iterable<KeyValuePair<O>>() {
                @Override
                public Iterator<KeyValuePair<O>> iterator() {
                    return new LazyIterator<KeyValuePair<O>>() {

                        Node currentNode = root;
                        int charsMatched = 0;

                        final int documentLength = input.length();

                        @Override
                        protected KeyValuePair<O> computeNext() {
                            while (charsMatched < documentLength) {
                                Node nextNode = currentNode.getOutgoingEdge(input.charAt(charsMatched));
                                if (nextNode == null) {
                                    // Next node is a dead end...
                                    return endOfData();
                                }

                                currentNode = nextNode;
                                CharSequence currentNodeEdgeCharacters = currentNode.getIncomingEdge();
                                final int numCharsInEdge = currentNodeEdgeCharacters.length();
                                if (numCharsInEdge + charsMatched > documentLength) {
                                    // This node can't be a match because it is too long...
                                    return endOfData();
                                }
                                for (int i = 0; i < numCharsInEdge; i++) {
                                    if (currentNodeEdgeCharacters.charAt(i) != input.charAt(charsMatched + i)) {
                                        // Found a difference between a character in the input
                                        // and a character in the edge represented by current node,
                                        // current node is a dead end...
                                        return endOfData();
                                    }
                                }
                                // All characters in the current edge matched, add this number to total chars matched...
                                charsMatched += numCharsInEdge;

                                if (currentNode.getValue() != null) {
                                    // This is an explicit node and all of its chars match input, return a match...
                                    return new KeyValuePairImpl<O>(CharSequences.toString(input.subSequence(0, charsMatched)), currentNode.getValue());
                                } // else the node matches, but is not an explicit node so we should continue scanning...
                            }
                            return endOfData();
                        }
                    };
                }
            };
        }

        /**
         * Traverses the tree based on characters in the given input, and returns the longest key in the tree
         * which is a prefix of the input, and its associated value.
         * <p/>
         * This uses a similar algorithm as {@link #scanForKeysAtStartOfInput(CharSequence)} except it returns
         * the last result that would be returned, however this algorithm locates the last node more efficiently
         * by creating garbage objects during traversal due to not having to return the intermediate results.
         *
         * @param input A sequence of characters which controls traversal of the tree
         * @return The longest key in the tree which is a prefix of the input, and its associated value;
         * or null if no such key is contained in the tree
         */
        protected KeyValuePair<O> scanForLongestKeyAtStartOfInput(final CharSequence input) {
            Node currentNode = root;
            int charsMatched = 0;

            final int documentLength = input.length();

            Node candidateNode = null;
            int candidateCharsMatched = 0;

            outer_loop: while (charsMatched < documentLength) {
                Node nextNode = currentNode.getOutgoingEdge(input.charAt(charsMatched));
                if (nextNode == null) {
                    // Next node is a dead end...
                    break;
                }

                currentNode = nextNode;
                CharSequence currentNodeEdgeCharacters = currentNode.getIncomingEdge();
                final int numCharsInEdge = currentNodeEdgeCharacters.length();
                if (numCharsInEdge + charsMatched > documentLength) {
                    // This node can't be a match because it is too long...
                    break;
                }
                for (int i = 0; i < numCharsInEdge; i++) {
                    if (currentNodeEdgeCharacters.charAt(i) != input.charAt(charsMatched + i)) {
                        // Found a difference between a character in the input
                        // and a character in the edge represented by current node,
                        // current node is a dead end...
                        break outer_loop;
                    }
                }
                // All characters in the current edge matched, add this number to total chars matched...
                charsMatched += numCharsInEdge;

                if (currentNode.getValue() != null) {
                    // This is an explicit node and all of its chars match input, return a match...
                    candidateNode = currentNode;
                    candidateCharsMatched = charsMatched;
                } // else the node matches, but is not an explicit node so we should continue scanning...
            }
            return candidateNode == null ? null : new KeyValuePairImpl<O>(CharSequences.toString(input.subSequence(0, candidateCharsMatched)), candidateNode.getValue());
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
    public Iterable<CharSequence> getKeysStartingWith(CharSequence prefix) {
        return radixTree.getKeysStartingWith(prefix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<O> getValuesForKeysStartingWith(CharSequence prefix) {
        return radixTree.getValuesForKeysStartingWith(prefix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<KeyValuePair<O>> getKeyValuePairsForKeysStartingWith(CharSequence prefix) {
        return radixTree.getKeyValuePairsForKeysStartingWith(prefix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<CharSequence> getClosestKeys(CharSequence candidate) {
        return radixTree.getClosestKeys(candidate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<O> getValuesForClosestKeys(CharSequence candidate) {
        return radixTree.getValuesForClosestKeys(candidate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<KeyValuePair<O>> getKeyValuePairsForClosestKeys(CharSequence candidate) {
        return radixTree.getKeyValuePairsForClosestKeys(candidate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<CharSequence> getKeysPrefixing(final CharSequence document) {
        return new Iterable<CharSequence>() {
            @Override
            public Iterator<CharSequence> iterator() {
                return new LazyIterator<CharSequence>() {
                    Iterator<KeyValuePair<O>> matchesForCurrentSuffix = radixTree.scanForKeysAtStartOfInput(document).iterator();

                    @Override
                    protected CharSequence computeNext() {
                        if (matchesForCurrentSuffix.hasNext()) {
                            return matchesForCurrentSuffix.next().getKey();
                        }
                        else {
                            return endOfData();
                        }
                    }
                };
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<O> getValuesForKeysPrefixing(final CharSequence document) {
        return new Iterable<O>() {
            @Override
            public Iterator<O> iterator() {
                return new LazyIterator<O>() {
                    Iterator<KeyValuePair<O>> matchesForCurrentSuffix = radixTree.scanForKeysAtStartOfInput(document).iterator();

                    @Override
                    protected O computeNext() {
                        if (matchesForCurrentSuffix.hasNext()) {
                            return matchesForCurrentSuffix.next().getValue();
                        }
                        else {
                            return endOfData();
                        }
                    }
                };
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<KeyValuePair<O>> getKeyValuePairsForKeysPrefixing(final CharSequence document) {
        return new Iterable<KeyValuePair<O>>() {
            @Override
            public Iterator<KeyValuePair<O>> iterator() {
                return new LazyIterator<KeyValuePair<O>>() {
                    Iterator<KeyValuePair<O>> matchesForCurrentSuffix = radixTree.scanForKeysAtStartOfInput(document).iterator();

                    @Override
                    protected KeyValuePair<O> computeNext() {
                        if (matchesForCurrentSuffix.hasNext()) {
                            return matchesForCurrentSuffix.next();
                        }
                        else {
                            return endOfData();
                        }
                    }
                };
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence getLongestKeyPrefixing(CharSequence document) {
        KeyValuePair<O> match = radixTree.scanForLongestKeyAtStartOfInput(document);
        return match == null ? null : match.getKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public O getValueForLongestKeyPrefixing(CharSequence document) {
        KeyValuePair<O> match = radixTree.scanForLongestKeyAtStartOfInput(document);
        return match == null ? null : match.getValue();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public KeyValuePair<O> getKeyValuePairForLongestKeyPrefixing(CharSequence document) {
        return radixTree.scanForLongestKeyAtStartOfInput(document);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<CharSequence> getKeysContainedIn(final CharSequence document) {
        return new Iterable<CharSequence>() {
            @Override
            public Iterator<CharSequence> iterator() {
                return new LazyIterator<CharSequence>() {
                    Iterator<CharSequence> documentSuffixes = CharSequences.generateSuffixes(document).iterator();
                    Iterator<KeyValuePair<O>> matchesForCurrentSuffix = Collections.<KeyValuePair<O>>emptyList().iterator();

                    @Override
                    protected CharSequence computeNext() {
                        while(!matchesForCurrentSuffix.hasNext()) {
                            if (documentSuffixes.hasNext()) {
                                CharSequence nextSuffix = documentSuffixes.next();
                                matchesForCurrentSuffix = radixTree.scanForKeysAtStartOfInput(nextSuffix).iterator();
                            }
                            else {
                                return endOfData();
                            }
                        }
                        return matchesForCurrentSuffix.next().getKey();
                    }
                };
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<O> getValuesForKeysContainedIn(final CharSequence document) {
        return new Iterable<O>() {
            @Override
            public Iterator<O> iterator() {
                return new LazyIterator<O>() {
                    Iterator<CharSequence> documentSuffixes = CharSequences.generateSuffixes(document).iterator();
                    Iterator<KeyValuePair<O>> matchesForCurrentSuffix = Collections.<KeyValuePair<O>>emptyList().iterator();

                    @Override
                    protected O computeNext() {
                        while(!matchesForCurrentSuffix.hasNext()) {
                            if (documentSuffixes.hasNext()) {
                                CharSequence nextSuffix = documentSuffixes.next();
                                matchesForCurrentSuffix = radixTree.scanForKeysAtStartOfInput(nextSuffix).iterator();
                            }
                            else {
                                return endOfData();
                            }
                        }
                        return matchesForCurrentSuffix.next().getValue();
                    }
                };
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<KeyValuePair<O>> getKeyValuePairsForKeysContainedIn(final CharSequence document) {
        return new Iterable<KeyValuePair<O>>() {
            @Override
            public Iterator<KeyValuePair<O>> iterator() {
                return new LazyIterator<KeyValuePair<O>>() {
                    Iterator<CharSequence> documentSuffixes = CharSequences.generateSuffixes(document).iterator();
                    Iterator<KeyValuePair<O>> matchesForCurrentSuffix = Collections.<KeyValuePair<O>>emptyList().iterator();

                    @Override
                    protected KeyValuePair<O> computeNext() {
                        while(!matchesForCurrentSuffix.hasNext()) {
                            if (documentSuffixes.hasNext()) {
                                CharSequence nextSuffix = documentSuffixes.next();
                                matchesForCurrentSuffix = radixTree.scanForKeysAtStartOfInput(nextSuffix).iterator();
                            }
                            else {
                                return endOfData();
                            }
                        }
                        return matchesForCurrentSuffix.next();
                    }
                };
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return radixTree.size();
    }

    @Override
    public Iterator<KeyValuePair<O>> iterator() {
        return radixTree.iterator();
    }

    @Override
    public Node getNode() {
        return radixTree.getNode();
    }
}
