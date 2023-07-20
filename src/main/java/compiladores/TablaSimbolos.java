package compiladores;

import java.util.HashMap;
import java.util.LinkedList;

public class TablaSimbolos {

    private static TablaSimbolos instancia; //Singleton
    
    private LinkedList<HashMap<String, Id>> tablaSimbolos;
    private LinkedList<HashMap<String, Id>> tablaSimbolosFinal;

    //Constructor para iniciar el objeto y añadir el primer contexto
    public TablaSimbolos() {
        this.tablaSimbolos = new LinkedList<HashMap<String, Id>>();
        this.tablaSimbolosFinal = new LinkedList<HashMap<String, Id>>();
        this.addContexto();

    }

    public void addContexto() {
        HashMap<String, Id> contexto = new HashMap<String, Id>();        
        this.tablaSimbolos.add(contexto);

        this.tablaSimbolosFinal.add(contexto);
    }
    
    public static TablaSimbolos getInstancia() {
        if(instancia == null)
            instancia = new TablaSimbolos();

        return instancia;
    }

    //Utilidades

    public void nuevoContexto() {
        this.tablaSimbolos.add(new HashMap<String, Id>());
    }

    public void eliminarContexto() {
        this.tablaSimbolos.removeLast();
    }

    public int getContextos() {
        return this.tablaSimbolos.size();
    }

    //Agregar id 
    public void addId(final Id id) {
        this.tablaSimbolos.getLast().put(id.getNombre(), id);
    }

    //Busqueda y devolucion del elemento
    public Id buscarId(Id id) {
        for(int i = 0; i < this.tablaSimbolos.size(); i++) {
            if(this.tablaSimbolos.get(i).containsKey(id.getNombre()))
                return this.tablaSimbolos.get(i).get(id.getNombre());
        }

        return null;
    }

    public Id buscarVariable(final String nombre) {
        for(int i = 0; i < this.tablaSimbolos.size(); i++) {
            if(this.tablaSimbolos.get(i).containsKey(nombre))
                return this.tablaSimbolos.get(i).get(nombre);
        }

        return null;
    }

    public boolean variableDeclarada(Id id) {
        for(int i = this.tablaSimbolos.size() - 1; i >= 0; i--) {
            if (this.tablaSimbolos.get(i).containsKey(id.getNombre())) {
                return true;
            }
        }
        return false;
    }

    public boolean variableDeclarada(final String nombre) {
        for(int i = this.tablaSimbolos.size() - 1; i >= 0; i--) {
            if (this.tablaSimbolos.get(i).containsKey(nombre)) {
                return true;
            }
        }
        return false;
    }


    public void setIdUsado(final String nombre) {
        for (HashMap<String, Id> contexto : this.tablaSimbolos) {
            for (Id id : contexto.values()) {
                if (id.getNombre().equals(nombre))
                    id.setUsado(true);
            }
        }
    }

    public void addContextoParam() {
        this.tablaSimbolos.add(new HashMap<String, Id>());
    }


    public void addParamFuncion(final Id id) {
        // luego de agregar el contexto
        this.tablaSimbolos.getLast().put(id.getNombre(), id);
    }

    public void addFuncion(final Funcion funcion) {
        this.tablaSimbolos.getLast().put(funcion.getNombre(), funcion);
        if (this.tablaSimbolos.size() == 0){
            if (this.tablaSimbolosFinal.size() > 1){
                this.tablaSimbolosFinal.get(this.tablaSimbolosFinal.size() - 2).put(funcion.getNombre(), funcion);
            }
            else {
                this.tablaSimbolosFinal.get(this.tablaSimbolosFinal.size() - 1).put(funcion.getNombre(), funcion);
            }
        }
    }

    public Funcion getFuncion(final Funcion function) {
        Id id = this.tablaSimbolos.getFirst().get(function.getNombre());
        
        if (id instanceof Funcion)
            return (Funcion) id;
        else
            return null; 
    }

    public void print(){

        int ctx = 1;
        
        /* 
        for (HashMap<String, Id> contexto : this.tablaSimbolos) {
            System.out.println("-> Contexto " + ctx++);
            for (Id id : contexto.values()) {
                System.out.println("    " + id.toString());
            }
            System.out.println("----\n");
        }
        */

        ctx = 1;
        System.out.println("\n-- Tabla de simbolos final: \n");
        for (HashMap<String, Id> contexto : this.tablaSimbolosFinal) {
            System.out.println("-> Contexto " + ctx++);
            for (Id id : contexto.values()) {
                System.out.println("    " + id.toString());
            }
            System.out.println("----\n");
        }
    }
}
