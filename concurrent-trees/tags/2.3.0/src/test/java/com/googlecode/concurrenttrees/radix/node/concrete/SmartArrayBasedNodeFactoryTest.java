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
package com.googlecode.concurrenttrees.radix.node.concrete;

import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.concrete.bytearray.ByteArrayNodeLeafVoidValue;
import com.googlecode.concurrenttrees.radix.node.concrete.chararray.CharArrayNodeLeafVoidValue;
import com.googlecode.concurrenttrees.radix.node.concrete.voidvalue.VoidValue;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * @author Niall Gallagher
 */
public class SmartArrayBasedNodeFactoryTest {

    final SmartArrayBasedNodeFactory smartNodeFactory = new SmartArrayBasedNodeFactory();

    @Test
    public void testCreateNode_CompatibleCharacters() throws Exception {
        Node node = smartNodeFactory.createNode("FOOBAR", VoidValue.SINGLETON, Collections.<Node>emptyList(), false);
        Assert.assertEquals(ByteArrayNodeLeafVoidValue.class, node.getClass());
    }

    @Test
    public void testCreateNode_IncompatibleCharacters() throws Exception {
        Node node = smartNodeFactory.createNode("FOOBAR○", VoidValue.SINGLETON, Collections.<Node>emptyList(), false);
        Assert.assertEquals(CharArrayNodeLeafVoidValue.class, node.getClass());
    }
}
