package com.bonc.frame.util;

/**
 * 项目中通用常量工具类
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月27日 下午5:35:35
 */
public class ConstantUtil {

    /**
     * 系统日志类型
     */
    public static final String SYS_LOG_TYPE_SYS = "0";
    public static final String SYS_LOG_TYPE_OPERATE = "1";

    /**
     * 系统操作类型
     */
    public static final String OPERATE_LOGIN = "登录";
    public static final String OPERATE_LOGOUT = "登出";

    /**
     * 创建接口
     */
    public static final String OPERATE_CREATE_API = "新建接口";
    public static final String OPERATE_UPDATE_API = "修改接口";
    public static final String OPERATE_DELETE_API = "删除接口";
    public static final String OPERATE_PUBLISH_API = "发布接口";

    public static final String OPERATE_CREATE_RULE_FOLDER = "新建场景";
    public static final String OPERATE_UPDATE_RULE_FOLDER = "修改场景";
    public static final String OPERATE_DELETE_RULE_FOLDER = "删除场景";

    public static final String OPERATE_CREATE_RULE = "新建模型";
    public static final String OPERATE_UPDATE_RULE = "修改模型";
    public static final String OPERATE_DELETE_RULE = "删除模型";
    public static final String OPERATE_PUBLISH_RULE = "发布模型";

    public static final String OPERATE_CREATE_PARAM = "新建参数";
    public static final String OPERATE_UPDATE_PARAM = "修改参数";
    public static final String OPERATE_DELETE_PARAM = "删除参数";
    public static final String OPERATE_PUBLISH_PARAM = "发布参数";

    public static final String OPERATE_CREATE_FUNC = "新建函数";
    public static final String OPERATE_UPDATE_FUNC = "修改函数";
    public static final String OPERATE_DELETE_FUNC = "删除函数";
    public static final String OPERATE_PUBLISH_FUNC = "发布函数";

    public static final String OPERATE_CREATE_KPI = "新建指标";
    public static final String OPERATE_UPDATE_KPI = "修改指标";
    public static final String OPERATE_DELETE_KPI = "删除指标";

    public static final String OPERATE_CREATE_RULE_SET_HRADER = "新建规则集基础信息";
    public static final String OPERATE_CREATE_RULE_SET_VERSION = "新建规则集";
    public static final String OPERATE_UPDATE_RULE_SET_HRADER = "修改规则集基础信息";
    public static final String OPERATE_UPDATE_RULE_SET_VERSION = "修改规则集";
    public static final String OPERATE_DELETE_RULE_SET_HRADERT = "删除规则集基础信息";
    public static final String OPERATE_DELETE_RULE_SET_VERSION = "删除规则集";
    public static final String OPERATE_ENABLE_RULE_SET_VERSION = "启用规则集";
    public static final String OPERATE_DISABLE_RULE_SET_VERSION = "停用规则集";
    public static final String OPERATE_PUBLISH_RULE_SET_VERSION = "发布规则集";

    public static final String OPERATE_CREATE_MATEDATA_TABLE = "新建数据源表";
    public static final String OPERATE_UPDATE_MATEDATA_TABLE = "修改数据源表";
    public static final String OPERATE_DELETE_MATEDATA_TABLE = "删除数据源表";
    public static final String OPERATE_CREATE_MATEDATA = "新建数据源";
    public static final String OPERATE_UPDATE_MATEDATA = "修改数据源";
    public static final String OPERATE_DELETE_MATEDATA = "删除数据源";
    public static final String OPERATE_CREATE_MATEDATA_TABLE_COLUMN = "新建数据源列";
    public static final String OPERATE_UPDATE_MATEDATA_TABLE_COLUMN = "修改数据源列";
    public static final String OPERATE_DELETE_MATEDATA_TABLE_COLUMN = "删除数据源列";

	/*public static final String OPERATE_CREATE_COMMON_API = "新建公共接口";
	public static final String OPERATE_UPDATE_COMMON_API = "修改公共接口";
	public static final String OPERATE_DELETE_COMMON_API = "删除公共接口";

	public static final String OPERATE_CREATE_COMMON_PARAM = "新建公共参数";
	public static final String OPERATE_UPDATE_COMMON_PARAM = "修改公共参数";
	public static final String OPERATE_DELET_COMMONE_PARAM = "删除公共参数";*/

    public static final String VARIABLE_ENTITY_TYPE = "3";

    /**
     * 规则状态-准备/未开始
     */
    public static final String RULE_STATUS_READY = "0";
    /**
     * 规则状态-执行中 启用
     */
    public static final String RULE_STATUS_RUNNING = "1";
    /**
     * 规则状态-执行完成 停用
     */
    public static final String RULE_STATUS_OVER = "2";


    /**
     * 模型库中,是是头信息
     */
    public static final String RULE_IS_HEADER = "1";
    /**
     * 模型库中,不是头信息,是版本信息
     */
    public static final String RULE_IS_VERSION = "0";


    /**
     * 规则状态-异常
     */
    public static final String RULE_STATUS_EXCEPTION = "-1";

    /**
     * 是否删除标识 删除
     */
    public static final String IS_DEL_DEL = "1";
    /**
     * 是否删除标识 未删除
     */
    public static final String IS_DEL_NDEL = "0";

    /**
     * 变量种类-输入变量
     */
    public static final String VARIABLE_KIND_INPUT = "K1";
    /**
     * 变量种类-输出变量
     */
    public static final String VARIABLE_KIND_OUTPUT = "K2";
    /**
     * 变量种类-中间变量
     */
    public static final String VARIABLE_KIND_TEMP = "K3";
    /**
     * 变量种类-系统常量
     */
    public static final String VARIABLE_KIND_SYS_CONSTANT = "K4";

    /**
     * 变量种类-公共参数
     */
    public static final String VARIABLE_KIND_PUB = "K9";

    /**
     * 指标
     */
    public static final String KPI = "KPI";

    /**
     * 文件夹菜单的session key
     */
    public static final String SESSION_KEY_FOLDERMENU = "folderMeun";

    /**
     * 规则是否更新key
     */
    public static final String RULE_KEY_PRE = "ruleOper_";

    /**
     * 是否为公共资源
     *
     * @see com.bonc.frame.entity.variable.Variable
     * @see com.bonc.frame.entity.api.ApiConf
     */
    public static final String PUBLIC = "1";

    public static final String TRIAL_SUCCESS = "1";
    public static final String TRIAL_FAILED = "0";
    public static final String TRIAL_INIT = "-1";
}
