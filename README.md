[![Build Status](https://travis-ci.org/npgall/concurrent-trees.svg?branch=master)](https://travis-ci.org/npgall/concurrent-trees)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.googlecode.concurrent-trees/concurrent-trees/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.googlecode.concurrent-trees%22%20AND%20a%3Aconcurrent-trees)
[![Reference Status](https://www.versioneye.com/java/com.googlecode.concurrent-trees:concurrent-trees/reference_badge.svg?style=flat-square)](https://www.versioneye.com/java/com.googlecode.concurrent-trees:concurrent-trees/references)


_"A tree is a tree. How many more do you have to look at?"_

―Ronald Reagan

# Concurrent Trees #

This project provides concurrent [Radix Trees](http://en.wikipedia.org/wiki/Radix_tree) and concurrent [Suffix Trees](http://en.wikipedia.org/wiki/Suffix_tree) for Java.



## Overview ##
A **Radix Tree** (also known as patricia trie, radix _trie_ or compact prefix tree) is a space-optimized tree data structure which allows keys (and optionally values associated with those keys) to be inserted for subsequent lookup using only a _prefix_ of the key rather than the whole key. Radix trees have applications in string or document indexing and scanning, where they can allow faster scanning and lookup than brute force approaches. Some applications of Radix Trees:
  * Associate objects with keys which have a natural _hierarchy_ (for example nested categories, or paths in a file system)
  * Scan documents for large numbers of keywords in a _scalable way_ (i.e. more scalable than naively running `document.contains(keyword)`, see below)
  * Build indexes supporting fast "starts with", "ends with" or "equals" lookup
  * Support _auto-complete_ or query suggestion, for partial queries entered into a search box

A **Suffix Tree** (also known as PAT tree or position tree) is an extension of a radix tree which allows the _suffixes_ of keys to be inserted into the tree. This allows subsequent lookup using _any suffix or fragment_ of the key rather than the whole key, and in turn this can support fast string operations or analysis of documents. Some applications of Suffix Trees:
  * Build indexes supporting fast "ends with" or "contains" lookup
  * Perform more complex analyses of collections of documents, such as finding common substrings
  
The implementation in this project is actually a [Generalized Suffix Tree](http://en.wikipedia.org/wiki/Generalized_suffix_tree).

### Concurrency Support ###
All of the trees (data structures and algorithms) in this project are optimized for **high-concurrency and high performance reads**, and **low-concurrency or background writes**:
  * Reads are _lock-free_ (reading threads never block, even while writes are ongoing)
  * Reading threads always see a consistent _version_ of the tree
  * Reading threads do not block writing threads
  * Writing threads block each other but never block reading threads

As such reading threads should never encounter latency due to ongoing writes or other concurrent readers.

## Tree Design ##

The trees in this project support lock-free reads while allowing concurrent writes, by treating the tree as a mostly-immutable structure, and assembling the changes to be made to the tree into a **patch**, which is then applied to the tree in a **single atomic operation**.

Inserting an entry into Concurrent Radix Tree which requires an existing node within the tree to be split:
![tree-apply-patch.png](documentation/images/tree-apply-patch.png)

  * Reading threads traversing the tree while the patch above is being applied, will either see the _old version_ or the _new version_ of the (sub-)tree, but both versions are consistent views of the tree, which preserve the invariants. For more details see [TreeDesign](documentation/TreeDesign.md).

## Tree Implementations ##
Feature matrix for tree implementations provided in this project, and lookup operations supported.


| <sub>**Tree Interface**</sub> | <sub>**Implementation**</sub> | <sub>**Key Equals (exact match)**</sub> | <sub>**Key Starts With**</sub> | <sub>**Key Ends With**</sub> | <sub>**Key Contains**</sub> | <sub>**Find Keywords In External Documents**</sub> <sup>[1]</sup> |
|:-------------------|:-------------------|:-----------------------------|:--------------------|:------------------|:-----------------|:-------------------------------------------------------|
|[<sub>RadixTree</sub>](http://htmlpreview.github.io/?http://raw.githubusercontent.com/npgall/concurrent-trees/master/documentation/javadoc/apidocs/com/googlecode/concurrenttrees/radix/RadixTree.html)|[<sub>ConcurrentRadixTree</sub>](http://htmlpreview.github.io/?http://raw.githubusercontent.com/npgall/concurrent-trees/master/documentation/javadoc/apidocs/com/googlecode/concurrenttrees/radix/ConcurrentRadixTree.html)|✓                             |✓                    |                   |                  |                                                        |
|[<sub>ReversedRadixTree</sub>](http://htmlpreview.github.io/?http://raw.githubusercontent.com/npgall/concurrent-trees/master/documentation/javadoc/apidocs/com/googlecode/concurrenttrees/radixreversed/ReversedRadixTree.html)|[<sub>ConcurrentReversedRadixTree</sub>](http://htmlpreview.github.io/?http://raw.githubusercontent.com/npgall/concurrent-trees/master/documentation/javadoc/apidocs/com/googlecode/concurrenttrees/radixreversed/ConcurrentReversedRadixTree.html)|✓                             |                     |✓                  |                  |                                                        |
|[<sub>InvertedRadixTree</sub>](http://htmlpreview.github.io/?http://raw.githubusercontent.com/npgall/concurrent-trees/master/documentation/javadoc/apidocs/com/googlecode/concurrenttrees/radixinverted/InvertedRadixTree.html)|[<sub>ConcurrentInvertedRadixTree</sub>](http://htmlpreview.github.io/?http://raw.githubusercontent.com/npgall/concurrent-trees/master/documentation/javadoc/apidocs/com/googlecode/concurrenttrees/radixinverted/ConcurrentInvertedRadixTree.html)|✓                             |✓                    |                   |                  |✓                                                       |
|[<sub>SuffixTree</sub>](http://htmlpreview.github.io/?http://raw.githubusercontent.com/npgall/concurrent-trees/master/documentation/javadoc/apidocs/com/googlecode/concurrenttrees/suffix/SuffixTree.html)|[<sub>ConcurrentSuffixTree</sub>](http://htmlpreview.github.io/?http://raw.githubusercontent.com/npgall/concurrent-trees/master/documentation/javadoc/apidocs/com/googlecode/concurrenttrees/suffix/ConcurrentSuffixTree.html)|✓                             |                     |✓                  |✓                 |                                                        |

<sup>[1]</sup> **Scanning for Keywords in External Documents**

`ConcurrentInvertedRadixTree` allows unseen documents to be scanned efficiently for keywords contained in the tree, and performance does not degrade as additional keywords are added.

Let _d_ = number of characters in document, _n_ = number of keywords, _k_ = average keyword length

| <sub>**Keyword scanning approach**</sub> | <sub>**Time Complexity (Number of character comparisons)**</sub> | <sub>**Example: 10000 10-character keywords, 10000 character document**</sub> |
|:------------------------------|:------------------------------------------------------|:------------------------------------------------------------------|
| Naive `document.contains(keyword)` for every keyword | O(_d_ _n_ _k_)                                        | 1,000,000,000 character comparisons |
| ConcurrentInvertedRadixTree   | O(_d_ log(_k_))                                       | 10,000 character comparisons (≤100,000 times faster) |

## Solver Utilities ##

Utilities included which solve problems using the included trees.

| **Solver** | **Solves** |
|:-----------|:-----------|
|[LCSubstringSolver](http://htmlpreview.github.io/?http://raw.githubusercontent.com/npgall/concurrent-trees/master/documentation/javadoc/apidocs/com/googlecode/concurrenttrees/solver/LCSubstringSolver.html)|[Longest common substring problem](http://en.wikipedia.org/wiki/Longest_common_substring_problem)|

## Documentation and Example Usage ##

**General Documentation**

  * [JavaDocs](http://htmlpreview.github.io/?http://raw.githubusercontent.com/npgall/concurrent-trees/master/documentation/javadoc/apidocs/index.html) - APIs
  * [Discussion Group](http://groups.google.com/group/concurrent-trees-discuss) - Post questions here
  * [FrequentlyAskedQuestions](documentation/FrequentlyAskedQuestions.md) - Frequently Asked Questions, _for various values of frequently_
  * [NodeFactoryAndMemoryUsage](documentation/NodeFactoryAndMemoryUsage.md) - How to use custom node implementations and manage memory
  * [TreeDesign](documentation/TreeDesign.md) - Overview of the approach to concurrency

For more documentation see the [documentation](documentation) directory.

**Example Usage**

  * [ConcurrentRadixTreeUsage](documentation/ConcurrentRadixTreeUsage.md) - Example Usage for Concurrent Radix Tree
  * [ConcurrentReversedRadixTreeUsage](documentation/ConcurrentReversedRadixTreeUsage.md) - Example Usage for Concurrent Reversed Radix Tree
  * [ConcurrentInvertedRadixTreeUsage](documentation/ConcurrentInvertedRadixTreeUsage.md) - Example Usage for Concurrent Inverted Radix Tree
  * [ConcurrentSuffixTreeUsage](documentation/ConcurrentSuffixTreeUsage.md) - Example Usage for Concurrent Suffix Tree
  * [LCSubstringSolverUsage](documentation/LCSubstringSolverUsage.md) - Example Usage to find the Longest Common Substring in a collection of documents
  * [InMemoryFileSystemUsage](documentation/InMemoryFileSystemUsage.md) - Example Usage for an In-Memory File System proof of concept based on Concurrent Radix Tree

## Usage in Maven and Non-Maven Projects ##

Concurrent-Trees is in Maven Central. See [Downloads](documentation/Downloads.md).

## Related Projects ##

  * [CQEngine](http://github.com/npgall/cqengine/), a NoSQL indexing and query engine with ultra-low latency


## Project Status ##

As of writing (July 2017), version 2.6.1 of concurrent-trees is the latest release.
  * Full test coverage
  * Over 14,000 downloads per month and over 150,000 downloads to-date

See [Release Notes](documentation/ReleaseNotes.md) and [Frequently Asked Questions](documentation/FrequentlyAskedQuestions.md) for details.

Report any bugs/feature requests in the [Issues](http://github.com/npgall/concurrent-trees/issues) tab.
For support please use the [Discussion Group](http://groups.google.com/forum/?fromgroups#!forum/concurrent-trees-discuss), not direct email to the developers.
