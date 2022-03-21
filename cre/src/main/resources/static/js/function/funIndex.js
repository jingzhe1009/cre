//初始化表格
function initTable(){
	//搜索按钮
	$("#funSearchBth").click(function(){
		$("#table1").dataTable().fnDraw(false);//重新绘制表格
	});
	//初始化表格
	var pageLength = 10;//每页显示条数
	$.extend( $.fn.dataTable.defaults, {
	    info: true,
		"serverSide":true,
//		"paging": true, 
	    "pageLength": pageLength
	});
	$('#table1').width('100%').dataTable({
		"searching": false,
		"ordering":  false,
		"destroy": true,
		"bLengthChange":false,
		"pagingType": "full_numbers",
		"columns": [
            {"title":"函数名称" ,"data":"functionName"},
            {"title":"函数描述" ,"data":"functionDesc"},
            {"title":"返回值类型" ,"data":"functionReturnType","render":function (data) {
            	switch($.trim(data)){
	            	case '0':
	            		return '无';
	            	case '1':
	            		return '基本类型';
	            	case '2':
	            		return '实体类型';
	            	default:
	            		return '--';
	        	}
            }},
            {"title":"函数返回值" ,"data":"functionReturnValue","render":function (data, type, row) {
            	if(data == null || data == ''){
            		return '';
            	}
            	var obj = JSON.parse(data);
            	var returnValue = obj.returnValue;
            	if(row.functionReturnType=='1') {
            		for(var i=0;i<baseVariableType.length;i++){
            			if(returnValue==baseVariableType[i].key){
            				return baseVariableType[i].text;
            			}
            		}
            	}else if(row.functionReturnType=='2') {
            		var returnValueFormat = obj.returnValueFormat;
            		for(var i=0;i<entityType.length;i++){
            			if(returnValue==entityType[i].key){
            				return entityType[i].text+"/"+returnValueFormat;
            			}
            		}
            	}
            	return "--";
            }},
            {"title":"函数类类型" ,"data":"classType","render":function (data) {
            	//函数类的类型,0-系统内置,1-内部,2-外部
            	switch($.trim(data)){
	            	case '0':
	            		return '系统内置';
	            	case '1':
	            		return '内部';
	            	case '2':
	            		return '外部';
	            	default:
	            		return '--';
            	}
        	}},
            {"title":"类路径" ,"data":"functionClassPath"},
            {"title":"类名" ,"data":"className"},
            {"title":"方法名" ,"data":"functionMethodName"},
            {"title":"创建时间" ,"data":"createDate"},
            {"title":"操作" ,"data": null,"render": function(data, type, row) {
            	var html = '<div>';
            		html += '<div id="row_'+row.functionId+'" style="display:none;">'+JSON.stringify(row)+'</div>'
            		html += '<span onclick="modifyRow(\''+row.functionId+'\');" class="btn-sm cm-tblB">修改</span>';
            		html += '<span onclick="delRow(\''+row.functionId+'\');" class="btn-sm cm-tblC">删除</span>';
            		html += '</div>';
            	$("#row_"+row.functionId).data("rowData",row);
				return html;
			}}
        ],
        ajax: {
            url:webpath+ '/fun/list',
            "type":'POST',
            "data": function ( d ) {//查询参数
                return $.extend( {}, d, {
                	"functionName": $.trim($("#funName").val()),
                	"folderId":$.trim($("#folderId").val()),
                	"groupId":$.trim($("#groupId").val())
                });
            }
        },
        "fnDrawCallback": function (oSettings, json) {
            // $("tr:even").css("background-color", "#fbfbfd");
            // $("table:eq(0) th").css("background-color", "#f6f7fb");
        }
	});
}
//修改API接口
function modifyRow(functionId){
	//清空数据
	$(".modal-body input[type='text']").val('');
	var rowObj = JSON.parse($("#row_"+functionId).text());
	//初始化数据
	$("#functionId").val(functionId);
	$("#groupId").val(rowObj.groupId);
	$("#functionName").val(rowObj.functionName);
	$("#functionDesc").val(rowObj.functionDesc);
	
	var returnValueType = rowObj.functionReturnType;
	
  	var returnValueObj = new Object();
  	var returnVaue = '';
  	if(returnValueType == null || returnValueType==''||returnValueType=='0'){
  		returnValueType = '0';
  	}else{
  		returnValueObj = JSON.parse(rowObj.functionReturnValue);
  		returnVaue = returnValueObj.returnValue;
  	}
  	$("input[name='returnValueType'][value='"+returnValueType+"']").prop("checked",true); 
  	var returnValueSelect = null;
    if(returnValueType == '0'){
		$("#returnValueDiv").hide();
		$("#returnValueFormatDiv").hide();
	}else if(returnValueType == '1'){
		$("#returnValueDiv").show();
		$("#returnValueFormatDiv").hide();
		returnValueSelect = $('#returnValueSelect').cm_select({field:"returnValueSelect1",data:baseVariableType});
		returnValueSelect.setValue(returnVaue);
	}else if(returnValueType == '2'){
		$("#returnValueDiv").show();
		$("#returnValueFormatDiv").show();
		returnValueSelect = $('#returnValueSelect').cm_select({field:"returnValueSelect1",data:entityType});
		returnValueSelect.setValue(returnVaue);
		var returnValueFormatData = [
       		{key:'json',text: 'JSON',selected:true},
       		{key:'xml',text: 'XML'}
       	];
		var returnValueFormatSelect = $('#returnValueFormatSelect').cm_select({field:"returnValueFormatSelect1",data:returnValueFormatData});
		returnValueFormatSelect.setValue(returnValueObj.returnValueFormat);
	}
    
    //类类型设置
    var selectData = [
		{key:'1',text: '内部',selected:true},
//		{key:'2',text: '外部'}
	];
	var classTypeSelect = $('#classTypeSelect').cm_select({field:"classTypeSelect1",data:selectData});
	classTypeSelect.setValue(rowObj.classType);
		
    $("#classTypeSelect").val(rowObj.classType);
	$("#functionClassPath").val(rowObj.functionClassPath);
	$("#className").val(rowObj.className);
	$("#functionMethodName").val(rowObj.functionMethodName);
	
	//设置参数
	fnInitNewParam.fnShowAllDictData($("#updateForm .dictValues"),rowObj.functionParamsConf);
	
	$('#updateForm').validator('cleanUp');//清除表单中的全部验证消息
	$("#modifyFunDiv").modal({show:true});
}
//保存修改API接口
function saveModifyFun() {
	if(!$('#updateForm').isValid()){
		return;
	}
	var functionId = $("#functionId").val();
	var functionName = $("#functionName").val();
	var functionDesc = $("#functionDesc").val();
	
	var returnValueType = $("input[name='returnValueType']:checked").val();
	var returnValueObj = new Object();
	returnValueObj['returnValue'] = $("#returnValueSelect1").val();
	returnValueObj['returnValueFormat'] = $("#returnValueFormatSelect1").val();
	
	var classType = $("#classTypeSelect1").val();
	var functionClassPath = $("#functionClassPath").val();
	if(classType=='1'){
		var packagePatt = /^([a-zA-Z|_|\$]+\.([a-zA-Z|_|\$\d]+\.)*[a-zA-Z|_|\$|\d]+)$/;
		var packagePattResult = packagePatt.test(functionClassPath);
		if(packagePattResult==false){
			creCommon.showErrorValidator('updateForm','functionClassPath','不符合java包名规范.');
			return;
		}
	}else if(classType=='2'){
		var pathPatt = new RegExp(/^([A-Za-z]{1}:\/(\w\/)*)|(\/?(\w\/)+)$/);
		var result = pathPatt.exec(functionClassPath);
		console.log(result);
		var pathPattResult = pathPatt.test(functionClassPath);
		console.log(pathPattResult)
		if(pathPattResult==false){
			creCommon.showErrorValidator('updateForm','functionClassPath','非法文件路径.');
			return;
		}
	}
	
	var className = $("#className").val();
	var functionMethodName = $("#functionMethodName").val();
	
	var paramObj = fnInitNewParam.fnGetAllDictValues($(".dictValues > p"));
	var paramObjStr = JSON.stringify(paramObj);
	
	$.ajax({
		url:webpath + "/fun/update",
		data:{
			"functionId":functionId,
			"folderId":$("#folderId").val(),
			"groupId":$("#groupId").val(),
			"functionName":functionName,
			"functionDesc":functionDesc,
			"functionReturnType":returnValueType,
			"functionReturnValue":JSON.stringify(returnValueObj),
			"classType":classType,
			"functionClassPath":functionClassPath,
			"className":className,
			"functionMethodName":functionMethodName,
			"functionParamsConf":paramObjStr
		},
		type:"post",
		dataType:"json",
		success:function(data) {
	      	if(data.status==0){
	      		new $.zui.Messager('修改成功', {
	      		    placement: 'center', // 定义显示位置
      		    	time:1000,//表示时间延迟，单位毫秒
      		    	type: 'success' // 定义颜色主题
	      		}).show();
	      		$('#modifyFunDiv').modal('hide');
	      		$("#table1").dataTable().fnDraw(false);//重新绘制表格
	      	}else{
	      		new $.zui.Messager('修改失败--'+data.msg, {
	      			time:1000,//表示时间延迟，单位毫秒
	      		    placement: 'center', // 定义显示位置
	      		    type: 'warning' // 定义颜色主题
	      		}).show();
	      	}
		}
	});
}
//删除API接口
function delRow(functionId){
	$("#functionId").val(functionId);
	$("#delFunDiv").modal({});
}

