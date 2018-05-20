//---------------------------------
//页页对象
//---------------------------------
var pageConfigObj = {}
//---------------------------------
//表格管理器
//---------------------------------
pageConfigObj.transTypeGridManager = null;
//---------------------------------
//模块下拉框管理器
//---------------------------------
pageConfigObj.tabManager = null;
pageConfigObj.newRecordIdPrefix = "_NEW_";

pageConfigObj.initData = function () {
	if (pageConfigObj.data) {
		$("#txtId").val(pageConfigObj.data.id);
		$("#txtName").val(pageConfigObj.data.name);
		$("#cboGoodsTypes").val(pageConfigObj.data.goods_type_id);
		$("#cboBusinessTypes").val(pageConfigObj.data.business_type_id);
	} else {
		$("#txtId").val("");
		$("#txtName").val("");
		$("#cboGoodsTypes").val("");
		$("#cboBusinessTypes").val("");
	}
	pageConfigObj.initTransTypeGrid();
}

pageConfigObj.initTransTypeGrid = function() {
	var transTypeData = {Rows: [], Total: 0};
	if (pageConfigObj.data) {
		for(var key in pageConfigObj.data.transTypes) {
			var transTypeRow = {};
			var transType = pageConfigObj.data.transTypes[key];
			if (transType) {
				for(var key1 in transType)
					transTypeRow[key1] = transType[key1];
				transTypeData.Rows.push(transTypeRow);
			}
		}
	}
	var transTypesDictData = new Array();
	if (pageConfigObj.transTypes && pageConfigObj.transTypes.length > 0) {
		for(var i = 0; i < pageConfigObj.transTypes.length; i++)
			transTypesDictData.push({ id: pageConfigObj.transTypes[i].id, text: pageConfigObj.transTypes[i].name });
	}
	
	pageConfigObj.transTypeGridManager = $("#transTypeGrid").ligerGrid({
		columns: [
			{ display: '名称', name: 'name', width: 160, editor: { type: 'text' } },
			{ display: '引用交易方式', name: 'trans_type_id', width: 160 ,
				editor: { type: 'select', data: transTypesDictData, valueColumnName: 'id' },
				render: function (row) {
					if (pageConfigObj.transTypes && pageConfigObj.transTypes.length > 0) {
						for(var i = 0; i < pageConfigObj.transTypes.length; i++) {
							if (pageConfigObj.transTypes[i].id == row.trans_type_id)
								return pageConfigObj.transTypes[i].name;
						}
					} else
						return '';
				}
			},
			{ display: '竞买申请步骤 & 多指标模板', name: 'operater', width: 200, render: function (row, rowNamber) {
				return "<a href='javascript:pageConfigObj.transTypeConfig()'>设置</a>";
			} }
		],
		data: transTypeData,
		usePager: false,
		isScroll: true,
		showTitle: false,
//		width: '99.7%',//350,
		height: 230,
		enabledEdit: true,
		toolbar: { items: [
			{ text: '新增', id: 'btnAddTransType', click: pageConfigObj.addTransType, icon: 'add' },
			{ line: true },
			{ text: '删除', id: 'btnDeleteTransType', click: pageConfigObj.deleteTransType, icon: 'delete' }
		]}
	});
}

pageConfigObj.transTypeConfig = function () {
	var selectedRow = pageConfigObj.transTypeGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要编辑的交易方式。');
		return false;
	}
	$("#lstTransTypeSteps").empty();
	$("#lstTransTypeQuotas").empty();
	if (selectedRow.steps && selectedRow.steps.length > 0) {
		for(var i = 0; i < selectedRow.steps.length; i++) {
			var licenseStepName = "";//不使用selectedRow.steps[i].name(trans_license_step_rel.name)，此值基本无意义
			if (pageConfigObj.steps && pageConfigObj.steps.length > 0)
				for(var j = 0; j < pageConfigObj.steps.length; j++)
					if (pageConfigObj.steps[j].id == selectedRow.steps[i].license_step_id) {
						licenseStepName = pageConfigObj.steps[j].name;
						break;
					}
			$("#lstTransTypeSteps").append("<option value='" + selectedRow.steps[i].license_step_id + "'>" + licenseStepName + "</option>");
		}
	}
	if (selectedRow.quotas && selectedRow.quotas.length > 0) {
		for(var i = 0; i < selectedRow.quotas.length; i++) {
			var quotaName = "";//不使用selectedRow.quotas[i].name(trans_multi_trade_rel.name)，此值基本无意义
			if (pageConfigObj.quotas && pageConfigObj.quotas.length > 0)
				for(var j = 0; j < pageConfigObj.quotas.length; j++)
					if (pageConfigObj.quotas[j].id == selectedRow.quotas[i].multi_trade_id) {
						quotaName = pageConfigObj.quotas[j].name;
						break;
					}
			$("#lstTransTypeQuotas").append("<option value='" + selectedRow.quotas[i].multi_trade_id + "'>" + quotaName + "</option>");
		}
	}
	pageConfigObj.tabManager.selectTabItem("tabitem1");
	$.ligerDialog.open({ height: 325, width: 310, title: '设置交易方式', target: $("#tabTransTypeDtl"),//310,295
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				pageConfigObj.saveTransTypeConfig();
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	});
}

