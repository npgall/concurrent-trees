package com.googlecode.concurrenttrees.wildcard.predicate.builtin;

import com.googlecode.concurrenttrees.wildcard.predicate.CharacterPredicate;

import java.util.Set;

/**
 * @author npgall
 */
public class IsWhitelistedCharacterPredicate implements CharacterPredicate {

    final Set<Character> whitelist;

    public IsWhitelistedCharacterPredicate(Set<Character> whitelist) {
        this.whitelist = whitelist;
    }

    @Override
    public boolean matches(char c) {
        return whitelist.contains(c);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IsWhitelistedCharacterPredicate)) return false;

        IsWhitelistedCharacterPredicate that = (IsWhitelistedCharacterPredicate) o;

        return whitelist.equals(that.whitelist);
    }

    @Override
    public int hashCode() {
        return whitelist.hashCode();
    }
}
