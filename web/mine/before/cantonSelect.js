var pageObj = {};

pageObj.setting = {
	view : {
		selectedMulti : false
	},
	data : {
		key : {
			name : 'text'
		}
	}
};

pageObj.initPage = function() {
	pageObj.tree = $.fn.zTree.init($("#cantonTree"), pageObj.setting);
	var cmd = new Command("sysman", "Canton", "getCantonTreeData");
	cmd.parentId = '441600';
	cmd.recurse = 1; 
	cmd.u = '0001';
	cmd.success = function(data) {
		pageObj.tree.addNodes(null, data.data);
	}
	cmd.execute();

}

pageObj.ok = function() {
	var nodes = pageObj.tree.getSelectedNodes();
	if (nodes.length >= 1) {
		var node = nodes[0];
		var parentNode = node.getParentNode();
		var fullName = node.text;
		var b = "0";
		while (parentNode) {
			fullName = parentNode.text + '>' + fullName;
			parentNode = parentNode.getParentNode();
		}
		var obj = {};
		obj.name = node.text;
		obj.id = node.id;
		obj.fullName = fullName;
		window.returnValue = obj;
		window.close();
	}
}

$(document).ready(function() {
			pageObj.initPage();
			$('#ok').click(pageObj.ok);
		});