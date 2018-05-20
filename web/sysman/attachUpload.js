//---------------------------------
//页面对象
//---------------------------------
var pageAttachObj = {};
// ---------------------------------
// 对话框打开时记录对话框引用
// ---------------------------------
pageAttachObj.dialogManager = null;
// ---------------------------------
// 记录打开本页面的页面对象引用
// ---------------------------------
pageAttachObj.parentPageObj = null;
// ---------------------------------
// 标识上传进行中
// ---------------------------------
pageAttachObj.uploading = false;
// ---------------------------------
// 标识上传过
// ---------------------------------
pageAttachObj.uploaded = 0;
// ---------------------------------
// 系统模板中设置限制文件大小，0表示未限制
// ---------------------------------
pageAttachObj.limitFileSize = 0;
// ---------------------------------
// 系统模板中设置限制文件类型
// ---------------------------------
pageAttachObj.limitFileType = null;
// ---------------------------------
// 选择文件的大小，-1表示未取到大小
// ---------------------------------
pageAttachObj.fileSize = -1;
// ---------------------------------
// 显示文件大小行
// ---------------------------------
pageAttachObj.showFileSize = false;
// ---------------------------------
// 显示文件类别行
// ---------------------------------
pageAttachObj.showFileType = true;

// ---------------------------------
// 上传文件
// ---------------------------------
pageAttachObj.uploadFile = function() {
	var form = document.getElementById('fileForm');
	form.action = approot
			+ "/upload?module=base&service=BaseUpload&method=uploadFile"
			+ "&subDir=common&refTableName=sys_attach_dtl&refId="
			+ pageAttachObj.id;
	form.submit();
	pageAttachObj.uploading = true;
	pageAttachObj.uploaded = 1;
	$("#txtFile").attr("disabled", true);
	$("#txtRename").attr("disabled", true);
	$("#txtRemark").attr("disabled", true);
	$("#btnUpload input").val("上传中...");
	$("#btnUpload input").attr("disabled", true);
}

// ---------------------------------
// 检查调用页面对象
// ---------------------------------
pageAttachObj.checkParentObj = function() {
	if (pageAttachObj.dialogManager == null
			|| pageAttachObj.parentPageObj == null) {
		var randomVarPrefix = Utils.getPageValue("randomVarPrefix");
		var manager = getGlobalAttribute(randomVarPrefix
				+ "_attachUploadDialog");
		if (manager) {
			pageAttachObj.dialogManager = manager.dialog;
			pageAttachObj.parentPageObj = manager.parentPageObj;
			removeGlobalAttribute(randomVarPrefix + "_attachUploadDialog");
		}
	}
	return pageAttachObj.dialogManager != null
			&& pageAttachObj.parentPageObj != null;
}

// ---------------------------------
// 检查上传文件
// ---------------------------------
pageAttachObj.checkUploadData = function() {
	var form = $("#fileForm")[0];
	if (form.txtFile.value == '') {
		DialogError('请选择需要上传的附件');
		return false;
	}
	if (pageAttachObj.limitFileSize > 0) {
		if (pageAttachObj.fileSize == -1)
			alert("由于浏览器环境问题未取得选择文件的大小，因此无法判断上传文件是否符合系统要求的大小，请自行判断。不符合要求的文件大小将在服务器端拒绝。");
		else {
			var fileSize = parseInt((pageAttachObj.fileSize / 1024).toFixed(0));
			if (fileSize > pageAttachObj.limitFileSize) {
				DialogError('附件文件过大');
				return false;
			}
		}
	}
	var fileName = form.txtFile.value;
	var pos = fileName.lastIndexOf("\\");
	fileName = fileName.substring(pos + 1);
	if (pageAttachObj.limitFileType) {
		pos = fileName.lastIndexOf(".");
		if (pos >= 0) {
			var fileExt = fileName.substring(pos);
			if (fileExt) {
				fileExt = fileExt.toLowerCase();
				if (!Utils.paramExists(pageAttachObj.limitFileType, fileExt,
						true, "=", ";")) {
					DialogError('当前附件节点系统模板中设置的附件文件类型为'
							+ pageAttachObj.limitFileType.replaceAll(";", "、")
							+ '，选择的文件类型不正确。');
					return false;
				}
			}
		} else {
			DialogError('当前附件节点系统模板中设置的附件文件类型为'
					+ pageAttachObj.limitFileType.replaceAll(";", "、")
					+ '，选择的文件类型不正确。');
			return false;
		}
	}
	return true;
}

