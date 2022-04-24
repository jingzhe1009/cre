/**
 * 创建规则向导页面
 * data:2018/3/19
 * author:jsj
 */
var rtSel;
var rtSelSec;
var kindSel;
var kindSelSec;

function initSelect() {
    // 初始化变量类型下拉框
    variableTypeList[0].selected = true;
    rtSel = $('#valType').cm_select({field: "valTypeSel", data: variableTypeList}); // 新建变量弹框内变量类型
    rtSelSec = $('#valTypeSec').cm_select({field: "valTypeSelSec", data: variableTypeList}); // 修改变量弹框变量类型

    // 左上角种类下拉框
    variableKindList[0].selected = true;
    kindSel = $('#valKind').cm_select({
        field: "valKindSel", data: variableKindList, onselect: function () {
            refreshDataTable();
        }
    });

    kindSelSec = $('#valKindSec').cm_select({field: "valKindSelSec", data: variableKindList});
//	if(variableKind){
//		ruleTypeSec.setValue(variableKind);
//	}
}

function nextSetp() {
    var url = webpath + "/ruleFolder/rulePackageMgr?folderId=" + $.trim($("#ruleFolderSel").val());
    creCommon.loadHtml(url);
}

//绑定事件
function eventBind() {
    deleteVariableBind();
}

//显示最后创建的文件夹
function showLastRuleFolder() {
    $.ajax({
        url: webpath + "/ruleProp/ruleFolder",
        type: "post",
        dataType: "json",
        success: function (data) {
            //$('#ruleFolder').cm_select({field:"ruleFolderSel",data:JSON.parse(data)});
            //选中最后一行
            data[data.length - 1].selected = true;
            $('#ruleFolder').cm_select({
                field: "ruleFolderSel", data: data, onselect: function () {
                    refreshDataTable();
                }
            });
        }

    });
}

function refreshDataTable() {
    $("#table1").dataTable().fnDraw(false);//重新绘制表格
}

//初始化表格
function initTable() {
    //搜索按钮
    $("#searchBtn").click(function () {
        refreshDataTable();
    });
    //初始化表格
    var pageLength = 10;//每页显示条数
    $.extend($.fn.dataTable.defaults, {
        info: true,
        "serverSide": true,
//		"paging": true, 
        "pageLength": pageLength
    });
    $('#table1').width('100%').dataTable({
        "searching": false,
        "ordering": false,
        "destroy": true,
        "bLengthChange": false,
        "pagingType": "full_numbers",
        "columns": [
            {"title": "变量别名", "data": "variableAlias"},
            {"title": "变量编码", "data": "variableCode"},
            {"title": "变量类型", "data": "typeValue"},
            {"title": "变量种类", "data": "kindValue"},
            {"title": "默认值", "data": "defaultValue"},
            {
                "title": "操作", "data": null, "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<span class="cm-tblB" onclick="btnModify(this);" data-position="center" data-toggle="modal" data-target="#modifyVariable">修改</span>';
                    if (row.typeId == "3") {
                        htmlStr += '<span class="cm-tblB openAttrBtn" onclick="initEntityTable(\'' + row.entityId + '\',\'' + row.variableAlias + '\',false)">属性</span>';
                    }
                    htmlStr += '<span class="cm-tblC delBtn" onclick="deleteVariable(\'' + row.variableId + '\',\'' + row.entityId + '\')">删除</span>';
                    return htmlStr;
                }
            }
        ],
        ajax: {
            url: webpath + '/variable/queryVariable',
            "type": 'POST',
            "data": function (d) {//查询参数
                return $.extend({}, d, {
                    "folderId": $.trim($("#ruleFolderSel").val()),
                    "kindId": $.trim($("#valKindSel").val())
                });
            }
        }
    });
}

function submitNewVal() {
    //新建变量
    var data = {};
    //变量别名
    data.variableAlias = $("#variableAlias").val();
    //变量编码
    data.variableCode = $("#variableCode").val();
    if ($.inArray(data.variableCode, javaKeyWordsCheckArr) != -1) {
        showerrorvalidator("newValForm", 'variableCode', data.variableCode + '为关键字.');
        return;
    }
    //变量类型
    data.typeId = rtSel.getValue();
    data.kindId = kindSel.getValue();
    //所属类别
    data.folderId = $.trim($("#ruleFolderSel").val());
    //变量描述
    data.variableRemarks = $("#variableRemarks").val();
    data.defaultValue = $("#variableDefaultValue").val();
    if ("K4" == data.kindId && (data.defaultValue == null || data.defaultValue == '')) {
        creCommon.showErrorValidator("newValForm", "variableDefaultValue", "系统常量值不能为空");
        return;
    }
    $.ajax({
        url: webpath + "/insertVariable",
        data: data,
        type: "post",
        dataType: "json",
        success: function (data) {
            if (data.status === 0) {
                refreshDataTable();
            } else {
                failedMessager.show(data.msg);
            }
        }
    });

}

