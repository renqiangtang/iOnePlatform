//---------------------------------
//页页对象
//---------------------------------
var pageObj = {};
//---------------------------------
//表格对象
//---------------------------------
pageObj.id = null;
pageObj.userId = null;
pageObj.iframeParent = null;
//---------------------------------
//页面初始化
//---------------------------------
pageObj.initHtml = function() {
	var url = document.location.href;
	pageObj.userId = (window.dialogArguments && window.dialogArguments.userId)||Utils.getUrlParamValue(url, "userId");
	pageObj.id = (window.dialogArguments && window.dialogArguments.id)||Utils.getUrlParamValue(url, "id");
	var cmd = new Command();
	cmd.module = "bank";
	cmd.service = "Bank";
	cmd.method = "bankBusinessDetail";
	cmd.id = pageObj.id;
	cmd.u = pageObj.userId;
	cmd.success = function (data) {
		$("#id").html("&nbsp;"+data.id);
		$("#organName").html("&nbsp;"+data.organ_name);
		$("#trxCode").html("&nbsp;"+data.trx_code);
		$("#trxCodeName").html("&nbsp;"+pageObj.trxCodeRender(data.trx_code));
		$("#bankId").html("&nbsp;"+data.bank_id);
		$("#bankName").html("&nbsp;"+data.bank_name);
		$("#sequenceNo").html("&nbsp;"+data.sequence_no);
		$("#busiDate").html("&nbsp;"+data.busi_date);
		$("#content").val(data.content);
		$("#response").val(data.response);
		$("#status").html("&nbsp;"+pageObj.statusRender(data.status));
	};
	cmd.execute();
	
}

pageObj.trxCodeRender = function(trxCode) {
	if (trxCode == '10000') {
		return '连接测试';
	}else	if (trxCode == '10001') {
		return '创建子账号';
	}else	if (trxCode == '10002') {
		return '来帐通知';
	}else	if (trxCode == '10003') {
		return '计息';
	}else	if (trxCode == '10004') {
		return '子转总';
	}else	if (trxCode == '10005') {
		return '销户';
	}else	{
		return trxCode;
	}
}

pageObj.statusRender = function(status) {
	if (status == '0') {
		return '未处理';
	}else	if (status == '1') {
		return '已发送';
	}else	if (status == '2') {
		return '成功';
	}else	if (status == '3') {
		return '失败';
	}else{
		return status;
	}
}

$(document).ready(function(){
	pageObj.initHtml();
	$("#btnClose input").click(function(){
		window.close();
	});
});



