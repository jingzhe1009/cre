package com.bonc.frame.module.db;

import com.bonc.frame.applicationrunner.StaticDataLoader;
import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.staticdata.StaticDataVo;
import com.bonc.frame.module.db.operator.hbase.HbaseConfig;
import com.bonc.frame.module.db.operator.hbase.HbaseHelper;
import com.bonc.framework.util.FrameLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

/**
 * <p>Description: 获取数据库基本信息的工具类</p>
 *
 * @author qxl
 * @date 2016年7月22日 下午1:00:34
 */
@Component
public class DbInfoUtil {

    @Autowired
    private StaticDataLoader staticDataLoader;

    /**
     * 根据数据库连接的格式串、ip、port、serverName生成数据库连接URL
     *
     * @return
     */
    public String createUrl(DataSource dbInfo) {
        StaticDataVo dbTypeInfoVo = staticDataLoader.getDatasourceCodeVo(dbInfo.getDbType().toString());
        //jdbc:oracle:thin:@${ip}:${port}/${serviceName}
        String url = dbTypeInfoVo.getRemarks();
        url = url.replace("${ip}", dbInfo.getDbIp());
        url = url.replace("${port}", dbInfo.getDbPort().toString());
        url = url.replace("${serviceName}", dbInfo.getDbServiceName());
        //System.out.println("url:"+url);
        return url;
    }

    /**
     * 根据数据库的连接参数，获取指定表的基本信息：字段名、字段类型、字段注释--重载方法
     *
     * @return
     * @throws Exception
     */
    public List<Map<String, String>> getTableColumnsInfo(DataSource dbInfo, String table) throws Exception {
        StaticDataVo dbTypeInfoVo = staticDataLoader.getDatasourceCodeVo(dbInfo.getDbType().toString());
        return getTableColumnsInfo(dbTypeInfoVo.getRemarks1(), createUrl(dbInfo), dbInfo.getDbUsername(), dbInfo.getDbPassword(), table);
    }

