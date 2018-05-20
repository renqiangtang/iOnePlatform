//---------------------------------
//页页对象
//---------------------------------
var pageObj = {}
pageObj.organId = null;
pageObj.myBankGridManager = null;
pageObj.trustBankGridManager = null;




//---------------------------------
//初始化表格
//---------------------------------
pageObj.initGrid = function(){
	pageObj.myBankGridManager = $("#myBankListGrid").ligerGrid({
		url: approot + '/data?module=trademan&service=BankManage&method=myBankList&organId='+pageObj.organId,
		columns: [
		    { display: '编号', name: 'bank_no', width :'10%'},
			{ display: '账号', name: 'no', width :'15%'},
			{ display: '户名', name: 'name', width :'15%'},
			{ display: '用途', name: 'business_name', width :'10%'},
			{ display: '币种', name: 'currency', width :'9%'},
			{ display: '地区', name: 'canton_name', width :'10%'},
			{ display: '方式', name: 'in_account_mode', width :'9%',render:pageObj.in_account_mode_Render},
			{ display: '银行名称', name: 'bank_name', width :'15%'},
			{ display: '类型', name: 'is_outside', width :'9%',render:pageObj.is_outside}
		],
		groupColumnName:'bank_name',
		//groupColumnDisplay:'银行',
		groupRender: function (bank_name,groupdata){
			return "<a href='javascript:pageObj.editBankInfo(\""+groupdata[0].bank_id + "\")'>银行："+bank_name+"</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:pageObj.deleteBankInfo(\""+groupdata[0].bank_id + "\")'>删除</a>"
		} ,
		checkbox: true,
		usePager: false,
		selectRowButtonOnly: true,
		rownumbers: false,
		isScroll : true, // 是否滚动
		rownumbers : true,
		showTitle : false,
		onDblClickRow : pageObj.myBankEdit,
		width : '99.8%',
		height : 200
	});
	
	
	pageObj.trustBankGridManager = $("#trustBankListGrid").ligerGrid({
		url: approot + '/data?module=trademan&service=BankManage&method=trustBankList&organId='+pageObj.organId,
		columns: [
			 { display: '编号', name: 'no', width :'20%'},
			 { display: '银行名称', name: 'name', width :'20%'},
			 { display: '所属单位', name: 'organ_name', width :'20%'},
			 { display: '地区', name: 'canton_name', width :'20%'}
		],
		groupColumnName:'organ_name',
		groupColumnDisplay:'单位',
		checkbox: true,
		usePager: false,
		rownumbers: false,
		isScroll : true, // 是否滚动
		frozen : false,// 是否固定列
		showTitle : false,
		width : '99.8%'
		//detail: {onShowDetail: pageObj.showAccountList }
	});
}


//----------------------------
//渲染账户类型
//---------------------------
pageObj.in_account_mode_Render = function(row, rowNamber) {
	var status = row.in_account_mode;
	if (status == '0') {
		return '基本账号手工';
	}else	if (status == '1') {
		return '基本账号直连';
	}else	if (status == '2') {
		return '虚拟子账号';
	}else	if (status == '3') {
		return '子账号直连';
	}
}

//----------------------------
//渲染账户类型
//---------------------------
pageObj.is_outside = function(row, rowNamber) {
	var is_outside = row.is_outside;
	if (is_outside == '0') {
		return '境内';
	}else	if (is_outside == '1') {
		return '境外';
	}else {
		return '境内';
	}
}


//---------------------------------
//渲染分组名称
//---------------------------------
pageObj.groupRender = function(bank_name){
	return bank_name;
	//return '<a href='javascript:pageTransGoodsListObj.editBankInfo(\"" + goodsId + "\",\"" + targetId + "\",\"" + relId + "\",\"" + multiTradeId + "\")'>银行:' + bank_name + '('+ length + ')';
	
}

