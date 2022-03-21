/**
 * 元数据管理
 * data:2019/05/23
 * author:bambi
 */

// 当前所在规则包id
var folderId = $('#folderId').prop('value');

/**
 * 元数据管理页面内容
 */
var getContent = {
    /**
     * 拼接主页面数据htmlStr
     *
     * 1. 输入表：panelElement_input icon-database
     * 2. 输出表：panelElement_output icon-table
     * 3. 关联关系：panelElement_relation icon-link
     */
    createEleHtml: function (className, iconName, tableName, tableCode, tableKind, tableId, scanId) {
        var htmlStr = '';
        htmlStr += '<div class="panelEle ';
        htmlStr += className;
        htmlStr += '"><i class="icon ';
        htmlStr += iconName;
        htmlStr += '"> </i><span table-code=\'' + tableCode + '\' tablekind=\'' + tableKind + '\' tableId=\'' + tableId + '\' scanId=\'' + scanId + '\'>' + tableName + '</span></div>';
        return htmlStr;
    },
    // 拼接添加关联表页面数据htmlStr
    createRelationEleHtml: function (tableName, tableCode, tableId, dbId) {
        var htmlStr = '';
        htmlStr += '<div class="checkbox checkbox_relPage"><label><input table-code=\'' + tableCode + '\' tableId=\'' + tableId + '\' dbId=\'' + dbId + '\' class="addRelationCheckInput" type="checkbox">' + tableName + '</label></div>';
        return htmlStr;
    },
    // 拼接绑定关系页面数据htmlStr
    createBindRelEleHtml: function (data) {
        var relId = data.DBRESOURCEID;
        var tableId = data.TABLEID;
        var tableCode = data.TABLECODE;
        var tableName = data.TABLENAME;
        var isMain = data.ISMAIN;
        var join = data.JOIN;
        var relation = data.RELATION;

        if (relId == '' || relId == undefined || tableId == '' || tableId == undefined) {
            failedMessager.show('接口参数错误！');
        } else {
            $('#bindRelModalBody').attr('rel-id', relId);
            // 复制一个模版
            var newNode = $('#template').clone(true);
            // 重置id
            newNode.prop('id', tableId);
            // 填充表名 & ID信息
            $(newNode).find('.tableNameTitle').attr('data-id', tableId).attr('table-code', tableCode).text(tableName);
            $(newNode).find('.bindRel_setMain').attr('data-id', tableId);
            $(newNode).find('.bindRel_editButton').attr('data-id', tableId);
            // 填充主表信息
            if (isMain == '1') { // 主表, 隐藏设置按钮, 按钮不可编辑, 设置主表id
                $(newNode).find('.bindRel_setMain').css('display', 'none');
                $(newNode).find('.bindRel_idMain').css('display', 'block');
                $(newNode).find('button').addClass('disabled');
                $('#bindRelModalBody').attr('main-id', tableId);
            } else {
                $(newNode).find('.bindRel_idMain').css('display', 'none');
                $(newNode).find('.bindRel_setMain').css('display', 'block');
            }
            // 对应连接信息
            var joinText = $(newNode).find(".dropdown-menu li[join='" + join + "']").text();
            $(newNode).find('.bindTypeGroup .join').attr('join-value', join).text(joinText);
            // 填充关联关系内容
            $(newNode).find('.bindRelContent').text(relation);
            $('#bindRelModalBody').append(newNode);
            newNode.show();
        }
    },
    // 拼接绑定关系-编辑tree节点对象
    createTreeNode: function (data) {
        var obj = {};
        var htmlStr = '';
        if (data.isMain == '1') {
            htmlStr += '<i class="icon icon-table tree_mainTableIcon" table-code=\'' + data.tableCode + '\' > <b>';
        } else {
            htmlStr += '<i class="icon icon-table tree_commonTableIcon" table-code=\'' + data.tableCode + '\' > <b>';
        }
        htmlStr += data.tableCode;
        htmlStr += '</b></i>';
        obj['html'] = htmlStr;
        obj['id'] = data.id;
        // obj['open'] = data.open;
        if ((data.isParent == true) && (data.children.length > 0)) {
            var childrenData = data.children;
            var children = [];
            for (var i = 0; i < childrenData.length; i++) {
                var childObj = {};
                var childHtmlStr = '';
                childHtmlStr += '<i draggable="true" class="icon icon-columns tree_childrenTableIcon" column-code=\'' + childrenData[i].COLUMNCODE + '\' table-code=\'' + childrenData[i].TABLECODE + '\'> <span>';
                childHtmlStr += childrenData[i].COLUMNCODE;
                childHtmlStr += '</span></i>';
                childObj['html'] = childHtmlStr;
                childObj['id'] = childrenData[i].ID;
                children.push(childObj);
            }
            obj['children'] = children;
        }
        return obj;
    }
}

