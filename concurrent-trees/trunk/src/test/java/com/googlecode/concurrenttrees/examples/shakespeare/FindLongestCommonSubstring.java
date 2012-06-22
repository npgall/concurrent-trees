package com.googlecode.concurrenttrees.examples.shakespeare;


import com.googlecode.concurrenttrees.examples.shakespeare.util.IOUtil;
import com.googlecode.concurrenttrees.radix.node.concrete.NaiveCharSequenceNodeFactory;
import com.googlecode.concurrenttrees.solver.LCSubstringSolver;

import java.util.Arrays;
import java.util.List;

/**
 * Finds the longest common substring in Shakespeare's Tragedies.
 *
 * @author Niall Gallagher
 */
public class FindLongestCommonSubstring {

    static final List<String> files = Arrays.asList(
        "/shakespeare/tragedies/antony_and_cleopatra.txt",
        "/shakespeare/tragedies/coriolanus.txt",
        "/shakespeare/tragedies/hamlet.txt",
        "/shakespeare/tragedies/julius_caesar.txt",
        "/shakespeare/tragedies/king_lear.txt",
        "/shakespeare/tragedies/macbeth.txt",
        "/shakespeare/tragedies/othello.txt",
        "/shakespeare/tragedies/romeo_and_juliet.txt",
        "/shakespeare/tragedies/timon_of_athens.txt",
        "/shakespeare/tragedies/titus_andronicus.txt"
    );

    // This program needs 2-3GB of RAM with the NaiveCharSequenceNodeFactory (set -Xmx accordingly).
    // Example output in: test/resources/shakespeare-trees/tragedies-longest-common-substring.txt
    public static void main(String[] args) throws Exception {
        LCSubstringSolver solver = new LCSubstringSolver(new NaiveCharSequenceNodeFactory());
        System.out.println("Building suffix tree...");
        long startTime = System.nanoTime();
        for (String file : files) {
            // Load manuscript and strip punctuation, strip line breaks, convert to lowercase (respectively)..
            String manuscript = IOUtil.loadTextFileFromClasspath(file, true, true, true);
            String manuscriptName = file.replaceAll("/.*/.*/", "").replace(".txt", "");
            solver.add(manuscript);
            System.out.println("Added manuscript: " + manuscriptName);
        }
        System.out.println("Built suffix tree in " + ((System.nanoTime() - startTime)/1000000000) + " seconds.");
        long searchTime = System.nanoTime();
        System.out.println("Searching for longest common substring...");
        CharSequence longestCommonSubstring = solver.getLongestCommonSubstring();
        System.out.println("Found longest common substring in "
                + ((System.nanoTime() - searchTime)/1000000000) + " seconds.");
        System.out.println("Longest common substring: [" + longestCommonSubstring + "]");
    }
}
