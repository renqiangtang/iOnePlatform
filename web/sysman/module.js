//---------------------------------
//页页对象
//---------------------------------
var pageModuleObj = {}
//---------------------------------
//树管理器引用
//---------------------------------
pageModuleObj.treeManager = null;
//---------------------------------
//菜单下拉框管理器引用
//---------------------------------
pageModuleObj.menuComboBoxManager = null;
pageModuleObj.menuTreeManager = null;
//---------------------------------
//树当前操作节点引用
//---------------------------------
pageModuleObj.operNode = null;
//---------------------------------
//自定义节点图标
//---------------------------------
pageModuleObj.dataSetIcon = approot + '/base/skins/default/images/icons/database.gif';
pageModuleObj.resourceIcon = approot + '/base/skins/default/images/icons/resource.gif';
//---------------------------------
//树敌数据内容。0包含模块及下属所有数据，1仅包含模块，2模块数据集，3模块字符串
//---------------------------------
pageModuleObj.treeDataType = 0;
pageModuleObj.treeDataSetNo = "queryModuleAllFlatTree";
//---------------------------------
//初始化界面时首个节点ID
//---------------------------------
pageModuleObj.initFirstNodeId = '';
//---------------------------------
//检查数据是否修改
//---------------------------------
pageModuleObj.checkDataModified = null;
//---------------------------------
//选择数据标问标识。0编辑模式，1单选模块，2多选模块，3单选数据集，4多选数据集，5单选字符串，6多选字符串
//---------------------------------
pageModuleObj.selectDataMode = 0;

//---------------------------------
//树展开前事件，异步加载下级节点
//---------------------------------
pageModuleObj.nodeBeforeExpand = function (node)
{
	if (node.data.children && node.data.children.length == 0)
    {
        var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Module";
		cmd.method = "getModuleTreeData";
		cmd.dataSetNo = pageModuleObj.treeDataSetNo;
		cmd.parentId = node.data.id;
		cmd.recurse = 2;
		cmd.success = function(data) {
			if (data.data.length > 0) {
				pageModuleObj.itemsConvert(data);
				pageModuleObj.treeManager.append(node.target, data.data);
			}
		};
		cmd.execute();
    }
}

//---------------------------------
//树展开事件
//---------------------------------
pageModuleObj.nodeExpand = function (node)
{
	//alert('nodeExpand');
}

//---------------------------------
//树选择节点前
//---------------------------------
pageModuleObj.nodeBeforeSelect = function(node){
	if (pageModuleObj.selectDataMode == 0 && pageModuleObj.checkDataModified
		&& pageModuleObj.checkDataModified.checkModified(true) && (pageModuleObj.operNode || pageModuleObj.id == "")) {
		if (!confirm("数据已修改未保存，确认切换模块节点？\n点击确定按钮将放弃未保存数据"))
			return false;
	}
}

//---------------------------------
//树选择节点
//---------------------------------
pageModuleObj.nodeSelect = function(node){
	if (node && pageModuleObj.id != node.data.id) {
		pageModuleObj.operNode = node;
		if (pageModuleObj.selectDataMode == 0) {
			if (node.data.dataType == "1") {
				pageModuleObj.editDataSetData();
			} else if (node.data.dataType == "2") {
				pageModuleObj.editResourceData();
			} else {
				pageModuleObj.editModuleData();
			}
		}
	}
}

pageModuleObj.test = function(){
	//alert(pageModuleObj.operNode.data.text + " = " + pageModuleObj.operNode.data.dataType);
	//return;
	var node = pageModuleObj.operNode;
	var g=pageModuleObj.treeManager;
	var p=g.getParent(node.target);
	var data=g._getDataNodeByTreeDataIndex(g.data,p);
	alert(data.text);
}

//---------------------------------
//新增节点
//---------------------------------
pageModuleObj.addTreeNode = function(text, id, parentId, dataType){
	var node = pageModuleObj.operNode;
    var nodes = [];
    if (dataType == "1") {
    	nodes.push({ text: text, id: id, parentId: parentId, isexpand: false, dataType: dataType, icon: pageModuleObj.dataSetIcon});
    } else if (dataType == "2") {
    	nodes.push({ text: text, id: id, parentId: parentId, isexpand: false, dataType: dataType, icon: pageModuleObj.resourceIcon});
    } else {
    	nodes.push({ text: text, id: id, parentId: parentId, isexpand: false, dataType: dataType});
    }
    if (node) {
    	if (parentId == undefined || parentId == null || parentId.toLowerCase() == "null") {//一级子节点
    		pageModuleObj.treeManager.append(null, nodes);
    	} else if (node.data.dataType == "1" || node.data.dataType == "2") {//添加数据集、字符串资源节点则添加为当前节点平级节点
    		var parentNodeTarget = pageModuleObj.treeManager.getParentTreeItem(node.target);
    		if (parentNodeTarget) {
    			pageModuleObj.treeManager.append(parentNodeTarget, nodes);
    		}
    	} else {
    		pageModuleObj.treeManager.append(node.target, nodes);
    	}
    	
    } else {//未选择任何节点时新增
    	pageModuleObj.treeManager.append(null, nodes);
    }
    //选中新加的节点
	var selectNodeCallback = function (data)
	    {
	        return data.id == id;
	    };
    pageModuleObj.treeManager.selectNode(selectNodeCallback);
    pageModuleObj.operNode = pageModuleObj.treeManager.getSelected();
}

