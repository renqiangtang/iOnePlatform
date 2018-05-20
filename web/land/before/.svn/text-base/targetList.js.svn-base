var pageObj = {};
pageObj.grid = null;

// ----------------------------
// 渲染状态
// ----------------------------
pageObj.renderTargetStatus = function(row, rowNumber) {
	var status = row.status;
	var is_stop = row.is_stop;
	var is_suspend = row.is_suspend;
	var is_s = '';
	if (is_suspend == 1) {
		is_s = '中止';
	}
	if (is_stop == 1) {
		is_s = '终止';
	}
	if (is_s != null && is_s != "") {
		is_s = '<font color="red">(' + is_s + ')</font>';
	}
	return comObj.targetStatusObj[status] + is_s;
}

// -----------------------------
// 渲染保证金
// ----------------------------
pageObj.renderEarnestMoneny = function(row, rowNumber) {
	var moneys = row.earnest_money;
	if (moneys) {
		var arr1 = moneys.split("&nbsp;");
		var arr2 = [];
		for ( var i = 0; i < arr1.length; i++) {
			var arr3 = arr1[i].split(":");
			arr2.push(comObj.cf({
				flag : arr3[0],
				unit : row.unit,
				amount : parseFloat(arr3[1])
			}));
		}
		return arr2.join(" ");
	}
	return moneys;
}

//-----------------------------
//渲染起始价
//----------------------------
pageObj.renderBeginPrice=function(row,number){
	return comObj.cf({amount:row.begin_price,unit:row.unit});
}

// -----------------------------
// 渲染名称
// ----------------------------
pageObj.renderName = function(row, rowNumber) {
	var html = '<a href="javascript:pageObj.targetView(\'' + row.id + '\');">' + row.name + '</a>';
	return html;
}

// ---------------------------------
// 查询按钮事件
// ---------------------------------
pageObj.query = function() {
	var canton_id = $("#canton_id").val();
	var no = $("#no").val().trim();
	var trans_type = $('#trans_type').val();
	var status = $("#status").val();
//	var create_user_id = $("#create_user_id").val();
	var goods_use = $("#goods_use").val();

	var queryParams = {
		canton_id : canton_id,
		no : no,
		trans_type : trans_type,
		//
		status : status,
//		create_user_id : create_user_id,
		goods_use : goods_use,
		//
		goods_type : pageObj.goodsType,
		u : pageObj.u
	//

	};
	var url = approot + '/data?module=before&service=TransGoodsLand&method=getTransTargetList';
	pageObj.grid.refresh(url, queryParams);
}

// ---------------------------------
// 重置
// ---------------------------------
pageObj.reset = function() {
	$('#canton_id').val(-1);
	$('#no').val('');
	$('#trans_type').val(-1);
	$('#address').val('');
	$('#status').val(-1);
//	$('#create_user_id').val(-1);
	$('#goods_use').val('');
}

