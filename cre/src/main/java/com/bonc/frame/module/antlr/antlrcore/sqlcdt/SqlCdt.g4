grammar SqlCdt;

parse
 : ( expr )* EOF
 ;

expr
 : expr ( PLUS | MINUS | STAR | DIV | MOD ) expr
 | expr ( LT | LT_EQ | GT | GT_EQ ) expr
 | expr ( ASSIGN | NOT_EQ1 | NOT_EQ2 | PIPE2 | K_IS | K_IS K_NOT | K_IN | K_LIKE ) expr
 | expr op=(K_AND | K_OR) expr
 | function_name '(' ( K_DISTINCT? expr ( ',' expr )* | '*' )? ')'
 | OPEN_PAR expr (',' expr)* CLOSE_PAR
 | expr ( K_ISNULL | K_NOTNULL | K_NOT K_NULL )
 | expr K_IS K_NOT? expr
 | expr K_IS K_NOT? K_NULL
 | expr K_NOT? K_BETWEEN expr K_AND expr
 | STRING
 | ID2
 | NUMBER
 ;

function_name
 : ID
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


K_ADD      : A D D;
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


NUMBER : [0-9]+('.'([0-9]+)?)? ;        // 数字
ID : [a-zA-Z_][a-zA-Z0-9_]* ;      		// 标识符，由0到多个小写字母组成
ID1 : [a-zA-Z0-9_]+ ;
ID2 :'[' ID1 ('.' ID1)? ']';
STRING : '"' ('\\"'|.)*? '"'  			// 字符串
	   | '\'' ('\\"'|.)*? '\'' ; 		// 单引号字符串

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
