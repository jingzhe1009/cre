/**
 * 公共资源池管理
 * data:2019/07/18
 * author:bambi
 */


/**
 *  公共参数
 */
var paramModule = {
    // 展示弹框
    show: function (handleType, $this) {
        var detail = {};
        if ($this) {
            var curRow = $this.parentNode.parentNode;
            if (handleType === 0 || handleType === 1) { //外层参数
                detail = $('#commonResourceTable').DataTable().row(curRow).data();
            } else if (handleType === 2 || handleType === 3) { //对象参数内参数
                detail = $('#objParamsTable').DataTable().row(curRow).data();
            }
        }

        $('#paramAlertModal form')[0].reset();
        $('#paramAlertModal').removeAttr('varId'); // 清除varId标识
        $('#paramAlertModal').removeAttr('usedFlag'); // 清除usedCheck状态标识
        $('.cron_msg').addClass('hide');
        $('.cron_msg').parents('.form-group').find('input').removeAttr('disabled');
        $('#paramAlertModal .variableKindFormGroup').addClass('hide');
        $('#paramAlertModal .variableKindSelector option:first').prop('selected', true);
        // $('#cron_msg').addClass('hide');
        // $('#paramAlertModal #variableAliasInput').removeAttr('disabled');

        // handleType: 0新增 1修改 2新增对象参数 3修改对象参数
        if (handleType === 0) {
            $.ajax({
                url: webpath + '/createCheck/check',
                type: 'GET',
                dataType: "json",
                // data: {variableId: JSON.parse(detail).variableId},
                data: {variableId: detail.variableId},
                success: function (data) {
                    if (data.status === 0) {
                        $('#paramTitle').text('添加参数');
                        $('#addParamGroupDiv').parent().css('display', 'block'); // 展示参数组
                        // $('#variableAliasDiv').css('display', 'none'); // 隐藏编码别名
                        $('#paramAlertModal .variableKindFormGroup').removeClass('hide'); // 展示参数种类
                        $('#paramAlertModal .variableKindSelector').trigger('change');
                        $('#paramAlertModal').attr('handleType', handleType).modal({'show': 'center', "backdrop": "static"});
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType === 1) {
            $('#paramAlertModal .variableKindFormGroup').removeClass('hide'); // 展示参数种类
            // 参数修改权限校验 authCheck
            $.ajax({
                url: webpath + '/variable/update/checkAuth',
                type: 'GET',
                dataType: "json",
                // data: {variableId: JSON.parse(detail).variableId},
                data: {variableId: detail.variableId},
                success: function (data) {
                    if (data.status === 0) {
                        $('#paramTitle').text('修改参数');
                        $('#addParamGroupDiv').parent().css('display', 'block'); // 展示参数组
                        // $('#variableAliasDiv').css('display', 'none'); // 隐藏编码别名
                        paramModule.echoData(detail); // 数据回显
                        $('#paramAlertModal').attr('handleType', handleType).modal({
                            'show': 'center',
                            "backdrop": "static"
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType === 2) {
            $('#paramTitle').text('添加参数');
            $('#addParamGroupDiv').parent().css('display', 'none'); // 不展示参数组
            // $('#variableAliasDiv').css('display', 'block'); // 展示编码别名
            $('#paramAlertModal .variableKindSelector').trigger('change');
            objectParam.hide();
            $('#paramAlertModal').attr('handleType', handleType).modal({'show': 'center', "backdrop": "static"});
        } else if (handleType === 3) {
            // 参数修改权限校验 authCheck
            $.ajax({
                url: webpath + '/variable/update/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {variableId: $('#objectParamAlertModal').attr('fatherVarId')},
                success: function (data) {
                    if (data.status === 0) {
                        $('#paramTitle').text('修改参数');
                        $('#addParamGroupDiv').parent().css('display', 'none'); // 不展示参数组
                        // $('#variableAliasDiv').css('display', 'block'); // 展示编码别名
                        paramModule.echoData(detail); // 数据回显
                        objectParam.hide();
                        $('#paramAlertModal').attr('handleType', handleType).modal({
                            'show': 'center',
                            "backdrop": "static"
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        }
        // 参数修改: 在用状态check, 在用状态下不可修改参数名称
        if (handleType === 1 || handleType === 3) {
            $.ajax({ // 验证是否在用
                url: webpath + '/variable/checkUsed',
                type: 'POST',
                dataType: "json",
                // data: {"variableId": JSON.parse(detail).variableId},
                data: {"variableId": detail.variableId},
                success: function (data) {
                    if (data.status === -1) {
                        $('#paramAlertModal').attr('usedFlag', '1');
                        $('.cron_msg').parents('.form-group').find('input').attr('disabled', true);
                        $('.cron_msg').removeClass('hide');
                        // $('#paramAlertModal #variableAliasInput').attr('disabled', true);
                        // $('#cron_msg').removeClass('hide');
                    } else {
                        $('#paramAlertModal').attr('usedFlag', '0'); // 未使用
                        $('.cron_msg').parents('.form-group').find('input').removeAttr('disabled');
                        $('.cron_msg').addClass('hide');
                        // $('#paramAlertModal #variableAliasInput').removeAttr('disabled');
                        // $('#cron_msg').addClass('hide');
                    }
                }
            });
        }
    },
    // 关闭弹框
    hidden: function () {
        $('#paramAlertModal form')[0].reset();
        $('#paramAlertModal').modal('toggle', 'center');
        var handleType = $('#paramAlertModal').attr('handleType');
        if (handleType == '2' || handleType == '3') {
            // 跳转回属性列表页面
            objectParam.show();
        }
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
                    var htmlStr_search = '';
                    var htmlStr_edit = '';
                    for (var i = 0; i < data.length; i++) {
                        htmlStr_search += '<li group-id=\'' + data[i].variableGroupId + '\'><a>' + data[i].variableGroupName + '</a></li>';
                        htmlStr_edit += '<option group-id=\'' + data[i].variableGroupId + '\'>' + data[i].variableGroupName + '</option>';
                    }
                    $('.paramGroupSelector_search').empty().html(htmlStr_search); // 资源表搜索栏
                    $('#paramGroupSelector_edit').empty().html(htmlStr_edit); // 弹框下拉框
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            },
            complete: function () {
                // 绑定事件
                $('.paramGroupSelector_search>li').unbind('click').on('click', function () {
                    $(this).parent().siblings('.form-control').val($(this).first().text());
                });
            }
        });
    },
    // 初始化参数类型
    initVariable: function () {
        $.ajax({
            url: webpath + '/variable/variableType/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.data.length > 0) {
                    var data = data.data;
                    var htmlStr = '';
                    var htmlListStr = '';
                    for (var i = 0; i < data.length; i++) {
                        htmlStr += '<option type-id=\'' + data[i].key + '\'>' + data[i].text + '</option>';
                        htmlListStr += '<li type-id=\'' + data[i].key + '\'><a>' + data[i].text + '</a></li>';
                    }
                    $('#variableTypeSelector').empty().html(htmlStr);
                    $('.variableTypeList').empty().html(htmlListStr);
                    $('.variableTypeList li').unbind().on('click', function () {
                        $('.variableTypeInput').val($(this).first().text()).attr('typeId', $(this).attr('type-id'));
                    });
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            }
        });
    },
    // 保存参数(添加/修改)
    saveParam: function () {
        if (!$('#paramAlertModal form').isValid()) {
            return;
        }
        var handleType = $('#paramAlertModal').attr('handleType'); // 0新增 1修改 2新增对象属性 3修改对象属性
        var urlStr = '';
        if (handleType == '0') {
            urlStr = '/variable/pub/save';
        } else if (handleType == '1') {
            urlStr = '/variable/updateVariable';
        } else if (handleType == '2') {
            urlStr = '/variable/pub/insertVariableRelation'
        } else if (handleType == '3') {
            urlStr = '/variable/updateVariableRelation';
        } else {
            return;
        }
        var varId = $('#paramAlertModal').attr('varId');
        var obj = paramModule.getAlertData(handleType, varId); // 获取表单数据对象
        if (obj) {
            if (obj.kindId === 'K4') {
                if (obj.typeId === '2') {
                    obj.defaultValue = parseFloat(obj.defaultValue);
                    if (Math.floor(obj.defaultValue) !== obj.defaultValue) {
                        failedMessager.show('你输入的默认值不是整型，请重新输入！');
                        return;
                    }
                }
                if (obj.typeId === '4') {
                    if (isNaN(parseFloat(obj.defaultValue))) {
                        failedMessager.show('你输入的默认值不是浮点型，请重新输入！');
                        return;
                    }
                }
            }
            if (handleType == '2' || handleType == '3') {
                // 校验组id
                if (obj['variableGroupId'] == null || obj['variableGroupId'] == '') {
                    failedMessager.show('父对象无参数组无法保存！');
                    return;
                }
            }
            if (handleType == '1' || handleType == '3') { // 修改操作需要验证
                var usedFlag = $('#paramAlertModal').attr('usedFlag');
                if (usedFlag == '0') {
                    paramModule.saveParamAjax(handleType, urlStr, obj, true);
                } else if (usedFlag == '1') {
                    // // 直接拒绝
                    // failedMessager.show('保存失败，' + data.msg);
                    // 验证未通过则警告, 若再次确认则修改
                    $('#paramAlertModal').modal('toggle', 'center');
                    confirmAlert.show(
                        '参数被引用中, 是否仍要修改？',
                        function () {
                            paramModule.saveParamAjax(handleType, urlStr, obj, false)
                        }, function () {
                            if (handleType == '3') { // 对象参数展示其弹框
                                initObjectPropertyTable($('#objectParamAlertModal').attr('entityId')); // 刷新table
                                objectParam.show();
                            }
                        });
                } else {
                    return;
                }

                // $.ajax({ // 验证是否在用
                //     url: webpath + '/variable/checkUsed',
                //     type: 'POST',
                //     dataType: "json",
                //     data: {"variableId": varId},
                //     success: function (data) {
                //         if (data.status == 0) {
                //             paramModule.saveParamAjax(handleType, urlStr, obj, true);
                //         } else {
                //             // failedMessager.show('保存失败，' + data.msg);
                //             // 验证未通过则警告, 若再次确认则修改
                //             $('#paramAlertModal').modal('toggle', 'center');
                //             confirmAlert.show(
                //                 data.msg + ', 是否仍要修改？',
                //                 function () {
                //                     paramModule.saveParamAjax(handleType, urlStr, obj, false)
                //                 }, function () {
                //                     if (handleType == '3') { // 对象参数展示其弹框
                //                         initObjectPropertyTable($('#objectParamAlertModal').attr('entityId')); // 刷新table
                //                         objectParam.show();
                //                     }
                //                 });
                //         }
                //     },
                //     error: function (data) {
                //         failedMessager.show(data.msg);
                //     },
                // });
            } else {
                paramModule.saveParamAjax(handleType, urlStr, obj, true);
            }
        }
    },
    // 发送请求, 保存参数
    saveParamAjax: function (handleType, urlStr, obj, closeModalFlag) {
        $.ajax({
            url: webpath + urlStr,
            type: 'POST',
            dataType: "json",
            data: obj,
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('保存成功');
                    // initTable('0');
                    $('.searchBtn').trigger('click');
                    if (closeModalFlag) {
                        paramModule.hidden();
                    }
                    if (handleType == '2' || handleType == '3') { // 对象参数展示其弹框
                        initObjectPropertyTable($('#objectParamAlertModal').attr('entityId')); // 刷新table
                        objectParam.show();
                    }
                } else {
                    failedMessager.show(data.msg);
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            },
        });
    },
    // 获取弹框内数据
    getAlertData: function (handleType, variableId) {
        var obj = {};
        var inputs = $('#paramAlertModal .form-control');
        for (var i = 0; i < inputs.length; i++) {
            obj[$(inputs[i]).attr('col-name')] = $.trim($(inputs[i]).val());
        }
        // 参数组&变量类型 单独处理一下
        obj['typeId'] = $('#variableTypeSelector option:selected').attr('type-id');
        obj['variableGroupId'] = $('#paramGroupSelector_edit option:selected').attr('group-id');
        obj['kindId'] = $('.variableKindSelector option:selected').prop('value');
        if (handleType === '1' || handleType == '3') { // 修改参数多传varId
            if (variableId) {
                obj['entityId'] = $('#paramAlertModal').attr('entityId');
                obj['variableId'] = variableId;
            } else {
                return {};
            }
        }
        if (handleType == '2' || handleType == '3') { // 对象属性
            obj['entityPid'] = $('#objectParamAlertModal').attr('entityId');
            obj['variableGroupId'] = $('#addObjectParam').attr('paramGroupId');
            obj['entityVariableAlias'] = $.trim($('#variableCodeInput').val()); // 编码别名字段不填写, 值与变量编码一致
            obj['kindId'] = $('#addObjectParam').attr('kindId');
        }
        return obj;
    },
    // 参数数据回显
    echoData: function (detail) {
        // var data = JSON.parse(detail);
        var data = detail ? detail : {};
        for (var key in data) {
            if (key === 'variableGroupId') { // 参数组单独处理
                $('#paramGroupSelector_edit option[group-id="' + data[key] + '"]').prop('selected', true);
                continue;
            }
            if (key === 'typeId') { // 变量类型单独处理
                $('#variableTypeSelector option[type-id="' + data[key] + '"]').prop('selected', true);
                continue;
            }
            if (key === 'variableId') {
                $('#paramAlertModal').attr('varId', data[key]);
                continue;
            }
            if (key === 'entityId') {
                var entityId = (data[key] == null) ? '' : data[key];
                $('#paramAlertModal').attr('entityId', entityId);
                continue;
            }
            if (key === 'kindId') { // 变量种类单独处理
                $("#paramAlertModal .variableKindSelector option[value='" + data[key] + "']").prop('selected', true);
                $('.variableKindSelector').trigger('change');
                continue;
            }
            var target = $("#paramAlertModal .form-control[col-name='" + key + "']");
            if (target.length > 0) {
                $(target).val(data[key]);
            }
        }
    },
    // 删除参数
    deleteParam: function (varId, isObject, objectEntityId) {
        if (varId) {
            // 权限校验
            $.ajax({
                url: webpath + '/variable/delete/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {"variableId": varId},
                success: function (data) {
                    // 拥有权限
                    if (data.status === 0) {
                        var obj = {};
                        obj['variableId'] = varId;
                        if (isObject) {
                            if (objectEntityId) {
                                obj['entityId'] = objectEntityId;
                            } else {
                                failedMessager.show('参数不全！无法删除！');
                                return;
                            }
                        }
                        confirmAlert.show('是否确认删除？', function () {
                            $.ajax({ // 验证是否可删
                                url: webpath + '/variable/checkUsed',
                                type: 'POST',
                                dataType: "json",
                                data: {"variableId": varId},
                                success: function (data) {
                                    if (data.status === 0) { // 可删
                                        $.ajax({
                                            url: webpath + '/variable/deleteVariable',
                                            type: 'POST',
                                            dataType: "json",
                                            data: obj,
                                            success: function (data) {
                                                if (data.status === 0) {
                                                    successMessager.show('删除成功');
                                                    // initTable('0');
                                                    $('.searchBtn').trigger('click');
                                                } else {
                                                    failedMessager.show(data.msg);
                                                }
                                            }
                                        });
                                    } else {
                                        failedMessager.show(data.msg);
                                    }
                                }
                            });
                        });
                    } else {
                        // 无权限
                        failedMessager.show(data.msg);
                    }
                }
            });
        }
    },
    // 展示添加参数组弹框
    showAddGroupAlert: function (handleType, $this) {
        var detail = {};
        if ($this) {
            var curRow = $this.parentNode.parentNode;
            detail = $('#paramGroupTable').DataTable().row(curRow).data();
        }

        // handleType: 0新增 1修改 2查看
        $('#paramGroupAlertModal form')[0].reset();
        $('#paramGroupAlertModal .modal-footer button').css('display', 'none');
        if (handleType == 0) {
            $.ajax({
                url: webpath + '/createCheck/check',
                type: 'GET',
                data: {'variableGroupId': detail['variableGroupId']? detail['variableGroupId']:''},
                dataType: "json",
                success: function (data) {
                    if (data.status === 0) {
                        $('#paramGroupAlertModal').modal({'show': 'center', "backdrop": "static"});
                        $('#paramGroupAlertModal .modal-footer .notView button').css('display', 'inline-block');
                        $('#paramGroupAlertModal .modal-title').text('').text('添加参数组');
                        $('#paramGroupAlertModal .form-control').removeAttr('disabled');
                        paramModule.echoGroupData(detail);
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType == 1) {
            $.ajax({
                url: webpath + '/variable/group/update/checkAuth',
                type: 'GET',
                data: {'variableGroupId': detail['variableGroupId']? detail['variableGroupId']:''},
                dataType: "json",
                success: function (data) {
                    if (data.status === 0) {
                        $('#paramGroupAlertModal').modal({'show': 'center', "backdrop": "static"});
                        $('#paramGroupAlertModal .modal-footer .notView button').css('display', 'inline-block');
                        $('#paramGroupAlertModal .modal-title').text('').text('修改参数组');
                        $('#paramGroupAlertModal .form-control').removeAttr('disabled');
                        paramModule.echoGroupData(detail);
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType == 2) {
            $('#paramGroupAlertModal').modal({'show': 'center', "backdrop": "static"});
            $('#paramGroupAlertModal').attr('handleType', handleType);
            $('#paramGroupAlertModal .modal-footer #closeViewParamGroup').css('display', 'inline-block');
            $('#paramGroupAlertModal .modal-title').text('').text('查看参数组');
            $('#paramGroupAlertModal .form-control').attr('disabled', true);
            paramModule.echoGroupData(detail);
        }
    },
    // 关闭添加参数组弹框
    hiddenAddGroupAlert: function () {
        $('#paramGroupAlertModal').modal('toggle', 'center');
    },
    // 保存参数组
    saveParamGroup: function () {
        var mustInputs = $('#paramGroupAlertModal form .must');
        for (var i = 0; i < mustInputs.length; i++) {
            if ($(mustInputs[i]).val() == '') {
                failedMessager.show('请填入必填项！');
                $(mustInputs[i]).focus();
                return;
            }
        }
        var handleType = $('#paramGroupAlertModal').attr('handleType'); // 0新增 1修改
        var urlStr = '';
        if (handleType == '0') {
            urlStr = '/variable/pub/group/save';
        } else if (handleType == '1') {
            urlStr = '/variable/pub/group/update';
        } else {
            return;
        }
        var obj = paramModule.getGroupData(handleType);
        if (obj) {
            $.ajax({
                url: webpath + urlStr,
                type: 'POST',
                dataType: "json",
                data: obj,
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('保存成功');
                        // initGroupTable('0');
                        // initTable('0');
                        $('.searchBtn').trigger('click');
                        paramModule.hiddenAddGroupAlert();
                        paramModule.initParamGroup(); // 刷新参数组下拉框: 搜索栏*2/弹框
                        paramModule.initVariable(); // 刷新弹框变量类型
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
    // 参数组数据回显
    echoGroupData: function (detail) {
        if (detail) {
            var data = detail ? detail : {};
            for (var key in data) {
                if (key === 'variableGroupId') { // 回显参数组id
                    $('#paramGroupAlertModal').attr('groupId', data[key]);
                    continue;
                }
                var target = $("#paramGroupAlertModal .form-control[col-name='" + key + "']");
                if (target.length > 0) {
                    $(target).val(data[key]);
                }
            }
        }
    },
    // 获取参数组表单数据
    getGroupData: function (handleType) {
        var obj = {};
        var inputs = $('#paramGroupAlertModal form .form-control');
        for (var i = 0; i < inputs.length; i++) {
            obj[$(inputs[i]).attr('col-name')] = $(inputs[i]).val();
        }
        if (handleType == 1) { //修改需要加上参数组id
            obj['variableGroupId'] = $('#paramGroupAlertModal').attr('groupId');
        }
        return obj;
    },
    // 删除参数组
    deleteGroup: function (variableGroupId) {
        if (variableGroupId) {
            $.ajax({ // 删除权限校验
                url: webpath + '/variable/group/delete/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {'variableGroupId': variableGroupId},
                success: function (data) {
                    debugger;
                    if (data.status === 0) {
                        confirmAlert.show('是否确认删除？', function () {
                            $.ajax({
                                url: webpath + '/variable/pub/group/delete',
                                type: 'POST',
                                dataType: "json",
                                data: {'variableGroupId': variableGroupId},
                                success: function (data) {
                                    if (data.status === 0) {
                                        successMessager.show('删除成功');
                                        // initGroupTable('0');
                                        // initTable('0');
                                        $('.searchBtn').trigger('click');
                                        paramModule.initParamGroup(); // 刷新参数组下拉框: 搜索栏*2/弹框
                                        paramModule.initVariable(); // 刷新弹框变量类型
                                    } else {
                                        failedMessager.show(data.msg);
                                    }
                                },
                            });
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        }
    },
    // 参数查看
    viewParam: function (type, $this) {
        $('#paramViewAlertModal .form-control').attr('disabled', true);
        // 获取表格当前行数据
        var data = {};
        var curRow = $this.parentNode.parentNode;
        if (type === 0) { //外层表格
            data = $('#commonResourceTable').DataTable().row(curRow).data();
        } else if (type === 1) { //对象内参数
            data = $('#objParamsTable').DataTable().row(curRow).data();
        }
        $("#paramViewAlertModal form")[0].reset();
        $("#paramViewAlertModal .paramGroupDiv").removeClass('hide');
        $("#paramViewAlertModal .kindValueDiv").removeClass('hide');
        $("#paramViewAlertModal .defaultValueGroup").removeClass('hide');
        $('#closeParamView').unbind();
        for (var key in data) {
            var target = $("#paramViewAlertModal .form-control[col-name='" + key + "']");
            if (target.length > 0) {
                $(target).val(data[key]);
            }
        }
        if (type === 1) { // 对象属性内参数 无组, 无参数种类
            $("#paramViewAlertModal .paramGroupDiv").addClass('hide');
            $("#paramViewAlertModal .kindValueDiv").addClass('hide');
            $('#objectParamAlertModal').modal('hide');
            $('#closeParamView').click(function () {
                $('#objectParamAlertModal').modal({'show': 'center', "backdrop": "static"});
                $('#paramViewAlertModal .form-control').removeAttr('disabled');
            });
        }
        if (data.kindId !== 'K4') {
            $("#paramViewAlertModal .defaultValueGroup").addClass('hide');
        }
        $("#paramViewAlertModal").modal({'show': 'center', "backdrop": "static"});
    }
}

/**
 * 公共参数-对象参数
 */
var objectParam = {
    // 点击属性弹出弹框
    init: function (entityId, entityName, parGroupId, isNested, kindId, fatherVarId) {
        if (isNested) {
            // 是否是对象内的对象参数, 是则将父entity缓存下来
            var fatherEntityIdArr = [];
            var fatherEntityNameArr = [];
            if ($('#objectParamAlertModal').attr('fatherEntityIdArr')) {
                fatherEntityIdArr = JSON.parse($('#objectParamAlertModal').attr('fatherEntityIdArr'));
            }
            if ($('#objectParamAlertModal').attr('fatherEntityNameArr')) {
                fatherEntityNameArr = JSON.parse($('#objectParamAlertModal').attr('fatherEntityNameArr'));
            }
            var fatherEntityId = $('#objectParamAlertModal').attr('entityId');
            var fatherEntityName = $('#objectParamAlertModal .modal-title span').text();
            fatherEntityIdArr.push(fatherEntityId);
            fatherEntityNameArr.push(fatherEntityName);
            $('#objectParamAlertModal').attr('fatherEntityIdArr', JSON.stringify(fatherEntityIdArr)).attr('fatherEntityNameArr', JSON.stringify(fatherEntityNameArr));
        } else { // 不是对象嵌套的对象(即为最外层父对象参数)
            $('#addObjectParam').attr('paramGroupId', parGroupId); // 新建变量按钮:缓存记录下父对象的参数组id
            $('#addObjectParam').attr('kindId', kindId); // 在弹框上记录kindId
            $('#objectParamAlertModal').attr('fatherVarId', fatherVarId); // 在弹框上记录fatherVarId; 用于内层删除修改权限校验
        }
        initObjectPropertyTable(entityId); // 初始化table
        $('#objectParamAlertModal').attr('entityId', entityId); // 在弹框上记录entityId
        $('#objectParamAlertModal .modal-title span').text('').text(entityName);
        $('#objectParamAlertModal').modal({'show': 'center', "backdrop": "static"}); // 展示弹框
    },
    // 仅展示弹框
    show: function () {
        $('#objectParamAlertModal').modal({'show': 'center', "backdrop": "static"}); // 展示弹框
    },
    // 关闭弹框
    hide: function () {
        $('#objectParamAlertModal').modal('toggle', 'center');
    },
    // 点击属性弹框完成按钮
    closeObjParamModal: function () {
        var parentIdArrStr = $('#objectParamAlertModal').attr('fatherEntityIdArr');
        if (parentIdArrStr) {
            var parentIdArr = JSON.parse(parentIdArrStr);
            if (parentIdArr.length > 0) { // 不是最外层则加载上一个
                var fatherEntityNameArr = JSON.parse($('#objectParamAlertModal').attr('fatherEntityNameArr'));

                var lastId = parentIdArr[parentIdArr.length - 1]; // 取上一个id
                var lastName = fatherEntityNameArr[fatherEntityNameArr.length - 1];

                parentIdArr.splice(parentIdArr.length - 1, 1); // 删掉最后一个
                fatherEntityNameArr.splice(fatherEntityNameArr.length - 1, 1); // 删掉最后一个

                // 重新记录
                $('#objectParamAlertModal').attr('fatherEntityIdArr', JSON.stringify(parentIdArr))
                    .attr('fatherEntityNameArr', JSON.stringify(fatherEntityNameArr));

                // 加载回上一个列表, 回显id和name
                $('#objectParamAlertModal').attr('entityId', lastId);
                $('#objectParamAlertModal .modal-title span').text('').text(lastName);
                initObjectPropertyTable(lastId);

            } else { // 是最外层的则直接关掉
                objectParam.hide();
            }
        } else { // 还没有记录的父id, 初始化默认关闭弹框
            objectParam.hide();
        }
    },
    // 点击属性弹框关闭按钮
    closeAllObjParamModal: function () {
        // 清除属性缓存并退出弹框
        $('#objectParamAlertModal').removeAttr('fatherEntityIdArr fatherEntityNameArr entityId');
        objectParam.hide();
    },
    // 添加对象属性
    addObjectParam: function () {
        // 参数新增权限校验 authCheck
        $.ajax({
            url: webpath + '/variable/update/checkAuth',
            type: 'GET',
            dataType: "json",
            data: {variableId: $('#objectParamAlertModal').attr('fatherVarId')},
            success: function (data) {
                if (data.status === 0) {
                    paramModule.show(2);
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 删除对象属性
    deleteObjectParam: function (varId, isObject, entityId) {
        if (varId) {
            var obj = {};
            obj['variableId'] = varId;
            if (isObject) {
                if (entityId) {
                    obj['entityId'] = entityId;
                } else {
                    failedMessager.show('参数不全，无法删除！');
                    return;
                }
            }

            // 删除权限校验, varId为最外层父id
            var fatherVarId = $('#objectParamAlertModal').attr('fatherVarId');
            $.ajax({
                url: webpath + '/variable/delete/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {"variableId": fatherVarId},
                success: function (data) {
                    if (data.status === 0) {
                        objectParam.hide();
                        confirmAlert.show('是否确认删除？', function () {
                            $.ajax({ // 验证是否可删
                                url: webpath + '/variable/checkUsed',
                                type: 'POST',
                                dataType: "json",
                                data: {"variableId": varId},
                                success: function (data) {
                                    if (data.status === 0) {
                                        $.ajax({
                                            url: webpath + '/variable/deleteVariableRelation',
                                            type: 'POST',
                                            dataType: "json",
                                            data: obj,
                                            success: function (data) {
                                                if (data.status === 0) {
                                                    successMessager.show('删除成功');
                                                    initObjectPropertyTable($('#objectParamAlertModal').attr('entityId'));
                                                    objectParam.show();
                                                } else {
                                                    failedMessager.show(data.msg);
                                                }
                                            },
                                            error: function (data) {
                                                failedMessager.show(data.msg);
                                            }
                                        });
                                    } else {
                                        failedMessager.show(data.msg);
                                    }
                                },
                                error: function (data) {
                                    failedMessager.show(data.msg);
                                }
                            });
                        }, function () {
                            objectParam.show();
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
 *  公共接口
 */
var apiModule = {
    // 展示
    show: function (handleType, apiId) {
        // 数据、属性初始化
        $("#apiAlertModal .modal-body .form-control[type='text']").val(''); // 清空表单数据
        $('#baseForm, #httpForm, #redisForm, #codisForm').validator('cleanUp'); // 清除表单中的全部验证消息
        $('#apiAlertModal').removeAttr('usedFlag'); // 清除引用状态标识
        $('#apiNameInput').removeAttr('disabled');
        $('#cron_api_msg').addClass('hide');
        $('#apiAlertModal')
            .removeAttr('param')
            .removeAttr('newParam')
            .removeAttr('oldParamId')
            .removeAttr('newParamId')
            .removeAttr('oldReturnParamId')
            .removeAttr('newReturnParamId')
            .removeAttr('returnObj'); // 清除数据缓存属性

        // 表单动态展示内容初始化, 默认加载http类型表单内容
        $('.apiTypeContent').removeClass('apiTypeContent_active').addClass('apiTypeContent_init');
        $('.redisTypeContent').removeClass('redisTypeContent_active').addClass('redisTypeContent_init');
        $('#apiTypeSelector option').first().prop('selected', true);
        apiModule.initApiAlertForm($('#apiTypeSelector option').first().attr('type-key'));

        $('#addApiReturnParam, #addApiParam').css('display', 'inline-block'); // 恢复操作按钮
        $("#apiAlertModal .modal-footer button").css('display', 'inline-block');
        $("#apiAlertModal .form-control").removeAttr('disabled');
        $("#apiAlertModal #returnValueTypeSelector").attr('disabled', true);

        // 0新增 1修改 2查看
        if (handleType == 0) {
            $.ajax({
                url: webpath + '/createCheck/check',
                type: 'GET',
                dataType: "json",
                data: {'apiId': apiId},
                success: function (data) {
                    if (data.status === 0) {
                        $("#apiAlertModal #closeViewApi").css('display', 'none');
                        $('#apiTitle').text('添加接口');
                        apiModule.initData(); // 初始化新增接口弹框
                        $('#apiAlertModal').attr('handleType', handleType).modal({'show': 'center', "backdrop": "static"});
                        $("#apiAlertModal .form-control").removeAttr('disabled');
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType == 1) {
            $("#apiAlertModal #closeViewApi").css('display', 'none');
            $("#apiAlertModal .form-control").removeAttr('disabled');
            // 验证是否有权限修改 authCheck
            $.ajax({
                url: webpath + '/api/update/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {'apiId': apiId},
                success: function (data) {
                    if (data.status === 0) {
                        $('#apiTitle').text('修改接口');
                        // 查询接口输入参数
                        $.ajax({
                            url: webpath + '/api/variables',
                            type: 'GET',
                            dataType: "json",
                            data: {'apiId': apiId},
                            success: function (data) {
                                if (data.status === 0) {
                                    if (data.data.length > 0) {
                                        $('#apiAlertModal').attr('newParam', JSON.stringify(data.data));
                                    }
                                    apiModule.echoData(apiId); // 接口数据回显
                                    $('#apiAlertModal').attr('handleType', handleType).modal({
                                        'show': 'center',
                                        "backdrop": "static"
                                    });
                                } else {
                                    failedMessager.show(data.msg);
                                }
                            }
                        });
                        // 查询接口引用状态并标识
                        $.ajax({
                            url: webpath + '/api/checkUsed',
                            type: 'POST',
                            dataType: "json",
                            data: {"apiId": apiId},
                            success: function (data) {
                                if (data.status === -1) {
                                    $('#apiAlertModal').attr('usedFlag', '1'); // 被引用
                                    $('#apiNameInput').attr('disabled', true);
                                    $('#cron_api_msg').removeClass('hide');
                                } else {
                                    $('#apiAlertModal').attr('usedFlag', '0'); // 未使用
                                    $('#apiNameInput').removeAttr('disabled');
                                    $('#cron_api_msg').addClass('hide');
                                }
                            }
                        });
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType == 2) { // 查看
            $('#apiTitle').text('查看接口');
            $('#addApiReturnParam, #addApiParam').css('display', 'none');
            $("#apiAlertModal .notViewBtns button").css('display', 'none');
            $("#apiAlertModal .form-control").attr('disabled', true);
            // $("#apiAlertModal .form-control").removeAttr('disabled');
            // 查询接口输入参数
            $.ajax({
                url: webpath + '/api/variables',
                type: 'GET',
                dataType: "json",
                data: {'apiId': apiId},
                success: function (data) {
                    if (data.status === 0) {
                        if (data.data.length > 0) {
                            $('#apiAlertModal').attr('newParam', JSON.stringify(data.data));
                        }
                        apiModule.echoData(apiId); // 接口数据回显
                        $('#apiAlertModal').attr('handleType', handleType).modal({
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
    // 展开弹框, 不做其他操作
    open: function () {
        $('#apiAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 关闭弹框
    hidden: function () {
        $('#apiAlertModal').modal('toggle', 'center');
    },
    // 初始化接口组
    initApiGroup: function () {
        $.ajax({
            url: webpath + '/api/pub/group/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.data.length > 0) {
                    var data = data.data;
                    var htmlStr_search = '';
                    var htmlStr_edit = '';
                    for (var i = 0; i < data.length; i++) {
                        htmlStr_search += '<li group-id=\'' + data[i].apiGroupId + '\'><a>' + data[i].apiGroupName + '</a></li>';
                        htmlStr_edit += '<option group-id=\'' + data[i].apiGroupId + '\'>' + data[i].apiGroupName + '</option>';
                    }
                    $('.apiGroupSelector_search').empty().html(htmlStr_search); // 资源表搜索栏
                    $('#apiGroupSelector_edit').empty().html(htmlStr_edit); // 弹框下拉框
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            },
            complete: function () {
                // 绑定事件
                $('.apiGroupSelector_search>li').unbind('click').on('click', function () {
                    $(this).parent().siblings('.form-control').val($(this).first().text());
                });
            }
        });
    },
    // 初始化接口类型
    initApiType: function () {
        $.ajax({
            url: webpath + '/api/apiType',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.length > 0) {
                    var htmlStr = '';
                    for (var i = 0; i < data.length; i++) {
                        htmlStr += '<option type-key=\'' + data[i].key + '\'>' + data[i].text + '</option>';
                    }
                    $('#apiTypeSelector').empty().html(htmlStr);
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            },
            complete: function () {
                $('#apiTypeSelector option').first().prop('selected', true);
                apiModule.initApiAlertForm($('#apiTypeSelector option').first().attr('type-key'));
            }
        });
    },
    // 根据接口类型切换表单内容
    initApiAlertForm: function (apiTypeKey) {
        if (apiTypeKey) {
            $('.apiTypeContent').removeClass('apiTypeContent_active').addClass('apiTypeContent_init');
            $(".apiTypeContent[apiTypeKey='" + apiTypeKey + "']").removeClass('apiTypeContent_init').addClass('apiTypeContent_active');
            apiModule.initRedisForm($('#redisTypeSelector option').first().attr('type-key'));
        }
    },
    // 根据redis类型切换表单内容
    initRedisForm: function (redisTypeKey) {
        if (redisTypeKey) {
            $('.redisTypeContent').removeClass('redisTypeContent_active').addClass('redisTypeContent_init');
            $(".redisTypeContent[redisTypeKey='" + redisTypeKey + "']").removeClass('redisTypeContent_init').addClass('redisTypeContent_active');
        }
    },
    // 新建接口时数据维护
    initData: function () {
        // 初始化记录数据缓存属性
        $('#apiAlertModal')
            .attr('param', '[]')
            .attr('newParam', '[]')
            .attr('oldParamId', [])
            .attr('newParamId', [])
            .attr('oldReturnParamId', '')
            .attr('newReturnParamId', '')
            .attr('returnObj');
        // .attr('returnObj', {});
        // 初始化参数表格, 新建时参数列表为空
        paramsTableModal.initParamsTable($('#apiAlertModal'), '1', '', [], apiModule.hidden, apiModule.updateAddParam);
    },
    // 添加参数后更新弹框缓存, 重新刷新加载参数表格
    updateAddParam: function () {
        var updateTable = ($('#apiAlertModal').attr('paramAddType_isTable') == '1') ? true : false;
        if (updateTable) {
            // 更新参数,刷新参数表格
            var newParam = [];
            if ($('#apiAlertModal').attr('newParam')) {
                newParam = JSON.parse($('#apiAlertModal').attr('newParam'));
            }
            paramsTableModal.initParamsTable($('#apiAlertModal'), '1', '', newParam, apiModule.hidden, apiModule.updateAddParam);
        } else {
            // 刷新返回值相关
            if ($('#apiAlertModal').attr('returnObj') && $('#apiAlertModal').attr('returnObj') !== '{}') {
                var returnObj = JSON.parse($('#apiAlertModal').attr('returnObj'));
                $("#returnValueTypeSelector option[returnValueType='" + returnObj.returnValueType + "']").prop('selected', true);
                if (returnObj.returnValue) {
                    $('#returnValue').val(returnObj.returnValue);
                }
            } else if ($('#apiAlertModal').attr('returnObj') === '{}') {
                $("#returnValueTypeSelector option:first").prop('selected', true);
                $('#returnValue').val('');
            }
        }
        apiModule.open();
    },
    // 接口数据回显
    echoData: function (apiId) {
        if (apiId) {
            var data = JSON.parse($("#row_" + apiId).text()); // 接口全部数据
            var paramObj = JSON.parse(data.apiContent); // 接口apiContent数据
            for (var key in data) {
                if (key === 'apiId') { // 接口id记录至弹框
                    $('#apiAlertModal').attr('apiId', data[key]);
                    continue;
                }
                if (key === 'apiGroupId') { // 接口组单独处理
                    $('#apiGroupSelector_edit option[group-id="' + data[key] + '"]').prop('selected', true);
                    continue;
                }
                if (key === 'isLog') { // 是否记录日志单独处理
                    $('#isLogSelector option[is-log="' + data[key] + '"]').prop('selected', true);
                    continue;
                }
                if (key === 'apiType') { // 接口类型及展示字段单独处理 http/redis
                    var apiType = data[key];
                    $('#apiTypeSelector option').removeAttr('selected');
                    $('#redisTypeSelector option').removeAttr('selected');
                    $('#apiTypeSelector option[type-key="' + apiType + '"]').prop('selected', true);
                    apiModule.initApiAlertForm(apiType); // 动态加载当前选项应该展示的字段内容

                    if (apiType == 'http') { // HTTP (有参数绑定)

                        $("#httpTypeSelector option[http-type='" + paramObj.httpType + "']").prop('selected', true); // get/post
                        $('#urlTextArea').val(paramObj.url);
                        $("#returnValueTypeSelector option[returnValueType='" + paramObj.returnValueType + "']").prop('selected', true);
                        $('#returnValue').empty().val(paramObj.returnValue);
                        $("#returnValueFormatSelector option[format='" + paramObj.returnValueFormat + "']").prop('selected', true);

                        // 数据缓存记录回显
                        var returnObj = {};
                        returnObj['isPublic'] = '1';
                        returnObj['variableId'] = paramObj.newReturnParamId;
                        returnObj['returnValue'] = paramObj.returnValue;
                        returnObj['returnValueType'] = paramObj.returnValueType;

                        $('#apiAlertModal')
                            .attr('param', JSON.stringify(paramObj.param) || [])
                            // .attr('newParam', JSON.stringify(paramObj.newParam) || []) // 不从列表数据里取, 重新访问接口从接口取
                            .attr('oldParamId', paramObj.newParamId || [])
                            .attr('newParamId', paramObj.newParamId || [])
                            .attr('oldReturnParamId', paramObj.newReturnParamId || '')
                            .attr('newReturnParamId', paramObj.newReturnParamId || '')
                            .attr('returnObj', JSON.stringify(returnObj));

                        // 回显接口参数列表
                        var newParam = [];
                        if ($('#apiAlertModal').attr('newParam')) {
                            newParam = JSON.parse($('#apiAlertModal').attr('newParam'));
                        }
                        paramsTableModal.initParamsTable($('#apiAlertModal'), '1', '', newParam, apiModule.hidden, apiModule.updateAddParam);

                    } else if (apiType == 'redis') { // REDIS (无参数绑定)
                        var redisType = 'redis'; // redisType 根据参数字段去判断
                        if (paramObj.zk || paramObj.zkAddr || paramObj.codisKey || paramObj.codisAuth) {
                            redisType = 'codis';
                        } else if (paramObj.redisIp || paramObj.redisPort || paramObj.redisKey || paramObj.redisAuth) {
                            redisType = 'redis';
                        }
                        $("#redisTypeSelector option[type-key='" + redisType + "']").prop('selected', true);
                        apiModule.initRedisForm(redisType); // 动态加载form表单展示内容
                        for (var key in paramObj) { // redis数据回显
                            var redisTarget = $(".redisTypeContent .form-control[col-name='" + key + "']");
                            if (redisTarget.length > 0) {
                                $(redisTarget).val(paramObj[key]);
                            }
                        }
                    }
                    continue;
                }

                var target = $("#apiAlertModal .form-control[col-name='" + key + "']");
                if (target.length > 0) {
                    $(target).val(data[key]);
                }
            }
        }
    },
    // 保存接口
    saveApi: function () {
        // 表单验证
        if (!$('#apiAlertModal #baseForm').isValid()) {
            return;
        }
        var apiType = $('#apiTypeSelector option:selected').attr('type-key');
        if (apiType == 'http') {
            if (!$('#apiAlertModal #httpForm').isValid()) {
                return;
            }
        } else if (apiType == 'redis') {
            var redisType = $('#redisTypeSelector option:selected').attr('type-key');
            if (redisType == 'redis') {
                if (!$('#apiAlertModal #redisForm').isValid()) {
                    return;
                }
            } else if (redisType == 'codis') {
                if (!$('#apiAlertModal #codisForm').isValid()) {
                    return;
                }
            }
        }

        var handleType = $('#apiAlertModal').attr('handleType'); // 0新增 1修改
        var apiId = $('#apiAlertModal').attr('apiId');
        var urlStr = '';
        var obj = {};
        if (handleType == 0) {
            urlStr = '/api/pub/save';
            obj = apiModule.getApiData(handleType, apiId);
            apiModule.saveApiAjax(urlStr, obj, true);
        } else if (handleType == 1) {
            urlStr = '/api/pub/update';
            // 接口引用状态下进行修改需要提醒
            var usedFlag = $('#apiAlertModal').attr('usedFlag');
            if (usedFlag == '0') {
                // 获取表单数据
                obj = apiModule.getApiData(handleType, apiId);
                if (obj) {
                    apiModule.saveApiAjax(urlStr, obj, true);
                }
            } else if (usedFlag == '1') {
                apiModule.hidden();
                confirmAlert.show(
                    '接口被引用中, 是否仍要修改？',
                    function () {
                        obj = apiModule.getApiData(handleType, apiId);
                        if (obj) {
                            apiModule.saveApiAjax(urlStr, obj, true);
                        }
                    },
                    function () {
                        apiModule.open();
                    }
                );
            }
        }
    },
    // 发送保存接口请求
    saveApiAjax: function (urlStr, obj, closeFlag) {
        // 发请求
        $.ajax({
            url: webpath + urlStr,
            type: 'POST',
            dataType: "json",
            data: obj,
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('保存成功');
                    // initTable('2');
                    $('.searchBtn').trigger('click');
                    if (closeFlag) {
                        $('#apiAlertModal').modal('hide');
                    }
                } else {
                    failedMessager.show(data.msg);
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            }
        });
    },
    // 获取接口表单数据
    getApiData: function (handleType, apiId) {
        var obj = {};
        // folderId 场景下需要
        if (handleType == 1 && apiId != '') { // 修改带id
            obj['apiId'] = apiId;
        }
        obj['apiGroupId'] = $('#apiGroupSelector_edit option:selected').attr('group-id'); //接口组
        obj['apiGroupName'] = $('#apiGroupSelector_edit').val();
        obj['isLog'] = $('#isLogSelector option:selected').attr('is-log'); //是否记录日志
        obj['apiType'] = $('#apiTypeSelector option:selected').attr('type-key'); //接口类型
        obj['apiDesc'] = $.trim($('#apiDescTextArea').val());
        obj['apiName'] = $.trim($('#apiNameInput').val());
        obj['isPublic'] = '1';
        obj['apiContent'] = '';

        // 根据接口类型拼接obj
        if (obj.apiType == 'http') {
            var httpObj = {};
            httpObj['httpType'] = $('#httpTypeSelector option:selected').attr('http-type');
            httpObj['url'] = $.trim($('#urlTextArea').val());
            httpObj['returnValue'] = $('#returnValue').val();
            httpObj['returnValueType'] = $('#returnValueTypeSelector option:selected').attr('returnValueType');
            httpObj['returnValueFormat'] = $('#returnValueFormatSelector option:selected').attr('format');
            // httpObj['param'] = $('#apiAlertModal').attr('param'); //arr
            httpObj['param'] = JSON.parse($('#apiAlertModal').attr('param') || '[]'); //arr
            httpObj['newParam'] = JSON.parse($('#apiAlertModal').attr('newParam') || '[]'); //arr
            httpObj['oldParamId'] = $('#apiAlertModal').attr('oldParamId'); //arr
            httpObj['newParamId'] = $('#apiAlertModal').attr('newParamId'); //arr
            httpObj['oldReturnParamId'] = $('#apiAlertModal').attr('oldReturnParamId');
            httpObj['newReturnParamId'] = $('#apiAlertModal').attr('newReturnParamId');
            obj['apiContent'] = JSON.stringify(httpObj);
        } else if (obj.apiType == 'redis') {
            var redisType = $('#redisTypeSelector option:selected').attr('type-key');
            var redisObj = {};
            var redisInputs = [];
            if (redisType == 'redis') { // 单节点
                redisInputs = $(".redisTypeContent[redisTypeKey='redis'] .form-control");
            } else if (redisType == 'codis') { // 集群
                redisInputs = $(".redisTypeContent[redisTypeKey='codis'] .form-control");
            }
            for (var i = 0; i < redisInputs.length; i++) {
                var colName = $(redisInputs[i]).attr('col-name');
                if (colName) {
                    redisObj[colName] = $.trim($(redisInputs[i]).val());
                }
            }
            obj['apiContent'] = JSON.stringify(redisObj);
        }
        return obj;
    },
    // 删除接口
    deleteApi: function (apiId) {
        if (apiId) {
            // 删除权限校验
            $.ajax({
                url: webpath + '/api/delete/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {'apiId': apiId},
                success: function (data) {
                    if (data.status === 0) {
                        confirmAlert.show('是否确认删除？', function () {
                            // 校验是否正在使用中
                            $.ajax({
                                url: webpath + '/api/checkUsed',
                                type: 'POST',
                                dataType: "json",
                                data: {'apiId': apiId},
                                success: function (data) {
                                    if (data.status === 0) {
                                        // 删除接口
                                        $.ajax({
                                            url: webpath + '/api/pub/delete',
                                            type: 'POST',
                                            dataType: "json",
                                            data: {'apiId': apiId},
                                            success: function (data) {
                                                if (data.status === 0) {
                                                    successMessager.show('删除成功');
                                                    // initTable('2');
                                                    $('.searchBtn').trigger('click');
                                                } else {
                                                    failedMessager.show(data.msg);
                                                }
                                            }
                                        });
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
    },
    // 展示添加接口组弹框
    showAddTypeAlert: function (handleType, $this) {
        var detail = {};
        if ($this) {
            var curRow = $this.parentNode.parentNode;
            detail = $('#paramGroupTable').DataTable().row(curRow).data();
        }
        // handleType: 0新增 1修改 2查看
        $('#apiTypeAlertModal form')[0].reset();
        $('#apiTypeAlertModal .modal-footer button').css('display', 'none');
        if (handleType == 0) {
            $.ajax({
                url: webpath + '/createCheck/check',
                type: 'GET',
                data: {'apiGroupId': detail['apiGroupId']? detail['apiGroupId'] : ''},
                dataType: "json",
                success: function (data) {
                    if (data.status === 0) {
                        $('#apiTypeAlertModal').attr('handleType', handleType);
                        $('#apiTypeAlertModal').modal({'show': 'center', "backdrop": "static"});
                        $('#apiTypeAlertModal .modal-footer .notView button').css('display', 'inline-block');
                        $('#apiTypeAlertModal .modal-title').empty().text('添加接口组');
                        $('#apiTypeAlertModal .form-control').removeAttr('disabled');
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType == 1) {
            $.ajax({
                url: webpath + '/api/group/update/checkAuth',
                type: 'GET',
                data: {'apiGroupId': detail['apiGroupId']? detail['apiGroupId'] : ''},
                dataType: "json",
                success: function (data) {
                    debugger;
                    if (data.status === 0) {
                        $('#apiTypeAlertModal').attr('handleType', handleType);
                        $('#apiTypeAlertModal').modal({'show': 'center', "backdrop": "static"});
                        $('#apiTypeAlertModal .modal-footer .notView button').css('display', 'inline-block');
                        $('#apiTypeAlertModal .modal-title').empty().text('修改接口组');
                        $('#apiTypeAlertModal .form-control').removeAttr('disabled');
                        apiModule.echoGroupData(detail);
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        } else if (handleType == 2) {
            $('#apiTypeAlertModal').attr('handleType', handleType);
            $('#apiTypeAlertModal').modal({'show': 'center', "backdrop": "static"});
            $('#apiTypeAlertModal .modal-footer #closeViewApiGroup').css('display', 'inline-block');
            $('#apiTypeAlertModal .modal-title').empty().text('查看接口组');
            $('#apiTypeAlertModal .form-control').attr('disabled', true);
            apiModule.echoGroupData(detail);
        }
    },
    // 关闭添加接口组弹框
    hiddenAddTypeAlert: function () {
        $('#apiTypeAlertModal').modal('toggle', 'center');
    },
    // 保存接口组
    saveApiGroup: function () {
        var mustInputs = $('#apiTypeAlertModal form .must');
        for (var i = 0; i < mustInputs.length; i++) {
            if ($(mustInputs[i]).val() == '') {
                failedMessager.show('请填入必填项！');
                $(mustInputs[i]).focus();
                return;
            }
        }
        var handleType = $('#apiTypeAlertModal').attr('handleType'); // 0新增 1修改
        var urlStr = '';
        if (handleType == '0') {
            urlStr = '/api/pub/group/save';
        } else if (handleType == '1') {
            urlStr = '/api/group/update';
        } else {
            return;
        }
        var obj = apiModule.getGroupData(handleType);
        if (obj) {
            $.ajax({
                url: webpath + urlStr,
                type: 'POST',
                dataType: "json",
                data: obj,
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('保存成功');
                        // initGroupTable('2');
                        // initTable('2');
                        $('.searchBtn').trigger('click');
                        apiModule.hiddenAddTypeAlert();
                        apiModule.initApiGroup(); // 刷新接口组下拉框
                        apiModule.initApiType(); // 刷新接口类型下拉框
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
    // 接口组数据回显
    echoGroupData: function (detail) {
        if (detail) {
            var data = detail ? detail : {};
            for (var key in data) {
                if (key === 'apiGroupId') { // 回显组id
                    $('#apiTypeAlertModal').attr('groupId', data[key]);
                    continue;
                }
                var target = $("#apiTypeAlertModal .form-control[col-name='" + key + "']");
                if (target.length > 0) {
                    $(target).val(data[key]);
                }
            }
        }
    },
    // 获取组表单数据
    getGroupData: function (handleType) {
        var obj = {};
        var inputs = $('#apiTypeAlertModal form .form-control');
        for (var i = 0; i < inputs.length; i++) {
            obj[$(inputs[i]).attr('col-name')] = $(inputs[i]).val();
        }
        if (handleType == 1) { //修改需要加上参数组id
            obj['apiGroupId'] = $('#apiTypeAlertModal').attr('groupId');
        }
        return obj;
    },
    // 删除接口组
    deleteApiGroup: function (groupId) {
        if (groupId) {
            $.ajax({ // 删除权限校验
                url: webpath + '/api/group/delete/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {'apiGroupId': groupId},
                success: function (data) {
                    if (data.status === 0) {
                        confirmAlert.show('是否确认删除？', function () {
                            $.ajax({
                                url: webpath + '/api/group/delete',
                                type: 'POST',
                                dataType: "json",
                                data: {'apiGroupId': groupId},
                                success: function (data) {
                                    if (data.status === 0) {
                                        successMessager.show('删除成功');
                                        // initGroupTable('2');
                                        // initTable('2');
                                        $('.searchBtn').trigger('click');
                                        apiModule.initApiGroup(); // 刷新接口组下拉框
                                        apiModule.initApiType(); // 刷新接口类型下拉框
                                    } else {
                                        failedMessager.show(data.msg);
                                    }
                                },
                                error: function (data) {
                                    failedMessager.show(data.msg);
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
 *  公共函数 暂不支持
 */
var funModule = {
    // 展示
    show: function (handleType) {
        // 0新增 1修改
        if (handleType == 0) {
            $('#functionTitle').text('添加函数');
            // 展示添加函数类别
            $('#addFuncTypeDiv').removeClass().addClass('col-xs-5 col-sm-5 col-md-5 col-lg-5');
            $('#addFunTypeButton').css('display', 'block');
            $('#saveFun').css('display', 'inline-block');
            // 可编辑参数操作栏
            $('#funcParamDivParent_edit').css('display', 'block');
            $('.funcParamDel').css('display', 'inline');
            $('#funcParamDivParent_look>label').text('');
            $('#funcParamDivParent_look').removeClass('funcParamDivParent_edit').addClass('funcParamDivParent_look');
        } else if (handleType == 1) {
            $('#functionTitle').text('修改函数');
            // 隐藏添加函数类别
            $('#addFuncTypeDiv').removeClass().addClass('col-xs-7 col-sm-7 col-md-7 col-lg-7');
            $('#addFunTypeButton, #saveFun').css('display', 'none');
            // 查看参数操作栏
            $('#funcParamDivParent_edit').css('display', 'none');
            $('.funcParamDel').css('display', 'none');
            $('#funcParamDivParent_look>label').text('参数列表');
            $('#funcParamDivParent_look').removeClass('funcParamDivParent_look').addClass('funcParamDivParent_edit');
        }
        $('#functionAlertModal').attr('handleType', handleType).modal({'show': 'center', "backdrop": "static"});
    },
    // 关闭弹框
    hidden: function () {
        $('#functionAlertModal form')[0].reset();
        $('#functionAlertModal').modal('toggle', 'center');
    },
    // 展示添加函数类别弹框
    showAddFunTypeAlert: function () {
        $('#funTypeAlertModal form')[0].reset();
        $('#functionAlertModal').modal('toggle', 'center');
        $('#funTypeAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 关闭添加函数类别弹框
    hiddenAddFunTypeAlert: function () {
        $('#funTypeAlertModal').modal('toggle', 'center');
        $('#functionAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
}

function initPage() {
    // 为tab绑定事件切换tab数据
    $('#commonResourceTab li').click(function () {
        // 1. 销毁资源表格并重新绘制
        var resourceTable = $('#commonResourceTable').dataTable();
        resourceTable.fnClearTable(); // 清空表格数据
        resourceTable.fnDestroy(); // 销毁dataTable
        $('#commonResourceTable').remove();
        $('#commonResourceContent').append('<table id="commonResourceTable"></table>');

        // 2. 销毁组表格并重新绘制
        var groupTable = $('#paramGroupTable').dataTable();
        groupTable.fnClearTable(); // 清空表格数据
        groupTable.fnDestroy(); // 销毁dataTable
        $('#paramGroupTable').remove();
        $('#resourceGroupContent').append('<table id="paramGroupTable"></table>');

        // 3. 清空搜索框内容
        var inputs = $('#commonResourceContainer .input-group .form-control');
        for (var i = 0; i < inputs.length; i++) {
            $(inputs[i]).val('');
        }

        var typeId = $(this).attr('tab-id');
        initTable(typeId); // 重新加载资源表格
        initGroupTable(typeId); // 重新加载组表格
        initBar(typeId); // 刷新搜索条件和添加按钮内容
    });

    // 时间选择插件: 选择时间和日期
    $(".form-datetime").datetimepicker(
        {
            weekStart: 1,
            todayBtn: 1,
            autoclose: 1,
            todayHighlight: 1,
            startView: 2,
            forceParse: 0,
            showMeridian: 1,
            format: "yyyy-mm-dd hh:ii:ss"
        });

    // 搜索
    $('.searchBtn').click(function () {
        searchData($(this).attr('tableType'));
    });

    initParamPage();
    initApiPage();
    initFunPage();
}

/* 参数 页面初始化 */
function initParamPage() {
    // 关闭参数弹框
    $('#closeParamModal').click(function () {
        paramModule.hidden();
    });

    // 保存参数
    $('#saveParam').click(function () {
        paramModule.saveParam();
    });

    // 添加对象属性
    $('#addObjectParam').click(function () {
        objectParam.addObjectParam();
    });

    // 参数属性弹框完成按钮绑定事件(层层进/退)
    $('#closeObjectParamAlert').click(function () {
        objectParam.closeObjParamModal();
    });

    // 参数属性弹框关闭按钮绑定事件(完全退出)
    $('#closeAllObjectParamAlert').click(function () {
        objectParam.closeAllObjParamModal();
    });

    // 关闭参数组弹框
    $('#cancelParamGroup').click(function () {
        paramModule.hiddenAddGroupAlert();
    });

    // 保存参数组
    $('#saveParamGroup').click(function () {
        paramModule.saveParamGroup();
    });

    // 参数种类搜索
    $('.kindIdSel li').click(function () {
        $('.kindIdInput').val($(this).text()).attr('kindId', $(this).attr('value'));
    });

    // 参数默认值
    $('#paramAlertModal .variableKindSelector').change(function () {
        var kindId = $('#paramAlertModal .variableKindSelector option:selected').attr('value');
        if (kindId === 'K4') { // 只有系统常量可以设置默认值
            $('#paramAlertModal .defaultValueGroup').removeClass('hide');
        } else {
            $('#paramAlertModal .defaultValueGroup').addClass('hide');
        }
    });
}

/* 接口 页面初始化 */
function initApiPage() {
    // 关闭接口弹框
    $('#closeApiModal').click(function () {
        apiModule.hidden();
    });

    // 保存接口
    $('#saveApi').click(function () {
        apiModule.saveApi();
    });

    // 保存接口组
    $('#saveApiType').click(function () {
        apiModule.saveApiGroup();
    });

    // 接口类型切换事件 http/redis
    $('#apiTypeSelector').on('change', function () {
        apiModule.initApiAlertForm($('#apiTypeSelector option:selected').attr('type-key'));
    });

    // redis类型切换事件 redis/codis
    $('#redisTypeSelector').on('change', function () {
        apiModule.initRedisForm($('#redisTypeSelector option:selected').attr('type-key'));
    });

    // 返回值选择参数
    $('#addApiReturnParam').click(function () {
        // 标识为返回值添加参数
        $('#apiAlertModal').attr('paramAddType_isTable', '0');
        var returnObj = $('#apiAlertModal').attr('returnObj');
        paramAddModal.initParamAddPage($('#apiAlertModal'), '2', '', apiModule.hidden, apiModule.updateAddParam, returnObj);
    });

}

/* 函数 页面初始化 */
function initFunPage() {
    // 关闭函数弹框
    $('#closeFunModal').click(function () {
        funModule.hidden();
    });

    // 展示添加函数类别弹框
    $('#addFunTypeButton').click(function () {
        funModule.showAddFunTypeAlert();
    });

    // 关闭添加函数类别弹框
    $('#cancelFunType').click(function () {
        funModule.hiddenAddFunTypeAlert();
    });
}

/* 搜索功能 */
function searchData(tableType) {
    // tableType: 0资源表格 1组表格
    var inputs;
    if (tableType == '0') {
        inputs = $('#commonResourceSearchContainer .input-group .form-control');
    } else if (tableType == '1') {
        inputs = $('#paramGroupDiv .input-group .form-control');
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

    if (obj != {}) {
        if (obj.kindId) {
            obj.kindId = $('.kindIdInput').attr('kindId');
        }
        if (obj.variableTypeId) {
            obj.variableTypeId = $('.variableTypeInput').attr('typeId');
        }
        var tabId = $('#commonResourceTab').children('.active').attr('tab-id');
        if (tableType == '0') {
            initTable(tabId, obj);
        } else if (tableType == '1') {
            initGroupTable(tabId, obj);
        }
    }
}

/* 初始化工具栏 搜索内容 按钮内容 绑定添加按钮事件 */
function initBar(tabId) {
    $('.special .input-group').css('display', 'none');
    if (tabId == '0') { // 公共参数
        // 1. 搜索栏部分
        paramModule.initParamGroup(); // 初始化参数组下拉框: 搜索栏*2/弹框
        paramModule.initVariable(); // 初始化弹框变量类型
        // 操作按钮修改
        $('.special .common_params').css('display', 'block');
        $('#addButton').text('添加参数').unbind('click').bind('click', function () {
            paramModule.show(0);
        });
        // 2. 组部分
        $($('.resourceTableIcon i')[0]).text(' 公共参数');
        $($('.resourceTableIcon i')[1]).text(' 参数组');
        $('#addGroupButton').text('添加参数组').unbind('click').click(function () {
            paramModule.showAddGroupAlert(0);
        });
    } else if (tabId == '1') { // 公共函数
        $('.special .common_functions').css('display', 'block');
        $('#addButton').text('添加函数').unbind('click').bind('click', function () {
            funModule.show(0);
        });
    } else if (tabId == '2') { // 公共接口
        // 1. 搜索栏部分
        apiModule.initApiGroup(); // 初始化接口组下拉框
        apiModule.initApiType(); // 初始化接口类型下拉框
        $('.special .common_api').css('display', 'block');
        $('#addButton').text('添加接口').unbind('click').bind('click', function () {
            apiModule.show(0);
        });
        // 2. 组部分
        $($('.resourceTableIcon i')[0]).text(' 公共接口');
        $($('.resourceTableIcon i')[1]).text(' 接口组');
        $('#addGroupButton').text('添加接口组').unbind('click').click(function () {
            apiModule.showAddTypeAlert(0);
        });
    }
}

/**
 *  参数: 对象类型参数-属性表格
 */
function initObjectPropertyTable(entityId) {
    $('#objParamsTable').width('100%').dataTable({
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
            {"title": "变量别名", "data": "variableAlias"},
            {"title": "变量编码", "data": "variableCode"},
            // {"title": "编码别名", "data": "entityVariableAlias"},
            {"title": "变量类型", "data": "typeValue"},
            {"title": "变量种类", "data": "kindValue"},
            {"title": "默认值", "data": "defaultValue"},
            {
                "title": "操作", "data": null, "render": function (data, type, row) {
                    var htmlStr = "";
                    var varId = row.variableId;
                    var isObject = (row.typeId == '3') ? true : false;
                    var entityId = '';
                    var entityName = '';
                    var groupId = '';

                    htmlStr += '<span type="button" class="cm-tblB" onclick="paramModule.viewParam(1, this)">查看</span>';
                    htmlStr += '<span type="button" class="cm-tblB" onclick="paramModule.show(3, this)" >修改</span>';
                    if (isObject) {
                        entityId = row.entityId;
                        entityName = row.variableAlias;
                        groupId = row.variableGroupId;
                        htmlStr += '<span type="button" class="cm-tblB detailBtn" onclick="objectParam.init(\'' + entityId + '\', \'' + entityName + '\', \'' + groupId + '\', true, \'' + row.kindId + '\')">属性</span>';
                    }
                    if (isObject) {
                        htmlStr += '<span type="button" class="cm-tblC delBtn" onclick="objectParam.deleteObjectParam(\'' + varId + '\', true, \'' + entityId + '\')">删除</span>';
                    } else {
                        htmlStr += '<span type="button" class="cm-tblC delBtn" onclick="objectParam.deleteObjectParam(\'' + varId + '\', false, \'' + entityId + '\')">删除</span>';
                    }
                    return htmlStr;
                }
            }
        ],
        ajax: {
            url: webpath + '/variable/queryVariableRelation',
            "type": 'GET',
            "data": function (d) { // 查询参数
                return $.extend({}, d, {'entityId': entityId});
            }
        },
        "fnDrawCallback": function (oSettings, json) {
            // $("tr:even").css("background-color", "#fbfbfd");
            // $("table:eq(0) th").css("background-color", "#f6f7fb");
        }
    });
}

/**
 * 资源table
 */

/* 初始化资源表格 typeId:日志类型; obj:搜索条件 */
function initTable(tabId, obj) {
    var titles = initTitles(tabId); // 获取表头
    var url = initUrl(tabId); // 获取url
    obj == null ? '' : obj;
    var pageLength = 10; //每页显示条数
    $.extend($.fn.dataTable.defaults, {
        info: true,
        "serverSide": true,
        "pageLength": pageLength
    });
    $('#commonResourceTable').width('100%').dataTable({
        "searching": false, // 是否开启搜索功能
        "ordering": false,
        "destroy": true,
        "bLengthChange": false,
        "pagingType": "full_numbers",
        "paging": true,
        "info": true,
        "serverSide": true,
        "pageLength": pageLength,
        "columns": titles, // 不能为空
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
        }
    });
}

/* 获取资源表格表头 */
function initTitles(tabId) {
    // 公共参数
    var paramCols = [
        {"title": "参数组", "data": "variableGroupName", "width": "10%"},
        {"title": "变量别名", "data": "variableAlias", "width": "15%"},
        {"title": "变量类型", "data": "typeValue", "width": "10%"},
        {"title": "变量种类", "data": "kindValue", "width": "10%"},
        {"title": "描述", "data": "variableRemarks", "width": "10%"},
        {"title": "创建人", "data": "createPerson", "width": "10%"},
        {"title": "创建时间", "data": "createDate", "width": "15%"},
        {
            "title": "操作", "data": null, "width": "20%", "render": function (data, type, row) {
                var htmlStr = "";
                var varId = row.variableId;
                var isObject = (row.typeId == '3') ? true : false;
                var entityId = '';
                var entityName = '';

                htmlStr += '<span type="button" class="cm-tblB" onclick="paramModule.viewParam(0, this)">查看</span>';
                htmlStr += '<span type="button" class="cm-tblB" onclick="paramModule.show(1, this)">修改</span>';
                if (isObject) {
                    entityId = row.entityId;
                    entityName = row.variableAlias;
                    htmlStr += '<span group-id=\'' + row.variableGroupId + '\' type="button" class="cm-tblB detailBtn" onclick="objectParam.init(\'' + entityId + '\', \'' + entityName + '\', \'' + row.variableGroupId + '\', false, \'' + row.kindId + '\', \'' + varId + '\')">属性</span>';
                }

                if (isObject) {
                    htmlStr += '<span type="button" class="cm-tblC delBtn" onclick="paramModule.deleteParam(\'' + varId + '\', true, \'' + entityId + '\')">删除</span>';
                } else {
                    htmlStr += '<span type="button" class="cm-tblC delBtn" onclick="paramModule.deleteParam(\'' + varId + '\', false, \'' + entityId + '\')">删除</span>';
                }
                return htmlStr;
            }
        }
    ];
    // 公共接口
    var apiCols = [
        {"title": "接口组", "data": "apiGroupName", "width": "10%"},
        {"title": "接口名称", "data": "apiName", "width": "15%"},
        {
            "title": "接口类型", "data": "apiType", "width": "6%", "render": function (data) {
                switch ($.trim(data)) {
                    case 'http':
                        return 'HTTP';
                    case 'redis':
                        return 'REDIS';
                    case 'soap':
                        return 'SOAP';
                    default:
                        return '--';
                }
            }
        },
        {
            "title": "是否记录日志", "data": "isLog", "width": "8%", "render": function (data) {
                switch ($.trim(data)) {
                    case '1':
                        return '是';
                    case '0':
                        return '否';
                    default:
                        return '--';
                }
            }
        },
        {"title": "接口描述", "data": "apiDesc", "width": "15%"},
        {"title": "创建人", "data": "createPersion", "width": "9%"},
        {"title": "创建时间", "data": "createDate", "width": "12%"},
        {
            "title": "操作", "data": null, "width": "25%", "render": function (data, type, row) {
                var htmlStr = '<div>';
                htmlStr += '<div id="row_' + row.apiId + '" style="display:none;">' + JSON.stringify(row) + '</div>';
                htmlStr += '<span class="cm-tblB" onclick="apiModule.show(2,\'' + row.apiId + '\');" >查看</span>';
                htmlStr += '<span class="cm-tblB" onclick="apiModule.show(1,\'' + row.apiId + '\');" >修改</span>';
                htmlStr += '<span class="cm-tblC delBtn" onclick="apiModule.deleteApi(\'' + row.apiId + '\')">删除</span>';
                htmlStr += '</div>';
                $("#row_" + row.apiId).data("rowData", row);
                return htmlStr;
            }
        }
    ];
    // 公共函数
    var functionCols = [
        {"title": "函数名称", "data": ""},
        {"title": "函数描述", "data": ""},
        {
            "title": "返回值类型", "data": "", "render": function (data) {
                switch ($.trim(data)) {
                    case '1':
                        return '执行中';
                    case '2':
                        return '执行完成';
                    case '-1':
                        return '异常';
                    default:
                        return '--';
                }
            }
        },
        {"title": "函数返回值", "data": ""},
        {"title": "函数类类型", "data": ""},
        {"title": "类路径", "data": ""},
        {"title": "类名", "data": ""},
        {"title": "方法名", "data": ""},
        {"title": "创建时间", "data": ""},
        {
            "title": "操作", "data": null, "render": function (data, type, row) {
                var htmlStr = "";
                htmlStr += '<span type="button" class="cm-tblB" onclick="funModule.show(1)">修改</span>';
                htmlStr += '<span type="button" class="cm-tblC delBtn">删除</span>';
                return htmlStr;
            }
        }
    ];
    switch ($.trim(tabId)) {
        case '0':
            return paramCols;
        case '1':
            return functionCols;
        case '2':
            return apiCols;
    }
}

/* 匹配后台接口url */
function initUrl(typeId) {
    // 0公共参数 1公共函数 2公共接口
    switch ($.trim(typeId)) {
        case '0':
            return '/variable/pub/list';
        case '1':    // 公共函数暂不支持
            return '/log/operate';
        case '2':
            return '/api/pub/list';
    }
}

/**
 * 组table
 */

/* 初始化组表格 typeId:日志类型; obj:搜索条件 */
function initGroupTable(tabId, obj) {
    // tabId: 0参数 1函数 2接口
    var titles = initGroupTitles(tabId); // 获取表头
    var url = initGroupUrl(tabId); // 获取url
    obj == null ? '' : obj;
    $('#paramGroupTable').width('100%').dataTable({
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

/* 获取组表格表头 */
function initGroupTitles(tabId) {
    // 参数组
    var paramGroupCols = [
        {"title": "参数组名称", "data": "variableGroupName", "width": "25%"},
        {"title": "创建人", "data": "createPerson", "width": "25%"},
        {"title": "创建时间", "data": "createDate", "width": "25%"},
        {
            "title": "操作", "data": null, "width": "25%", "render": function (data, type, row) {
                var htmlStr = "";
                var groupId = row.variableGroupId;
                htmlStr += '<span type="button" class="cm-tblB detailBtn" onclick="paramModule.showAddGroupAlert(2, this)">查看</span>';
                htmlStr += '<span type="button" class="cm-tblB" onclick="paramModule.showAddGroupAlert(1, this)">修改</span>';
                htmlStr += '<span type="button" class="cm-tblC delBtn" onclick="paramModule.deleteGroup(\'' + groupId + '\')">删除</span>';
                return htmlStr;
            }
        }
    ];
    // 接口组
    var apiGroupCols = [
        {"title": "接口组名称", "data": "apiGroupName", "width": "25%"},
        {"title": "创建人", "data": "createPerson", "width": "25%"},
        {"title": "创建时间", "data": "createDate", "width": "25%"},
        {
            "title": "操作", "data": null, "width": "25%", "render": function (data, type, row) {
                var htmlStr = "";
                var groupId = row.apiGroupId;
                htmlStr += '<span type="button" class="cm-tblB" onclick="apiModule.showAddTypeAlert(2, this)">查看</span>';
                htmlStr += '<span type="button" class="cm-tblB" onclick="apiModule.showAddTypeAlert(1, this)">修改</span>';
                htmlStr += '<span type="button" class="cm-tblC delBtn" onclick="apiModule.deleteApiGroup(\'' + groupId + '\')">删除</span>';
                return htmlStr;
            }
        }
    ];
    switch ($.trim(tabId)) {
        case '0':
            return paramGroupCols;
        case '2':
            return apiGroupCols;
    }
}

/* 匹配组表格后台接口url */
function initGroupUrl(tabId) {
    // 0公共参数 1公共函数 2公共接口
    switch ($.trim(tabId)) {
        case '0':
            return '/variable/pub/group/page/list';
        case '2':
            return '/api/pub/group/page/list';
    }
}

$(function () {
    var initId = $('#commonResourceTab').children('.active').attr('tab-id');
    initTable(initId); // 初始化资源表格
    initGroupTable(initId); // 初始化组表格
    initPage();
    initBar(initId);
});