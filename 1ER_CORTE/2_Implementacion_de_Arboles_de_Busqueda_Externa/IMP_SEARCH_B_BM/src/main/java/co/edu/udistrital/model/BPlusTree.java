package co.edu.udistrital.model;

import java.util.Collections;

/**
 * arbol b+.
 */
public class BPlusTree {
    public BPlusNode root;
    private int order;

    public BPlusTree(int order) {
        this.order = order;
        this.root = new BPlusNode(true);
    }

    /**
     * split hacia arriba.
     */
    private class SplitResult {
        public int promotedKey;
        public BPlusNode newNode;

        public SplitResult(int key, BPlusNode node) {
            this.promotedKey = key;
            this.newNode = node;
        }
    }

    public void insert(int key) {
        SplitResult result = insertRec(root, key);
        if (result != null) {
            BPlusNode newRoot = new BPlusNode(false);
            newRoot.keys.add(result.promotedKey);
            newRoot.children.add(root);
            newRoot.children.add(result.newNode);
            root = newRoot;
        }
    }

    private SplitResult insertRec(BPlusNode node, int key) {
        if (node.isLeaf) {
            if (!node.keys.contains(key)) {
                node.keys.add(key);
                Collections.sort(node.keys);
            }
            if (node.keys.size() == order) {
                return splitLeaf(node);
            }
            return null;
        } else {
            int idx = 0;
            while (idx < node.keys.size() && key >= node.keys.get(idx)) {
                idx++;
            }
            SplitResult result = insertRec(node.children.get(idx), key);
            if (result != null) {
                node.keys.add(idx, result.promotedKey);
                node.children.add(idx + 1, result.newNode);
                if (node.keys.size() == order) {
                    return splitInternal(node);
                }
            }
            return null;
        }
    }

    private SplitResult splitLeaf(BPlusNode leaf) {
        BPlusNode newLeaf = new BPlusNode(true);
        int mid = leaf.keys.size() / 2;
        
        newLeaf.keys.addAll(leaf.keys.subList(mid, leaf.keys.size()));
        leaf.keys.subList(mid, leaf.keys.size()).clear();
        
        // mantiene la lista enlazada
        newLeaf.nextLeaf = leaf.nextLeaf;
        leaf.nextLeaf = newLeaf;
        
        return new SplitResult(newLeaf.keys.get(0), newLeaf);
    }

    private SplitResult splitInternal(BPlusNode internal) {
        BPlusNode newInternal = new BPlusNode(false);
        int mid = internal.keys.size() / 2;
        int promoted = internal.keys.get(mid);
        
        newInternal.keys.addAll(internal.keys.subList(mid + 1, 
                internal.keys.size()));
        newInternal.children.addAll(internal.children.subList(mid + 1, 
                internal.children.size()));
        
        internal.keys.subList(mid, internal.keys.size()).clear();
        internal.children.subList(mid + 1, internal.children.size()).clear();
        
        return new SplitResult(promoted, newInternal);
    }

    /**
     * eliminacionenfocado en la hoja.
     */
    public void delete(int key) {
        BPlusNode curr = root;
        while (!curr.isLeaf) {
            int idx = 0;
            while (idx < curr.keys.size() && key >= curr.keys.get(idx)) {
                idx++;
            }
            curr = curr.children.get(idx);
        }
        curr.keys.remove(Integer.valueOf(key));
    }
}