var pageObj = {};
pageObj.tabId = 0;
pageObj.pageSize = 10 ; //每页显示条数
pageObj.page = 1 ;//显示第 pageObj.page 页
pageObj.pageCount = 1;
pageObj.buUrl = null;
pageObj.bu_ip = null;
pageObj.bu_web = null;
pageObj.demo_ip = null;
pageObj.demo_web = null;
pageObj.demoUrl = null;
pageObj.searchValue=null;
pageObj.cantonName="";
//-----------------------------
//初始化查找全部公告
//----------------------------
pageObj.initPage = function() {
	var cmd = new Command();
	cmd.module = "portal";
	cmd.service = "QueryLand";
	cmd.method = "getTransTargetList";
	cmd.tabId=pageObj.tabId;
	cmd.pagesize=pageObj.pageSize;
	cmd.page=pageObj.page;
	cmd.goods_type="101";
	cmd.u="portal";
	pageObj.searchCondition(cmd);
	pageObj.myScroll();
	cmd.success = function(data) {
		var objs=data.Rows;
		pageObj.getBuUrl(data);
		$("#divallnotice").html("");
		var html="";
		var lastname="";
		$(objs).each(function(i, ele) {
			var tstatus=ele.status;
			var infohtml="";
			//显示操作
			infohtml="<td class='c2'><a href=javascript:pageObj.viewTarget('"+ele.id+"','"+ele.status+"')>查看</a></td>";
			
		   
		    var is_stop = ele.is_stop;
			var is_suspend = ele.is_suspend;
			var is_s = '';
			if (is_suspend == 1) {
				is_s = '中止';
			}
			if (is_stop == 1) {
				is_s = '终止';
			}
			if (is_s != null && is_s != "") {
				is_s = '<font color="red">(' + is_s + ')</font>';
			}
			 tstatus=comObj.targetStatusObj[tstatus]+is_s;
			if(ele.tnno==lastname){//如果下一条的公告号与此条相同，那么合并到一个表格显示，不显示表头
				html += "<tr id="+ele.id;
				if(i%2==0){
					html += " ";
				}
				html += " >";
				html += "<td>"+(pageObj.pageSize*(pageObj.page-1)+i+1)+"</td>"+
		        "<td><a href=javascript:pageObj.viewTarget('"+ele.id+"','"+ele.status+"')>"+ele.name+"</a></td>"+
		        "<td>"+pageObj.replaceNull(ele.tgoods_use)+"</td>"+
		        "<td>"+ele.trans_type_name.replace("（","(").replace("）",")")+"</td>"+
		        "<td>"+tstatus+"</td>"+
		        "<td >"+pageObj.subTime(ele.end_earnest_time)+"</td>"+
		        "<td>"+pageObj.subTime(ele.end_apply_time)+"</td>"+infohtml+"</tr>";
				if(i!=data.Rows.length-1){
					if(ele.tnno!=data.Rows[i+1].tnno){
						html+="</table></div>";
					}
				}else{
					html+="</table></div>";
				}
			}else{
				lastname=ele.tnno;
				html +="<div class='mh_info_list'><h1>"+ele.tnno+"</h1>";
				html +="<table width='100%' border='0' cellspacing='0' cellpadding='0'>"+
		        "<tr>"+
		          "<th width='28'>序号</th>"+
		          "<th width='241'>宗地编号</th>"+
		          "<th width='138'>土地用途</th>"+
		          "<th width='121'>交易方式</th>"+
		          "<th width='77'>交易状态</th>"+
		          "<th width='132'>保证金截止时间</th>"+
		          "<th width='132'>竞买申请截止时间</th>"+
		          "<th width='88'>操作</th>"+
		          "</tr>";
				
				html += "<tr id="+ele.id;
				if(i%2==0){
					html += "  ";
				}
				html += " >";
				html += "<td>"+(pageObj.pageSize*(pageObj.page-1)+i+1)+"</td>"+
		        "<td><a href=javascript:pageObj.viewTarget('"+ele.id+"','"+ele.status+"')>"+ele.name+"</a></td>"+
		        "<td>"+pageObj.replaceNull(ele.tgoods_use)+"</td>"+
		        "<td>"+ele.trans_type_name.replace("（","(").replace("）",")")+"</td>"+
		        "<td>"+tstatus+"</td>"+
		        "<td >"+pageObj.subTime(ele.end_earnest_time)+"</td>"+
		        "<td>"+pageObj.subTime(ele.end_apply_time)+"</td>"+infohtml+"</tr>";
				
				//查找下一条公告是否和上一条相同，不相同则table结束
				if(i!=data.Rows.length-1){
					if(ele.tnno!=data.Rows[i+1].tnno){
						html+="</table></div>";
					}
				}else{
					html+="</table></div>";
				}
			}
		});
		if(objs.length>0){
			$("#divallnotice").html(html);
		}else{
			$("#divallnotice").html(pageObj.nullHint());
		}
		pageObj.pageCount=data.pageCount;
		
		if(data.blackstatus==0){
			$("#hmd").hide();
		}
		setInterval("pageObj.hideWait()",500);
	}
	cmd.execute();
}

