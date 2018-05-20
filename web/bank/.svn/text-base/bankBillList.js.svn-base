// 页页对象
var pageObj = {};
// 表格对象
// ---------------------------------
pageObj.bankBillData = null;
pageObj.bankBillSubAccountData = null;
pageObj.gridManager = null;
pageObj.userId = null;
pageObj.bankInfo = null;
pageObj.dialog = null;
pageObj.reload = null;

pageObj.reload = function(){
	var manager = $("#bankBillData").ligerGetGridManager();
	var str = 'method=getBankBillListData&module=bank&service=BankBill&dataSetNo=transBankBillQuery&moduleId='+pageObj.moduleId+'&u=' + pageObj.userId;
	manager.loadServerData(str);
}

// 查询的列名
pageObj.getColumns = function() {
	return [
			{ display: 'ID', name: 'id', hide:true,width:'10px'},
			{ display: '是否手工入账', name: 'is_bank_input', hide:true,width:'10px'},
			{ display: '入账单申请', name: 'apply_id', hide:true,width:'10px'},
			{ display: '交款户名', name: 'bidder_account_name', width :'14%'},
			{ display: '交款账号', name: 'bidder_account_no', width :'15%'},
			{ display: '交款账户开户行', name: 'bidder_account_bank', width :'15%'},
			{ display: '类型', name: 'bill_type', width :'10%', render:pageObj.typeRender},
			{ display: '金额(元)', name: 'amount', width :'15%'},
			{ display: '到账时间', name: 'bank_business_date', width :'15%'},
			{ display: '状态', name: 'status', width :'15%', render:pageObj.statusRender},
			{ display: '子账号账号', name: 'child_account_no', width :'0px',hide:true},
			{ display: '子账号户名', name: 'child_account_name', width :'0px',hide:true},
			{ display: '子账号开户行', name: 'child_account_bank', width :'0px',hide:true}
		];
}

// ----------------------------
// 渲染入账单类型
// ----------------------------
pageObj.typeRender = function(row, rowNamber) {
	var billType = row.bill_type;
	if (billType == '0') {
		return '其它款';
	}else	if (billType == '1') {
		return '收保证金';
	}else	if (billType == '2') {
		return '收服务费';
	}else	if (billType == '3') {
		return '收成交款';
	}else	if (billType == '4') {
		return '退成交款';
	}else	if (billType == '5') {
		return '暂交款';
	}else	if (billType == '6') {
		return '错转款';
	}
}

// ----------------------------
// 渲染状态
// ----------------------------
pageObj.statusRender = function(row, rowNamber) {
	var status = row.status;
	if (status == '0') {
		return '录入中';
	}else	if (status == '1') {
		return '审核通过';
	}else	if (status == '2') {
		return '子账号已转入主账号';
	}else	if (status == '3') {
		return '退回';
	}
}

// 功能栏的显示和隐藏
pageObj.controlButton = function(tabid) {
	if (tabid == 'tabitem1') {
		$('#bankBill').show();
		$('#bankBillSubAccount').hide();
		$('#bankBillAll').hide();
	} else if (tabid == 'tabitem2') {
		$('#bankBill').hide();
		$('#bankBillSubAccount').show();
		$('#bankBillAll').hide();
	} else if (tabid == 'tabitem3') {
		$('#bankBill').hide();
		$('#bankBillSubAccount').hide();
		$('#bankBillAll').show();
		$("#startTime").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'})});
		$("#endTime").click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'})});
	}
}

// ---------------------------------
// 新增按钮事件
// ---------------------------------

pageObj.addBankBill = function (accoutntype) {
	var url = approot + '/bank/bankBillEdit.html';
	if(accoutntype=="0"){// 流水号
		var param={win:window,moduleId:pageObj.moduleId,inAccountMode:"0"};
	}else if(accoutntype=="2"){// 子账号
		var param={win:window,moduleId:pageObj.moduleId,inAccountMode:"2"};
	}
	pageObj.openBankBillEdit(url,"新增入账单",param);
}

pageObj.openBankBillEdit=function(url,title,param){
	var dialogWidth = 750;
	var dialogHeight = 390;
	DialogModal({url: url,param:param,
	feature: "location=no;status=no;dialogWidth=" + dialogWidth + "px;dialogHeight=" + dialogHeight + "px"});
}
// ---------------------------------
// 修改按钮事件
// ---------------------------------
pageObj.modifyBankBill = function () {
	var manager = $("#bankBillData").ligerGetGridManager();
	var selectedRow = manager.getSelectedRow();
	if (selectedRow == null||selectedRow.id==null) {
		DialogWarn('请选择要修改的入账单。');
		return false;
	}
	var url = approot + '/bank/bankBillEdit.html';
	var no = selectedRow.child_account_no;
	var name = selectedRow.child_account_name;
	var bank = selectedRow.child_account_bank;
	var iam="";
	if(no==""||name==""||bank==""||no==null||name==null||bank==null) iam="0";
	else iam="2";
	var param={win:window,moduleId:pageObj.moduleId,id:selectedRow.id,applyid:selectedRow.apply_id,inAccountMode:iam};
	pageObj.openBankBillEdit(url,"修改入账单",param);
}
// ---------------------------------
// 删除按钮事件
// ---------------------------------
pageObj.deleteBankBill = function () {
	var manager = $("#bankBillData").ligerGetGridManager();
	var selectedRow = manager.getSelectedRow();
	if (selectedRow == null||selectedRow.id==null) {
		DialogWarn('请选择要删除的入账单。');
		return false;
	}
	if(selectedRow.isBankInput!=null&&selectedRow.isBankInput!='1'){
		DialogWarn('只能删除手工录入的入账单。');
		return false;
	}
	DialogConfirm('删除当前入账单 ' + selectedRow.bidder_account_name + '?', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "bank";
			cmd.service = "BankBill";
			cmd.method = "deleteBankBillData";
			cmd.u = getUserId();
			cmd.id = selectedRow.id;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					DialogAlert('删除入账单数据成功完成');
					pageObj.reload();
				} else {
					DialogError('删除入账单失败,错误原因：' + data.message);
					return false;
				}
			};
			cmd.execute();
		}
	});
}

