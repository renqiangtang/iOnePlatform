var pageObj = {};
pageObj.tabManager = null;
pageObj.gridManager = null;
pageObj.businessType = "";
pageObj.businessTypeConfig = null;
pageObj.initTabing = false;
pageObj.sysAllowGoodsTypeArray = null;
pageObj.userAllowGoodsTypeArray = null;
pageObj.allowGoodsTypeArray = null;
pageObj.allowGoodsTypes = "";

// ------------------------------
// 初始化tab
// ------------------------------
pageObj.initTab = function () {
	pageObj.tabManager = $("#tabDone").ligerTab({onAfterSelectTabItem: pageObj.afterSelectTabItem});
	if (!(pageObj.allowGoodsTypeArray && pageObj.allowGoodsTypeArray.length > 0 && pageTradeCommonObj.allBusinessTypeFull))
		return;
	pageObj.initTabing = true;
	var businessTypeRelArray = new Array();
	for(var key in pageTradeCommonObj.allBusinessTypeFull) {
		if ($.inArray(key.substr(0, 3), pageObj.allowGoodsTypeArray) > -1)
			businessTypeRelArray.push(key);
	}
	// 对数组排序，后再生成标签
	businessTypeRelArray.sort(function (a, b) {
		return a > b;
	});
	for(var i = 0; i < businessTypeRelArray.length; i++) {
		var businessTypeConfig = pageTradeCommonObj.allBusinessTypeFull[businessTypeRelArray[i]];
		pageObj.tabManager.addTabItem({text: businessTypeConfig.name, tabid: 'tabBusinessType_' + businessTypeConfig.id, showClose: false});
	}
	pageObj.initTabing = false;
}

pageObj.afterSelectTabItem = function (tabid) {
	if (pageObj.initTabing)
		return;
	pageObj.businessType = null;
	pageObj.businessTypeConfig = null;
	var url = approot + '/data?module=trademan&service=DoneTarget&method=getDoneTargetListData'
		+ '&moduleId=' + pageObj.moduleId + '&userId=' + pageObj.userId
		+ '&allowGoodsTypes=' + pageObj.allowGoodsTypes
		+ '&goodsUse=' + $("#txtGoodsUse").val() + '&transType=' + $("#txtTransTypeValue").val();
	if(tabid == 'tabitem1') {
		$("#targetNoCol1").show();
		$("#targetNoCol2").show();
		$("#targetNameCol1").show();
		$("#targetNameCol2").show();
		if(pageObj.goodsType=="301"||pageObj.goodsType=="301,401") $("#targetNoTitle").html("矿区名称");
		else $("#targetNoTitle").html("宗地编号");
		pageObj.queryData();;
	} else {
		pageObj.businessType = tabid.substr(16);
		if (pageTradeCommonObj.allBusinessTypeFull && pageObj.businessType in pageTradeCommonObj.allBusinessTypeFull) {
			pageObj.businessTypeConfig = pageTradeCommonObj.allBusinessTypeFull[pageObj.businessType];
			if (pageObj.businessTypeConfig.no_label) {
				$("#targetNoCol1").show();
				$("#targetNoCol2").show();
			} else {
				$("#targetNoCol1").hide();
				$("#targetNoCol2").hide();
			}
			if (pageObj.businessTypeConfig.name_label) {
				$("#targetNameCol1").show();
				$("#targetNameCol2").show();
			} else {
				$("#targetNameCol1").hide();
				$("#targetNameCol2").hide();
			}
			if(pageObj.businessTypeConfig.no_label=="宗地"){
				$("#targetNoTitle").html("宗地编号");
				pageObj.businessTypeConfig.no_label="宗地编号";
			}else{
				$("#targetNoTitle").html(pageObj.businessTypeConfig.no_label);
			}
			
		} else {
			$("#targetNoCol1").show();
			$("#targetNoCol2").show();
			$("#targetNameCol1").show();
			$("#targetNameCol2").show();
			if(pageObj.goodsType=="301"||pageObj.goodsType=="301,401") $("#targetNoTitle").html("矿区名称");
			else $("#targetNoTitle").html("宗地编号");
		//	$("#targetNameTitle").html("名称");
			url += '&targetNo=' + $("#txtTargetNo").val()
				+ '&targetName=' + $("#txtTargetName").val();
		}
		url += '&businessType=' + pageObj.businessType;
	}

	pageObj.gridManager = $("#targetGrid").ligerGrid({
		url: url,
		columns: pageObj.getGridColumns(pageObj.businessType),
		pagesizeParmName: 'pageRowCount',
		pageParmName: 'pageIndex',
		sortnameParmName: 'sortField',
		sortorderParmName: 'sortDir',
		isScroll: true,
		pageSizeOptions: [10, 20, 30], 
		showTitle: false,
		height: 397,
		detail: {height: 100, onShowDetail: pageObj.showDetail }
	});
	pageObj.queryData();
	$(".l-grid-body").css("overflow-x","auto"); 
}

