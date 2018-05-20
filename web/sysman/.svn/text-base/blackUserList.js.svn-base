//---------------------------------
//页对象
//---------------------------------
var pageObj = {};
// ---------------------------------
// 表格对象
// ---------------------------------
pageObj.gridManager = null;
// ---------------------------------
// 页面用于选择数据标识
// ---------------------------------
pageObj.selectDataMode = 0;// 0编辑页面，1单选，2多选
// ---------------------------------
// 表格初始高度
// ---------------------------------
pageObj.initGridWidth = 0;
pageObj.initGridHeight = 0;
pageObj.goodsType = null;
pageObj.limit_type  = null;//查询黑名单或者非黑名单
pageObj.blackStatus = null;//黑名单处理的状态0非公布，1公布
// ---------------------------------
// 转换名称
// ---------------------------------
pageObj.nameRender = function(row, rowNamber) {
	var userName = row.userName;
	if (pageObj.selectDataMode > 0) {
		return userName;
	} else {
		return "<a href='javascript:pageObj.editUser()'>" + userName + "</a>";
	}
}


// ---------------------------------
// 初始化表格
// ---------------------------------
pageObj.initGrid = function() {
	pageObj.gridManager = $("#userGrid")
			.ligerGrid(
					{
						url : approot
								+ '/data?module=sysman&service=User&method=getBlackUserListData&moduleId='
								+ pageObj.moduleId+"&limit_type="+pageObj.limit_type+"&ban_type="+pageObj.goodsType,
						checkbox : pageObj.selectDataMode == 2,
						columns : [{
							display : '名称',
							name : 'name',
							width : '15%'
						},{
							display : '证件号码/组织机构代码',
							name : 'no',
							width : '25%'
							
						}, {
							display : '竞买人类型',
							name : 'bidder_type',
							width : '8%',
							render : function (item){
								if(item.bidder_type==0){
									return "个人"
								}else if(item.bidder_type==1){
									return "企业"
								}else{
									return item.bidder_type;
								}
							}
						}, {
							display : '当前状态',
							name : 'limit_type',
							width : '12%',
							render : function (item){
								if(item.limit_type==0){
									return "黑名单";
								}else if(item.limit_type==4){
									return "已移出黑名单";
								}else {
									return "暂无";
								}
							}
						}, {
							display : '加入原因',
							name : 'remark',
							width : '24%'
						},{
							display : '创建日期',
							name : 'create_date',
							width : '16%'
						}],
						pagesizeParmName : 'pageRowCount',// 每页记录数
						pageParmName : 'pageIndex',// 页码数
						sortnameParmName : 'sortField',// 排序列名
						sortorderParmName : 'sortDir',// 排序方向
						isScroll : true,// 是否滚动
						frozen : false,// 是否固定列
						pageSizeOptions : [ 10, 20, 30 ],
						showTitle : false,
						height: '92%',
						detail : false,
						onError : function(a, b) {
						}
					});
}

// ---------------------------------
// 查询数据
// ---------------------------------
pageObj.queryData = function() {
	var queryParams = {};
	queryParams.username = $("#txtUserName").val();
	queryParams.ban_type = pageObj.goodsType;
	queryParams.limit_type = pageObj.limit_type;
	var url = approot
			+ '/data?module=sysman&service=User&method=getBlackUserListData&'
			+ pageObj.moduleId;
	pageObj.gridManager.refresh(url, queryParams);
}

//---------------------------------
//查询已删除黑名单或黑名单
//---------------------------------
pageObj.btnInFo = function() {
	if(pageObj.limit_type==0){
		pageObj.limit_type=4;
		$("#infoV").val("查看黑名单");
		$("#btnAdd").show();
		$("#btnDelete").hide();
		$("#btnBlack").hide();
	}else if(pageObj.limit_type==4){
		pageObj.limit_type=0;
		$("#infoV").val("查看已删除黑名单");
		$("#btnDelete").show();
		$("#btnAdd").hide();
		$("#btnBlack").show();
	}
	 $("#txtUserName").val('');
	var queryParams = {};
	queryParams.limit_type = pageObj.limit_type;
	queryParams.ban_type = pageObj.goodsType;
	var url = approot
			+ '/data?module=sysman&service=User&method=getBlackUserListData&moduleId='
			+ pageObj.moduleId;
	pageObj.gridManager.refresh(url, queryParams);
}

// ---------------------------------
// 删除黑名单
// ---------------------------------
pageObj.btnDelete = function() {
	var selectedRow = pageObj.gridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择一条已移出黑名单数据。');
		return false;
	}
	var row = pageObj.gridManager.getSelectedRow();
	var limit_type=row.limit_type;
	if (limit_type == 4) {
		DialogAlert('请选择一条已移出黑名单数据。');
		return false;
	}
	var name=row.name;
	var no=row.no;
	var bidder_type=row.bidder_type;
	var dialog = DialogOpen({height: 350, width: 700, title: '移出黑名单',
		url: approot + '/sysman/blackUserDelete.html?moduleId=' + pageObj.moduleId+'&goodsType='+pageObj.goodsType+'&name='+name+'&id='+row.id+'&limittype='+limit_type+'&no='+no+'&biddertype='+bidder_type});
	var manager = {};
	manager.dialog = dialog;
	manager.parentPageObj = pageObj;
	setGlobalAttribute("blackUserDeleteDialog", manager);

}

