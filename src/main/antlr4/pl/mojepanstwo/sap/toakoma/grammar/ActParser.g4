parser grammar ActParser;

options { tokenVocab=ActLexer; }

act
    :    title main
    ;

main
    : PREAMBLE? paragraph*
    ;


paragraph
    : PARAGRAPH
    ;




title
    : .*?
    ;
