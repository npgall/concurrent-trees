package com.googlecode.concurrenttrees.suffix.metadata;

import java.util.Collection;
import java.util.List;

/**
 * An interface for a factory which creates new {@link SuffixAnnotation} objects on demand, to encapsulate specified
 * variables. Factory objects can choose to return implementations of the {@link SuffixAnnotation} interface which are
 * memory-optimized for storing only the given variables, potentially further optimized based on variable values.
 *
 * @author Niall Gallagher
 */
public interface SuffixAnnotationFactory<FullKey extends CharSequence> {

    /**
     * Returns a new {@link SuffixAnnotation} object which encapsulates the arguments supplied, optionally returning
     * implementations of the {@link SuffixAnnotation} interface which are memory-optimized for storing only the
     * supplied combination of variables.
     *
     * @param originalKeys A list of {@link FullKey} objects (which extend CharSequence). This will never
     * be null, but may be empty
     *
     * @param originalKeyEqualToSuffix A {@link FullKey} object, which can be null
     *
     * @return An object implementing the {@link SuffixAnnotation} interface which stores the given variables
     */
    SuffixAnnotation<FullKey> createSuffixAnnotation(Collection<FullKey> originalKeys, FullKey originalKeyEqualToSuffix);
}
