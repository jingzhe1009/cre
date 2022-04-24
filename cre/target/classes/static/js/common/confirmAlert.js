// 确认弹框
var confirmAlert = {
    /**
     * @param msg: 弹框提示信息
     * @param confirmHandler: 确认handler
     * @param cancelHandler: 取消handler
     */
    show: function (msg, confirmHandler, cancelHandler) {
        $('#msgText').text(msg ? msg : '');
        if (confirmHandler) {
            $('#msgConfirm').unbind().on('click', function () {
                confirmHandler();
            });
        }
        if (cancelHandler) {
            $('#msgCancel').unbind().on('click', function () {
                cancelHandler();
            });
        }
        $('#msgAlertModal').modal({'show': 'center', "backdrop": "static"});
    },

    hide: function () {
        $('#msgAlertModal').modal('hide');
    }
}