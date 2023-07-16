package compiladores;

import java.util.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Trees;

import compiladores.compiladoresParser.AsignacionContext;
import compiladores.compiladoresParser.DeclaracionContext;
import compiladores.compiladoresParser.ExpresionContext;
import compiladores.compiladoresParser.FuncionContext;
import compiladores.compiladoresParser.FuncionDeclaraContext;
import compiladores.compiladoresParser.FuncionDefiniContext;
import compiladores.compiladoresParser.IforContext;
import compiladores.compiladoresParser.IifContext;
import compiladores.compiladoresParser.IwhileContext;
import compiladores.compiladoresParser.OpLogicaContext;

public class Caminante extends compiladoresBaseVisitor<String>{

    private String codigo;// C. 3 D.
    private int contador, contadorC; //Contador de labels de salto
    private String actual, previo;
    private Boolean bandFunc;
    private HashMap<String, String> opuestos; //Para if y ciclos
    private String[] operadoresLogicos; //Para if y ciclos

    public Caminante(){
        this.codigo = "";
        this.contador = 0; 
        this.contadorC = 0;
        this.actual = "";
        this.previo = "";
        this.bandFunc = false;
        

        this.opuestos = new HashMap<String, String>() {{
            put("!=", "==");
            put("==", "!=");
            put("<", ">=");
            put(">=", "<");
            put("<=", ">");
            put(">", "<=")
            ;
        }};

        this.operadoresLogicos = new String[]{"&&", "||"};
    }

    private List<ParseTree> getnodos(ParseTree ctx, int idx_rule) {
        return new ArrayList<ParseTree>(Trees.findAllRuleNodes(ctx, idx_rule));
    }

    private void procesaOpLogica(OpLogicaContext ctx) {
        String simbolo = "||";
        List<ParseTree> terminos = separaOR(ctx);
        String temp;
        for (int i = 0; i < terminos.size(); i++) {
            temp = this.actual;
            divideAND(terminos.get(i));
            this.previo = temp;
            if (i > 0) {
                concatenaTemps(simbolo);
            }
        }
    }

    private List<ParseTree> separaOR(ParseTree t) {
        List<ParseTree> nodos = new ArrayList<ParseTree>();
        List<ParseTree> aux = this.getnodos(t, compiladoresParser.RULE_disyuncion);
        List<ParseTree> operaciones = this.getnodos(t, compiladoresParser.RULE_factor);
        int count = 0;
        for (int i = 0; i < operaciones.size(); i++){
            if(((compiladoresParser.FactorContext) operaciones.get(i)).PA() != null){
                count++;
            }
        }
        int params = Trees.findAllRuleNodes(t, compiladoresParser.RULE_parametros).size();
        params = aux.size() == params ? 0 : params;
        for (int i = 0; i < aux.size() - params - count; i++) {
            if(aux.get(i).getChild(0) instanceof compiladoresParser.DisyuncionContext){
                nodos.add(((compiladoresParser.DisyuncionContext) aux.get(i)).conjuncion());
            }
            else {
                nodos.add(aux.get(i));
            }
        }
        Collections.reverse(nodos);
        return nodos;        
    }

    private void divideAND(ParseTree ctx) {
        String operador = "&&";
        List<ParseTree> terminos = separaAND(ctx);
        String temp;
        for (int i = 0; i < terminos.size(); i++) {
            temp = this.actual;
            divideComparaciones(terminos.get(i));
            this.previo = temp;
            if (i > 0) {
                concatenaTemps(operador);
            }
        }
    }

    private void divideComparaciones(ParseTree ctx) {
        List<ParseTree> terminos = separaComparaciones(ctx);
        String temp;
        for (int i = 0; i < terminos.size(); i++) {
            temp = actual;
            processTerms(terminos.get(i));
            previo = temp;
            if (i > 0) {
                String operador = terminos.get(i).getParent().getChild(1).getText();
                concatenaTemps(operador);
            }
        }
    }

