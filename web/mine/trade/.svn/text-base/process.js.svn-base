//---------------------------------
//页页对象
//---------------------------------
var pageProcessObj = {}
//---------------------------------

//---------------------------------
//表格对象
//---------------------------------
pageProcessObj.gridManager = null;
pageProcessObj.timer = null;// 刷新定时器
pageProcessObj.intervalTime = 1000;// 刷新频率为1000ms

//---------------------------------
//启动流程引擎
//---------------------------------
pageProcessObj.startEngine = function() {
	var cmd = new Command();
	cmd.module = "trademine";
	cmd.service = "Trade";
	cmd.method = "startEngine";
	cmd.success = function(data) {
	   DialogAlert('启动完成！');
	   pageProcessObj.queryData();
	}
	cmd.execute();
}


//---------------------------------
//展示正在活动的流程
//---------------------------------
pageProcessObj.initGrid = function(){
	pageProcessObj.gridManager = $("#processGrid").ligerGrid({
		url: approot + '/data?module=trademine&service=Trade&method=activeProccess&moduleId=' + pageProcessObj.moduleId,
		columns: [
			{ display: '流程', name: 'id', width :'25%'},
			{ display: '标的名称', name: 'targetname', width :'25%'},
			{ display: '状态', name: 'state', width :'10%',render:pageProcessObj.stateRender},
			{ display: '阶段', name: 'phase', width :'10%'},
			{ display: '定时任务', name: 'task', width :'30%'}
		],
		rownumbers: true,
		isScroll : true, // 是否滚动
		frozen : false,// 是否固定列
		pageSizeOptions : [10, 20, 30],
		showTitle : false,
		width : '99.8%',
		height: '450',
	});
}

//---------------------------------
//查询引擎状态
//---------------------------------
pageProcessObj.queryEngineState = function(){
	var cmd = new Command();
	cmd.module = "trademine";
	cmd.service = "Trade";
	cmd.method = "getEngineState";
	cmd.success = function(data) {
	  var started = data.started; 
	  if(started){
	  	$("#btnStart").attr("disabled", true);
	  	$("#btnStart").val("定时器已启动");
	  }else{
	  	$("#btnStart").attr("disabled", false);
	  	$("#btnStart").val("启动定时器");
	  }
	}
	cmd.complete = function(xhr) {
		
	}
	cmd.execute();
}

//---------------------------------
//查询按钮事件
//---------------------------------
pageProcessObj.queryData = function () {
	var pagesize=pageProcessObj.gridManager.options.pageSize; //每页条数
	var page=pageProcessObj.gridManager.options.page;//当前页数
	var sortField = pageProcessObj.gridManager.options.sortname;
	var sortDir = pageProcessObj.gridManager.options.sortorder;
	var queryParams = {pagesize: pagesize, page: page,sortField: sortField, sortDir: sortDir,mm:'get'};
	var url = approot + '/data?module=trademine&service=Trade&method=activeProccess&moduleId=' + pageProcessObj.moduleId;
	for(var key in queryParams){
		url = Utils.setParamValue(url, key, queryParams[key], false, '=', '&');
	}
	pageProcessObj.gridManager.loadServerData(queryParams);
	pageProcessObj.gridManager.options.url = url;
	
	pageProcessObj.queryEngineState();
}


pageProcessObj.reload = function(){
	pageProcessObj.gridManager.loadServerData( approot + "/data?module=trademine&service=Trade&method=activeProccess&moduleId=" + pageProcessObj.moduleId);
}

//----------------------------
//渲染状态
//----------------------------
pageProcessObj.stateRender=function (row,rowNamber){
	var state=Number(row.state).toFixed(0);
	var result = state
	if(state==1){
		result =  '运行';
	}else if(state==2){
		result =   '挂起';
	}else if(state==3){
		result =   '停止';
	}else if(state==4){
		result =   '废弃';
	}else if(state==5){
		result =   '完成';
	}else if(state==50){
		result =   '等待完成';
	}
	return result;
}

//---------------------------------
//页面初始化
//---------------------------------
$(document).ready(function(){

	pageProcessObj.initGrid();
	pageProcessObj.queryEngineState();
	//启动流程引擎
	$('#btnStart').click(function() {
		 pageProcessObj.startEngine();
	});
	//刷新
	$('#btnRefalsh').click(pageProcessObj.queryData);
	
	//监控出价
	$('#btnMonitor').click(function() {
		var obj={};
		obj.url=approot+'/mine/trade/tradeMonitor.html';
		obj.feature="dialogWidth=1000px;dialogHeight=625px";
	   	DialogModal(obj);
	});

});