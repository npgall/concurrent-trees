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
package com.googlecode.concurrenttrees.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;

/**
 * A facade implementation of a Java set for representing a Java map implementation.
 *
 * @param <E> The element type.
 */
public class SetFromMap<E> extends AbstractSet<E> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<E, Boolean> delegate;

    private transient Set<E> keySet;

    public SetFromMap(Map<E, Boolean> delegate) {
        this.delegate = delegate;
        keySet = delegate.keySet();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        keySet = delegate.keySet();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.containsKey(o);
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o) != null;
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean add(E e) {
        return delegate.put(e, Boolean.TRUE) == null;
    }

    @Override
    public Iterator<E> iterator() {
        return keySet.iterator();
    }

    @Override
    public Object[] toArray() {
        return keySet.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return keySet.toArray(a);
    }

    @Override
    public String toString() {
        return keySet.toString();
    }

    @Override
    public int hashCode() {
        return keySet.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || keySet.equals(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return keySet.containsAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return keySet.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return keySet.retainAll(c);
    }
}
