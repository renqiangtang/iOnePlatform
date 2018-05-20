// ---------------------------------
// 页对象
// ---------------------------------
var pageAttachObj = {}
// ---------------------------------
// 树管理器引用
// ---------------------------------
pageAttachObj.treeManager = null;
pageAttachObj.gridManager = null;
// ---------------------------------
// 树当前操作节点引用
// ---------------------------------
pageAttachObj.node = null;
pageAttachObj.nodeOwnerGrant = null;
pageAttachObj.nodeGrant = null;
// ---------------------------------
// owner未传入grant属性时如何处理
// 0只读且不能下载，1只读并且可以下载(缺省值)，2按系统缺省方式(指pageAttachObj.nodeSelect中的设置)
// 无论grant属性如何，都不能逾越系统缺省权限。如来自模板的附件分类节点不能修改名称，那么无论权限如何都不能修改
// ---------------------------------
pageAttachObj.grantMode = 1;
// ---------------------------------
// 初始化界面时首个节点ID
// ---------------------------------
pageAttachObj.initFirstNodeId = '';
// ---------------------------------
// 显示选中节点的附件文件描述
// ---------------------------------
pageAttachObj.showAttachFileDesc = true;
// ---------------------------------
// 显示选中节点的附件路径
// ---------------------------------
pageAttachObj.showAttachPath = false;
pageAttachObj.gridHeightOffset = 8;
pageAttachObj.autoRename = false;
 

// ---------------------------------
// 解析权限字符串
// ---------------------------------
pageAttachObj.parseGrant = function(node) {
	pageAttachObj.nodeOwnerGrant = null;
	pageAttachObj.nodeGrant = null;
	var ownerGrant = null;
	if (node && node.data && pageAttachObj.owners && pageAttachObj.owners.length > 0) {
		var refId = node.data.tree_level == "1" ? node.data.real_id : node.data.real_parent_id;
		var refTableName = node.data.ref_table_name;
		var category = node.data.category;
		for(var i = 0; i < pageAttachObj.owners.length; i++) {
			var owner = pageAttachObj.owners[i];
			if (owner.id == refId
				&& ((!owner.tableName && !refTableName) || owner.tableName == refTableName)
				&& ((!owner.category && !category) || owner.category == category)) {
				ownerGrant = owner.grant;
				break;
			}
		}
	}
	
	var nodeGrant = pageAttachObj.parseAttachGrant(node, ownerGrant);
	if (nodeGrant) {
		
		pageAttachObj.visualSpanBtn("btnNewAttach", nodeGrant.add, false);
		pageAttachObj.visualSpanBtn("btnEditAttach", nodeGrant.edit, false);
		pageAttachObj.visualSpanBtn("btnDeleteAttach", nodeGrant.del, false);
		pageAttachObj.visualSpanBtn("btnNewAttachDtl", nodeGrant.uploadFile, false);
		pageAttachObj.visualSpanBtn("btnDownloadAttachDtl", nodeGrant.downloadFile, false);
		pageAttachObj.visualSpanBtn("btnDeleteAttachDtl", nodeGrant.delFile, false);
	}
	pageAttachObj.nodeOwnerGrant = ownerGrant;
	pageAttachObj.nodeGrant = nodeGrant;
	return pageAttachObj.nodeGrant;
}

