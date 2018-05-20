// ---------------------------------
// 页对象
// ---------------------------------
var pageObj = {};
pageObj.goodsType=null;

pageObj.OKBtn = function(){
	if(pageObj.goodsType=="101"){
		location.href="land/portal/index.html?goodsType=101";
	}else if(pageObj.goodsType=="301"){
		location.href="mine/portal/index.html?goodsType=301";
	}else{
		location.href="land/portal/index.html?goodsType=101";
	}
}

pageObj.CloseBtn = function(){
	location.href="home.html";
}

// ---------------------------------
// 页面初始化
// ---------------------------------
pageObj.initHtml = function() {
	pageObj.goodsType = Utils.getPageValue('goodsType');
	$("#btnOK").click(pageObj.OKBtn);
	$("#btnClose").click(pageObj.CloseBtn);
}

$(document).ready(pageObj.initHtml);