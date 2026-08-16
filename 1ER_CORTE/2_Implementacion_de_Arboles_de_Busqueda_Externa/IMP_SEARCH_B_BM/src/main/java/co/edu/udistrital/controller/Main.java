package co.edu.udistrital.controller;

/**
 * lanzador
 */
public class Main {
    public static void main(String[] args) {
        // inicializamos orden 
        TreeController app = new TreeController(3);
        app.start(); // iniciar menu
    }
}