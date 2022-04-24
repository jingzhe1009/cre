/**
 * 接口添加参数组件
 * data:2019/08/03
 * author:bambi
 */

var checkedCache = new Object(); // 选中项数据缓存cache
var pageLength = 10; // 每页显示条数
var isReturnValue;

var paramAddModal = {
    /**
     * 初始化添加参数页面
     *      targetContainer: 参数信息记录缓存元素 $(target)
     *      pageType: '0'场景下参数
     *                '1'公共池参数(只能从公共中选参数)
     *                '2'公共池返回值(限定只能选一个参数)
     *                '3'场景下返回值(限定只能选一个参数)
     *      folderId: '0'/'3'场景下传场景id
     *      initFunction: 初始化页面时的初始化函数
     *      callBackFunction: 保存/取消退出后的回调函数
     *      echoParamArr: 当前已选的参数列表数据（数组格式）
     */
    initParamAddPage: function (targetContainer, pageType, folderId, initFunction, callBackFunction, echoParamArr) {
        // 1 重置搜索栏
        var inputArr = $('#paramAddAlertModal .modal-body .form-control');
        for (var i = 0; i < inputArr.length; i++) {
            $(inputArr[i]).val('');
        }
        if (!(targetContainer && pageType && callBackFunction)) {
            failedMessager.show('参数不全!');
            return;
        }
        if (initFunction) {
            initFunction(); // 执行初始化函数
        }
        paramAddModal.initParamGroup(); // 初始化参数组
        paramAddModal.initVariableType(); // 初始化参数类型

        // 2 初始化回显数据, 记录至缓存
        checkedCache = {}; // 初始化页面时先置空缓存对象
        if (pageType == '0' || pageType == '1') { // arr
            if (echoParamArr.length > 0) {
                for (var i = 0; i < echoParamArr.length; i++) {
                    checkedCache[echoParamArr[i].variableId] = echoParamArr[i];
                }
            }
        } else if (pageType == '2' || pageType == '3') { // 返回值 obj
            if (!(echoParamArr == null || echoParamArr == {})) {
                var obj = JSON.parse(echoParamArr);
                if (obj.variableId) {
                    checkedCache[obj.variableId] = obj;
                }
            }
        } else {
            failedMessager.show('paramAdd 回显参数有误无法展示！');
            return;
        }

        // 初始化内容
        isReturnValue = (pageType == '2' || pageType == '3') ? true : false; // 是否为返回值
        $('#paramAddFolderContainer, #paramAddCommonContainer').css('display', 'none');
        if (pageType == '1' || pageType == '2') { // 只能从公共参数中选择
            $('#paramAddCommonContainer').css('display', 'block');
        } else {
            $('#paramAddFolderContainer, #paramAddCommonContainer').css('display', 'block');
            // 初始化 场景 参数table并回显已选中的参数项
            initParamAddTable($('#paramAdd_folderParamTable'), initParamAddUrl(false, folderId, isReturnValue), initParamAddTitles(false), {});
        }

        // 参数种类搜索下拉框事件
        $('.paramAdd_tableKindList>li').unbind('click').on('click', function () {
            var input = $(this).parent().siblings('.form-control');
            input.val($(this).first().text());
            input.attr('kindId', $(this).attr('kindId'));
        });

        // 初始 公共池 参数table并回显已选中的参数项
        initParamAddTable($('#paramAdd_commonParamTable'), initParamAddUrl(true, '', isReturnValue), initParamAddTitles(true), {});
        $('#paramAddAlertModal').modal({'show': 'center', "backdrop": "static"});

        // 绑定页面取消事件
        $('#paramAddCancel').unbind('').click(function () {
            paramAddModal.closeParamPage();
            callBackFunction();
        });

        // 绑定页面确定事件
        $('#paramAddConfirm').unbind('').click(function () {
            paramAddModal.saveParamAdd(targetContainer, pageType, callBackFunction);
        });
    },
    // 初始化参数组下拉框
    initParamGroup: function () {
        $.ajax({
            url: webpath + '/variable/pub/group/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.data.length > 0) {
                    var data = data.data;
                    var htmlStr_search = '<li group-id=""><a>全部</a></li>';
                    for (var i = 0; i < data.length; i++) {
                        htmlStr_search += '<li group-id=\'' + data[i].variableGroupId + '\'><a>' + data[i].variableGroupName + '</a></li>';
                    }
                    $('.paramAdd_paramGroupList').empty().html(htmlStr_search); // 资源表搜索栏
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            },
            complete: function () {
                // 绑定事件
                $('.paramAdd_paramGroupList>li').unbind('click').on('click', function () {
                    $(this).parent().siblings('.form-control').val($(this).first().text());
                });
            }
        });
    },
    // 初始化参数类型
    initVariableType: function () {
        $.ajax({
            url: webpath + '/variable/variableType/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.data.length > 0) {
                    var data = data.data;
                    var htmlStr = '<li type-id=""><a>全部</a></li>';
                    for (var i = 0; i < data.length; i++) {
                        htmlStr += '<li type-id=\'' + data[i].key + '\'><a>' + data[i].text + '</a></li>';
                    }
                    $('.paramAdd_typeValueList').empty().html(htmlStr);
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            },
            complete: function () {
                // 绑定事件
                $('.paramAdd_typeValueList>li').unbind('click').on('click', function () {
                    var input = $(this).parent().siblings('.form-control');
                    input.val($(this).first().text());
                    input.attr('type-id', $(this).attr('type-id'));
                });
            }
        });
    },
    // 关闭
    closeParamPage: function () {
        $('#paramAddAlertModal').modal('toggle', 'center');
    },
    // 切换表格页数时更新缓存
    updateCache: function (that, checkStatus) {
        var checkObj = JSON.parse(that.parents('.checkboxEle').prev().text());
        if (checkStatus == '1') { // 被选中
            if (!(checkObj.variableId in checkedCache)) {
                checkedCache[checkObj.variableId] = checkObj;
            }
        } else { // 被取消
            if (checkObj.variableId in checkedCache) {
                for (var key in checkedCache) {
                    if (key == checkObj.variableId) {
                        delete checkedCache[key];
                    }
                }
            }
        }
    },
    // 保存, 更新container属性数据缓存
    saveParamAdd: function (targetContainer, pageType, callBackFunction) {
        // pageType:
        //      0场景下参数
        //      1公共规则池参数
        //      2公共规则池返回值(限定只能选一个参数)
        //      3场景下返回值参数(限定只能选一个参数)

        if (!(targetContainer && pageType && callBackFunction)) {
            failedMessager.show('参数不全, 无法保存！');
            return;
        }

        // 返回值限制只能选一个
        var isReturnValue = (pageType == '2' || pageType == '3') ? true : false;
        if (isReturnValue) {
            var limitFlag = true;
            for (var index in checkedCache) {
                if (limitFlag) {
                    limitFlag = false;
                } else {
                    failedMessager.show('返回值只能绑定一个参数！');
                    return;
                }
            }
        }

        var param = [];
        var newParam = [];
        var newParamId = [];
        var newReturnParamId = '';
        var returnObj = {};

        for (var key in checkedCache) {
            var checkObj = checkedCache[key];
            if (isReturnValue) {
                newReturnParamId = checkObj.variableId;
                var returnValueType = '0';
                if (checkObj.returnValueType) {
                    returnValueType = checkObj.returnValueType;
                } else {
                    returnValueType = (checkObj.typeId == '3') ? '2' : '1';
                }
                returnObj['isPublic'] = checkObj.isPublic;
                returnObj['variableId'] = checkObj.variableId;
                returnObj['returnValueType'] = returnValueType;
                returnObj['variableAlias'] = checkObj.variableAlias;
                returnObj['returnValue'] = checkObj.variableCode; // 编码值
            } else {
                var obj = {};
                obj['isPublic'] = checkObj.isPublic;
                obj['variableId'] = checkObj.variableId;
                obj['variableCode'] = checkObj.variableCode;
                obj['variableAlias'] = checkObj.variableAlias;
                obj['variableGroupId'] = checkObj.variableGroupId;
                obj['variableGroupName'] = checkObj.variableGroupName;
                obj['kindId'] = checkObj.kindId;
                obj['typeId'] = checkObj.typeId;
                obj['typeValue'] = checkObj.typeValue;
                param.push(checkObj.variableCode); // 编码值
                newParam.push(obj);
                newParamId.push(checkObj.variableId);
            }
        }
        // 更新至container属性中
        if (isReturnValue) {
            targetContainer
                .attr('newReturnParamId', newReturnParamId)
                .attr('returnObj', JSON.stringify(returnObj));
        } else {
            targetContainer
                .attr('param', JSON.stringify(param))
                .attr('newParam', JSON.stringify(newParam))
                .attr('newParamId', JSON.stringify(newParamId));
        }

        successMessager.show('添加成功！');
        paramAddModal.closeParamPage();
        callBackFunction(); // 执行完成后的回调函数
    },
    // 表格全选/全不选
    paramAddSelector: function (that) {
        var flag = that.attr('is-check') == '0' ? true : false;
        var allInput = that.parents('table').find('.checkbox input'); // 全部多选框
        var eleInput = that.parents('table').find('.checkboxEle input'); // 数据多选框

        if (flag) { // 全选
            allInput.prop('checked', 'checked');
            eleInput.attr('is-check', '1');
            that.attr('is-check', '1');
            for (var i = 0; i < eleInput.length; i++) {
                paramAddModal.updateCache($(eleInput[i]), '1');
            }
        } else { // 全不选
            allInput.removeAttr('checked');
            eleInput.attr('is-check', '0');
            that.attr('is-check', '0');
            for (var i = 0; i < eleInput.length; i++) {
                paramAddModal.updateCache($(eleInput[i]), '0');
            }
        }
    },
    // 回显缓存: 页面初次初始化/翻页/检索
    echoCache: function () {
        for (var key in checkedCache) {
            var variableId = checkedCache[key]['variableId'];
            $(".paramAddTableContainer .checkboxEle input[data-id='" + variableId + "']").prop('checked', 'checked');
            $(".paramAddTableContainer .checkboxEle input[data-id='" + variableId + "']").attr('is-check', '1');
        }
    },
}

