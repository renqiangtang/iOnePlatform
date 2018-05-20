var pageObj = {};
pageObj.urlBefore = approot + '/data?module=after&service=Statistics&method=tradeBeforeStatistics&targetId=' + Utils.getPageValue('targetId');
pageObj.urlMiddle = approot + '/data?module=after&service=Statistics&method=tradeMiddleStatistics&targetId=' + Utils.getPageValue('targetId');
pageObj.urlAfter = approot + '/data?module=after&service=Statistics&method=tradeAfterStatistics&targetId=' + Utils.getPageValue('targetId');

pageObj.initBefore = function() {
	var cols = [];
	cols.push({
		display : '标的',
		name : 'no',
		align : 'left',
		render:function(row,rowNumber){
			return '<a href="javascript:pageObj.viewTarget(\''+row.id+'\')">'+row.no+'</a>';
		},
		width : 200
	});
	cols.push({
		display : '竞买人',
		name : 'bidder_name',
		align : 'left',
		width : 200
	});
	cols.push({
		display : '保证金应交金额',
		name : 'earnest_money',
		align : 'left',
		render : function(row, rowNumber) {
			return comObj.cf({
				unit : row.unit,
				flag : row.currency,
				amount : row.earnest_money
			});
		},
		width : 150
	});
	cols.push({
		display : '保证金实交金额',
		name : 'targetname',
		align : 'left',
		render : function(row, rowNumber) {
			return comObj.cf({
				unit : row.unit,
				flag : row.currency,
				amount : row.amount
			});
		},
		width : 150
	});
	cols.push({
		display : '是否通过资格审核',
		name : 'confirmed',
		align : 'center',
		render : function(row, rowNumber) {
			if (row.status >= 1)
				return '已通过';
			else 
				return '未通过';
		},
		width : 150
	});
	cols.push({
		display : '竞价资格开通情况',
		name : 'status',
		align : 'center',
		render : function(row, rowNumber) {
			if (row.status == 0)
				return '未开通';
			if (row.status == 1)
				return '已开通';
			if (row.status == 2)
				return '未竞得';
			if (row.status == 4)
				return '已竞得';
			return '-';
		},
		width : 150
	});
	cols.push({
		display : '申请时间',
		name : 'apply_date',
		align : 'center',
		width : 200
	});
	pageObj.gridBefore = $("#gridBefore").ligerGrid({
		url : pageObj.urlBefore,
		columns : cols,
		isScroll : true,// 是否滚动
		pageSizeOptions : [ 10, 20, 30 ],
		showTitle : false,
		width : '99.8%',
		checkbox : false,
		usePager : false,
		rownumbers: true,
		height : 460
	});

}

pageObj.initMiddle = function() {
	var cols = [];
	cols.push({
		display : '标的',
		name : 'no',
		align : 'left',
		render:function(row,rowNumber){
			return '<a href="javascript:pageObj.viewTarget(\''+row.id+'\')">'+row.no+'</a>';
		},
		width : 200
	});
	cols.push({
		display : '交易类型',
		name : 'business_type_name',
		align : 'center',
		width : 200
	});
	cols.push({
		display : '交易类型',
		name : 'trans_type_label',
		align : 'center',
		width : 200
	});
	cols.push({
		display : '挂牌起止时间',
		name : 'begin_focus_time',
		align : 'center',
		render:function(row,rowNumber){
			if(row.begin_focus_time && row.end_focus_time)
				return  row.begin_focus_time+' 至   '+row.end_focus_time;
			return "";
		},
		width : 300
	});
	cols.push({
		display : '起始价(万元)',
		name : 'begin_price',
		align : 'right',
		render : function(row, rowNumber) {
			var m = row.begin_price / 10000;
			return parseFloat(m.toFixed(4));
		},
		width : 200
	});
	cols.push({
		display : '当前最高报价(万元)',
		name : 'trans_price',
		align : 'right',
		render : function(row, rowNumber) {
			var m = row.current_price / 10000;
			return parseFloat(m.toFixed(4));
		},
		width : 200
	});
	cols.push({
		display : '状态',
		name : 'status',
		align : 'center',
		render : function(row, rowNumber) {
			return comObj.targetStatusObj['' + row.status];
		},
		width : 100
	});
	cols.push({
		display : '报名人数',
		name : 'license_num',
		align : 'center',
		width : 200
	});
	pageObj.gridMiddle = $("#gridMiddle").ligerGrid({
		url : pageObj.urlMiddle,
		columns : cols,
		isScroll : true,// 是否滚动
		pageSizeOptions : [ 10, 20, 30 ],
		showTitle : false,
		width : '99.8%',
		usePager : false,
		checkbox : false,
		rownumbers : true,
		height : 460
	});
}

