package com.googlecode.concurrenttrees.suffix.metadata.concrete;

import com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A non-optimized implementation of the {@link com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotation} interface. Stores all variables and supports all
 * behaviours required by the tree, but not very memory efficient (contains fields which might contain empty lists).
 *
 * @author Niall Gallagher
 */
public class DefaultSuffixAnnotation<Document extends CharSequence> implements SuffixAnnotation<Document> {

    private final List<Document> documentsEndingWithSuffix;
    private final List<Document> documentsExactlyMatchingSuffix;

    public DefaultSuffixAnnotation(List<Document> documentsEndingWithSuffix, List<Document> documentsExactlyMatchingSuffix) {
        if (documentsEndingWithSuffix instanceof CopyOnWriteArrayList) {
            this.documentsEndingWithSuffix = documentsEndingWithSuffix;
        }
        else {
            this.documentsEndingWithSuffix = new CopyOnWriteArrayList<Document>(documentsEndingWithSuffix);
        }
        if (documentsExactlyMatchingSuffix instanceof CopyOnWriteArrayList) {
            this.documentsExactlyMatchingSuffix = documentsExactlyMatchingSuffix;
        }
        else {
            this.documentsExactlyMatchingSuffix = new CopyOnWriteArrayList<Document>(documentsExactlyMatchingSuffix);
        }
    }

    @Override
    public List<Document> getDocumentsEndingWithSuffix() {
        return this.documentsEndingWithSuffix;
    }

    @Override
    public List<Document> getDocumentsExactlyMatchingSuffix() {
        return this.documentsExactlyMatchingSuffix;
    }
}
