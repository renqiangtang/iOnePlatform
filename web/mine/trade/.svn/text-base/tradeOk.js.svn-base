//成功界面

var tradeOkObj = {};

// -------------------------------------
// 页面初始化，清除页面元素
// ------------------------------------
tradeOkObj.html_clear = function() {
	tradeOkObj.mp3();
	$('#Oksuccbiddername').empty();
	$('#OksuccPrice').empty();
	$('#OksuccTime').empty();
	$('#OktargetName').empty();
	$('#OkbusinessTypeName').empty();
	$('#OkinitValueStr').empty();
	$('#OkstepStr').empty();
	$('#OkbeginTime').empty();
	$('#OkendTime').empty();
	$('#OkbidTable').empty();
	$('#popupOk').click(function() {
		$('#popupOk').hide();
		try {
			var player = $$('#playerOk');
			player.controls.stop();
		} catch (e) {
		}
	});
	$('#popupOkBidderName').empty();
	$('#popupOkTargetName').empty();
}

// ----------------------------------------------
// 页面加载
// ---------------------------------------------
tradeOkObj.html_refresh = function() {
	var cmd = new Command();
	cmd.module = tradeMain.module;
	cmd.service = "Trade";
	cmd.method = "getTradeInfo";
	cmd.targetId = tradeCommon.targetId;
	cmd.count = 5;
	cmd.success = function(data) {
		tradeOkObj.html_load(data);
	};
	cmd.execute();
}

tradeOkObj.mp3 = function() {
	try {
		var player = $$('#playerOk');
		if (!player) {
			var html = '';
			html += '<object id="playerOk" style="display:none" classid="CLSID:6BF52A52-394A-11d3-B153-00C04F79FAA6">';
			html += '	<param NAME="AutoStart" VALUE="1">';
			html += '	<param NAME="url" value="tradeOk.mid">';
			html += '	<param NAME="PlayCount" VALUE="1">';
			html += '</object>';
			$(document.body).append(html);
		} else {
			player.controls.stop();
			player.controls.play();
		}
	} catch (e) {
	}
}

// -------------------------------------
// 页面加载
// -------------------------------------
tradeOkObj.html_load = function(data) {
	if(tradeCommon.targetId!=data.target.target_id) return;
	//
	var html = '';
	tradeCommon.licenseId = data.target.licenseId;
	tradeCommon.targetName = data.target.targetName;
	tradeCommon.dealerName = data.target.biddername;
	tradeCommon.qtype = data.target.qtype;
	//
	$(data.target.succbiddername).each(function(i, ele) {
		if (i == 0)
			html += ele;
		else
			html += ',' + ele;
	});
	$('#popupOkBidderName').html(html);
	$('#popupOkTargetName').html(tradeCommon.targetName);
	$('#Oksuccbiddername').html(data.target.succbiddername.join(","));
	$('#popupOkPrice').html(data.target.succPrice);
	$('#OksuccPrice').html(data.target.succPrice);
	$('#OksuccTime').html(data.target.endTime);
	// var targetHref = '<a href="javascript:pageTradeCommonObj.viewTarget(\'' +
	// tradeCommon.targetId + '\')" class="fb link01">' + tradeCommon.targetName
	// + '</a>'
	var targetHref = '<span onclick="javascript:pageTradeCommonObj.viewTarget(\'' + tradeCommon.targetId + '\')" class="fb link01 chand">' + tradeCommon.targetName + '</span>'
	$('#OktargetName').html(targetHref);
	$('#OkbusinessTypeName').html(data.target.business_type_name);
	$('#OkinitValueStr').html(data.target.initValueStr);
	$('#OkstepStr').html(data.target.stepStr);
	$('#OkbeginTime').html(data.target.beginTime);
	$('#OkendTime').html(data.target.endTime);
	var href = approot + "/download?module=" + tradeMain.module + "&service=Trade&method=downLoadOfferLog&licenseId=" + data.target.licenseId;
	href += '&bidderName=' + encodeURIComponent(data.target.succbiddername.join(","));
	href += '&targetName=' + encodeURIComponent(tradeCommon.targetName);
	$('#OkList').attr("href", href);
	if (data.target.licenseId == data.target.succlicense) {
		$('#popupOk').show();
		$('#OkList').show();
		$('#OkBook').show();
	} else {
		$('#popupOk').hide();
		$('#OkList').hide();
		$('#OkBook').hide();
	}
	if (data.target.trans_type && data.target.trans_type == 1) {// 拍卖
		$('#OkendTimeDiv').hide();
		$('#OktransTypeName').html('拍卖');
	} else {
		$('#OkendTimeDiv').show();
		$('#OktransTypeName').html('挂牌');
	}
	//
	tradeOkObj.bookName = '网上竞得证明';
	if ('2' == tradeCommon.qtype)
		tradeOkObj.bookName = '网上竞得证明';
	$('#OkBookName').html(tradeOkObj.bookName);
	//
	$('#OkbidTable').html(html);
	tradeCommon.showOffer(data);
	$('.i-offerLogList').height(375);
	//刷新时间
	tradeCommon.displayTime(data);
}

