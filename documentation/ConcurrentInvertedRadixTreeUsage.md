Example Usage for Concurrent Inverted Radix Tree.


## General Usage ##
  * Create a concurrent inverted radix tree
  * Insert **_keywords_** "TEST", "TOAST", "TEAM", associate with integer values 1, 2, 3
  * Graphically print the structure of the tree
  * Find values for keywords exactly matching "TEST", "TOAST"
  * Find keywords **_contained in example document_** "MY TEAM LIKES TOAST"
  * Find keywords contained in example document "MY TEAM LIKES TOASTERS"
  * Find values for keywords contained in example document "MY TEAM LIKES TOAST"
  * Find keyword-value pairs for keywords contained in example document "MY TEAM LIKES TOAST"

### Code ###
```
public static void main(String[] args) {
    InvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(new DefaultCharArrayNodeFactory());

    tree.put("TEST", 1);
    tree.put("TOAST", 2);
    tree.put("TEAM", 3);

    System.out.println("Tree structure:");
    // PrettyPrintable is a non-public API for testing, prints semi-graphical representations of trees...
    PrettyPrinter.prettyPrint((PrettyPrintable) tree, System.out);

    System.out.println();
    System.out.println("Value for 'TEST' (exact match): " + tree.getValueForExactKey("TEST"));
    System.out.println("Value for 'TOAST' (exact match): " + tree.getValueForExactKey("TOAST"));
    System.out.println();
    System.out.println("Keys contained in 'MY TEAM LIKES TOAST': " + Iterables.toString(tree.getKeysContainedIn("MY TEAM LIKES TOAST")));
    System.out.println("Keys contained in 'MY TEAM LIKES TOASTERS': " + Iterables.toString(tree.getKeysContainedIn("MY TEAM LIKES TOASTERS")));
    System.out.println("Values for keys contained in 'MY TEAM LIKES TOAST': " + Iterables.toString(tree.getValuesForKeysContainedIn("MY TEAM LIKES TOAST")));
    System.out.println("Key-value pairs for keys contained in 'MY TEAM LIKES TOAST': " + Iterables.toString(tree.getKeyValuePairsForKeysContainedIn("MY TEAM LIKES TOAST")));
}
```

### Output ###
```
Tree structure:
○
└── ○ T
    ├── ○ E
    │   ├── ○ AM (3)
    │   └── ○ ST (1)
    └── ○ OAST (2)

Value for 'TEST' (exact match): 1
Value for 'TOAST' (exact match): 2

Keys contained in 'MY TEAM LIKES TOAST': [TEAM, TOAST]
Keys contained in 'MY TEAM LIKES TOASTERS': [TEAM, TOAST]
Values for keys contained in 'MY TEAM LIKES TOAST': [3, 2]
Key-value pairs for keys contained in 'MY TEAM LIKES TOAST': [(TEAM, 3), (TOAST, 2)]
```

## Adding Keys without Values ##
```
tree.put("FOO", VoidValue.SINGLETON);
```
Supplying [VoidValue](http://concurrent-trees.googlecode.com/svn/concurrent-trees/javadoc/apidocs/com/googlecode/concurrenttrees/radix/node/concrete/voidvalue/VoidValue.html) as above, stores the key in the tree without associating it with any value.

Internally, a special type of node will be used which omits a field for storing a value entirely, which can reduce memory usage when values are not needed and there will be a lot of such nodes.

For more details see NodeFactoryAndMemoryUsage.