var pageObj = {}
pageObj.goodsId = null;
pageObj.targetId = null;
pageObj.mode = null;
pageObj.tabManager = null;
pageObj.attachLoaded = false;
pageObj.conFirm=0;//等于1时表示审核
pageObj.isrr=-1;//0表示当前审核人点击标识正确 1表示点了标识错误
pageObj.noticestatus=0;

// ---------------------------------
// 初始化页面上的区域
// ---------------------------------
pageObj.intiPage = function() {
	// 
	$("#provide_date").click(function() {
				WdatePicker({
							dateFmt : 'yyyy-MM-dd'
						})
			});
	// 土地用途
	Utils.addOption('goods_use_select', "", "--请选择--");
	for (var pro in comObj.use_type) {
		Utils.addOption('goods_use_select', comObj.use_type[pro],pro);
	}
	// 区域
//	var cmd = new Command();
//	cmd.module = "before";
//	cmd.service = "TransGoodsLand";
//	cmd.method = "cantonList";
//	cmd.goodsId = pageObj.goodsId;
//	cmd.u = pageObj.u;
//	cmd.success = function(data) {
//		$("#canton_id").empty();
//		Utils.addOption('canton_id', "","--请选择--");
//		for (var i = 0; i < data.canton.length; i++) {
//			Utils.addOption('canton_id', data.canton[i][0],data.canton[i][1]);
//		}
//	};
//	cmd.execute();
}

// ---------------------------------
// 保存数据
// ---------------------------------
pageObj.save = function() {
	if (!Utils.requiredCheck())
		return;
	
	var plot1_down=$("#plot1_down").val();
	var plot1_up=$("#plot1_up").val();
	var build_density_down=$("#build_density_down").val();
	var build_density_up=$("#build_density_up").val();
	var build_height=$("#build_height").val();
	var build_height2=$("#build_height2").val();
	var green_ratio_down=$("#green_ratio_down").val();
	var green_ratio_up=$("#green_ratio_up").val();
	if(plot1_down.trim()=="" && plot1_up.trim()=="" && build_density_down.trim()=="" && build_density_up.trim()=="" &&
			build_height.trim()=="" && build_height2.trim()=="" && green_ratio_down.trim()=="" && green_ratio_up.trim()==""){
		DialogAlert("容积率、建筑密度(%)、建筑高度(米)、绿地率(%)不能同时为空!");
		return;
	}
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsLand";
	cmd.method = "saveTransGoods";
	cmd.mode = pageObj.mode;
	cmd.targetId = pageObj.targetId;
	cmd.goodsId = pageObj.goodsId;
	cmd.goods_type = '101';
	cmd.u = pageObj.u;
	cmd.address=$("#no").val();
	var transLandTypeVale="";
	$("input:checkbox[name=transLandType]:checked'").each(function(){ 
            if($(this).attr("checked")){
            	transLandTypeVale += $(this).val()+","
            }
    });
	if(transLandTypeVale!="" && transLandTypeVale.substring(transLandTypeVale.length-1,transLandTypeVale.length)==","){
		transLandTypeVale=transLandTypeVale.substring(0,transLandTypeVale.length-1);
	}
	cmd.transLandTypeValue=transLandTypeVale;
	Utils.getForm(cmd);
	cmd.success = function(data) {
		DialogAlert(data.message);
		if (data.state == 1) {
			if(pageObj.mode == 'update'){
				pageObj.mode == 'update';
			}else{
				pageObj.mode = 'modify';
			}
			pageObj.goodsId = data.goodsId;
			pageObj.btnDis();
			window.returnValue = true;
		}
	};
	cmd.execute();
}

// -------------------------------------------
// 获取页面数据函数
// -------------------------------------------
pageObj.refresh = function() {
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransGoodsLand";
	cmd.method = "getTransGoodsData";
	cmd.goodsId = pageObj.goodsId;
	cmd.tabName="goods";
	cmd.success = function(data) {
		Utils.setForm(data);
		var transLandTypeValue=data.transLandTypeValue;
		if(transLandTypeValue!="" && transLandTypeValue!=undefined){
			var transLandTypeValues=transLandTypeValue.split(",");
			$("input:checkbox[name=transLandType]'").each(function(){ 
				for(var i=0;i<transLandTypeValues.length;i++){
					if(this.value==transLandTypeValues[i]){
						$("#"+this.id).attr("checked", true);
					}
					
				}
	    });
		}
		//加载审核信息
		if((pageObj.noticestatus==3 && pageObj.conFirm==1) || (pageObj.noticestatus==4 && (pageObj.conFirm==0 || pageObj.conFirm==null))){
			var confirmData=data.noticeConfirmData;
			comObj.setConfirm(confirmData);
		}
		//土地用途显示加载
//	    $("#goods_use_select").val(data.goods_use);
		
		pageObj.btnDis();
	};
	cmd.execute();
}