//---------------------------------
//更新节点
//---------------------------------
pageModuleObj.updateTreeNode = function(text){
	var node = pageModuleObj.operNode;
    if (node)
    	pageModuleObj.treeManager.update(node.target, { text: text});
}

//---------------------------------
//树删除节点
//---------------------------------
pageModuleObj.removeTreeNode = function(){
	var node = pageModuleObj.operNode;
    if (node)
        pageModuleObj.treeManager.remove(node.target);
    else
        DialogError('请先选择需要删除的节点');
}

//---------------------------------
//树节点转换方法，对后台返回的JSON生成自定义内容
//---------------------------------
pageModuleObj.itemsConvert = function (items) {
	items.idFieldName = 'id';
    items.parentIDFieldName = 'parentId'; 
    items.nodeWidth = 200;
    //items.checkbox = false;
    items.checkbox = pageModuleObj.selectDataMode == 2 || pageModuleObj.selectDataMode == 4 || pageModuleObj.selectDataMode == 6;
    //items.nodeDraggable = true;
    items.onBeforeExpand = this.nodeBeforeExpand;
    items.onExpand = this.nodeExpand;
    items.onBeforeSelect = pageModuleObj.nodeBeforeSelect;
    items.onSelect = pageModuleObj.nodeSelect;
    for(var i = 0; i < items.data.length; i++)
		itemConvert(items.data[i]);
}

function itemConvert(item){
	if (item.dataType == "1") {
		item.icon = pageModuleObj.dataSetIcon;
	} else if (item.dataType == "2") {
		item.icon = pageModuleObj.resourceIcon;
	} else if (item.children) {
		for(var i = 0; i < item.children.length; i++)
			itemConvert(item.children[i]);
	}
}

//---------------------------------
//树数据初始化
//---------------------------------
pageModuleObj.initTreeData = function() {
	$("#moduleTree").html("");
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Module";
	cmd.method = "getModuleTreeData";
	cmd.dataSetNo = pageModuleObj.treeDataSetNo;
	if($("#searchID").val()!=""){
		cmd.searchNO=$("#searchID").val();
	}
	cmd.parentId = '';
	cmd.recurse = 2;
	cmd.async = false;
	cmd.success = function(data) {
		if (data.data.length > 0){
			pageModuleObj.initFirstNodeId = data.data[0].id;
		} else {
			pageModuleObj.initFirstNodeId = '';
		}
		pageModuleObj.itemsConvert(data);
		$("#moduleTree").ligerTree(data);
        pageModuleObj.treeManager = $("#moduleTree").ligerGetTreeManager();
	};
	cmd.execute();
}

//---------------------------------
//菜单树展开节点前
//---------------------------------
pageModuleObj.menuNodeBeforeExpand = function (node)
{
	if (node.data.children && node.data.children.length == 0)
    {
        var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Menu";
		cmd.method = "getMenuTreeData";
		cmd.dataSetNo = 'queryMenuFlatTree';
		cmd.parentId = node.data.id;
		cmd.recurse = 2;
		cmd.async = false;
		cmd.success = function(data) {
			if (data.menu.length > 0) {
				pageModuleObj.menuItemsConvert(data);
				var menuId = $("#txtMenuId").val();
				var menu = $("#txtMenu").val();
				pageModuleObj.menuTreeManager.append(node.target, data.data);
				if (pageModuleObj.menuTreeManager.getSelected() == null) {
					$("#txtMenuId").val(menuId);
					$("#txtMenu").val(menu);
				}
			}
		};
		cmd.execute();
    }
}

pageModuleObj.menuNodeExpand = function (node)
{
	//alert('nodeExpand');
}

//---------------------------------
//菜单树节点转换
//---------------------------------
pageModuleObj.menuItemsConvert = function (items) {
	items.idFieldName = 'id';
	items.parentIDFieldName = 'parentId'; 
	items.nodeWidth = 200;
	items.checkbox = false;
	items.onBeforeExpand = pageModuleObj.menuNodeBeforeExpand;
	items.onExpand = pageModuleObj.menuNodeExpand;
	items.data = items.menu;
	delete items.menu;
}

