//---------------------------------
//页页对象
//---------------------------------
var pageObj = {}
//---------------------------------
//树管理器引用
//---------------------------------
pageObj.treeManager = null;
//---------------------------------
//树当前操作节点引用
//---------------------------------
pageObj.operNode = null;
//---------------------------------
//标签页管理器引用
//---------------------------------
pageObj.tabManager = null;
//---------------------------------
//表格对象
//---------------------------------
pageObj.jobGridManager = null;
pageObj.empGridManager = null;
//---------------------------------
//表格数据重新加载标识
//---------------------------------
pageObj.jobGridDataLoaded = false;
pageObj.empGridDataLoaded = false;
//---------------------------------
//初始化界面时首个节点ID
//---------------------------------
pageObj.initFirstNodeId = '';
//---------------------------------
//单位ID
//---------------------------------
pageObj.initOrganId = '';
//---------------------------------
//仅删除关联的人员ID
//---------------------------------
pageObj.deleteEmpRelIds = ';';
//---------------------------------
//选择数据标识。0编辑模式，1单选部门，2多选部门，3单选岗位，4多选岗位，5单选人员，6多选人员
//---------------------------------
pageObj.selectDataMode = 0;
pageObj.selectOrganData = false;
//---------------------------------
//检查数据是否修改
//---------------------------------
pageObj.checkDataModified = null;
//---------------------------------
//初始高度
//---------------------------------
pageObj.initWidth = 0;
pageObj.initHeight = 0;
pageObj.initGridWidth = 0;
pageObj.initGridHeight = 0;

//---------------------------------
//树展开前事件，异步加载下级节点
//---------------------------------
pageObj.nodeBeforeExpand = function (node)
{
	if (node.data.children && node.data.children.length == 0)
    {
        var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Department";
		cmd.method = "getDepartmentTreeData";
		cmd.dataSetNo = '';
		cmd.parentId = node.data.id;
		cmd.recurse = 2;
		cmd.success = function(data) {
			if (data.data.length > 0) {
				pageObj.itemsConvert(data);
				pageObj.treeManager.append(node.target, data.data);
			}
		};
		cmd.execute();
    }
}

pageObj.nodeExpand = function (node)
{
	//alert('nodeExpand');
}

//---------------------------------
//树选择节点前
//---------------------------------
pageObj.nodeBeforeSelect = function(node){
	if ((pageObj.jobGridManager && pageObj.jobGridManager.isDataChanged)
		|| (pageObj.empGridManager && pageObj.empGridManager.isDataChanged)
		|| (pageObj.checkDataModified.checkModified(true) && (pageObj.operNode || pageObj.id == ""))) {
		if (!confirm("数据已修改未保存，确认切换部门节点？\n点击确定按钮将放弃未保存数据"))
			return false;
	}
	
}

//---------------------------------
//树选择节点
//---------------------------------
pageObj.nodeSelect = function(node){
	if (node && pageObj.id != node.data.id) {
		pageObj.operNode = node;
		pageObj.editDepartmentData();
	}
}

//---------------------------------
//新增节点
//---------------------------------
pageObj.addTreeNode = function(text, id, parentId){
	var node = pageObj.operNode;
    var nodes = [];
  	nodes.push({ text: text, id: id, parentId: parentId, isexpand: false});
    if (node) {
    	if (parentId == undefined || parentId == null || parentId.toLowerCase() == "null" || parentId == "undefined"
    		|| parentId == pageObj.initOrganId) {//一级子节点
    		pageObj.treeManager.append(null, nodes);
    	} else if (node.data.id == parentId) {
    		pageObj.treeManager.append(node.target, nodes);
    	} else {
    		var parentNodeTarget = pageObj.treeManager.getParentTreeItem(node.target);
    		if (parentNodeTarget) {
    			pageObj.treeManager.append(parentNodeTarget, nodes);
    		} else {
    			pageObj.treeManager.append(null, nodes);
    		}
    	}
    } else {//未选择任何节点时新增
    	pageObj.treeManager.append(null, nodes);
    }
    //选中新加的节点
	var selectNodeCallback = function (data)
	    {
	        return data.id == id;
	    };
    pageObj.treeManager.selectNode(selectNodeCallback);
    pageObj.operNode = pageObj.treeManager.getSelected();
}

