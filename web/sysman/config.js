//---------------------------------
//页页对象
//---------------------------------
var pageConfigObj = {}
//---------------------------------
//表格管理器
//---------------------------------
pageConfigObj.goodsTypeDictGridManager = null;
pageConfigObj.businessTypeDictGridManager = null;
pageConfigObj.licenseStepDictGridManager = null;
pageConfigObj.transTypeDictGridManager = null;
pageConfigObj.multiTradeDictGridManager = null;
pageConfigObj.businessTypeRelGridManager = null;
//---------------------------------
//标签页数据加载标识
//---------------------------------
pageConfigObj.generalDataLoaded = false;
pageConfigObj.transRuleDataLoaded = false;
pageConfigObj.caDataLoaded = false;
pageConfigObj.checkGeneralModified = null;
pageConfigObj.checkTransRoleModified = null;
pageConfigObj.businessType = null;
pageConfigObj.checkCAModified = null;
//---------------------------------
//手工记录一些可能未监测到的数据修改
//---------------------------------
pageConfigObj.goodsTypeDictDataModified = false;
pageConfigObj.businessTypeDictDataModified = false;
pageConfigObj.licenseStepDictDataModified = false;
pageConfigObj.transTypeDictDataModified = false;
pageConfigObj.multiTradeDictDataModified = false;
pageConfigObj.businessTypeRelDataModified = false;

pageConfigObj.businessTypeAddKeys = new Array();
pageConfigObj.newRecordIdPrefix = "_NEW_";

//---------------------------------
//检查输入是否数字
//---------------------------------
pageConfigObj.checkValidityOnlyNumber = function () 
{ 
	if (!(event.keyCode == 46) && !(event.keyCode == 8) && !(event.keyCode == 37) && !(event.keyCode == 39)) 
		if (!((event.keyCode >= 48 && event.keyCode <= 57) || (event.keyCode >= 96 && event.keyCode <= 105))) 
			event.returnValue = false; 
}

pageConfigObj.initGeneralData = function() {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Config";
	cmd.method = "getGeneralParams";
	cmd.success = function (data) {
		for(var i = 0; i < data.rs.length; i++) {
			var paramNo = Utils.getRecordValue(data, i, "no").toLowerCase();
			if (paramNo == "transSystemName".toLowerCase()) {
				$("#txtSystemName").val(Utils.getRecordValue(data, i, "lvalue"));
			} else if (paramNo == "transSystemShortName".toLowerCase()) {
				$("#txtSystemShortName").val(Utils.getRecordValue(data, i, "lvalue"));
			}
		}
		pageConfigObj.checkGeneralModified = new FieldModifiedChecker($("#generalConfig")[0]);
	}
	cmd.execute();
}

pageConfigObj.initTransRuleData = function() {
	pageConfigObj.goodsTypeDictData = {Rows: [], Total: 0};
	pageConfigObj.businessTypeDictData = {Rows: [], Total: 0};
	pageConfigObj.licenseStepDictData = {Rows: [], Total: 0};
	pageConfigObj.transTypeDictData = {Rows: [], Total: 0};
	pageConfigObj.multiTradeDictData = {Rows: [], Total: 0};
	pageConfigObj.businessTypeRelData = {Rows: [], Total: 0};
	var goodsType = Utils.getPageValue("goodsType");
	var method = "getTransRuleConfig";
	if(goodsType == '501')
	method = "getTransRuleConfigPlow";
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Config";
	cmd.method =method;
	cmd.success = function (data) {
		if (data.state == 1) {
			pageConfigObj.transRuleConfig = data.transRule;
			pageConfigObj.tradeRuleXMLConfig = data.tradeRuleXML;
			pageConfigObj.modules = data.modules;
			//交易物类别字典
			if (pageConfigObj.transRuleConfig.dictionaries && pageConfigObj.transRuleConfig.dictionaries.goodsTypes) {
				var goodsTypesData = pageConfigObj.transRuleConfig.dictionaries.goodsTypes;
				var rows = new Array();
				for (var key in goodsTypesData) {
					var row = goodsTypesData[key];
					rows.push(row);
				}
				pageConfigObj.goodsTypeDictData = {Rows: rows, Total: rows.length};
			}
			//业务类别字典
			if (pageConfigObj.transRuleConfig.dictionaries && pageConfigObj.transRuleConfig.dictionaries.businessTypes) {
				var businessTypesData = pageConfigObj.transRuleConfig.dictionaries.businessTypes;
				var rows = new Array();
				for (var key in businessTypesData) {
					var row = businessTypesData[key];
					rows.push(row);
				}
				pageConfigObj.businessTypeDictData = {Rows: rows, Total: rows.length};
			}
			//竞买申请步骤字典
			if (pageConfigObj.transRuleConfig.dictionaries && pageConfigObj.transRuleConfig.dictionaries.steps) {
				var stepsData = pageConfigObj.transRuleConfig.dictionaries.steps;
				var rows = new Array();
				for (var key in stepsData) {
					var row = stepsData[key];
					rows.push(row);
				}
				pageConfigObj.licenseStepDictData = {Rows: rows, Total: rows.length};
			}
			//交易方式字典
			if (pageConfigObj.transRuleConfig.dictionaries && pageConfigObj.transRuleConfig.dictionaries.transTypes) {
				var transTypesData = pageConfigObj.transRuleConfig.dictionaries.transTypes;
				var rows = new Array();
				for (var key in transTypesData) {
					var row = transTypesData[key];
					rows.push(row);
				}
				pageConfigObj.transTypeDictData = {Rows: rows, Total: rows.length};
			}
			//多指标字典
			if (pageConfigObj.transRuleConfig.dictionaries && pageConfigObj.transRuleConfig.dictionaries.quotas) {
				var multiTradeData = pageConfigObj.transRuleConfig.dictionaries.quotas;
				var rows = new Array();
				for (var key in multiTradeData) {
					var row = multiTradeData[key];
					rows.push(row);
				}
				pageConfigObj.multiTradeDictData = {Rows: rows, Total: rows.length};
			}
			//交易类型
			if (pageConfigObj.transRuleConfig.businessTypes) {
				var businessTypeRelData = pageConfigObj.transRuleConfig.businessTypes;
				var rows = new Array();
				for (var key in businessTypeRelData) {
					var item = businessTypeRelData[key];
					var row = {};
					row.transTypes = {};
					for(var itemKey in item) {
						var itemValue = item[itemKey];
						if (typeof(itemValue) == "object") {
							if (itemValue)
								row.transTypes[itemKey] = itemValue;
						} else {
							row[itemKey] = itemValue;
						}
					}
					rows.push(row);
				}
				pageConfigObj.businessTypeRelData = {Rows: rows, Total: rows.length};
			}
		} else {
			DialogError('读取交易规则数据失败,错误原因：' + data.message);
			return false;
		}
	}
	cmd.execute();
}

pageConfigObj.initCAData = function() {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Config";
	cmd.method = "getCAParams";
	cmd.success = function (data) {
		if ("supportCAs" in data) {
			for(var i = 0; i < data.supportCAs.rs.length; i++) {
				$("#cboCA").append("<option value='" + Utils.getRecordValue(data.supportCAs, i, "lvalue") + "'>" + Utils.getRecordValue(data.supportCAs, i, "name") + "</option>");
			}
		}
		for(var i = 0; i < data.rs.length; i++) {
			var paramNo = Utils.getRecordValue(data, i, "no").toLowerCase();
			if (paramNo == "transCAType".toLowerCase()) {
				$("#cboCA").val(Utils.getRecordValue(data, i, "lvalue"));
			} else if (paramNo == "transCheckCABeforeOpenModule".toLowerCase()) {
				$("#chkCheckCABeforeOpenModule").attr("checked", Utils.getRecordValue(data, i, 'lvalue') == "1");
			}
		}
		pageConfigObj.checkCAModified = new FieldModifiedChecker($("#transCA")[0]);
	}
	cmd.execute();
}

