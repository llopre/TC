package compiladores;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import compiladores.compiladoresParser.AsignacionContext;
import compiladores.compiladoresParser.DeclaracionContext;
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
        System.out.println("---  Tabla de símbolos ---");
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
    public void exitDeclaracion(DeclaracionContext ctx) {
        ListaDeclaracionContext lista = ctx.listaDeclaracion();
        
        while(lista != null){
            System.out.println("ENTRO AL WHILE");
           if(lista.asignacion() == null){
            System.out.println("ENTRO AL IF");
                Id id = new Variable(lista.ID().getText(), ctx.tipo().getText());
                if(!this.tablaSimbolos.variableDeclarada(id)){
                    this.tablaSimbolos.addId(id);
                    System.out.println("ENTRO EN ADD");
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
        System.out.println("\tNueva Asignacion: |" + ctx.getText() 
                        + "| - Hijos:" + ctx.getChildCount());
        super.enterAsignacion(ctx);
    }

    @Override
    public void exitAsignacion(AsignacionContext ctx) {
        // TODO Auto-generated method stub
        super.exitAsignacion(ctx);
        System.out.println("\t --> Fin Asignacion: |" + ctx.getText() 
                        + "| - Hijos:" + ctx.getChildCount());
    }

    

    @Override
    public void enterInstruccion(InstruccionContext ctx) {
        nodos++;
        super.enterInstruccion(ctx);
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

