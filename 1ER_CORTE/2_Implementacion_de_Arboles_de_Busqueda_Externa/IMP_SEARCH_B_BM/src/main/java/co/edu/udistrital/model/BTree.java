package co.edu.udistrital.model;

/**
 * implementacion de arbol b .
 */
public class BTree {
    public BTreeNode root;
    private int order;
    private int minDegree;

    public BTree(int order) {
        this.order = order;
        this.minDegree = (order + 1) / 2;
        this.root = new BTreeNode(true);
    }

    /**
     * busca una clave en el arbol.
     */
    public boolean search(int key) {
        return searchNode(root, key);
    }

    private boolean searchNode(BTreeNode node, int key) {
        int idx = 0;
        while (idx < node.keys.size() && key > node.keys.get(idx)) {
            idx++;
        }
        if (idx < node.keys.size() && key == node.keys.get(idx)) {
            return true;
        }
        if (node.isLeaf) {
            return false;
        }
        return searchNode(node.children.get(idx), key);
    }

    /**
     * inserta una nueva clave manejando division de raiz si es necesario.
     */
    public void insert(int key) {
        BTreeNode currRoot = root;
        if (currRoot.keys.size() == order - 1) {
            BTreeNode newRoot = new BTreeNode(false);
            newRoot.children.add(currRoot);
            splitChild(newRoot, 0, currRoot);
            this.root = newRoot;
            insertNonFull(newRoot, key);
        } else {
            insertNonFull(currRoot, key);
        }
    }

    private void insertNonFull(BTreeNode node, int key) {
        int idx = node.keys.size() - 1;
        if (node.isLeaf) {
            node.keys.add(0);
            while (idx >= 0 && key < node.keys.get(idx)) {
                node.keys.set(idx + 1, node.keys.get(idx));
                idx--;
            }
            node.keys.set(idx + 1, key);
        } else {
            while (idx >= 0 && key < node.keys.get(idx)) {
                idx--;
            }
            idx++;
            if (node.children.get(idx).keys.size() == order - 1) {
                splitChild(node, idx, node.children.get(idx));
                if (key > node.keys.get(idx)) {
                    idx++;
                }
            }
            insertNonFull(node.children.get(idx), key);
        }
    }

    private void splitChild(BTreeNode parent, int idx, BTreeNode fullNode) {
        int mid = minDegree - 1;
        BTreeNode newNode = new BTreeNode(fullNode.isLeaf);
        
        for (int j = mid + 1; j < fullNode.keys.size(); j++) {
            newNode.keys.add(fullNode.keys.get(j));
        }
        if (!fullNode.isLeaf) {
            for (int j = mid + 1; j < fullNode.children.size(); j++) {
                newNode.children.add(fullNode.children.get(j));
            }
            fullNode.children.subList(mid + 1, 
                    fullNode.children.size()).clear();
        }
        int midKey = fullNode.keys.get(mid);
        fullNode.keys.subList(mid, fullNode.keys.size()).clear();
        
        parent.children.add(idx + 1, newNode);
        parent.keys.add(idx, midKey);
    }

    /**
     * elimina una clave del arbol previniendo underflow.
     */
    public void delete(int key) {
        if (root == null) return;
        deleteKey(root, key);
        if (root.keys.size() == 0) {
            root = root.isLeaf ? null : root.children.get(0);
        }
    }

    private void deleteKey(BTreeNode node, int key) {
        int idx = findKeyIndex(node, key);
        if (idx < node.keys.size() && node.keys.get(idx) == key) {
            if (node.isLeaf) {
                node.keys.remove(idx);
            } else {
                deleteInternal(node, idx);
            }
        } else {
            if (node.isLeaf) return;
            boolean isLast = (idx == node.keys.size());
            if (node.children.get(idx).keys.size() < minDegree) {
                fillChild(node, idx);
            }
            if (isLast && idx > node.keys.size()) {
                deleteKey(node.children.get(idx - 1), key);
            } else {
                deleteKey(node.children.get(idx), key);
            }
        }
    }

    private int findKeyIndex(BTreeNode node, int key) {
        int idx = 0;
        while (idx < node.keys.size() && node.keys.get(idx) < key) {
            idx++;
        }
        return idx;
    }

    private void deleteInternal(BTreeNode node, int idx) {
        int key = node.keys.get(idx);
        if (node.children.get(idx).keys.size() >= minDegree) {
            int pred = getPredecessor(node, idx);
            node.keys.set(idx, pred);
            deleteKey(node.children.get(idx), pred);
        } else if (node.children.get(idx + 1).keys.size() >= minDegree) {
            int succ = getSuccessor(node, idx);
            node.keys.set(idx, succ);
            deleteKey(node.children.get(idx + 1), succ);
        } else {
            mergeChildren(node, idx);
            deleteKey(node.children.get(idx), key);
        }
    }

    private int getPredecessor(BTreeNode node, int idx) {
        BTreeNode curr = node.children.get(idx);
        while (!curr.isLeaf) curr = curr.children.get(curr.keys.size());
        return curr.keys.get(curr.keys.size() - 1);
    }

    private int getSuccessor(BTreeNode node, int idx) {
        BTreeNode curr = node.children.get(idx + 1);
        while (!curr.isLeaf) curr = curr.children.get(0);
        return curr.keys.get(0);
    }

    private void fillChild(BTreeNode node, int idx) {
        if (idx != 0 && node.children.get(idx - 1).keys.size() >= minDegree) {
            borrowFromPrev(node, idx);
        } else if (idx != node.keys.size() && 
                node.children.get(idx + 1).keys.size() >= minDegree) {
            borrowFromNext(node, idx);
        } else {
            if (idx != node.keys.size()) mergeChildren(node, idx);
            else mergeChildren(node, idx - 1);
        }
    }

    private void borrowFromPrev(BTreeNode node, int idx) {
        BTreeNode child = node.children.get(idx);
        BTreeNode sibling = node.children.get(idx - 1);
        child.keys.add(0, node.keys.get(idx - 1));
        if (!child.isLeaf) {
            child.children.add(0, 
                    sibling.children.remove(sibling.children.size() - 1));
        }
        node.keys.set(idx - 1, sibling.keys.remove(sibling.keys.size() - 1));
    }

    private void borrowFromNext(BTreeNode node, int idx) {
        BTreeNode child = node.children.get(idx);
        BTreeNode sibling = node.children.get(idx + 1);
        child.keys.add(node.keys.get(idx));
        if (!child.isLeaf) {
            child.children.add(sibling.children.remove(0));
        }
        node.keys.set(idx, sibling.keys.remove(0));
    }

    private void mergeChildren(BTreeNode node, int idx) {
        BTreeNode child = node.children.get(idx);
        BTreeNode sibling = node.children.get(idx + 1);
        child.keys.add(node.keys.remove(idx));
        child.keys.addAll(sibling.keys);
        if (!child.isLeaf) {
            child.children.addAll(sibling.children);
        }
        node.children.remove(idx + 1);
    }
}