/**
 * Copyright 2012-2013 Niall Gallagher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.concurrenttrees.radix.node.concrete.bytearray;

import com.googlecode.concurrenttrees.common.CharSequences;

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

    @Override
    public String toString() {
        return CharSequences.toString(this);
    }

    /**
     * Encodes a given {@link CharSequence} into a {@code byte[]} in UTF-8 encoding, with the requirement that
     * it must be possible to represent all characters as a single byte in UTF-8. Otherwise an exception will be
     * thrown.
     *
     * @param charSequence The {@link CharSequence} to encode
     * @return A new {@code byte[]} encoding characters from the given {@link CharSequence} in UTF-8
     * @throws IllegalStateException If the characters cannot be encoded as described
     */
    public static byte[] toSingleByteUtf8Encoding(CharSequence charSequence) {
        final int length = charSequence.length();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            char inputChar = charSequence.charAt(i);
            if (inputChar > 255) {
                throw new IncompatibleCharacterException("Input contains a character which cannot be represented as a single byte in UTF-8: " + inputChar);
            }
            bytes[i] = (byte)inputChar;
        }
        return bytes;
    }

    public static class IncompatibleCharacterException extends IllegalStateException {
        public IncompatibleCharacterException(String s) {
            super(s);
        }
    }
}
