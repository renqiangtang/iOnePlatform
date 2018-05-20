//---------------------------------
//页页对象
//---------------------------------
var pageObj = {};
pageObj.targetId = null;

//---------------------------------
//初始化数据
//---------------------------------
pageObj.initData = function () {
	var cmd = new Command();
	cmd.module = "trademan";
	cmd.service = "LicenseManage";
	cmd.method = "getConfirmLicenseData";
	cmd.id = pageObj.id;
	cmd.success = function(data) {
		$("#txtBidderName").val(Utils.getRecordValue(data, 0, "bidder_name"));
		$("#txtTarget").val(Utils.getRecordValue(data, 0, "target_name"));
		$("#txtTransConditionOrgan").val(Utils.getRecordValue(data, 0, "condition_organ_name"));
		$("#txtTransCondition").val(Utils.getRecordValue(data, 0, "trans_condition"));
		$("input[name='confirmStatus'][value='" + Utils.getRecordValue(data, 0, "target_status") + "']").attr("checked", true);
		$("#txtConfirmOpinion").val(Utils.getRecordValue(data, 0, 'confirmed_opinion'));
		$("#txtConfirmDate").val(Utils.getRecordValue(data, 0, 'confirmed_date'));
		pageObj.targetId = Utils.getRecordValue(data, 0, "target_id");
	};
	cmd.execute();
}

//---------------------------------
//保存按钮事件
//---------------------------------
pageObj.saveData = function () {
	var cmd = new Command();
	cmd.module = "trademan";
	cmd.service = "LicenseManage";
	cmd.method = "confirmLicenseAfter";
	cmd.confirmStatus = $("input[name='confirmStatus']:checked").val();
	cmd.confirmOpinion = $("#txtConfirmOpinion").val();
	cmd.id = pageObj.id;
	cmd.targetId = pageObj.targetId;
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			DialogAlert('审批操作成功完成');
			var mainFrame = getIframeWin();
			if (mainFrame) {
				var licenseListObj = mainFrame.pageObj;
				if (licenseListObj)
					licenseListObj.queryData();
			}
		} else {
			DialogError('审批操作失败,错误原因：' + data.message);
			return false;
		}
		pageObj.dialogManager = getGlobalAttribute("licenseAfterDialog");
		removeGlobalAttribute("licenseAfterDialog");
		pageObj.dialogManager.dialog.close();
	};
	cmd.execute();
}

//---------------------------------
//页面初始化
//---------------------------------
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getUrlParamValue(document.location.href, "moduleId");
	pageObj.id = Utils.getUrlParamValue(document.location.href, "id");
	$("#btnSave").click(pageObj.saveData);
	pageObj.initData();
}

$(document).ready(pageObj.initHtml);


