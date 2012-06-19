package com.googlecode.concurrenttrees.suffix;

import com.googlecode.concurrenttrees.common.KeyValuePair;

import java.util.Collection;
import java.util.Set;

/**
 * @author Niall Gallagher
 */
public interface SuffixTree<O> {

    /**
     * Returns the value associated with the given key (exact match), or returns null if no such value
     * is associated with the key.
     *
     * @param key The key with which a sought value might be associated
     * @return The value associated with the given key (exact match), or null if no value was associated with the key
     */
    O get(CharSequence key);

    /**
     * Associates the given value with the given key; replacing any previous value associated with the key.
     * Returns the previous value associated with the key, if any.
     * <p/>
     * This operation is performed atomically.
     *
     * @param key The key with which the specified value should be associated
     * @param value The value to associate with the key, which cannot be null
     * @return The previous value associated with the key, if there was one, otherwise null
     */
    O put(CharSequence key, O value);

    /**
     * If a value is not already associated with the given key in the tree, associates the given value with the
     * key; otherwise if an existing value is already associated, returns the existing value and does not overwrite it.
     * <p/>
     * This operation is performed atomically.
     *
     * @param key The key with which the specified value should be associated
     * @param value The value to associate with the key, which cannot be null
     * @return The existing value associated with the key, if there was one; otherwise null in which case the new
     * value was successfully associated
     */
    O putIfAbsent(CharSequence key, O value);
    /**
     * Returns the set of keys in the tree which start with the given prefix.
     * <p/>
     * This is <i>inclusive</i> - if the given prefix is an exact match for a key in the tree, that key is also
     * returned.
     *
     * @param prefix A prefix of sought keys in the tree
     * @return The set of keys in the tree which start with the given prefix, inclusive
     */
    Set<CharSequence> getKeysStartingWith(CharSequence prefix);

    /**
     * Returns the set of values associated with keys in the tree which start with the given prefix.
     * <p/>
     * This is <i>inclusive</i> - if the given prefix is an exact match for a key in the tree, the value associated
     * with that key is also returned.
     *
     * @param prefix A prefix of keys in the tree for which associated values are sought
     * @return The set of values associated with keys in the tree which start with the given prefix, inclusive
     */
    Collection<O> getValuesForKeysStartingWith(CharSequence prefix);

    /**
     * Returns the set of {@link KeyValuePair}s for keys and their associated values in the tree, where the keys start
     * with the given prefix.
     * <p/>
     * This is <i>inclusive</i> - if the given prefix is an exact match for a key in the tree, the {@link KeyValuePair}
     * for that key is also returned.
     *
     * @param prefix A prefix of keys in the tree for which associated {@link KeyValuePair}s are sought
     * @return The set of {@link KeyValuePair}s for keys in the tree which start with the given prefix, inclusive
     */
    Set<KeyValuePair<O>> getKeyValuePairsForKeysStartingWith(CharSequence prefix);

    /**
     * Returns the set of keys in the tree which end with the given suffix.
     * <p/>
     * This is <i>inclusive</i> - if the given suffix is an exact match for a key in the tree, that key is also
     * returned.
     *
     * @param suffix A suffix of sought keys in the tree
     * @return The set of keys in the tree which end with the given suffix, inclusive
     */
    Set<CharSequence> getKeysEndingWith(CharSequence suffix);

    /**
     * Returns the set of values associated with keys in the tree which end with the given suffix.
     * <p/>
     * This is <i>inclusive</i> - if the given suffix is an exact match for a key in the tree, the value associated
     * with that key is also returned.
     *
     * @param suffix A suffix of keys in the tree for which associated values are sought
     * @return The set of values associated with keys in the tree which end with the given suffix, inclusive
     */
    Collection<O> getValuesForKeysEndingWith(CharSequence suffix);

    /**
     * Returns the set of {@link KeyValuePair}s for keys and their associated values in the tree, where the keys end
     * with the given suffix.
     * <p/>
     * This is <i>inclusive</i> - if the given suffix is an exact match for a key in the tree, the {@link KeyValuePair}
     * for that key is also returned.
     *
     * @param suffix A suffix of keys in the tree for which associated {@link KeyValuePair}s are sought
     * @return The set of {@link KeyValuePair}s for keys in the tree which end with the given suffix, inclusive
     */
    Set<KeyValuePair<O>> getKeyValuePairsForKeysEndingWith(CharSequence suffix);

    /**
     * Returns the set of keys in the tree which start with the given prefix.
     * <p/>
     * This is <i>inclusive</i> - if the given prefix is an exact match for a key in the tree, that key is also
     * returned.
     *
     * @param prefix A prefix of sought keys in the tree
     * @return The set of keys in the tree which start with the given prefix, inclusive
     */
    Set<CharSequence> getKeysContaining(CharSequence prefix);

    /**
     * Returns the set of values associated with keys in the tree which start with the given prefix.
     * <p/>
     * This is <i>inclusive</i> - if the given prefix is an exact match for a key in the tree, the value associated
     * with that key is also returned.
     *
     * @param prefix A prefix of keys in the tree for which associated values are sought
     * @return The set of values associated with keys in the tree which start with the given prefix, inclusive
     */
    Collection<O> getValuesForKeysContaining(CharSequence prefix);

    /**
     * Returns the set of {@link com.googlecode.concurrenttrees.common.KeyValuePair}s for keys and their associated values in the tree, where the keys start
     * with the given prefix.
     * <p/>
     * This is <i>inclusive</i> - if the given prefix is an exact match for a key in the tree, the {@link com.googlecode.concurrenttrees.common.KeyValuePair}
     * for that key is also returned.
     *
     * @param prefix A prefix of keys in the tree for which associated {@link com.googlecode.concurrenttrees.common.KeyValuePair}s are sought
     * @return The set of {@link com.googlecode.concurrenttrees.common.KeyValuePair}s for keys in the tree which start with the given prefix, inclusive
     */
    Set<KeyValuePair<O>> getKeyValuePairsForKeysContaining(CharSequence prefix);

    /**
     * Removes the value associated with the given key (exact match).
     * If no value is associated with the key, does nothing.
     *
     * @param key The key for which an associated value should be removed
     * @return True if a value was removed (and therefore was associated with the key), false if no value was
     * associated/removed
     */
    boolean remove(CharSequence key);
}
