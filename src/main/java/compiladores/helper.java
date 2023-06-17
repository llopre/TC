package compiladores;

import java.util.*;

import org.antlr.v4.runtime.ParserRuleContext;

import compiladores.compiladoresParser.FuncionDefiniContext;
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


    public static Funcion infoFuncion(FuncionDefiniContext ctx) {
        TablaSimbolos tablaSimbolos = TablaSimbolos.getInstancia();
        String tipo;

        tipo = ctx.tipo().getText();
        
        String nombre = ctx.ID().getText();
        Funcion funcion = new Funcion(tipo, nombre);

        LinkedList<Id> parametros = new LinkedList<>();

        //Con parametros declarados los chequeo
        if (ctx.listaParam().getChildCount() != 0) {
            tablaSimbolos.addContextoParam();
            parametros = getParametros(ctx.listaParam(), parametros);
            for (int i = 0; i < parametros.size(); i++) {
                tablaSimbolos.addParamFuncion(parametros.get(i));
            }
            tablaSimbolos.eliminarContexto();
        }
        funcion.setParametros(parametros);

        //Busco en contexto
        if (tablaSimbolos.getContextos() == 1) {
            Funcion miFuncion = tablaSimbolos.getFuncion(funcion);
            if (miFuncion != null && funcion.getNombre() == miFuncion.getNombre()) {
              //  System.out.println("Error con la declaracion de la función.");
            }
        }
        return funcion;
    }
    
}
