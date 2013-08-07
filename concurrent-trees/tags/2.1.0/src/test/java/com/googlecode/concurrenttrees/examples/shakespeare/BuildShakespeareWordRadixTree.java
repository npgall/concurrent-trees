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
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Niall Gallagher
 */
public class BuildShakespeareWordRadixTree {

    static final List<String> files = Arrays.asList(
        "/shakespeare/comedies/a_midsummer_nights_dream.txt",
        "/shakespeare/comedies/alls_well_that_ends_well.txt",
        "/shakespeare/comedies/as_you_like_it.txt",
        "/shakespeare/comedies/comedy_of_errors.txt",
        "/shakespeare/comedies/cymbeline.txt",
        "/shakespeare/comedies/loves_labours_lost.txt",
        "/shakespeare/comedies/measure_for_measure.txt",
        "/shakespeare/comedies/merchant_of_venice.txt",
        "/shakespeare/comedies/merry_wives_of_windsor.txt",
        "/shakespeare/comedies/much_ado_about_nothing.txt",
        "/shakespeare/comedies/pericles_prince_of_tyre.txt",
        "/shakespeare/comedies/the_taming_of_the_shrew.txt",
        "/shakespeare/comedies/the_tempest.txt",
        "/shakespeare/comedies/the_winters_tale.txt",
        "/shakespeare/comedies/troilus_and_cressida.txt",
        "/shakespeare/comedies/twelfth_night.txt",
        "/shakespeare/comedies/two_gentlemen_of_verona.txt",
        "/shakespeare/histories/king_henry_iv_part_1.txt",
        "/shakespeare/histories/king_henry_iv_part_2.txt",
        "/shakespeare/histories/king_henry_v.txt",
        "/shakespeare/histories/king_henry_vi_part_1.txt",
        "/shakespeare/histories/king_henry_vi_part_2.txt",
        "/shakespeare/histories/king_henry_vi_part_3.txt",
        "/shakespeare/histories/king_henry_viii.txt",
        "/shakespeare/histories/king_john.txt",
        "/shakespeare/histories/king_richard_ii.txt",
        "/shakespeare/histories/king_richard_iii.txt",
        "/shakespeare/poetry/a_lovers_complaint.txt",
        "/shakespeare/poetry/sonnets.txt",
        "/shakespeare/poetry/the_passionate_pilgrim.txt",
        "/shakespeare/poetry/the_rape_of_lucrece.txt",
        "/shakespeare/poetry/venus_and_adonis.txt",
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

    public static void main(String[] args) {
        ConcurrentRadixTree<WordValue> tree = new ConcurrentRadixTree<WordValue>(new DefaultCharArrayNodeFactory());
        for (String file : files) {
            Set<String> wordsInFile = IOUtil.loadWordsFromTextFileOnClasspath(file, true); // true = convert to lowercase
            for (String word : wordsInFile) {
                WordValue wordValue = tree.getValueForExactKey(word);
                if (wordValue == null) {
                    wordValue = new WordValue(word);
                    tree.put(word, wordValue); // not using concurrency support here
                }
                wordValue.manuscriptsContainingWord.add(file.replaceAll("/.*/.*/", "").replace(".txt", ""));
            }
        }

        final String radixTreePrinted = PrettyPrinter.prettyPrint(tree);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JTextArea textArea = new JTextArea();
                textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
                textArea.setText(radixTreePrinted);
                JScrollPane scrollPane = new JScrollPane(textArea);
                textArea.setEditable(false);
                JFrame frame = new JFrame("Shakespeare Radix Tree");
                frame.add(scrollPane);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setSize(640, 480);
                frame.setVisible(true);
            }
        });

    }

    static class WordValue {
        final String fullWord;
        final Set<String> manuscriptsContainingWord = new TreeSet<String>();

        WordValue(String fullWord) {
            this.fullWord = fullWord;
        }

        @Override
        public String toString() {
            return fullWord + ": " + manuscriptsContainingWord;
        }
    }
}
