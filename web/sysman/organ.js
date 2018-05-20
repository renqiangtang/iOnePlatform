//---------------------------------
//页页对象
//---------------------------------
var pageObj = {}
//---------------------------------
//管理器引用
//---------------------------------
pageObj.treeManager = null;
pageObj.tabManager = null;
//---------------------------------
//树当前操作节点引用
//---------------------------------
pageObj.operNode = null;
//---------------------------------
//TAB标签数据是否加载标识
//---------------------------------
pageObj.departmentDataLoaded = false;
pageObj.empDataLoaded = false;
pageObj.businessDataLoaded = false;
pageObj.bankDataLoaded = false;
pageObj.infoDataLoaded = false;
//---------------------------------
//初始化界面时首个节点ID
//---------------------------------
pageObj.initFirstNodeId = '';
//---------------------------------
//单位数据编辑模式，0仅能编辑当前人员所在单位，1可编辑所有单位数据
//---------------------------------
pageObj.editMode = 0;
//---------------------------------
//选择数据标识。0编辑模式，1单选单位，2多选单位，3单选部门，4多选部门，5单选岗位，6多选岗位
//---------------------------------
pageObj.selectDataMode = 0;
//---------------------------------
//行政区下拉框管理器引用
//---------------------------------
pageObj.cantonComboBoxManager = null;
pageObj.cantonTreeManager = null;
//---------------------------------
//初始高度
//---------------------------------
pageObj.initWidth = 0;
pageObj.initHeight = 0;
pageObj.checkDataModified = null;

