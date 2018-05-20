var logPageObj={};

//--------------------------------
//重新加载表格
//--------------------------------
logPageObj.reloadGrid=function(){
	var gridData = {};
	gridData.Rows = [];
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Log";
	cmd.method = "getFiles";
	cmd.beginDate=$("#beginDate").val();
	cmd.endDate=$("#endDate").val();
	cmd.success=function(data){
		gridData.Rows=data.rows;
		logPageObj.manageGrid.loadData(gridData);
		$('#now').html(data.now);
	}
	if(cmd.beginDate&&cmd.endDate){
		logPageObj.manageGrid.loadData(gridData);
		$('#now').html();
		cmd.execute();
	}else
		alert("时间段不对！");
}

//--------------------------------
//初始化页面
//--------------------------------
$(document).ready(function () {
	$("#beginDate").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'})});
	$("#endDate").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'})});
	$("#btnQuery").click(logPageObj.reloadGrid);
	logPageObj.manageGrid = $("#logGrid").ligerGrid({
		columns:[   { display: '日志文件名',name:'name',isSort:false,width:'60%',align:'center'},
	                { display: '大小(MB)',name:'length',isSort:false,width:'20%',align:'center'},
	                { display: '操作',name:'op',isSort:false,width:'20%',align:'center'}
                ],
		isScroll: true, //是否滚动
		frozen:false,//是否固定列
		usePager : false, 
		showTitle: false,
		fixedCellHeight :false,
		width:'99.8%',
		height:485
	});
});