pageConfigObj.saveData = function() {
	var blnModified = false;
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Config";
	cmd.method = "updateConfigParams";
	if (pageConfigObj.generalDataLoaded && pageConfigObj.checkGeneralModified.checkModified(true)) {
		cmd.transSystemName = $("#txtSystemName").val();
		cmd.transSystemShortName = $("#txtSystemShortName").val();
		blnModified = true;
	}
	if (pageConfigObj.transRuleDataLoaded) {
		var updateTransRuleConfig = {};
		//交易物类别字典
		if (pageConfigObj.goodsTypeDictGridManager
			&& (pageConfigObj.goodsTypeDictGridManager.isDataChanged || pageConfigObj.goodsTypeDictDataModified)) {
			var goodsTypes = {};
			var checkExistsName = ";";
			var gridData = pageConfigObj.goodsTypeDictGridManager.getData();
			for(var i = 0; i < gridData.length; i++) {
				var row = gridData[i];
				if (!row.name) {
					DialogAlert('交易物类别名称不能为空');
					return false;
				} else if (checkExistsName.indexOf(';' + row.name + ';') == -1) {
					checkExistsName += row.name + ';';
					var goodsType = {};
					for(var key in row)
						if (key.substr(0, 2) != "__")
							goodsType[key] = row[key];
					if (goodsType.id)
						goodsTypes[goodsType.id] = goodsType;
					else
						goodsTypes[pageConfigObj.newRecordIdPrefix + i] = goodsType;
				} else {
					DialogAlert('交易物类别名称不能重复');
					return false;
				}
			}
			if (!updateTransRuleConfig.dictionaries)
				updateTransRuleConfig.dictionaries = {};
			updateTransRuleConfig.dictionaries["goodsTypes"] = goodsTypes;
			blnModified = true;
		}
		//业务类别字典
		if (pageConfigObj.businessTypeDictGridManager
			&& (pageConfigObj.businessTypeDictGridManager.isDataChanged || pageConfigObj.businessTypeDictDataModified)) {
			var businessTypes = {};
			var checkExistsName = ";";
			var gridData = pageConfigObj.businessTypeDictGridManager.getData();
			for(var i = 0; i < gridData.length; i++) {
				var row = gridData[i];
				if (!row.name) {
					DialogAlert('业务类别名称不能为空');
					return false;
				} else if (checkExistsName.indexOf(';' + row.name + ';') == -1) {
					checkExistsName += row.name + ';';
					var businessType = {};
					for(var key in row)
						if (key.substr(0, 2) != "__")
							businessType[key] = row[key];
					if (businessType.id)
						businessTypes[businessType.id] = businessType;
					else
						businessTypes[pageConfigObj.newRecordIdPrefix + i] = businessType;
				} else {
					DialogAlert('业务类别名称不能重复');
					return false;
				}
			}
			if (!updateTransRuleConfig.dictionaries)
				updateTransRuleConfig.dictionaries = {};
			updateTransRuleConfig.dictionaries["businessTypes"] = businessTypes;
			blnModified = true;
		}
		//竞买申请步骤字典
		if (pageConfigObj.licenseStepDictGridManager
			&& (pageConfigObj.licenseStepDictGridManager.isDataChanged || pageConfigObj.licenseStepDictDataModified)) {
			var steps = {};
			var checkExistsName = ";";
			var gridData = pageConfigObj.licenseStepDictGridManager.getData();
			for(var i = 0; i < gridData.length; i++) {
				var row = gridData[i];
				if (!row.name) {
					DialogAlert('竞买申请步骤名称不能为空');
					return false;
				} else if (checkExistsName.indexOf(';' + row.name + ';') == -1) {
					checkExistsName += row.name + ';';
					var step = {};
					for(var key in row)
						if (key.substr(0, 2) != "__")
							step[key] = row[key];
					if (step.id)
						steps[step.id] = step;
					else
						steps[pageConfigObj.newRecordIdPrefix + i] = step;
				} else {
					DialogAlert('竞买申请步骤名称不能重复');
					return false;
				}
			}
			if (!updateTransRuleConfig.dictionaries)
				updateTransRuleConfig.dictionaries = {};
			updateTransRuleConfig.dictionaries["steps"] = steps;
			blnModified = true;
		}
		//交易方式字典
		if (pageConfigObj.transTypeDictGridManager
			&& (pageConfigObj.transTypeDictGridManager.isDataChanged || pageConfigObj.transTypeDictDataModified)) {
			var transTypes = {};
			var checkExistsName = ";";
			var gridData = pageConfigObj.transTypeDictGridManager.getData();
			for(var i = 0; i < gridData.length; i++) {
				var row = gridData[i];
				if (!row.name) {
					DialogAlert('交易方式名称不能为空');
					return false;
				} else if (checkExistsName.indexOf(';' + row.name + ';') == -1) {
					checkExistsName += row.name + ';';
					var transType = {};
					for(var key in row)
						if (key.substr(0, 2) != "__")
							transType[key] = row[key];
					if (transType.id)
						transTypes[transType.id] = transType;
					else
						transTypes[pageConfigObj.newRecordIdPrefix + i] = transType;
				} else {
					DialogAlert('交易方式名称不能重复');
					return false;
				}
			}
			if (!updateTransRuleConfig.dictionaries)
				updateTransRuleConfig.dictionaries = {};
			updateTransRuleConfig.dictionaries["transTypes"] = transTypes;
			blnModified = true;
		}
		//多指标字典
		if (pageConfigObj.multiTradeDictGridManager
			&& (pageConfigObj.multiTradeDictGridManager.isDataChanged || pageConfigObj.multiTradeDictDataModified)) {
			var quotas = {};
			var checkExistsName = ";";
			var gridData = pageConfigObj.multiTradeDictGridManager.getData();
			for(var i = 0; i < gridData.length; i++) {
				var row = gridData[i];
				if (!row.name) {
					DialogAlert('多指标名称不能为空');
					return false;
				} else if (!row.class_type) {
					DialogAlert('多指标处理类别不能为空');
					return false;
				} else if (checkExistsName.indexOf(';' + row.name + ';') == -1) {
					checkExistsName += row.name + ';';
					var quota = {};
					for(var key in row)
						if (key.substr(0, 2) != "__")
							quota[key] = row[key];
					if (quota.id)
						quotas[quota.id] = quota;
					else
						quotas[pageConfigObj.newRecordIdPrefix + i] = quota;
				} else {
					DialogAlert('多指标名称不能重复');
					return false;
				}
			}
			if (!updateTransRuleConfig.dictionaries)
				updateTransRuleConfig.dictionaries = {};
			updateTransRuleConfig.dictionaries["quotas"] = quotas;
			blnModified = true;
		}
		//业务类别
		if (pageConfigObj.businessTypeRelGridManager
			&& (pageConfigObj.businessTypeRelGridManager.isDataChanged || pageConfigObj.businessTypeRelDataModified)) {
			updateTransRuleConfig.businessTypes = {};
			var checkExistsId = ";";
			var checkExistsName = ";";
			var gridData = pageConfigObj.businessTypeRelGridManager.getData();
			for(var i = 0; i < gridData.length; i++) {
				var row = gridData[i];
				if (checkExistsName.indexOf(';' + row.name + ';') != -1) {
					DialogAlert('业务类别名称不能重复（如果未输入值则是由系统自动组装的名称）');
					return false;
				} else {
					checkExistsName += row.name + ';';
					var businessType = {};
					for (var key in row) {
						if (key.substr(0, 2) != "__" && key != "transTypes")
							businessType[key] = row[key];
					}
					for(var key in row.transTypes) {
						businessType[key] = row.transTypes[key];
					}
				}
				updateTransRuleConfig.businessTypes[businessType.id] = businessType;
			}
			blnModified = true;
		}
		cmd.updateTransRuleConfig = JSON.stringify(updateTransRuleConfig);
	}
	if (pageConfigObj.caDataLoaded && pageConfigObj.checkCAModified.checkModified(true)) {
		cmd.transCAType = $("#cboCA").val();
		if ($("#chkCheckCABeforeOpenModule").attr("checked")) {
				cmd.transCheckCABeforeOpenModule = 1;
			} else {
				cmd.transCheckCABeforeOpenModule = 0;
			}
		blnModified = true;
	}
	if (blnModified) {
		cmd.success = function (data) {
			var state = data.state;
			if(state == '1') {
				DialogAlert('保存设置数据成功完成');
				pageConfigObj.loadTransRuleData();
				if (pageConfigObj.checkGeneralModified) {
					pageConfigObj.checkGeneralModified.initFileds();
				}
				if (pageConfigObj.checkCAModified) {
					pageConfigObj.checkCAModified.initFileds();
				}
				pageConfigObj.goodsTypeDictDataModified = false;
				pageConfigObj.businessTypeDictDataModified = false;
				pageConfigObj.licenseStepDictDataModified = false;
				pageConfigObj.transTypeDictDataModified = false;
				pageConfigObj.multiTradeDictDataModified = false;
				pageConfigObj.businessTypeRelDataModified = false;
			} else {
				DialogError('保存设置数据失败,错误原因：' + data.message);
				return false;
			}
		}
		cmd.execute();
	} else {
		DialogAlert('保存设置数据成功完成');
	}
}

