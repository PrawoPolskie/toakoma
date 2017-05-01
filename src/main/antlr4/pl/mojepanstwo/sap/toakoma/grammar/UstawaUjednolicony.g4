/**
 * Define a grammar
 */
grammar UstawaUjednolicony;

HEADER_OF_PAGE : '©Kancelaria Sejmu  s. ' .*?[\n].*?[\n] -> skip ;

ustawa
   : artykul + EOF
   ;

artykul : 'x';


