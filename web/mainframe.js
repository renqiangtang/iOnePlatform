var pageName = 'mainframe';
var pageObj = {};
pageObj.cantonName=null;
pageObj.loc = '<span class="fb">您现在的位置：</span>';
// --------------------------------------------
// 缺省主界面
// --------------------------------------------
pageObj.mainModuleUrl = approot + "/main.html";
// --------------------------------------------
// 全局CA对象
// --------------------------------------------
pageObj.loginUserByCakey = true;
// --------------------------------------------
// 系统名称
// --------------------------------------------
pageObj.systemName = "交易系统";

// --------------------------------------------
// 默认角色
// --------------------------------------------
pageObj.roleNo = null;

// --------------------------------------------
// 初始化
// --------------------------------------------
pageObj.init = function() {
	var sysInfo = getSysInfoObj();
	if (sysInfo.goodsTypeIds) {
		var allIndex = $.inArray('000', sysInfo.goodsTypeIds);
		if (allIndex != -1) {
			pageObj.goodsType = '000,';
			sysInfo.goodsTypeIds.splice(allIndex, 1);
		} else
			pageObj.goodsType = '';
		for ( var i = 0; i < sysInfo.goodsTypeIds.length; i++)
			pageObj.goodsType += sysInfo.goodsTypeIds[i] + ',';
		if (pageObj.goodsType != '')
			pageObj.goodsType = pageObj.goodsType.substr(0, pageObj.goodsType.length - 1);
	} else
		pageObj.goodsType = Utils.getPageValue('goodsType');
	// 多个交易物类型时取第一作为图片等资源名称
	$('#logoImg').attr('src', 'base/skins/default/images/base/home/' + Utils.getSubStr(pageObj.goodsType, ',', '0', true) +pageObj.cantonName+'_logo.jpg');
	// 获取缺省模块
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.method = "getLoginUserAllData";
	cmd.userId = getUserId();
	cmd.success = function(data) {
		// 加载当前用户缺省主界面
		var userDefaultModuleId = data.defaultModule.moduleId;
		var userDefaultModuleUrl = data.defaultModule.url;
		if (userDefaultModuleUrl != undefined && userDefaultModuleUrl != null && userDefaultModuleUrl != "") {
			pageObj.mainModuleUrl = approot + userDefaultModuleUrl;
			pageObj.mainModuleUrl = Utils.urlAddParams(pageObj.mainModuleUrl, "?moduleId=" + userDefaultModuleId);
			if (pageObj.mainModuleUrl.indexOf('goodsType') < 0)
				pageObj.mainModuleUrl += "&goodsType=" + pageObj.goodsType;
		}
		if (!pageObj.initPage)
			$('#framePage').attr('src', pageObj.mainModuleUrl);
		// 加载主菜单
		var html = '<li id="000" onclick="pageObj.homePage();" onmouseover="pageMenu.menu1MouseOver(this);">首页</li>';
		for ( var i = 0; i < data.mainMenu.menu.length; i++) {
			var item1 = data.mainMenu.menu[i];
			pageMenu.menuObj[item1.id] = item1;
			html += '<li id="' + item1.id + '" onmouseover="pageMenu.menu1MouseOver(this);" >' + item1.text + '</li>';
		}
		$('#menu1').html(html);
		// 加载角色列表
		var defaultItem = data.roleListMenu.menu[0];
		pageObj.roleNo = defaultItem.no;
		var select = $('#roleSelect');
		select.empty();
		select.hide();
		var html = '<option value="' + defaultItem.id + '">' + defaultItem.text + '</option>';
		if ("children" in data.roleListMenu.menu[0]) {
			for ( var i = 0; i < data.roleListMenu.menu[0].children.length; i++) {
				var item = data.roleListMenu.menu[0].children[i];
				html += '<option value="' + item.id + '">' + item.text + '</option>';
			}
			select.html(html);
			select.val(defaultItem.id);
			select.show();
		}
		// 记录系统设置中系统名称（用户配置）
		if (data.systemShortName) {
			pageObj.systemName = data.systemShortName;
			document.title = pageObj.systemName;
		}
		// 中文简称
		$('#organName').html($userInfo.organName);
		$('#loginName').html($userInfo.displayName);
	}
	cmd.execute();
	// 检测是否为竞买人
	var userInfo = getUserInfoObj();
	if ("1" == userInfo['userType']) {
		$('#bidderDiv').show();
		pageObj.refreshCar();
	} else {
		$('#bidderDiv').hide();
	}
}

