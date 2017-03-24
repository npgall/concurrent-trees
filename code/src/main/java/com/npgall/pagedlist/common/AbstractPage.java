package com.npgall.pagedlist.common;

import com.googlecode.concurentlocks.ReadWriteUpdateLock;
import com.googlecode.concurentlocks.ReentrantReadWriteUpdateLock;
import com.npgall.pagedlist.disk.DiskPage;
import com.npgall.pagedlist.heap.HeapPage;

import java.util.Arrays;

/**
 * A page which caches its elements in an array, on the Java heap.
 * <p/>
 * This serves as a common superclass of both the {@link HeapPage} and {@link DiskPage} implementations;
 * because the heap page is little more than a wrapper around this, and the disk page uses this
 * as its on-heap representation of the pages it persists to disk.
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public abstract class AbstractPage<E> implements Page<E> {

    protected final Object[] elements;
    // The following read-write-*update* lock from the concurrent-locks library,
    // is a special lock which supports concurrent read-before-write access patterns efficiently...
    protected final ReadWriteUpdateLock pageLock = new ReentrantReadWriteUpdateLock();
    protected volatile PageReference<E> nextPageReference;
    protected volatile int currentSize = 0;

    protected AbstractPage(int pageSize) {
        this.elements = new Object[pageSize];
    }


    // ===== Inter-page access methods =====

    @Override
    public PageReference<E> getNextPageReference() {
        return nextPageReference;
    }

    @Override
    public void setNextPageReference(PageReference<E> nextPageReference) {
        pageLock.writeLock().lock();
        try {
            this.nextPageReference = nextPageReference;
        }
        finally {
            pageLock.writeLock().unlock();
        }
    }

    @Override
    public PageReference<E> getPreviousPageReference() {
        throw new UnsupportedOperationException("This Page implementation does not store references to previous pages");
    }

    @Override
    public void setPreviousPageReference(PageReference<E> previousPageReference) {
        throw new UnsupportedOperationException("This Page implementation does not store references to previous pages");
    }


    // ===== Page locking methods =====

    @Override
    public ReadWriteUpdateLock getPageLock() {
        return pageLock;
    }


    // ===== Methods to modify the contents of the page =====

    @Override
    public void add(E element) {
        add(currentSize, element); // the target method will acquire a lock
    }

    @Override
    public void add(int indexWithinPage, E element) {
        pageLock.writeLock().lock();
        try {
            if (currentSize >= elements.length) {
                throw new IndexOutOfBoundsException("Page full");
            }
            System.arraycopy(elements, indexWithinPage, elements, indexWithinPage + 1, currentSize - indexWithinPage);

            elements[indexWithinPage] = element;
            currentSize++;
        }
        finally {
            pageLock.writeLock().unlock();
        }
    }

    @Override
    public E remove(int indexWithinPage) {
        pageLock.writeLock().lock();
        try {
            E previous = getInternal(indexWithinPage);
            int itemsToMove = currentSize - indexWithinPage - 1;
            if (itemsToMove > 0) {
                System.arraycopy(elements, indexWithinPage + 1, elements, indexWithinPage, itemsToMove);
            }
            elements[--currentSize] = null; // null-out the space to the right of elements moved
            return previous;
        }
        finally {
            pageLock.writeLock().unlock();
        }
    }

    @Override
    public E set(int index, E element) {
        pageLock.writeLock().lock();
        try {
            E previous = getInternal(index);
            elements[index] = element;
            return previous;
        }
        finally {
            pageLock.writeLock().unlock();
        }
    }

    @Override
    public void rebalance(int pageSize, Page<E> otherPage) {
        pageLock.writeLock().lock();
        otherPage.getPageLock().writeLock().lock();
        try {
            if (!(otherPage instanceof AbstractPage)) {
                throw new IllegalArgumentException("Other page must also be an instance of " + this.getClass().getSimpleName());
            }
            AbstractPage<E> otherAbstractPage = (AbstractPage<E>) otherPage;
            final int elementsToMove = (int) Math.ceil((double) pageSize / 2); // rounds up for odd page sizes
            final int fromIndex = pageSize - elementsToMove;

            System.arraycopy(elements, fromIndex, otherAbstractPage.elements, 0, elementsToMove);
            // Now, null-out the second half of the first page...
            Arrays.fill(elements, fromIndex, pageSize, null);
            currentSize = currentSize - elementsToMove;
            otherAbstractPage.currentSize = elementsToMove;
        }
        finally {
            otherPage.getPageLock().writeLock().unlock();
            pageLock.writeLock().unlock();
        }
    }


    // ===== Methods to access the contents of the page =====

    @Override
    public E get(int index) {
        return getInternal(index);
    }

    @SuppressWarnings("unchecked")
    private E getInternal(int index) {
        return (E) elements[index];
    }

    @Override
    public int size() {
        return currentSize;
    }


    // ===== Miscellaneous methods =====

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < currentSize; i++) {
            sb.append(String.valueOf(elements[i]));
            if (i < currentSize - 1)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
