package com.googlecode.concurrenttrees.examples.shakespeare;


import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.common.PrettyPrintUtil;
import com.googlecode.concurrenttrees.examples.shakespeare.util.IOUtil;
import com.googlecode.concurrenttrees.radix.node.concrete.NaiveCharSequenceNodeFactory;
import com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Niall Gallagher
 */
public class BuildShakespeareDocumentSuffixTree {

    static final List<String> files = Arrays.asList(
//        "/shakespeare/comedies/a_midsummer_nights_dream.txt",
//        "/shakespeare/comedies/alls_well_that_ends_well.txt",
//        "/shakespeare/comedies/as_you_like_it.txt",
//        "/shakespeare/comedies/comedy_of_errors.txt",
//        "/shakespeare/comedies/cymbeline.txt",
//        "/shakespeare/comedies/loves_labours_lost.txt",
//        "/shakespeare/comedies/measure_for_measure.txt",
//        "/shakespeare/comedies/merchant_of_venice.txt",
//        "/shakespeare/comedies/merry_wives_of_windsor.txt",
//        "/shakespeare/comedies/much_ado_about_nothing.txt",
//        "/shakespeare/comedies/pericles_prince_of_tyre.txt",
//        "/shakespeare/comedies/the_taming_of_the_shrew.txt",
//        "/shakespeare/comedies/the_tempest.txt",
//        "/shakespeare/comedies/the_winters_tale.txt",
//        "/shakespeare/comedies/troilus_and_cressida.txt",
//        "/shakespeare/comedies/twelfth_night.txt",
//        "/shakespeare/comedies/two_gentlemen_of_verona.txt",
//        "/shakespeare/histories/king_henry_iv_part_1.txt",
//        "/shakespeare/histories/king_henry_iv_part_2.txt",
//        "/shakespeare/histories/king_henry_v.txt",
//        "/shakespeare/histories/king_henry_vi_part_1.txt",
//        "/shakespeare/histories/king_henry_vi_part_2.txt",
//        "/shakespeare/histories/king_henry_vi_part_3.txt",
//        "/shakespeare/histories/king_henry_viii.txt",
//        "/shakespeare/histories/king_john.txt",
//        "/shakespeare/histories/king_richard_ii.txt",
//        "/shakespeare/histories/king_richard_iii.txt",
//        "/shakespeare/poetry/a_lovers_complaint.txt",
//        "/shakespeare/poetry/sonnets.txt",
//        "/shakespeare/poetry/the_passionate_pilgrim.txt",
//        "/shakespeare/poetry/the_rape_of_lucrece.txt",
//        "/shakespeare/poetry/venus_and_adonis.txt",
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
        ConcurrentSuffixTree<String> tree = new ConcurrentSuffixTree<String>(new NaiveCharSequenceNodeFactory());
        for (String file : files) {
            String manuscript = IOUtil.loadTextFileFromClasspath(file, true, true, true); // true = convert to lowercase
            String manuscriptName = file.replaceAll("/.*/.*/", "").replace(".txt", "");
            tree.put(manuscript, manuscriptName);
            System.out.println("added " + manuscriptName);
        }
        System.out.println("built");
    }
}
