var pageObj = {};
pageObj.targetId = null;
pageObj.goodsArr = null;
pageObj.transTypeObj = {};
pageObj.config = null;
pageObj.transTypeArr = null;
pageObj.notModifyMode = "update";
pageObj.conFirm=0;//等于1时表示审核
pageObj.isrr=-1;//0表示当前审核人点击标识正确 1表示点了标识错误
pageObj.noticestatus=0;//公告状态
pageObj.bailAdd = function() {
	if (pageObj.mode == pageObj.notModifyMode) {
		DialogAlert('不能修改！');
		return;
	}
	var grid = pageObj.bailGrid;
	var row = grid.getSelectedRow();
	grid.addRow({
		'type' : 'CNY',
		'amount' : 0,
		'unit' : 'CNY'
	}, row);
}

pageObj.bailDel = function() {
	if (pageObj.mode == pageObj.notModifyMode) {
		DialogAlert('不能修改！');
		return;
	}
	DialogConfirm('确实需要删除已选择的数据?', function(yn) {
		if (!yn)
			return;
		var grid = pageObj.bailGrid;
		grid.deleteSelectedRow();
	});
}

pageObj.quotasAdd = function(obj1, obj2) {
	if (pageObj.mode == pageObj.notModifyMode) {
		DialogAlert('不能修改！');
		return;
	}
	var grid = pageObj.quotasGrid;
	var length = grid.getData(null, true).length;
	var obj = {};
	if (obj2)
		obj = obj2;
	obj.turn = length + 1;
	obj.enter_flag1 = 0;
	obj.enter_flag2 = 2;
	obj.enter_flag3 = 0;
	obj.first_wait = 300;
	obj.limit_wait = 300;
	obj.last_wait = 600;
	obj.final_value = '';
	grid.addRow(obj);
}

pageObj.quotasDel = function() {
	if (pageObj.mode == pageObj.notModifyMode) {
		DialogAlert('不能修改！');
		return;
	}
	DialogConfirm('确实需要删除已选择的数据?', function(yn) {
		if (!yn)
			return;
		var grid = pageObj.quotasGrid;
		grid.deleteSelectedRow();
	});
}

pageObj.businessTypeChange = function() {
	var html = "";
	var curr_business_type = $('#business_type').val();
	var arr = pageObj.transTypeArr;
	$("#trans_type_id").html("");
	for ( var i = 0; i < arr.length; i++) {
		if (arr[i].business_type_rel_id == curr_business_type) {
			Utils.addOption('trans_type_id', arr[i].id, arr[i].name);
		}
		pageObj.transTypeObj[arr[i].id] = arr[i];
	}
	pageObj.transTypeChange();
}

pageObj.transTypeChange = function() {
	//
	var obj = pageObj.transTypeObj[$('#trans_type_id').val()];
	if (!obj)
		return;
	//
	if (obj.is_limit_trans)
		$('#begin_limit_time').parents('tr').show();
	else
		$('#begin_limit_time').parents('tr').hide();
	//
	switch (obj.trans_type) {
	case 0:// 挂牌
		$('#begin_list_time').parents('tr').show();
		$('#begin_focus_time').parents('tr').show();
		break;
	case 1:// 拍卖
		$('#begin_list_time').parents('tr').hide();
		$('#begin_focus_time').parents('tr').hide();
		$('#beginLimitTimeTd').parents('tr').show();
		break;
	}
}

pageObj.boundGoods = function() {
	var obj = {};
	// 地址
	obj.url = "goodsList.html";
	// 参数
	obj.param = {
		u : Utils.getPageValue('u'),
		mode : comObj.mode['view'],
		targetId : pageObj.targetId,
		goodsType : pageObj.goodsType
	};
	// 窗口参数
	obj.feature = "dialogWidth=900px;dialogHeight=550px";
	var returnValue = DialogModal(obj);
	if (returnValue) {
		pageObj.refresh();
		DialogAlert('绑定地块成功！');
//		pageObj.query();
	}
}

pageObj.initPage = function() {
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsLand";
	cmd.method = "initTargetEditPageParam";
	cmd.u = pageObj.u;
	cmd.goodsType = pageObj.goodsType;
	cmd.success = function(data) {
		pageObj.quotasType = data.quotasType;
		pageObj.config = data.config;
		//
		var arr = data.businessType;
		for ( var i = 0; i < arr.length; i++) {
			Utils.addOption('business_type', arr[i].id, arr[i].name);
		}
		$('#business_type').change(pageObj.businessTypeChange);
		//
		var curr_business_type = $('#business_type').val();
//		Utils.addOption('trans_type_id', '', '--请选择--');
		arr = data.transType;
		pageObj.transTypeArr = data.transType;
		for ( var i = 0; i < arr.length; i++) {
			if (arr[i].business_type_rel_id == curr_business_type) {
				Utils.addOption('trans_type_id', arr[i].id, arr[i].name);
			}
			$('#trans_type_id').val('107');
			pageObj.transTypeObj[arr[i].id] = arr[i];
		}
		$('#trans_type_id').change(pageObj.transTypeChange);
		pageObj.transTypeChange();
	}
	cmd.execute();
}

