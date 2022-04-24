var menuCache = {}; // 菜单缓存 {menuResourceId:[{resourceId:id, url:url, typeId:typeId}, ...]}

var metaTableCacheOld = [];
var metaTableCache = {}; // 元数据缓存 {dataRowResourceId: checkedUrlStr}

var ruleCacheOld = [];
var ruleCache = {}; // 模型缓存 {dataRowResourceId: checkedUrlStr}

var modelGroupCacheOld = [];
var modelGroupCache = {}; // 产品缓存 {dataRowResourceId: checkedUrlStr}

var paramCacheOld = [];
var paramCache = {}; // 公共参数缓存 {dataRowResourceId: checkedUrlStr}
var entityCache = {}; // 对象参数entityId缓存 {dataRowResourceId: entityId}

var paramGroupCacheOld = [];
var paramGroupCache = {};  // 公共参数组缓存 {dataRowResourceId: checkedUrlStr}

var apiCacheOld = [];
var apiCache = {}; // 公共接口缓存 {dataRowResourceId: checkedUrlStr}

var apiGroupCacheOld =[];
var apiGroupCache = {}; // 接口组缓存

var ruleSetCacheOld = [];
var ruleSetCache = {}; // 规则集缓存 {dataRowResourceId: checkedUrlStr}

var ruleSetGroupCacheOld = [];
var ruleSetGroupCache = {}; // 规则集组缓存 {dataRowResourceId: checkedUrlStr}

var modelCacheOld = [];
var modelCache = {}; // 模型库缓存 {dataRowResourceId: checkedUrlStr}

var dataSourceCacheOld = [];
var dataSourceCache = {}; // 数据源缓存 {dataRowResourceId: checkedUrlStr}

var taskCacheOld = [];
var taskCache = {}; // 离线任务缓存 {dataRowResourceId: checkedUrlStr}

var kpiCacheOld = [];
var kpiCache = {}; // 指标缓存 {dataRowResourceId: checkedUrlStr}

var kpiGroupCacheOld = [];
var kpiGroupCache = {}; // 指标组缓存 {dataRowResourceId: checkedUrlStr}

var folderCacheOld = [];
var folderCache = {}; // 场景缓存 {dataRowResourceId: checkedUrlStr}

/**
 * 授权页面
 * data:2019/07/10
 * author:bambi
 */
