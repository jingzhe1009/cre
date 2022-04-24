var main = {
	// 初始化状态数据
	data: {
		mechanism: [],
		RuleExecution: {
			day: 15,
			chart: null
		},
		NumberOfCallsLeft: {
			day: 7,
			chart: null
		},
		NumberOfCallsRight: {
			day: 7,
			deptIdIndex: 0,
			chart: null
		}
	},
	// 初始化数据
	initData: function () {
		// 获取模型执行情况
		main.getModelStatisticsData();
		// 获取top3
		main.getTopThree();
		// 获取模型汇总
		main.getModelSummary();
		// 获取模型启用
		main.getModelEnable();
		// 获取离线任务
		main.getOfflineTask();
		// 获取模型执行场景
		main.getScenes();
		// 获取机构
		main.getMechanism();
		// 获取模型执行
		main.getRuleExecution();
		// 获取调用次数
		main.getNumberOfCallsLeft();
		// 获取调用次数
		main.getNumberOfCallsRight();
		// 图表宽度自适应监听
		main.resize();
	},

	getModelStatisticsData: function () {
		main.$ajax( '/analysis/allModelExecuteStatis', 'GET', null, function ( data ) {
			var count = data.data.statesCount;
			$( '.successNum' ).html( main.tranNumber( count.successCount ? count.successCount : 0 ) );
			$( '.successNum' ).attr( 'title', count.successCount ? count.successCount : 0 );
			$( '.failNum' ).html( main.tranNumber( count.falseCount ? count.falseCount : 0 ) );
			$( '.failNum' ).attr( 'title', count.falseCount ? count.falseCount : 0 );
			$( '.diaoyongNum' ).html( main.tranNumber( count.allCount ? count.allCount : 0 ) );
			$( '.diaoyongNum' ).attr( 'title', count.allCount ? count.allCount : 0 );
		} );
	},

	getTopThree: function () {
		main.$ajax( '/analysis/allModelExecuteTopThree', 'GET', null, function ( data ) {
			var models = data.data.data;
			var html = "";
			var empty = {
				count: 0,
				folderName: '暂无场景',
				moduleName: '暂无模型'
			};
			for ( var i = 0; i < 3; i++ ) {
				if ( models.length <= i ) models.push( empty );
				if ( i !== 0 ) html += '<hr class="splitLabel"/>';
				html += (
					'<span style="display: flex;flex-wrap: wrap;flex-shrink: 1">' +
					'<div style="display: flex;align-items: baseline;margin-right: 5px">' +
					'<span class="topNum" title="' + models[ i ].count + '">' + main.tranNumber( models[ i ].count ) + '</span>次' +
					'</div>' +
					'<div style="display: flex; flex-direction: column;padding-top: 2px;">' +
					'<div class="topTitle" title="' + models[ i ].folderName + '">' + models[ i ].folderName + '</div>' +
					'<div class="topTitle" title="' + models[ i ].moduleName + '">' + models[ i ].moduleName + '</div>' +
					'</div>' +
					'</span>'
				);
			}
			$( '.top3 .numBox' ).html( html );
		} );
	},

	getModelSummary: function () {
		main.$ajax( '/analysis/allModelStatis', 'GET', null, function ( data ) {
			var model = data.data;
			var html = "";
			for ( var i = 0; i < model.length; i++ ) {
				if ( model[ i ].ruleType === 'ruleModel' || model[ i ].ruleType === 'gradeModel' ) {
					var name = model[ i ].ruleType === 'ruleModel' ? '规则模型' : '评分模型';
					html += (
						'<li>' +
						'<span class="model">' + name +
						'<span class="modelNum">' + model[ i ].modelCount + '</span>' +
						'</span>' +
						'<span class="edition">版本' +
						'<span class="editionNum">' + model[ i ].modelVersionCount + '</span>' +
						'</span>' +
						'</li>'
					);
				}
			}
			$( '.modelSummary ul' ).html( html );
		} );
	},

	getModelEnable: function () {
		main.$ajax( '/analysis/allModelStatusStatis', 'GET', null, function ( data ) {
			var model = data.data;
			var html = "<li>";
			for ( var i = 0; i < model.length; i++ ) {
				var name = model[ i ].ruleStatus === 'enable' ? '已启用' : '未启用';
				html += (
					'<span class="model">' + name +
					'<span class="modelNum">' + model[ i ].modelCount + '</span>' +
					'</span>'
				);
			}
			html += '</li>';
			$( '.modelEnable ul' ).html( html );
		} );
	},

	getOfflineTask: function () {
		main.$ajax( '/analysis/taskStatusStatis', 'GET', null, function ( data ) {
			var model = data.data;
			var modelObj = {};
			for ( var i = 0; i < model.length; i++ ) {
				modelObj[ model[ i ].status ] = model[ i ].count;
			}
			var html = (
				'<li>' +
				'<span class="model">已启用' +
				'<span class="modelNum">' + ( modelObj.enable === void 0 ? 0 : modelObj.enable ) + '</span>' +
				'</span>' +
				'<span class="edition">正在执行' +
				'<span class="editionNum">' + ( modelObj.execute === void 0 ? 0 : modelObj.execute ) + '</span>' +
				'</span>' +
				'</li>' +
				'<li>' +
				'<span class="model">已完成' +
				'<span class="modelNum">' + ( modelObj.success === void 0 ? 0 : modelObj.success ) + '</span>' +
				'</span>' +
				'<span class="edition">异常' +
				'<span class="editionNum">' + ( modelObj.exception === void 0 ? 0 : modelObj.exception ) + '</span>' +
				'</span>' +
				'</li>'
			);
			$( '.offlineTask ul' ).html( html );
		} );
	},

	getScenes: function () {
		main.$ajax( '/ruleFolderList', 'GET', null, function ( data ) {
			var html = '<option value="">全部场景</option>';
			for ( var i = 0; i < data.length; i++ ) {
				html += '<option value="' + data[ i ].key + '">' + data[ i ].text + '</option>';
			}
			$( '.scenes' ).html( html );
		} );
	},

	getMechanism: function () {
		main.$ajax( '/dept/nameList', 'GET', null, function ( data ) {
			main.data.mechanism = data;
			var html = '<option value="">全部机构</option>';
			for ( var i = 0; i < data.length; i++ ) {
				html += '<option value="' + data[ i ].DEPT_ID + '">' + data[ i ].DEPT_NAME + '</option>';
			}
			$( '.mechanism' ).html( html );
		} );
	},

	getRuleExecution: function () {
		var path = '/analysis/ruleExecuteNumByFolderIdDeptId';
		var param = main.data.RuleExecution;
		path += '?day=' + param.day;
		if ( param.folderId !== void 0 && param.folderId !== '' ) path += '&folderId=' + param.folderId;
		if ( param.deptId !== void 0 && param.deptId !== '' ) path += '&deptId=' + param.deptId;
		main.$ajax( path, 'GET', null, function ( data ) {
			main.data.RuleExecution.chart = echarts.init( document.getElementById( 'ruleExecution' ) );
			main.data.RuleExecution.chart.setOption( {
				title: {
					text: '模型执行',
					x: 'left',
					textStyle: {
						color: '#333',
						fontSize: 14,
						fontWeight: 'bold'
					},
					padding: 8,
					backgroundColor: new echarts.graphic.LinearGradient( 0, 0, 1, 0, [ {
						offset: 0,
						color: '#fbfdfd'
					}, {
						offset: 1,
						color: '#eefafb'
					} ] ),
					borderRadius: 10
				},
				grid: {
					width: '95%',
					left: '50px',
					height: '60%'
				},
				tooltip: {
					trigger: 'axis',
					axisPointer: {
						type: 'cross',
						crossStyle: {
							color: '#999'
						}
					},
					padding: 15,
					textStyle: {
						color: '#000',
						fontSize: 12
					},
					backgroundColor: '#fff',
					borderColor: '#ccc'

				},
				legend: {
					left: '50%',
					top: 5,
					data: [ '总次数', '成功', '失败', ],
					orient: 'horizontal',
					icon: "circle",
					itemHeight: 9,
					textStyle: {
						color: '#999',
					}
				},
				xAxis: [
					{
						type: 'category',
						data: main.getDateArray( param.day ),
						axisPointer: {
							type: 'shadow'
						},
						axisLine: {
							lineStyle: {
								color: '#F3F3F3',
							}
						},
						axisLabel: {
							color: '#000',
						},
					}
				],
				yAxis: [
					{
						type: 'value',
						name: '',
						axisLine: {
							lineStyle: {
								color: '#F3F3F3'
							}
						},
						splitLine: {
							lineStyle: {
								color: '#F3F3F3'
							}
						},
						splitArea: {
							show: true,
							areaStyle: {
								color: [ '#fff', '#f6f8fb' ]
							}
						},
						axisLabel: {
							color: '#000',
							formatter: function ( value ) {
								return main.tranNumber( value, false );
							}
						},
						nameTextStyle: {
							color: '#C2C3C6'
						}
					}
				],
				series: [
					{
						name: '成功',
						type: 'line',
						smooth: true,
						color: '#3965ff',
						lineStyle: {
							type: 'solid',
						},
						itemStyle: {
							emphasis: {
								color: '#3965ff',
								borderColor: '#3965ff'
							}
						},
						data: data.successData
					},
					{
						name: '失败',
						type: 'line',
						smooth: true,
						color: '#eb7d26',
						lineStyle: {
							type: 'solid',
						},
						itemStyle: {
							emphasis: {
								color: '#eb7d26',
								borderColor: '#eb7d26'
							}
						},
//					            yAxisIndex: 1,
						data: data.failData
					},
					{
						name: '总次数',
						type: 'line',
						smooth: true,
						color: '#7e38ff',
						lineStyle: {
							type: 'solid',
						},
						itemStyle: {
							emphasis: {
								color: '#7e38ff',
								borderColor: '#7e38ff'
							}
						},
						data: data.allData
					}
				]
			} );
		} );
	},

	getNumberOfCallsLeft: function () {
		var path = '/analysis/modelExecuteHitRateByDays';
		var param = main.data.NumberOfCallsLeft;
		path += '?day=' + param.day;
		if ( param.folderId !== void 0 && param.folderId !== '' ) path += '&folderId=' + param.folderId;
		main.$ajax( path, 'GET', null, function ( data ) {
			var arr = data.data;
			var chartData = {
				hitRuleNum: [],
				notHitRuleNum: [],
				allRuleNum: []
			};
			for ( var i = 0; i < arr.length; i++ ) {
				chartData.hitRuleNum.push( arr[ i ].hitRuleNum === void 0 ? 0 : arr[ i ].hitRuleNum );
				chartData.notHitRuleNum.push( arr[ i ].notHitRuleNum === void 0 ? 0 : arr[ i ].notHitRuleNum );
				chartData.allRuleNum.push( arr[ i ].allRuleNum === void 0 ? 0 : arr[ i ].allRuleNum );
			}
			main.data.NumberOfCallsLeft.chart = echarts.init( document.getElementById( 'numberOfCallsLeft' ) );
			main.data.NumberOfCallsLeft.chart.setOption( {
				title: {
					text: '模型执行命中率',
					x: 'left',
					textStyle: {
						color: '#333',
						fontSize: 16,
						fontWeight: 'bold'
					},
					padding: 8,
					backgroundColor: new echarts.graphic.LinearGradient( 0, 0, 1, 0, [ {
						offset: 0,
						color: '#fbfdfd'
					}, {
						offset: 1,
						color: '#eefafb'
					} ] ),
					borderRadius: 10
				},
				tooltip: {
					trigger: 'item',
					formatter: function ( params ) {
						var count = chartData.allRuleNum[ params.dataIndex ];
						var rate = count === 0 ? 0 : Math.round( params.value * 10000 / count ) / 100;
						return params.seriesName + " <br/>" + params.name + " : " + params.value + " (" + rate + "%)";
					}
				},
				legend: {
					orient: 'horizontal',
					left: 'center',
					top: 'bottom',
					itemWidth: 10,
					itemHeight: 10,
					itemGap: 40,
					data: [ '命中', '未命中' ]
				},
				grid: {
					width: '91%',
					left: '50px'
				},
				xAxis: [
					{
						type: 'category',
						data: main.getDateArray( param.day ),
						axisPointer: {
							type: 'shadow'
						},
						axisLine: {
							lineStyle: {
								color: '#F3F3F3',
							}
						},
						axisLabel: {
							color: '#000',
						},
					}
				],
				yAxis: [
					{
						type: 'value',
						name: '',
						axisLine: {
							lineStyle: {
								color: '#F3F3F3'
							}
						},
						splitLine: {
							lineStyle: {
								color: '#F3F3F3'
							}
						},
						splitArea: {
							show: true,
							areaStyle: {
								color: [ '#fff', '#f6f8fb' ]
							}
						},
						axisLabel: {
							color: '#000',
							formatter: function ( value ) {
								return main.tranNumber( value, false );
							}
						},
						nameTextStyle: {
							color: '#C2C3C6'
						}
					}
				],
				series: [
					{
						name: '命中',
						type: 'bar',
						data: chartData.hitRuleNum,
						label: {
							show: true,
							position: 'top'
						},
						itemStyle: {
							barBorderRadius: [ 15, 15, 0, 0 ],
							color: new echarts.graphic.LinearGradient( 0, 0, 0, 1, [ {
								offset: 0,
								color: '#6277d6'
							}, {
								offset: 1,
								color: '#e3e7f6'
							} ] )
						},
						barWidth: '20'
					},
					{
						name: '未命中',
						type: 'bar',
						data: chartData.notHitRuleNum,
						label: {
							show: true,
							position: 'top'
						},
						itemStyle: {
							barBorderRadius: [ 15, 15, 0, 0 ],
							color: new echarts.graphic.LinearGradient( 0, 0, 0, 1, [ {
								offset: 0,
								color: '#eb8524'
							}, {
								offset: 1,
								color: '#f6f1e5'
							} ] )
						},
						barWidth: '20'
					}
				]
			} );
		} );
	},

	getNumberOfCallsRight: function () {
		var path = '/analysis/ruleExecuteNumByFolderIdDeptId';
		var param = main.data.NumberOfCallsRight;
		path += '?day=' + param.day;
		if ( param.folderId !== void 0 && param.folderId !== '' ) path += '&folderId=' + param.folderId;
		if ( param.deptId !== void 0 && param.deptId !== '' ) path += '&deptId=' + param.deptId;
		var deptIdIndex = main.data.NumberOfCallsRight.deptIdIndex;
		main.$ajax( path, 'GET', null, function ( data ) {
			main.data.NumberOfCallsRight.chart = echarts.init( document.getElementById( 'numberOfCallsRight' ) );
			main.data.NumberOfCallsRight.chart.setOption( {
				title: {
					text: '调用次数',
					x: 'left',
					textStyle: {
						color: '#333',
						fontSize: 16,
						fontWeight: 'bold'
					},
					padding: 8,
					backgroundColor: new echarts.graphic.LinearGradient( 0, 0, 1, 0, [ {
						offset: 0,
						color: '#fbfdfd'
					}, {
						offset: 1,
						color: '#eefafb'
					} ] ),
					borderRadius: 10
				},
				tooltip: {
					trigger: 'item',
					formatter: "{a} <br/>{b} : {c}"
				},
				grid: {
					width: '91%',
					left: '50px'
				},
				legend: {
					orient: 'horizontal',
					left: 'center',
					top: 'bottom',
					itemWidth: 10,
					itemHeight: 10,
					itemGap: 40,
					data: [ deptIdIndex === 0 ? '全部机构' : main.data.mechanism[ main.data.NumberOfCallsRight.deptIdIndex - 1 ].DEPT_NAME ]
				},
				xAxis: [
					{
						type: 'category',
						data: main.getDateArray( param.day ),
						axisPointer: {
							type: 'shadow'
						},
						axisLine: {
							lineStyle: {
								color: '#F3F3F3',
							}
						},
						axisLabel: {
							color: '#000',
						},
					}
				],
				yAxis: [
					{
						type: 'value',
						name: '',
						axisLine: {
							lineStyle: {
								color: '#F3F3F3'
							}
						},
						splitLine: {
							lineStyle: {
								color: '#F3F3F3'
							}
						},
						splitArea: {
							show: true,
							areaStyle: {
								color: [ '#fff', '#f6f8fb' ]
							}
						},
						axisLabel: {
							color: '#000',
							formatter: function ( value ) {
								return main.tranNumber( value, false );
							}
						},
						nameTextStyle: {
							color: '#C2C3C6'
						}
					}
				],
				series: [
					{
						name: deptIdIndex === 0 ? '全部机构' : main.data.mechanism[ main.data.NumberOfCallsRight.deptIdIndex - 1 ].DEPT_NAME,
						type: 'bar',
						data: data.allData,
						label: {
							show: true,
							position: 'top'
						},
						itemStyle: {
							barBorderRadius: [ 15, 15, 0, 0 ],
							color: new echarts.graphic.LinearGradient( 0, 0, 0, 1, [ {
								offset: 0,
								color: '#6277d6'
							}, {
								offset: 1,
								color: '#e3e7f6'
							} ] )
						},
						barWidth: '20'
					}
				]
			} );
		} );
	},

	$ajax: function ( url, method, body, callback, dataType ) {
		if ( dataType === void 0 ) dataType = 'json';
		$.ajax( {
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
		} );
	},

	tranNumber: function ( num, html, point ) {
		if ( point === void 0 ) point = 2;
		if ( html === void 0 ) html = true;
		var numStr = num.toString();
		var decimal = "";
		// 万以内直接返回
		if ( numStr.length < 5 ) {
			return numStr;
		}
		//大于8位数是亿
		else if ( numStr.length > 8 ) {
			decimal = numStr.substring( numStr.length - 8, numStr.length - 8 + point );
			return parseFloat( parseInt( num / 100000000 ) + '.' + decimal ) + ( html ? '<span class="unit">亿</span>' : '亿' );
		}
		//大于4位数是万 (以1W分割 1W以下全部显示)
		else if ( numStr.length > 4 ) {
			decimal = numStr.substring( numStr.length - 4, numStr.length - 4 + point );
			return parseFloat( parseInt( num / 10000 ) + '.' + decimal ) + ( html ? '<span class="unit">万</span>' : '万' );
		}
	},

	getDateArray: function ( num ) {
		var today = moment();
		var startTime = today.subtract( num, 'days' );
		var days = [];
		for ( var i = 0; i < num; i++ ) {
			days.push( startTime.add( 1, 'days' ).format( 'MM-DD' ) );
		}
		return days;
	},

	selectChange: function ( space, name, value, index ) {
		main.data[ space ][ name ] = value;
		main.data[ space ][ name + 'Index' ] = index;
		main[ 'get' + space ]();
	},

	resize: function () {
		$( '#pageContent' ).resize( function () {
			var ruleExecution = main.data.RuleExecution.chart;
			var numberOfCallsLeft = main.data.NumberOfCallsLeft.chart;
			var numberOfCallsRight = main.data.NumberOfCallsRight.chart;
			ruleExecution && ruleExecution.resize();
			numberOfCallsLeft && numberOfCallsLeft.resize();
			numberOfCallsRight && numberOfCallsRight.resize();
		} );
	}
};

$( function () {
	main.initData();
} );