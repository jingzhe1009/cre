/**
 * 规则配置页面
 * data:2018/3/20
 * author:jsj
 */

var myTreeData = inVariableTree;
// 表达式规则弹框内容
var expRuleContent = "";
var expCdtObj;
var outVarsTree = outVariableTree;
var funcMethTree = funcMethodTree;
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
    {key: "t_value", text: "值", selected: "true"},
    {key: "t_variable", text: "变量"},
    {key: "t_expRule", text: "表达式"}
];

// 关于规则集自定义的所有js
var actionObj = {
    actionTbl: "",// 规则集表格对象
    tableData: [],// 规则集表格数据
    actDtlRuleIdx: 0,// 记录规则条件idx
    actDtlRstIdx: 0,// 记录规则结果idx
    actSelL: [],// 记录规则条件select Obj
    actSelR: [],// 记录规则结果select Objs
    actRuleConnSel: '',// 规则条件设置连接条件是“全部”或“任一”
    dialog: "",// 弹窗对象
    actionName: '规则集',// 规则集名称
    importType: -1, // 导入方式 0存为本地 1引用 -1初始化状态
    ruleSetHeaderId: '', // 导入的规则集headerId
    ruleSetId: '', // 导入的规则集id
    kpiObjList: [], // 记录指标名称、类型(基于整个模型, 所有规则集) [{'kpiId': kpiId, 'kpiName': kpiName, 'kpiType': kpiType}, ...]
    //关于规则集自定义的所有js
    init: function () {
        actionObj.kpiObjList = kpiObjList;
        actionObj.initActionTbl(actionObj.tableData); // 初始化规则集表格
        actionObj.initBtnClick(); // 事件绑定
        actionObj.initSelect(); // 初始化规则条件类型（全部/任一）
    },
    // 清空规则数据
    clearActCfg: function () {
        actionObj.actSelL = [];
        actionObj.actSelR = [];
        actionObj.actDtlRuleIdx = 0;
        actionObj.actDtlRstIdx = 0;
        actionObj.actRuleConnSel.setValue('or');
        $('#editArrIdx').val('');
        $('#actDetailName').val('');
        $("#actionConfig .actDT").text('规则配置');
        $('.actLeftRuleCdt, .actRightRuleCdt, .cdtRstWrap').html('');
        $('.actDCWrap .cdtResuleIpt').val('');
        $("input[name='isEndAction']").prop('checked', false);
        $("input[name='isEndFlow']").prop('checked', false);
    },
    // 清空上个动作的名字和还原最开始状态
    clear: function () {
        actionObj.actionTbl.clear().draw();
        $('.topOptionWrap .actName').html('<span class="cm-blueLeft actName">规则集</span>');
        $('#doneActionTbl').addClass('hide');
        $('#editActionTbl').removeClass('hide');
        actionObj.tableData = [];
    },
    // 在列表 obj中查txt  在下拉列表中（普通参数）
    searchKey: function (key, obj) {
        var value = '';
        for (var i = 0; i < obj.length; i++) {
            if (value != '') {
                return value;
            }
            if (obj[i].id == key) {
                value = obj[i].title;
                return value;
            } else if (obj[i].children && obj[i].children.length > 0) {
                value = actionObj.searchKey(key, obj[i].children);
            }
        }
        return value;
    },
    // 根据变量id获取变量类型
    searchVariableType: function (variableId, obj) {
        // 在列表obj中查txt  在下拉列表中
        var value = '';
        for (var i = 0; i < obj.length; i++) {
            if (value != '') {
                return value;
            }
            if (obj[i].id == variableId) {
                value = obj[i].typeId;
                return value;
            } else if (obj[i].children && obj[i].children.length > 0) {
                value = actionObj.searchVariableType(variableId, obj[i].children);
            }
        }
        return value;
    },
    // 在正常下拉中寻找txt
    searchTxtFormSel: function (key, obj) {
        for (i = 0; i < obj.length; i++) {
            if (obj[i].key == key) {
                return obj[i].text;
            }
        }
        return "";
    },
    // 更新kpiObj缓存
    updateKpiObjList: function (kpiId, kpiName, kpiType) {
        var hasFlag = false;
        for (var i = 0; i < actionObj.kpiObjList.length; i++) {
            if (kpiId == actionObj.kpiObjList[i].kpiId) { // 有缓存不更新
                hasFlag = true;
                return;
            }
        }
        if (!hasFlag) {
            actionObj.kpiObjList.push({"kpiId": kpiId, "kpiName": kpiName, "kpiType": kpiType});
        }
    },
    // 批量更新kpiObjList缓存, 引用规则集后回调函数更新
    batchUpdateKpiObjList: function (importKpiObjList) {
        if (importKpiObjList && importKpiObjList.length > 0) {
            for (var i = 0; i < importKpiObjList.length; i++) {
                actionObj.updateKpiObjList(importKpiObjList[i].kpiId, importKpiObjList[i].kpiName, importKpiObjList[i].kpiType);
            }
        }
    },
    // 通过kpiId找kpiName
    searchKpiName: function (kpiId) {
        var kpiName = '';
        for (var i = 0; i < actionObj.kpiObjList.length; i++) {
            if (kpiId == actionObj.kpiObjList[i].kpiId) { // 有缓存不更新
                kpiName = actionObj.kpiObjList[i].kpiName;
            }
        }
        return kpiName;
    },
    // 通过kpiId找kpiType
    searchKpiType: function (kpiId) {
        var kpiType = '';
        for (var i = 0; i < actionObj.kpiObjList.length; i++) {
            if (kpiId == actionObj.kpiObjList[i].kpiId) { // 有缓存不更新
                kpiType = actionObj.kpiObjList[i].kpiType;
            }
        }
        return kpiType;
    },
    // 通过元素生成列表
    objToTxt: function (ruleAct) {
        var dataArr = [];
        var tempRuleAct = ruleAct;
        for (var j = 0; j < tempRuleAct.length; j++) {
            var obj = tempRuleAct[j];
            var RHS = obj.RHS;
            var LHS = obj.LHS.condition;
            var conn = obj.LHS.union;
            var strObj = {
                "ruleName": obj.actRuleName,
                "ruleCodition": "",
                "ruleResult": "",
                "isEndAction": obj.isEndAction,
                "isEndFlow": obj.isEndFlow
            };
            var tempStr = [];
            // 遍历规则条件
            for (var i = 0; i < LHS.length; i++) {
                var str = '';
                var lhsArr = JSON.parse(LHS[i]);
                if (lhsArr.varId) { // 普通变量
                    var key1 = lhsArr.varId.substring(1, lhsArr.varId.length - 1);
                    str = actionObj.searchKey(key1, myTreeData) + ' '; // 变量名称
                    str = str + actionObj.searchTxtFormSel(lhsArr.opt, compareSel) + ' '; // 连接条件中文
                } else if (lhsArr.kpiId) { // 左侧指标类型
                    var kpiId = lhsArr.kpiId.substring(1, lhsArr.kpiId.length - 1);
                    str = actionObj.searchKpiName(kpiId) + ' ';
                    str = str + actionObj.searchTxtFormSel(lhsArr.opt, compareSel) + ' ';
                }
                if (lhsArr.valueType == "t_variable") {
                    var keyVal = lhsArr.value.substring(1, lhsArr.value.length - 1);
                    if (lhsArr.isKpi) {
                        str = str + actionObj.searchKpiName(keyVal);
                    } else {
                        str = str + actionObj.searchKey(keyVal, myTreeData);
                    }
                } else {
                    str = str + lhsArr.value;
                }
                tempStr.push(str);
            }
            if (conn == 'or') {
                strObj.ruleCodition = tempStr.join(' 或 ');
            } else if (conn == 'and') {
                strObj.ruleCodition = tempStr.join(' 且 ');
            }

            tempStr = [];
            // 遍历规则结果
            for (var i = 0; i < RHS.length; i++) {
                var str = '';
                var rhsArr = JSON.parse(RHS[i]);
                var key1 = rhsArr.varId.substring(1, rhsArr.varId.length - 1);
                str = actionObj.searchKey(key1, myTreeData) + ' ';
                str = str + actionObj.searchTxtFormSel(rhsArr.opt, compareSelRst) + ' ';
                if (rhsArr.valueType == "t_variable") {
                    var keyVal = rhsArr.value.substring(1, rhsArr.value.length - 1);
                    if (rhsArr.isKpi) {
                        str = str + actionObj.searchKpiName(keyVal);
                    } else {
                        str = str + actionObj.searchKey(keyVal, myTreeData);
                    }
                } else {
                    str = str + rhsArr.value;
                }

                tempStr.push(str);
            }
            strObj.ruleResult = tempStr.join('， ');
            dataArr.push(strObj);
        }

        return dataArr;
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
        resultObj.uid = $("#actUid").val(); // 规则uid
        var txtStr = '';

        if (actRuleName == "") {
            errorTxt = "规则名称不能为空！";
        }
        // 条件配置
        for (var i = 0; i < $('.actLeftRuleCdt>div').length; i++) {
            txtStr = ''; // 条件规则文字描述
            var $tag = $('.actLeftRuleCdt>div:eq(' + i + ')');
            var $selDd = $tag.find('.cm-listSelect');
//			var idx = $selDd.attr('id').substring($selDd.attr('id').length-1,$selDd.attr('id').length);
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
                    typeId1 = actionObj.searchVariableType($('#actLeftIpt' + idx).val(), myTreeData);
                }
                if ($('#lcdtResuleIpt' + idx).val() === 'KPI') {
                    typeId4 = $('#lcdtResuleIpt' + idx).attr('kpiType');
                } else {
                    typeId4 = actionObj.searchVariableType($('#lcdtResuleIpt' + idx).val(), myTreeData);
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
                    leftType = actionObj.searchVariableType($('#actLeftIpt' + idx).val(), myTreeData);
                }
                if (!actionObj.checkValueType(leftType, $.trim($tag.find('.cdtValue').val()))) {
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
                actionObj.updateKpiObjList(kpiId, kpiName, kpiType);
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
                    actionObj.updateKpiObjList(kpiId, kpiName, kpiType);
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
//			var idx = $selDd.attr('id').substring($selDd.attr('id').length - 1,$selDd.attr('id').length);
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
                var typeId1 = actionObj.searchVariableType($('#actRightIpt' + idx).val(), myTreeData);
                var typeId4 = 'typeId4';
                if ($('#rcdtResuleIpt' + idx).val() === 'KPI') {
                    typeId4 = $('#rcdtResuleIpt' + idx).attr('kpiType');
                } else {
                    typeId4 = actionObj.searchVariableType($('#rcdtResuleIpt' + idx).val(), myTreeData);
                }
                if (typeId1 != typeId4) {
                    errorTxt = '变量"' + $('#actRightIpt' + idx + 'Text').val() + '"与变量"' + $('#rcdtResuleIpt' + idx + 'Text').val() + '"类型不符';
                }
            }

            if ($('#actRightValType' + idx).val() == "t_value") { // 校验值类型的输入
                var leftType = actionObj.searchVariableType($('#actRightIpt' + idx).val(), myTreeData);
                if (!actionObj.checkValueType(leftType, $.trim($tag.find('.cdtValue').val()))) {
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
                    actionObj.updateKpiObjList(kpiId, kpiName, kpiType);
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
//			var idx = $selDd.attr('id').substring($selDd.attr('id').length-1,$selDd.attr('id').length);
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
//			var idx = $selDd.attr('id').substring($selDd.attr('id').length-1,$selDd.attr('id').length);
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
    // 初始化规则集表格
    initActionTbl: function (tableData) {
        // console.dir(tableData);
        // 根据引入标识 & 引入方式修改表格排序配置
        if (actionObj.importType == -1 || actionObj.importType == 0) {
            $.extend($('#actionTbl').dataTable.defaults, {
                "rowReorder": {"selector": "td:nth-child(1)"}, // 可拖动行进行重新排序, selector: 拖动第一列
            });
        } else {
            $.extend($('#actionTbl').dataTable.defaults, {
                "rowReorder": false
            });
        }
        actionObj.actionTbl = $('#actionTbl').DataTable({
            "searching": false,
            "destroy": true,
            "bLengthChange": false,
            "paging": false,
//            "pagingType": "full_numbers",
            "data": tableData,
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
                    "targets": 6, "visible": false,
                    "render": function (data, type, row, meta) {
                        return '<span class="iconEdit optIcon"></span><span class="icondel optIcon"></span>';
                    }
                }
            ],
            "drawCallback": function (settings) {
                $('#actionTbl th').unbind("click").removeClass('sorting');
            }
        });
    },
    // 初始化规则条件类型
    initSelect: function () {
        var selectData1 = [
            {key: 'or', text: "任一", selected: true},
            {key: 'and', text: "全部"}
        ];
        actionObj.actRuleConnSel = $('#actRuleConnSel').cm_select({field: "actRuleConn", data: selectData1});
    },
    // 回显规则集规则数据至表格, 此函数调用场景：
    //      1、myflow.editors.js actionEditMore() 调用(点击规则集长方形后回显)
    //      2、删除规则集内某条数据时
    //      3、从规则库引入后重置规则集数据
    setValue: function () {
        actionObj.clearActCfg();
        actionObj.clear();
        // svg数据显示到动作规则配置上
        $('#actionWrap .actName').text($('#propTxts').val());
        actionObj.actionName = $('#propTxts').val();
        actionObj.importType = $('#propIsPublic').val();
        actionObj.ruleSetId = $('#propRuleSetId').val();

        var tblVal = $('#paction input').data('val');
        if (tblVal && tblVal.length && tblVal.length != 0) {
            // console.log("需要回显成表格的动作数据：" + tblVal);
            // 回显
            var txtArr = actionObj.objToTxt(tblVal);
            for (var i = 0; i < txtArr.length; i++) {
                var idx = actionObj.prefixInteger(i + 1, 4);
//				actionObj.tableData.push([parseInt(i+1),txtArr[i].ruleName,txtArr[i].isEndAction,txtArr[i].isEndFlow, txtArr[i].ruleCodition, txtArr[i].ruleResult, tblVal[i]]);
                actionObj.tableData.push([idx, txtArr[i].ruleName, txtArr[i].isEndAction, txtArr[i].isEndFlow, txtArr[i].ruleCodition, txtArr[i].ruleResult, tblVal[i]]);
            }
            actionObj.initActionTbl(actionObj.tableData);
        }
        if (actionObj.importType == 1) { // 仅引用不可编辑修改, 可以本地化
            $('#editActionTbl, #doneActionTbl, #addNewActionRow, #setCommon').addClass('hide');
            $('#setLocal').removeClass('hide');
            // 解绑规则集名称doubleclick事件
            $('#actionWrap .actName').unbind('dblclick');
        } else {
            $('#editActionTbl, #addNewActionRow, #setCommon').removeClass('hide');
            $('#doneActionTbl').addClass('hide');
            $('#setLocal').addClass('hide');
            // 规则集名称doubleclick 变成可编辑状态
            $('#actionWrap .actName').unbind('dblclick').dblclick(function () {
                if ($('#editAcName').length == 0) {
                    var $actName = $('#actionWrap .actName');
                    var htmlStr = '<input id="editAcName" class="cm-blueIpt" value="' + $actName.text() + '" />';
                    $actName.html(htmlStr);
                }
            });
        }
    },
    prefixInteger: function (num, n) { // 数字前补0
        return (Array(n).join(0) + num).slice(-n);
    },
    // 回显规则数据
    setValActValue: function (data) {
        actionObj.clearActCfg();
        $('#editArrIdx').val(data[0]);
        $('#actDetailName').val(data[1]);
        $("#actionConfig .actDT").text(actionObj.actionName + '规则配置');

        var tempData = data[6];
        var LHS = tempData.LHS;
        var RHS = tempData.RHS;
        var uid = tempData.uid;
        $("#actUid").val(uid);
        if (tempData.isEndAction) {
            $("input[name='isEndAction']").prop('checked', tempData.isEndAction);
        }
        if (tempData.isEndFlow) {
            $("input[name='isEndFlow']").prop('checked', tempData.isEndFlow);
        }
        if (LHS.union) {
            actionObj.actRuleConnSel.setValue(LHS.union);
        }

        // 回显规则条件至配置框内
        for (var i = 0; i < LHS.condition.length; i++) {
            $('button.cdtRuleAddBtn').trigger('click');
            var cdtL = JSON.parse(LHS.condition[i]);
            var idx = actionObj.actDtlRuleIdx - 1;
            for (var j = 0; j < actionObj.actSelL.length; j++) {
                if (idx == actionObj.actSelL[j].id) {
                    if (cdtL.varId) { // 普通参数回显
                        actionObj.actSelL[j].sel1.setValue(cdtL.varId.substring(1, cdtL.varId.length - 1));
                    } else if (cdtL.kpiId) { // 回显时条件左侧为指标变量
                        $("#actLeftIpt" + idx).attr('openModal', '0'); // 标识不需要打开model
                        var kpiId = cdtL.kpiId.substring(1, cdtL.kpiId.length - 1);
                        actionObj.actSelL[j].sel1.setValue("KPI");
                        $("#actLeftIpt" + idx).attr('kpiId', kpiId);
                        $("#actLeftIpt" + idx).attr('kpiType', actionObj.searchKpiType(kpiId));
                        $("#actLeftIpt" + idx + "Text").val(actionObj.searchKpiName(kpiId));
                    }
                    actionObj.actSelL[j].sel2.setValue(cdtL.opt);
                    actionObj.actSelL[j].sel3.setValue(cdtL.valueType);
                    if (cdtL.valueType == "t_variable") {
                        if (cdtL.isKpi) { // 回显时条件右侧为指标变量
                            $("#lcdtResuleIpt" + idx).attr('openModal', '0'); // 标识不需要打开model
                            var kpiId = cdtL.value.substring(1, cdtL.value.length - 1);
                            actionObj.actSelL[j].sel4.setValue("KPI");
                            $("#lcdtResuleIpt" + idx).attr('kpiId', kpiId);
                            $("#lcdtResuleIpt" + idx).attr('kpiType', actionObj.searchKpiType(kpiId));
                            $("#lcdtResuleIpt" + idx + "Text").val(actionObj.searchKpiName(kpiId));
                        } else {
                            actionObj.actSelL[j].sel4.setValue(cdtL.value.substring(1, cdtL.value.length - 1));
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
        // 回显规则结果至配置框内
        for (var i = 0; i < RHS.length; i++) {
            $('button.cdtResuleAddBtn').trigger('click');
            var cdtR = JSON.parse(RHS[i]);
            var idx = actionObj.actDtlRstIdx - 1;
            for (var j = 0; j < actionObj.actSelR.length; j++) {
                if (idx == actionObj.actSelR[j].id) {
                    actionObj.actSelR[j].sel1.setValue(cdtR.varId.substring(1, cdtR.varId.length - 1));
                    actionObj.actSelR[j].sel2.setValue(cdtR.opt);
                    actionObj.actSelR[j].sel3.setValue(cdtR.valueType);
                    if (cdtR.valueType == "t_variable") {
                        if (cdtR.isKpi) { // 回显时结果右侧为指标变量
                            $("#rcdtResuleIpt" + idx).attr('openModal', '0'); // 标识不需要打开model
                            var kpiId = cdtR.value.substring(1, cdtR.value.length - 1);
                            actionObj.actSelR[j].sel4.setValue("KPI");
                            $("#rcdtResuleIpt" + idx).attr('kpiId', kpiId);
                            $("#rcdtResuleIpt" + idx).attr('kpiType', actionObj.searchKpiType(kpiId));
                            $("#rcdtResuleIpt" + idx + "Text").val(actionObj.searchKpiName(kpiId));
                        } else {
                            actionObj.actSelR[j].sel4.setValue(cdtR.value.substring(1, cdtR.value.length - 1));
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
        var trdom = $('.saveAskTxt').data("trdom");
        var tagName = actionObj.actionTbl.row(trdom).data()[1];
        // 对应画布规则集记录数据修改
        var actData = $('#paction .moreCdt').data('val');
        for (var i = 0; i < actData.length; i++) {
            if (actData[i].actRuleName == tagName) {
                actData.splice(i, 1);
                $('#paction .moreCdt').data('val', actData);
                break;
            }
        }
        actionObj.setValue();
//		actionObj.actionTbl.row(trdom).remove().draw();
        actionObj.dialog.close();
    },
    // 生成全局唯一ruleID
    getUId: function () {
        var timestamp = (new Date()).valueOf();
        var random = parseInt(Math.random() * 1000);
        return timestamp + random;
    },
    // 事件绑定
    initBtnClick: function () {
        // 规则集表格右上角 编辑 按钮
        $('#editActionTbl').click(function () {
            actionObj.actionTbl.column(6).visible(true); // 展示表格操作列
            $('#actionTbl th').unbind("click").removeClass('sorting');
            $('#doneActionTbl').removeClass('hide');
            $(this).addClass('hide');
            // 解决 点击编辑且无数据时只有三列选中问题
            actionObj.actionTbl.draw();
            // 规则集名称变成可编辑
            if ($('#editAcName').length == 0) {
                var $actName = $('#actionWrap .actName');
                var htmlStr = '<input id="editAcName" class="cm-blueIpt" value="' + $actName.text() + '" />';
                $actName.html(htmlStr);
            }
        });

        // 规则集表格右上角 完成 按钮
        $('#doneActionTbl').click(function () {
            actionObj.actionTbl.column(6).visible(false); // 隐藏表格操作列
            $('#editActionTbl').removeClass('hide');
            $(this).addClass('hide');
            // 规则集名称保存
            if ($('#editAcName').length > 0 && $('#editAcName').val() != '') {
                var txt = $('#editAcName').val();
                $('#editAcName').parent().html(txt);
                $('#propTxts').val(txt).trigger('change');
                actionObj.actionName = txt;
            }
            if ($('#editAcName').length > 0 && $('#editAcName').val() == '') {
                new $.zui.Messager("规则名称不能为空！", {placement: 'center'}).show();
            }
        });

        // 添加新规则 打开规则配置页面
        $('#addNewActionRow').click(function () {
            actionObj.clearActCfg(); // 清空规则数据
            $('#pageContent').addClass('hide'); // 隐藏页面各类配置
            $('#actionConfig').removeClass('hide'); // 显示规则配置div
            $("#actionConfig .actDT").text(actionObj.actionName + '规则配置'); // 显示规则集名称
        });

        // 规则配置取消按钮
        $('#cslActCfg').click(function () {
            $('#pageContent').removeClass('hide');
            $('#actionConfig').addClass('hide');
        });

        // 规则配置保存按钮
        $('#saveActRuleTotbl').click(function () {
            var actObj = actionObj.generateActObj(); // 获取配置页面上的规则数据obj
            var legal = true;
            var arrIpt = $("#paction .moreCdt").data('val') ? $("#paction .moreCdt").data('val') : [];
            if (typeof (actObj) == "object") {
                var conditionStr = '', idx = '';
                if (actObj.LHS.union == 'or') {
                    conditionStr = actObj.LHSTxt.join(' 或 ');
                } else if (actObj.LHS.union == 'and') {
                    conditionStr = actObj.LHSTxt.join(' 且 ');
                }

                if ($('#editArrIdx').val() != '') { // 编辑点进来的
                    idx = parseInt($('#editArrIdx').val()) - 1;
                    var uid = $("#actUid").val();
                    for (var i = 0; i < arrIpt.length; i++) {
                        if (arrIpt[i].uid == uid) {
                            arrIpt[i] = actObj;
                        }
                    }
                } else { // 新增一条规则: 先判断名字不重复然后添加
                    var newName = actObj.actRuleName;
                    for (var i = 0; i < actionObj.tableData.length; i++) {
                        if (actionObj.tableData[i] && actionObj.tableData[i].length > 1 && actionObj.tableData[i][1] == newName) {
                            legal = false;
                            new $.zui.Messager("规则名称不能重复！", {
                                placement: 'center' // 定义显示位置
                            }).show();
                        }
                    }
                    if (legal) {
                        actObj.uid = actionObj.getUId();
                        arrIpt.push(actObj);
                        idx = parseInt(actionObj.tableData.length);
                    }
                }
                actObj.isEndAction = $("input[name='isEndAction']").is(':checked');
                actObj.isEndFlow = $("input[name='isEndFlow']").is(':checked');
                if (legal) {
                    // 更新规则集表格数据并刷新表格
                    actionObj.tableData[idx] = [idx + 1, actObj.actRuleName, actObj.isEndAction, actObj.isEndFlow, conditionStr, actObj.RHSTxt.join('，'), actObj];
                    actionObj.initActionTbl(actionObj.tableData);

                    $('#pageContent').removeClass('hide');
                    $('#actionConfig').addClass('hide');

                    $("#paction .moreCdt").data('val', arrIpt).val(JSON.stringify(arrIpt)).trigger('change');
                    actionObj.clearActCfg();

                    if ($('#editActionTbl.hide').length > 0) {
                        // 当前状态是编辑状态
                        $('#doneActionTbl').trigger('click');
                    }
                }
            } else if (typeof (actObj) == "string") {
                new $.zui.Messager(actObj, {
                    placement: 'center' // 定义显示位置
                }).show();
            }
        });

        // 查看配置预览按钮
        $('#seePreviewBtn').click(function () {
            var tempObj = actionObj.generateActTxt();
            // {"LHSTxt":"","union":"", "RHSTxt":""};
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

        // 新增条件按钮
        $('.cdtRuleAddBtn').click(function () {
            var idx = actionObj.actDtlRuleIdx++;
            var newHtmlStr = '<div class="L' + idx + '">';
            newHtmlStr += '	<div class="leftCdtRuleCdt cm-sel2" id="actLeft' + idx + '"></div>';
            newHtmlStr += '	<div id="actLeftCn' + idx + '" class="cdtRuleCdtOpt cm-sel2"></div>';
            newHtmlStr += '	<div class="cdtResultIptWrap">';
            newHtmlStr += '		<div class="valType cm-sel2" id="actLeftValTypeSel' + idx + '"></div>';
            newHtmlStr += '		<div class="inputVal"><input type="text" class="textL cdtValue" /></div>';
            newHtmlStr += '		<div class="inputSel cm-sel2" id="lcdtResuleSel' + idx + '"></div>';
            newHtmlStr += '	</div>';
            newHtmlStr += '	<span class="delActRow"></span>';
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
                        importKpiModal.initPage(0, echoKpiId, $("#actLeftIpt" + idx), $("#actLeftIpt" + idx + "Text"));
                    } else {
                        $("#actLeftIpt" + idx).removeAttr('kpiId');
                    }
                }
            });
            var sel2 = $('#actLeftCn' + idx).cm_select({field: "actLeftopt" + idx, data: compareSel});
            var sel3 = $('#actLeftValTypeSel' + idx).cm_select({
                field: "actLeftValType" + idx, data: valueType, onselect: function (val) {
                    $(this).find('.cdtValue').val("");
                    if (val == 't_variable') {
                        $(this).siblings('.inputVal').hide();
                        $(this).siblings('.inputSel').show();
                    } else if (val == 't_expRule') {
                        $(this).siblings('.inputSel').hide();
                        $(this).siblings('.inputVal').show();
                        $(this).siblings('.inputVal').bind("click", function () {
                            expRuleContent = $(this).find('.cdtValue').val();
                            expCdtObj = $(this).find('.cdtValue');
                            $("#expRuleModal").modal({'show': 'center', "backdrop": "static"});
                        })
                    } else {
                        $(this).siblings('.inputSel').hide();
                        $(this).siblings('.inputVal').show();
                        $(this).siblings('.inputVal').unbind("click");
                    }
                }
            });
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
                        importKpiModal.initPage(0, echoKpiId, $("#lcdtResuleIpt" + idx), $("#lcdtResuleIpt" + idx + "Text"));
                    } else {
                        $("#lcdtResuleIpt" + idx).removeAttr('kpiId');
                    }
                }
            });
            actionObj.actSelL.push({"id": idx, "sel1": sel1, "sel2": sel2, "sel3": sel3, "sel4": sel4});
        });

        // 新增结果按钮
        $('.cdtResuleAddBtn').click(function () {
            var idx = actionObj.actDtlRstIdx++;
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
                    $(this).find('.cdtValue').val("");
                    if (val == 't_variable') {
                        $(this).siblings('.inputVal').hide();
                        $(this).siblings('.inputSel').show();
                    } else if (val == 't_expRule') {
                        $(this).siblings('.inputSel').hide();
                        $(this).siblings('.inputVal').show();
                        $(this).siblings('.inputVal').bind("click", function () {
                            expRuleContent = $(this).find('.cdtValue').val();
                            expCdtObj = $(this).find('.cdtValue');
                            $("#expRuleModal").modal({'show': 'center', "backdrop": "static"});
                        })
                    } else {
                        $(this).siblings('.inputSel').hide();
                        $(this).siblings('.inputVal').show();
                        $(this).siblings('.inputVal').unbind("click");
                    }
                }
            });
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
                        importKpiModal.initPage(0, echoKpiId, $("#rcdtResuleIpt" + idx), $("#rcdtResuleIpt" + idx + "Text"));
                    } else {
                        $("#rcdtResuleIpt" + idx).removeAttr('kpiId');
                    }
                }
            });

            actionObj.actSelR.push({"id": idx, "sel1": sel1, "sel2": sel2, "sel3": sel3, "sel4": sel4});
        });

        // 删除一条 条件/结果配置数据
        $('.rglWrap').on("click", ".delActRow", function () {
            $(this).parent().remove();
        });

        // 规则集名称修改input的enter事件
        $('#actionWrap').on("keypress", "#editAcName", function (event) {
            if (event.keyCode == 13) {
                if ($('#editAcName').val() == '') {
                    new $.zui.Messager("规则集名称不能为空！", {
                        placement: 'center' // 定义显示位置
                    }).show();
                } else {
                    var txt = $(this).val();
                    $(this).parent().html(txt);
                    $('#propTxts').val(txt).trigger('change');
                }
            }
        });

        // 规则集名称doubleclick 变成可编辑状态
        $('#actionWrap .actName').dblclick(function () {
            if ($('#editAcName').length == 0) {
                var $actName = $('#actionWrap .actName');
                var htmlStr = '<input id="editAcName" class="cm-blueIpt" value="' + $actName.text() + '" />';
                $actName.html(htmlStr);
            }
        });

        // 规则集表格内删除按钮
        $('#actionTbl').on('click', '.icondel', function () {
            /* 弹窗显示是否删除 */
            var htmlStr = '<p class="saveAskTxt">确定删除此条数据？（删除不可恢复）</p>';
            htmlStr += '<div class="modal-footer">';
            htmlStr += '<button type="button" class="btn btn-default cm-btnSec" data-dismiss="modal">取消</button>';
            htmlStr += '<button type="button" class="saveAll btn btn-primary" onclick="actionObj.delOneActionRule(this)">删除</button>';
            htmlStr += '</div>';
            actionObj.dialog = new $.zui.ModalTrigger({title: "提示", custom: htmlStr});
            actionObj.dialog.show();
            // trdom元素挂到span上方便删除时候读取
            $('.saveAskTxt').data("trdom", $(this).parents('tr'));
        });

        // 规则集表格内编辑按钮
        $('#actionTbl').on('click', '.iconEdit', function () {
            // 打开规则配置
            $('#pageContent').addClass('hide');
            $('#actionConfig').removeClass('hide');

            var data = actionObj.actionTbl.row($(this).parents('tr')).data();
            actionObj.setValActValue(data); // 回显规则数据
        });

        // 规则集表格数据行拖动后更新缓存数据
        actionObj.actionTbl.on('row-reordered', function (e, diff, edit) {
            actionObj.tableData = actionObj.tableData.sort();
            actionObj.initActionTbl(actionObj.tableData);
            var tblVal = $('#paction input').data('val');
            var tempArr = [];
            for (var i = 0; i < actionObj.tableData.length; i++) {
                for (var j = 0; j < tblVal.length; j++) {
                    if (tblVal[j].actRuleName == actionObj.tableData[i][1]) {
                        tempArr.push(tblVal[j]);
                    }
                }
            }
            $("#paction .moreCdt").data('val', tempArr).val(JSON.stringify(tempArr)).trigger('change');
        });

        // 点击规则库引入按钮
        $('#importRuleSet').on('click', function () {
            importRuleSetModal.initPage(actionObj.importCallBack);
        });

        // 点击发布按钮
        $('#setCommon').on('click', function () {
            setCommonRuleSetModal.initPage(actionObj.tableData, ruleId, folderId, actionObj.setCommonCallBack);
        });

        // 点击本地化按钮
        $('#setLocal').on('click', function () {
            // 更新importType, ruleSetId置空
            actionObj.importType = 0;
            actionObj.ruleSetId = '';
            $('#propIsPublic').val(0).trigger('change');
            $('#propRuleSetId').val('').trigger('change');
            actionObj.setValue();
            // 更新页面展示内容
            $('#editActionTbl, #addNewActionRow, #setCommon').removeClass('hide');
            $('#doneActionTbl').addClass('hide');
            $('#setLocal').addClass('hide');
            // 规则集名称doubleClick 变成可编辑状态
            $('#actionWrap .actName').unbind('dblclick').dblclick(function () {
                if ($('#editAcName').length == 0) {
                    var $actName = $('#actionWrap .actName');
                    var htmlStr = '<input id="editAcName" class="cm-blueIpt" value="' + $actName.text() + '" />';
                    $actName.html(htmlStr);
                }
            });
        });
    },
    // 引入规则库中规则集modal的回调函数 importType 1仅引用(不可添加/编辑规则) ; 0存为本地
    importCallBack: function (importType, newTableData, ruleSetName, ruleSetHeaderId, ruleSetId, kpiObjList) {
        actionObj.importType = importType;
        actionObj.ruleSetHeaderId = ruleSetHeaderId;
        actionObj.actionName = ruleSetName;
        actionObj.batchUpdateKpiObjList(kpiObjList); // 对比更新模型指标信息数组
        if (importType == 1) { // 仅引用保存id, 其余情况均不保存
            actionObj.ruleSetId = ruleSetId;
            $('#propRuleSetId').val(ruleSetId).trigger('change');
        } else {
            actionObj.ruleSetId = "";
            $('#propRuleSetId').val("").trigger('change');
        }
        // 替换规则集画布缓存数据并重新绘制表格
        $('#propTxts').val(ruleSetName).trigger('change');
        $("#paction .moreCdt").data('val', newTableData).val(JSON.stringify(newTableData)).trigger('change');
        $('#propIsPublic').val(importType).trigger('change');
        actionObj.setValue();
        // 根据引入类型页面展示内容调整
        if (importType == 1) {
            $('#editActionTbl, #doneActionTbl, #addNewActionRow, #setCommon').addClass('hide');
            $('#setLocal').removeClass('hide');
            // 解绑规则集名称doubleclick事件
            $('#actionWrap .actName').unbind('dblclick');
        } else {
            $('#editActionTbl, #addNewActionRow, #setCommon').removeClass('hide');
            $('#doneActionTbl').addClass('hide');
            $('#setLocal').addClass('hide');
            // 规则集名称doubleclick 变成可编辑状态
            $('#actionWrap .actName').unbind('dblclick').dblclick(function () {
                if ($('#editAcName').length == 0) {
                    var $actName = $('#actionWrap .actName');
                    var htmlStr = '<input id="editAcName" class="cm-blueIpt" value="' + $actName.text() + '" />';
                    $actName.html(htmlStr);
                }
            });
        }
    },
    // 发布 回调函数
    setCommonCallBack: function (ruleSetId, ruleSetName) {
        // 更新模型json内相关数据, 发布成功后保存引用, 规则集不可编辑
        actionObj.actionName = ruleSetName;
        actionObj.importType = 1;
        actionObj.ruleSetId = ruleSetId;
        $('#propTxts').val(ruleSetName).trigger('change');
        $('#propIsPublic').val(1).trigger('change');
        $('#propRuleSetId').val(ruleSetId).trigger('change');
        actionObj.setValue();
        $('#editActionTbl, #doneActionTbl, #addNewActionRow, #setCommon').addClass('hide');
        $('#setLocal').removeClass('hide');
        $('#actionWrap .actName').unbind('dblclick'); // 解绑规则集名称doubleClick事件
        // 同步更新保存发布操作后的模型json
        initFlowObj.refreshFlag = false; // 不跳转页面
        $('#stageModel').trigger('click'); // 暂存操作
        // $(".saveAll").click();
    },
};

