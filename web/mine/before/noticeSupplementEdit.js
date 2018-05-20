var pageObj = {};

pageObj.initPage = function(){
	pageObj.mode = Utils.getPageValue('mode');
	pageObj.u = Utils.getPageValue('u');
	pageObj.noticeId = Utils.getPageValue('noticeId');
	pageObj.parentId = Utils.getPageValue('parentId');
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNoticeMine";
	cmd.method = "getSupplementInfo";
	cmd.u = pageObj.u;
	cmd.mode = pageObj.mode;
	cmd.noticeId = pageObj.noticeId;
	cmd.parentId = pageObj.parentId;
	cmd.success = function(data) {
		pageObj.noticeId = data.notice.id;
		pageObj.parentId = data.parent.id;
		pageObj.mode = data.mode;
		pageObj.status = data.notice.status;
		var html = "";
		$('#business_type').val(data.notice.business_type);
		$(data.businessType).each(function(index, obj) {
			if (obj.id == data.notice.business_type){
				$('#business_type_name').val(obj.name);
			}
		});
		$('#notice_type').val(1);
		$('#notice_type_name').val("补充公告");
		$('#name').val(data.notice.name);
		$('#no').val(data.notice.no);
		$('#notice_date').val(data.notice.notice_date);
		$('#remark').val(data.notice.remark);
		$('#parent_notice').html(data.parent.name+"("+data.parent.no+")");
	};
	$("#notice_date").click(function() {
		WdatePicker({
					dateFmt : 'yyyy-MM-dd HH:mm:ss'
				})
	});
	cmd.execute();
	
	pageObj.targetGrid = $("#targetGrid").ligerGrid({
		url: approot + '/data?module=before&service=TransNoticeMine&method=getSupplementNoticeTargetData&parentId='+ pageObj.parentId+"&noticeId="+pageObj.noticeId,
		columns:[{display : '宗地编号',name : 'name',width : 200}, 
		           {display : '交易类型',name : 'business_name',width : 150},
		           {display : '起始价',name : 'begin_price',width : 100,render:pageObj.renderBeginPrice}, 
		           {display : '保证金',name : 'target_earnest_money',width : 100,render:pageObj.renderEarnestMoneny},
		           {display : '状态',name : 'status',width : 120,render : pageObj.statusRender}, 
		           {display : '经办人',name : 'user_name',width : 150}
		],
		usePager: false,
		checkbox: true,
		rownumbers: true,
		isScroll : true, // 是否滚动
		frozen : false,// 是否固定列
		showTitle : false,
		width : '99.8%',
		alternatingRow:false,
		isChecked:pageObj.initChecked
	});
	
	//
	$('#noticeSave').click(pageObj.save);
	$('#noticeCancel').click(pageObj.noticeCancel);
	$('#noticePublish').click(pageObj.noticePublish);
	
}

//-----------------------------
//渲染保证金
//----------------------------
pageObj.renderEarnestMoneny = function(row, rowNumber) {
	var moneys = row.target_earnest_money;
	if (moneys) {
		var arr1 = moneys.split("&nbsp;");
		var arr2 = [];
		for ( var i = 0; i < arr1.length; i++) {
			var arr3 = arr1[i].split(":");
			arr2.push(comObj.cf({
				flag : arr3[0],
				unit : row.unit,
				amount : parseFloat(arr3[1])
			}));
		}
		return arr2.join(" ");
	}
	return moneys;
}

//-----------------------------
//渲染起始价
//----------------------------
pageObj.renderBeginPrice=function(row,number){
	return comObj.cf({amount:row.begin_price,unit:row.unit});
}

pageObj.initChecked = function(rowData){
	if(pageObj.noticeId){
		if(rowData.notice_rel_id){
			return true;
		}else{
			return false;
		}
	}else{
		return true;
	}
}

pageObj.statusRender = function(row, rowNumber) {
	return comObj.targetStatusObj[row.status + ''];
}



