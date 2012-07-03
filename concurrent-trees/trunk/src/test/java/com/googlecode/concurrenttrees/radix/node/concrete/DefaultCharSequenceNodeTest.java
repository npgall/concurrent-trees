package com.googlecode.concurrenttrees.radix.node.concrete;

import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.concrete.charsequence.CharSequenceNodeDefault;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * @author Niall Gallagher
 */
public class DefaultCharSequenceNodeTest {

    @Test
    public void testToString() throws Exception {
        Node node = new CharSequenceNodeDefault("FOO", null, Collections.<Node>emptyList());
        Assert.assertEquals("Node{edge=FOO, value=null, edges=[]}", node.toString());

    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateOutgoingEdge_NonExistentEdge() throws Exception {
        Node node = new CharSequenceNodeDefault("FOO", null, Collections.<Node>emptyList());
        node.updateOutgoingEdge(new CharSequenceNodeDefault("BAR", null, Collections.<Node>emptyList()));
    }
}
