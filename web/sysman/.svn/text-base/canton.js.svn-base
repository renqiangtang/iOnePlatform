//---------------------------------
//页页对象
//---------------------------------
var pageCantonObj = {}
//---------------------------------
//树管理器引用
//---------------------------------
pageCantonObj.treeManager = null;
//---------------------------------
//树当前操作节点引用
//---------------------------------
pageCantonObj.operNode = null;
//---------------------------------
//初始化界面时首个节点ID
//---------------------------------
pageCantonObj.initFirstNodeId = '';
//---------------------------------
//检查数据是否修改
//---------------------------------
pageCantonObj.checkDataModified = null;

//---------------------------------
//树展开前事件，异步加载下级节点
//---------------------------------
pageCantonObj.nodeBeforeExpand = function (node)
{
	if (node.data.children && node.data.children.length == 0)
    {
        var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Canton";
		cmd.method = "getCantonTreeData";
		cmd.dataSetNo = '';
		cmd.parentId = node.data.id;
		cmd.recurse = 2;
		cmd.success = function(data) {
			if (data.data.length > 0) {
				pageCantonObj.itemsConvert(data);
				pageCantonObj.treeManager.append(node.target, data.data);
			}
		};
		cmd.execute();
    }
}

pageCantonObj.nodeExpand = function (node)
{
	//alert('nodeExpand');
}

//---------------------------------
//树选择节点前
//---------------------------------
pageCantonObj.nodeBeforeSelect = function(node){
	if (pageCantonObj.checkDataModified && pageCantonObj.checkDataModified.checkModified(true) && (pageCantonObj.operNode || pageCantonObj.id == "")) {
		if (!confirm("数据已修改未保存，确认切换行政区节点？\n点击确定按钮将放弃未保存数据"))
			return false;
	}
}

//---------------------------------
//树选择节点
//---------------------------------
pageCantonObj.nodeSelect = function(node){
	if (node && pageCantonObj.id != node.data.id) {
		pageCantonObj.operNode = node;
		pageCantonObj.editCantonData();
	}
}

//---------------------------------
//新增节点
//---------------------------------
pageCantonObj.addTreeNode = function(text, id, parentId){
	var node = pageCantonObj.operNode;
    var nodes = [];
  	nodes.push({ text: text, id: id, parentId: parentId, isexpand: false});
    if (node) {
    	if (parentId == undefined || parentId == null || parentId.toLowerCase() == "null") {//一级子节点
    		pageCantonObj.treeManager.append(null, nodes);
    	} else {
    		pageCantonObj.treeManager.append(node.target, nodes);
    	}
    } else {//未选择任何节点时新增
    	pageCantonObj.treeManager.append(null, nodes);
    }
    //选中新加的节点
	var selectNodeCallback = function (data)
	    {
	        return data.id == id;
	    };
    pageCantonObj.treeManager.selectNode(selectNodeCallback);
    pageCantonObj.operNode = pageCantonObj.treeManager.getSelected();
}

//---------------------------------
//更新节点
//---------------------------------
pageCantonObj.updateTreeNode = function(text){
	var node = pageCantonObj.operNode;
    if (node)
    	pageCantonObj.treeManager.update(node.target, { text: text});
}

//---------------------------------
//树删除节点
//---------------------------------
pageCantonObj.removeTreeNode = function(){
	var node = pageCantonObj.operNode;
    if (node)
        pageCantonObj.treeManager.remove(node.target);
    else
        DialogError('请先选择行政区节点');
}

//---------------------------------
//树节点转换方法，对后台返回的JSON生成自定义内容
//---------------------------------
pageCantonObj.itemsConvert = function (items) {
	items.idFieldName = 'id';
    items.parentIDFieldName = 'parentId'; 
    items.nodeWidth = 200;
    items.checkbox = false;
    items.onBeforeExpand = pageCantonObj.nodeBeforeExpand;
    items.onExpand = pageCantonObj.nodeExpand;
    items.onBeforeSelect = pageCantonObj.nodeBeforeSelect;
    items.onSelect = pageCantonObj.nodeSelect;
}

//---------------------------------
//行政区编辑数据初始化
//---------------------------------
pageCantonObj.editCantonData = function () {
	pageCantonObj.operate = "editCanton";
	$("#btnAdd").show();
	$("#btnDelete").show();
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Canton";
	cmd.method = "getCantonData";
	cmd.dataSetNo = "";
	cmd.id = pageCantonObj.operNode.data.id;
	cmd.success = function (data) {
		pageCantonObj.id = Utils.getRecordValue(data, 0, 'id');
		pageCantonObj.parentId = Utils.getRecordValue(data, 0, 'parent_id');
		if (data && data.rs && data.rs.length > 0) {
			$("#txtName").val(Utils.getRecordValue(data, 0, 'name'));
			$("#txtNo").val(Utils.getRecordValue(data, 0, 'no'));
			$("#txtRemark").val(Utils.getRecordValue(data, 0, 'remark'));
		}
		pageCantonObj.checkDataModified.initFileds();
	};
	cmd.execute();
}

