grammar from;

// в данном примере всё утащено в лексер, что просто написать но сложно развивать
// правило 'endpoint' нельзя использовать в лексере, это косяк подхода
// парсер
proga: (route|config)* EOF;
route: ROUTE;
config: CFG;
endpoint: STRING_LITERAL ;

// лексер
ROUTE: FROM ('.' TO)* ';';
FROM: 'from' '(' STRING_LITERAL? ')';
TO: 'to' '(' ')';

CFG: CONFIG ';';
CONFIG: 'config' '(' ')';
STRING_LITERAL: '"' (~["\r\n])* '"';

WS : [ \t\r\n]+ -> skip ;
