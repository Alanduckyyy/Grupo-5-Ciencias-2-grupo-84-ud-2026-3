package com.mycompany.arboles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArbolBM {

    public class NodoBMas {
        List<Integer> claves;
        List<NodoBMas> hijos;
        boolean esHoja;
        NodoBMas siguienteHoja; 

        public NodoBMas(boolean esHoja) {
            this.esHoja = esHoja;
            this.claves = new ArrayList<>();
            this.hijos = new ArrayList<>();
            this.siguienteHoja = null;
        }
    }

    // clase auxiliar para atrapar cuando un hijo se rompe y manda una clave para arriba
    private class ResultadoSplit {
        int clavePromovida;
        NodoBMas nuevoNodo;
        public ResultadoSplit(int c, NodoBMas n) { 
            clavePromovida = c; 
            nuevoNodo = n; 
        }
    }

    public NodoBMas raiz;
    private int orden;

    public ArbolBM(int orden) {
        this.orden = orden;
        this.raiz = new NodoBMas(true);
    }

    public void insertar(int dato) {
        // empezamos la recursion desde la raiz
        ResultadoSplit res = insertarRec(raiz, dato);
        
        // si la raiz se dividio toca crear una raiz nueva arriba de todo
        if (res != null) {
            NodoBMas nuevaRaiz = new NodoBMas(false);
            nuevaRaiz.claves.add(res.clavePromovida);
            nuevaRaiz.hijos.add(raiz);
            nuevaRaiz.hijos.add(res.nuevoNodo);
            raiz = nuevaRaiz;
        }
    }

    private ResultadoSplit insertarRec(NodoBMas nodo, int dato) {
        if (nodo.esHoja) {
            // si es hoja lo metemos normal y ordenamos
            nodo.claves.add(dato);
            Collections.sort(nodo.claves);

            // si la hoja se paso del orden la partimos
            if (nodo.claves.size() == orden) {
                return dividirHoja(nodo);
            }
            return null;
        } else {
            // si es nodo interno buscamos por cual hijo tiene que bajar
            int i = 0;
            while (i < nodo.claves.size() && dato >= nodo.claves.get(i)) {
                i++;
            }
            
            // llamamos recursivo para que siga bajando
            ResultadoSplit res = insertarRec(nodo.hijos.get(i), dato);
            
            // si el hijo se rompio y nos escupio una clave, la acomodamos aca
            if (res != null) {
                nodo.claves.add(i, res.clavePromovida);
                nodo.hijos.add(i + 1, res.nuevoNodo);
                
                // si al meter la clave nueva este nodo interno tambien revienta, lo partimos
                if (nodo.claves.size() == orden) {
                    return dividirInterno(nodo);
                }
            }
            return null;
        }
    }

    private ResultadoSplit dividirHoja(NodoBMas hoja) {
        NodoBMas nuevaHoja = new NodoBMas(true);
        // ante impar queda mas cargado el derecho
        int mitad = hoja.claves.size() / 2; 
        
        for (int i = mitad; i < hoja.claves.size(); i++) {
            nuevaHoja.claves.add(hoja.claves.get(i));
        }
        hoja.claves.subList(mitad, hoja.claves.size()).clear();
        
        // no romper la cadenita de las hojas
        nuevaHoja.siguienteHoja = hoja.siguienteHoja;
        hoja.siguienteHoja = nuevaHoja;
        
        // en las hojas la clave sube copiandose (el primero del nodo nuevo)
        int promovida = nuevaHoja.claves.get(0);
        return new ResultadoSplit(promovida, nuevaHoja);
    }

    private ResultadoSplit dividirInterno(NodoBMas interno) {
        NodoBMas nuevoInterno = new NodoBMas(false);
        int mitad = interno.claves.size() / 2;
        int promovida = interno.claves.get(mitad);
        
        // en nodos internos la clave de la mitad SUBE Y DESAPARECE del nivel
        for (int i = mitad + 1; i < interno.claves.size(); i++) {
            nuevoInterno.claves.add(interno.claves.get(i));
        }
        for (int i = mitad + 1; i < interno.hijos.size(); i++) {
            nuevoInterno.hijos.add(interno.hijos.get(i));
        }
        
        // borramos desde la mitad pq la de la mitad ya se fue p'arriba
        interno.claves.subList(mitad, interno.claves.size()).clear();
        interno.hijos.subList(mitad + 1, interno.hijos.size()).clear();
        
        return new ResultadoSplit(promovida, nuevoInterno);
    }

    public void imprimirArbolCompleto() {
        System.out.println("Arbol B+");

        int h = obtenerAltura(raiz);
        for (int i = 1; i <= h; i++) {
            boolean esNivelHoja = (i == h);
            System.out.print("Nivel " + (i - 1) + (esNivelHoja ? " [Hojas]: " : " [Indices]: "));
            imprimirNivelDado(raiz, i);
            System.out.println();
        }

        NodoBMas actual = raiz;
        while (actual != null && !actual.esHoja) {
            actual = actual.hijos.get(0);
        }

        StringBuilder listaStr = new StringBuilder();
        while (actual != null) {
            listaStr.append(actual.claves);
            if (actual.siguienteHoja != null) {
                listaStr.append("  ==[siguiente]==>  ");
            } else {
                listaStr.append("  ==[null]");
            }
            actual = actual.siguienteHoja;
        }

        System.out.println(listaStr.toString());
        System.out.println("==================================================\n");
    }

    private int obtenerAltura(NodoBMas nodo) {
        if (nodo == null) return 0;
        if (nodo.esHoja) return 1;
        return 1 + obtenerAltura(nodo.hijos.get(0));
    }

    private void imprimirNivelDado(NodoBMas nodo, int nivel) {
        if (nodo == null) return;

        if (nivel == 1) {
            System.out.print(nodo.claves + "  ");
        } else if (nivel > 1 && !nodo.esHoja) {
            for (NodoBMas hijo : nodo.hijos) {
                imprimirNivelDado(hijo, nivel - 1);
            }
        }
    }
}