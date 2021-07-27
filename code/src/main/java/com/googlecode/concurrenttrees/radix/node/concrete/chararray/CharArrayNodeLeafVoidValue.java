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

import com.googlecode.concurrenttrees.common.CharSequences;
import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.NodeList;
import com.googlecode.concurrenttrees.radix.node.SimpleNodeList;
import com.googlecode.concurrenttrees.radix.node.concrete.voidvalue.VoidValue;

/**
 * Stores only incoming edge as a {@code char[]}.
 * Returns {@link VoidValue} for the value. Does <b>not</b> store any outgoing edges.
 *
 * @author Niall Gallagher
 */
public class CharArrayNodeLeafVoidValue implements Node {

    private static final long serialVersionUID = 1L;

    // Characters in the edge arriving at this node from a parent node.
    // Once assigned, we never modify this...
    private final char[] incomingEdgeCharArray;

    public CharArrayNodeLeafVoidValue(CharSequence edgeCharSequence) {
        this.incomingEdgeCharArray = CharSequences.toCharArray(edgeCharSequence);
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
        return VoidValue.SINGLETON;
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
        sb.append("edge=").append(incomingEdgeCharArray);
        sb.append(", value=").append(VoidValue.SINGLETON);
        sb.append(", edges=[]");
        sb.append("}");
        return sb.toString();
    }
}