/**
 * 获取所有数据源
 */
function getAllDataSource() {
    $.ajax({
        url: webpath + '/datasource/dataSourceList',
        type: 'POST',
        dataType: "json",
        success: function (data) {
            var htmlStr = '<option>--请选择--</option>';
            for (var i = 0; i < data.length; i++) {
                var isHiveDb = (data[i].DBTYPEID == '5' || data[i].DBTYPEID == '6') ? '1' : '0';  // hive类型数据库特殊标识
                htmlStr += '<option datasource-id=\'' + data[i].DBID + '\' isHiveDb=\'' + isHiveDb + '\'>' + data[i].DBALIAS + '</option>';
            }
            $('.datasourceList').html(htmlStr);
            $('#scanDatasourceList option:first').prop('selected', true);
        }
    });
}

/**
 * 根据数据源id获取其下所有tables
 */
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

/**
 *  返回跳转
 */
function returnPage() {
    var url = webpath + "/ruleFolder/rulePackageMgr?folderId=" + folderId + '&childOpen=o';
    creCommon.loadHtml(url);
}

/**
 * 为每一个面板内的内容添加click事件
 */
var contentHandle = {
    // 输出表/输入表
    colsConfig: function (tableId, tableName, scanId) {
        var url = webpath + "/batchdata/column/view?packageId=" + folderId + "&tableId=" + tableId + "&tableName=" + tableName + "&scanId=" + scanId + "&childOpen=o";
        creCommon.loadHtml(url);
    },
    // 添加关联关系/点击编辑关联关系
    relationConfig: function (relId) {
        $('#template').nextAll().remove(); // 先清空内容(除模版)
        $('#saveRel').removeClass('disabled');
        $('#addRelNextStep').addClass('disabled').removeAttr('dbResourceId');
        if (relId != '' && relId != undefined) {
            $.ajax({
                url: webpath + '/dbresource/getTableRelation',
                type: 'POST',
                dataType: "json",
                data: {'dbResourceId': relId},
                success: function (data) {
                    if (data.length > 0) {
                        for (var i = 0; i < data.length; i++) {
                            getContent.createBindRelEleHtml(data[i]);
                        }
                        relationModal.show_bind();
                    } else {
                        failedMessager.show('该关联表数据错误！')
                    }
                }
            });
        }
    }
}

/**
 *  扫描
 */
