grammar TableRelation;                            // 文法的名字为TableRelation

//表的关联关系校验文件

// 以下以小写字母开头的文法表示为语法元素
// 由大写字母开头的文法表示为词法元素
// 词法元素的表示类似于正则表示式
// 语法元素的表示类似于BNF

exprs : calcExpr ;                           // 或calc表达式

calcExpr: 'check' expr ;                    // 以calc命令开头，后面是一个计算表达式

// expr可能由多个产生式
// 在前面的产生式优先于在后面的产生式
// 这样来解决优先级的问题

expr: expr op=(AND | OR) expr					// 并且,或者
	| expr op=(GT|GE|LT|LE|EQUAL|NE) expr		//> >= < <= == !=
    | factor                            		// 一个计算因子——可做为+-*/的操作数据的东西
	;

factor: '[' id=ID ']' 					#variable 	// 计算因子可以是自定义的参数
	;


WS : [ \t\n\r]+ -> skip ;                // 空白， 后面的->skip表示antlr4在分析语言的文本时，符合这个规则的词法将被无视
ID : [a-zA-Z_][a-zA-Z0-9_.]* ;      		// 标识符，由0到多个小写字母组成
NUMBER : [0-9]+('.'([0-9]+)?)? ;        // 数字

AND : '&&' ;
OR  : '||' ;
GT  : '>' ;
GE  : '>=' ;
LT  : '<' ;
LE  : '<=' ;
EQUAL  : '=' ;
NE  : '!=' ;
