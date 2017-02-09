package com.googlecode.concurrenttrees.wildcard.predicate;

import com.googlecode.concurrenttrees.common.CharSequences;
import com.googlecode.concurrenttrees.wildcard.predicate.builtin.*;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author npgall
 */
public class WildcardPredicatesTest {

    @Test
    public void testAnyCharacterPredicate() {
        assertEquals(
                asList("", "F", "FO", "FOO", "FOO ", "FOO B", "FOO BA", "FOO BAR"),
                generateMatchingPrefixes("FOO BAR", withPredicate(anyCharacter(), 0, Integer.MAX_VALUE))
        );
        assertEquals(
                asList("F", "FO", "FOO", "FOO ", "FOO B", "FOO BA", "FOO BAR"),
                generateMatchingPrefixes("FOO BAR", withPredicate(anyCharacter(), 1, Integer.MAX_VALUE))
        );
        assertEquals(
                asList("FO"),
                generateMatchingPrefixes("FOO BAR", withPredicate(anyCharacter(), 2, 2))
        );
        assertEquals(
                asList("FO", "FOO"),
                generateMatchingPrefixes("FOO BAR", withPredicate(anyCharacter(), 2, 3))
        );
        assertEquals(
                asList(""),
                generateMatchingPrefixes("FOO BAR", withPredicate(anyCharacter(), 0, 0))
        );
    }

    @Test
    public void testWhitespacePredicate() {
        assertEquals(
                asList("", "F", "FO", "FOO"),
                generateMatchingPrefixes("FOO BAR", withPredicate(notWhitespace(), 0, Integer.MAX_VALUE))
        );
        assertEquals(
                asList("F", "FO", "FOO"),
                generateMatchingPrefixes("FOO BAR", withPredicate(notWhitespace(), 1, Integer.MAX_VALUE))
        );
        assertEquals(
                asList("FO"),
                generateMatchingPrefixes("FOO BAR", withPredicate(notWhitespace(), 2, 2))
        );
        assertEquals(
                asList("FO", "FOO"),
                generateMatchingPrefixes("FOO BAR", withPredicate(notWhitespace(), 2, 3))
        );
        assertEquals(
                asList(""),
                generateMatchingPrefixes("FOO BAR", withPredicate(notWhitespace(), 0, 0))
        );
    }

    @Test
    public void testWhitelistedPredicate() {
        assertEquals(
                asList("", "F", "FO", "FOO"),
                generateMatchingPrefixes("FOO BAR", withPredicate(whitelistedCharacter("FOBAR"), 0, Integer.MAX_VALUE))
        );
        assertEquals(
                asList("F", "FO", "FOO"),
                generateMatchingPrefixes("FOO BAR", withPredicate(whitelistedCharacter("FOBAR"), 1, Integer.MAX_VALUE))
        );
        assertEquals(
                asList("FO"),
                generateMatchingPrefixes("FOO BAR", withPredicate(whitelistedCharacter("FOBAR"), 2, 2))
        );
        assertEquals(
                asList("FO", "FOO"),
                generateMatchingPrefixes("FOO BAR", withPredicate(whitelistedCharacter("FOBAR"), 2, 3))
        );
        assertEquals(
                asList(""),
                generateMatchingPrefixes("FOO BAR", withPredicate(whitelistedCharacter("FOBAR"), 0, 0))
        );
    }

    @Test
    public void testBlacklistedPredicate() {
        assertEquals(
                asList("", "F", "FO", "FOO"),
                generateMatchingPrefixes("FOO BAR", withPredicate(notBlacklistedCharacter(" "), 0, Integer.MAX_VALUE))
        );
        assertEquals(
                asList("F", "FO", "FOO"),
                generateMatchingPrefixes("FOO BAR", withPredicate(notBlacklistedCharacter(" "), 1, Integer.MAX_VALUE))
        );
        assertEquals(
                asList("FO"),
                generateMatchingPrefixes("FOO BAR", withPredicate(notBlacklistedCharacter(" "), 2, 2))
        );
        assertEquals(
                asList("FO", "FOO"),
                generateMatchingPrefixes("FOO BAR", withPredicate(notBlacklistedCharacter(" "), 2, 3))
        );
        assertEquals(
                asList(""),
                generateMatchingPrefixes("FOO BAR", withPredicate(notBlacklistedCharacter(" "), 0, 0))
        );
    }

