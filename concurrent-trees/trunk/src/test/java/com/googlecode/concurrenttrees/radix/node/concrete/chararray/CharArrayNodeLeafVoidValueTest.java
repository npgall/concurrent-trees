package com.googlecode.concurrenttrees.radix.node.concrete.chararray;

import com.googlecode.concurrenttrees.radix.node.Node;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * @author Niall Gallagher
 */
public class CharArrayNodeLeafVoidValueTest {

    @Test(expected = IllegalStateException.class)
    public void testUpdateOutgoingEdge() throws Exception {
        Node node = new CharArrayNodeLeafVoidValue("FOO");
        node.updateOutgoingEdge(new CharArrayNodeDefault("BAR", null, Collections.<Node>emptyList()));
    }

    @Test
    public void testToString() throws Exception {
        Node node = new CharArrayNodeLeafVoidValue("FOO");
        Assert.assertEquals("Node{edge=FOO, value=-, edges=[]}", node.toString());
    }
}
