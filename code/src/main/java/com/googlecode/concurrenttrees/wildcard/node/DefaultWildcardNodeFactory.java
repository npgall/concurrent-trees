package com.googlecode.concurrenttrees.wildcard.node;

import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.googlecode.concurrenttrees.wildcard.WildcardPattern;
import com.googlecode.concurrenttrees.wildcard.predicate.WildcardPredicate;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author npgall
 */
public class DefaultWildcardNodeFactory implements WildcardNodeFactory {

    @Override
    public WildcardNode createNode(Set<WildcardPredicate> wildcardPredicates, InvertedRadixTree<WildcardNode> subtree, ConcurrentMap<WildcardPattern, Object> wildcardPatternsMatched) {
        return new WildcardNodeDefault(wildcardPredicates, subtree, wildcardPatternsMatched);
    }

    @Override
    public WildcardNode createNode(InvertedRadixTree<WildcardNode> subtree) {
        return createNode(Collections.newSetFromMap(
                new ConcurrentHashMap<WildcardPredicate, Boolean>()),
                subtree,
                new ConcurrentHashMap<WildcardPattern, Object>()
        );
    }
}
