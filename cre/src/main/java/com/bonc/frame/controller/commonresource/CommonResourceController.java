package com.bonc.frame.controller.commonresource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 公共资源池
 *
 * @author yedunyao
 * @date 2019/5/9 16:26
 */
@Controller
@RequestMapping("/common/resource")
public class CommonResourceController {

    @RequestMapping("/view")
    public String view(String idx, String childOpen, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);
        return "/pages/commonResource/resource";
    }

}
