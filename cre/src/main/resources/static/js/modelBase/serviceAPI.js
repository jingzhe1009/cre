/**
 * 模型服务API查看
 */
var serviceAPIModal = {
    /**
     * 数据
     */
    apiBaseData: [{
        'url': 'http://&lt;ip&gt;:&lt;port&gt;/cre/ws/rule/executeRule',
        'describe': '调用已发布（启用的）模型',
        'type': 'POST'
    }],  //接口说明
    modelBaseDataArr: [
        {
            'varName': 'folderId',
            'varType': 'String',
            'isRequired': '是',
            'describe': '基础参数，模型所在场景唯一标识',
            'example': ''
        },
        {
            'varName': 'ruleId',
            'varType': 'String',
            'isRequired': '是',
            'describe': '基础参数，已发布模型唯一标识',
            'example': ''
        },
        {
            'varName': 'paramStr',
            'varType': 'String',
            'isRequired': '是',
            'describe': '模型参数，模型所需的输入参数，json格式的字符串',
            'example': ''
        },
        {
            'varName': 'consumerId',
            'varType': 'String',
            'isRequired': '否',
            'describe': '业务参数，渠道唯一标识',
            'example': ''
        },
        {
            'varName': 'serverId',
            'varType': 'String',
            'isRequired': '否',
            'describe': '业务参数，上游服务唯一标识',
            'example': ''
        },
        {
            'varName': 'consumerSeqNo',
            'varType': 'String',
            'isRequired': '否',
            'describe': '业务参数，流水号',
            'example': ''
        }
    ], //基础参数
    resultsDataArr: [
        {
            'varName': 'msg',
            'varType': 'String',
            'describe': '返回信息说明，包括成功和异常信息。执行成功返回“执行成功”，执行失败返回异常信息,异常信息详见异常信息说明',
            'example': '"msg":"执行成功"'
        },
        {
            'varName': 'status',
            'varType': 'int',
            'describe': '执行状态，0：成功；-1：失败',
            'example': ''
        },
        {
            'varName': 'data',
            'varType': 'String',
            'describe': '返回数据，执行成功返回模型的输出参数，json格式字符串；执行失败返回null',
            'example': ''
        }
    ], //返回结果参数
    modelParamsDataArr: [], //业务参数
    /**
     * 表头
     */
    apiBaseColumns: [
        {
            "title": "URL",
            "width": "45%",
            "data": "url"
        },
        {
            "title": "描述",
            "width": "45%",
            "data": "describe"
        },
        {
            "title": "请求方式",
            "width": "10%",
            "data": "type"
        }
    ], //API基本信息
    modelBaseColumns: [
        {
            "title": "参数",
            "width": "15%",
            "data": "varName"
        },
        {
            "title": "类型",
            "width": "15%",
            "data": "varType"
        },
        {
            "title": "是否必填",
            "width": "10%",
            "data": "isRequired"
        },
        {
            "title": "描述",
            "width": "30%",
            "data": "describe"
        },
        {
            "title": "示例/值",
            "width": "30%",
            "data": "example"
        }
    ], //基础参数
    resultsColumns: [
        {
            "title": "返回字段",
            "width": "10%",
            "data": "varName"
        },
        {
            "title": "类型",
            "width": "10%",
            "data": "varType"
        },
        {
            "title": "描述",
            "width": "50%",
            "data": "describe"
        },
        {
            "title": "示例",
            "width": "30%",
            "data": "example"
        }
    ], //返回结果参数
    modelParamsColumns: [
        {
            "title": "",
            "width": "20%",
            "data": "type"
        },
        {
            "title": "参数种类",
            "width": "20%",
            "data": "kindId",
            "render": function (data) {
                switch (data) {
                    case 'K1':
                        return '输入变量';
                    case 'K2':
                        return '输出变量';
                    case 'K3':
                        return '中间变量';
                    case 'K4':
                        return '系统常量';
                    default:
                        return '--';
                }
            }
        },
        {
            "title": "参数别名",
            "width": "20%",
            "data": "variableAlias"
        },
        {
            "title": "参数编码",
            "width": "20%",
            "data": "variableCode"
        },
        {
            "title": "类型",
            "width": "20%",
            "data": "typeId",
            "render": function (data) {
                switch (data) {
                    case '1':
                        return '字符串';
                    case '2':
                        return '整型';
                    case '3':
                        return '对象';
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
        }
    ], //业务参数
    paramsStrMaxLength: 20,
    folderId: '',
    ruleId: '',
    callBackFn: null,
    // 清除页面缓存数据
    clearCache: function () {
        serviceAPIModal.modelParamsDataArr = [];
        (serviceAPIModal.modelBaseDataArr)[0]['example'] = ''; //folderId
        (serviceAPIModal.modelBaseDataArr)[1]['example'] = ''; //ruleId
        (serviceAPIModal.modelBaseDataArr)[2]['example'] = ''; //in_paramsStr
        (serviceAPIModal.resultsDataArr)[2]['example'] = ''; //out_paramsStr
        serviceAPIModal.folderId = '';
        serviceAPIModal.ruleId = '';
        serviceAPIModal.callBackFn = null;
    },
    // 展示弹框
    show: function () {
        $('#serviceApiAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 页面初始化
    initPage: function (folderId, ruleId, callBackFn) {
        serviceAPIModal.clearCache(); //清除缓存
        if (!folderId || !ruleId) {
            console.log('serviceApi ruleId/folderId undefined!');
            return;
        }
        serviceAPIModal.folderId = folderId;
        serviceAPIModal.ruleId = ruleId;
        if (callBackFn) {
            serviceAPIModal.callBackFn = callBackFn;
        }
        serviceAPIModal.getPageData(ruleId);
    },
    // 确定退出弹框
    closePage: function () {
        $('#serviceApiAlertModal').modal('hide');
        if (serviceAPIModal.callBackFn) {
            serviceAPIModal.callBackFn();
        }
        serviceAPIModal.clearCache();
    },
    // 获取模型相关API信息 分析，填充表格内数据
    getPageData: function (ruleId) {
        $.ajax({
            url: webpath + '/rule/api/info',
            type: 'GET',
            data: {"ruleId": ruleId},
            beforeSend: function () {
                loading.show();
            },
            success: function (data) {
                if (data.status === 0) {
                    serviceAPIModal.analyzeData(data.data); //分析数据
                } else {
                    failedMessager.show(data.msg);
                }
            },
            complete: function () {
                loading.hide();
            }
        });
    },
    // 根据获取数据分析获取各个表格需要的数据
    analyzeData: function (data) {
        var dataArr = [];
        var in_paramsStrExample = '';
        var out_paramsStrExample = '';

        if (data.variableList) { //输入参数
            var variableList = data.variableList;
            for (var j = 0; j < variableList.length; j++) {
                if (j === 0) {
                    variableList[j]['numFlag'] = variableList.length;
                } else {
                    variableList[j]['numFlag'] = 0;
                }
                variableList[j]['type'] = '输入';
                dataArr.push(variableList[j]);
            }
            in_paramsStrExample = serviceAPIModal.getParamsStrExample(variableList);
        }

        if (data.outVariableList) { //输出参数
            var outVariableList = data.outVariableList;
            for (var i = 0; i < outVariableList.length; i++) {
                if (i === 0) {
                    outVariableList[i]['numFlag'] = outVariableList.length;
                } else {
                    outVariableList[i]['numFlag'] = 0;
                }
                outVariableList[i]['type'] = '输出';
                dataArr.push(outVariableList[i]);
            }
            out_paramsStrExample = serviceAPIModal.getParamsStrExample(outVariableList);
        }

        serviceAPIModal.modelParamsDataArr = dataArr;
        (serviceAPIModal.modelBaseDataArr)[0]['example'] = serviceAPIModal.folderId; // folderId
        (serviceAPIModal.modelBaseDataArr)[1]['example'] = serviceAPIModal.ruleId; // ruleId
        (serviceAPIModal.modelBaseDataArr)[2]['example'] = in_paramsStrExample; //填充请求参数表内容
        (serviceAPIModal.resultsDataArr)[2]['example'] = out_paramsStrExample; //填充返回参数表内容
        serviceAPIModal.initTable($('#serviceApi_apiBaseTable'), serviceAPIModal.apiBaseColumns, serviceAPIModal.apiBaseData);
        serviceAPIModal.initTable($('#serviceApi_modelBaseTable'), serviceAPIModal.modelBaseColumns, serviceAPIModal.modelBaseDataArr);
        serviceAPIModal.initTable($('#serviceApi_resultsTable'), serviceAPIModal.resultsColumns, serviceAPIModal.resultsDataArr);
        serviceAPIModal.initParamsTable($('#serviceApi_modelParamsTable'), serviceAPIModal.modelParamsColumns, serviceAPIModal.modelParamsDataArr);
        serviceAPIModal.show();
    },
    // 拼接模型参数示例
    getParamsStrExample: function (varList) {
        if (!varList || varList.length <= 0) {
            return '';
        }
        var dataNum = varList.length; //数据真实长度
        var maxNum = serviceAPIModal.paramsStrMaxLength; //可以展示的最大数
        var forNum = (dataNum < maxNum) ? dataNum : maxNum; //确定循环次数

        var varExampleObjStr = '<span class="serviceApi_paramsExample">{</span>';
        for (var i = 0, len = forNum; i < len; i++) {
            var varObj = varList[i];
            varExampleObjStr += '<span class="serviceApi_paramsExample">"' + varObj.variableCode + '":' + serviceAPIModal.getVarExampleValue(varObj.typeId);
            if (i === (len - 1)) {
                if (maxNum < dataNum) { //不是最后一个但是已经达到显示个数最大值，则省略
                    varExampleObjStr += ',</span><span class="serviceApi_paramsExample">...</span>';
                } else {
                    varExampleObjStr += "</span>";
                }
            } else {
                varExampleObjStr += ",</span>";
            }
        }
        varExampleObjStr += '<span class="serviceApi_paramsExample">}</span>';
        return varExampleObjStr;
    },
    // 根据变量类型给定对应的示例值
    getVarExampleValue: function (typeId) {
        switch (typeId) {
            case '1':
                return '""'; //字符串
            case '2':
                return 1; //整型
            case '4':
                return 1.0; //浮点型
            default:
                return '--';
        }
    }
    ,
    // 加载表格
    initTable: function ($container, columns, dataArr) {
        $.fn.dataTable.ext.errMode = 'none';
        $container
            .on('error.dt', function (e, settings, techNote, message) {
                console.log('An error has been reported by DataTables: ', message);
            })
            .width('100%').dataTable({
            "searching": false, // 是否开启搜索功能
            "ordering": false,
            "destroy": true,
            "bLengthChange": false,
            "paging": false,
            "info": false,
            "autoWidth": false,
            "columns": columns,
            "data": dataArr
        });
    },
    // 加载业务参数表格（涉及单元格合并）
    initParamsTable: function ($container, columns, dataArr) {
        $.fn.dataTable.ext.errMode = 'none';
        $container
            .on('error.dt', function (e, settings, techNote, message) {
                console.log('An error has been reported by DataTables: ', message);
            })
            .width('100%').dataTable({
            "searching": false, // 是否开启搜索功能
            "ordering": false,
            "destroy": true,
            "bLengthChange": false,
            "paging": false,
            "info": false,
            "autoWidth": false,
            "columns": columns,
            "data": dataArr,
            "columnDefs": [{
                "targets": [0], // 输入/输出
                "createdCell": function (td, cellData, rowData, row, col) {
                    var rowspan = rowData.numFlag;
                    if (rowspan > 0) {
                        $(td).attr('rowspan', rowspan);
                    } else {
                        $(td).remove();
                    }
                }
            }]
        });
    }
};