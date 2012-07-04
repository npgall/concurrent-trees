package com.googlecode.concurrenttrees.examples.filesystem;

import java.util.Collection;

/**
 * A skeleton interface for a file system, as proof of concept.
 *
 * @param <F> The type of file-like object to store in the file system
 *
 * @author Niall Gallagher
 */
public interface InMemoryFileSystem<F> {

    void addFile(String containingDirectory, String fileName, F file);

    F getFile(String containingDirectory, String fileName);

    Collection<String> getFileNamesInDirectory(String containingDirectory);

    Collection<F> getFilesInDirectory(String containingDirectory);

    Collection<String> getFileNamesInDirectoryRecursive(String containingDirectory);

    Collection<F> getFilesInDirectoryRecursive(String containingDirectory);
}
