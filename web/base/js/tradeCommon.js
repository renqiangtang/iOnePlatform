var tradeCommon = {};

tradeCommon.intervalTime = 1000;// 刷新频率为1000ms
tradeCommon.init = function() {
	// 页面控件
	tradeCommon.priceInputId = null;// 出价输入框
	tradeCommon.resetButtonId = null;// 复位按钮
	tradeCommon.addButtonId = null;// 添加按钮
	tradeCommon.reduceButtonId = null;// 减少按钮
	tradeCommon.priceUpCaseId = null;// 价格大写
	tradeCommon.tipId = null;// 提示
	tradeCommon.timeTipId = null;// 时间提示
	tradeCommon.offerListId = null;// 出价记录TBody
	// 页面数据
	tradeCommon.focusing = false;// 输入框有焦点
	tradeCommon.licenseId = null;// 竞买申请号
	tradeCommon.targetId = null;// 当前标的Id
	tradeCommon.targetName = null;// 宗地号
	tradeCommon.dealerName = null;// 竞买人名称
	tradeCommon.unit = null;// 单位
	tradeCommon.initPrice = null;// 起始价
	tradeCommon.stepPrice = null;// 阶梯
	tradeCommon.maxPrice = null;// 最高报价
	tradeCommon.mustPrice = null;// 应出价
	tradeCommon.sending = false;// 正在提交价格
	tradeCommon.timer = null;// 刷新定时器
	tradeCommon.ptype = null;// 阶段
	tradeCommon.qtype = null;// 指标
	tradeCommon.itemCount = 5;// 竞买记录
	tradeCommon.decimalNumber = null;// 小数位数
	tradeCommon.maxPriceUser = null;// 最高出价者
	tradeCommon.allowRefresh = false;// 刷新界面
}
// -----------------------------------------
// 数据加密
// -----------------------------------------
tradeCommon.encode = function() {
	var obj = {};
	obj.targetName = tradeCommon.targetName;
	obj.name = tradeCommon.dealerName;
	obj.price = $('#' + tradeCommon.priceInputId).val();
	obj.unit = tradeCommon.unit;
	obj.time = tradeCommon.stdTime;
	var str = '';
	try {
		str = JSON.stringify(obj);
		str = ca.encode(getUserCakey(), str);
	} catch (e) {
		// Utils.log(e);
		if (!Utils.debug) {
			var sss = '请插入Key!!!!';
			alert(sss);
			throw sss;
		}
	}
	if (!str)
		str = JSON.stringify(obj);
	return str;
}

tradeCommon.digitKey = [ 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 46, 96, 97,
		98, 99, 100, 101, 102, 103, 104, 105, 109, 110, 189 ];
tradeCommon.controlKey = [ 190, 8, 37, 39 ];

// -------------------------------------
// 处理手工输入的价格
// -------------------------------------
tradeCommon.dealCurPrice = function() {
	var value = $('#L02PriceInput').val();
	if (isNaN(value)) {
		var t = value.charAt(0);
		value = value.replace(/[^\d\.]/g, ''); // 先把非数字的都替换掉，除了数字和.
		value = value.replace(/^\./g, ''); // 必须保证第一个为数字而不是.
		value = value.replace(/\.{2,}/g, '.'); // 保证只有出现一个.而没有多个.
		value = value.replace('.', '$#$').replace(/\./g, '')
				.replace('$#$', '.');// 保证.只出现一次，而不能出现两次以上
		if (t == '-') {// 如果第一位是负号，则允许添加
			value = '-' + value;
		}
	}
	value = value + "";
	var decimalNumber = tradeL02Obj.decimalNumber;
	if (value.indexOf('.') > 0) {
		var result = value.substring(0, value.indexOf('.') + decimalNumber + 1);
		if ($('#' + tradeCommon.priceInputId).val() != result) {
			$('#' + tradeCommon.priceInputId).val(result);
		}
		return parseFloat(result);
	} else {
		if ($('#' + tradeCommon.priceInputId).val() != value) {
			$('#' + tradeCommon.priceInputId).val(parseFloat(value));
		}
		return parseFloat(value);
	}
}