// 关于接口自定义的相关函数
var interfaceObj = {
    init: function () {
        interfaceObj.bindRadioChange();
    },
    clearValue: function () {
        $(".interfacesWrap input[name='interface']:checked").removeAttr('checked');
    },
    setValue: function () {
        //svg 标签上的数据带到接口信息中
        interfaceObj.clearValue();
        var id = $('#pointerface input').val();
        if (id != '') {
            $(".interfacesWrap input[value='" + id + "']").trigger('click');
        }
    },
    setSelValToSvg: function () {
        var ival = $(".interfacesWrap input[name='interface']:checked").val();
        $('#pointerface .interfaceIpt').val(ival).trigger('change');
        var text = $(".interfacesWrap input[name='interface']:checked").parent().find('span').text();
        $('#propTxts').val(text).trigger('change');
    },
    bindRadioChange: function () {
        $('.interfacesWrap').on("change", "input[name='interface']", function () {
            interfaceObj.setSelValToSvg();
        });
    }
};

// 关于线上条件自定义操作的相关函数
var lineCdtObj = {
    lineCdtConnSel: '',
    lineCdtIdx: 0,
    lineSelArr: [],
    // 关于线上条件自定义操作的相关函数
    init: function () {
        lineCdtObj.initSelect();
        lineCdtObj.initBtnClick();
    },
    initSelect: function () {
        var selectData1 = [
            {key: 'or', text: "任一", selected: true},
            {key: 'and', text: "全部"}
        ];
        lineCdtObj.lineCdtConnSel = $('#lineRuleConnSel').cm_select({field: "lineActRuleConn", data: selectData1});
    },
    // 还原最原始数据
    clearValue: function () {
        $('#editLineName').val('');
        $('.lineRuleCdt').html('');
        lineCdtObj.lineCdtConnSel.setValue('or');
        lineCdtObj.lineCdtIdx = 0;
        $("input[name='isElse']").prop('checked', false);
        $("#lineJudgeWrap .rglWrap").show();
    },
    // 回显条件数据
    setValue: function () {
        lineCdtObj.clearValue();
        //设置名字
        $('#editLineName').val($('#propTxts').val());
        //设置条件
        var secVal = $('#ppathCdt .moreCdt').data('val');
        if (secVal.union) {
            lineCdtObj.lineCdtConnSel.setValue(secVal.union);
        }
        var isElse = secVal.isElse;
        if (isElse == null || isElse == '') {
            isElse = false;
        }
        $("input[name='isElse']").prop('checked', isElse);
        if (isElse) {
            $("#lineJudgeWrap .rglWrap").hide();
        } else {
            $("#lineJudgeWrap .rglWrap").show();
        }
        if (secVal != '' && secVal != 'true' && secVal.condition) {
            for (var i = 0; i < secVal.condition.length; i++) {
                $('button.lineRuleAddBtn').trigger('click');
                var valObj = JSON.parse(secVal.condition[i]);

                var idx = lineCdtObj.lineCdtIdx - 1;
                for (var j = 0; j < lineCdtObj.lineSelArr.length; j++) {
                    if (idx == lineCdtObj.lineSelArr[j].id) {
                        // lineCdtObj.lineSelArr[j].sel1.setValue(valObj.varId.substring(1, valObj.varId.length - 1));
                        // lineCdtObj.lineSelArr[j].sel2.setValue(valObj.opt);
                        // lineCdtObj.lineSelArr[j].sel3.setValue(valObj.valueType);
                        // if (valObj.valueType == 't_variable') {
                        //     lineCdtObj.lineSelArr[j].sel4.setValue(valObj.value.substring(1, valObj.varId.length - 1));
                        // } else {
                        //     $('.lineCdt' + idx + ' .cdtValue').val(valObj.value);
                        // }

                        if (valObj.varId) { // 普通参数回显
                            lineCdtObj.lineSelArr[j].sel1.setValue(valObj.varId.substring(1, valObj.varId.length - 1));
                        } else if (valObj.kpiId) { // 回显时条件左侧为指标变量
                            $("#lineLeftIpt" + idx).attr('openModal', '0'); // 标识不需要打开model
                            var kpiId = valObj.kpiId.substring(1, valObj.kpiId.length - 1);
                            lineCdtObj.lineSelArr[j].sel1.setValue("KPI");
                            $("#lineLeftIpt" + idx).attr('kpiId', kpiId);
                            $("#lineLeftIpt" + idx).attr('kpiType', actionObj.searchKpiType(kpiId));
                            $("#lineLeftIpt" + idx + "Text").val(actionObj.searchKpiName(kpiId));
                        }
                        lineCdtObj.lineSelArr[j].sel2.setValue(valObj.opt);
                        lineCdtObj.lineSelArr[j].sel3.setValue(valObj.valueType);
                        if (valObj.valueType == "t_variable") {
                            if (valObj.isKpi) { // 回显时条件右侧为指标变量
                                $("#lineCdtResuleIpt" + idx).attr('openModal', '0'); // 标识不需要打开model
                                var kpiId = valObj.value.substring(1, valObj.value.length - 1);
                                lineCdtObj.lineSelArr[j].sel4.setValue("KPI");
                                $("#lineCdtResuleIpt" + idx).attr('kpiId', kpiId);
                                $("#lineCdtResuleIpt" + idx).attr('kpiType', actionObj.searchKpiType(kpiId));
                                $("#lineCdtResuleIpt" + idx + "Text").val(actionObj.searchKpiName(kpiId));
                            } else {
                                lineCdtObj.lineSelArr[j].sel4.setValue(valObj.value.substring(1, valObj.value.length - 1));
                            }
                        } else if (valObj.valueType == "t_expRule") {
                            $('.lineCdt' + idx + ' .cdtValue').val(valObj.value);
                            $('.lineCdt' + idx + ' .cdtValue').attr('references', JSON.stringify(valObj.references));
                        } else {
                            $('.lineCdt' + idx + ' .cdtValue').val(valObj.value);
                        }
                    }
                }
            }
        }
    },
    // 保存条件设置
    generateLineObj: function () {
        var resultObj = {
            "isElse": false,
            "condition": [],
            "union": "",
            "pathCdtTxt": []
        };
        resultObj.union = lineCdtObj.lineCdtConnSel.getValue();

        var errorTxt = '';
        var cdtObj = new Object();
        var isElse = $("input[name='isElse']").is(':checked');
        if (isElse == null || isElse == '') {
            isElse = false;
        }
        resultObj.isElse = isElse;
        if (isElse) {
            return resultObj;
        }
        var pathCdtTxtStr = ''; // 条件规则文字描述
        for (var i = 0; i < $('.lineRuleCdt>div').length; i++) {
            cdtObj = {};
            pathCdtTxtStr = '';
            var $tag = $('.lineRuleCdt>div:eq(' + i + ')');
            var $selDd = $tag.find('.cm-listSelect');
//			var idx = $selDd.attr('id').substring($selDd.attr('id').length-1,$selDd.attr('id').length);
            var idx = $selDd.attr('id').replace(/[^0-9]/ig, "");
            if (!isElse && $('#lineLeftIpt' + idx).val() == '') {
                errorTxt = '规则集条件配置的左侧不能为空！';
                return errorTxt;
            } else {
                // cdtObj.varId = '[' + $('#lineLeftIpt' + idx).val() + ']';
                if ($('#lineLeftIpt' + idx).val() === 'KPI') { // 判断左侧变量是否为指标
                    var kpiId = $('#lineLeftIpt' + idx).attr('kpiId');
                    var kpiType = $('#lineLeftIpt' + idx).attr('kpiType');
                    var kpiName = $('#lineLeftIpt' + idx + 'Text').val();
                    cdtObj.kpiId = '[' + kpiId + ']';
                    actionObj.updateKpiObjList(kpiId, kpiName, kpiType);
                } else {
                    cdtObj.varId = '[' + $('#lineLeftIpt' + idx).val() + ']';
                }
                pathCdtTxtStr += $('#lineLeftIpt' + idx + 'Text').val() + ' ';
            }
            if (!isElse && $('#lineLeftOpt' + idx).val() == '') {
                errorTxt = '规则集条件配置的操作符不能为空！';
                return errorTxt;
            } else {
                cdtObj.opt = $('#lineLeftOpt' + idx).val();
                pathCdtTxtStr += $('#lineLeftOpt' + idx + 'Text').val() + ' ';
            }
            cdtObj.valueType = $('#lineValType' + idx).val();
            if (!isElse && $tag.find('.cdtValue').val() == '' && $('#lineCdtResuleIpt' + idx).val() == '') {
                errorTxt = '规则集条件配置的右侧不能为空！';
                return errorTxt;
            } else {
                // if (cdtObj.valueType == "t_variable") {
                //     cdtObj.value = '[' + $('#lineCdtResuleIpt' + idx).val() + ']';
                // } else {
                //     cdtObj.value = $.trim($tag.find('.cdtValue').val());
                // }
                if (cdtObj.valueType == "t_variable") {
                    if ($('#lineCdtResuleIpt' + idx).val() === 'KPI') { // 判断右侧变量是否为指标
                        var kpiId = $('#lineCdtResuleIpt' + idx).attr('kpiId');
                        var kpiType = $('#lineCdtResuleIpt' + idx).attr('kpiType');
                        var kpiName = $('#lineCdtResuleIpt' + idx + 'Text').val();
                        cdtObj.isKpi = true;
                        cdtObj.value = '[' + kpiId + ']';
                        actionObj.updateKpiObjList(kpiId, kpiName, kpiType);
                    } else {
                        cdtObj.value = '[' + $('#lineCdtResuleIpt' + idx).val() + ']';
                    }
                    pathCdtTxtStr += $('#lineCdtResuleIpt' + idx + 'Text').val();
                } else if (cdtObj.valueType == "t_expRule") {
                    cdtObj.value = $.trim($tag.find('.cdtValue').val());
                    cdtObj.references = JSON.parse($tag.find('.cdtValue').attr('references'));
                    pathCdtTxtStr += $.trim($tag.find('.cdtValue').val());
                } else {
                    cdtObj.value = $.trim($tag.find('.cdtValue').val());
                    pathCdtTxtStr += $.trim($tag.find('.cdtValue').val());
                }
            }

            //保存规则条件详情
            resultObj.condition.push(JSON.stringify(cdtObj));
            resultObj.pathCdtTxt.push(pathCdtTxtStr);
        }

        return resultObj;
    },
    initBtnClick: function () {
        // 新增条件按钮
        $('button.lineRuleAddBtn').click(function () {
            var idx = lineCdtObj.lineCdtIdx++;
            var newHtmlStr = '<div class="lineCdt' + idx + '">';
            newHtmlStr += '	<div class="lineCdtRuleCdt cm-sel2" id="lineLeft' + idx + '"></div>';
            newHtmlStr += '	<div id="lineLeftCn' + idx + '" class="lineRuleCdtOpt cm-sel2"></div>';
            newHtmlStr += '	<div class="cdtResultIptWrap">';
            newHtmlStr += '		<div class="valType cm-sel2" id="lineValTypeSel' + idx + '"></div>';
            newHtmlStr += '		<div class="inputVal"><input type="text" class="textL cdtValue" /></div>';
            newHtmlStr += '		<div class="inputSel cm-sel2" id="lineCdtResule' + idx + '"></div>';
            newHtmlStr += '	</div>';
            newHtmlStr += '	<span class="delActRow"></span>';
            newHtmlStr += '</div>';

            $('.lineRuleCdt').append(newHtmlStr);
            // var sel1 = $('#lineLeft' + idx).cm_treeSelect({field: "lineLeftIpt" + idx, data: myTreeData});
            var sel1 = $('#lineLeft' + idx).cm_treeSelect({
                field: "lineLeftIpt" + idx, data: myTreeData, onselect: function (val) {
                    if (val === 'KPI') { // 条件左侧变量引入指标
                        if ($("#lineLeftIpt" + idx).attr('openModal') === '0') { // 0通过回显触发事件
                            $("#lineLeftIpt" + idx).attr('openModal', '1'); // 修改标识, 不弹出弹框
                            return;
                        }
                        var echoKpiId = '';
                        if ($("#lineLeftIpt" + idx).attr('value') === 'KPI') { // 如果之前选择了指标则保持状态
                            echoKpiId = $("#lineLeftIpt" + idx).attr('kpiId');
                        }
                        $("#lineLeftIpt" + idx + "Text").val('');
                        importKpiModal.initPage(0, echoKpiId, $("#lineLeftIpt" + idx), $("#lineLeftIpt" + idx + "Text"));
                    } else {
                        $("#lineLeftIpt" + idx).removeAttr('kpiId');
                    }
                }
            });
            var sel2 = $('#lineLeftCn' + idx).cm_select({field: "lineLeftOpt" + idx, data: compareSel});
            // var sel3 = $('#lineValTypeSel' + idx).cm_select({
            //     field: "lineValType" + idx, data: valueType, onselect: function (val) {
            //         if (val == 't_variable') {
            //             $(this).siblings('.inputVal').hide();
            //             $(this).siblings('.inputSel').show();
            //         } else {
            //             $(this).siblings('.inputSel').hide();
            //             $(this).siblings('.inputVal').show();
            //         }
            //     }
            // });
            var sel3 = $('#lineValTypeSel' + idx).cm_select({
                field: "lineValType" + idx, data: valueType, onselect: function (val) {
                    $(this).find('.cdtValue').val("");
                    if (val == 't_variable') {
                        $(this).siblings('.inputVal').hide();
                        $(this).siblings('.inputSel').show();
                    } else if (val == 't_expRule') {
                        $(this).siblings('.inputSel').hide();
                        $(this).siblings('.inputVal').show();
                        $(this).siblings('.inputVal').bind("click", function () {
                            expRuleContent = $(this).find('.cdtValue').val();
                            expCdtObj = $(this).find('.cdtValue');
                            $("#expRuleModal").modal({'show': 'center', "backdrop": "static"});
                        })
                    } else {
                        $(this).siblings('.inputSel').hide();
                        $(this).siblings('.inputVal').show();
                        $(this).siblings('.inputVal').unbind("click");
                    }
                }
            });
            // var sel4 = $('#lineCdtResule' + idx).cm_treeSelect({field: "lineCdtResuleIpt" + idx, data: myTreeData});
            var sel4 = $('#lineCdtResule' + idx).cm_treeSelect({
                field: "lineCdtResuleIpt" + idx, data: myTreeData, onselect: function (val) {
                    if (val === 'KPI') { // 条件右侧变量引入指标
                        if ($("#lineCdtResuleIpt" + idx).attr('openModal') === '0') { // 0通过回显触发事件
                            $("#lineCdtResuleIpt" + idx).attr('openModal', '1'); // 修改标示, 不弹出弹框
                            return;
                        }
                        var echoKpiId = '';
                        if ($("#lineCdtResuleIpt" + idx).attr('value') === 'KPI') { // 如果之前选择了指标则保持状态
                            echoKpiId = $("#lineCdtResuleIpt" + idx).attr('kpiId');
                        }
                        $("#lineCdtResuleIpt" + idx + "Text").val('');
                        importKpiModal.initPage(0, echoKpiId, $("#lineCdtResuleIpt" + idx), $("#lineCdtResuleIpt" + idx + "Text"));
                    } else {
                        $("#lineCdtResuleIpt" + idx).removeAttr('kpiId');
                    }
                }
            });
            lineCdtObj.lineSelArr.push({"id": idx, "sel1": sel1, "sel2": sel2, "sel3": sel3, "sel4": sel4});
        });

        // 保存线上条件
        $('#saveLineCdt').click(function () {
            var lineName = $('#editLineName').val();
            var lineObj = lineCdtObj.generateLineObj();
            var legal = true;
            var isElse = lineObj.isElse;
            if (isElse == null || isElse == '') {
                isElse = false;
            }
            if (typeof (lineObj) == "object") {
                if (lineName == "") {
                    new $.zui.Messager("条件名称不能为空！", {
                        placement: 'center' // 定义显示位置
                    }).show();
                    legal = false;
                }
                if (!isElse && lineObj.condition.length == 0) {
                    new $.zui.Messager("条件一定要有一条条件配置！", {
                        placement: 'center' // 定义显示位置
                    }).show();
                    legal = false;
                }
                // console.log(JSON.stringify(lineObj));
                if (legal) {
                    $('#propTxts').val(lineName).trigger('change');
                    $('#ppathCdt .moreCdt').data('val', lineObj).val(JSON.stringify(lineObj)).trigger('change');
                    new $.zui.Messager("保存到条件判断成功！", {
                        type: 'success',
                        placement: 'center' // 定义显示位置
                    }).show();
                    $('#lineJudgeWrap').hide();
                    $('#propFootDiv').show();
                    $('#content').removeClass('minContent');
                }
            } else if (typeof (lineObj) == "string") {
                new $.zui.Messager(lineObj, {
                    placement: 'center' // 定义显示位置
                }).show();
            }

        });

        // 是否else分支CheckBox的点击事件
        $(".isElseClass").click(function () {
            var value = $(this).is(":checked");
            if (value) {
                $("#lineJudgeWrap .rglWrap").hide();
            } else {
                $("#lineJudgeWrap .rglWrap").show();
            }
        });
    }
};