// ---------------------------------
// 初始化查询条件
// ---------------------------------
pageObj.initQueryParam = function() {
	var html = "";
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsLand";
	cmd.method = "initQueryParam";
	cmd.goodsType = pageObj.goodsType;
	cmd.u = pageObj.u;
	cmd.success = function(data) {
//		var html = '<option value="-1" checked>--请选择--</option>';
//		for ( var i = 0; i < data.canton.length; i++) {
//			html += '<option value="' + data.canton[i].id + '">' + data.canton[i].name + '</option>';
//		}
//		$('#canton_id').html(html);
		//
//		html = '<option value="-1" checked>--请选择--</option>';
//		for ( var i = 0; i < data.user.length; i++) {
//			html += '<option value="' + data.user[i].id + '">' + data.user[i].user_name + '</option>';
//		}
//		$('#create_user_id').html(html);
		//
		var transTypeHavaValue="";
		html = '<option value="-1" checked>--请选择--</option>';
		for ( var i = 0; i < data.transType.length; i++) {
			if(transTypeHavaValue.indexOf(data.transType[i].trans_type)==-1){
				html += '<option value="' + data.transType[i].trans_type + '">' + data.transType[i].name + '</option>';
			}
			transTypeHavaValue+=data.transType[i].trans_type;
		}
		$('#trans_type').html(html);
		//

	};
	cmd.execute();
	// 状态
	$('#status').empty();
	html = '<option value="-1" checked>--请选择--</option>';
	for ( var pro in comObj.targetStatusObj) {
		html += "<option value='" + pro + "'>" + comObj.targetStatusObj[pro] + "</option>";
	}
	$('#status').html(html);
	// 用途
	// html = '<option value="-1" checked>--请选择--</option>';
	// for (var pro in comObj.use_type) {
	// html += "<option value='" + pro + "'>" + comObj.use_type[pro] +
	// "</option>";
	// }
	// $('#goods_use').html(html);
	$('#goods_use').html('');

}
// ---------------------------------
// 初始化标的表格
// ---------------------------------
pageObj.initGrid = function() {
	pageObj.grid = $("#grid").ligerGrid({
		url : approot + '/data?module=before&service=TransGoodsLand&method=getTransTargetList&goods_type=' + pageObj.goodsType,
		columns : [ {
			display : '宗地编号',
			name : 'name',
			width : '30%',
			render : pageObj.renderName
		}, {
			display : '交易类型',
			name : 'business_name',
			width : '10%'
		}, {
			display : '起始价',
			name : 'begin_price',
			align : 'left',
			width : '17%',
			render:pageObj.renderBeginPrice
		}, {
			display : '保证金',
			name : 'earnest_money',
			width : '17%',
			align : 'left',
			render : pageObj.renderEarnestMoneny
		}, {
			display : '状态',
			name : 'status',
			width : '9%',
			render : pageObj.renderTargetStatus
		}, {
			display : '经办人',
			name : 'user_name',
			width : '11%'
		} ],
		isScroll : true,// 是否滚动
		pageSizeOptions : [ 10, 20, 30 ],
		showTitle : false,
		checkbox : true,
		rownumbers : true,
		// selectRowButtonOnly : true,
		// detail : {
		// onShowDetail : pageObj.initDetailGrid
		// },
		height : 400,
		detailHeight : 200
	});
}

// ---------------------------------
// 初始化明细表格
// ---------------------------------
pageObj.initDetailGrid = function(row, detailPane1) {
	var tr1 = $(detailPane1).parents("tr");
	var id1 = $(tr1).attr("id");
	var id2 = id1.replace("detail", "2");
	$(detailPane1).parent("td").attr("colspan", "2");
	$(detailPane1).parent("td").attr("valign", "middle");
	$(detailPane1).parent("td").attr("bgcolor", "#fff3df");
	$(detailPane1).css({
		width : "20px",
		height : "100px",
		display : "table",
		margin : "0 auto",
		font : "bold"
	});
	$(detailPane1).html("标的地块信息");
	var detailDiv = document.createElement('div');
	var ttr = $$(id2);
	var tr2 = $(ttr).next();
	var div = tr2.find("div");
	div.append(detailDiv);
	$(div).parent("td").attr("colspan", pageObj.grid.getColumns().length);
	var gridOption = {};
	gridOption.url = approot + '/data?dm=Rows&module=before&service=TransGoodsLand&method=getTransTargetGoodsList&targetId=' + row.id;
	gridOption.columns = [ {
		display : '地块号',
		width : 200,
		name : 'no',
		render : comObj.goodsNoRender
	}, {
		display : '区域',
		width : 100,
		name : 'canton_name'
	}, {
		display : '位置',
		width : 200,
		name : 'address'
	}, {
		display : '经办人',
		width : 100,
		name : 'user_name'
	}, {
		display : '创建时间',
		width : 200,
		name : 'create_date'
	} ];
	gridOption.height = 198;
	gridOption.usePager = false;
	gridOption.showTitle = false;
	// gridOption.rownumbers = true;
	$(detailDiv).ligerGrid(gridOption);
}

