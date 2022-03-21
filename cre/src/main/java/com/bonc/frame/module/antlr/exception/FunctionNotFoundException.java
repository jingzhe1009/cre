package com.bonc.frame.module.antlr.exception;

/**
 * @author qxl
 * @version 1.0.0
 * @date 2016年12月29日 上午9:27:46
 */
public class FunctionNotFoundException extends Exception {
    public FunctionNotFoundException(String funName) {
        super("The function [" + funName + "] is illegal.");
    }
}
