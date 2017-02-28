package com.googlecode.concurrenttrees.wildcard;

import java.util.List;

/**
 * @author npgall
 */
public class WildcardPattern {

    final List<WildcardComponent> wildcardComponents;

    public WildcardPattern(List<WildcardComponent> wildcardComponents) {
        this.wildcardComponents = wildcardComponents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WildcardPattern)) return false;

        WildcardPattern that = (WildcardPattern) o;

        return wildcardComponents.equals(that.wildcardComponents);
    }

    @Override
    public int hashCode() {
        return wildcardComponents.hashCode();
    }

    @Override
    public String toString() {
        return "WildcardPattern{" +
                "wildcardComponents=" + wildcardComponents +
                '}';
    }
}
