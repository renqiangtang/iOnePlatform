// ---------------------------------
// 页页对象
// ---------------------------------
var pageObj = {};
// ---------------------------------
// 表格对象
// ---------------------------------
pageObj.gridManager = null;

pageObj.getColumns = function() {
	return [{
				display : '竞买号',
				name : 'license_no',
				width : '20%'
			}, {
				display : '标的',
				name : 'target_name',
				width : '20%'
			}, {
				display : '应交保证金',
				name : 'earnest_money',
				width : '20%',
				render : pageObj.showEarnestMoney
			}, {
				display : '实交保证金',
				name : 'amount',
				width : '20%',
				type : 'float',
				render : pageObj.showEnPayMoney
			}, {
				display : '申请日期',
				name : 'apply_date',
				width : '20%',
				render : pageObj.dateFormat
			}];
}
// ---------------------------------
// 初始化表格
// ---------------------------------
pageObj.initGrid = function() {
	pageObj.gridManager = $("#earnestGrid").ligerGrid({
		url : approot
				+ '/data?module=bidder&service=Earnest&method=getEarnestListData&dataSetNo=queryBidderTransEarnest&moduleId='
				+ pageObj.moduleId + '&u=' + pageObj.userId,
		columns : pageObj.getColumns(),
		isScroll : true, // 是否滚动
		pageSizeOptions : [10, 20, 30],
		showTitle : false,
		height : 441
		// totalRender: pageObj.f_totalRender,
	});
}

pageObj.f_totalRender = function(data, currentPageData) {
	var rtnStr = null;
	if (data.Total == '0') {
		rtnStr = "总实交保证金: 0 元";
	} else {
		rtnStr = "总实交保证金: " + data.amountTotal + " 元";
	}
	return rtnStr;
}

pageObj.dateFormat = function(row, rowNumber) {
	var applyDate = row.apply_date;
	if (applyDate != null && applyDate.indexOf(" ") != -1) {
		applyDate = applyDate.split(" ")[0];
	}
	return applyDate;
}

pageObj.showEarnestMoney = function(row, rowNumber) {
	return comObj.cf({flag:row.currency,amount:row.earnest_money,unit:row.unit});
}

pageObj.showEnPayMoney = function(row, rowNumber) {
	return comObj.cf({flag:row.currency,amount:row.amount,unit:row.unit});
}

// ---------------------------------
// 查询按钮事件
// ---------------------------------
pageObj.queryData = function() {
	var licenseNo = $("#licenseNo").val();
	var targetName = $("#targetName").val();
	var userId = getUserId();
	var queryParams = {
		licenseNo : licenseNo,
		targetName : targetName,
		u : userId
	};
	var url = approot
			+ '/data?module=bidder&service=Earnest&method=getEarnestListData&moduleId='
			+ pageObj.moduleId;
	pageObj.gridManager.refresh(url,queryParams);
}

// ---------------------------------
// 清空查询条件
// ---------------------------------
pageObj.resetConditionCtrl = function() {
	$("#licenseNo").val("");
	$("#targetName").val("");
}

// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getUrlParamValue(document.location.href,
			"moduleId");
	pageObj.userId = getUserId();
	pageObj.initGrid();
	// pageObj.initGridData();
	$("#btnQuery").click(pageObj.queryData);
	$("#btnReset").click(pageObj.resetConditionCtrl);
	Utils.autoIframeSize();
}

$(document).ready(pageObj.initHtml);