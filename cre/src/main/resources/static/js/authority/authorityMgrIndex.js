/**
 * 系统菜单
 * data:2019/07/29
 * author:bambi
 */

/* 跳转tab页面 */
function changeTab(tabId) {
    var flagName = $('#folderMenuWrap').attr('class');
    var flagStr = '&childOpen=' + flagName;
    var tabUrl = '';
    if (tabId == '0') {
        tabUrl = '/user/view?idx=9&tabId=0' + flagStr;
    } else if (tabId == '1') {
        tabUrl = '/role/view?idx=10&tabId=1' + flagStr;
    } else if (tabId == '2') {
        tabUrl = '/dept/view?idx=11&tabId=2' + flagStr;
    } else if (tabId == '3') {
        tabUrl = '/auth/view?idx=6&tabId=3' + flagStr;
    } else if (tabId == '4') {
        tabUrl = '/channel/view?idx=8&tabId=4' + flagStr;
    } else {
        failedMessager.show('跳转失败！');
        return;
    }
    var url = webpath + tabUrl;
    creCommon.loadHtml(url);
}

/* 校验手机号 */
function isPoneAvailable(poneInput) {
    var reg = /^[1][3,4,5,7,8][0-9]{9}$/;
    if (!reg.test(poneInput)) {
        return false;
    } else {
        return true;
    }
}

/* 初始化页面角色下拉框 */
function initRoleSelector() {
    $.ajax({
        url: webpath + '/role/list',
        type: 'POST',
        dataType: "json",
        data: {},
        success: function (data) {
            if (data.data.length > 0) {
                var data = data.data;
                var htmlStr = '';
                for (var i = 0; i < data.length; i++) {
                    htmlStr += '<option role-id=\'' + data[i].roleId + '\'>' + data[i].roleName + '</option>';
                }
                $('#addUserAlertModal .roleSelector').empty().html(htmlStr);
            }
        },
        error: function (data) {
            failedMessager.show(data.msg);
        }
    });
}

/* 初始化页面机构下拉框 */
function initOrgSelector() {
    $.ajax({
        url: webpath + '/dept/list',
        type: 'POST',
        dataType: "json",
        data: {},
        success: function (data) {
            if (data.data.length > 0) {
                var data = data.data;
                var htmlStr = '';
                for (var i = 0; i < data.length; i++) {
                    htmlStr += '<option org-id=\'' + data[i].deptId + '\'>' + data[i].deptName + '</option>';
                }
                $('#addUserAlertModal .orgSelector').empty().html(htmlStr);
            }
        },
        error: function (data) {
            failedMessager.show(data.msg);
        }
    });
}

/* 初始化页面用户组下拉框 */
function initUserGroupSelector() {
    $.ajax({
        url: webpath + '/group/list',
        type: 'POST',
        dataType: "json",
        data: {},
        success: function (data) {
            if (data.data.length > 0) {
                var data = data.data;
                var htmlStr = '';
                for (var i = 0; i < data.length; i++) {
                    htmlStr += '<option data-id=\'' + data[i].groupId + '\'>' + data[i].groupName + '</option>';
                }
                $('#addUserAlertModal .userGroupSelector').empty().html(htmlStr);
            }
        },
        error: function (data) {
            failedMessager.show(data.msg);
        }
    });
}

/* 初始化页面渠道下拉框 */
function initChanSelector() {
    $.ajax({
        url: webpath + '/channel/list',
        type: 'POST',
        dataType: "json",
        data: {},
        success: function (data) {
            if (data.data.length > 0) {
                var data = data.data;
                var htmlStr = '';
                for (var i = 0; i < data.length; i++) {
                    htmlStr += '<option channelId=\'' + data[i].channelId + '\'>' + data[i].channelName + '</option>';
                }
                $('#addUserAlertModal .chanSelector').empty().html(htmlStr);
            }
        },
        error: function (data) {
            failedMessager.show(data.msg);
        }
    });
}

/**
 * 用户管理
 */
