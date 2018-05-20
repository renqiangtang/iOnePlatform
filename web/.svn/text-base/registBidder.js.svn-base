//---------------------------------
//页页对象
//---------------------------------
var pageObj = {}
pageObj.staticUserId = '0001';
pageObj.userInfo = getUserInfoObj();
//---------------------------------
//向导页面控制
//---------------------------------
pageObj.steps = ['stepNotice', 'stepUser', 'stepBidderType', 'stepBidder', 'stepCondition', 'stepAttach', 'stepSubmit', 'stepConfirm'];
pageObj.navis = ['naviNotice', 'naviUser', 'naviBidderType', 'naviBidder', 'naviCondition', 'naviAttach', 'naviSubmit', 'naviConfirm'];
pageObj.step = 0;
pageObj.maxStep = 0;
//---------------------------------
//竞买人数据
//---------------------------------
pageObj.id = null;
pageObj.initBidderType = 1;
pageObj.bidderType = 1;
pageObj.status = 0;
pageObj.isCompany = 0;
pageObj.isUnion = 0;
pageObj.bidders = null;
pageObj.newBidder = true;
//---------------------------------
//附件编辑状态
//---------------------------------
pageObj.attachAdd = false;
pageObj.attachEdit = false;
pageObj.attachDelete = false;
pageObj.attachDtlUpload = false;
pageObj.attachDtlDownload = true;
pageObj.attachDtlDelete = false;
//---------------------------------
//是否允许修改部分基本信息，指当getReadonly=true时
//---------------------------------
pageObj.allowModifySome = false;
//---------------------------------
//竞买人数据修改前原始数据
//---------------------------------
pageObj.originalData = null;
//---------------------------------
//标识是否重新选择竞买人类别
//---------------------------------
pageObj.initSelectBidderType = true;
//---------------------------------
//附件加载标识
//---------------------------------
pageObj.attachDataLoaded = false;
//---------------------------------
//数据修改检查对象
//---------------------------------
pageObj.dataModifyedChecker = null;
//---------------------------------
//是否必须用户页
//---------------------------------
pageObj.requiredUser = false;

//---------------------------------
//获取用户信息，未登录则取静态用户ID
//---------------------------------
pageObj.getUserId = function () {
	if (pageObj.userInfo && pageObj.userInfo.userId)
		return pageObj.userInfo.userId;
	else
		return pageObj.staticUserId;
}

//---------------------------------
//上一步
//---------------------------------
pageObj.priorStep = function () {
	if (pageObj.step > 0)
		pageObj.gotoStep(pageObj.step - 1);
	return;
}

//---------------------------------
//下一步
//---------------------------------
pageObj.nextStep = function () {
	if (pageObj.step < pageObj.steps.length - 1)
		pageObj.gotoStep(pageObj.step + 1);
	return;
}

//---------------------------------
//指定步
//---------------------------------
pageObj.gotoStep = function (step) {
	if (typeof(step) == "string") {
		step = $.inArray(step, pageObj.steps);
		if (step == -1)
			return false;
	}
	if (step < 0 || step >= pageObj.steps.length)
		return false;
	var beforeStepCheck = true;
	if (pageObj.id) {
		if (pageObj.getReadonly())
			//查看资料（只读）时可自由前后导航，不作任何数据检查
			beforeStepCheck = false;
		else if (step < pageObj.step)
			//修改资料时可自由向前导航，向后则检查
			beforeStepCheck = false;
	} else if (step < pageObj.step)
		//新注册竞买人时可自由向前导航，向后则检查
		beforeStepCheck = false;
	//执行当前页面步骤的数据检查方法
	var newStepId = pageObj.steps[step];
	if (beforeStepCheck)
		if (!pageObj.beforeStep(pageObj.step, step)) 
			return false;
	//检查经过beforeStep方法后导航数有无变化
	var step = $.inArray(newStepId, pageObj.steps);
	pageObj.step = step;
	if (pageObj.maxStep < step)
		pageObj.maxStep = step;
	for(var i = 0; i < pageObj.steps.length; i++) {
		var stepId = pageObj.steps[i];
		var naviId = pageObj.navis[i];
		if (i == step) {
			$("#" + stepId).show();
			$("#" + naviId).removeClass("ved");
			$("#" + naviId).addClass("sel");
		} else {
			$("#" + stepId).hide();
			$("#" + naviId).removeClass("sel");
			if (i <= pageObj.maxStep) {
				$("#" + naviId).addClass("ved");
			}
		}
		if (i == step || i <= pageObj.maxStep)
			$("#" + naviId + " a").attr("href", "javascript:pageObj.naviStep(" + i + ")");
		else {
			$("#" + naviId + " a").removeAttr("href");
			$("#" + naviId).removeClass("sel");
			$("#" + naviId).removeClass("ved");
		}
	}
	pageObj.afterStep(pageObj.step);
}

//---------------------------------
//步骤跳转前，检查后返回false则限制向目标页跳转
//---------------------------------
pageObj.beforeStep = function (step, newStep) {
	if (typeof(step) == "string") {
		step = $.inArray(step, pageObj.steps);
		if (step == -1) {
			DialogError('导航步骤编号不存在。');
			return false;
		}
	}
	if (step < 0 || step >= pageObj.steps.length) {
		DialogError('导航步骤超出索引。');
		return false;
	}
	var stepSubmitIndex = $.inArray("stepSubmit", pageObj.steps);
	var stepId = pageObj.steps[step];
	var newStepId = pageObj.steps[newStep];
	var checkBidderResult = false;
	//未提交不能到上传附件、审核页面
	if (stepSubmitIndex != -1 && !pageObj.id && newStep > stepSubmitIndex)
		return false;
	if (stepId == "stepUser") {
		if (!pageObj.checkUser())
			return false;
	} else if (stepId == "stepBidder") {
		//离开基本资料页面检查数据
//		checkBidderResult = pageObj.checkBidder();
//		if (!checkBidderResult)
//			return false;
		//点击下一步时自动提交
//		if (!pageObj.saveData())
//			return false;
		if (!pageObj.checkData())
			return false;
		if (pageObj.checkDataModified())
			if (!pageObj.doSaveData(true))
				return false;
	}
	return true;
}

//---------------------------------
//步骤跳转后
//---------------------------------
pageObj.afterStep = function (step) {
	if (typeof(step) == "string") {
		step = $.inArray(step, pageObj.steps);
		if (step == -1)
			return;
	}
	if (step < 0 || step >= pageObj.steps.length)
		return;
	var stepId = pageObj.steps[step];
	//设置下方三个按钮可视性
	$("#priorStep").hide();
	$("#nextStep").hide();
	$("#saveData").hide();
	$("#submitData").hide();
	$("#pnlToolbar").hide();
	//常规情况的工具栏按钮状态
	if (stepId == "stepNotice") {
		if (pageObj.id) {
			$("#chkAlreadyRead").hide();
			$("#lblAlreadyRead").hide();
			$("#nextStep1").hide();
		} else {
			$("#chkAlreadyRead").show();
			$("#lblAlreadyRead").show();
			$("#nextStep1").show();
		}
	} else {
		$("#pnlToolbar").show();
		if (step == pageObj.steps.length - 1) {
			$("#priorStep").show();
			$("#nextStep").hide();
			if (stepId == "stepBidder")
				$("#saveData").show();
		} else {
			$("#priorStep").show();
			$("#nextStep").show();
		}
	}
	//按不同的页面
	if (stepId == "stepBidderType") {
		$("#nextStep").hide();
	} else if (stepId == "stepBidder") {
		if (pageObj.initSelectBidderType) {
			pageObj.initSelectBidderType = false;
			pageObj.initBidders();
			if (pageObj.isUnion == 1) {
				$("#unionNavi").show();
				$("#pnlIsCompany").show();
				//选中第一个联合竞买人
				pageObj.selectUnionBidder(0);
			} else {
				$("#unionNavi").hide();
				$("#pnlIsCompany").hide();
				var bidder = null;
				if (pageObj.bidders && pageObj.bidders.length > 0)
					bidder = pageObj.bidders[0];
				pageObj.bidder2Control(bidder);
				$("input[name='isCompany'][value='0']").attr("checked", pageObj.isCompany != 1);
				$("input[name='isCompany'][value='1']").attr("checked", pageObj.isCompany == 1);
				pageObj.changeIsCompany();
				pageObj.changeIsOversea();
			}
		}
	} else if (stepId == "stepAttach") {
		if (pageObj.isUnion == 1)
			$("#attachNavi").show();
		else
			$("#attachNavi").hide();
		if (!pageObj.attachDataLoaded || pageObj.getActiveAttachOwnerIndex() == -1)
			pageObj.selectAttachOwner(0);
	} else if (stepId == "stepSubmit") {
		if (pageObj.getReadonly()) {
			$("#nextStep").show();
			$("#submitData").hide();
		} else {
			$("#nextStep").hide();
			$("#submitData").show();
		}
		pageObj.createSubmitSummary();
	}
}

//---------------------------------
//获取当前激活的步骤ID
//---------------------------------
pageObj.getActiveStepId = function () {
	if (pageObj.step < 0 || pageObj.step >= pageObj.steps.length)
		return null;
	else
		return pageObj.steps[pageObj.step];
}

pageObj.getStepId = function (step) {
	if (step < 0 || step >= pageObj.steps.length)
		return null;
	else
		return pageObj.steps[step];
}

pageObj.getStep = function (stepId) {
	return $.inArray(stepId, pageObj.steps);
}

//---------------------------------
//添加步骤。注意添加id为stepId的div，否则该步骤无实际内容
//---------------------------------
pageObj.addStep = function (stepId, stepLabel, stepIndex) {
	if (!stepId)
		return false;
	var naviId = "navi" + stepId.substr(4);
	var content = "<li id='{0}'><a><b>{1}</b><span class='f14 fb f2 ml5 fl'>{2}</span></a><i></i></li>";
	if (stepIndex >= 0 && stepIndex < pageObj.steps.length) {
		pageObj.steps.splice(stepIndex, 0, stepId);
		pageObj.navis.splice(stepIndex, 0, naviId);
		$("#registNavi li").each(function() {
			$(this).children("a").each(function() {
				$(this).children("b").each(function() {
					var index = $(this).html();
					index = parseInt(index);
					if (index >= stepIndex + 1)
						$(this).html(index + 1);
					if (index == stepIndex + 1)
						$(this).parent().parent().before(content.format(naviId, stepIndex + 1, stepLabel ? stepLabel : stepId));
				});
			});
		});
	} else {
		pageObj.steps.push(stepId);
		pageObj.navis.push(naviId);
		$("#registNavi").append(content.format(naviId, pageObj.steps.length, stepLabel ? stepLabel : stepId));
		stepIndex = pageObj.steps.length - 1;
	}
	if (pageObj.step >= stepIndex)
		pageObj.step = pageObj.step + 1;
	if (pageObj.maxStep >= stepIndex)
		pageObj.maxStep = pageObj.maxStep + 1;
}

