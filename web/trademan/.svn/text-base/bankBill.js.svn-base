// 页页对象
var pageObj = {};
// 表格对象
// ---------------------------------
pageObj.gridManager = null;
pageObj.time = null;
// ----------------------------
// 渲染入账单类型
// ----------------------------
pageObj.typeRender = function(row, rowNamber) {
	var billType = row.bill_type;
	if (billType == '0') {
		return '其它款';
	}else	if (billType == '1') {
		return '收保证金';
	}else	if (billType == '2') {
		return '收服务费';
	}else	if (billType == '3') {
		return '收成交款';
	}else	if (billType == '4') {
		return '退成交款';
	}else	if (billType == '5') {
		return '暂交款';
	}else	if (billType == '6') {
		return '错转款';
	}
}
// ---------------------------------
// 查询按钮事件
// ---------------------------------
pageObj.queryData = function () {
	var billtype = $("#billtype").val();
	var startTime = $("#startTime").val();
	var endTime = $("#endTime").val();
	var userId = getUserId();
	var queryParams = { billtype: billtype,startTime: startTime,endTime: endTime, u:userId};
	var url = approot+'/data?module=bank&service=BankBill&method=queryBankBill&moduleId='+pageObj.moduleId;
	pageObj.gridManager.refresh(url,queryParams);
	
}
pageObj.getBankBill = function(){
	return [
	      { display: '中心账户开户行', name: 'account_bank', width :'0px',hide:true},
	      { display: '中心账户账号', name: 'account_no', width :'0px',hide:true},
	      { display: '中心账户户名', name: 'account_name', width :'0px',hide:true},
	      { display: '中心子账户开户行', name: 'child_account_bank', width :'0px',hide:true},
	      { display: '中心子账户账号', name: 'child_account_no', width :'0px',hide:true},
	      { display: '中心子账户户名', name: 'child_account_name', width :'0px',hide:true},
	      { display: '竞买人账户开户行', name: 'bidder_account_bank', width :'0px',hide:true},
	      { display: '竞买人户名', name: 'bidder_account_name', width :'0px',hide:true},
	      { display: '状态', name: 'status', width :'0px',hide:true},
	      { display: '备注', name: 'remark', width :'0px',hide:true},
	      { display: '竞买人账号', name: 'bidder_account_no', width :'25%'},
	      { display: '到账时间', name: 'bank_business_date', width :'25%'},
	      { display: '金额', name: 'amount', width :'25%',render:pageObj.showBillMoney},
	      { display: '类型', name: 'bill_type', width :'25%',render:pageObj.typeRender}
	];
}

pageObj.showBillMoney = function(row,rowNumber){
	var amount = row.amount;
	var currency = row.currency;
	return currency+":"+amount;
}

pageObj.reset = function(){
	$("#startTime").val('');
	$("#billtype").val('');
	pageObj.showD();
	$("#endTime").val(pageObj.time);
}
pageObj.showD = function(){
	var d = new Date()
	var vYear = d.getFullYear()
	var vMon = d.getMonth() + 1
	var vDay = d.getDate()
	var h = d.getHours(); 
	var m = d.getMinutes(); 
	var se = d.getSeconds(); 
	pageObj.time = vYear+"-"+(vMon<10 ? "0" + vMon : vMon)+"-"+(vDay<10 ? "0"+ vDay : vDay);
}
$(document).ready(function() {
	$('#btnQuery').click(pageObj.queryData);
	$('#btnReset').click(pageObj.reset);
	pageObj.moduleId = Utils.getUrlParamValue(document.location.href, "moduleId");
	pageObj.gridManager = $("#tabGrid").ligerGrid({
		url: approot + '/data?module=bank&service=BankBill&method=queryBankBill&moduleId=' + pageObj.moduleId,
		columns: pageObj.getBankBill(),
		isScroll: true,// 是否滚动
		pageSizeOptions: [10, 20, 30], 
		showTitle: false,
		width: '99.8%',
		height:435,
	});
	pageObj.showD();
	$("#startTime").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'})});
	$("#endTime").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'})});
	$("#endTime").val(pageObj.time);// 时间
	
});
 