package com.bonc.frame.controller.aBTest;

import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yedunyao
 * @since 2020/9/2 18:50
 */
@Controller
@RequestMapping("/ws/mock")
public class MockController {

    private Logger logger = LogManager.getLogger(MockController.class);

    @RequestMapping("/income")
    @ResponseBody
    public Object getIncome(String idcard) {
        logger.debug("模拟接口 获取收入  idcard {}", idcard);
        ImmutableMap<String, Integer> result = ImmutableMap.of("income", 1000);
        return 1000;
    }

    @RequestMapping(value = "/getUserByIdCard", method = RequestMethod.GET)
    @ResponseBody
    public Object getUserByIdCard(String wzb_idCard) {
        if ("001001".equals(wzb_idCard)) {
            Map<String, Object> user = new HashMap<>();
            Map<String, Object> edu = new HashMap<>();
            user.put("wzb_age", 3);
            user.put("wzb_sex", "男");
            user.put("wzb_idCard", "001001");
            user.put("wzb_edu", edu);
            edu.put("wzb_school", "山西大学");
            edu.put("wzb_specialty", "专业");
            return user;
        } else {
            Map<String, Object> user = new HashMap<>();
            Map<String, Object> edu = new HashMap<>();
            user.put("wzb_age", 9999);
            user.put("wzb_sex", "女");
            user.put("wzb_idCard", "1111111");
            user.put("wzb_edu", edu);
            edu.put("wzb_school", "太原理工");
            edu.put("wzb_specialty", "物理专业");
            return user;
        }
    }


}
