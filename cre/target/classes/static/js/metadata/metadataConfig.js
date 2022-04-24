/**
 *  列信息表格
 * data:2019/05/23
 * author:bambi
 */

var packageId = $('#packageId').prop('value');
var tableId = $('#tableId').prop('value');
var colsTable = {
    // 输入列名搜索
    search: function () {
        initTable($.trim($('#searchInput').val()));
    },
    // 表格全选/全不选
    selector: function () {
        var flag = $('#metadataConfig_selector').attr('is-check') === '0' ? true : false;
        if (flag) { // 全选
            $('#tableColsTable .checkbox input').prop('checked', 'checked');
            $('#metadataConfig_selector').attr('is-check', '1');
        } else { // 全不选
            $('#tableColsTable .checkbox input').removeAttr('checked');
            $('#metadataConfig_selector').attr('is-check', '0');
        }
    },
    // 删除列
    delCol: function (colId) {
        $('#msgText').text('确认删除？');
        $('#msgAlertModal').modal('toggle', 'center'); // 弹出确认弹框
        $('#msgConfirm').click(function () {
            $.ajax({
                url: webpath + "/batchdata/column/deleteColumn",
                type: 'POST',
                data: {'columnId': colId, 'tableId': $.trim($("#tableId").val())},
                dataType: "json",
                success: function (data) {
                    if (data.status == '0') {
                        successMessager.show('删除成功！');
                        initTable();
                    } else {
                        failedMessager.show(data.msg);
                    }
                },
                error: function (data) {
                    failedMessager.show(data.msg);
                }
            });
            $('#msgConfirm').unbind('click'); // 清除confirm绑定事件, 避免复用有问题
        });

    },
    // 编辑保存列信息
    editCol: function (colId) {
        if ($.trim($('#colNameInput').val()) != '') {
            if ($.trim($('#colCodeInput').val()) != '') {
                if ($.trim($('#colSizeInput').val()) != '') {
                    var obj = colAlertModal.getContentObj();
                    obj['columnId'] = colId;
                    $.ajax({
                        url: webpath + "/batchdata/column/updateColumn",
                        type: 'POST',
                        data: obj,
                        dataType: "json",
                        success: function (data) {
                            if (data.status == '0') {
                                successMessager.show('修改成功！');
                                colAlertModal.hidden();
                                initTable();
                            } else {
                                failedMessager.show('修改失败！' + data.msg);
                            }
                        },
                        error: function (data) {
                            failedMessager.show(data.msg);
                        }
                    });
                } else {
                    failedMessager.show('列大小不能为空');
                    $('#colSizeInput').focus();
                    return;
                }
            } else {
                failedMessager.show('列编码不能为空！');
                $('#colCodeInput').focus();
                return;
            }
        } else {
            failedMessager.show('列表名不能为空！');
            $('#colNameInput').focus();
            return;
        }
    },
    // 批量删除列
    delCols: function () {
        // 获取所有选中的列
        var checkedArr = $('#tableColsTable tbody .checkbox input:checked');
        if (checkedArr.length < 1) {
            warningMessager.show('请选择要删除的列');
        } else {
            // 确认
            $('#msgText').text('确认删除？');
            $('#msgAlertModal').modal('toggle', 'center'); // 弹出确认弹框
            $('#msgConfirm').click(function () {
                var colIds = [];
                for (var i = 0; i < checkedArr.length; i++) {
                    colIds.push($(checkedArr[i]).attr('col-id'));
                }
                $.ajax({
                    url: webpath + "/metadata/deleteColumns",
                    type: 'POST',
                    data: {'columnIds': JSON.stringify(colIds), 'tableId': $.trim($("#tableId").val())},
                    dataType: "json",
                    success: function (data) {
                        if (data.status == '0') {
                            successMessager.show('删除成功！');
                            initTable();
                        } else {
                            failedMessager.show('删除失败！' + data.msg);
                        }
                    },
                    error: function (data) {
                        failedMessager.show(data.msg);
                    }
                });
                $('#msgConfirm').unbind('click'); // 清除confirm绑定事件, 避免复用有问题
            });

        }
    }
}

