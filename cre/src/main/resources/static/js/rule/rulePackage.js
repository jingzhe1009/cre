/**
 * 规则文件夹相关操作js
 */

var rulePackageObj = {
    modelVersionMap: {}, // ruleName: versionsStr
    // 加载版本下拉列表
    searchModelVersion: function ( curRuleName, that ) {
        if ( !curRuleName ) {
            return;
        }
        var versionsStr = '';
        if ( rulePackageObj.modelVersionMap[ curRuleName ] ) {
            versionsStr = rulePackageObj.modelVersionMap[ curRuleName ];
            that.siblings( '.versionSelMenu' ).html( versionsStr );
        } else {
            $.ajax( {
                url: webpath + '/rule/versions',
                type: 'GET',
                dataType: "json",
                data: { "ruleName": curRuleName, "isPublic": "0" },
                success: function ( data ) {
                    if ( data.status === 0 ) {
                        for ( var i = 0; i < data.data.length; i++ ) {
                            versionsStr += '<li onclick="rulePackageObj.versionCheckBind(\'' + curRuleName + '\', \'' + data.data[ i ].ruleId + '\', $(this))"><a>' + data.data[ i ].version + '</a></li>';
                        }
                        that.siblings( '.versionSelMenu' ).html( versionsStr );
                        rulePackageObj.modelVersionMap[ curRuleName ] = versionsStr;
                    } else {
                        failedMessager.show( data.msg );
                    }
                }
            } );
        }
    },
    // 切换版本事件
    versionCheckBind: function ( oldRuleName, ruleId, that ) {
        var oldRuleId = that.parents( '.modelCard' ).prop( 'id' ).replace( 'modelCard_', '' ); // 获取当前ruleId
        $.ajax( {
            url: webpath + '/rule/getRuleByRuleId',
            type: 'POST',
            dataType: "json",
            data: { "ruleId": ruleId },
            success: function ( data ) {
                if ( data.status === 0 ) {
                    var newCardStr = rulePackageObj.updateCardContent( data.data );
                    $( "#modelCard_" + oldRuleId ).after( newCardStr );
                    $( "#modelCard_" + oldRuleId ).remove();
                    rulePackageObj.changeStatusBind();
                } else {
                    failedMessager.show( data.msg );
                }
            }
        } );
    },
    // 更新卡片页
    updateCardContent: function ( obj ) {
        var newCardHtmlStr = (
            '<li id="modelCard_' + obj.ruleId + '" class="modelCard col-xs-12 col-sm-12 col-md-6 col-lg-4">' +
            '<div class="card_container card_background_' + obj.ruleType + '">' +
            '<div class="card_header">' +
            '<div class="card_title" title="' + obj.ruleName + '">' + obj.moduleName + '<i class="editModelBase icon icon-pencil" onclick="editModelBase(\'' + obj.ruleName + '\')"></i></div>' +
            '<div class="card_subtitle">' +
            '<div class="versionSel">' +
            '<span id="versionSelSpan_' + obj.ruleId + '" class="versionSpan">' + obj.version + '</span>' +
            '<label for="versionSelSpan_' + obj.ruleId + '" class="input-control-icon-right versionSelLabel" data-toggle="dropdown" onclick="rulePackageObj.searchModelVersion(\'' + obj.ruleName + '\', $(this))">' +
            '<i class="icon icon-caret-down"></i>' +
            '</label>' +
            '<ul class="dropdown-menu versionSelMenu"></ul>' +
            '</div>' +
            '<input type="checkbox" idx="' + obj.ruleId + '" id="ck_' + obj.ruleId + '" class="inset_3" rule-name="' + obj.ruleName + '" ' + ( obj.ruleStatus === '1' ? 'checked' : '' )  + ' >' +
            '<label for="ck_' + obj.ruleId + '" class="blue"></label>' +
            '</div>' +
            '</div>' +
            '<div class="card_main">' +
            '<div class="card_content_left" title="' + ( obj.versionDesc ? obj.versionDesc : '暂无介绍' ) + '">' +
            '<p>' + ( obj.versionDesc ? obj.versionDesc : '暂无介绍' ) + '</p>' +
            '</div>' +
            '<div class="hr" ></div>' +
            '<div class="card_content_right">' +
            '<div class="card_button_group">' +
            '<div class="link" onclick="testRule(\'' + obj.ruleId + '\',\'' + obj.ruleName + '\')">测试</div>' +
            '<div class="link" onclick="ruleTrial(\'' + obj.ruleId + '\',\'' + obj.ruleName + '\')">试算</div>' +
            '</div>' +
            '<div class="card_button_group">' +
            '<div class="link" onclick="cloneRule(\'' + obj.ruleId + '\', \'' + obj.ruleName + '\', \'' + obj.ruleDesc + '\', \'' + obj.ruleType + '\', \'' + obj.isPublic + '\', \'' + obj.modelGroupId + '\', \'' + obj.deptId + '\', \'' + obj.deptName + '\', \'' + obj.partnerCode + '\', \'' + obj.partnerName + '\')">克隆</div>' +
            ( obj.version.indexOf( "草稿" ) === -1 ? '<div class="link" onclick="publishModel(\'' + obj.ruleId + '\')">发布</div>' : '' ) +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="card_footer">' +
            '<button class="butD1" onclick="editRule(\'' + obj.ruleId + '\',\'' + obj.ruleName + '\',\'' + obj.folderId + '\', 1)">查看</button>' +
            '<button class="butD1" onclick="editRule(\'' + obj.ruleId + '\',\'' + obj.ruleName + '\',\'' + obj.folderId + '\', -1)">编辑</button>' +
            '<button class="butD1" onclick="deleteRule(\'' + obj.ruleId + '\', \'' + obj.ruleName + '\')">删除</button>' +
            '<button class="butD1" onclick="serviceApi(\'' + obj.ruleId + '\')">服务API</button>' +
            // '<button class="butD1" >设为公有</button>' +
            '</div>' +
            '</div>' +
            '</li>'
        );
        return newCardHtmlStr;
    },
    // 启用停用 规则状态切换
    changeStatusBind: function () {
        $( ".inset_3" ).unbind().on( 'change', function () {
            var value = $( this ).is( ":checked" );
            if ( $( this ).parents( '.card_subtitle' ).find( '.versionSpan' ).text().indexOf( '草稿' ) !== -1 ) {
                failedMessager.show( '草稿不能修改状态！' );
                $( this ).prop( 'checked', !value );
                return;
            }
            // 是否有
            var ruleId = $( this ).attr( 'idx' );
            if ( ruleId == null || ruleId === '' ) {
                return;
            }
            var ruleName = $( this ).attr( 'rule-name' );
            if ( ruleName == null || ruleName === '' ) {
                return;
            }
            var status = '';
            var msg = '';
            if ( value ) {
                status = "1";
                msg = '规则启用';
            } else {
                status = "2";
                msg = '规则停用';
            }

            // 启用停用权限 authCheck
            $.ajax( {
                url: webpath + '/rule/enable/checkAuth',
                type: 'GET',
                dataType: "json",
                data: { "ruleName": ruleName },
                success: function ( data ) {
                    if ( data.status === 0 ) {
                        // 修改状态
                        $.ajax( {
                            url: webpath + "/rule/updateRuleStatus",
                            data: {
                                "folderId": folderId,
                                "ruleId": ruleId,
                                "status": status
                            },
                            type: "post",
                            dataType: "json",
                            success: function ( data ) {
                                if ( data.status === 0 ) {
                                    successMessager.show( msg + '成功' );
                                    // setTimeout( resetPage, 500 ); // 刷新页面 刷新模型操作
                                } else {
                                    failedMessager.show( msg + '失败' );
                                    // $( this ).prop( 'checked', !value );
                                    setTimeout( resetPage, 500 ); // 刷新页面 刷新模型状态
                                }
                            }
                        } );
                    } else {
                        failedMessager.show( data.msg );
                        setTimeout( resetPage, 500 ); // 刷新页面 刷新模型状态
                        // $( this ).prop( 'checked', !value );
                    }
                }
            } );
        } );
    },
    $ajax: function ( url, method, body, callback, dataType ) {
        if ( dataType === void 0 ) dataType = 'json';
        var option = {
            url: webpath + url,
            type: method,
            dataType: dataType,
            success: function ( data ) {
                callback( data );
            },
            error: function ( XMLHttpRequest, textStatus, errorThrown ) {
                console.log( errorThrown );
                if ( textStatus === 'timeout' ) {
                    console.log( '请求超时' );
                }
            }
        };
        if ( body ) option.data = body;
        $.ajax( option );
    },
}

