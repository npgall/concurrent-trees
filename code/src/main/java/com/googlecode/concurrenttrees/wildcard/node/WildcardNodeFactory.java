/**
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
package com.googlecode.concurrenttrees.wildcard.node;

import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.googlecode.concurrenttrees.wildcard.WildcardPattern;

import java.io.Serializable;
import java.util.List;

/**
 * An interface for a factory which creates new {@link WildcardNode} objects on demand, to encapsulate specified
 * variables.
 * Factory objects can choose to return implementations of the {@link WildcardNode} interface which are memory-optimized
 * for storing only the given variables, potentially further optimized based on variable values.
 *
 * @author Niall Gallagher
 */
public interface WildcardNodeFactory extends Serializable {

    /**
     * Returns a new {@link WildcardNode} object which encapsulates the arguments supplied, optionally returning
     * implementations of the {@link WildcardNode} interface which are memory-optimized for storing only the supplied
     * combination of variables, potentially further optimized based on variable values.
     *
     * @return An object implementing the {@link WildcardNode} interface which stores the given variables
     */
    WildcardNode createNode(WildcardPattern key, Object value, InvertedRadixTree<WildcardNode> subtree);
}
