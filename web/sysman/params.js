//---------------------------------
//页页对象
//---------------------------------
var pageParamsObj = {}
//---------------------------------
//树管理器引用
//---------------------------------
pageParamsObj.treeManager = null;
//---------------------------------
//树当前操作节点引用
//---------------------------------
pageParamsObj.operNode = null;
//---------------------------------
//初始化界面时首个节点ID
//---------------------------------
pageParamsObj.initFirstNodeId = '';
//---------------------------------
//检查数据是否修改
//---------------------------------
pageParamsObj.checkDataModified = null;

//---------------------------------
//树展开前事件，异步加载下级节点
//---------------------------------
pageParamsObj.nodeBeforeExpand = function (node)
{
	if (node.data.children && node.data.children.length == 0)
    {
        var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Params";
		cmd.method = "getParamsTreeData";
		cmd.dataSetNo = '';
		cmd.parentId = node.data.id;
		cmd.recurse = 2;
		cmd.success = function(data) {
			if (data.data.length > 0) {
				pageParamsObj.itemsConvert(data);
				pageParamsObj.treeManager.append(node.target, data.data);
			}
		};
		cmd.execute();
    }
}

pageParamsObj.nodeExpand = function (node)
{
	//alert('nodeExpand');
}

pageParamsObj.nodeBeforeSelect = function(node){
	if (pageParamsObj.checkDataModified.checkModified(true) && (pageParamsObj.operNode || pageParamsObj.id == "")) {
		if (!confirm("数据已修改未保存，确认切换参数节点？\n点击确定按钮将放弃未保存数据"))
			return false;
//		DialogConfirm("数据已修改未保存，确认切换参数节点（切换将丢弃未保存数据）？", function (yes) {
//			if (!yes) {
//				return false;
//			}
//		});
	}
}

//---------------------------------
//树选择节点
//---------------------------------
pageParamsObj.nodeSelect = function(node){
	if (node && pageParamsObj.id != node.data.id) {
		pageParamsObj.operNode = node;
		pageParamsObj.editParamsData();
	}
}

//---------------------------------
//新增节点
//---------------------------------
pageParamsObj.addTreeNode = function(text, id, parentId){
	var node = pageParamsObj.operNode;
    var nodes = [];
  	nodes.push({ text: text, id: id, parentId: parentId, isexpand: false});
    if (node) {
    	if (parentId == undefined || parentId == null || parentId.toLowerCase() == "null") {//一级子节点
    		pageParamsObj.treeManager.append(null, nodes);
    	} else {
    		pageParamsObj.treeManager.append(node.target, nodes);
    	}
    } else {//未选择任何节点时新增
    	pageParamsObj.treeManager.append(null, nodes);
    }
    //选中新加的节点
	var selectNodeCallback = function (data)
	    {
	        return data.id == id;
	    };
    pageParamsObj.treeManager.selectNode(selectNodeCallback);
    pageParamsObj.operNode = pageParamsObj.treeManager.getSelected();
}

//---------------------------------
//更新节点
//---------------------------------
pageParamsObj.updateTreeNode = function(text){
	var node = pageParamsObj.operNode;
    if (node)
    	pageParamsObj.treeManager.update(node.target, { text: text});
}

//---------------------------------
//树删除节点
//---------------------------------
pageParamsObj.removeTreeNode = function(){
	var node = pageParamsObj.operNode;
    if (node)
        pageParamsObj.treeManager.remove(node.target);
    else
        DialogError('请先选择系统参数节点');
}

//---------------------------------
//树节点转换方法，对后台返回的JSON生成自定义内容
//---------------------------------
pageParamsObj.itemsConvert = function (items) {
	items.idFieldName = 'id';
    items.parentIDFieldName = 'parentId'; 
    items.nodeWidth = 200;
    items.checkbox = false;
    items.onBeforeExpand = pageParamsObj.nodeBeforeExpand;
    items.onExpand = pageParamsObj.nodeExpand;
    items.onBeforeSelect = pageParamsObj.nodeBeforeSelect;
    items.onSelect = pageParamsObj.nodeSelect;
}

//---------------------------------
//系统参数编辑数据初始化
//---------------------------------
pageParamsObj.editParamsData = function () {
	pageParamsObj.operate = "editParams";
	$("#btnAdd").show();
	$("#btnDelete").show();
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Params";
	cmd.method = "getParamsData";
	cmd.dataSetNo = "";
	if (pageParamsObj.operNode) {
		cmd.id = pageParamsObj.operNode.data.id;
	} else {
		cmd.id = pageParamsObj.id;
	}
	cmd.success = function (data) {
		pageParamsObj.id = Utils.getRecordValue(data, 0, 'id');
		pageParamsObj.parentId = Utils.getRecordValue(data, 0, 'parent_id');
		if (data && data.rs && data.rs.length > 0) {
			$("#txtName").val(Utils.getRecordValue(data, 0, 'name'));
			$("#txtNo").val(Utils.getRecordValue(data, 0, 'no'));
			$("#txtLValue").val(Utils.getRecordValue(data, 0, 'lvalue'));
			$("#txtMValue").val(Utils.getRecordValue(data, 0, 'mvalue'));
			$("#txtHValue").val(Utils.getRecordValue(data, 0, 'hvalue'));
			$("#txtRemark").val(Utils.getRecordValue(data, 0, 'remark'));
		}
		pageParamsObj.checkDataModified.initFileds();
	};
	cmd.execute();
}