pageAttachObj.parseAttachGrant = function(node, ownerGrant) {
	var resultGrant = null;
	var grantMode = pageAttachObj.grantMode;
	//owner.grant属性格式(0不显示，1不可用，2可用)：
	//{add: 2, edit: 2, del: 2,
	//	addDtl: 2, editDtl: 2, delDtl: 2, //预留，可不传
	//	uploadFile: 2, downloadFile: 2, delFile: 2,
	//	excludes: [
	//		{ids: ['frsfz', 'yyzz'],
	//		nos: ['frsfz', 'yyzz'],
	//		names: ['法人身份证', '营业执照'],
	//		add: 2, edit: 1, del: 1,
	//		addDtl: 2, editDtl: 2, delDtl: 2,
	//		uploadFile: 2, downloadFile: 2, delFile: 2},
	//		{ids: ['frsfz', 'yyzz'],
	//		nos: ['frsfz', 'yyzz'],
	//		names: ['法人身份证', '营业执照'],
	//		add: 2, edit: 1, del: 1,
	//		addDtl: 2, editDtl: 2, delDtl: 2,
	//		uploadFile: 2, downloadFile: 2, delFile: 2}
	//	]
	//}
	if (ownerGrant) {
		var grant = ownerGrant;
		var excludes = grant.excludes;
		resultGrant = {};
		resultGrant.add = grant.add ? grant.add : (grantMode == 0 || grantMode == 1 ? 0 : -1);
		resultGrant.edit = grant.edit ? grant.edit : (grantMode == 0 || grantMode == 1 ? 0 : -1);
		resultGrant.del = grant.del ? grant.del : (grantMode == 0 || grantMode == 1 ? 0 : -1);
		resultGrant.addDtl = grant.addDtl ? grant.addDtl : (grantMode == 0 || grantMode == 1 ? 0 : -1);
		resultGrant.editDtl = grant.editDtl ? grant.editDtl : (grantMode == 0 || grantMode == 1 ? 0 : -1);
		resultGrant.delDtl = grant.delDtl ? grant.delDtl : (grantMode == 0 || grantMode == 1 ? 0 : -1);
		resultGrant.uploadFile = grant.uploadFile ? grant.uploadFile : (grantMode == 0 || grantMode == 1 ? 0 : -1);
		resultGrant.downloadFile = grant.downloadFile ? grant.downloadFile : (grantMode == 0 ? 0 : -1);
		resultGrant.delFile = grant.delFile ? grant.delFile : (grantMode == 0 || grantMode == 1 ? 0 : -1);
		//检查是否有单独权限
		if (node && excludes && excludes.length > 0) {
			var nodeId = node.data.real_id;
			var nodeNo = node.data.templet_file_no;
			var nodeName = node.data.text;
			for(var i = 0; i < excludes.length; i++) {
				var exclude = excludes[i];
				if (exclude) {
					if ((nodeId && (exclude.ids && $.inArray(nodeId, exclude.ids) >= 0)
						|| (nodeNo && exclude.nos && $.inArray(nodeNo, exclude.nos) >= 0)
						|| (nodeName && exclude.names && $.inArray(nodeName, exclude.names) >= 0))) {
							resultGrant.add = exclude.add ? exclude.add : resultGrant.add;
							resultGrant.edit = exclude.edit ? exclude.edit : resultGrant.edit;
							resultGrant.del = exclude.del ? exclude.del : resultGrant.del;
							resultGrant.addDtl = exclude.addDtl ? exclude.addDtl : resultGrant.addDtl;
							resultGrant.editDtl = exclude.editDtl ? exclude.editDtl : resultGrant.editDtl;
							resultGrant.delDtl = exclude.delDtl ? exclude.delDtl : resultGrant.delDtl;
							resultGrant.uploadFile = exclude.uploadFile ? exclude.uploadFile : resultGrant.uploadFile;
							resultGrant.downloadFile = exclude.downloadFile ? exclude.downloadFile : resultGrant.downloadFile;
							resultGrant.delFile = exclude.delFile ? exclude.delFile : resultGrant.delFile;
							break;
						}
				}
			}
		}
	} else {
		if (grantMode == 0 || grantMode == 1) {
			resultGrant = {};
			//-1按缺省不修改权限，0不显示，1不可用，2可用
			resultGrant.add = 0;
			resultGrant.edit = 0;
			resultGrant.del = 0;
			resultGrant.addDtl = 0;
			resultGrant.editDtl = 0;
			resultGrant.delDtl = 0;
			resultGrant.uploadFile = 0;
			resultGrant.downloadFile = 0;
			resultGrant.delFile = 0;
		}
	}
	return resultGrant;
}

pageAttachObj.getCurrentNodeGrant = function () {
	var currentNodeGrant = {};
	currentNodeGrant.add = pageAttachObj.visualSpanBtn("btnNewAttach");
	currentNodeGrant.edit = pageAttachObj.visualSpanBtn("btnEditAttach");
	currentNodeGrant.del = pageAttachObj.visualSpanBtn("btnDeleteAttach");
	currentNodeGrant.addDtl = pageAttachObj.visualSpanBtn("btnNewAttachDtl");
	currentNodeGrant.editDtl = pageAttachObj.visualSpanBtn("btnNewAttach");
	currentNodeGrant.delDtl = pageAttachObj.visualSpanBtn("btnNewAttach");
	currentNodeGrant.uploadFile = pageAttachObj.visualSpanBtn("btnNewAttachDtl");
	currentNodeGrant.downloadFile = pageAttachObj.visualSpanBtn("btnDownloadAttachDtl");
	currentNodeGrant.delFile = pageAttachObj.visualSpanBtn("btnDeleteAttachDtl");
	return currentNodeGrant;
}

// ---------------------------------
// 设置或者读取按钮(span包裹的按钮)可视状态，状态值(visualControl): 
// spanBtnId: 按钮id
// visualControl: 0不显示，1显示不可用，2显示且可用。不传入参数则为读取，此时第三个参数无意义
// allowUpLevel：是否允许提升显示级别，设置权限使用。
// ---------------------------------
pageAttachObj.visualSpanBtn = function(spanBtnId, visualControl, allowUpLevel) {
	if (visualControl == undefined || visualControl == null) {//读取按钮可视状态
		if (!spanBtnId)
			return -1;
		else {
			if ($("#" + spanBtnId).css("display") == "block" || $("#" + spanBtnId).css("display") == "inline-block") {
				if (comObj.spanBtnDisabled(spanBtnId))
					return 1;
				else
					return 2;
			} else
				return 0;
		}
	} else {//设置按钮可视状态
		if (!spanBtnId)
			return false;
		var currentVisual = pageAttachObj.visualSpanBtn(spanBtnId);
		if (allowUpLevel == undefined || allowUpLevel || currentVisual >= visualControl) {
			if (visualControl == 0) {
				comObj.spanBtnDisabled(spanBtnId, true);
				$("#" + spanBtnId).hide();
			} else if (visualControl == 2) {
				comObj.spanBtnDisabled(spanBtnId, false);
				$("#" + spanBtnId).show();
			} else {// if (visualControl == 1) {
				comObj.spanBtnDisabled(spanBtnId, true);
				$("#" + spanBtnId).show();
			}
		}
	}
}

