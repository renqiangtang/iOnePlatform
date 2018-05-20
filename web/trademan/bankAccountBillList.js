//---------------------------------
//页页对象
//---------------------------------
var pageObj = {}
// ---------------------------------

// ---------------------------------
// 表格对象
// ---------------------------------
pageObj.gridManager = null;
pageObj.timer = null;// 刷新定时器
pageObj.intervalTime = 1000;// 刷新频率为1000ms




// ---------------------------------
// 展示正在活动的流程
// ---------------------------------
pageObj.initGrid = function(){
	pageObj.gridManager = $("#gridDiv").ligerGrid({
		url: approot + '/data?module=bank&service=Bank&method=bankAccountBillList&targetId='+pageObj.targetId,
		columns: [
		    { display: '子账号', name: 'child_account_no', width :'15%'},
		    { display: '金额', name: 'amount', width :'15%',render:pageObj.renderAmount,align:'right'},
		    { display: '日期', name: 'bank_business_date', width :'15%'},
			{ display: '类型', name: 'bill_type', width :'10%',render:pageObj.renderBillType},
			{ display: '缴款人', name: 'bidder_account_name', width :'10%',render:pageObj.renderAccountName},
			{ display: '状态', name: 'status', width :'20%',render:pageObj.renderStatus},
			{ display: '是否使用', name: 'child_account_freed', width :'10%',render:pageObj.renderFeed}
		],
		groupColumnName:'child_no',
		groupRender: pageObj.groupRender ,
		usePager: false,
		selectRowButtonOnly: true,
		rownumbers: false,
		isScroll : true, // 是否滚动
		frozen : false,// 是否固定列
		showTitle : false,
		width : '99.8%',
		height : 600
	});
}

pageObj.groupRender  = function (child_no,groupdata){
	if(groupdata){
		var row=groupdata[0];
		var bidderName=row.bidder_name;
		var accountFeed=row.child_account_freed;
		var licenseNo=row.license_no;
		var licenseStatus=row.license_status;
		
		if(licenseStatus == 0){
			licenseStatus = '申请中';
		}else if(licenseStatus == 1){
			licenseStatus = '有效';
		}else if(licenseStatus == 2){
			licenseStatus = '未竞得';
		}else if(licenseStatus == 4){
			licenseStatus = '已竞得';
		}
		if(accountFeed == 0){
			accountFeed = "使用中(<a href='javascript:void(0)' onclick='javascript:pageObj.deleteChildNo(\""+row.apply_id + "\");return false;'>销户</a>)";
		}else if(accountFeed == 1){
			accountFeed = '已销户'
		}
		if(pageObj.targetStatus <=4){
			return '竞买人:***(子账号：'+row.child_no+')'
		}else{
			return '竞买人:'+bidderName+'(子账号：'+row.child_no+'); 状态:'+licenseStatus+';子账号状态:'+accountFeed;
		}
	}
}

pageObj.renderAmount = function (row,rowNamber){
	var amount = row.amount;
	var currency = row.currency;
	if(amount && !isNaN(amount)){
		return amount.addComma() + "元&nbsp;";
	}else{
		return "";
	}
}

pageObj.renderBillType = function(row, rowNamber) {
	var status = row.bill_type;
	if (status == '0') {
		return '其它款';
	}else	if (status == '1') {
		return '保证金';
	}else	if (status == '2') {
		return '服务费';
	}else	if (status == '3') {
		return '成交款';
	}else	if (status == '4') {
		return '退成交款';
	}else	if (status == '5') {
		return '暂交款';
	}else	if (status == '6') {
		return '错转款';
	}
	return status;
}

pageObj.renderStatus = function(row, rowNamber) {
	var status = row.status;
	if (status == '0') {
		return '录入中';
	}else	if (status == '1') {
		return "审核通过 : <a href='javascript:void(0)'  onclick='javascript:pageObj.submitChildNo(\""+row.id + "\");return false;'>转入主账户</a>";
	}else	if (status == '2') {
		return '已转入主账户';
	}else	if (status == '3') {
		return '已申请退款';
	}else	if (status == '4') {
		return '已退款';
	}
	return status
}


pageObj.renderFeed = function(row, rowNamber) {
	var status = row.child_account_freed;
	if (status == '0') {
		return '使用中';
	}else	if (status == '1') {
		return '已销户';
	}
	return status;
}

render:pageObj.renderAccountName = function(row, rowNamber) {
	var name = row.bidder_account_name;
	if (pageObj.targetStatus <=4) {
		return '***';
	}else{
		return name;
	}
}

pageObj.getStatusShow = function(status){
	if(pageObj.goodsType == "501"){
		return comObj.plowTargetStatusObj[status];
	}else{
		return comObj.targetStatusObj[status];
	}
}

// ---------------------------------
// 查询按钮事件
// ---------------------------------
pageObj.queryData = function () {
	var queryParams = {};
	var url = approot + '/data?module=bank&service=Bank&method=bankAccountBillList&targetId='+pageObj.targetId;
	for(var key in queryParams){
		url = Utils.setParamValue(url, key, queryParams[key], false, '=', '&');
	}
	pageObj.gridManager.refresh(url,queryParams);
}

// ---------------------------------
// 发起销户
// ---------------------------------
pageObj.deleteChildNo = function(applyId) {
	DialogConfirm('您确定要执行销户操作吗?', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "bank";
			cmd.service = "Bank";
			cmd.method = "deleteChildNo";
			cmd.applyId = applyId;
			cmd.success = function(data) {
			   DialogAlert(data.message);
			   pageObj.queryData();
			}
			cmd.execute();
		}
	});
}


// ---------------------------------
// 发起子转总
// ---------------------------------
pageObj.submitChildNo = function(billId) {
	DialogConfirm('您确定要执行子转总操作吗?', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "bank";
			cmd.service = "Bank";
			cmd.method = "submitChildNo";
			cmd.billId = billId;
			cmd.success = function(data) {
			   DialogAlert(data.message);
			   pageObj.queryData();
			}
			cmd.execute();
		}
	});
}


// ---------------------------------
// 页面初始化
// ---------------------------------
$(document).ready(function(){
	pageObj.targetId = (window.dialogArguments && window.dialogArguments.id)||Utils.getUrlParamValue(document.location.href, "id");
	pageObj.targetNo = (window.dialogArguments && window.dialogArguments.no)||Utils.getUrlParamValue(document.location.href, "no");
	pageObj.targetStatus = (window.dialogArguments && window.dialogArguments.status)||Utils.getUrlParamValue(document.location.href, "status");
	pageObj.goodsType = (window.dialogArguments && window.dialogArguments.goodsType)||Utils.getUrlParamValue(document.location.href, "goodsType");
	pageObj.targetStatusShow = pageObj.getStatusShow(pageObj.targetStatus);
	$("#targetShow").html("&nbsp;&nbsp;&nbsp;&nbsp;标的:"+pageObj.targetNo+"("+pageObj.targetStatusShow+")");
	pageObj.initGrid();
});