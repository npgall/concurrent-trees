package com.googlecode.concurrenttrees.wildcard.predicate.builtin;

import com.googlecode.concurrenttrees.wildcard.predicate.CharacterPredicate;

/**
 * @author npgall
 */
public class IsAnyCharacterPredicate implements CharacterPredicate {

    @Override
    public boolean matches(char c) {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IsAnyCharacterPredicate;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
