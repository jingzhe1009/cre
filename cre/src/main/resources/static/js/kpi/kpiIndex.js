/**
 * 指标库管理页面
 * data:2019/10/14
 * author:bambi
 */

function initPage() {
    // 页面内容初始化
    // kpiModal.showIndex();
    kpiLimitColsModal.initConnSel();

    // 时间选择插件：选择时间和日期
    $(".form-datetime").datetimepicker({
        weekStart: 1,
        todayBtn: 1,
        autoclose: 1,
        todayHighlight: 1,
        startView: 2,
        forceParse: 0,
        showMeridian: 1,
        format: "yyyy-mm-dd hh:ii:ss"
    });

    // 搜索功能
    $('.kpiSearch').click(function () {
        // tableType: 0kpi列表 1组列表
        var tableType = $(this).attr('tableType');
        var inputs;
        if (tableType === '0') {
            inputs = $('#kpiIndexPageContent .kpiBaseWrapper .input-group .form-control');
        } else if (tableType === '1') {
            inputs = $('#kpiIndexPageContent .kpiGroupWrapper .input-group .form-control');
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
        if (tableType === '0') { // 0基础列表
            initKpiTable(obj);
        } else if (tableType === '1') { // 1组列表
            initKpiGroupTable(obj);
        }
    });

    kpiModal.initPage();
    kpiGroupModal.initPage();
    kpiLimitColsModal.initPage();
}

/**
 * 指标管理
 */
var kpiModal = {
    data: {
        handleType: 0,
        kpiDetail: null,
        dbTable: [],
        kpiValueList: []
    },
    initPage: function () {
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
        $('#kpiSourceTypeRadioDiv .kpiSourceTypeRadio').trigger('change');

        // 新增kpi
        $('#addKpi').on('click', function () {
            kpiModal.showKpi(0);
        });

        // 保存指标信息
        $('#saveKpiContent').on('click', function () {
            kpiModal.saveKpi();
        });

        // 退出指标内容页面
        $('#closeKpiContent').on('click', function () {
            // kpiModal.showIndex();
            $('#editKpi').modal('hide');
        });
    },
    // 展示管理页面
    showIndex: function () {
        $('#kpiDefContentWarp').addClass('hide');
        $('#kpiIndexPageContent').removeClass('hide');
        $('#detailTitle').text('');
    },
    // 修改指标校验权限
    updateKpiAuthCheck: function (handleType, kpiId, kpiGroupId) {
        $.ajax({
            url: webpath + '/kpi/update/checkAuth',
            type: 'GET',
            data: {'kpiId': kpiId},
            dataType: "json",
            success: function (data) {
                if (data.status === 0) {
                    kpiModal.showKpi(handleType, kpiId, kpiGroupId);
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 展开指标内容
    showKpi: function (handleType, kpiId, kpiGroupId) {
        // handleType: 0新增 1修改 2查看
        kpiModal.data.handleType = handleType;
        $('#kpiDefContentWarp').removeAttr('kpiId').attr('handleType', handleType);
        // $('#kpiIndexPageContent').addClass('hide');
        // $('#kpiDefContentWarp').removeClass('hide');
        $('.kpiDefBase form')[0].reset();
        $('.kpiDefBase form').validator('cleanUp');
        $('.kpiDBWrapper form').validator('cleanUp');
        kpiLimitColsModal.clearCache(); // 清除各项缓存数据
        $('.kpiDefBase .cron_msg').addClass('hide'); // 恢复提示文字状态
        $('.kpiDefBase .cron_msg').parent().find('.form-control').removeAttr('disabled');
        $('#editKpi .modal-footer button').css('display', 'none');
        // 初始化接口
        $('.kpiApiWrapper form').validator('cleanUp');
        $('#kpiDef_apiReturnInput').val('');
        $('#apiKpiLimitP').show();
        $('#kpiDef_apiParamsTable').hide();
        // 初始化取数方式
        $("#kpiSourceTypeRadioDiv input[name='kpiSourceTypeRadio'][typeValue='0']").prop('checked', true);
        $("#kpiSourceTypeRadioDiv .kpiSourceTypeRadio").trigger('change');
        // 初始化数据源限定字段内容
        $('#kpiDef_dsSel option:first').prop('selected', true);
        $('#kpiDef_dsSel').trigger('change');
        // 初始化接口限定内容
        $('#actRuleConn').val('');
        $('#actRuleConnSel').cm_treeSelect({field: "actRuleConn", data: []});
        $('.kpiDef_apiList li:first').trigger('click');
        $('#kpiDefContentWarp .form-control').attr('disabled', false);
        $('#kpiDefContentWarp input[type="radio"]').attr('disabled', false);
        $('#kpiDefContentWarp #addKpiLimitColRow').removeClass('hide').addClass('show');

        if (handleType === 0) {
            // $('#detailTitle').text('>指标定义');
            $('#editKpi .modal-footer .notView button').css('display', 'inline-block');
            $('#editKpi .modal-title').text('定义指标');
            // 获取接口数据
            initApiList();
            $('#editKpi').modal({'show': 'center', "backdrop": "static"});
            $('#editKpi .form-control').removeAttr('disabled');
        } else if (handleType === 1) {
            $('#editKpi .modal-footer .notView button').css('display', 'inline-block');
            $('#editKpi .modal-title').text('修改指标');
            kpiModal.getDetail(kpiId, kpiGroupId);
            $('#editKpi .form-control').removeAttr('disabled');
        } else if (handleType === 2) {
            $('#editKpi .modal-footer #closeViewKpiContent').css('display', 'inline-block');
            $('#editKpi .modal-title').text('指标详情');
            kpiModal.getDetail(kpiId, kpiGroupId);
            $('#editKpi .form-control').attr('disabled', true);
            // $('#kpiDefContentWarp input[type="radio"]').attr('disabled', true);
            $('#kpiDefContentWarp #addKpiLimitColRow').removeClass('show').addClass('hide');
        }

        $('select.chosen-select').chosen({
            width: '100%',
            no_results_text: '没有找到',    // 当检索时没有找到匹配项时显示的提示文本
            // disable_search_threshold: 10, // 10 个以下的选择项则不显示检索框
            search_contains: true         // 从任意位置开始检索
        });
    },
    getCheckKpi: function (kpiId, callback) {
        $.ajax({ // 验证是否在引用状态
            url: webpath + '/kpi/check/kpiUsed', // 验证是否在引用状态
            type: 'POST',
            dataType: "json",
            data: {'kpiId': kpiId},
            success: function (data) {
                if (data.status === 0) { // 未引用
                    callback();
                } else if (data.status === -1) { // 引用中
                    callback();
                    if (kpiModal.data.handleType !== 2) {
                        $('.kpiDefBase .cron_msg').removeClass('hide'); // 改变msg状态
                        $('.kpiDefBase .cron_msg').parent().find('.form-control').attr('disabled', true);
                    }
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },
    // 获取指标详情
    getDetail: function (kpiId, kpiGroupId) {
        kpiModal.getCheckKpi(kpiId, function () {
            $.ajax({
                url: webpath + '/kpi/detail',
                type: 'POST',
                dataType: 'json',
                data: {'kpiId': kpiId},
                success: function (data) {
                    if (data.status === 0) {
                        kpiModal.data.kpiDetail = data.data;
                        kpiModal.echoKpiData(data.data, kpiGroupId);
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        });
    },
    // 回显指标
    echoKpiData: function (data, kpiGroupId) {
        // 回显组信息(详情没有该字段)
        $('.kpiDefBase .kpiGroupSelector option[group-id="' + kpiGroupId + '"]').prop('selected', true);

        var fetchType = data['fetchType'];
        if (fetchType === '0') { // ds
            $("#kpiDef_dsSel option[data-id='" + data['dbId'] + "']").prop('selected', true);
            $("#kpiDef_dsSel").trigger('change', [data['tableId'], data['kpiValueSource']]);
            kpiLimitColsModal.echoKpiFetchLimitersList = data['kpiFetchLimitersList'];
            initApiList();
        }
        if (fetchType === '1') { // api
            initApiList(function () {
                $('#kpiDef_apiInput').val(data.apiId);
                $('#kpiDef_apiInput').trigger('change');
            });
        }

        for (var key in data) {
            if (key === 'kpiGroupId') {
                continue;
            }
            if (key === 'kpiId') {
                $('#kpiDefContentWarp').attr('kpiId', data[key]);
                continue;
            }
            if (key === 'kpiType') { // 数据类型
                $('#kpiTypeSel option[kpiType="' + data[key] + '"]').prop('selected', true);
                continue;
            }
            if (key === 'fetchType') { // 取值方式
                $("input[name='kpiSourceTypeRadio'][typeValue='" + data[key] + "']").prop('checked', true);
                $('#kpiSourceTypeRadioDiv .kpiSourceTypeRadio').trigger('change');
                continue;
            }
            if (key === 'dbId') { // 数据源
                continue;
            }
            if (key === 'tableId') { // 表
                continue;
            }
            if (key === 'kpiValueSource') { // 取值字段
                continue;
            }
            if (key === 'kpiFetchLimitersList') {
                continue;
            }

            var target = $("#kpiDefContentWarp .form-control[col-name='" + key + "']");
            if (target.length > 0) {
                $(target).val(data[key]);
            }
        }
        $('#editKpi').modal({'show': 'center', "backdrop": "static"});
    },
    // 保存指标
    saveKpi: function () {
        // 表单验证
        if (!$('.kpiDefBase form').isValid()) {
            return;
        }
        var fetchType = $("input[name='kpiSourceTypeRadio']:checked").attr('typeValue');
        if (fetchType === '0') { // DS_kpi
            if (!$('#kpiDef_dsSel option:selected').attr('data-id')) {
                failedMessager.show('请选择数据源！');
                return;
            }
            if (!$('#kpiDef_tableInput').val()) {
                failedMessager.show('请选择表！');
                return;
            }
            if (!$('#kpiDef_colInput').val()) {
                failedMessager.show('取值字段无效！');
                return;
            }
            if ($('#dsKpiLimitCols .kpiLimitColRow').length <= 0) {
                failedMessager.show('限定字段不能为空！');
                return;
            }
        } else if (fetchType === '1') { // API_kpi
            var apiIndex = $('#kpiDef_apiInput')[0].options.selectedIndex;
            if (apiIndex === 0) {
                failedMessager.show('请选择接口！');
                return;
            }
            if (!$('#actRuleConn').val()) {
                failedMessager.show('取值字段无效');
                return;
            }
        }
        var handleType = $('#kpiDefContentWarp').attr('handleType'); // 0新增 1修改
        var kpiFetchLimitersList = kpiModal.getKpiLimitList(handleType, fetchType);
        if (kpiFetchLimitersList.length <= 0 && fetchType === '0') {
            failedMessager.show('限定字段填写无效！请检查！');
            return;
        }
        var obj = kpiModal.getKpiData(handleType, fetchType, kpiFetchLimitersList);
        if (obj) {
            var urlStr = '';
            if (handleType === '0') {
                urlStr = '/kpi/create';
            } else if (handleType === '1') {
                urlStr = '/kpi/update';
            } else {
                return;
            }

            $.ajax({
                url: webpath + urlStr,
                type: 'POST',
                contentType: 'application/json', // 定义发送给服务器的数据格式
                dataType: "json",
                data: JSON.stringify(obj),
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('保存成功');
                        // initKpiTable();
                        $('.kpiSearch').trigger('click');
                        // kpiModal.showIndex();
                        $('#editKpi').modal('hide');
                    } else {
                        failedMessager.show(data.msg);
                    }
                }
            });
        }
    },
    // 获取指标数据 handleType 0新增 1修改; fetchType 0数据源 1接口
    getKpiData: function (handleType, fetchType, kpiFetchLimitersList) {
        var obj = {
            dbId: '',
            dbAlias: '',
            dbType: '',
            tableId: '',
            tableCode: '',
            apiId: '',
            apiName: ''
        };
        if (handleType === '1') { // 修改需要加上kpiId
            obj['kpiId'] = $('#kpiDefContentWarp').attr('kpiId');
        }
        obj['kpiGroupId'] = $('.kpiDefBase .kpiGroupSelector option:selected').attr('group-id');
        obj['kpiName'] = $.trim($('#kpiNameInput').val());
        obj['kpiCode'] = $.trim($('#kpiCodeInput').val());
        obj['kpiType'] = $('#kpiTypeSel option:selected').attr('kpiType');
        obj['kpiDesc'] = $.trim($('#kpiDes').val());
        obj['kpiDefaultValue'] = $.trim($('#kpiDefaultValue').val());
        obj['fetchType'] = fetchType;
        obj['kpiFetchLimitersList'] = kpiFetchLimitersList;

        if (fetchType === '0') {
            obj['dbId'] = $('#kpiDef_dsSel option:selected').attr('data-id');
            obj['dbAlias'] = $('#kpiDef_dsSel option:selected').val();
            obj['dbType'] = $('#kpiDef_dsSel option:selected').attr('dbType');
            var dbIndex = $('#kpiDef_tableInput')[0].options.selectedIndex - 1;
            var kpiValueIndex = $('#kpiDef_colInput')[0].options.selectedIndex - 1;
            obj['tableId'] = $('#kpiDef_tableInput').val();
            obj['tableCode'] = kpiModal.data.dbTable[dbIndex].TABLECODE;
            var colId = $('#kpiDef_colInput').val();
            var colObj = kpiModal.data.kpiValueList[kpiValueIndex];
            obj['kpiValueSource'] = colId;
            obj['kpiValueSourceCode'] = colObj.COLUMNCODE;
            obj['kpiValueSourceName'] = colObj.COLUMNNAME;
        }
        if (fetchType === '1') {
            var apiIndex = $('#kpiDef_apiInput')[0].options.selectedIndex;
            var apiData = kpiLimitColsModal.apiData[apiIndex - 1];
            obj['apiId'] = apiData.apiId;
            obj['apiName'] = apiData.apiName;
            obj['kpiValueSource'] = apiData.apiContent.newReturnParamId;
            obj['kpiValueSourceCode'] = $('#actRuleConn').val();
            obj['kpiValueSourceName'] = $('#actRuleConnText').val();
        }

        return obj;
    },
    // 获取限定字段数据
    getKpiLimitList: function (handleType, fetchType) {
        var kpiFetchLimitersList = [];
        if (fetchType === '0') {
            var rowsArr = $('#dsKpiLimitCols .kpiLimitColRow');
            for (var i = 0; i < rowsArr.length; i++) {
                var limitObj = {};
                var rowColId = $(rowsArr[i]).find('.limitRowColInput').attr('colId');
                var rowVarId = $(rowsArr[i]).find('.limitRowParamInput').attr('variableId');
                if (!(rowColId && rowVarId)) {
                    return [];
                }
                var rowColObj = kpiLimitColsModal.colListObj[rowColId];
                var rowVarObj = kpiLimitColsModal.varListObj[rowVarId];
                limitObj['columnId'] = rowColId;
                limitObj['columnCode'] = rowColObj.COLUMNCODE;
                limitObj['columnName'] = rowColObj.COLUMNNAME;
                limitObj['variableId'] = rowVarId;
                limitObj['variableCode'] = rowVarObj.variableCode;
                limitObj['variableName'] = rowVarObj.variableAlias;
                limitObj['variableTypeId'] = rowVarObj.typeId;
                limitObj['fetchType'] = fetchType;
                if (handleType === '1') {
                    limitObj['kpiId'] = $('#kpiDefContentWarp').attr('kpiId');
                }
                kpiFetchLimitersList.push(limitObj);
            }
        }
        if (fetchType === '1') {
            var apiIndex = $('#kpiDef_apiInput')[0].options.selectedIndex;
            var param = kpiLimitColsModal.apiData[apiIndex - 1].apiContent.newParam;
            for (var j = 0; j < param.length; j++) {
                var obj = {
                    fetchType: fetchType,
                    variableCode: param[j].variableCode,
                    variableId: param[j].variableId,
                    variableName: param[j].variableAlias,
                    variableTypeId: param[j].typeId
                };
                if (handleType === '1') {
                    obj.kpiId = $('#kpiDefContentWarp').attr('kpiId');
                }
                kpiFetchLimitersList.push(obj);
            }
        }
        return kpiFetchLimitersList;
    },
    // 删除指标
    deleteKpi: function (kpiId) {
        if (kpiId) {
            $.ajax({ // 删除权限校验
                url: webpath + '/kpi/delete/checkAuth',
                type: 'GET',
                dataType: "json",
                data: {'kpiId': kpiId},
                success: function (data) {
                    if (data.status === 0) {
                        confirmAlert.show('是否确认删除？', function () {
                            $.ajax({
                                url: webpath + '/kpi/delete',
                                type: 'POST',
                                dataType: "json",
                                data: {'kpiId': kpiId},
                                success: function (data) {
                                    if (data.status === 0) {
                                        // initKpiTable();
                                        $('.kpiSearch').trigger('click');
                                        successMessager.show('删除成功');
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
    // 选择表的回调
    handleDbTableChange: function (e) {
        // var index = e[ 0 ].options.selectedIndex - 1;
        // var data = kpiModal.data.dbTable[ index ];

        // var tableName = data.TABLENAME;
        // var tableId = $( e ).val();
        // var tableCode = $(this).attr('tableCode');
        // $('#kpiDef_tableInput').val(tableName);
        // if (tableId) {
        //     $('#kpiDef_tableInput').attr('tableId', tableId);
        //     $('#kpiDef_tableInput').attr('tableCode', tableCode);
        kpiLimitColsModal.showDsKpiLimitContent();
        // } else {
        //     $('#kpiDef_tableInput').removeAttr('tableId');
        //     $('#kpiDef_tableInput').removeAttr('tableCode');
        //     kpiLimitColsModal.showDsKpiLimitP();
        // }
        kpiModal.data.echoTableId = $(e).val();
        initTableCols();
    },
    handleKpiValueChange: function (e) {

    }
}

// 限定字段
var kpiLimitColsModal = {
    rowNum: 0,
    colListObj: {}, // 字段列表对象 {colId: colObj}
    colListHtmlStrData: '', // 字段列表htmlStr
    varListObj: {}, // 参数列表对象 {varId: varObj}
    varListHtmlStrData: '', // 公共参数扁平化列表htmlStr
    echoKpiFetchLimitersList: [],
    apiData: [],
    initPage: function () {
        // 新增一条限定条件
        $('#addKpiLimitColRow').on('click', function () {
            kpiLimitColsModal.addLimitCol();
        });
        // 删除一条限定条件
        $('#dsKpiLimitCols').on('click', '.delLimitColRow', function () {
            $(this).parent().remove();
        });
    },
    clearCache: function () {
        kpiLimitColsModal.rowNum = 0;
        kpiLimitColsModal.colListObj = {};
        kpiLimitColsModal.colListHtmlStrData = '';
        kpiLimitColsModal.echoKpiFetchLimitersList = [];
    },
    // 初始化连接符号
    initConnSel: function () {
        var limitColsConnSelData = [
            {key: 'or', text: "任一", selected: true},
            {key: 'and', text: "全部"}
        ];
        $('#kpiDefContentWarp .limitColsConnSel').cm_select({field: "limitColsConn", data: limitColsConnSelData});
    },
    // dsKpi: 为参数li绑定事件
    varLiBind: function (that) {
        var variableAlias = that.text();
        var variableId = that.attr('data-id');
        var inputEle = that.parent().siblings('input');
        inputEle.val(variableAlias);
        if (variableId) {
            inputEle.attr('variableId', variableId);
        } else {
            inputEle.removeAttr('variableId');
        }
    },
    // dsKpi: 为列下拉框项绑定事件
    colLiBind: function (that) {
        var colName = that.text();
        var colId = that.attr('data-id');
        var inputEle = that.parent().siblings('input');
        inputEle.val(colName);
        if (colId) {
            inputEle.attr('colId', colId);
        } else {
            inputEle.removeAttr('colId');
        }
    },
    // dsKpi: 为ul初始化填充varList
    initVarList: function (container) {
        container.html(kpiLimitColsModal.varListHtmlStrData);
    },
    // dsKpi: 隐藏限定字段, 展示提示语
    showDsKpiLimitP: function () {
        $('#dsKpiLimitP').removeClass('hide');
        $('#dsKpiLimitContent').addClass('hide');
    },
    // dsKpi: 展示限定字段, 隐藏提示语
    showDsKpiLimitContent: function () {
        $('#dsKpiLimitP').addClass('hide');
        $('#dsKpiLimitContent').removeClass('hide');
        $('#dsKpiLimitCols').empty();
    },
    // dsKpi: 添加限定字段
    //      echoColId & echoVarId [回显colId&varId] (如果是通过新建进入则没有此参数)
    addLimitCol: function (echoColId, echoVarId) {
        var idx = kpiLimitColsModal.rowNum++;
        var newHtmlStr = '';
        newHtmlStr += '<div id="kpiLimitColRow' + idx + '" class="kpiLimitColRow">';
        newHtmlStr += ' <div class="input-control has-icon-right">'; // 表字段start
        // newHtmlStr += '     <input id="limitCol' + idx + '" class="form-control limitRowColInput" ' + (kpiModal.data.handleType === 2 ? 'disabled' : '') + ' placeholder="--请选择 表字段--">';
        newHtmlStr += '     <input id="limitCol' + idx + '" class="form-control limitRowColInput" placeholder="--请选择 表字段--">';
        newHtmlStr += '     <label for="limitCol' + idx + '" class="input-control-icon-right" data-toggle="dropdown"><i class="icon icon-caret-down"></i></label>';
        newHtmlStr += '     <ul class="dropdown-menu kpiLimitWarpDropDown kpiDef_tbColsList"></ul>';
        newHtmlStr += ' </div>'; // 表字段end
        newHtmlStr += ' <div class="limitLinkIcon"><i class="icon icon-link"></i></div>'; // 绑定图标
        newHtmlStr += ' <div class="input-control has-icon-right">'; // 绑定公共参数start
        // newHtmlStr += '     <input id="limitParam' + idx + '" class="form-control limitRowParamInput" ' + (kpiModal.data.handleType === 2 ? 'disabled' : '') + ' placeholder="--请选择 参数--">';
        newHtmlStr += '     <input id="limitParam' + idx + '" class="form-control limitRowParamInput" placeholder="--请选择 参数--">';
        newHtmlStr += '     <label for="limitParam' + idx + '" class="input-control-icon-right" data-toggle="dropdown"><i class="icon icon-caret-down"></i></label>';
        newHtmlStr += '     <ul class="dropdown-menu kpiLimitWarpDropDown kpiLimitParamsList"></ul>';
        newHtmlStr += ' </div>'; // 绑定公共参数end
        if (kpiModal.data.handleType !== 2) {
            newHtmlStr += ' <span class="delLimitColRow"></span>'; // 删除行图标
        }
        newHtmlStr += '</div>';
        $('#dsKpiLimitCols').append(newHtmlStr);
        $('#kpiLimitColRow' + idx).find('.kpiDef_tbColsList').html(kpiLimitColsModal.colListHtmlStrData); // 加载字段列表
        kpiLimitColsModal.initVarList($('#kpiLimitColRow' + idx).find('.kpiLimitParamsList')); // 加载参数列表
        if (echoColId) {
            $('#kpiLimitColRow' + idx).find('.kpiDef_tbColsList').children("li[data-id='" + echoColId + "']").trigger('click');
        }
        if (echoVarId) {
            $('#kpiLimitColRow' + idx).find('.kpiLimitParamsList').children("li[data-id='" + echoVarId + "']").trigger('click');
        }
    },
    // 接口选择回调
    apiKpiChange: function (that) {
        var index = that.context.options.selectedIndex;
        if (index !== 0) {
            $('#apiKpiLimitP').hide();
            $('#kpiDef_apiParamsTable').show();
            var data = kpiLimitColsModal.apiData[index - 1];
            // 设置取值字段
            kpiLimitColsModal.getApiValueTree();
            $('#kpiDef_apiReturnInput').val(data.apiContent.returnValue);
            // 设置接口参数表格数据
            var param = data.apiContent.newParam;
            kpiLimitColsModal.loadApiParamTable(param);
        } else {
            // 设置取值字段为空
            $('#kpiDef_apiReturnInput').val('');
            $('#apiKpiLimitP').show();
            $('#kpiDef_apiParamsTable').hide();
        }
    },
    getApiValueTree: function () {
        var apiIndex = $('#kpiDef_apiInput')[0].options.selectedIndex;
        var apiId = kpiLimitColsModal.apiData[apiIndex - 1].apiId;
        $.ajax({
            url: webpath + '/api/getApiValueTree?apiId=' + apiId + '&apiValueType=0',
            type: 'GET',
            dataType: "json",
            data: {},
            success: function (data) {
                var kpiDetail = kpiModal.data.kpiDetail;
                var kpiObj = $('#actRuleConnSel').cm_treeSelect(
                    {
                        field: "actRuleConn",
                        data: kpiLimitColsModal.renderTree(data.data)
                    }
                );
                if (kpiDetail) {
                    kpiObj.setValue(kpiDetail.kpiValueSourceCode);
                }
            }
        });
    },
    renderTree: function (data) {
        var tree = [];
        for (var i = 0; i < data.length; i++) {
            var item = {
                title: data[i].variableAlias,
                value: data[i].variableCode,
                id: data[i].variableCode
            };
            if (data[i].variableNestedList.length !== 0) {
                item.children = kpiLimitColsModal.renderTree(data[i].variableNestedList);
            }
            tree.push(item);
        }
        return tree;
    },
    loadApiParamTable: function (data) {
        $('#kpiDef_apiParamsTable').width('100%').dataTable({
            "searching": false, // 是否开启搜索功能
            "ordering": false,
            "destroy": true,
            "bLengthChange": false,
            "paging": false,
            "info": false,
            "columns": [
                {"title": "变量别名", "data": "variableAlias"},
                {"title": "变量编码", "data": "variableCode"},
                {"title": "参数组", "data": "variableGroupName"},
                {"title": "变量类型", "data": "typeValue"}
            ],
            "data": data,
        });
    }
}

/**
 * 初始化 指标表格
 */
function initKpiTable(obj) {
    $('#kpiTable').width('100%').dataTable({
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
            {"title": "指标组", "data": "kpiGroupName", "width": "10%"},
            {"title": "指标名称", "data": "kpiName", "width": "11%"},
            {"title": "指标编码", "data": "kpiCode", "width": "11%"},
            {
                "title": "数据类型", "data": "kpiType", "width": "9%", "render": function (data, type, row) {
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
                "title": "取数方式", "data": "fetchType", "width": "9%", "render": function (data, type, row) {
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
            {"title": "指标描述", "data": "kpiDesc", "width": "11%"},
            {"title": "创建人", "data": "createPerson", "width": "9%"},
            {"title": "创建时间", "data": "createDate", "width": "10%"},
            {
                "title": "操作", "data": null, "width": "20%", "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<span type="button" class="cm-tblB" onclick="kpiModal.showKpi(2, \'' + row.kpiId + '\', \'' + row.kpiGroupId + '\')">查看</span>';
                    // htmlStr += '<span type="button" class="cm-tblB" onclick="kpiModal.showKpi(1, \'' + row.kpiId + '\', \'' + row.kpiGroupId + '\')">修改</span>';
                    htmlStr += '<span type="button" class="cm-tblB" onclick="kpiModal.updateKpiAuthCheck(1, \'' + row.kpiId + '\', \'' + row.kpiGroupId + '\')">修改</span>';
                    htmlStr += '<span type="button" class="cm-tblC delBtn" onclick="kpiModal.deleteKpi(\'' + row.kpiId + '\')">删除</span>';
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

/**
 * 指标组
 */
var kpiGroupModal = {
    initPage: function () {
        $('#addKpiGroup').on('click', function () { // 添加指标组
            kpiGroupModal.show(0);
        });
        $('#saveKpiGroup').on('click', function () { // 保存指标组
            kpiGroupModal.saveGroup();
        });
    },
    show: function (handleType, $this) {
        var detail = {};
        if ($this) {
            var curRow = $this.parentNode.parentNode;
            detail = $('#kpiGroupTable').DataTable().row(curRow).data();
        }
        // handleType: 0新增 1修改
        $('#kpiGroupAlert form')[0].reset();
        $('#kpiGroupAlert .modal-footer button').css('display', 'none');
        $('#kpiGroupAlert .form-control').attr('disabled', false);
        if (handleType == 0) {
            $('#kpiGroupAlert .modal-footer .notView button').css('display', 'inline-block');
            $('#kpiGroupAlert .modal-title').text('').text('添加指标组');
        } else if (handleType == 1) {
            $('#kpiGroupAlert .modal-footer .notView button').css('display', 'inline-block');
            $('#kpiGroupAlert .modal-title').text('').text('修改指标组');
            kpiGroupModal.echoGroupData(detail);
        } else if (handleType == 2) {
            $('#kpiGroupAlert .modal-footer #closeViewKpiGroup').css('display', 'inline-block');
            $('#kpiGroupAlert .modal-title').text('').text('查看指标组');
            $('#kpiGroupAlert .form-control').attr('disabled', true);
            kpiGroupModal.echoGroupData(detail);
        }
        $('#kpiGroupAlert').attr('handleType', handleType).modal({'show': 'center', "backdrop": "static"});
    },
    // 关闭添加指标组弹框
    hiddenAddGroupAlert: function () {
        $('#kpiGroupAlert').modal('hide');
    },
    // 回显组数据
    echoGroupData: function (detail) {
        if (detail) {
            var data = detail ? detail : {};
            for (var key in data) {
                if (key === 'kpiGroupId') { // 回显组id
                    $('#kpiGroupAlert').attr('groupId', data[key]);
                    continue;
                }
                var target = $("#kpiGroupAlert .form-control[col-name='" + key + "']");
                if (target.length > 0) {
                    $(target).val(data[key]);
                }
            }
        }
    },
    // 保存组数据
    saveGroup: function () {
        // 表单验证
        if (!$('#kpiGroupAlert form').isValid()) {
            return;
        }
        var handleType = $('#kpiGroupAlert').attr('handleType'); // 0新增 1修改
        var urlStr = '';
        if (handleType == 0) {
            urlStr = '/kpi/group/create';
        } else if (handleType == 1) {
            urlStr = '/kpi/group/update';
        } else {
            return;
        }
        var obj = kpiGroupModal.getGroupData(handleType);
        if (obj) {
            $.ajax({
                url: webpath + urlStr,
                type: 'POST',
                dataType: "json",
                data: obj,
                success: function (data) {
                    if (data.status === 0) {
                        successMessager.show('保存成功');
                        // initKpiGroupTable();
                        // initKpiTable();
                        $('.kpiSearch').trigger('click');
                        initKpiGroup(); // 刷新模型组下拉框
                        kpiGroupModal.hiddenAddGroupAlert();
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
    // 获取组表单数据
    getGroupData: function (handleType) {
        var obj = {};
        var inputs = $('#kpiGroupAlert form .form-control');
        for (var i = 0; i < inputs.length; i++) {
            obj[$(inputs[i]).attr('col-name')] = $(inputs[i]).val();
        }
        if (handleType == 1) { //修改需要加上组id
            obj['kpiGroupId'] = $('#kpiGroupAlert').attr('groupId');
        }
        return obj;
    },
    // 删除指标组
    deleteGroup: function (groupId) {
        if (groupId) {
            confirmAlert.show('是否确认删除？', function () {
                $.ajax({
                    url: webpath + '/kpi/group/delete',
                    type: 'POST',
                    dataType: "json",
                    data: {'kpiGroupId': groupId},
                    success: function (data) {
                        if (data.status === 0) {
                            successMessager.show('删除成功');
                            // initKpiGroupTable();
                            // initKpiTable();
                            $('.kpiSearch').trigger('click');
                            initKpiGroup(); // 刷新模型组下拉框
                        } else {
                            failedMessager.show(data.msg);
                        }
                    }
                });
            });
        }
    }
}

/**
 * 初始化 组表格
 */
function initKpiGroupTable(obj) {
    $('#kpiGroupTable').width('100%').dataTable({
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
            {"title": "指标组名称", "data": "kpiGroupName", "width": "20%"},
            {"title": "描述", "data": "kpiGroupDesc", "width": "20%"},
            {"title": "创建时间", "data": "createDate", "width": "20%"},
            {"title": "创建人", "data": "createPerson", "width": "20%"},
            {
                "title": "操作", "data": null, "width": "20%", "render": function (data, type, row) {
                    var htmlStr = "";
                    htmlStr += '<span type="button" class="cm-tblB" onclick="kpiGroupModal.show(2, this)">查看</span>';
                    htmlStr += '<span type="button" class="cm-tblB" onclick="kpiGroupModal.show(1, this)">修改</span>';
                    htmlStr += '<span type="button" class="cm-tblC delBtn" onclick="kpiGroupModal.deleteGroup(\'' + row.kpiGroupId + '\')">删除</span>';
                    return htmlStr;
                }
            }],
        ajax: {
            url: webpath + '/kpi/group/paged',
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

//  初始化指标组下拉框
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
            } else {
                htmlStr_list += "<li group-id='empty'><a>无</a></li>";
                htmlStr_selector += "<option group-id='empty'>无</option>";
            }
            $('#kpiIndexPageContent .kpiGroupList').empty().html(htmlStr_list); // 下拉框
            $('.kpiGroupSelector').empty().html(htmlStr_selector); // 表单
        },
        complete: function () {
            // 绑定事件
            $('#kpiIndexPageContent .kpiGroupList>li').unbind('click').on('click', function () {
                $(this).parent().siblings('.form-control').val($(this).first().text());
            });
        }
    });
}

// 初始化指标类型
function initKpiType() {
    if (kpiTypeList) {
        var htmlStr = '';
        for (var i = 0; i < kpiTypeList.length; i++) {
            htmlStr += '<option kpiType=\'' + kpiTypeList[i].key + '\'>' + kpiTypeList[i].text + '</option>';
        }
        $('#kpiTypeSel').html('').html(htmlStr);
    }
}

// 加载数据源下拉列表
function initDSList() {
    $.ajax({
        url: webpath + '/datasource/listAll',
        type: 'GET',
        dataType: "json",
        data: {},
        success: function (data) {
            var htmlStr_selector = '<option>--请选择--</option>';
            if (data.data.length > 0) {
                var data = data.data;
                for (var i = 0; i < data.length; i++) {
                    htmlStr_selector += '<option data-id=\'' + data[i].dbId + '\' dbType=\'' + data[i].dbType + '\'>' + data[i].dbAlias + '</option>';
                }
            }
            $('#kpiDef_dsSel').empty().html(htmlStr_selector);
            $('#kpiDef_dsSel option:first').prop('selected', true);
            $('#kpiDef_dsSel').unbind().on('change', function (event, echoTableId, echoColId) {
                kpiModal.data.echoTableId = echoTableId;
                kpiModal.data.echoColId = echoColId;
                initDbTables($('#kpiDef_dsSel option:selected').attr('data-id'), echoTableId, echoColId);
            });
        }
    });
}

// 根据dbId加载元数据表
function initDbTables(dbId, echoTableId, echoColId) {
    if (!dbId) {
        // 复原tables列表
        $("#kpiDef_tableInput").val('');
        $('#kpiDef_tableInput').html('');
        $('#kpiDef_tableInput').trigger('chosen:updated');
        // 复原cols列表
        $('#kpiDef_colInput').val('');
        $('#kpiDef_colInput').html('');
        $('#kpiDef_colInput').trigger('chosen:updated');
        // 复原限定字段内容
        kpiLimitColsModal.showDsKpiLimitP();
        // 清空列数据缓存
        kpiLimitColsModal.colListObj = {};
        return;
    }
    $.ajax({
        url: webpath + '/datasource/metadata/table/list',
        type: 'POST',
        dataType: "json",
        data: {"dbId": dbId},
        success: function (data) {
            kpiModal.data.dbTable = data;
            var htmlStr = '<option value=""></option>';
            if (data.length > 0) {
                for (var i = 0; i < data.length; i++) {
                    htmlStr += '<option value="' + data[i].TABLEID + '">' + data[i].TABLENAME + '</option>';
                }
            }
            $('#kpiDef_tableInput').html(htmlStr);
            if (kpiModal.data.echoTableId !== void 0) {
                $("#kpiDef_tableInput").val(kpiModal.data.echoTableId);
                kpiModal.handleDbTableChange($("#kpiDef_tableInput"));
            } else {
                initTableCols(kpiModal.data.echoTableId);
                kpiLimitColsModal.showDsKpiLimitP();
            }
            $('#kpiDef_tableInput').trigger('chosen:updated');

            // $('.kpiDef_dsTablesList li').unbind().on('click', function (event, echoColId) {
            //     var tableName = $(this).text();
            //     var tableId = $(this).attr('data-id');
            //     var tableCode = $(this).attr('tableCode');
            //     $('#kpiDef_tableInput').val(tableName);
            //     if (tableId) {
            //         $('#kpiDef_tableInput').attr('tableId', tableId);
            //         $('#kpiDef_tableInput').attr('tableCode', tableCode);
            //         kpiLimitColsModal.showDsKpiLimitContent();
            //     } else {
            //         $('#kpiDef_tableInput').removeAttr('tableId');
            //         $('#kpiDef_tableInput').removeAttr('tableCode');
            //         kpiLimitColsModal.showDsKpiLimitP();
            //     }
            //     initTableCols(tableId, echoColId);
            // });
            //
            // $('.kpiDef_dsTablesList li:first').trigger('click');
        },
        complete: function () {
            if (echoTableId) {
                $("#kpiDef_tableInput").val(echoTableId);
            }
        }
    });
}

// 根据元数据表id加载元数据列
function initTableCols(tableId, echoColId) {
    if (!kpiModal.data.echoTableId) {
        // 复原cols列表
        $('#kpiDef_colInput').val('');
        $('#kpiDef_colInput').html('');
        $('#kpiDef_colInput').trigger('chosen:updated');
        // 清空列数据缓存
        kpiLimitColsModal.colListObj = {};
        kpiLimitColsModal.colListHtmlStrData = '';
        return;
    }
    $.ajax({
        url: webpath + '/batchdata/column/columnList',
        type: 'POST',
        dataType: "json",
        data: {"tableId": kpiModal.data.echoTableId},
        success: function (data) {
            kpiLimitColsModal.colListObj = {};
            kpiLimitColsModal.colListHtmlStrData = '';
            var htmlStr = '<option value=""></option>';
            var limitStr = '<li onclick="kpiLimitColsModal.colLiBind($(this))"><a>--请选择--</a></li>';
            if (data.data.length > 0) {
                var data = data.data;
                kpiModal.data.kpiValueList = data;
                for (var i = 0; i < data.length; i++) {
                    htmlStr += '<option value="' + data[i].COLUMNID + '">' + data[i].COLUMNNAME + '</option>';
                    limitStr += '<li onclick="kpiLimitColsModal.colLiBind($(this))" data-id=\'' + data[i].COLUMNID + '\'><a>' + data[i].COLUMNNAME + '</a></li>';
                    kpiLimitColsModal.colListObj[data[i].COLUMNID] = data[i]; // 缓存列内容对象
                }
            }
            kpiLimitColsModal.colListHtmlStrData = limitStr;
            $('#kpiDef_colInput').html(htmlStr);
            if (kpiModal.data.echoColId !== void 0) {
                $("#kpiDef_colInput").val(kpiModal.data.echoColId);
            }
            $('#kpiDef_colInput').trigger('chosen:updated');
            // $('.kpiDef_tbColsList li:first').trigger('click');
        },
        complete: function () {
            if (kpiModal.data.echoColId !== void 0) {
                // $("#kpiValueSourceList li[data-id='" + kpiModal.data.echoColId + "']").trigger('click'); // 修改回显列
                var echoLimits = kpiLimitColsModal.echoKpiFetchLimitersList;
                for (var i = 0; i < echoLimits.length; i++) {
                    kpiLimitColsModal.addLimitCol(echoLimits[i].columnId, echoLimits[i].variableId);
                }
            }
        }
    });
}

// 初始化公共参数数据
function initCommonParamListData() {
    $.ajax({
        url: webpath + '/variable/pub/list/flat/list',
        type: 'GET',
        dataType: "json",
        data: {},
        success: function (data) {
            kpiLimitColsModal.varsListObj = {}; // 先清空参数列表对象缓存
            var htmlStr = '<li onclick="kpiLimitColsModal.varLiBind($(this))"><a>--请选择--</a></li>';
            var varListObj = {};
            if (data.data.length > 0) {
                var data = data.data;
                for (var i = 0; i < data.length; i++) {
                    varListObj[data[i].variableId] = data[i]; // 初始化缓存至对象中
                    htmlStr += '<li onclick="kpiLimitColsModal.varLiBind($(this))" data-id=\'' + data[i].variableId + '\'><a>' + data[i].variableAlias + '</a></li>';
                }
            }
            kpiLimitColsModal.varListObj = varListObj;
            kpiLimitColsModal.varListHtmlStrData = htmlStr;
            $('.kpiDef_tbColsList li:first').trigger('click');
        }
    });
}

// 公共接口
function initApiList(callback) {
    $.ajax({
        url: webpath + '/api/pub/listAll',
        type: 'GET',
        dataType: "json",
        data: {},
        success: function (data) {
            if (Array.isArray(data.data)) {
                var htmlStr_list = '<option value=""><a>--请选择--</a></option>';
                if (data.data.length > 0) {
                    var apiData = data.data;
                    kpiLimitColsModal.apiData = [];
                    for (var i = 0; i < apiData.length; i++) {
                        apiData[i].apiContent = JSON.parse(apiData[i].apiContent);
                        kpiLimitColsModal.apiData.push(apiData[i]);
                        htmlStr_list += (
                            '<option value="' + apiData[i].apiId + '">' +
                            '<a>' + apiData[i].apiName + '</a>' +
                            '</option>'
                        );
                    }
                }
                $('#kpiDefContentWarp #kpiDef_apiInput').html(htmlStr_list);
                callback && callback();
            }
        }
    });
}

$(function () {
    initPage();
    initKpiTable(); // 初始化指标table
    initKpiGroupTable(); // 初始化组table
    initKpiGroup(); // 初始化指标组下拉框
    initKpiType(); // 初始化指标类型下拉框
    initDSList(); // 初始化数据源下拉表
    initCommonParamListData(); // 初始化参数数据
    // initApiList(); // 初始化公共接口下拉列表
});