//---------------------------------
//页页对象
//---------------------------------
var pageUserObj = {};
//---------------------------------
//表格管理器
//---------------------------------
pageUserObj.roleGridManager = null;
pageUserObj.addRoleIds = ";";
pageUserObj.deleteRoleIds = ";";
pageUserObj.goodsType = null
pageUserObj.limittype = null;
//---------------------------------
//表格管理器
//---------------------------------
pageUserObj.id = null;

//---------------------------------
//初始化数据
//---------------------------------
pageUserObj.initData = function () {
	pageUserObj.id = Utils.getUrlParamValue(document.location.href, "id");
	if (pageUserObj.id == null || pageUserObj.id == "" || pageUserObj.id.toLowerCase() == "null") {
		pageUserObj.id = "";
		pageUserObj.cakeyId = "";
		$("#txtValidity").val("100");
		$("#btnActive").hide();
		$("#btnInvalid").hide();
		return;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.method = "getUserData";
	cmd.id = pageUserObj.id;
	cmd.success = function(data) {
		
		
	};
	cmd.execute();
}

pageUserObj.checkValidityOnlyNumber = function () 
{ 
	if (!(event.keyCode == 46) && !(event.keyCode == 8) && !(event.keyCode == 37) && !(event.keyCode == 39)) 
		if (!((event.keyCode >= 48 && event.keyCode <= 57) || (event.keyCode >= 96 && event.keyCode <= 105))) 
			event.returnValue = false; 
} 

//---------------------------------
//保存按钮事件
//---------------------------------
pageUserObj.saveData = function () {
	if(pageUserObj.checkForm()==false){
		return;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.id = pageUserObj.id;
	cmd.remark = $("#remark").val();
	cmd.method = "setBlackUserData";
	if(pageUserObj.limittype==0){
		cmd.limittype=4;
	}else if(pageUserObj.limittype==4){
		cmd.limittype=0;
	}
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			pageUserObj.dialogManager = getGlobalAttribute("blackUserDeleteDialog");
			removeGlobalAttribute("blackUserDeleteDialog");
			pageUserObj.dialogManager.parentPageObj.queryData();
			DialogAlert(data.message);
			pageUserObj.dialogManager.dialog.close();
		} else {
			DialogError('失败,错误原因：' + data.message, window);
			return false;
		}
	};
	cmd.execute();
}

pageUserObj.checkForm = function (){
	var remark=$("#remark").val();
	var name=$("#name").val();
	if(remark==null || remark=="" || remark=="null"){
		DialogError("原因不能为空！");
		return false;
	}else if(name==null || name=="" || name=="null"){
		DialogError("证件号码/名称出现错误！");
		return false;
	}
	return true;
}
//---------------------------------
//页面初始化
//---------------------------------
pageUserObj.initHtml = function() {
	pageUserObj.moduleId = Utils.getUrlParamValue(document.location.href, "moduleId");
	pageUserObj.userId = getUserId(); 
	pageUserObj.id = Utils.getUrlParamValue(document.location.href, "id");
	pageUserObj.name = Utils.getUrlParamValue(document.location.href, "name");
	pageUserObj.no = Utils.getUrlParamValue(document.location.href, "no");
	pageUserObj.limittype = Utils.getUrlParamValue(document.location.href, "limittype");
	$("#btnSave").click(pageUserObj.saveData);
	pageUserObj.goodsType= Utils.getUrlParamValue(document.location.href, "goodsType");
	pageUserObj.bidderType= Utils.getUrlParamValue(document.location.href, "biddertype");
	if(pageUserObj.name!=null && pageUserObj.name!="null"){
		$("#name").val(pageUserObj.name);
	}
	if(pageUserObj.no!=null && pageUserObj.no!="null"){
		$("#no").val(pageUserObj.no );
	}
	if(pageUserObj.bidderType==1){
		$("#grzjh").html("组织机构代码:");
	}
	if(pageUserObj.limittype==0){
		$("#remarktext").html("<span class='c6'>*</span>移出黑名单原因");
	}else if(pageUserObj.limittype==4){
		$("#remarktext").html("<span class='c6'>*</span>加入黑名单原因");
	}
}

$(document).ready(pageUserObj.initHtml);