pageObj.refresh = function() {
	if (pageObj.targetId) {
		var cmd = new Command();
		cmd.module = "before";
		cmd.service = "TransGoodsLand";
		cmd.method = "initTargetEditData";
		cmd.targetId = pageObj.targetId;
		cmd.tabName = "target";
		cmd.success = function(data) {
			$('#business_type').val(data.target.business_type);
			pageObj.businessTypeChange();
			$('#trans_type_id').val(data.target.trans_type_id);
			pageObj.transTypeChange();
			
			
			Utils.setForm(data.target);
			var nav = '';
			var goods = data.goods;
			pageObj.goodsArr = [];
			$(goods).each(function(index, ele) {
				pageObj.goodsArr.push(ele.id);
				var canid="";
				if(ele.canton_id=="null" || ele.canton_id==undefined ){
					canid="";
				}else{
					canid=ele.canton_id;
				}
				nav += '<tr><td colspan="2" class="tc l-grid-hd-cell"><label class="c2 chand" onclick="javascript:comObj.viewGoods(\'' + ele.id + '\',\'' + pageObj.noticestatus + '\',\'' + pageObj.conFirm + '\')">' + ele.no + '</label></td><td colspan="2" class="tc l-grid-hd-cell">' + canid+ '</td><td colspan="2" class="tc l-grid-hd-cell"><label class="c2 chand" onclick="javascript:pageObj.delRelGoods(\'' + ele.id + '\')">解除捆绑</label></td></tr>';
			});
			$("#trzdxx").html('');
			$("#trzdxx").html(nav);
			$('#targetName').html(data.target.no);
			// /加载数据后将起始价和增价幅度设置为万元
			var bp = parseFloat($("#begin_price").val());
			var ps = parseFloat($("#price_step").val());
			var rp = parseFloat($("#reserve_price").val());
			$("#begin_price").val(bp.toWanYuan());
			$("#price_step").val(ps.toWanYuan());
			$("#reserve_price").val(rp.toWanYuan());
			if ($("#trans_price").val() != "") {
				var tp = parseFloat($("#trans_price").val());
				$("#trans_price").val(tp.toWanYuan());// 成交价
			}
			if(pageObj.noticestatus!=null){
				if((pageObj.noticestatus==3 && pageObj.conFirm==1) || (pageObj.noticestatus==4 && (pageObj.conFirm==0 || pageObj.conFirm==null))){
					var confirmData=data.noticeConfirmData;
					comObj.setConfirm(confirmData);
				}
			}

		}
		cmd.execute();

	}
}

pageObj.delRelGoods = function(goodsId) {
	if (goodsId == ""){
		DialogAlert('请选择一条数据进行解除捆绑!');
		return;
	}
	if (!confirm("确定解除这个地块的捆绑?"))
		return;
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsLand";
	cmd.method = "doUnBoundGoods";
	cmd.targetId = pageObj.targetId;
	cmd.goodsIds = goodsId;
	cmd.success = function(data) {
		DialogAlert(data.message);
		pageObj.refresh();
	}
	cmd.execute();

}

pageObj.delGoods = function(goodsId, goodsName) {
	if (!confirm("确定解除[" + goodsName + "]捆绑?"))
		return;
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsLand";
	cmd.method = "doUnBoundGoods";
	cmd.targetId = pageObj.targetId;
	cmd.goodsIds = goodsId;
	cmd.success = function(data) {
		DialogAlert(data.message);
		pageObj.refresh();
	}
	cmd.execute();

}

