/**
 * 模型库管理页面
 * data:2019/09/16
 * author:bambi
 */

function initPage() {
    // 时间选择插件：选择时间和日期
    $(".form-datetime").datetimepicker({
        weekStart: 1,
        todayBtn: 1,
        autoclose: 1,
        todayHighlight: 1,
        startView: 2,
        forceParse: 0,
        showMeridian: 1,
        format: "yyyy-mm-dd hh:ii:ss"
    });

    // 搜索功能
    $('.modelBaseSearch').click(function () {
        // tableType: 0模型列表 1组列表 2版本展示列表
        var tableType = $(this).attr('tableType');
        var inputs;
        if (tableType == '0') {
            inputs = $('#modelBasePageContent .modelBaseWrapper .input-group .form-control');
        } else if (tableType == '1') {
            inputs = $('#modelBasePageContent .modelBaseGroupWrapper .input-group .form-control');
        } else if (tableType == '2') {
            inputs = $('#modelVersionTableAlertModal .selfAdaptionLeft .input-group .form-control');
        } else {
            return;
        }
        var obj = {};
        for (var i = 0; i < inputs.length; i++) {
            if ($.trim($(inputs[i]).val()) == '') {
                continue;
            }
            obj[$(inputs[i]).attr('data-col')] = $.trim($(inputs[i]).val());
        }
        if (tableType == '0') { // 0模型列表
            initModelBaseTable(obj);
        } else if (tableType == '1') { // 1组列表
            initModelBaseGroupTable(obj);
        } else if (tableType == '2') { // 2版本展示列表
            obj['isPublic'] = '1';
            // obj['ruleName'] = $('#versionTable_title').text();
            obj['ruleName'] = $('#versionTable_title').attr('ruleName');
            if (obj.ruleStatus) {
                obj['ruleStatus'] = $('#enableInput').attr('isEnable');
            }
            initVersionTable(obj);
        }
    });

    // 模型基本信息
    $('#addModelBase').on('click', function () { // 新增模型
        modelBaseModal.showBase(0);
    });
    $('#saveModelBase').on('click', function () { // 修改保存模型基本信息
        modelBaseModal.saveBase();
    });
    $('#firstVersion').on('click', function () { // 新增模型下的第一版本
        var ruleType = $('#ruleTypeSelector option:selected').attr('ruleType');
        pageJumpObj.newModelPage(ruleType, '1', '');
    });

    // 模型组
    $('#addModelBaseGroup').on('click', function () { // 添加模型组
        modelGroupModal.show(0);
    });
    $('#saveModelBaseGroup').on('click', function () { // 保存模型组
        modelGroupModal.saveRuleSetGourp();
    });

    // 模型版本列表
    $('.enableList li').on('click', function () { // 搜索启用/停用下拉框
        var val = $(this).first().text();
        var isEnable = $(this).attr('isEnable');
        $('#enableInput').val(val).attr('isEnable', isEnable);
    });
    $('#addModelVersion').on('click', function () { // 添加新版本(模型下已有版本后的新增版本)
        // var ruleName = $('#versionTable_title').text();
        var ruleName = $('#versionTable_title').attr('ruleName');
        var ruleType = $('#versionTable_title').attr("ruleType");
        pageJumpObj.newModelPage(ruleType, '2', ruleName);
    });

    // 导入
    $('#importBtn').on('click', function () {
        pageJumpObj.import();
    });
}