pageObj.getGridColumns = function(businessType){
	if(pageObj.goodsType=="301"||pageObj.goodsType=="301,401"){
		var goodsUseLabel = "矿种";
	}else {
		var goodsUseLabel = "用途";
	}
	var columns = new Array();
	if (pageObj.businessTypeConfig) {
		if (pageObj.businessTypeConfig.user_label)
			goodsUseLabel = pageObj.businessTypeConfig.user_label;
		if (pageObj.businessTypeConfig.no_label && pageObj.businessTypeConfig.name_label)
			columns.push({ display: pageObj.businessTypeConfig.no_label, name: 'no', width: '12%', align: 'left', render: pageObj.noRender,frozen: true },
				{ display: pageObj.businessTypeConfig.name_label, name: 'name', width: '12%', align: 'left', render: pageObj.nameRender});
		else if (pageObj.businessTypeConfig.no_label)
			columns.push({ display: pageObj.businessTypeConfig.no_label, name: 'no', width: '12%', align: 'left', render: pageObj.targetRender });
		else if (pageObj.businessTypeConfig.name_label)
			columns.push({ display: pageObj.businessTypeConfig.name_label, name: 'name', width: '12%', align: 'left', render: pageObj.targetRender});
	} else {
		if(pageObj.goodsType=="301"||pageObj.goodsType=="301,401")
		columns.push({ display: '矿区名称', name: 'no', width: '17%', align: 'left', render: pageObj.noRender  });
		else columns.push({ display: '宗地编号', name: 'no', width: '17%', align: 'left', render: pageObj.noRender });
	}
	columns.push({ display: '类型', name: 'business_type', width :'100', render: pageObj.businessTypeRender},
		{ display: goodsUseLabel, name: 'goods_use', width :'200' },
		{ display: '交易方式', name: 'trans_type_label', width :'100'},
		{ display: '状态', name: 'status', width :'100',render:pageObj.statusRender},
		{ display: '成交日期', name: 'end_trans_time', width :'100',render:pageObj.transTimeRender},
		{ display: '成交价', name: 'trans_price', width :'200' ,render:pageObj.transPriceRender},
		{ display: '竞买人数', name: 'apply_count', width :'100',render:pageObj.applyCountRender }
	);
	return columns;
}
pageObj.convertzh = function (value, convertUnit) {
	if (value == null || isNaN(value))
		return "";
	else {
		var fltValue = parseFloat(value);
		if (convertUnit && convertUnit == "万元")
			fltValue = parseFloat((fltValue / 10000).toFixed(6));
		return fltValue.addComma();
	}
}
// ---------------------
// 显示明细
// -----------------
pageObj.showDetail = function(row, detailPanel, callback) {
	var targetId = row.target_id;
	var transType = Number(row.trans_type).toFixed(0);// 交易类型[2]
	var isNetTrans = Number(row.is_net_trans).toFixed(0);// 交易方式[18]
	var priceUnit=row.unit;
	var beginPrice = pageObj.convertzh(row.begin_price,priceUnit);// 起始价
	var priceStep = pageObj.convertzh(row.price_step,priceUnit);// 增价幅度
	var businessType = row.business_type;// 交易类型
	var businessTypeConfig = pageObj.businessTypeConfig ? pageObj.businessTypeConfig : pageTradeCommonObj.allBusinessTypeFull[businessType];
	var beginApplyTime = row.begin_apply_time ? row.begin_apply_time : '';// 竞买申请开始时间
	var beginFocusTime = row.begin_focus_time ? row.begin_focus_time : '';// 出价开始时间
	var endFocusTime = row.end_focus_time ? row.end_focus_time : '';// 出价截止时间
	var beginLimitTime = row.begin_limit_time ? row.begin_limit_time : '';// 限时竞拍开始时间
	var address = row.address ? row.address : '';
	var goodsSize = row.goods_size ? row.goods_size : '';
	var businessTypeText = businessTypeConfig ? businessTypeConfig.name : "其它";
	var mjTitle = (businessTypeConfig ? businessTypeConfig.goods_name : '') + '面积';
	var beginFocusTimeTile = null;
	if(transType == 2) {
		beginFocusTimeTile = '招标开始时间';
	} else if(transType == 1 && isNetTrans == 0) {
		beginFocusTimeTile = '拍卖开始时间';
	}
	var detailBody = null;
	var landormineunit="";
	var landormineaddress="";
	if(pageObj.goodsType=="301"||pageObj.goodsType=="301,401"){
		landormineunit="平方公里";
		landormineaddress="矿区位置";
	}else{
		landormineunit="平方千米";
		landormineaddress="宗地编号";
	}
	if(transType == 2 || (transType == 1 && isNetTrans == 0)) {
		detailBody = '<div class="i-arrow"></div><table class="i-l-gridGetails" style="width:98%; height:87px">' +
					'<tr><td width="120" height="28" class="tr">' + mjTitle + '：</td><td width="150" class="fb">' + goodsSize + landormineunit+'</td><td width="120" class="tr"><span id="address">'+landormineaddress+'</span>：</td><td width="200" class="fb">' + address + '</td><td width="120" class="tr">类型：</td><td class="fb">' + businessTypeText + '</td></tr>' +
					'<tr><td height="28" class="tr">竞买申请开始时间：</td><td class="fb">' + beginApplyTime + '</td><td class="tr">' + beginFocusTimeTile + '：</td><td class="fb">' + beginLimitTime + '</td><td colspan=\'2\'></td></tr>' +
				 '</table>';
	}else{
		detailBody = '<div class="i-arrow"></div><table class="i-l-gridGetails" style="width:98%">'+
					'<tr><td width="120" height="28" class="tr">起始价：</td><td width="150" class="fb">' + beginPrice + priceUnit + '</td><td width="120" class="tr">增价幅度：</td><td width="200" class="fb">' + priceStep + priceUnit + '</td><td width="120" class="tr">类型：</td><td class="fb">' + businessTypeText + '</td></tr>' +
					'<tr><td class="tr"><span id="address">'+landormineaddress+'</span>：</td><td class="fb">' + address + '</td><td class="tr">竞买申请开始时间：</td><td class="fb">' + beginApplyTime + '</td></tr>';
		if (transType == 0 && isNetTrans == 0) {
			detailBody = detailBody + '<tr><td height="28" class="tr">电脑报价截止时间：</td><td class="fb">' + endFocusTime + '</td> <td colspan="4"></td></tr>';
		}else if (transType == 0 && isNetTrans == 1) {
			detailBody = detailBody + '<tr><td height="28" class="tr">自由报价期开始时间：</td><td class="fb">' + beginFocusTime + '</td><td class="tr">自由报价期截止时间：</td><td class="fb">' + endFocusTime + '</td><td class="tr">限时竞拍开始时间：</td><td class="fb">' + beginLimitTime + '</td></tr>';
		}else{
			detailBody = detailBody + '<tr><td height="28" class="tr">竞价开始时间：</td><td class="fb">' + beginLimitTime + '</td> <td colspan="4"></td></tr>';
		}			
		detailBody = detailBody + '</table>';
	}
	$(detailPanel).html(detailBody);
//	if(pageObj.goodsType=="301"||pageObj.goodsType=="301,401") $("#address").html("矿区位置");
}

