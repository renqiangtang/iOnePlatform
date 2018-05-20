//---------------------------------
//页页对象
//---------------------------------
var pageObj = {}
pageObj.nodeArray = new Array();
//---------------------------------

//---------------------------------
//表格对象
//---------------------------------
pageObj.status = {};
pageObj.status['1'] = '运行';
pageObj.status['2'] = '等待挂起';
pageObj.status['3'] = '停止';
pageObj.status['4'] = '废弃';
pageObj.status['5'] = '正常完成';
pageObj.status['50'] = '等待结束';


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
		$('#phase').html(data.phase);
		$('#phase2').html(data.phase+"截止时间:");
		$('#state').html(pageObj.status[data.state]);
		$('#beginTime').html(data.beginTime);
		$('#stopTime').html(data.stopTime);
		$('#phaseEndTime').html(data.endTime);
		
		if(data.isLimit){
			$('#targetTimes').hide();
			$('#phaseTimes').show();
			pageObj.initPhaseTimesList(data);
		}else{
			$('#targetTimes').show();
			$('#phaseTimes').hide();
			pageObj.initTimesInput(data.target);
		}
	};
	cmd.execute();
	pageObj.changSelectRedio();
}


//---------------------------------
//初始化时间表格框
//---------------------------------
pageObj.initTimesInput = function(obj) {
	
	$('#begin_notice_time').val(obj.begin_notice_time);
	$('#end_notice_time').val(obj.end_notice_time);
	$('#begin_apply_time').val(obj.begin_apply_time);
	$('#end_apply_time').val(obj.end_apply_time);
	$('#begin_earnest_time').val(obj.begin_earnest_time);
	$('#end_earnest_time').val(obj.end_earnest_time);
	$('#begin_list_time').val(obj.begin_list_time);
	$('#end_list_time').val(obj.end_list_time);
	$('#begin_focus_time').val(obj.begin_focus_time);
	$('#end_focus_time').val(obj.end_focus_time);
	$('#begin_limit_time').val(obj.begin_limit_time);
	
	$('#begin_notice_time').attr('readonly','true');
	$('#begin_notice_time').attr('disabled','true');
	if(obj.status == "4"){
		$('#begin_focus_time').attr('readonly','true');
		$('#begin_focus_time').attr('disabled','true');
	}
	
	if (obj.is_limit_trans)
		$('#begin_limit_time').parents('tr').show();
	else
		$('#begin_limit_time').parents('tr').hide();
	if(obj.trans_type == 0){// 挂牌
		$('#begin_list_time').parents('tr').show();
		$('#begin_focus_time').parents('tr').show();
	}else if(obj.trans_type == 1){// 拍卖
		$('#begin_list_time').parents('tr').hide();
		$('#begin_focus_time').parents('tr').hide();
		$('#beginLimitTimeTd').parents('tr').show();
	}
}


//---------------------------------
//初始化流程时间列表
//---------------------------------
pageObj.initPhaseTimesList = function(data){
	html = '';
	var isEnd = 1;
	for(var i=0;i<data.phaseList.length;i++){
		var obj = data.phaseList[i];
		html+='<tr>';
		html+='<td height="24" align="center" class="h_bk s_rb">'+obj.name+'</td>';
		if(obj.nodeId == data.nodeId){
			isEnd = 0;
		}
		if(isEnd == 1){
			html+='<td height="24" align="center" class="s_rb">'+obj.beginTime+'</td>';
			html+='<td height="24" align="center" class="s_rb">'+obj.endTime+'</td>';
		}else if(isEnd == 0 && obj.nodeId == data.nodeId){
			html+='<td height="24" align="center" class="s_rb">'+obj.beginTime+'</td>';
			html+='<td height="24" align="center" class="s_rb"><input type="test" id="'+obj.nodeId+'_endTime" value="'+obj.endTime+'" class="i-input_off " onClick="WdatePicker({dateFmt : \'yyyy-MM-dd HH:mm:ss\'})"/></td>';
			pageObj.nodeArray.push(obj.nodeId);
		}else{
			if(obj.beginTime){
				html+='<td height="24" align="center" class="s_rb"><input type="test" id="'+obj.nodeId+'_beginTime" value="'+obj.beginTime+'" class="i-input_off " onClick="WdatePicker({dateFmt : \'yyyy-MM-dd HH:mm:ss\'})"/></td>';
			}else{
				html+='<td height="24" align="center" class="s_rb">未进行</td>';
			}
			if(obj.endTime){
				html+='<td height="24" align="center" class="s_rb"><input type="test" id="'+obj.nodeId+'_endTime" value="'+obj.endTime+'" class="i-input_off " onClick="WdatePicker({dateFmt : \'yyyy-MM-dd HH:mm:ss\'})"/></td>';
			}else{
				html+='<td height="24" align="center" class="s_rb">&nbsp;</td>';
			}
			pageObj.nodeArray.push(obj.nodeId);
		}
		html+='</tr>';
	}
	$('#phaseList').html(html);
}