//-----------------------------
//查找正在公告
//----------------------------
pageObj.nowtrans = function() {
	var cmd = new Command();
	cmd.module = "portal";
	cmd.service = "QueryLand";
	cmd.u="portal";
	cmd.method = "getTransTargetList";
	cmd.tabId=pageObj.tabId;
	cmd.pagesize=pageObj.pageSize;
	cmd.page=pageObj.page;
	cmd.goods_type="101";
	pageObj.searchCondition(cmd);
	cmd.success = function(data) {
		pageObj.myScroll();
		var objs=data.Rows;
		var html="";
		var lastname="";
		$("#divnownotice").html("");
		$(objs).each(function(i, ele) {
			var tstatus=ele.status;
			var is_stop = ele.is_stop;
			var is_suspend = ele.is_suspend;
			var is_s = '';
			if (is_suspend == 1) {
				is_s = '中止';
			}
			if (is_stop == 1) {
				is_s = '终止';
			}
			if (is_s != null && is_s != "") {
				is_s = '<font color="red">(' + is_s + ')</font>';
			}
			 tstatus=comObj.targetStatusObj[tstatus]+is_s
			if(ele.tnno==lastname){//如果下一条的公告号与此条相同，那么合并到一个表格显示，不显示表头
				html += "<tr id="+ele.id;
				if(i%2==0){
					html += " ";
				}
				html += " >";
				html += "<td>"+(pageObj.pageSize*(pageObj.page-1)+i+1)+"</td>"+
			    "<td><a href=javascript:pageObj.viewTarget('"+ele.id+"','"+ele.status+"')>"+ele.name+"</a></td>"+
			    "<td>"+ele.begin_price.toWanYuan().addComma()+"万元</td>"+
			    "<td>"+pageObj.renderEarnestMoneny(ele.earnest_money,ele.unit)+"</td>"+
			    "<td>"+pageObj.replaceNull(ele.tgoods_use)+"</td>"+
			    "<td>"+ele.trans_type_name.replace("（","(").replace("）",")")+"</td>"+
			    "<td>"+pageObj.subTime(ele.begin_focus_time)+"</td>"+
			    "<td>"+pageObj.subTime(ele.begin_apply_time)+"</td>"+
			    "<td>"+pageObj.subTime(ele.end_apply_time)+"</td>"+
			    "<td class='c2' ><a href=javascript:pageObj.viewTarget('"+ele.id+"','"+ele.status+"')>查看</a></td>";
				if(i!=data.Rows.length-1){
					if(ele.tnno!=data.Rows[i+1].tnno){
						html+="</table></div>";
					}
				}else{
					html+="</table></div>";
				}
			}else{
				lastname=ele.tnno;
				html +="<div class='mh_info_list'><h1>"+ele.tnno+"</h1>";
				html +="<table width='100%' border='0' cellspacing='0' cellpadding='0'>"+
				"<tr>"+
		          "<th width='24'>序号</th>"+
		          "<th width='174'>宗地编号</th>"+
		          "<th width='81'>挂牌起始价</th>"+
		          "<th width='97'>竞买保证金</th>"+
		          "<th width='80'>土地用途</th>"+
		          "<th width='95'>交易类型</th>"+
		          "<th width='114'>竞价开始时间</th>"+
		          "<th width='114'>竞买申请开始时间</th>"+
		          "<th width='114'>竞买申请截止时间</th>"+
		          "<th width='56'>操作</th>"+
		          "</tr>";
				
				html += "<tr id="+ele.id;
				if(i%2==0){
					html += " ";
				}
				html += " >";
				html += "<td>"+(pageObj.pageSize*(pageObj.page-1)+i+1)+"</td>"+
			    "<td><a href=javascript:pageObj.viewTarget('"+ele.id+"','"+ele.status+"')>"+ele.name+"</a></td>"+
			    "<td>"+ele.begin_price.toWanYuan().addComma()+"万元</td>"+
			    "<td>"+pageObj.renderEarnestMoneny(ele.earnest_money,ele.unit)+"</td>"+
			    "<td>"+pageObj.replaceNull(ele.tgoods_use)+"</td>"+
			    "<td>"+ele.trans_type_name.replace("（","(").replace("）",")")+"</td>"+
			    "<td>"+pageObj.subTime(ele.begin_focus_time)+"</td>"+
			    "<td>"+pageObj.subTime(ele.begin_apply_time)+"</td>"+
			    "<td>"+pageObj.subTime(ele.end_apply_time)+"</td>"+
			    "<td class='c2' ><a href=javascript:pageObj.viewTarget('"+ele.id+"','"+ele.status+"')>查看</a></td>";
				
				//查找下一条公告是否和上一条相同，不相同则table结束
				if(i!=data.Rows.length-1){
					if(ele.tnno!=data.Rows[i+1].tnno){
						html+="</table></div>";
					}
				}else{
					html+="</table></div>";
				}
//				alert("i"+i+","+"length"+data.Rows.length);
//				html+="</table>";
			}
			
			});
			if(objs.length>0){
				$("#divnownotice").html(html);
			}else{
				$("#divnownotice").html(pageObj.nullHint());
			}
			pageObj.pageCount=data.pageCount;
			setInterval("pageObj.hideWait()",500);
		}
		cmd.execute();
}

//-----------------------------
//查找正在竞价
//----------------------------
pageObj.limittrans = function() {
	var cmd = new Command();
	cmd.module = "portal";
	cmd.service = "QueryLand";
	cmd.method = "getTransTargetList";
	cmd.u="portal";
	cmd.tabId=pageObj.tabId;
	cmd.pagesize=pageObj.pageSize;
	cmd.page=pageObj.page;
	cmd.goods_type="101";
	pageObj.searchCondition(cmd);
	cmd.success = function(data) {
		pageObj.myScroll();
		$("#divlimittarget").html("");
		var objs=data.Rows;
		var html="";
		var lastname="";
		$(objs).each(function(i, ele) {
			var tstatus=ele.status;
			var is_stop = ele.is_stop;
			var is_suspend = ele.is_suspend;
			var is_s = '';
			if (is_suspend == 1) {
				is_s = '中止';
			}
			if (is_stop == 1) {
				is_s = '终止';
			}
			if (is_s != null && is_s != "") {
				is_s = '<font color="red">(' + is_s + ')</font>';
			}
			 tstatus=comObj.targetStatusObj[tstatus]+is_s
			
			if(ele.tnno==lastname){//如果下一条的公告号与此条相同，那么合并到一个表格显示，不显示表头
				html += "<tr id="+ele.id;
				if(i%2==0){
					html += "  ";
				}
				html += " >";
				html += "<td>"+(pageObj.pageSize*(pageObj.page-1)+i+1)+"</td>"+
		          "<td><a href=javascript:pageObj.viewTarget('"+ele.id+"','"+ele.status+"')>"+ele.name+"</a></td>"+
		          "<td>"+ele.begin_price.toWanYuan().addComma()+"万元</td>"+
		          "<td>"+ele.tgoods_use+"</td>"+
		          "<td>"+ele.trans_type_name.replace("（","(").replace("）",")")+"</td>"+
		          "<td>"+pageObj.subTime(ele.begin_focus_time)+"</td>"+
		          "<td>"+pageObj.subTime(ele.end_list_time)+"</td>"+
		          "<td class='c2'><a href=javascript:pageObj.viewTarget('"+ele.id+"','"+ele.status+"')>查看</a></td>"+
		          "</tr>";
				if(i!=data.Rows.length-1){
					if(ele.tnno!=data.Rows[i+1].tnno){
						html+="</table></div>";
					}
				}else{
					html+="</table></div>";
				}
			}else{
				lastname=ele.tnno;
				html +="<div class='mh_info_list'><h1>"+ele.tnno+"</h1>";
				html +="<table width='100%' border='0' cellspacing='0' cellpadding='0'>"+
				  "<tr>"+
		          "<th width='25'>序号</th>"+
		          "<th width='203'>宗地编号</th>"+
		          "<th width='130'>挂牌起始价</th>"+
		          "<th width='136'>土地用途</th>"+
		          "<th width='122'>交易类型</th>"+
		          "<th width='120'>竞价开始时间</th>"+
		          "<th width='120'>挂牌截止时间</th>"+
		          "<th width='90'>操作</th>"+
		          "</tr>";		          
				html += "<tr id="+ele.id;
				if(i%2==0){
					html += "  ";
				}
				html += " >";
				html += "<td>"+(pageObj.pageSize*(pageObj.page-1)+i+1)+"</td>"+
		          "<td><a href=javascript:pageObj.viewTarget('"+ele.id+"','"+ele.status+"')>"+ele.name+"</a></td>"+
		          "<td>"+ele.begin_price.toWanYuan().addComma()+"万元</td>"+
		          "<td>"+ele.tgoods_use+"</td>"+
		          "<td>"+ele.trans_type_name.replace("（","(").replace("）",")")+"</td>"+
		          "<td>"+pageObj.subTime(ele.begin_focus_time)+"</td>"+
		          "<td>"+pageObj.subTime(ele.end_list_time)+"</td>"+
		          "<td class='c2'><a href=javascript:pageObj.viewTarget('"+ele.id+"','"+ele.status+"')>查看</a></td>"+
		          "</tr>";
				
				//查找下一条公告是否和上一条相同，不相同则table结束
				if(i!=data.Rows.length-1){
					if(ele.tnno!=data.Rows[i+1].tnno){
						html+="</table></div>";
					}
				}else{
					html+="</table></div>";
				}
			}
			
		});
		if(objs.length>0){
			$("#divlimittarget").html(html);
		}else{
			$("#divlimittarget").html(pageObj.nullHint());
		}
		pageObj.pageCount=data.pageCount;
		setInterval("pageObj.hideWait()",500);
		}
		cmd.execute();
}

