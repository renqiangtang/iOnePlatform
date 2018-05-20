var pageMyselfObj = {};

pageMyselfObj.userType = null; //用户类型0 内部用户,1竞买人,2银行人员

pageMyselfObj.infoId = null;//用户id： empId ,bidderId

pageMyselfObj.isCompany = null; //是公司还是个人

pageMyselfObj.regTypeSelect = null;

pageMyselfObj.pageStyleSelect = null;

pageMyselfObj.styleName = null;

//-------------------------------------
//页面初始化方法：查询然后加载我的资料
//-------------------------------------
pageMyselfObj.html_init =function() {
	$("#bidderPersonDiv").hide();
	$("#bidderCompanyDiv").hide();
	$("#bidderDiv").hide();
	$("#empDiv").hide();
	$("#bankDiv").hide();
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.method = "getLoginUserData";
	cmd.success = function(data) {
		pageMyselfObj.userType = data.userType;
		pageMyselfObj.initStyleSelect(data.styleName);
		if(data.userType == 0){
			var emp = data.emp;
			pageMyselfObj.showEmp(emp);
		}else if(data.userType == 1){
			var bidder = data.bidder;
			pageMyselfObj.showBidder(bidder);
		}else if(data.userType == 2){
			var bank = data.bank;
			pageMyselfObj.showBank(bank);
		}
	};
	cmd.execute();
}



//----------------------------
//加载内部人员
//----------------------------
pageMyselfObj.showEmp = function(emp){
	$("#bidderPersonDiv").hide();
	$("#bidderCompanyDiv").hide();
	$("#bidderDiv").hide();
	$("#empDiv").show();
	$("#bankDiv").hide();
	pageMyselfObj.infoId = Utils.getRecordValue(emp, 0, 'id');
	$('#emp_name').val(Utils.getRecordValue(emp, 0, 'name'));
	$('input:[name=emp_gender]:radio').each(function (){
		if(this.value == Utils.getRecordValue(emp, 0, 'gender')){
			this.checked = true;  
		}else{
			this.checked=false;
		}
	}); 
	$('#emp_tel').val(Utils.getRecordValue(emp, 0, 'tel'));
	$('#emp_mobile').val(Utils.getRecordValue(emp, 0, 'mobile'));
	$('#emp_email').val(Utils.getRecordValue(emp, 0, 'email'));
}


//----------------------------
//加载竞买人人员
//----------------------------
pageMyselfObj.showBidder = function(bidder){
	$("#empDiv").hide();
	$("#bidderDiv").show();
	$("#bankDiv").hide();
	$('input:[name=bidder_is_company]:radio').each(function (){
		if(this.value == Utils.getRecordValue(bidder, 0, 'is_company')){
			this.checked = true;  
		}else{
			this.checked=false;
		}
	}); 
	pageMyselfObj.isCompany = Utils.getRecordValue(bidder, 0, 'is_company');
	pageMyselfObj.infoId = Utils.getRecordValue(bidder, 0, 'id');
	if(pageMyselfObj.isCompany == 1){
		$("#bidderPersonDiv").hide();
		$("#bidderCompanyDiv").show();
		
	}else{
		$("#bidderCompanyDiv").hide();
		$("#bidderPersonDiv").show();
	}
	$('#company_bidder_type').val(Utils.getRecordValue(bidder, 0, 'bidder_type'));
	$('#company_bidder_name').val(Utils.getRecordValue(bidder, 0, 'name'));
	$('#company_bidder_address').val(Utils.getRecordValue(bidder, 0, 'address'));
	$('#company_bidder_post_code').val(Utils.getRecordValue(bidder, 0, 'post_code'));
	$('#company_bidder_tel').val(Utils.getRecordValue(bidder, 0, 'tel'));
	$('#company_bidder_fax').val(Utils.getRecordValue(bidder, 0, 'fax'));
	$('#company_bidder_mobile').val(Utils.getRecordValue(bidder, 0, 'mobile'));
	$('#company_bidder_email').val(Utils.getRecordValue(bidder, 0, 'email'));
	$('#company_bidder_contact').val(Utils.getRecordValue(bidder, 0, 'contact'));
	$('#company_bidder_certificate_type').val(Utils.getRecordValue(bidder, 0, 'certificate_type'));
	$('#company_bidder_certificate_no').val(Utils.getRecordValue(bidder, 0, 'certificate_no'));
	$('#company_bidder_reg_no').val(Utils.getRecordValue(bidder, 0, 'reg_no'));
	$('#company_bidder_reg_address').val(Utils.getRecordValue(bidder, 0, 'reg_address'));
	$('#company_bidder_reg_capital').val(Utils.getRecordValue(bidder, 0, 'reg_capital'));
	$('#company_bidder_reg_type_select').val(Utils.getRecordValue(bidder, 0, 'reg_type'));
//	pageMyselfObj.regTypeSelect.selectValue(Utils.getRecordValue(bidder, 0, 'reg_type'));
	$('#company_bidder_reg_area').val(Utils.getRecordValue(bidder, 0, 'reg_area'));
	$('#company_bidder_reg_corporation').val(Utils.getRecordValue(bidder, 0, 'reg_corporation'));
	$('#company_bidder_reg_corporation_idno').val(Utils.getRecordValue(bidder, 0, 'reg_corporation_idno'));
	
	$('#person_bidder_type').val(Utils.getRecordValue(bidder, 0, 'bidder_type'));
	$('#person_bidder_name').val(Utils.getRecordValue(bidder, 0, 'name'));
	$('#person_bidder_address').val(Utils.getRecordValue(bidder, 0, 'address'));
	$('#person_bidder_post_code').val(Utils.getRecordValue(bidder, 0, 'post_code'));
	$('#person_bidder_tel').val(Utils.getRecordValue(bidder, 0, 'tel'));
	$('#person_bidder_fax').val(Utils.getRecordValue(bidder, 0, 'fax'));
	$('#person_bidder_mobile').val(Utils.getRecordValue(bidder, 0, 'mobile'));
	$('#person_bidder_email').val(Utils.getRecordValue(bidder, 0, 'email'));
	$('#person_bidder_certificate_type').val(Utils.getRecordValue(bidder, 0, 'certificate_type'));
	$('#person_bidder_certificate_no').val(Utils.getRecordValue(bidder, 0, 'certificate_no'));

	
}