pageObj.quotasCheck = function() {
	var quotasArr = pageObj.quotasGrid.getData(null, true);
	// if (quotasArr.length > 0) {
	// var vv = $('#final_price').val().trim();
	// if (vv.length == 0) {
	// DialogAlert("有多指标封顶价不能为空!");
	// $('#final_price').focus();
	// return false;
	// }
	// }
	var typeArr = [];
	var nameArr = [];
	var turnArr = [];
	var maxturn = 0;
	for ( var i = 0; i < quotasArr.length; i++) {
		var turnStr = quotasArr[i].turn + '';
		if ($.inArray(turnStr, turnArr) > -1) {
			DialogAlert("多指标:第" + (i + 1) + "行序号重复!");
			return false;
		}
		if (turnStr.isNumber()) {
			var turn = parseFloat(turnStr);
			if (turn > maxturn)
				maxturn = turn;
		}
		turnArr.push(turnStr);
	}
	for ( var i = 0; i < quotasArr.length; i++) {
		var initValueStr = quotasArr[i].init_value + '';// 防止数字自动转换
		var stepStr = quotasArr[i].step + '';
		var finalValueStr = quotasArr[i].final_value + '';
		var initValue = null;
		var step = null;
		var finalValue = null;
		var turn = parseFloat(quotasArr[i].turn + '');
		// 不是最后的最终价必须填写
		if (turn != maxturn && finalValueStr.length == 0) {
			DialogAlert("多指标:第" + (i + 1) + "行最终价不能为空!");
			return false;
		}
		// 起始价
		if (!initValueStr.isNumber()) {
			DialogAlert("多指标:第" + (i + 1) + "行起始价不为数字!");
			return false;
		}
		initValue = parseFloat(initValueStr);
		if (!initValue.nDigits(2)) {
			DialogAlert("多指标:第" + (i + 1) + "行起始价最多保留2位小数!");
			return false;
		}

		// 增价幅度
		if (!stepStr.isNumber()) {
			DialogAlert("多指标:第" + (i + 1) + "行增价幅度不为数字!");
			return false;
		}
		step = parseFloat(stepStr);
		if (!initValue.nDigits(2)) {
			DialogAlert("多指标:第" + (i + 1) + "行增价幅度最多保留2位小数!");
			return false;
		}
		if (step == 0) {
			DialogAlert("多指标:第" + (i + 1) + "行增价幅度不能为0!");
			return false;
		}

		// 最终价
		if (!finalValueStr.isNumber() && turn != maxturn) {
			DialogAlert("多指标:第" + (i + 1) + "行最终价不为数字!");
			return false;
		}
		finalValue = parseFloat(finalValueStr);
		if (!initValue.nDigits(2)) {
			DialogAlert("多指标:第" + (i + 1) + "行最终价最多保留2位小数!");
			return false;
		}

		//
		if (step > 0 && finalValue != null && initValue != null && finalValue <= initValue) {
			DialogAlert("多指标:第" + (i + 1) + "行增价幅度为正数,最终价必须大于起始价!");
			return false;
		}
		if (step < 0 && finalValue != null && initValue != null && finalValue >= initValue) {
			DialogAlert("多指标:第" + (i + 1) + "行增价幅度为负数,最终价必须小于起始价!");
			return false;
		}
		if ($.inArray(quotasArr[i].name, nameArr) >= 0) {
			DialogAlert("多指标:第" + (i + 1) + "行指标名称重复!");
			return false;
		}
		if ($.inArray(quotasArr[i].type, typeArr) >= 0) {
			DialogAlert("多指标:第" + (i + 1) + "行指标类型重复!");
			return false;
		}
		nameArr.push(quotasArr[i].name);
		typeArr.push(quotasArr[i].type);
	}
	return true;
}

// --------------------------
// 时间比较
// ---------------------------
pageObj.timeCheck = function() {
	var arr = [];
	arr.push({
		tag : 'notice',
		type : '公告'
	});
	arr.push({
		tag : 'apply',
		type : '竞买申请'
	});
	arr.push({
		tag : 'earnest',
		type : '保证金'
	});
	arr.push({
		tag : 'list',
		type : '挂牌'
	});
	arr.push({
		tag : 'focus',
		type : '集中报价'
	});
	for ( var i = 0; i < arr.length; i++) {
		var beginId = 'begin_' + arr[i].tag + '_time';
		var endId = 'end_' + arr[i].tag + '_time';
		var msg = '[' + arr[i].type + ']截止时间必须大于开始时间！';
		var display = $('#' + beginId).parents('tr').css('display');
		if ('none' != display) {
			var b = pageObj.timeCompare(beginId, endId, msg, ">");
			if (!b)
				return false;
		}
	}
	//
	var display = $('#end_focus_time').parents('tr').css('display');
	if ('none' != display) {
		var b = pageObj.timeCompare('end_focus_time', 'begin_limit_time', '[限时竞拍开始时间]不能小于[集中报价截止时间]！', '>');
		if (!b)
			return false;
	}
	//
	var obj = pageObj.transTypeObj[$('#trans_type_id').val()];
	switch (obj.trans_type) {
	case 0:// 挂牌
		var b = pageObj.timeCompare('begin_notice_time', 'begin_list_time', '[挂牌开始时间]必须大于[公告开始时间]！', '>=');
		if (!b)
			return false;
		b = pageObj.timeCompare('begin_notice_time', 'begin_focus_time', '[集中报价开始时间]必须大于[公告开始时间]！', '>=');
		if (!b)
			return false;
		break;
	case 1:// 拍卖
		var b = pageObj.timeCompare('begin_notice_time', 'begin_limit_time', '[拍卖(限时竞价、现场拍卖)开始时间]必须大于[公告开始时间]！', '>=');
		if (!b)
			return false;
		break;
	}
	return true;

}

