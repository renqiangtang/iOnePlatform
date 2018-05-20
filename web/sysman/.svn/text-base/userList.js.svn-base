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
pageObj.userType="";

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
// 转换用户类型
// ---------------------------------
pageObj.userTypeRender = function(row, rowNamber) {
	var userType = Number(row.userType).toFixed(0);
	if (userType == "1")
		return "竞买人";
	else if (userType == "2")
		return "银行工作人员";
	else
		return "内部人员";
}

// ---------------------------------
// 转换用户状态
// ---------------------------------
pageObj.statusRender = function(row, rowNumber) {
	var isValid = Number(row.isValid).toFixed(0);
	var status = Number(row.status).toFixed(0);
	// 0申请中，1已激活(此状态为正常可用状态)，2过期
	if (isValid == 1) {
		if (status == "0")
			return "申请中";
		else if (status == "1")
			return "正常";
		else
			return "已过期";
	} else
		return "无效";
}

// ---------------------------------
// 黑白名单
// ---------------------------------
pageObj.rbUserType = function(row, rowNumber) {
	if (row.listType && row.listType == '200') {
		return '是';
	}
	return '否';
}

// ---------------------------------
// 初始化表格
// ---------------------------------
pageObj.initGrid = function() {
	// var gridWidth = pageObj.initGridWidth > 0 ? pageObj.initGridWidth :
	// '99.8%';
	// var gridHeight = pageObj.initGridHeight > 0 ? pageObj.initGridHeight :
	// 427;
	pageObj.gridManager = $("#userGrid")
			.ligerGrid(
					{
						url : approot
								+ '/data?module=sysman&service=User&method=getUserListData&userType='+pageObj.userType+'&moduleId='
								+ pageObj.moduleId,
						checkbox : pageObj.selectDataMode == 2,
						columns : [ {
							display : '名称',
							name : 'userName',
							width : '23%',
							render : pageObj.nameRender
						}, {
							display : '用户类型',
							name : 'userType',
							width : '12%',
							render : pageObj.userTypeRender
						}, {
							display : '关联名称',
							name : 'refName',
							width : '15%'
						}, {
							display : '激活日期',
							name : 'activeDate',
							width : '12%'
						}, {
							display : '有效期',
							name : 'validity',
							width : '8%'
						}, {
							display : '所属角色',
							name : 'relRoles',
							isSort : false,
							width : '15%'
						}, {
							display : '状态',
							name : 'status',
							width : '8%',
							render : pageObj.statusRender
						}, {
							display : '黑名单',
							width : '8%',
							render : pageObj.rbUserType
						} ],
						pagesizeParmName : 'pageRowCount',// 每页记录数
						pageParmName : 'pageIndex',// 页码数
						sortnameParmName : 'sortField',// 排序列名
						sortorderParmName : 'sortDir',// 排序方向
						isScroll : true,// 是否滚动
						frozen : false,// 是否固定列
						pageSizeOptions : [ 10, 20, 30 ],
						showTitle : false,
						width : '99.8%',
						height : '99%',
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
	queryParams.userName = $("#txtUserName").val();
	queryParams.userType = $("#cboUserType").val();
	queryParams.userStatus = $("#cboStatus").val();
	queryParams.userHasCakey = $("#cboHasCakey").val();
	queryParams.userCakey = $("#txtCakey").val();
	queryParams.listType = $('#rbuserType').val();
	var url = approot
			+ '/data?module=sysman&service=User&method=getUserListData&moduleId='
			+ pageObj.moduleId;
	pageObj.gridManager.refresh(url, queryParams);
}

// ---------------------------------
// 新增按钮事件
// ---------------------------------
pageObj.newUser = function() {
	DialogOpen({
		height : 650,
		width : 650,
		title : '新增用户',
		url : approot + '/sysman/userEdit.html?moduleId=' + pageObj.moduleId
				+ '&id='
	});
}

// ---------------------------------
// 修改按钮事件
// ---------------------------------
pageObj.editUser = function() {
	var selectedRow = pageObj.gridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要修改的用户。');
		return false;
	}
	DialogOpen({
		height : 650,
		width : 650,
		title : '修改用户',
		url : approot + '/sysman/userEdit.html?moduleId=' + pageObj.moduleId
				+ '&id=' + selectedRow.id
	});
}

// ---------------------------------
// 删除按钮事件
// ---------------------------------
pageObj.deleteUser = function() {
	var selectedRow = pageObj.gridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogAlert('请选择要删除的用户。');
		return false;
	}
	var message = '删除当前用户 ' + selectedRow.userName + '？';
	if (selectedRow.userType == "1") {
		message = message + "关联的竞买人信息不会被删除。";
	} else if (selectedRow.userType == "2") {
		message = message + "关联的银行工作人员信息不会被删除。";
	} else {
		message = message + "关联的内部人员信息不会被删除。";
	}
	DialogConfirm(message, function(yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "User";
			cmd.method = "deleteUserData";
			cmd.id = selectedRow.id;
			cmd.success = function(data) {
				var state = data.state;
				if (state == '1') {
					DialogAlert('删除用户数据成功完成');
					pageObj.gridManager.deleteSelectedRow();
				} else {
					DialogError('删除用户失败,错误原因：' + data.message);
					return false;
				}
			};
			cmd.execute();
		}
	});
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
// 读取Cakey按钮事件
// ---------------------------------
pageObj.readCakey = function() {
	DialogConfirm('请插上新的CAKey后点击“是”按钮！', function(yes) {
		if (yes) {
			var caKeyId = ca.readId();
			$("#txtCakey").val(caKeyId);
		}
	});
}

