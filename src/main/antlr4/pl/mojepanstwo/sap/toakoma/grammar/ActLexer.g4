lexer grammar ActLexer;

ROOT_NODE
    : ('<html xmlns="http://www.w3.org/1999/xhtml">' | '</html>') -> skip
    ;

WHITESPACE
    : [\t\n\r\f ]+ -> skip
    ;

TITLE_O
    : '<title>' -> pushMode(TITLE)
    ;

MAIN_O
    : '<main>' -> pushMode(MAIN)
    ;


mode TITLE;

TITLE_WHITESPACE
    : WHITESPACE -> type(WHITESPACE)
    ;

TITLE_C
    : '</title>' -> popMode
    ;


mode MAIN;

MAIN_WHITESPACE
    : WHITESPACE -> type(WHITESPACE)
    ;

MAIN_C
    : '</main>' -> popMode
    ;
