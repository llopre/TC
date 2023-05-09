grammar compiladores;

// @header {
// package compiladores;
// }

INT : [0-9]+;
FLOAT : [0-9]*'.'[0-9]*; 

fragment LETRA : [A-Za-z] ;
fragment DIGITO : [0-9] ;

PYC : ';' ;
PA  : '(' ;
PC  : ')' ;
LLA : '{' ;
LLC : '}' ;
ASIGN : '=' ;
COMA : ',' ;
EQ : '==' ;
SUMA : '+';
RESTA : '-';
MULT : '*';
DIV: '/';
MOD : '%';
OPECOMP : ('<' | '>' | '<=' | '>=' | '==' | '!=');
OPELOGI: ('&&' | '||'); 

WHILE: 'while';
IF: 'if';

NUMERO : DIGITO+ ;
// OTRO : . ;

//INT : 'int' ;
//test
TIPO: ('int' | 'double');

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
            ;

asignacion : ID ASIGN expresion PYC;
           

declaracion : TIPO ID inicializacion listaid PYC ;


inicializacion : ASIGN (INT | FLOAT | ID)
               |
               ;

listaid : COMA ID inicializacion listaid
        |
        ;

exprLog : (ID | FLOAT | INT) OPECOMP (ID | FLOAT | INT) listaExprLog;
        

listaExprLog: OPELOGI exprLog
            | 
            ;

estructura : (iwhile | iif);

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

factor : INT
       | FLOAT
       | ID
       | PA expresion PC 
       ;