//---------------------------------
//载入账号表格
//---------------------------------
pageObj.showAccountList = function(row, detailPanel,callback){
	 var grid = document.createElement('div');
	 $(detailPanel).append(grid);
	 $(grid).css('margin',5).ligerGrid({
		 url: approot + '/data?module=trademan&service=BankManage&method=getAccountList&bankId='+row.bank_id,
		 columns:
		 [
		  		{ display: '编号', name: 'bank_no', width :'10%'},
				{ display: '账号', name: 'no', width :'15%'},
				{ display: '户名', name: 'name', width :'15%'},
				{ display: '用途', name: 'business_name ', width :'10%'},
				{ display: '币种', name: 'currency', width :'10%'},
				{ display: '类型', name: 'in_account_mode', width :'10%',render:pageObj.in_account_mode_Render},
				{ display: '银行名称', name: 'bank_name', width :'15%'}
		 ], 
	     showToggleColBtn: false,
	     usePager: false,
	     width: '99%',
	     rownumbers: false,
		 isScroll : true, // 是否滚动
		 showTitle: false, 
		 onAfterShowData: callback,
		 frozen:false
	 }); 
}

//---------------------------------
//重新载入表格
//---------------------------------
pageObj.queryData = function(){
	pageObj.queryBankDate(pageObj.myBankGridManager,approot + '/data?module=trademan&service=BankManage&method=myBankList');
	pageObj.queryBankDate(pageObj.trustBankGridManager,approot + '/data?module=trademan&service=BankManage&method=trustBankList');
}


//---------------------------------
//重新载入表格具体方法
//---------------------------------
pageObj.queryBankDate = function(gridManager,url){
	var	page = gridManager.options.page;
	if (!page)
		page = 1;    
	var	pagesize = gridManager.options.pageSize;
	if (!pagesize)
		pagesize = 10;    
	var userId = getUserId();
  
	var queryParams = {pagesize: pagesize, page: page,u:userId,organId:pageObj.organId};
	gridManager.options.url = url;
	gridManager.loadServerData(queryParams);
	
	queryParams = {u:userId,organId:pageObj.organId, mm:'get'};
	for(var key in queryParams){
	  url = Utils.setParamValue(url, key, queryParams[key], false, '=', '&');
	}
	gridManager.options.url = url;
}


//---------------------------------
//修改直连账号
//---------------------------------
pageObj.myBankEdit = function(row, rowNamber){
	var id = row.account_id;
	var dialogWidth = 800;
	var dialogHeight = 400;
	var url = approot + '/trademan/organBankAccountEdit.html'
	var param = {win:window,organId:pageObj.organId,id:id};
	DialogModal({url: url,param:param,
	feature: "dialogWidth=" + dialogWidth + "px;dialogHeight=" + dialogHeight + "px;location=no;status=no;center=yes;scroll=no;edge=sunken"});
}

//---------------------------------
//创建直连账号
//---------------------------------
pageObj.myBankCreate = function(){
	var dialogWidth = 800;
	var dialogHeight = 400;
	var url = approot + '/trademan/organBankAccountEdit.html'
	var param = {win:window,organId:pageObj.organId};
	DialogModal({url: url,param:param,
	feature: "dialogWidth=" + dialogWidth + "px;dialogHeight=" + dialogHeight + "px;location=no;status=no;center=yes;scroll=no;edge=sunken"});
}


//---------------------------------
//选择委托账号
//---------------------------------
pageObj.trustBankSelect = function(){
	var dialogWidth = 800;
	var dialogHeight = 600;
	var url = approot + '/trademan/organBankAccountSelect.html'
	var param = {win:window,organId:pageObj.organId};
	DialogModal({url: url,param:param,
	feature: "dialogWidth=" + dialogWidth + "px;dialogHeight=" + dialogHeight + "px;location=no;status=no;center=yes;scroll=no;edge=sunken"});
}