pageConfigObj.saveTransTypeConfig = function () {
	var selectedRow = pageConfigObj.transTypeGridManager.getSelectedRow();
	if (selectedRow == null) {
		return false;
	}
	var steps = new Array();
	$("#lstTransTypeSteps option").each(function() {
		var step = {};
		step.license_step_id = $(this).val();
		if (selectedRow.steps && selectedRow.steps.length > 0)
			for(var i = 0; i < selectedRow.steps.length; i++) {
				if (selectedRow.steps[i].license_step_id == step.license_step_id) {
					step.id = selectedRow.steps[i].id;
					break;
				}
			}
		if (!step.id)
			step.id = pageConfigObj.newRecordIdPrefix + newGUID(true);
		steps.push(step);  
    });
    selectedRow.steps = steps;
	var quotas = new Array();
	$("#lstTransTypeQuotas option").each(function() {  
		var quota = {};
		quota.multi_trade_id = $(this).val();
		if (selectedRow.quotas && selectedRow.quotas.length > 0)
			for(var i = 0; i < selectedRow.quotas.length; i++) {
				if (selectedRow.quotas[i].multi_trade_id == quota.multi_trade_id) {
					quota.id = selectedRow.quotas[i].id;
					break;
				}
			}
		if (!quota.id)
			quota.id = pageConfigObj.newRecordIdPrefix + newGUID(true);
		quotas.push(quota);  
    });
    selectedRow.quotas = quotas;
}

pageConfigObj.addTransType = function () {
	pageConfigObj.transTypeGridManager.addRow({
			id: pageConfigObj.newRecordIdPrefix + newGUID(true)
		});
}

pageConfigObj.deleteTransType = function () {
	var selectedRow = pageConfigObj.transTypeGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要删除的交易方式。');
		return false;
	} else {
		DialogConfirm("确定要删除选择的交易方式 " + selectedRow.name + "？", function (yes) {
			if (yes) {
				pageConfigObj.transTypeGridManager.deleteSelectedRow();
			}
		});
	}
}

pageConfigObj.initTransTypeTab = function() {
	pageConfigObj.tabManager = $("#tabTransTypeDtl").ligerTab({
		//width: '286px',
		height: 250,
		contextmenu: false,
		onBeforeSelectTabItem: pageConfigObj.beforeSelectTabItem,
	    onAfterSelectTabItem: pageConfigObj.afterSelectTabItem
     });
}

pageConfigObj.addStep = function () {
	var intSelectedIndex = $("#cboSteps")[0].selectedIndex;
	if (intSelectedIndex >= 0) {
		var selectedStepId = $("#cboSteps").val();
		var selectedStepName = $("#cboSteps").find("option:selected").text();
		var blnExists = false;
		$("#lstTransTypeSteps option").each(function() {  
			if ($(this).val() == selectedStepId) {
				$(this).attr("selected", "selected");  
				blnExists = true;
			}
        });
        if (!blnExists)
        	$("#lstTransTypeSteps").append("<option value='" + selectedStepId + "'>" + selectedStepName + "</option>");
	}
}

pageConfigObj.deleteStep = function () {
	var intSelectedIndex = $("#lstTransTypeSteps")[0].selectedIndex;
	if (intSelectedIndex >= 0)
		//$("#lstTransTypeSteps option[index='" + intSelectedIndex + "']").remove();
		$("#lstTransTypeSteps option[value='" + $("#lstTransTypeSteps").val() + "']").remove();
}

pageConfigObj.clearStep = function () {
	$("#lstTransTypeSteps").empty();
}

pageConfigObj.moveUpStep = function () {
	var intSelectedIndex = $("#lstTransTypeSteps")[0].selectedIndex;
	if (intSelectedIndex <= 0)
		return;
	$("#lstTransTypeSteps option").filter(':eq(' + (intSelectedIndex - 1) + ')').before($("#lstTransTypeSteps option:selected"));
}

