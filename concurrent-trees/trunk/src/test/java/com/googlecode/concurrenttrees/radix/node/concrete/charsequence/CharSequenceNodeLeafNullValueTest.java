package com.googlecode.concurrenttrees.radix.node.concrete.charsequence;

import com.googlecode.concurrenttrees.radix.node.Node;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * @author Niall Gallagher
 */
public class CharSequenceNodeLeafNullValueTest {

    @Test(expected = IllegalStateException.class)
    public void testUpdateOutgoingEdge() throws Exception {
        Node node = new CharSequenceNodeLeafNullValue("FOO");
        node.updateOutgoingEdge(new CharSequenceNodeDefault("BAR", null, Collections.<Node>emptyList()));
    }

    @Test
    public void testToString() throws Exception {
        Node node = new CharSequenceNodeLeafNullValue("FOO");
        Assert.assertEquals("Node{edge=FOO, value=null, edges=[]}", node.toString());
    }

    @Test
    public void testGetIncomingEdgeFirstCharacter() {
        Node node = new CharSequenceNodeLeafNullValue("FOO");
        Assert.assertEquals(Character.valueOf('F'), node.getIncomingEdgeFirstCharacter());
    }
}
