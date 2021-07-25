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
package com.googlecode.concurrenttrees.radix.node.concrete.chararray;

import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.util.NodeCharacterComparator;
import com.googlecode.concurrenttrees.radix.node.util.NodeUtil;
import com.googlecode.concurrenttrees.radix.node.util.AtomicReferenceArrayListAdapter;
import com.googlecode.concurrenttrees.common.CharSequences;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * A non-optimized implementation of the {@link Node} interface. Stores all variables and supports all behaviours
 * required by the tree, but not very memory efficient.
 * <p/>
 * See {@link com.googlecode.concurrenttrees.radix.node.NodeFactory} for documentation on how alternative
 * node implementations can be created to reduce memory overhead. See the {@link Node} interface for details on how
 * to write memory-efficient nodes.
 * <p/>
 * This implementation stores references to child nodes in an {@link AtomicReferenceArray}, in ascending sorted order
 * of the first character of the edges which child nodes define.
 * <p/>
 * The {@link #getOutgoingEdge(char)} method uses binary search to locate a requested node, given the first character
 * of an edge indicated. The node is then read and returned atomically from the {@link AtomicReferenceArray}.
 * <p/>
 * The {@link #updateOutgoingEdge(com.googlecode.concurrenttrees.radix.node.Node)} method ensures that any
 * attempt to update a reference to a child node preserves the constraints defined in the {@link Node} interface. New
 * child nodes are written atomically to the {@link AtomicReferenceArray}.
 * <p/>
 * The constraints defined in the {@link Node} interface ensure that the {@link AtomicReferenceArray} always remains in
 * ascending sorted order regardless of modifications performed concurrently, as long as the modifications comply with
 * the constraints. This node enforces those constraints.
 *
 * @author Niall Gallagher
 */
public class CharArrayNodeDefault implements Node {

    private static final long serialVersionUID = 1L;

    // Characters in the edge arriving at this node from a parent node.
    // Once assigned, we never modify this...
    private final char[] incomingEdgeCharArray;

    // References to child nodes representing outgoing edges from this node.
    // Once assigned we never add or remove references, but we do update existing references to point to new child
    // nodes provided new edges start with the same first character...
    private final AtomicReferenceArray<Node> outgoingEdges;

    // A read-only List wrapper around the outgoingEdges AtomicReferenceArray...
    private final List<Node> outgoingEdgesAsList;

    // An arbitrary value which the application associates with a key matching the path to this node in the tree.
    // This value can be null...
    private final Object value;

    public CharArrayNodeDefault(CharSequence edgeCharSequence, Object value, List<Node> outgoingEdges) {
        Node[] childNodeArray = outgoingEdges.toArray(new Node[0]);
        // Sort the child nodes...
        Arrays.sort(childNodeArray, new NodeCharacterComparator());
        this.outgoingEdges = new AtomicReferenceArray<Node>(childNodeArray);
        this.incomingEdgeCharArray = CharSequences.toCharArray(edgeCharSequence);
        this.value = value;
        this.outgoingEdgesAsList = new AtomicReferenceArrayListAdapter<Node>(this.outgoingEdges);
    }

    @Override
    public CharSequence getIncomingEdge() {
        return CharSequences.fromCharArray(incomingEdgeCharArray);
    }

    @Override
    public char getIncomingEdgeFirstCharacter() {
        return incomingEdgeCharArray[0];
    }

    @Override
    public int getIncomingEdgeLength() {
        return incomingEdgeCharArray.length;
    }

    @Override
    public char getIncomingEdgeCharacterAt(int index) {
        return incomingEdgeCharArray[index];
    }

    @Override
    public Object getValue() {
        return value;
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
        sb.append("edge=").append(incomingEdgeCharArray);
        sb.append(", value=").append(value);
        sb.append(", edges=").append(getOutgoingEdges());
        sb.append("}");
        return sb.toString();
    }
}
