var pageObj = {};

pageObj.initPage = function() {
	var url = approot + '/data?module=before&service=TransNotice&method=noticeTreeList&u='+ pageObj.u + "&goodsType=" + pageObj.goodsType;;
	pageObj.grid = $('#grid').ligerGrid({
				url : url,
				columns : [{display : '编号',name : 'no',width : '28%',render : pageObj.nameRender}, 
//				           {display : '编号',name : 'no',width : '18%'}, 
				           {display : '公告类型',name : 'notice_type',width : '13%',render : pageObj.renderNoticeType}, 
				           {display : '交易类型',name : 'business_type_name',width : '13%'}, 
				           {display : '公告日期',name : 'notice_date',width : '10%'}, 
				           {display : '标的数',name : 'target_acount',width : '8%'},
				           {display : '状态',name : 'status',width : '8%',render : pageObj.statusRender},
				           {display : '操作',name : 'status',width : '20%',render : pageObj.opRender}
				          ],
				tree : {
					columnName : 'no'
				},
				isScroll : true,// 是否滚动
				selectRowButtonOnly : true,
				autoCheckChildren : false,
				usePager : false,
				onAfterShowData : function(data) {
					var g = pageObj.grid;
					for (var i = g.rows.length - 1; i >= 0; i--) {
						var targetRowObj = g.getRowObj(g.rows[i]);
						var linkbtn = $(".l-grid-tree-link", targetRowObj);
						if (linkbtn.hasClass("l-grid-tree-link-close"))
							continue;
						g.toggle(g.rows[i]);
					}
				}
			});
}

pageObj.renderNoticeType = function(row, rowNumber) {
	return comObj.notice_type[row.notice_type + ''];
}

pageObj.statusRender = function(row, rowNumber) {
	return comObj.notice_status[row.status + ''];
}
pageObj.nameRender = function(row, rowNumber) {
	return '<a href="javascript:pageObj.noticeEdit(\'' + row.id + '\',\'' + row.notice_type + '\')">'
		+ row.no + '</a>';
}
pageObj.opRender = function(row, rowNamber) {
	var html = '&nbsp;<a href="javascript:pageObj.addSupplement(\'' + row.id + '\')">创建补充公告</a>';
	if(row.notice_type != 0){
		if(row.status == 2){
			html += '&nbsp;<a href="javascript:pageObj.cancelSupplement(\'' + row.id + '\')">撤销发布</a>';
		}else{
			html += '&nbsp;<a href="javascript:pageObj.deleteSupplement(\'' + row.id + '\')">删除</a>';
			html += '&nbsp;<a href="javascript:pageObj.publishSupplement(\'' + row.id + '\')">发布</a>';
		}
	}
	return html;
}

pageObj.addSupplement = function(noticeId){
	var obj = {};
	// 地址
	obj.url = "noticeSupplementEdit.html";
	// 参数
	obj.param = {
		u : pageObj.u,
		parentId : noticeId,
		mode :'new'
	};

	// 窗口参数
	var sheight = screen.height - 100;
	var swidth = screen.width - 10;
	obj.feature = 'dialogWidth=' + swidth + 'px;dialogHeight=' + sheight + 'px';
	var returnValue = DialogModal(obj);
	if (returnValue)
		pageObj.query();
}

pageObj.deleteSupplement = function(noticeId){
	DialogConfirm('确定要将该补充公告删除?', function(yn) {
		if (!yn)
			return;
		var cmd = new Command();
		cmd.module = "before";
		cmd.service = "TransNotice";
		cmd.method = "deleteNotice";
		cmd.noticeIds = noticeId;
		cmd.u = pageObj.u;
		cmd.success = function(data) {
			DialogAlert(data.message);
			if (data.state)
				pageObj.query();
		};
		cmd.execute();
	});
}

pageObj.publishSupplement = function(noticeId){
	DialogConfirm('确定要将该补充公告发布?', function(yn) {
		if (!yn)
			return;
		var cmd = new Command();
		cmd.module = "before";
		cmd.service = "TransNotice";
		cmd.method = "publishNotice";
		cmd.u = pageObj.u;
		cmd.noticeIds = noticeId;
		cmd.success = function(data) {
			DialogAlert(data.message);
			pageObj.query();
		};
		cmd.execute();
	});
}


pageObj.cancelSupplement = function(noticeId){
	DialogConfirm('确定要撤销发布该补充公告?', function(yn) {
		if (!yn)
			return;
		var cmd = new Command();
		cmd.module = "before";
		cmd.service = "TransModify";
		cmd.method = "cancelNotice";
		cmd.u = pageObj.u;
		cmd.noticeId = noticeId;
		cmd.success = function(data) {
			DialogAlert(data.message);
			pageObj.query();
		};
		cmd.execute();
	});
}

pageObj.noticeEdit = function(noticeId , noticeType){
	var obj = {};
	// 地址
	if(noticeType == 0){
		obj.url = "noticeEdit.html";
	}else if(noticeType == 1){
		obj.url = "noticeSupplementEdit.html";
	}
	
	// 参数
	obj.param = {
			u : pageObj.u,
			noticeId : noticeId,
			goodsType : pageObj.goodsType
	};
	if (noticeId) {
		obj.param.noticeId = noticeId;
		obj.param.mode = 'modify';
	} else {
		obj.param.mode = 'new';
	}

	// 窗口参数
	var sheight = screen.height - 100;
	var swidth = screen.width - 10;
	obj.feature = 'dialogWidth=' + swidth + 'px;dialogHeight=' + sheight + 'px';
	var returnValue = DialogModal(obj);
	if (returnValue)
		pageObj.query();
}

pageObj.query = function() {
	var paraObj = {
		u : pageObj.u
	};
	var url = approot+ '/data?module=before&service=TransNotice&method=noticeTreeList&goodsType=' + pageObj.goodsType;
	pageObj.grid.refresh(url, paraObj);
}

$(document).ready(function() {
			pageObj.u = getUserId();
			pageObj.goodsType = Utils.getPageValue('goodsType');
			pageObj.initPage();
});