//---------------------------------
//菜单树下拉框初始化
//---------------------------------
pageModuleObj.initMenuComboBox = function () {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Menu";
	cmd.method = "getMenuTreeData";
	cmd.dataSetNo = "queryMenuFlatTree";
	cmd.parentId = '';
	cmd.recurse = 2;
	cmd.async = false;
	cmd.isexpand = 'false';
	cmd.success = function(data) {
		pageModuleObj.menuItemsConvert(data);
		pageModuleObj.menuComboBoxManager.setTree(data);
		pageModuleObj.menuTreeManager = pageModuleObj.menuComboBoxManager.treeManager;
	};
	cmd.execute();
}

//---------------------------------
//模块编辑数据初始化
//---------------------------------
pageModuleObj.editModuleData = function () {
	pageModuleObj.operate = "editModule";
	$("#editModule").show();
	$("#editDataSet").hide();
	$("#editResource").hide();
	$("#btnAdd").show();
	$("#btnAddDataSet").show();
	$("#btnAddResource").show();
	$("#btnDelete").show();
	$("#editTitle").html("编辑模块");
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Module";
	cmd.method = "getModuleData";
	cmd.dataSetNo = "";
	cmd.id = pageModuleObj.operNode.data.id;
	cmd.success = function (data) {
		pageModuleObj.id = Utils.getRecordValue(data, 0, 'id');
		pageModuleObj.parentId = Utils.getRecordValue(data, 0, 'parent_id');
		if (data && data.rs && data.rs.length > 0) {
			$("#txtName").val(Utils.getRecordValue(data, 0, 'name'));
			$("#txtNo").val(Utils.getRecordValue(data, 0, 'no'));
			$("#txtUrl").val(Utils.getRecordValue(data, 0, 'class_name'));
			$("#txtCreateUserName").val(Utils.getRecordValue(data, 0, 'create_user_id'));
			$("#txtCreateDate").val(Utils.getRecordValue(data, 0, 'create_date'));
			$("#txtRemark").val(Utils.getRecordValue(data, 0, 'remark'));
			var menuId = Utils.getRecordValue(data, 0, 'menu_id');
			if (menuId == null) {
				pageModuleObj.menuComboBoxManager.selectValueByTree("");
				$("#txtMenu").val("");
				$("#txtMenuId").val("");
			}
			else {
				pageModuleObj.menuComboBoxManager.selectValueByTree(menuId);
				$("#txtMenu").val(Utils.getRecordValue(data, 0, 'menu_name'));
    			$("#txtMenuId").val(menuId);
			}
		}
		pageModuleObj.checkDataModified.initFileds();
	};
	cmd.execute();
}

//---------------------------------
//新增模块
//---------------------------------
pageModuleObj.newModule = function (){
	pageModuleObj.operate = "addModule";
	$("#editModule").show();
	$("#editDataSet").hide();
	$("#editResource").hide();
	$("#btnAdd").hide();
	$("#btnAddDataSet").hide();
	$("#btnAddResource").hide();
	$("#btnDelete").hide();
	$("#editTitle").html("新增模块");
	$("#txtName").val('');
	$("#txtNo").val('');
	$("#txtUrl").val('');
	$("#txtMenu").val("");
	$("#txtMenuId").val("");
	$("#txtCreateUserName").val('');
	$("#txtCreateDate").val('');
	$("#txtRemark").val('');
	pageModuleObj.id = '';
	if (pageModuleObj.operNode == null)
		pageModuleObj.parentId = '';
	else
		pageModuleObj.parentId = pageModuleObj.operNode.data.id;
	pageModuleObj.checkDataModified.initFileds();
}

//---------------------------------
//保存模块
//---------------------------------
pageModuleObj.saveModule = function (){
	if ($("#txtName").val() == ''){
		DialogAlert('模块名称不能为空');
		return false;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Module";
	cmd.method = "updateModuleData";
	cmd.id = pageModuleObj.id;
	cmd.parent_id = pageModuleObj.parentId;
	cmd.no = $("#txtNo").val();
	cmd.name = $("#txtName").val();
	cmd.class_name = $("#txtUrl").val();//encodeURIComponent
	cmd.menu_id = $("#txtMenuId").val();
	cmd.remark = $("#txtRemark").val();
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			if (pageModuleObj.operate == "addModule") {
				pageModuleObj.id = data.id;
				pageModuleObj.operate = "editModule";
				$("#btnAdd").show();
				$("#btnAddDataSet").show();
				$("#btnAddResource").show();
				$("#btnDelete").show();
				$("#editTitle").html("编辑模块");
				pageModuleObj.addTreeNode($("#txtName").val(), data.id, pageModuleObj.parentId, 0);
			}
			else
				pageModuleObj.updateTreeNode($("#txtName").val());
			pageModuleObj.checkDataModified.initFileds();
			DialogAlert('保存模块成功完成');
		} else {
			DialogError('保存模块失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

//---------------------------------
//删除模块
//---------------------------------
pageModuleObj.deleteModule = function (){
	if (pageModuleObj.id == '') { //正在新增
		if (pageModuleObj.operNode == null)
			pageModuleObj.newModule();
		else
			pageModuleObj.editModuleData();
		return true;
	}
	if (pageModuleObj.operNode == null){
		DialogWarn('请选择要删除的模块。');
		return false;
	}
	var message = null;
	if (pageModuleObj.operNode.data.children) {
		if (pageModuleObj.operNode.data.children.length > 0)
			message = '当前模块节点 ' + pageModuleObj.operNode.data.text + ' 包含子节点，删除它将删除所有下级模块节点。继续删除？';
		else
			message = '当前模块节点 ' + pageModuleObj.operNode.data.text + ' 可能包含仍未加载的子节点，删除它将删除所有下级模块节点。继续删除？';
	} else
		message = '删除当前模块节点 ' + pageModuleObj.operNode.data.text + '？';
	DialogConfirm(message, function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "Module";
			cmd.method = "deleteModuleData";
			cmd.id = pageModuleObj.operNode.data.id;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					pageModuleObj.removeTreeNode();
					DialogAlert('删除模块数据成功完成');
				} else {
					DialogError('删除模块失败,错误原因：' + data.message);
					return false;
				}
			};
			cmd.execute();
		}
	});
}

