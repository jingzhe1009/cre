/**
 * 导入
 */
var importModal = {
    modelImportStrategy: 'removal', // 默认removal
    toFolder: '', // folderId
    globalFlag: false, // 全局导入导出页面为true, 模型库/场景下进入为false
    globalImportFlag: true, // 全局导入导出页面中: 选择全局导入true/导入至场景false
    pubTargetFlag: false, // 导入至模型库true/导入至场景false
    modelGroupId: '',
    modelBaseFolderId: '',
    /**
     * 初始化上传页面
     *      @param toFolder: 上传位置 folderId
     *      @param globalFlag: 是否为全局导入导出
     */
    initUploadPage: function (toFolder, globalFlag) {
        importModal.toFolder = toFolder;
        importModal.globalFlag = globalFlag;
        if (globalFlag) {
            importModal.initImportTarget();
            importModal.$query('.globalDiv').show();
            $('#cancelImport').css('display', 'none');
        } else {
            $('#uploadAlertModal').modal({'show': 'center', "backdrop": "static"});
        }
    },
    // 初始化导入位置下拉框
    initImportTarget: function () {
        // 获取模型库ID
        importModal.$ajax('/ruleFolder/modelBaseId', 'GET', null, function (model) {
            importModal.modelBaseFolderId = model.data;
        });

        // 获取场景列表
        importModal.$ajax('/ruleFolder/ruleFolderList', 'GET', null, function (data) {
            var html = '<option value="">请选择</option>';
            for (var i = 0; i < data.length; i++) {
                html += '<option value="' + data[i].key + '">' + data[i].text + '</option>';
            }
            importModal.$query('#import_folderId').html(html);
        });

        // 获取模型组
        importModal.$ajax('/modelBase/group/list', 'GET', null, function (data) {
            var list = data.data;
            var html = '<option value="">请选择</option>';
            for (var i = 0; i < list.length; i++) {
                html += '<option value="' + list[i].modelGroupId + '">' + list[i].modelGroupName + '</option>';
            }
            importModal.$query('#import_modelGroupId').html(html);
        });
    },
    // 导入策略切换
    handleStrategyChange: function () {
        importModal.modelImportStrategy = $("input[name='modelImportStrategyRadio']:checked").attr('value');
    },
    // 导入位置切换
    handleTargetChange: function () {
        var targetType = $("input[name='modelImportTargetRadio']:checked").attr('value');
        if (targetType === '0') { // 全局导入
            $('.targetChoose').css('display', 'none');
            importModal.globalImportFlag = true;
            importModal.pubTargetFlag = false;
            importModal.toFolder = '';
            importModal.modelGroupId = '';
        } else if (targetType === '1') { // 模型库
            $('.targetChoose').css('display', 'block');
            importModal.$query('.folderId').hide();
            importModal.$query('.modelGroupId').show();
            importModal.globalImportFlag = false;
            importModal.pubTargetFlag = true;
        } else if (targetType === '2') { // 场景
            $('.targetChoose').css('display', 'block');
            importModal.$query('.folderId').show();
            importModal.$query('.modelGroupId').hide();
            importModal.globalImportFlag = false;
            importModal.pubTargetFlag = false;
        } else if (targetType === '9') { // add 模型库自动建模型组
            $('.targetChoose').css('display', 'block');
            importModal.$query('.folderId').hide();
            importModal.$query('.modelGroupId').hide();
            importModal.globalImportFlag = false;
            importModal.pubTargetFlag = true;
        }
    },
    // 场景切换
    handleFolderChange: function () {
        importModal.toFolder = $('#import_folderId option:selected').attr('value');
        importModal.modelGroupId = '';
    },
    // 模型组切换
    handleGroupChange: function () {
        importModal.toFolder = importModal.modelBaseFolderId;
        importModal.modelGroupId = $('#import_modelGroupId option:selected').attr('value');
    },
    // 导入
    uploadeFile: function () {
        if (!$('#uploadFile')[0].files[0]) {
            failedMessager.show('请选择文件！');
            return;
        }

        //edit
        var targetType = $("input[name='modelImportTargetRadio']:checked").attr('value');
		if(targetType!='9'){
			if (importModal.globalFlag) { // 全局导入导出页面
	            if (!importModal.globalImportFlag) { // 导入位置: 模型库/场景
	                if (importModal.pubTargetFlag) { // 导入至模型库
	                    if (!$('#importPage .modelGroupIdForm').isValid()) { // 模型组表单验证
	                        failedMessager.show('模型组不能为空！');
	                        return;
	                    }
	                } else {
	                    if (!$('#importPage .folderIdForm').isValid()) { // 场景表单验证
	                        failedMessager.show('场景不能为空！');
	                        return;
	                    }
	                }
	            }
	        }
		}

        var target = {
            folderId: importModal.toFolder,
            modelGroupId: importModal.modelGroupId
        };
        debugger;
        //add
        if(targetType=='9'){
			target = {
	            folderId: importModal.modelBaseFolderId,
	            modelGroupId: '',
	            type: '9'
	        };
		}

        var formData = new FormData();
        formData.append('file', $('#uploadFile')[0].files[0]);
        formData.append("target", JSON.stringify(target));
        formData.append("modelImportStrategy", importModal.modelImportStrategy);
        $.ajax({
            url: webpath + '/model/importAndExport/import',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            cache: false,
            beforeSend: function () {
                loading.show();
                $('#uploadAlertModal').modal('hide');
            },
            success: function (data) {
                if (data.status === 0) {
                    // $('#uploadAlertModal').modal('hide');
                    // successMessager.show('上传成功！');
                    importReportModel.initImportReport(data.data);
                } else {
                    failedMessager.show(data.msg);
                    // if (!importModal.globalFlag) {
                    //     $('#uploadAlertModal').modal({'show': 'center', "backdrop": "static"});
                    // }
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.log(errorThrown);
                if (textStatus === 'timeout') {
                    console.log('请求超时');
                }
                if (!importModal.globalFlag) {
                    $('#uploadAlertModal').modal({'show': 'center', "backdrop": "static"});
                }
            },
            complete: function () {
                loading.hide();
            }
        });
    },
    $ajax: function (url, method, body, callback, dataType) {
        if (dataType === void 0) dataType = 'json';
        var option = {
            url: webpath + url,
            type: method,
            dataType: dataType,
            success: function (data) {
                callback(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.log(errorThrown);
                if (textStatus === 'timeout') {
                    console.log('请求超时');
                }
            }
        };
        if (body) option.data = body;
        $.ajax(option);
    },
    $query: function (name) {
        return $('#importPage ' + (name !== void 0 ? name : ''));
    }
}

// 导入报告
var importReportModel = {
    // 总统计表格展示字段映射
    totalKeyMap: {
        "folderNumber": '场景数',
        "modelGroupNumber": '模型组数',
        "modelHeaderNumber": '模型数',
        "modelVersionNumber": '模型版本数',
        "apiGroupNumber": '接口组数',
        "apiNumber": '接口数',
        "kpiGroupNumber": '指标组数',
        "kpiNumber": '指标数',
        "variableGroupNumber": '参数组数',
        "variableNumber": '参数数',
        "dbNumber": '数据源数',
        "pubDbTableNumber": '公共的数据源表数',
        "priDbTableNumber": '场景下的数据源表数',
        "dbColunmNumber": '字段数'
    },
    // 类型映射
    dataTypeMap: {
        'folder': {type: '场景', numCol: 'folderNumber', changeNumCol: 'folderNumber'},
        'modelGroup': {type: '模型组', numCol: 'modelGroupNumber', changeNumCol: 'modelHeaderNumber'},
        'modelHeader': {type: '模型', numCol: 'modelHeaderNumber', changeNumCol: 'modelHeaderNumber'},
        'variableGroup': {type: '参数组', numCol: 'variableGroupNumber', changeNumCol: 'variableNumber'},
        'variable': {type: '参数', numCol: 'variableNumber', changeNumCol: 'variableNumber'},
        'kpiGroup': {type: '指标组', numCol: 'kpiGroupNumber', changeNumCol: 'kpiNumber'},
        'kpi': {type: '指标', numCol: 'kpiNumber', changeNumCol: 'kpiNumber'},
        'apiGroup': {type: '接口组', numCol: 'apiGroupNumber', changeNumCol: 'apiNumber'},
        'api': {type: '接口', numCol: 'apiNumber', changeNumCol: 'apiNumber'},
        'db': {type: '数据源', numCol: 'dbNumber', changeNumCol: 'pubDbTableNumber'},
        'pubDbTable': {type: '数据源表', numCol: 'pubDbTableNumber', changeNumCol: 'pubDbTableNumber'},
        'dbColunm': {type: '数据源字段', numCol: 'dbColunmNumber', changeNumCol: 'dbColunmNumber'}
    },
    // 导入方式映射
    importTypeMap: {
        'useImportData': '直接导入',
        'useUpdateData': '冲突调整',
        'useSystemData': '使用系统'
    },
    // 缺失类型映射
    lackMap: {
        'folder': {
            type: '场景',
            id: 'folderId',
            name: 'folderName',
        },
        'modelGroup': {
            type: '模型组',
            id: 'modelGroupId',
            name: 'modelGroupName'
        },
        'variableGroup': {
            type: '参数组',
            id: 'variableGroupId',
            name: 'variableGroupName'
        },
        'variable': {
            type: '参数',
            id: 'variableId',
            name: 'variableCode'
        },
        'kpiGroup': {
            type: '指标组',
            id: 'kpiGroupId',
            name: 'kpiGroupName'
        },
        'kpi': {
            type: '指标',
            id: 'kpiId',
            name: 'kpiName'
        },
        'apiGroup': {
            type: '接口组',
            id: 'apiGroupId',
            name: 'apiGroupName'
        },
        'api': {
            type: '接口',
            id: 'apiId',
            name: 'apiName'
        },
        'db': {
            type: '数据源',
            id: 'dbId',
            name: 'dbAlias'
        },
        'pubDbTable': {
            type: '数据源表',
            id: 'tableId',
            name: 'tableCode'
        },
        'dbColunm': {
            type: '数据源字段',
            id: 'columnId',
            name: 'columnCode'
        },
        'modelHeader': {
            type: '模型',
            id: 'ruleName',
            name: 'modelName',
            version: 'version'
        },
        'modelVersion': {
            type: '模型版本',
            id: 'versionId',
            name: 'version'
        }
    },
    // 初始化导入报告
    initImportReport: function (data) {
        // var dataStr = '{"report":{"statistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useImportDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useUpdateDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useSystemDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"pri":{"statistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":6,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useImportDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useUpdateDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0,"allNumber":5},"useSystemDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"folder":{"folderId-1":{"statistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":3,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useImportDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useUpdateDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0,"allNumber":3},"useSystemDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"fromData":{"id":"","name":"场景1-前","code":"","desc":""},"toData":{"id":"","name":"场景1-后","code":"","desc":""},"importSuccessType":"useImportData","modelHeader":{"modelId-1":{"fromData":{"id":"","name":"模型1-前","code":"","desc":""},"toData":{"id":"","name":"模型1-后","code":"","desc":""},"importSuccessType":"useImportData","version":{"id1":{"importSuccessType":"useImportData","fromData":{"id":"","name":"V1-前","code":"","desc":""},"toData":{"id":"","name":"v1-后","code":"","desc":""}},"id2":{"importSuccessType":"useImportData","fromData":{"id":"","name":"V2-前","code":"","desc":""},"toData":{"id":"","name":"v2-后","code":"","desc":""}}}},"modelId-2":{"fromData":{"id":"","name":"模型2-前","code":"","desc":""},"toData":{"id":"","name":"模型2-后","code":"","desc":""},"importSuccessType":"useImportData","version":{"id1":{"importSuccessType":"useImportData","fromData":{"id":"","name":"V1-前","code":"","desc":""},"toData":{"id":"","name":"V1-后","code":"","desc":""}}}}},"variable":{"varId-1":{"fromData":{"id":"","name":"参数1-前","code":"","desc":""},"toData":{"id":"","name":"参数1-后","code":"","desc":""},"importSuccessType":"useImportData"},"varId-2":{"fromData":{"id":"","name":"参数2-前","code":"","desc":""},"toData":{"id":"","name":"参数2-后","code":"","desc":""},"importSuccessType":"useImportData"}},"api":{"apiId-1":{"fromData":{"id":"","name":"接口1-前","code":"","desc":""},"toData":{"id":"","name":"接口1-后","code":"","desc":""},"importSuccessType":"useImportData"}}},"folderId-2":{"statistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":3,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useImportDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useUpdateDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0,"allNumber":2},"useSystemDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"fromData":{"id":"","name":"场景2-前","code":"","desc":""},"toData":{"id":"","name":"场景2-后","code":"","desc":""},"importSuccessType":"useImportData","modelHeader":{"modelId-1":{"fromData":{"id":"","name":"模型1-前","code":"","desc":""},"toData":{"id":"","name":"模型1-后","code":"","desc":""},"importSuccessType":"useImportData","version":{"id1":{"importSuccessType":"useImportData","fromData":{"id":"","name":"V1-前","code":"","desc":""},"toData":{"id":"","name":"v1-后","code":"","desc":""}},"id2":{"importSuccessType":"useImportData","fromData":{"id":"","name":"V2-前","code":"","desc":""},"toData":{"id":"","name":"v2-后","code":"","desc":""}}}},"modelId-2":{"fromData":{"id":"","name":"模型2-前","code":"","desc":""},"toData":{"id":"","name":"模型2-后","code":"","desc":""},"importSuccessType":"useImportData","version":{"id1":{"importSuccessType":"useImportData","fromData":{"id":"","name":"V1-前","code":"","desc":""},"toData":{"id":"","name":"V1-后","code":"","desc":""}}}}},"variable":{"varId-3":{"fromData":{"id":"","name":"参数3-前","code":"","desc":""},"toData":{"id":"","name":"参数3-后","code":"","desc":""},"importSuccessType":"useImportData"}},"api":{"apiId-2":{"fromData":{"id":"","name":"接口2-前","code":"","desc":""},"toData":{"id":"","name":"接口2-后","code":"","desc":""},"importSuccessType":"useImportData"}}}}},"pub":{"statistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":2,"apiGroupNumber":0,"apiNumber":1,"kpiGroupNumber":0,"kpiNumber":1,"variableGroupNumber":0,"variableNumber":3,"dbNumber":0,"pubDbTableNumber":1,"priDbTableNumber":0,"dbColunmNumber":0},"useImportDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useUpdateDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":2,"apiGroupNumber":0,"apiNumber":1,"kpiGroupNumber":0,"kpiNumber":1,"variableGroupNumber":0,"variableNumber":3,"dbNumber":0,"pubDbTableNumber":1,"priDbTableNumber":0,"dbColunmNumber":0},"useSystemDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"modelGroup":{"标识":{"statistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":2,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useImportDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useUpdateDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useSystemDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"fromData":{"id":"","name":"模型组1-前","code":"","desc":""},"toData":{"id":"","name":"模型组1-后","code":"","desc":""},"importSuccessType":"useImportData","modelHeader":{"modelId-3":{"fromData":{"id":"","name":"模型组模型1-前","code":"","desc":""},"toData":{"id":"","name":"模型组模型1-后","code":"","desc":""},"importSuccessType":"useImportData","version":{"id1":{"importSuccessType":"useImportData","fromData":{"id":"","name":"V1-前","code":"","desc":""},"toData":{"id":"","name":"V1-后","code":"","desc":""}}}},"modelId-4":{"fromData":{"id":"","name":"模型组模型2-前","code":"","desc":""},"toData":{"id":"","name":"模型组模型2-后","code":"","desc":""},"importSuccessType":"useImportData","version":{"id1":{"importSuccessType":"useImportData","fromData":{"id":"","name":"V1-前","code":"","desc":""},"toData":{"id":"","name":"V1-后","code":"","desc":""}}}}}}},"variableGroup":{"varGroup-1":{"statistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useImportDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useUpdateDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":1,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useSystemDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"fromData":{"id":"","name":"参数组1-前","code":"","desc":""},"toData":{"id":"","name":"参数组1-后","code":"","desc":""},"importSuccessType":"useImportData","variable":{"varC-1":{"fromData":{"id":"","name":"c参数2-前","code":"","desc":""},"toData":{"id":"","name":"c参数2-后","code":"","desc":""},"importSuccessType":"useImportData"}}},"varGroup-2":{"statistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useImportDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useUpdateDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":2,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useSystemDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"fromData":{"id":"","name":"参数组2-前","code":"","desc":""},"toData":{"id":"","name":"参数组2-后","code":"","desc":""},"importSuccessType":"useImportData","variable":{"varC-3":{"fromData":{"id":"","name":"c参数1-前","code":"","desc":""},"toData":{"id":"","name":"c参数1-后","code":"","desc":""},"importSuccessType":"useImportData"},"varC-4":{"fromData":{"id":"","name":"c参数2-前","code":"","desc":""},"toData":{"id":"","name":"c参数2-后","code":"","desc":""},"importSuccessType":"useImportData"}}}},"kpiGroup":{"标识":{"statistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useImportDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useUpdateDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":1,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useSystemDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"fromData":{"id":"","name":"指标组1-前","code":"","desc":""},"toData":{"id":"","name":"指标组1-后","code":"","desc":""},"importSuccessType":"useImportData","kpi":{"id1":{"fromData":{"id":"","name":"指标1-前","code":"","desc":""},"toData":{"id":"","name":"指标1-后","code":"","desc":""},"importSuccessType":"useImportData"}}}},"apiGroup":{"接口组Id":{"statistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":1,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useImportDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useUpdateDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":1,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useSystemDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"fromData":{"id":"","name":"接口组1-前","code":"","desc":""},"toData":{"id":"","name":"接口组1-后","code":"","desc":""},"importSuccessType":"useImportData","api":{"id1":{"fromData":{"id":"","name":"接口1-前","code":"","desc":""},"toData":{"id":"","name":"接口1-后","code":"","desc":""},"importSuccessType":"useImportData"}}}},"db":{"dbId":{"statistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":1,"priDbTableNumber":0,"dbColunmNumber":0},"useImportDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"useUpdateDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":1,"priDbTableNumber":0,"dbColunmNumber":0},"useSystemDataStatistics":{"folderNumber":0,"modelGroupNumber":0,"modelHeaderNumber":0,"modelVersionNumber":0,"apiGroupNumber":0,"apiNumber":0,"kpiGroupNumber":0,"kpiNumber":0,"variableGroupNumber":0,"variableNumber":0,"dbNumber":0,"pubDbTableNumber":0,"priDbTableNumber":0,"dbColunmNumber":0},"fromData":{"id":"","name":"数据源-前","code":"","desc":""},"toData":{"id":"","name":"数据源-后","code":"","desc":""},"importSuccessType":"useImportData","pubDbTable":{"id1":{"fromData":{"id":"","name":"表1-前","code":"","desc":""},"toData":{"id":"","name":"表1-后","code":"","desc":""},"importSuccessType":"useImportData","dbColumn":{"id1":{"fromData":{"id":"","name":"列1-前","code":"","desc":""},"toData":{"id":"","name":"列1-后","code":"","desc":""},"importSuccessType":"useImportData"}}}}}}}}}';
        // var dataObj = JSON.parse(dataStr);
        // var report = dataObj.report;
        // console.dir(data);

        if (!data) {
            failedMessager.show('报告生成失败，导入报告数据缺失，请检查！');
            return;
        }
        $('#operateType_import').text(data.operateType === 'export' ? '导出' : '导入');
        $('#systemVersion_import').text(data.systemVersion);
        $('#date_import').text(data.operateDate);
        $('#success_import').text((data.success === '1') ? '成功' : '失败');

        var content = data.operateContent ? data.operateContent : '{}';
        var contentObj = content ? JSON.parse(content) : {};

        if (data.success === '1') { // 导入成功
            $('.importSuccessTable').removeClass('hide');
            $('.importFailedTable').addClass('hide');
            var report = contentObj ? contentObj.report : {};
            importReportModel.initTotalTable(report); // 总统计表格
            importReportModel.initFolderTable(report); // 导入模型列表
            importReportModel.initChangeTable(report); // 调整数据列表
        } else {
            $('.importFailedTable').removeClass('hide');
            $('.importSuccessTable').addClass('hide');
            var lack = contentObj ? contentObj.lack : {};
            importReportModel.initLackTable(lack);
        }
        $('#importReportAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 导入报告 - 成功 - 总统计表格
    initTotalTable: function (report) {
        var totalDataArr = [];
        if (report) {
            var totalKeyMap = importReportModel.totalKeyMap;
            var totalAll = report.statistics ? report.statistics : {};
            var totalPri_import = report.pri ? report.pri.useImportDataStatistics : {};
            var totalPri_system = report.pri ? report.pri.useSystemDataStatistics : {};
            var totalPri_update = report.pri ? report.pri.useUpdateDataStatistics : {};
            var totalPri_all = report.pri ? report.pri.statistics : {};
            var totalPub_import = report.pub ? report.pub.useImportDataStatistics : {};
            var totalPub_system = report.pub ? report.pub.useSystemDataStatistics : {};
            var totalPub_update = report.pub ? report.pub.useUpdateDataStatistics : {};
            var totalPub_all = report.pub ? report.pub.statistics : {};

            for (var key in totalKeyMap) {
                if (!totalKeyMap[key]) {
                    console.log('function initTotalTable, key missed: ' + key);
                    continue;
                }
                totalDataArr.push([
                    totalKeyMap[key],
                    totalPri_import ? (totalPri_import[key] ? totalPri_import[key] : 0) : 0,
                    totalPri_system ? (totalPri_system[key] ? totalPri_system[key] : 0) : 0,
                    totalPri_update ? (totalPri_update[key] ? totalPri_update[key] : 0) : 0,
                    totalPri_all ? (totalPri_all[key] ? totalPri_all[key] : 0) : 0,
                    totalPub_import ? (totalPub_import[key] ? totalPub_import[key] : 0) : 0,
                    totalPub_system ? (totalPub_system[key] ? totalPub_system[key] : 0) : 0,
                    totalPub_update ? (totalPub_update[key] ? totalPub_update[key] : 0) : 0,
                    totalPub_all ? (totalPub_all[key] ? totalPub_all[key] : 0) : 0,
                    totalAll ? (totalAll[key] ? totalAll[key] : 0) : 0
                ]);
            }
        }
        // console.dir(totalDataArr);

        $.fn.dataTable.ext.errMode = 'none';
        $('#importSuccess_total')
            .on('error.dt', function (e, settings, techNote, message) {
                console.log('An error has been reported by DataTables: ', message);
            })
            .width('100%').dataTable({
            "searching": false, // 是否开启搜索功能
            "ordering": false,
            "destroy": true,
            "bLengthChange": false,
            "paging": false,
            "info": false,
            "autoWidth": false,
            "data": totalDataArr,
            "drawCallback": function (settings) {
                $('#importSuccess_total thead tr:eq(0) th').css('text-align', 'center');
                $('#importSuccess_total thead th').css('background-color', '#F5FCFD');
                $("table:eq(0) th").css("background-color", "#f6f7fb");
            }
        });
    },
    // 导入报告 - 成功  - 模型列表
    initFolderTable: function (report) {
        var dataArr = [];
        if (report) {
            var foldersObj = report.pri ? report.pri.folder : {};
            var foldersObj_versionNum = report.pri ? report.pri.statistics.modelVersionNumber : 1;
            var groupsObj = report.pub ? report.pub.modelGroup : {};
            var groupsObj_versionNum = report.pub ? report.pub.statistics.modelVersionNumber : 1;
            var folderArr = importReportModel.getFolderTableData('场景', foldersObj, foldersObj_versionNum);
            var groupArr = importReportModel.getFolderTableData('模型组', groupsObj, groupsObj_versionNum);
            dataArr = folderArr.concat(groupArr);
            // console.dir(dataArr);
        }

        $('#importSuccess_folder')
            .on('error.dt', function (e, settings, techNote, message) {
                console.log('An error has been reported by DataTables: ', message);
            })
            .width('100%').dataTable({
            "searching": false, // 是否开启搜索功能
            "ordering": false,
            "destroy": true,
            "bLengthChange": false,
            "paging": false,
            "info": false,
            "autoWidth": false,
            "columns": [
                {
                    "title": "类型",
                    "data": "folderType"
                },
                {
                    "title": "导入模型总数",
                    "data": "modelNum"
                },
                {
                    "title": "导入版本总数",
                    "data": "versionNum"
                },
                {
                    "title": "导入前名称",
                    "data": "folderNameBefore"
                },
                {
                    "title": "导入方式",
                    "data": "folderImportType"
                },
                {
                    "title": "导入后名称",
                    "data": "folderNameAfter"
                },
                {
                    "title": "导入前模型名称",
                    "data": "modelNameBefore"
                },
                {
                    "title": "模型导入方式",
                    "data": "modelImportType"
                },
                {
                    "title": "导入后模型名称",
                    "data": "modelNameAfter"
                },
                {
                    "title": "导入前版本",
                    "data": "versionBefore"
                },
                {
                    "title": "版本导入方式",
                    "data": "versionImportType"
                },
                {
                    "title": "导入后版本",
                    "data": "versionAfter"
                }
            ],
            "data": dataArr,
            "columnDefs": [
                {
                    "targets": [0, 1, 2], // 场景/组下所有模型所有版本个数
                    "createdCell": function (td, cellData, rowData, row, col) {
                        var rowspan = rowData.allFolderVersionNumFlag;
                        if (rowspan > 0) {
                            $(td).attr('rowspan', rowspan);
                        } else {
                            $(td).remove();
                        }
                    }
                },
                {
                    "targets": [3, 4, 5], // 该场景下版本个数
                    "createdCell": function (td, cellData, rowData, row, col) {
                        var rowspan = rowData.folderVersionNumFlag;
                        if (rowspan > 0) {
                            $(td).attr('rowspan', rowspan);
                        } else {
                            $(td).remove();
                        }
                    }
                },
                {
                    "targets": [6, 7, 8], // 该模型下版本个数
                    "createdCell": function (td, cellData, rowData, row, col) {
                        var rowspan = rowData.modelVersionFlag;
                        if (rowspan > 0) {
                            $(td).attr('rowspan', rowspan);
                        } else {
                            $(td).remove();
                        }
                    }
                }
            ],
            "drawCallback": function (settings) {
                $("#importSuccess_folder tr:nth-child(even)").css("background-color", "#ffffff");
            }
        });
    },
    // 解析导入模型数据
    getFolderTableData: function (folderType, foldersObj, versionNumTotal) {
        var dataArr = [];
        var importTypeMap = importReportModel.importTypeMap;

        var allFolderVersionNumFlag = -1; // 场景或者组下所有版本个数，决定012列
        // 遍历所有场景
        for (var folderId in foldersObj) {
            if (allFolderVersionNumFlag < 0) {
                allFolderVersionNumFlag = versionNumTotal;
            } else {
                allFolderVersionNumFlag = 0;
            }

            var folderObj = foldersObj[folderId];
            var folderVersionNum = folderObj['statistics']['modelVersionNumber']; // 单一场景下版本个数
            var folderNameBefore = folderObj['fromData']['name'];
            var folderNameAfter = folderObj['toData']['name'];
            var folderImportType = importTypeMap[folderObj['importSuccessType']];

            var modelsObj = foldersObj[folderId]['modelHeader'];
            var modelNum = modelsObj ? (Object.keys(modelsObj).length) : 0;
            var folderVersionNumFlag = folderVersionNum ? folderVersionNum : 0; // 单一场景下版本个数，决定345列
            // 遍历该场景下所有模型
            for (var modelId in modelsObj) {
                var modelObj = modelsObj[modelId];
                var modelNameBefore = modelObj['fromData']['name'];
                var modelNameAfter = modelObj['toData']['name'];
                var modelImportType = importTypeMap[modelObj['importSuccessType']];

                var modelVersionFlag = -1; // 该模型下版本个数，决定678列
                var versionsObj = modelsObj[modelId]['version'];
                for (var versionId in versionsObj) {
                    if (modelVersionFlag < 0) {
                        modelVersionFlag = versionsObj ? (Object.keys(versionsObj).length) : 0;
                    } else {
                        modelVersionFlag = 0;
                    }
                    var versionObj = versionsObj[versionId];
                    var versionBefore = versionObj['fromData']['name'];
                    var versionAfter = versionObj['toData']['name'];
                    var versionImportType = importTypeMap[versionObj['importSuccessType']];
                    dataArr.push({
                        'folderType': folderType,
                        'folderNameBefore': folderNameBefore,
                        'folderNameAfter': folderNameAfter,
                        'folderImportType': folderImportType,
                        'modelNum': modelNum,
                        'versionNum': versionNumTotal,
                        'modelNameBefore': modelNameBefore,
                        'modelNameAfter': modelNameAfter,
                        'modelImportType': modelImportType,
                        'versionBefore': versionBefore,
                        'versionAfter': versionAfter,
                        'versionImportType': versionImportType,
                        'allFolderVersionNumFlag': allFolderVersionNumFlag,
                        'folderVersionNumFlag': folderVersionNumFlag,
                        'modelVersionFlag': modelVersionFlag
                    });
                    if (folderVersionNumFlag > 0) {
                        folderVersionNumFlag = 0;
                    }
                    allFolderVersionNumFlag = 0;
                }
            }
        }
        return dataArr;
    },
    // 导入报告 - 成功  - 调整数据列表
    initChangeTable: function (report) {
        var dataArr = [];
        if (report) {
            var foldersObj = report.pri ? report.pri.folder : {};
            var foldersChangeNum = report.pri ? (report.pri.useUpdateDataStatistics ? report.pri.useUpdateDataStatistics.allNumber - report.pri.useUpdateDataStatistics.modelHeaderNumber - report.pri.useUpdateDataStatistics.modelVersionNumber : 1) : 1;
            var pubObj = report.pub ? report.pub : {};
            var folderArr = importReportModel.getPriChangeData(foldersObj, foldersChangeNum);
            var groupArr = importReportModel.getPubChangeData(pubObj);
            dataArr = folderArr.concat(groupArr);
            // console.dir(dataArr);
        }

        $('#importSuccess_change')
            .on('error.dt', function (e, settings, techNote, message) {
                console.log('An error has been reported by DataTables: ', message);
            })
            .width('100%').dataTable({
            "searching": false, // 是否开启搜索功能
            "ordering": false,
            "destroy": true,
            "bLengthChange": false,
            "paging": false,
            "info": false,
            "autoWidth": false,
            "columns": [
                {
                    "title": "类型",
                    "data": "changeType"
                },
                {
                    "title": "导入前名称",
                    "data": "typeNameBefore"
                },
                {
                    "title": "导入方式",
                    "data": "typeImportType"
                },
                {
                    "title": "导入后名称",
                    "data": "typeNameAfter"
                },
                {
                    "title": "数据类型",
                    "data": "dataType"
                },
                {
                    "title": "导入总数",
                    "data": "totalNum"
                },
                {
                    "title": "直接导入总数",
                    "data": "importNum"
                },
                {
                    "title": "使用系统导入总数",
                    "data": "systemNum"
                },
                {
                    "title": "调整总数",
                    "data": "changeNum"
                },
                {
                    "title": "导入前数据名称",
                    "data": "dataNameBefore"
                },
                {
                    "title": "导入前数据编码",
                    "data": "dataCodeBefore"
                },
                {
                    "title": "数据导入方式",
                    "data": "dataImportType"
                },
                {
                    "title": "导入后数据名称",
                    "data": "dataNameAfter"
                },
                {
                    "title": "导入后数据编码",
                    "data": "dataCodeAfter"
                }
            ],
            "data": dataArr,
            "columnDefs": [
                {
                    "targets": [0], // 类型
                    "createdCell": function (td, cellData, rowData, row, col) {
                        var rowspan = rowData.changeTypeFlag;
                        if (rowspan > 0) {
                            $(td).attr('rowspan', rowspan);
                        } else {
                            $(td).remove();
                        }
                    }
                },
                {
                    "targets": [1, 2, 3], // 具体场景/具体某一个组
                    "createdCell": function (td, cellData, rowData, row, col) {
                        var rowspan = rowData.groupFlag;
                        if (rowspan > 0) {
                            $(td).attr('rowspan', rowspan);
                        } else {
                            $(td).remove();
                        }
                    }
                }, {
                    "targets": [4, 5, 6, 7, 8], // 场景下数据类型/公共组下具体的数据
                    "createdCell": function (td, cellData, rowData, row, col) {
                        var rowspan = rowData.dataTypeFlag;
                        if (rowspan > 0) {
                            $(td).attr('rowspan', rowspan);
                        } else {
                            $(td).remove();
                        }
                    }
                }
            ],
            "drawCallback": function (settings) {
                $("#importSuccess_change tr:nth-child(even)").css("background-color", "#ffffff");
            }
        });
    },
    // 获取场景调整数据
    getPriChangeData: function (foldersObj, foldersChangeNum) {
        var dataArr = [];
        var importTypeMap = importReportModel.importTypeMap;
        var dataTypeMap = importReportModel.dataTypeMap;
        var changeTypeFlag = foldersChangeNum; // 等于某一个类型下调整的数据的总条数
        for (var folderId in foldersObj) {
            var folderObj = foldersObj[folderId];
            // var folderChangeNum = folderObj['useUpdateDataStatistics'] ? (folderObj['useUpdateDataStatistics']['allNumber']) : 1; // 场景下所有类型变动总数
            var folderChangeNum = folderObj['useUpdateDataStatistics'] ? (folderObj['useUpdateDataStatistics']['allNumber']) - (folderObj['useUpdateDataStatistics']['modelHeaderNumber']) - (folderObj['useUpdateDataStatistics']['modelVersionNumber']) : 1; // 场景下所有类型变动总数
            var groupFlag = folderChangeNum ? folderChangeNum : 1;
            var statistics = folderObj['statistics'];
            var useImportDataStatistics = folderObj['useImportDataStatistics'];
            var useUpdateDataStatistics = folderObj['useUpdateDataStatistics'];
            var useSystemDataStatistics = folderObj['useSystemDataStatistics'];

            var typeNameBefore = folderObj['fromData']['name'];
            var typeNameAfter = folderObj['toData']['name'];
            var typeImportType = importTypeMap[folderObj['importSuccessType']];

            for (var key in folderObj) {
                if (key === 'fromData' || key === 'toData' || key === 'importSuccessType' || key === 'modelHeader' || key === 'statistics' || key === 'useImportDataStatistics' || key === 'useUpdateDataStatistics' || key === 'useSystemDataStatistics') {
                    continue;
                }
                if (!dataTypeMap[key]) {
                    console.log('function getPriChangeData, folderObj key type missed, key: ', key);
                    continue;
                }
                var dataType = dataTypeMap[key]['type'];
                var numCol = dataTypeMap[key]['numCol'];
                var totalNum = statistics ? (statistics[numCol]) : 0;
                var importNum = useImportDataStatistics ? (useImportDataStatistics[numCol]) : 0;
                var systemNum = useSystemDataStatistics ? (useSystemDataStatistics[numCol]) : 0;
                var changeNum = useUpdateDataStatistics ? (useUpdateDataStatistics[numCol]) : 0;

                var changeTypeObjs = folderObj[key];
                var dataTypeFlag = -1;
                for (var changeDataId in changeTypeObjs) {
                    if (dataTypeFlag < 0) {
                        dataTypeFlag = changeTypeObjs ? (Object.keys(changeTypeObjs).length) : 1; // 某类型下某数据类型的条数
                    } else {
                        dataTypeFlag = 0;
                    }
                    var changeDataObj = changeTypeObjs[changeDataId];
                    var dataNameBefore = changeDataObj['fromData']['name']
                    var dataCodeBefore = changeDataObj['fromData']['code'];
                    var dataNameAfter = changeDataObj['toData']['name'];
                    var dataCodeAfter = changeDataObj['toData']['code'];
                    var dataImportType = importTypeMap[changeDataObj['importSuccessType']];
                    dataArr.push(
                        {
                            'changeType': '场景',
                            'typeNameBefore': typeNameBefore,
                            'typeImportType': typeImportType,
                            'typeNameAfter': typeNameAfter,
                            'dataType': dataType,
                            'totalNum': totalNum,
                            'importNum': importNum,
                            'systemNum': systemNum,
                            'changeNum': changeNum,
                            'dataNameBefore': dataNameBefore,
                            'dataCodeBefore': dataCodeBefore,
                            'dataImportType': dataImportType,
                            'dataNameAfter': dataNameAfter,
                            'dataCodeAfter': dataCodeAfter,
                            'changeTypeFlag': changeTypeFlag,
                            'groupFlag': groupFlag,
                            'dataTypeFlag': dataTypeFlag
                        }
                    );
                    if (groupFlag > 0) {
                        groupFlag = 0;
                    }
                    changeTypeFlag = 0;
                }
            }
        }
        return dataArr;
    },
    // 获取公共调整数据
    getPubChangeData: function (pubObj) {
        var dataArr = [];
        var importTypeMap = importReportModel.importTypeMap;
        var dataTypeMap = importReportModel.dataTypeMap;
        var pub_useUpdateDataStatistics = pubObj['useUpdateDataStatistics'];

        for (var key in pubObj) {
            if (key === 'modelGroup' || key === 'statistics' || key === 'useImportDataStatistics' || key === 'useUpdateDataStatistics' || key === 'useSystemDataStatistics') {
                continue;
            }
            if (!dataTypeMap[key]) {
                console.log('function getPubChangeData, pubObj key missed, key: ', key);
                continue;
            }
            var changeType = dataTypeMap[key]['type'];
            var changeTypeObjs = pubObj[key];
            var changeNumCol_type = dataTypeMap[key]['changeNumCol'];
            var changeTypeNum = pub_useUpdateDataStatistics ? (pub_useUpdateDataStatistics[changeNumCol_type]) : 1; // 等于当前类型组下调整的数据的总条数
            var changeTypeFlag = changeTypeNum;

            // 遍历各个公共组
            for (var changeTypeId in changeTypeObjs) {
                var changeTypeObj = changeTypeObjs[changeTypeId];
                var statistics = changeTypeObj['statistics'];
                var useImportDataStatistics = changeTypeObj['useImportDataStatistics'];
                var useUpdateDataStatistics = changeTypeObj['useUpdateDataStatistics'];
                var useSystemDataStatistics = changeTypeObj['useSystemDataStatistics'];

                var typeNameBefore = changeTypeObj['fromData']['name'];
                var typeNameAfter = changeTypeObj['toData']['name'];
                var typeImportType = importTypeMap[changeTypeObj['importSuccessType']];

                var groupChangeNum = useUpdateDataStatistics ? (useUpdateDataStatistics[changeNumCol_type]) : 1; // 具体组下调整总数
                var groupFlag = groupChangeNum;

                // 遍历公共组下所有数据的对象
                for (var changeTypeKey in changeTypeObj) {
                    if (changeTypeKey === 'fromData' || changeTypeKey === 'toData' || changeTypeKey === 'importSuccessType' || changeTypeKey === 'statistics' || changeTypeKey === 'useImportDataStatistics' || changeTypeKey === 'useUpdateDataStatistics' || changeTypeKey === 'useSystemDataStatistics') {
                        continue;
                    }
                    if (!dataTypeMap[changeTypeKey]) {
                        console.log('function getPubChangeData, changeTypeObj changeTypeKey missed, key: ', changeTypeKey);
                        continue;
                    }
                    var dataType = dataTypeMap[changeTypeKey]['type'];
                    var numCol = dataTypeMap[changeTypeKey]['numCol'];
                    var totalNum = statistics ? (statistics[numCol]) : 0;
                    var importNum = useImportDataStatistics ? (useImportDataStatistics[numCol]) : 0;
                    var systemNum = useSystemDataStatistics ? (useSystemDataStatistics[numCol]) : 0;
                    var changeNum = useUpdateDataStatistics ? (useUpdateDataStatistics[numCol]) : 0;

                    var changeDataObjs = changeTypeObj[changeTypeKey];
                    var dataTypeFlag = -1;
                    // 遍历单个数据
                    for (var changeDataId in changeDataObjs) {
                        if (dataTypeFlag < 0) {
                            dataTypeFlag = changeDataObjs ? (Object.keys(changeDataObjs).length) : 1; // 某类型下某数据类型的条数
                        } else {
                            dataTypeFlag = 0;
                        }
                        var changeDataObj = changeDataObjs[changeDataId];
                        var dataNameBefore = changeDataObj['fromData']['name']
                        var dataCodeBefore = changeDataObj['fromData']['code'];
                        var dataNameAfter = changeDataObj['toData']['name'];
                        var dataCodeAfter = changeDataObj['toData']['code'];
                        var dataImportType = importTypeMap[changeDataObj['importSuccessType']];
                        dataArr.push(
                            {
                                'changeType': changeType,
                                'typeNameBefore': typeNameBefore,
                                'typeImportType': typeImportType,
                                'typeNameAfter': typeNameAfter,
                                'dataType': dataType,
                                'totalNum': totalNum,
                                'importNum': importNum,
                                'systemNum': systemNum,
                                'changeNum': changeNum,
                                'dataNameBefore': dataNameBefore,
                                'dataCodeBefore': dataCodeBefore,
                                'dataImportType': dataImportType,
                                'dataNameAfter': dataNameAfter,
                                'dataCodeAfter': dataCodeAfter,
                                'changeTypeFlag': changeTypeFlag,
                                'groupFlag': groupFlag,
                                'dataTypeFlag': dataTypeFlag
                            }
                        );
                        groupFlag = 0;
                        changeTypeFlag = 0;
                    }
                }
            }
        }
        return dataArr;
    },
    // 导入报告 - 失败
    initLackTable: function (lack) {
        // lack = JSON.parse('{"modelHeader":{"4028a1817177371b0171773d8c7e014e":{"modelName":"4028a1817177371b0171773d8c7e014e","ruleName":"4028a1817177371b0171773d8c7e014e","success":true,"version":{"4028a1817177371b01717742f5b20307":{"success":true,"version":"1.02","versionId":"4028a1817177371b01717742f5b20307"},"4028a1817177371b01717741656f023d":{"success":true,"version":"1.01","versionId":"4028a1817177371b01717741656f023d"},"4028a1817177371b017177431fde03cd":{"success":true,"version":"1.03","versionId":"4028a1817177371b017177431fde03cd"},"ff8080817189b65601718ba4330e046a":{"success":true,"version":"1.04","versionId":"ff8080817189b65601718ba4330e046a"}}},"4028a1233":{"modelName":"4028a1817177371b0171773d8c7e014e","ruleName":"4028a1817177371b0171773d8c7e014e","success":true,"version":{"4028a1817177371b01717742f5b20307":{"success":true,"version":"1.02","versionId":"4028a1817177371b01717742f5b20307"},"4028a1817177371b01717741656f023d":{"success":true,"version":"1.01","versionId":"4028a1817177371b01717741656f023d"},"4028a1817177371b017177431fde03cd":{"success":true,"version":"1.03","versionId":"4028a1817177371b017177431fde03cd"},"ff8080817189b65601718ba4330e046a":{"success":true,"version":"1.04","versionId":"ff8080817189b65601718ba4330e046a"}}}}}');
        // lack = JSON.parse('{"kpi":{"ff8080817189b65601718ba30b210383":{"from":{"modelHeader":{"4028a181719a9f1901719a9fb56a00fe":{"modelName":"导出测试模型7","ruleName":"4028a181719a9f1901719a9fb56a00fe","success":true,"version":{"ff8080817189b65601718ba4330e046a":{"success":true,"version":"1.04","versionId":"ff8080817189b65601718ba4330e046a"},"ff8080817189b656012346a":{"success":true,"version":"1.03","versionId":"ff8080817189b65601718ba4330e046a"}}},"4028a181719a9f1901719a9fb5123":{"modelName":"导出测试模型6","ruleName":"4028a181719a9f1901719a9fb56a00fe","success":true,"version":{"ff8080817189b65601718ba4330e046a":{"success":true,"version":"1.02","versionId":"ff8080817189b65601718ba4330e046a"},"ff8080817189b656012346a":{"success":true,"version":"1.01","versionId":"ff8080817189b65601718ba4330e046a"}}}}},"kpiId":"ff8080817189b65601718ba30b210383","success":true}}}');
        // lack = JSON.parse('{"variable":{"2c91e2516e1c352f016e1c39e3e40005":{"from":{"kpi":{"ff8080817189b65601718ba30b210383":{"kpiId":"ff8080817189b65601718ba30b210383","kpiName":"导出测试指标_接口方式","success":true}}},"success":true,"variableId":"2c91e2516e1c352f016e1c39e3e40005"},"2c91e2516f27aa3c016f27c28e50009b":{"from":{"modelHeader":{"4028a181719a9f1901719a9fb56a00fe":{"modelName":"导出测试模型7","ruleName":"4028a181719a9f1901719a9fb56a00fe","success":true,"version":{"ff8080817189b65601718ba4330e046a":{"success":true,"version":"1.04","versionId":"ff8080817189b65601718ba4330e046a"}}},"4028a181719a9f1901719a9fb1a50053":{"modelName":"导出测试模型7","ruleName":"4028a181719a9f1901719a9fb1a50053","success":true,"version":{"4028a1817177371b01717742f5b20307":{"success":true,"version":"1.02","versionId":"4028a1817177371b01717742f5b20307"}}},"4028a181719a9f1901719a9fb43300c3":{"modelName":"导出测试模型7","ruleName":"4028a181719a9f1901719a9fb43300c3","success":true,"version":{"4028a1817177371b017177431fde03cd":{"success":true,"version":"1.03","versionId":"4028a1817177371b017177431fde03cd"}}}}},"success":true,"variableId":"2c91e2516f27aa3c016f27c28e50009b"},"8a8ae90171728db7017173c2cf8807c4":{"from":{"api":{"4028a1817177371b0171773c5922007e":{"apiDesc":"导出公共接口","apiId":"4028a1817177371b0171773c5922007e","apiName":"导出公共接口","success":true},"4028a1817177371b0171773995fc0047":{"apiDesc":"草除场景测试接口","apiId":"4028a1817177371b0171773995fc0047","apiName":"导出场景测试接口","success":true}}},"success":true,"variableId":"8a8ae90171728db7017173c2cf8807c4"}}}');
        // lack = JSON.parse('{"variable":{"2c91e2516e1c352f016e1c39e3e40005":{"static":{"lackNum":3},"from":{"kpi":{"ff8080817189b65601718ba30b210383":{"kpiId":"ff8080817189b65601718ba30b210383","kpiName":"导出测试指标_接口方式","success":true}},"variable":{"2c91e2516d616370016d616370c50001":{"success":true,"variableAlias":"公共参数-个人信息","variableCode":"public_personal_info","variableId":"2c91e2516d616370016d616370c50001"}},"api":{"8a8ae5c16ecfa348016ecfcff844000c":{"apiDesc":"获取个人信息","apiId":"8a8ae5c16ecfa348016ecfcff844000c","apiName":"获取个人信息","success":true}}},"success":true,"variableId":"2c91e2516e1c352f016e1c39e3e40005"},"4028a1817177371b01717740af030237":{"static":{"lackNum":3},"from":{"modelHeader":{"4028a1817177371b0171773d8c7e014e":{"modelName":"导出测试模型","ruleName":"4028a1817177371b0171773d8c7e014e","success":true,"version":{"4028a1817177371b01717742f5b20307":{"success":true,"version":"1.02","versionId":"4028a1817177371b01717742f5b20307"},"4028a1817177371b017177431fde03cd":{"success":true,"version":"1.03","versionId":"4028a1817177371b017177431fde03cd"},"ff8080817189b65601718ba4330e046a":{"success":true,"version":"1.04","versionId":"ff8080817189b65601718ba4330e046a"}}}}},"success":true,"variableId":"4028a1817177371b01717740af030237"},"4028a1817177371b01717738b93e0034":{"static":{"lackNum":3},"from":{"modelHeader":{"4028a1817177371b0171773d8c7e014e":{"modelName":"导出测试模型","ruleName":"4028a1817177371b0171773d8c7e014e","success":true,"version":{"4028a1817177371b01717741656f023d":{"success":true,"version":"1.01","versionId":"4028a1817177371b01717741656f023d"}}}},"api":{"4028a1817177371b0171773c5922007e":{"apiDesc":"导出公共接口","apiId":"4028a1817177371b0171773c5922007e","apiName":"导出公共接口","success":true},"4028a1817177371b0171773995fc0047":{"apiDesc":"草除场景测试接口","apiId":"4028a1817177371b0171773995fc0047","apiName":"导出场景测试接口","success":true}}},"success":true,"variableId":"4028a1817177371b01717738b93e0034"}}}');
        // console.log(JSON.stringify(lack));
        // console.dir(lack);
        var lackDataArr = [];
        if (lack) {
            var lackMap = importReportModel.lackMap;
            for (var lackTypeKey in lack) {
                if (!lackMap[lackTypeKey]) {
                    console.log('function initLackTable, lackTypeKey missed: ', lackTypeKey);
                    continue;
                }
                if (lackTypeKey === 'modelHeader') { // 过滤 modelHeader
                    continue;
                }
                var lackObjs = lack[lackTypeKey]; // 某一缺失类型的所有缺失对象 的对象
                var lackType = lackMap[lackTypeKey]['type'];
                var lackIdCol = lackMap[lackTypeKey]['id'];
                var lackNameCol = lackMap[lackTypeKey]['name'];

                for (var lackObjKey in lackObjs) {
                    var lackObj = lackObjs[lackObjKey];
                    var lackId = lackObj[lackIdCol] ? lackObj[lackIdCol] : '';
                    var lackName = lackObj[lackNameCol] ? lackObj[lackNameCol] : '';

                    var lackTypeTotalNum = lackObj['lackStatistics'] ? lackObj['lackStatistics']['allNumber'] : 1; // 获取当前对象缺失来源的数据总数
                    var hasLackTypeTotalFlag = lackObj['lackStatistics'] ? (!!lackObj['lackStatistics']['allNumber']) : false; // 是否有上述统计项
                    var lackTypeNumFlag = lackTypeTotalNum;

                    // var versionFlag = !!lackObj['version'];
                    // if (versionFlag) { // 缺失类型为模型
                    //     var versionObjs = lackObj['version'];
                    //     var versionCol = lackMap[lackTypeKey]['version'] ? lackMap[lackTypeKey]['version'] : '';
                    //     var dataNumFlag = -1;
                    //     for (var versionObjId in versionObjs) { // 遍历单个模型所有版本
                    //         if (dataNumFlag < 0) {
                    //             dataNumFlag = versionObjs ? (Object.keys(versionObjs).length) : 0;
                    //         } else {
                    //             dataNumFlag = 0;
                    //         }
                    //         lackDataArr.push({
                    //             'lackType': lackType,
                    //             'lackId': lackId,
                    //             'lackName': lackName,
                    //             'lackFromType': '',
                    //             'lackFromName': '',
                    //             'version': versionObjs[versionObjId][versionCol],
                    //             'lackTypeNumFlag': lackTypeNumFlag,
                    //             'lackFromNumFlag': dataNumFlag
                    //         });
                    //         if (hasLackTypeTotalFlag) {
                    //             lackTypeNumFlag = 0;
                    //         }
                    //     }
                    // } else {
                    var fromObjs = lackObj['from'];
                    for (var fromLackKey in fromObjs) { // 遍历所有缺失来源对象 的对象
                        if (!lackMap[fromLackKey]) { // fromLackKey: kpi modelHeader...
                            console.log('function initLackTable, fromLackKey missed: ', fromLackKey);
                            continue;
                        }
                        var lackFromType = lackMap[fromLackKey]['type'];
                        var fromLackNameCol = lackMap[fromLackKey]['name'];
                        var versionCol = lackMap[fromLackKey]['version'] ? lackMap[fromLackKey]['version'] : '';

                        var numFlag = -1;
                        var fromLackObjs = fromObjs[fromLackKey];
                        for (var lackObjKey in fromLackObjs) {
                            if (numFlag < 0) {
                                numFlag = fromLackObjs ? (Object.keys(fromLackObjs).length) : 0;
                            } else {
                                numFlag = 0;
                            }

                            var fromLackObj = fromLackObjs[lackObjKey];
                            var lackFromName = fromLackObj[fromLackNameCol];
                            var versionFlag = !!fromLackObj['version']; // 缺失来源是否包含版本信息
                            var fromVerNumFlag = -1;
                            if (versionFlag) { // 有版本信息
                                var versionObjs = fromLackObj['version'];
                                for (var versionObjId in versionObjs) {
                                    if (fromVerNumFlag < 0) {
                                        fromVerNumFlag = versionObjs ? (Object.keys(versionObjs).length) : 0;
                                    } else {
                                        fromVerNumFlag = 0;
                                    }
                                    lackDataArr.push({
                                        'lackType': lackType,
                                        'lackId': lackId,
                                        'lackName': lackName,
                                        'lackFromType': lackFromType,
                                        'lackFromName': lackFromName,
                                        'version': versionObjs[versionObjId][versionCol],
                                        'lackTypeNumFlag': lackTypeNumFlag,
                                        'lackFromNumFlag': fromVerNumFlag
                                    });
                                    if (hasLackTypeTotalFlag) {
                                        lackTypeNumFlag = 0;
                                    }
                                }
                            } else {
                                lackDataArr.push({
                                    'lackType': lackType,
                                    'lackId': lackId,
                                    'lackName': lackName,
                                    'lackFromType': lackFromType,
                                    'lackFromName': lackFromName,
                                    'version': '',
                                    'lackTypeNumFlag': lackTypeNumFlag,
                                    'lackFromNumFlag': numFlag
                                });
                                if (hasLackTypeTotalFlag) {
                                    lackTypeNumFlag = 0;
                                }
                            }
                        }
                    }
                    // }
                }
            }
        }

        // console.log(JSON.stringify(lackDataArr));

        $.fn.dataTable.ext.errMode = 'none';
        $('#importFailed_lack')
            .on('error.dt', function (e, settings, techNote, message) {
                console.log('An error has been reported by DataTables: ', message);
            })
            .width('100%').dataTable({
            "searching": false, // 是否开启搜索功能
            "ordering": false,
            "destroy": true,
            "bLengthChange": false,
            "paging": false,
            "info": false,
            "autoWidth": false,
            "columns": [
                {
                    "title": "缺失类型",
                    "data": "lackType"
                },
                {
                    "title": "缺失ID",
                    "data": "lackId"
                },
                {
                    "title": "缺失名称",
                    "data": "lackName"
                },
                {
                    "title": "缺失来源类型",
                    "data": "lackFromType"
                },
                {
                    "title": "缺失来源名称",
                    "data": "lackFromName"
                },
                {
                    "title": "来源版本",
                    "data": "version"
                }
            ],
            "data": lackDataArr,
            "columnDefs": [
                {
                    "targets": [0, 1, 2],
                    "createdCell": function (td, cellData, rowData, row, col) {
                        var rowspan = rowData.lackTypeNumFlag;
                        if (rowspan > 0) {
                            $(td).attr('rowspan', rowspan);
                        } else {
                            $(td).remove();
                        }
                    }
                },
                {
                    "targets": [3], // 来源类型
                    "createdCell": function (td, cellData, rowData, row, col) {
                        var rowspan = rowData.lackFromNumFlag;
                        if (rowspan > 0) {
                            $(td).attr('rowspan', rowspan);
                        } else {
                            $(td).remove();
                        }
                    }
                }
            ],
            "drawCallback": function (settings) {
                $("#importFailed_lack tr:nth-child(even)").css("background-color", "#ffffff");
            }
        });

    }
}