//---------------------------------
//删除指定步骤
//---------------------------------
pageObj.deleteStep = function (stepId) {
	if (!stepId)
		return false;
	var stepIndex = $.inArray(stepId, pageObj.steps);
	var naviId = "navi" + stepId.substr(4);
	$("#" + naviId).remove();
	if (stepIndex != -1) {
		if (pageObj.step > stepIndex)
			pageObj.step = pageObj.step - 1;
		else if (pageObj.step == stepIndex) {
			pageObj.step = pageObj.step - 1;
			gotoStep(pageObj.step);
		}
		if (pageObj.maxStep >= stepIndex)
			pageObj.maxStep = pageObj.maxStep - 1;
		pageObj.steps.splice(stepIndex, 1);
		pageObj.navis.splice(stepIndex, 1);
		$("#registNavi li").each(function() {
			$(this).children("a").each(function() {
				$(this).children("b").each(function() {
					var index = $(this).html();
					index = parseInt(index);
					if (index > stepIndex)
						$(this).html(index - 1);
				});
			});
		});
	}
}

//---------------------------------
//检查基本资料数据
//未传showPrompt或者showPrompt!=false时显示提示信息
//---------------------------------
pageObj.checkBidder = function (showPrompt) {
	var bidderTypeLabel = null;
	if (pageObj.isUnion == 1) {
		bidderTypeLabel = "联合竞买人";
		pageObj.control2Bidder(pageObj.bidders[pageObj.getActiveBidderIndex()]);
	} else {
		bidderTypeLabel = "竞买人";
		pageObj.control2Bidder(pageObj.bidders[0]);
	}
	var bidderNameExists0 = new Array();
	var bidderIdnoExists = new Array();
	var bidderNameExists1 = new Array();
	var bidderRegnoExists = new Array();
	var bidderCompanyCodeExists = new Array();
	if (pageObj.bidders && pageObj.bidders.length > 0) {
		for(var i = 0; i < pageObj.bidders.length; i++) {
			var bidder = pageObj.bidders[i];
			var bidderPrefix = bidderTypeLabel + (bidder.name ? bidder.name : (pageObj.isUnion == 1 ? i + 1 : "")) + "的";
			var isCompany = bidder.is_company;
			if (!bidder.name){
				if (showPrompt != false)
					DialogError(bidderPrefix + (isCompany == 1 || pageObj.bidderType == 3 ? '名称' : '姓名') + '不能为空', window);
				return false;
			}
			if (!bidder.mobile) {
				if (showPrompt != false)
					DialogError(bidderPrefix + '移动电话不能为空', window);
				return false;
			}
			if (!pageObj.verify('mobile', bidder.mobile)) {
				if (showPrompt != false)
					DialogError(bidderPrefix + '移动电话不正确', window);
				return false;
			}
			if (!bidder.address && pageObj.bidderType == 1) {
				if (showPrompt != false)
					DialogError(bidderPrefix + '通讯地址不能为空', window);
				return false;
			}
			if (!bidder.canton_id) {
				if (showPrompt != false)
					DialogError(bidderPrefix + '行政区不能为空', window);
				return false;
			}
//			if (!bidder.email) {
//				if (showPrompt != false)
//					DialogError(bidderPrefix + '电子邮箱不能为空', window);
//				return false;
//			}
			if (bidder.email && !pageObj.verify('email', bidder.email)) {
				if (showPrompt != false)
					DialogError(bidderPrefix + '电子邮箱不正确', window);
				return false;
			}
			if (isCompany == 1 || pageObj.bidderType == 3) {
				if (!bidder.contact) {
					if (showPrompt != false)
						DialogError(bidderPrefix + '联系人不能为空', window);
					return false;
				}
				if (!bidder.reg_corporation) {
					if (showPrompt != false)
						DialogError(bidderPrefix + '企业法定代表人不能为空', window);
					return false;
				}
				if (!bidder.reg_corporation_idno) {
					if (showPrompt != false)
						DialogError(bidderPrefix + '企业法定代表人证件号码不能为空', window);
					return false;
				}
				if (!bidder.reg_no) {
					if (showPrompt != false)
						DialogError(bidderPrefix + '营业执照号不能为空', window);
					return false;
				}
				if (!bidder.certificate_no) {
					if (showPrompt != false)
						DialogError(bidderPrefix + '组织机构代码不能为空', window);
					return false;
				}
				if (!bidder.reg_address && pageObj.bidderType != 1) {
					if (showPrompt != false)
						DialogError(bidderPrefix + '注册地址不能为空', window);
					return false;
				}
			} else {
				if (!bidder.certificate_no) {
					if (showPrompt != false)
						DialogError(bidderPrefix + '身份证不能为空', window);
					return false;
				}
				if (!pageObj.verify('idno', bidder.certificate_no)) {
				if (showPrompt != false)
					DialogError(bidderPrefix + '身份证不正确', window);
				return false;
			}
			}
			if (pageObj.isUnion == 1) {
				if (isCompany == 1 || pageObj.bidderType == 3) {
					if ($.inArray(bidder.name, bidderNameExists1) != -1) {
						if (showPrompt != false)
							DialogError(bidderPrefix + '名称不能重复', window);
						return false;
					} else
						bidderNameExists1.push(bidder.name);
					if ($.inArray(bidder.reg_no, bidderRegnoExists) != -1) {
						if (showPrompt != false)
							DialogError(bidderPrefix + '营业执照编号不能重复', window);
						return false;
					} else
						bidderRegnoExists.push(bidder.reg_no);
					if ($.inArray(bidder.certificate_no, bidderCompanyCodeExists) != -1) {
						if (showPrompt != false)
							DialogError(bidderPrefix + '组织机构代码不能重复', window);
						return false;
					} else
						bidderCompanyCodeExists.push(bidder.certificate_no);
				} else {
//					if ($.inArray(bidder.name, bidderNameExists0) != -1) {
//						DialogError(bidderPrefix + '姓名不能重复', window);
//						return false;
//					} else
//						bidderNameExists0.push(bidder.name);
					if ($.inArray(bidder.certificate_no, bidderIdnoExists) != -1) {
						if (showPrompt != false)
							DialogError(bidderPrefix + '身份证不能重复', window);
						return false;
					} else
						bidderIdnoExists.push(bidder.certificate_no);
				}
			} else {
				//非联合仅检查第一个竞买信息，先选择联合后重新选择个人或者企业的情况下会走到这里。未删除是为了在内存中保留联合时录入的数据
				break;
			}
		}
	} else {
		if (showPrompt != false)
			DialogError('未输入竞买人信息', window);
		return false;
	}
	return true;
}

//---------------------------------
//检查用户数据
//---------------------------------
pageObj.checkUser = function (showPrompt) {
	alert(222);
	if (pageObj.id) {
		if ($("#txtPassword").val() != "" || $("#txtConfirmPassword").val() != "") {
			if ($("#txtPassword").val() != $("#txtConfirmPassword").val()) {
				if (showPrompt != false)
					DialogError('确认密码不一致', window);
				return false;
			}
		}
	} else {
		if (!$("#txtUserName").val()) {
			if (showPrompt != false)
				DialogError('登录用户不能为空', window);
			return false;
		}
		if ($("#txtPassword").val() == "") {
			if (showPrompt != false)
				DialogError('登录密码不能为空', window);
			return false;
		}
		if ($("#txtPassword").val() != $("#txtConfirmPassword").val()) {
			if (showPrompt != false)
				DialogError('确认密码不一致', window);
			return false;
		}
		if (!pageObj.dataModifyedChecker.verify($("#stepUser")))
			return false;
	}
	if (!pageObj.id || pageObj.dataModifyedChecker.checkElementModified($("#txtUserName")))
		if (pageObj.checkUserNameExists()) {
			if (showPrompt != false)
				DialogError('用户名已存在', window);
			return false;
		}
	return true;
}

//---------------------------------
//检查所有数据
//---------------------------------
pageObj.checkData = function (showPrompt) {
	return pageObj.checkBidder(showPrompt) && (!pageObj.requiredUser || pageObj.checkUser(showPrompt));
}

