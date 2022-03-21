/**
 * 场景下公共接口列表
 * data:2019/08/04
 * author:bambi
 */

function initCommonApiPage() {
    folderApiTable.initApiGroup();

    // 搜索
    $('#folder_apiSearch').click(function () {
        var inputs = $('#folder_commonApiContainer .folderApiSearch .input-group .form-control');
        var obj = {};
        for (var i = 0; i < inputs.length; i++) {
            if ($.trim($(inputs[i]).val()) == '') {
                continue;
            }
            obj[$(inputs[i]).attr('data-col')] = $.trim($(inputs[i]).val());
        }
        if (obj.length != 0) {
            initCommonApiTable(obj);
            // 清空搜索框内容
            for (var i = 0; i < inputs.length; i++) {
                $(inputs[i]).val('');
            }
        }
    });
}

var folderApiTable = {
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
                    for (var i = 0; i < data.length; i++) {
                        htmlStr_search += '<li group-id=\'' + data[i].apiGroupId + '\'><a>' + data[i].apiGroupName + '</a></li>';
                    }
                    $('#folder_apiGroupSelector').empty().html(htmlStr_search); // 资源表搜索栏
                }
            },
            error: function (data) {
                failedMessager.show(data.msg);
            },
            complete: function () {
                // 绑定事件
                $('#folder_apiGroupSelector>li').unbind('click').on('click', function () {
                    $(this).parent().siblings('.form-control').val($(this).first().text());
                });
            }
        });
    },
    // 退出弹框按钮重写click事件恢复一些默认值
    detailParamsClean: function (that) {
        $('#addApiParam, #apiIndex_addReturnParam, #saveModifyApiButton').css('display', 'inline-block');
        that.unbind('click');
    },
    // 查看接口详情
    detailApi: function (apiId) {
        // 隐藏: 返回值添加参数 & 参数列表添加操作 & 弹框保存按钮隐藏
        $('#addApiParam, #apiIndex_addReturnParam, #saveModifyApiButton').css('display', 'none');

        //清空数据
        $(".modal-body input[type='text']").val('');
        $("dl dd").remove();
        var rowObj = JSON.parse($("#folderRow_" + apiId).text());
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

            // 参数回显:数据缓存记录回显
            var returnObj = {};
            returnObj['isPublic'] = '0';
            returnObj['variableId'] = paramObj.newReturnParamId;
            returnObj['returnValue'] = paramObj.returnValue;
            returnObj['returnValueType'] = paramObj.returnValueType;

            $('#modifyApiDiv')
                .attr('param', paramObj.param)
                .attr('newParam', JSON.stringify(paramObj.newParam) || '[]')
                .attr('oldParamId', paramObj.newParamId || '[]')
                .attr('newParamId', paramObj.newParamId || '[]')
                .attr('oldReturnParamId', paramObj.newReturnParamId || '')
                .attr('newReturnParamId', paramObj.newReturnParamId || '')
                .attr('returnObj', JSON.stringify(returnObj));

            // 回显接口参数列表
            var newParam = JSON.parse($('#modifyApiDiv').attr('newParam'));
            paramsTableModal.initParamsTable(
                $('#modifyApiDiv'), '0', folderId, newParam,
                function () {
                }, function () {
                });

        } else if (rowObj.apiType == 'redis') {
            for (var key in paramObj) {
                $("#" + key).val(paramObj[key]);
            }
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
                field: "redisTypeSelect1", data: selectData, onselect: function () {
                    var value = redisTypeSelect.getValue();
                    $(".redisdiv").hide();
                    $(".type-" + value).show();
                    $(".type-" + value + " input[type='text']").val('');
                }
            });
        }

        $('#updateForm').validator('cleanUp');//清除表单中的全部验证消息
        $("#modifyApiDiv .modal-title").text('查看接口');
        $('#modifyApiDiv .notViewBtns button').css('display', 'none');
        $('#closeViewApi').css('display', 'inline-block');
        $("#modifyApiDiv").modal({show: true});
        // 弹框关闭按钮事件重置定制化的修改
        $('#cancelModifyApi').on('click', function () {
            folderApiTable.detailParamsClean($(this));
        });
    },
}

/* 初始化公共接口table obj:搜索条件 */
function initCommonApiTable(obj) {
    obj == null ? '' : obj;
    var pageLength = 10; //每页显示条数
    $.extend($.fn.dataTable.defaults, {
        info: true,
        "serverSide": true,
        "pageLength": pageLength
    });
    $('#folder_apiTable').width('100%').dataTable({
        "searching": false, // 是否开启搜索功能
        "ordering": false,
        "destroy": true,
        "bLengthChange": false,
        "pagingType": "full_numbers",
        "paging": true,
        "info": true,
        "serverSide": true,
        "pageLength": pageLength,
        "columns": [
            {"title": "接口组", "data": "apiGroupName"},
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
            {"title": "创建人", "data": "createPersion"},
            {
                "title": "操作", "data": null, "render": function (data, type, row) {
                    var htmlStr = '<div>';
                    htmlStr += '<div id="folderRow_' + row.apiId + '" style="display:none;">' + JSON.stringify(row) + '</div>';
                    htmlStr += '<span class="cm-tblB" onclick="folderApiTable.detailApi(\'' + row.apiId + '\');" >查看</span>';
                    htmlStr += '</div>';
                    $("#folderRow_" + row.apiId).data("rowData", row);
                    return htmlStr;
                }
            }
        ],
        ajax: {
            url: webpath + '/api/pub/list',
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
    initCommonApiTable(); // 初始化表格
    initCommonApiPage();
});