//---------------------------------
//页页对象
//---------------------------------
var pageObj = {};
pageObj.caManager = false;
//---------------------------------
//表格对象
//---------------------------------
pageObj.tabManager = null;
//---------------------------------
//数据加载标识
//---------------------------------
pageObj.bidderRelLoaded = false;
pageObj.attachDataLoaded = false;
//---------------------------------
//数据只读。0编辑，1只读
//---------------------------------
pageObj.readonly = 0;
pageObj.userInfo = getUserInfoObj();
//---------------------------------
//数据修改检查对象
//---------------------------------
pageObj.checkDataModified = null;
pageObj.okIcon = approot + '/base/skins/default/images/icons/ok.gif';
//---------------------------------
//缓存部分数据
//---------------------------------
pageObj.status = 0;
pageObj.validDate = null;
pageObj.valid = true;
pageObj.bidderType = 0;
pageObj.isCompany = 1;
pageObj.goodsType=null;
//---------------------------------
//标识当前用户是否是领导者(原来有部分信息需要部长才能完成)
//---------------------------------
pageObj.isManagerUser = false;
//---------------------------------
//是否必须用户
//---------------------------------
pageObj.requiredUser = false;

//---------------------------------
//初始化数据
//---------------------------------
pageObj.initData = function () {
	$("#btnFreeze").hide();
	$("#btnCheck").hide();
	$("#btnLogoff").hide();
	$("#btnActive").hide();
	$("#imgMobileOk").hide();
	$("#imgTelOk").hide();
	$("#imgUserPasswordOk").hide();
	if (!pageObj.id || pageObj.id.toLowerCase() == "null") {
		pageObj.id = "";
		pageObj.userId = "";
		pageObj.cakeyId = "";
		pageObj.status = 0;
		pageObj.bidderType = $("#cboBidderType").val();
		if (pageObj.bidderType == 1)
			$("#cboIsCompany").val(0);
		else
			$("#cboIsCompany").val(1);
		pageObj.isCompany = $("#cboIsCompany").val();
		var nextYear = new Date();
		nextYear = Date.dateAdd(nextYear, 1, "yy");
		pageObj.validDate = nextYear.toString().substr(0, 10);
		pageObj.valid = true;
		$("#txtValidDate").val(pageObj.validDate);
		pageObj.initControl();
		return;
	}
	var cmd = new Command();
	cmd.module = "trademan";
	cmd.service = "Bidder";
	cmd.method = "getBidderData";
	cmd.dataSetNo = '';
	cmd.id = pageObj.id;
	cmd.success = function(data) {
		pageObj.userId = Utils.getRecordValue(data, 0, 'user_id');
		pageObj.cakeyId = Utils.getRecordValue(data, 0, 'cakey_id');
		pageObj.bidderType = Utils.getRecordValue(data, 0, 'bidder_type');
		pageObj.isCompany = Utils.getRecordValue(data, 0, 'is_company');
		pageObj.validDate = Utils.getRecordValue(data, 0, 'valid_date');
		pageObj.status = Utils.getRecordValue(data, 0, 'status');
		if (pageObj.validDate) {
			var now = new Date();
			now = now.toString().substr(0, 10);
			pageObj.valid = now < pageObj.validDate;
		} else
			pageObj.valid = true;
		$("#cboBidderType").val(pageObj.bidderType);
		$("#cboIsCompany").val(pageObj.isCompany);
		$("#txtName").val(Utils.getRecordValue(data, 0, 'name'));
		$("#txtCantonName").val(Utils.getRecordValue(data, 0, 'canton_full_name'));
		$("#txtCantonId").val(Utils.getRecordValue(data, 0, 'canton_id'));
		$("#txtMobile").val(Utils.getRecordValue(data, 0, 'mobile'));
		$("#txtCertificateNo").val(Utils.getRecordValue(data, 0, 'certificate_no'));
		$("#txtTel").val(Utils.getRecordValue(data, 0, 'tel'));
		$("#txtFax").val(Utils.getRecordValue(data, 0, 'fax'));
		$("#txtEmail").val(Utils.getRecordValue(data, 0, 'email'));
		$("#txtAddress").val(Utils.getRecordValue(data, 0, 'address'));
		$("#txtPostCode").val(Utils.getRecordValue(data, 0, 'post_code'));
		$("#txtContact").val(Utils.getRecordValue(data, 0, 'contact'));
		$("#txtCompanyCode").val(Utils.getRecordValue(data, 0, 'certificate_no'));
		$("#txtRegNo").val(Utils.getRecordValue(data, 0, 'reg_no'));
		$("#txtRegAddress").val(Utils.getRecordValue(data, 0, 'reg_address'));
		$("#txtRegCapital").val(Utils.getRecordValue(data, 0, 'reg_capital'));
		$("#txtRegType").val(Utils.getRecordValue(data, 0, 'reg_type'));
		$("#txtRegArea").val(Utils.getRecordValue(data, 0, 'reg_area'));
		$("#txtCorporation").val(Utils.getRecordValue(data, 0, 'reg_corporation'));
		$("#txtCorporationIdno").val(Utils.getRecordValue(data, 0, 'reg_corporation_idno'));
		//$("#cboIsOversea").val(Utils.getRecordValue(data, 0, 'is_oversea'));
		$("#txtCakey").val(Utils.getRecordValue(data, 0, 'cakey'));
		$("#txtUserName").val(Utils.getRecordValue(data, 0, 'user_name'));
		$("#imgUserPasswordOk").hide();
		if (pageObj.validDate)
			$("#txtValidDate").val(pageObj.validDate.substr(0, 10));
		$("#txtRemark").val(Utils.getRecordValue(data, 0, 'remark'));
		$("#lblConfirmOpinion").html(Utils.getRecordValue(data, 0, 'confirm_opinion'));
		var confirmedDate = Utils.getRecordValue(data, 0, "confirm_date");
		if (confirmedDate)
			confirmedDate = confirmedDate.substr(0, 10);
		$("#lblConfirmDate").html(confirmedDate);
		$("#txtAgentName").val(data.txtAgentName);
		$("#txtAgentNo").val(data.txtAgentNo);
		$("#txtAgentPhone").val(data.txtAgentPhone);
		pageObj.initControl();
	};
	cmd.execute();
}