// ---------------------------------
// 上传完成回调函数
// ---------------------------------
pageAttachObj.checkUploadResult = function(data) {
	if (data.state == 1)
		pageAttachObj.uploaded = 2;
	pageAttachObj.uploading = false;
	if (pageAttachObj.uploaded == 2) {
		DialogAlert('上传附件已成功完成');
		$("#txtFile").attr("disabled", false);
		$("#txtRename").attr("disabled", false);
		$("#txtRemark").attr("disabled", false);
		$("#btnUpload input").val("上传");
		$("#btnUpload input").attr("disabled", false);
		if (pageAttachObj.uploaded == 2)// 上传成功自动关闭
			pageAttachObj.closeWindow();
	} else if (data.message)
		DialogError(data.message);
	else
		DialogError('上传操作失败');
}

// ---------------------------------
// 保存按钮事件
// ---------------------------------
pageAttachObj.saveData = function() {
	if (!pageAttachObj.checkUploadData())
		return false;
	var form = $("#fileForm")[0];
	var fileName = form.txtFile.value;
	var pos = fileName.lastIndexOf("\\");
	fileName = fileName.substring(pos + 1);
	var rename = $("#txtRename").val();
	if (rename) {
		var pos = fileName.lastIndexOf(".");
		if (pos >= 0)
			fileName = rename + fileName.substring(pos);
		else
			fileName = rename;
	}
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Attach";
	cmd.method = "updateAttachDtlData";
	cmd.id = "";
	cmd.file_name = fileName;
	cmd.attach_id = pageAttachObj.attachId;
	cmd.remark = $("#txtRemark").val();
	cmd.success = function(data) {
		var state = data.state;
		if (state == '1') {
			pageAttachObj.id = data.id;
			pageAttachObj.uploadFile();
		} else {
			DialogError('上传附件失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

pageAttachObj.getFileSize = function (file) {
	var isIE = !!window.ActiveXObject;
	var fileSize = -1;
	if (isIE) {
//		var image = new Image();
//		image.dynsrc = filePath;
//		alert(image.fileSize);
		try {
			var fso = new ActiveXObject("Scripting.FileSystemObject");
			fileSize = fso.GetFile(file.value).size;
		} catch(e) {
			fileSize = -1;
		}
	} else
		fileSize = file.files[0].size;
	pageAttachObj.fileSize = fileSize;
	var fileSizeLabel = null;
	var decimal = 2;
	if (fileSize < 0)
		fileSizeLabel = '无法获取文件大小';
	else if (fileSize == 0)
		fileSizeLabel = '0Byte';
	else if (fileSize >= 1024 * 1024 * 1024 * 1024)
		fileSizeLabel = (fileSize / (1024 * 1024 * 1024 * 1024)).toFixed(decimal) + 'TB';
	else if (fileSize >= 1024 * 1024 * 1024)
		fileSizeLabel = (fileSize / (1024 * 1024 * 1024)).toFixed(decimal) + 'GB';
	else if (fileSize >= 1024 * 1024)
		fileSizeLabel = (fileSize / (1024 * 1024)).toFixed(decimal) + 'MB';
	else if (fileSize >= 1024)
		fileSizeLabel = (fileSize / 1024).toFixed(decimal) + 'KB';
	else
		fileSizeLabel = fileSize.toFixed(decimal) + 'Byte';
	$("#txtFileSize").val(fileSizeLabel);
}

// ---------------------------------
// 关闭对话框
// ---------------------------------
pageAttachObj.closeWindow = function() {
	if (pageAttachObj.uploading) {
		DialogError('上传附件暂未完成，请稍后再关闭');
		return false;
	}
	if (pageAttachObj.checkParentObj()) {
		if (pageAttachObj.uploaded > 0) {
			if (pageAttachObj.parentPageObj
					&& pageAttachObj.parentPageObj.getAttachDtlData)
				pageAttachObj.parentPageObj.getAttachDtlData();
		}
		if (pageAttachObj.dialogManager)
			pageAttachObj.dialogManager.close();
	}
}

pageAttachObj.convertFileSize = function(fileSize, addFileSizeUnit, decimal) {
	if (!decimal)
		decimal = 2;
	var result = 0;
	var resultUnit = 'byte';
	if (fileSize > 0) {
		if (fileSize >= 1024 * 1024 * 1024) {
			result = parseFloat((fileSize / (1024 * 1024 * 1024)).toFixed(decimal));
			resultUnit = "TB";
		} else if (fileSize >= 1024 * 1024) {
			result = parseFloat((fileSize / (1024 * 1024)).toFixed(decimal));
			resultUnit = "GB";
		} else if (fileSize >= 1024) {
			result = parseFloat((fileSize / 1024).toFixed(decimal));
			resultUnit = "MB";
		} else {
			result = parseFloat(fileSize);
			resultUnit = "KB";
		}
	}
	if (addFileSizeUnit)
		return result + resultUnit;
	else
		return result;
}

// ---------------------------------
// 页面初始化
// ---------------------------------
pageAttachObj.initHtml = function() {
	pageAttachObj.moduleId = Utils.getPageValue("moduleId");
	pageAttachObj.id = null;
	pageAttachObj.attachId = Utils.getPageValue("attachId");
	pageAttachObj.limitFileType = Utils.getPageValue("allowFileType") || Utils.getPageValue("limitFileType");
	if (pageAttachObj.limitFileType && pageAttachObj.limitFileType != "null" && pageAttachObj.limitFileType != "undefined")
		pageAttachObj.limitFileType = decodeURIComponent(pageAttachObj.limitFileType.toLowerCase());
	else
		pageAttachObj.limitFileType = null;
	var rename = Utils.getPageValue("rename");
	if (rename)
		$("#txtRename").val(decodeURIComponent(rename));
	var unallowRename = Utils.getPageValue("unallowRename");
	if (unallowRename && (unallowRename == 1 || unallowRename.toLowerCase() == "true" || unallowRename.toLowerCase() == "yes")) {
		$("#txtRename").attr("readonly", true);
		$("#txtRename").attr("title", "系统设置不允许重命名");
	}
	var limitFileSize = Utils.getPageValue("fileSize");
	if (limitFileSize) {
		limitFileSize = parseFloat(limitFileSize);
		if (isNaN(limitFileSize) || limitFileSize <= 0)
			limitFileSize = 0;
		if (limitFileSize > 0)
			pageAttachObj.limitFileSize = limitFileSize;
	}
	if (pageAttachObj.limitFileSize > 0) {
		$("#txtFileSize").css("width", "50%");
		$("#limitFileSizeUnit").html("<=" + pageAttachObj.convertFileSize(pageAttachObj.limitFileSize, true));
		$("#limitFileSizeUnit").attr("title", "系统限制文件大小必须小于" + pageAttachObj.convertFileSize(pageAttachObj.limitFileSize, true));
		$("#limitFileSizeUnit").show();
	} else {
		$("#txtFileSize").removeAttr("style");
		$("#limitFileSizeUnit").hide();
	}
	var showFileSize = Utils.getPageValue("showFileSize");
	if (showFileSize && (showFileSize == 1 || showFileSize.toLowerCase() == "true" || showFileSize.toLowerCase() == "yes"))
		pageAttachObj.showFileSize = true;
	if (pageAttachObj.showFileSize && pageAttachObj.limitFileSize)
		$("#fileSizeRow").show();
	else
		$("#fileSizeRow").hide();
	var showFileType = Utils.getPageValue("showFileType");
	if (showFileType && showFileType != 1 && showFileType.toLowerCase() != "true" && showFileType.toLowerCase() != "yes")
		pageAttachObj.showFileType = false;
	if (pageAttachObj.showFileType && pageAttachObj.limitFileType) {
		$("#txtFileType").val(pageAttachObj.limitFileType);
		$("#fileTypeRow").show();
	} else
		$("#fileTypeRow").hide();
	$("#btnUpload").click(pageAttachObj.saveData);
	// $("#btnUpload").click(pageAttachObj.uploadFile);
	$("#btnClose").click(pageAttachObj.closeWindow);
	formCallback = pageAttachObj.checkUploadResult;
}

$(document).ready(pageAttachObj.initHtml);
