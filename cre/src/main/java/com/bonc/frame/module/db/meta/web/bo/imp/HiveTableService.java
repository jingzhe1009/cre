package com.bonc.frame.module.db.meta.web.bo.imp;

import com.bonc.frame.module.db.meta.core.IConnection;
import com.bonc.frame.module.db.meta.core.IScan;
import com.bonc.frame.module.db.meta.core.connection.ConnectionProvider;
import com.bonc.frame.module.db.meta.core.exception.AfterScanException;
import com.bonc.frame.module.db.meta.core.exception.BeforeScanException;
import com.bonc.frame.module.db.meta.core.exception.ScanException;
import com.bonc.frame.module.db.meta.core.model.DatabaseResource;
import com.bonc.frame.module.db.meta.core.model.ScanTask;
import com.bonc.frame.module.db.meta.core.model.StructInfo;
import com.bonc.frame.module.db.meta.core.model.TableInfo;
import com.bonc.frame.module.db.meta.core.scan.HiveTableScanProvider;
import com.bonc.frame.module.db.meta.core.service.AbstractScanService;
import com.bonc.frame.module.db.meta.util.Constants;
import com.bonc.frame.module.db.meta.web.bo.TableMetaInfoBO;
import com.bonc.frame.module.db.meta.web.vo.MetaChangeLog;
import com.bonc.frame.module.db.meta.web.vo.TableMetaInfoWapper;
import com.bonc.framework.util.IdUtil;
import com.bonc.framework.util.JsonUtils;

import java.sql.SQLException;
import java.util.*;


public class HiveTableService extends AbstractScanService<TableInfo> {

	private IScan<TableInfo> scan;
	private IConnection conn;
	
	private List<TableMetaInfoWapper>  needInsertTables;
	private List<TableMetaInfoWapper>  needUpdateTables;
	
	private TableMetaInfoBO tableMetaInfoBO;
	
	public HiveTableService(ScanTask scanTask, DatabaseResource databaseResource, TableMetaInfoBO tableMetaInfoBO)
			throws SQLException {
		super(scanTask);
		this.conn  =  new ConnectionProvider(databaseResource);
		this.scan = new HiveTableScanProvider(this.conn,scanTask);
		this.tableMetaInfoBO   = tableMetaInfoBO;
		tableMetaInfoBO.setScanTask(scanTask);
		this.needInsertTables  = new ArrayList<TableMetaInfoWapper>();
		this.needUpdateTables  = new ArrayList<TableMetaInfoWapper>();
	}


	@Override
	public void beforeScan() throws BeforeScanException {
		this.needInsertTables.clear();
		this.needUpdateTables.clear();
		try{
			this.scanTask.setTaskStatus(Constants.Task_Status_Scanning);
			this.tableMetaInfoBO.updateScanTask(scanTask);//??????????????????????????????
		}catch(Exception e){
			throw new BeforeScanException(e.getMessage());
		}
	}

	@Override
	public void scan() throws ScanException {
		try {
            List<TableInfo> tables = scan.scanObjectMetaInfo();    // ?????????????????????
			for(TableInfo table:tables){
				TableInfo tempTableInfo = tableMetaInfoBO.selectTableStructMetaInfo(table.getTableId(),this.scanTask.getPackageId());
				if(tempTableInfo==null){//????????????????????????????????????????????????
					table.setResStatus((short)0);//???????????????????????????
					this.needInsertTables.add(new TableMetaInfoWapper(table));
					continue;
				}
				tempTableInfo.setScanId(scanTask.getTaskId());
				Map<String,StructInfo> orgTempMap = new HashMap<String,StructInfo>();//??????????????????????????????key value
				Map<String,StructInfo> targetTempMap = new HashMap<String,StructInfo>();//????????????????????????key value
				table.setResStatus(tempTableInfo.getResStatus());//???????????????????????????????????????
				TableMetaInfoWapper tempTableWapper = new TableMetaInfoWapper(table);
				for(StructInfo orgStructInfo:tempTableInfo.getStructs()){//?????????????????????????????????????????????
					orgTempMap.put(orgStructInfo.getColumnId(), orgStructInfo);
				}
				for(StructInfo targetStructInfo:table.getStructs()){
					targetTempMap.put(targetStructInfo.getColumnId(), targetStructInfo);
				}
				for(StructInfo tarStructInfo:table.getStructs()){//?????????????????????
					tarStructInfo.setScanId(scanTask.getTaskId());
                    if(orgTempMap.containsKey(tarStructInfo.getColumnId())){//??????????????????
                    	StructInfo orgStructInfo = orgTempMap.get(tarStructInfo.getColumnId());
                    	if(!orgStructInfo.equals(tarStructInfo)){
                    		tempTableWapper.addUpdateStructs(tarStructInfo);
                    	}
                    }else{//???????????????????????????????????????
                    	tempTableWapper.addInsertStructs(tarStructInfo);
                    }
				}
				for(StructInfo orgStructInfo:tempTableInfo.getStructs()){//?????????????????????
					if(!targetTempMap.containsKey(orgStructInfo.getColumnId())){//????????????????????????????????????????????????????????????
						tempTableWapper.addDeleteStructs(orgStructInfo);
					}
				}
				tempTableWapper.setUpdateTableInfo(!table.equals(tempTableInfo));//???????????????????????????
				this.needUpdateTables.add(tempTableWapper);
			}
		} catch (Exception e) {
			throw new ScanException(e.getMessage());
		}
	}

	@Override
	public void afterScan() throws AfterScanException {
		try{
			this.tableMetaInfoBO.updateTableStructMetaInfos(needInsertTables, needUpdateTables);//???????????????
			//???????????????
			for(TableMetaInfoWapper tw:needInsertTables){
				tw.setUpdateTableInfo(true);
				tw.setNeedInsertStructs(tw.getTableInfo().getStructs());
				tw.getTableInfo().setStructs(null);
			}
			int total = 0;
			List<TableMetaInfoWapper>  needUpdateTablesLog = new ArrayList<TableMetaInfoWapper>();;
			for(TableMetaInfoWapper tw:needUpdateTables){
				tw.getTableInfo().setStructs(null);
				if(tw.isUpdateTableInfo()||tw.getNeedInsertStructs().size()>0||tw.getNeedUpdateStructs().size()>0){
					total++;
					needUpdateTablesLog.add(tw);
				}
			}
			MetaChangeLog mcl = new MetaChangeLog(needInsertTables.size(), //????????????????????????
				                              total, needInsertTables, needUpdateTablesLog);
			String logDetail = JsonUtils.toJSONString(mcl);
			this.scanTaskLog.setTaskDetail(logDetail);//????????????????????????
	    }catch(Exception e){
	    	this.scanTaskLog.setTaskDetail(e.getMessage());
	    }
	}

	@Override
	public void makeLog() {
		this.scanTask.setTaskStatus(Constants.Task_Status_Finish);//?????????????????????????????????????????????
		this.scanTask.setEndDate(new Date());
		this.tableMetaInfoBO.updateScanTask(scanTask);
		scanTaskLog.setLogId(IdUtil.createId());
		this.tableMetaInfoBO.insertScanTaskLog(scanTaskLog);
	}

	public void setTableMetaInfoBO(TableMetaInfoBO tableMetaInfoBO) {
		this.tableMetaInfoBO = tableMetaInfoBO;
	}
}
