/**
 * 函数参数配置用的方法
 */
var dictChooseList = new Array();
var dictData;

var fnInitNewParam ={
	/**
	 * 获取所有选中的字典值对象
	 * 
	 * @param {[页面元素s]}
	 *            target [传入页面元素对象]
	 * @return {[数组]} dictObjs [返回字典数组，每个元素含有key和value值]
	 * @example var a = fnGetAllDictValues($(".dictValues > p"));
	 * 
	 */
	fnGetAllDictValues:function(target) {
		var dictObjs = new Array();
		var list = target, i = 0;
		for (i = 0; i < list.length; i++) {
			var newDictObj = new Object;
			newDictObj.typeId = $(list[i]).attr("typeId");
			newDictObj.typeDesc = $(list[i]).attr("typeDesc");
			newDictObj.desc = $(list[i]).attr("desc");
			dictObjs.push(newDictObj);
		}
		dictChooseList = dictObjs;
		return dictObjs;
	},
	//绑定选择字典的元素点击事件
	bindDictElement:function(dom){
		$(dom).addClass("active").siblings().removeClass("active");
	},
	//回显数据,target-$("#modifyVars-content .dictValues")
	fnShowAllDictData:function(target,data) {
		//处理参数配置json串
		$(target).empty();//去掉残留的
		if($.trim(data) != "" && data != "[]"){
			var tmpHtml,temp = eval(data);
			for(var i = 0;i < temp.length;i++){
				tmpHtml = '<p typeId="' + temp[i].typeId + '" typeValue="'+temp[i].typeValue
				+'" typeDesc="'+temp[i].typeDesc+'" desc="'+temp[i].desc+'" onclick="fnInitNewParam.bindDictElement(this)">' 
				+ temp[i].typeDesc + '&nbsp;&nbsp;' + temp[i].desc + '</p>';
				$(target).append(tmpHtml);
			}
		}
	}
}

//新建和修改函数时添加按钮事件
function showAddDict(idx){
	var $div = $("#paramlist");
	var left = $div.offset().left + $div.width() ;
	var top = $div.offset().top ;
	left = 0;top = 0;
	$(".addDictIpts").attr('style','left:' + left + 'px;top:' + top + 'px').show();
	$(".addDictIpts").show();
}
//新建和修改时添加参数类型的input输入关闭按钮
function cancleAddDict(){
	$(".addDictIpts > input[type='text']").val('');
	$(".addDictIpts").hide();
}
//新建和修改时删除当前选中的可选字典值
function deleteDict(){
	var i = 0;
	var $target = $(".dictValues > p.active");
	var typeId = $target.attr("typeId");
	$target.remove();

	for (i = 0; i < dictChooseList.length; i++) {
		if (typeId == dictChooseList[i].typeId) {
			dictChooseList.splice(i, 1);
		}
	}
}
//新建时添加参数类型的input添加按钮
function addDictNew(){
	var typeId = $("#paramTypeSelect1").val();
	var typeDesc = $("#paramTypeSelect1Text").val();
	var desc = $("#desc").val();
	if(desc.length > 10){
		layer.msg('参数描述不能超过10个字符');
		return false;
	}
	if (typeId != null && typeId != "") {
		var tmpHtml = '<p typeId="' + typeId + '" typeDesc="'+typeDesc
			+'" desc="'+desc+'" onclick="fnInitNewParam.bindDictElement(this)">' 
			+ typeDesc + '&nbsp;&nbsp;' + desc + '</p>';

		$(".dictValues").append(tmpHtml);
		var newDictObj = new Object();
		newDictObj.typeId = typeId;
		newDictObj.typeDesc = typeDesc;
		newDictObj.desc = desc;
		dictChooseList.push(newDictObj);
	}
}
//修改时添加参数类型的input添加按钮
function addDictModify(){
	var typeId = $("#modifyVars-content #functionParam option:selected").attr("typeId");
	var typeValue = $("#modifyVars-content #functionParam option:selected").attr("typeValue");
	var typeDesc = $("#modifyVars-content #functionParam option:selected").text();
	var desc = $("#modifyVars-content #desc").val();
	if(desc.length > 10){
		layer.msg('参数描述不能超过10个字符');
		return false;
	}
	if (typeId != null && typeId != "") {
		var tmpHtml = '<p typeId="' + typeId + '" typeValue="'+typeValue+'" typeDesc="'+typeDesc
			+'" desc="'+desc+'" onclick="fnInitNewParam.bindDictElement(this)">' 
			+ typeDesc + '&nbsp;&nbsp;' + desc + '</p>';

		$("#modifyVars-content .dictValues").append(tmpHtml);
		var newDictObj = new Object();
		newDictObj.typeId = typeId;
		newDictObj.typeValue = typeValue;
		newDictObj.typeDesc = typeDesc;
		newDictObj.desc = desc;
		dictChooseList.push(newDictObj);
	}
}


