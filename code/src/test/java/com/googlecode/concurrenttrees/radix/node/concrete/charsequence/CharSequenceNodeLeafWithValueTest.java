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
package com.googlecode.concurrenttrees.radix.node.concrete.charsequence;

import com.googlecode.concurrenttrees.radix.node.Node;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * @author Niall Gallagher
 */
public class CharSequenceNodeLeafWithValueTest {

    @Test(expected = IllegalStateException.class)
    public void testUpdateOutgoingEdge() throws Exception {
        Node node = new CharSequenceNodeLeafWithValue("FOO", 1);
        node.updateOutgoingEdge(new CharSequenceNodeDefault("BAR", null, Collections.<Node>emptyList()));
    }

    @Test
    public void testToString() throws Exception {
        Node node = new CharSequenceNodeLeafWithValue("FOO", 1);
        Assert.assertEquals("Node{edge=FOO, value=1, edges=[]}", node.toString());
    }

    @Test
    public void testGetOutgoingEdge() {
        Node node = new CharSequenceNodeLeafWithValue("FOO", 1);
        Assert.assertNull(node.getOutgoingEdge('A'));
    }
}
