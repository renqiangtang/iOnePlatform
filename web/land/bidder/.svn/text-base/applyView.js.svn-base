var pageObj = {};

pageObj.applyCorpInfoHide = function(){
	$('#applyCorpInfo').hide();
}
pageObj.initPage = function() {
	pageObj.licenseId = Utils.getPageValue('licenseId');
	comObj.getTransOrganByLicenseId(pageObj.licenseId);
	var cmd = new Command();
	cmd.module = "bidder";
	cmd.service = "Apply";
	cmd.method = "getBillInfo";
	cmd.valueTag = 'steps';
	cmd.licenseId = pageObj.licenseId;
	cmd.success = function(data) {
		document.title = data.targetNo;
		var frameHtml = '';
		for (var i = 0; i < data.steps.length; i++) {
			var name = data.steps[i].license_step_name;
			var url = data.steps[i].module_url + '?mode=view&licenseId=' + pageObj.licenseId;
			if (url.indexOf('applyBank.html') >= 0) {
				name = '入账信息';
				url = comObj.getConfigDoc('入账申请单', 'mode=view2&licenseId=' + pageObj.licenseId);
			}
			if(url.indexOf('applyCorpInfo.html') >= 0){
				frameHtml += '<div class="mod_order" id="applyCorpInfo">';
			}else{
				frameHtml += '<div class="mod_order">';
			}
			frameHtml += '	<div class="inner">';
			frameHtml += '		<div class="hd">';
			frameHtml += '   		 <h3 class="tc">' + name + '</h3>';
			frameHtml += '  	</div>';
			frameHtml += '  <div class="bd">';
			frameHtml += '  		<iframe frameborder="0" width="100%" height="0" src="' + url + '"></iframe>';
			frameHtml += '  </div>';
			frameHtml += ' 	</div>';
			frameHtml += ' </div>';
		}
		$('#frameDiv').html(frameHtml);
	}
	cmd.execute();
}
$(document).ready(pageObj.initPage);