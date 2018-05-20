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
		$("#txtUserName").val(Utils.getRecordValue(data, 0, "user_name"));
		$("#txtPassword").val("");
		$("#txtConfirmPassword").val("");
		$("#txtRefName").val(Utils.getRecordValue(data, 0, 'ref_name'));
		$("#txtActiveDate").val(Utils.getRecordValue(data, 0, 'active_date'));
		$("#txtValidity").val(Utils.getRecordValue(data, 0, 'validity'));
		$("#txtStyleName").val(Utils.getRecordValue(data, 0, 'style_name'));
		$("#txtCakey").val(Utils.getRecordValue(data, 0, 'cakey'));
		//$("#chkIsValid").val(Utils.getRecordValue(data, 0, 'is_valid'));
		$("#chkIsValid").attr("checked", Utils.getRecordValue(data, 0, 'is_valid') == "1");
		$("#txtRemark").val(Utils.getRecordValue(data, 0, 'remark'));
		pageUserObj.cakeyId = Utils.getRecordValue(data, 0, 'cakey_id');
		var userType = Utils.getRecordValue(data, 0, 'user_type');
		if (userType == "1")
			$("#txtUserType").val("竞买人");
		else if (userType == "2")
			$("#txtUserType").val("银行工作人员");
		else
			$("#txtUserType").val("内部人员");
		var status = Utils.getRecordValue(data, 0, 'status');
		if (status == "1") {//激活
			$("#txtStatus").val("正常");
			$("#btnActive").hide();
			$("#btnInvalid").show();
		} else if (status == "2") {//过期
			$("#txtStatus").val("过期");
			$("#btnActive").show();
			$("#btnInvalid").hide();
		} else {//申请中、其它
			$("#txtStatus").val("申请中");
			$("#btnActive").show();
			$("#btnInvalid").hide();
		}
		//var isValid = Utils.getRecordValue(data, 0, 'is_valid');
		//if (isValid == "1") {
			
		//} else {
		//}
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
	if ($("#txtUserName").val() == ''){
		DialogAlert('用户名称不能为空', window);
		return false;
	}
	if (isNaN($("#txtValidity").val())){
		DialogAlert('用户有效期只能为数字', window);
		return false;
	}
	if (pageUserObj.id == '') {//新增用户
		if ($("#txtPassword").val() == '') {
			DialogAlert('新增用户密码不能为空', window);
			return false;
		}
	}
	if (($("#txtPassword").val() != '' || $("#txtConfirmPassword").val() != '')
		&& $("#txtPassword").val() != $("#txtConfirmPassword").val()){
		DialogAlert('确认密码不一致', window);
		return false;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.method = "updateUserData";
	cmd.id = pageUserObj.id;
	cmd.user_name = $("#txtUserName").val();
	if ($("#txtPassword").val() != "")
		//cmd.password = $.md5($.md5($("#txtUserName").val()) + $("#txtPassword").val());
		cmd.password = $.md5($("#txtPassword").val());
	cmd.validity = $("#txtValidity").val();
	cmd.style_name = $("#txtStyleName").val();
	cmd.cakey = $("#txtCakey").val();
	if ($("#chkIsValid").attr("checked")) {
		cmd.is_valid = 1;
	} else {
		cmd.is_valid = 0;
	}
	cmd.remark = $("#txtRemark").val();
	cmd.cakey_id = pageUserObj.cakeyId;
	//所属角色数据
	if (pageUserObj.roleGridManager != null) {
		if (pageUserObj.addRoleIds.length > 1) {
			cmd.addRoleIds = pageUserObj.addRoleIds;
		}
		if (pageUserObj.deleteRoleIds.length > 1) {
			cmd.deleteRoleIds = pageUserObj.deleteRoleIds;
		}
	}
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			pageUserObj.id = data.id;
			pageUserObj.cakeyId = data.cakeyId;
			DialogAlert('保存用户成功完成', window);
			var mainFrame = getIframeWin();
			if (mainFrame) {
				var userListObj = mainFrame.pageUserObj;
				if (userListObj)
					userListObj.queryData();
			}
		} else {
			DialogError('保存用户失败,错误原因：' + data.message, window);
			return false;
		}
	};
	cmd.execute();
}

//---------------------------------
//过期按钮事件
//---------------------------------
pageUserObj.invalidUser = function () {
	DialogConfirm('设置当前用户 ' + $("#txtUserName").val() + '状态为已过期？过期的用户不能正常使用。', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "User";
			cmd.method = "updateUserData";
			cmd.id = pageUserObj.id;
			cmd.status = 2;
			cmd.cakey_id = pageUserObj.cakeyId;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					$("#txtStatus").val("过期");
					$("#btnActive").show();
					$("#btnInvalid").hide();
					pageUserObj.id = data.id;
					pageUserObj.cakeyId = data.cakeyId;
					DialogAlert('用户状态设置为已过期操作完成');
				} else {
					DialogError('用户状态设置为已过期操作失败,错误原因：' + data.message);
					return false;
				}
			};
			cmd.execute();
		}
	}, window);
}

