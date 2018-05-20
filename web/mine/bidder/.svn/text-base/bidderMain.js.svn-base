// ---------------------------------
// 页页对象
// ---------------------------------
var pageMainObj = {};
pageMainObj.goodsType =  null;
// ---------------------------------
// 检查竞买人最近有效的竞买申请信息需要提示
// ---------------------------------
pageMainObj.queryLicenseData = function() {
	var cmd = new Command();
	cmd.module = "trademan";
	cmd.service = "LicenseManageMine";
	cmd.method = "getBidderLicenseListData";
	cmd.targetStatus = "3;4";
	cmd.success = function(data) {
		var rowCount = data.Total + '';
		$("#licenses").html("");
		if (rowCount == "0") {
			$("#licensePromptPanel").hide();
		} else {
			var rowBodyTemplet = "<tr>"
					+ "<td width='100' height='23' class='tc s_rb'>{0}</td>"
					+ "<td width='200' class='tc s_rb'>"
					+ "  <div style='width:200px;text-overflow:ellipsis;overflow:hidden;white-space: nowrap;'>"
					+ "    <a id='btnView' href='javascript:pageTradeCommonObj.viewTarget(\"{1}\")'>{2}</a>"
					+ "  </div>"
					+ "</td>"
					+ "<td width='129' class='tc s_rb'>{3}</td>"
					+ "<td width='302' class='tc s_b'>"
					+ "  <div style='width:302px;text-overflow:ellipsis;overflow:hidden;white-space: nowrap;'>{4}</div>"
					+ "</td>" + "</tr>\n";
			var licensesBody = "";
			for (var i = 0; i < rowCount; i++) {
				var row = data.Rows[i];
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
				var blnInFocusTime = targetStatus == 4 && (!endFocusTime || now < endFocusTime) && (!beginLimitTime || now < beginLimitTime);
				var statusLabel = "";
				var statusDetail = "";
				var unionLabel = "";
				if (status == 0) {
					statusLabel = "申请中";
					if (payEarnest == 1) {
						statusDetail += "已交足保证金";
					} else {
						statusDetail += "未交足保证金";
					}
					if (hasTransCondition == 1) {
						if (confirmed == 1) {
							statusDetail += "、已审批通过";
						} else if (confirmed == 2) {
							statusDetail += "、审批不通过";
							if (confirmedOpinion != null && confirmedOpinion.toLowerCase() != "null") {
								statusDetail += "(" + confirmedOpinion + ")";
							}
						} else {
							statusDetail += "、暂未审批";
						}
					}
					if (unionCount > 0) {
						if (isUnion == 1) {
							unionLabel = "被联合";
							if (unionConfirmCount == unionCount) {
								unionLabel += "(全部确认)&nbsp;<a href='javascript:void(0)'; onclick=javascript:pageMainObj.getLicenseUnionList(\""+licenseId+"\",\""+row.targetName+"\");return false;>查看竞买情况</a>";
//								if (targetStatus == 3 || blnInFocusTime)
//									unionLabel += "&nbsp;<a id='btnUnionReject' href='javascript:pageMainObj.unionConfirm(\""
//											+ licenseId + "\", 2)'>拒绝</a>";
							} else if (unionStatus == 0) {
								if (targetStatus == 3 || blnInFocusTime)
									unionLabel += "&nbsp;<a id='btnUnionConfirm' href='javascript:pageMainObj.unionConfirm(\""
											+ licenseId
											+ "\", 1)'>确认</a>"
											+ "&nbsp;<a id='btnUnionReject' href='javascript:pageMainObj.unionConfirm(\""
											+ licenseId + "\", 2)'>拒绝</a>&nbsp;<a href='javascript:void(0)'; onclick=javascript:pageMainObj.getLicenseUnionList(\""+licenseId+"\",\""+row.targetName+"\");return false;>查看竞买情况</a>";
							} else if (unionStatus == 1) {
								unionLabel += "(已确认)";
								if (targetStatus == 3 || blnInFocusTime)
									unionLabel += "&nbsp;<a href='javascript:void(0)'; onclick=javascript:pageMainObj.getLicenseUnionList(\""+licenseId+"\",\""+row.targetName+"\");return false;>查看竞买情况</a>";
							} else {
								unionLabel += "(已拒绝)";
								if (targetStatus == 3 || blnInFocusTime)
									unionLabel += "&nbsp<a href='javascript:void(0)'; onclick=javascript:pageMainObj.getLicenseUnionList(\""+licenseId+"\",\""+row.targetName+"\");return false;>查看竞买情况</a>";
							}
						} else if (unionCount > 0) {
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
				if (statusDetail != "")
					statusLabel += " (" + statusDetail + ")";
				var rowBody = rowBodyTemplet.format(row.licenseNo, row.targetId, row.targetName, row.applyDate,
						statusLabel);
				licensesBody += rowBody;
			}
			$("#licenses").html(licensesBody);
			// 控制竞买人竞买标的信息弹出
			if (rowCount > 4)
				rowCount = 4;
			$('.i-guide-grid-data').height(rowCount * 23 + 2);
			if ($('.i-guide-grid-data').height() < 92) {
				$('.i-guide-grid-data').css('overflow', 'hidden')
			}
			$('#licensePromptPanel').slideDown(2000);

			window.setTimeout(pageMainObj.queryLicenseData, 4 * 60 * 1000);
		}
	};
	cmd.execute();
}

//---------------------------------
//获取竞买份额tyw
//---------------------------------
pageMainObj.getLicenseUnionList = function(licenseId,name){
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

//---------------------------------
//确认联合竞买申请
//---------------------------------
pageMainObj.unionConfirm = function(licenseId, confirmed) {
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
							pageMainObj.queryLicenseData();
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
			cmd1.goodsType = pageMainObj.goodsType;
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
								pageMainObj.queryLicenseData();
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
// 页面初始化
// ---------------------------------
pageMainObj.viewLicenseList = function() {
	var mainFrame = getMainFrame(window);
	if (mainFrame) {
		mainFrame = mainFrame.document.getElementById('framePage');
		if (mainFrame) {
			mainFrame.src = approot + "/mine/bidder/licenseList.html";
		}
	}
}

// ---------------------------------
// 页面初始化
// ---------------------------------
pageMainObj.initHtml = function() {
	$('#tradeTargets').bind('click', function() {
				pageTradeCommonObj.viewTradeMain();
			});

	$('#noticeTargets').click(function() {
				parent.document.getElementById('framePage').src = approot + "/mine/bidder/productList.html?goodsType=301";
			});
	var mainFrame = getMainFrame(window);
	var div = mainFrame.document.getElementById("quickDiv");
	$(div).hide();
	$(".i-guide-grid-close-btn").bind('click', function() {
				$('.i-guide-grid-panel').hide()
			});
	pageMainObj.goodsType= Utils.getUrlParamValue(document.location.href, "goodsType");
	pageMainObj.queryLicenseData();
	Utils.autoIframeSize();
}

$(document).ready(pageMainObj.initHtml);