// --------------------------------------------
// 购物车数目
// --------------------------------------------
pageObj.refreshCar = function() {
	var cmd = new Command();
	cmd.module = "bidder";
	cmd.service = "TransLicense";
	cmd.method = "getCartTargetNum";
	cmd.userId = getUserId();
	cmd.goodsType = pageObj.goodsType;
	cmd.success = function(data) {
		$('#carDetailNum').html(data.num);
	}
	cmd.execute();
}

// --------------------------------------------
// 主菜单
// --------------------------------------------
pageObj.menuItemClick = function(itemId) {
	var item = pageObj.menuObj[itemId];
	var userCakey = getUserCakey();
	if (userCakey && userCakey != "null" && userCakey != "undefined" && item.checkCa) {
		if (ca.readId() != userCakey) {
			// DialogError('请检查用户Cakey是否插上，或者是否是登录用户的Cakey');
			return false;
		}
	}
	var roleNo = pageObj.roleNo;
	if (roleNo == 'bidder' && item.url.indexOf(comObj.getGoodsUrlPath + "/bidder/bidderMain.html") == -1)
		$('#quickDiv').show();
	else
		$('#quickDiv').hide();
	var pageUrl = item.url;
	if (pageUrl.indexOf('goodsType') < 0)
		pageUrl += '&goodsType=' + pageObj.goodsType;
	$('#framePage').attr('src', approot + pageUrl);
	var html = pageObj.loc + item.menuPath;
	$('#loc').html(html);
	$('#loc').show();
}
// --------------------------------------------
// 回到主界面
// --------------------------------------------
pageObj.homePage = function() {
	$('#framePage').attr('src', pageObj.mainModuleUrl);// "/main.html"
	$('#loc').hide();
}

// --------------------------------------------
// 帮助
// --------------------------------------------
pageObj.help = function() {
//	$('#framePage').attr('src', approot +"/"+comObj.getGoodsUrlPath() +"/help/plowHelp.html");
	$("#help").attr("href",approot +"/"+"help/help_index.html");
}

// --------------------------------------------
// 退出登录
// --------------------------------------------
pageObj.logOut = function() {
	var cmd = new Command();
	cmd.module = 'sysman';
	cmd.service = 'User';
	cmd.method = 'logout';
	cmd.success = function() {
		var protocol = window.location.protocol + "//";
		var host = window.location.host;
		location.href = protocol + host  +"/"+comObj.urlPath[pageObj.goodsType]+"/portal/index.html?goodsType=" + pageObj.goodsType+"&canton="+pageObj.cantonName;
//		location.href = protocol + host + approot + "/login.html?goodsType=" + pageObj.goodsType;
	}
	cmd.execute();
}

// --------------------------------------------
// 我的资料
// --------------------------------------------
pageObj.mySelf = function() {
	var obj = {};
	obj.url = approot + '/sysman/mySelf.html?goodsType='+Utils.getPageValue('goodsType');
	obj.feature = "dialogWidth=600px;dialogHeight=450px";
	DialogModal(obj);
}

pageObj.top = function() {
	$(document).scrollTop(0);
}

$(document).ready(function(event) {
	$('.trade_nav_main').empty();
	var roleSelect = $('#roleSelect');
	roleSelect.change(function(ev) {
		var roleId = $(this).val();
		var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "User";
		cmd.method = "saveUserDefaultRoleId";
		cmd.userId = getUserId();// 用户id
		cmd.default_role_id = roleId;
		cmd.success = function(data) {
			document.title = pageObj.systemName;
			location.reload();
		}
		cmd.execute();
	});
	pageObj.init();
	$('#loc').hide();
});

// --------------------------------------------
// 显示购物车内容
// --------------------------------------------
pageObj.myCar = function() {
	var url = approot + '/' + comObj.getGoodsUrlPath() + '/bidder/myCar.html';
	url = Utils.urlAddParams(url, '&goodsType=' + pageObj.goodsType);
	$('#framePage').attr('src', url);
	var html = pageObj.loc + '购物车';
	$('#loc').html(html);
	$('#loc').show();
}

// --------------------------------------------
// 显示购物车内容
// --------------------------------------------
pageObj.myBill = function() {
	var url = approot + '/' + comObj.getGoodsUrlPath() + '/bidder/licenseList.html';
	url = Utils.urlAddParams(url, '&goodsType=' + pageObj.goodsType);
	$('#framePage').attr('src', url);
	var html = pageObj.loc + '我的订单';
	$('#loc').html(html);
	$('#loc').show();
}

