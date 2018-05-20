var pageObj = {};
pageObj.noticeId = null;
pageObj.navtab = null;
pageObj.attachDataLoaded = false;//标识附件是否加载
pageObj.oldBusiness_type = null;//保存加载好的bussiness_type，以免在加载附件时出现重复附件记录
pageObj.goodsType = null;
pageObj.isrr=-1;//0表示当前审核人点击标识正确 1表示点了标识错误

pageObj.renderName = function(row, rowNamber) {
	var html = '<a href="javascript:void(0)" onclick="javascript:pageObj.targetView(\'' + row.id + '\',\'' + pageObj.goodsType +'\'); return false;">' + row.name + '</a>';
	return html;
}

// ---------------------------------
// 查看标的
// ---------------------------------
pageObj.targetView = function(targetId,goodstype) {
	var obj = {};
	// 地址
	obj.url = "targetEdit.html";
	// 参数
	//中
	obj.param = {
		u : pageObj.u,
		targetId : targetId,
		mode : comObj.mode['modify'],
		goodsType : goodstype,
		conFirm : 1,
		noticestatus : pageObj.noticestatus
	};
	// 窗口参数
	var sheight = screen.height - 100;
	var swidth = screen.width - 30;
	obj.feature = 'dialogWidth=' + swidth + 'px;dialogHeight=' + sheight + 'px';
	var returnValue = DialogModal(obj);
	if (returnValue)
		pageObj.refresh();
}

