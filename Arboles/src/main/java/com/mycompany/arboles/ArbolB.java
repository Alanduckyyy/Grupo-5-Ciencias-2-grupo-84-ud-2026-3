package com.mycompany.arboles;

import java.util.ArrayList;
import java.util.List;

public class ArbolB {

    public Nodo raiz;
    private int orden; 

    public ArbolB(int orden) {
        this.orden = orden;
        this.raiz = new Nodo(true);
    }

    public boolean buscar(int clave) {
        return buscarEnNodo(raiz, clave);
    }

    private boolean buscarEnNodo(Nodo nodo, int clave) {
        int i = 0;
        while (i < nodo.claves.size() && clave > nodo.claves.get(i)) {
            i++;
        }
        if (i < nodo.claves.size() && clave == nodo.claves.get(i)) {
            return true; // encontrado
        }
        if (nodo.esHoja) {
            return false; // no esta
        }
        return buscarEnNodo(nodo.hijos.get(i), clave); // buscar en hoja
    }

    public void insertar(int dato) {
        Nodo r = raiz;
        if (r.claves.size() == orden - 1) {
            Nodo nuevaRaiz = new Nodo(false);
            nuevaRaiz.hijos.add(r);
            dividirHijo(nuevaRaiz, 0, r);
            this.raiz = nuevaRaiz;
            insertarNoLleno(nuevaRaiz, dato);
        } else {
            insertarNoLleno(r, dato);
        }
    }

    private void insertarNoLleno(Nodo nodo, int dato) {
        int i = nodo.claves.size() - 1;

        if (nodo.esHoja) {
            // encontrar posicion
            nodo.claves.add(0); 
            while (i >= 0 && dato < nodo.claves.get(i)) {
                nodo.claves.set(i + 1, nodo.claves.get(i));
                i--;
            }
            nodo.claves.set(i + 1, dato);
        } else {
            // buscar el hijo donde debe ir
            while (i >= 0 && dato < nodo.claves.get(i)) {
                i--;
            }
            i++;
            // si el hijo está lleno, dividir
            if (nodo.hijos.get(i).claves.size() == orden - 1) {
                dividirHijo(nodo, i, nodo.hijos.get(i));
                if (dato > nodo.claves.get(i)) {
                    i++;
                }
            }
            insertarNoLleno(nodo.hijos.get(i), dato);
        }
    }

    private void dividirHijo(Nodo padre, int i, Nodo hijoLleno) {
        int t = (orden + 1) / 2;
        int medianaIndex = t - 1;

        Nodo nuevoNodo = new Nodo(hijoLleno.esHoja);

        // mitad al nuevo nodo
        for (int j = medianaIndex + 1; j < hijoLleno.claves.size(); j++) {
            nuevoNodo.claves.add(hijoLleno.claves.get(j));
        }
        // izq
        if (!hijoLleno.esHoja) {
            for (int j = medianaIndex + 1; j < hijoLleno.hijos.size(); j++) {
                nuevoNodo.hijos.add(hijoLleno.hijos.get(j));
            }
            hijoLleno.hijos.subList(medianaIndex + 1, hijoLleno.hijos.size()).clear();
        }

        int claveMediana = hijoLleno.claves.get(medianaIndex);
        hijoLleno.claves.subList(medianaIndex, hijoLleno.claves.size()).clear();

        padre.hijos.add(i + 1, nuevoNodo);
        padre.claves.add(i, claveMediana);
    }

    public void imprimirPorNiveles() {
        int h = obtenerAltura(raiz);
        System.out.println("Arbol B");
        for (int i = 1; i <= h; i++) {
            System.out.print("Nivel " + (i - 1) + ": ");
            imprimirNivelDado(raiz, i);
            System.out.println();
        }
    }

    private int obtenerAltura(Nodo nodo) {
        if (nodo == null) return 0;
        if (nodo.esHoja) return 1;
        return 1 + obtenerAltura(nodo.hijos.get(0));
    }

    private void imprimirNivelDado(Nodo nodo, int nivel) {
        if (nodo == null) return;

        if (nivel == 1) {
            System.out.print(nodo.claves + " ");
        } else if (nivel > 1 && !nodo.esHoja) {
            for (Nodo hijo : nodo.hijos) {
                imprimirNivelDado(hijo, nivel - 1);
            }
        }
    }
}