//---------------------------------
//查看标的详情
//---------------------------------
pageObj.viewTarget = function(targetParam) {
	var param = {};
	if (typeof(targetParam) == "string") {
		param.targetId = targetParam;
	} else if (typeof(targetParam) == "object" && targetParam) {
		param = targetParam;
	}
	if (!("width" in param))
		param.width = 1050;
	// param.type = 1;
	if(pageObj.goodsType=="301"||pageObj.goodsType=="301,401") param.url = approot + "/mine/trade/viewTarget.html";
	else if(pageObj.goodsType=="101") param.url = approot + "/land/trade/viewTarget.html";
	param.urlParams = {
		targetId : param.targetId
	};
	param.title = "标的详情";
	pageObj.openDialog(param);
}

//---------------------------------
//通用的对话框
//---------------------------------
pageObj.openDialog = function(param) {
	var dialogParam = pageObj.getDialogParam(param);
	if (dialogParam.urlParams && (dialogParam.type == 1 || dialogParam.type == 2)) {
		if (typeof(dialogParam.urlParams) == "string") {
			if (dialogParam.urlParams in dialogParam)
				dialogParam.url = dialogParam.url.format(dialogParam[dialogParam.urlParams]);
			else
				dialogParam.url = dialogParam.url.format("");
		} else if (typeof(dialogParam.urlParams) == "object") {
			if (dialogParam.urlParams.length) {// 传入数组
				var userParamsExec = "dialogParam.url = dialogParam.url.format(";
				for (var i = 0; i < dialogParam.urlParams.length; i++)
					userParamsExec += "'" + dialogParam.urlParams + "',";
				userParamsExec = userParamsExec.substr(0, userParamsExec.length - 1);
				userParamsExec += ")";
				eval(userParamsExec);
			} else {// 传入对象
				for (var p in dialogParam.urlParams) {
					if (dialogParam.url.indexOf("?") == -1)
						dialogParam.url += "?1=1";
					if (typeof(dialogParam.urlParams[p]) != "function")
						dialogParam.url += "&" + p + "=" + dialogParam.urlParams[p];
				}
			}
		}
	}
	if (dialogParam.type == 1) {
		delete dialogParam.top;
		delete dialogParam.left;
		DialogOpen(dialogParam);
		// DialogOpen({
		// height: dialogParam.height,
		// width: dialogParam.width,
		// url: dialogParam.url,
		// title: dialogParam.title
		// });
	} else if (dialogParam.type == 2) {
		// 此方式慎用，无法获取框架的内容
		var viewWin = window.open(dialogParam.url, "", "");// location=no,status=no
		viewWin.moveTo(dialogParam.left, dialogParam.top);
		viewWin.resizeTo(dialogParam.width, dialogParam.height);
	} else {
		// dialogParam.url不要带参数
		// dialogParam.urlParams请传入json对象
		DialogModal({
					url : dialogParam.url,
					param : dialogParam.urlParams,
					feature : "location=no;status=no;dialogLeft=" + dialogParam.left + ";dialogTop=" + dialogParam.top
							+ ";dialogWidth=" + dialogParam.width + "px;dialogHeight=" + dialogParam.height + "px"
				});
	}
}

