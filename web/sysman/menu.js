var pageMenuObj={};
//---------------------------------
//用于存放树
//---------------------------------
pageMenuObj.manager = null;
//---------------------------------
//用于保存选择的树节点
//---------------------------------
pageMenuObj.operNode=null;
//---------------------------------
//用于存放ID和父ID变量
//---------------------------------
pageMenuObj.dataInfo = {};
//---------------------------------
//展开时异步加载
//---------------------------------
pageMenuObj.moduleComboBoxManager=null;
pageMenuObj.initFirstNodeId = '';

pageMenuObj.onBeforeExpand=function(note){
	if (note.data.children && note.data.children.length == 0){
	    //这里模拟一个加载节点的方法，append方法也用loadData(target,url)代替
	    var parentId=note.data.id;
	   	var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Menu";
		cmd.method = "getMenuData";
		cmd.dataSetNo="query_meun";
		cmd.parentId=parentId;
		cmd.success = function (data) {
			pageMenuObj.manager.append(note.target, data.json);
		};
		cmd.execute();
	}
}

pageMenuObj.onExpand=function(note){
	
}
//---------------------------------
//选择节点
//---------------------------------
pageMenuObj.onClick=function(note){
	pageMenuObj.operNode=note;
	pageMenuObj.initUpdate();
}
//---------------------------------
//新增节点方法
//---------------------------------
pageMenuObj.addTreeItem=function(name,id,pid){
	var node = pageMenuObj.operNode;
    var nodes = [];
    nodes.push({ text:name,pid:pid,id:id,isexpand:false});
    if (node) {//有选择节点时新增
    	if (pid == undefined || pid == null || pid.toLowerCase() == "null") {//一级子节点
    		pageMenuObj.manager.append(null, nodes);
    	} else if (pid == node.data.id) {//子节点
    		pageMenuObj.manager.append(node.target, nodes);
    	} else {//兄弟节点
			var parentNodeTarget = pageMenuObj.manager.getParentTreeItem(node.target);
			if (parentNodeTarget) {
				pageMenuObj.manager.append(parentNodeTarget, nodes);
			} else {
				pageMenuObj.manager.append(null, nodes);
			}
    	}
    } else {//未选择任何节点时新增
    	pageMenuObj.manager.append(null, nodes);
    }
    //选中新加的节点
	var selectNodeCallback = function (data)
	    {
	        return data.id == id;
	    };
    pageMenuObj.manager.selectNode(selectNodeCallback);
    pageMenuObj.operNode = pageMenuObj.manager.getSelected();
    pageMenuObj.onClick(pageMenuObj.operNode);
}
//---------------------------------
//修改节点方法
//---------------------------------
pageMenuObj.updateTreeItem=function(name){
	var node = pageMenuObj.operNode;
    if (node)
    	pageMenuObj.manager.update(node.target, { text: name});
}
//---------------------------------
//删除节点方法
//---------------------------------
pageMenuObj.removeTreeItem=function(){
	var node = pageMenuObj.operNode;
    if (node)
        pageMenuObj.manager.remove(node.target);
    else
        DialogAlert('请先选择节点');
}
//------------------------------------
//新增页面初始化
//------------------------------------
pageMenuObj.initNew=function(addChild){
	var id = pageMenuObj.operNode ? pageMenuObj.operNode.data.id : '';
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Menu";
	cmd.method = "initOperMenu";
	cmd.dataSetNo="select_meun";
	cmd.operType='add';
	cmd.id=id;
	cmd.success = function (data) {
		if (addChild) {
			if (pageMenuObj.operNode)
				pageMenuObj.dataInfo.pid=id;
			else
				pageMenuObj.dataInfo.pid='';
		} else {
			if (pageMenuObj.operNode == null)
				pageMenuObj.dataInfo.pid = '';
			else
				pageMenuObj.dataInfo.pid = pageMenuObj.operNode.data.pid;
		}
		pageMenuObj.dataInfo.id='';
		pageMenuObj.dataInfo.operType='add';
		var maxturn=Utils.getRecordValue(data, 0, "maxturn");//最大排序码加1
		$("#turn").val(maxturn);
		$("#no").val('');
		$("#name").val('');
		$("#module_id").val('');
		pageMenuObj.moduleComboBoxManager.selectValueByTree("");
		$("#txtModule").val("");
		$("#txtModuleParams").val("");
	};
	cmd.execute();
}
//------------------------------------
//修改和点击节点时初始化
//------------------------------------
pageMenuObj.initUpdate=function(){
	var id=pageMenuObj.operNode.data.id;
	if(id!=null){
		var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "Menu";
		cmd.method = "initOperMenu";
		cmd.dataSetNo="select_meun";
		cmd.operType='update';
		cmd.id=id;
		cmd.success = function (data) {
			for(var i=0;data && data.rs && i < data.rs.length; i++){
				var selName=Utils.getRecordValue(data, i, "name");
				var selId=Utils.getRecordValue(data, i, "id");
				var selPid=Utils.getRecordValue(data, i, "parent_id");
				var selTurn=Utils.getRecordValue(data, i, "turn");
				var selModuleId=Utils.getRecordValue(data, i, "module_id");
				var selNo=Utils.getRecordValue(data, i, "no");
				pageMenuObj.dataInfo.id=selId;
				pageMenuObj.dataInfo.pid=selPid;
				pageMenuObj.dataInfo.operType='update';
				$("#no").val(selNo);
				$("#name").val(selName);
				$("#turn").val(selTurn);
				$("#module_id").val(selModuleId);
				$("#txtModuleParams").val(Utils.getRecordValue(data, i, "module_params"));
				var relModuleId = Utils.getRecordValue(data, 0, 'module_id');
				if (relModuleId == null) {
					pageMenuObj.moduleComboBoxManager.selectValueByTree("");
					$("#txtModule").val("");
				}
				else
					pageMenuObj.moduleComboBoxManager.selectValueByTree(Utils.getRecordValue(data, 0, 'module_id'));
			}
		};
		cmd.execute();
	}
}
//-------------------------------
//----------保存数据方法
//--------------------------------
pageMenuObj.operMenu=function(){
	var no=$("#no").val();
	var name=$("#name").val();
	//用于新增sys_menu时给sys_menu表中的module_Id插入的值
	var module_id=$("#txtModuleId").val();
	var turn=$("#turn").val();
	var pid=pageMenuObj.dataInfo.pid;
	var id=pageMenuObj.dataInfo.id;
	if(name==''){
		DialogAlert('名称不能为空');
		return false;
	}

	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Menu";
	cmd.method = "operMenu";
	//从父页面传入的moduleId,用于整个功能的查询SQL
	//新增和修改的判断类型
	cmd.operType=pageMenuObj.dataInfo.operType;
	cmd.id=id;
	cmd.parent_id=pid;
	cmd.turn=turn;
	//和上边的moduleId不同，用于新增sys_menu时给sys_menu表中的module_Id插入的值
	cmd.module_id=module_id;
	cmd.module_params = $("#txtModuleParams").val();//encodeURIComponent
	cmd.no=no;
	cmd.name=name;
	cmd.success = function (data) {
		var state=data.state;
		if(state=='1'){
			for(var i=0;data && data.rs && i < data.rs.length; i++){
				var returnName=Utils.getRecordValue(data, i, "name");
				var returnId=Utils.getRecordValue(data, i, "id");
				var returnPid=Utils.getRecordValue(data, i, "parent_id");
				var operType=pageMenuObj.dataInfo.operType;
				DialogAlert('保存成功');
				//如果是新增操作执行下面语句
				if(operType=='add'){
					pageMenuObj.addTreeItem(returnName,returnId,returnPid);
				}else if(operType=='update'){//如果是修改操作执行下面语句
					pageMenuObj.updateTreeItem(returnName,returnId,returnPid);
					pageMenuObj.initUpdate();
				}
				
			}	
		}else if(state==0){
			var message=data.message;
			DialogAlert('操作出错,出错原因：'+message);
			return false;
		}
	};
	cmd.execute();
}