function deleteApiBind(){
	$("#delFunBtn").click(function(){
		$.ajax({
			url:webpath + "/fun/delete",
			data:{
				"functionId":$("#functionId").val(),
				"folderId":$("#folderId").val()
			},
			type:"post",
			dataType:"json",
			success:function(data) {
		      	if(data.status==0){
		      		new $.zui.Messager('删除成功', {
		      		    placement: 'center', // 定义显示位置
	      		    	time:1000,//表示时间延迟，单位毫秒
	      		    	type: 'success' // 定义颜色主题
		      		}).show();
		      		$("#delFunDiv").modal('hide');
		      		$("#table1").dataTable().fnDraw(false);//重新绘制表格
		      	}else{
		      		new $.zui.Messager('删除失败--'+data.msg, {
		      			time:1000,//表示时间延迟，单位毫秒
		      		    placement: 'center', // 定义显示位置
		      		    type: 'warning' // 定义颜色主题
		      		}).show();
		      		$("#delFunDiv").modal('hide');
		      	}
			}
		});
	});
}
//新建接口
function createFunction() {
	var folderId = $.trim($("#folderId").val());
	var groupId = $.trim($("#groupId").val());
	var url = webpath + "/fun/createFunIndex?folderId="+folderId+"&groupId="+groupId;
	creCommon.loadHtml(url);
}