// 刷新页面
function resetPage () {
    var flagName = $( '#folderMenuWrap' ).attr( 'class' );
    var flagStr = '&childOpen=' + flagName;
    var url = webpath + "/ruleFolder/rulePackageMgr?folderId=" + folderId + flagStr;
    creCommon.loadHtml( url );
}

// 测试
function testRule ( ruleId, ruleName, moduleName ) {
    if ( ruleId === null || ruleId === '' || ruleName === '' ) {
        return;
    }
    // 规则测试 authCheck
    $.ajax( {
        url: webpath + '/rule/test/checkAuth',
        type: 'GET',
        dataType: "json",
        data: { "ruleName": ruleName },
        success: function ( data ) {
            if ( data.status === 0 ) {
                var url = webpath + "/ruleTest/index?ruleId=" + ruleId + "&folderId=" + folderId + "&ruleName=" + ruleName + "&moduleName=" + moduleName + "&childOpen=o";
                creCommon.loadHtml( url );
            } else {
                failedMessager.show( data.msg );
            }
        }
    } );
}

// 试算
function ruleTrial ( ruleId, ruleName, moduleName ) {
    if ( ruleId === null || ruleId === '' || ruleName === '' ) {
        return;
    }
    $.ajax( {
        url: webpath + '/rule/trial/checkAuth',
        type: 'GET',
        dataType: "json",
        data: { "ruleName": ruleName },
        success: function ( data ) {
            if ( data.status === 0 ) {
                var url = webpath + "/ruleTrial/index?ruleId=" + ruleId + "&folderId=" + folderId + "&ruleName=" + ruleName + "&moduleName=" + moduleName + "&childOpen=o";
                creCommon.loadHtml( url );
            } else {
                failedMessager.show( data.msg );
            }
        }
    } );
}

