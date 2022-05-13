package com.bonc.frame.service.impl.monitor;

import com.bonc.frame.dao.DaoHelper;
import com.bonc.frame.entity.monitor.DescResult;
import com.bonc.frame.entity.monitor.ExcuteResult;
import com.bonc.frame.entity.monitor.MonitorParam;
import com.bonc.frame.service.monitor.MonitorService;

import com.bonc.frame.util.*;
import com.bonc.framework.util.DateUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("monitorService")
public class MonitorServiceImpl implements MonitorService {

    @Autowired
    private DaoHelper daoHelper;

    private final String _MONITOR_MANAGE = "com.bonc.frame.mapper.monitorMapper.";



    /**
     * 监控-日志检索-分页展示日志数据
     * @param param 筛选参数
     * @return 日志数据
     */
    @Override
    public Map<String, Object> getLogPage(MonitorParam param,String start,String length) {
        Map<String, Object> map = daoHelper.queryForPageList(_MONITOR_MANAGE + "getLogPage",
                param, start, length);
        // Object data = map.get("data");
        return map;
    }

    /**
     * 获取日志详情
     * @param logId 日志id
     * @return 详情信息
     */
    @Override
    public DescResult getRuleLogDesc(String logId) {
        List<DescResult> list = daoHelper.queryForList(_MONITOR_MANAGE + "getRuleLogDesc", logId);
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }
    //调用分析-响应时间
    @Override
    public Map<String, Object> getUseTime(@RequestBody MonitorParam monitorParam) {
        List<String> titleList = getTitle(monitorParam.getCycleId(), "titleList");
        Map<String, Object> map = getUseTimeData(monitorParam.getCycleId(),monitorParam.getModelId());
		map.put("titleData", titleList.toArray());
		return map;

    }

    /**
     * @param flag 年月日标记
     * @param modelId 模型id
     * @return
     */
    private Map<String,Object> getUseTimeData(String flag,String modelId){
            Map<String, Object> map = new HashMap<>();
            List<String> useTimeList = new ArrayList<>();
            List<String> timeList = new ArrayList<>();
            String path;
            String date; // 当前时间
            if ("1".equals(flag)) { // 按年查询-月时间点暂定格式为：202201
                SimpleDateFormat sd = new SimpleDateFormat("yyyy");
                Date da = new Date();
                date = sd.format(da);
                path = "getYearUseTime";
                for (int i = 1; i <=12 ; i++) {
                    timeList.add(date+String.format("%02d", i));
                }
            } else if ("2".equals(flag)) { // 按月查询-日时间点暂定格式为：20220101
                path = "getDayUseTime";
                SimpleDateFormat sd = new SimpleDateFormat("yyyyMM");
                Date da = new Date();
                date = sd.format(da);
                int length;
                String str = date.substring(3, 5);
                switch (str) {
                    case "02":
                        length = 28;
                    case "04":
                    case "06":
                    case "09":
                    case "11":
                        length = 30;
                    default:
                        length = 31;
                }
                for (int i = 1; i <=length ; i++) {
                    timeList.add(date+String.format("%02d", i));
                }
            } else {// 按日查询-小时时间点暂定格式为：2022010100
                date = DateUtil.getDate(DateFormatUtil.YYYYMMDD_PATTERN);
                path = "getHourUseTime";
                for (int i = 0; i <=23 ; i++) {
                    timeList.add(date+String.format("%02d", i));
                }
            }
            for (String s : timeList) {
                HashMap<String, String> param = new HashMap<>();
                param.put("timePoint", s);
                param.put("date", date);
                param.put("modelId", modelId);
                // sql返回响应时间键值对，解析
                List<Map<String, Object>> result = daoHelper.queryForList(_MONITOR_MANAGE + path, param);
                result.remove(null);
                if (result.size() != 1) {
                    useTimeList.add("0");
                    continue;
                }
                Object succ = result.get(0).get("AVERAGEUSETIME");
                int use = Integer.parseInt(succ.toString());
                // 统计到集合中
                useTimeList.add(Integer.toString(use));
            }
            map.put("useTime",useTimeList.toArray());
            return map;
    }

