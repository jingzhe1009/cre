/**
 * 规则集管理
 * data:2019/09/02
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
    $('.ruleSetSearch').click(function () {
        // tableType: 0规则集列表 1组列表 2版本展示列表
        var tableType = $(this).attr('tableType');
        var inputs;
        if (tableType == '0') {
            inputs = $('#ruleSetPageContent .ruleSetWrapper .input-group .form-control');
        } else if (tableType == '1') {
            inputs = $('#ruleSetPageContent .ruleSetGroupWrapper .input-group .form-control');
        } else if (tableType == '2') {
            inputs = $('#ruleSetVersionTableAlertModal .selfAdaptionLeft .input-group .form-control');
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
        if (tableType == '0') { // 0规则集列表
            initRuleSetTable(obj);
        } else if (tableType == '1') { // 1组列表
            initRuleSetGroupTable(obj);
        } else if (tableType == '2') { // 2版本展示列表
            var ruleSetHeaderId = $('#ruleSetVersionTableAlertModal').attr('ruleSetHeaderId');
            obj['ruleSetHeaderId'] = ruleSetHeaderId;
            if (obj.enable) {
                obj['enable'] = $('#enableInput').attr('attrVal');
            }
            ruleSetVersionTableModal.initTable(obj);
        }
    });

    // 规则集基本信息
    $('#addRuleSet').on('click', function () { // 添加规则集基本信息
        ruleSetBaseModal.showBase(0);
    });
    $('#saveRuleSetBase').on('click', function () { // 保存规则集header基本信息
        ruleSetBaseModal.saveBase();
    });

    // 规则集组
    $('#addRuleSetGroup').on('click', function () { // 添加规则集组
        ruleSetGroupModal.show(0);
    });
    $('#saveRuleSetGroup').on('click', function () { // 保存规则集组
        ruleSetGroupModal.saveRuleSetGourp();
    });

    // 规则集版本列表
    $('.enableList li').on('click', function () { // 搜索启用/停用下拉框
        var val = $(this).first().text();
        var attrVal = $(this).attr('enable-value');
        $('#enableInput').val(val).attr('attrVal', attrVal);
    });
    $('#addRuleSetVersion').on('click', function () { // 添加新版本
        // 权限校验
        $.ajax({
            url: webpath + '/ruleSet/version/add/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {"ruleSetHeaderId": $('#ruleSetVersionTableAlertModal').attr('ruleSetHeaderId')},
            success: function (data) {
                if (data.status === 0) {
                    ruleSetVersionModal.initPage(
                        0,
                        versionIndexModal.initFun,
                        versionIndexModal.callBackFun,
                        $('#ruleSetVersionTableAlertModal').attr('ruleSetHeaderId'),
                        '',
                        $('#ruleSetHeaderName').val()
                    );
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    });
}

/**
 * 规则集基础信息及操作
 */
