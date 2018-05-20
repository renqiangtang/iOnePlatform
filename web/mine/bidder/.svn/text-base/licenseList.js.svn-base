// ---------------------------------
// 页页对象
// ---------------------------------
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
pageObj.goodsType =  null;
// ---------------------------------
// 表格对象
// ---------------------------------
pageObj.gridManager = null;

// ---------------------------------
// 转换名称
// ---------------------------------
pageObj.operateRender = function(row, rowNamber) {
	var status = Number(row.status).toFixed(0);
	var targetStatus = Number(row.targetStatus).toFixed(0);
	var payEarnest = Number(row.payEarnest).toFixed(0);
	var isUnion = Number(row.isUnion).toFixed(0);
	var unionCount = Number(row.unionCount).toFixed(0);
	var licenseId = row.id;
	var targetId = row.targetId;
	var targetName= row.targetName;
	var tnno= row.tnno;
	var gsName = row.tpgs_name;
	var businessType = row.businessType;
	var transTypeId = row.transType;
	if(transTypeId==0){
		transTypeId="107";
	}else{
		transTypeId="108";
	}
	var operate = "<a id='btnBidder' href='javascript:pageObj.openBidder(\"" + licenseId+ "\",\"" + targetId+ "\",\"" +tnno+ "\",\"" +targetName+ "\",\"" +gsName	+ "\",\"" +businessType	+ "\",\"" +transTypeId	+ "\")'>竞买申请书</a>&nbsp;&nbsp;";
	operate += "<a id='btnView' href='javascript:pageObj.openApplyView(\"" + licenseId
	+ "\")'>入账申请单</a>&nbsp;&nbsp;";
	if (status >= 1) {
		operate += "<a id='btnCert' href='javascript:pageTradeCommonObj.viewCert(\"" + licenseId
				+ "\")'>资格确认书</a>&nbsp;&nbsp;";
	}
	if (targetStatus ==5 && (status == 3 || status == 4)) {
//		operate += "<a id='btnTransCert' href='javascript:pageTradeCommonObj.viewTransCert(\"" + licenseId
//				+ "\")'>成交确认书</a>&nbsp;&nbsp;<a id='btnTransCert' href='"+approot + "/download?module=tradeland&service=Trade&method=downLoadOfferLog&licenseId="
//			+ licenseId+"&bidderName=&targetName="+row.targetName+"'>交易记录证书文件</a>&nbsp;&nbsp;";
		operate += "<a id='btnTransCert' style='display:none;' href='javascript:pageTradeCommonObj.viewTransCert(\"" + licenseId
		+ "\")'>成交确认书</a><a id='btnTransCert' href='"+approot + "/download?module=tradeland&service=Trade&method=downLoadOfferLog&licenseId="
	+ licenseId+"&bidderName=&targetName="+row.targetName+"'>交易记录证书文件</a>&nbsp;&nbsp;";
	}
	if (targetStatus ==7 && (status == 3 || status == 4)) {
		operate += "<a id='btnTransCert' href='javascript:pageTradeCommonObj.viewTransCertNotice(\"" + licenseId
				+ "\")'>网上竞得证明</a>&nbsp;&nbsp;";
	}
	if (targetStatus > 3) {
		operate += "<a id='btnViewOfferLog' href='javascript:pageTradeCommonObj.viewLicenseOfferLog(\"" + licenseId
				+ "\")'>出价记录</a>&nbsp;&nbsp;";
	}
	if (targetStatus <= 4 && status == 0 && payEarnest == 0 && isUnion == 0) {
		var unionLicense = unionCount > 0 ? 1 : 0;
		operate += "<a id='btnDeleteLicense' href='javascript:pageObj.deleteLicense(\"" + licenseId + "\", "
				+ unionLicense + ")'>删除</a>&nbsp;&nbsp;";
	}
	operate += "<a  href='javascript:pageObj.orderFormUploadFile(\"" + licenseId + "\",\""+row.targetName+"\",\""+row.status+"\",\""+row.targetStatus+"\",\""+row.payEarnest+"\")'>上传附件</a>&nbsp;&nbsp;";
	return operate;
}

