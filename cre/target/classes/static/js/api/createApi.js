//初始化 接口类型下拉框数据
function initApiType() {
    $.ajax({
        url: webpath + "/api/apiType",
        data: {},
        type: "post",
        dataType: "json",
        success: function (data) {
            $(".apiparamdiv").hide();
            $(".apitype-http").show();
            var apiTypeSelect = $('#apiTypeSelect').cm_select({
                field: "apiTypeSelect1", data: data, onselect: function () {
                    var value = apiTypeSelect.getValue();
                    $(".apiparamdiv").hide();
                    $(".apitype-" + value).show();
                }
            });
        }
    });

}

var createApiModal = {
    // 选择参数后更新页面
    updateParamAdd: function () {
        var updateTable = ($('#insertForm').attr('paramAddType_isTable') == '1') ? true : false;
        if (updateTable) {
            // 更新参数,刷新参数表格
            var newParam = JSON.parse($('#insertForm').attr('newParam'));
            paramsTableModal.initParamsTable($('#insertForm'), '0', folderId, newParam, createApiModal.initParamAdd, createApiModal.updateParamAdd);
        } else {
            // 刷新返回值相关
            if ($('#insertForm').attr('returnObj')) {
                var returnObj = JSON.parse($('#insertForm').attr('returnObj'));
                $("#createApi_returnTypeSelect_new input[value='" + returnObj.returnValueType + "']").prop('checked', 'checked');
                $('#createApi_returnValue_new').val(returnObj.returnValue); // 返回值回显
            }
        }
    }
}

//初始化下拉选数据
function initPage() {
    //http请求方式下拉选数据
    var selectData = [
        {key: 'get', text: 'GET', selected: true},
        {key: 'post', text: 'POST'}
    ];
    $('#httpTypeSelect').cm_select({field: "httpTypeSelect1", data: selectData});

    //单机/集群redis下拉选数据
    var selectData = [
        {key: 'redis', text: '单节点', selected: true},
        {key: 'codis', text: 'codis集群'}
    ];
    $(".redisdiv").hide();
    $(".type-redis").show();
    var redisTypeSelect = $('#redisTypeSelect').cm_select({
        field: "redisTypeSelect1", data: selectData, onselect: function () {
            var value = redisTypeSelect.getValue();
            $(".redisdiv").hide();
            $(".type-" + value).show();
        }
    });

    // 初始化接口数据缓存
    $('#insertForm')
        .attr('param', '[]')
        .attr('newParam', '[]')
        .attr('oldParamId', [])
        .attr('newParamId', [])
        .attr('oldReturnParamId', '')
        .attr('newReturnParamId', '')
        .attr('returnObj');
        // .attr('returnObj', {});
    // 初始化参数表格, 新建时参数列表为空
    paramsTableModal.initParamsTable($('#insertForm'), '0', folderId, [], createApiModal.initParamAdd, createApiModal.updateParamAdd);

    // 返回值添加参数绑定事件
    $('#createApi_addReturnParam').unbind('').on('click', function () {
        // 标识为返回值添加参数
        $('#insertForm').attr('paramAddType_isTable', '0');
        var returnObj = $('#insertForm').attr('returnObj');
        paramAddModal.initParamAddPage($('#insertForm'), '3', folderId, createApiModal.initParamAdd, createApiModal.updateParamAdd, returnObj);
    });
}

