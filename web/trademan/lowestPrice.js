var pageObj = {};

pageObj.init = function() {
	pageObj.goodsType = Utils.getPageValue('goodsType');
	pageObj.url = approot + '/data?module=trademan&service=PriceChange&method=lowestPriceTargetList&goodsType=' + pageObj.goodsType;
	pageObj.gridManager = $("#grid").ligerGrid({
		url : pageObj.url,
		columns : [ {
			display : '宗地编号',
			name : 'name',
			width : 200
		}, {
			display : '起始价',
			width : 120,
			render : function(row, rowNumber) {
				var value=row['begin_price'];
				value=parseFloat(value);
				if(value)
					return value.toWanYuan() + '万';
				else
					return value;
			}
		}, {
			display : '增价幅度',
			width : 120,
			render : function(row, rowNumber) {
				var value=row['price_step'];
				value=parseFloat(value);
				if(value)
					return value.toWanYuan() + '万';
				else
					return value;
			}
		},{
			display : '待审核底价',
			width : 110,
			render : function(row, rowNumber) {
				var value=row['confirmprice'];
				value=parseFloat(value);
				if(value)
					return value.toWanYuan() + '万';
				else
					return value;
			}
		},{
			display : '底价',
			width : 120,
			render : function(row, rowNumber) {
				var value=row['reserve_price'];
				value=parseFloat(value);
				if(value)
					return value.toWanYuan() + '万';
				else
					return value;
			}
		}, {
			display : '操作',
			width : 300,
			render : function(row, rowNumber) {
				var lowestPrice = row['reserve_price'].toWanYuan();
				var html = '';
				html += '<input type="button" class="lowestprice_btn chand" value="修改底价" onclick="pageObj.changeLowestPrice(\'' + row.id + '\',\'' + row.name + '\',' + lowestPrice + ',' + row.price_step + ',' + row.begin_price + ')"/>';
				html += '&nbsp;&nbsp;<input type="button" class="lowestprice_btn chand" value="查看修改历史记录" onclick="pageObj.viewLowestPriceHistory(\'' + row.id + '\',\'' + row.name + '\')"/>';
				return html;
			}
		} ],
		isScroll : true,// 是否滚动
		frozen : false,// 是否固定列
		width : '99.8%',
		rownumbers : true,
		height : 423
	});
}

pageObj.queryData = function() {
	pageObj.gridManager.refresh(pageObj.url, {});
}

pageObj.changeLowestPrice = function(targetId, targetNo, price , step , beginPrice) {
	var value = prompt("请输入新的底价(万元):", price);
	if(isNaN(value)){
		DialogAlert("底价必须为数字");
		return;
	}
	value = parseFloat(value);
	beginPrice = parseFloat(beginPrice);
	if(beginPrice){
		beginPrice = beginPrice.toWanYuan();
		if(step>0 && value < beginPrice){
			DialogAlert("底价必须大于等于起始价");
			return;
		}else if(step<0 && value > beginPrice){
			DialogAlert("底价必须小于等于起始价");
			return;
		}
	}
	if (value && (price.sub(value)) < 0.00000000001) {
		var cmd = new Command('trademan', 'PriceChange', 'saveLowestPrice');
		cmd.lowestPrice = value.toYuan();
		cmd.targetId = targetId;
		cmd.success = function(data) {
			DialogAlert(data.message);
			pageObj.queryData();
		};
		cmd.execute();
	}

}

pageObj.viewLowestPriceHistory = function(targetId, targetNo) {
	var opt = {};
	obj.url = "lowestPriceChangeHistory.html";
	obj.param = {};
	obj.param.targetId = targetId;
	obj.param.targetName = targetNo;
	obj.feature = "dialogWidth=800px;dialogHeight=420px;";
	DialogModal(obj);
}

$(document).ready(function() {
	pageObj.init();
});