package com.googlecode.concurrenttrees.wildcard.node;

import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.googlecode.concurrenttrees.wildcard.WildcardPattern;

/**
 * @author npgall
 */
public class DefaultWildcardNodeFactory implements WildcardNodeFactory {

    @Override
    public WildcardNode createNode(WildcardPattern key, Object value, InvertedRadixTree<WildcardNode> subtree) {
        if (key != null && value == null) {
            throw new IllegalStateException("Cannot create WildcardNode with a key but no value");
        }
        if (key == null && subtree == null) {
            throw new IllegalStateException("Cannot create WildcardNode with neither a key nor a subtree");
        }
        // At this point we have either a key+value, a subtree, or a key+value+subtree.
        return new WildcardNodeDefault(key, value, subtree);
    }
}
