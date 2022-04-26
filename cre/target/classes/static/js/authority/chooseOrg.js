
var chooseChannelModal = {
    authCheck: function (userId, userName, orgIds, callBackFun) {
        // 修改用户 权限校验 authCheck
        $.ajax({
            url: webpath + '/user/update/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.status === 0) {
                    chooseChannelModal.initPage(userId, userName, orgIds, callBackFun);
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    initPage: function (userId, userName, orgIds, callBackFun) {
        // 回显用户信息
        $('#chooseOrgAlertModal').attr('userId', userId);
        $('#chooseOrg_userName').val(userName);

        // 初始化组织tree
        chooseChannelModal.initOrgTree(orgIds);

        // 绑定页面事件
        $('#saveChooseOrg').unbind().on('click', function () { // 保存
            chooseChannelModal.saveChoose(userId, callBackFun);
        });

        $('#cancelChooseOrg').unbind().on('click', function () { // 取消退出
            chooseChannelModal.clearAndClose();
        });

        $('#chooseOrgAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 初始化组织树
    initOrgTree: function (orgIds) {
        $.ajax({
            url: webpath + '/choose/channelTreeWithDept',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                var treeData = [];
                var data = data.data;
                if (data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        treeData.push(chooseChannelModal.createTreeNode(data[i]));
                    }
                }
                $('#chooseOrgTree').tree({data: treeData, initialState: 'expand'});
            },
            complete: function () {
                // 样式
                $('#chooseOrgTree .orgTree_parentNode').css({'margin': '0 8px 0 0', 'cursor': 'pointer'});
                $('#chooseOrgTree .orgTree_childNode').css({'margin': '0 8px 0 -8px', 'cursor': 'pointer'});

                $("#chooseOrgTree .orgTreeCheckBox").unbind().click(function () {
                    var currStatus = $(this).prop('checked');
                    $("#chooseOrgTree .orgTreeCheckBox").prop('checked', false);
                    $(this).prop('checked', currStatus);
                });

                // 回显
                var orgIdsArr = JSON.parse(orgIds);
                if (orgIdsArr.length > 0) {
                    for (var i = 0; i < orgIdsArr.length; i++) {
                        $("#chooseOrgTree .orgTreeCheckBox[data-id='" + orgIdsArr[i] + "']").prop('checked', true);
                    }
                }
            }
        });
    },
    // 构造tree节点
    createTreeNode: function (data) {
        var isParent = (data.children.length > 0) ? true : false; // 是否有子节点: 决定是否可以被选中
        var obj = {};
        var htmlStr = '';
        if (isParent) {
            htmlStr += '<input class="orgTreeCheckBox orgTree_parentNode" type="checkbox" data-id=\'' + data.id + '\'/><b>' + data.name + '</b>';
        } else {
            htmlStr += '<input class="orgTreeCheckBox orgTree_childNode" type="checkbox" data-id=\'' + data.id + '\'/><span>' + data.name + '</span>';
        }
        obj['html'] = htmlStr;
        obj['id'] = data.id;

        // 子节点
        if (isParent) {
            var childrenData = data.children;
            var children = [];
            for (var i = 0; i < childrenData.length; i++) {
                children.push(chooseChannelModal.createTreeNode(childrenData[i]));
            }
            obj['children'] = children;
        }
        return obj;
    },
    // 保存选择
    saveChoose: function (userId, callBackFun) {
        var checkedArr = $('#chooseOrgTree .orgTreeCheckBox:checked');
        if ((checkedArr.length > 0) && (checkedArr.length !== 1)) {
            failedMessager.show('只能选择一个渠道！');
            return;
        }
        if (userId && userId != '') {
            var channelId;
            for (var i = 0; i < checkedArr.length; i++) {
                channelId = $(checkedArr[i]).attr('data-id');
            }
            $.ajax({
                url: webpath + '/choose/userAddChannel',
                type: 'POST',
                dataType: "json",
                data: {"userId": userId, "channelId": channelId},
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('保存成功！');
                        chooseChannelModal.clearAndClose();
                        if (callBackFun) {
                            callBackFun();
                        }
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        }
    },
    // 清除缓存并退出
    clearAndClose: function () {
        $('#chooseOrgTree .orgTreeCheckBox').prop('checked', false);
        $('#chooseOrgAlertModal').modal('toggle', 'center');
    }
};


/**
 * 用户管理-选择机构页面
 * data:2019/08/19
 * author:bambi
 */

var chooseOrgModal = {
    // 权限authCheck
    authCheck: function (userId, userName, orgIds, callBackFun) {
        // 修改用户 权限校验 authCheck
        $.ajax({
            url: webpath + '/user/update/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.status === 0) {
                    chooseOrgModal.initPage(userId, userName, orgIds, callBackFun);
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    /**
     * 初始化组织树选择页面
     *      userId: 用户id
     *      userName: 用户名
     *      orgIds: 需要回显的已有orgIds
     *      callBackFun: 保存成功后的回调函数
     */
    initPage: function (userId, userName, orgIds, callBackFun) {
        // 回显用户信息
        $('#chooseOrgAlertModal').attr('userId', userId);
        $('#chooseOrg_userName').val(userName);

        // 初始化组织tree
        chooseOrgModal.initOrgTree(orgIds);

        // 绑定页面事件
        $('#saveChooseOrg').unbind().on('click', function () { // 保存
            chooseOrgModal.saveChoose(userId, callBackFun);
        });

        $('#cancelChooseOrg').unbind().on('click', function () { // 取消退出
            chooseOrgModal.clearAndClose();
        });

        $('#chooseOrgAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 初始化组织树
    initOrgTree: function (orgIds) {
        $.ajax({
            url: webpath + '/choose/deptTree',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                var treeData = [];
                var data = data.data;
                if (data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        treeData.push(chooseOrgModal.createTreeNode(data[i]));
                    }
                }
                $('#chooseOrgTree').tree({data: treeData, initialState: 'expand'});
            },
            complete: function () {
                // 样式
                $('#chooseOrgTree .orgTree_parentNode').css({'margin': '0 8px 0 0', 'cursor': 'pointer'});
                $('#chooseOrgTree .orgTree_childNode').css({'margin': '0 8px 0 -8px', 'cursor': 'pointer'});

                $("#chooseOrgTree .orgTreeCheckBox").unbind().click(function () {
                    var currStatus = $(this).prop('checked');
                    $("#chooseOrgTree .orgTreeCheckBox").prop('checked', false);
                    $(this).prop('checked', currStatus);
                });

                // 回显
                var orgIdsArr = JSON.parse(orgIds);
                if (orgIdsArr.length > 0) {
                    for (var i = 0; i < orgIdsArr.length; i++) {
                        $("#chooseOrgTree .orgTreeCheckBox[data-id='" + orgIdsArr[i] + "']").prop('checked', true);
                    }
                }
            }
        });
    },
    // 构造tree节点
    createTreeNode: function (data) {
        var isParent = (data.children.length > 0) ? true : false; // 是否有子节点: 决定是否可以被选中
        var obj = {};
        var htmlStr = '';
        if (isParent) {
            htmlStr += '<input class="orgTreeCheckBox orgTree_parentNode" type="checkbox" data-id=\'' + data.id + '\'/><b>' + data.departName + '</b>';
        } else {
            htmlStr += '<input class="orgTreeCheckBox orgTree_childNode" type="checkbox" data-id=\'' + data.id + '\'/><span>' + data.departName + '</span>';
        }
        obj['html'] = htmlStr;
        obj['id'] = data.id;

        // 子节点
        if (isParent) {
            var childrenData = data.children;
            var children = [];
            for (var i = 0; i < childrenData.length; i++) {
                children.push(chooseOrgModal.createTreeNode(childrenData[i]));
            }
            obj['children'] = children;
        }
        return obj;
    },
    // 保存选择
    saveChoose: function (userId, callBackFun) {
        var checkedArr = $('#chooseOrgTree .orgTreeCheckBox:checked');
        if ((checkedArr.length > 0) && (checkedArr.length !== 1)) {
            failedMessager.show('只能选择一个组织机构！');
            return;
        }
        if (userId && userId != '') {
            var deptIds = new Array();
            for (var i = 0; i < checkedArr.length; i++) {
                deptIds.push($(checkedArr[i]).attr('data-id'));
            }
            $.ajax({
                url: webpath + '/choose/userAddDept',
                type: 'POST',
                dataType: "json",
                data: {"userId": userId, "deptIds": deptIds},
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('保存成功！');
                        chooseOrgModal.clearAndClose();
                        if (callBackFun) {
                            callBackFun();
                        }
                    }
                }
            });
        }
    },
    // 清除缓存并退出
    clearAndClose: function () {
        $('#chooseOrgTree .orgTreeCheckBox').prop('checked', false);
        $('#chooseOrgAlertModal').modal('toggle', 'center');
    }
}