//----------------------------
//加载银行人员
//----------------------------
pageMyselfObj.showBank = function(bank){
	$("#bidderPersonDiv").hide();
	$("#bidderCompanyDiv").hide();
	$("#bidderDiv").hide();
	$("#empDiv").hide();
	$("#bankDiv").show();
	pageMyselfObj.infoId = Utils.getRecordValue(bank, 0, 'id');
	$('#txtBankName').val(Utils.getRecordValue(bank, 0, 'name'));
	$('#txtBankTel').val(Utils.getRecordValue(bank, 0, 'tel'));
	$('#txtBankContact').val(Utils.getRecordValue(bank, 0, 'contact'));
	$('#txtBankCanton').val(Utils.getRecordValue(bank, 0, 'canton_name'));
	$('#txtBankAddress').val(Utils.getRecordValue(bank, 0, 'address'));
}

//----------------------------
//切换竞买人为个人
//----------------------------

pageMyselfObj.changeToPerson = function(){
		$("#bidderPersonDiv").show();
		$("#bidderCompanyDiv").hide();
}

//----------------------------
//切换竞买人为公司
//----------------------------

pageMyselfObj.changeToCompany = function(){
		$("#bidderPersonDiv").hide();
		$("#bidderCompanyDiv").show();
}

//----------------------------
//修改人员信息
//----------------------------
pageMyselfObj.updateInfo = function(){
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.method = "updateLoginUserData";
	cmd.userType = pageMyselfObj.userType ; 
	if(pageMyselfObj.userType == 0){//内部用户
		cmd.id = pageMyselfObj.infoId;
		var gender ;
		$('input:[name=emp_gender]:radio').each(function (){
			if(this.checked == true){
				gender = this.value;  
			}
		}); 
		cmd.gender = gender;
		cmd.tel = $("#emp_tel").val();
		cmd.mobile = $("#emp_mobile").val();
		cmd.email = $("#emp_email").val();
	}else if(pageMyselfObj.userType == 1){//竞买用户
		cmd.id = pageMyselfObj.infoId;
		var is_company ;
		$('input:[name=bidder_is_company]:radio').each(function (){
			if(this.checked == true){
				is_company = this.value;  
			}
		}); 
		if(is_company == 1){
			cmd.address = $('#company_bidder_address').val();
			cmd.post_code = $('#company_bidder_post_code').val();
			cmd.tel = $('#company_bidder_tel').val();
			cmd.fax = $('#company_bidder_fax').val();
			cmd.mobile = $('#company_bidder_mobile').val();
			cmd.email = $('#company_bidder_email').val();
			cmd.contact = $('#company_bidder_contact').val();
			//cmd.certificate_type = $('#company_bidder_certificate_type').val();
			//cmd.certificate_no = $('#company_bidder_certificate_no').val();
			cmd.reg_no = $('#company_bidder_reg_no').val();
			cmd.reg_address = $('#company_bidder_reg_address').val();
			cmd.reg_capital = $('#company_bidder_reg_capital').val();
			cmd.reg_type = $('#company_bidder_reg_type_select').val();
			cmd.reg_area = $('#company_bidder_reg_area').val();
			//cmd.reg_corporation = $('#company_bidder_reg_corporation').val();
			//cmd.reg_corporation_idno = $('#company_bidder_reg_corporation_idno').val();
		}else{
			//cmd.name = $('#person_bidder_name').val();
			cmd.address = $('#person_bidder_address').val();
			cmd.post_code = $('#person_bidder_post_code').val();
			cmd.tel = $('#person_bidder_tel').val();
			cmd.fax = $('#person_bidder_fax').val();
			cmd.mobile = $('#person_bidder_mobile').val();
			cmd.email = $('#person_bidder_email').val();
			//cmd.certificate_type = $('#person_bidder_certificate_type').val();
			//cmd.certificate_no = $('#person_bidder_certificate_no').val();
			cmd.contact = "";
			cmd.reg_no = "";
			cmd.reg_address = "";
			cmd.reg_capital = "";
			cmd.reg_type = "";
			cmd.reg_area = "";
			cmd.reg_corporation = "";
			cmd.reg_corporation_idno = "";
		}
		cmd.is_company = is_company;
	} else if(pageMyselfObj.userType == 2){//银行工作人员
		cmd.id = pageMyselfObj.infoId;
		cmd.tel = $("#txtBankTel").val();
		cmd.contact = $("#txtBankContact").val();
		cmd.address = $("#txtBankAddress").val();
	}
	cmd.success = function(data) {
		if (data.state == 1) {
			DialogAlert('保存登录用户资料成功完成');
		} else {
			DialogError('保存登录用户资料失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

//----------------------------
//初始化风格select
//----------------------------
pageMyselfObj.initStyleSelect = function(style){
	pageMyselfObj.pageStyleSelect = $("#page_style_select").ligerComboBox({
			data: [
			{ text: '默认',id:'default'},
			{ text: '绿色', id:'green' }
			], 
			valueFieldID: 'page_style' , 
			selectBoxWidth:100,
			initValue:style,
			initText:style,
			onSelected: function(value, text){
				pageMyselfObj.changePageStyle(value);
			}
	});
	pageMyselfObj.styleName = style;
}

//----------------------------
//更改风格
//----------------------------
pageMyselfObj.changePageStyle = function(style){
	pageMyselfObj.styleName = style;
	
}

//----------------------------
//保存风格
//----------------------------
pageMyselfObj.saveStyle = function(){
	if(pageMyselfObj.styleName==null || pageMyselfObj.styleName == ""){
		DialogAlert("请选择你需要的样式!");
		return;
	}else{
		var cmd = new Command();
		cmd.module = "sysman";
		cmd.service = "User";
		cmd.method = "changeStyle";
		cmd.styleName = pageMyselfObj.styleName;
		cmd.success = function(data) {
			if (data.state == 1) {
				DialogAlert('保存样式成功!');
			} else {
				DialogError('保存样式失败,错误原因：' + data.message);
				return false;
			}
		};
		cmd.execute();
	}
}

pageMyselfObj.showProjectBidder = function(){
	var obj = {};
	obj.url = "projectBidderList.html";
	// 参数
	obj.param = {
		pd_id : pageMyselfObj.infoId
	};
	var sheight = 550;
	var swidth = 1024;
	obj.feature = 'dialogWidth=' + swidth + 'px;dialogHeight=' + sheight + 'px';
	var returnValue = DialogModal(obj);
}

$(document).ready(function(){
	 $("#projectBidder").click(pageMyselfObj.showProjectBidder);
	 $("#person_bidder_mobile").ligerTip({width: 80, auto: true});
	 $("#company_bidder_mobile").ligerTip({width: 80, auto: true});
//	 $('#btnSave').click(pageMyselfObj.updateInfo);
//	 $('#btnSave1').click(pageMyselfObj.updateInfo);
//	 $('#btnSave2').click(pageMyselfObj.updateInfo);
//	 $('#btnSave3').click(pageMyselfObj.updateInfo);
	 $('#bidder_is_company1').click(function(event) {
			pageMyselfObj.changeToPerson();
	 });
	 $('#bidder_is_company2').click(function(event) {
			pageMyselfObj.changeToCompany();
	 });
//	 pageMyselfObj.regTypeSelect = $("#company_bidder_reg_type_select").ligerComboBox({
//			data: [
//			{ text: '国有企业',id:'国有企业'},
//			{ text: '集体企业', id:'集体企业' },
//			{ text: '股份合作企业', id:'股份合作企业' },
//			{ text: '联营企业', id:'联营企业' },
//			{ text: '有限责任公司', id:'有限责任公司' },
//			{ text: '股份有限公司', id:'股份有限公司' },
//			{ text: '私营企业', id:'私营企业' },
//			{ text: '港、澳、台商投资企业', id:'港、澳、台商投资企业' },
//			{ text: '外商投资企业' , id:'外商投资企业' },
//			{ text: '个体经营', id:'个体经营' }
//			], 
//			valueFieldID: 'company_bidder_reg_type' , 
//			selectBoxWidth :185
//	});
	$('#btnChangeStyle').click(pageMyselfObj.saveStyle);
	pageMyselfObj.html_init();
    var goodsType=Utils.getPageValue('goodsType');
    if(goodsType=="501"){
    	$("#bidderTypeText").html("土地储备机构");
    }else{
    	$("#bidderTypeText").html("个人");
    }
});

