package com.googlecode.concurrenttrees.wildcard.node;

import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.googlecode.concurrenttrees.wildcard.WildcardPattern;

/**
 * @author npgall
 */
public interface WildcardNode {

    WildcardPattern getKey();

    Object getValue();

    InvertedRadixTree<WildcardNode> getSubtree();
}