pageConfigObj.moveDownStep = function () {
	var intSelectedIndex = $("#lstTransTypeSteps")[0].selectedIndex;
	var intMaxIndex = $("#lstTransTypeSteps option:last").attr("index");
	if (intSelectedIndex < 0 || intSelectedIndex == intMaxIndex)
		return;
	$("#lstTransTypeSteps option").filter(':eq(' + (intSelectedIndex + 1) + ')').after($("#lstTransTypeSteps option:selected"));
}

pageConfigObj.addQuota = function () {
	var intSelectedIndex = $("#cboQuotas")[0].selectedIndex;
	if (intSelectedIndex >= 0) {
		var selectedQuotaId = $("#cboQuotas").val();
		var selectedQuotaName = $("#cboQuotas").find("option:selected").text();
		var blnExists = false;
		$("#lstTransTypeQuotas option").each(function() {  
			if ($(this).val() == selectedQuotaId) {
				$(this).attr("selected", "selected");  
				blnExists = true;
			}
        });
        if (!blnExists)
        	$("#lstTransTypeQuotas").append("<option value='" + selectedQuotaId + "'>" + selectedQuotaName + "</option>");
	}
}

pageConfigObj.deleteQuota = function () {
	var intSelectedIndex = $("#lstTransTypeQuotas")[0].selectedIndex;
	if (intSelectedIndex >= 0)
		//$("#lstTransTypeQuotas option[index='" + intSelectedIndex + "']").remove();
		$("#lstTransTypeQuotas option[value='" + $("#lstTransTypeQuotas").val() + "']").remove();
}

pageConfigObj.clearQuota = function () {
	$("#lstTransTypeQuotas").empty();
}

pageConfigObj.moveUpQuota = function () {
	var intSelectedIndex = $("#lstTransTypeQuotas")[0].selectedIndex;
	if (intSelectedIndex <= 0)
		return;
	$("#lstTransTypeQuotas option").filter(':eq(' + (intSelectedIndex - 1) + ')').before($("#lstTransTypeQuotas option:selected"));
}

pageConfigObj.moveDownQuota = function () {
	var intSelectedIndex = $("#lstTransTypeQuotas")[0].selectedIndex;
	var intMaxIndex = $("#lstTransTypeQuotas option:last").attr("index");
	if (intSelectedIndex < 0 || intSelectedIndex == intMaxIndex)
		return;
	$("#lstTransTypeQuotas option").filter(':eq(' + (intSelectedIndex + 1) + ')').after($("#lstTransTypeQuotas option:selected"));
}

//---------------------------------
//调用页面调用此方法检查设置内容
//---------------------------------
pageConfigObj.checkBusinessTypeConfig = function (nonePrompt) {
	if ($("#txtId").val() == "") {
		if (!nonePrompt)
			DialogWarn('必须输入业务类别编号。');
		return false;
	}
	if ($("#cboGoodsTypes").val() == "") {
		if (!nonePrompt)
			DialogWarn('必须选择引用的交易物类别。');
		return false;
	}
	if ($("#cboBusinessTypes").val() == "") {
		if (!nonePrompt)
			DialogWarn('必须输入引用的业务类别类别。');
		return false;
	}
	pageConfigObj.transTypeGridManager.endEdit();
	var checkExistsName = ";";
	for(var i = 0; i < pageConfigObj.transTypeGridManager.data.Rows.length; i++) {
		if (!pageConfigObj.transTypeGridManager.data.Rows[i].trans_type_id) {
			if (!nonePrompt)
				DialogWarn('未选择引用的交易方式');
			return false;
		} else if (checkExistsName.indexOf(';' + pageConfigObj.transTypeGridManager.data.Rows[i].trans_type_id + ';') != -1) {
			if (!nonePrompt)
				DialogWarn('引用的交易方式不能重复(没有意义)');
			return false;
		}
		checkExistsName += pageConfigObj.transTypeGridManager.data.Rows[i].trans_type_id + ';';
	}
	return true;
}

