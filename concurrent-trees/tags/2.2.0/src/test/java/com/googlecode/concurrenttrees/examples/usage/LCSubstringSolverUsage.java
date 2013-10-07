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
package com.googlecode.concurrenttrees.examples.usage;

import com.googlecode.concurrenttrees.common.CharSequences;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory;
import com.googlecode.concurrenttrees.solver.LCSubstringSolver;

/**
 * @author Niall Gallagher
 */
public class LCSubstringSolverUsage {

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
}
