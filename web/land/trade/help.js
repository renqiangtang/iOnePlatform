//---------------------------------
//页页对象
//---------------------------------
var pageHelpObj = {}
//---------------------------------
//标签TAB数据加载标识
//---------------------------------
pageHelpObj.welcomeDataLoaded = false;
pageHelpObj.operatorGuideDataLoaded = false;
pageHelpObj.transGuideDataLoaded = false;
pageHelpObj.downloadDataLoaded = false;
pageHelpObj.otherDataLoaded = false;

pageHelpObj.tabManager = null;

//---------------------------------
//标签TAB切换前事件
//---------------------------------
pageHelpObj.beforeSelectTabItem = function(tabid) {
 		
}

//---------------------------------
//标签TAB切换后事件
//---------------------------------
pageHelpObj.afterSelectTabItem = function(tabid) {
	if (tabid == 'tabitem1') {//欢迎使用
		if (!pageHelpObj.welcomeDataLoaded) {
			pageHelpObj.welcomeDataLoaded = true;
		}
 	} else if (tabid == 'tabitem2') {//操作手册
 		if (!pageHelpObj.operatorGuideDataLoaded) {
 			pageHelpObj.loadOperatorGuide();
			pageHelpObj.operatorGuideDataLoaded = true;
		}
 	} else if (tabid == 'tabitem3') {//竞买指南
 		if (!pageHelpObj.transGuideDataLoaded) {
 			pageHelpObj.loadTransGuide();
			pageHelpObj.transGuideDataLoaded = true;
		}
 	} else if (tabid == 'tabitem4') {//下载
 		if (!pageHelpObj.downloadDataLoaded) {
			pageHelpObj.downloadDataLoaded = true;
		}
 	} else if (tabid == 'tabitem5') {//其它
 		if (!pageHelpObj.otherDataLoaded) {
			pageHelpObj.otherDataLoaded = true;
		}
 	}
}

//---------------------------------
//加载操作指南
//---------------------------------
pageHelpObj.loadOperatorGuide = function() {
	//pageHelpObj.tabManager.overrideSelectedTabItem({text: '操作手册', tabid: 'operatorGuide', url: approot + '/help/help_index.html', showClose: false });
}

//---------------------------------
//加载竞买指南
//---------------------------------
pageHelpObj.loadTransGuide = function() {
	return;
	var params = {
		SwfFile : encodeURI(approot + "/test/swf/notice/{Paper[*,0].swf,4}"),
		EncodeURI: true,
		Scale : 1.0
	}
	swfobject.embedSWF(approot + "/base/flash/paperView.swf?v=1.1d6", "transGuideContent", "965", "468", "9.0.0",
		approot + "/base/flash/PaperView.swf/expressInstall.swf", params);
}

//---------------------------------
//标签TAB初始化
//---------------------------------
pageHelpObj.initTab = function() {
	pageHelpObj.tabManager = $("#tabHelp").ligerTab({
		contextmenu: false,
		height: 498,
		onAfterSelectTabItem: pageHelpObj.afterSelectTabItem
     });
}

//---------------------------------
//页面初始化
//---------------------------------
pageHelpObj.initHtml = function() {
	pageHelpObj.initTab();
	pageHelpObj.afterSelectTabItem('tabitem1');
	pageHelpObj.downloadfile();
}

//---------------------------------
//下载文件url汉字转码
//---------------------------------
pageHelpObj.downloadfile = function(){
	//pageHelpObj.fileUrl = "http://192.168.5.235:5500/std/download?module=base&service=BaseDownload&method=directDownLoad&file="+docName+".doc";	
	//location.href = encodeURI(encodeURI(pageHelpObj.fileUrl));
	$("#download").empty();
	
	
	pageHelpObj.downloadUri = "/std/download?module=base&service=BaseDownload&method=directDownLoad&file=";
	pageHelpObj.downloadHtml = "<table width='100%' align='center' class='i-grid-panel-default'>"
	                         + "<thead><tr><td class='tc'>序号</td><td class='t2'>标题</td><td class='tc'>操作</td></tr></thead>"
							 + "<tr><td width='60' height='26' class='tc'>1</td>"
							 + "<td width='700' class='t2'><a href="+ pageHelpObj.downloadUri+"SDCASetup.rar>USB KEY设备驱动</a></td>"
							 + "<td class='tc'><a href="+ pageHelpObj.downloadUri+"SDCASetup.rar>下载</a></td></tr>"
							 + "<tr><td height='26' class='tc'>2</td>"
							 + "<td class='t2'><a href="+ pageHelpObj.downloadUri+"JMXZYB.doc>竞买须知样本</a></td>"
							 + "<td class='tc'><a href="+ pageHelpObj.downloadUri+"JMXZYB.doc>下载</a></td></tr>"
							 + "<tr><td height='26' class='tc'>3</td>"
							 + "<td class='t2'><a href="+ pageHelpObj.downloadUri+"CJQRSYB.doc>成交确认书样本</a></td>"
							 + "<td class='tc'><a href="+ pageHelpObj.downloadUri+"CJQRSYB.doc>下载</a></td></tr>"
							 + "<tr><td height='26' class='tc'>4</td>"
							 + "<td class='t2'><a href="+ pageHelpObj.downloadUri+"JMSQSYB.doc>竞买申请书样本</a></td>"
							 + "<td class='tc'><a href="+ pageHelpObj.downloadUri+"JMSQSYB.doc>下载</a></td></tr>"
							 + "</td></tr></table>";
							 
	$("#download").html(pageHelpObj.downloadHtml);
}



$(document).ready(pageHelpObj.initHtml);