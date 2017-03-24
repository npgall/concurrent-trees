package com.npgall.pagedlist.disk;

import com.npgall.pagedlist.common.Page;
import com.npgall.pagedlist.common.PageFactory;

import java.io.File;
import java.util.UUID;

/**
 * Creates {@link DiskPage} objects on demand, which will be persisted to the configured storage directory.
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public class DiskPageFactory<E> implements PageFactory<E> {

    private final int pageSize;
    private final File storageDirectory;
    private final UUID listUuid; // UUID of the first page file in the list

    /**
     * Creates a new list in the given storage directory.
     *
     * @param pageSize The page size of the list
     * @param storageDirectory The directory in which page files belonging to the list will be created
     */
    public DiskPageFactory(int pageSize, File storageDirectory) {
        this(pageSize, storageDirectory, UUID.randomUUID());
    }

    /**
     * Opens an existing list or creates a new one with the given UUID, in the given storage directory.
     *
     * @param pageSize The page size of the list
     * @param storageDirectory The directory in which page files belonging to the list will be created
     * @param listUuid The UUID of the list to use (it may optionally refer to a list already on disk,
     * in which case the existing list will be opened)
     */
    public DiskPageFactory(int pageSize, File storageDirectory, UUID listUuid) {
        this.pageSize = pageSize;
        this.storageDirectory = storageDirectory;
        this.listUuid = listUuid;
        if (!storageDirectory.isDirectory() && !storageDirectory.mkdirs()) {
            throw new IllegalStateException("Could not access or create storage directory for paged list: " + storageDirectory);
        }
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    public File getStorageDirectory() {
        return storageDirectory;
    }

    public UUID getListUuid() {
        return listUuid;
    }

    @Override
    public boolean isDoublyLinked() {
        return false;
    }

    @Override
    public DiskPage<E> createFirstPage() {
        return doCreatePage(listUuid);
    }

    @Override
    public DiskPage<E> createPage() {
        return doCreatePage(UUID.randomUUID());
    }

    private DiskPage<E> doCreatePage(UUID pageUuid) {
        File pageDataFile = new File(storageDirectory, pageUuid + ".dat");
        onPageDataFileCreated(pageDataFile);
        return new DiskPage<>(this, pageUuid, pageDataFile);
    }

    public Page<E> loadPage(UUID pageUuid) {
        File pageDataFile = new File(storageDirectory, pageUuid + ".dat");
        return new DiskPage<>(this, pageUuid, pageDataFile);
    }

        @Override
    public DiskPageReference<E> getPageReference(Page<E> page) {
        DiskPage<E> diskPage = (DiskPage<E>)page;
        return new DiskPageReference<>(this, diskPage.getPageUuid(), page);
    }

    // ===== Hook method for testing... =====

    /**
     * A hook method to allow tests to clean up page files which are created by Guava-testlib automated test suites.
     */
    protected void onPageDataFileCreated(File pageDataFile) {
        // No-op by default
    }
}
