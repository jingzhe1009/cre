var ruleTrial = {	data: {		ruleId: ruleId,		logId: null,		size: 5,		isTrial: false,		isTrialCheck: false,		isClick: true,		jumpPage: false,		variableList: variableList,		kpiMapList: kpiMapList,		outVariableList: outVariableList,		compareSel: [			{ key: "+", text: " 加 ", isRegExp: false, reg: /\+/g },			{ key: "-", text: " 减 ", isRegExp: false, reg: /\-/g  },			{ key: "*", text: " 乘 ", isRegExp: false, reg: /\*/g },			{ key: "/", text: " 除 ", isRegExp: false, reg: /\//g },			{ key: "=", text: " 等于 ", isRegExp: true },			{ key: ">", text: " 大于 ", isRegExp: true },			{ key: "<", text: " 小于 ", isRegExp: true },			{ key: "==", text: " 等于 ", isRegExp: true },			{ key: "!=", text: " 不等于 ", isRegExp: true },			{ key: ">=", text: " 大于等于 ", isRegExp: true },			{ key: "<=", text: " 小于等于 ", isRegExp: true },			{ key: " in ", text: " 属于 ", isRegExp: true },			{ key: " && ", text: " 且 ", isRegExp: true },			{ key: " || ", text: " 或 ", isRegExp: true }		],		resultInput: {},		trialData: null,		ruleKeyMap: {},        isCompareResultShow: false,		isTrialSuccess: false, // 试算通过/不通过，该状态仅会因试算请求操作变动		varTipsMap: {}, //参数规则提示映射		kpiTipsMap: {}, //指标规则提示映射},	failedMessager :new $.zui.Messager({		type: 'danger',		placement: 'center',		icon: 'exclamation-sign',		time: 0 //不会自动消失	}),	successMessager :new $.zui.Messager({		type: 'success',		placement: 'center',		icon: 'ok-sign',		time: 0	}),	// 初始化	init: function () {		moment.locale( 'zh-cn', {			meridiem: function ( hour, minute ) {				if ( hour < 9 ) {					return "早上";				} else if ( hour < 11 && minute < 30 ) {					return "上午";				} else if ( hour < 13 && minute < 30 ) {					return "中午";				} else if ( hour < 18 ) {					return "下午";				} else {					return "晚上";				}			}		} );		$( ".form-datetime input" ).datetimepicker(			{				language: "zh-CN",				weekStart: 1,				todayBtn: 1,				autoclose: 1,				todayHighlight: 1,				startView: 2,				minView: 0,				forceParse: 0,				format: "yyyy-mm-dd hh:ii:ss"			}		);		creCommon.beforeLoadHtml = ruleTrial.handleLevelPage;		ruleTrial.initKeyMap();		ruleTrial.initParameter();		ruleTrial.initKpi();		ruleTrial.initImplementationDetails();		ruleTrial.initOverallResult();		ruleTrial.modelParamsTipsAnalysis();	},	// 初始化参数 id-name 映射数据	initKeyMap: function () {		ruleTrial.$ajax( '/ruleTrial/getVariableAndKpiDesc?ruleId=' + ruleTrial.data.ruleId, 'GET', null, function ( data ) {			ruleTrial.data.ruleKeyMap = data.data;		} );	},	// 初始化参数表格	initParameter: function () {		var variableList = ruleTrial.data.variableList;		if ( variableList.length !== 0 ) {			ruleTrial.$ajax( '/variable/variableType/list', 'GET', null, function ( data ) {				var variableTypeKeyMap = {};				for ( var i = 0; i < data.data.length; i++ ) {					variableTypeKeyMap[ data.data[ i ].key ] = data.data[ i ].text;				}				$( '.parameter' ).show();				$( '.parameter #parameter' ).width( '100%' ).dataTable( {					"searching": false, // 是否开启搜索功能					"ordering": false,					"destroy": true,					"bLengthChange": false,					"paging": false,					"info": false,					"autoWidth": false,					"columns": [						{							"title": "参数名称",							"data": "variableAlias",							"width": "15%",							// TODO 试算参数提示							"render": function ( value, key, data ) {								var varTipArr = ruleTrial.data.varTipsMap[ data.variableId ] ? ruleTrial.data.varTipsMap[ data.variableId ] : [];								var ruleContent = '<div><p>无</p></div>';								if ( varTipArr.length > 0 ) {									ruleContent = ruleTrial.getTipContent( varTipArr );								}								var html =									'<div class="ruleTrial_paramsWithTip" onclick="ruleTrial.showTips( $(this) )" >' +										'<span>' + data.variableAlias + '</span>' +										'<span class="ruleTrial_tipBox_leftTriangle hide"></span>' +										'<div class="ruleTrial_tipBox hide">' + ruleContent + '</div>' +									'</div>';								return html;							}						},						{							"title": "描述",							"data": "variableAlias",							"width": "15%"						},						{							"title": "类型",							"data": "typeId",							"width": "15%",							"render": function ( value ) {								return variableTypeKeyMap[ value ];							}						},                        {                            "title": "值域",                            "data": "variableRange",                            "width": "15%",							"render": function (value) {                            	var variableRange = ruleTrial.kpiRangeTranslate( value );                            	var html = '';                            	if (variableRange) {                            		html =										'<div class="variableRangeFather">' +											'<div class="variableRangeDiv" onclick="ruleTrial.handleRangeDetailD( $(this) )" >' +												variableRange +											'</div>' +											'<p class="variableRangeDetailD variableRangeDetail hide" >'+												variableRange +											'</p>' +											'<span class="variableRangeDetailD hide"></span>' +										'</div>';								}								return html;							}                        },						{							"title": "基准值",							"width": "40%",							"render": function ( value, key, data ) {								var code = data.aliasCode === void 0 ? data.variableCode : data.aliasCode;								return (									'<div class="form-group">' +									'<input class="form-control form-focus" type="text" name=' + code + ' data-rule="' + data.variableAlias + ':required;" />' +									'</div>'								);							}						}					],					"data": variableList				} );			} );		}	},	// 获取参数/指标规则详情展示内容	getTipContent: function ( paramTipArr ) {		// 按照规则集整理数据		var ruleSetMap = {};		for ( var i = 0, iLen = paramTipArr.length; i < iLen; i++ ){			var ruleSetIndex = paramTipArr[i].ruleSetIndex;			if ( ruleSetMap[ruleSetIndex] ) {				ruleSetMap[ruleSetIndex].push( paramTipArr[i] );			} else {				ruleSetMap[ruleSetIndex] = [ paramTipArr[i] ];			}		}		// 组织 tip 显示内容		var html = '';		html += '<div>';		Object.keys( ruleSetMap ).map( function( value, index ){			var tipArr = ruleSetMap[value];			for ( var j = 0, jLen = tipArr.length; j < jLen; j++ ) {				if ( j === 0 ) {					html += '<p class="tipContent_ruleSetName" ><b> 规则集名称：' + tipArr[j].ruleSetName + '</b></p>'; //规则集名称				}				// html += '<p class="tipContent_ruleName" ><b>' + tipArr[j].ruleName + '</b></p>'; //规则名称				html += '<p class="tipContent_ruleContent" >' + tipArr[j].ruleContent + '</p>'; //规则内容			}		} );		html += '</div>';		return html;	},	handleRangeDetailD: function( $cur ){		var details = $cur.siblings( '.variableRangeDetailD' );		var show = !$( details ).hasClass( 'hide' );		if ( show ) {			$( details ).addClass( 'hide' );		} else {			$( details ).removeClass( 'hide' );		}	},	// 初始化指标	initKpi: function () {		ruleTrial.$ajax( '/kpi/getKpiType', 'GET', null, function ( data ) {			var kpiTypeKeyMap = {};			for ( var i = 0; i < data.data.length; i++ ) {				kpiTypeKeyMap[ data.data[ i ].key ] = data.data[ i ].text;			}			var kpiMapList = ruleTrial.data.kpiMapList;			$( '.kpi #kpi' ).width( '100%' ).dataTable( {				"searching": false, // 是否开启搜索功能				"ordering": false,				"destroy": true,				"bLengthChange": false,				"paging": false,				"info": false,				"autoWidth": false,				"columns": [					{						"title": "指标名称",						"width": "15%",						"render": function ( value, key, data ) {							var kpiTipArr = ruleTrial.data.kpiTipsMap[ data.kpiId ] ? ruleTrial.data.kpiTipsMap[ data.kpiId ] : [];							var ruleContent = '<div><p>无</p></div>';							if ( kpiTipArr.length > 0 ) {								ruleContent = ruleTrial.getTipContent( kpiTipArr );							}							var html =								'<div class="ruleTrial_paramsWithTip" onclick="ruleTrial.showTips( $(this) )" >' +								'<span>' + data.kpiName + '</span>' +								'<span class="ruleTrial_tipBox_leftTriangle hide"></span>' +								'<div class="ruleTrial_tipBox hide">' + ruleContent + '</div>' +								'</div>';							return html;						}					},					{						"title": "描述",						"data": "kpiDesc",						"width": "15%"					},					{						"title": "类型",						"data": "kpiType",						"width": "15%",						"render": function ( value ) {							return kpiTypeKeyMap[ value ];						}					},                    {                        "title": "值域",                        "data": "kpiRange",                        "width": "15%",						"render": function (value) {							return ruleTrial.kpiRangeTranslate( value );						}                    },					{						"title": "基准值",						"width": "40%",						"render": function ( value, key, data ) {							return (								'<div class="form-group">' +								'<input class="form-control form-focus" type="text" name=' + data.kpiCode + ' data-rule="' + data.kpiName + ':required;" />' +								'</div>'							);						}					}				],				"data": kpiMapList			} );		} );	},	// 开始试算	trial: function () {		ruleTrial.data.isTrialSuccess = false; //每次试算都先重置试算状态		if ( !ruleTrial.data.isClick ) {			failedMessager.show( '试算中，请稍后！！' );			return;		}		ruleTrial.data.isClick = false;		if ( !$( '#pageContent form' ).isValid() ) {			ruleTrial.data.isClick = true;			return;		}		var param = {};		$( ".ruleTrial .parameter input[name], .ruleTrial .kpi input[name]" ).each( function () {			var code = $( this ).attr( 'name' );			param[ code ] = $( this ).val();		} );		var body = {			ruleId: ruleId,			folderId: folderId,			paramStr: JSON.stringify( param )		};		ruleTrial.$ajax( '/ruleTrial/trial', 'POST', body, function ( data ) {			if ( data.status === 0 ) {                ruleTrial.data.isCompareResultShow = true;                // TODO				// ruleTrial.handleToggleBtn( 1 );				ruleTrial.data.isTrial = true;				var res = JSON.parse( data.data );				ruleTrial.data.logId = res.logId;				var showRes = Object.assign( {}, res );				delete showRes.logId;				ruleTrial.data.trialData = showRes;				ruleTrial.initImplementationDetails(); //试算执行详情				ruleTrial.initOverallResult(); //试算总结果				$( '.result>div' ).show();				// TODO 根据试算结果发起试算通过/不通过的判定				if ( ruleTrial.data.isTrialSuccess ) {					ruleTrial.checkTrial( 'success' );				} else {					ruleTrial.checkTrial( 'failed' );				}			} else {				// failedMessager.show( '试算失败：' + data.msg );				ruleTrial.failedMessager.show( '试算失败：' + data.msg );			}			ruleTrial.data.isClick = true;			ruleTrial.data.isTrialSuccess = false;		} );	},	// 试算执行详情	initImplementationDetails: function () {		var trialData = ruleTrial.data.trialData;		var res = trialData ? trialData.executorDetail.ruleLogDetailQueue : [];		var data = [];		res.map( function ( item ) {			if ( item.nodeType === 'task' ) {				item.result = JSON.parse( item.result );				data.push( item );			}		} );		$( '.result #implementationDetails' ).width( '100%' ).dataTable( {			"searching": false, // 是否开启搜索功能			"ordering": false,			"destroy": true,			"bLengthChange": false,			"paging": false,			"info": false,			"autoWidth": false,			"columns": [				{					"title": "规则名称",					"data": "nodeName",					"width": "20%"				},				{					"title": "命中规则",					"width": "40%",					"render": function ( value, key, data ) {						var result = data.result;						var html = "";						if ( Array.isArray( result ) ) {							for ( var i = 0; i < result.length; i++ ) {								// var rule = result[ i ].rule.split( " " )[ 2 ];								var ruleEle = result[ i ].rule;								var rule = ruleEle.slice( ruleEle.indexOf( ' if' ) + 4, ruleEle.indexOf( 'then' ) - 1 );								html += '<div>' + ruleTrial.ruleTranslate( rule ) + '</div>';							}						}						return html;					}				},				{					"title": "计算结果",					"width": "40%",					"render": function ( value, key, data ) {						var result = data.result;						var html = "";						if ( Array.isArray( result ) ) {							for ( var i = 0; i < result.length; i++ ) {								var rule = result[ i ].rule.match( /\{([^\}]+)\}/ )[ 1 ];								html += '<div>' + ruleTrial.ruleTranslate( rule ) + '</div>';							}						}						return html;					}				}			],			"data": data		} );	},	// 试算总结果	initOverallResult: function () {		$.fn.dataTable.ext.errMode = 'none';		var trialData = ruleTrial.data.trialData;		var res = trialData ? trialData.paramMap : {};		var outVariableList = [].concat( ruleTrial.data.outVariableList );		$( '.result #overallResult' )			.on( 'error.dt', function ( e, settings, techNote, message ) {				console.log( 'An error has been reported by DataTables: ', message );			} )			.width( '100%' ).dataTable( {			"searching": false, // 是否开启搜索功能			"ordering": false,			"destroy": true,			"bLengthChange": false,			"paging": false,			"info": false,			"autoWidth": false,			"columns": [				{					"title": "输出参数名称",					"data": "variableAlias",					"width": "20%"				},				{					"title": "计算结果",					"width": "20%",					"render": function ( value, key, data ) {						var code = data.aliasCode === void 0 ? data.variableCode : data.aliasCode;						return res[ code ] === void 0 ? '' : res[ code ];					}				},				{					"title": "基准值",					"width": "20%",					"render": function ( value, key, data ) {						var code = data.aliasCode === void 0 ? data.variableCode : data.aliasCode;						var resultInput = ruleTrial.data.resultInput;						var val = resultInput[ code ] !== void 0 ? resultInput[ code ] : '';						return (							'<div class="form-group">' +							'<input class="form-control form-focus" type="text" name=' + code + ' data-rule="' + data.variableAlias + ':required;" value="' + val + '" onchange="ruleTrial.resultInputChange( \'' + code + '\' )" />' +							'</div>'						);					}				},				{					"title": "基准结果",					"width": "20%",					"render": function ( value, key, data ) {						// var code = data.aliasCode === void 0 ? data.variableCode : data.aliasCode;						// var resultInput = ruleTrial.data.resultInput;						// var val = resultInput[ code ] !== void 0 ? isNaN( parseFloat( resultInput[ code ] ) ) ? resultInput[ code ] : parseFloat( resultInput[ code ] ) : '';						// var html = "";						// if ( val === res[ code ] ) {						// 	html = "<i class=\"icon icon-check\"></i>";						// } else {						// 	html = "<i class=\"icon icon-times\"></i>";						// }						// return html;						var code = data.aliasCode === void 0 ? data.variableCode : data.aliasCode;						var resultInput = ruleTrial.data.resultInput;						var val = resultInput[ code ] !== void 0 ? resultInput[ code ] : '';						var html = "";						if ( val == res[ code ] ) {							html = "<i class=\"icon icon-check\"></i>";						} else {							html = "<i class=\"icon icon-times\"></i>";						}						return html;					}				},				{					"title": "判断结果",					"width": "20%",					"render": function ( value, x, data, key ) {						if ( key.row === 0 ) {							var resultInput = ruleTrial.data.resultInput;							var html = "通过";							for ( var i = 0; i < outVariableList.length; i++ ) {								var code = outVariableList[ i ].aliasCode === void 0 ? outVariableList[ i ].variableCode : outVariableList[ i ].aliasCode;								var val = resultInput[ code ] !== void 0 ? isNaN( parseFloat( resultInput[ code ] ) ) ? resultInput[ code ] : parseFloat( resultInput[ code ] ) : '';								if ( res[ code ] !== val ) {									html = "失败";									break;								}							}							if ( !ruleTrial.data.isClick ) { //试算请求引起的表格刷新								if ( html === "通过" ) {									ruleTrial.data.isTrialSuccess = true;								} else {									ruleTrial.data.isTrialSuccess = false;								}							}							return html;						} else {							return null;						}					}				}			],			"columnDefs": [ {				"targets": 4,				"createdCell": function ( td, cellData, rowData, row ) {					if ( row === 0 ) {						$( td ).attr( 'rowspan', outVariableList.length );					} else {						$( td ).remove();					}				}			} ],			"data": outVariableList,            "fnDrawCallback": function ( oSettings, json ) {				// FIXME                // if ( ruleTrial.data.isCompareResultShow ) {                //     $('#overallResult tr').find('td:gt(2)').css('display', '');                //     $('#overallResult tr').find('th:gt(2)').css('display', '');                // } else {                //     $('#overallResult tr').find('td:gt(2)').css('display', 'none');                //     $('#overallResult tr').find('th:gt(2)').css('display', 'none');                // }            }		} );	},	resultInputChange: function ( key ) {		ruleTrial.data.resultInput[ key ] = $( 'input[name="' + key + '"]' ).val();		ruleTrial.initOverallResult();	},	// 试算通过/失败	checkTrial: function ( status ) {		if ( !ruleTrial.data.logId ) {			failedMessager.show( 'logId错误' );			return false;		}		var body = {			logId: ruleTrial.data.logId		};		ruleTrial.$ajax( '/ruleTrial/trial/' + status, 'POST', body, function ( data ) {			var text = status === 'success' ? '通过' : '不通过';			if ( data.status === 0 ) {				// ruleTrial.data.isTrialCheck = true;				// ruleTrial.handleToggleBtn( 0 );				// successMessager.show( '试算' + text + '成功' );				// TODO				ruleTrial.data.isTrialCheck = true;				status === 'success' ? ruleTrial.successMessager.show( '试算' + text ) : ruleTrial.failedMessager.show( '试算' + text )				// ruleTrial.successMessager.show( '试算' + text );			} else {				// failedMessager.show( '试算' + text + '失败' );				// TODO				ruleTrial.data.isTrialCheck = true;				ruleTrial.failedMessager.show( "试算失败！" + data.msg );			}		} );	},	// 查询试算历史	searchTrial: function () {		var keyList = [ 'startDate', 'endDate', 'operateIsSuccess' ];		var body = {			modelId: ruleId		};		for ( var i = 0; i < keyList.length; i++ ) {			var value = $( '#' + keyList[ i ] ).val();			if ( value !== '' ) {				body[ keyList[ i ] ] = value;			}		}		ruleTrial.loadTrialHistoryTable( 1, body );	},	// 加载试算历史表格	loadTrialHistoryTable: function ( type, param ) {		var option = {};		if ( type === 0 ) {			option = {				"data": []			};		} else {			option = {				"paging": true,				"info": true,				"serverSide": true,				"ajax": {					"url": webpath + '/ruleTrial/trial/history',					"type": 'GET',					"data": function ( d ) { // 查询参数						return $.extend( {}, d, param );					}				}			};		}		$( '#trialHistoryTable' ).width( '100%' ).dataTable( Object.assign( {}, {			"searching": false,			"ordering": false,			"destroy": true,			"bLengthChange": false,			"pagingType": "full_numbers",			"paging": false,			"info": false,			"pageLength": ruleTrial.data.size,			"columns": [				{ "title": "模型名称", "data": "modelName", "width": '16%' },				{ "title": "模型版本", "data": "modelVersion", "width": '16%' },				// {				// 	"title": "参数", "data": "operateContent", "width": '20%', render: function ( value ) {				// 		value = JSON.parse( value );				// 		var html = '<div class="code">';				// 		var keys = '<div class="keys">';				// 		var values = '<div class="values">';				// 		Object.keys( value ).map( function ( key ) {				// 			keys += '<div>' + key + '<span>：</span></div>';				// 			values += '<div>' + value[ key ] + '</div>';				// 		} );				// 		keys += '</div>';				// 		values += '</div>';				// 		html += keys + values + '</div>';				// 		return html;				// 	}				// },				{ "title": "试算人", "data": "operatePerson", "width": '16%' },				// {				// 	"title": "操作结果", "data": "operateResult", "width": '24%', render: function ( value ) {				// 		value = JSON.parse( value );				// 		var html = '<div class="code">';				// 		var keys = '<div class="keys">';				// 		var values = '<div class="values">';				// 		Object.keys( value ).map( function ( key ) {				// 			keys += '<div>' + key + '<span>：</span></div>';				// 			values += '<div>' + value[ key ] + '</div>';				// 		} );				// 		keys += '</div>';				// 		values += '</div>';				// 		html += keys + values + '</div>';				// 		return html;				// 	}				// },				{ "title": "试算时间", "data": "operateTime", "width": '20%' },				{					"title": "试算结果", "data": "operateIsSuccess", "width": '16%', render: function ( value ) {						return value === '1' ? '成功' : '失败';					}				},				{				    "title": "操作", "data": null, "width": '16%', render: function( data, type, row ) {				    	var html = '';						html += '<div id="historyDetail_' + row.logId + '" style="display: none;">' + JSON.stringify(row) + '</div>';						html += '<span type="button" class="cm-tblB" onclick="ruleTrial.trialHistoryDetail( \''+ row.logId + '\' )">详情</span>';						$( "#historyDetail_" + row.logId ).data( "rowData", row );                        return html;				    }				}			]		}, option ) );	},	// 试算历史详情	trialHistoryDetail: function( logId ) {		var paramsArr = [];		var resultArr = [];		ruleTrial.initHistoryDetailRulesDetail( logId );		var detail = JSON.parse( $( '#historyDetail_'+logId ).text() );		var operateContentObj = JSON.parse( detail.operateContent );		Object.keys( operateContentObj ).map( function ( key ) {			paramsArr.push ({ "key": key, "value": operateContentObj[ key ] } )		} );		var operateResultObj = JSON.parse( detail.operateResult );		Object.keys( operateResultObj ).map( function ( key ) {			resultArr.push ({ "key": key, "value": operateResultObj[ key ] } )		} );		ruleTrial.initHistoryDetailParams( paramsArr );		ruleTrial.initHistoryDetailResult( resultArr );		$( '#trialHistory' ).modal( 'hide' );		$( '#trialHistoryDetail' ).modal();	},	trialHistoryDetailClose: function() {		$( '#trialHistory' ).modal( { show: 'center', backdrop: "static", position: 20 } );	},	initHistoryDetailParams: function( paramsArr ) {		$( '#trialHistoryDetail_params' ).width( '100%' ).dataTable( {			"searching": false, // 是否开启搜索功能			"ordering": false,			"destroy": true,			"bLengthChange": false,			"paging": false,			"info": false,			"autoWidth": false,			"columns": [				{					"title": "输入字段",					"data": "key",					"width": "50%"				},				{					"title": "输入值",					"data": "value",					"width": "50%"				}			],			"data": paramsArr		} );	},	initHistoryDetailResult: function( resultArr ) {		$( '#trialHistoryDetail_result' ).width( '100%' ).dataTable( {			"searching": false, // 是否开启搜索功能			"ordering": false,			"destroy": true,			"bLengthChange": false,			"paging": false,			"info": false,			"autoWidth": false,			"columns": [				{					"title": "输出字段",					"data": "key",					"width": "50%"				},				{					"title": "输出结果",					"data": "value",					"width": "50%"				}			],			"data": resultArr		} );	},	initHistoryDetailRulesDetail: function( logId ) {		$( '#trialHistoryDetail_ruleDetail' ).width( '100%' ).dataTable( {			"searching": false, // 是否开启搜索功能			"ordering": false,			"destroy": true,			"bLengthChange": false,			"paging": false,			"info": false,			"autoWidth": false,			"columns": [				{					"title": "规则名称",					"data": "nodeName",					"width": "20%"				},				{					"title": "命中规则",					"width": "40%",					"render": function ( value, key, data ) {						var result = JSON.parse( data.result );						var html = "";						if ( Array.isArray( result ) ) {							for ( var i = 0; i < result.length; i++ ) {								// var rule = result[ i ].rule.split( " " )[ 2 ];								var ruleEle = result[ i ].rule;								var rule = ruleEle.slice( ruleEle.indexOf( ' if' ) + 4, ruleEle.indexOf( 'then' ) - 1 );								html += '<div>' + ruleTrial.ruleTranslate( rule ) + '</div>';							}						}						return html;					}				},				{					"title": "计算结果",					"width": "40%",					"render": function ( value, key, data ) {						var result = JSON.parse( data.result );						var html = "";						if ( Array.isArray( result ) ) {							for ( var i = 0; i < result.length; i++ ) {								var rule = result[ i ].rule.match( /\{([^\}]+)\}/ )[ 1 ];								html += '<div>' + ruleTrial.ruleTranslate( rule ) + '</div>';							}						}						return html;					}				}			],			ajax: {				"url": webpath + '/ruleTrial/trial/historyDetail',				"type": 'GET',				"data": function ( d ) {					return $.extend( {}, d, { 'modelTrialLogId': logId } );				}			}		} );	},	// 打开试算历史	trialHistory: function () {		ruleTrial.searchTrial();		$( '#trialHistory' ).modal( { show: 'center', backdrop: "static", position: 20 } );	},	// 返回上一页	lastPage: function () {		var hrefStr = webpath;		if ( $.trim( folderName ) === '模型库' ) {			var flagName = $( '#folderMenuWrap' ).attr( 'class' );			var flagStr = '&childOpen=' + flagName;			hrefStr += "/modelBase/view?idx=16" + flagStr + '&jumpRuleName=' + ruleName + '&moduleName=' + moduleName;		} else {			hrefStr += "/ruleFolder/rulePackageMgr?folderId=" + folderId + '&childOpen=o';		}		creCommon.loadHtml( hrefStr );	},	handleLevelPage: function ( url ) {		if ( ( ruleTrial.data.isTrial && !ruleTrial.data.isTrialCheck ) && !ruleTrial.data.jumpPage ) {			ruleTrial.data.nextUrl = url;			ruleTrial.showDialog();			return false;		}		return true;	},	// 切换展示的按钮	handleToggleBtn: function ( type ) {		if ( type === 0 ) {			$( '.check-btn' ).hide();			$( '.trial-btn' ).show();		} else {			$( '.check-btn' ).show();			$( '.trial-btn' ).hide();		}	},	showDialog: function () {		var htmlStr = (			'<p class="saveAskTxt">系统判断当前试算未选择通过或不通过，继续跳转吗？</p>' +			'<div class="modal-footer">' +			'<button type="button" class="btn btn-minor" style="border: 1px solid var(--colorNormal); line-height: 28px;" data-dismiss="modal">取消</button>' +			'<button type="button" class="btn btn-primary" onclick="ruleTrial.jumpPage()">跳转</button>' +			'</div>'		);		( new $.zui.ModalTrigger( { title: "提示", custom: htmlStr } ) ).show();	},	jumpPage: function () {		ruleTrial.data.jumpPage = true;		creCommon.loadHtml( ruleTrial.data.nextUrl );	},	$ajax: function ( url, method, body, callback, dataType ) {		if ( dataType === void 0 ) dataType = 'json';		var option = {			url: webpath + url,			type: method,			dataType: dataType,			success: function ( data ) {				callback( data );			},			error: function ( XMLHttpRequest, textStatus, errorThrown ) {				console.log( errorThrown );				if ( textStatus === 'timeout' ) {					console.log( '请求超时' );				}			}		};		if ( body ) option.data = body;		$.ajax( option );	},	getTime: function () {		return '#' + moment().format( 'YYYY/MM/DD Ah:mm:ss.SSS' ) + '#';	},	ruleTranslate: function ( rule ) {		if ( rule.includes( '1==1' ) ) {			return "";		} else {			var compareSel = ruleTrial.data.compareSel;			var ruleKeyMap = Object.keys( ruleTrial.data.ruleKeyMap );			for ( var i = compareSel.length - 1; i >= 0; i-- ) {				if ( rule.includes( compareSel[ i ].key ) ) {					var reg = compareSel[ i ].isRegExp ? new RegExp( compareSel[ i ].key, "g" ) : compareSel[ i ].reg;					rule = rule.replace( reg, compareSel[ i ].text );				}			}			for ( var j = 0; j < ruleKeyMap.length; j++ ) {				if ( rule.includes( ruleKeyMap[ j ] ) ) {					// var name = ruleTrial.data.ruleKeyMap[ ruleKeyMap[ j ] ];					// rule = rule.replace( ruleKeyMap[ j ], name );					var name = ruleTrial.data.ruleKeyMap[ ruleKeyMap[ j ] ];					var code = new RegExp( ruleKeyMap[ j ], "g" );					rule = rule.replace( code, name );				}			}			return rule;		}	},	// 值域语义化解释	kpiRangeTranslate: function ( kpiRangeStr ) {		if ( !kpiRangeStr ) {			return '';		}		var strLength = kpiRangeStr.length;		if ( (kpiRangeStr.indexOf('(') === 0 || kpiRangeStr.indexOf('[') === 0) &&			(kpiRangeStr.indexOf(')') === (strLength-1) || kpiRangeStr.indexOf(']') === (strLength-1)) ) {			var result = '';			var strArr = kpiRangeStr.split(',');			if ( ( strArr[0].indexOf('Infinity') !== -1 ) && ( strArr[1].indexOf('Infinity') !== -1 ) ) { // 无限制			} else if ( strArr[0].indexOf('Infinity') !== -1 ) { // 左侧无穷				var valueEndIndex = ( strArr[1].indexOf(')') !== -1 ) ? strArr[1].indexOf(')') : strArr[1].indexOf(']');				var value = strArr[1].slice(0, valueEndIndex);				var join = strArr[1].slice( valueEndIndex, strArr[1].length ).replace(')', '小于').replace(']', '小于等于');				result = join + $.trim( value );			} else if ( strArr[1].indexOf('Infinity') !== -1 ) { // 右侧无穷				var value = strArr[0].slice(1, strArr[0].length);				var join = ( strArr[0].indexOf('(') !== -1 ) ? '大于' : '大于等于';				result = join + $.trim( value );			} else {				var leftValue = strArr[0].slice(1, strArr[0].length);				var leftJoin = ( strArr[0].indexOf('(') !== -1 ) ? '大于' : '大于等于';				var rightIndex = ( strArr[1].indexOf(')') !== -1 ) ? strArr[1].indexOf(')') : strArr[1].indexOf(']');				var rightValue = strArr[1].slice(0, rightIndex);				var rightJoin = strArr[1].slice( rightIndex, strArr[1].length ).replace(')', '小于').replace(']', '小于等于');				result = leftJoin + $.trim( leftValue ) + ' 且 ' + rightJoin + $.trim( rightValue );			}			return result;		} else {			return 	kpiRangeStr;		}	},	// 模型参数、指标提示	modelParamsTipsAnalysis: function () {		if ( !ruleContent ) {			return;		}		var varTipsMap = {};		var kpiTipsMap = {};		var modelContentNodes = ruleContent.states;		Object.keys( modelContentNodes ).map( function ( value, index ) {			if( modelContentNodes[value].type === "task" ) { //筛选规则集节点				var ruleSetName = modelContentNodes[value].props.text.value; //规则集名称				var rulesArr = modelContentNodes[value].props.action.value; //规则集规则数组				// 遍历规则数组				for ( var r = 0, rLen = rulesArr.length; r < rLen; r++ ) {					var ruleName = rulesArr[r].actRuleName; //规则名称					var union = ( rulesArr[r].LHS.union === 'and' ) ? '且' : '或' ; //连接逻辑					var LHSTxt = rulesArr[r].LHSTxt; //规则条件文字数组					var RHSTxt = rulesArr[r].RHSTxt; //规则结果文字数组					var ruleConditions = rulesArr[r].LHS.condition; //规则的条件数组					// 1. 检查变量					var ruleCondition_containsFlag_var =  false; // 只要规则中有一处地方有该变量 则整条规则直接拿来用 结束遍历-继续寻找下一个参数					for ( var i = 0, iLen = variableList.length; i < iLen; i++ ) {						var varId = variableList[i].variableId;						// 遍历该规则的条件数组						for ( var c = 0, cLen = ruleConditions.length; c < cLen; c++ ) {							if ( ruleCondition_containsFlag_var ) {								break;							}							if( ruleConditions[c].indexOf( varId ) !== -1 ) { //找到了								ruleCondition_containsFlag_var = true;								var tip_v = {									ruleSetName: ruleSetName,									ruleSetIndex: index,									ruleName: ruleName,									ruleContent: ruleTrial.getRuleTxt( union, LHSTxt, RHSTxt )								};								if ( !varTipsMap[ varId ] ) {									varTipsMap[ varId ] = [ tip_v ];								} else {									varTipsMap[ varId ].push( tip_v );								}							}						}						ruleCondition_containsFlag_var = false; //重置该变量flag					}					// 2. 检查指标					var ruleCondition_containsFlag_kpi =  false;					for ( var j = 0, jLen = kpiMapList.length; j < jLen; j++ ) {						var kpiId = kpiMapList[j].kpiId;						// 遍历该规则的条件数组						for (var c_k = 0, c_kLen = ruleConditions.length; c_k < c_kLen; c_k++ ) {							if ( ruleCondition_containsFlag_kpi ) {								break;							}							if( ruleConditions[c_k].indexOf( kpiId ) !== -1 ) { //找到了								ruleCondition_containsFlag_kpi = true;								var tip_k = {									ruleSetName: ruleSetName,									ruleSetIndex: index,									ruleName: ruleName,									ruleContent: ruleTrial.getRuleTxt( union, LHSTxt, RHSTxt )								};								if ( !kpiTipsMap[ kpiId ] ) {									kpiTipsMap[ kpiId ] = [ tip_k ];								} else {									kpiTipsMap[ kpiId ].push( tip_k );								}							}						}						ruleCondition_containsFlag_kpi = false; //重置该变量flag					}				}			}		} );		ruleTrial.data.varTipsMap = varTipsMap;		ruleTrial.data.kpiTipsMap = kpiTipsMap;	},	getRuleTxt: function ( union, LHSTxt, RHSTxt ) {		return LHSTxt.join( union ).replace( /\s+/g, '' ) + ' 则 ' +  RHSTxt.join( ' ' ).replace( /\s+/g, '' );	},	// 参数/指标规则提示	showTips: function ( $cur ) {		var tips = $cur.find( '.ruleTrial_tipBox' );		var tips_tri = $cur.find( '.ruleTrial_tipBox_leftTriangle' );		var show = !$( tips ).hasClass( 'hide' );		if ( show ) {			$( tips ).addClass( 'hide' );			$( tips_tri ).addClass( 'hide' );		} else {			$( tips ).removeClass( 'hide' );			$( tips_tri ).removeClass( 'hide' );		}	},};$( function () {	ruleTrial.init();} );