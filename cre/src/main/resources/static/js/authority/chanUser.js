/**
 * 渠道下用户查看
 *
 *
 */

var chanUserModal = {
    init: function (channelId, channelName) {
        // 回显渠道名称
        $('.chanMsgContainer input').val(channelName);
        // 加载渠道下用户表格
        var initObj = {'channelId': channelId};
        initChanUserTable(initObj);
        // 绑定搜索事件
        $('.chanUserContent .selfAdaptionRight button').unbind().on('click', function () {
            chanUserModal.searchChanUser(channelId);
        });
        // 展开弹框
        $('#chanUserAlertModal').modal({'show': 'center', "backdrop": "static"});
    },
    // 搜索
    searchChanUser: function (channelId) {
        var obj = {};
        var input = $('.chanUserContent .selfAdaptionLeft input');
        obj['channelId'] = channelId;
        obj[input.attr('data-col')] = $.trim(input.val());
        initChanUserTable(obj);
    }
}

/* 初始化渠道下用户表格 */
function initChanUserTable(obj) {
    $('#chanPersonTable').width('100%').dataTable({
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
            url: webpath + '/choose/channel2User',
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
