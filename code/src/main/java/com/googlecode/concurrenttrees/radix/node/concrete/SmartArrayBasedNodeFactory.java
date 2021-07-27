/*
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
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.NodeList;
import com.googlecode.concurrenttrees.radix.node.concrete.bytearray.ByteArrayCharSequence;

/**
 * A {@link NodeFactory} which internally uses {@link DefaultByteArrayNodeFactory} to create nodes by default (which
 * can reduce memory overhead), but falls back to {@link DefaultCharArrayNodeFactory} if characters are detected which
 * cannot be represented as a single byte.
 *
 * @author Niall Gallagher
 */
public class SmartArrayBasedNodeFactory implements NodeFactory {

    private static final long serialVersionUID = 1L;

    final NodeFactory charArrayNodeFactory = new DefaultCharArrayNodeFactory();
    final NodeFactory byteArrayNodeFactory = new DefaultByteArrayNodeFactory(true);

    @Override
    public Node createNode(CharSequence edgeCharacters, Object value, NodeList childNodes, boolean isRoot) {
        Node node = byteArrayNodeFactory.createNode(edgeCharacters, value, childNodes, isRoot);
        if (node == null) {
            return charArrayNodeFactory.createNode(edgeCharacters, value, childNodes, isRoot);
        } else {
            return node;
        }
    }
}
