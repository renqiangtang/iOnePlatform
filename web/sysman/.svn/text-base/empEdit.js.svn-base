//---------------------------------
//页页对象
//---------------------------------
var pageObj = {};
//---------------------------------
//表格对象
//---------------------------------
pageObj.departmentGridManager = null;
pageObj.jobGridManager = null;
pageObj.roleGridManager = null;
//---------------------------------
//表格数据重新加载标识
//---------------------------------
pageObj.departmentGridDataLoaded = false;
pageObj.jobGridDataLoaded = false;
pageObj.roleGridDataLoaded = false;
//---------------------------------
//角色下属数据新增、删除数据ID缓存字符串
//---------------------------------
pageObj.addDepartmentIds = ";";
pageObj.deleteDepartmentIds = ";";
pageObj.addJobIds = ";";
pageObj.deleteJobIds = ";";
pageObj.addRoleIds = ";";
pageObj.deleteRoleIds = ";";
//---------------------------------
//单位ID
//---------------------------------
pageObj.initOrganId = '';

//---------------------------------
//初始化数据
//---------------------------------
pageObj.initData = function () {
	pageObj.departmentGridDataLoaded = false;
	pageObj.jobGridDataLoaded = false;
	pageObj.roleGridDataLoaded = false;
	pageObj.addDepartmentIds = ";";
	pageObj.deleteDepartmentIds = ";";
	pageObj.addJobIds = ";";
	pageObj.deleteJobIds = ";";
	pageObj.addRoleIds = ";";
	pageObj.deleteRoleIds = ";";
	pageObj.afterSelectTabItem(pageObj.activeTabId);	
	if (pageObj.id == null || pageObj.id == "" || pageObj.id.toLowerCase() == "null") {
		pageObj.id = "";
		pageObj.userId = "";
		pageObj.cakeyId = "";
		$("#cboGender").val("男");
		$("#chkIsValid").attr("checked", true);
		$("#txtValidity").val("100");
		$("#chkUserIsValid").attr("checked", true);
		$("#btnActive").hide();
		$("#btnInvalid").hide();
		return;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Emp";
	cmd.method = "getEmpData";
	cmd.id = pageObj.id;
	cmd.success = function(data) {
		$("#txtName").val(Utils.getRecordValue(data, 0, "name"));
		$("#txtNo").val(Utils.getRecordValue(data, 0, "no"));
		$("#cboGender").val(Utils.getRecordValue(data, 0, "gender"));
		$("#txtTel").val(Utils.getRecordValue(data, 0, "tel"));
		$("#txtMobile").val(Utils.getRecordValue(data, 0, "mobile"));
		$("#txtEmail").val(Utils.getRecordValue(data, 0, "email"));
		$("#chkIsValid").attr("checked", Utils.getRecordValue(data, 0, 'is_valid') == "1");
		
		$("#txtUserName").val(Utils.getRecordValue(data, 0, "user_name"));
		$("#txtPassword").val("");
		$("#txtConfirmPassword").val("");
		$("#txtActiveDate").val(Utils.getRecordValue(data, 0, 'active_date'));
		$("#txtValidity").val(Utils.getRecordValue(data, 0, 'validity'));
		$("#txtStyleName").val(Utils.getRecordValue(data, 0, 'style_name'));
		$("#txtCakey").val(Utils.getRecordValue(data, 0, 'cakey'));
		$("#chkUserIsValid").attr("checked", Utils.getRecordValue(data, 0, 'user_is_valid') == "1");
		$("#txtRemark").val(Utils.getRecordValue(data, 0, 'remark'));
		pageObj.userId = Utils.getRecordValue(data, 0, 'user_id');
		pageObj.cakeyId = Utils.getRecordValue(data, 0, 'cakey_id');
		pageObj.initOrganId = Utils.getRecordValue(data, 0, 'organ_id');
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
	};
	cmd.execute();
}

pageObj.checkValidityOnlyNumber = function () 
{ 
	if (!(event.keyCode == 46) && !(event.keyCode == 8) && !(event.keyCode == 37) && !(event.keyCode == 39)) 
		if (!((event.keyCode >= 48 && event.keyCode <= 57) || (event.keyCode >= 96 && event.keyCode <= 105))) 
			event.returnValue = false; 
} 

//---------------------------------
//保存按钮事件
//---------------------------------
pageObj.saveData = function () {
	if ($("#txtName").val() == ''){
		DialogAlert('姓名不能为空', window);
		return false;
	}
	if ($("#txtUserName").val() == '' && ($("#txtPassword").val() != '' || $("#txtValidity").val() != '' || $("#txtStyleName").val() != ''
		|| $("#txtCakey").val() != '')) {
		DialogAlert('输入了用户相关信息，用户名不能为空。否则不能添加登录用户', window);
		return false;
	}
//	if ((pageObj.userId == '' || pageObj.userId == null || pageObj.userId.toLowerCase() == 'null' || pageObj.userId.toLowerCase() == 'undefined')
//		&& $("#txtUserName").val() != '') {//新增登录用户
//		if ($("#txtPassword").val() == '') {
//			DialogAlert('第一次新增登录用户密码不能为空，也可以清空用户名以暂时不新增登录用户信息', window);
//			return false;
//		}
//	}
	if ($("#txtUserName").val() != '') {
		if (($("#txtPassword").val() != '' || $("#txtConfirmPassword").val() != '')
			&& $("#txtPassword").val() != $("#txtConfirmPassword").val()){
			DialogAlert('确认密码不一致', window);
			return false;
		}
	}
//	if (pageObj.roleGridManager != null && (pageObj.roleGridManager.data.Rows.length > 0 && (pageObj.userId == '' || $("#txtUserName").val() == ''))) {
//		DialogAlert('必须输入登录用户信息才能保存所属角色数据', window);
//		return false;
//	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Emp";
	cmd.method = "updateEmpData";
	cmd.id = pageObj.id;
	cmd.name = $("#txtName").val();
	cmd.no = $("#txtNo").val();
	cmd.gender = $("#cboGender").val();
	cmd.tel = $("#txtTel").val();
	cmd.mobile = $("#txtMobile").val();
	cmd.email = $("#txtEmail").val();
	if ($("#chkIsValid").attr("checked")) {
		cmd.is_valid = 1;
	} else {
		cmd.is_valid = 0;
	}
	if (pageObj.initOrganId)
		cmd.organ_id = pageObj.initOrganId;
	cmd.user_name = $("#txtUserName").val();
	if (cmd.user_name) {
		if ($("#txtPassword").val() != "")
			if ($("#txtPassword").val()) {
				//cmd.password = $.md5($.md5($("#txtUserName").val()) + $("#txtPassword").val());
				cmd.password = $.md5($("#txtPassword").val());
			} else {
				cmd.password = $.md5("123456");
			}
		cmd.validity = $("#txtValidity").val();
		cmd.style_name = $("#txtStyleName").val();
		cmd.cakey = $("#txtCakey").val();
		if ($("#chkUserIsValid").attr("checked")) {
			cmd.user_is_valid = 1;
		} else {
			cmd.user_is_valid = 0;
		}
	}
	cmd.remark = $("#txtRemark").val();
	cmd.user_id = pageObj.userId;
	cmd.cakey_id = pageObj.cakeyId;
	//所属部门数据
	if (pageObj.departmentGridManager != null) {
		if (pageObj.addDepartmentIds.length > 1) {
			cmd.addDepartmentIds = pageObj.addDepartmentIds;
		}
		if (pageObj.deleteDepartmentIds.length > 1) {
			cmd.deleteDepartmentIds = pageObj.deleteDepartmentIds;
		}
	}
	//所属岗位数据
	if (pageObj.jobGridManager != null) {
		if (pageObj.addJobIds.length > 1) {
			cmd.addJobIds = pageObj.addJobIds;
		}
		if (pageObj.deleteJobIds.length > 1) {
			cmd.deleteJobIds = pageObj.deleteJobIds;
		}
	}
	//所属角色数据
	if (pageObj.roleGridManager != null) {
		if (pageObj.addRoleIds.length > 1) {
			cmd.addRoleIds = pageObj.addRoleIds;
		}
		if (pageObj.deleteRoleIds.length > 1) {
			cmd.deleteRoleIds = pageObj.deleteRoleIds;
		}
	}
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			pageObj.id = data.id;
			pageObj.userId = data.userId;
			pageObj.cakeyId = data.cakeyId;
			pageObj.addDepartmentIds = ";";
			pageObj.deleteDepartmentIds = ";";
			pageObj.addJobIds = ";";
			pageObj.deleteJobIds = ";";
			pageObj.addRoleIds = ";";
			pageObj.deleteRoleIds = ";";
			DialogAlert('保存人员成功完成', window);
			var mainFrame = getIframeWin();
			if (mainFrame) {
				var empListObj = mainFrame.pageObj;
				if (empListObj)
					empListObj.queryData();
			}
		} else {
			DialogError('保存人员失败,错误原因：' + data.message, window);
			return false;
		}
	};
	cmd.execute();
}

