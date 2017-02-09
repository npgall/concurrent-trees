package com.googlecode.concurrenttrees.wildcard.node;

import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.googlecode.concurrenttrees.wildcard.WildcardPattern;
import com.googlecode.concurrenttrees.wildcard.predicate.WildcardPredicate;

import java.util.Collection;

/**
 * @author npgall
 */
public class WildcardNodeDefault implements WildcardNode {

    final Collection<WildcardPredicate> nextSubtreePredicates;

    final InvertedRadixTree<WildcardNode> nextSubtree;

    final Object value;

    public WildcardNodeDefault(Collection<WildcardPredicate> nextSubtreePredicates, InvertedRadixTree<WildcardNode> nextSubtree, Object value) {
        this.nextSubtreePredicates = nextSubtreePredicates;
        this.nextSubtree = nextSubtree;
        this.value = value;
    }

    @Override
    public Collection<WildcardPredicate> getNextSubtreePredicates() {
        return this.nextSubtreePredicates;
    }

    @Override
    public InvertedRadixTree<WildcardNode> getNextSubtree() {
        return this.nextSubtree;
    }

    @Override
    public Object getValue() {
        return this.value;
    }
}
