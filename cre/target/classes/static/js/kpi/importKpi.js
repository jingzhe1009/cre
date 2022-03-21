/**
 * 模型引入指标
 * data:2019/10/29
 * author:bambi
 */

var importKpiModal = {
    idx: 0,
    /**
     * 初始化引用指标页面
     * @param handleType: 0左右侧引入指标 1表达式引入指标
     * @param echoKpiId: 0变量引入回显 1不传
     * @param idInput&textInput: 0引用后回显的元素 1不传
     * @param callBackFun: 退出回调函数
     */
    initPage: function (handleType, echoKpiId, idInput, textInput, callBackFun) {
        if (handleType === 0) { // 左右侧参数
            if (!(idInput && textInput)) {
                failedMessager.show('缺少参数[idInput/textInput]！');
                return;
            }
        }
        $(".importKpiCheckInput").prop('checked', false);
        // 回显上一次勾选的指标
        if (echoKpiId) {
            $(".importKpiCheckInput[data-id='" + echoKpiId + "']").prop('checked', true);
        }
        importKpiModal.showImportIndex();

        // 确定引用事件绑定
        $('#kpiImportConfirm').unbind().click(function () {
            importKpiModal.confirmImport(handleType, idInput, textInput, callBackFun);
        });

        // 取消引用操作
        $('#kpiImportCancel').unbind().click(function () {
            if (handleType === 0) {
                if (echoKpiId) {
                    var kpiName = JSON.parse($('#importKpi_' + echoKpiId).text()).kpiName;
                    textInput.val(kpiName);
                }
            } else if (handleType === 1) {
                $("#expRuleModal").modal({'show': 'center', "backdrop": "static"});
            }
            if (callBackFun) {
                callBackFun();
            }
        });
    },
    // 事件绑定
    eventBind: function () {
        // 搜索功能
        $('.importKpiSearch').on('click', function () {
            var inputs = $('#importKpiAlertModal .selfAdaptionLeft .input-group .form-control');
            var obj = {};
            for (var i = 0; i < inputs.length; i++) {
                if ($.trim($(inputs[i]).val()) == '') {
                    continue;
                }
                obj[$(inputs[i]).attr('data-col')] = $.trim($(inputs[i]).val());
            }
            if (obj.fetchType) {
                obj['fetchType'] = $('#importKpi_fetchTypeInput').attr('fetchType');
            }
            if (obj.kpiType) {
                obj['kpiType'] = $('#importKpi_kpiTypeInput').attr('kpiType');
            }
            initImportKpiTable(obj);
        });

        // 退出指标详情弹框
        $('#closeKpiDetail').on('click', function () {
            importKpiModal.showImportIndex();
        });

        // 取数方式radio change event
        $('#kpiSourceTypeRadioDiv .kpiSourceTypeRadio').change(function () {
            var typeValueChecked = $("input[name='kpiSourceTypeRadio']:checked").attr('typeValue');
            if (typeValueChecked === '0') { // DS
                $('.kpiApiWrapper').addClass('hide');
                $('.kpiDBWrapper').removeClass('hide');
            } else if (typeValueChecked === '1') { // API
                $('.kpiApiWrapper').removeClass('hide');
                $('.kpiDBWrapper').addClass('hide');
            }
        });

        // 搜索栏: 取数方式
        $('.importKpi_fetchTypeList li').unbind('click').on('click', function () {
            $(this).parent().siblings('.form-control').val($(this).first().text());
            $(this).parent().siblings('.form-control').attr('fetchType', $(this).first().attr('fetchType'));
        });

        // 勾选checkbox只能同时勾选一个
        $('#importKpi_kpiTable').on('click', '.importKpiCheckInput', function () {
            var currStatus = $(this).prop('checked');
            $(".importKpiCheckInput").prop('checked', false);
            $(this).prop('checked', currStatus);
        });

        // 详情只读
        $('#importKpiDetailAlertModal').on('focus', 'input, textarea', function () {
            $(this).blur();
        });
    },
    // 展示指标选择页面
    showImportIndex: function () {
        $('#importKpiAlertModal').modal({'show': 'center', "backdrop": "static"});
        $('#importKpiDetailAlertModal').modal('hide');
    },
    // 展示指标详情页面
    showDetailPage: function () {
        $('#importKpiDetailAlertModal').modal({'show': 'center', "backdrop": "static"});
        $('#importKpiAlertModal').modal('hide');
    },
    // 获取指标详情
    showKpiDetail: function (kpiId, kpiGroupName) {
        if (!(kpiId && kpiGroupName)) {
            failedMessager.show('参数[kpiId/kpiGroupName]缺失！');
            return;
        }
        $.ajax({
            url: webpath + '/kpi/detail',
            type: 'POST',
            dataType: 'json',
            data: {'kpiId': kpiId},
            success: function (data) {
                if (data.status === 0) {
                    // 恢复内容
                    importKpiModal.idx = 0;
                    $('#dsKpiLimitCols').empty();
                    importKpiModal.showDetailPage();
                    // 回显详情
                    var detailObj = data.data;
                    detailObj['kpiGroupName'] = kpiGroupName;
                    importKpiModal.echoKpiData(detailObj);
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 回显指标
    echoKpiData: function (data) {
        for (var key in data) {
            if (key === 'kpiType') { // 数据类型
                $('#kpiTypeSel option[kpiType="' + data[key] + '"]').prop('selected', true);
                continue;
            }
            if (key === 'fetchType') { // 取值方式
                $("input[name='kpiSourceTypeRadio'][typeValue='" + data[key] + "']").prop('checked', true);
                $('#kpiSourceTypeRadioDiv .kpiSourceTypeRadio').trigger('change');
                continue;
            }
            if (key === 'kpiFetchLimitersList') {
                continue;
            }
            var target = $("#importKpiDetailAlertModal .form-control[col-name='" + key + "']");
            if (target.length > 0) {
                $(target).val(data[key]);
            }
        }
        var fetchType = data['fetchType'];
        if (fetchType === '0') { // ds
            var echoLimits = data['kpiFetchLimitersList'];
            for (var i = 0; i < echoLimits.length; i++) {
                importKpiModal.echoDsKpiLimitCols(echoLimits[i].columnName, echoLimits[i].variableName);
            }
        }
        if (fetchType === '1') { // api
            importKpiModal.loadApiParamTable( data.kpiFetchLimitersList );
        }
    },
    // 回显数据源类型指标限定字段内容
    echoDsKpiLimitCols: function (echoColName, echoVarName) {
        var idx = importKpiModal.idx++;
        var newHtmlStr = '';
        newHtmlStr += '<div class="kpiLimitColRow">';
        newHtmlStr += ' <div class="input-control"><input id="importKpi_dsEchoColInput' + idx + '" class="form-control"></div>'; // 表字段
        newHtmlStr += ' <div class="limitLinkIcon"><i class="icon icon-link"></i></div>'; // 绑定图标
        newHtmlStr += ' <div class="input-control"><input id="importKpi_dsEchoVarInput' + idx + '" class="form-control"></div>'; // 绑定参数
        newHtmlStr += '</div>';
        $('#dsKpiLimitCols').append(newHtmlStr);
        $('#importKpi_dsEchoColInput' + idx).val(echoColName);
        $('#importKpi_dsEchoVarInput' + idx).val(echoVarName);
    },
    // 确认保存
    confirmImport: function (handleType, idInput, textInput, callBackFun) {
        var checkedNum = $('#importKpi_kpiTable .importKpiCheckInput:checked').length;
        if (checkedNum <= 0) {
            warningMessager.show('请选择要引用的指标！');
            return;
        }
        if (checkedNum > 1) {
            warningMessager.show('仅能选择一个指标！');
            return;
        }
        if (checkedNum === 1) {
            var kpiId = $('#importKpi_kpiTable .importKpiCheckInput:checked').attr('data-id');
            var kpiObj = JSON.parse($('#importKpi_' + kpiId).text());
            if (handleType === 0) {
                idInput.attr('kpiId', kpiId).attr('kpiType', kpiObj.kpiType);
                textInput.val(kpiObj.kpiName);
            } else if (handleType === 1) {
                ruleCfg.insertAtCursor(document.getElementById("coreTxt"), '[' + kpiObj.kpiName + ']', true, 'kpiId', kpiId);
                $("#expRuleModal").modal({'show': 'center', "backdrop": "static"});
            }

            $('#importKpiAlertModal').modal('hide');
            if (callBackFun) {
                callBackFun();
            }
        }
    },
    loadApiParamTable: function ( data ) {
        var typeList = [ '', '字符串', '整型', '对象', '浮点型', '数组', '高精度小数', '长整型', '日期' ];
        $('#kpiDef_apiParamsTable').width('100%').dataTable({
            "searching": false, // 是否开启搜索功能
            "ordering": false,
            "destroy": true,
            "bLengthChange": false,
            "paging": false,
            "info": false,
            "columns": [
                {"title": "变量别名", "data": "variableName"},
                {"title": "变量编码", "data": "variableCode"},
                // {"title": "参数组", "data": "variableGroupName"},
                {"title": "变量类型", "data": "variableTypeId", render: function ( value ) {
                        return typeList[ value ];
                    } }
            ],
            "data": data,
        });
    }
}

function initKpiGroup() {
    $.ajax({
        url: webpath + '/kpi/group/list',
        type: 'GET',
        dataType: "json",
        data: {},
        success: function (data) {
            var htmlStr_list = '';
            var htmlStr_selector = '';
            if (data.data.length > 0) {
                var data = data.data;
                for (var i = 0; i < data.length; i++) {
                    htmlStr_list += '<li group-id=\'' + data[i].kpiGroupId + '\'><a>' + data[i].kpiGroupName + '</a></li>';
                    htmlStr_selector += '<option group-id=\'' + data[i].kpiGroupId + '\'>' + data[i].kpiGroupName + '</option>';
                }
            }
            $('#importKpiAlertModal .importKpi_kpiGroupList').empty().html(htmlStr_list); // 下拉框
            $('#importKpiDetailAlertModal .importKpi_kpiGroupSelector').empty().html(htmlStr_selector); // 表单
        },
        complete: function () {
            $('#importKpiAlertModal .importKpi_kpiGroupList>li').unbind('click').on('click', function () {
                $(this).parent().siblings('.form-control').val($(this).first().text());
            });
        }
    });
}

// 初始化指标类型
function initKpiType() {
    if (kpiTypeList) {
        var htmlStr_sel = '';
        var htmlStr_list = '';
        for (var i = 0; i < kpiTypeList.length; i++) {
            htmlStr_list += '<li kpiType=\'' + kpiTypeList[i].key + '\'><a>' + kpiTypeList[i].text + '</a></li>';
            htmlStr_sel += '<option kpiType=\'' + kpiTypeList[i].key + '\'>' + kpiTypeList[i].text + '</option>';
        }
        $('#kpiTypeSel').html('').html(htmlStr_sel);
        $('.importKpi_kpiTypeList').html('').html(htmlStr_list);
        $('.importKpi_kpiTypeList li').unbind('click').on('click', function () {
            $(this).parent().siblings('.form-control').val($(this).first().text());
            $(this).parent().siblings('.form-control').attr('kpiType', $(this).first().attr('kpiType'));
        });
    }
}

// 初始化指标表格
function initImportKpiTable(obj) {
    obj = token ? Object.assign({}, obj, {token: token}) : obj;
    $.extend($('#importKpi_kpiTable').dataTable.defaults, {
        "rowReorder": false
    });
    $('#importKpi_kpiTable').width('100%').dataTable({
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
            {
                "title": "",
                "data": "kpiId",
                "width": "5%",
                "render": function (data, type, row) {
                    var htmlStr = '';
                    htmlStr += '<div id="importKpi_' + data + '" style="display:none;">' + JSON.stringify(row) + '</div>'; //当前指标数据
                    htmlStr += '<div class="checkbox checkboxEle"><label><input class="importKpiCheckInput" data-id=\'' + data + '\' type="checkbox"></label></div>';
                    $("#importKpi_" + data).data("kpiData", row);
                    return htmlStr;
                }
            },
            {"title": "指标组", "data": "kpiGroupName", "width": "10%"},
            {"title": "指标名称", "data": "kpiName", "width": "10%"},
            {"title": "指标编码", "data": "kpiCode", "width": "15%"},
            {
                "title": "数据类型", "data": "kpiType", "width": "10%", "render": function (data, type, row) {
                    switch (data) {
                        case '1':
                            return '字符串';
                        case '2':
                            return '整型';
                        case '4':
                            return '浮点型';
                        case '5':
                            return '数组';
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
                "title": "取数方式", "data": "fetchType", "width": "10%", "render": function (data, type, row) {
                    switch (data) {
                        case '0':
                            return '数据源';
                        case '1':
                            return '接口';
                        default:
                            return '--';
                    }
                }
            },
            {"title": "指标描述", "data": "kpiDesc", "width": "10%"},
            {"title": "创建人", "data": "createPerson", "width": "10%"},
            {"title": "创建时间", "data": "createDate", "width": "10%"},
            {
                "title": "操作", "data": null, "width": "10%", "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<span type="button" class="cm-tblB" onclick="importKpiModal.showKpiDetail(\'' + row.kpiId + '\', \'' + row.kpiGroupName + '\')">查看</span>';
                    return htmlStr;
                }
            }],
        ajax: {
            url: webpath + '/kpi/paged',
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

$(function () {
    importKpiModal.eventBind();
    initKpiGroup(); // 初始化指标组下拉框
    initKpiType(); // 指标类型
    initImportKpiTable(); // 初始化指标表格
});