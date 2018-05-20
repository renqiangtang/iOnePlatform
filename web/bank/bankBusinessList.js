// 页页对象
var pageObj = {};
// 表格对象
// ---------------------------------
pageObj.bankBusinessData = null;
pageObj.userId = null;

pageObj.reload = function() {
	var manager = $("#bankBusinessData").ligerGetGridManager();
	var str = 'method=bankBusinessList&module=bank&service=Bank&u=' + pageObj.userId;
	manager.loadServerData(str);
}

// 查询的列名
pageObj.getColumns = function() {
	return [{
				display : '单位',
				name : 'organ_name',
				width : '15%'
			}, {
				display : '银行',
				name : 'bank_name',
				width : '15%'
			}, {
				display : '银行编号',
				name : 'bank_id',
				width : '8%'
			}, {
				display : '报文类型',
				name : 'trx_code',
				width : '8%'
			}, {
				display : '报文类型',
				name : 'trx_code',
				width : '8%',
				render : pageObj.trxCodeRender
			}, {
				display : '流水号',
				name : 'sequence_no',
				width : '15%',
				render : pageObj.noRender
			}, {
				display : '日期',
				name : 'busi_date',
				width : '10%'
			}, {
				display : '状态',
				name : 'status',
				width : '8%',
				render : pageObj.statusRender
			}];
}

// ----------------------------
// 渲染入账单类型
// ----------------------------
pageObj.trxCodeRender = function(row, rowNamber) {
	var trxCode = row.trx_code;
	if (trxCode == '10000') {
		return '连接测试';
	} else if (trxCode == '10001') {
		return '创建子账号';
	} else if (trxCode == '10002') {
		return '来帐通知';
	} else if (trxCode == '10003') {
		return '计息';
	} else if (trxCode == '10004') {
		return '子转总';
	} else if (trxCode == '10005') {
		return '销户';
	} else {
		return trxCode;
	}
}

// ----------------------------
// 渲染状态
// ----------------------------
pageObj.statusRender = function(row, rowNamber) {
	var status = row.status;
	if (status == '0') {
		return '未处理';
	} else if (status == '1') {
		return '已发送';
	} else if (status == '2') {
		return '成功';
	} else if (status == '3') {
		return '失败';
	} else {
		return status;
	}
}

pageObj.noRender = function(row, rowNamber) {
	var html = '<a href="javascript:pageObj.businessDetail(\'' + row.id + '\')">' + row.sequence_no + '</a>';
	return html;
}

// ---------------------------------
// 修改按钮事件
// ---------------------------------
pageObj.businessDetail = function(id) {
	var url = approot + '/bank/bankBusinessDetail.html';
	var param = {
		win : window,
		id : id,
		userId : getUserId()
	};
	var dialogWidth = 750;
	var dialogHeight = 450;
	DialogModal({
				url : url,
				param : param,
				feature : "location=no;status=no;dialogWidth=" + dialogWidth + "px;dialogHeight=" + dialogHeight + "px"
			});
}

// ---------------------------------
// 查询按钮事件
// ---------------------------------
pageObj.queryData = function() {
	var queryParams = {};
	queryParams.trxCode = $("#trxCode").val();
	queryParams.bankId = $("#bankId").val();
	queryParams.startTime = $("#startTime").val();
	queryParams.endTime = $("#endTime").val();
	queryParams.userId = getUserId();
	queryParams.sortname = "busi_date";
	queryParams.sortorder = "desc";
	var url = approot + '/data?method=bankBusinessList&module=bank&service=Bank';
	pageObj.gridManager.refresh(url, queryParams);
}

$(document).ready(function() {
			$('#btnQuery').click(pageObj.queryData);
			$("#startTime").click(function() {
						WdatePicker({
									dateFmt : 'yyyy-MM-dd'
								})
					});
			$("#endTime").click(function() {
						WdatePicker({
									dateFmt : 'yyyy-MM-dd'
								})
					});
			pageObj.userId = getUserId();
			pageObj.gridManager = $("#bankBusinessData").ligerGrid({
						url : approot + '/data?method=bankBusinessList&module=bank&service=Bank&u=' + pageObj.userId,
						columns : pageObj.getColumns(),
						isScroll : true, // 是否滚动
						frozen : false,// 是否固定列
						pageSizeOptions : [10, 20, 30],
						showTitle : false,
						width : '99.8%',
						height : 424
					});
			Utils.autoIframeSize();
		});
