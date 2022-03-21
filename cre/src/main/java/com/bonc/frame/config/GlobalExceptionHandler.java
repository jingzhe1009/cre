package com.bonc.frame.config;

import com.bonc.frame.security.MyAuthorizationException;
import com.bonc.frame.security.MyRedirectAuthorizationException;
import com.bonc.frame.util.ResponseResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yedunyao on 2018/10/17.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private Log log = LogFactory.getLog(getClass());

    @ExceptionHandler(value = MyRedirectAuthorizationException.class)
    public String authErrorHandler(MyRedirectAuthorizationException e) {
        log.error("没有权限，将进行重定向", e);
        return "redirect:/error/noAuth";
    }

    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public ResponseResult runtimeExceptionHandler(HttpServletRequest req, HttpServletResponse response, RuntimeException e) {
        log.error("服务内部异常", e);
        if (e instanceof MyAuthorizationException) {
            return ResponseResult.createAuthFailInfo(e.getMessage());
        }
        return ResponseResult.createFailInfo(e.getMessage());
    }

    @ExceptionHandler(value = Error.class)
    @ResponseBody
    public ResponseResult errorHanadler(HttpServletRequest req, HttpServletResponse response, RuntimeException e) {
        log.error("服务内部异常", e);
        if (e instanceof MyAuthorizationException) {
            return ResponseResult.createAuthFailInfo(e.getMessage());
        }
        return ResponseResult.createFailInfo(e.getMessage());
    }

}