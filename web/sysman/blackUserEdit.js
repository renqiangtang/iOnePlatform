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
pageUserObj.goodsType = null;
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
	if($("input[name$='biddertype']:checked").val()!=1 && $("input[name$='biddertype']:checked").val()!=0){
		DialogAlert('请选择一个竞买人类型', window);
		return;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.method = "updateBlackUserData";
	cmd.id = pageUserObj.id;
	cmd.ban_type = pageUserObj.goodsType;
	cmd.bidder_type = $("input[name$='biddertype']:checked").val();
	cmd.create_user_id=getUserInfoObj().userName;
	Utils.getForm(cmd);
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			pageUserObj.id = data.id;
			pageUserObj.dialogManager = getGlobalAttribute("blackUserEditDialog");
			removeGlobalAttribute("blackUserEditDialog");
			pageUserObj.dialogManager.parentPageObj.queryData();
			DialogAlert('保存黑名单成功完成');
			pageUserObj.dialogManager.dialog.close();
		} else {
			DialogError('保存黑名单失败,错误原因：' + data.message, window);
			return false;
		}
	};
	cmd.execute();
}


pageUserObj.btypeChange = function() {
	var bval=$("input[name$='biddertype']:checked").val();
	if(bval==0){//个人
		$("#grzjh").html("<span class='c6'>*</span>证件号码:");
		$("#mc").html("<span class='c6'>*</span>姓名:");
	}else if(bval==1){//企业
		$("#grzjh").html("<span class='c6'>*</span>组织机构代码:");
		$("#mc").html("<span class='c6'>*</span>名称:");
	}
}


//---------------------------------
//页面初始化
//---------------------------------
pageUserObj.initHtml = function() {
	pageUserObj.moduleId = Utils.getUrlParamValue(document.location.href, "moduleId");
	var biddertype = Utils.getUrlParamValue(document.location.href, "biddertype");
	var listedcompany = Utils.getUrlParamValue(document.location.href, "is_listedcompany");
	var name = Utils.getUrlParamValue(document.location.href, "name");
	var no = Utils.getUrlParamValue(document.location.href, "no");
	var address = Utils.getUrlParamValue(document.location.href, "address");
	var tel = Utils.getUrlParamValue(document.location.href, "tel");
	if(biddertype!=null && biddertype!="null" && biddertype!=""){
		if(biddertype==0){//个人
			$("input[name=biddertype][value=0]").attr("checked","checked");
			$("#grzjh").html("<span class='c6'>*</span>证件号码:");
			$("#mc").html("<span class='c6'>*</span>姓名:");
		}else{//企业
			$("input[name=biddertype][value=1]").attr("checked","checked");
			//$("#grzjh").html("组织机构代码:");
			$("#grzjh").html("<span class='c6'>*</span>组织机构代码:");
			$("#mc").html("<span class='c6'>*</span>名称:");
		}
		
		$("#btgr").attr("disabled","disabled");
		$("#btqy").attr("disabled","disabled");
		$("#name").attr("disabled","disabled");
		$("#no").attr("disabled","disabled");
		$("#address").attr("disabled","disabled");
		$("#tel").attr("disabled","disabled");
	}
	if(name!=null && name!="null" && name!=""){
		$("#name").val(name);
	}
	if(no!=null && no!="null" && no!=""){
		$("#no").val(no);
	}
	if(address!=null && address!="null" && address!=""){
		$("#address").val(address);
	}
	if(tel!=null && tel!="null" && tel!=""){
		$("#tel").val(tel);
	}
	pageUserObj.userId = getUserId(); 
	pageUserObj.id = Utils.getUrlParamValue(document.location.href, "id");
	$("#btnSave").click(pageUserObj.saveData);
	pageUserObj.initData();
	$("#biddertype").bind("change",pageUserObj.btypeChange);
	$("#btgr").bind("click",pageUserObj.btypeChange);
	$("#btqy").bind("click",pageUserObj.btypeChange);
	pageUserObj.goodsType= Utils.getUrlParamValue(document.location.href, "goodsType");
}

$(document).ready(pageUserObj.initHtml);