    /**
     * 标题
     * @param flag
     * @param method
     * @return
     */
    public List<String> getTitle(String flag,String method){
        List<String> resultList = new ArrayList<>();
        if("1".equals(flag)) {//年
            for(int i=0;i<12;i++) {
                if("titleList".equals(method)) {//标题
                    resultList.add((i+1)+"月");
                }
            }

        }else if("2".equals(flag)) {//月
            SimpleDateFormat sd = new SimpleDateFormat("yyyyMM");
            Date da = new Date();
            String date = sd.format(da);
            int length;
            String str = date.substring(3, 5);
            switch (str) {
                case "02":
                    length = 28;
                case "04":
                case "06":
                case "09":
                case "11":
                    length = 30;
                default:
                    length = 31;
            }
            for(int i=0;i<length;i++) {
                if("titleList".equals(method)) {//标题
                    resultList.add((i+1)+"日");
                }
            }
        }else if("3".equals(flag)) {//日
            for(int i=0;i<24;i++) {
                if("titleList".equals(method)) {//标题
                    resultList.add((i+1)+"时");
                }
            }
        }
        return resultList;
    }

    //调用分析-模型执行情况
    @Override
    public Map<String, Object> excuteState(@RequestBody MonitorParam monitorParam){
        List<String> titleList = getTitle(monitorParam.getCycleId(), "titleList");

        Map<String, Object> map = getCountData(monitorParam.getCycleId(),monitorParam.getModelId());
        map.put("titleData", titleList.toArray());
        return map;
    }

    /**
     * 获取模型分析页面的数据
     * @param flag 年月日标记
     * @param modelId 模型id
     * @return
     */
    private Map<String,Object> getCountData(String flag,String modelId){

        Map<String, Object> map = new HashMap<>();
        List<String> allList = new ArrayList<>();
        List<String> successList = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        String path;
        String date; // 当前时间
        if ("1".equals(flag)) { // 按年查询-月时间点暂定格式为：202201
            SimpleDateFormat sd = new SimpleDateFormat("yyyy");
            Date da = new Date();
            date = sd.format(da);
            path = "getYearCountData";
            for (int i = 1; i <=12 ; i++) {
                timeList.add(date+String.format("%02d", i));
            }
        } else if ("2".equals(flag)) { // 按月查询-日时间点暂定格式为：20220101
            path = "getDayCountData";
            SimpleDateFormat sd = new SimpleDateFormat("yyyyMM");
            Date da = new Date();
            date = sd.format(da);
            int length;
            String str = date.substring(3, 5);
            switch (str) {
                case "02":
                    length = 28;
                case "04":
                case "06":
                case "09":
                case "11":
                    length = 30;
                default:
                    length = 31;
            }
            for (int i = 1; i <=length ; i++) {
                timeList.add(date+String.format("%02d", i));
            }
        } else {// 按日查询-小时时间点暂定格式为：2022010100
            date = DateUtil.getDate(DateFormatUtil.YYYYMMDD_PATTERN);
            path = "getHourCountData";
            for (int i = 0; i <=23 ; i++) {
                timeList.add(date+String.format("%02d", i));
            }
        }
        for (String s : timeList) {
            HashMap<String, String> param = new HashMap<>();
            param.put("timePoint", s);
            param.put("date", date);
            param.put("modelId", modelId);
            // sql返回成功次数与失败次数的键值对，解析后计算
            List<Map<String, Object>> result = daoHelper.queryForList(_MONITOR_MANAGE + path, param);
            result.remove(null);
            if (result.size() != 1) {
                allList.add("0");
                successList.add("0");
                failList.add("0");
                continue;
            }
            Object succ = result.get(0).get("SUCCESSCOUNT");
            int su = Integer.parseInt(succ.toString());
            Object fail = result.get(0).get("FAILCOUNT");
            int fa = Integer.parseInt(fail.toString());
            int total= su+fa;
            // 统计到集合中
            allList.add(Integer.toString(total));
            successList.add(Integer.toString(su));
            failList.add(Integer.toString(fa));
        }
        map.put("allData", allList.toArray());
        map.put("failData", failList.toArray());
        map.put("successData", successList.toArray());
        return map;
    }

