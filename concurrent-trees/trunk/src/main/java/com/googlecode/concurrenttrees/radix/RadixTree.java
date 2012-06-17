/**
 * Copyright 2012 Niall Gallagher
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
package com.googlecode.concurrenttrees.radix;

import java.util.Collection;
import java.util.Set;

/**
 * API of a concurrent radix tree.
 * <p/>
 * See documentation on each method for details.
 *
 * @param <O> The type of the values associated with keys in the index
 *
 * @author Niall Gallagher
 */
public interface RadixTree<O> {

    /**
     * Returns the value associated with the given key (exact match), or returns <code>null</code> if no such value
     * is associated with the key.
     *
     * @param key The key with which the specified object might be associated
     * @return The value associated with the given key (exact match), or <code>null</code> if no value was associated
     * with the key
     */
    O get(CharSequence key);

    /**
     * Associates the given object with the key; replacing any existing object associated with the key.
     * Returns the previous object associated with the key, if any.
     * <p/>
     * This operation is performed atomically.
     *
     * @param key The key with which the specified object should be associated
     * @param value The object to associate with the key, which cannot be null
     * @return The existing value for this key, if there was one, otherwise null
     */
    O put(CharSequence key, O value);

    /**
     * If an object is not already associated with the specified key in the tree, associates the given object with the
     * key; otherwise if an existing object is already associated, returns the existing object.
     * <p/>
     * This operation is performed atomically.
     *
     * @param key The key with which the specified object should be associated
     * @param value The object to associate with the key, which cannot be null
     * @return The existing value for this key, if there was one, otherwise null
     */
    O putIfAbsent(CharSequence key, O value);

    /**
     * Returns the set of keys in the tree for which the given key is a prefix.
     * <p/>
     * This is <i>inclusive</i> - if the given key is an exact match for a key in the tree,
     * that key is also returned.
     *
     * @param key A key which is a prefix of sought keys in the tree
     * @return The set of keys in the tree for which the given key is a prefix, inclusive
     */
    Set<CharSequence> getKeysForPrefix(CharSequence key);

    /**
     * Returns a collection of values associated with keys in the tree for which the given key is a prefix.
     * <p/>
     * This is <i>inclusive</i> - if the given key is an exact match for a key in the tree,
     * that value associated with that key is also returned.
     *
     * @param key A key which is a prefix of keys in the tree for which associated values are sought
     * @return A collection of values associated with keys in the tree for which the given key is a prefix
     */
    Collection<O> getValuesForPrefix(CharSequence key);

    /**
     * Returns a set of {@link KeyValuePair} objects for which the given key
     * is a prefix of the pairs' keys in the index.
     * <p/>
     * This is <i>inclusive</i> - if the given key is an exact match for a key in the tree,
     * that pair for that key is also returned.
     *
     * @param key A key which is a prefix of keys in the tree for which {@link KeyValuePair} objects
     * are sought
     * @return A set of {@link KeyValuePair} objects for which the given key is a prefix of the
     * pairs' keys in the index
     */
    Set<KeyValuePair<O>> getKeyValuePairsForPrefix(CharSequence key);

    /**
     * Removes the object associated with the given key (exact match).
     * If no object is associated with the key, does nothing.
     *
     * @param key The key with which the specified object might be associated
     * @return True if a value was removed (and therefore was associated with the key), false if no object was
     * associated/removed
     */
    boolean remove(CharSequence key);

    /**
     * Encapsulates a key and a value. Objects of this type are returned by
     * {@link RadixTree#getKeyValuePairsForPrefix(CharSequence)}.
     *
     * @param <O> The type of the value
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
}
