/*
package com.bonc.frame.module.antlr.util;

import com.bonc.frame.module.antlr.antlrcore.actioncore.impl.ActionCheckServiceImpl;
import com.bonc.frame.module.antlr.antlrcore.actioncore.impl.ActionParser;
import com.bonc.frame.module.antlr.antlrcore.actioncore.impl.MyActionVisitor;
import com.bonc.frame.module.antlr.antlrcore.conditioncore.impl.*;
import com.bonc.frame.module.antlr.exception.MyDefaultErrorStrategy;
import com.bonc.frame.module.antlr.listener.LogErrorListener;
import com.bonc.frame.module.antlr.service.AntlrService;
import com.bonc.frame.util.ResponseResult;
import com.bonc.framework.util.FrameLogUtil;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;
import java.util.Map;

*/
/**
 * @author qxl
 * @date 2016年11月1日 下午2:01:58
 * @version 1.0.0
 * <p>
 * 校验规则的条件部分
 * @param ruleContent
 * @return 校验规则的动作部分
 * @param ruleContent
 * @return 遍历语法树，转换规则的动作部分
 * 将[a]转成m["a"]的形式
 * 将函数名转成  包名.类名.方法名  形式
 * @param funList
 * @param actionStr
 * @return 遍历语法树，转换规则的条件部分
 * 将[a]转成m["a"]的形式
 * 将函数名转成  包名.类名.方法名  形式
 * @param funList
 * @param actionStr
 * @return 遍历语法树，转换决策树/规则流 中的条件部分
 * 将[a]转成a的形式
 * 将函数名转成  包名.类名.方法名  形式
 * @param funList
 * @param actionStr
 * @return
 *//*

public class AntlrUtil {
	
	*/
/**
 * 校验规则的条件部分
 * @param ruleContent
 * @return
 *//*

	public static String validateCondition(String ruleContent){
		AntlrService antlrService = new ConditionCheckServiceImpl();
		String msg = antlrService.validate(ruleContent);
		return msg;
	}
	
	*/
/**
 * 校验规则的动作部分
 * @param ruleContent
 * @return
 *//*

	public static String validateAction(String ruleContent){
		if(ruleContent==null ||ruleContent.isEmpty()){
			return null;
		}
		AntlrService antlrService = new ActionCheckServiceImpl();
		String msg = antlrService.validate(ruleContent);
		return msg;
	}
	
	*/
/**
 * 遍历语法树，转换规则的动作部分
 * 	将[a]转成m["a"]的形式
 * 	将函数名转成  包名.类名.方法名  形式
 * @param funList
 * @param actionStr
 * @return
 *//*

	public static ResponseResult parseAction(List<RuleFunction> funList, String actionStr){
		final String startStr = "action ";
		Map<String,String> funMap = new HashMap<String,String>();
		for(RuleFunction fun : funList){
			funMap.put(fun.getFunctionMethodName(), fun.getFunctionClassPath());
		}
		ANTLRInputStream input = new ANTLRInputStream(startStr+actionStr);
		ActionLexer lexer = new ActionLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ActionParser parser = new ActionParser(tokens);
		parser.addErrorListener(new LogErrorListener());
		parser.setErrorHandler(new MyDefaultErrorStrategy());
		ParseTree tree = parser.exprs();
		String msg = LogErrorListener.getLogErrorSession().get();
		LogErrorListener.getLogErrorSession().remove();
        if(msg != null){
        	FrameLogUtil.error(AntlrUtil.class, "the rule is wrong!");
        	return ResultUtil.createFailInfo("the rule is wrong!");
        }
        MyActionVisitor mv = new MyActionVisitor(funMap);
        String result = mv.visit(tree);
        if(result==null || result.isEmpty()){
        	return ResultUtil.createFailInfo("Result is null!");
        }
        return ResultUtil.createSuccessInfo("success",result);
	}
	
	*/
/**
 * 遍历语法树，转换规则的条件部分
 * 	将[a]转成m["a"]的形式
 * 	将函数名转成  包名.类名.方法名  形式
 * @param funList
 * @param actionStr
 * @return
 *//*

	public static ResponseResult parseCondition(List<RuleFunction> funList,String actionStr){
		final String startStr = "condition ";
		Map<String,String> funMap = new HashMap<String,String>();
		for(RuleFunction fun : funList){
			funMap.put(fun.getFunctionMethodName(), fun.getFunctionClassPath());
		}
		ANTLRInputStream input = new ANTLRInputStream(startStr+actionStr);
		ConditionLexer lexer = new ConditionLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ConditionParser parser = new ConditionParser(tokens);
		parser.addErrorListener(new LogErrorListener());
		parser.setErrorHandler(new MyDefaultErrorStrategy());
		ParseTree tree = parser.exprs();
		String msg = LogErrorListener.getLogErrorSession().get();
		LogErrorListener.getLogErrorSession().remove();
        if(msg != null){
        	FrameLogUtil.error(AntlrUtil.class, "the rule is wrong!");
        	return ResultUtil.createFailInfo("the rule is wrong!");
        }
        MyConditionVisitor mv = new MyConditionVisitor(funMap);
        String result = mv.visit(tree);
        if(result==null || result.isEmpty()){
        	return ResultUtil.createFailInfo("Result is null!");
        }
        return ResultUtil.createSuccessInfo("success",result);
	}
	
	*/
/**
 * 遍历语法树，转换决策树/规则流 中的条件部分
 * 	将[a]转成a的形式
 * 	将函数名转成  包名.类名.方法名  形式
 * @param funList
 * @param actionStr
 * @return
 *//*

	public static ResponseResult parseFlowCondition(List<RuleFunction> funList,String actionStr){
		final String startStr = "condition ";
		Map<String,String> funMap = new HashMap<String,String>();
		for(RuleFunction fun : funList){
			funMap.put(fun.getFunctionMethodName(), fun.getFunctionClassPath());
		}
		ANTLRInputStream input = new ANTLRInputStream(startStr+actionStr);
		ConditionLexer lexer = new ConditionLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ConditionParser parser = new ConditionParser(tokens);
		parser.addErrorListener(new LogErrorListener());
		parser.setErrorHandler(new MyDefaultErrorStrategy());
		ParseTree tree = parser.exprs();
		String msg = LogErrorListener.getLogErrorSession().get();
		LogErrorListener.getLogErrorSession().remove();
        if(msg != null){
        	FrameLogUtil.error(AntlrUtil.class, "the rule is wrong!");
        	return ResultUtil.createFailInfo("the rule is wrong!");
        }
        MyFlowConditionVisitor mv = new MyFlowConditionVisitor(funMap);
        String result = mv.visit(tree);
        if(result==null || result.isEmpty()){
        	return ResultUtil.createFailInfo("Result is null!");
        }
        return ResultUtil.createSuccessInfo("success",result);
	}
	
	
	public static void main(String[] args) {
		String str = "[code1] &&  abs([code1])  ||  [code1] 包含 {\"MYSQL\"}";
		parseCondition(null,str);
	}
	
}
*/
