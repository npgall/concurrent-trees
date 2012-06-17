package com.googlecode.concurrenttrees.reverseradix;

import com.googlecode.concurrenttrees.common.KeyValuePair;

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
public interface ReverseRadixTree<O> {

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
     * Returns the set of keys in the tree for which the given key is a postfix.
     * <p/>
     * This is <i>inclusive</i> - if the given key is an exact match for a key in the tree,
     * that key is also returned.
     *
     * @param key A key which is a postfix of sought keys in the tree
     * @return The set of keys in the tree for which the given key is a postfix, inclusive
     */
    Set<CharSequence> getKeysForPostfix(CharSequence key);

    /**
     * Returns a collection of values associated with keys in the tree for which the given key is a postfix.
     * <p/>
     * This is <i>inclusive</i> - if the given key is an exact match for a key in the tree,
     * that value associated with that key is also returned.
     *
     * @param key A key which is a postfix of keys in the tree for which associated values are sought
     * @return A collection of values associated with keys in the tree for which the given key is a postfix
     */
    Collection<O> getValuesForPostfix(CharSequence key);

    /**
     * Returns a set of {@link KeyValuePair} objects for which the given key
     * is a postfix of the pairs' keys in the index.
     * <p/>
     * This is <i>inclusive</i> - if the given key is an exact match for a key in the tree,
     * that pair for that key is also returned.
     *
     * @param key A key which is a postfix of keys in the tree for which {@link KeyValuePair} objects
     * are sought
     * @return A set of {@link KeyValuePair} objects for which the given key is a postfix of the
     * pairs' keys in the index
     */
    Set<KeyValuePair<O>> getKeyValuePairsForPostfix(CharSequence key);

    /**
     * Removes the object associated with the given key (exact match).
     * If no object is associated with the key, does nothing.
     *
     * @param key The key with which the specified object might be associated
     * @return True if a value was removed (and therefore was associated with the key), false if no object was
     * associated/removed
     */
    boolean remove(CharSequence key);
}
