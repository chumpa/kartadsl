grammar Grammar1;

// Парсерные правила
route
    : 'from' '(' endpoint ')' 
      (processor | choice | setBody | to | log | delay | transform | filter)*
    ;

endpoint
    : STRING_LITERAL
    | DIRECT_ENDPOINT
    ;

// Основные EIP паттерны
processor
    : 'process' '(' beanExpression ')' 
    ;

choice
    : 'choice' '(' ')' 
      (whenClause)+ 
      (otherwiseClause)?
      'end' '(' ')'
    ;

whenClause
    : 'when' '(' simpleExpression ')' 
      (processor | to | log | setBody | transform | filter)*
    ;

otherwiseClause
    : 'otherwise' '(' ')' 
      (processor | to | log | setBody | transform | filter)*
    ;

filter
    : 'filter' '(' simpleExpression ')' 
      (processor | to | log | setBody | transform)*
    ;

to
    : 'to' '(' endpoint ')' 
    ;

log
    : 'log' '(' STRING_LITERAL ')' 
    ;

setBody
    : 'setBody' '(' simpleExpression ')' 
    ;

delay
    : 'delay' '(' number ')' 
    ;

transform
    : 'transform' '(' simpleExpression ')' 
    ;

// Выражения
beanExpression
    : BEAN_REF '.' METHOD_NAME '(' (argument (',' argument)*)? ')'
    ;

simpleExpression
    : SIMPLE_PREFIX simpleBody SIMPLE_SUFFIX
    | STRING_LITERAL
    | BEAN_REF
    | 'header' '(' STRING_LITERAL ')'
    | 'body' '(' ')'
    ;

simpleBody: ' ';

argument
    : STRING_LITERAL
    | number
    | BEAN_REF
    ;

number
    : INT
    | FLOAT
    ;

// Лексерные правила
DIRECT_ENDPOINT
    : 'direct:' IDENTIFIER
    ;

BEAN_REF
    : '#' IDENTIFIER
    ;

METHOD_NAME
    : IDENTIFIER
    ;

SIMPLE_PREFIX
    : '${'
    ;

SIMPLE_SUFFIX
    : '}'
    ;

STRING_LITERAL
    : '"' (~["\r\n])* '"'
    | '\'' (~['\r\n])* '\''
    ;

INT
    : [0-9]+
    ;

FLOAT
    : [0-9]+ '.' [0-9]+
    ;

IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_]*
    ;

WS
    : [ \t\r\n]+ -> skip
    ;

COMMENT
    : '//' ~[\r\n]* -> skip
    ;
