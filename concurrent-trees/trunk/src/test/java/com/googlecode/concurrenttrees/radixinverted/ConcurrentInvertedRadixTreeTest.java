/**
 * Copyright 2012 Niall Gallagher
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
import com.googlecode.concurrenttrees.common.PrettyPrintUtil;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Niall Gallagher
 */
public class ConcurrentInvertedRadixTreeTest {

    private final NodeFactory nodeFactory = new DefaultCharArrayNodeFactory();

    @Test
    public void testPut() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(nodeFactory);
        tree.put("FOO", 1);
        tree.put("FOOBAR", 2);

        String expected =
                "○\n" +
                "└── ○ FOO (1)\n" +
                "    └── ○ BAR (2)\n";
        String actual = PrettyPrintUtil.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testPutIfAbsent() {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(nodeFactory);

        Integer existing = tree.putIfAbsent("FOO", 1);
        assertNull(existing);

        existing = tree.putIfAbsent("FOO", 2);
        assertNotNull(existing);

        assertEquals(Integer.valueOf(1), existing);
        assertEquals(Integer.valueOf(1), tree.getValueForExactKey("FOO"));
    }

    @Test
    public void testRemove() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(nodeFactory);
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
    public void testGetKeysContainedIn() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(nodeFactory);
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
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(nodeFactory);
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
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(nodeFactory);
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
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(nodeFactory);
        tree.put("FOO", 1);
        tree.put("FOOBAR", 2);

        String document =
                "BAZ FOOBAR BAZ";

        assertEquals("[FOO, FOOBAR]", Iterables.toString(tree.getKeysContainedIn(document)));
    }

    @Test
    public void testGetKeysContainedIn_NoKeys() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(nodeFactory);

        String document =
                "BAZ BAZ BAZ";

        assertEquals("[]", Iterables.toString(tree.getKeysContainedIn(document)));
    }

    @Test
    public void testGetKeysContainedIn_NoMatches() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(nodeFactory);
        tree.put("FOO", 1);
        tree.put("FOOBAR", 2);

        String document =
                "BAZ BAZ BAZ";

        assertEquals("[]", Iterables.toString(tree.getKeysContainedIn(document)));
    }

    @Test
    public void testGetKeysContainedIn_EmptyDocument() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(nodeFactory);
        tree.put("FOO", 1);
        tree.put("FOOBAR", 2);

        String document =
                "";

        assertEquals("[]", Iterables.toString(tree.getKeysContainedIn(document)));
    }

    @Test
    public void testGetKeysContainedIn_PartialEdgeMatch() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(nodeFactory);
        tree.put("FOO", 1);
        tree.put("FOOBAR", 2);

        String document =
                "FOOBA";

        assertEquals("[FOO]", Iterables.toString(tree.getKeysContainedIn(document)));
    }

    @Test
    public void testGetKeysContainedIn_OverlappingMatches() throws Exception {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(nodeFactory);
        tree.put("FOOD", 1);
        tree.put("FOO", 2);

        String document =
                "FOOD";

        assertEquals("[FOO, FOOD]", Iterables.toString(tree.getKeysContainedIn(document)));
    }

    @Test
    public void testRestrictConcurrency() {
        ConcurrentInvertedRadixTree<Integer> tree = new ConcurrentInvertedRadixTree<Integer>(nodeFactory, true);
        assertNotNull(tree);
    }
}
