package com.bonc.frame.module.antlr.antlrcore.sqlcdt.impl;


import com.bonc.frame.module.antlr.exception.MyDefaultErrorStrategy;
import com.bonc.frame.module.antlr.listener.LogErrorListener;
import com.bonc.frame.module.antlr.service.AntlrService;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * @author qxl
 * @version 1.0
 * @date 2017年12月5日 上午9:46:04
 */
public class SqlCdtServiceImpl implements AntlrService {

    @Override
    public String validate(String str) {
        ANTLRInputStream input = new ANTLRInputStream(str);
        SqlCdtLexer lexer = new SqlCdtLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SqlCdtParser parser = new SqlCdtParser(tokens);
        parser.addErrorListener(new LogErrorListener());
        parser.setErrorHandler(new MyDefaultErrorStrategy());
        ParseTree tree = parser.expr();
        String msg = LogErrorListener.getLogErrorSession().get();
        LogErrorListener.getLogErrorSession().remove();
        return msg;
    }

}
