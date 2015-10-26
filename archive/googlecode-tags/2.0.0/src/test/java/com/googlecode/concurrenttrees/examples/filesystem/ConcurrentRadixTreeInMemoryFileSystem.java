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
package com.googlecode.concurrenttrees.examples.filesystem;

import com.googlecode.concurrenttrees.common.CharSequences;
import com.googlecode.concurrenttrees.common.Iterables;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;

import java.util.*;

/**
 * A simple implementation of {@link InMemoryFileSystem} based on {@link ConcurrentRadixTree}.
 *
 * @author Niall Gallagher
 */
public class ConcurrentRadixTreeInMemoryFileSystem<F> implements PrettyPrintable, InMemoryFileSystem<F> {

    private final RadixTree<F> radixTree = new ConcurrentRadixTree<F>(new DefaultCharArrayNodeFactory());

    @Override
    public void addFile(String containingDirectory, String fileName, F file) {
        if (!containingDirectory.endsWith("/")) {
            throw new IllegalArgumentException("Containing directory must end with '/': " + containingDirectory);
        }
        if (containingDirectory.contains("$")) {
            throw new IllegalArgumentException("Containing directory cannot contain '$': " + containingDirectory);
        }
        if (fileName.contains("/")) {
            throw new IllegalArgumentException("File name cannot contain '/': " + fileName);
        }
        if (fileName.contains("$")) {
            throw new IllegalArgumentException("File name cannot contain '$': " + fileName);
        }
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        String fullyQualifiedFileName = containingDirectory + "$" + fileName;
        radixTree.put(fullyQualifiedFileName, file);
    }

    @Override
    public F getFile(String containingDirectory, String fileName) {
        if (!containingDirectory.endsWith("/")) {
            throw new IllegalArgumentException("Containing directory must end with '/': " + containingDirectory);
        }
        if (containingDirectory.contains("$")) {
            throw new IllegalArgumentException("Containing directory cannot contain '$': " + containingDirectory);
        }
        if (fileName.contains("/")) {
            throw new IllegalArgumentException("File name cannot contain '/': " + fileName);
        }
        if (fileName.contains("$")) {
            throw new IllegalArgumentException("File name cannot contain '$': " + fileName);
        }
        String fullyQualifiedFileName = containingDirectory + "$" + fileName;
        return radixTree.getValueForExactKey(fullyQualifiedFileName);
    }

    @Override
    public Collection<String> getFileNamesInDirectory(String containingDirectory) {
        if (!containingDirectory.endsWith("/")) {
            throw new IllegalArgumentException("Containing directory must end with '/': " + containingDirectory);
        }
        if (containingDirectory.contains("$")) {
            throw new IllegalArgumentException("Containing directory cannot contain '$': " + containingDirectory);
        }
        String fullyQualifiedDirectory = containingDirectory + "$";
        Iterable<CharSequence> filePaths = radixTree.getKeysStartingWith(fullyQualifiedDirectory);
        List<String> fileNames = new LinkedList<String>();
        for (CharSequence filePath : filePaths) {
            fileNames.add(new StringBuilder(CharSequences.subtractPrefix(filePath, fullyQualifiedDirectory)).toString());
        }
        return fileNames;
    }

    @Override
    public Collection<F> getFilesInDirectory(String containingDirectory) {
        if (!containingDirectory.endsWith("/")) {
            throw new IllegalArgumentException("Containing directory must end with '/': " + containingDirectory);
        }
        if (containingDirectory.contains("$")) {
            throw new IllegalArgumentException("Containing directory cannot contain '$': " + containingDirectory);
        }
        String fullyQualifiedPath = containingDirectory + "$";
        return Iterables.toList(radixTree.getValuesForKeysStartingWith(fullyQualifiedPath));
    }

    @Override
    public Collection<String> getFileNamesInDirectoryRecursive(String containingDirectory) {
        if (!containingDirectory.endsWith("/")) {
            throw new IllegalArgumentException("Containing directory must end with '/': " + containingDirectory);
        }
        if (containingDirectory.contains("$")) {
            throw new IllegalArgumentException("Containing directory cannot contain '$': " + containingDirectory);
        }
        Iterable<CharSequence> filePaths = radixTree.getKeysStartingWith(containingDirectory);
        List<String> fileNames = new LinkedList<String>();
        for (CharSequence filePath : filePaths) {
            String filePathString = new StringBuilder(filePath).toString();
            filePathString = filePathString.replaceFirst(".*\\$", "");
            fileNames.add(filePathString);
        }
        return fileNames;
    }

    @Override
    public Collection<F> getFilesInDirectoryRecursive(String containingDirectory) {
        if (!containingDirectory.endsWith("/")) {
            throw new IllegalArgumentException("Containing directory must end with '/': " + containingDirectory);
        }
        if (containingDirectory.contains("$")) {
            throw new IllegalArgumentException("Containing directory cannot contain '$': " + containingDirectory);
        }
        return Iterables.toList(radixTree.getValuesForKeysStartingWith(containingDirectory));
    }

    @Override
    public Node getNode() {
        return ((PrettyPrintable)radixTree).getNode();
    }
}