pageObj.removeFromCar = function(dtlId) {
	var cmd = new Command();
	cmd.module = "bidder";
	cmd.service = "TransLicenseMine";
	cmd.method = 'removeFromCar';
	cmd.dtlId = dtlId;
	cmd.success = function(data) {
		if (data.state == 1) {
			DialogAlert('删除当前竞买申请操作成功完成');
		}
	}
	cmd.execute();

}

//---------------------------------
//打开入帐申请单
//---------------------------------
pageObj.openApplyView = function(licenseId) {
	var obj = {};
	obj.param = {
		mode : 'view1',
		licenseId : licenseId
	};
	obj.feature = "dialogWidth=1000px;dialogHeight=600px";
	comObj.getTransOrganByLicenseId(licenseId);
	obj.url = comObj.getConfigDoc('入账申请单');
	DialogModal(obj);
}

//---------------------------------
//打开竞买申请书
//---------------------------------
pageObj.openBidder = function(licenseId,targetId,tnoticeName,targetName,gsName,businessType,transTypeId){
	var obj = {};
	// 地址
	obj.url = "applyMain.html";
	// 参数
	obj.param = {
		u : pageObj.u,
		targetId : targetId,
		noticeName : tnoticeName,
		targetName : targetName,
		gsName : gsName,
		isli : 1,
		userName : getUserInfoObj().displayName,
		businessType : businessType,
		transTypeId : transTypeId
	};
	// 窗口参数
	obj.feature = "dialogWidth=1000px;dialogHeight=600px";
	DialogModal(obj);
}

pageObj.licenseNoRender = function(row, rowNamber) {
	var licenseNo = row.licenseNo;
	var licenseId = row.id;
	var targetId = row.targetId;
	var operate = "<a id='btnView' href='javascript:pageObj.openApplyView(\"" + licenseId + "\")'>" + licenseNo
			+ "</a>&nbsp;&nbsp;";
	return operate;
}

pageObj.targetNameRender = function(row, rowNamber) {
	var targetName = row.targetName;
	var targetId = row.targetId;
	var operate = "<a id='btnView' href='javascript:pageTradeCommonObj.viewTarget(\"" + targetId + "\")'>" + targetName
			+ "</a>&nbsp;&nbsp;";
	return operate;
}

