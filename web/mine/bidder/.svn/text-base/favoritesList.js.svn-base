var pageObj = {};
pageObj.noRender = function(row, rowNamber) {
	var no = row.no;
	var name = row.name;
	var noText = no && name && no != name ? name + '(' + no + ')' : no ? no : name;
	var targetId = row.id;
	return "<a href='javascript:pageTradeCommonObj.viewTarget(\"" + targetId + "\")'>" + noText + "</a>";
}
// ----------------------------
// 渲染交易状态
// ----------------------------
pageObj.statusRender = function(row, rowNamber) {
	var status = Number(row.status).toFixed(0);
	if (status == '3')
		return '已公告';
	else if (status == '4')
		return '交易中';
	else if (status == '5')
		return '已成交';
	else if (status == '6')
		return '未成交';
	else if (status == '7')
		return '成交资格审核';
}
pageObj.businessTypeRender = function(row, rowNamber) {
	var businessType = row.business_type;
	var businessTypeText = pageTradeCommonObj.allBusinessTypeFull && businessType in pageTradeCommonObj.allBusinessTypeFull ? pageTradeCommonObj.allBusinessTypeFull[businessType].name : "其它";
	return businessTypeText;
}
pageObj.convertFloatValue = function(value, convertUnit) {
	if (value == null || isNaN(value))
		return "";
	else {
		var fltValue = parseFloat(value);
		if (convertUnit && convertUnit == "万元")
			fltValue = parseFloat((fltValue / 10000).toFixed(6));
		return fltValue.addComma();
	}
}
pageObj.showDetail = function(row, detailPanel, callback) {
	var targetId = row.target_id;
	var transType = Number(row.trans_type).toFixed(0);
	var isNetTrans = Number(row.is_net_trans).toFixed(0);
	var priceUnit = row.unit;
	var beginPrice = pageObj.convertFloatValue(row.begin_price, priceUnit);
	var priceStep = pageObj.convertFloatValue(row.price_step, priceUnit);
	var businessType = row.business_type;
	var businessTypeConfig = pageTradeCommonObj.allBusinessTypeFull[businessType];
	var beginApplyTime = row.begin_apply_time ? row.begin_apply_time : '';
	var beginFocusTime = row.begin_focus_time ? row.begin_focus_time : '';
	var endFocusTime = row.end_focus_time ? row.end_focus_time : '';
	var beginLimitTime = row.begin_limit_time ? row.begin_limit_time : '';
	var address = row.address ? row.address : '';
	var goodsSize = row.goods_size ? row.goods_size+"平方千米" : '';
	var businessTypeText = businessTypeConfig ? businessTypeConfig.name : "其它";
	var mjTitle = (businessTypeConfig ? businessTypeConfig.goods_name : '') + '面积';
	var beginFocusTimeTile = null;
	if (transType == 2) {
		beginFocusTimeTile = '招标开始时间';
	} else if (transType == 1 && isNetTrans == 0) {
		beginFocusTimeTile = '拍卖开始时间';
	}
	var detailBody = null;
	if (transType == 2 || (transType == 1 && isNetTrans == 0)) {
		detailBody = '<div class="i-arrow"></div><table class="i-l-gridGetails" style="width:98%; height:87px">' + '<tr><td width="120" height="28" class="tr">' + mjTitle + '：</td><td width="150" class="fb">' + goodsSize
				+ '</td><td width="120" class="tr">位置：</td><td width="200" class="fb">' + address + '</td><td width="120" class="tr">类型：</td><td class="fb">' + businessTypeText + '</td></tr>' + '<tr><td height="28" class="tr">竞买申请开始时间：</td><td class="fb">' + beginApplyTime
				+ '</td><td class="tr">' + beginFocusTimeTile + '：</td><td class="fb">' + beginLimitTime + '</td><td colspan=\'2\'></td></tr>' + '</table>';
	} else {
		detailBody = '<div class="i-arrow"></div><table class="i-l-gridGetails" style="width:98%">' + '<tr><td width="120" height="28" class="tr">起始价：</td><td width="150" class="fb">' + beginPrice + priceUnit
				+ '</td><td width="120" class="tr">增价幅度：</td><td width="200" class="fb">' + priceStep + priceUnit + '</td><td width="120" class="tr">类型：</td><td class="fb">' + businessTypeText + '</td></tr>' + '<tr><td height="28" class="tr">' + mjTitle + '：</td><td class="fb">'
				+ goodsSize + '</td><td class="tr">位置：</td><td class="fb">' + address + '</td><td class="tr">竞买申请开始时间：</td><td class="fb">' + beginApplyTime + '</td></tr>';
		if (transType == 0 && isNetTrans == 0) {
			detailBody = detailBody + '<tr><td height="28" class="tr">电脑报价截止时间：</td><td class="fb">' + endFocusTime + '</td> <td colspan="4"></td></tr>';
		} else if (transType == 0 && isNetTrans == 1) {
			detailBody = detailBody + '<tr><td height="28" class="tr">集中报价期开始时间：</td><td class="fb">' + beginFocusTime + '</td><td class="tr">集中报价期截止时间：</td><td class="fb">' + endFocusTime + '</td><td class="tr">限时竞拍开始时间：</td><td class="fb">' + beginLimitTime + '</td></tr>';
		} else {
			detailBody = detailBody + '<tr><td height="28" class="tr">竞价开始时间：</td><td class="fb">' + beginLimitTime + '</td> <td colspan="4"></td></tr>';
		}
		detailBody = detailBody + '</table>';
	}
	$(detailPanel).html(detailBody);
}
pageObj.initGrid = function() {
	pageObj.favoritesGrid = $("#favoritesGrid").ligerGrid({
		url : approot + '/data?module=bidder&service=TargetFavorites&method=getFavoritesListData&moduleId=' + pageObj.moduleId + '&userId=' + pageObj.userId,
		columns : [ {
			display : '标的',
			name : 'no',
			width : '35%',
			render : pageObj.noRender
		}, {
			display : '类型',
			name : 'business_type',
			width : '15%',
			render : pageObj.businessTypeRender
		}, {
			display : '交易方式',
			name : 'trans_type_label',
			width : '15%'
		}, {
			display : '状态',
			name : 'status',
			width : '15%',
			render : pageObj.statusRender
		},
		// { display: '说明', name: 'explain', width :'15%' },
		{
			display : '收藏日期',
			name : 'create_date',
			width : '15%'
		} ],
		pagesizeParmName : 'pageRowCount',
		pageParmName : 'pageIndex',
		isScroll : true,
		frozen : false,
		pageSizeOptions : [ 10, 20, 30 ],
		showTitle : false,
		fixedCellHeight : false,
		width : '99.8%',
		height : 455,
		detail : {
			height : 100,
			onShowDetail : pageObj.showDetail
		}
	});
}
// ---------------------------------
// 查询数据
// ---------------------------------
pageObj.queryData = function() {
	var target = $("#txtTarget").val();
	var beginCreateDate = $("#txtBeginCreateDate").val();
	var endCreateDate = $("#txtEndCreateDate").val();
	var queryParams = {
		target : target,
		beginCreateDate : beginCreateDate,
		endCreateDate : endCreateDate,
		u : pageObj.userId
	};
	var url = approot + '/data?module=bidder&service=TargetFavorites&method=getFavoritesListData&moduleId=' + pageObj.moduleId;
	pageObj.favoritesGrid.refresh(url, queryParams);
}
// ---------------------------------
// 清空查询条件
// ---------------------------------
pageObj.resetConditionCtrl = function() {
	$("#txtTarget").val("");
	$("#txtBeginCreateDate").val("");
	$("#txtEndCreateDate").val("");
}
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getPageValue("moduleId");
	pageObj.userId = getUserId();
	pageTradeCommonObj.getBusinessType();
	pageTradeCommonObj.getBusinessTypeFull();
	$("#btnQuery").click(pageObj.queryData);
	$("#btnReset").click(pageObj.resetConditionCtrl);
	$("#txtBeginCreateDate").ligerDateEditor();
	$("#txtEndCreateDate").ligerDateEditor();
	pageObj.initGrid();
	Utils.autoIframeSize();
}
$(document).ready(pageObj.initHtml);