var operators = new Array();
var intVariableTree = jQuery.extend(true, [], inVariableTree);

var expRuleConfigObj = {
    expParamMap: {}, //  记录引用参数/指标的Map[name:{key:'varId'/'kpiId', id:paramId}]
    references: [], // json回传idArr与表达式一一对应
}

var ruleCfg = new (function () {
    // 给textArea的鼠标处添加值
    //      field 文本框容器
    //      value 文本内容
    //      isNode 是否为节点(变量/指标true; 运算符false)
    //      nodeKey 'varId'/'kpiId' 用于json标识变量/指标
    //      nodeId 节点id varId/kpiId
    this.insertAtCursor = function (field, value, isNode, nodeKey, nodeId) {
        if (isNode) {
            if (!nodeId) {
                failedMessager.show('参数[nodeId]缺失！');
                return;
            }
            if (!expRuleConfigObj.expParamMap[value]) {
                var nodeObj = {key: nodeKey, id: '[' + nodeId + ']'};
                expRuleConfigObj.expParamMap[value] = nodeObj;
            }
        }
        // IE support
        if (document.selection) {
            field.focus();
            sel = document.selection.createRange();
            sel.text = value;
            sel.select();
        }
        // MOZILLA/NETSCAPE support
        else if (field.selectionStart || field.selectionStart == '0') {
            var startPos = field.selectionStart;
            var endPos = field.selectionEnd;
            // save scrollTop before insert www.keleyi.com
            var restoreTop = field.scrollTop;
            field.value = field.value.substring(0, startPos) + value + field.value.substring(endPos, field.value.length);
            if (restoreTop > 0) {
                field.scrollTop = restoreTop;
            }
            field.focus();
            field.selectionStart = startPos + value.length;
            field.selectionEnd = startPos + value.length;
        } else {
            field.value += value;
            field.focus();
        }
    },
        // 验证表达式
        this.vaildateRule = function () {
            var flag = true;
            reseqReferences(); // 重排idList
            var sendObj = {};
            if (!isPublicRuleSet) { // 规则库下不传folderId
                sendObj.folderId = folderId;
            }
            sendObj.expRule = $("#coreTxt").val();
            sendObj.references = JSON.stringify(expRuleConfigObj.references);
            $.ajax({
                url: webpath + "/rule/vaildateRule",
                data: sendObj,
                dataType: "json",
                type: "post",
                async: false,
                success: function (data) {
                    //验证成功
                    if (data.status === 0) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                }
            });
            return flag;
        },
        //在列表 obj中查txt  在下拉列表中
        this.searchVariableType = function (obj) {
            for (var i = 0; i < obj.length; i++) {
                if (obj[i].children && obj[i].children.length > 0) {
                    obj[i].isParent = true;
                    ruleCfg.searchVariableType(obj[i].children);
                } else {
                    if (obj[i].typeId != '2') {
                        obj.splice(i, 1);
                        i--;
                    }
                }
            }
        },
        this.init = function () {
            ruleCfg.searchVariableType(intVariableTree);

            // 清空按钮
            $("button.clear").click(function () {
                // 清空textarea
                $("#coreTxt").val('');
                expRuleConfigObj.expParamMap = {};
            });

            //提交到后台校验
            $("button.test").click(function () {
                if (ruleCfg.vaildateRule()) {
                    successMessager.show('规则正确');
                } else {
                    failedMessager.show('表达式验证失败，请检查表达式格式或参数类型是否合法');
                }
            });

            // 保存配置按钮事件
            $("#expRuleSave").click(function () {
                // 验证表达式是否正确
                if (ruleCfg.vaildateRule()) {
                    reseqReferences(); // 重排idList
                    $('#expRuleModal').removeAttr('status');
                    $("#expRuleModal").modal('hide');
                    if (isPublicRuleSet) { // 规则库下
                        $('#ruleConfigModal').modal({"show": "center", "backdrop": "static"});
                    }
                } else {
                    failedMessager.show('表达式验证失败，请检查表达式格式或参数类型是否合法');
                }
            });

            // 取消配置按钮事件
            $("#expRuleCancle").click(function () {
                $('#expRuleModal').removeAttr('status');
                $("#coreTxt").val(expRuleContent);
                $("#expRuleModal").modal('hide');
                if (isPublicRuleSet) { // 规则库下
                    $('#ruleConfigModal').modal({"show": "center", "backdrop": "static"});
                }
            });

            // 点击运算符
            $(".conditonConn > input[type='button']").click(function () {
                var value = $(this).val();
                ruleCfg.insertAtCursor(document.getElementById("coreTxt"), value, false);
            });

            // varTree = $.fn.zTree.init($("#varsTree"), pkgVarsTreeSetting, intVariableTree); // 动态数据
            varTree = $.fn.zTree.init($("#varsTree"), pkgVarsTreeSetting, inVariableTree); // 动态数据

            // [treeNode=选择指标]绑定点击事件
            $('#varsTree').find("a[title='选择指标']").click(function () {
                $('#expRuleModal').attr('status', 1);
                $('#expRuleModal').modal('hide');
                importKpiModal.initPage(1);
            });
        }
});

