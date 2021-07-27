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
package com.googlecode.concurrenttrees.radix.node.util;

import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.NodeList;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class AtomicNodeReferenceArray extends AtomicReferenceArray<Node> implements NodeList {

    private static final long serialVersionUID = 1L;

    public AtomicNodeReferenceArray(Node[] array) {
        super(array);
    }

    @Override
    public int size() {
        return length();
    }

    @Override
    public boolean isEmpty() {
        return length() == 0;
    }

    @Override
    public boolean contains(Node node) {
        for (int index = 0; index < length(); index++) {
            if (node == null && get(index) == null || node != null && node.equals(get(index))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addTo(Collection<? super Node> nodes) {
        for (int index = 0; index < length(); index++) {
            nodes.add(get(index));
        }
    }

    @Override
    public Node[] toArray() {
        Node[] nodes = new Node[length()];
        for (int index = 0; index < nodes.length; index++) {
            nodes[index] = get(index);
        }
        return nodes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int index = 0; index < length(); index++) {
            if (index > 0) {
                sb.append(", ");
            }
            sb.append(get(index));
        }
        sb.append(']');
        return sb.toString();
    }
}
