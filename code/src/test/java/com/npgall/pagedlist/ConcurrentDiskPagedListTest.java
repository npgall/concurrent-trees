package com.npgall.pagedlist;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link ConcurrentDiskPagedList}.
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public class ConcurrentDiskPagedListTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testPageFactoryInitialization() {
        File storageDirectory = createTemporaryStorageDirectory();

        ConcurrentDiskPagedList<String> pagedList;

        pagedList = new ConcurrentDiskPagedList<>(storageDirectory);
        assertEquals(storageDirectory, pagedList.getStorageDirectory());

        pagedList = new ConcurrentDiskPagedList<>(2, storageDirectory);
        assertEquals(storageDirectory, pagedList.getStorageDirectory());
        assertEquals(2, pagedList.getPageFactory().getPageSize());

        UUID someUuid = UUID.randomUUID();
        pagedList = new ConcurrentDiskPagedList<>(2, storageDirectory, someUuid);
        assertEquals(storageDirectory, pagedList.getStorageDirectory());
        assertEquals(2, pagedList.getPageFactory().getPageSize());
        assertEquals(someUuid, pagedList.getListUuid());
    }

    @Test
    public void testListPersistence() {
        File storageDirectory = createTemporaryStorageDirectory();
        UUID listUuid = UUID.randomUUID();

        // Store a list on disk...
        ConcurrentDiskPagedList<String> originalPagedList = new ConcurrentDiskPagedList<>(4, storageDirectory, listUuid);
        Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h").forEach(originalPagedList::add);
        assertEquals("[[a, b, c, d], [e, f, g, h]]", originalPagedList.toStructuralString());

        // Reload the same list, and assert the contents and internal structure are the same...
        ConcurrentDiskPagedList<String> reloadedPagedList = new ConcurrentDiskPagedList<>(4, storageDirectory, listUuid);
        assertEquals(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"), reloadedPagedList);
        assertEquals("[[a, b, c, d], [e, f, g, h]]", reloadedPagedList.toStructuralString());
    }


    // ===== Boilerplate methods to support the tests =====

    private File createTemporaryStorageDirectory() {
        try {
            return folder.newFolder();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}