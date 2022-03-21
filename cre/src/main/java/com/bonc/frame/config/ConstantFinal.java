package com.bonc.frame.config;
/**
 *@author	作者：limf	
 *@date	创建时间：2018年2月8日 上午11:44:49
 *@version	版本： 1.0
 *说明：常量类
 */
public class ConstantFinal {

	 //session key
	 public static final String SESSION_KEY = "user";

    // FIXME: 为业务团队对接设置的临时方案，需要为业务团队提供超级用户的用户名、密码，
    // FIXME: 使用realUserAccount接收真实用户工号，记日志时用session中的realUser（用户名$真实用户工号）记录日志
    public static final String REAL_USER = "realUser";

	 public static final String SESSION_KEY_USER_ID = "session_key_user_id";
	 
	 //登陸URL
	 public static final String TOLOGIN_URL = "/tologin";

	 //用户名
	 public static final String SESSION_KEY_USER_NAME = "session_key_user_name";

	 // ip
	 public static final String SESSION_KEY_USER_IP = "session_key_user_ip";

	 // 用户主体id
	 public static final String SESSION_KEY_SUBJECT_ID = "session_key_subject_id";

	 // session中授权主体的更新时间戳
	 public static final String SESSION_KEY_SUBJECT_TIMESTAMP = "session_key_subject_timestamp";

	 // 定时删除数据库中的日志
	 public static final String DIS_ENABLE = "false";
	 public static final String CRE_TASK_DELETELOG_DATABASE_CRON_DEFAULT = "0 0 0 * * ?";
	 public static final String CRE_TASK_DELETELOG_DATABASE_DAY_DEFAULT = "30";
	 public static final String CRE_TASK_DELETERYLETASKLOG_DATABASE_DAY_DEFAULT = "0";


}