var pageJumpObj = {
    /** 新建模型/新版本
     *      ruleType: 模型类型 1规则模型 0评分模型
     *      pageType insert[0场景下新建模型 1模型库模型第一版本 2模型库新增版本] update[3场景下 4模型库] view[5场景下 6模型库]
     *      ruleName: 模型名称
     */
    newModelPage: function (ruleType, pageType, ruleName) {
        $('#modelBaseAlertModal').modal('hide');
        $('#modelVersionTableAlertModal').modal('hide');
        var flagName = $('#folderMenuWrap').attr('class');
        var url = webpath + "/rule/ruleConfig?folderId=" + folderId + "&ruleType=" + ruleType + "&childOpen=" + flagName + "&pageType=" + pageType;
        if (ruleName != '') {
            url += "&ruleName=" + ruleName;
        }
        creCommon.loadHtml(url);
    },
    /** 修改/查看版本
     *      ruleId: 模型Id
     *      isEdit: 0查看 1修改
     */
    echoModelPage: function (ruleId, isEdit) {
        $('#modelVersionTableAlertModal').modal('hide');
        var flagName = $('#folderMenuWrap').attr('class');
        var pageType = (isEdit === 1) ? '4' : '6';
        var url = webpath + "/rule/updateRule?ruleId=" + ruleId + "&folderId=" + folderId + "&childOpen=" + flagName + "&pageType=" + pageType;
        creCommon.loadHtml(url);
    },
    /** 测试
     *      ruleId: 模型id
     *      ruleName: 模型名称
     */
    testRule: function (ruleId, ruleName, moduleName) {
        if (ruleId === null || ruleId === '' || ruleName === '') {
            return;
        }
        // 规则测试 authCheck
        $.ajax({
            url: webpath + '/rule/pub/test/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {"ruleName": ruleName},
            success: function (data) {
                if (data.status === 0) {
                    var flagName = $('#folderMenuWrap').attr('class');
                    var url = webpath + "/ruleTest/index?ruleId=" + ruleId + "&folderId=" + folderId + "&ruleName=" + ruleName + "&moduleName=" + moduleName + "&childOpen=" + flagName;
                    creCommon.loadHtml(url);
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 模型发布
    publishModel: function (ruleId) {
        if (!ruleId) {
            return;
        }
        $.ajax({
            url: webpath + "/rule/changeToExecute",
            data: {"ruleId": ruleId},
            type: "post",
            dataType: "json",
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('发布成功');
                    // initVersionTable({"ruleName": $('#versionTable_title').text(), "isPublic": '1'});
                    initVersionTable({"ruleName": $('#versionTable_title').attr('ruleName'), "isPublic": '1'});
                } else {
                    failedMessager.show('发布失败，' + data.msg);
                }
            }
        });
    },
    // 模型克隆
    clone: function (ruleId, ruleName, ruleDesc, ruleType, isPublic, modelGroupId, deptId, deptName, partnerCode, partnerName) {
        $.ajax({
            url: webpath + '/rule/pub/clone/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {"ruleName": ruleName},
            success: function (data) {
                if (data.status === 0) {
                    // 暂时隐藏上层弹出框
                    modelVersionTableModal.hidden();
                    var param = {
                        oldRuleId: {
                            ruleId: ruleId,
                            ruleName: ruleName,
                            ruleDesc: ruleDesc,
                            ruleType: ruleType,
                            isPublic: isPublic,
                            modelGroupId: modelGroupId,
                            deptId: deptId,
                            deptName: deptName,
                            partnerCode: partnerCode,
                            partnerName: partnerName
                        }
                    };
                    // 传递参数，合并对象
                    ruleClone.data = Object.assign({}, ruleClone.data, param);
                    // 打开弹出框
                    ruleClone.handleModelToggle(true);
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 试算跳转
    ruleTrial: function (ruleId, ruleName, moduleName) {
        if (ruleId === null || ruleId === '' || ruleName === '') {
            return;
        }
        $.ajax({
            url: webpath + '/rule/pub/trial/checkAuth', // 试算 authCheck
            type: 'GET',
            dataType: "json",
            data: {"ruleName": ruleName},
            success: function (data) {
                if (data.status === 0) {
                    var url = webpath + "/ruleTrial/index?ruleId=" + ruleId + "&folderId=" + folderId + "&ruleName=" + ruleName + "&moduleName=" + moduleName + "&childOpen=o";
                    creCommon.loadHtml(url);
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 版本导出
    exportVersion: function (pageType, rootLevel, params) {
        $('#modelVersionTableAlertModal').modal('hide');
        exportModal.initExportPage(pageType, rootLevel, params, function () {
            $('#modelVersionTableAlertModal').modal({'show': 'center', "backdrop": "static"});
        });
    },
    // 导入
    import: function () {
        importModal.initUploadPage(folderId, false);
    },
    // 服务API
    serviceApi: function (ruleId) {
        $('#modelVersionTableAlertModal').modal('hide');
        serviceAPIModal.initPage(folderId, ruleId, function () {
            $('#modelVersionTableAlertModal').modal({'show': 'center', "backdrop": "static"});
        });
    }
};

/**
 * 模型基础信息及操作
 */
var modelBaseModal = {
    // 展开模型头基础信息弹框
    showBase: function (handleType, $this) {
        var detail = {};
        if ($this) {
            var curRow = $this.parentNode.parentNode;
            detail = $('#modelBaseTable').DataTable().row(curRow).data();
        }

        // handleType: 0新增 1修改 2查看
        $('#modelBaseAlertModal form')[0].reset();
        $('#modelBaseAlertModal .modal-footer button').css('display', 'none');
        $('#modelBaseAlertModal form').validator('cleanUp'); // 清除表单中的全部验证消息
        $('#modelBaseAlertModal').removeAttr('oldRuleName');
        $('#modelBaseAlertModal').removeAttr('oldModuleName');
        if (handleType === 0) {
            $('#firstVersion, #cancelModelBase').css('display', 'inline-block');
            $('.notFirst').addClass('hide');
            $('#modelBaseAlertModal .modal-title').text('添加模型');
            $('#modelBaseAlertModal').attr('handleType', handleType).modal({'show': 'center', "backdrop": "static"});
            $('#modelBaseAlertModal .form-control').removeAttr('disabled');
        } else if (handleType === 1) {
            $.ajax({
                url: webpath + '/rule/update/checkAuthModelPub',
                type: 'GET',
                dataType: "json",
                data: {"ruleName": detail.ruleName},
                success: function (data) {
                    if (data.status === 0) {
                        $('#saveModelBase, #cancelModelBase').css('display', 'inline-block');
                        $('.notFirst').removeClass('hide');
                        $('#modelBaseAlertModal .modal-title').text('修改模型');
                        modelBaseModal.echoData(detail); // 数据回显
                        $('#modelBaseAlertModal').attr('handleType', handleType).modal({
                            'show': 'center',
                            "backdrop": "static"
                        });
                        $('#modelBaseAlertModal .form-control').removeAttr('disabled');
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType === 2) {
            $('#modelBaseAlertModal .modal-footer .notView button').css('display', 'none');
            $('#closeViewModalBase').css('display', 'inline-block');
            $('.notFirst').removeClass('hide');
            $('#modelBaseAlertModal .modal-title').text('查看模型');
            modelBaseModal.echoData(detail); // 数据回显
            $('#modelBaseAlertModal').attr('handleType', handleType).modal({'show': 'center', "backdrop": "static"});
            $('#modelBaseAlertModal .form-control').attr('disabled', true);
        } else {
            return;
        }
    },
    // 关闭弹框
    hidden: function () {
        $('#modelBaseAlertModal').modal('hide');
    },
    // 回显规则集基础信息
    echoData: function (detail) {
        var data = detail ? detail : {};
        for (var key in data) {
            if (key === 'modelGroupId') { // 模型组单独处理
                $('#modelBaseAlertModal .modelBaseGroupSelector option[group-id="' + data[key] + '"]').prop('selected', true);
                continue;
            }
            if (key === 'ruleType') { // 模型类型单独处理
                $('#ruleTypeSelector option[ruleType="' + data[key] + '"]').prop('selected', true);
                continue;
            }
            if (key === 'ruleName') { // 记录模型名称(主键)
                $('#modelBaseAlertModal').attr('oldRuleName', data[key]);
                continue;
            }
            if (key === 'moduleName') { // 记录模型展示名称
                $('#modelBaseAlertModal').attr('oldModuleName', data[key]);
            }
            var target = $("#modelBaseAlertModal .form-control[col-name='" + key + "']");
            if (target.length > 0) {
                $(target).val(data[key]);
            }
        }
    },
    // 修改规则集头信息submit
    saveBase: function () {
        // 表单验证
        if (!$('#modelBaseAlertModal form').isValid()) {
            return;
        }
        var handleType = $('#modelBaseAlertModal').attr('handleType'); // 0新增 1修改
        if (handleType != 1) { // 新增需要新建版本
            return;
        }
        var obj = modelBaseModal.getBaseData(handleType);
        if (obj) {
            $.ajax({
                url: webpath + '/rule/public/header/update', // 修改头信息
                type: 'POST',
                dataType: "json",
                data: obj,
                success: function (data) {
                    if (data.status === 0) {
                        // initModelBaseTable();
                        $('.modelBaseSearch').trigger('click');
                        modelBaseModal.hidden();
                        successMessager.show('保存成功');
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        }
    },
    // 获取修改头信息表单数据
    getBaseData: function () {
        var obj = {};
        var inputsArr = $('#modelBaseAlertModal .form-control');
        for (var i = 0; i < inputsArr.length; i++) {
            obj[$(inputsArr[i]).attr('col-name')] = $.trim($(inputsArr[i]).val());
        }
        obj['modelGroupId'] = $('#modelBaseAlertModal .modelBaseGroupSelector option:selected').attr('group-id');
        obj['ruleType'] = $('#ruleTypeSelector option:selected').attr('ruleType');
        obj['oldRuleName'] = $('#modelBaseAlertModal').attr('oldRuleName');
        obj['oldModuleName'] = $('#modelBaseAlertModal').attr('oldModuleName');
        obj['isPublic'] = '1';
        return obj;
    },
    // 删除模型: ruleName 模型名称
    deleteModel: function (ruleName) {
        if (ruleName) {
            // 删除权限校验
            $.ajax({
                url: webpath + '/rule/pub/delete/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {"ruleName": ruleName},
                success: function (data) {
                    if (data.status === 0) {
                        confirmAlert.show('该模型下所有版本也会一并删除，是否继续？', function () {
                            $.ajax({
                                // url: webpath + '/rule/public/header/delete',
                                url: webpath + '/rule/pub/deleteByName',
                                type: 'POST',
                                dataType: "json",
                                data: {'ruleName': ruleName},
                                success: function (data) {
                                    if (data.status === 0) {
                                        // initModelBaseTable();
                                        $('.modelBaseSearch').trigger('click');
                                        successMessager.show('删除成功');
                                    } else {
                                        failedMessager.show(data.msg);
                                    }
                                }
                            });
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        }
    }
}

/**
 * 模型版本列表
 */
var modelVersionTableModal = {
    // 初始化模型版本列表页面
    initPage: function (ruleName, ruleType, moduleName) {
        $('#versionTable_title').text(moduleName || '');
        // $('#versionTable_title').text(ruleName || ''); // ruleName 模型头信息主键
        $('#versionTable_title').attr('ruleName', ruleName); // ruleName 模型头信息主键
        $('#versionTable_title').attr("ruleType", ruleType); // ruleType 模型类型
        $('#modelVersionTableAlertModal .selfAdaptionLeft .input-group .form-control').val(''); // 清空搜索栏内容
        initVersionTable({"ruleName": ruleName, "isPublic": '1'}); // 根据模型名称初始化列表
        modelVersionTableModal.show();
    },
    // 展开弹框
    show: function () {
        $('#modelVersionTableAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 关闭弹框
    hidden: function () {
        $('#modelVersionTableAlertModal').modal('hide');
    },
    // 删除模型版本: ruleId 版本id
    deleteVersion: function (ruleId, ruleStatus, ruleName) {
        // 启用状态下不可删除
        if (ruleStatus === '1') {
            failedMessager.show('启用状态下不可删除！');
            return;
        }
        // 版本删除权限校验
        $.ajax({
            url: webpath + '/rule/pub/delete/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {"ruleName": ruleName},
            success: function (data) {
                if (data.status === 0) {
                    modelVersionTableModal.hidden();
                    // 删除最后一个版本则将模型头信息也一并删除
                    var trLength = $('#modelVersionTable tbody tr').length;
                    var msg = "是否确认删除？";
                    var lastFlag = false;
                    if (trLength === 1) {
                        msg = '删除唯一版本会将该模型一并删除，是否继续？';
                        lastFlag = true;
                    }
                    confirmAlert.show(msg, function () {
                        $.ajax({
                            url: webpath + '/rule/public/version/deleteModelBaseRule',
                            type: 'POST',
                            dataType: "json",
                            data: {"ruleId": ruleId, "ruleName": ruleName},
                            success: function (data) {
                                if (data.status === 0) {
                                    successMessager.show('删除成功');
                                    if (lastFlag) {
                                        // initModelBaseTable();
                                        $('.modelBaseSearch').trigger('click');
                                    } else {
                                        modelVersionTableModal.show();
                                        // initVersionTable({"ruleName": $('#versionTable_title').text(), "isPublic": '1'});
                                        initVersionTable({
                                            "ruleName": $('#versionTable_title').attr('ruleName'),
                                            "isPublic": '1'
                                        });
                                    }
                                } else {
                                    failedMessager.show(data.msg);
                                }
                            }
                        });
                    }, function () {
                        modelVersionTableModal.show();
                    });
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 启用/停用
    changeStatus: function (that, ruleId, ruleName) {
        if (ruleId === null || ruleId === '') {
            return;
        }
        var value = that.is(":checked");
        var status = '';
        var msg = '';
        if (value) {
            status = "1";
            msg = '规则启用';
        } else {
            status = "2";
            msg = '规则停用';
        }

        // 启用停用权限 authCheck
        $.ajax({
            url: webpath + '/rule/pub/enable/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {"ruleName": ruleName},
            success: function (data) {
                if (data.status === 0) {
                    // 修改状态
                    $.ajax({
                        url: webpath + "/rule/public/version/updateModelBaseRuleStatus",
                        data: {
                            "ruleName": ruleName,
                            "folderId": folderId,
                            "ruleId": ruleId,
                            "status": status
                        },
                        type: "post",
                        dataType: "json",
                        success: function (data) {
                            if (data.status === 0) {
                                successMessager.show(msg + '成功');
                            } else {
                                failedMessager.show(msg + '失败');
                            }
                            // 刷新列表更新状态
                            // initVersionTable({"ruleName": $('#versionTable_title').text(), "isPublic": '1'});
                            initVersionTable({"ruleName": $('#versionTable_title').attr('ruleName'), "isPublic": '1'});
                        }
                    });
                } else {
                    failedMessager.show(data.msg);
                    if (value) {
                        that.prop('checked', false);
                    } else {
                        that.prop('checked', true)
                    }
                }
            }
        });
    },
    // 更新缓存
    updateRuleCache: function (ruleId) {
        if (ruleId == null || ruleId == '') {
            return;
        }
        $.ajax({
            url: webpath + "/rule/updateRuleCache",
            data: {
                "folderId": folderId,
                "ruleId": ruleId
            },
            type: "post",
            dataType: "json",
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('更新缓存成功');
                } else {
                    failedMessager.show('更新缓存失败，' + data.msg);
                }
            }
        });
    },
}

/**
 * 模型组
 */
var modelGroupModal = {
    show: function (handleType, $this) {
        var detail = {};
        if ($this) {
            var curRow = $this.parentNode.parentNode;
            detail = $('#modelBaseGroupTable').DataTable().row(curRow).data();
        }
        // handleType: 0新增 1修改 2查看
        $('#modelBaseGroupAlert form')[0].reset();
        $('#modelBaseGroupAlert .modal-footer button').css('display', 'none');
        $('#modelBaseGroupAlert .form-control').attr('disabled', false);
        if (handleType === 0) {
            $('#modelBaseGroupAlert .modal-footer .notView button').css('display', 'inline-block');
            $('#modelBaseGroupAlert .modal-title').text('').text('添加产品');
            modelGroupModal.channelNameList();
        } else if (handleType == 1) {
            $('#modelBaseGroupAlert .modal-footer .notView button').css('display', 'inline-block');
            $('#modelBaseGroupAlert .modal-title').text('').text('修改产品');
            modelGroupModal.echoGroupData(detail);
            modelGroupModal.channelNameList();
        } else if (handleType == 2) {
            $('#modelBaseGroupAlert .modal-footer #closeModelBaseGroup').css('display', 'inline-block');
            $('#modelBaseGroupAlert .modal-title').text('').text('查看产品');
            $('#modelBaseGroupAlert .form-control').attr('disabled', true);
            modelGroupModal.echoGroupData(detail);
            // modelGroupModal.channelNameList();
        }
        $('#modelBaseGroupAlert').attr('handleType', handleType).modal({'show': 'center', "backdrop": "static"});
    },
    // 关闭添加参数组弹框
    hiddenAddGroupAlert: function () {
        $('#modelBaseGroupAlert').modal('toggle', 'center');
    },
    // 回显组数据
    echoGroupData: function (detail) {
        if (detail) {
            var data = detail ? detail : {};
            for (var key in data) {
                if (key === 'modelGroupId') { // 回显组id
                    $('#modelBaseGroupAlert').attr('groupId', data[key]);
                    continue;
                }
                if (key === 'channelId') { // 回显渠道id
                    // $('#modelBaseGroupAlert').attr('channelId', data[key]);
                    $('#modelGroupModal .channelSelector option[channelId="' + data[key] + '"]').prop('selected', true);
                    continue;
                }
                var target = $("#modelBaseGroupAlert .form-control[col-name='" + key + "']");
                if (target.length > 0) {
                    $(target).val(data[key]);
                }
            }
        }
    },
    channelNameList: function () {
        // 初始化渠道列表选择
        $.ajax({
            url: webpath + '/choose/channelNameList',
            type: 'GET',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.status === 0) {
                    var htmlStr = '';
                    for (var i = 0; i < data.data.length; i++) {
                        htmlStr += '<option channel-id=\'' + data.data[i].channelId + '\'>' + data.data[i].channelName+'--'+ data.data[i].deptName + '</option>';
                    }
                   $('#channelSelector').empty().html(htmlStr);
                } else {
                    failedMessager.show(data.msg);
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            },
        });
    },
    // 保存组数据
    saveRuleSetGourp: function () {
        // 表单验证
        if (!$('#modelBaseGroupAlert form').isValid()) {
            return;
        }
        var handleType = $('#modelBaseGroupAlert').attr('handleType'); // 0新增 1修改
        var urlStr = '';
        if (handleType == 0) {
            urlStr = '/modelBase/group/create';
        } else if (handleType == 1) {
            urlStr = '/modelBase/group/update';
        } else {
            return;
        }
        var obj = modelGroupModal.getGroupData(handleType);
        if (obj) {
            $.ajax({
                url: webpath + urlStr,
                type: 'POST',
                dataType: "json",
                data: obj,
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('保存成功');
                        initModelBaseGroupTable();
                        // initModelBaseTable();
                        $('.modelBaseSearch').trigger('click');
                        initModelBaseGroup(); // 刷新模型组下拉框
                        modelGroupModal.hiddenAddGroupAlert();
                    } else {
                        failedMessager.show(data.msg);
                    }
                },
                error: function (data) {
                    failedMessager.show(data.msg);
                }
            });
        }
    },
    // 获取组表单数据
    getGroupData: function (handleType) {
        var obj = {};
        var inputs = $('#modelBaseGroupAlert form .form-control');
        for (var i = 0; i < inputs.length; i++) {
            obj[$(inputs[i]).attr('col-name')] = $(inputs[i]).val();
        }
        if (handleType == 1) { //修改需要加上组id
            obj['modelGroupId'] = $('#modelBaseGroupAlert').attr('groupId');
            obj['channelList'] = $('#modelBaseGroupAlert .channelSelector option:selected').attr('channel-id');
        }
        return obj;
    },
    // 删除模型集组
    deleteGroup: function (groupId) {
        if (groupId) {
            confirmAlert.show('是否确认删除？', function () {
                $.ajax({
                    url: webpath + '/modelBase/group/delete',
                    type: 'POST',
                    dataType: "json",
                    data: {'modelGroupId': groupId},
                    success: function (data) {
                        if (data.status === 0) {
                            successMessager.show('删除成功');
                            initModelBaseGroupTable();
                            // initModelBaseTable();
                            $('.modelBaseSearch').trigger('click');
                            initModelBaseGroup(); // 刷新模型组下拉框
                        } else {
                            failedMessager.show(data.msg);
                        }
                    }
                });
            });
        }
    }
}

/**
 * 初始化 模型头信息表格
 */
function initModelBaseTable(obj) {
    $('#modelBaseTable').width('100%').dataTable({
        "searching": false,
        "ordering": false,
        "destroy": true,
        "bLengthChange": false,
        "pagingType": "full_numbers",
        "paging": true,
        "info": true,
        "serverSide": true,
        "pageLength": 10,
        "columns": [
            {"title": "模型组", "data": "modelGroupName", "width": "10%"},
            // {"title": "模型名称", "data": "ruleName"},
            {"title": "模型名称", "data": "moduleName", "width": "15%"},
            {
                "title": "模型类型", "data": "ruleType", "width": "9%", "render": function (data, type, row) {
                    switch (data) {
                        case '1':
                            return '规则模型';
                        case '0':
                            return '评分模型';
                        default:
                            return '--';
                    }
                }
            },
            {"title": "模型描述", "data": "ruleDesc", "width": "13%"},
            {"title": "创建人", "data": "createPerson", "width": "8%"},
            {"title": "创建时间", "data": "createDate", "width": "13%"},
            {
                "title": "操作", "data": null, "width": "32%", "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<span type="button" class="cm-tblB detailBtn" onclick="modelBaseModal.showBase(2, this)">查看</span>';
                    htmlStr += '<span type="button" class="cm-tblB" onclick="modelBaseModal.showBase(1, this)">修改</span>';
                    htmlStr += '<span type="button" class="cm-tblB detailBtn" onclick="modelVersionTableModal.initPage(\'' + row.ruleName + '\', \'' + row.ruleType + '\', \'' + row.moduleName + '\')">版本查看</span>';
                    htmlStr += '<span type="button" class="cm-tblB" onclick="exportModal.initExportPage(1, 2, getExportParams(\'' + row.modelGroupId + '\', \'' + row.modelGroupName + '\', \'' + row.ruleName + '\', \'' + row.moduleName + '\'))">导出</span>';
                    htmlStr += '<span type="button" class="cm-tblC delBtn" onclick="modelBaseModal.deleteModel(\'' + row.ruleName + '\')">删除</span>';
                    return htmlStr;
                }
            }],
        ajax: {
            url: webpath + '/rule/public/header/paged',
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

/**
 * 初始化 版本列表
 */
function initVersionTable(obj) {
    if (obj == null) obj = {};
    $('#modelVersionTable').width('100%').dataTable({
        "searching": false,
        "ordering": false,
        "destroy": true,
        "bLengthChange": false,
        "pagingType": "full_numbers",
        "paging": true,
        "info": true,
        "serverSide": true,
        "pageLength": 8,
        "columns": [
            {"title": "版本号", "data": "version"},
            {"title": "版本描述", "data": "versionDesc"},
            {"title": "创建人", "data": "versionCreatePerson"},
            {"title": "创建时间", "data": "versionCreateDate"},
            {
                "title": "状态", "data": "ruleStatus", "render": function (data, type, row) {
                    var disabledStr = (row.version.indexOf('草稿') === -1) ? '' : 'disabled'; // 草稿不能启停用操作
                    // 0准备 1执行中 2停用 -1异常
                    var checkBoxValue = '';
                    var checkHtmlStr = ''; // 控制开关开启状态
                    var htmlStr = "";
                    if ($.trim(data) == '1') {
                        checkBoxValue = '已启用';
                        checkHtmlStr += 'checked';
                    } else {
                        checkBoxValue = '未启用';
                    }
                    htmlStr += '<div class="switch"><input ' + disabledStr + ' onclick="modelVersionTableModal.changeStatus($(this), \'' + row.ruleId + '\', \'' + row.ruleName + '\')" type="checkbox" ' + checkHtmlStr + '><label>' + checkBoxValue + '</label></div>';
                    return htmlStr;
                }
            },
            {
                "title": "操作", "data": null, "render": function (data, type, row) {
                    var ruleStatus = row.ruleStatus;
                    var htmlStr = "";
                    // if (ruleStatus == '1') { // 启用中只能查看不能编辑
                    htmlStr += '<span type="button" class="cm-tblB" onclick="pageJumpObj.echoModelPage(\'' + row.ruleId + '\', 0)">查看</span>';
                    // } else {
                    htmlStr += '<span type="button" class="cm-tblB" onclick="pageJumpObj.echoModelPage(\'' + row.ruleId + '\', 1)">编辑</span>';
                    // }
                    htmlStr += '<span type="button" class="cm-tblC delBtn" onclick="modelVersionTableModal.deleteVersion(\'' + row.ruleId + '\', \'' + row.ruleStatus + '\', \'' + row.ruleName + '\')">删除</span>';
                    htmlStr += '<span type="button" class="cm-tblB detailBtn" onclick="pageJumpObj.testRule(\'' + row.ruleId + '\', \'' + row.ruleName + '\', \'' + row.moduleName + '\')">测试</span>';
                    if (row.version.indexOf('草稿') === -1) {
                        htmlStr += '<span type="button" class="cm-tblB detailBtn" onclick="pageJumpObj.publishModel(\'' + row.ruleId + '\')">发布</span>';
                    }
                    htmlStr += '<span type="button" class="cm-tblB detailBtn" onclick="pageJumpObj.clone( ' +
                        '\'' + row.ruleId + '\', ' +
                        '\'' + row.ruleName + '\', ' +
                        '\'' + row.ruleDesc + '\', ' +
                        '\'' + row.ruleType + '\', ' +
                        '\'' + row.isPublic + '\', ' +
                        '\'' + row.modelGroupId + '\', ' +
                        '\'' + row.deptId + '\', ' +
                        '\'' + row.deptName + '\', ' +
                        '\'' + row.partnerCode + '\', ' +
                        '\'' + row.partnerName + '\'' +
                        ')">克隆</span>';
                    htmlStr += '<span type="button" class="cm-tblB delBtn" onclick="pageJumpObj.ruleTrial(\'' + row.ruleId + '\', \'' + row.ruleName + '\', \'' + row.moduleName + '\')">试算</span>';
                    // htmlStr += '<span type="button" class="cm-tblB" onclick="modelVersionTableModal.updateRuleCache(\'' + row.ruleId + '\')">更新缓存</span>';
                    htmlStr += '<span type="button" class="cm-tblB" onclick="pageJumpObj.exportVersion(1, 3, getExportParams(' +
                        '\'' + row.modelGroupId + '\', ' +
                        '\'' + row.modelGroupName + '\', ' +
                        '\'' + row.ruleName + '\', ' +
                        '\'' + row.moduleName + '\', ' +
                        '\'' + row.ruleId + '\', ' +
                        '\'' + row.version + '\'' +
                        '))">导出</span>';
                    //TODO
                    htmlStr += '<span type="button" class="cm-tblB" onclick="pageJumpObj.serviceApi(\'' + row.ruleId + '\')">服务API</span>';
                    return htmlStr;
                }
            }],
        ajax: {
            // url: webpath + '/rule/public/version/paged',
            url: webpath + '/rule/versions/paged',
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

/**
 * 初始化 组表格
 */
function initModelBaseGroupTable(obj) {
    $('#modelBaseGroupTable').width('100%').dataTable({
        "searching": false,
        "ordering": false,
        "destroy": true,
        "bLengthChange": false,
        "pagingType": "full_numbers",
        "paging": true,
        "info": true,
        "serverSide": true,
        "pageLength": 10,
        "columns": [
            {"title": "产品名称", "data": "modelGroupName", "width": "8%"},
            {"title": "产品编码", "data": "modelGroupCode", "width": "8%"},
            {"title": "产品描述", "data": "modelGroupDesc", "width": "12%"},
            {"title": "调用渠道", "data": "modelGroupChannel", "width": "12%"},
            {"title": "创建时间", "data": "createDate", "width": "10%"},
            {"title": "创建人", "data": "createPerson", "width": "10%"},
            {
                "title": "操作", "data": null, "width": "40%", "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<span type="button" class="cm-tblB" onclick="modelGroupModal.show(2, this)">查看</span>';
                    htmlStr += '<span type="button" class="cm-tblB" onclick="modelGroupModal.show(1, this)">修改</span>';
                    htmlStr += '<span type="button" class="cm-tblB" onclick="exportModal.initExportPage(1, 1, getExportParams(\'' + row.modelGroupId + '\', \'' + row.modelGroupName + '\'))">导出</span>';
                    htmlStr += '<span type="button" class="cm-tblC delBtn" onclick="modelGroupModal.deleteGroup(\'' + row.modelGroupId + '\')">删除</span>';
                    htmlStr += '<span type="button" class="cm-tblB" onclick="modelGroupModal.show(1, this)">设置调用渠道</span>';
                    htmlStr += '<span type="button" class="cm-tblB" onclick="modelGroupModal.show(\'' + row.modelGroupId + '\')">查看模型</span>';
                    return htmlStr;
                }
            }],
        ajax: {
            url: webpath + '/modelBase/group/paged',
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

//  初始化模型组下拉框
function initModelBaseGroup() {
    $.ajax({
        url: webpath + '/modelBase/group/list',
        type: 'POST',
        dataType: "json",
        data: {},
        success: function (data) {
            var htmlStr_list = '';
            var htmlStr_selector = '';
            if (data.status === 0) {
                if (data.data.length > 0) {
                    var data = data.data;
                    for (var i = 0; i < data.length; i++) {
                        htmlStr_list += '<li group-id=\'' + data[i].modelGroupId + '\'><a>' + data[i].modelGroupName + '</a></li>';
                        htmlStr_selector += '<option group-id=\'' + data[i].modelGroupId + '\'>' + data[i].modelGroupName + '</option>';
                    }
                } else {
                    htmlStr_list += "<li group-id='empty'><a>无</a></li>";
                    htmlStr_selector += "<option group-id='empty'>无</option>";
                }
            }
            $('#modelBasePageContent .modelBaseGroupList').empty().html(htmlStr_list); // 下拉框
            $('.modelBaseGroupSelector').empty().html(htmlStr_selector); // 表单
        },
        complete: function () {
            // 绑定事件
            $('#modelBasePageContent .modelBaseGroupList>li').unbind('click').on('click', function () {
                $(this).parent().siblings('.form-control').val($(this).first().text());
            });
        }
    });
}

function getExportParams(groupId, groupName, modelId, modelName, versionId, versionName) {
    return {
        "folderObj": {"folderId": folderId, "folderName": "模型库"},
        "groupObj": {"groupId": groupId, "groupName": groupName},
        "modelObj": {"modelId": modelId, "modelName": modelName},
        "versionObj": {"versionId": versionId, "versionName": versionName}
    };
}

$(function () {
    initPage();
    initModelBaseTable(); // 初始化模型table
    initModelBaseGroup(); // 初始化模型组下拉框
    initModelBaseGroupTable(); // 初始化组table
    if (jumpRuleName != '') { // 跳转至此页面并展示模型名为jumpRuleName的版本列表弹框
        $.ajax({
            url: webpath + '/rule/public/header/info',
            type: 'GET',
            dataType: "json",
            data: {"ruleName": jumpRuleName},
            success: function (data) {
                if (data.status === 0) {
                    modelVersionTableModal.initPage(jumpRuleName, data.data.ruleType, data.data.moduleName);
                }
            }
        });
    }
    // 初始化模型克隆
    ruleClone.init();
    // 克隆模态框关闭事件绑定
    ruleClone.event.onClose = function () {
        modelVersionTableModal.show();
    };
    ruleClone.event.onSuccess = function () {
        modelVersionTableModal.show();
        initModelBaseTable();
    };
});