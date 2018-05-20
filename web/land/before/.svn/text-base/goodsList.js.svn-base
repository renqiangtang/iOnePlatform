var pageObj = {};

pageObj.ok = function() {
	var goodsIdArr = pageObj.goodsGrid.getCheckedArr();
	if (goodsIdArr.length == 0){
		DialogAlert('请选择需要绑定的地块！');
		return;
	}
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsLand";
	cmd.method = "doBoundGoods";
	cmd.targetId = pageObj.targetId;
	cmd.u = pageObj.u;
	cmd.goodsIds = goodsIdArr.join(",");
	cmd.success = function(data) {
		//DialogAlert(data.message);
		if (data.state) {
			window.returnValue = true;
			window.close();
		}
	}
	cmd.execute();
}

pageObj.renderMemo = function(row, rowNamber) {
	var html = '';
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsLand";
	cmd.method = "getGoodsTargetList";
	cmd.goodsId = row.id;
	cmd.success = function(data) {
		if ('Rows' in data) {
			var rows = data.Rows;
			$(rows).each(function(index, obj) {
						if (index > 0)
							html += '\n';
						html += obj.no + "[" + comObj.targetStatusObj[obj.status] + "]";
						if(obj.is_stop == 1){
							html +="<font color='red'>(终止)</font>";
						}else if(obj.is_suspend == 1){
							html +="<font color='red'>(中止)</font>";
						}

					});
		}
	}
	cmd.execute();
	return '<div title="' + html + '">' + html + '</div>';
}

// -----------------------------
// 渲染交易物-地块信息
// ----------------------------
pageObj.renderNo = function(row, rowNamber) {
	var html = '<a href="javascript:void(0);" onclick="javascript:pageObj.viewGoods(\'' + row.id + '\');return false;">' + row.no + '</a>';
	return html;
}

// -----------------------------
// 查看交易物
// ----------------------------
pageObj.viewGoods = function(goodsId) {
	var obj = {};
	// 地址
	obj.url = pageObj.goodsType + ".html";
	// 参数
	obj.param = {
		mode : 'modify',
		u : pageObj.u,
		goodsId : goodsId
	};
	// 窗口参数
	obj.feature = "dialogWidth=800px;dialogHeight=600px";
	var returnValue = DialogModal(obj);
	if (returnValue)
		pageObj.query();
}

pageObj.initPage = function() {
	//
	var url = approot + '/data?module=before&service=TransGoodsLand&method=getTransGoodsList&u=' + pageObj.u + '&mode='
			+ pageObj.mode;
	if (pageObj.targetId)
		url += '&targetId=' + pageObj.targetId;
	url += '&goods_type=' + pageObj.goodsType;
	pageObj.goodsGrid = $('#goodsGrid').ligerGrid({
				columns : [{
							display : comObj.target_name[pageObj.goodsType],
							width : 200,
							name : 'no',
							render : pageObj.renderNo
						}, {
							display : '行政辖区',
							width : 100,
							name : 'canton_name'
						}, {
							display : '交易物类型',
							width : 100,
							name : 'goods_type_name'
						}, {
							display : '经办人',
							width : 100,
							name : 'user_name'
						}, {
							display : '标的绑定信息',
							width : 200,
							render : pageObj.renderMemo
						}, {
							display : '创建时间',
							width : 200,
							name : 'create_date'
						}, {
							display : '用途',
							width : 200,
							name : 'goods_use'
						}],
				rownumbers : true,
				isScroll : true,
				usePager : true,
				checkbox : true,
				selectRowButtonOnly : true,
				height : 400,
				url : url
			});
	// 用途
	// $('#goods_use').empty();
	// html = "<option value='' checked>--请选择--</option>";
	// for (var pro in comObj.use_type) {
	// html += "<option value='" + pro + "'>" + comObj.use_type[pro]
	// + "</option>"
	// }
	// $('#goods_use').html(html);
	$('#goods_use').html('');
	//
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsLand";
	cmd.method = "initGoodsListQueryParam";
	cmd.u = pageObj.u;
	cmd.success = function(data) {
//		var html = '<option value="" checked>--请选择--</option>';
//		for (var i = 0; i < data.canton.length; i++) {
//			html += '<option value="' + data.canton[i].id + '">' + data.canton[i].name + '</option>';
//		}
//		$('#canton_id').html(html);
		//
		html = '<option value="" checked>--请选择--</option>';
		for (var i = 0; i < data.goodsType.length; i++) {
			html += '<option value="' + data.goodsType[i].id + '">' + data.goodsType[i].name + '</option>';
		}
		$('#goods_type').html(html);
	};
	cmd.execute();
}

pageObj.reset = function() {
//	$('#canton_id').val('');
	$('#no').val('');
	$('#goods_type').val('');
	$('#goods_use').val('');
}

