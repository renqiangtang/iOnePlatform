//---------------------------------
//页页对象
//---------------------------------
var pageObj = {};
// ---------------------------------
// 查询条件下拉框对象
// ---------------------------------
pageObj.offerDate1DateManager = null;
pageObj.offerDate2DateManager = null;
// ---------------------------------
// 表格对象
// ---------------------------------
pageObj.gridManager = null;
// ---------------------------------
// 是否显示多指标列
// ---------------------------------
pageObj.showMultiTradeCol = true;
// ---------------------------------
// 表格初始宽高
// ---------------------------------
pageObj.initGridWidth = 0;
pageObj.initGridHeight = 0;
pageObj.hideBidderName = false;
pageObj.userInfoObj = null;
// ---------------------------------
// 转换报价
// ---------------------------------
pageObj.priceRender = function(row, rowNamber) {
	var price = row.price;
	var priceUnit = row.price_unit;
	if (priceUnit && priceUnit.indexOf("元") >= 0)
		if (priceUnit.indexOf("万元") >= 0) {
			var fltValue = parseFloat(price);
			fltValue = parseFloat((fltValue / 10000).toFixed(6));
			return fltValue.addComma() + " " + priceUnit;
		} else
			return price.addComma() + " " + priceUnit;
	else if (priceUnit)
		return price + " " + priceUnit;
	else
		return price;
}
// ---------------------------------
// 转换竞买申请号，当前登录人员为竞买人的则其报价记录显示名称，除非pageObj.hideBidderName=true
// ---------------------------------
pageObj.licenseNoRender = function(row, rowNamber) {
	var licenseNo = row.license_no;
	var bidderId = row.bidder_id;
	if (pageObj.hideBidderName)
		return licenseNo;
	else if (bidderId && pageObj.userInfoObj && pageObj.userInfoObj.userType == 1 && pageObj.userInfoObj.refId == bidderId) {
		if (row.bidder_name)
			return row.bidder_name;
		else if (pageObj.userInfoObj.displayName)
			return pageObj.userInfoObj.displayName;
		else
			return licenseNo;
	} else
		return licenseNo;
}
// ---------------------------------
// 转换出价时间
// ---------------------------------
pageObj.offerDateRender = function(row, rowNamber) {
	var offerDate = row.offer_date;
	return offerDate;// offerDate.substr(0, 19);
}
// ---------------------------------
// 转换竞买报价状态
// ---------------------------------
pageObj.statusRender = function(row, rowNamber) {
	var status = Number(row.status).toFixed(0);
	var statusLabel = "";
	if (status == 2) {
		statusLabel = "成交价";
	} else if (status == 1) {
		statusLabel = "有效";
	} else {
		statusLabel = "无效";
	}
	return statusLabel;
}
// ---------------------------------
// 转换竞买人数
// ---------------------------------
pageObj.licenseCountRender = function(row, rowNamber) {
	var licenseCount = Number(row.licenseCount).toFixed(0);
	var licenseCountLabel = "";
	if (licenseCount >= 0) {
		if (licenseCount == 0) {
			licenseCountLabel = "无人申请";
		} else if (licenseCount == 1) {
			licenseCountLabel = "一人申请";
		} else {
			licenseCountLabel = "多人申请";
		}
	} else {// 后台数据集不允许查看竞买人数
		licenseCountLabel = "-";
	}
	return licenseCountLabel;
}
// ---------------------------------
// 初始化表格
// ---------------------------------
pageObj.getTargetMultiTradeCount = function() {
	var cmd = new Command();
	cmd.module = "trademine";
	cmd.service = "OfferLog";
	cmd.method = "getTargetMultiTradeCount";
	if (pageObj.targetId) {
		cmd.targetId = pageObj.targetId;
	} else if (pageObj.licenseId) {
		cmd.licenseId = pageObj.licenseId;
	} else {
		pageObj.showMultiTradeCol = true;
		return;
	}
	cmd.async = false;
	cmd.success = function(data) {
		if (data.state == "1") {
			pageObj.showMultiTradeCol = data.rowCount > 1;
		}
	};
	cmd.execute();
}
// ---------------------------------
// 初始化表格
// ---------------------------------
pageObj.initGrid = function() {
	// var gridWidth = pageObj.initGridWidth > 0 ? pageObj.initGridWidth :
	// '99.8%';
	var gridHeight = pageObj.initGridHeight > 0 ? pageObj.initGridHeight : 455;
	var columns = [ ];
	if (pageObj.licenseId) {
	} else if (pageObj.targetId) {
		columns.push({
			display : '竞买人',
			name : 'license_no',
			width : 150,
			render : pageObj.licenseNoRender
		});
	} else {
		columns.push({
			display : '竞买人',
			name : 'license_no',
			width : 150,
			render : pageObj.licenseNoRender
		}, {
			display : '标的',
			name : 'target_name',
			width : 220
		});
	}
	columns.push({
		display : '竞价时间',
		name : 'offer_date',
		width : 160,
		render : pageObj.offerDateRender
	},{
		display : '竞买出价',
		name : 'price_full',
		width : 300
	});
//	if (pageObj.showMultiTradeCol)
//		columns.push({
//			display : '竞价指标',
//			name : 'multi_trade_name',
//			width : 80
//		});
	if (pageObj.showStatusCol)
		columns.push({
			display : '状态',
			name : 'status',
			width : 40,
			render : pageObj.statusRender
		});
	var url = approot + '/data?module=trademine&service=OfferLog&method=getOfferLogListData&moduleId=' + pageObj.moduleId + '&u=' + pageObj.userId;
	if (pageObj.targetId) {
		url += '&target_id=' + pageObj.targetId;
	} else if (pageObj.licenseId) {
		url += '&license_id=' + pageObj.licenseId;
	} else if (pageObj.bidderId) {
		url += '&bidder_id=' + pageObj.bidderId;
	}
	pageObj.gridManager = $("#offerLogGrid").ligerGrid({
		url : url,
		columns : columns,
		pagesizeParmName : 'pageRowCount',// 每页记录数
		pageParmName : 'pageIndex',// 页码数
		sortnameParmName : 'sortField',// 排序列名
		sortorderParmName : 'sortDir',// 排序方向
		isScroll : true,// 是否滚动
		rownumbers : true,
		pageSizeOptions : [ 10, 20, 30 ],
		pageSize : 20,
		showTitle : false,
		height : 400,//gridHeight - 10,
		onError : function(a, b) {
		}
	});
}
// ---------------------------------
// 查询数据
// ---------------------------------
pageObj.queryData = function() {
	var target = $("#txtTarget").val();
	var offerDate1 = $("#txtOfferDate1").val();
	var offerDate2 = $("#txtOfferDate2").val();
	var licenseNo = $("#txtLicenseNo").val();
	var queryParams = {
		target : target,
		offerDate1 : offerDate1,
		offerDate2 : offerDate2,
		licenseNo : licenseNo,
		u : pageObj.userId
	};
	if (pageObj.targetId) {
		queryParams.target_id = pageObj.targetId;
	} else if (pageObj.licenseId) {
		queryParams.license_id = pageObj.licenseId;
	} else if (pageObj.bidderId) {
		queryParams.bidder_id = pageObj.bidderId;
	}
	var url = approot + '/data?module=trademine&service=OfferLog&method=getOfferLogListData&moduleId=' + pageObj.moduleId;
	pageObj.gridManager.refresh(url, queryParams);
}
// ---------------------------------
// 查看竞价详细数据
// ---------------------------------
pageObj.viewOfferLog = function(offerLogId) {
	DialogOpen({
		height : 750,
		width : 1000,
		url : approot + '/mine/trade/viewOfferLog.html?offerLogId=' + offerLogId,
		title : '查看竞买报价',
		buttons : [ {
			text : '关闭',
			onclick : function(item, dialog) {
				dialog.close();
			}
		} ]
	});
}
// ---------------------------------
// 查询条件初始化
// ---------------------------------
pageObj.initQueryConditionCtrl = function() {
	$("#txtOfferDate1").ligerDateEditor({
		showTime : true
	});
	$("#txtOfferDate2").ligerDateEditor({
		showTime : true
	});
}
// ---------------------------------
// 清空查询条件
// ---------------------------------
pageObj.resetConditionCtrl = function() {
	$("#txtTarget").val("");
	$("#txtOfferDate1").val("");
	$("#txtOfferDate2").val("");
	$("#txtLicenseNo").val("");
}
// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getPageValue("moduleId");
	pageObj.bidderId = Utils.getPageValue("bidderId");
	pageObj.licenseId = Utils.getPageValue("licenseId");
	pageObj.targetId = Utils.getPageValue("targetId");
	pageObj.userId = getUserId();
	pageObj.userInfoObj = getUserInfoObj();
	var strHideBidderName = Utils.getPageValue("hideBidderName");
	if (strHideBidderName && (strHideBidderName == "1" || strHideBidderName.toLowerCase() == "true" || strHideBidderName.toLowerCase() == "yes"))
		pageObj.hideBidderName = true;
	pageObj.initGridWidth = parseInt(Utils.getPageValue("gridWidth"));
	pageObj.initGridWidth = isNaN(pageObj.initGridWidth) ? 0 : pageObj.initGridWidth;
	pageObj.initGridHeight = parseInt(Utils.getPageValue("gridHeight"));
	pageObj.initGridHeight = isNaN(pageObj.initGridHeight) ? 0 : pageObj.initGridHeight;
	if (pageObj.licenseId != "" || pageObj.targetId != "") {
		$("#targetCondition").hide();
		$("#targetConditionText").hide();
	}
	var hideQueryCondition = Utils.getPageValue("hideQueryCondition");
	if (hideQueryCondition && (hideQueryCondition == "1" || hideQueryCondition.toLowerCase() == "true" || hideQueryCondition.toLowerCase() == "yes")) {
		$("#queryCondition").hide();
	}
	//pageObj.getTargetMultiTradeCount();
	pageObj.initGrid();
	pageObj.initQueryConditionCtrl();
	$("#btnQuery").click(pageObj.queryData);
	$("#btnReset").click(pageObj.resetConditionCtrl);
//	Utils.autoIframeSize();
}
$(document).ready(pageObj.initHtml);