//---------------------------------
//竞买人相关信息标签TAB初始化
//---------------------------------
pageObj.initBidderRelTab = function() {
	if (!pageObj.bidderRelLoaded) {
		pageObj.tabManager = $("#tabBidderRel").ligerTab({
			contextmenu: false,
			onAfterSelectTabItem: pageObj.afterSelectTabItem
		});
		pageObj.bidderRelLoaded = true;
	}
}

pageObj.checkValidityOnlyNumber = function () {
	if(((event.keyCode >= 48 && event.keyCode <= 57) || event.keyCode == 8 || (event.keyCode >= 96 && event.keyCode <= 105)
		|| event.keyCode == 46 || event.keyCode == 37 || event.keyCode == 39 || event.keyCode == 190 || event.keyCode == 110) && !event.shiftKey) {
		event.returnValue = true;
	} else {
		event.returnValue = false;
	}
}

//---------------------------------
//是否企业下拉框变更事件
//---------------------------------
pageObj.changeIsCompany = function () {
	var bidderType = $("#cboBidderType").val();
	var isCompany = $("#cboIsCompany").val();
	if (isCompany == 0 && bidderType != 3) {
		$('#lblNameLabel').html("姓名");
		$("#companyEditor").hide();
		$("#personalEditor").show();
	} else {
		$('#lblNameLabel').html("名称");
		$("#companyEditor").show();
		$("#personalEditor").hide();
	}
	if(isCompany == 0 &&bidderType == 3){
		$("#fr").hide();
		$("#frnum").hide();
		$("#orgnum").hide();
		$("#num").hide();
	}
	if(isCompany == 1 &&bidderType == 3){
		$("#fr").show();
		$("#frnum").show();
		$("#orgnum").show();
		$("#num").show();
	}
	if (bidderType != pageObj.bidderType || isCompany != pageObj.isCompany) {
		$("#unsavedPrompt").html("当前竞买人数据部分修改必须保存后才能管理附件材料。");
		if (pageObj.activeTabId == "tabAttach") {
			$("#attachPanel").hide();
			$("#unsavedPrompt").show();
		}
	}
	Utils.autoIframeSize();
}

//---------------------------------
//是否企业下拉框变更事件
//---------------------------------
pageObj.selectCanton = function () {
	var cantonObj = comObj.cantonSelect('txtCantonId', 'txtCantonName');
	if (cantonObj) {
		$("#txtCantonName").val(cantonObj.fullName);
		$("#txtCantonId").val(cantonObj.id);
	}
}