    private List<ParseTree> separaAND(ParseTree t){
        List<ParseTree> nodos = new ArrayList<ParseTree>();
        List<ParseTree> aux = getnodos(t, compiladoresParser.RULE_conjuncion);
        List<ParseTree> factores = getnodos(t, compiladoresParser.RULE_factor); // factors enclosed in parentheses, such as ((1+2)+3)
        int count = 0;
        for (int i = 0; i < factores.size(); i++) {
            if (((compiladoresParser.FactorContext) factores.get(i)).PA() != null){
                count++;
            }
        }
        int params = Trees.findAllRuleNodes(t, compiladoresParser.RULE_parametros).size();
        params = aux.size() == params ? 0 : params;
        for (int i = 0; i < aux.size() - params - count; i++) {
            if (aux.get(i).getChild(i) instanceof compiladoresParser.ConjuncionContext){
                nodos.add(((compiladoresParser.ConjuncionContext) aux.get(i)).proposicion());
            } else{
                nodos.add(aux.get(i));
            }
        }
        Collections.reverse(nodos);
        return nodos;
    }

    private void concatenaTemps(String operation) {
        this.codigo += String.format("t%d = %s %s %s \n", this.contador, this.previo, operation, this.actual);
        this.actual = "t" + this.contador;
        this.contador++;
    }

    private List<ParseTree> separaComparaciones(ParseTree t){
        List<ParseTree> nodos = new ArrayList<ParseTree>();
        List<ParseTree> aux = getnodos(t, compiladoresParser.RULE_proposicion);
        List<ParseTree> factors = getnodos(t, compiladoresParser.RULE_factor); // factors enclosed in parentheses, such as ((1+2)+3)
        int count = 0;
        for (int i = 0; i < factors.size(); i++) {
            if (((compiladoresParser.FactorContext) factors.get(i)).PA() != null){
                count++;
                count += getnodos(factors.get(i), compiladoresParser.RULE_comparacion).size();
            }
        }
        int params = Trees.findAllRuleNodes(t, compiladoresParser.RULE_parametros).size();
        params = aux.size() == params ? 0 : params;
        for (int i = 0; i < aux.size() - params - count; i++) {
            if (aux.get(i).getChild(0) instanceof compiladoresParser.ProposicionContext){
                nodos.add(((compiladoresParser.ProposicionContext) aux.get(i)).expresion());
            } else{
                nodos.add(aux.get(i));
            }
        }
        Collections.reverse(nodos);
        return nodos;
    }

    private void processTerms(ParseTree ctx) {
        List<ParseTree> ruleTerms = new ArrayList<ParseTree>();
        getNodosPorRegla(ctx, compiladoresParser.RULE_termino, ruleTerms);
        List<ParseTree> terms = new ArrayList<ParseTree>(ruleTerms);
        String temp;
        for (int i = 0; i < terms.size(); i++) {
            List<ParseTree> factors = new ArrayList<ParseTree>();
            getNodosPorRegla(terms.get(i), compiladoresParser.RULE_factor, factors);
            if (factors.size() > 1) {
                temp = this.actual;
                procesaFactores(factors); 
                this.previo = temp;
                this.actual = "t" + (this.contador - 1);
            } else {
                this.previo = this.actual;
                if (((compiladoresParser.TerminoContext) terms.get(i)).factor().opLogica() != null) {
                    temp = this.actual;
                    procesaOpLogica(((compiladoresParser.TerminoContext) terms.get(i)).factor().opLogica());
                    this.previo = temp;
                } else if (((compiladoresParser.TerminoContext) terms.get(i)).factor().funcion() != null){
                    temp = this.actual;
                    this.bandFunc = true;
                    visitFuncion(((compiladoresParser.TerminoContext) terms.get(i)).factor().funcion());
                    this.bandFunc = false;
                    this.previo = temp;
                    this.actual = "x" + (this.contador - 1);
                } else {
                    this.actual = factors.get(0).getText();
                }
            }
            if(i > 0){ 
                concatenaTemps(terms.get(i).getParent().getChild(0).getText());
            }
        }
    }

    public void getNodosPorRegla(ParseTree t, int index, List<ParseTree> nodos) {
        if (t instanceof ParserRuleContext) {
            ParserRuleContext ctx = (ParserRuleContext) t;
            if (ctx.getRuleIndex() == index) {
                nodos.add(t);
            }
        }
        // hijos
        for (int i = 0; i < t.getChildCount(); i++) {
            if (!(t.getChild(i) instanceof compiladoresParser.OpLogicaContext)) {
                getNodosPorRegla(t.getChild(i), index, nodos);
            }
        }
    }