//---------------------------------
//获取通用的对话框参数
//---------------------------------
pageObj.getDialogParam = function(param) {
	var dialogParam = {}
	dialogParam.paramType = 0;
	dialogParam.type = 0;
	dialogParam.width = 0;
	dialogParam.height = 0;
	dialogParam.title = "";
	dialogParam.url = "";
	if (typeof(param) == "string") {
		dialogParam.param = param;
	} else if (typeof(param) == "object" && param) {
		dialogParam.paramType = 1;
		for (var p in param) {
			if (typeof(param[p]) != "function")
				dialogParam[p] = param[p];
		}
	}
	if (!dialogParam.width || dialogParam.width == 0 || dialogParam.width > screen.availWidth)
		dialogParam.width = screen.availWidth;
	dialogParam.left = (screen.availWidth - dialogParam.width) / 2;
	if (!dialogParam.height || dialogParam.height == 0)
		dialogParam.height = $(getMainFrame(window)).height();
	if (dialogParam.height > screen.availHeight)
		dialogParam.height = screen.availHeight;
	dialogParam.top = (screen.availHeight - dialogParam.height) / 2;
	return dialogParam;
}

// ----------------------------
// 渲染交易类型
// ----------------------------
pageObj.noRender = function (row, rowNamber){
	var no = row.no ? row.no : '';
	var targetId = row.target_id;
	return "<a href='javascript:pageObj.viewTarget(\"" + targetId + "\")'>" + no + "</a>";
}

pageObj.nameRender = function (row, rowNamber){
	var name = row.name ? row.name : '';
	var targetId = row.target_id;
	return "<a href='javascript:pageObj.viewTarget(\"" + targetId + "\")'>" + name + "</a>";
}

