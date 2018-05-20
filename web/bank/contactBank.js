//---------------------------------
//页页对象
//---------------------------------
var pageContactBankObj = {};

//---------------------------------
//页面初始化
//---------------------------------
pageContactBankObj.initHtml = function() {
	pageContactBankObj.moduleId = Utils.getUrlParamValue(document.location.href, "moduleId");
	pageContactBankObj.userId = getUserId();
	var cmd = new Command();
	cmd.module = "trade";
	cmd.service = "Bank";
	cmd.method = "getBankListData";
	cmd.success = function(data) {
		if (data.Total > 0) {
			for(var i = 0; i < data.Rows.length; i++) {
				$("#" + data.Rows[i].no + "Tel").html(data.Rows[i].tel);
			}
		}
		else {
			$("#JSYHTel").html("");
			$("#ZSYHTel").html("");
			$("#GSYHTel").html("");
		}
	};
	cmd.execute();
}

$(document).ready(pageContactBankObj.initHtml);


