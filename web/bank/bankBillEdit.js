//---------------------------------
//页页对象
//---------------------------------
var pageObj = {};
//---------------------------------
//表格对象
//---------------------------------
pageObj.apply_id = null;
pageObj.id = null;
pageObj.userId = null;
pageObj.inAccountMode = null;
pageObj.moduleId = null;
pageObj.accountNo = '';
pageObj.accountName = '';
pageObj.accountBank = '';
pageObj.iframeParent = null;
pageObj.child_account_no = '';
pageObj.child_account_name = '';
pageObj.child_account_bank = '';


pageObj.today = function(){
	var date = new Date(); //日期对象
	var now = date.getFullYear()+"-"; 
	if(date.getMonth()+1<10)
		now = now + "0" + (date.getMonth()+1)+"-";
	else
		now = now + (date.getMonth()+1)+"-";
	if(date.getDate()<10)
		now = now + "0" + date.getDate()+" ";
	else
		now = now + date.getDate()+" ";
	if(date.getHours()<10)
		now = now + "0" + date.getHours()+":";
	else
		now = now + date.getHours()+":";
	if(date.getMinutes()<10)
		now = now + "0" + date.getMinutes()+":";
	else
		now = now + date.getMinutes()+":";
	if(date.getSeconds()<10)
		now = now + "0" + date.getSeconds();
	else
		now = now + date.getSeconds();
	return now;
}

pageObj.enumeration = function(){
	var status = $('#status').val();
	var rtnStr = "录入中";
	if(status!=null&&status!=""&&status.toLowerCase()!="null"){
		if (status == '0') {
			rtnStr = '录入中';
		}else	if (status == '1') {
			rtnStr = '审核通过';
		}else	if (status == '2') {
			rtnStr = '子账号已转入主账号';
		}else	if (status == '3') {
			rtnStr = '退回';
		}
	}
	return rtnStr;
}

//---------------------------------
//初始化数据
//---------------------------------
pageObj.initCreateData = function (flag) {
	pageObj.userId = getUserId();
	if (pageObj.id == null || pageObj.id == "" || pageObj.id.toLowerCase() == "null") {
		$('#status').val("0");
		$('#idStatus').text(pageObj.enumeration);
		$("#bankBusinessDate").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})});
		$("#bankBusinessDate").val(pageObj.today);
		$('#noTa').val(pageObj.accountNo);
		$('#nameTa').val(pageObj.accountName);
		$('#bankNameTa').val(pageObj.accountBank);
		$('#bidderAccountNo').val('');
		$('#bidderAccountNoDouble').val('');
		$('#bidderAccountName').val('');
		$('#bidderAccountBank').val('');
		$('#amount').val('');
		$('#bankBusinessNo').val('');
		$('#remark').val('');
		if(flag){
			var method = '';
			if(pageObj.inAccountMode=='0'||pageObj.inAccountMode=='1'){
				$('#sn').html("流水号:");
				method = "getBankSNListData";
			}else{
				$('#sn').html("子账号:");
				method = "getBankSubListData";
			}
			pageObj.initBankSNSubInfo(method);
		}
	}
}