    @Test
    public void testHexadecimalPredicate() {
        assertEquals(
                asList("", "A", "AB", "ABC", "ABCD", "ABCDE", "ABCDEF", "ABCDEFa", "ABCDEFab", "ABCDEFabc", "ABCDEFabcd", "ABCDEFabcde", "ABCDEFabcdef", "ABCDEFabcdef0", "ABCDEFabcdef01", "ABCDEFabcdef012", "ABCDEFabcdef0123", "ABCDEFabcdef01234", "ABCDEFabcdef012345", "ABCDEFabcdef0123456", "ABCDEFabcdef01234567", "ABCDEFabcdef012345678", "ABCDEFabcdef0123456789"),
                generateMatchingPrefixes("ABCDEFabcdef0123456789Zz", withPredicate(hexadecimal(), 0, Integer.MAX_VALUE))
        );
        assertEquals(
                asList("A", "AB", "ABC", "ABCD", "ABCDE", "ABCDEF", "ABCDEFa", "ABCDEFab", "ABCDEFabc", "ABCDEFabcd", "ABCDEFabcde", "ABCDEFabcdef", "ABCDEFabcdef0", "ABCDEFabcdef01", "ABCDEFabcdef012", "ABCDEFabcdef0123", "ABCDEFabcdef01234", "ABCDEFabcdef012345", "ABCDEFabcdef0123456", "ABCDEFabcdef01234567", "ABCDEFabcdef012345678", "ABCDEFabcdef0123456789"),
                generateMatchingPrefixes("ABCDEFabcdef0123456789Zz", withPredicate(hexadecimal(), 1, Integer.MAX_VALUE))
        );
        assertEquals(
                asList("ABCDEFabcdef0123456789"),
                generateMatchingPrefixes("ABCDEFabcdef0123456789Zz", withPredicate(hexadecimal(), 22, 22))
        );
        assertEquals(
                asList("A", "AB", "ABC"),
                generateMatchingPrefixes("ABCDEFabcdef0123456789Zz", withPredicate(hexadecimal(), 1, 3))
        );
        assertEquals(
                asList(""),
                generateMatchingPrefixes("ABCDEFabcdef0123456789Zz", withPredicate(hexadecimal(), 0, 0))
        );
    }

    @Test
    public void testConstructor() {
        assertNotNull(new WildcardPredicates());
    }

    // ***** Boilerplate test helper methods... *****

    private static List<String> generateMatchingPrefixes(CharSequence input, WildcardPredicate wildcardPredicate) {
        return asStrings(WildcardPredicates.generateMatchingPrefixes(input, wildcardPredicate));
    }

    private static List<String> asStrings(Iterable<CharSequence> charSequences) {
        List<String> result = new LinkedList<String>();
        for (CharSequence charSequence : charSequences) {
            result.add(CharSequences.toString(charSequence));
        }
        return result;
    }

    private static WildcardPredicate withPredicate(CharacterPredicate characterPredicate, int minOccurrences, int maxOccurrences) {
        return new WildcardPredicate(characterPredicate, minOccurrences, maxOccurrences);
    }

    private static Set<Character> distinctCharacters(String chars) {
        Set<Character> characters = new HashSet<Character>();
        for (int i = 0; i < chars.length(); i++) {
            characters.add(chars.charAt(i));
        }
        return characters;
    }

    static CharacterPredicate notWhitespace() {
        return new NotWhitespaceCharacterPredicate();
    }

    static CharacterPredicate anyCharacter() {
        return new IsAnyCharacterPredicate();
    }

    static CharacterPredicate whitelistedCharacter(String characters) {
        return new IsWhitelistedCharacterPredicate(distinctCharacters(characters));
    }

    static CharacterPredicate notBlacklistedCharacter(String characters) {
        return new NotBlacklistedCharacterPredicate(distinctCharacters(characters));
    }

    static CharacterPredicate hexadecimal() {
        return new IsHexadecimalCharacterPredicate();
    }
}