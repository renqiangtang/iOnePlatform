var pageObj = {};

pageObj.initPage = function() {
	var path = Utils.getPageValue('path');
	var count = Utils.getPageValue('count');
	//alert(path + "  " + count);

	var params = {
		SwfFile : encodeURI(approot + path + "/{Paper[*,0].swf," + count + "}"),
		EncodeURI : true,
		Scale : 1.0
	}
	swfobject.embedSWF(approot + "/base/flash/paperView.swf?v=1.1d6", "should", "968", "550", "9.0.0", approot + "/base/flash/PaperView.swf/expressInstall.swf", params);
}

$(document).ready(function() {
	$('#btnOk').click(function() {
		window.parent.pageObj.viewApplyLicense('must');
	});
	$('#btnClose').click(function() {
		window.parent.close();
	});
	pageObj.initPage();
//	Utils.autoIframeSize();
});