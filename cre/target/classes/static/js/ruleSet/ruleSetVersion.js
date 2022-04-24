/**
 * 规则集版本
 * data:2019/09/05
 * author:bambi
 */

var ruleSetVersionModal = {
    /**
     * 初始化页面内容/绑定事件
     *      handleType 0新建版本 1修改版本 -1有问题
     *      initFun 初始化函数
     *      callBackFun 保存后回调函数
     *      ruleSetHeaderId 规则集id
     *      ruleSetId 规则集版本id(修改进入有)
     *      ruleSetName 规则集名称
     */
    handleType: -1,
    ruleSetHeaderId: '',
    ruleSetId: '',
    oldRuleContent: [], // 记录上次规则内容
    enable: -1, // 当前版本启用状态
    version: '',
    kpiObjList: [], // 缓存记录引用的指标信息 [{'kpiId': kpiId, 'kpiName': kpiName, 'kpiType': kpiType}, ...]
    ruleSetName: '',
    initPage: function (handleType, initFun, callBackFun, ruleSetHeaderId, ruleSetId, ruleSetName) {
        // 必传参数校验
        if (handleType === -1 || ruleSetHeaderId == null || ruleSetHeaderId == '') {
            return;
        }
        if (handleType === 1 && (ruleSetId == null || ruleSetId == '')) {
            return;
        }

        // 初始化页面内容
        if (initFun) initFun();
        ruleSetVersionModal.handleType = parseInt(handleType);
        ruleSetVersionModal.ruleSetHeaderId = ruleSetHeaderId;
        ruleSetVersionModal.ruleSetId = ruleSetId;
        ruleSetVersionModal.ruleSetName = ruleSetName;
        var addFlag = (handleType === 0) ? true : false;
        var titleStr = addFlag ? '新建' : '修改';
        $('#rulSetVersionAlertModal .modal-title').text(titleStr + '版本'); // 弹框title
        $('#saveTypeDiv').addClass('hide');

        // 数据初始化缓存
        if (addFlag) {
            ruleObj.tableData = [];
            ruleSetVersionModal.oldRuleContent = [];
            ruleSetVersionModal.kpiObjList = [];
            ruleObj.initRulesTable(ruleObj.tableData); // 初始化规则表格
            ruleObj.bindFuns();
        } else {
            // 修改: ajax查询版本数据及回显
            $.ajax({
                url: webpath + '/ruleSet/getOne',
                type: 'POST',
                dataType: "json",
                data: {"ruleSetId": ruleSetId},
                success: function (data) {
                    if (data.status === 0) {
                        ruleSetVersionModal.kpiObjList = data.data.kpiInfo; // 获取 kpiObjList
                        $('#ruleSetVersion').val(data.data.version);
                        $('#ruleSetVersionDes').val(data.data.ruleSetDesc);

                        if (data.data.ruleSetContent != null && data.data.ruleSetContent != '') {
                            ruleObj.tableData = JSON.parse(data.data.ruleSetContent);
                            ruleSetVersionModal.oldRuleContent = JSON.parse(data.data.ruleSetContent);
                        }
                        ruleSetVersionModal.enable = data.data.enable;
                        ruleSetVersionModal.version = data.data.version;
                        ruleObj.initRulesTable(ruleObj.tableData); // 初始化规则表格
                        ruleObj.bindFuns();

                        // 保存方式内容初始化
                        if (!addFlag) { // 修改
                            $('#saveTypeDiv').removeClass('hide');
                            var enable = ruleSetVersionModal.enable;
                            if (enable != 1 && enable != 0) {
                                return;
                            }
                            if (enable == 1) { // 启用状态下仅能创建新版本
                                $("input[name='saveTypeRadio'][value='1']").removeProp('checked').attr('disabled', true);
                                $("input[name='saveTypeRadio'][value='0']").prop('checked', true);
                                $('#ruleSetVersion').removeAttr('disabled', true);
                            } else {
                                $("input[name='saveTypeRadio'][value='1']").prop('checked', true).removeAttr('disabled');
                                $("input[name='saveTypeRadio'][value='0']").removeProp('checked');
                                $('#ruleSetVersion').val(ruleSetVersionModal.version).attr('disabled', true);
                            }
                        }
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        }

        $('#addVersionRule').unbind().on('click', function () { // 新建规则按钮
            $('#rulSetVersionAlertModal').modal('hide');
            ruleObj.showRuleConfig();
        });

        $('#saveRuleSetVersion').unbind().on('click', function () { // 保存版本
            if (handleType == 0) {
                ruleSetVersionModal.saveVersion(handleType, callBackFun);
            } else if (handleType == 1) { // 修改版本选择保存方式
                var updateHandleType = $("input[name='saveTypeRadio']:checked").attr('value');
                ruleSetVersionModal.saveVersion(updateHandleType, callBackFun);
            }
        });

        $('.saveTypeRadio').unbind().on('change', function () { // 更新方式单选框change事件
            var updateHandleType = $("input[name='saveTypeRadio']:checked").attr('value');
            if (updateHandleType == 1) { // 仅更新不能修改版本号
                $('#ruleSetVersion').val(ruleSetVersionModal.version).attr('disabled', true);
            } else {
                $('#ruleSetVersion').removeAttr('disabled', true);
            }
        });

        $('#versionModalCancel').unbind().on('click', function () { // 退出版本编辑
            ruleSetVersionModal.clearAndClose(callBackFun);
        });

        $('#rulSetVersionAlertModal').modal({"show": "center", "backdrop": "static"});
    },
    // 显示弹框
    show: function () {
        $('#rulSetVersionAlertModal').modal({"show": "center", "backdrop": "static"});
    },
    // 隐藏弹框
    hide: function () {
        $('#rulSetVersionAlertModal').modal('hide');
    },
    // 清除数据并退出
    clearAndClose: function (callBackFun) {
        $('#rulSetVersionAlertModal form').validator('cleanUp'); // 清除表单验证消息
        ruleSetVersionModal.handleType = -1;
        ruleSetVersionModal.ruleSetHeaderId = '';
        ruleSetVersionModal.ruleSetId = '';
        ruleSetVersionModal.ruleSetName = '';
        ruleSetVersionModal.oldRuleContent = [];
        ruleSetVersionModal.enable = -1;
        ruleSetVersionModal.kpiObjList = [];
        $('#ruleSetVersion').val('');
        $('#ruleSetVersionDes').val('');
        ruleObj.clearAll();
        $('#rulSetVersionAlertModal').modal('hide');
        if (callBackFun) callBackFun();
    },
    // 保存版本
    saveVersion: function (handleType, callBackFun) {
        if (!$('#rulSetVersionAlertModal form').isValid()) {
            return;
        }
        var urlStr = '';
        if (handleType == 0) { // 0新建版本
            urlStr = '/ruleSet/create';
        } else if (handleType == 1) { // 1更新版本
            urlStr = '/ruleSet/update';
        } else {
            return;
        }
        var obj = ruleSetVersionModal.getVersionData(handleType);
        if (obj && obj != {}) {
            $.ajax({
                url: webpath + urlStr,
                type: 'POST',
                dataType: "json",
                data: obj,
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('保存成功');
                        ruleSetVersionModal.clearAndClose();
                        if (callBackFun) callBackFun();
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        }
    },
    // 获取版本数据
    getVersionData: function (handleType) {
        var obj = {};
        var dataArr = ruleObj.tableData;
        if (handleType == 0) {
            obj['ruleSetHeaderId'] = ruleSetVersionModal.ruleSetHeaderId;
        }
        if (handleType == 1) { // 修改版本
            obj['ruleSetHeaderId'] = ruleSetVersionModal.ruleSetHeaderId;
            obj['ruleSetId'] = ruleSetVersionModal.ruleSetId;
            obj['oldRuleContent'] = JSON.stringify(ruleSetVersionModal.oldRuleContent);
        }
        obj['version'] = $('#ruleSetVersion').val();
        obj['ruleSetDesc'] = $('#ruleSetVersionDes').val();
        obj['ruleSetContent'] = JSON.stringify(dataArr);
        obj['ruleSetName'] = ruleSetVersionModal.ruleSetName;

        // var paramsArr = [];
        // if (dataArr.length > 0) {
        //     for (var i = 0; i < dataArr.length; i++) {
        //         var objParams = {};
        //         var rulObj = dataArr[i][6];
        //         // 条件参数
        //         var LHS_idArr = rulObj.LHS.condition;
        //         var LHS_txtArr = rulObj.LHSTxt;
        //         // 结果参数
        //         var RHS_idArr = rulObj.RHS;
        //         var RHS_txtArr = rulObj.RHSTxt;
        //
        //         var L_obj = ruleSetVersionModal.getParams(LHS_idArr, LHS_txtArr);
        //         var R_obj = ruleSetVersionModal.getParams(RHS_idArr, RHS_txtArr);
        //         objParams['LHS_ids'] = L_obj.ids;
        //         objParams['LHS_txt'] = L_obj.txt;
        //         objParams['RHS_ids'] = R_obj.ids;
        //         objParams['RHS_txt'] = R_obj.txt;
        //         paramsArr.push(objParams);
        //     }
        // }
        // obj['params'] = paramsArr; // 暂时不用这个参数
        return obj;
    },
    // 记录当前参数arr ids与txt
    // getParams: function (idArr, txtArr) {
    //     var obj = {};
    //     if (idArr != '' && idArr.length > 0) { // ids
    //         var paramIdArr = [];
    //         for (var i = 0; i < idArr.length; i++) {
    //             var idx = JSON.parse(idArr[i]).varId.replace('[', '').replace(']', '');
    //             paramIdArr.push(idx);
    //         }
    //         obj['ids'] = paramIdArr;
    //     }
    //     if (txtArr != '' && txtArr.length > 0) { // txt
    //         var paramTxtArr = [];
    //         for (var i = 0; i < txtArr.length; i++) {
    //             var txtSplitArr = txtArr[i].split(' ');
    //             paramTxtArr.push(txtSplitArr[0].trim());
    //         }
    //         obj['txt'] = paramTxtArr;
    //     }
    //     return obj;
    // },
    // 修改回显时更新参数名称
    // echoUpdataParams: function (newParams, rulesArr) {
    //     for (var i = 0; i < newParams.length; i++) {
    //         // 更新参数名称
    //         var LHSTxt_newArr = ruleSetVersionModal.compareAndReplace(newParams[i].LHS_txt, rulesArr[i][6].LHSTxt);
    //         var RHSTxt_newArr = ruleSetVersionModal.compareAndReplace(newParams[i].RHS_txt, rulesArr[i][6].RHSTxt);
    //         if (LHSTxt_newArr.length < 1 || RHSTxt_newArr.length < 1) {
    //             return;
    //         }
    //         rulesArr[i][6].LHSTxt = LHSTxt_newArr;
    //         rulesArr[i][6].RHSTxt = RHSTxt_newArr;
    //     }
    //
    //     return rulesArr;
    // },
}

var myTreeData = inVariableTree;
var outVarsTree = outVariableTree;
var expRuleContent = ""; // 表达式内容
var expCdtObj;
var compareSel = [
    {key: ">", text: "大于"},
    {key: "<", text: "小于"},
    {key: "==", text: "等于"},
    {key: "!=", text: "不等于"},
    {key: ">=", text: "大于等于"},
    {key: "<=", text: "小于等于"},
    {key: " in ", text: "属于"}
];
var compareSelRst = [{key: "=", text: "等于"}];
var valueType = [
    {key: "t_value", text: "值", selected: true},
    {key: "t_variable", text: "变量"},
    {key: "t_expRule", text: "表达式"}
];
var ruleObj = {
    actionTbl: "",// 规则集表格对象
    tableData: [],// 规则集表格数据
    dialog: "",// 确认信息弹窗对象(删除一行时弹出)
    actionName: '规则集',// 规则集名称
    // 规则ruleObj:
    actDtlRuleIdx: 0,// 记录规则条件idx
    actDtlRstIdx: 0,// 记录规则结果idx
    actSelL: [],// 记录规则条件select Obj
    actSelR: [],// 记录规则结果select Objs
    actRuleConnSel: '',// 规则条件设置连接条件是“全部”或“任一”
    // 展示规则配置弹框
    showRuleConfig: function () {
        ruleObj.clearRuleObj();
        $('#ruleConfigModal').modal({"show": "center", "backdrop": "static"});
        $("#ruleConfigModal .actDT").text(ruleObj.actionName + '规则配置'); // 显示规则集名称
    },
    // 隐藏规则配置弹框
    hideRuleConfig: function () {
        $('#ruleConfigModal').modal('hide');
    },
    // 引用指标回调函数
    importKpiCallFun: function () {
        $('#ruleConfigModal').modal({"show": "center", "backdrop": "static"});
    },
    // 初始化条件连接符
    initRuleConnect: function () {
        var selectData1 = [
            {key: 'or', text: "任一", selected: true},
            {key: 'and', text: "全部"}
        ];
        ruleObj.actRuleConnSel = $('#actRuleConnSel').cm_select({field: "actRuleConn", data: selectData1});
    },
    // 初始化规则列表
    initRulesTable: function (rulesArr) {
        ruleObj.actionTbl = $('#actionTbl').css('width', '100%').DataTable({
            "searching": false,
            "destroy": true,
            "rowReorder": {"selector": "td:nth-child(1)"}, // 以第一列进行排序, 可拖动行进行重新排序, selector: 拖动第一列
            "bLengthChange": false,
            "paging": false,
            "data": rulesArr,
            "columns": [
                {"name": "idx"},
                {"name": "ruleName"},
                {"name": "isEndAction", "width": "70px"},
                {"name": "isEndFlow", "width": "60px"},
                {"name": "ruleCodition"},
                {"name": "ruleResult"},
                {"name": "opt"}
            ],
            "columnDefs": [
                {
                    "targets": 0, "visible": false
                },
                {
                    "targets": 2, "render": function (data) {
                        switch ($.trim(data)) {
                            case 'true':
                                return '是';
                            case 'false':
                                return '否';
                            default:
                                return '--';
                        }
                    }
                },
                {
                    "targets": 3, "render": function (data) {
                        switch ($.trim(data)) {
                            case 'true':
                                return '是';
                            case 'false':
                                return '否';
                            default:
                                return '--';
                        }
                    }
                },
                {
                    "targets": 6, "render": function (data, type, row, meta) {
                        return '<span class="iconEdit optIcon"></span><span class="iconDel optIcon"></span>';
                    }
                }
            ],
            "drawCallback": function (settings) {
                $('#actionTbl th').unbind("click").removeClass('sorting'); // 去除表头排序样式
                $("table:eq(0) th").css("background-color", "#f6f7fb");
            }
        });
    },
    // 清除所有数据并退出
    clearAll: function () {
        ruleObj.actionTbl = '';
        ruleObj.tableData = [];
        ruleObj.dialog = '';
        ruleObj.actionName = '规则集';
        ruleObj.clearRuleObj();
    },
    // 清除 规则cache数据;规则配置页面内容
    clearRuleObj: function () {
        ruleObj.actSelL = [];
        ruleObj.actSelR = [];
        ruleObj.actDtlRuleIdx = 0;
        ruleObj.actDtlRstIdx = 0;
        ruleObj.actRuleConnSel.setValue('or');
        $('#editRuleIdx').val(''); // 清空规则回显id
        $('#actDetailName').val(''); // 规则名称
        $("#ruleConfigModal .actDT").text('规则配置'); // 还原规则集名称弹框title
        $('.actLeftRuleCdt, .actRightRuleCdt, .cdtRstWrap').html(''); // 清空配置内容及预览
        $("#ruleConfigModal input[name='isEndAction']").prop('checked', false);
        $("#ruleConfigModal input[name='isEndFlow']").prop('checked', false);
    },
    // 生成全局唯一ruleID
    getUId: function () {
        var timestamp = (new Date()).valueOf();
        var random = parseInt(Math.random() * 1000);
        return timestamp + random;
    },
    // 根据变量id获取变量类型
    searchVariableType: function (variableId, obj) {
        var value = '';
        for (var i = 0; i < obj.length; i++) {
            if (value != '') {
                return value;
            }
            if (obj[i].id == variableId) {
                value = obj[i].typeId;
                return value;
            } else if (obj[i].children && obj[i].children.length > 0) {
                value = ruleObj.searchVariableType(variableId, obj[i].children);
            }
        }
        return value;
    },
    // 更新kpiObj缓存
    updateKpiObjList: function (kpiId, kpiName, kpiType) {
        var hasFlag = false;
        for (var i = 0; i < ruleSetVersionModal.kpiObjList.length; i++) {
            if (kpiId == ruleSetVersionModal.kpiObjList[i].kpiId) { // 有缓存不更新
                hasFlag = true;
                return;
            }
        }
        if (!hasFlag) {
            ruleSetVersionModal.kpiObjList.push({"kpiId": kpiId, "kpiName": kpiName, "kpiType": kpiType});
        }
    },
    // 通过kpiId找kpiName
    searchKpiName: function (kpiId) {
        var kpiName = '';
        for (var i = 0; i < ruleSetVersionModal.kpiObjList.length; i++) {
            if (kpiId == ruleSetVersionModal.kpiObjList[i].kpiId) { // 有缓存不更新
                kpiName = ruleSetVersionModal.kpiObjList[i].kpiName;
            }
        }
        return kpiName;
    },
    // 通过kpiId找kpiType
    searchKpiType: function (kpiId) {
        var kpiType = '';
        for (var i = 0; i < ruleSetVersionModal.kpiObjList.length; i++) {
            if (kpiId == ruleSetVersionModal.kpiObjList[i].kpiId) { // 有缓存不更新
                kpiType = ruleSetVersionModal.kpiObjList[i].kpiType;
            }
        }
        return kpiType;
    },
    // 保存一条规则配置的数据
    generateActObj: function () {
        var errorTxt = '';
        var actRuleName = $('#actDetailName').val(); // 规则名称
        var resultObj = {
            "actRuleName": actRuleName,
            "LHS": {"union": "", "condition": []},
            "LHSTxt": [],
            "RHS": [],
            "RHSTxt": [],
        };
        resultObj.LHS.union = $('#actRuleConn').val(); // 规则条件连接操作符
        resultObj.uid = $("#ruleUid").val(); // 规则uid
        var txtStr = '';

        if (actRuleName == "") {
            errorTxt = "规则名称不能为空！";
        }
        // 条件配置
        for (var i = 0; i < $('.actLeftRuleCdt>div').length; i++) {
            txtStr = '';
            var $tag = $('.actLeftRuleCdt>div:eq(' + i + ')');
            var $selDd = $tag.find('.cm-listSelect');
            var idx = $selDd.attr('id').replace(/[^0-9]/ig, "");
            if ($('#actLeftIpt' + idx + 'Text').val() == '') {
                errorTxt = '规则条件配置的左侧不能为空！';
            } else {
                txtStr += $('#actLeftIpt' + idx + 'Text').val() + ' ';
            }
            if ($('#actLeftopt' + idx + 'Text').val() == '') {
                errorTxt = '规则条件配置的操作符不能为空！';
            } else {
                txtStr += $('#actLeftopt' + idx + 'Text').val() + ' ';
            }
            if ($tag.find('.cdtValue').val() == '' && $('#lcdtResuleIpt' + idx).val() == '') {
                errorTxt = '规则条件配置的右侧不能为空！';
            } else {
                if ($('#actLeftValType' + idx).val() == "t_variable") {
                    txtStr += $('#lcdtResuleIpt' + idx + 'Text').val();
                } else {
                    txtStr += $.trim($tag.find('.cdtValue').val());
                }
            }

            if ($('#actLeftValType' + idx).val() == "t_variable") { // 验证左右两侧变量类型是否一致
                var typeId1 = 'typeId1';
                var typeId4 = 'typeId4';
                if ($('#actLeftIpt' + idx).val() === 'KPI') {
                    typeId1 = $('#actLeftIpt' + idx).attr('kpiType');
                } else {
                    typeId1 = ruleObj.searchVariableType($('#actLeftIpt' + idx).val(), myTreeData);
                }
                if ($('#lcdtResuleIpt' + idx).val() === 'KPI') {
                    typeId4 = $('#lcdtResuleIpt' + idx).attr('kpiType');
                } else {
                    typeId4 = ruleObj.searchVariableType($('#lcdtResuleIpt' + idx).val(), myTreeData);
                }
                if (typeId1 != typeId4) {
                    errorTxt = '变量"' + $('#actLeftIpt' + idx + 'Text').val() + '"与变量"' + $('#lcdtResuleIpt' + idx + 'Text').val() + '"类型不符';
                }
            }

            if ($('#actLeftValType' + idx).val() == "t_value") { // 校验值类型的输入
                var leftType;
                if ($('#actLeftIpt' + idx).val() === 'KPI') {
                    leftType = $('#actLeftIpt' + idx).attr('kpiType');
                } else {
                    leftType = ruleObj.searchVariableType($('#actLeftIpt' + idx).val(), myTreeData);
                }
                if (!ruleObj.checkValueType(leftType, $.trim($tag.find('.cdtValue').val()))) {
                    errorTxt = '变量"' + $('#actLeftIpt' + idx + 'Text').val() + '"输入格式错误，请检查！';
                }
            }

            resultObj.LHSTxt.push(txtStr);

            // 保存规则条件详情
            var valObj = new Object();
            if ($('#actLeftIpt' + idx).val() === 'KPI') { // 判断左侧变量是否为指标
                var kpiId = $('#actLeftIpt' + idx).attr('kpiId');
                var kpiType = $('#actLeftIpt' + idx).attr('kpiType');
                var kpiName = $('#actLeftIpt' + idx + 'Text').val();
                valObj.kpiId = '[' + kpiId + ']';
                ruleObj.updateKpiObjList(kpiId, kpiName, kpiType);
            } else {
                valObj.varId = '[' + $('#actLeftIpt' + idx).val() + ']';
            }
            valObj.opt = $('#actLeftopt' + idx).val();
            valObj.valueType = $('#actLeftValType' + idx).val();
            if (valObj.valueType == "t_variable") {
                if ($('#lcdtResuleIpt' + idx).val() === 'KPI') { // 判断右侧变量是否为指标
                    var kpiId = $('#lcdtResuleIpt' + idx).attr('kpiId');
                    var kpiType = $('#lcdtResuleIpt' + idx).attr('kpiType');
                    var kpiName = $('#lcdtResuleIpt' + idx + 'Text').val();
                    valObj.isKpi = true;
                    valObj.value = '[' + kpiId + ']';
                    ruleObj.updateKpiObjList(kpiId, kpiName, kpiType);
                } else {
                    valObj.value = '[' + $('#lcdtResuleIpt' + idx).val() + ']';
                }
            } else if (valObj.valueType == "t_expRule") {
                valObj.value = $.trim($tag.find('.cdtValue').val());
                valObj.references = JSON.parse($tag.find('.cdtValue').attr('references'));
            } else {
                valObj.value = $.trim($tag.find('.cdtValue').val());
            }

            resultObj.LHS.condition.push(JSON.stringify(valObj));
        }
        // 结果配置
        for (var i = 0; i < $('.actRightRuleCdt>div').length; i++) {
            txtStr = '';
            var $tag = $('.actRightRuleCdt>div:eq(' + i + ')');
            var $selDd = $tag.find('.cm-listSelect');
            var idx = $selDd.attr('id').replace(/[^0-9]/ig, "");
            if ($('#actRightIpt' + idx + 'Text').val() == '') {
                errorTxt = '规则结果配置的左侧不能为空！';
            } else {
                txtStr += $('#actRightIpt' + idx + 'Text').val() + ' ';
            }
            if ($('#actRightopt' + idx + 'Text').val() == '') {
                errorTxt = '规则结果配置的操作符不能为空！';
            } else {
                txtStr += $('#actRightopt' + idx + 'Text').val() + ' ';
            }

            if ($tag.find('.cdtValue').val() == '' && $('#rcdtResuleIpt' + idx).val() == '') {
                errorTxt = '规则条件配置的右侧不能为空！';
            } else {
                if ($('#actRightValType' + idx).val() == "t_variable") {
                    txtStr += $('#rcdtResuleIpt' + idx + 'Text').val();
                } else {
                    txtStr += $.trim($tag.find('.cdtValue').val());
                }
            }

            if ($('#actRightValType' + idx).val() == "t_variable") { // 验证左右两侧变量类型是否一致
                var typeId1 = ruleObj.searchVariableType($('#actRightIpt' + idx).val(), myTreeData);
                var typeId4 = 'typeId4';
                if ($('#rcdtResuleIpt' + idx).val() === 'KPI') {
                    typeId4 = $('#rcdtResuleIpt' + idx).attr('kpiType');
                } else {
                    typeId4 = ruleObj.searchVariableType($('#rcdtResuleIpt' + idx).val(), myTreeData);
                }
                if (typeId1 != typeId4) {
                    errorTxt = '变量"' + $('#actRightIpt' + idx + 'Text').val() + '"与变量"' + $('#rcdtResuleIpt' + idx + 'Text').val() + '"类型不符';
                }
            }

            if ($('#actRightValType' + idx).val() == "t_value") { // 校验值类型的输入
                var leftType = ruleObj.searchVariableType($('#actRightIpt' + idx).val(), myTreeData);
                if (!ruleObj.checkValueType(leftType, $.trim($tag.find('.cdtValue').val()))) {
                    errorTxt = '变量"' + $('#actRightIpt' + idx + 'Text').val() + '"输入格式错误，请检查！';
                }
            }

            resultObj.RHSTxt.push(txtStr);

            //保存规则结果详情
            var resultValObj = new Object();
            resultValObj.varId = '[' + $('#actRightIpt' + idx).val() + ']';
            resultValObj.opt = $('#actRightopt' + idx).val();
            resultValObj.valueType = $('#actRightValType' + idx).val();
            if (resultValObj.valueType == "t_variable") {
                if ($('#rcdtResuleIpt' + idx).val() === 'KPI') { // 右侧变量为指标
                    var kpiId = $('#rcdtResuleIpt' + idx).attr('kpiId');
                    var kpiType = $('#rcdtResuleIpt' + idx).attr('kpiType');
                    var kpiName = $('#rcdtResuleIpt' + idx + 'Text').val();
                    resultValObj.isKpi = true;
                    resultValObj.value = '[' + kpiId + ']';
                    ruleObj.updateKpiObjList(kpiId, kpiName, kpiType);
                } else {
                    resultValObj.value = '[' + $('#rcdtResuleIpt' + idx).val() + ']';
                }
            } else if (resultValObj.valueType == "t_expRule") {
                resultValObj.value = $.trim($tag.find('.cdtValue').val());
                resultValObj.references = JSON.parse($tag.find('.cdtValue').attr('references'));
            } else {
                resultValObj.value = $.trim($tag.find('.cdtValue').val());
            }
            resultObj.RHS.push(JSON.stringify(resultValObj));
        }
        if (resultObj.RHS.length == 0) {
            errorTxt = "规则一定要有一条结果配置！";
        }
        if (resultObj.LHS.length == 0) {
            errorTxt = "规则一定要有一条条件配置！";
        }
        if (errorTxt != "") {
            return errorTxt;
        }
        return resultObj;
    },
    // 验证规则值类型的输入格式
    checkValueType: function (type, data) {
        var result = true;
        if (type === '2') {
            data = parseFloat(data);
            if (Math.floor(data) !== data) {
                result = false;
            }
        } else if (type === '4') {
            if (isNaN(parseFloat(data))) {
                result = false;
            }
        } else if (type === '5') {
            if (!Array.isArray(JSON.parse(data))) {
                result = false;
            }
        }
        return result;
    },
    // 生成规则预览文字
    generateActTxt: function () {
        var resultObj = {"LHSTxt": [], "union": "", "RHSTxt": []};
        resultObj.union = $('#actRuleConn').val();
        var txtStr = '';

        for (var i = 0; i < $('.actLeftRuleCdt>div').length; i++) {
            var $tag = $('.actLeftRuleCdt>div:eq(' + i + ')');
            var $selDd = $tag.find('.cm-listSelect');
            var idx = $selDd.attr('id').replace(/[^0-9]/ig, "");
            var valStr = '';
            if ($('#actLeftValType' + idx).val() == "t_variable") {
                valStr = $('#lcdtResuleIpt' + idx + 'Text').val();
            } else {
                valStr = $.trim($tag.find('.cdtValue').val());
            }
            txtStr = $('#actLeftIpt' + idx + 'Text').val() + ' ' + $('#actLeftopt' + idx + 'Text').val() + ' ' + valStr;
            resultObj.LHSTxt.push(txtStr);
        }

        txtStr = '';
        for (var i = 0; i < $('.actRightRuleCdt>div').length; i++) {
            var $tag = $('.actRightRuleCdt>div:eq(' + i + ')');
            var $selDd = $tag.find('.cm-listSelect');
            var idx = $selDd.attr('id').replace(/[^0-9]/ig, "");
            var valStr = '';
            if ($('#actRightValType' + idx).val() == "t_variable") {
                valStr = $('#rcdtResuleIpt' + idx + 'Text').val();
            } else {
                valStr = $.trim($tag.find('.cdtValue').val());
            }
            txtStr = $('#actRightIpt' + idx + 'Text').val() + ' ' + $('#actRightopt' + idx + 'Text').val() + ' ' + valStr;
            resultObj.RHSTxt.push(txtStr);
        }
        return resultObj;
    },
    // 回显规则数据
    echoRuleData: function (data) {
        $('#editRuleIdx').val(data[0]); // ruleId
        $('#actDetailName').val(data[1]); // ruleName
        $("#ruleConfigModal .actDT").text(ruleObj.actionName + '规则配置'); // ruleSetName
        var tempData = data[6];
        var LHS = tempData.LHS;
        var RHS = tempData.RHS;
        var uid = tempData.uid;
        $("#ruleUid").val(uid); // ruleUid
        if (tempData.isEndAction) {
            $("#ruleConfigModal input[name='isEndAction']").prop('checked', tempData.isEndAction);
        }
        if (tempData.isEndFlow) {
            $("#ruleConfigModal input[name='isEndFlow']").prop('checked', tempData.isEndFlow);
        }
        if (LHS.union) {
            ruleObj.actRuleConnSel.setValue(LHS.union);
        }
        // 回显条件配置内容
        for (var i = 0; i < LHS.condition.length; i++) {
            $('button.cdtRuleAddBtn').trigger('click'); // 新建一个规则条
            var cdtL = JSON.parse(LHS.condition[i]);
            var idx = ruleObj.actDtlRuleIdx - 1;
            for (var j = 0; j < ruleObj.actSelL.length; j++) {
                if (idx == ruleObj.actSelL[j].id) {
                    if (cdtL.varId) { // 普通参数回显
                        ruleObj.actSelL[j].sel1.setValue(cdtL.varId.substring(1, cdtL.varId.length - 1));
                    } else if (cdtL.kpiId) { // 回显时条件左侧为指标变量
                        $("#actLeftIpt" + idx).attr('openModal', '0'); // 标识不需要打开model
                        var kpiId = cdtL.kpiId.substring(1, cdtL.kpiId.length - 1);
                        ruleObj.actSelL[j].sel1.setValue("KPI");
                        $("#actLeftIpt" + idx).attr('kpiId', kpiId);
                        $("#actLeftIpt" + idx).attr('kpiType', ruleObj.searchKpiType(kpiId));
                        $("#actLeftIpt" + idx + "Text").val(ruleObj.searchKpiName(kpiId));
                    }
                    ruleObj.actSelL[j].sel2.setValue(cdtL.opt);
                    ruleObj.actSelL[j].sel3.setValue(cdtL.valueType);
                    if (cdtL.valueType == "t_variable") {
                        if (cdtL.isKpi) { // 回显时条件右侧为指标变量
                            $("#lcdtResuleIpt" + idx).attr('openModal', '0'); // 标识不需要打开model
                            var kpiId = cdtL.value.substring(1, cdtL.value.length - 1);
                            ruleObj.actSelL[j].sel4.setValue("KPI");
                            $("#lcdtResuleIpt" + idx).attr('kpiId', kpiId);
                            $("#lcdtResuleIpt" + idx).attr('kpiType', ruleObj.searchKpiType(kpiId));
                            $("#lcdtResuleIpt" + idx + "Text").val(ruleObj.searchKpiName(kpiId));
                        } else {
                            ruleObj.actSelL[j].sel4.setValue(cdtL.value.substring(1, cdtL.value.length - 1));
                        }
                    } else if (cdtL.valueType == "t_expRule") {
                        $('.L' + idx + ' .cdtValue').val(cdtL.value);
                        $('.L' + idx + ' .cdtValue').attr('references', JSON.stringify(cdtL.references));
                    } else {
                        $('.L' + idx + ' .cdtValue').val(cdtL.value);
                    }
                }
            }
        }
        // 回显结果配置内容
        for (var i = 0; i < RHS.length; i++) {
            $('button.cdtResuleAddBtn').trigger('click');
            var cdtR = JSON.parse(RHS[i]);
            var idx = ruleObj.actDtlRstIdx - 1;
            for (var j = 0; j < ruleObj.actSelR.length; j++) {
                if (idx == ruleObj.actSelR[j].id) {
                    ruleObj.actSelR[j].sel1.setValue(cdtR.varId.substring(1, cdtR.varId.length - 1));
                    ruleObj.actSelR[j].sel2.setValue(cdtR.opt);
                    ruleObj.actSelR[j].sel3.setValue(cdtR.valueType);
                    if (cdtR.valueType == "t_variable") {
                        if (cdtR.isKpi) { // 回显时结果右侧为指标变量
                            $("#rcdtResuleIpt" + idx).attr('openModal', '0'); // 标识不需要打开model
                            var kpiId = cdtR.value.substring(1, cdtR.value.length - 1);
                            ruleObj.actSelR[j].sel4.setValue("KPI");
                            $("#rcdtResuleIpt" + idx).attr('kpiId', kpiId);
                            $("#rcdtResuleIpt" + idx).attr('kpiType', ruleObj.searchKpiType(kpiId));
                            $("#rcdtResuleIpt" + idx + "Text").val(ruleObj.searchKpiName(kpiId));
                        } else {
                            ruleObj.actSelR[j].sel4.setValue(cdtR.value.substring(1, cdtR.value.length - 1));
                        }
                    } else if (cdtR.valueType == "t_expRule") {
                        $('.R' + idx + ' .cdtValue').val(cdtR.value);
                        $('.R' + idx + ' .cdtValue').attr("references", JSON.stringify(cdtR.references));
                    } else {
                        $('.R' + idx + ' .cdtValue').val(cdtR.value);
                    }
                }
            }
        }
    },
    // 删除规则集表格的一条数据
    delOneActionRule: function () {
        var trDom = $('.saveAskTxt').data("trdom");
        var tagName = ruleObj.actionTbl.row(trDom).data()[1]; // 要删除的行的ruleName
        var tableDataArr = ruleObj.tableData;
        // 先删除数组中的原有数据
        for (var i = 0; i < tableDataArr.length; i++) {
            if (tableDataArr[i][1] == tagName) {
                tableDataArr.splice(i, 1);
                break;
            }
        }
        // 剩余数据idx重新赋值
        for (var i = 0; i < tableDataArr.length; i++) {
            tableDataArr[i][0] = '' + (i + 1) + '';
        }
        ruleObj.initRulesTable(ruleObj.tableData);
        ruleObj.dialog.close();
        ruleSetVersionModal.show();
    },
    // 取消删除规则集表格内某一条规则
    cancelDelRule: function () {
        ruleSetVersionModal.show();
    },
    // 事件绑定
    bindFuns: function () {
        // 初始化连接条件
        ruleObj.initRuleConnect();

        // 新增条件按钮
        $('.cdtRuleAddBtn').unbind().on('click', function () {
            var idx = ruleObj.actDtlRuleIdx++;
            var newHtmlStr = '<div class="L' + idx + '">';
            newHtmlStr += '	<div class="leftCdtRuleCdt cm-sel2" id="actLeft' + idx + '"></div>'; // 参数
            newHtmlStr += '	<div id="actLeftCn' + idx + '" class="cdtRuleCdtOpt cm-sel2"></div>'; // 运算符
            newHtmlStr += '	<div class="cdtResultIptWrap">';
            newHtmlStr += '		<div class="valType cm-sel2" id="actLeftValTypeSel' + idx + '"></div>'; // 值/变量/表达式
            newHtmlStr += '		<div class="inputVal"><input type="text" class="textL cdtValue" /></div>'; // 值/表达式
            newHtmlStr += '		<div class="inputSel cm-sel2" id="lcdtResuleSel' + idx + '"></div>'; // 变量
            newHtmlStr += '	</div>';
            newHtmlStr += '	<span class="delActRow"></span>'; // 删除行
            newHtmlStr += '</div>';

            $('.actLeftRuleCdt').append(newHtmlStr);
            var sel1 = $('#actLeft' + idx).cm_treeSelect({
                field: "actLeftIpt" + idx, data: myTreeData, onselect: function (val) {
                    if (val === 'KPI') { // 条件左侧变量引入指标
                        if ($("#actLeftIpt" + idx).attr('openModal') === '0') { // 0通过回显触发事件
                            $("#actLeftIpt" + idx).attr('openModal', '1'); // 修改标识, 不弹出弹框
                            return;
                        }
                        var echoKpiId = '';
                        if ($("#actLeftIpt" + idx).attr('value') === 'KPI') { // 如果之前选择了指标则保持状态
                            echoKpiId = $("#actLeftIpt" + idx).attr('kpiId');
                        }
                        $("#actLeftIpt" + idx + "Text").val('');
                        $('#ruleConfigModal').modal('hide');
                        importKpiModal.initPage(0, echoKpiId, $("#actLeftIpt" + idx), $("#actLeftIpt" + idx + "Text"), ruleObj.importKpiCallFun);
                    } else {
                        $("#actLeftIpt" + idx).removeAttr('kpiId');
                    }
                }
            }); // 参数树左
            var sel2 = $('#actLeftCn' + idx).cm_select({field: "actLeftopt" + idx, data: compareSel}); // 运算符
            var sel3 = $('#actLeftValTypeSel' + idx).cm_select({ // 值/变量/表达式 & 事件绑定切换展示内容
                field: "actLeftValType" + idx, data: valueType, onselect: function (val) {
                    $(this).find('.cdtValue').val("");
                    if (val == 't_variable') { // 变量
                        $(this).siblings('.inputVal').hide();
                        $(this).siblings('.inputSel').show();
                    } else if (val == 't_expRule') { // 表达式
                        $(this).siblings('.inputSel').hide();
                        $(this).siblings('.inputVal').show();
                        $(this).siblings('.inputVal').bind("click", function () {
                            expRuleContent = $(this).find('.cdtValue').val();
                            expCdtObj = $(this).find('.cdtValue');
                            ruleObj.hideRuleConfig();
                            $("#expRuleModal").modal({'show': 'center', "backdrop": "static"});
                        })
                    } else { // 值
                        $(this).siblings('.inputSel').hide();
                        $(this).siblings('.inputVal').show();
                        $(this).siblings('.inputVal').unbind("click");
                    }
                }
            });
            // 默认显示 '值' 对应内容
            $('#actLeftValTypeSel' + idx).siblings('.inputSel').hide();
            $('#actLeftValTypeSel' + idx).siblings('.inputVal').show();
            $('#actLeftValTypeSel' + idx).siblings('.inputVal').unbind("click");

            var sel4 = $('#lcdtResuleSel' + idx).cm_treeSelect({
                field: "lcdtResuleIpt" + idx, data: myTreeData, onselect: function (val) {
                    if (val === 'KPI') { // 条件右侧变量引入指标
                        if ($("#lcdtResuleIpt" + idx).attr('openModal') === '0') { // 0通过回显触发事件
                            $("#lcdtResuleIpt" + idx).attr('openModal', '1'); // 修改标示, 不弹出弹框
                            return;
                        }
                        var echoKpiId = '';
                        if ($("#lcdtResuleIpt" + idx).attr('value') === 'KPI') { // 如果之前选择了指标则保持状态
                            echoKpiId = $("#lcdtResuleIpt" + idx).attr('kpiId');
                        }
                        $("#lcdtResuleIpt" + idx + "Text").val('');
                        $('#ruleConfigModal').modal('hide');
                        importKpiModal.initPage(0, echoKpiId, $("#lcdtResuleIpt" + idx), $("#lcdtResuleIpt" + idx + "Text"), ruleObj.importKpiCallFun);
                    } else {
                        $("#lcdtResuleIpt" + idx).removeAttr('kpiId');
                    }
                }
            }); // 参数树右
            ruleObj.actSelL.push({"id": idx, "sel1": sel1, "sel2": sel2, "sel3": sel3, "sel4": sel4});
        });

        // 新增结果按钮
        $('.cdtResuleAddBtn').unbind().on('click', function () {
            var idx = ruleObj.actDtlRstIdx++;
            var newHtmlStr = '<div class="R' + idx + '">';
            newHtmlStr += '	<div class="rightCdtRuleCdt  cm-sel2" id="actRight' + idx + '"></div>';
            newHtmlStr += '	<div id="actRightCn' + idx + '" class="cdtRuleCdtOpt  cm-sel2"></div>';
            newHtmlStr += '	<div class="cdtResultIptWrap">';
            newHtmlStr += '		<div class="valType cm-sel2" id="actRightValTypeSel' + idx + '"></div>';
            newHtmlStr += '		<div class="inputVal"><input type="text" class="textL cdtValue" /></div>';
            newHtmlStr += '		<div class="inputSel cm-sel2" id="rcdtResuleSel' + idx + '"></div>';
            newHtmlStr += '	</div>';
            newHtmlStr += '	<span class="delActRow"></span>';
            newHtmlStr += '</div>';

            $('.actRightRuleCdt').append(newHtmlStr);
            var sel1 = $('#actRight' + idx).cm_treeSelect({field: "actRightIpt" + idx, data: outVarsTree});
            var sel2 = $('#actRightCn' + idx).cm_select({field: "actRightopt" + idx, data: compareSelRst});
            var sel3 = $('#actRightValTypeSel' + idx).cm_select({
                field: "actRightValType" + idx, data: valueType, onselect: function (val) {
                    $(this).find('.cdtValue').val("")
                    if (val == 't_variable') {
                        $(this).siblings('.inputVal').hide();
                        $(this).siblings('.inputSel').show();
                    } else if (val == 't_expRule') {
                        $(this).siblings('.inputSel').hide();
                        $(this).siblings('.inputVal').show();
                        $(this).siblings('.inputVal').bind("click", function () {
                            expRuleContent = $(this).find('.cdtValue').val();
                            expCdtObj = $(this).find('.cdtValue');
                            ruleObj.hideRuleConfig();
                            $("#expRuleModal").modal({'show': 'center', "backdrop": "static"});
                        })
                    } else {
                        $(this).siblings('.inputSel').hide();
                        $(this).siblings('.inputVal').show();
                        $(this).siblings('.inputVal').unbind("click");
                    }
                }
            });
            // 默认显示 '值' 对应内容
            $('#actRightValTypeSel' + idx).siblings('.inputSel').hide();
            $('#actRightValTypeSel' + idx).siblings('.inputVal').show();
            $('#actRightValTypeSel' + idx).siblings('.inputVal').unbind("click");

            var sel4 = $('#rcdtResuleSel' + idx).cm_treeSelect({
                field: "rcdtResuleIpt" + idx, data: myTreeData, onselect: function (val) {
                    if (val === 'KPI') { // 结果右侧变量引入指标
                        if ($("#rcdtResuleIpt" + idx).attr('openModal') === '0') { // 0通过回显触发事件
                            $("#rcdtResuleIpt" + idx).attr('openModal', '1'); // 修改标示, 不弹出弹框
                            return;
                        }
                        var echoKpiId = '';
                        if ($("#rcdtResuleIpt" + idx).attr('value') === 'KPI') { // 如果之前选择了指标则保持状态
                            echoKpiId = $("#rcdtResuleIpt" + idx).attr('kpiId');
                        }
                        $("#rcdtResuleIpt" + idx + "Text").val('');
                        $('#ruleConfigModal').modal('hide');
                        importKpiModal.initPage(0, echoKpiId, $("#rcdtResuleIpt" + idx), $("#rcdtResuleIpt" + idx + "Text"), ruleObj.importKpiCallFun);
                    } else {
                        $("#rcdtResuleIpt" + idx).removeAttr('kpiId');
                    }
                }
            });

            ruleObj.actSelR.push({"id": idx, "sel1": sel1, "sel2": sel2, "sel3": sel3, "sel4": sel4});
        });

        // 删除一条条件/结果配置数据
        $('.rglWrap').unbind().on("click", ".delActRow", function () {
            $(this).parent().remove();
        });

        // 规则配置取消按钮
        $('#cslActCfg').click(function () {
            ruleObj.hideRuleConfig();
            ruleSetVersionModal.show();
        });

        // 保存规则配置按钮
        $('#saveActRuleTotbl').unbind().on('click', function () {
            var legal = true;
            var actObj = ruleObj.generateActObj(); // 获取配置页面上的规则数据obj
            if (typeof (actObj) == "object") { // 成功获取页面数据并返回对象
                var conditionStr = '', idx = '';
                if (actObj.LHS.union == 'or') {
                    conditionStr = actObj.LHSTxt.join(' 或 ');
                } else if (actObj.LHS.union == 'and') {
                    conditionStr = actObj.LHSTxt.join(' 且 ');
                }
                if ($('#editRuleIdx').val() != '') { // 编辑规则点进来的, ruleId
                    idx = parseInt($('#editRuleIdx').val()) - 1;
                } else { // 新增规则: 先判断名字不重复然后添加
                    var newName = actObj.actRuleName;
                    for (var i = 0; i < ruleObj.tableData.length; i++) {
                        if (ruleObj.tableData[i] && ruleObj.tableData[i].length > 1 && ruleObj.tableData[i][1] == newName) {
                            legal = false;
                            failedMessager.show('规则名称不能重复！');
                        }
                    }
                    if (legal) {
                        actObj.uid = ruleObj.getUId();
                        idx = parseInt(ruleObj.tableData.length);
                    }
                }
                actObj.isEndAction = $("#ruleConfigModal input[name='isEndAction']").is(':checked');
                actObj.isEndFlow = $("#ruleConfigModal input[name='isEndFlow']").is(':checked');
                if (legal) {
                    // 更新规则集表格数据并刷新表格
                    ruleObj.tableData[idx] = [idx + 1, actObj.actRuleName, actObj.isEndAction, actObj.isEndFlow, conditionStr, actObj.RHSTxt.join('，'), actObj];
                    ruleObj.initRulesTable(ruleObj.tableData);
                    // 清空规则cache 关闭添加规则弹框 展示规则集表格弹框
                    ruleObj.clearRuleObj();
                    ruleObj.hideRuleConfig();
                    ruleSetVersionModal.show();
                }
            } else if (typeof (actObj) == "string") { // 返回了错误信息
                failedMessager.show(actObj);
            }
        });

        // 查看配置预览按钮
        $('#seePreviewBtn').unbind().on('click', function () {
            var tempObj = ruleObj.generateActTxt();
            var union = '';
            if (tempObj.union == 'or') {
                union = ' 或 ';
            } else if (tempObj.union == 'and') {
                union = ' 且 ';
            }
            var tempStr = '<p><span class="describeTxt">如果</span>' + tempObj.LHSTxt.join(union) + '</p>';
            tempStr = tempStr + '<p><span class="describeTxt">则执行</span>' + tempObj.RHSTxt.join('，') + '</p>';
            $('.cdtRstWrap').html(tempStr);
        });

        // 规则集表格内删除按钮
        $('#actionTbl').on('click', '.iconDel', function () {
            ruleSetVersionModal.hide();
            // 确认弹窗
            var htmlStr = '<p class="saveAskTxt">确定删除此条数据？（删除不可恢复）</p>';
            htmlStr += '<div class="modal-footer">';
            htmlStr += '<button type="button" class="btn btn-default cm-btnSec" data-dismiss="modal" onclick="ruleObj.cancelDelRule()">取消</button>';
            htmlStr += '<button type="button" class="btn btn-primary" onclick="ruleObj.delOneActionRule(this)">删除</button>';
            htmlStr += '</div>';
            ruleObj.dialog = new $.zui.ModalTrigger({title: "提示", custom: htmlStr});
            ruleObj.dialog.show();
            // trDom元素挂到span上方便删除时候读取
            $('.saveAskTxt').data("trdom", $(this).parents('tr'));
        });

        // 规则集表格内编辑按钮
        $('#actionTbl').on('click', '.iconEdit', function () {
            // 打开规则配置弹框
            ruleSetVersionModal.hide();
            ruleObj.showRuleConfig();
            // 回显规则数据
            var data = ruleObj.actionTbl.row($(this).parents('tr')).data();
            ruleObj.echoRuleData(data);
        });

        // 表格行拖动后更新缓存并刷新表格
        ruleObj.actionTbl.on('row-reordered', function (e, diff, edit) {
            ruleObj.tableData = ruleObj.tableData.sort(); // 根据配置(第一列 ruleIdx)进行数据重排序, 使数据与展示的顺序保持一致
            ruleObj.initRulesTable(ruleObj.tableData);
        });
    }
}
