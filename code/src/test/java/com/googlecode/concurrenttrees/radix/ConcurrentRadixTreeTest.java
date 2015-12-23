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
package com.googlecode.concurrenttrees.radix;

import com.googlecode.concurrenttrees.common.CharSequences;
import com.googlecode.concurrenttrees.common.Iterables;
import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.common.PrettyPrinter;
import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.voidvalue.VoidValue;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;
import com.googlecode.concurrenttrees.testutil.TestUtility;
import junit.framework.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
/**
 * @author Niall Gallagher
 */
public class ConcurrentRadixTreeTest {

    private final NodeFactory nodeFactory = new DefaultCharArrayNodeFactory();

    protected NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    @Test
    public void testBuildTreeByHand() {
        // Build the tree by hand, as if the following strings were added: B, BA, BAN, BANDANA, BANAN, BANANA

        //    ○
        //    └── ○ B (1)
        //        └── ○ A (2)
        //            └── ○ N (3)
        //                ├── ○ AN (5)
        //                │   └── ○ A (6)
        //                └── ○ DANA (4)

        final Node root, n1, n2, n3, n4, n5, n6;
        n6 = getNodeFactory().createNode("A", 6, Collections.<Node>emptyList(), false);
        n5 = getNodeFactory().createNode("AN", 5, Arrays.asList(n6), false);
        n4 = getNodeFactory().createNode("DANA", 4, Collections.<Node>emptyList(), false);
        n3 = getNodeFactory().createNode("N", 3, Arrays.asList(n4, n5), false); // note: it should sort alphabetically such that n5 is first
        n2 = getNodeFactory().createNode("A", 2, Arrays.asList(n3), false);
        n1 = getNodeFactory().createNode("B", 1, Arrays.asList(n2), false);
        //noinspection NullableProblems
        root = getNodeFactory().createNode("", null, Arrays.asList(n1), true);

        String expected =
                "○\n" +
                "└── ○ B (1)\n" +
                "    └── ○ A (2)\n" +
                "        └── ○ N (3)\n" +
                "            ├── ○ AN (5)\n" +
                "            │   └── ○ A (6)\n" +
                "            └── ○ DANA (4)\n";

        String actual = PrettyPrinter.prettyPrint(wrapNodeForPrinting(root));
        assertEquals(expected, actual);
    }

    @Test
    public void testPut_AddToRoot() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("A", 1);
        String expected =
                "○\n" +
                "└── ○ A (1)\n";
        String actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testPut_ChildNodeSorting() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("B", 1);
        tree.put("A", 2);
        String expected =
                "○\n" +
                "├── ○ A (2)\n" +
                "└── ○ B (1)\n";
        String actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testPut_AppendChild() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("FOO", 1);
        tree.put("FOOBAR", 2);

        String expected =
                "○\n" +
                "└── ○ FOO (1)\n" +
                "    └── ○ BAR (2)\n";
        String actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testPut_SplitEdge() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("FOOBAR", 1);
        tree.put("FOO", 2);

        String expected =
                "○\n" +
                "└── ○ FOO (2)\n" +
                "    └── ○ BAR (1)\n";
        String actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testPut_SplitWithImplicitNode() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("FOOBAR", 1);
        tree.put("FOOD", 2);