//-----------------------------
//查找结果公示
//----------------------------
pageObj.resulttrans = function() {
	var cmd = new Command();
	cmd.module = "portal";
	cmd.service = "QueryLand";
	cmd.method = "getTransTargetList";
	cmd.u="portal";
	cmd.tabId=pageObj.tabId;
	cmd.pagesize=pageObj.pageSize;
	cmd.page=pageObj.page;
	cmd.goods_type="101";
	pageObj.searchCondition(cmd);
	cmd.success = function(data) {
		pageObj.myScroll();
		$("#divresultnotice").html("");
		var objs=data.Rows;
		var html="";
		var lastname="";
		$(objs).each(function(i, ele) {
			var tstatus=ele.status;
			var begin_price=ele.begin_price;
			if(begin_price!=null && begin_price!="null"){
				begin_price=ele.begin_price.toWanYuan().addComma();
			}else{
				begin_price="";
			}
			var trans_price=ele.trans_price;
			if(trans_price!=null && trans_price!="null"){
				trans_price=ele.trans_price.toWanYuan().addComma();
			}else{
				trans_price="";
			}
			
			var transhtml="";//流拍是没有成交价成交时间成交人
			
			if(tstatus==6 || tstatus==7 || tstatus==8){
				transhtml= "<td>&nbsp;</td>"+
		          "<td>&nbsp;</td>"+
		          "<td>&nbsp;</td>";
			}
			var is_stop = ele.is_stop;
			var is_suspend = ele.is_suspend;
			var is_s = '';
			if (is_suspend == 1) {
				is_s = '中止';
			}
			if (is_stop == 1) {
				is_s = '终止';
			}
			if (is_s != null && is_s != "") {
				is_s = '<font color="red">(' + is_s + ')</font>';
			}
			 tstatus=comObj.targetStatusObj[tstatus]+is_s
			
			if(transhtml==""){
				transhtml= "<td>"+pageObj.subTime(pageObj.replaceNull(ele.trans_date))+"</td>"+
		          "<td>"+pageObj.replaceNull(ele.tbname)+"</td>"+
		          "<td>"+pageObj.replaceNull(trans_price+"万元")+"</td>";
			}
			if(ele.tnno==lastname){//如果下一条的公告号与此条相同，那么合并到一个表格显示，不显示表头
				html += "<tr id="+ele.id;
				if(i%2==0){
					html += " ";
				}
				html += "<tr id="+ele.id;
				if(i%2==0){
					html += "  ";
				}
				html += " >";
				html += "<td>"+(pageObj.pageSize*(pageObj.page-1)+i+1)+"</td>"+
		          "<td><a href=javascript:pageObj.viewTarget('"+ele.id+"','"+ele.status+"')>"+ele.ttname+"</a></td>"+
		          "<td>"+begin_price+"万元</td>"+
		          "<td>"+ele.trans_type_name.replace("（","(").replace("）",")")+"</td>"+
		          "<td>"+tstatus+"</td>"+
		          transhtml+
		          "<td class='c2'><a href=javascript:pageObj.viewTarget('"+ele.id+"','"+ele.status+"')>查看</a></td>";
		          "</tr>";
				if(i!=data.Rows.length-1){
					if(ele.tnno!=data.Rows[i+1].tnno){
						html+="</table></div>";
					}
				}else{
					html+="</table></div>";
				}
			}else{
				lastname=ele.tnno;
				html +="<div class='mh_info_list'><h1>"+ele.tnno+"</h1>";
				html +="<table width='100%' border='0' cellspacing='0' cellpadding='0'>"+
				"<tr>"+
		        "<th width='24'>序号</th>"+
		        "<th width='176'>宗地编号</th>"+
		        "<th width='103'>起始价</th>"+
		        "<th width='96'>交易类型</th>"+
		        "<th width='77'>交易状态</th>"+
		        "<th width='113'>成交时间</th>"+
		        "<th width='211'>竞得人</th>"+
		        "<th width='111'>成交价</th>"+
		        "<th width='47'>操作</th>"+
		        "</tr>";
				
		        html += "<tr id="+ele.id;
				if(i%2==0){
					html += "  ";
				}
				html += " >";
				html += "<td>"+(pageObj.pageSize*(pageObj.page-1)+i+1)+"</td>"+
		          "<td><a href=javascript:pageObj.viewTarget('"+ele.id+"','"+ele.status+"')>"+ele.ttname+"</a></td>"+
		          "<td>"+begin_price+"万元</td>"+
		          "<td>"+ele.trans_type_name.replace("（","(").replace("）",")")+"</td>"+
		          "<td>"+tstatus+"</td>"+
		          transhtml+
		          "<td class='c2'><a href=javascript:pageObj.viewTarget('"+ele.id+"','"+ele.status+"')>查看</a></td>"+
		          "</tr>";
				
				//查找下一条公告是否和上一条相同，不相同则table结束
				if(i!=data.Rows.length-1){
					if(ele.tnno!=data.Rows[i+1].tnno){
						html+="</table></div>";
					}
				}else{
					html+="</table></div>";
				}
			}
			});
			if(objs.length>0){
				$("#divresultnotice").html(html);
			}else{
				$("#divresultnotice").html(pageObj.nullHint());
			}
			pageObj.pageCount=data.pageCount;
			setInterval("pageObj.hideWait()",500);
		}
		cmd.execute();
}