pageObj.targetRender = function (row, rowNamber) {
	var no = row.no;
	var name = row.name;
	var noText = no && name && no != name ? name + '(' + no + ')' : no ? no : name;
	var targetId = row.target_id;
	return "<a href='javascript:pageObj.viewTarget(\"" + targetId + "\")'>" + noText + "</a>";
}

pageObj.transTimeRender = function (row, rowNamber) {
	var endTransTime = row.end_trans_time;
	if (endTransTime)
		return endTransTime.substr(0, 16);
	else
		return "";
}

// ----------------------------
// 渲染交易状态
// ----------------------------
pageObj.statusRender = function (row, rowNamber) {
	var status = Number(row.status).toFixed(0);
//	if (status == '3') {
//		return '已公告';
//	} else if(status == '4') {
//		return '交易中';
//	} else if (status == '5') {
//		return '已成交';
//	} else if(status == '6') {
//		return '未成交';
//	} else if(status == '7') {
//		return '成交资格审核';
//	}
	return comObj.targetStatusObj[status];
}

pageObj.businessTypeRender = function (row, rowNamber){
	var businessType = row.business_type;
	var businessTypeText = pageTradeCommonObj.allBusinessTypeFull && businessType in pageTradeCommonObj.allBusinessTypeFull
		? pageTradeCommonObj.allBusinessTypeFull[businessType].name : "其它";
	return businessTypeText;
}

// ----------------------------
// 渲染竞买人数
// ----------------------------
pageObj.applyCountRender = function (row, rowNamber) {
	var licenseCount = Number(row.apply_count).toFixed(0);
	var licenseCountLabel = "";
	if (licenseCount >= 0) {
		if (licenseCount == 0) {
			licenseCountLabel = "无人申请";
		} else if (licenseCount == 1) {
			licenseCountLabel = "一人申请";
		} else {
			licenseCountLabel = "多人申请";
		}
	} else {
		licenseCountLabel = "-";
	}
	return licenseCountLabel;
}

pageObj.transPriceRender = function (row, rowNamber) {
	if (row.status == '7'|| row.status == '6' || row.status == '8') {
		return "";
	} else
		return row.trans_price;
}

pageObj.convertFloatValue = function (value) {
	if (value == null || isNaN(value))
		return "0";
	else {
		var fltValue = parseFloat(value);
		return fltValue.addComma()
	}
}

// -------------------------------------------
// 查询数据
// ------------------------------------------
pageObj.queryData = function () {
	var url = approot + '/data?module=trademan&service=DoneTarget&method=getDoneTargetListData&moduleId=' + pageObj.moduleId;
	var targetNo = $("#txtTargetNo").val();
	var transType = $("#txtTransType").val();
	var goodsUse = $("#txtGoodsUse").val();
	var queryParams = { userId: pageObj.userId,
		transType: transType, goodsUse: goodsUse, allowGoodsTypes: pageObj.allowGoodsTypes};
	if (pageObj.businessTypeConfig) {
		if (pageObj.businessTypeConfig.no_label && pageObj.businessTypeConfig.name_label) {
			queryParams.targetNo = targetNo;
		} else if (pageObj.businessTypeConfig.no_label)
			queryParams.targetNo = targetNo;
		else if (pageObj.businessTypeConfig.name_label){
			
		}
		queryParams.businessType = pageObj.businessType;
	} else {
		queryParams.targetNo = targetNo;
	}
	if (pageObj.gridManager != null) {
		pageObj.gridManager.refresh(url, queryParams);
	}
}

