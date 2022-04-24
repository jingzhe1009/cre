/**
 * 场景下公共接口/公共参数列表页面
 * data:2019/08/06
 * author:bambi
 */

// 初始化页面
function initFolderParamPage() {
    folderParamTable.initParamGroup();
    folderParamTable.initParamType();
    folderParamTable.bindKindSel();

    // 参数属性弹框 完成 按钮绑定事件
    $('#folder_closeObjectParamAlert').click(function () {
        var parentIdArrStr = $('#folder_objectParamAlertModal').attr('fatherEntityIdArr');
        if (parentIdArrStr) {
            var parentIdArr = JSON.parse(parentIdArrStr);
            if (parentIdArr.length > 0) { // 不是最外层则加载上一个
                var fatherEntityNameArr = JSON.parse($('#folder_objectParamAlertModal').attr('fatherEntityNameArr'));

                var lastId = parentIdArr[parentIdArr.length - 1]; // 取上一个id
                var lastName = fatherEntityNameArr[fatherEntityNameArr.length - 1];

                parentIdArr.splice(parentIdArr.length - 1, 1); // 删掉最后一个
                fatherEntityNameArr.splice(fatherEntityNameArr.length - 1, 1); // 删掉最后一个

                // 重新记录
                $('#folder_objectParamAlertModal').attr('fatherEntityIdArr', JSON.stringify(parentIdArr))
                    .attr('fatherEntityNameArr', JSON.stringify(fatherEntityNameArr));

                // 加载回上一个列表, 回显id和name
                $('#folder_objectParamAlertModal').attr('entityId', lastId);
                $('#folder_objectParamAlertModal .modal-title span').text('').text(lastName);
                initFolderObjectPropertyTable(lastId);

            } else { // 是最外层的则直接关闭弹框
                $('#folder_objectParamAlertModal').modal('toggle', 'center');
            }
        } else { // 初始化默认直接关闭弹框
            $('#folder_objectParamAlertModal').modal('toggle', 'center');
        }
    });

    // 参数属性弹框 关闭 按钮绑定事件
    $('#folder_closeAllObjectParamAlert').click(function () {
        $('#folder_objectParamAlertModal').removeAttr('fatherEntityIdArr fatherEntityNameArr entityId');
        $('#folder_objectParamAlertModal').modal('toggle', 'center');
    });

    // 搜索
    $('#folder_paramSearch').click(function () {
        var inputs = $('#folder_commonParamContainer .folderParamSearch .input-group .form-control');
        var obj = {};
        for (var i = 0; i < inputs.length; i++) {
            if ($.trim($(inputs[i]).val()) === '') {
                continue;
            }
            obj[$(inputs[i]).attr('data-col')] = $.trim($(inputs[i]).val());
        }
        if (obj.variableTypeId) {
            obj.variableTypeId = $('#folder_commonParamContainer .commonParams_typeInput').attr('typeId');
        }
        if (obj.kindId) {
            obj.kindId = $('#folder_commonParamContainer .commonParams_kindInput').attr('kindId');
        }
        initCommonParamTable(obj);
    });
}

var folderParamTable = {
    // 初始化参数组下拉框
    initParamGroup: function () {
        $.ajax({
            url: webpath + '/variable/pub/group/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.data.length > 0) {
                    var data = data.data;
                    var htmlStr_search = '';
                    for (var i = 0; i < data.length; i++) {
                        htmlStr_search += '<li group-id=\'' + data[i].variableGroupId + '\'><a>' + data[i].variableGroupName + '</a></li>';
                    }
                    $('#folder_paramGroupSelector').empty().html(htmlStr_search); // 资源表搜索栏
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            },
            complete: function () {
                // 绑定事件
                $('#folder_paramGroupSelector>li').unbind('click').on('click', function () {
                    $(this).parent().siblings('.form-control').val($(this).first().text());
                });
            }
        });
    },
    // 初始化参数类型下拉框
    initParamType: function () {
        $.ajax({
            url: webpath + '/variable/variableType/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.data.length > 0) {
                    var dataArr = data.data;
                    var htmlStr = '';
                    for (var i = 0; i < dataArr.length; i++) {
                        htmlStr += '<li type-id=\'' + dataArr[i].key + '\'><a>' + dataArr[i].text + '</a></li>';
                    }
                    $('#folder_commonParamContainer .commonParams_typeList').empty().html(htmlStr);
                }
            },
            complete: function () {
                // 绑定事件
                $('#folder_commonParamContainer .commonParams_typeList>li').unbind('click').on('click', function () {
                    var input = $(this).parent().siblings('.form-control');
                    input.val($(this).first().text());
                    input.attr('typeId', $(this).attr('type-id'));
                });
            }
        });
    },
    // 参数种类选择绑定
    bindKindSel: function () {
        $('#folder_commonParamContainer .commonParams_kindList>li').unbind('click').on('click', function () {
            var input = $(this).parent().siblings('.form-control');
            input.val($(this).first().text());
            input.attr('kindId', $(this).attr('kindId'));
        });
    },
    // 查看对象参数属性
    objParamDetail: function (entityId, entityName, isNested) {
        if (isNested) {
            // 是否为对象内的对象参数, 是则将父entity缓存下来(id & name)
            var fatherEntityIdArr = [];
            var fatherEntityNameArr = [];
            if ($('#folder_objectParamAlertModal').attr('fatherEntityIdArr')) {
                fatherEntityIdArr = JSON.parse($('#folder_objectParamAlertModal').attr('fatherEntityIdArr'));
            }
            if ($('#folder_objectParamAlertModal').attr('fatherEntityNameArr')) {
                fatherEntityNameArr = JSON.parse($('#folder_objectParamAlertModal').attr('fatherEntityNameArr'));
            }
            var fatherEntityId = $('#folder_objectParamAlertModal').attr('entityId');
            var fatherEntityName = $('#folder_objectParamAlertModal .modal-title span').text();
            fatherEntityIdArr.push(fatherEntityId);
            fatherEntityNameArr.push(fatherEntityName);
            $('#folder_objectParamAlertModal').attr('fatherEntityIdArr', JSON.stringify(fatherEntityIdArr)).attr('fatherEntityNameArr', JSON.stringify(fatherEntityNameArr));
        }

        $('#folder_objectParamAlertModal').attr('entityId', entityId); // 在弹框上记录entityId
        $('#folder_objectParamAlertModal .modal-title span').text('').text(entityName);
        initFolderObjectPropertyTable(entityId); // 初始化table
        $('#folder_objectParamAlertModal').modal({'show': 'center', "backdrop": "static"}); // 展示弹框
    }
}

