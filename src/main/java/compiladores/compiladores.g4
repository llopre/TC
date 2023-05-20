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
INC: '++';
DEC: '--';
NOT: '!';

//Comparación
MENOR: '<';
MAYOR: '>';
MENORIGUAL: '<=';
MAYORIGUAL: '>=';
IGUAL: '==';
DISTINTO: '!=';

comparacion : MENOR
            | MAYOR
            | MENORIGUAL
            | MAYORIGUAL
            | IGUAL
            | DISTINTO
            ;

//Operadores logicos
AND: '&&';
OR: '||';

//Ciclos
WHILE: 'while';
FOR: 'for';

//Condicion
IF: 'if';
ELSE: 'else';

//Tipos de datos
INT: 'int';
DOUBLE: 'double';
FLOAT: 'float';
TIPOFUNC : 'void'; 

tipo : INT
     | DOUBLE
     | FLOAT;

RETORNO: 'return';



ID : (LETRA | '_')(LETRA | DIGITO | '_')* ;

WS : [ \t\n\r] -> skip ;


//////------ Reglas ---


programa : instrucciones EOF ;

instrucciones : instruccion instrucciones
              |
              ;

instruccion : asignacion PYC //lo pongo aca porque sino quedaba deformado el FOR
            | declaracion PYC
            | expresion PYC //para los casos q++ sueltos o en FOR
            | estructura
            | funcionDeclara
            | funcionDefini
            | bloque
            | retorno PYC
            ;

asignacion : ID ASIGN expresion;
           

declaracion : tipo ID inicializacion listaid; 

inicializacion : ASIGN constante 
               |
               ;

listaid : COMA ID inicializacion listaid
        |
        ;

constante : NUMERO
          | ID;

retorno :  RETORNO opLogica;


// Estructuras y ciclos -------
estructura : iwhile 
           | iif
           | ifor
           ;

ifor: FOR PA (asignacion | declaracion) PYC opLogica PYC opLogica PC bloque;

 
iwhile : WHILE PA opLogica PC bloque;

iif : IF PA opLogica PC bloque ielse;

ielse: ELSE bloque
     |
     ;

bloque : LLA instrucciones LLC;


// terminos con op aritmeticas ------
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
       | prefijo ID sufijo
       | PA expresion PC 
       ;

prefijo : INC
        | DEC
        | NOT
        |
        ;

sufijo : INC
       | DEC
       |
       ;


//terminos con op logicas -------
// integro las aritmeticologicas -----

opLogica: disyuncion; //Porque separa terminos

//Separa el OR
//Siempre va primero a buscar una proposicion y/o el and, 
//cuando vuelve pasa por el posible or.
disyuncion: conjuncion 
          | disyuncion OR conjuncion
          ;

conjuncion: proposicion
          | conjuncion AND proposicion;

proposicion : expresion
            | proposicion comparacion expresion;


listaParam : tipo ID
           | tipo ID COMA listaParam
           | 
           ;

//exprLog :  constante OPECOMP constante listaExprLog;

//listaExprLog: (AND | OR) exprLog
//            | 
//            ;


// Funciones ------
funcionDeclara : (tipo | TIPOFUNC) ID PA listaParam PC PYC;

funcionDefini : (tipo | TIPOFUNC) ID PA listaParam PC bloque;


/////
// s : ID     { System.out.println("ID ->" + $ID.getText() + "<--"); }         s
//   | NUMERO { System.out.println("NUMERO ->" + $NUMERO.getText() + "<--"); } s
//   | OTRO   { System.out.println("Otro ->" + $OTRO.getText() + "<--"); }     s
//   | EOF
//   ;
