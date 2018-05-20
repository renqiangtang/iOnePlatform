//---------------------------------
//页对象
//---------------------------------
var pageObj = {};
// ---------------------------------
// 查询条件下拉框对象
// ---------------------------------
pageObj.businessTypeComboManager = null;
pageObj.transTypeComboManager = null;
pageObj.statusComboManager = null;
pageObj.beginListTimeDateManager = null;
pageObj.endListTimeDateManager = null;
pageObj.beginLimitTimeDateManager = null;
// ---------------------------------
// 表格对象
// ---------------------------------
pageObj.gridManager = null;
// ---------------------------------
// 转换竞买申请状态
// ---------------------------------
pageObj.statusRender = function(row, rowNamber) {
	var status = Number(row.status).toFixed(0);
	var targetStatus = Number(row.targetStatus).toFixed(0);
	var confirmed = Number(row.confirmed).toFixed(0);
	var endFocusTime = row.endFocusTime;
	var beginLimitTime = row.beginLimitTime;
	var now = new Date();
	now = now.toString();
	var blnInFocusTime = targetStatus == 4 && (!endFocusTime || now < endFocusTime) && (!beginLimitTime || now < beginLimitTime);
	var blnAllowConfirmLicense = targetStatus == 3 || blnInFocusTime;
	var licenseId = row.id;
	var statusLabel = "";
	if (status == 0) {
		if (blnAllowConfirmLicense) {
			var operate = "<a id='btnConfirm' href='javascript:pageObj.confirmLicense(\"" + licenseId + "\")'>审核</a>";
			statusLabel = "申请中&nbsp;&nbsp;" + operate;
			statusLabel += "&nbsp;&nbsp;<a  href='javascript:pageObj.attach(\"" + licenseId + "\",\"" + row.bidderName + "\")'>附件</a>&nbsp;&nbsp;";
		} else {
			statusLabel = "申请中";
		}
	} else if (status == 1) {
		statusLabel = "已开通";
	} else if (status == 2) {
		statusLabel = "未成交";
	} else if (status == 3 || status == 4) {
		statusLabel = "已成交";
	} else if (status == 5) {
		statusLabel = "撤销成交";
	}
	if (confirmed == 1)
		statusLabel += "";
	return statusLabel;
}
pageObj.attach = function(licenseId, userName) {
	var para = {};
	para.height = 600;
	para.width = 1050;
	para.title = '竞买人相关附件';
	para.url = approot + '/trademan/docConfig.html?mode=view&home=竞买人/' + licenseId + "&title=竞买申请资料库[" + userName + "]";
	DialogOpen(para);
}
pageObj.nameRender = function(row, rowNamber) {
	var bidderName = row.bidderName;
	var unionCount = row.unionCount + 1;
	if (unionCount > 1) {
		var tipBidderNames = "共" + unionCount + "个竞买人联合竞买：<br/>";// bidderName.replaceAll(",",
																	// "<br/>")
		for ( var i = 0; i <= Utils.getSubStrCount(bidderName, ",", false); i++)
			tipBidderNames += (i + 1) + "、" + Utils.getSubStr(bidderName, ",", i, false) + "<br/>"
		return "<label id='labBidderName' title='" + tipBidderNames + "'>" + bidderName + "</label>";
	} else
		return bidderName;
}
pageObj.gridAfterShowData = function(data) {
	$("label[id='labBidderName']").ligerTip();
}
// ---------------------------------
// 转换标的物类别
// ---------------------------------
pageObj.businessTypeRender = function(row, rowNamber) {
	var businessType = Number(row.businessType).toFixed(0);
	var businessTypeText = Utils.getParamValue(pageObj.allBusinessType, businessType, true, "=", ";");
	if (businessTypeText == "")
		businessTypeText = "其它";
	return businessTypeText;
}
// ---------------------------------
// 转换标的状态
// ---------------------------------
pageObj.targetStatusRender = function(row, rowNamber) {
	var targetStatus = Number(row.targetStatus).toFixed(0);
	if (targetStatus == 0)
		return "录件";
	else if (targetStatus == 1)
		return "审核中";
	else if (targetStatus == 2)
		return "已审核";
	else if (targetStatus == 3)
		return "公告中";
	else if (targetStatus == 4)
		return "交易中";
	else if (targetStatus == 5)
		return "已成交";
	else if (targetStatus == 6)
		return "未成交";
	else
		return "其它";
}
// ---------------------------------
// 初始化表格
// ---------------------------------
pageObj.initGrid = function() {
	pageObj.gridManager = $("#licenseGrid").ligerGrid({
		url : approot + '/data?module=trademan&service=LicenseManage&method=getConfirmLicenseListData&confirmStatus=1&moduleId=' + pageObj.moduleId + '&u=' + pageObj.userId+'&goodsType='+pageObj.goodsType+'&isOrgan='+pageObj.isOrgan,
		columns : [ {
			display : '竞买人',
			name : 'bidderName',
			width : 250,
			render : pageObj.nameRender
		}, {
			display : '标的',
			name : 'targetName',
			width : 160
		}, {
			display : '类型',
			name : 'businessType',
			width : 80,
			render : pageObj.businessTypeRender
		}, {
			display : '交易方式',
			name : 'transTypeLabel',
			width : 80
		}, {
			display : '位置',
			name : 'address',
			width : 100
		}, {
			display : '申请时间',
			name : 'applyDate',
			width : 100
		}, {
			display : '标的状态',
			name : 'targetStatus',
			width : 80,
			render : pageObj.targetStatusRender
		}, {
			display : '状态',
			name : 'status',
			width : 150,
			render : pageObj.statusRender
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
		height : 390,
		detail : false,
		onAfterShowData : pageObj.gridAfterShowData
	});
}
// ---------------------------------
// 查询数据
// ---------------------------------
pageObj.queryData = function() {
	var bidderName = $("#txtBidderName").val();
	var target = $("#txtTarget").val();
	var businessType = $("#txtBusinessTypeValue").val();
	var transType = $("#txtTransTypeValue").val();
	var isUnion = $("#cboIsUnion").val();
	var beginListTime = $("#txtBeginListTime").val();
	var endListTime = $("#txtEndListTime").val();
	var beginLimitTime = $("#txtBeginLimitTime").val();
	var status = $("#cboStatus").val();
	var confirmOrgan = $("#txtConfirmOrgan").val();
	var confirmStatus = $("#cboConfirmed").val();
	var queryParams = {
		bidderName : bidderName,
		target : target,
		businessType : businessType,
		transType : transType,
		isUnion : isUnion,
		beginListTime : beginListTime,
		endListTime : endListTime,
		beginLimitTime : beginLimitTime,
		status : status,
		confirmOrgan : confirmOrgan,
		confirmStatus : confirmStatus,
		u : pageObj.userId,
		goodsType : pageObj.goodsType,
		isOrgan : pageObj.isOrgan
	};
	var url = approot + '/data?module=trademan&service=LicenseManage&method=getConfirmLicenseListData&moduleId=' + pageObj.moduleId;
	pageObj.gridManager.refresh(url, queryParams);
}
// ---------------------------------
// 查看竞买申请
// ---------------------------------
pageObj.confirmLicense = function(licenseId) {
	DialogOpen({
		height : 500,
		width : 700,
		url : approot + '/trademan/confirmLicense.html?id=' + licenseId,
		title : '竞买申请资格审核'
	});
}
// ---------------------------------
// 查询业务范围数据
// ---------------------------------
pageObj.queryBusinessTypeData = function() {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Config";
	cmd.method = "getBusinessType";
	cmd.returnType = 5;
	cmd.async = false;
	cmd.success = function(data) {
		if (data.state == "1") {
			var businessTypeData = data.businessType;
			pageObj.allBusinessType = businessTypeData.substr(0, businessTypeData.indexOf("|"));
			pageObj.businessType = businessTypeData.substr(businessTypeData.indexOf("|") + 1, businessTypeData.length);
		}
	};
	cmd.execute();
}
// ---------------------------------
// 查询条件初始化
// ---------------------------------
pageObj.initQueryConditionCtrl = function() {
	var data = [ {
		text : '土地出让',
		id : '0'
	}, {
		text : '土地转让',
		id : '1'
	}, {
		text : '房产拍卖',
		id : '2'
	}, {
		text : '房产挂牌',
		id : '3'
	}, {
		text : '探矿权出让',
		id : '4'
	}, {
		text : '探矿权转让',
		id : '5'
	}, {
		text : '采矿权出让',
		id : '6'
	}, {
		text : '采矿权转让',
		id : '7'
	} ];
	if (pageObj.allBusinessType && pageObj.allBusinessType != "" && pageObj.businessType && pageObj.businessType != "") {
		data.splice(0, data.length);
		var aryBusinessType = pageObj.businessType.split(";");
		var businessType;
		for ( var i = 0; businessType = aryBusinessType[i]; i++) {
			if(pageObj.goodsType){
				if(businessType.indexOf(pageObj.goodsType)==0){
					var businessTypeItem = {};
					businessTypeItem.text = Utils.getParamValue(pageObj.allBusinessType, businessType, true, "=", ";");
					businessTypeItem.id = businessType;
					data.push(businessTypeItem);
				}
			}else{
				var businessTypeItem = {};
				businessTypeItem.text = Utils.getParamValue(pageObj.allBusinessType, businessType, true, "=", ";");
				businessTypeItem.id = businessType;
				data.push(businessTypeItem);
			}
		}
	}
	pageObj.businessTypeComboManager = $("#txtBusinessType").ligerComboBox({
		isMultiSelect : true,
		data : data,
		valueFieldID : 'txtBusinessTypeValue'
	});
	pageObj.transTypeComboManager = $("#txtTransType").ligerComboBox({
		isShowCheckBox : true,
		isMultiSelect : true,
		data : [ {
			text : '挂牌',
			id : '0'
		}, {
			text : '拍卖',
			id : '1'
		}, {
			text : '网上交易(挂牌)',
			id : '2'
		}, {
			text : '网上交易(拍卖)',
			id : '3'
		} ],
		valueFieldID : 'txtTransTypeValue'
	});
	$("#cboStatus").val("1");
	$("#cboConfirmed").val("1");
	pageObj.beginListTimeDateManager = $("#txtBeginListTime").ligerDateEditor();
	pageObj.endListTimeDateManager = $("#txtEndListTime").ligerDateEditor();
	pageObj.beginLimitTimeDateManager = $("#txtBeginLimitTime").ligerDateEditor();
}
// ---------------------------------
// 清空查询条件
// ---------------------------------
pageObj.resetConditionCtrl = function() {
	$("#txtBidderName").val("");
	$("#txtTarget").val("");
	$("#txtBusinessType").val("");
	$("#txtBusinessTypeValue").val("");
	$("#txtTransType").val("");
	$("#txtTransTypeValue").val("");
	$("#cboIsUnion").val("");
	$("#txtBeginListTime").val("");
	$("#txtEndListTime").val("");
	$("#txtBeginLimitTime").val("");
	$("#cboStatus").val("1");
	$("#cboConfirmed").val("1");
	$("#txtConfirmOrgan").val("");
}
// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getUrlParamValue(document.location.href, "moduleId");
	pageObj.goodsType = Utils.getUrlParamValue(document.location.href, "goodsType");
	pageObj.isOrgan = Utils.getUrlParamValue(document.location.href, "isOrgan"); //是否只查询本单位的数据 1查询 , 0或null查询所有
	pageObj.userId = getUserId();
	pageObj.initGrid();
	pageObj.queryBusinessTypeData();
	pageObj.initQueryConditionCtrl();
	$("#btnQuery").click(pageObj.queryData);
	$("#btnReset").click(pageObj.resetConditionCtrl);
}
$(document).ready(pageObj.initHtml);
