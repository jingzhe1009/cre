package com.bonc.frame.controller.aBTest;

import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.config.Config;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/9/23 18:28
 */
@Controller
@RequestMapping("/ws/mock")
public class ABTestDataProducerController {

    private Logger logger = LogManager.getLogger(ABTestDataProducerController.class);

    @RequestMapping("/ruleData")
    @ResponseBody
    public Object ruleData(String ruleid, final String aBTestId, String startdate, String enddate) {
        // ruleid startdate enddate aBTestId
        logger.info("模拟接口 ruleData {} {} {} {}", ruleid, startdate, enddate, aBTestId);
        Random r = new Random();
        final int num = r.nextInt(5);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message = "RuleLog{logId='7e2bd85b6f4a4406911f9e834083293e', ruleId='2c90fc3b74bdb4a00174bdd5f1e901ed', folderId='402881ed6c847f54016ca7f2e2130096', state='2', hitRuleNum='4', startTime=Thu Sep 24 11:18:39 CST 2020, endTime=Thu Sep 24 11:18:41 CST 2020, inputData='{\"idcard\":\"2\",\"wzb_idCard\":\"\",\"age\":25}', outputData='{\"income\":\"1000\",\"output_school\":\"太原理工\",\"idcard\":\"2\",\"score_age\":10,\"wzb_idCard\":\"\",\"output_income\":\"1000\",\"outParam\":1,\"age\":25,\"getageByIdCardAndDate\":35,\"kpi_api_ren_shcool\":\"太原理工\"}', exception='null', consumerId='cre_test', serverId='null', consumerSeqNo='null',detail=[RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_rect1, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=rect1, nodeName=开始, nodeType=start, state=2, startTime=Thu Sep 24 11:18:39 CST 2020, endTime=Thu Sep 24 11:18:39 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_path10, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=path10, nodeName=true, nodeType=path, state=2, startTime=Thu Sep 24 11:18:39 CST 2020, endTime=Thu Sep 24 11:18:39 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},ApiLog{apiId=ff8080817495116e0174bdcf2208006e, logId=2c90fc3b74be1ebd0174be1ebfd50000, ruleLogId=7e2bd85b6f4a4406911f9e834083293e, sourceLogId=null, logContent=null, logOccurtime=Thu Sep 24 11:18:40 CST 2020, callResult={\"income\":\"1000\"}, logParam={\"idcard\":\"2\"}, consumerId=cre_test, serverId=null, consumerSeqNo=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_rect3, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=rect3, nodeName=收入接口, nodeType=interface, state=2, startTime=Thu Sep 24 11:18:39 CST 2020, endTime=Thu Sep 24 11:18:40 CST 2020, result={\"income\":\"1000\",\"idcard\":\"2\",\"wzb_idCard\":\"\",\"age\":25}, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_path11, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=path11, nodeName=true, nodeType=path, state=2, startTime=Thu Sep 24 11:18:40 CST 2020, endTime=Thu Sep 24 11:18:40 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_rect2, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=rect2, nodeName=分支, nodeType=fork, state=2, startTime=Thu Sep 24 11:18:40 CST 2020, endTime=Thu Sep 24 11:18:40 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_path17, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=path17, nodeName=true, nodeType=path, state=2, startTime=Thu Sep 24 11:18:40 CST 2020, endTime=Thu Sep 24 11:18:40 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_path19, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=path19, nodeName=true, nodeType=path, state=2, startTime=Thu Sep 24 11:18:40 CST 2020, endTime=Thu Sep 24 11:18:40 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_path13, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=path13, nodeName=true, nodeType=path, state=2, startTime=Thu Sep 24 11:18:40 CST 2020, endTime=Thu Sep 24 11:18:40 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_path12, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=path12, nodeName=true, nodeType=path, state=2, startTime=Thu Sep 24 11:18:40 CST 2020, endTime=Thu Sep 24 11:18:40 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},ApiLog{apiId=2c90fc3b7490aec5017490be0503005a, logId=2c90fc3b74be1ebd0174be1ec02d0001, ruleLogId=7e2bd85b6f4a4406911f9e834083293e, sourceLogId=null, logContent=null, logOccurtime=Thu Sep 24 11:18:40 CST 2020, callResult={\"wzb_sex\":\"女\",\"wzb_school\":\"太原理工\",\"wzb_specialty\":\"物理专业\",\"wzb_age\":9999,\"wzb_idCard\":\"1111111\"}, logParam={\"wzb_idCard\":\"\"}, consumerId=cre_test, serverId=null, consumerSeqNo=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_rect8, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=rect8, nodeName=指标-接口, nodeType=task, state=2, startTime=Thu Sep 24 11:18:40 CST 2020, endTime=Thu Sep 24 11:18:40 CST 2020, result=[{\"actRuleName\":\"指标接口-输出学校\",\"isEndFlow\":false,\"rule\":\" if 1==1  then {output_school=kpi_api_ren_shcool};\",\"executeResult\":\"太原理工\",\"isEndAction\":false}], consumerId=cre_test, serverId=null, consumerSeqNo=null,executionProcess={\"kpiLog\":[{\"inputData\":{\"wzb_idCard\":\"\"},\"kpiId\":\"2c90fc3b74bdb4a00174bdd406e201b1\",\"logId\":\"2c90fc3b74be1ebd0174be1ec0440002\",\"logOccurtime\":\"Thu Sep 24 11:18:40 CST 2020\",\"outputData\":{\"kpi_api_ren_shcool\":\"太原理工\"},\"ruleLogId\":\"7e2bd85b6f4a4406911f9e834083293e\",\"state\":\"2\"}]}},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_path18, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=path18, nodeName=true, nodeType=path, state=2, startTime=Thu Sep 24 11:18:40 CST 2020, endTime=Thu Sep 24 11:18:40 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_rect9, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=rect9, nodeName=接口返回值, nodeType=task, state=2, startTime=Thu Sep 24 11:18:40 CST 2020, endTime=Thu Sep 24 11:18:40 CST 2020, result=[{\"actRuleName\":\"接口返回值\",\"isEndFlow\":false,\"rule\":\" if 1==1  then {output_income=income};\",\"executeResult\":\"1000\",\"isEndAction\":false}], consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_path20, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=path20, nodeName=true, nodeType=path, state=2, startTime=Thu Sep 24 11:18:40 CST 2020, endTime=Thu Sep 24 11:18:40 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_rect5, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=rect5, nodeName=年龄指标-数据库, nodeType=task, state=2, startTime=Thu Sep 24 11:18:40 CST 2020, endTime=Thu Sep 24 11:18:41 CST 2020, result=[{\"actRuleName\":\"年龄指标-数据库\",\"isEndFlow\":false,\"rule\":\" if getageByIdCardAndDate<37 then {outParam=1};\",\"executeResult\":1,\"isEndAction\":false}], consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess={\"kpiLog\":[{\"inputData\":{\"idcard\":\"2\"},\"kpiId\":\"ff80808174299c5c01742dcdceac0004\",\"logId\":\"2c90fc3b74be1ebd0174be1ec3940003\",\"logOccurtime\":\"Thu Sep 24 11:18:41 CST 2020\",\"outputData\":{\"getageByIdCardAndDate\":35},\"ruleLogId\":\"7e2bd85b6f4a4406911f9e834083293e\",\"state\":\"2\"}]}},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_path15, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=path15, nodeName=true, nodeType=path, state=2, startTime=Thu Sep 24 11:18:41 CST 2020, endTime=Thu Sep 24 11:18:41 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_rect4, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=rect4, nodeName=年龄规则集-常规, nodeType=task, state=2, startTime=Thu Sep 24 11:18:41 CST 2020, endTime=Thu Sep 24 11:18:41 CST 2020, result=[{\"actRuleName\":\"年龄-25~50\",\"isEndFlow\":false,\"rule\":\" if age>=25 && age<=50 then {score_age=10};\",\"executeResult\":10,\"isEndAction\":false}], consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_path14, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=path14, nodeName=true, nodeType=path, state=2, startTime=Thu Sep 24 11:18:41 CST 2020, endTime=Thu Sep 24 11:18:41 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_rect7, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=rect7, nodeName=聚合, nodeType=join, state=2, startTime=Thu Sep 24 11:18:41 CST 2020, endTime=Thu Sep 24 11:18:41 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_path16, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=path16, nodeName=true, nodeType=path, state=2, startTime=Thu Sep 24 11:18:41 CST 2020, endTime=Thu Sep 24 11:18:41 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=7e2bd85b6f4a4406911f9e834083293e_rect6, logId=7e2bd85b6f4a4406911f9e834083293e, nodeId=rect6, nodeName=结束, nodeType=end, state=2, startTime=Thu Sep 24 11:18:41 CST 2020, endTime=Thu Sep 24 11:18:41 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null}]}";
                message = message + ",aBTestId='" + aBTestId + "'";
                Producer producer = new Producer<String, Object>(new ProducerConfig(Config.AB_TEST_PRODUCER_PROPERTIES));

//                num = 10000;
                int num = 2000;
                logger.info("模拟接口 ruleData 生产数据:[" + num + "]条");
                for (int i = 0; i < num; i++) {
                    KeyedMessage<Object, Object> objectObjectKeyedMessage =
                            new KeyedMessage<Object, Object>("cre-abTest", i + "", message);
                    producer.send(objectObjectKeyedMessage);
                }
            }
        }).start();

        final JSONObject map = new JSONObject();
        map.put("status", "ok");
        map.put("HBaseNumTotal", num);
        return map;
    }

    @RequestMapping("/ruleTotal")
    @ResponseBody
    public Object ruleTotal(String ruleid, String startdate, String enddate) {
        // ruleid startdate enddate
        logger.info("模拟接口 ruleTotal {} {} {}", ruleid, startdate, enddate);


        final JSONObject map = new JSONObject();
        map.put("total", 100);
        return map;
    }

    @RequestMapping("/ruleFirst")
    @ResponseBody
    public Object ruleFirst(String ruleid) {
        // ruleid
        logger.info("模拟接口 ruleFirst {}", ruleid);

        final JSONObject map = new JSONObject();
        map.put("firstTime", 20200101);
        return map;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            Random r = new Random();
            final int num = r.nextInt(5);
            System.out.println(num);
        }
    }


}
