grammar from2;

// парсер
proga: (route|config|apilist|ico|poae)* EOF;
route: from ('.' to)* ';'+;
from: 'from' '(' endpoint ')';
to: 'to' '(' endpoint ')';
endpoint: STRING_LITERAL;
sidonly: STRING_LITERAL;

config: cfg ( ('.'set|'.'secstore)+ ) ';'+;
cfg: 'config' '(' endpoint ')';
set: 'set' '(' endpoint ',' endpoint ')';
secstore: 'secstore' '(' endpoint ')';

apilist: apisid ';'+;
apisid: 'api' '(' endpoint ')';

poae: 'poae' '(' sidonly ')' ';';

// ICo
ico: 'ico' '(' icoheader ')' ('.' poaeref) ('.' channel) ';'+;
poaeref: 'poae' '(' refid ')';
channel: 'channel' '(' refid ')';
icoheader: STRING_LITERAL;
namespace: STRING_LITERAL;
name: STRING_LITERAL;
refid: STRING_LITERAL;

// лексер
STRING_LITERAL: '"' (~["\r\n])* '"';

WS : [ \t\r\n]+ -> skip ;
COMMENT      : '/*' .*? '*/'    -> channel(HIDDEN);
LINE_COMMENT : '//' ~[\r\n]*    -> channel(HIDDEN);