//---------------------------------
//检查所有必须的附件材料是否都提交
//---------------------------------
pageObj.checkRequiredAttach = function (showPrompt) {
	if (!pageObj.id) {
		if (showPrompt != false)
			DialogError('注册信息还未保存。', window);
		return false;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Attach";
	cmd.method = "requiredAttachCheck";
	cmd.u = pageObj.getUserId();
	var owners = new Array();
	var owner = new Object();
	owner.refId = pageObj.id;
	owner.refTableName = "trans_bidder";
	owners.push(owner);
	if (pageObj.isUnion == 1) {
		for(var i = 0; i < pageObj.bidders.length; i++) {
			owner = new Object();
			owner.refId = pageObj.bidders[i].id;
			owner.refTableName = "trans_bidder_union";
			owners.push(owner);
		}
	}
	cmd.owners = JSON.stringify(owners);
	var result = false;
	cmd.success = function (data) {
		if (data.state == 1) {
//			if (showPrompt != false)
//				DialogAlert('必需的附件全部提交。', window);
			result = true;
		} else {
			if (showPrompt != false) {
				var failOwners = data.failOwners;
				var message = "有必需的附件未提交，";
				if (failOwners && failOwners.length > 0) {
					var ownerError = "";
					if (pageObj.isUnion == 1) {
						for(var j = 0; j < failOwners.length; j++) {
							var failOwner = failOwners[j];
							if (failOwner.refId == pageObj.id)
								ownerError += "公共材料：<br/>";
							else {
								for(var i = 0; i < pageObj.bidders.length; i++) {
									if (pageObj.bidders[i].id == failOwner.refId) {
										ownerError += pageObj.bidders[i].name + "材料：<br/>";
										break;
									}
								}
							}
							var attachError = "";
							var errorAttachs = failOwner ? failOwner.errorAttachs : null;
							if (errorAttachs && errorAttachs.length > 0)
								for(var i = 0; i < errorAttachs.length; i++)
									attachError += "&nbsp;&nbsp;" + errorAttachs[i] + "<br/>";
							ownerError += attachError ? attachError : "请检查上传材料清单。";
						}
					} else {
						var failOwner = failOwners[0];
						var errorAttachs = failOwner ? failOwner.errorAttachs : null;
						if (errorAttachs && errorAttachs.length > 0)
							for(var i = 0; i < errorAttachs.length; i++)
								ownerError += errorAttachs[i] + "<br/>";
					}
					message += ownerError ? "包括：<br/>" + ownerError : "请检查上传材料清单。";
				} else
					message += data.message ? '错误原因：' + data.message : '未知错误原因。';
				DialogError(message, window);
			}
			return false;
		}
	}
	cmd.execute();
	return result;
}

//---------------------------------
//检查数据有无修改
//attachCheck=true则检查是否必须保存数据后才能上传附件
//---------------------------------
pageObj.checkDataModified = function (attachCheck) {
	if (!pageObj.id)
		return true;
	if (pageObj.bidders && pageObj.originalData) {
		if (pageObj.isCompany != pageObj.originalData.isCompany || pageObj.isUnion != pageObj.originalData.isUnion)
			return true;
		else {
			if (!pageObj.originalData.bidders || pageObj.originalData.bidders.length != pageObj.bidders.length)
				return true;
			else {
				for(var i = 0; i < pageObj.originalData.bidders.length; i++) {
					var originalBidder = pageObj.originalData.bidders[i];
					var deleted = true;
					for(var j = 0; j < pageObj.bidders.length; j++) {
						var bidder = pageObj.bidders[j];
						if (bidder.id == originalBidder.id) {
							deleted = false;
							if (attachCheck) {
								if (bidder.is_company != originalBidder.is_company)
									return true;
							} else {
								for(var key in originalBidder) {
									if (key in bidder) {
										if (!pageObj.compareBidderString(bidder[key], originalBidder[key]))
											return true;
									} else
										return true;
								}
							}
							break;
						}
					}
					if (deleted)
						return true;
				}
				for(var i = 0; i < pageObj.bidders.length; i++) {
					if (!pageObj.id)
						return true;
				}
			}
		}
	} else
		return true;
	if (!attachCheck && $("#txtPassword").val())
		return true;
	return false;
}

//---------------------------------
//导航跳转
//---------------------------------
pageObj.naviStep = function (step) {
	if (typeof(step) == "string") {
		step = $.inArray(step, pageObj.steps);
		if (step == -1)
			return false;
	}
	if (step < 0 || step >= pageObj.steps.length || step > pageObj.maxStep)
		return false;
	pageObj.gotoStep(step);
}

//---------------------------------
//审核状态页面跳转
//---------------------------------
pageObj.gotoModifyBidderDataStep = function () {
	pageObj.gotoStep("stepBidder");
}

pageObj.gotoUploadAttachStep = function (step) {
	pageObj.gotoStep("stepAttach");
}

//---------------------------------
//竞买人类型选择：0个人、1企业、2联合
//---------------------------------
pageObj.selectBidderType = function (selected) {
	//pageObj.clearBidderData();
	if (selected == 1) {
		pageObj.isCompany = 1;
		pageObj.isUnion = 0;
	} else if (selected == 2) {
		pageObj.isCompany = 0;
		pageObj.isUnion = 1;
	} else {
		pageObj.isCompany = 0;
		pageObj.isUnion = 0;
	}
	pageObj.initSelectBidderType = true;
	pageObj.gotoStep("stepBidder");
}

//---------------------------------
//添加联合竞买人
//---------------------------------
pageObj.unionBidderHtml = "<li>"
	+ "	<a id='unionBidder{0}' href='{1}'>"
	+ "		联合竞买人{2}"
	+ "	</a>"
	+ "	<i id='deleteUnionBidder{0}' {3}>"
	+ "	</i>"
	+ "</li>";
pageObj.addUnionBidder = function (selected, bidderIndex) {
	var unionBidderIndex = 2;
	if (typeof(bidderIndex) == "number") {
		if (bidderIndex < unionBidderIndex)
			return;
		unionBidderIndex = bidderIndex;
	} else {
		if (pageObj.bidders && pageObj.bidders.length > 2)
			unionBidderIndex = pageObj.bidders.length;
	}
	var unionBidderHtml = pageObj.unionBidderHtml.format(unionBidderIndex,
		"javascript:pageObj.selectUnionBidder(" + unionBidderIndex + ")",
		unionBidderIndex + 1,
		pageObj.id ? "style='display:none'" : "");
	$("#unionList").append(unionBidderHtml);
	$("#deleteUnionBidder" + unionBidderIndex).click(pageObj.deleteUnionBidder);
	pageObj.initBidders(unionBidderIndex + 1);
	if (selected != false)
		pageObj.selectUnionBidder(unionBidderIndex);
}

//---------------------------------
//删除联合竞买人
//---------------------------------
pageObj.deleteUnionBidder = function () {
	var srcElement = event.srcElement;
	if (!srcElement)
		return;
	var index = $(srcElement).attr("id");
	index = index.substr("deleteUnionBidder".length);
	index = parseInt(index);
	$(srcElement).parent().remove();
	if (pageObj.bidders && pageObj.bidders.length > index) {
		pageObj.bidders.splice(index, 1);
		//将后面的导航元素全部向前移动一位，因为没有在导航元素上记录索引(pageObj.bidders)
		if (pageObj.bidders.length > index) {
			$("#unionList li").each(function() {
				$(this).children("a").each(function() {
					var aid = $(this).attr("id");
					var bidderIndex = aid.substr("unionBidder".length);
					bidderIndex = parseInt(bidderIndex);
					if (bidderIndex > index) {
						var newIndex = bidderIndex - 1;
						var newId = "unionBidder" + newIndex;
						var bidder = pageObj.bidders[newIndex];
						var bidderName = bidder.name;
						if (bidderName)
							bidderName += " " + bidderIndex;
						else
							bidderName = "联合竞买人" + bidderIndex;
						$(this).attr("id", newId);
						$(this).attr("href", "javascript:pageObj.selectUnionBidder(" + newIndex + ")");
						$(this).html(bidderName);
						newId = "deleteUnionBidder" + newIndex;
						$("#deleteUnionBidder" + bidderIndex).attr("id", newId);
					}
				});
			});
		}
		var activeIndex = pageObj.bidders.length > index ? index : index - 1;
		pageObj.selectUnionBidder(activeIndex);
	}
}

//---------------------------------
//选择联合竞买人
//---------------------------------
pageObj.selectUnionBidder = function (unionBidderIndex, updateBidder) {
	//将之前的界面数据写入内存
	var bidder = null;
	if (updateBidder != false) {
		bidder = pageObj.bidders[pageObj.getActiveBidderIndex()];
		pageObj.control2Bidder(bidder);
//		if (pageObj.steps[pageObj.step] == "stepBidder" && pageObj.checkDataModified(true))
//			$("#nextStep").hide();
	}
	//切换到目标竞买人数据
	bidder = null;
	if (pageObj.bidders && pageObj.bidders.length > 0 && unionBidderIndex >= 0 && unionBidderIndex < pageObj.bidders.length)
		bidder = pageObj.bidders[unionBidderIndex];
	pageObj.bidder2Control(bidder);
	pageObj.selectUnionBidderNavi(unionBidderIndex);
	$("#txtName").focus();
}

//---------------------------------
//选择联合竞买人节点
//---------------------------------
pageObj.selectUnionBidderNavi = function (unionBidderIndex) {
	$("#unionList li").each(function() {
		$(this).removeClass("sel");
		if (unionBidderIndex > 0) {
			var selected = false;
			$(this).children("a").each(function() {
				var index = $(this).attr("id");//a标签id
				index = parseInt(index.substr("unionBidder".length));
				if (index == unionBidderIndex) {
					selected = true;
				}
			});
			if (selected)
				$(this).addClass("sel");
		}
	});
	if (unionBidderIndex == 0)
		$("#unionList li:first-child").addClass("sel");
}

//---------------------------------
//清空联合竞买人节点，剩余两个必须的
//---------------------------------
pageObj.clearUnionBidderNavi = function () {
	$("#unionList li").each(function() {
		$(this).children("a").each(function() {
			var index = $(this).attr("id");
			index = index.substr("unionBidder".length);
			if (index <= 1)
				$(this).html("联合竞买人" + (++index));
			else
				$(this).parent().remove();
		});
	});
	pageObj.selectUnionBidderNavi(0);
}

//---------------------------------
//初始化联合竞买人节点
//---------------------------------
pageObj.initUnionBidderNavi = function () {
	pageObj.clearUnionBidderNavi();
	if (pageObj.bidders && pageObj.bidders.length > 0) {
		for(var i = 0; i < pageObj.bidders.length; i++) {
			if (i > 1)
				pageObj.addUnionBidder(false, i);
			pageObj.syncBidderName2Navi(pageObj.bidders[i].name, i);
		}
	}
	pageObj.selectUnionBidderNavi(0);
}

//---------------------------------
//同步指定联合竞买人导航节点显示名称
//---------------------------------
pageObj.syncBidderName2Navi = function (bidderName, bidderIndex) {
	var showBidderIndex = bidderIndex + 1;
	var showBidderName = bidderName;
	if (showBidderName)
		showBidderName += " " + showBidderIndex;
	else
		showBidderName = "联合竞买人" + showBidderIndex;
	var syncBidderName = false;
	$("#unionList li").each(function() {
		if (!syncBidderName) {
			$(this).children("a").each(function() {
				var index = $(this).attr("id");
				index = parseInt(index.substr("unionBidder".length));
				if (index == bidderIndex) {
					$(this).html(showBidderName);
					$(this).attr("title", showBidderName);
					syncBidderName = true;
				}
			});
		}
	});
	pageObj.syncBidderName2OwnerNavi(bidderName, bidderIndex);
}

//---------------------------------
//竞买人输入框值变化时同步联合竞买人导航节点
//---------------------------------
pageObj.changeBidderName = function () {
	if (pageObj.isUnion == 1)
		pageObj.syncBidderName2Navi($("#txtName").val(), pageObj.getActiveBidderIndex());
}

//---------------------------------
//获取当前联合竞买人索引，通过导航节点
//---------------------------------
pageObj.getActiveBidderIndex = function () {
	var result = -1;
	$("#unionList li").each(function() {
		if (result == -1 && $(this).hasClass("sel")) {
			$(this).children("a").each(function() {
				var index = $(this).attr("id");
				index = index.substr("unionBidder".length);
				result = parseInt(index);
			});
		}
	});
	return result;
}

//---------------------------------
//清空附件页面的联合竞买人节点
//---------------------------------
pageObj.clearAttachOwnerNavi = function () {
	$("#ownerList li").each(function() {
		$(this).children("a").each(function() {
			$(this).parent().remove();
		});
	});
}

//---------------------------------
//添加联合竞买人
//---------------------------------
pageObj.attachOwnerHtml = "<li>"
	+ "	<a id='{0}' href='{1}'>"
	+ "		{2}"
	+ "	</a>"
	+ "</li>";
pageObj.addAttachOwner = function (ownerIndex) {
	var ownerId = null;
	var ownerLabel = null;
	if (ownerIndex == -1)
		ownerLabel = "公用材料";
	else {
		if (!(pageObj.bidders && ownerIndex >= 0 && ownerIndex < pageObj.bidders.length))
			return;
		var showOwnerIndex = ownerIndex + 1;
		ownerLabel = "联合竞买人" + showOwnerIndex;
	}
	ownerId = "owner" + ownerIndex;
	var ownerHtml = pageObj.attachOwnerHtml.format(ownerId,
		"javascript:pageObj.selectAttachOwner(" + ownerIndex + ")",
		ownerLabel);
	$("#ownerList").append(ownerHtml);
}

//---------------------------------
//初始化附件页面的联合竞买人节点
//---------------------------------
pageObj.initAttachOwnerNavi = function () {
	pageObj.clearAttachOwnerNavi();
	if (pageObj.bidders && pageObj.bidders.length > 0) {
		for(var i = 0; i < pageObj.bidders.length; i++) {
			pageObj.addAttachOwner(i);
			pageObj.syncBidderName2OwnerNavi(pageObj.bidders[i].name, i);
		}
	}
	pageObj.addAttachOwner(-1);
	pageObj.selectUnionBidderNavi(0);
}

//---------------------------------
//同步指定附件联合竞买人导航节点显示名称
//---------------------------------
pageObj.syncBidderName2OwnerNavi = function (bidderName, bidderIndex) {
	var showBidderIndex = bidderIndex + 1;
	var showBidderName = bidderName;
	if (showBidderName)
		showBidderName += " " + showBidderIndex;
	else
		showBidderName = "联合竞买人" + showBidderIndex;
	var syncBidderName = false;
	$("#ownerList li").each(function() {
		if (!syncBidderName) {
			$(this).children("a").each(function() {
				var index = $(this).attr("id");
				index = parseInt(index.substr("owner".length));
				if (index == bidderIndex) {
					$(this).html(showBidderName);
					$(this).attr("title", showBidderName);
					syncBidderName = true;
				}
			});
		}
	});
}

//---------------------------------
//选择附件所有者导航节点
//---------------------------------
pageObj.selectAttachOwnerNavi = function (ownerIndex) {
	$("#ownerList li").each(function() {
		$(this).removeClass("sel");
		if (ownerIndex > 0) {
			var selected = false;
			$(this).children("a").each(function() {
				var index = $(this).attr("id");//a标签id
				index = parseInt(index.substr("owner".length));
				if (index == ownerIndex) {
					selected = true;
				}
			});
			if (selected)
				$(this).addClass("sel");
		}
	});
	if (ownerIndex == 0)
		$("#ownerList li:first-child").addClass("sel");
	else if (ownerIndex == -1)
		$("#ownerList li:last-child").addClass("sel");
}

//---------------------------------
//选择附件所有者
//---------------------------------
pageObj.selectAttachOwner = function (ownerIndex) {
	var bidder = null;
	if (pageObj.bidders && pageObj.bidders.length > 0 && ownerIndex >= 0 && ownerIndex < pageObj.bidders.length)
		bidder = pageObj.bidders[ownerIndex];
	pageObj.selectAttachOwnerNavi(ownerIndex);
	var ownerId = null;
	var ownerTableName = null;
	var templetNo = null;
	if (ownerIndex == -1) {
		ownerId = pageObj.id;
		ownerTableName = "trans_bidder";
		templetNo = Utils.decode(pageObj.bidderType, 1, 'house', 2, 'mineral', 3, 'plow', 'land') + 'Bidder2';
	} else if (bidder) {
		ownerId = bidder.id;
		ownerTableName = bidder.__type == 0 ? "trans_bidder" : "trans_bidder_union";
		templetNo = Utils.decode(pageObj.bidderType, 1, 'house', 2, 'mineral', 3, 'plow', 'land') + 'Bidder' + Utils.decode(bidder.is_company, 1, '1', '0');
	}
	pageObj.loadAttachData(ownerId, ownerTableName, templetNo);
}

//---------------------------------
//获取当前联合竞买人索引，通过导航节点
//---------------------------------
pageObj.getActiveAttachOwnerIndex = function () {
	var result = -1;
	$("#ownerList li").each(function() {
		if (result == -1 && $(this).hasClass("sel")) {
			$(this).children("a").each(function() {
				var index = $(this).attr("id");
				index = index.substr("owner".length);
				result = parseInt(index);
			});
		}
	});
	return result;
}

//---------------------------------
//清空基本资料所有输入项
//---------------------------------
pageObj.clearBidderControl = function () {
	$("#txtName").val("");
	$("input[name='isCompany'][value='0']").attr("checked", pageObj.isCompany != 1);
	$("input[name='isCompany'][value='1']").attr("checked", pageObj.isCompany == 1);
	$("input[name='isOversea'][value='0']").attr("checked", true);
	pageObj.changeIsCompany();
	pageObj.changeIsOversea();
	$("#txtMobile").val("");
	$("#txtEmail").val("");
	$("#txtCertificateNo").val("");
	$("#txtContact").val("");
	$("#txtCompanyCode").val("");
	$("#txtRegNo").val("");
	$("#txtRegAddress").val("");
	$("#txtRegCapital").val("");
	$("#txtRegArea").val("");
	$("#txtRegType").val("");
	$("#txtRegCorporation").val("");
	$("#txtRegCorporationIdno").val("");
	$("#txtTel").val("");
	$("#txtFax").val("");
	$("#txtAddress").val("");
	$("input[name='isOversea'][value='0']").attr("checked", true);
	pageObj.clearUnionBidderNavi();
}

//---------------------------------
//清空用户所有输入项
//---------------------------------
pageObj.clearUserControl = function () {
	$("#txtUserName").val("");
	$("#txtPassword").val("");
	$("#txtConfirmPassword").val("");
	$("#imgUserPasswordOk").hide();
}

pageObj.clearConfirmControl = function () {
	$("#lblStatus").html(Utils.decode(pageObj.status, 5, "审核中", 1, "已通过", comObj.bidderStatus[pageObj.status]));
	if (pageObj.id && pageObj.status == 0) {
		$("#lblStatusPrompt").html("<请及时完善注册资料及上传材料后提交审核。>");
		$("#lblStatusPrompt").show();
	} else {
		$("#lblStatusPrompt").hide();
	}
	$("#lblConfirmDate").val("");
	$("#confirmFail").hide();
	$("#lblConfirmOpinion").val("");
	$("#gotoPrefix").hide();
	$("#gotoModifyBidderData").hide();
	$("#gotoUploadAttach").hide();
}

pageObj.clearAttachControl = function () {
	var attachFrame = $('#attachFrame');
	if (attachFrame.length > 0 || pageObj.attachDataLoaded) {
		$('#attachPanel').html("");
		pageObj.attachDataLoaded = false;
	}
}

//---------------------------------
//初始化界面内容
//---------------------------------
pageObj.initControl = function () {
	//设置界面控件可写、只读情况
	var readonly = pageObj.getReadonly();
	$("#bidderEditor input").each(function() {
		$(this).attr("readonly", readonly);
	});
	$("#bidderEditor select").each(function() {
		$(this).attr("disabled", readonly);
	});
	$("#bidderEditor select").each(function() {
		$(this).attr("disabled", readonly);
	});
	$("#bidderEditor input[type='radio']").each(function() {
		$(this).attr("disabled", readonly);
	});
	$("#txtPassword").attr("readonly", false);
	$("#txtConfirmPassword").attr("readonly", false);
	if (readonly)
		$("#btnSelectCanton").hide();
	else
		$("#btnSelectCanton").show();
	if (pageObj.id) {
		$("#txtUserName").attr("readonly", true);
		$("#btnRegist").hide();
		$("#btnSave").show();
		$("#lblPasswordRequired").hide();
		$("#lblConfirmPasswordRequired").hide();
	} else {
		$("#txtUserName").attr("readonly", false);
		$("#txtName").attr("readonly", false);
		$("#btnRegist").show();
		$("#btnSave").hide();
		$("#lblPasswordRequired").show();
		$("#lblConfirmPasswordRequired").show();
	}
	pageObj.initBidders();
	if (pageObj.isUnion == 1) {
		$("#unionNavi").show();
		$("#pnlIsCompany").show();
	} else {
		$("#unionNavi").hide();
		$("#pnlIsCompany").hide();
		$("input[name='isCompany'][value='0']").attr("checked", pageObj.isCompany != 1);
		$("input[name='isCompany'][value='1']").attr("checked", pageObj.isCompany == 1);
		pageObj.changeIsCompany();
		pageObj.changeIsOversea();
	}
	$("#txtCantonName").attr("readonly", true);
}

//---------------------------------
//检查数据是否只读
//---------------------------------
pageObj.getReadonly = function () {
	if (!pageObj.id || pageObj.status == 0 || pageObj.status == 6)
		return false;
	else
		return true;
}

//---------------------------------
//获取界面数据竞买人对象
//---------------------------------
pageObj.control2Bidder = function (bidder) {
	var bidderObj = bidder ? bidder : {};
	bidderObj.name = $("#txtName").val();
	bidderObj.mobile = $("#txtMobile").val();
	bidderObj.email = $("#txtEmail").val();
	bidderObj.is_company = $("input[name='isCompany']:checked").val();
	bidderObj.address = $("#txtAddress").val();
	bidderObj.canton_id = $("#txtCantonId").val();
	bidderObj.canton_name = $("#txtCantonName").val();
	//bidderObj.post_code = $("#txtPostCode").val();
	bidderObj.contact = null;
	bidderObj.reg_no = null;
	bidderObj.reg_address = null;
	bidderObj.reg_capital = null;
	bidderObj.reg_type = null;
	bidderObj.reg_area = null;
	bidderObj.reg_corporation = null;
	bidderObj.reg_corporation_idno = null;
	bidderObj.certificate_no = null;
	if (bidderObj.is_company == 1 || pageObj.bidderType == 3) {//企业信息
		bidderObj.is_oversea = 0;
		bidderObj.contact = $("#txtContact").val();
		bidderObj.certificate_no = $("#txtCompanyCode").val();
		bidderObj.reg_no = $("#txtRegNo").val();
		bidderObj.reg_address = $("#txtRegAddress").val();
		bidderObj.reg_capital = $("#txtRegCapital").val();
		bidderObj.reg_type = $("#txtRegType").val();
		bidderObj.reg_area = $("#txtRegArea").val();
		bidderObj.reg_corporation = $("#txtRegCorporation").val();
		bidderObj.reg_corporation_idno = $("#txtRegCorporationIdno").val();
		bidderObj.tel = $("#txtTel").val();
		bidderObj.fax = $("#txtFax").val();
	} else {//个人信息
		bidderObj.is_oversea = 0;
		bidderObj.certificate_no = $("#txtCertificateNo").val();
	}
	return bidderObj;
}

//---------------------------------
//竞买人对象写入界面
//---------------------------------
pageObj.bidder2Control = function (bidder) {
	if (bidder) {
		$("#txtName").val(bidder.name);
		$("#txtMobile").val(bidder.mobile);
		$("input[name='isCompany'][value='" + bidder.is_company + "']").attr("checked", true);
		pageObj.changeIsCompany();
		if (bidder.is_company == 1 || pageObj.bidderType == 3) {
			//企业信息
			$("#txtCompanyCode").val(bidder.certificate_no);
			$("#txtContact").val(bidder.contact);
			$("#txtRegNo").val(bidder.reg_no);
			$("#txtRegAddress").val(bidder.reg_address);
			$("#txtRegCapital").val(bidder.reg_capital);
			$("#txtRegType").val(bidder.reg_type);
			$("#txtRegArea").val(bidder.reg_area);
			$("#txtRegCorporation").val(bidder.reg_corporation);
			$("#txtRegCorporationIdno").val(bidder.reg_corporation_idno);
			$("#txtTel").val(bidder.tel);
			$("#txtFax").val(bidder.fax);
		} else {
			//个人信息
			$("input[name='isOversea'][value='" + bidder.is_oversea + "']").attr("checked", true);
			pageObj.changeIsOversea();
			$("#txtCertificateNo").val(bidder.certificate_no);
		}
		$("#txtEmail").val(bidder.email);
		$("#txtAddress").val(bidder.address);
		$("#txtCantonId").val(bidder.canton_id);
		$("#txtCantonName").val(bidder.canton_name);
		//$("#txtPostCode").val(bidder.post_code);
	} else
		pageObj.clearBidderControl();
}

//---------------------------------
//清除所有竞买人数据，包括界面内容
//---------------------------------
pageObj.clearBidderData = function () {
	pageObj.id = null;
	pageObj.userId = null;
	pageObj.cakeyId = null;
	//pageObj.bidderType = 1;
	pageObj.bidderType = pageObj.initBidderType;
	pageObj.changeBidderType();
	pageObj.status = 0;
	pageObj.isCompany = 0;
	pageObj.isUnion = 0;
	pageObj.bidders = null;
	pageObj.clearBidderControl();
	pageObj.clearUserControl();
	pageObj.clearConfirmControl();
	pageObj.clearAttachControl();
	pageObj.initControl();
	pageObj.step = 0;
	pageObj.maxStep = 0;
	pageObj.newBidder = true;
	pageObj.attachDtlUpload = true;
	pageObj.attachDtlDownload = true;
	pageObj.attachDtlDelete = true;
	pageObj.gotoStep(pageObj.step);
}

//---------------------------------
//初始化竞买人数据数组
//---------------------------------
pageObj.initBidders = function (unionCount) {
	if (!pageObj.bidders)
		pageObj.bidders = new Array();
	var bidderCount = 0;
	if (unionCount)
		bidderCount = unionCount;
	else {
		if (pageObj.isUnion == 1)
			bidderCount = 2;
		else
			bidderCount = 1;
	}
	for(var i = pageObj.bidders.length; i < bidderCount; i++) {
		var bidder = {};
		pageObj.bidders.push(bidder);
	}
}

pageObj.getBidderString = function (s) {
	if (s == null || s == undefined)
		return "";
	else
		return s;
}
pageObj.compareBidderString = function (s1, s2) {
	return pageObj.getBidderString(s1) == pageObj.getBidderString(s2);
}

//---------------------------------
//生成摘要数据
//---------------------------------
pageObj.createSubmitSummary = function () {
	$("#submitSummary").html("正在汇总摘要内容...");
	var tableContent = "";
	var row = "<tr><th width='20%'>{0}：</th><td width='80%' class='c2'>{1}</td></tr>";
	var subRow = "<tr><td width='30%' align='right'>{0}：</td><td width='70%' class='c2'>{1}</td></tr>";
	var subTable = "<table width='100%' border='0' cellspacing='0' cellpadding='3'>{0}</table>";
	var replaceTo = "<img src='" + approot + "/base/skins/default/images/dateeditor/icon-next.gif'/>";
	var rowContent = null;
	//新注册竞买人
	//tableContent += row.format("登录用户名", $("#txtUserName").val());
	tableContent += row.format("竞买人类别", pageObj.isUnion == 1 ? "联合竞买人" : (pageObj.bidderType == 3 ? (pageObj.isCompany == 1 ? "企业" : "土地储备机构") : (pageObj.isCompany == 1 ? "企业竞买人" : "个人竞买人")));
	if (pageObj.isUnion == 1) {
		for(var i = 0; i < pageObj.bidders.length; i++) {
			rowContent = "";
			rowContent += subRow.format("类别", pageObj.bidderType == 3 ? (pageObj.bidders[i].is_company == 1 ? "企业" : "土地储备机构") : (pageObj.bidders[i].is_company == 1 ? "企业" : "个人"));
			rowContent += subRow.format(pageObj.bidders[i].is_company == 1 || pageObj.bidderType == 3 ? "名称" : "姓名", pageObj.bidders[i].name);
			if (pageObj.bidders[i].is_company == 1 || pageObj.bidderType == 3) {
				rowContent += subRow.format("联系人",  pageObj.bidders[i].contact);
				rowContent += subRow.format("法定代表人名称", pageObj.bidders[i].reg_corporation);
				rowContent += subRow.format("法定代表人证件号码", pageObj.bidders[i].reg_corporation_idno);
				rowContent += subRow.format("营业执照号", pageObj.bidders[i].reg_no);
				rowContent += subRow.format("组织机构代码", pageObj.bidders[i].certificate_no);
				rowContent += subRow.format("注册地址", pageObj.bidders[i].reg_address);
				if (pageObj.bidders[i].reg_capital)
					rowContent += subRow.format("注册资本", pageObj.bidders[i].reg_capital);
				if (pageObj.bidders[i].reg_type)
					rowContent += subRow.format("企业类别", pageObj.bidders[i].reg_type);
				if (pageObj.bidders[i].reg_area)
					rowContent += subRow.format("经营范围", pageObj.bidders[i].reg_area);
				if (pageObj.bidders[i].tel)
					rowContent += subRow.format("电话", pageObj.bidders[i].tel);
				if (pageObj.bidders[i].fax)
					rowContent += subRow.format("传真", pageObj.bidders[i].fax);
			} else {
				rowContent += subRow.format("户籍类型", Utils.decode(pageObj.bidders[i].is_oversea, 0, "深户", 1, "境内非深户", 2, "港澳居民", 3, "台湾居民", 4, "外籍人士", "其它"));
				rowContent += subRow.format(Utils.decode(pageObj.bidders[i].is_oversea, 2, "港澳身份证号", 3, "台胞证号", 4, "护照号", 5, "证件号", "身份证号"), pageObj.bidders[i].certificate_no);
			}
			rowContent += subRow.format("移动电话", pageObj.bidders[i].mobile);
			if (pageObj.bidders[i].email)
				rowContent += subRow.format("电子邮箱", pageObj.bidders[i].email);
			if (pageObj.bidders[0].address)
				rowContent += subRow.format("通讯地址", pageObj.bidders[0].address);
			tableContent += row.format("联合竞买人&nbsp;" + (i + 1), subTable.format(rowContent));
		}
	} else {
		tableContent += row.format(pageObj.bidders[0].is_company == 1 || pageObj.bidderType == 3 ? "名称" : "姓名", pageObj.bidders[0].name);
		if (pageObj.bidders[0].is_company == 1 || pageObj.bidderType == 3) {
			tableContent += row.format("联系人", pageObj.bidders[0].contact);
			tableContent += row.format("法定代表人名称", pageObj.bidders[0].reg_corporation);
			tableContent += row.format("法定代表人证件号码", pageObj.bidders[0].reg_corporation_idno);
			tableContent += row.format("营业执照号", pageObj.bidders[0].reg_no);
			tableContent += row.format("组织机构代码", pageObj.bidders[0].certificate_no);
			if (pageObj.bidders[0].reg_capital)
				rowContent += subRow.format("注册资本", pageObj.bidders[0].reg_capital);
			if (pageObj.bidders[0].reg_type)
				rowContent += subRow.format("企业类别", pageObj.bidders[0].reg_type);
			if (pageObj.bidders[0].reg_area)
				rowContent += subRow.format("经营范围", pageObj.bidders[0].reg_area);
			if (pageObj.bidders[0].tel)
				rowContent += subRow.format("电话", pageObj.bidders[0].tel);
			if (pageObj.bidders[0].fax)
				rowContent += subRow.format("传真", pageObj.bidders[0].fax);
		} else {
			tableContent += row.format("户籍类型", Utils.decode(pageObj.bidders[0].is_oversea, 0, "深户", 1, "境内非深户", 2, "港澳居民", 3, "台湾居民", 4, "外籍人士", "其它"));
			tableContent += row.format(Utils.decode(pageObj.bidders[0].is_oversea, 2, "港澳身份证号", 3, "台胞证号", 4, "护照号", 5, "证件号", "身份证号"), pageObj.bidders[0].certificate_no);
		}
		tableContent += row.format("移动电话", pageObj.bidders[0].mobile);
		if (pageObj.bidders[0].email)
			tableContent += row.format("电子邮箱", pageObj.bidders[0].email);
		if (pageObj.bidders[0].address)
			tableContent += row.format("通讯地址", pageObj.bidders[0].address);
	}
	$("#summaryTitle").html("以下内容是您注册的资料汇总信息" + (pageObj.getReadonly() ? "" : "，请认真检查无误后提交给工作人员审核，提交后不能再修改") + "：");
	$("#submitSummary").html(tableContent.replaceAll("null", ""));
}

//---------------------------------
//初始化数据
//---------------------------------
pageObj.initData = function () {
	if (!pageObj.id) {
		pageObj.clearBidderData();
		return;
	}
	var cmd = new Command();
	cmd.module = "trademan";
	cmd.service = "Bidder";
	cmd.method = "getBidderData";
	cmd.returnUnion = 1;
	cmd.id = pageObj.id;
	cmd.u = pageObj.getUserId();
	cmd.success = function(data) {
		if (data.state == 1 && data.totalRowCount > 0) {
			pageObj.bidderType = Utils.getRecordValue(data, 0, 'bidder_type');
			pageObj.changeBidderType();
			pageObj.status = Utils.getRecordValue(data, 0, 'status');
			pageObj.isCompany = Utils.getRecordValue(data, 0, 'is_company');
			pageObj.isUnion = Utils.getRecordValue(data, 0, 'is_union');
			if (!pageObj.isUnion)
				pageObj.isUnion = 0;
			pageObj.userId = Utils.getRecordValue(data, 0, 'user_id');
			pageObj.cakeyId = Utils.getRecordValue(data, 0, 'cakey_id');
			//用户信息
			$("#txtUserName").val(Utils.getRecordValue(data, 0, 'user_name'));
			$("#txtPassword").val("");
			$("#txtConfirmPassword").val("");
			$("#imgUserPasswordOk").hide();
			$("#lblStatus").html(Utils.decode(pageObj.status, 5, "审核中", 1, "已通过", comObj.bidderStatus[pageObj.status]));
			$("#lblStatusPrompt").hide();
			if (pageObj.status == 6 || pageObj.status == 0) {
				if (pageObj.status == 6) {
					$("#lblStatus").addClass("c6");
					$("#confirmFail").show();
				}
				$("#lblStatusPrompt").html("<请及时完善注册资料及上传材料后提交审核。>");
				$("#lblStatusPrompt").show();
			} else {
				$("#lblStatus").addClass(Utils.decode(pageObj.status, 0, "c3", 1, "c4", 5, "c7", "c3"));
			}
			var confirmDate = Utils.getRecordValue(data, 0, 'confirm_date');
			if (confirmDate)
				confirmDate = confirmDate.substr(0, 10); 
			$("#lblConfirmDate").html(confirmDate);
			$("#lblConfirmOpinion").html(Utils.getRecordValue(data, 0, 'confirm_opinion'));
			pageObj.bidders = new Array();
			if (pageObj.isUnion == 0) {
				var bidder = {};
				bidder.id = Utils.getRecordValue(data, 0, 'id');
				bidder.name = Utils.getRecordValue(data, 0, 'name');
				bidder.mobile = Utils.getRecordValue(data, 0, 'mobile');
				bidder.email = Utils.getRecordValue(data, 0, 'email');
				bidder.is_company = Utils.getRecordValue(data, 0, 'is_company');
				if (bidder.is_company == 1 || pageObj.bidderType == 3) {
					//企业信息
					bidder.is_oversea = 0;
					bidder.contact = Utils.getRecordValue(data, 0, 'contact');
					bidder.certificate_no = Utils.getRecordValue(data, 0, 'certificate_no');
					bidder.reg_no = Utils.getRecordValue(data, 0, 'reg_no');
					bidder.reg_address = Utils.getRecordValue(data, 0, 'reg_address');
					bidder.reg_capital = Utils.getRecordValue(data, 0, 'reg_capital');
					bidder.reg_type = Utils.getRecordValue(data, 0, 'reg_type');
					bidder.reg_area = Utils.getRecordValue(data, 0, 'reg_area');
					bidder.reg_corporation = Utils.getRecordValue(data, 0, 'reg_corporation');
					bidder.reg_corporation_idno = Utils.getRecordValue(data, 0, 'reg_corporation_idno');
					bidder.tel = Utils.getRecordValue(data, 0, 'tel');
					bidder.fax = Utils.getRecordValue(data, 0, 'fax');
				} else {
					//个人信息
					bidder.is_company = 0;
					bidder.is_oversea = Utils.getRecordValue(data, 0, 'is_oversea');
					bidder.certificate_no = Utils.getRecordValue(data, 0, 'certificate_no');
				}
				bidder.address = Utils.getRecordValue(data, 0, 'address');
				bidder.canton_id = Utils.getRecordValue(data, 0, 'canton_id');
				bidder.canton_name = Utils.getRecordValue(data, 0, 'canton_full_name');
				//bidder.post_code = Utils.getRecordValue(data, 0, 'post_code');
				bidder.__type = 0;
				pageObj.bidders.push(bidder);
			} else {
				pageObj.isCompany = 0;
				var unionData = data.unionData;
				if (unionData) {
					for(var i = 0; i < unionData.totalRowCount; i++) {
						var bidder = {};
						bidder.id = Utils.getRecordValue(unionData, i, 'id');
						bidder.name = Utils.getRecordValue(unionData, i, 'name');
						bidder.mobile = Utils.getRecordValue(unionData, i, 'mobile');
						bidder.email = Utils.getRecordValue(unionData, i, 'email');
						bidder.address = Utils.getRecordValue(unionData, i, 'address');
						bidder.canton_id = Utils.getRecordValue(data, i, 'canton_id');
						bidder.canton_name = Utils.getRecordValue(data, i, 'canton_full_name');
						//bidder.post_code = Utils.getRecordValue(unionData, i, 'post_code');
						bidder.is_company = Utils.getRecordValue(unionData, i, 'is_company');
						if (bidder.is_company == 1 || pageObj.bidderType == 3) {
							//企业信息
							bidder.is_oversea = 0;
							bidder.contact = Utils.getRecordValue(unionData, i, 'contact');
							bidder.certificate_no = Utils.getRecordValue(unionData, i, 'certificate_no');
							bidder.reg_no = Utils.getRecordValue(unionData, i, 'reg_no');
							bidder.reg_address = Utils.getRecordValue(unionData, i, 'reg_address');
							bidder.reg_capital = Utils.getRecordValue(unionData, i, 'reg_capital');
							bidder.reg_type = Utils.getRecordValue(unionData, i, 'reg_type');
							bidder.reg_area = Utils.getRecordValue(unionData, i, 'reg_area');
							bidder.reg_corporation = Utils.getRecordValue(unionData, i, 'reg_corporation');
							bidder.reg_corporation_idno = Utils.getRecordValue(unionData, i, 'reg_corporation_idno');
							bidder.tel = Utils.getRecordValue(unionData, i, 'tel');
							bidder.fax = Utils.getRecordValue(unionData, i, 'fax');
						} else {
							//个人信息
							bidder.is_company = 0;
							bidder.is_oversea = 0;
							bidder.certificate_no = Utils.getRecordValue(unionData, i, 'certificate_no');
						}
						bidder.__type = 1;
						pageObj.bidders.push(bidder);
					}
				}
			}
			//alert(JSON.stringify(pageObj.bidders));
			//保存一份原始竞买人数据
			pageObj.originalData = {};
			pageObj.originalData.isCompany = pageObj.isCompany;
			pageObj.originalData.isUnion = pageObj.isUnion;
			var originalBidders = new Array();
			for(var i = 0; i < pageObj.bidders.length; i++) {
				var originalBidder = cloneObj(pageObj.bidders[i]);
				originalBidders.push(originalBidder);
			}
			pageObj.originalData.bidders = originalBidders;
			//已存在的竞买人不允许部分操作begin
			//联合竞买人不能再添加新的联合竞买人及删除已有的联合竞买人，如果允许则必须提交后才能再处理附件
			$("#pnlAddUnionBidder").hide();
			//不能再选择竞买人类型
			pageObj.deleteStep("stepBidderType");
			//已存在的竞买人不允许部分操作end
			if (pageObj.isUnion == 1) {
				pageObj.initUnionBidderNavi();
				pageObj.initAttachOwnerNavi();
			}
			pageObj.initControl();
			pageObj.selectUnionBidder(0, false);
			//如果附件已打开
			pageObj.clearAttachControl();
			//设置附件权限等
			if (pageObj.getReadonly()) {
				pageObj.attachDtlUpload = false;
				pageObj.attachDtlDownload = true;
				pageObj.attachDtlDelete = false;
		    } else {
		    	pageObj.attachDtlUpload = true;
				pageObj.attachDtlDownload = true;
				pageObj.attachDtlDelete = true;
		    }
		    if (pageObj.id) {
		    	$("#registTitle").removeAttr("class");
				if (pageObj.bidderType == 1)
					$("#registTitle").addClass("c2");
				pageObj.gotoStep("stepConfirm");
		      	if (pageObj.status == 6) {
		      		var confirmOpinion = $("#lblConfirmOpinion").html();
		      		//请查看bidderEdit.html中的“主要原因”项内容
		      		if (confirmOpinion.indexOf("不正确") >= 0) {
		      			$("#gotoPrefix").show();
		      			$("#gotoModifyBidderData").show();
		      			$("#gotoUploadAttach").hide();
		      		} else if (confirmOpinion.indexOf("不齐全") >= 0) {
		      			$("#gotoPrefix").show();
		      			$("#gotoModifyBidderData").hide();
		      			$("#gotoUploadAttach").show();
		      		} else if (confirmOpinion.indexOf("不匹配") >= 0) {
		      			$("#gotoPrefix").show();
		      			$("#gotoModifyBidderData").show();
		      			$("#gotoUploadAttach").show();
		      		}
		      	} else
			    	pageObj.gotoStep("stepBidder");
			} else
		    	pageObj.gotoStep(0);
		} else
			pageObj.clearBidderData();
	};
	cmd.execute();
}

//---------------------------------
//保存数据
//---------------------------------
pageObj.saveData = function () {
	if (!pageObj.checkData())
		return;
	if (pageObj.checkDataModified()) {
		$("#txtSecurityCode").val("");
		pageObj.refreshSecurityCode();
		DialogOpen({ height: 150, width: 330, title: '保存数据', target: $("#pnlSubmitData"),
			buttons: [{ text: '确定', onclick: function (item, dialog) {
					if (!pageObj.checkSecurityCode()) {
						DialogError('验证码不正确', window);
						return;
					}
					dialog.hide();
					pageObj.doSaveData();
				}
			},
			{ text: '取消', onclick: function (item, dialog) {
					dialog.hide();
				}
			}]
		}, window);
	}
	return;
}

//---------------------------------
//实际保存数据方法
//---------------------------------
pageObj.doSaveData = function (showPrompt) {
	var cmd = new Command();
	cmd.module = "trademan";
	cmd.service = "Bidder";
	cmd.method = "updateBidderData";
	cmd.updateMode = "updateByBidder";
	cmd.u = pageObj.getUserId();
	cmd.id = pageObj.id;
	cmd.bidder_type = pageObj.bidderType;
	cmd.is_union = pageObj.isUnion;
	cmd.status=5;
	if (pageObj.isUnion == 1) {
		var unionNames = null;
		var unions = new Array();
		for(var i = 0; i < pageObj.bidders.length; i++) {
			var bidder = pageObj.bidders[i];
			if (i == 0) {
				//将第一个联合竞买人信息保存给trans_bidder，注意会覆盖部分共同数据，请写在后面（如cmd.id）
				for(var key in bidder)
					if (!(key in cmd))
						cmd[key] = bidder[key];
			}
			bidder.turn = i;
			unions.push(bidder);
			if (unionNames)
				unionNames += ";" + bidder.name;
			else
				unionNames = bidder.name;
		}
		cmd.unions = JSON.stringify(unions);
		cmd.unionNames = unionNames;
		var bidderName = unionNames.replaceAll(";", ",");
		cmd.name = bidderName.ansiLength() > 200 ? bidderName.ansiSubstr(0, 200) : bidderName;
		cmd.is_company = pageObj.bidders[0].is_company;
	} else {
		//独立竞买人
		var bidder = pageObj.bidders[0];
		for(var key in bidder)
			if (!(key in cmd))
				cmd[key] = bidder[key];
	}
	//用户信息
	if (pageObj.requiredUser) {
		cmd.user_id = pageObj.userId;
		cmd.cakey_id = pageObj.cakeyId;
		if (pageObj.id) {
			if ($("#txtPassword").val() != "")
				cmd.user_password = $.md5($('#txtPassword').val());
		} else {
			cmd.user_name = $("#txtUserName").val();
			cmd.user_password = $.md5($('#txtPassword').val());
			if (pageObj.bidderType != 1) {
				var nextYear = new Date();
				nextYear = Date.dateAdd(nextYear, 1, "yy");
				cmd.valid_date = nextYear.toString().substr(0, 10);
			}
		}
	} else if (!pageObj.id)
		cmd.user_name = null;
	var result = false;
	cmd.success = function (data) {
		if(data.state == 1) {
			pageObj.id = data.id;
			pageObj.userId = data.userId;
			pageObj.cakeyId = data.cakeyId;
			pageObj.initData();
			pageObj.dataModifyedChecker.initFileds();
			if (showPrompt != false)
				DialogAlert('保存注册信息成功。', window);
			result = true;
			return true;
		} else {
			DialogError('注册信息保存失败，' +(data.message ? '错误原因：' + data.message : '未知错误原因。'), window);
			return false;
		}
	};
	cmd.execute();
	return result;
}

//---------------------------------
//提交审核
//---------------------------------
pageObj.submitData = function () {
	//提交审核时仍然检查一次数据
	if (!pageObj.checkBidder(true))
		return;
	//检查必须的附件材料是否全部提交
	if (!pageObj.checkRequiredAttach(true)) {
		pageObj.gotoStep("stepAttach");
		return;
	}
	DialogConfirm('确定提交审核您注册的信息吗？<br/>注册的内容一旦提交后<span class="c6">不能再修改及提交材料</span>。', function (yes) {
		if (yes)
			pageObj.doSubmitData();
	}, window);
}

pageObj.doSubmitData = function () {
	var cmd = new Command();
	cmd.module = "trademan";
	cmd.service = "Bidder";
	cmd.method = "updateBidderData";
	cmd.updateMode = "updateByBidder";
	cmd.u = pageObj.getUserId();
	cmd.id = pageObj.id;
	cmd.status = 5;
	var result = false;
	cmd.success = function (data) {
		if (data.state == 1) {
			pageObj.initData();
			pageObj.dataModifyedChecker.initFileds();
			if (pageObj.getActiveStepId() == "stepSubmit")
				pageObj.gotoStep("stepSubmit");
			DialogAlert('注册信息提交审核成功，请等待工作人员审核。', window);
			result = true;
		} else {
			DialogError('注册信息提交审核失败，' +(data.message ? '错误原因：' + data.message : '未知错误原因。'), window);
			return false;
		}
	}
	cmd.execute();
	return result;
}

//---------------------------------
//验证用户名是否已存在，返回true表示已存在重复用户名
//---------------------------------
pageObj.checkUserNameExists = function (showErrorPrompt) {
	var result = false;
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.method = "checkUserExists";
	cmd.user_name = $("#txtUserName").val();
	cmd.id = pageObj.userId;
	cmd.u = pageObj.getUserId();
	cmd.success = function (data) {
		if (data.state == 1) {
			if (Utils.getRecordValue(data, 0, 'row_count') > 0) {
				result = true;
				if (showErrorPrompt == true)
					DialogError(data.message ? data.message : '用户名已存在。', window);
			}
		}
	};
	cmd.execute();
	return result;
}

//---------------------------------
//刷新验证码
//---------------------------------
pageObj.refreshSecurityCode = function () {
	var times = (new Date()).valueOf();
	$("#codeImage").attr("src", approot + "/securityCode?module=base&service=SecurityCode&method=getCode&times=" + times);
}

//---------------------------------
//验证验证码
//---------------------------------
pageObj.checkSecurityCode = function (showErrorPrompt) {
	var result = false;
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.method = "checkSecurityCode";
	cmd.newCode = $("#txtSecurityCode").val();
	cmd.u = pageObj.getUserId();
	cmd.success = function (data) {
		if(data.state == 1) {
			result = true;
		} else if (showErrorPrompt == true) {
			DialogError(data.message ? data.message : '验证码错误。', window);
		}
	};
	cmd.execute();
	return result;
}

pageObj.passwordKeyUp = function () {
	var srcElement = event.srcElement;
	if (!srcElement)
		return;
	var checkElement = null;
	if ($(srcElement).attr("id") == "txtPassword")
		checkElement = $("#txtConfirmPassword");
	else if ($(srcElement).attr("id") == "txtConfirmPassword")
		checkElement = $("#txtPassword");
	if ($(srcElement).val() && checkElement.val()) {
		if ($(srcElement).val() == checkElement.val())
			$("#imgUserPasswordOk").show();
		else
			$("#imgUserPasswordOk").hide();
	} else
		$("#imgUserPasswordOk").hide();
}

//---------------------------------
//是否企业变更事件
//---------------------------------
pageObj.changeBidderType = function () {
	if (pageObj.bidderType == 1) {
		$("#lblAddressRequired").show();
		$("#lblRegAddressRequired").hide();
		$("#addressRow").insertAfter($("#mobileRow"));
		pageObj.allowModifySome = true;
	} else {
		$("#lblAddressRequired").hide();
		$("#lblRegAddressRequired").show();
		$("#addressRow").insertBefore($("#emailRow"));
		pageObj.allowModifySome = false;
	}
	if (pageObj.bidderType == 3) {
		$("#imgUserType").attr("src", "./images/base/user_type1.jpg");
		$("#isCompany0").attr("title", "土地储备机构");
		$("#isCompany0").attr("coords", "2,1,208,59");
		$("#isCompany1").attr("title", "企业");
		$("#isCompany1").attr("coords", "240,-7,393,60");
	} else {
		$("#imgUserType").attr("src", "./images/base/user_type.jpg");
		$("#isCompany0").attr("title", "个人竞买人");
		$("#isCompany0").attr("coords", "1,2,176,59");
		$("#isCompany1").attr("title", "企业竞买人");
		$("#isCompany2").attr("coords", "207,1,383,60");
	}
}

//---------------------------------
//是否企业变更事件
//---------------------------------
pageObj.changeIsCompany = function () {
	var isCompany = $("input[name='isCompany']:checked").val();
	if (isCompany == 1 || pageObj.bidderType == 3) {		
		$("#pnlPersonal").hide();
		$("#pnlCompany0").show();
		$("#pnlCompany1").show();
		$("#lblName").html("名称");
//		if (event) {
//			//点击更换是否企业时清空内容
//			$("#txtContact").val("");
//			$("#txtRegCorporation").val("");
//			$("#txtRegCorporationIdno").val("");
//			$("#txtRegNo").val("");
//			$("#txtCompanyCode").val("");
//			$("#txtRegAddress").val("");
//			$("#txtRegCapital").val("");
//			$("#txtRegArea").val("");
//			$("#txtRegType").val("");
//			$("#txtTel").val("");
//			$("#txtFax").val("");
//		}
	} else {
		$("#pnlPersonal").show();
		$("#pnlCompany0").hide();
		$("#pnlCompany1").hide();
		$("#lblName").html("姓名");
//		if (event) {
//			$("input[name='isOversea'][value='0']").attr("checked", true);
//			$("#txtCertificateNo").val("");
//		}
	}
}

//---------------------------------
//个人户籍变更事件
//---------------------------------
pageObj.changeIsOversea = function () {
	//广州无所在区域，改为行政区
	return;
	var isOversea = $("input[name='isOversea']:checked").val();
	var certificateNoLabel = null;
	if (isOversea == 0 || isOversea == 1)
		certificateNoLabel = "身份证号";
	else if (isOversea == 2)
		certificateNoLabel = "港澳身份证号";
	else if (isOversea == 3)
		certificateNoLabel = "台胞证号";
	else if (isOversea == 4)
		certificateNoLabel = "护照号";
	else
		certificateNoLabel = "证件号";
	$("#lblCertificateNo").html(certificateNoLabel);
}

//---------------------------------
//是否企业下拉框变更事件
//---------------------------------
pageObj.selectCanton = function () {
	var cantonObj = comObj.cantonSelect();
	if (cantonObj) {
		$("#txtCantonName").val(cantonObj.fullName);
		$("#txtCantonId").val(cantonObj.id);
	}
}

//---------------------------------
//加载附件数据
//---------------------------------
pageObj.loadAttachData = function (ownerId, ownerTableName, templetNo) {
	if (ownerId) {
		$("#unsavedAttachPrompt").hide();
		$("#attachPanel").show();
		var initWidth = pageObj.isUnion == 1 ? 575 : 709;
		var initHeight = 312;
		var grant = {add: pageObj.attachAdd ? 2 : 0,
			edit: pageObj.attachEdit ? 2 : 0,
			del: pageObj.attachDelete ? 2 : 0,
			addDtl: pageObj.attachDtlUpload ? 2 : 0,
			editDtl: pageObj.attachDtlUpload && pageObj.attachDtlDelete ? 2 : 0,
			delDtl: pageObj.attachDtlDelete ? 2 : 0,
			uploadFile: pageObj.attachDtlUpload ? 2 : 1,
			downloadFile: pageObj.attachDtlDownload ? 2 : 1,
			delFile: pageObj.attachDtlDelete ? 2 : 1};
		var owners = new Array();
		var owner = {};
		owner.id = ownerId;
		owner.name = "竞买人";
		owner.title = "竞买人村料";
		owner.tableName = ownerTableName;
		owner.templetNo = templetNo;
		owner.clearTemplet = 1;
		owner.grant = grant;
		owners.push(owner);
		var url = approot + '/sysman/attachList.html?u=' + pageObj.getUserId()
			+ '&owners=' + encodeURIComponent(JSON.stringify(owners))
			//+ '&ltitle=' + (pageObj.isUnion == 1 ? '' : encodeURIComponent("材料分类")) + '&rtitle=' + encodeURIComponent("材料列表")
			+ '&ltitle=' + encodeURIComponent("材料分类") + '&rtitle=' + encodeURIComponent("材料列表")
			+ '&attachLabel=' + encodeURIComponent('材料')
			+ '&autoRename=1';
		if (pageObj.isUnion == 1)		
			url += "&lwidth=33%&rwidth=66%";
		var attachFrame = $('#attachFrame');
		if (attachFrame.length == 0) {
			$('#attachPanel').html('<iframe id="attachFrame" src="'
					+ url + '" scrolling="no" style="height:' + initHeight
					+ 'px; width:' + initWidth + 'px; border-style: none"></iframe>');
		} else {
			attachFrame.attr('src', url);
		}
		pageObj.attachDataLoaded = true;
	} else {
		$("#attachPanel").hide();
		$("#unsavedAttachPrompt").show();
	}
}

pageObj.verify = function (type, s) {
	return true;
	if (!type || !s)
		return false;
	var patrn = null;
	type = type.toLowerCase();
	if (type == "mobile")//移动电话
		patrn = /^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/; 
	else if (type == "tel" || type == "fax")//电话、传真
		patrn = /^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/;
	else if (type == "email")//电子邮箱
		patrn = /^((\w)|[-]|[.])+@(((\w)|[-])+[.])+[a-z]{2,4}$/;
	else if (type == "postcode")//邮编
		patrn = /^[a-z0-9 ]{3,12}$/;
	else if (type == "date")//"日期（格式：yyyy-mm-dd）
		patrn = /^[12]{1}(\d){3}[-][01]?(\d){1}[-][0123]?(\d){1}$/;
	else if (type == "int" || type == "integer")//整数 /^(-|\+)?\d+$/
		patrn = /^[1-9]{1}[0-9]{0,6}$/;
	else if (type == "idno" || type == "idcard") {//身份证
		if (s.length == 15)
			patrn = /^(\d{6})()?(\d{2})(\d{2})(\d{2})(\d{3})$/;
		else if (s.length == 18)
			patrn = /^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\d)$/;
		else
			return false;
	} else if (type == 'ip')//ip
		patrn = /^[0-9.]{1,20}$/;
	if (!patrn.exec(s))
		return false;
	return true;
}

