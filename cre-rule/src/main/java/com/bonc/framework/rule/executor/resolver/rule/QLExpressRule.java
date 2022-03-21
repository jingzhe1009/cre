package com.bonc.framework.rule.executor.resolver.rule;

import com.bonc.framework.rule.executor.context.IQLExpressContext;
import com.bonc.framework.rule.executor.context.impl.QLExpressContext;
import com.ql.util.express.ExpressRunner;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QLExpressRule extends AbstractRule {
    //	private static QLExpressRule instance;
//	public static QLExpressRule getInstance(){
//		if(instance == null){
//			instance = new QLExpressRule();
//		}
//		return instance;
//	}
    private ExpressRunner runner;

    @Override
    public Object execute(String express, IQLExpressContext<?, ?> context) throws Exception {
        QLExpressContext aContext = null;
        if (context instanceof QLExpressContext || context == null) {
            aContext = (QLExpressContext) context;
        } else {
            throw new Exception("context is error");
        }
        Object r = null;
        runner = getRunner();
        Pattern p = Pattern.compile("\\[(.*?)\\]");//(.*?)]
        Matcher m = p.matcher(express);
        while (m.find()) {
            String repaceStr = m.group(0);//[xxx]
            String replace = m.group(1);
            express = express.replace(repaceStr, replace);
        }
        r = runner.execute(express, aContext, null, true, false);
        return r;
    }


    public ExpressRunner getRunner() {
        if (runner == null) {
            runner = new ExpressRunner(true, false);
        }
        return runner;
    }

    public void setRunner(ExpressRunner runner) {
        this.runner = runner;
    }

    public void addFunctionOfClassMethod(String name, String aClassName, String aFunctionName, String[] aParameterTypes, String errorInfo) throws Exception {
        runner = getRunner();
        runner.addFunctionOfClassMethod(name, aClassName, aFunctionName, aParameterTypes, errorInfo);
    }

    public void addFunctionOfClassMethod(IQLExpressContext<?, ?> context) throws Exception {
        String name = (String) context.get("name");
        String aClassName = (String) context.get("aClassName");
        String aFunctionName = (String) context.get("aFunctionName");
        String[] aParameterTypes = (String[]) context.get("aParameterTypes");
        String errorInfo = (String) context.get("errorInfo");
        runner = getRunner();
        runner.addFunctionOfClassMethod(name, aClassName, aFunctionName, aParameterTypes, errorInfo);
    }


    @Override
    public IQLExpressContext<?, ?> createContext(Map<String, Object> map) {
        IQLExpressContext<?, ?> context = new QLExpressContext(map);
        return context;
    }

}