//---------------------------------
//数据集编辑数据初始化
//---------------------------------
pageModuleObj.editDataSetData = function () {
	pageModuleObj.operate = "editDataSet";
	$("#editModule").hide();
	$("#editDataSet").show();
	$("#editResource").hide();
	$("#btnAdd").hide();
	$("#btnAddDataSet").show();
	$("#btnAddResource").hide();
	$("#btnDelete").show();
	$("#editTitle").html("编辑数据集");
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Module";
	cmd.method = "getDataSetData";
	cmd.dataSetNo = "";
	cmd.id = pageModuleObj.operNode.data.id;
	cmd.success = function (data) {
		if (data && data.rs && data.rs.length > 0) {
			$("#txtDataSetNo").val(Utils.getRecordValue(data, 0, 'no'));
			$("#txtDataSetName").val(Utils.getRecordValue(data, 0, 'name'));
			$("#txtDataSetSql").val(Utils.getRecordValue(data, 0, 'sql'));
			$("#txtDataSetCreateUserName").val(Utils.getRecordValue(data, 0, 'create_user_id'));
			$("#txtDataSetCreateDate").val(Utils.getRecordValue(data, 0, 'create_date'));
			$("#txtDataSetRemark").val(Utils.getRecordValue(data, 0, 'remark'));
		}
		pageModuleObj.id = Utils.getRecordValue(data, 0, 'id');
		pageModuleObj.parentId = Utils.getRecordValue(data, 0, 'module_id');
		pageModuleObj.checkDataModified.initFileds();
	};
	cmd.execute();
}

//---------------------------------
//新增数据集
//---------------------------------
pageModuleObj.newDataSet = function (){
	pageModuleObj.operate = "addDataSet";
	$("#editModule").hide();
	$("#editDataSet").show();
	$("#editResource").hide();
	$("#btnAdd").hide();
	$("#btnAddDataSet").hide();
	$("#btnAddResource").hide();
	$("#btnDelete").hide();
	$("#editTitle").html("新增数据集");
	$("#txtDataSetNo").val('');
	$("#txtDataSetName").val('');
	$("#txtDataSetSql").val('');
	$("#txtDataSetCreateUserName").val('');
	$("#txtDataSetCreateDate").val('');
	$("#txtDataSetRemark").val('');
	pageModuleObj.id = '';
	if (pageModuleObj.operNode == null)
		return false;
	if (pageModuleObj.operNode.data.dataType == "0")
		pageModuleObj.parentId = pageModuleObj.operNode.data.id;
	else
		pageModuleObj.parentId = pageModuleObj.operNode.data.parentId;
	pageModuleObj.checkDataModified.initFileds();
}

