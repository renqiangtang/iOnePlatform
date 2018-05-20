//---------------------------------
//页对象
//---------------------------------
var pageObj = {};
// ---------------------------------
// 表格对象
// ---------------------------------
pageObj.gridManager = null;
// ---------------------------------
// 查询数据静态条件，由调用处传入
// ---------------------------------
pageObj.staticConditions = null;
pageObj.userInfo = getUserInfoObj();
pageObj.defaultColumns = {
	id : {
		title : 'ID',
		width : 80
	},
	ref_table_name : {
		title : '表名',
		width : 80
	},
	ref_id : {
		title : '键值',
		width : 180
	},
	field_name : {
		title : '字段',
		width : 80
	},
	old_value : {
		title : '旧值',
		width : 80
	},
	new_value : {
		title : '新值',
		width : 80
	}, // render: {0: '暂未审核', 1: '审核通过', 2: '审核不通过'}
	change_cause : {
		title : '变更原因',
		width : 160
	},
	change_date : {
		title : '日期',
		width : 80
	},
	extend1 : {
		title : '扩展值1',
		width : 80
	},
	extend2 : {
		title : '扩展值2',
		width : 80
	},
	extend3 : {
		title : '扩展值3',
		width : 80
	},
	change_batch : {
		title : '批次号',
		width : 80
	},
	change_type : {
		title : '变更类别',
		width : 80
	},
	remark : {
		title : '备注',
		width : 80
	}
};
pageObj.getColumn = function(fieldName, defTitle, defWidth) {
	if (fieldName && pageObj.gridColumns) {
		var defaultColumn = pageObj.defaultColumns[fieldName];
		var strTitle = null;
		var intWidth = 0;
		var strRender = null;
		var blnHint = false;
		if (typeof (pageObj.gridColumns) == "object" && fieldName in pageObj.gridColumns) {
			var column = pageObj.gridColumns[fieldName];
			strTitle = column ? column.title : null;
			intWidth = column ? column.width : 0;
			strRender = column ? column.render : null;
			blnHint = column ? column.hint : false;
		} else if (typeof (pageObj.gridColumns) == "string" && Utils.paramExists(pageObj.gridColumns, fieldName, true, ':', ';')) {
			var strColumn = Utils.getParamValue(pageObj.gridColumns, fieldName, true, ':', ';');
			strTitle = Utils.getSubStr(strColumn, ',', 0);
			var strWidth = Utils.getSubStr(strColumn, ',', 1);
			intWidth = parseInt(strWidth, 10);
			strRender = Utils.getSubStr(strColumn, ',', 2);
			blnHint = Utils.getSubStr(strColumn, ',', 3) == 1;
		} else
			return null;
		if (!strTitle)
			if (defTitle)
				strTitle = defTitle;
			else if (defaultColumn)
				strTitle = defaultColumn.title;
			else
				strTitle = fieldName;
		if (isNaN(intWidth) || intWidth <= 0)
			if (defWidth)
				intWidth = defWidth;
			else if (defaultColumn)
				intWidth = defaultColumn.width;
			else
				intWidth = 80;
		return {
			display : strTitle,
			name : fieldName,
			width : intWidth,
			render : strRender || blnHint ? pageObj.columnRender : null,
			hint : blnHint
		};
	} else
		return null;
}
pageObj.addColumn = function(columns, fieldName, defTitle, defWidth) {
	var column = pageObj.getColumn(fieldName, defTitle, defWidth);
	if (column)
		columns.push(column);
}
// ---------------------------------
// 初始化表格
// ---------------------------------
pageObj.initGrid = function() {
	var columns = null;
	if (pageObj.gridColumns) {
		columns = new Array();
		pageObj.addColumn(columns, 'ref_table_name');
		pageObj.addColumn(columns, 'ref_id');
		pageObj.addColumn(columns, 'field_name');
		pageObj.addColumn(columns, 'old_value');
		pageObj.addColumn(columns, 'new_value');
		pageObj.addColumn(columns, 'change_cause');
		pageObj.addColumn(columns, 'change_date');
		pageObj.addColumn(columns, 'extend1');
		pageObj.addColumn(columns, 'extend2');
		pageObj.addColumn(columns, 'extend3');
	} else
		columns = [ {
			display : '表名',
			name : 'ref_table_name',
			width : 80
		}, {
			display : '键值',
			name : 'ref_id',
			width : 180
		}, {
			display : '字段',
			name : 'field_name',
			width : 80
		}, {
			display : '旧值',
			name : 'old_value',
			width : 80
		}, {
			display : '新值',
			name : 'new_value',
			width : 80
		}, {
			display : '变更原因',
			name : 'change_cause',
			width : 160
		}, {
			display : '日期',
			name : 'change_date',
			width : 80
		}, {
			display : '扩展值1',
			name : 'extend1',
			width : 80
		}, {
			display : '扩展值2',
			name : 'extend2',
			width : 80
		}, {
			display : '扩展值3',
			name : 'extend3',
			width : 80
		} ];
	var url = approot + '/data?module=sysman&service=FieldChange&method=getFieldChangeListData&pageQuery=1&moduleId=' + pageObj.moduleId + '&u=' + pageObj.userInfo.userId;
	if (pageObj.staticConditions)
		for ( var key in pageObj.staticConditions)
			url = Utils.setParamValue(url, key, pageObj.staticConditions[key], false, '=', '&');
	pageObj.gridManager = $("#fieldChangeLogGrid").ligerGrid({
		url : url,
		columns : columns,
		pagesizeParmName : 'pageRowCount',
		pageParmName : 'pageIndex',
		sortnameParmName : 'sortField',
		sortorderParmName : 'sortDir',
		isScroll : true,
		frozen : false,
		pageSizeOptions : [ 10, 20, 30 ],
		pageSize : 20,
		showTitle : false,
		width : '99%',
		height : '99%'
	});
}
pageObj.getColumnRender = function(fieldName, value) {
	if (fieldName && pageObj.gridColumns) {
		if (typeof (pageObj.gridColumns) == "object" && fieldName in pageObj.gridColumns) {
			var column = pageObj.gridColumns[fieldName];
			if (column && column.render && value in column.render)
				return column.render[value];
			else
				return value;
		} else if (typeof (pageObj.gridColumns) == "string" && Utils.paramExists(pageObj.gridColumns, fieldName, true, ':', ';')) {
			var strColumn = Utils.getParamValue(pageObj.gridColumns, fieldName, true, ':', ';');
			var strRender = Utils.getSubStr(strColumn, ',', 2);
			var render = Utils.getParams(strRender, true, '#', '|');
			return render[value];
		} else
			return value;
	} else
		return value;
}
pageObj.columnRender = function(rowdata, rowindex, value, column) {
	var result = pageObj.getColumnRender(column.columnname, value);
	if (result) {
		if (column.hint)
			return "<span title='" + result + "'>" + result + "</span>";
		else
			return result;
	} else
		return "";
}
// ---------------------------------
// 查询数据
// ---------------------------------
pageObj.queryData = function() {
	var refId = $("#txtRefId").val();
	var fieldName = $("#txtFieldName").val();
	var changeDate1 = $("#txtChangeDate1").val();
	var changeDate2 = $("#txtChangeDate2").val();
	var queryParams = {
		refId : refId,
		fieldName : fieldName,
		changeDate1 : changeDate1,
		changeDate2 : changeDate2,
		u : pageObj.userInfo.userId
	};
	if (pageObj.staticConditions)
		for ( var key in pageObj.staticConditions)
			queryParams[key] = pageObj.staticConditions[key];
	var url = approot + '/data?module=tradeplow&service=OfferLog&method=getOfferLogListData&pageQuery=1&moduleId=' + pageObj.moduleId;
	pageObj.gridManager.refresh(url, queryParams);
}
pageObj.addStaticCondition = function(name, value) {
	if (!pageObj.staticConditions)
		pageObj.staticConditions = {};
	pageObj.staticConditions[name] = value;
}
// ---------------------------------
// 查看字段值变更日志
// ---------------------------------
pageObj.viewFieldChange = function(changeLogId) {
	DialogOpen({
		height : 750,
		width : 1000,
		url : approot + '/sysman/viewFieldChange.html?changeLogId=' + changeLogId,
		title : '查看变更日志',
		buttons : [ {
			text : '关闭',
			onclick : function(item, dialog) {
				dialog.close();
			}
		} ]
	});
}
// ---------------------------------
// 清空查询条件
// ---------------------------------
pageObj.resetConditionCtrl = function() {
	$("#txtRefId").val("");
	$("#txtFieldName").val("");
	$("#txtChangeDate1").val("");
	$("#txtChangeDate2").val("");
}
// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getPageValue("moduleId");
	var hideQueryCondition = Utils.getPageValue("hideQueryCondition");
	if (hideQueryCondition == "1" || hideQueryCondition.toLowerCase() == "true" || hideQueryCondition.toLowerCase() == "yes") {
		$("#queryCondition").hide();
	}
	pageObj.gridColumns = decodeURIComponent(Utils.getPageValue("columns"));
	if (pageObj.gridColumns.indexOf("{") >= 0 && pageObj.gridColumns.indexOf("}") >= 0 && pageObj.gridColumns.indexOf(":") >= 0)
		/*
		 * pageObj.gridColumns格式如： {new_value: {title: '审核状态', width: 0, render:
		 * {0: '暂未审核', 1: '审核通过', 2: '审核不通过'}}, change_cause: {title: '意见'},
		 * change_date: {title: '日期'} }
		 */
		pageObj.gridColumns = eval("(" + pageObj.gridColumns + ")");
	else {// 字符串格式时转换
		// pageObj.gridColumns格式如：new_value:审核状态,90,0#暂未审核|1#审核通过|2#审核不通过;change_cause:意见;change_date:日期
		var gridColumns = Utils.getParams(pageObj.gridColumns, true, ':', ';');
		pageObj.gridColumns = {};
		for ( var key in gridColumns) {
			var column = null;
			var strColumn = gridColumns[key];
			if (strColumn) {
				column = {};
				column.title = Utils.getSubStr(strColumn, ',', 0);
				var colWidth = parseInt(Utils.getSubStr(strColumn, ',', 1), 10);
				if (!isNaN(colWidth) && colWidth > 0)
					column.width = colWidth;
				var strRender = Utils.getSubStr(strColumn, ',', 2);
				if (strRender) {
					var render = Utils.getParams(strRender, true, '#', '|');
					if (!objIsEmpty(render))
						column.render = render;
				}
			}
			pageObj.gridColumns[key] = column;
		}
		if (objIsEmpty(pageObj.gridColumns))
			pageObj.gridColumns = null;
	}
	var strRefTableName = Utils.getPageValue("refTableName");
	if (strRefTableName)
		pageObj.addStaticCondition("staticRefTableName", strRefTableName);
	var strRefId = Utils.getPageValue("refId");
	if (strRefId)
		pageObj.addStaticCondition("staticRefId", strRefId);
	var strFieldName = Utils.getPageValue("fieldName");
	if (strFieldName)
		pageObj.addStaticCondition("staticFieldName", strFieldName);
	var strCreateType = Utils.getPageValue("createType");
	if (strCreateType)
		pageObj.addStaticCondition("staticCreateType", strCreateType);
	pageObj.initGrid();
	$("#txtChangeDate1").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd'
		})
	});
	$("#txtChangeDate2").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd'
		})
	});
	$("#btnQuery").click(pageObj.queryData);
	$("#btnReset").click(pageObj.resetConditionCtrl);
	Utils.autoIframeSize();
}
$(document).ready(pageObj.initHtml);