// ---------------------------------
// 新建标的
// ---------------------------------
pageObj.targetNew = function() {
	var obj = {};
	// 地址
	obj.url = "targetEdit.html";
	// 参数
	obj.param = {
		u : pageObj.u,
		mode : comObj.mode['new'],
		goodsType : pageObj.goodsType
	};
	// 窗口参数
	var sheight = screen.height - 130;
	var swidth = screen.width - 50;
	obj.feature = 'dialogWidth=' + swidth + 'px;dialogHeight=' + sheight + 'px';
	var returnValue = DialogModal(obj);
	if (returnValue)
		pageObj.query();
}

// ---------------------------------
// 查看标的
// ---------------------------------
pageObj.targetView = function(targetId) {
	var obj = {};
	// 地址
	obj.url = "targetEdit.html";
	// 参数
	obj.param = {
		u : pageObj.u,
		targetId : targetId,
		mode : comObj.mode['modify'],
		goodsType : pageObj.goodsType
	};
	// 窗口参数
	var sheight = screen.height - 130;
	var swidth = screen.width - 50;
	obj.feature = 'dialogWidth=' + swidth + 'px;dialogHeight=' + sheight + 'px';
	var returnValue = DialogModal(obj);
	if (returnValue)
		pageObj.query();
}

// ---------------------------------
// 审核标的
// ---------------------------------
pageObj.targetCheck = function() {
	var targetIdArr = pageObj.grid.getCheckedArr();
	if (targetIdArr.length > 0) {
		DialogConfirm('确定要审核这' + targetIdArr.length + '个标的吗?', function(yn) {
			if (!yn)
				return;
		var cmd = new Command();
		cmd.module = "before";
		cmd.service = "TransGoodsLand";
		cmd.method = "doCheckTransTarget";
		cmd.u = pageObj.u;
		cmd.targetIds = targetIdArr.join(",");
		cmd.success = function(data) {
			DialogAlert(data.message);
			if (data.state)
				pageObj.query();
		}
		cmd.execute();
		});
	} else {
		DialogAlert('请选择需要审核的条目!');
	}
}

// ---------------------------------
// 删除标的
// ---------------------------------
pageObj.targetDelete = function() {
	var targetIdArr = pageObj.grid.getCheckedArr();
	if (targetIdArr.length == 0){
		DialogAlert('请选择至少一条数据进行删除!');
		return;
	}
	DialogConfirm('确定要将这' + targetIdArr.length + '条删除?', function(yn) {
		if (!yn)
			return;
		var cmd = new Command();
		cmd.module = "before";
		cmd.service = "TransGoodsLand";
		cmd.method = "deleteTransTargets";
		cmd.u = pageObj.u;
		cmd.targetIds = targetIdArr.join(",");
		cmd.success = function(data) {
			DialogAlert(data.message);
			if (data.state)
				pageObj.query();
		}
		cmd.execute();
	});
}

// ---------------------------------
// 页面初始化
// ---------------------------------
$(document).ready(function() {
	pageObj.u = getUserId();
	pageObj.goodsType = Utils.getPageValue('goodsType');
	pageObj.initQueryParam();
	pageObj.initGrid();
	$("#btnQuery").click(pageObj.query);
	$("#btnReset").click(pageObj.reset);
	$("#btnNew").click(pageObj.targetNew);
	$("#btnDelete").click(pageObj.targetDelete);
	$("#btnCheck").click(pageObj.targetCheck);
	//
	$('#noTitleDiv').html(comObj.target_name[pageObj.goodsType] + "：");
	$('#goodsUserTitleDiv').html(comObj.goodsUser[pageObj.goodsType] + "：");
});