//---------------------------------
//树展开前事件，异步加载下级节点
//---------------------------------
pageObj.nodeBeforeExpand = function (node)
{
	if (node.data.children && node.data.children.length == 0)
    {
        var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Organ";
		cmd.method = "getOrganTreeData";
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

pageObj.nodeBeforeSelect = function(node){
	if (pageObj.checkDataModified && pageObj.checkDataModified.checkModified(true) && (pageObj.operNode || pageObj.id == "")) {
		if (!confirm("数据已修改未保存，确认切换单位节点？\n点击确定按钮将放弃未保存数据"))
			return false;
	}
}

//---------------------------------
//树选择节点
//---------------------------------
pageObj.nodeSelect = function(node){
	if (node && pageObj.id != node.data.id) {
		pageObj.operNode = node;
		pageObj.id = pageObj.operNode.data.id;
		if (pageObj.selectDataMode == 0)
			pageObj.editOrganData();
		else {
			pageObj.departmentDataLoaded = false;
			pageObj.empDataLoaded = false;
			pageObj.businessDataLoaded = false;
			pageObj.bankDataLoaded = false;
			pageObj.infoDataLoaded = false;
			pageObj.afterSelectTabItem(pageObj.activeTabId);
		}
	}
}

//---------------------------------
//新增节点
//---------------------------------
pageObj.addTreeNode = function(text, id, parentId){
	var node = pageObj.operNode;
    var nodes = [];
  	nodes.push({ text: text, id: id, parentId: parentId, isexpand: false});
    if (node) {//有选择节点时新增
    	if (parentId == undefined || parentId == null || parentId.toLowerCase() == "null") {//一级子节点
    		pageObj.treeManager.append(null, nodes);
    	} else if (parentId == node.data.id) {//子节点
    		pageObj.treeManager.append(node.target, nodes);
    	} else {//兄弟节点
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
    if (pageObj.operNode && pageObj.operNode.data.id == id) {
    	$("#txtOrgan").val($("#txtName").val());
    	$("#txtOrganId").val(id);
    }
    pageObj.checkDataModified.initFileds();
}

//---------------------------------
//更新节点
//---------------------------------
pageObj.updateTreeNode = function(text){
	var node = pageObj.operNode;
    if (node) {
    	pageObj.treeManager.update(node.target, { text: text});
    	$("#txtOrgan").val(text);
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
        DialogError('请先选择单位节点');
}

//---------------------------------
//树节点转换方法，对后台返回的JSON生成自定义内容
//---------------------------------
pageObj.itemsConvert = function (items) {
	items.idFieldName = 'id';
	items.parentIDFieldName = 'parentId'; 
	items.nodeWidth = 200;
	items.checkbox = pageObj.selectDataMode == 2;
	if (pageObj.selectDataMode == 0 || pageObj.selectDataMode >= 3) {
		items.onBeforeExpand = this.nodeBeforeExpand;
		items.onBeforeSelect = pageObj.nodeBeforeSelect;
    	items.onSelect = pageObj.nodeSelect;
	}
}

//---------------------------------
//单位编辑数据初始化
//---------------------------------
pageObj.editOrganData = function () {
	pageObj.operate = "editOrgan";
	$("#btnAdd").show();
	$("#btnAddChild").show();
	$("#btnDelete").show();
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Organ";
	cmd.method = "getOrganData";
	cmd.dataSetNo = "";
	if (pageObj.operNode != undefined && pageObj.operNode != null)
		cmd.id = pageObj.operNode.data.id;
	else
		cmd.id = pageObj.id;
	cmd.async = false;
	cmd.success = function (data) {
		pageObj.parentId = Utils.getRecordValue(data, 0, 'parent_id');
		if (data && data.rs && data.rs.length > 0) {
			//避免可能延迟加载造成的树上实际无指定的节点
			$("#txtOrgan").val(Utils.getRecordValue(data, 0, 'name'));
			$("#txtOrganId").val(pageObj.id);
			if (pageObj.selectDataMode == 0) {
				$("#txtName").val(Utils.getRecordValue(data, 0, 'name'));
				$("#txtNo").val(Utils.getRecordValue(data, 0, 'no'));
				$("#txtAddress").val(Utils.getRecordValue(data, 0, 'address'));
				$("#txtPostCode").val(Utils.getRecordValue(data, 0, 'post_code'));
				$("#txtTel").val(Utils.getRecordValue(data, 0, 'tel'));
				$("#txtFax").val(Utils.getRecordValue(data, 0, 'fax'));
				$("#txtMobile").val(Utils.getRecordValue(data, 0, 'mobile'));
				$("#txtEmail").val(Utils.getRecordValue(data, 0, 'email'));
				$("#txtContact").val(Utils.getRecordValue(data, 0, 'contact'));
				$("#cboKind").val(Utils.getRecordValue(data, 0, 'kind'));
				$("#chkIsValid").attr("checked", Utils.getRecordValue(data, 0, 'is_valid') == "1");
				$('#chkIsTransOrgan').attr("checked",Utils.getRecordValue(data, 0, 'is_trans_organ') == "1");
				$("#txtParent").val(Utils.getRecordValue(data, 0, 'parent_name'));
				$("#txtRemark").val(Utils.getRecordValue(data, 0, 'remark'));
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
			}
			pageObj.departmentDataLoaded = false;
			pageObj.empDataLoaded = false;
			pageObj.businessDataLoaded = false;
			pageObj.bankDataLoaded = false;
			pageObj.infoDataLoaded = false;
			pageObj.afterSelectTabItem(pageObj.activeTabId);
			pageObj.checkDataModified.initFileds();
		}
	};
	cmd.execute();
}

//---------------------------------
//新增单位
//---------------------------------
pageObj.newOrgan = function (addChild){
	pageObj.operate = "addOrgan";
	$("#btnAdd").hide();
	$("#btnAddChild").hide();
	$("#btnDelete").hide();
	$("#txtName").val('');
	$("#txtNo").val('');
	$("#txtAddress").val('');
	$("#txtCanton").val('');
	$("#txtCantonId").val('');
	$("#txtPostCode").val('');
	$("#txtTel").val('');
	$("#txtFax").val('');
	$("#txtMobile").val('');
	$("#txtEmail").val('');
	$("#txtContact").val('');
	$("#cboKind").val('');
	$("#chkIsValid").attr("checked", true);
	$("#txtRemark").val('');
	if (addChild == undefined || !addChild) {//默认为兄弟单位
		if (pageObj.operNode == null) {
			if (pageObj.id == undefined || pageObj.id == null || pageObj.id == "") {
				pageObj.parentId = '';
			}
		} else {
			pageObj.parentId = pageObj.operNode.data.parentId;
		}
	} else {//新增子单位
		if (pageObj.operNode == null) {
			if (pageObj.id == undefined || pageObj.id == null || pageObj.id == "") {
				$("#txtParent").val('');
				pageObj.parentId = '';
			} else {
				$("#txtParent").val($("#txtOrgan").val());
				pageObj.parentId = pageObj.id;
			}
		} else {
			$("#txtParent").val($("#txtOrgan").val());
			pageObj.parentId = pageObj.id;
		}
	}
	pageObj.id = '';
	pageObj.departmentDataLoaded = false;
	pageObj.empDataLoaded = false;
	pageObj.businessDataLoaded = false;
	pageObj.bankDataLoaded = false;
	pageObj.infoDataLoaded = false;
	pageObj.afterSelectTabItem(pageObj.activeTabId);
}

//---------------------------------
//新增子单位单位
//---------------------------------
pageObj.addOrgan = function (){
	pageObj.newOrgan(false);
}

//---------------------------------
//新增子单位单位
//---------------------------------
pageObj.addChildOrgan = function (){
	pageObj.newOrgan(true);
}

//---------------------------------
//保存单位
//---------------------------------
pageObj.saveOrganData = function (){
	if ($("#txtName").val() == ''){
		DialogAlert('单位名称不能为空');
		return false;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Organ";
	cmd.method = "updateOrganData";
	cmd.id = pageObj.id;
	cmd.parent_id = pageObj.parentId;
	cmd.no = $("#txtNo").val();
	cmd.name = $("#txtName").val();
	cmd.address =  $("#txtAddress").val();
	cmd.canton_id = $("#txtCantonId").val();
	cmd.post_code = $("#txtPostCode").val();
	cmd.tel = $("#txtTel").val();
	cmd.fax = $("#txtFax").val();
	cmd.mobile = $("#txtMobile").val();
	cmd.email = $("#txtEmail").val();
	cmd.contact = $("#txtContact").val();
	cmd.kind = $("#cboKind").val();
	if ($("#chkIsValid").attr("checked")) {
		cmd.is_valid = 1;
	} else {
		cmd.is_valid = 0;
	}
	if ($("#chkIsTransOrgan").attr("checked")) {
		cmd.is_trans_organ = 1;
		cmd.status = 1;
	} else {
		cmd.is_trans_organ = 0;
	}
	cmd.parent_id = pageObj.parentId;
	cmd.remark = $("#txtRemark").val();
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			if (pageObj.operate == "addOrgan") {
				pageObj.id = data.id;
				pageObj.operate = "editOrgan";
				pageObj.addTreeNode($("#txtName").val(), data.id, pageObj.parentId);
			}
			else
				pageObj.updateTreeNode($("#txtName").val());
			$("#btnAdd").show();
			$("#btnAddChild").show();
			$("#btnDelete").show();
			pageObj.checkDataModified.initFileds();
			DialogAlert('保存单位成功完成');
		} else {
			DialogError('保存单位失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

//---------------------------------
//保存单位
//---------------------------------
pageObj.saveOrgan = function (){
	pageObj.saveOrganData(false);
}

//---------------------------------
//删除单位
//---------------------------------
pageObj.deleteOrgan = function (){
	if (pageObj.id == '') { //正在新增
		if (pageObj.operNode == null)
			pageObj.newOrgan();
		else
			pageObj.editOrganData();
		return true;
	}
	if (pageObj.id == undefined || pageObj.id == null || pageObj.id == ""){
		DialogWarn('请选择要删除的单位。');
		return false;
	}
	var strOrganName = $("#txtName").val();
	var message = null;
	if (pageObj.operNode != null) {
		if (pageObj.operNode.data.children) {
			if (pageObj.operNode.data.children.length > 0) {
				DialogAlert('当前单位节点 ' + strOrganName + ' 包含子单位，系统不允许级联删除。如必须删除请先删除子单位。');
				return;
			}
			else {
				DialogAlert('当前单位节点 ' + strOrganName + ' 可能包含仍未加载的子单位，系统不允许级联删除。如必须删除请先删除子单位。');
				return;
			}
				
		}
	}
	DialogConfirm('删除当前单位节点 ' + strOrganName + '？如果删除失败可能是当前单位包含人员、部门及岗位等信息，请先删除这些数据。', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "Organ";
			cmd.method = "deleteOrganData";
			cmd.id = pageObj.id;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					pageObj.removeTreeNode();
					DialogAlert('删除单位数据成功完成');
				} else {
					DialogError('删除单位失败,错误原因：' + data.message);
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
	cmd.service = "Organ";
	cmd.method = "getOrganTreeData";
	cmd.dataSetNo = '';
	cmd.parentId = '';
	cmd.recurse = 2;
	cmd.async = false;
	cmd.success = function(data) {
		if (data.data.length > 0){
			pageObj.initFirstNodeId = data.data[0].id;
		} else {
			pageObj.initFirstNodeId = '';
		}
		pageObj.itemsConvert(data);
		pageObj.treeManager = $("#organTree").ligerTree(data);
	};
	cmd.execute();
}

//---------------------------------
//选择数据页面时的选择方法
//---------------------------------
pageObj.getSelectedOrganData = function () {
    return pageObj.treeManager.getSelected();
}

pageObj.getCheckedOrganData = function () {
    return pageObj.treeManager.getChecked();
}

pageObj.getSelectedDepartmentData = function () {
	var departmentFrame = $("#departmentFrame")[0];
    var selectedDataFn = departmentFrame.contentWindow.pageObj.getSelectedDepartmentData;
	var selectedData = selectedDataFn();
	return selectedData;
}

pageObj.getCheckedDepartmentData = function () {
    var departmentFrame = $("#departmentFrame")[0];
    var selectedDataFn = departmentFrame.contentWindow.pageObj.getCheckedDepartmentData;
	var selectedData = selectedDataFn();
	return selectedData;
}

pageObj.getSelectedJobData = function () {
    var departmentFrame = $("#departmentFrame")[0];
    var selectedDataFn = departmentFrame.contentWindow.pageObj.getSelectedJobData;
	var selectedData = selectedDataFn();
	return selectedData;
}

pageObj.getCheckedJobData = function () {
    var departmentFrame = $("#departmentFrame")[0];
    var selectedDataFn = departmentFrame.contentWindow.pageObj.getCheckedJobData;
	var selectedData = selectedDataFn();
	return selectedData;
}

//---------------------------------
//标签TAB切换前事件
//---------------------------------
pageObj.beforeSelectTabItem = function(tabid) {
}

//---------------------------------
//标签TAB切换后事件
//---------------------------------
pageObj.afterSelectTabItem = function(tabid) {
	pageObj.activeTabId = tabid;
	if (tabid == 'tabitem1') {//单位信息
		
	} else if (tabid == 'tabitem2') {//部门
		if (!pageObj.departmentDataLoaded) {
			var initWidth = pageObj.initWidth > 0 ? pageObj.initWidth : 645;
			var initHeight = pageObj.initHeight > 0 ? pageObj.initHeight : 500;
 			var url = approot + '/sysman/department.html?organId=' + pageObj.id;
 			if (pageObj.selectDataMode == 0) {//编辑单位
 				var initGridHeight = initHeight > 0 ? initHeight - 500 + 120 : 120;
 				url += "&gridHeight=" + initGridHeight;
 			} else {
 				if (pageObj.selectDataMode >= 3) {//选择岗位、部门
 					var initDepartHeight = pageObj.selectDataMode == 3 || pageObj.selectDataMode == 4 ? initHeight - 40 : initHeight;
	 				url += '&selectData=' + (pageObj.selectDataMode - 2)
	 					+ '&selectOrganData=1&width=' + initWidth + '&height=' + initDepartHeight;
	 				if (pageObj.selectDataMode == 3 || pageObj.selectDataMode == 4)
	 					initHeight -= 90;
 				}
 			}
			var departmentFrame = $('#departmentFrame');
			if (departmentFrame.length == 0) {
				$('#departmentPanel').html('<iframe id="departmentFrame" src="' + url + '"></iframe>');
			} else {
				departmentFrame.attr('src', url);
			}
			pageObj.departmentDataLoaded = true;
		}
	} else if (tabid == 'tabitem3') {//人员
		if (!pageObj.empDataLoaded) {
 			var url = approot + '/sysman/empList.html?organId=' + pageObj.id;
			var empFrame = $('#empFrame');
			if (empFrame.length == 0) {
				$('#empPanel').html('<iframe id="empFrame" scrolling="no" src="' + url + '"></iframe>');
			} else {
				empFrame.attr('src', url);
			}
			pageObj.empDataLoaded = true;
		}
	} else if (tabid == 'tabitem4') {//业务资格
		if (!pageObj.businessDataLoaded) {
 			var url = approot + '/sysman/organQualification.html?organId=' + pageObj.id;
			var businessFrame = $('#businessFrame');
			if (businessFrame.length == 0) {
				$('#businessPanel').html('<iframe id="businessFrame"  scrolling="no" src="' + url + '"></iframe>');
			} else {
				businessFrame.attr('src', url);
			}
			pageObj.businessDataLoaded = true;
		}
	}else if (tabid == 'tabitem5') {//银行
		if (!pageObj.bankDataLoaded) {
 			var url = approot + '/trademan/organBankAccountList.html?organId=' + pageObj.id;
			var bankFrame = $('#bankFrame');
			if (bankFrame.length == 0) {
				$('#bankPanel').html('<iframe id="bankFrame" scrolling="no" src="' + url + '"></iframe>');
			} else {
				bankFrame.attr('src', url);
			}
			pageObj.bankDataLoaded = true;
		}
	} else if (tabid == 'tabitem6') {//系统信息
		if (!pageObj.infoDataLoaded) {
 			var url = approot + '/sysman/organInfo.html?organId=' + pageObj.id;
			var infoFrame = $('#infoFrame');
			if (infoFrame.length == 0) {
				$('#infoPanel').html('<iframe id="infoFrame" scrolling="no" src="' + url + '"></iframe>');
			} else {
				infoFrame.attr('src', url);
			}
			pageObj.infoDataLoaded = true;
		}
 	}
}

//---------------------------------
//标签TAB初始化
//---------------------------------
pageObj.initOrganTab = function() {
	pageObj.tabManager = $("#tabOrganInfo").ligerTab({
		contextmenu: false,
		onBeforeSelectTabItem: pageObj.beforeSelectTabItem,
	    onAfterSelectTabItem: pageObj.afterSelectTabItem
     });
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
	//items.nodeWidth = 200;
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
//页面初始化
//---------------------------------
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getUrlParamValue(document.location.href, "moduleId");
	pageObj.userId = getUserId();
	pageObj.initWidth = parseInt(Utils.getUrlParamValue(document.location.href, "width"));
	pageObj.initWidth = isNaN(pageObj.initWidth) ? 0 : pageObj.initWidth;
	pageObj.initHeight = parseInt(Utils.getUrlParamValue(document.location.href, "height"));
	pageObj.initHeight = isNaN(pageObj.initHeight) ? 0 : pageObj.initHeight;
	var editModeParam = Utils.getUrlParamValue(document.location.href, "editMode");
	if (editModeParam != undefined && editModeParam != null && editModeParam != "" && !isNaN(editModeParam)) {
		pageObj.editMode = parseInt(editModeParam);
		if (pageObj.editMode < 0 || pageObj.editMode > 1)
			pageObj.editMode = 0;
	}
	var selectDataParam = Utils.getUrlParamValue(document.location.href, "selectData");
	if (selectDataParam == "" || selectDataParam == null || selectDataParam.toLowerCase() == "null" || isNaN(selectDataParam)) {
		pageObj.selectDataMode = 0;
	}else {
		pageObj.selectDataMode = parseInt(selectDataParam);
		if (pageObj.selectDataMode < 0 || pageObj.selectDataMode > 6) {
			pageObj.selectDataMode = 0;
		}
	}
	if (pageObj.selectDataMode == 0) {
		$("#btnAdd input").click(pageObj.addOrgan);
		$("#btnAddChild input").click(pageObj.addChildOrgan);
		$("#btnDelete input").click(pageObj.deleteOrgan);
		$("#btnSave input").click(pageObj.saveOrgan);
	}
	if (pageObj.selectDataMode == 0 || pageObj.selectDataMode >= 3) {
		pageObj.cantonComboBoxManager = $("#txtCanton").ligerComboBox({valueField: "id", valueFieldID: "txtCantonId", treeLeafOnly: false, isShowCheckBox: true, width: 363 });
		pageObj.initCantonComboBox();
	}
	pageObj.initTreeData();
	pageObj.initOrganTab();
	if (pageObj.selectDataMode == 1 || pageObj.selectDataMode == 2) {
		$("#organTreeTitle").hide();
		$("#organ").hide();
		$(".i-wrap").css("width", pageObj.initWidth - 20 + "px");
		$(".i-wrap").css("height", pageObj.initHeight - 35 + "px");
		$(".i-wrap").css("background", "white");
		$(".i-left_bar").css("border", "none");
		$(".i-left_bar").css("width", pageObj.initWidth - 20 + "px");
		$(".i-left_bar").css("height", pageObj.initHeight - 35 + "px");
	} else if (pageObj.selectDataMode >= 3) {
		pageObj.tabManager.removeTabItem("tabitem1");
		pageObj.tabManager.removeTabItem("tabitem3");
		pageObj.tabManager.removeTabItem("tabitem4");
		pageObj.tabManager.removeTabItem("tabitem5");
		pageObj.tabManager.removeTabItem("tabitem6");
		pageObj.activeTabId = "tabitem2";
	} else {
		pageObj.activeTabId = "tabitem1";
	}
	pageObj.tabManager.selectTabItem(pageObj.activeTabId);
	if (pageObj.selectDataMode == 0 || pageObj.selectDataMode >= 3) {
		var defaultNodeId = null;
		//选中首节点
		if (pageObj.initFirstNodeId != "") {
			defaultNodeId = pageObj.initFirstNodeId;
		}
		var selectNodeCallback = function (data)
		{
			return data.id == defaultNodeId;
		};
//		pageObj.treeManager.selectNode(selectNodeCallback);
		pageObj.operNode = pageObj.treeManager.getSelected();
		if (pageObj.operNode != undefined && pageObj.operNode != null) {
			pageObj.nodeSelect(pageObj.operNode);
		}
	    if (pageObj.editMode == 0) {
			$("#btnAdd").hide();
			$("#btnAddChild").hide();
			$("#btnDelete").hide();
		}
	}
	if (pageObj.selectDataMode == 0)
		pageObj.checkDataModified = new FieldModifiedChecker($("#organEditor")[0]);
}

$(document).ready(pageObj.initHtml);