/**
 * 初始化表格数据
 * @param container -> $('#table') 表格table
 * @param url 接口
 * @param titles 表头
 * @param obj 检索对象
 */
function initParamAddTable(container, url, titles, obj) {
    obj == null ? '' : obj;
    $.extend($.fn.dataTable.defaults, {
        info: true,
        "serverSide": true,
        "pageLength": pageLength
    });
    container.width('100%').dataTable({
        "searching": false, // 是否开启搜索功能
        "ordering": false,
        "destroy": true,
        "bLengthChange": false,
        "pagingType": "full_numbers",
        "paging": true,
        "info": true,
        "serverSide": true,
        "pageLength": pageLength,
        "columns": titles,
        ajax: {
            url: webpath + url,
            "type": 'GET',
            "data": function (d) { // 查询参数
                return $.extend({}, d, obj);
            }
        },
        "fnDrawCallback": function (oSettings, json) {
            // $("tr:even").css("background-color", "#fbfbfd");
            // $("table:eq(0) th").css("background-color", "#f6f7fb");

            // 全选/全不选按钮事件绑定
            container.find('.paramAddSelector').on('click', function () {
                paramAddModal.paramAddSelector($(this));
            });

            // 给checkboxEle点击绑定事件更新选中状态
            container.find('.checkboxEle input').on('click', function () {
                var newAttrValue = ($(this).attr('is-check') == '0') ? '1' : '0';
                $(this).attr('is-check', newAttrValue);
                paramAddModal.updateCache($(this), newAttrValue); // 直接更新缓存数据
            });

            // 已选参数列表回显
            paramAddModal.echoCache();
        }
    });
}

