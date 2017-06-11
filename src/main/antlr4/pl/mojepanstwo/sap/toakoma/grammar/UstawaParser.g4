parser grammar UstawaParser;

options { tokenVocab=UstawaLexer; }

act         :   HTML_O BODY_O ;


//
//prolog      :   XMLDeclOpen attribute* SPECIAL_CLOSE ;
//
//content     :   chardata?
//                ((element | reference | CDATA | PI | COMMENT) chardata?)* ;
//
//element     :   '<' Name attribute* '>' content '<' '/' Name '>'
//            |   '<' Name attribute* '/>'
//            ;
//
//reference   :   EntityRef | CharRef ;
//
//attribute   :   Name '=' STRING ; // Our STRING is AttValue in spec
//
///** ``All text that is not markup constitutes the character data of
// *  the document.''
// */
//chardata    :   TEXT | SEA_WS ;
//
