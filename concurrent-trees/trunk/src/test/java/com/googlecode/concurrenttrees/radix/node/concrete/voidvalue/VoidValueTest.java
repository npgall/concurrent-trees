package com.googlecode.concurrenttrees.radix.node.concrete.voidvalue;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Niall Gallagher
 */
public class VoidValueTest {

    @Test
    public void testHashCode() throws Exception {
        Assert.assertEquals(1, new VoidValue().hashCode());
    }

    @Test
    public void testEquals() throws Exception {
        Assert.assertTrue(new VoidValue().equals(new VoidValue()));
        Assert.assertFalse(new VoidValue().equals(new Object()));
        //noinspection NullableProblems,ObjectEqualsNull
        Assert.assertFalse(new VoidValue().equals(null));
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals("-", new VoidValue().toString());
    }
}
