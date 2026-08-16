package com.mycompany.arboles;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Nodo {
    public List<Integer> claves;
    public List<Nodo> hijos;
    public boolean esHoja;

    public Nodo(boolean esHoja) {
        this.esHoja = esHoja;
        this.claves = new ArrayList<>();
        this.hijos = new ArrayList<>();
    }
}
    
    
    
    
    