    private void procesaFactores(List<ParseTree> factors) {
        String temp;
        for(int i=0; i < factors.size(); i++) {
            if(((compiladoresParser.FactorContext)factors.get(i)).opLogica() != null) {
                temp = this.actual;
                procesaOperacion(((compiladoresParser.FactorContext) factors.get(i)).opLogica());
                this.previo = temp;
            }  else if (((compiladoresParser.FactorContext) factors.get(i)).funcion() != null){
                temp = this.actual;
                this.bandFunc = true;
                visitFuncion(((compiladoresParser.FactorContext) factors.get(i)).funcion());
                this.bandFunc = false;
                this.previo = temp;
                this.actual = "t" + (this.contador - 1);
            } else {
                this.previo = this.actual;
                this.actual = factors.get(i).getText();
            }
            if (i > 0){
                concatenaTemps(factors.get(i).getParent().getChild(0).getText());
            }
        }
    }

    private void procesaOperacion(compiladoresParser.OpLogicaContext ctx) {
        String operador = "||";
        List<ParseTree> terminos = separaOR(ctx);
        String temp;
        for (int i = 0; i < terminos.size(); i++) {
            temp = this.actual;
            divideAND(terminos.get(i));
            this.previo = temp;
            if (i > 0) {
                concatenaTemps(operador);
            }
        }
    }

    public String getUltimaLinea(String string) {
        List<String> lineas = Arrays.asList(string.split("\n"));
        return new ArrayList<>(lineas.subList(Math.max(0, lineas.size() - 1), lineas.size())).get(0);
    }

    private String getOperacionInversa(String operation) {
        StringBuilder newOperation = new StringBuilder();
            for (int i = 0; i < operation.length(); i++) {     
                if (i+2 <= operation.length()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(operation.charAt(i));
                    sb.append(operation.charAt(i+1));
                    String op = sb.toString();
                    if (this.opuestos.containsKey(op)) {
                        newOperation.append(this.opuestos.get(op));
                        i++;
                    } else if (this.opuestos.containsKey(String.valueOf(operation.charAt(i)))) {
                        newOperation.append(this.opuestos.get(String.valueOf(operation.charAt(i))));
                    } else if (Arrays.asList(this.operadoresLogicos).contains(op)) {
                        newOperation.append(op);
                        i++;
                    } else {
                        newOperation.append(operation.charAt(i));
                    }
                } else{ //Last character
                    newOperation.append(operation.charAt(i));
                }       
            }
        return newOperation.toString();
    }



    // Redefiniciones de métodos //////////////////////


    
    
    @Override
    public String visitAsignacion(AsignacionContext ctx) {
        List<ParseTree> fact = this.getnodos(ctx, compiladoresParser.RULE_factor);
        if(fact.size() == 1){
            this.codigo += ctx.ID().getText() + " = " + fact.get(0).getText() + "\n";
        }
        else {
            OpLogicaContext ctx_expresion = ctx.opLogica();
            procesaOpLogica(ctx_expresion);
            this.codigo = this.codigo.replace("x" + (this.contador - 1), ctx.ID().getText());
            this.contador--;
        }
        return null;
    }

    @Override
    public String visitDeclaracion(DeclaracionContext ctx) {
        compiladoresParser.ListaDeclaracionContext lista = ctx.listaDeclaracion();
        while (lista != null){
            if (lista.asignacion() != null) {
                List<ParseTree> factor = getnodos(lista, compiladoresParser.RULE_factor);
                if (factor.size() == 1){
                    this.codigo +=  lista.asignacion().ID().getText() + " = " + factor.get(0).getText() + "\n";
                } else{
                    compiladoresParser.OpLogicaContext operaciones_ctx = lista.asignacion().opLogica();
                    procesaOperacion(operaciones_ctx);
                    this.codigo = this.codigo.replace("x" + (this.contador - 1), lista.asignacion().ID().getText());
                    this.contador--;
                }
            }
            lista = lista.listaDeclaracion();
        }
        return null;
    }

    @Override
    public String visitFuncionDefini(FuncionDefiniContext ctx) {

        this.codigo += ctx.ID().getText() + ":\n";
        this.codigo += "ComienzaFuncion\n";
        String parametro;
        List<ParseTree> params = getnodos(ctx, compiladoresParser.RULE_listaParam);
        for (int i = 0; i < params.size(); i++) {
            compiladoresParser.ListaParamContext actual = (compiladoresParser.ListaParamContext)params.get(i);
            if (actual.ID() != null) {
                parametro = actual.ID().getText();
                this.codigo += "PopParametro " + parametro + "\n"; //se saca el param de la pila
            }
        }
        visitChildren(ctx.bloque());
        this.codigo += "FinFuncion\n";
        return null;
    }

