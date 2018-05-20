var pageObj = {};

pageObj.init = function() {
	pageObj.targetId = Utils.getPageValue('targetId');
	pageObj.targetName = Utils.getPageValue('targetName');
	document.title = pageObj.targetName;
	pageObj.url = approot + '/data?module=trademan&service=ProcessManage&method=historyList&id=' + pageObj.targetId;
	pageObj.gridManager = $("#grid").ligerGrid({
		url : pageObj.url,
		columns: [
					{ display: '标的', width :'20%',render:pageObj.renderTargetName},
					{ display: '原状态',  width :'15%',render:pageObj.renderOldValue},
					{ display: '新状态',  width :'15%',render:pageObj.renderNewValue},
					{ display: '操作', name: 'change_cause', width :'15%'},
					{ display: '改变时间', name: 'create_date', width :'25%'}
				],
		isScroll : true,// 是否滚动
		frozen : false,// 是否固定列
		width : '99.8%',
		rownumbers : true,
		usePager:false,
		height : 400
	});
}

pageObj.renderTargetName  = function(){
	return pageObj.targetName;
}

pageObj.renderOldValue  = function(row,number){
	if(row.old_value == '0'){
		return "运行中";
	}else if(row.old_value == '1'){
		return "已中止";
	}
}

pageObj.renderNewValue  = function(row,number){
	if(row.new_value == '0'){
		return "运行中";
	}else if(row.new_value == '1'){
		if(row.change_cause=='终止'){
			return "已终止";
		}else{
			return "已中止";
		}
	}
}

$(document).ready(function() {
	pageObj.init();
});