package com.googlecode.concurrenttrees.suffix;

import com.googlecode.concurrenttrees.common.CharSequenceUtil;
import com.googlecode.concurrenttrees.common.PrettyPrintUtil;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultNodeFactory;
import com.googlecode.concurrenttrees.reverseradix.ConcurrentReverseRadixTree;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Niall Gallagher
 */
public class ConcurrentSuffixTreeTest {

    private final NodeFactory nodeFactory = new DefaultNodeFactory();

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
        String actual = PrettyPrintUtil.prettyPrint(tree);
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
        String actual = PrettyPrintUtil.prettyPrint(tree);
        assertEquals(expected, actual);
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
        String actual = PrettyPrintUtil.prettyPrint(tree);
        assertEquals(expected, actual);
        assertEquals(Integer.valueOf(1), tree.getValueForExactKey("BANANA"));
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
        actual = PrettyPrintUtil.prettyPrint(tree);
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
        actual = PrettyPrintUtil.prettyPrint(tree);
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
        actual = PrettyPrintUtil.prettyPrint(tree);
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
        actual = PrettyPrintUtil.prettyPrint(tree);
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
        actual = PrettyPrintUtil.prettyPrint(tree);
        assertEquals(expected, actual);

        boolean removed = tree.remove("APPLE");
        assertFalse(removed);

        actual = PrettyPrintUtil.prettyPrint(tree);
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
    public void testGetKeysEndingWith() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        assertEquals("[BANANA, BANDANA]", tree.getKeysEndingWith("ANA").toString());
        assertEquals("[BANDANA]", tree.getKeysEndingWith("DANA").toString());
        assertEquals("[]", tree.getKeysEndingWith("BAN").toString());
        assertEquals("[]", tree.getKeysEndingWith("").toString());
    }

    @Test
    public void testGetValuesForKeysEndingWith() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        assertEquals("[1, 2]", tree.getValuesForKeysEndingWith("ANA").toString());
        assertEquals("[2]", tree.getValuesForKeysEndingWith("DANA").toString());
        assertEquals("[]", tree.getValuesForKeysEndingWith("BAN").toString());
        assertEquals("[]", tree.getValuesForKeysEndingWith("").toString());
    }

    @Test
    public void testGetKeyValuePairsForKeysEndingWith() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        assertEquals("[(BANANA, 1), (BANDANA, 2)]", tree.getKeyValuePairsForKeysEndingWith("ANA").toString());
        assertEquals("[(BANDANA, 2)]", tree.getKeyValuePairsForKeysEndingWith("DANA").toString());
        assertEquals("[]", tree.getKeyValuePairsForKeysEndingWith("BAN").toString());
        assertEquals("[]", tree.getKeyValuePairsForKeysEndingWith("").toString());
    }

    @Test
    public void testGetKeysContaining() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        assertEquals("[BANANA]", tree.getKeysContaining("ANAN").toString());
        assertEquals("[BANDANA]", tree.getKeysContaining("DA").toString());
        assertEquals("[BANANA, BANDANA]", tree.getKeysContaining("AN").toString());
        assertEquals("[BANANA, BANDANA]", tree.getKeysContaining("BAN").toString());
        assertEquals("[BANANA, BANDANA]", tree.getKeysContaining("ANA").toString());
        assertEquals("[]", tree.getKeysContaining("APPLE").toString());
        assertEquals("[BANANA, BANDANA]", tree.getKeysContaining("").toString());
    }

    @Test
    public void testGetValuesForKeysContaining() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        assertEquals("[1]", tree.getValuesForKeysContaining("ANAN").toString());
        assertEquals("[2]", tree.getValuesForKeysContaining("DA").toString());
        assertEquals("[1, 2]", tree.getValuesForKeysContaining("AN").toString());
        assertEquals("[1, 2]", tree.getValuesForKeysContaining("BAN").toString());
        assertEquals("[1, 2]", tree.getValuesForKeysContaining("ANA").toString());
        assertEquals("[]", tree.getValuesForKeysContaining("APPLE").toString());
        assertEquals("[1, 2]", tree.getValuesForKeysContaining("").toString());
    }

    @Test
    public void testGetKeyValuePairsForKeysContaining() throws Exception {
        ConcurrentSuffixTree<Integer> tree = newConcurrentSuffixTreeForUnitTests();

        tree.put("BANANA", 1);
        tree.put("BANDANA", 2);

        assertEquals("[(BANANA, 1)]", tree.getKeyValuePairsForKeysContaining("ANAN").toString());
        assertEquals("[(BANDANA, 2)]", tree.getKeyValuePairsForKeysContaining("DA").toString());
        assertEquals("[(BANANA, 1), (BANDANA, 2)]", tree.getKeyValuePairsForKeysContaining("AN").toString());
        assertEquals("[(BANANA, 1), (BANDANA, 2)]", tree.getKeyValuePairsForKeysContaining("BAN").toString());
        assertEquals("[(BANANA, 1), (BANDANA, 2)]", tree.getKeyValuePairsForKeysContaining("ANA").toString());
        assertEquals("[]", tree.getKeyValuePairsForKeysContaining("APPLE").toString());
        assertEquals("[(BANANA, 1), (BANDANA, 2)]", tree.getKeyValuePairsForKeysContaining("").toString());
    }

    @Test
    public void testRestrictConcurrency() {
        ConcurrentSuffixTree<Integer> tree = new ConcurrentSuffixTree<Integer>(nodeFactory, true);
        assertNotNull(tree);
    }

    /**
     * Creates a new {@link ConcurrentSuffixTree} but overrides
     * {@link com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree#createSetForOriginalKeys()} to return a set
     * which provides consistent iteration order (useful for unit tests).
     */
    @SuppressWarnings({"JavaDoc"})
    <O> ConcurrentSuffixTree<O> newConcurrentSuffixTreeForUnitTests() {
        return new ConcurrentSuffixTree<O>(nodeFactory) {
            // Override this method to return a set which has consistent iteration order, for unit testing...
            @Override
            protected Set<String> createSetForOriginalKeys() {
                return new LinkedHashSet<String>();
            }
        };
    }
}