    @Override
    public String visitFuncionDeclara(FuncionDeclaraContext ctx) {
        
        return null;
    }

    @Override
    public String visitFuncion(FuncionContext ctx) {
        LinkedList<String> params = new LinkedList<String>();
        if (ctx.parametros() != null) {
            LinkedList<ParseTree> operaciones = new LinkedList<ParseTree>();
            getNodosPorRegla(ctx.parametros(), compiladoresParser.RULE_parametros, operaciones);
            for (ParseTree operacion : operaciones) {
                compiladoresParser.ParametrosContext param = (compiladoresParser.ParametrosContext) operacion;
                procesaOperacion(((compiladoresParser.OpLogicaContext) param.opLogica()));
                params.add("pushParametro " + this.actual);
            }
        }
        for (String param : params) {
            this.codigo += param + "\n";
        }
        if (this.bandFunc) {
            this.codigo += "x" + this.contador + " = CALL " + ctx.ID() + "\n";
            this.contador++;
        } else{
            this.codigo += "CALL " + ctx.ID() + "\n";
        }
        return null;
    }

    
    @Override
    public String visitIif(IifContext ctx) {
        this.contadorC++;
        procesaOperacion(ctx.opLogica());
        
        String auxString = getUltimaLinea(this.codigo);
        String operacion = auxString.substring(auxString.indexOf("=")+2);
        this.codigo = this.codigo.replace(auxString, String.format("if %s goto L%s", getOperacionInversa(operacion), this.contadorC));
        this.contador--;
        int goTo = this.contadorC;
        int aux1 = this.contadorC;
        visitChildren(ctx.bloque());

        compiladoresParser.IelseContext elseCtx = ctx.ielse();
        while(elseCtx != null) {
        if (!elseCtx.getText().equals("")) {
            goTo++;
        }
        elseCtx = elseCtx.ielse();
        }

        elseCtx = ctx.ielse();
        while(elseCtx != null) {
            if (!elseCtx.getText().equals("")) {
                this.codigo += "goto L" + goTo + "\n";
            }
            if (elseCtx.ELSE() != null) {
                this.codigo += "L" + aux1 + "\n";
                visitChildren(elseCtx.bloque());
            }
            aux1++;
            elseCtx = elseCtx.ielse();
        }
        
        this.codigo += "L" + goTo + "\n";
        this.contadorC = goTo;
        
        return null;
    }
    

    @Override
    public String visitIwhile(IwhileContext ctx) {
        this.contadorC++;
        int goTo = this.contadorC;
        String operacion = ctx.opLogica().getChild(0).getText();
        codigo += String.format("\nL%s:", this.contadorC);
        this.codigo += String.format("if %s goto L%s\n", getOperacionInversa(operacion), ++this.contadorC);
        visitChildren(ctx.bloque());
        codigo += String.format("goto L%s", goTo);
        codigo += String.format("\nL%s:", this.contadorC);
        return null;
    }

    @Override
    public String visitIfor(IforContext ctx) {
        this.contadorC++;
        int first_label = this.contadorC;
        if (ctx.getChild(2) instanceof compiladoresParser.DeclaracionContext) {
            visitDeclaracion((compiladoresParser.DeclaracionContext) ctx.getChild(2));
        } else if (ctx.getChild(2) instanceof compiladoresParser.AsignacionContext) {
            visitAsignacion((compiladoresParser.AsignacionContext) ctx.getChild(2));
        }
        String operacion = ctx.getChild(4).getText();
        codigo += String.format("L%s:", this.contadorC);
        this.codigo += String.format("if %s goto L%s\n", getOperacionInversa(operacion), ++this.contadorC);
        visitChildren(ctx.bloque());
        codigo +=  ctx.getChild(6).getText() + "\n";
        codigo += String.format("goto L%s", first_label);
        codigo += String.format("\nL%s:", this.contadorC);

        return null;
    }

    //Impresion del codigo para app
    public void getCodigo() {
        System.out.println("\n----- Código de tres direcciones ----- ");
        System.out.println(this.codigo);
    }


   
    
}