// 克隆
function cloneRule ( ruleId, ruleName, ruleDesc, ruleType, isPublic, modelGroupId, deptId, deptName, partnerCode, partnerName ) {
    $.ajax( {
        url: webpath + '/rule/clone/checkAuth',
        type: 'GET',
        dataType: "json",
        data: { "ruleName": ruleName },
        success: function ( data ) {
            if ( data.status === 0 ) {
                var param = {
                    oldRuleId: {
                        ruleId: ruleId,
                        ruleName: ruleName,
                        ruleDesc: ruleDesc,
                        ruleType: ruleType,
                        isPublic: isPublic,
                        modelGroupId: modelGroupId,
                        deptId: deptId,
                        deptName: deptName,
                        partnerCode: partnerCode,
                        partnerName: partnerName
                    }
                };
                // 传递参数，合并对象
                ruleClone.data = Object.assign( {}, ruleClone.data, param );
                $('#ruleClone .modelGroupId label').text('').text('产品组');
                $('#ruleClone #folderId').text('模型库');
                $('#ruleClone .folderId').hide();
                // 打开弹出框
                ruleClone.handleModelToggle( true );
            } else {
                failedMessager.show( data.msg );
            }
        }
    } );
}

// 编辑/查看
function editRule ( ruleId, ruleName, folderId, isEdit ) {
    if ( ruleId == null || ruleId == '' ) {
        return;
    }
    if ( ruleName == null || ruleName == '' ) {
        return;
    }
    if ( folderId == null || folderId == '' ) {
        return;
    }
    folderId = window.location.href.split('=')[1].split('&')[0]
    if ( isEdit === 1 ) { // 查看操作
        // 查看权限 authCheck
        $.ajax( {
            url: webpath + '/rule/view/checkAuth',
            type: 'GET',
            dataType: "json",
            data: { "ruleName": ruleName },
            success: function ( data ) {
                if ( data.status === 0 ) {
                    var url = webpath + "/rule/updateRule?ruleId=" + ruleId + "&folderId=" + folderId + "&childOpen=o&pageType=5";
                    creCommon.loadHtml( url );
                } else {
                    failedMessager.show( data.msg );
                }
            }
        } );

    } else if ( isEdit === -1 ) { // 编辑操作
        // 编辑权限 authCheck
        // $.ajax( {
        // 	url: webpath + '/rule/update/checkAuth',
        // 	type: 'GET',
        // 	dataType: "json",
        // 	data: { "ruleName": ruleName },
        // 	success: function ( data ) {
        // 		if ( data.status === 0 ) {
        var url = webpath + "/rule/updateRule?ruleId=" + ruleId + "&folderId=" + folderId + "&childOpen=o&pageType=3";
        creCommon.loadHtml( url );
        // 		} else {
        // 			failedMessager.show( data.msg );
        // 		}
        // 	}
        // } );
    } else {
        return;
    }
}

