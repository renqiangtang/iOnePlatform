var pageObj = {};
pageObj.noticeId = null;
pageObj.navtab=null;
pageObj.attachDataLoaded = false;// 标识附件是否加载
pageObj.oldBusiness_type = null;// 保存加载好的bussiness_type，以免在加载附件时出现重复附件记录
pageObj.isCheckNotice=0;//0不需要审核 1需要审核
pageObj.noticestatus=0;
pageObj.ischange=0;//判断是否改变，提交时提示需要保存
pageObj.renderName = function(row, rowNamber) {
	var html = '<a href="javascript:void(0)" onclick="javascript:pageObj.targetView(\'' + row.id + '\'); return false;">' + row.name + '</a>';
	return html;
}

// ---------------------------------
// 查看标的
// ---------------------------------
pageObj.targetView = function(targetId) {
	var obj = {};
	// 地址
	obj.url = "targetEdit.html";
	// 参数
	obj.param = {
		u : pageObj.u,
		targetId : targetId,
		mode : comObj.mode['modify'],
		goodsType : pageObj.goodsType,
		noticestatus : pageObj.noticestatus,
		conFirm : 0
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
	var html = "";
	for ( var pro in comObj.notice_type) {
		Utils.addOption('notice_type', pro, comObj.notice_type[pro]);
	}
	$('#notice_type').val('0');
	$("#notice_type").attr("disabled", "disabled");
	//
	
	
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
			//加载审核信息
			if(data.notice.status==4){
				var confirmData=data.noticeConfirmData;
				comObj.setConfirm(confirmData);
			}
			pageObj.noticestatus=data.notice.status;
			
		}
		cmd.execute();
		break;
	}
	$("#notice_date").click(function() {
		WdatePicker({
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		})
	});
	var cmd1 = new Command();
	cmd1.module = "before";
	cmd1.service = "TransNotice";
	cmd1.method = "initNoticeListPageParam";
	cmd1.u = pageObj.u;
	cmd1.goodsType = pageObj.goodsType;
	cmd1.success = function(data) {
		pageObj.isCheckNotice=data.isCheckNotice;
		if(pageObj.noticestatus==5 || pageObj.isCheckNotice==0){// 通过的公告给发布按扭或者后台设置了不需要审核的
			$("#noticePublish").val("发布");
			$('#noticePublish').click(pageObj.noticePublish2);
		}else{
			$("#noticePublish").val("提交");
			$('#noticePublish').click(pageObj.noticePublish);
		}
		if(pageObj.noticestatus==2){
			$("#noticePublish").hide();
		}
		
		$('#noticeSave').click(pageObj.save);
		
		$('#relAttach').click(pageObj.initAttachList);
		var html = "";
		$(data.businessType).each(function(index, obj) {
			if (index == 0){
				Utils.addOption('business_type', obj.id, obj.name);
				$("#business_type").val(obj.id);
			}
			else{
				Utils.addOption('business_type', obj.id, obj.name);
			}
			if(pageObj.oldBusiness_type!=null){
				$("#business_type").val(pageObj.oldBusiness_type);
			}
		});
	};
	cmd1.execute();
	
	pageObj.btnDis();
	if(Utils.getPageValue('status')!=null && Utils.getPageValue('status')!=undefined){
		pageObj.noticestatus = Utils.getPageValue('status');
	}
	
	
}