pageObj.statusRender = function(row, rowNumber) {
	return comObj.targetStatusObj[row.status + ''];
}
// ---------------------------------
// 初始化界面
// ---------------------------------
pageObj.initPage = function() {
	pageObj.mode = Utils.getPageValue('mode');
	pageObj.u = Utils.getPageValue('u');
	pageObj.noticeId = Utils.getPageValue('noticeId');
	pageObj.goodsType = Utils.getPageValue('goodsType');
	pageObj.noticestatus = Utils.getPageValue('status');
	if(pageObj.noticestatus==5){//通过的公告给发布按扭
		$("#noticePublish").val("发布");
		$('#noticePublish').click(pageObj.noticePublish2);
	}else{
		if(pageObj.noticestatus==4){
			$('#noticePublish').hide();
		}else{
			$('#noticePublish').click(pageObj.noticeCheck);
		}
	}
	if(pageObj.noticestatus==2){
		$("#noticePublish").hide();
	}
	var html = "";
	for ( var pro in comObj.notice_type) {
		Utils.addOption('notice_type', pro, comObj.notice_type[pro]);
	}
	$('#notice_type').val('0');
	
	$("#notice_type").attr("disabled", "disabled");
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNotice";
	cmd.method = "initNoticeListPageParam";
	cmd.u = pageObj.u;
	cmd.goodsType = pageObj.goodsType;
	cmd.success = function(data) {
		var html = "";
		$(data.businessType).each(function(index, obj) {
			if (index == 0){
				Utils.addOption('business_type', obj.id, obj.name);
				$("#business_type").val(obj.id);
			}
			else{
				Utils.addOption('business_type', obj.id, obj.name);
			}
		});
	};
	cmd.execute();
	switch (pageObj.mode) {
	case comObj.mode['new']:
		break;
	case comObj.mode['modify']:
	case comObj.mode['update']:
		var cmd = new Command();
		cmd.module = "before";
		cmd.service = "TransNotice";
		cmd.method = "getNoticeInfo";
		cmd.noticeId = pageObj.noticeId;
		cmd.tabName="notice";
		cmd.success = function(data) {
			Utils.setForm(data.notice);
			pageObj.oldBusiness_type=data.notice.business_type;
			var confirmData=data.noticeConfirmData;
			comObj.setConfirm(confirmData);
		}
		cmd.execute();
		break;
	}
	//
	$("#notice_date").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		})
	});
	var targetObj = {
		url : approot + '/data?module=before&service=TransNotice&method=getNoticeOtherTargetData',
		columns : [ {
			display : '宗地编号',
			name : 'name',
			width : 200,
			render : pageObj.renderName
		}, {
			display : '交易类型',
			name : 'business_name',
			width : 150
		}, {
			display : '起始价',
			name : 'begin_price',
			render : pageObj.renderBeginPrice,
			width : 200
		}, {
			display : '保证金',
			name : 'target_earnest_money',
			render : pageObj.renderEarnest,
			width : 200
		}, {
			display : '状态',
			name : 'status',
			width : 120,
			render : pageObj.statusRender
		}, {
			display : '经办人',
			name : 'user_name',
			width : 150
		} ],
		isScroll : true,// 是否滚动
		pageSizeOptions : [ 10, 20, 30 ],
		showTitle : false,
		checkbox : true,
		rownumbers : true,
		selectRowButtonOnly : true,
		width : '99.8%',
		height : 400
	};
	pageObj.targetGrid = $('#targetGrid').ligerGrid(targetObj);
	//
	targetObj.url = approot + '/data?module=before&service=TransNotice&method=getNoticeTargetData&noticeId=' + pageObj.noticeId;
	targetObj.toolbar = {
		items : [ {
			text : '增加标的',
			click : pageObj.boundTargets,
			icon : 'add'
		}, {
			line : true
		}, {
			text : '删除标的',
			click : pageObj.delBoundTargets,
			icon : 'delete'
		} ]
	};
	targetObj.height = 200;
	pageObj.boundedTargetGrid = $('#boundedTargetGrid').ligerGrid(targetObj);
	//
	var bankObj = {
		url : approot + '/data?module=before&service=TransNotice&method=getOtherBankList',
		columns : [ {
			display : '收款单位',
			name : 'organ_name',
			width : 150
		}, {
			display : '银行区域',
			name : 'canton_name',
			width : 100
		}, {
			display : '收款银行',
			name : 'bank_name',
			width : 150
		}, {
			display : '币种',
			name : 'currency',
			width : 100
		}, {
			display : '收款账户',
			name : 'no',
			width : 150
		}
//		, {
//			display : '类型',
//			name : 'is_outside',
//			width : 100,
//			render:pageObj.is_outside
//		} 
		],
		isScroll : true,// 是否滚动
		usePager : false,
		pageSizeOptions : [ 10, 20, 30 ],
		showTitle : false,
		checkbox : true,
		rownumbers : true,
		selectRowButtonOnly : true,
		width : '99.8%',
		height : 400
	};
	pageObj.bankGrid = $('#bankGrid').ligerGrid(bankObj);
	bankObj.url = approot + '/data?module=before&service=TransNotice&method=getNoticeBankList&noticeId=' + pageObj.noticeId;
	bankObj.toolbar = {
		items : [ {
			text : '增加银行',
			click : pageObj.boundBanks,
			icon : 'add'
		}, {
			line : true
		}, {
			text : '删除银行',
			click : pageObj.delBoundBanks,
			icon : 'delete'
		} ]
	};
	bankObj.height = 200;
	pageObj.boundedBankGrid = $('#boundedBankGrid').ligerGrid(bankObj);
	$('#noticeSave').click(pageObj.save);
	$("#noticeSave").hide();
	$('#relAttach').click(pageObj.initAttachList);
	pageObj.btnDis();
}

pageObj.renderBeginPrice = function(row, number) {
	return comObj.cf({
		amount : row.begin_price,
		unit : row.unit
	});
}

pageObj.renderEarnest = function(row, number) {
	var arr = row.target_earnest_money.split("&nbsp;");
	var arr2 = [];
	for ( var i = 0; i < arr.length; i++) {
		var arr1 = arr[i].split(":");
		arr2.push(comObj.cf({
			flag : arr1[0],
			amount : parseFloat(arr1[1]),
			unit : row.unit
		}));
	}
	return arr2.join(" ");
}

//----------------------------
//渲染账户类型
//---------------------------
pageObj.is_outside = function(row, rowNamber) {
	var is_outside = row.is_outside;
	if (is_outside == '0') {
		return '境内';
	}else	if (is_outside == '1') {
		return '境外';
	}else {
		return '境内';
	}
}

// ---------------------------------
// 刷新标的信息
// ---------------------------------
pageObj.refresh = function() {
	//
	var url = approot + '/data?module=before&service=TransNotice&method=getNoticeTargetData&noticeId=' + pageObj.noticeId;
	pageObj.boundedTargetGrid.refresh(url, {});
	//
	url = approot + '/data?module=before&service=TransNotice&method=getNoticeBankList&noticeId=' + pageObj.noticeId;
	pageObj.boundedBankGrid.refresh(url, {});

}

