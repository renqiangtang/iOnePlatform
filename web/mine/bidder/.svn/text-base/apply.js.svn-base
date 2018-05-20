var pageObj = {};
pageObj.goodsType = null;
pageObj.corpName = "";
pageObj.corpId = "";
pageObj.corpUrl = "";
pageObj.corpVal = "";
pageObj.allowTrust=null;
pageObj.initPage = function() {
	cmd = new Command();
	cmd.module = "bidder";
	cmd.service = "Apply";
	cmd.method = "initLicenseEditStep";
	cmd.u = pageObj.u;
	cmd.targetId = pageObj.targetId;
	cmd.success = function(data) {
		var frameHtml = '';
		for ( var i = 0; i < data.steps.length; i++) {
			var name = data.steps[i].license_step_name;
			var url = data.steps[i].module_url + '?mode=edit&targetId=' + pageObj.targetId + '&u=' + pageObj.u;
			var id = data.steps[i].id;
			if(name=="拟成立项目公司"){
//		        frameHtml += '<div><input id="corp" name="corp" type="checkbox">拟成立项目公司</input>（若打勾则以下*号项必填!）</div>';
//				frameHtml += '<div class="mod_order" id="corporation" style="display:none"></div>';
//				pageObj.corpName = name;
//				pageObj.corpId = id;
//				pageObj.corpUrl = url;
			}else{
				if(name=="委托报价"){
					if(pageObj.allowTrust==1 ){
						frameHtml += '<div class="mod_order" style="height:96px;">';
						frameHtml += '	<div class="inner">';
						frameHtml += '		<div class="hd">';
						frameHtml += '   		 <h3 class="tc">' + name + '</h3>';
						frameHtml += '   		 <a name="' + id + '"></a>';
						frameHtml += '  	</div>';
						frameHtml += '  <div class="bd">';
						frameHtml += '  		<iframe frameborder="0" width="100%" height="0" src="' + url + '"></iframe>';
						frameHtml += '  </div>';
						frameHtml += ' 	</div>';
						frameHtml += ' </div>';
					}else{
						
					}
				}else{
					frameHtml += '<div class="mod_order">';
					frameHtml += '	<div class="inner">';
					frameHtml += '		<div class="hd">';
					frameHtml += '   		 <h3 class="tc">' + name + '</h3>';
					frameHtml += '   		 <a name="' + id + '"></a>';
					frameHtml += '  	</div>';
					frameHtml += '  <div class="bd">';
					frameHtml += '  		<iframe frameborder="0" width="100%" height="0" src="' + url + '"></iframe>';
					frameHtml += '  </div>';
					frameHtml += ' 	</div>';
					frameHtml += ' </div>';
				}
			}
			
		}
		$('#frameDiv').html(frameHtml);
		//$("#corp").bind("click",pageObj.corporationCheck);
//		if(corp != null && corp != ""){
//			$("#corporation").hide();
//		}
	}
	cmd.execute();
	//
	$('#saveBtn').click(pageObj.save);
}
pageObj.corporationCheck = function(){
	if ($("#corp").is(":checked")) {  
		pageObj.showCorporation();
		pageObj.corpVal = "0";
	}else{
		pageObj.hideCorporation();
		pageObj.corpVal = "1";
	}
}
pageObj.showCorporation = function(){
	var frameHtml="";
	frameHtml += '	<div class="inner">';
	frameHtml += '		<div class="hd">';
	frameHtml += '   		 <h3 class="tc">' + pageObj.corpName + '</h3>';
	frameHtml += '   		 <a name="' + pageObj.corpId + '"></a>';
	frameHtml += '  	</div>';
	frameHtml += '  <div class="bd">';
	frameHtml += '  		<iframe frameborder="0" width="100%" height="0" src="' + pageObj.corpUrl + '"></iframe>';
	frameHtml += '  </div>';
	frameHtml += ' 	</div>';
	$('#corporation').html(frameHtml);
	$("#corporation").show();
}
pageObj.hideCorporation = function(){
	$("#corporation").hide();
}
// --------------------------------------------
// 保存信息
// --------------------------------------------
pageObj.save = function() {
	var cmd1 = new Command();
	cmd1.module = "sysman";
	cmd1.service = "User";
	cmd1.method = "checkBlackUser";
	cmd1.u = pageObj.u;
	cmd1.goodsType = pageObj.goodsType;
	cmd1.success = function(data1) {
		if(data1.state!=1){
			DialogAlert(data1.message);
			return;
		}
	var cmd = new Command();
	var iframes = document.getElementsByTagName('iframe');
	for ( var i = 0; i < iframes.length; i++) {
		if ("getValue" in iframes[i].contentWindow.pageObj) {
			var b = iframes[i].contentWindow.pageObj.getValue(cmd);
			if (!b)
				return;
		}
	}
	cmd.module = "bidder";
	cmd.service = "Apply";
	cmd.method = 'saveLicenseApply';
	cmd.u = pageObj.u;
	cmd.targetId = pageObj.targetId;
	cmd.success = function(data) {
		if (data.state == 1) {
			window.returnValue = true;
			var obj = {};
			obj.param = {
				mode : 'view1',
				licenseId : data.licenseId
			};
			obj.feature = "dialogWidth=1000px;dialogHeight=600px";
			obj.url = comObj.getConfigDoc('入账申请单');
			var v = DialogModal(obj);
			// if (!v)
			window.close();
		} else {
			window.returnValue = false;
			DialogAlert(data.message);
		}
	}
	cmd.execute();
	}
	cmd1.execute();
}

pageObj.viewApplyLicense = function(modeV) {
	var obj = {
		mode : modeV,
		noticeName : pageObj.noticeName,
		targetName : pageObj.targetName,
		userName : pageObj.userName
	};
	var url = comObj.getConfigDoc('竞买申请书');
	url += '&targetId=' + pageObj.targetId;
	url += '&u=' + pageObj.u;
	// alert(url);
	var v = window.showModalDialog(url, obj, "dialogWidth=850px;dialogHeight=550px");
	if (modeV == 'must' && !v)
		window.close();
}

$(document).ready(function() {
	pageObj.u = getUserId();
	pageObj.targetId = Utils.getPageValue('targetId');
	pageObj.noticeName = Utils.getPageValue('noticeName');
	pageObj.targetName = Utils.getPageValue('targetName');
	pageObj.userName = Utils.getPageValue('userName');
	pageObj.goodsType = Utils.getPageValue('goodsType');
	pageObj.allowTrust = Utils.getPageValue('allowTrust');
	pageObj.initPage();
	comObj.getTransOrganByTargetId(pageObj.targetId);
});