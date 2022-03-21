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
    //初始化下拉选
    //规则类型
    if (typeof (ruleTypeList) != "undefined") {
        ruleTypeList[0].selected = true;
        var ruleTypeSec = $('#ruleType').cm_select({field: "ruleTypeSel", data: ruleTypeList});
        if (ruleType != undefined && ruleType != null && ruleType != "") {
            ruleTypeSec.setValue(ruleType);
        }
    }

    //规则所属类别
    if (typeof (ruleFolderList) != "undefined") {
        ruleFolderList[0].selected = true;
        if (myFolderId != undefined && myFolderId != null && myFolderId != "") {
            for (var i = 0; i < ruleFolderList.length; i++) {
                if (ruleFolderList[i].key == myFolderId) {
                    ruleFolderList[0].selected = false;
                    ruleFolderList[i].selected = true;
                }
            }
        }
        var ruleFolderSec = $('#ruleFolder').cm_select({
            field: "ruleFolderSel", data: ruleFolderList, onselect: function () {
                variableDataTable.refresh();
            }
        });
    }

    variableTypeList[0].selected = true;
    rtSel = $('#valType').cm_select({field: "valTypeSel", data: variableTypeList});
    //修改变量类型
    rtSelSec = $('#valTypeSec').cm_select({field: "valTypeSelSec", data: variableTypeList});

    variableKindList[0].selected = true;
    kindSel = $('#valKind').cm_select({
        field: "valKindSel", data: variableKindList, onselect: function () {
            variableDataTable.refresh();
        }
    });

    kindSelSec = $('#valKindSec').cm_select({field: "valKindSelSec", data: variableKindList});

}

function nextSetp() {
    // var url = webpath + "/rule/ruleConfig?folderId=" + $.trim($("#ruleFolderSel").val()) + "&ruleType=" + $("#ruleTypeSel").val() + '&childOpen=o';
    var url = webpath + "/rule/ruleConfig?folderId=" + $.trim($("#ruleFolderSel").val()) + "&ruleType=" + $("#ruleTypeSel").val() + '&childOpen=o&pageType=0';
    creCommon.loadHtml(url);
}

function preStep() {
    var url = webpath + "/ruleFolder/rulePackageMgr?folderId=" + $.trim($("#ruleFolderSel").val()) + '&childOpen=o';
    creCommon.loadHtml(url);
}

var ruleFolder = {
    show: function () {
        $("#newFolder").modal({});
    },
    hide: function () {
        $("#newFolder").modal("hide");
    },
    //显示最后创建的文件夹
    showLastRuleFolder: function () {
        $.ajax({
            url: webpath + "/ruleProp/ruleFolder",
            type: "post",
            dataType: "json",
            success: function (data) {
                //$('#ruleFolder').cm_select({field:"ruleFolderSel",data:JSON.parse(data)});
                //选中最后一行
                var ruleSel = $('#ruleFolder').cm_select({
                    field: "ruleFolderSel", data: data, onselect: function () {
                        variableDataTable.refresh();
                    }
                });
                ruleSel.setValue(data[data.length - 1].key);
            }

        });
    },
    //新建规则类别
    submit: function () {
        if ($("#newFolderForm").isValid()) {
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
                        ruleFolder.clear();
                        ruleFolder.hide();
                        successMessager.show('添加成功');
                        ruleFolder.showLastRuleFolder();
                        var flagName = $('#folderMenuWrap').attr('class');
                        var url = webpath + "/create?idx=2&childOpen=" + flagName;
                        creCommon.loadHtml(url);
                    } else {
                        failedMessager.show('添加失败，' + data.msg);
                    }
                }
            });
        }
    },
    clear: function () {
        $("#newFolder .modal-body input").each(function () {
            $(this).val("");
        })
    }
}

