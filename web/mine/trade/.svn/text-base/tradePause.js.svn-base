var tradePauseObj = {};

// -------------------------------------
// 页面初始化，清除页面元素
// ------------------------------------
tradePauseObj.html_clear = function() {
	$('#PausePName').empty();
	$('#PausestdTime').empty();
	$('#PauserestTime').empty();
	$('#PausemaxPrice').empty();
	$('#PausetNo').empty();
	$('#PausebusinessTypeName').empty();
	$('#PausetransTypeName').empty();
	$('#PausebeginPrice').empty();
	$('#PausestepPrice').empty();
	$('#PausedealerName').empty();
	$('#PausebeginTime').empty();
	$('#PauseendTime').empty();
	$('#Pausebiddername').empty();
}

// ----------------------------------------------
// 页面刷新
// ---------------------------------------------
tradePauseObj.html_refresh = function() {
	var cmd = new Command();
	cmd.module = tradeMain.module;
	cmd.service = "Trade";
	cmd.method = "getTradeInfo";
	cmd.targetId = tradeCommon.targetId;
	cmd.success = function(data) {
		if (data.node && data.node.ptype && data.node.ptype == "Pause")
			tradePauseObj.html_load(data);
	};
	cmd.executeAsync();

}

// -------------------------------------
// 页面加载
// -------------------------------------
tradePauseObj.html_load = function(data) {
	if(tradeCommon.targetId!=data.target.target_id) return;
	if (!tradeCommon.allowRefresh) {
		return;
	}
	if (tradeCommon.sending) {// 如果是发送状态，则不更新页面
		return;
	}
	//
	tradeCommon.dealerName = data.target.biddername;
	tradeCommon.targetName = data.target.targetName;
	tradeCommon.initPrice = data.node.initValue;
	tradeCommon.stepPrice = data.node.step;
	tradeCommon.maxPrice = data.node.price;
	tradeCommon.mustPrice = data.node.mustValue;
	tradeCommon.unit = data.node.unit;
	tradeCommon.decimalNumber = data.node.decimalNumber;
	tradeCommon.ptype = data.node.ptype;
	tradeCommon.qtype = data.node.qtype;
	tradeCommon.maxPriceUser = data.node.priceUser;
	//	
	$('#PausePName').html(data.node.pname);
	$('#PausestdTime').html(data.target.stdTime);
	$('#PauserestTime').html(data.node.restTime);
	$('#PausemaxPrice').html(data.node.priceStr);
	// var targetHref = '<a href="javascript:pageTradeCommonObj.viewTarget(\'' +
	// tradeCommon.targetId + '\')" class="fb link01">' + data.target.targetName
	// + '</a>'
	var targetHref = '<span onclick="javascript:pageTradeCommonObj.viewTarget(\'' + tradeCommon.targetId + '\')" class="fb link01 chand">' + data.target.targetName + '</span>'
	$('#PausetNo').html(targetHref+tradeCommon.reserveInfo(data));
	$$('#info').onclick = function() {
		pageTradeCommonObj.viewTarget(tradeCommon.targetId)
	};
	$('#PausebusinessTypeName').html(data.target.business_type_name);
	if (data.target.trans_type == 1) {
		$('#PausetransTypeName').html('拍卖');
	} else {
		$('#PausetransTypeName').html('挂牌');
	}
	$('#PausebeginPrice').html(data.node.initValueStr);
	$('#PausestepStr').html(data.node.stepStr);
	$('#PausedealerName').html(data.target.biddername);
	$('#PausebeginTime').html(data.node.pauseTime);
	$('#Pausebiddername').html(data.target.biddername);
	//刷新时间
	tradeCommon.displayTime(data);
}

// ---------------------------------
// 启动
// --------------------------------
tradePauseObj.start = function(targetId) {
	tradePauseObj.html_clear();
	tradeCommon.init();
	tradeCommon.allowRefresh = true;
	tradeCommon.targetId = targetId;
	tradePauseObj.html_refresh();
	tradeCommon.timer = setInterval("tradePauseObj.html_refresh()", tradeCommon.intervalTime);
}

// ---------------------------------
// 停止
// --------------------------------
tradePauseObj.destory = function() {
	clearInterval(tradeCommon.timer);
}