var ruleSetBaseModal = {
    // 展开规则集基础信息弹框
    showBase: function (handleType, $this) {
        var detail = {};
        if ($this) {
            var curRow = $this.parentNode.parentNode;
            detail = $('#ruleSetTable').DataTable().row(curRow).data();
        }
        // handleType: 0新增 1修改 2查看
        $('#ruleSetBaseAlertModal form')[0].reset();
        $('#ruleSetName').removeAttr('disabled');
        $('#ruleSetBaseAlertModal .cron_msg').addClass('hide');
        $('#ruleSetBaseAlertModal .modal-footer button').css('display', 'none');
        $('#ruleSetBaseAlertModal form').validator('cleanUp'); // 清除表单中的全部验证消息
        $('#ruleSetBaseAlertModal').removeAttr('ruleSetHeaderId'); // 清除ruleSetHeaderId标识
        if (handleType == 0) {
            $('#ruleSetBaseAlertModal .modal-footer .notView button').css('display', 'inline-block');
            $('#ruleSetBaseAlertModal .modal-title').text('添加规则集');
            $('#ruleSetBaseAlertModal').attr('handleType', handleType).modal({'show': 'center', "backdrop": "static"});
            $('#ruleSetBaseAlertModal .form-control').removeAttr('disabled');
        } else if (handleType == 1) {
            // 修改权限check
            $.ajax({
                url: webpath + '/ruleSet/update/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {"ruleSetHeaderId": detail['ruleSetHeaderId'] ? detail['ruleSetHeaderId'] : ''},
                success: function (data) {
                    if (data.status === 0) {
                        $('#ruleSetBaseAlertModal .modal-footer .notView button').css('display', 'inline-block');
                        $('#ruleSetBaseAlertModal .modal-title').text('修改规则集');
                        $('#ruleSetBaseAlertModal .form-control').removeAttr('disabled');
                        if (ruleSetBaseModal.echoData(detail)) { // 数据回显
                            var ruleSetHeaderId = $('#ruleSetBaseAlertModal').attr('ruleSetHeaderId');
                            if (ruleSetHeaderId) {
                                // 校验规则集内是否有启用中的版本, 有则不能修改规则集名称
                                $.ajax({
                                    url: webpath + '/ruleSet/header/check/existEnableVersion',
                                    type: 'POST',
                                    dataType: "json",
                                    data: {"ruleSetHeaderId": ruleSetHeaderId},
                                    success: function (data) {
                                        $('#ruleSetBaseAlertModal').attr('handleType', handleType).modal({
                                            'show': 'center',
                                            "backdrop": "static"
                                        });
                                        if (data.status === -1) { // 有启用
                                            $('#ruleSetName').attr('disabled', true);
                                            $('#ruleSetBaseAlertModal .cron_msg').removeClass('hide');
                                        } else {
                                            $('#ruleSetName').removeAttr('disabled');
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType == 2) {
            $('#ruleSetBaseAlertModal .modal-footer #closeViewRuleSetBase').css('display', 'inline-block');
            $('#ruleSetBaseAlertModal .modal-title').text('查看规则集');
            ruleSetBaseModal.echoData(detail);
            $('#ruleSetBaseAlertModal').attr('handleType', handleType).modal({'show': 'center', "backdrop": "static"});
            $('#ruleSetBaseAlertModal .form-control').attr('disabled', true);
        } else {
            return;
        }
    },
    // 关闭弹框
    hidden: function () {
        $('#ruleSetBaseAlertModal').modal('toggle', 'center');
    },
    // 回显规则集基础信息
    echoData: function (detail) {
        var flag = false;
        var data = detail ? detail : {};
        for (var key in data) {
            if (key === 'ruleSetGroupId') { // 规则集组单独处理
                $('#ruleSetBaseAlertModal .ruleSetGroupSelector option[group-id="' + data[key] + '"]').prop('selected', true);
                continue;
            }
            if (key === 'ruleSetHeaderId') { // 规则集id
                $('#ruleSetBaseAlertModal').attr('ruleSetHeaderId', data[key]);
                flag = true;
                continue;
            }
            var target = $("#ruleSetBaseAlertModal .form-control[col-name='" + key + "']");
            if (target.length > 0) {
                $(target).val(data[key]);
            }
        }
        return flag;
    },
    // 保存规则集base
    saveBase: function () {
        // 表单验证
        if (!$('#ruleSetBaseAlertModal form').isValid()) {
            return;
        }
        var handleType = $('#ruleSetBaseAlertModal').attr('handleType'); // 0新增 1修改
        var urlStr = '';
        if (handleType == 0) {
            urlStr = '/ruleSet/header/create';
        } else if (handleType == 1) {
            urlStr = '/ruleSet/header/update';
        } else {
            return;
        }
        var obj = ruleSetBaseModal.getBaseData(handleType);
        if (obj) {
            $.ajax({
                url: webpath + urlStr,
                type: 'POST',
                dataType: "json",
                data: obj,
                success: function (data) {
                    if (data.status === 0) {
                        // initRuleSetTable();
                        $('.ruleSetSearch').trigger('click');
                        ruleSetBaseModal.hidden();
                        successMessager.show('保存成功');
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        }
    },
    // 获取规则集基本信息
    getBaseData: function (handleType) {
        var obj = {};
        var inputs = $('#ruleSetBaseAlertModal form .form-control');
        for (var i = 0; i < inputs.length; i++) {
            obj[$(inputs[i]).attr('col-name')] = $(inputs[i]).val();
        }
        if (handleType == 1) { //修改需要加上headerId
            obj['ruleSetHeaderId'] = $('#ruleSetBaseAlertModal').attr('ruleSetHeaderId');
        }
        obj['ruleSetGroupId'] = $('#ruleSetBaseAlertModal .ruleSetGroupSelector option:selected').attr('group-id');
        return obj;
    },
    // 删除规则集
    deleteRuleSet: function (ruleSetId) {
        if (ruleSetId) {
            // 删除权限校验
            $.ajax({
                url: webpath + '/ruleSet/delete/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {'ruleSetHeaderId': ruleSetId},
                success: function (data) {
                    if (data.status === 0) {
                        confirmAlert.show('是否确认删除？', function () {
                            $.ajax({
                                url: webpath + '/ruleSet/header/delete',
                                type: 'POST',
                                dataType: "json",
                                data: {'ruleSetHeaderId': ruleSetId},
                                success: function (data) {
                                    if (data.status === 0) {
                                        // initRuleSetTable();
                                        $('.ruleSetSearch').trigger('click');
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
 * 规则集版本列表
 */
var ruleSetVersionTableModal = {
    // 初始化规则集版本列表页面
    initPage: function (ruleSetHeaderId, ruleSetId, ruleSetName) {
        $('#versionTable_title').text(ruleSetName || '');
        $('#ruleSetHeaderName').val(ruleSetName || '');
        $('#ruleSetVersionTableAlertModal').removeAttr('ruleSetHeaderId').attr('ruleSetHeaderId', ruleSetHeaderId); // 总规则集
        $('#ruleSetVersionTableAlertModal').removeAttr('ruleSetId').attr('ruleSetId', ruleSetId); // 该版本
        $('#ruleSetVersionTableAlertModal .selfAdaptionLeft .input-group .form-control').val(''); // 清空搜索栏内容
        ruleSetVersionTableModal.initTable({"ruleSetHeaderId": ruleSetHeaderId}); // 根据总规则集id初始化列表
        ruleSetVersionTableModal.show();
    },
    // 展开弹框
    show: function () {
        $('#ruleSetVersionTableAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 关闭弹框
    hidden: function () {
        $('#ruleSetVersionTableAlertModal').modal('toggle', 'center');
    },
    // 初始化规则集版本列表 obj中必须有ruleSetHeaderId属性
    initTable: function (obj) {
        if (obj == null) obj = {};
        $('#ruleSetVersionTable').width('100%').dataTable({
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
                {"title": "规则集名称", "data": "ruleSetName"},
                {"title": "版本号", "data": "version"},
                {"title": "版本描述", "data": "ruleSetDesc"},
                {"title": "创建人", "data": "createPerson"},
                {"title": "创建时间", "data": "createDate"},
                {
                    "title": "状态", "data": "enable", "render": function (data, type, row) {
                        var checkBoxValue = '';
                        var checkHtmlStr = ''; // 控制开关开启状态
                        var htmlStr = "";
                        if ($.trim(data) == '1') {
                            checkBoxValue = '已启用';
                            checkHtmlStr += 'checked';
                        } else if ($.trim(data) == '0') {
                            checkBoxValue = '未启用';
                        } else {
                            checkBoxValue = '--';
                        }
                        htmlStr += '<div class="switch"><input onclick="ruleSetVersionTableModal.changeStatus($(this), \'' + row.ruleSetId + '\')" type="checkbox" ' + checkHtmlStr + '><label>' + checkBoxValue + '</label></div>';
                        return htmlStr;
                    }
                },
                {
                    "title": "操作", "data": null, "render": function (data, type, row) {
                        var htmlStr = "";
                        // htmlStr += '<span type="button" class="cm-tblB editVersion" onclick="ruleSetVersionModal.initPage(1, versionIndexModal.initFun, versionIndexModal.callBackFun, \'' + row.ruleSetHeaderId + '\',  \'' + row.ruleSetId + '\',  \'' + row.ruleSetName + '\')">修改</span>';
                        htmlStr += '<span type="button" class="cm-tblB editVersion" onclick="ruleSetVersionTableModal.updateVersionAuthCheck(1, versionIndexModal.initFun, versionIndexModal.callBackFun, \'' + row.ruleSetHeaderId + '\',  \'' + row.ruleSetId + '\',  \'' + row.ruleSetName + '\')">修改</span>';
                        htmlStr += '<span type="button" class="cm-tblC delBtn" onclick="ruleSetVersionTableModal.deleteVersion(\'' + row.ruleSetId + '\', \'' + row.enable + '\')">删除</span>';
                        return htmlStr;
                    }
                }],
            ajax: {
                url: webpath + '/ruleSet/paged',
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
    },
    // 版本修改权限校验
    updateVersionAuthCheck: function (handleType, initFun, callBackFun, ruleSetHeaderId, ruleSetId, ruleSetName) {
        $.ajax({
            url: webpath + '/ruleSet/update/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {"ruleSetHeaderId": ruleSetHeaderId},
            success: function (data) {
                if (data.status === 0) {
                    ruleSetVersionModal.initPage(1, versionIndexModal.initFun, versionIndexModal.callBackFun, ruleSetHeaderId, ruleSetId, ruleSetName);
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 删除规则集某一版本
    deleteVersion: function (ruleSetId, enable) {
        // 启用状态下不可删除
        if ($.trim(enable) == '1') {
            failedMessager.show('启用状态下不可删除！');
            return;
        }
        var ruleSetHeaderId = $('#ruleSetVersionTableAlertModal').attr('ruleSetHeaderId');
        // 权限校验
        $.ajax({
            url: webpath + '/ruleSet/delete/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {"ruleSetHeaderId": ruleSetHeaderId},
            success: function (data) {
                if (data.status === 0) {
                    ruleSetVersionTableModal.hidden();
                    confirmAlert.show('是否确认删除？', function () {
                        $.ajax({
                            url: webpath + '/ruleSet/delete',
                            type: 'POST',
                            dataType: "json",
                            data: {"ruleSetId": ruleSetId, "ruleSetHeaderId": ruleSetHeaderId},
                            success: function (data) {
                                if (data.status === 0) {
                                    ruleSetVersionTableModal.show();
                                    ruleSetVersionTableModal.initTable({"ruleSetHeaderId": ruleSetHeaderId});
                                    successMessager.show('删除成功');
                                } else {
                                    failedMessager.show(data.msg);
                                }
                            }
                        });
                    }, function () {
                        ruleSetVersionTableModal.show();
                    });
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 启用/停用
    changeStatus: function (that, ruleSetId) {
        var ruleSetHeaderId = $('#ruleSetVersionTableAlertModal').attr('ruleSetHeaderId');
        if (ruleSetId == null || ruleSetId == '') {
            ruleSetVersionTableModal.initTable({"ruleSetHeaderId": ruleSetHeaderId});
            return;
        }
        // 若要进行停用操作需要先check是否被引用; 引用中不可停用 (在后端进行校验)
        // var status = that.prop('checked');
        // if (!status) {
        //     $.ajax({
        //         url: webpath + '/ruleSet/check/used',
        //         type: 'POST',
        //         dataType: "json",
        //         data: {"ruleSetId": ruleSetId},
        //         success: function (data) {
        //             if (data.status === 0) {
        //                 ruleSetVersionTableModal.changeStatusSubmit(that, ruleSetId);
        //             } else {
        //                 failedMessager.show('被引用状态下不可停用！');
        //                 ruleSetVersionTableModal.initTable({"ruleSetHeaderId": ruleSetHeaderId});
        //             }
        //         }
        //     });
        // } else {
        ruleSetVersionTableModal.changeStatusSubmit(that, ruleSetId, ruleSetHeaderId);
        // }
    },
    // 启用/停用请求提交
    changeStatusSubmit: function (that, ruleSetId, ruleSetHeaderId) {
        var ruleSetHeaderId = $('#ruleSetVersionTableAlertModal').attr('ruleSetHeaderId');
        if (ruleSetId == null || ruleSetId == '') {
            ruleSetVersionTableModal.initTable({"ruleSetHeaderId": ruleSetHeaderId});
            return;
        }
        var status = that.prop('checked');
        var actionValue = status ? '启用' : '停用';
        var labelValue = status ? '已启用' : '未启用';
        var urlStr = status ? '/ruleSet/enable' : '/ruleSet/disable';
        $.ajax({
            url: webpath + urlStr,
            type: 'POST',
            dataType: "json",
            data: {"ruleSetId": ruleSetId, "ruleSetHeaderId": ruleSetHeaderId},
            success: function (data) {
                if (data.status === 0) {
                    that.siblings('label').text(labelValue);
                    successMessager.show(actionValue + '成功');
                } else {
                    failedMessager.show(actionValue + '失败，' + data.msg);
                }
                ruleSetVersionTableModal.initTable({"ruleSetHeaderId": ruleSetHeaderId});
            }
        });
    }
}

/**
 * 规则集新版本 新建/修改等操作
 */
var versionIndexModal = {
    // 初始化函数
    initFun: function () {
        $('#ruleSetVersionTableAlertModal').modal('hide');
    },
    // 回调函数
    callBackFun: function () {
        var ruleSetHeaderId = $('#ruleSetVersionTableAlertModal').attr('ruleSetHeaderId');
        ruleSetVersionTableModal.initTable({"ruleSetHeaderId": ruleSetHeaderId});
        ruleSetVersionTableModal.show();
    }
}

/**
 * 规则集组
 */
var ruleSetGroupModal = {
    show: function (handleType, $this) {
        var detail = {};
        if ($this) {
            var curRow = $this.parentNode.parentNode;
            detail = $('#ruleSetTypeTable').DataTable().row(curRow).data();
        }

        // handleType: 0新增 1修改 2查看
        $('#ruleSetGroupAlert form')[0].reset();
        $('#ruleSetGroupAlert .modal-footer button').css('display', 'none');
        if (handleType === 0) {
            $('#ruleSetGroupAlert .modal-footer .notView button').css('display', 'inline-block');
            $('#ruleSetGroupAlert .modal-title').text('').text('添加规则集组');
            $('#ruleSetGroupAlert .form-control').removeAttr('disabled');
        } else if (handleType === 1) {
            $('#ruleSetGroupAlert .modal-footer .notView button').css('display', 'inline-block');
            $('#ruleSetGroupAlert .modal-title').text('').text('修改规则集组');
            ruleSetGroupModal.echoGroupData(detail);
            $('#ruleSetGroupAlert .form-control').removeAttr('disabled');
        } else if (handleType === 2) {
            $('#ruleSetGroupAlert .modal-footer #closeViewRuleSetGroup').css('display', 'inline-block');
            $('#ruleSetGroupAlert .modal-title').text('').text('查看规则集组');
            ruleSetGroupModal.echoGroupData(detail);
            $('#ruleSetGroupAlert .form-control').attr('disabled', true);
        }
        $('#ruleSetGroupAlert').attr('handleType', handleType).modal({'show': 'center', "backdrop": "static"});
    },
    // 关闭添加参数组弹框
    hiddenAddGroupAlert: function () {
        $('#ruleSetGroupAlert').modal('toggle', 'center');
    },
    // 回显组数据
    echoGroupData: function (detail) {
        if (detail) {
            var data = detail ? detail : {};
            for (var key in data) {
                if (key === 'ruleSetGroupId') { // 回显组id
                    $('#ruleSetGroupAlert').attr('groupId', data[key]);
                    continue;
                }
                var target = $("#ruleSetGroupAlert .form-control[col-name='" + key + "']");
                if (target.length > 0) {
                    $(target).val(data[key]);
                }
            }
        }
    },
    // 保存组数据
    saveRuleSetGourp: function () {
        // 表单验证
        if (!$('#ruleSetGroupAlert form').isValid()) {
            return;
        }
        var handleType = $('#ruleSetGroupAlert').attr('handleType'); // 0新增 1修改
        var urlStr = '';
        if (handleType == 0) {
            urlStr = '/ruleSet/group/create';
        } else if (handleType == 1) {
            urlStr = '/ruleSet/group/update';
        } else {
            return;
        }
        var obj = ruleSetGroupModal.getGroupData(handleType);
        if (obj) {
            $.ajax({
                url: webpath + urlStr,
                type: 'POST',
                dataType: "json",
                data: obj,
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('保存成功');
                        // initRuleSetTable();
                        initRuleSetGroupTable();
                        $('.ruleSetSearch').trigger('click');
                        initRuleSetGroup(); // 刷新规则集组下拉框
                        ruleSetGroupModal.hiddenAddGroupAlert();
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
        var inputs = $('#ruleSetGroupAlert form .form-control');
        for (var i = 0; i < inputs.length; i++) {
            obj[$(inputs[i]).attr('col-name')] = $.trim($(inputs[i]).val());
        }
        if (handleType == 1) { //修改需要加上组id
            obj['ruleSetGroupId'] = $('#ruleSetGroupAlert').attr('groupId');
        }
        return obj;
    },
    // 删除规则集组
    deleteGroup: function (groupId) {
        if (groupId) {
            confirmAlert.show('是否确认删除？', function () {
                $.ajax({
                    url: webpath + '/ruleSet/group/delete',
                    type: 'POST',
                    dataType: "json",
                    data: {'ruleSetGroupId': groupId},
                    success: function (data) {
                        if (data.status === 0) {
                            successMessager.show('删除成功');
                            // initRuleSetTable();
                            initRuleSetGroupTable();
                            $('.ruleSetSearch').trigger('click');
                            initRuleSetGroup(); // 刷新规则集组下拉框
                        } else {
                            failedMessager.show(data.msg);
                        }
                    }
                });
            });
        }
    }
}

// 初始化规则集组下拉框
function initRuleSetGroup() {
    $.ajax({
        url: webpath + '/ruleSet/group/list',
        type: 'POST',
        dataType: "json",
        data: {},
        success: function (data) {
            if (data.data.length > 0) {
                var data = data.data;
                var htmlStr_list = '';
                var htmlStr_selector = '';
                for (var i = 0; i < data.length; i++) {
                    htmlStr_list += '<li group-id=\'' + data[i].ruleSetGroupId + '\'><a>' + data[i].ruleSetGroupName + '</a></li>';
                    htmlStr_selector += '<option group-id=\'' + data[i].ruleSetGroupId + '\'>' + data[i].ruleSetGroupName + '</option>';
                }
                $('#ruleSetPageContent .ruleSetGroupList').empty().html(htmlStr_list); // 下拉框
                $('.ruleSetGroupSelector').empty().html(htmlStr_selector); // 表单
            }
        },
        complete: function () {
            // 绑定事件
            $('#ruleSetPageContent .ruleSetGroupList>li').unbind('click').on('click', function () {
                $(this).parent().siblings('.form-control').val($(this).first().text());
            });
        }
    });
}

/**
 * 初始化 规则集表格
 */
function initRuleSetTable(obj) {
    $('#ruleSetTable').width('100%').dataTable({
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
            {"title": "规则集组", "data": "ruleSetGroupName", "width": "10%"},
            {"title": "规则集名称", "data": "ruleSetName", "width": "20%"},
            {"title": "规则集描述", "data": "ruleSetHeaderDesc", "width": "20%"},
            {"title": "创建人", "data": "createPerson", "width": "10%"},
            {"title": "创建时间", "data": "createDate", "width": "15%"},
            {
                "title": "操作", "data": null, "width": "25%", "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<span type="button" class="cm-tblB" onclick="ruleSetBaseModal.showBase(2, this)">查看</span>';
                    htmlStr += '<span type="button" class="cm-tblB" onclick="ruleSetBaseModal.showBase(1, this)">修改</span>';
                    htmlStr += '<span type="button" class="cm-tblB detailBtn" onclick="ruleSetVersionTableModal.initPage(\'' + row.ruleSetHeaderId + '\', \'' + row.ruleSetId + '\', \'' + row.ruleSetName + '\')">版本查看</span>';
                    htmlStr += '<span type="button" class="cm-tblC delBtn" onclick="ruleSetBaseModal.deleteRuleSet(\'' + row.ruleSetHeaderId + '\')">删除</span>';
                    return htmlStr;
                }
            }],
        ajax: {
            url: webpath + '/ruleSet/header/paged',
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
function initRuleSetGroupTable(obj) {
    $('#ruleSetTypeTable').width('100%').dataTable({
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
            {"title": "规则集组名称", "data": "ruleSetGroupName", "width": "25%"},
            {"title": "创建时间", "data": "createDate", "width": "25%"},
            {"title": "创建人", "data": "createPerson", "width": "25%"},
            {
                "title": "操作", "data": null, "width": "25%", "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<span type="button" class="cm-tblB" onclick="ruleSetGroupModal.show(2, this)">查看</span>';
                    htmlStr += '<span type="button" class="cm-tblB" onclick="ruleSetGroupModal.show(1, this)">修改</span>';
                    htmlStr += '<span type="button" class="cm-tblC delBtn" onclick="ruleSetGroupModal.deleteGroup(\'' + row.ruleSetGroupId + '\')">删除</span>';
                    return htmlStr;
                }
            }],
        ajax: {
            url: webpath + '/ruleSet/group/paged',
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

$(function () {
    initPage();
    initRuleSetTable(); // 初始化规则集table
    initRuleSetGroup(); // 初始化规则组下拉框
    initRuleSetGroupTable(); // 初始化组table
});