/**
 *  列展示弹框: 新建列/编辑列
 */
var colAlertModal = {
    // 展示弹框
    show: function (title) {
        $('#colAlertModal .modal-title').text(title);
        $('#colAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 隐藏弹框
    hidden: function () {
        $('#colAlertModal').modal('toggle', 'center');
        $('#colAlertModal form')[0].reset(); // 清空表单
    },
    // 获取表格内的所有数据内容
    getContentObj: function () {
        var obj = {};
        var inputArr = $('#colAlertModalForm input');
        for (var i = 0; i < inputArr.length; i++) {
            var data = $(inputArr[i]);
            // if ($.trim(data) != '') {
            obj[data.attr('col-name')] = data.val();
            // }
        }
        var selectArr = $('#colAlertModalForm select');
        for (var i = 0; i < selectArr.length; i++) {
            var data = $(selectArr[i]).children("option:selected")[0];
            var colName = $(selectArr[i]).attr('col-name');
            obj[colName] = $(data).attr('data-value');
        }
        obj['tableId'] = tableId;
        return obj;
    },
    // 新建列-保存
    save: function () {
        if ($.trim($('#colNameInput').val()) != '') {
            if ($.trim($('#colCodeInput').val()) != '') {
                if ($.trim($('#colSizeInput').val()) != '') {
                    $.ajax({
                        url: webpath + "/batchdata/column/insertColumn",
                        type: 'POST',
                        data: colAlertModal.getContentObj(),
                        dataType: "json",
                        success: function (data) {
                            if (data.status == '0') {
                                successMessager.show('添加成功！');
                                colAlertModal.hidden();
                                initTable();
                            } else {
                                failedMessager.show('添加失败！' + data.msg);
                            }
                        },
                        error: function (data) {
                            failedMessager.show(data.msg);
                        }
                    });
                } else {
                    failedMessager.show('列大小不能为空！')
                    $('#colSizeInput').focus();
                    return;
                }
            } else {
                failedMessager.show('列编码不能为空！');
                $('#colCodeInput').focus();
                return;
            }
        } else {
            failedMessager.show('列表名不能为空！');
            $('#colNameInput').focus();
            return;
        }
    },
    // 表格内--编辑列弹出列信息
    getColInfo: function (columnId) {
        // 查看列信息并填入表格
        $.ajax({
            url: webpath + "/batchdata/column/columnInfo",
            type: 'POST',
            data: {'columnId': columnId},
            dataType: "json",
            success: function (data) {
                $('#colNameInput').val(data.columnName);
                $('#colCodeInput').val(data.columnCode);
                $('#colSizeInput').val(data.columnSize);
                $($("#colTypeFormGroup option[data-value= '" + data.columnType + "']")).prop('selected', true);
                $($("#isPkFormGroup option[data-value= '" + data.isPk + "']")).prop('selected', true);
                $($("#isNullFormGroup option[data-value= '" + data.isNull + "']")).prop('selected', true);
                $('#col_saveButton').attr('col-id', columnId);
            },
            error: function (data) {
                failedMessager.show(data.msg);
            }
        });
        colAlertModal.show('修改列信息');
    }
}

/**
 *  返回跳转
 */
function returnPage() {
    var url = webpath + "/metadata/view?packageId=" + packageId;
    creCommon.loadHtml(url);
}

/**
 *  初始化变量类型下拉框
 */
function initBaseVariableType() {
    var htmlStr = '';
    for (var i = 0; i < baseVariableType.length; i++) {
        if (i == 0) {
            htmlStr += '<option data-value=\'' + baseVariableType[i].key + '\' selected>' + baseVariableType[i].text + '</option>';
        } else {
            htmlStr += '<option data-value=\'' + baseVariableType[i].key + '\'>' + baseVariableType[i].text + '</option>';
        }
    }
    $('#colTypeFormGroup select').html(htmlStr);
}

function initPage() {
    /* 添加列按钮 */
    $('#addColButton').click(function () {
        // 新建列权限 authCheck
        $.ajax({
            url: webpath + '/batchdata/column/save/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {"tableId": $.trim($("#tableId").val())},
            success: function (data) {
                if (data.status == 0) {
                    $('#col_saveButton').attr('operate-type', '0'); // 修改保存按钮标识
                    colAlertModal.show('新建列');
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    });

    /* 保存新建列/列修改 保存按钮 */
    $('#col_saveButton').click(function () {
        if ($(this).attr('operate-type') === '0') { // 新建保存
            colAlertModal.save();
        } else if ($(this).attr('operate-type') === '1') { // 修改保存
            colsTable.editCol($(this).attr('col-id'));
        }
    });

    /* 批量删除 */
    $('#colsDelButton').click(function () {
        colsTable.delCols();
    });

    /* 输入列名搜索 */
    $('#metadataConfig_searchBtn').click(function () {
        colsTable.search();
    });

    /* 表格全选/全不选 */
    $('#tableColsTable').on('click', '#metadataConfig_selector', function () {
        colsTable.selector();
    });

    /* 列删除事件 */
    $('#tableColsTable').on('click', '.metadataConfig_delSpan', function () {
        colsTable.delCol($(this).attr('col-id'));
    });

    /* 列编辑事件 */
    $('#tableColsTable').on('click', '.metadataConfig_editSpan', function () {
        $('#col_saveButton').attr('operate-type', '1'); // 修改保存按钮标识
        colAlertModal.getColInfo($(this).attr('col-id'));
    });
}

/**
 *  加载表字段表格
 */
function initTable(obj) {
    var columnName = ($.trim(obj) == '') ? '' : $.trim(obj);
    $('#tableColsTable').width('100%').dataTable({
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
            {
                "title": function () {
                    var htmlStr = '';
                    htmlStr += '<div id="metadataConfig_selector" is-check="0" class="checkbox"><label><input type="checkbox"></label></div>';
                    return htmlStr;
                },
                "data": "COLUMNID",
                "render": function (data) {
                    var htmlStr = '';
                    htmlStr += '<div class="checkbox"><label><input col-id=\'' + data + '\' type="checkbox"></label></div>';
                    return htmlStr;
                }
            },
            {"title": "列名", "data": "COLUMNNAME"},
            {"title": "列编码", "data": "COLUMNCODE"},
            {"title": "列类型", "data": "COLUMNTYPE"},
            {"title": "列大小", "data": "COLUMNSIZE"},
            {
                "title": "是否主键", "data": "ISPK", "render": function (data) {
                    switch ($.trim(data)) {
                        case '0':
                            return '否';
                        case '1':
                            return '是';
                        default:
                            return '--';
                    }
                }
            },
            {
                "title": "是否可为空", "data": "ISNULL", "render": function (data) {
                    switch ($.trim(data)) {
                        case '0':
                            return '否';
                        case '1':
                            return '是';
                        default:
                            return '--';
                    }
                }
            },
            {
                "title": "操作", "data": "COLUMNID", "render": function (data) {
                    var htmlStr = "";
                    htmlStr += '<span col-id=\'' + data + '\' class="cm-tblB metadataConfig_editSpan" type="button">修改</span>';
                    htmlStr += '<span col-id=\'' + data + '\' class="cm-tblC metadataConfig_delSpan" type="button">删除</span>';
                    return htmlStr;
                }
            }
        ],
        ajax: {
            url: webpath + '/batchdata/column/columnList',
            "type": 'GET',
            "data": function (d) { // 查询参数
                return $.extend({}, d, {
                    'tableId': $.trim($("#tableId").val()),
                    'columnName': columnName
                });
            }
        },
        "fnDrawCallback": function (oSettings, json) {
            // $("tr:even").css("background-color", "#fbfbfd");
            // $("table:eq(0) th").css("background-color", "#f6f7fb");
        }
    });
}

$(function () {
    // // 扫描添加的表不允许新建列操作
    // if (scanId != '' && scanId != 'undefined') {
    //     $('#addColButton').css('display', 'none');
    // }
    // 输出表不允许新建列操作
    if (scanId != '' && scanId != 'undefined') {
        if (scanId == '1') {
            $('#addColButton').css('display', 'inline-block');
        }
    }
    initTable();
    initBaseVariableType();
    initPage();
});