//-----------------------------
//查找补充公告
//----------------------------
pageObj.supptrans = function() {
	var cmd = new Command();
	cmd.module = "portal";
	cmd.service = "QueryLand";
	cmd.method = "getTransTargetList";
	cmd.u="portal";
	cmd.tabId=pageObj.tabId;
	cmd.pagesize=pageObj.pageSize;
	cmd.page=pageObj.page;
	cmd.goods_type="101";
	pageObj.searchCondition(cmd);
	cmd.success = function(data) {
		pageObj.myScroll();
		$("#divsuppnotice").html("");
		var objs=data.Rows;
		var html="";
		var lastname="";
		$(objs).each(function(i, ele) {
			var tstatus=ele.status;
			var is_stop = ele.is_stop;
			var is_suspend = ele.is_suspend;
			var is_s = '';
			if (is_suspend == 1) {
				is_s = '中止';
			}
			if (is_stop == 1) {
				is_s = '终止';
			}
			if (is_s != null && is_s != "") {
				is_s = '<font color="red">(' + is_s + ')</font>';
			}
			 tstatus=comObj.targetStatusObj[tstatus]+is_s
			 
			if(ele.tnno==lastname){//如果下一条的公告号与此条相同，那么合并到一个表格显示，不显示表头
				html += "<tr id="+ele.id;
				if(i%2==0){
					html += " ";
				}
				html += " >";
				html += "<td>"+(pageObj.pageSize*(pageObj.page-1)+i+1)+"</td>"+
		        "<td><a href=javascript:pageObj.viewTarget('"+ele.id+"','-1')>"+ele.name+"</a></div></td>"+
		        "<td>"+pageObj.replaceNull(ele.tgoods_use)+"</td>"+
		        "<td>"+ele.trans_type_name.replace("（","(").replace("）",")")+"</td>"+
		        "<td>"+tstatus+"</td>"+
		        "<td>"+pageObj.subTime(ele.end_earnest_time)+"</td>"+
		        "<td>"+pageObj.subTime(ele.end_apply_time)+"</td>"+
		        "<td class='c2'><a href=javascript:pageObj.viewTarget('"+ele.id+"','-1')>查看补充公告</a></td>"+
		        "</tr>";
				if(i!=data.Rows.length-1){
					if(ele.tnno!=data.Rows[i+1].tnno){
						html+="</table></div>";
					}
				}else{
					html+="</table></div>";
				}
			}else{
				lastname=ele.tnno;
				html +="<div class='mh_info_list'><h1>"+ele.tnno+"</h1>";
				html +="<table width='100%' border='0' cellspacing='0' cellpadding='0'>"+
				"<tr>"+
	            "<th width='28'>序号</th>"+
	            "<th width='241'>宗地编号</th>"+
	            "<th width='138'>土地用途</th>"+
	            "<th width='121'>交易方式</th>"+
	            "<th width='77'>交易状态</th>"+
	            "<th width='132'>保证金截止时间</th>"+
	            "<th width='132'>竞买申请截止时间</th>"+
	            "<th width='88'>操作</th>"+
	            "</tr>";
				
				html += "<tr id="+ele.id;
				if(i%2==0){
					html += " ";
				}
				html += " >";
				html += "<td>"+(pageObj.pageSize*(pageObj.page-1)+i+1)+"</td>"+
		        "<td><a href=javascript:pageObj.viewTarget('"+ele.id+"','-1')>"+ele.name+"</a></div></td>"+
		        "<td>"+pageObj.replaceNull(ele.tgoods_use)+"</td>"+
		        "<td>"+ele.trans_type_name.replace("（","(").replace("）",")")+"</td>"+
		        "<td>"+tstatus+"</td>"+
		        "<td>"+pageObj.subTime(ele.end_earnest_time)+"</td>"+
		        "<td>"+pageObj.subTime(ele.end_apply_time)+"</td>"+
		        "<td class='c2'><a href=javascript:pageObj.viewTarget('"+ele.id+"','-1')>查看补充公告</a></td>"+
		        "</tr>";
				//查找下一条公告是否和上一条相同，不相同则table结束
				if(i!=data.Rows.length-1){
					if(ele.tnno!=data.Rows[i+1].tnno){
						html+="</table></div>";
					}
				}else{
					html+="</table></div>";
				}
			}
			});
			if(objs.length>0){
				$("#divsuppnotice").html(html);
			}else{
				$("#divsuppnotice").html(pageObj.nullHint());
			}
			pageObj.pageCount=data.pageCount;
			setInterval("pageObj.hideWait()",500);
		}
		cmd.execute();
}

