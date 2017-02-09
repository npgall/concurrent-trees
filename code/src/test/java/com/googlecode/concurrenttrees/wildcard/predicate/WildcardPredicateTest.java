package com.googlecode.concurrenttrees.wildcard.predicate;

import com.googlecode.concurrenttrees.wildcard.predicate.builtin.IsAnyCharacterPredicate;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author npgall
 */
public class WildcardPredicateTest {

    @Test
    public void testValidArguments1() {
        assertNotNull(new WildcardPredicate(new IsAnyCharacterPredicate(), 0, 5));
    }

    @Test
    public void testValidArguments2() {
        assertNotNull(new WildcardPredicate(new IsAnyCharacterPredicate(), 0, 0));
    }

    @Test
    public void testValidArguments3() {
        assertNotNull(new WildcardPredicate(new IsAnyCharacterPredicate(), 1, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidArguments1() {
        //noinspection ConstantConditions
        assertNotNull(new WildcardPredicate(null, 0, 5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidArguments2() {
        assertNotNull(new WildcardPredicate(new IsAnyCharacterPredicate(), -1, 5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidArguments3() {
        assertNotNull(new WildcardPredicate(new IsAnyCharacterPredicate(), 5, 1));
    }

}