package com.npgall.pagedlist.disk;

import com.npgall.pagedlist.common.AbstractPage;
import com.npgall.pagedlist.common.Page;
import com.npgall.pagedlist.common.PageReference;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An implementation of {@link Page} which is stored on disk.
 * <p>
 *     Each disk page is stored in a single file, named after the UUID which identifies the page.
 * </p>
 * <p>
 *     The on-disk format of a page is as follows:
 *     <pre>
 *     [this page UUID (16 bytes)]
 *     [next page UUID (16 bytes, all zeroes if none)]
 *     ([length of element to follow (4-byte int)][element data])*
 *     </pre>
 *     That is, a page on disk is comprised of a header of 2 UUIDs,
 *     followed by zero or more pairs of element length + element data sections.
 * </p>
 * @author Niall Gallagher (niall@npgall.com)
 */
public class DiskPage<E> extends AbstractPage<E> {

    private static final UUID EMPTY_UUID = new UUID(0, 0);

    /*
     * Note: the current implementation below attempts to optimize the write path for the common use case
     * where a page is populated sequentially by adding elements to it one by one. Data for each element
     * is appended to the file on disk, without rewriting elements which were written previously.
     *
     * The file format also allows the next page UUID to be updated in-place, without
     * rewriting any other data in the page file.
     *
     * However, due to time constraints, the current implementation supports, but is not optimized for
     * the cases where existing entries within the page are modified. The implementation is less efficient
     * for these cases, in that if an existing entry in the page is modified, the entire page will be
     * rewritten instead.
     *
     * Additional notes:
     * - all integers are saved in big-endian format.
     * - Java serialization is used instead of something more efficient like Kryo or protobuf, for brevity.
     */

    private final DiskPageFactory<E> pageFactory;
    private final UUID pageUuid;
    private final File pageDataFile;
    private final Lock fileLock = new ReentrantLock();
    private volatile UUID nextPageUuid = EMPTY_UUID;

    public DiskPage(DiskPageFactory<E> pageFactory, UUID pageUuid, File pageDataFile) {
        super(pageFactory.getPageSize());
        this.pageFactory = pageFactory;
        this.pageUuid = pageUuid;
        this.pageDataFile = pageDataFile;
        createOrLoadPageFile();
    }

    public UUID getPageUuid() {
        return pageUuid;
    }

    // ===== Methods to load and save the page to disk =====

    private void createOrLoadPageFile() {
        fileLock.lock();
        try {
            if (pageDataFile.exists()) {
                loadPage();
            } else {
                savePage();
            }
        }
        finally {
            fileLock.unlock();
        }
    }