//返回
function returnPage(folderId) {
	var url = webpath + "/ruleFolder/rulePackageMgr?folderId="+folderId+'&childOpen=o';
	creCommon.loadHtml(url);
}

//初始化http类型的返回值redio切换事件
function initReturnRedio() {
	$("#returnValueDiv").hide();
	$('input:radio[name="returnValueType"]').change( function(){
		var value = $(this).val();
		if(value == '0'){
			$("#returnValueDiv").hide();
			$("#returnValueFormatDiv").hide();
		}else if(value == '1'){
			$("#returnValueDiv").show();
			$("#returnValueFormatDiv").hide();
			$('#returnValueSelect').cm_select({field:"returnValueSelect1",data:baseVariableType});
		}else if(value == '2'){
			$("#returnValueDiv").show();
			$("#returnValueFormatDiv").show();
			if(entityType!=null && entityType.length>0){
				entityType[0].selected = true;
			}
			$('#returnValueSelect').cm_select({field:"returnValueSelect1",data:entityType});
			var returnValueFormatData = [
        		{key:'json',text: 'JSON',selected:true},
        		{key:'xml',text: 'XML'}
        	];
			$('#returnValueFormatSelect').cm_select({field:"returnValueFormatSelect1",data:returnValueFormatData});
		}
	});
}

$(function(){
	if(baseVariableType!=null && baseVariableType.length>0){
		baseVariableType[0].selected = true;
	}
	$('#paramTypeSelect').cm_select({field:"paramTypeSelect1",data:baseVariableType});
	
	initTable();
	deleteApiBind();
	//初始化http类型的返回值redio切换事件
	initReturnRedio();
	//忽略校验隐藏的元素
	$('#updateForm').validator({
	    ignore: ':hidden'
	});
});

