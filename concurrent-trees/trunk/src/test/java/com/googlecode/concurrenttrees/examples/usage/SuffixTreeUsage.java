package com.googlecode.concurrenttrees.examples.usage;

import com.googlecode.concurrenttrees.common.CharSequenceUtil;
import com.googlecode.concurrenttrees.common.PrettyPrintUtil;
import com.googlecode.concurrenttrees.examples.usage.testutils.TestUtils;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.NaiveCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;
import com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree;
import com.googlecode.concurrenttrees.suffix.SuffixTree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Niall Gallagher
 */
public class SuffixTreeUsage {

    public static void main(String[] args) {
        System.out.println("Suffixes for 'TEST': " + TestUtils.iterableToString(CharSequenceUtil.generateSuffixes("TEST")));
        System.out.println("Suffixes for 'TOAST': " + TestUtils.iterableToString(CharSequenceUtil.generateSuffixes("TOAST")));
        System.out.println("Suffixes for 'TEAM': " + TestUtils.iterableToString(CharSequenceUtil.generateSuffixes("TEAM")));

        SuffixTree<Integer> tree = new ConcurrentSuffixTree<Integer>(new NaiveCharArrayNodeFactory());

        tree.put("TEST", 1);
        tree.put("TOAST", 2);
        tree.put("TEAM", 3);

        System.out.println();
        System.out.println("Tree structure:");
        // PrettyPrintable is a non-public API for testing, prints semi-graphical representations of trees...
        PrettyPrintUtil.prettyPrint((PrettyPrintable)tree, System.out);

        System.out.println();
        System.out.println("Value for 'TEST' (exact match): " + tree.getValueForExactKey("TEST"));
        System.out.println("Value for 'TOAST' (exact match): " + tree.getValueForExactKey("TOAST"));
        System.out.println();
        System.out.println("Keys ending with 'ST': " + tree.getKeysEndingWith("ST"));
        System.out.println("Keys ending with 'M': " + tree.getKeysEndingWith("M"));
        System.out.println("Values for keys ending with 'ST': " + tree.getValuesForKeysEndingWith("ST"));
        System.out.println("Key-Value pairs for keys ending with 'ST': " + tree.getKeyValuePairsForKeysEndingWith("ST"));
        System.out.println();
        System.out.println("Keys containing 'TE': " + tree.getKeysContaining("TE"));
        System.out.println("Keys containing 'A': " + tree.getKeysContaining("A"));
        System.out.println("Values for keys containing 'A': " + tree.getValuesForKeysContaining("A"));
        System.out.println("Key-Value pairs for keys containing 'A': " + tree.getKeyValuePairsForKeysContaining("A"));
    }
}
