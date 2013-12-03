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

import com.googlecode.concurrenttrees.common.CharSequences;
import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.bytearray.*;
import com.googlecode.concurrenttrees.radix.node.concrete.voidvalue.VoidValue;
import com.googlecode.concurrenttrees.radix.node.util.NodeUtil;

import java.util.List;

/**
 * A {@link NodeFactory} which creates {@link Node} objects which store incoming edge characters as a byte array inside
 * the node. This is similar to {@link DefaultCharArrayNodeFactory}, except nodes use a single byte to represent each
 * character in UTF-8, instead of Java's default 2-byte UFT-16 encoding.
 *  <p/>
 * This can reduce the memory overhead of storing character data by 50%, but supports only characters which can be
 * represented as a single byte in UTF-8. Throws an exception if characters are encountered which cannot be represented
 * as a single byte.
 *
 * @author Niall Gallagher
 */
public class DefaultByteArrayNodeFactory implements NodeFactory {

    @Override
    public Node createNode(CharSequence edgeCharacters, Object value, List<Node> childNodes, boolean isRoot) {
        if (edgeCharacters == null) {
            throw new IllegalStateException("The edgeCharacters argument was null");
        }
        if (!isRoot && edgeCharacters.length() == 0) {
            throw new IllegalStateException("Invalid edge characters for non-root node: " + CharSequences.toString(edgeCharacters));
        }
        if (childNodes == null) {
            throw new IllegalStateException("The childNodes argument was null");
        }
        NodeUtil.ensureNoDuplicateEdges(childNodes);
        if (childNodes.isEmpty()) {
            // Leaf node...
            if (value instanceof VoidValue) {
                return new ByteArrayNodeLeafVoidValue(edgeCharacters);
            }
            else if (value != null) {
                return new ByteArrayNodeLeafWithValue(edgeCharacters, value);
            }
            else {
                return new ByteArrayNodeLeafNullValue(edgeCharacters);
            }
        }
        else {
            // Non-leaf node...
            if (value instanceof VoidValue) {
                return new ByteArrayNodeNonLeafVoidValue(edgeCharacters, childNodes);
            }
            else if (value == null) {
                return new ByteArrayNodeNonLeafNullValue(edgeCharacters, childNodes);
            }
            else {
                return new ByteArrayNodeDefault(edgeCharacters, value, childNodes);
            }
        }
    }

}
