(function (window) {
    $('[data-toggle="tooltip"]').tooltip(); // 初始化提示消息

   // 检查子菜单有无样式类，如果没有：初始化页面，赋close隐藏类
    var flag = $('#folderMenuWrap').hasClass('o')  ?  true : false
    if (flag) {
        $('#folderMenuWrap').removeClass().addClass('o');
    }else{
        $('#folderMenuWrap').removeClass().addClass('c');
    }

    window.creCommon = _common = {};

    function setActMenu(idx) {
        // 设置激活的菜单
        for (var i = 0; i < $('#menusWrap>li').length; i++) {
            var $li = $('#menusWrap>li:eq(' + i + ')');
            if ($li.attr('idx') == idx) {
                $li.addClass('active').siblings().removeClass('active');
            }
        }
        // 设置激活的场景菜单main.cssmain.css
        for (var i = 0; i < $('#folderMenuWrap>li').length; i++) {
            var $li = $('#folderMenuWrap>li:eq(' + i + ')');
            if ($li.attr('idx') == idx) {
                $li.addClass('active').siblings().removeClass('active');
                $('#folderMenuWrap').removeClass().addClass('o');
            }
        }
    }

    _common.setActMenu = setActMenu;
    function beforeLoadHtml(url) {
        return true;
    }

    _common.beforeLoadHtml = beforeLoadHtml;

    function loadHtml(url) {
        if (_common.beforeLoadHtml(url)) {
            $('#pageContent').html('');
            $.pjax({
                url: url,
                type: 'post',
                dataType: 'html',
                container: 'body'
            });
        }
    }

    _common.loadHtml = loadHtml;

    /**
     * 使用jquery的cookie方法，解决页面跳转cookie失效问题
     */
    function setCookie(key, value, iDay) {
        var oDate = new Date();
        oDate.setDate(oDate.getDate() + iDay);
        //document.cookie = key + '=' + value + ';expires=' + oDate;
        $.cookie(key, value, {path: '/', expires: oDate});
    }

    _common.setCookie = setCookie;

    function removeCookie(key) {
        //setCookie(key, '', -1);//这里只需要把Cookie保质期退回一天便可以删除
        $.cookie(key, null, {path: '/'});
    }

    _common.removeCookie = removeCookie;

    function getCookie(key) {
        /*
        var cookieArr = document.cookie.split('; ');
        for(var i = 0; i < cookieArr.length; i++) {
            var arr = cookieArr[i].split('=');
            if(arr[0] === key) {
                return arr[1];
            }
        }
        return false;*/
        return $.cookie(key);
    }

    _common.getCookie = getCookie;

    //手动设置Validator的错误信息
    function showErrorValidator(showForm, showId, showMsg) {
        $('#' + showForm).validator('showMsg', '#' + showId, {type: "error", msg: showMsg});
    }

    _common.showErrorValidator = showErrorValidator;
})(window);


