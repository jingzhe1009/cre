package com.bonc.framework.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author qxl
 * @version 1.0
 * @date 2018年4月11日 上午11:07:52
 */
public class ExceptionUtil {

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try {
            throwable.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }

}
