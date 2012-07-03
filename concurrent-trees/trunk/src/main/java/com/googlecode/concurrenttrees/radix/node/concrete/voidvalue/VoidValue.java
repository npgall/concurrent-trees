package com.googlecode.concurrenttrees.radix.node.concrete.voidvalue;

/**
 * A dummy object which if supplied as a value for an entry in a tree, will not actually be stored in the tree by
 * {@link com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory} or
 * {@link com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory} to save memory.
 *
 * @author Niall Gallagher
 */
public class VoidValue {

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VoidValue;
    }

    @Override
    public String toString() {
        return "-";
    }

    VoidValue() {
    }

    public static final VoidValue SINGLETON = new VoidValue();
}
