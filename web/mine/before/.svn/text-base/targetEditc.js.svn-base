var pageObj = {};
pageObj.targetId = null;
pageObj.goodsArr = null;
pageObj.transTypeObj = {};
pageObj.config = null;
pageObj.transTypeArr = null;
pageObj.notModifyMode = "update";

pageObj.bailAdd = function() {
	if (pageObj.mode == pageObj.notModifyMode) {
		DialogAlert('不能修改！');
		return;
	}
	var grid = pageObj.bailGrid;
	var row = grid.getSelectedRow();
	grid.addRow({
				'type' : 'CNY',
				'amount' : 0
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
	obj.first_wait = 480;
	obj.limit_wait = 300;
	obj.last_wait = 10;
	grid.addRow(obj);
}
// --------------------------------------------
// 加载默认多指标数据
// --------------------------------------------
pageObj.quotasAddDefault = function(obj1) {
	if (pageObj.mode == pageObj.notModifyMode) {
		DialogAlert('不能修改！');
		return;
	}
	DialogConfirm('加载模版数据会清除本表格,是否继续?', function(yn) {
				if (!yn)
					return;
				var length = pageObj.quotasGrid.getData(null, true).length;
				for (var i = length - 1; i >= 0; i--)
					pageObj.quotasGrid.deleteRow(i);
				try {
					var business_type = $('#business_type').val();
					var trans_type_id = $('#trans_type_id').val();
					var config = pageObj.config[business_type][business_type + trans_type_id];
					var quotas = config.quotas;
					for (var i = 0; i < quotas.length; i++) {
						var obj = {};
						obj.name = quotas[i].multi_trade_name;
						obj.type = quotas[i].class_type;
						obj.unit = quotas[i].unit;
						obj.init_value = '';
						obj.step = '';
						obj.final_value = '';
						obj.enter_flag1 = quotas[i].enter_flag_0;
						obj.enter_flag2 = quotas[i].enter_flag_1;
						obj.enter_flag3 = quotas[i].enter_flag_2;
						obj.first_wait = quotas[i].first_wait;
						obj.limit_wait = quotas[i].limit_wait;
						obj.last_wait = quotas[i].last_wait;
						pageObj.quotasAdd(null, obj);
					}
				} catch (ex) {
					DialogAlert('请检查配置!');
				}
			})
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
	for (var i = 0; i < arr.length; i++) {
		if (arr[i].business_type_rel_id == curr_business_type) {
			html += '<option value="' + arr[i].id + '">' + arr[i].name + '</option>';
		}
		pageObj.transTypeObj[arr[i].id] = arr[i];
	}
	$('#trans_type_id').html(html);
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
		case 0 :// 挂牌
			$('#begin_list_time').parents('tr').show();
			$('#begin_focus_time').parents('tr').show();
			break;
		case 1 :// 拍卖
			$('#begin_list_time').parents('tr').hide();
			$('#begin_focus_time').parents('tr').hide();
			$('#beginLimitTimeTd').parents('tr').show();
			break;
	}
}

pageObj.initPage = function() {
	//
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsMine";
	cmd.method = "initTargetEditPageParam";
	cmd.u = pageObj.u;
	cmd.goodsType = pageObj.goodsType;
	cmd.success = function(data) {
		pageObj.quotasType = data.quotasType;
		pageObj.config = data.config;
		//
		var html = '<option value="" selected>--请选择--</option>';
		html = '';
		var arr = data.businessType;
		for (var i = 0; i < arr.length; i++) {
			html += '<option value="' + arr[i].id + '">' + arr[i].name + '</option>';
		}
		$('#business_type').html(html);
		$('#business_type').change(pageObj.businessTypeChange);
		//
		var curr_business_type = $('#business_type').val();
		html = '<option value="" selected>--请选择--</option>';
		html = '';
		arr = data.transType;
		pageObj.transTypeArr = data.transType;
		for (var i = 0; i < arr.length; i++) {
			if (arr[i].business_type_rel_id == curr_business_type) {
				html += '<option value="' + arr[i].id + '">' + arr[i].name + '</option>';
			}
			pageObj.transTypeObj[arr[i].id] = arr[i];
		}
		$('#trans_type_id').html(html);
		$('#trans_type_id').change(pageObj.transTypeChange);
		pageObj.transTypeChange();
		//
		html = '';
		arr = data.transOrgan;
		for (var i = 0; i < arr.length; i++) {
			html += '<option value="' + arr[i].id + '">' + arr[i].name + '</option>';
		}
		$('#trans_organ_id').html(html);
	}
	cmd.execute();
}

pageObj.refresh = function() {
	if (pageObj.targetId) {
		var cmd = new Command();
		cmd.module = "before";
		cmd.service = "TransGoodsMine";
		cmd.method = "initTargetEditData";
		cmd.targetId = pageObj.targetId;
		cmd.success = function(data) {
			$('#business_type').val(data.target.business_type);
			pageObj.businessTypeChange();
			$('#trans_type_id').val(data.target.trans_type_id);
			pageObj.transTypeChange();
			Utils.setForm(data.target);
			var html = '';
			var nav = '';
			var goods = data.goods;
			pageObj.goodsArr = [];
			$(goods).each(function(index, ele) {
				pageObj.goodsArr.push(ele.id);
				nav += '<li><a><span onclick=pageObj.delGoods("' + ele.id + '","' + ele.no
						+ '")></span><label onclick="javascript:pageObj.viewGoods(\'' + ele.id + '\')">' + ele.no
						+ '</label></a></li>';
			});
			$('#goodsDiv').html(html);
			$('#goodsNav').html(nav);
			$('#targetName').html(data.target.no);
		}
		cmd.execute();
		pageObj.transTypeChange();
	}
}
pageObj.viewGoods = function(goodsId) {
	var obj = {};
	// 地址
	if( pageObj.goodsType=="301") obj.url = "prospect301.html";
	else obj.url = "mine301.html";
	// 参数
	obj.param = {
		mode : 'modify',
		u : pageObj.u,
		goodsId : goodsId,
		goodsType : pageObj.goodsType
	};
	// 窗口参数
	obj.feature = "dialogWidth=800px;dialogHeight=600px";
	DialogModal(obj);
}

pageObj.delGoods = function(goodsId, goodsName) {
	if (!confirm("确定解除[" + goodsName + "]捆绑?"))
		return;
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsMine";
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
	if (quotasArr.length > 0) {
		var vv = $('#final_price').val().trim();
		if (vv.length == 0) {
			DialogAlert("有多指标封顶价不能为空!");
			$('#final_price').focus();
			return false;
		}
	}
	var typeArr = [];
	var nameArr = [];
	var turnArr = [];
	var maxturn = 0;
	for (var i = 0; i < quotasArr.length; i++) {
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
	for (var i = 0; i < quotasArr.length; i++) {
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
	for (var i = 0; i < arr.length; i++) {
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
		case 0 :// 挂牌
			var b = pageObj.timeCompare('begin_notice_time', 'begin_list_time', '[挂牌开始时间]必须大于[公告开始时间]！', '>=');
			if (!b)
				return false;
			b = pageObj.timeCompare('begin_notice_time', 'begin_focus_time', '[集中报价开始时间]必须大于[公告开始时间]！', '>=');
			if (!b)
				return false;
			break;
		case 1 :// 拍卖
			var b = pageObj.timeCompare('begin_notice_time', 'begin_limit_time', '[拍卖(限时竞价、现场拍卖)开始时间]必须大于[公告开始时间]！',
					'>=');
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
			case '>' :
				if (et < bt) {
					DialogAlert(msg);
					$('#' + endTimeId).focus();
					return false;
				}
				break;
			case '>=' :
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
	var finalPrice = parseFloat($('#final_price').val().trim());
	if (step == 0) {
		DialogAlert('价款信息：增价幅度不能为0!');
		return false;
	}
	if (step > 0 && finalPrice <= beginPrice) {
		DialogAlert('价款信息：增价幅度为正数,封顶价必须大于起始价!');
		return false;
	}
	if (step < 0 && finalPrice >= beginPrice) {
		DialogAlert('价款信息：增价幅度为负数,封顶价必须小于起始价!');
		return false;
	}
	if (!beginPrice.nDigits(2)) {
		DialogAlert("起始价最多保留两位小数!");
		$('#begin_price').focus();
		return false;
	}
	if (!step.nDigits(2)) {
		DialogAlert("增价幅度最多保留两位小数!");
		$('#price_step').focus();
		return false;
	}
	var reserve_price = parseFloat($('#reserve_price').val().trim());
	if (!reserve_price.nDigits(2)) {
		DialogAlert("底价最多保留两位小数!");
		$('#reserve_price').focus();
		return false;
	}
	var final_price_str = $('#final_price').val().trim();
	var final_price = parseFloat(final_price_str);
	if (final_price_str && !final_price.nDigits(2)) {
		DialogAlert("封顶价最多保留两位小数!");
		$('#final_price').focus();
		return false;
	}

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
	for (var i = 0; i < arr.length; i++) {
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
		if (!amount.nDigits(2)) {
			DialogAlert('保证金:第' + (i + 1) + '行小数位最多为2位!');
			return false;
		}
		nameArr.push(arr[i].type);
	}
	return true;
}

pageObj.save = function() {
	// 必须项
	if (!Utils.requiredCheck())
		return;
	// 保证金
	if (!pageObj.bailCheck())
		return;
	// 价格
	if (!pageObj.priceCheck())
		return;
	// 多指标
	if (!Utils.requiredCheckForGrid(pageObj.quotasGrid, '多指标信息'))
		return;
	// 多指标
	if (!pageObj.quotasCheck())
		return;
	// 时间
	if (!pageObj.timeCheck())
		return;
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsMine";
	cmd.method = "saveTransTarget";
	if (pageObj.mode == "update") {
		cmd.method = "updateBidingTarget";
	}
	cmd.targetId = pageObj.targetId;
	cmd.mode = pageObj.mode;
	Utils.getForm(cmd);
	// 保证金
	cmd.bailsJson = JSON.stringify(pageObj.bailGrid.getData(null, true));
	// 多指标
	cmd.quotasJson = JSON.stringify(pageObj.quotasGrid.getData(null, true));
	cmd.success = function(data) {
		if ('targetId' in data) {
			pageObj.targetId = data.targetId;
			if (pageObj.targetId) {
				$("#attachContainer").show();
				pageObj.initAttachList();
			} else {
				$("#attachContainer").hide();
			}
		}
		pageObj.refreshButtons();
		DialogAlert(data.message);
		if (data.state)
			window.returnValue = true;
	};
	cmd.execute();
}

pageObj.refreshButtons = function() {
//	$('#addImg').val('新增' + comObj.goods_name[pageObj.goodsType]);
//	$('#boundImg').val('绑定' + comObj.goods_name[pageObj.goodsType]);
	if (pageObj.targetId) {
		$('#addImg').removeAttr('disabled');
		$('#boundImg').removeAttr('disabled');
	} else {
		$('#addImg').attr('disabled', 'disabled');
		$('#boundImg').attr('disabled', 'disabled');
	}

}

pageObj.addGoods = function() {
	if ($('#addImg').attr('disabled')) {
		DialogAlert('标的信息还未保存,请先保存标的信息！');
		return;
	}
	var obj = {};
	// 地址
	if( pageObj.goodsType=="301") obj.url = "prospect301.html";
	else obj.url = "mine301.html";
	// 参数
	obj.param = {
		mode : 'new',
		u : Utils.getPageValue('u'),
		targetId : pageObj.targetId,
		callback : window.pageObj.fresh,
		goodsType : pageObj.goodsType
	};
	// 窗口参数
	obj.feature = "dialogWidth=900px;dialogHeight=600px";
	var rv = DialogModal(obj);
	if (rv)
		pageObj.refresh();
}

pageObj.boundGoods = function() {
	if ($('#addImg').attr('disabled')) {
		DialogAlert('标的信息还未保存,请先保存标的信息！');
		return;
	}
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
	}
}

pageObj.initAttachList = function() {
	var initWidth = 978;
	var initHeight = 200;
	
	var grant = {add: 2, edit: 2, del: 2,
		addDtl: 2, editDtl: 2, delDtl: 2,
		uploadFile: 2, downloadFile: 2, delFile: 2};
	var owners = new Array();
	var owner = {};
	owner.id = pageObj.targetId;
	owner.name = "标的";
	owner.title = $('#targetName').html();
	owner.tableName = "trans_target";
	owner.templetNo = "businessType" + $("#business_type").val() + "TargetAttach";
	owner.grant = grant;
	owners.push(owner);
	var url = approot + '/sysman/attachList.html?u=' + pageObj.u + '&owners='
			+ encodeURIComponent(JSON.stringify(owners)) + '&width=' + initWidth + '&height=' + initHeight;
	var attachFrame = $('#attachFrame');
	var tableWidth = $("#attachPanel").parent().width() - 1;  //获取父节点宽度 并减去边框宽度
	
	if (attachFrame.length == 0) {
		$('#attachPanel').html('<iframe id="attachFrame" frameborder="0" src="' + url + '" style="height:' + initHeight + 'px;width:'
				+ tableWidth + 'px"></iframe>');
	} else {
		attachFrame.attr('src', url);
	}
}

pageObj.afterCheck = function(goodsType , isAfter){
	if(isAfter==null || isAfter=="" || isafter=="null"){
		isAfter = goodsType == "101"?1:0;
	}
	//$("input[name='is_after_check'][value="+isAfter+"]").attr("checked",true); 
	$("input[name='is_after_check'][value=1]").attr("checked",true); 
}

$(document).ready(function() {
	pageObj.mode = Utils.getPageValue('mode');
	pageObj.targetId = Utils.getPageValue('targetId');
	pageObj.u = Utils.getPageValue('u');
	pageObj.goodsType = Utils.getPageValue('goodsType');
	pageObj.afterCheck(pageObj.goodsType , null);
	pageObj.initPage();
	$('#goodsNav').empty();
	pageObj.refresh();
	pageObj.refreshButtons();
	$('#addImg').click(pageObj.addGoods);
	$('#boundImg').click(pageObj.boundGoods);
	$('#saveImg').click(pageObj.save);
	//
	switch (pageObj.mode) {
		case pageObj.notModifyMode :
			pageObj.disabledInput("reserve_price,price_step,field9,field8,field7,saveImg", "not");
			break;
		default :
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
					display : '金额(元)',
					width : 200,
					name : 'amount',
					editor : {
						type : 'text'
					}
				}],
		toolbar : {
			items : [{
						text : '增加',
						click : pageObj.bailAdd,
						icon : 'add'
					}, {
						line : true
					}, {
						text : '删除',
						click : pageObj.bailDel,
						icon : 'delete'
					}]
		},
		rownumbers : true,
		enabledEdit : true,
		usePager : false,
		height : 200,
		checkbox : true,
		url : approot + '/data?module=before&service=TransGoodsMine&method=initTargetEditEarnestMoney&targetId='
				+ pageObj.targetId
	});
	// 多指标
	pageObj.quotasGrid = $('#quotas').ligerGrid({
		columns : [{
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
						for (var i = 0; i < arr.length; i++) {
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
						type : 'text'
					}
				}, {
					display : '*起始价',
					requireValue : '[起始价]不能为空!',
					requireNumber : '[起始价]必须是数字!',
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
					display : '最终价',
					// requireValue : '[最终价]不能为空!',
					requireNumber : '[最终价]必须是数字!',
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
						for (var i = 0; i < arr.length; i++) {
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
				}],
		toolbar : {
			items : [{
						text : '加载模版数据',
						click : pageObj.quotasAddDefault,
						icon : 'logout'
					}, {
						text : '增加',
						click : pageObj.quotasAdd,
						icon : 'add'
					}, {
						line : true
					}, {
						text : '删除',
						click : pageObj.quotasDel,
						icon : 'delete'
					}]
		},
		rownumbers : true,
		enabledEdit : true,
		usePager : false,
		height : 200,
		checkbox : true,
		url : approot + '/data?module=before&service=TransGoodsMine&method=initTargetEditMultiTrade&targetId='
				+ pageObj.targetId
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
	$("#targetfield9").click(function() {
				WdatePicker({
							dateFmt : 'yyyy-MM-dd HH:mm:ss'
						})
			});// 答疑会召开时间
	if (pageObj.targetId) {
		$("#attachContainer").show();
		pageObj.initAttachList();
	} else {
		$("#attachContainer").hide();
	}
	//
	$('#begin_price').keyup(function() {
				$('#reserve_price').val($('#begin_price').val());
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
	
});

pageObj.toUpCase = function() {
	try {
		var vstr = $(this).val();
		if (vstr.isNumber()) {
			var v = parseFloat(vstr);
			$('#UCase').html(v.toUpcase());
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
			$('#end_focus_time').val(begin_focus_time.addDays(obj.end_notice_time));
		//
		if ($('#begin_limit_time').val().length == 0) {
			switch (obj.trans_type) {
				case 0 :// 挂牌
					$('#begin_limit_time').val($('#end_focus_time').val());
					break;
				case 1 :// 拍卖
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

pageObj.disabledInput = function(fields, type) {
	if (type == "yes") {
		if (fields != null) {
			var fieldsArray = fields.split(",");
			if (fieldsArray != null && fieldsArray.length > 0) {
				for (var i = 0; i < fieldsArray.length; i++) {
					$('#' + fieldsArray[i]).attr("disabled", "disabled");
				}
			}
		}
	} else if (type == "not") {
		fields = ',' + fields + ',';
		var inputTypes = new Array("input", "select", "radio", "textarea");
		for (var k = 0; k < inputTypes.length; k++) {
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
