//---------------------------------
//页对象
//---------------------------------
var pageObj = {}
pageObj.organId = null;
pageObj.gridManager = null;
// ---------------------------------
// 展示正在活动的流程
// ---------------------------------
pageObj.initGrid = function() {
	pageObj.gridManager = $("#bankSelectGrid").ligerGrid({
		url : approot + '/data?module=trademan&service=BankManage&method=otherBankList&organId=' + pageObj.organId,
		columns : [ {
			display : '编号',
			name : 'no',
			width : '20%'
		}, {
			display : '银行名称',
			name : 'name',
			width : '20%'
		}, {
			display : '所属单位',
			name : 'organ_name',
			width : '20%'
		}, {
			display : '地区',
			name : 'canton_name',
			width : '20%'
		} ],
		groupColumnName : 'organ_name',
		groupColumnDisplay : '单位',
		checkbox : true,
		usePager : false,
		rownumbers : false,
		isScroll : true, // 是否滚动
		frozen : false,// 是否固定列
		showTitle : false,
		width : '99.8%'
	});
}
// ---------------------------------
// 刷新页面
// ---------------------------------
pageObj.queryData = function() {
	var userId = getUserId();
	var queryParams = {
		u : userId,
		organId : pageObj.organId
	};
	var url = approot + '/data?module=trademan&service=BankManage&method=otherBankList';
	pageObj.gridManager.refresh(url, queryParams);
}
// ---------------------------------
// 选择按钮动作
// ---------------------------------
pageObj.selectBank = function() {
	var manager = $("#bankSelectGrid").ligerGetGridManager();
	var selectedRow = manager.getSelectedRows();
	if (selectedRow == null || selectedRow.length < 1) {
		DialogAlert('请选择要委托的银行。');
		return false;
	}
	var ids = "";
	for ( var i = 0; i < selectedRow.length; i++) {
		if (i == selectedRow.length - 1) {
			ids = ids + selectedRow[i].bank_id;
		} else {
			ids = ids + selectedRow[i].bank_id + ",";
		}
	}
	DialogConfirm('您要选择这些银行?', function(yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "trademan";
			cmd.service = "BankManage";
			cmd.method = "selectTrustBank";
			cmd.ids = ids;
			cmd.organId = pageObj.organId;
			cmd.success = function(data) {
				var state = data.state;
				if (state == '1') {
					DialogSuccess('选择银行成功');
					pageObj.queryData();
					window.dialogArguments.win.pageObj.queryData();
				} else {
					DialogError('选择银行失败');
					return false;
				}
			};
			cmd.execute();
		}
	});
}
pageObj.closeThisWindow = function() {
	window.close();
}
// ---------------------------------
// 页面初始化
// ---------------------------------
$(document).ready(function() {
	pageObj.organId = (window.dialogArguments && window.dialogArguments.organId) || Utils.getUrlParamValue(document.location.href, "organId");
	pageObj.initGrid();
	$("#selectBank").click(pageObj.selectBank);
	$("#btnClose").click(pageObj.closeThisWindow);
});