/**
 * 模型下规则集引入
 * data:2019/09/16
 * author:bambi
 */

var importRuleSetModal = {
    tableData: [],
    ruleSetHeaderId: '',
    ruleSetId: '',
    kpiObjList: [],
    initPage: function (callBackFun) {
        // 清空表单内容
        $('#import_ruleSetGroupSelector, #ruleSetSelector, #versionSelector').html('');
        $('#ruleSetVersionDes').val('');
        importRuleSetModal.tableData = [];
        importRuleSetModal.ruleSetHeaderId = '';
        importRuleSetModal.ruleSetId = '';
        importRuleSetModal.kpiObjList = [];
        $(".saveTypeRadio input[typeValue='1']").prop('checked', true); // 默认选中仅引用项
        // 初始化内容
        importRuleSetModal.initRuleSetGroupSelector();
        importRuleSetModal.initRulesTable(importRuleSetModal.tableData); // 初始化规则表格
        $('#importConfirm').unbind().click(function () {
            importRuleSetModal.importSubmit(callBackFun);
        });
        $('#importRuleSetModal').modal({"show": "center", "backdrop": "static"});
    },
    // 加载组下拉框
    initRuleSetGroupSelector: function () {
        $.ajax({
            url: webpath + '/ruleSet/group/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                var htmlStr = '';
                if (data.data.length > 0) {
                    var dataArr = data.data;
                    for (var i = 0; i < dataArr.length; i++) {
                        htmlStr += '<option data-id=\'' + dataArr[i].ruleSetGroupId + '\'>' + dataArr[i].ruleSetGroupName + '</option>';
                    }
                } else {
                    htmlStr += '<option data-id="empty">无</option>';
                }
                $('#import_ruleSetGroupSelector').html('').html(htmlStr);
                $('#import_ruleSetGroupSelector').on('change', function () {
                    var ruleSetGroupName = $('#import_ruleSetGroupSelector option:selected').val();
                    importRuleSetModal.initRuleSetHeaderSelector(ruleSetGroupName);
                });
            },
            complete: function () {
                $('#import_ruleSetGroupSelector option:first').prop('selected', true); // 初始化选中第一个
                var ruleSetGroupName = $('#import_ruleSetGroupSelector option:selected').val();
                importRuleSetModal.initRuleSetHeaderSelector(ruleSetGroupName);
            }
        });
    },
    // 加载规则集下拉框
    initRuleSetHeaderSelector: function (ruleSetGroupName) {
        if (ruleSetGroupName == '无') {
            $('#ruleSetSelector').html('').html('<option data-id="empty">无</option>');
            $('#versionSelector').html('').html('<option data-id="empty">无</option>');
            $('#ruleSetVersionDes').val('');
            importRuleSetModal.tableData = [];
            importRuleSetModal.ruleSetHeaderId = '';
            importRuleSetModal.ruleSetId = '';
            importRuleSetModal.kpiObjList = [];
            importRuleSetModal.initRulesTable(importRuleSetModal.tableData);
            return;
        }
        $.ajax({
            url: webpath + '/ruleSet/header/list',
            type: 'POST',
            dataType: "json",
            data: {"ruleSetGroupName": ruleSetGroupName},
            success: function (data) {
                var htmlStr = '';
                if (data.data.length > 0) {
                    var dataArr = data.data;
                    for (var i = 0; i < dataArr.length; i++) {
                        htmlStr += '<option data-id=\'' + dataArr[i].ruleSetHeaderId + '\'>' + dataArr[i].ruleSetName + '</option>';
                    }
                } else {
                    htmlStr += '<option data-id="empty">无</option>';
                }
                $('#ruleSetSelector').html('').html(htmlStr);
                $('#ruleSetSelector').on('change', function () {
                    var ruleSetHeaderId = $('#ruleSetSelector option:selected').attr('data-id');
                    importRuleSetModal.ruleSetHeaderId = ruleSetHeaderId;
                    importRuleSetModal.initVersionSelector(ruleSetHeaderId);
                });
            },
            complete: function () {
                $('#ruleSetSelector option:first').prop('selected', true); // 初始化选中第一个
                var ruleSetHeaderId = $('#ruleSetSelector option:selected').attr('data-id');
                importRuleSetModal.ruleSetHeaderId = ruleSetHeaderId;
                importRuleSetModal.initVersionSelector(ruleSetHeaderId);
            }
        });
    },
    // 加载版本下拉框
    initVersionSelector: function (ruleSetHeaderId) {
        if (ruleSetHeaderId && ruleSetHeaderId != '') {
            if (ruleSetHeaderId == 'empty') {
                $('#versionSelector').html('').html('<option data-id="empty">无</option>');
                $('#ruleSetVersionDes').val('');
                importRuleSetModal.tableData = [];
                importRuleSetModal.ruleSetHeaderId = '';
                importRuleSetModal.ruleSetId = '';
                importRuleSetModal.kpiObjList = [];
                importRuleSetModal.initRulesTable(importRuleSetModal.tableData);
                return;
            }
            $.ajax({
                url: webpath + '/ruleSet/list',
                type: 'POST',
                dataType: "json",
                data: {"ruleSetHeaderId": ruleSetHeaderId, "enable": 1},
                success: function (data) {
                    var htmlStr = '';
                    if (data.data.length > 0) {
                        var dataArr = data.data;
                        for (var i = 0; i < dataArr.length; i++) {
                            htmlStr += '<option data-id=\'' + dataArr[i].ruleSetId + '\' data-des=\'' + dataArr[i].ruleSetDesc + '\'>' + dataArr[i].version + '</option>';
                        }
                    } else {
                        htmlStr += '<option data-id="empty">无</option>';
                    }
                    $('#versionSelector').html('').html(htmlStr);

                    $('#versionSelector').on('change', function () {
                        var ruleSetId = $('#versionSelector option:selected').attr('data-id');
                        importRuleSetModal.ruleSetId = ruleSetId;
                        importRuleSetModal.initRulesArr(ruleSetId);
                    });
                },
                complete: function () {
                    $('#versionSelector option:first').prop('selected', true); // 初始化选中第一个
                    var ruleSetId = $('#versionSelector option:selected').attr('data-id');
                    importRuleSetModal.ruleSetId = ruleSetId;
                    importRuleSetModal.initRulesArr(ruleSetId);
                }
            });
        }
    },
    // 处理规则表格数组数据
    initRulesArr: function (ruleSetId) {
        if (ruleSetId && ruleSetId != '') {
            if (ruleSetId == 'empty') {
                $('#ruleSetVersionDes').val('');
                importRuleSetModal.tableData = [];
                importRuleSetModal.ruleSetId = '';
                importRuleSetModal.kpiObjList = [];
                importRuleSetModal.initRulesTable(importRuleSetModal.tableData);
                return;
            }
            // 查询版本数据及回显
            $.ajax({
                url: webpath + '/ruleSet/getOne',
                type: 'POST',
                dataType: "json",
                data: {"ruleSetId": ruleSetId},
                success: function (data) {
                    if (data.status === 0) {
                        importRuleSetModal.kpiObjList = data.data.kpiInfo; // 引用的指标信息List
                        importRuleSetModal.ruleSetId = ruleSetId;
                        importRuleSetModal.ruleSetHeaderId = data.data.ruleSetHeaderId;
                        $('#ruleSetVersionDes').val(data.data.ruleSetDesc);
                        if (data.data.ruleSetContent != null && data.data.ruleSetContent != '') {
                            importRuleSetModal.tableData = JSON.parse(data.data.ruleSetContent);
                        }
                        importRuleSetModal.initRulesTable(importRuleSetModal.tableData); // 初始化规则表格
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        }
    },
    // 加载规则表格
    initRulesTable: function (rulesArr) {
        $('#importActionTbl').css('width', '100%').DataTable({
            "rowReorder": false,
            "searching": false,
            "ordering": false,
            "destroy": true,
            "bLengthChange": false,
            "paging": false,
            "data": rulesArr,
            "columns": [
                {"name": "idx"},
                {"name": "ruleName"},
                {"name": "isEndAction", "width": "70px"},
                {"name": "isEndFlow", "width": "60px"},
                {"name": "ruleCodition"},
                {"name": "ruleResult"},
            ],
            "columnDefs": [
                {
                    "targets": 0, "visible": false
                },
                {
                    "targets": 2, "render": function (data) {
                        switch ($.trim(data)) {
                            case 'true':
                                return '是';
                            case 'false':
                                return '否';
                            default:
                                return '--';
                        }
                    }
                },
                {
                    "targets": 3, "render": function (data) {
                        switch ($.trim(data)) {
                            case 'true':
                                return '是';
                            case 'false':
                                return '否';
                            default:
                                return '--';
                        }
                    }
                }
            ],
            "drawCallback": function (settings) {
                $("table:eq(0) th").css("background-color", "#f6f7fb");
            }
        });
    },
    // 引入
    importSubmit: function (callBackFun) {
        // importType 1仅引用 0存为本地
        var importType = $("input[name='importTypeRadio']:checked").attr('typeValue');
        if (!(importType == 1 || importType == 0)) {
            failedMessager.show('引入无效！');
            return;
        }
        if (importRuleSetModal.ruleSetId == '' || importRuleSetModal.ruleSetHeaderId == '') {
            failedMessager.show('引入无效！');
            return;
        }
        var importRuleSetName = $('#ruleSetSelector').val() + '-' + $('#versionSelector').val();
        var tableDataArr = importRuleSetModal.tableData;
        var rulesArr = [];
        for (var i = 0; i < tableDataArr.length; i++) {
            rulesArr.push(tableDataArr[i][6]);
        }
        callBackFun(importType, rulesArr, importRuleSetName, importRuleSetModal.ruleSetHeaderId, importRuleSetModal.ruleSetId, importRuleSetModal.kpiObjList);
        $('#importRuleSetModal').modal('hide');
    }
}