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
package com.googlecode.concurrenttrees.examples.shakespeare;


import com.googlecode.concurrenttrees.examples.shakespeare.util.IOUtil;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory;
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

    // This program needs 2-3GB of RAM with the DefaultCharSequenceNodeFactory (set -Xmx accordingly).
    // Example output in: test/resources/shakespeare-trees/tragedies-longest-common-substring.txt
    public static void main(String[] args) throws Exception {
        LCSubstringSolver solver = new LCSubstringSolver(new DefaultCharSequenceNodeFactory());
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
