package com.bonc.frame.module.db.meta.web.bo;


import com.bonc.frame.module.db.meta.core.model.ScanTask;
import com.bonc.frame.module.db.meta.core.model.ScanTaskLog;
import com.bonc.frame.module.db.meta.core.model.TableInfo;
import com.bonc.frame.module.db.meta.web.vo.TableMetaInfoWapper;

import java.util.List;


public interface TableMetaInfoBO {

	public TableInfo selectTableStructMetaInfo(String tableId, String packageId);
	public TableInfo selectTableMetaInfo(String tableId, String packageId);
	public void updateScanTask(ScanTask scanTask);
	public void insertScanTaskLog(ScanTaskLog scanTaskLog);
	public void updateTableStructMetaInfos(List<TableMetaInfoWapper> needInsertTables, List<TableMetaInfoWapper> needUpdateTables)throws Exception;
    public void setScanTask(ScanTask st);
//    public Map<String, Object> sampleData(String tableId) throws SqlExecuteException, SQLException;
}