//---------------------------------
//调用页面调用此方法获取设置内容
//---------------------------------
pageConfigObj.getBusinessTypeConfig = function () {
	if (!pageConfigObj.checkBusinessTypeConfig(true))
		return null;
	var data = {};
	data.id = $("#txtId").val();
	if (!data.id)
		data.id = $("#cboGoodsTypes").val() + $("#cboBusinessTypes").val();
	data.name = $("#txtName").val();
	if (!data.name)
		data.name = $("#cboGoodsTypes").find("option:selected").text() + $("#cboBusinessTypes").find("option:selected").text();;
	data.goods_type_id = $("#cboGoodsTypes").val();
	data.business_type_id = $("#cboBusinessTypes").val();
	var transTypes = {};
	pageConfigObj.transTypeGridManager.endEdit();
	var gridData = pageConfigObj.transTypeGridManager.getData();
	for(var i = 0; i < gridData.length; i++) {
		var row = gridData[i];
		var transType = {};
		transType.id = row.id;
		transType.trans_type_id = row.trans_type_id;
		transType.name = row.name;
		if (!transType.name && pageConfigObj.transTypes && pageConfigObj.transTypes.length > 0) {
			for(var i = 0; i < pageConfigObj.transTypes.length; i++) {
				if (pageConfigObj.transTypes[i].id == row.trans_type_id) {
					transType.name = pageConfigObj.transTypes[i].name;
					break;
				}
			}
		}
		transType.steps = row.steps;
		transType.quotas = row.quotas;
		if (!transType.id)
			transType.id = pageConfigObj.newRecordIdPrefix + newGUID(true);
		transTypes[transType.id] = transType;
	}
	data.transTypes = transTypes;
	return data;
}

//---------------------------------
//页面初始化
//---------------------------------
pageConfigObj.initHtml = function() {
	var randomVarPrefix = Utils.getUrlParamValue(document.location.href, "randomVarPrefix");
	//业务类别表格行记录
	pageConfigObj.data = getGlobalAttribute(randomVarPrefix + "_data");
	//交易物类别字典
	pageConfigObj.goodsTypes = getGlobalAttribute(randomVarPrefix + "_goodsTypes");
	$("#cboGoodsTypes").empty();
	if (pageConfigObj.goodsTypes) {
		for(var i = 0; i < pageConfigObj.goodsTypes.length; i++) {
			$("#cboGoodsTypes").append("<option value='" + pageConfigObj.goodsTypes[i].id + "'>" + pageConfigObj.goodsTypes[i].name + "</option>");
		}
	}
	//业务类别字典
	pageConfigObj.businessTypes = getGlobalAttribute(randomVarPrefix + "_businessTypes");
	$("#cboBusinessTypes").empty();
	if (pageConfigObj.businessTypes) {
		for(var i = 0; i < pageConfigObj.businessTypes.length; i++) {
			$("#cboBusinessTypes").append("<option value='" + pageConfigObj.businessTypes[i].id + "'>" + pageConfigObj.businessTypes[i].name + "</option>");
		}
	}
	//竞买申请步骤字典
	pageConfigObj.steps = getGlobalAttribute(randomVarPrefix + "_steps");
	$("#cboSteps").empty();
	$("#lstTransTypeSteps").empty();
	if (pageConfigObj.steps) {
		for(var i = 0; i < pageConfigObj.steps.length; i++) {
			$("#cboSteps").append("<option value='" + pageConfigObj.steps[i].id + "'>" + pageConfigObj.steps[i].name + "</option>");
		}
	}
	//多指标模板字典
	pageConfigObj.quotas = getGlobalAttribute(randomVarPrefix + "_quotas");
	$("#cboQuotas").empty();
	$("#lstTransTypeQuotas").empty();
	if (pageConfigObj.quotas) {
		for(var i = 0; i < pageConfigObj.quotas.length; i++) {
			$("#cboQuotas").append("<option value='" + pageConfigObj.quotas[i].id + "'>" + pageConfigObj.quotas[i].name + "</option>");
		}
	}
	//交易方式字典
	pageConfigObj.transTypes = getGlobalAttribute(randomVarPrefix + "_transTypes");
	removeGlobalAttribute(randomVarPrefix + "_data");
	removeGlobalAttribute(randomVarPrefix + "_goodsTypes");
	removeGlobalAttribute(randomVarPrefix + "_businessTypes");
	removeGlobalAttribute(randomVarPrefix + "_steps");
	removeGlobalAttribute(randomVarPrefix + "_transTypes");
	removeGlobalAttribute(randomVarPrefix + "_quotas");
	$("#btnAddStep").click(pageConfigObj.addStep);
	$("#btnDeleteStep").click(pageConfigObj.deleteStep);
	$("#btnClearStep").click(pageConfigObj.clearStep);
	$("#btnMoveUpStep").click(pageConfigObj.moveUpStep);
	$("#btnMoveDownStep").click(pageConfigObj.moveDownStep);
	$("#btnAddQuota").click(pageConfigObj.addQuota);
	$("#btnDeleteQuota").click(pageConfigObj.deleteQuota);
	$("#btnClearQuota").click(pageConfigObj.clearQuota);
	$("#btnMoveUpQuota").click(pageConfigObj.moveUpQuota);
	$("#btnMoveDownQuota").click(pageConfigObj.moveDownQuota);
	pageConfigObj.initData();
	pageConfigObj.initTransTypeTab();
}

$(document).ready(pageConfigObj.initHtml);