//---------------------------------
//更新节点
//---------------------------------
pageObj.updateTreeNode = function(text){
	var node = pageObj.operNode;
    if (node) {
    	pageObj.treeManager.update(node.target, { text: text});
    }
}

//---------------------------------
//树删除节点
//---------------------------------
pageObj.removeTreeNode = function(){
	var node = pageObj.operNode;
    if (node)
        pageObj.treeManager.remove(node.target);
    else
        DialogError('请先选择部门节点');
}

//---------------------------------
//树节点转换方法，对后台返回的JSON生成自定义内容
//---------------------------------
pageObj.itemsConvert = function (items) {
	items.idFieldName = 'id';
    items.parentIDFieldName = 'parentId'; 
    items.nodeWidth = 200;
    items.checkbox = pageObj.selectDataMode == 2;
    items.onBeforeExpand = pageObj.nodeBeforeExpand;
    items.onExpand = pageObj.nodeExpand;
    items.onBeforeSelect = pageObj.nodeBeforeSelect;
    items.onSelect = pageObj.nodeSelect;
}

//---------------------------------
//部门编辑数据初始化
//---------------------------------
pageObj.editDepartmentData = function () {
	if (pageObj.selectDataMode == 0) {
		pageObj.operate = "editDepartment";
		$("#btnAdd").show();
		$("#btnDelete").show();
		var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Department";
		cmd.method = "getDepartmentData";
		cmd.dataSetNo = "";
		cmd.id = pageObj.operNode.data.id;
		pageObj.id = pageObj.operNode.data.id;
		cmd.success = function (data) {
			pageObj.id = Utils.getRecordValue(data, 0, 'id');
			pageObj.parentId = Utils.getRecordValue(data, 0, 'parent_id');
			if (data && data.rs && data.rs.length > 0) {
				$("#txtName").val(Utils.getRecordValue(data, 0, 'name'));
				$("#txtNo").val(Utils.getRecordValue(data, 0, 'no'));
				$("#txtOrgan").val(Utils.getRecordValue(data, 0, 'organ_name'));
				$("#chkIsValid").attr("checked", Utils.getRecordValue(data, 0, 'is_valid') == "1");
				$("#txtRemark").val(Utils.getRecordValue(data, 0, 'remark'));
			}
			pageObj.jobGridDataLoaded = false;
			pageObj.empGridDataLoaded = false;
			pageObj.deleteEmpRelIds = ";";
			pageObj.afterSelectTabItem(pageObj.activeTabId);
			pageObj.checkDataModified.initFileds();
			if (pageObj.jobGridManager)
				pageObj.jobGridManager.cancelEdit();
			if (pageObj.empGridManager)
				pageObj.empGridManager.cancelEdit();
		};
		cmd.execute();
	} else {//选择数据
		pageObj.id = pageObj.operNode.data.id;
		pageObj.jobGridDataLoaded = false;
		pageObj.empGridDataLoaded = false;
		pageObj.deleteEmpRelIds = ";";
		if (pageObj.selectDataMode > 2) {
			pageObj.afterSelectTabItem(pageObj.activeTabId);
		}
	}
}

//---------------------------------
//新增部门
//---------------------------------
pageObj.newDepartment = function () {
	return pageObj.addDepartment(false);
}

//---------------------------------
//新增子部门
//---------------------------------
pageObj.newChildDepartment = function () {
	return pageObj.addDepartment(true);
}

