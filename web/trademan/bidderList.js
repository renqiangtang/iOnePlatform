// ---------------------------------
// 页页对象
// ---------------------------------
var pageObj = {};
// ---------------------------------
// 表格对象
// ---------------------------------
pageObj.gridManager = null;
// ---------------------------------
// 数据访问模式。0编辑，1单选，2多选
// ---------------------------------
pageObj.readonly = 0;
pageObj.showCheckbox = 0;
pageObj.userInfo = getUserInfoObj();

pageObj.objGrid = null;
// ---------------------------------
// 查询数据静态条件，由调用处传入
// ---------------------------------
pageObj.staticConditions = null;

// ---------------------------------
// 转换名称
// ---------------------------------
pageObj.nameRender = function(rowdata, rowindex, value) {
	if (pageObj.readonly == 1)
		return value;
	else
		return "<a href='javascript:pageObj.editBidder(\"" + rowdata.id
				+ "\")'>" + value + "</a>";
}

pageObj.telRender = function(rowdata, rowindex, value) {
	if (rowdata.bidder_type == 1)
		return "********";
	else
		return value;
}

// ---------------------------------
// 转换竞买人类型（个人/企业）
// ---------------------------------
pageObj.isCompanyRender = function(rowdata, rowindex, value) {
	var bidderType = rowdata.bidder_type;
	var isCompany = value;
	if (isCompany == 0)
		if (bidderType == 3)
			return "土地储备机构";
		else
			return "个人";
	else
		return "企业";
}

// ---------------------------------
// 转换竞买人是否联合（仅房产）
// ---------------------------------
pageObj.isUnionRender = function(rowdata, rowindex, value) {
	var bidderType = rowdata.bidder_type;
	var isUnion = value;
	if (bidderType == 1 && isUnion == 1)
		return "联合";
	else
		return "";
}

pageObj.bidderTypeRender = function(rowdata, rowindex, value) {
	var bidderType = value;
	if (bidderType == 1)
		return "房产";
	else if (bidderType == 2)
		return "矿产";
	else if (bidderType == 3)
		return "耕指";
	else
		return "土地";
}

// ---------------------------------
// 转换竞买人状态
// ---------------------------------
pageObj.statusRender = function(rowdata, rowindex, value) {
	// 0申请中，1已激活(此状态为正常可用状态)，2冻结，3待年审，4注销，5已提交，6审核未通过退回
	var blnValid = true;
	var validDate = rowdata.valid_date;
	if (validDate) {
		validDate = validDate.toString().substr(0, 10);
		var now = new Date();
		now = now.toString().substr(0, 10);
		blnValid = now < validDate;
	}
	if (value == 0)
		return "申请中";
	else if (value == 1) {
		if (blnValid)
			return "正常";
		else
			return "冻结";
	} else if (value == 2)
		return "冻结";
	else if (value == 3)
		return "待年审";
	else if (value == 4)
		return "注销";
	else if (value == 5)
		return "已提交";
	else if (value == 6)
		return "未通过";// 退回
	else
		return "未知";
}

// ---------------------------------
// 初始化表格
// ---------------------------------
pageObj.initGrid = function() {
	var url = approot
			+ '/data?module=trademan&service=Bidder&method=getBidderListData&moduleId='
			+ pageObj.moduleId + '&u=' +pageObj.userInfo.userId;
	url = Utils.urlAddParams(url, pageObj.getQueryParams());
	pageObj.gridManager = $("#bidderGrid").ligerGrid({
				url : url,
				columns : [{
							display : '名称',
							name : 'name',
							width : 200,
							render : pageObj.nameRender
						}, {
							display : '电话',
							name : 'tel',
							width : 100,
							render : pageObj.telRender
						}, {
							display : '地址',
							name : 'address',
							width : 120
						}, {
							display : '邮编',
							name : 'post_code',
							width : 50
						}, {
							display : '电子邮件',
							name : 'email',
							width : 80
						}, {
							display : '类别',
							name : 'bidder_type',
							width : 50,
							render : pageObj.bidderTypeRender
						}, {
							display : '机构类型',
							name : 'is_company',
							width : 50,
							render : pageObj.isCompanyRender
						}, {
							display : '注册日期',
							name : 'create_date',
							width : 80
						}, {
							display : '有效期',
							name : 'valid_date',
							width : 80
						}, {
							display : '状态',
							name : 'status',
							width : 80,
							render : pageObj.statusRender
						}, {
							display : '是否黑名单',
							name : 'isBlack',
							width : 80,
							render : pageObj.getBlackStatus
						}],
				checkbox : pageObj.showCheckbox == 1,
				pagesizeParmName : 'pageRowCount',// 每页记录数
				pageParmName : 'pageIndex',// 页码数
				sortnameParmName : 'sortField',// 排序列名
				sortorderParmName : 'sortDir',// 排序方向
				pageSizeOptions : [10, 20, 30],
				width : '99.8%',
				height : '100%'
			});
}

