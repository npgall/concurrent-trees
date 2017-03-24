package com.npgall.pagedlist;

import com.npgall.pagedlist.common.PageFactory;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Interface of the {@link ConcurrentPagedList}.
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public interface PagedList<E> {

    // ===== Methods to modify the contents of the list =====

    /**
     * Appends the specified element to the end of this list.
     * @param element element to be appended to this list
     */
    void add(E element);

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any subsequent elements to the right
     * (adds one to their indices).
     *
     * @param index index in the list at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
     */
    void add(int index, E element);

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     *
     * @param index index in the list of the element to remove
     * @return the element previously at the specified position, or null if no such element was stored at the position
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    E remove(int index);

    /**
     * Replaces the element at the specified position in this list with the specified element.
     *
     * @param index index in the list of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position, or null if no such element was stored at the position
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    E set(int index, E element);


    // ===== Methods to access the contents of the list =====

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index in the list of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    E get(int index) throws IndexOutOfBoundsException;

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    int size();

    // ===== Methods to access the PageFactory =====

    /**
     * Returns the {@link PageFactory} used by this list.
     * @return the {@link PageFactory} used by this list
     */
    PageFactory<E> getPageFactory();

}
