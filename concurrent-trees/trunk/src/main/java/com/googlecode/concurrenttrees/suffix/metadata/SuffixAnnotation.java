package com.googlecode.concurrenttrees.suffix.metadata;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Used internally by {@link com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree} as values which it associates
 * with suffixes it adds to a {@link com.googlecode.concurrenttrees.radix.RadixTree}.
 *
 * @param <OriginalKey> The type of the key from which the suffix was generated (could represent as
 * {@link CharSequence} or {@link String})
 *
 * @author Niall Gallagher
 */
public interface SuffixAnnotation<OriginalKey extends CharSequence> {

    /**
     * Returns the (mutable) collection of original keys from which the suffix was generated. Multiple original keys can
     * share a common suffix hence this is a collection. Typically these will be references to the original keys rather
     * than copies.
     * <p/>
     * This is a {@link Collection} rather than a {@link Set} to support adding {@link CharSequence} objects which do
     * not properly support {@link #equals(Object)} and {@link #hashCode()}. Duplicates should not be added.
     * <p/>
     * The collection returned should support concurrent access.
     *
     * @return The original keys from which the suffix was generated
     */
    Collection<OriginalKey> getOriginalKeys();

    /**
     * Returns the original key which is equal to the suffix, or null if this suffix is not equal to any original key.
     * Typically this will be a reference to an original key rather than a copy.
     * <p/>
     * When a key is added to the suffix tree, all possible suffixes of that key are inserted into the tree. One of
     * those suffixes is actually equal to the entire original key. Only one of these suffixes inserted will be
     * equal to the original key, therefore this will be null for most suffixes. However other keys inserted
     * subsequently might cause exiting nodes to become flagged as equal to other keys.
     * <p/>
     * This is useful for speeding up "starts with" searches. The path in the tree to a node representing a suffix
     * which is equal to an original key, encodes the entire original key through the sequence of edges. As such,
     * ancestors of that node represent prefixes of that original key.
     * <p/>
     * An algorithm supporting "starts with" searches:
     * <ol>
     *     <li>
     *         Find the node which exactly matches the search key, and then find all of that node's descendants
     *     </li>
     *     <li>
     *         Add the "original keys equal to suffixes" from all of these nodes to a result list to be returned
     *     </li>
     * </ol>
     * The search key will be a prefix of all of those original keys.
     * <p/>
     * Implementation hint: the original key returned by this method should be included in the set returned by
     * {@link #getOriginalKeys()}
     *
     * @return The original key which equals the suffix, or null if this suffix does not equal an original key
     */
    OriginalKey getOriginalKeyEqualToSuffix();

    /**
     * Sets the original key equal to this suffix.
     *
     * @param originalKey The original key to store, can be null to remove an association
     * @throws IllegalStateException If an original key equal to this suffix is already stored
     */
    void setOriginalKeyEqualToSuffix(OriginalKey originalKey);

}
