var pageObj = {}
pageObj.moduleId=null;
pageObj.tab_id=null;
//-----------------------------------------
//初始化页面
//-----------------------------------------
pageObj.initHtml=function(){
	var cmd = new Command();
	cmd.module = "after";
	cmd.service = "TransAccount";
	cmd.method = "getRefundAccountViewData";
	cmd.moduleId=pageObj.moduleId;
	cmd.tab_id=pageObj.tab_id;
	cmd.success = function (data) {
		var no=Utils.getRecordValue(data, 0,"no");
		var name=Utils.getRecordValue(data, 0,"name");
		var target_status=Utils.getRecordValue(data, 0,"target_status");
		var bill_type=Utils.getRecordValue(data, 0,"bill_type");
		var tab_status=Utils.getRecordValue(data, 0,"tab_status");
		if(no!=null&&no!=''){
			$("#target").html(no);
		}else{
			if(name!=null&&name!=''){
				$("#target").html(name);
			}
		}
		$("#target_status").html(comObj.targetStatusObj[target_status]);
		$("#bidder_name").html(Utils.getRecordValue(data, 0,"bidder_name"));
		$("#apply_no").html(Utils.getRecordValue(data, 0,"apply_no"));
		if(bill_type=='0'){
			$("#bill_type").html('其它款');
		}else if(bill_type=='1'){
			$("#bill_type").html('保证金');
		}else if(bill_type=='2'){
			$("#bill_type").html('服务费');
		}else if(bill_type=='3'){
			$("#bill_type").html('收成交款');
		}else if(bill_type=='4'){
			$("#bill_type").html('退成交款');
		}else if(bill_type=='5'){
			$("#bill_type").html('暂交款');
		}else if(bill_type=='6'){
			$("#bill_type").html('错转款');
		}
		if(tab_status=='0'){
			$("#tab_status").html('录入中');
		}else if(tab_status=='1'){
			$("#tab_status").html('审核通过');
		}else if(tab_status=='2'){
			$("#tab_status").html('子账户已转入主账户');
		}else if(tab_status=='3'){
			$("#tab_status").html('退回');
		}
		$("#account_bank").html(Utils.getRecordValue(data, 0,"account_bank"));
		$("#account_no").html(Utils.getRecordValue(data, 0,"account_no"));
		$("#account_name").html(Utils.getRecordValue(data, 0,"account_name"));
		$("#child_account_bank").html(Utils.getRecordValue(data, 0,"child_account_bank"));
		$("#child_account_no").html(Utils.getRecordValue(data, 0,"child_account_no"));
		$("#child_account_name").html(Utils.getRecordValue(data, 0,"child_account_name"));
		$("#bidder_account_bank").html(Utils.getRecordValue(data, 0,"bidder_account_bank"));
		$("#bidder_account_no").html(Utils.getRecordValue(data, 0,"bidder_account_no"));
		$("#bidder_account_name").html(Utils.getRecordValue(data, 0,"bidder_account_name"));
		$("#bank_business_date").html(Utils.getRecordValue(data, 0,"bank_business_date"));
		
	};
	cmd.execute();
}

//-----------------------------------------
//格式化账号四位隔开
//-----------------------------------------
pageObj.forMatNo=function(no){
	var str=no.replace(/(?=(?:\d{4})+(?!\d))/g,' ');
	return str;
	
}

$(document).ready(function () {
	pageObj.moduleId=(window.dialogArguments && window.dialogArguments.moduleId && window.dialogArguments.moduleId)
	||Utils.getUrlParamValue(document.location.href, "moduleId");
	pageObj.tab_id=(window.dialogArguments && window.dialogArguments.tab_id && window.dialogArguments.tab_id)
	||Utils.getUrlParamValue(document.location.href, "tab_id");
	
	pageObj.initHtml();
	
});