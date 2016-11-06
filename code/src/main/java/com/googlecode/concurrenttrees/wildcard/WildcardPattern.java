package com.googlecode.concurrenttrees.wildcard;

import java.util.List;

/**
 * @author npgall
 */
public class WildcardPattern {

    final List<String> segments;

    public WildcardPattern(List<String> segments) {
        this.segments = segments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WildcardPattern)) return false;

        WildcardPattern that = (WildcardPattern) o;

        return segments.equals(that.segments);
    }

    @Override
    public int hashCode() {
        return segments.hashCode();
    }
}
