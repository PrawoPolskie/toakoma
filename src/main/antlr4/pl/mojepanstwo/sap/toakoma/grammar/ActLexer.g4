lexer grammar ActLexer;

ROOT_NODE
    : ('<html xmlns="http://www.w3.org/1999/xhtml">' | '</html>') -> skip
    ;

WHITESPACE
    : [\t\n\r\f ]+ -> skip
    ;