pageObj.initUpdateData = function () {
	if (pageObj.id != null && pageObj.id != "" && pageObj.id.toLowerCase() != "null") {
		if(pageObj.apply_id != null && pageObj.apply_id != "" &&pageObj.apply_id != "null"){
			pageObj.queryTabaAccount(pageObj.apply_id,pageObj.inAccountMode);
			$('#noTa').val(pageObj.accountNo);
			$('#nameTa').val(pageObj.accountName);
			$('#bankNameTa').val(pageObj.accountBank);
		}
		pageObj.userId = getUserId();
		var cmd = new Command();
		cmd.module = "bank";
		cmd.service = "BankBill";
		cmd.method = "getBankBillData";
		cmd.moduleId = pageObj.moduleId;
		cmd.id = pageObj.id;
		cmd.inAccountMode = pageObj.inAccountMode;
		cmd.success = function(data) {
			var method = "";
			$('#status').val(Utils.getRecordValue(data, 0, "status"));
			$('#idStatus').text(pageObj.enumeration);
			$('#bidderAccountNo').val(Utils.getRecordValue(data, 0, "bidder_account_no"));
			$('#bidderAccountNoDouble').val(Utils.getRecordValue(data, 0, "bidder_account_no"));
			$('#bidderAccountName').val(Utils.getRecordValue(data, 0, "bidder_account_name"));
			$('#bidderAccountBank').val(Utils.getRecordValue(data, 0, "bidder_account_bank"));		
			$('#amount').val(Utils.getRecordValue(data, 0, "amount"));
			var money = $('#amount').val();
			var money = new Number(money);
			$('#rmb').html("<font color='red'>"+money.toUpcase()+'</font>');
			$('#bankBusinessNo').val(Utils.getRecordValue(data, 0, "bank_business_no"));
			$('#remark').val(Utils.getRecordValue(data, 0, "remark"));
			if(Utils.getRecordValue(data, 0, "is_bank_input")=='2'){
				$("#bankBusinessDate").val(Utils.getRecordValue(data, 0, "bank_business_date"));
				$("#bankBusinessDate").attr("disabled",true);
				$('#amount').attr("disabled",true);
				$('#noTa').attr("disabled",true);
				$('#nameTa').attr("disabled",true);
				$('#bankNameTa').attr("disabled",true);
				$('#createDate').attr("disabled",true);
			}else{
				$("#bankBusinessDate").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})});
				$("#bankBusinessDate").val(Utils.getRecordValue(data, 0, "bank_business_date"));
			}
			if(pageObj.inAccountMode=='0'||pageObj.inAccountMode=='1'){
				$('#ai').html("流水号:");
				method = "getBankSNListData";
			}else{
				$('#ai').html("子账号:");
				method = "getBankSubListData";
			}
			pageObj.initBankSNSubInfo(method);
			$("#applyId").val(pageObj.apply_id);
			
		};
		cmd.execute();
	}
}

//---------------------------------
//保存按钮事件
//---------------------------------
pageObj.saveData = function () {
	if ($("#bidderAccountNo").val() == ''){
		DialogAlert('交款账号不能为空');
		return false;
	}
	if(isNaN($("#bidderAccountNo").val())){
		DialogAlert("交款账号必须是数字")
		return false;
	}
	if ($("#bidderAccountNoDouble").val() == ''){
		DialogAlert('重新输入交款账号不能为空');
		return false;
	}
	if ($("#bidderAccountNo").val() != $("#bidderAccountNoDouble").val()){
		DialogAlert('交款账号必须与重新输入交款账号一致');
		return false;
	}
	if ($("#bidderAccountName").val() == ''){
		DialogAlert('交款户名不能为空');
		return false;
	}
	if ($("#bidderAccountBank").val() == ''){
		DialogAlert('交款账户开户行不能为空');
		return false;
	}
	if ($("#amount").val() == ''){
		DialogAlert('金额不能为空');
		return false;
	}
	if(isNaN($("#amount").val())){
		DialogAlert("金额必须是数字")
		return false;
	}
	var cmd = new Command();
	cmd.module = "bank";
	cmd.service = "BankBill";
	cmd.method = "updateBankBillData";
	cmd.moduleId = pageObj.moduleId;
	cmd.id = pageObj.id;	
	cmd.u = getUserId();
	cmd.bill_type = "1";
	cmd.account_no = $("#noTa").val();
	cmd.account_name = $("#nameTa").val();
	cmd.account_bank = $("#bankNameTa").val();
	cmd.child_account_no = pageObj.child_account_no;
	cmd.child_account_name = pageObj.child_account_name;
	cmd.child_account_bank = pageObj.child_account_bank;
	cmd.bidder_account_no = $('#bidderAccountNo').val();
	cmd.bidder_account_name = $('#bidderAccountName').val();
	cmd.bidder_account_bank = $('#bidderAccountBank').val();
	cmd.bank_business_no = $('#bankBusinessNo').val();
	cmd.bank_business_date = $('#bankBusinessDate').val();
	var amount = $('#amount').val().toString();
	if (amount != "" && amount.indexOf(".") != -1) {
		cmd.amount = amount.substring(0, amount.indexOf(".") + 3);
    }else{
    	cmd.amount = $('#amount').val();
    }
	cmd.apply_id = $('#applyId').val();
	cmd.is_bank_input = '1';
	cmd.create_user_id = pageObj.userId;
	cmd.status = $('#status').val();
	cmd.is_valid = '1';
	cmd.remark = $("#remark").val();
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			DialogAlert('入账单保存成功');
			if(data.id!=null)
				pageObj.id = data.id;
			pageObj.iframeParent.pageObj.reload();
		} else {
			DialogError('入账单保存失败,错误原因：' + data.message);
			return false;
		}
	};
	if($("#applyId").val() != ''){
		var name = null;
		var cmd1 = new Command();
		cmd1.module = "bank";
		cmd1.service = "BankBill";
		cmd1.method = "saveBankBillData";
		cmd1.moduleId = pageObj.moduleId;
		cmd1.apply_id = $("#applyId").val();
		cmd1.u = getUserId();
		cmd1.success = function (data) {
			var state = data.state;
			if(state == '1') {
				name = Utils.getRecordValue(data, 0, 'name');
			} 
		};
		cmd1.execute();
		if(name!=$("#bidderAccountName").val()){
			DialogConfirm('由于户名与竞买人的名称不一致，请检查竞买人对应的流水号、子账号、交款户名是否错误！保存后本次入账不能作为保证金使用，而作为错转款入账。', function (yes) {
				if (yes) {
					cmd.execute();
				}
			});
		}else{
			cmd.execute();
		}
	}else{
		cmd.execute();
	}
}
//查找银行账户
pageObj.queryTabaAccount = function(tabaid,type){
	$('#noTa').val('');
	$('#nameTa').val('');
	$('#bankNameTa').val('');
	if(tabaid != null && tabaid != "" && tabaid != "null"){
		var cmd = new Command();
		cmd.module = "bank";
		cmd.service = "BankBill";
		cmd.method = "queryTabaAccount";
		cmd.moduleId = pageObj.moduleId;
		cmd.id = tabaid;
		cmd.success = function (data) {
			pageObj.accountNo = Utils.getRecordValue(data, 0, 'account_no');
			pageObj.accountName = Utils.getRecordValue(data, 0, 'account_name');
			pageObj.accountBank = Utils.getRecordValue(data, 0, 'account_bank');
			pageObj.child_account_no = Utils.getRecordValue(data, 0, 'child_account_no');
			pageObj.child_account_name = Utils.getRecordValue(data, 0, 'child_account_name');
			pageObj.child_account_bank = Utils.getRecordValue(data, 0, 'child_account_bank');
			$('#noTa').val(pageObj.accountNo);
			$('#nameTa').val(pageObj.accountName);
			$('#bankNameTa').val(pageObj.accountBank);
		};
		cmd.execute();
	}
}
//获取银行登录人员的信息
pageObj.initBankSNSubInfo = function(method){
	var cmd = new Command();
	cmd.module = "bank";
	cmd.service = "BankBill";
	cmd.method = method;
	cmd.moduleId = pageObj.moduleId;
	cmd.success = function (data) {
		//如果是流水号显示流水号
		var no=""
		$("#applyId").append("<option value=''>--请选择--</option>");
		if(pageObj.inAccountMode=="0"){
			no="order_no";
		}else if(pageObj.inAccountMode=="2"){
			no="child_account_no";
		}
		for(var i=0;i<data.rs.length;i++){
	  		$("#applyId").append("<option value='"+Utils.getRecordValue(data, i, "id")+"'>"+Utils.getRecordValue(data, i, no)+"</option>");
	  	}
	};
	cmd.execute();
}

