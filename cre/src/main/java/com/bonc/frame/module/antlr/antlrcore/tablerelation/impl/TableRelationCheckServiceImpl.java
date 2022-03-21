package com.bonc.frame.module.antlr.antlrcore.tablerelation.impl;


import com.bonc.frame.module.antlr.exception.MyDefaultErrorStrategy;
import com.bonc.frame.module.antlr.listener.LogErrorListener;
import com.bonc.frame.module.antlr.service.AntlrService;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年12月19日 上午9:24:48
 */
public class TableRelationCheckServiceImpl implements AntlrService {

    private static final String conditionStart = "check ";

    @Override
    public String validate(String str) {
        ANTLRInputStream input = new ANTLRInputStream(conditionStart + str);
        TableRelationLexer lexer = new TableRelationLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TableRelationParser parser = new TableRelationParser(tokens);
        parser.addErrorListener(new LogErrorListener());
        parser.setErrorHandler(new MyDefaultErrorStrategy());
        ParseTree tree = parser.exprs();
        String msg = LogErrorListener.getLogErrorSession().get();
        LogErrorListener.getLogErrorSession().remove();
        return msg;
    }

}
