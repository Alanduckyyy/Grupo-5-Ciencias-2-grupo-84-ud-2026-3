package co.edu.udistrital.model;

import java.util.ArrayList;
import java.util.List;

/**
 * nodo para arbol b+ con puntero para lista
 */
public class BPlusNode {
    public List<Integer> keys;
    public List<BPlusNode> children;
    public boolean isLeaf;
    public BPlusNode nextLeaf; 

    public BPlusNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.nextLeaf = null;
    }
}