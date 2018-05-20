var pageObj = {};
/**
 * home:文档的根路径 name:文件名/标题 path:针对home的相对路径 mode:admin edit view
 */

pageObj.initPage = function() {
	pageObj.home = Utils.getPageValue('home');
	pageObj.title = Utils.getPageValue('title');
	pageObj.mode = Utils.getPageValue("mode");
	if (!pageObj.mode)
		pageObj.mode = "admin";
	if (pageObj.mode == "view") {
		$("#op").hide();
	}
	if (!pageObj.home)
		pageObj.home = 'config';
	if (!pageObj.title)
		pageObj.title = '配置文件';
	var zNodes = [{
				name : pageObj.title,
				path : "/",
				iconSkin : "home",
				isParent : true
			}];
	pageObj.tree = $('#dir').tree(pageObj.setting, zNodes);
	var rootNode = pageObj.tree.getNodes()[0];
	pageObj.tree.expandNode(rootNode, true, false, true, true);
}

pageObj.beforeExpand = function(treeId, treeNode) {
	pageObj.tree.removeChildNodes(treeNode);
	var cmd = new Command();
	cmd.module = 'trademan';
	cmd.service = 'DocConfig';
	cmd.method = 'getList';
	cmd.home = pageObj.home;
	cmd.path = pageObj.getRelativePath(treeNode);
	cmd.fsType = 0;
	cmd.success = function(data) {
		if (data.state == 1)
			pageObj.tree.addNodes(treeNode, data.rs);
	};
	cmd.execute();
	return false;
}

pageObj.onClick = function(event, treeId, treeNode) {
	pageObj.curNode = treeNode;
	pageObj.curPath = pageObj.getRelativePath(pageObj.curNode);
	if (treeNode.isParent) {
		pageObj.dirRefresh(pageObj.curPath);
	}
}

pageObj.onDblClick = function(event, treeId, treeNode) {
	pageObj.curNode = treeNode;
	pageObj.curPath = pageObj.getRelativePath(pageObj.curNode);
	if (!treeNode.isParent) {
		var path = pageObj.getRelativePath(treeNode);
		if (pageObj.mode == 'admin')
			pageObj.edit(path);
	}
}

pageObj.dirRefresh = function(path) {
	var cmd = new Command();
	cmd.module = 'trademan';
	cmd.service = 'DocConfig';
	cmd.method = 'getList';
	cmd.home = pageObj.home;
	cmd.path = path;
	cmd.fsType = 2;
	cmd.success = function(data) {
		var html = '';
		if (data.state == 1) {
			var arr = data.rs;
			for (var i = 0; i < arr.length; i++) {
				var obj = arr[i];
				var absolutePath = pageObj.getRelativePath(obj);
				html += '<tr>';
				html += '<td width="50%" height=28 class="pl5 s_rb">';
				if (pageObj.mode == 'admin')
					html += '<a href="javascript:pageObj.edit(\'' + absolutePath + '\')">' + obj.name + '</a>';
				else
					html += '<a href="javascript:void(0);">' + obj.name + '</a>';
				html += '</td>';
				html += '<td width="20%" class="tc s_rb">' + new Date(obj.date) + '</td>';
				html += '<td width="20%" class="tc s_rb">' + obj.size + 'B</td>';
				html += '<td class="tc s_rb"><a href="' + approot
						+ '/download?module=trademan&service=DocConfig&method=directDownload&path='
						+ pageObj.getAbsolutePath(obj) + '" target="_blank">下载</a></td>';
				html += '</tr>'
			}
		}
		$('#file').html(html);
	};
	cmd.execute();
}

pageObj.edit = function(path) {
	var cmd = new Command('trademan', 'DocConfig', 'getContent');
	cmd.home = pageObj.home;
	cmd.path = path;
	pageObj.curPath = path;
	var html = '';
	cmd.success = function(data) {
		if (data.state == 1) {
			html = data.content;
		}
	}
	cmd.execute();
	$('#content').val(html);
	$('#fileDiv').dialog({
				title : '文件内容',
				modal : true,
				width : 850,
				height : 500
			});
}

