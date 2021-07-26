package com.googlecode.concurrenttrees.radix.node;

import java.util.Collection;

public interface NodeList {

    int size();

    Node get(int index);

    boolean isEmpty();

    void set(int index, Node node);

    boolean contains(Node node);

    void addTo(Collection<? super Node> nodes);

    Node[] toArray();
}
