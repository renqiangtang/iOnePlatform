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
// 单位ID
// ---------------------------------
pageObj.initOrganId = '';

// ---------------------------------
// 转换名称
// ---------------------------------
pageObj.nameRender = function (row, rowNamber){
	var name = row.name;
	if (pageObj.selectDataMode > 0) {
		return name;
	} else {
		return "<a href='javascript:pageObj.editEmp()'>" + name + "</a>";
	}
}

// ---------------------------------
// 转换人员状态
// ---------------------------------
pageObj.statusRender = function (row, rowNamber){
	var isValid = Number(row.isValid).toFixed(0);
	if (isValid == 1) {
		return "有效";
	} else
		return "无效";
}

// ---------------------------------
// 初始化表格
// ---------------------------------
pageObj.initGrid = function () {
	pageObj.gridManager = $("#empGrid").ligerGrid({
		url: approot + '/data?module=sysman&service=Emp&method=getEmpListData&moduleId=' + pageObj.moduleId + '&organId=' + pageObj.initOrganId,
		checkbox: pageObj.selectDataMode == 2,
		columns: [
			{ display: '姓名', name: 'name', width :'10%', render: pageObj.nameRender},
			{ display: '编号', name: 'no', width :'10%'},
			{ display: '性别', name: 'gender', width :'10%'},
			{ display: '电话', name: 'tel', width :'10%'},
			{ display: '移动电话', name: 'mobile', width :'10%'},
			{ display: '电子邮件', name: 'email', width :'10%'},
			{ display: '所属部门', name: 'relDepartments', isSort: false, width :'150'},
			{ display: '所任岗位', name: 'relJobs', isSort: false, width :'150'},
			{ display: '所属角色', name: 'relRoles', isSort: false, width :'150'},
			{ display: '状态', name: 'isValid', width :'10%', render: pageObj.statusRender },
			{ display: '创建日期', name: 'createDate', width :'10%'},
			{ display: '备注', name: 'remark', width :'20%'}
		],
		pagesizeParmName: 'pageRowCount',// 每页记录数
		pageParmName: 'pageIndex',// 页码数
		sortnameParmName: 'sortField',// 排序列名
		sortorderParmName: 'sortDir',// 排序方向
		isScroll: true,// 是否滚动
		rownumbers : true,
		pageSizeOptions: [10, 20, 30], 
		showTitle: false,
		width: '99.8%',
		height: 390,
		detail: false,
		onError: function (a, b) {}
	});
}

// ---------------------------------
// 查询数据
// ---------------------------------
pageObj.queryData = function () {
	var empName = $("#txtName").val();
	var empGender = $("#cboGender").val();
	var empMobile = $("#txtMobile").val();
	var empEmail = $("#txtEmail").val();
	var empIsValid = $("#cboIsValid").val();
	var empHasCakey = $("#cboHasCakey").val();
	var empCakey = $("#txtCakey").val();
	var queryParams = {organId: pageObj.initOrganId, empName: empName, empGender: empGender,
		empMobile: empMobile, empEmail: empEmail, empIsValid: empIsValid,
		empHasCakey: empHasCakey, empCakey: empCakey};
	var url = approot + '/data?module=sysman&service=Emp&method=getEmpListData&moduleId=' + pageObj.moduleId;
	pageObj.gridManager.refresh(url,queryParams);
}

// ---------------------------------
// 新增按钮事件
// ---------------------------------
pageObj.newEmp = function () {
	DialogOpen({height:590, width:650, title: '新增人员', url: approot + '/sysman/empEdit.html?moduleId=' + pageObj.moduleId
		+ '&id=&organId=' + pageObj.initOrganId
		+ '&u=' + pageObj.userId});
}

// ---------------------------------
// 修改按钮事件
// ---------------------------------
pageObj.editEmp = function () {
	var selectedRow = pageObj.gridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要修改的人员。');
		return false;
	}
	DialogOpen({height: 585, width: 650, title: '修改人员', url: approot + '/sysman/empEdit.html?moduleId=' + pageObj.moduleId
		+ '&id=' + selectedRow.id
		+ '&u=' + pageObj.userId});
}