//-----------------------------
//查找黑名单
//----------------------------
pageObj.getBlack = function() {
	var cmd = new Command();
	cmd.u="portal";
	cmd.module = "portal";
	cmd.service = "QueryLand";
	cmd.method = "getBlack";
	cmd.ban_type="101";
	cmd.tabId=pageObj.tabId;
	cmd.pagesize=pageObj.pageSize;
	cmd.page=pageObj.page;
	pageObj.searchCondition(cmd);
	pageObj.myScroll();
	cmd.success = function(data) {
		if(data.status==1 ){
			$("#blackth").html("");
			$("#blacktr").html("<div class=' f14 fb c2 tc' style='line-height:360px;'>没有公布黑名单！</div>");
		}else{
			var objs=data.Rows;
			$("#blacktr").html("");
			var html="";
			$(objs).each(function(i, ele) {
				html += "<tr id="+ele.id;
				if(i%2==0){
					html += " ";
				}
				html += " >";
				html += "<td>"+(pageObj.pageSize*(pageObj.page-1)+i+1)+"</td>"+
		        "<td>"+ele.name+"</a></td>"+
		        "<td>"+pageObj.replaceBt(pageObj.replaceNull(ele.bidder_type))+"</td>"+
		        "<td>"+pageObj.subTime(ele.create_date)+"</td>";
				html+="</tr>";
			});
			if(objs.length>0){
				$("#blackth").html("<tr>"+
	            "<th width='10%'>序号</th>"+
	            "<th width='55%'>名称</th>"+
	            "<th width='10%'>竞买人类型</th>"+
	            "<th width='15%'>加入日期</th>"+
	          "</tr>");
				$("#blacktr").html(html);
			}else{
				$("#blackth").html("");
				$("#divblack").html(pageObj.nullHint());
			}
		}
		pageObj.pageCount=data.pageCount;
		setInterval("pageObj.hideWait()",500);
		
	}
	cmd.execute();
}

//文件下载
pageObj.queryFileDownload = function() {
	var cmd = new Command();
	cmd.module = "trademan";
	cmd.service = "DocConfig";
	cmd.method = 'getList';
	cmd.home = 'config';
	cmd.u="portal";
	cmd.pageNum = pageObj.pageNum;
	cmd.pageSize = pageObj.pageSize;
	cmd.path ='河源市国土资源交易中心'+'/doc';
	cmd.fsType = 2;
	var html = '';
	cmd.success = function(data) {
//		pageObj.showPageInfo(data, 'queryFileDownload', 'resultFileDownload');
		var objs=data.rs
		if (data.state == 1) {
			$(objs).each(function(i, ele) {
				html += "<tr id="+ele.id;
				if(i%2==0){
					html += " ";
				}
				html += " >";
				html += "<td>"+(pageObj.pageSize*(pageObj.page-1)+i+1)+"</td>"+
		        "<td>"+ele.name+"</a></td>"+
		        "<td>"+new Date(ele.date) +"</td>"+
		        '<td><a href="../../download?module=trademan&amp;service=DocConfig&amp;method=directDownload&amp;name=' + ele.name + '&amp;path=' + cmd.home + '/' + ele.path + '">下载</a></td>';
				html+="</tr>";
			});
		}
//		alert(data.pageCount);
//		pageObj.pageCount=data.pageCount;
		$('#wjktr').html(html);
	};
	cmd.execute();
}


//-----------------------------
//空字段不显示null
//----------------------------
pageObj.replaceNull = function (value){
	if(value!=null && value!="null" && value!="null万元" && value!="万元"){
		return value;
	}else{
		return "";
	}
}

//-----------------------------
//转换竞买人类型
//----------------------------
pageObj.replaceBt = function (value){
	if(value=="0"){
		return "个人";
	}else if(value=="1"){
		return "企业";
	}else{
		return "未知";
	}
}

//-----------------------------
//截取时间的秒
//----------------------------
pageObj.subTime = function (time){
	if(time!=null && time!="" && time!="null" && time.length==19){
		return time;
	}else{
		return time;
	}
}

