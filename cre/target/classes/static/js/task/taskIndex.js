/**
 * 离线任务
 * data:2019/05/29
 * author:bambi
 */

/**
 * 构造页面内容数据
 */
var getContent = {
    // 初始化字段映射页面输入/输出变量下拉框 K1 输入变量 / K2 输出变量
    initVariable: function (ruleId) {
        // 先清空之前的数据
        $('#inputVariables').empty();
        $('#outputVariables').empty();
        // 输入变量
        $.ajax({
            url: webpath + '/variable/queryVariablesByRuleId/flat',
            type: 'POST',
            dataType: "json",
            // data: {'folderId': folderId, 'kindId': 'K1', "start": 0, "length": 10000},
            data: {'ruleId': ruleId},
            success: function (data) {
                if (data.data.length > 0) {
                    $('#inputVariables').html(getContent.createVariableEle(data.data));
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            },
            complete: function () {
                // 编辑回显, 隐藏已经绑定的变量选项
                // var variableSelected = $('#taskBindAlertModal').attr('variableSelected');
                var variableSelected = $('#taskBindAlertModal').attr('variableSelectedInput');
                if (variableSelected != undefined && variableSelected.length > 0) {
                    var arr = variableSelected.split(',');
                    for (var i = 0; i < arr.length - 1; i++) {
                        $("#inputVariables .variableLi[variableId='" + arr[i] + "']").addClass('hide');
                    }
                }
            }
        });
        // 输出变量
        $.ajax({
            url: webpath + '/variable/queryOutVariablesByRuleId/flat',
            type: 'POST',
            dataType: "json",
            // data: {'folderId': folderId, 'kindId': 'K2', "start": 0, "length": 10000},
            data: {'ruleId': ruleId},
            success: function (data) {
                if (data.data.length > 0) {
                    $('#outputVariables').html(getContent.createVariableEle(data.data));
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            },
            complete: function () {
                // 编辑回显, 隐藏已经绑定的变量选项
                // var variableSelected = $('#taskBindAlertModal').attr('variableSelected');
                var variableSelected = $('#taskBindAlertModal').attr('variableSelectedOutput');
                if (variableSelected != undefined && variableSelected.length > 0) {
                    var arr = variableSelected.split(',');
                    for (var i = 0; i < arr.length - 1; i++) {
                        $("#outputVariables > .variableLi[variableId='" + arr[i] + "']").addClass('hide');
                    }
                }
            }
        });
        // 同步至input框
        $('#inputVariables, #outputVariables').on('click', '.variableLi', function () {
            $(this).parent().siblings('input').val($(this).text()).attr('variableId', $(this).attr('variableId')).attr('variableCode', $(this).attr('variableCode')).attr('variableTypeId', $(this).attr('variableTypeId'));
        });
    },
    // 拼接输入/输出变量html
    createVariableEle: function (variableArr) {
        var htmlStr = '';
        for (var i = 0; i < variableArr.length; i++) {
            if (variableArr[i].typeId == '3') { // 过滤掉Object类型变量
                continue;
            }
            htmlStr += '<li class="variableLi" variableId=\'' + variableArr[i].variableId + '\' variableCode= \'' + variableArr[i].variableCode + '\' variableTypeId= \'' + variableArr[i].typeId + '\'><a>' + variableArr[i].variableAlias + '</a></li>';
        }
        return htmlStr;
    },
    // 初始化tree
    initTree: function (folderId) {
        var initialized = $('#taskTree').attr('initialized') == '1' ? true : false;
        $.ajax({
            url: webpath + "/batchdata/metadata/tableList",
            type: 'POST',
            data: {'packageId': folderId},
            dataType: "json",
            success: function (data) {
                var treeData = getContent.createWholeTree(data);
                if (!initialized) {
                    // 初始化tree数据
                    $('#taskTree').empty();
                    $('#taskTree').tree({data: treeData});
                    $('#taskTree').attr('initialized', '1');
                    getContent.addClassToLi();
                } else {
                    // 更新tree数据
                    var tree = $('#taskTree').data('zui.tree');
                    tree.reload(treeData);
                    getContent.addClassToLi();
                    tree.expand($('#taskTree').find('.rootLevel')); // 确保最外三层根节点展开
                    tree.collapse($('#taskTree').find('.firstLevel')); // 将tree所有的子li先全部折叠起来避免之后绑定的展开/折叠控制事件受影响
                }

                // 绑定拖放事件
                getContent.clearBind();
                getContent.bindTreeDrag();

                // [实体表] 展开/收起图标绑定事件
                $('#taskTree .icon_dsTable').parent().children('.list-toggle').click(function () {
                    getContent.createColsTree($(this).siblings('.icon_dsTable').attr('table-id'), $(this));
                });
                // [关联表] 展开/收起图标绑定事件
                $('#taskTree .icon_relTable').parent().children('.list-toggle').click(function () {
                    getContent.createRelTablesTree($(this).siblings('.icon_relTable').attr('relTableId'), $(this));
                });
                // [关联表-子表] 展开/收起图标绑定事件
                $('#taskTree').on('click', '.relChildTableLi .list-toggle', function () {
                    getContent.createColsTree($(this).siblings('.icon_relChildTable').attr('table-id'), $(this));
                });
            },
            complete: function () {
                // 编辑回显, 展开tree被选中的第一级目录, 如果被选中第二季目录, 在第一季的click事件中控制回显
                getContent.treeColsEcho_parentEcho($('#bindContent_input').attr('bindId'), $('#bindContent_input').attr('inputTableKind') == '3' ? true : false, true);
                getContent.treeColsEcho_parentEcho($('#bindContent_output').attr('bindId'), false, true);
            }
        });
    },
    // 编辑任务-字段tree回显选中
    treeColsEcho: function (idsStr) {
        if (idsStr != undefined && idsStr.length > 0) {
            var arr = idsStr.split(',');
            for (var i = 0; i < arr.length - 1; i++) {
                $("#taskTree .colSpan[colId='" + arr[i] + "']").siblings('input').prop('checked', true);
            }
        }
    },
    // 编辑回显, 先展开(click触发)被选中的列的表菜单以供后来选择准备
    treeColsEcho_parentEcho: function (bindId, isRelTable, isFirstOpen) {
        // 需要先找到父节点, 主动触发父节点的click事件再从子节点中寻找并选中
        if (bindId != undefined && bindId != '') {
            if (isFirstOpen) { // 第一次只展开父节点
                if (isRelTable) {
                    var relLi = $("#taskTree .firstLevel > .icon-table[relTableId='" + bindId + "']");
                    if (relLi.length > 0 && !relLi.parent().hasClass('open')) { // 找到符合条件且未展开的节点
                        relLi.siblings('.list-toggle').click(); // 先展开输入表第一级目录
                    }
                } else {
                    var tableLi = $("#taskTree .firstLevel > .icon-table[table-id='" + bindId + "']");
                    if (tableLi.length > 0 && !tableLi.parent().hasClass('open')) { // 找到符合条件且未展开的节点
                        tableLi.siblings('.list-toggle').click();
                    }
                }
            } else { // 第二次只展开是关系节点的子节点
                if (isRelTable) {
                    var relLi = $("#taskTree .firstLevel > .icon-table[relTableId='" + bindId + "']");
                    if (relLi.length > 0 && relLi.parent().hasClass('open')) { // 找到符合条件 & 父节点已展开 & 子表节点未展开 的节点
                        var childTableArr = relLi.siblings('ul').children('li');
                        for (var i = 0; i < childTableArr.length; i++) {
                            if (!$(childTableArr[i]).hasClass('open')) {
                                $(childTableArr[i]).children('.list-toggle').click();
                            }
                        }
                    }
                }
            }
        }
    },
    // 为每一级的li添加class
    addClassToLi: function () {
        $('.inputIcon').parent().addClass('rootLevel').children('ul').children('li').addClass('inputTableLi firstLevel').attr('tableKind', '1');
        $('.outputIcon').parent().addClass('rootLevel').children('ul').children('li').addClass('outputTableLi firstLevel').attr('tableKind', '2');
        $('.relIcon').parent().addClass('rootLevel').children('ul').children('li').addClass('relTableLi firstLevel').attr('tableKind', '3');
    },
    // 构造treeData
    createWholeTree: function (data) {
        var treeData = [];
        // 1级目录
        var inputTree = {
            'open': true,
            'html': '<i class="icon icon-database inputIcon"> 输入表</i>'
        };
        var relationTree = {
            'open': true,
            'html': '<i class="icon icon-database relIcon"> 关联关系表</i>'
        };
        var outputTree = {
            'open': true,
            'html': '<i class="icon icon-database outputIcon"> 输出表</i>'
        };
        var inputsArr = [];
        var outputsArr = [];
        var relationsArr = [];
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if (data[i].TABLEKIND == '1') { // 1 输入表
                    inputsArr.push(getContent.createDsTreeNode(data[i]));
                } else if (data[i].TABLEKIND == '2') { // 2 输出表
                    outputsArr.push(getContent.createDsTreeNode(data[i]));
                } else if (data[i].TABLEKIND == '3') { // 3 关联关系表
                    relationsArr.push(getContent.createRelTreeNode(data[i]));
                } else {
                    continue;
                }
            }
            inputTree['children'] = inputsArr;
            outputTree['children'] = outputsArr;
            relationTree['children'] = relationsArr;
        }
        treeData.push(inputTree, relationTree, outputTree);
        return treeData;
    },
    // 构造 输入/输出2级表目录
    createDsTreeNode: function (data) {
        var obj = {};
        // 输入输出表有dbType
        var htmlStr = '<i class="icon icon-table icon_dsTable" table-id=\'' + data.TABLEID + '\' table-code=\'' + data.TABLECODE + '\' dbId= \'' + data.DBID + '\' tableKind= \'' + data.TABLEKIND + '\' dbType= \'' + data.DBTYPE + '\'> <span>'
            + data.TABLECODE + '</span></i>';
        obj['html'] = htmlStr;
        obj['children'] = [{'title': '---'}]; // 先造一个假数据，后面再覆盖
        obj['open'] = false;
        return obj;
    },
    // 构造 关联关系2级表目录
    createRelTreeNode: function (data) {
        var obj = {};
        // 关联表没有dbType
        var htmlStr = '<i class="icon icon-table icon_relTable" relTableId=\'' + data.TABLEID + '\' table-code=\'' + data.TABLECODE + '\' tableKind="3"> <span>'
            + data.TABLECODE + '</span></i>';
        obj['html'] = htmlStr;
        obj['open'] = false;
        obj['children'] = [{'title': '---'}];
        return obj;
    },
    // 构造 关联表3级子表表目录
    createRelTablesTree: function (dbResourceId, currentThis) {
        var isOpened = currentThis.attr('isOpened') == '1' ? true : false;
        if (!isOpened) {
            $.ajax({
                url: webpath + '/dbresource/getTables',
                type: 'POST',
                dataType: "json",
                data: {'dbResourceId': dbResourceId},
                success: function (data) {
                    if (data.length > 0) {
                        var objArr = [];
                        for (var i = 0; i < data.length; i++) {
                            var childObj = {};
                            var htmlStr = '<i class="icon icon-table icon_relChildTable" table-id=\'' + data[i].TBLID + '\' table-code=\'' + data[i].TABLECODE + '\' dbId = \'' + data[i].DBID + '\'  tableKind="3"> <span>'
                                + data[i].TABLECODE + '</span></i>';
                            childObj['html'] = htmlStr;
                            childObj['open'] = false;
                            childObj['children'] = [{'title': '---'}];
                            objArr.push(childObj);
                        }
                        var myTree = $('#taskTree').data('zui.tree');
                        currentThis.parent().children('ul').empty(); // 先清空之前放进去的数据/假数据
                        myTree.add(currentThis.parent(), objArr);
                        currentThis.attr('isOpened', '1');
                        currentThis.parent().find('li').addClass('relChildTableLi'); // 为每一个关联关系子表li添加class
                    }
                },
                error: function (data) {
                    failedMessager.show(data.msg);
                }
            });
        } else {
            currentThis.parent().children('ul').empty(); // 先清空之前放进去的数据/假数据
            currentThis.attr('isOpened', '0');
        }
    },
    // 构造字段目录
    createColsTree: function (tableId, currentThis) {
        var isOpened = currentThis.attr('isOpened') == '1' ? true : false;
        if (!isOpened) {
            $.ajax({
                url: webpath + '/batchdata/column/columnList/all',
                type: 'POST',
                dataType: "json",
                data: {'tableId': tableId, 'columnName': ''},
                success: function (data) {
                    if (data.data.length > 0) {
                        var data = data.data;
                        var dataArr = [];
                        for (var i = 0; i < data.length; i++) {
                            var obj = {};
                            var htmlStr = '<input disabled class="colCheckInput" type="checkbox"/><span draggable="true" class="colSpan" colId=\'' + data[i].COLUMNID + '\'>' + data[i].COLUMNCODE + '</span>';
                            obj['html'] = htmlStr;
                            obj['open'] = true;
                            dataArr.push(obj);
                        }
                        var myTree = $('#taskTree').data('zui.tree');
                        currentThis.parent().children('ul').empty(); // 先清空之前放进去的数据/假数据
                        myTree.add(currentThis.parent(), dataArr);
                        $('.colCheckInput').css({'margin': '0 8px 0 -8px', 'cursor': 'pointer'});
                        $('.colSpan').css('color', '#333');
                        currentThis.attr('isOpened', '1');
                    }
                },
                error: function (data) {
                    failedMessager.show(data.msg);
                },
                complete: function () {
                    if ($('#bindContent_input').attr('inputTableKind') == '3') {
                        var idsStr = $('#taskBindAlertModal').attr('treeCheckedIds');
                        if (idsStr != undefined && idsStr.length > 0) {
                            var arr = idsStr.split(',');
                            for (var i = 0; i < arr.length - 1; i++) {
                                $("#taskTree .colSpan[colId='" + arr[i] + "']").siblings('input').prop('checked', true);
                            }
                        }
                    }
                }
            });
        } else {
            currentThis.parent().children('ul').empty(); // 先清空之前放进去的数据/假数据
            currentThis.attr('isOpened', '0');
        }
    },
    // 清除tree绑定事件避免重复绑定
    clearBind: function () {
        $('.dragTarget').unbind();
    },
    // 为拖动元素绑定拖放事件事件
    bindTreeDrag: function () {
        getContent.bindTreeDragTarget();
        $('#taskTree').on('dragstart', '.colSpan', function (event) {
            // 判断是否已被选中, 已选中状态下不能再次被选中
            if ($(this).siblings('input').prop('checked') == true) {
                $('.inputDragTarget').attr('disabled', true).unbind();
                $('.outputDragTarget').attr('disabled', true).unbind();
                failedMessager.show('该字段已被绑定，请重新选择列');
            }
            var colId = $(this).attr('colId');
            var colName = $(this).text();
            var tableKind = $(this).parents('.firstLevel').attr('tableKind');
            var dbId = '';
            var tableId = '';
            var tableCode = '';
            var relTableId = '';
            var dbType = ''; // dbType 输入输出表有, 关联表没有, 用来区分主键绑定页面
            if (tableKind == '3') {
                dbId = $(this).parents('.relChildTableLi').children('.icon-table').attr('dbId');
                tableId = $(this).parents('.relChildTableLi').children('.icon-table').attr('table-id');
                tableCode = $(this).parents('.relChildTableLi').children('.icon-table').attr('table-code');
                relTableId = $(this).parents('.firstLevel').children('.icon-table').attr('relTableId');
            } else {
                dbType = $(this).parents('.firstLevel').children('.icon-table').attr('dbType');
                dbId = $(this).parents('.firstLevel').children('.icon-table').attr('dbId');
                tableId = $(this).parents('.firstLevel').children('.icon-table').attr('table-id');
                tableCode = $(this).parents('.firstLevel').children('.icon-table').attr('table-code');
            }

            var flag = true;
            var bindId = '';
            // 控制可以拖放的目标区域
            if (tableKind == '2') { // 输出
                bindId = $('#bindContent_output').attr('bindId');
                flag = getContent.bindDragVrify(bindId, tableId);
                $('.inputDragTarget').attr('disabled', true).unbind();
            } else { // 输入
                bindId = $('#bindContent_input').attr('bindId');
                if (tableKind == '1') { // 输入表
                    flag = getContent.bindDragVrify(bindId, tableId);
                } else if (tableKind == '3') { // 关联关系表
                    flag = getContent.bindDragVrify(bindId, relTableId);
                }
                $('.outputDragTarget').attr('disabled', true).unbind();
            }
            // 校验不通过则两个区域都无法拖入
            if (!flag) {
                $('.inputDragTarget').attr('disabled', true).unbind();
                $('.outputDragTarget').attr('disabled', true).unbind();
            }
            event.originalEvent.dataTransfer.setData('colName', colName);
            event.originalEvent.dataTransfer.setData('colId', colId);
            event.originalEvent.dataTransfer.setData('dbId', dbId);
            event.originalEvent.dataTransfer.setData('tableId', tableId);
            event.originalEvent.dataTransfer.setData('tableCode', tableCode);
            event.originalEvent.dataTransfer.setData('tableKind', tableKind);
            event.originalEvent.dataTransfer.setData('relTableId', relTableId);
            event.originalEvent.dataTransfer.setData('dbType', dbType);
        });
        //  拖放元素结束拖放时恢复input框状态
        $('#taskTree').on('dragend', '.colSpan', function () {
            $('.outputDragTarget, .inputDragTarget').removeAttr('disabled');
            getContent.bindTreeDragTarget();
        });
    },
    // 为拖放目标元素绑定事件
    bindTreeDragTarget: function () {
        $('.dragTarget').bind('dragover', function (event) {
            event.preventDefault();
        });
        $('.dragTarget').bind('drop', function (event) {
            event.preventDefault();
            var dataTransfer = event.originalEvent.dataTransfer;
            $(this).val(dataTransfer.getData('colName')).attr('colId', dataTransfer.getData('colId'))
                .attr('dbId', dataTransfer.getData('dbId'))
                .attr('tableId', dataTransfer.getData('tableId'))
                .attr('tableCode', dataTransfer.getData('tableCode'))
                .attr('tableKind', dataTransfer.getData('tableKind'))
                .attr('relTableId', dataTransfer.getData('relTableId'))
                .attr('dbType', dataTransfer.getData('dbType'));
            $(this).siblings('label').children('.inputColIcon').css('display', 'none');
            $(this).siblings('label').children('.cancelIcon').css('display', 'inline');
        });
    },
    // 验证是否可以拖入目标区域
    bindDragVrify: function (bindId, targetId) {
        var flag = true;
        if (!(bindId == undefined || bindId == '')) {
            if (bindId != targetId) {
                // 打断拖动事件, 提示
                failedMessager.show('请选择同一实体表/关联表下的字段进行绑定！');
                flag = false;
            }
        }
        return flag;
    },
    // 主键绑定页面: 初始化输入/输出下拉框
    initKeyCols: function (tableId, container, isRelTable, tableCode, isInputTable, isHbase) {
        var span = ' ' + tableCode + ' ';
        if (isInputTable) {
            $('#keyBind_inputTableCodeSpan').empty().html(span);
        } else {
            $('#keyBind_outputTableCodeSpan').empty().html(span);
        }
        if (isRelTable) {
            // 关联关系表输入
            $.ajax({
                url: webpath + '/dbresource/getTables',
                type: 'POST',
                dataType: "json",
                data: {'dbResourceId': tableId},
                success: function (data) {
                    if (data.length > 0) {
                        var filterHtml = '';
                        for (var i = 0; i < data.length; i++) { // 获取子表找到主表
                            filterHtml += '<li tableId=\'' + data[i].TBLID + '\' onclick="getContent.initFilterCols(\'' + data[i].TBLID + '\', \'' + isHbase + '\')"><a>' + data[i].TABLECODE + '</a></li>';
                            if (data[i].ISMAIN == '1') {
                                getContent.initKeyCols(data[i].TBLID, container, false, data[i].TABLECODE, true, false);
                            }
                        }
                        $('#filterTablesSelector').empty().html(filterHtml);
                    }
                },
                error: function (data) {
                    failedMessager.show(data.msg);
                }
            });
        } else {
            if (isInputTable) {
                var filterHtml = '<li tableId=\'' + tableId + '\' onclick="getContent.initFilterCols(\'' + tableId + '\', false)"><a>' + tableCode + '</a></li>';
                $('#filterTablesSelector').empty().html(filterHtml);
                if (isHbase) { // 默认选中主键且不允许更改
                    $('#filterInputTableInput').val($('#filterTablesSelector').first().text());
                    getContent.initFilterCols(tableId, true);
                    $('#keyColBind_inputTableSelector, #filterInputTableInput, #filterInputColInput').attr('disabled', true);
                    $('#filterTablesSelector, #filterColsSelector').siblings('label').css('display', 'none');
                }
            }
            // 非关联关系表输入/输出
            $.ajax({
                url: webpath + '/batchdata/column/columnList',
                type: 'POST',
                dataType: "json",
                data: {'tableId': tableId, 'columnName': ''},
                success: function (data) {
                    if (data.data.length > 0) {
                        container.html('');
                        var data = data.data;
                        var htmlStr = '';
                        for (var i = 0; i < data.length; i++) {
                            htmlStr += '<option col-id=\'' + data[i].COLUMNID + '\' table-code=\'' + tableCode + '\'>' + data[i].COLUMNCODE + '</option>';
                        }
                        container.html(htmlStr);
                    }
                },
                error: function (data) {
                    failedMessager.show(data.msg);
                }
            });
        }
    },
    // 过滤条件:加载选中表的字段至下拉框
    initFilterCols: function (tableId, isHbase) {
        $.ajax({
            url: webpath + '/batchdata/column/columnList',
            type: 'POST',
            dataType: "json",
            data: {'tableId': tableId, 'columnName': ''},
            success: function (data) {
                if (data.data.length > 0) {
                    var data = data.data;
                    var htmlStr = '';
                    for (var i = 0; i < data.length; i++) {
                        htmlStr += '<li col-id=\'' + data[i].COLUMNID + '\' col-code=\'' + data[i].COLUMNCODE + '\'><a>' + data[i].COLUMNCODE + '</a></li>';
                    }
                    $('#filterColsSelector').empty().html(htmlStr);
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            },
            complete: function () {
                if (isHbase) { // 如果是hbase则选中
                    $('#filterInputColInput').val('RowKey');
                }
            }
        });
    }
}

/**
 * 新建/编辑任务弹框
 */
var alertModal = {
    // 展开基本信息弹框
    showMain: function () {
        $('#taskMainAlertModal form')[0].reset(); // 清空表单
        $('#taskMainAlertModal').modal({'show': 'center', "backdrop": "static"});
        $('#saveTask').attr('isAddHandle', '1'); // 标识保存类型为新建
    },
    // 编辑任务时展示弹框
    editShowMain: function () {
        $('#taskMainAlertModal').modal({'show': 'center', "backdrop": "static"});
        $('#saveTask').attr('isAddHandle', '0'); // 标识保存类型为更新
    },
    // 退出整个页面前清理所有缓存数据
    clearAllCache: function (closeModal) {
        alertModal.clearAllBindEle('0');
        $('.bindModal_right_ele input').val('');
        // $('#taskBindAlertModal').removeAttr('variableSelected').removeAttr('treeCheckedIds');
        $('#taskBindAlertModal').removeAttr('variableSelectedInput').removeAttr('variableSelectedOutput').removeAttr('treeCheckedIds');
        $('#taskMainAlertModal form')[0].reset(); // 重置表单
        getRules($('#packageSelectors option').first().attr('folderId'), 1); // 重置规则下拉框
        $('#cron_tip').css('display', 'none');
        $('#keyColBindAlertModal').removeAttr('inputTableId').removeAttr('outputTableId').removeAttr('keyMapId');
        $('#keyColBind_filterContainer').attr('isFormal', '1');
        $('.filter_hbase').css('display', 'none');
        $('.filter_formal').css('display', 'inline-block');
        closeModal.modal('toggle', 'center');
        // initTable();
        mainPage.searchTask();
    },
    // 下一步 -> 字段映射
    nextStep: function () {
        if (!$('#taskMainAlertModal form').isValid()) {
            return;
        }
        if ($('#cron_tip').css('display') != 'none') {
            failedMessager.show('请输入合法cron表达式！');
            return;
        }
        // 检查scan参数是否规范(>0的整数)
        if (!isInteger($('#scanOffsetFormGroup input').val())) {
            failedMessager.show('任务起始位置：请输入>0的整数！');
            $('#scanOffsetFormGroup input').focus();
            return;
        }
        if (!isInteger($('#scanMaxSizeFormGroup input').val())) {
            failedMessager.show('单次任务处理最大数：请输入>0的整数！');
            $('#scanMaxSizeFormGroup input').focus();
            return;
        }
        // 2. 初始化映射页面内容
        alertModal.initBindPage();
        $('#taskMainAlertModal').modal('toggle', 'center');
        $('#taskBindAlertModal').modal({'show': 'center', "backdrop": "static"});

        // 弹框shown事件
        $('#taskBindAlertModal').on('shown.zui.modal', function () {
            // 如果是关联关系表就展开关联关系表的子表
            getContent.treeColsEcho_parentEcho($('#bindContent_input').attr('bindId'), $('#bindContent_input').attr('inputTableKind') == '3' ? true : false, false);
            // 编辑任务: tree选中状态回显(弹框加载后再选, 因为中间有ajax请求的关系, 在加载完成前无法选中)
            var treeCheckedIds = $('#taskBindAlertModal').attr('treeCheckedIds');
            if (treeCheckedIds != undefined && treeCheckedIds.length > 0) {
                getContent.treeColsEcho(treeCheckedIds);
            }
        })
    },
    // 上一步 -> 基本信息
    lastStep: function () {
        // 清除之前回显的缓存数据
        // $('#taskBindAlertModal').removeAttr('variableSelected');
        $('#taskBindAlertModal').removeAttr('variableSelectedInput');
        $('#taskBindAlertModal').removeAttr('variableSelectedOutput');
        $('#taskBindAlertModal').removeAttr('treeCheckedIds');

        // 1. 获取当前页面绑定元素的信息并保存至taskBindAlertModal的属性中, 以供下次通过下一步进入页面后的数据回显　
        var colsCheckedArr = $("#taskTree .colSpan").siblings('input:checked'); // 1-1 获取tree所有选中的列的colId
        if (colsCheckedArr.length > 0) {
            var cols = '';
            for (var i = 0; i < colsCheckedArr.length; i++) {
                cols += $(colsCheckedArr[i]).siblings('.colSpan').attr('colId') + ',';
            }
            $('#taskBindAlertModal').attr('treeCheckedIds', cols);
        }
        // var variableSelectedArr = $(".rightEle_inputs .hide"); // 1-2 获取所有隐藏起来的变量id
        // if (variableSelectedArr.length > 0) {
        //     var variableIds = '';
        //     for (var i = 0; i < variableSelectedArr.length; i++) {
        //         variableIds += $(variableSelectedArr[i]).attr('variableId') + ',';
        //     }
        //     $('#taskBindAlertModal').attr('variableSelected', variableIds);
        // }

        var variableSelectedArr_input = $(".rightEle_inputs_input .hide"); // 1-2 获取所有隐藏起来的输入变量id
        if (variableSelectedArr_input.length > 0) {
            var variableIds_input = '';
            for (var i = 0; i < variableSelectedArr_input.length; i++) {
                variableIds_input += $(variableSelectedArr_input[i]).attr('variableId') + ',';
            }
            $('#taskBindAlertModal').attr('variableSelectedInput', variableIds_input);
        }

        var variableSelectedArr_output = $(".rightEle_inputs_output .hide"); // 1-3 获取所有隐藏起来的输出变量id
        if (variableSelectedArr_output.length > 0) {
            var variableIds_output = '';
            for (var i = 0; i < variableSelectedArr_output.length; i++) {
                variableIds_output += $(variableSelectedArr_output[i]).attr('variableId') + ',';
            }
            $('#taskBindAlertModal').attr('variableSelectedOutput', variableIds_output);
        }

        // 2. 显示弹框
        $('#taskBindAlertModal').modal('toggle', 'center');
        $('#taskMainAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 下一步 -> 主键绑定
    showKeyBind: function () {
        // 1. 检查绑定参数是否为空
        if (($('#bindContent_input').children().length <= 0) || ($('#bindContent_output').children().length <= 0)) {
            failedMessager.show('请至少绑定一个输入和输出参数！');
            return;
        }
        // 2. 比对上次绑定id是否有更新 --> 有则更新并记录新的id; 无则不更新
        var updateFlag = true; // 默认更新
        var lastInputId = $('#keyColBindAlertModal').attr('inputTableId');
        var lastOutputId = $('#keyColBindAlertModal').attr('outputTableId');
        var inputId = $('#bindContent_input').attr('bindId');
        var outputId = $('#bindContent_output').attr('bindId');
        if (lastInputId != undefined && lastInputId != '' && lastOutputId != undefined && lastOutputId != '') {
            if (lastInputId == inputId && lastOutputId == outputId) {
                updateFlag = false;
            }
        }
        if (updateFlag) {
            alertModal.initKeyPage();
            // 更新id
            $('#keyColBindAlertModal').attr('inputTableId', inputId);
            $('#keyColBindAlertModal').attr('outputTableId', outputId);
            // 为每一个ul下拉列表绑定点击事件同步至input框内
            $('.filterEle .dropdown-menu').on('click', 'a', function () {
                $(this).parent().parent().siblings('input').val($(this).text());
            })
        }
        // 3. 显示弹框
        $('#taskBindAlertModal').modal('toggle', 'center');
        $('#keyColBindAlertModal').modal({'show': 'center', "backdrop": "static"});

        // 弹框shown事件, 选中
        $('#keyColBindAlertModal').on('shown.zui.modal', function () {
            var inputColumnId = $('#keyColBindAlertModal').attr('inputColumnId');
            var outputColumnId = $('#keyColBindAlertModal').attr('outputColumnId');
            if (inputColumnId != undefined && inputColumnId != '' && outputColumnId != undefined && outputColumnId != '') {
                $("#keyColBind_inputTableSelector option[col-id='" + inputColumnId + "']").prop('selected', true);
                $("#keyColBind_outputTableSelector option[col-id='" + outputColumnId + "']").prop('selected', true);
                $('#keyColBindAlertModal').removeAttr('inputColumnId');
                $('#keyColBindAlertModal').removeAttr('outputColumnId');
            }
        })
    },
    // 初始化主键绑定页面
    initKeyPage: function () {
        // 先恢复初始化的布局
        $('.filter_formal').css('display', 'inline-block');
        $('.filter_hbase').css('display', 'none');
        $('#keyColBind_filterContainer').attr('isFormal', '1');
        $('#keyColBind_inputTableSelector, #filterInputTableInput, #filterInputColInput').removeAttr('disabled');
        $('#filterTablesSelector, #filterColsSelector').siblings('label').css('display', 'block');

        // 获取输入输出字段填充(根据字段映射页面的输入输出表id)
        $('#filterTablesSelector, #filterColsSelector').empty(); // 清空 表&字段 下拉框
        $('.filterEle input').val(''); // 清空过滤条件的input
        var inputId = $('#bindContent_input').attr('bindId');
        var outputId = $('#bindContent_output').attr('bindId');
        var isRelTable = $('#bindContent_input').attr('inputTableKind') == '3' ? true : false;
        var inputTableCode = '';
        var outputTableCode = $('#bindContent_output span').first().attr('tableCode');
        var isHbase = $('#bindContent_input span').first().attr('dbType') == '8' ? true : false;
        if (!isRelTable) {
            // 非关联关系表: 查看是否dbType=8(hbase), 是则显示hbase特有的页面
            if (isHbase) {
                $('.filter_formal').css('display', 'none');
                $('.filter_hbase').css('display', 'inline-block');
                $('#keyColBind_filterContainer').attr('isFormal', '0');
            }
            inputTableCode = $('#bindContent_input span').first().attr('tableCode');
        }
        getContent.initKeyCols(inputId, $('#keyColBind_inputTableSelector'), isRelTable, inputTableCode, true, isHbase);
        getContent.initKeyCols(outputId, $('#keyColBind_outputTableSelector'), false, outputTableCode, false, isHbase);
    },
    // 上一步 -> 字段映射
    hideKeyBind: function () {
        $('#keyColBindAlertModal').modal('toggle', 'center');
        $('#taskBindAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 初始化字段映射页面数据: 初始化tree & 初始化变量下拉框 & 记录规则包id信息
    initBindPage: function () {
        // 记录folderId, 与上一次的进行比对，一样则不清除状态， 否则清除所有状态
        var folderId = $('#packageSelectors option:selected').attr('folderId');
        // var flag = $('#taskBindAlertModal').attr('lastFolderId') == folderId;

        var ruleId = $('#versionSelector option:selected').attr('ruleId');
        var flag = $('#taskBindAlertModal').attr('lastRuleId') == ruleId;

        if (!flag) {
            // $('#taskBindAlertModal').attr('lastFolderId', folderId);
            $('#taskBindAlertModal').attr('lastRuleId', ruleId);
            alertModal.clearAllBindEle('0');
        }
        getContent.initTree(folderId);
        // getContent.initVariable(folderId);
        getContent.initVariable(ruleId);
    },
    // 新建离线任务
    saveTask: function () {
        // ajax请求保存数据 isAddHandle(1新建 0更新)标识操作类型
        var isAddHandle = $('#saveTask').attr('isAddHandle');
        if (!isFilterLegal()) { // 如果过滤条件不为空, 验证过滤条件内容
            failedMessager.show('过滤条件：请勿输入非法字段！');
            $('#saveTask').attr('isClicked', '0');
            return;
        }
        if (isAddHandle == '1') {
            var obj = alertModal.getTaskData(false); // 获取所有数据拼接数据
            $.ajax({
                url: webpath + '/task/save',
                type: 'POST',
                contentType: 'application/json', // 定义发送给服务器的数据格式
                dataType: "json",
                data: JSON.stringify(obj),
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('保存成功！');
                        alertModal.clearAllCache($('#keyColBindAlertModal'));
                    } else {
                        failedMessager.show('保存失败！' + data.msg);
                    }
                },
                error: function (data) {
                    failedMessager.show(data.msg);
                },
                beforeSend: function () {
                    loading.show();
                },
                complete: function () {
                    $('#saveTask').attr('isClicked', '0');
                    loading.hide();
                }
            });
        } else if (isAddHandle == '0') {
            var taskId = $('#saveTask').attr('task-id');
            if (taskId != undefined && taskId != '') {
                var obj = alertModal.getTaskData(true, taskId); // 获取所有数据拼接数据
                $.ajax({
                    url: webpath + '/task/update',
                    type: 'POST',
                    contentType: 'application/json',
                    dataType: "json",
                    data: JSON.stringify(obj),
                    success: function (data) {
                        if (data.status == 0) {
                            successMessager.show('更新成功！');
                            alertModal.clearAllCache($('#keyColBindAlertModal'));
                        } else {
                            failedMessager.show('更新失败！' + data.msg);
                        }
                    },
                    error: function (data) {
                        failedMessager.show(data.msg);
                    },
                    beforeSend: function () {
                        loading.show();
                    },
                    complete: function () {
                        $('#saveTask').attr('isClicked', '0');
                        loading.hide();
                    }
                });
            }
        } else {
            $('#saveTask').attr('isClicked', '0');
            failedMessager.show('错误参数：isAddHandle');
        }
    },
    // 获取弹出框所有数据拼接成obj
    getTaskData: function (isUpdate, taskId) {
        var obj = {};
        // 任务基本信息
        var inputArr = $('#taskMainAlertModal form input');
        for (var i = 0; i < inputArr.length; i++) {
            obj[$(inputArr[i]).attr('col-name')] = $(inputArr[i]).val();
        }
        var packageSelected = $('#packageSelectors option:selected');
        var ruleSelected = $('#ruleSelectors option:selected');
        var versionSelected = $('#versionSelector option:selected');
        obj['packageId'] = packageSelected.attr('folderId');
        obj['packageName'] = packageSelected.val();
        obj['ruleName'] = ruleSelected.val();
        obj['ruleId'] = versionSelected.attr('ruleId');
        obj['ruleVersion'] = $('#versionSelector option:selected').val();
        if (isUpdate) {
            obj['taskId'] = taskId;
        }
        // 绑定参数信息
        obj = alertModal.createVariableMaps($('#bindContent_input span'), obj, true, isUpdate);
        obj = alertModal.createVariableMaps($('#bindContent_output span'), obj, false, isUpdate);
        // 主键映射信息
        obj['columnMapping'] = alertModal.getKeyBindData(isUpdate, taskId);
        obj['inputTableKind'] = $('#bindContent_input').attr('inputTableKind');
        obj['inputTableWhereClause'] = ' ' + $.trim($('#filterContent').text()) + ' ';
        return obj;
    },
    // 获取绑定参数数据
    createVariableMaps: function (target, parentObj, isInput, isUpdate) {
        if (target.length > 0) {
            var objArr = [];
            for (var i = 0; i < target.length; i++) {
                var obj = {};
                obj['variableId'] = $(target[i]).attr('variableId');
                obj['variableCode'] = $(target[i]).attr('variableCode');
                obj['variableName'] = $(target[i]).attr('variableName');
                obj['variableKind'] = $(target[i]).attr('variableKind');
                obj['dbId'] = $(target[i]).attr('dbId');
                obj['tableId'] = $(target[i]).attr('tableId');
                obj['tableCode'] = $(target[i]).attr('tableCode');
                obj['columnId'] = $(target[i]).attr('columnId');
                obj['columnCode'] = $(target[i]).attr('colCode');
                obj['variableTypeId'] = $(target[i]).attr('variableTypeId');
                if (isUpdate) {
                    obj['taskId'] = parentObj.taskId;
                }
                objArr.push(obj);
            }
            // 其他信息
            if (isInput) { // 输入
                parentObj['inputTableId'] = $('#bindContent_input').attr('bindId'); // 绑定关联关系表id/实体输入表id
                parentObj['inputTableCode'] = $(target[0]).attr('tableCode');
                parentObj['inputVariableMappings'] = objArr;
                parentObj['inputDbType'] = $(target[0]).attr('dbType');
            } else { // 输出
                parentObj['outputTableId'] = $(target[0]).attr('tableId');
                parentObj['outputTableCode'] = $(target[0]).attr('tableCode');
                parentObj['outputVariableMappings'] = objArr;
                parentObj['outputDbType'] = $(target[0]).attr('dbType');
            }
        }
        return parentObj;
    },
    // 获取主键映射信息
    getKeyBindData: function (isUpdate, taskId) {
        var obj = {};
        if (isUpdate) {
            obj['taskId'] = taskId;
            obj['id'] = $('#keyColBindAlertModal').attr('keyMapId');
        }
        obj['inputTableId'] = $('#bindContent_input').attr('bindId');
        obj['inputTableCode'] = $('#keyColBind_inputTableSelector option:selected').attr('table-code');
        obj['inputColumnId'] = $('#keyColBind_inputTableSelector option:selected').attr('col-id');
        obj['inputColumnCode'] = $('#keyColBind_inputTableSelector option:selected').val();

        obj['outputTableId'] = $('#bindContent_output').attr('bindId');
        obj['outputTableCode'] = $('#keyColBind_outputTableSelector option:selected').attr('table-code');
        obj['outputColumnId'] = $('#keyColBind_outputTableSelector option:selected').attr('col-id');
        obj['outputColumnCode'] = $('#keyColBind_outputTableSelector option:selected').val();
        return obj;
    },
    // 输入/输出字段删除
    cancelInput: function (tableKind) {
        if (tableKind == '1') {
            $('.inputDragTarget').val('');
        } else if (tableKind == '2') {
            $('.outputDragTarget').val('');
        }
    },
    // 添加一个字段绑定信息至参数栏
    addBindEle: function (tableKind) {
        // 1. 进行元素绑定
        if (tableKind == '1') { // 输入
            var variableAlias = $('#variableCode_input').val();
            var colCode = $('#columnCode_input').val();
            if (variableAlias == '' || colCode == '') {
                failedMessager.show('参数不全无法绑定, 请补全参数！');
            } else {
                $('#bindContent_input').append(alertModal.createBindEle(
                    $('#columnCode_input').attr('colId'),
                    colCode,
                    variableAlias,
                    $('#variableCode_input').attr('variableId'),
                    $('#variableCode_input').attr('variableCode'),
                    'K1',
                    $('#columnCode_input').attr('dbId'),
                    $('#columnCode_input').attr('tableId'),
                    $('#columnCode_input').attr('tableCode'),
                    $('#columnCode_input').attr('tableKind'),
                    '',
                    $('#columnCode_input').attr('dbType'),
                    $('#variableCode_input').attr('variableTypeId')
                ));
                // 2. 清空input, 控制变量不可再次选择
                $("#inputVariables > .variableLi[variableId='" + $('#variableCode_input').attr('variableId') + "']").addClass('hide');
                $('#variableCode_input, #columnCode_input').val('');

                // 3. 标识tree被选列
                $(".colSpan[colId='" + $('#columnCode_input').attr('colId') + "']").siblings('input').prop('checked', true);
                // 标识已绑定的数据源id(输出表: tableId, 关联关系表: relId) ---> 用于限制只能从同一个目录下勾选列
                if ($('#columnCode_input').attr('tableKind') == '3') { // 关联关系表用relId标识
                    $('#bindContent_input').attr('inputTableKind', '3');
                    $('#bindContent_input').attr('bindId', $('#columnCode_input').attr('relTableId'));
                } else {
                    $('#bindContent_input').attr('inputTableKind', '1');
                    $('#bindContent_input').attr('bindId', $('#columnCode_input').attr('tableId'));
                }

                // 4. 图标变化
                $(".cancelIcon[tableKind='1']").css('display', 'none').siblings('.inputColIcon').css('display', 'inline');
                // 5. 清除属性
                $('#columnCode_input').removeAttr('dbId tableId tableCode tableKind colId dbType relTableId');
                $('#variableCode_input').removeAttr('variableId variableCode variableTypeId');
            }
        } else if (tableKind == '2') { // 输出
            var variableAlias = $('#variableCode_output').val();
            var colCode = $('#columnCode_output').val();
            if (variableAlias == '' || colCode == '') {
                failedMessager.show('参数不全无法绑定, 请补全参数！');
            } else {
                $('#bindContent_output').append(alertModal.createBindEle(
                    $('#columnCode_output').attr('colId'),
                    colCode,
                    variableAlias,
                    $('#variableCode_output').attr('variableId'),
                    $('#variableCode_output').attr('variableCode'),
                    'K2',
                    $('#columnCode_output').attr('dbId'),
                    $('#columnCode_output').attr('tableId'),
                    $('#columnCode_output').attr('tableCode'),
                    $('#columnCode_output').attr('tableKind'),
                    '',
                    $('#columnCode_output').attr('dbType'),
                    $('#variableCode_output').attr('variableTypeId')
                ));
                // 2. 清空input, 控制变量不可再次选择
                $("#outputVariables > .variableLi[variableId='" + $('#variableCode_output').attr('variableId') + "']").addClass('hide');
                $('#variableCode_output, #columnCode_output').val('');

                // 3. 标识tree被选列
                $(".colSpan[colId='" + $('#columnCode_output').attr('colId') + "']").siblings('input').prop('checked', true);
                // 标识已绑定的数据源id: tableId ---> 用于限制只能从同一个目录下勾选列
                $('#bindContent_output').attr('bindId', $('#columnCode_output').attr('tableId'));

                // 4. 图标变化
                $(".cancelIcon[tableKind='2']").css('display', 'none').siblings('.inputColIcon').css('display', 'inline');
                // 5. 清除属性
                $('#columnCode_output').removeAttr('dbId tableId tableCode tableKind colId dbType relTableId');
                $('#variableCode_output').removeAttr('variableId variableCode variableTypeId');
            }
        }
    },
    // 构造绑定元素内容
    createBindEle: function (columnId, colCode, variableAlias, variableId, variableCode, variableKind, dbId, tableId, tableCode, tableKind, taskId, dbType, variableTypeId) {
        var htmlStr = '<span class="label label-primary" ' +
            'columnId=\'' + columnId + '\' colCode=\'' + colCode + '\' ' +
            'variableId=\'' + variableId + '\' variableCode=\'' + variableCode + '\' ' +
            'variableKind=\'' + variableKind + '\' dbId=\'' + dbId + '\' ' +
            'tableId=\'' + tableId + '\' tableCode=\'' + tableCode + '\' ' +
            'tableKind=\'' + tableKind + '\' variableName=\'' + variableAlias + '\' ' +
            'taskId=\'' + taskId + '\' dbType=\'' + dbType + '\' variableTypeId=\'' + variableTypeId + '\'>'
            + variableAlias + ' - ' + colCode
            + '<i class="bindEleCancel icon icon-times"></i></span>';
        // 绑定清除事件
        $('#bindModal_right').on('click', '.bindEleCancel', function () {
            alertModal.bindEleCancel($(this));
        });
        return htmlStr;
    },
    // 清除单个绑定参数
    bindEleCancel: function (that) {
        var colId = that.parent('span').attr('columnId');
        var variableKind = that.parent('span').attr('variableKind');
        var variableId = that.parent('span').attr('variableId');
        // 恢复左侧tree复选框状态
        $(".colSpan[colId='" + colId + "']").siblings('input').removeProp('checked');
        // 恢复变量列表展示状态, 如果绑定区域为空则清除区域的bindId
        if (variableKind == 'K1') {
            $("#inputVariables > .variableLi[variableId='" + variableId + "']").removeClass('hide');
            if ($('#bindContent_input').children().length == 0) {
                $('#bindContent_input').removeAttr('bindId');
                $('#bindContent_input').removeAttr('inputTableKind');
            }
        }
        if (variableKind == 'K2') {
            $("#outputVariables > .variableLi[variableId='" + variableId + "']").removeClass('hide');
            if ($('#bindContent_output').children().length == 0) {
                $('#bindContent_output').removeAttr('bindId');
            }
        }
        // 清除当前绑定元素
        that.parent().remove();
    },
    // 清除所有绑定元素并恢复所有状态: 1 输入 / 2 输出 / 全部
    clearAllBindEle: function (type) {
        if (type == '1') { // 输入
            var tableKind = $('#bindContent_input').attr('inputTableKind');
            $(".firstLevel[tableKind='" + tableKind + "']").find('.colSpan').siblings('input').removeProp('checked');
            $('#inputVariables .variableLi').removeClass('hide');
            $('#bindContent_input').empty().removeAttr('bindId');
            $('#bindContent_input').removeAttr('inputTableKind');
        }
        if (type == '2') { // 输出
            $(".firstLevel[tableKind='2']").find('.colSpan').siblings('input').removeProp('checked');
            $('#outputVariables .variableLi').removeClass('hide');
            $('#bindContent_output').empty().removeAttr('bindId');
        }
        if (type == '0') {
            // 清除全部状态
            $('.colSpan').siblings('input').removeProp('checked');
            $('.variableLi').removeClass('hide');
            $('#bindContent_input, #bindContent_output').empty().removeAttr('bindId');
            $('#bindContent_input').removeAttr('inputTableKind');
            $('#keyColBind_inputTableSelector').empty();
            $('#keyColBind_outputTableSelector').empty();
        }
    },
    // 编辑任务
    editTask: function (taskId) {
        // 获取任务数据
        if (taskId != undefined && taskId != '') {
            $('#saveTask').attr('task-id', taskId); // 更新绑定taskId
            // 修改任务权限 authCheck
            $.ajax({
                url: webpath + '/task/update/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {'taskId': taskId},
                success: function (data) {
                    if (data.status === 0) {
                        $.ajax({
                            url: webpath + '/task/detail',
                            type: 'POST',
                            dataType: "json",
                            data: {'taskId': taskId},
                            success: function (data) {
                                if (data.data != {}) {
                                    if (alertModal.editDataEcho(data.data)) { // 数据回显至modal
                                        alertModal.editShowMain(); // 展开modal
                                    }
                                }
                            },
                            error: function (data) {
                                failedMessager.show(data.msg);
                            },
                            beforeSend: function () {
                                loading.show();
                            },
                            complete: function () {
                                loading.hide();
                            }
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        }
    },
    // 任务数据回显
    editDataEcho: function (data) {
        // 1. 普通信息回显
        $("#packageSelectors option[folderId='" + data.packageId + "']").prop('selected', true);
        // getRules(data.packageId, 1, data.ruleName, data.ruleId); // 规则及版本回显
        getRules(data.packageId, 1, data.modelId, data.ruleId); // 规则及版本回显
        $('#taskNameFormGroup input').val(data.taskName);
        $('#cronFormGroup input').val(data.cron);
        $('#scanOffsetFormGroup input').val(data.scanOffset);
        $('#scanMaxSizeFormGroup input').val(data.scanMaxSize);
        $('#describeFormGroup input').val(data.describe);
        $('#scanNextOffsetFormGroup input').val(data.scanNextOffset);
        $('#scanNextRowKeyFormGroup input').val(data.scanNextRowKey);
        // 绑定页面folderId回显
        $('#taskBindAlertModal').attr('lastFolderId', data.packageId);
        $('#taskBindAlertModal').attr('lastRuleId', data.ruleId);

        // 2. 绑定参数回显
        alertModal.createEchoBind(data.inputVariableMappings, data.inputTableKind, 'K1', data.inputTableId, data.inputDbType);
        alertModal.createEchoBind(data.outputVariableMappings, '2', 'K2', data.outputTableId, data.outputDbType);

        // 3. 主键映射回显: 标识输入表类型 & map_id & 记录更新前的输入输出表id
        $('#bindContent_input').attr('inputTableKind', data.inputTableKind); // 1 输入实体表 3 关联表
        if (data.columnMapping != null) {
            $('#keyColBindAlertModal').attr('keyMapId', data.columnMapping.id);
            $('#keyColBindAlertModal').attr('inputColumnId', data.columnMapping.inputColumnId);
            $('#keyColBindAlertModal').attr('outputColumnId', data.columnMapping.outputColumnId);
        }
        $('#filterContent').text(data.inputTableWhereClause == null ? '' : data.inputTableWhereClause);

        return true;
    },
    // 绑定参数回显
    createEchoBind: function (arr, tableKind, bindType, bindId, dbType) {
        if (arr.length > 0) {
            var htmlStr = '';
            var treeCheckedIds = '';
            // var variableSelected = '';
            var variableSelectedInput = '';
            var variableSelectedOutput = '';
            for (var i = 0; i < arr.length; i++) {
                htmlStr += alertModal.createBindEle(
                    arr[i].columnId,
                    arr[i].columnCode,
                    arr[i].variableName,
                    arr[i].variableId,
                    arr[i].variableCode,
                    arr[i].variableKind,
                    arr[i].dbId,
                    arr[i].tableId,
                    arr[i].tableCode,
                    tableKind,
                    arr[i].taskId,
                    dbType,
                    arr[i].variableTypeId
                );
                // 变量下拉框状态回显 & tree被选列回显
                treeCheckedIds += arr[i].columnId + ',';
                // variableSelected += arr[i].variableId + ',';
                if (bindType == 'K1') {
                    variableSelectedInput += arr[i].variableId + ',';
                } else if (bindType == 'K2') {
                    variableSelectedOutput += arr[i].variableId + ',';
                }
            }

            // 被选中的tree-col-ids放入属性中
            treeCheckedIds = $('#taskBindAlertModal').attr('treeCheckedIds') == undefined ? treeCheckedIds : $('#taskBindAlertModal').attr('treeCheckedIds') + treeCheckedIds;
            $('#taskBindAlertModal').attr('treeCheckedIds', treeCheckedIds);

            // // 被选中的变量ids放入属性中
            // variableSelected = $('#taskBindAlertModal').attr('variableSelected') == undefined ? variableSelected : $('#taskBindAlertModal').attr('variableSelected') + variableSelected;
            // $('#taskBindAlertModal').attr('variableSelected', variableSelected);

            // 绑定元素内容回显
            if (bindType == 'K1') { // 输入
                $('#bindContent_input').append(htmlStr).attr('bindId', bindId);
                // 被选中的变量ids放入属性中
                variableSelectedInput = $('#taskBindAlertModal').attr('variableSelectedInput') == undefined ? variableSelectedInput : $('#taskBindAlertModal').attr('variableSelectedInput') + variableSelectedInput;
                $('#taskBindAlertModal').attr('variableSelectedInput', variableSelectedInput);
            } else if (bindType == 'K2') { // 输出
                $('#bindContent_output').append(htmlStr).attr('bindId', bindId);
                // 被选中的变量ids放入属性中
                variableSelectedOutput = $('#taskBindAlertModal').attr('variableSelectedOutput') == undefined ? variableSelectedOutput : $('#taskBindAlertModal').attr('variableSelectedOutput') + variableSelectedOutput;
                $('#taskBindAlertModal').attr('variableSelectedOutput', variableSelectedOutput);
            }
        }
    },
    // cron校验
    cronCheck: function (that) {
        if (that.val() != '') {
            $.ajax({
                url: webpath + '/task/validExpression',
                type: 'POST',
                dataType: "json",
                data: {'cron': that.val()},
                success: function (data) {
                    if (!data) {
                        $('#cron_tip').css('display', 'inline-block');
                    } else {
                        $('#cron_tip').css('display', 'none');
                    }
                },
                error: function (data) {
                    failedMessager.show(data.msg);
                }
            });
        }
    },
    // 添加过滤条件
    addFilter: function () {
        // 验证必填项
        var inputs = $('.filterEle .mustInput:visible');
        for (var i = 0; i < inputs.length; i++) {
            if ($(inputs[i]).val() == '') {
                failedMessager.show('请补全过滤条件信息！');
                return;
            }
        }
        // 拼接过滤条件字符串
        var sql = '';
        var inputArr = $('.filterEle input');
        sql += '[' + $(inputArr[0]).val() + '].[' + $(inputArr[1]).val() + ']';
        if ($('#keyColBind_filterContainer').attr('isFormal') == '1') {
            sql += $(inputArr[2]).val() + $(inputArr[5]).val() + $(inputArr[6]).val();
        } else { // hbase
            sql += $(inputArr[3]).val();
            sql += $(inputArr[4]).siblings('ul').children("li[data-value= '" + $(inputArr[4]).val() + "']").attr('paramName');
            sql += '(' + $(inputArr[5]).val() + ')';
        }
        $('#filterContent').append(sql);
        inputArr.val('');
    },
    // 重置过滤条件inputs
    resetFilter: function () {
        if ($('#keyColBind_filterContainer').attr('isFormal') != '1') {
            $('.filterEle input').slice(2, $('.filterEle input').length).val('');
        } else {
            $('.filterEle input').val('');
            $('#filterColsSelector').empty();
        }
    }
}

/**
 * 主页面及表格功能
 */
var mainPage = {
    // 搜索任务
    searchTask: function () {
        var inputs = $('#searchBar input');
        var obj = {};
        var flag = 0;
        for (var i = 0; i < inputs.length; i++) {
            var value = $.trim($(inputs[i]).val());
            if (value == '') {
                flag++;
                continue;
            } else {
                obj[$(inputs[i]).attr('data-col')] = value;
            }
        }
        if (flag == inputs.length) {
            initTable();
        } else {
            initTable(obj); // 刷新表格
        }
    },
    // 删除任务
    delTask: function (taskId) {
        $.ajax({
            url: webpath + '/task/delete/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {'taskId': taskId},
            success: function (data) {
                if (data.status === 0) {
                    $('#taskMsgText').text('确认删除？');
                    $('#taskMsgAlertModal').modal('toggle', 'center'); // 弹出确认弹框
                    $('#taskMsgConfirm').click(function () {
                        $.ajax({
                            url: webpath + "/task/delete",
                            type: 'POST',
                            data: {'taskId': taskId},
                            dataType: "json",
                            success: function (data) {
                                if (data.status === 0) {
                                    successMessager.show('删除成功！');
                                    // initTable();
                                    mainPage.searchTask();
                                } else {
                                    failedMessager.show(data.msg);
                                }
                            }
                        });
                        $('#taskMsgConfirm').unbind('click'); // 清除confirm绑定事件, 避免复用有问题
                    });
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 批量删除 FIXME
    batchDelete: function () {
        // 获取所有选中的列
        var checkedArr = $('.taskTableInput input:checked');
        if (checkedArr.length < 1) {
            failedMessager.show('请选择要删除的任务！');
        } else {
            // 确认
            $('#taskMsgText').text('确认删除？');
            $('#taskMsgAlertModal').modal('toggle', 'center'); // 弹出确认弹框
            $('#taskMsgConfirm').click(function () {
                var taskIds = [];
                for (var i = 0; i < checkedArr.length; i++) {
                    taskIds.push($(checkedArr[i]).attr('task-id'));
                }
                $.ajax({
                    url: webpath + "/task/deleteByIds",
                    type: 'POST',
                    data: {'taskIds': JSON.stringify(taskIds)},
                    dataType: "json",
                    success: function (data) {
                        if (data.status === 0) {
                            successMessager.show('删除成功！');
                            // initTable();
                            mainPage.searchTask();
                        } else {
                            failedMessager.show('删除失败！' + data.msg);
                        }
                    }
                });
                $('#taskMsgConfirm').unbind('click'); // 清除confirm绑定事件, 避免复用有问题
            });
        }
    },
    // 表格全选/全不选
    selector: function () {
        var flag = $('#tasksSelector').attr('is-check') == '0' ? true : false;
        if (flag) { // 全选
            $('#taskTable .checkbox input').prop('checked', 'checked');
            $('#tasksSelector').attr('is-check', '1');
            $('#taskTable .checkbox input:disabled').removeAttr('checked');
        } else { // 全不选
            $('#taskTable .checkbox input').removeAttr('checked');
            $('#tasksSelector').attr('is-check', '0');
        }
    },
    // 查看任务详情
    detail: function (taskId) {
        // 获取任务数据
        if (taskId != undefined && taskId != '') {
            $.ajax({
                url: webpath + '/task/detail',
                type: 'POST',
                dataType: "json",
                data: {'taskId': taskId},
                success: function (data) {
                    // 数据回显至modal
                    if (data.data != {}) {
                        mainPage.taskDetailEcho(data.data);
                    }
                },
                beforeSend: function () {
                    loading.show();
                },
                complete: function () {
                    loading.hide();
                    $('#taskDetailAlertModal').modal({'show': 'center', "backdrop": "static"});
                }
            });
        }
    },
    // 任务详情-数据内容填充
    taskDetailEcho: function (data) {
        // 先清空之前的数据
        $('#taskDetailAlertModal form')[0].reset();
        $('#taskDetailAlertModal .form-control').attr('disabled', true);
        // 填充数据
        $('#detail_taskName').find('input').val(data.taskName);
        $('#detail_packageName').find('input').val(data.packageName);
        $('#detail_ruleName').find('input').val(data.ruleName);
        $('#detail_version').find('input').val(data.ruleVersion);
        $('#detail_cron').find('input').val(data.cron);
        $('#detail_scanOffset').find('input').val(data.scanOffset);
        $('#detail_scanNextOffset').find('input').val(data.scanNextOffset);
        $('#detail_scanMaxSize').find('input').val(data.scanMaxSize);
        $('#detail_scanNextRowKey').find('input').val(data.scanNextRowKey);
        $('#detail_describe').find('input').val(data.describe);
        $('#detail_resultDescribe').find('.content').html(data.resultDescribe);
        $('#detail_createDate').find('input').val(data.createDate);
        $('#detail_createPerson').find('input').val(data.createPerson);
        $('#detail_updateDate').find('input').val(data.updateDate);
        $('#detail_updatePerson').find('input').val(data.updatePerson);
        var keyHtmlStr = '<span class="label label-warning">' + data.columnMapping.inputColumnCode + ' - ' + data.columnMapping.outputColumnCode + '</span>';
        $('#detail_keyCol').find('.content').html(keyHtmlStr);
        if (data.inputVariableMappings != {}) {
            var inputArr = data.inputVariableMappings;
            var htmlStr = '';
            for (var i = 0; i < inputArr.length; i++) {
                // htmlStr += '<span style="margin: 3px 5px 3px 0px;display: inline-block" class="label label-primary">' + inputArr[i].variableName + ' - ' + inputArr[i].columnCode + '</span>';
                htmlStr += '<span style="margin: 3px 5px 3px 0px;display: inline-block" class="label">' + inputArr[i].variableName + ' - ' + inputArr[i].columnCode + '</span>';
            }
            $('#detail_input').find('.content').html(htmlStr);
        }
        if (data.outputVariableMappings != {}) {
            var inputArr = data.outputVariableMappings;
            var htmlStr = '';
            for (var i = 0; i < inputArr.length; i++) {
                // htmlStr += '<span style="margin: 3px 5px 3px 0px;display: inline-block" class="label label-primary">' + inputArr[i].variableName + ' - ' + inputArr[i].columnCode + '</span>';
                htmlStr += '<span style="margin: 3px 5px 3px 0px;display: inline-block" class="label">' + inputArr[i].variableName + ' - ' + inputArr[i].columnCode + '</span>';
            }
            $('#detail_output').find('.content').html(htmlStr);
        }
        var taskStatus = data.taskStatus;
        var status = '';
        if (taskStatus == '0') {
            status = '初始化';
        } else if (taskStatus == '1') {
            status = '就绪';
        } else if (taskStatus == '2') {
            status = '运行中';
        } else if (taskStatus == '3') {
            status = '暂停';
        } else if (taskStatus == '4') {
            status = '停用中';
        } else if (taskStatus == '5') {
            status = '停用';
        } else if (taskStatus == '6') {
            status = '已删除';
        } else if (taskStatus == '-1') {
            status = '异常运行中';
        } else if (taskStatus == '-2') {
            status = '异常停止';
        } else {
            status = '--'
        }
        $('#detail_taskStatus').find('input').val(status);
    },
    // 启用/停用
    changeStatus: function (taskId, status) {
        $.ajax({
            url: webpath + "/task/" + status,
            type: 'POST',
            data: {'taskId': taskId},
            dataType: "json",
            // async: false, // 等ajax执行完成再执行后续函数
            beforeSend: function () {
                loading.show();
            },
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('修改成功！');
                    // initTable();
                    mainPage.searchTask();
                } else {
                    failedMessager.show('修改失败！');
                }
            },
            complete: function () {
                loading.hide();
            }
        });
    }
}

function initPage() {
    /* 搜索 */
    $('#searchButton').click(function () {
        mainPage.searchTask();
    });

    /* 批量删除 */
    $('#tasksDelButton').click(function () {
        mainPage.batchDelete();
    });

    /* 删除任务 */
    $('#taskTable').on('click', '.deleteButton', function () {
        mainPage.delTask($(this).attr('task-id'));
    });

    /* 查看详情 */
    $('#taskTable').on('click', '.detailButton', function () {
        mainPage.detail($(this).attr('task-id'));
    });

    /* 编辑任务 */
    $('#taskTable').on('click', '.editButton', function () {
        alertModal.editTask($(this).attr('task-id'));
    });

    /* 新建任务按钮 */
    $('#addTaskButton').click(function () {
        // 新建任务权限 authCheck
        // $.ajax({
        //     url: webpath + '/task/save/checkAuth',
        //     type: 'GET',
        //     dataType: "json",
        //     data: {},
        //     success: function (data) {
        //         if (data.status === 0) {
        alertModal.showMain();
        //         } else {
        //             failedMessager.show(data.msg);
        //         }
        //     }
        // });
    });

    /* cron表达式校验 */
    $('#cronFormGroup input').bind('input', function () {
        alertModal.cronCheck($(this));
    });

    /* 下一步 -> 字段映射 */
    $('#nextStep').click(function () {
        alertModal.nextStep();
    });

    /* 上一步 -> 基本信息 */
    $('#lastStep').click(function () {
        alertModal.lastStep();
    });

    /* 下一步 -> 主键字段绑定 */
    $('#nextStep_bind').click(function () {
        alertModal.showKeyBind();
    });

    /* 上一步 -> 字段映射 */
    $('#bindLastStep').click(function () {
        alertModal.hideKeyBind();
    });

    /* 保存 */
    $('#saveTask').click(function () {
        if ($(this).attr('isClicked') == '0') { //限流
            $(this).attr('isClicked', '1');
            alertModal.saveTask();
        }
    });

    /* 绑定字段取消确认 - 退出并消除数据及状态 */
    $('#cancelBind').click(function () {
        alertModal.clearAllCache($('#taskBindAlertModal'));
    });

    /* 基本信息取消确认 - 退出并消除数据及状态 */
    $('#mainClose').click(function () {
        alertModal.clearAllCache($('#taskMainAlertModal'));
    });

    /* 主键绑定取消确认 - 退出并消除数据及状态 */
    $('#bindLastCancel').click(function () {
        alertModal.clearAllCache($('#keyColBindAlertModal'));
    });

    /* 输出框右侧取消按钮: 输入/输出字段删除 */
    $('.cancelIcon').click(function () {
        alertModal.cancelInput($(this).attr('tableKind'));
        $(this).css('display', 'none');
        $(this).siblings('.inputColIcon').css('display', 'inline');
    });

    /* 添加参数绑定元素到内容区域 */
    $('.addBindEle').click(function () {
        alertModal.addBindEle($(this).attr('tableKind'));
    });

    /* 清除所有绑定的字段 */
    $('.clearAllBindEle').click(function () {
        alertModal.clearAllBindEle($(this).attr('tableKind'));
    });

    /* 添加过滤条件 */
    $('#filterAdd').click(function () {
        alertModal.addFilter();
    });

    /* 重置过滤条件inputs */
    $('#filterReset').click(function () {
        alertModal.resetFilter();
    });

    /* 重置整个过滤条件 */
    $('#resetFilterContent').click(function () {
        alertModal.resetFilter();
        $('#filterContent').empty();
    });

    /* 启用/停用任务 */
    $('#taskTable').on('mousedown', '.switch input', function () {
        mainPage.changeStatus($(this).attr('task-id'), $(this).prop('checked') ? 'stop' : 'run');
    });
}

/**
 *  初始化场景下拉框
 */
function initRuleFolder() {
    $.ajax({
        url: webpath + "/ruleFolder",
        dataType: "json",
        success: function (data) {
            if (data.length > 0) {
                // 1. 初始化搜索框内规则包
                var empty = '';
                var htmlStrList = '<li onclick="getRules(\'' + empty + '\', 0)"><a>--请选择--</a></li>';
                for (var i = 0; i < data.length; i++) {
                    htmlStrList += '<li folderId=\'' + data[i].key + '\' onclick="getRules(\'' + data[i].key + '\', 0)"><a>' + data[i].text + '</a></li>';
                }
                $('#packageList').html(htmlStrList);
                // 为li绑定点击事件同步选项到input并设置folderId属性
                $('#packageList li').click(function () {
                    $(this).parent().siblings('input').val($(this).text()).attr('folderId', $(this).attr('folderId'));
                });
                // 2. 初始化新建弹框内规则包
                var htmlStr = "<option>--请选择--</option>";
                for (var i = 0; i < data.length; i++) {
                    htmlStr += '<option folderId=\'' + data[i].key + '\'>' + data[i].text + '</option>';
                }
                $('#packageSelectors').html(htmlStr);
                $('#packageSelectors').unbind().change(function () {
                    var folderId = $('#packageSelectors option:selected').attr('folderId');
                    getRules(folderId, 1);
                });
                $('#packageSelectors').trigger('change'); // 默认加载第一个规则包下的规则
            }
        }
    });
}

/**
 *  获取场景下所有模型
 */
function getRules(foldId, type, echoRuleName, echoRuleId) {
    if (type === 0) { // 0 搜索栏
        $('#ruleList').html('<li><a>--请选择--</a></li>');
        $('#ruleVersionList').html('<li><a>--请选择--</a></li>');
        $("#searchBar input[data-col='ruleName']").val('');
        $("#searchBar input[data-col='ruleVersion']").val('');
        if (!foldId || $.trim(foldId) === '') {
            return;
        }
    }
    if (type === 1) { // 1 任务弹框
        if (!foldId) {
            $('#nextStep').addClass('disabled'); // 未选定场景不许下一步
            $('#ruleSelectors').html('<option>--请选择--</option>');
            $('#versionSelector').html('<option>--请选择--</option>');
            return;
        }
    }

    $.ajax({
        url: webpath + "/ruleFolder/ruleName/inHeader",
        data: {"foldId": foldId},
        dataType: "json",
        success: function (data) {
            if (type === 0) { // 0 搜索栏下拉框
                var htmlStrList = "<li><a>--请选择--</a></li>";
                if (data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        htmlStrList += '<li ruleName=\'' + data[i].ruleName + '\'><a>' + data[i].moduleName + '</a></li>';
                    }
                }
                $('#ruleList').html(htmlStrList);

                $('#ruleList li').unbind().click(function () {
                    $(this).parent().siblings('input').val($(this).text());
                    getModelVersions(1, $(this).attr('ruleName'));
                });

            } else if (type === 1) { // 1 新建任务下拉框
                var htmlStr = "<option>--请选择--</option>";
                if (data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        htmlStr += '<option ruleName=\'' + data[i].ruleName + '\'>' + data[i].moduleName + '</option>';
                    }
                }
                $('#ruleSelectors').html(htmlStr);

                $('#ruleSelectors').unbind().change(function (event, echoRuleName, echoRuleId) {
                    if (echoRuleName && echoRuleId) {
                        getModelVersions(0, echoRuleName, echoRuleId);
                    } else {
                        getModelVersions(0, $('#ruleSelectors option:selected').attr('ruleName'));
                    }
                });

            }
        },
        complete: function () {
            // 如果需要回显, 在请求完成后选中
            if (echoRuleName && echoRuleId) {
                $("#ruleSelectors option[ruleName='" + echoRuleName + "']").prop('selected', true);
                $("#ruleSelectors").trigger('change', [echoRuleName, echoRuleId]);
            } else {
                $('#ruleSelectors').trigger('change');
            }
        }
    });
}

// 获取模型下启用中的版本
function getModelVersions(handleType, ruleName, echoRuleId) {
    if (handleType === 1) { // 搜索栏内
        if (!ruleName || ruleName == null || ruleName === '') {
            $('#ruleVersionList').html('<li><a>--请选择--</a></li>');
            $("#searchBar input[data-col='ruleVersion']").val('');
            return;
        }
    }
    if (handleType === 0) { // 任务弹框
        if (!ruleName) {
            $('#versionSelector').html('<option>--请选择--</option>');
            $('#nextStep').addClass('disabled'); // 未选定场景不许下一步
            return;
        }
    }
    $.ajax({
        url: webpath + "/rule/getEnableVersion/baseInfo",
        data: {"ruleName": ruleName},
        type: "GET",
        dataType: "json",
        success: function (data) {
            if (data.status === 0) {
                if (handleType === 0) {
                    // 1、任务弹框
                    var htmlStr = "<option>--请选择--</option>";
                    if (data.data.length > 0) {
                        $('#nextStep').removeClass('disabled');
                        for (var i = 0; i < data.data.length; i++) {
                            htmlStr += '<option ruleId=\'' + data.data[i].RULE_ID + '\'>' + data.data[i].VERSION + '</option>';
                        }
                    } else {
                        $('#nextStep').addClass('disabled'); // 没模型不许下一步
                    }
                    $('#versionSelector').html(htmlStr);
                    $('#versionSelector').unbind().change(function () {
                        if (!$('#versionSelector option:selected').attr('ruleId')) {
                            $('#nextStep').addClass('disabled');
                        } else {
                            $('#nextStep').removeClass('disabled');
                        }
                    });
                } else if (handleType === 1) {
                    // 2、搜索栏内
                    var htmlStrList = '<li><a>--请选择--</a></li>';
                    if (data.data.length > 0) {
                        for (var i = 0; i < data.data.length; i++) {
                            htmlStrList += '<li ruleId=\'' + data.data[i].RULE_ID + '\'><a>' + data.data[i].VERSION + '</a></li>';
                        }
                    }
                    $('#ruleVersionList').html(htmlStrList);
                    // 为li绑定点击事件同步选项到input
                    $('#ruleVersionList li').click(function () {
                        $(this).parent().siblings('input').val($(this).text());
                    });
                }
            } else {
                failedMessager.show(data.msg);
            }
        },
        complete: function () {
            if (echoRuleId) {
                $("#versionSelector option[ruleId='" + echoRuleId + "']").prop('selected', true);
            }
            $('#versionSelector').trigger('change');
        }
    });
}

/**
 * 是否为 >=1 的整数
 */
function isInteger(obj) {
    return /^[1-9]\d*$/.test(obj);
}

/**
 * 验证过滤条件
 */
function isFilterLegal() {
    var filterContent = $.trim($('#filterContent').text());
    if (filterContent != '') {
        // var re = /select|update|delete|exec|count|'|"|=|;|>|<|%/i;
        // var re = /select|update|delete|exec|count|'|"|;|%/i;
        var re = /select|update|delete|exec|count|"|;/i;
        if (re.test(filterContent)) {
            return false;
        } else {
            return true;
        }
    } else {
        return true;
    }
}

/**
 * 初始化离线任务表格
 */
function initTable(obj) {
    var searchObj = obj == undefined ? {} : obj;
    $('#taskTable').width('100%').dataTable({
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
            {
                "title": function () {
                    var htmlStr = '';
                    htmlStr += '<div id="tasksSelector" is-check="0" class="checkbox"><label><input type="checkbox"></label></div>';
                    return htmlStr;
                },
                "data": "taskId",
                "width": "5%",
                "render": function (data, type, row) {
                    var htmlStr = '';
                    var taskStatus = $.trim(row.taskStatus);
                    // 就绪 & 运行中 & 暂停 & 停用中 & 异常运行中 状态下的任务不可编辑
                    if (!(taskStatus == '1' || taskStatus == '2' || taskStatus == '3' || taskStatus == '4' || taskStatus == '-1')) {
                        htmlStr += '<div class="checkbox taskTableInput"><label><input task-id=\'' + data + '\' type="checkbox"></label></div>';
                    } else {
                        htmlStr += '<div class="checkbox taskTableInput"><label><input disabled task-id=\'' + data + '\' type="checkbox"></label></div>';
                    }
                    return htmlStr;
                }
            },
            {"title": "任务名称", "data": "taskName", "width": "20%"},
            {"title": "场景名称", "data": "packageName", "width": "15%"},
            {"title": "模型名称", "data": "ruleName", "width": "20%"},
            {
                "title": "运行状态", "data": "taskStatus", "width": "10%", "render": function (data) {
                    switch ($.trim(data)) {
                        case '0':
                            return '<span class="label label-dot label-primary"></span>' + ' 初始化';
                        case '1':
                            return '<span class="label label-dot label-success"></span>' + ' 就绪';
                        case '2':
                            return '<span class="label label-dot label-success"></span>' + ' 运行中';
                        case '3':
                            return '<span class="label label-dot label-warning"></span>' + ' 暂停';
                        case '4':
                            return '<span class="label label-dot label-warning"></span>' + ' 停用中';
                        case '5':
                            return '<span class="label label-dot label-danger"></span>' + ' 停用';
                        case '6':
                            return '<span class="label label-dot label-danger"></span>' + ' 已删除';
                        case '-1':
                            return '<span class="label label-dot label-danger"></span>' + ' 异常运行中';
                        case '-2':
                            return '<span class="label label-dot label-danger"></span>' + ' 异常停止';
                        default:
                            return '--';
                    }
                }
            },
            {
                "title": "启用/停用", "data": "taskStatus", "width": "10%", "render": function (data, type, row) {
                    var switchValue = '';
                    var checkHtmlStr = ''; // 控制开关开启状态
                    var disabledStr = ''; // 控制是否可操作

                    // if ($.trim(data) == '0' || $.trim(data) == '3' || $.trim(data) == '4' || $.trim(data) == '5' || $.trim(data) == '-2') {
                    //     switchValue = '点击运行';
                    // } else if ($.trim(data) == '1' || $.trim(data) == '2') {
                    //     switchValue = '点击停用';
                    //     checkHtmlStr += 'checked';
                    // } else {
                    //     switchValue = '--';
                    // }

                    if ($.trim(data) == '0' || $.trim(data) == '3' || $.trim(data) == '5' || $.trim(data) == '-2') {
                        switchValue = '点击运行';
                    } else if ($.trim(data) == '1' || $.trim(data) == '2') {
                        switchValue = '点击停用';
                        checkHtmlStr += 'checked';
                    } else if ($.trim(data) == '4' || $.trim(data) == '-1') {
                        switchValue = '点击停用';
                        checkHtmlStr += 'checked';
                        disabledStr = ' disabled';
                    } else {
                        switchValue = '--';
                    }

                    var htmlStr = "";
                    htmlStr += '<div class="switch"><input task-id=\'' + row.taskId + '\' type="checkbox" ' + checkHtmlStr + ' ' + disabledStr + '><label>' + switchValue + '</label></div>';
                    return htmlStr;
                }
            },
            {
                "title": "操作", "data": "taskId", "width": "20%", "render": function (data, type, row) {
                    var taskStatus = $.trim(row.taskStatus);
                    var htmlStr = "";
                    htmlStr += '<span task-id=\'' + data + '\' class="cm-tblB detailButton" type="button">详情</span>';
                    // 就绪 & 运行中 & 暂停 & 停用中 & 异常运行中 状态下的任务不可编辑
                    if (!(taskStatus == '1' || taskStatus == '2' || taskStatus == '3' || taskStatus == '4' || taskStatus == '-1')) {
                        htmlStr += '<span task-id=\'' + data + '\' class="cm-tblB editButton" type="button" >编辑</span>';
                        htmlStr += '<span task-id=\'' + data + '\' class="cm-tblC deleteButton" type="button">删除</span>';
                    }
                    return htmlStr;
                }
            }
        ],
        ajax: {
            url: webpath + '/task/list',
            "type": 'GET',
            "data": function (d) { // 查询参数
                return $.extend({}, d, searchObj);
            }
        },
        "fnDrawCallback": function (oSettings, json) {
            // $("tr:even").css("background-color", "#fbfbfd");
            // $("table:eq(0) th").css("background-color", "#f6f7fb");
        }
    });

    // 全选/全不选
    $('#tasksSelector').bind('click', function () {
        mainPage.selector();
    });
}

$(function () {
    initTable();
    initPage();
    initRuleFolder();
});