var colsAlertModal = {
    // 显示弹框
    show: function (tableKind) {
        $('#dsModalForm')[0].reset(); // 清空表单数据
        $('#scanConfirm').attr('tableKind', tableKind); // 把表类型直接带到扫描按钮上
        $('#scanModal').modal({'show': 'center', "backdrop": "static"});
        $('#scanModal').unbind('shown.zui.modal');
        $('#scanModal').on('shown.zui.modal', function () {
            if (tableKind === '2') { // 输出表扫描过滤掉hive类型数据库选项
                $("#scanDatasourceList option[isHiveDb='1']").css('display', 'none');
            } else {
                $('#scanDatasourceList option').css('display', 'block');
            }
        });
    },
    // 数据源表扫描
    scanConfirm: function () {
        $('#colsListContainer').html(''); // 先清空
        var datasourceId = $('#scanDatasourceList option:selected').attr('datasource-id');
        var tableName = $('#scanTables option:selected').val();
        var tableKind = $('#scanConfirm').attr('tableKind');
        if (!datasourceId || !tableName) {
            failedMessager.show('信息无效，扫描失败！');
            return;
        }
        $.ajax({
            url: webpath + '/metadata/scan',
            type: 'POST',
            dataType: "json",
            data: {dbId: datasourceId, packageId: folderId, scanKey: tableName, tableKind: tableKind},
            // async: false, // 等ajax执行完成再执行后续函数, 相当于是同步请求, 不能与后面的beforeSend共用
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
                    $('#scanModal').modal('toggle', 'center'); // 关闭扫描弹框
                    $('#scanColsModal').modal({'show': 'center', "backdrop": "static"}); // 弹出列展示选择弹框
                } else {
                    warningMessager.show(data.msg);
                }
            },
            complete: function () {
                loading.hide();
            }
        });
    },
    // 扫描后保存至表
    save: function () {
        var scanId = $('#colsListContainer').attr('scanId');
        var checkedArr = $('#colsListContainer .checkInput:checked');
        if (checkedArr.length > 0) {
            var columnIds = [];
            for (var i = 0; i < checkedArr.length; i++) {
                columnIds.push($.trim($(checkedArr[i]).attr('colId')));
            }
            var cols = JSON.stringify(columnIds);
            $.ajax({
                url: webpath + '/metadata/save',
                type: 'POST',
                dataType: "json",
                data: {
                    'scanId': scanId,
                    'columnIds': cols
                },
                // async: false, // 等ajax执行完成再执行后续函数
                beforeSend: function () {
                    loading.show();
                },
                success: function (data) {
                    if (data.status === 0) {
                        $('#scanColsModal').modal('toggle', 'center'); // 关闭列展示选择弹框
                        successMessager.show('保存成功');
                        initContent();
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
            url: webpath + '/metadata/scan/cancel',
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
                    $('#scanColsModal').modal('toggle', 'center'); // 关闭列展示选择弹框
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
    }
}

/**
 * 主页面添加表
 */
var addTableModal = {
    // 展示添加弹框
    show: function () {
        $('#addTableModalForm')[0].reset(); // 清空表单数据
        $('#addModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 关闭弹框
    hide: function () {
        $('#addModal').modal('toggle', 'center');
    },
    // 添加保存
    insert: function () {
        if ($.trim($('#tableName').val()) != '') {
            if ($.trim($('#tableCode').val()) != '') {
                // 获取表单数据
                var obj = {};
                var inputs = $('#addTableModalForm .form-group input');
                for (var i = 0; i < inputs.length; i++) {
                    obj[$(inputs[i]).attr('id')] = $(inputs[i]).val();
                }
                obj['isTemp'] = $('#isTempGroup :selected').attr('isTemp');
                obj['isPublic'] = $('#isPublicGroup :selected').attr('isPublic');
                obj['tableType'] = $('#tableTypeGroup :selected').attr('tableType');
                obj['dbId'] = $('#addDataSourceGroup :selected').attr('datasource-id');
                obj['packageId'] = folderId;
                obj['tableKind'] = 2; // 仅输出表可以添加表
                // ajax请求
                $.ajax({
                    url: webpath + '/batchdata/metadata/insertTable',
                    type: 'POST',
                    dataType: "json",
                    data: obj,
                    success: function (data) {
                        if (data.status === 0) { // 成功, 刷新页面
                            successMessager.show('保存成功！');
                            addTableModal.hide();
                            initContent();
                        } else { // 失败
                            failedMessager.show(data.msg);
                        }
                    }
                });
            } else {
                failedMessager.show('请输入表编码！');
                $('#tableCode').focus();
                return;
            }
        } else {
            failedMessager.show('请输入表别名！');
            $('#tableName').focus();
            return;
        }
    }
}

/**
 *  关联关系
 */
var relationModal = {
    // 显示/关闭 添加关系弹框
    show: function () {
        relationModal.clearAll();
        relationModal.initFirstPage();
        $('#addRelationModal').modal({'show': 'center', "backdrop": "static"});
    },
    hide: function () {
        $('#addRelationModal').modal('toggle', 'center');
    },
    // 添加关联表 -> 下一步按钮
    nextStep: function (dbResourceId) {
        initContent();
        $('#template').nextAll().remove(); // 先清空内容(除模版)
        // 根据关联表ID获取关联表信息
        $.ajax({
            url: webpath + '/dbresource/getTableRelation',
            type: 'POST',
            dataType: "json",
            data: {
                'dbResourceId': dbResourceId
            },
            success: function (data) {
                if (data != '' && data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        getContent.createBindRelEleHtml(data[i]);
                    }
                    relationModal.hide(); // 隐藏添加关联表弹框
                    relationModal.show_bind(); // 显示绑定关系弹框
                } else {
                    // 如果关联关系表有问题则提示
                    failedMessager.show('该关系表数据数据错误');
                    relationModal.hide();
                }
            }
        });
    },
    // 保存关联关系
    saveRelation: function () {
        // 检查前两个选项必须填写
        var relName = $.trim($('#relModal_tableName').val());
        var relCode = $.trim($('#relModal_tableCode').val());
        if (relName != '' || relName == undefined) {
            if (relCode != '' || relCode == undefined) {
                // 检查是否选中至少两个表
                var checkedArr = $('.relationPanelContent .addRelationCheckInput:checked');
                if (checkedArr.length <= 1) {
                    failedMessager.show('请至少选择两个表进行关联！');
                    return;
                } else {
                    // 两两对比检查是否属于同一个数据源
                    for (var i = 0; i < checkedArr.length; i++) {
                        for (var j = i + 1; j < checkedArr.length; j++) {
                            if ($(checkedArr[i]).attr('dbId') != $(checkedArr[j]).attr('dbId')) {
                                failedMessager.show('请选择相同数据源下的表！');
                                return;
                            }
                        }
                    }
                    var dbId = $(checkedArr[0]).attr('dbId');
                    var relTables = [];
                    for (var i = 0; i < checkedArr.length; i++) {
                        relTables.push($(checkedArr[i]).attr('tableId'));
                    }
                    // 请求接口创建一个关联关系任务并返回关联表id
                    $.ajax({
                        url: webpath + '/batchdata/metadata/insertTable',
                        type: 'POST',
                        dataType: "json",
                        data: {
                            'packageId': folderId,
                            'tableKind': 3,
                            'tableName': relName,
                            'tableCode': relCode,
                            'relTable': JSON.stringify(relTables),
                            'dbId': dbId
                        },
                        beforeSend: function () {
                            loading.show();
                        },
                        success: function (data) {
                            $('#saveRel').addClass('disabled');
                            $('#addRelNextStep').removeClass('disabled').attr('dbResourceId', data.data);
                            successMessager.show('保存成功！');
                            for (var i = 0; i < checkedArr.length; i++) {
                                $('#relationInputs').find("input[tableId='" + $(checkedArr[i]).attr('tableId') + "']").prop('checked', true);
                            }
                            $('#relationInputs').find('input').attr('disabled', true);
                        },
                        complete: function () {
                            loading.hide();
                        }
                    });
                }
            } else {
                failedMessager.show('请输入关系编码！');
                $('#relModal_tableCode').focus();
                return;
            }
        } else {
            failedMessager.show('请输入关联关系名！');
            $('#relModal_tableName').focus();
            return;
        }
    },
    // 显示绑定关系弹框
    show_bind: function () {
        $('#bindRelationModal').modal({'show': 'center', "backdrop": "static"});
        // 初始化tree
        bindEdit.initTree($('#bindRelModalBody').attr('rel-id'), $('#bindRelModalBody').attr('main-id'));
    },
    // 隐藏绑定关系弹框
    hide_bind: function () {
        $('#bindRelationModal').modal('toggle', 'center');
    },
    // 设置主表
    setMain: function (dbResourceId, tableId) {
        var flag = false;
        if ($.trim(tableId) == '' || tableId == undefined) {
            failedMessager.show('设置主表参数错误！');
        } else {
            $.ajax({
                url: webpath + '/dbresource/setMainTable',
                type: 'POST',
                dataType: "json",
                async: false,
                data: {'dbResourceId': dbResourceId, 'tableId': tableId},
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('修改主表成功');
                        flag = true;
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
            return flag;
        }
    },
    // 保存关联关系
    save: function () {
        var dbResourceId = $('#bindRelModalBody').attr('rel-id');
        // 获取 [绑定关系] 页面内容
        var data = [];
        var siblings = $('#template').nextAll();
        if (siblings.length > 0) {
            for (var i = 0; i < siblings.length; i++) {
                var obj = {};
                var titleObj = $(siblings[i]).find('.tableNameTitle');
                obj['tableId'] = titleObj.attr('data-id');
                obj['tableCode'] = titleObj.attr('table-code');
                obj['tableName'] = titleObj.text();
                obj['relation'] = $(siblings[i]).find('.bindRelContent').text();
                obj['join'] = $(siblings[i]).find('.join').attr('join-value');
                obj['isMain'] = $(siblings[i]).find('.bindRel_idMain').css('display') == 'block' ? 1 : 0;
                obj['dbResourceId'] = dbResourceId;
                data.push(obj);
            }
        } else {
            failedMessager.show('关联表参数错误无法保存！');
            return;
        }
        $.ajax({
            url: webpath + '/dbresource/saveTableRelation',
            type: 'POST',
            dataType: "json",
            data: {'dbResourceId': dbResourceId, 'data': JSON.stringify(data)},
            beforeSend: function () {
                loading.show();
            },
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('保存成功！');
                    relationModal.clearAll();
                    relationModal.hide_bind();
                } else {
                    failedMessager.show('保存失败！');
                }
            },
            complete: function () {
                loading.hide();
            }
        });
    },
    // 退出 清理缓存
    clearAll: function () {
        $('#template').nextAll().remove(); // 先清空内容(除模版)
        $('#saveRel').removeClass('disabled');
        $('#addRelNextStep').addClass('disabled').removeAttr('dbResourceId');
    },
    // 初始化添加关联表首页
    initFirstPage: function () {
        $('#addRelationModal form')[0].reset();
        $('#relationInputs').find('input').removeAttr('disabled');
        $('#relationInputs').find('input').removeProp('checked');
    }
}

/**
 * 绑定关系 -> 编辑弹框
 */
var bindEdit = {
    // 展示弹框
    show: function (dbResourceId, mainTableId, relation) {
        if (dbResourceId != '' && mainTableId != '') {
            // 更新tree
            $.ajax({
                url: webpath + '/dbresource/getColumnTree',
                type: 'POST',
                dataType: "json",
                data: {
                    'dbResourceId': dbResourceId,
                    'tableId': mainTableId
                },
                success: function (data) {
                    var treeData = [];
                    if (data.length > 0) {
                        for (var i = 0; i < data.length; i++) {
                            treeData.push(getContent.createTreeNode(data[i]));
                        }
                    }
                    var myTree = $('#modal_tree').data('zui.tree');
                    myTree.reload(treeData); // reload tree数据
                    bindEdit.clearBind();
                    bindEdit.bindTreeDrag();
                    relationModal.hide_bind(); // 隐藏绑定关系弹框
                    $('#bindEditModal').modal({'show': 'center', "backdrop": "static"}); // 显示编辑弹框
                },
                error: function (data) {
                    failedMessager.show(data.msg);
                }
            });
            // 初始化relation
            if (relation != undefined && relation != '') {
                $('#bindEditModal_relation').text(relation);
            }
        } else {
            failedMessager.show('参数不全！');
        }
    },
    // 进入绑定关系弹框时初始化tree
    initTree: function (dbResourceId, mainTableId) {
        // 初始化tree
        $.ajax({
            url: webpath + '/dbresource/getColumnTree',
            type: 'POST',
            dataType: "json",
            data: {
                'dbResourceId': dbResourceId,
                'tableId': mainTableId
            },
            success: function (data) {
                var treeData = [];
                if (data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        treeData.push(getContent.createTreeNode(data[i]));
                    }
                }
                $('#modal_tree').tree({data: treeData, initialState: 'expand'});
                bindEdit.clearBind();
                bindEdit.bindTreeDrag();
            },
            error: function (data) {
                failedMessager.show(data.msg);
            }
        });
    },
    // 为treeNode绑定拖放事件
    bindTreeDrag: function () {
        $('.tree_childrenTableIcon').bind('dragstart', function (event) {
            event.originalEvent.dataTransfer.setData('data', '[' + $(this).attr('table-code') + '.' + $(this).children().text() + ']');
        });
        $('#bindEditModal_relation').bind('dragover', (function (event) {
            event.preventDefault();
        }));
        $('#bindEditModal_relation').bind('drop', (function (event) {
            event.preventDefault();
            $('#bindEditModal_relation').append(event.originalEvent.dataTransfer.getData('data'));
        }));
    },
    // 清除tree绑定事件避免重复绑定
    clearBind: function () {
        $('#bindEditModal_relation').unbind();
    },
    // 退出编辑弹框
    hide: function () {
        $('#bindEditModal').modal('toggle', 'center'); // 隐藏编辑弹框
        bindEdit.clearAll(); // 清空relation
        $('#modal_tree').empty(); // 清空tree
        relationModal.show_bind(); // 显示绑定关系弹框
    },
    // clear relation
    clearAll: function () {
        $('#bindEditModal_relation').text('');
    },
    // 验证
    verify: function (dbResourceId, relation) {
        $.ajax({
            url: webpath + '/dbresource/checkTableRelation',
            type: 'POST',
            dataType: "json",
            data: {
                'dbResourceId': dbResourceId,
                'data': relation
            },
            beforeSend: function () {
                loading.show();
            },
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('验证成功');
                } else {
                    failedMessager.show(data.msg);
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            },
            complete: function () {
                loading.hide();
            }
        });
    }
}

function initPage() {
    // 为主页面所有面板内的内容添加click事件
    $('.panel-body').on('click', '.panelElement_input span', function () {
        // contentHandle.colsConfig($(this).attr('tableId'), $(this).text(), $(this).attr('scanId'));
        contentHandle.colsConfig($(this).attr('tableId'), $(this).text(), 1);
    });
    $('.panel-body').on('click', '.panelElement_output span', function () {
        // contentHandle.colsConfig($(this).attr('tableId'), $(this).text(), $(this).attr('scanId'));
        contentHandle.colsConfig($(this).attr('tableId'), $(this).text(), 0);
    });
    $('.panel-body').on('click', '.panelElement_relation span', function () {
        contentHandle.relationConfig($(this).attr('tableId'));
    });

    /* ======= 输出表添加 ======= */
    $('.addButton').click(function () {
        addTableModal.show();
    });

    // 添加至输出表
    $('#addModalInsertButton').click(function () {
        addTableModal.insert();
    });

    /* ======= 扫描 ======= */
    $('.scanButton').click(function () {
        getAllDataSource();
        colsAlertModal.show($(this).attr('tableKind'));
    });

    /* 为扫描弹框内的数据源选项添加事件-->加载其下所有表 */
    $('#scanDatasourceList').change(function () {
        var dsId = $('#scanDatasourceList option:selected').attr('datasource-id');
        getTablesById(dsId);
    });

    /* 扫描数据源表 */
    $('#scanConfirm').click(function () {
        colsAlertModal.scanConfirm();
    });

    /* 列选择弹出框 - 全选 */
    $('#allSelectButton').click(function () {
        colsAlertModal.selector(1);
    });

    /* 列选择弹出框 - 全不选 */
    $('#allCancelButton').click(function () {
        colsAlertModal.selector(0);
    });

    /* 扫描选择列保存至表 */
    $('#scanSaveToTableButton').click(function () {
        colsAlertModal.save();
    });

    /* 取消扫描 */
    $('#scanCancelButton').click(function () {
        colsAlertModal.cancelScan();
    });

    /* 搜索 */
    $('#colsSearchBtn').click(function () {
        var input = $.trim($('#colsSearchInput').val());
        if (input == '') {
            $('#colsListContainer .checkbox_mainPage').show();
        } else {
            $('#colsListContainer .checkbox_mainPage').hide().filter(':contains(' + input + ')').show();
        }
    });

    /* ======= 关联关系 ======= */
    $('.addRelationButton').click(function () {
        relationModal.show();
    });

    /* 保存关联关系 */
    $('#saveRel').click(function () {
        relationModal.saveRelation();
    });

    /* 下一步 */
    $('#addRelNextStep').click(function () {
        relationModal.nextStep($(this).attr('dbResourceId'));
    });

    /* 绑定关系连接方式: 选择下拉框选项后同步到输出框内 */
    $('#bindRelModalBody .dropdown-menu li').click(function () {
        var joinValue = $(this).attr('join');
        $(this).parent().siblings('button').children('.join').attr('join-value', joinValue).text($(this).text());
    });

    /* 设为主表 */
    $('.bindRel_setMain').click(function () {
        if (relationModal.setMain($('#bindRelModalBody').attr('rel-id'), $(this).attr('data-id'))) { // 设置成功
            // 先将所有表状态置为副表状态: 显示设为主表, 按钮可编辑
            $('.bindRel_setMain').css('display', 'block');
            $('.bindRel_idMain').css('display', 'none');
            $('#bindRelModalBody button').removeClass('disabled');
            // 再单独设置当前表的状态: 主表, 隐藏设置按钮, 按钮不可编辑
            $(this).siblings('.bindRel_idMain').css('display', 'block');
            $(this).css('display', 'none');
            $(this).parent().parent().parent().find('button').addClass('disabled');
            // 更新main-id
            $('#bindRelModalBody').attr('main-id', $(this).attr('data-id'));
        } else {
            failedMessager.show('设置失败');
        }
    });

    /* 保存关联关系 */
    $('#saveBindRel').click(function () {
        relationModal.save();
    });

    /* ======= 关联关系 -> 编辑弹框 ======= */
    /* 点击编辑-展示弹框　*/
    $('.bindRel_editButton').click(function () {
        var relation = $(this).parent().parent().parent().find('.bindRelContent').text();
        bindEdit.show($('#bindRelModalBody').attr('rel-id'), $('#bindRelModalBody').attr('main-id'), relation);
        $('#bindEditConfirm').attr('click-tableId', $(this).attr('data-id')); // 用于编辑弹框保存回显
    });

    /* 清空 */
    $('#clearAll').click(function () {
        bindEdit.clearAll();
    });

    /* 点击运算符追加文本 */
    $('#operationSign span').click(function () {
        $('#bindEditModal_relation').append($(this).text());
    });

    /* 验证 */
    $('#verify').click(function () {
        bindEdit.verify($('#bindRelModalBody').attr('rel-id'), $('#bindEditModal_relation').text());
    });

    /* 取消 */
    $('#bindEditCancel').click(function () {
        bindEdit.hide();
    });

    /* 编辑保存之后回显到上级绑定关系页面 */
    $('#bindEditConfirm').click(function () {
        var tableId = $(this).attr('click-tableId');
        var newRel = $('#bindEditModal_relation').text();
        $(".bindPanelElement[id='" + tableId + "']").find('.bindRelContent').text('').text(newRel);
        bindEdit.hide();
    });

    // 新建关联关系-取消清除缓存数据
    $('#cancelSaveRel, #relBindCancel').click(function () {
        relationModal.clearAll();
        initContent();
    });
}

/**
 *  初始化规则包下所有的输入表/输出表/关联关系内容
 */
function initContent() {
    $.ajax({
        url: webpath + "/batchdata/metadata/tableList",
        type: 'POST',
        data: {'packageId': folderId},
        dataType: "json",
        success: function (data) {
            // 主页面
            var inputStr = '';
            var outputStr = '';
            var relationStr = '';
            // 添加关系页面
            var inputStr_relPage = '';
            var outputStr_relPage = '';
            for (var i = 0; i < data.length; i++) {
                var type = data[i].TABLEKIND;
                if (type == '1') { // 1 输入表
                    inputStr += getContent.createEleHtml(
                        'panelElement_input',
                        'icon-database',
                        data[i].TABLENAME,
                        data[i].TABLECODE,
                        type,
                        data[i].TABLEID,
                        data[i].SCANID
                    );
                    inputStr_relPage += getContent.createRelationEleHtml(data[i].TABLENAME, data[i].TABLECODE, data[i].TABLEID, data[i].DBID);
                } else if (type == '2') { // 2 输出表
                    outputStr += getContent.createEleHtml(
                        'panelElement_output',
                        'icon-table',
                        data[i].TABLENAME,
                        data[i].TABLECODE,
                        type,
                        data[i].TABLEID,
                        data[i].SCANID
                    );
                    outputStr_relPage += getContent.createRelationEleHtml(data[i].TABLENAME, data[i].TABLECODE, data[i].TABLEID, data[i].DBID);
                } else if (type == '3') { // 3 关联关系表
                    relationStr += getContent.createEleHtml(
                        'panelElement_relation',
                        'icon-link',
                        data[i].TABLENAME,
                        data[i].TABLECODE,
                        type,
                        data[i].TABLEID,
                        data[i].SCANID
                    );
                } else {
                    continue;
                }
            }
            // 主页面内容填充
            $('#inputContainer .panel-body').html(inputStr);
            $('#outputContainer .panel-body').html(outputStr);
            $('#relationContainer .panel-body').html(relationStr);

            // 添加关系页面内容填充, 仅可以添加输入表之间的关联关系
            $('#relationInputs').html(inputStr_relPage);
            // $('#relationOutputs').html(outputStr_relPage);
        }
    });
}

$(function () {
    initPage();
    initContent(); // 加载当前规则包下所有输入表/输出表/关联关系
});