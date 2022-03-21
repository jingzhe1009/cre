grammar Condition;                            // 文法的名字为Condition

//条件部分校验文件

// 以下以小写字母开头的文法表示为语法元素
// 由大写字母开头的文法表示为词法元素
// 词法元素的表示类似于正则表示式
// 语法元素的表示类似于BNF

exprs : calcExpr ;                           // 或calc表达式

calcExpr: 'condition' expr ;                    // 以calc命令开头，后面是一个计算表达式

// expr可能由多个产生式
// 在前面的产生式优先于在后面的产生式
// 这样来解决优先级的问题

//> >= < <= == != contains / not contains / memberOf / not memberOf
expr: expr op=(AND | OR) expr			// 并且,或者,非
	| expr op=(MUL | DIV) expr            	// 乘法或除法
    | expr op=(ADD | SUB) expr            	// 加法或减法
	| expr op=(GT|GE|LT|LE|EQUAL|NE) expr		//> >= < <= == !=
	| expr op=(CT|NCT|MO|NMO) arrExpr			//contains / not contains / memberOf / not memberOf
    | factor                            	// 一个计算因子——可做为+-*/的操作数据的东西
	;



	
factor: (sign=(ADD | SUB))? num=NUMBER    	// 计算因子可以是一个正数或负数
    | '(' expr ')'                        	// 计算因子可以是括号括起来的表示式
	| '[' id=ID ']' 					 	// 计算因子可以是自定义的参数
    | funCall                            	// 计算因子可以是一个函数调用
    | STRING
	;

funCall: name=ID '(' params ')'				//有参函数
	   | name=ID '('')'						//无参函数
	;        // 函数名后面加参数列表
	
params : expr (',' params)? ;            // 参数列表是由一个表达式后面跟关一个可选的参数列表组成


WS : [ \t\n\r]+ -> skip ;                // 空白， 后面的->skip表示antlr4在分析语言的文本时，符合这个规则的词法将被无视
ID : [a-zA-Z_][a-zA-Z0-9_]* ;      		// 标识符，由0到多个小写字母组成
NUMBER : [0-9]+('.'([0-9]+)?)? ;        // 数字
STRING : '"' ('\\"'|.)*? '"'  			// 字符串
	   | '\'' ('\\"'|.)*? '\'' ; 		// 单引号字符串
arrExpr: '{' params '}';

AND : '&&' ;
OR  : '||' ;
ADD : '+' ;
SUB : '-' ;
MUL : '*' ;
DIV : '/' ;
GT  : '>' ;
GE  : '>=' ;
LT  : '<' ;
LE  : '<=' ;
EQUAL  : '==' ;
NE  : '!=' ;
CT  : '包含' ;
NCT : '不包含' ;
MO  : '属于' ;
NMO : '不属于' ;