//---------------------------------
//时间检查
//---------------------------------
pageObj.timeCheck = function(){
	$('#begin_list_time').parents('tr').show();
	$('#begin_focus_time').parents('tr').show();
	
	var limitDisplay = $('#begin_list_time').parents('tr').css('display');
	var focusDisplay = $('#begin_focus_time').parents('tr').css('display');
	if ('none' != limitDisplay && 'none' != focusDisplay  ) {
		var b = pageObj.timeCompare('end_focus_time', 'begin_limit_time','[限时竞拍开始时间]不能小于[集中报价截止时间]！');
		if (!b){
			return false;
		}
	}
	return true;
}


pageObj.timeCompare = function(beginTimeId, endTimeId, msg) {
	var bt = $('#' + beginTimeId).val();
	var et = $('#' + endTimeId).val();
	if(bt == null || bt == "" || et == null || et == "" ){
		return ture;
	}
	var btTime = bt.replace(/ /g,"").replace(/-/g,"").replace(/:/g,"");
	var etTime = et.replace(/ /g,"").replace(/-/g,"").replace(/:/g,"");
	if (btTime < etTime) {
		DialogAlert(msg);
		$('#' + endTimeId).focus();
		return false;
	}
	return true;
}


//---------------------------------
//改变radio选择
//---------------------------------
pageObj.changSelectRedio = function(){
	var radio = $('input:radio[name="set_type"]:checked');
	var set_type = radio.val();
	if (set_type == '1') {
		$('#setDely').show();
		$('#setTimes').hide();
	}else if(set_type == '2'){
		$('#setDely').hide();
		$('#setTimes').show();
	}
}

//----------------------------
//恢复运行
//----------------------------
pageObj.restart = function (){
	var radio = $('input:radio[name="set_type"]:checked');
	var set_type = radio.val();
	DialogConfirm('您确定要执行重启操作吗?', function (yes) {
		if (yes) {
			var cmd = new Command();
			cmd.module = "trademan";
			cmd.service = "ProcessManage";
			cmd.method = "restartProcess";
			cmd.id = pageObj.targetId;
			cmd.nodeId = pageObj.nodeId;
			cmd.tasknodeId = pageObj.tasknodeId;
			cmd.setType = set_type;
			if(set_type == 1){
				cmd.delayDay = $("#delayDay").val();
				var re = /^[0-9]+$/;
				if (!re.test(cmd.delayDay)){
						DialogAlert("推迟天数必须为非负整数!");
				        return false;
				}
			}else if(set_type == 2){
				cmd.begin_notice_time = $('#begin_notice_time').val();
				cmd.end_notice_time = $('#end_notice_time').val();
				cmd.begin_apply_time = $('#begin_apply_time').val();
				cmd.end_apply_time = $('#end_apply_time').val();
				cmd.begin_earnest_time = $('#begin_earnest_time').val();
				cmd.end_earnest_time = $('#end_earnest_time').val();
				cmd.begin_list_time = $('#begin_list_time').val();
				cmd.end_list_time = $('#end_list_time').val();
				cmd.begin_focus_time = $('#begin_focus_time').val();
				cmd.end_focus_time = $('#end_focus_time').val();
				cmd.begin_limit_time = $('#begin_limit_time').val();
				cmd.phase_end_time = $("#"+pageObj.nodeId+"_endTime").val(); 
				
//				if ( pageObj.isLimit && !pageObj.timeCheck() ){
//					return;
//				}
			}
			cmd.success = function(data) {
				if(data.state){
					DialogAlert('执行重启操作成功！');
					window.returnValue = true;
					window.close();
				}else{
					if(data.message){
						DialogAlert(data.message);
					}else{
						DialogAlert('执行重启操作失败！');
					}
				}
			}
			cmd.execute();
		}
	});
}


