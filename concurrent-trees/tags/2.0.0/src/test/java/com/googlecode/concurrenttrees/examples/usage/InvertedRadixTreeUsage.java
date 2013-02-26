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

import com.googlecode.concurrenttrees.common.Iterables;
import com.googlecode.concurrenttrees.common.PrettyPrinter;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;
import com.googlecode.concurrenttrees.radixinverted.ConcurrentInvertedRadixTree;
import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;

/**
 * @author Niall Gallagher
 */
public class InvertedRadixTreeUsage {

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
}
