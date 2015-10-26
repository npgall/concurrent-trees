## Example Usage for Concurrent Suffix Tree ##
### Objective ###
  * Print the suffixes of "TEST", "TOAST", "TEAM" (for reference only)
  * Create a concurrent suffix tree
  * Insert keys "TEST", "TOAST", "TEAM", associate with integer values 1, 2, 3
  * Graphically print the structure of the tree
  * Find values for keys exactly matching "TEST", "TOAST"
  * Find keys ending with "ST", "M"
  * Find values for keys ending with "ST"
  * Find key-value pairs for keys ending with "ST"
  * Find keys containing "TE", "A"
  * Find values for keys containing "A"
  * Find key-value pairs for keys containing "A"

### Code ###
```
public static void main(String[] args) {
    System.out.println("Suffixes for 'TEST': " + Iterables.toString(CharSequences.generateSuffixes("TEST")));
    System.out.println("Suffixes for 'TOAST': " + Iterables.toString(CharSequences.generateSuffixes("TOAST")));
    System.out.println("Suffixes for 'TEAM': " + Iterables.toString(CharSequences.generateSuffixes("TEAM")));

    SuffixTree<Integer> tree = new ConcurrentSuffixTree<Integer>(new DefaultCharArrayNodeFactory());

    tree.put("TEST", 1);
    tree.put("TOAST", 2);
    tree.put("TEAM", 3);

    System.out.println();
    System.out.println("Tree structure:");
    // PrettyPrintable is a non-public API for testing, prints semi-graphical representations of trees...
    PrettyPrinter.prettyPrint((PrettyPrintable) tree, System.out);

    System.out.println();
    System.out.println("Value for 'TEST' (exact match): " + tree.getValueForExactKey("TEST"));
    System.out.println("Value for 'TOAST' (exact match): " + tree.getValueForExactKey("TOAST"));
    System.out.println();
    System.out.println("Keys ending with 'ST': " + Iterables.toString(tree.getKeysEndingWith("ST")));
    System.out.println("Keys ending with 'M': " + Iterables.toString(tree.getKeysEndingWith("M")));
    System.out.println("Values for keys ending with 'ST': " + Iterables.toString(tree.getValuesForKeysEndingWith("ST")));
    System.out.println("Key-Value pairs for keys ending with 'ST': " + Iterables.toString(tree.getKeyValuePairsForKeysEndingWith("ST")));
    System.out.println();
    System.out.println("Keys containing 'TE': " + Iterables.toString(tree.getKeysContaining("TE")));
    System.out.println("Keys containing 'A': " + Iterables.toString(tree.getKeysContaining("A")));
    System.out.println("Values for keys containing 'A': " + Iterables.toString(tree.getValuesForKeysContaining("A")));
    System.out.println("Key-Value pairs for keys containing 'A': " + Iterables.toString(tree.getKeyValuePairsForKeysContaining("A")));
}
```

### Output ###
```
Suffixes for 'TEST': [TEST, EST, ST, T]
Suffixes for 'TOAST': [TOAST, OAST, AST, ST, T]
Suffixes for 'TEAM': [TEAM, EAM, AM, M]

Tree structure:
○
├── ○ A
│   ├── ○ M ([TEAM])
│   └── ○ ST ([TOAST])
├── ○ E
│   ├── ○ AM ([TEAM])
│   └── ○ ST ([TEST])
├── ○ M ([TEAM])
├── ○ OAST ([TOAST])
├── ○ ST ([TOAST, TEST])
└── ○ T ([TOAST, TEST])
    ├── ○ E
    │   ├── ○ AM ([TEAM])
    │   └── ○ ST ([TEST])
    └── ○ OAST ([TOAST])

Value for 'TEST' (exact match): 1
Value for 'TOAST' (exact match): 2

Keys ending with 'ST': [TOAST, TEST]
Keys ending with 'M': [TEAM]
Values for keys ending with 'ST': [2, 1]
Key-Value pairs for keys ending with 'ST': [(TEST, 1), (TOAST, 2)]

Keys containing 'TE': [TEAM, TEST]
Keys containing 'A': [TEAM, TOAST]
Values for keys containing 'A': [3, 2]
Key-Value pairs for keys containing 'A': [(TEAM, 3), (TOAST, 2)]
```