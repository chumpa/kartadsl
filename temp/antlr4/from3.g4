grammar from3;

// ==================== КОРНЕВОЕ ПРАВИЛО ====================
route
    : 'from' '(' uri ')'
      (routeOption | chainElement)*
      (';'? | EOF)
    ;

// ==================== ОПЦИИ РОУТА ====================
routeOption
    : 'onException' '(' exceptionType ')' chainBody
    | 'transacted' '(' ')'
    | 'policy' '(' stringLiteral ')'
    ;

// ==================== ЦЕПОЧКА ВЫЗОВОВ ====================
chainElement
    : methodCall ('.' methodCall)*
    ;

methodCall
    : simpleMethod
    | eipBlockMethod
    | endMethod
    | endChoiceMethod
    ;

// -------- Простые методы ----------
simpleMethod
    : 'process' '(' processorRef ')'                    # ProcessCall
    | 'process' '(' lambdaExpression ')'               # ProcessLambda
    | 'bean' '(' beanRef (',' methodName)? ')'         # BeanCall
    | 'to' '(' uri ')'                                 # ToCall
    | 'toD' '(' uri ')'                                # ToDynamicCall
    | 'setBody' '(' expression ')'                     # SetBodyCall
    | 'setHeader' '(' stringLiteral ',' expression ')' # SetHeaderCall
    | 'removeHeader' '(' stringLiteral ')'             # RemoveHeaderCall
    | 'transform' '(' expression ')'                   # TransformCall
    | 'delay' '(' duration ')'                         # DelayCall
    | 'throttle' '(' count ')'                         # ThrottleCall
    | 'filter' '(' predicate ')'                       # FilterCall
    | 'log' '(' logLevel? ',' stringLiteral ')'        # LogCall
    | 'id' '(' stringLiteral ')'                       # IdCall
    | 'routeId' '(' stringLiteral ')'                  # RouteIdCall
    ;

// -------- EIP-методы с вложенными блоками ----------
eipBlockMethod
    : 'choice' '(' ')' chainBody                       # ChoiceBlock
    | 'split' '(' expression ')' chainBody             # SplitBlock
    | 'aggregate' '(' expression ',' strategy ')' chainBody  # AggregateBlock
    | 'resequence' '(' expression ')' chainBody        # ResequenceBlock
    | 'multicast' '(' ')' chainBody                    # MulticastBlock
    | 'routingSlip' '(' header ')' chainBody           # RoutingSlipBlock
    | 'dynamicRouter' '(' beanRef ')' chainBody        # DynamicRouterBlock
    | 'loadBalance' '(' strategy ')' chainBody         # LoadBalanceBlock
    | 'circuitBreaker' '(' ')' chainBody               # CircuitBreakerBlock
    | 'doTry' '(' ')' chainBody                        # DoTryBlock
    | 'onFallback' '(' ')' chainBody                   # OnFallbackBlock
    | 'when' '(' predicate ')' chainBody               # WhenBlock
    | 'otherwise' '(' ')' chainBody                    # OtherwiseBlock
    | 'doCatch' '(' exceptionType ')' chainBody        # DoCatchBlock
    | 'doFinally' '(' ')' chainBody                    # DoFinallyBlock
    ;

// -------- Методы завершения блоков ----------
endMethod
    : 'end' '(' ')'                                     # EndCall
    ;

endChoiceMethod
    : 'endChoice' '(' ')'                               # EndChoiceCall
    ;

// ==================== ТЕЛО БЛОКА ====================
chainBody
    : '{' chainElement* '}'        # BlockBody
    | ';' chainElement*            # InlineBody
    | (chainElement)*              # ImplicitBody  // ← для cases без { }
    ;

// ==================== ЛЕКСЕМЫ ====================
uri             : STRING_LITERAL | TEMPLATE_STRING ;
expression      : STRING_LITERAL | SINGLE_QUOTED_STRING | SIMPLE_EXPRESSION | TEMPLATE_STRING ;
predicate       : expression | IDENTIFIER ;
processorRef    : IDENTIFIER ('.' IDENTIFIER)? ;
beanRef         : IDENTIFIER ('.' IDENTIFIER)? ;
methodName      : IDENTIFIER ;
header          : STRING_LITERAL ;
strategy        : IDENTIFIER | STRING_LITERAL ;
exceptionType   : IDENTIFIER ('.' IDENTIFIER)* ;
logLevel        : 'DEBUG' | 'INFO' | 'WARN' | 'ERROR' ;
duration        : INTEGER ( 'ms' | 's' | 'm' )? ;
count           : INTEGER ;

lambdaExpression
    : '(' IDENTIFIER ')' '->' '{' JAVA_CODE '}'   # LambdaBlock
    | IDENTIFIER '::' IDENTIFIER                  # LambdaMethodRef
    ;

stringLiteral
    : STRING_LITERAL
    | SINGLE_QUOTED_STRING
    | TEXT_BLOCK
    ;

// ==================== ТЕРМИНАЛЫ ====================
STRING_LITERAL  : '"' ( ESC_SEQ | ~["\\\r\n] )* '"' ;
SINGLE_QUOTED_STRING : '\'' ( ESC_SEQ | ~['\\\r\n] )* '\'' ;
TEXT_BLOCK      : '"""' ( . | '\n' )*? '"""' ;
SIMPLE_EXPRESSION : '${' ( ~[}] )* '}' ;
TEMPLATE_STRING : '"' ( ~["\\] | '\\' . | '${' ~[}]* '}' )* '"' ;
JAVA_CODE       : ~[{}]+ ;

IDENTIFIER      : [a-zA-Z_$][a-zA-Z0-9_$]* ;
INTEGER         : [0-9]+ ;
WS              : [ \t\r\n]+ -> skip ;
COMMENT         : '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT   : '/*' .*? '*/' -> skip ;

fragment ESC_SEQ
    : '\\' ( 'n' | 'r' | 't' | 'b' | 'f' | '"' | '\'' | '\\' | '/' )
    | '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    | '\\' 'x' HEX_DIGIT HEX_DIGIT?
    ;

fragment HEX_DIGIT : [0-9a-fA-F] ;