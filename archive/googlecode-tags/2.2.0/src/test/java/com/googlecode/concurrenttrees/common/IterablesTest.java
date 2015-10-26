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

import java.util.*;

/**
 * @author Niall Gallagher
 */
public class IterablesTest {

    final Iterable<Integer> DUMMY_COLLECTION = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
    final Iterable<Integer> DUMMY_ITERABLE = new Iterable<Integer>() {
        @Override
        public Iterator<Integer> iterator() {
            return LazyIteratorTest.newLazyIterator(1, 2, 3, 4);
        }
    };

    @Test
    public void testToList() {
        Assert.assertTrue("should be an instance of ArrayList", Iterables.toList(DUMMY_COLLECTION) instanceof ArrayList);
        Assert.assertTrue("should have 4 elements", Iterables.count(Iterables.toList(DUMMY_COLLECTION)) == 4);
        Assert.assertEquals("[1, 2, 3, 4]", Iterables.toString(Iterables.toList(DUMMY_COLLECTION)));


        Assert.assertTrue("should be an instance of LinkedList", Iterables.toList(DUMMY_ITERABLE) instanceof LinkedList);
        Assert.assertTrue("should have 4 elements", Iterables.count(Iterables.toList(DUMMY_ITERABLE)) == 4);
        Assert.assertEquals("[1, 2, 3, 4]", Iterables.toString(Iterables.toList(DUMMY_ITERABLE)));
    }

    @Test
    public void testToSet() {
        Assert.assertTrue("should be an instance of LinkedHashSet", Iterables.toSet(DUMMY_COLLECTION) instanceof LinkedHashSet);
        Assert.assertTrue("should have 4 elements", Iterables.count(Iterables.toSet(DUMMY_COLLECTION)) == 4);
        Assert.assertEquals("[1, 2, 3, 4]", Iterables.toString(Iterables.toSet(DUMMY_COLLECTION)));

        Assert.assertTrue("should be an instance of LinkedHashSet", Iterables.toSet(DUMMY_ITERABLE) instanceof LinkedHashSet);
        Assert.assertTrue("should have 4 elements", Iterables.count(Iterables.toSet(DUMMY_ITERABLE)) == 4);
        Assert.assertEquals("[1, 2, 3, 4]", Iterables.toString(Iterables.toSet(DUMMY_ITERABLE)));

    }

    @Test
    public void testToString() {
        Assert.assertEquals("[1, 2, 3, 4]", Iterables.toString(DUMMY_COLLECTION));
    }

    @Test
    public void testCount() {
        Assert.assertEquals(4, Iterables.count(DUMMY_COLLECTION));
    }

    @Test
    public void testConstructor() {
        Assert.assertNotNull(new Iterables());
    }
}