// ----------------------------
// 出价控件 初始化
// ----------------------------
tradeCommon.initBtns = function() {
	//
	var priceInput = $$('#' + tradeCommon.priceInputId);
	var resetButton = $$('#' + tradeCommon.resetButtonId);
	var addButton = $$('#' + tradeCommon.addButtonId);
	var reduceButton = $$('#' + tradeCommon.reduceButtonId);
	var priceUpCase = $$('#' + tradeCommon.priceUpCaseId);

	// 只许输入数字
	priceInput.onkeydown = function(event) {
		var theEvent = window.event || event;
		var code = theEvent.keyCode || theEvent.which;
		var digitIndex = $.inArray(code, tradeCommon.digitKey);
		var controlIndex = $.inArray(code, tradeCommon.controlKey);
		var b = true;
		if ((controlIndex == -1) && (digitIndex == -1))
			b = false;
		try {
			if (!b) {
				theEvent.cancelBubble = true;
				theEvent.returnValue = false;
				theEvent.keyCode = 0;
			} else {
				return true;
			}
		} catch (ex) {
		}
		return false;
	};
	// 失去焦点
	$(priceInput).focusout(function() {
		tradeCommon.focusing = false;
	});
	// 得到焦点
	$(priceInput).focusin(function() {
		tradeCommon.focusing = true;
	});

	priceInput.onkeyup = priceInput.onchange = priceInput.onpropertychange = function() {
		var f = parseFloat($(priceInput).val());
		var p = f;
		if (tradeCommon.unit && tradeCommon.unit.indexOf('万') >= 0)
			p = f.mul(10000);
		$(priceUpCase).html(p.toUpcase());
	}

	resetButton.onclick = function(event) {
		tradeCommon.setPrice(tradeCommon.mustPrice);
	};

	addButton.onclick = function(event) {
		var f = parseFloat($(priceInput).val());
		var f1 = f.add(Math.abs(tradeCommon.stepPrice));
		tradeCommon.setPrice(f1);
	};

	reduceButton.onclick = function() {
		var f = parseFloat($(priceInput).val());
		var f1 = f.sub(Math.abs(tradeCommon.stepPrice));
		tradeCommon.setPrice(f1);
	};

}

// ------------------------
// 设置输入框价格
//
// ---------------------------
tradeCommon.setPrice = function(price) {
	if (!tradeCommon.focusing) {
		$('#' + tradeCommon.priceInputId).val(price);
		var p = price;
		if ('万元' == tradeCommon.unit)
			p = price.mul(10000);
		$('#' + tradeCommon.priceUpCaseId).html(p.toUpcase());
	}
}

// ------------------------
// 刷新输入框价格
//
// ---------------------------
tradeCommon.refreshPrice = function() {
	var v = $('#' + tradeCommon.priceInputId).val();
	if (v.isNumber()) {
		var f = parseFloat(v);
		if (tradeCommon.stepPrice > 0 && f >= tradeCommon.mustPrice)
			return;
		if (tradeCommon.stepPrice < 0 && f <= tradeCommon.mustPrice)
			return;
		if (tradeCommon.stepPrice == 0)
			return;
	}
	tradeCommon.setPrice(tradeCommon.mustPrice);
}

// ------------------------
// 检查报价
// beginPrice, stepPrice, maxPrice, curPrice
// ---------------------------
tradeCommon.checkPrice = function() {
	var info = {};
	info.flag = true;
	var curPrice = parseFloat($('#' + tradeCommon.priceInputId).val());
	var beginPrice = tradeCommon.beginPrice;
	var stepPrice = tradeCommon.stepPrice;
	var maxPrice = tradeCommon.maxPrice;
	var unit = tradeCommon.unit;
	if (stepPrice > 0) {
		if (maxPrice == null && curPrice < beginPrice) {
			info.msg = '第一次报价不能小于起始价';
			info.falg = false;
			return info;
		}
		if (maxPrice != null && curPrice < maxPrice.add(stepPrice)) {
			info.msg = curPrice.addComma() + unit + '未达到当前最高报价的1倍出价阶梯';
			info.flag = false;
			return info;
		}
		if (maxPrice != null && curPrice >= maxPrice.add(stepPrice.mul(5))) {
			info.msg = curPrice.addComma() + unit + '出价已达到最高报价5倍出价阶梯,确认出价？';
			return info;
		}
	}

	if (stepPrice < 0) {
		if (maxPrice == null && curPrice > beginPrice) {
			info.msg = '第一次报价不能大于起始价';
			info.flag = false;
			return info;
		}

		if (maxPrice != null && curPrice > maxPrice.add(stepPrice)) {
			info.msg = curPrice.addComma() + unit + '出价未达到最高报价的1倍出价阶梯';
			info.flag = false;
			return info;
		}

		if (maxPrice != null && curPrice <= maxPrice.add(stepPrice.mul(5))) {
			info.msg = curPrice.addComma() + unit + '出价已达到最高报价5倍出价阶梯,确认出价？';
			return info;
		}
	}

	return info;
}

// --------------------------------------
// 竞价记录
// --------------------------------------
tradeCommon.showOffer = function(data) {
	var html = '';
	for ( var i = 0; i < data.list.rs.length; i++) {
		var offerStatus = Utils.getRecordValue(data.list, i, "status");
		var trFontColor = offerStatus == "2" ? 'style= "color:red"' : '';
		var licenseId = Utils.getRecordValue(data.list, i, "licenseid");
		var mylicenseId = data.target.licenseId;
		if (licenseId == mylicenseId)
			trFontColor = 'style="color:blue"';
		html += '<tr ' + trFontColor + ' >';
		html += '<td height="24" align="center">'
				+ Utils.getRecordValue(data.list, i, "bidtimes") + '</td>';
		if (licenseId == mylicenseId)
			html += '<td align="center">' + tradeCommon.dealerName + '</td>';
		else
			html += '<td align="center">'
					+ Utils.getRecordValue(data.list, i, "licenseno") + '</td>';
		html += '<td align="center">'
				+ Utils.getRecordValue(data.list, i, "offerdate") + '</td>';
		html += '<td align="center">'
				+ Utils.getRecordValue(data.list, i, "price");
		if(Utils.getRecordValue(data.list,i,"is_trust"))
			html+='(委托报价)';
		html += '</td>';
		html += '<td style="display:none">&nbsp;</td>';
		html += '</tr>';
	}
	$('#' + tradeCommon.offerListId).html(html);
}

