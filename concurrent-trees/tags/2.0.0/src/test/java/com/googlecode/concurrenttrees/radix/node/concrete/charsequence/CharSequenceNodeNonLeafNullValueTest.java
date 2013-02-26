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

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Niall Gallagher
 */
public class CharSequenceNodeNonLeafNullValueTest {

    @Test
    public void testUpdateOutgoingEdge() throws Exception {
        Node node = new CharSequenceNodeNonLeafNullValue("FOO", Arrays.asList((Node)new CharSequenceNodeDefault("BAR1", 1, Collections.<Node>emptyList())));
        node.updateOutgoingEdge(new CharSequenceNodeDefault("BAR2", null, Collections.<Node>emptyList()));
    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateOutgoingEdge_NonExistentEdge() throws Exception {
        Node node = new CharSequenceNodeNonLeafNullValue("FOO", Arrays.asList((Node)new CharSequenceNodeDefault("BAR", 1, Collections.<Node>emptyList())));
        node.updateOutgoingEdge(new CharSequenceNodeDefault("CAR", null, Collections.<Node>emptyList()));
    }

    @Test
    public void testToString() throws Exception {
        Node node = new CharSequenceNodeNonLeafNullValue("FOO", Collections.<Node>emptyList());
        Assert.assertEquals("Node{edge=FOO, value=null, edges=[]}", node.toString());
    }
}
