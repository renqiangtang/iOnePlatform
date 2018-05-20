//-------------------------
//页面对象
//-------------------------
var pageObj = {};

//-------------------------
//表格对象
//-------------------------
pageObj.gridManager = null;

//-------------------------
//标的状态
//-------------------------
pageObj.target_status = {};
pageObj.target_status['0'] = '终止';
pageObj.target_status['1'] = '中止';
pageObj.target_status['2'] = '成交';
pageObj.target_status['3'] = '流拍';

//-------------------------
//表格对象
//-------------------------
pageObj.gridManager = null;


//----------------
//加载标的列表
//---------------
pageObj.initLandList = function() {
	pageObj.u = getUserId();
    var url = approot +'/data?module=after&service=LandStatisticsQuery&method=queryLandList&goodsType=101&u=' + pageObj.u;
	pageObj.gridManager = $("#target_list").ligerGrid({
		url: url,
		columns: [
			{ display: '标的名称', name: 'name', width: 200},
			{ display: '标的状态', name: 'status', width: 150, render: pageObj.targetStauts},
			{ display: '区域', name: 'canton', width: 150},
			{ display: '交易时间', name: 'end_trans_time', width: 150},
			{ display: '操作', width: 330, render: pageObj.exportOption}
		],
		pagesizeParmName: 'pageRowCount',//每页记录数
		pageParmName: 'pageIndex',//页码数
		sortnameParmName: 't.end_trans_time',//排序列名
		sortorderParmName: 'desc',//排序方向
		isScroll: true,//是否滚动
		frozen: false,//是否固定列
		pageSizeOptions: [10, 20, 30], 
		showTitle: false,
		height: '100%',
		detail: false
	});
}
// -------------------------------------------
// 渲染标的状态列
// -------------------------------------------
pageObj.targetStauts = function(rowdata, rowindex, value){
	var returnValue ="";
	if(rowdata.status=='5'){
		returnValue = pageObj.target_status['2'];
	}else if(rowdata.status=='6'){
		returnValue = pageObj.target_status['3'];
	}else if(rowdata.is_suspend=='1'){
		returnValue = pageObj.target_status['0'];
	}else if(rowdata.is_stop=='1'){
		returnValue = pageObj.target_status['1'];
	}
	return returnValue;
};
// -------------------------------------------
// 渲染操作列
// -------------------------------------------
pageObj.exportOption = function(rowdata, rowindex, value){
	return "<a onclick='pageObj.exportEnlistList(\""+rowdata.id+"\");'>导出报名情况</a>&nbsp;&nbsp;&nbsp;&nbsp;<a onclick='pageObj.exportQuoteList(\""+rowdata.id+"\");'>导出现场竞价记录</a>";
};
// -------------------------------------------
// 获取查询参数
// -------------------------------------------
pageObj.getQueryParams = function () {
	var queryParams = {};
	if ($("#targetName").val())
		queryParams.name = $("#targetName").val(); //标的名称
	if ($("#status").val())
		queryParams.status = $("#status").val();
	if ($("#end_trans_time_1").val())
		queryParams.end_trans_time_1 = $("#end_trans_time_1").val();
	if ($("#end_trans_time_2").val())
		queryParams.end_trans_time_2 = $("#end_trans_time_2").val();
	return queryParams;
}
// -------------------------------------------
// 导出报名
// -------------------------------------------
pageObj.exportEnlistList = function (id) {
	var cmd = new Command("after","LandStatisticsQuery","exportEnlistList");
	cmd.stype="/download";
	cmd.target_id = id;
	cmd.u=pageObj.u;
	cmd.execute();
};

// -------------------------------------------
// 导出竞价记录
// -------------------------------------------
pageObj.exportQuoteList = function (id) {
	var cmd = new Command("after","LandStatisticsQuery","exportQuoteList");
	cmd.stype="/download";
	cmd.target_id = id;
	cmd.u=pageObj.u;
	cmd.execute();
};


// -------------------------------------------
// 重置搜索条件
// -------------------------------------------
pageObj.reset = function() {
	$('#targetName').val('');
	$('#status').val(-1);
	$('#beginTime').val('');
	$('#endTime').val('');
}
//---------------------------------
//查询按钮事件
//---------------------------------
pageObj.queryData = function () {
	var url = approot +'/data?module=after&service=LandStatisticsQuery&method=queryLandList&goodsType=101&u=' + pageObj.u;
	pageObj.gridManager.refresh(url, pageObj.getQueryParams());
}
//----------------
//页面初始化
//---------------
pageObj.init = function(){
	pageObj.initLandList();
};

//----------------
//页面加载完毕调用
//---------------
$(document).ready(function() {
	$("#end_trans_time_1").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd'
		})
	});
	$("#end_trans_time_2").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd'
		})
	});
	pageObj.init();
	$('#btnQuery').click(pageObj.queryData);
	$('#btnReset').click(pageObj.reset);
});