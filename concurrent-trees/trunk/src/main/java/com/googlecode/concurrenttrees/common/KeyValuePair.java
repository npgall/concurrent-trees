package com.googlecode.concurrenttrees.common;

/**
 * Encapsulates a key and a value.
 *
 * @param <O> The type of the value
 * @author Niall Gallagher
 */
public interface KeyValuePair<O> {

    /**
     * Returns the key with which the value is associated
     * @return The key with which the value is associated
     */
    CharSequence getKey();

    /**
     * Returns the value associated with the key
     * @return The value associated with the key
     */
    O getValue();

    /**
     * Compares this {@link KeyValuePair} object with another for equality.
     * <p/>
     * This is implemented based on equality of the keys.
     *
     * @param o The other object to compare
     * @return True if the other object is also a {@link KeyValuePair} and is equal to this one as specified above
     */
    @Override
    boolean equals(Object o);

    /**
     * Returns a hash code for this object.
     */
    @Override
    int hashCode();

    /**
     * Returns a string representation as {@code (key, value)}.
     * @return A string representation as {@code (key, value)}
     */
    @Override
    String toString();
}