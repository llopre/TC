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
OPECOMP : ('<' | '>' | '<=' | '>=' | '==' | '!=');
OPEARIT : ('+' | '-' | '*' | '/' | '%');
OPELOGI: ('&&' | '||'); 

WHILE: 'while';

NUMERO : DIGITO+ ;
// OTRO : . ;

//INT : 'int' ;

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
              | estructura instrucciones
              |
              ;

instruccion : asignacion
            | declaracion
            ;

asignacion : ID ASIGN (INT | FLOAT | ID) PYC;
           

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

estructura : iwhile;


iwhile : WHILE PA exprLog PC LLA instrucciones LLC;
