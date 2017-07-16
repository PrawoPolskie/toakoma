parser grammar ActParser;

options { tokenVocab=ActLexer; }

act
    : TITLE_O title TITLE_C MAIN_O main MAIN_C
    ;


title
    : preface
    ;

preface
    : DZIENNIK_USTAW
    ;


main
    : preamble? paragraph?
    ;

preamble
    : PREAMBLE
    ;

paragraph
    : PARAGRAPH
    ;

