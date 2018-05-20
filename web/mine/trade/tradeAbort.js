//流拍界面

var tradeAbortObj = {};

// -------------------------------------
// 页面初始化，清除页面元素
// ------------------------------------
tradeAbortObj.html_clear = function() {
	$('#AbortabortMessage').empty();
	$('#Abortbiddername').empty();
	$('#AbortsuccPrice').empty();
	$('#AborttargetName').empty();
	$('#AbortbusinessTypeName').empty();
	$('#AborttransTypeName').empty();
	$('#AbortinitValueStr').empty();
	$('#AbortstepStr').empty();
	$('#AbortbeginTime').empty();
	$('#AbortendTime').empty();
	$('#AbortbidTable').empty();
}

// ----------------------------------------------
// 页面加载
// ---------------------------------------------
tradeAbortObj.html_refresh = function() {
	var cmd = new Command();
	cmd.module = "tradeland";
	cmd.service = "Trade";
	cmd.method = "getTradeInfo";
	cmd.targetId = tradeCommon.targetId;
	cmd.count = 5;
	cmd.success = function(data) {
		tradeAbortObj.html_load(data);
	};
	cmd.execute();

}

// -------------------------------------
// 页面加载
// -------------------------------------
tradeAbortObj.html_load = function(data) {
	if(tradeCommon.targetId!=data.target.target_id) return;
	//
	tradeCommon.dealerName = data.target.biddername;
	//
	$('#AbortabortMessage').html(data.target.abortMessage);
	$('#Abortbiddername').html(data.target.biddername);
	$('#AbortsuccPrice').html(data.target.succPrice);
	// var targetHref='<a
	// href="javascript:pageTradeCommonObj.viewTarget(\''+tradeCommon.targetId+'\')"
	// class="fb link01">'+data.target.targetName+'</a>'
	var targetHref = '<span onclick="javascript:pageTradeCommonObj.viewTarget(\'' + tradeCommon.targetId + '\')" class="fb link01 chand">' + data.target.targetName + '</span>'
	$('#AborttargetName').html(targetHref+tradeCommon.reserveInfo(data));
	$('#AbortbusinessTypeName').html(data.target.business_type_name);
	$('#AbortinitValueStr').html(data.target.initValueStr);
	$('#AbortstepStr').html(data.target.stepStr);
	$('#AbortbeginTime').html(data.target.beginTime);
	$('#AbortendTime').html(data.target.endTime);
	//
	tradeCommon.showOffer2(data);
	$('.i-offerLogList').height(375);
	//
	if (data.target.trans_type && data.target.trans_type == 1) {// 拍卖
		$('#AbortendTimeDiv').hide();
		$('#AborttransTypeName').html('拍卖');
	} else {
		$('#AbortendTimeDiv').show();
		$('#AborttransTypeName').html('挂牌');
	}
	//刷新时间
	tradeCommon.displayTime(data);
}

// ---------------------------------
// 启动
// --------------------------------
tradeAbortObj.start = function(targetId) {
	tradeAbortObj.html_clear();
	tradeCommon.init();
	tradeCommon.targetId = targetId;
	tradeCommon.offerListId = 'AbortbidTable';
	tradeAbortObj.html_refresh();
}

// ---------------------------------
// 停止
// --------------------------------
tradeAbortObj.destory = function() {
}
