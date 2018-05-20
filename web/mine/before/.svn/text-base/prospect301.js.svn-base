var pageObj = {};
pageObj.attachLoaded = false;

pageObj.save = function() {
	if (!Utils.requiredCheck())
		return;
	var years = parseFloat($('#mineral_use_years').val().trim());
//	if (!years.nDigits(1)) {
//		DialogAlert("使用年限最多保留1位小数!");
//		$('#mineral_use_years').focus();
//		return false;
//	}
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsMine";
	cmd.method = "saveTransGoods";
	cmd.mode = pageObj.mode;
	cmd.targetId = pageObj.targetId;
	cmd.goodsId = pageObj.goodsId;
	cmd.goods_type = pageObj.goodsType;
	cmd.u = pageObj.u;
	cmd.canton_id = $("#txtCantonId").val();
	Utils.getForm(cmd);
	cmd.success = function(data) {
		DialogAlert(data.message);
		if (data.state == 1) {
			if(typeof(data.mode)!="undefined"){
				pageObj.mode = data.mode;
			}else{
				pageObj.mode = 'modify';
			}
			pageObj.goodsId = data.goodsId;
			pageObj.btnDis();
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
		pageObj.btnDis();
		$("#txtCantonName").val(data["canton"]);
		$("#txtCantonId").val(data["canton_id"]);
	};
	cmd.execute();
}


// ---------------------------------
// 加载附件
// ---------------------------------
pageObj.initAttachList = function() {
	if (!pageObj.attachLoaded) {
		if (pageObj.goodsId) {
			var initWidth = 800;
			var initHeight = 410;
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
			obj.param = {};
			obj.param.u=pageObj.u;
			obj.param.owners=encodeURIComponent(JSON.stringify(owners));;
			
			// 地址
			obj.url =approot + '/sysman/attachList.html';
			
			// 窗口参数
			obj.feature = "dialogWidth=900px;dialogHeight=600px";
			var rv = DialogModal(obj);
		}
	}
}

pageObj.btnDis = function (){
	if (pageObj.goodsId != null && pageObj.goodsId != "") {
		$("#btnSave").removeAttr("disabled");
		$("#relAttach").removeAttr("disabled");
	}else{
		$("#btnSave").removeAttr('disabled');
		$("#relAttach").attr('disabled',"true");
	}
}

pageObj.initPage = function() {
	pageObj.mode = Utils.getPageValue("mode");
	pageObj.targetId = Utils.getPageValue("targetId");
	pageObj.goodsId = Utils.getPageValue("goodsId");
	pageObj.u = Utils.getPageValue("u");
	pageObj.goodsType = Utils.getPageValue("goodsType");
	//矿种
//	pageObj.goods_use();
	
	
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
}

pageObj.goods_use = function(){
	// 矿种
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsMine";
	cmd.method = "getGoodsUseListData";
	cmd.goodsId = pageObj.goodsId;
	cmd.u = pageObj.u;
	cmd.success = function(data) {
		$("#goods_use").empty();
		Utils.addOption('goods_use', "", "--请选择--");
		for ( var i = 0; i < data.goodsuse.length; i++) {
			Utils.addOption('goods_use',  data.goodsuse[i]["name"], data.goodsuse[i]["name"] );
		}
	};
	cmd.execute();
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
pageObj.selectCanton = function () {
	var cantonObj = comObj.cantonSelect('txtCantonId', 'txtCantonName');
	if (cantonObj) {
		$("#txtCantonName").val(cantonObj.fullName);
		$("#txtCantonId").val(cantonObj.id);
	}
}
$(document).ready(function() {
	pageObj.initPage();
	$('#relAttach').click(pageObj.initAttachList);
	$('#cantonBtn').click(pageObj.selectCanton);//初始化“选择”区域按扭事件
	$('#mineral_area').focus(pageObj.mineral);
	$('#mineral_area').keyup(pageObj.mineral);
});