//---------------------------------
//保存数据集
//---------------------------------
pageModuleObj.saveDataSet = function (){
	if ($("#txtDataSetNo").val() == ''){
		DialogAlert('数据集编号不能为空');
		return false;
	}
	if ($("#txtDataSetName").val() == ''){
		DialogAlert('数据集名称不能为空');
		return false;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Module";
	cmd.method = "updateDataSetData";
	cmd.id = pageModuleObj.id;
	cmd.module_id = pageModuleObj.parentId;
	cmd.no = $("#txtDataSetNo").val();
	cmd.name = $("#txtDataSetName").val();
	cmd.sql = $("#txtDataSetSql").val();//encodeURIComponent
	cmd.remark = $("#txtDataSetRemark").val();
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			if (pageModuleObj.operate == "addDataSet") {
				pageModuleObj.id = data.id;
				pageModuleObj.operate = "editDataSet";
				$("#btnAdd").hide();
				$("#btnAddDataSet").show();
				$("#btnAddResource").hide();
				$("#btnDelete").show();
				$("#editTitle").html("编辑数据集");
				pageModuleObj.addTreeNode($("#txtDataSetName").val(), data.id, pageModuleObj.parentId, 1);
			}
			else
				pageModuleObj.updateTreeNode($("#txtDataSetName").val());
			pageModuleObj.checkDataModified.initFileds();
			DialogAlert('保存数据集成功完成');
		} else {
			DialogError('保存数据集失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

//---------------------------------
//删除数据集
//---------------------------------
pageModuleObj.deleteDataSet = function (){
	if (pageModuleObj.id == '') { //正在新增
		if (pageModuleObj.operNode == null)
			pageModuleObj.newModule();
		else
			pageModuleObj.editModuleData();
		return true;
	}
	if (pageModuleObj.operNode == null){
		DialogWarn('请选择要删除的数据集。');
		return false;
	}
	DialogConfirm('删除当前数据集节点 ' + pageModuleObj.operNode.data.text + '？', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "Module";
			cmd.method = "deleteDataSetData";
			cmd.id = pageModuleObj.operNode.data.id;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					pageModuleObj.removeTreeNode();
					DialogAlert('删除数据集数据成功完成');
				} else {
					DialogError('删除数据集失败,错误原因：' + data.message);
					return false;
				}
			};
			cmd.execute();
		}
	});
}

//---------------------------------
//字符串资源编辑数据初始化
//---------------------------------
pageModuleObj.editResourceData = function () {
	pageModuleObj.operate = "editResource";
	$("#editModule").hide();
	$("#editDataSet").hide();
	$("#editResource").show();
	$("#btnAdd").hide();
	$("#btnAddDataSet").hide();
	$("#btnAddResource").show();
	$("#btnDelete").show();
	$("#editTitle").html("编辑字符串");
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Module";
	cmd.method = "getResourceData";
	cmd.dataSetNo = "";
	cmd.id = pageModuleObj.operNode.data.id;
	cmd.success = function (data) {
		if (data && data.rs && data.rs.length > 0) {
			$("#txtResourceNo").val(Utils.getRecordValue(data, 0, 'no'));
			$("#txtResourceName").val(Utils.getRecordValue(data, 0, 'name'));
			$("#txtResourceContent").val(Utils.getRecordValue(data, 0, 'content'));
			$("#txtResourceCreateUserName").val(Utils.getRecordValue(data, 0, 'create_user_id'));
			$("#txtResourceCreateDate").val(Utils.getRecordValue(data, 0, 'create_date'));
			$("#txtResourceRemark").val(Utils.getRecordValue(data, 0, 'remark'));
		}
		pageModuleObj.id = Utils.getRecordValue(data, 0, 'id');
		pageModuleObj.parentId = Utils.getRecordValue(data, 0, 'module_id');
		pageModuleObj.checkDataModified.initFileds();
	};
	cmd.execute();
}

//---------------------------------
//新增字符串资源
//---------------------------------
pageModuleObj.newResource = function (){
	pageModuleObj.operate = "addResource";
	$("#editModule").hide();
	$("#editDataSet").hide();
	$("#editResource").show();
	$("#btnAdd").hide();
	$("#btnAddDataSet").hide();
	$("#btnAddResource").hide();
	$("#btnDelete").hide();
	$("#editTitle").html("新增字符串");
	$("#txtResourceNo").val('');
	$("#txtResourceName").val('');
	$("#txtResourceContent").val('');
	$("#txtResourceCreateUserName").val('');
	$("#txtResourceCreateDate").val('');
	$("#txtResourceRemark").val('');
	pageModuleObj.id = '';
	if (pageModuleObj.operNode == null)
		return false;
	if (pageModuleObj.operNode.data.dataType == "0")
		pageModuleObj.parentId = pageModuleObj.operNode.data.id;
	else
		pageModuleObj.parentId = pageModuleObj.operNode.data.parentId;
	pageModuleObj.checkDataModified.initFileds();
}

