/**
 * 日志管理
 * data:2019/05/08
 * author:bambi
 */
var logModal = {
    activeLogTypeId: '2',
    // 初始化日志页面
    initLogPage: function () {
        logModal.activeLogTypeId = $('#logTab .active').attr('log-type');
        logModal.handleTabChange(); // tab页切换事件绑定
        logModal.handleSearch(); // 搜索
        logModal.initTimePicker(); // 初始化时间选择插件
        logModal.initModelOprType(); // 初始化模型操作类型
        logModal.initRuleFolder(); // 初始化场景列表
        logModal.handleListChange(); // 下拉框信息同步至输入框
        logModal.initSearchContent(); // 初始化搜索条件
        logModal.initTable(); // 初始化表格
    },
    // tab切换事件绑定
    handleTabChange: function () {
        $('#logTab li').unbind().click(function () {
            logModal.activeLogTypeId = $(this).attr('log-type');

            // 销毁表格并重新绘制
            var table = $('#logTable').dataTable();
            // table.fnClearTable(); // 清空表格数据
            table.fnDestroy(); // 销毁dataTable
            $('#logTable').remove();
            $('#logContent').append('<table id="logTable"></table>');

            // 重置页面内容
            $('.input-group .form-control').val('');
            logModal.initSearchContent();
            logModal.initTable();
        });
    },
    // 选择下拉框选项后同步到输出框内
    handleListChange: function () {
        $('#logSearchContainer .dropdown-menu li').unbind().click(function () {
            $(this).parent().siblings('input').val($(this).text());
        });
    },
    // 搜索
    handleSearch: function () {
        $('#searchBtn').unbind().click(function () {
            var logType = logModal.activeLogTypeId;
            var inputs = $('#logContainer .input-group .form-control');
            var obj = {};
            for (var i = 0; i < inputs.length; i++) {
                if ($.trim($(inputs[i]).val()) === '') {
                    continue;
                }
                if ($(inputs[i]).attr('data-col') === 'modelOperateTypeInput' && $.trim($(inputs[i]).val()) === '全部') {
                    continue;
                }
                obj[$(inputs[i]).attr('data-col')] = $.trim($(inputs[i]).val());
            }
            // 规则执行日志
            if (obj.state) {
                obj.state = $("#log_ruleStateList li[value='" + obj.state + "']").attr('col-value');
            }
            if (obj.folderId) {
                if (logType === '2') {
                    obj.folderId = $('#logSearchContainer .ruleLog .folderIdInput').attr('folderId');
                }
                if (logType === '4') {
                    obj.folderId = $('#logSearchContainer .modelOperateLog .folderIdInput').attr('folderId');
                }
            }
            if (obj.ruleId) {
                if (logType === '2') {
                    obj.ruleId = $('#logSearchContainer .ruleLog .ruleVersionInput').attr('ruleId');
                }
                // if (logType === '4') {
                //     obj.ruleId = $('#logSearchContainer .modelOperateLog .ruleVersionInput').attr('ruleId');
                // }
            }
            if (obj.operateType) {
                if (obj.operateType === '全部') {
                    obj.operateType = '';
                }
                if (logType === '4') {
                    if (obj.operateType) {
                        obj.operateType = $('#modelOperateTypeInput').attr('operateType');
                    }
                }
                if (logType === '5') {
                    if (obj.operateType) {
                        obj.operateType = $("#importAndExportOperateTypeList li[typeValue='" + obj.operateType + "']").attr('operateType');
                    }
                }
            }
            if (obj.success) {
                if (obj.success === '全部') {
                    obj.success = '';
                }
                obj.success = $("#importAndExportResultList li[typeValue='" + obj.success + "']").attr('result');
            }
            if (obj.modelImportStrategy) {
                if (obj.modelImportStrategy === '全部') {
                    obj.modelImportStrategy = '';
                }
                obj.modelImportStrategy = $("#modelImportStrategyList li[typeValue='" + obj.modelImportStrategy + "']").attr('result');
            }
            logModal.initTable(obj);
        });
    },
    // 时间选择插件：选择时间和日期
    initTimePicker: function () {
        $(".form-datetime").datetimepicker({
            weekStart: 1,
            todayBtn: 1,
            autoclose: 1,
            todayHighlight: 1,
            startView: 2,
            forceParse: 0,
            showMeridian: 1,
            format: "yyyy-mm-dd hh:ii:ss"
        });
    },
    // 初始化模型操作类型
    initModelOprType: function () {
        $.ajax({
            url: webpath + "/modelOperateLog/getModelOperateType",
            data: {},
            type: "GET",
            dataType: "json",
            success: function (data) {
                if (data.status === 0) {
                    var htmlStr = '<li operateType=""><a>全部</a></li>';
                    if (data.data.length > 0) {
                        for (var i = 0; i < data.data.length; i++) {
                            htmlStr += '<li operateType=\'' + data.data[i].operateCode + '\'><a>' + data.data[i].operatedesc + '</a></li>';
                        }
                    } else {
                        htmlStr += '<li><a>--无--</a></li>';
                    }
                    $('.modelOperateTypeList').html(htmlStr);
                    $('.modelOperateTypeList li').unbind().click(function () {
                        $('#modelOperateTypeInput').val($(this).text());
                        $('#modelOperateTypeInput').attr('operateType', $(this).attr('operateType'));
                    });
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 初始化规则包下拉框
    initRuleFolder: function () {
        $.ajax({
            url: webpath + "/ruleFolder",
            dataType: "json",
            success: function (data) {
                var htmlStr = '<li folderId=\'' + pubFolderId + '\' onclick="logModal.getRules(\'' + pubFolderId + '\', 1)"><a>模型库</a></li>';
                if (data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        htmlStr += '<li folderId=\'' + data[i].key + '\' onclick="logModal.getRules(\'' + data[i].key + '\', 0)"><a>' + data[i].text + '</a></li>';
                    }
                }
                $('.ruleFolderList').html(htmlStr);
                // 为li绑定点击事件同步选项到input并设置folderId属性
                $('.ruleFolderList li').click(function () {
                    $(this).parent().siblings('input').val($(this).text()).attr('folderId', $(this).attr('folderId'));
                });
            }
        })
    },
    // 根据规则包获取包下所有规则
    getRules: function (foldId, isPublic) {
        $('.ruleList, .versionList').empty();
        $('.ruleIdInput, .ruleVersionInput').val('');
        $('.ruleVersionInput').removeAttr('ruleId');
        $.ajax({
            url: webpath + "/ruleFolder/ruleName",
            data: {"foldId": foldId},
            dataType: "json",
            success: function (data) {
                var htmlStr = "";
                if (data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        htmlStr += '<li onclick="logModal.getModelVersions(\'' + data[i].ruleName + '\', \'' + isPublic + '\')"><a>' + data[i].moduleName + '</a></li>';
                    }
                } else {
                    htmlStr += '<li><a>--无--</a></li>';
                }
                $('.ruleList').html(htmlStr);
                // 为li绑定点击事件同步选项到input
                $('.ruleList li').click(function () {
                    $(this).parent().siblings('input').val($(this).text());
                });
            }
        })
    },
    // 获取模型下启用中的版本
    getModelVersions: function (ruleName, isPublic) {
        $('.versionList').empty();
        $('.ruleVersionInput').val('').removeAttr('ruleId');
        $.ajax({
            url: webpath + "/rule/versions",
            data: {"ruleName": ruleName, "isPublic": isPublic},
            type: "GET",
            dataType: "json",
            success: function (data) {
                if (data.status === 0) {
                    var htmlStr = "";
                    if (data.data.length > 0) {
                        for (var i = 0; i < data.data.length; i++) {
                            htmlStr += '<li ruleId=\'' + data.data[i].ruleId + '\'><a>' + data.data[i].version + '</a></li>';
                        }
                    } else {
                        htmlStr += '<li><a>--无--</a></li>';
                    }
                    $('.versionList').html(htmlStr);
                    $('.versionList li').unbind().click(function () {
                        $(this).parent().siblings('input').val($(this).text()).attr('ruleId', $(this).attr('ruleId'));
                    });
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 获取表头
    getTitles: function () {
        // 系统日志
        var sysLogCols = [
            {"title": "账号", "data": "userId", "width": "20%"},
            {"title": "用户名称", "data": "userName", "width": "20%"},
            {"title": "IP地址", "data": "ip", "width": "20%"},
            {"title": "操作类型", "data": "operateType", "width": "20%"},
            {"title": "时间", "data": "operateTime", "width": "20%"}
        ];
        // 用户操作日志
        var operationLogCols = [
            {"title": "账号", "data": "userId", "width": "16%"},
            {"title": "用户名称", "data": "userName", "width": "16%"},
            {"title": "IP地址", "data": "ip", "width": "16%"},
            {"title": "操作类型", "data": "operateType", "width": "16%"},
            {"title": "时间", "data": "operateTime", "width": "20%"},
            {
                "title": "操作信息", "data": null, "width": "16%", "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<span type="button" class="cm-tblB logDetailBtn" data-position="center" detail-col-name="operateInfo" >详情</span>';
                    return htmlStr;
                }
            }
        ];
        // 模型执行日志
        var ruleLogCols = [
            {"title": "场景名称", "data": "folderName", "width": "12%"},
            {"title": "模型名称", "data": "moduleName", "width": "16%"},
            {"title": "版本号", "data": "version", "width": "12%"},
            {
                "title": "执行状态", "data": "state", "width": "12%", "render": function (data) {
                    switch ($.trim(data)) {
                        case '1':
                            return '执行中';
                        case '2':
                            return '执行完成';
                        case '-1':
                            return '异常';
                        default:
                            return '--';
                    }
                }
            },
            {"title": "规则命中个数", "data": "hitRuleNum", "width": "12%"},
            {"title": "开始时间", "data": "startTime", "width": "12%"},
            {"title": "结束时间", "data": "endTime", "width": "12%"},
            {
                "title": "操作信息", "data": null, "width": "12%", "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<span type="button" class="cm-tblB logDetailBtn" onclick="logRuleDetailModal.show(\'' + row.logId + '\')" data-position="center">详情</span>';
                    return htmlStr;
                }
            }
        ];
        // 接口日志
        var apiLogCols = [
            {"title": "接口名称", "data": "apiName", "width": "25%"},
            {"title": "返回结果", "data": "callResult", "width": "25%"},
            {"title": "日志发生时间", "data": "logOccurTime", "width": "25%"},
            {
                "title": "参数", "data": null, "width": "25%", "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<span type="button" class="cm-tblB logDetailBtn" data-position="center" detail-col-name="logParam" >详情</span>';
                    return htmlStr;
                }
            }
        ];
        // 模型操作日志
        var modelOperateLogCols = [
            {"title": "场景名称", "data": "folderName", "width": "9%"},
            {"title": "模型名称", "data": "modelName", "width": "9%"},
            {"title": "版本号", "data": "version", "width": "9%"},
            {"title": "操作后场景名称", "data": "newFolderName", "width": "9%"},
            {"title": "操作后模型名称", "data": "newModelName", "width": "9%"},
            {"title": "操作后版本号", "data": "newVersion", "width": "9%"},
            {"title": "ip", "data": "ip", "width": "9%"},
            {
                "title": "操作类型", "data": "operateType", "width": "9%", "render": function (data) {
                    switch ($.trim(data)) {
                        case '0':
                            return '创建模型基础信息';
                        case '1':
                            return '删除模型';
                        case '2':
                            return '删除版本';
                        case '3':
                            return '暂存';
                        case '4':
                            return '提交';
                        case '5':
                            return '发布';
                        case '6':
                            return '启用';
                        case '7':
                            return '停用';
                        case '8':
                            return '克隆';
                        case '9':
                            return '修改模型基础信息';
                        default:
                            return '--';
                    }
                }
            },
            {"title": "操作人", "data": "operatePerson", "width": "9%"},
            {"title": "操作时间", "data": "operateTime", "width": "9%"},
            {
                "title": "操作信息", "data": null, "width": "9%", "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<span type="button" class="cm-tblB logDetailBtn" data-position="center" detail-col-name="operateContent" >详情</span>';
                    return htmlStr;
                }
            }
        ];
        // 导入导出日志
        var importAndExportLogCols = [
            {
                "title": "操作类型", "data": "operateType", "width": "10%", "render": function (data) {
                    switch ($.trim(data)) {
                        case 'export':
                            return '导出';
                        case 'import':
                            return '导入';
                        default:
                            return '--';
                    }
                }
            },
            {
                "title": "导入策略", "data": "modelImportStrategy", "width": "15%", "render": function (data) {
                    switch ($.trim(data)) {
                        case 'removal':
                            return '冲突重命名';
                        case 'upLine':
                            return '冲突新建版本';
                        default:
                            return '--';
                    }
                }
            },
            {"title": "系统版本", "data": "systemVersion", "width": "10%"},
            {
                "title": "结果", "data": "success", "width": "10%", "render": function (data) {
                    switch ($.trim(data)) {
                        case '1':
                            return '成功';
                        case '-1':
                            return '失败';
                        default:
                            return '--';
                    }
                }
            },
            {"title": "操作人", "data": "operatePerson", "width": "12.5%"},
            {"title": "操作时间", "data": "operateDate", "width": "20%"},
            {"title": "文件名称", "data": "fileName", "width": "12.5%"},
            {
                "title": "操作信息", "data": null, "width": "10%", "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<div id="logDetail_' + row.logId + '" style="display:none;">' + JSON.stringify(row) + '</div>'
                    htmlStr += '<span type="button" class="cm-tblB logDetailBtn" data-position="center" ' +
                        'onclick="importAndExportDetailModal.show(\'' + row.operateType + '\', \'' + row.logId + '\')">详情</span>';
                    $("#logDetail_" + row.logId).data("rowData", row);
                    return htmlStr;
                }
            }
        ];
        switch ($.trim(logModal.activeLogTypeId)) {
            case '0':
                return sysLogCols;
            case '1':
                return operationLogCols;
            case '2':
                return ruleLogCols;
            case '3':
                return apiLogCols;
            case '4':
                return modelOperateLogCols;
            case '5':
                return importAndExportLogCols;
            default:
                return [];
        }
    },
    // 获取表格url
    getUrl: function () {
        switch ($.trim(logModal.activeLogTypeId)) {
            case '0':
                return '/log/sys';
            case '1':
                return '/log/operate';
            case '2':
                return '/log/rule';
            case '3':
                return '/log/api';
            case '4':
                return '/modelOperateLog/selectModelOperateLog';
            case '5':
                return '/model/importAndExport/selectImportAndExportOperateLogWithContentPage/page';
            default:
                return '';
        }
    },
    // 初始化搜索条件
    initSearchContent: function () {
        var typeId = logModal.activeLogTypeId;
        $('#special .input-group').css('display', 'none');
        if (typeId === '0') {
            $('#special .syslog').css('display', 'block');
        } else if (typeId === '1') {
            $('#special .operateLog').css('display', 'block');
        } else if (typeId === '2') {
            $('#special .ruleLog').css('display', 'block');
        } else if (typeId === '3') {
            $('#special .apiLog').css('display', 'block');
        } else if (typeId === '4') {
            $('#special .modelOperateLog').css('display', 'block');
        } else if (typeId === '5') {
            $('#special .importAndExportLog').css('display', 'block');
        }
    },
    // 初始化表格 obj:搜索条件
    initTable: function (obj) {
        var titles = logModal.getTitles();
        var url = logModal.getUrl();
        $('#logTable')
            .on('error.dt', function (e, settings, techNote, message) {
                console.log('An error has been reported by DataTables: ', message);
            })
            .width('100%').dataTable({
            "processing": true,
            "searching": false, // 是否开启搜索功能
            "ordering": false,
            "destroy": true,
            "bLengthChange": false,
            "pagingType": "full_numbers",
            "paging": true,
            "info": true,
            "serverSide": true,
            "pageLength": 10,
            "columns": titles, // 不能为空
            ajax: {
                url: webpath + url,
                "type": 'GET',
                "data": function (d) { // 查询参数
                    return $.extend({}, d, obj);
                }
            },
            "fnDrawCallback": function (oSettings, json) {
                // $("tr:even").css("background-color", "#fbfbfd");
                // $("table:eq(0) th").css("background-color", "#f6f7fb");
            }
        });

        var activeLogTypeId = logModal.activeLogTypeId;
        // 日志详情 -> 操作日志 & 接口日志 & 模型操作日志
        if (activeLogTypeId === '1' || activeLogTypeId === '3' || activeLogTypeId === '4') {
            $('#logTable').on('click', '.logDetailBtn', function (e) {
                var dataCol = $(this).attr('detail-col-name');
                var curRow = e.currentTarget.parentNode.parentNode;
                var row = $('#logTable').DataTable().row(curRow).data();
                var data = row[dataCol];
                $('#logModalBodyText').text(data);
                $("#logDetailModal").modal('show', 'center');
            });
        }
    }
};

// 规则执行日志详情
var logRuleDetailModal = {
    show: function (logId) {
        $.ajax({
            url: webpath + "/ruleDetailList",
            data: {"logId": logId},
            dataType: "json",
            success: function (data) {
                logRuleDetailModal.fillModal(data);
                $("#ruleLogDetailModal").modal({});
            }
        })
    },
    fillModal: function (data) {
        var htmlStr = "";
        if (data && data.length > 0) {
            var inTxt = data[0].inputData ? data[0].inputData : '';
            var outTxt = data[0].outputData ? data[0].outputData : '';
            htmlStr += '<div class="logEle"><span class="modalTxt">输入数据：</span><textarea type="text" id="logInputData" value=\'' + inTxt + '\' class="wtd-80" readonly="readonly">' + inTxt + '</textarea></div>';
            htmlStr += '<div class="logEle"><span class="modalTxt">输出数据：</span><textarea type="text" id="logOutputData" value=\'' + outTxt + '\' class="wtd-80" readonly="readonly">' + outTxt + '</textarea></div>';
            htmlStr += '<div class="ruleLogResultContainer">';
            for (var i = 0; i < data.length; i++) {
                if (data[i].nodeType != "task" && data[i].nodeType != "interface")
                    continue;
                try {
                    var ruleNames = "";
                    var result = JSON.parse(data[i].result);
                    if (result.length > 0 && data[i].nodeType == "task") {
                        for (var j = 0; j < result.length; j++) {
                            ruleNames += result[j].actRuleName + "命中,";
                        }
                        ruleNames = ruleNames.substr(0, ruleNames.length - 1);
                    } else {
                        ruleNames = "正常";
                    }
                } catch (e) {
                    ruleNames = "执行异常" + data[i].result;
                }
                htmlStr += '<div class="logEle"><span class="modalTxt">' + data[i].nodeName + '：</span>' + ruleNames + '</div>';
            }
            htmlStr += '</div>';
        } else {
            htmlStr += '无详情';
        }
        $("#ruleLogDetailModalBody").html(htmlStr);
    }
};

// 导入导出日志详情
var importAndExportDetailModal = {
    show: function (operateType, logId) {
        if (!(logId && operateType)) {
            failedMessager.show('详情数据丢失！');
            return;
        }
        var logDetailObj = JSON.parse($("#logDetail_" + logId).text());
        // console.dir(logDetailObj);

        if (operateType === 'import') {
            importReportModel.initImportReport(logDetailObj);
        }
        if (operateType === 'export') {
            reportModel.initExportReportData(logDetailObj, false);
            $('#exportReportAlertModal #closeExportReport').css('display', 'none');
        }
    }
};

$(function () {
    logModal.initLogPage();
});