//---------------------------------
//删除直连账号
//---------------------------------
pageObj.deleteMyBank = function(){
	var manager = $("#myBankListGrid").ligerGetGridManager();
	var selectedRow = manager.getSelectedRows();
	if (selectedRow == null||selectedRow.length<1) {
		DialogAlert('请选择要删除的银行账号。');
		return false;
	}
	var ids="";
	for(var i=0;i<selectedRow.length;i++){
		if(i==selectedRow.length-1){
			ids=ids+selectedRow[i].account_id;
		}else{
			ids=ids+selectedRow[i].account_id+",";
		}
	}
	DialogConfirm('您要删除这些银行账号?', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "trademan";
			cmd.service = "BankManage";
			cmd.method = "deleteMyBank";
			cmd.ids = ids;
			cmd.organId = pageObj.organId;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					DialogSuccess('删除银行账号成功');
					pageObj.queryBankDate(pageObj.myBankGridManager,approot + '/data?module=trademan&service=BankManage&method=myBankList');
				} else {
					DialogError('删除银行账号失败');
					return false;
				}
			};
			cmd.execute();
		}
	});
}


//---------------------------------
//删除委托账号
//---------------------------------
pageObj.deleteTrustBank = function(){
	var manager = $("#trustBankListGrid").ligerGetGridManager();
	var selectedRow = manager.getSelectedRows();
	if (selectedRow == null||selectedRow.length<1) {
		DialogAlert('请选择要删除的委托银行。');
		return false;
	}
	var ids="";
	for(var i=0;i<selectedRow.length;i++){
		if(i==selectedRow.length-1){
			ids=ids+selectedRow[i].id;
		}else{
			ids=ids+selectedRow[i].id+",";
		}
	}
	DialogConfirm('您要删除这些委托银行?', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "trademan";
			cmd.service = "BankManage";
			cmd.method = "deleteTrustBank";
			cmd.ids = ids;
			cmd.organId = pageObj.organId;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					DialogSuccess('删除委托银行成功');
					pageObj.queryBankDate(pageObj.trustBankGridManager,approot + '/data?module=trademan&service=BankManage&method=trustBankList');
				} else {
					DialogError('删除委托银行失败');
					return false;
				}
			};
			cmd.execute();
		}
	});
}


//---------------------------------
//创建银行
//---------------------------------
pageObj.createBankInfo = function(){
	var dialogWidth = 800;
	var dialogHeight = 400;
	var url = approot + '/trademan/organBankEdit.html'
	var param = {win:window,organId:pageObj.organId};
	DialogModal({url: url,param:param,
	feature: "dialogWidth=" + dialogWidth + "px;dialogHeight=" + dialogHeight + "px;location=no;status=no;center=yes;scroll=no;edge=sunken"});
}

//---------------------------------
//修改银行信息
//---------------------------------
pageObj.editBankInfo = function(id){
	var dialogWidth = 800;
	var dialogHeight = 400;
	var url = approot + '/trademan/organBankEdit.html'
	var param = {win:window,organId:pageObj.organId,id:id};
	DialogModal({url: url,param:param,
	feature: "dialogWidth=" + dialogWidth + "px;dialogHeight=" + dialogHeight + "px;location=no;status=no;center=yes;scroll=no;edge=sunken"});
}

//---------------------------------
//删除
//---------------------------------
pageObj.deleteBankInfo = function(id){
	DialogConfirm('您确定要删除该银行?', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "trademan";
			cmd.service = "Bank";
			cmd.method = "deleteBankData";
			cmd.id = id;
			cmd.organId = pageObj.organId;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					DialogSuccess('删除银行成功');
					pageObj.queryBankDate(pageObj.myBankGridManager,approot + '/data?module=trademan&service=BankManage&method=myBankList');
				} else {
					if(data.message){
						DialogError(data.message);
					}else{
						DialogError('删除银行失败');
					}
					return false;
				}
			};
			cmd.execute();
		}
	});
}

//---------------------------------
//页面初始化
//---------------------------------
$(document).ready(function(){
	pageObj.organId = (window.dialogArguments && window.dialogArguments.organId)||Utils.getUrlParamValue(document.location.href, "organId");
	pageObj.initGrid();
	$("#createBank").click(pageObj.createBankInfo);
	$("#myBankCreate").click(pageObj.myBankCreate);
	$("#trustBankSelect").click(pageObj.trustBankSelect);
	$("#myBankDelete").click(pageObj.deleteMyBank);
	$("#trustBankDelete").click(pageObj.deleteTrustBank);
	
	Utils.autoIframeSize();
	
});