var userModal = {
    // 展示添加用户弹框
    show: function (handleType, userId) {
        $('#addUserAlertModal form')[0].reset(); // 清空表单
        $('#addUserAlertModal .modal-footer button').css('display', 'none');
        $('#addUserAlertModal .cron_msg').addClass('hide');
        $('#userIdInput').removeAttr('disabled');
        // handleType: 0新增 1修改 2查看
        if (handleType == 0) {
            $('#addUserAlertModal .modal-footer .notView button').css('display', 'inline-block');
            // 权限校验 authCheck
            $.ajax({
                url: webpath + '/user/save/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {},
                success: function (data) {
                    if (data.status === 0) {
                        $('#addUserAlertModal .modal-title').text('添加用户');
                        $('#addUserAlertModal').attr('handleType', handleType).modal({
                            'show': 'center',
                            "backdrop": "static"
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType == 1) {
            $('#addUserAlertModal .modal-footer .notView button').css('display', 'inline-block');
            $('#addUserAlertModal .cron_msg').removeClass('hide');
            $('#userIdInput').attr('disabled', true);
            // 权限校验 authCheck
            $.ajax({
                url: webpath + '/user/update/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {},
                success: function (data) {
                    if (data.status === 0) {
                        $('#addUserAlertModal .modal-title').text('修改用户');
                        userModal.echoData(userId); // 数据回显
                        $('#addUserAlertModal').attr('handleType', handleType).modal({
                            'show': 'center',
                            "backdrop": "static"
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType == 2) {
            $('#addUserAlertModal .modal-footer #closeViewUser').css('display', 'inline-block');
            $('#addUserAlertModal .modal-title').text('查看用户');
            $('#addUserAlertModal .cron_msg').addClass('hide');
            userModal.echoData(userId); // 数据回显
            $('#addUserAlertModal').attr('handleType', handleType).modal({'show': 'center', "backdrop": "static"});
        }
    },
    // 关闭弹框
    closeModal: function () {
        $('#addUserAlertModal form')[0].reset(); // 清空表单
        $('#addUserAlertModal').modal('toggle', 'center');
    },
    // 保存用户信息
    saveUser: function () {
        // 表单校验
        if (!$('#addUserAlertModal form').isValid()) {
            return;
        }
        // 校验手机号
        if (($.trim($('#phoneInput').val()) != '') && (!isPoneAvailable($.trim($('#phoneInput').val())))) {
            creCommon.showErrorValidator('addUserForm', 'phoneInput', '请填写有效的手机号');
            return;
        }
        // 校验密码是否一致
        if ($('#passwordInput').val() != $('#passwordAgainInput').val()) {
            creCommon.showErrorValidator('addUserForm', 'passwordAgainInput', '两次密码不一致');
            return;
        }

        // 验证通过清除所有验证信息
        $('#addUserAlertModal form').validator('cleanUp');
        var urlStr = '';
        var obj = {};
        var handleType = $('#addUserAlertModal').attr('handleType'); // 0新增 1修改 2查看
        if (handleType == '0') {
            urlStr = '/user/save';
            obj = userModal.getAddData(); // 获取表单数据对象
        } else if (handleType == '1') {
            urlStr = '/user/update';
            obj = userModal.getUpdateData();
        } else {
            return;
        }
        if (obj) {
            $.ajax({
                url: webpath + urlStr,
                type: 'POST',
                dataType: "json",
                contentType: 'application/json',
                data: JSON.stringify(obj),
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('保存成功');
                        // initTable('0');
                        searchData('0');
                        userModal.closeModal();
                    } else {
                        failedMessager.show('保存失败: ' + data.msg);
                    }
                },
                error: function (data) {
                    failedMessager.show(data.msg);
                }
            });
        }
    },
    // 获取新增用户数据
    getAddData: function () {
        var obj = {};
        var inputs = $('#addUserAlertModal .form-control');
        for (var i = 0; i < inputs.length; i++) {
            var colName = $(inputs[i]).attr('col-name');
            if (colName && colName != '') {
                obj[colName] = $.trim($(inputs[i]).val());
            }
        }
        // userGroupIds
        var groupArr = $('#addUserAlertModal .userGroupSelector option:selected');
        var groupList = '';
        for (var i = 0; i < groupArr.length; i++) {
            if (i == (groupArr.length - 1)) {
                groupList += $(groupArr[i]).attr('data-id');
            } else {
                groupList += $(groupArr[i]).attr('data-id') + ',';
            }
        }
        obj['groupId'] = groupList; // 用户组

        // // roleIds
        // var rolesArr = $('#addUserAlertModal .roleSelector option:selected');
        // var roleList = '';
        // for (var i = 0; i < rolesArr.length; i++) {
        //     if (i == (rolesArr.length - 1)) {
        //         roleList += $(rolesArr[i]).attr('role-id');
        //     } else {
        //         roleList += $(rolesArr[i]).attr('role-id') + ',';
        //     }
        // }
        // obj['roleId'] = roleList; // 角色
        // obj['deptId'] = $('#addUserAlertModal .orgSelector option:selected').attr('org-id'); // 组织
         obj['channelId'] = $('#addUserAlertModal .chanSelector option:selected').attr('channelId'); // 渠道

        return obj;
    },
    // 获取修改用户数据
    getUpdateData: function () {
        var obj = {};
        var inputs = $('#addUserAlertModal .form-control');
        for (var i = 0; i < inputs.length; i++) {
            var colName = $(inputs[i]).attr('col-name');
            if (colName && colName != '') {
                obj[colName] = $.trim($(inputs[i]).val());
            }
        }
        delete obj.groupId;
        // delete obj.roleId;
        // delete obj.deptId;
        // delete obj.channelId;

        var groupArr = $('#addUserAlertModal .userGroupSelector option:selected'); // groupList
        var groupList = new Array();
        for (var i = 0; i < groupArr.length; i++) {
            groupList.push({"groupId": $(groupArr[i]).attr('data-id')});
        }
        obj['groupList'] = groupList;

        // // roleList
        // var rolesArr = $('#addUserAlertModal .roleSelector option:selected');
        // var roleList = new Array();
        // for (var i = 0; i < rolesArr.length; i++) {
        //     roleList.push({'roleId': $(rolesArr[i]).attr('role-id')});
        // }
        // obj['roleList'] = roleList;
        //
        // // org
        // var deptList = new Array();
        // deptList.push({'deptId': $('#addUserAlertModal .orgSelector option:selected').attr('org-id')});
        // obj['deptList'] = deptList;

        return obj;
    },
    // 回显用户
    echoData: function (userId) {
        if (userId) {
            var data = JSON.parse($("#user_" + userId).text()); // 用户全部数据
            for (var key in data) {
                if (key === 'groupList') { // 用户组回显
                    var dataArr = data[key];
                    for (var i = 0; i < dataArr.length; i++) {
                        $("#addUserAlertModal .userGroupSelector option[data-id='" + dataArr[i].groupId + "']").prop('selected', true);
                    }
                    continue;
                }
                if (key === 'userSex') { // 性别
                    $("#addUserAlertModal .sexSelector option[value='" + data[key] + "']").prop('selected', true);
                    continue;
                }
                var target = $("#addUserAlertModal .form-control[col-name='" + key + "']");
                if (target.length > 0) {
                    target.val(data[key]);
                }
            }
        }
    },
    // 删除用户
    deleteUser: function (userId) {
        if (userId) {
            confirmAlert.show('是否确认删除？', function () {
                $.ajax({
                    url: webpath + '/user/delete',
                    type: 'POST',
                    dataType: "json",
                    data: {'userId': userId},
                    success: function (data) {
                        if (data.status == 0) {
                            // initTable('0');
                            searchData('0');
                            successMessager.show('删除成功');
                        } else {
                            failedMessager.show(data.msg);
                        }
                    },
                    error: function (data) {
                        failedMessager.show(data.msg);
                    }
                });
            });
        }
    },
    // 角色选择用户保存后回调函数
    distributeCallBack: function () {
        // initTable('0');
        searchData('0');
    },
    // 组织选择保存后回调函数
    chooseOrgCallBack: function () {
        // initTable('0');
        searchData('0');
    },
    // 渠道选择用户后保存后回调函数
    chooseChanCallBack: function () {
        // initTable('0');
        searchData('0');
    }
}

/**
 * 用户组管理
 */
var userGroupModal = {
    // 展示添加组弹框
    showAddGroupAlert: function (handleType, $this) {
        var detail = {};
        if ($this) {
            var curRow = $this.parentNode.parentNode;
            detail = $('#userGroupTable').DataTable().row(curRow).data();
        }
        // handleType: 0新增 1修改 2查看
        $('#userGroupAlertModal form')[0].reset();
        $('#userGroupAlertModal .modal-footer button').css('display', 'none');
        if (handleType === 0) {
            $('#userGroupAlertModal .modal-footer .notView button').css('display', 'inline-block');
            // 权限校验 authCheck
            $.ajax({
                url: webpath + '/group/save/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {},
                success: function (data) {
                    if (data.status === 0) {
                        $('#userGroupAlertModal .modal-title').text('').text('添加用户组');
                        $('#userGroupAlertModal').attr('handleType', handleType).modal({
                            'show': 'center',
                            "backdrop": "static"
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType === 1) {
            $('#userGroupAlertModal .modal-footer .notView button').css('display', 'inline-block');
            // 权限校验 authCheck
            $.ajax({
                url: webpath + '/group/update/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {},
                success: function (data) {
                    if (data.status === 0) {
                        $('#userGroupAlertModal .modal-title').text('').text('修改用户组');
                        userGroupModal.echoGroupData(detail);
                        $('#userGroupAlertModal').attr('handleType', handleType).modal({
                            'show': 'center',
                            "backdrop": "static"
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType == 2) {
            $('#userGroupAlertModal .modal-footer #closeViewUserGroup').css('display', 'inline-block');
            // 权限校验 authCheck
            $.ajax({
                url: webpath + '/group/update/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {},
                success: function (data) {
                    if (data.status === 0) {
                        $('#userGroupAlertModal .modal-title').text('').text('查看用户组');
                        userGroupModal.echoGroupData(detail);
                        $('#userGroupAlertModal').attr('handleType', handleType).modal({
                            'show': 'center',
                            "backdrop": "static"
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        }
    },
    // 关闭添加组弹框
    hiddenAddGroupAlert: function () {
        $('#userGroupAlertModal').modal('toggle', 'center');
    },
    // 保存组
    saveGroup: function () {
        if (!$('#userGroupAlertModal form').isValid()) {
            return;
        }
        var handleType = $('#userGroupAlertModal').attr('handleType'); // 0新增 1修改
        var urlStr = '';
        if (handleType == '0') {
            urlStr = '/group/save';
        } else if (handleType == '1') {
            urlStr = '/group/update';
        } else {
            return;
        }
        var obj = userGroupModal.getGroupData(handleType);
        if (obj) {
            $.ajax({
                url: webpath + urlStr,
                type: 'POST',
                dataType: "json",
                contentType: 'application/json',
                data: JSON.stringify(obj),
                success: function (data) {
                    if (data.status == 0) {
                        successMessager.show('保存成功');
                        initTabContent('0');
                        userGroupModal.hiddenAddGroupAlert();
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
    // 组数据回显
    echoGroupData: function (detail) {
        if (detail) {
            var data = detail ? detail : {};
            for (var key in data) {
                if (key === 'groupId') { // 回显参数组id
                    $('#userGroupAlertModal').attr('groupId', data[key]);
                    continue;
                }
                var target = $("#userGroupAlertModal .form-control[col-name='" + key + "']");
                if (target.length > 0) {
                    $(target).val(data[key]);
                }
            }
        }
    },
    // 获取组表单数据
    getGroupData: function (handleType) {
        var obj = {};
        var inputs = $('#userGroupAlertModal form .form-control');
        for (var i = 0; i < inputs.length; i++) {
            obj[$(inputs[i]).attr('col-name')] = $(inputs[i]).val();
        }
        if (handleType == 1) { //修改需要加上参数组id
            obj['groupId'] = $('#userGroupAlertModal').attr('groupId');
        }
        return obj;
    },
    // 删除组
    deleteGroup: function (groupId) {
        if (groupId) {
            confirmAlert.show('是否确认删除？', function () {
                $.ajax({
                    url: webpath + '/group/delete',
                    type: 'POST',
                    dataType: "json",
                    data: {'groupId': groupId},
                    success: function (data) {
                        if (data.status === 0) {
                            successMessager.show('删除成功');
                            initTabContent('0');
                        } else {
                            failedMessager.show(data.msg);
                        }
                    },
                    error: function (data) {
                        failedMessager.show(data.msg);
                    }
                });
            });
        }
    }
}

function initUserPage() {
    // 保存用户
    $('#saveUser').click(function () {
        userModal.saveUser();
    });

    // 重置表单
    $('#resetAddUser').click(function () {
        var userId = $.trim($('#userIdInput').val());
        $('#addUserAlertModal form')[0].reset(); // 清空表单
        if ($('#addUserAlertModal').attr('handleType') === '1') {
            $('#userIdInput').val(userId); // 修改不重置账号
        }
    });

    // 新增用户组
    $('#addGroupButton').click(function () {
        userGroupModal.showAddGroupAlert(0);
    });

    // 保存用户组
    $('#saveParamGroup').click(function () {
        userGroupModal.saveGroup();
    });
}

/**
 *  角色管理
 */
var roleModal = {
    // 展示弹框
    show: function (handleType, $this) {
        var detail = {};
        if ($this) {
            var curRow = $this.parentNode.parentNode;
            detail = $('#authorityPageTable').DataTable().row(curRow).data();
        }
        $('#roleAlertModal form')[0].reset(); // 清空表单
        $('#roleAlertModal .modal-footer button').css('display', 'none');
        $('#roleAlertModal').removeAttr('roleId'); // 清除roleId标识
        // handleType: 0新增 1修改 2查看
        if (handleType === 0) {
            $('#roleAlertModal .modal-footer .notView button').css('display', 'inline-block');
            // 权限校验 authCheck
            $.ajax({
                url: webpath + '/role/save/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {},
                success: function (data) {
                    if (data.status === 0) {
                        $('#roleAlertModal .modal-title').text('添加角色');
                        $('#roleAlertModal').attr('handleType', handleType).modal({
                            'show': 'center',
                            "backdrop": "static"
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType === 1) {
            $('#roleAlertModal .modal-footer .notView button').css('display', 'inline-block');
            // 权限校验 authCheck
            $.ajax({
                url: webpath + '/role/update/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {},
                success: function (data) {
                    if (data.status === 0) {
                        $('#roleAlertModal .modal-title').text('修改角色');
                        roleModal.echoData(detail); // 数据回显
                        $('#roleAlertModal').attr('handleType', handleType).modal({
                            'show': 'center',
                            "backdrop": "static"
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType == 2) {
            $('#roleAlertModal .modal-footer #closeViewRole').css('display', 'inline-block');
            $('#roleAlertModal .modal-title').text('查看角色');
            roleModal.echoData(detail); // 数据回显
            $('#roleAlertModal').attr('handleType', handleType).modal({'show': 'center', "backdrop": "static"});
        }
    },
    // 关闭弹框
    hidden: function () {
        $('#roleAlertModal form')[0].reset(); // 清空表单
        $('#roleAlertModal').modal('toggle', 'center');
    },
    // 保存角色
    saveRole: function () {
        if (!$('#roleAlertModal form').isValid()) {
            return;
        }
        var handleType = $('#roleAlertModal').attr('handleType'); // 0新增 1修改
        var urlStr = '';
        if (handleType == '0') {
            urlStr = '/role/save';
        } else if (handleType == '1') {
            urlStr = '/role/update';
        } else {
            return;
        }
        var roleId = $('#roleAlertModal').attr('roleId');
        var obj = roleModal.getAlertData(handleType, roleId); // 获取表单数据对象
        if (obj) {
            $.ajax({
                url: webpath + urlStr,
                type: 'POST',
                dataType: "json",
                contentType: 'application/json',
                data: JSON.stringify(obj),
                success: function (data) {
                    if (data.status == 0) {
                        successMessager.show('保存成功');
                        // initTable('1');
                        searchData('0');
                        roleModal.hidden();
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
    // 获取表单数据
    getAlertData: function (handleType, roleId) {
        var obj = {};
        var inputs = $('#roleAlertModal .form-control');
        for (var i = 0; i < inputs.length; i++) {
            obj[$(inputs[i]).attr('col-name')] = $.trim($(inputs[i]).val());
        }
        if (handleType === '1') { // 修改参数多传varId
            if (roleId) {
                obj['roleId'] = roleId;
            } else {
                return {};
            }
        }
        return obj;
    },
    // 回显角色数据
    echoData: function (detail) {
        var data = detail ? detail : {};
        for (var key in data) {
            if (key === 'roleId') { // 角色roleId回显
                $('#roleAlertModal').attr('roleId', data[key]);
                continue;
            }
            var target = $("#roleAlertModal .form-control[col-name='" + key + "']");
            if (target.length > 0) {
                $(target).val(data[key]);
            }
        }
    },
    // 删除角色
    deleteRole: function (roleId) {
        if (roleId) {
            confirmAlert.show('是否确认删除？', function () {
                $.ajax({
                    url: webpath + '/role/delete',
                    type: 'POST',
                    dataType: "json",
                    data: {'roleId': roleId},
                    success: function (data) {
                        if (data.status == 0) {
                            successMessager.show('删除成功');
                            // initTable('1');
                            searchData('0');
                        } else {
                            failedMessager.show(data.msg);
                        }
                    },
                    error: function (data) {
                        failedMessager.show(data.msg);
                    }
                });
            });
        }
    },
    // 角色选择用户保存后回调函数
    distributeCallBack: function () {
        // initTable('1');
        searchData('0');
    }
}

function initRolePage() {
    // 保存角色
    $('#saveRole').click(function () {
        roleModal.saveRole();
    });

    // 重置表单
    $('#resetRole').click(function () {
        $('#roleAlertModal form')[0].reset(); // 清空表单
    });
}

/**
 * 机构组织管理
 */
var orgModal = {
    // 展示弹框
    show: function (handleType, $this) {
        var detail = {};
        if ($this) {
            var curRow = $this.parentNode.parentNode;
            detail = $('#authorityPageTable').DataTable().row(curRow).data();
        }
        $('#orgAlertModal form')[0].reset();
        $('#orgAlertModal .modal-footer button').css('display', 'none');
        $('#orgAlertModal').removeAttr('deptId'); // 清除id标识
        // handleType: 0新增 1修改 2查看
        if (handleType == 0) {
            $('#orgAlertModal .notView button').css('display', 'inline-block');
            // 权限校验 authCheck
            $.ajax({
                url: webpath + '/dept/save/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {},
                success: function (data) {
                    if (data.status === 0) {
                        $('#orgAlertModal .modal-title').text('添加机构');
                        $('#orgAlertModal').attr('handleType', handleType).modal({
                            'show': 'center',
                            "backdrop": "static"
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType == 1) {
            $('#orgAlertModal .notView button').css('display', 'inline-block');
            // 权限校验 authCheck
            $.ajax({
                url: webpath + '/dept/update/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {},
                success: function (data) {
                    if (data.status === 0) {
                        $('#orgAlertModal .modal-title').text('修改机构');
                        if (detail) {
                            orgModal.echoData(detail); // 数据回显
                        } else {
                            failedMessager.show('无法查看！');
                        }
                        $('#orgAlertModal').attr('handleType', handleType).modal({
                            'show': 'center',
                            "backdrop": "static"
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType == 2) {
            $('#orgAlertModal #closeViewOrg').css('display', 'inline-block');
            $('#orgAlertModal .modal-title').text('查看机构');
            orgModal.echoData(detail); // 数据回显
            $('#orgAlertModal').attr('handleType', handleType).modal({'show': 'center', "backdrop": "static"});
        }
    },
    // 初始化机构下拉框
    initOrgList: function () {
        $.ajax({
            url: webpath + '/dept/nameList',
            type: 'GET',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.length > 0) {
                    var htmlStr = '';
                    for (var i = 0; i < data.length; i++) {
                        htmlStr += '<option org-id=\'' + data[i].DEPT_ID + '\'>' + data[i].DEPT_NAME + '</option>';
                    }
                    $('#orgAlertOrgSelector').empty().html('<option class="noParentOrgOption" org-id=" ">请选择</option>' + htmlStr);
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            }
        });
    },
    // 关闭弹框
    hidden: function () {
        $('#orgAlertModal form')[0].reset(); // 清空表单
        $('#orgAlertModal').modal('toggle', 'center');
    },
    // 保存
    saveOrg: function () {
        if (!$('#orgAlertModal form').isValid()) {
            return;
        }
        var handleType = $('#orgAlertModal').attr('handleType'); // 0新增 1修改
        var urlStr = '';
        if (handleType == '0') {
            urlStr = '/dept/save';
        } else if (handleType == '1') {
            urlStr = '/dept/update';
        } else {
            return;
        }
        var deptId = $('#orgAlertModal').attr('deptId');
        var obj = orgModal.getAlertData(handleType, deptId); // 获取表单数据对象
        if (obj) {
            $.ajax({
                url: webpath + urlStr,
                type: 'POST',
                dataType: "json",
                contentType: 'application/json',
                data: JSON.stringify(obj),
                success: function (data) {
                    if (data.status == 0) {
                        successMessager.show('保存成功');
                        // initTable('2');
                        searchData('0');
                        orgModal.initOrgList(); // 需要刷新弹出框的组织下拉框
                        orgModal.hidden();
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
    // 获取表单数据
    getAlertData: function (handleType, deptId) {
        var obj = {};
        var inputs = $('#orgAlertModal .form-control');
        for (var i = 0; i < inputs.length; i++) {
            obj[$(inputs[i]).attr('col-name')] = $.trim($(inputs[i]).val());
        }
        // 组织机构 单独处理一下
        obj['parentId'] = $.trim($('#orgAlertModal .orgSelector option:selected').attr('org-id'));
        if (handleType === '1') { // 修改参数多传varId
            if (deptId) {
                obj['deptId'] = deptId;
            } else {
                return {};
            }
        }
        return obj;
    },
    // 回显
    echoData: function (detail) {
        var data = detail ? detail : {};
        for (var key in data) {
            if (key === 'deptId') { // 机构id回显
                $('#orgAlertModal').attr('deptId', data[key]);
                continue;
            }
            if (key === 'parentId') { // 上级组织机构特殊处理一下
                $('#orgAlertModal .orgSelector option[org-id="' + data[key] + '"]').prop('selected', true);
                continue;
            }
            if (key === 'parentId') { // 无父组织机构的回显时回显'请选择'字样
                if ($.trim(data[key]) == '') {
                    $('#orgAlertOrgSelector .noParentOrgOption').prop('selected', true);
                    continue;
                }
            }
            var target = $("#orgAlertModal .form-control[col-name='" + key + "']");
            if (target.length > 0) {
                $(target).val(data[key]);
            }
        }
    },
    // 删除
    deleteOrg: function (deptId) {
        if (deptId) {
            confirmAlert.show('是否确认删除？', function () {
                $.ajax({
                    url: webpath + '/dept/delete',
                    type: 'POST',
                    dataType: "json",
                    data: {'deptId': deptId},
                    success: function (data) {
                        if (data.status == 0) {
                            // initTable('2');
                            searchData('0');
                            successMessager.show('删除成功');
                        } else {
                            failedMessager.show(data.msg);
                        }
                    },
                    error: function (data) {
                        failedMessager.show(data.msg);
                    }
                });
            });
        }
    },
}

function initOrgPage() {
    // 保存
    $('#saveOrg').click(function () {
        orgModal.saveOrg();
    });
}

/**
 * 渠道管理
 */
var chanModal = {
    // 展示弹框
    show: function (handleType, $this) {
        var detail = {};
        if ($this) {
            var curRow = $this.parentNode.parentNode;
            detail = $('#authorityPageTable').DataTable().row(curRow).data();
        }
        $('#chanAlertModal form')[0].reset();
        $('#chanAlertModal .modal-footer button').css('display', 'none');
        $('#chanAlertModal').removeAttr('channelId'); // 清除id标识
        // handleType: 0新增 1修改 2查看
        if (handleType == 0) {
            $('#chanAlertModal .notView button').css('display', 'inline-block');
            // 权限校验 authCheck
            // $('#chanAlertModal .modal-title').text('添加渠道');
            // $('#chanAlertModal').attr('handleType', handleType).modal({
            //     'show': 'center',
            //     "backdrop": "static"
            // });
            $.ajax({
                    url: webpath + '/channel/save/checkAuth',
                    type: 'GET',
                    dataType: "json",
                    data: {},
                    success: function (data) {
                        if (data.status === 0) {
                            $('#chanAlertModal .modal-title').text('添加渠道');
                            $('#chanAlertModal').attr('handleType', handleType).modal({
                                'show': 'center',
                                "backdrop": "static"
                            });
                        } else {
                            failedMessager.show(data.msg);
                        }
                    }
                });
        } else if (handleType == 1) {
            $('#chanAlertModal .notView button').css('display', 'inline-block');
            // 权限校验 authCheck
            $.ajax({
                url: webpath + '/channel/update/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {},
                success: function (data) {
                    if (data.status === 0) {
                        $('#chanAlertModal .modal-title').text('修改渠道');
                        if (detail) {
                            chanModal.echoData(detail); // 数据回显
                        } else {
                            failedMessager.show('无法查看！');
                        }
                        $('#chanAlertModal').attr('handleType', handleType).modal({
                            'show': 'center',
                            "backdrop": "static"
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType == 2) {
            $('#chanAlertModal #closeViewChan').css('display', 'inline-block');
            $('#chanAlertModal .modal-title').text('查看渠道');
            chanModal.echoData(detail); // 数据回显
            $('#chanAlertModal').attr('handleType', handleType).modal({'show': 'center', "backdrop": "static"});
        }
    },
    // 初始化渠道下拉框
    initChanList: function () {
        $.ajax({
            url: webpath + '/dept/nameList',
            type: 'GET',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.length > 0) {
                    var htmlStr = '';
                    for (var i = 0; i < data.length; i++) {
                        htmlStr += '<option channelId=\'' + data[i].DEPT_ID + '\'>' + data[i].DEPT_NAME + '</option>';
                    }
                    $('#chanAlertChanSelector').empty().html('<option class="noParentChanOption" channelId=" ">请选择</option>' + htmlStr);
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            }
        });
    },
    // 关闭弹框

    hidden: function () {
        $('#chanAlertModal form')[0].reset(); // 清空表单
        $('#chanAlertModal').modal('toggle', 'center');
    },
    // 保存
    saveChan: function () {
        if (!$('#chanAlertModal form').isValid()) {
            return;
        }
        var handleType = $('#chanAlertModal').attr('handleType'); // 0新增 1修改
        var urlStr = '';
        if (handleType == '0') {
            urlStr = '/channel/save';
        } else if (handleType == '1') {
            urlStr = '/channel/update';
        } else {
            return;
        }
        var channelId = $('#chanAlertModal').attr('channelId');
        var obj = chanModal.getAlertData(handleType, channelId); // 获取表单数据对象

        if (obj) {
            $.ajax({
                url: webpath + urlStr,
                type: 'POST',
                dataType: "json",
                contentType: 'application/json',
                data: JSON.stringify(obj),
                success: function (data) {
                    if (data.status == 0) {
                        successMessager.show('保存成功');
                        // initTable('2');
                        searchData('0');
                        chanModal.initChanList(); // 需要刷新弹出框的渠道下拉框
                        chanModal.hidden();
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
    // 获取表单数据
    getAlertData: function (handleType, channelId) {
        var obj = {};
        obj['channelId'] = channelId;
        var inputs = $('#chanAlertModal .form-control');
        for (var i = 0; i < inputs.length; i++) {
            obj[$(inputs[i]).attr('col-name')] = $.trim($(inputs[i]).val());
        }
        //  单独处理一下
        // obj['parentId'] = $.trim($('#chanAlertModal .chanSelector option:selected').attr('channelId'));
        // if (handleType === '1') { // 修改参数多传varId
        //     if (channelId) {
        //         obj['channelId'] = channelId;
        //     } else {
        //         return {};
        //     }
        // }
        obj['deptId'] = $.trim($('#chanAlertModal .chanSelector option:selected').attr('channelId'));
        return obj;
    },
    // 回显
    echoData: function (detail) {
        var data = detail ? detail : {};
        for (var key in data) {
            if (key === 'channelId') { // 渠道id回显
                $('#chanAlertModal').attr('channelId', data[key]);
                continue;
            }
            if (key === 'parentId') { // 上级渠道特殊处理一下
                $('#chanAlertModal .chanSelector option[channelId="' + data[key] + '"]').prop('selected', true);
                continue;
            }
            if (key === 'parentId') { // 无父渠道的回显时回显'请选择'字样
                if ($.trim(data[key]) == '') {
                    $('#chanAlertChanSelector .noParentChanOption').prop('selected', true);
                    continue;
                }
            }
            var target = $("#chanAlertModal .form-control[col-name='" + key + "']");
            if (target.length > 0) {
                $(target).val(data[key]);
            }
        }
    },
    // 删除
    deleteChan: function (channelId) {
        if (channelId) {
            confirmAlert.show('是否确认删除？', function () {
                $.ajax({
                    url: webpath + '/channel/delete',
                    type: 'POST',
                    dataType: "json",
                    data: {'channelId': channelId},
                    success: function (data) {
                        if (data.status == 0) {
                            // initTable('2');
                            searchData('0');
                            successMessager.show('删除成功');
                        } else {
                            failedMessager.show(data.msg);
                        }
                    },
                    error: function (data) {
                        failedMessager.show(data.msg);
                    }
                });
            });
        }
    },
}

function initChanPage() {
    // 保存
    $('#saveChan').click(function () {
        chanModal.saveChan();
    });
}

function initPage() {
    // 时间选择插件: 选择时间和日期
    // $(".form-datetime").datetimepicker({
    //     weekStart: 1,
    //     todayBtn: 1,
    //     autoclose: 1,
    //     todayHighlight: 1,
    //     startView: 2,
    //     forceParse: 0,
    //     showMeridian: 1,
    //     format: "yyyy-mm-dd hh:ii:ss"
    // });

    // 为tab绑定事件切换tab数据
    $('#authorityTab li').click(function () {
        var tabId = $(this).attr('tab-id');
        changeTab(tabId);
    });

    // 搜索
    $('.searchBtn').click(function () {
        searchData($(this).attr('table-type'));
    });
    initChanPage();
    initRolePage();
    initOrgPage();
    initUserPage();
}

/* 搜索功能 */
function searchData(tableType) {
    // tableType 0主表 1组表
    var inputs;
    if (tableType == '0') {
        inputs = $('.searchContainer .input-group .form-control');
    } else if (tableType == '1') {
        inputs = $('#groupDiv .input-group .form-control');
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
    if (obj.length != 0) {
        if (tableType == '0') {
            var tabId = $('#authorityTab').children('.active').attr('tab-id');
            initTable(tabId, obj);
        } else if (tableType == '1') {
            initGroupTable(obj);
        }
    }
}

/* 初始化tab内容 */
function initTabContent(tabId) {
    $('#otherMgrContent, #authorityMgr').removeClass('show').addClass('close');
    $('#groupDiv').removeClass('show').addClass('close');
    if (tabId == '3') { // 权限管理
        $('#authorityMgr').removeClass('close').addClass('show');
        authorityMgrModal.initAuthorityMgrPage(); // 初始化授权页面
    } else {
        $('#otherMgrContent').removeClass('close').addClass('show');
        $('.special .input-group').css('display', 'none');
        if (tabId == '0') { // 用户管理
            initRoleSelector(); // 初始化角色下拉框
            initOrgSelector(); // 初始化组织下拉框
            initUserGroupSelector(); // 初始化用户组下拉框
            initChanSelector();//初始化渠道下拉框
            // initGroupTable(); // 初始化用户组列表
            searchData('1');
            $('#groupDiv').removeClass('close').addClass('show');
            $('.special .userMgr').css('display', 'block');
            $('#firstTableIcon').text(' 用户列表');
            $('#authorityAddButton').text('添加用户').unbind('click').bind('click', function () {
                userModal.show(0);
            });
        } else if (tabId == '1') { // 角色
            $('.special .roleMgr').css('display', 'block');
            $('#firstTableIcon').text(' 角色列表');
            $('#authorityAddButton').text('添加角色').unbind('click').bind('click', function () {
                roleModal.show(0);
            });
        } else if (tabId == '2') { // 机构
            $('.special .orgMgr').css('display', 'block');
            orgModal.initOrgList();
            $('#firstTableIcon').text(' 机构列表');
            $('#authorityAddButton').text('添加组织').unbind('click').bind('click', function () {
                orgModal.show(0);
            });
        }else if (tabId == '4') { // 渠道
            $('.special .chanMgr').css('display', 'block');
            chanModal.initChanList();
            $('#firstTableIcon').text(' 渠道列表');
            $('#authorityAddButton').text('添加渠道').unbind('click').bind('click', function () {
                chanModal.show(0);
            });
        }
    }
}

/* 初始化表格 obj:搜索条件 */
function initTable(tabId, obj) {
    if (tabId == '3') {
        return;
    }
    var titles = initTitles(tabId); // 获取表头
    var url = initUrl(tabId); // 获取url
    obj == null ? '' : obj;
    $('#authorityPageTable').width('100%').dataTable({
        "searching": false, // 是否开启搜索功能
        "ordering": false,
        "destroy": true,
        "bLengthChange": false,
        "pagingType": "full_numbers",
        "paging": true,
        "info": true,
        "serverSide": true,
        "pageLength": 10,
        "columns": titles, // 不能为空
        ajax: {
            url: webpath + url,
            "type": 'POST',
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

/* 获取表格表头 */
function initTitles(tabId) {
    // 用户管理
    var userCols = [
        {"title": "账号", "data": "userId"},
        {"title": "用户名", "data": "userName"},
        {"title": "工号", "data": "jobNumber"},
        // {
        //     "title": "用户组", "data": "groupList", "render": function (data, type, row) {
        //         var gourpStr = '';
        //         for (var i = 0; i < data.length; i++) {
        //             if (i == (data.length - 1)) {
        //                 gourpStr += data[i].groupName;
        //             } else {
        //                 gourpStr += data[i].groupName + '，';
        //             }
        //         }
        //         return gourpStr;
        //     }
        // },
        {
            "title": "角色", "data": "roleList", "render": function (data, type, row) {
                var roleStr = '';
                for (var i = 0; i < data.length; i++) {
                    if (i == (data.length - 1)) {
                        roleStr += data[i].roleName;
                    } else {
                        roleStr += data[i].roleName + '，';
                    }
                }
                return roleStr;
            }
        },
        // {
        //     "title": "所属机构", "data": "deptList", "render": function (data, type, row) {
        //         var orgStr = '';
        //         for (var i = 0; i < data.length; i++) {
        //             orgStr += data[i].deptName;
        //         }
        //         return orgStr;
        //     }
        // },
        {
            "title": "所属渠道", "data": "channelList", "render": function (data, type, row) {
                var chanStr = '';
                for (var i = 0; i < data.length; i++) {
                    chanStr += data[i].channelName;
                }
                return chanStr;
            }
        },
        {
            "title": "操作", "data": null, "render": function (data, type, row) {
                var deptListArr = row.deptList;
                var channelListArr = row.channelList;
                var orgIds = new Array();
                var chanIds = new Array();
                for (var i = 0; i < deptListArr.length; i++) {
                    orgIds.push(deptListArr[i].deptId);
                }
                for (var i = 0; i < channelListArr.length; i++) {
                    chanIds.push(channelListArr[i].channelId);
                }
                var htmlStr = "";
                htmlStr += '<div id="user_' + row.userId + '" style="display:none;">' + JSON.stringify(row) + '</div>';
                htmlStr += '<span class="cm-tblB" onclick="userModal.show(2, \'' + row.userId + '\')" type="button">查看</span>';
                htmlStr += '<span class="cm-tblB" onclick="userModal.show(1, \'' + row.userId + '\')" type="button">修改</span>';
                htmlStr += '<span class="cm-tblC" type="button" onclick="userModal.deleteUser(\'' + row.userId + '\')">删除</span>';
                htmlStr += '<span class="cm-tblB" onclick="distributeModal.checkAuth(0, \'' + row.userId + '\', \'' + row.userName + '\', userModal.distributeCallBack)" type="button">选择角色</span>';
                htmlStr += '<span class="cm-tblB" onclick="chooseChanModal.authCheck(\'' + row.userId + '\', \'' + row.userName + '\', \'' + JSON.stringify(orgIds).replace(/"/g, '&quot;') + '\', userModal.chooseOrgCallBack)" type="button">选择渠道</span>';
                return htmlStr;
            }
        },
    ];
    // 角色管理 TODO
    var roleCols = [
        {"title": "角色名", "data": "roleName"},
        {"title": "角色描述", "data": "roleDesc"},
        {"title": "创建人", "data": "createPerson"},
        {"title": "创建时间", "data": "createDate"},
        {
            "title": "操作", "data": null, "render": function (data, type, row) {
                var htmlStr = "";
                htmlStr += '<span class="cm-tblB" onclick="roleModal.show(2, this)" type="button">查看</span>';
                htmlStr += '<span class="cm-tblB" onclick="roleModal.show(1, this)" type="button">修改</span>';
                htmlStr += '<span class="cm-tblC" onclick="roleModal.deleteRole(\'' + row.roleId + '\')" type="button">删除</span>';
                htmlStr += '<span class="cm-tblB" onclick="distributeModal.checkAuth(1, \'' + row.roleId + '\', \'' + row.roleName + '\', roleModal.distributeCallBack)" type="button">用户管理</span>';
                return htmlStr;
            }
        }
    ];
    // 机构管理
    var orgCols = [
        {"title": "机构名称", "data": "deptName"},
        {"title": "机构编码", "data": "deptCode"},
        // {"title": "上级机构", "data": "parentName"},
        {"title": "机构描述", "data": "deptDesc"},
        {"title": "创建人", "data": "createPerson"},
        {"title": "创建时间", "data": "createDate"},
        {
            "title": "操作", "data": "deptId", "render": function (data, type, row) {
                var htmlStr = "";
                htmlStr += '<span class="cm-tblB" onclick="orgModal.show(2, this)" type="button">查看</span>';
                htmlStr += '<span class="cm-tblB" onclick="orgModal.show(1, this)" type="button">修改</span>';
                htmlStr += '<span class="cm-tblC" onclick="orgModal.deleteOrg(\'' + row.deptId + '\')" type="button">删除</span>';
                htmlStr += '<span class="cm-tblB" onclick="orgUserModal.init(\'' + row.deptId + '\', \'' + row.deptName + '\')" type="button">查看渠道</span>';
                return htmlStr;
            }
        }
    ];
    // 渠道管理
    var chanCols = [
        {"title": "渠道名称", "data": "channelName"},
        {"title": "渠道编码", "data": "channelCode"},
        // {"title": "上级渠道", "data": "parentName"},
        {"title": "渠道描述", "data": "channelDesc"},
        {"title": "所属机构", "data": "deptName"},
        {"title": "创建人", "data": "createPerson"},
        {"title": "创建时间", "data": "createDate"},
        {
            "title": "操作", "data": "channelId", "render": function (data, type, row) {
                var htmlStr = "";
                htmlStr += '<span class="cm-tblB" onclick="chanModal.show(2, this)" type="button">查看</span>';
                htmlStr += '<span class="cm-tblB" onclick="chanModal.show(1, this)" type="button">修改</span>';
                htmlStr += '<span class="cm-tblC" onclick="chanModal.deleteChan(\'' + row.channelId + '\')" type="button">删除</span>';
                htmlStr += '<span class="cm-tblB" onclick="chanUserModal.init(\'' + row.channelId+ '\', \'' + row.channelName + '\')" type="button">查看用户</span>';
                return htmlStr;
            }
        }
    ];
    switch ($.trim(tabId)) {
        case '0':
            return userCols;
        case '1':
            return roleCols;
        case '2':
            return orgCols;
        case '4':
            return chanCols;
        default:
            return '';
    }
}

/* 匹配后台接口url */
function initUrl(typeId) {
    // 0用户管理 1角色管理 2机构管理 4渠道管理
    switch ($.trim(typeId)) {
        case '0':
            return '/user/list';
        case '1':
            return '/role/list';
        case '2':
            return '/dept/list';
        case '4':
            return '/channel/list';
        default:
            return '';
    }
}

/* 初始化用户组表格  */
function initGroupTable(obj) {
    obj == null ? '' : obj;
    $('#userGroupTable').width('100%').dataTable({
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
            {"title": "用户组名称", "data": "groupName"},
            {"title": "创建人", "data": "createPerson"},
            {"title": "创建时间", "data": "createDate"},
            {"title": "描述", "data": "groupDesc"},
            {
                "title": "操作", "data": null, "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<span class="cm-tblB " onclick="userGroupModal.showAddGroupAlert(2, this)" type="button">查看</span>';
                    htmlStr += '<span class="cm-tblB " onclick="userGroupModal.showAddGroupAlert(1, this)" type="button">修改</span>';
                    htmlStr += '<span class="cm-tblC " onclick="userGroupModal.deleteGroup(\'' + row.groupId + '\')" type="button">删除</span>';
                    return htmlStr;
                }
            }],
        ajax: {
            url: webpath + '/group/list',
            "type": 'POST',
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
    // 获取菜单tabId信息, 初始化tab页选中样式
    var initId = (tabId == '') ? '0' : tabId;
    $('#authorityTab li').removeClass('active');
    $("#authorityTab li[tab-id='" + initId + "']").addClass('active');
    // 初始化页面展示内容, 初始化各种下拉框, 为右上角添加按钮绑定事件等
    initTabContent(initId);
    // 初始化管理模块的表格
    initTable(initId);
    initPage();
    // 用户管理模块初始化用户组
    if (initId == '0') {
        initGroupTable();
    }
});