// 场景下参数列表
var variableDataTable = {
    init: function () {
        //搜索按钮
        $("#searchBtn").click(function () {
            variableDataTable.refresh();
        });
        var a = $('#table1').width('100%').dataTable({
            "searching": false,
            "ordering": false,
            "destroy": true,
            "paging": true,
            "info": true,
            "serverSide": true,
            "pageLength": 10,
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
                        htmlStr += '<span class="cm-tblB" onclick="modifyModal.show(this, 1);" type="button">查看</span>';
                        htmlStr += '<span class="cm-tblB" onclick="modifyModal.show(this, 0);" type="button">修改</span>';
                        if (row.typeId == "3") {
                            htmlStr += '<span class="cm-tblB openAttrBtn" onclick="entityModal.init(\'' + row.entityId + '\',\'' + row.variableAlias + '\',false)" type="button">属性</span>';
                        }
                        htmlStr += '<span class="cm-tblC delBtn" onclick="deleteModal.show(\'' + row.variableId + '\',\'' + row.entityId + '\')" type="button">删除</span>';
                        htmlStr += '<span class="cm-tblB setCommon" onclick="setCommonModal.show(\'' + row.variableId + '\',\'' + row.entityId + '\',\'' + row.typeId + '\')" type="button">设为公有</span>';
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
                        "kindId": $.trim($("#valKindSel").val()),
                        "variableAlias": $.trim($("#searchVal").val())
                    });
                }
            },
            "fnDrawCallback": function (oSettings, json) {
                // $("tr:even").css("background-color", "#fbfbfd");
                // $("table:eq(0) th").css("background-color", "#f6f7fb");
            }
        });
    },
    refresh: function () {
        $("#table1").dataTable().fnDraw(true);//重新绘制表格
    }
}