// ---------------------------------
// 树选择节点
// ---------------------------------
pageAttachObj.nodeSelect = function(node) {

	var attachPath = null;
	var attachFileDesc = null;
	if (node && node.data) {
		pageAttachObj.node = node;
		pageAttachObj.id = pageAttachObj.node.data.id;
	
		pageAttachObj.visualSpanBtn("btnNewAttach",2, true);
		if (pageAttachObj.node.data.tree_level == 1) {
			pageAttachObj.visualSpanBtn("btnEditAttach",1, true);
			pageAttachObj.visualSpanBtn("btnDeleteAttach",1, true);
		} else if (pageAttachObj.node.data.templet_dtl_id) {
			pageAttachObj.visualSpanBtn("btnEditAttach",2, true);
			pageAttachObj.visualSpanBtn("btnDeleteAttach",1, true);
		} else {
			pageAttachObj.visualSpanBtn("btnEditAttach",2, true);
			pageAttachObj.visualSpanBtn("btnDeleteAttach",2, true);
		}
	  
		if (pageAttachObj.node.data.tree_level == 2)
			pageAttachObj.visualSpanBtn("btnNewAttachDtl",2, true);
		else
			pageAttachObj.visualSpanBtn("btnNewAttachDtl",1, true);
		pageAttachObj.visualSpanBtn("btnDownloadAttachDtl", 1, true);
		pageAttachObj.visualSpanBtn("btnDeleteAttachDtl", 1, true);
		if (pageAttachObj.ignoreGrant == 0)
			pageAttachObj.parseGrant(node);
		//if (pageAttachObj.id != node.data.id)
			
			pageAttachObj.getAttachDtlData();
				
		if (pageAttachObj.node.data.tree_level == 1) {
			if (pageAttachObj.showAttachFileDesc || pageAttachObj.showAttachPath)
				attachPath = node.data.text;
		} else if (pageAttachObj.node.data.tree_level == 2) {
			var parentNodeTarget = pageAttachObj.treeManager.getParentTreeItem(pageAttachObj.node.target);
			if (parentNodeTarget) {
				var parentText = parentNodeTarget.textContent;
				if (parentText)
					attachPath = parentText.replace(pageAttachObj.node.data.text, "") + "\\" + pageAttachObj.node.data.text;
				else
					attachPath = pageAttachObj.node.data.text;
			} else
				attachPath = pageAttachObj.node.data.text;
		}
		attachFileDesc = pageAttachObj.node.data.file_desc;
	} else {
	
		pageAttachObj.visualSpanBtn("btnNewAttach", 0, true);
		pageAttachObj.visualSpanBtn("btnEditAttach", 0, true);
		pageAttachObj.visualSpanBtn("btnDeleteAttach", 0, true);
		pageAttachObj.visualSpanBtn("btnNewAttachDtl", 0, true);
		pageAttachObj.visualSpanBtn("btnDownloadAttachDtl", 0, true);
		pageAttachObj.visualSpanBtn("btnDeleteAttachDtl", 0, true);
		if (pageAttachObj.showAttachFileDesc || pageAttachObj.showAttachPath)
			attachFileDesc = "未选择有效附件节点。";
	}
	pageAttachObj.showAttachDesc(attachPath, attachFileDesc);
}

// ---------------------------------
// 新增节点
// ---------------------------------
pageAttachObj.addTreeNode = function(text, id, parentId, tableName, category) {
	var node = pageAttachObj.node;
	var nodes = [];
	nodes.push({
				text: text,
				id: id,
				real_id: id,
				parent_id: parentId + "-" + category ? category : "",
				real_parent_id: parentId,
				ref_table_name: tableName,
				tree_level: 2,
				category: category,
				isexpand: false
			});
	if (node) {
		if (node.data.tree_level == "1") {
			pageAttachObj.treeManager.append(node.target, nodes);
		} else {
			var parentNodeTarget = pageAttachObj.treeManager.getParentTreeItem(node.target);
			if (parentNodeTarget) {
				pageAttachObj.treeManager.append(parentNodeTarget, nodes);
			}
		}
	} else {// 未选择任何节点时新增
		pageAttachObj.treeManager.append(null, nodes);
	}
	// 选中新加的节点
	var selectNodeCallback = function(data) {
		return data.id == id;
	};
	pageAttachObj.treeManager.selectNode(selectNodeCallback);
	pageAttachObj.node = pageAttachObj.treeManager.getSelected();
}

// ---------------------------------
// 更新节点
// ---------------------------------
pageAttachObj.updateTreeNode = function(text) {
	var node = pageAttachObj.node;
	if (node)
		pageAttachObj.treeManager.update(node.target, {
					text : text
				});
}

// ---------------------------------
// 树删除节点
// ---------------------------------
pageAttachObj.removeTreeNode = function() {
	var node = pageAttachObj.node;
	if (node)
		pageAttachObj.treeManager.remove(node.target);
	else
		DialogError('请先选择' + pageAttachObj.attachLabel + '节点', window);
}

// ---------------------------------
// 树节点转换方法，对后台返回的JSON生成自定义内容
// ---------------------------------
pageAttachObj.itemsConvert = function(items) {
	items.idFieldName = 'id';
	items.parentIDFieldName = 'parent_id';
	items.nodeWidth = 200;
	items.checkbox = false;
	items.onSelect = pageAttachObj.nodeSelect;
}

// ---------------------------------
// 获取附件明细表格数据
// ---------------------------------
pageAttachObj.getAttachDtlData = function() {
	if (!(pageAttachObj.node && pageAttachObj.node.data))
		return;
	pageAttachObj.queryAttachDtlData();
}

