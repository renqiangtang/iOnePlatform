var pageObj = {};

pageObj.init = function() {
	$('#targetName').html(pageObj.targetName+" ("+pageObj.targetStatus+")");
	
	var cols = [];
	cols.push({display : 'id',	name : 'rel_id',align : 'center',width : 10 ,hide:1 });
	cols.push({display : '编号',	name : 'no',align : 'center',width : 150});
	cols.push({display : '位置',	name : 'field0',align : 'left',width :150});
	cols.push({display : '用途',	name : 'goods_use',align : 'left',width : 140});
	cols.push({display : '比例(合计100)',name : 'scale',align : 'left',width : 100 , editor : { type : 'float'}});
	pageObj.grid = $("#grid").ligerGrid({
				url : approot + '/data?module=after&service=Statistics&method=targetGoodsList&targetId='+pageObj.targetId,
				columns : cols,
				isScroll : true,// 是否滚动
				showTitle : false,
				width : '99.8%',
				checkbox : false,
				usePager : false,
				enabledEdit : true,
				rownumbers : true,
				height : 300
			});
}

//=========================================
//下载按比例拆分的竞价记录
//=========================================
pageObj.downLog = function(){
	var arr = pageObj.grid.getData(null, true);
	if (arr.length == 0) {
		DialogAlert('交易物不能为空！');
		return false;
	}
	var allScale = 0;
	for (var i = 0; i < arr.length; i++) {
		var scale = arr[i].scale;
		if(Number(scale)<0){
			DialogAlert('比例不能为负数！');
			return false;
		}
		allScale = allScale.add(Number(scale));
	}
	if(allScale==100){
		var cmd = new Command("after", "Statistics", "setScale");
		cmd.targetId = pageObj.targetId;
		cmd.scale = JSON.stringify(pageObj.grid.getData(null, true));
		cmd.success = function(data) {
			if(data.state){
				window.location.href=approot + '/download?module=after&service=Statistics&method=downLog&targetId='+pageObj.targetId;
			}else{
				DialogAlert(data.message);
			}
		};
		cmd.execute();
	}else{
		DialogAlert('交易物比例合计应该为100！');
		return false;
	}
	
}


$(document).ready(function() {
		pageObj.targetId = Utils.getPageValue("targetId");
		pageObj.targetName = Utils.getPageValue("targetName");
		pageObj.targetStatus = Utils.getPageValue("targetStatus");
		pageObj.init();
		$('#btnDownLog').click(pageObj.downLog);
});