//-----------------------------
//点击tab
//----------------------------
pageObj.clickTab = function (tab){
	var clicktabId=tab.data.tabId;
	pageObj.clearSearch();//清空搜索项
	pageObj.tabId=clicktabId;
	pageObj.page=1;//切换TAB页时初始化页面
	pageObj.showWait();
	if(clicktabId==0){
		pageObj.initPage();//全部
		//显示此tab页，隐藏其它tab 并加上此tab背景
		$("#divallnotice").show();
		$("#divnownotice").hide();
		$("#divlimittarget").hide();
		$("#divresultnotice").hide();
		$("#divsuppnotice").hide();
		$("#divblack").hide();
		$("#divwj").hide();
		
		$("#cbgg").addClass("sel");
		$("#zzgg").removeAttr("class");
		$("#zzjj").removeAttr("class");
		$("#jggs").removeAttr("class");
		$("#bcgg").removeAttr("class");
		$("#hmd").removeAttr("class");
		$("#wjxz").removeAttr("class");
		
		$("#divBlackSearch").hide();
		$("#divSearch").show();
		
		$("#trans_status").show();//全部公告时隐藏拍卖方式显示交易状态
		$("#ztlab").show();
		$("#fslab").hide();
		
		pageObj.addStatusAll();//添加状态
		
	}else if(clicktabId==1){
		pageObj.nowtrans();//正在公告
		//显示此tab页，隐藏其它tab 并加上此tab背景
		$("#divallnotice").hide();
		$("#divnownotice").show();
		$("#divlimittarget").hide();
		$("#divresultnotice").hide();
		$("#divsuppnotice").hide();
		$("#divblack").hide();
		$("#divwj").hide();
		
		$("#cbgg").removeAttr("class");
		$("#zzgg").addClass("sel");
		$("#zzjj").removeAttr("class");
		$("#jggs").removeAttr("class");
		$("#bcgg").removeAttr("class");
		$("#hmd").removeAttr("class");
		$("#wjxz").removeAttr("class");
		
		$("#divBlackSearch").hide();
		$("#divSearch").show();
		
		$("#trans_status").hide();//显示拍卖方式隐藏交易状态
		$("#ztlab").hide();
		$("#fslab").show();
	}else if(clicktabId==2){
		pageObj.limittrans();//正在竞价
		//显示此tab页，隐藏其它tab 并加上此tab背景
		$("#divallnotice").hide();
		$("#divnownotice").hide();
		$("#divlimittarget").show();
		$("#divresultnotice").hide();
		$("#divsuppnotice").hide();
		$("#divblack").hide();
		$("#divwj").hide();
		
		$("#cbgg").removeAttr("class");
		$("#zzgg").removeAttr("class");
		$("#zzjj").addClass("sel");
		$("#jggs").removeAttr("class");
		$("#bcgg").removeAttr("class");
		$("#hmd").removeAttr("class");
		$("#wjxz").removeAttr("class");
		
		$("#divBlackSearch").hide();
		$("#divSearch").show();
		
		$("#trans_status").hide();//显示拍卖方式隐藏交易状态
		$("#ztlab").hide();
		$("#fslab").show();
	}else if(clicktabId==3){
		pageObj.resulttrans();//结果公示
		//显示此tab页，隐藏其它tab 并加上此tab背景
		$("#divallnotice").hide();
		$("#divnownotice").hide();
		$("#divlimittarget").hide();
		$("#divresultnotice").show();
		$("#divsuppnotice").hide();
		$("#divblack").hide();
		$("#divwj").hide();
		
		$("#cbgg").removeAttr("class");
		$("#zzgg").removeAttr("class");
		$("#zzjj").removeAttr("class");
		$("#jggs").addClass("sel");
		$("#bcgg").removeAttr("class");
		$("#hmd").removeAttr("class");
		$("#wjxz").removeAttr("class");
		
		$("#divBlackSearch").hide();
		$("#divSearch").show();
		
		$("#trans_status").show();//全部公告时显示拍卖方式隐藏交易状态
		$("#ztlab").show();
		$("#fslab").hide();
		
		pageObj.addStatusRe();
		
	}else if(clicktabId==4){
		pageObj.supptrans();//补充公告
		//显示此tab页，隐藏其它tab 并加上此tab背景
		$("#divallnotice").hide();
		$("#divnownotice").hide();
		$("#divlimittarget").hide();
		$("#divresultnotice").hide();
		$("#divsuppnotice").show();
		$("#divblack").hide();
		$("#divwj").hide();
		
		$("#cbgg").removeAttr("class");
		$("#zzgg").removeAttr("class");
		$("#zzjj").removeAttr("class");
		$("#jggs").removeAttr("class");
		$("#hmd").removeAttr("class");
		$("#bcgg").addClass("sel");
		$("#wjxz").removeAttr("class");
		
		$("#divBlackSearch").hide();
		$("#divSearch").show();
		
		$("#trans_status").hide();//显示拍卖方式隐藏交易状态
		$("#ztlab").hide();
		$("#fslab").show();
	}else if(clicktabId==5){
		pageObj.getBlack();//黑名单
		//显示此tab页，隐藏其它tab 并加上此tab背景
		$("#divallnotice").hide();
		$("#divnownotice").hide();
		$("#divlimittarget").hide();
		$("#divresultnotice").hide();
		$("#divsuppnotice").hide();
	   	$("#divblack").show();
	   	$("#divwj").hide();
	   	
		$("#cbgg").removeAttr("class");
		$("#zzgg").removeAttr("class");
		$("#zzjj").removeAttr("class");
		$("#jggs").removeAttr("class");
		$("#bcgg").removeAttr("class");
		$("#hmd").addClass("sel");
		$("#wjxz").removeAttr("class");
		
		$("#divBlackSearch").show();
		$("#divSearch").hide();
	}else if(clicktabId==6){
		pageObj.queryFileDownload();
		//显示此tab页，隐藏其它tab 并加上此tab背景
		$("#divallnotice").hide();
		$("#divnownotice").hide();
		$("#divlimittarget").hide();
		$("#divresultnotice").hide();
		$("#divsuppnotice").hide();
	   	$("#divblack").hide();
        $("#divwj").show();
	   	
		$("#cbgg").removeAttr("class");
		$("#zzgg").removeAttr("class");
		$("#zzjj").removeAttr("class");
		$("#jggs").removeAttr("class");
		$("#bcgg").removeAttr("class");
		$("#hmd").removeAttr("class");
		$("#wjxz").addClass("sel");
		
		$("#divBlackSearch").hide();
		$("#divSearch").hide();
		
	}
	if(clicktabId!=6){
		pageObj.loadPageBtn(pageObj.page);
	}else{
		$("#pagebtn").html('');
	}
}


//-----------------------------
//清空搜索项
//----------------------------
pageObj.clearSearch = function (){
	$("#no").val('');
	$("#goods_use").val('');
	$("#timetype").val('');
	$("#timevalue").val('');
	$("#name").val('');
	$("#trans_status").val('');
	$("#begin_notice_time").val('');
	$("#end_notice_time").val('');
}


//-----------------------------
//初始化面板点击事件
//----------------------------
pageObj.initTab = function (){
	$("#cbgg").bind("click",{tabId:"0"},pageObj.clickTab);
	$("#zzgg").bind("click",{tabId:"1"},pageObj.clickTab);
	$("#zzjj").bind("click",{tabId:"2"},pageObj.clickTab);
	$("#jggs").bind("click",{tabId:"3"},pageObj.clickTab);
	$("#bcgg").bind("click",{tabId:"4"},pageObj.clickTab);
	$("#hmd").bind("click",{tabId:"5"},pageObj.clickTab);
	$("#wjxz").bind("click",{tabId:"6"},pageObj.clickTab);
	
}

//-----------------------------
//渲染保证金
//----------------------------
pageObj.renderEarnestMoneny = function(earnest_money,unit) {
	var moneys = earnest_money;
	if (moneys) {
		var arr1 = moneys.split("&nbsp;");
		var arr2 = [];
		for ( var i = 0; i < arr1.length; i++) {
			var arr3 = arr1[i].split(":");
			arr2.push(comObj.cf({
				flag : arr3[0],
				unit : unit,
				amount : parseFloat(arr3[1])
			}));
		}
		return arr2.join(" ");
	}
	return moneys;
}

//-----------------------------
//下一页
//----------------------------
pageObj.nextPage =function(){
	pageObj.showWait();
	pageObj.page = pageObj.page+1;
	if(pageObj.pageCount>=pageObj.page){
		if(pageObj.tabId==0){
			pageObj.initPage();//全部
		}else if(pageObj.tabId==1){
			pageObj.nowtrans();//正在公告
		}else if(pageObj.tabId==2){
			pageObj.limittrans();//正在竞价
		}else if(pageObj.tabId==3){
			pageObj.resulttrans();//结果公示
		}else if(pageObj.tabId==4){
			pageObj.supptrans();//补充公告
		}else if(pageObj.tabId==5){
			pageObj.getBlack();//黑名单
		}
		pageObj.loadPageBtn(pageObj.page);
	}else{
		alert("已经是最后一页");
		pageObj.page = pageObj.page-1;
	}
}

//-----------------------------
//上一页
//----------------------------
pageObj.upPage =function(){
	pageObj.page = pageObj.page-1;
	if(1<=pageObj.page){//全部
		pageObj.showWait();
		if(pageObj.tabId==0){
			pageObj.initPage();//全部
		}else if(pageObj.tabId==1){
			pageObj.nowtrans();//正在公告
		}else if(pageObj.tabId==2){
			pageObj.limittrans();//正在竞价
		}else if(pageObj.tabId==3){
			pageObj.resulttrans();//结果公示
		}else if(pageObj.tabId==4){
			pageObj.supptrans();//补充公告
		}else if(pageObj.tabId==5){
			pageObj.getBlack();//黑名单
		}
		pageObj.loadPageBtn(pageObj.page);
	}else{
		alert("已经是第一页");
		pageObj.page = pageObj.page+1;
	}
}

