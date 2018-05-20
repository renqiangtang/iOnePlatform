//---------------------------------
//页页对象
//---------------------------------
var pageObj = {};
// ---------------------------------
// 表格对象
// ---------------------------------
pageObj.gridManager = null;
// ---------------------------------
// 转换名称
// ---------------------------------
pageObj.nameRender = function(row, rowNamber) {
	var name = row.name;
	return "<a href='javascript:pageObj.editBank()'>" + name + "</a>";
}
// ---------------------------------
// 初始化表格
// ---------------------------------
pageObj.initGrid = function() {
	pageObj.gridManager = $("#bankGrid").ligerGrid({
		url : approot + '/data?module=trademan&service=Bank&method=getBankListData&moduleId=' + pageObj.moduleId,
		columns : [ {
			display : '名称',
			name : 'name',
			width : '12%',
			render : pageObj.nameRender
		}, {
			display : '编号',
			name : 'no',
			width : '12%'
		}, {
			display : '电话',
			name : 'tel',
			width : '12%'
		}, {
			display : '联系人',
			name : 'contact',
			width : '12%'
		}, {
			display : '地址',
			name : 'address',
			width : '28%'
		}, {
			display : '行政区',
			name : 'cantonName',
			width : '12%'
		}, {
			display : '注册日期',
			name : 'createDate',
			width : '12%'
		} ],
		pagesizeParmName : 'pageRowCount',// 每页记录数
		pageParmName : 'pageIndex',// 页码数
		sortnameParmName : 'sortField',// 排序列名
		sortorderParmName : 'sortDir',// 排序方向
		isScroll : true,// 是否滚动
		frozen : false,// 是否固定列
		pageSizeOptions : [ 10, 20, 30 ],
		showTitle : false,
		width : '99.8%',
		height : 422,
		detail : false
	});
}
// ---------------------------------
// 查询按钮事件
// ---------------------------------
pageObj.queryData = function() {
	var bankName = $("#txtName").val();
	var bankNo = $("#txtNo").val();
	var bankTel = $("#txtTel").val();
	var bankAddress = $("#txtAddress").val();
	var bankHasCakey = $("#cboHasCakey").val();
	var bankCakey = $("#txtCakey").val();
	var queryParams = {
		bankName : bankName,
		bankNo : bankNo,
		bankTel : bankTel,
		bankAddress : bankAddress,
		bankHasCakey : bankHasCakey,
		bankCakey : bankCakey
	};
	var url = approot + '/data?module=trademan&service=Bank&method=getBankListData&moduleId=' + pageObj.moduleId;
	pageObj.gridManager.refresh(url, queryParams);
}
// ---------------------------------
// 新增按钮事件
// ---------------------------------
pageObj.newBank = function() {
	DialogOpen({
		height : 380,
		width : 650,
		title : '新增银行',
		url : approot + '/trademan/bankEdit.html?moduleId=' + pageObj.moduleId + '&id='
	});
}
// ---------------------------------
// 修改按钮事件
// ---------------------------------
pageObj.editBank = function() {
	var selectedRow = pageObj.gridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要修改的银行。');
		return false;
	}
	DialogOpen({
		height : 380,
		width : 650,
		title : '修改银行',
		url : approot + '/trademan/bankEdit.html?moduleId=' + pageObj.moduleId + '&id=' + selectedRow.id + '&onlyModifyCakey=' + pageObj.onlyModifyCakey + '&unallowDelete=' + pageObj.unallowDelete
	});
}
// ---------------------------------
// 删除按钮事件
// ---------------------------------
pageObj.deleteBank = function() {
	var selectedRow = pageObj.gridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要删除的银行。');
		return false;
	}
	DialogConfirm('删除当前银行 ' + selectedRow.name + '？', function(yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "trademan";
			cmd.service = "Bank";
			cmd.method = "deleteBankData";
			cmd.id = selectedRow.id;
			cmd.success = function(data) {
				var state = data.state;
				if (state == '1') {
					DialogAlert('删除银行数据成功完成');
					// pageObj.queryData(pageObj.gridManager.options.page);
					pageObj.gridManager.deleteSelectedRow();
				} else {
					DialogError('删除银行失败,错误原因：' + data.message);
					return false;
				}
			};
			cmd.execute();
		}
	});
}
// ---------------------------------
// 读取Cakey按钮事件
// ---------------------------------
pageObj.readCakey = function() {
	DialogConfirm('请插上新的CAKey后点击“是”按钮！', function(yes) {
		if (yes) {
			var caKeyId = ca.readId();
			$("#txtCakey").val(caKeyId);
		}
	});
}
// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getUrlParamValue(document.location.href, "moduleId");
	pageObj.onlyModifyCakey = Utils.getUrlParamValue(document.location.href, "onlyModifyCakey") == "true";
	pageObj.unallowDelete = Utils.getUrlParamValue(document.location.href, "unallowDelete") == "true";
	pageObj.initGrid();
	$("#btnQuery").click(pageObj.queryData);
	$("#btnAdd").click(pageObj.newBank);
	if (pageObj.unallowDelete) {
		$("#btnDelete").hide();
	} else {
		$("#btnDelete").click(pageObj.deleteBank);
	}
	$("#btnReadCakey").click(pageObj.readCakey);
	Utils.autoIframeSize();
}
$(document).ready(pageObj.initHtml);
