(function($){
var myflow = $.myflow;

$.extend(true,myflow.config.props.props,{
	"pkgId": "00000",
	"id": "00000",
	"props" : {
		"text" : {"name":'name', "label":'名称：', "value":'新建决策树', "editor":function(){return new myflow.editors.inputEditor();}}
	}
});
$.extend(true,myflow.config.tools.states,{
			"start" : {
				"type" : 'start',
				"name" : {"text":'<<start>>'},
				"text" : {"text":'开始', "fill":"#02BABC"},
				"img" : {"src" : webpath+'/static/imgs/decisionTree/icon_begin_green.png',"width" : 18, "height":18},
				"attr": {"fill":"#E7F5F5", "stroke":"#fff","width":65,"height":26},
				"props": {
					"text": {"name":"text","label": "名称：", "value":"", "editor": function(){return new myflow.editors.textEditor();}, value:'开始'},
				}},
			"end" : {"type" : 'end',
				"name" : {"text":'<<end>>'},
				"text" : {"text":'结束', "fill":"#CE2C2C"},
				"img" : {"src" : webpath+'/static/imgs/decisionTree/icon_end_red.png',"width" : 18, "height":18},
				"attr": {"fill":"#FCE0E0", "stroke":"#fff","width":65,"height":26},
				"props" : {
					"text": {"name":'text',"label": '名称：', "value":'', "editor": function(){return new myflow.editors.textEditor();}, value:'结束'},
				}},
			"fork" : {"type" : 'fork',
				"name" : {"text":'<<fork>>'},
				"showType": 'image',
				"text" : {"text":'分支'},
				"attr":{"width":26, "height":26},
				"img" : {"src" : webpath+'/static/imgs/decisionTree/icon_fenzhi_grey.png',"width":26, "height":26},
				"props" : {
					"text": {"name":'text', "label": '名称：', "value":'', "editor": function(){return new myflow.editors.textEditor();}, value:'分支'}
				}},
			"forkTxt" : {"type" : 'forkTxt',
				"name" : {"text":'<<fork>>'},
				"text" : {"text":'分支', "fill":"#ffffff"},
				"attr":{"fill":"#a29e98", "stroke":"#fff","width":70, "height":26},
				"img" : {"src" : webpath+'/static/imgs/decisionTree/branchWhite.png',"width":18, "height":18},
				"props" : {
					"text": {"name":'text', "label": '名称：', "value":'', "editor": function(){return new myflow.editors.inputEditor();}, value:'分支'}
				}},
			"join" : {"type" : 'join',
				"name" : {"text":'<<join>>'},
				"text" : {"text":'合并'},
				"showType": 'image',
				"attr":{"width":26, "height":26},
				"img" : {"src" : webpath+'/static/imgs/decisionTree/icon_juhe_grey.png',"width":26, "height":26},
				"props" : {
					"text": {"name":'text', "label": '名称：', "value":'', "editor": function(){return new myflow.editors.textEditor();}, value:'合并'}
				}},
			"task" : {"type" : 'task',
				"name" : {"text":'<<task>>'},
				"text" : {"text":'规则集', "fill":"#1B4485"},
				"attr":{"fill":"#D6E1F3", "stroke":"#fff","width":70, "height":26},
				"img" : {"src" : webpath+'/static/imgs/decisionTree/icon_rule_btn.png',"width":18, "height":18},
				"props" : {
					"isPublic": {"name":'isPublic', "label": '是否公共：', "value":'', "editor": function(){return new myflow.editors.inputEditorIsPublic();}, value:-1},
					"ruleSetId": {"name":'ruleSetId', "label": '规则ID：', "value":'', "editor": function(){return new myflow.editors.inputEditorRuleSet();}},
					"text": {"name":'text', "label": '名称：', "value":'', "editor": function(){return new myflow.editors.inputEditor();}, value:'规则集'},
					"action": {"name":'action', "label": '规则集：', "value":'', "editor": function(){return new myflow.editors.actionEditMore();}}
				}},
			"node" : {"type" : 'node',
				"name" : {"text":'<<node>>'},
				"text" : {"text":'节点', "fill":"#ffffff"},
				"attr":{"fill":"#4496d1", "stroke":"#fff","width":70, "height":26},
				"img" : {"src" : webpath+'/static/imgs/decisionTree/nodeWhite.png',"width":18, "height":18},
				"props" : {
					"text": {"name":'text', "label": '名称：', "value":'', "editor": function(){return new myflow.editors.inputEditor();}, value:'节点'},
					"nodeId": {"name":'action', "label": '规则ID：', "value":'', "editor": function(){return new myflow.editors.inputEditorMore("nodeId");}},
					"nodeName": {"name":'action', "label": '规则：', "value":'', "editor": function(){return new myflow.editors.inputEditorMore("nodeName");}}
				}},
			"T" : {"type" : 'T',
				"name" : {"text":'<<true>>'},
				"text" : {"text":'真', "fill":"#ffffff"},
				"attr":{"fill":"#009a4b", "stroke":"#fff","width":70, "height":26},
				"img" : {"src" : webpath+'/static/imgs/decisionTree/trueWhite.png',"width":18, "height":18},
				"props" : {
					"text": {"name":'text', "label": '名称：', "value":'true', "editor": function(){return new myflow.editors.inputEditor();}, value:'true'},
					"TType": {"name":'TType', "label": '类型：', "value":'', "editor": function(){return new myflow.editors.inputEditorSec();}},
				}},
			"F" : {"type" : 'F',
				"name" : {"text":'<<false>>'},
				"text" : {"text":'假', "fill":"#ffffff"},
				"attr":{"fill":"#ff0000", "stroke":"#fff","width":70, "height":26},
				"img" : {"src" : webpath+'/static/imgs/decisionTree/falseWhite.png',"width":18, "height":18},
				"props" : {
					"text": {"name":'text', "label": '名称：', "value":'false', "editor": function(){return new myflow.editors.inputEditor();}, value:'false'},
				}},
			"interface" : {"type" : 'interface',
				"name" : {"text":'<<interface>>'},
				"text" : {"text":'接口', "fill":"#EB6813"},
				"attr":{"fill":"#F9E4D7", "stroke":"#fff","width":70, "height":26},
				"img" : {"src" : webpath+'/static/imgs/decisionTree/icon_jiekou_yellow.png',"width":18, "height":18},
				"props" : {
					"text": {"name":'text', "label": '名称：', "value":'', "editor": function(){return new myflow.editors.inputEditor();}, value:'接口'},
					"ointerface": {"name":'ointerface', "label": '接口：', "value":'', "editor": function(){return new myflow.editors.openOtherDiv();}}
				}},
			"funcMethod" : {"type" : 'funcMethod',
				"name" : {"text":'<<funcMethod>>'},
				"text" : {"text":'接口', "fill":"#4B2E55"},
				"attr":{"fill":"#DED9FA", "stroke":"#fff","width":70, "height":26},
				"img" : {"src" : webpath+'/static/imgs/decisionTree/icon_hanshu_purple.png',"width":18, "height":18},
				"props" : {
					"text": {"name":'text', "label": '名称：', "value":'', "editor": function(){return new myflow.editors.inputEditor();}, value:'接口'},
					"funcMethod": {"name":'funcMethod', "label": '函数：', "value":'', "editor": function(){return new myflow.editors.funcMethodSec();}}
				}}
});
})(jQuery);