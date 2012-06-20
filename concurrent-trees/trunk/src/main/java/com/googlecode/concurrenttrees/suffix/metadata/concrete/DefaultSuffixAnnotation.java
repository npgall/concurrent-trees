package com.googlecode.concurrenttrees.suffix.metadata.concrete;

import com.googlecode.concurrenttrees.common.CharSequenceUtil;
import com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotation;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A non-optimized implementation of the {@link com.googlecode.concurrenttrees.suffix.metadata.SuffixAnnotation} interface. Stores all variables and supports all
 * behaviours required by the tree, but not very memory efficient (contains fields which might contain empty lists).
 *
 * @author Niall Gallagher
 */
public class DefaultSuffixAnnotation<FullKey extends CharSequence> implements SuffixAnnotation<FullKey> {

    private final Collection<FullKey> originalKeys;
    private volatile FullKey originalKeyEqualToSuffix;

    public DefaultSuffixAnnotation(Collection<FullKey> originalKeys, FullKey originalKeyEqualToSuffix) {
        if (originalKeys instanceof CopyOnWriteArrayList) {
            this.originalKeys = originalKeys;
        }
        else {
            this.originalKeys = new CopyOnWriteArrayList<FullKey>(originalKeys);
        }
        this.originalKeyEqualToSuffix = originalKeyEqualToSuffix;
    }

    @Override
    public Collection<FullKey> getOriginalKeys() {
        return this.originalKeys;
    }

    @Override
    public FullKey getOriginalKeyEqualToSuffix() {
        return this.originalKeyEqualToSuffix;
    }

    @Override
    public synchronized void setOriginalKeyEqualToSuffix(FullKey fullKey) {
        FullKey existingValue = this.originalKeyEqualToSuffix;
        if (existingValue != null) {
            throw new IllegalStateException("OriginalKeyEqualToSuffix already set: " + CharSequenceUtil.toString(existingValue));
        }
        this.originalKeyEqualToSuffix = fullKey;
    }
}
