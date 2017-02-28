package com.googlecode.concurrenttrees.wildcard;

import com.googlecode.concurrenttrees.common.CharSequences;
import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.common.LazyIterator;
import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;
import com.googlecode.concurrenttrees.radixinverted.ConcurrentInvertedRadixTree;
import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.googlecode.concurrenttrees.wildcard.node.WildcardNode;
import com.googlecode.concurrenttrees.wildcard.node.WildcardNodeFactory;

import java.util.*;

/**
 * @author npgall
 */
public class ConcurrentWildcardTree<O> implements PrettyPrintable {

    final NodeFactory radixNodeFactory;
    final WildcardNodeFactory wildcardNodeFactory;

    volatile WildcardNode rootNode;

    public ConcurrentWildcardTree(NodeFactory radixNodeFactory, WildcardNodeFactory wildcardNodeFactory) {
        this.radixNodeFactory = radixNodeFactory;
        this.wildcardNodeFactory = wildcardNodeFactory;
        this.rootNode = wildcardNodeFactory.createNode(createSubtree());
    }

    InvertedRadixTree<WildcardNode> createSubtree() {
        return new ConcurrentInvertedRadixTree<WildcardNode>(radixNodeFactory);
    }

    public O put(WildcardPattern wildcardPattern, O value) {
        WildcardNode currentNode = rootNode;
        Object existingValue = null;
        for (Iterator<WildcardComponent> iterator = wildcardPattern.wildcardComponents.iterator(); iterator.hasNext(); ) {
            WildcardComponent wildcardComponent = iterator.next();

            currentNode.getWildcardPredicates().add(wildcardComponent.wildcardPredicate);

            WildcardNode nextNode = currentNode.getSubtree().getValueForExactKey(wildcardComponent.characterSequence);
            if (nextNode == null) {
                nextNode = wildcardNodeFactory.createNode(createSubtree());
                currentNode.getSubtree().put(wildcardComponent.characterSequence, nextNode);
            }
            if (!iterator.hasNext()) {
                existingValue = nextNode.getWildcardPatternsMatched().put(wildcardPattern, value);
            }
            currentNode = nextNode;
        }
        @SuppressWarnings("unchecked")
        O existingValueTyped = (O) existingValue;
        return existingValueTyped;
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

    @Override
    public Node getNode() {
        return new WildcardNodeAdapter(this.rootNode);
    }

    static class WildcardNodeAdapter implements Node {

        static final NodeFactory radixNodeFactoryForWildcards = new DefaultCharSequenceNodeFactory();

        final WildcardNode wildcardNode;
        final Node subtreeRootNode;

        public WildcardNodeAdapter(WildcardNode wildcardNode) {
            this.wildcardNode = wildcardNode;
            this.subtreeRootNode = ((PrettyPrintable)wildcardNode.getSubtree()).getNode();
        }

        @Override
        public Character getIncomingEdgeFirstCharacter() {
            return getIncomingEdge().charAt(0);
        }

        @Override
        public CharSequence getIncomingEdge() {
            String str = wildcardNode.toString();
            return str.length() == 0 ? "_" : str;
        }

        @Override
        public Object getValue() {
            return wildcardNode.getWildcardPatternsMatched().isEmpty() ? null : wildcardNode.getWildcardPatternsMatched();
        }

        @Override
        public Node getOutgoingEdge(Character edgeFirstCharacter) {
            return subtreeRootNode.getOutgoingEdge(edgeFirstCharacter);
        }

        @Override
        public void updateOutgoingEdge(Node childNode) {
            subtreeRootNode.updateOutgoingEdge(childNode);
        }

        @Override
        public List<Node> getOutgoingEdges() {
            List<Node> outgoingEdges = new ArrayList<Node>();
            for (Node outgoingEdge : subtreeRootNode.getOutgoingEdges()) {
                if (outgoingEdge.getValue() instanceof WildcardNode) {
                    WildcardNode wildcardNode = (WildcardNode) outgoingEdge.getValue();

                    List<Node> nestedOutgoingEdges = new ArrayList<Node>();
                    nestedOutgoingEdges.addAll(outgoingEdge.getOutgoingEdges());
                    nestedOutgoingEdges.add(new WildcardNodeAdapter(wildcardNode));
                    outgoingEdge = radixNodeFactoryForWildcards.createNode(outgoingEdge.getIncomingEdge(), null, nestedOutgoingEdges, false);
                }
                outgoingEdges.add(outgoingEdge);

            }
            return outgoingEdges;
        }
    }

}