// -------------------------------------------
// 设置页面输入元素为只读
// -------------------------------------------
pageObj.setReadOnly = function() {
	$('input').attr("readonly", true);
	$('select').attr("readonly", true);
}

//// -------------------------------------------
//// 初始化标签页
//// -------------------------------------------
//pageObj.initTab = function() {
//	pageObj.tabManager = $("#tabPanel").ligerTab({
//				contextmenu : false,
//				onAfterSelectTabItem : pageObj.afterSelectTabItem
//			});
//}

// ---------------------------------
// 点击附件
// ---------------------------------
pageObj.initAttachList = function(){
	var initWidth = 800;
	var initHeight = 550;
	var grant = {add: 2, edit: 2, del: 2,
		addDtl: 2, editDtl: 2, delDtl: 2,
		uploadFile: 2, downloadFile: 2, delFile: 2};
	var owners = new Array();
	var owner = {};
	owner.id = pageObj.goodsId;
	owner.name = "交易物";
	owner.title = $("#no").val();
	owner.tableName = "trans_goods";
	owner.templetNo = "businessType101001GoodsAttach";
	owner.grant = grant;
	owners.push(owner);
	obj.param = {};
	obj.param.u=pageObj.u;
	obj.param.owners=encodeURIComponent(JSON.stringify(owners));;
	
	// 地址
	obj.url =approot + '/sysman/attachList.html';
	
	// 窗口参数
	obj.feature = "dialogWidth=900px;dialogHeight=600px";
	var rv = DialogModal(obj);
}
//-------------------------------------------
//按扭的禁用与启用
//-------------------------------------------
pageObj.btnDis = function (){
	if (pageObj.goodsId != null && pageObj.goodsId != "") {
		$("#btnSave").removeAttr("disabled");
		$("#relAttach").removeAttr("disabled");
	}else{
		$("#btnSave").removeAttr('disabled');
		$("#relAttach").attr('disabled',"true");
	}
}

pageObj.bodyClick = function(e){
	comObj.clickBody(e,pageObj.goodsId,"goods");
}
// -------------------------------------------
// 初始化页面函数
// -------------------------------------------
$(document).ready(function() {
			pageObj.mode = Utils.getPageValue("mode");
			pageObj.targetId = Utils.getPageValue("targetId");
			pageObj.goodsId = Utils.getPageValue("goodsId");
			pageObj.u = Utils.getPageValue("u");
			pageObj.noticestatus=Utils.getPageValue("noticestatus");
			pageObj.conFirm=Utils.getPageValue('conFirm');//获取是否审核页面
			pageObj.btnDis();
			  var columns = [
			                 { header: 'ID', name: 'id', width: 80 },
			                 { header: '名字', name: 'name', width: 170 },
			                 { header: '描述', name: 'desc', width: 170 }
			             ];

			$("#txt3").ligerComboBox({
				columns: columns,
				  valueField: 'id',
                  textField: 'name', 
                  selectBoxWidth: 400,
				  autocomplete: true
			});
			switch (pageObj.mode) {
				case comObj.mode['new'] :
					pageObj.intiPage();
					$('#btnDel').hide();
					break;
				case comObj.mode['modify'] :
				case comObj.mode['update'] :
					pageObj.intiPage();
					pageObj.refresh();
					$('#btnDel').hide();
					break;
				case comObj.mode['view'] :
					pageObj.intiPage();
					pageObj.refresh();
					$('#btnSave').hide();
					pageObj.setReadOnly();
					break;
				default :
					DialogAlert("mode is null");
			}
			$('#btnSave').click(pageObj.save);
			$('#btnDel').click(pageObj.del);
			$('#relAttach').click(pageObj.initAttachList);
//			$("#goods_use").change(function(){
//				$("#use_years").val(comObj.use_type_years[this.value]);
//			});
			if(pageObj.noticestatus!=null && pageObj.noticestatus==3){
				if(pageObj.conFirm==null || pageObj.conFirm==0){//如果不是审核
					$("#rightBtn").hide();
					$("#errorBtn").hide();
				}else{
					$("#rightBtn").show();
					$("#errorBtn").show();
					$("#rightBtn").click(function(){
						pageObj.isrr=0;
					});
					$("#errorBtn").click(function(){
						pageObj.isrr=1;
					});
					$("body").click(pageObj.bodyClick);
				}
			}else{
				$("#rightBtn").hide();
				$("#errorBtn").hide();
			}
		});