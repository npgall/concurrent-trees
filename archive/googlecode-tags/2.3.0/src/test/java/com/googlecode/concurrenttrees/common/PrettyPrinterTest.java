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

import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Niall Gallagher
 */
public class PrettyPrinterTest {

    @Test
    public void testPrettyPrint_ToString() throws Exception {
        Node root = getHandBuiltTestTree();
        String expected1 =
                "○\n" +
                "└── ○ B (1)\n" +
                "    └── ○ A (2)\n" +
                "        └── ○ N (3)\n" +
                "            ├── ○ AN (5)\n" +
                "            │   └── ○ A (6)\n" +
                "            └── ○ DANA (4)\n";

        String actual1 = PrettyPrinter.prettyPrint(wrapNodeForPrinting(root));
        Assert.assertEquals(expected1, actual1);

        String expected2 =
                "○ B (1)\n" +
                "└── ○ A (2)\n" +
                "    └── ○ N (3)\n" +
                "        ├── ○ AN (5)\n" +
                "        │   └── ○ A (6)\n" +
                "        └── ○ DANA (4)\n";

        String actual2 = PrettyPrinter.prettyPrint(wrapNodeForPrinting(root.getOutgoingEdge('B')));
        Assert.assertEquals(expected2, actual2);
    }

    @Test
    public void testPrettyPrint_ToAppendable() throws Exception {
        Node root = getHandBuiltTestTree();
        String expected1 =
                "○\n" +
                "└── ○ B (1)\n" +
                "    └── ○ A (2)\n" +
                "        └── ○ N (3)\n" +
                "            ├── ○ AN (5)\n" +
                "            │   └── ○ A (6)\n" +
                "            └── ○ DANA (4)\n";

        StringBuilder appendable = new StringBuilder();
        PrettyPrinter.prettyPrint(wrapNodeForPrinting(root), appendable);
        Assert.assertEquals(expected1, appendable.toString());
    }

    @Test(expected = IllegalStateException.class)
    public void testPrettyPrint_WrapIOException() {
        Node root = getHandBuiltTestTree();
        Appendable appendable = new Appendable() {
            @Override
            public Appendable append(CharSequence csq) throws IOException {
                throw new IOException("Intentional exception");
            }

            @Override
            public Appendable append(CharSequence csq, int start, int end) throws IOException {
                throw new IOException("Intentional exception");
            }

            @Override
            public Appendable append(char c) throws IOException {
                throw new IOException("Intentional exception");
            }
        };
        PrettyPrinter.prettyPrint(wrapNodeForPrinting(root), appendable);
    }

    static Node getHandBuiltTestTree() {
        NodeFactory nodeFactory = new DefaultCharArrayNodeFactory();
        // Build the tree by hand, as if the following strings were added: B, BA, BAN, BANDANA, BANAN, BANANA

        //    ○
        //    └── ○ B (1)
        //        └── ○ A (2)
        //            └── ○ N (3)
        //                ├── ○ AN (5)
        //                │   └── ○ A (6)
        //                └── ○ DANA (4)

        final Node n1, n2, n3, n4, n5, n6;
        n6 = nodeFactory.createNode("A", 6, Collections.<Node>emptyList(), false);
        n5 = nodeFactory.createNode("AN", 5, Arrays.asList(n6), false);
        n4 = nodeFactory.createNode("DANA", 4, Collections.<Node>emptyList(), false);
        n3 = nodeFactory.createNode("N", 3, Arrays.asList(n4, n5), false); // note: it should sort these such that n5 is first
        n2 = nodeFactory.createNode("A", 2, Arrays.asList(n3), false);
        n1 = nodeFactory.createNode("B", 1, Arrays.asList(n2), false);
        //noinspection NullableProblems
        return nodeFactory.createNode("", null, Arrays.asList(n1), true); // root
    }

    PrettyPrintable wrapNodeForPrinting(final Node node) {
        return new PrettyPrintable() {
            @Override
            public Node getNode() {
                return node;
            }
        };
    }

    @Test
    public void testConstructor() {
        Assert.assertNotNull(new PrettyPrinter());
    }
}
