package com.googlecode.concurrenttrees.solver;

import com.googlecode.concurrenttrees.common.PrettyPrintUtil;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Niall Gallagher
 */
public class LCSubstringSolverTest {

    final String document1 =
            "albert einstein, was a german theoretical physicist who developed the theory of general relativity";

    final String document2 =
            "near the beginning of his career, albert einstein thought that newtonian mechanics was no longer " +
            "enough to reconcile the laws of classical mechanics with the laws of the electromagnetic field";

    final String document3 =
            "in late summer 1895, at the age of sixteen, albert einstein sat the entrance examinations for " +
            "the swiss federal polytechnic in zurich";


    @Test
    public void testGetLongestCommonSubstring() throws Exception {
        LCSubstringSolver solver = new LCSubstringSolver(new DefaultCharSequenceNodeFactory());

        solver.add(document1);
        solver.add(document2);
        solver.add(document3);

        String longestCommonSubstring = solver.getLongestCommonSubstring().toString();

        assertEquals("albert einstein", longestCommonSubstring);
    }




    @Test
    public void testAddSuffixesToRadixTree_DuplicateHandling() {
        LCSubstringSolver solver = new LCSubstringSolver(new DefaultCharSequenceNodeFactory());
        solver.addSuffixesToRadixTree("FOO");
        // This would not really happen since add method prevents duplicates. Simulate adding duplicate to tree...
        // Would update existing document references instead of creating new...
        solver.addSuffixesToRadixTree("FOO");
        String expected =
                "○\n" +
                "├── ○ FOO ([FOO])\n" +
                "└── ○ O ([FOO])\n" +
                "    └── ○ O ([FOO])\n";
        Assert.assertEquals(expected, PrettyPrintUtil.prettyPrint(solver.suffixTree));
    }

    @Test
    public void testAdd_Duplicate() {
        LCSubstringSolver solver = new LCSubstringSolver(new DefaultCharSequenceNodeFactory());
        Assert.assertTrue(solver.add("FOO"));
        Assert.assertFalse(solver.add("FOO"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAdd_ArgumentValidation1() {
        LCSubstringSolver solver = new LCSubstringSolver(new DefaultCharSequenceNodeFactory());
        solver.add("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdd_ArgumentValidation2() {
        LCSubstringSolver solver = new LCSubstringSolver(new DefaultCharSequenceNodeFactory());
        //noinspection NullableProblems
        solver.add(null);
    }

    @Test
    public void testRestrictConcurrency() {
        LCSubstringSolver solver = new LCSubstringSolver(new DefaultCharSequenceNodeFactory(), true);
        assertNotNull(solver);
    }
}