var funcMethodObj = {
    funcMethodSel: '',
    methodResultcSel: '',
    paramSels: [],
    //关于线上条件自定义操作的相关函数
    init: function () {
        funcMethodObj.initSelect();
        funcMethodObj.initBtnClick();
    },
    initSelect: function () {
        funcMethodObj.funcMethodSel = $('#funcMethodSel').cm_treeSelect({
            field: "funcMethodField", data: funcMethTree, onselect: function (index) {
                for (var i = 0; i < funcMethTree.length; i++) {
                    if (index == funcMethTree[i].id) {
                        funcMethodObj.initParam(funcMethTree[i].funcList);
                        funcMethodObj.initMethodResult(funcMethTree[i].functionReturnType);
                    }
                }
            }
        });
    },
    initMethodResult: function (functionReturnType) {
        var html = '';
        if (functionReturnType == 1) {
            html += '<span>函数结果:</span><div id="methodResultcSel" class="lineRuleCdtOpt cm-sel2"></div>';
            $("#funcResult").addClass("funcMethodLine");
            $("#funcResult").html(html);
            funcMethodObj.methodResultcSel = $('#methodResultcSel').cm_treeSelect({
                field: "paramSelIpt1",
                data: myTreeData
            });
        }
    },
    splitMyTreeDataByType: function (type) {
        var myTreeDataByType = jQuery.extend(true, [], myTreeData);
        for (var i = 0; i < myTreeDataByType.length; i++) {
            var variableData = myTreeDataByType[i].children;
            for (var j = 0; j < variableData.length; j++) {
                if (type != variableData[j].typeId) {
                    variableData.splice(j, 1);
                }
            }
        }
        return myTreeDataByType;
    },
    initParam: function (funcList) {
        var html = "";
        var num = funcList.length;
        if (num > 0) {
            html = '<span>参数:</span>';
            for (var i = 0; i < num; i++) {
                html += '<div id="paramSel' + i + '" class="lineRuleCdtOpt cm-sel2"></div>';
            }
        }
        $("#funcMethodParam").html(html);
        funcMethodObj.paramSels = [];
        for (var i = 0; i < num; i++) {
            var myTreeDataByType = funcMethodObj.splitMyTreeDataByType(funcList[i].typeId);
            var paramSel = $('#paramSel' + i).cm_treeSelect({field: "paramSelIpt" + i, data: myTreeDataByType});
            funcMethodObj.paramSels.push(paramSel);
        }
    },
    clearValue: function () {
        //还原最原始数据
        $("#funcMethodParam").html("");
        funcMethodObj.paramSels = [];
    },
    setValue: function () {
        funcMethodObj.clearValue();
        var secVal = $('#pfuncMethod .funcMethodP').data('val');
        if (secVal == null || secVal == '') {
            return;
        }
        funcMethodObj.funcMethodSel.setValue(secVal.funcId);
        if (funcMethodObj.methodResultcSel != "") {
            funcMethodObj.methodResultcSel.setValue(secVal.rtnResult);
        }

        if (secVal != '') {
            for (var i = 0; i < secVal.params.length; i++) {
                if (i < funcMethodObj.paramSels.length) {
                    funcMethodObj.paramSels[i].setValue(secVal.params[i].variableId)
                }
            }
        }
    },
    generateLineObj: function () {
        var resultObj = new Object();
        resultObj.funcId = funcMethodObj.funcMethodSel.getValue();
        if (funcMethodObj.methodResultcSel != "") {
            resultObj.rtnResult = funcMethodObj.methodResultcSel.getValue();
        }
        var params = new Array();
        for (var i = 0; i < funcMethodObj.paramSels.length; i++) {
            var json = new Object();
            json.type = 1;
            json.variableId = funcMethodObj.paramSels[i].getValue();
            params.push(json);
        }
        resultObj.params = params;
        return resultObj;
    },
    initBtnClick: function () {
        $('#saveFuncMethod').click(function () {
            //保存线上条件
            var funcObj = funcMethodObj.generateLineObj();
            var funcName = funcMethodObj.funcMethodSel.getText();
            var legal = true;
            if (typeof (funcObj) == "object") {
                if (legal) {
                    $('#propTxts').val(funcName).trigger('change');
                    $('#pfuncMethod .funcMethodP').data('val', funcObj).val(JSON.stringify(funcObj)).trigger('change');
                    new $.zui.Messager("保存到函数成功！", {
                        type: 'success',
                        placement: 'center' // 定义显示位置
                    }).show();
                    $('#funcMeWrap').hide();
                    $('#propFootDiv').show()
                    $('#content').removeClass('minContent');
                }
            } else if (typeof (funcObj) == "string") {
                new $.zui.Messager(funcObj, {
                    placement: 'center' // 定义显示位置
                }).show();
            }

        });
    }
};

