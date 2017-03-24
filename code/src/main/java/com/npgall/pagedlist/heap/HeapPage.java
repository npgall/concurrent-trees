package com.npgall.pagedlist.heap;

import com.npgall.pagedlist.common.AbstractPage;
import com.npgall.pagedlist.common.Page;
import com.npgall.pagedlist.common.PageReference;

/**
 * An implementation of {@link Page} which is stored on the JVM heap.
 * <p>
 *     This implementation of Page implements the {@link PageReference} interface directly as well, because on-heap
 *     objects can be accessed via Java object references; there is no need for a separate reference entity.
 * </p>
 * @author Niall Gallagher (niall@npgall.com)
 */
public class HeapPage<E> extends AbstractPage<E> implements PageReference<E> {

    public HeapPage(int pageSize) {
        super(pageSize);
    }

    /**
     * {@inheritDoc}
     * <p>
     *     HeapPage objects don't require external PageReference objects, and so they implement the PageReference
     *     interface directly to return 'this' instead.
     * </p>
     */
    @Override
    public Page<E> getPage() {
        return this;
    }

    /**
     * {@inheritDoc}
     * Does nothing in this implementation.
     */
    @Override
    public void delete() {
        // No-op
    }
}
