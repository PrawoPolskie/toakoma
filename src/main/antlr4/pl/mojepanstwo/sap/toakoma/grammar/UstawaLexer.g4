lexer grammar UstawaLexer;

// Default "mode"
WS          : [ \r\n]+                 -> skip ;

HTML_O      : '<html xmlns="http://www.w3.org/1999/xhtml">' -> pushMode(HTML) ;

fragment
DIGIT       :   [0-9] ;

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

ANOTES_O    : '<authorialNotes>'       -> pushMode(ANOTES) ;

BODY_C      : '</body>'                -> popMode ;

// ----------------- Everything INSIDE of a title ---------------------
mode TITLE;

TITLE_WS     : [ \r\n]+                -> skip ;

TITLE_LINE   : '<line>'.*'</line>' { setText(getText().substring(6,getText().length()-7)); };
T_EMPTY_LINE : '</line>'               -> skip ;

AN_MARK      : '<authorialNoteMark>'.*'</authorialNoteMark>' { setText(getText().substring(6,getText().length()-7)); };

TITLE_C      : '</title>'              -> popMode ;

// ----------------- Everything INSIDE of a main ---------------------
mode MAIN;

MAIN_WS      : [ \r\n]+                -> skip ;

MAIN_LINE    : '<line>'.*'</line>' { setText(getText().substring(6,getText().length()-7)); };
M_EMPTY_LINE : '</line>'               -> skip ;

MAIN_C       : '</main>'               -> popMode ;

// ----------------- Everything INSIDE of a authorialNotes ---------------------
mode ANOTES;

ANOTES_WS  : [ \r\n]+                  -> skip ;

ANOTE_O    : '<authorialNote '         -> pushMode(ANOTE) ;

ANOTES_C   : '</authorialNotes>'       -> popMode ;

// ----------------- Everything INSIDE of a authorialNote ---------------------
mode ANOTE;

ANOTE_WS  : [ \r\n]+                   -> skip ;

AMARK     : 'marker="'DIGIT*')">' { setText(getText().substring(6,getText().length()-7)); };

ALINE     : '<line>'.*'</line>' { setText(getText().substring(6,getText().length()-7)); };

ANOTE_C   : '</authorialNote>'         -> popMode ;
