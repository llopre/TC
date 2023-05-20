package compiladores;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import compiladores.compiladoresParser.AsignacionContext;
import compiladores.compiladoresParser.ProgramaContext;

public class Escucha extends compiladoresBaseListener{

    private Integer nodos = 0;
    private Integer tokens = 0;
    private Integer errores = 0;

    @Override
    public void enterPrograma(ProgramaContext ctx) {
        System.out.println("Comienza parsing");
        super.enterPrograma(ctx);
    }
    
    @Override
    public void exitPrograma(ProgramaContext ctx) {
        super.exitPrograma(ctx);
        System.out.println("Fin compilacion. Nodos: " + nodos + ". Tokens: " 
                           + tokens + ". Errores:" + errores);
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
    public void enterEveryRule(ParserRuleContext ctx) {
        nodos++;
        super.enterEveryRule(ctx);
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

