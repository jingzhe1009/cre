package com.bonc.frame.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/4/24 17:11
 */
public class StringUtil {
    /**
     * 判断字符串是不是数字
     */
    public static boolean stringIsNumber(String str) {
        return str.matches("-?[0-9]+(\\.[0-9]+)?");
    }

    /**
     * toString的字符串转Map<String,String>
     */
    public static Map<String, String> stringToMap(String str) {
        str = str.substring(str.indexOf("{") + 1, str.lastIndexOf("}"));
        char[] array = str.toCharArray();
        Stack<Character> stack = new Stack<>();
        StringBuilder sb = new StringBuilder();
        String property = null;
        String preProperty = null;
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            if ('[' == c || ']' == c || '{' == c || '}' == c || '\'' == c || '"' == c) {
                if (i > 1 && array[i - 1] != '\\') {
                    if ('[' == c) {
                        stack.push(c);
                    } else if (']' == c) {
                        if (!stack.isEmpty() && stack.peek() == '[') {
                            stack.pop();
                        }
                    } else if ('{' == c) {
                        stack.push(c);
                    } else if ('}' == c) {
                        if (!stack.isEmpty() && stack.peek() == '{') {
                            stack.pop();
                        }
                    } else if ('\'' == c) {
                        if (!stack.isEmpty() && stack.peek() == '\'') {
                            stack.pop();
                        } else {
                            stack.push(c);
                        }
                    } else if ('"' == c) {
                        if (!stack.isEmpty() && stack.peek() == '"') {
                            stack.pop();
                        } else {
                            stack.push(c);
                        }
                    }
                }
            }
            if (stack.isEmpty()) {
                if ('=' == c) {
                    if (StringUtils.isBlank(property) && StringUtils.isNotBlank(sb.toString().trim())) {
                        property = sb.toString().trim();
                        sb.delete(0, sb.length());
                    } else {
                        sb.append(c);
                    }
                } else if (',' == c) {
                    if (StringUtils.isNotBlank(property)) {
                        map.put(property, sb.toString().trim());
                        preProperty = property;
                        property = null;
                    } else if (StringUtils.isNotBlank(preProperty)) {
                        map.put(preProperty, map.get(preProperty) + "," + sb.toString().trim());
                    }
                    sb.delete(0, sb.length());
                } else {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }

        }
        if (StringUtils.isNotBlank(property)) {
            map.put(property, sb.toString().trim());
            property = null;
            sb.delete(0, sb.length());
        } else {
            map.put(preProperty, map.get(preProperty) + "," + sb.toString().trim());
            property = null;
            preProperty = null;
            sb.delete(0, sb.length());
        }
        return map;
    }

    public static Map<String, List<String>> stringToList(String str) {
        return stringToList(str, '[', ']',
                '{', true, ',');
    }

