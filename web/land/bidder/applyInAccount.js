var pageObj = {};

pageObj.initPage1 = function() {
	var cmd = new Command();
	cmd.module = "bidder";
	cmd.service = "Apply";
	cmd.method = 'getApplyInAccount';
	cmd.licenseId = pageObj.licenseId;
	cmd.success = function(data) {
		pageObj.setInAccountInfo(data);
		var html = '&nbsp;';
		for (var i = 0; i < data.unit.length; i++) {
			html += data.unit[i].name + '(' + data.unit[i].percent + '%)&nbsp;';
		}
		$('#msg').html(html);
	};
	cmd.execute();
}

pageObj.setInAccountInfo = function(data) {
	$('#accountName').html(data.child_account_name);
	$('#accountNo').html(data.child_account_no);
	$('#accountBank').html(data.child_account_bank);
	$('#accountCurrency').html(data.currency);
	$('#accountSum').html(data.amount);
	$('#licenseNo').html(data.order_no);
	$('#targetNo').html(data.no);
	$('#unitStyleName').html(data.unitStyleName);
}

pageObj.initPage2 = function() {
	var cmd = new Command();
	cmd.module = 'bidder';
	cmd.service = "Apply";
	cmd.method = 'getApplyInAccount';
	cmd.licenseId = pageObj.licenseId;
	cmd.success = function(data) {
		pageObj.setInAccountInfo(data);
		$('#inAccountTitle').hide();
		$('#inAccountHead').hide();
		$('#inAccountFoot').hide();
	};
	cmd.execute();
}

$(document).ready(function() {
			pageObj.mode = Utils.getPageValue('mode');
			pageObj.licenseId = Utils.getPageValue('licenseId');
			switch (pageObj.mode) {
				case 'view1' :// 模式1
					pageObj.initPage1();
					break;
				case 'view2' :// 模式2
					pageObj.initPage2();
					break;
			}
			Utils.autoIframeSize();
			$('#btnClose').click(function(e) {
						window.returnValue = true;
						window.close();
					});
		});