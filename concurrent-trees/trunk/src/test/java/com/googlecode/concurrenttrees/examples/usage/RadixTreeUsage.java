package com.googlecode.concurrenttrees.examples.usage;

import com.googlecode.concurrenttrees.common.PrettyPrintUtil;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;

/**
 * @author Niall Gallagher
 */
public class RadixTreeUsage {

    public static void main(String[] args) {
        RadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(new DefaultCharArrayNodeFactory());

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
        System.out.println("Keys starting with 'T': " + tree.getKeysStartingWith("T"));
        System.out.println("Keys starting with 'TE': " + tree.getKeysStartingWith("TE"));
        System.out.println();
        System.out.println("Values for keys starting with 'TE': " + tree.getValuesForKeysStartingWith("TE"));
        System.out.println("Key-Value pairs for keys starting with 'TE': " + tree.getKeyValuePairsForKeysStartingWith("TE"));
    }
}
