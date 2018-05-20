var pageObj = {};
pageObj.attachLoaded = false;

pageObj.save = function() {
	if (!Utils.requiredCheck())
		return;
	if (!pageObj.mineral_levelCheck())
		return;
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsMine";
	cmd.method = "saveTransGoods";
	cmd.mode = pageObj.mode;
	cmd.targetId = pageObj.targetId;
	cmd.goodsId = pageObj.goodsId;
	cmd.goods_type = pageObj.goodsType;
	cmd.u = pageObj.u;
	Utils.getForm(cmd);
	cmd.success = function(data) {
		DialogAlert(data.message);
		if (data.state == 1) {
			pageObj.mode = 'modify';
			pageObj.goodsId = data.goodsId;
			window.returnValue = true;
		}
	};
	cmd.execute();
}

pageObj.refresh = function() {
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsMine";
	cmd.method = "getTransGoodsData";
	cmd.goodsId = pageObj.goodsId;
	cmd.success = function(data) {
		Utils.setForm(data);
	};
	cmd.execute();
}

// -------------------------------------------
// 初始化标签页
// -------------------------------------------
pageObj.initTab = function() {
	pageObj.tabManager = $("#tabPanel").ligerTab({
		contextmenu : false,
		onAfterSelectTabItem : pageObj.afterSelectTabItem
	});
}

// ---------------------------------
// 标签TAB切换后事件
// ---------------------------------
pageObj.afterSelectTabItem = function(tabid) {
	pageObj.activeTabId = tabid;
	if (tabid == 'tabitem1') {// 矿区信息，不作任何操作

	} else if (tabid == 'tabitem2') {// 附件
		if (!pageObj.attachLoaded) {
			if (pageObj.goodsId) {
				$("#noneGoodsPrompt").hide();
				$("#attachPanel").show();
				var initWidth = 800;
				var initHeight = 470;
				var grant = {add: 2, edit: 2, del: 2,
					addDtl: 2, editDtl: 2, delDtl: 2,
					uploadFile: 2, downloadFile: 2, delFile: 2};
				var owners = new Array();
				var owner = {};
				owner.id = pageObj.goodsId;
				owner.name = "交易物";
				owner.title = $("#no").val();
				owner.tableName = "trans_goods";
				owner.templetNo = "businessType301001GoodsAttach";
				owner.grant = grant;
				owners.push(owner);
				var url = approot + '/sysman/attachList.html?u=' + pageObj.u + '&owners=' + encodeURIComponent(JSON.stringify(owners)) + '&width=' + initWidth + '&height=' + initHeight;
				var attachFrame = $('#attachFrame');
				if (attachFrame.length == 0) {
					$('#attachPanel').html('<iframe id="attachFrame" src="' + url + '" style="height:' + initHeight + 'px;width:' + initWidth + 'px"></iframe>');
				} else {
					attachFrame.attr('src', url);
				}
				pageObj.attachLoaded = true;
			} else {
				$("#attachPanel").hide();
				$("#noneGoodsPrompt").show();
			}
		}
	}
}

pageObj.initPage = function() {
	pageObj.mode = Utils.getPageValue("mode");
	pageObj.targetId = Utils.getPageValue("targetId");
	pageObj.goodsId = Utils.getPageValue("goodsId");
	pageObj.u = Utils.getPageValue("u");
	pageObj.goodsType = Utils.getPageValue("goodsType");

	// 区域
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsMine";
	cmd.method = "cantonList";
	cmd.goodsId = pageObj.goodsId;
	cmd.u = pageObj.u;
	cmd.success = function(data) {
		$("#canton_id").empty();
		$("#canton_id").append("<option value='' checked>--请选择--</option>");
		for ( var i = 0; i < data.canton.length; i++) {
			$("#canton_id").append("<option value='" + data.canton[i][0] + "'>" + data.canton[i][1] + "</option>");
		}
	};
	cmd.execute();

	switch (pageObj.mode) {
	case comObj.mode['new']:
		break;
	case comObj.mode['update']:
	case comObj.mode['modify']:
		pageObj.refresh();
		break;
	default:
		DialogAlert("mode is null");
	}

	// 
	$('#btnSave').click(pageObj.save);
	pageObj.initTab();
}

pageObj.mineral = function() {
	var msg = '';
	var v = $('#mineral_area').val();
	v = parseFloat(v);
	if (v.nDigits(4)) {
		v = v.mul(1500);
		if (!isNaN(v))
			msg = v + "亩";
	} else {
		msg = '最多保留四位小数';
		$('#mineral_areaInfo').html(msg);
		return false;
	}
	$('#mineral_areaInfo').html(msg);
	return true;
}

// -------------------------------------------
// 开采标高 检查
// -------------------------------------------
pageObj.mineral_levelCheck = function() {
	var v1 = $('#mineral_level1').val().trim();
	var v2 = $('#mineral_level2').val().trim();
	if (v1 && v2) {
		v1 = parseFloat(v1);
		v2 = parseFloat(v2);
		if (v1 <= v2) {
			var msg = '开采标高1必须大于开采标高2';
			DialogAlert(msg);
			return false;
		}
	}
	return true;
}

pageObj.mineral_storageUnitChange=function(){
	var value=$('#mineral_storageUnit').val();
	$('#mineral_production_scaleUnit').val(value+"/年");
}

$(document).ready(function() {
	pageObj.initPage();
	$('#mineral_area').focus(pageObj.mineral);
	$('#mineral_area').keyup(pageObj.mineral);
	$('#mineral_storageUnit').change(pageObj.mineral_storageUnitChange);
});