//---------------------------------
//激活按钮事件
//---------------------------------
pageUserObj.activeUser = function () {
	DialogConfirm('激活当前竞买人 ' + $("#txtUserName").val() + '？激活后该用户可以正常使用。', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "User";
			cmd.method = "updateUserData";
			cmd.id = pageUserObj.id;
			cmd.status = 1;
			cmd.cakey_id = pageUserObj.cakeyId;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					$("#txtStatus").val("正常");
					$("#btnActive").hide();
					$("#btnInvalid").show();
					pageUserObj.id = data.id;
					pageUserObj.cakeyId = data.cakeyId;
					DialogAlert('用户激活完成，可以正常登录使用');
				} else {
					DialogError('用户激活失败,错误原因：' + data.message);
					return false;
				}
			};
			cmd.execute();
		}
	}, window);
}

//---------------------------------
//设置Cakey按钮事件
//---------------------------------
pageUserObj.setCakey = function () {
	DialogConfirm('请插上新的CAKey后点击“是”按钮！', function (yes) {
		if (yes) {
			var caKeyId = ca.readId('getOtherKey');
			$("#txtCakey").val(caKeyId);
		}
	}, window);
}

//---------------------------------
//添加角色事件
//---------------------------------
pageUserObj.addRole = function() {
	DialogOpen({height: 400, width: 300, title: '选择角色', url: approot + '/sysman/role.html?selectData=2'
		+ "&height=350&width=300",
		buttons: [{ text: '确定', onclick: function (item, dialog) {
					var selectedDataFn = dialog.frame.pageRoleObj.getCheckedRoleData || dialog.frame.window.pageRoleObj.getCheckedRoleData;
					var selectedData = selectedDataFn();
					if (selectedData.length == 0) {
						DialogWarn('请选择要添加的角色。');
						return false;
					}
					for (var i = 0; i < selectedData.length; i++) {
						var blnExists = false;
						var id = selectedData[i].data.id;
						for (var row in pageUserObj.roleGridManager.records) {
							if (pageUserObj.roleGridManager.records[row].id == id) {
								blnExists = true;
								break;
							}
						}
						if (!blnExists) {
							pageUserObj.roleGridManager.addRow({ 
								id: id,
								name: selectedData[i].data.text,
								no: selectedData[i].data.no
							});
							if (pageUserObj.deleteRoleIds.indexOf(';' + id + ';') != -1) {
								pageUserObj.deleteRoleIds = pageUserObj.deleteRoleIds.replace(';' + id + ';', ';');
							} else if (pageUserObj.addRoleIds.indexOf(';' + id + ';') == -1) {
								pageUserObj.addRoleIds += id + ';';
							}
						}
					}
					dialog.close();
				}
			},
			{ text: '取消', onclick: function (item, dialog) {
					dialog.close();
				}
			}]
		}
	);
}

//---------------------------------
//删除角色事件
//---------------------------------
pageUserObj.deleteRole = function() {
	var selectedRow = pageUserObj.roleGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要删除的角色。', window);
		return false;
	}
	DialogConfirm('将当前人员 ' + $("#txtUserName").val() + ' 从角色 ' + selectedRow.name + '中移除？移除后不再属于该角色人员', function (yes) {
		if (yes) {
			var id = selectedRow.id;
			if (pageUserObj.addRoleIds.indexOf(';' + id + ';') != -1) {
				pageUserObj.addRoleIds = pageUserObj.addRoleIds.replace(';' + id + ';', ';');
			} else if (pageUserObj.deleteRoleIds.indexOf(';' + id + ';') == -1) {
				pageUserObj.deleteRoleIds += id + ';';
			}
			pageUserObj.roleGridManager.deleteSelectedRow();
		}
	}, window);
}

//---------------------------------
//初始化表格
//---------------------------------
pageUserObj.initRoleGrid = function () {
	pageUserObj.roleGridManager = $("#roleGrid").ligerGrid({
		url: approot + '/data?module=sysman&service=User&method=getUserRoleData&moduleId=' + pageUserObj.moduleId
			+ '&u=' + pageUserObj.userId
			+ '&id=' + pageUserObj.id,
		columns: [
			{ display: '名称', name: 'name', width: 200},
			{ display: '编号', name: 'no', width: 120}
		],
		usePager: false,
		pagesizeParmName: 'pageRowCount',
		pageParmName: 'pageIndex',
		sortnameParmName: 'sortField',
		sortorderParmName: 'sortDir',
		isScroll: true,
		frozen: false,
		pageSizeOptions: [10, 20, 30], 
		showTitle: false,
		fixedCellHeight: false,
		height: 150,
		width: 365,
		toolbar: { items: [
            { text: '新增', id: 'btnAddRole', click: pageUserObj.addRole, icon: 'add' },
            { line: true },
            { text: '删除', id: 'btnDeleteRole', click: pageUserObj.deleteRole, icon: 'delete' }
        ]}
	});
}

//---------------------------------
//页面初始化
//---------------------------------
pageUserObj.initHtml = function() {
	pageUserObj.moduleId = Utils.getUrlParamValue(document.location.href, "moduleId");
	pageUserObj.userId = getUserId(); 
	$("#txtValidity").keydown(pageUserObj.checkValidityOnlyNumber);
	$("#btnSetCakey").click(pageUserObj.setCakey);
	$("#btnSave").click(pageUserObj.saveData);
	$("#btnActive").click(pageUserObj.activeUser);
	$("#btnInvalid").click(pageUserObj.invalidUser);
	pageUserObj.initData();
	pageUserObj.initRoleGrid();
}

$(document).ready(pageUserObj.initHtml);