// ---------------------------------
// 新增附件树数据
// ---------------------------------
pageAttachObj.newAttach = function() {
	if (pageAttachObj.node == null) {
		DialogAlert('请选择' + pageAttachObj.attachLabel + '分类节点。', window);
		return;
	}
	var refId = pageAttachObj.node.data.tree_level == "1" ? pageAttachObj.node.data.real_id : pageAttachObj.node.data.real_parent_id;
	var refTableName = pageAttachObj.node.data.ref_table_name;
	var category = pageAttachObj.node.data.category;
	var randomVarPrefix = newGUID(true);
	var dialog = DialogOpen({
				height : 200,
				width : 400,
				title : '新增' + pageAttachObj.attachLabel + '分类',
				url : approot + '/sysman/attachEdit.html?moduleId=' + pageAttachObj.moduleId
					+ '&id=&refId=' + refId + '&refTableName=' + refTableName + '&category=' + (category ? category : '')
					+ '&u=' + pageAttachObj.userId + '&randomVarPrefix=' + randomVarPrefix
			}, window);
	var manager = {};
	manager.dialog = dialog;
	manager.parentPageObj = pageAttachObj;
	setGlobalAttribute(randomVarPrefix + "_attachEditDialog", manager);
}

// ---------------------------------
// 编辑附件树数据
// ---------------------------------
pageAttachObj.editAttach = function() {
	if (pageAttachObj.node == null) {
		DialogAlert('请选择需要修改的' + pageAttachObj.attachLabel + '节点。', window);
		return;
	}
	if (pageAttachObj.node.data.tree_level == "1") {
		DialogAlert('请选择需要修改的' + pageAttachObj.attachLabel + '节点，当前选择的是' + pageAttachObj.attachLabel + '所有者节点。', window);
		return false;
	}
	var attachId = pageAttachObj.node.data.id;
	var templetDtlId = pageAttachObj.node.data.templet_dtl_id;
	var nameReadOnly = templetDtlId != undefined && templetDtlId != null && templetDtlId != "" && templetDtlId.toLowerCase() != "null";
	var randomVarPrefix = newGUID(true);
	var dialog = DialogOpen({
				height : 200,
				width : 400,
				title : '修改' + pageAttachObj.attachLabel + '分类',
				url : approot + '/sysman/attachEdit.html?moduleId=' + pageAttachObj.moduleId + '&id=' + attachId + '&nameReadOnly=' + nameReadOnly + '&u=' + pageAttachObj.userId + '&randomVarPrefix='
						+ randomVarPrefix
			}, window);
	var manager = {};
	manager.dialog = dialog;
	manager.parentPageObj = pageAttachObj;
	setGlobalAttribute(randomVarPrefix + "_attachEditDialog", manager);
}

// ---------------------------------
// 删除附件树数据
// ---------------------------------
pageAttachObj.deleteAttach = function() {
	if (pageAttachObj.node == null) {
		DialogAlert('请选择要删除的' + pageAttachObj.attachLabel + '节点。', window);
		return false;
	}
	if (pageAttachObj.node.data.tree_level == "1") {
		DialogAlert('请选择要删除的' + pageAttachObj.attachLabel + '节点，当前选择的是' + pageAttachObj.attachLabel + '所有者节点。', window);
		return false;
	}
	if (pageAttachObj.node.data.templet_dtl_id) {
		DialogAlert('选择要' + pageAttachObj.attachLabel + '节点来自系统模板，不允许删除。', window);
		return false;
	}
	DialogConfirm('删除当前' + pageAttachObj.attachLabel + '节点 ' + pageAttachObj.node.data.text + '？', function(yes) {
				if (yes) {
					var cmd = new Command();
					cmd.module = "sysman";
					cmd.service = "Attach";
					cmd.method = "deleteAttachData";
					cmd.id = pageAttachObj.node.data.id;
					cmd.success = function(data) {
						var state = data.state;
						if (state == '1') {
							pageAttachObj.removeTreeNode();
							pageAttachObj.queryAttachDtlData();
							pageAttachObj.node = null;
							DialogAlert('删除' + pageAttachObj.attachLabel + '数据成功完成');
						} else {
							DialogError('删除' + pageAttachObj.attachLabel + '失败,错误原因：' + data.message);
							return false;
						}
					};
					cmd.execute();
				}
			}, window);
}

// ---------------------------------
// 上移附件数据
// ---------------------------------
pageAttachObj.moveUpAttach = function() {

}

// ---------------------------------
// 下移附件数据
// ---------------------------------
pageAttachObj.moveDownAttach = function() {

}