//-----------------------------
//点击页数
//----------------------------
pageObj.toPage =function(page){
	pageObj.page = page;
	pageObj.showWait();
	if(pageObj.tabId==0){
		pageObj.initPage();//全部
	}else if(pageObj.tabId==1){
		pageObj.nowtrans();//正在公告
	}else if(pageObj.tabId==2){
		pageObj.limittrans();//正在竞价
	}else if(pageObj.tabId==3){
		pageObj.resulttrans();//结果公示
	}else if(pageObj.tabId==4){
		pageObj.supptrans();//补充公告
	}else if(pageObj.tabId==5){
		pageObj.getBlack();//黑名单
	}
	pageObj.loadPageBtn(pageObj.page);
}

//-----------------------------
//首页
//----------------------------
pageObj.sPage =function(){
	if(pageObj.page==1){
		alert("已经是第一页");
	}else{
		pageObj.page = 1;
		pageObj.toPage(pageObj.page);
		pageObj.loadPageBtn(pageObj.page);
	}
}

//-----------------------------
//最后一页
//----------------------------
pageObj.ePage =function(){
	if(pageObj.page==pageObj.pageCount){
		alert("已经是最后一页");
	}else{
		pageObj.page = pageObj.pageCount;
		pageObj.toPage(pageObj.page);
		pageObj.loadPageBtn(pageObj.page);
	}
}

