function jumpToLogin(){
	location.href =  webpath + "/tologin";
}
function sureBtn(){
	var oldPsd = $('#oldPsd').val();
	var newPsd = $('#newPsd').val();
	var rptPsd = $('#rptPsd').val();
	var errTxt = '';
	if(oldPsd == ''){
		errTxt = "当前密码不能为空！";
	}
	if(errTxt == '' && newPsd == ''){
		errTxt = "新密码不能为空！"
	}
	if(errTxt == '' && rptPsd == ''){
		errTxt = "确认密码不能为空！"
	}
	if(errTxt == '' && rptPsd != newPsd){
		errTxt = "确认密码和新密码不一致！"
	}
	if(errTxt == '' && (rptPsd.length < 5 || rptPsd.length > 20)){
		errTxt = "新密码长度需要在5-20位！"
	}
	if(errTxt == ''){
		$.ajax({
			url:webpath + "/modifypassword",
			data:{"oldPsd":oldPsd,"newPsd":newPsd},
			dataType:"json",
			success:function(data){
				//console.log(data.message);
				//修改成功跳转到登录页面/失败的信息
				if(data.success){
					$('#myModal').modal({backdrop:'static',keyboard:false});
				}else{
					$('.errTxt').text(data.message);
				}
				
			}
		});
	}else{
		$('.errTxt').text(errTxt);
	}
}