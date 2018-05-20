// ---------------------------------
// 页页对象
// ---------------------------------
var pageRoleObj = {}
// ---------------------------------
// 树管理器引用
// ---------------------------------
pageRoleObj.treeManager = null;
// ---------------------------------
// 树当前操作节点引用
// ---------------------------------
pageRoleObj.operNode = null;
// ---------------------------------
// 菜单、模块下拉框管理器引用
// ---------------------------------
pageRoleObj.rootMenuComboBoxManager = null;
pageRoleObj.rootMenuTreeManager = null;
pageRoleObj.menuComboBoxManager = null;
pageRoleObj.menuTreeManager = null;
pageRoleObj.moduleComboBoxManager = null;
pageRoleObj.moduleTreeManager = null;
// ---------------------------------
// 表格对象
// ---------------------------------
pageRoleObj.userGridManager = null;
pageRoleObj.jobGridManager = null;
pageRoleObj.departmentGridManager = null;
pageRoleObj.goodsTypeGridManager = null;
// ---------------------------------
// 表格数据重新加载标识
// ---------------------------------
pageRoleObj.userGridDataLoaded = false;
pageRoleObj.jobGridDataLoaded = false;
pageRoleObj.departmentGridDataLoaded = false;
pageRoleObj.goodsTypeGridDataLoaded = false;
// ---------------------------------
// 角色下属数据新增、删除数据ID缓存字符串
// ---------------------------------
pageRoleObj.addUserIds = ";";
pageRoleObj.deleteUserIds = ";";
pageRoleObj.addJobIds = ";";
pageRoleObj.deleteJobIds = ";";
pageRoleObj.addDepartmentIds = ";";
pageRoleObj.deleteDepartmentIds = ";";
// ---------------------------------
// 初始化界面时首个节点ID
// ---------------------------------
pageRoleObj.initFirstNodeId = '';
// ---------------------------------
// 选择数据标问标识。0编辑模式，1单选角色，2多选角色
// ---------------------------------
pageRoleObj.selectDataMode = 0;
// ---------------------------------
// 检查数据是否修改
// ---------------------------------
pageRoleObj.checkDataModified = null;
pageRoleObj.dtlDataModified = false;

// ---------------------------------
// 树展开前事件，异步加载下级节点
// ---------------------------------
pageRoleObj.nodeBeforeExpand = function(node) {
	if (node.data.children && node.data.children.length == 0) {
		var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Role";
		cmd.method = "getRoleTreeData";
		cmd.dataSetNo = '';
		cmd.parentId = node.data.id;
		cmd.recurse = 2;
		cmd.success = function(data) {
			if (data.data.length > 0) {
				pageRoleObj.itemsConvert(data);
				pageRoleObj.treeManager.append(node.target, data.data);
			}
		};
		cmd.execute();
	}
}

pageRoleObj.nodeExpand = function(node) {
	// alert('nodeExpand');
}

// ---------------------------------
// 树选择节点前
// ---------------------------------
pageRoleObj.nodeBeforeSelect = function(node) {
	if (pageRoleObj.dtlDataModified
			|| (pageRoleObj.checkDataModified.checkModified(true) && (pageRoleObj.operNode || pageRoleObj.id == ""))) {
		if (!confirm("数据已修改未保存，确认切换角色节点？\n点击确定按钮将放弃未保存数据"))
			return false;
	}
}

// ---------------------------------
// 树选择节点
// ---------------------------------
pageRoleObj.nodeSelect = function(node) {
	if (node && pageRoleObj.id != node.data.id) {
		pageRoleObj.operNode = node;
		pageRoleObj.editRoleData();
	}
}

// ---------------------------------
// 新增节点
// ---------------------------------
pageRoleObj.addTreeNode = function(text, id, parentId) {
	var node = pageRoleObj.operNode;
	var nodes = [];
	nodes.push({
				text : text,
				id : id,
				parentId : parentId,
				isexpand : false
			});
	if (node) {
		if (parentId == undefined || parentId == null
				|| parentId.toLowerCase() == "null") {// 一级子节点
			pageRoleObj.treeManager.append(null, nodes);
		} else {
			pageRoleObj.treeManager.append(node.target, nodes);
		}
	} else {// 未选择任何节点时新增
		pageRoleObj.treeManager.append(null, nodes);
	}
	// 选中新加的节点
	var selectNodeCallback = function(data) {
		return data.id == id;
	};
	pageRoleObj.treeManager.selectNode(selectNodeCallback);
	pageRoleObj.operNode = pageRoleObj.treeManager.getSelected();
}

// ---------------------------------
// 更新节点
// ---------------------------------
pageRoleObj.updateTreeNode = function(text) {
	var node = pageRoleObj.operNode;
	if (node)
		pageRoleObj.treeManager.update(node.target, {
					text : text
				});
}

// ---------------------------------
// 树删除节点
// ---------------------------------
pageRoleObj.removeTreeNode = function() {
	var node = pageRoleObj.operNode;
	if (node)
		pageRoleObj.treeManager.remove(node.target);
	else
		DialogError('请先选择角色节点');
}

// ---------------------------------
// 树节点转换方法，对后台返回的JSON生成自定义内容
// ---------------------------------
pageRoleObj.itemsConvert = function(items) {
	items.idFieldName = 'id';
	items.parentIDFieldName = 'parentId';
	items.nodeWidth = 200;
	items.checkbox = pageRoleObj.selectDataMode == 2;
	items.onBeforeExpand = this.nodeBeforeExpand;
	items.onExpand = this.nodeExpand;
	items.onBeforeSelect = pageRoleObj.nodeBeforeSelect;
	items.onSelect = pageRoleObj.nodeSelect;
}

