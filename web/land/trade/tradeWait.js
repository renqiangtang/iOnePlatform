var tradeWaitObj = {};

tradeWaitObj.html_clear = function() {
	$('#WaitstdTime').empty();
	$('#WaitPName').empty();
	$('#WaitNo').empty();
	$('#WaitRestTime').empty();
	$('#WaitTypeName').empty();
	$('#WaitbeginPrice').empty();
	$('#WaitstepStr').empty();
	$('#WaitFocusBeginTime').empty();
	$('#WaitFocusEndTime').empty();
	$('#WaitLimitBeginTime').empty();
	$('#Waitbiddername').empty();
	$('#WaitInfo').empty();

}

tradeWaitObj.html_refresh = function() {
	var cmd = new Command();
	cmd.module = tradeMain.module;
	cmd.service = "Trade";
	cmd.method = "getTradeInfo";
	cmd.targetId = tradeCommon.targetId;
	cmd.success = function(data) {
		if (data.node && data.node.ptype)
			tradeWaitObj.html_load(data);
	};
	cmd.executeAsync();
}

tradeWaitObj.html_load = function(data) {
	if (tradeCommon.targetId != data.target.target_id)
		return;
	$('#WaitstdTime').html(data.target.stdTime);
	$('#WaitPName').html(data.node.pname);
	var targetHref = '<span onclick="javascript:pageTradeCommonObj.viewTarget(\'' + tradeCommon.targetId + '\')" class="fb link01 chand">' + data.target.targetName + '</span>'
	$('#WaitNo').html(targetHref);
	$('#WaitRestTime').html(data.node.restTime);
	$('#WaitTypeName').html(data.target.business_type_name);
	$('#WaitbeginPrice').html(data.node.initValueStr);
	$('#WaitstepStr').html(data.node.stepStr);
	$('#WaitFocusBeginTime').html(data.target.begin_focus_time);
	$('#WaitFocusEndTime').html(data.target.end_focus_time);
	$('#WaitLimitBeginTime').html(data.target.begin_limit_time);
	$('#Waitbiddername').html(data.target.biddername);
	$('#WaitInfo').html('本标的正在等待进入' + data.node.pname+'...');

}

// ---------------------------------
// 启动
// --------------------------------
tradeWaitObj.start = function(targetId) {
	tradeCommon.targetId = targetId;
	tradeWaitObj.html_clear();
	tradeWaitObj.html_refresh();
	tradeCommon.timer = setInterval("tradeWaitObj.html_refresh()", tradeCommon.intervalTime);
}

// ---------------------------------
// 停止
// --------------------------------
tradeWaitObj.destory = function() {
	clearInterval(tradeCommon.timer);
}

// ---------------------------------
// 页面初始化
// --------------------------------
$(document).ready(function() {
	tradeWaitObj.html_clear();
});