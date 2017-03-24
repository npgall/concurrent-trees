package com.npgall.pagedlist;

import com.npgall.pagedlist.common.JDKCompatibleConcurrentPagedList;
import com.npgall.pagedlist.disk.DiskPageFactory;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * An implementation of the JDK {@link List} interface on top of the {@link ConcurrentPagedList} data structure,
 * configured to store the list on disk.
 * <p>
 *     An existing list which is stored on disk can be accessed by supplying its UUID to the constructor.
 * </p>
 *
 * @author Niall Gallagher (niall@npgall.com)
 */
public class ConcurrentDiskPagedList<E> extends JDKCompatibleConcurrentPagedList<E> {

    /**
     * Creates a new paged list with a default page size, which will store pages on disk in the given directory.
     * @param storageDirectory The directory in which pages should be stored
     */
    public ConcurrentDiskPagedList(File storageDirectory) {
        this(16, storageDirectory);
    }

    /**
     * Creates a new paged list with the given page size, which will store pages on disk in the given directory.
     * @param pageSize The number of elements to be stored in each page
     * @param storageDirectory The directory in which pages should be stored
     */
    public ConcurrentDiskPagedList(int pageSize, File storageDirectory) {
        super(new DiskPageFactory<>(pageSize, storageDirectory));
    }

    /**
     * Creates a new paged list <b>OR opens an existing list from disk</b> with the given UUID, with the given page
     * size, which will store pages on disk in the given directory.
     *
     * @param pageSize The number of elements to be stored in each page
     * @param storageDirectory The directory in which pages should be stored
     */
    public ConcurrentDiskPagedList(int pageSize, File storageDirectory, UUID listUuid) {
        super(new DiskPageFactory<>(pageSize, storageDirectory, listUuid));
    }

    /**
     * Constructor used by unit tests only.
     * @param pageFactory page factory to use
     */
    protected ConcurrentDiskPagedList(DiskPageFactory<E> pageFactory) {
        super(pageFactory);
    }

    /**
     * @return The UUID of the list
     */
    public UUID getListUuid() {
        return ((DiskPageFactory<E>)getPageFactory()).getListUuid();
    }

    /**
     * @return The directory in which pages are stored
     */
    public File getStorageDirectory() {
        return ((DiskPageFactory<E>)getPageFactory()).getStorageDirectory();
    }

}
