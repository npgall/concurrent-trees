package com.googlecode.concurrenttrees.examples.shakespeare;


import com.googlecode.concurrenttrees.common.PrettyPrintUtil;
import com.googlecode.concurrenttrees.examples.shakespeare.util.IOUtil;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory;
import com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Niall Gallagher
 */
public class BuildShakespeareSinglePlaySuffixTree {

    static final List<String> files = Arrays.asList(
        "/shakespeare/tragedies/antony_and_cleopatra.txt"
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
        DummyAppendable dummyAppendable = new DummyAppendable();
        PrettyPrintUtil.prettyPrint(tree, dummyAppendable);
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