        String expected =
                "○\n" +
                "└── ○ FOO\n" + // We never explicitly inserted FOO
                "    ├── ○ BAR (1)\n" +
                "    └── ○ D (2)\n";
        String actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testPut_SplitAndMove() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        String expected =
                "○\n" +
                "└── ○ T\n" +             // implicit node added automatically
                "    ├── ○ E\n" +         // implicit node added automatically
                "    │   ├── ○ AM (2)\n" +
                "    │   └── ○ ST (1)\n" +
                "    └── ○ OAST (3)\n";
        String actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testPut_OverwriteValue() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());

        Integer existing;
        existing = tree.put("FOO", 1);
        assertNull(existing);
        existing = tree.put("FOO", 2);
        assertNotNull(existing);

        assertEquals(Integer.valueOf(1), existing);
        assertEquals(Integer.valueOf(2), tree.getValueForExactKey("FOO"));
    }

    @Test
    public void testPutIfAbsent_DoNotOverwriteValue() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());

        Integer existing = tree.putIfAbsent("FOO", 1);
        assertNull(existing);

        existing = tree.putIfAbsent("FOO", 2);
        assertNotNull(existing);

        assertEquals(Integer.valueOf(1), existing);
        assertEquals(Integer.valueOf(1), tree.getValueForExactKey("FOO"));
    }

    @Test
    public void testPutIfAbsent_SplitNode() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());

        //    ○
        //    └── ○ FOO             // implicit node added automatically
        //        ├── ○ BAR (1)
        //        └── ○ D (1)

        Integer existing;
        existing = tree.putIfAbsent("FOOBAR", 1);
        assertNull(existing);
        existing = tree.putIfAbsent("FOOD", 1);
        assertNull(existing);

        // This tests 'overwrite' set to true and exact match for node,
        // but no existing value to return (i.e. implicit node above)...

        //    ○
        //    └── ○ FOO (2)
        //        ├── ○ BAR (1)
        //        └── ○ D (1)

        existing = tree.putIfAbsent("FOO", 2);
        assertNull(existing);
    }

    @Test
    public void testPut_VoidValue_CharArrayNodeFactory() {
        ConcurrentRadixTree<VoidValue> tree = new ConcurrentRadixTree<VoidValue>(getNodeFactory());
        tree.put("FOO", VoidValue.SINGLETON);
        tree.put("FOOBAR", VoidValue.SINGLETON);
        String expected =
                "○\n" +
                "└── ○ FOO (-)\n" +
                "    └── ○ BAR (-)\n";
        String actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testPut_VoidValue_CharSequenceNodeFactory() {
        ConcurrentRadixTree<VoidValue> tree = new ConcurrentRadixTree<VoidValue>(new DefaultCharSequenceNodeFactory());
        tree.put("FOO", VoidValue.SINGLETON);
        tree.put("FOOBAR", VoidValue.SINGLETON);
        String expected =
                "○\n" +
                "└── ○ FOO (-)\n" +
                "    └── ○ BAR (-)\n";
        String actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutInternal_ArgumentValidation1() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        //noinspection NullableProblems
        tree.put(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutInternal_ArgumentValidation2() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        //noinspection NullableProblems
        tree.put("FOO", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutInternal_ArgumentValidation3() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        //noinspection NullableProblems
        tree.put("", 1);
    }

    @Test(expected = IllegalStateException.class)
    public void testPutInternal_InvalidClassification() {
        // Create a dummy subclass of SearchResult which returns an INVALID classification...
        class InvalidSearchResult extends ConcurrentRadixTree.SearchResult {
            InvalidSearchResult(CharSequence key, Node nodeFound, int charsMatched, int charsMatchedInNodeFound, Node parentNode, Node parentNodesParent) {
                super(key, nodeFound, charsMatched, charsMatchedInNodeFound, parentNode, parentNodesParent);
            }
            @Override
            protected Classification classify(CharSequence key, Node nodeFound, int charsMatched, int charsMatchedInNodeFound) {
                return Classification.INVALID;
            }
        }
        // Override searchTree() to return the InvalidSearchResult...
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory()) {
            @Override
            SearchResult searchTree(CharSequence key) {
                return new InvalidSearchResult("FOO", root, 4, 4, null, null);

            }
        };
        // We expect put() to throw an IllegalStateException
        // when it encounters the unsupported INVALID classification...
        tree.put("FOO", 1);
    }

    @Test
    public void testSize() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
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
    public void testGet() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        //    ○
        //    └── ○ T               // implicit node added automatically
        //        ├── ○ E           // implicit node added automatically
        //        │   ├── ○ AM (2)
        //        │   └── ○ ST (1)
        //        └── ○ OAST (3)

        assertEquals(Integer.valueOf(1), tree.getValueForExactKey("TEST"));
        assertEquals(Integer.valueOf(2), tree.getValueForExactKey("TEAM"));
        assertEquals(Integer.valueOf(3), tree.getValueForExactKey("TOAST"));
        assertNull(tree.getValueForExactKey("T"));
        assertNull(tree.getValueForExactKey("TE"));
        assertNull(tree.getValueForExactKey("E")); // sanity check, no such edge from root
        assertNull(tree.getValueForExactKey("")); // sanity check, root never has a value
    }

    @Test
    public void testRemove_MoreThanOneChildEdge() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("FOO", 1);
        tree.put("FOOBAR", 2);
        tree.put("FOOD", 3);

        //    ○
        //    └── ○ FOO (1)
        //        ├── ○ BAR (2)
        //        └── ○ D (3)
        
        String expected, actual;
        expected =
                "○\n" +
                "└── ○ FOO (1)\n" +
                "    ├── ○ BAR (2)\n" +
                "    └── ○ D (3)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);

        boolean removed = tree.remove("FOO");
        assertTrue(removed);

        //    ○
        //    └── ○ FOO         // value removed from FOO, but node needs to stay (as implicit node)
        //        ├── ○ BAR (2)
        //        └── ○ D (3)

        expected =
                "○\n" +
                "└── ○ FOO\n" +
                "    ├── ○ BAR (2)\n" +
                "    └── ○ D (3)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testRemove_ExactlyOneChildEdge() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("FOO", 1);
        tree.put("FOOBAR", 2);
        tree.put("FOOBARBAZ", 3);

        //    ○
        //    └── ○ FOO (1)
        //        └── ○ BAR (2)
        //            └── ○ BAZ (3)


        String expected, actual;
        expected =
                "○\n" +
                "└── ○ FOO (1)\n" +
                "    └── ○ BAR (2)\n" +
                "        └── ○ BAZ (3)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);

        boolean removed = tree.remove("FOO");
        assertTrue(removed);

        //    ○
        //    └── ○ FOOBAR (2)          // Edges FOO and BAR merged,
        //        └── ○ BAZ (3)         // and the value and child edges from BAR also copied into merged node

        expected =
                "○\n" +
                "└── ○ FOOBAR (2)\n" +
                "    └── ○ BAZ (3)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testRemove_ZeroChildEdges_DirectChildOfRoot() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("FOO", 1);
        tree.put("BAR", 2);

        //    ○
        //    ├── ○ BAR (2)
        //    └── ○ FOO (1)

        String expected, actual;
        expected =
                "○\n" +
                "├── ○ BAR (2)\n" +
                "└── ○ FOO (1)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);

        boolean removed = tree.remove("FOO");
        assertTrue(removed);

        //    ○                 // FOO removed, which involved recreating the root to change its child edges
        //    └── ○ BAR (2)

        expected =
                "○\n" +
                "└── ○ BAR (2)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testRemove_LastRemainingKey() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("FOO", 1);

        //    ○
        //    └── ○ FOO (1)

        String expected, actual;
        expected =
                "○\n" +
                "└── ○ FOO (1)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);

        boolean removed = tree.remove("FOO");
        assertTrue(removed);

        //    ○                 // FOO removed, which involved recreating the root with no remaining edges

        expected =
                "○\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testRemove_ZeroChildEdges_OneStepFromRoot() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("FOO", 1);
        tree.put("FOOBAR", 2);

        //    ○
        //    └── ○ FOO (1)
        //        └── ○ BAR (2)


        String expected, actual;
        expected =
                "○\n" +
                "└── ○ FOO (1)\n" +
                "    └── ○ BAR (2)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);

        boolean removed = tree.remove("FOOBAR");
        assertTrue(removed);

        //    ○
        //    └── ○ FOO (1)     // BAR removed, which involved recreating FOO and re-adding it to root node

        expected =
                "○\n" +
                "└── ○ FOO (1)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testRemove_ZeroChildEdges_SeveralStepsFromRoot() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("FOO", 1);
        tree.put("FOOBAR", 2);
        tree.put("FOOBARBAZ", 3);

        //    ○
        //    └── ○ FOO (1)
        //        └── ○ BAR (2)
        //            └── ○ BAZ (3)


        String expected, actual;
        expected =
                "○\n" +
                "└── ○ FOO (1)\n" +
                "    └── ○ BAR (2)\n" +
                "        └── ○ BAZ (3)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);

        boolean removed = tree.remove("FOOBARBAZ");
        assertTrue(removed);

        //    ○
        //    └── ○ FOO (1)
        //        └── ○ BAR (2) // BAZ removed, which involved recreating BAR and re-adding it to its parent FOO

        expected =
                "○\n" +
                "└── ○ FOO (1)\n" +
                "    └── ○ BAR (2)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testRemove_DoNotRemoveSplitNode() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("FOOBAR", 1);
        tree.put("FOOD", 2);

        //    ○
        //    └── ○ FOO             // implicit node added automatically
        //        ├── ○ BAR (1)
        //        └── ○ D (2)


        String expected, actual;
        expected =
                "○\n" +
                "└── ○ FOO\n" +
                "    ├── ○ BAR (1)\n" +
                "    └── ○ D (2)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);

        boolean removed = tree.remove("FOO");
        assertFalse(removed);

        expected =
                "○\n" +
                "└── ○ FOO\n" +             // we expect no change
                "    ├── ○ BAR (1)\n" +
                "    └── ○ D (2)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testRemove_MergeSplitNode() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        String expected, actual;
        expected =
                "○\n" +
                "└── ○ T\n" +
                "    ├── ○ E\n" +
                "    │   ├── ○ AM (2)\n" +
                "    │   └── ○ ST (1)\n" +
                "    └── ○ OAST (3)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);

        boolean removed = tree.remove("TEST");
        assertTrue(removed);

        expected =
                "○\n" +
                "└── ○ T\n" +
                "    ├── ○ EAM (2)\n" +
                "    └── ○ OAST (3)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testRemove_DoNotMergeSplitNodeWithValue() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);
        tree.put("TE", 4);

        String expected, actual;
        expected =
                "○\n" +
                "└── ○ T\n" +
                "    ├── ○ E (4)\n" +
                "    │   ├── ○ AM (2)\n" +
                "    │   └── ○ ST (1)\n" +
                "    └── ○ OAST (3)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);

        boolean removed = tree.remove("TEST");
        assertTrue(removed);

        expected =
                "○\n" +
                "└── ○ T\n" +
                "    ├── ○ E (4)\n" +
                "    │   └── ○ AM (2)\n" +
                "    └── ○ OAST (3)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testRemove_NoSuchKey() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("FOO", 1);
        tree.put("BAR", 2);

        String expected, actual;
        expected =
                "○\n" +
                "├── ○ BAR (2)\n" +
                "└── ○ FOO (1)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);

        boolean removed = tree.remove("BAZ");
        assertFalse(removed);

        expected =
                "○\n" +                     // we expect no change
                "├── ○ BAR (2)\n" +
                "└── ○ FOO (1)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetKeysForPrefix() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);
        tree.put("TEA", 4);
        tree.put("COFFEE", 5);

        //    ○
        //    ├── ○ COFFEE (5)
        //    └── ○ T
        //        ├── ○ E
        //        │   ├── ○ A (4)
        //        │   │   └── ○ M (2)
        //        │   └── ○ ST (1)
        //        └── ○ OAST (3)

        assertEquals("[COFFEE, TEA, TEAM, TEST, TOAST]", Iterables.toString(tree.getKeysStartingWith("")));
        assertEquals("[COFFEE]", Iterables.toString(tree.getKeysStartingWith("C")));
        assertEquals("[COFFEE]", Iterables.toString(tree.getKeysStartingWith("COFFEE")));
        assertEquals("[]", Iterables.toString(tree.getKeysStartingWith("COFFEES")));
        assertEquals("[TEA, TEAM, TEST, TOAST]", Iterables.toString(tree.getKeysStartingWith("T")));
        assertEquals("[TEA, TEAM, TEST]", Iterables.toString(tree.getKeysStartingWith("TE")));
        assertEquals("[TEA, TEAM]", Iterables.toString(tree.getKeysStartingWith("TEA")));
        assertEquals("[TOAST]", Iterables.toString(tree.getKeysStartingWith("TO")));
    }

    @Test
    public void testGetClosestKeys() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("COD", 1);
        tree.put("CODFISH", 2);
        tree.put("COFFEE", 3);

        //    ○
        //    └── ○ CO
        //        ├── ○ D (1)
        //        │   └── ○ FISH (2)
        //        └── ○ FFEE (3)

        assertEquals("[COD, CODFISH, COFFEE]", Iterables.toString(tree.getClosestKeys("COW")));
        assertEquals("[COD, CODFISH, COFFEE]", Iterables.toString(tree.getClosestKeys("CX")));
        assertEquals("[COD, CODFISH]", Iterables.toString(tree.getClosestKeys("COD")));
        assertEquals("[COFFEE]", Iterables.toString(tree.getClosestKeys("COF")));
        assertEquals("[]", Iterables.toString(tree.getClosestKeys("DO")));
        assertEquals("[CODFISH]", Iterables.toString(tree.getClosestKeys("CODFISHES")));
    }

    @Test
    public void testGetValuesForClosestKeys() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("COD", 1);
        tree.put("CODFISH", 2);
        tree.put("COFFEE", 3);

        //    ○
        //    └── ○ CO
        //        ├── ○ D (1)
        //        │   └── ○ FISH (2)
        //        └── ○ FFEE (3)

        assertEquals("[1, 2, 3]", Iterables.toString(tree.getValuesForClosestKeys("COW")));
        assertEquals("[1, 2, 3]", Iterables.toString(tree.getValuesForClosestKeys("CX")));
        assertEquals("[1, 2]", Iterables.toString(tree.getValuesForClosestKeys("COD")));
        assertEquals("[3]", Iterables.toString(tree.getValuesForClosestKeys("COF")));
        assertEquals("[]", Iterables.toString(tree.getValuesForClosestKeys("DO")));
        assertEquals("[2]", Iterables.toString(tree.getValuesForClosestKeys("CODFISHES")));
    }

    @Test
    public void testGetKeyValuePairsForClosestKeys() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("COD", 1);
        tree.put("CODFISH", 2);
        tree.put("COFFEE", 3);

        //    ○
        //    └── ○ CO
        //        ├── ○ D (1)
        //        │   └── ○ FISH (2)
        //        └── ○ FFEE (3)

        assertEquals("[(COD, 1), (CODFISH, 2), (COFFEE, 3)]", Iterables.toString(tree.getKeyValuePairsForClosestKeys("COW")));
        assertEquals("[(COD, 1), (CODFISH, 2), (COFFEE, 3)]", Iterables.toString(tree.getKeyValuePairsForClosestKeys("CX")));
        assertEquals("[(COD, 1), (CODFISH, 2)]", Iterables.toString(tree.getKeyValuePairsForClosestKeys("COD")));
        assertEquals("[(COFFEE, 3)]", Iterables.toString(tree.getKeyValuePairsForClosestKeys("COF")));
        assertEquals("[]", Iterables.toString(tree.getKeyValuePairsForClosestKeys("DO")));
        assertEquals("[(CODFISH, 2)]", Iterables.toString(tree.getKeyValuePairsForClosestKeys("CODFISHES")));
    }

    @Test
    public void testKeyValuePair_Accessor() {
        KeyValuePair<Integer> pair = new ConcurrentRadixTree.KeyValuePairImpl<Integer>("FOO", 5);
        assertEquals(pair.getKey(), "FOO");
        assertEquals(pair.getValue(), Integer.valueOf(5));
        assertEquals("(FOO, 5)", pair.toString());
    }

    @Test
    public void testKeyValuePair_EqualsAndHashCode() {
        KeyValuePair<Integer> pair1 = new ConcurrentRadixTree.KeyValuePairImpl<Integer>("FOO", 5);
        KeyValuePair<Integer> pair2 = new ConcurrentRadixTree.KeyValuePairImpl<Integer>("FOO", 6);
        KeyValuePair<Integer> pair3 = new ConcurrentRadixTree.KeyValuePairImpl<Integer>("BAR", 5);
        assertTrue(pair1.equals(pair1));
        assertTrue(pair1.equals(pair2));
        assertFalse(pair1.equals(pair3));
        //noinspection NullableProblems,ObjectEqualsNull
        assertFalse(pair1.equals(null));
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(pair1.equals("FOO"));
        assertTrue(pair1.hashCode() == pair2.hashCode());
        assertFalse(pair1.hashCode() == pair3.hashCode());
    }

    @Test
    public void testGetValuesForPrefix() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);
        tree.put("TEA", 4);
        tree.put("COFFEE", 5);

        //    ○
        //    ├── ○ COFFEE (5)
        //    └── ○ T
        //        ├── ○ E
        //        │   ├── ○ A (4)
        //        │   │   └── ○ M (2)
        //        │   └── ○ ST (1)
        //        └── ○ OAST (3)

        assertEquals("[5, 4, 2, 1, 3]", Iterables.toString(tree.getValuesForKeysStartingWith("")));
        assertEquals("[5]", Iterables.toString(tree.getValuesForKeysStartingWith("C")));
        assertEquals("[5]", Iterables.toString(tree.getValuesForKeysStartingWith("COFFEE")));
        assertEquals("[]", Iterables.toString(tree.getValuesForKeysStartingWith("COFFEES")));
        assertEquals("[4, 2, 1, 3]", Iterables.toString(tree.getValuesForKeysStartingWith("T")));
        assertEquals("[4, 2, 1]", Iterables.toString(tree.getValuesForKeysStartingWith("TE")));
        assertEquals("[4, 2]", Iterables.toString(tree.getValuesForKeysStartingWith("TEA")));
        assertEquals("[3]", Iterables.toString(tree.getValuesForKeysStartingWith("TO")));
    }

    @Test
    public void testGetKeyValuePairsForPrefix() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);
        tree.put("TEA", 4);
        tree.put("COFFEE", 5);

        //    ○
        //    ├── ○ COFFEE (5)
        //    └── ○ T
        //        ├── ○ E
        //        │   ├── ○ A (4)
        //        │   │   └── ○ M (2)
        //        │   └── ○ ST (1)
        //        └── ○ OAST (3)

        assertEquals("[(COFFEE, 5), (TEA, 4), (TEAM, 2), (TEST, 1), (TOAST, 3)]", Iterables.toString(tree.getKeyValuePairsForKeysStartingWith("")));
        assertEquals("[(COFFEE, 5)]", Iterables.toString(tree.getKeyValuePairsForKeysStartingWith("C")));
        assertEquals("[(COFFEE, 5)]", Iterables.toString(tree.getKeyValuePairsForKeysStartingWith("COFFEE")));
        assertEquals("[]", Iterables.toString(tree.getKeyValuePairsForKeysStartingWith("COFFEES")));
        assertEquals("[(TEA, 4), (TEAM, 2), (TEST, 1), (TOAST, 3)]", Iterables.toString(tree.getKeyValuePairsForKeysStartingWith("T")));
        assertEquals("[(TEA, 4), (TEAM, 2), (TEST, 1)]", Iterables.toString(tree.getKeyValuePairsForKeysStartingWith("TE")));
        assertEquals("[(TEA, 4), (TEAM, 2)]", Iterables.toString(tree.getKeyValuePairsForKeysStartingWith("TEA")));
        assertEquals("[(TOAST, 3)]", Iterables.toString(tree.getKeyValuePairsForKeysStartingWith("TO")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemove_ArgumentValidation() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        //noinspection NullableProblems
        tree.remove(null);
    }

    @Test
    public void testSearchTree() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory());
        // Build the tree by hand, as if the following strings were added: B, BA, BAN, BANDANA, BANAN, BANANA
        
        //    ○
        //    └── ○ B (1)
        //        └── ○ A (2)
        //            └── ○ N (3)
        //                ├── ○ AN (5)
        //                │   └── ○ A (6)
        //                └── ○ DANA (4)

        final Node root, n1, n2, n3, n4, n5, n6;
        n6 = getNodeFactory().createNode("A", 6, Collections.<Node>emptyList(), false);
        n5 = getNodeFactory().createNode("AN", 5, Arrays.asList(n6), false);
        n4 = getNodeFactory().createNode("DANA", 4, Collections.<Node>emptyList(), false);
        n3 = getNodeFactory().createNode("N", 3, Arrays.asList(n4, n5), false); // note: it should sort these such that n5 is first
        n2 = getNodeFactory().createNode("A", 2, Arrays.asList(n3), false);
        n1 = getNodeFactory().createNode("B", 1, Arrays.asList(n2), false);
        //noinspection NullableProblems
        root = getNodeFactory().createNode("", null, Arrays.asList(n1), true);

        // Overwrite the tree's default root with the one built by hand...
        tree.root = root;

        // Sanity checks to assert that we built tree as expected....
        String expected =
                "○\n" +
                "└── ○ B (1)\n" +
                "    └── ○ A (2)\n" +
                "        └── ○ N (3)\n" +
                "            ├── ○ AN (5)\n" +
                "            │   └── ○ A (6)\n" +
                "            └── ○ DANA (4)\n";
        
        assertEquals(expected, PrettyPrinter.prettyPrint(tree));
        assertEquals(2, n3.getOutgoingEdges().size());
        assertTrue(n3.getOutgoingEdges().contains(n4));
        assertTrue(n3.getOutgoingEdges().contains(n5));

        // Search for non-existing node. Should return root, with null parent, 0 charsMatched...
        assertEquals(tree.root, tree.searchTree("Z").nodeFound);
        assertNull(tree.searchTree("Z").parentNode);
        assertNull(tree.searchTree("Z").parentNodesParent);
        assertEquals(0, tree.searchTree("Z").charsMatched);

        // Search for first child node "B". Should return n1, parent should be root, 1 charsMatched...
        assertEquals(n1, tree.searchTree("B").nodeFound);
        assertEquals(tree.root, tree.searchTree("B").parentNode);
        assertEquals(null, tree.searchTree("B").parentNodesParent);
        assertEquals(1, tree.searchTree("B").charsMatched);

        // Search for node with split and multi-char child node at "BAN". Should return n3, parent n2, 3 charsMatched...
        assertEquals(n3, tree.searchTree("BAN").nodeFound);
        assertEquals(n2, tree.searchTree("BAN").parentNode);
        assertEquals(n1, tree.searchTree("BAN").parentNodesParent);
        assertEquals(3, tree.searchTree("BAN").charsMatched);

        // Search for node with multi-char label (exact match) at "BANAN". Should return n5, parent n3, 5 charsMatched...
        assertEquals(n5, tree.searchTree("BANAN").nodeFound);
        assertEquals(n3, tree.searchTree("BANAN").parentNode);
        assertEquals(n2, tree.searchTree("BANAN").parentNodesParent);
        assertEquals(5, tree.searchTree("BANAN").charsMatched);

        // Search for node with multi-char label (inexact match) at "BANA". Should return n5, parent n3, 4 charsMatched...
        assertEquals(n5, tree.searchTree("BANA").nodeFound);
        assertEquals(n3, tree.searchTree("BANA").parentNode);
        assertEquals(n2, tree.searchTree("BANA").parentNodesParent);
        assertEquals(4, tree.searchTree("BANA").charsMatched);

        // Search for the last node in "BANANA". Should return n6, parent n5, 6 charsMatched...
        assertEquals(n6, tree.searchTree("BANANA").nodeFound);
        assertEquals(n5, tree.searchTree("BANANA").parentNode);
        assertEquals(n3, tree.searchTree("BANANA").parentNodesParent);
        assertEquals(6, tree.searchTree("BANANA").charsMatched);

        // Search for string longer than anything in tree, differing after leaf node "BANANA".
        // Should return n6, parent n5, 6 chars matched...
        assertEquals(n6, tree.searchTree("BANANAS").nodeFound);
        assertEquals(n5, tree.searchTree("BANANAS").parentNode);
        assertEquals(n3, tree.searchTree("BANANAS").parentNodesParent);
        assertEquals(6, tree.searchTree("BANANAS").charsMatched);

        // Search for string longer than anything in tree, differing before split at "BAN".
        // Should return n2, parent n1, 2 chars matched...
        assertEquals(n2, tree.searchTree("BAR").nodeFound);
        assertEquals(n1, tree.searchTree("BAR").parentNode);
        assertEquals(tree.root, tree.searchTree("BAR").parentNodesParent);
        assertEquals(2, tree.searchTree("BAR").charsMatched);

        // Search for string longer than anything in tree, differing immediately after split at "BAN".
        // Should return n3, parent n2, 3 chars matched...
        assertEquals(n3, tree.searchTree("BANS").nodeFound);
        assertEquals(n2, tree.searchTree("BANS").parentNode);
        assertEquals(n1, tree.searchTree("BANS").parentNodesParent);
        assertEquals(3, tree.searchTree("BANS").charsMatched);

        // Search for string longer than anything in tree, differing in multi-char node "BANDANA".
        // Should return n4, parent n3, 5 chars matched...
        assertEquals(n4, tree.searchTree("BANDAIDS").nodeFound);
        assertEquals(n3, tree.searchTree("BANDAIDS").parentNode);
        assertEquals(n2, tree.searchTree("BANDAIDS").parentNodesParent);
        assertEquals(5, tree.searchTree("BANDAIDS").charsMatched);
    }

    @Test(expected = IllegalStateException.class)
    public void testSearchResult_FailureToClassify1() {
        // Testing the various (unlikely) ways to fall through classification to have the exception thrown...
        new ConcurrentRadixTree.SearchResult("DUMMY", null, 70, 70, null, null);
    }

    @Test(expected = IllegalStateException.class)
    public void testSearchResult_FailureToClassify2() {
        // Testing the various (unlikely) ways to fall through classification to have the exception thrown...
        Node dummyNodeFound = getNodeFactory().createNode("DUMMY", 1, Collections.<Node>emptyList(), false);
        new ConcurrentRadixTree.SearchResult("DUMMY", dummyNodeFound, 5, 70, null, null);

    }

    @Test(expected = IllegalStateException.class)
    public void testSearchResult_FailureToClassify3() {
        // Testing the various (unlikely) ways to fall through classification to have the exception thrown...
        Node dummyNodeFound = getNodeFactory().createNode("DUMMY", 1, Collections.<Node>emptyList(), false);
            new ConcurrentRadixTree.SearchResult("DUMMY", dummyNodeFound, 4, 70, null, null);
    }

    @Test
    public void testRestrictConcurrency() {
        ConcurrentRadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(getNodeFactory(), true);
        tree.acquireReadLockIfNecessary();
        tree.releaseReadLockIfNecessary();
    }

    @Test
    public void testSerialization() {
        ConcurrentRadixTree<Integer> tree1 = new ConcurrentRadixTree<Integer>(getNodeFactory());
        tree1.put("TEST", 1);
        tree1.put("TEAM", 2);
        tree1.put("TOAST", 3);

        ConcurrentRadixTree<Integer> tree2 = TestUtility.deserialize(ConcurrentRadixTree.class, TestUtility.serialize(tree1));
        assertEquals(PrettyPrinter.prettyPrint(tree1), PrettyPrinter.prettyPrint(tree2));
    }

    private static PrettyPrintable wrapNodeForPrinting(final Node node) {
        return new PrettyPrintable() {
            @Override
            public Node getNode() {
                return node;
            }
        };
    }
}