//---------------------------------
//保存字符串资源
//---------------------------------
pageModuleObj.saveResource = function (){
	if ($("#txtResourceNo").val() == ''){
		DialogAlert('字符串编号不能为空');
		return false;
	}
	if ($("#txtResourceName").val() == ''){
		DialogAlert('字符串名称不能为空');
		return false;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Module";
	cmd.method = "updateResourceData";
	cmd.id = pageModuleObj.id;
	cmd.module_id = pageModuleObj.parentId;
	cmd.no = $("#txtResourceNo").val();
	cmd.name = $("#txtResourceName").val();
	cmd.content = $("#txtResourceContent").val();//encodeURIComponent
	cmd.remark = $("#txtResourceRemark").val();
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			if (pageModuleObj.operate == "addResource") {
				pageModuleObj.id = data.id;
				pageModuleObj.operate = "editResource";
				$("#btnAdd").hide();
				$("#btnAddDataSet").hide();
				$("#btnAddResource").show();
				$("#btnDelete").show();
				$("#editTitle").html("编辑字符串");
				pageModuleObj.addTreeNode($("#txtResourceName").val(), data.id, pageModuleObj.parentId, 2);
			}
			else
				pageModuleObj.updateTreeNode($("#txtResourceName").val());
			pageModuleObj.checkDataModified.initFileds();
			DialogAlert('保存字符串成功完成');
		} else {
			DialogError('保存字符串失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

//---------------------------------
//删除字符串资源
//---------------------------------
pageModuleObj.deleteResource = function (){
	if (pageModuleObj.id == '') { //正在新增
		if (pageModuleObj.operNode == null)
			pageModuleObj.newModule();
		else
			pageModuleObj.editModuleData();
		return true;
	}
	if (pageModuleObj.operNode == null){
		DialogWarn('请选择要删除的字符串。');
		return false;
	}
	DialogConfirm('删除当前字符串节点 ' + pageModuleObj.operNode.data.text + '？', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "Module";
			cmd.method = "deleteResourceData";
			cmd.id = pageModuleObj.operNode.data.id;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					pageModuleObj.removeTreeNode();
					DialogAlert('删除字符串数据成功完成');
				} else {
					DialogError('删除字符串失败,错误原因：' + data.message);
					return false;
				}
			};
			cmd.execute();
		}
	});
}

//---------------------------------
//保存数据事件
//---------------------------------
pageModuleObj.saveData = function (){
	if (pageModuleObj.operate == "addDataSet" || pageModuleObj.operate == "editDataSet") {
		pageModuleObj.saveDataSet();
	} else if (pageModuleObj.operate == "addResource" || pageModuleObj.operate == "editResource") {
		pageModuleObj.saveResource();
	} else {
		pageModuleObj.saveModule();
	}
}

//---------------------------------
//删除数据事件
//---------------------------------
pageModuleObj.deleteData = function (){
	if (pageModuleObj.id == '') { //正在新增
		if (pageModuleObj.operNode == null)
			pageModuleObj.newModule();
		else if (pageModuleObj.operNode.data.dataType == "1") {
			pageModuleObj.editDataSetData();
		} else if (pageModuleObj.operNode.data.dataType == "2") {
			pageModuleObj.editResourceData();
		} else {
			pageModuleObj.editModuleData();
		}
		return true;
	}
	if (pageModuleObj.operNode == null){
		DialogWarn('请选择要删除的节点。');
		return false;
	}
	if (pageModuleObj.operNode.data.dataType == "1") {
		pageModuleObj.deleteDataSet();
	} else if (pageModuleObj.operNode.data.dataType == "2") {
		pageModuleObj.deleteResource();
	} else {
		pageModuleObj.deleteModule();
	}
}

//---------------------------------
//维护数据和同步数据操作切换
//---------------------------------
pageModuleObj.changeDataOperate = function (){
	if ($("#btnChangeDataOperate").data("dataMode") == 1) {
		$("#btnChangeDataOperate").data("dataMode", 0);
		$("#editTitle").html($("#btnChangeDataOperate").data("editTitle"));
		$("#btnChangeDataOperate1").val("同步");
		$("#dataEditor").show();
		$("#dataSync").hide();
	} else {
		$("#btnChangeDataOperate").data("dataMode", 1);
		$("#btnChangeDataOperate").data("editTitle", $("#editTitle").html());
		$("#editTitle").html("同步数据");
		$("#btnChangeDataOperate1").val("编辑");
		$("#dataEditor").hide();
		$("#dataSync").show();
	}
}

//---------------------------------
//检查同步数据输入
//---------------------------------
pageModuleObj.checkSyncDataOption = function (downloadSyncData){
	if (!downloadSyncData)
		if ($("#txtSyncRemoteUrl").val() == "") {
			DialogWarn('未输入远程系统的地址。');
			return false;
		}
	if (!($("#chkSyncArea0").attr("checked") || $("#chkSyncArea1").attr("checked") || $("#chkSyncArea2").attr("checked"))) {
		DialogWarn('至少需要选择一类同步范围。');
		return false;
	}
	if ($("#rbtnSyncDataArea1").attr("checked")) {
		if (pageModuleObj.operNode == null) {
			DialogWarn('未选择需要同步的节点。');
			return false;
		}
		if ($("#chkSyncArea0").attr("checked")) {
			if (pageModuleObj.operNode.data.dataType != "0") {
				DialogWarn('请选择模块节点。');
				return false;
			}
		} else {
			if (pageModuleObj.operNode.data.dataType == "1" && !$("#chkSyncArea1").attr("checked")) {
				DialogWarn('请选择数据集节点或者模块节点。');
				return false;
			} else if (pageModuleObj.operNode.data.dataType == "2" && !$("#chkSyncArea2").attr("checked")) {
				DialogWarn('请选择字符串节点或者模块节点。');
				return false;
			}
		}
	}
	return true;
}

