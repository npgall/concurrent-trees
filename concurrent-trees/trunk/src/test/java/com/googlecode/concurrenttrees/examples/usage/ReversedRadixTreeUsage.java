package com.googlecode.concurrenttrees.examples.usage;

import com.googlecode.concurrenttrees.common.PrettyPrintUtil;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.NaiveCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;
import com.googlecode.concurrenttrees.radixreversed.ConcurrentReversedRadixTree;
import com.googlecode.concurrenttrees.radixreversed.ReversedRadixTree;

/**
 * @author Niall Gallagher
 */
public class ReversedRadixTreeUsage {

    public static void main(String[] args) {
        ReversedRadixTree<Integer> tree = new ConcurrentReversedRadixTree<Integer>(new NaiveCharArrayNodeFactory());

        tree.put("TEST", 1);
        tree.put("TOAST", 2);
        tree.put("TEAM", 3);

        System.out.println("Tree structure:");
        // PrettyPrintable is a non-public API for testing, prints semi-graphical representations of trees...
        PrettyPrintUtil.prettyPrint((PrettyPrintable)tree, System.out);

        System.out.println();
        System.out.println("Value for 'TEST' (exact match): " + tree.getValueForExactKey("TEST"));
        System.out.println("Value for 'TOAST' (exact match): " + tree.getValueForExactKey("TOAST"));
        System.out.println();
        System.out.println("Keys ending with 'ST': " + tree.getKeysEndingWith("ST"));
        System.out.println("Keys ending with 'M': " + tree.getKeysEndingWith("M"));
        System.out.println();
        System.out.println("Values for keys ending with 'ST': " + tree.getValuesForKeysEndingWith("ST"));
        System.out.println("Key-Value pairs for keys ending with 'ST': " + tree.getKeyValuePairsForKeysEndingWith("ST"));
    }
}
