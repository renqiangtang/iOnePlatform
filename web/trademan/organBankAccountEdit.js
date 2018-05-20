//---------------------------------
//页页对象
//---------------------------------
var pageObj = {}
pageObj.organId = null;
pageObj.id = null;
pageObj.bankSelect = null;

//---------------------------------
//页页初始化
//---------------------------------
pageObj.init = function(){
	//transGoodsOperObj.userId = getUserId();
	pageObj.organId= (window.dialogArguments && window.dialogArguments.organId)||Utils.getUrlParamValue(document.location.href, "organId");
	pageObj.id=(window.dialogArguments && window.dialogArguments.id)||Utils.getUrlParamValue(document.location.href, "id");
	pageObj.initSelect();
	pageObj.initData();
	
}

//---------------------------------
//初始化下拉列表
//---------------------------------
pageObj.initSelect = function(){
	var cmd = new Command();
    cmd.module = "trademan";
    cmd.service = "BankManage";
    cmd.method = "getPageSelect";
    cmd.organId=pageObj.organId;
    cmd.success = function (data) {
    	pageObj.bankSelect = data.bank;
    	$("#bank_id").empty();
        $("#bank_id").append("<option value='' checked>--请选择--</option>");
  	    for(var i=0;i<data.bank.length;i++){
  	      $("#bank_id").append("<option value='"+data.bank[i][0]+"'>"+data.bank[i][1]+"</option>");
  	    }
  	    $("#business_type").empty();
  	    $("#business_type").append("<option value='' checked>--请选择--</option>");
        for(var i=0;i<data.business.length;i++){
	      $("#business_type").append("<option value='"+data.business[i][0]+"'>"+data.business[i][1]+"</option>");
	    }
        $("#currency").empty();
        for ( var pro in comObj.Currency) {
        	 $("#currency").append('<option value="' + pro + '" >' + comObj.Currency[pro]+'&nbsp;&nbsp;('+pro + ')</option>');
    	}
  	 };
	cmd.execute();
}

//---------------------------------
//选择银行
//---------------------------------
pageObj.changeBank = function(){
	var p1=$(this).children('option:selected').val();
	for(var i=0;i<pageObj.bankSelect.length;i++){
	     if(p1!="" && p1 == pageObj.bankSelect[i][0]){
	    	 $("#bank_no").val(pageObj.bankSelect[i][2]);
	    	 $("#bank_name").val(pageObj.bankSelect[i][1]);
	    	 break;
	     }
	}
}

//---------------------------------
//初始化数据
//---------------------------------
pageObj.initData = function(){
	if(pageObj.id){
		var cmd = new Command();
	    cmd.module = "trademan";
	    cmd.service = "BankManage";
	    cmd.method = "getBankInfo";
	    cmd.id=pageObj.id;
	    cmd.success = function (data) {
	    	pageObj.showData(data);
		};
		cmd.execute();
	}
}

//---------------------------------
//展示数据
//---------------------------------
pageObj.showData = function(data){
	$("#no").val(Utils.getRecordValue(data, 0, 'no'));
	$("#name").val(Utils.getRecordValue(data, 0, 'name'));
	$("#bank_name").val(Utils.getRecordValue(data, 0, 'bank_name'));
	$("#bank_no").val(Utils.getRecordValue(data, 0, 'bank_no'));
	$("#bank_id").val(Utils.getRecordValue(data, 0, 'bank_id'));
	$("#currency").val(Utils.getRecordValue(data, 0, 'currency'));
	$("#business_type").val(Utils.getRecordValue(data, 0, 'business_type'));
	$("#in_account_mode").val(Utils.getRecordValue(data, 0, 'in_account_mode'));
	var isOutside = Utils.getRecordValue(data, 0, 'is_outside');
	$("input[name='isOutside'][value="+isOutside+"]").attr("checked",true); 
}

//---------------------------------
//保存
//---------------------------------
pageObj.save = function(){
	var name=$("#name").val();
	if(name==null||name==''){
		DialogAlert('户名不能为空');
		return false;
	}
	var no=$("#no").val();
	if(no==null||no==''){
		DialogAlert('银行账号不能为空');
		return false;
	}
	var bank_id=$("#bank_id").val();
	if(bank_id==null||bank_id==''){
		DialogAlert('银行不能为空');
		return false;
	}
	var bank_no=$("#bank_no").val();
	if(bank_no==null||bank_no==''){
		DialogAlert('银行编号不能为空');
		return false;
	}
	var business_type=$("#business_type").val();
	if(business_type==null||business_type==''){
		DialogAlert('用途不能为空');
		return false;
	}
	var currency=$("#currency").val();
	if(currency==null||currency==''){
		DialogAlert('币种不能为空');
		return false;
	}
	var in_account_mode = $("#in_account_mode").val();
	var bank_name = $("#bank_name").val();
	var cmd = new Command();
    cmd.module = "trademan";
    cmd.service = "BankManage";
    cmd.method = "updateBankInfo";
    cmd.id=pageObj.id;
    cmd.name=name;
    cmd.no=no;
    cmd.bank_id = bank_id;
    cmd.bank_name=bank_name;
	cmd.bank_no=bank_no;
	cmd.business_type = business_type;
	cmd.currency=currency;
	cmd.in_account_mode=in_account_mode;
	cmd.is_outside = $('input[name="isOutside"]:checked').val();
	cmd.organId =pageObj.organId;
	cmd.userId = getUserId();
	cmd.success = function (data) {
		var state = data.state;
		if(state=='1') {
			DialogAlert(pageObj.id?"修改银行账号成功":"创建银行账号成功");
			pageObj.id = data.id;
		}else{
			DialogAlert(pageObj.id?"修改银行账号失败":"创建银行账号失败");
		} 
		window.dialogArguments.win.pageObj.queryData();
	};
	cmd.execute();
}


pageObj.close = function(){
	window.close();
}
//---------------------------------
//页面初始化
//---------------------------------
$(document).ready(function(){

	pageObj.init();
	$("#btnSave").click(pageObj.save);
	$("#btnClose").click(pageObj.close);
	$("#bank_id").change(pageObj.changeBank);
	
	
	
});