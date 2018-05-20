var pageObj = {};
pageObj.grid = null;

pageObj.renderNoticeType = function(row, rowNumber) {
	return comObj.notice_type[row.notice_type + ''];
}

pageObj.business_typeRender = function(row, rowNumber) {
	return pageObj.businessType[row.business_type + ''];
}

pageObj.statusRender = function(row, rowNumber) {
	return comObj.notice_status[row.status + ''];
}
pageObj.nameRender = function(row, rowNumber) {
	return '<a href="javascript:pageObj.noticeEdit(null,\'' + row.id + '\',\'' + row.notice_type + '\',\'' + row.business_type + '\',\'' + row.status + '\')">'
			+ row.name + '</a>';
}

// ---------------------------------
// 按钮事件 新增 修改
// ---------------------------------
pageObj.noticeEdit = function(event,noticeId,noticeType,businessType,status) {
	var obj = {};
	var goodsType="";
	// 地址
	if(noticeType){
		if(noticeType == 0){
			obj.url = "noticeConfirmEdit.html";
		}else if(noticeType == 1){
			obj.url = "noticeSupplementEdit.html";
		}
	}else{
		obj.url = "noticeConfirmEdit.html";
	}
	if(businessType){
		goodsType = businessType.substring(0,3);
	}else if(event.data.a){
		goodsType = event.data.a;
	}
	// 参数
	obj.param = {
		u : pageObj.u,
		noticeId : noticeId,
		goodsType : goodsType,
		status : status
	};
	if (noticeId) {
		obj.param.noticeId = noticeId;
		obj.param.mode = 'modify';
	} else {
		obj.param.mode = 'new';
	}

	// 窗口参数
	var sheight = screen.height - 150;
	var swidth = screen.width - 10;
	obj.feature = 'dialogWidth=' + swidth + 'px;dialogHeight=' + sheight + 'px';
	var returnValue = DialogModal(obj);
	if (returnValue)
		pageObj.query();
}
// ---------------------------------
// 删除按钮事件
// ---------------------------------
pageObj.noticeDelete = function() {
	var noticeIdArr = pageObj.grid.getCheckedArr();
	if (noticeIdArr.length == 0){
		DialogAlert('请选择需要删除的记录！');
		return;
	}
	DialogConfirm('确定要将这' + noticeIdArr.length + '条删除?', function(yn) {
				if (!yn)
					return;
				var cmd = new Command();
				cmd.module = "before";
				cmd.service = "TransNotice";
				cmd.method = "deleteNotice";
				cmd.noticeIds = noticeIdArr.join(",");
				cmd.u = pageObj.u;
				cmd.success = function(data) {
					DialogAlert(data.message);
					if (data.state)
						pageObj.query();
				};
				cmd.execute();
			});
}
// ---------------------------------
// 查询按钮事件
// ---------------------------------
pageObj.query = function() {
	var paraObj = {
		name : $("#name").val(),
		no : $("#no").val(),
		status : $("#status").val(),
		business_type : $('#business_type').val(),
		isCon : 1,
		u : pageObj.u
	};
	var url = approot
			+ '/data?module=before&service=TransNotice&method=getNoticeListData';
	url += '&goodsType=' + pageObj.goodsType;
	pageObj.grid.refresh(url, paraObj);
}
// ---------------------------------
// 重置
// ---------------------------------
pageObj.reset = function() {
	$('#name').val('');
	$("#no").val('');
	$("#status").val(-1);
	$('#business_type').val(-1);
}

// ---------------------------------
// 审核公告
// ---------------------------------
pageObj.noticeCheck = function() {
	var arr = pageObj.grid.getCheckedArr();
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNotice";
	cmd.method = "checkNotice";
	cmd.u = pageObj.u;
	cmd.noticeIds = arr.join(",");
	if(arr.length<1){
		DialogAlert("请选择需要审核的记录！");
		return;
	}
	var selectRow=pageObj.grid.getSelectedRows()
	for(var i=0;i<selectRow.length;i++){
		if(selectRow[i].status==2){
			alert("公告:'"+selectRow[i].name+"'审核失败！公告已经发布!");
			return;
		}
	}
	
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
			DialogAlert(message);
			pageObj.query();
		};
		cmd.execute();
	});
}