    /**
     * @param str
     * @param startIndexChar
     * @param endIndexChar
     * @param keyValueSeparator
     * @param isNeedkeyValueSeparator
     * @param objectSeparator
     * @return
     */
    public static Map<String, List<String>> stringToList(String str, char startIndexChar, char endIndexChar,
                                                         char keyValueSeparator, boolean isNeedkeyValueSeparator,
                                                         char objectSeparator) {
        str = str.substring(str.indexOf(startIndexChar) + 1, str.lastIndexOf(endIndexChar));
        char[] array = str.toCharArray();
        Stack<Character> stack = new Stack<>();
        StringBuilder sb = new StringBuilder();
        String property = null;
        String preProperty = null;
        Map<String, List<String>> map = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            if ('[' == c || ']' == c || '{' == c || '}' == c || '\'' == c || '"' == c) {
                if (i > 1 && array[i - 1] != '\\') {
                    if ('[' == c) {
                        stack.push(c);
                    } else if (']' == c) {
                        if (!stack.isEmpty() && stack.peek() == '[') {
                            stack.pop();
                        }
                    } else if ('{' == c) {
                        stack.push(c);
                    } else if ('}' == c) {
                        if (!stack.isEmpty() && stack.peek() == '{') {
                            stack.pop();
                        }
                    } else if ('\'' == c) {
                        if (!stack.isEmpty() && stack.peek() == '\'') {
                            stack.pop();
                        } else {
                            stack.push(c);
                        }
                    } else if ('"' == c) {
                        if (!stack.isEmpty() && stack.peek() == '"') {
                            stack.pop();
                        } else {
                            stack.push(c);
                        }
                    }
                }
            }

            if (stack.isEmpty() || (c == keyValueSeparator && stack.peek() == c)) {
                if (keyValueSeparator == c) {
                    if (StringUtils.isBlank(property) && StringUtils.isNotBlank(sb.toString().trim())) {
                        property = sb.toString().trim();
                        sb.delete(0, sb.length());
                        if (isNeedkeyValueSeparator) {
                            sb.append(c);
                        }
                    } else {
                        sb.append(c);
                    }
                } else if (objectSeparator == c) {
                    if (StringUtils.isNotBlank(property)) {
                        List<String> strings = map.get(property);
                        if (strings == null) {
                            strings = new ArrayList<>();
                            map.put(property, strings);
                        }
                        strings.add(sb.toString().trim());
                        preProperty = property;
                        property = null;
                    } else if (StringUtils.isNotBlank(preProperty)) {
                        List<String> strings = map.get(preProperty);
                        if (strings != null && !strings.isEmpty()) {
                            String s = strings.get(strings.size() - 1);
                            s = s + objectSeparator + sb.toString().trim();
                            strings.set(strings.size() - 1, s);
                        }
                    }
                    sb.delete(0, sb.length());
                } else {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        if (StringUtils.isNotBlank(property)) {
            List<String> strings = map.get(property);
            if (strings == null) {
                strings = new ArrayList<>();
                map.put(property, strings);
            }
            strings.add(sb.toString().trim());
            preProperty = property;
            property = null;
        } else {
            List<String> strings = map.get(preProperty);
            if (strings != null && !strings.isEmpty()) {
                String s = strings.get(strings.size() - 1);
                s = s + "," + sb.toString().trim();
                strings.set(strings.size() - 1, s);
            }
        }
        return map;
    }


    public static void main(String[] args) {
        String str = "RuleLog{logId='912e9d9bd132487d90c0f34622e14fa3', ruleId='8a8ae4ec745cec8001745cf153490000', folderId='2c90fc187424f117017424f1e66b0000', state='2', hitRuleNum='5', startTime=Sat Sep 05 14:26:36 CST 2020, endTime=Sat Sep 05 14:26:39 CST 2020, inputData='{\"year\":\"2020\",\"idCard\":\"00001\"}', outputData='{\"kpi_api_getAge\":3,\"specialty\":\"专业\",\"current_year\":2018,\"result_int_2\":50,\"year\":\"2020\",\"gsdm\":3,\"school\":\"山西大学\",\"idCard\":\"00001\",\"sex\":\"男\",\"score_int\":23,\"kpi_wzb_getAgeByIdCard\":23,\"age\":3}', exception='null', consumerId='cre_test', serverId='null', consumerSeqNo='null',detail=[RuleLogDetail{id=912e9d9bd132487d90c0f34622e14fa2_rect2, logId=912e9d9bd132487d90c0f34622e14fa3, nodeId=rect2, nodeName=结束, nodeType=end, state=2, startTime=Sat Sep 05 14:26:39 CST 2020, endTime=Sat Sep 05 14:26:39 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=912e9d9bd132487d90c0f34622e14fa2_path13, logId=912e9d9bd132487d90c0f34622e14fa3, nodeId=path13, nodeName=true, nodeType=path, state=2, startTime=Sat Sep 05 14:26:39 CST 2020, endTime=Sat Sep 05 14:26:39 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=912e9d9bd132487d90c0f34622e14fa2_rect5, logId=912e9d9bd132487d90c0f34622e14fa3, nodeId=rect5, nodeName=聚合, nodeType=join, state=2, startTime=Sat Sep 05 14:26:39 CST 2020, endTime=Sat Sep 05 14:26:39 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},RuleLogDetail{id=912e9d9bd132487d90c0f34622e14fa2_path19, logId=912e9d9bd132487d90c0f34622e14fa3, nodeId=path19, nodeName=true, nodeType=path, state=2, startTime=Sat Sep 05 14:26:39 CST 2020, endTime=Sat Sep 05 14:26:39 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null},ApiLog{apiId=2c90fc3b73ebf0360173ec034fb50021, logId=912e9d9bd132487d90c0f34622e14fa3, logContent=null, logOccurtime=Sat Sep 05 14:26:39 CST 2020, callResult={\"specialty\":\"专业\",\"school\":\"山西大学\",\"idCard\":\"001001\",\"sex\":\"男\",\"age\":3}, logParam={\"kpi_api_getAge\":3,\"current_year\":2018,\"result_int_2\":50,\"year\":\"2020\",\"gsdm\":3,\"idCard\":\"00001\",\"score_int\":23,\"kpi_wzb_getAgeByIdCard\":23}, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null}]}, aBTestId=’11111111111111’";
//        Map<String, String> map = stringToMap(str);
//        System.out.println(JSON.toJSONString(map));
        String strList = "[RuleLogDetail{id=912e9d9bd132487d90c0f34622e14fa2_rect2, logId=912e9d9bd132487d90c0f34622e14fa3, nodeId=rect2, nodeName=结束, nodeType=end, state=2, startTime=Sat Sep 05 14:26:39 CST 2020, endTime=Sat Sep 05 14:26:39 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null}," +
                "RuleLogDetail{id=912e9d9bd132487d90c0f34622e14fa2_path19, logId=912e9d9bd132487d90c0f34622e14fa3, nodeId=path19, nodeName=true, nodeType=path, state=2, startTime=Sat Sep 05 14:26:39 CST 2020, endTime=Sat Sep 05 14:26:39 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null}," +
                "ApiLog{apiId=2c90fc3b73ebf0360173ec034fb50021, logId=912e9d9bd132487d90c0f34622e14fa3, logContent=null, logOccurtime=Sat Sep 05 14:26:39 CST 2020, callResult={\\\"specialty\\\":\\\"专业\\\",\\\"school\\\":\\\"山西大学\\\",\\\"idCard\\\":\\\"001001\\\",\\\"sex\\\":\\\"男\\\",\\\"age\\\":3}, logParam={\\\"kpi_api_getAge\\\":3,\\\"current_year\\\":2018,\\\"result_int_2\\\":50,\\\"year\\\":\\\"2020\\\",\\\"gsdm\\\":3,\\\"idCard\\\":\\\"00001\\\",\\\"score_int\\\":23,\\\"kpi_wzb_getAgeByIdCard\\\":23}, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null}," +
                "RuleLogDetail{id=912e9d9bd132487d90c0f34622e14fa2_path19, logId=912e9d9bd132487d90c0f34622e14fa3, nodeId=path19, nodeName=true, nodeType=path, state=2, startTime=Sat Sep 05 14:26:39 CST 2020, endTime=Sat Sep 05 14:26:39 CST 2020, result=true, consumerId=cre_test, serverId=null, consumerSeqNo=null, executionProcess=null}" +
                "]";
        Map<String, List<String>> stringListMap = stringToList(str, '{', '}',
                '=', false, ',');
        Map<String, List<String>> stringListMa1p = stringToList(str, '[', ']',
                '{', true, ',');
        System.out.println(JSON.toJSONString(stringListMap));
        System.out.println(JSON.toJSONString(stringListMa1p));
    }

}