// ---------------------------------
// 保存公告信息
// ---------------------------------
pageObj.save = function() {
	if (!Utils.requiredCheck())
		return;
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNotice";
	cmd.method = "saveNotice";
	cmd.noticeId = pageObj.noticeId;
	cmd.mode = pageObj.mode;
	Utils.getForm(cmd);
	cmd.success = function(data) {
		pageObj.noticeId = data.id;
		DialogAlert(data.message);
		if(data.state==1){
			pageObj.oldBusiness_type=$("#business_type").val();
		}
		if (data.state) {
			window.returnValue = true;
			pageObj.btnDis();
		}

	}
	cmd.execute();
}

//---------------------------------
//审核公告
//---------------------------------
pageObj.noticeCheck = function() {
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNotice";
	cmd.method = "checkNotice";
	cmd.u = pageObj.u;
	cmd.noticeIds = pageObj.noticeId;
	DialogWarn("请选择审核是否通过：选择'是'通过，选择'否' 不通过。 ", function(btn) {
		var message="";
		if (btn == 'yes' ) {
			cmd.isCon=5;//通过
			message="审核通过";
		}else if(btn== 'no'){
			cmd.isCon=4;//不通过
			message="审核不通过";
		}else{
			return;
		}
		cmd.success = function(data) {
			alert(message);
			if(cmd.isCon==4){
				window.close();
			}
			window.returnValue = true;
		};
		cmd.execute();
	});
}

//// ---------------------------------
//// 发布
//// ---------------------------------
//pageObj.noticePublish = function() {
//	var cmd = new Command();
//	cmd.module = "before";
//	cmd.service = "TransNotice";
//	cmd.method = "publishNotice";
//	cmd.u = pageObj.u;
//	cmd.noticeIds = pageObj.noticeId;
//	cmd.success = function(data) {
//		DialogAlert(data.message);
//		pageObj.refresh();
//		if(data.message.indexOf("未选择标的")!=-1 ){
//			pageObj.selectTabById("tabitem1");
//		}else if(data.message.indexOf("可入款账号")!=-1){
//			pageObj.selectTabById("tabitem2");
//		}else if(data.message.indexOf("无有效附件")!=-1){
//			pageObj.selectTabById("tabitem3");
//		}
//		if (data.state)
//			window.returnValue = true;
//	};
//	cmd.execute();
//}

//---------------------------------
//发布
//---------------------------------
pageObj.noticePublish2 = function() {
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNotice";
	cmd.method = "checkNotice";
	cmd.u = pageObj.u;
	cmd.noticeIds = pageObj.noticeId;
	cmd.isCon=2;//发布
	cmd.success = function(data) {
		DialogAlert(data.message);
		window.returnValue = true;
	};
	cmd.execute();
}

// ---------------------------------
// 绑定银行
// ---------------------------------
pageObj.boundBanks = function() {
	$('#bankDiv').dialog({
		title : '选择银行',
		modal : true,
		width : 850,
		buttons : {
			"确定" : function() {
				var arr = pageObj.bankGrid.getCheckedArr("account_id");
				if (arr.length > 0) {
					var cmd = new Command();
					cmd.module = "before";
					cmd.service = "TransNotice";
					cmd.method = "doBoundAccount";
					cmd.noticeId = pageObj.noticeId;
					cmd.accountIds = arr.join(',');
					cmd.u = pageObj.u;
					cmd.success = function(data) {
						DialogAlert(data.message);
						pageObj.refresh();
					}
					cmd.execute();

				}
				$(this).dialog('close');
			}
		}
	});
	$("#bankGrid").addClass("trade_panel");
	var queryParams = {
		noticeId : pageObj.noticeId,
		u : pageObj.u
	};
	var url = approot + '/data?module=before&service=TransNotice&method=getOtherBankList';
	pageObj.bankGrid.refresh(url, queryParams);
}