    //调用分析-执行结果
    @Override
    public Map<String, Object> excuteResult(MonitorParam monitorParam) {
        List<ExcuteResult> resultList = getExcuteResult(monitorParam.getCycleId(), monitorParam.getModelId());
        Map<String, Object> map = new HashMap<>();
        map.put("data",resultList);
        return map;
    }

    private List<ExcuteResult> getExcuteResult(String flag,String modelId){
        String path;
        String date; // 当前时间
        if ("1".equals(flag)) { // 按年查询-月时间点暂定格式为：202201
            SimpleDateFormat sd = new SimpleDateFormat("yyyy");
            Date da = new Date();
            date = sd.format(da);
            path = "getYearResult";
        } else if ("2".equals(flag)) { // 按月查询-日时间点暂定格式为：20220101
            path = "getDayResult";
            SimpleDateFormat sd = new SimpleDateFormat("yyyyMM");
            Date da = new Date();
            date = sd.format(da);
        } else {// 按日查询-小时时间点暂定格式为：2022010100
            date = DateUtil.getDate(DateFormatUtil.YYYYMMDD_PATTERN);
            path = "getHourResult";
        }
        HashMap<String, String> param = new HashMap<>();
        param.put("date", date);
        param.put("modelId", modelId);
        // sql返回成功次数与失败次数的键值对，解析后计算
        List<Map<String, Object>> result = daoHelper.queryForList(_MONITOR_MANAGE + path, param);
        result.remove(null);
        List<ExcuteResult> list = new ArrayList<>();
        if (result.size()!= 1) {
            list.add(new ExcuteResult("执行成功","0000","0"));
            list.add(new ExcuteResult("hbase错误", "9998", "0"));
            list.add(new ExcuteResult("规则引擎错误", "9999", "0"));
            list.add(new ExcuteResult("oracle错误", "9997", "0"));
            list.add(new ExcuteResult("红色接口错误", "9996", "0"));
            return list;
        }
        Object succ = result.get(0).get("SUCCESSCODECOUNT");
        Object hbace = result.get(0).get("HBASECODECOUNT");
        Object system = result.get(0).get("SYSTEMCODECOUNT");
        Object oracle = result.get(0).get("ORACLECODECOUNT");
        Object redInter = result.get(0).get("REDINTERCODECOUNT");
        list.add(new ExcuteResult("执行成功","0000",succ.toString()));
        list.add(new ExcuteResult("hbase错误", "9998", hbace.toString()));
        list.add(new ExcuteResult("规则引擎错误", "9999", system.toString()));
        list.add(new ExcuteResult("oracle错误", "9997", oracle.toString()));
        list.add(new ExcuteResult("红色接口错误", "9996", redInter.toString()));
        return list;
    }



    //调用分析-执行统计
    @Override
    public Map<String, Object> getExcuteCount(MonitorParam monitorParam) {
        List<String> pieList = new ArrayList<String>();
        List<String> titleList = new ArrayList<>();
        List<ExcuteResult> resultList = getExcuteResult(monitorParam.getCycleId(), monitorParam.getModelId());
        for(ExcuteResult data:resultList) {
            pieList.add(data.getExcuteTimes());
            titleList.add(data.getExcuteResult());
        }
        Map<String, Object> map = new HashMap<>();
        //饼图占比
        map.put("pieList", pieList.toArray());
        //饼图标题
        map.put("titleList", titleList.toArray());
        return map;
    }

