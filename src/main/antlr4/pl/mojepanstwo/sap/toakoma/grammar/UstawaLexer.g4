lexer grammar UstawaLexer;

// Default "mode"
WS          : [ \r\n]+                 -> skip ;

HTML_O      : '<html xmlns="http://www.w3.org/1999/xhtml">' -> pushMode(HTML) ;

// Default "mode": Everything OUTSIDE of a tag
//CDATA       :   '<![CDATA[' .*? ']]>' ;
///** Scarf all DTD stuff, Entity Declarations like <!ENTITY ...>,
// *  and Notation Declarations <!NOTATION ...>
// */
//DTD         :   '<!' .*? '>'            -> skip ;
//EntityRef   :   '&' Name ';' ;
//CharRef     :   '&#' DIGIT+ ';'
//            |   '&#x' HEXDIGIT+ ';'
//            ;
//SEA_WS      :   (' '|'\t'|'\r'? '\n')+ ;

//
//OPEN        :   '<'                     -> pushMode(INSIDE) ;
//XMLDeclOpen :   '<?xml' S               -> pushMode(INSIDE) ;
//SPECIAL_OPEN:   '<?' Name               -> more, pushMode(PROC_INSTR) ;
//
//TEXT        :   ~[<&]+ ;        // match any 16 bit char other than < and &

// ----------------- Everything INSIDE of a html ---------------------
mode HTML;

HTML_WS     : [ \r\n]+                 -> skip ;

BODY_O      : '<body>'                 -> pushMode(BODY) ;

HTML_C      : '</html>'                -> popMode ;

// ----------------- Everything INSIDE of a html ---------------------
mode BODY;

BODY_WS     : [ \r\n]+                 -> skip ;

TITLE_O     : '<title>'                -> pushMode(TITLE) ;
MAIN_O      : '<main>'                 -> pushMode(MAIN) ;

BODY_C      : '</body>'                -> popMode ;

// ----------------- Everything INSIDE of a title ---------------------
mode TITLE;

TITLE_LINE : '<line>'.*'</line>' { setText(getText().toUpperCase()); };

TITLE_C    : '</title>'              -> popMode ;

// ----------------- Everything INSIDE of a main ---------------------
mode MAIN;

MAIN_LINE  : '<line>'.*'</line>' { setText(getText().toUpperCase()); };
EMPTY_LINE : '</line>'                 -> skip ;

MAIN_C     : '</main>'                -> popMode ;




//SLASH       :   '/' ;
//EQUALS      :   '=' ;
//STRING      :   '"' ~[<"]* '"'
//            |   '\'' ~[<']* '\''
//            ;
//Name        :   NameStartChar NameChar* ;
//S           :   [ \t\r\n]               -> skip ;
//
//fragment
//HEXDIGIT    :   [a-fA-F0-9] ;
//
//fragment
//DIGIT       :   [0-9] ;
//
//fragment
//NameChar    :   NameStartChar
//            |   '-' | '_' | '.' | DIGIT
//            |   '\u00B7'
//            |   '\u0300'..'\u036F'
//            |   '\u203F'..'\u2040'
//            ;
//
//fragment
//NameStartChar
//            :   [:a-zA-Z]
//            |   '\u2070'..'\u218F'
//            |   '\u2C00'..'\u2FEF'
//            |   '\u3001'..'\uD7FF'
//            |   '\uF900'..'\uFDCF'
//            |   '\uFDF0'..'\uFFFD'
//            ;
//
//// ----------------- Handle <? ... ?> ---------------------
//mode PROC_INSTR;
//
//PI          :   '?>'                    -> popMode ; // close <?...?>
//IGNORE      :   .                       -> more ;
//
