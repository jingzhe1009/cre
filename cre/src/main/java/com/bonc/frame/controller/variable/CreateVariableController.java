package com.bonc.frame.controller.variable;

import com.bonc.frame.entity.variable.Variable;
import com.bonc.frame.service.syslog.SysLogService;
import com.bonc.frame.service.variable.VariableService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.JsonUtils;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 作者：limf
 * @date 创建时间：2018年3月22日 上午10:54:20
 * @version 版本： 1.0 说明：新建变量
 */
@Controller
@RequestMapping("/")
public class CreateVariableController {

    @Autowired
    private VariableService variableService;

    @Autowired
    private SysLogService sysLogService;

    @RequestMapping("/variableConfig")
    public String main(String ruleFolder, String variableKind, String childOpen, Model model) {
        List<Map<String, Object>> variableTypeList = this.variableService.getVariableType();
        List<Map<String, Object>> variableKindList = this.variableService.getVariableKind();

        model.addAttribute("variableTypeList", JsonUtils.beanToJson(variableTypeList).toString());
        model.addAttribute("variableKindList", JsonUtils.beanToJson(variableKindList).toString());
        model.addAttribute("ruleFolder", ruleFolder);
        model.addAttribute("variableKind", variableKind);
        model.addAttribute("idx", ruleFolder);
        model.addAttribute("childOpen", childOpen);
        return "/pages/variable/variableConfig";
    }

    @RequestMapping("/insertVariable")
    @ResponseBody
    public ResponseResult insertVariable(Variable variable, HttpServletRequest request) {
        variable.setCreateDate(new Date());
        variable.setCreatePerson(ControllerUtil.getLoginUserId(request));
        ResponseResult result = variableService.insertVariable(variable);
        return result;
    }
}
