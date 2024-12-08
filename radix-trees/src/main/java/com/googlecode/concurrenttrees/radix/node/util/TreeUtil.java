package com.googlecode.concurrenttrees.radix.node.util;

import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.common.LazyIterator;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.Node;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class TreeUtil {

    private static final Comparator<NodeCharacterProvider> NODE_COMPARATOR = new NodeCharacterComparator();

    record NodeWithParent<T>(Node node, int parentIndex, NodeWithParent<T> parent, CharSequence key) implements KeyValuePair<T> {
        public NodeWithParent(Node node, int parentIndex, NodeWithParent<T> parent){
            this(node,parentIndex,parent,getKey(node,parent));
        }
        private static <T> CharSequence getKey(Node node, NodeWithParent<T> parent){
            if(parent!=null){
                return "" + parent.key + node.getIncomingEdge();
            }
            return node.getIncomingEdge();
        }

        @Override
        public CharSequence getKey() {
            return key;
        }

        @Override
        public T getValue() {
            return (T)node.getValue();
        }

        @Override
        public String toString() {
            return "(" + key + ", " + node.getValue() + ")";
        }
    }

    private static <T> NodeWithParent<T> getFirstLeafNode(NodeWithParent<T> node){
        if(node==null || node.node==null){
            return null;
        }
        Node current = node.node;
        if(current.getValue()!=null){
            return node;
        }
        List<Node> children = current.getOutgoingEdges();
        if(children.isEmpty()){
            //shouldn't happen?
            return null;
        }
        return getFirstLeafNode(new NodeWithParent<>(children.getFirst(), 0,node));
    }

    private static <T> NodeWithParent<T> getNextNode(NodeWithParent<T> current, boolean skipChildren){
        if(current==null || current.node==null){
            return null;
        }
        //check depth first if not skip children
        if(!skipChildren){
            List<Node> children = current.node.getOutgoingEdges();
            if(children!=null && !children.isEmpty()){
                return new NodeWithParent<>(children.getFirst(),0,current);
            }
        }
        //If root
        if(current.parent==null){
            return null;
        }

        //check siblings
        List<Node> siblings = current.parent.node.getOutgoingEdges();
        int nextIndex = current.parentIndex + 1;
        if(siblings!=null && nextIndex < siblings.size()){
            return new NodeWithParent<>(siblings.get(nextIndex),nextIndex, current.parent);
        }

        //if eldest, check parent's siblings
        return getNextNode(current.parent,true);
    }

    private static <T>  NodeWithParent<T> getMatchingOrNextNode(CharSequence searchKey, Node root){
        return searchTree(searchKey, root);
    }

    private static <T> NodeWithParent<T> searchTree(CharSequence key, Node root){
        NodeWithParent<T> currentNode = new NodeWithParent<>(root, -1, null);
        int charsMatched = 0;
        final int keyLength = key.length();
        outer: while (charsMatched < keyLength) {
            NodeWithParent<T> nextNode = getNextNodeInSubTree(currentNode,key.charAt(charsMatched), false);
            if (nextNode == null) {
                // Next node search parent
                currentNode = getNextNode(currentNode,true);
                break ;
            }
            currentNode = nextNode;
            CharSequence currentNodeEdgeCharacters = currentNode.node.getIncomingEdge();
            for (int i = 0, numEdgeChars = currentNodeEdgeCharacters.length(); i < numEdgeChars && charsMatched < keyLength; i++) {
                if (currentNodeEdgeCharacters.charAt(i) != key.charAt(charsMatched)) {
                    // Found a difference in chars between character in key and a character in current node.
                    // Current node is the deepest match (inexact match)....
                    break outer;
                }
                charsMatched++;
            }
        }
        return currentNode;
    }

    private static <T> NodeWithParent<T> getNextNodeInSubTree(NodeWithParent<T> parent, Character nextChar, boolean exactMatchOnly){
        if (parent==null || parent.node==null ){
            return null;
        }
        List<Node> children = parent.node.getOutgoingEdges();
        int index = binarySearch(nextChar,children);
        if(index >= 0){
            return new NodeWithParent<>(children.get(index),index,parent);
        }
        if(exactMatchOnly) {
            return null;
        }
        index = -index -1;
        if(index==children.size()){
            return null;
        }else{
            return new NodeWithParent<>(children.get(index),index,parent);
        }
    }

    private static int binarySearch(Character firstChar,  List<Node> children){
        NodeCharacterProvider searchKey = new NodeCharacterKey(firstChar);
        return Collections.binarySearch(children, searchKey, NODE_COMPARATOR);
    }

    public static class TreeKVIterator<T> extends LazyIterator<KeyValuePair<T>> {
        private NodeWithParent<T> next;
        public TreeKVIterator(ConcurrentRadixTree<T> tree, CharSequence startKey){
            next = getMatchingOrNextNode(startKey, tree.getNode());
        }

        @Override
        protected KeyValuePair<T> computeNext(){
            next = getFirstLeafNode(next);
            if(next ==null){
                return endOfData();
            }
            KeyValuePair<T> current = next;
            next = getNextNode(next,false);
            return current;
        }
    }

    public static class TreeIterator<T> implements Iterator<CharSequence> {
        private final Iterator<KeyValuePair<T>> delegate;
        public TreeIterator(ConcurrentRadixTree<T> tree, CharSequence startKey){
            delegate = new TreeKVIterator<>(tree,startKey);
        }
        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }
        @Override
        public CharSequence next() {
            KeyValuePair<T> next = delegate.next();
           if(next!=null){
               return next.getKey();
           }else{
               return null;
           }
        }
    }

    public static class TreeValueIterator<T> implements Iterator<T> {
        private final Iterator<KeyValuePair<T>> delegate;
        public TreeValueIterator(ConcurrentRadixTree<T> tree, CharSequence startKey){
            delegate = new TreeKVIterator<>(tree,startKey);
        }
        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }
        @Override
        public T next() {
            KeyValuePair<T> next = delegate.next();
            if(next!=null){
                return next.getValue();
            }else{
                return null;
            }
        }
    }
}
