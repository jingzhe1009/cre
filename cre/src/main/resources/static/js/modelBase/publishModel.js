/**
 * 场景下模型设为公有
 * data:2019/10/12
 * author:bambi
 */

var publishModelObj = {
    /**
     * 初始化页面
     *      ruleId 模型id
     *      curFolderId 当前场景id
     */
    initPage: function (ruleId, curFolderId) {
        if (!ruleId || !curFolderId) {
            failedMessager.show('参数不全！');
            return;
        }
        // 清空表单内容
        $('#modelBaseForm, #modelVersionForm').validator('cleanUp'); // 清除表单中的全部验证消息
        $('#publishModelDiv form select').html('');
        $('#publishModelDiv form input').val('');
        $('#publishModelDiv form textarea').val('');
        //
        // 初始化组下拉框及modelBase下拉框
        initModelBaseGroup();

        // 单选框change事件
        $('.publishTypeRadio').unbind().on('change', function () {
            var typeValue = $(".publishTypeRadio input[name='publishTypeRadio']:checked").attr('typeValue');
            if (typeValue == '0') { // 选择现有
                $('#publishModelDiv .old').removeClass('hide');
                $('#publishModelDiv .new').addClass('hide');
                initModelBaseSel($('#modelGroupSelector option:selected').val());
                $('#modelBaseDes').removeAttr('placeholder').attr('disabled', true);
            } else if (typeValue == '1') { // 新建
                $('#publishModelDiv .new').removeClass('hide');
                $('#publishModelDiv .old').addClass('hide');
                $('#modelBaseDes').val('').attr('placeholder', '请填写模型描述').removeAttr('disabled');
            } else {
                return;
            }
        });

        // 发布绑定
        $('#publishModelConfirm').unbind().on('click', function () {
            // 校验组
            if ($('#modelGroupSelector option:selected').attr('data-id') == 'empty') {
                failedMessager.show('创建失败, 组信息无效！');
                return;
            }
            // 校验模型名称
            var typeValue = $(".publishTypeRadio input[name='publishTypeRadio']:checked").attr('typeValue');
            if (typeValue == '0') { // 已有规则集创建新版本
                if ($('#modelBaseSelector option:selected').attr('data-id') == 'empty') {
                    failedMessager.show('创建失败, 模型无效！');
                    return;
                }
            } else if (typeValue == '1') { // 新建模型, 新建版本
                if ($.trim($('#modelBaseName')) == '') {
                    failedMessager.show('创建失败, 模型名不能为空！');
                    return;
                }
            } else {
                return;
            }
            // 校验版本信息
            if (!$('#modelVersionForm').isValid()) {
                return;
            }
            // 获取数据并保存
            publishModelObj.setCommon(typeValue, ruleId, curFolderId);
        });

        // 默认选中从已有模型中选择
        $(".publishTypeRadio input[typeValue='0']").prop('checked', true);
        $(".publishTypeRadio").trigger('change');

        $('#publishModelDiv').modal({"show": "center", "backdrop": "static"});
    },
    // 获取数据
    setCommon: function (typeValue, ruleId, curFolderId) {
        var obj = {};
        // 0已有模型, 新建版本
        if (typeValue == '0') {
            obj['ruleName'] = $('#modelBaseSelector option:selected').val(); // 模型名称
        }
        // 1新建规则集, 新建版本
        else if (typeValue == '1') {
            obj['ruleName'] = $('#modelBaseName').val(); // 模型名称
            obj['modelGroupId'] = $('#modelGroupSelector option:selected').attr('data-id'); // 组id
            obj['ruleDesc'] = $.trim($('#modelBaseDes').val()); // 模型描述
            obj['ruleType'] = $("#modelBaseForm input[name='modelTypeRadio']:checked").attr('typeValue'); // 模型类型
        } else {
            return;
        }
        obj['oldFolderId'] = curFolderId; // 当前场景ID
        obj['ruleId'] = ruleId; // 当前模型ID
        obj['version'] = $.trim($('#modelVersionInput').val()); // 版本号
        obj['versionDesc'] = $.trim($('#modelVersionDes').val()); // 版本描述

        // 发布
        $.ajax({
            url: webpath + '/rule/publish',
            type: 'POST',
            dataType: "json",
            data: obj,
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('设为公有成功！');
                    $('#publishModelDiv').modal('hide');
                    setTimeout(resetPage, 500); // 刷新场景主页
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
}

//  初始化模型组下拉框
function initModelBaseGroup() {
    $.ajax({
        url: webpath + '/modelBase/group/list',
        type: 'POST',
        dataType: "json",
        data: {},
        success: function (data) {
            var htmlStr_selector = '';
            if (data.data.length > 0) {
                var data = data.data;
                for (var i = 0; i < data.length; i++) {
                    htmlStr_selector += '<option data-id=\'' + data[i].modelGroupId + '\'>' + data[i].modelGroupName + '</option>';
                }
            } else {
                htmlStr_selector += '<option data-id="empty">无</option>';
            }
            $('#modelGroupSelector').empty().html(htmlStr_selector); // 表单
            $('#modelGroupSelector').on('change', function () {
                initModelBaseSel($('#modelGroupSelector option:selected').val());
            });
        },
        complete: function () {
            $('#modelGroupSelector option:first').prop('selected', true); // 初始化选中第一个
            $('#modelGroupSelector').trigger('change');
        }
    });
}

// 根据组初始化模型下拉框
function initModelBaseSel(modelGroupName) {
    if (modelGroupName == '无') {
        return;
    }
    $.ajax({
        url: webpath + '/rule/public/header/list',
        type: 'GET',
        dataType: "json",
        data: {"modelGroupName": modelGroupName},
        success: function (data) {
            var htmlStr_selector = '';
            if (data.data.length > 0) {
                var data = data.data;
                for (var i = 0; i < data.length; i++) {
                    htmlStr_selector += '<option data-id=\'' + data[i].ruleName + '\' des=\'' + data[i].ruleDesc + '\'>' + data[i].ruleName + '</option>';
                }
            } else {
                htmlStr_selector += '<option data-id="empty">无</option>';
                $('#modelBaseDes').val('');
            }
            $('#modelBaseSelector').empty().html(htmlStr_selector); // 表单
            $('#modelBaseSelector').on('change', function () {
                $('#modelBaseDes').val($('#modelBaseSelector option:selected').attr('des'));
            });
        },
        complete: function () {
            $('#modelBaseSelector option:first').prop('selected', true); // 初始化选中第一个
            $('#modelBaseSelector').trigger('change');
        }
    });
}