// ---------------------------------
// 角色编辑数据初始化
// ---------------------------------
pageRoleObj.editRoleData = function() {
	if (pageRoleObj.selectDataMode == 0) {
		pageRoleObj.operate = "editRole";
		$("#btnAdd").show();
		$("#btnDelete").show();
		var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Role";
		cmd.method = "getRoleData";
		cmd.dataSetNo = "";
		cmd.id = pageRoleObj.operNode.data.id;
		pageRoleObj.id = pageRoleObj.operNode.data.id;
		cmd.success = function(data) {
			pageRoleObj.id = Utils.getRecordValue(data, 0, 'id');
			pageRoleObj.parentId = Utils.getRecordValue(data, 0, 'parent_id');
			if (data && data.rs && data.rs.length > 0) {
				$("#txtName").val(Utils.getRecordValue(data, 0, 'name'));
				$("#txtNo").val(Utils.getRecordValue(data, 0, 'no'));
				$("#chkIsValid").attr("checked",
						Utils.getRecordValue(data, 0, 'is_valid') == "1");
				$("#txtRemark").val(Utils.getRecordValue(data, 0, 'remark'));
				var mainRootMenuId = Utils.getRecordValue(data, 0,
						'main_root_menu_id');
				if (mainRootMenuId == null || mainRootMenuId == ""
						|| mainRootMenuId.toLowerCase() == "null") {
					$("#txtMainRootMenu").val("");
					$("#txtMainRootMenuId").val("");
				} else {
					pageRoleObj.rootMenuComboBoxManager
							.selectValueByTree(mainRootMenuId);
					$("#txtMainRootMenu").val(Utils.getRecordValue(data, 0,
							'main_root_menu_name'));
					$("#txtMainRootMenuId").val(mainRootMenuId);
				}
				var mainMenuId = Utils.getRecordValue(data, 0, 'main_menu_id');
				if (mainMenuId == null || mainMenuId == ""
						|| mainMenuId.toLowerCase() == "null") {
					$("#txtMainMenu").val("");
					$("#txtMainMenuId").val("");
				} else {
					pageRoleObj.menuComboBoxManager
							.selectValueByTree(mainMenuId);
					$("#txtMainMenu").val(Utils.getRecordValue(data, 0,
							'main_menu_name'));
					$("#txtMainMenuId").val(mainMenuId);
				}
				var mainModuleId = Utils.getRecordValue(data, 0,
						'main_module_id');
				if (mainModuleId == null || mainModuleId == ""
						|| mainModuleId.toLowerCase() == "null") {
					$("#txtMainModule").val("");
					$("#txtMainModuleId").val("");
				} else {
					pageRoleObj.moduleComboBoxManager
							.selectValueByTree(mainModuleId);
					$("#txtMainModule").val(Utils.getRecordValue(data, 0,
							'main_module_name'));
					$("#txtMainModuleId").val(mainModuleId);
				}
			}
			pageRoleObj.checkDataModified.initFileds();
			pageRoleObj.dtlDataModified = false;
			pageRoleObj.userGridDataLoaded = false;
			pageRoleObj.jobGridDataLoaded = false;
			pageRoleObj.departmentGridDataLoaded = false;
			pageRoleObj.goodsTypeGridDataLoaded = false;
			pageRoleObj.addUserIds = ";";
			pageRoleObj.deleteUserIds = ";";
			pageRoleObj.addJobIds = ";";
			pageRoleObj.deleteJobIds = ";";
			pageRoleObj.addDepartmentIds = ";";
			pageRoleObj.deleteDepartmentIds = ";";
			pageRoleObj.afterSelectTabItem(pageRoleObj.activeTabId);
		};
		cmd.execute();
	} else {
		pageRoleObj.id = pageRoleObj.operNode.data.id;
		pageRoleObj.userGridDataLoaded = false;
		pageRoleObj.jobGridDataLoaded = false;
		pageRoleObj.departmentGridDataLoaded = false;
		pageRoleObj.goodsTypeGridDataLoaded = false;
		pageRoleObj.addUserIds = ";";
		pageRoleObj.deleteUserIds = ";";
		pageRoleObj.addJobIds = ";";
		pageRoleObj.deleteJobIds = ";";
		pageRoleObj.addDepartmentIds = ";";
		pageRoleObj.deleteDepartmentIds = ";";
	}
}

// ---------------------------------
// 新增角色
// ---------------------------------
pageRoleObj.newRole = function() {
	pageRoleObj.operate = "addRole";
	$("#btnAdd").hide();
	$("#btnDelete").hide();
	$("#txtName").val('');
	$("#txtNo").val('');
	$("#txtMainRootMenu").val('');
	$("#txtMainRootMenuId").val('');
	$("#txtMainMenu").val('');
	$("#txtMainMenuId").val('');
	$("#txtMainModule").val('');
	$("#txtMainModuleId").val('');
	$("#chkIsValid").attr("checked", true);
	$("#txtRemark").val('');
	pageRoleObj.id = '';
	if (pageRoleObj.operNode == null)
		pageRoleObj.parentId = '';
	else
		pageRoleObj.parentId = pageRoleObj.operNode.data.id;
	pageRoleObj.checkDataModified.initFileds();
	pageRoleObj.dtlDataModified = false;
	pageRoleObj.userGridDataLoaded = false;
	pageRoleObj.jobGridDataLoaded = false;
	pageRoleObj.departmentGridDataLoaded = false;
	pageRoleObj.goodsTypeGridDataLoaded = false;
	pageRoleObj.addUserIds = ";";
	pageRoleObj.deleteUserIds = ";";
	pageRoleObj.addJobIds = ";";
	pageRoleObj.deleteJobIds = ";";
	pageRoleObj.addDepartmentIds = ";";
	pageRoleObj.deleteDepartmentIds = ";";
	pageRoleObj.afterSelectTabItem(pageRoleObj.activeTabId);
}