pageObj.initAfter = function() {
	var cols = [];
	cols.push({
		display : '标的',
		name : 'no',
		align : 'left',
		render:function(row,rowNumber){
			return '<a href="javascript:pageObj.viewTarget(\''+row.id+'\')">'+row.no+'</a>';
		},
		width : 200
	});
	cols.push({
		display : '交易类型',
		name : 'business_type_name',
		align : 'center',
		width : 150
	});
	cols.push({
		display : '交易类型',
		name : 'trans_type_label',
		align : 'center',
		width : 150
	});
	cols.push({
		display : '挂牌起止时间',
		render:function(row,rowNumber){
			if(row.begin_focus_time && row.end_focus_time)
				return row.begin_focus_time+' 至 '+row.end_focus_time;
			return "";
		},
		align : 'left',
		width : 300
	});
	cols.push({
		display : '起始价(万元)',
		name : 'noticetime',
		align : 'right',
		render:function(row,rowNumber){
			var m=row.begin_price/10000;
			return parseFloat(m.toFixed(4)); 
		},
		width : 200
	});
	cols.push({
		display : '成交价(万元)',
		align : 'right',
		render:function(row,rowNumber){
			var m=row.trans_price/10000;
			return parseFloat(m.toFixed(4)); 
		},
		width : 200
	});
	cols.push({
		display : '成交时间',
		name : 'end_trans_time',
		align : 'left',
		width : 150
	});
	cols.push({
		display : '标的状态',
		align : 'center',
		width : 100,
		render : function(row, rowNumber) {
			return comObj.targetStatusObj['' + row.status];
		}
	});
	cols.push({
		display : '竞得人',
		name : 'bidder_name',
		align : 'center',
		width : 200
	});
	cols.push({
		display : '操作',
		name : 'transtype',
		align : 'center',
		render:function(row,rowNumber){
			return '<a href="javascript:pageObj.showOfferLogList(\''+row.id+'\')">出价记录</a>';
		},
		width : 150
	});
	pageObj.gridAfter = $("#gridAfter").ligerGrid({
		url : pageObj.urlAfter,
		columns : cols,
		isScroll : true,// 是否滚动
		pageSizeOptions : [ 10, 20, 30 ],
		showTitle : false,
		width : '99.8%',
		usePager : false,
		checkbox : false,
		rownumbers : true,
		height : 460
	});
}

// ---------------------------------
// 查看历史记录
// ---------------------------------
pageObj.showOfferLogList = function(targetId){
	DialogModal({url: approot + '/land/trade/offerLogList.html',
	param: {targetId: targetId},
	feature: "dialogWidth=850px;dialogHeight=505px"});
}

// ---------------------------------
// 查看标的详情
// ---------------------------------
pageObj.viewTarget = function(targetId) {
	DialogModal({url: approot + '/land/trade/viewTarget.html',
		param: {targetId: targetId},
		feature: "dialogWidth=1050px;dialogHeight=505px"});
}

pageObj.afterSelectTabItem = function(tabId){
	if(tabId=="tabitem1"){
		pageObj.initBefore();
	}else if(tabId=="tabitem2"){
		pageObj.initMiddle();
	}else if(tabId=="tabitem3"){
		pageObj.initAfter();
	}else{
		alert("出错了，请联系管理员");
	}
}

$(document).ready(function() {
	$("#navtab1").ligerTab({
		onAfterSelectTabItem: pageObj.afterSelectTabItem
	});
	pageObj.initBefore();
});