pageObj.addDepartment = function (addChild) {
	pageObj.operate = "addDepartment";
	$("#btnAdd").hide();
	$("#btnAddChild").hide();
	$("#btnDelete").hide();
	$("#txtName").val('');
	$("#txtNo").val('');
	$("#chkIsValid").attr("checked", true);
	$("#txtRemark").val('');
	pageObj.id = '';
	if (pageObj.operNode == null)
		pageObj.parentId = '';
	else if (addChild)
		pageObj.parentId = pageObj.operNode.data.id;
	else {
		if (pageObj.operNode.data.parentId == pageObj.initOrganId)
			pageObj.parentId = '';
		else
			pageObj.parentId = pageObj.operNode.data.parentId;
	}
	pageObj.checkDataModified.initFileds();
	if (pageObj.jobGridManager)
		pageObj.jobGridManager.loadData();
	if (pageObj.empGridManager)
		pageObj.empGridManager.loadData();
	pageObj.jobGridDataLoaded = false;
	pageObj.empGridDataLoaded = false;
	pageObj.deleteEmpRelIds = ";";
	pageObj.afterSelectTabItem(pageObj.activeTabId);
}

//---------------------------------
//保存部门
//---------------------------------
pageObj.saveDepartment = function (){
	if ($("#txtName").val() == ''){
		DialogAlert('部门名称不能为空');
		return false;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Department";
	cmd.method = "updateDepartmentData";
	cmd.id = pageObj.id;
	cmd.parent_id = pageObj.parentId;
	cmd.organ_id = pageObj.initOrganId;
	cmd.no = $("#txtNo").val();
	cmd.name = $("#txtName").val();
	if ($("#chkIsValid").attr("checked")) {
		cmd.is_valid = 1;
	} else {
		cmd.is_valid = 0;
	}
	cmd.remark = $("#txtRemark").val();
	//下属岗位数据
	if (pageObj.jobGridManager != null) {
		//检查有无重名岗位
		var checkExistsName = ";";
		var gridData = pageObj.jobGridManager.getData();
		for(var i = 0; i < gridData.length; i++) {
			if (!gridData[i].name) {
				DialogAlert('必须输入岗位名称');
				return false;
			} else if (checkExistsName.indexOf(';' + gridData[i].name + ';') == -1) {
				checkExistsName += gridData[i].name + ';';
			} else {
				DialogAlert('同一部门下岗位名称 “' + gridData[i].name + '”不能重复');
				return false;
			}
		}
		cmd.jobUpdated = JSON.stringify(pageObj.jobGridManager.getUpdated());//encodeURIComponent
		cmd.jobAdded = JSON.stringify(pageObj.jobGridManager.getAdded());//encodeURIComponent
		cmd.jobDeleted = JSON.stringify(pageObj.jobGridManager.getDeleted());//encodeURIComponent
	}
	//下属人员数据
	if (pageObj.empGridManager != null) {
		//检查同一部门有无重名人员，每一个重名人员提示一次
		var checkExistsName = ";";
		var unpromptName = ";";
		var gridData = pageObj.empGridManager.getData();
		for(var i = 0; i < gridData.length; i++) {
			if (!gridData[i].name) {
				DialogAlert('必须输入人员名称');
				return false;
			} else if (checkExistsName.indexOf(';' + gridData[i].name + ';') == -1) {
				checkExistsName += gridData[i].name + ';';
			} else if (unpromptName.indexOf(';' + gridData[i].name + ';') == -1) {
				if (!confirm('同一部门下人员名称“' + gridData[i].name + '”重复，确认是同名人员？'))
					return false;
				else
					unpromptName += gridData[i].name + ';';
			}
		}
		cmd.empUpdated = JSON.stringify(pageObj.empGridManager.getUpdated());//encodeURIComponent
		cmd.empAdded = JSON.stringify(pageObj.empGridManager.getAdded());//encodeURIComponent
		cmd.empDeleted = JSON.stringify(pageObj.empGridManager.getDeleted());//encodeURIComponent
		cmd.deleteEmpRelIds = pageObj.deleteEmpRelIds;
	}
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			if (pageObj.operate == "addDepartment") {
				pageObj.id = data.id;
				pageObj.operate = "editDepartment";
				$("#btnAdd").show();
				$("#btnAddChild").show();
				$("#btnDelete").show();
				pageObj.addTreeNode($("#txtName").val(), data.id, pageObj.parentId);
			}
			else
				pageObj.updateTreeNode($("#txtName").val());
			pageObj.checkDataModified.initFileds();
			if (pageObj.jobGridManager)
				pageObj.getGridQueryParams(pageObj.jobGridManager, approot + '/data?module=sysman&service=Department&method=getDepartmentJobData&moduleId=' + pageObj.moduleId);
			if (pageObj.empGridManager)
				pageObj.getGridQueryParams(pageObj.empGridManager, approot + '/data?module=sysman&service=Department&method=getDepartmentEmpData&moduleId=' + pageObj.moduleId);
			DialogAlert('保存部门成功完成');
		} else {
			DialogError('保存部门失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

//---------------------------------
//删除部门
//---------------------------------
pageObj.deleteDepartment = function (){
	if (pageObj.id == '') { //正在新增
		if (pageObj.operNode == null)
			pageObj.newDepartment();
		else
			pageObj.editDepartmentData();
		return true;
	}
	if (pageObj.operNode == null){
		DialogWarn('请选择要删除的部门。');
		return false;
	}
	var message = null;
	if (pageObj.operNode.data.children) {
		if (pageObj.operNode.data.children.length > 0)
			message = '当前部门节点 ' + pageObj.operNode.data.text + ' 包含子节点，删除它将删除所有下级部门节点。继续删除？';
		else
			message = '当前部门节点 ' + pageObj.operNode.data.text + ' 可能包含仍未加载的子节点，删除它将删除所有下级部门节点。继续删除？';
	} else
		message = '删除当前部门节点 ' + pageObj.operNode.data.text + '？';
	DialogConfirm(message, function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "Department";
			cmd.method = "deleteDepartmentData";
			cmd.id = pageObj.operNode.data.id;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					pageObj.removeTreeNode();				
					DialogAlert('删除部门数据成功完成');
					pageObj.newDepartment();
				} else {
					DialogError('删除部门失败,错误原因：' + data.message);
					return false;
				}
			};
			cmd.execute();
		}
	});
}

