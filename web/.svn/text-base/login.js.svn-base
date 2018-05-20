var pageObj = {};
pageObj.vli=null;
pageObj.un=null;
pageObj.pd=null;
pageObj.newCode=null;
pageObj.cantonName=null;
//
pageObj.init = function() {
	document.location.href = approot + "/sysman/register.html";
}

pageObj.registBidder = function(bidderId) {
	var obj = {};
	obj.url = "registBidder.html";
	var param = {};
	if (typeof(bidderId) == "string")
		param.id = bidderId;
	else {
		var goodsTypeIds = Utils.getPageValue("goodsType");
		if (goodsTypeIds) {
			if (goodsTypeIds == '101')
				param.bidderType = 0;
			else if (goodsTypeIds == '301' || goodsTypeIds == '401')
				param.bidderType = 2;
			else if (goodsTypeIds == '501')
				param.bidderType = 3; 
		} else
			param.bidderType = 0;//新注册时缺省为土地竞买人，之后需要通过不同入口进行分别注册
	}
	if (!objIsEmpty(param))
		obj.param = param;
	obj.feature = "dialogWidth=800px;dialogHeight=500px";
	DialogModal(obj);
	pageObj.refreshSecurityCode();
}

pageObj.refreshSecurityCode = function () {
	var times = (new Date()).valueOf();
	$("#codeImage").attr("src", approot + "/securityCode?module=base&service=SecurityCode&method=getCode&times=" + times);
}

pageObj.login = function() {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.method = "getLoginUser";
	var goodsTypeIds = Utils.getPageValue("goodsType");
		
	if (!goodsTypeIds)
		goodsTypeIds = Utils.getPageValue("goodsTypeIds");
	cmd.goodsTypeIds = goodsTypeIds;
	if (!pageObj.noca) {// 检查CA
		var cakey = ca.check($('#pw').val());
		if (!cakey) {
			DialogError('CA密码错误');
			return;
		} else
			cmd.cakey = cakey;
	} else {
		if(pageObj.vli!=null){
			cmd.userName=pageObj.un;
			cmd.password=pageObj.pd;
		}else{
			cmd.userName = $('#un').val();
			cmd.password = $.md5($('#pw').val());
		}
	}
	if(pageObj.vli!=null){
		cmd.newCode = pageObj.newCode;
	}else{
		cmd.newCode = $("#sc").val();
	}
	cmd.success = function(data) {
		if (data.state == 1) {
			var roleNo = data.defaultRole.no;
			if (roleNo) {
				if (data.userType == 1 && data.refStatus == 0)
					alert("登录成功。\n但您注册的信息暂未审核通过，进入系统暂时不允许进行申请竞买等相关操作。\n请随时留意注册的手机短信通知或者进入主界面后查看右上角您的资料页面信息。");
				var href = approot + "/mainframe.html?goodsType=" + pageObj.goodsType;
				if(pageObj.cantonName!=null && pageObj.cantonName!=undefined && pageObj.cantonName!=""){ 
					href+="&pa="+encodeURIComponent(pageObj.cantonName);
				}
				if(pageObj.initPage)
					href+='&initPage='+pageObj.initPage;
				location.href = href;

			} else {
				//用户有效，但未关联任何角色。不允许登录操作
				var userType = data.userType;
				if (userType == 0)
					alert("登录成功，但可能由于系统管理员暂未对您进行系统授权，您暂时不能进入系统进行操作。");
				else if (userType == 1) {
					alert("竞买人可能申请的业务暂未审批通过，请等待审批或者补充注册信息。");
					pageObj.registBidder(data.refId);
				} else
					alert("登录成功，但用户未授权或者其它原因暂未启用无法进入系统操作。");
			}
		} else {
			/*
			errorType=message
			0=验证码错误
			1=检查用户信息失败(查询用户数据时失败)
			2=用户不存在
			3=用户密码错误
			4=用户处于无效状态
			5=用户关联信息读取失败
			6=用户关联信息(竞买人/银行/内部人员)不存在
			7=用户关联信息(竞买人/银行/内部人员)处于无效状态
			8=竞买人处于冻结/待年审/注销/申请中等非正常可用状态
			100=土地、房产竞买人不允许交叉登录
			101=未知错误(用户及关联的竞买人都正常，但不能确定什么原因不能正常使用)
			102=其它未知错误
			为了避免非法尝试登录，不提示具体登录错误。仅当验证码错误时
			 */
			if (data.errorType == 8 && data.refId && (data.refStatus == 0 || data.refStatus == 5 || data.refStatus == 6)) {
				alert(data.message + "。"
					+ "\n系统不允许登录，"
					+ Utils.decode(data.refStatus, 0, "请修改您的注册资料或者相关材料后提交审核。", 5, "请等待工作人员审批。", 6, "请查阅“审核状态”中工作人员的留言进行补充注册信息或者相关材料", "未知原因。"));
				pageObj.registBidder(data.refId);
			} else if (data.errorType == 0) {
				if (data.message)
					alert(data.message);
				else
					alert("验证码错误。");
			} else {
				if (data.message) {
					//alert(data.message);
					alert("登录失败，可能原因：用户名或者密码错误、用户无效或者被停用。"
						+ (data.errorType ? "错误码：" + data.errorType : ""));
				} else
					alert("登录失败，未知错误。");
				pageObj.refreshSecurityCode();
			}
		}
	};
	cmd.execute();
}

