parser grammar ActParser;

options { tokenVocab=ActLexer; }

act
    : HTML_TITLE_O
      html_title
      HTML_TITLE_C
      HTML_MAIN_O
      html_main
      HTML_MAIN_C
    ;


html_title
    : preface
    ;

preface
    : DZIENNIK_USTAW
      CITY
      DATE
      POSITION
      what
      whos
      authorialNoteMark?
      DATE2
      title
    ;

what
    : NL_CAPITALIC
    ;

whos
    : NL_CAPITALIC
    ;

title
    : NL_ALPHA
    ;

authorialNoteMark
   : AUTHORIALNOTEMARK_O
     AUTHORIALNOTEMARK
     AUTHORIALNOTEMARK_C
   ;


html_main
    : preamble?
      paragraph*?
    ;

preamble
    : PREAMBLE
    ;

paragraph
    : PARAGRAPH
    ;

