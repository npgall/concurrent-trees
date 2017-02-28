package com.googlecode.concurrenttrees.wildcard;

import com.googlecode.concurrenttrees.common.PrettyPrinter;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.wildcard.node.DefaultWildcardNodeFactory;
import com.googlecode.concurrenttrees.wildcard.predicate.WildcardPredicate;
import com.googlecode.concurrenttrees.wildcard.predicate.builtin.IsAnyCharacterPredicate;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author npgall
 */
public class ConcurrentWildcardTreeTest {

    @Test
    public void testConcurrentWildcardTree() {
        ConcurrentWildcardTree<String> tree = new ConcurrentWildcardTree<String>(new DefaultCharArrayNodeFactory(), new DefaultWildcardNodeFactory());

        WildcardPattern wildcardPattern = new WildcardPattern(Arrays.asList(
            new WildcardComponent(new WildcardPredicate(new IsAnyCharacterPredicate(), 0, 0), "/foo/"),
            new WildcardComponent(new WildcardPredicate(new IsAnyCharacterPredicate(), 0, Integer.MAX_VALUE), "/bar")
        ));
        tree.put(wildcardPattern, "xyz");
        PrettyPrinter.prettyPrint(tree, System.out);
    }

}