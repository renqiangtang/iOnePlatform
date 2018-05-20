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
pageObj.businessType=null;
// ---------------------------------
// 表格对象
// ---------------------------------
pageObj.gridManager = null;
// ---------------------------------
// 转换竞买申请状态
// ---------------------------------
pageObj.statusRender = function(row, rowNamber) {
	if(row.tlstatus==4){
		if(row.status == 7){
			var statusLabel = "<a id='btnConfirm' href='javascript:pageObj.confirmLicense(\"" + row.license_id + "\")'>审核</a>";
			statusLabel += "&nbsp;&nbsp;<a  href='javascript:pageObj.jdrUploadFile(\"" + row.license_id + "\",\"" + row.license_bidder_name + "\")'>附件</a>&nbsp;&nbsp;";
			return statusLabel;
		}else if(row.status == 5){
			if(row.after_check==1){
				return "审核通过"+ "&nbsp;&nbsp;<a  href='javascript:pageObj.jdrUploadFile(\"" + row.license_id + "\",\"" + row.license_bidder_name + "\")'>附件</a>&nbsp;&nbsp;";
			}else if(row.after_check==2){
				return "复核通过"+ "&nbsp;&nbsp;<a  href='javascript:pageObj.jdrUploadFile(\"" + row.license_id + "\",\"" + row.license_bidder_name + "\")'>附件</a>&nbsp;&nbsp;";
			}
		}else{
			var statusLabel = "";
			if(row.after_check==-1){
				statusLabel="审核不通过";
				statusLabel=statusLabel+" <a id='btnConfirm' href='javascript:pageObj.confirmLicenseFh(\"" + row.license_id + "\",\"" + row.after_check + "\")'>复核</a>";
				statusLabel += "&nbsp;&nbsp;<a  href='javascript:pageObj.jdrUploadFile(\"" + row.license_id + "\",\"" + row.license_bidder_name + "\")'>附件</a>&nbsp;&nbsp;";
			}else if(row.after_check==-2){
				statusLabel="复核不通过"+ "&nbsp;&nbsp;<a  href='javascript:pageObj.jdrUploadFile(\"" + row.license_id + "\",\"" + row.license_bidder_name + "\")'>附件</a>&nbsp;&nbsp;";
			}
		}
	}else{
		var statusLabel = "";
		if(row.succ_license_id==row.license_id){
			if(row.after_check==-1){
				statusLabel="审核不通过";
				statusLabel +=" <a id='btnConfirm' href='javascript:pageObj.confirmLicenseFh(\"" + row.license_id + "\",\"" + row.after_check + "\")'>复核</a>&nbsp;&nbsp;";
			}else if(row.after_check==-2){
				statusLabel="复核不通过&nbsp;&nbsp;";
			}
		}
		statusLabel += "<a  href='javascript:pageObj.jdrUploadFile(\"" + row.license_id + "\",\"" + row.license_bidder_name + "\")'>附件</a>&nbsp;&nbsp;";
	}
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

pageObj.renderPrice = function(row, rowNumber) {
	return comObj.cf({
		unit : row.unit,
		amount : row.trans_price
	});
}
// ---------------------------------
// 初始化表格
// ---------------------------------
pageObj.initGrid = function() {
	pageObj.gridManager = $("#licenseGrid").ligerGrid({
		url : approot + '/data?module=trademan&service=LicenseManage&method=getConfirmLicenseListDataAfter&confirmStatus=1&u=' + pageObj.userId+'&goodsType='+pageObj.goodsType,
		columns : [ {
			display : '竞买人',
			name : 'bidder_name',
			width : 100
		}, {
			display : '宗地编号',
			name : 'no',
			width : 200
		},{
			display : '供应方式',
			name : 'business_type_name',
			width : 80
		}, {
			display : '交易方式',
			name : 'trans_type_label',
			width : 150
		}, {
			display : '结束时间',
			name : 'end_trans_time',
			width : 150
		}, {
			display : '成交价',
			name : 'trans_price',
			width : 150
			//render : pageObj.renderPrice
		}, {
			display : '是否竞得人',
			name : 'isjd',
			width : 150,
			render : function(r){
				if(r.tlstatus==4){
					return "是";
				}else{
					return "否";
				}
			}
		}, {
			display : '操作',
			name : 'status',
			width : 180,
			render : pageObj.statusRender
		} ],
		pagesizeParmName : 'pageRowCount',// 每页记录数
		pageParmName : 'pageIndex',// 页码数
		sortnameParmName : 'sortField',// 排序列名
		sortorderParmName : 'sortDir',// 排序方向
		isScroll : true,// 是否滚动
		rownumbers : true,// 是否固定列
		pageSizeOptions : [ 10, 20, 30 ],
		showTitle : false,
		height : 390,
		detail : false,
		onAfterShowData : pageObj.gridAfterShowData
	});
	if(pageObj.goodsType=="101"){
		pageObj.gridManager.changeHeaderText("no","宗地编号");
	}else{
		pageObj.gridManager.changeHeaderText("no","矿区名称");
	}
}
// ---------------------------------
// 查询数据
// ---------------------------------
pageObj.queryData = function() {
	var bidderName = $("#txtBidderName").val();
//	if(pageObj.goodsType=="101") var targetNo = $("#txtTarget").val();
//	else var address = $("#txtTarget").val();
	var targetNo = $("#txtTarget").val();
	var businessType = $("#txtBusinessTypeValue").val();
	var transType = $("#txtTransTypeValue").val();
	var isUnion = $("#cboIsUnion").val();
	var beginTime = $("#txtBeginTime").val();
	var endTime = $("#txtEndTime").val();
	var confirmStatus = $("#cboConfirmed").val();
	var queryParams = {
		bidderName : bidderName,
		targetNo : targetNo,
		businessType : businessType,
		transType : transType,
		isUnion : isUnion,
		beginTime : beginTime,
		endTime : endTime,
		confirmStatus : confirmStatus,
		u : pageObj.userId,
		goodsType : pageObj.goodsType
	};
	var url = approot + '/data?module=trademan&service=LicenseManage&method=getConfirmLicenseListDataAfter';
	pageObj.gridManager.refresh(url, queryParams);
}
// ---------------------------------
// 资格审核
// ---------------------------------
pageObj.confirmLicense = function(licenseId) {
	var dialog = DialogOpen({
		height : 500,
		width : 700,
		url : approot + '/trademan/confirmLicenseAfter.html?id=' + licenseId,
		title : '竞买申请资格审核'
	});
	var manager = {};
	manager.dialog = dialog;
	manager.parentPageObj = pageObj;
	setGlobalAttribute("licenseAfterDialog", manager);
}

//---------------------------------
//查看竞买申请复核
//---------------------------------
pageObj.confirmLicenseFh = function(licenseId,afterCheck) {
	if(afterCheck!=-1){
		DialogAlert('竞买申请资格审批不通过的才能复核！');
		return;
	}
	var dialog = DialogOpen({
		height : 500,
		width : 700,
		url : approot + '/trademan/confirmLicenseAfterFh.html?id=' + licenseId+"&afterCheck="+afterCheck,
		title : '竞买申请资格复核'
	});
	var manager = {};
	manager.dialog = dialog;
	manager.parentPageObj = pageObj;
	setGlobalAttribute("licenseAfterDialog", manager);
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
	cmd.organId=getOrganId();
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
//	if (pageObj.allBusinessType != "" ) {
//		data.splice(0, data.length);
//		var aryBusinessType = pageObj.businessType.split(";");
//		var businessType;
//		for ( var i = 0; businessType = aryBusinessType[i]; i++) {
//			if(businessType.indexOf(pageObj.goodsType)==0){
//				var businessTypeItem = {};
//				businessTypeItem.text = Utils.getParamValue(pageObj.allBusinessType, businessType, true, "=", ";");
//				businessTypeItem.id = businessType;
//				data.push(businessTypeItem);
//			}
//		}
//		//如果为矿业权
//		if(data=="" && (pageObj.goodsType=="301,401" || pageObj.goodsType=="101" )){
//			var pogt=pageObj.goodsType;
//			for(var x=0;x<pogt.length;x++){
//				for ( var i = 0; businessType = aryBusinessType[i]; i++) {
//					if(businessType.indexOf(pogt[x])==0){
//						var businessTypeItem = {};
//						businessTypeItem.text = Utils.getParamValue(pageObj.allBusinessType, businessType, true, "=", ";");
//						businessTypeItem.id = businessType;
//						data.push(businessTypeItem);
//					}
//				}
//			}
//		}
//		
//	}
//	
	data.splice(0, data.length);
//	var allBusinessTypes=pageObj.allBusinessType.split(";");
//	if(pageObj.goodsType.indexOf(",")==-1){//不是多个goodsType
//		var zhgt=comObj.goods_type[pageObj.goodsType];//得到土地，采矿权或探矿权字样
//		for(var i=0;i<allBusinessTypes.length;i++){
//			if(allBusinessTypes[i].indexOf(zhgt)!=-1){
//				var businessTypeItem = {};
//				businessTypeItem.text = allBusinessTypes[i].split("=")[1];
//				businessTypeItem.id = allBusinessTypes[i].split("=")[0];
//				data.push(businessTypeItem);
//			}
//		}
//	}else{
//		var goodsTypes=pageObj.goodsType.split(",");
//		for(var j=0;j<goodsTypes.length;j++){
//			var zhgt=comObj.goods_type[goodsTypes[j]];//得到土地，采矿权或探矿权字样
//			for(var i=0;i<allBusinessTypes.length;i++){
//				if(allBusinessTypes[i].indexOf(zhgt)!=-1){
//					var businessTypeItem = {};
//					businessTypeItem.text = allBusinessTypes[i].split("=")[1];
//					businessTypeItem.id = allBusinessTypes[i].split("=")[0];
//					data.push(businessTypeItem);
//				}
//			}
//		}
//	}
//	
	var businessTypeItem = {};
	businessTypeItem.text = "出让"
	businessTypeItem.id = pageObj.goodsType+"001";
	var businessTypeItem1 = {};
	businessTypeItem1.text = "租赁"
	businessTypeItem1.id = pageObj.goodsType+"002";
	data.push(businessTypeItem);
	data.push(businessTypeItem1);
	
	pageObj.businessTypeComboManager = $("#txtBusinessType").ligerComboBox({
		isMultiSelect : true,
		data : data,
		valueFieldID : 'txtBusinessTypeValue'
	});
	pageObj.transTypeComboManager = $("#txtTransType").ligerComboBox({
		isShowCheckBox : true,
		isMultiSelect : true,
		data : [{
			text : '网上交易(无底价挂牌)',
			id : '107'
		}, {
			text : '网上交易(无底价拍卖)',
			id : '108'
		} ],
		valueFieldID : 'txtTransTypeValue'
	});
	pageObj.beginListTimeDateManager = $("#txtBeginTime").ligerDateEditor();
	pageObj.endListTimeDateManager = $("#txtEndTime").ligerDateEditor();
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
	$("#cboIsUnion").val("0");
	$("#txtBeginTime").val("");
	$("#txtEndTime").val("");
	$("#cboConfirmed").val("0");
}

//---------------------------------
//查看竞得人上传的附件
//---------------------------------
pageObj.jdrUploadFile = function(licenseId, targetName) {
	var grant = {
		add : 2,
		edit : 2,
		del : 2,
		addDtl : 2,
		editDtl : 2,
		delDtl : 2,
		uploadFile : 1,
		downloadFile : 2,
		delFile : 2
	};
	var owners = new Array();
	var owner = {};
	owner.id = licenseId;
	owner.name = "竞得人相关附件";
	owner.title = targetName;
	owner.tableName = "trans_license";
	owner.templetNo = "orderFormUploadFile";
	owner.grant = grant;
	owners.push(owner);
	var initWidth = 978;
	var initHeight = 200;
	var obj = {};
	obj.param = {};
	obj.param.u=pageObj.userId;
	obj.param.owners=encodeURIComponent(JSON.stringify(owners));;
	
	// 地址
	obj.url = approot + '/sysman/attachList.html';

	// 窗口参数
	obj.feature = "dialogWidth=900px;dialogHeight=600px";
	var rv = DialogModal(obj);
//	if (rv)
//		pageObj.refresh();
}

//---------------------------------
//查看竞买申请信息
//---------------------------------
pageObj.licenseInfo = function() {
	var licenseIdArr = pageObj.gridManager.getCheckedArr("license_id");
	if (licenseIdArr.length == 0){
		DialogAlert('请选择需要查看的申请！');
		return;
	}
	var obj = {};
	// 地址
	obj.url = "../"+comObj.urlPath[pageObj.goodsType]+"/bidder/applyLicenseInfo.html";
	// 参数
	obj.param = {
		u : pageObj.u,
		licenseId : licenseIdArr.join(',')
	};
	// 窗口参数
	obj.feature = "dialogWidth=1024px;dialogHeight=700px";
	var returnValue = DialogModal(obj);
}

// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initHtml = function() {
	pageObj.goodsType = Utils.getUrlParamValue(document.location.href, "goodsType");
	pageObj.userId = getUserId();
	if(pageObj.goodsType=="101"){
		$("#addressName").html("宗地编号");
	}else{
		$("#addressName").html("矿区名称");
		$("#licenseGrid").addClass("mine_trade_panel");
	}
	pageObj.initGrid();
	pageObj.queryBusinessTypeData();
	pageObj.initQueryConditionCtrl();
	$("#btnQuery").click(pageObj.queryData);
	$("#btnReset").click(pageObj.resetConditionCtrl);
	$("#btnInfo").click(pageObj.licenseInfo);
}
$(document).ready(pageObj.initHtml);
