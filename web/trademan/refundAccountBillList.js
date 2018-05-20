var pageObj={};
pageObj.gridManager=null;
pageObj.moduleId=null;
pageObj.dialog=null;
// -------------------------------------------
// 重置
// -------------------------------------------
pageObj.reset=function(){
	$("#no").val("");
	$("#noticeNo").val("");
	$("#dealerBank").val("");
	$("#dealerName").val("");
	$("#accountBank").val("-1");
	$("#noticeBeginDate").val("");
	$("#noticeEndDate").val("");
	$("#succBeginDate").val("");
	$("#succEndDate").val("");
	$("#inBeginDate").val("");
	$("#inEndDate").val("");
	//$("#bidderCanton").val("");
}

//---------------------------------
//查询按钮事件
//---------------------------------
pageObj.queryData = function () {
	var url = approot + '/data?module=after&service=TransAccount&method=targetAccountBillList&moduleId=' + pageObj.moduleId;
	var obj={};
	obj.no=$("#no").val();
	obj.noticeNo=$("#noticeNo").val();
	obj.dealerBank=$("#dealerBank").val();
	obj.dealerName=$("#dealerName").val();
	obj.accountBank=$("#accountBank").val();
	obj.noticetBeginDate=$("#noticetBeginDate").val();
	obj.noticeEndDate=$("#noticeEndDate").val();
	obj.succBeginDate=$("#succBeginDate").val();
	obj.succEndDate=$("#succEndDate").val();
	obj.inBeginDate=$("#inBeginDate").val();
	obj.inEndDate=$("#inEndDate").val();
	obj.bidderCanton=$("#bidderCanton").val();
	obj.u=pageObj.u;
	obj.goodsType=pageObj.goodsType;
	pageObj.gridManager.refresh(url,obj);

}

//---------------------------------
//导出
//---------------------------------
pageObj.exp = function () {
	var url = approot+ '/download?method=expBillList&service=TransAccount&module=after';
	url += '&no='+$("#no").val();
	url += '&noticeNo='+$("#noticeNo").val();
	url += '&dealerBank='+$("#dealerBank").val();
	url += '&dealerName='+$("#dealerName").val();
	url += '&accountBank='+$("#accountBank").val();
	url += '&noticetBeginDate='+$("#noticetBeginDate").val();
	url += '&noticeEndDate='+$("#noticeEndDate").val();
	url += '&succBeginDate='+$("#succBeginDate").val();
	url += '&succEndDate='+$("#succEndDate").val();
	url += '&inBeginDate='+$("#inBeginDate").val();
	url += '&inEndDate='+$("#inEndDate").val();
	url += '&bidderCanton='+$("#bidderCanton").val();
	url += '&u='+pageObj.u;
	url += '&goodsType='+pageObj.goodsType;
	var form = $("#filefrom")[0];
	form.action = url;
	form.method = "post";
	form.target = "hidden_frame";
	form.submit();

}

// -------------------------------------------
// 渲染标的状态
// -------------------------------------------
pageObj.targetStatus=function(row, rowNamber){
	var status=row.target_status;
	if(status=='0'){
		return '受理中';
	}else if(status=='1'){
		return '审核中';
	}else if(status=='2'){
		return '已审核';
	}else if(status=='3'){
		return '已公告';
	}else if(status=='4'){
		return '交易中';
	}else if(status=='5'){
		return '已成交';
	}else if(status=='6'){
		return '未成交(流拍)';
	}else if(status=='7'){
		return '成交资格审核';
	}
	
}


pageObj.licenseStatus=function(row, rowNamber){
	var licenseStatus=row.license_status;
	var licenseStatusStr = "";
	if(licenseStatus=='0'){
		licenseStatusStr = "无效";
	}else if(licenseStatus=='1'){
		licenseStatusStr = "有效";
	}else if(licenseStatus=='2'){
		licenseStatusStr = "未竞得";
	}else if(licenseStatus=='4'){
		licenseStatusStr = "已竞得";
	}else{
		licenseStatusStr = "无效";
	}
	return licenseStatusStr;
	
}

