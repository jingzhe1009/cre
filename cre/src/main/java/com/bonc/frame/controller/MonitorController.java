package com.bonc.frame.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.monitor.DescResult;
import com.bonc.frame.entity.monitor.ExcuteResult;
import com.bonc.frame.entity.monitor.IndexParam;
import com.bonc.frame.entity.monitor.MonitorParam;
import com.bonc.frame.service.UserService;
import com.bonc.frame.service.auth.ChannelService;
import com.bonc.frame.service.modelBase.ModelBaseService;
import com.bonc.frame.service.rule.RuleDetailService;
import com.bonc.frame.service.rule.RuleService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.FolderMenuUtil;

@Controller
@RequestMapping("/monitor")
public class MonitorController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RuleService ruleService;
	
	@Autowired
    ChannelService channelService;
	
	@Autowired
	ModelBaseService modelGroupService;
	
	@Autowired
    private RuleDetailService ruleDetailService;
	
	@RequestMapping({"/","/indexView"})
	public String main(String idx, String childOpen, HttpSession session,Model model) throws Exception {
		FolderMenuUtil.setFolderMenu(ruleService, session);
		if(idx==null || idx.isEmpty()){
			idx = "28";
		}
		IndexParam param = new IndexParam();
		param.setChannelCount(childOpen);
		param.setCountModel(childOpen);
		param.setCurrentDayFailTimes(childOpen);
		param.setCurrentDayResponseTime(childOpen);
		param.setCurrentDaySucTimes(childOpen);
		param.setKpiCount(childOpen);
		param.setProductCount(childOpen);
		param.setRuleModel(childOpen);
		param.setRuleSetCount(childOpen);
		param.setScoreModel(childOpen);
		model.addAttribute("idx", idx);
		model.addAttribute("childOpen", childOpen);
		model.addAttribute("param", param);
	    return "/pages/monitor/indexView";
	}
	
	//调用分析-模型执行情况
	@RequestMapping("/excuteState")
    @ResponseBody
    public Map<String, Object> excuteState(@RequestBody MonitorParam monitorParam) {
		List<String> titleList = getTitle(monitorParam.getCycleId(), "titleList");
		List<String> allList = getList(monitorParam.getCycleId(), "allList");
        List<String> failList = getList(monitorParam.getCycleId(), "failList");
        List<String> successList = getList(monitorParam.getCycleId(), "successList");
        
		Map<String, Object> map = new HashMap<>();
		map.put("titleData", titleList.toArray());
        map.put("allData", allList.toArray());
        map.put("failData", failList.toArray());
        map.put("successData", successList.toArray());
        return map;
	}
	
	//调用分析-响应时间 (模型执行命中率)
	@RequestMapping("/responseTime")
    @ResponseBody
    public Map<String, Object> responseTime(@RequestBody MonitorParam monitorParam) {
		List<String> titleList = getTitle(monitorParam.getCycleId(), "titleList");
		List<String> responseTimeList = getList(monitorParam.getCycleId(), "responseTime");
		Map<String, Object> map = new HashMap<>();
		map.put("titleData", titleList.toArray());
        map.put("responseData", responseTimeList);
        return map;
	}
	
	/**
	 * 通用假数据
	 * @param flag
	 * @param method
	 * @return
	 */
	private List<String> getList(String flag,String method){
		List<String> resultList = new ArrayList<>();
		if("1".equals(flag)) {//年
			for(int i=0;i<12;i++) {
	            if("titleList".equals(method)) {//标题
	            	resultList.add((i+1)+"");
				}else if("allList".equals(method)) {//总执行次数
					resultList.add((i+10)+"");
				}else if("successList".equals(method)) {//成功执行次数
					resultList.add((i+5)+"");
				}else if("failList".equals(method)) {//失败执行次数
					resultList.add((i+3)+"");
				}else if("responseTime".equals(method)) {//响应时间
					resultList.add((i+7)+"");
				}else if("ruleHitSuc".equals(method)) {//规则命中率-通过
					resultList.add((i+7)+"");
				}else if("ruleHitFail".equals(method)) {//规则命中率-拒绝
					resultList.add((i+7)+"");
				}else if("scoreHit".equals(method)) {//评分命中率
					resultList.add((i+7)+"");
				}
	        }
			
		}else if("2".equals(flag)) {//月
			for(int i=0;i<30;i++) {
	            if("titleList".equals(method)) {//标题
	            	resultList.add((i+1)+"");
				}else if("allList".equals(method)) {//总执行次数
					resultList.add((i+10)+"");
				}else if("successList".equals(method)) {//成功执行次数
					resultList.add((i+5)+"");
				}else if("failList".equals(method)) {//失败执行次数
					resultList.add((i+3)+"");
				}else if("responseTime".equals(method)) {//响应时间
					resultList.add((i+7)+"");
				}else if("ruleHitSuc".equals(method)) {//规则命中率-通过
					resultList.add((i+7)+"");
				}else if("ruleHitFail".equals(method)) {//规则命中率-拒绝
					resultList.add((i+7)+"");
				}else if("scoreHit".equals(method)) {//评分命中率
					resultList.add((i+7)+"");
				}
	        }
		}else if("3".equals(flag)) {//日
			for(int i=0;i<24;i++) {
	            if("titleList".equals(method)) {//标题
	            	resultList.add((i+1)+"");
				}else if("allList".equals(method)) {//总执行次数
					resultList.add((i+10)+"");
				}else if("successList".equals(method)) {//成功执行次数
					resultList.add((i+5)+"");
				}else if("failList".equals(method)) {//失败执行次数
					resultList.add((i+3)+"");
				}else if("responseTime".equals(method)) {//响应间
					resultList.add((i+7)+"");
				}else if("ruleHitSuc".equals(method)) {//规则命中率-通过
					resultList.add((i+7)+"");
				}else if("ruleHitFail".equals(method)) {//规则命中率-拒绝
					resultList.add((i+7)+"");
				}else if("scoreHit".equals(method)) {//评分命中率
					resultList.add((i+7)+"");
				}
	        }
		}
		return resultList;
	}
	/**
	 * 标题
	 * @param flag
	 * @param method
	 * @return
	 */
	private List<String> getTitle(String flag,String method){
		List<String> resultList = new ArrayList<>();
		if("1".equals(flag)) {//年
			for(int i=0;i<12;i++) {
	            if("titleList".equals(method)) {//标题
	            	resultList.add((i+1)+"月");
				}
	        }
			
		}else if("2".equals(flag)) {//月
			for(int i=0;i<30;i++) {
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
	
	//调用分析-执行统计
	@RequestMapping("/excuteCount")
    @ResponseBody
    public Map<String, Object> excuteCount(@RequestBody MonitorParam monitorParam) {
		List<String> pieList = new ArrayList<String>();
        List<String> titleList = new ArrayList<>();
        
        List<ExcuteResult> resultList = getData();
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
	
	//调用分析-执行结果
	@RequestMapping(value="/excuteResult",method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> excuteResult(MonitorParam monitorParam) {
		
		List<ExcuteResult> resultList = getData();
		Map<String, Object> map = new HashMap<>();
        map.put("data", resultList);
        return map;
	}
	
	//调用分析-模型规则命中率
	@RequestMapping("/ruleHit")
    @ResponseBody
    public Map<String, Object> ruleHit(@RequestBody MonitorParam monitorParam) {
        List<String> successList = getList(monitorParam.getCycleId(), "ruleHitSuc");
        List<String> failList = getList(monitorParam.getCycleId(), "ruleHitFail");
        List<String> titleList = getTitle(monitorParam.getCycleId(), "titleList");
		Map<String, Object> map = new HashMap<>();
		map.put("titleData", titleList.toArray());
        map.put("failData", failList.toArray());
        map.put("successData", successList.toArray());
        return map;
	}
	
	//调用分析-模型评分命中率
	@RequestMapping("/scoreHit")
    @ResponseBody
    public Map<String, Object> scoreHit(@RequestBody MonitorParam monitorParam) {
        List<String> scoreList = getList(monitorParam.getCycleId(), "scoreHit");
        List<String> titleList = getTitle(monitorParam.getCycleId(), "titleList");
		Map<String, Object> map = new HashMap<>();
		map.put("titleData", titleList.toArray());
        map.put("scoreData", scoreList.toArray());
        return map;
	}
	
	//调用分析-数据下载
	@RequestMapping("/desc")
    @ResponseBody
    public Map<String, Object> desc(@RequestBody MonitorParam monitorParam) {
		List<String> titleList = getTitle(monitorParam.getCycleId(), "titleList");
		List<String> allList = getList(monitorParam.getCycleId(), "allList");
        List<String> failList = getList(monitorParam.getCycleId(), "failList");
        List<String> successList = getList(monitorParam.getCycleId(), "successList");
        
        List<String> responseTimeList = getList(monitorParam.getCycleId(), "responseTime");
        List<String> ruleHitSuc = getList(monitorParam.getCycleId(), "ruleHitSuc");
        List<String> ruleHitFail = getList(monitorParam.getCycleId(), "ruleHitFail");
        List<String> scoreList = getList(monitorParam.getCycleId(), "scoreHit");
        
        DescResult desc = new DescResult("1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1");
        
		Map<String, Object> map = new HashMap<>();
		map.put("titleData", titleList.toArray());
        map.put("allData", allList.toArray());
        map.put("failData", failList.toArray());
        map.put("successData", successList.toArray());
        map.put("ruleHitSucData", ruleHitSuc.toArray());
        map.put("ruleHitFailData", ruleHitFail.toArray());
        map.put("scoreData", scoreList.toArray());
        map.put("responseData", responseTimeList.toArray());
        map.put("desc", desc);
        return map;
	}
	
	/**
	 * 执行统计和执行结果造的假数据
	 * @return
	 */
	private List<ExcuteResult> getData(){
		List<ExcuteResult> resultList = new ArrayList<ExcuteResult>();
		ExcuteResult data = new ExcuteResult();
		data.setExcuteResult("执行成功");
		data.setExcuteTimes("1000");
		data.setStatusCode("0000");
		resultList.add(data);
		ExcuteResult data2 = new ExcuteResult();
		data2.setExcuteResult("hbase错误");
		data2.setExcuteTimes("20");
		data2.setStatusCode("9998");
		resultList.add(data2);
		ExcuteResult data3 = new ExcuteResult();
		data3.setExcuteResult("oracle错误");
		data3.setExcuteTimes("10");
		data3.setStatusCode("9997");
		resultList.add(data3);
		ExcuteResult data4 = new ExcuteResult();
		data4.setExcuteResult("红色接口错误");
		data4.setExcuteTimes("2");
		data4.setStatusCode("9996");
		resultList.add(data4);
		ExcuteResult data5 = new ExcuteResult();
		data5.setExcuteResult("规则引擎错误");
		data5.setExcuteTimes("12");
		data5.setStatusCode("9999");
		resultList.add(data5);
		return resultList;
	}
	
	@RequestMapping("/channelName")
    @ResponseBody
    public List<Map<String, Object>> getChannelNameList(HttpServletRequest request) {
        String groupId = ControllerUtil.getParam(request, "groupId");
        List<Map<String, Object>> ruleFolderList = this.channelService.getChannelListByGroupId(groupId);
        return ruleFolderList;
    }
    
    @RequestMapping("/modelGroupName")
    @ResponseBody
    public List<ModelGroup> getModelGroupList(HttpServletRequest request) {
        List<ModelGroup> ruleFolderList = this.modelGroupService.getModelGroupList();
        return ruleFolderList;
    }
    
    @RequestMapping("/modelName")
    @ResponseBody
    public List<Map<String, Object>> getModelList(HttpServletRequest request) {
        String groupId = ControllerUtil.getParam(request, "groupId");
        String channelId = ControllerUtil.getParam(request, "channelId");
        List<Map<String, Object>> ruleFolderList = this.ruleDetailService.getModelList(groupId, channelId);
        return ruleFolderList;
    }
    /**
     * 导出excel
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value="/exportExcel")
	public void exportExcel(HttpServletResponse response,HttpServletRequest request) {
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
    	
    	List<String> allList = getList(cycleId, "allList");
        List<String> failList = getList(cycleId, "failList");
        List<String> successList = getList(cycleId, "successList");
        List<String> responseTimeList = getList(cycleId, "responseTime");
        
        
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
    	
    	Row headRow4 = sheet.createRow(4);
    	Cell headCell4 = headRow4.createCell(0);
    	headCell4.setCellValue("响应时间");
    	for(int i=0;i<responseTimeList.size();i++) {
    		String tile = responseTimeList.get(i);
    		Cell tmpCell = headRow4.createCell(i+1);
    		tmpCell.setCellValue(tile);
    	}
    	
    	
        
        if(tabId!=null&&"2".equals(tabId)) {
        	List<String> ruleHitSuc = getList(cycleId, "ruleHitSuc");
            List<String> ruleHitFail = getList(cycleId, "ruleHitFail");
            List<String> scoreList = getList(cycleId, "scoreHit");
        	
        	
        	Row headRow5 = sheet.createRow(5);
        	Cell headCell5 = headRow5.createCell(0);
        	headCell5.setCellValue("通过次数");
        	for(int i=0;i<ruleHitSuc.size();i++) {
        		String tile = ruleHitSuc.get(i);
        		Cell tmpCell = headRow5.createCell(i+1);
        		tmpCell.setCellValue(tile);
        	}
        	
        	
        	Row headRow6 = sheet.createRow(6);
        	Cell headCell6 = headRow6.createCell(0);
        	headCell6.setCellValue("拒绝次数");
        	for(int i=0;i<ruleHitFail.size();i++) {
        		String tile = ruleHitFail.get(i);
        		Cell tmpCell = headRow6.createCell(i+1);
        		tmpCell.setCellValue(tile);
        	}
        	
        	
        	Row headRow7 = sheet.createRow(7);
        	Cell headCell7 = headRow7.createCell(0);
        	headCell7.setCellValue("评分");
        	for(int i=0;i<scoreList.size();i++) {
        		String tile = scoreList.get(i);
        		Cell tmpCell = headRow7.createCell(i+1);
        		tmpCell.setCellValue(tile);
        	}
        }
    	
    	
    	
	    downLoadExcel(response, workbook);
	}
    
   
    
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

}