// ---------------------------------
// 查看成交通知书/成交确认书
// ---------------------------------
tradeOkObj.viewTransCert = function() {
	var licenseParam = tradeCommon.licenseId;
	var param = {};
	if (typeof (licenseParam) == "string") {
		param.licenseId = licenseParam;
	} else if (typeof (licenseParam) == "object" && licenseParam) {
		param = licenseParam;
	}
	if (!("width" in param))
		param.width = 800;
	if (!("height" in param))
		param.height = 750;
	param.type = 1;
	var cmd = new Command("bidder", "Apply", "getTransOrganByLicenseId");
	cmd.licenseId = licenseParam;
	cmd.success = function(data) {
		tradeOkObj.transOrganInfo = {};
		tradeOkObj.transOrganInfo['organId'] = data.organId;
		tradeOkObj.transOrganInfo['organName'] = data.transOrganName;
		tradeOkObj.transOrganInfo['cantonId'] = data.cantonId;
		tradeOkObj.transOrganInfo['cantonName'] = data.cantonName;
		tradeOkObj.transOrganInfo['businessType'] = data.businessName;
		tradeOkObj.transOrganInfo['goodsType'] = data.goodsTypeName;
		tradeOkObj.transOrganInfo['transTypeName'] = data.transTypeName;
	}
	cmd.execute();

//	var url = 'config/' + tradeOkObj.transOrganInfo['organName'] + "/" + tradeOkObj.transOrganInfo['goodsType'] + "/";
	var url = 'config/' + tradeOkObj.transOrganInfo['organName'] + "/" + tradeOkObj.transOrganInfo['goodsType'] + "/"+tradeOkObj.transOrganInfo['transTypeName']+"/"+tradeOkObj.transOrganInfo['businessType']+"/";
	url += tradeOkObj.bookName + ".html?r=true";
	for ( var pro in tradeOkObj.transOrganInfo) {
		var v = tradeOkObj.transOrganInfo[pro];
		url += '&' + pro + '=' + v;
	}
	if (param)
		url += '&' + param;

	param.url = url;
	param.urlParams = {
		licenseId : param.licenseId
	};
	param.buttons = [ {
		text : '关闭',
		onclick : function(item, dialog) {
			dialog.close();
		}
	} ];
	param.title = "查看" + tradeOkObj.bookName;
	pageTradeCommonObj.openDialog(param);

}

// ---------------------------------
// 启动
// --------------------------------
tradeOkObj.start = function(targetId) {
	tradeOkObj.html_clear();
	tradeCommon.init();
	tradeCommon.offerListId = 'OkbidTable';
	tradeCommon.targetId = targetId;
	tradeOkObj.html_refresh();
}

// ---------------------------------
// 停止
// --------------------------------
tradeOkObj.destory = function() {
}
