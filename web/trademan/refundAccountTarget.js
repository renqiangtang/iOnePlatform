var pageObj = {};

pageObj.sendData = function () {
	var object=$("input[id^='refund_time_']");
	var length = -1;
	var sendData = new Array();
	var allHaveTime = true;
	var allBank = true;
	$.each(object,function(n,value) { 
		length = n;
		var id = value.id;
		var val = value.value;
		if(val==null || val==''){
			allHaveTime = false;
			return;
		}
		var trueId = id.replace("refund_time_", "");
		var no = $("#refund_no_"+trueId).val();
		var name = $("#refund_name_"+trueId).val();
		var bank = $("#refund_bank_"+trueId).val();
		if(no==null || no=='' || name==null || name=='' || bank==null || bank==''){
			allBank = false;
			return;
		}
		sendData.push(trueId+","+val+","+no+","+name+","+bank);
	});
	if(!allHaveTime){
		DialogError("有入账单未填写退款时间!");
		return;
	}
	if(!allBank){
		DialogError("有入账单未填写退款信息!");
		return;
	}
	if(length == -1 ){
		DialogError("没有要发送的入账单数据!");
		return;
	}
	
	
	var cmd = new Command();
	cmd.module = "after";
	cmd.service = "TransAccount";
	cmd.method = "sendRefundAccount";
	cmd.sendData = sendData.join(";");
	cmd.success = function (data) {
		var state = data.state;
		if(state == '1') {
			DialogSuccess(data.message);
			$("#btnSend").hide();
		} else {
			DialogError(data.message);
		}
	};
	cmd.execute();
}

pageObj.currencyFormatted = function(amount) {
    var i = parseFloat(amount);
    if(isNaN(i)) { i = 0.00; }
    var minus = '';
    if(i < 0) { minus = '-'; }
    i = Math.abs(i);
    i = parseInt((i + .005) * 100);
    i = i / 100;
    s = new String(i);
    if(s.indexOf('.') < 0) { s += '.00'; }
    if(s.indexOf('.') == (s.length - 2)) { s += '0'; }
    s = minus + s;
    return s;
}


pageObj.refundTime=function(refund_time){
	var refund_time=refund_time+"";
	if(refund_time!=null && refund_time!='null'){
		if(refund_time.length>10){
			return refund_time.substring(0,10);
		}else{
			return refund_time;
		}
	}else{
		return "&nbsp;";
	}
	
}


pageObj.init = function() {
	pageObj.targetId = Utils.getPageValue('targetId');
	pageObj.targetName = Utils.getPageValue('targetName');
	document.title = "标的："+pageObj.targetName+"-退款申请";
	pageObj.url = approot + '/data?module=after&service=TransAccount&method=refundAccountTarget&targetId='+pageObj.targetId;
	pageObj.gridManager = $("#grid").ligerGrid({
		url : pageObj.url,
		columns : [{
			display : '户名',
			name : 'bidder_account_name',
			width : 100,
			render:function(row,rowNumber){
				var tabStatus=row['tab_status'];
				if(tabStatus<2){
					return row['bidder_account_name'];
				}else if(tabStatus==2){
					var id = 'refund_name_'+row['tab_id'];
					return '<input class="i-input_w80 i-input_bg" type="text" id="'+id+'" value="'+row['bidder_account_name']+'" />';
				}else if(tabStatus==3){
					return row['bidder_account_name'];
				}else{
					return row['bidder_account_name'];
				}
			}
		}, {
			display : '入款账号',
			name : 'bidder_account_no',
			width : 120,
			render:function(row,rowNumber){
				var tabStatus=row['tab_status'];
				if(tabStatus<2){
					return row['bidder_account_no'];
				}else if(tabStatus==2){
					var id = 'refund_no_'+row['tab_id'];
					return '<input class="i-input_w100 i-input_bg" type="text" id="'+id+'" value="'+row['bidder_account_no']+'" />';
				}else if(tabStatus==3){
					return row['bidder_account_no'];
				}else{
					return row['bidder_account_no'];
				}
			}
		},
		{
			display : '入款银行',
			name : 'bidder_account_bank',
			width : 100,
			render:function(row,rowNumber){
				var tabStatus=row['tab_status'];
				if(tabStatus<2){
					return row['bidder_account_bank'];
				}else if(tabStatus==2){
					var id = 'refund_bank_'+row['tab_id'];
					return '<input class="i-input_w80 i-input_bg" type="text" id="'+id+'" value="'+row['bidder_account_bank']+'" />';
				}else if(tabStatus==3){
					return row['bidder_account_bank'];
				}else{
					return row['bidder_account_bank'];
				}
			}
		}, {
			display : '保证金银行',
			name : 'account_bank',
			width:120
		}, {
			display : '金额',
			name : 'amount',
			width:120,
			render:function(row,rowNumber){
				var amout = row['amount'];
				return pageObj.currencyFormatted(amout);
			}
		}, {
			display:'退款时间&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;',
			name:'refund_time',
			align:'left',
			width:450,
			render:function(row,rowNumber){
				var tabStatus=row['tab_status'];
				if(tabStatus<2){
					return "入账单尚未进行子转总";
				}else if(tabStatus==2){
					var id = 'refund_time_'+row['tab_id'];
					var dateFmt = 'yyyy-MM-dd';
				    return '<input class="i-input_w120 i-input_bg" type="text" id="'+id+'" onClick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})" />';
					//return "111";
				}else if(tabStatus==3){
					return pageObj.refundTime(row['refund_time'])+"(已申请:"+row['refund_name']+";"+row['refund_no']+";"+row['refund_bank']+")";
				}else{
					return pageObj.refundTime(row['refund_time'])+"(已退款:"+row['refund_name']+";"+row['refund_no']+";"+row['refund_bank']+")";
				}
			}
		}],
		isScroll : true,// 是否滚动
		rownumbers : true,
		usePager: false,
		height : 350,
		checkbox:false,
	    enabledSort:false
	});
}

$(document).ready(function() {
	pageObj.init();
	$("#btnSend").click(function(){
		pageObj.sendData();
	});
});