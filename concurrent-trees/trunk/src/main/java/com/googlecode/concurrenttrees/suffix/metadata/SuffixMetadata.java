package com.googlecode.concurrenttrees.suffix.metadata;

import java.util.List;

/**
 * Used internally by {@link com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree} as values which it associates
 * with suffixes it adds to a {@link com.googlecode.concurrenttrees.radix.RadixTree}.
 *
 * @param <Document> The actual type of the documents (could represent as {@link CharSequence} or {@link String})
 *
 * @author Niall Gallagher
 */
public interface SuffixMetadata<Document extends CharSequence> {

    /**
     * Returns the original documents which end with the suffix represented by the node having this
     * {@link SuffixMetadata} value. Typically these will be references to the original documents rather than copies.
     *
     * @return The original documents which end with the suffix represented by the node having this
     * {@link SuffixMetadata} value
     */
    List<Document> getDocumentsEndingWithSuffix();

    /**
     * Returns the original documents which are an exact match for the suffix represented by the node having this
     * {@link SuffixMetadata} value. Typically these will be references to the original documents rather
     * than copies.
     * <p/>
     * In other words, the suffix represented by a node having this {@link SuffixMetadata} value,
     * does not represent a suffix of the original documents, but an exact match for the entire contents of the original
     * documents. As such, ancestors of this node represent prefixes of these documents, and the root node represents
     * the start of those documents.
     * <p/>
     * This is useful for speeding up "starts with" searches:
     * <ol>
     *     <li>
     *         Find the node which exactly matches the search key, and then find all of that node's descendants
     *     </li>
     *     <li>
     *         Add the "exact match documents" from these nodes to a result list to be returned
     *     </li>
     * </ol>
     * The search key will be a prefix of all of those documents.
     *
     * @return The original documents which are an exact match for the suffix represented by the node having this
     * {@link SuffixMetadata} value
     */
    List<Document> getDocumentsExactlyMatchingSuffix();
}
