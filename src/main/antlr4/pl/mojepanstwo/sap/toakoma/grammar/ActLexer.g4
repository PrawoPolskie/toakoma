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

public static String END_MAIN = "^\n</main>(?s:.)*$";

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