pageObj.timeCompare = function(beginTimeId, endTimeId, msg, flag) {
	var btstr = $('#' + beginTimeId).val();
	var etstr = $('#' + endTimeId).val();
	if (btstr && etstr) {
		var bt = btstr.toDate().getTime();
		var et = etstr.toDate().getTime();
		switch (flag) {
		case '>':
			if (et < bt) {
				DialogAlert(msg);
				$('#' + endTimeId).focus();
				return false;
			}
			break;
		case '>=':
			if (et <= bt) {
				DialogAlert(msg);
				$('#' + endTimeId).focus();
				return false;
			}
			break;
		}
	}
	return true;
}

pageObj.priceCheck = function() {
	var step = parseFloat($('#price_step').val().trim());
	var beginPrice = parseFloat($('#begin_price').val().trim());
	if (step == 0) {
		DialogAlert('价款信息：增价幅度不能为0!');
		return false;
	}
	if (!beginPrice.nDigits(4)) {
		DialogAlert("起始价最多保留4位小数!");
		$('#begin_price').focus();
		return false;
	}
	if (!step.nDigits(4)) {
		DialogAlert("增价幅度最多保留4位小数!");
		$('#price_step').focus();
		return false;
	}
//	var reserve_price = parseFloat($('#reserve_price').val().trim());
//	if (!reserve_price.nDigits(4)) {
//		DialogAlert("底价最多保留4位小数!");
//		$('#reserve_price').focus();
//		return false;
//	}

	return true;
}
// --------------------------
// 保证金
// ---------------------------
pageObj.bailCheck = function() {
	var arr = pageObj.bailGrid.getData(null, true);
	if (arr.length == 0) {
		DialogAlert('保证金不能为空！');
		return false;
	}
	var nameArr = [];
	for ( var i = 0; i < arr.length; i++) {
		if (arr[i].amount == null || arr[i].amount == "") {
			DialogAlert('保证金:第' + (i + 1) + '行不能为空!');
			return false;
		}
		if ($.inArray(arr[i].type, nameArr) > -1) {
			DialogAlert('保证金:第' + (i + 1) + '行币种重复!');
			return false;
		}
		var amount = arr[i].amount;
		if (isNaN(amount)) {
			DialogAlert('保证金:第' + (i + 1) + '行不为数字!');
			return false;
		}
		amount = parseFloat(amount);
		if (amount < 0) {
			DialogAlert('保证金:第' + (i + 1) + '行不能小于0!');
			return false;
		}
		if (!amount.nDigits(4)) {
			DialogAlert('保证金:第' + (i + 1) + '行小数位最多为4位!');
			return false;
		}
		nameArr.push(arr[i].type);
	}
	return true;
}

pageObj.save = function() {
	// 必须项
	if (!Utils.requiredCheck()) {
		return;
	}
	// 保证金
	if (!pageObj.bailCheck()) {
		return;
	}
	// 价格
	if (!pageObj.priceCheck()) {
		return;
	}
	// 多指标
	if (!Utils.requiredCheckForGrid(pageObj.quotasGrid, '多指标信息')) {
		return;
	}
	// 多指标
	if (!pageObj.quotasCheck()) {
		return;
	}
	// 时间
	if (!pageObj.timeCheck()) {
		return;
	}
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsLand";
	cmd.method = "saveTransTarget";
	if (pageObj.mode == "update") {
		cmd.method = "updateBidingTarget";
	}
	cmd.targetId = pageObj.targetId;
	cmd.mode = pageObj.mode;
	// 提交前将起始价和增价幅度设置为元
	var bp = parseFloat($("#begin_price").val());
	var ps = parseFloat($("#price_step").val());
	var rp = parseFloat($("#reserve_price").val());
	cmd.unit = "万元";
	$("#begin_price").val(bp.toYuan());
	$("#price_step").val(ps.toYuan());
	$("#reserve_price").val(rp.toYuan());
	Utils.getForm(cmd);
	$("#begin_price").val(bp);
	$("#price_step").val(ps);
	$("#reserve_price").val(rp);
	var gridData = pageObj.bailGrid.getData(null, true);
	// 将保证金的万元转换成转后存数据库
	for ( var i = 0; i < gridData.length; i++) {
		var selectData = gridData[i];
		if (gridData[i].type == 'CNY' || gridData[i].type == 'RMB') {
			gridData[i].amount = parseFloat(gridData[i].amount).toYuan();
		}
	}
	// 保证金
	cmd.bailsJson = JSON.stringify(gridData);
	// 多指标
	cmd.quotasJson = JSON.stringify(pageObj.quotasGrid.getData(null, true));
	cmd.success = function(data) {
		if ('targetId' in data) {
			pageObj.targetId = data.targetId;
			if(pageObj.targetId!=null && pageObj.targetId!=""){
				$("#relGoods").removeAttr("disabled");
				$("#relAttach").removeAttr("disabled");
			}else{
				$("#relGoods").attr('disabled',"true");
				$("#relAttach").attr('disabled',"true");
			}
		}
		pageObj.refreshButtons();
		DialogAlert(data.message);
		if (data.state)
			window.returnValue = true;
		// 提交后将起始价和增价幅度设置为万元
//		var bp = parseFloat($("#begin_price").val());
//		var ps = parseFloat($("#price_step").val());
//		var rp = parseFloat($("#reserve_price").val());
//
//		$("#begin_price").val(bp.toWanYuan());
//		$("#price_step").val(ps.toWanYuan());
//		$("#reserve_price").val(rp.toWanYuan());

	};
	cmd.execute();
}

