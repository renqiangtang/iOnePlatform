var pageObj={};
pageObj.gridManager=null;
pageObj.moduleId=null;
pageObj.dialog=null;
// -------------------------------------------
// 重置
// -------------------------------------------
pageObj.reset=function(){
	$("#no").val("");
	$("#accountBank").val("");
	$("#accountNo").val("");
	$("#bidderAccountName").val("");
	$("#billType").val("");
	$("#txtBeginDate").val("");
	$("#txtEenDate").val("");
}
// -------------------------------------------
// 渲染入账单状态
// -------------------------------------------
pageObj.tabStatus=function(row, rowNamber){
	var status=row.tab_status;
	if(status=='0'){
		return '录入中';
	}else if(status=='1'){
		return '审核通过';
	}else if(status=='2'){
		return '子账户已转入主账户';
	}else if(status=='3'){
		return '退回';
	}else if(status=='4'){
		return '已退竞得人';
	}else if(status=='5'){
		return '已出财政';
	}
}

// -------------------------------------------
// 渲染标的状态
// -------------------------------------------
pageObj.targetStatus=function(row, rowNamber){
	var status=row.target_status;
	return comObj.targetStatusObj[status];
	
}
// -------------------------------------------
// 渲染入账类型
// -------------------------------------------
pageObj.billType=function(row, rowNamber){
	var bill_type=row.bill_type;
	if(bill_type=='0'){
		return '其它款';
	}else if(bill_type=='1'){
		return '保证金';
	}else if(bill_type=='2'){
		return '服务费';
	}else if(bill_type=='3'){
		return '收成交款';
	}else if(bill_type=='4'){
		return '退成交款';
	}else if(bill_type=='5'){
		return '暂交款';
	}else if(bill_type=='6'){
		return '错转款';
	}
	
}
// -------------------------------------------
// 渲染标的编号
// -------------------------------------------
pageObj.renderNo=function(row, rowNamber){
	var tab_id=row.tab_id;
	var no=row.no;
	var name=row.name;
	if(no!=null&&no!=''){
		return "<a href='javascript:pageObj.view(\"" + tab_id + "\")'>" + no + "</a>";
	}else{
		if(name!=null&&name!=''){
			return "<a href='javascript:pageObj.view(\"" + tab_id + "\")'>" + name + "</a>";
		}
	}
}
// -------------------------------------------
// 渲染操作
// -------------------------------------------
pageObj.rendercz=function(row, rowNamber){
	var status=row.tab_status;
	var tab_id=row.tab_id;
	if(status==2){
		return "<a href='javascript:pageObj.editStatus(\"" + tab_id + "\",\"4\")'>退竞得人</a>&nbsp;&nbsp;" +
			   "<a href='javascript:pageObj.editStatus(\"" + tab_id + "\",\"5\")'>出财政</a>";
	}
}

// -------------------------------------------
// 执行操作
// -------------------------------------------
pageObj.editStatus=function(tab_id,status){
	var cmd = new Command();
	cmd.module = "after";
	cmd.service = "TransAccount";
	cmd.method = "editStatusAccount";
	cmd.tab_id=tab_id;
	cmd.status=status;
	cmd.success = function (data) {
		if(data.message!=null&&data.message!=''){
			DialogAlert(data.message);
			pageObj.queryData();
		}
		
	};
	cmd.execute();

}



// -------------------------------------------
// 显示详情
// -------------------------------------------
pageObj.view=function(tab_id){
	pageObj.dialog = DialogOpen({height:450, width:800, title: '详情', url: approot+'/mine/after/refundAccountView.html?tab_id='+tab_id});
}
// -------------------------------------------
// 初始化表头
// -------------------------------------------

