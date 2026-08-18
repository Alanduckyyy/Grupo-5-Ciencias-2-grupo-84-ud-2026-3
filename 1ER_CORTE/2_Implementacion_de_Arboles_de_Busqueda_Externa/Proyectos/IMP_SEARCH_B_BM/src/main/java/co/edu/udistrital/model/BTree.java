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
        
        // Si la raiz se queda vacia y tiene hijos, el hijo pasa a ser la nueva 
        // raiz
        if (root.keys.isEmpty() && !root.isLeaf && !root.children.isEmpty()) {
            root = root.children.get(0);
        }
    }

    private boolean deleteRec(BTreeNode node, int key) {
        int idx = 0;
        while (idx < node.keys.size() && key > node.keys.get(idx)) {
            idx++;
        }

        // Caso 1: La clave está en este nodo
        if (idx < node.keys.size() && node.keys.get(idx) == key) {
            if (node.isLeaf) {
                node.keys.remove(idx);
                return true;
            } else {
                // usar el sucesor
                int succ = getSuccessor(node, idx);
                node.keys.set(idx, succ); // Reemplazamos la clave
                
                // Borramos el sucesor recursivamente descendiendo por el hijo 
                // derecho (idx + 1)
                deleteRec(node.children.get(idx + 1), succ);
                
                // Chequeo de underflow en el hijo derecho (idx + 1)
                if (node.children.get(idx + 1).keys.size() < minKeys) {
                    fixUnderflow(node, idx + 1);
                }
                return true;
            }
        }

        // Caso 2: La clave no está en este nodo
        if (node.isLeaf) {
            return false;
        }

        boolean deleted = deleteRec(node.children.get(idx), key);

        if (node.children.get(idx).keys.size() < minKeys) {
            fixUnderflow(node, idx);
        }

        return deleted;
    }

    private int getSuccessor(BTreeNode node, int idx) {
        // 1. ir al hijo derecho
        BTreeNode curr = node.children.get(idx + 1);
        // 2. bajar todo a la izquierda hasta llegar a la hoja
        while (!curr.isLeaf) {
            curr = curr.children.get(0);
        }
        // 3. retornar el menor elemento
        return curr.keys.get(0);
    }

    private void fixUnderflow(BTreeNode parent, int childIdx) {
        if (childIdx > 0 && parent.children.get(childIdx - 1).keys.size() > minKeys) {
            borrowFromLeft(parent, childIdx);
        } 
        else if (childIdx < parent.children.size() - 1 && parent.children.get(childIdx + 1).keys.size() > minKeys) {
            borrowFromRight(parent, childIdx);
        } 
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
        parent.keys.set(childIdx - 1, leftSibling.keys.remove(leftSibling.keys.size() - 1));

        if (!child.isLeaf) {
            child.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
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