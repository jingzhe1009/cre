package com.bonc.frame.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateFormatUtil {

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYYMMDD_PATTERN = "yyyyMMdd";
    public static final Date earliestDate = new Date(0);
    public static final String earliestDateStr = "19700101";

    private final static Map<String, Integer> monthMap;
    private static Pattern withEngPattern = Pattern.compile("^(\\d{2})-([a-z|A-Z]+)-(\\d+)(.*)");
    private static Pattern inputStrPattern = Pattern.compile("^[\\u4E00-\\u9FA5A-Za-z0-9- .:/·•]+$");
    private static Pattern yyyyMMddHHmmssPattern = Pattern.compile("^([1-9]\\d{3})-(([0][1-9])|([1][0-2]))-(([0][1-9])|([1-2]\\d)|([3][0-1]))\\s+([0-1]\\d|[2][0-3]):([0-5]\\d):([0-5]\\d)$");
//    private static Map<String, DateTimeFormatter> formatterMap = Maps.newConcurrentMap();

    static {
        Map<String, Integer> months = Maps.newConcurrentMap();
        for (Month month : Month.values()) {
            months.put(month.name(), month.getValue());
            months.put(month.name().substring(0, 3), month.getValue());
        }
        monthMap = ImmutableMap.copyOf(months);
    }

    /**
     * 格式化日期字符串为规范的‘年月日时分秒’格式
     *
     * @param dateStr 支持的字符串类型
     *                "2016年",
     *                "2016年3月",
     *                "2016年3月5日",
     *                "2016.3.5",
     *                "2016•3",
     *                "2016·3",
     *                "2016.3",
     *                "2016",
     *                "大约2016年",
     *                "大约2016年3月去向不明",
     *                "2016-03-04 12:12:12",
     *                "2016-03 -04",
     *                "12-AUG-17",
     *                "12-AUG-17 12:23:56",
     *                "2016/06/07 12:23:56",
     *                "12-AUG-2005 ",
     *                "12-AUG-2005 12:23:56",
     *                "20160807",
     *                "20160807101254",
     *                "201608"
     * @return
     */
    public static String format(final String dateStr) {
        if (dateStr == null || dateStr.trim().equals("") || dateStr.length() < 4 || !inputStrPattern.matcher(dateStr).matches()) {
            return null;
        }
        //去掉两边的中文字符
        String parsedStr = dateStr.trim().replaceAll("^( *[\\u4e00-\\u9fa5]+ *)+|( *[\\u4e00-\\u9fa5]+ *)+$", "");
        //格式化连接符
        parsedStr = parsedStr.replaceAll(" *年 *| *月 *| *\\. *| */ *| *- *| *· *| *• *", "-").replaceAll(" *日 *| +", " ").replaceAll(" *时 *| *分 *| *: *", ":");
        //'12-AUG-17 12:23:56'格式化
        Matcher withEngmatcher = withEngPattern.matcher(parsedStr);
        if (withEngmatcher.find()) {
            String year = withEngmatcher.group(3);
            Integer month = getMonth(withEngmatcher.group(2));
            if (month == -1) {
                return null;
            }
            String day = withEngmatcher.group(1);
            String hms = withEngmatcher.groupCount() == 4 ? withEngmatcher.group(4) : "";
            if (year.length() == 2) {
                year = "20" + year;
            }
            parsedStr = new StringBuilder(year).append("-").append(month).append("-").append(day).append(hms).toString();
        }
        //去掉连接符并补全占位符'0'
        String[] splits = parsedStr.split("-|:| ");
        if (splits.length > 1) {
            StringBuilder builder = new StringBuilder(splits[0]);
            if (splits[0].length() != 4) {
                return null;
            }
            for (int i = 1; i < splits.length; i++) {
                String split = splits[i];
                if (split.length() > 2) {
                    return null;
                }
                builder.append(split.length() == 1 ? "0" + split : split);
            }
            parsedStr = builder.toString();
        } else {
            //异常字符长度检查避免出现 '2018-003-04'等情况
            if (parsedStr.length() < 4 || parsedStr.length() % 2 != 0) {
                return null;
            }
        }
        int zeroLength = 14 - parsedStr.length();
        if (zeroLength < 0) {
            //长度过长
            return null;
        } else if (zeroLength > 0) {
            //补全占位符至14位
            StringBuilder builder = new StringBuilder(parsedStr);
            if (zeroLength == 10) {
                builder.append("0101");
            } else if (zeroLength == 8) {
                builder.append("01");
            }
            builder.append("000000");
            parsedStr = builder.toString();
        }

        //格式化
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parsedStr.length(); i++) {
            builder.append(parsedStr.charAt(i));
            if (i == 3 || i == 5) {
                builder.append("-");
            } else if (i == 7) {
                builder.append(" ");
            } else if (i == 9 || i == 11) {
                builder.append(":");
            }
        }
        parsedStr = builder.toString();
        if (!yyyyMMddHHmmssPattern.matcher(parsedStr).matches()) {
            return null;
        } else {
            return parsedStr;
        }
    }

    public static Date parse(final String dateStr) throws ParseException {
        String formattedStr = format(dateStr);
        return parse(formattedStr, DEFAULT_PATTERN);
    }

    public static Date parse(final String dateStr, String pattern) throws ParseException {
        if (dateStr == null) {
            throw new IllegalArgumentException("dateStr is null");
        }
        if (pattern == null) {
            pattern = DEFAULT_PATTERN;
        }

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.parse(dateStr);
    }

    public static String format(final Date date, String pattern)  {
        if (date == null) {
            throw new IllegalArgumentException("date is null");
        }
        if (pattern == null) {
            pattern = DEFAULT_PATTERN;
        }

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    private static int getMonth(final String monthName) {
        if (monthName == null || monthName.trim().equals("")) {
            return -1;
        }
        if (monthMap.containsKey(monthName)) {
            return monthMap.get(monthName);
        }
        for (String name : monthMap.keySet()) {
            if (name.equalsIgnoreCase(monthName)) {
                return monthMap.get(name);
            }
        }
        return -1;
    }

    public static String fromatDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isBlank(pattern)) {
            pattern = DEFAULT_PATTERN;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String fromatDate(Date date) {
        return fromatDate(date, DEFAULT_PATTERN);
    }

    public static Date addDay(Date date, int amount) {
        Calendar calendar   =   new GregorianCalendar();
        calendar.setTime(date);
//        calendar.add(calendar.YEAR, 1);//把日期往后增加一年.整数往后推,负数往前移动
//        calendar.add(calendar.DAY_OF_MONTH, 1);//把日期往后增加一个月.整数往后推,负数往前移动
        calendar.add(calendar.DATE,amount);//把日期往后增加一天.整数往后推,负数往前移动
//        calendar.add(calendar.WEEK_OF_MONTH, 1);//把日期往后增加一个月.整数往后推,负数往前移动
        date=calendar.getTime();   //这个时间就是日期往后推一天的结果
        return date;
    }

    public static Date tomorrow(Date date) {
        return addDay(date, 1);
    }

    public static Date yestarday(Date date) {
        return addDay(date, -1);
    }

    public static long distanceDays(Date begin, Date end) {
        long day = (end.getTime()-begin.getTime()) / (24*60*60*1000);
        return day;
    }

    public static void main(String[] args) throws Exception {
        /*Date date = new Date();
        System.out.println(date);
        System.out.println(date.hashCode());
        date = DateFormatUtil.tomorrow(date);
        System.out.println(date);
        System.out.println(date.hashCode());*/

        Date d1009 = parse("2020-10-09 00:00:00");
        Date d100910 = parse("2020-10-09 10:00:00");
        Date d1010 = parse("2020-10-10 10:00:00");
        System.out.println(distanceDays(d1009, d100910));
        System.out.println(distanceDays(d100910, d1009));
        System.out.println(distanceDays(d1009, d1010));
        System.out.println(distanceDays(d1010, d1009));
    }



}
