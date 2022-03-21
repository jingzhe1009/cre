package com.bonc.frame.controller.modelImport;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.frame.config.Config;
import com.bonc.frame.entity.commonresource.ModelGroup;
import com.bonc.frame.entity.modelImportAndExport.ImportAndExportOperateLog;
import com.bonc.frame.entity.modelImportAndExport.modelExport.ExportParam;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportConstant;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.ExportResult;
import com.bonc.frame.entity.modelImportAndExport.modelExport.entity.data.ResultData;
import com.bonc.frame.entity.rulefolder.RuleFolder;
import com.bonc.frame.security.ResourceType;
import com.bonc.frame.security.aop.PermissionsRequires;
import com.bonc.frame.service.modelBase.ModelBaseService;
import com.bonc.frame.service.modelImportAndExport.ExportModelService;
import com.bonc.frame.service.modelImportAndExport.ImpoerAndExportLogService;
import com.bonc.frame.service.modelImportAndExport.ImportModelService;
import com.bonc.frame.service.rule.RuleFolderService;
import com.bonc.frame.util.ControllerUtil;
import com.bonc.frame.util.ExportUtil;
import com.bonc.frame.util.ResponseResult;
import com.bonc.frame.util.WriterUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangzhengbao
 * @DATE: 2020/4/14 15:47
 */
@Controller
@RequestMapping("/model/importAndExport")
public class ModelImportAndExportController {

    private Log log = LogFactory.getLog(ModelImportAndExportController.class);

    @Autowired
    private ImportModelService importModelService;

    @Autowired
    private ExportModelService exportModelService;

    @Autowired
    private ImpoerAndExportLogService impoerAndExportLogService;
    
    @Autowired
    ModelBaseService modelService;
    
    @Autowired
    RuleFolderService ruleFolderService;

    @PermissionsRequires(value = "/exportAndImport", resourceType = ResourceType.MENU)
    @RequestMapping("/view")
    public String view(String idx, String childOpen, Model model, HttpServletRequest request) {
        model.addAttribute("idx", idx);//菜单状态标识
        model.addAttribute("childOpen", childOpen);
        return "pages/migration/global";
    }


