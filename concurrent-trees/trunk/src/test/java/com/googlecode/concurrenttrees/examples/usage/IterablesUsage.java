package com.googlecode.concurrenttrees.examples.usage;

import com.googlecode.concurrenttrees.common.Iterables;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;

import java.util.List;
import java.util.Set;

/**
 * @author Niall Gallagher
 */
public class IterablesUsage {

    public static void main(String[] args) {
        RadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(new DefaultCharArrayNodeFactory());

        tree.put("TEST", 1);
        tree.put("TOAST", 2);
        tree.put("TEAM", 3);

        Iterable<CharSequence> keysStartingWithT    = tree.getKeysStartingWith("T");

        List<CharSequence> listOfKeysStartingWithT  = Iterables.toList  (keysStartingWithT);
        Set<CharSequence> setOfKeysStartingWithT    = Iterables.toSet   (keysStartingWithT);
        String toStringOfKeysStartingWithT          = Iterables.toString(keysStartingWithT);

        System.out.println("Keys starting with 'T': " + toStringOfKeysStartingWithT);
    }
}