// ---------------------------------
// 加入黑名单
// ---------------------------------
pageObj.blackAdd = function() {
	var row = pageObj.gridManager.getSelectedRow();
	if (!row) {
		DialogAlert('请选择要加入黑名单的用户！');
		return;
	}
	if(row.listType=='200'){
		DialogAlert(row.userName+'已经加入黑名单！');
	}else if ( row.listType == '100') {
		DialogConfirm('是否将' + row.userName + '加入黑名单?', function(yn) {
			if (!yn)
				return;
			DialogPrompt('加入黑名单原因', function(yes, value) {
				if (yes) {
					var cmd = new Command('sysman', 'User', 'changeListType');
					cmd.id = row.id;
					cmd.listType = '200';
					cmd.changeCause = value;
					cmd.success = function(data) {
						DialogAlert(row.userName + "加入黑名单成功！");
						pageObj.queryData();
					}
					cmd.execute();
				}
			});
		});
	}
}

// ---------------------------------
// 加入白名单
// ---------------------------------
pageObj.whiteAdd = function() {
	var row = pageObj.gridManager.getSelectedRow();
	if (!row) {
		DialogAlert('请选择要加入白名单的用户！');
		return;
	}
	if(row.listType=='100'){
		DialogAlert(row.userName+'已经加入白名单！');
	}else if ( row.listType == '200') {
		DialogConfirm('是否将' + row.userName + '加入白名单?', function(yn) {
			if (!yn)
				return;
			var cmd = new Command('sysman', 'User', 'changeListType');
			cmd.id = row.id;
			cmd.listType = '100';
			cmd.success = function(data) {
				DialogAlert(row.userName + "加入白名单成功！");
				pageObj.queryData();
			}
			cmd.execute();
		});
	}
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
				+ '&refTableName=sys_user&fieldName=list_type&refId=' + row.id
				+ '&columns=' + encodeURIComponent(JSON.stringify(columns)), // 方式二
		buttons : [ {
			text : '关闭',
			onclick : function(item, dialog) {
				dialog.close();
			}
		} ]
	}, window);
}

// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getUrlParamValue(document.location.href,
			"moduleId");
	/*
	 * pageObj.initGridWidth =
	 * parseInt(Utils.getUrlParamValue(document.location.href, "gridWidth"));
	 * pageObj.initGridWidth = isNaN(pageObj.initGridWidth) ? 0 :
	 * pageObj.initGridWidth; pageObj.initGridHeight =
	 * parseInt(Utils.getUrlParamValue(document.location.href, "gridHeight"));
	 * pageObj.initGridHeight = isNaN(pageObj.initGridHeight) ? 0 :
	 * pageObj.initGridHeight; if (pageObj.initGridWidth > 0 &&
	 * pageObj.initGridWidth <= 630) { $("div .i-user-search").addClass('mt5'); }
	 */
	var selectDataParam = Utils.getUrlParamValue(document.location.href,
			"selectData");
	var cboUserType=Utils.getUrlParamValue(document.location.href,
	"cboUserType");
	if(cboUserType!=undefined && cboUserType!=null && cboUserType!=""){
		$("#cboUserType").val(cboUserType);
		$("#cboUserType").attr("disabled",true);
		pageObj.userType=cboUserType;
	}
	if(selectDataParam!=null && selectDataParam!=undefined && selectDataParam!=""){
		if (selectDataParam == '1') {
			pageObj.selectDataMode = 1;
		} else if (selectDataParam == '2') {
			pageObj.selectDataMode = 2;
		} else{
			pageObj.selectDataMode = 0;
		}
	}else{
		pageObj.selectDataMode = 0;
	}
	if (pageObj.selectDataMode > 0) {
		$("#btnAdd").hide();
		$("#btnDelete").hide();
	}else{
		$("#btnAdd").show();
		$("#btnDelete").show();
	}
	pageObj.initGrid();
	$("#btnQuery").click(pageObj.queryData);
	$("#btnAdd").click(pageObj.newUser);
	$("#btnDelete").click(pageObj.deleteUser);
	$("#btnReadCakey").click(pageObj.readCakey);
	$("#btnBlack").click(pageObj.blackAdd);
	$("#btnWhite").click(pageObj.whiteAdd);
	$("#btnView").click(pageObj.viewHistory);
}

$(document).ready(pageObj.initHtml);
