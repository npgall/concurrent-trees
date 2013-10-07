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


import com.googlecode.concurrenttrees.common.PrettyPrinter;
import com.googlecode.concurrenttrees.examples.shakespeare.util.IOUtil;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory;
import com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Niall Gallagher
 */
public class BuildShakespeareTragediesSuffixTree {

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

    public static void main(String[] args) throws Exception {
        ConcurrentSuffixTree<String> tree = new ConcurrentSuffixTree<String>(new DefaultCharSequenceNodeFactory());
        for (String file : files) {
            String manuscript = IOUtil.loadTextFileFromClasspath(file, true, true, true); // true = convert to lowercase
            String manuscriptName = file.replaceAll("/.*/.*/", "").replace(".txt", "");
            tree.put(manuscript, manuscriptName);
            System.out.println("Added " + manuscriptName);
        }
        System.out.println("Built Suffix Tree. Estimating size on disk...");
        Thread.sleep(30000);
        DummyAppendable dummyAppendable = new DummyAppendable();
        PrettyPrinter.prettyPrint(tree, dummyAppendable);
        System.out.println("Done. Size on disk estimate:");
        System.out.println("Lines: " + dummyAppendable.lineCount);
        System.out.println("Characters: " + dummyAppendable.charCount);
    }

    static class DummyAppendable implements Appendable {

        public long lineCount = 0;
        public long charCount = 0;

        @Override
        public Appendable append(CharSequence csq) throws IOException {
            if (csq.length() > 0 && csq.charAt(csq.length() -1) == '\n') {
                lineCount++;
            }
            charCount+= csq.length();
            return this;
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) throws IOException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public Appendable append(char c) throws IOException {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}