// ---------------------------------
// 新增附件明细数据
// ---------------------------------
pageAttachObj.newAttachDtl = function() {
	if (pageAttachObj.node == null) {
		DialogAlert('请选择需要上传文件的' + pageAttachObj.attachLabel + '节点。', window);
		return;
	}
	if (pageAttachObj.node.data.tree_level == "1") {
		DialogAlert('请选择需要上传文件的' + pageAttachObj.attachLabel + '节点，当前选择的是' + pageAttachObj.attachLabel + '所有者节点。', window);
		return false;
	}
	var allowDtlCount = pageAttachObj.node.data.allow_dtl_count;
	if (pageAttachObj.node.data.templet_dtl_id && allowDtlCount > 0 && allowDtlCount <= pageAttachObj.gridManager.getData().length) {
		DialogAlert('当前' + pageAttachObj.attachLabel + '节点系统模板中设置的文件数量为' + allowDtlCount + '，当前已经达到该限制不允许再添加新的记录。', window);
		return false;
	}
	var renameFileName = null;
	if (pageAttachObj.autoRename) {
		renameFileName = pageAttachObj.node.data.text;
		var dtlData = pageAttachObj.gridManager.getData();
		if (dtlData.length > 0) {
			var fileNameIndex = 0;
			var newFileName = renameFileName;
			var i = 0;
			while (true) {
				var row = dtlData[i];
				var fileName = row.file_name;
				if (fileName.indexOf('.') >= 0)
					fileName = fileName.substr(0, fileName.lastIndexOf('.'));
				if (fileName == newFileName) {
					fileNameIndex++;
					newFileName = renameFileName + fileNameIndex;
					i = 0;
					continue;
				}
				i++;
				if (i >= dtlData.length)
					break;
			}
			renameFileName = newFileName;
		}
	}
	var limitFileSize = parseFloat(pageAttachObj.node.data.file_size);
	if (isNaN(limitFileSize) || limitFileSize <= 0)
		limitFileSize = 0;
	var allowFileType = pageAttachObj.node.data.allow_file_type;
	var attachId = pageAttachObj.node.data.id;
	var randomVarPrefix = newGUID(true);
	var dialog = DialogOpen({
				height : 230,
				width : 400,
				title : '新增' + pageAttachObj.attachLabel,
				allowClose : false,
				url : approot + '/sysman/attachUpload.html?moduleId=' + pageAttachObj.moduleId + '&attachId=' + attachId + (allowFileType ? '&allowFileType=' + encodeURIComponent(allowFileType) : '')
					+ (renameFileName ? '&rename=' + encodeURIComponent(renameFileName) + '&unallowRename=1' : '')
					+ (limitFileSize > 0 ? '&fileSize=' + limitFileSize : '')
					+ '&u=' + pageAttachObj.userId + '&randomVarPrefix=' + randomVarPrefix
			}, window);
	var manager = {};
	manager.dialog = dialog;
	manager.parentPageObj = pageAttachObj;
	setGlobalAttribute(randomVarPrefix + "_attachUploadDialog", manager);
}

// ---------------------------------
// 删除附件明细数据
// ---------------------------------
pageAttachObj.deleteAttachDtl = function(rowindex) {
	if(typeof(rowindex)=="number"){//点击附件明细的删除操作时
		pageAttachObj.gridManager.select(rowindex);
	}
	var selectedRow = pageAttachObj.gridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要删除的' + pageAttachObj.attachLabel + '明细。', window);
		return false;
	}
	DialogConfirm('删除当前' + pageAttachObj.attachLabel + '明细 ' + selectedRow.file_name + '？', function(yes) {
				if (yes) {
					var cmd = new Command();
					cmd.module = "sysman";
					cmd.service = "Attach";
					cmd.method = "deleteAttachDtlData";
					cmd.id = selectedRow.id;
					cmd.success = function(data) {
						var state = data.state;
						if (state == '1') {
							DialogAlert('删除' + pageAttachObj.attachLabel + '明细数据成功完成');
							pageAttachObj.gridManager.deleteSelectedRow();
							pageAttachObj.visualSpanBtn("btnDownloadAttachDtl", 1, true);
							pageAttachObj.visualSpanBtn("btnDeleteAttachDtl", 1, true);
						} else {
							DialogError('删除' + pageAttachObj.attachLabel + '明细失败,错误原因：' + data.message);
							return false;
						}
					};
					cmd.execute();
				}
			}, window);
}

pageAttachObj.getAttachDtlDownloadUrl = function(row) {
	var fileName = row.file_name;
	var lobIds = row.lob_ids;
	if (lobIds) {
		var downloadUrl = approot + "/download?module=base&service=BaseDownload&method=";
		if (lobIds.indexOf(",") != -1) {
			var pos = fileName.lastIndexOf(".");
			if (pos >= 0)
				fileName = fileName.substr(0, pos - 1);
			downloadUrl += "downloadZipFile&lobId=" + lobIds + "&isZip=true&zipFileName=" + fileName;
		} else {
			downloadUrl += "downloadFile&lobId=" + lobIds;
		}
		return downloadUrl;
	} else {
		return "";
	}
}

// ---------------------------------
// 下载附件明细数据
// ---------------------------------
pageAttachObj.downloadAttachDtl = function() {
	var selectedRow = pageAttachObj.gridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要下载的' + pageAttachObj.attachLabel + '明细。', window);
		return false;
	}
	var downloadUrl = pageAttachObj.getAttachDtlDownloadUrl(selectedRow);
	if (downloadUrl) {
		var form = $("#fileForm")[0];
		form.action = downloadUrl;
		form.method = "post";
		form.target = "hidden_frame";
		form.submit();
	} else {
		DialogAlert('当前行' + pageAttachObj.attachLabel + '明细没有实际文件可下载。', window);
		return false;
	}
}

// ---------------------------------
// 上移附件明细数据
// ---------------------------------
pageAttachObj.moveUpAttachDtl = function() {

}

// ---------------------------------
// 下移附件明细数据
// ---------------------------------
pageAttachObj.moveDownAttachDtl = function() {

}

pageAttachObj.fileNameRender = function(row, rowNamber) {
	var nodeGrant = pageAttachObj.nodeGrant;
	var downloadUrl = pageAttachObj.getAttachDtlDownloadUrl(row);
	if (downloadUrl && (pageAttachObj.ignoreGrant == 1 || (nodeGrant && nodeGrant.downloadFile == 2)))
		return "<a href='" + downloadUrl + "'>" + row.file_name + "</a>";
	else
		return row.file_name;			
}