// 关于初始化flow插件、大页面保存、返回按钮相关函数
var initFlowObj = {
    nextStepUrl: '',
    jumpFlag: true,
    saveRuleSuccess: false,
    // svgWidth: 1500,
    // svgHeight: $(document).height() - 260,
    svgWidth: 2000, //为了避免画布大小不够用，直接定更大的
    svgHeight: 1800,
    refreshFlag: true, // 保存json之后是否跳转页面flag(规则集发布功能会改变该值)
    // 关于初始化flow插件、大页面保存、返回按钮相关函数
    init: function () {
        if (optMode == 'update' || optMode == 'view') {
            $('.topStepWrap').css('text-align', 'left');
            $(".ruleNameEdit").text(moduleName).removeClass('hide');
            $('#ruleName').val(moduleName);
            $('#ruleDiscribe').val(ruleDesc);
            $(".modelVersionTitle").text('（' + version + '）').removeClass('hide'); // 回显版本号
            $("input[name='isLog'][value=" + isLog + "]").attr("checked", true);
            $('.topStepWrap label, #gotoLastPage').addClass('hide');
        }
        initFlowObj.initFlow(ruleContent);
        initFlowObj.beforeLeave();
        initFlowObj.gotoLastPageBtn();
        initFlowObj.bindBtnClick();
    },
    // 初始化流程图插件 TODOTODO
    initFlow: function (ruleContent) {
        //console.log(ruleContent);
        // if (ruleContent.svgWidth) {
        //     initFlowObj.svgWidth = ruleContent.svgWidth;
        // }
        // if (ruleContent.svgHeight && initFlowObj.svgHeight < ruleContent.svgHeight) {
        //     initFlowObj.svgHeight = ruleContent.svgHeight;
        // }
        // $('#myflow').html('').myflow({
        //     svgWidth: initFlowObj.svgWidth + 'px',
        //     svgHeight: initFlowObj.svgHeight + 'px',
        //     restore: (ruleContent)
        // });

        $('#myflow').html('').myflow({
            svgWidth: initFlowObj.svgWidth + 'px',
            svgHeight: initFlowObj.svgHeight + 'px',
            restore: (ruleContent)
        });

        $("#myflow_tools").show();
    },
    jumpPage: function () {
        initFlowObj.saveRuleSuccess = true;
        initFlowObj.jumpFlag = true;
        creCommon.loadHtml(initFlowObj.nextStepUrl);
    },
    showJumpSureDialog: function () {
        //判断有未保存数据弹窗询问是否跳转
        /* 使用触发器对象直接显示 */
        var htmlStr = '<p class="saveAskTxt">系统判断有未保存的数据，如跳转页面可能导致数据丢失，继续跳转吗？</p>';
        htmlStr += '<div class="modal-footer">';
        // htmlStr += '<button type="button" class="btn btn-default cm-btnSec" data-dismiss="modal">取消</button>';
        htmlStr += '<button type="button" class="btn btn-minor" data-dismiss="modal">取消</button>';
        htmlStr += '<button type="button" class="saveAll btn btn-primary" onclick="initFlowObj.jumpPage()">跳转</button>';
        htmlStr += '</div>';
        (new $.zui.ModalTrigger({title: "提示", custom: htmlStr})).show();
    },
    // 离开页面触发是否保存提示，防止误点丢失数据
    beforeLeave: function () {
        function beforeLoadHtml(url) {
            initFlowObj.nextStepUrl = url;
            if (hasChange && ("update" == optMode || "insert" == optMode || "view" == optMode) && !initFlowObj.saveRuleSuccess) {
                if (!(pageType === '5' || pageType === '6')) { // 非查看
                    initFlowObj.jumpFlag = false;
                    initFlowObj.showJumpSureDialog();
                }
            }
            return initFlowObj.jumpFlag;
        }

        creCommon.beforeLoadHtml = beforeLoadHtml;
    },
    saveRule: function (data) {
        if (!$('#editRuleName form').isValid()) {
            return;
        }

        var ruleNameInput = $.trim($('#ruleName').val());
        var curData = JSON.parse(data);
        curData.svgWidth = initFlowObj.svgWidth;
        curData.svgHeight = initFlowObj.svgHeight;
        var data = JSON.stringify(curData);

        var urlStr = '';
        var saveAllType = $('#editRuleName .saveAll').attr('saveAllType');
        if (saveAllType === 'stage') { // 暂存
            urlStr = '/rule/version/stage';
        } else if (saveAllType === 'submit') { // 提交
            urlStr = '/rule/version/commit';
        } else if (saveAllType === 'publish') { // 发布
            urlStr = '/rule/version/publish';
        }

        var sendObj;
        sendObj = {
            "folderId": folderId,
            "ruleId": ruleId,
            "ruleName": ruleName,
            "moduleName": ruleNameInput,
            "ruleDesc": $('#ruleDiscribe').val(),
            "ruleType": ruleType,
            'versionDesc': $.trim($('#modelVersionDes').val()),
            "data": data,
            "optMode": optMode,
            "isFirst": 0
        };

        // insert[0场景下新建模型 1模型库模型第一版本 2模型库新增版本(无)]
        // update[3场景下 4模型库]
        // view[5场景下 6模型库]
        if (pageType === '0' || pageType === '3' || pageType === '5') {
            sendObj['isPublic'] = '0';
            if (pageType === '0') {
                sendObj["deptId"] = $.trim($('#deptId').val());
                sendObj["deptName"] = $.trim($('#deptName').val());
                sendObj["partnerCode"] = $.trim($('#partnerCode').val());
                sendObj["partnerName"] = $.trim($('#partnerName').val());
                sendObj["productCode"] = $.trim($('#productCode').val());
                sendObj["productName"] = $.trim($('#productName').val());
                sendObj["systemCode"] = $.trim($('#systemCode').val());
                sendObj["systemName"] = $.trim($('#systemName').val());
                sendObj["isFirst"] = '1';
            }
        } else if (pageType === '1' || pageType === '2' || pageType === '4' || pageType === '6') {
            sendObj['isPublic'] = '1';
            if (pageType === '1') { // 模型库下第一版本携带组基本信息及其他头部信息
                sendObj['modelGroupId'] = $('#modelGroupSel').attr('value'); // 模型库进入带groupId
                sendObj['modelGroupName'] = $('#modelGroupSelText').val();
                sendObj["deptId"] = $.trim($('#deptId').val());
                sendObj["deptName"] = $.trim($('#deptName').val());
                sendObj["partnerCode"] = $.trim($('#partnerCode').val());
                sendObj["partnerName"] = $.trim($('#partnerName').val());
                sendObj["productCode"] = $.trim($('#productCode').val());
                sendObj["productName"] = $.trim($('#productName').val());
                sendObj["systemCode"] = $.trim($('#systemCode').val());
                sendObj["systemName"] = $.trim($('#systemName').val());
                sendObj["isFirst"] = '1';
                if (sendObj.modelGroupId === 'empty' || !sendObj.modelGroupId) {
                    failedMessager.show('模型组无效！');
                    return;
                }
            }
        } else {
            failedMessager.show('[pageType]参数无效');
            return;
        }

        // console.dir(sendObj);

        $('#editRuleName').modal('hide');
        // TODO 保存模型请求: 测试用,记得解开注释
        $.ajax({
            url: webpath + urlStr,
            data: sendObj,
            dataType: "json",
            type: "post",
            success: function (data) {
                //判断是否成功跳转页面
                if (data.status === 0) {
                    initFlowObj.saveRuleSuccess = true;
                    initFlowObj.jumpFlag = true;
                    var message = '保存成功';
                    successMessager.show(message);

                    if (initFlowObj.refreshFlag) {
                        var flagName = $('#folderMenuWrap').attr('class');
                        var flagStr = '&childOpen=' + flagName;
                        if (pageType === '1') { // 1库新模型第一版本
                            // var url = webpath + "/modelBase/view?idx=16" + flagStr;
                            // creCommon.loadHtml(url);
                        } else if (pageType === '2' || pageType === '4') { // 2新版本 & 4修改版本(更新/新版本)
                            // var jumpRuleName = sendObj.ruleName || ruleName;
                            // var url = webpath + "/modelBase/view?idx=16" + flagStr + '&jumpRuleName=' + jumpRuleName;
                            // creCommon.loadHtml(url);
                        } else { // 0/3
                            // var url = webpath + "/ruleFolder/rulePackageMgr?folderId=" + folderId;
                            // creCommon.loadHtml(url);
                        }

                        // insert[0场景下新建模型 1模型库模型第一版本 2模型库新增版本(无)]
                        // update[3场景下 4模型库]
                        // view[5场景下 6模型库]

                        // 不跳转页面, 刷新模型编辑页展示最新的模型
                        var newPageType = pageType;
                        if (pageType === '0') {
                            newPageType = '3';
                        }
                        if (pageType === '1') {
                            newPageType = '4';
                        }
                        var url = webpath + "/rule/updateRule?ruleId=" + data.data.ruleId + "&folderId=" + folderId + "&childOpen=" + flagName + "&pageType=" + newPageType;
                        creCommon.loadHtml(url);
                    }

                } else {
                    failedMessager.show('保存失败--' + data.msg);
                }
            },
            complete: function () {
                initFlowObj.refreshFlag = true;
            }
        });
    },
    // 开始结束节点校验
    checkStartAndEnd: function () {
        var curData = JSON.parse($.myflow.config.getCurData());
        var statesObj = curData.states;
        var hasStart = false;
        var hasEnd = false;
        for (var k in statesObj) {
            if (statesObj[k].type === "start") {
                hasStart = true;
            }
            if (statesObj[k].type === "end") {
                hasEnd = true;
            }
        }
        if (hasStart && !hasEnd) {
            failedMessager.show('模型至少有一个结束节点！');
            return false;
        }
        if (!hasStart && hasEnd) {
            failedMessager.show('模型缺失开始节点，请添加！');
            return false;
        }
        return true;
    },
    bindBtnClick: function () {
        // 点击暂存 变更保存按钮属性
        $('#stageModel').click(function () {
            if (initFlowObj.checkStartAndEnd()) {
                $("#editRuleName .saveAll").attr('saveAllType', 'stage');
                $('#editRuleName').modal({'show': 'center', "backdrop": "static"});
            }
        });
        // 点击提交 变更保存按钮属性
        $('#submitModel').click(function () {
            if (initFlowObj.checkStartAndEnd()) {
                $("#editRuleName .saveAll").attr('saveAllType', 'submit');
                $('#editRuleName').modal({'show': 'center', "backdrop": "static"});
            }
        });
        // 点击发布 变更保存按钮属性
        $('#publishModel').click(function () {
            if (initFlowObj.checkStartAndEnd()) {
                $("#editRuleName .saveAll").attr('saveAllType', 'publish');
                $('#editRuleName').modal({'show': 'center', "backdrop": "static"});
            }
        });

        $('body').on('keydown', 'input', function (e) {
            //阻止delete按键之后影响svg图的删除节点
            window.event ? window.event.cancelBubble = true : e.stopPropagation();
        });

        //放大画布
        $('#biggerSVG').click(function () {
            var $mf = $('#myflow');
            var $svg = $('#myflow svg');
            var tagW = $mf.width() + 100;
            var tagH = $mf.height();
            $mf.width(tagW).height(tagH);
            $svg.width(tagW).height(tagH);
            initFlowObj.svgWidth = tagW;
            initFlowObj.svgHeight = tagH;
            var ruleContent = JSON.parse($.myflow.config.getCurData());
            initFlowObj.initFlow(ruleContent);
        });
        //缩小画布
        $('#smallerSVG').click(function () {
            var $mf = $('#myflow');
            var $svg = $('#myflow svg');
            var tagW = $mf.width() - 100;
            var tagH = $mf.height();
            $mf.width(tagW).height(tagH);
            $svg.width(tagW).height(tagH);
            initFlowObj.svgWidth = tagW > 1500 ? tagW : 1500;
            initFlowObj.svgHeight = tagH;
            var ruleContent = JSON.parse($.myflow.config.getCurData());
            initFlowObj.initFlow(ruleContent);
        });
    },
    // 画布上一步页面跳转
    gotoLastPageBtn: function () {
        $('#gotoLastPage, #lastPageLink').click(function () {
            var hrefStr = webpath;
            if (optMode == "insert") { // pageType 0/1/2
                if (pageType === '0') {
                    hrefStr += "/ruleFolder/rulePackageMgr?folderId=" + folderId + '&childOpen=o';
                } else {
                    var flagName = $('#folderMenuWrap').attr('class');
                    var flagStr = '&childOpen=' + flagName;
                    hrefStr += "/modelBase/view?idx=16" + flagStr + '&jumpRuleName=' + ruleName;
                }
            } else { // pageType Update(3/4) View(5/6)
                if (pageType === '3' || pageType === '5') {
                    hrefStr += "/ruleFolder/rulePackageMgr?folderId=" + folderId + '&childOpen=o';
                } else {
                    var flagName = $('#folderMenuWrap').attr('class');
                    var flagStr = '&childOpen=' + flagName;
                    hrefStr += "/modelBase/view?idx=16" + flagStr + '&jumpRuleName=' + ruleName + '&moduleName=' + moduleName;
                }
            }
            creCommon.loadHtml(hrefStr);
        });
    }
};