//---------------------------------
//保存按钮事件
//---------------------------------
pageObj.saveData = function (activeSave) {
	var bidderType = $("#cboBidderType").val();
	var isCompany = $("#cboIsCompany").val();
	var bidderName = $("#txtName").val();
	if (!$("#txtName").val()){
		if(pageObj.goodsType == '501')
		DialogError('名称不能为空', window);
		else 
		DialogError((isCompany == 1 ? '名称' : '姓名') + '不能为空', window);
		return false;
	}
	if (!$("#txtCantonId").val()){
		DialogError('请选择行政区', window);
		return false;
	} 
//	if($("#txtAgentName").val()==""){
//		DialogError('代理人姓名不能为空', window);
//		return false;
//	}
//	if($("#txtAgentNo").val()==""){
//		DialogError('代理人身份证号码不能为空', window);
//		return false;
//	}
	if (!pageObj.id || !pageObj.caManager) {
		if (!$("#txtMobile").val()){
			DialogError('移动电话不能为空', window);
			return false;
		}
//		if (!$("#txtEmail").val()){
//			DialogError('电子邮箱不能为空', window);
//			return false;
//		}
//		if (!$("#txtAddress").val()){
//			DialogError('地址不能为空', window);
//			return false;
//		}
		if (isCompany == 1) {
			if (!$("#txtCorporation").val()) {
				DialogError('法人代表不能为空', window);
				return false;
			}
			if (!$("#txtCorporationIdno").val()) {
				DialogError('法人证件号码不能为空', window);
				return false;
			}
			if (!$("#txtCompanyCode").val()) {
				DialogError('组织机构代码不能为空', window);
				return false;
			}
			if (!$("#txtRegNo").val()) {
				DialogError('营业执照编号不能为空', window);
				return false;
			}
		} else {
			if ($("#txtCertificateNo").val() == ''&&Utils.getPageValue("goodsType")!='501'){
				DialogError('身份证号码不能为空', window);
				return false;
			}
		}
		if (pageObj.requiredUser)
			if (!$("#txtUserName").val()){
				DialogError('登录用户不能为空', window);
				return false;
			}
		if (!pageObj.id) {//新增用户必须输入密码
			if (pageObj.requiredUser)
				if (!$("#txtUserPassword").val()) {
					DialogError('登录用户密码不能为空', window);
					return false;
				}
		}
		if ($("#txtUserPassword").val() != $("#txtConfirmUserPassword").val()){
			DialogError('登录用户确认密码不一致', window);
			return false;
		}
	}
	if (pageObj.caManager) {
		if (!$("#txtCakey").val()){
			DialogError('CAKEY不能为空', window);
			return false;
		}
	}
	var cmd1 = new Command();
	cmd1.module = "sysman";
	cmd1.service = "User";
	cmd1.method = "checkBlackUserByName";
	cmd1.u = pageObj.u;
	cmd1.goodsType = pageObj.goodsType;
	cmd1.is_company = isCompany;
	cmd1.name = $("#txtName").val();
	cmd1.no = $("#txtCertificateNo").val();
	cmd1.success = function(data1) {
	if(data1.state!=1){
			DialogConfirm(data1.message+",是否继续保存？", function (yes) {
				if (yes) {
					var cmd = new Command();
					cmd.module = "trademan";
					cmd.service = "Bidder";
					cmd.method = "updateBidderData";
					cmd.updateMode = "update";
					cmd.id = pageObj.id;
					if (!pageObj.id)
						cmd.status = 5;
					cmd.bidder_type = bidderType;
					cmd.is_company = isCompany;
					cmd.name = bidderName;
					cmd.canton_id = $("#txtCantonId").val();
					cmd.mobile = $("#txtMobile").val();
					cmd.tel = $("#txtTel").val();
					if ((cmd.is_company ==1)||(cmd.is_company == 0&&cmd.bidder_type==3)) {
						cmd.certificate_no = $("#txtCompanyCode").val();
						cmd.reg_no = $("#txtRegNo").val();
						cmd.reg_address = $("#txtRegAddress").val();
						var regCapital = parseFloat($("#txtRegCapital").val());
						if (isNaN(regCapital))
							regCapital = 0;
						cmd.reg_capital = regCapital;
						cmd.reg_type = $("#txtRegType").val();
						cmd.reg_area = $("#txtRegArea").val();
						cmd.reg_corporation = $("#txtCorporation").val();
						cmd.reg_corporation_idno = $("#txtCorporationIdno").val();
					} else {
						cmd.certificate_no = $("#txtCertificateNo").val();
					}
					cmd.fax = $("#txtFax").val();
					cmd.email = $("#txtEmail").val();
					cmd.address = $("#txtAddress").val();
					cmd.post_code = $("#txtPostCode").val();
					cmd.contact = $("#txtContact").val();
					//cmd.is_oversea = $("#cboIsOversea").val();
					cmd.cakey = $("#txtCakey").val();
					cmd.valid_date = $("#txtValidDate").val();
					cmd.remark = $("#txtRemark").val();
					cmd.cakey = $("#txtCakey").val();
					cmd.user_name = $("#txtUserName").val();
					cmd.txtAgentName = $("#txtAgentName").val();
					cmd.txtAgentNo = $("#txtAgentNo").val();
					cmd.txtAgentPhone = $("#txtAgentPhone").val();
					if ($("#txtUserPassword").val() != "")
						cmd.user_password = $.md5($('#txtUserPassword').val());
					cmd.user_id = pageObj.userId;
					cmd.cakey_id = pageObj.cakeyId;
					if (pageObj.id && activeSave == true) {
						//审核操作时选择审核不通过时，仅保存数据。此时保存审核意见相关信息
						cmd.status = 6;
						cmd.confirm_opinion = $("#cboConfirmCause").val() != 3 ? ($("#cboConfirmCause").find("option:selected").text() + ($("#txtConfirmOpinion").val() ? "，" + $("#txtConfirmOpinion").val() : "。")) : $("#txtConfirmOpinion").val();
						cmd.confirm_user_id = pageObj.userInfo.userId;
					}
					cmd.success = function (data) {
						var state = data.state;
						if(state == '1') {
							pageObj.id = data.id;
							pageObj.userId = data.userId;
							pageObj.cakeyId = data.cakeyId;
							pageObj.initData();//重新刷新数据，获取数据库中的默认值
							pageObj.afterSelectTabItem('tabConfirm');
							pageObj.checkDataModified.initFileds();
							DialogAlert('保存竞买人成功完成', window);
							if (pageObj.checkParentObj()) {
								pageObj.dialogManager.parentPageObj.queryData(true);
							}
						} else {
							DialogError('保存竞买人失败' + (data.message ? ',错误原因：' + data.message : '。'), window);
							return false;
						}
					};
					cmd.execute();
				}
			});
			
		}else{
			var cmd = new Command();
			cmd.module = "trademan";
			cmd.service = "Bidder";
			cmd.method = "updateBidderData";
			cmd.updateMode = "update";
			cmd.id = pageObj.id;
			if (!pageObj.id)
				cmd.status = 5;
			cmd.bidder_type = bidderType;
			cmd.is_company = isCompany;
			cmd.name = bidderName;
			cmd.canton_id = $("#txtCantonId").val();
			cmd.mobile = $("#txtMobile").val();
			cmd.tel = $("#txtTel").val();
			if ((cmd.is_company ==1)||(cmd.is_company == 0&&cmd.bidder_type==3)) {
				cmd.certificate_no = $("#txtCompanyCode").val();
				cmd.reg_no = $("#txtRegNo").val();
				cmd.reg_address = $("#txtRegAddress").val();
				var regCapital = parseFloat($("#txtRegCapital").val());
				if (isNaN(regCapital))
					regCapital = 0;
				cmd.reg_capital = regCapital;
				cmd.reg_type = $("#txtRegType").val();
				cmd.reg_area = $("#txtRegArea").val();
				cmd.reg_corporation = $("#txtCorporation").val();
				cmd.reg_corporation_idno = $("#txtCorporationIdno").val();
			} else {
				cmd.certificate_no = $("#txtCertificateNo").val();
			}
			cmd.fax = $("#txtFax").val();
			cmd.email = $("#txtEmail").val();
			cmd.address = $("#txtAddress").val();
			cmd.post_code = $("#txtPostCode").val();
			cmd.contact = $("#txtContact").val();
			//cmd.is_oversea = $("#cboIsOversea").val();
			cmd.cakey = $("#txtCakey").val();
			cmd.valid_date = $("#txtValidDate").val();
			cmd.remark = $("#txtRemark").val();
			cmd.cakey = $("#txtCakey").val();
			cmd.user_name = $("#txtUserName").val();
			cmd.txtAgentName = $("#txtAgentName").val();
			cmd.txtAgentNo = $("#txtAgentNo").val();
			cmd.txtAgentPhone = $("#txtAgentPhone").val();
			if ($("#txtUserPassword").val() != "")
				cmd.user_password = $.md5($('#txtUserPassword').val());
			cmd.user_id = pageObj.userId;
			cmd.cakey_id = pageObj.cakeyId;
			if (pageObj.id && activeSave == true) {
				//审核操作时选择审核不通过时，仅保存数据。此时保存审核意见相关信息
				cmd.status = 6;
				cmd.confirm_opinion = $("#cboConfirmCause").val() != 3 ? ($("#cboConfirmCause").find("option:selected").text() + ($("#txtConfirmOpinion").val() ? "，" + $("#txtConfirmOpinion").val() : "。")) : $("#txtConfirmOpinion").val();
				cmd.confirm_user_id = pageObj.userInfo.userId;
			}
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					pageObj.id = data.id;
					pageObj.userId = data.userId;
					pageObj.cakeyId = data.cakeyId;
					pageObj.initData();//重新刷新数据，获取数据库中的默认值
					pageObj.afterSelectTabItem('tabConfirm');
					pageObj.checkDataModified.initFileds();
					DialogAlert('保存竞买人成功完成', window);
					if (pageObj.checkParentObj()) {
						pageObj.dialogManager.parentPageObj.queryData(true);
					}
				} else {
					DialogError('保存竞买人失败' + (data.message ? ',错误原因：' + data.message : '。'), window);
					return false;
				}
			};
			cmd.execute();
		}
	}
	cmd1.execute();
}