//---------------------------------
//标签TAB切换后事件
//---------------------------------
pageConfigObj.afterSelectTabItem = function(tabid) {
	if (tabid == 'tabitem1') {//常规
		if (!pageConfigObj.generalDataLoaded) {
			pageConfigObj.initGeneralData();
			pageConfigObj.generalDataLoaded = true;
		}
 	} else if (tabid == 'tabitem2') {//交易规则
 		if (!pageConfigObj.transRuleDataLoaded) {
 			pageConfigObj.loadTransRuleData();
 			pageConfigObj.transRuleDataLoaded = true;
		}
 	} else if (tabid == 'tabitem3') {//CA
		if (!pageConfigObj.caDataLoaded) {
			pageConfigObj.initCAData();
			pageConfigObj.caDataLoaded = true;
		}
 	}
}

//---------------------------------
//标签交易规则TAB切换后事件
//---------------------------------
pageConfigObj.afterSelectTabItemTrans = function(tabid) {
	if(tabid=='tabitem4'){
		pageConfigObj.initTransTypeDictGrid();
	}else if(tabid=='tabitem5'){
		pageConfigObj.initMultiTradeDictGrid();
	}
}

pageConfigObj.loadTransRuleData = function() {
	pageConfigObj.initTransRuleData();
	//if (pageConfigObj.goodsTypeDictGridManager == null)
		pageConfigObj.initGoodsTypeDictGrid();
	//if (pageConfigObj.businessTypeDictGridManager == null)
		pageConfigObj.initBusinessTypeDictGrid();
	//if (pageConfigObj.licenseStepDictGridManager == null)
		pageConfigObj.initLicenseStepDictGrid();
	//if (pageConfigObj.transTypeDictGridManager == null)
//		pageConfigObj.initTransTypeDictGrid();
	//if (pageConfigObj.multiTradeDictGridManager == null)
//		pageConfigObj.initMultiTradeDictGrid();
	//if (pageConfigObj.businessTypeRelGridManager == null)
		pageConfigObj.initBusinessTypeRelGrid();
}

pageConfigObj.initGoodsTypeDictGrid = function() {
	pageConfigObj.goodsTypeDictGridManager = $("#goodsTypeDictGrid").ligerGrid({
		columns: [
			{ display: '编号', name: 'no', width: 60,
				render: function (row) {
					return "<a href='javascript:pageConfigObj.editGoodsTypeDict()'>" + row.no + "</a>";
				}
			},
			{ display: '名称', name: 'name', width: 80,
				render: function (row) {
					return "<a href='javascript:pageConfigObj.editGoodsTypeDict()'>" + row.name + "</a>";
				}
			},
			{ display: '标的名称', name: 'goods_name', width: 100, editor: { type: 'text' } },
			{ display: '编号标签', name: 'no_label', width: 100, editor: { type: 'text' } },
			{ display: '名称标签', name: 'name_label', width: 100, editor: { type: 'text' } },
			{ display: '用途标签', name: 'use_label', width: 100, editor: { type: 'text' } },
			{ display: '编辑模块', name: 'edit_goods_module_id', width: 100,
				editor: { type: 'select',
					ext :
						function (rowdata) {
							return {
								onBeforeOpen: pageConfigObj.goodsTypeDictSelectEditModule,
								render: function () { 
									if (rowdata.edit_goods_module_id && pageConfigObj.modules && rowdata.edit_goods_module_id in pageConfigObj.modules)
										return pageConfigObj.modules[rowdata.edit_goods_module_id];
									else
										return '';
								}
							};
						}
				},
				render: function (row) {
					if (row.edit_goods_module_id && pageConfigObj.modules && row.edit_goods_module_id in pageConfigObj.modules) {
						return pageConfigObj.modules[row.edit_goods_module_id];
					} else
						return '';
				}
			},
			{ display: '编辑模块参数', name: 'edit_goods_module_params', width: 100, editor: { type: 'text' } },
			{ display: '编辑模块URL', name: 'edit_goods_module_url', width: 100, editor: { type: 'text' } },
			{ display: '查看模块', name: 'view_goods_module_id', width: 100,
				editor: { type: 'select',
					ext :
						function (rowdata) {
							return {
								onBeforeOpen: pageConfigObj.goodsTypeDictSelectViewModule,
								render: function () { 
									if (rowdata.view_goods_module_id && pageConfigObj.modules && rowdata.view_goods_module_id in pageConfigObj.modules)
										return pageConfigObj.modules[rowdata.view_goods_module_id];
									else
										return '';
								}
							};
						}
				},
				render: function (row) {
					if (row.view_goods_module_id && pageConfigObj.modules && row.view_goods_module_id in pageConfigObj.modules) {
						return pageConfigObj.modules[row.view_goods_module_id];
					} else
						return '';
				}
			},
			{ display: '查看模块参数', name: 'view_goods_module_params', width: 100, editor: { type: 'text' } },
			{ display: '查看模块URL', name: 'view_goods_module_url', width: 100, editor: { type: 'text' } }
		],
		data: pageConfigObj.goodsTypeDictData,
		usePager: false,
		isScroll: true,
		showTitle: false,
		width: 830,
		rownumbers : true,
		height: 170,
		enabledEdit: true,
		toolbar: { items: [
			{ text: '新增', id: 'btnAddGoodsTypeDict', click: pageConfigObj.addGoodsTypeDict, icon: 'add' },
			{ line: true },
			{ text: '删除', id: 'btnDeleteGoodsTypeDict', click: pageConfigObj.deleteGoodsTypeDict, icon: 'delete' }
		]}
	});
}

pageConfigObj.goodsTypeDictSelectEditModule = function () {
	DialogOpen({ title: '选择编辑模块', width: 312, height: 540, url: approot + '/sysman/module.html?selectData=1&u=' + pageConfigObj.userId,
		buttons: [
			{ text: '确定', onclick: function (item, dialog) {
					var data = dialog.frame.pageModuleObj.getSelectedData();
					if (!data) {
						return false;
					}
					if (!pageConfigObj.modules)
						pageConfigObj.modules = {};
					pageConfigObj.modules[data.data.id] = data.data.text;
					pageConfigObj.goodsTypeDictGridManager.updateCell('edit_goods_module_id', data.data.id, pageConfigObj.goodsTypeDictGridManager.getSelected());
					pageConfigObj.goodsTypeDictGridManager.endEdit();
					pageConfigObj.goodsTypeDictDataModified = true;
					dialog.close();
				}
			},
			{ text: '取消', onclick: function (item, dialog) {
					dialog.close();
				}
 			}
		]
	});
	return false;
}

pageConfigObj.goodsTypeDictSelectViewModule = function () {
	DialogOpen({ title: '选择查看模块', width: 312, height: 540, url: approot + '/sysman/module.html?selectData=1&u=' + pageConfigObj.userId,
		buttons: [
			{ text: '确定', onclick: function (item, dialog) {
					var data = dialog.frame.pageModuleObj.getSelectedData();
					if (!data) {
						return false;
					}
					if (!pageConfigObj.modules)
						pageConfigObj.modules = {};
					pageConfigObj.modules[data.data.id] = data.data.text;
					pageConfigObj.goodsTypeDictGridManager.updateCell('view_goods_module_id', data.data.id, pageConfigObj.goodsTypeDictGridManager.getSelected());
					pageConfigObj.goodsTypeDictGridManager.endEdit();
					pageConfigObj.goodsTypeDictDataModified = true;
					dialog.close();
				}
			},
			{ text: '取消', onclick: function (item, dialog) {
					dialog.close();
				}
 			}
		]
	});
	return false;
}

pageConfigObj.initBusinessTypeDictGrid = function() {
	pageConfigObj.businessTypeDictGridManager = $("#businessTypeDictGrid").ligerGrid({
		columns: [
			{ display: '编号', name: 'no', width: 200,
				render: function (row) {
					return "<a href='javascript:pageConfigObj.editBusinessTypeDict()'>" + row.no + "</a>";
				}
			},
			{ display: '名称', name: 'name', width: 200,
				render: function (row) {
					return "<a href='javascript:pageConfigObj.editBusinessTypeDict()'>" + row.name + "</a>";
				}
			}
		],
		data: pageConfigObj.businessTypeDictData,
		usePager: false,
		isScroll: true,
		showTitle: false,
		width: 830,
		height: 170,
		enabledEdit: true,
		toolbar: { items: [
			{ text: '新增', id: 'btnAddBusinessTypeDict', click: pageConfigObj.addBusinessTypeDict, icon: 'add' },
			{ line: true },
			{ text: '删除', id: 'btnDeleteBusinessTypeDict', click: pageConfigObj.deleteBusinessTypeDict, icon: 'delete' }
		]}
	});
}