    //模型分析-模型规则命中率
    @Override
    public Map<String, Object> ruleHit(@RequestBody MonitorParam monitorParam) {
        List<String> titleList = getTitle(monitorParam.getCycleId(), "titleList");
        Map<String, Object> map = getRuleHitCountData(monitorParam.getCycleId(),monitorParam.getModelId());
        map.put("titleData", titleList.toArray());
        return map;
    }

    private Map<String,Object> getRuleHitCountData(String flag,String modelId){
        Map<String, Object> map = new HashMap<>();
        List<String> successList = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        String path;
        String date; // 当前时间
        if ("1".equals(flag)) { // 按年查询-月时间点暂定格式为：202201
            SimpleDateFormat sd = new SimpleDateFormat("yyyy");
            Date da = new Date();
            date = sd.format(da);
            path = "getMonthHitCountData";
            for (int i = 1; i <=12 ; i++) {
                timeList.add(date+String.format("%02d", i));
            }
        } else if ("2".equals(flag)) { // 按月查询-日时间点暂定格式为：20220101
            path = "getDayHitCountData";
            SimpleDateFormat sd = new SimpleDateFormat("yyyyMM");
            Date da = new Date();
            date = sd.format(da);
            int length;
            String str = date.substring(3, 5);
            switch (str) {
                case "02":
                    length = 28;
                case "04":
                case "06":
                case "09":
                case "11":
                    length = 30;
                default:
                    length = 31;
            }
            for (int i = 1; i <=length ; i++) {
                timeList.add(date+String.format("%02d", i));
            }
        } else {// 按日查询-小时时间点暂定格式为：2022010100
            date = DateUtil.getDate(DateFormatUtil.YYYYMMDD_PATTERN);
            path = "getHourHitCountData";
            for (int i = 0; i <=23 ; i++) {
                timeList.add(date+String.format("%02d", i));
            }
        }
        for (String s : timeList) {
            HashMap<String, String> param = new HashMap<>();
            param.put("timePoint", s);
            param.put("date", date);
            param.put("modelId", modelId);
            // sql返回成功次数与失败次数的键值对，解析后计算
            List<Map<String, Object>> result = daoHelper.queryForList(_MONITOR_MANAGE + path, param);
            result.remove(null);
            if (result.size() != 1) {
                successList.add("0");
                failList.add("0");
                continue;
            }
            Object succ = result.get(0).get("SUCCESSCOUNT");
            int su = Integer.parseInt(succ.toString());
            Object fail = result.get(0).get("FAILCOUNT");
            int fa = Integer.parseInt(fail.toString());
            // 统计到集合中
            successList.add(Integer.toString(su));
            failList.add(Integer.toString(fa));
        }
        map.put("failData", failList.toArray());
        map.put("successData", successList.toArray());
        return map;
    }

    //模型分析-模型评分命中率
    @Override
    public Map<String,Object> scoreHit(@RequestBody MonitorParam monitorParam) {
        List<String> titleList = getTitle(monitorParam.getCycleId(), "titleList");
        Map<String, Object> map =getScoreHit(monitorParam.getCycleId(),monitorParam.getModelId());
        map.put("titleData", titleList.toArray());
        return map;
    }