// ---------------------------------
// 提交按钮事件
// ---------------------------------
pageObj.submitBankBill = function () {
	var manager = $("#bankBillData").ligerGetGridManager();
	var selectedRow = manager.getSelectedRow();
	if (selectedRow == null||selectedRow.id==null) {
		DialogWarn('请选择要提交的入账单。');
		return false;
	}
	if (selectedRow.apply_id == null) {
		DialogWarn('入账单所属的入账单申请不能为空。请先关联流水号或子账号。');
		return false;
	}
	DialogConfirm('确定要提交吗？', function (yes) {
		if (yes) {
			var name = null;
			var earnest_money_pay = null;
			var emdtime = null;
			var sysdate = null;
			var cmd = new Command();
			cmd.module = "bank";
			cmd.service = "BankBill";
			cmd.method = "submitCheckBankBillData";
			cmd.id = selectedRow.id;
			cmd.async = false;
			cmd.u = getUserId();
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					name = Utils.getRecordValue(data, 0, 'name');
					earnest_money_pay = Utils.getRecordValue(data, 0, 'earnest_money_pay');
					emdtime = Utils.getRecordValue(data, 0, 'end_earnest_time');
					sysdate = data.sysdate;
				} else {
					DialogError('入账单提交失败');
					return false;
				}
			};
			cmd.execute();
			var cmd1 = new Command();
			cmd1.module = "bank";
			cmd1.service = "BankBill";
			cmd1.method = "submitBankBillData";
			cmd1.id = selectedRow.id;
			cmd1.u = getUserId();
			cmd1.applyId = selectedRow.apply_id;
			if(name!=selectedRow.bidder_account_name&&name!=null&&name!='undefined'){
				// 交款户名与竞买人一致
				DialogConfirm('由于户名与竞买人的名称不一致，本次入账不能作为保证金使用，而作为错转款入账。请检查竞买人对应的流水号或者子账号是否错误，如果无误则告知到交易中心财务退款。', function (yes) {
					if (yes) {
						cmd1.name = name;
						cmd1.bidderAccountName = selectedRow.bidder_account_name;
						cmd1.success = function (data) {
							var state = data.state;
							if(state == '1') {
								DialogAlert('入账单提交成功');
								pageObj.reload();
							} else {
								DialogError('入账单提交失败,错误原因：' + data.message);
								return false;
							}
						};
						cmd1.execute();
					}
				});
			}else if(emdtime!=null&&emdtime!='undefined'&&sysdate!=null&&sysdate!='undefined'&&emdtime<sysdate){
				// 超过交保证金时间
				DialogConfirm('由于交保证金时间已截止，只能作为错转款入账。请确认是否入账。', function (yes) {
					if (yes) {
						cmd1.end = "end";
						cmd1.success = function (data) {
							var state = data.state;
							if(state == '1') {
								DialogAlert('入账单提交成功');
								pageObj.reload();
							} else {
								DialogError('入账单提交失败,错误原因：' + data.message);
								return false;
							}
						};
						cmd1.execute();
					}
				});
			}else if(earnest_money_pay!=null&&earnest_money_pay!="undefined"&&earnest_money_pay!="0"){
				// 已交足保证金
				DialogConfirm('此竞买申请已交足保证金，只能作为错转款入账。请确认是否入账。', function (yes) {
					if (yes) {
						cmd1.earnest_money_pay = "1";
						cmd1.success = function (data) {
							var state = data.state;
							if(state == '1') {
								DialogAlert('入账单提交成功');
								pageObj.reload();
							} else {
								DialogError('入账单提交失败,错误原因：' + data.message);
								return false;
							}
						};
						cmd1.execute();
					}
				});
			}else{
				cmd1.name = name;
				cmd1.bidderAccountName = selectedRow.bidder_account_name;
				cmd1.success = function (data) {
					var state = data.state;
					if(state == '1') {
						DialogAlert('入账单提交成功');
						pageObj.reload();
					} else {
						DialogError('入账单提交失败,错误原因：' + data.message);
						return false;
					}
				};
				cmd1.execute();
			}
		}
	});
}
// ---------------------------------
// 转账按钮事件
// ---------------------------------
pageObj.transferBankBill = function () {
	var manager = $("#bankBillSubAccountData").ligerGetGridManager();
	var selectedRow = manager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要转账的入账单。');
		return false;
	}
	DialogConfirm('当前转账的入账单 ' + selectedRow.bidder_account_name + '?', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "bank";
			cmd.service = "BankBill";
			cmd.method = "transferBankBillData";
			cmd.u = getUserId();
			cmd.id = selectedRow.id;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					DialogAlert('子账户转账成功完成');
					var manager = $("#bankBillSubAccountData").ligerGetGridManager();
					var str = 'method=getBankBillSubAccountListData&module=bank&service=BankBill&dataSetNo=transBankBillSubAccountQuery&moduleId='+pageObj.moduleId+'&u=' + pageObj.userId+'&days=3';
					manager.loadServerData(str);
				} else {
					DialogError('子账户转账失败,错误原因：' + data.message);
					return false;
				}
			};
			cmd.execute();
		}
	});
}