(function (jQuery) {

    /**
     * 移除数组指定位置的元素
     */

    Array.prototype.remove = function (dx) {
        if (isNaN(dx) || dx > this.length) {
            return false;
        }
        for (var i = 0, n = 0; i < this.length; i++) {
            if (this[i] != this[dx]) {
                this[n++] = this[i];
            }
        }
        this.length -= 1;
    }

    /**
     * 移除指定值的元素
     */
    Array.prototype.removeVal = function (dx) {
        var isremove = false;
        if ((this.length == 1) && (this[0] == dx)) {
            this.length = 0;
            this[0] == "";
            return;
        }
        for (var i = 0, n = 0; i < this.length; i++) {
            if (this[i] != dx) {
                this[n++] = this[i];
            } else {
                isremove = true;
            }
        }
        if (isremove) {
            this.length -= 1;
        }
    }

    var bindWindowClick = function () {
        $(document).click(function (events) {
            var tgt = events.target;
            var targetCls = events.target.getAttribute('class');
            var tgtParentLen = $(tgt).parents('.cm-allOptions').length;
            var notsel = targetCls && targetCls.indexOf('cm-selectOption') < 0 && targetCls.indexOf('cm-selectIcon') < 0;
            if ((!targetCls || notsel) && tgtParentLen == 0) {
                $(".cm-allOptions").addClass('cm-noElement');
                $('.cm-selectPeacock').removeClass('active');
            }
        });
    }


    /*** 下拉框方法     ***/
    $.fn.cm_select = function (option) {
        var definedOption = {
            field: "pIpt",
            data: [],
            onselect: function () {

            }
        };
        $.extend(definedOption, option);
        var _this = this;
        var li_str = "";
        var placeHStr = '';
        var selObj = '';
        if (!definedOption) {
            return null;
        }
        if (definedOption.data) {
            li_data = definedOption.data;
            for (var i = 0; i < li_data.length; i++) {
                li_str += '<li idx = "' + li_data[i].key.toString() + '" title="' + li_data[i].text + '">' + li_data[i].text + '</li>';
                if (li_data[i].selected) {
                    selObj = li_data[i];
                }
            }
        }
        _this.addClass("cm-selectPeacock");
        if (_this.attr('class') && _this.attr('class').indexOf('cm-select-xs') < 0) placeHStr = ' placeholder="--请选择--"';

        var idDefineHtml = _this.attr('idxName') ? 'name="' + _this.attr('idxName') + '"' : 'name="' + definedOption.field + '"';
        var txtDefineHtml = _this.attr('txtName') ? 'name="' + _this.attr('txtName') + '"' : 'name="' + definedOption.field + 'Text"';
        idDefineHtml += _this.attr('idxId') ? 'id="' + _this.attr('idName') + '"' : 'id="' + definedOption.field + '"';
        txtDefineHtml += _this.attr('txtId') ? 'id="' + _this.attr('txtId') + '"' : 'id="' + definedOption.field + 'Text"';

        _this.html('<input class="cm-selectId" ' + idDefineHtml + ' value="" type="hidden"/>'
            + '<input class="cm-selectOption input-md form-control" readonly="true" ' + txtDefineHtml + placeHStr + '/>'
            + '<i class="cm-selectIcon"></i>'
            + '<ul class="cm-allOptions cm-noElement">'
            + li_str
            + '</ul>');

        if (selObj != '') {
            _this.selectValue = selObj.key;
            _this.selectText = selObj.text;
            _this.find(".cm-selectOption").val(selObj.text);
            _this.find('.cm-selectId').val(selObj.key);
        }

        //如果select id的input值变了，改变txt的值，可用于回显form.load
        _this.find(".cm-selectId").change(function () {
            var thisDom = $(this);
            var id = thisDom.val();
            for (var i = 0; i < definedOption.data.length; i++) {
                if (definedOption.data[i].key == id) {
                    thisDom.next('.cm-selectOption').val(definedOption.data[i].text);
                    _this.selectValue = id;
                    _this.selectText = definedOption.data[i].text;
                    break;
                }
            }
        });

        _this.find(".cm-selectOption,.cm-selectIcon").click(function () {
            var option_this = _this.find(".cm-selectOption");
            var option_id = _this.find('.cm-selectId');
            var topDistance = option_this.offset().top;
            var documentHeight = $(document).height();
            var bottomDistance = documentHeight - topDistance;
            if (bottomDistance < 260) {
                _this.find(".cm-allOptions").addClass("cm-turnUp");
            }
            var allOptionsClass = _this.find(".cm-allOptions").attr('class');
            _this.toggleClass('active');
            if (allOptionsClass && allOptionsClass.indexOf('cm-noElement') >= 0) {
                $(".cm-allOptions").addClass('cm-noElement');
                _this.find(".cm-allOptions").removeClass('cm-noElement');
            } else {
                _this.find(".cm-allOptions").addClass('cm-noElement');
            }
            _this.find(".cm-allOptions li").each(function () {
                var li_this = $(this);
                li_this.unbind("click").click(function () {
                    _this.find(".cm-allOptions").removeClass("cm-turnUp");
                    var select_text = li_this.text();
                    var select_id = li_this.attr("idx");
                    _this.selectValue = select_id;
                    _this.selectText = select_text;
                    option_this.val(select_text);
                    option_id.val(select_id);
                    _this.find(".cm-allOptions").addClass('cm-noElement');
                    definedOption.onselect.call(_this, _this.selectValue);
                });
            });
        });
        bindWindowClick();

        function setSelectVal(tag, key) {
            tag.selectValue = key;
            var i = 0;
            for (i = 0; i < option.data.length; i++) {
                if (option.data[i].key == key) {
                    $(tag).find(".cm-selectOption").val(option.data[i].text);
                    $(tag).find(".cm-selectId").val(key);
                    tag.selectText = option.data[i].text;
                }
            }
        }

        $.fn.extend(_this, {
            /**
             * 获取数值方法
             */
            getValue: function () {
                return this.selectValue;
            },
            getText: function () {
                return this.selectText;
            },
            setValue: function (key) {
                setSelectVal(this, key);
                definedOption.onselect.call(_this, _this.selectValue);
            }
        });

        return _this;
    }
    /*** 下拉框方法结束     ***/


    /*** 下拉列表方法     ***/
    $.fn.cm_treeSelect = function (option) {
        var definedOption = {
            field: "pIpt",
            data: [],
            treeOpt: null,
            selObj: null,
            onselect: function () {
            }
        };
        $.extend(definedOption, option);
        var _this = this;
        var li_str = "";
        var placeHStr = '';
        var selObj = definedOption.selObj;
        if (!definedOption) {
            return null;
        }

        _this.addClass("cm-listSelect");
        if (_this.attr('class').indexOf('cm-select-xs') < 0) placeHStr = ' placeholder="--请选择--"';

        var idDefineHtml = _this.attr('idxName') ? 'name="' + _this.attr('idxName') + '"' : 'name="' + definedOption.field + '"';
        var txtDefineHtml = _this.attr('txtName') ? 'name="' + _this.attr('txtName') + '"' : 'name="' + definedOption.field + 'Text"';
        idDefineHtml += _this.attr('idxId') ? 'id="' + _this.attr('idName') + '"' : 'id="' + definedOption.field + '"';
        txtDefineHtml += _this.attr('txtId') ? 'id="' + _this.attr('txtId') + '"' : 'id="' + definedOption.field + 'Text"';

        _this.html('<input class="cm-selectId" ' + idDefineHtml + ' value="" type="hidden"/>'
            + '<input class="cm-selectOption input-md form-control" readonly="true" ' + txtDefineHtml + placeHStr + '/>'
            + '<i class="cm-selectIcon"></i>'
            + '<ul class="cm-allOptions cm-noElement tree tree-menu cm-zuiSelectTree" data-ride="tree">'
            + '</ul>');

        if (definedOption.data) {
            li_data = definedOption.data;
            _this.find('.cm-allOptions').tree({
                data: li_data,
                itemCreator: function ($li, item) {
                    $li.append($('<a/>', {href: '#'}).text(item.title));
                }
            });
        }

        if (selObj != null) {
            _this.selectValue = selObj.key;
            _this.selectText = selObj.text;
            _this.find(".cm-selectOption").val(selObj.text);
            _this.find('.cm-selectId').val(selObj.key);
        }

        function searchKey(key, obj) {
            var value = '';
            for (var i = 0; i < obj.length; i++) {
                if (value != '') {
                    return value;
                }
                if (obj[i].id == key) {
                    value = obj[i].title;
                    return value;
                } else if (obj[i].children && obj[i].children.length > 0) {
                    value = searchKey(key, obj[i].children);
                }
            }
            return value;
        }

        //如果select id的input值变了，改变txt的值，可用于回显form.load
        _this.find(".cm-selectId").change(function () {
            var thisDom = $(this);
            var id = thisDom.val();
            var txt = searchKey(key, definedOption.data);
            thisDom.next('.cm-selectOption').val(txt);
            _this.selectValue = id;
            _this.selectText = txt;
        });

        _this.find(".cm-selectOption,.cm-selectIcon").click(function () {
            var option_this = _this.find(".cm-selectOption");
            var option_id = _this.find('.cm-selectId');
            var topDistance = option_this.offset().top;
            var documentHeight = $(document).height();
            var bottomDistance = documentHeight - topDistance;
            if (bottomDistance < 260) {
                _this.find(".cm-allOptions").addClass("cm-turnUp");
            }
            var allOptionsClass = _this.find(".cm-allOptions").attr('class');
            _this.toggleClass('active');
            if (allOptionsClass && allOptionsClass.indexOf('cm-noElement') >= 0) {
                $(".cm-allOptions").addClass('cm-noElement');
                _this.find(".cm-allOptions").removeClass('cm-noElement');
            } else {
                _this.find(".cm-allOptions").addClass('cm-noElement');
            }
            _this.find(".cm-allOptions li").each(function () {
                var li_this = $(this);
                li_this.unbind("click").click(function (e) {
                    if (li_this.find('ul').length == 0) {
                        _this.find(".cm-allOptions").removeClass("cm-turnUp");
                        var select_text = li_this.text();
                        var select_id = li_this.attr("data-id");
                        _this.selectValue = select_id;
                        _this.selectText = select_text;
                        option_this.val(select_text);
                        option_id.val(select_id);
                        _this.find(".cm-allOptions").addClass('cm-noElement');
                        definedOption.onselect.call(_this, _this.selectValue);
                    }
                });
            });
        });
        bindWindowClick();

        function setSelectVal(tag, key) {
            tag.selectValue = key;
            var txt = searchKey(key, option.data);
            $(tag).find(".cm-selectOption").val(txt);
            $(tag).find(".cm-selectId").val(key);
            tag.selectText = txt;
        }

        $.fn.extend(_this, {
            /**
             * 获取数值方法
             */
            getValue: function () {
                return this.selectValue;
            },
            getText: function () {
                return this.selectText;
            },
            setValue: function (key) {
                setSelectVal(this, key);
                definedOption.onselect.call(_this, _this.selectValue);
            }
        });

        return _this;
    }
    /*** 下拉列表方法结束     ***/


})(jQuery);


