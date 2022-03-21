package com.bonc.frame.service.aBTest;

import com.bonc.frame.entity.aBTest.ABTest;
import com.bonc.frame.entity.aBTest.ABTestDetail;
import com.bonc.frame.util.ResponseResult;

import java.util.List;
import java.util.Map;

/**
 * @author yedunyao
 * @since 2020/9/2 10:22
 */
public interface ABTestService {

    Map<String, Object> list(String aBTestName, String aModelName, String productName,
                             String folderName, String folderId,
                             String status, String start, String size);

    ABTestDetail getDetail(String aBTestId);

    List<ABTest> getByStatus(String status);

//    boolean isRunning(String aBTestId);

    boolean isNameExist(String aBTestName, String aBTestId);

    ResponseResult save(ABTestDetail abTestDetail);

    ResponseResult update(ABTestDetail abTestDetail);

    ResponseResult updateFetchStartTime(ABTestDetail abTestDetail);

    ResponseResult delete(String aBTestId);

    ResponseResult startInternal(String aBTestId);

    ResponseResult startAndSubmit(String aBTestId);

//    ResponseResult stopping(String aBTestId);

    ResponseResult stopInternal(String aBTestId);

    ResponseResult stopAndSubmit(String aBTestId);

    ResponseResult online(String aBTestId) throws Exception;

    void switchRule(String aRuleId, String bRuleId) throws Exception;

    void updateExecuteStatus(ABTest abTest);

    /**
     * 获取模型的切换结果
     * 返回null，代表不存在切换
     *
     * @param aRuleId
     * @return
     */
    String getSwitchRule(String aRuleId);

}