// ---------------------------------
// 删除按钮事件
// ---------------------------------
pageObj.deleteEmp = function () {
	var selectedRow = pageObj.gridManager.getSelectedRow();
	if (selectedRow == null) {
		DialogWarn('请选择要删除的人员。');
		return false;
	}
	DialogConfirm('删除当前人员 ' + selectedRow.name + '？', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "sysman";
			cmd.service = "Emp";
			cmd.method = "deleteEmpData";
			cmd.id = selectedRow.id;
			cmd.success = function (data) {
				var state = data.state;
				if(state == '1') {
					DialogAlert('删除人员数据成功完成');
					pageObj.gridManager.deleteSelectedRow();
				} else {
					DialogError('删除人员失败,错误原因：' + data.message);
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
pageObj.getSelectedEmpData = function () {
    return pageObj.gridManager.getSelectedRow();
}

pageObj.getCheckedEmpData = function () {
    return pageObj.gridManager.getCheckedRows();
}

// ---------------------------------
// 读取Cakey按钮事件
// ---------------------------------
pageObj.readCakey = function () {
	DialogConfirm('请插上新的CAKey后点击“是”按钮！', function (yes) {
		if (yes) {
			var caKeyId = ca.readId();
			$("#txtCakey").val(caKeyId);
		}
	}
	);
}

pageObj.addOldUser = function(){
		DialogOpen({
			height : 500,
			width : 900,
			title : '选择新增用户',
			url : approot
					+ '/sysman/userList.html?selectData=2&gridWidth=400&gridHeight=255&cboUserType=0',
			buttons : [{
				text : '确定',
				onclick : function(item, dialog) {
					var selectedDataFn = dialog.frame.pageObj.getCheckedUserData
							|| dialog.frame.window.pageObj.getCheckedUserData;
					var selectedData = selectedDataFn();
					if (selectedData.length == 0) {
						DialogWarn('请选择要添加的用户。');
						return false;
					}
					for (var i = 0; i < selectedData.length; i++) {
						var id = selectedData[i].refId;
						if(selectedData[i].organName!=""){
							DialogConfirm(selectedData[i].userName+"已经在"+selectedData[i].organName+"是否要这个用户移到目前操作的部门下？", function (yes) {
								if (yes) {
									for (var row in pageObj.gridManager.records) {
										if (pageObj.gridManager.records[row].id == id) {
											alert(id);
											break;
										}else{
											//添加
											var cmd = new Command();
											cmd.module = "sysman";
											cmd.service = "User";
											cmd.method = "updateEmpOrgan";
											cmd.id = id;
											cmd.organ_id=pageObj.initOrganId;
											alert(pageObj.initOrganId);
											cmd.success = function (data) {
												if(data.state==1){
													DialogAlert("添加用户成功！");
													dialog.close();
												}
											}
											cmd.execute();
										}
									}
								}
						});
						
						}
					}
					
				}
			}, {
				text : '取消',
				onclick : function(item, dialog) {
					dialog.close();
				}
			}]
		});
}

// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getPageValue("moduleId");
	pageObj.initOrganId = Utils.getPageValue("organId");
	pageObj.userId = getUserId();
	var selectDataParam = Utils.getPageValue("selectData");
	if (selectDataParam == '1') {
		pageObj.selectDataMode = 1;
	} else if (selectDataParam == '2') {
		pageObj.selectDataMode = 2;
	} else {
		pageObj.selectDataMode = 0;
	}
	if (pageObj.selectDataMode > 0) {
		$("#btnAdd").hide();
		$("#btnDelete").hide();
		$("#btnOldAdd").hide();
	}
	pageObj.initGrid();
	$("#btnQuery").click(pageObj.queryData);
	$("#btnAdd").click(pageObj.newEmp);
	$("#btnDelete").click(pageObj.deleteEmp);
	$("#btnReadCakey").click(pageObj.readCakey);
	$("#btnOldAdd").click(pageObj.addOldUser);
}

$(document).ready(pageObj.initHtml);