//下面是公共html的初始化代码
function bindCommonBtnClick() {
    //userInfo drop down click
    var timmer, $uwrap = $('.userWrap');
    $uwrap.mouseenter(function () {
        clearTimeout(timmer);
        $uwrap.addClass('active');
    }).mouseleave(function () {
        timmer = setTimeout(function () {
            $uwrap.removeClass('active');
        }, 500);
    });

    //expend or collapse left menu
    $('.ctrlBtn').click(function () {
        $('body').toggleClass('miniMenu');
    });
    $('#menusWrap > li').mouseover(function(){
        if(!$(this).hasClass('active')){
           $(this).addClass('hover');
        }
    });
    $('#menusWrap > li').mouseout(function(){
        if(!$(this).hasClass('active')){
            $(this).removeClass('hover');
        }
    })
    //左侧菜单点击事件
    $('#menusWrap > li').click(function () {
        var flagName = $('#folderMenuWrap').attr('class');
        if ($(this).hasClass('folderMenuLi')) {
            //场景目录菜单点击事件
            $(this).css('color', '#fff');
            if (flagName === 'c') {
               // $(this).children('.arr').css('display', 'none'); // 右侧图标隐藏
                $("#menusWrap > li.active").find('.ck').hide();
                $("#menusWrap > li.active").find('.nck').show();
                $('#folderMenuWrap').removeClass('c').addClass('o'); //展开子菜单+
                $(this).children('.arr').css('transform', 'rotate(0deg)');
            } else {
                $(this).children('.arr').css('transform', 'rotate(180deg)');
                $('#folderMenuWrap').removeClass('o').addClass('c');
            }
        } else {
            $('#folderMenuWrap').removeClass('o').addClass('c');
            $('.folderMenuLi').children('.arr').css('transform', 'rotate(0deg)');
            var flagStr = '&childOpen=c';
            var dataStr = $(this).attr('data');
            // var urlStr = window.location.origin + dataStr;
            console.log(flagStr + "hhh" + dataStr);
            var urlStr = window.location.origin + dataStr + flagStr;
            creCommon.loadHtml(urlStr);
        }
        $(this).addClass('active').siblings().removeClass('active');

    });

    //左侧菜单-场景点击事件
    // $('#folderMenuWrap > li').click(function () {
    //     var flagStr = '&childOpen=o';
    //     var dataStr = $(this).attr('data');
    //     var urlStr = window.location.origin + dataStr + flagStr;
    //     creCommon.loadHtml(urlStr);
    //     $(this).addClass('active');
    //     $(this).siblings().removeClass('active');
    // });

    //右上角菜单点击事件
    $('#sysMenuList > li').click(function () {
        var flagName = $('#folderMenuWrap').attr('class');
        var flagStr = '&childOpen=' + flagName;
        var dataStr = $(this).attr('data');
        // var urlStr = window.location.origin + dataStr;
        var urlStr = window.location.origin + dataStr + flagStr;
        creCommon.loadHtml(urlStr);
    });

    //修改密码点击事件
    $('#modifyPas').click(function () {
        var urlStr = window.location.origin + webpath + '/modifyPassword';
        creCommon.loadHtml(urlStr);
    });
}