//---------------------------------
//新增系统参数
//---------------------------------
pageParamsObj.newParams = function (){
	pageParamsObj.operate = "addParams";
	$("#btnAdd").hide();
	$("#btnDelete").hide();
	$("#txtName").val('');
	$("#txtNo").val('');
	$("#txtLValue").val("");
	$("#txtMValue").val("");
	$("#txtHValue").val("");
	$("#txtRemark").val('');
	pageParamsObj.id = '';
	if (pageParamsObj.operNode == null)
		pageParamsObj.parentId = '';
	else
		pageParamsObj.parentId = pageParamsObj.operNode.data.id;
	pageParamsObj.checkDataModified.initFileds();
}

//---------------------------------
//保存系统参数
//---------------------------------
pageParamsObj.saveParams = function (){
	if ($("#txtName").val() == ''){
		DialogAlert('系统参数名称不能为空');
		return false;
	}
	if ($("#txtNo").val() == ''){
		DialogAlert('系统参数编号不能为空');
		return false;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Params";
	cmd.method = "updateParamsData";
	cmd.id = pageParamsObj.id;
	cmd.parent_id = pageParamsObj.parentId;
	cmd.no = $("#txtNo").val();
	cmd.name = $("#txtName").val();
	cmd.lvalue = $("#txtLValue").val();
	cmd.mvalue = $("#txtMValue").val();
	cmd.hvalue = $("#txtHValue").val();
	cmd.hidden = 1;
	cmd.remark = $("#txtRemark").val();
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			if (pageParamsObj.operate == "addParams") {
				pageParamsObj.id = data.id;
				pageParamsObj.operate = "editParams";
				$("#btnAdd").show();
				$("#btnDelete").show();
				pageParamsObj.addTreeNode($("#txtName").val(), data.id, pageParamsObj.parentId);
			}
			else
				pageParamsObj.updateTreeNode($("#txtName").val());
			pageParamsObj.checkDataModified.initFileds();
			DialogAlert('保存系统参数成功完成');
		} else {
			DialogError('保存系统参数失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

//---------------------------------
//删除系统参数
//---------------------------------
pageParamsObj.deleteParams = function (){
	if (pageParamsObj.id == '') { //正在新增
		if (pageParamsObj.operNode == null)
			pageParamsObj.newParams();
		else
			pageParamsObj.editParamsData();
		return true;
	}
	if (pageParamsObj.operNode == null){
		DialogWarn('请选择要删除的系统参数。');
		return false;
	}
	var message = null;
	if (pageParamsObj.operNode.data.children) {
		if (pageParamsObj.operNode.data.children.length > 0)
			message = '当前系统参数节点 ' + pageParamsObj.operNode.data.text + ' 包含子节点，删除它将删除所有下级系统参数节点。继续删除？';
		else
			message = '当前系统参数节点 ' + pageParamsObj.operNode.data.text + ' 可能包含仍未加载的子节点，删除它将删除所有下级系统参数节点。继续删除？';
	} else
		message = '删除当前系统参数节点 ' + pageParamsObj.operNode.data.text + '？';
	DialogConfirm(message, function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "Params";
			cmd.method = "deleteParamsData";
			cmd.id = pageParamsObj.operNode.data.id;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					pageParamsObj.removeTreeNode();
					DialogAlert('删除系统参数数据成功完成');
				} else {
					DialogError('删除系统参数失败,错误原因：' + data.message);
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
pageParamsObj.initTreeData = function() {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Params";
	cmd.method = "getParamsTreeData";
	cmd.dataSetNo = '';
	cmd.parentId = '';
	cmd.recurse = 2;
	cmd.async = false;
	cmd.success = function(data) {
		if (data.data.length > 0){
			pageParamsObj.initFirstNodeId = data.data[0].id;
		} else {
			pageParamsObj.initFirstNodeId = '';
		}
		pageParamsObj.itemsConvert(data);
		$("#paramsTree").ligerTree(data);
        pageParamsObj.treeManager = $("#paramsTree").ligerGetTreeManager();
	};
	cmd.execute();
}

//---------------------------------
//页面初始化
//---------------------------------
pageParamsObj.initHtml = function() {
	pageParamsObj.checkDataModified = new FieldModifiedChecker($("#paramEditor")[0]);
	$("#btnAdd").click(pageParamsObj.newParams);
	$("#btnDelete").click(pageParamsObj.deleteParams);
	$("#btnSave").click(pageParamsObj.saveParams);
	pageParamsObj.initTreeData();
	if (pageParamsObj.initFirstNodeId == "")
		pageParamsObj.newModule();
	else {
		//选中首节点
    	var selectNodeCallback = function (data)
		    {
		        return data.id == pageParamsObj.initFirstNodeId;
		    };
	    pageParamsObj.treeManager.selectNode(selectNodeCallback);
	    pageParamsObj.operNode = pageParamsObj.treeManager.getSelected();
	    pageParamsObj.nodeSelect(pageParamsObj.operNode);
	}
}

$(document).ready(pageParamsObj.initHtml);