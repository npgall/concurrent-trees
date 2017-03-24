package com.npgall.pagedlist;

import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.TestStringListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;
import com.npgall.pagedlist.disk.DiskPageFactory;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.util.*;

/**
 * Runs several thousand tests from guava-testlib on the paged list using DiskPageFactory
 * with various page sizes, to validate compliance with the JDK List API.
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public class ConcurrentDiskPagedListJDKComplianceTest extends TestCase {

    private static final int[] pageSizesToTest = new int[] {1, 2, 3, 4, 5};

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
        // Add test suite for each of the 5 page sizes, using DiskPages...
        for (int pageSize : pageSizesToTest) {
            suite.addTest(createDiskPagedListTest(pageSize));
        }
        return suite;
    }

    private static TestSuite createDiskPagedListTest(final int pageSize) {
        File temporaryStorageDirectory = createTemporaryStorageDirectory();

        // Prepare a TearDown task which will delete the page files created on disk by these tests...
        Set<File> pageFilesCreated = new HashSet<>();
        final Runnable deletePageFiles = new Runnable() {
            @Override
            public void run() {
                for (Iterator<File> iterator = pageFilesCreated.iterator(); iterator.hasNext(); ) {
                    File pageFile = iterator.next();
                    if (pageFile.exists()) {
                        if (!pageFile.delete()) {
                            throw new IllegalStateException("Failed to delete page file created by tests in tear down: " + pageFile);
                        }
                    }
                    iterator.remove();
                }
                if (!temporaryStorageDirectory.delete()) {
                    throw new IllegalStateException("Failed to delete temporary storage directory created by tests: " + temporaryStorageDirectory);
                }
            }
        };
        // Create the test suite, configured to run the TearDown task above...
        return ListTestSuiteBuilder.using(diskPagedListGenerator(pageSize, temporaryStorageDirectory, pageFilesCreated))
                .withFeatures(CollectionSize.ANY, CollectionFeature.ALLOWS_NULL_VALUES, ListFeature.GENERAL_PURPOSE)
                .named("page_size_" + pageSize)
                .withTearDown(deletePageFiles)
                .createTestSuite();
    }

    private static TestStringListGenerator diskPagedListGenerator(final int pageSize, File storageDirectory, Collection<File> pageFilesCreated) {
        return new TestStringListGenerator() {
            @Override protected List<String> create(String[] elements) {
                // Subclass DiskPageFactory to keep track of page files created...
                DiskPageFactory<String> tearDownSupportingDiskPageFactory = new DiskPageFactory<String>(pageSize, storageDirectory) {
                    @Override
                    protected void onPageDataFileCreated(File pageDataFile) {
                        pageFilesCreated.add(pageDataFile);
                    }
                };
                List<String> jdkCompatiblePagedList = new ConcurrentDiskPagedList<>(tearDownSupportingDiskPageFactory);
                Collections.addAll(jdkCompatiblePagedList, elements); // .. calls pagedList.add() repeatedly
                return jdkCompatiblePagedList;
            }
        };
    }

    private static File createTemporaryStorageDirectory() {
        String tmpDirString = System.getProperty("java.io.tmpdir", "");
        File tmpDir = new File(tmpDirString);
        if (!tmpDir.isDirectory()) {
            throw new IllegalStateException("Cannot access java.io.tmpDir: " + tmpDirString);
        }
        File storageDirectory = new File(tmpDir, "paged_list_" + UUID.randomUUID());
        if (!storageDirectory.isDirectory() && !storageDirectory.mkdir()) {
            throw new IllegalStateException("Cannot create temporary storage directory: " + storageDirectory);
        }
        return storageDirectory;
    }

}