pageObj.refreshButtons = function() {
	// $('#addImg').val('新增' + comObj.goods_name[pageObj.goodsType]);
	// $('#boundImg').val('绑定' + comObj.goods_name[pageObj.goodsType]);
	// if (pageObj.targetId) {
	// $('#addImg').removeAttr('disabled');
	// $('#boundImg').removeAttr('disabled');
	// } else {
	// $('#addImg').attr('disabled', 'disabled');
	// $('#boundImg').attr('disabled', 'disabled');
	// }

}

pageObj.addGoods = function() {
	// if ($('#addImg').attr('disabled')) {
	// DialogAlert('标的信息还未保存,请先保存标的信息！');
	// return;
	// }
	var obj = {};
	// 地址
	obj.url = pageObj.goodsType + ".html";
	// 参数
	obj.param = {
		mode : 'new',
		u : Utils.getPageValue('u'),
		targetId : pageObj.targetId,
		callback : window.pageObj.fresh
	};
	// 窗口参数
	obj.feature = "dialogWidth=900px;dialogHeight=600px";
	var rv = DialogModal(obj);
	if (rv)
		pageObj.refresh();
}

pageObj.initAttachList = function() {
	var initWidth = 978;
	var initHeight = 430;
	var grant = {
		add : 2,
		edit : 2,
		del : 2,
		addDtl : 2,
		editDtl : 2,
		delDtl : 2,
		uploadFile : 2,
		downloadFile : 2,
		delFile : 2
	};
	var owners = new Array();
	var owner = {};
	owner.id = pageObj.targetId;
	owner.name = $('#targetName').html();
	owner.title = $('#targetName').html();
	owner.tableName = "trans_target";
	owner.templetNo = "businessType" + $("#business_type").val() + "TargetAttach";
	owner.grant = grant;
	owners.push(owner);

	obj.param = {};
	obj.param.u = pageObj.u;
	obj.param.owners = encodeURIComponent(JSON.stringify(owners));

	// 地址
	obj.url = approot + '/sysman/attachList.html';

	// 窗口参数
	obj.feature = "dialogWidth=900px;dialogHeight=600px";
	var rv = DialogModal(obj);

	// if (attachFrame.length == 0) {
	// $('#attachPanel').html('<iframe id="attachFrame" frameborder="0" src="' +
	// url + '" style="height:' + initHeight + 'px;width:'
	// + tableWidth + 'px"></iframe>');
	// } else {
	// attachFrame.attr('src', url);
	// }

}

pageObj.afterCheck = function(goodsType, isAfter) {
	if (isAfter == null || isAfter == "" || isafter == "null") {
		isAfter = goodsType == "101" ? 1 : 0;
	}
	$("input[name='is_after_check'][value=" + isAfter + "]").attr("checked", true);
}

pageObj.showModelGoods = function() {
	var obj = {};
	// 地址
	obj.url = 'goodsList.html';
	// 参数
	obj.param = {
		u : pageObj.u,
		mode : 'relView',
		targetId : pageObj.targetId,
		goodsType : '101'
	};
	// 窗口参数
	var sheight = screen.height - 230;
	var swidth = screen.width - 250;
	obj.feature = 'dialogWidth=' + swidth + 'px;dialogHeight=' + sheight + 'px';
	var returnValue = DialogModal(obj);
	if (returnValue != "")
		pageObj.refresh();
}

pageObj.toUpCase = function() {
	try {
		var vstr = $(this).val();
		if (vstr.isNumber()) {
			var v = parseFloat(vstr);
			var x = v.toYuan();
			$('#UCase').html(x.toUpcase());
		} else
			$('#UCase').html('');
	} catch (ex) {

	}
}

