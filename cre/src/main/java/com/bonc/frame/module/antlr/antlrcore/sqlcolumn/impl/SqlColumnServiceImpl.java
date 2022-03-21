package com.bonc.frame.module.antlr.antlrcore.sqlcolumn.impl;


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
 * @date 2016年12月23日 下午5:24:21
 */
public class SqlColumnServiceImpl implements AntlrService {

    private Map<String, String> funCallMap;

    public SqlColumnServiceImpl() {
        funCallMap = new HashMap<String, String>();
    }

    public SqlColumnServiceImpl(Map<String, String> funCallMap) {
        this.funCallMap = funCallMap;
    }

    private static final String sqlColumnStart = "check ";

    @Override
    public String validate(String str) {
        ANTLRInputStream input = new ANTLRInputStream(sqlColumnStart + str);
        SqlColumnLexer lexer = new SqlColumnLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SqlColumnParser parser = new SqlColumnParser(tokens);
        parser.addErrorListener(new LogErrorListener());
        parser.setErrorHandler(new MyDefaultErrorStrategy());
        ParseTree tree = parser.exprs();
        String msg = LogErrorListener.getLogErrorSession().get();
        LogErrorListener.getLogErrorSession().remove();
        if (msg != null && !msg.isEmpty()) {
            return msg;
        }
        MySqlColumnVisitor mv = new MySqlColumnVisitor(funCallMap);
        String result = mv.visit(tree);
        if (result != null && result.contains("###ERROR###:")) {
            String funName = result.substring(result.indexOf("###ERROR###:") + 12, result.indexOf("###END###"));
            return "The function name [" + funName + "] is illegal.";
        }
        return msg;
    }

}
