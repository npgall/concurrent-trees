## Find the Longest Common Substring in a Collection of Documents ##
### Objective ###
  * Solve the [longest common substring problem](http://en.wikipedia.org/wiki/Longest_common_substring_problem) using the included `LCSubstringSolver`

### Code ###
```
static final String document1 =
        "albert einstein, was a german theoretical physicist who developed the theory of general relativity";

static final String document2 =
        "near the beginning of his career, albert einstein thought that newtonian mechanics was no longer " +
        "enough to reconcile the laws of classical mechanics with the laws of the electromagnetic field";

static final String document3 =
        "in late summer 1895, at the age of sixteen, albert einstein sat the entrance examinations for " +
        "the swiss federal polytechnic in zurich";

public static void main(String[] args) {
    LCSubstringSolver solver = new LCSubstringSolver(new DefaultCharSequenceNodeFactory());

    solver.add(document1);
    solver.add(document2);
    solver.add(document3);

    String longestCommonSubstring = CharSequences.toString(solver.getLongestCommonSubstring());
    System.out.println(longestCommonSubstring);
}
```

### Output ###
```
albert einstein
```