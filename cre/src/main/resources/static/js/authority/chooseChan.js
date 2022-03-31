/**
 * 用户管理-选择渠道页面
 *
 */
var chooseChanModal = {
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
                    chooseChanModal.initPage(userId, userName, orgIds, callBackFun);
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    /**
     * 初始化渠道树选择页面
     *      userId: 用户id
     *      userName: 用户名
     *
     *      callBackFun: 保存成功后的回调函数
     */
    initPage: function (userId, userName, orgIds, callBackFun) {
        // 回显用户信息
        $('#chooseChanAlertModal').attr('userId', userId);
        $('#chooseChan_userName').val(userName);

        // 初始化组织tree
        chooseChanModal.initOrgTree(orgIds);

        // 绑定页面事件
        $('#saveChooseOrg').unbind().on('click', function () { // 保存
            chooseChanModal.saveChoose(userId, callBackFun);
        });

        $('#cancelChooseOrg').unbind().on('click', function () { // 取消退出
            chooseChanModal.clearAndClose();
        });

        $('#chooseChanAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 初始化组织树
    initOrgTree: function (orgIds) {
        $.ajax({
            url: webpath + '/choose/channelTree',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                var treeData = [];
                var data = data.data;
                if (data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        treeData.push(chooseChanModal.createTreeNode(data[i]));
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
            htmlStr += '<input class="chanTreeCheckBox chanTree_parentNode" type="checkbox" data-id=\'' + data.id + '\'/><b>' + data.placeName + '</b>';
        } else {
            htmlStr += '<input class="chanTreeCheckBox chanTree_childNode" type="checkbox" data-id=\'' + data.id + '\'/><span>' + data.placeName + '</span>';
        }
        obj['html'] = htmlStr;
        obj['id'] = data.id;

        // 子节点
        if (isParent) {
            var childrenData = data.children;
            var children = [];
            for (var i = 0; i < childrenData.length; i++) {
                children.push(chooseChanModal.createTreeNode(childrenData[i]));
            }
            obj['children'] = children;
        }
        return obj;
    },
    // 保存选择
    saveChoose: function (userId, callBackFun) {
        var checkedArr = $('#chooseOrgTree .chanTreeCheckBox:checked');
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
                        chooseChanModal.clearAndClose();
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
        $('#chooseChanAlertModal').modal('toggle', 'center');
    }
}