//---------------------------------
//同步数据
//---------------------------------
pageModuleObj.syncRemoteData = function (){
	if (!pageModuleObj.checkSyncDataOption())
		return false;
	DialogConfirm("按选择的数据范围同步数据到远程服务器吗？<br/><span style='color:red; font-weight:700'>同步的数据内容为系统核心的模块、数据集和字符串资源，请谨慎检查来源和目的的正确性后继续。</span>", function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "ExchangeData";
			cmd.method = "syncRemoteData";
			cmd.syncRemoteUrl = $("#txtSyncRemoteUrl").val();//encodeURIComponent
			if ($("#rbtnSyncDataArea1").attr("checked")) {
				if (pageModuleObj.operNode.data.dataType == "1")
					cmd.syncDataSetId = pageModuleObj.operNode.data.id;
				else if (pageModuleObj.operNode.data.dataType == "2")
					cmd.syncResourceId = pageModuleObj.operNode.data.id;
				else
					cmd.syncModuleId = pageModuleObj.operNode.data.id;
			}
			cmd.syncModuleData = $("#chkSyncArea0").attr("checked") ? 1 : 0;
			cmd.syncDataSetData = $("#chkSyncArea1").attr("checked") ? 1 : 0;
			cmd.syncResourceData = $("#chkSyncArea2").attr("checked") ? 1 : 0;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					DialogAlert('远程系统同步数据成功完成');
				} else {
					DialogError('远程系统同步数据失败,错误原因：' + data.message);
					return false;
				}
			};
			cmd.execute();
		}
	});
}

//---------------------------------
//下载同步数据
//---------------------------------
pageModuleObj.downloadSyncData = function (){
	if (!pageModuleObj.checkSyncDataOption(true))
		return false;
	var cmd = new Command();
	cmd.stype = "/download";
	cmd.module = "sysman";
	cmd.service = "ExchangeData";
	cmd.method = "downloadSyncData";
	if ($("#rbtnSyncDataArea1").attr("checked")) {
		if (pageModuleObj.operNode.data.dataType == "1")
			cmd.syncDataSetId = pageModuleObj.operNode.data.id;
		else if (pageModuleObj.operNode.data.dataType == "2")
			cmd.syncResourceId = pageModuleObj.operNode.data.id;
		else
			cmd.syncModuleId = pageModuleObj.operNode.data.id;
	}
	cmd.syncModuleData = $("#chkSyncArea0").attr("checked") ? 1 : 0;
	cmd.syncDataSetData = $("#chkSyncArea1").attr("checked") ? 1 : 0;
	cmd.syncResourceData = $("#chkSyncArea2").attr("checked") ? 1 : 0;
	cmd.success = function (data) {
	};
	cmd.execute();
}

pageModuleObj.uploadSyncData = function() {
	var form = $("#fileForm")[0];
	if(form.txtUploadFile.value == ''){
		DialogError('请选择需要上传的同步数据文件');
		return false;
	}
	DialogConfirm("确认上传选择的同步数据文件到当前系统？<br/><span style='color:red; font-weight:700'>同步的数据内容为系统核心的模块、数据集和字符串资源，请谨慎检查来源和目的的正确性后继续。</span>", function (yes) {
		if (yes) {
			form.action = approot + "/upload?module=sysman&service=ExchangeData&method=uploadSyncDataFile";
			form.method = "post";
			form.target = "hidden_frame";
			form.submit();
		}
	});
}

//---------------------------------
//选择数据页面时的选择方法
//---------------------------------
pageModuleObj.getSelectedData = function () {
	var selectedData = pageModuleObj.treeManager.getSelected();
	if ((pageModuleObj.selectDataMode == 1 || pageModuleObj.selectDataMode == 2)
		&& (selectedData.dataType == "1" || selectedData.dataType == "2")) {
		DialogError('请选择模块节点数据');
		return null;
	} else if ((pageModuleObj.selectDataMode == 3 || pageModuleObj.selectDataMode == 4)
		&& selectedData.dataType != "1") {
		DialogError('请选择数据集节点数据');
		return null;
	} else if ((pageModuleObj.selectDataMode == 5 || pageModuleObj.selectDataMode == 6)
		&& selectedData.dataType != "2") {
		DialogError('请选择字符串资源节点数据');
		return null;
	}
    return selectedData;
}