pageObj.query = function() {
	var para = {};
	Utils.getForm(para);
	para.u = pageObj.u;
	para.mode = pageObj.mode;
	para.goods_type = pageObj.goodsType;
	para.targetId=pageObj.targetId;
	var url = approot + '/data?module=before&service=TransGoodsLand&method=getTransGoodsList';
	pageObj.goodsGrid.refresh(url, para);
}

pageObj.goodsNew = function() {
	var obj = {};
	// 地址
	obj.url = pageObj.goodsType + ".html";
	// 参数
	if(pageObj.mode=='relView'){
		obj.param = {
			mode : 'new',
			u : Utils.getPageValue('u'),
			targetId : pageObj.targetId
		};
	}else{
		obj.param = {
			mode : 'new',
			u : pageObj.u
		};
	}
	// 窗口参数
	obj.feature = "dialogWidth=800px;dialogHeight=600px";
	var returnValue = DialogModal(obj);
	if (returnValue)
		pageObj.query();
}

pageObj.goodsDel = function() {
	var goodsIdArr = pageObj.goodsGrid.getCheckedArr();
	if (goodsIdArr.length == 0){
		DialogAlert('请选择至少一条数据进行删除!');
		return;
	}
	DialogConfirm('确定要将这' + goodsIdArr.length + '条删除?', function(yn) {
				if (!yn)
					return;
				var cmd = new Command();
				cmd.module = "before";
				cmd.service = "TransGoodsLand";
				cmd.method = 'deleteTransGoodses';
				cmd.goodsIds = goodsIdArr.join(",");
				cmd.success = function(data) {
					if (data.state == 0)
						DialogAlert(data.message);
					else
						pageObj.query();
				}
				cmd.execute();
			});
}

pageObj.delRelGoods = function() {
	var goodsIdArr = pageObj.goodsGrid.getCheckedArr();
	if (goodsIdArr.length == 0){
		DialogAlert('请选择一条数据进行解除捆绑!');
		return;
	}
	if (!confirm("确定解除这个地块的捆绑?"))
		return;
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsLand";
	cmd.method = "doUnBoundGoods";
	cmd.targetId = pageObj.targetId;
	cmd.goodsIds = goodsIdArr.join(",");
	cmd.success = function(data) {
		DialogAlert(data.message);
		pageObj.query();
	}
	cmd.execute();

}

pageObj.boundGoods = function() {
	var obj = {};
	// 地址
	obj.url = "goodsList.html";
	// 参数
	obj.param = {
		u : Utils.getPageValue('u'),
		mode : comObj.mode['view'],
		targetId : pageObj.targetId,
		goodsType : pageObj.goodsType
	};
	// 窗口参数
	obj.feature = "dialogWidth=900px;dialogHeight=550px";
	var returnValue = DialogModal(obj);
	if (returnValue) {
		DialogAlert('绑定地块成功！');
		pageObj.query();
	}
}

$(document).ready(function() {
			pageObj.mode = Utils.getPageValue('mode');
			pageObj.goodsType = Utils.getPageValue('goodsType');
			if (!pageObj.mode)
				pageObj.mode = 'modify';
			pageObj.targetId = Utils.getPageValue('targetId');
			pageObj.u = getUserId();
			pageObj.callback = Utils.getPageValue('callback');
			
			//
			$('#noTitleDiv').html(comObj.target_name[pageObj.goodsType] + "：");
			$('#goodsUserTitleDiv').html(comObj.goodsUser[pageObj.goodsType] + "：");
			//
			switch (pageObj.mode) {
				case comObj.mode['modify'] :
					$('#btnOk').hide();
					$('#btnDelRelGoods').hide();
					$('#boundImg').hide();
					break;
				case comObj.mode['view'] :
					$('#btnReset').hide();
//					$('#btnNewGoods').hide();
					$('#btnDelGoods').hide();
					$('#btnDelRelGoods').hide();
					$('#boundImg').hide();
					break;
				case 'relView' ://新增或修改标地信息时点击地块信息
					$('#btnReset').hide();
					$('#btnDelGoods').hide();
					$('#btnOk').hide();
					break;
				default :
					DialogAlert("mode is null");
			}
			$('#btnOk').click(pageObj.ok);
			$('#btnReset').click(pageObj.reset);
			$('#btnQuery').click(pageObj.query);
			$('#btnNewGoods').click(pageObj.goodsNew);
			$('#btnDelGoods').click(pageObj.goodsDel);
			$('#btnDelRelGoods').click(pageObj.delRelGoods);//解除捆绑
			$('#boundImg').click(pageObj.boundGoods);//绑定地块
			pageObj.initPage();
			//Utils.autoIframeSize();
			
		});