function exitCommonLogin() {
    var url = webpath + "/logout";
    window.location.href = url;
}

/*  获取个人中心数据     */
function pMesage() {
    $.ajax({
        url: webpath + "/queryInfo",
        type: "post",
        success: function (data) {
            var Pmsg = JSON.parse(data);
            if (Pmsg.success) {
                // $(".list_message li:eq(0)>span").text(Pmsg.loginId);
                $(".list_message li:eq(0)>span").text(Pmsg.jobNumber);
                $(".list_message li:eq(1)>span").text(Pmsg.userRole);
                $(".list_message li:eq(2)>span").text(Pmsg.phoneNumber);
                $(".list_message li:eq(3)>span").text(Pmsg.email);
                $(".list_message li:eq(4)>span").text(Pmsg.org);
                $(".list_message li:eq(5)>span").text('');
            }
        }
    });
}

/* js获取个人中心数据      */
function ajax_method() {
    //创建异步对象
    var xhr = new XMLHttpRequest();
    //设置请求的类型及url
    //post请求一定要添加请求头才行不然会报错        CONTENT-TYPE:application/x-www-form-urlencoded含义是表示客户端提交给服务器文本内容的编码方式 是URL编码，即除了标准字符外，每字节以双字节16进制前加个“%”表示
    xhr.open('post', webpath + '/queryInfo');
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    //发送请求
    xhr.send();
    xhr.onreadystatechange = function () {
        // 这步为判断服务器是否正确响应
        if (xhr.readyState == 4 && xhr.status == 200) {
            var Pmsg = JSON.parse(xhr.responseText);
            if (Pmsg.success) {
                var list = document.getElementById("list_ul");
                var spans = list.getElementsByTagName("span");
                // // spans[0].textContent = Pmsg.loginId;
                // spans[0].textContent = Pmsg.jobNumber;
                // spans[1].textContent = Pmsg.userRole;
                // spans[2].textContent = Pmsg.phoneNumber;
                // spans[3].textContent = Pmsg.email;
                // spans[4].textContent = Pmsg.org;
                // spans[5].textContent = "";

                spans[0].textContent = Pmsg.jobNumber;
                spans[1].textContent = Pmsg.phoneNumber;
                spans[2].textContent = Pmsg.email;
            }
        }
    }
}

// 定义success主题提示消息
var successMessager = new $.zui.Messager({
    type: 'success', // 定义颜色主题
    placement: 'center', // 位置
    icon: 'ok-sign',
    time: 2000
});

// 定义danger主题提示消息
var failedMessager = new $.zui.Messager({
    type: 'danger',
    placement: 'center',
    icon: 'exclamation-sign',
    time: 10000
});

// 定义warning主题提示消息
var warningMessager = new $.zui.Messager({
    type: 'warning', // 定义颜色主题
    placement: 'center', // 位置
    icon: 'info-sign',
    time: 2000
});

$(function () {
    bindCommonBtnClick();
    ajax_method();
});