function initParamAddPage() {
    // 搜索
    $('.paramAddSearch').click(function () {
        var searchType = $(this).attr('is-public'); // 0场景表格 1公共表格
        var inputs;
        if (searchType === '0') {
            inputs = $('#paramAddFolderContainer .input-group .form-control');
        } else if (searchType === '1') {
            inputs = $('#paramAddCommonContainer .input-group .form-control');
        } else {
            return;
        }

        var obj = {};
        for (var i = 0; i < inputs.length; i++) {
            if ($.trim($(inputs[i]).val()) === '') {
                continue;
            }
            if ( $(inputs[i]).attr('data-col') === 'variableGroupName' || $(inputs[i]).attr('data-col') === 'variableTypeId' ) {
                if ( $.trim($(inputs[i]).val()) === '全部' ) {
                    continue;
                }
            }
            obj[$(inputs[i]).attr('data-col')] = $.trim($(inputs[i]).val());
        }
        if (obj.length !== 0) {
            if (searchType === '0') { // 场景下
                if (obj['kindId']) {
                    obj['kindId'] = $('#paramAddFolderContainer .paramAdd_kindInput').attr('kindId');
                }
                if (obj['variableTypeId']) {
                    obj['variableTypeId'] = $('#paramAddFolderContainer .typeIdInput').attr('type-id');
                }
                initParamAddTable($('#paramAdd_folderParamTable'), initParamAddUrl(false, folderId, isReturnValue), initParamAddTitles(false), obj);
            } else if (searchType === '1') { // 公共
                if (obj['kindId']) {
                    obj['kindId'] = $('#paramAddCommonContainer .paramAdd_kindInput').attr('kindId');
                }
                if (obj['variableTypeId']) {
                    obj['variableTypeId'] = $('#paramAddCommonContainer .typeIdInput').attr('type-id');
                }
                initParamAddTable($('#paramAdd_commonParamTable'), initParamAddUrl(true, '', isReturnValue), initParamAddTitles(true), obj);
            }
        }
    });
}