//---------------------------------
//页面初始化
//---------------------------------
pageObj.initHtml = function() {
	var url = document.location.href;
	pageObj.userId = getUserId();
	pageObj.moduleId = (window.dialogArguments && window.dialogArguments.moduleId)||Utils.getUrlParamValue(url, "moduleId");
	pageObj.id = (window.dialogArguments && window.dialogArguments.id)||Utils.getUrlParamValue(url, "id");
	pageObj.apply_id = (window.dialogArguments && window.dialogArguments.applyid)||Utils.getUrlParamValue(url, "applyid");
	pageObj.inAccountMode = (window.dialogArguments && window.dialogArguments.inAccountMode)||Utils.getUrlParamValue(url, "inAccountMode");
	pageObj.iframeParent=getIframeWin();
	if(pageObj.inAccountMode=="0"){
		$("#ai").html("流水号:");
	}
	if(pageObj.inAccountMode=="2"){
		$("#ai").html("子账号:");
	}
	$("#applyId").change(function(){pageObj.queryTabaAccount($("#applyId").val()),pageObj.inAccountMode});
	pageObj.initCreateData(true);
	pageObj.initUpdateData();
	$("#btnSave input").click(pageObj.saveData);
	$("#btnClose input").click(function(){
		pageObj.iframeParent.pageObj.reload();
		window.close();
	});
	
	$('#amount').keyup(function(){
		var money = $('#amount').val();
		if(isNaN(money)){
			$('#rmb').html('');
		}else{
			var money = new Number(money);
			$('#rmb').html("<font color='red'>"+money.toUpcase()+'</font>');
		}
	});
	
}

$(document).ready(function(){
	pageObj.initHtml();
});