// 设为公共参数
var setCommonModal = {
    show: function (variableId, entityId, typeId) {
        if (variableId == null || variableId == '') {
            return;
        }
        if (typeId == null || typeId == '') {
            return;
        }
        // 初始化参数组
        setCommonModal.initParamGroup();
        $('#setCommonParamAlert').modal({'show': 'center', "backdrop": "static"});
        // 绑定保存事件
        $('#setCommonParamAlert #setCommonSubmit').unbind().click(function () {
            var obj = {};
            obj["variableId"] = variableId;
            obj["typeId"] = typeId;
            obj["entityId"] = entityId;
            obj["variableGroupId"] = $('#setCommon_paramGroupSelector option:selected').attr('group-id');
            setCommonModal.setCommonSubmit(obj);
        });
    },
    batchShow: function () {
        $('#batchSetCommonParamAlert').modal({'show': 'center', "backdrop": "static"});
    },
    batchSave: function () {
    	debugger;
        var folderId = $("#ruleFolderSel").val();
        var groupName = $("#paramGroupName").val();
        var obj = {};
        obj["folderId"] = folderId;
        obj["variableRemarks"] = groupName;
        $.ajax({
            url: webpath + '/variable/batchUpgrade',
            type: 'POST',
            dataType: "json",
            data: obj,
            beforeSend: function () {
            	$('#loadingModal').modal({'show': 'center', "backdrop": "static"});
            },
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('修改成功！');
                    variableDataTable.refresh();
                } else {
                    failedMessager.show(data.msg);
                }
                $("#batchSetCommonParamAlert").modal("hide");
            },
            complete: function () {
            	$('#loadingModal').modal('hide');
            }
        });
    },
    initParamGroup: function () {
        $.ajax({
            url: webpath + '/variable/pub/group/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.data.length > 0) {
                    var data = data.data;
                    var htmlStr = '';
                    for (var i = 0; i < data.length; i++) {
                        htmlStr += '<option group-id=\'' + data[i].variableGroupId + '\'>' + data[i].variableGroupName + '</option>';
                    }
                    $('#setCommon_paramGroupSelector').empty().html(htmlStr);
                }
            }
        });
    },
    // 场景下参数设为公共参数
    setCommonSubmit: function (obj) {
        $.ajax({
            url: webpath + '/variable/upgrade',
            type: 'POST',
            dataType: "json",
            data: obj,
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('修改成功！');
                    variableDataTable.refresh();
                    $('#setCommonParamAlert').modal('toggle', 'center');
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
}

// 场景下新建变量
var newValModal = {
    show: function () {
        var kindType = $('#valKindSel').attr('value');
        if (kindType === 'K4') {
            $('.defaultValueWrap').removeClass('hide');
        } else {
            $('.defaultValueWrap').addClass('hide');
        }
        $("#newVal").modal({});
    },
    hide: function () {
        $("#newVal").modal("hide");
    },
    clear: function () {
        $("#newVal .modal-body input").each(function () {
            $(this).val("");
        })
        rtSel.setValue(variableTypeList[0].key);
    },
    submit: function () {
        if (!$("#newValForm").isValid()) {
            return;
        }
        //新建变量
        var data = {};
        //变量别名
        data.variableAlias = $("#variableAlias").val();
        //变量编码
        data.variableCode = $("#variableCode").val();
        if ($.inArray(data.variableCode, javaKeyWordsCheckArr) != -1) {
            creCommon.showErrorValidator("newValForm", 'variableCode', data.variableCode + '为关键字.');
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
        if ("K4" === data.kindId) {
            if (data.defaultValue == null || data.defaultValue == '') {
                creCommon.showErrorValidator("newValForm", "variableDefaultValue", "系统常量值不能为空");
                return;
            }
            if (data.typeId === '2') {
                data.defaultValue = parseFloat(data.defaultValue);
                if (Math.floor(data.defaultValue) !== data.defaultValue) {
                    failedMessager.show('你输入的默认值不是整型，请重新输入！');
                    return;
                }
            }
            if (data.typeId === '4') {
                if (isNaN(parseFloat(data.defaultValue))) {
                    failedMessager.show('你输入的默认值不是浮点型，请重新输入！');
                    return;
                }
            }
        }

        $.ajax({
            url: webpath + "/insertVariable",
            data: data,
            type: "post",
            dataType: "json",
            success: function (data) {
                if (data.status === 0) {
                    newValModal.clear();
                    newValModal.hide();
                    successMessager.show('添加成功');
                    variableDataTable.refresh();
                } else {
                    // showerrorvalidator("newValForm", data.msg, "名称已存在");
                    failedMessager.show(data.msg);
                }
            }
        });
    }
}


function showerrorvalidator(showForm, showId, showMsg) {
    $('#' + showForm).validator('showMsg', '#' + showId, {type: "error", msg: showMsg});
}

var modifyModal = {
    show: function ($this, type) {
        // type 0修改 1查看
        var row = {};
        if ($this) {
            var curRow = $this.parentNode.parentNode;
            row = $('#table1').DataTable().row(curRow).data();
        }
        var kindType = $('#valKindSel').attr('value');
        if (kindType === 'K4') {
            $('.defaultValueWrap').removeClass('hide');
        } else {
            $('.defaultValueWrap').addClass('hide');
        }
        //修改/查看规则
        $("#variableIdSec").val(row.variableId);
        $("#variableAliasSec").val(row.variableAlias);
        $("#variableCodeSec").val(row.variableCode);
        rtSelSec.setValue(row.typeId);
//			 kindSelSec.setValue(row.kindId);
        $("#variableRemarksSec").val(row.variableRemarks);
        $("#variableDefaultValueSec").val(row.defaultValue);
        variableTypeOri = row.typeId;
        $("#entityIdSec").val(row.entityId); // 补上entityId
        $("#modifyVariable").removeAttr('usedFlag');
        $('#variableAliasSec, #variableCodeSec').removeAttr('disabled');
        $('.modifyVariable_cronMsg').addClass('hide');

        if (type === 0) {
            $('#modifyVariable .modal-title').text('修改变量');
            // 校验是否在用状态并标识
            $.ajax({ // 验证是否在用
                url: webpath + '/variable/checkUsed',
                type: 'POST',
                dataType: "json",
                data: {"variableId": $("#variableIdSec").val()},
                success: function (data) {
                    if (data.status === -1) {
                        $("#modifyVariable").attr('usedFlag', '1'); // 被引用
                        $('#variableAliasSec, #variableCodeSec').attr('disabled', true);
                        $('.modifyVariable_cronMsg').removeClass('hide');
                    } else {
                        $("#modifyVariable").attr('usedFlag', '0'); // 未被引用
                        $('#variableAliasSec, #variableCodeSec').removeAttr('disabled');
                        $('.modifyVariable_cronMsg').addClass('hide');
                    }
                }
            });
            $("#modifyVariable").modal({});
        } else if (type === 1) {
            for (var key in row) {
                var target = $("#paramViewAlertModal .form-control[col-name='" + key + "']");
                if (target.length > 0) {
                    $(target).val(row[key]);
                }
            }
            if (row.kindId === 'K4') {
                $('#paramViewAlertModal .defaultValueGroup').removeClass('hide');
            } else {
                $('#paramViewAlertModal .defaultValueGroup').addClass('hide');
            }
            $("#paramViewAlertModal").modal({});
        }
    },
    hide: function () {
        $("#modifyVariable").modal("hide");
    },
    submit: function () {
        if ($("#modifyForm").isValid()) {
            //修改变量
            var obj = {};
            obj.folderId = $.trim($("#ruleFolderSel").val());
            obj.variableId = $("#variableIdSec").val();
            obj.variableAlias = $("#variableAliasSec").val();
            obj.variableCode = $("#variableCodeSec").val();
            if ($.inArray(obj.variableCode, javaKeyWordsCheckArr) != -1) {
                creCommon.showErrorValidator("modifyForm", 'variableCodeSec', obj.variableCode + '为关键字.');
                return;
            }
            //变量类型
            obj.typeId = rtSelSec.getValue();
            obj.kindId = kindSel.getValue();
            if (variableTypeOri == "3" && variableTypeOri != obj.typeId) {
                failedMessager.show('对象类型不允许修改');
                return;
            }
            //变量描述
            obj.variableRemarks = $("#variableRemarksSec").val();
            obj.defaultValue = $("#variableDefaultValueSec").val();
            if ("K4" === obj.kindId) {
                if (obj.defaultValue == null || obj.defaultValue == '') {
                    creCommon.showErrorValidator("modifyForm", "variableDefaultValueSec", "系统常量值不能为空");
                    return;
                }
                if (obj.typeId === '2') {
                    obj.defaultValue = parseFloat(obj.defaultValue);
                    if (Math.floor(obj.defaultValue) !== obj.defaultValue) {
                        failedMessager.show('你输入的默认值不是整型，请重新输入！');
                        return;
                    }
                }
                if (obj.typeId === '4') {
                    if (isNaN(parseFloat(obj.defaultValue))) {
                        failedMessager.show('你输入的默认值不是浮点型，请重新输入！');
                        return;
                    }
                }
            }
            obj.entityId = $("#entityIdSec").val(); // 补上entityId

            var usedFlag = $("#modifyVariable").attr('usedFlag');
            if (usedFlag == '0') {
                modifyModal.submitAjax(obj);
            } else if (usedFlag == '1') {
                // 验证未通过则警告, 若再次确认则修改
                $('#modifyVariable').modal('hide');
                confirmAlert.show(
                    '参数被引用中, 是否仍要修改？',
                    function () {
                        modifyModal.submitAjax(obj);
                    }, function () {
                        $('#modifyVariable').modal();
                    });
            } else {
                return;
            }

            // $.ajax({ // 验证是否在用
            //     url: webpath + '/variable/checkUsed',
            //     type: 'POST',
            //     dataType: "json",
            //     data: {"variableId": $("#variableIdSec").val()},
            //     success: function (data) {
            //         if (data.status == 0) {
            //             modifyModal.submitAjax(obj);
            //         } else {
            //             // 验证未通过直接拒绝
            //             // failedMessager.show('修改失败，' + data.msg);
            //
            //             // 验证未通过则警告, 若再次确认则修改
            //             $('#modifyVariable').modal('hide');
            //             confirmAlert.show(
            //                 data.msg + ', 是否仍要修改？',
            //                 function () {
            //                     modifyModal.submitAjax(obj);
            //                 });
            //         }
            //     }
            // });
        }
    },
    submitAjax: function (obj) {
        $.ajax({
            url: webpath + "/variable/updateVariable",
            data: obj,
            type: "post",
            dataType: "json",
            success: function (data) {
                if (data.status === 0) {
                    modifyModal.hide();
                    successMessager.show('修改成功');
                    variableDataTable.refresh();
                } else {
                    failedMessager.show('修改失败，' + data.msg);
                }
            }
        });
    }
}

var deleteModal = {
    show: function (variableId, entityId) {
        $("#variableIdSec").val(variableId);
        $("#entityIdSec").val(entityId);
        $("#delVariableDiv").modal({});
    },
    submit: function () {
        //删除规则
        var obj = {};
        //变量别名
        obj.variableId = $("#variableIdSec").val();
        obj.entityId = $("#entityIdSec").val();
        obj.folderId = $("#ruleFolderSel").val();
        $.ajax({ // 验证是否可删
            url: webpath + '/variable/checkUsed',
            type: 'POST',
            dataType: "json",
            data: {"variableId": $("#variableIdSec").val()},
            success: function (data) {
                if (data.status == 0) { // 可删
                    $.ajax({
                        url: webpath + "/variable/deleteVariable",
                        data: obj,
                        type: "post",
                        dataType: "json",
                        success: function (data) {
                            if (data.status == 0) {
                                $("#delVariableDiv").modal('hide');
                                successMessager.show('删除成功');
                                variableDataTable.refresh();
                            } else {
                                failedMessager.show('删除失败，' + data.msg);
                                $("#delVariableDiv").modal('hide');
                            }
                        }
                    });
                } else {
                    failedMessager.show('删除失败，' + data.msg);
                    $("#delVariableDiv").modal('hide');
                }
            }
        });
    }
}

function bindMoal() {
    $('#newFolder').on('hidden.zui.modal', function () {
        $("#newFolderForm").validator("cleanUp");
    });
    $('#newVal').on('hidden.zui.modal', function () {
        $("#newValForm").validator("cleanUp");
    });
    $('#modifyVariable').on('hidden.zui.modal', function () {
        $("#modifyForm").validator("cleanUp");
    });
}

// 创建向导快捷入口
var quickEnter = {
    url: '',
    jumpPage: function () {
        if (quickEnter.url) {
            creCommon.loadHtml(quickEnter.url);
        }
    },
    // 公共池
    intoCommonResource: function () {
        quickEnter.url = webpath + "/common/resource/view?idx=5&childOpen=" + $('#folderMenuWrap').attr('class');
        quickEnter.jumpPage();
    },
    // 指标
    intoKpi: function () {
        quickEnter.url = webpath + "/kpi/view?idx=14&childOpen=" + $('#folderMenuWrap').attr('class');
        quickEnter.jumpPage();
    },
    // 规则
    intoRuleSet: function () {
        quickEnter.url = webpath + "/ruleSet/view?idx=15&childOpen=" + $('#folderMenuWrap').attr('class');
        quickEnter.jumpPage();
    },
    // 模型
    intoModel: function () {
        quickEnter.url = webpath + "/modelBase/view?idx=16&childOpen=" + $('#folderMenuWrap').attr('class');
        quickEnter.jumpPage();
    }
};

$(function () {
    //文档加载完成初始化
    initSelect();
    variableDataTable.init();
    bindMoal();
});