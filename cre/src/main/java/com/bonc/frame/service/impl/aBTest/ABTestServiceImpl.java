package com.bonc.frame.service.impl.aBTest;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.aBTest.ABSwitch;
import com.bonc.frame.entity.aBTest.ABSwitchPath;
import com.bonc.frame.entity.aBTest.ABTest;
import com.bonc.frame.entity.aBTest.ABTestDetail;
import com.bonc.frame.entity.rule.RuleDetail;
import com.bonc.frame.entity.rule.RuleDetailWithBLOBs;
import com.bonc.frame.module.aBTest.manager.ABTestManager;
import com.bonc.frame.service.aBTest.ABTestService;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author yedunyao
 * @since 2020/9/2 10:22
 */
@Service("abTestService")
public class ABTestServiceImpl implements ABTestService {

    private static final String ABTEST_MAPPER = "com.bonc.frame.mapper.oracle.aBTest.ABTestMapper.";
    private static final String ABSWITCH_MAPPER = "com.bonc.frame.mapper.oracle.aBTest.ABSwitchMapper.";

    private Logger logger = LogManager.getLogger(ABTestServiceImpl.class);

    @Autowired
    private VariableService variableService;

    @Autowired
    private RuleDetailService ruleDetailService;

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private ABTestManager abTestManager;

    @Override
    public Map<String, Object> list(String aBTestName, String aModelName, String productName,
                                    String folderName, String folderId,
                                    String status, String start, String size) {
        Map<String, Object> param = new HashMap<>(4);
        param.put("aBTestName", aBTestName);
        param.put("aModelName", aModelName);
        param.put("productName", productName);
        param.put("folderName", folderName);
        param.put("folderId", folderId);
        param.put("status", status);
        Map<String, Object> result = daoHelper.queryForPageList(ABTEST_MAPPER + "list", param, start, size);
        return result;
    }

    @Override
    public ABTestDetail getDetail(String aBTestId) {
        ABTestDetail one = (ABTestDetail) daoHelper.queryOne(ABTEST_MAPPER + "detail", aBTestId);
        return one;
    }

    public List<ABTest> getByStatus(String status) {
        List<ABTest> abTests = daoHelper.queryForList(ABTEST_MAPPER + "getByStatus", status);
        return abTests;
    }