// 更新缓存
function updateRuleCache ( ruleId, folderId ) {
    $.ajax( {
        url: webpath + "/rule/updateRuleCache",
        data: {
            "folderId": folderId,
            "ruleId": ruleId
        },
        type: "post",
        dataType: "json",
        success: function ( data ) {
            if ( data.status === 0 ) {
                successMessager.show( '更新缓存成功' );
            } else {
                failedMessager.show( '更新缓存失败，' + data.msg );
            }
        }
    } );
}

// 删除规则
function deleteRule ( ruleId, ruleName ) {
    if ( ruleId === null || ruleId === '' ) {
        return;
    }
    $.ajax( {
        url: webpath + '/rule/delete/checkAuth',
        type: 'GET',
        dataType: "json",
        data: { "ruleName": ruleName },
        success: function ( data ) {
            if ( data.status === 0 ) {
                rulePackageObj.$ajax( '/rule/versions?ruleName=' + ruleName + '&isPublic=0', 'GET', null, function ( data ) {
                    var html = '<option value="">全部版本</option>';
                    for ( var i = 0; i < data.data.length; i++ ) {
                        html += '<option value="' + data.data[ i ].ruleId + '">' + data.data[ i ].version + '</option>';
                    }
                    $( '#delRuleDiv #version' ).html( html );
                } );
                $( '#delRuleDiv #version' ).attr( 'data-rule-name', ruleName );
                $( "#delRuleDiv" ).modal( {} );
            } else {
                failedMessager.show( data.msg );
            }
        }
    } );
}

// 删除文件夹
function deleteFolder () {
    $( "#delFolderDiv" ).modal( {} );
}

// 新建模型
function insertRule () {
    // var url = webpath + "/create?idx=2&ruleFolder=" + folderId;
    // creCommon.loadHtml( url );
    $('#insertModelAlert').modal();
}

// 添加产品（其他）中的模型
function addRule () {
    $('#addModel').modal({'show': 'center', "backdrop": "static"});
    initModelTable();

    $('#saveModel').unbind('click').on('click',function() {
        var modelList = [];
        $('#modelTable').find(':checkbox').each(function(){
            var detail = {};
            if ($(this).prop("checked")) {
                var curRow = this.parentNode.parentNode;
                detail = $('#modelTable').DataTable().row(curRow).data();
                modelList.push(detail);
            }
        });
        var obj = {
            'modelGroupId': folderId,
            'modelList':modelList,
            'channelList':'',
        }
        var json = JSON.stringify(obj);
        $.ajax({
            url: webpath + '/modelBase/group/addModel',
            type: 'POST',
            dataType: "json",
            data: json,
            contentType:"application/json;charset=UTF-8",
            success: function (data) {
                if (data.status === 0) {
                    successMessager.show('保存成功');
                    setTimeout( resetPage, 500 );
                } else {
                    failedMessager.show(data.msg);
                }
            }
        });
    })
}

// 初始化 添加模型弹窗中的表格
function initModelTable(){
    $('#modelTable').width('100%').dataTable({
        destroy:true,
        paging:false,
        info:false,
        searching:false,
        ordering:false,
        "columns": [
            {"title": "模型名称", "data": "ruleName"},
            {"title": "模型类型", "data": "ruleType","targets":[1],"searchable":false},
            {"title": "模型描述", "data": "ruleDesc","targets":[2],"searchable":false},
            {"title": "是否添加","render": function (row) {
                    var htmlStr = '';
                    htmlStr += '<input type="checkbox" />';
                    return htmlStr;
                }
            }],
        ajax:{
            url: webpath + '/modelBase/group/getModel',
            type: 'POST',
            "dataSrc": function ( data ) {
                var baseData = data.data.modelList;
                for(var i = 0; i < baseData.length; i++){
                    if(baseData[i].ruleType == 0){
                        baseData[i].ruleType = '评分模型';
                    }
                    if(baseData[i].ruleType == 1){
                        baseData[i].ruleType = '规则模型';
                    }
                }
                return baseData;
            },
            "data": {
                'modelGroupId':'defaultGroup',
            }
        },
        "fnDrawCallback": function (oSettings, json) {
            $("#modelTable th").css("text-align", "center");
            $("#modelTable td").css("text-align", "center");
        },
    });
}
// 返回按钮
function goBack() {
    var proUrl = $('#menusWrap li :contains("产品管理")').parent().attr('data')
    var finalUrl = proUrl.substr(4)
    var url = webpath + finalUrl + "&childOpen=c"
    creCommon.loadHtml(url);
}