pageObj.update = function() {
	var cmd = new Command('trademan', 'DocConfig', 'updateContent');
	cmd.home = pageObj.home;
	cmd.path = pageObj.curPath;
	cmd.content = $('#content').val();
	cmd.success = function(data) {
		if (data.state == 1) {
			alert('更新成功!')
		} else {
			alert('更新失败!')
		}
	}
	cmd.execute();

}

pageObj.getParentPath = function(node) {
	var parentNode = node.getParentNode();
	if (parentNode)
		return parentNode.path;
	else
		return null;
}

/**
 * 获取绝对路径
 * 
 * @param {}
 *            node
 * @return {}
 */
pageObj.getAbsolutePath = function(node) {
	return pageObj.home + "/" + node.path;
}

/**
 * 获取相对路径
 * 
 * @param {}
 *            node
 * @return {}
 */
pageObj.getRelativePath = function(node) {
	return node.path;
}

pageObj.dirNew = function() {
	var nodes = pageObj.tree.getSelectedNodes();
	if (nodes.length != 1) {
		alert("请先选择一个节点");
		return;
	}
	var curNode = nodes[0];
	var newName = prompt("请输入新的目录名:", '');
	newName = newName.trim();
	if (newName) {
		var cmd = new Command('trademan', 'DocConfig', 'newFile');
		cmd.home = pageObj.home;
		cmd.isDir = true;
		cmd.path = pageObj.getRelativePath(curNode);
		cmd.newName = newName;
		cmd.success = function(data) {
			if (data.state == 1) {
				pageObj.tree.addNodes(curNode, data.fileInfo);
			} else {
				alert('新建子目录失败!');
			}
		};
		cmd.execute();
	}

}

pageObj.fileNew = function() {
	var nodes = pageObj.tree.getSelectedNodes();
	if (nodes.length != 1) {
		alert("请先选择一个节点");
		return;
	}
	var curNode = nodes[0];
	var newName = prompt("请输入新的文件名:", '');
	newName = newName.trim();
	if (newName) {
		var cmd = new Command();
		cmd.module = 'trademan';
		cmd.service = 'DocConfig';
		cmd.method = 'newFile';
		cmd.home = pageObj.home;
		cmd.isDir = false;
		cmd.path = pageObj.getRelativePath(curNode);
		cmd.newName = newName;
		cmd.success = function(data) {
			if (data.state == 1) {
				pageObj.tree.addNodes(curNode, data.fileInfo);
			} else {
				alert('新建文件失败!');
			}
		};
		cmd.execute();
	}

}

pageObj.nodeRemove = function() {
	var nodes = pageObj.tree.getSelectedNodes();
	if (nodes.length != 1) {
		alert("请先选择一个节点");
		return;
	}
	var curNode = nodes[0];
	var b = confirm('确定删除' + curNode.name + '?');
	if (!b)
		return;
	var cmd = new Command('trademan', 'DocConfig', 'delFile');
	cmd.home = pageObj.home;
	cmd.path = pageObj.getRelativePath(curNode);
	cmd.success = function(data) {
		if (data.state == 1) {
			var parentNode = curNode.getParentNode();
			pageObj.tree.removeNode(curNode);
			parentNode.isParent = true;
			pageObj.tree.updateNode(parentNode);
			pageObj.dirRefresh(pageObj.getRelativePath(parentNode));
		} else {
			alert('删除失败!');
		}
	};
	cmd.execute();
}