// --------------------------------------------
// 
// --------------------------------------------
$(document).ready(function(event) {
	pageObj.cantonName=Utils.getUrlParamValue(window.parent.document.location.href, "pa");
	if(pageObj.cantonName!=undefined && pageObj.cantonName!=null && pageObj.cantonName!=""){
		pageObj.cantonName=decodeURIComponent(pageObj.cantonName.replace("%2F%2F123","").replace("%2F%2F123","")).split(":")[0];
	}
	pageObj.initPage = Utils.getPageValue('initPage');
	if (pageObj.initPage) {
		var goodsType = Utils.getPageValue('goodsType');
		$('#framePage').attr('src', pageObj.initPage + "?goodsType=" + goodsType);
	}
	var goodsType=Utils.getPageValue('goodsType');
	
		$('#myCar').click(pageObj.myCar);
	if(goodsType == '501' || goodsType=='301' || goodsType=='101') {
		$("#myCar").hide();
		$("#carDetailNum").attr("style","display:none;");
	}
	$('#myBill').click(pageObj.myBill);
	$('#quickDiv').hide();
	// $('#subbuy_small_btn').click(pageTradeCommonObj.viewNoticeList);
	// $('#online_small_btn').click(pageTradeCommonObj.viewTradeMain);
	$('#mySelf').attr('href', 'javascript:pageObj.mySelf();');
//	$('#help').attr('href', 'javascript:pageObj.help();');
	pageObj.help();
	$('#logout').attr('href', 'javascript:pageObj.logOut();');
	$('#favorite').attr('href', '');
	$(document.body).removeClass();
	$(document.body).addClass(comObj.getGoodsUrlPath);

	pageObj.init();
	// 替换打印
	window.print2 = window.print;
	window.print = pageObj.printFrame;
	pageObj.printPageSetupNoneHeaderFooter();
});

// ////////////////////////////////////////////////////////////////////
// 以下是打印代码///////////////////////////////////////////////////////
// ////////////////////////////////////////////////////////////////////
pageObj.printFrame = function(frame, onfinish) {
	if (!frame)
		frame = window;
	function execOnFinish() {
		switch (typeof (onfinish)) {
		case "string":
			execScript(onfinish);
			break;
		case "function":
			onfinish();
		}
		if (focused && !focused.disabled)
			focused.focus();
	}

	if (frame.document.readyState !== "complete" && !confirm("文件未下载完成，继续打印？")) {
		execOnFinish();
		return;
	}

	if (window.print2) {
		var focused = document.activeElement;
		frame.focus();
		if (frame.print2)
			frame.print2();
		else
			frame.print();
		execOnFinish();
		return;
	}

	var eventScope = printGetEventScope(frame);
	var focused = document.activeElement;

	window.printHelper = function() {
		execScript("on error resume next: printWB.ExecWB 6, 1", "VBScript");
		pageObj.printFireEvent(frame, eventScope, "onafterprint");
		printWB.outerHTML = "";
		execOnFinish();
		window.printHelper = null;
	}

	document.body.insertAdjacentHTML("beforeEnd", "<object id=\"printWB\" width=0 height=0 \
    classid=\"clsid:8856F961-340A-11D0-A96B-00C04FD705A2\"></object>");
	pageObj.printFireEvent(frame, eventScope, "onbeforeprint");
	frame.focus();
	window.printHelper = printHelper;
	setTimeout("window.printHelper()", 0);
}

pageObj.printIsNativeSupport = function() {
	var agent = window.navigator.userAgent;
	var i = agent.indexOf("MSIE ") + 5;
	return parseInt(agent.substr(i)) >= 5;
}

pageObj.printFireEvent = function(frame, obj, name) {
	var handler = obj[name];
	switch (typeof (handler)) {
	case "string":
		frame.execScript(handler);
		break;
	case "function":
		handler();
	}
}

pageObj.printGetEventScope = function(frame) {
	var frameset = frame.document.all.tags("FRAMESET");
	if (frameset.length)
		return frameset[0];
	return frame.document.body;
}

// 设置网页打印的页眉页脚为空
pageObj.printPageSetupNoneHeaderFooter = function() {
	try {
		var HKEY_Root = "HKEY_CURRENT_USER";
		var HKEY_Path = "\\Software\\Microsoft\\Internet Explorer\\PageSetup\\";
		var wss = new ActiveXObject("WScript.Shell");
		var HKEY_Key = "header";
		wss.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, "");
		HKEY_Key = "footer";
		wss.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, "");
	} catch (e) {

	}
}
