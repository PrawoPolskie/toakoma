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

private boolean canBePreamble = true;

public static String END_TITLE   = "^\n</title>(?s:.)*$";
public static String END_MAIN    = "^\n</main>(?s:.)*$";
public static String PARAGRAPH_S = "^\n§ [0-9]*\\.(?s:.)*$";

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

@after {
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

HTML_TITLE_C
    : NL '</title>' -> popMode
    ;

DZIENNIK_USTAW
    : NL 'DZIENNIK USTAW'~[\n]*?
      NL 'RZECZYPOSPOLITEJ POLSKIEJ'~[\n]*?
    ;

CITY
    : NL 'Warszawa, '
    ;

DATE
    : 'dnia '[0-9]+' '[a-z]+' '[0-9]+' r.'~[\n]*?
    ;

POSITION
    : NL 'Poz. '[0-9]+~[\n]*?
    ;

NL_CAPITALIC
    : NL ([A-Z]|'Ą'|' ')+~[\n]*?
    ;

DATE2
    : NL 'z ' DATE -> pushMode(TITLE_TITLE)
    ;

TITLE_AUTHORIALNOTEMARK_O : AUTHORIALNOTEMARK_O -> type(AUTHORIALNOTEMARK_O);
TITLE_AUTHORIALNOTEMARK   : AUTHORIALNOTEMARK   -> type(AUTHORIALNOTEMARK);
TITLE_AUTHORIALNOTEMARK_C : AUTHORIALNOTEMARK_C -> type(AUTHORIALNOTEMARK_C);


mode TITLE_TITLE;

NL_ALPHA
    : NL.+? { untilRegexes(10, END_TITLE) }? -> popMode
    ;


mode HTML_MAIN;

HTML_MAIN_C
    : '\n</main>' -> popMode
    ;

PARAGRAPH_START
    : '\n§ '[0-9]+'. ' { canBePreamble = false; } -> pushMode(PARAGRAPH)
    ;

PREAMBLE
    : '\n'.+? { canBePreamble && untilRegexes(10, END_MAIN,
  	                                              PARAGRAPH_S) }?
  	          { canBePreamble = false; }
    ;
    //{ setText(getText().substring(6,getText().length()-7)); };

mode PARAGRAPH;

PARAGRAPH_ANY
    : .+? { untilRegexes(10, END_MAIN,
    	                     PARAGRAPH_S) }? -> popMode
    ;