pageObj.getGridColumns=function(){
	var gridColumns=new Array();
	gridColumns.push({ display: '标的', name: 'no', align: 'left', width: '17%' ,render:pageObj.renderNo},
			{ display: '交款人户名', name: 'bidder_account_name', width: '14%',align: 'left' },
			{ display: '交款人账号', name: 'bidder_account_no', width: '14%', align: 'left' },
			{ display: '交款人账户开户行', name: 'bidder_account_bank', width: '14%', align: 'left' },
			{ display: '中心账户开户行', name: 'account_bank', width: '13%', align: 'left' },
			{ display: '交款金额', width: '13%', align: 'right',render:pageObj.renderAmount},
			{ display: '交易日期', name: 'bank_business_date',align: 'center' ,width: '9%'}, 
			{ display: '类型', name: 'bill_type' ,align: 'center', width: '8%',render:pageObj.billType},
			{ display: '入账单状态', name: 'tab_status' ,align: 'center', width: '8%',render:pageObj.tabStatus},  
			{ display: '标的状态', name: 'target_status' ,align: 'center', width: '8%',render:pageObj.targetStatus},
			// { display: '操作', name: '' ,align: 'center', width:
			// '12%',render:pageObj.rendercz},
			{ display: 'license_id', name: 'license_id',align: 'center' ,width: '1',hide :true});
	
	return gridColumns;
}

pageObj.renderAmount=function(row,number){
	return comObj.cf({unit:row.unit,flag:row.currency,amount:row.amount});
}

pageObj.groupRender=function(license_id,groupdata){
	if(groupdata){
		var bidderName="";
		var no="";
		var apply_no="";
		var amount=0;
		var name="";
		for(var i=0;i<groupdata.length;i++){
			var row=groupdata[i];
			bidderName=row.bidder_name;
			no=row.no;
			name=row.name;
			apply_no=row.apply_no;
			amount=amount+row.amount;
		}
		if(no!=null&&no!=''){
			return '竞买人：'+bidderName+"，竞买编号:"+apply_no+"，标的编号："+no;
		}else{
			if(name!=null&&name!=''){
				return '竞买人：'+bidderName+"，竞买编号:"+apply_no+"，标的名称："+name;
			}
		}
		
	}
	
}

// -------------------------------------------
// 初始化表格
// -------------------------------------------
pageObj.initGrid = function () {
	pageObj.gridManager = $("#refundAccountGrid").ligerGrid({
		url: approot + '/data?module=after&service=TransAccount&method=getRefundAccountListData&goodsType='+pageObj.goodsType+'&u='+pageObj.u,
	    columns: pageObj.getGridColumns(),
	    isScroll: true,// 是否滚动
	    pageSizeOptions: [10, 20, 30], 
	    showTitle: false,
	    checkbox:false,
	    rownumbers : true,
	    height:400,
	    groupColumnName:'license_id',
	    groupRender: pageObj.groupRender
	});	

}
// ---------------------------------
// 查询按钮事件
// ---------------------------------
pageObj.queryData = function () {

	var url = approot + '/data?module=after&service=TransAccount&method=getRefundAccountListData&moduleId=' + pageObj.moduleId;
	var obj={};
	obj.no=$("#no").val().trim();
	obj.accountBank=$("#accountBank").val().trim();
	obj.accountNo=$("#accountNo").val().trim();
	obj.bidderAccountName=$("#bidderAccountName").val().trim();
	obj.billType=$("#billType").val().trim();
	obj.txtBeginDate=$("#txtBeginDate").val().trim();
	obj.txtBeginDate=$("#txtBeginDate").val().trim();
	obj.txtEenDate=$("#txtEenDate").val().trim();
	obj.u=pageObj.u;
	obj.goodsType=pageObj.goodsType;
	pageObj.gridManager.refresh(url,obj);

}




$(document).ready(function () {
	pageObj.moduleId=(window.dialogArguments && window.dialogArguments.moduleId && window.dialogArguments.moduleId)
	||Utils.getUrlParamValue(document.location.href, "moduleId");
	pageObj.goodsType = Utils.getPageValue('goodsType');
	pageObj.u = getUserId();
	$("#txtBeginDate").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'})});
	$("#txtEenDate").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'})});
	pageObj.initGrid();
	
	$("#btnReset").click(function(){
		pageObj.reset();
	});
	$("#btnQuery").click(function(){
		pageObj.queryData();
	});
});