/**
 * 规则集发布
 * data:2019/09/22
 * author:bambi
 */

var setCommonRuleSetModal = {
    // 初始化页面
    initPage: function (tableData, ruleId, folderId, callBackFun) {
        if (!ruleId || !folderId || !callBackFun) {
            failedMessager.show('参数不全！');
            return;
        }
        // 清空表单
        $('#ruleSetHeaderForm, #ruleSetVersionForm').validator('cleanUp'); // 清除表单中的全部验证消息
        $('#setCommonRuleSetModal form select').html('');
        $('#setCommonRuleSetModal form input').val('');
        $('#setCommonRuleSetModal form textarea').val('');

        // 初始化规则集组下拉框及规则集下拉框
        initRuleSetGroup();

        // 默认选中从已有规则集选择
        $(".setCommonTypeRadio input[typeValue='0']").prop('checked', true);
        $('.setCommonType').addClass('hide');
        $('.old').removeClass('hide');
        $('.new').addClass('hide');
        $('#ruleSetHeaderDes').val('').removeAttr('placeholder');

        // 单选框change事件
        $('.setCommonTypeRadio').unbind().on('change', function () {
            var typeValue = $(".setCommonTypeRadio input[name='setCommonTypeRadio']:checked").attr('typeValue');
            if (typeValue == '0') { // 选择现有
                $('.old').removeClass('hide');
                $('.new').addClass('hide');
                initRuleSetHeader($('#ruleSetGroupSelector option:selected').val());
                $('#ruleSetHeaderDes').removeAttr('placeholder');
            } else if (typeValue == '1') { // 新建
                $('.new').removeClass('hide');
                $('.old').addClass('hide');
                $('#ruleSetHeaderDes').val('').attr('placeholder', '请填写规则集描述');
            } else {
                return;
            }
        });

        // 发布
        $('#setCommonConfirm').unbind().on('click', function () {
            // 校验组
            if ($('#ruleSetGroupSelector option:selected').attr('data-id') == 'empty') {
                failedMessager.show('创建失败, 组信息无效！');
                return;
            }
            // 校验规则集名称
            var typeValue = $(".setCommonTypeRadio input[name='setCommonTypeRadio']:checked").attr('typeValue');
            if (typeValue == '0') { // 已有规则集创建新版本, 更新引用关系
                if ($('#ruleSetHeaderSelector option:selected').attr('data-id') == 'empty') {
                    failedMessager.show('创建失败, 规则集无效！');
                    return;
                }
            } else if (typeValue == '1') { // 新建规则集, 新建版本, 更新引用关系
                if ($.trim($('#ruleSetHeaderInput')) == '') {
                    failedMessager.show('创建失败, 请正确填写规则集名称！');
                    return;
                }
            } else {
                return;
            }
            // 校验版本信息
            if (!$('#ruleSetVersionForm').isValid()) {
                return;
            }
            // 获取数据并保存
            setCommonRuleSetModal.setCommon(typeValue, tableData, ruleId, folderId, callBackFun);
        });

        $('#setCommonRuleSetModal').modal({"show": "center", "backdrop": "static"});
    },
    // 获取数据
    setCommon: function (typeValue, tableData, ruleId, folderId, callBackFun) {
        var obj = {};
        // 0已有规则集创建新版本
        if (typeValue == '0') {
            obj['ruleSetHeaderId'] = $('#ruleSetHeaderSelector option:selected').attr('data-id'); // 规则集headerId
            obj['ruleSetName'] = $('#ruleSetHeaderSelector option:selected').text(); // 规则集名称
        }
        // 1新建规则集, 新建版本
        else if (typeValue == '1') {
            obj['ruleSetGroupId'] = $('#ruleSetGroupSelector option:selected').attr('data-id'); // 组id
            obj['ruleSetHeaderDesc'] = $.trim($('#ruleSetHeaderDes').val()); // 规则集header描述
            obj['ruleSetName'] = $.trim($('#ruleSetHeaderInput').val()); // 规则集名称
        } else {
            return;
        }
        // obj['version'] = $.trim($('#version').val()); // 版本号
        obj['ruleSetDesc'] = $.trim($('#versionDes').val()); // 版本描述
        obj['enable'] = 1; // 启用状态
        obj['ruleSetContent'] = JSON.stringify(tableData); // 表格数据
        obj['ruleId'] = ruleId; // 模型ID
        obj['folderId'] = folderId; // 场景ID
        // var ruleSetName = obj.ruleSetName + '-' + obj.version;
        var ruleSetName = obj.ruleSetName;
        // 发布
        $.ajax({
            url: webpath + '/ruleSet/publish',
            type: 'POST',
            dataType: "json",
            data: obj,
            success: function (data) {
                if (data.status === 0) {
                    if (data.data != '') {
                        callBackFun(data.data, ruleSetName); // 返回该版本的ruleSetId
                        $('#setCommonRuleSetModal').modal('hide');
                    }
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
}

//  初始化规则集组下拉框
function initRuleSetGroup() {
    $.ajax({
        url: webpath + '/ruleSet/group/list',
        type: 'POST',
        dataType: "json",
        data: {},
        success: function (data) {
            var htmlStr_selector = '';
            if (data.data.length > 0) {
                var data = data.data;
                for (var i = 0; i < data.length; i++) {
                    htmlStr_selector += '<option data-id=\'' + data[i].ruleSetGroupId + '\'>' + data[i].ruleSetGroupName + '</option>';
                }
            } else {
                htmlStr_selector += '<option data-id="empty">无</option>';
            }
            $('#ruleSetGroupSelector').empty().html(htmlStr_selector); // 表单
            $('#ruleSetGroupSelector').on('change', function () {
                initRuleSetHeader($('#ruleSetGroupSelector option:selected').val());
            });
        },
        complete: function () {
            $('#ruleSetGroupSelector option:first').prop('selected', true); // 初始化选中第一个
            initRuleSetHeader($('#ruleSetGroupSelector option:selected').val());
        }
    });
}

// 根据组名称初始化规则header下拉框
function initRuleSetHeader(ruleSetGroupName) {
    if (ruleSetGroupName == '无') {
        return;
    }
    $.ajax({
        url: webpath + '/ruleSet/header/list',
        type: 'POST',
        dataType: "json",
        data: {"ruleSetGroupName": ruleSetGroupName},
        success: function (data) {
            var htmlStr_selector = '';
            if (data.data.length > 0) {
                var data = data.data;
                for (var i = 0; i < data.length; i++) {
                    htmlStr_selector += '<option data-id=\'' + data[i].ruleSetHeaderId + '\' des=\'' + data[i].ruleSetHeaderDesc + '\'>' + data[i].ruleSetName + '</option>';
                }
            } else {
                htmlStr_selector += '<option data-id="empty">无</option>';
                $('#ruleSetHeaderDes').val('');
            }
            $('#ruleSetHeaderSelector').empty().html(htmlStr_selector); // 表单
            $('#ruleSetHeaderSelector').on('change', function () {
                $('#ruleSetHeaderDes').val($('#ruleSetHeaderSelector option:selected').attr('des'));
            });
        },
        complete: function () {
            $('#ruleSetHeaderSelector option:first').prop('selected', true); // 初始化选中第一个
            $('#ruleSetHeaderDes').val($('#ruleSetHeaderSelector option:selected').attr('des'));
        }
    });
}