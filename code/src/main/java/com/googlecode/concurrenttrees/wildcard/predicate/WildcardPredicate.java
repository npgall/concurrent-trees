package com.googlecode.concurrenttrees.wildcard.predicate;

/**
 * @author npgall
 */
public class WildcardPredicate {

    final CharacterPredicate characterPredicate;
    final int minLength;
    final int maxLength;

    public WildcardPredicate(CharacterPredicate characterPredicate, int minLength, int maxLength) {
        if (characterPredicate == null) {
            throw new IllegalArgumentException("characterPredicate cannot be null");
        }
        if (minLength < 0) {
            throw new IllegalArgumentException("minLength cannot be negative: " + minLength);
        }
        if (maxLength < minLength) {
            throw new IllegalArgumentException("maxLength cannot be less than minLength: " + maxLength);
        }
        this.characterPredicate = characterPredicate;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WildcardPredicate)) return false;

        WildcardPredicate that = (WildcardPredicate) o;

        if (minLength != that.minLength) return false;
        if (maxLength != that.maxLength) return false;
        return characterPredicate.equals(that.characterPredicate);
    }

    @Override
    public int hashCode() {
        int result = characterPredicate.hashCode();
        result = 31 * result + minLength;
        result = 31 * result + maxLength;
        return result;
    }

    @Override
    public String toString() {
        if (minLength == 0 && maxLength == 1) {
            return characterPredicate + "?";
        }
        else if (minLength == 0 && maxLength == Integer.MAX_VALUE) {
            return characterPredicate + "*";
        }
        else if (minLength == 1 && maxLength == Integer.MAX_VALUE) {
            return characterPredicate + "+";
        }
        else if (minLength == maxLength) {
            return characterPredicate + "{" + minLength + "}";
        }
        else if (maxLength == Integer.MAX_VALUE) {
            return characterPredicate + "{" + minLength + ",}";
        }
        else {
            return characterPredicate.toString() + "{" + minLength + "," + maxLength + "}";
        }
    }
}
