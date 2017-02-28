package com.googlecode.concurrenttrees.wildcard.node;

import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.googlecode.concurrenttrees.wildcard.WildcardPattern;
import com.googlecode.concurrenttrees.wildcard.predicate.WildcardPredicate;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author npgall
 */
public class WildcardNodeDefault implements WildcardNode {

    final Set<WildcardPredicate> wildcardPredicates;

    final InvertedRadixTree<WildcardNode> subtree;

    final ConcurrentMap<WildcardPattern, Object> wildcardPatternsMatched;

    public WildcardNodeDefault(Set<WildcardPredicate> wildcardPredicates, InvertedRadixTree<WildcardNode> subtree, ConcurrentMap<WildcardPattern, Object> wildcardPatternsMatched) {
        this.wildcardPredicates = wildcardPredicates;
        this.subtree = subtree;
        this.wildcardPatternsMatched = wildcardPatternsMatched;
    }

    @Override
    public Set<WildcardPredicate> getWildcardPredicates() {
        return wildcardPredicates;
    }

    @Override
    public InvertedRadixTree<WildcardNode> getSubtree() {
        return subtree;
    }

    @Override
    public ConcurrentMap<WildcardPattern, Object> getWildcardPatternsMatched() {
        return wildcardPatternsMatched;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Iterator<WildcardPredicate> iterator = wildcardPredicates.iterator(); iterator.hasNext(); ) {
            WildcardPredicate wildcardPredicate = iterator.next();
            sb.append(wildcardPredicate);
            if (iterator.hasNext()) {
                sb.append("|");
            }
        }
        return sb.toString();
    }
}
