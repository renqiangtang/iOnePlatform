var pageObj = {};

pageObj.init = function() {
	pageObj.targetId = Utils.getPageValue('targetId');
	pageObj.targetName = Utils.getPageValue('targetName');
	document.title = pageObj.targetName;
	pageObj.url = approot + '/data?module=trademan&service=PriceChange&method=lowestChangeLogList&targetId=' + pageObj.targetId;
	pageObj.gridManager = $("#grid").ligerGrid({
		url : pageObj.url,
		columns : [ {
			display : '提交人',
			name : 'create_user',
			width : 150
		},{
			display : '提交日期',
			name : 'create_date',
			width : 150
		},{
			display : '审批人',
			name : 'check_user',
			width : 150
		}, {
			display : '审批日期',
			name : 'check_date',
			width:150
		}, {
			display:'修改前底价(万元)',
			name:'old_value',
			width:120,
			render:function(row,rowNumber){
				var value=row['old_value'];
				value=parseFloat(value);
				if(value)
					return value.toWanYuan();
				else
					return value;
			}
		},
		{
			display:'修改后底价(万元)',
			name:'new_value',
			width:120,
			render:function(row,rowNumber){
				var value=row['new_value'];
				value=parseFloat(value);
				if(value)
					return value.toWanYuan();
				else
					return value;
			}
		},
		{
			display : '状态',
			width : '20%',
			render : function(row, rowNumber) {
				return comObj.priceStatus[row.status];
			}
		} ],
		isScroll : true,// 是否滚动
		width : '99.8%',
		rownumbers : true,
		height : 400
	});
}

$(document).ready(function() {
	pageObj.init();
});