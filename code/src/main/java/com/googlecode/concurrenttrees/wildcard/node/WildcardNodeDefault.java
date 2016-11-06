package com.googlecode.concurrenttrees.wildcard.node;

import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.googlecode.concurrenttrees.wildcard.WildcardPattern;

/**
 * @author npgall
 */
public class WildcardNodeDefault implements WildcardNode {

    final WildcardPattern key;
    final Object value;
    final InvertedRadixTree<WildcardNode> subtree;

    public WildcardNodeDefault(WildcardPattern key, Object value, InvertedRadixTree<WildcardNode> subtree) {
        this.key = key;
        this.value = value;
        this.subtree = subtree;
    }

    @Override
    public WildcardPattern getKey() {
        return key;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public InvertedRadixTree<WildcardNode> getSubtree() {
        return subtree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WildcardNodeDefault)) return false;

        WildcardNodeDefault that = (WildcardNodeDefault) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return subtree != null ? subtree.equals(that.subtree) : that.subtree == null;

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (subtree != null ? subtree.hashCode() : 0);
        return result;
    }
}
