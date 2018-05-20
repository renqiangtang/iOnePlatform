var tradeL01Obj = {};

// -------------------------------------
// 页面初始化，清除页面元素
// ------------------------------------
tradeL01Obj.html_clear = function() {
	$('#L01stdTime').empty();
	$('#L01restTime').empty();
	$('#L01PName').empty();
	$('#L01biddername').empty();
	$('#L01tNo').empty();
	$('#L01businessTypeName').empty();
	$('#L01transTypeName').empty();
	$('#L01beginPrice').empty();
	$('#L01stepStr').empty();
	$('#L01beginTime').empty();
	$('#L01endTime').empty();
	$('#L01Unit1').empty();
	$('#L01Unit2').empty();
	$('#L01PriceInput').val("");
	$('#L01hzPrice').empty();
	$('#L01bidTip').empty();
	$('#L01bidTip').hide();
}

// ----------------------------------------------
// 页面加载
// ---------------------------------------------
tradeL01Obj.html_refresh = function() {
	var cmd = new Command();
	cmd.module = tradeMain.module;
	cmd.service = "Trade";
	cmd.method = "getTradeInfo";
	cmd.targetId = tradeCommon.targetId;
	cmd.count = 5;
	cmd.success = function(data) {
	  if (data.node && data.node.ptype && data.node.ptype == "L01")
		 tradeL01Obj.html_load(data);
	};
	cmd.executeAsync();
}

// -------------------------------------
// 页面加载
// -------------------------------------
tradeL01Obj.html_load = function(data) {
	if(tradeCommon.targetId!=data.target.target_id) return;
	//
	if (!tradeCommon.allowRefresh)
		return;
	
	$('#L01PName').html(data.node.pname);
	$('#L01stdTime').html(data.target.stdTime);
	$('#L01restTime').html(data.node.restTime);
	$('#L01biddername').html(data.target.biddername);
	//var targetHref = '<a href="javascript:pageTradeCommonObj.viewTarget(\'' + tradeCommon.targetId + '\')" class="fb link01">' + data.target.targetName + '</a>';
	var targetHref = '<span onclick="javascript:pageTradeCommonObj.viewTarget(\'' + tradeCommon.targetId + '\')" class="fb link01 chand">' + data.target.targetName + '</span>';
	$('#L01tNo').html(targetHref+tradeCommon.reserveInfo(data));
	$('#L01businessTypeName').html(data.target.business_type_name);
	$('#L01beginPrice').html(data.node.initValueStr);
	$('#L01stepStr').html(data.node.stepStr);
	$('#L01beginTime').html(data.node.beginTime);
	$('#L01endTime').html(data.node.endTime);
	$('#L01Unit1').html(data.node.unit);
	$('#L01Unit2').html(data.node.unit);
	if(data.target.trans_type==1)
		$('#L01transTypeName').html('拍卖');
	else
		$('#L01transTypeName').html('挂牌');
	tradeCommon.dealerName = data.target.biddername;
	tradeCommon.targetName = data.target.targetName;
	tradeCommon.initPrice = data.node.initValue;
	tradeCommon.stepPrice = data.node.step;
	tradeCommon.maxPrice = data.node.price;
	tradeCommon.mustPrice = data.node.mustValue;
	tradeCommon.unit = data.node.unit;
	tradeCommon.decimalNumber = data.node.decimalNumber;
	//刷新时间
	tradeCommon.displayTime(data);
	// 刷新价格输入框
	tradeCommon.refreshPrice();
}

// ---------------------------------
// 页面初始化
// --------------------------------
$(document).ready(function() {
	tradeL01Obj.html_clear();
	$('#L01bidBtn').click(function(event) {
		alert("公告期不允许出价!");
	});
});

// ---------------------------------
// 启动
// --------------------------------
tradeL01Obj.start = function(targetId) {
	tradeL01Obj.html_clear();
	tradeCommon.init();
	tradeCommon.allowRefresh = true;
	tradeCommon.targetId = targetId;
	tradeCommon.priceInputId = 'L01PriceInput';// 出价输入框
	tradeCommon.resetButtonId = 'L01resetBtn';// 复位按钮
	tradeCommon.addButtonId = 'L01addbtn';// 添加按钮
	tradeCommon.reduceButtonId = 'L01reducebtn';// 减少按钮
	tradeCommon.priceUpCaseId = 'L01hzPrice';// 价格大写
	tradeCommon.initBtns();
	tradeL01Obj.html_refresh();
	tradeCommon.timer = setInterval("tradeL01Obj.html_refresh()", tradeCommon.intervalTime);
}

// ---------------------------------
// 停止
// --------------------------------
tradeL01Obj.destory = function() {
	clearInterval(tradeCommon.timer);
}