pageConfigObj.initLicenseStepDictGrid = function() {
	pageConfigObj.licenseStepDictGridManager = $("#licenseStepDictGrid").ligerGrid({
		columns: [
			{ display: '名称', name: 'name', width: 200,
				render: function (row) {
					return "<a href='javascript:pageConfigObj.editLicenseStepDict()'>" + row.name + "</a>";
				}
			},
			{ display: '模块', name: 'module_id', width: 180,
				editor: { type: 'select',
					ext :
						function (rowdata) {
							return {
								onBeforeOpen: pageConfigObj.licenseStepDictSelectModule,
								render: function () { 
									if (pageConfigObj.modules && rowdata.module_id in pageConfigObj.modules)
										return pageConfigObj.modules[rowdata.module_id];
									else
										return '';
								}
							};
						}
				},
				render: function (row) {
					var moduleId = row.module_id;
					if (pageConfigObj.modules && moduleId in pageConfigObj.modules) {
						return pageConfigObj.modules[moduleId];
					} else
						return '';
				}
			},
			{ display: '模块参数', name: 'module_params', width: 120, editor: { type: 'text' } },
			{ display: '地址', name: 'module_url', width: 150, editor: { type: 'text' } }
		],
		data: pageConfigObj.licenseStepDictData,
		usePager: false,
		isScroll: true,
		showTitle: false,
		width: 830,
		height: 170,
		enabledEdit: true,
		toolbar: { items: [
			{ text: '新增', id: 'btnAddLicenseStepDict', click: pageConfigObj.addLicenseStepDict, icon: 'add' },
			{ line: true },
			{ text: '删除', id: 'btnDeleteLicenseStepDict', click: pageConfigObj.deleteLicenseStepDict, icon: 'delete' }
		]}
	});
}

pageConfigObj.licenseStepDictSelectModule = function () {
	DialogOpen({ title: '选择模块', width: 312, height: 540, url: approot + '/sysman/module.html?selectData=1&u=' + pageConfigObj.userId,
		buttons: [
			{ text: '确定', onclick: function (item, dialog) {
					var data = dialog.frame.pageModuleObj.getSelectedData();
					if (!data) {
						return false;
					}
					if (!pageConfigObj.modules)
						pageConfigObj.modules = {};
					pageConfigObj.modules[data.data.id] = data.data.text;
					var selectRow = pageConfigObj.licenseStepDictGridManager.getSelected();
					pageConfigObj.licenseStepDictGridManager.updateCell('module_id', data.data.id, selectRow);
					pageConfigObj.licenseStepDictGridManager.endEdit();
					//pageConfigObj.licenseStepDictGridManager.getSelected().module_id = data.data.id;
					//pageConfigObj.licenseStepDictGridManager.reRender();
					pageConfigObj.licenseStepDictDataModified = true;
					dialog.close();
				}
			},
			{ text: '取消', onclick: function (item, dialog) {
					dialog.close();
				}
 			}
		]
	});
	return false;
}

pageConfigObj.initTransTypeDictGrid = function() {
	pageConfigObj.transTypeDictGridManager = $("#transTypeDictGrid").ligerGrid({
		columns: [
			{ display: '名称', name: 'name', width: 120,
				render: function (row) {
					return "<a href='javascript:pageConfigObj.editTransTypeDict()'>" + row.name + "</a>";
				}
			},
			{ display: '交易方式', name: 'trans_type', width: 80, type:'int',
				editor: { type: 'select', data: [{ id: 0, text: '挂牌' }, { id: 1, text: '拍卖'}], valueColumnName: 'id' },
				render: function (row) {
					if (parseInt(row.trans_type) == 1) return '拍卖';
					return '挂牌';
				}
			},
			{ display: '网上交易', name: 'is_net_trans', width: 80, type:'int',
				editor: { type: 'select', data: [{ id: 1, text: '是' }, { id: 0, text: '否'}], valueColumnName: 'id' },
				render: function (row) {
					if (parseInt(row.is_net_trans) == 1) return '是';
					return '<span style="color:gray">否</span>';
				}
			},
			{ display: '是否进行限时竞价', name: 'is_limit_trans', width: 120, type:'int',
				editor: { type: 'select', data: [{ id: 1, text: '是' }, { id: 0, text: '否'}], valueColumnName: 'id' },
				render: function (row) {
					if (parseInt(row.is_limit_trans) == 1) return '是';
					return '<span style="color:gray">否</span>';
				}
			},
			{ display: '允许直播', name: 'allow_live', width: 60, type:'int',
				editor: { type: 'select', data: [{ id: 1, text: '是' }, { id: 0, text: '否'}], valueColumnName: 'id' },
				render: function (row) {
					if (parseInt(row.allow_live) == 1) return '是';
					return '<span style="color:gray">否</span>';
				}
			},
			{ display: '允许联合申请', name: 'allow_union', width: 80, type:'int',
				editor: { type: 'select', data: [{ id: 1, text: '是' }, { id: 0, text: '否'}], valueColumnName: 'id' },
				render: function (row) {
					if (parseInt(row.allow_union) == 1) return '是';
					return '<span style="color:gray">否</span>';
				}
			},
			{ display: '允许委托报价', name: 'allow_trust', width: 80, type:'int',
				editor: { type: 'select', data: [{ id: 1, text: '是' }, { id: 0, text: '否'}], valueColumnName: 'id' },
				render: function (row) {
					if (parseInt(row.allow_trust) == 1) return '是';
					return '<span style="color:gray">否</span>';
				}
			},
			{ display: '允许多指标', name: 'allow_multi_trade', width: 70, type:'int',
				editor: { type: 'select', data: [{ id: 1, text: '是' }, { id: 0, text: '否'}], valueColumnName: 'id' },
				render: function (row) {
					if (parseInt(row.allow_multi_trade) == 1) return '是';
					return '<span style="color:gray">否</span>';
				}
			},
			{ display: '公告时长(天)', name: 'end_notice_time', width: 80, type: 'int', editor: { type: 'int'} },
			{ display: '挂牌时长(天)', name: 'end_list_time', width: 80, type: 'int', editor: { type: 'int'} },
			{ display: '挂牌报价时长(天)', name: 'end_focus_time', width: 120, type: 'float', editor: { type: 'float'} },
			{ display: '仅出价者可进入', name: 'enter_flag_0', width: 110, type:'int',
				editor: { type: 'select', data: [{ id: 1, text: '是' }, { id: 0, text: '否'}], valueColumnName: 'id' },
				render: function (row) {
					if (parseInt(row.enter_flag_0) == 1) return '是';
					return '<span style="color:gray">否</span>';
				}
			},
			{ display: '必须申请人数', name: 'enter_flag_1', width: 100, type: 'int', editor: { type: 'int'} },
			{ display: '必须出价人数', name: 'enter_flag_2', width: 100, type: 'int', editor: { type: 'int'} }
		],
		data: pageConfigObj.transTypeDictData,
		usePager: false,
		isScroll: true,
		showTitle: false,
		width: 830,
		height: 170,
		rownumbers:true,
		enabledEdit: true,
		toolbar: { items: [
			{ text: '新增', id: 'btnAddTransTypeDict', click: pageConfigObj.addTransTypeDict, icon: 'add' },
			{ line: true },
			{ text: '删除', id: 'btnDeleteTransTypeDict', click: pageConfigObj.deleteTransTypeDict, icon: 'delete' }
		]}
	});
}

