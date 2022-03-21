package com.bonc.frame.controller.analysis;


import com.bonc.frame.entity.analysis.ModelExecuteCountInfo;
import com.bonc.frame.entity.analysis.ModelExecuteStatistics;
import com.bonc.frame.entity.analysis.ModelStatisticsInfo;
import com.bonc.frame.entity.analysis.TaskStatusStatisInfo;
import com.bonc.frame.service.analysis.AnalysisService;
import com.bonc.frame.service.workbench.WorkbenchService;
import com.bonc.frame.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private WorkbenchService workbenchService;

    // 统计所有模型的执行信息
    @RequestMapping("/allModelExecuteStatis")
    @ResponseBody
    public ResponseResult allmodelExecuteStatis(@RequestParam(defaultValue = "14") int day) {
        ModelExecuteCountInfo modelLogCount = analysisService.allmodelExecuteStatis(day, null, null);
        return ResponseResult.createSuccessInfo("", modelLogCount);
    }

    // 七天以内被调用排名前三的规则
    @RequestMapping("/allModelExecuteTopThree")
    @ResponseBody
    public ResponseResult allmodelExecuteTopThree(@RequestParam(defaultValue = "14") int day, @RequestParam(defaultValue = "3") String size) {
        Map<String, Object> results = analysisService.allmodelExecuteTopThree(day, size);
        return ResponseResult.createSuccessInfo("", results);
    }

    // 所有模型的信息统计 评分模型和版本,规则模型和版本 的数量
    @RequestMapping("/allModelStatis")
    @ResponseBody
    public ResponseResult allmodelStatis() {
        List<ModelStatisticsInfo> modelStatisticsInfos = analysisService.allmodelStatis();
        return ResponseResult.createSuccessInfo("", modelStatisticsInfos);
    }

    //所有模型的状态统计 启用/未启用/正在运行
    @RequestMapping("/allModelStatusStatis")
    @ResponseBody
    public ResponseResult allModelStatusStatis() {
        List<ModelStatisticsInfo> modelStatisticsInfoList = analysisService.allModelStatusStatis();
        return ResponseResult.createSuccessInfo("", modelStatisticsInfoList);
    }

    //统计所有离线任务的状态
    @RequestMapping("/taskStatusStatis")
    @ResponseBody
    public ResponseResult taskStatusStatis() {
        return ResponseResult.createSuccessInfo("", analysisService.taskStatusStatis());
    }

    //获取{day}天内 每天的规则命中统计
    @RequestMapping("/modelExecuteHitRateByDays")
    @ResponseBody
    private ResponseResult selectHitRateByDays(@RequestParam(defaultValue = "14") int day, String folderId, String deptId) {

        List<Map<String, Object>> hitRateList = workbenchService.selectHitRateByDays(day, folderId, deptId);

        List<Map<String, Object>> results = new ArrayList<>();
        //FIXME : 这里的遍历放到SQL中
        for (int i = day - 1; i >= 0; i--) {
            boolean flag = false;
            for (Map<String, Object> param : hitRateList) {
                if (getDelayDay(-i).equals(param.get("time"))) {
                    results.add(param);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                results.add(Collections.<String, Object>emptyMap());
            }
        }
        return ResponseResult.createSuccessInfo("", results);
    }

    //统计规则执行情况  day从当前起的天数
    @RequestMapping("/ruleExecuteNumByFolderIdDeptId")
    @ResponseBody
    public Map<String, Object> ruleExecuteNum(int day, String folderId, String deptId, String state) {
        List<Map<String, Object>> ruleExecuteAll = this.workbenchService.ruleExecuteGroupByTimeAndState(day, folderId, deptId, state);
        List<String> allList = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        List<String> successList = new ArrayList<>();
        Map<Object, Map<Object, Object>> tmpMap = new HashMap<>(day + 1); // time:{state:num}
        for (Map<String, Object> ruleExecute : ruleExecuteAll) {
            if (ruleExecute != null) {
                Object time = ruleExecute.get("time");
                Map<Object, Object> timeStateMap = tmpMap.get(time);
                if (timeStateMap == null) {
                    timeStateMap = new HashMap<>(5);
                    tmpMap.put(time, timeStateMap);
                }
                timeStateMap.put(ruleExecute.get("state"), ruleExecute.get("num"));
            }
        }
        for (int i = day - 1; i >= 0; i--) {
            boolean flag = false;
            Map<Object, Object> timeStateMap = tmpMap.get(getDelayDay(-i));
            if (timeStateMap != null && !timeStateMap.isEmpty()) {
                int failnum = Integer.parseInt(timeStateMap.get("-1") == null ? "0" : timeStateMap.get("-1").toString());
                int successNum = Integer.parseInt(timeStateMap.get("2") == null ? "0" : timeStateMap.get("2").toString());
                int allNum = failnum + successNum;
                allList.add(String.valueOf(allNum));
                failList.add(String.valueOf(failnum));
                successList.add(String.valueOf(successNum));
                flag = true;
            }
            if (!flag) {
                allList.add("0");
                failList.add("0");
                successList.add("0");
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("allData", allList);
        map.put("failData", failList);
        map.put("successData", successList);
        return map;
    }

    /**
     * 获取前几天的日期,返回字符串
     */
    public String getDelayDay(int dayDelay) {
        Date date = new Date();//取时间
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date); //需要将date数据转移到Calender对象中操作
        calendar.add(calendar.DATE, dayDelay);//把日期往后增加n天.正数往后推,负数往前移动
        date = calendar.getTime();   //这个时间就是日期往后推一天的结果
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        return df.format(date).toString();
    }

    /**
     * 前几天的list如果为空那么填0
     */
    @Deprecated
    public List<String> getDayDataList(List<Map<String, Object>> list, int day) {
        List<String> result = new ArrayList<>();
        for (int i = day - 1; i >= 0; i--) {
            boolean flag = false;
            for (Map<String, Object> param : list) {
                if (getDelayDay(-i).equals(param.get("time"))) {
                    result.add(String.valueOf(param.get("num")));
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                result.add("0");
            }
        }
        return result;
    }


}
