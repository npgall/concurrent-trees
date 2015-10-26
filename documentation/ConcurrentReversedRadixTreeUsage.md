Example Usage for Concurrent Reversed Radix Tree.


## General Usage ##
  * Create a concurrent reversed radix tree
  * Insert keys "TEST", "TOAST", "TEAM", associate with integer values 1, 2, 3
  * Graphically print the structure of the tree
  * Find values for keys exactly matching "TEST", "TOAST"
  * Find keys ending with "ST", "M"
  * Find values for keys ending with "ST"
  * Find key-value pairs for keys ending with "ST"

### Code ###
```
public static void main(String[] args) {
    ReversedRadixTree<Integer> tree = new ConcurrentReversedRadixTree<Integer>(new DefaultCharArrayNodeFactory());

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
    System.out.println("Keys ending with 'ST': " + Iterables.toString(tree.getKeysEndingWith("ST")));
    System.out.println("Keys ending with 'M': " + Iterables.toString(tree.getKeysEndingWith("M")));
    System.out.println();
    System.out.println("Values for keys ending with 'ST': " + Iterables.toString(tree.getValuesForKeysEndingWith("ST")));
    System.out.println("Key-Value pairs for keys ending with 'ST': " + Iterables.toString(tree.getKeyValuePairsForKeysEndingWith("ST")));
}
```

### Output ###
```
Tree structure:
○
├── ○ MAET (3)
└── ○ TS
    ├── ○ AOT (2)
    └── ○ ET (1)

Value for 'TEST' (exact match): 1
Value for 'TOAST' (exact match): 2

Keys ending with 'ST': [TOAST, TEST]
Keys ending with 'M': [TEAM]

Values for keys ending with 'ST': [2, 1]
Key-Value pairs for keys ending with 'ST': [(TOAST, 2), (TEST, 1)]
```

## Adding Keys without Values ##
```
tree.put("FOO", VoidValue.SINGLETON);
```
Supplying [VoidValue](http://concurrent-trees.googlecode.com/svn/concurrent-trees/javadoc/apidocs/com/googlecode/concurrenttrees/radix/node/concrete/voidvalue/VoidValue.html) as above, stores the key in the tree without associating it with any value.

Internally, a special type of node will be used which omits a field for storing a value entirely, which can reduce memory usage when values are not needed and there will be a lot of such nodes.

For more details see NodeFactoryAndMemoryUsage.