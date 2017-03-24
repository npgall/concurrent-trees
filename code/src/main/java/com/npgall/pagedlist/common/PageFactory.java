package com.npgall.pagedlist.common;

/**
 * Creates {@link Page} objects on-demand.
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public interface PageFactory<E> {

    /**
     * Returns the configured page size for the factory (typically configured via its constructor).
     * The factory will create Pages which contain at most <i>page size</i> elements.
     *
     * @return The configured page size.
     */
    int getPageSize();

    /**
     * Indicates if the Page implementations created by this factory are doubly-linked.
     * <p>
     *     If pages are singly-linked, only next page references are supported.
     *     If pages are doubly-linked, previous page references are supported as well.
     * </p>
     * <p>
     *     This method can be used to determine if the methods {@link Page#getPreviousPageReference()} or
     *     {@link Page#setPreviousPageReference(PageReference)} are supported by Pages created by this factory.
     * </p>
     * @return true if this factory creates Pages which support previous page references, false if it does not.
     */
    boolean isDoublyLinked();

    /**
     * Creates the first page in a list.
     * <p>
     *     This is the same as {@link #createPage()} except that some subclass implementations of {@link PageFactory}
     *     may implement this to return a reference to an already-existing first page.
     * </p>
     */
    Page<E> createFirstPage();

    /**
     * Creates a new page in a new list.
     */
    Page<E> createPage();

    /**
     * Creates an object which can later provide access to the given page.
     * <p>
     *     Depending on the implementation, the reference object might contain an object reference to the page itself,
     *     or it may contain enough details to load the page later.
     * </p>
     * @param page The page for which the reference should be created
     * @return an object which can later provide access to the given page
     */
    PageReference<E> getPageReference(Page<E> page);

}