//---------------------------------
//冻结按钮事件
//---------------------------------
pageObj.freezeBidder = function () {
	if (pageObj.status == 2) {
		DialogAlert('竞买人已经是冻结状态，不需要再次冻结', window);
		return false;
	}
	$("#confirmPrompt").html("请输入冻结意见：");
	$("#confirmStatus").hide();
	$("#confirmCause").hide();
	$("#lblConfirmOpinion").html("意见");
	$("#txtConfirmOpinion").val("");
	$("#lblConfirmPrompt").html("");
	DialogOpen({ width: 330, title: '冻结竞买人', target: $("#confirmEditor"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				if (!$("#txtConfirmOpinion").val()) {
					alert('必须输入审核意见。');
					return false;
				}
				var cmd = new Command();
				cmd.module = "trademan";
				cmd.service = "Bidder";
				cmd.method = "updateBidderData";
				cmd.updateMode = "freeze";
				cmd.id = pageObj.id;
				cmd.status = 2;
				cmd.confirm_opinion = $("#txtConfirmOpinion").val();
				cmd.confirm_user_id = pageObj.userInfo.userId;
				cmd.user_id = pageObj.userId;
				cmd.cakey_id = pageObj.cakeyId;
				cmd.success = function (data) {
					if(data.state == 1) {
						pageObj.status = 2;
						$("#lblConfirmOpinion").html($("#txtConfirmOpinion").val());
						var confirmedDate = new Date();
						$("#lblConfirmDate").html(confirmedDate.toString().substr(0, 10));
						pageObj.initControl();
						DialogAlert('竞买人冻结完成', window);
						if (pageObj.checkParentObj()) {
							pageObj.dialogManager.parentPageObj.queryData();
						}
					} else {
						DialogError('竞买人冻结失败' + (data.message ? ',错误原因：' + data.message : '。'), window);
						return false;
					}
				};
				cmd.execute();
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	}, window);
}

//---------------------------------
//年审按钮事件
//---------------------------------
pageObj.checkBidder = function () {
	if (pageObj.status == 3) {
		DialogAlert('竞买人已经是待年审状态，不需要再次设置', window);
		return false;
	}
	$("#confirmPrompt").html("请输入待年审意见：");
	$("#confirmStatus").hide();
	$("#confirmCause").hide();
	$("#lblConfirmOpinion").html("意见");
	$("#txtConfirmOpinion").val("");
	$("#lblConfirmPrompt").html("");
	DialogOpen({ width: 330, title: '竞买人待年审', target: $("#confirmEditor"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				if (!$("#txtConfirmOpinion").val()) {
					alert('必须输入待年审意见。');
					return false;
				}
				var cmd = new Command();
				cmd.module = "trademan";
				cmd.service = "Bidder";
				cmd.method = "updateBidderData";
				cmd.updateMode = "check";
				cmd.id = pageObj.id;
				cmd.status = 3;
				cmd.confirm_opinion = $("#txtConfirmOpinion").val();
				cmd.confirm_user_id = pageObj.userInfo.userId;
				cmd.user_id = pageObj.userId;
				cmd.cakey_id = pageObj.cakeyId;
				cmd.success = function (data) {
					if(data.state == 1) {
						pageObj.status = 3;
						$("#lblConfirmOpinion").html($("#txtConfirmOpinion").val());
						var confirmedDate = new Date();
						$("#lblConfirmDate").html(confirmedDate.toString().substr(0, 10));
						pageObj.initControl();
						DialogAlert('竞买人设置为待年审完成', window);
						if (pageObj.checkParentObj()) {
							pageObj.dialogManager.parentPageObj.queryData();
						}
					} else {
						DialogError('竞买人设置为待年审失败' + (data.message ? ',错误原因：' + data.message : '。'), window);
						return false;
					}
				};
				cmd.execute();
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	}, window);
}

//---------------------------------
//注销按钮事件
//---------------------------------
pageObj.logoffBidder = function () {
	if (pageObj.status == 4) {
		DialogAlert('竞买人已经是注销状态，不需要再次注销', window);
		return false;
	}
	$("#confirmPrompt").html("请输入注销意见：");
	$("#confirmStatus").hide();
	$("#confirmCause").hide();
	$("#lblConfirmOpinion").html("意见");
	$("#txtConfirmOpinion").val("");
	$("#lblConfirmPrompt").html("");
	DialogOpen({ width: 330, title: '注销竞买人', target: $("#confirmEditor"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				if (!$("#txtConfirmOpinion").val()) {
					alert('必须输入注销意见。');
					return false;
				}
				var cmd = new Command();
				cmd.module = "trademan";
				cmd.service = "Bidder";
				cmd.method = "updateBidderData";
				cmd.updateMode = "logoff";
				cmd.id = pageObj.id;
				cmd.status = 4;
				cmd.confirm_opinion = $("#txtConfirmOpinion").val();
				cmd.confirm_user_id = pageObj.userInfo.userId;
				cmd.user_id = pageObj.userId;
				cmd.cakey_id = pageObj.cakeyId;
				cmd.success = function (data) {
					if(data.state == 1) {
						pageObj.status = 4;
						$("#lblConfirmOpinion").html($("#txtConfirmOpinion").val());
						var confirmedDate = new Date();
						$("#lblConfirmDate").html(confirmedDate.toString().substr(0, 10));
						pageObj.initControl();
						DialogAlert('竞买人注销完成', window);
						if (pageObj.checkParentObj()) {
							pageObj.dialogManager.parentPageObj.queryData();
						}
					} else {
						DialogError('竞买人注销失败' + (data.message ? ',错误原因：' + data.message : '。'), window);
						return false;
					}
				};
				cmd.execute();
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	}, window);
}

//---------------------------------
//激活按钮事件
//---------------------------------
pageObj.activeBidder = function () {
	var blnValid = true;
	var isCompany = $("#cboIsCompany").val();
	var validDate = $("#txtValidDate").val();
	if (validDate) {
		validDate = validDate.substr(0, 10);
		var now = new Date();
		now = now.toString().substr(0, 10);
		blnValid = now < validDate;
	}
	if (blnValid) {
		if (pageObj.status == 0) {
			if (!confirm('竞买人还处于申请中编辑状态，不需要提交直接审核？'))
				return false;
		}
		if (pageObj.status == 1) {
			DialogAlert('竞买人已经是正常状态，不需要审核', window);
			return false;
		}
	}
	var bidderName = $("#txtName").val();
	var message = (blnValid ? '审核通过后该竞买人可以正常使用。'
			: '当前竞买人有效期已过，审核操作将自动由今天延后一年有效期。<br/>'
			+ '也可以直接修改有效日期后保存。')
		+ '当相关材料审核不通过时请选择“仅保存数据”。';
	$("#confirmPrompt").html("请输入审核意见：");
	$("#cboConfirmStatus").val("1");
	$("#cboConfirmCause").val("0");
	pageObj.changeConfirmStatus();
	$("#confirmStatus").show();
	$("#txtConfirmOpinion").val("");
	$("#lblConfirmPrompt").html("3、" + message);
	DialogOpen({ width: 330, title: '审核竞买人', target: $("#confirmEditor"),
		buttons: [{ text: '确定', onclick: function (item, dialog) {
				var confirmStatus = $("#cboConfirmStatus").val();
				if (confirmStatus == 1) {
					if (!confirm('竞买人审核通过后就可以正常使用了，确定审核？' + (!$("#txtConfirmOpinion").val() ? '\n注意没有输入审核意见（非必须）。' : '')))
						return false;
					var cmd = new Command();
					cmd.module = "trademan";
					cmd.service = "Bidder";
					cmd.method = "updateBidderData";
					cmd.updateMode = "active";
					cmd.id = pageObj.id;
					cmd.status = 1;
					cmd.oldStatus = pageObj.status;
					cmd.oldValidDate = pageObj.validDate;
					if (!blnValid) {
						var nextYear = new Date();
						nextYear = Date.dateAdd(nextYear, 1, "yy");
						cmd.valid_date = nextYear.toString().substr(0, 10);
					}
					cmd.confirm_opinion = $("#txtConfirmOpinion").val();
					cmd.confirm_user_id = pageObj.userInfo.userId;
					cmd.user_id = pageObj.userId;
					cmd.cakey_id = pageObj.cakeyId;
					cmd.success = function (data) {
						if(data.state == 1) {
							if (pageObj.status == 1) {
								var nextYear = new Date();
								nextYear = Date.dateAdd(nextYear, 1, "yy");
								pageObj.validDate = nextYear.toString().substr(0, 10);
								$("#txtValidDate").val(pageObj.validDate);
							} else
								pageObj.status = 1;
							$("#lblConfirmOpinion").html($("#txtConfirmOpinion").val());
							var confirmedDate = new Date();
							$("#lblConfirmDate").html(confirmedDate.toString().substr(0, 10));
							pageObj.initControl();
							DialogAlert('竞买人激活完成', window);
							if (pageObj.checkParentObj()) {
								pageObj.dialogManager.parentPageObj.queryData();
							}
						} else {
							DialogError('竞买人激活失败' + (data.message ? ',错误原因：' + data.message : '。'), window);
							return false;
						}
					};
					cmd.execute();
				} else {
					//仅保存数据
					if (!$("#txtConfirmOpinion").val()) {
						alert('必须输入审核意见。');
						return false;
					}
					if (!confirm('当前竞买人审核不通过？'))
						return false;
					pageObj.saveData(true);
				}
				dialog.hide();
			}
		},
		{ text: '取消', onclick: function (item, dialog) {
				dialog.hide();
			}
		}]
	}, window);
}


pageObj.changeBidderType = function() {
	var bidderType = $('#cboBidderType').val();
	if (pageObj.id) {
		var isCompany = $('#cboIsCompany').val();
		if (bidderType != pageObj.bidderType || isCompany != pageObj.isCompany) {
			$("#unsavedPrompt").html("当前竞买人数据部分修改必须保存后才能管理附件材料。");
			if (pageObj.activeTabId == "tabAttach") {
				$("#attachPanel").hide();
				$("#unsavedPrompt").show();
			}
		}
	}
	if (bidderType == 3)//耕指
		$("#cboIsCompany").html("<option value='1'>企业</option><option value='0'>土地储备机构</option>");
	else
		$("#cboIsCompany").html("<option value='1'>企业</option><option value='0'>个人</option>");
	
	$("#cboIsCompany").val(isCompany);
}

pageObj.confirmInputKeyUp = function () {
	var srcElement = event.srcElement;
	if (!srcElement)
		return;
	var checkElement = null;
	var confirmElement = null;
	var promptElement = null;
	if (srcElement == $("#txtMobile")[0] || srcElement == $("#txtConfirmMobile")[0]) {
		checkElement = $("#txtMobile");
		confirmElement = $("#txtConfirmMobile");
		promptElement = $("#imgMobileOk");
	} else if (srcElement == $("#txtTel")[0] || srcElement == $("#txtConfirmTel")[0]) {
		checkElement = $("#txtTel");
		confirmElement = $("#txtConfirmTel");
		promptElement = $("#imgTelOk");
	} else if (srcElement == $("#txtUserPassword")[0] || srcElement == $("#txtConfirmUserPassword")[0]) {
		checkElement = $("#txtUserPassword");
		confirmElement = $("#txtConfirmUserPassword");
		promptElement = $("#imgUserPasswordOk");
	}
	if (checkElement.val() && checkElement.val() == confirmElement.val())
		promptElement.show();
	else
		promptElement.hide();
}

//---------------------------------
//查看审核、冻结、年审、激活、注销等操作历史
//---------------------------------
pageObj.viewConfirmHistory = function () {
	//方式一
	//var columns = "new_value:审核状态,,0#申请中|1#审核|2#冻结|3#年审|4#注销;change_cause:意见;change_date:日期";
	//方式二
	var columns = {new_value: {title: '审核状态', width: 0,
		render: {0: '申请中', 1: '审核', 2: '冻结', 3: '年审', 4: '注销'}},
		change_cause: {title: '意见'},
		change_date: {title: '日期'}
	};
	DialogOpen({height: 400, width: 360, title: '审核历史', url: approot + '/sysman/fieldChangeList.html?hideQueryCondition=1'
		+ '&refTableName=trans_bidder&fieldName=status&refId=' + pageObj.id
		//+ '&columns=' + columns, //方式一
		+ '&columns=' + encodeURIComponent(JSON.stringify(columns)), //方式二
		buttons: [{ text: '关闭', onclick: function (item, dialog) {
					dialog.close();
				}
			}]
		}, window);
}

//---------------------------------
//加载附件数据
//---------------------------------
pageObj.loadAttachData = function () {
	if (!pageObj.attachDataLoaded) {
		if (pageObj.id) {
			var bidderType = $('#cboBidderType').val();
			var isCompany = $('#cboIsCompany').val();
			if (bidderType != pageObj.bidderType || isCompany != pageObj.isCompany) {
				$("#unsavedPrompt").html("当前竞买人数据部分修改必须保存后才能管理附件材料。");
				$("#attachPanel").hide();
				$("#unsavedPrompt").show();
			} else {
				$("#unsavedPrompt").hide();
				$("#attachPanel").show();
				var initWidth = 780;
				var initHeight = 200;
				var grant = null;
				if (pageObj.readonly == 1 || pageObj.caManager)
					grant = {add: 1, edit: 1, del: 1,
						addDtl: 1, editDtl: 1, delDtl: 1,
						uploadFile: 1, downloadFile: 2, delFile: 1};
				else
					grant = {add: 2, edit: 2, del: 2,
						addDtl: 2, editDtl: 2, delDtl: 2,
						uploadFile: 2, downloadFile: 2, delFile: 2};
				var owners = new Array();
				var owner = {};
				owner.id = pageObj.id;
				owner.name = "竞买人";
				owner.title = "竞买人资料";
				owner.tableName = "trans_bidder";
				var templetNoPrefix = 
				owner.templetNo = Utils.decode(pageObj.bidderType, 1, 'house', 2, 'mineral', 3, 'plow', 'land') + 'Bidder' + Utils.decode(isCompany, 1, '1', '0');
				owner.grant = grant;
				owners.push(owner);
				var url = approot + '/sysman/attachList.html?u=' + pageObj.userInfo.userId
						+ '&owners=' + encodeURIComponent(JSON.stringify(owners))
						+ '&width=' + initWidth + '&height=' + initHeight;
				var attachFrame = $('#attachFrame');
				if (attachFrame.length == 0) {
					$('#attachPanel').html('<iframe id="attachFrame" src="'
							+ url + '" style="height:' + initHeight
							+ 'px;width:' + initWidth + 'px"></iframe>');
				} else {
					attachFrame.attr('src', url);
				}
			}
			pageObj.attachDataLoaded = true;
		} else {
			$("#attachPanel").hide();
			$("#unsavedPrompt").show();
		}
	}
}

//---------------------------------
//委托人所属信息标签TAB切换后事件
//---------------------------------
pageObj.afterSelectTabItem = function(tabid) {
	if (tabid == 'tabAttach') {
		pageObj.loadAttachData();
 	}
 	pageObj.activeTabId = tabid;
}

//---------------------------------
//设置Cakey按钮事件
//---------------------------------
pageObj.setCakey = function (e) {
	DialogConfirm('请插上新的CAKey后点击“是”按钮！', function (yes) {
		if (yes) {
			var caKeyId = ca.readId('getOtherKey');
			$("#txtCakey").val(caKeyId);
		}
	}, window);
}

pageObj.changeConfirmStatus = function () {
	var confirmStatus = $("#cboConfirmStatus").val();
	if (confirmStatus == 1) {
		$("#confirmCause").hide();
		$("#lblConfirmOpinionLabel").html("意见");
	} else {
		$("#confirmCause").show();
		$("#lblConfirmOpinionLabel").html("详细说明");
	}
}

pageObj.initControl = function () {
	var bidderType = $("#cboBidderType").val();
	var isCompany = $("#cboIsCompany").val();
	pageObj.changeBidderType();
	pageObj.changeIsCompany();
	if (pageObj.id) {
		$('#userPasswordRequired').hide();
		$('#confirmUserPasswordRequired').hide();
	} else {
		if (pageObj.requiredUser) {
			$('#userPasswordRequired').show();
			$('#confirmUserPasswordRequired').show();
		} else {
			$('#userPasswordRequired').hide();
			$('#confirmUserPasswordRequired').hide();
		}
	}
	if (pageObj.readonly == 1 || pageObj.caManager) {
		$("#bidderEditor input").each(function() {
			$(this).attr("readonly", true);
		});
		$("#bidderEditor select").each(function() {
			$(this).attr("disabled", true);
		});
		$("#btnFreeze").hide();
		$("#btnCheck").hide();
		$("#btnLogoff").hide();
		$("#btnActive").hide();
		if (pageObj.readonly == 1) {
			$("#btnSelectCanton").hide();
			$("#txtCantonName").removeAttr("style");
			$("#chkAllowModifyCakey").hide();
			$("#btnSetCakey").hide();
			$("#txtCakey").removeAttr("style");
		} else {
			$("#unmodifyPrompt").show();
		}
	} else {
		$("#unmodifyPrompt").hide();
	}
	if ($('#chkAllowModifyCakey').attr("disabled") || !$('#chkAllowModifyCakey').attr("checked"))
		$('#txtCakey').attr("readonly", true);
	else
		$('#txtCakey').attr("readonly", false);
	//$('#chkAllowModifyCakey').removeAttr("checked");
	if (pageObj.status == 1) {//审核
		if (pageObj.valid) {
			$("#lblStatus").html("正常");
			$("#btnActive").hide();
			if (pageObj.readonly == 0 && !pageObj.caManager) {
				$("#btnFreeze").show();
				$("#btnCheck").show();
				$("#btnLogoff").show();
			}
		} else {
			$("#lblStatus").html("冻结");
			$("#btnFreeze").hide();
			if (pageObj.readonly == 0 && !pageObj.caManager) {
				$("#btnCheck").show();
				$("#btnLogoff").show();
				$("#btnActive").show();
			}
		}
	} else if (pageObj.status == 2) {//冻结
		$("#lblStatus").html("冻结");
		$("#btnFreeze").hide();
		if (pageObj.readonly == 0 && !pageObj.caManager) {
			$("#btnCheck").show();
			$("#btnLogoff").show();
			$("#btnActive").show();
		}
	} else if (pageObj.status == 3) {//待年审
		$("#lblStatus").html("待年审");
		$("#btnCheck").hide();
		if (pageObj.readonly == 0 && !pageObj.caManager) {
			$("#btnFreeze").show();
			$("#btnLogoff").show();
			$("#btnActive").show();
		}
	} else if (pageObj.status == 4) {//注销
		$("#lblStatus").html("注销");
		$("#btnFreeze").hide();
		$("#btnCheck").hide();
		$("#btnLogoff").hide();
		if (pageObj.readonly == 0 && !pageObj.caManager) {
			$("#btnActive").show();
		}
	} else if (pageObj.status == 5) {//未通过退回
		$("#lblStatus").html("已提交");
		$("#btnFreeze").hide();
		$("#btnCheck").hide();
		$("#btnLogoff").hide();
		if (pageObj.id && pageObj.readonly == 0 && !pageObj.caManager) {
			$("#btnActive").show();
		}
	} else if (pageObj.status == 6) {//未通过退回
		$("#lblStatus").html("未通过(退回)");
		$("#btnFreeze").hide();
		$("#btnCheck").hide();
		$("#btnLogoff").hide();
		if (pageObj.id && pageObj.readonly == 0 && !pageObj.caManager) {
			$("#btnActive").show();
		}
	} else {//申请中、其它
		$("#lblStatus").html("申请中");
		$("#btnFreeze").hide();
		$("#btnCheck").hide();
		$("#btnLogoff").hide();
		if (pageObj.id && pageObj.readonly == 0 && !pageObj.caManager) {
			$("#btnActive").show();
		}
	}
}

pageObj.checkParentObj = function () {
	if (!pageObj.dialogManager) {
		pageObj.dialogManager = getGlobalAttribute("bidderEditDialog");
		removeGlobalAttribute("bidderEditDialog");
	}
	if (pageObj.dialogManager)
		return true;
	else
		return false;
}

pageObj.closeDialog = function () {
	if (pageObj.checkParentObj() && pageObj.dialogManager) {
		if (pageObj.readonly == 0
			&& (pageObj.checkDataModified.checkModified(true))) {
			DialogConfirm('当前竞买人 数据已修改，直接退出将不会被保存。<br/><br/>不保存数据退出？', function (yes) {
					if (yes) {
						pageObj.dialogManager.dialog.close();
					}
				}
			, window);
		} else
			pageObj.dialogManager.dialog.close();
	}
}

//---------------------------------
//页面初始化
//---------------------------------
pageObj.initHtml = function() {
	pageObj.isManagerUser = pageObj.userInfo.defaultRole && pageObj.userInfo.defaultRole.no == "house_manager";
	var readonly = Utils.getPageValue("readonly");
	if (readonly && (readonly == 1 || readonly.toLowerCase() == "true" || readonly.toLowerCase() == "yes"))
		pageObj.readonly = 1;
	else
		pageObj.readonly = 0;
	var caManager = Utils.getPageValue("caManager");
	if (caManager && (caManager == 1 || caManager.toLowerCase() == "true" || caManager.toLowerCase() == "yes"))
		pageObj.caManager = 1;
	else
		pageObj.caManager = 0;
	pageObj.id = Utils.getPageValue("id");
	pageObj.initBidderTypeList = null;
	pageObj.initBidderTypes = "";
	var initBidderTypes = Utils.getPageValue("bidderType");
	pageObj.goodsType = Utils.getPageValue("goodsType");
	if (initBidderTypes) {
		var bidderTypes = Utils.getSubStrs(initBidderTypes, ",", true);
		if (bidderTypes.length > 0) {
			for(var i = 0; i < bidderTypes.length; i++) {
				var bidderType = parseInt(bidderTypes[i]);
				if (!isNaN(bidderType)) {
					if (!pageObj.initBidderTypeList)
						pageObj.initBidderTypeList = new Array();
					pageObj.initBidderTypeList.push(bidderType.toString());
					if (pageObj.initBidderTypes == "")
						pageObj.initBidderTypes = bidderType.toString();
					else
						pageObj.initBidderTypes += "," + bidderType.toString();
				}
			}
		}
	}
	if (pageObj.initBidderTypeList && pageObj.initBidderTypeList.length >= 0) {
		$("#cboBidderType option").each(function() {
				if($.inArray($(this).val(), pageObj.initBidderTypeList) == -1) {
					$(this).remove();
			}
		});
		if (pageObj.initBidderTypeList.length == 1)
			$("#cboBidderType").attr("disabled", true);
		$("#cboBidderType ").get(0).selectedIndex = 0;
	}
	$("#privacy").hide();
	$("#companyEditor").show();
	$("#personalEditor").hide();
	$("#txtMobile").ligerTip({content:"系统在需要的情况下将通过此号码发送通知信息", width: 100, auto: true});
	$("#txtRegType").ligerTip({content:"国有企业、集体企业、股份合作企业等", width: 100, auto: true});
	$("#btnClose").click(pageObj.closeDialog);
	pageObj.initBidderRelTab();
	pageObj.initData();
	//初始化事件
	$("#cboIsCompany").change(pageObj.changeIsCompany);
	$("#btnSelectCanton").click(pageObj.selectCanton);
	$("#btnFreeze input").click(pageObj.freezeBidder);
	$("#btnCheck input").click(pageObj.checkBidder);
	$("#btnLogoff input").click(pageObj.logoffBidder);
	$("#btnActive input").click(pageObj.activeBidder);
	$("#btnSetCakey").click(pageObj.setCakey);
	$("#btnSave input").click(pageObj.saveData);
	$("#cboBidderType").change(pageObj.changeBidderType);
	$("#txtUserPassword").keyup(pageObj.confirmInputKeyUp);
	$("#txtConfirmUserPassword").keyup(pageObj.confirmInputKeyUp);
	$("#imgUserPasswordOk").attr("src", pageObj.okIcon);
	$("#txtRegCapital").keydown(pageObj.checkValidityOnlyNumber);
	$("#txtValidDate").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'})});
	$("#btnConfirmHistory").click(pageObj.viewConfirmHistory);
	$("#cboConfirmStatus").change(pageObj.changeConfirmStatus);
	if (pageObj.requiredUser)
		$("#userRequired").show();
	else
		$("#userRequired").hide();
	if (pageObj.readonly == 0 && pageObj.userInfo && pageObj.userInfo.userName.toLowerCase() == "admin") {
		$('#chkAllowModifyCakey').show();
		$('#chkAllowModifyCakey').click(function(){
			if ($('#chkAllowModifyCakey').attr("checked")) {
	  　　		$('#txtCakey').removeAttr("readonly");
			} else {
				$('#txtCakey').attr("readonly", true);
			}
		});
	} else {
		$('#txtCakey').attr("readonly", true);
		$('#chkAllowModifyCakey').hide();
	}
	pageObj.checkDataModified = new FieldModifiedChecker($("#bidderEditor")[0], true);
	$("#btnTest").click(pageObj.test);
	$("#btnTest").hide();
}

pageObj.test = function () {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Attach";
	cmd.method = "requiredAttachCheck";
	cmd.refId = pageObj.id;
	cmd.refTableName = "trans_bidder";
	cmd.success = function(data) {
		if (data.state == 1) {
			DialogAlert("附件检查正常。<br/>"
				+ "附件分类数：" + data.attachCount + "<br/>"
				+ "附件数：" + data.attachDtlCount + "<br/>"
				+ "附件文件数：" + data.attachDtlFileCount + "<br/>"
				+ "必须附件数：" + data.requiredCount, window);
		} else {
			if (data.errorAttachs && data.errorAttachs.length > 0) {
				var error = "附件上传情况有问题：<br/>"
					+ "附件分类数：" + data.attachCount + "<br/>"
					+ "附件数：" + data.attachDtlCount + "<br/>"
					+ "附件文件数：" + data.attachDtlFileCount + "<br/>"
					+ "必须附件数：" + data.requiredCount + "。<br/>"
					+ "错误情况：<br/>";
				for(var i = 0; i < data.errorAttachs.length; i++)
					error += data.errorAttachs[i] + "<br/>";
				DialogError(error, window);
			} else
				DialogError("附件上传情况有问题，错误未知。<br/>"
					+ "附件分类数：" + data.attachCount + "<br/>"
					+ "附件数：" + data.attachDtlCount + "<br/>"
					+ "附件文件数：" + data.attachDtlFileCount + "<br/>"
					+ "必须附件数：" + data.requiredCount, window);
			
		}
	}
	cmd.execute();
}

$(document).ready(pageObj.initHtml);