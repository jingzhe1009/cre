/**
 * 导出模型
 */

var exportModal = {
    /**
     * 初始化导出模型树
     *
     *      pageType / rootLevel:
     *          0 全局
     *          1 模型库下: rootLevel 1某组 2某模型 3某版本
     *          2 场景下: rootLevel 1某场景 2某模型
     *      params: ( 有什么传什么 )
     *          eg: params = {
     *               "folderObj": {"folderId": "", "folderName": ""},
     *               "groupObj": {"groupId": "", "groupName": ""},
     *               "modelObj": {"modelId": "", "modelName": ""},
     *               "versionObj": {"versionId": "", "versionName": ""}
     *           };
     */
    callBackFun: {},
    globalPageFlag: false,
    initExportPage: function (pageType, rootLevel, params, callBackFun) {
        exportModal.callBackFun = callBackFun;
        exportModal.globalPageFlag = (pageType === 0);
        // 初始化tree
        exportModal.initCheck(pageType, rootLevel, params);
        // 事件绑定
        $('#submitExport').unbind().on('click', function () {
            exportModal.export(pageType, rootLevel, params);
        });
        $('#cancelExport').unbind().on('click', function () {
            exportModal.clearAndClose();
        });
        $('#resetExport').unbind().on('click', function () {
            exportModal.resetExportTree();
        });
    },
    // 初始化tree
    initCheck: function (pageType, rootLevel, params) {
        if (pageType === 0) {
            exportModal.initGlobalModelTree();
            $('#cancelExport').css('display', 'none');
        } else if (pageType === 1) {
            exportModal.initPublicModelTree(rootLevel, params);
        } else if (pageType === 2) {
            exportModal.initFolderModelTree(rootLevel, params);
        }
    },
    // 创建全局模型导出树
    initGlobalModelTree: function () {
        // console.log('全局模型导出入口');
        var treeData = [];
        // 1. 模型库 FIXME publicFolderId 暂时写死
        var publicTreeObj = exportModal.createFolderNode('0000000000000000000000000000001', '模型库', true, 0, 1);
        var publicTreeObj_children = [];
        $.ajax({ // 查询所有模型库下组
            url: webpath + '/modelBase/group/list',
            type: 'GET',
            dataType: "json",
            success: function (data) {
                if (data.status === 0) {
                    var arr = data.data;
                    if (arr.length > 0) {
                        for (var i = 0; i < arr.length; i++) {
                            publicTreeObj_children.push(exportModal.createGroupNode(arr[i]['modelGroupId'], arr[i]['modelGroupName'], false, 0));
                        }
                    }
                    publicTreeObj['children'] = publicTreeObj_children;
                    treeData.push(publicTreeObj);
                }
            },
            complete: function () {
                // 2. 场景
                var privateTreeObj = exportModal.createPrivateFolderNode('', '场景库', true, 0);
                var privateTreeObj_children = [];
                $.ajax({ // 查询所有场景
                    url: webpath + '/ruleFolder',
                    type: 'GET',
                    success: function (data) {
                        if (data.length > 0) {
                            for (var i = 0; i < data.length; i++) {
                                var folderTreeObj = exportModal.createFolderNode(data[i]['key'], data[i]['text'], false, 0, 0);
                                // treeData.push(folderTreeObj);
                                privateTreeObj_children.push(folderTreeObj);
                            }
                            privateTreeObj['children'] = privateTreeObj_children;
                            treeData.push(privateTreeObj);
                        }
                    },
                    complete: function () {
                        exportModal.createTree(treeData);
                    }
                });
            }
        });
    },
    // 创建场景下模型导出树
    initFolderModelTree: function (rootLevel, params) {
        // console.log('场景下导出入口');
        var treeData = [];
        // rootLevel 1某场景 2某模型
        if (rootLevel === 1) {
            var folderData = params['folderObj'];
            var folderTreeObj = exportModal.createFolderNode(folderData['folderId'], folderData['folderName'], true, 1, 0);
            var folderTreeObj_children = [];
            // 获取当前场景下全部模型
            $.ajax({
                url: webpath + '/ruleFolder/ruleName/inHeader',
                type: 'GET',
                dataType: "json",
                data: {"foldId": folderData['folderId']},
                success: function (data) {
                    if (data.length > 0) {
                        for (var i = 0; i < data.length; i++) {
                            folderTreeObj_children.push(exportModal.createModelNode(data[i]['ruleName'], data[i]['moduleName'], false, 0, 0));
                        }
                    }
                    folderTreeObj['children'] = folderTreeObj_children;
                    treeData.push(folderTreeObj);
                },
                complete: function () {
                    exportModal.createTree(treeData);
                }
            });
        } else if (rootLevel === 2) {
            var modelData = params['modelObj'];
            var modelTreeObj = exportModal.createModelNode(modelData['modelId'], modelData['modelName'], true, 1, 0);
            var modelTreeObj_children = [];
            // 获取当前模型下所有版本
            $.ajax({
                url: webpath + '/rule/versions',
                type: 'GET',
                dataType: "json",
                data: {"ruleName": modelData['modelId'], "isPublic": '0', 'needDraft': '-1'},
                success: function (data) {
                    if (data.status === 0) {
                        var arr = data.data;
                        if (arr.length > 0) {
                            for (var i = 0; i < arr.length; i++) {
                                modelTreeObj_children.push(exportModal.createVersionNode(arr[i]['ruleId'], arr[i]['version']));
                            }
                        }
                        modelTreeObj['children'] = modelTreeObj_children;
                        treeData.push(modelTreeObj);
                    }
                },
                complete: function () {
                    exportModal.createTree(treeData);
                }
            });
        } else {
            failedMessager.show('pageType = 2; rootLevel error!');
        }
    },
    // 创建模型库树
    initPublicModelTree: function (rootLevel, params) {
        // console.log('模型库导出入口');
        var treeData = [];
        // rootLevel 1组 2模型 3版本
        if (rootLevel === 1) { // 组导出按钮进入
            var groupData = params['groupObj'];
            var groupObj = exportModal.createGroupNode(groupData['groupId'], groupData['groupName'], true, 1);
            var groupObj_children = [];
            // 获取当前组下全部模型
            $.ajax({
                url: webpath + '/rule/public/header/list',
                type: 'GET',
                dataType: "json",
                data: {"modelGroupName": groupData['groupName']},
                success: function (data) {
                    if (data.status === 0) {
                        var arr = data.data;
                        if (arr.length > 0) {
                            for (var i = 0; i < arr.length; i++) {
                                groupObj_children.push(exportModal.createModelNode(arr[i]['ruleName'], arr[i]['moduleName'], false, 0, 1));
                            }
                        }
                        groupObj['children'] = groupObj_children;
                        treeData.push(groupObj);
                    }
                },
                complete: function () {
                    exportModal.createTree(treeData);
                }
            });
        } else if (rootLevel === 2) { // 模型导入按钮进入
            var modelData = params['modelObj'];
            var modelObj = exportModal.createModelNode(modelData['modelId'], modelData['modelName'], true, 1, 1);
            var modelObj_children = [];
            // 获取当前模型下所有版本
            $.ajax({
                url: webpath + '/rule/versions',
                type: 'GET',
                dataType: "json",
                data: {"ruleName": modelData['modelId'], "isPublic": '1', 'needDraft': '-1'},
                success: function (data) {
                    if (data.status === 0) {
                        var arr = data.data;
                        if (arr.length > 0) {
                            for (var i = 0; i < arr.length; i++) {
                                modelObj_children.push(exportModal.createVersionNode(arr[i]['ruleId'], arr[i]['version']));
                            }
                        }
                        modelObj['children'] = modelObj_children;
                        treeData.push(modelObj);
                    }
                },
                complete: function () {
                    exportModal.createTree(treeData);
                }
            });
        } else if (rootLevel === 3) { // 从版本导出按钮进入
            // 确认弹框进行确认导出
            confirmAlert.show(
                '确定导出该版本吗？',
                function () {
                    exportModal.export(1, rootLevel, params);
                }, exportModal.callBackFun);
        } else {
            failedMessager.show('pageType = 1; rootLevel error!');
        }
    },
    // 创建导出树，初始化样式，绑定事件，控制checkbox状态
    createTree: function (treeData, pageType, rootLevel) {
        if (!exportModal.globalPageFlag) {
            $('#exportModelsAlertModal').modal({'show': 'center', "backdrop": "static"});
        }
        $('#exportTree').remove();
        var treeHtml = '<ul class="tree tree-lines tree-angles" id="exportTree"></ul>';
        $('#exportTreeContainer').html(treeHtml);
        $('#exportTree').tree({data: treeData});
        $('#exportTree .exportTreeCheckBox').css({'margin': '0 8px 0 0', 'cursor': 'pointer'});
        $('#exportTree span').css({'vertical-align': 'middle'});

        // 扩展模型库-组节点
        $('#exportTree .exportTree_groupNode').siblings('.list-toggle').on('click', function () {
            exportModal.getGroupChildren($(this).siblings('span').text(), $(this));
        });

        // 扩展场景节点
        $("#exportTree .exportTree_folderNode[isPublic='0']").siblings('.list-toggle').on('click', function () {
            exportModal.getFolderChildren(
                $(this).siblings('.exportTree_folderNode').attr('data-id'),
                $(this));
        });

        // 扩展模型节点
        $('#exportTree .exportTree_modelNode').siblings('.list-toggle').on('click', function () {
            var modelNode = $(this).siblings('.exportTree_modelNode');
            exportModal.getModelChildren(
                modelNode.attr('data-id'),
                modelNode.attr('isPublic'),
                $(this));
        });

        // 多选框状态
        $("#exportTree").on('change', '.exportTreeCheckBox', function () {
            var flag = $(this).prop('checked');
            // 同步子状态
            $(this).siblings('ul').find('.exportTreeCheckBox').prop('checked', flag);
            // 同步直接上层父状态
            var brothers = $(this).parent().parent().children('li').children('.exportTreeCheckBox');
            var brothersChecked = $(this).parent().parent().children('li').children('.exportTreeCheckBox:checked');
            if (flag) { // 选中
                if (brothers.length === brothersChecked.length) {
                    $(this).parent().parent().siblings(".exportTreeCheckBox").prop('checked', flag); // 上层多选框同步
                }
            } else { // 取消选中
                if (brothersChecked.length < brothers.length) {
                    $(this).parent().parent().siblings(".exportTreeCheckBox").prop('checked', flag); // 上层多选框同步
                }
            }
            // FIXME 深层子节点取消选中，但是较外层未同步状态的bug

        });
    },
    // 创建场景库根节点
    createPrivateFolderNode: function (folderId, folderName, isOpen, isRoot) {
        var obj = {};
        obj['id'] = folderId;
        obj['html'] = '<input class="exportTreeCheckBox exportTree_privateFolderNode exportTree_firstLevelNode" type="checkbox" data-id=\'' + folderId + '\' isRoot=\'' + isRoot + '\'/><span>' + folderName + '</span>';
        obj['children'] = [{'title': '---'}]; // 先造一个假数据，加载后再覆盖
        obj['open'] = isOpen;
        return obj;
    },
    // 创建场景/库根节点
    createFolderNode: function (folderId, folderName, isOpen, isRoot, isPublic) {
        var obj = {};
        obj['id'] = folderId;
        if (isPublic) {
            obj['html'] = '<input class="exportTreeCheckBox exportTree_folderNode exportTree_firstLevelNode" type="checkbox" data-id=\'' + folderId + '\' isRoot=\'' + isRoot + '\' isPublic=\'' + isPublic + '\'/><span>' + folderName + '</span>';
        } else {
            obj['html'] = '<input class="exportTreeCheckBox exportTree_folderNode" type="checkbox" data-id=\'' + folderId + '\' isRoot=\'' + isRoot + '\' isPublic=\'' + isPublic + '\'/><span>' + folderName + '</span>';
        }
        obj['children'] = [{'title': '---'}]; // 先造一个假数据，加载后再覆盖
        obj['open'] = isOpen;
        return obj;
    },
    // 构造模型组单节点
    createGroupNode: function (groupId, groupName, isOpen, isRoot) {
        var obj = {};
        obj['id'] = groupId;
        obj['html'] = '<input class="exportTreeCheckBox exportTree_groupNode" type="checkbox" data-id=\'' + groupId + '\' isRoot=\'' + isRoot + '\'/><span>' + groupName + '</span>';
        obj['children'] = [{'title': '---'}]; // 先造一个假数据，加载后再覆盖
        obj['open'] = isOpen;
        return obj;
    },
    // 构造模型单节点
    createModelNode: function (modelId, modelName, isOpen, isRoot, isPublic) {
        var obj = {};
        obj['id'] = modelId;
        obj['html'] = '<input class="exportTreeCheckBox exportTree_modelNode" type="checkbox" data-id=\'' + modelId + '\' isRoot=\'' + isRoot + '\' isPublic=\'' + isPublic + '\'/><span>' + modelName + '</span>';
        obj['children'] = [{'title': '---'}]; // 先造一个假数据，加载后再覆盖
        obj['open'] = isOpen;
        return obj;
    },
    // 构造版本单节点
    createVersionNode: function (ruleId, version) {
        var obj = {};
        obj['id'] = ruleId;
        obj['html'] = '<input class="exportTreeCheckBox exportTree_versionNode" type="checkbox" data-id=\'' + ruleId + '\'/><span>' + version + '</span>';
        return obj;
    },
    // 扩展组节点
    getGroupChildren: function (modelGroupName, curIcon) {
        var initedFlag = curIcon.siblings('.exportTreeCheckBox').attr('inited') === '1';
        if (!initedFlag) {
            $.ajax({
                url: webpath + '/rule/public/header/list',
                type: 'GET',
                dataType: "json",
                data: {"modelGroupName": modelGroupName},
                success: function (data) {
                    if (data.status === 0) {
                        var arr = data.data;
                        var childrenArr = [];
                        if (arr.length > 0) {
                            for (var i = 0; i < arr.length; i++) {
                                childrenArr.push(exportModal.createModelNode(arr[i]['ruleName'], arr[i]['moduleName'], false, 0, 1));
                            }
                        }
                        exportModal.expandCallBack(curIcon, childrenArr);
                    }
                },
                complete: function () {
                    // 扩展模型节点
                    $('#exportTree .exportTree_modelNode').siblings('.list-toggle').on('click', function () {
                        var modelNode = $(this).siblings('.exportTree_modelNode');
                        exportModal.getModelChildren(
                            modelNode.attr('data-id'),
                            modelNode.attr('isPublic'),
                            $(this));
                    });
                }
            });
        }
    },
    // 扩展场景节点
    getFolderChildren: function (folderId, curIcon) {
        var initedFlag = curIcon.siblings('.exportTreeCheckBox').attr('inited') === '1';
        if (!initedFlag) {
            $.ajax({
                url: webpath + '/ruleFolder/ruleName/inHeader',
                type: 'GET',
                dataType: "json",
                data: {"foldId": folderId},
                success: function (data) {
                    var childrenArr = [];
                    if (data.length > 0) {
                        for (var i = 0; i < data.length; i++) {
                            childrenArr.push(exportModal.createModelNode(data[i]['ruleName'], data[i]['moduleName'], false, 0, 0));
                        }
                    }
                    exportModal.expandCallBack(curIcon, childrenArr);
                },
                complete: function () {
                    // 扩展模型节点
                    $('#exportTree .exportTree_modelNode').siblings('.list-toggle').on('click', function () {
                        var modelNode = $(this).siblings('.exportTree_modelNode');
                        exportModal.getModelChildren(
                            modelNode.attr('data-id'),
                            modelNode.attr('isPublic'),
                            $(this));
                    });
                }
            });
        }
    },
    // 扩展模型节点
    getModelChildren: function (ruleName, isPublic, curIcon) {
        var initedFlag = curIcon.siblings('.exportTreeCheckBox').attr('inited') === '1';
        if (!initedFlag) {
            $.ajax({
                url: webpath + '/rule/versions',
                type: 'GET',
                dataType: "json",
                data: {"ruleName": ruleName, "isPublic": isPublic, 'needDraft': '-1'},
                success: function (data) {
                    if (data.status === 0) {
                        var arr = data.data;
                        var childrenArr = [];
                        if (arr.length > 0) {
                            for (var i = 0; i < arr.length; i++) {
                                childrenArr.push(exportModal.createVersionNode(arr[i]['ruleId'], arr[i]['version'], false, 0));
                            }
                        }
                        exportModal.expandCallBack(curIcon, childrenArr);
                    }
                }
            });
        }
    },
    // 扩展节点后的统一处理
    expandCallBack: function (curIcon, childrenArr) {
        if (childrenArr.length !== 0) {
            // 清空之前放进去的数据/假数据, 添加新的子数据
            var myTree = $('#exportTree').data('zui.tree');
            curIcon.siblings('ul').empty();
            myTree.add(curIcon.parent(), childrenArr);
        }
        curIcon.siblings('.exportTreeCheckBox').attr('inited', '1');
        curIcon.siblings('ul').find('.exportTreeCheckBox').css({
            'margin': '0 8px 0 0',
            'cursor': 'pointer'
        });
        $('#exportTree span').css({'vertical-align': 'middle'});

        // 子元素展开后检查上层多选框状态
        var checkFlag = curIcon.siblings('.exportTreeCheckBox').prop('checked');
        curIcon.siblings('ul').find('.exportTreeCheckBox').prop('checked', checkFlag);
    },
    // 进行导出
    export: function (pageType, rootLevel, params) {
        var checked = $('#exportTree .exportTreeCheckBox:checked').length;
        // 非单一版本导出需要检查勾选项数目
        var versionFlag = (pageType === 1 && rootLevel === 3);
        if (!versionFlag) {
            if (checked === 0) {
                failedMessager.show('请选择要导出的模型！');
                return;
            }
        }
        var dataArr = [];
        debugger;
        if (pageType === 0) { // 0全局
            // 模型库组数据
            var publicFolderId = $("#exportTree .exportTree_folderNode[isPublic='1']").eq(0).attr('data-id');
            var groupCheckBoxArr = $('#exportTree .exportTree_groupNode');
            var publicDataArr = exportModal.getGroupData(publicFolderId, groupCheckBoxArr);
            // 场景数据
            var folderCheckBoxArr = $("#exportTree .exportTree_folderNode[isPublic='0']");
            var folderDataArr = exportModal.getFolderData(folderCheckBoxArr);
            dataArr = publicDataArr.concat(folderDataArr);
        }
        if (pageType === 1) { // 1模型库下
            var folderId = params['folderObj']['folderId'];
            var groupId = params['groupObj']['groupId'];
            var modelId = params['modelObj']['modelId'];
            var versionId = params['versionObj']['versionId'];
            if (rootLevel === 1) { // 某组
                var groupCheckBoxArr = $('#exportTree .exportTree_groupNode');
                dataArr = exportModal.getGroupData(folderId, groupCheckBoxArr);
            }
            if (rootLevel === 2) { // 某组下 某模型
                var modelCheckBoxArr = $('#exportTree .exportTree_modelNode');
                var data = {
                    "folderId": folderId,
                    "modelGroupId": groupId,
                    "type": "part",
                    "modelHeader": exportModal.getModelData(modelCheckBoxArr)
                };
                dataArr.push(data);
            }
            if (rootLevel === 3) { // 某组下 某模型下 某版本
                dataArr = [{
                    "folderId": folderId,
                    "modelGroupId": groupId,
                    "type": "part",
                    "modelHeader": [
                        {
                            "ruleName": modelId,
                            "type": "part",
                            "modelVersion": [versionId]
                        }
                    ]
                }];
            }
        }
        if (pageType === 2) { // 2场景下
            var folderId = params['folderObj']['folderId'];
            if (rootLevel === 1) { // 该场景
                var folderCheckBoxArr = $('#exportTree .exportTree_folderNode');
                dataArr = exportModal.getFolderData(folderCheckBoxArr);
            }
            if (rootLevel === 2) { // 该场景下 某模型
                var modelCheckBoxArr = $('#exportTree .exportTree_modelNode');
                var data = {
                    "folderId": folderId,
                    "modelGroupId": "",
                    "type": "part",
                    "modelHeader": exportModal.getModelData(modelCheckBoxArr)
                };
                dataArr.push(data);
            }
        }
        // 导出
        $.ajax({
            url: webpath + '/model/importAndExport/export',
            type: 'POST',
            dataType: "json",
            data: {"exportParams": JSON.stringify(dataArr)},
            beforeSend: function () {
                loading.show();
                if (!exportModal.globalPageFlag) {
                    $('#exportModelsAlertModal').modal('hide');
                }
            },
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show(data.msg);
                    // if (!exportModal.globalPageFlag) {
                    //     $('#exportModelsAlertModal').modal('hide');
                    // }
                    var exportReportData = data.data;
                    reportModel.initExportReportData(exportReportData, versionFlag); // 生成导出报告
                } else {
                    failedMessager.show(data.msg);
                    // if (!exportModal.globalPageFlag) { // FIXME
                    //     $('#exportModelsAlertModal').modal({'show': 'center', "backdrop": "static"});
                    // }
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.log(errorThrown);
                if (textStatus === 'timeout') {
                    console.log('请求超时');
                }
                if (!exportModal.globalPageFlag) {
                    $('#exportModelsAlertModal').modal({'show': 'center', "backdrop": "static"});
                }
            },
            complete: function () {
                loading.hide();
            }
        });
    },
    // 获取场景下导出数据
    getFolderData: function (folderCheckBoxArr) {
        var dataArr = [];
        for (var i = 0; i < folderCheckBoxArr.length; i++) {
            var groupData = {
                "folderId": $(folderCheckBoxArr[i]).attr('data-id'),
                "modelGroupId": "", // 场景下无组id
                "type": "",
                "modelHeader": []
            };
            var folderFlag = $(folderCheckBoxArr[i]).prop('checked');
            if (folderFlag) { // 全场景选中
                groupData['type'] = 'all';
            } else {
                var modelCheckArr = $(folderCheckBoxArr[i]).siblings('ul').find('.exportTree_modelNode');
                var modelDataArr = exportModal.getModelData(modelCheckArr);
                if (modelDataArr.length === 0) {
                    continue;
                }
                groupData['type'] = 'part';
                groupData['modelHeader'] = modelDataArr;
            }
            dataArr.push(groupData);
        }
        return dataArr;
    },
    // 获取模型组导出数据
    getGroupData: function (folderId, groupCheckBoxArr) {
        var dataArr = [];
        for (var i = 0; i < groupCheckBoxArr.length; i++) {
            var groupData = {
                "folderId": folderId,
                "modelGroupId": $(groupCheckBoxArr[i]).attr('data-id'),
                "type": "",
                "modelHeader": []
            };
            var groupFlag = $(groupCheckBoxArr[i]).prop('checked');
            if (groupFlag) { // 全组选中
                groupData['type'] = 'all';
            } else {
                var modelCheckArr = $(groupCheckBoxArr[i]).siblings('ul').find('.exportTree_modelNode');
                var modelDataArr = exportModal.getModelData(modelCheckArr);
                if (modelDataArr.length === 0) {
                    continue;
                }
                groupData['type'] = 'part';
                groupData['modelHeader'] = modelDataArr;
            }
            dataArr.push(groupData);
        }
        return dataArr;
    },
    // 获取模型导出数据
    getModelData: function (modelCheckBoxArr) {
        var modelDataArr = [];
        for (var i = 0; i < modelCheckBoxArr.length; i++) {
            var modelData = {
                "ruleName": $(modelCheckBoxArr[i]).attr('data-id'),
                "type": "",
                "modelVersion": []
            };
            var modelFlag = $(modelCheckBoxArr[i]).prop('checked');
            if (modelFlag) { // 模型全选
                modelData["type"] = 'all';
            } else { // 遍历选择的版本
                modelData["type"] = 'part';
                var versionCheckArr = $(modelCheckBoxArr[i]).siblings('ul').find('.exportTree_versionNode:checked');
                if (versionCheckArr.length === 0) { // 无选中版本跳出本次循环
                    continue;
                }
                var versionDataArr = [];
                for (var j = 0; j < versionCheckArr.length; j++) {
                    if ($(versionCheckArr[j]).prop('checked')) {
                        versionDataArr.push($(versionCheckArr[j]).attr('data-id'));
                    }
                }
                modelData['modelVersion'] = versionDataArr;
            }
            modelDataArr.push(modelData);
        }
        return modelDataArr;
    },
    // 清除缓存并退出
    clearAndClose: function () {
        if (!exportModal.globalPageFlag) {
            $('#exportModelsAlertModal').modal('toggle', 'center');
        }
        if (exportModal.callBackFun) {
            exportModal.callBackFun();
        }
    },
    // 重置导出树
    resetExportTree: function () {
        $('#exportTree .exportTreeCheckBox').prop('checked', false);
    }
};