//---------------------------------
//模块树下拉框初始化
//---------------------------------
pageMenuObj.moduleComboBoxInit = function () {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Menu";
	cmd.method = "getModuleBox";
	cmd.moduleNo="sys_module_manager";
	cmd.dataSetNo="queryModuleRecurseTree";
	cmd.isexpand = 'true';
	cmd.async = false;
	cmd.success = function(data) {
		//pageMenuObj.menuItemsConvert(data);
		var tree = {data: data.data, checkbox: false};
		pageMenuObj.moduleComboBoxManager.setTree(tree);
	};
	cmd.execute();
}

pageMenuObj.addMenu = function (){
	pageMenuObj.initNew(false);
}

pageMenuObj.addChildMenu = function (){
	pageMenuObj.initNew(true);
}

$(document).ready(function () {
	//---------------------------------
	//初始化树
	//---------------------------------
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Menu";
	cmd.method = "getMenuData";
	cmd.dataSetNo="query_meun";
	cmd.success = function (data) {
		$("#tree1").ligerTree({
		    nodeWidth : 200,
		    idFieldName : 'id',
	    	parentIDFieldName : 'pid', 
	    	//展开前事件,可以通过返回false来阻止继续展开,异步加载子节点
		    onBeforeExpand: pageMenuObj.onBeforeExpand,
		    onExpand: pageMenuObj.onExpand,
		    treeLine :true,
		    slide :true,
		    checkbox :false,
		    data:data.json,
		    onClick:pageMenuObj.onClick
		});
		pageMenuObj.manager = $("#tree1").ligerGetTreeManager();
		//$("#layout1").ligerLayout({height:1000,leftWidth: 300,allowLeftCollapse:false,allowRightCollapse:false});
		if (data.json.length > 0){
			pageMenuObj.initFirstNodeId = data.json[0].id;
		} else {
			pageMenuObj.initFirstNodeId = '';
		}
	};
	cmd.execute();

	//---------------------------------
	//删除节点数据
	//---------------------------------
	$("#btnDel").click(function() {
		if (!pageMenuObj.operNode) {
			DialogAlert('请选择要删除的节点');
			return false;
		}
		if (pageMenuObj.operNode.data.isChildren) {
			DialogAlert('此节点包含子节点，不能删除！');
			return false;
		} else {
			var delId = pageMenuObj.operNode.data.id;
			if(delId) {
				DialogConfirm('确定要删除此节点吗?', function (yes) {
					if (yes) {
						var cmd = new Command();
						cmd.module = "sysman";
						cmd.service = "Menu";
						cmd.method = "delMenu";
						cmd.dataSetNo="del_meun";
						cmd.id=delId;
						cmd.success = function (data) {
							var state=data.state;
							if(state=='1'){
								pageMenuObj.removeTreeItem();
								DialogAlert('删除成功');
								pageMenuObj.operNode=null;
							}else{
								var message=data.message;
								DialogAlert('删除失败,出错原因：'+message);
							}
						};
						cmd.execute();
					}
				});
				
			}

		}
	});
	$("#btnNew").click(pageMenuObj.addMenu);
	$("#btnNewChild").click(pageMenuObj.addChildMenu);
	$("#btnSave").click(pageMenuObj.operMenu);
	//-------------------------------
	//加载模块树
	//-------------------------------
	pageMenuObj.moduleComboBoxManager = $("#txtModule").ligerComboBox({valueField: "id", valueFieldID: "txtModuleId", treeLeafOnly: false, isShowCheckBox: true,width:364,selectBoxWidth:364 });
	pageMenuObj.moduleComboBoxInit();
	//选中首节点
	if (pageMenuObj.initFirstNodeId == "")
		pageMenuObj.addMenu();
	else {
    	var selectNodeCallback = function (data)
		    {
		        return data.id == pageMenuObj.initFirstNodeId;
		    };
	    pageMenuObj.manager.selectNode(selectNodeCallback);
	    pageMenuObj.operNode = pageMenuObj.manager.getSelected();
	    pageMenuObj.onClick(pageMenuObj.operNode);
	}
});