//---------------------------------
//发布公告
//---------------------------------
pageObj.noticePublish2 = function() {
	var arr = pageObj.grid.getCheckedArr();
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNotice";
	cmd.method = "checkNotice";
	cmd.u = pageObj.u;
	cmd.noticeIds = arr.join(",");
	cmd.isCon=2;//发布
	cmd.success = function(data) {
		DialogAlert(data.message);
		pageObj.query();
	};
	cmd.execute();
}

//// ---------------------------------
//// 发布
//// ---------------------------------
//pageObj.noticePublish = function() {
//	var arr = pageObj.grid.getCheckedArr();
//	var cmd = new Command();
//	cmd.module = "before";
//	cmd.service = "TransNoticeMine";
//	cmd.method = "publishNotice";
//	cmd.u = pageObj.u;
//	cmd.noticeIds = arr.join(",");
//	cmd.success = function(data) {
//		if (data.state) {
//			DialogAlert(data.message);
//		} else {
//			if (data.message) {
//				DialogAlert(data.message);
//			} else {
//				DialogAlert("发布失败！");
//			}
//		}
//		pageObj.query();
//	};
//	cmd.execute();
//}
// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initPage = function() {
	pageObj.u = getUserId();
	pageObj.goodsType = Utils.getPageValue('goodsType');
	//
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNotice";
	cmd.method = "initNoticeListPageParam";
	cmd.u = pageObj.u;
	cmd.goodsType = pageObj.goodsType;
	cmd.success = function(data) {
		html = "<option value='-1' checked>--请选择--</option>";
		pageObj.businessType = {};
		$(data.businessType).each(function(index, obj) {
					pageObj.businessType[obj.id] = obj.name;
					html += "<option value='" + obj.id + "'>" + obj.name
							+ "</option>";
				});
		$('#business_type').html(html);
	};
	cmd.execute();
	//
	var url = approot
			+ '/data?module=before&service=TransNotice&method=getNoticeListData&u='
			+ pageObj.u + "&business_type=" + pageObj.business_type+ "&isCon=1";
	url += '&goodsType=' + pageObj.goodsType;
	pageObj.grid = $("#grid").ligerGrid({
				url : url,
				columns : [{
					display : '公告编号',
					name : 'name',
					width : '30%',
					render : pageObj.nameRender
				}, {
					display : '公告类型',
					name : 'notice_type',
					width : '19%',
					render : pageObj.renderNoticeType
				}, {
					display : '交易类型',
					name : 'business_type',
					width : '15%',
					render : pageObj.business_typeRender
				}, {
					display : '公告日期',
					name : 'notice_date',
					width : '20%'
				}, {
					display : '状态',
					name : 'status',
					width : '10%',
					render : pageObj.statusRender
				}],
				isScroll : true,// 是否滚动
				pageSizeOptions : [10, 20, 30],
				showTitle : false,
				rownumbers : true,
				selectRowButtonOnly : true,
				height : 435,
				checkbox : true
			});
	//
	var html = "<option value='-1' checked>--请选择--</option>";
	for (var pro in comObj.notice_status) {
		if(pro>=2 && pro!=4){
			html += "<option value='" + pro + "'>"
				+ comObj.notice_status[pro] + "</option>"
		}
	}
	$('#status').html(html);
	//
	$("#btnQuery").click(pageObj.query);
	$('#btnReset').click(pageObj.reset);
	$("#btnCheck").click(pageObj.noticeCheck);
	$("#btnPublish2").click(pageObj.noticePublish2);
}

$(document).ready(function() {
			pageObj.initPage();
			Utils.autoIframeSize();
		});

