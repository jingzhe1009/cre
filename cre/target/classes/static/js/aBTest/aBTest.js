/**
 *  ABTest
 */
var abTestModal = {
    data: {
        testStatusMap: {}, // 测试状态map
        handleType: 0, // 0新增 1修改
        echo_aBTestId: null, // 回显的测试id
        chosen_folderId: null,
        chosen_folderName: null,
        chosen_ruleName: null, //不回传
        chosen_moduleName: null,
        chosen_aRuleId: null,
        chosen_aVersion: null,
        chosen_bRuleId: null,
        chosen_bVersion: null,
    },
    // 初始化页面
    initPage: function () {
        // 时间选择插件：选择时间和日期
        $("#ABTestEditAlertModal .form-datetime").datetimepicker({
            weekStart: 1,
            todayBtn: 1,
            autoclose: 1,
            todayHighlight: 1,
            startView: 2,
            forceParse: 0,
            showMeridian: 1,
            format: "yyyy-mm-dd"
        });

        abTestModal.data.testStatusMap = abTestStatus ? abTestStatus : {};
        abTestModal.initStatusList();
        abTestModal.initSearch();
        abTestModal.initPackage();
    },
    // 初始化搜索栏测试状态下拉框
    initStatusList: function () {
        var all = '全部';
        var html = '<li onclick="abTestModal.handleSearchChange(this, null, \'' + all + '\')"><a>全部</a></li>';
        var map = abTestModal.data.testStatusMap;
        Object.keys(map).map(function (key, index) {
            html += '<li data-key="' + key + '" onclick="abTestModal.handleSearchChange(this, \'' + key + '\', \'' + map[key] + '\')"><a>' + map[key] + '</a></li>';
        });
        $('#ABTestPage_SearchContainer .status ul').html(html);
        // $("#ABTestPage_SearchContainer .status li[data-key='" + 1 + "']").trigger('click'); //默认选中运行中的测试
    },
    handleSearchChange: function ($this, id, value) {
        $($this).parent().parent().find('input').attr('data-key', id ? id : '').val(value);
    },
    // 搜索功能
    initSearch() {
        var inputs = $('#ABTestPage_SearchContainer .selfAdaptionLeft .input-group .form-control');
        var obj = {};
        for (var i = 0; i < inputs.length; i++) {
            if ($.trim($(inputs[i]).val()) === '') {
                continue;
            }
            obj[$(inputs[i]).attr('data-col')] = $.trim($(inputs[i]).val());
            if (obj.status) {
                var statusKey = $('#ABTestPage_SearchContainer .status input').attr('data-key');
                if (statusKey) {
                    obj.status = statusKey;
                } else {
                    delete obj.status;
                }
            }
        }
        abTestModal.initABTestTable(obj);
    },
    // 初始化测试编辑页场景下拉框(将模型库和场景列表合并)
    initPackage: function () {
        // 获取模型库
        abTestModal.$ajax('/ruleFolder/modelBaseId', 'GET', null, function (model) {
            var html = '<option value=""></option>';
            html += '<option value="' + model.data + '">模型库</option>';
            abTestModal.$ajax('/ruleFolder/ruleFolderList', 'GET', null, function (data) {
                for (var i = 0; i < data.length; i++) {
                    html += '<option value="' + data[i].key + '" >' + data[i].text + '</option>';
                }
                $('#ABTest_packageInput').html(html);
                $('#ABTest_packageInput').trigger('chosen:updated');
            });
        });
    },
    // 获取场景下所有模型
    loadPackageModels: function () {
        var chosen_folderId = abTestModal.data.chosen_folderId;
        var chosen_ruleName = abTestModal.data.chosen_ruleName;
        if (!chosen_folderId) {
            // 复原模型列表
            $('#ABTest_moduleInput').val('');
            $('#ABTest_moduleInput').html('');
            $('#ABTest_moduleInput').trigger('chosen:updated');
            // 复原版本列表
            $('#ABTest_AModel').html('');
            $('#ABTest_BModel').html('');
            return;
        }
        var html = '<option value=""></option>';
        abTestModal.$ajax('/rule/getModelByProduct', 'GET', {folderId: chosen_folderId}, function (data) {
            var arr = data.data;
            for (var i = 0; i < arr.length; i++) {
                html += '<option value="' + arr[i].ruleName + '" >' + arr[i].moduleName + '</option>';
            }
            $('#ABTest_moduleInput').html(html);
            if (chosen_ruleName) { // 回显
                $("#ABTest_moduleInput").val(chosen_ruleName);
                abTestModal.handleModelChange($("#ABTest_moduleInput"));
            }
            $('#ABTest_moduleInput').trigger('chosen:updated');
        });
    },
    // 获取A模型版本列表
    load_ModelA_Versions: function () {
        var chosen_ruleName = abTestModal.data.chosen_ruleName;
        var chosen_aRuleId = abTestModal.data.chosen_aRuleId;
        if (!chosen_ruleName) {
            $('#ABTest_AModel').html('');
            abTestModal.data.chosen_aRuleId = null;
            return;
        }
        abTestModal.$ajax('/rule/getVersionListByStatus', 'GET', {ruleName: chosen_ruleName}, function (data) {
            var html = '<option value=""></option>';
            var arr = data.data;

            if (arr && arr.length > 0) {
                for (var i = 0; i < arr.length; i++) {
                    html += '<option value="' + arr[i].ruleId + '" >' + arr[i].version + '</option>';
                }
            } else {
                html += '<option value="">--无可用版本--</option>';
            }
            $('#ABTest_AModel').html(html);
            $('#ABTest_AModel').unbind().on('change', function (e) {
                abTestModal.handleAVersionChange();
            });
            if (chosen_aRuleId) { // 回显
                $("#ABTest_AModel option[value='" + chosen_aRuleId + "']").prop('selected', true);
            }
        });
    },
    // 获取B模型版本列表
    load_ModelB_Versions: function () {
        var chosen_ruleName = abTestModal.data.chosen_ruleName;
        var chosen_bRuleId = abTestModal.data.chosen_bRuleId;
        if (!chosen_ruleName) {
            $('#ABTest_BModel').html('');
            abTestModal.data.chosen_bRuleId = null;
            return;
        }
        abTestModal.$ajax('/rule/getVersionListWithDraftByStatus', 'GET', {ruleName: chosen_ruleName}, function (data) {
            var html = '<option value=""></option>';
            var arr = data.data;

            if (arr && arr.length > 0) {
                for (var i = 0; i < arr.length; i++) {
                    html += '<option value="' + arr[i].ruleId + '" >' + arr[i].version + '</option>';
                }

            } else {
                html += '<option value="">--无可用版本--</option>';
            }
            $('#ABTest_BModel').html(html);
            $('#ABTest_BModel').unbind().on('change', function (e) {
                abTestModal.handleBVersionChange();
            });
            if (chosen_bRuleId) {
                $("#ABTest_BModel option[value='" + chosen_bRuleId + "']").prop('selected', true);
            }
        });
    },
    // 选择场景的回调
    handleFolderChange: function (e, value) {
        if (e) {
            abTestModal.data.chosen_folderId = $(e).val();
            abTestModal.data.chosen_folderName = $('#ABTest_packageInput option:selected').text();
        } else {
            if (value) {
                $('#ABTest_packageInput').val(value);
                $('#ABTest_packageInput').trigger('chosen:updated');
            } else {
                // 新建/退出页面时触发，无值：复原场景列表
                $('#ABTest_packageInput').val('');
                $('#ABTest_packageInput').trigger('chosen:updated');
            }
        }
        abTestModal.loadPackageModels();
    },
    // 选择模型的回调
    handleModelChange: function (e) {
        abTestModal.data.chosen_ruleName = $(e).val();
        abTestModal.data.chosen_moduleName = $('#ABTest_moduleInput option:selected').text();
        abTestModal.load_ModelA_Versions();
        abTestModal.load_ModelB_Versions();
    },
    handleAVersionChange: function () {
        if ($('#ABTest_AModel option:selected').attr('value')) {
            abTestModal.data.chosen_aRuleId = $('#ABTest_AModel option:selected').attr('value');
            abTestModal.data.chosen_aVersion = $('#ABTest_AModel option:selected').text();
        }
    },
    handleBVersionChange: function () {
        if ($('#ABTest_BModel option:selected').attr('value')) {
            abTestModal.data.chosen_bRuleId = $('#ABTest_BModel option:selected').attr('value');
            abTestModal.data.chosen_bVersion = $('#ABTest_BModel option:selected').text();
        }
    },
    // 进入测试编辑页面 0新增/1修改
    showABTest: function (handleType, $this) {
        var detail = null;
        if ($this) {
            var curRow = $this.parentNode.parentNode;
            detail = $('#ABTestTable').DataTable().row(curRow).data();
        }
        abTestModal.data.handleType = handleType;
        abTestModal.echoData(detail);

        $('select.chosen-select').chosen({
            width: '100%',
            no_results_text: '没有找到',    // 当检索时没有找到匹配项时显示的提示文本
            // disable_search_threshold: 10, // 10个以下的选择项则不显示检索框
            search_contains: true         // 从任意位置开始检索
        });

        $('#ABTestEditAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 保存测试
    saveABTest: function () {
        // 表单验证
        if (!$('#ABTestEditAlertModal form').isValid()) {
            return;
        }
        if (!abTestModal.data.chosen_folderId) {
            failedMessager.show('请选择场景！');
            return;
        }
        if (!abTestModal.data.chosen_ruleName) {
            failedMessager.show('请选择模型！');
            return;
        }
        if (!$('#ABTest_AFetchStartTime_FormGroup input').val()) {
            failedMessager.show('请选择A模型测试开始时间！');
            return;
        }
        var handleType = abTestModal.data.handleType;
        var url = (handleType === 0) ? '/save' : '/update';
        var obj = abTestModal.getAbTestData();
        abTestModal.$ajax('/aBTest' + url, 'POST', obj, function (data) {
            if (data.status === 0) {
                successMessager.show('保存成功！');
                abTestModal.initSearch(); //刷新列表
                $('#ABTestEditAlertModal').modal('hide');
            } else {
                failedMessager.show('保存失败！' + data.msg);
            }
        });
    },
    // 获取测试数据
    getAbTestData: function () {
        var obj = {};
        if (abTestModal.data.handleType === 1) {
            obj.aBTestId = abTestModal.data.echo_aBTestId;
        }
        obj.aBTestName = $("#ABTestEditAlertModal .form-control[col-name='aBTestName']").val();
        obj.folderId = abTestModal.data.chosen_folderId;
        obj.folderName = abTestModal.data.chosen_folderName;
        obj.aModelName = abTestModal.data.chosen_moduleName;
        obj.bModelName = abTestModal.data.chosen_moduleName;
        obj.aRuleId = abTestModal.data.chosen_aRuleId;
        obj.aModelVerson = abTestModal.data.chosen_aVersion;
        obj.bRuleId = abTestModal.data.chosen_bRuleId;
        obj.bModelVerson = abTestModal.data.chosen_bVersion;
        obj.aFetchStartTime = $("#ABTestEditAlertModal .form-control[col-name='aFetchStartTime']").val();
        obj.aBTestDesc = $("#ABTestEditAlertModal .form-control[col-name='aBTestDesc']").val();
        return obj;
    },
    // 回显测试数据
    echoData: function (detail) {
        abTestModal.data.echo_aBTestId = detail ? detail.aBTestId : null;
        abTestModal.data.chosen_folderId = detail ? detail.folderId : null;
        abTestModal.data.chosen_folderName = detail ? detail.folderName : null;
        abTestModal.data.chosen_ruleName = detail ? detail.ruleName : null;
        abTestModal.data.chosen_moduleName = detail ? detail.aModelName : null;
        abTestModal.data.chosen_aRuleId = detail ? detail.aRuleId : null;
        abTestModal.data.chosen_aVersion = detail ? detail.aModelVerson : null;
        abTestModal.data.chosen_bRuleId = detail ? detail.bRuleId : null;
        abTestModal.data.chosen_bVersion = detail ? detail.bModelVerson : null;
        if (abTestModal.data.handleType === 0) {
            abTestModal.handleFolderChange();
        } else {
            abTestModal.handleFolderChange(null, detail.folderId); // 下拉框回显
            $("#ABTestEditAlertModal .form-control[col-name='aBTestName']").val(detail.aBTestName ? detail.aBTestName : '');
            $("#ABTestEditAlertModal .form-control[col-name='aBTestDesc']").val(detail.aBTestDesc ? detail.aBTestDesc : '');
            $("#ABTestEditAlertModal .form-control[col-name='aFetchStartTime']").val(detail.aFetchStartTime ? detail.aFetchStartTime : '');
        }
    },
    // 退出编辑测试弹框时清除缓存及数据
    clearEdit: function () {
        abTestModal.data.echo_aBTestId = null;
        abTestModal.data.chosen_folderId = null;
        abTestModal.data.chosen_folderName = null;
        abTestModal.data.chosen_ruleName = null;
        abTestModal.data.chosen_moduleName = null;
        abTestModal.data.chosen_aRuleId = null;
        abTestModal.data.chosen_aVersion = null;
        abTestModal.data.chosen_bRuleId = null;
        abTestModal.data.chosen_bVersion = null;
        abTestModal.handleFolderChange();
        $("#ABTestEditAlertModal .form-control[col-name='aBTestName']").val('');
        $("#ABTestEditAlertModal .form-control[col-name='aBTestDesc']").val('');
        $("#ABTestEditAlertModal .form-control[col-name='aFetchStartTime']").val('');
        $('#ABTestEditAlertModal form').validator('cleanUp');
    },
    // 查看详情
    showDetail: function (ABTestId) {
        if (ABTestId) {
            abTestModal.$ajax('/aBTest/detail', 'POST', {'aBTestId': ABTestId}, function (data) {
                if (data.status === 0) {
                    var obj = data.data;
                    for (var key in obj) {
                        if (key === 'status') { //测试状态
                            var status = abTestModal.data.testStatusMap[obj[key]];
                            $("#ABTestDetailAlertModal .form-control[col-name='" + key + "']").val(status);
                            continue;
                        }
                        var target = $("#ABTestDetailAlertModal .form-control[col-name='" + key + "']");
                        if (target.length > 0) {
                            $(target).val(obj[key]);
                        }
                    }
                    // 时长
                    var duration = abTestModal.getDuration(obj.startTime, obj.endTime);
                    $("#ABTestDetailAlertModal .form-control[col-name='duration']").val(duration);

                    $('#ABTestDetailAlertModal').modal({'show': 'center', "backdrop": "static"});
                } else {
                    failedMessager.show(data.msg);
                }
            });
        }
    },
    // 计算测试时长
    getDuration: function (startTime, endTime) {
        var duration = 0;
        if (startTime) {
            var startDate = new Date(Date.parse(startTime.replace(/-/g, '/')));
            var endDate = endTime ? new Date(Date.parse(endTime.replace(/-/g, '/'))) : new Date();
            var dateDiff = endDate.getTime() - startDate.getTime();
            var dateDiffDayNum = dateDiff / (24 * 3600 * 1000);
            // duration = Math.floor(dateDiffDayNum);
            // duration = Math.ceil(dateDiffDayNum);
            // duration = Math.round(dateDiffDayNum);
            duration = dateDiffDayNum.toFixed(1) > 0.0 ? dateDiffDayNum.toFixed(1) : 0;
        }
        return duration;
    },
    // 退出详情页面清除展示数据
    clearDetail: function () {
        $("#ABTestDetailAlertModal .form-control").val('');
    },
    // 删除测试
    deleteTest: function (aBTestId) {
        if (aBTestId) {
            confirmAlert.show('是否确认删除？', function () {
                abTestModal.$ajax('/aBTest/delete', 'POST', {'aBTestId': aBTestId}, function (data) {
                    if (data.status === 0) {
                        abTestModal.initSearch(); //刷新列表
                        successMessager.show('删除成功！');
                    } else {
                        failedMessager.show('删除失败！' + data.msg);
                    }
                });
            });
        }
    },
    // 启/停用
    changeStatus: function (that, aBTestId) {
        var status = $(that).prop('checked');
        var url = status ? '/start' : '/stop';
        var txt = status ? '启用' : '停用';
        if (aBTestId) {
            abTestModal.$ajax('/aBTest' + url, 'POST', {'aBTestId': aBTestId}, function (data) {
                if (data.status === 0) {
                    successMessager.show(txt + '成功！');
                } else {
                    failedMessager.show(txt + '失败！' + data.msg);
                }
                abTestModal.initSearch(); //刷新列表
            });
        }
    },
    // 无缝切换
    online: function (aBTestId) {
        if (aBTestId) {
            abTestModal.$ajax('/aBTest/online', 'POST', {'aBTestId': aBTestId}, function (data) {
                if (data.status === 0) {
                    abTestModal.initSearch(); //刷新列表
                    successMessager.show('切换成功！');
                } else {
                    failedMessager.show('切换失败！' + data.msg);
                }
            });
        }
    },
    $ajax: function (url, method, body, callback, dataType) {
        if (dataType === void 0) dataType = 'json';
        var option = {
            url: webpath + url,
            type: method,
            dataType: dataType,
            success: function (data) {
                callback(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.log(errorThrown);
                if (textStatus === 'timeout') {
                    console.log('请求超时');
                }
            }
        };
        if (body) option.data = body;
        $.ajax(option);
    },
    // 初始化 指标表格
    initABTestTable: function (obj) {
        $('#ABTestTable').width('100%').dataTable({
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
                {"title": "测试名称", "data": "aBTestName", "width": "10%"},
                {"title": "场景名称", "data": "folderName", "width": "10%"},
                {"title": "模型名称", "data": "aModelName", "width": "10%"},
                {"title": "A模型版本", "data": "aModelVerson", "width": "10%"},
                {"title": "B模型版本", "data": "bModelVerson", "width": "10%"},
                {
                    "title": "测试状态", "data": "status", "width": "10%", "render": function (data, type, row) {
                        var map = abTestModal.data.testStatusMap;
                        var status = map[data] ? map[data] : '--';
                        var label = '<span class="label label-dot label-primary"></span> ' + status; // 默认初始化状态
                        if (data === '1') { // 运行中
                            label = '<span class="label label-dot label-success"></span> ' + status;
                        }
                        if (data === '3') { //异常运行中
                            label = '<span class="label label-dot label-danger"></span> ' + status;
                        }
                        return label;
                    }
                },
                {
                    "title": "启用/停用", "data": "status", "width": "10%", "render": function (data, type, row) {
                        // status: 0未开始 1运行中 2停止 3异常运行中 4已上线
                        var switchValue = ''; // 展示文字
                        var checkHtmlStr = ''; // 控制开关开启状态
                        var disabledStr = ''; // 控制是否可操作
                        if (data === '0' || data === '2' || data === '3') {
                            switchValue = '开始测试';
                        } else if (data === '1') {
                            switchValue = '停止测试';
                            checkHtmlStr += 'checked';
                        } else { // 已上线不允许操作
                            switchValue = '--';
                            disabledStr = ' disabled';
                        }
                        var htmlStr = "";
                        htmlStr += '<div class="switch"><input onclick="abTestModal.changeStatus( this, \'' + row.aBTestId + '\');" type="checkbox" ' + checkHtmlStr + ' ' + disabledStr + ' ><label>' + switchValue + '</label></div>';
                        return htmlStr;
                    }
                },
                {
                    "title": "操作", "data": null, "width": "30%", "render": function (data, type, row) {
                        var htmlStr = "";
                        htmlStr += '<span type="button" class="cm-tblB" onclick="abTestModal.showDetail(\'' + row.aBTestId + '\')">查看</span>';
                        htmlStr += '<span type="button" class="cm-tblB" onclick="abTestModal.showABTest(1, this)">修改</span>';
                        htmlStr += '<span type="button" class="cm-tblC delBtn" onclick="abTestModal.deleteTest(\'' + row.aBTestId + '\')">删除</span>';
                        htmlStr += '<span type="button" class="cm-tblB" onclick="abTestModal.online(\'' + row.aBTestId + '\')">执行切换</span>';
                        return htmlStr;
                    }
                }
            ],
            ajax: {
                url: webpath + '/aBTest/list',
                "type": 'GET',
                "data": function (d) { // 查询参数
                    return $.extend({}, d, obj);
                }
            }
        });
    }
};

$(function () {
    abTestModal.initPage();
});