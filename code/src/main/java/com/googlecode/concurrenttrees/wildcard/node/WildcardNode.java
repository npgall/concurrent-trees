package com.googlecode.concurrenttrees.wildcard.node;

import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.googlecode.concurrenttrees.wildcard.WildcardPattern;
import com.googlecode.concurrenttrees.wildcard.predicate.WildcardPredicate;

import java.util.Collection;
import java.util.Map;

/**
 * @author npgall
 */
public interface WildcardNode {

    Collection<WildcardPredicate> getNextSubtreePredicates();

    InvertedRadixTree<WildcardNode> getNextSubtree();
    
    Object getValue();

}
