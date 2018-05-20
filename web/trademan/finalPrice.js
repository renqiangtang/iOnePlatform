var pageObj={};

pageObj.init=function(){
	pageObj.goodsType = Utils.getPageValue('goodsType');
	var cmd = new Command();
	cmd.module = "trademan";
	cmd.service = "PriceChange";
	cmd.method = "finalPriceTargetList";
	cmd.goodsType = pageObj.goodsType;
	cmd.success=function(data){
		var rows=data.Rows;
		var html='<option value="0000">请选择标的</option>';
		$.each(rows,function(index,item){
			html+='<option value="'+item.id+'">'+item.no+'</option>';
		});
		$('#targets').html(html);
	};
	cmd.execute();
	//
	pageObj.url='';
	pageObj.gridManager = $("#grid").ligerGrid({
		url : pageObj.url,
		columns : [ {
			display : '指标名称',
			name : 'name',
			width : 200
		}, {
			display : '指标单位',
			name : 'unit',
			width : 80
		},{
			display:'起始价',
			name:'init_value',
			width:100,
			render:pageObj.renderInit
		},{
			display:'增价幅度',
			name:'step',
			width:100,
			render:pageObj.renderStep
		},{
			display:'待审批封顶价',
			name:'init_value',
			width:100,
			render : function(row, rowNumber) {
				var status=row['log_status'];
				if(status==1){
					var value=row['new_value'];
					value=parseFloat(value);
					if(value && row.unit == '万元')
						return value.toWanYuan();
					else
						return value;
				}else{
					return "";
				}
			}
		},{
			display:'封顶价',
			name:'final_value',
			width:100,
			render:pageObj.renderFinal
		},{
			display:'审批状态',
			name:'log_status',
			width:80,
			render:function(row, rowNumber) {
				var value=row['log_status'];
				if(value==1){
					return "待审核";
				}else if(value==2){
					return "审核通过"
				}else if(value==3){
					return "审核不通过";
				}else if(value==0){
					return "暂未设置";
				}else{
					return value;
				}
			}
		}
		,{
			display:'操作',
			width:200,
			render:function(row,rowNumber){
				var final_value=row['final_value'];
				var html='';
				html += '<input class="lowestprice_btn" type="button" value="修改封顶价" onclick="pageObj.changeFinalPrice(\'' + row.id + '\',\'' + row.name + '\',' + final_value + ',\'' + row.unit + '\','+ row.init_value +','+ row.step +')"/>';
				html += '&nbsp;&nbsp;<input class="lowestprice_btn" type="button" value="查看修改历史记录" onclick="pageObj.viewFinalPriceHistory(\'' + row.id + '\',\'' + row.name + '\',\'' + row.unit + '\')"';
				return html;
			}
		}
		],
		isScroll : true,// 是否滚动
		frozen : false,// 是否固定列
		width : '99.8%',
		rownumbers : true,
		height : 423
	});
	//
	$('#targets').change(function(){
		pageObj.targetId=$('#targets').val();
		pageObj.queryData();
	});
	
}

//-----------------------------
//渲染价格
//----------------------------
pageObj.renderInit=function(row,number){
	return pageObj.getShowPrice(row.init_value,row.unit);
}

pageObj.renderStep=function(row,number){
	return pageObj.getShowPrice(row.step,row.unit);
}

pageObj.renderFinal=function(row,number){
	return pageObj.getShowPrice(row.final_value,row.unit);
}

pageObj.getShowPrice = function(amount , unit) {
	if (!amount)
		return '';
	if (unit == '万元') {
		var m = amount / 10000;
		amount = parseFloat(m.toFixed(4));
	}
	return amount;
}

pageObj.changeFinalPrice=function(quotaId,quotaName,price,unit,beginValue,step){
	if(unit == '万元' && price){
		price = parseFloat(price).toWanYuan();
	}
	var value = prompt("请输入新的封顶值("+unit+"):", price);
	if(isNaN(value)){
		DialogAlert("封顶价必须为数字");
		return;
	}
	value = parseFloat(value);
	if(unit == '万元'){
		value = value.toYuan();
	}
	beginValue = parseFloat(beginValue);
	step = parseFloat(step);
	if(step>0 && value < beginValue){
		DialogAlert("封顶价必须大于等于起始价");
		return;
	}else if(step<0 && value > beginValue){
		DialogAlert("封顶价必须小于等于起始价");
		return;
	}
	if (value) {
		var cmd = new Command('trademan', 'PriceChange', 'saveFinalPrice');
		cmd.finalPrice = value;
		cmd.multiId = quotaId;
		cmd.targetId=pageObj.targetId;
		cmd.success = function(data) {
			DialogAlert(data.message);
			pageObj.queryData();
		};
		cmd.execute();
	}
}


pageObj.viewFinalPriceHistory=function(quotaId,quotaName){
	var opt = {};
	obj.url = "finalPriceChangeHistory.html";
	obj.param = {};
	obj.param.multiId  = quotaId;
	obj.param.quotaName = quotaName;
	obj.feature = "dialogWidth=800px;dialogHeight=420px;";
	DialogModal(obj);
}

pageObj.queryData = function() {
	pageObj.url=approot + '/data?module=trademan&service=PriceChange&method=targetMultiList&targetId=' + pageObj.targetId;
	pageObj.gridManager.refresh(pageObj.url, {});
}
	

$(document).ready(function(){
	pageObj.init();
});