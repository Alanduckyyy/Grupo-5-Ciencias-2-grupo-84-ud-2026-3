package co.edu.udistrital.view;

import co.edu.udistrital.model.BTreeNode;
import co.edu.udistrital.model.BPlusNode;
import java.util.Scanner;

/**
 * mostrador visual y manejador de entradas del usuario
 */
public class ConsoleView {

    private Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * muestra el menu principal y retorna la opcion elegida
     */
    public int showMenuAndGetOption() {
        System.out.println("\nARBOLES B Y B+");
        System.out.println("1. insertar datos de prueba en arbol b");
        System.out.println("2. insertar dato en arbol b");
        System.out.println("3. eliminar dato de arbol b");
        System.out.println("4. mostrar estructura del arbol b");
        System.out.println("5. insertar datos de prueba en arbol b+");
        System.out.println("6. insertar dato en arbol b+");
        System.out.println("7. eliminar dato de arbol b+");
        System.out.println("8. mostrar estructura del arbol b+");
        System.out.println("9. salir");
        System.out.print("elige una opcion: ");
        return scanner.nextInt();
    }

    /**
     * pide un numero al usuario con un mensaje
     */
    public int getIntInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextInt();
    }

    // vista dela arbol b

    public void drawBTree(BTreeNode root) {
        showMessage("\nEstructura del arbol b");
        if (root == null || root.keys.isEmpty()) {
            showMessage("el arbol esta vacio.");
        } else {
            renderB(root, "", true);
        }
    }

    private void renderB(BTreeNode node, String prefix, boolean isLast) {
        System.out.print(prefix + (isLast ? "\\-- " : "+-- ") 
                + node.keys + "\n");
        for (int i = 0; i < node.children.size(); i++) {
            renderB(node.children.get(i), prefix + (isLast ? "    " : "|   "), 
                    i == node.children.size() - 1);
        }
    }

    public void drawBPlusTree(BPlusNode root) {
        showMessage("\nEstructura del arbol b+");
        if (root == null || root.keys.isEmpty()) {
            showMessage("el arbol esta vacio.");
            return;
        }
        renderBPlus(root, "", true);
        
        showMessage("\nHojas enlazadas:");
        BPlusNode curr = root;
        while (curr != null && !curr.isLeaf) {
            curr = curr.children.get(0);
        }
        StringBuilder linearList = new StringBuilder();
        while (curr != null) {
            linearList.append(curr.keys).append(curr.nextLeaf != null
                    ? " -> " : " -> null");
            curr = curr.nextLeaf;
        }
        showMessage(linearList.toString());
    }

    private void renderBPlus(BPlusNode node, String prefix, boolean isLast) {
        String tag = node.isLeaf ? "[hoja] " : "[indice] ";
        System.out.print(prefix + (isLast ? "\\-- " : "+-- ") + tag + 
                node.keys + "\n");
        for (int i = 0; i < node.children.size(); i++) {
            renderBPlus(node.children.get(i), prefix + 
                    (isLast ? "    " : "|   "), i == node.children.size() - 1);
        }
    }
}