//---------------------------------
//过期按钮事件
//---------------------------------
pageObj.invalidUser = function () {
	if (pageObj.userId == undefined || pageObj.userId == null
		|| pageObj.userId == '' || pageObj.userId.toLowerCase() == "undefined"
		|| pageObj.userId.toLowerCase() == "null") {
		DialogAlert('当前人员暂无登录用户信息，无需进行状态设置为已过期操作', window);
		return;
	}
	DialogConfirm('设置当前人员的登录用户 ' + $("#txtUserName").val() + '状态为已过期？过期的用户不能正常登录使用。', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "User";
			cmd.method = "updateUserData";
			cmd.id = pageObj.userId;
			cmd.status = 2;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					$("#txtStatus").val("过期");
					$("#btnActive").show();
					$("#btnInvalid").hide();
					DialogAlert('当前人员的登录用户状态设置为已过期操作完成');
				} else {
					DialogError('当前人员的登录用户状态设置为已过期操作失败,错误原因：' + data.message);
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
pageObj.activeUser = function () {
	if (pageObj.userId == undefined || pageObj.userId == null
		|| pageObj.userId == '' || pageObj.userId.toLowerCase() == "undefined"
		|| pageObj.userId.toLowerCase() == "null") {
		DialogAlert('当前人员暂无登录用户信息，无需进行激活操作', window);
		return;
	}
	DialogConfirm('激活当前竞买人 ' + $("#txtUserName").val() + '？激活后该用户可以正常使用。', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "User";
			cmd.method = "updateUserData";
			cmd.id = pageObj.userId;
			cmd.status = 1;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					$("#txtStatus").val("正常");
					$("#btnActive").hide();
					$("#btnInvalid").show();
					DialogAlert('当前人员的登录用户激活完成，可以正常登录使用');
				} else {
					DialogError('当前人员的登录用户激活失败,错误原因：' + data.message);
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
//添加部门事件
//---------------------------------
pageObj.addDepartment = function() {
	DialogOpen({height: 400, width: 300, title: '选择新增部门', url: approot + '/sysman/department.html?selectData=2&organId=' + pageObj.initOrganId
		+ "&height=350&width=300",
		buttons: [{ text: '确定', onclick: function (item, dialog) {
					var selectedDataFn = dialog.frame.pageObj.getCheckedDepartmentData || dialog.frame.window.pageObj.getCheckedDepartmentData;
					var selectedData = selectedDataFn();
					if (selectedData.length == 0) {
						DialogWarn('请选择要添加的部门。');
						return false;
					}
					for (var i = 0; i < selectedData.length; i++) {
						var blnExists = false;
						var id = selectedData[i].data.id;
						for (var row in pageObj.departmentGridManager.records) {
							if (pageObj.departmentGridManager.records[row].id == id) {
								blnExists = true;
								break;
							}
						}
						if (!blnExists) {
							pageObj.departmentGridManager.addRow({ 
								id: id,
								name: selectedData[i].data.text,
								no: selectedData[i].data.no
							});
							if (pageObj.deleteDepartmentIds.indexOf(';' + id + ';') != -1) {
								pageObj.deleteDepartmentIds = pageObj.deleteDepartmentIds.replace(';' + id + ';', ';');
							} else if (pageObj.addDepartmentIds.indexOf(';' + id + ';') == -1) {
								pageObj.addDepartmentIds += id + ';';
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
//删除部门事件
//---------------------------------
pageObj.deleteDepartment = function() {
	var selectedRow = pageObj.departmentGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要删除的部门。', window);
		return false;
	}
	DialogConfirm('将当前人员 ' + $("#txtName").val() + ' 从部门 ' + selectedRow.name + '中移除？移除后不再属于该部门人员', function (yes) {
		if (yes) {
			var id = selectedRow.id;
			if (pageObj.addDepartmentIds.indexOf(';' + id + ';') != -1) {
				pageObj.addDepartmentIds = pageObj.addDepartmentIds.replace(';' + id + ';', ';');
			} else if (pageObj.deleteDepartmentIds.indexOf(';' + id + ';') == -1) {
				pageObj.deleteDepartmentIds += id + ';';
			}
			pageObj.departmentGridManager.deleteSelectedRow();
		}
	}, window);
}

//---------------------------------
//添加岗位事件
//---------------------------------
pageObj.addJob = function() {
	DialogOpen({width: 650, height: 420, title: '选择新增岗位', url: approot + '/sysman/department.html?selectData=4&organId=' + pageObj.initOrganId
		+ "&height=332&width=650",
		buttons: [{ text: '确定', onclick: function (item, dialog) {
					var selectedDataFn = dialog.frame.pageObj.getCheckedJobData || dialog.frame.window.pageObj.getCheckedJobData;
					var selectedData = selectedDataFn();
					if (selectedData.length == 0) {
						DialogWarn('请选择要添加的岗位。');
						return false;
					}
					for (var i = 0; i < selectedData.length; i++) {
						var blnExists = false;
						var id = selectedData[i].id;
						for (var row in pageObj.jobGridManager.records) {
							if (pageObj.jobGridManager.records[row].id == id) {
								blnExists = true;
								break;
							}
						}
						if (!blnExists) {
							pageObj.jobGridManager.addRow({ 
								id: id,
								name: selectedData[i].name,
								no: selectedData[i].no,
								organName: selectedData[i].organName
							});
							if (pageObj.deleteJobIds.indexOf(';' + id + ';') != -1) {
								pageObj.deleteJobIds = pageObj.deleteJobIds.replace(';' + id + ';', ';');
							} else if (pageObj.addJobIds.indexOf(';' + id + ';') == -1) {
								pageObj.addJobIds += id + ';';
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
//删除岗位事件
//---------------------------------
pageObj.deleteJob = function() {
	var selectedRow = pageObj.jobGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要删除的岗位。', window);
		return false;
	}
	DialogConfirm('将当前人员 ' + $("#txtName").val() + ' 的岗位 ' + selectedRow.name + '移除？移除后该人员将不再担任该岗位', function (yes) {
		if (yes) {
			var id = selectedRow.id;
			if (pageObj.addJobIds.indexOf(';' + id + ';') != -1) {
				pageObj.addJobIds = pageObj.addJobIds.replace(';' + id + ';', ';');
			} else if (pageObj.deleteJobIds.indexOf(';' + id + ';') == -1) {
				pageObj.deleteJobIds += id + ';';
			}
			pageObj.jobGridManager.deleteSelectedRow();
		}
	}, window);
}

//---------------------------------
//添加角色事件
//---------------------------------
pageObj.addRole = function() {
	DialogOpen({height: 400, width: 300, title: '选择新增角色', url: approot + '/sysman/role.html?selectData=2'
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
						for (var row in pageObj.roleGridManager.records) {
							if (pageObj.roleGridManager.records[row].id == id) {
								blnExists = true;
								break;
							}
						}
						if (!blnExists) {
							pageObj.roleGridManager.addRow({ 
								id: id,
								name: selectedData[i].data.text,
								no: selectedData[i].data.no
							});
							if (pageObj.deleteRoleIds.indexOf(';' + id + ';') != -1) {
								pageObj.deleteRoleIds = pageObj.deleteRoleIds.replace(';' + id + ';', ';');
							} else if (pageObj.addRoleIds.indexOf(';' + id + ';') == -1) {
								pageObj.addRoleIds += id + ';';
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
pageObj.deleteRole = function() {
	var selectedRow = pageObj.roleGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要删除的角色。', window);
		return false;
	}
	DialogConfirm('将当前人员 ' + $("#txtName").val() + ' 从角色 ' + selectedRow.name + '中移除？移除后不再属于该角色人员', function (yes) {
		if (yes) {
			var id = selectedRow.id;
			if (pageObj.addRoleIds.indexOf(';' + id + ';') != -1) {
				pageObj.addRoleIds = pageObj.addRoleIds.replace(';' + id + ';', ';');
			} else if (pageObj.deleteRoleIds.indexOf(';' + id + ';') == -1) {
				pageObj.deleteRoleIds += id + ';';
			}
			pageObj.roleGridManager.deleteSelectedRow();
		}
	}, window);
}

//---------------------------------
//获取表格查询参数
//---------------------------------
pageObj.getGridQueryParams = function(gridManager, gridBaseUrl) {
	var pageRowCount = gridManager.options.pageSize;
	if (pageRowCount == undefined || pageRowCount == null || pageRowCount <= 0) {
		pageRowCount = 10;
	}
	var pageIndex = gridManager.options.page;
	if (pageIndex == undefined || pageIndex == null || pageIndex <= 0) {
	  pageIndex = 1;
	}
	var sortField = gridManager.options.sortname;
	if (sortField == undefined || sortField == null) {
		sortField = "";
	}
	var sortDir = gridManager.options.sortorder;
	if (sortDir == undefined || sortDir == null) {
		sortDir = "";
	}
	var id = pageObj.id;
	var queryParams = {};
	if (gridManager.options.usePager) {
		queryParams.pageRowCount = pageRowCount;
		queryParams.pageIndex = pageIndex;
		queryParams.sortField = sortField;
		queryParams.sortDir = sortDir;
	}
	queryParams.id = id;
	queryParams.u = pageObj.loginUserId;
	var url = gridBaseUrl;
	//var gridOptions = {url: url}
	//gridManager.setOptions(gridOptions);
	gridManager.options.url = url;
	gridManager.loadServerData(queryParams);
	queryParams = {id: id, mm:'get'};
	for(var key in queryParams){
		url = Utils.setParamValue(url, key, queryParams[key], false, '=', '&');
	}
	gridManager.options.url = url;
	return queryParams;
}

//---------------------------------
//人员所属信息标签TAB切换前事件
//---------------------------------
pageObj.beforeSelectTabItem = function(tabid) {
}

//---------------------------------
//人员所属信息标签TAB切换后事件
//---------------------------------
pageObj.afterSelectTabItem = function(tabid) {
	if (tabid == 'tabitem1'){//部门
 		if (pageObj.departmentGridManager == null) {
	 		pageObj.departmentGridManager = $("#departmentGrid").ligerGrid({
				url: approot + '/data?module=sysman&service=Emp&method=getEmpDepartmentData&moduleId=' + pageObj.moduleId
					+ '&id=' + pageObj.id + '&u=' + pageObj.loginUserId,
				columns: [
					{ display: '名称', name: 'name', width: 200},
					{ display: '编号', name: 'no', width: 150}
				],
				usePager: false,
				pagesizeParmName: 'pageRowCount',
				pageParmName: 'pageIndex',
				sortnameParmName:'sortField',
				sortorderParmName:'sortDir',
				isScroll: true,
				frozen: false,
				pageSizeOptions: [10, 20, 30], 
				showTitle: false,
				fixedCellHeight :false,
				width: '99.8%',
				height: 170,
				toolbar: { items: [
	                { text: '新增', id: 'btnAddDepartment', click: pageObj.addDepartment, icon: 'add' },
	                { line: true },
	                { text: '删除', id: 'btnDeleteDepartment', click: pageObj.deleteDepartment, icon: 'delete' }
                ]}
			});
		} else if (!pageObj.departmentGridDataLoaded) {
			pageObj.getGridQueryParams(pageObj.departmentGridManager, approot + '/data?module=sysman&service=Emp&method=getEmpDepartmentData&moduleId=' + pageObj.moduleId);
		}
		pageObj.departmentGridDataLoaded = true;
 	} else if (tabid == 'tabitem2') {//岗位
 		if (pageObj.jobGridManager == null) {
	 		pageObj.jobGridManager = $("#jobGrid").ligerGrid({
				url: approot + '/data?module=sysman&service=Emp&method=getEmpJobData&moduleId=' + pageObj.moduleId
					+ '&id=' + pageObj.id + '&u=' + pageObj.loginUserId,
				columns: [
					{ display: '名称', name: 'name', width: 200},
					{ display: '编号', name: 'no', width: 150}
				],
				usePager: false,
				pagesizeParmName: 'pageRowCount',
				pageParmName: 'pageIndex',
				sortnameParmName:'sortField',
				sortorderParmName:'sortDir',
				isScroll: true,
				frozen: false,
				pageSizeOptions: [10, 20, 30], 
				showTitle: false,
				fixedCellHeight :false,
				width: '99.8%',
				height: 170,
				toolbar: { items: [
	                { text: '新增', id: 'btnAddJob', click: pageObj.addJob, icon: 'add' },
	                { line: true },
	                { text: '删除', id: 'btnDeleteJob', click: pageObj.deleteJob, icon: 'delete' }
                ]}
			});
		} else if (!pageObj.jobGridDataLoaded) {
			pageObj.getGridQueryParams(pageObj.jobGridManager, approot + '/data?module=sysman&service=Emp&method=getEmpJobData&moduleId=' + pageObj.moduleId);
		}
		pageObj.jobGridDataLoaded = true;
 	} else if (tabid == 'tabitem3') {//角色
		if (pageObj.roleGridManager == null) {
	 		pageObj.roleGridManager = $("#roleGrid").ligerGrid({
				url: approot + '/data?module=sysman&service=Emp&method=getEmpRoleData&moduleId=' + pageObj.moduleId
					+ '&id=' + pageObj.id + '&u=' + pageObj.loginUserId,
				columns: [
					{ display: '名称', name: 'name', width: 200},
					{ display: '编号', name: 'no', width: 150}
				],
				usePager: false,
				pagesizeParmName: 'pageRowCount',
				pageParmName: 'pageIndex',
				sortnameParmName:'sortField',
				sortorderParmName:'sortDir',
				isScroll: true,
				frozen: false,
				pageSizeOptions: [10, 20, 30], 
				showTitle: false,
				fixedCellHeight :false,
				width: '99.8%',
				height: 170,
				toolbar: { items: [
	                { text: '新增', id: 'btnAddRole', click: pageObj.addRole, icon: 'add' },
	                { line: true },
	                { text: '删除', id: 'btnDeleteRole', click: pageObj.deleteRole, icon: 'delete' }
                ]}
			});
		} else if (!pageObj.userGridDataLoaded) {
			pageObj.getGridQueryParams(pageObj.roleGridManager, approot + '/data?module=sysman&service=Emp&method=getEmpRoleData&moduleId=' + pageObj.moduleId);
		}
		pageObj.userGridDataLoaded = true;
 	}
 	pageObj.activeTabId = tabid;
}

//---------------------------------
//人员属信息标签TAB初始化
//---------------------------------
pageObj.initEmpRelTab = function() {
	$("#tabEmpRel").ligerTab({
		contextmenu: false,
		onBeforeSelectTabItem: pageObj.beforeSelectTabItem,
	    onAfterSelectTabItem: pageObj.afterSelectTabItem
     });
}

//---------------------------------
//页面初始化
//---------------------------------
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getPageValue("moduleId");
	pageObj.initOrganId = Utils.getPageValue("organId");
	pageObj.id = Utils.getUrlParamValue(document.location.href, "id");
	pageObj.loginUserId = getUserId();
	$("#txtValidity").keydown(pageObj.checkValidityOnlyNumber);
	$("#btnSetCakey").click(pageObj.setCakey);
	$("#btnSave").click(pageObj.saveData);
	$("#btnActive").click(pageObj.activeUser);
	$("#btnInvalid").click(pageObj.invalidUser);
	pageObj.initEmpRelTab();
	pageObj.initData();
	pageObj.afterSelectTabItem('tabitem1');
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


