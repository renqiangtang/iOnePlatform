// ---------------------------------
// 页对象
// ---------------------------------
var pageObj = {};
pageObj.licenseId =null;

pageObj.OKBtn = function(){
	pageTradeCommonObj.viewTransCertNotice(pageObj.licenseId);
}


// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initHtml = function() {
	pageObj.licenseId = Utils.getPageValue('licenseId');
	$("#btnOK").click(pageObj.OKBtn);
}

$(document).ready(pageObj.initHtml);