pageObj.registerDemo = function(){
	var obj = {};
	obj.url = "registerDemo.html";
	obj.param = {};
	obj.param.goodsType = pageObj.goodsType;
	obj.feature = "dialogWidth=500px;dialogHeight=300px";
	DialogModal(obj);
	pageObj.refreshSecurityCode();
}

pageObj.isNoca = function(){
	var cmd = new Command();
	cmd.module = "base";
	cmd.service = "AppConfig";
	cmd.method = "getPro";
	cmd.key = "noca";
	cmd.u = "portal";
	var pa=Utils.getPageValue("pav");
    var upv=Utils.getPageValue("upv");
    var dn=Utils.getPageValue("dn");
    if(pa!=null){
    	cmd.pav =  pa;
    }
    if(upv!=null){
    	cmd.upv =  upv;
    }
    if(dn!=null){
    	cmd.dn =  dn;
    }
	cmd.success = function(data) {
		pageObj.noca = data.noca =="1" || data.noca=="true" ? true :false;
		if (pageObj.noca) {// 通过用户名密码
			$('#userLogin').show();
			$('#un').focus();
		} else {// 通过CA登录
			$('#userLogin').hide();
			$('#pw').focus();
		}
		Utils.gtPa(data);
	};
	cmd.execute();
}

pageObj.isDemoVersion = function(){
	var cmd = new Command();
	cmd.module = "base";
	cmd.service = "AppConfig";
	cmd.method = "getPro";
	cmd.key = "versionMode";
	cmd.u = "portal";
	cmd.success = function(data) {
		if(data.versionMode == 'demo' ){
			pageObj.versionMode = data.versionMode;
			pageObj.isDemo = true;
			$('#demo').show();
//			$('#registerBtn').show();
		}
	};
	cmd.execute();
}

//
$(document).ready(function() {
	pageObj.versionMode = Utils.getPageValue('versionMode');
	pageObj.noca = parseInt(Utils.getPageValue("noca"));
	pageObj.goodsType = Utils.getPageValue('goodsType');
	pageObj.cantonName = Utils.getPageValue('pa');
	var cantonCode=pageObj.cantonName;
	pageObj.initPage = Utils.getPageValue('initPage');
	pageObj.isDemo = 'demo'==pageObj.versionMode;
	$('#demo').hide();
//	$('#registerBtn').hide();
	pageObj.isDemoVersion();
	$("#codeImage").attr("src", approot + "/securityCode?module=base&service=SecurityCode&method=getCode");
	pageObj.isNoca();
	$('#un').bind({
		blur : function() {
			$(this).attr('class', 'i-text')
		},
		focus : function() {
			$(this).attr('class', 'i-text-focus')
		}
	});

	$('#pw').bind({
		blur : function() {
			$(this).attr('class', 'i-text')
		},
		focus : function() {
			$(this).attr('class', 'i-text-focus')
		}
	});

	$('#sc').bind({
		blur : function() {
			$(this).attr('class', 'i-text i-text-yzm')
		},
		focus : function() {
			$(this).attr('class', 'i-text i-text-yzm-focus')
		}
	});
	$('#loginBtn').click(function(event){
			pageObj.login();
			});
//	$('#registerBtn').click(function(event){
//			if(pageObj.isDemo)
//				pageObj.registerDemo();
//			else	
//				pageObj.registBidder();
//			});

	$('#un').keydown(function(event) {
		event = (event) ? event : ((window.event) ? window.event : "");
		var key = event.keyCode ? event.keyCode : event.which;
		if (key == 13) {
			$('#pw').focus();
		}
	});
	$('#pw').keydown(function(event) {
		event = (event) ? event : ((window.event) ? window.event : "");
		var key = event.keyCode ? event.keyCode : event.which;
		if (key == 13) {
			$('#sc').focus();
		}
	});
	$("#getCodeDiv").click(function() {
		var times = (new Date()).valueOf();
		$("#codeImage").attr("src", approot + "/securityCode?module=base&service=SecurityCode&method=getCode&times=" + times);
	});
	$('#sc').keydown(function(event) {
		event = (event) ? event : ((window.event) ? window.event : "");
		var key = event.keyCode ? event.keyCode : event.which;
		if (key == 13) {
			pageObj.login();
		}
	});
	// $(".i-body-login").width($(window).width());
	// $(".i-body-login").height($(window).height());

	$('#101').hide();
	$('#301').hide();
	$('#501').hide();
	
	$('#'+pageObj.goodsType).show();
	if(cantonCode!=undefined && cantonCode!=null && cantonCode!=""){
		cantonCode=decodeURIComponent(encodeURIComponent(cantonCode).replace("%2F%2F123","").replace("%2F%2F123","")).split(":")[0];
		$("#"+pageObj.goodsType).html($("#"+pageObj.goodsType).html().toString().replace('河源市',comObj.cantonName[cantonCode]));
		$("#bodyClassId").removeClass().addClass("i-body-login-"+cantonCode); 
	}
	//$('#goodsDiv').addClass('goods_' + Utils.getSubStr(pageObj.goodsType, ',', '0', true));
	//
	if(pageObj.goodsType == '501'){
		$("#help").attr("href",approot+"/plow/help/plowhelp.html");
	}
	$("#backportal").attr("href","/"+comObj.urlPath[pageObj.goodsType]+"/portal/index.html?goodsType="+pageObj.goodsType);
	$(document.body).removeClass();
	var bodyCss=comObj.getGoodsUrlPath()+'_login_bg';
	$(document.body).addClass(bodyCss);
});
