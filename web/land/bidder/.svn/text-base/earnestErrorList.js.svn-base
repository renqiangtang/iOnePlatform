// ---------------------------------
// 页页对象
// ---------------------------------
var pageObj = {};
// ---------------------------------
// 表格对象
// ---------------------------------
pageObj.gridManager = null;
pageObj.moduleId = null;
pageObj.userId = null;

// ---------------------------------
// 初始化表格
// ---------------------------------
pageObj.initGrid = function() {
	pageObj.gridManager = $("#earnestErrorGrid").ligerGrid({
		url : approot + '/data?module=bidder&service=EarnestError&method=getEarnestErrorListData&moduleId=' + pageObj.moduleId + '&u=' + pageObj.userId,
		columns : [ {
			display : '交款户名',
			name : 'bidder_account_name',
			width : '20%'
		}, {
			display : '交款账号',
			name : 'bidder_account_no',
			width : '20%'
		}, {
			display : '交款账户开户行',
			name : 'bidder_account_bank',
			width : '20%'
		}, {
			display : '金额',
			name : 'amount',
			width : '20%',
			align : 'right',
			render : pageObj.showErrorBillMoney
		}, {
			display : '到账时间',
			name : 'bank_business_date',
			width : '20%',
			render : pageObj.dateFormat
		} ],
		isScroll : true,// 是否滚动
		pageSizeOptions : [ 10, 20, 30 ],
		showTitle : false,
		height : 451
	});
}
pageObj.dateFormat = function(row, rowNumber) {
	var bankBusinessDate = row.bank_business_date;
	if (bankBusinessDate != null && bankBusinessDate.indexOf(" ") != -1) {
		bankBusinessDate = bankBusinessDate.split(" ")[0];
	}
	return bankBusinessDate;
}

pageObj.showErrorBillMoney = function(row, rowNumber) {
	return comObj.cf({
		flag : row.currency,
		amount : row.amount,
		unit : '元'
	});
}

// ---------------------------------
// 查询按钮事件
// ---------------------------------
pageObj.queryData = function() {
	var bidderAccountName = $("#bidderAccountName").val();
	var bidderAccountNo = $("#bidderAccountNo").val();
	var bidderAccountBank = $("#bidderAccountBank").val();
	var userId = getUserId();
	var queryParams = {
		bidderAccountName : bidderAccountName,
		bidderAccountNo : bidderAccountNo,
		bidderAccountBank : bidderAccountBank,
		u : userId
	};
	var url = approot + '/data?module=bidder&service=EarnestError&method=getEarnestErrorListData&moduleId=' + pageObj.moduleId;
	pageObj.gridManager.refresh(url, queryParams);
}

pageObj.btnReset = function() {
	$("#bidderAccountName").val("");
	$("#bidderAccountNo").val("");
	$("#bidderAccountBank").val("");
}

// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getUrlParamValue(document.location.href, "moduleId");
	pageObj.userId = getUserId();
	pageObj.initGrid();
	$("#btnQuery").click(pageObj.queryData);
	$("#btnReset").click(pageObj.btnReset);
	Utils.autoIframeSize();
}

$(document).ready(pageObj.initHtml);