pageAttachObj.operateRender = function(row, rowNamber) {
	var nodeGrant = pageAttachObj.nodeGrant;
	var downloadUrl = pageAttachObj.getAttachDtlDownloadUrl(row);
	var operate = "";
	if (downloadUrl && (pageAttachObj.ignoreGrant == 1 || (nodeGrant && nodeGrant.downloadFile == 2)))
		operate += "<span class='c2 chand' onclick=javascript:pageAttachObj.openDownUrl('"+downloadUrl+"')>下载</span>&nbsp;&nbsp;&nbsp;"
	if (pageAttachObj.ignoreGrant == 1 || (nodeGrant && nodeGrant.delFile == 2))
		operate += "<a href='javascript:void(0)' onclick='javascript:pageAttachObj.deleteAttachDtl("+rowNamber+"); return false;'>删除</a>";
	return operate;
}

pageAttachObj.openDownUrl = function(url){
	var form = $("#fileForm")[0];
	form.action = url;
	form.method = "post";
	form.target = "hidden_frame";
	form.submit();
}

// ---------------------------------
// 表格初始化
// ---------------------------------
pageAttachObj.initGrid = function() {
	pageAttachObj.showAttachDesc(pageAttachObj.node);
	var gridHeight = $("#attachTree")[0].offsetHeight + pageAttachObj.gridHeightOffset;
	if ($("#attachFileDesc").is(":visible"))
		gridHeight -= $("#attachFileDesc").height();
	var option = {
		url : approot + '/data?module=sysman&service=Attach&method=getAttachDtlListData',
		checkbox : false,
		pagesizeParmName : 'pageRowCount',// 每页记录数
		pageParmName : 'pageIndex',// 页码数
		sortnameParmName : 'sortField',// 排序列名
		sortorderParmName : 'sortDir',// 排序方向
		isScroll : true,// 是否滚动
		frozen : false,// 是否固定列
		pageSizeOptions : [10, 20, 30],
		showTitle : false,
		width : '99.8%',
		height : gridHeight,
		detail : false,
		onSelectRow : pageAttachObj.gridSelectRow
	};
	var columns = [{
		display : '文件名',
		name : 'file_name',
		width : pageAttachObj.showFileCount == 1 ? '50%' : '80%',
		render : pageAttachObj.fileNameRender
	}];
	if (pageAttachObj.showFileCount == 1)
		columns.push({
			display : '文件数',
			name : 'file_count',
			width : '30%'
		});
	columns.push({
		display : '操作',
		name : 'operate',
		width : '20%',
		isSort : false,
		render : pageAttachObj.operateRender
	});
	//alert(JSON.stringify(columns));
	option.columns = columns;
	pageAttachObj.gridManager = $("#attachDtlGrid").ligerGrid(option);
}

pageAttachObj.gridSelectRow = function(rowdata, rowindex, rowDomElement) {
	var nodeGrant = pageAttachObj.nodeGrant;
	var downloadUrl = pageAttachObj.getAttachDtlDownloadUrl(rowdata);
	if (downloadUrl) {
		if (pageAttachObj.ignoreGrant == 1)
			pageAttachObj.visualSpanBtn("btnDownloadAttachDtl", 2, true);
		else if (nodeGrant)
			pageAttachObj.visualSpanBtn("btnDownloadAttachDtl", nodeGrant.downloadFile, true);
		else
			pageAttachObj.visualSpanBtn("btnDownloadAttachDtl", 1, true);
	}
	if (pageAttachObj.ignoreGrant == 1)
		pageAttachObj.visualSpanBtn("btnDeleteAttachDtl", 2, true);
	else if (nodeGrant)
		pageAttachObj.visualSpanBtn("btnDeleteAttachDtl", nodeGrant.delFile, true);
	else
		pageAttachObj.visualSpanBtn("btnDeleteAttachDtl", 1, true);
}

pageAttachObj.getQueryParams = function () {
	var queryParams = {};
	var refId = pageAttachObj.node.data.tree_level == 1 ? pageAttachObj.node.data.real_id : pageAttachObj.node.data.real_parent_id;
	var refTableName = pageAttachObj.node.data.ref_table_name;
	var category = pageAttachObj.node.data.category;
	var attachId = pageAttachObj.node.data.tree_level == 1 ? null : pageAttachObj.node.data.real_id;
	if (refId)
		queryParams.ref_id = refId;
	if (refTableName)
		queryParams.ref_table_name = refTableName;
	if (category)
		queryParams.category = category;
	if (attachId)
		queryParams.attach_id = attachId;
	return queryParams;
}

// ---------------------------------
// 查询数据
// ---------------------------------
pageAttachObj.queryAttachDtlData = function() {
	var url = approot + '/data?module=sysman&service=Attach&method=getAttachDtlListData';
	pageAttachObj.gridManager.refresh(url, pageAttachObj.getQueryParams());
}

