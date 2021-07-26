package com.googlecode.concurrenttrees.radix.node;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