//---------------------------------
//树数据初始化
//---------------------------------
pageObj.initTreeData = function() {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Department";
	cmd.method = "getDepartmentTreeData";
	cmd.dataSetNo = '';
	cmd.parentId = pageObj.initOrganId;
	cmd.recurse = 2;
	cmd.async = false;
	cmd.success = function(data) {
		if (data.data.length > 0){
			pageObj.initFirstNodeId = data.data[0].id;
		} else {
			pageObj.initFirstNodeId = '';
		}
		pageObj.itemsConvert(data);
		pageObj.treeManager = $("#departmentTree").ligerTree(data);
	};
	cmd.execute();
}

//---------------------------------
//转换用户类型
//---------------------------------
pageObj.userTypeRender = function (row, rowNamber){
	var userType = Number(row.userType).toFixed(0);
	if(userType == "1")
		return "竞买人";
	else if(userType == "2")
		return "银行工作人员";
	else
		return "内部人员";
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
//添加岗位事件
//---------------------------------
pageObj.addJob = function() {
	pageObj.jobGridManager.addRow({ 
			id: "",
			name: '<新增岗位>',
			no: "",
			isValid: 1
		});
}

//---------------------------------
//删除岗位事件
//---------------------------------
pageObj.deleteJob = function() {
	var selectedRow = pageObj.jobGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要删除的岗位。');
		return false;
	}
	DialogConfirm('在当前部门 ' + $("#txtName").val() + ' 下移除岗位 ' + selectedRow.name + '？', function (yes) {
		if (yes) {
			pageObj.jobGridManager.deleteSelectedRow();
		}
	});
}