pageConfigObj.initMultiTradeDictGrid = function() {
	var multiTradeType = new Array();
	if (pageConfigObj.tradeRuleXMLConfig && pageConfigObj.tradeRuleXMLConfig.length > 0) {
		for(var i = 0; i < pageConfigObj.tradeRuleXMLConfig.length; i++)
			multiTradeType.push({ id: pageConfigObj.tradeRuleXMLConfig[i].id,
				text: pageConfigObj.tradeRuleXMLConfig[i].name + "(" + pageConfigObj.tradeRuleXMLConfig[i].id + ")" });
	}
	pageConfigObj.multiTradeDictGridManager = $("#multiTradeDictGrid").ligerGrid({
		columns: [
			{ display: '名称', name: 'name', width: 120,
				render: function (row) {
					return "<a href='javascript:pageConfigObj.editMultiTradeDict()'>" + row.name + "</a>";
				}
			},
			{ display: '处理类别', name: 'class_type', width: 120,
				editor: { type: 'select', data: multiTradeType, valueColumnName: 'id' },
				render: function (row) {
					if (pageConfigObj.tradeRuleXMLConfig && pageConfigObj.tradeRuleXMLConfig.length > 0) {
						for(var i = 0; i < pageConfigObj.tradeRuleXMLConfig.length; i++) {
							if (pageConfigObj.tradeRuleXMLConfig[i].id == row.class_type)
								return pageConfigObj.tradeRuleXMLConfig[i].name + "(" + pageConfigObj.tradeRuleXMLConfig[i].id + ")";
						}
					} else
						return '';
				}
			},
			{ display: '单位', name: 'unit', width: 60, editor: { type: 'text' } },
			{ display: '仅出价者可进入', name: 'enter_flag_0', width: 120, type:'int',
				editor: { type: 'select', data: [{ id: 1, text: '是' }, { id: 0, text: '否'}], valueColumnName: 'id' },
				render: function (row) {
					if (parseInt(row.enter_flag_0) == 1) return '是';
					return '<span style="color:gray">否</span>';
				}
			},
			{ display: '必须申请人数', name: 'enter_flag_1', width: 100, editor: { type: 'int'} },
			{ display: '必须出价人数', name: 'enter_flag_2', width: 100, type: 'int', editor: { type: 'int'} },
			{ display: '进入等待(秒)', name: 'first_wait', width: 100, type: 'int', editor: { type: 'int'} },
			{ display: '间隔时长(秒)', name: 'limit_wait', width: 100, type: 'int', editor: { type: 'int'} },
			{ display: '结束等待(秒)', name: 'last_wait', width: 100, type: 'int', editor: { type: 'int'} }
		],
		data: pageConfigObj.multiTradeDictData,
		usePager: false,
		isScroll: true,
		showTitle: false,
		width: 830,
		height: 170,
		rownumbers:true,
		enabledEdit: true,
		toolbar: { items: [
			{ text: '新增', id: 'btnAddMultiTradeDict', click: pageConfigObj.addMultiTradeDict, icon: 'add' },
			{ line: true },
			{ text: '删除', id: 'btnDeleteMultiTradeDict', click: pageConfigObj.deleteMultiTradeDict, icon: 'delete' }
		]}
	});
}

pageConfigObj.initBusinessTypeRelGrid = function() {
	pageConfigObj.businessTypeRelGridManager = $("#businessTypeRelGrid").ligerGrid({
		//checkbox: true,
		columns: [
			{ display: '交易物类别', name: 'goods_type_id', width: 200, render: pageConfigObj.goodsTypeIdRender },
			{ display: '业务类别', name: 'business_type_id', width: 120, render: pageConfigObj.businessTypeIdRender },
			{ display: '名称', name: 'name', width: 120 }
		],
		data: pageConfigObj.businessTypeRelData,
		usePager: false,
		isScroll: true,
		showTitle: false,
		width: 830,
		height: 200,
		toolbar: { items: [
			{ text: '新增', id: 'btnAddBusinessType', click: pageConfigObj.addBusinessTypeRel, icon: 'add' },
			{ line: true },
			{ text: '修改', id: 'btnEditBusinessType', click: pageConfigObj.editBusinessTypeRel, icon: 'modify' },
			{ line: true },
			{ text: '删除', id: 'btnDeleteBusinessType', click: pageConfigObj.deleteBusinessTypeRel, icon: 'delete' }
		]}
	});
}

pageConfigObj.goodsTypeIdRender = function (row, rowNamber) {
	var goodsTypeName = "";
	var dictGridData = pageConfigObj.goodsTypeDictGridManager.getData();
	for(var i = 0; i < dictGridData.length; i++) {
		var dictRow = dictGridData[i];
		if (dictRow.id == row.goods_type_id) {
			goodsTypeName = dictRow.name;
			break;
		}
	}
	if (!goodsTypeName)
		goodsTypeName = row.goods_type_id;
	return "<a href='javascript:pageConfigObj.editBusinessTypeRel()'>" + goodsTypeName + "</a>";
}

pageConfigObj.businessTypeIdRender = function (row, rowNamber) {
	var businessTypeName = "";
	var dictGridData = pageConfigObj.businessTypeDictGridManager.getData();
	for(var i = 0; i < dictGridData.length; i++) {
		var dictRow = dictGridData[i];
		if (dictRow.id == row.business_type_id) {
			businessTypeName = dictRow.name;
			break;
		}
	}
	if (!businessTypeName)
		businessTypeName = row.business_type_id;
	return "<a href='javascript:pageConfigObj.editBusinessTypeRel()'>" + businessTypeName + "</a>";
}

pageConfigObj.addGoodsTypeDict = function () {
	$("#txtDictItemName").val("");
	$("#inputDictNoPanel").show();
	$("#txtDictItemNo").val("");
	$.ligerDialog.open({ height: 150, width: 310, title: '新增交易物类别', target: $("#inputDictInfoPanel"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				var newGoodsTypeNo = $("#txtDictItemNo").val();
				var newGoodsTypeName = $("#txtDictItemName").val();
				if (newGoodsTypeNo && newGoodsTypeName) {
					var gridData = pageConfigObj.goodsTypeDictGridManager.getData();
					for(var i = 0; i < gridData.length; i++) {
						if (gridData[i].no == newGoodsTypeNo) {
							DialogAlert('交易物类别编号' + newGoodsTypeNo + '已经存在，不能重复。');
							return false;
						}
						if (gridData[i].name == newGoodsTypeName) {
							DialogAlert('交易物类别名称' + newGoodsTypeName + '已经存在，不能重复。');
							return false;
						}
					}
					pageConfigObj.goodsTypeDictGridManager.addRow({
						id: pageConfigObj.newRecordIdPrefix + newGUID(true),
						no: newGoodsTypeNo,
						name: newGoodsTypeName
					});
					pageConfigObj.goodsTypeDictDataModified = true;
				} else {
					DialogAlert('新增的交易物类别编号和名称不能为空。');
					return false;
				}
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	});
}

pageConfigObj.editGoodsTypeDict = function () {
	//仅修改名称，其它值直接在表格中修改
	var selectedRow = pageConfigObj.goodsTypeDictGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要修改的交易物类别字典项。');
		return false;
	}
	var oldGoodsTypeNo = selectedRow.no;
	var oldGoodsTypeName = selectedRow.name;
	$("#txtDictItemName").val(oldGoodsTypeName);
	$("#inputDictNoPanel").show();
	$("#txtDictItemNo").val(oldGoodsTypeNo);
	$.ligerDialog.open({ height: 150, width: 310, title: '修改交易物类别', target: $("#inputDictInfoPanel"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				var newGoodsTypeNo = $("#txtDictItemNo").val();
				var newGoodsTypeName = $("#txtDictItemName").val();
				if (newGoodsTypeNo && newGoodsTypeName)
					if (oldGoodsTypeNo != newGoodsTypeNo || oldGoodsTypeName != newGoodsTypeName) {
						var gridData = pageConfigObj.goodsTypeDictGridManager.getData();
						for(var i = 0; i < gridData.length; i++) {
							if (oldGoodsTypeNo != newGoodsTypeNo && gridData[i].no == newGoodsTypeNo) {
								DialogAlert('交易物类别编号' + newGoodsTypeNo + '已经存在，不能重复。');
								return false;
							} else if (oldGoodsTypeName != newGoodsTypeName && gridData[i].name == newGoodsTypeName) {
								DialogAlert('交易物类别名称' + newGoodsTypeName + '已经存在，不能重复。');
								return false;
							}
						}
						pageConfigObj.goodsTypeDictGridManager.updateCell("no", newGoodsTypeNo, selectedRow);
						pageConfigObj.goodsTypeDictGridManager.updateCell("name", newGoodsTypeName, selectedRow);
						pageConfigObj.businessTypeRelGridManager.reRender();
						pageConfigObj.goodsTypeDictDataModified = true;
					}
				else {
					DialogAlert('交易物类别编号和名称都不能为空。');
					return false;
				}
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	});
}

pageConfigObj.deleteGoodsTypeDict = function () {
	var selectedRow = pageConfigObj.goodsTypeDictGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要删除的交易物类别字典项。');
		return false;
	} else {
		//删除前检查是否有引用的业务类别关联数据，有则不允许删除
		var goodsTypeId = selectedRow.id;
		for(var i = 0; i < pageConfigObj.businessTypeRelGridManager.data.Rows.length; i++) {
			var row = pageConfigObj.businessTypeRelGridManager.data.Rows[i];
			if (row.goods_type_id == goodsTypeId) {
				DialogAlert('要删除的交易物类别已经被引用，请先删除业务规则中的引用数据。');
				return false;
			}
		}
		DialogConfirm("确定要删除选择的交易物类别字典项 " + selectedRow.name + "？", function (yes) {
			if (yes) {
				var oldGoodsTypeId = selectedRow.id;
				var oldGoodsTypeName = selectedRow.name;
				pageConfigObj.goodsTypeDictGridManager.deleteSelectedRow();
				pageConfigObj.goodsTypeDictDataModified = true;
			}
		});
	}
}

