package com.googlecode.concurrenttrees.radix.node.concrete;

import com.googlecode.concurrenttrees.radix.node.Node;
import org.junit.Test;

import java.util.Collections;

/**
 * @author Niall Gallagher
 */
public class NaiveCharSequenceNodeFactoryTest {

    @Test(expected = IllegalStateException.class)
    public void testCreateNode_NullEdge() throws Exception {
        //noinspection NullableProblems
        new NaiveCharSequenceNodeFactory().createNode(null, 1, Collections.<Node>emptyList(), false);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateNode_EmptyEdgeNonRoot() throws Exception {
        //noinspection NullableProblems
        new NaiveCharSequenceNodeFactory().createNode("", 1, Collections.<Node>emptyList(), false);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateNode_NullEdges() throws Exception {
        //noinspection NullableProblems
        new NaiveCharSequenceNodeFactory().createNode("FOO", 1, null, false);
    }
}
