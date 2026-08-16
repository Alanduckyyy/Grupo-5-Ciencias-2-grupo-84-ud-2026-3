package com.mycompany.arboles;

public class Main {
    public static void main(String[] args) {
        System.out.println("Prueba Arbol B");
        ArbolB arbolB = new ArbolB(3); // Orden 3
        int[] datos = {10, 20, 5, 6, 12, 30, 7, 17};
        for (int dato : datos) arbolB.insertar(dato);
        arbolB.imprimirPorNiveles();   
        
        System.out.println("\nPrueba Arbol B+");
        ArbolBM arbolBMas = new ArbolBM(3);
        for (int dato : datos) arbolBMas.insertar(dato);
        arbolBMas.imprimirArbolCompleto();
    }
}