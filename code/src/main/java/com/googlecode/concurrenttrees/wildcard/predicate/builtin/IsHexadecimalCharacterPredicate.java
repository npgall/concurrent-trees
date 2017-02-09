package com.googlecode.concurrenttrees.wildcard.predicate.builtin;

import com.googlecode.concurrenttrees.wildcard.predicate.CharacterPredicate;

import java.util.Set;

/**
 * @author npgall
 */
public class IsHexadecimalCharacterPredicate implements CharacterPredicate {

    @Override
    public boolean matches(char c) {
        return c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f';
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IsHexadecimalCharacterPredicate;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
