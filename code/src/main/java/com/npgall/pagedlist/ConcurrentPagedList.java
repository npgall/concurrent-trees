package com.npgall.pagedlist;

import com.npgall.pagedlist.common.Page;
import com.npgall.pagedlist.common.PageFactory;
import com.npgall.pagedlist.common.PageReference;

import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A list data structure which is a hybrid between an {@link java.util.ArrayList} and a {@link java.util.LinkedList},
 * which can be stored in heap memory or on disk, and which supports concurrent reads and writes.
 * <p>
 *     The elements added to the list are grouped in {@link Page} objects. The concept is similar to an
 *     <a href="https://en.wikipedia.org/wiki/Unrolled_linked_list">unrolled linked list</a>, except the list data
 *     structure is agnostic as to the internal representation of page objects, and as such heap-based or disk-based
 *     representations are supported. The internal representation of pages is determined by the {@link PageFactory}
 *     implementation which is supplied to the constructor.
 * </p>
 * <p>
 *     <b>Concurrency support</b><br/>
 *     Unlike the JDK {@link java.util.ArrayList} or {@link java.util.LinkedList}, this data structure supports
 *     concurrent reads and writes. See the {@link #findPage(int, boolean)} method for details on concurrency support.
 * </p>
 * <p>
 *     <b>Time complexity</b><br/>
 *     The page size (max number of elements which will be stored in each page) is configurable.
 *     <ul>
 *         <li>
 *             If page size is one, this data structure will behave like a linked list (albeit optionally stored on
 *             disk). Time complexity to access an element will be O(n).
 *         </li>
 *         <li>
 *             If page size is greater than the number of elements in the list,
 *             then this list will behave like an array. Time complexity to access an element will be O(1).
 *         </li>
 *         <li>
 *             If page size is set somewhere in the middle, time complexity to access an element will be O(n/pageSize).
 *         </li>
 *     </ul>
 *     Setting a page size somewhere in the middle allows the tradeoffs between random access performance, sequential
 *     performance, compactness, lock contention, mid-list insertion performance, and the overhead to extend the size
 *     of the list to be tuned.
 * </p>
 * <p>
 *     Note this class implements the paged list data structure only. It does not implement the full JDK
 *     List API. However two subclasses are also provided which implement the JDK List API on top of this data structure
 *     - see {@link ConcurrentHeapPagedList} and {@link ConcurrentDiskPagedList}.
 * </p>
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public class ConcurrentPagedList<E> implements PagedList<E> {

    /*
     * See JavaDocs in the PagedList interface
     * for API documentation for the methods implemented in this class.
     */

    private final PageFactory<E> pageFactory;
    private final PageReference<E> firstPageReference;

    public ConcurrentPagedList(PageFactory<E> pageFactory) {
        this.pageFactory = pageFactory;
        this.firstPageReference = pageFactory.getPageReference(pageFactory.createFirstPage());
    }

    // ===== Methods to modify the contents of the list =====

    @Override
    public void add(E element) {
        ScanResult<E> scanResult = findPage(Integer.MAX_VALUE, true); // ..find the last page
        try {
            int insertionIndex = scanResult.currentPageReference != null
                    ? scanResult.indexOfFirstItemInCurrentPage + scanResult.currentPageReference.getPage().size()
                    : scanResult.indexOfFirstItemInPreviousPage + scanResult.previousPageReference.getPage().size();
            doAdd(scanResult, insertionIndex, element);
        } finally {
            scanResult.releaseLocks();
        }
    }

    @Override
    public void add(int index, E element) {
        ScanResult<E> scanResult = findPage(index, true);
        try {
            doAdd(scanResult, index, element);
        } finally {
            scanResult.releaseLocks();
        }
    }

    private void doAdd(ScanResult<E> scanResult, int index, E element) {
        PageReference<E> currentPageReference = scanResult.currentPageReference;

        // We need to insert the item into an existing page,
        // which might in turn require us to split the page in two...
        PageReference<E> insertionPageReference;


        int insertionIndexWithinPage;

        boolean isAppendOperation = (currentPageReference == null);
        if (isAppendOperation) {
            // The index is greater than the highest index currently stored in the list.
            PageReference<E> previousPageReference = scanResult.previousPageReference;

            // Check that the index is exactly one higher than the currently highest index stored in the list...
            Page<E> previousPage = previousPageReference.getPage();
            int currentlyHighestIndex = scanResult.indexOfFirstItemInPreviousPage + previousPage.size() - 1;
            if (index != currentlyHighestIndex + 1) {
                throw new IndexOutOfBoundsException(String.valueOf(index));
            }
            insertionPageReference = previousPageReference;
            insertionIndexWithinPage = index - scanResult.indexOfFirstItemInPreviousPage;
        } else {
            insertionPageReference = currentPageReference;
            insertionIndexWithinPage = index - scanResult.indexOfFirstItemInCurrentPage;
        }

        Page<E> insertionPage = insertionPageReference.getPage();

        if (insertionPage.size() >= pageFactory.getPageSize()) {
            // The insertion page is full.

            // --> If the new element is being appended to the end of the list,
            // we just allocate a new page and insert the new element into that page.
            // --> OTOH, if the new element is being inserted into the middle of the insertion page,
            // we need to split that page in two, and rebalance elements between the pages,
            // so that the insertion page will retain the first half of its elements,
            // and the new page will contain the second half of its elements...

            Page<E> newPage = pageFactory.createPage();
            PageReference<E> nextPageReference = insertionPage.getNextPageReference();
            newPage.setNextPageReference(nextPageReference);
            if (!isAppendOperation) {
                insertionPage.rebalance(pageFactory.getPageSize(), newPage);
            }

            PageReference<E> newPageReference = pageFactory.getPageReference(newPage);
            insertionPage.setNextPageReference(newPageReference);

            // Determine if we need to insert the new element into the current page, or the new page...
            if ((pageFactory.getPageSize() > 1 || isAppendOperation) && insertionIndexWithinPage >= insertionPage.size()) {
                // Insert new item into the new page.
                insertionIndexWithinPage = insertionIndexWithinPage - insertionPage.size();
                insertionPage = newPage;
            }
            // ..else by default we insert into the current page.
        }
        // .. else the current page was not full, so there was no need to split it.

        // Insert the new element into the page...
        insertionPage.add(insertionIndexWithinPage, element);
    }

    @Override
    public E remove(int index) {
        ScanResult<E> scanResult = findPage(index, true);
        try {
            PageReference<E> currentPageReference = scanResult.currentPageReference;
            if (currentPageReference == null) {
                throw new IndexOutOfBoundsException(String.valueOf(index));
            }
            Page<E> currentPage = currentPageReference.getPage();
            int removalIndexWithinPage = index - scanResult.indexOfFirstItemInCurrentPage;
            E previous = currentPage.remove(removalIndexWithinPage);
            // If the page is not the first page and is now empty, remove it from the list...
            if (currentPageReference != firstPageReference && currentPage.size() == 0) {
                // Unlink the page from the list...
                Page<E> previousPage = scanResult.previousPageReference.getPage();
                previousPage.setNextPageReference(currentPage.getNextPageReference());
                // Delete the page from disk if necessary...
                currentPage.delete();
            }
            return previous;
        } finally {
            scanResult.releaseLocks();
        }
    }

    @Override
    public E set(int index, E element) {
        ScanResult<E> scanResult = findPage(index, true);
        try {
            if (scanResult.currentPageReference == null) {
                throw new IndexOutOfBoundsException(String.valueOf(index));
            }
            Page<E> page = scanResult.currentPageReference.getPage();
            final int indexWithinPage = index - scanResult.indexOfFirstItemInCurrentPage;
            return page.set(indexWithinPage, element);
        } finally {
            scanResult.releaseLocks();
        }
    }


    // ===== Methods to access the contents of the list =====

    @Override
    public E get(int index) {
        ScanResult<E> scanResult = findPage(index, false);
        try {
            if (scanResult.currentPageReference == null) {
                throw new IndexOutOfBoundsException(String.valueOf(index));
            }
            final int indexWithinPage = index - scanResult.indexOfFirstItemInCurrentPage;
            return scanResult.currentPageReference.getPage().get(indexWithinPage);
        } finally {
            scanResult.releaseLocks();
        }
    }

    @Override
    public int size() {
        int size = 0;
        PageReference<E> currentPageReference = firstPageReference;
        while (currentPageReference != null) {
            Page<E> currentPage = currentPageReference.getPage();
            Lock readLock = currentPage.getPageLock().readLock();
            readLock.lock();
            try {
                size += currentPage.size();
                currentPageReference = currentPage.getNextPageReference();
            } finally {
                readLock.unlock();
            }
        }
        return size;
    }


    // ===== Internal methods to navigate the paged list =====

    /**
     * Traverses the paged list to locate the page containing the given index.
     *
     * <ul>
     *     <li>
     *         If a page with the given index is found it will be returned as the <i>current</i> page.
     *         Unless that page is the first in the list, the <i>previous</i> page encountered will be returned as well.
     *     </li>
     *     <li>
     *         If a page with the given index is not found, null will be returned as the <i>current</i> page.
     *         The last page in the list will be returned as the <i>previous</i> page.
     *     </li>
     * </ul>
     * <p>
     *     <b>Note about locking</b><br/>
     *     This method uses a <i>windowed</i> locking algorithm, whereby only the current page being scanned and the
     *     previous page scanned, will be locked at any one time. The algorithm will lock these pairs of pages as it
     *     moves left-to-right, and pages to the left will be <i>unlocked</i> as the scan proceeds.
     * </p>
     * <p>
     *     The locking algorithm uses the {@link com.googlecode.concurentlocks.ReadWriteUpdateLock} from the
     *     <i>concurrent-locks</i> library to control locking.
     *     Unlike the {@link java.util.concurrent.locks.ReadWriteLock} in the JDK, this avoids the need to acquire
     *     <i>write</i> locks during the scan. Only a <i>read</i> lock or an <i>update</i> lock will be acquired during
     *     the scan, and only on at most two pages in the list at a time.
     * </p>
     * <p>
     *     As the <i>read</i> and <i>update</i> locks do not block reading threads, concurrent reads from the pages
     *     being scanned can proceed in parallel, and concurrent writes to different pages can proceed in parallel.
     * </p>
     * <p>
     *     When the algorithm has located the <i>current</i> and <i>previous</i> pages, it will <i>not</i> release
     *     the read or update locks it acquired on those pages.
     *     The locks will be returned in the {@link ScanResult} object to the calling code.
     *     <ul>
     *         <li>
     *             For read operations, the calling code will release the <i>read</i> locks which were acquired,
     *             whenever the particular read operation implemented by the calling code is complete.
     *         </li>
     *         <li>
     *             For write operations, the calling code will <i>upgrade the update locks on the pages to be modified,
     *             to write locks</i>, modify the pages, and will then release the locks.
     *         </li>
     *     </ul>
     * </p>
     *
     * @param index The index in the list for which the containing page is sought
     * @param lockForUpdate If true, the <i>windowed</i> locking algorithm should acquire <i>update</i> locks on
     * pairs of pages as the scan proceeds; if false, it should acquire <i>read</i> locks on the pairs of pages instead
     * @return An object encapsulating a <i>current</i> page found (if any), and the <i>previous</i> page found (if any)
     * and the locks which have been acquired on each of those pages
     */
    private ScanResult<E> findPage(final int index, final boolean lockForUpdate) {
        PageReference<E> previousPageReference = null;
        int indexOfFirstItemInPreviousPage = 0;

        PageReference<E> currentPageReference = firstPageReference;
        int indexOfFirstItemInCurrentPage = 0;

        // Lock slots...
        Lock currentPageLock = null;
        Lock previousPageLock = null;
        Lock trailingLock = null;

        while (currentPageReference != null) {
            Page<E> currentPage = currentPageReference.getPage();
            Lock newLock = lockForUpdate ? currentPage.getPageLock().updateLock() : currentPage.getPageLock().readLock();
            newLock.lock();

            // Shift the locks down in the slots..
            trailingLock = previousPageLock;
            previousPageLock = currentPageLock;
            currentPageLock = newLock;

            try {
                if (index >= indexOfFirstItemInCurrentPage && index <= indexOfFirstItemInCurrentPage + currentPage.size() - 1) {
                    // Found the page containing this index...
                    return new ScanResult<>(previousPageReference,
                            previousPageLock,
                            indexOfFirstItemInPreviousPage,
                            currentPageReference,
                            currentPageLock,
                            indexOfFirstItemInCurrentPage);
                }
                // ..otherwise we did not yet find the page containing the index, so the loop will continue.

                // Recalculate the indexes of the first item in the current and previous pages for the next iteration...
                indexOfFirstItemInPreviousPage = indexOfFirstItemInCurrentPage;
                indexOfFirstItemInCurrentPage = indexOfFirstItemInCurrentPage + currentPage.size();
                previousPageReference = currentPageReference;
                // Get a reference to the next page, from the current page.
                // Note this may be null if there is no next page...
                currentPageReference = currentPage.getNextPageReference();
            } finally {
                // Release the trailing lock, if there is one...
                if (trailingLock != null) {
                    trailingLock.unlock();
                }
            }
        }
        // Release the current page lock which was acquired, because the current page was a dead-end...
        if (currentPageLock != null) {
            currentPageLock.unlock();
        }
        return new ScanResult<>(previousPageReference, previousPageLock, indexOfFirstItemInPreviousPage, null, null, 0);
    }

    /** Result object returned by {@link #findPage(int, boolean)}. */
    private static class ScanResult<E> {

        final PageReference<E> previousPageReference;
        final Lock previousPageLock;
        final int indexOfFirstItemInPreviousPage;

        final PageReference<E> currentPageReference;
        final Lock currentPageLock;
        final int indexOfFirstItemInCurrentPage;

        ScanResult(PageReference<E> previousPageReference,
                   Lock previousPageLock,
                   int indexOfFirstItemInPreviousPage,
                   PageReference<E> currentPageReference,
                   Lock currentPageLock,
                   int indexOfFirstItemInCurrentPage) {
            this.previousPageReference = previousPageReference;
            this.previousPageLock = previousPageLock;
            this.indexOfFirstItemInPreviousPage = indexOfFirstItemInPreviousPage;
            this.currentPageReference = currentPageReference;
            this.currentPageLock = currentPageLock;
            this.indexOfFirstItemInCurrentPage = indexOfFirstItemInCurrentPage;
        }

        void releaseLocks() {
            if (currentPageLock != null) {
                currentPageLock.unlock();
            }
            if (previousPageLock != null) {
                previousPageLock.unlock();
            }
        }
    }


    // ===== Methods to access the PageFactory =====

    @Override
    public PageFactory<E> getPageFactory() {
        return pageFactory;
    }

    // ===== Methods to generate string representations of the paged list =====

    /**
     * Returns a standard string representation of the paged list.
     * <p>
     * Example output: <code>[a, b, c, d, e, f]</code>
     * </p>
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        final int size = size();
        for (int i = 0; i < size; i++) {
            E element = get(i);
            sb.append(element);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Returns a structural string representation of the paged list
     * which shows the layout of its pages and the elements in each page.
     * <p>
     * Example output: <code>[[a, b, c, d], [e, f]]</code>
     * </p>
     */
    public String toStructuralString() {
        PageReference<?> currentPageReference = firstPageReference;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        while (currentPageReference != null) {
            Page<?> currentPage = currentPageReference.getPage();
            sb.append(currentPage.toString());
            currentPageReference = currentPage.getNextPageReference();
            if (currentPageReference != null) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

}