pageModuleObj.getCheckedData = function () {
	var selectedData = pageModuleObj.treeManager.getChecked();
	for (var i = 0; i < selectedData.length; i++) {
		if ((pageModuleObj.selectDataMode == 1 || pageModuleObj.selectDataMode == 2)
			&& (selectedData[i].dataType == "1" || selectedData[i].dataType == "2")) {
			DialogError('请选择模块节点数据');
			return null;
		} else if ((pageModuleObj.selectDataMode == 3 || pageModuleObj.selectDataMode == 4)
			&& selectedData[i].dataType != "1") {
			DialogError('请选择数据集节点数据');
			return null;
		} else if ((pageModuleObj.selectDataMode == 5 || pageModuleObj.selectDataMode == 6)
			&& selectedData[i].dataType != "2") {
			DialogError('请选择字符串资源节点数据');
			return null;
		}
	}
    return selectedData;
}

//---------------------------------
//页面初始化
//---------------------------------
pageModuleObj.initHtml = function() {
	$("#searchID").change(pageModuleObj.initTreeData);
	//alert(getLabelByKey("test001"));
	var treeDataType = Utils.getUrlParamValue(document.location.href, "treeDataType");
	if (treeDataType != undefined && !isNaN(treeDataType)) {
		pageModuleObj.treeDataType = parseInt(treeDataType);
		if (pageModuleObj.treeDataType < 0 || pageModuleObj.treeDataType > 3)
			pageModuleObj.treeDataType = 0;
	}
	//检查是否选择数据页面
	var selectDataParam = Utils.getUrlParamValue(document.location.href, "selectData");
	if (selectDataParam == "" || selectDataParam == null || selectDataParam.toLowerCase() == "null" || isNaN(selectDataParam)) {
		pageModuleObj.selectDataMode = 0;
	}else {
		pageModuleObj.selectDataMode = parseInt(selectDataParam);
		if (pageModuleObj.selectDataMode < 0 || pageModuleObj.selectDataMode > 6) {
			pageModuleObj.selectDataMode = 0;
		}
	}
	if (pageModuleObj.selectDataMode == 0) {
		$("#dataPanel").show();
		$("#treeTitle").show();
		$("#btnAdd").click(pageModuleObj.newModule);
		$("#btnAddDataSet").click(pageModuleObj.newDataSet);
		$("#btnAddResource").click(pageModuleObj.newResource);
		$("#btnDelete").click(pageModuleObj.deleteData);
		$("#btnSave").click(pageModuleObj.saveData);
		$("#btnChangeDataOperate").click(pageModuleObj.changeDataOperate);
		//$("#btnTest").click(pageModuleObj.test);
		$("#btnTest").hide();
		$("#editDataSet").hide();
		$("#editResource").hide();
		$("#chkSyncArea1").attr("checked", true);
		$("#rbtnSyncDataArea1").attr("checked", true);
		$("#btnChangeDataOperate").data("dataMode", 0);
		$("#btnSyncRemote").click(pageModuleObj.syncRemoteData);
		$("#btnDownload").click(pageModuleObj.downloadSyncData);
		$("#btnUploadSyncData").click(pageModuleObj.uploadSyncData);
		pageModuleObj.checkDataModified = new FieldModifiedChecker($("#dataEditorForm")[0]);
	} else {
		$("#dataPanel").hide();
		$("#treeTitle").hide();
		if (pageModuleObj.selectDataMode == 1 ||  pageModuleObj.selectDataMode == 2)
			pageModuleObj.treeDataType = 1;
		else if (pageModuleObj.selectDataMode == 3 ||  pageModuleObj.selectDataMode == 4)
			pageModuleObj.treeDataType = 2;
		else
			pageModuleObj.treeDataType = 3;
	}
	switch (pageModuleObj.treeDataType) {
		case 1:
			pageModuleObj.treeDataSetNo = "queryModuleFlatTree";
			break;
		case 2:
			pageModuleObj.treeDataSetNo = "queryModuleDataSetFlatTree";
			break;
		case 3:
			pageModuleObj.treeDataSetNo = "queryModuleResourceFlatTree";
			break;
		default:
			pageModuleObj.treeDataSetNo = "queryModuleAllFlatTree";
	}
	pageModuleObj.initTreeData();
	if (pageModuleObj.selectDataMode == 0) {
		pageModuleObj.menuComboBoxManager = $("#txtMenu").ligerComboBox({valueField: "id", valueFieldID: "txtMenuId", treeLeafOnly: false, isShowCheckBox: true,width:364,selectBoxWidth:364 });
		pageModuleObj.initMenuComboBox();
	}
	if (pageModuleObj.initFirstNodeId == "")
		pageModuleObj.newModule();
	else {
		//选中首节点
    	var selectNodeCallback = function (data)
		    {
		        return data.id == pageModuleObj.initFirstNodeId;
		    };
	    pageModuleObj.treeManager.selectNode(selectNodeCallback);
	    pageModuleObj.operNode = pageModuleObj.treeManager.getSelected();
	    pageModuleObj.nodeBeforeSelect(pageModuleObj.operNode);
	}
}

function formCallback(data){
	alert(data.message);
}

$(document).ready(pageModuleObj.initHtml);