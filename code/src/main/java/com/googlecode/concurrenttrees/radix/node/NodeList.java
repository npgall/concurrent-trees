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

import java.util.Collection;

/**
 * A list of {@link Node}s represented by a tree. This interface is used rather then a Java list, 
 * to allow representing concurrent arrays as such lists and to avoid allocating wrapper instances.
 */
public interface NodeList {
    
    int size();

    Node get(int index);

    boolean isEmpty();

    void set(int index, Node node);

    boolean contains(Node node);

    void addTo(Collection<? super Node> nodes);

    Node[] toArray();
}