//保存接口按钮
function savaBtn() {
    if (!$('#insertForm').isValid()) {
        return;
    }
    var apiName = $("#apiName").val();
    var apiDesc = $("#apiDesc").val();
    var isLog = $("input[name='isLog']:checked").val();
    var apiType = $("#apiTypeSelect1").val();

    var apiContent = new Object();
    if (apiType == 'http') {
        apiContent['httpType'] = $("#httpTypeSelect1").val();
        apiContent['url'] = $("#url").val();
        var returnValueType = $("input[name='returnValueType']:checked").val();
        apiContent['returnValueType'] = returnValueType;
        // apiContent['returnValue'] = $("#returnValueSelect1").val();
        apiContent['returnValue'] = $("#createApi_returnValue_new").val();
        apiContent['returnValueFormat'] = $("#returnValueFormatSelect1").val();
        if (returnValueType == '0') {
            apiContent['returnValue'] = '';
        }
        // //参数: 旧
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

        // 参数: 新
        apiContent['param'] = JSON.parse($('#insertForm').attr('param')); //arr
        apiContent['newParam'] = JSON.parse($('#insertForm').attr('newParam')); //arr
        apiContent['oldParamId'] = $('#insertForm').attr('oldParamId'); //arr
        apiContent['newParamId'] = $('#insertForm').attr('newParamId'); //arr
        apiContent['oldReturnParamId'] = $('#insertForm').attr('oldReturnParamId');
        apiContent['newReturnParamId'] = $('#insertForm').attr('newReturnParamId');

    } else if (apiType == 'redis') {
        var redisType = $("#redisTypeSelect1").val();
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
    // console.log(JSON.stringify(apiContent));
    var apiContentStr = JSON.stringify(apiContent);
    $.ajax({
        url: webpath + "/api/insertApi",
        data: {
            "folderId": $("#folderId").val(),
            "apiName": apiName,
            "apiDesc": apiDesc,
            "isLog": isLog,
            "apiType": apiType,
            "apiContent": apiContentStr
        },
        type: "post",
        dataType: "json",
        success: function (data) {
            if (data.status === 0) {
                successMessager.show('保存成功！');
                cancleBtn();
            } else {
                failedMessager.show('保存失败--' + data.msg);
            }
        }
    });
}

//取消按钮
function cancleBtn() {
    var url = webpath + "/api/index?folderId=" + $("#folderId").val();
    location.href = url;
}

function initReturnRedio() {
    // $("#returnValueDiv").hide();
    // $("#returnValueFormatDiv").hide();
    // $('input:radio[name="returnValueType"]').change(function () {
    //     var selectData = [
    //         {key: 'get', text: 'GET', selected: true},
    //         {key: 'post', text: 'POST'}
    //     ];
    // var value = $(this).val();
    // if (value == '0') {
    //     $("#returnValueDiv").hide();
    //     $("#returnValueFormatDiv").hide();
    // } else if (value == '1') {
    //     $("#returnValueDiv").show();
    //     $("#returnValueFormatDiv").hide();
    //     if (baseVariable != null && baseVariable.length > 0) {
    //         baseVariable[0].selected = true;
    //     }
    //     $('#returnValueSelect').cm_select({field: "returnValueSelect1", data: baseVariable});
    // } else if (value == '2') {
    //     $("#returnValueDiv").show();
    //     $("#returnValueFormatDiv").show();
    //     if (entityType != null && entityType.length > 0) {
    //         entityType[0].selected = true;
    //     }
    $('#returnValueSelect').cm_select({field: "returnValueSelect1", data: entityType});
    var returnValueFormatData = [
        {key: 'json', text: 'JSON', selected: true},
        {key: 'xml', text: 'XML'}
    ];
    $('#returnValueFormatSelect').cm_select({field: "returnValueFormatSelect1", data: returnValueFormatData});
    // }
    // });
}

$(function () {
    //初始化 接口类型下拉框数据
    initApiType();
    initPage();
    initReturnRedio();
    //忽略校验隐藏的元素
    $('#insertForm').validator({
        ignore: ':hidden'
    });
});


/* 追加删除    */
$(function () {
    $(".addLine").click(function () {
        $(".setBut").parent().children("dl").append("<dd><input type='text' class='textL' placeholder='请输入参数' value=''  data-rule='参数名:required,code,length[1~32],filter;'/><buttom class='butD removeLine'>删除</buttom></dd>");
    });
    $(".commonLine").delegate(".removeLine", "click", function () {
        $(this).parent().remove();
    });
});


