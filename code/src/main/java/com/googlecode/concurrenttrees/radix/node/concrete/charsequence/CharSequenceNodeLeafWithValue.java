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
package com.googlecode.concurrenttrees.radix.node.concrete.charsequence;

import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.NodeList;
import com.googlecode.concurrenttrees.radix.node.SimpleNodeList;

/**
 * Stores incoming edge as a {@link CharSequence} (a <i>view</i> onto the original key) rather than copying the
 * edge into a character array. Also stores a reference to a value. Does <b>not</b> store any outgoing edges.
 *
 * @author Niall Gallagher
 */
public class CharSequenceNodeLeafWithValue implements Node {

    private static final long serialVersionUID = 1L;

    // Characters in the edge arriving at this node from a parent node.
    // Once assigned, we never modify this...
    private final CharSequence incomingEdgeCharSequence;

    // An arbitrary value which the application associates with a key matching the path to this node in the tree.
    // This value can be null...
    private final Object value;

    public CharSequenceNodeLeafWithValue(CharSequence edgeCharSequence, Object value) {
        // Sort the child nodes...
        this.incomingEdgeCharSequence = edgeCharSequence;
        this.value = value;
    }

    @Override
    public CharSequence getIncomingEdge() {
        return incomingEdgeCharSequence;
    }

    @Override
    public char getIncomingEdgeFirstCharacter() {
        return incomingEdgeCharSequence.charAt(0);
    }

    @Override
    public int getIncomingEdgeLength() {
        return incomingEdgeCharSequence.length();
    }

    @Override
    public char getIncomingEdgeCharacterAt(int index) {
        return incomingEdgeCharSequence.charAt(index);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Node getOutgoingEdge(char edgeFirstCharacter) {
        return null;
    }

    @Override
    public void updateOutgoingEdge(Node childNode) {
        throw new IllegalStateException("Cannot update the reference to the following child node for the edge starting with '" + childNode.getIncomingEdgeFirstCharacter() +"', no such edge already exists: " + childNode);
    }

    @Override
    public NodeList getOutgoingEdges() {
        return SimpleNodeList.EMPTY;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Node{");
        sb.append("edge=").append(incomingEdgeCharSequence);
        sb.append(", value=").append(value);
        sb.append(", edges=[]");
        sb.append("}");
        return sb.toString();
    }
}
