var pageObj = {}
pageObj.goodsType= null;
pageObj.renderInOp = function(row, rowNumber) {
	var html = '<a href="javascript:pageObj.apply(\'' + row.id
			+ '\',\''+row.notice_name+'\',\''+row.no+'\')">竞买申请</a>';
	html += '&nbsp;&nbsp;<a href="javascript:pageObj.removeFromCar(\''
			+ row.cart_dtl_id + '\');">移除</a>';
	return html;
}

pageObj.renderOutOp = function(row, rowNumber) {
	var html = '<a href="javascript:pageObj.addToCar(\'' + row.id
			+ '\');">加入购物车</a>';
	html += '&nbsp;&nbsp;<a href="javascript:pageObj.delFromCar(\''
			+ row.cart_dtl_id + '\',\'' + row.no + '\');">删除</a>';
	return html;
}

pageObj.renderBail = function(row, rowNumber) {
	var html = '';
	var cmd = new Command();
	cmd.module = 'bidder';
	cmd.service = 'TransLicense';
	cmd.method = 'earnestMoneyList';
	cmd.targetId = row.id;
	cmd.success = function(data) {
		var rows = data.Rows;
		for (var i = 0; i < rows.length; i++) {
			html += '<span>'+comObj.cf({flag:rows[i].currency,amount:rows[i].amount,unit:row.unit}) + '</span>&nbsp;&nbsp;';
		}
	}
	cmd.execute();
	return html;
}

pageObj.removeFromCar = function(dtlId) {
	DialogConfirm('确定移除?', function(yn) {
				if (!yn)
					return;
				var cmd = new Command();
				cmd.module = "bidder";
				cmd.service = "TransLicense";
				cmd.method = 'removeFromCar';
				cmd.dtlId = dtlId;
				cmd.success = function(data) {
					if (data.state == 1) {
						pageObj.refresh();
					}
				}
				cmd.execute();
			});

}

pageObj.delFromCar = function(dtlId, targetName) {
	DialogConfirm('删除[' + targetName + ']标的,数据将彻底删除！', function(yn) {
				if (!yn)
					return;
				var cmd = new Command();
				cmd.module = "bidder";
				cmd.service = "TransLicense";
				cmd.method = 'deleteCarTarget';
				cmd.dtlId = dtlId;
				cmd.success = function(data) {
					if (data.state == 1) {
						pageObj.refresh();
					}
				}
				cmd.execute();
			});
}

pageObj.addToCar = function(targetId) {
	var cmd = new Command();
	cmd.module = "bidder";
	cmd.service = "TransLicense";
	cmd.method = 'addToCar';
	cmd.targetId = targetId;
	cmd.u = pageObj.u;
	cmd.success = function(data) {
		DialogAlert(data.message);
		if (data.state == 1) {
			pageObj.refresh();
		}
	}
	cmd.execute();
}

pageObj.refresh = function() {
	pageObj.goodsType= Utils.getUrlParamValue(document.location.href, "goodsType");
	pageObj.u = getUserId();
	var opt = {};
	opt.columns = [{
				display : '公告名称',
				name : 'notice_name',
				width : '100'
			}, {
				display : '标的名称',
				name : 'no',
				width : '200'
			}, {
				display : '交易类型',
				name : 'trans_type_name',
				width : '150'
			}, {
				display : '发布时间',
				name : 'begin_notice_time',
				width : '120'
			}, {
				display : '保证金',
				render : pageObj.renderBail,
				width : '200'
			}, {
				display : '操作类型',
				width : '200',
				render : pageObj.renderInOp
			}];
	opt.rownumbers = true;
	opt.isScroll = true;
	opt.usePager = true;
	opt.checkbox = false;
	opt.selectRowButtonOnly = true;
	opt.height = 300;
	opt.url = approot
			+ '/data?module=bidder&service=TransLicense&method=carTargetList'
			+ '&type=1&u=' + pageObj.u;
	pageObj.inCarGrid = $('#inCar').ligerGrid(opt);
	//
	opt.url = approot
			+ '/data?module=bidder&service=TransLicense&method=carTargetList'
			+ '&type=0&u=' + pageObj.u;
	opt.columns = [{
				display : '公告名称',
				name : 'notice_name',
				width : '100'
			}, {
				display : '标的名称',
				name : 'no',
				width : '200'
			}, {
				display : '交易类型',
				name : 'trans_type_name',
				width : '150'
			}, {
				display : '发布时间',
				name : 'begin_notice_time',
				width : '120'
			}, {
				display : '保证金',
				render : pageObj.renderBail,
				width : '200'
			}, {
				display : '操作类型',
				width : '200',
				render : pageObj.renderOutOp
			}];
	pageObj.outCarGrid = $('#outCar').ligerGrid(opt);
	parent.pageObj.refreshCar();
	Utils.autoIframeSize();
}
// -----------------------------
// 竞买申请
// ----------------------------
pageObj.apply = function(targetId,noticeName,targetName) {
	var cmd1 = new Command();
	cmd1.module = "sysman";
	cmd1.service = "User";
	cmd1.method = "checkBlackUser";
	cmd1.u = pageObj.u;
	cmd1.goodsType = pageObj.goodsType;
	cmd1.success = function(data1) {
		if(data1.state!=1){
			DialogAlert(data1.message);
			return;
		}
		var cmd = new Command();
		cmd.module = "bidder";
		cmd.service = "Apply";
		cmd.method = 'checkCarTarget';
		cmd.targetId = targetId;
		cmd.u = pageObj.u;
		cmd.success = function(data) {
			if (data.state == 1) {
				var obj = {};
				// 地址
				obj.url = "applyMain.html";
				// 参数
				obj.param = {
					u : pageObj.u,
					targetId : targetId,
					noticeName:noticeName,
					targetName:targetName,
					goodsType:pageObj.goodsType,
					userName:getUserInfoObj().displayName
				};
				// 窗口参数
				obj.feature = "dialogWidth=1010px;dialogHeight=600px";
				var returnValue = DialogModal(obj);
				if (returnValue) {
					pageObj.refresh();
				}
			} else {
				DialogAlert(data.message);
			}
		}
		cmd.execute();
	}
	cmd1.execute();
}

$(document).ready(pageObj.refresh);