//---------------------------------
//添加人员事件
//---------------------------------
pageObj.addEmp = function() {
	pageObj.empGridManager.addRow({ 
			id: "",
			name: '<新增人员>',
			no: "",
			gender: "男",
			tel: "",
			mobile: "",
			email: "",
			isValid: 1,
			relDepartments: "",
			relJobs: "",
			relRoles: ""
		});
}

pageObj.addEmpRel = function() {
	DialogOpen({width: 650, height: 420, title: '选择添加人员', url: approot + '/sysman/department.html?selectData=6&organId=' + pageObj.initOrganId
		+ "&u=" + pageObj.userId + "&height=345&width=620",
		buttons: [{ text: '确定', onclick: function (item, dialog) {
					var selectedDataFn = dialog.frame.pageObj.getCheckedEmpData || dialog.frame.window.pageObj.getCheckedEmpData;
					var selectedData = selectedDataFn();
					if (selectedData.length == 0) {
						DialogWarn('请选择要添加的人员。');
						return false;
					}
					for (var i = 0; i < selectedData.length; i++) {
						var blnExists = false;
						var id = selectedData[i].id;
						for (var row in pageObj.empGridManager.records) {
							if (pageObj.empGridManager.records[row].id == id) {
								blnExists = true;
								break;
							}
						}
						if (!blnExists) {
							pageObj.empGridManager.addRow({
								id: id,
								name: selectedData[i].name,
								no: selectedData[i].no,
								gender: selectedData[i].gender,
								tel: selectedData[i].tel,
								mobile: selectedData[i].mobile,
								email: selectedData[i].email,
								isValid: selectedData[i].isValid,
								relDepartments: selectedData[i].relDepartments,
								relJobs: selectedData[i].relJobs,
								relRoles: selectedData[i].relRoles
							});
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
//删除人员事件
//---------------------------------
pageObj.deleteEmp = function() {
	var selectedRow = pageObj.empGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要删除的人员。');
		return false;
	}
	DialogConfirm('在当前部门 ' + $("#txtName").val() + ' 下移除人员 ' + selectedRow.name + '，并且删除该人员自身资料？', function (yes) {
		if (yes) {
			pageObj.empGridManager.deleteSelectedRow();
		}
	});
}

pageObj.deleteEmpRel = function() {
	var selectedRow = pageObj.empGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要删除关联的人员。');
		return false;
	}
	DialogConfirm('在当前部门 ' + $("#txtName").val() + ' 下移除人员 ' + selectedRow.name + '(不会删除人员自身资料)？', function (yes) {
		if (yes) {
			var selectedRow = pageObj.empGridManager.getSelectedRow();
			if (pageObj.deleteEmpRelIds.indexOf(';' + selectedRow.id + ';') == -1) {
				pageObj.deleteEmpRelIds += selectedRow.id + ';';
			}
			pageObj.empGridManager.deleteSelectedRow();
		}
	});
}

//---------------------------------
//部门下属信息标签TAB切换前事件
//---------------------------------
pageObj.beforeSelectTabItem = function(tabid) {
}

//---------------------------------
//部门下属信息标签TAB切换后事件
//---------------------------------
pageObj.afterSelectTabItem = function(tabid) {
	if (tabid == 'tabitem1') {//岗位
 		if (pageObj.jobGridManager == null) {
 			var gridWidth = pageObj.initGridWidth > 0 ? pageObj.initGridWidth : 645;
			var gridHeight = pageObj.initGridHeight > 0 ? pageObj.initGridHeight : 253;
 			var toolbar = null; 
 			if (pageObj.selectDataMode == 0) {
				toolbar = { items: [
	                { text: '新增', id: 'btnAddJob', click: pageObj.addJob, icon: 'add' },
	                { line: true },
	                { text: '删除', id: 'btnDeleteJob', click: pageObj.deleteJob, icon: 'delete' }
                ]};
			}
	 		pageObj.jobGridManager = $("#jobGrid").ligerGrid({
				url: approot + '/data?module=sysman&service=Department&method=getDepartmentJobData&moduleId=' + pageObj.moduleId
					+ '&u=' + pageObj.userId
					+ '&id=' + pageObj.id,
				columns: [
					{ display: '名称', name: 'name', width :'30%', editor: { type: 'text' }},
					{ display: '编号', name: 'no', width :'19%', editor: { type: 'text' }},
					{ display: '所属角色', name: 'relRoles', width :'30%'},
					{ display: '有效', name: 'isValid', width :'18%', editor: { type: 'select', data: [{ value: 1, text: '有效' }, { value: 0, text: '无效'}], valueColumnName: 'value' },
                    	render: function (item) {
							if (parseInt(item.isValid) == 1)
								return '有效';
							return '无效';
						}
					}
				],
				enabledEdit: pageObj.selectDataMode == 0,
				checkbox: pageObj.selectDataMode == 4,
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
				height: gridHeight,
				toolbar: toolbar,
				onAfterEdit: pageObj.gridDataEdit
			});
		} else if (!pageObj.jobGridDataLoaded) {
			pageObj.getGridQueryParams(pageObj.jobGridManager, approot + '/data?module=sysman&service=Department&method=getDepartmentJobData&moduleId=' + pageObj.moduleId);
		}
		pageObj.jobGridDataLoaded = true;
 	} else if (tabid == 'tabitem2'){//人员
 		if (pageObj.empGridManager == null) {
 			var gridWidth = pageObj.initGridWidth > 0 ? pageObj.initGridWidth : 645;
			var gridHeight = pageObj.initGridHeight > 0 ? pageObj.initGridHeight : 253;
 			var toolbar = null;
 			if (pageObj.selectDataMode == 0) {
				toolbar = { items: [
	                { text: '新增', id: 'btnAddEmp', click: pageObj.addEmp, icon: 'add' },
	                { line: true },
	                { text: '选择添加', id: 'btnAddEmpRel', click: pageObj.addEmpRel, icon: 'add' },
	                { line: true },
	                { text: '删除', id: 'btnDeleteEmp', click: pageObj.deleteEmp, icon: 'delete' },
	                { line: true },
	                { text: '删除关联', id: 'btnDeleteEmpRel', click: pageObj.deleteEmpRel, icon: 'delete' }
                ]};
			}
	 		pageObj.empGridManager = $("#empGrid").ligerGrid({
				url: approot + '/data?module=sysman&service=Department&method=getDepartmentEmpData&moduleId=' + pageObj.moduleId
					+ '&u=' + pageObj.userId
					+ '&id=' + pageObj.id,
				columns: [
					{ display: '名称', name: 'name', editor: { type: 'text' }, width: 100},
					{ display: '编号', name: 'no', editor: { type: 'text' }, width: 60},
					{ display: '性别', name: 'gender', width: 50, editor: { type: 'select', data: [{ value: '男', text: '男' }, { value: '女', text: '女'}], valueColumnName: 'value' }},
					{ display: '电话', name: 'tel', editor: { type: 'text' }, width: 80},
					{ display: '手机', name: 'mobile', editor: { type: 'text' }, width: 80},
					{ display: '电子邮箱', name: 'email', editor: { type: 'text' }, width: 100},
					{ display: '有效', name: 'isValid', width: 80, editor: { type: 'select', data: [{ value: 1, text: '有效' }, { value: 0, text: '无效'}], valueColumnName: 'value' },
                    	render: function (item) {
							if (parseInt(item.isValid) == 1)
								return '有效';
							return '无效';
						}
					},
					{ display: '所属部门', name: 'relDepartments', width: 150},
					{ display: '所在岗位', name: 'relJobs', width: 150},
					{ display: '所属角色', name: 'relRoles', width: 150}
				],
				enabledEdit: pageObj.selectDataMode == 0,
				checkbox: pageObj.selectDataMode == 6,
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
				height: gridHeight,
				toolbar: toolbar,
				onAfterEdit: pageObj.gridDataEdit
			});
		} else if (!pageObj.empGridDataLoaded) {
			pageObj.getGridQueryParams(pageObj.empGridManager, approot + '/data?module=sysman&service=Department&method=getDepartmentEmpData&moduleId=' + pageObj.moduleId);
		}
		pageObj.empGridDataLoaded = true;
 	}
 	pageObj.activeTabId = tabid;
}

//---------------------------------
//选择数据页面时的选择方法
//---------------------------------
pageObj.getSelectedDepartmentData = function () {
    return pageObj.treeManager.getSelected();
}

pageObj.getCheckedDepartmentData = function () {
    return pageObj.treeManager.getChecked();
}

pageObj.getSelectedJobData = function () {
    return pageObj.jobGridManager.getSelectedRow();
}

pageObj.getCheckedJobData = function () {
    return pageObj.jobGridManager.getCheckedRows();
}

pageObj.getSelectedEmpData = function () {
    return pageObj.empGridManager.getSelectedRow();
}

pageObj.getCheckedEmpData = function () {
    return pageObj.empGridManager.getCheckedRows();
}

//---------------------------------
//部门下属信息标签TAB初始化
//---------------------------------
pageObj.initDepartmentRelTab = function() {
	pageObj.tabManager = $("#tabDepartmentRel").ligerTab({
		contextmenu: false,
		//height: 320,
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
	pageObj.userId = getUserId();
	pageObj.initWidth = parseInt(Utils.getPageValue("width"));
	pageObj.initWidth = isNaN(pageObj.initWidth) ? 0 : pageObj.initWidth;
	pageObj.initHeight = parseInt(Utils.getPageValue("height"));
	pageObj.initHeight = isNaN(pageObj.initHeight) ? 0 : pageObj.initHeight;
	pageObj.initGridWidth = parseInt(Utils.getPageValue("gridWidth"));
	pageObj.initGridWidth = isNaN(pageObj.initGridWidth) ? 0 : pageObj.initGridWidth;
	pageObj.initGridHeight = parseInt(Utils.getPageValue("gridHeight"));
	pageObj.initGridHeight = isNaN(pageObj.initGridHeight) ? 0 : pageObj.initGridHeight;
	pageObj.selectOrganData = Utils.getPageValue("selectOrganData") == '1';
	var selectDataParam = Utils.getPageValue("selectData");
	if (selectDataParam == "" || selectDataParam == null || selectDataParam.toLowerCase() == "null" || isNaN(selectDataParam)) {
		pageObj.selectDataMode = 0;
	}else {
		pageObj.selectDataMode = parseInt(selectDataParam);
		if (pageObj.selectDataMode < 0 || pageObj.selectDataMode > 6) {
			pageObj.selectDataMode = 0;
		}
	}
	pageObj.initTreeData();
	pageObj.activeTabId = "tabitem1";
	//根据pageObj.selectDataMode设置界面：0编辑模式，1单选部门，2多选部门，3单选岗位，4多选岗位，5单选人员，6多选人员
	if (pageObj.selectDataMode == 0) {
		$("#btnAdd").click(pageObj.newDepartment);
		$("#btnAddChild").click(pageObj.newChildDepartment);
		$("#btnDelete").click(pageObj.deleteDepartment);
		$("#btnSave").click(pageObj.saveDepartment);
	} else {
		$("#btnAdd").hide();
		$("#btnAddChild").hide();
		$("#btnDelete").hide();
		$("#btnSave").hide();
		if (pageObj.selectDataMode == 1 || pageObj.selectDataMode == 2) {
			$("#departmentTreeTitle").hide();
			$("#department").hide();
			$(".i-wrap").css("width", pageObj.initWidth - 20 + "px");
			$(".i-wrap").css("height", pageObj.initHeight - 35 + "px");
			$(".i-wrap").css("background", "white");
			$(".i-left_bar").css("border", "none");
			$(".i-left_bar").css("width", pageObj.initWidth - 20 + "px");
			$(".i-left_bar").css("height", pageObj.initHeight - 35 + "px");

		} else {
			$("#departmentTitle").hide();
			$("#departmentData").hide();
			$(".i-wrap").css("width", "100%");
			$(".i-wrap").css("background", "white");
			$("div .i-left_bar").css("height", pageObj.initHeight+ "px");
			$("div .i-tree_box").css("height", pageObj.initHeight+ "px");
			$("div .i-right_bar").css("height", pageObj.initHeight+ "px");
			
			//$(".i-right_bar").css("width", (pageObj.initWidth - 20) * 2 / 3 - 5 + "px");
			//$(".i-right_bar").css("height", pageObj.initHeight + "px");
			//$("#departmentTreeTitle").css("width", pageObj.initWidth + "px");
			//$("div .i-tree_box").css("width", pageObj.initWidth / 3 + "px");
			//$("div .i-tree_box").css("height", pageObj.initHeight+ "px");
			//$(".i-right_bar").css("height", pageObj.initHeight - 50 + "px");
			
			//alert("d");
			pageObj.initGridHeight = pageObj.initHeight - 35;
			pageObj.initGridWidth = pageObj.initWidth * 2 / 3 - 70;
			
			//$("#tabDepartmentRel").removeClass("mt10");
			//$("#tabDepartmentRel").addClass("mt30");
		}
	}
	if (pageObj.selectDataMode == 0 || pageObj.selectDataMode >= 3) {
		pageObj.initDepartmentRelTab();
	}
	if (pageObj.selectDataMode == 3 || pageObj.selectDataMode == 4) {
		//$("#tabEmp").hide();
		//$("li[tabid='tabitem2']").hide();
		pageObj.tabManager.removeTabItem("tabitem2");
		pageObj.activeTabId = "tabitem1";
	} else if (pageObj.selectDataMode == 5 || pageObj.selectDataMode == 6) {
		//$("#tabJob").hide();
		//$("li[tabid='tabitem1']").hide();
		pageObj.tabManager.removeTabItem("tabitem1");
		pageObj.activeTabId = "tabitem2";
	}
	pageObj.checkDataModified = new FieldModifiedChecker($("#departmentData")[0]);
	if (pageObj.selectDataMode != 1 && pageObj.selectDataMode != 2) {
		if (pageObj.initFirstNodeId == "") {
			if (pageObj.selectDataMode == 0) {
				pageObj.newDepartment();
			}
		} else {
			//选中首节点
	    	var selectNodeCallback = function (data)
			    {
			        return data.id == pageObj.initFirstNodeId;
			    };
		    pageObj.treeManager.selectNode(selectNodeCallback);
		    pageObj.operNode = pageObj.treeManager.getSelected();
		    pageObj.nodeSelect(pageObj.operNode);
		}
	}
//	if (pageObj.selectDataMode == 0 || pageObj.selectDataMode == 3 || pageObj.selectDataMode == 4) {
//		pageObj.afterSelectTabItem('tabitem1');
//	} else if (pageObj.selectDataMode == 5 || pageObj.selectDataMode == 6) {
//		pageObj.afterSelectTabItem('tabitem2');
//	}
	if (pageObj.tabManager)
		pageObj.tabManager.selectTabItem(pageObj.activeTabId);
}

$(document).ready(pageObj.initHtml);