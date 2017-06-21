parser grammar UstawaParser;

options { tokenVocab=UstawaLexer; }

act         :   HTML_O BODY_O title main footnotes? BODY_C HTML_C;

title       :   TITLE_O (TITLE_LINE | AN_MARK)* TITLE_C;

main        :   MAIN_O (MAIN_LINE | AN_MARK)* MAIN_C;

footnotes   :   ANOTES_O footnote* ANOTES_C;

footnote    :   ANOTE_O AMARK ALINE* ANOTE_C;
