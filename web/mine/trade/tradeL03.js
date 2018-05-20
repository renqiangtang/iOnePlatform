var tradeL03Obj = {};

// -------------------------------------
// 页面初始化，清除页面元素
// ------------------------------------
tradeL03Obj.html_clear = function() {
	$('#L03stdTime').empty();
	$('#L03restTime').empty();
	$('#L03maxPrice').empty();
	$('#L03tNo').empty();
	$('#L03businessTypeName').empty();
	$('#L03transTypeName').empty();
	$('#L03beginPrice').empty();
	$('#L03stepStr').empty();
	$('#L03dealerName').empty();
	$('#L03beginTime').empty();
	$('#L03endTime').empty();
	$('#L03biddername').empty();
	$('#L03Unit1').empty();
	$('#L03Unit2').empty();
	$('#L03bidTable').empty();
	$('#L03PName').empty();
	$('#L03bidTip').hide();
	$('#L03PriceInput').val('');
	$('#L03hzPrice').empty();
}

// ----------------------------------------------
// 页面加载
// ---------------------------------------------
tradeL03Obj.html_refresh = function() {
	var cmd = new Command();
	cmd.module = tradeMain.module;
	cmd.service = "Trade";
	cmd.method = "getTradeInfo";
	cmd.targetId = tradeCommon.targetId;
	cmd.success = function(data) {
		if (data.node && data.node.ptype && data.node.ptype == "L03")
			tradeL03Obj.html_load(data);
	};
	cmd.executeAsync();
}

// -------------------------------------
// 页面加载
// -------------------------------------
tradeL03Obj.html_load = function(data) {
    if(tradeCommon.targetId!=data.target.target_id) return;
	if (!tradeCommon.allowRefresh) {
		return;
	}
	if (tradeCommon.sending) {
		return;
	}// 如果是发送状态，则不更新页面
	//
	tradeCommon.stdTime = data.target.stdTime;
	tradeCommon.targetId = data.target.target_id;
	tradeCommon.targetName = data.target.targetName;
	tradeCommon.dealerName = data.target.biddername;
	tradeCommon.initPrice = data.node.initValue;
	tradeCommon.stepPrice = data.node.step;
	tradeCommon.maxPrice = data.node.price;
	tradeCommon.mustPrice = data.node.mustValue;
	tradeCommon.unit = data.node.unit;
	tradeCommon.decimalNumber = data.node.decimalNumber;
	tradeCommon.ptype = data.node.ptype;
	if (tradeCommon.qtype != data.node.qtype) {
		tradeCommon.qtype = data.node.qtype;
		$('#L03PriceInput').val('');
	}
	tradeCommon.maxPriceUser = data.node.priceUser;
	//	
	$('#L03stdTime').html(data.target.stdTime);
	$('#L03restTime').html(data.node.restTime);
	// var targetHref='<a
	// href="javascript:pageTradeCommonObj.viewTarget(\''+tradeCommon.targetId+'\')"
	// class="fb link01">'+data.target.targetName+'</a>'
	var targetHref = '<span onclick="javascript:pageTradeCommonObj.viewTarget(\'' + tradeCommon.targetId + '\')" class="fb link01 chand">' + data.target.targetName + '</span>'
	$('#L03tNo').html(targetHref);
	$('#L03businessTypeName').html(data.target.business_type_name);
	if (data.target.trans_type == 1) {
		$('#L03transTypeName').html('拍卖');
	} else {
		$('#L03transTypeName').html('挂牌');
	}
	$('#L03beginPrice').html(data.node.initValueStr);
	$('#L03stepStr').html(data.node.stepStr);
	$('#L03dealerName').html(data.target.biddername);
	$('#L03beginTime').html(data.node.beginTime);
	// $('#L03endTime').html(data.node.endTime);
	$('#L03biddername').html(data.target.biddername);
	$('#L03PName').html(data.node.pname);
	$('#L03Unit1').html(data.node.unit);
	$('#L03Unit2').html(data.node.unit);
	$('#L03maxPrice').html(data.node.priceStr);
	$('#L03addbtn').attr('title', '增加' + Math.abs(tradeCommon.stepPrice) + tradeCommon.unit);
	$('#L03reducebtn').attr('title', '减少' + Math.abs(tradeCommon.stepPrice) + tradeCommon.unit);
	if(data.node.restSecond && data.node.restSecond<30){
		$('#'+tradeCommon.tipId+'Div').show();
		$('#'+tradeCommon.tipId).html('距限时竞价结束还有'+data.node.restSecond+'秒!');
	}else
		$('#'+tradeCommon.tipId+'Div').hide();
	if (tradeCommon.unit == '元' || tradeCommon.unit == '万元') {
		$('#L03hzDiv').css('visibility', 'visible');
	} else {
		$('#L03hzDiv').css('visibility', 'hidden');
	}
	$('.i-progressInnerLine').width(358 * data.node.process / 100);
	if (data.node.rightType == 1)
		$('#L03bjDiv').show();
	else
		$('#L03bjDiv').hide();
	// 显示是否同意
	if (data.node.isEnd == 1) {
		$('#L03priceDiv').hide();
		$('#L03choiceDiv').show();
		switch (data.node.isAgree) {
		case -1:
			$('#L03bjDiv').hide();
			break;
		case 0:
			$('#L03choiceMsg').html("本轮已有人出了封顶价[" + data.node.finalValueStr + "]，你是否同意本价格?");
			$('#L03iAgree').show();
			$('#L03iNoAgree').show();
			break;
		case 1:
			$('#L03choiceMsg').html("还剩下" + data.node.restTime + "进入下一轮指标竞价!");
			$('#L03iAgree').hide();
			$('#L03iNoAgree').hide();
			break;
		}
	} else {
		$('#L03priceDiv').show();
		$('#L03choiceDiv').hide();
	}
	// 多指标导航
	var html = '';
	var quotas = data.quotas;
	if (quotas.length == 1)
		$('#L03dzbBid').css("visibility", "hidden");
	else {
		var clazz = 'i-dzb_curr c3';
		for ( var i = 0; i < quotas.length; i++) {
			var quota = quotas[i];
			html += '<dd class="' + clazz + '">' + (i + 1) + '.' + quota['name'] + '</dd>';
			if (data.node.qtype == quota.qtype)
				clazz = '';
		}
		$('#L03dzbList').html(html);
		$('#L03dzbBid').css("visibility", "");
	}
	tradeCommon.showOffer2(data);
	//
	tradeCommon.refreshPrice();
	//刷新时间
	tradeCommon.displayTime(data);
}