// ---------------------------------
// 转换竞买申请状态
// ---------------------------------
pageObj.statusRender = function(row, rowNamber) {
	var status = Number(row.status).toFixed(0);
	var targetStatus = Number(row.targetStatus).toFixed(0);
	var hasTransCondition = Number(row.hasTransCondition).toFixed(0);
	var confirmed = Number(row.confirmed).toFixed(0);
	var confirmedDate = row.confirmedDate;
	var confirmedOpinion = row.confirmedOpinion;
	var payEarnest = Number(row.payEarnest).toFixed(0);
	var licenseId = row.id;
	var isUnion = Number(row.isUnion).toFixed(0);
	var unionStatus = Number(row.unionStatus).toFixed(0);
	var unionCount = Number(row.unionCount).toFixed(0);
	var unionConfirmCount = Number(row.unionConfirmCount).toFixed(0);
	var unionRejectCount = Number(row.unionRejectCount).toFixed(0);
	var endFocusTime = row.endFocusTime;
	var beginLimitTime = row.beginLimitTime;
	var now = new Date();
	now = now.toString();
	var blnInFocusTime = targetStatus == 4 && (!endFocusTime || now < endFocusTime)
			&& (!beginLimitTime || now < beginLimitTime);
	var blnAllowConfirmUnion = targetStatus == 3 || blnInFocusTime;
	var statusLabel = "";
	var statusTitle = "";
	var unionLabel = "";
	if (status == 0) {
		statusLabel = "申请中";
		if (payEarnest == 1) {
			statusTitle += "已交足保证金";
		} else {
			statusTitle += "未交足保证金";
		}
		if (hasTransCondition == 1) {
			if (confirmed == 1) {
				statusTitle += "、已审批通过";
			} else if (confirmed == 2) {
				statusTitle += "、审批不通过";
				if (confirmedOpinion != null && confirmedOpinion.toLowerCase() != "null") {
					statusTitle += "(" + confirmedOpinion + ")";
				}
			} else {
				statusTitle += "、暂未审批";
			}
		}
		if (unionCount > 0) {
			if (isUnion == 1) {
				unionLabel = "被联合";
				if (unionConfirmCount == unionCount) {
					unionLabel += "(全部确认)&nbsp;<a href='javascript:void(0)'; onclick=javascript:pageObj.getLicenseUnionList(\""+licenseId+"\",\""+row.targetName+"\");return false;>查看竞买情况</a>";
//					if (blnAllowConfirmUnion)
//						unionLabel += "&nbsp;<a id='btnUnionReject' href='javascript:pageObj.unionConfirm(\""
//								+ licenseId + "\", 2)'>拒绝</a>";
				} else if (unionStatus == 0) {
					if (blnAllowConfirmUnion)
						unionLabel += "&nbsp;<a id='btnUnionConfirm' href='javascript:pageObj.unionConfirm(\""
								+ licenseId + "\", 1)'>确认</a>"
								+ "&nbsp;<a id='btnUnionReject' href='javascript:pageObj.unionConfirm(\"" + licenseId
								+ "\", 2)'>拒绝</a>&nbsp;<a href='javascript:void(0)'; onclick=javascript:pageObj.getLicenseUnionList(\""+licenseId+"\",\""+row.targetName+"\");return false;>查看竞买情况</a>";
				} else if (unionStatus == 1) {
					unionLabel += "(已确认)";
					if (blnAllowConfirmUnion)
						unionLabel += "&nbsp;<a href='javascript:void(0)'; onclick=javascript:pageObj.getLicenseUnionList(\""+licenseId+"\",\""+row.targetName+"\");return false;>查看竞买情况</a>";
				} else {
					unionLabel += "(已拒绝)";
					if (blnAllowConfirmUnion)
						unionLabel += "&nbsp;<a href='javascript:void(0)'; onclick=javascript:pageObj.getLicenseUnionList(\""+licenseId+"\",\""+row.targetName+"\");return false;>查看竞买情况</a>";
				}
			}else if (unionCount > 0) {
				unionLabel = "联合";
				if (unionConfirmCount == unionCount)
					unionLabel += "(全部确认)";
				else if (unionRejectCount > 0)
					unionLabel += "(被拒绝)";
				else if (unionConfirmCount > 0)
					unionLabel += "(部分确认)";
				else
					unionLabel += "(无确认)";
			}
		}
	} else {
		if (status == 1) {
			statusLabel = "已开通";
		} else if (status == 2) {
			statusLabel = "未成交";
		} else if (status == 3 || status == 4) {
			statusLabel = "已成交";
		} else if (status == 5) {
			statusLabel = "撤销成交";
		}
		if (unionCount > 0)
			if (isUnion == 1)
				unionLabel = "被联合";
			else
				unionLabel = "联合";
	}
	if (unionLabel != "") {
		statusLabel += "," + unionLabel;
	}
	if (statusTitle == "") {
		return statusLabel;
	} else {
		return "<label id='labStatus' title='" + statusTitle + "'>" + statusLabel + "</label>";
	}
}

// ---------------------------------
// 转换标的物类别
// ---------------------------------
pageObj.businessTypeRender = function(row, rowNamber) {
	var businessType = Number(row.businessType).toFixed(0);
	var businessTypeText = Utils.getParamValue(pageTradeCommonObj.allBusinessType, businessType, true, "=", ";");
	if (businessTypeText == "")
		businessTypeText = "其它";
	return businessTypeText;
}


