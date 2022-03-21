package com.bonc.frame.module.antlr.antlrcore.sqlcondition.impl;


import com.bonc.frame.module.antlr.exception.MyDefaultErrorStrategy;
import com.bonc.frame.module.antlr.listener.LogErrorListener;
import com.bonc.frame.module.antlr.service.AntlrService;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年12月23日 下午2:47:58
 */
public class SqlConditionServiceImpl implements AntlrService {

    private Map<String, String> funCallMap;

    public SqlConditionServiceImpl() {
        funCallMap = new HashMap<String, String>();
    }

    public SqlConditionServiceImpl(Map<String, String> funCallMap) {
        this.funCallMap = funCallMap;
    }

    @Override
    public String validate(String str) {
        ANTLRInputStream input = new ANTLRInputStream(str);
        SqlConditionLexer lexer = new SqlConditionLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SqlConditionParser parser = new SqlConditionParser(tokens);
        parser.addErrorListener(new LogErrorListener());
        parser.setErrorHandler(new MyDefaultErrorStrategy());
        ParseTree tree = parser.expr();
        String msg = LogErrorListener.getLogErrorSession().get();
        LogErrorListener.getLogErrorSession().remove();
        if (msg != null && !msg.isEmpty()) {
            return msg;
        }
        MySqlConditionVisitor mv = new MySqlConditionVisitor(funCallMap);
        String result = mv.visit(tree);
        if (result != null && result.contains("###ERROR###:")) {
            String funName = result.substring(result.indexOf("###ERROR###:") + 12, result.indexOf("###END###"));
            return "The function name [" + funName + "] is illegal.";
        }
        return msg;
    }

}
