var pageObj = {};
pageObj.id = null;

pageObj.init = function() {
	$('#display_content').hide();
	$('#display_btn').hide();
	//pageObj.def();
	$("#cboIsCompany").change(pageObj.isCompanyChange);
	pageObj.isCompanyChange();
	$('#save').click(pageObj.save);
	//
	$('#name').keyup(pageObj.key);
	$('#mobile').keyup(pageObj.key);
	$('#contact').keyup(pageObj.key);
	$('#reg_corporation').keyup(pageObj.key);
	$('#reg_corporation_idno').keyup(pageObj.key);
	$('#cakey').keyup(pageObj.key);
	
	$('#getCaBtn').click(pageObj.getCa);
	$('#update').click(pageObj.update);
}

pageObj.isCompanyChange = function() {
	var cboIsCompany = $('#cboIsCompany').val();
	$('ul li').each(function(i, ele1) {
				var tag = $(ele1).attr("tag");
				if (tag.indexOf(cboIsCompany) >= 0) {
					$(ele1).show();
					$('input[type="text"]', ele1).each(function(i, ele2) {
								$(ele2).val('');
							});
					$('textarea', ele1).each(function(i, ele2) {
								$(ele2).val('');
							});
				} else {
					$(ele1).hide();
				}
			});
	if (cboIsCompany == '1')
		$('#listedcompany').show();
	else
		$('#listedcompany').hide();
}

pageObj.save = function() {
	var b = pageObj.check();
	if (b) {
		var cboIsCompany = $('#cboIsCompany').val();
		var cmd = new Command("trademan", "Bidder", "updateBidderData");
		$('ul li').each(function(i, ele1) {
					var tag = $(ele1).attr("tag");
					if (tag.indexOf(cboIsCompany) >= 0) {
						$('input[type="text"]', ele1).each(function(i, ele2) {
									var id = $(ele2).attr("id");
									var value = $(ele2).val();
									cmd[id] = value;
								});
						$('textarea', ele1).each(function(i, ele2) {
									var id = $(ele2).attr("id");
									var value = $(ele2).val();
									cmd[id] = value;
								});
					}
				});
		cmd.is_company = 0;
		cmd.id = pageObj.id;
		cmd.user_id = pageObj.userId;
		cmd.cakey_id = pageObj.cakeyId;
		if (cboIsCompany == "1" && "checked" == $('#is_listedcompany').attr("checked")) {
			cmd.is_company = 1
		}
		cmd.success = function(data) {
			var state = data.state;
			if (state == 1) {
				pageObj.id = data.id;
				pageObj.userId = data.userId;
				pageObj.cakeyId = data.cakeyId;
				DialogConfirm('保存竞买人成功,立即去登陆界面？', function(yes) {
							if (!yes)
								return;
							var protocol = window.location.protocol + "//";
							var host = window.location.host;
							location.href = protocol + host + approot
						});
			} else {
				DialogError('保存竞买人失败,错误原因：' + data.message);
			}
		};
		cmd.execute();
	}

}

pageObj.key = function() {
	var ele = this;
	var id = $(ele).attr("id");
	var v = $(ele).val();
	if (v.length > 0)
		$('#' + id + 'Tip').html('');
}