// ---------------------------------
// 查询按钮事件
// ---------------------------------
pageObj.queryData = function () {
	var bidderAccountName = $("#bidderAccountName").val();
	var bidderAccountNo = $("#bidderAccountNo").val();
	var startTime = $("#startTime").val();
	var endTime = $("#endTime").val();
	var userId = getUserId();
	var days = 3;
	var queryParams = {bidderAccountName: bidderAccountName,bidderAccountNo: bidderAccountNo,
		days:days,startTime: startTime,endTime: endTime, u:userId};
		
	var url = approot+'/data?method=getBankBillAllListData&module=bank&service=BankBill&dataSetNo=transBankBillAllQuery&moduleId='+pageObj.moduleId+'&u=' + userId;
	pageObj.gridManager.refresh(url,queryParams);
	
}

$(document).ready(function() {
	$('#btnQuery').click(pageObj.queryData);
	$("#btnAdd").click(function(){pageObj.addBankBill("0")});
	$("#btnAdd2").click(function(){pageObj.addBankBill("2")});
	$("#btnModify").click(pageObj.modifyBankBill);
	$("#btnDelete").click(pageObj.deleteBankBill);
	$("#btnSubmit").click(pageObj.submitBankBill);
	$("#btnTransferAccount").click(pageObj.transferBankBill);
	pageObj.userId = getUserId();
	pageObj.moduleId = (window.dialogArguments && window.dialogArguments.moduleId)||Utils.getUrlParamValue(document.location.href, "moduleId");
	$("#tab1").ligerTab({
		onBeforeSelectTabItem : function(tabid) {
			pageObj.controlButton(tabid);
		},
		onAfterSelectTabItem : function(tabid) {
			if (tabid == 'tabitem1') {// 入账单
				pageObj.gridManager = $("#bankBillData").ligerGrid({
					url : approot+'/data?method=getBankBillListData&module=bank&service=BankBill&dataSetNo=transBankBillQuery&moduleId='+pageObj.moduleId+'&u=' + pageObj.userId,
					columns : pageObj.getColumns(),
					isScroll : true, // 是否滚动
					pageSizeOptions : [10, 20, 30],
					showTitle : false,
					height:427,
					width : '99.8%'
				});
			}
			if (tabid == 'tabitem2') {// 子账号转账
				pageObj.gridManager = $("#bankBillSubAccountData").ligerGrid({
					url : approot+'/data?method=getBankBillSubAccountListData&module=bank&service=BankBill&dataSetNo=transBankBillSubAccountQuery&moduleId='+pageObj.moduleId+'&u=' + pageObj.userId+'&days=3',
					columns : pageObj.getColumns(),
					isScroll : true, // 是否滚动
					frozen : false,// 是否固定列
					pageSizeOptions : [10, 20, 30],
					showTitle : false,
					height:427,
					width : '99.8%'
				});

			}
			if (tabid == 'tabitem3') {// 所有入账
				pageObj.gridManager = $("#bankBillAllData").ligerGrid({
					url : approot+'/data?method=getBankBillAllListData&module=bank&service=BankBill&dataSetNo=transBankBillAllQuery&moduleId='+pageObj.moduleId+'&u=' + pageObj.userId+'&days=3',
					columns : pageObj.getColumns(),
					isScroll : true, // 是否滚动
					frozen : false,// 是否固定列
					pageSizeOptions : [10, 20, 30],
					showTitle : false,
					height:427,
					width : '99.8%'
				});
			}
		}
	});
	pageObj.gridManager = $("#bankBillData").ligerGrid({
		url : approot+'/data?method=getBankBillListData&module=bank&service=BankBill&dataSetNo=transBankBillQuery&moduleId='+pageObj.moduleId+'&u=' + pageObj.userId,
		columns : pageObj.getColumns(),
		isScroll : true, // 是否滚动
		frozen : false,// 是否固定列
		pageSizeOptions : [10, 20, 30],
		showTitle : false,
		width : '99.8%',
		height:424,
	});
	Utils.autoIframeSize();
});
 