// 关于页面内容的初始化
var pageContentObj = {
    modelBaseObj: {}, // 由模型库新版本/修改版本进入页面后存储headObj
    init: function () {
        if (pageType === '5' || pageType === '6') { // 查看 无保存操作按钮
            $('#propFootDiv .btn-primary').css('display', 'none');
        } else {
            $('#propFootDiv .btn-primary').css('display', 'inline-block');
        }

        if (pageType === '0' || pageType === '1') { // 新建
            // $('#setCommon, #setLocal').css('display', 'none'); // 无规则集发布、无本地化按钮
            $('#setCommon').css('display', 'none'); // 无规则集发布
            $('#ruleName').val('');
        } else {
            $('#setCommon, #setLocal').css('display', ''); // 恢复默认样式
        }

        $('#ruleName').val(moduleName); // 回显模型基本信息中的模型名称以免保存时报错

        // pageType
        //      insert[0场景下新建模型 1模型库模型第一版本 2模型库新增版本]
        //      update[3场景下 4模型库]
        //      view[5场景下 6模型库]
        if (pageType === '0') { // 场景下新建模型
            $('.modelBaseContent').removeClass('hide'); // 基础信息展示
            // 无组信息
            $('.publicModel').addClass('hide');
            $('#modelBaseGroupSelectorP').css('display', 'none');
        } else if (pageType === '1') { // 模型库新建模型: all展示
            $('.modelBaseContent').removeClass('hide');
            $('#modelBaseGroupSelectorP').css('display', 'inline-block');
            // 初始化模型库组下拉框
            $.ajax({
                url: webpath + '/modelBase/group/list',
                type: 'POST',
                dataType: "json",
                data: {},
                success: function (data) {
                    var groupObj = [{key: 'empty', text: "无"}];
                    if (data.data.length > 0) {
                        groupObj = [];
                        var dataArr = data.data;
                        for (var i = 0; i < dataArr.length; i++) {
                            var obj = {key: dataArr[i].modelGroupId, text: dataArr[i].modelGroupName};
                            groupObj.push(obj);
                        }
                    }
                    $('#modelBaseGroupSelector').cm_select({field: "modelGroupSel", data: groupObj});
                }
            });
        } else if (pageType === '3' || pageType === '4') { // 修改: 所有头信息不可见
            $('.modelBaseContent').addClass('hide');
            $('#modelBaseGroupSelectorP').css('display', 'none');
        }
    }
}

$(function () {
    //文档加载完成初始化
    actionObj.init();
    interfaceObj.init();
    initFlowObj.init();
    lineCdtObj.init();
    funcMethodObj.init();
    pageContentObj.init();
});