/* 初始化公共接口table obj:搜索条件 */
function initCommonParamTable(obj) {
    obj == null ? '' : obj;
    $('#folder_paramTable').width('100%').dataTable({
        "searching": false, // 是否开启搜索功能
        "ordering": false,
        "destroy": true,
        "bLengthChange": false,
        "pagingType": "full_numbers",
        "paging": true,
        "info": true,
        "serverSide": true,
        "pageLength": 10,
        "columns": [
            {"title": "参数组", "data": "variableGroupName"},
            {"title": "变量别名", "data": "variableAlias"},
            {"title": "变量类型", "data": "typeValue"},
            {"title": "变量种类", "data": "kindValue"},
            {"title": "描述", "data": "variableRemarks"},
            {"title": "创建人", "data": "createPerson"},
            {"title": "创建时间", "data": "createDate"},
            {
                "title": "操作", "data": null, "render": function (data, type, row) {
                    var htmlStr = "";
                    var isObject = (row.typeId == '3') ? true : false;
                    var entityId = '';
                    var entityName = '';
                    if (isObject) {
                        entityId = row.entityId;
                        entityName = row.variableAlias;
                        htmlStr += '<span type="button" class="cm-tblB detailBtn" onclick="folderParamTable.objParamDetail(\'' + entityId + '\', \'' + entityName + '\', false)">属性</span>';
                    }

                    return htmlStr;
                }
            }
        ],
        ajax: {
            url: webpath + '/variable/pub/list',
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
}

// 参数: 对象类型参数-属性表格
function initFolderObjectPropertyTable(entityId) {
    $('#folder_objParamsTable').width('100%').dataTable({
        "searching": false, // 是否开启搜索功能
        "ordering": false,
        "destroy": true,
        "bLengthChange": false,
        // "pagingType": "full_numbers",
        "paging": true,
        "info": true,
        // "serverSide": true,
        "pageLength": 10,
        "columns": [
            {"title": "变量别名", "data": "variableAlias"},
            {"title": "变量编码", "data": "variableCode"},
            // {"title": "编码别名", "data": "entityVariableAlias"},
            {"title": "变量类型", "data": "typeValue"},
            {"title": "变量种类", "data": "kindValue"},
            {"title": "默认值", "data": "defaultValue"},
            {
                "title": "操作", "data": null, "render": function (data, type, row) {
                    var htmlStr = "";
                    var varId = row.variableId;
                    var isObject = (row.typeId == '3') ? true : false;
                    var entityId = '';
                    var entityName = '';
                    if (isObject) {
                        entityId = row.entityId;
                        entityName = row.variableAlias;
                        htmlStr += '<span type="button" class="cm-tblB detailBtn" onclick="folderParamTable.objParamDetail(\'' + entityId + '\', \'' + entityName + '\', true)">属性</span>';
                    }
                    return htmlStr;
                }
            }
        ],
        ajax: {
            url: webpath + '/variable/queryVariableRelation',
            "type": 'GET',
            "data": function (d) { // 查询参数
                return $.extend({}, d, {'entityId': entityId});
            }
        },
        "fnDrawCallback": function (oSettings, json) {
            // $("tr:even").css("background-color", "#fbfbfd");
            // $("table:eq(0) th").css("background-color", "#f6f7fb");
        }
    });
}


$(function () {
    initCommonParamTable(); // 初始化表格
    initFolderParamPage();
});