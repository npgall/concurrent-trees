package com.npgall.pagedlist;


import com.npgall.pagedlist.common.JDKCompatibleConcurrentPagedList;
import com.npgall.pagedlist.heap.HeapPageFactory;

import java.util.List;

/**
 * An implementation of the JDK {@link List} interface on top of the {@link ConcurrentPagedList} data structure,
 * configured to store the list on disk.
 * <p>
 *     An existing list which is stored on disk can be accessed by supplying its UUID to the constructor.
 * </p>
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public class ConcurrentHeapPagedList<E> extends JDKCompatibleConcurrentPagedList<E> {

    /**
     * Creates a new paged list with a default page size.
     */
    public ConcurrentHeapPagedList() {
        this(16);
    }

    /**
     * Creates a new paged list with the given page size.
     *
     * @param pageSize The number of elements to be stored in each page
     */
    public ConcurrentHeapPagedList(int pageSize) {
        super(new HeapPageFactory<>(pageSize));
    }
}
