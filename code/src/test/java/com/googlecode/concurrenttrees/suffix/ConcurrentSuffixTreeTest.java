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
package com.googlecode.concurrenttrees.suffix;

import com.googlecode.concurrenttrees.common.Iterables;
import com.googlecode.concurrenttrees.common.PrettyPrinter;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.testutil.TestUtility;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * @author Niall Gallagher
 */
public class ConcurrentSuffixTreeTest {

    private final NodeFactory nodeFactory = new DefaultCharArrayNodeFactory();

    protected NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    @Test
    public void testPut_SingleKey() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();
        tree.put("BANANA", 1);

        // Suffixes:
        //
        //    BANANA
        //    ANANA
        //    NANA
        //    ANA
        //    NA
        //    A

        // Expected suffix tree:
        //
        //    ○
        //    ├── ○ A ([BANANA])
        //    │   └── ○ NA ([BANANA])
        //    │       └── ○ NA ([BANANA])
        //    ├── ○ BANANA ([BANANA])
        //    └── ○ NA ([BANANA])
        //        └── ○ NA ([BANANA])

        String expected =
                "○\n" +
                "├── ○ A ([BANANA])\n" +
                "│   └── ○ NA ([BANANA])\n" +
                "│       └── ○ NA ([BANANA])\n" +
                "├── ○ BANANA ([BANANA])\n" +
                "└── ○ NA ([BANANA])\n" +
                "    └── ○ NA ([BANANA])\n";
        String actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testPut_MultipleKeys() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();
        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        // Suffixes:
        //
        //    BANANA
        //    ANANA
        //    NANA
        //    ANA
        //    NA
        //    A
        //
        //    BANDANA
        //    ANDANA
        //    NDANA
        //    DANA
        //    ANA
        //    NA
        //    A

        // Expected suffix tree:
        //
        //    ○
        //    ├── ○ A ([BANANA, BANDANA])
        //    │   └── ○ N
        //    │       ├── ○ A ([BANANA, BANDANA])
        //    │       │   └── ○ NA ([BANANA])
        //    │       └── ○ DANA ([BANDANA])
        //    ├── ○ BAN
        //    │   ├── ○ ANA ([BANANA])
        //    │   └── ○ DANA ([BANDANA])
        //    ├── ○ DANA ([BANDANA])
        //    └── ○ N
        //        ├── ○ A ([BANANA, BANDANA])
        //        │   └── ○ NA ([BANANA])
        //        └── ○ DANA ([BANDANA])


        String expected =
                "○\n" +
                "├── ○ A ([BANANA, BANDANA])\n" +
                "│   └── ○ N\n" +
                "│       ├── ○ A ([BANANA, BANDANA])\n" +
                "│       │   └── ○ NA ([BANANA])\n" +
                "│       └── ○ DANA ([BANDANA])\n" +
                "├── ○ BAN\n" +
                "│   ├── ○ ANA ([BANANA])\n" +
                "│   └── ○ DANA ([BANDANA])\n" +
                "├── ○ DANA ([BANDANA])\n" +
                "└── ○ N\n" +
                "    ├── ○ A ([BANANA, BANDANA])\n" +
                "    │   └── ○ NA ([BANANA])\n" +
                "    └── ○ DANA ([BANDANA])\n";
        String actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testPut_ReplaceValue() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();
        tree.put("BANANA", 1);
        tree.put("BANANA", 2);

