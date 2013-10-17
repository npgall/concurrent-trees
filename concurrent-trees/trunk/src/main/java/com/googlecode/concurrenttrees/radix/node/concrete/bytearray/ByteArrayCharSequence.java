package com.googlecode.concurrenttrees.radix.node.concrete.bytearray;

/**
 * A {@link CharSequence} <i>view</i> onto a byte array of UTF-8-encoded characters, with the proviso that all of the
 * characters were encoded as a single byte in UTF-8. This uses Java's built-in casting from UTF-8 to char primitives.
 *
 * @author Niall Gallagher
 */
public class ByteArrayCharSequence implements CharSequence {

    final byte[] bytes;
    final int start;
    final int end;

    public ByteArrayCharSequence(byte[] bytes, int start, int end) {
        if (start < 0) throw new IllegalArgumentException("start " + start + " < 0");
        if (end > bytes.length) throw new IllegalArgumentException("end " + end + " > length " + bytes.length);
        if (end < start) throw new IllegalArgumentException("end " + end + " < start " + start);
        this.bytes = bytes;
        this.start = start;
        this.end = end;
    }

    @Override
    public int length() {
        return end - start;
    }

    @Override
    public char charAt(int index) {
        return (char) (bytes[index + start] & 0xFF);
    }

    @Override
    public ByteArrayCharSequence subSequence(int start, int end) {
        if (start < 0) throw new IllegalArgumentException("start " + start + " < 0");
        if (end > length()) throw new IllegalArgumentException("end " + end + " > length " + length());
        if (end < start) throw new IllegalArgumentException("end " + end + " < start " + start);
        return new ByteArrayCharSequence(bytes, this.start + start, this.start + end);
    }
}