    @Override
    public boolean isNameExist(String aBTestName, String aBTestId) {
        Map<String, String> param = new HashMap<>(2);
        param.put("aBTestName", aBTestName);
        param.put("aBTestId", aBTestId);
        final int count = (int) daoHelper.queryOne(ABTEST_MAPPER + "isNameExist", param);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult save(ABTestDetail abTestDetail) {
        if (isNameExist(abTestDetail.getaBTestName(), abTestDetail.getaBTestId())) {
            return ResponseResult.createFailInfo("AB测试名称重复.");
        }
        // 验证，如：AB是否同一模型，参数是否相同
        if (!checkABModelVariablesEquals(abTestDetail)) {
            return ResponseResult.createFailInfo("AB模型输入参数、输出参数不一致.");
        }

        String loginUserId = ControllerUtil.getCurrentUser();
        String accountUser = ControllerUtil.getRealUserAccount();
        final String id = IdUtil.createId();
        abTestDetail.setaBTestId(id);
        abTestDetail.setStatus(ABTest.INIT);
        abTestDetail.setCreatePerson(loginUserId);
        abTestDetail.setCreateDate(new Date());
        abTestDetail.setCreateUserJobNumber(accountUser);

        daoHelper.insert(ABTEST_MAPPER + "insertSelective", abTestDetail);
        return ResponseResult.createSuccessInfo("", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult update(ABTestDetail abTestDetail) {
        ABTestDetail oldDetail = getDetail(abTestDetail.getaBTestId());
        if (oldDetail == null) {
            return ResponseResult.createFailInfo("测试不存在");
        }
        if (ABTest.RUNNING.equals(oldDetail.getStatus())) {
            return ResponseResult.createFailInfo("编辑失败，AB测试正在运行中。");
        }
        if (isNameExist(abTestDetail.getaBTestName(), abTestDetail.getaBTestId())) {
            return ResponseResult.createFailInfo("AB测试名称重复.");
        }
        // 验证，如：AB是否同一模型，参数是否相同
        if (!checkABModelVariablesEquals(abTestDetail)) {
            return ResponseResult.createFailInfo("AB模型输入参数、输出参数不一致.");
        }

        String loginUserId = ControllerUtil.getCurrentUser();
        String accountUser = ControllerUtil.getRealUserAccount();
        abTestDetail.setUpdatePerson(loginUserId);
        abTestDetail.setUpdateDate(new Date());
        abTestDetail.setUpdateUserJobNumber(accountUser);

        daoHelper.update(ABTEST_MAPPER + "updateByPrimaryKeySelective", abTestDetail);

        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult updateFetchStartTime(ABTestDetail abTestDetail) {
        daoHelper.update(ABTEST_MAPPER + "updateFetchStartTime", abTestDetail);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult delete(String aBTestId) {
        ABTestDetail detail = getDetail(aBTestId);
        if (detail == null) {
            return ResponseResult.createFailInfo("测试不存在");
        }
        String status = detail.getStatus();
        if (!ABTest.INIT.equals(status) &&
                !ABTest.STOP.equals(status) &&
                !ABTest.ONLINE.equals(status)) {
            return ResponseResult.createFailInfo("删除失败，只能删除未开始、已停止或已上线的测试。");
        }
        // 未开始以及已上线的测试进行删除
        daoHelper.delete(ABTEST_MAPPER + "deleteByPrimaryKey", aBTestId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult startAndSubmit(String aBTestId) {
        ABTestDetail detail = getDetail(aBTestId);
        if (detail == null) {
            return ResponseResult.createFailInfo("测试不存在");
        }
        if (!ABTest.INIT.equals(detail.getStatus())) {
            return ResponseResult.createFailInfo("开始测试失败，AB测试不是未开始状态。");
        }
        boolean submit = false;
        try {
            submit = abTestManager.start(detail);
        } catch (Exception e) {
            logger.error("开始任务失败", e);
            return ResponseResult.createFailInfo("开始任务失败，原因：" + e.getMessage());
        }
        if (submit) {
            changeStatus(aBTestId, ABTest.RUNNING, true);
        } else {
            changeStatus(aBTestId, ABTest.READY, true);
        }
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult startInternal(String aBTestId) {
        ABTestDetail detail = getDetail(aBTestId);
        if (detail == null) {
            return ResponseResult.createFailInfo("测试不存在");
        }
        if (!ABTest.INIT.equals(detail.getStatus()) && !ABTest.READY.equals(detail.getStatus())) {
            return ResponseResult.createFailInfo("开始测试失败，AB测试不是未开始状态。");
        }
        changeStatusInternal(aBTestId, ABTest.RUNNING, true);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult stopAndSubmit(String aBTestId) {
        ABTestDetail detail = getDetail(aBTestId);
        if (detail == null) {
            return ResponseResult.createFailInfo("测试不存在");
        }
        if (!ABTest.RUNNING.equals(detail.getStatus())) {
            return ResponseResult.createFailInfo("停止任务失败，AB测试不在运行中。");
        }
        boolean submit = false;
        try {
            submit = abTestManager.stop(detail);
        } catch (Exception e) {
            logger.error("停止任务失败", e);
            return ResponseResult.createFailInfo("停止任务失败，原因：" + e.getMessage());
        }
        if (submit) {
            changeStatus(aBTestId, ABTest.STOP, false);
        } else {
            changeStatus(aBTestId, ABTest.STOPPING, false);
        }
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult stopInternal(String aBTestId) {
        ABTestDetail detail = getDetail(aBTestId);
        if (detail == null) {
            return ResponseResult.createFailInfo("测试不存在");
        }
        if (!ABTest.RUNNING.equals(detail.getStatus()) && !ABTest.STOPPING.equals(detail.getStatus())) {
            return ResponseResult.createFailInfo("停止失败，AB测试不在运行中。");
        }
        changeStatusInternal(aBTestId, ABTest.STOP, false);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult online(String aBTestId) throws Exception {
        ABTestDetail detail = getDetail(aBTestId);
        if (detail == null) {
            return ResponseResult.createFailInfo("测试不存在");
        }
        if (!ABTest.STOP.equals(detail.getStatus())) {
            return ResponseResult.createFailInfo("执行失败，AB测试未完成。");
        }
        String aRuleId = detail.getaRuleId();
        String bRuleId = detail.getbRuleId();
//        String folderId = detail.getFolderId();
        RuleDetail aModel = ruleDetailService.getOne(aRuleId);
        if (aModel == null) {
            return ResponseResult.createFailInfo("执行失败，A模型不存在");
        }
        String aModelFolderId = aModel.getFolderId();
        RuleDetail bModel = ruleDetailService.getOne(bRuleId);
        if (bModel == null) {
            return ResponseResult.createFailInfo("执行失败，B模型不存在");
        }
        String bModelFolderId = bModel.getFolderId();

        // A停用 B提交、启用
        String loginUserId = ControllerUtil.getCurrentUser();
        String accountUser = ControllerUtil.getRealUserAccount();
        ruleDetailService.updateRuleStatus(aModelFolderId, aRuleId, ConstantUtil.RULE_STATUS_OVER, accountUser);
        RuleDetailWithBLOBs bRuleDetail = ruleDetailService.getRuleFromOfficial(bRuleId);
        if (bRuleDetail == null) {
            bRuleDetail = ruleDetailService.getRuleFromDraft(bRuleId);
            if (bRuleDetail == null) {
                throw new RuntimeException("B模型不存在");
            }

            ResponseResult responseResult = ruleDetailService.commitWithVersion(bRuleDetail, bRuleDetail.getRuleContent(), loginUserId,null);
            if (ResponseResult.SUCCESS_STAUS == responseResult.getStatus()) {
                Map data = (Map) responseResult.getData();
                String newRuleId = (String) data.get("ruleId");
                if (newRuleId == null) {
                    throw new RuntimeException("B模型提交失败");
                }
                // 修改B模型id
                bRuleId = newRuleId;
            } else {
                throw new RuntimeException("B模型提交失败");
            }
        }
        ruleDetailService.updateRuleStatus(bModelFolderId, bRuleId, ConstantUtil.RULE_STATUS_RUNNING, accountUser);

        // 记录AB映射
        switchRule(aRuleId, bRuleId);

        changeStatus(aBTestId, ABTest.ONLINE, bRuleId);
        return ResponseResult.createSuccessInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchRule(String aRuleId, String bRuleId) throws Exception {
        String headerRuleId = "";
        int count = (int) daoHelper.queryOne(ABSWITCH_MAPPER + "isInTail", aRuleId);
        if (count > 0) {
            if (count > 1) {
                logger.warn("同时存在多个模型映射到同一A模型，A模型：{}，B模型：{}", aRuleId, bRuleId);
                throw new RuntimeException("同时存在多个模型映射到同一A模型");
            }
            ABSwitch oldSwitch = (ABSwitch) daoHelper.queryOne(ABSWITCH_MAPPER + "getSwitchByTail", aRuleId);
            oldSwitch.setbRuleId(bRuleId);
            headerRuleId = oldSwitch.getaRuleId();
            // 存在则更新
            daoHelper.update(ABSWITCH_MAPPER + "updateSwitch", oldSwitch);
        } else {
            count = (int) daoHelper.queryOne(ABSWITCH_MAPPER + "isInHeader", aRuleId);
            if (count > 0) {
                logger.warn("已存在A模型的映射，A模型：{}，B模型：{}", aRuleId, bRuleId);
                throw new RuntimeException("已存在A模型的映射");
            } else {
                count = (int) daoHelper.queryOne(ABSWITCH_MAPPER + "isInTail", bRuleId);
                if (count > 0) {
                    logger.warn("同时存在多个模型映射到同一B模型，A模型：{}，B模型：{}", aRuleId, bRuleId);
                    throw new RuntimeException("同时存在多个模型映射到同一B模型");
                }
                ABSwitch abSwitch = new ABSwitch(aRuleId, bRuleId);
                headerRuleId = aRuleId;
                daoHelper.insert(ABSWITCH_MAPPER + "insertSwitch", abSwitch);
            }
        }
        ABSwitchPath abSwitchPath = new ABSwitchPath();
        abSwitchPath.setLogId(IdUtil.createId());
        abSwitchPath.setHeadRuleId(headerRuleId);
        abSwitchPath.setpRuleId(aRuleId);
        abSwitchPath.setnRuleId(bRuleId);
        abSwitchPath.setOperatePerson(ControllerUtil.getCurrentUser());
        abSwitchPath.setOperateUserJobNumber(ControllerUtil.getRealUserAccount());
        abSwitchPath.setOperateTime(new Date());
        daoHelper.insert(ABSWITCH_MAPPER + "insertSwitchPath", abSwitchPath);
    }

    @Override
    @Transactional
    public void updateExecuteStatus(ABTest abTest) {
        daoHelper.update(ABTEST_MAPPER + "updateExecuteStatus", abTest);
    }

    /**
     * 获取模型的切换结果 返回null，代表不存在切换
     *
     * @param aRuleId
     * @return
     */
    @Override
    public String getSwitchRule(String aRuleId) {
        ABSwitch oldSwitch = (ABSwitch) daoHelper.queryOne(ABSWITCH_MAPPER + "getSwitchByHeader", aRuleId);
        if (oldSwitch == null) {
            return null;
        }
        return oldSwitch.getbRuleId();
    }

    private void changeStatus(String aBTestId, String status, boolean start) {
        ABTest abTest = new ABTest();
        Date date = new Date();
        abTest.setaBTestId(aBTestId);
        abTest.setStatus(status);
        String loginUserId = ControllerUtil.getCurrentUser();
        String accountUser = ControllerUtil.getRealUserAccount();
        abTest.setUpdatePerson(loginUserId);
        abTest.setUpdateDate(date);
        abTest.setUpdateUserJobNumber(accountUser);
        if (start) {
            abTest.setStartTime(date);
        } else {
            abTest.setEndTime(date);
        }
        daoHelper.update(ABTEST_MAPPER + "changeStatus", abTest);
    }

    /**
     * 内部调用不需要修改操作人信息
     *
     * @param aBTestId
     * @param status
     * @param start
     */
    private void changeStatusInternal(String aBTestId, String status, boolean start) {
        ABTest abTest = new ABTest();
        Date date = new Date();
        abTest.setaBTestId(aBTestId);
        abTest.setStatus(status);
        abTest.setUpdateDate(date);
        if (start) {
            abTest.setStartTime(date);
        } else {
            abTest.setEndTime(date);
        }
        daoHelper.update(ABTEST_MAPPER + "changeStatus", abTest);
    }

    private void changeStatus(String aBTestId, String status) {
        ABTest abTest = new ABTest();
        abTest.setaBTestId(aBTestId);
        abTest.setStatus(status);
        String loginUserId = ControllerUtil.getCurrentUser();
        String accountUser = ControllerUtil.getRealUserAccount();
        abTest.setUpdatePerson(loginUserId);
        abTest.setUpdateDate(new Date());
        abTest.setUpdateUserJobNumber(accountUser);

        daoHelper.update(ABTEST_MAPPER + "changeStatus", abTest);
    }

    private void changeStatus(String aBTestId, String status, String bRuleId) {
        ABTest abTest = new ABTest();
        abTest.setaBTestId(aBTestId);
        abTest.setStatus(status);
        abTest.setbRuleId(bRuleId);
        String loginUserId = ControllerUtil.getCurrentUser();
        String accountUser = ControllerUtil.getRealUserAccount();
        abTest.setUpdatePerson(loginUserId);
        abTest.setUpdateDate(new Date());
        abTest.setUpdateUserJobNumber(accountUser);

        daoHelper.update(ABTEST_MAPPER + "changeStatus", abTest);
    }

    /**
     * 比较AB测试中A模型与B模型的输入参数、输出参数是否完全一致
     *
     * @param abTest
     * @return
     */
    private boolean checkABModelVariablesEquals(ABTest abTest) {
        String aRuleId = abTest.getaRuleId();
        String bRuleId = abTest.getbRuleId();
        List<Map<String, Object>> aInputVariableList = variableService.getVariableMapByRuleId(aRuleId, "K1");
        List<Map<String, Object>> bInputVariableList = variableService.getVariableMapByRuleId(bRuleId, "K1");

        List<Map<String, Object>> aOutputVariableList = variableService.getOutVariableMapByRuleId(aRuleId);
        List<Map<String, Object>> bOutputVariableList = variableService.getOutVariableMapByRuleId(bRuleId);

        if (equalsVariableList(aInputVariableList, bInputVariableList) && equalsVariableList(aOutputVariableList, bOutputVariableList)) {
            return true;
        }
        return false;
    }

    /**
     * 比较参数列表是否完全一致 通过参数id进行比较
     *
     * @param aVariableList
     * @param bVariableList
     * @return
     */
    private boolean equalsVariableList(List<Map<String, Object>> aVariableList, List<Map<String, Object>> bVariableList) {
        if (CollectionUtil.isEmpty(aVariableList) && CollectionUtil.isEmpty(bVariableList)) {
            return true;
        }

        if (!CollectionUtil.isEmpty(aVariableList) && !CollectionUtil.isEmpty(bVariableList)) {
            int aVariableSize = aVariableList.size();
            int bVariableSize = bVariableList.size();
            if (aVariableSize == bVariableSize) {
                Comparator<Map<String, Object>> comparator = new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        String aVariableId = o1.get("variableId").toString();
                        String bVariableId = o2.get("variableId").toString();
                        return aVariableId.compareTo(bVariableId);
                    }
                };
                Collections.sort(aVariableList, comparator);
                Collections.sort(bVariableList, comparator);
                for (int i = 0; i < aVariableList.size(); i++) {
                    String aVariableId = (String) aVariableList.get(i).get("variableId");
                    String bVariableId = (String) bVariableList.get(i).get("variableId");
                    if (!aVariableId.equals(bVariableId)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

}
