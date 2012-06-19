package com.googlecode.concurrenttrees.suffix.metadata.concrete;

import com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotation;
import com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotationFactory;

import java.util.List;

/**
 * A basic implementation of {@link com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotationFactory} which creates {@link DefaultSuffixAnnotation} objects.
 *
 * @author Niall Gallagher
 */
public class DefaultSuffixAnnotationFactory<Document extends CharSequence> implements SuffixAnnotationFactory<Document> {

    @Override
    public SuffixAnnotation<Document> createSuffixAnnotation(List<Document> documentsEndingWithSuffix, List<Document> documentsExactlyMatchingSuffix) {
        if (documentsEndingWithSuffix == null) {
            throw new IllegalStateException("The documentsEndingWithSuffix argument was null");
        }
        if (documentsExactlyMatchingSuffix == null) {
            throw new IllegalStateException("The documentsExactlyMatchingSuffix argument was null");
        }
        return new DefaultSuffixAnnotation<Document>(documentsEndingWithSuffix, documentsExactlyMatchingSuffix);
    }
}
