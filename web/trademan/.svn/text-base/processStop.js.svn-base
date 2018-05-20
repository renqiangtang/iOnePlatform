//---------------------------------
//页页对象
//---------------------------------
var pageObj = {}
pageObj.nodeArray = new Array();
//---------------------------------

//---------------------------------
//页面初始化
//---------------------------------
pageObj.initPage = function() {
	$('#setDely').hide();
	$('#setTimes').hide();
	
	pageObj.targetId = Utils.getPageValue('id');
	var cmd = new Command();
	cmd.module = "trademan";
	cmd.service = "ProcessManage";
	cmd.method = "getProcessParameter";
	cmd.id = pageObj.targetId;
	cmd.success = function(data) {
		pageObj.nodeId = data.nodeId;
		pageObj.tasknodeId = data.tasknodeId;
		pageObj.ptype = data.ptype;
		pageObj.qtype = data.qtype;
		pageObj.isLimit = data.isLimit;
		$('#targetName').html(data.target.no);
	};
	cmd.execute();
}



//----------------------------
//中止操作
//----------------------------
pageObj.stop = function (){
		var cmd = new Command();
		cmd.module = "trademan";
		cmd.service = "ProcessManage";
		cmd.method = "stopProcess";
		cmd.reason = $("#reson").val()+" ";
		cmd.id = pageObj.targetId;
		cmd.success = function(data) {
			if(data.state){
				DialogAlert('执行中止操作成功！');
				window.returnValue = true;
				window.close();
			}else{
				if(data.message){
					DialogAlert(data.message);
				}else{
					DialogAlert('执行中止操作失败！');
				}
			}
		}
		cmd.execute();

}



//---------------------------------
//页面初始化
//---------------------------------
$(document).ready(function(){
	pageObj.initPage();
	$('#btnStop').click(pageObj.stop);
	Utils.autoIframeSize();
});