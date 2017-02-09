package com.googlecode.concurrenttrees.common;

/**
 * A {@link CharSequence} which wraps a delegate {@link CharSequence}, and keeps track of the <i>search depth</i> -
 * the greatest index within the delegate {@link CharSequence} which was accessed via the {@link #charAt(int)} method.
 *
 * @author npgall
 */
public class DepthTrackingCharSequence implements CharSequence {

    private final CharSequence delegate;
    private final int start;
    private final int length;
    private final IndexHolder lastIndexAccessed;

    public DepthTrackingCharSequence(CharSequence delegate) {
        this(delegate, 0, delegate.length(), new IndexHolder());
    }

    private DepthTrackingCharSequence(CharSequence delegate, int start, int length, IndexHolder lastIndexAccessed) {
        this.delegate = delegate;
        this.start = start;
        this.length = length;
        this.lastIndexAccessed = lastIndexAccessed;
    }

    /**
     * @return the <i>search depth</i> - the greatest index within the delegate {@link CharSequence} which was accessed
     * via the {@link #charAt(int)} method. Returns -1 if {@link #charAt(int)} has not been called successfully.
     */
    public int getSearchDepth() {
        return lastIndexAccessed.value;
    }

    @Override
    public int length() {
        return this.length;
    }

    @Override
    public char charAt(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        if (index >= length) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        lastIndexAccessed.value = Math.max(this.lastIndexAccessed.value, this.start + index);
        return delegate.charAt(this.start + index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (start < 0) {
            throw new IndexOutOfBoundsException(String.valueOf(start));
        }
        if (end > length) {
            throw new IndexOutOfBoundsException(String.valueOf(end));
        }
        int subsequenceLength = end - start;
        if (subsequenceLength < 0) {
            throw new IndexOutOfBoundsException(String.valueOf(subsequenceLength));
        }
        return new DepthTrackingCharSequence(delegate, this.start + start, subsequenceLength, this.lastIndexAccessed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DepthTrackingCharSequence)) return false;

        DepthTrackingCharSequence that = (DepthTrackingCharSequence) o;

        if (start != that.start) return false;
        if (length != that.length) return false;
        if (!delegate.equals(that.delegate)) return false;
        return lastIndexAccessed.value == that.lastIndexAccessed.value;
    }

    @Override
    public int hashCode() {
        int result = delegate.hashCode();
        result = 31 * result + start;
        result = 31 * result + length;
        result = 31 * result + lastIndexAccessed.value;
        return result;
    }

    static class IndexHolder {
        int value = -1;
    }

    @Override
    public String toString() {
        return CharSequences.toString(this);
    }
}
