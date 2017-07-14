# Concurrent-Trees Release Notes #

### Version 2.6.1 - 2017-07-14 ###
  * Performance improvements.
  * Reduced GC overhead of binary search during tree traversals, via pull request #27 from Clement Pang (many thanks!)
  * Additionally added some caching to further reduce GC overhead of the Node.getOutgoingEdges() method during tree traversals
  * Deployed to Maven central
  
### Version 2.6.0 - 2016-07-12 ###
  * Added new methods to `InvertedRadixTree` via pull request #17 from Jose Luis Pedrosa (many thanks!):
      * `getLongestKeyPrefixing()`
      * `getValueForLongestKeyPrefixing()`
      * `getKeyValuePairForLongestKeyPrefixing()`
  * Added support for using the library in OSGi environments via pull request #16 from Brian O'Connor (many thanks!)
  * Removed the unused support internally to `restrictConcurrency`, and associated constructors which allowed it to be requested. As the feature was unused internally it's unlikely anyone was using these constructors, so this should not cause problems, but please open an issue if any are encountered
  * Full test coverage
  * Deployed to Maven central

### Version 2.5.0 - 2016-01-24 ###
  * The trees are now serializable
  * Deployed to Maven central

### Version 2.4.0 - 2013-12-03 ###
  * Added tree.size() methods
  * Note tree.size() is relatively expensive for the radix trees having O(n) time complexity, but this might be useful for debugging purposes
  * Deployed to Maven central

### Version 2.3.0 - 2013-10-20 ###
  * Up to 50% reduction in memory overhead for UTF-8 single byte/ASCII-compatible strings
  * Added `DefaultByteArrayNodeFactory` - represents characters in the tree using UTF-8 single byte per character (throwing an exception if incompatible characters are detected)
  * Added `SmartArrayBasedNodeFactory` - automatically selects between UTF-8 and UTF-16 character encoding on a node-by-node basis (`DefaultByteArrayNodeFactory` and `DefaultCharArrayNodeFactory` respectively)
  * Deployed to Maven central

### Version 2.2.0 - 2013-10-07 ###
  * `InvertedRadixTree` now extends the pubic interface of `RadixTree`
  * This allows the same tree to be searched for keywords starting with a prefix, and to be used to scan documents for keywords contained in the tree
  * Minor change in the API of `InvertedRadixTree` to provide additional methods, _should_ be a drop in replacement for 2.1.x
  * Deployed to Maven central

### Version 2.1.1 - 2013-10-07 ###
  * Fixed potential for `InvertedRadixTree` to return false positives when input document is shorter than keywords in the tree, with thanks to Beth Tirado for patch ([issue 6](https://code.google.com/p/concurrent-trees/issues/detail?id=6))
  * Additional test cases
  * This is a drop in replacement for 2.1.0
  * Deployed to Maven central

### Version 2.1.0 - 2013-08-07 ###
  * Added feature to `InvertedRadixTree` to scan input documents for keywords stored in the tree which prefix those documents. See `InvertedRadixTree.getKeysPrefixing()` and related methods
  * This is conceptually similar to the existing `InvertedRadixTree.getKeysContainedIn()` methods, but is restricted to scanning the input for keys prefixing the document instead of keys contained anywhere in the document
  * This can be used to process phone numbers
  * Discussed in [issue 5](https://code.google.com/p/concurrent-trees/issues/detail?id=5)
  * Full test coverage
  * Deployed to Maven central

### Version 2.0.0 - 2013-02-26 ###
  * Uses lazy evaluation for lower latency, lower CPU/memory usage, and better filtering support: see [Frequently Asked Questions](http://code.google.com/p/concurrent-trees/wiki/FrequentlyAskedQuestions#What_is_Lazy_Evaluation,_and_why_return_Iterable_instead_of_Coll)
  * Minor API changes, example usage has been updated accordingly
  * Added new methods for looking up keys/values: `RadixTree.getClosestKeys` ([issue 2](https://code.google.com/p/concurrent-trees/issues/detail?id=2))
  * Full test coverage
  * Deployed to Maven central

### Version 1.0.0 - 2012-07-10 ###
  * First public release
  * `RadixTree`, `ReversedRadixTree`, `InvertedRadixTree`, `SuffixTree`, Longest common substring solver
  * Full test coverage
  * Deployed to Maven central
