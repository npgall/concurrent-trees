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
package com.googlecode.concurrenttrees.radixreversed;

import com.googlecode.concurrenttrees.common.CharSequences;
import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.common.LazyIterator;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;

import java.io.Serializable;
import java.util.Iterator;

/**
 * An implementation of {@link ReversedRadixTree} which supports lock-free concurrent reads, and allows items to be added
 * to and to be removed from the tree <i>atomically</i> by background thread(s), without blocking reads.
 * <p/>
 * This implementation is a lightweight wrapper around {@link ConcurrentRadixTree}, see that class for
 * implementation details.
 *
 * @author Niall Gallagher
 */
public class ConcurrentReversedRadixTree<O> implements ReversedRadixTree<O>, PrettyPrintable, Serializable {

    class ConcurrentReverseRadixTreeImpl<O> extends ConcurrentRadixTree<O> {

        public ConcurrentReverseRadixTreeImpl(NodeFactory nodeFactory) {
            super(nodeFactory);
        }

        // Override this hook method to reverse the order of keys stored in the tree and about to be returned to the
        // application, this undoes the reversing of keys when added to the tree in the first place...
        @Override
        protected CharSequence transformKeyForResult(CharSequence rawKey) {
            return CharSequences.reverse(rawKey);
        }
    }
    private final ConcurrentRadixTree<O> radixTree;

    /**
     * Creates a new {@link ConcurrentReversedRadixTree} which will use the given {@link NodeFactory} to create nodes.
     *
     * @param nodeFactory An object which creates {@link Node} objects on-demand, and which might return node
     * implementations optimized for storing the values supplied to it for the creation of each node
     */
    public ConcurrentReversedRadixTree(NodeFactory nodeFactory) {
        this.radixTree = new ConcurrentReverseRadixTreeImpl<O>(nodeFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public O getValueForExactKey(CharSequence key) {
        return radixTree.getValueForExactKey(CharSequences.reverse(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public O put(CharSequence key, O value) {
        return radixTree.put(CharSequences.reverse(key), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public O putIfAbsent(CharSequence key, O value) {
        return radixTree.putIfAbsent(CharSequences.reverse(key), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<CharSequence> getKeysEndingWith(CharSequence suffix) {
        return radixTree.getKeysStartingWith(CharSequences.reverse(suffix));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<O> getValuesForKeysEndingWith(CharSequence suffix) {
        return radixTree.getValuesForKeysStartingWith(CharSequences.reverse(suffix));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<KeyValuePair<O>> getKeyValuePairsForKeysEndingWith(CharSequence suffix) {
        return radixTree.getKeyValuePairsForKeysStartingWith(CharSequences.reverse(suffix));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(CharSequence key) {
        return radixTree.remove(CharSequences.reverse(key));
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
        return new LazyIterator<KeyValuePair<O>>() {

            final Iterator<KeyValuePair<O>> it = radixTree.iterator();

            @Override
            protected KeyValuePair<O> computeNext() {
                if (it.hasNext()) {
                    KeyValuePair<O> current = it.next();
                    return new ConcurrentRadixTree.KeyValuePairImpl<O>(
                            CharSequences.toString(CharSequences.reverse(current.getKey())),
                            current.getValue());
                } else {
                    return endOfData();
                }
            }
        };
    }

    @Override
    public Node getNode() {
        return radixTree.getNode();
    }
}