// -----------------------------
// 发送报价到后台
// ------------------------------
tradeL03Obj.sendPrice = function() {
	var cmd = new Command();
	cmd.module = tradeMain.module;
	cmd.service = "Trade";
	cmd.method = "savePrice";
	cmd.targetId = tradeCommon.targetId;
	cmd.price = $('#L03PriceInput').val();
	cmd.enPrice = tradeCommon.encode();
	cmd.step = tradeCommon.stepPrice;
	cmd.ptype = tradeCommon.ptype;
	cmd.qtype = tradeCommon.qtype;
	cmd.success = function(data) {
		$.ligerDialog.alert(data.message);
	}
	cmd.complete = function(xhr) {
		tradeCommon.sending = false;
		tradeL03Obj.html_refresh();
	}
	cmd.execute();

}

// -----------------------------
// 发送意见到后台
// ------------------------------
tradeL03Obj.agree = function(agreeType) {
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
	tradeL03Obj.html_clear();
	$('#L03iAgree').click(function(event) {
		tradeL03Obj.agree(1);
	});
	$('#L03iNoAgree').click(function(event) {
		tradeL03Obj.agree(-1);
	});
	$('#L03bidBtn').click(function(event) {
		tradeCommon.sending = true;
		var info = tradeCommon.checkPrice();
		if (info.flag) {
			var msg = '确定出价' + $('#L03PriceInput').val() + tradeCommon.unit + '?';
			if (tradeCommon.maxPriceUser == getUserId()) {
				msg = "前一个有效报价也是您出的!" + msg;
			}
			if (info.msg)
				msg = info.msg;
			var m = $.ligerDialog.confirm(msg, function(yes) {
				if (yes) {
					tradeL03Obj.sendPrice();
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
tradeL03Obj.start = function(targetId) {
	tradeL03Obj.html_clear();
	tradeCommon.init();
	tradeCommon.allowRefresh = true;
	tradeCommon.targetId = targetId;
	tradeCommon.priceInputId = 'L03PriceInput';// 出价输入框
	tradeCommon.resetButtonId = 'L03resetBtn';// 复位按钮
	tradeCommon.addButtonId = 'L03addbtn';// 添加按钮
	tradeCommon.reduceButtonId = 'L03reducebtn';// 减少按钮
	tradeCommon.priceUpCaseId = 'L03hzPrice';// 价格大写
	tradeCommon.priceTipId = 'L03bidTip';// 价格提示
	tradeCommon.timeTip = 'L03timeTip';// 价格提示
	tradeCommon.offerListId = 'L03OfferList';// 出价记录
	tradeCommon.initBtns();
	tradeL03Obj.html_refresh();
	tradeCommon.timer = setInterval("tradeL03Obj.html_refresh()", tradeCommon.intervalTime);
}

// ---------------------------------
// 停止
// --------------------------------
tradeL03Obj.destory = function() {
	clearInterval(tradeCommon.timer);
}
