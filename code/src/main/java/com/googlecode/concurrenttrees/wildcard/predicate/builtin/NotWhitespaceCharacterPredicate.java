package com.googlecode.concurrenttrees.wildcard.predicate.builtin;

import com.googlecode.concurrenttrees.wildcard.predicate.CharacterPredicate;

/**
 * @author npgall
 */
public class NotWhitespaceCharacterPredicate implements CharacterPredicate {

    @Override
    public boolean matches(char c) {
        return !Character.isWhitespace(c);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NotWhitespaceCharacterPredicate;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "[^\\w]";
    }
}