        String expected =
                "○\n" +
                "├── ○ A ([BANANA])\n" +
                "│   └── ○ NA ([BANANA])\n" +
                "│       └── ○ NA ([BANANA])\n" +
                "├── ○ BANANA ([BANANA])\n" +
                "└── ○ NA ([BANANA])\n" +
                "    └── ○ NA ([BANANA])\n";
        String actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
        assertEquals(Integer.valueOf(2), tree.getValueForExactKey("BANANA"));
    }

    @Test
    public void testPutIfAbsent() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();
        tree.putIfAbsent("BANANA", 1);
        tree.putIfAbsent("BANANA", 2); // should be ignored

        String expected =
                "○\n" +
                "├── ○ A ([BANANA])\n" +
                "│   └── ○ NA ([BANANA])\n" +
                "│       └── ○ NA ([BANANA])\n" +
                "├── ○ BANANA ([BANANA])\n" +
                "└── ○ NA ([BANANA])\n" +
                "    └── ○ NA ([BANANA])\n";
        String actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
        assertEquals(Integer.valueOf(1), tree.getValueForExactKey("BANANA"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPut_ArgumentValidation1() {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();
        //noinspection NullableProblems
        tree.put(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPut_ArgumentValidation2() {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();
        //noinspection NullableProblems
        tree.put("FOO", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPut_ArgumentValidation3() {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();
        //noinspection NullableProblems
        tree.put("", 1);
    }

    @Test
    public void testRemove_RemoveSecondKey() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        String expected, actual;
        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        expected =
                "○\n" +
                "├── ○ A ([BANANA, BANDANA])\n" +
                "│   └── ○ N\n" +
                "│       ├── ○ A ([BANANA, BANDANA])\n" +
                "│       │   └── ○ NA ([BANANA])\n" +
                "│       └── ○ DANA ([BANDANA])\n" +
                "├── ○ BAN\n" +
                "│   ├── ○ ANA ([BANANA])\n" +
                "│   └── ○ DANA ([BANDANA])\n" +
                "├── ○ DANA ([BANDANA])\n" +
                "└── ○ N\n" +
                "    ├── ○ A ([BANANA, BANDANA])\n" +
                "    │   └── ○ NA ([BANANA])\n" +
                "    └── ○ DANA ([BANDANA])\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);

        boolean removed = tree.remove("BANDANA");
        assertTrue(removed);

        expected =
                "○\n" +
                "├── ○ A ([BANANA])\n" +
                "│   └── ○ NA ([BANANA])\n" +
                "│       └── ○ NA ([BANANA])\n" +
                "├── ○ BANANA ([BANANA])\n" +
                "└── ○ NA ([BANANA])\n" +
                "    └── ○ NA ([BANANA])\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
        assertNull(tree.getValueForExactKey("BANDANA"));
    }

    @Test
    public void testRemove_RemoveFirstKey() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        String expected, actual;
        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        expected =
                "○\n" +
                "├── ○ A ([BANANA, BANDANA])\n" +
                "│   └── ○ N\n" +
                "│       ├── ○ A ([BANANA, BANDANA])\n" +
                "│       │   └── ○ NA ([BANANA])\n" +
                "│       └── ○ DANA ([BANDANA])\n" +
                "├── ○ BAN\n" +
                "│   ├── ○ ANA ([BANANA])\n" +
                "│   └── ○ DANA ([BANDANA])\n" +
                "├── ○ DANA ([BANDANA])\n" +
                "└── ○ N\n" +
                "    ├── ○ A ([BANANA, BANDANA])\n" +
                "    │   └── ○ NA ([BANANA])\n" +
                "    └── ○ DANA ([BANDANA])\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);

        boolean removed = tree.remove("BANANA");
        assertTrue(removed);

        expected =
                "○\n" +
                "├── ○ A ([BANDANA])\n" +
                "│   └── ○ N\n" +
                "│       ├── ○ A ([BANDANA])\n" +
                "│       └── ○ DANA ([BANDANA])\n" +
                "├── ○ BANDANA ([BANDANA])\n" +
                "├── ○ DANA ([BANDANA])\n" +
                "└── ○ N\n" +
                "    ├── ○ A ([BANDANA])\n" +
                "    └── ○ DANA ([BANDANA])\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
        assertNull(tree.getValueForExactKey("BANANA"));
    }

    @Test
    public void testRemove_RemoveNonExistentKey() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        String expected, actual;
        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        expected =
                "○\n" +
                "├── ○ A ([BANANA, BANDANA])\n" +
                "│   └── ○ N\n" +
                "│       ├── ○ A ([BANANA, BANDANA])\n" +
                "│       │   └── ○ NA ([BANANA])\n" +
                "│       └── ○ DANA ([BANDANA])\n" +
                "├── ○ BAN\n" +
                "│   ├── ○ ANA ([BANANA])\n" +
                "│   └── ○ DANA ([BANDANA])\n" +
                "├── ○ DANA ([BANDANA])\n" +
                "└── ○ N\n" +
                "    ├── ○ A ([BANANA, BANDANA])\n" +
                "    │   └── ○ NA ([BANANA])\n" +
                "    └── ○ DANA ([BANDANA])\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);

        boolean removed = tree.remove("APPLE");
        assertFalse(removed);

        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetValueForExactKey() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        assertEquals(Integer.valueOf(1), tree.getValueForExactKey("BANANA"));
        assertEquals(Integer.valueOf(2), tree.getValueForExactKey("BANDANA"));
        assertNull(tree.getValueForExactKey("BAN"));
        assertNull(tree.getValueForExactKey("ANA"));
    }

    @Test
    public void testSize() {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();
        assertEquals(0, tree.size());
        tree.put("TEST", 1);
        assertEquals(1, tree.size());
        tree.put("TEAM", 2);
        assertEquals(2, tree.size());
        tree.put("TOAST", 3);
        assertEquals(3, tree.size());

        tree.remove("FOO");
        assertEquals(3, tree.size()); // no change
        tree.remove("TOAST");
        assertEquals(2, tree.size());
        tree.remove("TEAM");
        assertEquals(1, tree.size());
        tree.remove("TEST");
        assertEquals(0, tree.size());
    }

    @Test
    public void testGetKeysEndingWith() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        assertEquals("[BANANA, BANDANA]", Iterables.toString(tree.getKeysEndingWith("ANA")));
        assertEquals("[BANDANA]", Iterables.toString(tree.getKeysEndingWith("DANA")));
        assertEquals("[]", Iterables.toString(tree.getKeysEndingWith("BAN")));
        assertEquals("[]", Iterables.toString(tree.getKeysEndingWith("")));
    }

    @Test
    public void testGetValuesForKeysEndingWith() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        assertEquals("[1, 2]", Iterables.toString(tree.getValuesForKeysEndingWith("ANA")));
        assertEquals("[2]", Iterables.toString(tree.getValuesForKeysEndingWith("DANA")));
        assertEquals("[]", Iterables.toString(tree.getValuesForKeysEndingWith("BAN")));
        assertEquals("[]", Iterables.toString(tree.getValuesForKeysEndingWith("")));
    }

    @Test
    public void testGetKeyValuePairsForKeysEndingWith() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        assertEquals("[(BANANA, 1), (BANDANA, 2)]", Iterables.toString(tree.getKeyValuePairsForKeysEndingWith("ANA")));
        assertEquals("[(BANDANA, 2)]", Iterables.toString(tree.getKeyValuePairsForKeysEndingWith("DANA")));
        assertEquals("[]", Iterables.toString(tree.getKeyValuePairsForKeysEndingWith("BAN")));
        assertEquals("[]", Iterables.toString(tree.getKeyValuePairsForKeysEndingWith("")));
    }

    @Test
    public void testGetKeysContaining() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        assertEquals("[BANANA]", Iterables.toString(tree.getKeysContaining("ANAN")));
        assertEquals("[BANDANA]", Iterables.toString(tree.getKeysContaining("DA")));
        assertEquals("[BANANA, BANDANA]", Iterables.toString(tree.getKeysContaining("AN")));
        assertEquals("[BANANA, BANDANA]", Iterables.toString(tree.getKeysContaining("BAN")));
        assertEquals("[BANANA, BANDANA]", Iterables.toString(tree.getKeysContaining("ANA")));
        assertEquals("[]", Iterables.toString(tree.getKeysContaining("APPLE")));
        assertEquals("[BANANA, BANDANA]", Iterables.toString(tree.getKeysContaining("")));
    }

    @Test
    public void testGetValuesForKeysContaining() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        assertEquals("[1]", Iterables.toString(tree.getValuesForKeysContaining("ANAN")));
        assertEquals("[2]", Iterables.toString(tree.getValuesForKeysContaining("DA")));
        assertEquals("[1, 2]", Iterables.toString(tree.getValuesForKeysContaining("AN")));
        assertEquals("[1, 2]", Iterables.toString(tree.getValuesForKeysContaining("BAN")));
        assertEquals("[1, 2]", Iterables.toString(tree.getValuesForKeysContaining("ANA")));
        assertEquals("[]", Iterables.toString(tree.getValuesForKeysContaining("APPLE")));
        assertEquals("[1, 2]", Iterables.toString(tree.getValuesForKeysContaining("")));
    }

    @Test
    public void testGetKeyValuePairsForKeysContaining() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        assertEquals("[(BANANA, 1)]", Iterables.toString(tree.getKeyValuePairsForKeysContaining("ANAN")));
        assertEquals("[(BANDANA, 2)]", Iterables.toString(tree.getKeyValuePairsForKeysContaining("DA")));
        assertEquals("[(BANANA, 1), (BANDANA, 2)]", Iterables.toString(tree.getKeyValuePairsForKeysContaining("AN")));
        assertEquals("[(BANANA, 1), (BANDANA, 2)]", Iterables.toString(tree.getKeyValuePairsForKeysContaining("BAN")));
        assertEquals("[(BANANA, 1), (BANDANA, 2)]", Iterables.toString(tree.getKeyValuePairsForKeysContaining("ANA")));
        assertEquals("[]", Iterables.toString(tree.getKeyValuePairsForKeysContaining("APPLE")));
        assertEquals("[(BANANA, 1), (BANDANA, 2)]", Iterables.toString(tree.getKeyValuePairsForKeysContaining("")));
    }

    @Test
    public void testRestrictConcurrency() {
        ConcurrentSuffixTree<Integer> tree = new ConcurrentSuffixTree<Integer>(getNodeFactory(), true);
        assertNotNull(tree);
    }

    @Test
    public void testCreateSetForOriginalKeys() {
        // Test the default (production) implementation of this method, should return a set based on ConcurrentHashMap...
        ConcurrentSuffixTree<Integer> tree = new ConcurrentSuffixTree<Integer>(getNodeFactory(), true);
        assertTrue(tree.createSetForOriginalKeys().getClass().equals(Collections.newSetFromMap(new ConcurrentHashMap<Object, Boolean>()).getClass()));
    }

    /**
     * Creates a new {@link ConcurrentSuffixTree} but overrides
     * {@link com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree#createSetForOriginalKeys()} to return a set
     * which provides consistent iteration order (useful for unit tests).
     */
    @SuppressWarnings({"JavaDoc"})
    <O> ConcurrentSuffixTree<O> newConcurrentSuffixTreeForUnitTests() {
        return new ConcurrentSuffixTree<O>(getNodeFactory()) {
            // Override this method to return a set which has consistent iteration order, for unit testing...
            @Override
            protected Set<String> createSetForOriginalKeys() {
                return new LinkedHashSet<String>();
            }
        };
    }

    @Test
    public void testSerialization() {
        ConcurrentSuffixTree<Integer> tree1 = new ConcurrentSuffixTreeTestImpl<Integer>(getNodeFactory());
        tree1.put("TEST", 1);
        tree1.put("TEAM", 2);
        tree1.put("TOAST", 3);

        ConcurrentSuffixTree<Integer> tree2 = TestUtility.deserialize(ConcurrentSuffixTree.class, TestUtility.serialize(tree1));
        assertEquals(PrettyPrinter.prettyPrint(tree1), PrettyPrinter.prettyPrint(tree2));
    }

    /**
     * Extends ConcurrentSuffixTree for testing purposes, to override{@link ConcurrentSuffixTree#createSetForOriginalKeys()}
     * in order to ensure deterministic ordering of original keys in the PrettyPrintable representation used in tests.
     * Note that ordering of original keys is an internal implementation detail and is externally not defined.
     */
    static class ConcurrentSuffixTreeTestImpl<T> extends ConcurrentSuffixTree<T> {
        public ConcurrentSuffixTreeTestImpl(NodeFactory nodeFactory) { super(nodeFactory); }
        @Override
        protected Set<String> createSetForOriginalKeys() {
            return new TreeSet<String>();
        }
    }
}
