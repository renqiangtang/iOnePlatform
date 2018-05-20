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

pageObj.status = {};
pageObj.status['1'] = '运行';
pageObj.status['2'] = '等待挂起';
pageObj.status['3'] = '停止';
pageObj.status['4'] = '废弃';
pageObj.status['5'] = '正常完成';
pageObj.status['50'] = '等待结束';

// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initPage = function() {
	var html = "<option value='-1' checked>--请选择--</option>";
	for ( var pro in pageObj.status) {
		html += "<option value='" + pro + "'>" + pageObj.status[pro] + "</option>"
	}
	$('#status').html(html);

	pageObj.u = getUserId();
	var cmd = new Command();
	cmd.module = "before";
	if (pageObj.goodsType == "101")
		cmd.service = "TransNotice";
	else
		cmd.service = "TransNoticeMine";
	cmd.method = "initNoticeListPageParam";
	cmd.u = pageObj.u;
	cmd.goodsType = pageObj.goodsType;
	cmd.success = function(data) {
		html = "<option value='-1' checked>--请选择--</option>";
		pageObj.businessType = {};
		$(data.businessType).each(function(index, obj) {
			pageObj.businessType[obj.id] = obj.name;
			html += "<option value='" + obj.id + "'>" + obj.name + "</option>";
		});
		$('#business_type').html(html);
	};
	cmd.execute();
}

// ---------------------------------
// 展示正在活动的流程
// ---------------------------------
pageObj.initGrid = function() {
	pageObj.grid = $("#grid").ligerGrid({
		url : approot + '/data?module=trademan&service=ProcessManage&method=processList&u=' + pageObj.u + '&goods_type=' + pageObj.goodsType,
		columns : [ {
			display : '公告名称',
			name : 'notice_name',
			width : '20%'
		}, {
			display : '标的名称',
			name : 'targetname',
			width : '20%',
			render : function(row, rowNamber) {
				var html = '<a href="javascript:pageObj.memInfo(\'' + row.processid + '\')">' + row.targetname + '</a>';
				return html;
			}
		}, {
			display : '交易类型',
			name : 'business_name',
			width : '10%'
		}, {
			display : '保证金(元)',
			width : '10%',
			render : pageObj.renderEarnest
		}, {
			display : '阶段',
			name : 'phase',
			width : '10%'
		}, {
			display : '状态',
			name : 'state',
			width : '10%',
			render : pageObj.stateRender
		}, {
			display : '操作',
			name : 'task',
			width : '15%',
			render : pageObj.operateRender
		} ],
		rownumbers : true,
		isScroll : true, // 是否滚动
		frozen : false,// 是否固定列
		pageSizeOptions : [ 10, 20, 30 ],
		showTitle : false,
		height : '81%'
	});
}

//---------------------------------
//显示引擎信息
//---------------------------------
pageObj.memInfo = function(processId) {
	var cmd = new Command('trademan', 'ProcessManage', 'processMemInfo');
	cmd.processId = processId;
	cmd.success = function(data) {
		if (data.info)
			DialogAlert(data.info);
	};
	cmd.execute();
}

pageObj.renderEarnest = function(row, number) {
	var businessType = row.business_type;
	if (businessType.indexOf("501") >= 0) {
		return row.earnest_money;
	} else {
		var arr = row.target_earnest_money.split("&nbsp;");
		var arr2 = [];
		for ( var i = 0; i < arr.length; i++) {
			var arr1 = arr[i].split(":");
			arr2.push(comObj.cf({
				flag : arr1[0],
				amount : parseFloat(arr1[1]),
				unit : row.unit
			}));
		}
		return arr2.join(" ");
	}
}

// ----------------------------
// 渲染状态
// ----------------------------
pageObj.stateRender = function(row, rowNamber) {
	var state = Number(row.state).toFixed(0);
	var result = pageObj.status[state];
	return result;
}

// ----------------------------
// 渲染操作
// ----------------------------
pageObj.operateRender = function(row, rowNamber) {
	var html = '';
	if (row.state == '1') {
		html = '<a href="javascript:pageObj.stopProcess(\'' + row.id + '\')">中止</a>&nbsp;&nbsp;<a href="javascript:pageObj.cancelProcess(\'' + row.id + '\')">终止</a>';
	} else if (row.state == '3') {
		html = '<a href="javascript:pageObj.restartProcess(\'' + row.id + '\')">恢复运行</a>&nbsp;&nbsp;<a href="javascript:pageObj.cancelProcess(\'' + row.id + '\')">终止</a>';
	} else if (row.state == '4') {
		html = '终止';
	}
	html += '&nbsp;&nbsp;<a href="javascript:pageObj.showHistory(\'' + row.id + '\',\'' + row.targetname + '\')">历史</a>'
	return html;
}

