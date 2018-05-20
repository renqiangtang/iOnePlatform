var pageObj = {};

// -------------------------------------------
// 重置
// -------------------------------------------
pageObj.reset = function() {
	$('#targetName').val('');
	$('#transType').val(-1);
	$('#status').val(-1);
	$('#beginTime').val('');
	$('#endTime').val('');
}

// -------------------------------------------
// 下载出价记录
// -------------------------------------------
pageObj.downLog = function() {
	var selectedRow = pageObj.grid.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要下载出价记录的标的。');
		return false;
	}
	if (selectedRow.target_status < 5) {
		DialogWarn('只有交易结束后的标的才有出价记录。');
		return false;
	}
	var downObj = {};
	downObj.url = "goodsScaleSet.html";
	downObj.param = {
		targetId : selectedRow.targetid,
		targetName : selectedRow.targetname,
		targetStatus : selectedRow.status
	};
	downObj.feature = "dialogWidth=600px;dialogHeight=400px";
	var returnValue = DialogModal(downObj);
	// if (returnValue){
	// pageObj.query();
	// }
}

pageObj.query = function() {
	var url = approot + '/data?module=after&service=Statistics&method=targetStatistics';
	var obj = {};
	obj.targetName = $('#targetName').val().trim();
	obj.transType = $('#transType').val().trim();
	obj.status = $('#status').val().trim();
	obj.beginTime = $('#beginTime').val().trim();
	obj.endTime = $('#endTime').val().trim();
	obj.u = pageObj.u;
	obj.goodsType = pageObj.goodsType;
	pageObj.grid.refresh(url, obj);
}

pageObj.init = function() {
	pageObj.goodsType = Utils.getPageValue('goodsType');
	pageObj.u = getUserId();
	var cmd = new Command("after", "Statistics", "type");
	cmd.table = "trans_business_type_rel where goods_type_id = '" + pageObj.goodsType + "' ";
	var html = '';
	cmd.success = function(data) {
		var objs = data.list;
		html += '<option value="-1">--请选择--</option>';
		$(objs).each(function(i, ele) {
			html += '<option value="' + ele.id + '">' + ele.name + '</option>';
		});
		$('#transType').html(html);
	}
	cmd.execute();
	//
	html = '<option value="-1">--请选择--</option>';
	for ( var pro in comObj.targetStatusObj) {
		var v = comObj.targetStatusObj[pro];
		if(pro<=7){
			html += '<option value="' + pro + '">' + v + '</option>';
		}
	}
	$('#status').html(html);
	//
	var goodsUse = pageObj.goodsType == '101' ? "用途" : "矿种";
	var cols = [];
	cols.push({
		display : '标的',
		name : 'targetname',
		render:function(row,rowNumber){
			return '<a href="javascript:pageObj.allStatistics(\''+row.targetid+'\')">'+row.targetname+'</a>';
		},
		align : 'left',
		width : 200
	});
	cols.push({
		display : '位置',
		name : 'location',
		align : 'left',
		width : 200
	});
//	cols.push({
//		display : '面积(平方米)',
//		name : 'area',
//		align : 'left',
//		width : 100
//	});
	cols.push({
		display : goodsUse,
		name : 'use',
		align : 'left',
		width : 100
	});
	cols.push({
		display : '公告起止时间',
		name : 'noticetime',
		align : 'left',
		width : 200
	});
	cols.push({
		display : '状态',
		name : 'status',
		align : 'left',
		width : 100,
		render : pageObj.renderStatus
	});
	cols.push({
		display : '成交时间',
		name : 'endtranstime',
		align : 'left',
		width : 150,
		render : function(r){
			if(r.status==6 || r.status==7 || r.status==8){
				return "";
			}else{
				return r.endtranstime;
			}
		}
	});
	cols.push({
		display : '成交价',
		align : 'right',
		width : 200,
		render : function(r){
			if(r.status==6 || r.status==7 || r.status==8){
				return "";
			}else{
				return r.price;
			}
		}
	});
	cols.push({
		display : '竞得人',
		name : 'biddername',
		align : 'left',
		width : 200,
		render : function(r){
			if(r.status==6 || r.status==7 || r.status==8){
				return "";
			}else{
				return r.biddername;
			}
		}
	});
	cols.push({
		display : '交易类型',
		name : 'transtype',
		align : 'left',
		width : 130
	});
	pageObj.grid = $("#grid").ligerGrid({
		url : approot + '/data?module=after&service=Statistics&method=targetStatistics&goodsType=' + pageObj.goodsType + '&u=' + pageObj.u,
		columns : cols,
		isScroll : true,// 是否滚动
		pageSizeOptions : [ 10, 20, 30 ],
		showTitle : false,
		rownumbers : true,
		checkbox : false,
		height : 400
	});
}

pageObj.allStatistics=function(targetId){
	DialogModal({url: approot + '/land/after/allStatistics.html',
		param: {targetId: targetId},
		feature: "dialogWidth=1050px;dialogHeight=505px"});
}

pageObj.renderPrice = function(row, rowNumber) {
	return comObj.cf({
		unit : row.unit,
		amount : row.price
	});
}

pageObj.renderStatus = function(row, rowNumber) {
	var status = row.status;
	var is_stop = row.is_stop;
	var is_suspend = row.is_suspend;
	status = comObj.targetStatusObj[status];
	if (is_stop == 1) {
		status = status + '<font color="red">(终止)</font>';
	} else if (is_suspend == 1) {
		status = status + '<font color="red">(中止)</font>';
	}
	return status;
}

$(document).ready(function() {
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
	pageObj.init();
	$('#btnQuery').click(pageObj.query);
	$('#btnReset').click(pageObj.reset);
	//$('#btnDownLog').click(pageObj.downLog);
});