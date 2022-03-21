/**
 * 组织下用户查看
 * data:2019/08/16
 * author:bambi
 */

var orgUserModal = {
    init: function (orgId, orgName) {
        // 回显组织名称
        $('.orgMsgContainer input').val(orgName);
        // 加载组织下用户表格
        var initObj = {'deptId': orgId};
        initOrgUserTable(initObj);
        // 绑定搜索事件
        $('.orgUserContent .selfAdaptionRight button').unbind().on('click', function () {
            orgUserModal.searchOrgUser(orgId);
        });
        // 展开弹框
        $('#orgUserAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 搜索
    searchOrgUser: function (orgId) {
        var obj = {};
        var input = $('.orgUserContent .selfAdaptionLeft input');
        obj['deptId'] = orgId;
        obj[input.attr('data-col')] = $.trim(input.val());
        initOrgUserTable(obj);
    }
}

/* 初始化组织下用户表格 */
function initOrgUserTable(obj) {
    $('#orgPersonTable').width('100%').dataTable({
        "searching": false, // 是否开启搜索功能
        "ordering": false,
        "destroy": true,
        "bLengthChange": false,
        "pagingType": "full_numbers",
        "paging": true,
        "info": true,
        "serverSide": true,
        "pageLength": 10,
        "columns": [
            {"title": "用户名称", "data": "userName"},
            {"title": "电话", "data": "phoneNumber"},
            {"title": "邮箱", "data": "email"},
        ],
        ajax: {
            url: webpath + '/choose/dept2User',
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
