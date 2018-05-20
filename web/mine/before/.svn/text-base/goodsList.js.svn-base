var pageObj = {};

pageObj.ok = function() {
	var goodsIdArr = pageObj.goodsGrid.getCheckedArr();
	if (goodsIdArr.length == 0){
		DialogAlert('请选择需要绑定的矿区！');
		return;
	}
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsMine";
	cmd.method = "doBoundGoods";
	cmd.targetId = pageObj.targetId;
	cmd.u = pageObj.u;
	cmd.goodsIds = goodsIdArr.join(",");
	cmd.success = function(data) {
		DialogAlert(data.message);
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
	cmd.service = "TransGoodsMine";
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
// 渲染交易物-宗地信息
// ----------------------------
pageObj.renderNo = function(row, rowNamber) {
	var html = '<a href="javascript:void(0);" onclick="javascript:pageObj.viewGoods(\'' + row.id + '\',\'' + row.goods_type + '\');return false;">' + row.no + '</a>';
	return html;
}

// -----------------------------
// 查看交易物
// ----------------------------
pageObj.viewGoods = function(goodsId,goodsType) {
	var obj = {};
	// 地址
	if(goodsType=="301") {
		obj.url = "prospect301.html";
		obj.feature = "dialogWidth=800px;dialogHeight=579px";// 窗口参数
	}else{
		obj.url = "mine301.html";
		obj.feature = "dialogWidth=800px;dialogHeight=650px";
	}
	// 参数
	obj.param = {
		mode : 'modify',
		u : pageObj.u,
		goodsId : goodsId,
		goodsType : goodsType
	};
	var returnValue = DialogModal(obj);
	if (returnValue)
		pageObj.query();
}

pageObj.initPage = function() {
	//
	var url = approot + '/data?module=before&service=TransGoodsMine&method=getTransGoodsList&u=' + pageObj.u + '&mode='
			+ pageObj.mode;
	if (pageObj.targetId)
		url += '&targetId=' + pageObj.targetId;
	url += '&goods_type=' + pageObj.goodsType;
	pageObj.goodsGrid = $('#goodsGrid').ligerGrid({
				columns : [{
							//display : comObj.target_name[pageObj.goodsType],
							display : comObj.target_name["301"],
							width : 200,
							name : 'no',
							render : pageObj.renderNo
						}, {
							display : '矿区位置',
							width : 100,
							name : 'address'
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
							display : '矿种',
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
	$('#goods_use').html('');
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsMine";
	cmd.method = "initGoodsListQueryParam";
	cmd.u = pageObj.u;
	cmd.success = function(data) {
//		var html = '<option value="" checked>--请选择--</option>';
//		for (var i = 0; i < data.canton.length; i++) {
//			html += '<option value="' + data.canton[i].id + '">' + data.canton[i].name + '</option>';
//		}
//		$('#canton_id').html(html);
//		html = '<option value="" checked>--请选择--</option>';
//		for (var i = 0; i < data.user.length; i++) {
//			html += '<option value="' + data.user[i].id + '">' + data.user[i].user_name + '</option>';
//		}
//		$('#create_user_id').html(html);
	};
	cmd.execute();
	
}


pageObj.reset = function() {
//	$('#canton_id').val('');
//	$('#canton').val('');
	$('#no').val('');
	$('#address').val('');
	$('#goods_use').val('');
	$('#goods_type').val(pageObj.goodsType);
}

pageObj.query = function() {
//	if($('#canton').val().trim()==""){
//		$('#canton_id').val('');
//	}
	var para = {};
	Utils.getForm(para);
	para.u = pageObj.u;
	para.mode = pageObj.mode;
	if(pageObj.goodsType=='301' || pageObj.goodsType=='401'){
		para.goods_type = pageObj.goodsType;
	}else{
		para.goods_type = $('#goods_type').val();
	}
//	para.canton_id = $('#canton_id').val();
	if($('#goods_type').val()=="")
		para.goods_type = pageObj.goodsType;
	para.targetId=pageObj.targetId;
	var url = approot + '/data?module=before&service=TransGoodsMine&method=getTransGoodsList';
	pageObj.goodsGrid.refresh(url, para);
}
pageObj.goodsNew = function(event) {
	var obj = {};
	if(event.data.a=="prospect301") {
		obj.url = "prospect301.html";
		obj.feature = "dialogWidth=800px;dialogHeight=579px";// 窗口参数
	}else{
		obj.url = "mine301.html";
		obj.feature = "dialogWidth=800px;dialogHeight=650px";
	}
	// 参数
	if(pageObj.mode=='relView'){
		obj.param = {
			mode : 'new',
			u : Utils.getPageValue('u'),
			targetId : pageObj.targetId,
			goodsType : event.data.b
		};
	}else{
		obj.param = {
			mode : 'new',
			u : pageObj.u,
			goodsType : event.data.b
		};
	}
	var returnValue = DialogModal(obj);
	if (returnValue)
		pageObj.query();
}


pageObj.goodsDel = function() {
	var goodsIdArr = pageObj.goodsGrid.getCheckedArr();
	if (goodsIdArr.length == 0){
		DialogAlert('请选择需要删除的记录！');
		return;
	}
	DialogConfirm('确定要将这' + goodsIdArr.length + '条删除?', function(yn) {
				if (!yn)
					return;
				var cmd = new Command();
				cmd.module = "before";
				cmd.service = "TransGoodsMine";
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
		DialogAlert('请选择需要解绑的条目！');
		return;
	}
	if (!confirm("确定解除以下"+goodsIdArr.length+"个矿区的捆绑?"))
		return;
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsMine";
	cmd.method = "doUnBoundGoods";
	cmd.targetId = pageObj.targetId;
	//cmd.goodsIds = goodsIdArr[0];
	cmd.goodsIds = goodsIdArr.join(",");
	cmd.success = function(data) {
		DialogAlert(data.message);
		pageObj.query();
	}
	cmd.execute();
}

//pageObj.goods_use = function(){
//	// 矿种
//	var cmd = new Command();
//	cmd.module = "before";
//	cmd.service = "TransGoodsMine";
//	cmd.method = "getGoodsUseListData";
//	cmd.goodsId = pageObj.goodsId;
//	cmd.u = pageObj.u;
//	cmd.success = function(data) {
//		$("#goods_use").empty();
//		$("#goods_use").append("<option value='' checked>--请选择--</option>");
//		for ( var i = 0; i < data.goodsuse.length; i++) {
//			$("#goods_use").append("<option value='" + data.goodsuse[i]["name"] + "'>" + data.goodsuse[i]["name"] + "</option>");
//		}
//	};
//	cmd.execute();
//}

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
		DialogAlert('矿区捆绑成功！');
		pageObj.query();
	}
}

pageObj.selectCanton = function () {
	var cantonObj = comObj.cantonSelect('txtCantonId', 'txtCantonName');
	if (cantonObj) {
		$("#txtCantonName").val(cantonObj.fullName);
		$("#txtCantonId").val(cantonObj.id);
	}
}
$(document).ready(function() {
			$('#cantonBtn').click(pageObj.selectCanton);//初始化“选择”区域按扭事件
			pageObj.goodsType = Utils.getPageValue('goodsType');
			pageObj.mode = Utils.getPageValue('mode');
			if (!pageObj.mode)
				pageObj.mode = 'modify';
			pageObj.targetId = Utils.getPageValue('targetId');
			pageObj.u = getUserId();
			pageObj.callback = Utils.getPageValue('callback');
			switch (pageObj.mode) {
				case comObj.mode['modify'] :
					$('#btnOk').hide();
					$('#btnDelRelGoods').hide();
					$('#boundImg').hide();
					break;
				case comObj.mode['view'] :
					$('#btnReset').hide();
					if(pageObj.goodsType=="301"){
						$('#btnNewGoodsC').hide();
					}else{
						$('#btnNewGoodsT').hide();
					}
					$('#btnDelGoods').hide();
					$('#btnDelRelGoods').hide();
					$('#boundImg').hide();
					break;
				case 'relView' ://新增或修改标地信息时点击矿区信息
					$('#goods_type1').hide();
					$('#goods_type').hide();
					if(pageObj.goodsType=="301"){
						$('#btnNewGoodsC').hide();
					}else{
						$('#btnNewGoodsT').hide();
					}
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
//			$('#btnNewGoodsT').click(pageObj.goodsNewt);
//			$('#btnNewGoodsC').click(pageObj.goodsNewc);
			$("#btnNewGoodsT").bind("click",{a:"prospect301",b:"301"},pageObj.goodsNew);
			$("#btnNewGoodsC").bind("click",{a:"mine301",b:"401"},pageObj.goodsNew);
			$('#btnDelRelGoods').click(pageObj.delRelGoods);//解除捆绑
			$('#btnDelGoods').click(pageObj.goodsDel);
			$('#boundImg').click(pageObj.boundGoods);//绑定矿区
			pageObj.initPage();
//			pageObj.goods_use();
			Utils.autoIframeSize();
		});