//---------------------------------
//新增行政区
//---------------------------------
pageCantonObj.newCanton = function (){
	pageCantonObj.operate = "addCanton";
	$("#btnAdd").hide();
	$("#btnDelete").hide();
	$("#txtName").val('');
	$("#txtNo").val('');
	$("#txtRemark").val('');
	pageCantonObj.id = '';
	if (pageCantonObj.operNode == null)
		pageCantonObj.parentId = '';
	else
		pageCantonObj.parentId = pageCantonObj.operNode.data.id;
	pageCantonObj.checkDataModified.initFileds();
}

//---------------------------------
//保存行政区
//---------------------------------
pageCantonObj.saveCanton = function (){
	if ($("#txtName").val() == ''){
		DialogAlert('行政区名称不能为空');
		return false;
	}
	if ($("#txtNo").val() == ''){
		DialogAlert('行政区编号不能为空');
		return false;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Canton";
	cmd.method = "updateCantonData";
	cmd.id = pageCantonObj.id;
	cmd.parent_id = pageCantonObj.parentId;
	cmd.no = $("#txtNo").val();
	cmd.name = $("#txtName").val();
	cmd.remark = $("#txtRemark").val();
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			if (pageCantonObj.operate == "addCanton") {
				pageCantonObj.id = data.id;
				pageCantonObj.operate = "editCanton";
				$("#btnAdd").show();
				$("#btnDelete").show();
				pageCantonObj.addTreeNode($("#txtName").val(), data.id, pageCantonObj.parentId);
			}
			else
				pageCantonObj.updateTreeNode($("#txtName").val());
			pageCantonObj.checkDataModified.initFileds();
			DialogAlert('保存行政区成功完成');
		} else {
			DialogError('保存行政区失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

//---------------------------------
//删除行政区
//---------------------------------
pageCantonObj.deleteCanton = function (){
	if (pageCantonObj.id == '') { //正在新增
		if (pageCantonObj.operNode == null)
			pageCantonObj.newCanton();
		else
			pageCantonObj.editCantonData();
		return true;
	}
	if (pageCantonObj.operNode == null){
		DialogWarn('请选择要删除的行政区。');
		return false;
	}
	var message = null;
	if (pageCantonObj.operNode.data.children) {
		if (pageCantonObj.operNode.data.children.length > 0)
			message = '当前行政区节点 ' + pageCantonObj.operNode.data.text + ' 包含子节点，删除它将删除所有下级行政区节点。继续删除？';
		else
			message = '当前行政区节点 ' + pageCantonObj.operNode.data.text + ' 可能包含仍未加载的子节点，删除它将删除所有下级行政区节点。继续删除？';
	} else
		message = '删除当前行政区节点 ' + pageCantonObj.operNode.data.text + '？';
	DialogConfirm(message, function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "Canton";
			cmd.method = "deleteCantonData";
			cmd.id = pageCantonObj.operNode.data.id;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					pageCantonObj.removeTreeNode();
					DialogAlert('删除行政区数据成功完成');
				} else {
					DialogError('删除行政区失败,错误原因：' + data.message);
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
pageCantonObj.initTreeData = function() {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Canton";
	cmd.method = "getCantonTreeData";
	cmd.dataSetNo = '';
	cmd.parentId = '';
	cmd.recurse = 2;
	cmd.async = false;
	cmd.success = function(data) {
		if (data.data.length > 0){
			pageCantonObj.initFirstNodeId = data.data[0].id;
		} else {
			pageCantonObj.initFirstNodeId = '';
		}
		pageCantonObj.itemsConvert(data);
        pageCantonObj.treeManager = $("#cantonTree").ligerTree(data);
	};
	cmd.execute();
}

//---------------------------------
//页面初始化
//---------------------------------
pageCantonObj.initHtml = function() {
	pageCantonObj.checkDataModified = new FieldModifiedChecker($("#cantonEditor")[0]);
	$("#btnAdd input").click(pageCantonObj.newCanton);
	$("#btnDelete input").click(pageCantonObj.deleteCanton);
	$("#btnSave input").click(pageCantonObj.saveCanton);
	pageCantonObj.initTreeData();
	if (pageCantonObj.initFirstNodeId == "")
		pageCantonObj.newModule();
	else {
		//选中首节点
    	var selectNodeCallback = function (data)
		    {
		        return data.id == pageCantonObj.initFirstNodeId;
		    };
	    pageCantonObj.treeManager.selectNode(selectNodeCallback);
	    pageCantonObj.operNode = pageCantonObj.treeManager.getSelected();
	    pageCantonObj.nodeSelect(pageCantonObj.operNode);
	}
}

$(document).ready(pageCantonObj.initHtml);