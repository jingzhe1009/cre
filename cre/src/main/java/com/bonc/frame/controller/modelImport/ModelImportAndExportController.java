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
        model.addAttribute("idx", idx);//??????????????????
        model.addAttribute("childOpen", childOpen);
        return "pages/migration/global";
    }


    // ---------------------------?????? ------------------------------
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
                return ResponseResult.createFailInfo("??????target????????????,target:[" + targetString + "],??????:[" + e.getMessage() + "]", e);
            }
        }
        String fileData = null;
        try {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
            // ??????
            fileData = WriterUtil.unzip(multipartFile.getInputStream());
        } catch (Exception e) {
            log.error("?????????????????????????????????", e);
            return ResponseResult.createFailInfo("????????????????????????,?????????????????????", e.getMessage());
        }

        if (StringUtils.isBlank(fileData)) {
            return ResponseResult.createFailInfo("????????????????????????,?????????????????????");
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
                    
                    if("9".equals(target.getType())) {//?????????????????????
                        ModelGroup model = modelService.getModelGroupByModelId(folderId);
                        if(model==null) {
                        	//???????????????
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
                   /* else{//??????,??????
                        RuleFolder ruleFolderDetail = ruleFolderService.getRuleFolderDetail(folderId, null);
                        if(ruleFolderDetail!=null) {
                        	return ResponseResult.createFailInfo("??????????????????????????????,?????????????????????");
                        }
                        ModelGroup model = modelService.getModelGroupByModelId(folderId);
                        if(model==null) {
                        	
                        }else {
                        	return ResponseResult.createFailInfo("?????????????????????????????????,??????????????????????????????????????????");
                        }
                    }*/
                }
            }
        }

        ImportAndExportOperateLog result = null;
        try {
            log.info("????????????");
            long start = System.currentTimeMillis();
            result = importModelService.import_V1(fileData, target, modelImportStrategy);
            log.info("????????????");
            log.info("???????????????" + (System.currentTimeMillis() - start) / 1000 + "???");
        } catch (Exception e) {
            log.error(e);
            return ResponseResult.createFailInfo("????????????", e);
        }
        if (result != null) {
            return ResponseResult.createSuccessInfo("????????????", result);
        } else {
            return ResponseResult.createFailInfo("????????????,??????????????????null", "");
        }
    }

    // --------------------------------?????? ------------------------------------

    @PermissionsRequires(value = "/exportAndImport/export", resourceType = ResourceType.BUTTON)
    @RequestMapping("/export")
    @ResponseBody
    public ResponseResult exportV2(HttpServletRequest request) {
        String parameter = request.getParameter("exportParams");
        if (StringUtils.isBlank(parameter)) {
            return ResponseResult.createFailInfo("???????????????null");
        }
        List<ExportParam> exportParams = null;
        try {
            exportParams = JSONArray.parseArray(parameter, ExportParam.class);
            if (exportParams == null || exportParams.isEmpty()) {
                return ResponseResult.createFailInfo("???????????????null");
            }
        } catch (Exception e) {
            return ResponseResult.createSuccessInfo("????????????,????????????????????????", e);
        }
        //????????????
        ExportResult exportResult = new ExportResult();
        exportResult.setExportParams(exportParams);
        exportResult = exportModelService.exportWithExportParam(exportResult, exportParams);

        String data = exportResult.buildResultDataToFile();
        ImportAndExportOperateLog importAndExportOperateLog = exportResult.paseExportOperateLog();
        if (importAndExportOperateLog == null) {
            return ResponseResult.createSuccessInfo("????????????", importAndExportOperateLog);
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
                return ResponseResult.createSuccessInfo("????????????", importAndExportOperateLog);
            }
        }
        return ResponseResult.createSuccessInfo("????????????", importAndExportOperateLog);

    }

    @RequestMapping("/export/downLoader/check")
    @ResponseBody
    public ResponseResult checkDownLoader(String filePath, HttpServletResponse response) {
        if (StringUtils.isBlank(filePath)) {
            return ResponseResult.createFailInfo("????????????????????????????????????null");
//            throw new Exception("????????????????????????????????????null");
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
            return ResponseResult.createFailInfo("???????????????");
        } else {
            fileName = targetFile.getName();
        }
        if (StringUtils.isBlank(fileName)) {
            return ResponseResult.createFailInfo("???????????????,???????????????null");
        }
        return ResponseResult.createSuccessInfo("??????????????????,????????????");
    }

    @RequestMapping("/export/downLoader")
    @ResponseBody
    public ResponseResult downLoader(String filePath, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(filePath)) {
//            return ResponseResult.createFailInfo("????????????????????????????????????null");
            throw new Exception("????????????????????????????????????null");
        }
        String exportFilePath = getExportFilePath();

        String fileName = null;
        File targetFile = new File(exportFilePath, filePath);
        if (!targetFile.exists()) {
//            throw new Exception("???????????????");
            return ResponseResult.createFailInfo("???????????????");
        } else {
            fileName = targetFile.getName();
        }
        if (StringUtils.isBlank(fileName)) {
            return ResponseResult.createFailInfo("???????????????,???????????????null");
//            throw new Exception("???????????????,???????????????null");
        }

        OutputStream stream = null;
        try {
            stream = response.getOutputStream();
            //??????????????????????????????????????????????????????????????????????????????????????????
            response.reset();
            stream.write(FileUtils.readFileToByteArray(targetFile));
            stream.flush();

            //??????????????????????????????????????????
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentType("application/octet-stream; charset=utf-8");
        } catch (Exception e) {
//            return ResponseResult.createFailInfo("????????????," + e.getMessage(), e);
            throw new Exception("????????????, e.getMessage()", e);
        } finally {
            //?????????
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new Exception("????????????, " + e.getMessage(), e);
                }
            }
        }
        return ResponseResult.createSuccessInfo("????????????");
    }

    public boolean writerResult(String resultData, String fileName) throws Exception {
        if (StringUtils.isBlank(resultData)) {
            throw new Exception("??????????????????,?????????null");
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
                    log.error("????????????????????????????????????");
                    throw new Exception("????????????????????????????????????");
                }
            } else if (configExportFilepath.startsWith("file:")) {
                exportFilePath = configExportFilepath.replaceFirst("file:", "") + "/";
            } else {
                log.error("?????????????????????????????????");
                throw new Exception("?????????????????????????????????");
            }
        }
        return exportFilePath;
    }


    public void zipExportFile(String outputFilePath) throws Exception {
        File outputFile = new File(outputFilePath);
        List<File> srcfile = new ArrayList<>();//?????????????????????????????????????????????
        srcfile.add(outputFile);
        String zipname = outputFilePath + ".zip";
        log.info("????????????????????????:" + zipname);
        File zip = new File(zipname);// ????????????
        WriterUtil.zipFiles(srcfile, zip);
        WriterUtil.deleteFile(outputFile);
    }


    // ------------------------------------------ ?????????????????? ----------------------------------------------------

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