//---------------------------------
//获取竞买份额tyw
//---------------------------------
pageObj.getLicenseUnionList = function(licenseId,name){
	var cmd = new Command();
	cmd.module = "bidder";
	cmd.service = "Apply";
	cmd.method = 'getLicenseUnionList';
	cmd.licenseId = licenseId;
	cmd.success = function(data) {
		var html="'"+name+"'竞买情况如下：\n";
		for(var i=0;i<data.unit.length;i++){
			if(i!=data.unit.length-1){
				html += data.unit[i].name+"(占有份额"+data.unit[i].percent+"%);";
			}else{
				html += data.unit[i].name+"(占有份额"+data.unit[i].percent+"%)。";
			}
		}
		alert(html);
	}
	cmd.execute();
}
// ---------------------------------
// 转换竞买人数
// ---------------------------------
pageObj.licenseCountRender = function(row, rowNamber) {
	var licenseCount = Number(row.licenseCount).toFixed(0);
	var licenseCountLabel = "";
	if (licenseCount >= 0) {
		if (licenseCount == 0) {
			licenseCountLabel = "无人申请";
		} else if (licenseCount == 1) {
			licenseCountLabel = "一人申请";
		} else {
			licenseCountLabel = "多人申请";
		}
	} else {// 后台数据集不允许查看竞买人数
		licenseCountLabel = "-";
	}
	return licenseCountLabel;
}

// ---------------------------------
// 转换标的状态
// ---------------------------------
pageObj.targetStatusRender = function(row, rowNamber) {
	var targetStatus = Number(row.targetStatus).toFixed(0);
//	if (targetStatus == 0)
//		return "录件";
//	else if (targetStatus == 1)
//		return "审核中";
//	else if (targetStatus == 2)
//		return "已审核";
//	else if (targetStatus == 3)
//		return "公告中";
//	else if (targetStatus == 4)
//		return "交易中";
//	else if (targetStatus == 5)
//		return "已成交";
//	else if (targetStatus == 6)
//		return "未成交";
//	else if (targetStatus == 7)
//		return "成交资格审核";
//	else
//		return "其它";
	return comObj.targetStatusObj[targetStatus];
}

pageObj.maxPriceRender = function(row, rowNamber) {
	var maxPrice = row.maxPrice;
	var unit = row.unit;
	if (maxPrice) {
		if (unit) {
			var fltValue = parseFloat(maxPrice);
			if (unit == "万元")
				fltValue = parseFloat((fltValue / 10000).toFixed(6));
			return fltValue.addComma() + " " + unit;
		} else
			return parseFloat(maxPrice).addComma();
	} else
		return "";
}

pageObj.gridAfterShowData = function(data) {
	$("label[id='labStatus']").ligerTip();
}

// ---------------------------------
// 初始化表格
// ---------------------------------
pageObj.initGrid = function() {
	pageObj.gridManager = $("#licenseGrid").ligerGrid({
		url : approot + '/data?module=trademan&service=LicenseManage&method=getBidderLicenseListData&moduleId='
				+ pageObj.moduleId + '&u=' + pageObj.userId,
		columns : [{
					display : '竞买号',
					name : 'licenseNo',
					width : 80,
					isSort : false,
					render : pageObj.licenseNoRender
				}, {
					display : '宗的位置',
					name : 'targetName',
					width : 150,
					align : 'left',
					render : pageObj.targetNameRender
				}, {
					display : '类型',
					name : 'businessType',
					width : 60,
					render : pageObj.businessTypeRender
				}, {
					display : '交易方式',
					name : 'transTypeLabel',
					width : 100
				},
				// { display: '应交保证金', name: 'earnestMoney', width: 100},
				{
					display : '申请时间',
					name : 'applyDate',
					width : 90
				}, {
					display : '竞买申请状态',
					name : 'status',
					width : 180,
					render : pageObj.statusRender
				}, {
					display : '标的状态',
					name : 'targetStatus',
					width : 80,
					render : pageObj.targetStatusRender
				}, {
					display : '操作',
					name : 'operate',
					width : 310,
					align : 'left',
					isSort : false,
					render : pageObj.operateRender
				}, {
					display : '竞买人数',
					name : 'licenseCount',
					width : 60,
					isSort : false,
					render : pageObj.licenseCountRender
				},// 开始交易前1小时出现：无人申请，一人申请，多人申请
				{
					display : '最高报价',
					name : 'maxPrice',
					width : 100,
					render : pageObj.maxPriceRender
				}],
		pagesizeParmName : 'pageRowCount',// 每页记录数
		pageParmName : 'pageIndex',// 页码数
		sortnameParmName : 'sortField',// 排序列名
		sortorderParmName : 'sortDir',// 排序方向
		isScroll : true,// 是否滚动
		rownumbers : true,
		pageSizeOptions : [10, 20, 30],
		showTitle : false,
		height : 388,
		detail : false,
		onAfterShowData : pageObj.gridAfterShowData
	});
}