//接口 产品相关信息
function initInformation() {
    var detail = JSON.parse(sessionStorage.getItem('detail'));
    console.log(detail);
    $('#pageInformation p:eq(0)').html('创建人：'+detail.createPerson+'&nbsp;&nbsp;&nbsp;创建时间：'+detail.createDate);
    $('#pageInformation p:eq(1)').html('产品名称：'+detail.modelGroupName+'&nbsp;&nbsp;&nbsp;产品编码：'+detail.modelGroupCode);
    $('#pageInformation p:eq(2)').html('产品描述：'+detail.modelGroupDesc);
    $('#pageInformation p:eq(3)').html('调用渠道：'+detail.modelGroupChannel);
}

// 模型设为公有
function setCommon ( ruleId, folderId ) {
    if ( !ruleId || !folderId ) {
        return;
    }
    publishModelObj.initPage( ruleId, folderId );
}

// 编辑模型基础信息
function editModelBase ( ruleName ) {
    // 清空表单
    $( '#editModelBaseDiv form' )[ 0 ].reset();
    $( '#editModelBaseDiv form' ).validator( 'cleanUp' );
    $.ajax( {
        url: webpath + '/rule/update/checkAuthModel',
        type: 'GET',
        dataType: "json",
        data: { "ruleName": ruleName },
        success: function ( data ) {
            if ( data.status === 0 ) {
                $.ajax( {
                    url: webpath + '/rule/public/header/info',
                    type: 'GET',
                    dataType: "json",
                    data: { "ruleName": ruleName },
                    success: function ( data ) {
                        if ( data.status === 0 ) {
                            // 数据回显
                            for ( var key in data.data ) {
                                if ( key === 'ruleType' ) { // 模型类型单独处理
                                    $( '#ruleTypeSelector option[ruleType="' + data.data[ key ] + '"]' ).prop( 'selected', true );
                                    continue;
                                }
                                if ( key === 'ruleName' ) { // 记录模型名称(主键)
                                    $( '#editModelBaseDiv' ).attr( 'oldRuleName', data.data[ key ] );
                                }
                                if ( key === 'moduleName' ) { // 记录模型展示名称
                                    $( '#editModelBaseDiv' ).attr( 'oldModuleName', data.data[ key ] );
                                }
                                var target = $( "#editModelBaseDiv .form-control[col-name='" + key + "']" );
                                if ( target.length > 0 ) {
                                    $( target ).val( data.data[ key ] );
                                }
                            }
                            $( '#editModelBaseDiv' ).modal( { "show": "center", "backdrop": "static" } );
                        } else {
                            failedMessager.show( data.msg );
                        }
                    }
                } );
            } else {
                failedMessager.show( data.msg );
            }
        }
    } );
}

// 模型发布 不升版本仅修改状态
function publishModel ( ruleId ) {
    if ( !ruleId ) {
        return;
    }
    $.ajax( {
        url: webpath + "/rule/changeToExecute",
        data: { "ruleId": ruleId },
        type: "post",
        dataType: "json",
        success: function ( data ) {
            if ( data.status === 0 ) {
                successMessager.show( '发布成功' );
                setTimeout( resetPage, 500 ); // 刷新页面 刷新模型操作
            } else {
                failedMessager.show( '发布失败，' + data.msg );
            }
        }
    } );
}

// 导出
function exportFromFolder(){
    exportModal.initExportPage(2,1, getExportParams() );
}

function getExportParams( modelId, modelName, versionId, versionName ) {
    return {
        "folderObj": { "folderId": folderId, "folderName": folderName },
        "modelObj": { "modelId": modelId, "modelName": modelName },
        "versionObj": { "versionId": versionId, "versionName": versionName }
    };
}

// 导入
function importModels(){
    importModal.initUploadPage( folderId, false );
}

// 服务API
function serviceApi( ruleId ){
    serviceAPIModal.initPage( folderId, ruleId );
}