pageObj.refundTime=function(row, rowNamber){
	var refund_time=row.refund_time+"";
	if(refund_time!=null && refund_time!='null'){
		if(refund_time.length>10){
			return refund_time.substring(0,10);
		}else{
			return refund_time;
		}
	}else{
		return "&nbsp;";
	}
	
}


pageObj.billType=function(row, rowNamber){
	var billType=row.bill_type;
	if(billType=='0'){
		return '其他款';
	}else if(billType=='1'){
		return '保证金';
	}else if(billType=='6'){
		return '错转款';
	}else{
		return '其他款';
	}
	
}

pageObj.renderBillAmount = function (row,rowNamber){
	var amount = row.amount;
	if(amount && !isNaN(amount)){
		var result = CurrencyFormatted(amount);
		return result ;
	}else{
		return "";
	}
}


pageObj.renderEarnestMoney = function (row,rowNamber){
	var amount = row.earnest_money;
	if(amount && !isNaN(amount)){
		var result = CurrencyFormatted(amount);
		return result ;
	}else{
		return "";
	}
}


function CurrencyFormatted(amount) {
    var i = parseFloat(amount);
    if(isNaN(i)) { i = 0.00; }
    var minus = '';
    if(i < 0) { minus = '-'; }
    i = Math.abs(i);
    i = parseInt((i + .005) * 100);
    i = i / 100;
    s = new String(i);
    if(s.indexOf('.') < 0) { s += '.00'; }
    if(s.indexOf('.') == (s.length - 2)) { s += '0'; }
    s = minus + s;
    return s;
}

// -------------------------------------------
// 渲染操作
// -------------------------------------------
pageObj.rendercz=function(row, rowNamber){
	var status=row.tab_status;
	var licenseStatus=row.license_status;
	if(licenseStatus != 4){
		if(status==2){
			return "<a href='javascript:pageObj.refundAccountBill(\"" + row.tab_id + "\",\"" + row.no + "\")'>退款申请</a>" ;
		}else if(status == 3){
			return "已发退款申请";
		}else if(status == 4){
			return "已退款";
		}
	}else{
		return "<a href='javascript:pageObj.refundAccountBill(\"" + row.tab_id + "\",\"" + row.no + "\")' style='color:#000'>竞得(退款)</a>" ;
	}
	
}

pageObj.refundAccountTarget=function(targetId,targetName){
	var opt = {};
	obj.url = "refundAccountTarget.html";
	obj.param = {};
	obj.param.targetId  = targetId;
	obj.param.targetName = targetName;
	obj.feature = "dialogWidth=900px;dialogHeight=500px;";
	DialogModal(obj);
}

pageObj.refundAccountBill=function(billId){
	var opt = {};
	obj.url = "refundAccountBill.html";
	obj.param = {};
	obj.param.billId  = billId;
	obj.feature = "dialogWidth=900px;dialogHeight=400px;";
	DialogModal(obj);
}


pageObj.groupRender=function(target_id,groupdata){
	return groupdata[0].no+"(公告："+groupdata[0].notice_name+"&nbsp;)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:pageObj.refundAccountTarget(\""+target_id+ "\",\""+groupdata[0].no+ "\")'>标的退款申请</a>";
}

