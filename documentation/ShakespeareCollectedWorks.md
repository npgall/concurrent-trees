The Collected Works of William Shakespeare was used as a dataset to test trees during development. This is not actually a large dataset by today's standards, at <10 MB of text files, but nonetheless useful for testing. It's unclear how the Bard would feel about his work being used for this purpose, no offense intended!

Standalone programs to build trees from and analyze The Collected Works of William Shakespeare can be found [here](http://concurrent-trees.googlecode.com/svn/concurrent-trees/trunk/src/test/java/com/googlecode/concurrenttrees/examples/shakespeare/).

Some output from these programs:

  * Radix Tree of the **individual words** contained in all of Shakespeare's works
    * Tree [viewable here](http://concurrent-trees.googlecode.com/svn/concurrent-trees/trunk/src/test/resources/shakespeare-trees/radix-tree-words-shakespeare-collected-works.txt)
    * Values are the works containing those words
    * 29,008 nodes

  * Suffix Tree of the **individual words** contained in all of Shakespeare's works
    * Tree [viewable here](http://concurrent-trees.googlecode.com/svn/concurrent-trees/trunk/src/test/resources/shakespeare-trees/suffix-tree-words-shakespeare-collected-works.txt)
    * Values in this output are the complete words associated with each suffix
    * 75,780 nodes

  * Suffix Tree of Shakespeare's Tragedy, Antony and Cleopatra
    * 280 MB in RAM using `DefaultCharSequenceNodeFactory`
    * Insufficient RAM to build tree using `DefaultCharArrayNodeFactory`
    * 29.438 GB if written to disk (highlights problems suffered by `DefaultCharArrayNodeFactory`)
    * 217,697 nodes

  * Suffix Tree of the entirety of Shakespeare's Tragedies (10 plays)
    * 2.0 GB in RAM using `DefaultCharSequenceNodeFactory`
    * Insufficient RAM to build tree using `DefaultCharArrayNodeFactory`
    * 248.997 GB if written to disk (highlights memory which would be required by `DefaultCharArrayNodeFactory`)
    * 1,965,884 nodes

  * The Longest Common Substring of Shakespeare's Tragedies (10 plays)
    * Output [viewable here](http://concurrent-trees.googlecode.com/svn/concurrent-trees/trunk/src/test/resources/shakespeare-trees/tragedies-longest-common-substring.txt)
    * Longest common substring is: "**` dramatis personae `**"