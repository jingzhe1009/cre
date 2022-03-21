package com.bonc.frame.util;


import com.bonc.frame.entity.datasource.DataSource;
import com.bonc.frame.entity.metadata.MetaDataScanTask;
import com.bonc.frame.module.db.meta.core.model.DatabaseResource;
import com.bonc.frame.module.db.meta.core.model.ScanTask;

/**
 * 将元数据相关的实体进行转化，转成扫描中可以用的
 *
 * @author qxl
 * @version 1.0.0
 * @date 2016年12月8日 上午11:40:06
 */
public class MetaConvertUtil {

    /**
     * 将DataSourceCon类型转成DatabaseResource类型
     *
     * @param dataSource
     * @return DatabaseResource
     */
    public static DatabaseResource convertToDatabaseResource(DataSource dataSource) {
        DatabaseResource databaseResource = new DatabaseResource();
        databaseResource.setCreateDate(dataSource.getCreateDate());
        databaseResource.setCreatePersion(dataSource.getCreatePerson());
        databaseResource.setRemarks(null);
        databaseResource.setResCharset(null);
        databaseResource.setResId(dataSource.getDbId());
        databaseResource.setResIp(dataSource.getDbIp());
        databaseResource.setResMax(dataSource.getMaxConnect().shortValue());
        databaseResource.setResName(dataSource.getDbAlias());
        databaseResource.setResPassword(dataSource.getDbPassword());
        databaseResource.setResPort(dataSource.getDbPort());
        databaseResource.setResService(dataSource.getDbServiceName());
        databaseResource.setResType(Short.parseShort(dataSource.getDbType()));
        databaseResource.setResUsername(dataSource.getDbUsername());
        databaseResource.setUpdateDate(dataSource.getUpdateDate());
        databaseResource.setUpdatePersion(dataSource.getUpdatePerson());
        return databaseResource;
    }

    /**
     * 将MetaDataScanTask类型转成ScanTask类型
     *
     * @return ScanTask
     */
    public static ScanTask convertToScanTask(MetaDataScanTask metaDataScanTask) {
        ScanTask scanTask = new ScanTask();
        scanTask.setCreateUser(metaDataScanTask.getScanPerson());
        scanTask.setEndDate(metaDataScanTask.getEndDate());
        scanTask.setPackageId(metaDataScanTask.getPackageId());
        scanTask.setResId(null);
        scanTask.setStartDate(metaDataScanTask.getStartDate());
        scanTask.setTaskId(metaDataScanTask.getScanId());
        scanTask.setTableKind(metaDataScanTask.getTableKind());
        if (metaDataScanTask.getScanItem() == null || metaDataScanTask.getScanItem().isEmpty()) {
            scanTask.setTaskItems("TABLE,VIEW");
        } else {
            scanTask.setTaskItems(metaDataScanTask.getScanItem());
        }

        scanTask.setTaskKey(metaDataScanTask.getScanKey());
        scanTask.setTaskStatus(Short.parseShort(metaDataScanTask.getScanStatus()));
        return scanTask;
    }

}
