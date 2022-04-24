var entityIds = new Array();
var rtEntitySel;
var rtEntitySelSec;
var variableTypeOri;

function initEntitySelect() {
    variableTypeList[0].selected = true;
    rtEntitySel = $('#entityValType').cm_select({field: "valTypeEntitySel", data: variableTypeList});
    //修改变量类型
    rtEntitySelSec = $('#entityValTypeSec').cm_select({field: "valTypeEntitySelSec", data: variableTypeList});

    variableKindList[0].selected = true;
}

var entityDataTable = {
    entityDataTable: "",
    init: function () {
        //初始化表格
        entityDataTable.entityDataTable = $('#tableEntity').width("100%").DataTable({
            "searching": false,
            "ordering": false,
            "destroy": true,
            "paging": false,
            "info": false,
//			"serverSide":true,
            "scrollY": "200px",
            "scrollCollapse": "true",
//			"pageLength":10,
            "bLengthChange": false,
//			"pagingType": "full_numbers",
            "columns": [
                {"title": "变量别名", "data": "variableAlias"},
                {"title": "变量编码", "data": "variableCode"},
                // {"title": "编码别名", "data": "entityVariableAlias"},
                {"title": "变量类型", "data": "typeValue"},
                {"title": "变量种类", "data": "kindValue"},
                {"title": "默认值", "data": "defaultValue"},
                {
                    "title": "操作", "data": null, "width": "200px", "render": function (data, type, row) {
                        var htmlStr = "";
                        htmlStr += '<span class="cm-tblB" onclick="entityModifyModal.show(this, 1);">查看</span>';
                        htmlStr += '<span class="cm-tblB" onclick="entityModifyModal.show(this, 0);">修改</span>';
                        if (row.typeId == "3") {
                            htmlStr += '<span class="cm-tblB openAttrBtn" onclick="entityModal.recursive(\'' + row.entityId + '\',\'' + row.variableAlias + '\',false)">属性</span>';
                        }
                        htmlStr += '<span class="cm-tblC delBtn" onclick="deleteEntityModal.show(\'' + row.variableId + '\',\'' + row.entityId + '\')">删除</span>';
                        return htmlStr;
                    }
                }
            ],
            ajax: {
                url: webpath + '/variable/queryVariableRelation',
                "type": 'POST',
                "data": function (d) {//查询参数
                    return $.extend({}, d, {
                        "entityId": $("#entityPid").val()
//	                	"folderId":$.trim($("#ruleFolderSel").val())
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
        var entity = entityIds[entityIds.length - 1];
        if (entity) {
            entityModal.init(entity.entityId, entity.entityName, true);
            $("#tableEntity").dataTable().fnDraw(false);
//			initEntityTable();
        }
    },
    clear: function () {
//		entityDataTable.entityDataTable.clear();
        entityDataTable.entityDataTable.destroy();
        $('#tableEntity').empty();
    }
}

var entityModal = {
    recursive: function (entityId, entityName, flag) {
        entityDataTable.clear();
        entityModal.init(entityId, entityName, flag);
        entityDataTable.init();
    },
    init: function (entityId, entityName, flag) {
        if (!flag) {
            var entity = {};
            entity.entityId = entityId, entity.entityName = entityName;
            entityIds.push(entity);
        }
        $("#entityPropTitle").text(entityName + "属性");
        $("#entityPid").val(entityId);
        entityModal.show();
    },
    show: function () {
        $("#entityProp").modal({'backdrop': 'static', "show": true});
    },
    //显示上一层
    showLast: function () {
        var showEntity = entityIds.pop();

        if (showEntity == undefined || entityIds.length == 0) {
            entityModal.hide();
        } else {
            var entity = entityIds[entityIds.length - 1];
            entityModal.recursive(entity.entityId, entity.entityName, true);
        }
    },
    hide: function () {
        entityDataTable.clear();
        $("#entityProp").modal('hide');
    },
    hideClear: function () {
        entityIds.splice(0, entityIds.length);
        entityDataTable.clear();
        $("#entityProp").modal('hide');
    }
}

// 新建
var newEntityVal = {
    show: function () {
        entityModal.hide();
        newEntityVal.clear();
        $("#newEntityValModal").modal({});
    },
    hide: function () {
        $("#newEntityValModal").modal("hide");
    },
    clear: function () {
        $("#newEntityValModal .modal-body input").each(function () {
            $(this).val("");
        })
        rtEntitySel.setValue(variableTypeList[0].key);
    },
    submit: function () {
        if ($("#newEntityValForm").isValid()) {
            //新建变量
            var data = {};
            //变量别名
            data.variableAlias = $("#variableAliasEntity").val();
            //变量编码
            data.variableCode = $("#entityVariableCode").val();
            //变量类型
            data.typeId = rtEntitySel.getValue();
            data.kindId = kindSel.getValue();
            //所属类别
            data.folderId = $.trim($("#ruleFolderSel").val());
            //变量描述
            data.variableRemarks = $("#entityVariableRemarks").val();
            // data.entityVariableAlias = $("#entityVariableAlias").val();
            data.entityVariableAlias = $("#entityVariableCode").val(); // 编码别名与变量编码一致, 页面不展示
            data.entityPid = $("#entityPid").val();
            $.ajax({
                url: webpath + "/variable/insertVariableRelation",
                data: data,
                type: "post",
                dataType: "json",
                success: function (data) {
//					entityDataTable.refresh();
                    if (data.status === 0) {
                        newEntityVal.hide();
                        successMessager.show('新建成功');
                    } else {
                        // showerrorvalidator("newEntityValForm", "entityVariableAlias", data.msg);
                        failedMessager.show('新建失败' + data.msg);
                    }
                }
            });
        }
    }
}

// 场景下对象类型参数修改 TODO
var entityModifyModal = {
    show: function ($this, type) {
        var row = {};
        if ($this) {
            var curRow = $this.parentNode.parentNode;
            row = $('#tableEntity').DataTable().row(curRow).data();
        }
        //type 0修改 1查看
        entityModal.hide();
        //修改规则
        $("#entityVariableIdSec").val(row.variableId);
        $("#variableAliasEntitySec").val(row.variableAlias);
        $("#entityVariableCodeSec").val(row.variableCode);
        rtEntitySelSec.setValue(row.typeId);
        $("#entityVariableRemarksSec").val(row.variableRemarks);
        // $("#entityVariableAliasSec").val(row.entityVariableAlias); // 编码别名
        variableTypeOri = row.typeId;

        $("#modifyEntity").removeAttr('usedFlag');
        $('#variableAliasEntitySec').removeAttr('disabled');
        $('#modifyEntity_cronMsg').addClass('hide');

        if (type === 0) {
            $('#modifyEntity .modal-title').text('修改变量');
            // 校验是否在用状态并标识
            $.ajax({ // 验证是否在用
                url: webpath + '/variable/checkUsed',
                type: 'POST',
                dataType: "json",
                data: {"variableId": $("#entityVariableIdSec").val()},
                success: function (data) {
                    if (data.status === -1) {
                        $("#modifyEntity").attr('usedFlag', '1'); // 被引用
                        $('#variableAliasEntitySec').attr('disabled', true);
                        $('#modifyEntity_cronMsg').removeClass('hide');
                    } else {
                        $("#modifyEntity").attr('usedFlag', '0'); // 未被引用
                        $('#variableAliasEntitySec').removeAttr('disabled');
                        $('#modifyEntity_cronMsg').addClass('hide');
                    }
                }
            });
            $("#modifyEntity").modal({});
        } else if (type === 1) {
            for (var key in row) {
                var target = $("#objParamViewAlertModal .form-control[col-name='" + key + "']");
                if (target.length > 0) {
                    $(target).val(row[key]);
                }
            }
            if (row.kindId === 'K4') {
                $('#objParamViewAlertModal .defaultValueGroup').removeClass('hide');
            } else {
                $('#objParamViewAlertModal .defaultValueGroup').addClass('hide');
            }
            $("#objParamViewAlertModal").modal({});
            $('.closeObjParam').unbind().click(function () {
                $("#entityProp").modal({});
            });
        }
    },
    hide: function () {
        $("#modifyEntity").modal("hide");
    },
    // 验证变量状态并警告
    verify: function () {
        var usedFlag = $("#modifyEntity").attr('usedFlag');
        if (usedFlag == '0') {
            entityModifyModal.submit();
        } else if (usedFlag == '1') {
            // 验证未通过则警告
            confirmAlert.show(
                '参数被引用中, 是否仍要修改？',
                function () {
                    entityModifyModal.submit();
                });
        } else {
            return;
        }

        // $.ajax({ // 验证是否在用
        //     url: webpath + '/variable/checkUsed',
        //     type: 'POST',
        //     dataType: "json",
        //     data: {"variableId": $("#entityVariableIdSec").val()},
        //     success: function (data) {
        //         if (data.status === 0) {
        //             entityModifyModal.submit();
        //         } else {
        //             // 验证未通过则警告
        //             confirmAlert.show(
        //                 data.msg + ', 是否仍要修改？',
        //                 function () {
        //                     entityModifyModal.submit();
        //                 });
        //         }
        //     }
        // });
    },
    submit: function () {
        if ($("#modifyEntityForm").isValid()) {
            //修改变量
            var obj = {};
            obj.folderId = $.trim($("#ruleFolderSel").val());
            obj.variableId = $("#entityVariableIdSec").val();
            obj.variableAlias = $("#variableAliasEntitySec").val();
            obj.variableCode = $("#entityVariableCodeSec").val();
            //变量类型
            obj.typeId = rtEntitySelSec.getValue();
            obj.kindId = kindSel.getValue();
            if (variableTypeOri == "3" && variableTypeOri != obj.typeId) {
                failedMessager.show('对象类型不允许修改');
                return;
            }
            //变量描述
            obj.variableRemarks = $("#entityVariableRemarksSec").val();
            // obj.entityVariableAlias = $("#entityVariableAliasSec").val();
            obj.entityVariableAlias = $("#entityVariableCodeSec").val(); // 对象类型参数编码别名值与参数编码一致, 页面不展示填写该字段
            obj.entityPid = $("#entityPid").val();

            // $.ajax({ // 验证是否在用
            //     url: webpath + '/variable/checkUsed',
            //     type: 'POST',
            //     dataType: "json",
            //     data: {"variableId": $("#entityVariableIdSec").val()},
            //     success: function (data) {
            //         if (data.status == 0) { // 验证通过直接修改

            $.ajax({
                url: webpath + "/variable/updateVariableRelation",
                data: obj,
                type: "post",
                dataType: "json",
                success: function (data) {
                    if (data.status == 0) {
                        entityModifyModal.hide();
                        successMessager.show('修改成功');
                        //					refreshDataTableEntity();
                    } else {
                        // showerrorvalidator("modifyEntityForm", "entityVariableAliasSec", data.msg);
                        failedMessager.show('修改失败' + data.msg);
                    }
                }
            });
            //         } else {
            //             // 验证未通过
            //             failedMessager.show('修改失败，' + data.msg);
            //         }
            //     }
            // });

        }
    }
}

var deleteEntityModal = {
    show: function (variableId, entityId) {
        entityModal.hide();
        $("#delEntityVariableDiv").modal({});
        $("#entityVariableIdSec").val(variableId);
        $("#entityIdSec").val(entityId);
    },
    //判断变量是否可删除,如果规则中已使用则不能删除
    isDeleteEntity: function () {
        var data = {};
        data.variableId = $("#entityVariableIdSec").val();
        $.ajax({
            url: webpath + "/variable/isDeleteEntity",
            data: data,
            type: "post",
            dataType: "json",
            success: function (data) {
                if (data.status == 0) {
                    deleteEntityModal.submit();
                } else {
                    failedMessager.show('有规则在使用,不能删除');
                    $("#delEntityVariableDiv").modal('hide');
                }
            }
        });
    },
    submit: function () {
        //删除规则
        var obj = {};
        //变量别名
        obj.variableId = $("#entityVariableIdSec").val();
        obj.entityId = $("#entityIdSec").val();
        $.ajax({ // 验证是否可删
            url: webpath + '/variable/checkUsed',
            type: 'POST',
            dataType: "json",
            data: {"variableId": $("#entityVariableIdSec").val()},
            success: function (data) {
                if (data.status == 0) { // 可删
                    $.ajax({
                        url: webpath + "/variable/deleteVariableRelation",
                        data: obj,
                        type: "post",
                        dataType: "json",
                        success: function (data) {
                            if (data.status == 0) {
                                $("#delEntityVariableDiv").modal('hide');
                                successMessager.show('删除成功');
                            } else {
                                failedMessager.show('删除失败');
                                $("#delEntityVariableDiv").modal('hide');
                            }
                        }
                    });
                } else {
                    failedMessager.show('删除失败，' + data.msg);
                    $("#delEntityVariableDiv").modal('hide');
                }
            }
        });
    }
}

function bindEntityMoal() {
    $('#entityProp').on('shown.zui.modal', function () {
        entityDataTable.init();
    })
    $('#entityProp').on('hidden.zui.modal', function () {
//		refreshDataTableEntity();
//		  entityIds.splice(0,entityIds.length);
    })
    $('#newEntityValModal').on('hidden.zui.modal', function () {
        entityModal.show();
        $("#newEntityValForm").validator("cleanUp");
//		entityDataTable.refresh();
    });
    $('#modifyEntity').on('hidden.zui.modal', function () {
        entityModal.show();
        $("#modifyEntityForm").validator("cleanUp");
//		entityDataTable.refresh();
    });
    $('#delEntityVariableDiv').on('hidden.zui.modal', function () {
        entityModal.show();
//		entityDataTable.refresh();
    });
}

$(function () {
    //文档加载完成初始化
    initEntitySelect();
    bindEntityMoal();
});