//-----------------------------
//加载分页按扭,参数page为当前页
//----------------------------
pageObj.loadPageBtn = function(page){
	$("#pagebtn").html('');
	var i=1;
	if(page>5){//
		i=page-4;
	}
	var pagesum=pageObj.pageCount;
	if(pageObj.pageCount>page+5){
		if(page>5){
			pagesum=page+5;
		}else{
			if(pagesum<10){
				pagesum=pageObj.pageCount;
			}else{
				pagesum=10;
			}
		}
	}else{
		pagesum=pageObj.pageCount;
	}
	for(;i<=pagesum;i++){
		if(i==page){
			$("#pagebtn").html($("#pagebtn").html()+"<i class='cur'>"+i+"</i>");
		}else{
			$("#pagebtn").html($("#pagebtn").html()+"<i onclick=pageObj.toPage("+i+")>"+i+"</i>");
		}
		
	}
	
	if(pageObj.pageCount>0){//如果没有数据则不显示分页DIV
		$("#pagediv").show();
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
//搜索
//----------------------------
pageObj.btnsearch = function (){
	pageObj.showWait();
	pageObj.page=1;//初始化页面
	if(pageObj.tabId==0){
		pageObj.initPage();//全部
	}else if(pageObj.tabId==1){
		pageObj.nowtrans();//正在公告
	}else if(pageObj.tabId==2){
		pageObj.limittrans();//正在竞价
	}else if(pageObj.tabId==3){
		pageObj.resulttrans();//结果公示
	}else if(pageObj.tabId==4){
		pageObj.supptrans();//补充公告
	}else if(pageObj.tabId==5){
		pageObj.getBlack();//黑名单
	}
	pageObj.loadPageBtn(pageObj.page);
}

//-----------------------------
//高级搜索
//----------------------------
pageObj.adsearch = function (){
	var obj = {};
	// 地址
	obj.url = "portalSearch.html";
	// 参数
	obj.param = {
			tabId:pageObj.tabId
	};
	// 窗口参数
	var sheight = 375;
	var swidth = 500;
	obj.feature = 'dialogWidth=' + swidth + 'px;dialogHeight=' + sheight + 'px';
	var returnValue = DialogModal(obj);
	pageObj.searchValue=returnValue;
	pageObj.page=1;//初始化页面
	if(pageObj.tabId==0){
		pageObj.initPage();//全部
	}else if(pageObj.tabId==1){
		pageObj.nowtrans();//正在公告
	}else if(pageObj.tabId==2){
		pageObj.limittrans();//正在竞价
	}else if(pageObj.tabId==3){
		pageObj.resulttrans();//结果公示
	}else if(pageObj.tabId==4){
		pageObj.supptrans();//补充公告
	}else if(pageObj.tabId==5){
		pageObj.getBlack();//黑名单
	}
	pageObj.loadPageBtn(pageObj.page);
	pageObj.searchValue=null;
	
}

//-----------------------------
//搜索条件
//----------------------------
pageObj.searchCondition = function (cmd){
	cmd.canton=Utils.getPageValue("canton");
	if(pageObj.searchValue!=null){
		var no=pageObj.searchValue.no;
		var canton=pageObj.searchValue.canton;
		var goods_use=pageObj.searchValue.goods_use;
		var trans_status=pageObj.searchValue.trans_status;
		var timevalue=pageObj.searchValue.timevalue;
		var timetype=pageObj.searchValue.timetype;
		if(typeof(canton)!="undefined" && canton!=""){
			cmd.canton=canton;
		}
		if(typeof(no)!="undefined" && no!=""){
			cmd.no=no;
		}
		if(typeof(goods_use)!="undefined" && goods_use!=""){
			cmd.goods_use=goods_use;
		}
		if(typeof(trans_status)!="undefined" && trans_status!=""){
			cmd.trans_status=trans_status;
		}
		if(typeof(timevalue)!="undefined" && typeof(timetype)!="undefined" && timevalue!="" && timetype!=""){
			cmd.timetype=timetype;
			cmd.timevalue=timevalue;
		}
	}else{
		var goods_use=$("#goods_use").val();
		
		var no=$("#no").val();
		var name=$("#name").val();
		var trans_status=$("#trans_status").val();
		var begin_notice_time=$("#begin_notice_time").val();
		var end_notice_time=$("#end_notice_time").val();
		
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
		
		if(begin_notice_time!=""){
			cmd.begin_notice_time=begin_notice_time;
		}
		if(end_notice_time!=""){
			cmd.end_notice_time=end_notice_time;
		}
	
	}
}

//-----------------------------
//时间控件
//----------------------------
pageObj.initQueryTimeCondition = function() {
	$("#timevalue").ligerDateEditor();
	$("#begin_notice_time").ligerDateEditor();
	$("#end_notice_time").ligerDateEditor();
}

//-----------------------------
//获取交易系统的地址
//----------------------------
pageObj.getBuUrl = function (data){
	pageObj.bu_ip=data.bu_ip;
	pageObj.bu_web=data.bu_web;
	pageObj.demo_ip=data.demo_ip;
	pageObj.demo_web=data.demo_web;
	pageObj.buUrl=pageObj.bu_ip+pageObj.bu_web+"/login.html?goodsType=101";
	pageObj.demoUrl=pageObj.demo_ip+pageObj.demo_web+"/login.html?goodsType=101";
//	$('#ysb').attr("href",pageObj.demoUrl+"&versionMode=demo");
//	$('#userlogin').attr("href",pageObj.buUrl);
	$('#userlogin').click(function(){
		var canton1=Utils.getPageValue("canton");
		if(canton1!=null){
			$("input:radio[name='canton']").attr("disabled","disabled");
			$("input:radio[name='canton']").each(
					function(){
						if(this.value==canton1){
							$(this).attr("checked","checked");
						}
					}
			);
		}
		$("#win").show();
	});
	$("#winCitySure").click(function(){
		pageObj.cantonName=$("input:radio[name='canton']:checked").val();
		if(pageObj.cantonName!=null && pageObj.cantonName!=undefined && pageObj.cantonName!=""){
			var rn1=Math.floor(Math.random()*(pageObj.cantonName.length));
			var rn2=Math.floor(Math.random()*(pageObj.cantonName.length));
			var reg=eval("/(.{"+rn1+"})/");
			var reg1=eval("/(.{"+rn2+"})/");
			var newString=encodeURIComponent(pageObj.cantonName.replace(reg,"$1//123").replace(reg1,"$1//123")+":"+rn1+","+rn2);
			pageObj.buUrl+="&pa="+newString;
		}
		window.location.href=pageObj.buUrl;
	});
	$("#winCityCancel").click(function(){$("#win").hide();});
}

//-----------------------------
//查看标的详情
//----------------------------
pageObj.viewTarget = function(targetId,tabId){
	var obj = {};
	obj.url = approot + "/" + comObj.getGoodsUrlPath()+ "/trade/viewTarget.html";
	// 地址
	// 参数
	obj.param = {
		u : 'portal',
		tabId : tabId,
		targetId : targetId
	};
	// 窗口参数
	var sheight = screen.height - 150;
	var swidth = screen.width - 250;
	obj.feature = 'dialogWidth=' + swidth + 'px;dialogHeight=' + sheight + 'px';
	DialogModal(obj);
}

//-----------------------------
//
//----------------------------
pageObj.toLoginPage = function (){
	window.open(pageObj.buUrl);
}

//-----------------------------
//翻页后返回顶部
//----------------------------
pageObj.myScroll = function () { 
//前边是获取chrome等一般浏览器 如果获取不到就是ie了 就用ie的办法获取 
	var x=document.body.scrollTop||document.documentElement.scrollTop; 
	var timer=setInterval(function(){ 
	x=x-100; 
	if(x<100) 
	{ 
		x=0; 
		window.scrollTo(x,x); 
		clearInterval(timer); 
	} 
		window.scrollTo(x,x); 
	},"50"); 
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

//-----------------------------
//关闭等待窗口
//----------------------------
pageObj.hideWait = function() {
	$("#divwait").hide();
	$("#btnsearch").removeAttr("disabled"); 
}

//-----------------------------
//打开等待窗口
//----------------------------
pageObj.showWait = function(){
	$("#divwait").show();//加载等待
	$("#btnsearch").attr('disabled',true);
}

//-----------------------------
//回车搜索
//----------------------------
pageObj.BindEnter = function(obj) {    
	 if(obj.keyCode == 13) {           
		 pageObj.btnsearch();
	 }         
} 

//-----------------------------
//没有查询到数据
//----------------------------
pageObj.nullHint = function() {    
	 $("#pagediv").hide();
	 return "<div class=' f14 fb c2 tc' style='line-height:360px;'>没有查询到相关数据！</div>";      
} 

//-----------------------------
//回到顶部的滚动条
//----------------------------
pageObj.divscroll = function (){
	var x=document.body.scrollTop||document.documentElement.scrollTop; 
	if(x>0){
		$("#floatBtn1").show();
		$("#floatBtn2").hide();
	}else{
		$("#floatBtn1").hide();
		$("#floatBtn2").hide();
	}
}
pageObj.scrolltop = function(){
	$(window).scrollTop(0);
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
//交易大厅
//----------------------------
pageObj.tradeMonitor=function() {
	var obj={};
	obj.url=approot+'/land/trade/tradeMain.html';
	obj.feature="dialogWidth=1000px;dialogHeight=725px";
	obj.param = {};
	obj.param.goodsType = '101';
	obj.param.u = 'portal';
	obj.param.method='getTargetsForMonitor';
   	DialogModal(obj);
}  	

$(document).ready(function() {
	pageObj.showWait();
	$("#nextPage").bind("click",pageObj.nextPage);//绑定下一页点击事件
	$("#upPage").bind("click",pageObj.upPage);//绑定上一页点击事件
	$("#btnsearch").bind("click",pageObj.btnsearch);//绑定搜索点击事件
	$("#btnblacksearch").bind("click",pageObj.btnsearch);//绑定搜索点击事件
	$("#reset").bind("click",pageObj.clearSearch);//绑定搜索点击事件
	pageObj.initUse();
	pageObj.initQueryTimeCondition();
	pageObj.initTab();
	pageObj.initPage();
	pageObj.loadPageBtn(1);
	$("#homePage").bind("click",pageObj.sPage);//首页
	$("#endPage").bind("click",pageObj.ePage);//最后一页
	$("#landlogin").bind("click",pageObj.toLoginPage);//登录事件
	window.onscroll=pageObj.divscroll;

	$("#divBlackSearch").hide();
	$("#divSearch").show();
	
	$("#floatBtn1").hide();//初始隐藏回到顶倍的DIV
	$("#floatBtn2").hide();
	$("#scroll_Top").bind("click",pageObj.scrolltop);
	
	$("#fslab").hide();
	
	pageObj.addStatusAll();
	
	$('#btnMonitor').bind("click",pageObj.tradeMonitor);//交易监控
	
	$("#adsearch").click(function(){
		pageObj.adsearch();
	});
	$("#xtbz").click(function(){
		window.open("/heyuan/help/help_index.html");
	});
	$("#hmd").hide();
});