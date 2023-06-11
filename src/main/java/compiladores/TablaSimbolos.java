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

    public boolean variableDeclarada(Id id) {
        for(int i = this.tablaSimbolos.size() - 1; i >= 0; i--) {
            if (this.tablaSimbolos.get(i).containsKey(id.getNombre())) {
                return true;
            }
        }
        return false;
    }

    public void print(){

        int ctx = 1;
        
        for (HashMap<String, Id> contexto : this.tablaSimbolos) {
            System.out.println("-> Contexto " + ctx++);
            for (Id id : contexto.values()) {
                System.out.println("    " + id.toString());
            }
            System.out.println("----\n");
        }
    }
}