//---------------------------------
//保存公告信息
//---------------------------------
pageObj.save = function() {
	if (!Utils.requiredCheck())
		return;
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNoticeMine";
	cmd.method = "saveNotice";
	cmd.noticeId = pageObj.noticeId;
	cmd.parent_id = pageObj.parentId;
	cmd.business_type = $("#business_type").val();
	cmd.notice_type = $("#notice_type").val();
	cmd.targetIds = pageObj.getTargetIds(); 
	if(cmd.targetIds==null || cmd.targetIds==""){
		DialogAlert("保存失败!发布的公告必须关联标的,请勾选标的信息中的标的!");
		return;
	}
	cmd.mode = pageObj.mode;
	Utils.getForm(cmd);
	cmd.success = function(data) {
		pageObj.noticeId = data.id;
		DialogAlert(data.message);
		if (data.state){
			window.returnValue = true;
			pageObj.status = 0;
			pageObj.showButton();
		}
			
	}
	cmd.execute();
}

pageObj.getTargetIds = function(){
	var manager = $("#targetGrid").ligerGetGridManager();
	var selectedRow = manager.getSelectedRows();
	var ids="";
	for(var i=0;i<selectedRow.length;i++){
		if(i==selectedRow.length-1){
			ids=ids+selectedRow[i].id;
		}else{
			ids=ids+selectedRow[i].id+",";
		}
	}
	return ids;
}

//---------------------------------
//撤销公告
//---------------------------------
pageObj.noticeCancel = function() {
	if(pageObj.status == 2 ){
		DialogConfirm('确定要将撤销发布该补充公告?', function(yn) {
			if (!yn)
				return;
			var cmd = new Command();
			cmd.module = "before";
			cmd.service = "TransModifyMine";
			cmd.method = "cancelNotice";
			cmd.u = pageObj.u;
			cmd.noticeId = pageObj.noticeId ;
			cmd.success = function(data) {
				DialogAlert(data.message);
				if(data.state){
					window.returnValue = true;
					pageObj.status = 0;
					pageObj.showButton();
				}
			};
			cmd.execute();
		});
	}else{
		alert("该公告尚未发布！");
	}
}

//---------------------------------
//发布
//---------------------------------
pageObj.noticePublish = function() {
	if(!pageObj.status && pageObj.status!=0){
		alert("请先保存公告！");
		return;
	}
	if(pageObj.status == 0 ){
		DialogConfirm('确定要发布该补充公告?', function(yn) {
			if (!yn)
				return;
			var cmd = new Command();
			cmd.module = "before";
			cmd.service = "TransNoticeMine";
			cmd.method = "publishNotice";
			cmd.u = pageObj.u;
			cmd.isCon=2;
			cmd.noticeIds = pageObj.noticeId;
			cmd.success = function(data) {
				DialogAlert(data.message);
				if(data.state){
					window.returnValue = true;
					pageObj.status = 2;
					pageObj.showButton();
				}
			};
			cmd.execute();
		});
	}else{
		alert("该公告已经发布！");
	}
}


//---------------------------------
//绑定标的
//---------------------------------
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
							cmd.service = "TransNoticeMine";
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
	var url = approot + '/data?module=before&service=TransNoticeMine&method=getNoticeOtherTargetData';
	pageObj.targetGrid.refresh(url, {
				noticeId : pageObj.noticeId,
				goodsType : pageObj.goodsType
			});
}

//---------------------------------
//删除绑定标的
//---------------------------------
pageObj.delBoundTargets = function() {
	var arr = pageObj.boundedTargetGrid.getCheckedArr();
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNoticeMine";
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
	owner.title = $('#name').val() + "(" + $('#no').val() + ")";
	owner.tableName = "trans_notice";
	owner.templetNo = "businessType" + $("#business_type").val() + "NoticeAttach";
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


pageObj.showButton = function() {
	if(pageObj.status && pageObj.status==2){
		$('#noticeSave').hide();
		$('#noticePublish').attr("disabled","true");
		$('#relAttach').attr("disabled","true");
		$('#noticeCancel').show();
	}else if(pageObj.status==0){
		$('#noticeSave').show();
		$('#noticePublish').removeAttr("disabled");
		$('#relAttach').removeAttr("disabled");
		$('#noticeCancel').hide();
	}else{
		$('#noticeSave').show();
		$('#noticePublish').attr("disabled","true");
		$('#relAttach').attr("disabled","true");
		$('#noticeCancel').hide();
	}
}

$(document).ready(function() {
	pageObj.initPage();
	pageObj.showButton();
	$('#relAttach').click(pageObj.initAttachList);//
});