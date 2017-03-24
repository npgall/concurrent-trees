# ConcurrentPagedList data structure

This package provides a `ConcurrentPagedList` data structure, which validates disk paging algorithms 
which can be integrated into the tree-based data structures in this project in future. `ConcurrentPagedList` is a
hybrid between an `ArrayList` and a `LinkedList`. It can be stored in heap memory or on disk, and it supports
concurrent reads and writes.

The elements added to the list are grouped in `Page` objects.
A Page encapsulates of a number of sequential elements from the larger list.

The concept is similar to an _unrolled linked list_ [4], except that the list data structure is agnostic as to the
internal representation of Page objects, and each page can be loaded and saved independently (that is, _paged_ in and
out of memory on-the-fly).

The actual representation used by the list is determined by a `PageFactory` supplied to its constructor.
The PageFactory concept is similar to the `NodeFactory` concept used elsewhere in _concurrent-trees_ [5].

Two implementations of the PageFactory interface are provided: `HeapPageFactory` and `DiskPageFactory`.

The HeapPageFactory creates `HeapPage` objects, which are straightforward Java objects which encapsulate an array of
elements, a currentSize which is the number of slots within the array which are actually occupied in the Page, and a
reference to a next page (if any).

The DiskPageFactory creates `DiskPage` objects, which have the same in-memory structure, but which also persist the
encapsulated data to disk. Each DiskPage is assigned a UUID, and is stored in a single file, named after the UUID.

## DiskPage file format
The on-disk format of a page file is as follows:
```
[this page UUID (16 bytes)]
[next page UUID (16 bytes, all zeroes if none)]
([length of element to follow (4-byte int)][serialized element data])*
```
That is, a page on disk is comprised of a header of 2 UUIDs, followed by zero or more pairs of _element length_ +
_element data_ sections.

The element data sections are stored last, to optimize the write path for the common use case where a page is populated
sequentially by adding elements to it one by one. In this case, new elements can simply be appended to the existing
page file on disk, without having to rewrite the entire page.

The file format also allows the next page UUID to be updated in-place, without rewriting any other data in the page
file. This is useful when new pages are added to the list, or when pages in the middle of the list must be split.

However, if any existing element is modified in-place, then the entire page file will be rewritten. This overhead
incurred when elements in the middle of the list are modified and their containing page must be rewritten, can be
mitigated by tuning the page size.

## Memory management

When the DiskPageFactory is used, DiskPage objects will be loaded into memory on-demand, and will be cached in memory
using `SoftReference`s (this is a fairly simple approach, to demonstrate paging behaviour only).

The garbage collector may remove these objects from memory at any time (unless they are being accessed by live threads).
The logic to _page in_ and cache DiskPage objects on-demand, can be found in the `DiskPageReference` class.

When pages are modified, the changes are written to the memory representation and to the disk representation
immediately; so this mechanism behaves like a _write-through_ cache.

This mechanism allows arbitrarily large lists to be traversed without loading more than two pages into memory at a time.

## Concurrency support
This list uses a _windowed locking algorithm_ when traversing the pages, whereby only the current page being scanned
and the previous page scanned, will be locked at any one time. This is inspired by the papers below [1], [2], [3] -
although the internal structure of pages within this package differs.

The algorithm in this package will lock pairs of pages as it traverses left-to-right, and it will unlock pages to the
left of the pair as the scan proceeds. The algorithm uses the `ReadWriteUpdateLock` from the _concurrent-locks_ [5] 
library to control locking.

Unlike the standard `ReadWriteLock` in the JDK, this avoids the need to acquire _write_ locks during the scan entirely.
Only a _read_ lock or an _update_ lock will be acquired during the scan, even if the purpose of the scan is to locate
pages for a write operation.

As the _read_ and _update_ locks do not block reading threads, concurrent reads from the pages being scanned can
proceed in parallel, and concurrent writes to different pages can proceed in parallel.

## Time complexity

The _page size_ (max number of elements which will be stored in each page) is configurable, which allows the time
complexity of operations on the list to be tuned.

 * If page size is one, this data structure will behave like a linked list (albeit optionally stored on disk).
   Time complexity to access an element will be O(n).
 * If page size is greater than the number of elements in the list, then this list will behave like an array.
   Time complexity to access an element will be O(1).
 * If page size is set somewhere in the middle, time complexity to access an element will be O(n/pageSize).
 
Setting a page size somewhere in the middle allows the tradeoffs between the following to be tuned:

 * Random access performance
 * Sequential performance
 * Compactness
 * Lock contention
 * Mid-list insertion performance
 * The overhead to extend the size of the list

## Compliance with JDK List API

The `ConcurrentPagedList` data structure itself, has no dependencies on any JDK data structures, 
and it does not implement the JDK `List` interface.
 
All of the operations discussed above, are implemented directly by the data structure.

However, two additional adaptor classes are also provided, which implement the `java.util.List`
interface on top of the `ConcurrentPagedList` data structure:

 * `ConcurrentHeapPagedList` is an implementation of `java.util.List` backed by the `ConcurrentPagedList`
   data structure, configured to store Page objects on the Java heap.
 * `ConcurrentDiskPagedList` is an implementation of `java.util.List` backed by the `ConcurrentPagedList`
   data structure, configured to store Page objects on disk, in a configurable `storageDirectory` supplied
   to its constructor.


## Test coverage

Functional tests can be found in `ConcurrentPagedListFunctionalTest`.

The package also uses _guava-testlib_ [7] to run several thousand additional tests on the paged list,
with `HeapPageFactory` and `DiskPageFactory`, and with various page sizes,
to validate compliance with the JDK List API.

These additional tests can be found in `ConcurrentHeapPagedListJDKComplianceTest`
and `ConcurrentDiskPagedListJDKComplianceTest`.


# References


1. Kenneth Platz, Neeraj Mittal, and S. Venkatesan, 2014.
    * Practical Concurrent Unrolled Linked Lists Using Lazy Synchronization. 
    * http://www.utdallas.edu/~kxp101120/UTDCS-09-14.pdf
2. Braginsky, A. and Petrank, E., 2010. 
    * Locality-conscious lock-free linked lists. 
    * http://www.cs.technion.ac.il/~erez/Papers/lf-linked-list-full.pdf
3. Harris, T., 2001.
    * A Pragmatic Implementation of Non-Blocking Linked-Lists.
    * https://www.cl.cam.ac.uk/research/srg/netos/papers/2001-caslists.pdf
4. Unrolled Linked List
    * https://en.wikipedia.org/wiki/Unrolled_linked_list
5. Niall Gallagher, 2012-2017.
    * Concurrent-Trees
    * Mechanism to decouple the internal structure of nodes from the algorithms which traverse them.
    * https://github.com/npgall/concurrent-trees/blob/master/documentation/NodeFactoryAndMemoryUsage.md
6. Niall Gallagher, 2012-2017.
    * Concurrent-Locks, providing `ReadWriteUpdateLock`
    * https://github.com/npgall/concurrent-locks
7. Guava-testlib
    * ListTestSuiteBuilder
    * https://github.com/google/guava/blob/master/guava-testlib/src/com/google/common/collect/testing/ListTestSuiteBuilder.java