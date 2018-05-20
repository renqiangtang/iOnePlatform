var pageObj = {};
pageObj.id=null;//股东ID
pageObj.pd_id=null;//主股东竞买人ID
pageObj.status=null;
pageObj.init =function() {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.method = "editProjectBidder";
	cmd.id=pageObj.id;
	cmd.pd_id=pageObj.pd_id;
	cmd.success = function(data) {
		if(data.projectBidder!=null){
			Utils.setForm(data.projectBidder);
			$("input[name='bidder_type']").each(function () {
				if ($(this).val() == data.projectBidder.biddertype) {
						$(this).attr("checked", "checked");
				}
			});
			if(data.projectBidder.biddertype==0){
				$("#notext").html("身份证号码");
			}else{
				 $("#notext").html("组织机构代码");
			}
		}
	};
	cmd.execute();
	
}

//保存股东数据
pageObj.save = function(){
	if (!Utils.requiredCheck())
		return;
	
	var biddertype;
	$('input:[name=bidder_type]:radio').each(function (){
		if(this.checked == true){
			biddertype = this.value;  
		}
	}); 
	if($("#percent").val()<1 || $("#percent").val()>100){
		DialogAlert("股东比率只能在1-100之间");
		return;
	}
	if(biddertype==0){
		if($("#idno").val()==""){
			DialogAlert("身份证号码不能为空！");
			return;
		}
		if($("#idno").val().length!=15 && $("#idno").val().length!=18){
			DialogAlert("身份证号码格式不正确！");
			return;
		}
	}else{
		if($("#idno").val()==""){
			DialogAlert("组织机构代码不能为空！");
			return;
		}
	}
	
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "User";
	cmd.method = "saveProjectBidder";
	cmd.id=pageObj.id;
	cmd.pd_id=pageObj.pd_id;
	Utils.getForm(cmd);
	cmd.success = function(data) {
		if(data.state==1){
			pageObj.id=data.id;
			DialogAlert("保存成功！",window);
			window.returnValue = true;
		}
//		if(data.totalRowCount>1){
//			Utils.setForm(data.projectBidder);
//		}
	};
	cmd.execute();
}


$(document).ready(function(){
	pageObj.id = Utils.getPageValue('id');
	pageObj.pd_id = Utils.getPageValue('pd_id');
	pageObj.init();
	$("#btnSave").click(pageObj.save);
	 $('#bidder_type1').click(function(event) {
		 $("#notext").html("身份证号码");
	 });
	 $('#bidder_type2').click(function(event) {
		 $("#notext").html("组织机构代码");
	 });
});

