package com.bonc.frame.module.antlr.util;


import com.bonc.frame.module.antlr.antlrcore.sqlaction.impl.SqlActionServiceImpl;
import com.bonc.frame.module.antlr.antlrcore.sqlcdt.impl.SqlCdtServiceImpl;
import com.bonc.frame.module.antlr.antlrcore.sqlcolumn.impl.SqlColumnServiceImpl;
import com.bonc.frame.module.antlr.antlrcore.sqlcondition.impl.SqlConditionServiceImpl;
import com.bonc.frame.module.antlr.antlrcore.tablerelation.impl.TableRelationCheckServiceImpl;
import com.bonc.frame.module.antlr.service.AntlrService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * SQL相关的语法校验与解析转换 工具类
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年12月19日 上午9:21:49
 */
public class SqlAntlrUtil {

    private static Log log = LogFactory.getLog(SqlAntlrUtil.class);

    private static Map<String, String> funCallMap;

    private static boolean isInit = false;

    private static synchronized void init() {
        if (!isInit) {
            funCallMap = new HashMap<String, String>();
			/*RuleMapper mapper = new RuleMapper();
			List<RuleFunction> funList = null;
			try {
				funList = mapper.selectFunction();
			} catch (SQLException e) {
				log.error("init antlr function fail.",e);
			}
			for(RuleFunction fun : funList){
				funCallMap.put(fun.getFunctionMethodName(), fun.getFunctionClassPath());
			}*/
            isInit = true;
        }
    }

    /**
     * 校验sql中表与表的关系
     *
     * @param relation
     * @return
     */
    public static String validateTableRelation(String relation) {
        AntlrService antlrService = new TableRelationCheckServiceImpl();
        String msg = antlrService.validate(relation);
        return msg;
    }

    /**
     * 校验sql语句中的where条件部分
     */
    public static String validateSqlCondition(String content) {
        AntlrService antlrService = new SqlConditionServiceImpl();
        String msg = antlrService.validate(content);
        return msg;
    }

    /**
     * 校验sql语句中的select部分
     */
    public static String validateSqlColumn(String content) {
        init();
        AntlrService antlrService = new SqlColumnServiceImpl(funCallMap);
        String msg = antlrService.validate(content);
        return msg;
    }


    /**
     * 获取函数
     *
     * @return
     */
    public static Map<String, String> getFunMap() {
        if (funCallMap == null) {
            init();
        }
        return funCallMap;
    }


    /**
     * 校验sql语句中的条件.包含关联关系条件、简单where条件
     *
     * @param content 要校验的内容
     * @return
     * @Date 2017-12-05
     */
    public static String validateSqlCdt(String content) {
        AntlrService antlrService = new SqlCdtServiceImpl();
        String msg = antlrService.validate(content);
        return msg;
    }

    /**
     * 校验sql语句中的动作部分.基于之前的动作校验语法
     *
     * @param content 要校验的内容
     * @return
     * @Date 2017-12-05
     */
    public static String validateSqlAction(String content) {
        AntlrService antlrService = new SqlActionServiceImpl();
        String msg = antlrService.validate(content);
        return msg;
    }
}
