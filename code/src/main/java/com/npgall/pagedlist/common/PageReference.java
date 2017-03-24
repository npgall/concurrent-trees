package com.npgall.pagedlist.common;

/**
 * A reference to a {@link Page} object, where the page may be in-memory, on-disk, or on a remote machine; depending on
 * the implementation of the reference.
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public interface PageReference<E> {

    /**
     * Returns the page which is the target of this reference, automatically loading it if necessary.
     * @return the page which is the target of this reference
     */
    Page<E> getPage();
}