// 导出报告
var reportModel = {
    // 总统计表格展示字段映射
    totalKeyMap: {
        "folderNumber": '场景数',
        "modelGroupNumber": '模型组数',
        "modelHeaderNumber": '模型数',
        "modelVersionNumber": '模型版本数',
        "apiGroupNumber": '接口组数',
        "apiNumber": '接口数',
        "kpiGroupNumber": '指标组数',
        "kpiNumber": '指标数',
        "variableGroupNumber": '参数组数',
        "variableNumber": '参数数',
        "dbNumber": '数据源数',
        "pubDbTableNumber": '公共的数据源表数',
        "priDbTableNumber": '场景下的数据源表数',
        "dbColunmNumber": '字段数'
    },
    // 缺失类型映射
    lackMap: {
        'folder': {
            type: '场景',
            id: 'folderId',
            name: 'folderName'
        },
        'modelGroup': {
            type: '模型组',
            id: 'modelGroupId',
            name: 'modelGroupName'
        },
        'variableGroup': {
            type: '参数组',
            id: 'variableGroupId',
            name: 'variableGroupName'
        },
        'variable': {
            type: '参数',
            id: 'variableId',
            name: 'variableCode'
        },
        'kpiGroup': {
            type: '指标组',
            id: 'kpiGroupId',
            name: 'kpiGroupName'
        },
        'kpi': {
            type: '指标',
            id: 'kpiId',
            name: 'kpiName'
        },
        'apiGroup': {
            type: '接口组',
            id: 'apiGroupId',
            name: 'apiGroupName'
        },
        'api': {
            type: '接口',
            id: 'apiId',
            name: 'apiName'
        },
        'db': {
            type: '数据源',
            id: 'dbId',
            name: 'dbAlias'
        },
        'pubDbTable': {
            type: '数据源表',
            id: 'tableId',
            name: 'tableCode'
        },
        'dbColunm': {
            type: '数据源字段',
            id: 'columnId',
            name: 'columnCode'
        },
        'modelHeader': {
            type: '模型',
            id: 'ruleName',
            name: 'modelName',
            version: 'version'
        }
    },
    // 初始化导出报告
    initExportReportData: function (data, versionFlag) {
        // console.dir(data);
        if (!data) {
            failedMessager.show('报告生成失败，导出报告数据缺失，请检查！');
            return;
        }

        if (exportModal.globalPageFlag) {
            $('#closeExportReport').css('display', 'none'); // 隐藏返回按钮
        } else {
            $('#closeExportReport').css('display', 'inline-block');
        }
        // 报告返回按钮 & 报告关闭按钮
        if (versionFlag) { // 单一版本导出
            $('#closeExportReport').unbind().on('click', function () {
                $('#exportReportAlertModal .downLoadBtn').remove();
                exportModal.callBackFun();
            });
            $('#closeExportAll').unbind().on('click', function () {
                $('#exportReportAlertModal .downLoadBtn').remove();
                $('#exportModelsAlertModal').modal('hide');
                exportModal.callBackFun();
            });
        } else {
            $('#closeExportReport').unbind().on('click', function () {
                if (!exportModal.globalPageFlag) {
                    $('#exportModelsAlertModal').modal({'show': 'center', "backdrop": "static"});
                }
                $('#exportReportAlertModal .downLoadBtn').remove();
            });
            $('#closeExportAll').unbind().on('click', function () {
                if (!exportModal.globalPageFlag) {
                    $('#exportModelsAlertModal').modal('hide');
                }
                $('#exportReportAlertModal .downLoadBtn').remove();
            });
        }

        // 基础信息
        $('#operateType').text(data.operateType === 'export' ? '导出' : '导入');
        $('#systemVersion').text(data.systemVersion);
        $('#date').text(data.operateDate);
        $('#success').text((data.success === '1') ? '成功' : '失败');
        $('#exportReportAlertModal .downLoadBtn').remove();

        // 导出报告
        if (data.operateType === 'export') {
            var operateContent = JSON.parse(data['operateContent']);
            if (data.success === '1') { // 成功
                reportModel.exportSuccessReport(operateContent['report']);
                reportModel.createDownLoad(data['fileName']);
            } else { // 失败
                reportModel.exportFailedReport(operateContent['lack']);
            }
        }
        $('#exportReportAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 导出成功报告
    exportSuccessReport: function (report) {
        $('.exportSuccess').removeClass('hide');
        $('.exportFailed').addClass('hide');
        reportModel.initTotalTable(report); // 总统计表格
        reportModel.initFolderTable(report); // 场景列表
        reportModel.initPublicTable(report); // 模型库列表
    },
    // 导出失败报告
    exportFailedReport: function (lack) {
        $('.exportFailed').removeClass('hide');
        $('.exportSuccess').addClass('hide');
        reportModel.initLackTable(lack); // 模型库列表
    },
    // 导出报告 - 成功 - 总统计表格
    initTotalTable: function (report) {
        var totalAll = report.statistics ? report.statistics : {};
        var totalPri = report.pri ? report.pri.priStatistics : {};
        var totalPub = report.pub ? report.pub.pubStatistics : {};
        var totalDataArr = [];
        var totalKeyMap = reportModel.totalKeyMap;
        for (var key in totalKeyMap) {
            if (!totalKeyMap[key]) {
                console.log('function initTotalTable, key missed: ' + key);
                continue;
            }
            totalDataArr.push({
                'key': totalKeyMap[key],
                'totalAll': totalAll ? (totalAll[key] ? totalAll[key] : 0) : 0,
                'totalPri': totalPri ? (totalPri[key] ? totalPri[key] : 0) : 0,
                'totalPub': totalPub ? (totalPub[key] ? totalPub[key] : 0) : 0
            });
        }

        $.fn.dataTable.ext.errMode = 'none';
        $('#exportSuccess_total')
            .on('error.dt', function (e, settings, techNote, message) {
                console.log('An error has been reported by DataTables: ', message);
            })
            .width('100%').dataTable({
            "searching": false, // 是否开启搜索功能
            "ordering": false,
            "destroy": true,
            "bLengthChange": false,
            "paging": false,
            "info": false,
            "autoWidth": false,
            "columns": [
                {
                    "title": "统计项",
                    "data": "key",
                    "width": "25%"
                },
                {
                    "title": "总计",
                    "data": "totalAll",
                    "width": "25%"
                },
                {
                    "title": "场景下",
                    "data": "totalPri",
                    "width": "25%"
                },
                {
                    "title": "模型库下",
                    "data": "totalPub",
                    "width": "25%"
                }
            ],
            "data": totalDataArr
        });
    },
    // 导出报告 - 成功  - 场景列表
    initFolderTable: function (report) {
        var foldersObj = report.pri ? report.pri.folder : {};
        var dataArr = [];
        for (var folderId in foldersObj) {
            var folderName = foldersObj[folderId]['folderName'];
            var modelsObj = foldersObj[folderId]['modelHeader'];
            var modelNum = modelsObj ? (Object.keys(modelsObj).length) : 0;

            var numFlag = -1;
            for (var modelId in modelsObj) {
                if (numFlag < 0) {
                    numFlag = modelNum;
                } else {
                    numFlag = 0;
                }
                var modelName = modelsObj[modelId]['modelName'];
                var versionsObj = modelsObj[modelId]['version'];
                var versionNum = versionsObj ? (Object.keys(versionsObj).length) : 0;
                dataArr.push({
                    'folderName': folderName,
                    'modelNum': modelNum,
                    'modelName': modelName,
                    'versionNum': versionNum,
                    'numFlag': numFlag
                });
            }
        }

        $('#exportSuccess_folder')
            .on('error.dt', function (e, settings, techNote, message) {
                console.log('An error has been reported by DataTables: ', message);
            })
            .width('100%').dataTable({
            "searching": false, // 是否开启搜索功能
            "ordering": false,
            "destroy": true,
            "bLengthChange": false,
            "paging": false,
            "info": false,
            "autoWidth": false,
            "columns": [
                {
                    "title": "场景名称",
                    "data": "folderName",
                    "width": "25%"
                },
                {
                    "title": "模型个数",
                    "data": "modelNum",
                    "width": "25%"
                },
                {
                    "title": "模型名称",
                    "data": "modelName",
                    "width": "25%"
                },
                {
                    "title": "版本个数",
                    "data": "versionNum",
                    "width": "25%"
                }
            ],
            "data": dataArr,
            "columnDefs": [{
                "targets": [0, 1],
                "createdCell": function (td, cellData, rowData, row, col) {
                    var rowspan = rowData.numFlag;
                    if (rowspan > 0) {
                        $(td).attr('rowspan', rowspan);
                    } else {
                        $(td).remove();
                    }
                }
            }]
        });
    },
    // 导出报告 - 成功  - 模型库列表
    initPublicTable: function (report) {
        var groupsObj = report.pub ? report.pub.modelGroup : {};
        var dataArr = [];
        for (var groupId in groupsObj) {
            var modelGroupName = groupsObj[groupId]['modelGroupName'];
            var modelsObj = groupsObj[groupId]['modelHeader'];
            var modelNum = modelsObj ? (Object.keys(modelsObj).length) : 0;

            var numFlag = -1;
            for (var modelId in modelsObj) {
                if (numFlag < 0) {
                    numFlag = modelNum;
                } else {
                    numFlag = 0;
                }
                var modelName = modelsObj[modelId]['modelName'];
                var versionsObj = modelsObj[modelId]['version'];
                var versionNum = versionsObj ? (Object.keys(versionsObj).length) : 0;
                dataArr.push({
                    'modelGroupName': modelGroupName,
                    'modelNum': modelNum,
                    'modelName': modelName,
                    'versionNum': versionNum,
                    'numFlag': numFlag
                });
            }
        }

        $('#exportSuccess_public')
            .on('error.dt', function (e, settings, techNote, message) {
                console.log('An error has been reported by DataTables: ', message);
            })
            .width('100%').dataTable({
            "searching": false, // 是否开启搜索功能
            "ordering": false,
            "destroy": true,
            "bLengthChange": false,
            "paging": false,
            "info": false,
            "autoWidth": false,
            "columns": [
                {
                    "title": "模型组名称",
                    "data": "modelGroupName",
                    "width": "25%"
                },
                {
                    "title": "模型个数",
                    "data": "modelNum",
                    "width": "25%"
                },
                {
                    "title": "模型名称",
                    "data": "modelName",
                    "width": "25%"
                },
                {
                    "title": "版本个数",
                    "data": "versionNum",
                    "width": "25%"
                }
            ],
            "data": dataArr,
            "columnDefs": [{
                "targets": [0, 1],
                "createdCell": function (td, cellData, rowData, row, col) {
                    var rowspan = rowData.numFlag;
                    if (rowspan > 0) {
                        $(td).attr('rowspan', rowspan);
                    } else {
                        $(td).remove();
                    }
                }
            }]
        });
    },
    // 导出报告 - 失败  - 缺失表格
    initLackTable: function (lack) {
        var lackDataArr = [];
        if (lack) {
            var lackMap = reportModel.lackMap;
            for (var lackTypeKey in lack) {
                if (!lackMap[lackTypeKey]) {
                    console.log('function initLackTable, lackTypeKey missed: ' + lackTypeKey);
                    continue;
                }
                var lackObjs = lack[lackTypeKey]; // 某一缺失类型的所有缺失对象 的对象
                var lackType = lackMap[lackTypeKey]['type'];
                var lackIdCol = lackMap[lackTypeKey]['id'];
                var lackNameCol = lackMap[lackTypeKey]['name'];

                var numFlag = -1;
                for (var lackObjKey in lackObjs) {
                    if (numFlag < 0) {
                        numFlag = lackObjs ? (Object.keys(lackObjs).length) : 0;
                    } else {
                        numFlag = 0;
                    }
                    var lackObj = lackObjs[lackObjKey];
                    var lackId = lackIdCol ? (lackObj[lackIdCol] ? lackObj[lackIdCol] : '') : '';
                    var lackName = lackNameCol ? (lackObj[lackNameCol] ? lackObj[lackNameCol] : '') : '';

                    var fromObjs = lackObj['from'];
                    for (var fromLackKey in fromObjs) {
                        if (!lackMap[fromLackKey]) {
                            console.log('function initLackTable, fromLackKey missed: ' + fromLackKey);
                            continue;
                        }
                        var lackFromType = lackMap[fromLackKey]['type'];
                        var fromLackNameCol = lackMap[fromLackKey]['name'];
                        var versionCol = lackMap[fromLackKey]['version'] ? lackMap[fromLackKey]['version'] : '';

                        var fromLackObjs = fromObjs[fromLackKey];
                        for (var lackObjKey in fromLackObjs) {
                            var lackObj = fromLackObjs[lackObjKey];
                            var lackFromName = fromLackNameCol ? (lackObj[fromLackNameCol]) : '';
                            var versionFlag = !!lackObj['version'];

                            if (versionFlag) { // 有版本信息
                                var versionObjs = lackObj['version'];
                                for (var versionObjId in versionObjs) {
                                    lackDataArr.push({
                                        'lackType': lackType,
                                        'lackId': lackId,
                                        'lackName': lackName,
                                        'lackFromType': lackFromType,
                                        'lackFromName': lackFromName,
                                        'version': versionObjs[versionObjId][versionCol],
                                        'lackTypeNum': numFlag
                                    });
                                }
                            } else {
                                lackDataArr.push({
                                    'lackType': lackType,
                                    'lackId': lackId,
                                    'lackName': lackName,
                                    'lackFromType': lackFromType,
                                    'lackFromName': lackFromName,
                                    'version': '',
                                    'lackTypeNum': numFlag
                                });
                            }
                        }
                    }
                }
            }
        }

        $.fn.dataTable.ext.errMode = 'none';
        $('#exportFailed_lack')
            .on('error.dt', function (e, settings, techNote, message) {
                console.log('An error has been reported by DataTables: ', message);
            })
            .width('100%').dataTable({
            "searching": false, // 是否开启搜索功能
            "ordering": false,
            "destroy": true,
            "bLengthChange": false,
            "paging": false,
            "info": false,
            "autoWidth": false,
            "columns": [
                {
                    "title": "缺失类型",
                    "data": "lackType"
                },
                {
                    "title": "缺失ID",
                    "data": "lackId"
                },
                {
                    "title": "缺失名称",
                    "data": "lackName"
                },
                {
                    "title": "缺失来源类型",
                    "data": "lackFromType"
                },
                {
                    "title": "缺失来源名称",
                    "data": "lackFromName"
                },
                {
                    "title": "来源版本",
                    "data": "version"
                }
            ],
            "data": lackDataArr,
            "columnDefs": [{
                "targets": [0],
                "createdCell": function (td, cellData, rowData, row, col) {
                    var rowspan = rowData.lackTypeNum;
                    if (rowspan > 0) {
                        $(td).attr('rowspan', rowspan);
                    } else {
                        $(td).remove();
                    }
                }
            }]
        });
    },
    // 创建下载标签
    createDownLoad: function (fileName) {
        var ele = '<a class="btn btn-primary downLoadBtn" init-flag="0" style="margin-right: 3px;" onclick="reportModel.downLoadCheck(\'' + fileName + '\');" >下载导出结果</a>';
        $('#closeExportReport').before(ele);
    },
    // 下载文件校验
    downLoadCheck: function (fileName) {
        var initFlag = $('#exportReportAlertModal .downLoadBtn').attr('init-flag');
        if (initFlag === '0') {
            $.ajax({
                url: webpath + '/model/importAndExport/export/downLoader/check',
                type: 'POST',
                dataType: "json",
                data: {filePath: fileName + '.zip'},
                success: function (data) {
                    if (data.status === 0) {
                        var download = fileName + '.zip';
                        var href = webpath + '/model/importAndExport/export/downLoader?filePath=' + download;

                        // 修改页面上的下载按钮属性
                        $('#exportReportAlertModal .downLoadBtn').prop('href', href);
                        $('#exportReportAlertModal .downLoadBtn').prop('download', download);
                        $('#exportReportAlertModal .downLoadBtn').attr('init-flag', '1');

                        // 创建隐藏的可下载链接
                        var eleLink = document.createElement('a');
                        eleLink.download = download;
                        eleLink.style.display = 'none';
                        eleLink.href = href;
                        document.body.appendChild(eleLink); // 触发点击
                        eleLink.click();
                        document.body.removeChild(eleLink); // 然后移除

                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        }
    }
};