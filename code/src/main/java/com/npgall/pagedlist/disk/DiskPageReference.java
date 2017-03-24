package com.npgall.pagedlist.disk;

import com.npgall.pagedlist.common.Page;
import com.npgall.pagedlist.common.PageReference;

import java.lang.ref.SoftReference;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a reference to a {@link DiskPage}.
 * <p>
 *     To avoid reloading frequently accessed pages, this implementation caches the referenced page (if loaded) in
 *     a {@link SoftReference}. If the page is garbage collected, it will automatically reload it from disk on-demand.
 * </p>
 * @author Niall Gallagher (niall@npgall.com)
 */
public class DiskPageReference<E> implements PageReference<E> {

    private final DiskPageFactory<E> diskPageFactory;
    private final UUID pageUuid;
    private final AtomicReference<SoftReference<Page<E>>> pageCache = new AtomicReference<>();

    /**
     * Creates a new DiskPageReference and caches the target page.
     */
    public DiskPageReference(DiskPageFactory<E> diskPageFactory, UUID pageUuid, Page<E> page) {
        this.diskPageFactory = diskPageFactory;
        this.pageUuid = pageUuid;
        this.pageCache.set(new SoftReference<>(page));
    }

    public UUID getPageUuid() {
        return pageUuid;
    }

    @Override
    public Page<E> getPage() {
        while (true) {
            SoftReference<Page<E>> cachedReference = pageCache.get();
            if (cachedReference != null) {
                Page<E> cachedPage = cachedReference.get();
                if (cachedPage != null) {
                    return cachedPage; // ..page was cached on the heap (it was not garbage collected since last accessed)
                }
            }
            // Page was not cached, load it...
            Page<E> loadedPage = diskPageFactory.loadPage(getPageUuid());

            // In the meantime, another thread might also have tried to load the page.
            // We use the AtomicReference to decide which thread "wins" this data race, using compare and set...
            SoftReference<Page<E>> newCachedReference = new SoftReference<>(loadedPage);
            boolean successful = pageCache.compareAndSet(cachedReference, newCachedReference);
            if (successful) {
                return loadedPage; // this thread won the data race.
            }
            // Else, the other thread won the data race,
            // so we restart the loop to retrieve its cached version of the page.
        }
    }
}