// ---------------------------------
// 树数据初始化
// ---------------------------------
pageAttachObj.initAttachData = function(owners, defaultOwnerId) {
	if (owners && owners != "null") {
		$("#noneOwnerPrompt").hide();
		$("#attachPanal").show();
	
		if (owners.indexOf("{") >= 0 && owners.indexOf("}") >= 0) {// 传入的是json对象字符串
			if (owners.substr(0,1) == "[" && owners.substr(owners.length - 1,1) == "]") {// 传入的是json对象数组字符串
				pageAttachObj.owners = eval("("+owners+")");
			} else {
				pageAttachObj.owners = new Array();
				pageAttachObj.owners.push(eval("(" + owners + ")"));
				

			}
		} else {
			pageAttachObj.owners = new Array();
			for (var i = 0; i <= Utils.getSubStrCount(owners, ";", true); i++) {
				// 00230000000000000000000000000031:标的,测试标的123,trans_target,businessType0TargetAttach;...
				var subStr = Utils.getSubStr(owners, ";", i, true);
				var owner = {};
				owner.id = Utils.getSubStr(subStr, ":", 0, true);
				subStr = Utils.getSubStr(subStr, ":", 1, true);
				owner.name = Utils.getSubStr(subStr, ",", 0, true);
				owner.title = Utils.getSubStr(subStr, ",", 1, true);
				owner.tableName = Utils.getSubStr(subStr, ",", 2, true);
				owner.templetNo = Utils.getSubStr(subStr, ",", 3, true);
				owner.category = Utils.getSubStr(subStr, ",", 4, true);
				pageAttachObj.owners.push(owner);
			}
		}
		pageAttachObj.defaultOwnerId = null;
		if (defaultOwnerId) {
			pageAttachObj.defaultOwnerId = defaultOwnerId;
			var blnDefaultOwnerExists = false;
			for (var i = 0; i < pageAttachObj.owners.length; i++)
				if (pageAttachObj.owners[i].id == pageAttachObj.defaultOwnerId) {
					blnDefaultOwnerExists = true;
					break;
				}
			if (!blnDefaultOwnerExists)
				pageAttachObj.defaultOwnerId = null;
		}
	
		if (!pageAttachObj.defaultOwnerId && pageAttachObj.owners.length > 0)
			pageAttachObj.defaultOwnerId = pageAttachObj.owners[0].id;
		$("#btnNewAttach input").click(pageAttachObj.newAttach);
		$("#btnEditAttach input").click(pageAttachObj.editAttach);
		$("#btnDeleteAttach input").click(pageAttachObj.deleteAttach);
		$("#btnMoveUpAttach input").click(pageAttachObj.moveUpAttach);
		$("#btnMoveDownAttach input").click(pageAttachObj.moveDownAttach);
		$("#btnNewAttachDtl input").click(pageAttachObj.newAttachDtl);
		$("#btnDownloadAttachDtl input").click(pageAttachObj.downloadAttachDtl);
		$("#btnDeleteAttachDtl input").click(pageAttachObj.deleteAttachDtl);
		$("#btnMoveUpAttachDtl input").click(pageAttachObj.moveUpAttachDtl);
		$("#btnMoveDownAttachDtl input").click(pageAttachObj.moveDownAttachDtl);
		pageAttachObj.visualSpanBtn("btnDownloadAttachDtl", 1, true);
		pageAttachObj.visualSpanBtn("btnDeleteAttachDtl", 1, true);
		pageAttachObj.initGrid();
		pageAttachObj.initTreeData();
		
		// 选中首节点
		var selectNodeCallback = function(data) {
			return data.id == pageAttachObj.initFirstNodeId;
		};
		if (pageAttachObj.treeManager) {
			pageAttachObj.treeManager.selectNode(selectNodeCallback);
			pageAttachObj.node = pageAttachObj.treeManager.getSelected();
			pageAttachObj.nodeSelect(pageAttachObj.node);
		}
			
	} else {
		$("#noneOwnerPrompt").show();
		$("#attachPanal").hide();
	}
}

