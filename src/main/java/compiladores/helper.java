package compiladores;

import java.util.*;

import org.antlr.v4.runtime.ParserRuleContext;

import compiladores.compiladoresParser.ListaParamContext;

public class helper {


    public static LinkedList<Id> getParametros(ParserRuleContext ruleCtx, LinkedList<Id> parametros) {
        
            ListaParamContext paramDefCtx = (ListaParamContext) ruleCtx;
            // si la lista no tiene nodos hijos, no tiene parametros
            if (paramDefCtx.children == null) {
                return parametros;
            }
            
            if (paramDefCtx.listaParam() != null) {
                Id param = new Variable(paramDefCtx.ID().getText(), paramDefCtx.tipo().getText());
                parametros.add(param);
                return getParametros(paramDefCtx.listaParam(), parametros);
            }
            
            //Para el ultimo parametro de la lista
            else {
                Id param = new Variable(paramDefCtx.ID().getText(), paramDefCtx.tipo().getText());
                parametros.add(param);
                return parametros;
            }
    }
    
}
