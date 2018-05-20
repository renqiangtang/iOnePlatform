var tradeL02Obj = {};

// -------------------------------------
// 页面初始化，清除页面元素
// ------------------------------------
tradeL02Obj.html_clear = function() {
	$('#L02PName').empty();
	$('#L02stdTime').empty();
	$('#L02restTime').empty();
	$('#L02maxPrice').empty();
	$('#L02tNo').empty();
	$('#L02businessTypeName').empty();
	$('#L02beginPrice').empty();
	$('#L02stepPrice').empty();
	$('#L02dealerName').empty();
	$('#L02beginTime').empty();
	$('#L02endTime').empty();
	$('#L02biddername').empty();
	$('#L02Unit1').empty();
	$('#L02Unit2').empty();
	$('#L02PriceInput').val("");
	$('#L02hzPrice').empty();
	$('#L02bidTip').hide();
}

// ----------------------------------------------
// 页面加载
// ---------------------------------------------
tradeL02Obj.html_refresh = function() {
	var cmd = new Command();
	cmd.module = tradeMain.module;
	cmd.service = "Trade";
	cmd.method = "getTradeInfo";
	cmd.targetId = tradeCommon.targetId;
	cmd.success = function(data) {
		if (data.node && data.node.ptype && data.node.ptype == "L02")
			tradeL02Obj.html_load(data);
	};
	cmd.executeAsync();
}

// -------------------------------------
// 页面加载
// -------------------------------------
tradeL02Obj.html_load = function(data) {
	if(tradeCommon.targetId!=data.target.target_id) return;
	if (!tradeCommon.allowRefresh) {
		return;
	}
	if (tradeCommon.sending) {// 如果是发送状态，则不更新页面
		return;
	}
	//
	tradeCommon.stdTime = data.target.stdTime;
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
	$('#L02stdTime').html(data.target.stdTime);
	$('#L02restTime').html(data.node.restTime);
	//var targetHref = '<a href="javascript:pageTradeCommonObj.viewTarget(\'' + tradeCommon.targetId + '\')" class="fb link01">' + data.target.targetName + '</a>'
	var targetHref = '<span onclick="javascript:pageTradeCommonObj.viewTarget(\'' + tradeCommon.targetId + '\')" class="fb link01 chand">' + data.target.targetName + '</span>'
	$('#L02tNo').html(targetHref);
	$('#L02businessTypeName').html(data.target.business_type_name);
	if(data.target.trans_type==1){
		$('#L02transTypeName').html('拍卖');
	}else{
		$('#L02transTypeName').html('挂牌');
	}
	$('#L02beginPrice').html(data.node.initValueStr);
	$('#L02stepPrice').html(data.node.stepStr);
	$('#L02dealerName').html(data.target.biddername);
	$('#L02beginTime').html(data.node.beginTime);
	$('#L02endTime').html(data.node.endTime);
	$('#L02biddername').html(data.target.biddername);
	$('#L02Unit1').html(data.node.unit);
	$('#L02Unit2').html(data.node.unit);
	$('#L02PName').html(data.node.pname);
	$('#L02maxPrice').html(data.node.priceStr);
	$('#L02addbtn').attr('title', '增加' + Math.abs(tradeCommon.stepPrice) + tradeCommon.unit);
	$('#L02reducebtn').attr('title', '减少' + Math.abs(tradeCommon.stepPrice) + tradeCommon.unit);
	if(data.node.restSecond && data.node.restSecond<60){
		$('#'+tradeCommon.tipId+'Div').show();
		$('#'+tradeCommon.tipId).html('距限时竞价开始还有'+data.node.restSecond+'秒!');
	}else
		$('#'+tradeCommon.tipId+'Div').hide();
	if (data.node.rightType == 1)
		$('#L02bjDiv').show();
	else
		$('#L02bjDiv').hide();
	// 显示是否同意
	if (data.node.isEnd == 1) {
		$('#L02priceDiv').hide();
		$('#L02choiceDiv').show();
		switch (data.node.isAgree) {
		case -1:
			$('#L02bjDiv').hide();
			break;
		case 0:
			$('#L02choiceMsg').html("本轮已有人出了封顶价[" + data.node.finalValueStr + "]，你是否同意本价格?");
			$('#L02iAgree').show();
			$('#L02iNoAgree').show();
			break;
		case 1:
			$('#L02choiceMsg').html("还剩下" + data.node.restTime + "进入下一轮指标竞价!");
			$('#L02iAgree').hide();
			$('#L02iNoAgree').hide();
			break;
		}
	} else {
		$('#L02priceDiv').show();
		$('#L02choiceDiv').hide();
	}
	// 报价记录
	tradeCommon.showOffer(data);
	// 刷新价格输入框
	tradeCommon.refreshPrice();
	//刷新时间
	tradeCommon.displayTime(data);
}

