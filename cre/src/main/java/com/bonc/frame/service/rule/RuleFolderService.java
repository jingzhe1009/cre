package com.bonc.frame.service.rule;

import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.util.ResponseResult;

/**
 * 规则详情相关操作service
 *
 * @author qxl
 * @version 1.0
 * @date 2018年3月27日 下午4:36:01
 */
public interface RuleFolderService {

    /**
     * 获取模型库id
     *
     * @return
     */
    String getModelBaseId();

    /**
     * 获取文件夹详情
     *
     * @param folderId
     * @return
     */
    RuleFolder getRuleFolderDetail(String folderId, String folderName);

    /**
     * 删除文件夹及文件夹中全部规则、变量、接口等
     *
     * @param folderId
     * @return
     */
    ResponseResult deleteFolder(String folderId, String currentUser, String platformUpdateUserJobNumber);


}