// -------------------------------------------
// 初始化表格
// -------------------------------------------
pageObj.initGrid = function () {
	pageObj.gridManager = $("#refundAccountGrid").ligerGrid({
		url: approot + '/data?module=after&service=TransAccount&method=targetAccountBillList&goodsType='+pageObj.goodsType+'&u='+pageObj.u,
		columns: [
				    { display: '竞买/竞卖人', name: 'bidder_name', align: 'center', width: '10%'},
					{ display: '竞买人联系电话', name: 'mobile', width: '10%',align: 'center' },
					{ display: '保证金', name: 'earnest_money', width: '10%',align: 'center',render:pageObj.renderEarnestMoney},
					{ display: '实交保证金', name: 'amount', width: '10%', align: 'center',render:pageObj.renderBillAmount},
					{ display: '汇入日期', name: 'bank_business_date', width: '8%', align: 'center' },
					{ display: '交款人', name: 'bidder_account_name', width: '10%', align: 'center'},
					{ display: '交款银行', name: 'bidder_account_bank',align: 'center' ,width: '10%'}, 
					{ display: '交款银行账号', name: 'bidder_account_no' ,align: 'center', width: '12%'},
					{ display: '中心银行', name: 'account_bank' ,align: 'center', width: '10%'},
					{ display: '中心银行子账号', name: 'child_account_no' ,align: 'center', width: '12%'},  
					{ display: '中心银行主账号', name: 'account_no' ,align: 'center', width: '12%'},
					{ display: '是否竞得', name: 'license_status' ,align: 'center', width: '10%',render:pageObj.licenseStatus},
					{ display: '退保证金时间', name: 'refund_time', width: '8%',render:pageObj.refundTime},
					{ display: '标的状态', name: 'target_status' ,align: 'center', width: '10%',render:pageObj.targetStatus},
					{ display: '入账类型', name: 'bill_type', width: '8%',render:pageObj.billType},
					{ display: '操作', align: 'center', width: '10%',render:pageObj.rendercz},
					{ display: 'target_id', name: 'target_id',align: 'center' ,width: '1',hide :true}
				],
	    isScroll: true,// 是否滚动
	    pageSizeOptions: [10, 20, 30], 
	    showTitle: false,
	    rownumbers : true,
	    checkbox:false,
	    enabledSort:false,
	    height:"100%",
	   groupColumnName:'target_id',
	    groupRender: pageObj.groupRender
	});	

}



//-------------------------------------------
//初始化查询条件
//-------------------------------------------
pageObj.initParams=function(tab_id,status){
	var cmd = new Command();
	cmd.module = "after";
	cmd.service = "TransAccount";
	cmd.method = "initPageParams";
	cmd.goodsType = pageObj.goodsType;
	cmd.success = function (data) {
		var bankList = data.bankList;
		for ( var i = 0; i < bankList.length; i++) {
			Utils.addOption('accountBank', bankList[i].no, bankList[i].bank_name);
		}
//		var cantonList = data.cantonList;
//		for ( var i = 0; i < cantonList.length; i++) {
//			Utils.addOption('bidderCanton', cantonList[i].id, cantonList[i].name);
//		}
//      if(pageObj.goodsType == '501'){
//			$("#trustDiv").show();
//			$("#trustSelectDiv").show();
//			for ( var i = 0; i < cantonList.length; i++) {
//				Utils.addOption('trustCanton', cantonList[i].id, cantonList[i].name);
//			}
//		}
		
	};
	cmd.execute();
}

$(document).ready(function () {
	pageObj.moduleId=(window.dialogArguments && window.dialogArguments.moduleId && window.dialogArguments.moduleId)
	||Utils.getUrlParamValue(document.location.href, "moduleId");
	pageObj.goodsType = Utils.getPageValue('goodsType');
	pageObj.u = getUserId();
	$("#noticeBeginDate").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'})});
	$("#noticeEndDate").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'})});
	$("#succBeginDate").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'})});
	$("#succEndDate").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'})});
	$("#inBeginDate").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'})});
	$("#inEndDate").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'})});
	//$("#trustDiv").hide();
	//$("#trustSelectDiv").hide();
	pageObj.initParams();
	pageObj.initGrid();
	
	$("#btnReset").click(function(){
		pageObj.reset();
	});
	$("#btnQuery").click(function(){
		pageObj.queryData();
	});
	$("#btnExp").click(function(){
		pageObj.exp();
	});
});