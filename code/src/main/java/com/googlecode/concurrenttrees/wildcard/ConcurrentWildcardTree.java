package com.googlecode.concurrenttrees.wildcard;

import com.googlecode.concurrenttrees.common.CharSequences;
import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.common.LazyIterator;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radixinverted.ConcurrentInvertedRadixTree;
import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.googlecode.concurrenttrees.wildcard.node.DefaultWildcardNodeFactory;
import com.googlecode.concurrenttrees.wildcard.node.WildcardNode;
import com.googlecode.concurrenttrees.wildcard.node.WildcardNodeFactory;
import com.googlecode.concurrenttrees.wildcard.predicate.WildcardPredicate;

import java.util.Collections;
import java.util.Iterator;

/**
 * @author npgall
 */
public class ConcurrentWildcardTree<O> {

    final NodeFactory nodeFactory;
    final WildcardNodeFactory wildcardNodeFactory = new DefaultWildcardNodeFactory();
    final InvertedRadixTree<WildcardNode> rootTree;

    public ConcurrentWildcardTree(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
        this.rootTree = createSubtree();
    }

    InvertedRadixTree<WildcardNode> createSubtree() {
        return new ConcurrentInvertedRadixTree<WildcardNode>(nodeFactory);
    }

    public O put(WildcardPattern key, O value) {
        InvertedRadixTree<WildcardNode> currentTree = rootTree;
        O existingValue = null;
        for (Iterator<String> segmentsIterator = key.segments.iterator(); segmentsIterator.hasNext(); ) {
            String segment = segmentsIterator.next();
            boolean notLastSegment = !segmentsIterator.hasNext();
            WildcardNode wildcardNode = currentTree.getValueForExactKey(segment);
            if (notLastSegment) {
                if (wildcardNode == null) {
                    // Create a WildcardNode without a value, to point to the new subtree for the next segment...
                    // TODO: predicates..
                    wildcardNode = wildcardNodeFactory.createNode(Collections.<WildcardPredicate>emptyList(), createSubtree(), null);
                    currentTree.put(segment, wildcardNode);
                } else if (wildcardNode.getNextSubtree() == null) {
                    // A WildcardNode with a value already exists.
                    // Recreate it to retain the same value, but adding pointer to the new subtree for next segment...
                    wildcardNode = wildcardNodeFactory.createNode(
                            wildcardNode.getNextSubtreePredicates(), // TODO ..predicates?
                            createSubtree(),
                            wildcardNode.getValue()
                    );
                    currentTree.put(segment, wildcardNode);
                }
                // ..else no need to modify the existing WildcardNode.
            } else {
                if (wildcardNode == null) {
                    // Create a WildcardNode with the given a value, with no subtree...
                    wildcardNode = wildcardNodeFactory.createNode(null, null, value);
                } else {
                    // A WildcardNode with a subtree and possibly a value already exists.
                    // Remember the existing value so we can return it...
                    existingValue = (O) wildcardNode.getValue();
                    // Replace the value in this node, leaving the pointer to any existing subtree intact...
                    wildcardNode = wildcardNodeFactory.createNode(
                            wildcardNode.getNextSubtreePredicates(),
                            wildcardNode.getNextSubtree(),
                            value
                    );
                }
                currentTree.put(segment, wildcardNode);
            }
        }
        return existingValue;
    }


    Iterable<WildcardPattern> getKeysMatching(CharSequence document) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    Iterable<KeyValuePair<WildcardNode>> scanForKeyValuePairsMatching(final CharSequence document, final InvertedRadixTree<WildcardNode> currentTree) {
        return new Iterable<KeyValuePair<WildcardNode>>() {
            @Override
            public Iterator<KeyValuePair<WildcardNode>> iterator() {
                return new LazyIterator<KeyValuePair<WildcardNode>>() {
                    Iterator<CharSequence> documentSuffixes = CharSequences.generateSuffixes(document).iterator();
                    Iterator<KeyValuePair<WildcardNode>> matchesForCurrentSuffix = Collections.<KeyValuePair<WildcardNode>>emptyList().iterator();

                    @Override
                    protected KeyValuePair<WildcardNode> computeNext() {
                        while(!matchesForCurrentSuffix.hasNext()) {
                            if (documentSuffixes.hasNext()) {
                                CharSequence nextSuffix = documentSuffixes.next();
                                matchesForCurrentSuffix = currentTree.getKeyValuePairsForKeysPrefixing(nextSuffix).iterator();
                            }
                            else {
                                return endOfData();
                            }
                        }
                        // TODO: termination condition (calc last offset, or scan for delimiter?)
                        return matchesForCurrentSuffix.next();
                    }
                };
            }
        };

    }

}