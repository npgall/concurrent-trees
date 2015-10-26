The design of trees in this project to support concurrent lock-free reads with ongoing writes.

## Architecture and Concurrency Mechanism ##
All of the trees in this project (derivatives of the Radix Tree, the Suffix Tree, and derivatives of the Suffix Tree) are based on the implementation of the Concurrent Radix Tree.

The Concurrent Radix Tree supports lock-free reads while allowing concurrent writes, by _calculating the changes which would be made to the tree were it mutable_, and assembling those changes into a **patch**, which is then applied to the tree in a **single atomic operation**.

### Atomic updates by patching the tree ###
Inserting an entry into Concurrent Radix Tree which requires an existing node within the tree to be split.
![http://concurrent-trees.googlecode.com/svn/wiki/images/tree-apply-patch.png](http://concurrent-trees.googlecode.com/svn/wiki/images/tree-apply-patch.png)

This mechanism takes advantage of spatial [locality of reference](http://en.wikipedia.org/wiki/Locality_of_reference), which is inherent in radix trees; which means that most changes to the tree need only affect a small section (a few nodes) of the tree.

A patch encapsulating the change is constructed, and descendant nodes of the existing node to be replaced, are attached unmodified to the patch (thus descendant nodes are referenced  _simultaneously_ by the existing node _and_ the replacement nodes in the patch). The patch is then applied to the tree by swapping out the existing node for the patch, by replacing the object reference to the existing node in its parent atomically.

Reading threads traversing the tree while the patch above is being applied, will either see the _old version_ or the _new version_ of the (sub-)tree, but both versions are consistent views of the tree, which preserve the invariants.

The node 'te' will be garbage collected per normal Java garbage collection rules, when it is no longer reachable by any thread; that is when threads which are traversing the old version of the tree, finish traversing that version and release their reference to the old node.

Most changes to be applied to the tree are not more complicated than the example above, and are implemented using the same patching mechanism. Their exact algorithms are documented in the source code.

### Restrictions on Mutability ###
To support atomicity of updates as above, nodes in the tree have the following restrictions in their design:
  * Nodes are _mostly-immutable_
  * The characters for an incoming "edge" to a node (a reference from a parent node to a child node which has some characters of a key associated with it) are stored in the child node rather than a dedicated Edge object
  * The incoming edge to a node is immutable (the characters of an edge represented by a child node can never be changed in that child node)
  * The reference to a value associated with a node, if any, is immutable
  * The invariant of radix trees that no two outgoing edges from a node can share the same first character is strictly enforced:
    * The number of outgoing edges from a node is immutable
    * The only _mutable_ aspect of a node is that a reference to its child node for an _existing_ outgoing edge as identified by the first character of that edge, can be updated to point to a new child node, as long as the edge of the new child node starts with the same first character
  * Updating the child node reference for an existing edge is an atomic operation

The restrictions above allow updates to be applied _**atomically**_ by applying a pre-assembled _**patch**_, and they also ensure that any patch to be applied to the tree, is itself fully formed and immutable.

Strictly speaking, some of the restrictions above are _not_ required for the atomicity aspect, but are in the design to reduce memory overhead as discussed in NodeFactoryAndMemoryUsage, and to allow deterministic binary search during traversal as discussed below.

## Traversal ##
  * Algorithms use iteration rather than recursion to traverse the trees, which allows large trees to be traversed without a risk of stack overflow
  * Traversals for querying the tree use lazy iteration, such that the application drives iteration by stepping through a results Iterable
  * The method to locate during traversal the sought child node among a collection of child nodes descending from a parent, is factored out to the implementation of Node objects
    * Currently, the provided `NodeFactory` implementations use **binary search** to locate sought child nodes; however alternative Node implementations which use hash maps keyed on the first character of the outgoing edge would also be possible