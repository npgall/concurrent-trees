package com.npgall.pagedlist;

import com.npgall.pagedlist.heap.HeapPageFactory;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Acceptance and functional tests for the ConcurrentPagedList.
 *
 * Note that thousands of additonal tests for edge cases, to validate compliance with the JDK List API,
 * are implemented by {@link ConcurrentHeapPagedListJDKComplianceTest} and {@link ConcurrentDiskPagedListJDKComplianceTest}.
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public class ConcurrentPagedListFunctionalTest {

    // ===== Tests for how pages are allocated and deallocated within the list =====

    @Test
    public void testPageAllocation() {
        ConcurrentPagedList<String> pagedList = new ConcurrentPagedList<>(new HeapPageFactory<>(4));

        pagedList.add("a");
        assertEquals("[[a]]", pagedList.toStructuralString());

        pagedList.add("b");
        assertEquals("[[a, b]]", pagedList.toStructuralString());

        pagedList.add("c");
        assertEquals("[[a, b, c]]", pagedList.toStructuralString());

        pagedList.add("d");
        assertEquals("[[a, b, c, d]]", pagedList.toStructuralString());

        pagedList.add("e");
        assertEquals("[[a, b, c, d], [e]]", pagedList.toStructuralString()); // page appended

        pagedList.add(2, "f");
        assertEquals("[[a, b], [f, c, d], [e]]", pagedList.toStructuralString()); // page split and rebalanced
    }

    @Test
    public void testPageDeallocation() {
        ConcurrentPagedList<String> pagedList = new ConcurrentPagedList<>(new HeapPageFactory<>(4));
        Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i").forEach(pagedList::add);
        assertEquals("[[a, b, c, d], [e, f, g, h], [i]]", pagedList.toStructuralString());

        pagedList.remove(3);
        assertEquals("[[a, b, c], [e, f, g, h], [i]]", pagedList.toStructuralString());

        pagedList.remove(3);
        assertEquals("[[a, b, c], [f, g, h], [i]]", pagedList.toStructuralString());

        pagedList.remove(1);
        pagedList.remove(1);
        assertEquals("[[a], [f, g, h], [i]]", pagedList.toStructuralString());

        pagedList.remove(2);
        pagedList.remove(2);
        assertEquals("[[a], [f], [i]]", pagedList.toStructuralString());

        pagedList.remove(1);
        assertEquals("[[a], [i]]", pagedList.toStructuralString()); // empty page deleted, sibling pages merged

        pagedList.remove(1);
        assertEquals("[[a]]", pagedList.toStructuralString()); // empty page deleted

        pagedList.remove(0);
        assertEquals("[[]]", pagedList.toStructuralString()); // first page not deleted, can be empty
    }

}