package com.npgall.pagedlist.heap;

import com.npgall.pagedlist.common.Page;
import com.npgall.pagedlist.common.PageFactory;
import com.npgall.pagedlist.common.PageReference;

/**
 * Creates {@link HeapPage} objects on demand, which will be persisted to the configured storage directory.
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public class HeapPageFactory<E> implements PageFactory<E> {

    private final int pageSize;

    public HeapPageFactory(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public boolean isDoublyLinked() {
        return false;
    }

    @Override
    public HeapPage<E> createFirstPage() {
        return createPage();
    }

    @Override
    public HeapPage<E> createPage() {
        return new HeapPage<>(pageSize);
    }

    @Override
    public PageReference<E> getPageReference(Page<E> page) {
        // HeapPages don't require external PageReference objects, and implement the PageReference interface directly...
        return (HeapPage<E>)page;
    }
}
