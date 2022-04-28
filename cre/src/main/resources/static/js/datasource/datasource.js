/**
 * 数据源管理
 * data:2019/05/16
 * author:bambi
 */


/**
 * 数据源管理
 */
var dataSourceModal = {
    // 修改数据源
    editDataSource: function (dbId) {
        // 修改数据源权限 authCheck
        $.ajax({
            url: webpath + '/datasource/update/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {dbId: dbId},
            success: function (data) {
                if (data.status === 0) {
                    $('#datasourceModalForm')[0].reset(); // 清空表单数据
                    $('#dsAlertModal .form-control').removeAttr('disabled');
                    $('#dsAlertModal .modal-footer .notView button').css('display', 'inline-block');
                    $('#closeViewDs').css('display', 'none');
                    // 复原hive选填项
                    $('#datasourceModalForm').find("input[id='dbUsername']").attr('isMust', '1').parent().siblings('label').find('.mustIcon').css('display', 'inline');
                    $('#datasourceModalForm').find("input[id='dbPassword']").attr('isMust', '1').parent().siblings('label').find('.mustIcon').css('display', 'inline');
                    // hbase 还原
                    $('#datasourceModalForm .maxConnectGroup, #datasourceModalForm .maxIdleGroup, #datasourceModalForm .dbUsernameGroup,  #datasourceModalForm .dbPasswordGroup').removeClass('hide');
                    $('#dsAlertTitle').text('修改数据源'); // 修改表单title
                    $.ajax({
                        url: webpath + "/datasource/selectByPrimaryKey",
                        type: 'POST',
                        data: {'dbId': dbId},
                        dataType: "json",
                        success: function (data) {
                            // 数据对应至表单内
                            $('#dsAlertModal').find('#dbAlias').val(data.dbAlias);
                            $('#dsAlertModal').find('#dbIp').val(data.dbIp);
                            $('#dsAlertModal').find('#dbPort').val(data.dbPort);
                            $('#dsAlertModal').find('#dbServiceName').val(data.dbServiceName);
                            $('#dsAlertModal').find('#maxConnect').val(data.maxConnect);
                            $('#dsAlertModal').find('#maxIdle').val(data.maxIdle);
                            $('#dsAlertModal').find('#dbUsername').val(data.dbUsername);
                            $('#dsAlertModal').find('#dbPassword').val(data.dbPassword);
                            $("#dbTypeFormGroup option[dbType='" + data.dbType + "']").prop('selected', true);
                            $("#isPoolFormGroup option[isPool='" + data.isPool + "']").prop('selected', true);
                            $('#dsTestButton').attr('data-id', data.dbId); // 给测试按钮添加data-id属性赋值dbId
                            $('#dsSaveButton').attr('data-id', data.dbId);

                            // hive 用户名密码选填
                            if (data.dbType === '5' || data.dbType === '8') {
                                $('#datasourceModalForm').find("input[id='dbUsername']").attr('isMust', '0').parent().siblings('label').find('.mustIcon').css('display', 'none');
                                $('#datasourceModalForm').find("input[id='dbPassword']").attr('isMust', '0').parent().siblings('label').find('.mustIcon').css('display', 'none');
                            }
                            if (data.dbType === '8') {
                                $('#datasourceModalForm .maxConnectGroup, #datasourceModalForm .maxIdleGroup, #datasourceModalForm .dbUsernameGroup,  #datasourceModalForm .dbPasswordGroup').addClass('hide');
                            }

                            $('#dbType').trigger('change');

                            // 弹出弹框
                            $('#dsAlertModal').modal('toggle', 'center');
                        }
                    });
                    // 编辑更新
                    $('#dsSaveButton').unbind('click');
                    $('#dsSaveButton').click(function () {
                        dsOperate('update', $(this).attr('data-id'));
                    });
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 查看数据源
    detailDatasource: function (dbId) {
        $('#datasourceModalForm')[0].reset(); // 清空表单数据
        $('#dsAlertModal .form-control').attr('disabled', true);
        $('#dsAlertModal .modal-footer .notView button').css('display', 'none');
        $('#closeViewDs').css('display', 'inline-block');
        // 复原hive选填项
        $('#datasourceModalForm').find("input[id='dbUsername']").attr('isMust', '1').parent().siblings('label').find('.mustIcon').css('display', 'inline');
        $('#datasourceModalForm').find("input[id='dbPassword']").attr('isMust', '1').parent().siblings('label').find('.mustIcon').css('display', 'inline');
        // 复原hbase隐藏项
        $('#datasourceModalForm .maxConnectGroup, #datasourceModalForm .maxIdleGroup, #datasourceModalForm .dbUsernameGroup,  #datasourceModalForm .dbPasswordGroup').removeClass('hide');
        $('#dsAlertTitle').text('查看数据源'); // 修改表单title

        $.ajax({
            url: webpath + "/datasource/selectByPrimaryKey",
            type: 'POST',
            data: {'dbId': dbId},
            dataType: "json",
            success: function (data) {
                // 数据对应至表单内
                $('#dsAlertModal').find('#dbAlias').val(data.dbAlias);
                $('#dsAlertModal').find('#dbIp').val(data.dbIp);
                $('#dsAlertModal').find('#dbPort').val(data.dbPort);
                $('#dsAlertModal').find('#dbServiceName').val(data.dbServiceName);
                $('#dsAlertModal').find('#maxConnect').val(data.maxConnect);
                $('#dsAlertModal').find('#maxIdle').val(data.maxIdle);
                $('#dsAlertModal').find('#dbUsername').val(data.dbUsername);
                $('#dsAlertModal').find('#dbPassword').val(data.dbPassword);
                $("#dbTypeFormGroup option[dbType='" + data.dbType + "']").prop('selected', true);
                $("#isPoolFormGroup option[isPool='" + data.isPool + "']").prop('selected', true);
                $('#dsTestButton').attr('data-id', data.dbId); // 给测试按钮添加data-id属性赋值dbId
                $('#dsSaveButton').attr('data-id', data.dbId);

                // hive 用户名密码选填
                if (data.dbType === '5' || data.dbType === '8') {
                    $('#datasourceModalForm').find("input[id='dbUsername']").attr('isMust', '0').parent().siblings('label').find('.mustIcon').css('display', 'none');
                    $('#datasourceModalForm').find("input[id='dbPassword']").attr('isMust', '0').parent().siblings('label').find('.mustIcon').css('display', 'none');
                }
                if (data.dbType === '8') {
                    $('#datasourceModalForm .maxConnectGroup, #datasourceModalForm .maxIdleGroup, #datasourceModalForm .dbUsernameGroup,  #datasourceModalForm .dbPasswordGroup').addClass('hide');
                }
                // 弹出弹框
                $('#dsAlertModal').modal('toggle', 'center');
            }
        });
        // 编辑更新
        $('#dsSaveButton').unbind('click');
        $('#dsSaveButton').click(function () {
            dsOperate('update', $(this).attr('data-id'));
        });
    },
    // 仅展示数据源管理内容
    showMgr: function () {
        $('.dsMetadataMgrWrap').addClass('hide');
        $('#datasourceContainer').removeClass('hide');
        $('#secondTitle').text('');
        $('#dsHeader').css('display', '');
    },
    // 仅展示元数据管理内容
    showMetadata: function () {
        $('#datasourceContainer, .dsMetadataMgrWrap').addClass('hide');
        $('.scanedTables').removeClass('hide');
        $('#secondTitle').text('>元数据管理');
        $('#dsHeader').css('display', 'none');
    },
    // 仅展示列管理
    showTableCols: function (tableName) {
        tableColsObj.checkedCols = [];
        $('#datasourceContainer, .dsMetadataMgrWrap').addClass('hide');
        $('.tableCols').removeClass('hide');
        $('#secondTitle').text('>元数据管理>' + tableName + '>表字段');
        $('#dsHeader').css('display', 'none');
    },
    // 元数据管理页面跳转
    metadataMgr: function (dbId) {
        if (dbId) {
            dataSourceModal.showMetadata();
            $('#scanedTablesDiv').removeAttr('dbId').attr('dbId', dbId);
            initDbScanTable({"dbId": dbId});
        }
    },
}

/**
 * 元数据管理
 */
var metadataObj = {
    // 删除元数据表
    delScanTable: function (tableId) {
        var auth = $('#scanedTablesDiv').attr('updateAuth');
        if (auth === '1') {
            if (tableId) {
                confirmAlert.show("确认删除该表？", function () {
                    $.ajax({
                        url: webpath + "/datasource/metadata/table/delete",
                        type: 'POST',
                        data: {'tableId': tableId},
                        dataType: "json",
                        success: function (data) {
                            if (data.status === 0) {
                                successMessager.show('删除成功！');
                                initDbScanTable({'dbId': $('#scanedTablesDiv').attr('dbId')});
                            } else {
                                failedMessager.show(data.msg);
                            }
                        }
                    });
                });
            }
        } else {
            failedMessager.show('没有权限！');
        }
    },
    // 列管理页面跳转
    colsConfig: function (tableId, tableName) {
        // var auth = $('#scanedTablesDiv').attr('updateAuth');
        // if (auth === '1') {
        if (tableId) {
            $('#tableColsDiv').removeAttr('tableId').attr('tableId', tableId);
            dataSourceModal.showTableCols(tableName);
            initTableCols({"tableId": tableId});
        }
        // } else {
        //     failedMessager.show('没有权限！');
        // }
    },
}

/**
 * 列管理
 */
var tableColsObj = {
    checkedCols: [],
    colTablePageSize: 10,
    // 新建列
    addCol: function () {
        $('#colAlertModal .modal-footer button').css('display', 'none');
        $('#colAlertModal .modal-footer .notView button').css('display', 'inline-block');
        // 新建列权限 authCheck
        $.ajax({
            url: webpath + '/batchdata/column/save/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {"tableId": $("#tableColsDiv").attr('tableId')},
            success: function (data) {
                if (data.status === 0) {
                    $('#col_saveButton').attr('operate-type', '0'); // 修改保存按钮标识 0新建 1修改 2查看
                    colAlertModal.show('新建列');
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 表格全选/全不选
    selector: function (status) {
        var colCheckBox = $('#tableColsTable .colCheckBox');
        colCheckBox.prop('checked', status);
        for (var i = 0; i < colCheckBox.length; i++) {
            tableColsObj.updateCache($(colCheckBox[i]).attr('col-id'), status);
        }
    },
    // 删除列
    delCol: function (colId, tableId) {
        if (tableId && colId) {
            confirmAlert.show("确认删除列？", function () {
                $.ajax({
                    url: webpath + "/batchdata/column/deleteColumn",
                    type: 'POST',
                    data: {'columnId': colId, 'tableId': tableId},
                    dataType: "json",
                    success: function (data) {
                        if (data.status === 0) {
                            successMessager.show('删除成功！');
                            initTableCols({"tableId": tableId});
                        } else {
                            failedMessager.show(data.msg);
                        }
                    }
                });
            });
        }
    },
    // 编辑保存列信息
    editCol: function (colId) {
        if (!$('#colAlertModalForm').isValid()) {
            return;
        }
        var obj = colAlertModal.getContentObj();
        obj['columnId'] = colId;
        $.ajax({
            url: webpath + "/batchdata/column/updateColumn",
            type: 'POST',
            data: obj,
            dataType: "json",
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('修改成功！');
                    colAlertModal.hidden();
                    initTableCols({"tableId": $('#tableColsDiv').attr('tableId')});
                } else {
                    failedMessager.show('修改失败！' + data.msg);
                }
            }
        });
    },
    // 批量删除列
    delCols: function () {
        // 获取所有选中的列
        // var checkedArr = $( '#tableColsTable tbody .checkbox input:checked' );
        // if ( checkedArr.length < 1 ) {
        // 	warningMessager.show( '请选择要删除的列' );
        // 	return;
        // }

        if (tableColsObj.checkedCols.length < 1) {
            warningMessager.show('请选择要删除的列');
            return;
        }
        confirmAlert.show("确认删除？", function () {
            // var colIds = [];
            // for ( var i = 0; i < checkedArr.length; i++ ) {
            // 	colIds.push( $( checkedArr[ i ] ).attr( 'col-id' ) );
            // }
            var tableId = $('#tableColsDiv').attr('tableId');
            $.ajax({
                url: webpath + "/metadata/deleteColumns",
                type: 'POST',
                // data: { 'columnIds': JSON.stringify( colIds ), 'tableId': tableId },
                data: {'columnIds': JSON.stringify(tableColsObj.checkedCols), 'tableId': tableId},
                dataType: "json",
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('删除成功！');
                        tableColsObj.checkAllCheckbox();
                        initTableCols({"tableId": tableId});
                    } else {
                        failedMessager.show('删除失败！' + data.msg);
                    }
                }
            });
        });
    },
    // 列选中更新缓存
    updateCache: function (colId, isCheck) {
        if (isCheck) { // 选中
            if (tableColsObj.checkedCols.indexOf(colId) === -1) {
                tableColsObj.checkedCols.push(colId);
            }
        } else { // 取消选中
            var index = tableColsObj.checkedCols.indexOf(colId);
            if (index !== -1) {
                tableColsObj.checkedCols.splice(index, 1);
            }
        }
        tableColsObj.checkAllCheckbox();
    },
    // 全选状态check
    checkAllCheckbox: function () {
        var checkBoxSize = $('#tableColsTable .colCheckBox').length;
        var checkedSize = $('#tableColsTable .colCheckBox:checked').length;
        if (checkBoxSize === 0) {
            $('#metadataConfig_selector input').prop('checked', false);
            return;
        }
        if (checkedSize === checkBoxSize) {
            $('#metadataConfig_selector input').prop('checked', true);
        } else {
            $('#metadataConfig_selector input').prop('checked', false);
        }
    }
}

/**
 *  列展示弹框: 新建列/编辑列
 */
var colAlertModal = {
    // 展示弹框
    show: function (title) {
        $('#colAlertModal .form-control').removeAttr('disabled');
        $('#colAlertModal form')[0].reset(); // 清空表单
        $('#colAlertModal .modal-title').text(title);
        $('#colAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 隐藏弹框
    hidden: function () {
        $('#colAlertModal').modal('hide');
    },
    // 获取表格内的所有数据内容
    getContentObj: function () {
        var obj = {};
        var inputArr = $('#colAlertModalForm input');
        for (var i = 0; i < inputArr.length; i++) {
            var data = $(inputArr[i]);
            obj[data.attr('col-name')] = data.val();
        }
        var selectArr = $('#colAlertModalForm select');
        for (var i = 0; i < selectArr.length; i++) {
            var data = $(selectArr[i]).children("option:selected")[0];
            var colName = $(selectArr[i]).attr('col-name');
            obj[colName] = $(data).attr('data-value');
        }
        obj['tableId'] = $('#tableColsDiv').attr('tableId');
        return obj;
    },
    // 新建列-保存
    save: function () {
        if (!$('#colAlertModalForm').isValid()) {
            return;
        }
        $.ajax({
            url: webpath + "/batchdata/column/insertColumn",
            type: 'POST',
            data: colAlertModal.getContentObj(),
            dataType: "json",
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('添加成功！');
                    colAlertModal.hidden();
                    initTableCols({"tableId": $('#tableColsDiv').attr('tableId')});
                } else {
                    failedMessager.show('添加失败！' + data.msg);
                }
            }
        });
    },
    // 表格内--编辑列弹出列信息
    echoCol: function (columnId) {
        $('#colAlertModal .form-control').removeAttr('disabled');
        $('#colAlertModal .modal-footer button').css('display', 'none');
        $('#colAlertModal .modal-footer .notView button').css('display', 'inline-block');
        $('#col_saveButton').attr('operate-type', '1'); // 修改保存按钮标识
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
            }
        });
        colAlertModal.show('修改列信息');
    },
    // 表格内--查看列弹出列信息
    detailCol: function (columnId) {
        $('#colAlertModal .modal-footer button').css('display', 'none');
        $('#colAlertModal .modal-footer #closeViewCol').css('display', 'inline-block');
        $('#col_saveButton').attr('operate-type', '2'); // 修改保存按钮标识
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
                $('#colAlertModal .form-control').attr('disabled', true);
            }
        });
        colAlertModal.show('查看列信息');
    },
}

