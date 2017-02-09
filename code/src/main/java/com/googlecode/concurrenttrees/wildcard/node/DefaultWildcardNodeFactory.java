package com.googlecode.concurrenttrees.wildcard.node;

import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.googlecode.concurrenttrees.wildcard.WildcardPattern;
import com.googlecode.concurrenttrees.wildcard.predicate.WildcardPredicate;

import java.util.Collection;

/**
 * @author npgall
 */
public class DefaultWildcardNodeFactory implements WildcardNodeFactory {

    @Override
    public WildcardNode createNode(Collection<WildcardPredicate> nextSubtreePredicates, InvertedRadixTree<WildcardNode> nextSubtree, Object value) {
        return new WildcardNodeDefault(nextSubtreePredicates, nextSubtree, value);
    }
}