pageObj.loadOtherGrid = function(){
	
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
	var bankObj = {
		url : approot + '/data?module=before&service=TransNotice&method=getOtherBankList',
		columns : [ {
			display : '单位名称',
			name : 'organ_name',
			width : 150
		}, {
			display : '银行区域',
			name : 'canton_name',
			width : 100
		}, {
			display : '银行名称',
			name : 'bank_name',
			width : 150
		}, {
			display : '币种',
			name : 'currency',
			width : 100,
			render:function(item,index){
				return comObj.Currency[item.currency];
			}
		}, {
			display : '账号',
			name : 'no',
			width : 150
		}, {
			display : '类型',
			name : 'is_outside',
			width : 100,
			render:pageObj.is_outside
		} ],
		isScroll : true,// 是否滚动
		usePager : false,
		pageSizeOptions : [ 10, 20, 30 ],
		showTitle : false,
		checkbox : true,
		rownumbers : true,
		selectRowButtonOnly : true,
		width : '99.8%',
		height : 300
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

// ----------------------------
// 渲染账户类型
// ---------------------------
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
	var oldNoticeId=pageObj.noticeId;
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNotice";
	cmd.method = "saveNotice";
	cmd.noticeId = pageObj.noticeId;
	cmd.mode = pageObj.mode;
	cmd.no=$("#name").val();
	Utils.getForm(cmd);
	cmd.success = function(data) {
		pageObj.noticeId = data.id;
		DialogAlert(data.message);
		if(data.state==1){
			pageObj.oldBusiness_type=$("#business_type").val();
			pageObj.ischange=0;
		}
		if (data.state) {
			if(oldNoticeId==undefined || oldNoticeId==null && oldNoticeId==""){
				pageObj.loadDefaultBank();//默认添加所有银行
			}
			pageObj.btnDis();
			window.returnValue = true;
		}

	}
	cmd.execute();
}

// ---------------------------------
// 审核公告
// ---------------------------------
pageObj.noticeCheck = function() {
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNotice";
	cmd.u = pageObj.u;
	cmd.method = "checkNotice";
	cmd.noticeIds = pageObj.noticeId;
	cmd.success = function(data) {
		DialogAlert(data.message);
		pageObj.refresh();
		if (data.state)
			window.returnValue = true;
	};
	cmd.execute();
}

//---------------------------------
//提交
//---------------------------------
pageObj.noticePublish = function() {
    if(pageObj.ischange==1){
    	DialogAlert("检测到数据有修改,请先保存后再提交！");
    	return;
    }
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNotice";
	cmd.method = "publishNotice";
	cmd.u = pageObj.u;
	cmd.noticeIds = pageObj.noticeId;
	cmd.success = function(data) {
		DialogAlert(data.message);
		pageObj.refresh();
		if(data.message.indexOf("未选择标的")!=-1 ){
			pageObj.selectTabById("tabitem1");
		}else if(data.message.indexOf("可入款账号")!=-1){
			pageObj.selectTabById("tabitem2");
		}else if(data.message.indexOf("无有效附件")!=-1){
			pageObj.selectTabById("tabitem3");
		}
		if (data.state)
			window.returnValue = true;
	};
	cmd.execute();
}
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
	cmd.isCon=2;// 发布
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
	var grant = {add: 2, edit: 2, del: 2,
		addDtl: 2, editDtl: 2, delDtl: 2,
		uploadFile: 2, downloadFile: 2, delFile: 2};
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

// ---------------------------------
// 控制按扭禁用与启用，标的信息的显示与隐藏
// ---------------------------------
pageObj.btnDis = function (){
	if(pageObj.noticeId != null && pageObj.noticeId != ""){
		$("#bankTable").show();// 银行面板显示
		$("#targetTable").show();// 标的信息
		$("#noticePublish").removeAttr("disabled");// 启用发布按扭
		$("#relAttach").removeAttr("disabled");// 启用附件按扭
		pageObj.loadOtherGrid();
	}else{
		$("#bankTable").hide();// 银行面板显示
		$("#targetTable").hide();// 标的信息
		$("#noticePublish").attr('disabled',"true");
		$("#relAttach").attr('disabled',"true");
	} 
}

pageObj.loadDefaultBank = function(){
		var cmd = new Command();
		cmd.module = "before";
		cmd.service = "TransNotice";
		cmd.method = "getOtherBankList";
		cmd.noticeId = pageObj.noticeId;
		cmd.u = pageObj.u;
		cmd.success = function(data) {
			if(data.state==1){
				var objs=data.Rows;
				var aids="";//所有银行的ID
				$(objs).each(function(i,ele){
						aids+=ele.account_id+",";
					}
				);
				if(aids.substring(aids.length-1,aids.length)==","){
					aids=aids.substring(0,aids.length-1);
				}
				if (aids.length > 0) {
					var cmd1 = new Command();
					cmd1.module = "before";
					cmd1.service = "TransNotice";
					cmd1.method = "doBoundAccount";
					cmd1.noticeId = pageObj.noticeId;
					cmd1.accountIds = aids;
					cmd1.u = pageObj.u;
					cmd1.success = function(data) {
						if(data.state!=1){
							alert("默认加载银行出错!");
						}
					}
					cmd1.execute();
				}
			}
			
		}
		cmd.execute();
}

$(document).ready(function() {
	pageObj.initPage();
	$("#name").change(function(){
		pageObj.ischange=1;
	});
	
	$("#notice_type").change(function(){
		pageObj.ischange=1;
	});
	
	$("#notice_date").change(function(){
		pageObj.ischange=1;
	});
	
	$("#business_type").change(function(){
		pageObj.ischange=1;
	});
	$("#remark").change(function(){
		pageObj.ischange=1;
	});
	
	
});