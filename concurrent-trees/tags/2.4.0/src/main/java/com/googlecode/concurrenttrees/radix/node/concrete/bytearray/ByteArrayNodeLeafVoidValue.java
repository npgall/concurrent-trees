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
package com.googlecode.concurrenttrees.radix.node.concrete.bytearray;

import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.concrete.voidvalue.VoidValue;

import java.util.Collections;
import java.util.List;

/**
 * Similar to {@link com.googlecode.concurrenttrees.radix.node.concrete.chararray.CharArrayNodeLeafVoidValue} but represents
 * each character in UTF-8, instead of Java's default 2-byte UFT-16 encoding.
 * <p/>
 * Supports only characters which can be represented as a single byte in UTF-8. Throws an exception if characters
 * are encountered which cannot be represented as a single byte.
 *
 * @author Niall Gallagher
 */
public class ByteArrayNodeLeafVoidValue implements Node {

    // Characters in the edge arriving at this node from a parent node.
    // Once assigned, we never modify this...
    private final byte[] incomingEdgeCharArray;

    public ByteArrayNodeLeafVoidValue(CharSequence edgeCharSequence) {
        this.incomingEdgeCharArray = ByteArrayCharSequence.toSingleByteUtf8Encoding(edgeCharSequence);
    }

    @Override
    public CharSequence getIncomingEdge() {
        return new ByteArrayCharSequence(incomingEdgeCharArray, 0, incomingEdgeCharArray.length);
    }

    @Override
    public Character getIncomingEdgeFirstCharacter() {
        return (char) (incomingEdgeCharArray[0] & 0xFF);
    }

    @Override
    public Object getValue() {
        return VoidValue.SINGLETON;
    }

    @Override
    public Node getOutgoingEdge(Character edgeFirstCharacter) {
        return null;
    }

    @Override
    public void updateOutgoingEdge(Node childNode) {
        throw new IllegalStateException("Cannot update the reference to the following child node for the edge starting with '" + childNode.getIncomingEdgeFirstCharacter() +"', no such edge already exists: " + childNode);
    }

    @Override
    public List<Node> getOutgoingEdges() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Node{");
        sb.append("edge=").append(getIncomingEdge());
        sb.append(", value=").append(VoidValue.SINGLETON);
        sb.append(", edges=[]");
        sb.append("}");
        return sb.toString();
    }
}