pageObj.begin_notice_timeChange = function() {
	// end_focus_time: 20 end_list_time: 20 end_notice_time: 30
	var begin_notice_time = $('#begin_notice_time').val();
	if (begin_notice_time) {
		var obj = pageObj.transTypeObj[$('#trans_type_id').val()];
		//
		if (obj.end_notice_time && $('#end_notice_time').val().length == 0)
			$('#end_notice_time').val(begin_notice_time.addDays(obj.end_notice_time));
		//
		if ($('#begin_apply_time').val().length == 0)
			$('#begin_apply_time').val(begin_notice_time);
		//
		if ($('#end_apply_time').val().length == 0) {
			var d1 = begin_notice_time.addDays(obj.end_notice_time).toDate();
			d1.setHours(16, 30, 0);
			$('#end_apply_time').val(d1);
		}
		//
		if ($('#begin_earnest_time').val().length == 0)
			$('#begin_earnest_time').val(begin_notice_time);
		//
		if ($('#end_earnest_time').val().length == 0) {
			var d1 = begin_notice_time.addDays(obj.end_notice_time).toDate();
			d1.setHours(17, 30, 0);
			$('#end_earnest_time').val(d1);
		}
		//
		var end_notice_time = $('#end_notice_time').val();
		if ($('#begin_list_time').val().length == 0)
			$('#begin_list_time').val(end_notice_time.addDays(-10));
		//
		if ($('#begin_focus_time').val().length == 0)
			$('#begin_focus_time').val(end_notice_time.addDays(-10));
		//
		var begin_list_time = $('#begin_list_time').val();
		if (obj.end_list_time && $('#end_list_time').val().length == 0)
			$('#end_list_time').val(begin_list_time.addDays(obj.end_list_time));
		//
		var begin_focus_time = $('#begin_focus_time').val();
		if (obj.end_notice_time && $('#end_focus_time').val().length == 0)
			$('#end_focus_time').val(begin_focus_time.addDays(obj.end_list_time));
		//
		if ($('#begin_limit_time').val().length == 0) {
			switch (obj.trans_type) {
			case 0:// 挂牌
				$('#begin_limit_time').val($('#end_focus_time').val());
				break;
			case 1:// 拍卖
				$('#begin_limit_time').val($('#end_earnest_time').val());
				break;
			}
		}

		//
	} else {
		$('#end_notice_time').val('');
		$('#begin_apply_time').val('');
		$('#end_apply_time').val('');
		$('#begin_earnest_time').val('');
		$('#end_earnest_time').val('');
		$('#begin_list_time').val('');
		$('#end_list_time').val('');
		$('#begin_focus_time').val('');
		$('#end_focus_time').val('');
		$('#begin_limit_time').val('');
	}
}

window.onbeforeunload = function() {
	window.returnValue = true;
}

pageObj.disabledInput = function(fields, type) {
	if (type == "yes") {
		if (fields != null) {
			var fieldsArray = fields.split(",");
			if (fieldsArray != null && fieldsArray.length > 0) {
				for ( var i = 0; i < fieldsArray.length; i++) {
					$('#' + fieldsArray[i]).attr("disabled", "disabled");
				}
			}
		}
	} else if (type == "not") {
		fields = ',' + fields + ',';
		var inputTypes = new Array("input", "select", "radio", "textarea");
		for ( var k = 0; k < inputTypes.length; k++) {
			var inputArray = $(inputTypes[k]);
			inputArray.each(function() {
				var input = $(this);
				var tempId = input.attr("id");
				if (fields.indexOf(tempId) < 0) {
					$('#' + tempId).attr("disabled", "disabled");
				}
			})
		}

	}
}

pageObj.bodyClick = function(e){
	comObj.clickBody(e,pageObj.targetId,"target");
}