// ---------------------------------
// 查询按钮事件
// ---------------------------------
pageObj.queryData = function() {
	var paraObj = {
		no : $("#no").val(),
		status : $("#status").val(),
		business_type : $('#business_type').val(),
		goods_type : pageObj.goodsType,
		u : pageObj.u
	};
	var url = approot + '/data?module=trademan&service=ProcessManage&method=processList';
	pageObj.grid.refresh(url, paraObj);
}

// ---------------------------------
// 重置
// ---------------------------------
pageObj.reset = function() {
	$("#no").val('');
	$("#status").val(-1);
	$('#business_type').val(-1);
}

// ----------------------------
// 中止操作
// ----------------------------
pageObj.stopProcess = function(id) {

	var obj = {};
	obj.url = "processStop.html";
	obj.param = {
		u : pageObj.u,
		id : id
	};
	var sheight = 200;
	var swidth = 600;
	obj.feature = 'dialogWidth=' + swidth + 'px;dialogHeight=' + sheight + 'px';
	var returnValue = DialogModal(obj);
	if (returnValue) {
		pageObj.queryData();
	}

	// DialogConfirm('您确定要执行中止操作吗?', function (yes) {
	// if (yes) {
	// var reason = prompt("请输入中止原因","");
	// var cmd = new Command();
	// cmd.module = "trademan";
	// cmd.service = "ProcessManage";
	// cmd.method = "stopProcess";
	// cmd.reason = reason;
	// cmd.id = id;
	// cmd.success = function(data) {
	// if(data.state){
	// DialogAlert('执行中止操作成功！');
	// pageObj.queryData();
	// }else{
	// if(data.message){
	// DialogAlert(data.message);
	// }else{
	// DialogAlert('执行中止操作失败！');
	// }
	// }
	// }
	// cmd.execute();
	// }
	// });
}

// ----------------------------
// 恢复运行
// ----------------------------
pageObj.restartProcess = function(id) {
	var obj = {};
	obj.url = "processRestartEdit.html";
	obj.param = {
		u : pageObj.u,
		id : id
	};
	var sheight = 600;
	var swidth = 900;
	obj.feature = 'dialogWidth=' + swidth + 'px;dialogHeight=' + sheight + 'px';
	var returnValue = DialogModal(obj);
	if (returnValue) {
		pageObj.queryData();
	}
}

// ----------------------------
// 终止操作
// ----------------------------
pageObj.cancelProcess = function(id) {
	DialogConfirm('标的终止后将无法恢复。您确定要执行终止操作吗?', function(yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "trademan";
			cmd.service = "ProcessManage";
			cmd.method = "cancelProcess";
			cmd.id = id;
			cmd.success = function(data) {
				if (data.state) {
					DialogAlert('执行终止操作成功！');
					pageObj.queryData();
				} else {
					if (data.message) {
						DialogAlert(data.message);
					} else {
						DialogAlert('执行终止操作失败！');
					}
				}
			}
			cmd.execute();
		}
	});
}

// ----------------------------
// 查看历史记录
// ----------------------------
pageObj.showHistory = function(targetId, targetName) {
	var opt = {};
	obj.url = "processListChangeHistory.html";
	obj.param = {};
	obj.param.targetId = targetId;
	obj.param.targetName = targetName;
	obj.feature = "dialogWidth=800px;dialogHeight=420px;";
	DialogModal(obj);
}

// ---------------------------------
// 页面初始化
// ---------------------------------
$(document).ready(function() {
	pageObj.goodsType = Utils.getPageValue('goodsType');
	if (pageObj.goodsType == "301" || pageObj.goodsType == "301,401")
		$("#grid").addClass("mine_trade_panel");
	pageObj.initPage();
	pageObj.initGrid();
	$('#btnQuery').click(pageObj.queryData);
	$('#btnReset').click(pageObj.reset);
	// Utils.autoIframeSize();
});