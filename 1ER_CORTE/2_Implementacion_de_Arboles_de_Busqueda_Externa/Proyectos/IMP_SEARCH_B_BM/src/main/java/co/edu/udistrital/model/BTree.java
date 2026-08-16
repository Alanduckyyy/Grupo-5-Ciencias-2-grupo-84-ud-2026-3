package co.edu.udistrital.model;

/**
 * implementacion de arbol b generalizado para cualquier orden.
 */
public class BTree {
    public BTreeNode root;
    private int order;
    private int minKeys;

    public BTree(int order) {
        this.order = order;
        this.minKeys = (order - 1) / 2; 
        this.root = new BTreeNode(true);
    }

    private class SplitResult {
        int promotedKey;
        BTreeNode newNode;

        public SplitResult(int promotedKey, BTreeNode newNode) {
            this.promotedKey = promotedKey;
            this.newNode = newNode;
        }
    }

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

    public void insert(int key) {
        SplitResult result = insertRec(root, key);
        if (result != null) {
            BTreeNode newRoot = new BTreeNode(false);
            newRoot.keys.add(result.promotedKey);
            newRoot.children.add(root);
            newRoot.children.add(result.newNode);
            this.root = newRoot;
        }
    }

    private SplitResult insertRec(BTreeNode node, int key) {
        int idx = 0;
        while (idx < node.keys.size() && key > node.keys.get(idx)) {
            idx++;
        }

        if (node.isLeaf) {
            node.keys.add(idx, key);
            // Si el nodo alcanza la capacidad maxima (order), se divide
            if (node.keys.size() == order) {
                return splitNode(node);
            }
            return null;
        } else {
            SplitResult result = insertRec(node.children.get(idx), key);
            if (result != null) {
                node.keys.add(idx, result.promotedKey);
                node.children.add(idx + 1, result.newNode);
                if (node.keys.size() == order) {
                    return splitNode(node);
                }
            }
            return null;
        }
    }

    private SplitResult splitNode(BTreeNode node) {
        // Al calcular el punto medio dinámicamente, sirve para ordenes pares 
        // e impares
        int mid = (node.keys.size() - 1) / 2;
        int promotedKey = node.keys.get(mid);

        BTreeNode newNode = new BTreeNode(node.isLeaf);

        for (int i = mid + 1; i < node.keys.size(); i++) {
            newNode.keys.add(node.keys.get(i));
        }

        if (!node.isLeaf) {
            for (int i = mid + 1; i < node.children.size(); i++) {
                newNode.children.add(node.children.get(i));
            }
            node.children.subList(mid + 1, node.children.size()).clear();
        }

        node.keys.subList(mid, node.keys.size()).clear();

        return new SplitResult(promotedKey, newNode);
    }

    /**
     * eliminacion ascendente
     */
    public void delete(int key) {
        if (root == null || root.keys.isEmpty()) return;
        
        deleteRec(root, key);
        
        // Si la raíz se queda vacía y tiene hijos, el hijo pasa a ser la
        // nueva raíz
        if (root.keys.isEmpty() && !root.isLeaf && !root.children.isEmpty()) {
            root = root.children.get(0);
        }
    }

    private boolean deleteRec(BTreeNode node, int key) {
        int idx = 0;
        while (idx < node.keys.size() && key > node.keys.get(idx)) {
            idx++;
        }

        // Caso 1: La clave esta en este nodo
        if (idx < node.keys.size() && node.keys.get(idx) == key) {
            if (node.isLeaf) {
                node.keys.remove(idx);
                return true;
            } else {
                int pred = getPredecessor(node, idx);
                node.keys.set(idx, pred);
                deleteRec(node.children.get(idx), pred);
                
                // Chequeo contra el minKeys calculado
                if (node.children.get(idx).keys.size() < minKeys) {
                    fixUnderflow(node, idx);
                }
                return true;
            }
        }

        // Caso 2: La clave no esta en este nodo
        if (node.isLeaf) {
            return false;
        }

        boolean deleted = deleteRec(node.children.get(idx), key);

        // Si el hijo descendido queda por debajo del minimo calculado, 
        // corregimos
        if (node.children.get(idx).keys.size() < minKeys) {
            fixUnderflow(node, idx);
        }

        return deleted;
    }

    private int getPredecessor(BTreeNode node, int idx) {
        BTreeNode curr = node.children.get(idx);
        while (!curr.isLeaf) {
            curr = curr.children.get(curr.keys.size());
        }
        return curr.keys.get(curr.keys.size() - 1);
    }

    private void fixUnderflow(BTreeNode parent, int childIdx) {
        // Intentar pedir prestado al hermano izquierdo
        if (childIdx > 0 && parent.children.get(childIdx - 1).keys.size() 
                > minKeys) {
            borrowFromLeft(parent, childIdx);
        } 
        // Intentar pedir prestado al hermano derecho
        else if (childIdx < parent.children.size() - 1 
                && parent.children.get(childIdx + 1).keys.size() > minKeys) {
            borrowFromRight(parent, childIdx);
        } 
        // Si ningún hermano puede prestar, se fusionan
        else {
            if (childIdx > 0) {
                mergeNodes(parent, childIdx - 1);
            } else {
                mergeNodes(parent, childIdx);
            }
        }
    }

    private void borrowFromLeft(BTreeNode parent, int childIdx) {
        BTreeNode child = parent.children.get(childIdx);
        BTreeNode leftSibling = parent.children.get(childIdx - 1);

        child.keys.add(0, parent.keys.get(childIdx - 1));
        parent.keys.set(childIdx - 1, 
                leftSibling.keys.remove(leftSibling.keys.size() - 1));

        if (!child.isLeaf) {
            child.children.add(0, 
                    leftSibling.children.remove(leftSibling.children.size() 
                            - 1));
        }
    }

    private void borrowFromRight(BTreeNode parent, int childIdx) {
        BTreeNode child = parent.children.get(childIdx);
        BTreeNode rightSibling = parent.children.get(childIdx + 1);

        child.keys.add(parent.keys.get(childIdx));
        parent.keys.set(childIdx, rightSibling.keys.remove(0));

        if (!child.isLeaf) {
            child.children.add(rightSibling.children.remove(0));
        }
    }

    private void mergeNodes(BTreeNode parent, int idx) {
        BTreeNode left = parent.children.get(idx);
        BTreeNode right = parent.children.get(idx + 1);

        left.keys.add(parent.keys.remove(idx));
        left.keys.addAll(right.keys);
        
        if (!left.isLeaf) {
            left.children.addAll(right.children);
        }

        parent.children.remove(idx + 1);
    }
}