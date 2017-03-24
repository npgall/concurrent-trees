package com.npgall.pagedlist;

import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.TestStringListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.*;

/**
 * Runs several thousand tests from guava-testlib on the paged list using HeapPageFactory
 * with various page sizes, to validate compliance with the JDK List API.
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public class ConcurrentHeapPagedListJDKComplianceTest extends TestCase {

    private static final int[] pageSizesToTest = new int[] {1, 2, 3, 4, 5};

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
        // Add test suite for each of the 5 page sizes, using HeapPages...
        for (int pageSize : pageSizesToTest) {
            suite.addTest(createHeapPagedListTest(pageSize));
        }
        return suite;
    }

    private static TestSuite createHeapPagedListTest(final int pageSize) {
        return ListTestSuiteBuilder.using(heapPagedListGenerator(pageSize))
                .withFeatures(CollectionSize.ANY, CollectionFeature.ALLOWS_NULL_VALUES, ListFeature.GENERAL_PURPOSE)
                .named("page_size_" + pageSize)
                .createTestSuite();
    }

    private static TestStringListGenerator heapPagedListGenerator(final int pageSize) {
        return new TestStringListGenerator() {
            @Override protected List<String> create(String[] elements) {
                List<String> jdkCompatiblePagedList = new ConcurrentHeapPagedList<>(pageSize);
                Collections.addAll(jdkCompatiblePagedList, elements); // .. calls pagedList.add() repeatedly
                return jdkCompatiblePagedList;
            }
        };
    }

}