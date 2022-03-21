package com.bonc.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 描述：java正则表达式校验工具
 * 作者：jxw
 * 时间：2014年7月16日22:41:08
 */
public final class RegexUtils {

    /**
     * 验证Email
     *
     * @param email email地址，格式：zhangsan@sina.com，zhangsan@xxx.com.cn，xxx代表邮件服务商
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkEmail(String email) {
        String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
        return Pattern.matches(regex, email);
    }

    /**
     * 验证身份证号码
     *
     * @param idCard 居民身份证号码15位或18位，最后一位可能是数字或字母
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkIdCard(String idCard) {
        String regex = "[1-9]\\d{13,16}[a-zA-Z0-9]{1}";
        return Pattern.matches(regex, idCard);
    }

    /**
     * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
     *
     * @param mobile 移动、联通、电信运营商的号码段
     *               <p>移动的号段：134(0-8)、135、136、137、138、139、147（预计用于TD上网卡）
     *               、150、151、152、157（TD专用）、158、159、187（未启用）、188（TD专用）</p>
     *               <p>联通的号段：130、131、132、155、156（世界风专用）、185（未启用）、186（3g）</p>
     *               <p>电信的号段：133、153、180（未启用）、189</p>
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkMobile(String mobile) {
        String regex = "(\\+\\d+)?1[3458]\\d{9}$";
        return Pattern.matches(regex, mobile);
    }

    /**
     * 验证固定电话号码
     *
     * @param phone 电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447
     *              <p><b>国家（地区） 代码 ：</b>标识电话号码的国家（地区）的标准国家（地区）代码。它包含从 0 到 9 的一位或多位数字，
     *              数字之后是空格分隔的国家（地区）代码。</p>
     *              <p><b>区号（城市代码）：</b>这可能包含一个或多个从 0 到 9 的数字，地区或城市代码放在圆括号——
     *              对不使用地区或城市代码的国家（地区），则省略该组件。</p>
     *              <p><b>电话号码：</b>这包含从 0 到 9 的一个或多个数字 </p>
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkPhone(String phone) {
        String regex = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";
        return Pattern.matches(regex, phone);
    }

    /**
     * 验证整数（正整数和负整数）
     *
     * @param digit 一位或多位0-9之间的整数
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkDigit(String digit) {
        String regex = "\\-?[1-9]\\d+";
        return Pattern.matches(regex, digit);
    }

    /**
     * 验证整数和浮点数（正负整数和正负浮点数）
     *
     * @param decimals 一位或多位0-9之间的浮点数，如：1.23，233.30
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkDecimals(String decimals) {
        String regex = "\\-?[0-9]\\d*(\\.\\d+)?";
        return Pattern.matches(regex, decimals);
    }

    /**
     * 验证空白字符
     *
     * @param blankSpace 空白字符，包括：空格、\t、\n、\r、\f、\x0B
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkBlankSpace(String blankSpace) {
        String regex = "\\s+";
        return Pattern.matches(regex, blankSpace);
    }

    /**
     * 验证中文
     *
     * @param chinese 中文字符
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkChinese(String chinese) {
        String regex = "^[\u4E00-\u9FA5]+$";
        return Pattern.matches(regex, chinese);
    }

    /**
     * 验证日期（年月日）
     *
     * @param birthday 日期，格式：1992-09-03，或1992.09.03
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkBirthday(String birthday) {
        String regex = "[0-9]{4}([-./])\\d{1,2}\\1\\d{1,2}";
        return Pattern.matches(regex, birthday);
    }

    /**
     * 验证日期
     *
     * @param str
     * @param format
     * @return
     */
    public static boolean checkDate(String str, String format) {
        if (format == null || format.isEmpty()) {
            format = "[0-9]{4}([-./])\\d{1,2}\\1\\d{1,2}";
        }
        return Pattern.matches(format, str);
    }

    public static boolean checkDate(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            //e.printStackTrace();
        }
        if (date != null) {
            return true;
        }
        return false;
    }

    /**
     * 验证URL地址
     *
     * @param url 格式：http://blog.csdn.net:80/xyang81/article/details/7705960? 或 http://www.csdn.net:80
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkURL(String url) {
        String regex = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
        return Pattern.matches(regex, url);
    }

    /**
     * 匹配中国邮政编码
     *
     * @param postcode 邮政编码
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkPostcode(String postcode) {
        String regex = "[1-9]\\d{5}";
        return Pattern.matches(regex, postcode);
    }

    /**
     * 匹配IP地址(简单匹配，格式，如：192.168.1.1，127.0.0.1，没有匹配IP段的大小)
     *
     * @param ipAddress IPv4标准地址
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkIpAddress(String ipAddress) {
        String regex = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";
        return Pattern.matches(regex, ipAddress);
    }

    /**
     * 校验字符串长度
     *
     * @param min最小长度 max最大长度
     * @return boolean
     */
    public static boolean checkStringLength(int min, int max, String param) {
        if (param.length() >= 0 && param.length() <= max) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 校验是否为手机号码
     *
     * @param moblies 手机号码
     *                根据实际开发于2009年9月7日最新统计：
     *                中国电信发布中国3G号码段:中国联通185,186;中国移动188,187;中国电信189,180共6个号段。
     *                3G业务专属的180-189号段已基本分配给各运营商使用, 其中180、189分配给中国电信,187、188归中国移动使用,185、186属于新联通。
     *                中国移动拥有号码段：139、138、137、136、135、134、159、158、157（3G）、152、151、150、188（3G）、187（3G）;14个号段
     *                中国联通拥有号码段：130、131、132、155、156（3G）、186（3G）、185（3G）;6个号段
     *                中国电信拥有号码段：133、153、189（3G）、180（3G）;4个号码段
     *                移动:
     *                2G号段(GSM网络)有139,138,137,136,135,134(0-8),159,158,152,151,150
     *                3G号段(TD-SCDMA网络)有157,188,187
     *                147是移动TD上网卡专用号段.
     *                联通:
     *                2G号段(GSM网络)有130,131,132,155,156
     *                3G号段(WCDMA网络)有186,185
     *                电信:
     *                2G号段(CDMA网络)有133,153
     *                3G号段(CDMA网络)有189,180
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 验证字符串是否含有特殊字符
     */
    public static boolean isStringCheck(String str) {
        if (str.replaceAll("[a-z]*[A-Z]*[\u4e00-\u9fa5]*\\d*-*_*\\s*", "").length() == 0)
            return true;
        else
            return false;
    }

    //验证是否为全部数字
    public static boolean checkNumber(String str) {
        //\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\w+?\\w+:\\.[a-z]+(\\.[a-z]+)?");
        java.util.regex.Matcher match = pattern.matcher(str);
        if (match.matches() == false) {
            return false;
        } else {
            return true;
        }
    }

    public static void main(String[] args) {
        String s = "2017-01-03 11:17:a0";
        System.out.println(RegexUtils.checkDate(s));
    }
} 