var varsTreeFun = {
    rMoveCurNode: null,
    //移除拖拽目标的active属性
    removeActive: function () {
        $('#coreTxt.active').removeClass('active');
    },
    //变量树的拖拽开始时触发函数
    dragMove: function (e, treeId, treeNodes) {
        // 给 document 绑定 mousemove 事件，自行监控
        if (!$(".cfgCore").is(":visible")) return;
        // 当鼠标拖拽的位置在目标元素上时该元素加蓝色外发光
        var isTarget = null,
            i;
        var targetCls = ["coreTxt"];
        var cls = $(e.target).attr("class");

        for (i = 0; i < targetCls.length; i++) {
            var parentCls = $(e.target).parents("." + targetCls[i]);
            if (cls != undefined && cls.indexOf(targetCls[i]) >= 0 && parentCls.length == 0) {
                varsTreeFun.removeActive();
                $(e.target).addClass('active');
            } else if (parentCls.length > 0) {
                varsTreeFun.removeActive();
                $(".funParamList").addClass('active');
            }
        }
    },
    // 拖拽变量树的子节点后松开时触发函数
    onDropParame: function (e, treeId, treeNodes, targetNode, moveType) {
        // mousemove unbind
        if (!$(".cfgCore").is(":visible")) return;

        var mX = 0,
            mY = 0,
            i, j, pos, domType,
            valTxt0 = "[" + treeNodes[0].name + "]",
            node = treeNodes[0],
            valTxts = "";
        var varId = node.id;
        var e = e || window.event;
        var targetCls = $(e.target).attr("class");

        // 判断添加到哪个元素上
        if (targetCls != undefined && targetCls.indexOf("coreTxt") >= 0) {
            //textarea中
            ruleCfg.insertAtCursor(document.getElementById("coreTxt"), valTxt0, true, 'varId', varId);
        }

        // 变激活状态为平常状态
        varsTreeFun.removeActive();
    },
    // 设置是父节点的节点不可拖拽, 初始化目标元素的坐标信息
    varsTreeBeforeDrag: function (treeId, treeNode) {
        if (treeNode[0].id === "KPI") { // 选择指标目录拖动事件无效
            return false;
        }
        if (treeNode[0].isParent == true) {
            return false;
        } else {
            return true;
        }
    },
}


var varTree;
// zTree 的参数配置
var pkgVarsTreeSetting = {
        data: {
            simpleData: {
                enable: true,
                idKey: 'id',
                pIdKey: 'pid'
            },
            key: {
                name: 'name'
            }
        },
        edit: {
            enable: true,
            drag: {
                isCopy: true,
                isMove: false,
                prev: false,
                next: false,
                inner: false
            },
            showRenameBtn: false,
            showRemoveBtn: false
        },
        view: {
            selectedMulti: false
        },
        callback: {
//	        onRightClick: varsTreeFun.OnRightClick,
            beforeDrag: varsTreeFun.varsTreeBeforeDrag,
            onDrop: varsTreeFun.onDropParame,
            onDragMove: varsTreeFun.dragMove
        }
    },
    myVarsTreeSetting = {
        data: {
            simpleData: {
                enable: true,
                idKey: 'id',
                pIdKey: 'pid'
            },
            key: {
                name: 'name'
            }
        },
        callback: {
            beforeDrag: varsTreeFun.varsTreeBeforeDrag,
            onRemove: varsTreeFun.varszTreeOnRemove,
            onDrop: varsTreeFun.onDropParame,
            onDragMove: varsTreeFun.dragMove
        },
        edit: {
            enable: true,
            drag: {
                isCopy: true,
                isMove: false,
                prev: false,
                next: false,
                inner: false
            },
            showRenameBtn: false,
            showRemoveBtn: true
        },
        view: {
            selectedMulti: false
        }
    };

