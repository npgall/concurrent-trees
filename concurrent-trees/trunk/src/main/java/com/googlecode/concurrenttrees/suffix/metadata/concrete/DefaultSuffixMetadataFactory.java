package com.googlecode.concurrenttrees.suffix.metadata.concrete;

import com.googlecode.concurrenttrees.suffix.metadata.SuffixMetadata;
import com.googlecode.concurrenttrees.suffix.metadata.SuffixMetadataFactory;

import java.util.List;

/**
 * A basic implementation of {@link SuffixMetadataFactory} which creates {@link DefaultSuffixMetadata} objects.
 *
 * @author Niall Gallagher
 */
public class DefaultSuffixMetadataFactory<Document extends CharSequence> implements SuffixMetadataFactory<Document> {

    @Override
    public SuffixMetadata<Document> createSuffixMetadata(List<Document> documentsEndingWithSuffix, List<Document> documentsExactlyMatchingSuffix) {
        if (documentsEndingWithSuffix == null) {
            throw new IllegalStateException("The documentsEndingWithSuffix argument was null");
        }
        if (documentsExactlyMatchingSuffix == null) {
            throw new IllegalStateException("The documentsExactlyMatchingSuffix argument was null");
        }
        return new DefaultSuffixMetadata<Document>(documentsEndingWithSuffix, documentsExactlyMatchingSuffix);
    }
}
