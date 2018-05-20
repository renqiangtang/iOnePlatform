var pageObj = {};
pageObj.goodsType = null;
pageObj.licenseId = null;

//获取竞买方式
pageObj.getUnion=function(){
	cmd = new Command();
	cmd.module = "bidder";
	cmd.service = "Apply";
	cmd.method = "getUnion";
	cmd.licenseId = pageObj.licenseId;
	cmd.success = function(data) {	
		$("input[name=style]:eq("+data.style+")").attr("checked",'checked'); 
		if(data.style==1){//联合竞买，列出联合人
			$("#unionTable").show();
			var objs=data.Rows;
			$(objs).each(function(i, ele) {
				var html = '';
				html += '<tr>';
				html += '	 <td>'+(i+1)+'</td>';
				html += '	 <td>'+ele.name+'</td>';
				html += '    <td>'+ele.percent;
				html += '    ％</td>';
				html += '</tr>';
				$('#unionTableTBody').append(html);
			});
		}else{
			//独立竞买，显示公司类型
			$("#unionTable").hide();
			$("input[name=dlradio]:eq("+data.dlstyle+")").attr("checked",'checked'); 
		}
		
		if(data.projectDealer!=""){//有拟成立项目公司
			var projectDealer=data.projectDealer;
			//拟成立项目公司信息
			$("input[name=gstype]:eq("+projectDealer[0].gs_type+")").attr("checked",'checked'); 
			$("#name").val(projectDealer[0].name); 
			$("#contact").val(projectDealer[0].contact); 
			$("#fax").val(projectDealer[0].fax);
			$("#contact_tel").val(projectDealer[0].contact_tel);
			$("#tel").val(projectDealer[0].tel);
			$("#contact_office").val(projectDealer[0].contact_office);
			$("#trust_name").val(projectDealer[0].trust_name);
			$("#corporation").val(projectDealer[0].corporation);
			$("#reg_capital").val(projectDealer[0].reg_capital);
			$("#reamrk").val(projectDealer[0].reamrk);
			//显示股东
			var objs=data.RowsPDO;
			$(objs).each(function(i, ele) {
				var html = '';
				html += '<tr>';
				html += '	 <td>'+pageObj.parseNull(ele.name)+'</td>';
				html += '	 <td>'+pageObj.parseNull(ele.idno)+'</td>';
				html += '	 <td>'+pageObj.parseNull(ele.tel)+'</td>';
				html += '	 <td>'+pageObj.parseNull(ele.address)+'</td>';
				html += '	 <td>'+pageObj.parseNull(ele.email)+'</td>';
				html += '	 <td>'+pageObj.parseNull(ele.reamrk)+'</td>';
				html += '    <td>'+pageObj.parseNull(ele.percent);
				html += '    ％</td>';
				html += '</tr>';
				$('#projectTableTBody').append(html);
			});
		}else{
			$("#corpInfoTable").hide();
			$("#dtlTable").hide();
			$("#yclgg").html("没有拟成立项目公司");
			
		}
		
	}
	cmd.execute();
}

pageObj.parseNull=function(s){
	if(s=="null" || s==null || s=="undefined" || s==undefined){
		return "";
	}else{
		return s;
	}
}

$(document).ready(function() {
	pageObj.licenseId = Utils.getPageValue('licenseId');
	pageObj.goodsType = Utils.getPageValue('goodsType');
	pageObj.getUnion();
	$("#btnPrint").click(
			function(){
				$('#btnPrint').hide();
				var mainFrame = getMainFrame(window);
				mainFrame.print(window);
				$('#btnPrint').show();
			}
			
		);
	$("input[type=radio]").attr("disabled",true);
});