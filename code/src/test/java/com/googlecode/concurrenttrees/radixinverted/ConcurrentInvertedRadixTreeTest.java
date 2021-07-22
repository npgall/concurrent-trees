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
package com.googlecode.concurrenttrees.radixinverted;

import com.googlecode.concurrenttrees.common.Iterables;
import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.common.PrettyPrinter;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.testutil.TestUtility;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Niall Gallagher
 */
public class ConcurrentInvertedRadixTreeTest {

    private final NodeFactory nodeFactory = new DefaultCharArrayNodeFactory();

    protected NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    @Test
    public void testPut() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());
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
    public void testPutIfAbsent() {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());

        Integer existing = tree.putIfAbsent("FOO", 1);
        assertNull(existing);

        existing = tree.putIfAbsent("FOO", 2);
        assertNotNull(existing);

        assertEquals(Integer.valueOf(1), existing);
        assertEquals(Integer.valueOf(1), tree.getValueForExactKey("FOO"));
    }

    @Test
    public void testRemove() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());
        tree.put("FOO", 1);
        tree.put("FOOBAR", 2);

        assertEquals(Integer.valueOf(1), tree.getValueForExactKey("FOO"));
        assertEquals(Integer.valueOf(2), tree.getValueForExactKey("FOOBAR"));

        boolean removed = tree.remove("FOO");
        assertTrue(removed);

        assertNull(tree.getValueForExactKey("FOO"));
        assertEquals(Integer.valueOf(2), tree.getValueForExactKey("FOOBAR"));
    }

    @Test
    public void testGetKeysPrefixing() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());

        tree.put("1234567", 1);
        tree.put("1234568", 2);
        tree.put("123", 3);

        //    ○
        //    └── ○ 123 (3)
        //        └── ○ 456
        //            ├── ○ 7 (1)
        //            └── ○ 8 (2)

        assertEquals("[123, 1234567]", Iterables.toString(tree.getKeysPrefixing("1234567")));
        assertEquals("[123, 1234567]", Iterables.toString(tree.getKeysPrefixing("12345670")));
        assertEquals("[123, 1234568]", Iterables.toString(tree.getKeysPrefixing("1234568")));
        assertEquals("[123, 1234568]", Iterables.toString(tree.getKeysPrefixing("12345680")));
        assertEquals("[123]", Iterables.toString(tree.getKeysPrefixing("1234569")));
        assertEquals("[123]", Iterables.toString(tree.getKeysPrefixing("123456")));
        assertEquals("[123]", Iterables.toString(tree.getKeysPrefixing("123")));
        assertEquals("[]", Iterables.toString(tree.getKeysPrefixing("12")));
        assertEquals("[]", Iterables.toString(tree.getKeysPrefixing("")));
    }

    @Test
    public void testGetValuesForKeysPrefixing() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());

        tree.put("1234567", 1);
        tree.put("1234568", 2);
        tree.put("123", 3);

        //    ○
        //    └── ○ 123 (3)
        //        └── ○ 456
        //            ├── ○ 7 (1)
        //            └── ○ 8 (2)

        assertEquals("[3, 1]", Iterables.toString(tree.getValuesForKeysPrefixing("1234567")));
        assertEquals("[3, 1]", Iterables.toString(tree.getValuesForKeysPrefixing("12345670")));
        assertEquals("[3, 2]", Iterables.toString(tree.getValuesForKeysPrefixing("1234568")));
        assertEquals("[3, 2]", Iterables.toString(tree.getValuesForKeysPrefixing("12345680")));
        assertEquals("[3]", Iterables.toString(tree.getValuesForKeysPrefixing("1234569")));
        assertEquals("[3]", Iterables.toString(tree.getValuesForKeysPrefixing("123456")));
        assertEquals("[3]", Iterables.toString(tree.getValuesForKeysPrefixing("123")));
        assertEquals("[]", Iterables.toString(tree.getValuesForKeysPrefixing("12")));
        assertEquals("[]", Iterables.toString(tree.getValuesForKeysPrefixing("")));
    }

    @Test
    public void testGetKeyValuePairsForKeysPrefixing() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());

        tree.put("1234567", 1);
        tree.put("1234568", 2);
        tree.put("123", 3);

        //    ○
        //    └── ○ 123 (3)
        //        └── ○ 456
        //            ├── ○ 7 (1)
        //            └── ○ 8 (2)

        assertEquals("[(123, 3), (1234567, 1)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("1234567")));
        assertEquals("[(123, 3), (1234567, 1)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("12345670")));
        assertEquals("[(123, 3), (1234568, 2)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("1234568")));
        assertEquals("[(123, 3), (1234568, 2)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("12345680")));
        assertEquals("[(123, 3)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("1234569")));
        assertEquals("[(123, 3)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("123456")));
        assertEquals("[(123, 3)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("123")));
        assertEquals("[]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("12")));
        assertEquals("[]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("")));
    }

    @Test
    public void testGetKeyValuePairsForKeysPrefixing_EdgeCases1() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());

        tree.put("/a/b/", 1);
        tree.put("/a/blob/", 2);
        tree.put("/a/blog/", 3);

        //    ○
        //    └── ○ /a/b
        //        ├── ○ / (1)
        //        └── ○ lo
        //            ├── ○ b/ (2)
        //            └── ○ g/ (3)

        assertEquals("[]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/")));
        assertEquals("[]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/a/")));
        assertEquals("[(/a/b/, 1)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/a/b/")));
        assertEquals("[]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/a/bl/")));
        assertEquals("[]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/a/blo/")));
        assertEquals("[(/a/blob/, 2)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/a/blob/")));
        assertEquals("[(/a/blog/, 3)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/a/blog/")));
        assertEquals("[(/a/blog/, 3)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/a/blog/s")));
    }

    @Test
    public void testGetKeyValuePairsForKeysPrefixing_EdgeCases2() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());

        tree.put("/a/b", 1);
        tree.put("/a/blob", 2);
        tree.put("/a/blog", 3);

        //    ○
        //    └── ○ /a/b (1)
        //        └── ○ lo
        //            ├── ○ b (2)
        //            └── ○ g (3)

        assertEquals("[]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/")));
        assertEquals("[]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/a")));
        assertEquals("[(/a/b, 1)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/a/b")));
        assertEquals("[(/a/b, 1)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/a/bl")));
        assertEquals("[(/a/b, 1)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/a/blo")));
        assertEquals("[(/a/b, 1), (/a/blob, 2)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/a/blob")));
        assertEquals("[(/a/b, 1), (/a/blog, 3)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/a/blog")));
        assertEquals("[(/a/b, 1), (/a/blog, 3)]", Iterables.toString(tree.getKeyValuePairsForKeysPrefixing("/a/blogs")));
    }

    @Test
    public void testGetValueForLongestKeyPrefixing() {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());

        tree.put("COD", 1);
        tree.put("CODFISH", 2);
        tree.put("COFFEE", 3);
        tree.put("CODFISHES", 4);

        //    ○
        //    └── ○ CO
        //        ├── ○ D (1)
        //        │   └── ○ FISH (2)
        //        │       └── ○ ES (4)
        //        └── ○ FFEE (3)

        assertEquals(new Integer(1), tree.getValueForLongestKeyPrefixing("COD"));
        assertEquals(new Integer(2), tree.getValueForLongestKeyPrefixing("CODFISH"));
        assertEquals(new Integer(4), tree.getValueForLongestKeyPrefixing("CODFISHES"));
        assertEquals(new Integer(1), tree.getValueForLongestKeyPrefixing("CODFUNKY"));
        assertEquals(new Integer(2), tree.getValueForLongestKeyPrefixing("CODFISHING"));
        assertNull(tree.getValueForLongestKeyPrefixing("DOESNOTEXIST"));
        assertNull(tree.getValueForLongestKeyPrefixing("C"));
        assertNull(tree.getValueForLongestKeyPrefixing("CO"));
        assertNull(tree.getValueForLongestKeyPrefixing(""));
    }

    @Test
    public void testGetLongestKeyPrefixing() {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());

        tree.put("COD", 1);
        tree.put("CODFISH", 2);
        tree.put("COFFEE", 3);
        tree.put("CODFISHES", 4);

        //    ○
        //    └── ○ CO
        //        ├── ○ D (1)
        //        │   └── ○ FISH (2)
        //        │       └── ○ ES (4)
        //        └── ○ FFEE (3)

        assertEquals("COD", tree.getLongestKeyPrefixing("COD"));
        assertEquals("CODFISH", tree.getLongestKeyPrefixing("CODFISH"));
        assertEquals("CODFISHES", tree.getLongestKeyPrefixing("CODFISHES"));
        assertEquals("COD", tree.getLongestKeyPrefixing("CODFUNKY"));
        assertEquals("CODFISH", tree.getLongestKeyPrefixing("CODFISHING"));
        assertNull(tree.getValueForLongestKeyPrefixing("DOESNOTEXIST"));
        assertNull(tree.getValueForLongestKeyPrefixing("C"));
        assertNull(tree.getValueForLongestKeyPrefixing("CO"));
        assertNull(tree.getValueForLongestKeyPrefixing(""));
    }

    @Test
    public void testGetKeyValuePairForLongestKeyPrefixing() {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());

        tree.put("COD", 1);
        tree.put("CODFISH", 2);
        tree.put("COFFEE", 3);
        tree.put("CODFISHES", 4);

        //    ○
        //    └── ○ CO
        //        ├── ○ D (1)
        //        │   └── ○ FISH (2)
        //        │       └── ○ ES (4)
        //        └── ○ FFEE (3)

        assertEquals("(COD, 1)", tree.getKeyValuePairForLongestKeyPrefixing("COD").toString());
        assertEquals("(CODFISH, 2)", tree.getKeyValuePairForLongestKeyPrefixing("CODFISH").toString());
        assertEquals("(CODFISHES, 4)", tree.getKeyValuePairForLongestKeyPrefixing("CODFISHES").toString());
        assertEquals("(COD, 1)", tree.getKeyValuePairForLongestKeyPrefixing("CODFUNKY").toString());
        assertEquals("(CODFISH, 2)", tree.getKeyValuePairForLongestKeyPrefixing("CODFISHING").toString());
        assertNull(tree.getKeyValuePairForLongestKeyPrefixing("DOESNOTEXIST"));
        assertNull(tree.getKeyValuePairForLongestKeyPrefixing("C"));
        assertNull(tree.getKeyValuePairForLongestKeyPrefixing("CO"));
        assertNull(tree.getKeyValuePairForLongestKeyPrefixing(""));
    }

    @Test
    public void testGetKeysContainedIn() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());
        tree.put("see", 1);
        tree.put("think", 2);
        tree.put("never", 3);
        tree.put("tree", 4);
        tree.put("poem", 5);

        String document =
                "i think that i shall never see\n" +
                "a poem lovely as a tree";

        assertEquals("[think, never, see, poem, tree]", Iterables.toString(tree.getKeysContainedIn(document)));
    }

    @Test
    public void testGetValuesForKeysContainedIn() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());
        tree.put("see", 1);
        tree.put("think", 2);
        tree.put("never", 3);
        tree.put("tree", 4);
        tree.put("poem", 5);

        String document =
                "i think that i shall never see\n" +
                "a poem lovely as a tree";

        assertEquals("[2, 3, 1, 5, 4]", Iterables.toString(tree.getValuesForKeysContainedIn(document)));
    }

    @Test
    public void testGetKeyValuePairsForKeysContainedIn() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());
        tree.put("see", 1);
        tree.put("think", 2);
        tree.put("never", 3);
        tree.put("tree", 4);
        tree.put("poem", 5);

        String document =
                "i think that i shall never see\n" +
                "a poem lovely as a tree";

        assertEquals("[(think, 2), (never, 3), (see, 1), (poem, 5), (tree, 4)]", Iterables.toString(tree.getKeyValuePairsForKeysContainedIn(document)));
    }

    @Test
    public void testGetKeysContainedIn_ConsecutiveKeys() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());
        tree.put("FOO", 1);
        tree.put("FOOBAR", 2);

        String document =
                "BAZ FOOBAR BAZ";

        assertEquals("[FOO, FOOBAR]", Iterables.toString(tree.getKeysContainedIn(document)));
    }

    @Test
    public void testGetKeysContainedIn_NoKeys() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());

        String document =
                "BAZ BAZ BAZ";

        assertEquals("[]", Iterables.toString(tree.getKeysContainedIn(document)));
    }

    @Test
    public void testGetKeysContainedIn_NoMatches() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());
        tree.put("FOO", 1);
        tree.put("FOOBAR", 2);

        String document =
                "BAZ BAZ BAZ";

        assertEquals("[]", Iterables.toString(tree.getKeysContainedIn(document)));
    }

    @Test
    public void testGetKeysContainedIn_EmptyDocument() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());
        tree.put("FOO", 1);
        tree.put("FOOBAR", 2);

        String document =
                "";

        assertEquals("[]", Iterables.toString(tree.getKeysContainedIn(document)));
    }

    @Test
    public void testGetKeysContainedIn_PartialEdgeMatch() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());
        tree.put("FOO", 1);
        tree.put("FOOBAR", 2);

        String document =
                "FOOBA";

        assertEquals("[FOO]", Iterables.toString(tree.getKeysContainedIn(document)));
    }

    @Test
    public void testGetKeysContainedIn_OverlappingMatches() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());
        tree.put("FOOD", 1);
        tree.put("FOO", 2);

        String document =
                "FOOD";

        assertEquals("[FOO, FOOD]", Iterables.toString(tree.getKeysContainedIn(document)));
    }

    @Test
    public void testSize() {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());
        assertEquals(0, tree.size());
        tree.put("1234567", 1);
        assertEquals(1, tree.size());
        tree.put("1234568", 2);
        assertEquals(2, tree.size());
        tree.put("123", 3);
        assertEquals(3, tree.size());

        tree.remove("FOO");
        assertEquals(3, tree.size()); // no change
        tree.remove("123");
        assertEquals(2, tree.size());
        tree.remove("1234568");
        assertEquals(1, tree.size());
        tree.remove("1234567");
        assertEquals(0, tree.size());
    }

    @Test
    public void testInheritedMethods() {
        // Basic tests for methods inherited from ConcurrentRadixTree, complete coverage in ConcurrentRadixTreeTest...
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());
        tree.put("FOOD", 1);
        tree.put("FOO", 2);

        assertEquals("[FOO, FOOD]", Iterables.toString(tree.getKeysStartingWith("F")));
        assertEquals("[2, 1]", Iterables.toString(tree.getValuesForKeysStartingWith("F")));
        assertEquals("[(FOO, 2), (FOOD, 1)]", Iterables.toString(tree.getKeyValuePairsForKeysStartingWith("F")));

        assertEquals("[FOO, FOOD]", Iterables.toString(tree.getClosestKeys("FOB")));
        assertEquals("[2, 1]", Iterables.toString(tree.getValuesForClosestKeys("FOB")));
        assertEquals("[(FOO, 2), (FOOD, 1)]", Iterables.toString(tree.getKeyValuePairsForClosestKeys("FOB")));
    }

    @Test
    public void testIteration() {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        Iterator<KeyValuePair<Integer>> it = tree.iterator();
        assertTrue(it.hasNext());
        KeyValuePair<Integer> team = it.next();
        assertEquals("TEAM", team.getKey());
        assertEquals(2, (Object) team.getValue());
        assertTrue(it.hasNext());
        KeyValuePair<Integer> test = it.next();
        assertEquals("TEST", test.getKey());
        assertEquals(1, (Object) test.getValue());
        assertTrue(it.hasNext());
        KeyValuePair<Integer> toast = it.next();
        assertEquals("TOAST", toast.getKey());
        assertEquals(3, (Object) toast.getValue());
        assertFalse(it.hasNext());
    }

    @Test
    public void testSerialization() {
        ConcurrentInvertedRadixTree<Integer> tree1 = new ConcurrentInvertedRadixTree<Integer>(getNodeFactory());
        tree1.put("TEST", 1);
        tree1.put("TEAM", 2);
        tree1.put("TOAST", 3);

        ConcurrentInvertedRadixTree<Integer> tree2 = TestUtility.deserialize(ConcurrentInvertedRadixTree.class, TestUtility.serialize(tree1));
        assertEquals(PrettyPrinter.prettyPrint(tree1), PrettyPrinter.prettyPrint(tree2));
    }
}
