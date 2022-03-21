package com.bonc.frame.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yedunyao
 * @date 2019/8/28 11:33
 */
@Controller
@RequestMapping("/error")
public class ErrorController {

    @RequestMapping("/404")
    public String error404() {
        return "/error/404";
    }

    @RequestMapping("/500")
    public String error500() {
        return "/error/500";
    }

    @RequestMapping("/noAuth")
    public String noAuth() {
        return "/error/noAuth";
    }

}
