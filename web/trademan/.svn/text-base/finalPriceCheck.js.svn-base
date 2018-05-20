var pageObj = {};

pageObj.init = function() {
	pageObj.goodsType = Utils.getPageValue('goodsType');
	pageObj.url = approot + '/data?module=trademan&service=PriceChange&method=waitCheckFinalList&goodsType=' + pageObj.goodsType;
	pageObj.gridManager = $("#grid").ligerGrid({
		url : pageObj.url,
		columns : [ {
			display : '宗地编号',
			name : 'name',
			width : 200
		},{
			display : '指标名称',
			name : 'multi_name',
			width : 180
		},{
			display : '单位',
			name : 'unit',
			width : 120
		}
		
		, {
			display : '修改前封顶价',
			width : 120,
			render : function(row, rowNumber) {
				var value=row['old_value'];
				value=parseFloat(value);
				if(value && row.unit == '万元')
					return value.toWanYuan();
				else
					return value;
			}
		}, 
		{
			display : '待审批封顶价',
			width : 120,
			render : function(row, rowNumber) {
				var value=row['new_value'];
				value=parseFloat(value);
				if(value && row.unit == '万元')
					return value.toWanYuan();
				else
					return value;
			}
		}, 
		
		{
			display : '操作',
			width : 200,
			render : function(row, rowNumber) {
				var lowestPrice = row['reserve_price'].toWanYuan();
				var html = '';
				html += '<input class="lowestprice_btn" type="button" value="审核通过" onclick="pageObj.check(\'' + row.log_id + '\',2)"/>';
				html += '&nbsp;&nbsp;<input class="lowestprice_btn" type="button" value="审核不通过" onclick="pageObj.check(\'' + row.log_id + '\',3)"/>';

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

pageObj.check = function(logId, status) {
		var cmd = new Command('trademan', 'PriceChange', 'checkFinalPrice');
		cmd.logId = logId;
		cmd.status = status;
		cmd.success = function(data) {
			DialogAlert(data.message);
			pageObj.queryData();
		};
		cmd.execute();
	}



pageObj.viewLowestPriceHistory = function(targetId, targetNo) {
	var opt = {};
	obj.url = "lowestPriceChangeHistory.html";
	obj.param = {};
	obj.param.targetId = targetId;
	obj.param.targetName = targetNo;
	obj.feature = "dialogWidth=700px;dialogHeight=500px";
	DialogModal(obj);
}

$(document).ready(function() {
	pageObj.init();
});