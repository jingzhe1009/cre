(function($) {
    var myflow = $.myflow;
    $.extend(true, myflow.editors, {
    	//接口的函数
    	openOtherDiv:function(){
    		var _props, _k, _div, _src, _r;
            this.init = function(props, k, div, src, r) {
                _props = props;
                _k = k;
                _div = div;
                _src = src;
                _r = r;
                $('<input class="interfaceIpt" style="width:100%;"/>').val(props.props[_k].value).change(function() {
                    props.props[_k].value = $(this).val();
                }).appendTo('#' + _div);
                $('#' + _div).data('editor', this);
                
                //其他操作
                $('#myflow_props,.flowOtherProp').hide();
                $('#interfWrap').show();
                //document.getElementById("interfWrap").scrollIntoView();
                $('#content').addClass('minContent');
                interfaceObj.setValue();
                $('#propFootDiv').hide();
                
            }
            this.destroy = function() {
                $('#' + _div + ' input').each(function() {
                    _props.props[_k].value = $(this).val();
                });
            }
    	},
    	//动作函数
    	actionEditMore:function(){
    		var _props, _k, _div, _src, _r;
            this.init = function(props, k, div, src, r) {
                _props = props;
                _k = k;
                _div = div;
                _src = src;
                _r = r;
                
                var rval = props.props[_k].value;
                if(typeof(rval) == "object"){
                	rval = JSON.stringify(rval);
                }
                $('<input class="moreCdt" style="width:100%;"/>').data('val',props.props[_k].value).val(rval).change(function() {
                	props.props[_k].value = $(this).data('val');
                }).appendTo('#' + _div);
                $('#' + _div).data('editor', this);
                
                //其他操作
                $('#myflow_props,.flowOtherProp').hide();
                $('#content').addClass('minContent');
                $('#actionWrap').show();
                $('#propFootDiv').hide();
            	actionObj.setValue();
                
            }
            this.destroy = function() {
                $('#' + _div + ' input').each(function() {
                	_props.props[_k].value = $(this).data('val');
                });
            }
    	},
    	funcMethodSec:function(){
    		var _props, _k, _div, _src, _r;
            this.init = function(props, k, div, src, r) {
                _props = props;
                _k = k;
                _div = div;
                _src = src;
                _r = r;
                
                var rval = props.props[_k].value;
                if(typeof(rval) == "object"){
                	rval = JSON.stringify(rval);
                }
                $('<input class="funcMethodP" style="width:100%;"/>').data('val',props.props[_k].value).val(rval).change(function() {
                	props.props[_k].value = $(this).data('val');
                }).appendTo('#' + _div);
                $('#' + _div).data('editor', this);
                
                //其他操作
                $('#myflow_props,.flowOtherProp').hide();
                $('#content').addClass('minContent');
                $('#funcMeWrap').show();
                $('#propFootDiv').hide();
                funcMethodObj.setValue();
                
            };
            this.destroy = function() {
                $('#' + _div + ' input').each(function() {
                	_props.props[_k].value = $(this).data('val');
                });
            }
    	},
    	inputEditorMore: function(type) {
            var _props, _k, _t, _div, _src, _r;
            
            this.init = function(O, k, div, src, r) {
                _props = O.props;
                _obj = O,
                    _k = k;
                _div = div;
                _src = src;
                _r = r;
                var _pathWithCdt = true;
                var states = myflow.config.restore.states;
                var pathObjs = myflow.config.restore.paths;
                
                
                //其他操作
                $('#myflow_props,.flowOtherProp').hide();
                $('#propFootDiv').show();
                $('#content').addClass('minContent');
                var tempValue =  _props[_k].value;
                if(typeof(tempValue) == 'object'){
                	tempValue = JSON.stringify(tempValue);
                }
                $('<input class="moreCdt"  readOnly="true"/>').data('val',_props[_k].value).val(tempValue).change(function(type) {
                	_props[_k].value = $(this).data('val');
                }).appendTo('#' + _div);

                if (type == "pathCdt") {
                    //判断是从分支节点出来的线
                    var from = pathObjs[_src.getId()].from;

                    for (var rect in states) {
                        if (rect == from && (states[rect].type == 'fork' || states[rect].type == 'forkTxt')) {
                            //线的from端是分支，可以显示配置条件
                            $(".moreCdt").width('128px');
                            _pathWithCdt = true;
                            break;
                        } else {
                            $(".moreCdt").width('100%');
                            _pathWithCdt = false;
                        }
                    }
                    $('#lineJudgeWrap').show();
                    $('#propFootDiv').hide();
                    lineCdtObj.setValue();
                }
                $('#' + _div).data('editor', this);

                //改变颜色函数绑定
                myflow.editors.bindPickColor(O, r, _src);
                
            }
            this.destroy = function(type) {
                $('#' + _div + ' input').each(function(type) {
                	_props[_k].value = $(this).data('val');
                });
            }
        }
    
    });

})(jQuery);