// --------------------------------------
// 竞价记录 多指标
// --------------------------------------
tradeCommon.showOffer2 = function(data) {
	var html = '';
	for ( var i = 0; i < data.list.rs.length; i++) {
		var offerStatus = Utils.getRecordValue(data.list, i, "status");
		var trFontColor = offerStatus == "2" ? 'style= "color:red"' : '';
		var licenseId = Utils.getRecordValue(data.list, i, "licenseid");
		var mylicenseId = data.target.licenseId;
		if (licenseId == mylicenseId)
			trFontColor = 'style="color:blue"';
		html += '<tr ' + trFontColor + ' >';
		html += '<td height="24" align="center">'
				+ Utils.getRecordValue(data.list, i, "bidtimes") + '</td>';
		if (licenseId == mylicenseId)
			html += '<td align="center">' + tradeCommon.dealerName + '</td>';
		else
			html += '<td align="center">'
					+ Utils.getRecordValue(data.list, i, "licenseno") + '</td>';
		html += '<td align="center">'
				+ Utils.getRecordValue(data.list, i, "offerdate") + '</td>';
		html += '<td align="left">'
				+ Utils.getRecordValue(data.list, i, "price") + '</td>';
		html += '</tr>';
	}
	$('#' + tradeCommon.offerListId).html(html);
}

// --------------------------------------
// 竞价记录耕地指标
// --------------------------------------
tradeCommon.showOfferPlow = function(data) {
	var html = '';
	for ( var i = 0; i < data.list.rs.length; i++) {
		var offerStatus = Utils.getRecordValue(data.list, i, "status");
		var trFontColor = offerStatus == "2" ? 'style= "color:red"' : '';
		html += '<tr ' + trFontColor + ' >';
		html += '<td height="24" align="center">'
				+ Utils.getRecordValue(data.list, i, "bidtimes") + '</td>';
		var licenseId = Utils.getRecordValue(data.list, i, "licenseid");
		var mylicenseId = data.target.licenseId;
		if (licenseId == mylicenseId)
			html += '<td align="center">' + data.target.biddername + '</td>';
		else
			html += '<td align="center">'
					+ Utils.getRecordValue(data.list, i, "licenseno") + '</td>';
		html += '<td align="right">'
				+ Utils.getRecordValue(data.list, i, "price") + '</td>';
		html += '<td align="center">'
				+ Utils.getRecordValue(data.list, i, "offerdate") + '</td>';
		if (Utils.getRecordValue(data.list, i, "is_trust")) {
			html += '<td align="center">自动报价</td>';
		} else {
			html += '<td align="center">手工报价</td>';
		}
		html += '</tr>';
	}
	$('#' + tradeCommon.offerListId).html(html);
}

// --------------------------------------
// 竞价记录更多
// --------------------------------------
tradeCommon.showOfferLogList = function(targetId) {
	var param = {};
	if (targetId)
		param.targetId = targetId;
	else if (tradeCommon.targetId)
		param.targetId = tradeCommon.targetId;
	else if (tradeCommon.licenseId)
		param.licenseId = tradeCommon.licenseId;
	else if (tradeCommon.bidderId)
		param.bidderId = tradeCommon.bidderId;
	DialogModal({
		url : approot + '/' + comObj.getGoodsUrlPath()
				+ '/trade/offerLogList.html',
		param : param,
		feature : "dialogWidth=850px;dialogHeight=550px"
	});
}

// --------------------------------------------
// 显示3个时间(集中报价开始时间 集中报价截止时间 限时竞拍开始时间)
// --------------------------------------------
tradeCommon.displayTime = function(data) {
	var ptype = tradeMain.curTarget.ptype;
	if (data.target.trans_type == 1) {//拍卖
		$('#' + ptype + 'FocusBeginTimeDiv').hide();
		$('#' + ptype + 'FocusEndTimeDiv').hide();
		$('#' + ptype + 'LimitBeginTimeDiv').show();
		$('#' + ptype + 'LimitBeginTime').html(data.target.begin_limit_time);
	} else {
		$('#' + ptype + 'FocusBeginTimeDiv').show();
		$('#' + ptype + 'FocusEndTimeDiv').show();
		$('#' + ptype + 'LimitBeginTimeDiv').show();
		$('#' + ptype + 'FocusBeginTime').html(data.target.begin_focus_time);
		$('#' + ptype + 'FocusEndTime').html(data.target.end_focus_time);
		$('#' + ptype + 'LimitBeginTime').html(data.target.begin_limit_time);
	}
}

//----------------------------------------
//显示有无底价
//--------------------------------------
tradeCommon.reserveInfo = function(data){
	var html='';
	if(data.target.isReserve)
		html='[有底价]';
	return html;
}
