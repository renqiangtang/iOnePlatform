var pageObj = {};



//-----------------------------
//高级搜索
//----------------------------
pageObj.adsearch = function (){
	pageObj.searchCondition();
}

//选择区域
pageObj.selCanton = function() {
	var canton = comObj.cantonSelect ();
	if (typeof(canton)!="undefined") {
		$("#canton").val(canton.fullName);
		$("#canton_id").val(canton.id);
	}
}

//-----------------------------
//搜索条件
//----------------------------
pageObj.searchCondition = function (){
	var cmd={};
	var goods_use=$("#goods_use").val();
	var canton=$("#canton_id").val();
	var no=$("#adno").val();
	var trans_status=$("#trans_status").val();
	var timetype=$("#timetype").val();
	var timevalue=$("#timevalue").val();
	if(goods_use!=""){
		cmd.goods_use=goods_use;
	}
	if(no!=""){
		cmd.no=no;
	}
	if(name!=""){
		cmd.name=name;
	}
	if(trans_status!=""){
		cmd.trans_status=trans_status;
	}
	
	if(canton!=""){
		cmd.canton=canton;
	}
	if(timevalue!=""){
		cmd.timevalue=timevalue;
	}
	if(timetype!=""){
		cmd.timetype=timetype;
	}
	returnValue=cmd;
	window.close();
}

//-----------------------------
//时间控件
//----------------------------
pageObj.initQueryTimeCondition = function() {
	$("#timevalue").ligerDateEditor();
}


//-----------------------------
//全部公告添加状态
//----------------------------
pageObj.addStatusAll = function(){
	$("#trans_status").html('');
	Utils.addOption('trans_status', "", "--请选择--");
	for ( var pro in comObj.targetStatusObj) {
		if(pro>2  && pro<7){
			Utils.addOption('trans_status', pro, comObj.targetStatusObj[pro]);
		}
	}
	Utils.addOption('trans_status', -2, "中止");
	Utils.addOption('trans_status', -3, "终止");
}

//-----------------------------
//结果公示添加状态
//----------------------------
pageObj.addStatusRe = function(){
	$("#trans_status").html('');
	Utils.addOption('trans_status', "", "--请选择--");
	for(var i=5 ;i<7;i++){
		Utils.addOption('trans_status', i, comObj.targetStatusObj[i]);
	}
}

//-----------------------------
//用途
//----------------------------
pageObj.initUse = function (){
	var html = '<option value="" >--请选择--</option>';
	for (var pro in comObj.use_type) {
		html += '<option value="' + comObj.use_type[pro] + '" >' + pro + '</option>';
	}
	$('#goods_use').html(html);
}


//-----------------------------
//比较时间大小
//----------------------------
pageObj.compareTime =  function(beginTime,endTime) {
    var beginTimes = beginTime.substring(0, 10).split('-');
    var endTimes = endTime.substring(0, 10).split('-');
    beginTime = beginTimes[1] + '-' + beginTimes[2] + '-' + beginTimes[0] + ' ' + beginTime.substring(10, 19);
    endTime = endTimes[1] + '-' + endTimes[2] + '-' + endTimes[0] + ' ' + endTime.substring(10, 19);
    var a = (Date.parse(endTime) - Date.parse(beginTime)) / 3600 / 1000;
    if (a <= 0) {
        return true;
    } else if (a > 0) {
        return false;
    } else {
        return 'exception'
    }
}


$(document).ready(function() {
	pageObj.initQueryTimeCondition();
	pageObj.addStatusAll();
	pageObj.initUse();
	$("#canton").click(pageObj.selCanton);
	$("#winSure").click(function(){
		pageObj.adsearch();
	});
	
	$("#winCancel").click(function(){
		window.close();
		}
	);
	
});