package com.bonc.frame.controller.aBTest;

import com.bonc.frame.entity.aBTest.ABTest;
import com.bonc.frame.entity.aBTest.ABTestDetail;
import com.bonc.frame.service.aBTest.ABTestService;
import com.bonc.frame.service.impl.aBTest.ABTestServiceImpl;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ValueConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yedunyao
 * @since 2020/9/2 18:50
 */
@Controller
@RequestMapping("/aBTest")
public class ABTestController {

    private Logger logger = LogManager.getLogger(ABTestController.class);

    @Autowired
    private ABTestService abTestService;

    @RequestMapping("/index")
    public String index(String idx, String childOpen, Model model) {
        model.addAttribute("idx", idx);
        model.addAttribute("childOpen", childOpen);
        Map<String, String> abTestStatus = new HashMap<>();
        abTestStatus.put(ABTest.INIT, "未开始");
        abTestStatus.put(ABTest.READY, "就绪");
        abTestStatus.put(ABTest.RUNNING, "运行中");
        abTestStatus.put(ABTest.STOPPING, "停用中");
        abTestStatus.put(ABTest.STOP, "停止");
        abTestStatus.put(ABTest.EXCEPTION, "异常");
        abTestStatus.put(ABTest.ONLINE, "已上线");
        model.addAttribute("abTestStatus", JsonUtils.beanToJson(abTestStatus).toString());
        return "/pages/aBTest/aBTest";
    }

    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(@RequestParam(value = "aBTestName", required = false) String aBTestName,
                                    @RequestParam(value = "aModelName", required = false) String aModelName,
                                    @RequestParam(value = "productName", required = false) String productName,
                                    @RequestParam(value = "folderName", required = false) String folderName,
                                    @RequestParam(value = "folderId", required = false) String folderId,
                                    @RequestParam(value = "status",  required = false) String status,
                                    @RequestParam(value = "start", defaultValue = "0", required = false) String start,
                                    @RequestParam(value = "size", defaultValue = "10", required = false) String size) {
        return abTestService.list(aBTestName, aModelName, productName, folderName, folderId, status, start, size);
    }

    @RequestMapping("/detail")
    @ResponseBody
    public ResponseResult detail(String aBTestId) {
        ABTestDetail detail = abTestService.getDetail(aBTestId);
        if (detail == null) {
            return ResponseResult.createFailInfo("测试不存在");
        }
        return ResponseResult.createSuccessInfo("", detail);
    }

    @RequestMapping("/save")
    @ResponseBody
    public ResponseResult save(ABTestDetail abTestDetail) {
        return abTestService.save(abTestDetail);
    }

    @RequestMapping("/update")
    @ResponseBody
    public ResponseResult update(ABTestDetail abTestDetail) {
        return abTestService.update(abTestDetail);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ResponseResult delete(String aBTestId) {
        return abTestService.delete(aBTestId);
    }

    @RequestMapping("/start")
    @ResponseBody
    public ResponseResult start(String aBTestId) {
        return abTestService.startAndSubmit(aBTestId);
    }

    @RequestMapping("/stop")
    @ResponseBody
    public ResponseResult stop(String aBTestId) {
        return abTestService.stopAndSubmit(aBTestId);
    }

    // 执行测试，模型上线，无缝切换
    @RequestMapping("online")
    @ResponseBody
    public ResponseResult online(String aBTestId) {
        try {
            return abTestService.online(aBTestId);
        } catch (Exception e) {
            logger.error("执行测试失败", e);
            return ResponseResult.createFailInfo("执行测试失败，原因：" + e.getMessage());
        }
    }

}