// ---------------------------------
// 绑定标的
// ---------------------------------
pageObj.boundTargets = function() {
	$('#targetDiv').dialog({
		title : '绑定标的',
		modal : true,
		width : 850,
		buttons : {
			"确定" : function() {
				var arr = pageObj.targetGrid.getCheckedArr();
				if (arr.length > 0) {
					var cmd = new Command();
					cmd.module = "before";
					cmd.service = "TransNotice";
					cmd.method = "doBoundTargets";
					cmd.noticeId = pageObj.noticeId;
					cmd.u = pageObj.u;
					cmd.targetIds = arr.join(',');
					cmd.success = function(data) {
						DialogAlert(data.message);
						if (data.state)
							pageObj.refresh();
					}
					cmd.execute();

				}
				$(this).dialog('close');
			}
		}
	});
	$("#targetDiv").addClass("trade_panel");
	var url = approot + '/data?module=before&service=TransNotice&method=getNoticeOtherTargetData';
	pageObj.targetGrid.refresh(url, {
		noticeId : pageObj.noticeId,
		goodsType : pageObj.goodsType
	});
}

// ---------------------------------
// 删除绑定标的
// ---------------------------------
pageObj.delBoundTargets = function() {
	var arr = pageObj.boundedTargetGrid.getCheckedArr();
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNotice";
	cmd.method = "undoBoundTargets";
	cmd.noticeId = pageObj.noticeId;
	cmd.u = pageObj.u;
	cmd.targetIds = arr.join(',');
	cmd.success = function(data) {
		DialogAlert(data.message);
		pageObj.refresh();
	}
	cmd.execute();
}

// ---------------------------------
// 删除绑定银行
// ---------------------------------
pageObj.delBoundBanks = function() {
	var arr = pageObj.boundedBankGrid.getCheckedArr();
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNotice";
	cmd.method = "undoBoundAccount";
	cmd.noticeId = pageObj.noticeId;
	cmd.u = pageObj.u;
	cmd.accountIds = arr.join(',');
	cmd.success = function(data) {
		DialogAlert(data.message);
		pageObj.refresh();
	}
	cmd.execute();
}

pageObj.initAttachList = function() {
	var initWidth = 940;
	var initHeight = 200;
	var grant = {};
	if(pageObj.noticestatus>=2 ){
		grant = {add: 1, edit: 1, del: 1,
		addDtl: 1, editDtl: 1, delDtl: 1,
		uploadFile: 1, downloadFile: 2, delFile: 1};
	}else{
		grant = {add: 2, edit: 2, del: 2,
				addDtl: 2, editDtl: 2, delDtl: 2,
				uploadFile: 2, downloadFile: 2, delFile: 2};
	}
	var owners = new Array();
	var owner = {};
	owner.id = pageObj.noticeId;
	owner.name = "公告";
	owner.title = $('#name').val();
	owner.tableName = "trans_notice";
	owner.templetNo = "businessType" + pageObj.oldBusiness_type + "NoticeAttach";
	owner.grant = grant;
	owners.push(owner);
	obj.param = {};
	obj.param.u=pageObj.u;
	obj.param.owners=encodeURIComponent(JSON.stringify(owners));;
	
	// 地址
	obj.url =approot + '/sysman/attachList.html';

	// 窗口参数
	obj.feature = "dialogWidth=900px;dialogHeight=600px";
	var rv = DialogModal(obj);
}

//---------------------------------
//控制按扭禁用与启用，标的信息的显示与隐藏
//---------------------------------
pageObj.btnDis = function (){
	if(pageObj.noticeId != null && pageObj.noticeId != ""){
		$("#bankTable").show();//银行面板显示
		$("#targetTable").show();//标的信息
		$("#noticePublish").removeAttr("disabled");//启用发布按扭
		$("#relAttach").removeAttr("disabled");//启用附件按扭
	}else{
		$("#bankTable").hide();//银行面板显示
		$("#targetTable").hide();//标的信息
		$("#noticePublish").attr('disabled',"true");
		$("#relAttach").attr('disabled',"true");
	} 
}

pageObj.bodyClick = function(e){
	comObj.clickBody(e,pageObj.noticeId,"notice");
}

$(document).ready(function() {
	pageObj.initPage();
	if(pageObj.noticestatus==3){
		$("body").click(pageObj.bodyClick);
		$("#rightBtn").click(function(){
			pageObj.isrr=0;
		});
		$("#errorBtn").click(function(){
			pageObj.isrr=1;
		});
		$("#rightBtn").show();
		$("#errorBtn").show();
	}else{
		$("#rightBtn").hide();
		$("#errorBtn").hide();
	}
	
});