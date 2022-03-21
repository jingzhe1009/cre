package com.bonc.frame.module.antlr.antlrcore.actioncore.impl;


import com.bonc.frame.module.antlr.exception.MyDefaultErrorStrategy;
import com.bonc.frame.module.antlr.listener.LogErrorListener;
import com.bonc.frame.module.antlr.service.AntlrService;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class ActionCheckServiceImpl implements AntlrService {

    private static final String actionStart = "action ";

    @Override
    public String validate(String str) {
        ANTLRInputStream input = new ANTLRInputStream(actionStart + str);
        ActionLexer lexer = new ActionLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ActionParser parser = new ActionParser(tokens);
        parser.addErrorListener(new LogErrorListener());
        parser.setErrorHandler(new MyDefaultErrorStrategy());
        ParseTree tree = parser.exprs();
        String msg = LogErrorListener.getLogErrorSession().get();
        LogErrorListener.getLogErrorSession().remove();
        return msg;
    }

}
