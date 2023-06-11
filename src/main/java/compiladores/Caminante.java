package compiladores;

import org.antlr.v4.runtime.tree.TerminalNode;

public class Caminante extends compiladoresBaseVisitor<String>{


    @Override
    public String visitTerminal(TerminalNode node) {
        //System.out.println("Desde el visitor: " + node.getText());
        return super.visitTerminal(node);
    }
    
}
