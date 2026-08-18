package co.edu.udistrital.model;

import java.util.ArrayList;
import java.util.List;

/**
 * nodode un arbol b.
 */
public class BTreeNode {
    public List<Integer> keys;
    public List<BTreeNode> children;
    public boolean isLeaf;

    public BTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
    }
}