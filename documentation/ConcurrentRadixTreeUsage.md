Example Usage for Concurrent Radix Tree.


## General Usage ##
  * Create a concurrent radix tree
  * Insert keys "TEST", "TOAST", "TEAM", associate with integer values 1, 2, 3
  * Graphically print the structure of the tree
  * Find values for keys exactly matching "TEST", "TOAST"
  * Find keys starting with "T", "TE"
  * Find values for keys starting with "TE"
  * Find key-value pairs for keys starting with "TE"
  * Find keys closest to non-existent key 'TEMPLE'

### Code ###
```
public static void main(String[] args) {
    RadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(new DefaultCharArrayNodeFactory());

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
    System.out.println("Keys starting with 'T': " + Iterables.toString(tree.getKeysStartingWith("T")));
    System.out.println("Keys starting with 'TE': " + Iterables.toString(tree.getKeysStartingWith("TE")));
    System.out.println();
    System.out.println("Values for keys starting with 'TE': " + Iterables.toString(tree.getValuesForKeysStartingWith("TE")));
    System.out.println("Key-Value pairs for keys starting with 'TE': " + Iterables.toString(tree.getKeyValuePairsForKeysStartingWith("TE")));
    System.out.println();
    System.out.println("Keys closest to 'TEMPLE': " + Iterables.toString(tree.getClosestKeys("TEMPLE")));
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

Keys starting with 'T': [TEAM, TEST, TOAST]
Keys starting with 'TE': [TEAM, TEST]

Values for keys starting with 'TE': [3, 1]
Key-Value pairs for keys starting with 'TE': [(TEAM, 3), (TEST, 1)]

Keys closest to 'TEMPLE': [TEAM, TEST]
```

## Adding Keys without Values ##
```
tree.put("FOO", VoidValue.SINGLETON);
```
Supplying [VoidValue](http://concurrent-trees.googlecode.com/svn/concurrent-trees/javadoc/apidocs/com/googlecode/concurrenttrees/radix/node/concrete/voidvalue/VoidValue.html) as above, stores the key in the tree without associating it with any value.

Internally, a special type of node will be used which omits a field for storing a value entirely, which can reduce memory usage when values are not needed and there will be a lot of such nodes.

For more details see NodeFactoryAndMemoryUsage.