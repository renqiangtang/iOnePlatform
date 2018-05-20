var pageObj = {};

pageObj.init = function() {
//	$('#td').attr("href",'land/portal/index.html?goodsType=101');
//	$('#kc').attr("href","mine/portal/index.html?goodsType=301");
//	$('#gd').attr("href","plow/portal/index.html?goodsType=501");

	$('#td').attr("href",'explain.html?goodsType=101');
	$('#kc').attr("href","explain.html?goodsType=301");
	
	$('#tdDemo').attr("href",'login.html?noca=1&goodsType=101&versionMode=demo');
	$('#kcDemo').attr("href","login.html?noca=1&goodsType=301&versionMode=demo");
	$('#gdDemo').attr("href","login.html?noca=1&goodsType=501&versionMode=demo");
}

$(document).ready(function() {
	pageObj.init();
});