//----------------------------
//取得时间 -- 不用了
//----------------------------
pageObj.getTimes = function (){
	var timesArray = new Array();
	for(var i = 0 ; i < pageObj.nodeArray.length ; i++){
		var nodeId = pageObj.nodeArray[i];
		var beginTime = $("#"+nodeId+"_beginTime").val(); 
		var endTime = $("#"+nodeId+"_endTime").val(); 
		var times = {};
		times.nodeId = nodeId;
		times.beginTime = beginTime;
		times.endTime = endTime;
		timesArray.push(times);
	}
	return JSON.stringify(timesArray);
}

//----------------------------
//校验是否整数
//----------------------------
function checkNum(obj){
   var re = /^[1-9]\d*|0$/;
     if (!re.test(obj.value))
    {
        alert("必须为正整数!");
   obj.value="";
        obj.focus();
        return false;
     }
} 


//---------------------------------
//页面初始化
//---------------------------------
$(document).ready(function(){
	pageObj.initPage();
	$('#btnRestart').click(pageObj.restart);
//	$("#endTime").click(function() {
//		WdatePicker({
//					dateFmt : 'yyyy-MM-dd HH:mm:ss'
//				})
//	});
	
	$('input:radio[name="set_type"]').click(function() {
		 pageObj.changSelectRedio();
	});
	
	$("#begin_limit_time").click(function() {
		WdatePicker({
					dateFmt : 'yyyy-MM-dd HH:mm:ss'
				})
	});// 拍卖(限时竞价、现场拍卖)开始时间
	$("#begin_notice_time").click(function() {
			WdatePicker({
						dateFmt : 'yyyy-MM-dd HH:mm:ss'
					})
		});// 公告开始时间
	$("#end_notice_time").click(function() {
			WdatePicker({
						dateFmt : 'yyyy-MM-dd HH:mm:ss'
					})
		});// 公告截止时间
	$("#begin_apply_time").click(function() {
			WdatePicker({
						dateFmt : 'yyyy-MM-dd HH:mm:ss'
					})
		});// 竞买申请开始时间
	$("#end_apply_time").click(function() {
			WdatePicker({
						dateFmt : 'yyyy-MM-dd HH:mm:ss'
					})
		});// 竞买申请截止时间
	$("#begin_earnest_time").click(function() {
			WdatePicker({
						dateFmt : 'yyyy-MM-dd HH:mm:ss'
					})
		});// 保证金开始时间
	$("#end_earnest_time").click(function() {
			WdatePicker({
						dateFmt : 'yyyy-MM-dd HH:mm:ss'
					})
		});// 保证金截止时间
	$("#begin_list_time").focus(function() {
			WdatePicker({
						dateFmt : 'yyyy-MM-dd HH:mm:ss'
					})
		});// 挂牌开始时间
	$("#end_list_time").click(function() {
			WdatePicker({
						dateFmt : 'yyyy-MM-dd HH:mm:ss'
					})
		});// 挂牌截止时间
	$("#begin_focus_time").click(function() {
			WdatePicker({
						dateFmt : 'yyyy-MM-dd HH:mm:ss'
					})
		});// 挂牌(集中、投标)报价开始时间
	$("#end_focus_time").click(function() {
			WdatePicker({
						dateFmt : 'yyyy-MM-dd HH:mm:ss'
					})
		});// 挂牌(集中、投标)报价截止时间
	Utils.autoIframeSize();
});