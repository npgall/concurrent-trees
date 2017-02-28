package com.googlecode.concurrenttrees.wildcard.node;

import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.googlecode.concurrenttrees.wildcard.WildcardPattern;
import com.googlecode.concurrenttrees.wildcard.predicate.WildcardPredicate;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author npgall
 */
public interface WildcardNode {

    Set<WildcardPredicate> getWildcardPredicates();

    InvertedRadixTree<WildcardNode> getSubtree();
    
    ConcurrentMap<WildcardPattern, Object> getWildcardPatternsMatched();

}
