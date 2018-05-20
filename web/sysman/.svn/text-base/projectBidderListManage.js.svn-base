var pageObj = {};
pageObj.pd_id= null;
pageObj.init =function() {
//	var cmd = new Command();
//	cmd.module = "sysman";
//	cmd.service = "User";
//	cmd.method = "getProjectBidderList";
//	cmd.success = function(data) {
//		pageMyselfObj.userType = data.userType;
//		pageMyselfObj.initStyleSelect(data.styleName);
//		if(data.userType == 0){
//			var emp = data.emp;
//			pageMyselfObj.showEmp(emp);
//		}else if(data.userType == 1){
//			var bidder = data.bidder;
//			pageMyselfObj.showBidder(bidder);
//		}else if(data.userType == 2){
//			var bank = data.bank;
//			pageMyselfObj.showBank(bank);
//		}
//	};
//	cmd.execute();
//	
	var url = approot
	+ '/data?module=sysman&service=User&method=getProjectBidderList&pd_id='+pageObj.pd_id+'&status=1';
	pageObj.grid = $("#grid").ligerGrid({
		url : url,
		columns : [{
					display : '股东名称',
					name : 'name',
					width : '20%',
					render : function(r){
						return "<a href=\"javascript:void(0)\" onclick=\"javascript:pageObj.editProjectBidder('"+r.id+"'); return false;\">"+r.name+"</a>"
					}
				}, {
					display : '组织机构代码',
					name : 'idno',
					width : '15%'
				}, {
					display : '地址',
					name : 'address',
					width : '15%'
				}, {
					display : '电话',
					name : 'tel',
					width : '15%'
				}, {
					display : '电子邮箱',
					name : 'email',
					width : '15%'
				}, {
					display : '股份比率',
					name : 'percent',
					width : '10%',
					render: function(r){
						return r.percent+"%";
					}
				}, {
					display : '状态',
					name : 'status',
					width : '10%',
					render: function(r){
						if(r.status==0){
							return "已保存";
						}else{
							return "已提交";
						}
					}
				}, {
					display : '类型',
					name : 'biddertype',
					width : '10%',
					render: function(r){
						if(r.biddertype==0){
							return "个人";
						}else{
							return "企业";
						}
					}
				}, {
					display : '备注',
					name : 'reamrk',
					width : '10%'
				}],
		isScroll : true,// 是否滚动
		pageSizeOptions : [10, 20, 30],
		showTitle : false,
		rownumbers : true,
		selectRowButtonOnly : true,
		height : 435,
		checkbox : true
	});
}

//打开编辑页面
pageObj.editProjectBidder = function(id){
	var obj = {};
	obj.url = "projectBidderEditManage.html";
	// 参数
	if(id==null || typeof(id)=="object" || typeof(id)=="undefined" || id=="null"){
		id="";
	}
	obj.param = {
		pd_id : pageObj.pd_id,
		id :id
	};
	var sheight = 550;
	var swidth = 1024;
	obj.feature = "dialogWidth=600px;dialogHeight=450px";
	var returnValue = DialogModal(obj);
	if(returnValue==true){
		pageObj.query();
	}
}

pageObj.deleteProjectBidder = function(){
	var arr = pageObj.grid.getCheckedArr();
	if(arr.length<1){
		DialogAlert("请选择要删除的数据！",window);
		return;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.method = "deleteProjectBidder";
	cmd.id = arr.join(",");
	cmd.success = function(data) {
		pageObj.query();
	};
	cmd.execute();
}

pageObj.searchProjectBidder = function(){
	pageObj.query();
}

pageObj.query = function() {
	var paraObj = {
		name:$("#name").val()
	};
	var url = approot
	+ '/data?module=sysman&service=User&method=getProjectBidderList&pd_id='+pageObj.pd_id+'&status=1';
	pageObj.grid.refresh(url, paraObj);
}


$(document).ready(function(){
	pageObj.pd_id = Utils.getPageValue('pd_id');
	pageObj.init();
	$("#addProjectBidder").click(pageObj.editProjectBidder);
	$("#deleteProjectBidder").click(pageObj.deleteProjectBidder);
	$("#searchProjectBidder").click(pageObj.searchProjectBidder);
	
});

