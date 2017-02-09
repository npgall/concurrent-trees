package com.googlecode.concurrenttrees.wildcard.predicate;

import com.googlecode.concurrenttrees.common.LazyIterator;

import java.util.Iterator;

/**
 * @author npgall
 */
public class WildcardPredicates {

    /**
     * Generates {@link CharSequence}s which are progressively longer prefixes of the given input and which match
     * the given {@link WildcardPredicate}.
     * <p>
     *     The {@link WildcardPredicate} can specify restrictions on the prefixes which will be generated:
     *     <ul>
     *         <li>
     *             A minimum and/or maximum length of the prefixes required.
     *         </li>
     *         <li>
     *             A {@link CharacterPredicate} which ensures that the prefixes generated will only contain
     *             characters which match the predicate.
     *             The method will stop generating prefixes when any characters which do not match this predicate
     *             are encountered.
     *         </li>
     *     </ul>
     * </p>
     * For example usage, see the unit tests in {@code WildcardPredicatesTest}.
     *
     * @param input
     * @param wildcardPredicate Encapsulates
     * @return
     */
    public static Iterable<CharSequence> generateMatchingPrefixes(final CharSequence input, final WildcardPredicate wildcardPredicate) {
        return new Iterable<CharSequence>() {
            @Override
            public Iterator<CharSequence> iterator() {
                return new LazyIterator<CharSequence>() {

                    final CharacterPredicate characterPredicate = wildcardPredicate.characterPredicate;
                    final int minOccurrences = wildcardPredicate.minLength;
                    final int maxOccurrences = wildcardPredicate.maxLength;
                    final int length = input.length();

                    int endIndex = 0;

                    @Override
                    protected CharSequence computeNext() {
                        while (endIndex <= length && endIndex <= maxOccurrences) {
                            if (endIndex > 0 && !characterPredicate.matches(input.charAt(endIndex - 1))) {
                                return endOfData();
                            }
                            if (endIndex >= minOccurrences) {
                                return input.subSequence(0, endIndex++);
                            }
                            endIndex++;
                        }
                        return endOfData();
                    }
                };
            }
        };
    }

    /**
     * Private constructor, not used.
     */
    WildcardPredicates() {
    }
}