    private Map<String,Object> getScoreHit(String flag,String modelId){
        Map<String, Object> map = new HashMap<>();
        List<String> scoreHitList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        String path;
        String date; // 当前时间
        if ("1".equals(flag)) { // 按年查询-月时间点暂定格式为：202201
            SimpleDateFormat sd = new SimpleDateFormat("yyyy");
            Date da = new Date();
            date = sd.format(da);
            path = "getMonthScoreHit";
            for (int i = 1; i <=12 ; i++) {
                timeList.add(date+String.format("%02d", i));
            }
        } else if ("2".equals(flag)) { // 按月查询-日时间点暂定格式为：20220101
            path = "getDayScoreHit";
            SimpleDateFormat sd = new SimpleDateFormat("yyyyMM");
            Date da = new Date();
            date = sd.format(da);
            int length;
            String str = date.substring(3, 5);
            switch (str) {
                case "02":
                    length = 28;
                case "04":
                case "06":
                case "09":
                case "11":
                    length = 30;
                default:
                    length = 31;
            }
            for (int i = 1; i <=length ; i++) {
                timeList.add(date+String.format("%02d", i));
            }
        } else {// 按日查询-小时时间点暂定格式为：2022010100
            date = DateUtil.getDate(DateFormatUtil.YYYYMMDD_PATTERN);
            path = "getHourScoreHit";
            for (int i = 0; i <=23 ; i++) {
                timeList.add(date+String.format("%02d", i));
            }
        }
        for (String s : timeList) {
            HashMap<String, String> param = new HashMap<>();
            param.put("timePoint", s);
            param.put("date", date);
            param.put("modelId", modelId);
            // sql返回成绩的评分键值对，解析后计算
            List<Map<String, Object>> result = daoHelper.queryForList(_MONITOR_MANAGE + path, param);
            result.remove(null);
            if (result.size() != 1) {
                scoreHitList.add("0");
                continue;
            }
            Object succ = result.get(0).get("SCORECOUNT");
            int su = Integer.parseInt(succ.toString());
            // 统计到集合中
            scoreHitList.add(Integer.toString(su));
        }
        map.put("scoreData", scoreHitList.toArray());
        return map;
    }


    //导出excel
    @Override
    public void exportExcel(HttpServletResponse response, HttpServletRequest request) {
        String cycleId = request.getParameter("cycleId");
        String tabId = request.getParameter("tabId");
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row headRow = sheet.createRow(0);//创建第一行
        List<String> titleList = getTitle(cycleId, "titleList");
        Cell headCell = headRow.createCell(0);
        headCell.setCellValue("数据统计");
        for(int i=0;i<titleList.size();i++) {
            String tile = titleList.get(i);
            Cell tmpCell = headRow.createCell(i+1);//创建第一行的第一个单元格
            tmpCell.setCellValue(tile);
        }

        Map<String, Object> map = excuteState(new MonitorParam(cycleId));
        Object allData = map.get("allData");
        Object failData = map.get("failData");
        Object successData = map.get("successData");
        List<String> allList = HAUtils.castList(allData);
        List<String> failList = HAUtils.castList(failData);
        List<String> successList = HAUtils.castList(successData);


        Row headRow1 = sheet.createRow(1);
        Cell headCell1 = headRow1.createCell(0);
        headCell1.setCellValue("总执行次数");
        for(int i=0;i<allList.size();i++) {
            String tile = allList.get(i);
            Cell tmpCell = headRow1.createCell(i+1);
            tmpCell.setCellValue(tile);
        }

        Row headRow2 = sheet.createRow(2);
        Cell headCell2 = headRow2.createCell(0);
        headCell2.setCellValue("执行成功次数");
        for(int i=0;i<successList.size();i++) {
            String tile = successList.get(i);
            Cell tmpCell = headRow2.createCell(i+1);
            tmpCell.setCellValue(tile);
        }

        Row headRow3 = sheet.createRow(3);
        Cell headCell3 = headRow3.createCell(0);
        headCell3.setCellValue("执行失败次数");
        for(int i=0;i<failList.size();i++) {
            String tile = failList.get(i);
            Cell tmpCell = headRow3.createCell(i+1);
            tmpCell.setCellValue(tile);
        }
        Map<String, Object> map2 = getUseTime(new MonitorParam(cycleId));
        Object useTime = map2.get("useTime");
        List<String> useTimeList = HAUtils.castList(useTime);

        Row headRow4 = sheet.createRow(4);
        Cell headCell4 = headRow4.createCell(0);
        headCell4.setCellValue("响应时间");
        for(int i=0;i<useTimeList.size();i++) {
            String tile = useTimeList.get(i);
            Cell tmpCell = headRow4.createCell(i+1);
            tmpCell.setCellValue(tile);
        }

        if(tabId!=null&&"2".equals(tabId)) {

            Map<String, Object> result = ruleHit(new MonitorParam(cycleId));
            Object fail = result.get("failData");
            Object success = result.get("successData");
            List<String> ruleHitSuc = HAUtils.castList(success);
            List<String> ruleHitFail = HAUtils.castList(fail);

            Row headRow5 = sheet.createRow(5);
            Cell headCell5 = headRow5.createCell(0);
            headCell5.setCellValue("规则通过命中率");
            for(int i=0;i<ruleHitSuc.size();i++) {
                String tile = ruleHitSuc.get(i);
                Cell tmpCell = headRow5.createCell(i+1);
                tmpCell.setCellValue(tile);
            }

            Row headRow6 = sheet.createRow(6);
            Cell headCell6 = headRow6.createCell(0);
            headCell6.setCellValue("规则拒绝命中率");
            for(int i=0;i<ruleHitFail.size();i++) {
                String tile = ruleHitFail.get(i);
                Cell tmpCell = headRow6.createCell(i+1);
                tmpCell.setCellValue(tile);
            }

            Map<String, Object> scoreHit = scoreHit(new MonitorParam(cycleId));
            Object scoreData = scoreHit.get("scoreData");
            List<String> scoreList = HAUtils.castList(scoreData);

            Row headRow7 = sheet.createRow(7);
            Cell headCell7 = headRow7.createCell(0);
            headCell7.setCellValue("评分命中率");
            for(int i=0;i<scoreList.size();i++) {
                String tile = scoreList.get(i);
                Cell tmpCell = headRow7.createCell(i+1);
                tmpCell.setCellValue(tile);
            }
        }
        downLoadExcel(response, workbook);
    }

