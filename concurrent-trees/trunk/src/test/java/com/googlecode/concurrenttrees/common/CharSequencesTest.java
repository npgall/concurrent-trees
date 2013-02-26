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
package com.googlecode.concurrenttrees.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Niall Gallagher
 */
public class CharSequencesTest {

    @Test
    public void testGeneratePrefixes() throws Exception {
        final CharSequence input = "BANANAS";
        List<CharSequence> expected = Arrays.<CharSequence>asList("B", "BA", "BAN", "BANA", "BANAN", "BANANA", "BANANAS");
        int index = 0;
        for (CharSequence prefix : CharSequences.generatePrefixes(input)) {
            Assert.assertEquals(expected.get(index++), prefix);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGeneratePrefixes_IteratorRemove() throws Exception {
        final CharSequence input = "BANANAS";
        Iterator<CharSequence> iterator = CharSequences.generatePrefixes(input).iterator();
        iterator.next();
        iterator.remove();
    }

    @Test
    public void testGenerateSuffixes() throws Exception {
        final CharSequence input = "BANANAS";
        List<CharSequence> expected = Arrays.<CharSequence>asList("BANANAS", "ANANAS", "NANAS", "ANAS", "NAS", "AS", "S");
        int index = 0;
        for (CharSequence suffix : CharSequences.generateSuffixes(input)) {
            Assert.assertEquals(expected.get(index++), suffix);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGenerateSuffixes_IteratorRemove() throws Exception {
        final CharSequence input = "BANANAS";
        Iterator<CharSequence> iterator = CharSequences.generateSuffixes(input).iterator();
        iterator.next();
        iterator.remove();
    }

    @Test
    public void testGetCommonPrefix() throws Exception {
        Assert.assertEquals("BAN", CharSequences.getCommonPrefix("BANANA", "BANDANA"));
        Assert.assertEquals("BAN", CharSequences.getCommonPrefix("BAN", "BANDANA"));
        Assert.assertEquals("BAN", CharSequences.getCommonPrefix("BANANA", "BAN"));
        Assert.assertEquals("", CharSequences.getCommonPrefix("BANANA", "ABANANA"));
        Assert.assertEquals("", CharSequences.getCommonPrefix("", "BANANA"));
        Assert.assertEquals("", CharSequences.getCommonPrefix("BANANA", ""));
        Assert.assertEquals("T", CharSequences.getCommonPrefix("TOAST", "TEAM"));
    }

    @Test
    public void testGetSuffix() throws Exception {
        Assert.assertEquals("BANANA", CharSequences.getSuffix("BANANA", 0));
        Assert.assertEquals("ANA", CharSequences.getSuffix("BANANA", 3));
        Assert.assertEquals("", CharSequences.getSuffix("BANANA", 6));
        Assert.assertEquals("", CharSequences.getSuffix("BANANA", 7));
    }

    @Test
    public void testGetPrefix() throws Exception {
        Assert.assertEquals("", CharSequences.getPrefix("BANANA", 0));
        Assert.assertEquals("BAN", CharSequences.getPrefix("BANANA", 3));
        Assert.assertEquals("BANANA", CharSequences.getPrefix("BANANA", 6));
        Assert.assertEquals("BANANA", CharSequences.getPrefix("BANANA", 7));
    }

    @Test
    public void testSubtractPrefix() throws Exception {
        Assert.assertEquals("ANA", CharSequences.subtractPrefix("BANANA", "BAN"));
        Assert.assertEquals("", CharSequences.subtractPrefix("BANANA", "BANANA"));
        Assert.assertEquals("", CharSequences.subtractPrefix("BANANA", "BANANAS"));
        Assert.assertEquals("BANANA", CharSequences.subtractPrefix("BANANA", ""));
        Assert.assertEquals("", CharSequences.subtractPrefix("", "BANANAS"));
    }

    @Test
    public void testConcatenate() throws Exception {
        CharSequence first = "APPLE";
        CharSequence second = "ORANGE";
        CharSequence concatenated = CharSequences.concatenate(first, second);
        Assert.assertEquals("APPLEORANGE", new StringBuilder().append(concatenated).toString());
    }

    @Test
    public void testToString_NullArgument() {
        //noinspection NullableProblems
        Assert.assertNull(CharSequences.toString(null));
    }

    @Test
    public void testConstructor() {
        Assert.assertNotNull(new CharSequences());
    }
}