function initPage() {
    dataSourceModal.showMgr();

    /**
     * 数据源管理
     */
    // 数据源管理页面搜索
    $('#dsSearchBtn').click(function () {
        initDbTable();
    });

    // 数据库类型selector事件
    $('#dbType').on('change', function () {
        if ($('#dbType option:selected').attr('dbType') === '8') { // hbase不允许开启连接池
            $('#isPool').attr('disabled', true);
            $("#isPool option[isPool='0']").prop('selected', true);
        } else {
            $('#isPool').removeAttr('disabled');
        }
    });

    // 新建数据源
    $('#addDataSourceBtn').click(function () {
        // // 新建数据源 authCheck（去掉校验了）
        // $.ajax( {
        // 	url: webpath + '/datasource/save/checkAuth',
        // 	type: 'GET',
        // 	dataType: "json",
        // 	data: {},
        // 	success: function ( data ) {
        // 		if ( data.status === 0 ) {
        $('#datasourceModalForm')[0].reset(); // 清空表单数据
        $('#dsAlertModal .form-control').removeAttr('disabled');
        $('#dsAlertModal .modal-footer .notView button').css('display', 'inline-block');
        $('#closeViewDs').css('display', 'none');
        // 复原hive选填项
        $('#datasourceModalForm').find("input[id='dbUsername']").attr('isMust', '1').parent().siblings('label').find('.mustIcon').css('display', 'inline');
        $('#datasourceModalForm').find("input[id='dbPassword']").attr('isMust', '1').parent().siblings('label').find('.mustIcon').css('display', 'inline');
        // 复原hbase隐藏项
        $('#datasourceModalForm .maxConnectGroup, #datasourceModalForm .maxIdleGroup, #datasourceModalForm .dbUsernameGroup,  #datasourceModalForm .dbPasswordGroup').removeClass('hide');
        $('#dsAlertTitle').text('新建数据源'); // 修改表单title
        $('#dsAlertModal').modal('toggle', 'center');
        $('#dsSaveButton').unbind('click'); // 清除其他由编辑绑定的click事件
        $('#dsSaveButton').click(function () {
            dsOperate('insert'); // 保存新建数据源
        });
        // 		} else {
        // 			failedMessager.show( data.msg );
        // 		}
        // 	}
        // } );
    });

    // 测试连接
    $('#dsTestButton').click(function () {
        dsOperate('test', $(this).attr('data-id'));
    });

    // 改变启用/不启用连接池
    $('#datasourceTable').on('click', '.switch input', function () {
        var flag = $(this).prop('checked');
        var status = flag ? 0 : 1; // 1启用 ; 0不启用
        if (flag) {
            status = 1;
        } else {
            status = 0;
        }
        // 调用接口启用/关闭连接池
        $.ajax({
            url: webpath + "/datasource/updateIsPool",
            type: 'POST',
            data: {'dbId': $(this).attr('data-id'), 'status': status},
            dataType: "json",
            beforeSend: function () {
                loading.show();
            },
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('修改成功！');
                } else {
                    failedMessager.show(data.msg);
                }
            },
            complete: function () {
                loading.hide();
                initDbTable();
            }
        });
    });

    // 删除数据源
    $('#datasourceTable').on('click', '.dsDelSpan', function () {
        var dbId = $(this).attr('data-id');
        $.ajax({
            url: webpath + '/datasource/delete/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {"dbId": dbId},
            success: function (data) {
                if (data.status === 0) {
                    confirmAlert.show("确认删除？", function () {
                        $.ajax({
                            url: webpath + "/datasource/delete",
                            type: 'POST',
                            data: {'dbId': dbId},
                            dataType: "json",
                            success: function (data) {
                                successMessager.show('删除成功！');
                                initDbTable();
                            }
                        });
                    });
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    });

    // 数据源类型改变影响表单展示内容
    $('#dbTypeFormGroup select').change(function () {
        var dbType = $('#dbTypeFormGroup :selected').attr('dbType');
        var flag = !!(dbType === '5' || dbType === '8'); // hive 用户名密码选填
        if (flag) {
            $('#datasourceModalForm').find("input[id='dbUsername']").attr('isMust', '0').parent().siblings('label').find('.mustIcon').css('display', 'none');
            $('#datasourceModalForm').find("input[id='dbPassword']").attr('isMust', '0').parent().siblings('label').find('.mustIcon').css('display', 'none');
        } else {
            $('#datasourceModalForm').find("input[id='dbUsername']").attr('isMust', '1').parent().siblings('label').find('.mustIcon').css('display', 'inline');
            $('#datasourceModalForm').find("input[id='dbPassword']").attr('isMust', '1').parent().siblings('label').find('.mustIcon').css('display', 'inline');
        }
        if (dbType === '8') { // hbase 隐藏部分内容
            $('#datasourceModalForm .maxConnectGroup, #datasourceModalForm .maxIdleGroup, #datasourceModalForm .dbUsernameGroup,  #datasourceModalForm .dbPasswordGroup').addClass('hide');
        } else {
            $('#datasourceModalForm .maxConnectGroup, #datasourceModalForm .maxIdleGroup, #datasourceModalForm .dbUsernameGroup,  #datasourceModalForm .dbPasswordGroup').removeClass('hide');
        }
    });

    // 元数据管理返回
    $('#returnToDsMgr').click(function () {
        dataSourceModal.showMgr();
    });

    // 列管理返回
    $('#returnToScaned').click(function () {
        dataSourceModal.showMetadata();
    });

    // 扫描按钮
    $('#scanDb').click(function () {
        var dbId = $('#scanedTablesDiv').attr('dbId');
        if (dbId) {
            scanDbTableModal.initPage(dbId);
        }
    });

    /**
     * 列管理表格
     */
    // 列搜索
    $('#colNameSearchBtn').click(function () {
        initTableCols();
    });

    // 表格全选/全不选
    $('#tableColsTable').on('click', '#metadataConfig_selector input', function () {
        tableColsObj.selector($(this).prop('checked'));
    });

    // 新建列
    $('#addColButton').click(function () {
        tableColsObj.addCol();
    });

    // 保存列
    $('#col_saveButton').click(function () {
        if ($(this).attr('operate-type') === '0') { // 新建保存
            colAlertModal.save();
        } else if ($(this).attr('operate-type') === '1') { // 修改保存
            tableColsObj.editCol($(this).attr('col-id'));
        }
    });

    // 批量删除
    $('#colsDelButton').click(function () {
        tableColsObj.delCols();
    });

    // 列check
    $('#tableColsTable').on('click', '.colCheckBox', function () {
        tableColsObj.updateCache($(this).attr('col-id'), $(this).prop('checked'));
    });
}

// 连接测试/保存/更新数据源
function dsOperate(type, dbId) {
    // 1. 表单验证
    if (!$('#datasourceModalForm').isValid()) {
        if (!($('#dbTypeFormGroup :selected').attr('dbType') === '8')) {
            return;
        }
    }
    // 2. 获取表单数据
    var obj = {};
    var inputs = $("#datasourceModalForm .form-group:not('.hide') input");
    for (var i = 0; i < inputs.length; i++) {
        obj[$(inputs[i]).attr('id')] = $(inputs[i]).val();
    }
    obj["dbType"] = $('#dbTypeFormGroup :selected').attr('dbType');
    obj["isPool"] = $('#isPoolFormGroup :selected').attr('isPool');
    if (obj.dbType !== '8') {
        // 如果没有指定则默认10
        if ($.trim(obj.maxConnect) === '') { // 最大连接数
            obj['maxConnect'] = '10';
        }
        if ($.trim(obj.maxIdle) === '') { // 最大活跃连接数
            obj['maxIdle'] = '10';
        }
    }
    // 3. 请求url
    var url = '';
    var successMsg = '';
    if (type == 'insert') { // 新建保存
        url = '/datasource/insert';
        successMsg = '新建数据源成功！';
    } else if (type == 'test') { // 连接测试
        url = '/datasource/dbConnetionTest';
        successMsg = '测试成功！';
        obj["dbId"] = dbId;
    } else if (type == 'update') { // 编辑保存
        url = '/datasource/update';
        successMsg = '保存成功！';
        obj["dbId"] = dbId;
    }
    // 4. 发起请求
    $.ajax({
        url: webpath + url,
        type: 'POST',
        data: obj,
        dataType: "json",
        beforeSend: function () {
            loading.show();
        },
        success: function (data) {
            if (data.status === 0) {
                if (type == 'insert' || type == 'update') {
                    $('#dsAlertModal').modal('toggle', 'center');
                }
                successMessager.show(successMsg);
                initDbTable();
            } else {
                failedMessager.show(data.msg);
            }
        },
        complete: function () {
            loading.hide();
        }
    });
}

// 加载弹框表单内容
function initModalForm() {
    var data = [
        {"id": "dbAlias", "name": "连接别名", "type": "text", "isMust": "1"},
        {"id": "dbIp", "name": "连接IP", "type": "text", "isMust": "1"},
        {"id": "dbPort", "name": "连接端口号", "type": "text", "isMust": "1"},
        {"id": "dbServiceName", "name": "数据库服务名", "type": "text", "isMust": "1"},
        {"id": "maxConnect", "name": "最大连接数", "type": "text", "isMust": "0"},
        {"id": "maxIdle", "name": "最大活跃连接数", "type": "text", "isMust": "0"},
        {"id": "dbUsername", "name": "数据库用户名", "type": "text", "isMust": "1"},
        {"id": "dbPassword", "name": "数据库密码", "type": "password", "isMust": "1"}
    ];
    var htmlArr = [];
    for (var i = 0; i < data.length; i++) {
        var id = data[i].id;
        var name = data[i].name;
        var type = data[i].type;
        var isMustStr = (data[i].isMust === '1') ? '<i class="mustIcon">*</i>' : '';
        var formInputHtmlStr = '';
        formInputHtmlStr += '<div class="form-group ';
        formInputHtmlStr += id + 'Group';
        formInputHtmlStr += ' "><label class="col-xs-3 col-sm-3 col-md-3 col-lg-3" for=\'' + id + '\' >';
        formInputHtmlStr += isMustStr + name;
        formInputHtmlStr += '</label><div class="col-xs-7 col-sm-7 col-md-7 col-lg-7"><input isMust=\'' + data[i].isMust + '\' type=\'' + type + '\' class="form-control" id=\'' + id + '\' data-rule="\'' + name + '\':required;"></div></div>';
        htmlArr.push(formInputHtmlStr);
    }
    $('#dbTypeFormGroup').before(htmlArr[0], htmlArr[1], htmlArr[2]);
    $('#isPoolFormGroup').after(htmlArr[3], htmlArr[4], htmlArr[5], htmlArr[6], htmlArr[7]);
}

// 数据源列表
function initDbTable() {
    $('#datasourceTable').width('100%').dataTable({
        "searching": false, // 是否开启搜索功能
        "ordering": false,
        "destroy": true,
        "bLengthChange": false,
        "pagingType": "full_numbers",
        "paging": true,
        "info": true,
        "serverSide": true,
        "pageLength": 9,
        "columns": [
            {"title": "连接别名", "data": "dbAlias"},
            {"title": "连接IP", "data": "dbIp"},
            {"title": "连接端口号", "data": "dbPort"},
            {
                "title": "连接类型", "data": "dbType", "render": function (data) {
                    switch ($.trim(data)) {
                        case '1':
                            return 'MYSQL';
                        case '2':
                            return 'ORACLE';
                        case '3':
                            return 'SQLSERVER';
                        case '4':
                            return 'DB2';
                        case '5':
                            return 'HIVE';
                        case '6':
                            return 'HIVE2';
                        case '7':
                            return 'XCLOUD';
                        case '8':
                            return 'HBASE';
                        default:
                            return '--';
                    }
                }
            },
            {"title": "数据库服务名", "data": "dbServiceName"},
            {"title": "数据库用户名", "data": "dbUsername"},
            {"title": "最大连接数", "data": "maxConnect"},
            {"title": "最大活跃连接数", "data": "maxIdle"},
            {
                "title": "是否启用连接池", "data": "isPool", "render": function (data, type, row) {
                    var isPoolValue = '';
                    var checkHtmlStr = '' // 控制开关开启状态
                    if ($.trim(data) == '1') {
                        isPoolValue = '已启用';
                        checkHtmlStr += 'checked';
                    } else if ($.trim(data) == '0') {
                        isPoolValue = '未启用';
                    } else {
                        isPoolValue = '--';
                    }
                    var htmlStr = "";
                    htmlStr += '<div class="switch"><input data-id=\'' + row.dbId + '\' type="checkbox" ' + checkHtmlStr + '><label>' + isPoolValue + '</label></div>';
                    return htmlStr;
                }
            },
            {
                "title": "操作", "data": "dbId", "render": function (data, type, row) {
                    var htmlStr = "";
                    // htmlStr += '<span data-id=\'' + data + '\' class="cm-tblB detailSpan"
                    // onclick="dataSourceModal.detailDatasource(\'' + data + '\')" type="button">查看</span>';
                    htmlStr += '<span data-id=\'' + data + '\' class="cm-tblB dsEditSpan" onclick="dataSourceModal.editDataSource(\'' + data + '\')" type="button">修改</span>';
                    htmlStr += '<span data-id=\'' + data + '\' class="cm-tblC dsDelSpan" type="button">删除</span>';
                    return htmlStr;
                }
            }
        ],
        ajax: {
            url: webpath + '/datasource/list',
            "type": 'GET',
            "data": function (d) { // 查询参数
                return $.extend({}, d, {
                    'dbAlias': $.trim($("#searchInput").val())
                });
            },
            dataSrc: loadDataList
        },
        "fnDrawCallback": function (oSettings, json) {
            // $( "tr:even" ).css( "background-color", "#fbfbfd" );
            // $( "table:eq(0) th" ).css( "background-color", "#f6f7fb" );
        }
    });
}

// 扫描表列表
function initDbScanTable(obj) {
    $('#dbScanTable').width('100%').dataTable({
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
            {"title": "表名", "data": "TABLENAME"},
            {"title": "表编码", "data": "TABLECODE"},
            {"title": "创建时间", "data": "CREATEDATE"},
            {
                "title": "操作", "data": null, "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<span class="cm-tblB detailSpan" onclick="metadataObj.colsConfig(\'' + row.TABLEID + '\', \'' + row.TABLENAME + '\')" type="button">列管理</span>';
                    htmlStr += '<span class="cm-tblC delSpan" onclick="metadataObj.delScanTable(\'' + row.TABLEID + '\')" type="button">删除</span>';
                    return htmlStr;
                }
            }
        ],
        ajax: {
            url: webpath + '/datasource/metadata/table/paged',
            "type": 'GET',
            "data": function (d) { // 查询参数
                return $.extend({}, d, obj);
            }
        },
        "fnDrawCallback": function (oSettings, json) {
            // $( "tr:even" ).css( "background-color", "#fbfbfd" );
            // $( "table:eq(0) th" ).css( "background-color", "#f6f7fb" );
        }
    });
}

// 加载列表格
function initTableCols(obj) {
    $.fn.dataTable.ext.errMode = 'none';
    $('#tableColsTable')
        .on('error.dt', function (e, settings, techNote, message) {
            console.log('An error has been reported by DataTables: ', message);
        })
        .width('100%').dataTable({
        "searching": false, // 是否开启搜索功能
        "ordering": false,
        "destroy": true,
        "bLengthChange": false,
        "pagingType": "full_numbers",
        "paging": true,
        "info": true,
        "serverSide": true,
        "pageLength": tableColsObj.colTablePageSize,
        "columns": [
            {
                "title": function () {
                    var htmlStr = '';
                    htmlStr += '<div id="metadataConfig_selector" is-check="0" class="checkbox"><label><input type="checkbox"></label></div>';
                    return htmlStr;
                },
                "data": "COLUMNID",
                "width": "9%",
                "render": function (data) {
                    var htmlStr = '';
                    htmlStr += '<div class="checkbox"><label><input class="colCheckBox" col-id=\'' + data + '\' type="checkbox"></label></div>';
                    return htmlStr;
                }
            },
            {"title": "列名", "data": "COLUMNNAME", "width": "13%"},
            {"title": "列编码", "data": "COLUMNCODE", "width": "13%"},
            {"title": "列类型", "data": "COLUMNTYPE", "width": "13%"},
            {"title": "列大小", "data": "COLUMNSIZE", "width": "13%"},
            {
                "title": "是否主键", "data": "ISPK", "width": "13%", "render": function (data) {
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
                "title": "是否可为空", "data": "ISNULL", "width": "13%", "render": function (data) {
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
                "title": "操作", "data": null, "width": "13%", "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<span col-id=\'' + data + '\' class="cm-tblB detailSpan" type="button" onclick="colAlertModal.detailCol(\'' + row.COLUMNID + '\')">查看</span>';
                    htmlStr += '<span col-id=\'' + data + '\' class="cm-tblB" type="button" onclick="colAlertModal.echoCol(\'' + row.COLUMNID + '\')">修改</span>';
                    htmlStr += '<span col-id=\'' + data + '\' class="cm-tblC delSpan" type="button" onclick="tableColsObj.delCol(\'' + row.COLUMNID + '\', \'' + row.TABLEID + '\')">删除</span>';
                    return htmlStr;
                }
            }
        ],
        ajax: {
            url: webpath + '/batchdata/column/columnList',
            "type": 'GET',
            "data": function (d) { // 查询参数
                return $.extend({}, d,
                    {
                        "tableId": $('#tableColsDiv').attr('tableId'),
                        "columnName": $.trim($('#search_colNameInput').val())
                    }
                );
            }
        },
        "fnDrawCallback": function (oSettings, json) {
            // $("tr:even" ).css( "background-color", "#fbfbfd" );
            // $("table:eq(0) th" ).css( "background-color", "#f6f7fb" );

            $("#tableColsTable input[type='checkbox']").css('display', 'inline-block');
            for (var i = 0; i < tableColsObj.checkedCols.length; i++) { // 勾选回显
                $("#tableColsTable .colCheckBox[col-id='" + tableColsObj.checkedCols[i] + "']").prop('checked', true);
            }
            tableColsObj.checkAllCheckbox();
        }
    });
}

// 初始化变量类型下拉框
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

// 加载新ui
function loadDataList(data) {
    data = data.data;
    var html = "";
    var keyMap = [
        {id: 'dbIp', name: '连接IP'},
        {id: 'dbPort', name: '连接端口号'},
        {id: 'dbType', name: '连接类型'},
        {id: 'dbServiceName', name: '数据库服务名'},
        {id: 'dbUsername', name: '数据库用户名'},
        {id: 'maxConnect', name: '最大连接数'},
        {id: 'maxIdle', name: '最大活跃连接数'}
    ];
    for (var i = 0; i < data.length; i++) {
        html += (
            '<li id="modelCard_0" class="modelCard col-xs-12 col-sm-12 col-md-6 col-lg-4">' +
            '<div class="card_container card_background_0">' +
            '<div class="card_header">' +
            '<div class="card_title" title="' + data[i].dbAlias + '">' +
            '<img src="' + contextPath + '/static/imgs/datasource/icon_sjy.png" />' + data[i].dbAlias +
            '</div>' +
            '<div class="card_subtitle">' +
            '<span>启用连接池</span>' +
            '<input type="checkbox" class="inset_3" ' + (data[i].isPool === '1' ? 'checked' : '') + '>' +
            '<label class="blue" onclick="dataSource.handlePoolClick( $( this ), \'' + data[i].dbId + '\' )"></label>' +
            '</div>' +
            '</div>' +
            '<div class="card_main">' +
            loadMain(data[i]) +
            '</div>' +
            '<div class="card_footer">' +
            '<button class="outside" onclick="dataSource.handleEditDataSource( \'' + data[i].dbId + '\' )">修改</button>' +
            '<button class="outside" onclick="dataSource.handleDeleteDataSource( \'' + data[i].dbId + '\' )">删除</button>' +
            '<button class="btn btn-primary" onclick="dataSource.handleDataManager( \'' + data[i].dbId + '\' )">元数据管理</button>' +
            '</div>' +
            '</div>' +
            '</li>'
        );
    }
    $('#datasourceContent ul').html(html);

    function loadMain(row) {
        var html = "";
        for (var i = 0; i < keyMap.length; i++) {
            if (i % 4 === 0) html += '<div class="row">';
            var value = '';
            if (keyMap[i].id === 'dbType') {
                switch ($.trim(row[keyMap[i].id])) {
                    case '1':
                        value = 'MYSQL';
                        break;
                    case '2':
                        value = 'ORACLE';
                        break;
                    case '3':
                        value = 'SQLSERVER';
                        break;
                    case '4':
                        value = 'DB2';
                        break;
                    case '5':
                        value = 'HIVE';
                        break;
                    case '6':
                        value = 'HIVE2';
                        break;
                    case '7':
                        value = 'XCLOUD';
                        break;
                    case '8':
                        value = 'HBASE';
                        break;
                    default:
                        value = '--';
                }
            } else {
                value = row[keyMap[i].id];
            }
            html += (
                '<div class="col-xs-12 col-sm-12 col-md-6 col-lg-3">' +
                '<div>' + keyMap[i].name + '</div>' +
                '<div>' + value + '</div>' +
                '</div>'
            );
            if ((i + 1) % 4 === 0 || i === keyMap.length - 1) html += '</div>';
        }
        return html;
    }

    return data;
}

var dataSource = {
    data: {},

    // 切换是否启用连接池
    handlePoolClick: function (dom, dbId) {
        var checked = $(dom).prev().is(':checked');
        var body = {
            'dbId': dbId,
            'status': checked ? 0 : 1
        };
        dataSource.$ajax('/datasource/updateIsPool', 'POST', body, function (data) {
            if (data.status === 0) {
                successMessager.show('修改成功！');
                $(dom).prev().prop('checked', !checked);
            } else {
                failedMessager.show(data.msg);
            }
        }, 'json', true);
    },

    // 修改数据源弹窗
    handleEditDataSource: function (dbId) {
        // 修改数据源权限 authCheck
        dataSource.$ajax('/datasource/update/checkAuth', 'GET', {dbId: dbId}, function (res) {
            if (res.status === 0) {
                $('#datasourceModalForm')[0].reset(); // 清空表单数据
                $('#dsAlertModal .form-control').removeAttr('disabled');
                $('#dsAlertModal .modal-footer .notView button').css('display', 'inline-block');
                $('#closeViewDs').css('display', 'none');
                // 复原hive选填项
                $('#datasourceModalForm').find("input[id='dbUsername']").attr('isMust', '1').parent().siblings('label').find('.mustIcon').css('display', 'inline');
                $('#datasourceModalForm').find("input[id='dbPassword']").attr('isMust', '1').parent().siblings('label').find('.mustIcon').css('display', 'inline');
                // 还原hbase过滤项
                $('#datasourceModalForm .maxConnectGroup, #datasourceModalForm .maxIdleGroup, #datasourceModalForm .dbUsernameGroup,  #datasourceModalForm .dbPasswordGroup').removeClass('hide');
                $('#dsAlertTitle').text('修改数据源'); // 修改表单title
                dataSource.$ajax('/datasource/selectByPrimaryKey', 'POST', {dbId: dbId}, function (data) {
                    // 数据对应至表单内
                    $('#dsAlertModal').find('#dbAlias').val(data.dbAlias);
                    $('#dsAlertModal').find('#dbIp').val(data.dbIp);
                    $('#dsAlertModal').find('#dbPort').val(data.dbPort);
                    $('#dsAlertModal').find('#dbServiceName').val(data.dbServiceName);
                    $('#dsAlertModal').find('#maxConnect').val(data.maxConnect);
                    $('#dsAlertModal').find('#maxIdle').val(data.maxIdle);
                    $('#dsAlertModal').find('#dbUsername').val(data.dbUsername);
                    $('#dsAlertModal').find('#dbPassword').val(data.dbPassword);
                    $("#dbTypeFormGroup option[dbType='" + data.dbType + "']").prop('selected', true);
                    $("#isPoolFormGroup option[isPool='" + data.isPool + "']").prop('selected', true);
                    $('#dsTestButton').attr('data-id', data.dbId); // 给测试按钮添加data-id属性赋值dbId
                    $('#dsSaveButton').attr('data-id', data.dbId);

                    // hive 用户名密码选填
                    if (data.dbType === '5' || data.dbType === '8') {
                        $('#datasourceModalForm').find("input[id='dbUsername']").attr('isMust', '0').parent().siblings('label').find('.mustIcon').css('display', 'none');
                        $('#datasourceModalForm').find("input[id='dbPassword']").attr('isMust', '0').parent().siblings('label').find('.mustIcon').css('display', 'none');
                    }
                    // hbase 隐藏部分内容
                    if (data.dbType === '8') {
                        $('#datasourceModalForm .maxConnectGroup, #datasourceModalForm .maxIdleGroup, #datasourceModalForm .dbUsernameGroup,  #datasourceModalForm .dbPasswordGroup').addClass('hide');
                    }

                    $('#dbType').trigger('change');

                    // 弹出弹框
                    $('#dsAlertModal').modal('toggle', 'center');
                });
                // 编辑更新
                $('#dsSaveButton').unbind('click');
                $('#dsSaveButton').click(function () {
                    dsOperate('update', $(this).attr('data-id'));
                });
            } else {
                failedMessager.show(res.msg);
            }
        }, 'json', false);
    },

    // 删除数据源
    handleDeleteDataSource: function (dbId) {
        // 修改数据源权限 authCheck
        dataSource.$ajax('/datasource/delete/checkAuth', 'GET', {dbId: dbId}, function (res) {
            if (res.status === 0) {
                confirmAlert.show("确认删除？", function () {
                    dataSource.$ajax('/datasource/delete', 'POST', {dbId: dbId}, function (data) {
                        if (data.status === 0) {
                            successMessager.show('删除成功！');
                            initDbTable();
                        } else {
                            failedMessager.show(data.msg);
                        }
                    }, 'json', false);
                });
            } else {
                failedMessager.show(res.msg);
            }
        }, 'json', false);
    },

    // 元数据管理
    handleDataManager: function (dbId) {
        $.ajax({ // 元数据管理校验
            url: webpath + '/datasource/metadataMgr/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {dbId: dbId},
            success: function (data) {
                if (data.status === 0) {
                    dataSourceModal.showMetadata();
                    $('#scanedTablesDiv').removeAttr('dbId').attr('dbId', dbId);
                    initDbScanTable({"dbId": dbId});
                    if (data.status === 0) { // 拥有元数据内修改权限（删除元数据、扫描）
                        $('#scanedTablesDiv').removeAttr('updateAuth').attr('updateAuth', '1');
                    } else {
                        $('#scanedTablesDiv').removeAttr('updateAuth').attr('updateAuth', '0');
                    }
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },

    $ajax: function (url, method, body, callback, dataType, isLoading) {
        if (dataType === void 0) dataType = 'json';
        var option = {
            url: webpath + url,
            type: method,
            dataType: dataType,
            beforeSend: function () {
                if (isLoading) loading.show();
            },
            success: function (data) {
                callback(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.log(errorThrown);
                if (textStatus === 'timeout') {
                    console.log('请求超时');
                }
            },
            complete: function () {
                if (isLoading) loading.hide();
            }
        };
        if (body) option.data = body;
        $.ajax(option);
    }
};

$(function () {
    initDbTable();
    initModalForm();
    initBaseVariableType();
    initPage();
});