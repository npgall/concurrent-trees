# Introduction #

The trees in this project are not coupled with the implementation of Node objects.

[Node](http://concurrent-trees.googlecode.com/svn/concurrent-trees/javadoc/apidocs/com/googlecode/concurrenttrees/radix/node/Node.html) is an interface that algorithms in the trees interact with, and so for any node it is possible to abstract its implementation to reduce memory overhead.

The tree algorithms do not create nodes directly, they request new nodes from a [NodeFactory](http://concurrent-trees.googlecode.com/svn/concurrent-trees/javadoc/apidocs/com/googlecode/concurrenttrees/radix/node/NodeFactory.html) supplied to the constructor of the tree. Some basic factories are included (discussed below).



# Memory reduction techniques #

The following techniques to reduce memory overhead are mostly applicable to suffix trees built for large keys/documents (such as adding entire documents to the tree as individual keys). This is less applicable to radix trees, which inherently require much less memory even for long documents.

A Java object consumes a minimum of perhaps 32 bytes of RAM, and a reference to the object will consume another 4 or 8 bytes. The number of fields and the type of fields in an object additionally increases memory overhead. Details [here](http://www.codeinstructions.com/2008/12/java-objects-memory-structure.html).

Building suffix trees from large text documents, can require many nodes. For example the `test/` folder in the source (see also ShakespeareCollectedWorks), contains tests based on the Collected Works of William Shakespeare (not actually a large data set by today's standards). A Shakespearean play is approximately 160KB on disk with UTF-8 encoding. A suffix tree for such a play was found to require approximately 217,697 nodes. Java by default stores characters as UTF-16. Storing character data within each node in a suffix tree for such a document would require >29 GB of RAM. Storing character data outside the suffix tree and using offset pointers instead, reduces this to ~280 MB. So it is useful that `NodeFactory` abstracts the internal representation of nodes from the algorithms which manipulate them.

Nodes are only required to expose the _edges_ within the tree as a `CharSequence` _view_ onto the character sequence, and as such they can either store character data inside the node - for example as a copied `char[]`, or outside the tree as start and end offsets into the original input string.

# Node factories provided #

The following implementations of `NodeFactory` are provided:
  * [DefaultCharArrayNodeFactory](http://concurrent-trees.googlecode.com/svn/concurrent-trees/javadoc/apidocs/com/googlecode/concurrenttrees/radix/node/concrete/DefaultCharArrayNodeFactory.html)
    * Stores character data inside the tree by copying character sequences into a `char[]` stored within each node
    * This can use more memory for suffix trees when storing the many suffixes of large documents
    * An advantage of this factory is garbage collection: there is no risk that a large string might be retained in memory by a single node referencing only a small subsequence of the string
  * [DefaultByteArrayNodeFactory](http://concurrent-trees.googlecode.com/svn/concurrent-trees/javadoc/apidocs/com/googlecode/concurrenttrees/radix/node/concrete/DefaultByteArrayNodeFactory.html)
    * Similar to `DefaultCharArrayNodeFactory`, but stores character data inside the tree as UTF-8 single byte per character, instead of Java's default UTF-16 two-bytes per character
    * This may reduce memory overhead compared with `DefaultCharArrayNodeFactory` by 50%, but is only compatible with strings containing characters which can be represented as UTF-8 single byte/ASCII
  * [SmartArrayBasedNodeFactory](http://concurrent-trees.googlecode.com/svn/concurrent-trees/javadoc/apidocs/com/googlecode/concurrenttrees/radix/node/concrete/SmartArrayBasedNodeFactory.html)
    * Internally uses `DefaultByteArrayNodeFactory` to create nodes by default, but falls back to `DefaultCharArrayNodeFactory` automatically if characters are detected which cannot be represented as a single byte
    * A combination of encodings may be used to represent any single path in the tree, as the representation is chosen on a node-by-node basis
    * **If you are unsure, this is the recommended node factory for most cases**
  * [DefaultCharSequenceNodeFactory](http://concurrent-trees.googlecode.com/svn/concurrent-trees/javadoc/apidocs/com/googlecode/concurrenttrees/radix/node/concrete/DefaultCharSequenceNodeFactory.html)
    * Does not store character data inside the tree, but instead stores pointers to character sequences in the input string, as start and end offsets and a reference to the original string in the node
    * An advantage of this factory is it uses much less memory for suffix trees
    * A disadvantage of this factory is garbage collection: if a large document/key is added to the tree, and then a small document is added to the tree, the nodes added for the small document will re-use edges which were previously added for the large document. If the large document is subsequently removed from the tree, edges which are still in use by the small document will not be removed, and so the large document will not be garbage collected

The node factories above return different implementations of nodes depending on the data that the tree algorithms supply to the factory to create a new node. For example when a node does not contain a value, these factories will return specific node implementations which omit the value field. Leaf nodes do not need a data structure to reference child nodes, and so additionally some node implementations omit that data structure. Finally, the node factories support inserting keys into the tree which **do not have values at all**. This is discussed below.

# Writing custom node factories #

The user could reduce memory overhead further by writing more sophisticated node factories, with various tradeoffs:
  * If the number of unique characters will be small, encode character data in even smaller character sets than UTF-8 (5-bit, 6-bit, 7-bit) and access bitwise
  * Compress character data for reduced memory overhead, but greater read overhead
  * Where an edge contains only two characters (for example), instead of storing a two-character `char[]`, a dedicated implementation of a node could be returned which stores the edge in two primitive `char` fields (`char[]` consumes more memory than primitive fields)

# Note about `VoidValue` - inserting keys without values #

The node factories above support inserting keys into the tree which do not have values, using [VoidValue](http://concurrent-trees.googlecode.com/svn/concurrent-trees/javadoc/apidocs/com/googlecode/concurrenttrees/radix/node/concrete/voidvalue/VoidValue.html) objects.

If an entry is added to a tree as follows, a custom node implementation will be used which does not actually store a value in the tree at all. This can reduce memory usage by eliminating a field and object reference.

**Insert a key without a value**
```
tree.put("FOO", VoidValue.SINGLETON);
```

_This optimization applies to Concurrent Radix Tree and its derivatives only._ The Concurrent Suffix Tree does not store application-supplied values in the tree in the first place.

In the case of Concurrent Suffix Tree, because application-supplied values are associated with complete keys, and suffixes must also be associated with complete keys, the tree stores references to complete keys in nodes associated with suffixes, and the mapping from complete keys to application-supplied values is maintained outside of the tree.

The compete key-to-value map would only have as many entries as complete keys added to the tree, so much fewer entries than there would be nodes. There would not be a great memory saving in applications using `VoidValue` with Concurrent Suffix Tree, however it would be harmless to do so. In terms of readability, supplying `VoidValue` could still be _recommended_, because it might convey the intent of the application more clearly.