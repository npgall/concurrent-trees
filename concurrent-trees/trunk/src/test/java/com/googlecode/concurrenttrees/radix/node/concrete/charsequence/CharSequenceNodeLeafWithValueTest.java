package com.googlecode.concurrenttrees.radix.node.concrete.charsequence;

import com.googlecode.concurrenttrees.radix.node.Node;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * @author Niall Gallagher
 */
public class CharSequenceNodeLeafWithValueTest {

    @Test(expected = IllegalStateException.class)
    public void testUpdateOutgoingEdge() throws Exception {
        Node node = new CharSequenceNodeLeafWithValue("FOO", 1);
        node.updateOutgoingEdge(new CharSequenceNodeDefault("BAR", null, Collections.<Node>emptyList()));
    }

    @Test
    public void testToString() throws Exception {
        Node node = new CharSequenceNodeLeafWithValue("FOO", 1);
        Assert.assertEquals("Node{edge=FOO, value=1, edges=[]}", node.toString());
    }

    @Test
    public void testGetOutgoingEdge() {
        Node node = new CharSequenceNodeLeafWithValue("FOO", 1);
        Assert.assertNull(node.getOutgoingEdge('A'));
    }
}