    /**
     * 根据数据库的连接参数，获取指定表的基本信息：字段名、字段类型、字段注释
     *
     * @param driver 数据库连接驱动
     * @param url    数据库连接url
     * @param user   数据库登陆用户名
     * @param pwd    数据库登陆密码
     * @param table  表名
     * @return Map集合
     * @throws Exception
     */
    public List<Map<String, String>> getTableColumnsInfo(String driver, String url, String user, String pwd, String table) throws Exception {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();

        Connection conn = null;
        DatabaseMetaData dbmd = null;
        try {
            conn = getConnections(driver, url, user, pwd);
            dbmd = conn.getMetaData();
            ResultSet resultSet = dbmd.getTables(null, "%", table, new String[]{"TABLE"});

            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                if (tableName.equals(table)) {
                    ResultSet rs = conn.getMetaData().getColumns(null, getSchema(conn), tableName, "%");
                    while (rs.next()) {
//		    			System.out.println("字段名："+rs.getString("COLUMN_NAME")+"--字段注释："+rs.getString("REMARKS")+"--字段数据类型："+rs.getString("TYPE_NAME"));
                        Map<String, String> map = new HashMap<String, String>();
                        String colName = rs.getString("COLUMN_NAME");
                        map.put("code", colName);

                        String remarks = rs.getString("REMARKS");
                        if (remarks == null || remarks.equals("") || remarks.length() > 32) {
                            remarks = colName;
                        }
                        map.put("name", remarks);

                        String dbType = rs.getString("TYPE_NAME");
                        map.put("dbType", dbType);

                        map.put("valueType", changeDbType(dbType));
                        result.add(map);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 根据数据库中的数据类型，转成相应的java类型
     *
     * @param dbType 数据库中的字段类型
     * @return 相应的java中的字段类型
     */
    public static String changeDbType(String dbType) {
        dbType = dbType.toUpperCase();
        switch (dbType) {
            case "VARCHAR":
            case "VARCHAR2":
            case "CHAR":
                return "1";
            case "NUMBER":
            case "DECIMAL":
                return "4";
            case "INT":
            case "SMALLINT":
            case "INTEGER":
                return "2";
            case "BIGINT":
                return "7";
            case "DATETIME":
            case "TIMESTAMP":
            case "DATE":
                return "8";
            case "TEXT":
            case "CLOB":
            case "BLOB":
                return "10";
            default:
                return "1";
        }
    }

    /**
     * 测试数据库能否成功连接
     *
     * @param dbInfo
     * @return
     * @throws Exception 连接异常信息
     */
    public boolean testConnection(DataSource dbInfo) throws Exception {
        StaticDataVo dbTypeInfoVo = staticDataLoader.getDatasourceCodeVo(dbInfo.getDbType().toString());
        if ("8".equals(dbInfo.getDbType())) {
            final HbaseConfig hbaseConfig = HbaseConfig.builder()
                    .host(dbInfo.getDbIp())
                    .port(String.valueOf(dbInfo.getDbPort()))
                    .poolSize(dbInfo.getMaxConnect())
                    .build();
            try (HbaseHelper hbaseHelper = new HbaseHelper(hbaseConfig)) {
                return hbaseHelper.testConnecttion();
            }
        } else {
            Connection conn = null;
            try {
                conn = getConnections(dbTypeInfoVo.getRemarks1(), createUrl(dbInfo), dbInfo.getDbUsername(), dbInfo.getDbPassword());
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
            if (conn == null) {
                return false;
            } else {
                conn.close();
                return true;
            }
        }
    }

    /**
     * 获取数据库连接
     *
     * @param driver
     * @param url
     * @param user
     * @param pwd
     * @return
     * @throws Exception
     */
    public static Connection getConnections(String driver, String url, String user, String pwd) throws Exception {
        Connection conn = null;
        try {
            Properties props = new Properties();
            props.put("remarksReporting", "true");
            props.put("user", user);
            props.put("password", pwd);
            Class.forName(driver);
            conn = DriverManager.getConnection(url, props);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return conn;
    }

    //其他数据库不需要这个方法 oracle和db2需要
    private static String getSchema(Connection conn) throws Exception {
        String schema;
        schema = conn.getMetaData().getUserName();
        if ((schema == null) || (schema.length() == 0)) {
            throw new Exception("ORACLE数据库模式不允许为空");
        }
        return schema.toUpperCase().toString();

    }

    /**
     * 判断指定库中是否存在给定的表
     *
     * @param driver
     * @param url
     * @param user
     * @param pwd
     * @param table
     * @return
     * @throws Exception
     */
    public static boolean isExistTable(String driver, String url, String user, String pwd, String table) throws Exception {
        Connection conn = null;
        DatabaseMetaData dbmd = null;

        try {
            conn = getConnections(driver, url, user, pwd);
            dbmd = conn.getMetaData();
            ResultSet resultSet = dbmd.getTables(null, "%", table, new String[]{"TABLE"});

            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("数据库访问异常");
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 根据sql，创建表
     *
     * @param driver
     * @param url
     * @param user
     * @param pwd
     * @param sql
     * @return
     * @throws Exception
     */
    public static boolean createTable(String driver, String url, String user, String pwd, String sql) throws Exception {
        Connection conn = null;
        boolean result = false;
        try {
            conn = getConnections(driver, url, user, pwd);
            Statement state = conn.createStatement();

            state.execute(sql);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("创建表失败");
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 查询数据库中所有的表的重载方法
     *
     * @param dbInfo
     * @return List<Map < String, String>>
     * @throws Exception
     */
    public List<Map<String, String>> getAllTables(DataSource dbInfo) throws Exception {
        StaticDataVo dbTypeInfoVo = staticDataLoader.getDatasourceCodeVo(dbInfo.getDbType().toString());
        String url = createUrl(dbInfo);
        return getAllTables(dbTypeInfoVo.getRemarks1(), url, dbInfo.getDbUsername(), dbInfo.getDbPassword());
    }

    /**
     * 查询数据库中所有的表
     * @throws Exception
     */
    /**
     * @param driver
     * @param url
     * @param user
     * @param pwd
     * @return
     * @throws Exception
     */
    public static List<Map<String, String>> getAllTables(String driver, String url, String user, String pwd) throws Exception {
        Connection conn = null;
        try {
            conn = getConnections(driver, url, user, pwd);

            List<Map<String, String>> tables = new ArrayList<Map<String, String>>();

            DatabaseMetaData dbMetaData = conn.getMetaData();

            // 可为:"TABLE", "VIEW", "SYSTEM TABLE",
            // "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"
            String[] types = {"TABLE", "VIEW"};

            //2017-07-20增加getSchema,解决oracle查询全部用户表问题
            ResultSet tabs = dbMetaData.getTables(null, getSchema(conn), null, types);
            /*
             * 记录集的结构如下:
             * TABLE_CAT 	String => table catalog (may be null)
             * TABLE_SCHEM 	String => table schema (may be null)
             * TABLE_NAME 	String => table name
             * TABLE_TYPE 	String => table type.
             * REMARKS 		String => explanatory
             * comment on the table
             * TYPE_CAT 	String => the types catalog (may be null)
             * TYPE_SCHEM 	String => the types schema (may be null)
             * TYPE_NAME	String => type name (may be null)
             * SELF_REFERENCING_COL_NAME String =>name of the designated "identifier" column of a typed table (may be
             * null)
             * REF_GENERATION String => specifies how values in
             * SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER","DERIVED". (may be null)
             */
            Map<String, String> tableInfo = null;
            while (tabs.next()) {
                tableInfo = new HashMap<String, String>();
                tableInfo.put("tableName", tabs.getObject("TABLE_NAME").toString());
                tableInfo.put("tableType", tabs.getObject("TABLE_TYPE").toString());
                tableInfo.put("tableRemarks", tabs.getString("REMARKS"));
                tables.add(tableInfo);
            }
            //		for(Map map : tables){
            //			System.out.println(map);
            //		}
            //		System.out.println("total table："+tables.size());
            return tables;
        } catch (Exception e) {
            FrameLogUtil.error(DbInfoUtil.class, e.getMessage(), e);
            throw e;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 执行指定SQL
     *
     * @throws Exception
     */
    public static List<Map<String, String>> executeSQL(String driver, String url, String user, String pwd, String sql) throws Exception {
        Connection conn = null;
        try {
            conn = getConnections(driver, url, user, pwd);
            Statement st = conn.createStatement();
            st.execute(sql);
            ResultSet rs = st.executeQuery(sql);
            Map<String, String> map = null;
            List<Map<String, String>> result = new ArrayList<Map<String, String>>();
            while (rs.next()) {
                map = new HashMap<String, String>();
                String key = rs.getString(1);
                String value = rs.getString(2);
                if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
                    continue;
                }
                map.put("key", key);
                map.put("value", value);
                result.add(map);
            }
//			for(Map<String,String> a : result){
//				System.out.println(a);
//			}
            return result;
        } catch (Exception e) {
            FrameLogUtil.error(DbInfoUtil.class, e.getMessage(), e);
            throw e;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 执行指定SQL--重载的方法
     *
     * @throws Exception
     */
    public List<Map<String, String>> executeSQL(DataSource dbInfo, String sql) throws Exception {
        StaticDataVo dbTypeInfoVo = staticDataLoader.getDatasourceCodeVo(dbInfo.getDbType().toString());
        String url = createUrl(dbInfo);
        return executeSQL(dbTypeInfoVo.getRemarks1(), url, dbInfo.getDbUsername(), dbInfo.getDbPassword(), sql);
    }


    /**
     * 解析数据库连接url，获取dbName、ip、port、serverName
     *
     * @param url 数据库连接url
     * @return Map<String, String>
     */
    public static Map<String, String> parseUrl(String url) {
        System.out.println("url:" + url);
        Map<String, String> info = new HashMap<String, String>();
        int index = url.indexOf(':');
        int index2 = url.indexOf(':', index + 1);
        String dbName = url.substring(index + 1, index2);
        //System.out.println("dbName:"+dbName);
        info.put("dbName", dbName);//数据库名

        int i0 = url.indexOf("//");
        if (i0 == -1) {
            i0 = url.indexOf("@") + 1;
        } else {
            i0 += 2;
        }
        int i1 = url.indexOf(':', i0);
        String ip = url.substring(i0, i1);
        //System.out.println(ip);
        info.put("ip", ip);//ip

        String serverName = null;
        int i2 = url.indexOf('/', i1 + 1);
        if (i2 == -1) {
            i2 = url.indexOf(':', i1 + 1);//oracle库
            serverName = url.substring(i2 + 1);
        }
        if (i2 == -1) {
            //解析sqlserver的可能存在问题
            i2 = url.indexOf(';', i1 + 1);//sqlserver库
            int s1 = url.toUpperCase().indexOf("DATABASENAME");
            int s2 = url.indexOf(';', s1 + 2);
            serverName = url.substring(s1 + 2, s2);
        }
        String port = url.substring(i1 + 1, i2);
        //System.out.println(port);
        info.put("port", port);//port

        int i3 = url.indexOf('?');
        if (i3 == -1) {
            serverName = url.substring(i2 + 1);
        } else {
            serverName = url.substring(i2 + 1, i3);
        }
        //System.out.println(serverName);
        info.put("serverName", serverName);//serverName
        return info;
    }

    public static void main(String[] args) throws Exception {

        //这里是Oracle连接方法
		/*
		String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@172.16.13.64:1521:orcl";
		String user = "rule_hn";
		String pwd = "rule_hn";
		//String table = "FZ_USER_T";
		String table = "fz_orguser_t";
		*/

        //mysql

//		String driver = "com.mysql.jdbc.Driver";
//		String user = "root";
//		String pwd = "root";
//		String url = "jdbc:mysql://172.16.13.64:3306/test?useUnicode=true&characterEncoding=UTF-8";
//		String key="firstname";
//		String value="lastname";
//		String tablename = "users";

        String driver = "org.apache.hive.jdbc.HiveDriver";
        String user = "bdap";
        String pwd = "bonc1qaz2wsx2133";
        String url = "jdbc:hive2://133.160.94.218:10000/default";

//		String driver = "org.apache.hive.jdbc.HiveDriver";
//		String user = "bdap";
//		String pwd = "bonc1qaz2wsx";
//		String url = "jdbc:hive2://133.160.94.124:10000/default";

        Connection conn = getConnections(driver, url, user, pwd);
        System.out.println(conn);

        //GetColum(driver,url,user,pwd,key,value,tablename);

        //xclude
		/*
		String driver = "com.bonc.xcloud.jdbc.XCloudDriver";
		String user = "UT_UPLOAD";
		
		String pwd = "bonc123";
		
		String url = "jdbc:xcloud:@192.168.0.161:1803/jingzhunhua";
		
		String table = "A_MY_TEST";
*/

    }

}
