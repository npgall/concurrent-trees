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
package com.googlecode.concurrenttrees.radix.node.concrete.bytearray;

import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.concrete.voidvalue.VoidValue;
import com.googlecode.concurrenttrees.radix.node.util.AtomicReferenceArrayListAdapter;
import com.googlecode.concurrenttrees.radix.node.util.NodeCharacterComparator;
import com.googlecode.concurrenttrees.radix.node.util.NodeUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Similar to {@link com.googlecode.concurrenttrees.radix.node.concrete.chararray.CharArrayNodeNonLeafVoidValue} but represents
 * each character in UTF-8, instead of Java's default 2-byte UFT-16 encoding.
 * <p/>
 * Supports only characters which can be represented as a single byte in UTF-8. Throws an exception if characters
 * are encountered which cannot be represented as a single byte.
 *
 * @author Niall Gallagher
 */
public class ByteArrayNodeNonLeafVoidValue implements Node {

    private static final long serialVersionUID = 1L;

    // Characters in the edge arriving at this node from a parent node.
    // Once assigned, we never modify this...
    private final byte[] incomingEdgeCharArray;

    // References to child nodes representing outgoing edges from this node.
    // Once assigned we never add or remove references, but we do update existing references to point to new child
    // nodes provided new edges start with the same first character...
    private final AtomicReferenceArray<Node> outgoingEdges;

    // A read-only List wrapper around the outgoingEdges AtomicReferenceArray...
    private final List<Node> outgoingEdgesAsList;

    public ByteArrayNodeNonLeafVoidValue(CharSequence edgeCharSequence, List<Node> outgoingEdges) {
        Node[] childNodeArray = outgoingEdges.toArray(new Node[0]);
        // Sort the child nodes...
        Arrays.sort(childNodeArray, new NodeCharacterComparator());
        this.outgoingEdges = new AtomicReferenceArray<Node>(childNodeArray);
        this.incomingEdgeCharArray = ByteArrayCharSequence.toSingleByteUtf8Encoding(edgeCharSequence);
        this.outgoingEdgesAsList = new AtomicReferenceArrayListAdapter<Node>(this.outgoingEdges);
    }

    @Override
    public CharSequence getIncomingEdge() {
        return new ByteArrayCharSequence(incomingEdgeCharArray, 0, incomingEdgeCharArray.length);
    }

    @Override
    public char getIncomingEdgeFirstCharacter() {
        return (char) (incomingEdgeCharArray[0] & 0xFF);
    }

    @Override
    public int getIncomingEdgeLength() {
        return incomingEdgeCharArray.length;
    }

    @Override
    public char getIncomingEdgeCharacterAt(int index) {
        return (char) (incomingEdgeCharArray[index] & 0xFF);
    }

    @Override
    public Object getValue() {
        return VoidValue.SINGLETON;
    }

    @Override
    public Node getOutgoingEdge(char edgeFirstCharacter) {
        // Binary search for the index of the node whose edge starts with the given character.
        // Note that this binary search is safe in the face of concurrent modification due to constraints
        // we enforce on use of the array, as documented in the binarySearchForEdge method...
        int index = NodeUtil.binarySearchForEdge(outgoingEdges, edgeFirstCharacter);
        if (index < 0) {
            // No such edge exists...
            return null;
        }
        // Atomically return the child node at this index...
        return outgoingEdges.get(index);
    }

    @Override
    public void updateOutgoingEdge(Node childNode) {
        // Binary search for the index of the node whose edge starts with the given character.
        // Note that this binary search is safe in the face of concurrent modification due to constraints
        // we enforce on use of the array, as documented in the binarySearchForEdge method...
        int index = NodeUtil.binarySearchForEdge(outgoingEdges, childNode.getIncomingEdgeFirstCharacter());
        if (index < 0) {
            throw new IllegalStateException("Cannot update the reference to the following child node for the edge starting with '" + childNode.getIncomingEdgeFirstCharacter() +"', no such edge already exists: " + childNode);
        }
        // Atomically update the child node at this index...
        outgoingEdges.set(index, childNode);
    }

    @Override
    public List<Node> getOutgoingEdges() {
        return outgoingEdgesAsList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Node{");
        sb.append("edge=").append(getIncomingEdge());
        sb.append(", value=").append(VoidValue.SINGLETON);
        sb.append(", edges=").append(getOutgoingEdges());
        sb.append("}");
        return sb.toString();
    }
}
