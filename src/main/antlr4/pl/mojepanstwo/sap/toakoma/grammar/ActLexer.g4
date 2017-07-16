lexer grammar ActLexer;

@header
{
}

@parser::members
{
}


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

TITLE_C
    : '\n</title>' -> popMode
    ;

DZIENNIK_USTAW
    : '\nDZIENNIK USTAW'~[\n]*
    ;


mode MAIN;

MAIN_C
    : '\n</main>' -> popMode
    ;

fragment PARAGRAPH_START: '\n§ '[0-9]*'.';

PARAGRAPH
    : PARAGRAPH_START.*
    ;

PREAMBLE
    : '\n'.* { (_input.LA(1) == '\n') &&
    	       (_input.LA(2) == '<') &&
               (_input.LA(3) == '/') &&
               (_input.LA(4) == 'm') &&
               (_input.LA(5) == 'a') &&
               (_input.LA(6) == 'i') &&
               (_input.LA(7) == 'n') &&
               (_input.LA(8) == '>')
             }?
    ;

    //{ setText(getText().substring(6,getText().length()-7)); };


