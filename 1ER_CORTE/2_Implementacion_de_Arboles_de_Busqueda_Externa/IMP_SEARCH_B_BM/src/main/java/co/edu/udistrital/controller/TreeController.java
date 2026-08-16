package co.edu.udistrital.controller;

import co.edu.udistrital.model.BTree;
import co.edu.udistrital.model.BPlusTree;
import co.edu.udistrital.view.ConsoleView;

/**
 * controlador principal que maneja la app
 */
public class TreeController {
    private BTree bTree;
    private BPlusTree bPlusTree;
    private ConsoleView view;

    public TreeController(int order) {
        this.bTree = new BTree(order);
        this.bPlusTree = new BPlusTree(order);
        this.view = new ConsoleView();
    }

    /**
     * bucle del menu
     */
    public void start() {
        int[] data = {10, 20, 5, 6, 12, 30, 7, 17};
        boolean exit = false;

        while (!exit) {
            int option = view.showMenuAndGetOption();

            switch (option) {
                case 1:
                    view.showMessage("Datos Iniciales Insertados en Arbol B");
                    for (int key : data) {
                        bTree.insert(key);
                    }
                    view.drawBTree(bTree.root);
                    break;
                
                case 2:
                    int insertB = view.getIntInput("ingrese el valor a "
                            + "insertar en b: ");
                    bTree.insert(insertB);
                    view.showMessage("dato insertado con exito:");
                    view.drawBTree(bTree.root);
                    break;
                case 3:
                    int deleteB = view.getIntInput("ingrese el valor a "
                            + "eliminar en b: ");
                    bTree.delete(deleteB);
                    view.showMessage("proceso de eliminacion finalizado.");
                    view.drawBTree(bTree.root);
                    break;
                case 4:
                    view.drawBTree(bTree.root);
                    break;
                case 5:
                    view.showMessage("Datos Iniciales Insertados en Arbol B+");
                    for (int key : data) {
                        bPlusTree.insert(key);
                    }
                    view.drawBPlusTree(bPlusTree.root);
                case 6:
                    int insertBPlus = view.getIntInput("ingrese el valor a "
                            + "insertar en b+: ");
                    bPlusTree.insert(insertBPlus);
                    view.showMessage("dato insertado con exito.");
                    view.drawBTree(bTree.root);
                    break;
                case 7:
                    int deleteBPlus = view.getIntInput("ingrese el valor a "
                            + "eliminar en b+: ");
                    bPlusTree.delete(deleteBPlus);
                    view.showMessage("proceso de eliminacion finalizado.");
                    view.drawBTree(bTree.root);
                    break;
                case 8:
                    view.drawBPlusTree(bPlusTree.root);
                    break;
                case 9:
                    view.showMessage("saliendo...");
                    exit = true;
                    break;
                default:
                    view.showMessage("opcion invalida. por favor intente de "
                            + "nuevo.");
                    break;
            }
        }
    }
}