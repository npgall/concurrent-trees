Frequently Asked Questions. For various values of "frequently".


## What is Lazy Evaluation, and why return Iterable instead of Collection or Set? ##

Lazy evaluation means deferring computation until it is actually needed, and avoiding computation when it is not needed.

In **Concurrent-Trees 1.0**, the APIs returned `java.util.Collection` or `java.util.Set`, which meant that given a query _"give me all keys which start with FOO"_, the library would literally build an entire `Set` of keys which started with FOO, and then return that fully-populated set to the application:
```
Set<CharSequence> keysStartingWithFoo = tree.getKeysStartingWith("foo");
```

There are a few problems with that approach:
  1. **Latency**: The application cannot access the first key that was found, until the library has first assembled the entire Set of matching keys
  1. **Memory**: The library adds all matching keys to a collection in memory before returning it to the application. If the query matched a large number of keys, then a relatively large amount of memory could be required just to temporarily store results for that query
  1. **CPU**: A lot of the time, the application would not actually be interested in accessing _all_ matching keys, but might just be interested in accessing the first _few_ matching keys. If the application iterated through the first few keys in the collection returned, and then discarded the rest of the results, then it would have been a _waste of CPU time_ for the library to have computed the superfluous keys which the application discarded
  1. Inability to **filter** keys efficiently. The traversal algorithms implemented in the library perform some filtering of results prior to returning them to the application. If the application needed to perform some additional filtering, it would have had to do so in a _second pass_ by iterating through the results again

In **Concurrent-Trees 2.0**, the APIs return `java.lang.Iterable` instead of `java.util.Collection` or `java.util.Set`. Applications which would have simply iterated through the Collection/Set in the past, can access results in exactly the same way as before:
```
Iterable<CharSequence> keysStartingWithFoo = tree.getKeysStartingWith("foo");
for (CharSequence key : keysStartingWithFoo) {
    // Do something with each key...
    System.out.println(key);
}
```

By returning an `Iterable`, the library does not pre-compute _any_ results.
  * It is only when the application starts iterating through the Iterable, that each next element in turn is computed: each step in iteration, drives the traversal algorithms to locate the _next_ result to return
  * Results are not stored, they are returned to the application immediately, reducing latency and memory overhead
  * The application can apply additional filtering/processing of results inside its iteration loop; this will be as efficient as if the traversal algorithm itself performed the filtering
  * The application can stop iterating at any point, and no wasted computations would have been performed

## How can I convert an Iterable object to a Collection, Set or List? ##

`java.lang.Iterable` objects can easily be converted to `java.util.List`, `java.util.Set` etc., using the `Iterables` convenience class provided:

```
public static void main(String[] args) {
    RadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(new DefaultCharArrayNodeFactory());

    tree.put("TEST", 1);
    tree.put("TOAST", 2);
    tree.put("TEAM", 3);

    Iterable<CharSequence> keysStartingWithT    = tree.getKeysStartingWith("T");

    List<CharSequence> listOfKeysStartingWithT  = Iterables.toList  (keysStartingWithT);
    Set<CharSequence> setOfKeysStartingWithT    = Iterables.toSet   (keysStartingWithT);
    String toStringOfKeysStartingWithT          = Iterables.toString(keysStartingWithT);
}
```


## Does the Concurrent Suffix Tree implement Ukkonen's algorithm? ##
[Ukkonen's algorithm](http://en.wikipedia.org/wiki/Ukkonen%27s_algorithm) is a method to construct a suffix tree in _linear-time_, O(_n_) or O(_n_ log(_n_)), by scanning a string from beginning-to-end in a single pass, adding nodes for suffixes in a single pass. It is the latest in a series of suffix tree construction algorithms, the earlier algorithms (Weiner and McCreight) supporting construction in linear time but by scanning from end-to-beginning.

The Concurrent Suffix Tree in this project does _not_ implement Ukkonen's algorithm, because _Ukkonen's algorithm is by definition not thread-safe_. None of these algorithms have been explicitly designed with concurrency in mind. Ukkonen's algorithm specifies that _incomplete suffixes_ must be added to the tree, to be completed as the algorithm progresses. This would certainly allow concurrent reading threads to observe the tree in an inconsistent state.

However... Concurrent Suffix Tree does include _hooks_ for implementing Ukkonen's algorithm, Weiner's algorithm or McCreight's algorithm, or derivatives thereof, in future, with some tradeoffs. There is support internally in Concurrent Suffix Tree to use a read-write lock, such that writing threads can block reading threads, but otherwise reading threads get concurrent access when writes are not taking place. A derivative class could use this to implement an O(_n_) insertion algorithm (e.g. `tree.putBlocking(key, value)`), so trading concurrency _temporarily_ for insertion speed.

It should also be noted that the current implementation when using [DefaultCharSequenceNodeFactory](http://concurrent-trees.googlecode.com/svn/concurrent-trees/javadoc/apidocs/com/googlecode/concurrenttrees/radix/node/concrete/DefaultCharSequenceNodeFactory.html), does not incur the overhead of string copying in inserts, so might actually possess some benefits of the approaches used in [Ukkonen's algorithm and its predecessors](http://europa.zbh.uni-hamburg.de/pubs/pdf/GieKur1997.pdf) (PDF).

Ukkonen's algorithm in particular does not seem to lend itself well to concurrency, and would require coarse locking. An algorithm based on right-to-left scanning of strings, inserting the shortest suffix first, would be most amenable to constructing a _generalized_ suffix tree (where the tree cannot be assumed to be empty) in O(_n_) or O(_n_ log(_n_)) time for each key, while involving fewer or potentially no read locks.

## Do you have a comic about Depth-First Search? ##
Yes certainly, glad you asked!

Credit: [xkcd.com](http://xkcd.com/), [Creative Commons Attribution-NonCommercial 2.5 License](http://xkcd.com/license.html)
![http://concurrent-trees.googlecode.com/svn/wiki/images/dfs-comic.png](http://concurrent-trees.googlecode.com/svn/wiki/images/dfs-comic.png)