package com.googlecode.concurrenttrees.radixreversed;

import com.googlecode.concurrenttrees.common.PrettyPrintUtil;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultNodeFactory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Niall Gallagher
 */
public class ConcurrentReverseRadixTreeTest {

    private final NodeFactory nodeFactory = new DefaultNodeFactory();

    @Test
    public void testGet() throws Exception {
        ConcurrentReverseRadixTree<Integer> tree = new ConcurrentReverseRadixTree<Integer>(nodeFactory);
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        assertEquals(Integer.valueOf(2), tree.getValueForExactKey("TEAM"));
    }

    @Test
    public void testPut() throws Exception {
        ConcurrentReverseRadixTree<Integer> tree = new ConcurrentReverseRadixTree<Integer>(nodeFactory);
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        String expected =
                "○\n" +
                "├── ○ MAET (2)\n" +
                "└── ○ TS\n" +
                "    ├── ○ AOT (3)\n" +
                "    └── ○ ET (1)\n";
        String actual = PrettyPrintUtil.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testPutIfAbsent() throws Exception {
        ConcurrentReverseRadixTree<Integer> tree = new ConcurrentReverseRadixTree<Integer>(nodeFactory);
        tree.putIfAbsent("TEST", 1);
        tree.putIfAbsent("TEAM", 2);
        tree.putIfAbsent("TOAST", 3);
        tree.putIfAbsent("TEAM", 4); // should be ignored

        String expected =
                "○\n" +
                "├── ○ MAET (2)\n" +
                "└── ○ TS\n" +
                "    ├── ○ AOT (3)\n" +
                "    └── ○ ET (1)\n";
        String actual = PrettyPrintUtil.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetKeysEndingWith() throws Exception {
        ConcurrentReverseRadixTree<Integer> tree = new ConcurrentReverseRadixTree<Integer>(nodeFactory);
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        assertEquals("[TOAST, TEST]", tree.getKeysEndingWith("ST").toString());
        assertEquals("[TEAM]", tree.getKeysEndingWith("M").toString());
        assertEquals("[TEAM, TOAST, TEST]", tree.getKeysEndingWith("").toString());
        assertEquals("[]", tree.getKeysEndingWith("Z").toString());
    }

    @Test
    public void testGetValuesForKeysEndingWith() throws Exception {
        ConcurrentReverseRadixTree<Integer> tree = new ConcurrentReverseRadixTree<Integer>(nodeFactory);
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        assertEquals("[3, 1]", tree.getValuesForKeysEndingWith("ST").toString());
        assertEquals("[2]", tree.getValuesForKeysEndingWith("M").toString());
        assertEquals("[2, 3, 1]", tree.getValuesForKeysEndingWith("").toString());
        assertEquals("[]", tree.getValuesForKeysEndingWith("Z").toString());
    }

    @Test
    public void testGetKeyValuePairsForKeysEndingWith() throws Exception {
        ConcurrentReverseRadixTree<Integer> tree = new ConcurrentReverseRadixTree<Integer>(nodeFactory);
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        assertEquals("[(TOAST, 3), (TEST, 1)]", tree.getKeyValuePairsForKeysEndingWith("ST").toString());
        assertEquals("[(TEAM, 2)]", tree.getKeyValuePairsForKeysEndingWith("M").toString());
        assertEquals("[(TEAM, 2), (TOAST, 3), (TEST, 1)]", tree.getKeyValuePairsForKeysEndingWith("").toString());
        assertEquals("[]", tree.getKeyValuePairsForKeysEndingWith("Z").toString());
    }

    @Test
    public void testRemove() throws Exception {
        ConcurrentReverseRadixTree<Integer> tree = new ConcurrentReverseRadixTree<Integer>(nodeFactory);
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        String expected, actual;
        expected =
                "○\n" +
                "├── ○ MAET (2)\n" +
                "└── ○ TS\n" +
                "    ├── ○ AOT (3)\n" +
                "    └── ○ ET (1)\n";
        actual = PrettyPrintUtil.prettyPrint(tree);
        assertEquals(expected, actual);

        tree.remove("TEST");

        expected =
                "○\n" +
                "├── ○ MAET (2)\n" +
                "└── ○ TSAOT (3)\n";
        actual = PrettyPrintUtil.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testRestrictConcurrency() {
        ConcurrentReverseRadixTree<Integer> tree = new ConcurrentReverseRadixTree<Integer>(nodeFactory, true);
        assertNotNull(tree);
    }
}