function submitNewFolƒder() {
    //新建规则类别
    var data = {};
    data.folderName = $("#folderName").val();
    data.folderDesc = $("#floderDesc").val();
    $.ajax({
        url: webpath + "/insertRuleFolder",
        data: data,
        type: "post",
        dataType: "json",
        success: function (data) {
            if (data.status === 0) {
                showLastRuleFolder();
            } else {
                failedMessager.show(data.msg);
            }
        }
    });
}

function btnModify($this) {
    var row = {};
    if ($this) {
        var curRow = $this.parentNode.parentNode;
        row = $('#table1').DataTable().row(curRow).data();
    }
    //修改规则
    $("#variableIdSec").val(row.variableId);
    $("#variableAliasSec").val(row.variableAlias);
    $("#variableCodeSec").val(row.variableCode);
    rtSelSec.setValue(row.typeId);
//	 kindSelSec.setValue(row.kindId);
    $("#variableRemarksSec").val(row.variableRemarks);
    $("#variableDefaultValueSec").val(row.defaultValue);

    variableTypeOri = row.typeId;
}

function deleteVariable(variableId, entityId) {
    $("#variableIdSec").val(variableId);
    $("#entityIdSec").val(entityId);
    $("#delVariableDiv").modal({});
}

function deleteVariableBind() {
    $("#delVariableBtn").bind("click", function () {
        //删除规则
        var data = {};
        //变量别名
        data.variableId = $("#variableIdSec").val();
        data.entityId = $("#entityIdSec").val();
        data.folderId = $("#folderId").val();

        $.ajax({
            url: webpath + "/variable/deleteVariable",
            data: data,
            type: "post",
            dataType: "json",
            success: function (data) {
                if (data.status === 0) {
                    $("#delVariableDiv").modal('hide');
                    new $.zui.Messager('删除成功', {
                        placement: 'center', // 定义显示位置
                        time: 1000,//表示时间延迟，单位毫秒
                        type: 'success' // 定义颜色主题
                    }).show();
                    refreshDataTable();
                } else {
                    failedMessager.show('删除失败，' + data.msg);
                    $("#delVariableDiv").modal('hide');
                }
            }
        });
    });
}

//确定修改
function sureModify() {
    //修改变量
    var data = {};
    data.folderId = $.trim($("#ruleFolderSel").val());
    data.variableId = $("#variableIdSec").val();
    data.variableAlias = $("#variableAliasSec").val();
    data.variableCode = $("#variableCodeSec").val();
    if ($.inArray(data.variableCode, javaKeyWordsCheckArr) != -1) {
        showerrorvalidator("modifyForm", 'variableCodeSec', data.variableCode + '为关键字.');
        return;
    }
    //变量类型
    data.typeId = rtSelSec.getValue();
    data.kindId = kindSel.getValue();
    if (variableTypeOri == "3" && variableTypeOri != data.typeId) {
        new $.zui.Messager('对象类型不允许修改', {
            time: 2000,//表示时间延迟，单位毫秒
            placement: 'center', // 定义显示位置
            type: 'warning' // 定义颜色主题
        }).show();
        return;
    }
    //变量描述
    data.variableRemarks = $("#variableRemarksSec").val();
    data.defaultValue = $("#variableDefaultValueSec").val();
    if ("K4" == data.kindId && (data.defaultValue == null || data.defaultValue == '')) {
        creCommon.showErrorValidator("modifyForm", "variableDefaultValueSec", "系统常量值不能为空");
        return;
    }
    $.ajax({
        url: webpath + "/variable/updateVariable",
        data: data,
        type: "post",
        dataType: "json",
        success: function (data) {
            if (data.status === 0) {
                new $.zui.Messager('修改成功', {
                    placement: 'center', // 定义显示位置
                    time: 1000,//表示时间延迟，单位毫秒
                    type: 'success' // 定义颜色主题
                }).show();
                refreshDataTable();
            } else {
                new $.zui.Messager('修改失败', {
                    time: 1000,//表示时间延迟，单位毫秒
                    placement: 'center', // 定义显示位置
                    type: 'warning' // 定义颜色主题
                }).show();
            }
        }
    });
}

$(function () {
    //文档加载完成初始化
    initSelect();
    initTable();
    eventBind();
});