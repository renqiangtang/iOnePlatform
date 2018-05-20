var pageOrganInfoObj = {};
pageOrganInfoObj.organId=null;

pageOrganInfoObj.checkFileType = function (fileInputName, fileType, fileLabel) {
	if (!fileInputName || !fileType)
		return true;
	var fileName = $('#' + fileInputName).val();
	if (!fileName)
		return true;
	var strFileLabel = fileLabel ? fileLabel : '';
	var pos = fileName.lastIndexOf("\\");
	fileName = fileName.substring(pos + 1);
	pos = fileName.lastIndexOf(".");
	if (pos >= 0) {
		var fileExt = fileName.substring(pos);
		if (fileExt) {
			fileExt = fileExt.toLowerCase();
			if (!Utils.paramExists(fileType, fileExt,
					true, "=", ";")) {
				DialogError(strFileLabel + '附件文件类型必须为'
						+ fileType.replaceAll(";", "、")
						+ '，选择的文件类型不正确。');
				return false;
			}
		}
	} else {
		DialogError(strFileLabel + '附件文件类型为'
				+ fileType.replaceAll(";", "、")
				+ '，选择的文件类型不正确。');
		return false;
	}
	return true;
}

//---------------------------------
//表单提交 解决文件上传和表单数据一次性提交
//---------------------------------
pageOrganInfoObj.submit=function(){
	if (!pageOrganInfoObj.checkFileType('logo', '.jpg;.jpeg;.gif;.png', 'logo图标'))
		return false;
	if (!pageOrganInfoObj.checkFileType('head_left', '.jpg;.jpeg;.gif;.png', '左边横幅'))
		return false;
	var form=$$("#infoForm");
	form.action=approot+"/upload?module=sysman&service=OrganInfo&method=submitInfo&organId="+pageOrganInfoObj.organId 
	form.submit();		
}

//---------------------------------
//表单初始化
//---------------------------------
pageOrganInfoObj.reset=function(){
	var form=$$("#infoForm");
	form.reset();		
}

//---------------------------------
//回调函数
//---------------------------------
function formCallback(dataObj){
	alert(dataObj.message);
}

//---------------------------------
//加载默认设置
//---------------------------------
pageOrganInfoObj.initHtml = function(){
	var cmd = new Command();
    cmd.module = "sysman";
    cmd.service = "OrganInfo";
    cmd.method = "getOrganInfo";
    cmd.organId=pageOrganInfoObj.organId;
    cmd.success = function (data) {
    	pageOrganInfoObj.showData(data);
	};
	cmd.execute();
}

//---------------------------------
//显示数据
//---------------------------------
pageOrganInfoObj.showData = function(data){
	for(var i=0;i<data.rs.length;i++){
		var fieldNo = Utils.getRecordValue(data,i,"field_no");
		var fieldValue = Utils.getRecordValue(data,i,"field_value");
		if(fieldNo == "logo" || fieldNo == "head_left"){
			if(fieldValue!=null && fieldValue!="" && fieldValue!="null"){
				$("#"+fieldNo+"Span").html("文件已上传");
			}
		}else{
			$("#"+fieldNo+"").val(fieldValue);
		}
	}
}

//---------------------------------
//页面初始化
//---------------------------------
$(document).ready(function(){
	pageOrganInfoObj.organId = (window.dialogArguments && window.dialogArguments.organId)||Utils.getUrlParamValue(document.location.href, "organId");
	pageOrganInfoObj.initHtml();
	Utils.autoIframeSize();
});
