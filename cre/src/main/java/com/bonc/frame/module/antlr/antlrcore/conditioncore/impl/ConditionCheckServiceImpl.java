package com.bonc.frame.module.antlr.antlrcore.conditioncore.impl;


import com.bonc.frame.module.antlr.exception.MyDefaultErrorStrategy;
import com.bonc.frame.module.antlr.listener.LogErrorListener;
import com.bonc.frame.module.antlr.service.AntlrService;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class ConditionCheckServiceImpl implements AntlrService {

    private static final String conditionStart = "condition ";

    @Override
    public String validate(String str) {
        ANTLRInputStream input = new ANTLRInputStream(conditionStart + str);
        ConditionLexer lexer = new ConditionLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ConditionParser parser = new ConditionParser(tokens);
        parser.addErrorListener(new LogErrorListener());
        parser.setErrorHandler(new MyDefaultErrorStrategy());
        ParseTree tree = parser.exprs();
        String msg = LogErrorListener.getLogErrorSession().get();
        LogErrorListener.getLogErrorSession().remove();
        return msg;
    }


}
