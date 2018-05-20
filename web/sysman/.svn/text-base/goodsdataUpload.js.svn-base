var pageObj = {};
pageObj.initPage = function(){
    pageObj.home = Utils.getPageValue('home');
    pageObj.title = Utils.getPageValue('title');
    pageObj.mode = Utils.getPageValue("mode");
	var userobj  = getUserInfoObj();
	pageObj.organId=userobj.organId;
    if (!pageObj.mode) 
        pageObj.mode = "admin";
    if (pageObj.mode == "view") {
        $("#op").hide();
    }
    if (!pageObj.home) 
        pageObj.home = 'interface';
    if (!pageObj.title) 
        pageObj.title = '配置文件';
    var zNodes = [{
        name: pageObj.title,
        path: "/",
        iconSkin: "home",
        isParent: true
    }];
    //pageObj.tree = $('#dir').tree(pageObj.setting, zNodes);
    //var rootNode = pageObj.tree.getNodes()[0];
    //pageObj.tree.expandNode(rootNode, true, false, true, true);
}

/**
 * 获取绝对路径
 *
 * @param {}
 *            node
 * @return {}
 */
pageObj.getAbsolutePath = function(node){
    return pageObj.home + "/" + node.path;
}

/**
 * 获取相对路径
 *
 * @param {}
 *            node
 * @return {}
 */
pageObj.getRelativePath = function(node){
    return node.path;
}


pageObj.upload = function(){
    if ($('#txtFile').val()) {
        var form = document.getElementById('fileForm');
        form.action = approot + "/upload?module=sysman&service=UploadData&method=uploaddataGoods&path=goods&organId="+ pageObj.organId+"&home=" + encodeURIComponent(pageObj.home);
        form.submit();
    }
    else {
        alert("请先选择需要上传的文件!");
    }
    
}
pageObj.upload1 = function(){
    if ($('#txtFile1').val()) {
        var form = document.getElementById('fileForm1');
        form.action = approot + "/upload?module=sysman&service=UploadData&method=uploadNoticeBefore&path=befor&organId="+pageObj.organId +"&home=" + encodeURIComponent(pageObj.home);
        form.submit();
    }
    else {
        alert("请先选择需要上传的文件!");
    }
    
}
pageObj.upload2 = function(){
    if ($('#txtFile2').val()) {
        var form = document.getElementById('fileForm2');
        form.action = approot + "/upload?module=sysman&service=UploadData&method=uploadNoticeAfter&path=after&organId="+pageObj.organId +"&home=" + encodeURIComponent(pageObj.home);
        form.submit();
    }
    else {
        alert("请先选择需要上传的文件!");
    }
    
}
function formCallback(data) {
	if (data.state == 1) {
		alert("上传成功!");
		$('#txtFile').val("");
		$('#fileUploadDiv').dialog('close');
		
	}
}

pageObj.uploadClick = function(){

    $('#txtFile').val('');
    $('#fileUploadDiv').dialog({
        title: '上传文件',
        modal: true,
        width: 850,
        height: 100
    });
}
$(document).ready(function(){
    pageObj.initPage();
    $('#upLoadBtn').click(pageObj.uploadClick);
    $('#removeBtn').click(pageObj.nodeRemove);
    $("#upLoadingBtn").click(pageObj.upload);
	$("#upLoadingbefore").click(pageObj.upload1);
	$("#upLoadingafter").click(pageObj.upload2);
});

