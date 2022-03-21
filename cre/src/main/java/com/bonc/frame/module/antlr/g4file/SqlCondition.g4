grammar SqlCondition;

parse
 : ( expr )* EOF
 ;

expr
 : ( ( database_name '.' )? table_name '.' )? column_name
 | expr '||' expr
 | expr op=( STAR | DIV | MOD | PLUS | MINUS ) expr
 | expr op=( ASSIGN | LT | LT_EQ | GT |GT_EQ | NOT_EQ1 | NOT_EQ2) expr
 | expr ( K_IS | K_IS K_NOT | K_IN | K_LIKE ) expr
 | expr K_AND expr
 | expr K_OR expr
 | function_name '(' ( K_DISTINCT? expr ( ',' expr )* | '*' )? ')'
 | '(' expr ')'
 | expr ( K_ISNULL | K_NOTNULL | K_NOT K_NULL )
 | expr K_IS K_NOT? expr
 | expr K_NOT? K_BETWEEN expr K_AND expr
 ;


keyword
 : K_ADD
 | K_AND
 | K_BETWEEN
 | K_IN
 | K_IS
 | K_ISNULL
 | K_LIKE
 | K_NOT
 | K_NOTNULL
 | K_NULL
 | K_OR
 | K_DISTINCT
 ;


name
 : any_name
 ;

function_name
 : any_name
 ;

database_name
 : any_name
 ;

table_name 
 : any_name
 ;

column_name 
 : any_name
 ;

any_name
 : IDENTIFIER 
 | NUMERIC_LITERAL
 | keyword
 | STRING_LITERAL
 | '(' any_name ')'
 ;

DOT : '.';
OPEN_PAR : '(';
CLOSE_PAR : ')';
COMMA : ',';
ASSIGN : '=';
STAR : '*';
PLUS : '+';
MINUS : '-';
TILDE : '~';
PIPE2 : '||';
DIV : '/';
MOD : '%';
LT : '<';
LT_EQ : '<=';
GT : '>';
GT_EQ : '>=';
NOT_EQ1 : '!=';
NOT_EQ2 : '<>';


K_ADD      :A D D;
K_AND      :A N D;
K_BETWEEN  :B E T W E E N;
K_IN       :I N;
K_IS       :I S;
K_ISNULL   :I S N U L L;
K_LIKE     :L I K E ;
K_NOT      :N O T;
K_NOTNULL  :N O T N U L L;
K_NULL     :N U L L;
K_OR       :O R;
K_DISTINCT :D I S T I N C T;


IDENTIFIER
 : '"' (~'"' | '""')* '"'
 | '`' (~'`' | '``')* '`'
 | '[' ~']'* ']'
 | [a-zA-Z_] [a-zA-Z_0-9]*
 ;

NUMERIC_LITERAL
 : DIGIT+ ( '.' DIGIT* )? ( E [-+]? DIGIT+ )?
 | '.' DIGIT+ ( E [-+]? DIGIT+ )?
 ;


STRING_LITERAL
 : '\'' ( ~'\'' | '\'\'' )* '\''
 ;

WS : [ \t\n\r]+ -> skip ;                // 空白， 后面的->skip表示antlr4在分析语言的文本时，符合这个规则的词法将被无视

fragment DIGIT : [0-9];

fragment A : [aA];
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];