$(document).ready(function() {
	var date1 = new Date();
	pageObj.mode = Utils.getPageValue('mode');
	pageObj.targetId = Utils.getPageValue('targetId');
	pageObj.u = Utils.getPageValue('u');
	pageObj.goodsType = Utils.getPageValue('goodsType');
	pageObj.conFirm=Utils.getPageValue('conFirm');//获取是否审核页面
	pageObj.noticestatus=Utils.getPageValue('noticestatus');//公告状态
	
	pageObj.afterCheck(pageObj.goodsType, null);
	pageObj.initPage();
	pageObj.refresh();
	pageObj.refreshButtons();
	if(pageObj.noticestatus!=null && pageObj.noticestatus==3){
		if(pageObj.conFirm==null || pageObj.conFirm==0){//如果不是审核
			$("#rightBtn").hide();
			$("#errorBtn").hide();
		}else{
			$("#rightBtn").show();
			$("#errorBtn").show();
			$("#rightBtn").click(function(){
				pageObj.isrr=0;
			});
			$("#errorBtn").click(function(){
				pageObj.isrr=1;
			});
			$("body").click(pageObj.bodyClick);
		}
	}else{
		$("#rightBtn").hide();
		$("#errorBtn").hide();
	}
	
	
	$('#saveImg').click(pageObj.save);
//	$('#relGoods').click(pageObj.showModelGoods);
	$('#relGoods').click(pageObj.boundGoods);
	
	$('#relAttach').click(pageObj.initAttachList);
	//
	switch (pageObj.mode) {
	case pageObj.notModifyMode:
		pageObj.disabledInput("reserve_price,price_step,field9,field8,field7,saveImg", "not");
		break;
	default:
		$('#trans_price').attr("disabled", "disabled");
		break;
	}
	// 多币种
	pageObj.bailGrid = $('#bail').ligerGrid({
		columns : [{
			display : '币种',
			width : 100,
			name : 'type',
			editor : {
				type : 'select',
				data : comObj.CurrencyList,
				valueColumnName : 'enName',
				displayColumnName : 'cnName'
			},
			render : function(item) {
				return comObj.Currency[item.type];
			}
		}, {
			display : '金额',
			width : 200,
			name : 'amount',
			editor : {
				type : 'text'
			}
		}, {
			display : '单位',
			width : 100,
			name : 'unit',
			// editor : {
			// type : 'select',
			// data : comObj.CurrencyUnitList,
			// valueColumnName : 'uenName',
			// displayColumnName : 'ucnName'
			// },
			render : function(item) {
				return comObj.CurrencyUnit[item.type];
			}
		} , {
			display : '备注',
			width : 300,
			name : 'remark',
			// editor : {
			// type : 'select',
			// data : comObj.CurrencyUnitList,
			// valueColumnName : 'uenName',
			// displayColumnName : 'ucnName'
			// },
			render : function(item) {
				if(item.type!='CNY' && item.type!='cny'){
					return "按交款当日汇率计算出来的人民币金额";
				}else{
					return "";
				}
			}
		}],
		toolbar : {
			items : [ {
				text : '增加',
				click : pageObj.bailAdd,
				icon : 'add'
			}, {
				line : true
			}, {
				text : '删除',
				click : pageObj.bailDel,
				icon : 'delete'
			} ]
		},
		rownumbers : true,
		enabledEdit : true,
		usePager : false,
		height : 200,
		checkbox : true,
		url : approot + '/data?module=before&service=TransGoodsLand&method=initTargetEditEarnestMoney&targetId=' + pageObj.targetId
	});
	// 多指标
	pageObj.quotasGrid = $('#quotas').ligerGrid({
		columns : [ {
			display : '序号',
			name : 'turn',
			width : 30,
			editor : {
				type : 'int'
			}
		}, {
			display : '*名称',
			name : 'name',
			requireValue : '[指标名称]不能为空!',
			width : 100,
			editor : {
				type : 'text'
			}
		}, {
			display : '*类型',
			requireValue : '[指标类型]不能为空!',
			name : 'type',
			width : 100,
			editor : {
				type : 'select',
				data : pageObj.quotasType,
				valueColumnName : 'id',
				displayColumnName : 'name'
			},
			render : function(item) {
				var arr = pageObj.quotasType;
				for ( var i = 0; i < arr.length; i++) {
					if (arr[i]['id'] == item.type)
						return arr[i]['name'];
				}
				return item.type;
			}
		}, {
			display : '*单位',
			requireValue : '[竞价单位]不能为空!',
			name : 'unit',
			width : 60,
			editor : {
				type : 'select',
				data : comObj.quotasUnit,
				valueColumnName : 'id',
				displayColumnName : 'name'
			},
			render : function(item) {
				var arr = comObj.quotasUnit;
				for ( var i = 0; i < arr.length; i++) {
					if (arr[i]['id'] == item.unit)
						return arr[i]['name'];
				}
				return item.unit;
			}
		}, {
			display : '*起始值',
			requireValue : '[起始值]不能为空!',
			requireNumber : '[起始值]必须是数字!',
			name : 'init_value',
			width : 100,
			editor : {
				type : 'text'
			}
		}, {
			display : '*增价幅度',
			requireValue : '[增价幅度]不能为空!',
			requireNumber : '[增价幅度]必须是数字!',
			name : 'step',
			width : 100,
			editor : {
				type : 'text'
			}
		}, {
			display : '最终面积',
			name : 'final_value',
			width : 100,
			editor : {
				type : 'text'
			}
		}, {
			display : '*仅出价者可进入',
			name : 'enter_flag1',
			requireValue : '[仅出价者可进入]不能为空!',
			width : 100,
			editor : {
				type : 'select',
				data : comObj.opt,
				valueColumnName : 'id',
				displayColumnName : 'name'
			},
			render : function(item) {
				var arr = comObj.opt;
				for ( var i = 0; i < arr.length; i++) {
					if (arr[i]['id'] == item.enter_flag1)
						return arr[i]['name'];
				}
				return item.enter_flag1;
			}
		}, {
			display : '*必须申请人数',
			name : 'enter_flag2',
			requireValue : '[必须申请人数]不能为空!',
			requireNumber : '[必须申请人数]必须是数字!',
			width : 100,
			editor : {
				type : 'text'
			}
		}, {
			display : '*必须出价人数',
			name : 'enter_flag3',
			requireValue : '[必须出价人数]不能为空!',
			requireNumber : '[必须出价人数]必须是数字!',
			width : 100,
			editor : {
				type : 'text'
			}
		}, {
			display : '*进入等待(秒)',
			name : 'first_wait',
			requireValue : '[进入等待]不能为空!',
			requireNumber : '[进入等待]必须是数字!',
			width : 120,
			editor : {
				type : 'text'
			}
		}, {
			display : '*间隔时长(秒)',
			name : 'limit_wait',
			requireValue : '[间隔时长]不能为空!',
			requireNumber : '[间隔时长]必须是数字!',
			width : 120,
			editor : {
				type : 'text'
			}
		}, {
			display : '*结束等待(秒)',
			name : 'last_wait',
			requireValue : '[结束等待]不能为空!',
			requireNumber : '[结束等待]必须是数字!',
			width : 120,
			editor : {
				type : 'text'
			}
		} ],
		toolbar : {
			items : [ {
				text : '增加',
				click : pageObj.quotasAdd,
				icon : 'add'
			}, {
				line : true
			}, {
				text : '删除',
				click : pageObj.quotasDel,
				icon : 'delete'
			} ]
		},
		rownumbers : true,
		enabledEdit : true,
		usePager : false,
		height : 200,
		checkbox : true,
		url : approot + '/data?module=before&service=TransGoodsLand&method=initTargetEditMultiTrade&targetId=' + pageObj.targetId
	});

	$("#begin_limit_time").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		})
	});// 拍卖(限时竞价、现场拍卖)开始时间
	$("#begin_notice_time").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		})
	});// 公告开始时间
	$("#end_notice_time").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		})
	});// 公告截止时间
	$("#begin_apply_time").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		})
	});// 竞买申请开始时间
	$("#end_apply_time").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		})
	});// 竞买申请截止时间
	$("#begin_earnest_time").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		})
	});// 保证金开始时间
	$("#end_earnest_time").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		})
	});// 保证金截止时间
	$("#begin_list_time").focus(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		})
	});// 挂牌开始时间
	$("#end_list_time").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		})
	});// 挂牌截止时间
	$("#begin_focus_time").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		})
	});// 挂牌(集中、投标)报价开始时间
	$("#end_focus_time").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		})
	});// 挂牌(集中、投标)报价截止时间
	//
	$('#begin_price').keyup(function() {
//		$('#reserve_price').val($('#begin_price').val());
	});

	$('#begin_price').focus(pageObj.toUpCase);
	$('#price_step').focus(pageObj.toUpCase);
	$('#reserve_price').focus(pageObj.toUpCase);
	$('#final_price').focus(pageObj.toUpCase);
	$('#trans_price').focus(pageObj.toUpCase);
	//
	$('#begin_price').keyup(pageObj.toUpCase);
	$('#price_step').keyup(pageObj.toUpCase);
	$('#reserve_price').keyup(pageObj.toUpCase);
	$('#final_price').keyup(pageObj.toUpCase);
	$('#trans_price').keyup(pageObj.toUpCase);
	//
	$('#unit_price').keyup(pageObj.toUpCase);
	$('#unit_price2').keyup(pageObj.toUpCase);
	$('#building_price').keyup(pageObj.toUpCase);
	// 如果标的已保存则显示地块列表及附件
	if (pageObj.targetId != null && pageObj.targetId != "") {
		$("#relGoods").removeAttr("disabled");
		$("#relAttach").removeAttr("disabled");
	}else{
		$("#relGoods").attr('disabled',"true");
		$("#relAttach").attr('disabled',"true");
	}
	
	//页加加载完默认加进一个保证金人民币
	if(pageObj.targetId==null || pageObj.targetId==""){
		var grid = pageObj.bailGrid;
		var row = grid.getSelectedRow();
		grid.addRow({
			'type' : 'CNY',
			'amount' : 0,
			'unit' : 'CNY'
		}, row);
	}
});
