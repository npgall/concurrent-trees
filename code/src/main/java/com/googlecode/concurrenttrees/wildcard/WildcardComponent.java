package com.googlecode.concurrenttrees.wildcard;

import com.googlecode.concurrenttrees.wildcard.predicate.WildcardPredicate;

/**
 * Represents a character sequence with a leading wildcard.
 *
 * @author npgall
 */
public class WildcardComponent {

    final WildcardPredicate wildcardPredicate;
    final CharSequence characterSequence;

    public WildcardComponent(WildcardPredicate wildcardPredicate, CharSequence characterSequence) {
        this.wildcardPredicate = wildcardPredicate;
        this.characterSequence = characterSequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WildcardComponent)) return false;

        WildcardComponent that = (WildcardComponent) o;

        if (!wildcardPredicate.equals(that.wildcardPredicate)) return false;
        return characterSequence.equals(that.characterSequence);
    }

    @Override
    public int hashCode() {
        int result = wildcardPredicate.hashCode();
        result = 31 * result + characterSequence.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "WildcardComponent{" +
                "wildcardPredicate=" + wildcardPredicate +
                ", characterSequence=" + characterSequence +
                '}';
    }
}