// ---------------------------------
// 查询条件初始化
// ---------------------------------
pageObj.initConditionCtrl = function () {
	var data = new Array();
// if (pageTradeCommonObj.allTransType) {
// var transTypeList = new Array();
// for (var key in pageTradeCommonObj.allTransType) {
// var transType = pageTradeCommonObj.allTransType[key];
// if (transType && $.inArray(transType.name, transTypeList) == -1) {
// transTypeList.push(transType.name);
// data.push({ text: transType.name, id: transType.trans_type_name });
// }
// }
// }
//	if (pageObj.allowGoodsTypeArray && pageObj.allowGoodsTypeArray.length > 0 && pageTradeCommonObj.allBusinessTypeFull) {
//		var businessTypeRelArray = new Array();
//		for(var key in pageTradeCommonObj.allBusinessTypeFull) {
//			if ($.inArray(key.substr(0, 3), pageObj.allowGoodsTypeArray) > -1)
//				businessTypeRelArray.push(key);
//		}
//		var transTypeObj = pageTradeCommonObj.getTransTypeList(null, businessTypeRelArray);
//		for(var key in transTypeObj) {
//			var transType = transTypeObj[key];
//	    	if (transType){
//	    		data.push({ text: key, id: key });
//	    	}
//		}
//	}
	var businessTypeItem = {};
	businessTypeItem.text = "出让"
	businessTypeItem.id = pageObj.goodsType+"001";
	var businessTypeItem1 = {};
	businessTypeItem1.text = "转让"
	businessTypeItem1.id = pageObj.goodsType+"002";
	data.push(businessTypeItem);
	data.push(businessTypeItem1);
	$("#txtTransType").ligerComboBox({ isShowCheckBox: true, isMultiSelect: true,
		data: data, valueFieldID: 'txtTransTypeValue'
	});
}

pageObj.resetConditionCtrl = function () {
	$("#txtTargetNo").val("");
	$("#txtTransType").val("");
	$("#txtTransTypeValue").val("");
	$("#txtGoodsUse").val("");
}

pageObj.loadAllowGoodsTypes = function () {
	// 加载系统允许的交易物类别
	var sysInfo = getSysInfoObj();
	if (sysInfo.goodsTypeIds && sysInfo.goodsTypeIds.length >= 0){
		pageObj.sysAllowGoodsTypeArray = sysInfo.goodsTypeIds;
	}
	// 加载用户允许的交易物类别
	var userInfo = getUserInfoObj();
	if (userInfo.goodsTypeIds && userInfo.goodsTypeIds.length >= 0)
		pageObj.userAllowGoodsTypeArray = userInfo.goodsTypeIds;
	// 合并允许的交易物类别，取交集
	if (pageObj.sysAllowGoodsTypeArray && pageObj.userAllowGoodsTypeArray) {
		pageObj.allowGoodsTypeArray = new Array();
		for(var i = 0; i < pageObj.userAllowGoodsTypeArray.length; i++) {
			if ($.inArray(pageObj.userAllowGoodsTypeArray[i], pageObj.allowGoodsTypeArray) == -1
					&& $.inArray(pageObj.userAllowGoodsTypeArray[i], pageObj.sysAllowGoodsTypeArray) != -1 ){
				pageObj.allowGoodsTypeArray.push(pageObj.userAllowGoodsTypeArray[i]);
				if(sysInfo.goodsTypeIds=='301'){
					pageObj.allowGoodsTypeArray.push('401');
				}
			}
		}
		for(var i = 0; i < pageObj.allowGoodsTypeArray.length; i++)
			pageObj.allowGoodsTypes += pageObj.allowGoodsTypeArray[i] + ';';
		if (pageObj.allowGoodsTypes != '')
			pageObj.allowGoodsTypes = pageObj.allowGoodsTypes.substr(0, pageObj.allowGoodsTypes.length - 1);
	}
}

pageObj.initHtml = function () {
	pageObj.moduleId = Utils.getUrlParamValue(document.location.href, "moduleId");
	pageObj.userId = getUserId();
	pageTradeCommonObj.getBusinessType();
	pageTradeCommonObj.getBusinessTypeFull();
	pageTradeCommonObj.getTransTypes();
	pageObj.goodsType=Utils.getUrlParamValue(document.location.href, "goodsType");
	pageObj.loadAllowGoodsTypes();
	pageObj.initConditionCtrl();
	if(pageObj.goodsType=="301"||pageObj.goodsType=="301,401") {
		$("#goodsUse").html("矿种");
		$("#targetGrid").addClass("mine_trade_panel");
		$("#tabDone").addClass("mine_tag");
	}else{
		$("#goodsUse").html("用途");
	}
	$("#btnQuery").click(pageObj.queryData);
	$("#btnReset").click(pageObj.resetConditionCtrl);
	pageObj.initTab();
	pageObj.tabManager.selectTabItem("tabitem1");
	
}

$(document).ready(pageObj.initHtml);