function initParamAddTitles(isPublic) {
    var titles = [];
    if (isPublic) {
        titles = [
            {
                "title": function () {
                    var htmlStr = '';
                    htmlStr += '<div class="checkbox"><label><input is-public="1" is-check="0" class="paramAddSelector" type="checkbox"></label></div>';
                    return htmlStr;
                },
                "data": "variableId",
                "width": "10%",
                "render": function (data, type, row) {
                    var htmlStr = '';
                    htmlStr += '<div id="addParam_' + data + '" style="display:none;">' + JSON.stringify(row) + '</div>'; //当前参数数据
                    htmlStr += '<div class="checkbox checkboxEle"><label><input is-check="0" data-id=\'' + data + '\' type="checkbox"></label></div>';
                    $("#addParam_" + data).data("paramRowData", row);
                    return htmlStr;
                }
            },
            {
                "title": "参数组", "data": "variableGroupId", "width": "15%", "render": function (data, type, row) {
                    var groupName = row.variableGroupName;
                    if (groupName == null) {
                        groupName = '--';
                    }
                    var htmlStr = '';
                    htmlStr += '<span data-id=\'' + data + '\'>' + groupName + '</span>';
                    return htmlStr;
                }
            },
            {"title": "变量别名", "data": "variableAlias", "width": "20%"},
            {"title": "变量编码", "data": "variableCode", "width": "20%"},
            {"title": "变量类型", "data": "typeValue", "width": "10%"},
            {
                "title": "变量种类", "data": "kindId", "width": "10%", "render": function (data) {
                    switch ($.trim(data)) {
                        case 'K1':
                            return '输入变量';
                        case 'K2':
                            return '输出变量';
                        case 'K3':
                            return '中间变量';
                        case 'K4':
                            return '系统常量';
                        default:
                            return '--';
                    }
                }
            },
            {"title": "描述", "data": "variableRemarks", "width": "15%"}
        ];
    } else {
        titles = [
            {
                "title": function () {
                    var htmlStr = '';
                    htmlStr += '<div class="checkbox"><label><input is-public="0" is-check="0" class="paramAddSelector" type="checkbox"></label></div>';
                    return htmlStr;
                },
                "data": "variableId",
                "width": "10%",
                "render": function (data, type, row) {
                    var htmlStr = '';
                    htmlStr += '<div id="addParam_' + data + '" style="display:none;">' + JSON.stringify(row) + '</div>'; //当前参数数据
                    htmlStr += '<div class="checkbox checkboxEle"><label><input is-check="0" data-id=\'' + data + '\' type="checkbox"></label></div>';
                    $("#addParam_" + data).data("paramRowData", row);
                    return htmlStr;
                }
            },
            {"title": "变量别名", "data": "variableAlias", "width": "20%"},
            {"title": "变量编码", "data": "variableCode", "width": "20%"},
            {"title": "变量类型", "data": "typeValue", "width": "15%"},
            {
                "title": "变量种类", "data": "kindId", "width": "15%", "render": function (data) {
                    switch ($.trim(data)) {
                        case 'K1':
                            return '输入变量';
                        case 'K2':
                            return '输出变量';
                        case 'K3':
                            return '中间变量';
                        case 'K4':
                            return '系统常量';
                        default:
                            return '--';
                    }
                }
            },
            {"title": "描述", "data": "variableRemarks", "width": "20%"}
        ];
    }
    return titles;
}

function initParamAddUrl(isPublic, folderId, isReturnValue) {
    if (isPublic) {
        if (isReturnValue) {
            return '/variable/pub/list'; //查看所有公共参数
        }
        return '/variable/pub/list/flat'; //扁平化查看所有公共参数
    } else {
        if (isReturnValue) {
            return '/variable/queryVariablesByFoldId?folderId=' + folderId; //查看场景下所有参数
        }
        return '/variable/queryVariablesByFoldId/flat?folderId=' + folderId; //扁平化查看场景下所有参数
    }
}

$(function () {
    initParamAddPage();
});