//function calcContentH() {
//    var wh = document.documentElement.clientHeight;
//    var ww = $(window).width();
//    var H = wh - $(".takePostion").height() - 30;
//    $("#expCfg").height(H);
//    var th1 = H - $(".wrapTitle").height() - 45;
//    var th2 = th1 - $("#splitTitle").height() - 25;
//    $("#varsTree").height(th2);
//    $(".expCfgParts").height(th1);
//}

// idMap与文字顺序校验重排序
function reseqReferences() {
    var text = $("#coreTxt").val();
    var referencesArr = [];
    if (text == '')
        return;
    var regex = /\[(.+?)\]/g;
    var paramsArr = text.match(regex);
    if (paramsArr !== null && paramsArr.length > 0) {
        for (var i = 0; i < paramsArr.length; i++) {
            var paramName = paramsArr[i];
            if (!expRuleConfigObj.expParamMap[paramName]) {
                failedMessager.show('expRuleConfig reseqReferences Error!');
                return [];
            }
            var obj = expRuleConfigObj.expParamMap[paramName];
            var referenceObj = {};
            referenceObj[obj.key] = obj.id;
            referencesArr.push(referenceObj);
        }
    }
    expRuleConfigObj.references = referencesArr; // 保存表达式退出弹框时更新references数组至元素属性
}

// 回显表达式expParamMap
function echoExpParamMap(referencesArrEcho) {
    if ($.trim(expRuleContent) == '' || referencesArrEcho.length <= 0) {
        return;
    }
    var regex = /\[(.+?)\]/g;
    var paramNameArr = expRuleContent.match(regex);
    if (paramNameArr !== null && paramNameArr.length > 0) {
        if (paramNameArr.length !== referencesArrEcho.length) {
            failedMessager.show('echoExpParamMap 回显参数idMap失败！');
            return;
        }
        for (var i = 0; i < referencesArrEcho.length; i++) {
            for (var key in referencesArrEcho[i]) {
                expRuleConfigObj.expParamMap[paramNameArr[i]] =
                    {key: key, id: referencesArrEcho[i][key]};
            }
        }
    }
}

var genSltHtml = function (Objdata, tagDiv) {
    tagDiv.css("width", (tagDiv.parent().width() - 2) + "px");
    tagDiv.combobox({
        valueField: 'val',
        textField: 'name',
        editable: false,
        data: Objdata,
        panelHeight: 'auto',
        onLoadSuccess: function () {
            tagDiv.combobox('setValue', Objdata[0].val);
        }
    });
}

$(function () {
    ruleCfg.init();
//    calcContentH();
    $(window).resize(function () {
//        calcContentH();
    });
    $('#expRuleModal').on('shown.zui.modal', function () {
        var status = $('#expRuleModal').attr('status');
        if (status && status === '1') {
            return;
        } else {
            $("#coreTxt").val(expRuleContent);
            if (expCdtObj.attr('references') && expCdtObj.attr('references') != []) {
                expRuleConfigObj.references = JSON.parse(expCdtObj.attr('references')); // 回显referencesArr
                echoExpParamMap(JSON.parse(expCdtObj.attr('references'))); // 回显expParamMap
            }
        }
    });
    $('#expRuleModal').on('hidden.zui.modal', function () {
        var status = $('#expRuleModal').attr('status');
        if (status && status === '1') {
            return;
        } else {
            expCdtObj.val($("#coreTxt").val());
            expCdtObj.attr('references', JSON.stringify(expRuleConfigObj.references)); // 记录referencesArr到元素
            // 清除缓存
            expRuleConfigObj.expParamMap = {};
            expRuleConfigObj.references = [];
        }
    });
});