pageObj.nodeReName = function() {
	var nodes = pageObj.tree.getSelectedNodes();
	if (nodes.length != 1) {
		alert("请先选择一个节点");
		return;
	}
	var curNode = nodes[0];
	var newName = prompt("请输入新的文件名:", curNode.name);
	newName = newName.trim();
	if (newName != curNode.name) {
		var cmd = new Command('trademan', 'DocConfig', 'reName');
		cmd.home = pageObj.home;
		cmd.path = pageObj.getParentPath(curNode);
		cmd.oldName = curNode.name;
		cmd.newName = newName;
		cmd.success = function(data) {
			if (data.state == 1) {
				alert("重命名成功!");
				curNode.name = newName;
				curNode.path = newName;
				pageObj.tree.updateNode(curNode);
			} else {
				alert("重命名失败!");
			}
		}
		cmd.execute();
	}
}

pageObj.beforeDrop = function(treeId, treeNodes, targetNode) {
	pageObj.path = pageObj.getRelativePath(treeNodes[0]);
	pageObj.parentNode = treeNodes[0].getParentNode();
	var toDir = pageObj.getRelativePath(targetNode);
	var b = targetNode.isParent;
	if (b) {
		b = confirm('确定移动[' + treeNodes[0].name + ']到[' + toDir + ']?');
		var cmd = new Command('trademan', 'DocConfig', 'moveFile');
		cmd.home = pageObj.home;
		cmd.file = pageObj.path;
		cmd.toDir = toDir;
		cmd.success = function(data) {
			if (data.state == 1) {
				b = true;
			} else {
				b = false;
				alert('移动失败!');
			}
		};
		cmd.execute();
	}
	return b;
}

pageObj.uploadClick = function() {
	var nodes = pageObj.tree.getSelectedNodes();
	if (!(nodes.length == 1 && nodes[0].isParent)) {
		alert("请先选择一个目录节点");
		return;
	}
	$('#txtFile').val('');
	$('#fileUploadDiv').dialog({
				title : '上传文件',
				modal : true,
				width : 850,
				height : 100
			});
}

pageObj.upload = function() {
	if ($('#txtFile').val()) {
		var form = document.getElementById('fileForm');
		var node = pageObj.curNode;
		if (!node.isParent)
			node = pageObj.curNode.getParentNode();
		form.action = approot + "/upload?module=trademan&service=DocConfig&method=upload&path="
				+ encodeURIComponent(pageObj.getRelativePath(node)) + "&home=" + encodeURIComponent(pageObj.home);
		form.submit();
	} else {
		alert("请先选择需要上传的文件!");
	}

}

function formCallback(data) {
	if (data.state == 1) {
		$('#fileUploadDiv').dialog('close');
		var node = pageObj.curNode;
		if (!node.isParent)
			node = pageObj.curNode.getParentNode();
		pageObj.tree.expandNode(node, true, false, true, true);
		pageObj.dirRefresh(pageObj.getRelativePath(node));
	}
}

$(document).ready(function() {
			pageObj.setting = {
				view : {
					selectedMulti : false
				},
				edit : {
					drag : {
						prev : false,
						inner : true,
						next : false
					},
					enable : true,
					showRemoveBtn : false,
					showRenameBtn : false
				},
				data : {
					simpleData : {
						enable : true,
						idKey : "name",
						rootPId : null
					}
				},
				callback : {
					beforeExpand : pageObj.beforeExpand,
					onClick : pageObj.onClick,
					onDblClick : pageObj.onDblClick,
					beforeDrop : pageObj.beforeDrop,
					onDrop : pageObj.onDrop,
					beforeDragOpen : function(treeId, treeNode) {
						if (treeNode.isParent)
							pageObj.tree.expandNode(treeNode, true, false, true, true);
						return false;
					}
				}
			};
			pageObj.initPage();
			$('#newDirBtn').click(pageObj.dirNew);
			$('#newFileBtn').click(pageObj.fileNew);
			$('#upLoadBtn').click(pageObj.uploadClick);
			$('#renameBtn').click(pageObj.nodeReName);
			$('#removeBtn').click(pageObj.nodeRemove);
			$('#updateBtn').click(pageObj.update);
			$('#upLoadingBtn').click(pageObj.upload);
		});