    //调用分析-数据下载
    private void downLoadExcel(HttpServletResponse response, Workbook workbook){
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            response.reset();
            String fileName = "excel"+new SimpleDateFormat("yyyy-MM-dd").format(new Date()) +".xlsx";
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Cache-Control", "no-cache");
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //调用分析-数据下载
    @Override
    public Map<String, Object> desc(MonitorParam monitorParam) {
        Map<String, Object> map = new HashMap<>();
        List<String> titleList = getTitle(monitorParam.getCycleId(), "titleList");

        Map<String, Object> map1 = getCountData(monitorParam.getCycleId(),monitorParam.getModelId());
        Object allData = map1.get("allData");
        Object failData = map1.get("failData");
        Object successData = map1.get("successData");
        List<String> allList = HAUtils.castList(allData);
        List<String> failList = HAUtils.castList(failData);
        List<String> successList = HAUtils.castList(successData);

        Map<String, Object> result = getRuleHitCountData(monitorParam.getCycleId(),monitorParam.getModelId());
        Object fail = result.get("failData");
        Object success = result.get("successData");
        List<String> ruleHitSuc = HAUtils.castList(success);
        List<String> ruleHitFail = HAUtils.castList(fail);

        Map<String, Object> scoreHit = getScoreHit(monitorParam.getCycleId(),monitorParam.getModelId());
        Object scoreData = scoreHit.get("scoreData");
        List<String> scoreList = HAUtils.castList(scoreData);

        Map<String, Object> map2 =getUseTimeData(monitorParam.getCycleId(),monitorParam.getModelId());
        Object useTime = map2.get("useTime");
        List<String> useTimeList = HAUtils.castList(useTime);

        map.put("titleData", titleList.toArray());
        map.put("allData", allList.toArray());
        map.put("failData", failList.toArray());
        map.put("successData", successList.toArray());
        map.put("successData", ruleHitSuc.toArray());
        map.put("failData", ruleHitFail.toArray());
        map.put("scoreData", scoreList.toArray());
        map.put("useTime", useTimeList.toArray());
        return map;
    }
}