// ---------------------------------
// 查询数据
// ---------------------------------
pageObj.queryData = function() {
	var target = $("#txtTarget").val();
	var businessType = $("#txtBusinessTypeValue").val();
	var transType = $("#txtTransTypeValue").val();
	var isUnion = $("#cboIsUnion").val();
	var beginListTime = $("#txtBeginListTime").val();
	var endListTime = $("#txtEndListTime").val();
	var beginLimitTime = $("#txtBeginLimitTime").val();
	var status = $("#txtStatusValue").val();
	var queryParams = {
		target : target,
		businessType : businessType,
		transType : transType,
		isUnion : isUnion,
		beginListTime : beginListTime,
		endListTime : endListTime,
		beginLimitTime : beginLimitTime,
		status : status,
		u : pageObj.userId
	};
	var url = approot + '/data?module=trademan&service=LicenseManage&method=getBidderLicenseListData&moduleId='
			+ pageObj.moduleId;
	pageObj.gridManager.refresh(url,queryParams);
}

// ---------------------------------
// 确认联合竞买申请
// ---------------------------------
pageObj.unionConfirm = function(licenseId, confirmed) {
	if (licenseId == "" || licenseId == null || licenseId == undefined) {
		DialogAlert('无联合竞买申请信息');
		return false;
	}	
		var message = "确认当前联合竞买申请？";
		if (confirmed == "2") {
			message = "拒绝当前联合竞买申请？";
			DialogConfirm(message, function(yes) {
				if (yes) {
					var cmd = new Command();
					cmd.module = "trademan";
					cmd.service = "LicenseManage";
					cmd.method = "confirmUnionLicense";
					cmd.id = licenseId;
					cmd.confirmed = confirmed ? confirmed : 2;
					cmd.success = function(data) {
						var state = data.state;
						if (state == '1') {
							if (confirmed == "2") {
								DialogAlert('拒绝当前联合竞买申请操作成功完成');
							} else {
								DialogAlert('确认当前联合竞买申请操作成功完成');
							}
							pageObj.queryData();
						} else {
							if (confirmed == "2") {
								DialogError('拒绝当前联合竞买申请操作失败,错误原因：' + data.message);
							} else {
								DialogError('确认当前联合竞买申请操作失败,错误原因：' + data.message);
							}
							return false;
						}
					};
					cmd.execute();
				}
			});
		}else if (confirmed == 1){
			var cmd1 = new Command();
			cmd1.module = "sysman";
			cmd1.service = "User";
			cmd1.method = "checkBlackUser";
			cmd1.u = getUserId();
			cmd1.goodsType = pageObj.goodsType;
			cmd1.success = function(data1) {
				if(data1.state!=1){
					DialogAlert(data1.message);
					return;
				}
				DialogConfirm(message, function(yes) {
					if (yes) {
						var cmd = new Command();
						cmd.module = "trademan";
						cmd.service = "LicenseManage";
						cmd.method = "confirmUnionLicense";
						cmd.id = licenseId;
						cmd.confirmed = confirmed ? confirmed : 2;
						cmd.success = function(data) {
							var state = data.state;
							if (state == '1') {
								if (confirmed == "2") {
									DialogAlert('拒绝当前联合竞买申请操作成功完成');
								} else {
									DialogAlert('确认当前联合竞买申请操作成功完成');
								}
								pageObj.queryData();
							} else {
								if (confirmed == "2") {
									DialogError('拒绝当前联合竞买申请操作失败,错误原因：' + data.message);
								} else {
									DialogError('确认当前联合竞买申请操作失败,错误原因：' + data.message);
								}
								return false;
							}
						};
						cmd.execute();
					}
				});
		}
			cmd1.execute();
	}
}

