package com.googlecode.concurrenttrees.radix.node.concrete.charsequence;

import com.googlecode.concurrenttrees.radix.node.Node;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Niall Gallagher
 */
public class CharSequenceNodeNonLeafNullValueTest {

    @Test
    public void testUpdateOutgoingEdge() throws Exception {
        Node node = new CharSequenceNodeNonLeafNullValue("FOO", Arrays.asList((Node)new CharSequenceNodeDefault("BAR1", 1, Collections.<Node>emptyList())));
        node.updateOutgoingEdge(new CharSequenceNodeDefault("BAR2", null, Collections.<Node>emptyList()));
    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateOutgoingEdge_NonExistentEdge() throws Exception {
        Node node = new CharSequenceNodeNonLeafNullValue("FOO", Arrays.asList((Node)new CharSequenceNodeDefault("BAR", 1, Collections.<Node>emptyList())));
        node.updateOutgoingEdge(new CharSequenceNodeDefault("CAR", null, Collections.<Node>emptyList()));
    }

    @Test
    public void testToString() throws Exception {
        Node node = new CharSequenceNodeNonLeafNullValue("FOO", Collections.<Node>emptyList());
        Assert.assertEquals("Node{edge=FOO, value=null, edges=[]}", node.toString());
    }
}
