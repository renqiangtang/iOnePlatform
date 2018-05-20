//---------------------------------
//页对象
//---------------------------------
var pageObj = {}
pageObj.organId = null;
pageObj.gridManager = null;





// ---------------------------------
// 交易类型列表
// ---------------------------------
pageObj.initGrid = function(){
	pageObj.gridManager = $("#organQualificationGrid").ligerGrid({
		url: approot + '/data?module=sysman&service=Organ&method=getOrganQualification&organId='+pageObj.organId,
		columns: [
            { display: '编号', name: 'id', width :'20%'},
		    { display: '交易物编号', name: 'goods_type_id', width :'20%'},
			{ display: '交易类型编号', name: 'business_type_id', width :'20%'},
			{ display: '名称', name: 'name', width :'20%'}
		],
		usePager: false,
		checkbox: true,
		rownumbers: true,
		isScroll : true, // 是否滚动
		frozen : false,// 是否固定列
		showTitle : false,
		width : '99.8%',
		alternatingRow:false,
		isChecked:pageObj.initChecked
	});
}

// ---------------------------------
// 初始化选择
// ---------------------------------
pageObj.initChecked = function(rowData){
	if(rowData.organ_id){
		return true;
	}else{
		return false;
	}
}


// ---------------------------------
// 刷新页面
// ---------------------------------
pageObj.queryData = function () {
	if($(".l-checked")){
		$(".l-checked").attr('class', 'l-grid-hd-row');// 去掉标题栏的复选框选择
	}
	var url = approot + '/data?module=sysman&service=Organ&method=getOrganQualification&organId='+pageObj.organId;
	var queryParams = {};
	pageObj.gridManager.refresh(url,queryParams);
}



// ---------------------------------
// 选择按钮动作
// ---------------------------------
pageObj.saveData = function(){
	var manager = $("#organQualificationGrid").ligerGetGridManager();
	var selectedRow = manager.getSelectedRows();
	var ids="";
	for(var i=0;i<selectedRow.length;i++){
		if(i==selectedRow.length-1){
			ids=ids+selectedRow[i].id;
		}else{
			ids=ids+selectedRow[i].id+",";
		}
	}
	DialogConfirm(ids?'您要选择这些交易类型?':'你要删除所有的交易类型?', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "Organ";
			cmd.method = "updateQualification";
			cmd.ids = ids;
			cmd.organId = pageObj.organId;
			cmd.userId = getUserId();
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					DialogSuccess(ids?'选择交易类型成功':'删除交易类型成功');
					pageObj.queryData();
				} else {
					DialogError(ids?'选择交易类型失败':'删除交易类型失败');
					return false;
				}
			};
			cmd.execute();
		}
	});
}



// ---------------------------------
// 页面初始化
// ---------------------------------
$(document).ready(function(){
	pageObj.organId = (window.dialogArguments && window.dialogArguments.organId)||Utils.getUrlParamValue(document.location.href, "organId");
	pageObj.initGrid();
	$("#saveBtn").click(pageObj.saveData);
	$("#queryBtn").click(pageObj.queryData);
});
