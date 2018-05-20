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
//初始化界面时首个节点ID
//---------------------------------
pageObj.initFirstNodeId = '';
//---------------------------------
//检查数据是否修改
//---------------------------------
pageObj.checkDataModified = null;
pageObj.templetDtlGridManager = null;
pageObj.templetDtlGridDataLoaded = false;
pageObj.deleteTempletDtlIds = null;
pageObj.templetDtlDataModified = false;

pageObj.userInfo = getUserInfoObj();

//---------------------------------
//树展开前事件，异步加载下级节点
//---------------------------------
pageObj.nodeBeforeExpand = function (node)
{
	if (node.data.children && node.data.children.length == 0)
    {
        var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Attach";
		cmd.method = "getTempletTreeData";
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

//---------------------------------
//树选择节点前
//---------------------------------
pageObj.nodeBeforeSelect = function(node){
	if ((pageObj.checkDataModified && pageObj.checkDataModified.checkModified(true) && (pageObj.operNode || pageObj.id == ""))
		|| !pageObj.templetDtlGridManager || pageObj.templetDtlGridManager.isDataChanged || pageObj.templetDtlDataModified) {
		if (!confirm("数据已修改未保存，确认切换附件模板节点？\n点击确定按钮将放弃未保存数据"))
			return false;
	}
}

//---------------------------------
//树选择节点
//---------------------------------
pageObj.nodeSelect = function(node){
	if (node && pageObj.id != node.data.id) {
		pageObj.operNode = node;
		pageObj.editTempletData();
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
    	if (!parentId) {
    		pageObj.treeManager.append(null, nodes);
    	} else {
    		var parentNodeTarget = pageObj.treeManager.getParentTreeItem(node.target);
    		if (parentId && parentId != node.data.id && parentNodeTarget)
				pageObj.treeManager.append(parentNodeTarget, nodes);
			else 
    			pageObj.treeManager.append(node.target, nodes);
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
    if (node)
    	pageObj.treeManager.update(node.target, { text: text});
}

//---------------------------------
//树删除节点
//---------------------------------
pageObj.removeTreeNode = function(){
	var node = pageObj.operNode;
    if (node)
        pageObj.treeManager.remove(node.target);
    else
        DialogError('请先选择附件模板节点');
}

//---------------------------------
//树节点转换方法，对后台返回的JSON生成自定义内容
//---------------------------------
pageObj.itemsConvert = function (items) {
	items.idFieldName = 'id';
    items.parentIDFieldName = 'parentId'; 
    items.nodeWidth = 200;
    items.checkbox = false;
    items.onBeforeExpand = pageObj.nodeBeforeExpand;
    items.onExpand = pageObj.nodeExpand;
    items.onBeforeSelect = pageObj.nodeBeforeSelect;
    items.onSelect = pageObj.nodeSelect;
}

pageObj.checkValidityOnlyInteger = function () {
	if(((event.keyCode >= 48 && event.keyCode <= 57) || event.keyCode == 8 || (event.keyCode >= 96 && event.keyCode <= 105)
		|| event.keyCode == 46 || event.keyCode == 37 || event.keyCode == 39 || event.keyCode == 110) && !event.shiftKey) {
		event.returnValue = true;
	} else {
		event.returnValue = false;
	}
}

//---------------------------------
//附件模板编辑数据初始化
//---------------------------------
pageObj.editTempletData = function () {
	pageObj.operate = "editTemplet";
	$("#btnAdd").show();
	$("#btnAddChild").show();
	$("#btnDelete").show();
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Attach";
	cmd.method = "getTempletData";
	cmd.dataSetNo = "";
	cmd.id = pageObj.operNode.data.id;
	cmd.success = function (data) {
		pageObj.id = Utils.getRecordValue(data, 0, 'id');
		pageObj.parentId = Utils.getRecordValue(data, 0, 'parent_id');
		if (data && data.rs && data.rs.length > 0) {
			$("#txtName").val(Utils.getRecordValue(data, 0, 'name'));
			$("#txtNo").val(Utils.getRecordValue(data, 0, 'no'));
			$("#txtRemark").val(Utils.getRecordValue(data, 0, 'remark'));
			pageObj.templetDtlGridDataLoaded = false;
			pageObj.deleteTempletDtlIds = null;
			pageObj.templetDtlDataModified = false;
			pageObj.afterSelectTabItem("tabTempletDtl");
		}
		pageObj.checkDataModified.initFileds();
	};
	cmd.execute();
}

//---------------------------------
//新增附件模板
//---------------------------------
pageObj.newTemplet = function (addChild) {
	pageObj.operate = "addTemplet";
	$("#btnAdd").hide();
	$("#btnAddChild").hide();
	$("#btnDelete").hide();
	$("#txtName").val('');
	$("#txtNo").val('');
	$("#txtRemark").val('');
	if (addChild == true) {//新增子模板
		if (!pageObj.operNode && !pageObj.id)
			pageObj.parentId = '';
		else
			pageObj.parentId = pageObj.id;
	} else {//新增兄弟模板
		if (pageObj.operNode && pageObj.operNode.data)
			pageObj.parentId = pageObj.operNode.data.parentId;
		else
			pageObj.parentId = '';
	}
	pageObj.id = '';
	pageObj.checkDataModified.initFileds();
	pageObj.templetDtlGridDataLoaded = false;
	pageObj.deleteTempletDtlIds = null;
	pageObj.templetDtlDataModified = false;
	pageObj.afterSelectTabItem(pageObj.activeTabId);
}

//---------------------------------
//新增兄弟模板
//---------------------------------
pageObj.addTemplet = function (){
	pageObj.newTemplet(false);
}

//---------------------------------
//新增子模板
//---------------------------------
pageObj.addChildTemplet = function (){
	pageObj.newTemplet(true);
}

//---------------------------------
//保存附件模板
//---------------------------------
pageObj.saveData = function (){
	if (!$("#txtName").val()){
		DialogAlert('附件模板名称不能为空');
		return false;
	}
	if (!$("#txtNo").val()){
		DialogAlert('附件模板编号不能为空，此编号将由代码中引用');
		return false;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Attach";
	cmd.method = "updateTempletData";
	cmd.id = pageObj.id;
	cmd.parent_id = pageObj.parentId;
	cmd.no = $("#txtNo").val();
	cmd.name = $("#txtName").val();
	cmd.remark = $("#txtRemark").val();
	if (pageObj.templetDtlGridDataLoaded && pageObj.templetDtlGridManager) {
		if (pageObj.deleteTempletDtlIds)
			cmd.deleteTempletDtlIds = pageObj.deleteTempletDtlIds;
		if (pageObj.templetDtlGridManager.isDataChanged || pageObj.templetDtlDataModified) {
			var templetDtls = new Array();
			var gridData = pageObj.templetDtlGridManager.getData();
			for(var i = 0; i < gridData.length; i++) {
				var row = gridData[i];
				var newTurn = i;
				if (row.__status == "update" || row.__status == "add"
					|| row.turn != newTurn) {
					row.turn = newTurn;
					templetDtls.push(row);
				}
			}
			if (templetDtls.length > 0)
				cmd.templetDtls = JSON.stringify(templetDtls);
		}
	}
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			if (pageObj.operate == "addTemplet") {
				pageObj.id = data.id;
				pageObj.operate = "editTemplet";
				$("#btnAdd").show();
				$("#btnAddChild").show();
				$("#btnDelete").show();
				pageObj.addTreeNode($("#txtName").val(), data.id, pageObj.parentId);
			}
			else
				pageObj.updateTreeNode($("#txtName").val());
			pageObj.checkDataModified.initFileds();
			pageObj.templetDtlGridDataLoaded = false;
			pageObj.deleteTempletDtlIds = null;
			pageObj.templetDtlDataModified = false;
			pageObj.afterSelectTabItem("tabTempletDtl");
			DialogAlert('保存附件模板成功完成');
		} else {
			DialogError('保存附件模板失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

//---------------------------------
//删除附件模板
//---------------------------------
pageObj.deleteTemplet = function (){
	if (pageObj.id == '') { //正在新增
		if (pageObj.operNode == null)
			pageObj.newTemplet();
		else
			pageObj.editTempletData();
		return true;
	}
	if (pageObj.operNode == null){
		DialogAlert('请选择要删除的附件模板。');
		return false;
	}
	var message = null;
	if (pageObj.operNode.data.children) {
		if (pageObj.operNode.data.children.length > 0)
			message = '当前附件模板节点 ' + pageObj.operNode.data.text + ' 包含子节点，删除它将删除所有下级附件模板节点。继续删除？';
		else
			message = '当前附件模板节点 ' + pageObj.operNode.data.text + ' 可能包含仍未加载的子节点，删除它将删除所有下级附件模板节点。继续删除？';
	} else
		message = '删除当前附件模板节点 ' + pageObj.operNode.data.text + '？';
	DialogConfirm(message, function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "Attach";
			cmd.method = "deleteTempletData";
			cmd.id = pageObj.operNode.data.id;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					pageObj.removeTreeNode();
					DialogAlert('删除附件模板数据成功完成');
				} else {
					DialogError('删除附件模板失败,错误原因：' + data.message);
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
	cmd.service = "Attach";
	cmd.method = "getTempletTreeData";
	if (pageObj.initParentId)
		cmd.parentId = pageObj.initParentId;
	else
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
		$("#templetTree").ligerTree(data);
        pageObj.treeManager = $("#templetTree").ligerGetTreeManager();
	};
	cmd.execute();
}

pageObj.afterSelectTabItem = function(tabid) {
	if (tabid == 'tabTempletDtl') {
 		if (!pageObj.templetDtlGridManager) {
	 		pageObj.templetDtlGridManager = $("#templetDtlGrid").ligerGrid({
				url: approot + '/data?module=sysman&service=Attach&method=getTempletDtlData&moduleId=' + pageObj.moduleId
					+ '&u=' + pageObj.userInfo.userId + '&templetId=' + pageObj.id,
				columns: [
					{ display: '名称', name: 'name', width: 120, render: pageObj.templetDtlNameRender},
					{ display: '编号', name: 'no', width: 80},
					{ display: '介质', name: 'media', width: 80},
					{ display: '必须', name: 'is_required', width: 80, render: pageObj.templetDtlStateRender},
					{ display: '公开', name: 'is_publish', width: 80, render: pageObj.templetDtlStateRender},
					{ display: '归档', name: 'is_archive', width: 100, render: pageObj.templetDtlStateRender},
					{ display: '限制附件数', name: 'dtl_count', width: 80},
					{ display: '限制文件数', name: 'dtl_file_count', width: 80},
					{ display: '限制文件类型', name: 'file_type', width: 100},
					{ display: '备注', name: 'remark', width: 100}
				],
				usePager: false,
				isScroll: true,
				rownumbers : true,
				showTitle: false,
				fixedCellHeight: true,
				rownumbers: true,
				rowDraggable: true,
//				width: '99.8%',
				height: 300,
				toolbar: { items: [
	                { text: '新增', id: 'btnAddTempletDtl', click: pageObj.addTempletDtl, icon: 'add' },
	                { line: true },
	                { text: '编辑', id: 'btnEditTempletDtl', click: pageObj.editTempletDtl },
	                { line: true },
	                { text: '删除', id: 'btnDeleteTempletDtl', click: pageObj.deleteTempletDtl, icon: 'delete' },
	                { line: true },
	                { text: '上移', id: 'btnUpTempletDtl', click: pageObj.upTempletDtl, icon: 'up' },
	                { text: '下移', id: 'btnDownTempletDtl', click: pageObj.downTempletDtl }
                ]},
                onRowDragDrop: pageObj.templetDtlGridDrag
			});
		} else if (!pageObj.templetDtlGridDataLoaded) {
			pageObj.templetDtlGridManager.refresh(approot + '/data?module=sysman&service=Attach&method=getTempletDtlData&moduleId=' + pageObj.moduleId
				+ '&u=' + pageObj.userInfo.userId + '&templetId=' + pageObj.id, {});
		}
		pageObj.templetDtlGridDataLoaded = true;
 	}
 	pageObj.activeTabId = tabid;
}

pageObj.templetDtlNameRender = function (rowdata, rowindex, value){
	return "<a href='javascript:pageObj.editTempletDtl()'>" + value + "</a>";
}

pageObj.templetDtlStateRender = function (rowdata, rowindex, value){
	if (value == "1")
		return "√";
	else
		return "";
}

//---------------------------------
//添加明细
//---------------------------------
pageObj.addTempletDtl = function() {
	$("#txtTempletDtlName").val("");
	$("#txtTempletDtlNo").val("");
	$("#cboTempletDtlMedia").val("");
	$("#chkTempletDtlIsRequired").val("");
	$("#chkTempletDtlIsPublish").val("");
	$("#chkTempletDtlIsArchive").val("");
	$("#txtTempletDtlDtlCount").val("");
	$("#txtTempletDtlDtlFileCount").val("");
	$("#txtTempletDtlFileType").val("");
	$("#txtTempletDtlFileSize").val("0");
	$("#cboFileSizeUnit").val("KB");
	$("#txtTempletDtlFileDesc").val("");
	$("#txtTempletDtlRemark").val("");
	$.ligerDialog.open({ width: 600, title: '新增附件', target: $("#pnlTempletDtlEditor"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				if (!pageObj.saveTempletDtl(true))
					return false;
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	});
}

//---------------------------------
//编辑明细
//---------------------------------
pageObj.editTempletDtl = function() {
	var selectedRow = pageObj.templetDtlGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要编辑的附件。');
		return false;
	}
	$("#txtTempletDtlName").val(selectedRow.name);
	$("#txtTempletDtlNo").val(selectedRow.no);
	$("#cboTempletDtlMedia").val(selectedRow.media);
	$("#chkTempletDtlIsRequired").attr("checked", selectedRow.is_required == 1);
	$("#chkTempletDtlIsPublish").attr("checked", selectedRow.is_publish == 1);
	$("#chkTempletDtlIsArchive").attr("checked", selectedRow.is_archive == 1);
	$("#txtTempletDtlDtlCount").val(selectedRow.dtl_count);
	$("#txtTempletDtlDtlFileCount").val(selectedRow.dtl_file_count);
	$("#txtTempletDtlFileType").val(selectedRow.file_type);
	var fileSize = selectedRow.file_size;
	fileSize = parseInt(fileSize);
	if (isNaN(fileSize) || fileSize <= 0) {
		$("#txtTempletDtlFileSize").val(0);
		$("#cboFileSizeUnit").val("KB");
	} else {
		var decimal = 2;
		if (fileSize >= 1024 * 1024 * 1024) {
			$("#txtTempletDtlFileSize").val(parseFloat((fileSize / (1024 * 1024 * 1024)).toFixed(decimal)));
			$("#cboFileSizeUnit").val("TB");
		} else if (fileSize >= 1024 * 1024) {
			$("#txtTempletDtlFileSize").val(parseFloat((fileSize / (1024 * 1024)).toFixed(decimal)));
			$("#cboFileSizeUnit").val("GB");
		} else if (fileSize >= 1024) {
			$("#txtTempletDtlFileSize").val(parseFloat((fileSize / 1024).toFixed(decimal)));
			$("#cboFileSizeUnit").val("MB");
		} else {
			$("#txtTempletDtlFileSize").val(parseFloat(fileSize));
			$("#cboFileSizeUnit").val("KB");
		}
	}
	$("#txtTempletDtlFileDesc").val(selectedRow.file_desc);
	$("#txtTempletDtlRemark").val(selectedRow.remark);
	$.ligerDialog.open({ width: 600, title: '编辑附件', target: $("#pnlTempletDtlEditor"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				if (!pageObj.saveTempletDtl(false))
					return false;
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	});
}

pageObj.saveTempletDtl = function(newTempletDtl) {
	if ($("#txtTempletDtlName").val() == "") {
		alert("必须输入附件名称。");
		return false;
	}
	var data = {};
	data.name = $("#txtTempletDtlName").val();
	data.no = $("#txtTempletDtlNo").val();
	data.media = $("#cboTempletDtlMedia").val();
	data.is_required = $("#chkTempletDtlIsRequired").attr("checked") ? 1 : 0;
	data.is_publish = $("#chkTempletDtlIsPublish").attr("checked") ? 1 : 0;
	data.is_archive = $("#chkTempletDtlIsArchive").attr("checked") ? 1 : 0;
	data.dtl_count = $("#txtTempletDtlDtlCount").val();
	data.dtl_file_count = $("#txtTempletDtlDtlFileCount").val();
	data.file_type = $("#txtTempletDtlFileType").val();
	var fileSizeUnit = $("#cboFileSizeUnit").val();
	if (fileSizeUnit == "TB")
		data.file_size = parseFloat($("#txtTempletDtlFileSize").val()) * 1024 * 1024 * 1024;
	else if (fileSizeUnit == "GB")
		data.file_size = parseFloat($("#txtTempletDtlFileSize").val()) * 1024 * 1024;
	else if (fileSizeUnit == "MB")
		data.file_size = parseFloat($("#txtTempletDtlFileSize").val()) * 1024;
	else
		data.file_size = parseFloat($("#txtTempletDtlFileSize").val());
	data.file_desc = $("#txtTempletDtlFileDesc").val();
	data.remark = $("#txtTempletDtlRemark").val();
	if (newTempletDtl)
		pageObj.templetDtlGridManager.addRow(data);
	else
		pageObj.templetDtlGridManager.updateRow(pageObj.templetDtlGridManager.getSelectedRow(), data);
	pageObj.templetDtlDataModified = true;
	return true;
}

//---------------------------------
//删除附件
//---------------------------------
pageObj.deleteTempletDtl = function() {
	var selectedRow = pageObj.templetDtlGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要删除的附件。', window);
		return false;
	}
	if (selectedRow.id)
		if (pageObj.deleteTempletDtlIds)
			pageObj.deleteTempletDtlIds += ";" + selectedRow.id;
		else
			pageObj.deleteTempletDtlIds = selectedRow.id;
	pageObj.templetDtlGridManager.deleteSelectedRow();
	pageObj.templetDtlDataModified = true;
}

pageObj.upTempletDtl = function() {
	var selectedRow = pageObj.templetDtlGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要上移的附件。', window);
		return false;
	}
	pageObj.templetDtlGridManager.up(selectedRow);
	pageObj.templetDtlDataModified = true;
}

pageObj.downTempletDtl = function() {
	var selectedRow = pageObj.templetDtlGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要下移的附件。', window);
		return false;
	}
	pageObj.templetDtlGridManager.down(selectedRow);
	pageObj.templetDtlDataModified = true;
}

pageObj.templetDtlGridDrag = function (rows, parent, near, after) {
	pageObj.templetDtlDataModified = true;
}

pageObj.initTempletRelTab = function() {
	if (!pageObj.templetRelLoaded) {
		$("#tabTempletRel").ligerTab({
			contextmenu: false,
			onAfterSelectTabItem: pageObj.afterSelectTabItem
		});
		pageObj.templetRelLoaded = true;
	}
}

//---------------------------------
//页面初始化
//---------------------------------
pageObj.initHtml = function() {
	pageObj.checkDataModified = new FieldModifiedChecker($("#templetEditor")[0]);
	pageObj.initParentId = Utils.getPageValue("parentId");
	$("#btnAdd input").click(pageObj.addTemplet);
	$("#btnAddChild input").click(pageObj.addChildTemplet);
	$("#btnDelete input").click(pageObj.deleteTemplet);
	$("#btnSave input").click(pageObj.saveData);
	$("#txtTempletDtlDtlCount").keydown(pageObj.checkValidityOnlyInteger);
	$("#txtTempletDtlDtlFileCount").keydown(pageObj.checkValidityOnlyInteger);
	$("#txtTempletDtlFileSize").keydown(Utils.keyPressIntegerOnly);
	pageObj.initTreeData();
	if (pageObj.initFirstNodeId == "")
		pageObj.newTemplet();
	else {
		//选中首节点
    	var selectNodeCallback = function (data)
		    {
		        return data.id == pageObj.initFirstNodeId;
		    };
	    pageObj.treeManager.selectNode(selectNodeCallback);
	    pageObj.operNode = pageObj.treeManager.getSelected();
	    pageObj.nodeSelect(pageObj.operNode);
	}
	pageObj.initTempletRelTab();
	pageObj.afterSelectTabItem("tabTempletDtl");
}

$(document).ready(pageObj.initHtml);