pageObj.getQueryParams = function() {
	var queryParams = {};
	if ($("#txtName").val())
		queryParams.name = $("#txtName").val();
	queryParams.bidderType = $("#cboBidderType").val();
	if (!queryParams.bidderType)
		queryParams.bidderType = pageObj.initBidderTypes;
	if ($("#cboIsCompany").val())
		queryParams.isCompany = $("#cboIsCompany").val();
	if ($("#txtCantonHiddenName").val())
		queryParams.cantonName = $("#txtCantonHiddenName").val();
	if ($("#txtAddress").val())
		queryParams.address = $("#txtAddress").val();
	if ($("#cboHasCakey").val())
		queryParams.hasCakey = $("#cboHasCakey").val();
	if ($("#txtCakey").val())
		queryParams.cakey = $("#txtCakey").val();
	if ($("#cboStatus").val())
		queryParams.status = $("#cboStatus").val();
	if (pageObj.staticConditions)
		for (var key in pageObj.staticConditions)
			queryParams[key] = pageObj.staticConditions[key];
	return queryParams;
}

// ---------------------------------
// 查询按钮事件
// ---------------------------------
pageObj.queryData = function(currentPage) {
	if (true == currentPage || !currentPage)
		pageObj.gridManager.refreshPage();
	else {
		var url = approot
				+ '/data?module=trademan&service=Bidder&method=getBidderListData&moduleId='
				+ pageObj.moduleId + '&u=' + getUserInfoObj().userId;
		pageObj.gridManager.refresh(url, pageObj.getQueryParams());
	}
}

pageObj.addStaticCondition = function(name, value) {
	if (!pageObj.staticConditions)
		pageObj.staticConditions = {};
	pageObj.staticConditions[name] = value;
}

// ---------------------------------
// 新增按钮事件
// ---------------------------------
pageObj.newBidder = function() {
	if (pageObj.readonly == 1)
		return;
	var dialog = DialogOpen({
				height : 620,
				width : 700,
				title : '新增竞买人',
				url : approot + '/trademan/bidderEdit.html?moduleId='
						+ pageObj.moduleId + '&id=&bidderType='
						+ pageObj.initBidderTypes + '&goodsType='
						+ pageObj.goodsType
			});
	var manager = {};
	manager.dialog = dialog;
	manager.parentPageObj = pageObj;
	setGlobalAttribute("bidderEditDialog", manager);
}

// ---------------------------------
// 修改按钮事件
// ---------------------------------
pageObj.editBidder = function(bidderId) {
	var editBidderId = bidderId;
	if (bidderId) {
		var selectedRow = pageObj.gridManager.getSelectedRow();
		if (!selectedRow) {
			DialogWarn('请选择要修改的竞买人。');
			return false;
		}
		editBidderId = selectedRow.id;
	}
	var dialog = DialogOpen({
				height : 620,
				width : 800,
				title : '修改竞买人',
				url : approot + '/trademan/bidderEdit.html?moduleId='
						+ pageObj.moduleId + '&id=' + editBidderId
						+ '&readonly=' + pageObj.readonly + '&caManager='
						+ pageObj.caManager + '&bidderType='
						+ pageObj.initBidderTypes + '&goodsType='
						+ pageObj.goodsType
			});
	var manager = {};
	manager.dialog = dialog;
	manager.parentPageObj = pageObj;
	setGlobalAttribute("bidderEditDialog", manager);
}

