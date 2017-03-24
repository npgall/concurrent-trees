package com.npgall.pagedlist;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link ConcurrentDiskPagedList}.
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public class ConcurrentHeapPagedListTest {

    @Test
    public void testPageFactoryInitialization() {
        ConcurrentHeapPagedList<String> pagedList = new ConcurrentHeapPagedList<>(4);
        assertEquals(4, pagedList.getPageFactory().getPageSize());
    }
}