//---------------------------------
//页页对象
//---------------------------------
var pageObj = {};
//---------------------------------
//行政区下拉框管理器引用
//---------------------------------
pageObj.cantonComboBoxManager = null;
pageObj.cantonTreeManager = null;

pageObj.onlyModifyCakey = false;

//---------------------------------
//初始化数据
//---------------------------------
pageObj.initData = function () {
	if (pageObj.id == null || pageObj.id == "" || pageObj.id.toLowerCase() == "null") {
		pageObj.id = "";
		pageObj.userId = "";
		pageObj.cakeyId = "";
		return;
	}
	var cmd = new Command();
	cmd.module = "trademan";
	cmd.service = "Bank";
	cmd.method = "getBankData";
	cmd.id = pageObj.id;
	cmd.success = function(data) {
		$("#txtName").val(Utils.getRecordValue(data, 0, 'name'));
		$("#txtNo").val(Utils.getRecordValue(data, 0, 'no'));
		$("#txtTel").val(Utils.getRecordValue(data, 0, 'tel'));
		$("#txtContact").val(Utils.getRecordValue(data, 0, 'contact'));
		$("#txtAddress").val(Utils.getRecordValue(data, 0, 'address'));
		$("#txtCakey").val(Utils.getRecordValue(data, 0, 'cakey'));
		$("#txtRemark").val(Utils.getRecordValue(data, 0, 'remark'));
		pageObj.userId = Utils.getRecordValue(data, 0, 'user_id');
		pageObj.cakeyId = Utils.getRecordValue(data, 0, 'cakey_id');
		var cantonId = Utils.getRecordValue(data, 0, 'canton_id');
		if (cantonId == null) {
			pageObj.cantonComboBoxManager.selectValueByTree("");
			$("#txtCanton").val("");
			$("#txtCantonId").val("");
		}
		else {
			pageObj.cantonComboBoxManager.selectValueByTree(cantonId);
			$("#txtCanton").val(Utils.getRecordValue(data, 0, 'canton_name'));
			$("#txtCantonId").val(cantonId);
		}
	};
	cmd.execute();
}

//---------------------------------
//行政区树展开节点前
//---------------------------------
pageObj.cantonNodeBeforeExpand = function (node)
{
	if (node.data.children && node.data.children.length == 0)
    {
        var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Canton";
		cmd.method = "getCantonTreeData";
		cmd.moduleNo = "";
		cmd.parentId = node.data.id;
		cmd.recurse = 2;
		cmd.async = false;
		cmd.success = function(data) {
			if (data.data.length > 0) {
				pageObj.cantonItemsConvert(data);
				var cantonId = $("#txtCantonId").val();
				var canton = $("#txtCanton").val();
				pageObj.cantonTreeManager.append(node.target, data.data);
				if (pageObj.cantonTreeManager.getSelected() == null) {
					$("#txtCantonId").val(cantonId);
					$("#txtCanton").val(canton);
				}
			}
		};
		cmd.execute();
    }
}

//---------------------------------
//行政区树节点转换
//---------------------------------
pageObj.cantonItemsConvert = function (items) {
	items.idFieldName = 'id';
	items.parentIDFieldName = 'parentId'; 
	items.nodeWidth = 200;
	items.checkbox = false;
	items.onBeforeExpand = pageObj.cantonNodeBeforeExpand;
}

//---------------------------------
//行政区树下拉框初始化
//---------------------------------
pageObj.initCantonComboBox = function () {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Canton";
	cmd.method = "getCantonTreeData";
	cmd.moduleNo = "";
	cmd.parentId = "";
	cmd.recurse = 2;
	cmd.isexpand = 'false';
	cmd.async = false;
	cmd.success = function(data) {
		pageObj.cantonItemsConvert(data);
		pageObj.cantonComboBoxManager.setTree(data);
		pageObj.cantonTreeManager = pageObj.cantonComboBoxManager.treeManager;
	};
	cmd.execute();
}

