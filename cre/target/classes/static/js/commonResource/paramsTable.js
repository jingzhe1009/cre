/**
 * 接口参数展示列表组件
 * data:2019/08/03
 * author:bambi
 */

var paramsTableModal = {
    /**
     * 初始化接口下参数表格
     *      targetContainer 保存参数数据的元素
     *      pageType '0'场景下
     *               '1'公共规则池参数(只能从公共中选参数)
     *               '2'公共规则池返回值(限定只能选一个参数)
     *               '3'场景下返回值参数(限定只能选一个参数)
     *      folderId 场景id
     *      pageType 0场景下 1公共规则池参数(只能从公共中选参数) 2返回值(限定只能选一个参数)
     *      paramsArr 参数数组, 本地数据
     *      addParamInitFn 点击列表右上角添加时初始化函数
     *      finishCallBackFn 从添加函数弹框退出: 保存/取消后的回调函数
     */
    initParamsTable: function (targetContainer, pageType, folderId, paramsArr, addParamInitFn, finishCallBackFn) {
        // 初始化参数表格
        initApiParamsTable(pageType, paramsArr);
        // 接口参数: 添加参数按钮事件
        $('#apiParamContainer #addApiParam').unbind().click(function () {
            // 标识添加参数操作为接口参数 '1'参数列表 '0'返回值操作
            targetContainer.attr('paramAddType_isTable', '1');
            if (pageType != '') {
                paramAddModal.initParamAddPage(targetContainer, pageType, folderId, addParamInitFn, finishCallBackFn, paramsArr);
            }
        });
    },
}

/**
 * 初始化接口参数table
 *      pageType: '0'场景下 ; '1'公共规则池参数(只能从公共中选参数)
 *                '2'公共规则池返回值(限定只能选一个参数) ; '3'场景下返回值参数(限定只能选一个参数)
 *      paramsArr 参数数组, 本地数据
 */
function initApiParamsTable(pageType, paramsArr) {
    var pageLength = 5; //每页显示条数
    var dataArr = (paramsArr.length > 0) ? paramsArr : [];
    var titles = initApiParamsTitles(pageType);
    $('#apiParamsTable').dataTable({
        searching: false, // 是否开启搜索功能
        ordering: false,
        destroy: true,
        bLengthChange: false,
        info: true,
        paging: true,
        pageLength: pageLength,
        pagingType: "full_numbers",
        serverSide: false,
        autoWidth: false,
        data: dataArr,
        columns: titles,
        fnDrawCallback: function (oSettings, json) {
            $("tr:even").css("background-color", "#fbfbfd");
            $("table:eq(0) th").css("background-color", "#f6f7fb");
        }
    });
}

function initApiParamsTitles(pageType) {
    //  pageType: '0'场景下 ; '1'公共规则池参数(只能从公共中选参数)
    //            '2'公共规则池返回值(限定只能选一个参数) ; '3'场景下返回值参数(限定只能选一个参数)
    var paramsTitles = [];
    if (pageType == '1') {
        paramsTitles = [
            {
                title: '参数组',
                render: function (data, type, row) {
                    return row.variableGroupName;
                }
            }, {
                title: '变量别名',
                render: function (data, type, row) {
                    return row.variableAlias;
                }
            }, {
                title: '变量编码',
                render: function (data, type, row) {
                    return row.variableCode;
                }
            }, {
                title: '变量类型',
                render: function (data, type, row) {
                    return row.typeValue;
                }
            }, {
                title: '变量种类',
                render: function (data, type, row) {
                    switch ($.trim(row.kindId)) {
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
            }
        ];
    } else if (pageType == '0') {
        paramsTitles = [
            {
                title: '变量来源',
                render: function (data, type, row) {
                    switch ($.trim(row.isPublic)) {
                        case '1':
                            return '公共参数池';
                        case '0':
                            return '当前场景';
                        default:
                            return '当前场景';
                    }
                }
            }, {
                title: '参数组',
                render: function (data, type, row) {
                    return ($.trim(row.variableGroupName) == '') ? '--' : row.variableGroupName;
                }
            }, {
                title: '变量别名',
                render: function (data, type, row) {
                    return row.variableAlias;
                }
            }, {
                title: '变量编码',
                render: function (data, type, row) {
                    return row.variableCode;
                }
            }, {
                title: '变量类型',
                render: function (data, type, row) {
                    return row.typeValue;
                }
            }, {
                title: '变量种类',
                render: function (data, type, row) {
                    switch ($.trim(row.kindId)) {
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
            }
        ];
    }
    return paramsTitles;
}