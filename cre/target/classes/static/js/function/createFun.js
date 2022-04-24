//初始化下拉选数据
function initSelect(){
	var selectData = [
  		{key:'1',text: '内部',selected:true},
//  		{key:'2',text: '外部'}
  	];
  	$('#classTypeSelect').cm_select({field:"classTypeSelect1",data:selectData});
  	
  	//参数类型
  	$('#paramTypeSelect').cm_select({field:"paramTypeSelect1",data:baseVariableType});
}

//保存按钮
function savaBtn(){
	if(!$('#insertForm').isValid()){
		return;
	}
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
			creCommon.showErrorValidator('insertForm','functionClassPath','不符合java包名规范.');
			return;
		}
	}else if(classType=='2'){
		var pathPatt = /^([A-Za-z]{1}:\/(\w\/)*)|(\/?(\w\/)+)$/;
		var pathPattResult = pathPatt.test(functionClassPath);
		if(pathPattResult==false){
			creCommon.showErrorValidator('insertForm','functionClassPath','非法文件路径.');
			return;
		}
	}
	var className = $("#className").val();
	var functionMethodName = $("#functionMethodName").val();
	
	var paramObj = fnInitNewParam.fnGetAllDictValues($(".dictValues > p"));
	var paramObjStr = JSON.stringify(paramObj);
	
	$.ajax({
		url:webpath + "/fun/insert",
		data:{
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
	      		cancleBtn();
	      	}else{
	      		new $.zui.Messager('保存失败--'+data.msg, {
	      			time:1000,//表示时间延迟，单位毫秒
	      		    placement: 'center', // 定义显示位置
	      		    type: 'warning' // 定义颜色主题
	      		}).show();
	      	}
		}
	});
}
//取消按钮
function cancleBtn(){
	var url = webpath + "/fun/index?folderId="+$("#folderId").val();
	creCommon.loadHtml(url);
}

function initReturnRedio() {
	$("#returnValueDiv").hide();
	$("#returnValueFormatDiv").hide();
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
	//初始化下拉框数据
	initSelect();
	initReturnRedio();
	//忽略校验隐藏的元素
	$('#insertForm').validator({
	    ignore: ':hidden'
	});
});




