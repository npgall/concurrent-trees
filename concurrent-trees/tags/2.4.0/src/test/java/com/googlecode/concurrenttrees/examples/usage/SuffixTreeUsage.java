/**
 * Copyright 2012-2013 Niall Gallagher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.concurrenttrees.examples.usage;

import com.googlecode.concurrenttrees.common.CharSequences;
import com.googlecode.concurrenttrees.common.Iterables;
import com.googlecode.concurrenttrees.common.PrettyPrinter;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;
import com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree;
import com.googlecode.concurrenttrees.suffix.SuffixTree;

/**
 * @author Niall Gallagher
 */
public class SuffixTreeUsage {

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
}
