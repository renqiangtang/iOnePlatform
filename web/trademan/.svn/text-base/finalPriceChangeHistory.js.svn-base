var pageObj = {};

pageObj.init = function() {
	pageObj.multiId = Utils.getPageValue('multiId');
	pageObj.quotaName = Utils.getPageValue('quotaName');
	document.title = pageObj.quotaName;
	pageObj.url = approot + '/data?module=trademan&service=PriceChange&method=finalChangeLogList&multiId=' + pageObj.multiId;
	pageObj.gridManager = $("#grid").ligerGrid({
		url : pageObj.url,
		columns : [{
			display : '提交人',
			name : 'create_user',
			width : 100
		}, {
			display : '提交日期',
			name : 'create_date',
			width : 120
		},
		{
			display : '审批人',
			name : 'check_user',
			width : 100
		}, {
			display : '审批日期',
			name : 'check_date',
			width:120
		}, {
			display : '单位',
			name : 'unit',
			width:120
		}, {
			display:'修改前封顶价',
			name:'old_value',
			width:120,
			render:function(row,rowNumber){
				var value=row['old_value'];
				value=parseFloat(value);
				if(value && row.unit == '万元')
					return value.toWanYuan();
				else
					return value;
			}
		},
		{
			display:'修改后封顶价',
			name:'new_value',
			width:120,
			render:function(row,rowNumber){
				var value=row['new_value'];
				value=parseFloat(value);
				if(value && row.unit == '万元')
					return value.toWanYuan();
				else
					return value;
			}
		},
		{
			display : '状态',
			width : 100,
			render : function(row, rowNumber) {
				return comObj.priceStatus[row.status];
			}
		} ],
		isScroll : true,// 是否滚动
		frozen : false,// 是否固定列
		width : '99.8%',
		rownumbers : true,
		height : 400
	});
}

$(document).ready(function() {
	pageObj.init();
});