    private void loadPage() {
        fileLock.lock();
        try (DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(pageDataFile)))) {
            // We should always be able to read at least the 2 UUIDs, so throw exceptions if this is not the case...
            ensureSkipped(16, input.skipBytes(16)); // Skip 16 bytes for the page's own UUID
            this.nextPageUuid = new UUID(input.readLong(), input.readLong()); // reads 16 bytes
            if (this.nextPageUuid.equals(EMPTY_UUID)) {
                this.nextPageUuid = EMPTY_UUID; // ..store reference to the EMPTY_UUID constant
            }
            else {
                this.nextPageReference = new DiskPageReference<>(pageFactory, nextPageUuid, null);
            }

            // Next, read zero or more elements until we reach the end of the file...
            while (true) {
                try {
                    // We need to read the length of the serialized element to follow.
                    // However, note this is not required when Java serialization decodes the element as the serialized
                    // form contains length and information. However this is required if we are to support random
                    // read access without deserializing preceding elements.
                    int elementLength = input.readInt();
                }
                catch (EOFException e) {
                    // If we get EOFException reading the integer length of the next element to follow,
                    // for *brevity* we assume there is no next element to read.
                    // This is not robust in the case the integer itself was partially corrupt.
                    break;
                }
                ObjectInputStream deserializer = new ObjectInputStream(input);
                @SuppressWarnings("unchecked")
                E element = (E) deserializer.readObject();
                super.add(currentSize, element);
            }

        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to load page file: " + pageDataFile, e);
        }
        finally {
            fileLock.unlock();
        }
    }

    private void savePage() {
        fileLock.lock();
        try (DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(pageDataFile)))){
            // Write this page UUID...
            output.writeLong(pageUuid.getMostSignificantBits());
            output.writeLong(pageUuid.getLeastSignificantBits());
            // Write next page UUID...
            output.writeLong(nextPageUuid.getMostSignificantBits());
            output.writeLong(nextPageUuid.getLeastSignificantBits());

            for (int i = 0; i < currentSize; i++) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream serializer = new ObjectOutputStream(baos);
                serializer.writeObject(elements[i]);
                serializer.flush();
                serializer.close();
                byte[] serializedElement = baos.toByteArray();

                output.writeInt(serializedElement.length);
                output.write(serializedElement);
            }
            output.flush();
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to save page file: " + pageDataFile, e);
        }
        finally {
            fileLock.unlock();
        }
    }

    private void saveNextPageUuid() {
        fileLock.lock();
        try (RandomAccessFile file = new RandomAccessFile(pageDataFile, "rw")) {
            file.seek(16);
            // Write next page UUID...
            file.writeLong(nextPageUuid.getMostSignificantBits());
            file.writeLong(nextPageUuid.getLeastSignificantBits());
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to write next page UUID to page file: " + pageDataFile, e);
        }
        finally {
            fileLock.unlock();
        }
    }

    private void saveLastElement() {
        fileLock.lock();
        // Open the file for append...
        try (DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(pageDataFile, true)))){

            Object lastElement = elements[currentSize -1];

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream serializer = new ObjectOutputStream(baos);
            serializer.writeObject(lastElement);
            serializer.flush();
            serializer.close();
            byte[] serializedElement = baos.toByteArray();

            output.writeInt(serializedElement.length);
            output.write(serializedElement);

            output.flush();
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to save last element to page file: " + pageDataFile, e);
        }
        finally {
            fileLock.unlock();
        }
    }

    private static void ensureSkipped(int expectedBytes, int actualBytes) {
        if (expectedBytes != actualBytes) {
            throw new IllegalStateException("Failed to find a required number of bytes for a read operation, required: " + expectedBytes + ", found: " + actualBytes);
        }
    }

    @Override
    public void setNextPageReference(PageReference<E> nextPageReference) {
        fileLock.lock();
        try {
            super.setNextPageReference(nextPageReference);
            this.nextPageUuid = this.nextPageReference == null ? EMPTY_UUID : ((DiskPageReference<E>)this.nextPageReference).getPageUuid();
            saveNextPageUuid();
        }
        finally {
            fileLock.unlock();
        }
    }

    @Override
    public void add(int indexWithinPage, E element) {
        fileLock.lock();
        try {
            boolean isAppend = indexWithinPage == currentSize;
            super.add(indexWithinPage, element);

            if (isAppend) {
                saveLastElement();
            }
            else {
                savePage();
            }
        }
        finally {
            fileLock.unlock();
        }
    }

    @Override
    public E remove(int indexWithinPage) {
        fileLock.lock();
        try {
            E previous = super.remove(indexWithinPage);
            savePage();
            return previous;
        }
        finally {
            fileLock.unlock();
        }
    }

    @Override
    public E set(int indexWithinPage, E element) {
        fileLock.lock();
        try {
            E previous = super.set(indexWithinPage, element);
            savePage();
            return previous;
        }
        finally {
            fileLock.unlock();
        }
    }

    @Override
    public void rebalance(int pageSize, Page<E> otherPage) {
        fileLock.lock();
        try {
            super.rebalance(pageSize, otherPage);
            this.savePage();
            ((DiskPage)otherPage).savePage();
        }
        finally {
            fileLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     * Deletes the page file from disk.
     */
    @Override
    public void delete() {
        if (!pageDataFile.delete()) {
            throw new IllegalStateException("Failed to delete data file for unlinked page: " + pageDataFile);
        }
    }
}
