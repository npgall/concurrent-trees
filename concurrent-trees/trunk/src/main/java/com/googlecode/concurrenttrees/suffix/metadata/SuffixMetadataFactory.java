package com.googlecode.concurrenttrees.suffix.metadata;

import java.util.List;

/**
 * An interface for a factory which creates new {@link SuffixMetadata} objects on demand, to encapsulate specified
 * variables. Factory objects can choose to return implementations of the {@link SuffixMetadata} interface which are
 * memory-optimized for storing only the given variables, potentially further optimized based on variable values.
 *
 * @author Niall Gallagher
 */
public interface SuffixMetadataFactory<Document extends CharSequence> {

    /**
     * Returns a new {@link SuffixMetadata} object which encapsulates the arguments supplied, optionally returning
     * implementations of the {@link SuffixMetadata} interface which are memory-optimized for storing only the supplied
     * combination of variables, potentially further optimized based on variable values.
     *
     * @param documentsEndingWithSuffix A list of {@link Document} objects (which extend CharSequence). This will never
     * be null, but may be empty
     *
     * @param documentsExactlyMatchingSuffix A list of {@link Document} objects (which extend CharSequence). This will
     * never be null, but may be empty
     *
     * @return An object implementing the {@link SuffixMetadata} interface which stores the given variables
     */
    SuffixMetadata<Document> createSuffixMetadata(List<Document> documentsEndingWithSuffix, List<Document> documentsExactlyMatchingSuffix);
}
