An in-memory file system implemented on top of ConcurrentRadixTree, is included in the source [here](http://concurrent-trees.googlecode.com/svn/concurrent-trees/trunk/src/test/java/com/googlecode/concurrenttrees/examples/filesystem/). This code is currently just proof of concept, so is located in the `test/` directory rather than `main/`.

## Example Usage for In-Memory File System (proof of concept) ##
### Objective ###
  * Create a bunch of Brochure objects, and store those in various "directories" and with various "file names" in the in-memory file system
  * Retrieve files by searching directories recursively etc.

### Code ###
```
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
```

### Output ###
```
Internal file system representation (not public):-
○
└── ○ /brochures/
    ├── ○ ford/$ford_f
    │   ├── ○ 150_brochure.txt (Marketing stuff for Ford F150)
    │   └── ○ ocus_brochure.txt (Marketing stuff for Ford Focus)
    └── ○ honda/$honda_civic_brochure.txt (Marketing stuff for Honda Civic)

Retrieve Ford brochure names in directory: [ford_f150_brochure.txt, ford_focus_brochure.txt]
Retrieve Honda brochure names in directory: [honda_civic_brochure.txt]
Retrieve All brochure names recursively: [ford_f150_brochure.txt, ford_focus_brochure.txt, honda_civic_brochure.txt]

Retrieve Ford F150 brochure contents using exact file name: Marketing stuff for Ford F150

Retrieve all Ford brochure contents in directory:-
Marketing stuff for Ford F150
Marketing stuff for Ford Focus

Retrieve contents from entire file system recursively:-
Marketing stuff for Ford F150
Marketing stuff for Ford Focus
Marketing stuff for Honda Civic
```