// ---------------------------------
// 删除竞买申请，必须不能有任何有效的转账记录
// ---------------------------------
pageObj.deleteLicense = function(licenseId, unionLicense) {
	var message = "删除当前标的您的竞买申请记录？<br/><span style='color:red; font-weight:700'>如果您已经转入保证金，无论交易系统有无到账都不应该删除竞买申请（已到账系统将不允许删除）。"
			+ "</span><br/>";
	if (unionLicense && unionLicense == 1) {
		message += "<span style='color:blue; font-weight:700'>请提前通知其它被联合的竞买人。</span><br/>";
		message += "<hr/>一般需要删除的情况：<br/>&nbsp;&nbsp;&nbsp;&nbsp;如您目前是联合竞买申请并且未交保证金，您希望重新独立申请而放弃当前的联合竞买申请，或者被联合竞买人不拒绝参与等。"
				+ "<br/>&nbsp;&nbsp;或者独立竞买申请并且未交保证金，您希望重新联合其它竞买人进行联合申请而放弃当前的独立竞买申请。";
	} else {
		message += "<hr/>一般需要删除的情况：<br/>&nbsp;&nbsp;&nbsp;&nbsp;如您目前是独立竞买申请并且未交保证金，您希望重新联合其它竞买人进行联合申请而放弃当前的独立竞买申请。"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;或者联合竞买申请并且未交保证金，您希望重新独立申请而放弃当前的联合竞买申请，或者被联合竞买人不拒绝参与等。";
	}
	DialogConfirm(message, function(yes) {
				if (yes) {
					var cmd = new Command();
					cmd.module = "trademan";
					cmd.service = "LicenseManage";
					cmd.method = "deleteLicenseData";
					cmd.id = licenseId;
					cmd.success = function(data) {
						var state = data.state;
						if (state == '1') {
							pageObj.removeFromCar(data.cart_dtl_id);
							pageObj.queryData();
							parent.pageObj.refreshCar();
						} else {
							DialogError('删除当前竞买申请操作失败,错误原因：' + data.message);
							return false;
						}
					};
					cmd.execute();
				}
			});
}

// ---------------------------------
// 查询条件初始化
// ---------------------------------
pageObj.initQueryConditionCtrl = function() {
	var data = new Array();
	data.splice(0, data.length);
	if (pageTradeCommonObj.allBusinessType && pageTradeCommonObj.allBusinessType != ""
			&& pageTradeCommonObj.businessType && pageTradeCommonObj.businessType != "") {
		var aryBusinessType = pageTradeCommonObj.businessType.split(";");
//		alert(pageTradeCommonObj.allBusinessType);
//		var businessType;
//		for (var i = 0; businessType = aryBusinessType[i]; i++) {
//			var businessTypeItem = {};
//			businessTypeItem.text = Utils.getParamValue(pageTradeCommonObj.allBusinessType, businessType, true, "=",
//					";");
//			businessTypeItem.id = businessType;
//			data.push(businessTypeItem);
//		}
		var businessTypeItem = {};
		businessTypeItem.text = "出让"
		businessTypeItem.id = "101001";
		var businessTypeItem1 = {};
		businessTypeItem1.text = "转让"
		businessTypeItem1.id = "101002";
		data.push(businessTypeItem);
		data.push(businessTypeItem1);
	}
	pageObj.businessTypeComboManager = $("#txtBusinessType").ligerComboBox({
				isShowCheckBox : true,
				isMultiSelect : true,
				data : data,
				valueFieldID : 'txtBusinessTypeValue'
			});
	data.splice(0, data.length);
	if (pageTradeCommonObj.allTransType) {
//		var transTypeList = new Array();
//		for (var key in pageTradeCommonObj.allTransType) {
//			var transType = pageTradeCommonObj.allTransType[key];
//			if (transType && $.inArray(transType.name, transTypeList) == -1) {
//				transTypeList.push(transType.name);
//				data.push({
//							text : transType.name,
//							id : transType.trans_type_name
//						});
//			}
//		}
		var businessTypeItem = {};
		businessTypeItem.text = "网上交易（挂牌）"
		businessTypeItem.id = "网上交易（挂牌）";
		var businessTypeItem1 = {};
		businessTypeItem1.text = "网上交易（拍卖）"
		businessTypeItem1.id = "网上交易（拍卖）";
		data.push(businessTypeItem);
		data.push(businessTypeItem1);
	}
	pageObj.transTypeComboManager = $("#txtTransType").ligerComboBox({
				isShowCheckBox : true,
				isMultiSelect : true,
				data : data,
				valueFieldID : 'txtTransTypeValue'
			});
	pageObj.statusComboManager = $("#txtStatus").ligerComboBox({
				isShowCheckBox : true,
				isMultiSelect : true,
				data : [{
							text : '申请中',
							id : '0'
						}, {
							text : '已通过',
							id : '1'
						}, {
							text : '未成交',
							id : '2'
						}, {
							text : '已成交',
							id : '3'
						}
//						, {
//							text : '撤销成交',
//							id : '4'
//						}, {
//							text : '未交保证金',
//							id : '5'
//						}, {
//							text : '未交足保证金',
//							id : '6'
//						}, {
//							text : '已交足保证金',
//							id : '7'
//						}, {
//							text : '不需要审批',
//							id : '8'
//						}, {
//							text : '暂未审批',
//							id : '9'
//						}, {
//							text : '未审批通过',
//							id : '10'
//						}, {
//							text : '审批通过',
//							id : '11'
//						}, {
//							text : '联合竞买无确认',
//							id : '12'
//						}, {
//							text : '联合竞买未全部确认',
//							id : '13'
//						}, {
//							text : '联合竞买全部确认',
//							id : '14'
//						}, {
//							text : '联合竞买拒绝',
//							id : '15'
//						}

				],
				valueFieldID : 'txtStatusValue'
			});
	$("#txtBeginListTime").ligerDateEditor();
	$("#txtEndListTime").ligerDateEditor();
	$("#txtBeginLimitTime").ligerDateEditor();
}

