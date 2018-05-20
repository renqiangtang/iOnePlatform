var pageObj = {};
pageObj.valueTag = 'applyTrust';

pageObj.initPage2 = function() {
	var cmd = new Command();
	cmd.module = "bidder";
	cmd.service = "Apply";
	cmd.method = 'getBillInfo';
	cmd.valueTag = pageObj.valueTag;
	cmd.licenseId = pageObj.licenseId;
	cmd.success = function(data) {
		if ("1" == data.style) {
			$('#radioSpan').html('委托系统自动报价');
			$('#accountDiv').show();
			$('#account').val(data.account);
			$('#account').attr('readonly', true);
		} else {
			$('#radioSpan').html('无');
		}
	};
	cmd.execute();
}

pageObj.getValue = function(cmd) {
	var obj = {};
	var radio = $('input:radio[name="style"]:checked');
	obj.style = radio.val();
	if (obj.style == '1') {
		var v = $('#account').val();
		//
		if (!v) {
			DialogAlert("最高委托金额不能为空！", parent);
			$('#account').focus();
			return false;
		}
		//
		if (isNaN(v)) {
			DialogAlert("最高委托金额不为数字！", parent);
			$('#account').focus();
			return false;
		}
		//
		if (parseFloat(v) < 0) {
			DialogAlert("最高委托金额不能为负数！", parent);
			$('#account').focus();
			return false;
		}
		//
		obj.account = parseFloat(v);
	}

	cmd[pageObj.valueTag] = JSON.stringify(obj);
	return true;
}

pageObj.initPage1 = function() {
	$('input:radio[name="style"]').click(function() {
				var radio = $('input:radio[name="style"]:checked');
				if (radio.val() == '1')
					$('#accountDiv').show();
				else
					$('#accountDiv').hide();
				Utils.autoIframeSize();
			});
}

$(document).ready(function() {
			pageObj.mode = Utils.getPageValue('mode');
			pageObj.licenseId = Utils.getPageValue('licenseId');
			switch (pageObj.mode) {
				case 'edit' :
					pageObj.initPage1();
					break;
				case 'view' :
					pageObj.initPage2();
					break;
			}
			Utils.autoIframeSize();
		});