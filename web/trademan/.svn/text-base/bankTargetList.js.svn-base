//---------------------------------
//页对象
//---------------------------------
var pageObj = {}
// ---------------------------------

// ---------------------------------
// 表格对象
// ---------------------------------
pageObj.grid = null;
pageObj.timer = null;// 刷新定时器
pageObj.intervalTime = 1000;// 刷新频率为1000ms




// ---------------------------------
// 展示正在活动的流程
// ---------------------------------
pageObj.initGrid = function(){
	pageObj.grid = $("#gridDiv").ligerGrid({
		url: approot + '/data?module=bank&service=Bank&method=bankTargetList&goodsType='+pageObj.goodsType,
		columns: [
		    { display: '标的编号', name: 'no', width :'15%'},
		    { display: '标的名称', name: 'name', width :'15%'},
		    { display: '交易类型', name: 'business_type', width :'10%',render:pageObj.renderBusiness},
			{ display: '交易类型', name: 'trans_type_label', width :'12%'},
			{ display: '公告名称', name: 'notice_name', width :'13%'},
			{ display: '公告开始时间', name: 'begin_notice_time', width :'15%'},
			{ display: '状态', name: 'status', width :'5%',render:pageObj.renderStatus},
			{ display: '未销户', name: 'not_feed', width :'5%'},
			{ display: '未子转总', name: 'not_submit', width :'5%'}
		],
		rownumbers: true,
		isScroll : true, // 是否滚动
		frozen : false,// 是否固定列
		showTitle : false,
		onDblClickRow : pageObj.showDetail,
		pageSizeOptions: [10, 20, 30],
		usePager : true,
		width : '99.8%',
		height:400
	});
}



pageObj.renderBusiness = function(row, rowNamber) {
	return comObj.business_type_rel[row.business_type];
}


pageObj.renderStatus = function(row, rowNamber) {
	var statue = row.status;
	if(pageObj.goodsType == "501"){
		return comObj.plowTargetStatusObj[statue];
	}else{
		return comObj.targetStatusObj[statue];
	}
}

pageObj.renderDeal = function(row, rowNamber) {
	return "<a href='javascript:pageObj.feedBackTarget(\""+row.id + "\")'>发数据到审批</a>";
	
}

pageObj.feedBackTarget = function(id) {
	DialogConfirm('您确定要 发送标的数据到审批系统?', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "bank";
			cmd.service = "Bank";
			cmd.method = "feedBackTarget";
			cmd.targetId = id;
			cmd.success = function(data) {
			   DialogAlert(data.message);
			}
			cmd.execute();
		}
	});
}


pageObj.showDetail = function(row, rowNamber){
	var id = row.id;
	var status = row.status;
	var no = row.no;
	var goodsType = pageObj.goodsType;
	var dialogWidth = 900;
	var dialogHeight = 600;
	var url = approot + '/trademan/bankAccountBillList.html'
	var param = {win:window,id:id,status:status,no:no};
	DialogModal({url: url,param:param,
	feature: "dialogWidth=" + dialogWidth + "px;dialogHeight=" + dialogHeight + "px;location=no;status=no;center=yes;scroll=no;edge=sunken"});
}


// ---------------------------------
// 查询按钮事件
// ---------------------------------
pageObj.queryData = function () {
	var no = $("#targetNo").val();
	var name = $("#targetName").val();
	var status = $("#targetStatus").val();
	var queryParams = {no:no , name:name , status:status };
	var url = approot + '/data?module=bank&service=Bank&method=bankTargetList&goodsType='+pageObj.goodsType;
	pageObj.grid.refresh(url, queryParams);
}


// ---------------------------------
// 页面初始化
// ---------------------------------
$(document).ready(function(){
	pageObj.goodsType = Utils.getPageValue('goodsType');
	pageObj.initGrid();
	
	$('#btnSearch').click(function() {
		pageObj.queryData();
	});
});