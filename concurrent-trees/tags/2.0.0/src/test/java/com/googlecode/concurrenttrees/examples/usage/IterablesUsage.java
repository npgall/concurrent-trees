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
