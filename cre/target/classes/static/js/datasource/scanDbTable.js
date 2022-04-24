/**
 * 数据源-元数据管理-扫描
 * data:2019/10/18
 * author:bambi
 */

var scanDbTableModal = {
    // 初始化页面内容
    initPage: function (dbId) {
        if (!dbId) {
            failedMessager.show('参数[dbId]无效，加载失败');
            return;
        }
        getAllDataSource(dbId);

        // 确认扫描
        $('#scanConfirm').unbind().click(function () {
            scanDbTableModal.scanConfirm();
        });

        // 全选列
        $('#allSelectButton').unbind().click(function () {
            scanDbTableModal.selector(1);
        });

        // 全不选
        $('#allCancelButton').unbind().click(function () {
            scanDbTableModal.selector(0);
        });

        // 扫描选择列保存至表
        $('#scanSaveToTableButton').unbind().click(function () {
            scanDbTableModal.saveToMetadata(dbId);
        });

        // 取消扫描
        $('#scanCancelButton').unbind().click(function () {
            scanDbTableModal.cancelScan();
        });

        // 搜索
        $('#colsSearchBtn').unbind().click(function () {
            scanDbTableModal.searchCols();
        });

        $('#colsSearchInput').unbind().keydown(function () {
            scanDbTableModal.searchCols();
        });

        scanDbTableModal.show();
    },
    // 显示弹框
    show: function () {
        $('#dsModalForm')[0].reset(); // 清空表单数据
        $('#scanModal').modal({'show': 'center', "backdrop": "static"});
        $('#scanModal').unbind('shown.zui.modal');
        $('#scanModal').on('shown.zui.modal', function () {
            $('#scanDatasourceList option').css('display', 'block');
        });
    },
    // 数据源表扫描
    scanConfirm: function () {
        $('#colsListContainer').html(''); // 先清空列展示内容
        var datasourceId = $('#scanDatasourceList option:selected').attr('datasource-id');
        var tableName = $('#scanTables option:selected').val();
        if (!datasourceId || !tableName) {
            failedMessager.show('信息无效，扫描失败！');
            return;
        }
        $.ajax({
            url: webpath + '/datasource/metadata/scan',
            type: 'POST',
            dataType: "json",
            data: {dbId: datasourceId, scanKey: tableName,},
            beforeSend: function () {
                loading.show();
            },
            success: function (data) {
                var status = data.status;
                if (status === 0) { // 扫描成功
                    var colsStr = '';
                    var columnsArr = data.data.columns;
                    for (var i = 0; i < columnsArr.length; i++) {
                        colsStr += '<div class="checkbox checkbox_mainPage"><label><input colId=\'' + columnsArr[i].COLUMNID + '\'  class="checkInput" type="checkbox" style="margin-right: 5px">' + columnsArr[i].COLUMNNAME + '</label></div>';
                    }
                    $('#colsListContainer').html(colsStr);
                    $('#colsListContainer').attr('scanId', data.data.scanId);
                    $('#scanModal').modal('hide'); // 关闭扫描弹框
                    $('#scanColsModal').modal({'show': 'center', "backdrop": "static"}); // 弹出列展示选择弹框
                    $( '#colsListContainer input[type="checkbox"]' ).css( 'display', 'inline-block' );
                } else {
                    warningMessager.show(data.msg);
                }
            },
            complete: function () {
                loading.hide();
            }
        });
    },
    // 扫描选取字段并确认保存
    saveToMetadata: function (dbId) {
        var scanId = $('#colsListContainer').attr('scanId');
        var checkedArr = $('#colsListContainer .checkInput:checked');
        if (checkedArr.length > 0) {
            var columnIds = [];
            for (var i = 0; i < checkedArr.length; i++) {
                columnIds.push($.trim($(checkedArr[i]).attr('colId')));
            }
            var cols = JSON.stringify(columnIds);
            $.ajax({
                url: webpath + '/datasource/metadata/scan/save',
                type: 'POST',
                dataType: "json",
                data: {
                    'scanId': scanId,
                    'columnIds': cols
                },
                beforeSend: function () {
                    loading.show();
                },
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('保存成功！');
                        initDbScanTable({'dbId': dbId});
                        $('#scanColsModal').modal('hide'); // 关闭列展示选择弹框
                    } else {
                        failedMessager.show(data.msg);
                    }
                },
                complete: function () {
                    loading.hide();
                }
            });
        } else {
            failedMessager.show('请选择列！');
        }
    },
    // 取消扫描
    cancelScan: function () {
        $.ajax({
            url: webpath + '/datasource/metadata/scan/cancel',
            type: 'POST',
            dataType: "json",
            data: {
                'scanId': $('#colsListContainer').attr('scanId')
            },
            beforeSend: function () {
                loading.show();
            },
            success: function (data) {
                if (data.status === 0) {
                    $('#scanColsModal').modal('hide'); // 关闭列展示选择弹框
                } else {
                    failedMessager.show(data.msg);
                }
            },
            complete: function () {
                loading.hide();
            }
        });
    },
    // 1 全选 / 0 全不选
    selector: function (type) {
        if (type == '1') {
            $('#colsListContainer .checkInput').prop('checked', 'checked');
        } else if (type == '0') {
            $('#colsListContainer .checkInput').removeAttr('checked');
        }
    },
    //  搜索列
    searchCols: function () {
        var input = $.trim($('#colsSearchInput').val());
        if (input === '') {
            $('#colsListContainer .checkbox_mainPage').show();
        } else {
            $('#colsListContainer .checkbox_mainPage').hide().filter(':contains(' + input + ')').show();
        }
    }
}

// 获取所有数据源
function getAllDataSource(dbId) {
    $.ajax({
        url: webpath + '/datasource/dataSourceList',
        type: 'POST',
        dataType: "json",
        success: function (data) {
            var htmlStr = '<option>--请选择--</option>';
            for (var i = 0; i < data.length; i++) {
                var isHiveDb = (data[i].DBTYPEID == '5' || data[i].DBTYPEID == '6') ? '1' : '0';  // hive类型数据库特殊标识
                // 回显数据源
                if (data[i].DBID === dbId) {
                    htmlStr += '<option selected datasource-id=\'' + data[i].DBID + '\' isHiveDb=\'' + isHiveDb + '\'>' + data[i].DBALIAS + '</option>';
                } else {
                    htmlStr += '<option datasource-id=\'' + data[i].DBID + '\' isHiveDb=\'' + isHiveDb + '\'>' + data[i].DBALIAS + '</option>';
                }
            }
            $('.datasourceList').html(htmlStr);

            // change事件绑定
            $('#scanDatasourceList').unbind().on('change', function () {
                getTablesById($('#scanDatasourceList option:selected').attr('datasource-id'));
            });

            $("#scanDatasourceList").trigger('change');
        }
    });
}

// 根据数据源id获取其下所有tables
function getTablesById(datasourceId) {
    if (!datasourceId) {
        $('#scanTables').html('');
        return;
    }
    $.ajax({
        url: webpath + '/datasource/getDbTables',
        type: 'POST',
        dataType: "json",
        data: {dbId: datasourceId},
        beforeSend: function () {
            loading.show();
        },
        success: function (data) {
            var htmlStr = '';
            for (var i = 0; i < data.length; i++) {
                htmlStr += '<option>' + data[i].tableName + '</option>';
            }
            $('#scanTables').html(htmlStr);
        },
        complete: function () {
            loading.hide();
        }
    });
}