    // ---------------------------导入 ------------------------------
    @PermissionsRequires(value = "/exportAndImport/import", resourceType = ResourceType.BUTTON)
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult importModel(HttpServletRequest request, String modelImportStrategy) {
        String targetString = request.getParameter("target");
        ExportParam target = null;
        if (StringUtils.isNotBlank(targetString)) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(targetString);
                target = JSONObject.toJavaObject(jsonObject, ExportParam.class);
            } catch (Exception e) {
                return ResponseResult.createFailInfo("参数target格式有误,target:[" + targetString + "],异常:[" + e.getMessage() + "]", e);
            }
        }
        String fileData = null;
        try {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
            // 导入
            fileData = WriterUtil.unzip(multipartFile.getInputStream());
        } catch (Exception e) {
            log.error("删除导入的缓存文件失败", e);
            return ResponseResult.createFailInfo("获取文件内容失败,请检查文件内容", e.getMessage());
        }

        if (StringUtils.isBlank(fileData)) {
            return ResponseResult.createFailInfo("获取文件内容为空,请检查文件内容");
        }
        
        //ADD 
        if (StringUtils.isNotBlank(targetString)) {
    		ExportResult importFileObject = JSONObject.toJavaObject(JSONObject.parseObject(fileData), ExportResult.class);
    		ResultData data = importFileObject.getData();
    		Map<String, RuleFolder> folderMap = data.getFolder();
            if (folderMap != null && !folderMap.isEmpty()) {
                for (RuleFolder folder : folderMap.values()) {
                    String folderId = ExportUtil.getObjectPropertyKeyValue(ExportConstant.FOLDER_ID, folder);
                    String folderName = ExportUtil.getObjectPropertyKeyValue(ExportConstant.FOLDER_NAME, folder);
                    
                    if("9".equals(target.getType())) {//自动创建模型组
                        ModelGroup model = modelService.getModelGroupByModelId(folderId);
                        if(model==null) {
                        	//创建模型组
                            ModelGroup modelGroup = new ModelGroup();
                            String currentUser = ControllerUtil.getCurrentUser();
                            modelGroup.setModelGroupId(folderId);
                            modelGroup.setModelGroupName(folderName);
                            modelGroup.setCreateDate(new Date());
                            modelGroup.setCreatePerson(currentUser);
                            modelService.insertModelGroupDataPersistence(modelGroup);
                        }
                        target.setModelGroupId(folderId);
                    }
                   /* else{//全部,场景
                        RuleFolder ruleFolderDetail = ruleFolderService.getRuleFolderDetail(folderId, null);
                        if(ruleFolderDetail!=null) {
                        	return ResponseResult.createFailInfo("同一场景不能重复导入,请先删除该场景");
                        }
                        ModelGroup model = modelService.getModelGroupByModelId(folderId);
                        if(model==null) {
                        	
                        }else {
                        	return ResponseResult.createFailInfo("同一模型组不能重复导入,请先删除该模型组及相关参数等");
                        }
                    }*/
                }
            }
        }

        ImportAndExportOperateLog result = null;
        try {
            log.info("开始导入");
            long start = System.currentTimeMillis();
            result = importModelService.import_V1(fileData, target, modelImportStrategy);
            log.info("导入完成");
            log.info("导入耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒");
        } catch (Exception e) {
            log.error(e);
            return ResponseResult.createFailInfo("导入失败", e);
        }
        if (result != null) {
            return ResponseResult.createSuccessInfo("导入成功", result);
        } else {
            return ResponseResult.createFailInfo("导入失败,生成的报告为null", "");
        }
    }

    // --------------------------------导出 ------------------------------------

    @PermissionsRequires(value = "/exportAndImport/export", resourceType = ResourceType.BUTTON)
    @RequestMapping("/export")
    @ResponseBody
    public ResponseResult exportV2(HttpServletRequest request) {
        String parameter = request.getParameter("exportParams");
        if (StringUtils.isBlank(parameter)) {
            return ResponseResult.createFailInfo("参数不能为null");
        }
        List<ExportParam> exportParams = null;
        try {
            exportParams = JSONArray.parseArray(parameter, ExportParam.class);
            if (exportParams == null || exportParams.isEmpty()) {
                return ResponseResult.createFailInfo("参数不能为null");
            }
        } catch (Exception e) {
            return ResponseResult.createSuccessInfo("导出失败,传入参数解析异常", e);
        }
        //执行导出
        ExportResult exportResult = new ExportResult();
        exportResult.setExportParams(exportParams);
        exportResult = exportModelService.exportWithExportParam(exportResult, exportParams);

        String data = exportResult.buildResultDataToFile();
        ImportAndExportOperateLog importAndExportOperateLog = exportResult.paseExportOperateLog();
        if (importAndExportOperateLog == null) {
            return ResponseResult.createSuccessInfo("导出失败", importAndExportOperateLog);
        }
        impoerAndExportLogService.insertImportAndExportOperateLog(importAndExportOperateLog);
        if (ImportAndExportOperateLog.ALL_SUCCESS.equals(importAndExportOperateLog.getSuccess())) {
            if (!StringUtils.isBlank(data)) {
                try {
                    writerResult(data, importAndExportOperateLog.getFileName());
                } catch (Exception e) {
                    return ResponseResult.createFailInfo(e.getMessage(), e);
                }
            } else {
                return ResponseResult.createSuccessInfo("导出失败", importAndExportOperateLog);
            }
        }
        return ResponseResult.createSuccessInfo("导出成功", importAndExportOperateLog);

    }

    @RequestMapping("/export/downLoader/check")
    @ResponseBody
    public ResponseResult checkDownLoader(String filePath, HttpServletResponse response) {
        if (StringUtils.isBlank(filePath)) {
            return ResponseResult.createFailInfo("需要下载的文件名称不能为null");
//            throw new Exception("需要下载的文件名称不能为null");
        }
        String exportFilePath = null;
        try {
            exportFilePath = getExportFilePath();
        } catch (Exception e) {
            return ResponseResult.createFailInfo(e.getMessage());
        }

        String fileName = null;
        File targetFile = new File(exportFilePath, filePath);
        if (!targetFile.exists()) {
            return ResponseResult.createFailInfo("文件找不到");
        } else {
            fileName = targetFile.getName();
        }
        if (StringUtils.isBlank(fileName)) {
            return ResponseResult.createFailInfo("文件找不到,文件名称为null");
        }
        return ResponseResult.createSuccessInfo("下载检查成功,文件存在");
    }

    @RequestMapping("/export/downLoader")
    @ResponseBody
    public ResponseResult downLoader(String filePath, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(filePath)) {
//            return ResponseResult.createFailInfo("需要下载的文件名称不能为null");
            throw new Exception("需要下载的文件名称不能为null");
        }
        String exportFilePath = getExportFilePath();

        String fileName = null;
        File targetFile = new File(exportFilePath, filePath);
        if (!targetFile.exists()) {
//            throw new Exception("文件找不到");
            return ResponseResult.createFailInfo("文件找不到");
        } else {
            fileName = targetFile.getName();
        }
        if (StringUtils.isBlank(fileName)) {
            return ResponseResult.createFailInfo("文件找不到,文件名称为null");
//            throw new Exception("文件找不到,文件名称为null");
        }

        OutputStream stream = null;
        try {
            stream = response.getOutputStream();
            //清空下载文件的空白行（空白行是因为有的前端代码编译后产生的）
            response.reset();
            stream.write(FileUtils.readFileToByteArray(targetFile));
            stream.flush();

            //设置响应头，把文件名字设置好
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentType("application/octet-stream; charset=utf-8");
        } catch (Exception e) {
//            return ResponseResult.createFailInfo("下载失败," + e.getMessage(), e);
            throw new Exception("下载失败, e.getMessage()", e);
        } finally {
            //关闭流
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new Exception("下载失败, " + e.getMessage(), e);
                }
            }
        }
        return ResponseResult.createSuccessInfo("下载成功");
    }

    public boolean writerResult(String resultData, String fileName) throws Exception {
        if (StringUtils.isBlank(resultData)) {
            throw new Exception("写入文件失败,数据为null");
        }
        String exportFilePath = getExportFilePath();
        String outputFilePath = exportFilePath + "/" + fileName;
        boolean writerSuccess = WriterUtil.save2File(outputFilePath, resultData);
        if (writerSuccess) {
            zipExportFile(outputFilePath);
            return true;
        } else {
            return false;
        }
    }

    public String getExportFilePath() throws Exception {
        String configExportFilepath = Config.CRE_EXPORT_FILEPATH;
        String exportFilePath = null;
        if (!StringUtils.isBlank(configExportFilepath)) {
            if (configExportFilepath.startsWith("classpath:")) {
                String resourceRootPath = ModelImportAndExportController.class.getResource("/").getPath();
                if (!StringUtils.isBlank(resourceRootPath)) {
                    exportFilePath = resourceRootPath + configExportFilepath.replaceFirst("classpath:", "");
                } else {
                    log.error("获取静态资源文件路径失败");
                    throw new Exception("获取静态资源文件路径失败");
                }
            } else if (configExportFilepath.startsWith("file:")) {
                exportFilePath = configExportFilepath.replaceFirst("file:", "") + "/";
            } else {
                log.error("文件导出路径格式不正确");
                throw new Exception("文件导出路径格式不正确");
            }
        }
        return exportFilePath;
    }


    public void zipExportFile(String outputFilePath) throws Exception {
        File outputFile = new File(outputFilePath);
        List<File> srcfile = new ArrayList<>();//数量可以通过本身的业务逻辑获取
        srcfile.add(outputFile);
        String zipname = outputFilePath + ".zip";
        log.info("导出文件保存路径:" + zipname);
        File zip = new File(zipname);// 压缩文件
        WriterUtil.zipFiles(srcfile, zip);
        WriterUtil.deleteFile(outputFile);
    }


    // ------------------------------------------ 导入导出日志 ----------------------------------------------------

    @RequestMapping("/selectImportAndExportOperateLogBaseInfo/page")
    @ResponseBody
    public Map<String, Object> selectImportAndExportOperateLogBaseInfoPage(String logId, String operateType, String success, String systemVersion, String fileName, String modelImportStrategy,
                                                                           String operatePerson, String startDate, String endDate, String ip,
                                                                           String start, String length) throws Exception {
        Map<String, Object> map = impoerAndExportLogService.selectImportAndExportOperateLogBaseInfoPage(logId, operateType, success, systemVersion, fileName, modelImportStrategy, operatePerson, startDate, endDate, ip, start, length);
        return map;
    }

    @RequestMapping("/selectImportAndExportOperateLogWithContentPage/page")
    @ResponseBody
    public Map<String, Object> selectImportAndExportOperateLogWithContentPage(String logId, String operateType, String success, String systemVersion, String fileName, String modelImportStrategy,
                                                                              String operatePerson, String startDate, String endDate, String ip,
                                                                              String start, String length) throws Exception {
        Map<String, Object> map = impoerAndExportLogService.selectImportAndExportOperateLogWithContentPage(logId, operateType, success, systemVersion, fileName, modelImportStrategy, operatePerson, startDate, endDate, ip, start, length);
        return map;
    }

}