$( function () {
    // 模型启停用
    rulePackageObj.changeStatusBind();

    initModelTable();
    initInformation();

    // 添加模型-下一步
    $('#insertModal').click(function () {
        var checkedRuleType = $('#insertModelAlert option:selected').attr('value');
        var url = webpath + "/rule/ruleConfig?folderId=" + folderId + "&ruleType=" + checkedRuleType + '&childOpen=o&pageType=0';
        creCommon.loadHtml(url);
    });

    // 删除规则对话框确定按钮点击事件
    $( "#delRuleBtn" ).click( function () {
        var ruleId = $( '#delRuleDiv #version' ).val();
        var ruleName = $( '#delRuleDiv #version' ).attr( 'data-rule-name' );
        if ( ruleId !== '' ) {
            rulePackageObj.$ajax( '/rule/deleteRule', 'POST', { ruleId: ruleId, ruleName: ruleName }, function ( data ) {
                if ( data.status === 0 ) {
                    successMessager.show( '删除成功' );
                    $( "#delRuleDiv" ).modal( 'hide' );
                    window.location.reload();
                } else {
                    failedMessager.show( '删除失败，' + data.msg );
                    $( "#delRuleDiv" ).modal( 'hide' );
                }
            } );
        } else {
            rulePackageObj.$ajax( '/rule/deleteByName', 'POST', { ruleName: ruleName }, function ( data ) {
                if ( data.status === 0 ) {
                    successMessager.show( '删除成功' );
                    $( "#delRuleDiv" ).modal( 'hide' );
                    window.location.reload();
                } else {
                    failedMessager.show( '删除失败，' + data.msg );
                    $( "#delRuleDiv" ).modal( 'hide' );
                }
            } );
        }
    } );

    // 删除文件夹对话框确定按钮点击事件
    $( "#delFolderBtn" ).click( function () {
        $.ajax( {
            url: webpath + "/ruleFolder/deleteFolder",
            data: {
                "folderId": folderId
            },
            type: "post",
            dataType: "json",
            success: function ( data ) {
                if ( data.status === 0 ) {
                    new $.zui.Messager( '删除成功', {
                        placement: 'center', // 定义显示位置
                        time: 2000,//表示时间延迟，单位毫秒
                        type: 'success' // 定义颜色主题
                    } ).show();
                    $( "#delFolderDiv" ).modal( 'hide' );
                    var url = webpath + "/main?childOpen=o";
                    creCommon.loadHtml( url );
                } else {
                    new $.zui.Messager( '删除失败' + data.msg, {
                        time: 2000,//表示时间延迟，单位毫秒
                        placement: 'center', // 定义显示位置
                        type: 'warning' // 定义颜色主题
                    } ).show();
                    $( "#delFolderDiv" ).modal( 'hide' );
                }
            }
        } );
    } );

    /* 鼠标滑过事件 */
    $( ".ruleContent>li" ).mouseover( function () {
        $( this ).find( ".a" ).addClass( "addS" );
        $( this ).find( "p" ).addClass( "addPs" );
    } );
    $( ".ruleContent>li" ).mouseleave( function () {
        $( this ).find( ".a" ).removeClass( "addS" );
        $( this ).find( "p" ).removeClass( "addPs" );
    } );

    // 基础修改保存
    $( '#saveModelBase' ).click( function () {
        if ( !$( '#editModelBaseDiv form' ).isValid() ) {
            return;
        }
        var obj = {};
        var inputs = $( '#editModelBaseDiv .form-control' );
        for ( var i = 0; i < inputs.length; i++ ) {
            obj[ $( inputs[ i ] ).attr( 'col-name' ) ] = $.trim( $( inputs[ i ] ).val() );
        }
        obj[ 'ruleType' ] = $( '#ruleTypeSelector option:selected' ).attr( 'ruleType' );
        obj[ 'oldRuleName' ] = $( '#editModelBaseDiv' ).attr( 'oldRuleName' );
        obj[ 'oldModuleName' ] = $( '#editModelBaseDiv' ).attr( 'oldModuleName' );
        obj[ 'isPublic' ] = '0';
        if ( obj ) {
            $.ajax( {
                url: webpath + '/rule/public/header/update',
                type: 'POST',
                dataType: "json",
                data: obj,
                success: function ( data ) {
                    if ( data.status === 0 ) {
                        $( '#editModelBaseDiv' ).modal( 'hide' );
                        successMessager.show( '保存成功' );
                        setTimeout( resetPage, 500 ); // 刷新页面 刷新模型操作
                    } else {
                        failedMessager.show( data.msg );
                    }
                }
            } );
        }
    } );

    // 模型克隆初始化
    // var param = {
    //     folderId: folderId
    // };
    ruleClone.init();
    // 克隆模态框事件绑定
    ruleClone.event.onSuccess = function () {
        window.location.reload();
    };
} );
      
