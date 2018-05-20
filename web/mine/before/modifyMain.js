var pageObj = {};
pageObj.editHtml = {};
pageObj.editHtml['公告'] = 'noticeEdit.html';
pageObj.editHtml['标的'] = 'targetEdit.html';
pageObj.editHtml['宗地'] = '101.html';
pageObj.editHtml['探矿权'] = 'prospect301.html';
pageObj.editHtml['采矿权'] = 'mine301.html';


pageObj.renderOp = function(row, rowNamber) {
	var op = row.operate;
	var html = '';
	if (op <= 1) {
		html += '&nbsp;<a href="javascript:pageObj.edit(\'' + row.infoType + '\',\'' + row.id + '\',\'' + row.goodsType
				+ '\')">修改</a>';
	}
	if (op <= 0) {
		html += '&nbsp;<a href="javascript:pageObj.cancel(\'' + row.infoType + '\',\'' + row.id + '\',\''
				+ row.goodsType + '\')">撤销</a>';
	}
	return html;
}

pageObj.renderName = function(row, rowNamber) {
	var name = row.name;
	var nameEnd = '';
	if(row.infoType == '标的'){
		if(row.is_suspend == 1){
			nameEnd = '中止';
		}
		if(row.is_stop == 1){
			nameEnd = '终止';
		}
		if(nameEnd != null && nameEnd != ''){
			nameEnd = '<font color="red">('+nameEnd+')</font>';
		}
	}
	return name+nameEnd;
}

pageObj.cancel = function(infoType, id, goodsType) {
	DialogConfirm('确定要撤销该公告?', function(yn) {
		if (!yn)
			return;
		var cmd = new Command("before", "TransModifyMine", "cancelNotice");
		cmd.noticeId = id;
		cmd.success = function(data) {
			DialogAlert(data.message);
			if (data.state == 1)
				location.reload(true);
		}
		cmd.execute();
	});
}

pageObj.edit = function(infoType, id, goodsType) {
	var obj = {};
	// 地址
	obj.url = pageObj.editHtml[infoType];
	// 参数
	obj.param = {
		mode : 'update',
		goodsType : goodsType,
		u : getUserId()
	};
	switch (obj.url) {
		case pageObj.editHtml['公告'] :
			obj.param.noticeId = id;
			break;
		case pageObj.editHtml['标的'] :
			obj.param.targetId = id;
			break;
		case pageObj.editHtml['宗地'] :
			obj.param.goodsId = id;
			break;
		case pageObj.editHtml['探矿权'] :
			obj.param.goodsId = id;
			break;
		case pageObj.editHtml['采矿权'] :
			obj.param.goodsId = id;
			break;
	}
	// 窗口参数
	var sheight = screen.height - 250;
	var swidth = screen.width - 10;
	obj.feature = 'dialogWidth=' + swidth + 'px;dialogHeight=' + sheight + 'px';
	DialogModal(obj);

}
//
pageObj.initPage = function() {
	var url = approot + '/data?module=before&service=TransModifyMine&method=getDatas&goodsType='+pageObj.goodsType;
	pageObj.grid = $('#grid').ligerGrid({
				url : url,
				columns : [{
							display : '名称',
							name : 'name',
							width : 500,
							align : 'left',
							render : pageObj.renderName
						}, {
							display : '类型',
							name : 'infoType',
							width : 100
						}, {
							display : '操作',
							width : '20%',
							render : pageObj.renderOp
						}],
				tree : {
					columnName : 'name'
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

//
$(document).ready(function() {
			pageObj.goodsType = Utils.getPageValue('goodsType');
			pageObj.initPage();
		});