// ---------------------------------
// 树数据初始化
// ---------------------------------
pageAttachObj.initTreeData = function() {
	if (!pageAttachObj.owners || pageAttachObj.owners.length == 0) {
		if (pageAttachObj.treeManager)
			pageAttachObj.treeManager.clear();
		return;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Attach";
	cmd.method = "getAttachRecurseTreeData";
	cmd.owners = JSON.stringify(pageAttachObj.owners);
	cmd.success = function(data) {
		
		if (data.data.length > 0) {
			pageAttachObj.initFirstNodeId = data.data[0].id;
		} else {
			pageAttachObj.initFirstNodeId = '';
		}
		pageAttachObj.itemsConvert(data);
		$("#attachTree").ligerTree(data);
		pageAttachObj.treeManager = $("#attachTree").ligerGetTreeManager();
		//为所有节点添加浮动提示
		$("#attachTree span").each(function () {
			$(this).attr("title", $(this).html());
		});
	};
	cmd.execute();
}

// ---------------------------------
// 设置右侧表格上方提示
// ---------------------------------
pageAttachObj.showNodeAttachDesc = function (node) {
	if (!node)
		return;
	if (node.data.tree_level == 1) {
		if (pageAttachObj.showAttachFileDesc || pageAttachObj.showAttachPath)
			attachPath = node.data.text;
	} else if (node.data.tree_level == 2) {
		var parentNodeTarget = pageAttachObj.treeManager.getParentTreeItem(node.target);
		if (parentNodeTarget) {
			var parentText = parentNodeTarget.textContent;
			if (parentText)
				attachPath = parentText.replace(node.data.text, "") + "\\" + node.data.text;
			else
				attachPath = node.data.text;
		} else
			attachPath = node.data.text;
	}
	attachFileDesc = node.data.file_desc;
	pageAttachObj.showAttachDesc(attachPath, attachFileDesc);
}

pageAttachObj.showAttachDesc = function (attachPath, attachFileDesc) {
	var attachDesc = null;
	if (pageAttachObj.showAttachPath || pageAttachObj.showAttachFileDesc) {
		pageAttachObj.showAttachPath && attachPath ? "<span class='fb'>" + attachPath + "</span>" : null;
		if (attachFileDesc && pageAttachObj.showAttachFileDesc)
			attachDesc = (attachDesc == null ? "" : attachDesc + "<br/>") + attachFileDesc;
	}
	if (attachDesc) {
		$("#attachFileDesc").html(attachDesc);
		$("#attachFileDesc").show();
	} else {
		$("#attachFileDesc").html("");
		$("#attachFileDesc").hide();
	}
	var gridHeight = $("#attachTree")[0].offsetHeight + pageAttachObj.gridHeightOffset;
	if ($("#attachFileDesc").is(":visible"))
		gridHeight -= $("#attachFileDesc").height();
	$('#attachDtlGrid').height(gridHeight);
	if (pageAttachObj.gridManager) {
		var option = {height: gridHeight};
		pageAttachObj.gridManager.setOptions(option);
	}
}

// ---------------------------------
// 自动计算高度
// ---------------------------------
pageAttachObj.autoHeight = function() {
	var iframe = Utils.getIframe();
	if (iframe) {
		$(iframe).attr('scrolling', 'no');
		pageAttachObj.height = $(iframe).height();
	} else {
		pageAttachObj.height = $(window).height();
	}
	$('#attachTree').height(pageAttachObj.height - 38);
	//$('#attachDtlGrid').height(pageAttachObj.height - 40);
}

// ---------------------------------
// 页面初始化
// ---------------------------------
pageAttachObj.initHtml = function() {
	pageAttachObj.moduleId = Utils.getPageValue("moduleId");
	pageAttachObj.userId = getUserId();
	pageAttachObj.title = Utils.getPageValue("title");
	if (pageAttachObj.title) {
		document.title = pageAttachObj.title;
	}
	var showAttachFileDesc = Utils.getPageValue("showAttachFileDesc") || Utils.getPageValue("showFileDesc");
	if (showAttachFileDesc && (showAttachFileDesc == 1 || showAttachFileDesc.toLowerCase() == "true" || showAttachFileDesc.toLowerCase() == "yes"))
		pageAttachObj.showAttachFileDesc = true;
	var showAttachPath = Utils.getPageValue("showAttachPath");
	if (showAttachPath && (showAttachPath == 1 || showAttachPath.toLowerCase() == "true" || showAttachPath.toLowerCase() == "yes"))
		pageAttachObj.showAttachPath = true;
	var leftPanelWidth = Utils.getPageValue("lwidth");
	if (leftPanelWidth)
		$("#leftPanel").attr("width", leftPanelWidth);
	var rightPanelWidth = Utils.getPageValue("rwidth");
	if (rightPanelWidth)
		$("#rightPanel").attr("width", rightPanelWidth);
	var leftTitle = Utils.getPageValue("ltitle");
	if (leftTitle)
		$("#lblLeftTitle").html(decodeURIComponent(leftTitle));
	else if (Utils.hasPageValue("ltitle"))
		$("#lblLeftTitle").html("");
	var rightTitle = Utils.getPageValue("rtitle");
	if (rightTitle)
		$("#lblRightTitle").html(decodeURIComponent(rightTitle));
	var attachLabel = Utils.getPageValue("attachLabel");
	if (attachLabel)
		pageAttachObj.attachLabel = decodeURIComponent(attachLabel);
	else
		pageAttachObj.attachLabel = '附件';
	var showFileCount = Utils.getPageValue("showFileCount");
	if (showFileCount && (showFileCount == 1 || showFileCount.toLowerCase() == "true" || showFileCount.toLowerCase() == "yes"))
		pageAttachObj.showFileCount = 1;
	else
		pageAttachObj.showFileCount = 0;
	var autoRename = Utils.getPageValue("autoRename");
	if (autoRename && (autoRename == 1 || autoRename.toLowerCase() == "true" || autoRename.toLowerCase() == "yes"))
		pageAttachObj.autoRename = true;
	var ignoreGrant = Utils.getPageValue("ignoreGrant");
	if (ignoreGrant && (ignoreGrant == "1" || ignoreGrant.toLowerCase() == "true" || ignoreGrant.toLowerCase() == "yes"))
		pageAttachObj.ignoreGrant = 1;
	else
		pageAttachObj.ignoreGrant = 0;
	if (pageAttachObj.showAttachFileDesc || pageAttachObj.showAttachPath)
		$("#attachFileDesc").show();
	pageAttachObj.autoHeight();
	var strOwners = Utils.getPageValue("owners");
	try {
		strOwners = decodeURIComponent(strOwners);
	} catch (e) {
	}
	var strDefaultOwnerId = Utils.getPageValue("defaultOwnerId");
	pageAttachObj.initAttachData(strOwners, strDefaultOwnerId);
	$('#rightPanel').ligerResizable({ handles: 'w',
			onStopResize:  function (current, e) {
				var clientWidth = $("#attachPanal").width();
				var rightWidth = current.width + 2;
				var rightNewWidth = current.newWidth + 2;
				var leftWidth = $("#leftPanel").width() + 2;
				var leftNewWidth = leftWidth - (rightNewWidth - rightWidth) - 2;
				$("#leftPanel").width(leftNewWidth + 'px');
				return true;
			}
		});
}


$(document).ready(pageAttachObj.initHtml);