//---------------------------------
//新增角色
//---------------------------------
pageRoleObj.newParentRole = function() {
	pageRoleObj.operate = "addRole";
	$("#btnAdd").hide();
	$("#btnParentAdd").hide();
	$("#btnDelete").hide();
	$("#txtName").val('');
	$("#txtNo").val('');
	$("#txtMainRootMenu").val('');
	$("#txtMainRootMenuId").val('');
	$("#txtMainMenu").val('');
	$("#txtMainMenuId").val('');
	$("#txtMainModule").val('');
	$("#txtMainModuleId").val('');
	$("#chkIsValid").attr("checked", true);
	$("#txtRemark").val('');
	pageRoleObj.id = '';
	pageRoleObj.parentId = "";
	pageRoleObj.checkDataModified.initFileds();
	pageRoleObj.dtlDataModified = false;
	pageRoleObj.userGridDataLoaded = false;
	pageRoleObj.jobGridDataLoaded = false;
	pageRoleObj.departmentGridDataLoaded = false;
	pageRoleObj.goodsTypeGridDataLoaded = false;
	pageRoleObj.addUserIds = ";";
	pageRoleObj.deleteUserIds = ";";
	pageRoleObj.addJobIds = ";";
	pageRoleObj.deleteJobIds = ";";
	pageRoleObj.addDepartmentIds = ";";
	pageRoleObj.deleteDepartmentIds = ";";
	pageRoleObj.afterSelectTabItem(pageRoleObj.activeTabId);
}

