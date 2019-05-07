package com.tree;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public char value;
    public int type;
    public int num;
    public List<Node> child = new ArrayList<>();

    public Node() {

    }
    public Node(char value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value + ":" + this.type + ":" + this.child + ";";
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o == this) {
            return true;
        }
        if(!(o instanceof Node)) {
            return false;
        }
        Node p = (Node) o;
        return p.value == this.value;
    }

    public static void insert(Node root, String word, int type) {
        if (word == null || word.isEmpty()) {
            return;
        }
        Node pNode = root;
        Node tmpNode = null;
        for (int i = 0; i < word.length(); i++) {
            tmpNode = new Node(word.charAt(i));
            int idx = pNode.child.indexOf(tmpNode);
            if(idx == -1) {
                pNode.child.add(tmpNode);
                idx = pNode.child.indexOf(tmpNode);
            } else {
                pNode.child.get(idx).num++;
            }
            pNode = pNode.child.get(idx);
        }
        pNode.type = type;
    }

    public static Node generateTree(String[] arr0,String[] arr1){
        int len0 = arr0.length;
        int len1 = arr1.length;
        Node root = new Node();
        for(int i=0;i<len0;i++){
            Node.insert(root, arr0[i],0);
        }
        for(int i=0;i<len1;i++){
            Node.insert(root, arr1[i],1);
        }
        return root;
    }

    public static void display(Node root) {
        System.out.println(root);
    }
}