pageConfigObj.addBusinessTypeDict = function () {
	$("#txtDictItemName").val("");
	$("#inputDictNoPanel").show();
	$("#txtDictItemNo").val("");
	$.ligerDialog.open({ height: 150, width: 310, title: '新增业务类别', target: $("#inputDictInfoPanel"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				var newBusinessTypeNo = $("#txtDictItemNo").val();
				var newBusinessTypeName = $("#txtDictItemName").val();
				if (newBusinessTypeNo && newBusinessTypeName) {
					var gridData = pageConfigObj.businessTypeDictGridManager.getData();
					for(var i = 0; i < gridData.length; i++) {
						if (gridData[i].no == newBusinessTypeNo) {
							DialogAlert('业务类别编号' + newBusinessTypeNo + '已经存在，不能重复。');
							return false;
						}
						if (gridData[i].name == newBusinessTypeName) {
							DialogAlert('业务类别名称' + newBusinessTypeName + '已经存在，不能重复。');
							return false;
						}
					}
					pageConfigObj.businessTypeDictGridManager.addRow({
						id: pageConfigObj.newRecordIdPrefix + newGUID(true),
						no: newBusinessTypeNo,
						name: newBusinessTypeName
					});
					pageConfigObj.businessTypeDictDataModified = true;
				} else {
					DialogAlert('新增的业务类别编号和名称不能为空。');
					return false;
				}
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	});
}

pageConfigObj.editBusinessTypeDict = function () {
	//仅修改名称，其它值直接在表格中修改
	var selectedRow = pageConfigObj.businessTypeDictGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要修改的业务类别字典项。');
		return false;
	}
	var oldBusinessTypeNo = selectedRow.no;
	var oldBusinessTypeName = selectedRow.name;
	$("#txtDictItemName").val(oldBusinessTypeName);
	$("#inputDictInfoPanel").show();
	$("#txtDictItemNo").val(oldBusinessTypeNo);
	$.ligerDialog.open({ height: 150, width: 310, title: '修改业务类别', target: $("#inputDictInfoPanel"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				var newBusinessTypeNo = $("#txtDictItemNo").val();
				var newBusinessTypeName = $("#txtDictItemName").val();
				if (newBusinessTypeNo && newBusinessTypeName)
					if (oldBusinessTypeNo != newBusinessTypeNo || oldBusinessTypeName != newBusinessTypeName) {
						var gridData = pageConfigObj.businessTypeDictGridManager.getData();
						for(var i = 0; i < gridData.length; i++) {
							if (oldBusinessTypeNo != newBusinessTypeNo && gridData[i].no == newBusinessTypeNo) {
								DialogAlert('业务类别编号' + newBusinessTypeNo + '已经存在，不能重复。');
								return false;
							} else if (oldBusinessTypeName != newBusinessTypeName && gridData[i].name == newBusinessTypeName) {
								DialogAlert('业务类别名称' + newBusinessTypeName + '已经存在，不能重复。');
								return false;
							}
						}
						pageConfigObj.businessTypeDictGridManager.updateCell("no", newBusinessTypeNo, selectedRow);
						pageConfigObj.businessTypeDictGridManager.updateCell("name", newBusinessTypeName, selectedRow);
						pageConfigObj.businessTypeRelGridManager.reRender();
						pageConfigObj.businessTypeDictDataModified = true;
					}
				else {
					DialogAlert('业务类别编号和名称都不能为空。');
					return false;
				}
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	});
}

pageConfigObj.deleteBusinessTypeDict = function () {
	var selectedRow = pageConfigObj.businessTypeDictGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要删除的业务类别字典项。');
		return false;
	} else {
		//删除前检查是否有引用的业务类别关联数据，有则不允许删除
		var businessTypeId = selectedRow.id;
		for(var i = 0; i < pageConfigObj.businessTypeRelGridManager.data.Rows.length; i++) {
			var row = pageConfigObj.businessTypeRelGridManager.data.Rows[i];
			if (row.business_type_id == businessTypeId) {
				DialogAlert('要删除的业务类别已经被引用，请先删除业务规则中的引用数据。');
				return false;
			}
		}
		DialogConfirm("确定要删除选择的业务类别字典项 " + selectedRow.name + "？", function (yes) {
			if (yes) {
				var oldBusinessTypeId = selectedRow.id;
				var oldBusinessTypeName = selectedRow.name;
				pageConfigObj.businessTypeDictGridManager.deleteSelectedRow();
				pageConfigObj.businessTypeDictDataModified = true;
			}
		});
	}
}

pageConfigObj.addLicenseStepDict = function () {
	$("#txtDictItemName").val("");
	$.ligerDialog.open({ height: 50, width: 310, title: '新增竞买申请步骤', target: $("#inputDictInfoPanel"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				var newLicenseStepName = $("#txtDictItemName").val();
				if (newLicenseStepName) {
					var gridData = pageConfigObj.licenseStepDictGridManager.getData();
					for(var i = 0; i < gridData.length; i++) {
						if (gridData[i].name == newLicenseStepName) {
							DialogAlert('竞买申请步骤名称' + newLicenseStepName + '已经存在，不能重复。');
							return false;
						}
					}
					pageConfigObj.licenseStepDictGridManager.addRow({
						id: pageConfigObj.newRecordIdPrefix + newGUID(true),
						name: newLicenseStepName
					});
					pageConfigObj.licenseStepDictDataModified = true;
				} else {
					DialogAlert('新增的竞买申请步骤名称不能为空。');
					return false;
				}
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	});
}

pageConfigObj.editLicenseStepDict = function () {
	//仅修改名称，其它值直接在表格中修改
	var selectedRow = pageConfigObj.licenseStepDictGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要修改的竞买申请步骤字典项。');
		return false;
	}
	var oldLicenseStepName = selectedRow.name;
	$("#txtDictItemName").val(oldLicenseStepName);
	$("#inputDictNoPanel").hide();
	$.ligerDialog.open({ height: 50, width: 310, title: '修改竞买申请步骤名称', target: $("#inputDictInfoPanel"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				var newLicenseStepName = $("#txtDictItemName").val();
				if (newLicenseStepName)
					if (oldLicenseStepName != newLicenseStepName) {
						var gridData = pageConfigObj.licenseStepDictGridManager.getData();
						for(var i = 0; i < gridData.length; i++) {
							if (gridData[i].name == newLicenseStepName) {
								DialogAlert('竞买申请步骤名称' + newLicenseStepName + '已经存在，不能重复。');
								return false;
							}
						}
						pageConfigObj.licenseStepDictGridManager.updateCell("name", newLicenseStepName, selectedRow);
						pageConfigObj.licenseStepDictDataModified = true;
					}
				else {
					DialogAlert('竞买申请步骤名称不能为空。');
					return false;
				}
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	});
}

pageConfigObj.deleteLicenseStepDict = function () {
	var selectedRow = pageConfigObj.licenseStepDictGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要删除的竞买申请步骤字典项。');
		return false;
	} else {
		DialogConfirm("确定要删除选择的竞买申请步骤字典项 " + selectedRow.name + "？<br/>删除字典记录将会同步删除所有业务类别范围中引用的当前被删除的竞买申请步骤记录。", function (yes) {
			if (yes) {
				var licenseStepId = selectedRow.id;
				pageConfigObj.licenseStepDictGridManager.deleteSelectedRow();
				pageConfigObj.licenseStepDictDataModified = true;
				//删除所有引用的数据
				for(var i = 0; i < pageConfigObj.businessTypeRelGridManager.data.Rows.length; i++) {
					var row = pageConfigObj.businessTypeRelGridManager.data.Rows[i];
					var transTypes = row.transTypes;
					if (transTypes) {
						for(var key in transTypes) {
							var transType = transTypes[key];
							var steps = transType.steps;
							if (steps) {
								for(var j = 0; j < steps.length; j++) {
									if (steps[j].license_step_id == licenseStepId) {
										steps.splice(j, 1);
										pageConfigObj.businessTypeRelDataModified = true;
										break;
									}
								}
							}
						}
					}
				}
			}
		});
	}
}

