var main = {
	// 初始化状态数据
	data: {
		mechanism: [],
		RuleExecution: {
			chart: null
		},
		NumberOfCallsLeft: {
			chart: null
		},
		NumberOfCallsRight: {
			chart: null
		},
		RuleHit:{
			chart: null
		},
		ScoreHit:{
			chart: null
		}
		
	},
	// 初始化数据
	initData: function () {
		
		
	},

	getModelStatisticsData: function () {},

	getTopThree: function () {
		
	},

	getModelSummary: function () {
		
	},

	getModelEnable: function () {
		
	},

	getOfflineTask: function () {
		
	},

	getScenes: function () {},

	getMechanism: function () {},

    //调用分析-模型执行情况
	getRuleExecution: function () {
		main.$ajax('/monitor/excuteState', 'POST', null, function ( data ) {
			//console.log(data);
			main.data.RuleExecution.chart = echarts.init( document.getElementById( 'ruleExecution' ) );
			main.data.RuleExecution.chart.setOption( {
				title: {
					text: '模型执行'
				},
				tooltip: {
					trigger: 'axis'

				},
				legend: {
					data: [ '总次数', '成功', '失败', ]
				},
				grid: {
				    left: '3%',
				    right: '4%',
				    bottom: '3%',
				    containLabel: true
			    },
				xAxis: [
					{
						type: 'category',
						data: data.titleData,
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
	//调用分析-响应时间
	getNumberOfCallsLeft: function () {
		var path = '/monitor/responseTime';
		main.$ajax( path, 'POST', null, function ( data ) {
			main.data.NumberOfCallsLeft.chart = echarts.init( document.getElementById( 'numberOfCallsLeft' ) );
			main.data.NumberOfCallsLeft.chart.setOption( {
				title: {
					text: '模型执行命中率'
				},
				tooltip: {
					trigger: 'axis'
				},
				legend: {
					data: [ '响应时间']
				},
				grid: {
				    left: '3%',
				    right: '4%',
				    bottom: '3%',
				    containLabel: true
			    },
				xAxis: [
					{
						type: 'category',
						boundaryGap: false,
						data: data.titleData,
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
					},
				],
				series: [
					{
						name: '响应时间',
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
						data: data.responseData
					}
				]
			} );
		} );
	},
	//调用分析-执行统计
	getNumberOfCallsRight: function () {
		var path = '/monitor/excuteCount';
		main.$ajax( path, 'POST', null, function ( data ) {
			//debugger;
			var chrNumber =[];
	        var chrnum=[];
	        var title =data.titleList;
	        var pie = data.pieList;
	        for (var i in title){//通过ajax获取后台数据，再将传入数组
	            chrNumber.push(pie[i]);
	            chrnum[i]={value:pie[i],name:title[i]};
		    }
			main.data.NumberOfCallsRight.chart = echarts.init( document.getElementById( 'numberOfCallsRight' ) );
			main.data.NumberOfCallsRight.chart.setOption( {
				title: {
				    text: '执行统计',
				    left: 'center'
				  },
				  tooltip: {
				    trigger: 'item'
				  },
				legend: {
					orient: 'vertical',
					left: 'left',
					data: data.titleList
				},
				series: [
					{
						name: '执行统计',
						type: 'pie',
						data: chrnum,
						radius: '50%',
						label: {
							show: true,
							position: 'top'
						},
						emphasis: {
					        itemStyle: {
					          shadowBlur: 10,
					          shadowOffsetX: 0,
					          shadowColor: 'rgba(0, 0, 0, 0.5)'
					        }
				       }
					}
				]
			} );
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

	getArray: function (data) {
		var months = [];
		for ( var i in data) {
			months.push(data[i]);
		}
		return months;
	},

	selectChange: function ( space, name, value, index ) {},

	resize: function () {},
	//调用分析-执行结果
	initTable: function(obj){
		$('#monitorTable').width('100%').dataTable({
	    	//默认搜索组件
	        "searching": false,
	        //排序功能
	        "ordering": false,
	        "destroy": true,
	        // 自动列宽
	        "autoWidth": false,
	        //滚动条
	        "scrollX":false,
	        //控制每页显示条数
	        "lengthChange": false,
	        //翻页功能
	        "paging": false,
	        //控制总数显示
	        "info": false,
	        //列表的过滤,搜索和排序信息会传递到Server端进行处理
	        "serverSide": false,
	        "columns": [
				{"title": "执行结果", "data": "excuteResult","render":function(data,type,row){
					return data;
	            }},
	            {"title": "状态码", "data": "statusCode","render":function(data,type,row){
					return data;
	            }},
	            {"title": "执行次数", "data": "excuteTimes","render":function(data,type,row){
					return data;
	            }},
	            {"title": "查询", "data": "","render":function(data,type,row){
					return '<button class="btn btn-primary">查询</button>';
	            }}
	            ],
	        ajax: {
	            url: webpath + '/monitor/excuteResult',
	            "type": 'GET',
	            "data": function (d) { // 查询参数
	            	//debugger;
	                return $.extend({}, d, obj);
	            }
	        },
	        "fnDrawCallback": function (oSettings, json) {
	            
	        }
	    });
	},
	//模型分析-规则类模型命中率
	getRuleHit : function(){
		main.$ajax('/monitor/ruleHit', 'POST', null, function ( data ) {
			main.data.RuleHit.chart = echarts.init( document.getElementById( 'ruleHit' ) );
			main.data.RuleHit.chart.setOption( {
				title: {
					text: '规则类模型命中率',
					left: 'left'
				},
				grid: {
				    left: '3%',
				    right: '4%',
				    top: '14%',
				    bottom: '3%',
				    containLabel: true
				  },
				tooltip: {
					trigger: 'axis',
				    axisPointer: {
				      type: 'shadow'
				    }

				},
				legend: {},
				xAxis: [
					{
						type: 'category',
						data: data.titleData
						
					}
				],
				yAxis: [
					{
						type: 'value'
						
					}
				],
				series: [
					
				    {
				      name: '通过',
				      type: 'bar',
				      stack: 'Ad',
				      emphasis: {
				        focus: 'series'
				      },
				      data: data.successData
				    },
				    {
				      name: '拒绝',
				      type: 'bar',
				      stack: 'Ad',
				      emphasis: {
				        focus: 'series'
				      },
				      data: data.failData
				    }
				]
			} );
		} );
	},
	//模型分析-评分类模型命中率
	getScoreHit : function(){
		main.$ajax('/monitor/scoreHit', 'POST', null, function ( data ) {
			main.data.ScoreHit.chart = echarts.init( document.getElementById( 'scoreHit' ) );
			main.data.ScoreHit.chart.setOption( {
				title: {
					text: '评分类模型命中率'
				},
				grid: {
				    left: '3%',
				    right: '4%',
				    bottom: '3%',
				    containLabel: true
				  },
				tooltip: {
					trigger: 'axis',
				    axisPointer: {
				      type: 'shadow'
				    }

				},
				legend: {},
				xAxis: [
					{
						type: 'category',
						data: data.titleData
					}
				],
				yAxis: [
					{
						type: 'value'
						
					}
				],
				series: [
					
				    {
				      name: '通过',
				      type: 'bar',
				      emphasis: {
				        focus: 'series'
				      },
				      data: data.scoreData
				    }
				]
			} );
		} );
	},
	//日志检索
	logTable: function(obj){
		$('#logTable').width('100%').dataTable({
	    	//默认搜索组件
	        "searching": false,
	        //排序功能
	        "ordering": false,
	        "destroy": true,
	        // 自动列宽
	        "autoWidth": false,
	        //滚动条
	        "scrollX":false,
	        //控制每页显示条数
	        "lengthChange": false,
	        "pagingType": "full_numbers",
	        //翻页功能
	        "paging": true,
	        //控制总数显示
	        "info": true,
	        //列表的过滤,搜索和排序信息会传递到Server端进行处理
	        "serverSide": false,
	        "pageLength": 100,
	        "columns": [
				{"title": "分行", "data": "excuteResult","render":function(data,type,row){
					return data;
	            }},
	            {"title": "渠道", "data": "statusCode","render":function(data,type,row){
					return data;
	            }},
	            {"title": "项目", "data": "excuteTimes","render":function(data,type,row){
					return data;
	            }},
	            {"title": "模型", "data": "excuteTimes","render":function(data,type,row){
					return data;
	            }},
	            {"title": "调用方式", "data": "excuteTimes","render":function(data,type,row){
					return data;
	            }},
	            {"title": "流水号", "data": "excuteTimes","render":function(data,type,row){
					return data;
	            }},
	            {"title": "执行状态", "data": "excuteTimes","render":function(data,type,row){
					return data;
	            }},
	            {"title": "执行时间", "data": "excuteTimes","render":function(data,type,row){
					return data;
	            }},
	            {"title": "响应时间", "data": "excuteTimes","render":function(data,type,row){
					return data;
	            }},
	            {"title": "详情", "data": "","render":function(data,type,row){
					return '<button class="btn btn-primary" onclick="main.getDesc()">详情</button>';
	            }}
	            ],
	        ajax: {
	            url: webpath + '/monitor/excuteResult',
	            "type": 'GET',
	            "data": function (d) { // 查询参数
	            	//debugger;
	                return $.extend({}, d, obj);
	            }
	        },
	        "fnDrawCallback": function (oSettings, json) {
	            
	        }
	    });
	},
	//日志检索详情  和  模型分析/调用分析的数据导出
	getDesc : function(flag){
		debugger;
		var url = '/monitor/desc'
		var param ={};
		param.cycleId=localStorage.getItem("cycleId");
		var json = JSON.stringify(param);
		$.ajax( {
			url: webpath + url,
			type: 'post',
			data:json,
			contentType:"application/json;charset=UTF-8",
			//dataType: dataType,
			success: function ( data ) {
				debugger;
				$("#monitorTab .tab12").hide();
				$("#monitorTab .tab3").hide();
				var tabId = localStorage.getItem("tabId");
				if(tabId=='3'){
					var desc = data.desc;
					var html ='<table class="table table-striped"  style="width:60%" align="center">';
					html +='<thead><tr><th colspan=4 >模型执行情况</th></tr>';
					html +='<tbody><tr><td>归属行ID</td><td>123'+desc.bankId+'</td><td>调用渠道ID</td><td>OPB001'+desc.bankId+'</td></tr>';
					html +='<tr><td>归属行名称</td><td>深圳'+desc.bankId+'</td><td>调用渠道名称</td><td>深圳分行大数据'+desc.bankId+'</td></tr>';
					html +='<tr><td>模型id</td><td>xxx'+desc.bankId+'</td><td>产品id</td><td>01'+desc.bankId+'</td></tr>';
					html +='<tr><td>模型名称</td><td>准入模型'+desc.bankId+'</td><td>产品名称</td><td>银税贷'+desc.bankId+'</td></tr>';
					html +='<tr><td>调用方式</td><td>实时'+desc.bankId+'</td><td>交易流水号</td><td>00022'+desc.bankId+'</td></tr>';
					html +='<tr><td>模型版本号</td><td>1.0'+desc.bankId+'</td><td>执行时间</td><td>2022-2-2 11:22:33'+desc.bankId+'</td></tr>';
					html +='<tr><td>执行状态</td><td>成功'+desc.bankId+'</td><td>响应时间</td><td>3s'+desc.bankId+'</td></tr></tbody>';
					html +='<thead><tr><th colspan=4 >外部数据库执行情况执行情况</th></tr>';
					html +='<tbody><tr><td>oracle入参</td><td>成功'+desc.bankId+'</td><td>oracle出参</td><td>oracle出参'+desc.bankId+'</td></tr>';
					html +='<tr><td>hbase入参</td><td>成功'+desc.bankId+'</td><td>hbase出参</td><td>hbase出参'+desc.bankId+'</td></tr></tbody>';
					html +='<thead><tr><th colspan=4 >红色执行情况</th></tr>';
					html +='<tbody><tr><td>红色接口名称</td><td>成功'+desc.bankId+'</td><td></td><td></td></tr>';
					html +='<tr><td>接口入参</td><td>'+desc.bankId+'</td><td>接口出参</td><td>'+desc.bankId+'</td></tr></tbody>';
					$("#desc").html(html);
					$(".tab-content").hide();
					$("#desc").attr("style","display:block");
				}else{
					if($("#exportButton").val()=="数据导出"){
						console.log(data);
						var title = data.titleData;
						var all = data.allData;
						var suc = data.successData;
						var fail = data.failData;
						
						var response = data.responseData;
						var score = data.scoreData;
						var ruleSuc = data.ruleHitSucData;
						var ruleFail = data.ruleHitFailData;
						
						var html ='<table class="table table-striped"  style="width:90%" align="center"><thead><tr>';
						html +="<th>数据统计</th>"
						for(var i in title){
							if(i=="remove"||i=="removeVal")
								continue;
							var index = title[i];
							html +="<th>"+title[i]+"</th>";
						}
						html +="</tr></thead>";
						
						html +="<tbody><tr><td>总执行次数</td>"
						for(var i in all){
							if(i=="remove"||i=="removeVal")
								continue;
							var index = all[i];
							//if(index.substring(0,1)=="f"){
							//	continue;
							//}
							html +="<td>"+all[i]+"</td>";
						}
						
						html +="</tr><tr><td>执行成功次数</td>"
						for(var i in suc){
							if(i=="remove"||i=="removeVal")
								continue;
							var index = suc[i];
							//if(index.substring(0,1)=="f"){
							//	continue;
							//}
							html +="<td>"+suc[i]+"</td>";
						}
						
						html +="</tr><tr><td>执行失败次数</td>"
						for(var i in fail){
							if(i=="remove"||i=="removeVal")
								continue;
							var index = fail[i];
							//if(index.substring(0,1)=="f"){
							//	continue;
							//}
							html +="<td>"+fail[i]+"</td>";
						}
						
						
						html +="</tr><tr><td>响应时间</td>"
						for(var i in response){
							if(i=="remove"||i=="removeVal")
								continue;
							var index = response[i];
							html +="<td>"+response[i]+"</td>";
						}
						
						//模型分析需显示
						if(tabId=='2'){
							html +="</tr><tr><td>评分通过命中率</td>"
							for(var i in score){
								if(i=="remove"||i=="removeVal")
									continue;
								var index = score[i];
								html +="<td>"+score[i]+"</td>";
							}
							
							
							html +="</tr><tr><td>规则通过命中率</td>"
							for(var i in ruleSuc){
								if(i=="remove"||i=="removeVal")
									continue;
								var index = ruleSuc[i];
								html +="<td>"+ruleSuc[i]+"</td>";
							}
							
							
							html +="</tr><tr><td>规则失败命中率</td>"
							for(var i in ruleFail){
								if(i=="remove"||i=="removeVal")
									continue;
								var index = ruleFail[i];
								html +="<td>"+ruleFail[i]+"</td>";
							}
						}
						
						
						html +="</tr><tbody></table>";
						$("#desc").html(html);
						
						$(".tab-content").hide();
						$("#exportButton").val("图表展示");
						$("#desc").attr("style","display:block");
					}else{
						//模型分析需显示
						if(tabId=='1'){
							$("#tab1").show();
						}else if(tabId=='2'){
							$("#tab2").show();
						}
						$("#exportButton").val("数据导出");
						$("#desc").attr("style","display:none");
					}
				}
				
			},
			error: function ( XMLHttpRequest, textStatus, errorThrown ) {
				console.log( errorThrown );
			}
		});
	},
	$ajax: function ( url, method, body, callback, dataType ) {
		if ( dataType === void 0 ) dataType = 'json';
		var param ={};
		param.cycleId=localStorage.getItem("cycleId");
		var json = JSON.stringify(param);
		$.ajax( {
			url: webpath + url,
			type: method,
			data:json,
			contentType:"application/json;charset=UTF-8",
			//dataType: dataType,
			success: function ( data ) {
				callback( data );
			},
			error: function ( XMLHttpRequest, textStatus, errorThrown ) {
				console.log( errorThrown );
				if ( textStatus === 'timeout' ) {
					console.log( '请求超时' );
				}
			}
		});
	},
	//切换标签
	changeTab : function(tabId){
		if(tabId==undefined)
			return;
		debugger;
		localStorage.setItem("tabId",tabId);
		var cycleId = localStorage.getItem("cycleId");
		$(".tab-content").hide();
		$("#desc").attr("style","display:none");
		//标签页选中样式
	    $('#monitorTab li').removeClass('active');
		$(".nav-primary li[cyc-id='" + cycleId + "']").addClass('active');
		$(".nav-tabs li[tab-id='" + tabId + "']").addClass('active');
		if(tabId=='1'){
			$("#tab1").show();
			$("#monitorTab .tab12").show();
			$("#monitorTab .tab3").hide();
			// 获取模型执行
			main.getRuleExecution();
			// 响应时间
			main.getNumberOfCallsLeft();
			//执行统计
			main.getNumberOfCallsRight();
			//执行结果
			var obj={};
			main.initTable(obj);
		}else if(tabId=='2'){
			$("#tab2").show();
			$("#monitorTab .tab12").show();
			$("#monitorTab .tab3").hide();
			//规则模型命中率
			main.getRuleHit();
			//评分模型命中率
			main.getScoreHit();
		}else if(tabId=='3'){
			$("#tab3").show();
			$("#monitorTab .tab12").hide();
			$("#monitorTab .tab3").show();
			//日志检索
			var obj={};
			main.logTable(obj);
		}
	},
	//切换年月日标签
	changeCycle:function(cycleId){
		var tabId = localStorage.getItem("tabId");
		localStorage.setItem("cycleId",cycleId);
		$('#monitorTab li').removeClass('active');
		$(".nav-primary  li[cyc-id='" + cycleId + "']").addClass('active');
		$(".nav-tabs li[tab-id='" + tabId + "']").addClass('active');
		main.changeTab(tabId);
	}
};

$( function () {
	//默认tab和年月日
	var tabId = "1";
	var cycleId = "3";
	localStorage.setItem("tabId",tabId);
	localStorage.setItem("cycleId",cycleId);
	main.changeCycle(cycleId);
    $('#monitorTab li').click(function () {
        var tabId = $(this).attr('tab-id');
        main.changeTab(tabId);
    });
} );