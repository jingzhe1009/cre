package com.bonc.frame.module.antlr.antlrcore.sqlaction.impl;


import com.bonc.frame.module.antlr.exception.MyDefaultErrorStrategy;
import com.bonc.frame.module.antlr.listener.LogErrorListener;
import com.bonc.frame.module.antlr.service.AntlrService;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * @author qxl
 * @version 1.0
 * @date 2017年12月5日 上午10:24:31
 */
public class SqlActionServiceImpl implements AntlrService {

    private static final String actionStart = "action ";

    @Override
    public String validate(String str) {
        ANTLRInputStream input = new ANTLRInputStream(actionStart + str);
        SqlActionLexer lexer = new SqlActionLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SqlActionParser parser = new SqlActionParser(tokens);
        parser.addErrorListener(new LogErrorListener());
        parser.setErrorHandler(new MyDefaultErrorStrategy());
        ParseTree tree = parser.exprs();
        String msg = LogErrorListener.getLogErrorSession().get();
        LogErrorListener.getLogErrorSession().remove();
        return msg;
    }
}
