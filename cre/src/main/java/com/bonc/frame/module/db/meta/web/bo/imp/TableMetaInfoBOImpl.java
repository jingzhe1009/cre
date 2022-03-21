package com.bonc.frame.module.db.meta.web.bo.imp;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.module.db.meta.core.model.ScanTask;
import com.bonc.frame.module.db.meta.core.model.ScanTaskLog;
import com.bonc.frame.module.db.meta.core.model.StructInfo;
import com.bonc.frame.module.db.meta.core.model.TableInfo;
import com.bonc.frame.module.db.meta.web.bo.TableMetaInfoBO;
import com.bonc.frame.module.db.meta.web.vo.TableMetaInfoWapper;
import com.bonc.frame.util.SpringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.List;


/** 
 * @author 作者: jxw 
 * @date 创建时间: 2016年12月8日 上午9:58:41 
 * @version 版本: 1.0 
*/
public class TableMetaInfoBOImpl implements TableMetaInfoBO {
	
	private Log log = LogFactory.getLog(this.getClass());

    private ScanTask st;

	private DaoHelper daoHelper;

	private final String _METADATA_TABLE_COLUMN_PREFIX = "com.bonc.frame.mapper.metadata.MetaDataTableColumnMapper.";

    public TableMetaInfoBOImpl(){
		this.daoHelper = (DaoHelper) SpringUtils.getBean("daoHelper");
    }
    
	@Override
	public void updateScanTask(ScanTask scanTask) {
    	daoHelper.update(_METADATA_TABLE_COLUMN_PREFIX + "updateScanTask", scanTask);
	}

	@Override
	public void insertScanTaskLog(ScanTaskLog scanTaskLog) {
//		System.out.println(scanTaskLog);
		daoHelper.insert(_METADATA_TABLE_COLUMN_PREFIX + "insertScanTaskLog", scanTaskLog);
	}

	// TODO: 增加事务
	@Override
	public void updateTableStructMetaInfos(List<TableMetaInfoWapper> needInsertTables,
										   List<TableMetaInfoWapper> needUpdateTables) throws Exception {
		int number = 1;
		for(TableMetaInfoWapper insertTable:needInsertTables){//需要插入的数据
			TableInfo it = insertTable.getTableInfo();
			it.setCreateDate(new Date(System.currentTimeMillis()+(number++)*1000));
			it.setPackageId(this.st.getPackageId());
			it.setCreatePerson("system_scan");
			daoHelper.insert(_METADATA_TABLE_COLUMN_PREFIX + "insertTableInfoSelective", it);
			for(StructInfo s:it.getStructs()){
				s.setCreateDate(new Date(System.currentTimeMillis()+(number++)*1000));
				s.setCreatePerson("system_scan");
				daoHelper.insert(_METADATA_TABLE_COLUMN_PREFIX + "insertColumnInfoSelective", s);
			}
		}
		for(TableMetaInfoWapper updateTable:needUpdateTables){//需要更新的数据
			TableInfo ut = updateTable.getTableInfo();
			if(updateTable.isUpdateTableInfo()){
				ut.setUpdateDate(new Date(System.currentTimeMillis()+(number++)*1000));
				ut.setUpdatePerson("system_scan");
				daoHelper.update(_METADATA_TABLE_COLUMN_PREFIX + "updateTableInfoSelective", ut);
			}
			for(StructInfo us:updateTable.getNeedUpdateStructs()){//需要更新的结构
				us.setUpdateDate(new Date(System.currentTimeMillis()+(number++)*1000));
				us.setUpdatePerson("system_scan");
				daoHelper.update(_METADATA_TABLE_COLUMN_PREFIX + "updateColumnInfoSelective", us);
			}
			for(StructInfo is:updateTable.getNeedInsertStructs()){//需要插入的结构
				is.setCreateDate(new Date(System.currentTimeMillis()+(number++)*1000));
				is.setCreatePerson("system_scan");
				daoHelper.insert(_METADATA_TABLE_COLUMN_PREFIX + "insertColumnInfoSelective", is);
			}
			for(StructInfo ds:updateTable.getNeedDeleteStructs()){//需要删除的结构
				daoHelper.delete(_METADATA_TABLE_COLUMN_PREFIX + "deleteColumnInfo", ds);
			}
		}

	}

	@Override
	public TableInfo selectTableStructMetaInfo(String tableId, String packageId) {
		TableInfo ti = new TableInfo();
		ti.setTableId(tableId);
		ti.setPackageId(this.st.getPackageId());
		return (TableInfo) daoHelper.queryOne(_METADATA_TABLE_COLUMN_PREFIX + "selectTableStructMetaInfo", ti);
	}

	@Override
	public TableInfo selectTableMetaInfo(String tableId, String packageId) {
		TableInfo ti = new TableInfo();
		ti.setTableId(tableId);
		ti.setPackageId(this.st.getPackageId());
		return (TableInfo) daoHelper.queryOne(_METADATA_TABLE_COLUMN_PREFIX + "selectTableMetaInfo", ti);
	}

	@Override
	public void setScanTask(ScanTask st) {
		this.st = st;
	}

	/*public Map<String, Object> sampleData(String tableId) throws SqlExecuteException, SQLException {
		Map<String,Object>result = new HashMap<String,Object>();
		TableInfo tableInfo = new TableInfo();
		tableInfo.setTableId(tableId);
		TableInfo ti = (TableInfo) daoHelper.queryOne(_METADATA_TABLE_COLUMN_PREFIX + "selectTableStructMetaInfo", tableInfo);
		DatabaseResource re = (DatabaseResource) daoHelper.queryOne(_METADATA_TABLE_COLUMN_PREFIX + "selectDataBaseResourceById", tableInfo);
		IConnection iconn = new ConnectionProvider(re);
		ISqlBuilder builder = Constants.getBuilderByDBType(re.getResType(),ti);
		PreparedStatement pst = null;
		ResultSet ret  = null;
		try{
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			List<String> columns = new ArrayList<String>();
			iconn.doConnection();
			log.debug("链接数据源成功！");
			Connection  conn = iconn.getConnection();
			builder.buildSql();
			log.debug("准备执行sql:"+builder.getSql());
			pst  = conn.prepareStatement(builder.getSql());
			ret = pst.executeQuery();
			while(ret.next()){
				Map<String,Object> temp = new HashMap<String,Object>();
				for(StructInfo struct:ti.getStructs()){
					if(struct.getColumnType().equals("BLOB")||struct.getColumnType().equals("CLOB")){
						temp.put(struct.getColumnCode(), struct.getColumnType());
					}else{
						temp.put(struct.getColumnCode(), ret.getString(struct.getColumnCode()));
					}
				}
				data.add(temp);
			}
			for(StructInfo struct:ti.getStructs()){
				columns.add(struct.getColumnCode());
			}
			result.put("data", data);
			result.put("col", columns);
			return result;
		}catch(Exception e){
			String msg = e.getMessage();
			throw new SqlExecuteException(msg,builder.getSql());
		}finally{
			if(ret!=null)
			ret.close();
			if(pst!=null)
			pst.close();
			iconn.releaseConnection();
		}
	}*/

	
	
	
}

