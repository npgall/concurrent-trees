package com.googlecode.concurrenttrees.common;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author npgall
 */
public class DepthTrackingCharSequenceTest {

    @Test
    public void testDepthTracking() {
        CharSequence delegate = "abcdef";
        DepthTrackingCharSequence depthTrackingCharSequence = new DepthTrackingCharSequence(delegate);

        assertEquals(-1, depthTrackingCharSequence.getSearchDepth());

        assertEquals('c', depthTrackingCharSequence.charAt(2));
        assertEquals(2, depthTrackingCharSequence.getSearchDepth());

        assertEquals('b', depthTrackingCharSequence.charAt(1));
        assertEquals(2, depthTrackingCharSequence.getSearchDepth()); // depth should not change

        assertEquals('d', depthTrackingCharSequence.charAt(3));
        assertEquals(3, depthTrackingCharSequence.getSearchDepth()); // depth should increase

        CharSequence subSequence = depthTrackingCharSequence.subSequence(4, 6);
        assertEquals(3, depthTrackingCharSequence.getSearchDepth()); // depth should not change
        assertEquals(2, subSequence.length());

        assertEquals('e', subSequence.charAt(0));
        assertEquals(4, depthTrackingCharSequence.getSearchDepth());  // depth should increase

        assertEquals('f', subSequence.charAt(1));
        assertEquals(5, depthTrackingCharSequence.getSearchDepth());  // depth should increase
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidCharAtIndex1() {
        CharSequence delegate = "abcdef";
        DepthTrackingCharSequence depthTrackingCharSequence = new DepthTrackingCharSequence(delegate);
        depthTrackingCharSequence.charAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidCharAtIndex2() {
        CharSequence delegate = "abcdef";
        DepthTrackingCharSequence depthTrackingCharSequence = new DepthTrackingCharSequence(delegate);
        depthTrackingCharSequence.charAt(6);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidSubsequenceStartIndex() {
        CharSequence delegate = "abcdef";
        DepthTrackingCharSequence depthTrackingCharSequence = new DepthTrackingCharSequence(delegate);
        depthTrackingCharSequence.subSequence(-1, depthTrackingCharSequence.length());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidSubsequenceEndIndex() {
        CharSequence delegate = "abcdef";
        DepthTrackingCharSequence depthTrackingCharSequence = new DepthTrackingCharSequence(delegate);
        depthTrackingCharSequence.subSequence(4, 7);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidSubsequenceEndIndexRepeated() {
        CharSequence delegate = "abcdef";
        DepthTrackingCharSequence depthTrackingCharSequence = new DepthTrackingCharSequence(delegate);
        CharSequence subSequence = depthTrackingCharSequence.subSequence(4, 6);
        subSequence.subSequence(0, 3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidSubsequenceNegative() {
        CharSequence delegate = "abcdef";
        DepthTrackingCharSequence depthTrackingCharSequence = new DepthTrackingCharSequence(delegate);
        depthTrackingCharSequence.subSequence(2, 1);
    }

    @Test
    public void testToString() {
        CharSequence delegate = "abcdef";
        DepthTrackingCharSequence depthTrackingCharSequence = new DepthTrackingCharSequence(delegate);
        assertEquals(delegate, depthTrackingCharSequence.toString());
        CharSequence subSequence = depthTrackingCharSequence.subSequence(1, 3);
        assertEquals("bc", subSequence.toString());
    }
}