//---------------------------------
//加入黑名单按钮事件
//---------------------------------
pageObj.btnAdd = function() {
	var row = pageObj.gridManager.getSelectedRow();
	if (row == null) {
		DialogAlert('请选择一条黑名单数据。');
		return false;
	}
	var limit_type=row.limit_type;
	if (limit_type == 0) {
		DialogAlert('请选择一条黑名单数据。');
		return false;
	}
	
	var name=row.name;
	var no=row.no;
	var bidder_type=row.bidder_type;
	var dialog = DialogOpen({height: 350, width: 700, title: '加入黑名单',
		url: approot + '/sysman/blackUserDelete.html?moduleId=' + pageObj.moduleId+'&goodsType='+pageObj.goodsType+'&name='+name+'&id='+row.id+'&limittype='+limit_type+'&no='+no+'&biddertype='+bidder_type });
	var manager = {};
	manager.dialog = dialog;
	manager.parentPageObj = pageObj;
	setGlobalAttribute("blackUserDeleteDialog", manager);

}
// ---------------------------------
// 选择数据页面时的选择方法
// ---------------------------------
pageObj.getSelectedUserData = function() {
	return pageObj.gridManager.getSelectedRow();
}

pageObj.getCheckedUserData = function() {
	return pageObj.gridManager.getCheckedRows();
}

// ---------------------------------
// 新增黑名单
// ---------------------------------
pageObj.btnBlack = function() {
	var row = pageObj.gridManager.getSelectedRow();
	
	var dialog = DialogOpen({height: 480, width: 700, title: '新增黑名单',
		url: approot + '/sysman/blackUserEdit.html?moduleId=' + pageObj.moduleId+'&goodsType='+pageObj.goodsType });
	var manager = {};
	manager.dialog = dialog;
	manager.parentPageObj = pageObj;
	setGlobalAttribute("blackUserEditDialog", manager);
}

// ---------------------------------
// 查看历史
// ---------------------------------
pageObj.viewHistory = function() {
	var row = pageObj.gridManager.getSelectedRow();
	if (!row) {
		DialogAlert('请选择要查看历史的用户！');
		return;
	}
	var columns = {
		new_value : {
			title : '类型',
			width : 0,
			render : {
				'100' : '白名单',
				'200' : '黑名单'
			}
		},
		change_cause : {
			title : '加入名单原因',
			width: 400,
			hint: true
		},
		change_date : {
			title : '日期'
		}
	};

	DialogOpen({
		height : 400,
		width : 600,
		title : '查看变更历史',
		url : approot + '/sysman/fieldChangeList.html?hideQueryCondition=1'
				+ '&refTableName=trans_bidder_limit_dtl&fieldName=is_valid&refId=' + row.id
				+ '&columns=' + encodeURIComponent(JSON.stringify(columns)), // 方式二
		buttons : [ {
			text : '关闭',
			onclick : function(item, dialog) {
				dialog.close();
			}
		} ]
	}, window);
}

//---------------------------------
//公布或关闭公开黑名单
//---------------------------------
pageObj.btnOvertBlack = function(){
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.method = "OvertBlack";
	cmd.goods_type=pageObj.goodsType;
	cmd.status = pageObj.blackStatus;
	cmd.success = function(data) {
		if(data.state==1){
			var messtatus="";
			if(data.status==0){
				messtatus="关闭公布黑名单成功!"
				$("#btnOvertBlack").val("公布黑名单");
				pageObj.blackStatus=1;
			}else if(data.status==1){
				messtatus="公布黑名单成功!";
				$("#btnOvertBlack").val("关闭公布黑名单");
				pageObj.blackStatus=0;
			}else{
				messtatus="状态错误";
			}
			DialogAlert(messtatus);
		}
	};
	cmd.execute();
}

pageObj.initOvertBlackBtn = function(){
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.method = "getOvertBlack";
	cmd.goods_type=pageObj.goodsType;
	cmd.success = function(data) {
		if(data.status==0){
			$("#btnOvertBlack").val("公布黑名单");
			pageObj.blackStatus=1;
		}else if(data.status==1){
			$("#btnOvertBlack").val("关闭公布黑名单");
			pageObj.blackStatus=0;
		}else{
			alert("设置按扭状态错误");
		}
	}
	cmd.execute();
}

// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getUrlParamValue(document.location.href,
			"moduleId");
	var selectDataParam = Utils.getUrlParamValue(document.location.href,
			"selectData");
	pageObj.limit_type=0;
	pageObj.goodsType=Utils.getUrlParamValue(document.location.href,"goodsType");
	pageObj.initOvertBlackBtn();
	pageObj.initGrid();
	$("#btnQuery").click(pageObj.queryData);
	$("#btnDelete").click(pageObj.btnDelete);//删除黑名单
	$("#btnBlack").click(pageObj.btnBlack);//添加黑名单
	$("#btnView").click(pageObj.viewHistory);//查看历史
	$("#btnAdd").click(pageObj.btnAdd);//加入黑名单
	$("#btnInFo").click(pageObj.btnInFo);//查看已删除黑名单
	$("#btnOvertBlack").click(pageObj.btnOvertBlack);//公布黑名单
	$("#btnAdd").hide();
}

$(document).ready(pageObj.initHtml);
