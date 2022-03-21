grammar SqlColumn;

exprs : calcExpr ;                           // 或calc表达式

calcExpr: 'check' column;                    // 以action命令开头，后面是一个计算表达式

column
 : funCall
 | ('case'|'CASE') expr? ( ('WHEN'|'when') expr ('then'|'THEN') expr )+ ( ('else'|'ELSE') expr )? ('end'|'END')
 ;

expr
 : expr op=(EVAL|LT|LT_EQ|GT|GT_EQ|NOT_EQ1|NOT_EQ2) expr
 | expr ('is null'|'IS NULL'|'is not null'|'IS NOT NULL')
 | factor 
 ;

factor
 : (sign=(ADD | SUB))? num=NUMBER    	// 计算因子可以是一个正数或负数
 | '(' expr ')'                        	// 计算因子可以是括号括起来的表示式
 | param				 				// 计算因子可以是自定义的参数
 | STRING
 ;
 
funCall
 : name=ID '(' params ')'			//有参函数
 | name=ID '(*)'						//无参函数
 ;        							// 函数名后面加参数列表
	
params : param (',' params)? ;            // 参数列表是由一个表达式后面跟关一个可选的参数列表组成


param
 : '[' (( ID '.')? ID '.')? ID ']'
 ;

 
 
WS : [ \t\n\r]+ -> skip ;                	// 空白， 后面的->skip表示antlr4在分析语言的文本时，符合这个规则的词法将被无视
ID : [_a-zA-Z_]+([_a-zA-Z0-9_])? ;         // 标识符，由0到多个小写字母组成
NUMBER : [0-9]+('.'([0-9]+)?)? ;        	// 数字
STRING 
 : '"' ('\\"'|.)*? '"'  		// 双引号字符串
 | '\'' ('\\"'|.)*? '\'' 		// 单引号字符串
 ;

ADD : '+';
SUB : '-';
EVAL: '=';
LT : '<';
LT_EQ : '<=';
GT : '>';
GT_EQ : '>=';
NOT_EQ1 : '!=';
NOT_EQ2 : '<>';


