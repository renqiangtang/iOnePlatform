var pageUploadObj={};
pageUploadObj.upLoadGrid=null;
pageUploadObj.refId=null;//数据来源表ID，这个参数一定不能为空,为空不让上传
pageUploadObj.refTableName=null;//数据来源表名

//--------------------------------
//上传文件
//--------------------------------
pageUploadObj.uploadFile=function(){
	var form=$$("#fileForm");
	var afreshName=$("#afreshName").val();//重命名
	var info=$("#info").val();//说明
	if(form.file.value==''){
		alert('请上传附件!');
		return false;
	}
	form.action=approot+"/upload?module=base&service=BaseUpload&method=uploadFile&afreshName="+afreshName+"&info="+info+"&refTableName="+pageUploadObj.refTableName+"&refId="+pageUploadObj.refId;
	form.method="post";
	form.target="hidden_frame";
	form.submit();
}




pageUploadObj.reset=function(){
	var form=$$("#fileForm");
	form.file.value='';
	form.afreshName.value='';
	form.info.value='';
}
pageUploadObj.formCallback=function(dataObj){
	alert(dataObj.message);
	pageUploadObj.refGrid();//刷新表格
	//清空页面文本框的内容
	var form=$$("#fileForm");
	form.file.value='';
	form.afreshName.value='';
	form.info.value='';


}
//------------------------------------
//刷新表格
//------------------------------------
pageUploadObj.refGrid=function(){
	var manager=$("#upLoadGrid").ligerGetGridManager(); 
	var pageSize=manager.options.pageSize;
	var page=manager.options.page;
	var queryParams = {refId:pageUploadObj.refId,refTableName:pageUploadObj.refTableName};
	var url=approot+'/data?method=getSysLob&module=base&service=BaseUpload';
	for(var key in queryParams){
		url = url + '&' + key + '=' + queryParams[key];
	}
	var gridOptions = {url: url}
	manager.setOptions(gridOptions);
	manager.loadServerData(queryParams);
}

//------------------------------------
//渲染操作
//------------------------------------
pageUploadObj.renderCz=function(row, rowNamber){
	var extendType=row.extendType;
	return '<a  href="javascript:pageUploadObj.dowLoadFile(\''+row.id+'\')">下载</a>&nbsp;&nbsp;'+
	'<a  href="javascript:pageUploadObj.delFile(\''+row.id+'\')">删除</a>&nbsp;&nbsp;'+
	'<a  href="javascript:pageUploadObj.moreFile(\''+row.id+'\')">测试多个</a>';
}
//------------------------------------
//删除文件
//------------------------------------

pageUploadObj.delFile=function(id){
	
	var cmd = new Command();
	cmd.module = "base";
	cmd.service = "BaseUpload";
	cmd.method = "delSysLob";
	cmd.lobId=id;
	cmd.success = function (data) {
		alert(data.message);
		pageUploadObj.refGrid();
	};
	cmd.execute();
}
//------------------------------------
//下载文件
//------------------------------------

pageUploadObj.dowLoadFile=function(id){
	var form=$$("#fileForm");
	form.action=approot+"/download?module=base&service=BaseDownload&method=downloadFile&lobId="+id;
	form.method="post";
	form.target="hidden_frame";
	form.submit();
}


pageUploadObj.moreFile=function(id){
	var form=$$("#fileForm");
	form.action=approot+"/download?module=base&service=BaseDownload&method=downloadZipFile&refId=2222";
	form.method="post";
	form.target="hidden_frame";
	form.submit();
}


$(document).ready(function () {
	pageUploadObj.refId='2222';
	pageUploadObj.refTableName=null;
	//获取别人调用时传递的参数refId和refTableName

	//------------------------------
	//初始化grid
	//------------------------------
	pageUploadObj.upLoadGrid = $("#upLoadGrid").ligerGrid({
		url:approot+'/data?method=getSysLob&module=base&service=BaseUpload&refId='+pageUploadObj.refId+'&refTableName='+pageUploadObj.refTableName,
		columns:[ 
	                { display: '逻辑名',name:'name',isSort:false,width:'39%',align:'left'},
	                { display: '文件名',name:'file_name',isSort:false,width:'39%', align:'left' },
	                { display: '操     作',isSort:false,width:'20%',align:'center',render:pageUploadObj.renderCz}
                ],
		isScroll: true, //是否滚动
		frozen:false,//是否固定列
		usePager : false, 
		showTitle: false,
		fixedCellHeight :false,
		width:'99.8%'
	});
	

});





