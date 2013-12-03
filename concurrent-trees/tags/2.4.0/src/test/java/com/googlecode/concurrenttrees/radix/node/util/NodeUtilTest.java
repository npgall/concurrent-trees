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
package com.googlecode.concurrenttrees.radix.node.util;

import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * @author Niall Gallagher
 */
public class NodeUtilTest {

    @Test
    @SuppressWarnings({"NullableProblems"})
    public void testBinarySearchForEdge() throws Exception {
        NodeFactory nodeFactory = new DefaultCharArrayNodeFactory();
        Node[] nodes = new Node[] {
                nodeFactory.createNode("A", null, Collections.<Node>emptyList(), false),
                nodeFactory.createNode("B", null, Collections.<Node>emptyList(), false),
                nodeFactory.createNode("C", null, Collections.<Node>emptyList(), false)
        };
        AtomicReferenceArray<Node> atomicReferenceArray = new AtomicReferenceArray<Node>(nodes);
        Assert.assertEquals(0, NodeUtil.binarySearchForEdge(atomicReferenceArray, 'A'));
        Assert.assertEquals(1, NodeUtil.binarySearchForEdge(atomicReferenceArray, 'B'));
        Assert.assertEquals(2, NodeUtil.binarySearchForEdge(atomicReferenceArray, 'C'));
        Assert.assertTrue(NodeUtil.binarySearchForEdge(atomicReferenceArray, 'D') < 0);
    }

    @Test
    @SuppressWarnings({"NullableProblems"})
    public void testEnsureNoDuplicateEdges_Positive() throws Exception {
        NodeFactory nodeFactory = new DefaultCharArrayNodeFactory();
        List<Node> nodes = Arrays.asList(
                nodeFactory.createNode("A", null, Collections.<Node>emptyList(), false),
                nodeFactory.createNode("B", null, Collections.<Node>emptyList(), false),
                nodeFactory.createNode("C", null, Collections.<Node>emptyList(), false)
        );
    }

    @Test
    @SuppressWarnings({"NullableProblems"})
    public void testEnsureNoDuplicateEdges_Negative() throws Exception {
        NodeFactory nodeFactory = new DefaultCharArrayNodeFactory();
        List<Node> nodes = Arrays.asList(
                nodeFactory.createNode("A", null, Collections.<Node>emptyList(), false),
                nodeFactory.createNode("B", null, Collections.<Node>emptyList(), false),
                nodeFactory.createNode("B", null, Collections.<Node>emptyList(), false),
                nodeFactory.createNode("C", null, Collections.<Node>emptyList(), false)
        );
        try {
            NodeUtil.ensureNoDuplicateEdges(nodes);
            Assert.fail("Should throw exception");
        }
        catch (IllegalStateException expected) {
            // Expected
        }
    }

    @Test
    public void testConstructor() {
        Assert.assertNotNull(new NodeUtil());
    }
}
