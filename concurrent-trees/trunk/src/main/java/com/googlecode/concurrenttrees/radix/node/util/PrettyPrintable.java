package com.googlecode.concurrenttrees.radix.node.util;

import com.googlecode.concurrenttrees.radix.node.Node;

/**
 * An internal interface implemented by trees, which allows internal details of trees to be accessed by
 * {@link com.googlecode.concurrenttrees.common.PrettyPrintUtil}.
 *
 * @author Niall Gallagher
 */
public interface PrettyPrintable {
    public Node getNode();
}
