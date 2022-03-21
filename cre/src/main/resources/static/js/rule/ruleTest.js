/**
 * 规则测试相关操作js
 */
//测试
var testFn = {
    test: function () {
        var param = new Object();
        $("input[type='text']").each(function () {
            var code = $(this).attr('name');
            var typeId = $(this).attr('variableType');
            var value = $(this).val();
            /*if(typeId != 1){
                if (parseFloat(value).toString() != "NaN") {
                    value = parseFloat(value);
                }
            }*/
            param[code] = value;
        });
        $("#ruleTestRuslt").empty().html('');
        var msgHeader = '#' + testFn.getDateAndTime() + '# ';
        $("#ruleTestRuslt").append('<p>' + msgHeader + '开始编译模型... </p>');
        $.ajax({
            url: webpath + "/ruleTest/test",
            data: {
                "ruleId": ruleId,
                "folderId": folderId,
                "paramStr": JSON.stringify(param)
            },
            type: "post",
            dataType: "json",
            success: function (data) {
                msgHeader = '#' + testFn.getDateAndTime() + '# ';
                $("#ruleTestRuslt").append('<p>' + msgHeader + '场景Id:' + folderId + '</p>');
                $("#ruleTestRuslt").append('<p>' + msgHeader + '场景名称:' + folderName + '</p>');
                $("#ruleTestRuslt").append('<p>' + msgHeader + '模型Id:' + ruleId + '</p>');
                $("#ruleTestRuslt").append('<p>' + msgHeader + '模型名称:' + ruleName + '</p>');
                $("#ruleTestRuslt").append('<p>' + msgHeader + '输入数据:' + JSON.stringify(param) + '</p>');
                if (data.status === 0) {
                    msgHeader = '#' + testFn.getDateAndTime() + '# ';
                    $("#ruleTestRuslt").append('<p>' + msgHeader + '执行完毕.');
                    // $("#ruleTestRuslt").append('<p>' + msgHeader + '输出结果：<table id="result"></table></p>');
                    // testFn.loadResultTable( JSON.parse( data.data ) );
                    $("#ruleTestRuslt").append('<p>' + msgHeader + '输出结果 <span class="green">' + data.data + '</span>.</p>');
                } else {
                    // var errorMsg = jQuery.parseJSON(data.data);
                    // var html = '<div>';
                    // html += '	<p><span class="iconOpen"></span><span class="red">' + msgHeader + errorMsg.message + '</span></p>';
                    // html += '	<ul>';
                    // html += '		<li>' + errorMsg.e + '</li>';
                    // //html +='		<li>请确保服务器没有挂掉</li>';
                    // html += '	</ul>';
                    // html += '</div>';
                    // $("#ruleTestRuslt").append(html);
                    // testFn.bindToggleDetail();

                    var html = '<div>';
                    html += '	<p><span class="iconOpen"></span><span class="red">' + msgHeader + data.msg + '</span></p>';
                    html += '</div>';
                    $("#ruleTestRuslt").append(html);
                    testFn.bindToggleDetail();
                }
            },
            error: function () {
                $("#testPackage .testRuslt").append('<p>' + msgHeader + '[ERROR]执行异常.');
            }
        });
    },

    loadResultTable: function ( res ) {
        var data = [];
        var obj = {
            title: '模型输入结果',
            result: res.paramNameValueMap
        };
        data.push( obj );
        $( '#ruleTestRuslt #result' ).width( '100%' ).dataTable( {
            "searching": false, // 是否开启搜索功能
            "ordering": false,
            "destroy": true,
            "bLengthChange": false,
            "paging": false,
            "info": false,
            "columns": [
                {
                    "title": "",
                    "data": "title"
                },
                {
                    "title": "计算结果",
                    "data": "result",
                    render: function ( value ) {
                        var html = '<div class="code">';
                        var keys = '<div class="keys">';
                        var values = '<div class="values">';
                        Object.keys( value ).map( function ( key ) {
                            keys += '<div>' + key + '：</div>';
                            values += '<div>' + value[ key ] + '</div>';
                        } );
                        keys += '</div>';
                        values += '</div>';
                        html += keys + values + '</div>';
                        return html;
                    }
                },
            ],
            "data": data
        } );
    },

    /**绑定测试详细信息中详细信息按钮*/
    bindToggleDetail: function () {
        $("#ruleTestRuslt > div .iconOpen").unbind();
        $("#ruleTestRuslt > div .iconOpen").click(function () {
            $(this).toggleClass("closing");
            $("ul", $(this).parent().parent()).slideToggle();
        });
    },
    /**测试页面中的测试按钮点击事件*/
    getDateAndTime: function () {
        var myDate = new Date();
        var date = myDate.toLocaleDateString();//可以获取当前日期
        var time = myDate.toLocaleTimeString();//可以获取当前时间
        var ss = myDate.getMilliseconds();//毫秒
        return date + ' ' + time + "." + ss;
    },
    // 上一步
    lastPage: function () {
        var hrefStr = webpath;
        if ($.trim(folderName) === '模型库') {
            var flagName = $('#folderMenuWrap').attr('class');
            var flagStr = '&childOpen=' + flagName;
            hrefStr += "/modelBase/view?idx=16" + flagStr + '&jumpRuleName=' + ruleName + '&moduleName=' + moduleName;
        } else {
            hrefStr += "/ruleFolder/rulePackageMgr?folderId=" + folderId + '&childOpen=o';
        }
        creCommon.loadHtml(hrefStr);
    }
};
$(function () {
});
      
