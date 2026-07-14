grammar from4;

proga: route* EOF;

route: from clause* ';';
from: 'from' '(' uri ')';               // from("direct:start")

// ==================== КЛАУЗЫ (CLAUSES) ====================
clause
    : toClause
    | processClause
    | choiceClause
    | splitClause
    | doTryClause
    | filterClause
    | setBodyClause
    | setHeaderClause
    | transformClause
    | beanClause
    | delayClause
    | throttleClause
    | logClause
    | endClause
    | routeIdClause
    | idClause
    ;

// ==================== ПРОСТЫЕ КЛАУЗЫ ====================
toClause
    : 'to' '(' uri ')'                 // to("direct:result")
    ;

processClause
    : 'process' '(' (beanRef | lambda) ')'  // process(exchange -> {...})
    ;

beanClause
    : 'bean' '(' beanRef (',' methodName)? ')'  // bean("myBean", "process")
    ;

setBodyClause
    : 'setBody' '(' expression ')'      // setBody(constant("Hello"))
    ;

setHeaderClause
    : 'setHeader' '(' STRING ',' expression ')'  // setHeader("type", constant("A"))
    ;

transformClause
    : 'transform' '(' expression ')'    // transform(body().regexReplaceAll(...))
    ;

filterClause
    : 'filter' '(' predicate ')'         // filter(header("type").isEqualTo("A"))
      (clause)*
    ;

delayClause
    : 'delay' '(' INTEGER ')'            // delay(1000)
      (clause)*
    ;

throttleClause
    : 'throttle' '(' INTEGER ')'         // throttle(10)
      (clause)*
    ;

logClause
    : 'log' '(' STRING ')'               // log("Processing order")
    ;

routeIdClause
    : 'routeId' '(' STRING ')'           // routeId("myRoute")
    ;

idClause
    : 'id' '(' STRING ')'                // id("step1")
    ;

endClause
    : 'end' '(' ')'                      // .end()
    ;

// ==================== EIP-БЛОКИ (ВЛОЖЕННЫЕ) ====================

// -------- CHOICE --------
choiceClause
    : 'choice' '(' ')'                   // .choice()
      (whenClause)+
      (otherwiseClause)?
    ;

whenClause
    : 'when' '(' predicate ')'           // .when(header("type").isEqualTo("A"))
      (clause)*
    ;

otherwiseClause
    : 'otherwise' '(' ')'                // .otherwise()
      (clause)*
    ;

// -------- SPLIT --------
splitClause
    : 'split' '(' expression ')'         // .split(body())
      (clause)*
    ;

// -------- DO TRY / CATCH / FINALLY --------
doTryClause
    : 'doTry' '(' ')'                    // .doTry()
      (clause)*
      (doCatchClause)+
      (doFinallyClause)?
    ;

doCatchClause
    : 'doCatch' '(' exceptionType ')'    // .doCatch(Exception.class)
      (clause)*
    ;

doFinallyClause
    : 'doFinally' '(' ')'                // .doFinally()
      (clause)*
    ;

// ==================== ВСПОМОГАТЕЛЬНЫЕ ПРАВИЛА ====================
uri         : STRING ;
expression  : STRING | SIMPLE_EXPRESSION | JAVA_EXPRESSION ;
predicate   : STRING | JAVA_EXPRESSION | IDENTIFIER ;
beanRef     : IDENTIFIER ('.' IDENTIFIER)? ;
methodName  : IDENTIFIER ;
exceptionType : IDENTIFIER ('.' IDENTIFIER)* ;
lambda      : '(' IDENTIFIER ')' '->' JAVA_BLOCK ;

// ==================== ЛЕКСЕМЫ ====================

// Строки
STRING      : '"' ( ESC_SEQ | ~["\\\r\n] )* '"' ;
SINGLE_QUOTED : '\'' ( ESC_SEQ | ~['\\\r\n] )* '\'' ;
SIMPLE_EXPRESSION : '${' ( ~[}] )* '}' ;

// Java-выражения (упрощенно)
JAVA_EXPRESSION
    : IDENTIFIER ('.' IDENTIFIER)* '(' (STRING | IDENTIFIER | INTEGER | JAVA_EXPRESSION)* ')'
    | IDENTIFIER ('.' IDENTIFIER)*
    ;

JAVA_BLOCK
    : '{' ( ~[{}] | JAVA_BLOCK )* '}'
    ;

// Идентификаторы
IDENTIFIER  : [a-zA-Z_$][a-zA-Z0-9_$]* ;
INTEGER     : [0-9]+ ;

// Escape-последовательности
fragment ESC_SEQ
    : '\\' ( 'n' | 'r' | 't' | 'b' | 'f' | '"' | '\'' | '\\' | '/' )
    | '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;

fragment HEX_DIGIT : [0-9a-fA-F] ;

// Пропускаем пробелы и комментарии
WS          : [ \t\r\n]+ -> skip ;
COMMENT     : '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT : '/*' .*? '*/' -> skip ;