// ---------------------------------
// 删除按钮事件
// ---------------------------------
pageObj.deleteBidder = function() {
	if (pageObj.readonly == 1)
		return;
	var selectedRow = pageObj.gridManager.getSelectedRow();
	if (!selectedRow) {
		DialogAlert('请选择要删除的竞买人。');
		return false;
	}
	DialogConfirm('删除当前竞买人 ' + selectedRow.name + '？', function(yes) {
				if (yes) {
					var cmd = new Command();
					cmd.module = "trademan";
					cmd.service = "Bidder";
					cmd.method = "deleteBidderData";
					cmd.id = selectedRow.id;
					cmd.success = function(data) {
						var state = data.state;
						if (state == '1') {
							DialogAlert('删除竞买人数据成功完成');
							// pageObj.queryData(pageObj.gridManager.options.page);
							pageObj.gridManager.deleteSelectedRow();
						} else {
							DialogError('删除竞买人失败,错误原因：' + data.message);
							return false;
						}
					};
					cmd.execute();
				}
			});
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
// 新增黑名单
// ---------------------------------
pageObj.btnAddBlack = function() {
	var row = pageObj.gridManager.getSelectedRow();
	if (!row) {
		DialogAlert('请选择要加入黑名单的竞买人。');
		return false;
	}
	var name = row.name;
	var no = row.certificate_no;
	var tel = row.tel;
	var address = row.address;
	if (no == null || no == "null") {
		no = "";
	}
	if (address == null || address == "null") {
		address = "";
	}
	if (tel == null || tel == "null") {
		tel = "";
	}
	var bidder_type = row.is_company;
	var cmd1 = new Command();
	cmd1.module = "sysman";
	cmd1.service = "User";
	cmd1.method = "checkBlackUserByName";
	cmd1.goodsType = pageObj.goodsType;
	cmd1.is_company = bidder_type;
	cmd1.name = name;
	cmd1.no = no;
	cmd1.success = function(data1) {
		if (data1.state != 1) {
			if(pageObj.goodsType !='501'){
				if (bidder_type == 0) {
					DialogAlert("个人竞买人名称：" + name + "<br>证件号：" + no + "<br>已在黑名单中");
				} else {
					DialogAlert("企业竞买人名称：" + name + "已在黑名单中");
				}
			}else {
			if (bidder_type == 0) {
					DialogAlert("土地储备竞买人名称：" + name + "已在黑名单中");
				} else {
					DialogAlert("企业竞买人名称：" + name + "已在黑名单中");
				}
			}
		} else {
			if (pageObj.goodsType == '501') {

				var dialog = DialogOpen({
							height : 480,
							width : 700,
							title : '新增黑名单',
							url : approot
									+ '/plow/bidder/blackUserEdit.html?moduleId='
									+ pageObj.moduleId + '&goodsType='
									+ pageObj.goodsType + '&name=' + name
									+ '&no=' + no + '&biddertype='
									+ bidder_type + '&address=' + address
									+ '&tel=' + tel + '&is_listedCompany='
									+ row.is_listedcompany
						});
				var manager = {};
				manager.dialog = dialog;
				manager.parentPageObj = pageObj;
				setGlobalAttribute("blackUserEditDialog", manager);
			} else {
				var dialog = DialogOpen({
							height : 480,
							width : 700,
							title : '新增黑名单',
							url : approot
									+ '/sysman/blackUserEdit.html?moduleId='
									+ pageObj.moduleId + '&goodsType='
									+ pageObj.goodsType + '&name=' + name
									+ '&no=' + no + '&biddertype='
									+ bidder_type + '&address=' + address
									+ '&tel=' + tel + '&is_listedCompany='
									+ row.is_listedcompany
						});
				var manager = {};
				manager.dialog = dialog;
				manager.parentPageObj = pageObj;
				setGlobalAttribute("blackUserEditDialog", manager);

			}
		}
	}
	cmd1.execute();
}

// ---------------------------------
// 加载前渲染是否黑名单
// ---------------------------------
pageObj.getBlackStatus = function(item) {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.method = "getBlackStatus";
	cmd.name = item.name;// 名称
	cmd.no = item.certificate_no;// 证件号或者组织机构代码
	cmd.is_company = item.is_company;
	cmd.goodsType = pageObj.goodsType;
	var status;
	cmd.success = function(data) {
		status = data.state;
	};
	cmd.execute();
	// pageObj.
	if (status == 1) {
		return "是";
	} else if (status == 2) {
		return "否";
	} else {
		return "错误";
	}

}

// ---------------------------------
// 重置
// ---------------------------------
pageObj.btnReset = function() {
	$("#txtName").val('');
	$("#cboIsCompany").val('');
	$("#txtCantonName").val('');
	$("#txtCantonHiddenName").val('');
	$("#txtAddress").val('');
	$("#cboHasCakey").val('');
	$("#cboStatus").val('');
	$("#txtCakey").val('');

}

pageObj.selCanton = function() {
	var canton = comObj.cantonSelect();
	if (canton != null) {
		$("#txtCantonName").val(canton.fullName);
		$("#txtCantonHiddenName").val(canton.name);
	}
}

pageObj.initt501IsCom = function() {
	$("#cboIsCompany").empty();
	//Utils.addOption("cboisCompany", "0", "土地储备机构");
	//Utils.addOption("cboisCompany", "1", "企业");
	var cbis = document.getElementById("cboIsCompany");
	 cbis.options.add(new Option("","")); 
	 cbis.options.add(new Option("土地储备机构","0")); 
	 cbis.options.add(new Option("企业","1")); 
	//cbis.innerHTML= "<option value='0'>土地储备机构</option><option value='1'>企业</option>";
	//alert("---");
}

// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initHtml = function() {
	pageObj.moduleId = Utils.getPageValue("moduleId");
	var readonly = Utils.getPageValue("readonly");
	if (readonly
			&& (readonly == 1 || readonly.toLowerCase() == "true" || readonly
					.toLowerCase() == "yes"))
		pageObj.readonly = 1;
	else
		pageObj.readonly = 0;
	var caManager = Utils.getPageValue("caManager");
	if (caManager
			&& (caManager == 1 || caManager.toLowerCase() == "true" || caManager
					.toLowerCase() == "yes"))
		pageObj.caManager = 1;
	else
		pageObj.caManager = 0;
	// 是否显示表格复选框
	var showCheckbox = Utils.getPageValue("showCheckbox")
			|| Utils.getPageValue("checkbox");
	if (showCheckbox
			&& (showCheckbox == 1 || showCheckbox.toLowerCase() == "true" || showCheckbox
					.toLowerCase() == "yes"))
		pageObj.showCheckbox = 1;
	else
		pageObj.showCheckbox = 0;
	pageObj.initBidderTypeList = null;
	pageObj.initBidderTypes = "";
	var initBidderTypes = Utils.getPageValue("bidderType");
	if (initBidderTypes) {
		var bidderTypes = Utils.getSubStrs(initBidderTypes, ",", true);
		if (bidderTypes.length > 0) {
			for (var i = 0; i < bidderTypes.length; i++) {
				var bidderType = parseInt(bidderTypes[i]);
				if (!isNaN(bidderType) && (bidderType == 0 || bidderType == 1 || bidderType == 2 || bidderType == 3)) {
					if (!pageObj.initBidderTypeList){
						pageObj.initBidderTypeList = new Array();
					}
					pageObj.initBidderTypeList.push(bidderType.toString());
					if (pageObj.initBidderTypes == "")
						pageObj.initBidderTypes = bidderType.toString();
					else{
						pageObj.initBidderTypes += "," + bidderType.toString();
					}
				}
			}
		}
	}
	
	if (pageObj.initBidderTypeList && pageObj.initBidderTypeList.length >= 0) {
		$("#cboBidderType option").each(function() {
			if ($(this).val() != ""
					&& $.inArray($(this).val(), pageObj.initBidderTypeList) == -1) {
				$(this).remove();
			}
		});
		if (pageObj.initBidderTypeList.length <= 1) {
			$("#cboBidderType").attr("disabled", true);
			$("#cboBidderType ").get(0).selectedIndex = pageObj.initBidderTypeList.length > 0
					? 1
					: 0;
		} else
			$("#cboBidderType ").get(0).selectedIndex = 0;
	}
	if (pageObj.readonly == 0) {
		$("#btnAdd").click(pageObj.newBidder);
		if (pageObj.caManager == 1)
			$("#btnDelete").hide();
		else
			$("#btnDelete").click(pageObj.deleteBidder);
	} else {
		$("#btnAdd").hide();
		$("#btnDelete").hide();
	}
	var staticStatus = Utils.getPageValue("staticStatus");
	if (staticStatus)
		pageObj.addStaticCondition("staticStatus", staticStatus);
	var staticIsCompany = Utils.getPageValue("staticIsCompany");
	if (staticIsCompany)
		pageObj.addStaticCondition("staticIsCompany", staticIsCompany);
	var staticBidderType = Utils.getPageValue("staticBidderType");
	if (staticBidderType)
		pageObj.addStaticCondition("staticBidderType", staticBidderType);
	var staticExcludeId = Utils.getPageValue("staticExcludeId");
	if (staticExcludeId)
		pageObj.addStaticCondition("staticExcludeId", staticExcludeId);
	pageObj.initGrid();
	$("#btnQuery").click(pageObj.queryData);
	$("#txtCantonName").click(pageObj.selCanton);
	$("#btnReadCakey").click(pageObj.readCakey);
	$("#btnAddBlack").click(pageObj.btnAddBlack);
	$("#btnReset").click(pageObj.btnReset);
	$("#showProjectBidder").click(pageObj.showProjectBidder);
	pageObj.goodsType = Utils.getUrlParamValue(document.location.href,
			"goodsType");
	if (pageObj.goodsType == '501')
		pageObj.initt501IsCom();
}

pageObj.showProjectBidder = function(){
	var row = pageObj.gridManager.getSelectedRow();
	if (!row) {
		DialogAlert('请选择要查看股东的竞买人。',window);
		return false;
	}
	var obj = {};
	obj.url = approot+ '/sysman/projectBidderListManage.html';
	// 参数
	obj.param = {
		pd_id : row.id
	};
	var sheight = 550;
	var swidth = 1024;
	obj.feature = 'dialogWidth=' + swidth + 'px;dialogHeight=' + sheight + 'px';
	var returnValue = DialogModal(obj);
}

$(document).ready(pageObj.initHtml);