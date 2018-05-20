var pageObj = {};
pageObj.valueTag = 'applyBank';

pageObj.initPage1 = function() {
	pageObj.targetId = Utils.getPageValue('targetId');
	var cmd = new Command();
	cmd.module = 'bidder';
	cmd.service = 'Apply';
	cmd.method = 'initTransAccounts';
	cmd.targetId = pageObj.targetId;
	cmd.success = function(data) {
		var html = '';
		for ( var pro in data.banks) {
			html += '<h3 class="pt10">币种:' + comObj.Currency[pro] + '</h3>';
			html += '<ul class="bank_list">';
			var bks = data.banks[pro];
			for ( var j = 0; j < bks.length; j++) {
				var imgEnd = "";
				if(bks[j].is_outside){
					imgEnd = "_01";
				}
				html += '<li><input type="radio" name="bankAccount" value="'
						+ bks[j].account_id
						+ '"/><img src="images\\base\\bank\\' + bks[j].alias_id+imgEnd
						+ '.gif"/>';
				html += '</li>';
			}
			html += '</ul>';
		}
		$('#bankDiv').html(html);
	}
	cmd.execute();
}


pageObj.getValue = function(cmd) {
	var obj = {};
	var radio = $('input:radio[name="bankAccount"]:checked');
	if (!radio.val()) {
		DialogAlert("请选择银行！", parent);
		return false;
	} else {
		obj.bankAccount = radio.val();
		cmd[pageObj.valueTag] = JSON.stringify(obj);
	}
	return true;
}

$(document).ready(function(){
	pageObj.mode=Utils.getPageValue('mode');
	pageObj.targetId=Utils.getPageValue('targetId');
	pageObj.u=getUserId();
	switch(pageObj.mode){
	case 'edit':
		pageObj.initPage1();
		break;
	}
	Utils.autoIframeSize();
});