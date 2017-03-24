package com.npgall.pagedlist.common;

import com.googlecode.concurentlocks.ReadWriteUpdateLock;

/**
 * A page is a size-bounded encapsulation of a number of sequential elements from a larger list.
 * <p>
 *     Each page itself has the characteristics of a list. Additionally, each page can store a reference to a
 *     <i>next</i> Page (if any) and a <i>previous</i> Page (if any, optional) within a larger list.
 * </p>
 * <p>
 *     Subclass implementations are free to use any storage representation for the page;
 *     for example both in-memory and disk-based representations are possible.
 *     Additionally, the references to next and previous pages, can refer to in-memory locations (via Java object
 *     references), on-disk locations, or network-accessible locations on remote machines.
 * </p>
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public interface Page<E> {

    // ===== Inter-page access methods =====

    /**
     * Returns a reference to the next page.
     *
     * @return a reference to the next page, or null if there is no next page
     */
    PageReference<E> getNextPageReference();

    /**
     * Stores a reference to the next page, or removes it if null is supplied.
     * @param nextPageReference a reference to the next page, or null to remove an existing reference to the next page
     */
    void setNextPageReference(PageReference<E> nextPageReference);

    /**
     * Returns a reference to the previous page.
     * Throws an exception if the page implementation is not doubly-linked, and so does not store
     * references to previous pages.
     *
     * @return a reference to the previous page, or null if there is no previous page
     * @throws UnsupportedOperationException if the page implementation is not doubly-linked, and so does not store
     * references to previous pages
     * @see PageFactory#isDoublyLinked()
     */
    PageReference<E> getPreviousPageReference();

    /**
     * Stores a reference to the previous page, or removes it if null is supplied.
     * Throws an exception if the page implementation is not doubly-linked, and so does not store
     * references to previous pages.
     *
     * @param previousPageReference a reference to the previous page, or null to remove an existing reference to the
     * previous page
     * @throws UnsupportedOperationException if the page implementation is not doubly-linked, and so does not store
     * references to previous pages
     * @see PageFactory#isDoublyLinked()
     */
    void setPreviousPageReference(PageReference<E> previousPageReference);


    // ===== Page locking methods =====

    ReadWriteUpdateLock getPageLock();


    // ===== Methods to modify the contents of the page =====

    /**
     * Appends the specified element to the end of this page.
     * @param element element to be appended to this page
     * @throws IndexOutOfBoundsException if the page is full (size() == pageSize)
     */
    void add(E element);

    /**
     * Inserts the specified element at the specified position in this page.
     * Shifts the element currently at that position (if any) and any subsequent elements to the right
     * (adds one to their indices).
     *
     * @param indexWithinPage index in the page at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
     */
    void add(int indexWithinPage, E element);

    /**
     * Removes the element at the specified position in this page.
     * Shifts any subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the page.
     *
     * @param indexWithinPage index in the page of the element to remove
     * @return the element previously at the specified position, or null if no such element was stored at the position
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    E remove(int indexWithinPage);

    /**
     * Replaces the element at the specified position in this page with the specified element.
     *
     * @param indexWithinPage index in the page of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position, or null if no such element was stored at the position
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    E set(int indexWithinPage, E element);

    /**
     * Moves elements from the second half of this page, to the first half of the other page.
     * If the page size is odd, the number of elements moved will be rounded up.
     * @param pageSize the page size as configured in the {@link PageFactory}
     * @param otherPage page to which elements will be moved
     */
    void rebalance(int pageSize, Page<E> otherPage);

    // ===== Methods to access the contents of the page =====

    /**
     * Returns the element at the specified position in this page.
     *
     * @param indexWithinPage index in the page of the element to return
     * @return the element at the specified position in this page
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    E get(int indexWithinPage) throws IndexOutOfBoundsException;

    /**
     * Returns the number of elements in this page.
     *
     * @return the number of elements in this page
     */
    int size();

    /**
     * Called when the page has been unlinked from the list, to allow it to free or delete any resources it was using.
     */
    void delete();

}