pageConfigObj.addTransTypeDict = function () {
	$("#txtDictItemName").val("");
	$("#inputDictNoPanel").hide();
	$.ligerDialog.open({ height: 50, width: 310, title: '新增交易方式', target: $("#inputDictInfoPanel"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				var newTransTypeName = $("#txtDictItemName").val();
				if (newTransTypeName) {
					var gridData = pageConfigObj.transTypeDictGridManager.getData();
					for(var i = 0; i < gridData.length; i++) {
							if (gridData[i].name == newTransTypeName) {
								DialogAlert('交易方式名称' + newTransTypeName + '已经存在，不能重复。');
								return false;
							}
						}
					pageConfigObj.transTypeDictGridManager.addRow({
						id: pageConfigObj.newRecordIdPrefix + newGUID(true),
						name: newTransTypeName,
						trans_type: 0,
						is_net_trans: 1,
						is_limit_trans: 1,
						allow_live: 0,
						allow_union: 1,
						allow_trust: 0,
						end_notice_time: 30,
						end_list_time: 30,
						end_focus_time: 30,
						allow_multi_trade: 0,
						enter_flag_0: 0,
						enter_flag_1: 2,
						enter_flag_2: 0
					});
					pageConfigObj.transTypeDictDataModified = true;
				} else {
					DialogAlert('新增的交易方式名称不能为空。');
					return false;
				}
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	});	
}

pageConfigObj.editTransTypeDict = function () {
	//仅修改名称，其它值直接在表格中修改
	var selectedRow = pageConfigObj.transTypeDictGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要修改的交易方式字典项。');
		return false;
	}
	var oldTransTypeName = selectedRow.name;
	$("#txtDictItemName").val(oldTransTypeName);
	$("#inputDictNoPanel").hide();
	$.ligerDialog.open({ height: 50, width: 310, title: '修改交易方式名称', target: $("#inputDictInfoPanel"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				var newTransTypeName = $("#txtDictItemName").val();
				if (newTransTypeName)
					if (oldTransTypeName != newTransTypeName) {
						var gridData = pageConfigObj.transTypeDictGridManager.getData();
						for(var i = 0; i < gridData.length; i++) {
							if (gridData[i].name == newTransTypeName) {
								DialogAlert('交易方式名称' + newTransTypeName + '已经存在，不能重复。');
								return false;
							}
						}
						pageConfigObj.transTypeDictGridManager.updateCell("name", newTransTypeName, selectedRow);
						pageConfigObj.transTypeDictDataModified = true;
					}
				else {
					DialogAlert('交易方式名称不能为空。');
					return false;
				}
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	});
}

pageConfigObj.deleteTransTypeDict = function () {
	var selectedRow = pageConfigObj.transTypeDictGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要删除的交易方式字典项。');
		return false;
	} else {
		DialogConfirm("确定要删除选择的交易方式字典项 " + selectedRow.name + "？<br/>删除字典记录将会同步删除所有业务类别范围中引用的当前被删除的交易方式记录。", function (yes) {
			if (yes) {
				var transTypeId = selectedRow.id;
				pageConfigObj.transTypeDictGridManager.deleteSelectedRow();
				pageConfigObj.transTypeDictDataModified = true;
				//删除所有引用的数据
				for(var i = 0; i < pageConfigObj.businessTypeRelGridManager.data.Rows.length; i++) {
					var row = pageConfigObj.businessTypeRelGridManager.data.Rows[i];
					var transTypes = row.transTypes;
					if (transTypes) {
						for(var key in transTypes) {
							var transType = transTypes[key];
							if (transType.trans_type_id == transTypeId) {
								delete transTypes[key];
								pageConfigObj.businessTypeRelDataModified = true;
								break;
							}
						}
					}
				}
			}
		});
	}
}

pageConfigObj.addMultiTradeDict = function () {
	$("#txtDictItemName").val("");
	$("#inputDictNoPanel").hide();
	$.ligerDialog.open({ height: 50, width: 310, title: '新增多指标', target: $("#inputDictInfoPanel"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				var newMultiTradeName = $("#txtDictItemName").val();
				if (newMultiTradeName) {
					var gridData = pageConfigObj.multiTradeDictGridManager.getData();
					for(var i = 0; i < gridData.length; i++) {
							if (gridData[i].name == newMultiTradeName) {
								DialogAlert('多指标名称' + newMultiTradeName + '已经存在，不能重复。');
								return false;
							}
						}
					pageConfigObj.multiTradeDictGridManager.addRow({
						id: pageConfigObj.newRecordIdPrefix + newGUID(true), 
						name: newMultiTradeName,
						enter_flag_0: 0,
						enter_flag_1: 2,
						enter_flag_2: 0,
						first_wait: 480,
						limit_wait: 300,
						last_wait: 10
					});
					pageConfigObj.multiTradeDictDataModified = true;
				} else {
					DialogAlert('新增的多指标名称不能为空。');
					return false;
				}
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	});
}

pageConfigObj.editMultiTradeDict = function () {
	//仅修改名称，其它值直接在表格中修改
	var selectedRow = pageConfigObj.multiTradeDictGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要修改的多指标字典项。');
		return false;
	}
	var oldMultiTradeName = selectedRow.name;
	$("#txtDictItemName").val(oldMultiTradeName);
	$("#inputDictNoPanel").hide();
	$.ligerDialog.open({ height: 50, width: 310, title: '修改多指标名称', target: $("#inputDictInfoPanel"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				var newMultiTradeName = $("#txtDictItemName").val();
				if (newMultiTradeName)
					if (oldMultiTradeName != newMultiTradeName) {
						var gridData = pageConfigObj.multiTradeDictGridManager.getData();
						for(var i = 0; i < gridData.length; i++) {
							if (gridData[i].name == newMultiTradeName) {
								DialogAlert('多指标名称' + newMultiTradeName + '已经存在，不能重复。');
								return false;
							}
						}
						pageConfigObj.multiTradeDictGridManager.updateCell("name", newMultiTradeName, selectedRow);
						pageConfigObj.multiTradeDictDataModified = true;
					}
				else {
					DialogAlert('多指标名称不能为空。');
					return false;
				}
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	});
}

pageConfigObj.deleteMultiTradeDict = function () {
	var selectedRow = pageConfigObj.multiTradeDictGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要删除的多指标字典项。');
		return false;
	} else {
		DialogConfirm("确定要删除选择的多指标字典项 " + selectedRow.name + "？<br/>删除字典记录将会同步删除所有业务类别范围中引用的当前被删除的多指标记录。", function (yes) {
			if (yes) {
				var multiTradeId = selectedRow.id;
				pageConfigObj.multiTradeDictGridManager.deleteSelectedRow();
				pageConfigObj.multiTradeDictDataModified = true;
				//删除所有引用的数据
				for(var i = 0; i < pageConfigObj.businessTypeRelGridManager.data.Rows.length; i++) {
					var row = pageConfigObj.businessTypeRelGridManager.data.Rows[i];
					var transTypes = row.transTypes;
					if (transTypes) {
						for(var key in transTypes) {
							var transType = transTypes[key];
							var quotas = transType.quotas;
							if (quotas) {
								for(var j = 0; j < quotas.length; j++) {
									if (quotas[j].multi_trade_id == multiTradeId) {
										quotas.splice(j, 1);
										pageConfigObj.businessTypeRelDataModified = true;
										break;
									}
								}
							}
						}
					}
				}
			}
		});
	}
}

