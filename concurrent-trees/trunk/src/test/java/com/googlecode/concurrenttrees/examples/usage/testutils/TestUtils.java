package com.googlecode.concurrenttrees.examples.usage.testutils;

import java.util.Iterator;

/**
 * @author Niall Gallagher
 */
public class TestUtils {

    public static String iterableToString(Iterable<CharSequence> charSequenceIterable) {
        StringBuilder sb = new StringBuilder("[");
        for (Iterator<CharSequence> iterator = charSequenceIterable.iterator(); iterator.hasNext(); ) {
            CharSequence charSequence = iterator.next();
            sb.append(charSequence);
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
