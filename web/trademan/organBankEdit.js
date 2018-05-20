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
pageObj.organId = null;
pageObj.id = null;


//---------------------------------
//银行简称下拉框初始化
//---------------------------------
pageObj.initBankAlias = function () {
	var cmd = new Command();
	cmd.module = "trademan";
	cmd.service = "Bank";
	cmd.method = "getAliasList";
	cmd.success = function(data) {
		var html = '<option value="">--请选择--</option>';
		for (var i = 0; i < data.alias.length; i++) {
			html += '<option value="' + data.alias[i].id + '" >' + data.alias[i].name+'&nbsp;&nbsp;('+data.alias[i].id + ')</option>';
		}
		$('#bank_alias').html(html);
	};
	cmd.execute();
}

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
	cmd.dataSetNo = '';
	cmd.id = pageObj.id;
	cmd.success = function(data) {
		$("#txtName").val(Utils.getRecordValue(data, 0, 'name'));
		$("#txtNo").val(Utils.getRecordValue(data, 0, 'no'));
		$("#txtTel").val(Utils.getRecordValue(data, 0, 'tel'));
		$("#txtContact").val(Utils.getRecordValue(data, 0, 'contact'));
		$("#txtAddress").val(Utils.getRecordValue(data, 0, 'address'));
		$("#txtCakey").val(Utils.getRecordValue(data, 0, 'cakey'));
		$("#txtRemark").val(Utils.getRecordValue(data, 0, 'remark'));
		$("#bank_alias").val(Utils.getRecordValue(data, 0, 'bank_alias'));
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
	}
	var cmd = new Command();
	cmd.module = "trademan";
	cmd.service = "Bank";
	cmd.method = "updateBankData";
	cmd.id = pageObj.id;
	cmd.organId = pageObj.organId;
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
		cmd.bank_alias = $("#bank_alias").val();
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
		window.dialogArguments.win.pageObj.queryData();
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
	pageObj.id = (window.dialogArguments && window.dialogArguments.id)||Utils.getUrlParamValue(document.location.href, "id");
	pageObj.onlyModifyCakey = Utils.getUrlParamValue(document.location.href, "onlyModifyCakey") == "true";
	pageObj.organId= (window.dialogArguments && window.dialogArguments.organId)||Utils.getUrlParamValue(document.location.href, "organId");
	//pageObj.unallowDelete = Utils.getUrlParamValue(document.location.href, "unallowDelete") == "true";
	$("#btnSetCakey").click(pageObj.setCakey);
	$("#btnSave input").click(pageObj.saveData);
	pageObj.cantonComboBoxManager = $("#txtCanton").ligerComboBox({valueField: "id", valueFieldID: "txtCantonId", treeLeafOnly: false, isShowCheckBox: true,width:385 });
	pageObj.initCantonComboBox();
	pageObj.initBankAlias();
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
}

$(document).ready(pageObj.initHtml);


