package com.googlecode.concurrenttrees.wildcard.predicate.builtin;

import com.googlecode.concurrenttrees.wildcard.predicate.CharacterPredicate;

import java.util.Set;

/**
 * @author npgall
 */
public class NotBlacklistedCharacterPredicate implements CharacterPredicate {

    final Set<Character> blacklist;

    public NotBlacklistedCharacterPredicate(Set<Character> blacklist) {
        this.blacklist = blacklist;
    }

    @Override
    public boolean matches(char c) {
        return !blacklist.contains(c);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotBlacklistedCharacterPredicate)) return false;

        NotBlacklistedCharacterPredicate that = (NotBlacklistedCharacterPredicate) o;

        return blacklist.equals(that.blacklist);
    }

    @Override
    public int hashCode() {
        return blacklist.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[^");
        for (Character c : blacklist) {
            sb.append(c);
        }
        sb.append("]");
        return sb.toString();
    }
}