// ---------------------------------
// 清空查询条件
// ---------------------------------
pageObj.resetConditionCtrl = function() {
	$("#txtTarget").val("");
	$("#txtBusinessType").val("");
	$("#txtBusinessTypeValue").val("");
	$("#txtTransType").val("");
	$("#txtTransTypeValue").val("");
	$("#cboIsUnion").val("0");
	$("#txtBeginListTime").val("");
	$("#txtEndListTime").val("");
	$("#txtBeginLimitTime").val("");
	$("#txtStatus").val("");
	$("#txtStatusValue").val("");
}

// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getUrlParamValue(document.location.href, "moduleId");
	pageObj.userId = getUserId();
	pageObj.initGrid();
	pageTradeCommonObj.getBusinessType();
	pageTradeCommonObj.getTransTypes();
	pageObj.initQueryConditionCtrl();
	$("#btnQuery").click(pageObj.queryData);
	$("#btnReset").click(pageObj.resetConditionCtrl);
	pageObj.goodsType= Utils.getUrlParamValue(document.location.href, "goodsType");
	Utils.autoIframeSize();

}

//---------------------------------
//附件
//---------------------------------
pageObj.orderFormUploadFile = function(licenseId, targetName,status,targetStatus,isupload) {
//	var isupload=2;
//	if(status==2 || status==3 || targetStatus==7){
//		isupload=1;
//	}
	var cmd = new Command();
	cmd.module = "trademan";
	cmd.service = "LicenseManage";
	cmd.method = "checkTransTargetEarnestMoneyPay";
	cmd.licenseId = licenseId;
	cmd.success = function(data) {
		var state = data.state;
		if (state == '1') {
			isupload=data.emp;
		}
		if(isupload==0){
			isupload=2;
		}else if(isupload==1){
			isupload=1;
		}
		var grant = {
			add : 2,
			edit : 2,
			del : 2,
			addDtl : 2,
			editDtl : 2,
			delDtl : 2,
			uploadFile : isupload,
			downloadFile : 2,
			delFile : isupload
		};
		var owners = new Array();
		var owner = {};
		owner.id = licenseId;
		owner.name = "竞买申请";
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
	}
	cmd.execute();
}

$(document).ready(pageObj.initHtml);