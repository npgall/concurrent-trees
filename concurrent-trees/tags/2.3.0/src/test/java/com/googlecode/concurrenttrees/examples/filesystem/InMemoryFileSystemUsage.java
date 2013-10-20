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

import com.googlecode.concurrenttrees.common.PrettyPrinter;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;

import java.util.Collection;

/**
 * Example usage of the {@link InMemoryFileSystem} proof of concept.
 * <p/>
 * Creates a bunch of Brochure objects, and stores those in various "directories" and with various "file names" in the
 * in-memory file system. Then retrieves files by searching directories recursively etc.
 *
 * @author Niall Gallagher
 */
public class InMemoryFileSystemUsage {

    static class Brochure {
        final String content;

        Brochure(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }

    public static void main(String[] args) {
        // A file system to store Brochure objects...
        InMemoryFileSystem<Brochure> fileSystem = new ConcurrentRadixTreeInMemoryFileSystem<Brochure>();

        Brochure fordFocusBrochure = new Brochure("Marketing stuff for Ford Focus");
        Brochure fordF150Brochure = new Brochure("Marketing stuff for Ford F150");
        Brochure hondaCivicBrochure = new Brochure("Marketing stuff for Honda Civic");

        fileSystem.addFile("/brochures/ford/", "ford_focus_brochure.txt", fordFocusBrochure);
        fileSystem.addFile("/brochures/ford/", "ford_f150_brochure.txt", fordF150Brochure);
        fileSystem.addFile("/brochures/honda/", "honda_civic_brochure.txt", hondaCivicBrochure);

        System.out.println("Internal file system representation (not public):-");
        PrettyPrinter.prettyPrint((PrettyPrintable) fileSystem, System.out);

        System.out.println();
        System.out.println("Retrieve Ford brochure names in directory: " + fileSystem.getFileNamesInDirectory("/brochures/ford/"));
        System.out.println("Retrieve Honda brochure names in directory: " + fileSystem.getFileNamesInDirectory("/brochures/honda/"));
        System.out.println("Retrieve All brochure names recursively: " + fileSystem.getFileNamesInDirectoryRecursive("/brochures/"));

        System.out.println();
        Brochure fordF150BrochureRetrieved = fileSystem.getFile("/brochures/ford/", "ford_f150_brochure.txt");
        System.out.println("Retrieve Ford F150 brochure contents using exact file name: " + fordF150BrochureRetrieved);

        System.out.println();
        System.out.println("Retrieve all Ford brochure contents in directory:-");
        Collection<Brochure> fordBrochuresRetrieved = fileSystem.getFilesInDirectory("/brochures/ford/");
        for (Brochure fordBrochure : fordBrochuresRetrieved) {
            System.out.println(fordBrochure);
        }

        System.out.println();
        System.out.println("Retrieve contents from entire file system recursively:-");
        Collection<Brochure> allFiles = fileSystem.getFilesInDirectoryRecursive("/");
        for (Brochure file : allFiles) {
            System.out.println(file);
        }
    }
}
