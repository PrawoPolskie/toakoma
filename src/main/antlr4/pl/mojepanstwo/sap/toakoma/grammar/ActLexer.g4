lexer grammar ActLexer;

@header
{
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
}

@lexer::members
{

public static String END_TITLE = "^\n</title>(?s:.)*$";
public static String END_MAIN  = "^\n</main>(?s:.)*$";

boolean untilRegexes(int max, String... regexes) {
    String future = java.util.stream.IntStream.rangeClosed(1, max)
                                              .boxed()
                                              .<String>map(i -> {
                                                  int c = _input.LA(i);
                                                  return c != -1 ? Character.toString((char) c) : " ";
                                              })
                                              .reduce((acc, e) -> acc  + e).get();
    return Stream.of(regexes).anyMatch(re -> future.matches(re));
}
}


ROOT_NODE
    : ('<html xmlns="http://www.w3.org/1999/xhtml">' | '</html>') -> skip
    ;

NL
    : '\n'
    ;

WHITESPACE
    : [\t\n\r\f ]+ -> skip
    ;

HTML_TITLE_O
    : '<title>' -> pushMode(HTML_TITLE)
    ;

HTML_MAIN_O
    : '<main>' -> pushMode(HTML_MAIN)
    ;



AUTHORIALNOTEMARK_O
   : '\n<authorialNoteMark>'
   ;

AUTHORIALNOTEMARK
   : [0-9]+')'
   ;

AUTHORIALNOTEMARK_C
   : '</authorialNoteMark>'
   ;

mode HTML_TITLE;

TITLE_NL : NL -> type(NL);

HTML_TITLE_C
    : TITLE_NL '</title>' -> popMode
    ;

DZIENNIK_USTAW
    : TITLE_NL 'DZIENNIK USTAW'~[\n]*?
      TITLE_NL 'RZECZYPOSPOLITEJ POLSKIEJ'~[\n]*?
    ;

CITY
    : TITLE_NL 'Warszawa, '
    ;

DATE
    : 'dnia '[0-9]+' '[a-z]+' '[0-9]+' r.'~[\n]*?
    ;

POSITION
    : TITLE_NL 'Poz. '[0-9]+~[\n]*?
    ;

NL_CAPITALIC
    : TITLE_NL ([A-Z]|'Ą'|' ')+~[\n]*?
    ;

TITLE_AUTHORIALNOTEMARK_O : AUTHORIALNOTEMARK_O -> type(AUTHORIALNOTEMARK_O);
TITLE_AUTHORIALNOTEMARK   : AUTHORIALNOTEMARK   -> type(AUTHORIALNOTEMARK);
TITLE_AUTHORIALNOTEMARK_C : AUTHORIALNOTEMARK_C -> type(AUTHORIALNOTEMARK_C);

DATE2
    : TITLE_NL 'z ' DATE
    ;

//TITLE
//    : '\n'.*? { untilRegexes(10, END_TITLE) }?
//    ;

mode HTML_MAIN;

HTML_MAIN_C
    : '\n</main>' -> popMode
    ;

fragment
PARAGRAPH_START
    : '\n§ '[0-9]*'.'
    ;

PARAGRAPH
    : PARAGRAPH_START.*? { untilRegexes(10, END_MAIN) }?
    ;

PREAMBLE
    : '\n'.*? { untilRegexes(10, END_MAIN) }?
    ;
    //{ setText(getText().substring(6,getText().length()-7)); };


