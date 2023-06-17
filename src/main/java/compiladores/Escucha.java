package compiladores;

import java.util.LinkedList;


import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import compiladores.compiladoresParser.AsignacionContext;
import compiladores.compiladoresParser.BloqueContext;
import compiladores.compiladoresParser.DeclaracionContext;
import compiladores.compiladoresParser.FuncionContext;
import compiladores.compiladoresParser.FuncionDeclaraContext;
import compiladores.compiladoresParser.FuncionDefiniContext;
import compiladores.compiladoresParser.InstruccionContext;
import compiladores.compiladoresParser.ListaDeclaracionContext;
import compiladores.compiladoresParser.ProgramaContext;

public class Escucha extends compiladoresBaseListener{

    private Integer nodos = 0;
    private Integer tokens = 0;
    private Integer errores = 0;

    private TablaSimbolos tablaSimbolos = TablaSimbolos.getInstancia();

    @Override
    public void enterPrograma(ProgramaContext ctx) {
        System.out.println("\n---  Comienza parsing ---");
        super.enterPrograma(ctx);
    }

    @Override
    public void exitPrograma(ProgramaContext ctx) {
        super.exitPrograma(ctx);
        System.out.println("\nFin de compilación --- Nodos: " + nodos + ". Tokens: " 
                           + tokens + ". Errores:" + errores + "\n");
        this.tablaSimbolos.print();
    }

    
    @Override
    public void enterInstruccion(InstruccionContext ctx) {
        nodos++;

        super.enterInstruccion(ctx);
       
    }
    
    @Override
    public void exitInstruccion(InstruccionContext ctx) {

        super.exitInstruccion(ctx);

    }

    @Override
    public void exitDeclaracion(DeclaracionContext ctx) {
        ListaDeclaracionContext lista = ctx.listaDeclaracion();
        

        while(lista != null){
           if(lista.asignacion() == null){
                Id id = new Variable(lista.ID().getText(), ctx.tipo().getText());
                if(!this.tablaSimbolos.variableDeclarada(id)){
                    this.tablaSimbolos.addId(id);
                }
                else{
                    //la variable ya existia
                    System.out.println("Variable duplicada linea: " + ctx.getStop().getLine());
                }
           }
           lista = lista.listaDeclaracion();
        }
        super.exitDeclaracion(ctx);
        
    }
    

    @Override
    public void enterAsignacion(AsignacionContext ctx) {
        //System.out.println("\tNueva Asignacion: |" + ctx.getText() 
        //                + "| - Hijos:" + ctx.getChildCount());
        super.enterAsignacion(ctx);
    }

    @Override
    public void exitAsignacion(AsignacionContext ctx) {
        
        Id variable = this.tablaSimbolos.buscarVariable(ctx.ID().getText());

        if (ctx.getParent().getClass().equals(ListaDeclaracionContext.class)) {
            ListaDeclaracionContext lista = (ListaDeclaracionContext) ctx.getParent();
            
            while(lista.getParent().getClass().equals(ListaDeclaracionContext.class)) {
                lista = (ListaDeclaracionContext) lista.getParent();
            }

            if (lista.getParent().getClass().equals(DeclaracionContext.class)) {
                String nombreVariable = ctx.ID().getText();
                String tipoVariable = ((DeclaracionContext) lista.getParent()).tipo().getText();
                variable = new Variable(nombreVariable, tipoVariable);

                if (!this.tablaSimbolos.variableDeclarada(variable)) {
                    this.tablaSimbolos.addId(variable);
                }
                else {
                    //parser de error -> variable duplicada
                    //this.errorReporter.printError(linea, "duplicated variable "+ variable.getNombre());
                    System.out.println("Variable duplicada linea: " + ctx.getStop().getLine());
                }
            }
        }
        else {
            //parser de error -> variable no declarada
            //this.errorReporter.printError(ctx.getStop().getLine(), "duplicated "+ ctx.ID().getText() +" is not declared");
            System.out.println("Variable no declarada linea: " + ctx.getStop().getLine());
        }
        if (this.tablaSimbolos.variableDeclarada(ctx.ID().getText())) {
            this.tablaSimbolos.setIdUsado(ctx.ID().getText());
        }

        super.exitAsignacion(ctx);
        
    }

    @Override
    public void exitFuncionDeclara(FuncionDeclaraContext ctx) {
        
        Funcion funcion = null;

        funcion = new Funcion(ctx.tipo().getText(), ctx.ID().getText());
    
        LinkedList<Id> paramFuncion = new LinkedList<Id>();

        if(!ctx.listaParam().isEmpty()) {
            this.tablaSimbolos.addContextoParam(); //para verificar variables
            paramFuncion = helper.getParametros(ctx.listaParam(), paramFuncion);
            
            for (Id id : paramFuncion) {
                if (id.getNombre() != "") {
                    if (this.tablaSimbolos.variableDeclarada(id)) {
                        //error: variable ya declarada
                        System.out.println("Variable duplicada linea: " + ctx.getStop().getLine());
                    }
                    this.tablaSimbolos.addParamFuncion(id);
                }
            }
            this.tablaSimbolos.eliminarContexto();
        }
        funcion.setParametros(paramFuncion);
        this.tablaSimbolos.addFuncion(funcion);

        super.exitFuncionDeclara(ctx);
    }

    

    @Override
    public void enterBloque(BloqueContext ctx) {
        
        nodos++;
        //pregunto si estoy definiendo una funcion
        if (ctx.getParent().getClass().equals(FuncionDefiniContext.class)) {
            FuncionDefiniContext fnctx = (FuncionDefiniContext) ctx.getParent();
            Funcion funcion = helper.infoFuncion(fnctx);

            if (!this.tablaSimbolos.variableDeclarada(funcion))
                this.tablaSimbolos.addFuncion(funcion);
                this.tablaSimbolos.addContexto();
                

            //Agrego los parametros al contexto
            if (fnctx.listaParam().getChildCount() != 0) {
                for (Id param : funcion.getParametros()) {
                    this.tablaSimbolos.addId(param);
                }
            }
            
        }
        else {
            this.tablaSimbolos.addContexto();
        }
    }


    @Override
    public void exitBloque(BloqueContext ctx) {
        this.tablaSimbolos.eliminarContexto();
    }

    @Override
    public void exitFuncion(FuncionContext ctx) {
        String nombre = ctx.ID().getText();

        Id funcion = this.tablaSimbolos.buscarVariable(nombre);

        if (funcion == null){
            System.out.println("La función "+ nombre + "no está declarada." + ctx.getStop().getLine());
            return;
        }
        funcion.setUsado(true);
    }

    

    @Override
    public void visitErrorNode(ErrorNode node) {
        errores++;
        super.visitErrorNode(node);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        tokens++; 
        super.visitTerminal(node);
    }
    
    
}

