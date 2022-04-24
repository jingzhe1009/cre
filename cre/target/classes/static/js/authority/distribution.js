/**
 * 角色选多用户 / 用户选多角色 资源分配弹框
 * data:2019/07/31
 * author:bambi
 */

var distributeModal = {
    // 权限check
    checkAuth: function (resourceType, targetId, targetName, callBackFunction) {
        if (resourceType == 0) {
            // 修改用户 权限校验 authCheck
            $.ajax({
                url: webpath + '/user/update/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {},
                success: function (data) {
                    if (data.status === 0) {
                        distributeModal.init(resourceType, targetId, targetName, callBackFunction);
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (resourceType == 1) {
            // 修改角色 权限校验 authCheck
            $.ajax({
                url: webpath + '/role/update/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {},
                success: function (data) {
                    if (data.status === 0) {
                        distributeModal.init(resourceType, targetId, targetName, callBackFunction);
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else {
            return;
        }
    },
    /**
     * 初始化资源分配弹框
     *      resourceType
     *          0用户选择多角色
     *          1角色选择多用户
     *      targetId : 分配对象id
     *      targetName : 分配对象名称
     */
    init: function (resourceType, targetId, targetName, callBackFunction) {
        initDistributePage();
        // 初始化弹框内的信息字段等
        var obj = distributeModal.initContentContent(resourceType);
        $('#ResourceDistributionAlertModal .modal-title').text(obj.title);
        $('.leftTableIcon').text(obj.leftTitle);
        $('.rightTableIcon').text(obj.rightTitle);
        var dataIdField = obj.dataIdField;
        var dataValueField = obj.dataValueField;
        var checkAllName = obj.checkAllName;
        var targetIdField = obj.targetIdField;

        // target回显
        var targetContainer = $(".resourceTargetContainer div[resourceType='" + resourceType + "']");
        $('.resourceTargetContainer div').css('display', 'none');
        targetContainer.css('display', 'block');
        targetContainer.children('input').val(targetName).attr('data-id', targetId);

        // 初始化左右表格
        var leftUrl = initDistributeUrl(resourceType, true);
        var rightUrl = initDistributeUrl(resourceType, false);
        initDistributeTable(resourceType, leftUrl, $('#distributeLeftTable'), dataIdField, dataValueField, checkAllName, targetIdField, targetId); // 左可选表
        initDistributeTable(resourceType, rightUrl, $('#distributeRightTable'), dataIdField, dataValueField, checkAllName, targetIdField, targetId); // 右已选表
        $('#ResourceDistributionAlertModal').modal({'show': 'center', "backdrop": "static"});

        // 绑定保存事件
        $('#saveDistribute').unbind().on('click', function () {
            distributeModal.saveChange(resourceType, targetIdField, targetId, obj.idListField, callBackFunction);
        });

        // 全选/全不选事件绑定
        $('.distributeTableContainer ul').on('click', '.checkAllBox input', function () {
            if ($(this).prop('checked')) {
                $(this).parents('ul').find('input[type=checkbox]').prop('checked', true);
            } else {
                $(this).parents('ul').find('input[type=checkbox]').prop('checked', false);
            }
        });
    },
    // 关闭弹框
    closeModal: function () {
        $('#ResourceDistributionAlertModal').modal('toggle', 'center');
    },
    // 初始化展示内容
    initContentContent: function (resourceType) {
        var obj = {};
        if (resourceType == 0) {
            obj['targetIdField'] = 'userId';
            obj['title'] = '用户选择角色';
            obj['leftTitle'] = ' 可选择角色列表';
            obj['rightTitle'] = ' 已选择角色列表';
            obj['checkAllName'] = '角色名称';
            obj['dataIdField'] = 'roleId';
            obj['dataValueField'] = 'roleName';
            obj['idListField'] = 'roleIds';
        }
        if (resourceType == 1) {
            obj['targetIdField'] = 'roleId';
            obj['title'] = '角色选择用户';
            obj['leftTitle'] = ' 可选择用户列表';
            obj['rightTitle'] = ' 已选择用户列表';
            obj['checkAllName'] = '用户名称';
            obj['dataIdField'] = 'userId';
            obj['dataValueField'] = 'userName';
            obj['idListField'] = 'userIds';
        }
        return obj;
    },
    // 拼接列表内容
    initCheckboxContent: function (dataId, dataValue, isAllCheck, allCheckName) {
        var htmlStr = '';
        if (isAllCheck) { // 表头
            htmlStr += '<div data-id=\'' + dataId + '\' class="checkbox checkAllBox"><label><input type="checkbox"><span>' + allCheckName + '</span></label></div>';
        } else {
            htmlStr += '<div data-id=\'' + dataId + '\' class="checkbox dataCheckbox"><label><input class="dataInput" type="checkbox"><span>' + dataValue + '</span></label></div>';
        }
        return htmlStr;
    },
    // 移动选中项至另一边 0 左->右; 1 右->左
    changeSide: function (side) {
        // 筛选出左边/右边选中的checkbox
        var leftCheckedArr = $('#distributeLeftTable .dataInput:checked').parents('.checkbox');
        var rightCheckedArr = $('#distributeRightTable .dataInput:checked').parents('.checkbox');
        if (leftCheckedArr.length === 0 && rightCheckedArr.length === 0) {
            failedMessager.show('请选择！');
            return;
        }
        if (side === 0 && leftCheckedArr.length === 0) {
            return;
        }
        if (side === 1 && rightCheckedArr.length === 0) {
            return;
        }
        var leftCheckedHtml = distributeModal.handleCheckArr(leftCheckedArr);
        var rightCheckedHtml = distributeModal.handleCheckArr(rightCheckedArr);

        if (side === 0) {
            leftCheckedArr.remove();
            $('#distributeRightTable').append(leftCheckedHtml);
        } else if (side === 1) {
            rightCheckedArr.remove();
            $('#distributeLeftTable .checkbox').last().after(rightCheckedHtml);
        } else {
            return;
        }
        // 还原全选状态
        $('.distributeTableContainer .checkAllBox input').prop('checked', false);
    },
    // 处理选中项, 拼接htmlStr
    handleCheckArr: function (checkArr) {
        var htmlStr = '';
        if (checkArr.length > 0) {
            for (var i = 0; i < checkArr.length; i++) {
                var dataId = $(checkArr[i]).attr('data-id');
                var dataValue = $(checkArr[i]).find('span').text();
                htmlStr += distributeModal.initCheckboxContent(dataId, dataValue, false, '');
            }
        }
        return htmlStr;
    },
    /**
     * 保存
     *      dataIdField: id字段
     *      targetId: id值
     *      idListField: 已选项列表回传字段
     */
    saveChange: function (resourceType, targetIdField, targetId, idListField, callBackFunction) {
        var url = getDistributeSaveUrl(resourceType);
        var obj = distributeModal.getPageData(targetIdField, targetId, idListField);
        if (obj) {
            $.ajax({
                url: webpath + url,
                type: 'POST',
                dataType: "json",
                data: obj,
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('保存成功');
                        distributeModal.closeModal();
                        callBackFunction();
                    } else {
                        failedMessager.show(data.msg);
                    }
                },
                error: function (data) {
                    failedMessager.show(data.msg);
                },
            });
        }
    },
    // 获取页面数据
    getPageData: function (targetIdField, targetId, idListField) {
        var obj = {};
        var checkIds = new Array();
        if (!(targetIdField && targetId && idListField)) {
            failedMessager.show('参数不全，无法保存！');
            return;
        }
        var checkArr = $('#distributeRightTable .dataCheckbox');
        if (checkArr.length > 0) {
            for (var i = 0; i < checkArr.length; i++) {
                var id = $(checkArr[i]).attr('data-id');
                if (id != '') {
                    checkIds.push(id);
                }
            }
        }
        obj[targetIdField] = targetId;
        obj[idListField] = checkIds;
        return obj;
    },
    // 搜索
    searchFromList: function (side) {
        // 0左侧 1右侧
        var leftInput = $.trim($('.threeRowsSelfAdaption_left .distributeSearchInput').val());
        var rightInput = $.trim($('.threeRowsSelfAdaption_right .distributeSearchInput').val());
        if (side == '0') {
            distributeModal.showSearchResult($('#distributeLeftTable'), leftInput);
        } else if (side == '1') {
            distributeModal.showSearchResult($('#distributeRightTable'), rightInput);
        } else {
            return;
        }
    },
    // 展示搜索结果列表
    showSearchResult: function (searchTargetContainer, inputValue) {
        if (inputValue == '') {
            searchTargetContainer.find('.dataCheckbox').show();
        } else {
            searchTargetContainer.find('.dataCheckbox').hide().filter(':contains(' + inputValue + ')').show();
        }
    }
}

function initDistributePage() {
    // 回显的角色/用户信息不可选中
    $('.resourceTargetContainer .form-control').unbind().focus(function () {
        $(this).blur();
    });

    // 移动方向按钮绑定事件
    $('#leftToRight').unbind().click(function () {
        distributeModal.changeSide(0);
    });

    $('#rightToLeft').unbind().click(function () {
        distributeModal.changeSide(1);
    });

    // 资源列表搜索
    $('.distributeSearch').unbind().click(function () {
        distributeModal.searchFromList($(this).attr('side'));
    });
}

// 初始化表格
function initDistributeTable(resourceType, url, container, dataIdField, dataValueField, checkAllName, targetIdField, targetId) {
    // resourceType: 0用户选择多角色 1角色选择多用户
    var obj = {};
    obj[targetIdField] = targetId;
    if (url != '') {
        $.ajax({
            url: webpath + url,
            type: 'POST',
            dataType: "json",
            data: obj,
            success: function (data) {
                var htmlStr = '';
                htmlStr += distributeModal.initCheckboxContent('', '', true, checkAllName); // 表头
                if (data.data.length > 0) {
                    var dataArr = data.data;
                    for (var i = 0; i < dataArr.length; i++) {
                        htmlStr += distributeModal.initCheckboxContent(dataArr[i][dataIdField], dataArr[i][dataValueField], false, '');
                    }
                }
                container.html(htmlStr);
            },
            error: function (data) {
                failedMessager.show(data.msg);
            },
        });
    }
}

// 初始化左右资源列表
//       resourceType 0用户选择多角色 1角色选择多用户
//       isLeft 左可选 右已选
function initDistributeUrl(resourceType, isLeft) {
    var urlStr = '';
    if (resourceType == 0) {
        urlStr = isLeft ? '/choose/userRole' : '/choose/user2Role';
    } else if (resourceType == 1) {
        urlStr = isLeft ? '/choose/roleUser' : '/choose/role2User';
    }
    return urlStr;
}

function getDistributeSaveUrl(resourceType) {
    var urlStr = '';
    if (resourceType == 0) {
        urlStr = '/choose/userAddRole';
    } else if (resourceType == 1) {
        urlStr = '/choose/roleAddUser';
    }
    return urlStr;
}