// ----------------------------
// 发送报价到后台
// ------------------------------
tradeL02Obj.sendPrice = function() {
	var cmd = new Command();
	cmd.module = tradeMain.module;
	cmd.service = "Trade";
	cmd.method = "savePrice";
	cmd.targetId = tradeCommon.targetId;
	cmd.price = $('#L02PriceInput').val();
	cmd.enPrice = tradeCommon.encode();
	cmd.step = tradeCommon.stepPrice;
	cmd.ptype = tradeCommon.ptype;
	cmd.qtype = tradeCommon.qtype;
	cmd.success = function(data) {
		$.ligerDialog.alert(data.message);
	}
	cmd.complete = function(xhr) {
		tradeCommon.sending = false;
		tradeL02Obj.html_refresh();
	}
	cmd.execute();

}

// -----------------------------
// 发送意见到后台
// ------------------------------
tradeL02Obj.agree = function(agreeType) {
	var cmd = new Command();
	cmd.module = tradeMain.module;
	cmd.service = "Trade";
	cmd.method = "agreeEnter";
	cmd.targetId = tradeCommon.targetId;
	cmd.agreeType = agreeType;
	cmd.success = function(data) {
		$.ligerDialog.alert('提交意见成功！');
	}
	cmd.execute();
}

// ---------------------------------
// 页面初始化
// --------------------------------

$(document).ready(function() {
	tradeL02Obj.html_clear();
	$('#L02iAgree').click(function(event) {
		tradeL02Obj.agree(1);
	});
	$('#L02iNoAgree').click(function(event) {
		tradeL02Obj.agree(-1);
	});
	$('#L02bidBtn').click(function(event) {
		tradeCommon.sending = true;
		var info = tradeCommon.checkPrice();
		if (info.flag) {
			var msg = '确定出价' + $('#L02PriceInput').val() + tradeCommon.unit + '?';
			if (tradeCommon.maxPriceUser == getUserId()) {
				msg = "前一个有效报价也是您出的!" + msg;
			}
			if (info.msg)
				msg = info.msg;
			var m = $.ligerDialog.confirm(msg, function(yes) {
				if (yes) {
					tradeL02Obj.sendPrice();
				}
				tradeCommon.sending = false;
			});
			$('.l-dialog-close').hide();
			// m.options.allowClose=false;
			// alert(m);
		} else {
			$.ligerDialog.alert(info.msg);
			tradeCommon.sending = false;
		}
	});
});

// ---------------------------------
// 启动
// --------------------------------
tradeL02Obj.start = function(targetId) {
	tradeL02Obj.html_clear();
	tradeCommon.init();
	tradeCommon.allowRefresh = true;
	tradeCommon.targetId = targetId;
	tradeCommon.priceInputId = 'L02PriceInput';// 出价输入框
	tradeCommon.resetButtonId = 'L02resetBtn';// 复位按钮
	tradeCommon.addButtonId = 'L02addbtn';// 添加按钮
	tradeCommon.reduceButtonId = 'L02reducebtn';// 减少按钮
	tradeCommon.priceUpCaseId = 'L02hzPrice';// 价格大写
	tradeCommon.TipId = 'L02Tip';// 提示
	tradeCommon.offerListId = 'L02OfferList';// 出价记录
	tradeCommon.initBtns();
	tradeL02Obj.html_refresh();
	tradeCommon.timer = setInterval("tradeL02Obj.html_refresh()", tradeCommon.intervalTime);
}

// ---------------------------------
// 停止
// --------------------------------
tradeL02Obj.destory = function() {
	clearInterval(tradeCommon.timer);
}
