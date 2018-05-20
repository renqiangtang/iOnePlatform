//---------------------------------
//页对象
//---------------------------------
var pageObj = {};
pageObj.goodsExists = false;

//---------------------------------
//查询交易物数据
//---------------------------------
pageObj.queryGoodsData = function () {
	var cmd = new Command();
	cmd.module = "trademine";
	cmd.service = "ViewTargetMine";
	cmd.method = "getTargetGoodsData";
	cmd.targetId = pageObj.targetId;
	cmd.success = function(data) {
		if (data.state == 1) {
			pageObj.goodsExists = data.rs.length > 0;
			$("#goodsPanel101").hide();
			$("#goodsPanel301").hide();
			$("#goodsPanel401").hide();
			var goodsType = Utils.getRecordValue(data, 0, 'goods_type');
			if (goodsType == "101") {
				$("#landArea").html(Utils.getRecordValue(data, 0, 'land_area'));
				$("#cubage").html(Utils.getRecordValue(data, 0, 'cubage'));
				$("#goodsUse").html(Utils.getRecordValue(data, 0, 'goods_use'));
				$("#address").html(Utils.getRecordValue(data, 0, 'address'));
				$("#goodsPanel101").show();
			} else if (goodsType == "301") {
				pageObj.init301();
				$("#goodsPanel301").show();
			} else if (goodsType == "401"){
				pageObj.init401();
				$("#goodsPanel401").show();
			}
		} else {
			DialogError('查询标的信息失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}
//---------------------------------
//采矿权
//---------------------------------
pageObj.init301=function(){
	var cmd=new Command("trademine","ViewTargetMine","getTargetGoods");
	cmd.targetId=pageObj.targetId;
	cmd.success=function(data){
		var arr=data.Rows;
		var obj=arr[0];
		$('#301mineral_area').html(obj.mineral_area);
		$('#301canton_name').html(obj.canton_name);
		$('#301mineral_use_years').html(obj.mineral_use_years);
		$('#301goods_use').html(obj.goods_use);
		$('#301address').html(obj.address);
	};
	cmd.execute();
}
pageObj.init401=function(){
	var cmd=new Command("trademine","ViewTargetMine","getTargetGoods");
	cmd.targetId=pageObj.targetId;
	cmd.success=function(data){
		var arr=data.Rows;
		var obj=arr[0];
		$('#401mineral_area').html(obj.mineral_area);
		$('#401canton_name').html(obj.canton_name);
		$('#401mineral_use_years').html(obj.mineral_use_years);
		$('#401goods_use').html(obj.goods_use);
		$('#401address').html(obj.address);
	};
	cmd.execute();
}


//---------------------------------
//初始化页面
//---------------------------------
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getPageValue("moduleId");
	pageObj.userId = getUserId();
	pageObj.targetId = Utils.getPageValue("targetId");
	pageObj.goodsId = Utils.getPageValue("goodsId");
	pageObj.queryGoodsData();
	if (!pageObj.goodsExists)
		$('#goodsBody').html("无交易物信息");
}

$(document).ready(pageObj.initHtml);