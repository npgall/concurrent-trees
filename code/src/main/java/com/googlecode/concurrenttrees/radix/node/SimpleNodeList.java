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
package com.googlecode.concurrenttrees.radix.node;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A simple implementation of a {@link NodeList} backed by a {@link List}.
 */
public class SimpleNodeList implements NodeList {

    private final List<Node> nodes;

    public static final NodeList EMPTY = new SimpleNodeList();

    SimpleNodeList() {
        this.nodes = Collections.emptyList();
    }

    public SimpleNodeList(Node node) {
        this.nodes = Collections.singletonList(node);
    }

    public SimpleNodeList(Node... nodes) {
        this.nodes = Arrays.asList(nodes);
    }

    public SimpleNodeList(List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public Node get(int index) {
        return nodes.get(index);
    }

    @Override
    public boolean contains(Node node) {
        return nodes.contains(node);
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public void set(int index, Node node) {
        nodes.set(index, node);
    }

    @Override
    public void addTo(Collection<? super Node> nodes) {
        nodes.addAll(this.nodes);
    }

    @Override
    public Node[] toArray() {
        return nodes.toArray(new Node[0]);
    }

    @Override
    public String toString() {
        return nodes.toString();
    }
}