pageObj.beforeUnload = function () {
	//window.onbeforeunload
	//$(window).bind('beforeunload', pageObj.beforeUnload);
	if (pageObj.checkDataModified())
		return "修改的数据未提交，确定退出？";
	if (pageObj.id && (pageObj.newBidder || (pageObj.attachMode && pageObj.status == 0)))
		return "修改的数据已提交，请确定是否需要上传新材料，退出后再进入只能上传材料而不能删除之前错误的材料。确定退出？";
	return;
}

pageObj.closeDialog = function () {
	$(window).unbind('beforeunload');
	window.close();
}

pageObj.changeAgreeNoticeContent = function () {
	var checked = $("#chkAlreadyRead").attr("checked") ? true : false;
	if (checked) {
		$("#nextStep1").attr("href", "javascript:pageObj.nextStep()");
		$("#nextStep1").removeClass("gray_btn");
		$("#nextStep1").addClass("next_btn");
	} else {
		$("#nextStep1").removeAttr("href");
		$("#nextStep1").removeClass("next_btn");
		$("#nextStep1").addClass("gray_btn");
	}
}

pageObj.test = function () {
	throw "错误";
	alert("正确");
}

//---------------------------------
//页面初始化
//---------------------------------
pageObj.initHtml = function() {
	$("#test").click(pageObj.test);
	//$("#test").show();
	pageObj.attachAdd = false;
	pageObj.attachEdit = false;
	pageObj.attachDelete = false;
	pageObj.id = Utils.getPageValue("id");
	if (pageObj.id) {
		pageObj.newBidder = false;
		pageObj.attachDtlUpload = false;
		pageObj.attachDtlDownload = true;
		pageObj.attachDtlDelete = false;
	} else {
		pageObj.newBidder = true;
		pageObj.attachDtlUpload = true;
		pageObj.attachDtlDownload = true;
		pageObj.attachDtlDelete = true;
	}
	var bidderType = Utils.getPageValue("bidderType");
	bidderType = parseInt(bidderType);
	if (bidderType == 0 || bidderType == 1 || bidderType == 2 || bidderType == 3) {
		pageObj.bidderType = bidderType;
		pageObj.initBidderType = bidderType;
	}
//	if (pageObj.initBidderType == 1)
//		$("#registTitle").html("拍卖竞买人注册");
//	else
//		$("#registTitle").html("土地竞买人注册");
	$("#registTitle").removeAttr("class");
	if (pageObj.initBidderType == 1)
		$("#registTitle").addClass("c2");
	//测试代码
//	pageObj.initBidderType = 1;
//	pageObj.bidderType = 1;
	pageObj.deleteStep("stepCondition");
	if (!pageObj.requiredUser) {
		pageObj.deleteStep("stepSubmit");
		pageObj.deleteStep("stepUser");
		pageObj.deleteStep("stepAttach");
		pageObj.deleteStep("stepConfirm");
		$("#saveData").show();
	}
	pageObj.changeBidderType();
	$("#priorStep").attr("href", "javascript:pageObj.priorStep()");
	$("#nextStep").attr("href", "javascript:pageObj.nextStep()");
	$("#saveData").attr("href", "javascript:pageObj.saveData()");
	$("#submitData").attr("href", "javascript:pageObj.submitData()");
	$("#closeDialog").attr("href", "javascript:pageObj.closeDialog()");
	//$("#nextStep1").attr("href", "javascript:pageObj.nextStep()");
	$("#chkAlreadyRead").click(pageObj.changeAgreeNoticeContent);
	pageObj.changeAgreeNoticeContent();
	$("#closeDialog1").attr("href", "javascript:pageObj.closeDialog()");
	$("#txtPassword").keyup(pageObj.passwordKeyUp);
	$("#txtConfirmPassword").keyup(pageObj.passwordKeyUp);
	$("#isCompany0").attr("href", "javascript:pageObj.selectBidderType(0)");
	$("#isCompany1").attr("href", "javascript:pageObj.selectBidderType(1)");
	$("#isUnion1").attr("href", "javascript:pageObj.selectBidderType(2)");
	$('#rbtnIsCompany0').click(pageObj.changeIsCompany);
    $('#rbtnIsCompany1').click(pageObj.changeIsCompany);
    $("#rbtnIsOversea0").click(pageObj.changeIsOversea);
    $("#rbtnIsOversea1").click(pageObj.changeIsOversea);
    $("#rbtnIsOversea2").click(pageObj.changeIsOversea);
    $("#rbtnIsOversea3").click(pageObj.changeIsOversea);
    $("#rbtnIsOversea4").click(pageObj.changeIsOversea);
    $("#rbtnIsOversea5").click(pageObj.changeIsOversea);
    $("#btnSelectCanton").click(pageObj.selectCanton);
    $("#unionBidder0").attr("href", "javascript:pageObj.selectUnionBidder(0)");
    $("#unionBidder1").attr("href", "javascript:pageObj.selectUnionBidder(1)");
    $("#addUnionBidder").attr("href", "javascript:pageObj.addUnionBidder()");
    $("#txtName").change(pageObj.changeBidderName);
    $("#gotoModifyBidderData").click(pageObj.gotoModifyBidderDataStep);
    $("#gotoUploadAttach").click(pageObj.gotoUploadAttachStep);
    $("#refreshSecurityCode").attr("href", "javascript:pageObj.refreshSecurityCode()");
    $("#attchHelp").ligerTip({content: "以下材料为必需材料（<span class='fb'>扫描件或者照片</span>）："
    	+ "<br/><span class='fb'>个人</span>：身份证正反面；"
    	+ "<br/><span class='fb'>企业</span>：企业营业执照、法定代表人证明书、法定代表人身份证；"
    	+ "<br/><span class='fb'>联合</span>：各联合竞买人按个人/企业自行上传各自的相关材料，并且可在“公共材料”中上传所有联合竞买人公共材料。",
    	distanceX: 20, distanceY: -100, width: 300, auto: true});
    //$(window).beforeunload(pageObj.beforeUnload);
    //$(window).bind('beforeunload', pageObj.beforeUnload);
    pageObj.initData();
    pageObj.dataModifyedChecker = new FieldModifiedChecker($("#bidderEditor")[0], true);
   	$("#stepNotice")[0].scrollTop = 0;
}

$(document).ready(pageObj.initHtml);