grammar compiladores;

 @header {
 package compiladores;
 }

NUMERO : DIGITO+;
// OTRO : . ;

fragment LETRA : [A-Za-z] ;
fragment DIGITO : [0-9] ;

PYC : ';' ;
PA  : '(' ;
PC  : ')' ;
LLA : '{' ;
LLC : '}' ;
ASIGN : '=' ;
COMA : ',' ;
//EQ : '==' ;
SUMA : '+';
RESTA : '-';
MULT : '*';
DIV: '/';
MOD : '%';
OPECOMP : ('<' | '>' | '<=' | '>=' | '==' | '!=');

//Comparación
MENOR: '<';
MAYOR: '>';
MENORIGUAL: '<=';
MAYORIGUAL: '>=';
IGUAL: '==';
DISTINTO: '!=';

//Operadores logicos
AND: '&&';
OR: '||';
OPELOGI: ('&&' | '||'); 

//Ciclos
WHILE: 'while';
FOR: 'for';

//Condicion
IF: 'if';

//Tipos de datos
INT: 'int';
DOUBLE: 'double';

TIPO: INT
    | DOUBLE;

ID : (LETRA | '_')(LETRA | DIGITO | '_')* ;

WS : [ \t\n\r] -> skip ;

// s : ID     { System.out.println("ID ->" + $ID.getText() + "<--"); }         s
//   | NUMERO { System.out.println("NUMERO ->" + $NUMERO.getText() + "<--"); } s
//   | OTRO   { System.out.println("Otro ->" + $OTRO.getText() + "<--"); }     s
//   | EOF
//   ;

// si : s
//    | EOF
//    ;

// s : PA s PC s
//   |
//   ; 

programa : instrucciones EOF ;

instrucciones : instruccion instrucciones
              |
              ;

instruccion : asignacion
            | declaracion
            | estructura
            | funcionDeclara
            ;

asignacion : ID ASIGN expresion PYC;
           

declaracion : TIPO ID inicializacion listaid PYC ;


inicializacion : ASIGN constante 
               |
               ;

listaid : COMA ID inicializacion listaid
        |
        ;

constante : NUMERO
          | ID;

exprLog :  constante OPECOMP constante listaExprLog;
        

listaExprLog: OPELOGI exprLog
            | 
            ;

estructura : iwhile 
           | iif;

bloque : LLA instrucciones LLC;

iwhile : WHILE PA exprLog PC bloque;

iif : IF PA exprLog PC bloque;

expresion : termino exp ;

exp : SUMA  termino exp
    | RESTA termino exp
    |
    ;

termino : factor term ;

term : MULT factor term
     | DIV  factor term
     | MOD  factor term
     |
     ;

factor : NUMERO
       | ID
       | PA expresion PC 
       ;

listaParam : TIPO ID
           | 
           ;

funcionDeclara : TIPO ID PA listaParam PC PYC;