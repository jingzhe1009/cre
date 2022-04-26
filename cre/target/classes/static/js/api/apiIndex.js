//初始化表格
function initTable() {
    //搜索按钮
    $("#apiSearchBth").click(function () {
        $("#table1").dataTable().fnDraw(false);//重新绘制表格
    });
    //初始化表格
    var pageLength = 10;//每页显示条数
    $.extend($.fn.dataTable.defaults, {
        info: true,
        "serverSide": true,
//		"paging": true, 
        "pageLength": pageLength
    });
    $('#table1').width('100%').dataTable({
        "searching": false,
        "ordering": false,
        "destroy": true,
        "bLengthChange": false,
        "pagingType": "full_numbers",
        "columns": [
            {"title": "接口名称", "data": "apiName"},
            {
                "title": "接口类型", "data": "apiType", "render": function (data) {
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
                "title": "是否记录日志", "data": "isLog", "render": function (data) {
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
            {"title": "接口描述", "data": "apiDesc"},
            {"title": "创建时间", "data": "createDate"},
            {
                "title": "操作", "data": null, "render": function (data, type, row) {
                    var html = '<div>';
                    html += '<div id="row_' + row.apiId + '" style="display:none;">' + JSON.stringify(row) + '</div>'
                    html += '<span onclick="modifyRow(\'' + row.apiId + '\', 1);" class="btn-sm cm-tblB">查看</span>';
                    html += '<span onclick="modifyRow(\'' + row.apiId + '\', 0);" class="btn-sm cm-tblB">修改</span>';
                    html += '<span onclick="delRow(\'' + row.apiId + '\');" class="btn-sm cm-tblC">删除</span>';
                    html += '<span class="cm-tblB setCommon" onclick="setCommonModal.show(\'' + row.apiId + '\')">设为公有</span>';
                    html += '</div>';
                    $("#row_" + row.apiId).data("rowData", row);
                    return html;
                }
            }
        ],
        ajax: {
            url: webpath + '/api/list',
            "type": 'POST',
            "data": function (d) {//查询参数
                return $.extend({}, d, {
                    "apiName": $.trim($("#apiName").val()),
                    "folderId": $.trim($("#folderId").val())
                });
            }
        },
        "fnDrawCallback": function (oSettings, json) {
            // $("tr:even").css("background-color", "#fbfbfd");
            // $("table:eq(0) th").css("background-color", "#f6f7fb");
        }
    });
}

var modifyApiModal = {
    show: function () {
        $('#modifyApiDiv').modal({'show': 'center', "backdrop": "static"});
    },
    hidden: function () {
        $('#modifyApiDiv').modal('toggle', 'center');
    },
    // 添加参数后更新回显
    updateParamAdd: function () {
        var updateTable = ($('#modifyApiDiv').attr('paramAddType_isTable') == '1') ? true : false;
        if (updateTable) {
            // 更新参数,刷新参数表格
            var newParam = JSON.parse($('#modifyApiDiv').attr('newParam'));
            paramsTableModal.initParamsTable(
                $('#modifyApiDiv'),
                '0',
                folderId,
                newParam,
                modifyApiModal.hidden,
                modifyApiModal.updateParamAdd
            );
        } else {
            // 刷新返回值相关
            if ($('#modifyApiDiv').attr('returnObj')) {
                var returnObj = JSON.parse($('#modifyApiDiv').attr('returnObj'));
                $("#returnTypeSelect_new input[value='" + returnObj.returnValueType + "']").prop('checked', 'checked');
                $('#returnValue_new').val(returnObj.returnValue); // 返回值回显
            }
        }
        modifyApiModal.show();
    }
}

// 设为公有接口
var setCommonModal = {
    show: function (apiId) {
        if (apiId == null || apiId == '') {
            return;
        }
        // 初始化接口组
        setCommonModal.initApiGroup();
        $('#setCommonApiAlert').modal({'show': 'center', "backdrop": "static"});
        // 绑定保存事件
        $('#setCommonApiAlert #setCommonSubmit').unbind().click(function () {
            var obj = {};
            obj["apiId"] = apiId;
            obj["apiGroupId"] = $('#setCommon_apiGroupSelector option:selected').attr('group-id');
            setCommonModal.setCommonSubmit(obj);
        });
    },
    initApiGroup: function () {
        $.ajax({
            url: webpath + '/api/pub/group/list',
            type: 'POST',
            dataType: "json",
            data: {},
            success: function (data) {
                if (data.data.length > 0) {
                    var data = data.data;
                    var htmlStr = '';
                    for (var i = 0; i < data.length; i++) {
                        htmlStr += '<option group-id=\'' + data[i].apiGroupId + '\'>' + data[i].apiGroupName + '</option>';
                    }
                    $('#setCommon_apiGroupSelector').empty().html(htmlStr);
                }
            }
        });
    },
    // 场景下接口设为公共接口
    setCommonSubmit: function (obj) {
        $.ajax({
            url: webpath + '/api/upgrade',
            type: 'POST',
            dataType: "json",
            data: obj,
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('修改成功！');
                    $("#table1").dataTable().fnDraw(false);//重新绘制表格
                    $('#setCommonApiAlert').modal('toggle', 'center');
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    },

}

//修改/查看API接口
function modifyRow(apiId, type) {
    // type 0修改 1查看
    // 清除之前的标识和状态
    $('#modifyApiDiv').removeAttr('usedFlag'); // 清除usedCheck状态标识
    $('#modApiName').removeAttr('disabled');
    $('#cron_msg').addClass('hide');
    $('#modifyApiDiv .modal-footer button').css('display', 'inline-block');
    $('#apiIndex_addReturnParam, #addApiParam').css('display', 'inline-block');
    $('#returnValueFormatSelect, #httpTypeSelect, #redisTypeSelect, ' +
        '#returnValueFormatSelect_input, #httpTypeSelect_input, #redisTypeSelect_input').css('display', 'none');

    if (type === 0) {
        $('#modifyApiDiv .modal-title').text('修改接口');
        $('#modifyApiDiv #closeViewApi').css('display', 'none');
        $('#modifyApiDiv input').removeAttr('disabled');
        $("#modifyApiDiv input[name='returnValueType']").attr('disabled', true);
        $('#returnValueFormatSelect, #httpTypeSelect, #redisTypeSelect').css('display', '');

        // 校验是否在用标识状态修改展示内容并添加tip提示
        $.ajax({
            url: webpath + '/api/checkUsed',
            type: 'POST',
            dataType: "json",
            data: {"apiId": apiId},
            success: function (data) {
                if (data.status === -1) {
                    $('#modifyApiDiv').attr('usedFlag', '1'); // 被引用
                    $('#modApiName').attr('disabled', true);
                    $('#cron_msg').removeClass('hide');
                } else {
                    $('#modifyApiDiv').attr('usedFlag', '0'); // 未使用
                    $('#modApiName').removeAttr('disabled');
                    $('#cron_msg').addClass('hide');
                }
            }
        });
    } else if (type === 1) {
        $('#modifyApiDiv .modal-title').text('查看接口');
        $('#modifyApiDiv .notViewBtns button').css('display', 'none');
        $('#apiIndex_addReturnParam, #addApiParam').css('display', 'none');
        $('#modifyApiDiv input').attr('disabled', true);
        $('#returnValueFormatSelect_input, #httpTypeSelect_input, #redisTypeSelect_input').css('display', '');
    }

    // 查询接口输入参数
    $.ajax({
        url: webpath + '/api/variables',
        type: 'GET',
        dataType: "json",
        data: {'apiId': apiId},
        success: function (data) {
            if (data.status === 0) {
                if (data.data.length > 0) {
                    $('#modifyApiDiv').attr('newParam', JSON.stringify(data.data));
                }
                //清空数据
                $(".modal-body input[type='text']").val('');
                $("dl dd").remove();
                var rowObj = JSON.parse($("#row_" + apiId).text()); // 接口整行数据
                console.dir(rowObj);
                //初始化数据
                $("#apiId").val(apiId);
                $("#modApiType").val(rowObj.apiType);
                $("#modApiName").val(rowObj.apiName);
                $("#apiDesc").val(rowObj.apiDesc);
                $("input[name='isLog'][value=" + rowObj.isLog + "]").prop("checked", true);

                $(".apiparamdiv").hide();
                $(".apitype-" + rowObj.apiType).show();

                var paramObj = JSON.parse(rowObj.apiContent);
                if (paramObj == null) {
                    paramObj = new Object();
                }

                if (rowObj.apiType == 'http') {
                    // 查看dom回显
                    $('#httpTypeSelect_input').val(paramObj.httpType.toUpperCase());
                    $('#returnValueFormatSelect_input').val(paramObj.returnValueFormat.toUpperCase());

                    //http请求方式下拉选数据
                    var selectData = [
                        {key: 'get', text: 'GET', selected: true},
                        {key: 'post', text: 'POST'}
                    ];
                    var httpTypeSelect = $('#httpTypeSelect').cm_select({field: "httpTypeSelect1", data: selectData});
                    if (paramObj == null) {
                        httpTypeSelect.setValue('get');
                    } else {
                        httpTypeSelect.setValue(paramObj.httpType);
                    }

                    $("#url").val(paramObj.url);

                    // 返回值回显
                    var returnValueType = paramObj.returnValueType;
                    if (returnValueType == null || returnValueType == '') {
                        returnValueType = '0';
                    }
                    $('#returnValue_new').val(paramObj.returnValue); // 返回值
                    $("#returnTypeSelect_new .radio-inline input[value='" + returnValueType + "']").prop('checked', 'checked'); // 返回值类型
                    // format
                    var returnValueFormatData = [
                        {key: 'json', text: 'JSON', selected: true},
                        {key: 'xml', text: 'XML'}
                    ];
                    var returnValueFormatSelect = $('#returnValueFormatSelect').cm_select({
                        field: "returnValueFormatSelect1",
                        data: returnValueFormatData
                    });
                    returnValueFormatSelect.setValue(paramObj.returnValueFormat);

                    // 返回值添加参数绑定事件
                    $('#apiIndex_addReturnParam').unbind('').on('click', function () {
                        // 标识为返回值添加参数
                        $('#modifyApiDiv').attr('paramAddType_isTable', '0');
                        var returnObj = $('#modifyApiDiv').attr('returnObj');
                        paramAddModal.initParamAddPage(
                            $('#modifyApiDiv'),
                            '3',
                            folderId,
                            modifyApiModal.hidden,
                            modifyApiModal.updateParamAdd,
                            returnObj
                        );
                    });

                    // 参数回显 数据缓存记录回显
                    var returnObj = {};
                    returnObj['isPublic'] = '0';
                    returnObj['variableId'] = paramObj.newReturnParamId;
                    returnObj['returnValue'] = paramObj.returnValue;
                    returnObj['returnValueType'] = paramObj.returnValueType;

                    $('#modifyApiDiv')
                        .attr('param', JSON.stringify(paramObj.param) || [])
                        // .attr('newParam', JSON.stringify(paramObj.newParam) || []) // 不从列表接口数据中取了, 重新请求接口从接口取
                        .attr('oldParamId', paramObj.newParamId || [])
                        .attr('newParamId', paramObj.newParamId || [])
                        .attr('oldReturnParamId', paramObj.newReturnParamId || '')
                        .attr('newReturnParamId', paramObj.newReturnParamId || '')
                        .attr('returnObj', JSON.stringify(returnObj));

                    // 回显接口参数列表
                    var newParam = [];
                    if ($('#modifyApiDiv').attr('newParam')) {
                        newParam = JSON.parse($('#modifyApiDiv').attr('newParam'));
                    }
                    paramsTableModal.initParamsTable(
                        $('#modifyApiDiv'),
                        '0',
                        folderId,
                        newParam,
                        modifyApiModal.hidden,
                        modifyApiModal.updateParamAdd
                    );

                } else if (rowObj.apiType == 'redis') {
                    // 查看dom回显
                    var redisType = paramObj.redisType;
                    var redisValue = '单节点';
                    if (redisType === 'codis'){
                        redisValue = 'codis集群';
                    }
                    $('#redisTypeSelect_input').val(redisValue);

                    var redisType = paramObj.redisType;
                    if (redisType == null || redisType == '') {
                        redisType = 'redis';
                    }
                    //单机/集群redis下拉选数据
                    var selectData = [
                        {key: 'redis', text: '单节点', selected: true},
                        {key: 'codis', text: 'codis集群'}
                    ];
                    $(".redisdiv").hide();
                    $(".type-" + redisType).show();
                    var redisTypeSelect = $('#redisTypeSelect').cm_select({
                        field: "redisTypeSelect1",
                        data: selectData,
                        onselect: function () {
                            var value = redisTypeSelect.getValue();
                            $(".redisdiv").hide();
                            $(".type-" + value).show();
                            $(".type-" + value + " input[type='text']").val('');
                        }
                    });
                    redisTypeSelect.setValue(paramObj.redisType);
                    for (var key in paramObj) {
                        $("#" + key).val(paramObj[key]);
                    }
                }

                $('#updateForm').validator('cleanUp');//清除表单中的全部验证消息
                $("#modifyApiDiv").modal({show: true});
            } else {
                failedMessager.show(data.msg);
            }
        }
    });
}

//保存修改API接口
function saveModifyApi() {
    if (!$('#updateForm').isValid()) {
        return;
    }
    var apiId = $("#apiId").val();
    var apiName = $("#modApiName").val();
    var apiDesc = $("#apiDesc").val();
    var apiType = $("#modApiType").val();
    var isLog = $("input[name='isLog']:checked").val();

    var apiContent = new Object();
    if (apiType == 'http') {
        apiContent['httpType'] = $("#httpTypeSelect1").val();
        apiContent['url'] = $("#url").val();
        // 旧
        //返回值
        // var returnValueType = $("input[name='returnValueType']:checked").val();
        // apiContent['returnValueType'] = returnValueType;
        // apiContent['returnValue'] = $("#returnValueSelect1").val();
        // apiContent['returnValueFormat'] = $("#returnValueFormatSelect1").val();
        // if (returnValueType == '0') {
        //     apiContent['returnValue'] = '';
        // }
        // //参数
        // var httpParam = new Array();
        // $("dl .textL").each(function () {
        //     var param = $(this).val();
        //     if (param && param != '请输入参数') {
        //         var isExist = false;
        //         for (var i = 0; i < httpParam.length; i++) {
        //             if (param == httpParam[i]) {
        //                 isExist = true;
        //                 break;
        //             }
        //         }
        //         if (!isExist) {
        //             httpParam.push(param);
        //         }
        //     }
        // });
        // apiContent.param = httpParam;

        // 新
        // 返回值
        apiContent['returnValue'] = $('#returnValue_new').val();
        var returnValueType = $('#returnTypeSelect_new input:checked').attr('value');
        apiContent['returnValueType'] = returnValueType;
        if (returnValueType == '0') {
            apiContent['returnValue'] = '';
        }
        apiContent['returnValueFormat'] = $("#returnValueFormatSelect1").val();

        apiContent['param'] = JSON.parse($('#modifyApiDiv').attr('param') || '[]'); //arr
        apiContent['newParam'] = JSON.parse($('#modifyApiDiv').attr('newParam') || '[]'); //arr
        apiContent['oldParamId'] = $('#modifyApiDiv').attr('oldParamId'); //arr
        apiContent['newParamId'] = $('#modifyApiDiv').attr('newParamId'); //arr
        apiContent['oldReturnParamId'] = $('#modifyApiDiv').attr('oldReturnParamId');
        apiContent['newReturnParamId'] = $('#modifyApiDiv').attr('newReturnParamId');

    } else if (apiType == 'redis') {
        var redisType = $("#redisTypeSelect1").val();
        apiContent.redisType = redisType;
        $(".type-" + redisType + " input[type='text']").each(function (i, ipt) {
            var key = $(this).attr('id');
            var value = $(this).val();
            apiContent[key] = value;
        });
        $(".type-" + redisType + " input[type='password']").each(function (i, ipt) {
            var key = $(this).attr('id');
            var value = $(this).val();
            apiContent[key] = value;
        });
    }

    // console.dir(apiContent);
    var apiContentStr = JSON.stringify(apiContent);

    // 接口修改校验
    var usedFlag = $('#modifyApiDiv').attr('usedFlag');
    if (usedFlag == '0') {
        $.ajax({
            url: webpath + "/api/updateApi",
            data: {
                "apiId": apiId,
                "folderId": $("#folderId").val(),
                "apiName": apiName,
                "apiDesc": apiDesc,
                "isLog": isLog,
                "apiContent": apiContentStr
            },
            type: "post",
            dataType: "json",
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('修改成功');
                    $('#modifyApiDiv').modal('hide');
                    $("#table1").dataTable().fnDraw(false);//重新绘制表格
                } else {
                    failedMessager.show('修改失败--' + data.msg);
                }
            }
        });
    } else if (usedFlag == '1') {
        $('#modifyApiDiv').modal('toggle', 'center');
        // 验证未通过则警告, 若再次确认则修改
        confirmAlert.show(
            '接口被引用中, 是否仍要修改？',
            function () {
                $.ajax({
                    url: webpath + "/api/updateApi",
                    data: {
                        "apiId": apiId,
                        "folderId": $("#folderId").val(),
                        "apiName": apiName,
                        "apiDesc": apiDesc,
                        "isLog": isLog,
                        "apiContent": apiContentStr
                    },
                    type: "post",
                    dataType: "json",
                    success: function (data) {
                        if (data.status == 0) {
                            successMessager.show('修改成功');
                            $('#modifyApiDiv').modal('hide');
                            $("#table1").dataTable().fnDraw(false);//重新绘制表格
                        } else {
                            failedMessager.show('修改失败--' + data.msg);
                        }
                    }
                });
            });
    }

    // $.ajax({ // 接口修改校验
    //     url: webpath + '/api/checkUsed',
    //     type: 'POST',
    //     dataType: "json",
    //     data: {'apiId': apiId},
    //     success: function (data) {
    //         if (data.status === 0) {
    //             $.ajax({
    //                 url: webpath + "/api/updateApi",
    //                 data: {
    //                     "apiId": apiId,
    //                     "folderId": $("#folderId").val(),
    //                     "apiName": apiName,
    //                     "apiDesc": apiDesc,
    //                     "isLog": isLog,
    //                     "apiContent": apiContentStr
    //                 },
    //                 type: "post",
    //                 dataType: "json",
    //                 success: function (data) {
    //                     if (data.status === 0) {
    //                         successMessager.show('修改成功');
    //                         $('#modifyApiDiv').modal('hide');
    //                         $("#table1").dataTable().fnDraw(false);//重新绘制表格
    //                     } else {
    //                         failedMessager.show('修改失败--' + data.msg);
    //                     }
    //                 }
    //             });
    //         } else {
    //             $('#modifyApiDiv').modal('toggle', 'center');
    //             // 验证未通过则警告, 若再次确认则修改
    //             confirmAlert.show(
    //                 data.msg + ', 是否仍要修改？',
    //                 function () {
    //                     $.ajax({
    //                         url: webpath + "/api/updateApi",
    //                         data: {
    //                             "apiId": apiId,
    //                             "folderId": $("#folderId").val(),
    //                             "apiName": apiName,
    //                             "apiDesc": apiDesc,
    //                             "isLog": isLog,
    //                             "apiContent": apiContentStr
    //                         },
    //                         type: "post",
    //                         dataType: "json",
    //                         success: function (data) {
    //                             if (data.status == 0) {
    //                                 successMessager.show('修改成功');
    //                                 $('#modifyApiDiv').modal('hide');
    //                                 $("#table1").dataTable().fnDraw(false);//重新绘制表格
    //                             } else {
    //                                 failedMessager.show('修改失败--' + data.msg);
    //                             }
    //                         }
    //                     });
    //                 });
    //         }
    //     }
    // });
}

//删除API接口
function delRow(apiId) {
    $("#apiId").val(apiId);
    $("#delApiDiv").modal({});
}

function deleteApiBind() {
    $("#delApiBtn").click(function () {
        $.ajax({
            url: webpath + '/api/checkUsed',
            type: 'POST',
            dataType: "json",
            data: {'apiId': $("#apiId").val()},
            success: function (data) {
                if (data.status == 0) { // 验证通过可删除
                    $.ajax({
                        url: webpath + "/api/deleteApi",
                        data: {
                            "apiId": $("#apiId").val(),
                            "folderId": $("#folderId").val()
                        },
                        type: "post",
                        dataType: "json",
                        success: function (data) {
                            if (data.status == 0) {
                                new $.zui.Messager('删除成功', {
                                    placement: 'center', // 定义显示位置
                                    time: 1000,//表示时间延迟，单位毫秒
                                    type: 'success' // 定义颜色主题
                                }).show();
                                $("#delApiDiv").modal('hide');
                                $("#table1").dataTable().fnDraw(false);//重新绘制表格
                            } else {
                                new $.zui.Messager('删除失败--' + data.msg, {
                                    time: 1000,//表示时间延迟，单位毫秒
                                    placement: 'center', // 定义显示位置
                                    type: 'warning' // 定义颜色主题
                                }).show();
                                $("#delApiDiv").modal('hide');
                            }
                        }
                    });
                } else {
                    failedMessager.show(data.msg);
                }
            },
        });
    });
}

//新建接口
function createApi() {
    var folderId = $.trim($("#folderId").val());
    var url = webpath + "/api/createApiIndex?folderId=" + folderId + "&childOpen=o";
    creCommon.loadHtml(url);
}

//返回
function returnPage(folderId) {
    var url = webpath + "/ruleFolder/rulePackageMgr?folderId=" + folderId + '&childOpen=o';
    creCommon.loadHtml(url);
}

//初始化http类型的返回值redio切换事件
function initReturnRedio() {
    $("#returnValueDiv").hide();
    $('input:radio[name="returnValueType"]').change(function () {
        var selectData = [
            {key: 'get', text: 'GET', selected: true},
            {key: 'post', text: 'POST'}
        ];
        var value = $(this).val();
        if (value == '0') {
            $("#returnValueDiv").hide();
            $("#returnValueFormatDiv").hide();
        } else if (value == '1') {
            $("#returnValueDiv").show();
            $("#returnValueFormatDiv").hide();
            if (baseVariable != null && baseVariable.length > 0) {
                baseVariable[0].selected = true;
            }
            $('#returnValueSelect').cm_select({field: "returnValueSelect1", data: baseVariable});
        } else if (value == '2') {
            $("#returnValueDiv").show();
            $("#returnValueFormatDiv").show();
            if (entityType != null && entityType.length > 0) {
                entityType[0].selected = true;
            }
            $('#returnValueSelect').cm_select({field: "returnValueSelect1", data: entityType});
            var returnValueFormatData = [
                {key: 'json', text: 'JSON', selected: true},
                {key: 'xml', text: 'XML'}
            ];
            $('#returnValueFormatSelect').cm_select({field: "returnValueFormatSelect1", data: returnValueFormatData});
        }
    });
}

$(function () {
    initTable();
    deleteApiBind();
    //初始化http类型的返回值redio切换事件
    // initReturnRedio();
    //忽略校验隐藏的元素
    $('#updateForm').validator({
        ignore: ':hidden'
    });
});

/* 追加删除    */
$(function () {
    $(".addLine").click(function () {
        $(".setBut").parent().children("dl").append("<dd><input type='text' class='textL' placeholder='请输入参数'  value='' /><buttom class='butD removeLine'>删除</buttom></dd>");
    });
    $(".commonLine").delegate(".removeLine", "click", function () {
        $(this).parent().remove();
    });
});