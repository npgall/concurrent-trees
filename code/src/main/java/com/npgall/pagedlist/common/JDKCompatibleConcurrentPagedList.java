package com.npgall.pagedlist.common;

import com.npgall.pagedlist.ConcurrentPagedList;

import java.util.AbstractList;

/**
 * Implements the JDK {@link java.util.List} interface on top of the {@link ConcurrentPagedList} data structure.
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public abstract class JDKCompatibleConcurrentPagedList<E> extends AbstractList<E> {

    private final ConcurrentPagedList<E> concurrentPagedList;

    protected JDKCompatibleConcurrentPagedList(PageFactory<E> pageFactory) {
        this.concurrentPagedList = new ConcurrentPagedList<>(pageFactory);
    }

    @Override
    public E get(int index) {
        return concurrentPagedList.get(index);
    }

    @Override
    public int size() {
        return concurrentPagedList.size();
    }

    @Override
    public E set(int index, E element) {
        return concurrentPagedList.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        concurrentPagedList.add(index, element);
    }

    @Override
    public E remove(int index) {
        return concurrentPagedList.remove(index);
    }

    public PageFactory<E> getPageFactory() {
        return concurrentPagedList.getPageFactory();
    }

    @Override
    public String toString() {
        return concurrentPagedList.toString();
    }

    public String toStructuralString() {
        return concurrentPagedList.toStructuralString();
    }
}