//---------------------------------
//保存按钮事件
//---------------------------------
pageObj.saveData = function () {
	if (!pageObj.onlyModifyCakey) {
		if ($("#txtName").val() == ''){
			DialogAlert('银行名称不能为空');
			return false;
		}
		if ($("#txtNo").val() == ''){
			DialogAlert('银行编号不能为空');
			return false;
		}
		if ($("#txtCakey").val() == ''){
			DialogAlert('银行CAKEY不能为空');
			return false;
		}
	}
	var cmd = new Command();
	cmd.module = "trademan";
	cmd.service = "Bank";
	cmd.method = "updateBankData";
	cmd.id = pageObj.id;
	if (pageObj.onlyModifyCakey) {
		cmd.cakey = $("#txtCakey").val();
	} else {
		cmd.name = $("#txtName").val();
		cmd.no = $("#txtNo").val();
		cmd.tel = $("#txtTel").val();
		cmd.contact = $("#txtContact").val();
		cmd.address = $("#txtAddress").val();
		cmd.canton_id = $("#txtCantonId").val();
		cmd.cakey = $("#txtCakey").val();
		cmd.remark = $("#txtRemark").val();
	}
	cmd.user_id = pageObj.userId;
	cmd.cakey_id = pageObj.cakeyId;
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			pageObj.id = data.id;
			pageObj.userId = data.userId;
			pageObj.cakeyId = data.cakeyId;
			DialogAlert('保存银行成功完成');
			var mainFrame = getIframeWin();
			if (mainFrame) {
				var bankListObj = mainFrame.pageObj;
				if (bankListObj)
					bankListObj.queryData();
			}
		} else {
			DialogError('保存银行失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

//---------------------------------
//设置Cakey按钮事件
//---------------------------------
pageObj.setCakey = function () {
	DialogConfirm('请插上新的CAKey后点击“是”按钮！', function (yes) {
		if (yes) {
			var caKeyId = ca.readId();
			$("#txtCakey").val(caKeyId);
		}
	}
	);
}

//---------------------------------
//页面初始化
//---------------------------------
pageObj.initHtml = function() {
	pageObj.id = Utils.getUrlParamValue(document.location.href, "id");
	pageObj.onlyModifyCakey = Utils.getUrlParamValue(document.location.href, "onlyModifyCakey") == "true";
	//pageObj.unallowDelete = Utils.getUrlParamValue(document.location.href, "unallowDelete") == "true";
	$("#btnSetCakey").click(pageObj.setCakey);
	$("#btnSave input").click(pageObj.saveData);
	pageObj.cantonComboBoxManager = $("#txtCanton").ligerComboBox({valueField: "id", valueFieldID: "txtCantonId", treeLeafOnly: false, isShowCheckBox: true,width:385 });
	pageObj.initCantonComboBox();
	pageObj.initData();
	if (pageObj.onlyModifyCakey) {
		$("#txtName").attr("readonly", true);
		$("#txtNo").attr("readonly", true);
		$("#txtTel").attr("readonly", true);
		$("#txtContact").attr("readonly", true);
		$("#txtCanton").attr("readonly", true);
		$("#txtAddress").attr("readonly", true);
		pageObj.cantonComboBoxManager.setDisabled();
		if (pageObj.id != "") {
			$("#unmodifyPrompt").show();
		}
	} else {
		$("#unmodifyPrompt").hide();
	}
	var userInfo = getUserInfoObj();
	if (userInfo && userInfo.userName.toLowerCase() == "Admin".toLowerCase()) {
		$('#chkAllowModifyCakey').show();
		$('#chkAllowModifyCakey').click(function(){
			if ($('#chkAllowModifyCakey').attr("checked")) {
	  　　		$('#txtCakey').removeAttr("readonly");
			} else {
				$('#txtCakey').attr("readonly", "readonly");
			}
		});
	}
}

$(document).ready(pageObj.initHtml);


