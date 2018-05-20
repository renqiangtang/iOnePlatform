//---------------------------------
//页页对象
//---------------------------------
var pageAttachObj = {};
pageAttachObj.dialogManager = null;
pageAttachObj.parentPageObj = null;
pageAttachObj.checkDataModified = null;

//---------------------------------
//初始化数据
//---------------------------------
pageAttachObj.initData = function () {
	if (pageAttachObj.id == null || pageAttachObj.id == "" || pageAttachObj.id.toLowerCase() == "null") {
		pageAttachObj.id = "";
		$("#txtAttachName").val("");
		$("#chkPublish").attr("checked", true);
		$("#txtRemark").val("");
		return;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Attach";
	cmd.method = "getAttachData";
	cmd.id = pageAttachObj.id;
	cmd.success = function(data) {
		$("#txtAttachName").val(Utils.getRecordValue(data, 0, "name"));
		$("#chkPublish").attr("checked", Utils.getRecordValue(data, 0, 'is_publish') == "1");
		$("#txtRemark").val(Utils.getRecordValue(data, 0, 'remark'));
		pageAttachObj.refId = Utils.getRecordValue(data, 0, 'ref_id');
		pageAttachObj.refTableName = Utils.getRecordValue(data, 0, 'ref_table_name');
		pageAttachObj.checkDataModified = new FieldModifiedChecker(window.document);
		pageAttachObj.checkDataModified.initFileds();
	};
	cmd.execute();
}

pageAttachObj.checkParentObj = function () {
	if (pageAttachObj.dialogManager == null || pageAttachObj.parentPageObj == null) {
		var randomVarPrefix = Utils.getPageValue("randomVarPrefix");
		var manager = getGlobalAttribute(randomVarPrefix + "_attachEditDialog");
		if (manager) {
			pageAttachObj.dialogManager = manager.dialog;
			pageAttachObj.parentPageObj = manager.parentPageObj;
			removeGlobalAttribute(randomVarPrefix + "_attachEditDialog");
		}
	}
	return pageAttachObj.dialogManager != null && pageAttachObj.parentPageObj != null;
}

//---------------------------------
//保存按钮事件
//---------------------------------
pageAttachObj.saveData = function () {
	if ($("#txtAttachName").val() == ''){
		DialogAlert('附件名称不能为空');
		return false;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Attach";
	cmd.method = "updateAttachData";
	cmd.id = pageAttachObj.id;
	cmd.name = $("#txtAttachName").val();
	if ($("#chkPublish").attr("checked")) {
		cmd.is_publish = 1;
	} else {
		cmd.is_publish = 0;
	}
	cmd.remark = $("#txtRemark").val();
	cmd.ref_id = pageAttachObj.refId;
	cmd.ref_table_name = pageAttachObj.refTableName;
	cmd.category = pageAttachObj.category;
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			var blnNew = false;
			if (data.id) {
				pageAttachObj.id = data.id;
				blnNew = true;
			}
			DialogAlert('保存附件成功完成');
			if (pageAttachObj.checkParentObj()) {
				if (pageAttachObj.parentPageObj)
					if (blnNew) {
						if (pageAttachObj.parentPageObj.addTreeNode)
							pageAttachObj.parentPageObj.addTreeNode($("#txtAttachName").val(), pageAttachObj.id, pageAttachObj.refId,
								pageAttachObj.refTableName, pageAttachObj.category);
					} else if (pageAttachObj.parentPageObj.updateTreeNode)
						pageAttachObj.parentPageObj.updateTreeNode($("#txtAttachName").val());
				if (pageAttachObj.dialogManager)
					pageAttachObj.dialogManager.close();
			}
		} else {
			DialogError('保存附件失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

pageAttachObj.closeWindow = function() {
	if (pageAttachObj.checkParentObj()) {
		if (pageAttachObj.checkDataModified && pageAttachObj.checkDataModified.checkModified(true)) {
			if (!confirm("数据已修改未保存，确认关闭？\n点击确定按钮将放弃未保存数据"))
				return false;
		}
		if (pageAttachObj.dialogManager)
			pageAttachObj.dialogManager.close();
	}
}


//---------------------------------
//页面初始化
//---------------------------------
pageAttachObj.initHtml = function() {
	pageAttachObj.moduleId = Utils.getPageValue("moduleId");
	pageAttachObj.userId = getUserId();
	pageAttachObj.id = Utils.getPageValue("id");
	pageAttachObj.refId = Utils.getPageValue("refId");
	pageAttachObj.refTableName = Utils.getPageValue("refTableName");
	pageAttachObj.category = Utils.getPageValue("category");
	if (!pageAttachObj.category || pageAttachObj.category.toLowerCase() == "null" || pageAttachObj.category.toLowerCase() == "undefined")
		pageAttachObj.category = null;
	var nameReadOnly = Utils.getPageValue("nameReadOnly");
	if (nameReadOnly && (nameReadOnly == 1 || nameReadOnly.toLowerCase() == "true" || nameReadOnly.toLowerCase() == "yes")) {
		$("#txtAttachName").attr("readonly", true);
		$("#txtAttachName").attr("title", "来自附件模板，不允许修改名称");
	}
	$("#btnSave").click(pageAttachObj.saveData);
	$("#btnClose").click(pageAttachObj.closeWindow);
	pageAttachObj.initData();
}

$(document).ready(pageAttachObj.initHtml);