package co.edu.udistrital.model;

import java.util.Collections;

/**
 * arbol b+ 
 */
public class BPlusTree {
    public BPlusNode root;
    private int order;

    public BPlusTree(int order) {
        this.order = order;
        this.root = new BPlusNode(true);
    }

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
     * eliminacion
     */
    public void delete(int key) {
        if (root == null) return;
        deleteRec(root, key);
        
        /** Si la raiz se queda sin claves y tiene un solo hijo, 
         *  se promueve ese hijo a raiz
         */
                while (!root.isLeaf && root.keys.isEmpty() 
                        && !root.children.isEmpty()) {
            root = root.children.get(0);
        }
    }

    private void deleteRec(BPlusNode node, int key) {
        if (node.isLeaf) {
            node.keys.remove(Integer.valueOf(key));
        } else {
            int idx = 0;
            while (idx < node.keys.size() && key >= node.keys.get(idx)) {
                idx++;
            }
            
            if (idx >= node.children.size()) {
                idx = node.children.size() - 1;
            }

            BPlusNode child = node.children.get(idx);
            deleteRec(child, key);

            // 1. Si la hoja queda vacia, y se elimina del padre
            if (child.isLeaf && child.keys.isEmpty()) {
                if (idx > 0) {
                    node.children.get(idx - 1).nextLeaf = child.nextLeaf;
                }
                
                node.children.remove(idx);
                if (idx > 0 && idx - 1 < node.keys.size()) {
                    node.keys.remove(idx - 1);
                } else if (!node.keys.isEmpty()) {
                    node.keys.remove(0);
                }
            } 
            // 2. Si un nodo indice se quedo sin claves por perder su hoja,
            // colapsa la envoltura conectando a su unico hijo con el nivel 
            // superior
            else if (!child.isLeaf && child.keys.isEmpty()) {
                if (!child.children.isEmpty()) {
                    node.children.set(idx, child.children.get(0));
                } else {
                    node.children.remove(idx);
                    if (idx > 0 && idx - 1 < node.keys.size()) {
                        node.keys.remove(idx - 1);
                    } else if (!node.keys.isEmpty()) {
                        node.keys.remove(0);
                    }
                }
            }
        }
    }
}