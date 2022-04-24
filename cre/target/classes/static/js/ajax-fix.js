/***重写ajax 错误方法，此文件依赖    frame-layer。js    layer。js*/
/***重寫了jquery的error方法，正常使用ajax即可*/
(function($){  
    //备份jquery的ajax方法  
    var _ajax=$.ajax;  
      
    //重写jquery的ajax方法  
    $.ajax=function(opt){  
        //备份opt中error和success方法  
        var fn = {  
            error:function(XMLHttpRequest, textStatus, errorThrown){},  
            success:function(data, textStatus){}  
        }  
        if(opt.error){  
            fn.error=opt.error;  
        }  
        if(opt.success){  
            fn.success=opt.success;  
        }  
          
        //扩展增强处理  
        var _opt = $.extend(opt,{  
            error:function(XMLHttpRequest, textStatus, errorThrown){  
                //错误方法增强处理  
                new $.zui.Messager(XMLHttpRequest.responseText, {
				    icon: '',
				    placement: 'center' // 定义显示位置
				}).show();
            }  
        });  
        _ajax(_opt);  
    };  
})(jQuery);  