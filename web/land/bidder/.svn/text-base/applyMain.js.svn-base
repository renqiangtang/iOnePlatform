var pageObj = {};
pageObj.goodsType = null;

// --------------------------------------------
// 竞买须知
// --------------------------------------------
pageObj.initPage = function() {
	var cmd = new Command("bidder", "Apply", "initShould");
	cmd.targetId = pageObj.targetId;
	cmd.success = function(data) {
		if ("path" in data) {
			var obj = {};
			obj.path = data.path;
			obj.count = data.count;
			var url = approot + '/land/bidder/applyShould.html?path=' + data.path + '&count=' + data.count;
			$$('#iframe').src = url;
		} else
			pageObj.viewApplyLicense('must');
	}
	cmd.execute();
}

pageObj.viewApplyLicense = function(modeV) {
	var url = comObj.getConfigDoc('竞买申请书');
	url += '&targetId=' + pageObj.targetId;
	url += '&u=' + pageObj.u;
	url += '&mode=' + modeV;
	url += '&noticeName=' + pageObj.noticeName;
	url += '&targetName=' + pageObj.targetName;
	url += '&userName=' + pageObj.userName;
	url += '&businessType=' + pageObj.businessType;
	url += '&licenseId=' + pageObj.licenseId;
	$$('#iframe').src = url;
	$(document).scrollTop(0);

}

pageObj.viewApply = function() {
	var url = approot + '/land/bidder/apply.html';
	url += '?u=' + pageObj.u;
	url += '&targetId=' + pageObj.targetId;
	url += '&noticeName=' + pageObj.noticeName;
	url += '&targetName=' + pageObj.targetName;
	url += '&userName=' + pageObj.userName;
	url += '&goodsType=' + pageObj.goodsType;
	url += '&transTypeId=' + pageObj.transTypeId;
	url += '&licenseId=' + pageObj.licenseId;
	$$('#iframe').src = url;
	$(document).scrollTop(0);
}

$(document).ready(function() {
	pageObj.u = getUserId();
	pageObj.targetId = Utils.getPageValue('targetId');
	pageObj.licenseId = Utils.getPageValue('licenseId');
	pageObj.noticeName = Utils.getPageValue('noticeName');
	pageObj.targetName = Utils.getPageValue('targetName');
	pageObj.userName = Utils.getPageValue('userName');
	pageObj.goodsType = Utils.getPageValue('goodsType');
	pageObj.businessType = Utils.getPageValue('businessType');
	pageObj.transTypeId = Utils.getPageValue('transTypeId');
	comObj.getTransOrganByTargetId(pageObj.targetId);
	pageObj.initPage();
});