// ---------------------------------
// 保存角色
// ---------------------------------
pageRoleObj.saveRole = function() {
	if ($("#txtName").val() == '') {
		DialogAlert('角色名称不能为空');
		return false;
	}
	if ($("#txtNo").val() == '') {
		DialogAlert('角色编号不能为空');
		return false;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Role";
	cmd.method = "updateRoleData";
	cmd.id = pageRoleObj.id;
	cmd.parent_id = pageRoleObj.parentId;
	cmd.no = $("#txtNo").val();
	cmd.name = $("#txtName").val();
	cmd.main_root_menu_id = $("#txtMainRootMenuId").val();
	cmd.main_menu_id = $("#txtMainMenuId").val();
	cmd.main_module_id = $("#txtMainModuleId").val();
	if ($("#chkIsValid").attr("checked")) {
		cmd.is_valid = 1;
	} else {
		cmd.is_valid = 0;
	}
	cmd.remark = $("#txtRemark").val();
	// 下属用户数据
	if (pageRoleObj.userGridManager != null && pageRoleObj.userGridDataLoaded) {
		if (pageRoleObj.addUserIds.length > 1) {
			cmd.addUserIds = pageRoleObj.addUserIds;
		}
		if (pageRoleObj.deleteUserIds.length > 1) {
			cmd.deleteUserIds = pageRoleObj.deleteUserIds;
		}
	}
	// 下属岗位数据
	if (pageRoleObj.jobGridManager != null && pageRoleObj.jobGridDataLoaded) {
		if (pageRoleObj.addJobIds.length > 1) {
			cmd.addJobIds = pageRoleObj.addJobIds;
		}
		if (pageRoleObj.deleteJobIds.length > 1) {
			cmd.deleteJobIds = pageRoleObj.deleteJobIds;
		}
	}
	// 下属部门数据
	if (pageRoleObj.departmentGridManager != null && pageRoleObj.departmentGridDataLoaded) {
		if (pageRoleObj.addDepartmentIds.length > 1) {
			cmd.addDepartmentIds = pageRoleObj.addDepartmentIds;
		}
		if (pageRoleObj.deleteDepartmentIds.length > 1) {
			cmd.deleteDepartmentIds = pageRoleObj.deleteDepartmentIds;
		}
	}
	//交易物类型
	if (pageRoleObj.goodsTypeGridManager != null && pageRoleObj.goodsTypeGridDataLoaded) {
		cmd.roleId = pageRoleObj.id;
		cmd.roleTypeids = pageRoleObj.goodsTypeGridManager.getCheckedArr("goods_type_id").join(",");
	}
	cmd.success = function(data) {
		var state = data.state;
		if (state == '1') {
			if (pageRoleObj.operate == "addRole") {
				pageRoleObj.id = data.id;
				pageRoleObj.operate = "editRole";
				pageRoleObj.addTreeNode($("#txtName").val(), data.id,
						pageRoleObj.parentId);
			} else
				pageRoleObj.updateTreeNode($("#txtName").val());
			pageRoleObj.addUserIds = ";";
			pageRoleObj.deleteUserIds = ";";
			pageRoleObj.addJobIds = ";";
			pageRoleObj.deleteJobIds = ";";
			pageRoleObj.addDepartmentIds = ";";
			pageRoleObj.deleteDepartmentIds = ";";
			pageRoleObj.checkDataModified.initFileds();
			pageRoleObj.dtlDataModified = false;
			DialogAlert('保存角色成功完成');
		} else {
			DialogError('保存角色失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

// ---------------------------------
// 删除角色
// ---------------------------------
pageRoleObj.deleteRole = function() {
	if (pageRoleObj.id == '') { // 正在新增
		if (pageRoleObj.operNode == null)
			pageRoleObj.newRole();
		else
			pageRoleObj.editRoleData();
		return true;
	}
	if (pageRoleObj.operNode == null) {
		DialogWarn('请选择要删除的角色。');
		return false;
	}
	var message = null;
	if (pageRoleObj.operNode.data.children) {
		if (pageRoleObj.operNode.data.children.length > 0)
			message = '当前角色节点 ' + pageRoleObj.operNode.data.text
					+ ' 包含子节点，删除它将删除所有下级角色节点。继续删除？';
		else
			message = '当前角色节点 ' + pageRoleObj.operNode.data.text
					+ ' 可能包含仍未加载的子节点，删除它将删除所有下级角色节点。继续删除？';
	} else
		message = '删除当前角色节点 ' + pageRoleObj.operNode.data.text + '？';
	DialogConfirm(message, function(yes) {
				if (yes) {
					var cmd = new Command();
					cmd.module = "sysman";
					cmd.service = "Role";
					cmd.method = "deleteRoleData";
					cmd.id = pageRoleObj.operNode.data.id;
					cmd.success = function(data) {
						var state = data.state;
						if (state == '1') {
							pageRoleObj.removeTreeNode();
							DialogAlert('删除角色数据成功完成');
						} else {
							DialogError('删除角色失败,错误原因：' + data.message);
							return false;
						}
					};
					cmd.execute();
				}
			});
}

// ---------------------------------
// 树数据初始化
// ---------------------------------
pageRoleObj.initTreeData = function() {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Role";
	cmd.method = "getRoleTreeData";
	cmd.dataSetNo = '';
	cmd.parentId = '';
	cmd.recurse = 2;
	cmd.async = false;
	cmd.success = function(data) {
		if (data.data.length > 0) {
			pageRoleObj.initFirstNodeId = data.data[0].id;
		} else {
			pageRoleObj.initFirstNodeId = '';
		}
		pageRoleObj.itemsConvert(data);
		$("#roleTree").ligerTree(data);
		pageRoleObj.treeManager = $("#roleTree").ligerGetTreeManager();
	};
	cmd.execute();
}

// ---------------------------------
// 根菜单树展开节点前
// ---------------------------------
pageRoleObj.rootMenuNodeBeforeExpand = function(node) {
	if (node.data.children && node.data.children.length == 0) {
		var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Menu";
		cmd.method = "getMenuTreeData";
		cmd.dataSetNo = 'queryMenuFlatTree';
		cmd.parentId = node.data.id;
		cmd.recurse = 2;
		cmd.success = function(data) {
			if (data.menu.length > 0) {
				pageRoleObj.rootMenuItemsConvert(data);
				pageRoleObj.rootMenuTreeManager.append(node.target, data.data);
			}
		};
		cmd.execute();
	}
}

// ---------------------------------
// 根菜单树节点转换
// ---------------------------------
pageRoleObj.rootMenuItemsConvert = function(items) {
	items.idFieldName = 'id';
	items.parentIDFieldName = 'parentId';
	items.nodeWidth = 200;
	items.checkbox = false;
	items.onBeforeExpand = pageRoleObj.rootMenuNodeBeforeExpand;
	items.data = items.menu;
	delete items.menu;
}

// ---------------------------------
// 根菜单树下拉框初始化
// ---------------------------------
pageRoleObj.initRootMenuComboBox = function() {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Menu";
	cmd.method = "getMenuTreeData";
	cmd.dataSetNo = "queryMenuFlatTree";
	cmd.parentId = '';
	cmd.recurse = 2;
	cmd.isexpand = 'false';
	cmd.success = function(data) {
		pageRoleObj.rootMenuItemsConvert(data);
		pageRoleObj.rootMenuComboBoxManager.setTree(data);
		pageRoleObj.rootMenuTreeManager = pageRoleObj.rootMenuComboBoxManager.treeManager;
	};
	cmd.execute();
}

// ---------------------------------
// 菜单树展开节点前
// ---------------------------------
pageRoleObj.menuNodeBeforeExpand = function(node) {
	if (node.data.children && node.data.children.length == 0) {
		var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Menu";
		cmd.method = "getMenuTreeData";
		cmd.dataSetNo = 'queryMenuFlatTree';
		cmd.parentId = node.data.id;
		cmd.recurse = 2;
		cmd.success = function(data) {
			if (data.menu.length > 0) {
				pageRoleObj.menuItemsConvert(data);
				pageRoleObj.menuTreeManager.append(node.target, data.data);
			}
		};
		cmd.execute();
	}
}

// ---------------------------------
// 菜单树节点转换
// ---------------------------------
pageRoleObj.menuItemsConvert = function(items) {
	items.idFieldName = 'id';
	items.parentIDFieldName = 'parentId';
	items.nodeWidth = 200;
	items.checkbox = false;
	items.onBeforeExpand = pageRoleObj.menuNodeBeforeExpand;
	items.data = items.menu;
	delete items.menu;
}

// ---------------------------------
// 菜单树下拉框初始化
// ---------------------------------
pageRoleObj.initMenuComboBox = function() {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Menu";
	cmd.method = "getMenuTreeData";
	cmd.dataSetNo = "queryMenuFlatTree";
	cmd.parentId = '';
	cmd.recurse = 2;
	cmd.isexpand = 'false';
	cmd.success = function(data) {
		pageRoleObj.menuItemsConvert(data);
		pageRoleObj.menuComboBoxManager.setTree(data);
		pageRoleObj.menuTreeManager = pageRoleObj.menuComboBoxManager.treeManager;
	};
	cmd.execute();
}

// ---------------------------------
// 模块树展开节点前
// ---------------------------------
pageRoleObj.moduleNodeBeforeExpand = function(node) {
	if (node.data.children && node.data.children.length == 0) {
		var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Module";
		cmd.method = "getModuleTreeData";
		cmd.moduleNo = "sys_module_manager";
		cmd.dataSetNo = 'queryModuleFlatTree';
		cmd.parentId = node.data.id;
		cmd.recurse = 2;
		cmd.success = function(data) {
			if (data.data.length > 0) {
				pageRoleObj.moduleItemsConvert(data);
				pageRoleObj.moduleTreeManager.append(node.target, data.data);
			}
		};
		cmd.execute();
	}
}

// ---------------------------------
// 模块树节点转换
// ---------------------------------
pageRoleObj.moduleItemsConvert = function(items) {
	items.idFieldName = 'id';
	items.parentIDFieldName = 'parentId';
	items.nodeWidth = 200;
	items.checkbox = false;
	items.onBeforeExpand = pageRoleObj.moduleNodeBeforeExpand;
	items.onExpand = pageRoleObj.moduleNodeExpand;
}

// ---------------------------------
// 模块树下拉框初始化
// ---------------------------------
pageRoleObj.initModuleComboBox = function() {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Module";
	cmd.method = "getModuleTreeData";
	cmd.moduleNo = "sys_module_manager";
	cmd.dataSetNo = "queryModuleFlatTree";
	cmd.parentId = '';
	cmd.recurse = 2;
	cmd.isexpand = 'false';
	cmd.success = function(data) {
		pageRoleObj.moduleItemsConvert(data);
		pageRoleObj.moduleComboBoxManager.setTree(data);
		pageRoleObj.moduleTreeManager = pageRoleObj.moduleComboBoxManager.treeManager;
	};
	cmd.execute();
}

// ---------------------------------
// 转换用户类型
// ---------------------------------
pageRoleObj.userTypeRender = function(row, rowNamber) {
	var userType = Number(row.userType).toFixed(0);
	if (userType == "1")
		return "竞买人";
	else if (userType == "2")
		return "银行工作人员";
	else
		return "内部人员";
}

// ---------------------------------
// 转换名称
// ---------------------------------
pageRoleObj.userNameRender = function(row, rowNamber) {
	var userType = Number(row.userType).toFixed(0);
	var userName = row.userName;
	var relName = row.relName;
	if (relName != null && relName != '' && relName.toLowerCase() != 'null'
			&& relName.toLowerCase() != 'undefined') {
		userName = relName + '(' + userName + ')'
	}
	if (userType == '0') {
		var organName = row.organName;
		return organName + '.' + userName;
	} else {
		return userName;
	}

}

pageRoleObj.jobNameRender = function(row, rowNamber) {
	var jobName = row.name;
	var departmentName = row.departmentName;
	var organName = row.organName;
	return organName + '.' + departmentName + '.' + jobName;
}

pageRoleObj.departmentNameRender = function(row, rowNamber) {
	var departmentName = row.name;
	var organName = row.organName;
	return organName + '.' + departmentName;
}

// ---------------------------------
// 获取表格查询参数
// ---------------------------------
pageRoleObj.getGridQueryParams = function(gridManager, gridBaseUrl) {
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
	var id = pageRoleObj.id;
	var queryParams = {};
	if (gridManager.options.usePager) {
		queryParams.pageRowCount = pageRowCount;
		queryParams.pageIndex = pageIndex;
		queryParams.sortField = sortField;
		queryParams.sortDir = sortDir;
	}
	queryParams.id = id;
	queryParams.u = pageRoleObj.userId;
	var url = gridBaseUrl;
	// var gridOptions = {url: url}
	// gridManager.setOptions(gridOptions);
	gridManager.options.url = url;
	gridManager.loadServerData(queryParams);
	queryParams = {
		id : id,
		mm : 'get'
	};
	for (var key in queryParams) {
		url = Utils.setParamValue(url, key, queryParams[key], false, '=', '&');
	}
	gridManager.options.url = url;
	return queryParams;
}

// ---------------------------------
// 添加用户事件
// ---------------------------------
pageRoleObj.addUser = function() {
	DialogOpen({
		height : 400,
		width : 650,
		title : '选择新增用户',
		url : approot
				+ '/sysman/userList.html?selectData=2&gridWidth=630&gridHeight=255',
		buttons : [{
			text : '确定',
			onclick : function(item, dialog) {
				var selectedDataFn = dialog.frame.pageObj.getCheckedUserData
						|| dialog.frame.window.pageObj.getCheckedUserData;
				var selectedData = selectedDataFn();
				if (selectedData.length == 0) {
					DialogWarn('请选择要添加的用户。');
					return false;
				}
				for (var i = 0; i < selectedData.length; i++) {
					var blnExists = false;
					var id = selectedData[i].id;
					for (var row in pageRoleObj.userGridManager.records) {
						if (pageRoleObj.userGridManager.records[row].id == id) {
							blnExists = true;
							break;
						}
					}
					if (!blnExists) {
						pageRoleObj.userGridManager.addRow({
									id : id,
									userName : selectedData[i].userName,
									userType : selectedData[i].userType,
									organName : selectedData[i].organName,
									relRoles : selectedData[i].relRoles,
									relDepartments : selectedData[i].relDepartments,
									relJobs : selectedData[i].relJobs
								});
						if (pageRoleObj.deleteUserIds.indexOf(';' + id + ';') != -1) {
							pageRoleObj.deleteUserIds = pageRoleObj.deleteUserIds
									.replace(';' + id + ';', ';');
						} else if (pageRoleObj.addUserIds.indexOf(';' + id
								+ ';') == -1) {
							pageRoleObj.addUserIds += id + ';';
						}
					}
				}
				pageRoleObj.dtlDataModified = true;
				dialog.close();
			}
		}, {
			text : '取消',
			onclick : function(item, dialog) {
				dialog.close();
			}
		}]
	});
}

// ---------------------------------
// 删除用户事件
// ---------------------------------
pageRoleObj.deleteUser = function() {
	var selectedRow = pageRoleObj.userGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要删除的用户。');
		return false;
	}
	DialogConfirm('在当前角色 ' + $("#txtName").val() + ' 下移除用户 '
					+ selectedRow.userName + '？', function(yes) {
				if (yes) {
					var id = selectedRow.id;
					if (pageRoleObj.addUserIds.indexOf(';' + id + ';') != -1) {
						pageRoleObj.addUserIds = pageRoleObj.addUserIds
								.replace(';' + id + ';', ';');
					} else if (pageRoleObj.deleteUserIds
							.indexOf(';' + id + ';') == -1) {
						pageRoleObj.deleteUserIds += id + ';';
					}
					pageRoleObj.userGridManager.deleteSelectedRow();
					pageRoleObj.dtlDataModified = true;
				}
			});
}

// ---------------------------------
// 添加岗位事件
// ---------------------------------
pageRoleObj.addJob = function() {
	DialogOpen({
		width : 880,
		height : 520,
		title : '选择新增岗位',
		url : approot + '/sysman/organ.html?editMode=1&selectData=6&u='
				+ pageRoleObj.userId + "&height=360&width=650",
		buttons : [{
			text : '确定',
			onclick : function(item, dialog) {
				var selectedDataFn = dialog.frame.pageOrganObj.getCheckedJobData
						|| dialog.frame.window.pageOrganObj.getCheckedJobData;
				var selectedData = selectedDataFn();
				if (selectedData.length == 0) {
					DialogWarn('请选择要添加的岗位。');
					return false;
				}
				for (var i = 0; i < selectedData.length; i++) {
					var blnExists = false;
					var id = selectedData[i].id;
					for (var row in pageRoleObj.jobGridManager.records) {
						if (pageRoleObj.jobGridManager.records[row].id == id) {
							blnExists = true;
							break;
						}
					}
					if (!blnExists) {
						pageRoleObj.jobGridManager.addRow({
									id : id,
									name : selectedData[i].name,
									no : selectedData[i].no,
									organName : selectedData[i].organName,
									departmentName : selectedData[i].departmentName,
									relRoles : selectedData[i].relRoles
								});
						if (pageRoleObj.deleteJobIds.indexOf(';' + id + ';') != -1) {
							pageRoleObj.deleteJobIds = pageRoleObj.deleteJobIds
									.replace(';' + id + ';', ';');
						} else if (pageRoleObj.addJobIds
								.indexOf(';' + id + ';') == -1) {
							pageRoleObj.addJobIds += id + ';';
						}
					}
				}
				pageRoleObj.dtlDataModified = true;
				dialog.close();
			}
		}, {
			text : '取消',
			onclick : function(item, dialog) {
				dialog.close();
			}
		}]
	});
}

// ---------------------------------
// 删除岗位事件
// ---------------------------------
pageRoleObj.deleteJob = function() {
	var selectedRow = pageRoleObj.jobGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要删除的岗位。');
		return false;
	}
	DialogConfirm('在当前角色 ' + $("#txtName").val() + ' 下移除岗位 ' + selectedRow.name
					+ '？', function(yes) {
				if (yes) {
					var id = selectedRow.id;
					if (pageRoleObj.addJobIds.indexOf(';' + id + ';') != -1) {
						pageRoleObj.addJobIds = pageRoleObj.addJobIds.replace(
								';' + id + ';', ';');
					} else if (pageRoleObj.deleteJobIds.indexOf(';' + id + ';') == -1) {
						pageRoleObj.deleteJobIds += id + ';';
					}
					pageRoleObj.jobGridManager.deleteSelectedRow();
					pageRoleObj.dtlDataModified = true;
				}
			});
}

// ---------------------------------
// 添加部门事件
// ---------------------------------
pageRoleObj.addDepartment = function() {
	DialogOpen({
		height : 420,
		width : 660,
		title : '选择新增部门',
		url : approot + '/sysman/organ.html?editMode=1&selectData=4&u='
				+ pageRoleObj.userId + "&height=350&width=650",
		buttons : [{
			text : '确定',
			onclick : function(item, dialog) {
				var selectedDataFn = dialog.frame.pageOrganObj.getCheckedDepartmentData
						|| dialog.frame.window.pageOrganObj.getCheckedDepartmentData;
				var selectedData = selectedDataFn();
				if (selectedData.length == 0) {
					DialogWarn('请选择要添加的部门。');
					return false;
				}
				for (var i = 0; i < selectedData.length; i++) {
					var blnExists = false;
					var id = selectedData[i].data.id;
					for (var row in pageRoleObj.departmentGridManager.records) {
						if (pageRoleObj.departmentGridManager.records[row].id == id) {
							blnExists = true;
							break;
						}
					}
					if (!blnExists) {
						pageRoleObj.departmentGridManager.addRow({
									id : id,
									name : selectedData[i].data.text,
									no : selectedData[i].data.no,
									organName : selectedData[i].data.organName,
									relRoles : selectedData[i].relRoles
								});
						if (pageRoleObj.deleteDepartmentIds.indexOf(';' + id
								+ ';') != -1) {
							pageRoleObj.deleteDepartmentIds = pageRoleObj.deleteDepartmentIds
									.replace(';' + id + ';', ';');
						} else if (pageRoleObj.addDepartmentIds.indexOf(';'
								+ id + ';') == -1) {
							pageRoleObj.addDepartmentIds += id + ';';
						}
					}
				}
				pageRoleObj.dtlDataModified = true;
				dialog.close();
			}
		}, {
			text : '取消',
			onclick : function(item, dialog) {
				dialog.close();
			}
		}]
	});
}

// ---------------------------------
// 删除部门事件
// ---------------------------------
pageRoleObj.deleteDepartment = function() {
	var selectedRow = pageRoleObj.departmentGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要删除的部门。');
		return false;
	}
	DialogConfirm('在当前角色 ' + $("#txtName").val() + ' 下移除部门 ' + selectedRow.name
					+ '？', function(yes) {
				if (yes) {
					var id = selectedRow.id;
					if (pageRoleObj.addDepartmentIds.indexOf(';' + id + ';') != -1) {
						pageRoleObj.addDepartmentIds = pageRoleObj.addDepartmentIds
								.replace(';' + id + ';', ';');
					} else if (pageRoleObj.deleteDepartmentIds.indexOf(';' + id
							+ ';') == -1) {
						pageRoleObj.deleteDepartmentIds += id + ';';
					}
					pageRoleObj.departmentGridManager.deleteSelectedRow();
					pageRoleObj.dtlDataModified = true;
				}
			});
}

// ---------------------------------
// 角色下属信息标签TAB切换前事件
// ---------------------------------
pageRoleObj.beforeSelectTabItem = function(tabid) {
}

// ---------------------------------
// 角色下属信息标签TAB切换后事件
// ---------------------------------
pageRoleObj.afterSelectTabItem = function(tabid) {
	if (tabid == 'tabitem1') {// 用户
		if (pageRoleObj.userGridManager == null) {
			pageRoleObj.userGridManager = $("#userGrid").ligerGrid({
				url : approot
						+ '/data?module=sysman&service=Role&method=getRoleUserData&moduleId='
						+ pageRoleObj.moduleId + '&id=' + pageRoleObj.id
						+ '&u=' + pageRoleObj.userId,
				columns : [{
							display : '名称',
							name : 'userName',
							width : '25%',
							render : pageRoleObj.userNameRender
						}, {
							display : '用户类型',
							name : 'userType',
							width : '10%',
							render : pageRoleObj.userTypeRender
						}, {
							display : '所属角色',
							name : 'relRoles',
							width : '25%'
						}, {
							display : '所属部门',
							name : 'relDepartments',
							width : '24%'
						}, {
							display : '担任岗位',
							name : 'relJobs',
							width : '14%'
						}],
				usePager : false,
				pagesizeParmName : 'pageRowCount',
				pageParmName : 'pageIndex',
				sortnameParmName : 'sortField',
				sortorderParmName : 'sortDir',
				isScroll : true,
				frozen : false,
				pageSizeOptions : [10, 20, 30],
				showTitle : false,
				fixedCellHeight : false,
				width : '99.5%',
				height : '99%',
				toolbar : {
					items : [{
								text : '新增',
								id : 'btnAddUser',
								click : pageRoleObj.addUser,
								icon : 'add'
							}, {
								line : true
							}, {
								text : '删除',
								id : 'btnDeleteUser',
								click : pageRoleObj.deleteUser,
								icon : 'delete'
							}]
				}
			});
		} else if (!pageRoleObj.userGridDataLoaded) {
			pageRoleObj
					.getGridQueryParams(
							pageRoleObj.userGridManager,
							approot
									+ '/data?module=sysman&service=Role&method=getRoleUserData&moduleId='
									+ pageRoleObj.moduleId);
		}
		pageRoleObj.userGridDataLoaded = true;
	} else if (tabid == 'tabitem2') {// 岗位
		if (pageRoleObj.jobGridManager == null) {
			pageRoleObj.jobGridManager = $("#jobGrid").ligerGrid({
				url : approot
						+ '/data?module=sysman&service=Role&method=getRoleJobData&moduleId='
						+ pageRoleObj.moduleId + '&id=' + pageRoleObj.id
						+ '&u=' + pageRoleObj.userId,
				columns : [{
							display : '名称',
							name : 'name',
							width : '50%',
							render : pageRoleObj.jobNameRender
						}, {
							display : '编号',
							name : 'no',
							width : '23%'
						}, {
							display : '所属角色',
							name : 'relRoles',
							width : '24%'
						}],
				usePager : false,
				pagesizeParmName : 'pageRowCount',
				pageParmName : 'pageIndex',
				sortnameParmName : 'sortField',
				sortorderParmName : 'sortDir',
				isScroll : true,
				frozen : false,
				pageSizeOptions : [10, 20, 30],
				showTitle : false,
				fixedCellHeight : false,
				width : '99.5%',
				height : '99%',
				toolbar : {
					items : [{
								text : '新增',
								id : 'btnAddJob',
								click : pageRoleObj.addJob,
								icon : 'add'
							}, {
								line : true
							}, {
								text : '删除',
								id : 'btnDeleteJob',
								click : pageRoleObj.deleteJob,
								icon : 'delete'
							}]
				}
			});
		} else if (!pageRoleObj.jobGridDataLoaded) {
			pageRoleObj
					.getGridQueryParams(
							pageRoleObj.jobGridManager,
							approot
									+ '/data?module=sysman&service=Role&method=getRoleJobData&moduleId='
									+ pageRoleObj.moduleId);
		}
		pageRoleObj.jobGridDataLoaded = true;
	} else if (tabid == 'tabitem3') {// 部门
		if (pageRoleObj.departmentGridManager == null) {
			pageRoleObj.departmentGridManager = $("#departmentGrid").ligerGrid(
					{
						url : approot
								+ '/data?module=sysman&service=Role&method=getRoleDepartmentData&moduleId='
								+ pageRoleObj.moduleId + '&id='
								+ pageRoleObj.id + '&u=' + pageRoleObj.userId,
						columns : [{
									display : '名称',
									name : 'name',
									width : '50%',
									render : pageRoleObj.departmentNameRender
								}, {
									display : '编号',
									name : 'no',
									width : '23%'
								}, {
									display : '所属角色',
									name : 'relRoles',
									width : '24%'
								}],
						usePager : false,
						pagesizeParmName : 'pageRowCount',
						pageParmName : 'pageIndex',
						sortnameParmName : 'sortField',
						sortorderParmName : 'sortDir',
						isScroll : true,
						frozen : false,
						pageSizeOptions : [10, 20, 30],
						showTitle : false,
						fixedCellHeight : false,
						width : '99.5%',
						height : '99%',
						toolbar : {
							items : [{
										text : '新增',
										id : 'btnAddDepartment',
										click : pageRoleObj.addDepartment,
										icon : 'add'
									}, {
										line : true
									}, {
										text : '删除',
										id : 'btnDeleteDepartment',
										click : pageRoleObj.deleteDepartment,
										icon : 'delete'
									}]
						}
					});
		} else if (!pageRoleObj.departmentGridDataLoaded) {
			pageRoleObj
					.getGridQueryParams(
							pageRoleObj.departmentGridManager,
							approot
									+ '/data?module=sysman&service=Role&method=getRoleDepartmentData&moduleId='
									+ pageRoleObj.moduleId);
		}
		pageRoleObj.departmentGridDataLoaded = true;
	} else if (tabid == 'tabitem4') {// 交易物类型
		var url = approot
				+ '/data?module=sysman&service=Role&method=getRoleGoodsType&roleId='
				+ pageRoleObj.id + '&u=' + pageRoleObj.userId;
		if (!pageRoleObj.goodsTypeGridManager)
			pageRoleObj.goodsTypeGridManager = $("#typeGrid").ligerGrid({
						url : url,
						columns : [{
									display : '类型',
									name : 'goods_type_name',
									width : '50%'
								}],
						usePager : false,
						checkbox : true,
						rownumbers : true,
						isScroll : true, // 是否滚动
						frozen : false,// 是否固定列
						showTitle : false,
						width : '99.8%',
						alternatingRow : false,
						isChecked : pageRoleObj.initRoleTypeChecked
					});
		else if (!pageRoleObj.goodsTypeGridDataLoaded)
			pageRoleObj.goodsTypeGridManager.refresh(url, {});
		pageRoleObj.goodsTypeGridDataLoaded = true;
	}
	pageRoleObj.activeTabId = tabid;
}
// ---------------------------------
// 保存角色 初始化选择
// ---------------------------------
pageRoleObj.initRoleTypeChecked = function(rowData) {
	if (rowData.rel_id)
		return true;
	else
		return false;
}



// ---------------------------------
// 角色下属信息标签TAB初始化
// ---------------------------------
pageRoleObj.initRoleRelTab = function() {
	$("#tabRoleRel").ligerTab({
				contextmenu : false,
				// height: 300,
				onBeforeSelectTabItem : pageRoleObj.beforeSelectTabItem,
				onAfterSelectTabItem : pageRoleObj.afterSelectTabItem
			});
}

// ---------------------------------
// 选择数据页面时的选择方法
// ---------------------------------
pageRoleObj.getSelectedRoleData = function() {
	return pageRoleObj.treeManager.getSelected();
}

pageRoleObj.getCheckedRoleData = function() {
	return pageRoleObj.treeManager.getChecked();
}

// ---------------------------------
// 清除选择下拉框内容
// ---------------------------------
pageRoleObj.clearSelectedRootMenu = function() {
	$("#txtMainRootMenu").val('');
	$("#txtMainRootMenuId").val('');
}

pageRoleObj.clearSelectedMenu = function() {
	$("#txtMainMenu").val('');
	$("#txtMainMenuId").val('');
}

pageRoleObj.clearSelectedModule = function() {
	$("#txtMainModule").val('');
	$("#txtMainModuleId").val('');
}

// ---------------------------------
// 页面初始化
// ---------------------------------
pageRoleObj.initHtml = function() {
	pageRoleObj.moduleId = Utils.getUrlParamValue(document.location.href,
			"moduleId");
	pageRoleObj.userId = getUserId();
	var selectDataParam = Utils.getUrlParamValue(document.location.href,
			"selectData");
	if (selectDataParam == '1') {
		pageRoleObj.selectDataMode = 1;
	} else if (selectDataParam == '2') {
		pageRoleObj.selectDataMode = 2;
	} else {
		pageRoleObj.selectDataMode = 0;
	}

	if (pageRoleObj.selectDataMode == 0) {
		$("#btnAdd").click(pageRoleObj.newRole);
		$("#btnDelete").click(pageRoleObj.deleteRole);
		$("#btnSave").click(pageRoleObj.saveRole);
	} else {
		$("#btnAdd").hide();
		$("#btnDelete").hide();
		$("#btnSave").hide();
		$("#roleTreeTitle").hide();
		$("#role").hide();
	}
	
	$("#btnParentAdd").click(pageRoleObj.newParentRole);
	pageRoleObj.initTreeData();
	if (pageRoleObj.selectDataMode == 0) {
		pageRoleObj.initRoleRelTab();
		pageRoleObj.rootMenuComboBoxManager = $("#txtMainRootMenu")
				.ligerComboBox({
							valueField : "id",
							valueFieldID : "txtMainRootMenuId",
							treeLeafOnly : false,
							isShowCheckBox : true,
							width : 320
						});
		pageRoleObj.initRootMenuComboBox();
		pageRoleObj.menuComboBoxManager = $("#txtMainMenu").ligerComboBox({
					valueField : "id",
					valueFieldID : "txtMainMenuId",
					treeLeafOnly : false,
					isShowCheckBox : true,
					width : 320
				});
		pageRoleObj.initMenuComboBox();
		pageRoleObj.moduleComboBoxManager = $("#txtMainModule").ligerComboBox({
					valueField : "id",
					valueFieldID : "txtMainModuleId",
					treeLeafOnly : false,
					isShowCheckBox : true,
					width : 320
				});
		pageRoleObj.initModuleComboBox();
		$("#btnClearMainRootMenu").click(pageRoleObj.clearSelectedRootMenu);
		$("#btnClearMainMenu").click(pageRoleObj.clearSelectedMenu);
		$("#btnClearMainModule").click(pageRoleObj.clearSelectedModule);
	}
	pageRoleObj.checkDataModified = new FieldModifiedChecker($("#roleData")[0]);
	if (pageRoleObj.initFirstNodeId == "") {
		if (pageRoleObj.selectDataMode == 0) {
			pageRoleObj.newModule();
		}
	} else {
		// 选中首节点
		var selectNodeCallback = function(data) {
			return data.id == pageRoleObj.initFirstNodeId;
		};
		pageRoleObj.treeManager.selectNode(selectNodeCallback);
		pageRoleObj.operNode = pageRoleObj.treeManager.getSelected();
		pageRoleObj.nodeSelect(pageRoleObj.operNode);
	}
	if (pageRoleObj.selectDataMode == 0) {
		pageRoleObj.afterSelectTabItem('tabitem1');
	} else {

		var initWidth = parseInt(Utils.getUrlParamValue(document.location.href,
				"width"));
		initWidth = isNaN(initWidth) ? 0 : initWidth;
		var initHeight = parseInt(Utils.getUrlParamValue(
				document.location.href, "height"));
		initHeight = isNaN(initHeight) ? 0 : initHeight;

		if (initHeight > 0 && initWidth > 0) {
			$(".i-wrap").css("height", initHeight - 35 + "px");
			$(".i-wrap").css("width", initWidth - 20 + "px");
			$(".i-wrap").css("background", "white");
			$(".i-left_bar").css("border", "none");
			$(".i-left_bar").css("width", initWidth - 25 + "px");
			$(".i-left_bar").css("height", initHeight - 25 + "px");
		}
	}
}

$(document).ready(pageRoleObj.initHtml);