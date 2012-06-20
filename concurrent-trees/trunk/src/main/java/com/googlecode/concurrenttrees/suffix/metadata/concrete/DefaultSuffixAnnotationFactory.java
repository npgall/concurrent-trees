package com.googlecode.concurrenttrees.suffix.metadata.concrete;

import com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotation;
import com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotationFactory;

import java.util.Collection;
import java.util.List;

/**
 * A basic implementation of {@link com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotationFactory} which creates {@link DefaultSuffixAnnotation} objects.
 *
 * @author Niall Gallagher
 */
public class DefaultSuffixAnnotationFactory<FullKey extends CharSequence> implements SuffixAnnotationFactory<FullKey> {

    @Override
    public SuffixAnnotation<FullKey> createSuffixAnnotation(Collection<FullKey> originalKeys, FullKey originalKeyEqualToSuffix) {
        if (originalKeys == null) {
            throw new IllegalStateException("The originalKeys argument was null");
        }
        if (originalKeyEqualToSuffix == null) {
            throw new IllegalStateException("The originalKeyEqualToSuffix argument was null");
        }
        return new DefaultSuffixAnnotation<FullKey>(originalKeys, originalKeyEqualToSuffix);
    }
}
