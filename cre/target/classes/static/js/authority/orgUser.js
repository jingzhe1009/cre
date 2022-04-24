/**
 * 机构管理下查看渠道
 * data:2019/08/16
 * author:bambi
 */

var orgUserModal = {
    init: function (deptId, deptName) {
        // 回显组织名称
        $('.orgMsgContainer input').val(deptName);
        // 加载组织下渠道表格
        var initObj = {'deptId': deptId};
        initOrgChanTable(initObj);
        // 绑定搜索事件
        $('.orgUserContent .selfAdaptionRight button').unbind().on('click', function () {
            orgUserModal.searchOrgUser(deptId);
        });
        // 展开弹框
        $('#orgUserAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 搜索
    searchOrgUser: function (deptId) {
        var obj = {};
        var input = $('.orgUserContent .selfAdaptionLeft input');
        obj['deptId'] = deptId;
        obj[input.attr('data-col')] = $.trim(input.val());
        initOrgChanTable(obj);
    }
}

/* 初始化组织下渠道表格 */
function initOrgChanTable(obj) {
    $('#chanOrgTable').width('100%').dataTable({
        "searching": false, // 是否开启搜索功能
        "ordering": false,
        "destroy": true,
        "bLengthChange": false,
        "bAutoWidth" : false,
        "pagingType": "full_numbers",
        "paging": true,
        "info": true,
        "serverSide": true,
        "pageLength": 10,
        "columns": [
            {"title": "渠道名称", "data": "channelName"},
            {"title": "渠道编码", "data": "channelCode"},
            {"title": "所属机构", "data": "deptName"},
        ],
        ajax: {
            url: webpath + '/choose/deptChannel',
            "type": 'POST',
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



