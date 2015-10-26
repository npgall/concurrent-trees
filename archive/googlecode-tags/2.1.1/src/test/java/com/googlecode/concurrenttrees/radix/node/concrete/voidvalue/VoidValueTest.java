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
package com.googlecode.concurrenttrees.radix.node.concrete.voidvalue;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Niall Gallagher
 */
public class VoidValueTest {

    @Test
    public void testHashCode() throws Exception {
        Assert.assertEquals(1, new VoidValue().hashCode());
    }

    @Test
    public void testEquals() throws Exception {
        Assert.assertTrue(new VoidValue().equals(new VoidValue()));
        Assert.assertFalse(new VoidValue().equals(new Object()));
        //noinspection NullableProblems,ObjectEqualsNull
        Assert.assertFalse(new VoidValue().equals(null));
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals("-", new VoidValue().toString());
    }
}