var authorityMgrModal = {
    // 面板颜色主题
    themeArr: [
        'panel-success',
        'panel-warning',
        'panel-danger',
        'panel-info',
        'panel-pink'
        // 'panel-primary',
        // 'panel-brown'
    ],
    // 初始化授权页面
    initAuthorityMgrPage: function () {
        // 页面面板主题设置
        var panelArr = $('#authorityMgrContent .authorityMgrEle');
        var themeArr = authorityMgrModal.themeArr;
        var themeIndex = 0;
        for (var i = 0; i < panelArr.length; i++) {
            if (themeIndex >= themeArr.length) {
                themeIndex = 0;
            }
            var panelClass = themeArr[themeIndex];
            $(panelArr[i]).addClass(panelClass);
            $(panelArr[i]).find('.authorityMgrEle_saveButton').addClass(panelClass + '-save-btn');
            themeIndex++;
        }

        // 清空全部缓存
        authorityMgrModal.clearCache('0');

        // 初始化角色下拉框
        authorityMgrModal.initRoleSelectors();

        // 初始化搜索栏下拉框
        authorityMgrModal.initSearchSelectors();

        // 搜索功能
        $('#authorityMgr .searchBtn').click(function () {
            var obj = {};
            var objStr = '';
            var currRoleId = $('#authorityHeader .roleSelector option:checked').attr('role-id');
            objStr += '?roleId=' + currRoleId;
            var tableId = $(this).attr('table-id');
            var inputs = $(this).parent().siblings('.search_selfAdaptionLeft').find('.form-control');
            for (var i = 0; i < inputs.length; i++) {
                if ($(inputs[i]).val() == '') {
                    continue;
                }
                obj[$(inputs[i]).attr('data-col')] = $(inputs[i]).val();
            }
            if ('ruleType' in obj) { // 模型类型
                obj['ruleType'] = $(this).parents('.authorityMgrEle').find('.ruleTypeInput').attr('data-id');
            }
            if ('variableTypeId' in obj) { // 变量类型
                obj['variableTypeId'] = $('#variableTypeIdInput').attr('data-id');
            }
            if ('fetchType' in obj) { // 指标取数方式
                obj['fetchType'] = $(this).parents('.authorityMgrEle').find('.fetchTypeInput').attr('data-id');
            }
            if ('kpiType' in obj) { // 指标类型
                obj['kpiType'] = $(this).parents('.authorityMgrEle').find('.kpiTypeInput').attr('data-id');
            }
            if ('dbType' in obj) { // 数据源类型
                obj['dbType'] = $(this).parents('.authorityMgrEle').find('.dbTypeInput').attr('data-id');
            }
            if ('ruleName' in obj) { // 模型id
                obj['ruleName'] = $(this).parents('.authorityMgrEle').find('.ruleNameInput').attr('data-id');
            }

            for (var key in obj) {
                objStr += '&' + key + '=' + obj[key];
            }
            authorityMgrModal.initTables(tableId, objStr, currRoleId, false);
        });

        // 不同模块保存事件绑定
        $('#authorityMgrContent .authorityMgrEle_saveButton').click(function () {
            var grantType = $(this).attr('grant-type');
            var currRoleId = $('#authorityHeader .roleSelector option:checked').attr('role-id');
            authorityMgrModal.saveGrant(grantType, currRoleId, authorityMgrModal.getGrantObj(grantType), 1);
        });

        // 页面全权(全权/全部无权)
        $('#allAuthCheck').click(function () {
            var flag = $(this).attr('all-check');
            var msg = (flag === '0') ? '确认赋全权？' : '确认取消全权？';
            confirmAlert.show(msg,
                function () {
                    var currRoleId = $('#authorityHeader .roleSelector option:checked').attr('role-id');
                    if (flag === '0') {
                        // 当前非全权: 赋予全权
                        $.ajax({
                            url: webpath + '/auth/all/grant',
                            type: 'POST',
                            dataType: "json",
                            data: {"roleId": currRoleId},
                            beforeSend: function () {
                                loading.show();
                            },
                            success: function (data) {
                                if (data.status === 0) {
                                    successMessager.show('授权保存成功！');
                                    authorityMgrModal.initTables(0, '?roleId=' + currRoleId, currRoleId, true); // 刷新对应权限表格
                                } else {
                                    failedMessager.show(data.msg);
                                }
                            },
                            complete: function () {
                                loading.hide();
                            }
                        });
                    } else if (flag === '1') {
                        // 当前全权: 删除全权
                        $.ajax({
                            url: webpath + '/auth/all/delete',
                            type: 'POST',
                            dataType: "json",
                            data: {"roleId": currRoleId},
                            beforeSend: function () {
                                loading.show();
                            },
                            success: function (data) {
                                if (data.status === 0) {
                                    successMessager.show('修改成功！');
                                    authorityMgrModal.initTables(0, '?roleId=' + currRoleId, currRoleId, true); // 刷新对应权限表格
                                } else {
                                    failedMessager.show(data.msg);
                                }
                            },
                            complete: function () {
                                loading.hide();
                            }
                        });
                    } else {
                        return;
                    }
                }
            );

        });

        // 全页保存
        $('#saveAuthority').click(function () {
            // 权限修改验证 authCheck
            $.ajax({
                url: webpath + '/auth/update/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {},
                beforeSend: function () {
                    loading.show();
                },
                success: function (data) {
                    if (data.status === 0) {
                        // 拥有权限 各表保存
                        var tableSaveArr = $('#authorityMgrContent .authorityMgrEle_saveButton');
                        var currRoleId = $('#authorityHeader .roleSelector option:checked').attr('role-id');
                        for (var i = 0; i < tableSaveArr.length; i++) {
                            var grantType = $(tableSaveArr[i]).attr('grant-type');
                            authorityMgrModal.saveGrant(grantType, currRoleId, authorityMgrModal.getGrantObj(grantType), 0);
                        }
                    } else {
                        failedMessager.show(data.msg);
                    }
                },
                complete: function () {
                    successMessager.show('授权保存成功！');
                    loading.hide();
                }
            });
        });
    },
    // 初始化搜索栏下拉框
    initSearchSelectors: function () {
        // 场景列表
        $.ajax({
            url: webpath + '/ruleFolder/ruleFolderList',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.length > 0) {
                    var htmlStr = '';
                    for (var i = 0; i < data.length; i++) {
                        htmlStr += '<li data-id=\'' + data[i].key + '\'><a>' + data[i].text + '</a></li>';
                    }
                    $('#authorityMgr .folderSelector').empty().html(htmlStr);
                }
            }, complete: function () {
                // 绑定事件
                $('#authorityMgr .folderSelector>li').unbind('click').on('click', function () {
                    var input = $(this).parent().siblings('.form-control');
                    input.val($(this).first().text());
                    input.attr('data-id', $(this).attr('data-id'));
                    if ($(this).parents('.authorityMgrEle').hasClass('taskPanel')) {
                        authorityMgrModal.getFolderModels($(this).attr('data-id'));
                    }
                });
            }
        });

        // 参数组
        $.ajax({
            url: webpath + '/variable/pub/group/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.status === 0) {
                    if (data.data.length > 0) {
                        var dataArr = data.data;
                        var htmlStr_search = '';
                        for (var i = 0; i < dataArr.length; i++) {
                            htmlStr_search += '<li data-id=\'' + dataArr[i].variableGroupId + '\'><a>' + dataArr[i].variableGroupName + '</a></li>';
                        }
                        $('#authorityMgr .paramGroupSelector').empty().html(htmlStr_search);
                    }
                }
            }, complete: function () {
                // 绑定事件
                $('#authorityMgr .paramGroupSelector>li').unbind('click').on('click', function () {
                    var input = $(this).parent().siblings('.form-control');
                    input.val($(this).first().text());
                    input.attr('data-id', $(this).attr('data-id'));
                });
            }
        });

        // 变量类型
        $.ajax({
            url: webpath + '/variable/variableType/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.status === 0) {
                    if (data.data.length > 0) {
                        var dataArr = data.data;
                        var htmlStr = '';
                        for (var i = 0; i < dataArr.length; i++) {
                            htmlStr += '<li data-id=\'' + dataArr[i].key + '\'><a>' + dataArr[i].text + '</a></li>';
                        }
                        $('#authorityMgr .variableTypeSelector').empty().html(htmlStr);
                    }
                }
            }, complete: function () {
                // 绑定事件
                $('#authorityMgr .variableTypeSelector>li').unbind('click').on('click', function () {
                    var input = $(this).parent().siblings('.form-control');
                    input.val($(this).first().text());
                    input.attr('data-id', $(this).attr('data-id'));
                });
            }
        });

        // 接口组
        $.ajax({
            url: webpath + '/api/pub/group/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.status === 0) {
                    if (data.data.length > 0) {
                        var dataArr = data.data;
                        var htmlStr_search = '';
                        for (var i = 0; i < dataArr.length; i++) {
                            htmlStr_search += '<li data-id=\'' + dataArr[i].apiGroupId + '\'><a>' + dataArr[i].apiGroupName + '</a></li>';
                        }
                        $('#authorityMgr .apiGroupSelector').empty().html(htmlStr_search);
                    }
                }
            }, complete: function () {
                // 绑定事件
                $('#authorityMgr .apiGroupSelector>li').unbind('click').on('click', function () {
                    var input = $(this).parent().siblings('.form-control');
                    input.val($(this).first().text());
                    input.attr('data-id', $(this).attr('data-id'));
                });
            }
        });

        // 规则集组
        $.ajax({
            url: webpath + '/ruleSet/group/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.status === 0) {
                    if (data.data.length > 0) {
                        var dataArr = data.data;
                        var htmlStr_search = '';
                        for (var i = 0; i < dataArr.length; i++) {
                            htmlStr_search += '<li data-id=\'' + dataArr[i].ruleSetGroupId + '\'><a>' + dataArr[i].ruleSetGroupName + '</a></li>';
                        }
                        $('#authorityMgr .ruleSetGroupSelector').empty().html(htmlStr_search);
                    }
                }
            }, complete: function () {
                // 绑定事件
                $('#authorityMgr .ruleSetGroupSelector>li').unbind('click').on('click', function () {
                    var input = $(this).parent().siblings('.form-control');
                    input.val($(this).first().text());
                    input.attr('data-id', $(this).attr('data-id'));
                });
            }
        });

        // 模型组
        $.ajax({
            url: webpath + '/modelBase/group/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.status === 0) {
                    if (data.data.length > 0) {
                        var dataArr = data.data;
                        var htmlStr_search = '';
                        for (var i = 0; i < dataArr.length; i++) {
                            htmlStr_search += '<li data-id=\'' + dataArr[i].modelGroupId + '\'><a>' + dataArr[i].modelGroupName + '</a></li>';
                        }
                        $('#authorityMgr .modelGroupSelector').empty().html(htmlStr_search);
                    }
                }
            }, complete: function () {
                // 绑定事件
                $('#authorityMgr .modelGroupSelector>li').unbind('click').on('click', function () {
                    var input = $(this).parent().siblings('.form-control');
                    input.val($(this).first().text());
                    input.attr('data-id', $(this).attr('data-id'));
                });
            }
        });

        // 指标组
        $.ajax({
            url: webpath + '/kpi/group/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.status === 0) {
                    if (data.data.length > 0) {
                        var dataArr = data.data;
                        var htmlStr_search = '';
                        for (var i = 0; i < dataArr.length; i++) {
                            htmlStr_search += '<li data-id=\'' + dataArr[i].kpiGroupId + '\'><a>' + dataArr[i].kpiGroupName + '</a></li>';
                        }
                        $('#authorityMgr .kpiGroupSelector').empty().html(htmlStr_search);
                    }
                }
            }, complete: function () {
                // 绑定事件
                $('#authorityMgr .kpiGroupSelector>li').unbind('click').on('click', function () {
                    var input = $(this).parent().siblings('.form-control');
                    input.val($(this).first().text());
                    input.attr('data-id', $(this).attr('data-id'));
                });
            }
        });

        // 规则类型、指标取数方式、指标类型、数据源类型
        $('#authorityMgr .ruleTypeSelector>li, #authorityMgr .kpiFetchTypeSelector>li, #authorityMgr .kpiTypeSelector>li, #authorityMgr .dbTypeSelector>li')
            .unbind('click').on('click', function () {
            var input = $(this).parent().siblings('.form-control');
            input.val($(this).first().text());
            input.attr('data-id', $(this).attr('data-id'));
        });
    },
    // 获取场景下所有模型
    getFolderModels: function (foldId) {
        $('#authorityMgr .ruleNameSelector').empty();
        $('#authorityMgr .ruleVersionSelector').empty();
        $('#authorityMgr .ruleNameInput').val('');
        $('#authorityMgr .ruleVersionInput').val('');
        $.ajax({
            url: webpath + "/ruleFolder/ruleName",
            data: {"foldId": foldId},
            dataType: "json",
            success: function (data) {
                var htmlStr = "";
                if (data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        htmlStr += '<li data-id=\'' + data[i].ruleName + '\'><a>' + data[i].moduleName + '</a></li>';
                    }
                } else {
                    htmlStr += '<li><a>--无--</a></li>';
                }
                $('#authorityMgr .ruleNameSelector').html(htmlStr);
                $('#authorityMgr .ruleNameSelector>li').unbind('click').on('click', function () {
                    var input = $(this).parent().siblings('.form-control');
                    input.val($(this).first().text());
                    input.attr('data-id', $(this).attr('data-id'));
                    authorityMgrModal.getModelVersions($(this).attr('data-id'));
                });
            }
        })
    },
    // 获取模型下所有版本
    getModelVersions: function (ruleName) {
        $('#authorityMgr .ruleVersionSelector').empty();
        $('#authorityMgr .ruleVersionInput').val('');
        $.ajax({
            url: webpath + "/rule/versions",
            data: {"ruleName": ruleName, "isPublic": '0'},
            type: "GET",
            dataType: "json",
            success: function (data) {
                if (data.status === 0) {
                    var htmlStr = "";
                    if (data.data.length > 0) {
                        for (var i = 0; i < data.data.length; i++) {
                            htmlStr += '<li data-id=\'' + data.data[i].ruleId + '\'><a>' + data.data[i].version + '</a></li>';
                        }
                    } else {
                        htmlStr += '<li><a>--无--</a></li>';
                    }
                    $('#authorityMgr .ruleVersionSelector').html(htmlStr);
                    $('#authorityMgr .ruleVersionSelector li').unbind().click(function () {
                        var input = $(this).parent().siblings('.form-control');
                        input.val($(this).first().text());
                        input.attr('data-id', $(this).attr('data-id'));
                    });
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 获取表cache
    getTableCache: function (tableId) {
        if (tableId == '1') { // 菜单table
            return menuCache;
        } else if (tableId == '2') { // 元数据table
            return metaTableCache;
        } else if (tableId == '3') { // 模型table
            return ruleCache;
        } else if (tableId == '4') { // 公共参数table
            return paramCache;
        } else if (tableId == '5') { // 公共接口table
            return apiCache;
        } else if (tableId == '6') { // 规则库table
            return ruleSetCache;
        } else if (tableId == '7') { // 模型库table
            return modelCache;
        } else if (tableId == '8') { // 数据源table
            return dataSourceCache;
        } else if (tableId == '9') { // 离线任务table
            return taskCache;
        } else if (tableId == '10') { // 指标table
            return kpiCache;
        } else if (tableId == '11') { // 场景table
            return folderCache;
        } else if (tableId == '13') { // 接口组table
            return apiGroupCache;
        } else if (tableId == '14') { // 指标组table
            return kpiGroupCache;
        } else if (tableId == '15') { // 规则集组table
            return ruleSetGroupCache;
        } else if (tableId == '12') { //  参数组table
            return paramGroupCache;
        } else if (tableId == '16') { //  产品table
            return modelGroupCache;
        } else {
            return;
        }
    },
    // 获取数据级别oldCache
    getTableOldCache: function (tableId) {
        if (tableId == '2') { // 元数据table
            return metaTableCacheOld;
        } else if (tableId == '3') { // 模型table
            return ruleCacheOld;
        } else if (tableId == '4') { // 公共参数table
            return paramCacheOld;
        } else if (tableId == '5') { // 公共接口table
            return apiCacheOld;
        } else if (tableId == '6') { // 规则集table
            return ruleSetCacheOld;
        } else if (tableId == '7') { // 模型库table
            return modelCacheOld;
        } else if (tableId == '8') { // 数据源table
            return dataSourceCacheOld;
        } else if (tableId == '9') { // 离线任务table
            return taskCacheOld;
        } else if (tableId == '10') { // 指标table
            return kpiCacheOld;
        } else if (tableId == '11') { // 场景table
            return folderCacheOld;
        } else if (tableId == '12') { // 参数组table
                return paramGroupCacheOld;
        } else if (tableId == "13") { // 接口组table
            return apiGroupCacheOld;
        } else if (tableId == '14') { // 指标组table
            return kpiGroupCacheOld;
        } else if (tableId == '15') { // 规则集组table
            return ruleSetGroupCacheOld;
        } else if (tableId == '16') { //  产品table
            return modelGroupCacheOld;
        } else {
            return;
        }
    },
    // 获取数据级resourceTypeId
    getTableResourceId: function (grantType) {
        if (grantType == '2') { // 元数据table
            return '4';
        } else if (grantType == '3') { // 模型table
            return '3';
        } else if (grantType == '4') { // 公共参数table
            return '5';
        } else if (grantType == '5') { // 公共接口table
            return '6';
        } else if (grantType == '6') { // 规则库table
            return '7';
        } else if (grantType == '7') { // 模型库table
            return '8';
        } else if (grantType == '8') { // 数据源table
            return '9';
        } else if (grantType == '9') { // 离线任务table
            return '10';
        } else if (grantType == '10') { // 指标table
            return '11';
        } else if (grantType == '11') { // 场景table
            return '12';
        } else if (grantType == '12') { // 参数组table
            return '13';
        } else if (grantType == '13') { // 接口组table
            return '14';
        } else if (grantType == '14') { // 指标组table
            return '15';
        } else if (grantType == '15') { // 规则集组table
            return '16';
        }else if (grantType == '16') { // 产品table
            return '17';
        } else {
            return;
        }
    },
    // 检查角色是否拥有全权
    checkIsGrantedAll: function (roleId) {
        if (roleId) {
            $.ajax({
                url: webpath + '/auth/all/isGranted',
                type: 'POST',
                dataType: "json",
                data: {"roleId": roleId},
                success: function (data) {
                    if (data.status === 0) {
                        if (data.data == 1) { // 全权
                            $('#allAuthCheck').text('取消全权');
                            $('#allAuthCheck').attr('all-check', '1');
                        } else {
                            $('#allAuthCheck').text('全权');
                            $('#allAuthCheck').attr('all-check', '0');
                        }
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        }
    },
    // 初始化角色下拉框, 根据角色id加载相应表格
    initRoleSelectors: function () {
        $.ajax({
            url: webpath + '/role/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.data.length > 0) {
                    var dataArr = data.data;
                    var htmlStr = '';
                    for (var i = 0; i < dataArr.length; i++) {
                        if (dataArr[i].roleName == '超级管理员') { // 授权角色过滤掉root
                            continue;
                        }
                        htmlStr += '<option role-id=\'' + dataArr[i].roleId + '\'>' + dataArr[i].roleName + '</option>';
                    }
                    $('#authorityHeader .roleSelector').empty().html(htmlStr);
                }
            },
            complete: function () {
                // 默认选中第一个role
                var firstRole = $('#authorityHeader .roleSelector option').first();
                firstRole.prop('checked', true);

                // 初始化角色下权限表
                var objStr = '?roleId=' + firstRole.attr('role-id');
                authorityMgrModal.initTables('0', objStr, firstRole.attr('role-id'), true);

                // 切换角色事件
                $('#authorityHeader .roleSelector').unbind().change(function () {
                    var checkedOption = $('#authorityHeader .roleSelector option:checked');
                    authorityMgrModal.initTables('0', '?roleId=' + checkedOption.attr('role-id'), checkedOption.attr('role-id'), true);
                });
            }
        });
    },
    // 初始化加载表格
    //      grantType: 0全部刷新 1菜单 2元数据 3模型;
    //      objStr: ?roleId=id (& 检索条件)
    //      ifClearCache: 是否刷新缓存
    initTables: function (grantType, objStr, roleId, ifClearCache) {
        if (ifClearCache) {
            authorityMgrModal.clearCache(grantType); // 清空缓存
        }
        if (grantType == '0') { // 全部
            initAuthorityTable(1, $('#menuTable'), menuTitle, '/auth/menu/view', objStr, false);
            initAuthorityTable(2, $('#dsTable'), dsTitle, '/auth/metaTable/view', objStr, true);
            initAuthorityTable(3, $('#ruleTable'), ruleTitle, '/auth/rule/view', objStr, true);
            initAuthorityTable(4, $('#paramTable'), paramTitle, '/auth/pub/variable/view', objStr, true);
            initAuthorityTable(5, $('#apiTable'), apiTitle, '/auth/pub/api/view', objStr, true);
            initAuthorityTable(6, $('#ruleSetTable'), ruleSetTitle, '/auth/pub/ruleSet/view', objStr, true);
            initAuthorityTable(7, $('#modelBaseTable'), modelTitle, '/auth/pub/modelBase/view', objStr, true);
            initAuthorityTable(8, $('#dataSourceTable'), dataSourceTitle, '/auth/dataSource/view', objStr, true);
            initAuthorityTable(9, $('#taskTable'), taskTitle, '/auth/task/view', objStr, true);
            initAuthorityTable(10, $('#kpiTable'), kpiTitle, '/auth/kpi/view', objStr, true);
            initAuthorityTable(11, $('#folderTable'), folderTitle, '/auth/folder/view', objStr, true);
            initAuthorityTable(12, $('#paramGroupTable'), paramGroupTitle, '/auth/pub/variableGroup/view', objStr, true);
            initAuthorityTable(13, $('#apiGroupTable'), apiGroupTitle, '/auth/pub/apiGroup/view', objStr, true);
            initAuthorityTable(14, $('#kpiGroupTable'), kpiGroupTitle, '/auth/kpiGroup/view', objStr, true);
            initAuthorityTable(15, $('#ruleSetGroupTable'), ruleSetGroupTitle, '/auth/pub/ruleSetGroup/view', objStr, true);
            initAuthorityTable(16, $('#modelGroupTable'), modelGroupTitle, '/auth/pub/modelGroup/view', objStr, true);
        } else if (grantType == '1') { // 菜单
            initAuthorityTable(1, $('#menuTable'), menuTitle, '/auth/menu/view', objStr, false);
        } else if (grantType == '2') { // 元数据
            initAuthorityTable(2, $('#dsTable'), dsTitle, '/auth/metaTable/view', objStr, true);
        } else if (grantType == '3') { // 模型
            initAuthorityTable(3, $('#ruleTable'), ruleTitle, '/auth/rule/view', objStr, true);
        } else if (grantType == '4') { // 公共参数
            initAuthorityTable(4, $('#paramTable'), paramTitle, '/auth/pub/variable/view', objStr, true);
        } else if (grantType == '5') { // 公共接口
            initAuthorityTable(5, $('#apiTable'), apiTitle, '/auth/pub/api/view', objStr, true);
        } else if (grantType == '6') { // 规则集
            initAuthorityTable(6, $('#ruleSetTable'), ruleSetTitle, '/auth/pub/ruleSet/view', objStr, true);
        } else if (grantType == '7') { // 模型库
            initAuthorityTable(7, $('#modelBaseTable'), modelTitle, '/auth/pub/modelBase/view', objStr, true);
        } else if (grantType == '8') { // 数据源
            initAuthorityTable(8, $('#dataSourceTable'), dataSourceTitle, '/auth/dataSource/view', objStr, true);
        } else if (grantType == '9') { // 离线任务
            initAuthorityTable(9, $('#taskTable'), taskTitle, '/auth/task/view', objStr, true);
        } else if (grantType == '10') { // 指标
            initAuthorityTable(10, $('#kpiTable'), kpiTitle, '/auth/kpi/view', objStr, true);
        } else if (grantType == '11') { // 场景
            initAuthorityTable(11, $('#folderTable'), folderTitle, '/auth/folder/view', objStr, true);
        } else if (grantType == '12') { // 参数组
            initAuthorityTable(12, $('#paramGroupTable'), paramGroupTitle, '/auth/pub/variableGroup/view', objStr, true);
        } else if (grantType == '13') { // 接口组
            initAuthorityTable(13, $('#apiGroupTable'), apiGroupTitle, '/auth/pub/apiGroup/view', objStr, true);
        } else if (grantType == '14') { // 指标组
            initAuthorityTable(14, $('#kpiGroupTable'), kpiGroupTitle, '/auth/kpiGroup/view', objStr, true);
        } else if (grantType == '15') { // 规则集组
            initAuthorityTable(15, $('#ruleSetGroupTable'), ruleSetGroupTitle, '/auth/pub/ruleSetGroup/view', objStr, true);
        } else if (grantType == '16') { // 产品
            initAuthorityTable(16, $('#modelGroupTable'), modelGroupTitle, '/auth/pub/modelGroup/view', objStr, true);
        }
        authorityMgrModal.checkIsGrantedAll(roleId); // 是否全权check
    },
    // 清除缓存数据
    clearCache: function (grantType) {
        if (grantType == '0') { // 全部
            menuCache = {};
            metaTableCache = {};
            ruleCache = {};
            paramCache = {};
            paramGroupCache = {};
            apiCache = {};
            apiGroupCache = {};
            ruleSetCache = {};
            ruleSetGroupCache = {};
            modelCache = {};
            modelGroupCache = {};
            dataSourceCache = {};
            taskCache = {};
            kpiCache = {};
            kpiGroupCache ={};
            folderCache = {};

            metaTableCacheOld = [];
            ruleCacheOld = [];
            paramCacheOld = [];
            paramGroupCacheOld = [];
            apiCacheOld = [];
            apiGroupCacheOld = [];
            ruleSetCacheOld = [];
            ruleSetGroupCacheOld = [];
            modelCacheOld = [];
            modelGroupCacheOld = [];
            dataSourceCacheOld = [];
            taskCacheOld = [];
            kpiCacheOld = [];
            kpiGroupCacheOld = [];
            folderCacheOld = [];
        } else if (grantType == '1') { // 菜单
            menuCache = {};
        } else if (grantType == '2') { // 元数据
            metaTableCache = {};
            metaTableCacheOld = [];
        } else if (grantType == '3') { // 模型
            ruleCache = {};
            ruleCacheOld = [];
        } else if (grantType == '4') { // 公共参数
            paramCache = {};
            paramCacheOld = [];
        } else if (grantType == '5') { // 公共接口
            apiCache = {};
            apiCacheOld = [];
        } else if (grantType == '6') { // 规则库
            ruleSetCache = {};
            ruleSetCacheOld = [];
        } else if (grantType == '7') { // 模型库
            modelCache = {};
            modelCacheOld = [];
        } else if (grantType == '8') { // 数据源
            dataSourceCache = {};
            dataSourceCacheOld = [];
        } else if (grantType == '9') { // 离线任务
            taskCache = {};
            taskCacheOld = [];
        } else if (grantType == '10') { // 指标
            kpiCache = {};
            kpiCacheOld = [];
        } else if (grantType == '11') { // 场景
            folderCache = {};
            folderCacheOld = [];
        } else if (grantType == '12') { // 参数组
            paramGroupCache = {};
            paramGroupCacheOld = [];
        } else if (grantType == '13') { // 接口组
            apiGroupCache = {};
            apiGroupCacheOld =[];
        } else if (grantType == '14') { // 指标组
            kpiGroupCache = {};
            kpiGroupCacheOld = [];
        } else if (grantType == '15') { // 规则集组
            ruleSetGroupCache = {};
            ruleSetGroupCacheOld = [];
        } else if (grantType == '16') { // 产品
            modelGroupCache = {};
            modelGroupCacheOld = [];
        }
    },
    // 授权保存
    saveGrant: function (grantType, roleId, obj, msgAlert) {
        if (roleId) {
            // 当前角色全权状态 & 验证是否全权选中 ---> 是否需要发送取消全权请求
            var roleAllChecked = ($('#allAuthCheck').attr('all-check') == '1') ? true : false;
            var isAllAuth = roleAllChecked ? '1' : '0';
            var url = '';
            if (grantType == '1') { // 菜单
                url = '/auth/menu/grant?roleId=' + roleId + '&isAllAuth=' + isAllAuth;
            } else if (grantType == '2') { // 元数据
                url = '/auth/metaTable/grant?roleId=' + roleId;
            } else if (grantType == '3') { // 模型
                url = '/auth/rule/grant?roleId=' + roleId;
            } else if (grantType == '4') { // 公共参数
                url = '/auth/pub/variable/grant?roleId=' + roleId;
            } else if (grantType == '5') { // 公共接口
                url = '/auth/pub/api/grant?roleId=' + roleId;
            } else if (grantType == '6') { // 规则库
                url = '/auth/pub/ruleSet/grant?roleId=' + roleId;
            } else if (grantType == '7') { // 模型库
                url = '/auth/pub/modelBase/grant?roleId=' + roleId;
            } else if (grantType == '8') { // 数据源
                url = '/auth/dataSource/grant?roleId=' + roleId;
            } else if (grantType == '9') { // 离线任务
                url = '/auth/task/grant?roleId=' + roleId;
            } else if (grantType == '10') { // 指标
                url = '/auth/kpi/grant?roleId=' + roleId;
            } else if (grantType == '11') { // 场景
                url = '/auth/folder/grant?roleId=' + roleId;
            } else if (grantType == '12') { // 参数组
                url = '/auth/pub/variableGroup/grant?roleId=' + roleId;
            } else if (grantType == '13') { // 接口组
                url = '/auth/pub/apiGroup/grant?roleId=' + roleId;
            } else if (grantType == '14') { // 指标组
                url = '/auth/kpiGroup/grant?roleId=' + roleId;
            } else if (grantType == '15') { // 规则集组
                url = '/auth/pub/ruleSetGroup/grant?roleId=' + roleId;
            } else if (grantType == '16') { // 产品
                url = '/auth/pub/modelGroup/grant?roleId=' + roleId;
            } else {
                return;
            }
            if (grantType != '1') {
                obj['isAllAuth'] = isAllAuth;
            }
            if (roleAllChecked) { // 角色全权
                var checkAllContainerArr = $("#authorityMgrContent table .checkAllContainer");
                var checkedAllBox = checkAllContainerArr.find("input[urlStr='*']:checked");
                if (checkedAllBox.length < checkAllContainerArr.length) { // 新:非全权
                    confirmAlert.show('当前角色为全权状态，继续保存将取消全权，是否继续？', function () {
                        // 取消全权状态
                        $.ajax({
                            url: webpath + '/auth/all/cancle',
                            type: 'POST',
                            dataType: "json",
                            data: {"roleId": roleId},
                            success: function (data) {
                                if (data.status !== 0) {
                                    failedMessager.show(data.msg);
                                }
                            }
                        });
                        // 授权
                        authorityMgrModal.saveGrantSubmit(url, obj, grantType, roleId, msgAlert);
                    });
                } else {
                    authorityMgrModal.saveGrantSubmit(url, obj, grantType, roleId, msgAlert);
                }
            } else { // 直接授权
                authorityMgrModal.saveGrantSubmit(url, obj, grantType, roleId, msgAlert);
            }
        }
    },
    // 授权保存确认 msgAlert 0全页保存 不一一弹出提示信息 1单表保存
    saveGrantSubmit: function (url, obj, grantType, roleId, msgAlert) {
        if (msgAlert === 1) { // 单表保存弹出提示信息
            $.ajax({
                url: webpath + url,
                type: 'POST',
                dataType: "json",
                contentType: 'application/json',
                data: JSON.stringify(obj),
                beforeSend: function () {
                    loading.show();
                },
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('授权保存成功！');
                        authorityMgrModal.initTables(grantType, '?roleId=' + roleId, roleId, true); // 刷新对应权限表格
                    } else {
                        failedMessager.show(data.msg);
                    }
                },
                complete: function () {
                    loading.hide();
                }
            });
        } else { // 全页保存
            $.ajax({
                url: webpath + url,
                type: 'POST',
                dataType: "json",
                contentType: 'application/json',
                data: JSON.stringify(obj),
                success: function (data) {
                    if (data.status === 0) {
                        authorityMgrModal.initTables(grantType, '?roleId=' + roleId, roleId, true); // 刷新对应权限表格
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        }
    },
    // 获取授权信息数据
    getGrantObj: function (grantType) {
        var dataArr = []; // 更新
        var deleteArr = []; // 需删除
        var insertArr = []; // 新增

        var paramFlag = false;
        var updateEntityArr = []; // 更新entityIds
        var delEntityArr = []; // 需删除entityIds
        var insertEntityArr = []; // 新增entityIds

        if (grantType == null || grantType == '') {
            return;
        }
        // 菜单授权
        if (grantType == '1') {
            var ifAllCheck = $('#menuTable .checkAllContainer input').prop('checked');
            if (ifAllCheck) { // 全选
                dataArr.push({"resourceId": "*", "resourceTypeId": "1", "resourceExpression": "*"}); // 菜单级
                dataArr.push({"resourceId": "*", "resourceTypeId": "2", "resourceExpression": "*"}); // 按钮级
            } else {
                var allCheckbox = $('#menuTable tbody .resourceCheckBox:checked');
                if (allCheckbox.length === 0) { // 全部未选
                    return dataArr;
                } else { // 部分选中
                    for (var key in menuCache) {
                        var resourceArr = menuCache[key];
                        if (resourceArr.length > 0) {
                            var menuUrl = $("#menuTable tbody .resourceCheckBoxContainer[resourceId='" + key + "']").attr('resourceUrl');
                            dataArr.push({"resourceId": key, "resourceTypeId": "1", "resourceExpression": menuUrl}); // 菜单级
                            for (var i = 0; i < resourceArr.length; i++) {
                                dataArr.push({
                                    "resourceId": resourceArr[i].resourceId,
                                    "resourceTypeId": resourceArr[i].resourceTypeId,
                                    "resourceExpression": resourceArr[i].resourceExpression
                                }); // 按钮级
                            }
                        }
                    }
                }
            }
            return dataArr;
        }
        // 数据级授权
        else {
            if (grantType === '4') { // 公共参数整理entityId数组
                paramFlag = true;
            }
            var obj = {};
            var tableCache = authorityMgrModal.getTableCache(grantType);
            var tableCacheOld = authorityMgrModal.getTableOldCache(grantType);
            for (var key in tableCache) {
                if (tableCache[key] == '') {
                    if (tableCacheOld.length > 0) {
                        if (tableCacheOld.indexOf(key) != -1) { // oldCache有 cache为空 ---> 删掉
                            deleteArr.push(key);
                            if (paramFlag && entityCache.hasOwnProperty(key)) {
                                delEntityArr.push(entityCache[key]);
                            }
                        }
                    }
                    continue;
                } else {
                    var resourceTypeId = authorityMgrModal.getTableResourceId(grantType);
                    if (tableCacheOld.length > 0) {
                        if (tableCacheOld.indexOf(key) == -1) { // oldCache无 cache有且不空 ---> 新增
                            insertArr.push({
                                "resourceId": key,
                                "resourceTypeId": resourceTypeId,
                                "resourceExpression": tableCache[key]
                            });
                            if (paramFlag && entityCache.hasOwnProperty(key)) {
                                insertEntityArr.push(entityCache[key]);
                            }
                        } else {
                            dataArr.push({
                                "resourceId": key,
                                "resourceTypeId": resourceTypeId,
                                "resourceExpression": tableCache[key]
                            });
                            if (paramFlag && entityCache.hasOwnProperty(key)) {
                                updateEntityArr.push(entityCache[key]);
                            }
                        }
                    } else {
                        insertArr.push({
                            "resourceId": key,
                            "resourceTypeId": resourceTypeId,
                            "resourceExpression": tableCache[key]
                        });
                        if (paramFlag && entityCache.hasOwnProperty(key)) {
                            insertEntityArr.push(entityCache[key]);
                        }
                    }
                }
            }
            obj["roleId"] = $('#authorityHeader .roleSelector option:checked').attr('role-id');
            obj["needDelAuthorities"] = deleteArr;
            obj["needInsertAuthorities"] = insertArr;
            obj["needUpdateAuthorities"] = dataArr;
            if (paramFlag) {
                obj["needUpdateEntityAuths"] = updateEntityArr;
                obj["needDelEntityAuths"] = delEntityArr;
                obj["needInsertEntityAuths"] = insertEntityArr;
            }

            return obj;
        }
    },
}

/**
 * 菜单级缓存
 */
var menuLevelCacheModal = {
    // 菜单级 初始化数据缓存
    initCache: function (dataArr, cache) {
        for (var i = 0; i < dataArr.length; i++) { // 行数据数组
            var rowResourceId = dataArr[i].resourceId;
            var resourcesArr = dataArr[i].resources;
            var cacheArr = [];
            for (var j = 0; j < resourcesArr.length; j++) { // 操作数组
                if (resourcesArr[j].choosed) { // 操作被选中
                    cacheArr.push({
                        "resourceId": resourcesArr[j].resourceId,
                        "resourceTypeId": resourcesArr[j].resourceTypeId,
                        "resourceExpression": resourcesArr[j].resourceUrl
                    });
                }
            }
            cache[rowResourceId] = cacheArr;
        }
    },
    // 表格刷新时回显check
    echoCheck: function () {
        var totalNum = $('#menuTable tbody input').length;
        var checkedNum = $('#menuTable tbody input:checked').length;
        if (totalNum === checkedNum) {
            $('#menuTable .checkAllContainer input').prop('checked', true);
        } else {
            $('#menuTable .checkAllContainer input').prop('checked', false);
        }
    },
    // 菜单级 更新缓存
    updateCache: function (menuResourceId, resourceId, resourceTypeId, url, cache, isCheck) {
        var cacheArr = cache[menuResourceId];
        var index = -1;
        for (var i = 0; i < cacheArr.length; i++) {
            if (cacheArr[i].resourceId == resourceId) {
                index = i;
                break;
            }
        }
        if (isCheck) { // 选中
            if (index == -1) { // 不存在
                cacheArr.push({
                    "resourceId": resourceId,
                    "resourceTypeId": resourceTypeId,
                    "resourceExpression": url
                });
                cache[menuResourceId] = cacheArr;
            }
        } else { // 取消选中
            if (index != -1) { // 存在
                cacheArr.splice(index, 1);
                cache[menuResourceId] = cacheArr;
            }
        }
    },
    // 资源checkbox状态变更后: 全选check
    checkAllCheckbox: function (that) {
        var isCheck = that.prop('checked');
        var parentContainer = that.parents('.resourceCheckBoxContainer'); // 数据行 父container
        var totalNum = parentContainer.find('.resourceCheckBox').length; // 行内checkbox总数
        var checkedNum = parentContainer.find('.resourceCheckBox:checked').length; // 行内选中的checkbox数
        // 行内横向check
        if (isCheck) { // 选中检查是否其余checkbox是否勾选, 是则全选
            if (totalNum === checkedNum) {
                parentContainer.find('.acrossCheckbox').prop('checked', true);
            }
        } else { // 取消选中检查其余checkbox是否都未勾选, 是则取消全选
            if (checkedNum < totalNum) {
                parentContainer.find('.acrossCheckbox').prop('checked', false);
            }
        }
        // 表格全选check
        var tableAllCheckbox = that.parents('table').find('.checkAllContainer input');
        var dataRowArr = that.parents('table').find(".resourceCheckBoxContainer:not('.checkAllContainer')"); // 表内所有数据行
        var dataCheckBox = dataRowArr.find('input').length;
        var dataChecked = dataRowArr.find('input:checked').length;
        if (isCheck) {
            if (dataCheckBox === dataChecked) {
                tableAllCheckbox.prop('checked', true);
            }
        } else {
            if (dataChecked < dataCheckBox) {
                tableAllCheckbox.prop('checked', false);
            }
        }
    },
    // 横向全选
    acrossCheck: function (that) {
        // 横向全部选中
        var flag = (that.prop('checked')) ? true : false;
        that.parents('.resourceCheckBoxContainer').find('input').prop('checked', flag);
        // 更新缓存
        var resourceArr = that.parents('.resourceCheckBoxContainer').find('.resourceCheckBox');
        for (var i = 0; i < resourceArr.length; i++) {
            var menuResourceId = $(resourceArr[i]).parents('.resourceCheckBoxContainer').attr('resourceId');
            var resourceBox = $(resourceArr[i]);
            menuLevelCacheModal.updateCache(
                menuResourceId,
                resourceBox.attr('resourceId'),
                resourceBox.attr('resourceTypeId'),
                resourceBox.attr('urlStr'),
                menuCache,
                flag);
        }
        // 表头全选状态检查
        var allCheckbox = that.parents('table').find('.checkAllContainer input');
        var dataRows = that.parents('table').find(".resourceCheckBoxContainer:not('.checkAllContainer')");
        var totalNum = dataRows.find('input').length;
        var checkedNum = dataRows.find('input:checked').length;
        if (flag) {
            if (totalNum === checkedNum) {
                allCheckbox.prop('checked', true);
            }
        } else {
            if (checkedNum < totalNum) {
                allCheckbox.prop('checked', false);
            }
        }
    },
    // 全选
    checkAll: function (that) {
        // 全部状态更新
        var flag = that.prop('checked');
        $('#menuTable input').prop('checked', flag);
        var rowsArr = $("#menuTable .resourceCheckBoxContainer:not('.checkAllContainer')");
        if (rowsArr.length > 0) {
            for (var i = 0; i < rowsArr.length; i++) {
                var menuId = $(rowsArr[i]).attr('resourceId');
                var resourceArr = $(rowsArr[i]).find('.resourceCheckBox');
                for (var j = 0; j < resourceArr.length; j++) {
                    menuLevelCacheModal.updateCache(
                        menuId,
                        $(resourceArr[j]).attr('resourceId'),
                        $(resourceArr[j]).attr('resourceTypeId'),
                        $(resourceArr[j]).attr('urlStr'),
                        menuCache,
                        flag);
                }
            }
        }
    },
}

/**
 * 数据级缓存
 */
var dataLevelCacheModal = {
    // 数据级 初始化表内当前页数据缓存
    initCache: function (dataArr, cache, cacheOld, tableId) {
        for (var i = 0; i < dataArr.length; i++) { // 行数据数组
            var rowResourceId = dataArr[i].resourceId;
            if (rowResourceId in cache) { // 渲染表格时缓存中 如果已有记录则不做操作; 无则进行数据缓存新增
                continue;
            }
            if (tableId == '4') { // 如果是公共参数
                if (dataArr[i].typeId === '3') { // 如果是对象类型则记录entityId
                    entityCache[rowResourceId] = dataArr[i].entityId;
                }
            }
            var resourcesArr = dataArr[i].resources;
            var cacheArr = [];
            var hasCheck = false;
            for (var j = 0; j < resourcesArr.length; j++) { // 操作数组
                if (resourcesArr[j].choosed) { // 操作被选中
                    cacheArr.push(resourcesArr[j].resourceUrl);
                    if (!hasCheck) { // cacheOld记录该角色数据库内权限的信息, 只要有一个选中项即记录在cacheOld中
                        hasCheck = true;
                        cacheOld.push(rowResourceId);
                    }
                }
            }
            cache[rowResourceId] = cacheArr.join(',');
        }
    },
    // 数据级 更新缓存
    updateCache: function (resourceId, url, cache, isCheck) {
        var urlStr = cache[resourceId];
        if (isCheck) { // 选中
            if (urlStr == '') {
                cache[resourceId] = url;
            } else {
                var urlStrArr = urlStr.split(',');
                if (urlStrArr.indexOf(url) == -1) { // 数据组无此项
                    urlStrArr.push(url);
                }
                cache[resourceId] = urlStrArr.join(',');
            }
        } else { // 取消选中
            if (urlStr != '') {
                var urlStrArr = urlStr.split(',');
                for (var i = 0; i < urlStrArr.length; i++) {
                    if (urlStrArr[i] == url) {
                        urlStrArr.splice(i, 1);
                        cache[resourceId] = urlStrArr.join(',');
                        break;
                    }
                }
            }
        }
    },
    // 资源checkbox状态变更后: 全选check
    checkAllCheckbox: function (that, isTitle) {
        var isCheck = that.prop('checked');
        var parentContainer = that.parents('.resourceCheckBoxContainer'); // 数据行 父container
        var dataRows = that.parents('table').find(".resourceCheckBoxContainer:not('.checkAllContainer')"); // 当页所有数据行
        var totalNum; // 行内checkbox总数
        var checkedNum; // 行内选中的checkbox数
        if (isTitle) {
            // 行内check
            totalNum = parentContainer.find(".verticalCheckbox:not('.acrossCheckbox')").length;
            checkedNum = parentContainer.find(".verticalCheckbox:not('.acrossCheckbox'):checked").length;
        } else {
            totalNum = parentContainer.find('.resourceCheckBox').length;
            checkedNum = parentContainer.find('.resourceCheckBox:checked').length;
            // 纵向check
            var url = that.attr('urlStr');
            var checkedArr = dataRows.find(".resourceCheckBox[urlStr='" + url + "']:checked");
            var verticalInput = that.parents('table').find(".checkAllContainer input[urlStr='" + url + "']");
            if (isCheck) {
                if (dataRows.length === checkedArr.length) {
                    verticalInput.prop('checked', true);
                }
            } else {
                if (checkedArr.length < dataRows.length) {
                    verticalInput.prop('checked', false);
                }
            }
        }

        // 行内横向check
        if (isCheck) { // 选中检查是否其余checkbox是否勾选, 是则全选
            if (totalNum === checkedNum) {
                parentContainer.find('.acrossCheckbox').prop('checked', true);
            }
        } else { // 取消选中检查其余checkbox是否都未勾选, 是则取消全选
            if (checkedNum < totalNum) {
                parentContainer.find('.acrossCheckbox').prop('checked', false);
            }
        }
    },
    // 数据级 表格回调函数 回显缓存数据; 表头选中状态check
    echoCacheCheckbox: function (container, cache) {
        // 缓存数据回显
        for (var key in cache) {
            if (cache[key] != '') {
                var resourceUrlArr = cache[key].split(',');
                var parent = container.find(".resourceCheckBoxContainer[resourceId='" + key + "']");
                for (var i = 0; i < resourceUrlArr.length; i++) {
                    parent.find(".resourceCheckBox[urlStr='" + resourceUrlArr[i] + "']").prop('checked', true);
                }
                // 行全选状态check
                var totalNum = parent.find('.resourceCheckBox').length;
                if (totalNum === resourceUrlArr.length) {
                    parent.find('.acrossCheckbox').prop('checked', true);
                }
            }
        }
        // 表头选中状态check
        container.find(".checkAllContainer input").prop('checked', false); // 先清除表头所有input选中状态
        var dataRows = container.find(".resourceCheckBoxContainer:not(.checkAllContainer)"); // 所有数据行

        // 特殊情况: 全部选中check
        var dataAcrossCheckbox = dataRows.find('.acrossCheckbox').length;
        var dataAcrossCheckbox_checked = dataRows.find('.acrossCheckbox:checked').length;
        if (dataAcrossCheckbox !== 0 &&
            dataAcrossCheckbox_checked !== 0 &&
            dataAcrossCheckbox === dataAcrossCheckbox_checked) { // 全部全选
            container.find(".checkAllContainer input").prop('checked', true);
            return;
        }

        // 列全选情况check
        var resourceThArr = container.find(".checkAllContainer input:not(.acrossCheckbox)");
        for (var j = 0; j < resourceThArr.length; j++) {
            var thUrl = $(resourceThArr[j]).attr('urlStr');
            var arrChecked = dataRows.find(".resourceCheckBox[urlStr='" + thUrl + "']:checked").length;
            if (dataRows.length !== 0 &&
                arrChecked !== 0 &&
                dataRows.length === arrChecked) {
                $(resourceThArr[j]).prop('checked', true);
            } else {
                $(resourceThArr[j]).prop('checked', false);
            }
        }
    },
    // 数据级 横向全选
    acrossCheck: function (that) {
        // 横向全部选中
        var flag = that.prop('checked');
        that.parents('.resourceCheckBoxContainer').find('input').prop('checked', flag);

        // 更新缓存
        var isTitle = that.hasClass('verticalCheckbox'); // 是否是表头全选框
        var cache = authorityMgrModal.getTableCache(that.parents('table').attr('table-id'));
        var rowArr = that.parents('table').find(".resourceCheckBoxContainer:not('.checkAllContainer')"); // 该页所有数据行

        if (isTitle) { // 表头th全选框(全表全选) 缓存更新
            that.parents('table').find('input').prop('checked', flag); // 全页全部checkbox更新选中状态
            var urlStr = [];
            if (flag) {
                // 拼接资源全部urlStr
                var headArr = that.parents('table').find(".checkAllContainer .verticalCheckbox:not('.acrossCheckbox')"); // 表头除全选框
                for (var i = 0; i < headArr.length; i++) {
                    urlStr.push($(headArr[i]).attr('urlStr'));
                }
            }
            // 全页数据缓存更新
            for (var i = 0; i < rowArr.length; i++) {
                var dataId = $(rowArr[i]).attr('resourceId');
                cache[dataId] = urlStr.join(',');
            }
        } else { // 资源td全选框 缓存更新
            var resourceArr = that.parents('.resourceCheckBoxContainer').find('.resourceCheckBox');
            for (var i = 0; i < resourceArr.length; i++) {
                var resourceBox = $(resourceArr[i]);
                dataLevelCacheModal.updateCache(resourceBox.attr('resourceId'), resourceBox.attr('urlStr'), cache, flag);
            }
        }

        // 纵向表头全选框状态check
        var headArrCheck = that.parents('table').find(".checkAllContainer input"); // 全部表头全选
        for (var i = 0; i < headArrCheck.length; i++) {
            var headStr = $(headArrCheck[i]).attr('urlStr');
            var urlChecked = that.parents('table').find("tbody input[urlStr='" + headStr + "']:checked").length;
            if (flag) { // 选中
                if (urlChecked === rowArr.length) {
                    $(headArrCheck[i]).prop('checked', true);
                }
            } else {
                if (urlChecked < rowArr.length) {
                    $(headArrCheck[i]).prop('checked', false);
                }
            }
        }
    },
    // 数据级 纵向全选
    verticalCheck: function (that) {
        // 特殊: 全选框的事件在横向选中function中处理, 这里不再次处理了
        if (that.hasClass('acrossCheckbox')) {
            return;
        }
        var urlStr = that.attr('urlStr');
        var flag = that.prop('checked');
        var cache = authorityMgrModal.getTableCache(that.parents('table').attr('table-id'));
        var allChildren = that.parents('table').find("td .resourceCheckBoxContainer input[urlStr='" + urlStr + "']");
        // 纵向checkbox选中状态更新
        allChildren.prop('checked', flag);
        // 全页列数据缓存更新
        var rowArr = that.parents('table').find(".resourceCheckBoxContainer:not('.checkAllContainer')"); // 全页数据
        for (var i = 0; i < rowArr.length; i++) {
            var resourceId = $(rowArr[i]).attr('resourceId'); // 行数据id
            dataLevelCacheModal.updateCache(resourceId, urlStr, cache, flag);
            // 横向全选框状态check
            var rowCheckAll = $(rowArr[i]).find('.acrossCheckbox');
            var resourceBoxTotal = $(rowArr[i]).find('.resourceCheckBox').length;
            var resourceBoxChecked = $(rowArr[i]).find('.resourceCheckBox:checked').length;
            if (flag) { // 选中
                if (resourceBoxTotal === resourceBoxChecked) {
                    rowCheckAll.prop('checked', true);
                }
            } else { // 未选中
                if (resourceBoxChecked < resourceBoxTotal) {
                    rowCheckAll.prop('checked', false);
                }
            }
        }
    },
}

// 初始化table
function initAuthorityTable(tableId, container, cols, url, objStr, paging) {
    var isDataLevel = container.hasClass('dataLevelTable'); // 是否是数据级
    container.width('100%').dataTable({
        "processing": true,
        "searching": false,
        "ordering": false,
        "destroy": true,
        "info": true,
        "paging": paging,
        "serverSide": paging,
        "pageLength": 10,
        "bLengthChange": false,
        "pagingType": "full_numbers",
        "columns": cols,
        ajax: {
            url: webpath + url + objStr,
            "type": 'POST',
            dataSrc: function (data) { // 从服务器接收的数据
                if (data.status === 0) {
                    // 分页设置
                    if (paging) { // 需要把分页的参数处理为顶级的属性
                        data.recordsTotal = data.data.permits.recordsTotal;
                        data.recordsFiltered = data.data.permits.recordsFiltered;
                    }

                    // 数据缓存更新/初始化
                    var dataArr = data.data.permits.data;
                    var cache = authorityMgrModal.getTableCache(tableId);
                    if (isDataLevel) { // 数据级
                        var cacheOld = authorityMgrModal.getTableOldCache(tableId);
                        dataLevelCacheModal.initCache(dataArr, cache, cacheOld, tableId);
                    } else { // 菜单级
                        menuLevelCacheModal.initCache(dataArr, cache);
                    }

                    return data.data.permits.data; // 返回表格数据
                } else {
                    failedMessager.show(data.msg);
                    return [];
                }
            }
        },
        "fnDrawCallback": function (oSettings, json) {
            // $("table:eq(0) th").css("background-color", "#f6f7fb");

            var cache = authorityMgrModal.getTableCache(tableId);
            /** 数据级 **/
            if (isDataLevel) {
                // 勾选项回显
                dataLevelCacheModal.echoCacheCheckbox(container, cache);
                // 资源操作checkbox事件绑定 checked变更时更新缓存
                container.find('.resourceCheckBox').on('click', function () {
                    dataLevelCacheModal.updateCache($(this).attr('resourceId'), $(this).attr('urlStr'), cache, $(this).prop('checked'));
                    dataLevelCacheModal.checkAllCheckbox($(this), false); // 全选状态check
                });
                // 横向全选事件绑定
                container.find('.acrossCheckbox').on('click', function () {
                    dataLevelCacheModal.acrossCheck($(this));
                });
                // 纵向全选事件绑定
                container.find('.verticalCheckbox').on('click', function () {
                    dataLevelCacheModal.verticalCheck($(this));
                    dataLevelCacheModal.checkAllCheckbox($(this), true);
                });
            }
            /** 菜单级 **/
            else {
                // 表格刷新数据回显时全选框状态check
                menuLevelCacheModal.echoCheck();
                // 资源操作checkbox事件绑定 checked变更时更新缓存
                container.find('.resourceCheckBox').on('click', function () {
                    menuLevelCacheModal.updateCache(
                        $(this).parents('.resourceCheckBoxContainer').attr('resourceId'),
                        $(this).attr('resourceId'),
                        $(this).attr('resourceTypeId'),
                        $(this).attr('urlStr'),
                        cache,
                        $(this).prop('checked')
                    );
                    menuLevelCacheModal.checkAllCheckbox($(this)); // 全选状态check
                });
                // 横向全选事件绑定
                container.find('.acrossCheckbox').on('click', function () {
                    menuLevelCacheModal.acrossCheck($(this));
                });
                // 纵向全选事件绑定
                container.find('.verticalCheckbox').on('click', function () {
                    menuLevelCacheModal.checkAll($(this));
                });
            }
        },
        "headerCallback": function (thead, data, start, end, display) {
            if (isDataLevel) {
                if (!data || data.length === 0) { //无数据 操作表头不可选中
                    container.find('.checkAllContainer').find('.verticalCheckbox').prop('checked', false);
                    container.find('.checkAllContainer').find('.verticalCheckbox').attr('disabled', true);
                } else {
                    container.find('.checkAllContainer').find('.verticalCheckbox').removeAttr('disabled');
                }
            }
        }
    });
}

// 菜单title
var menuTitle = [
    {"title": "资源名称", "data": "resourceName"},
    {
        "title": "资源类型", "data": "resourceTypeId", "render": function (data) {
            switch ($.trim(data)) {
                case '0':
                    return '全部资源';
                case '1':
                    return '菜单';
                case '2':
                    return '按钮';
                case '3':
                    return '模型';
                case '4':
                    return '元数据';
                default:
                    return '--';
            }
        }
    },
    {
        "title": function () {
            var htmlStr = '';
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": "",
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var checkAllFlag = 0;
            var htmlStr = '<div class="resourceCheckBoxContai  ner" resourceId=\'' + row.resourceId + '\' resourceUrl=\'' + row.resourceUrl + '\'>';
            for (var i = 0; i < resourcesArr.length; i++) {
                var checkStr = '';
                if (resourcesArr[i].choosed) {
                    checkAllFlag++;
                    checkStr = 'checked';
                }
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + resourcesArr[i].resourceId + '\' resourceTypeId=\'' + resourcesArr[i].resourceTypeId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' class="resourceCheckBox" type="checkbox" ' + checkStr + '>';
                htmlStr += resourcesArr[i].resourceName
                htmlStr += '</label></div>';
            }
            var checkAllStr = (checkAllFlag === resourcesArr.length) ? 'checked' : '';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox" ' + checkAllStr + '>全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];

// 元数据title
var dsTitle = [
    {"title": "场景名称", "data": "folderName"},
    {"title": "库名", "data": "dbAlias"},
    {"title": "表名", "data": "tableName"},
    {
        "title": function () {
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="/metaTable/view" class="verticalCheckbox" type="checkbox">查看</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/metaTable/add" class="verticalCheckbox " type="checkbox">添加</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/metaTable/update" class="verticalCheckbox" type="checkbox">修改</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/metaTable/delete" class="verticalCheckbox" type="checkbox">删除</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": null,
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var rowResourceId = row.resourceId;
            var htmlStr = '<div resourceId=\'' + rowResourceId + '\' class="resourceCheckBoxContainer">';
            for (var i = 0; i < resourcesArr.length; i++) {
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + rowResourceId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' class="resourceCheckBox" type="checkbox">';
                htmlStr += resourcesArr[i].resourceName;
                htmlStr += '</label></div>';
            }
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];

// 模型title
var ruleTitle = [
    {"title": "场景名称", "data": "folderName"},
    {"title": "模型名称", "data": "moduleName"},
    {
        "title": "模型类型", "data": "ruleType", "render": function (data) {
            switch ($.trim(data)) {
                case '0':
                    return '评分模型';
                case '1':
                    return '规则模型';
                default:
                    return '--';
            }
        }
    },
    {
        "title": function () {
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="/rule/view" class="verticalCheckbox" type="checkbox">查看</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/rule/update" class="verticalCheckbox" type="checkbox">基础信息修改</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/rule/delete" class="verticalCheckbox" type="checkbox">删除</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/rule/stage" class="verticalCheckbox" type="checkbox">暂存</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/rule/commit" class="verticalCheckbox" type="checkbox">提交</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/rule/publish" class="verticalCheckbox" type="checkbox">发布</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/rule/enable" class="verticalCheckbox" type="checkbox">启用/停用</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/rule/test" class="verticalCheckbox" type="checkbox">测试</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/rule/trial" class="verticalCheckbox" type="checkbox">试算</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/rule/clone" class="verticalCheckbox" type="checkbox">克隆</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": "",
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var rowResourceId = row.resourceId;
            var htmlStr = '<div resourceId=\'' + rowResourceId + '\' class="resourceCheckBoxContainer">';
            for (var i = 0; i < resourcesArr.length; i++) {
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + row.resourceId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' class="resourceCheckBox" type="checkbox">';
                htmlStr += resourcesArr[i].resourceName
                htmlStr += '</label></div>';
            }
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];

// 产品title
var modelGroupTitle = [
    {"title": "产品名称", "data": "modelGroupName"},
    {
        "title": function () {
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/modelGroup/view" class="verticalCheckbox" type="checkbox">查看</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/modelGroup/update" class="verticalCheckbox" type="checkbox">修改</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/modelGroup/delete" class="verticalCheckbox" type="checkbox">删除</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": "",
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var rowResourceId = row.resourceId;
            var htmlStr = '<div resourceId=\'' + rowResourceId + '\' class="resourceCheckBoxContainer">';
            for (var i = 0; i < resourcesArr.length; i++) {
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + row.resourceId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' class="resourceCheckBox" type="checkbox">';
                htmlStr += resourcesArr[i].resourceName
                htmlStr += '</label></div>';
            }
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];

// 公共参数title
var paramTitle = [
    {"title": "参数组", "data": "variableGroupName"},
    {"title": "变量别名", "data": "variableAlias"},
    {"title": "变量编码", "data": "variableCode"},
    {"title": "变量类型", "data": "typeDesc"},
    {
        "title": function () {
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/variable/view" class="verticalCheckbox" type="checkbox">查看</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/variable/update" class="verticalCheckbox" type="checkbox">修改</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/variable/delete" class="verticalCheckbox" type="checkbox">删除</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": null,
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var rowResourceId = row.resourceId;
            var kindId = row.kindId;
            var entityId = row.entityId;
            var htmlStr = '<div resourceId=\'' + rowResourceId + '\' class="resourceCheckBoxContainer">';
            for (var i = 0; i < resourcesArr.length; i++) {
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + rowResourceId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' kindId=\'' + kindId + '\' entityId=\'' + entityId + '\' class="resourceCheckBox" type="checkbox">';
                htmlStr += resourcesArr[i].resourceName;
                htmlStr += '</label></div>';
            }
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];


// 公共参数组title
var paramGroupTitle = [
    {"title": "参数组", "data": "variableGroupName"},
    {
        "title": function () {
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/variableGroup/view" class="verticalCheckbox" type="checkbox">查看</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/variableGroup/update" class="verticalCheckbox" type="checkbox">修改</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/variableGroup/delete" class="verticalCheckbox" type="checkbox">删除</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": null,
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var rowResourceId = row.resourceId;
            var kindId = row.kindId;
            var entityId = row.entityId;
            var htmlStr = '<div resourceId=\'' + rowResourceId + '\' class="resourceCheckBoxContainer">';
            for (var i = 0; i < resourcesArr.length; i++) {
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + rowResourceId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' kindId=\'' + kindId + '\' entityId=\'' + entityId + '\' class="resourceCheckBox" type="checkbox">';
                htmlStr += resourcesArr[i].resourceName;
                htmlStr += '</label></div>';
            }
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];

// 公共接口title
var apiTitle = [
    {"title": "接口名称", "data": "apiName"},
    {"title": "接口描述", "data": "apiDesc"},
    {"title": "接口类型", "data": "apiType"},
    {
        "title": function () {
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/api/view" class="verticalCheckbox" type="checkbox">查看</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/api/update" class="verticalCheckbox" type="checkbox">修改</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/api/delete" class="verticalCheckbox" type="checkbox">删除</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": null,
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var rowResourceId = row.resourceId;
            var htmlStr = '<div resourceId=\'' + rowResourceId + '\' class="resourceCheckBoxContainer">';
            for (var i = 0; i < resourcesArr.length; i++) {
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + rowResourceId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' class="resourceCheckBox" type="checkbox">';
                htmlStr += resourcesArr[i].resourceName;
                htmlStr += '</label></div>';
            }
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];

// 公共接口组title
var apiGroupTitle = [
    {"title": "接口组名称", "data": "apiGroupName"},
    {
        "title": function () {
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/apiGroup/view" class="verticalCheckbox" type="checkbox">查看</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/apiGroup/update" class="verticalCheckbox" type="checkbox">修改</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/apiGroup/delete" class="verticalCheckbox" type="checkbox">删除</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": null,
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var rowResourceId = row.resourceId;
            var htmlStr = '<div resourceId=\'' + rowResourceId + '\' class="resourceCheckBoxContainer">';
            for (var i = 0; i < resourcesArr.length; i++) {
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + rowResourceId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' class="resourceCheckBox" type="checkbox">';
                htmlStr += resourcesArr[i].resourceName;
                htmlStr += '</label></div>';
            }
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];

// 规则库title
var ruleSetTitle = [
    {"title": "规则集组名称", "data": "ruleSetGroupName"},
    {"title": "规则集名称", "data": "ruleSetName"},
    {
        "title": function () {
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/ruleSet/view" class="verticalCheckbox" type="checkbox">查看</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/ruleSet/version/add" class="verticalCheckbox" type="checkbox">版本添加</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/ruleSet/update" class="verticalCheckbox" type="checkbox">修改</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/ruleSet/delete" class="verticalCheckbox" type="checkbox">删除</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/ruleSet/version/enable" class="verticalCheckbox" type="checkbox">启用/停用</label></div>';
            // htmlStr += '<div class="checkbox"><label><input urlStr="/pub/ruleSet" class="verticalCheckbox" type="checkbox">规则库</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": "",
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var rowResourceId = row.resourceId;
            var htmlStr = '<div resourceId=\'' + rowResourceId + '\' class="resourceCheckBoxContainer">';
            for (var i = 0; i < resourcesArr.length; i++) {
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + row.resourceId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' class="resourceCheckBox" type="checkbox">';
                htmlStr += resourcesArr[i].resourceName
                htmlStr += '</label></div>';
            }
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];

// 规则集组title
var ruleSetGroupTitle = [
    {"title": "规则集组名称", "data": "ruleSetGroupName"},
    {
        "title": function () {
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/ruleSetGroup/view" class="verticalCheckbox" type="checkbox">查看</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/ruleSetGroup/update" class="verticalCheckbox" type="checkbox">修改</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/ruleSetGroup/delete" class="verticalCheckbox" type="checkbox">删除</label></div>';
            // htmlStr += '<div class="checkbox"><label><input urlStr="/pub/ruleSetGroup/version/enable" class="verticalCheckbox" type="checkbox">启用/停用</label></div>';
            // htmlStr += '<div class="checkbox"><label><input urlStr="/pub/ruleSet" class="verticalCheckbox" type="checkbox">规则库</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": "",
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var rowResourceId = row.resourceId;
            var htmlStr = '<div resourceId=\'' + rowResourceId + '\' class="resourceCheckBoxContainer">';
            for (var i = 0; i < resourcesArr.length; i++) {
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + row.resourceId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' class="resourceCheckBox" type="checkbox">';
                htmlStr += resourcesArr[i].resourceName
                htmlStr += '</label></div>';
            }
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];

// 模型库title
var modelTitle = [
    {"title": "模型组名称", "data": "modelGroupName"},
    {"title": "模型名称", "data": "moduleName"},
    {
        "title": "模型类型", "data": "ruleType", "render": function (data) {
            switch ($.trim(data)) {
                case '0':
                    return '评分模型';
                case '1':
                    return '规则模型';
                default:
                    return '--';
            }
        }
    },
    {
        "title": function () {
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/rule/view" class="verticalCheckbox" type="checkbox">查看</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/modelBase/update" class="verticalCheckbox" type="checkbox">基础信息修改</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/rule/delete" class="verticalCheckbox" type="checkbox">删除</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/rule/stage" class="verticalCheckbox" type="checkbox">暂存</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/rule/commit" class="verticalCheckbox" type="checkbox">提交</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/rule/publish" class="verticalCheckbox" type="checkbox">发布</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/modelBase/version/enable" class="verticalCheckbox" type="checkbox">启用/停用</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/rule/test" class="verticalCheckbox" type="checkbox">测试</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/rule/trial" class="verticalCheckbox" type="checkbox">试算</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/pub/rule/clone" class="verticalCheckbox" type="checkbox">克隆</label></div>';
            // htmlStr += '<div class="checkbox"><label><input urlStr="/pub/modelBase" class="verticalCheckbox" type="checkbox">模型库</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": "",
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var rowResourceId = row.resourceId;
            var htmlStr = '<div resourceId=\'' + rowResourceId + '\' class="resourceCheckBoxContainer">';
            for (var i = 0; i < resourcesArr.length; i++) {
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + row.resourceId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' class="resourceCheckBox" type="checkbox">';
                htmlStr += resourcesArr[i].resourceName
                htmlStr += '</label></div>';
            }
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];

// 数据源title
var dataSourceTitle = [
    {"title": "连接别名", "data": "dbAlias"},
    {"title": "连接IP", "data": "dbIp"},
    {
        "title": "数据库链接类型", "data": "dbType", "render": function (data) {
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
    {
        "title": function () {
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="/datasource/view" class="verticalCheckbox" type="checkbox">查看</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/datasource/update" class="verticalCheckbox" type="checkbox">修改</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/datasource/delete" class="verticalCheckbox" type="checkbox">删除</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": "",
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var rowResourceId = row.resourceId;
            var htmlStr = '<div resourceId=\'' + rowResourceId + '\' class="resourceCheckBoxContainer">';
            for (var i = 0; i < resourcesArr.length; i++) {
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + row.resourceId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' class="resourceCheckBox" type="checkbox">';
                htmlStr += resourcesArr[i].resourceName
                htmlStr += '</label></div>';
            }
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];

// 离线任务title
var taskTitle = [
    {"title": "任务名称", "data": "taskName"},
    {"title": "场景名称", "data": "packageName"},
    {"title": "模型名称", "data": "ruleName"},
    {"title": "模型版本", "data": "ruleVersion"},
    {
        "title": function () {
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="/task/view" class="verticalCheckbox" type="checkbox">查看</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/task/update" class="verticalCheckbox" type="checkbox">修改</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/task/delete" class="verticalCheckbox" type="checkbox">删除</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": "",
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var rowResourceId = row.resourceId;
            var htmlStr = '<div resourceId=\'' + rowResourceId + '\' class="resourceCheckBoxContainer">';
            for (var i = 0; i < resourcesArr.length; i++) {
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + row.resourceId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' class="resourceCheckBox" type="checkbox">';
                htmlStr += resourcesArr[i].resourceName
                htmlStr += '</label></div>';
            }
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];

// 指标title
var kpiTitle = [
    {"title": "指标组", "data": "kpiGroupName", "width": "10%"},
    {"title": "指标名称", "data": "kpiName", "width": "15%"},
    {"title": "指标编码", "data": "kpiCode", "width": "15%"},
    {
        "title": "数据类型", "data": "kpiType", "width": "10%", "render": function (data) {
            switch (data) {
                case '1':
                    return '字符串';
                case '2':
                    return '整型';
                case '4':
                    return '浮点型';
                case '6':
                    return '高精度小数';
                case '7':
                    return '长整型';
                case '8':
                    return '日期';
                default:
                    return '--';
            }
        }
    },
    {
        "title": "取数方式", "data": "fetchType", "width": "10%", "render": function (data) {
            switch (data) {
                case '0':
                    return '数据源';
                case '1':
                    return '接口';
                case '2':
                    return '输入指标';
                default:
                    return '--';
            }
        }
    },
    {"title": "指标描述", "data": "kpiDesc", "width": "20%"},
    {
        "title": function () {
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="/kpi/view" class="verticalCheckbox" type="checkbox">查看</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/kpi/update" class="verticalCheckbox" type="checkbox">修改</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/kpi/delete" class="verticalCheckbox" type="checkbox">删除</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": "",
        "width": "20%",
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var rowResourceId = row.resourceId;
            var htmlStr = '<div resourceId=\'' + rowResourceId + '\' class="resourceCheckBoxContainer">';
            for (var i = 0; i < resourcesArr.length; i++) {
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + row.resourceId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' class="resourceCheckBox" type="checkbox">';
                htmlStr += resourcesArr[i].resourceName
                htmlStr += '</label></div>';
            }
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];

//指标组title
var kpiGroupTitle =[
    {"title": "指标组", "data": "kpiGroupName", "width": "10%"},
    {"title": "指标描述", "data": "kpiGroupDesc", "width": "20%"},
    {
        "title": function () {
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="/kpi/group/view" class="verticalCheckbox" type="checkbox">查看</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/kpi/group/update" class="verticalCheckbox" type="checkbox">修改</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/kpi/group/delete" class="verticalCheckbox" type="checkbox">删除</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": "",
        "width": "20%",
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var rowResourceId = row.resourceId;
            var htmlStr = '<div resourceId=\'' + rowResourceId + '\' class="resourceCheckBoxContainer">';
            for (var i = 0; i < resourcesArr.length; i++) {
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + row.resourceId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' class="resourceCheckBox" type="checkbox">';
                htmlStr += resourcesArr[i].resourceName
                htmlStr += '</label></div>';
            }
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];

// 场景title
var folderTitle = [
    {"title": "场景名称", "data": "folderName"},
    {"title": "场景描述", "data": "folderDesc"},
    {
        "title": function () {
            var htmlStr = '<div class="resourceCheckBoxContainer checkAllContainer">';
            htmlStr += '<div class="checkbox"><label><input urlStr="/folder/view" class="verticalCheckbox" type="checkbox">查看</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/folder/update" class="verticalCheckbox" type="checkbox">修改</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="/folder/delete" class="verticalCheckbox" type="checkbox">删除</label></div>';
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox verticalCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        },
        "data": "",
        "render": function (data, type, row) {
            var resourcesArr = row.resources;
            var rowResourceId = row.resourceId;
            var htmlStr = '<div resourceId=\'' + rowResourceId + '\' class="resourceCheckBoxContainer">';
            for (var i = 0; i < resourcesArr.length; i++) {
                htmlStr += '<div class="checkbox"><label><input resourceId=\'' + row.resourceId + '\' urlStr=\'' + resourcesArr[i].resourceUrl + '\' class="resourceCheckBox" type="checkbox">';
                htmlStr += resourcesArr[i].resourceName
                htmlStr += '</label></div>';
            }
            htmlStr += '<div class="checkbox"><label><input urlStr="*" class="acrossCheckbox" type="checkbox">全选</label></div>';
            htmlStr += '</div>';
            return htmlStr;
        }
    }
];