pageConfigObj.addBusinessTypeRel = function () {
	var goodsTypesDict = pageConfigObj.getDictDataList("goodsType", ["id", "name"]);
	var businessTypesDict = pageConfigObj.getDictDataList("businessType", ["id", "name"]);
	DialogOpen({ height: 400, width: 650, title: '新增业务类别', url: approot + '/sysman/businessTypeConfig.html'
			+ '?goodsTypes=' + encodeURIComponent(JSON.stringify(goodsTypesDict))
			+ '&businessTypes=' + encodeURIComponent(JSON.stringify(businessTypesDict))
			+ '&steps=' + encodeURIComponent(JSON.stringify(pageConfigObj.getDictDataList("steps", ["id", "name"])))
			+ '&transTypes=' + encodeURIComponent(JSON.stringify(pageConfigObj.getDictDataList("transTypes", ["id", "name"])))
			+ '&quotas=' + encodeURIComponent(JSON.stringify(pageConfigObj.getDictDataList("quotas", ["id", "name"]))),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				var businessTypePageObj = dialog.frame.pageConfigObj || dialog.frame.window.pageConfigObj;
				if (!businessTypePageObj.checkBusinessTypeConfig())
					return false;
				var businessTypeData= businessTypePageObj.getBusinessTypeConfig();
				if (!businessTypeData) {
					DialogAlert('新增的业务类别数据有误，不能保存。');
					return false;
				}
				var businessTypeRelName = businessTypeData.name;
				if (!businessTypeRelName) {
					for(var i = 0; i < goodsTypesDict.length; i ++)
						if (goodsTypesDict[i].id == businessTypeData.goods_type_id) {
							businessTypeRelName = goodsTypesDict[i].name;
							break;
						}
					for(var i = 0; i < businessTypesDict.length; i ++)
						if (businessTypesDict[i].id == businessTypeData.business_type_id) {
							businessTypeRelName = businessTypesDict[i].name;
							break;
						}
				}
				var gridData = pageConfigObj.businessTypeRelGridManager.getData();
				for(var i = 0; i < gridData.length; i++) {
					if (gridData[i].id == businessTypeData.id) {
						DialogAlert('业务类别ID不能已存在相同的ID值（目前ID值自动等于编号' + businessTypeData.id + '）。');
						return false;
					} else if (gridData[i].name == businessTypeRelName) {
						DialogAlert('业务类别名称' + businessTypeRelName + '已经存在，不能重复。');
						return false;
					}
				}
				businessTypeData.name = businessTypeRelName;
				pageConfigObj.businessTypeRelGridManager.addRow(businessTypeData);
				pageConfigObj.businessTypeRelDataModified = true;
				dialog.close();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.close();
			}
		}]
	});
}

pageConfigObj.editBusinessTypeRel = function () {
	var selectedRow = pageConfigObj.businessTypeRelGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要编辑的业务类别。');
		return false;
	}
	var randomVarPrefix = newGUID(true);
	setGlobalAttribute(randomVarPrefix + "_data", selectedRow);
	setGlobalAttribute(randomVarPrefix + "_goodsTypes", pageConfigObj.getDictDataList("goodsType", ["id", "name"]));
	setGlobalAttribute(randomVarPrefix + "_businessTypes", pageConfigObj.getDictDataList("businessType", ["id", "name"]));
	setGlobalAttribute(randomVarPrefix + "_steps", pageConfigObj.getDictDataList("steps", ["id", "name"]));
	setGlobalAttribute(randomVarPrefix + "_transTypes", pageConfigObj.getDictDataList("transTypes", ["id", "name"]));
	setGlobalAttribute(randomVarPrefix + "_quotas", pageConfigObj.getDictDataList("quotas", ["id", "name"]));
	DialogOpen({ height: 400, width: 650, title: '编辑业务类别', url: approot + '/sysman/businessTypeConfig.html?randomVarPrefix=' + randomVarPrefix,
			data: selectedRow, goodsTypes: pageConfigObj.getDictDataList("goodsType", ["id", "name"]),
			businessTypes: pageConfigObj.getDictDataList("businessType", ["id", "name"]),
			steps: pageConfigObj.getDictDataList("steps", ["id", "name"]),
			transTypes: pageConfigObj.getDictDataList("transTypes", ["id", "name"]),
			quotas: pageConfigObj.getDictDataList("quotas", ["id", "name"]),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				var businessTypePageObj = dialog.frame.pageConfigObj || dialog.frame.window.pageConfigObj;
				if (!businessTypePageObj.checkBusinessTypeConfig())
					return false;
				var businessTypeData = businessTypePageObj.getBusinessTypeConfig();
				if (!businessTypeData) {
					DialogAlert('修改的业务类别数据有误，不能保存。');
					return false;
				}
				var businessTypeRelName = businessTypeData.name;
				if (!businessTypeRelName) {
					for(var i = 0; i < goodsTypesDict.length; i ++)
						if (goodsTypesDict[i].id == businessTypeData.goods_type_id) {
							businessTypeRelName = goodsTypesDict[i].name;
							break;
						}
					for(var i = 0; i < businessTypesDict.length; i ++)
						if (businessTypesDict[i].id == businessTypeData.business_type_id) {
							businessTypeRelName = businessTypesDict[i].name;
							break;
						}
				}
				var gridData = pageConfigObj.businessTypeRelGridManager.getData();
				for(var i = 0; i < gridData.length; i++) {
					if (gridData[i].id != businessTypeData.id) {
						if (gridData[i].name == businessTypeRelName) {
							DialogAlert('业务类别名称' + businessTypeRelName + '已经存在，不能重复。');
							return false;
						}
					}
				}
				for(var key in businessTypeData) {
					selectedRow[key] = businessTypeData[key];
					pageConfigObj.businessTypeRelGridManager.updateCell(key, businessTypeData[key], selectedRow);
				}
				pageConfigObj.businessTypeRelDataModified = true;
				dialog.close();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.close();
			}
		}]
	});
}

pageConfigObj.deleteBusinessTypeRel = function () {
	var selectedRow = pageConfigObj.businessTypeRelGridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要删除的业务类别。');
		return false;
	} else {
		DialogConfirm("确定要删除选择的业务类别 " + selectedRow.name + "？", function (yes) {
			if (yes) {
				pageConfigObj.businessTypeRelGridManager.deleteSelectedRow();
				pageConfigObj.businessTypeRelDataModified = true;
			}
		});
	}
}

pageConfigObj.getDictDataList = function (dict, attributeName) {
	var returnAttrType = 0;
	if (attributeName)
		if (typeof(attributeName) == "string")
			returnAttrType = 1;
		else if (typeof(attributeName) == "object" && Object.prototype.toString.call(attributeName) == "[object Array]" && attributeName.length > 0)
			returnAttrType = 2;
	var dictGrid = null;
	var gridData = null;
	if (typeof(dict) == "string") {
		if (dict == "goodsType" || dict == "goodsTypes" || dict == "goodsTypeDict")
			dictGrid = pageConfigObj.goodsTypeDictGridManager;
		else if (dict == "businessType" || dict == "businessTypes" || dict == "businessTypeDict")
			dictGrid = pageConfigObj.businessTypeDictGridManager;
		else if (dict == "licenseStep" || dict == "licenseSteps" || dict == "licenseStepDict"
			|| dict == "step" || dict == "steps" || dict == "stepDict" || dict == "stepsDict")
			dictGrid = pageConfigObj.licenseStepDictGridManager;
		else if (dict == "transType" || dict == "transTypes" || dict == "transTypeDict"){
			pageConfigObj.initTransTypeDictGrid()
			dictGrid = pageConfigObj.transTypeDictGridManager;
		}
		else if (dict == "multiTrade" || dict == "multiTrades" || dict == "multiTradeDict"
			|| dict == "quota" || dict == "quotas" || dict == "quotaDict" || dict == "quotasDict")
			dictGrid = pageConfigObj.multiTradeDictGridManager;
	} else if (typeof(dict) == "object")
		dictGrid = dict;
	if (!dictGrid)
		return null;
	gridData = dictGrid.getData();
	var dataList = new Array();
	for(var i = 0; i < gridData.length; i++) {
		var row = gridData[i];
		if (returnAttrType == 0) {
			var data = {};
			for(var key in row) {
				if (key.substr(1, 2) != "__")
					data[key] = row[key];
			}
			dataList.push(data);
		} else if (returnAttrType == 1) {
			dataList.push(row[attributeName]);
		} else {
			var data = {};
			for(var key in row) {
				for(var j = 0; j < attributeName.length; j++)
					if (key == attributeName[j]) {
						data[key] = row[key];
						break;
					}
			}
			dataList.push(data);
		}
	}
	return dataList;
}

//---------------------------------
//标签TAB初始化
//---------------------------------
pageConfigObj.initTab = function() {
	$("#tabConfig").ligerTab({
		contextmenu: false,
		height: 460,
		onAfterSelectTabItem: pageConfigObj.afterSelectTabItem
     });
}

//---------------------------------
//交易规则字典标签TAB初始化
//---------------------------------
pageConfigObj.initTransRuleDictTab = function() {
	$("#tabTransRuleDict").ligerTab({
		contextmenu: false,
		onAfterSelectTabItem: pageConfigObj.afterSelectTabItemTrans
     });
}

//---------------------------------
//页面初始化
//---------------------------------
pageConfigObj.initHtml = function() {
	pageConfigObj.moduleId = Utils.getUrlParamValue(document.location.href, "moduleId");
	pageConfigObj.userId = getUserId(); 
	pageConfigObj.initTab();
	pageConfigObj.initTransRuleDictTab();
	$("#btnSave input").click(pageConfigObj.saveData);
	pageConfigObj.afterSelectTabItem('tabitem1');
}

$(document).ready(pageConfigObj.initHtml);