pageObj.check = function() {
	var cboIsCompany = $('#cboIsCompany').val();
	if (cboIsCompany == "0") {
		if ($('#name').val().trim().length == 0) {
			$('#nameTip').html('名称不能为空!');
			$('#name').focus();
			return false;
		}
		if ($('#mobile').val().trim().length == 0) {
			$('#mobileTip').html('移动电话不能为空!');
			$('#mobile').focus();
			return false;
		}
		if ($('#reg_corporation_idno').val().trim().length == 0) {
			$('#reg_corporation_idnoTip').html('身份证号不能为空!');
			$('#reg_corporation_idno').focus();
			return false;
		}
		if ($('#cakey').val().trim().length == 0) {
			$('#cakeyTip').html('CAKEY不能为空!');
			$('#cakey').focus();
			return false;
		}
	} else if (cboIsCompany == "1") {
		if ($('#name').val().trim().length == 0) {
			$('#nameTip').html('名称不能为空!');
			$('#name').focus();
			return false;
		}
		if ($('#mobile').val().trim().length == 0) {
			$('#mobileTip').html('移动电话不能为空!');
			$('#mobile').focus();
			return false;
		}
		if ($('#contact').val().trim().length == 0) {
			$('#contactTip').html('联系人不能为空!');
			$('#contact').focus();
			return false;
		}
		if ($('#reg_corporation').val().trim().length == 0) {
			$('#reg_corporationTip').html('法人不能为空!');
			$('#reg_corporation').focus();
			return false;
		}
		if ($('#reg_corporation_idno').val().trim().length == 0) {
			$('#reg_corporation_idnoTip').html('法人身份证号不能为空!');
			$('#reg_corporation_idno').focus();
			return false;
		}
		if ($('#cakey').val().trim().length == 0) {
			$('#cakeyTip').html('CAKEY不能为空!');
			$('#cakey').focus();
			return false;
		}
	}
	return true;
}
pageObj.getCa = function() {
	
     var map=ca.getOU();
     /**
     证书主题项:C=CN, S=SD, L=jinan, O=河源市公共资源交易管理工作领导小组办公室, 
     * OU=IDliming --登陆ID,ID, 
     * OU=IXXXXXXXX--身份证,I, 
     * CN=XXXXX--名称
     * OU=JYPOXXXX--移动电话，JYPO；
     * OU=JYCOXXXX--联系人，JYCO；
     * OU=JYRExxxx--境内外，JYRE；
     * OU=JYLPXXXX--法人，JYLP；
     * OU=JYPPXXXX--是否上市公司，JYPP
     * OU=JYICXXXX--法人身份证号，JYIC
    **/
     if(!map.isEmpty()){
     	$('#cakey').val(map.get('key'));
     	$('#name').val(map.get('name'));
     	$('#mobile').val(map.get('JYPO'));
     	$('#reg_corporation_idno').val(map.get('JYIC'));
     	$('#contact').val(map.get('JYCO'));
     	$('#reg_corporation').val(map.get('JYLP'));
     	$('#is_listedcompany').prop("checked",function(i,val){
     		if(map.get('JYPP')=='是')
     		    return true;
     		else
     		    return false;
     		
     	});
     	$('#is_oversea').val(function(){
     		if(map.get('JYRE')=='境外')
     		    return '2';
     		else
     		    return '1';    
     	});
     	$('#cboIsCompany').val('1');
     	$('#display_content').show();
     	$('#display_btn').show();
     }else{
     	alert('读取CA信息有误，请检查CA初始化是否正确！');
     	return false;
     }
}
pageObj.def=function(){
		$('#cakey').prop('readonly',true);
		$('#name').prop('readonly',true);
		$('#mobile').prop('readonly',true);
		$('#reg_corporation_idno').prop('readonly',true);
		$('#contact').prop('readonly',true);
		$('#reg_corporation').prop('readonly',true);
		$("#cboIsCompany").click(function(){
			$('#cboIsCompany').val('1');
			pageObj.isCompanyChange();
		});
		//$('#is_listedcompany').prop('readonly',true);
		//$('#is_oversea').prop('readonly',true);
		//$('#cboIsCompany').prop('readonly',true);
}

pageObj.update=function(){
	if($("#cakey").val().trim().length==0){
		pageObj.getCa();
		$('#display_content').show();
	}
	var b = pageObj.check();
	if (b) {
		var cboIsCompany = $('#cboIsCompany').val();
		var cmd = new Command("trademan", "Bidder", "updateBidderUser");
		$('ul li').each(function(i, ele1) {
					var tag = $(ele1).attr("tag");
					if (tag.indexOf(cboIsCompany) >= 0) {
						$('input[type="text"]', ele1).each(function(i, ele2) {
									var id = $(ele2).attr("id");
									var value = $(ele2).val();
									cmd[id] = value;
								});
						$('textarea', ele1).each(function(i, ele2) {
									var id = $(ele2).attr("id");
									var value = $(ele2).val();
									cmd[id] = value;
								});
					}
				});
		cmd.is_listedcompany = 0;
		if (cboIsCompany == "1" && "checked" == $('#is_listedcompany').attr("checked")) {
			cmd.is_listedcompany = 1
		}
		cmd.success = function(data) {
			var state = data.state;
			if (state == 1) {
				pageObj.id = data.id;
				pageObj.userId = data.userId;
				pageObj.cakeyId = data.cakeyId;
				DialogConfirm('修改竞买人信息成功,立即去登陆界面？', function(yes) {
							if (!yes)
								return;
							var protocol = window.location.protocol + "//";
							var host = window.location.host;
							location.href = protocol + host + approot
						});
			} else {
				DialogError('修改竞买人信息失败,错误原因：' + data.message);
			}
		};
		cmd.execute();
	}
	
}
$(document).ready(pageObj.init);