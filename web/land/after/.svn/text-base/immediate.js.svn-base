var pageObj = {};
pageObj.url = approot + '/data?module=after&service=Statistics&method=immediateStatistics&goodsType=' + Utils.getPageValue('goodsType');

pageObj.init = function() {
	var cols = [];
	cols.push({
		display : '宗地编号',
		name : 'no',
		align : 'left',
		frozen: true ,
		width : 200
	});
	cols.push({
		display : '地块名称',
		name : 'address',
		align : 'left',
		totalSummary : {
			render : function(suminf, column, cell) {
				return '合计';
			},
			align : 'center'
		},
		width : 200
	});
	cols.push({
		display : '面积(平方米)',
		name : 'goods_size',
		align : 'right',
		type : 'float',
		totalSummary : {
			render : function(suminf, column, cell) {
				return '<div style="font-weight:bold;color:blue">' + suminf.sum.toFixed(2) + '</div>';
			},
			align : 'right'
		},
		width : 100
	});
//	cols.push({
//		display : '面积(亩)',
//		name : 'goods_size_mu',
//		align : 'right',
//		type : 'float',
//		totalSummary : {
//			render : function(suminf, column, cell) {
//				return '<div style="font-weight:bold;color:blue">' + suminf.sum.toFixed(2) + '</div>';
//			},
//			align : 'right'
//		},
//		width : 100
//	});
	cols.push({
		display : '起始价(万元)',
		name : 'begin_price',
		align : 'right',
		type : 'float',
		totalSummary : {
			render : function(suminf, column, cell) {
				return '<div style="font-weight:bold;color:blue">' + suminf.sum.toFixed(2) + '</div>';
			},
			align : 'right'
		},
		width : 100
	});
	cols.push({
		display : '成交价(万元)',
		name : 'trans_price',
		align : 'right',
		type : 'float',
		totalSummary : {
			render : function(suminf, column, cell) {
				return '<div style="font-weight:bold;color:blue">' + suminf.sum.toFixed(2) + '</div>';
			},
			align : 'right'
		},
		width : 100
	});
	cols.push({
		display : '单价(万元/亩)',
		name : 'unit_price',
		align : 'right',
		type : 'float',
		totalSummary : {
			render : function(suminf, column, cell) {
				return '<div style="font-weight:bold;color:blue">' + suminf.sum.toFixed(2) + '</div>';
			},
			align : 'right'
		},
		width : 100
	});
	cols.push({
		display : '溢价(万元)',
		name : 'more_price',
		align : 'right',
		type : 'float',
		totalSummary : {
			render : function(suminf, column, cell) {
				return '<div style="font-weight:bold;color:blue">' + suminf.sum.toFixed(2) + '</div>';
			},
			align : 'right'
		},
		width : 100
	});
	cols.push({
		display : '溢价率%',
		name : 'more_price_rate',
		align : 'right',
		type : 'float',
		totalSummary : {
			render : function(suminf, column, cell) {
				return '<div style="font-weight:bold;color:blue">' + suminf.sum.toFixed(2) + '</div>';
			},
			align : 'right'
		},
		width : 100
	});
	cols.push({
		display : '竞得人',
		name : 'bidder_name',
		align : 'left',
		width : 200
	});
	pageObj.grid = $("#grid").ligerGrid({
		url : pageObj.url,
		columns : cols,
		isScroll : true,// 是否滚动
		usePager : false,
		pageSizeOptions : [ 10, 20, 30 ],
		showTitle : false,
		checkbox : false,
		rownumbers:true,
		height : 400
	});
}

pageObj.query = function() {
	var obj = {};
	obj.bidderName = $('#bidderName').val().trim();
	obj.targetNo = $('#targetNo').val().trim();
	obj.businessType =$('#businessType').val().trim();
	obj.beginTime = $('#beginTime').val().trim();
	obj.endTime = $('#endTime').val().trim();
	pageObj.grid.refresh(pageObj.url, obj);
}

pageObj.reset = function() {
	$('#bidderName').val('');
	$('#targetNo').val('');
	$('#businessType').val('');
	$('#beginTime').val('');
	$('#endTime').val('');
}

$(document).ready(function() {
	pageObj.goodsType = Utils.getPageValue('goodsType');
	pageObj.u = getUserId();
	var cmd = new Command("after", "Statistics", "type");
	cmd.table = "trans_business_type_rel where goods_type_id = '" + pageObj.goodsType + "' ";
	var html = '';
	cmd.success = function(data) {
		var objs = data.list;
		html += '<option value="">--请选择--</option>';
		$(objs).each(function(i, ele) {
			html += '<option value="' + ele.id + '">' + ele.name + '</option>';
		});
		$('#businessType').html(html);
	}
	cmd.execute();
	
	$("#beginTime").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd'
		})
	});
	$("#endTime").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd'
		})
	});
	$('#btnQuery').click(pageObj.query);
	$('#btnReset').click(pageObj.reset);
	pageObj.init();
});