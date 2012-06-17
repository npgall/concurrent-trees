package com.googlecode.concurrenttrees.common;

import com.googlecode.concurrenttrees.radix.node.Node;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;

import java.util.List;

/**
 * Utility methods to generate semi-graphical string representations of trees.
 *
 * @author Niall Gallagher
 */
public class PrettyPrintUtil {

    /**
     * Private constructor, not used.
     */
    PrettyPrintUtil() {
    }

    /**
     * Generates a semi-graphical string representation of a given tree.
     * <p/>
     * Example output:<br/>
     * <pre>
     * ○
     * └── ○ B (1)
     *     └── ○ A (2)
     *         └── ○ N (3)
     *             ├── ○ AN (5)
     *             │   └── ○ A (6)
     *             └── ○ DANA (4)
     * </pre>
     *
     * @param tree The tree for which the semi-graphical representation should be generated
     * @return A semi-graphical string representation of the tree
     */
    public static String prettyPrint(PrettyPrintable tree) {
        StringBuilder sb = new StringBuilder();
        prettyPrint(tree.getNode(), sb, "", true, true);
        return sb.toString();
    }

    static void prettyPrint(Node node, StringBuilder sb, String prefix, boolean isTail, boolean isRoot) {
        StringBuilder label = new StringBuilder();
        if (isRoot) {
            label.append("○");
            if (node.getIncomingEdge().length() > 0) {
                label.append(" ");
            }
        }
        label.append(node.getIncomingEdge());
        if (node.getValue() != null) {
            label.append(" (").append(node.getValue()).append(")");
        }
        sb.append(prefix).append(isTail ? isRoot ? "" : "└── ○ " : "├── ○ ").append(label).append("\n");
        List<Node> children = node.getOutgoingEdges();
        for (int i = 0; i < children.size() - 1; i++) {
            prettyPrint(children.get(i), sb, prefix + (isTail ? isRoot ? "" : "    " : "│   "), false, false);
        }
        if (!children.isEmpty()) {
            prettyPrint(children.get(children.size() - 1), sb, prefix + (isTail ? isRoot ? "" : "    " : "│   "), true, false);
        }
    }
}
