var pageObj = {};

pageObj.save = function() {
	var userName = $('#txtUserName').val();
	var passWord = $('#txtPassword').val();
	var passWord2 = $('#txtConfirmPassword').val();
	if(userName.length == 0){
		alert('用户名不能为空');
		return;
	}
	if(passWord.length == 0){
		alert('密码不能为空');
		return;
	}
	if (passWord != passWord2) {
		alert('两次输入的密码不一致!');
		return;
	}

	var cmd = new Command('sysman', 'User', 'demoRegister');
	cmd.userName = userName;
	cmd.password = $.md5(passWord);
	cmd.u = '001';
	cmd.goodsTypeIds = Utils.getPageValue('goodsType');
	cmd.success = function(data) {
		if (data.state == 1) {
			alert('体验用户注册成功！');
			window.close();
		} else {
			alert(data.message);
		}

	};
	cmd.execute();
}

pageObj.init = function() {
	$('#save').attr('href', 'javascript:pageObj.save();');
}

$(document).ready(pageObj.init);
