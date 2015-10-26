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