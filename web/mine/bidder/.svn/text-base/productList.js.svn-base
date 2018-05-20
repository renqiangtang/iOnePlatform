var pageObj = {};
pageObj.page = 1;
pageObj.goodsType = null;
pageObj.cantonName = null;
// ---------------------------------
// 查询按钮事件mine
// ---------------------------------
pageObj.query = function(id) {
	pageObj.page = 1;
	if (!$('#' + id).hasClass('curr')) {
		var type = $('#' + id).attr('type');
		$('*[type="' + type + '"]').removeClass('curr');
		$('#' + id).addClass('curr');
		pageObj.exeQuery();
	}
}

pageObj.queryAll = function() {
	pageObj.page = 1;
	$('.curr').each(function(index, ele) {
		$(ele).removeClass('curr');
	});
	$('#canton0').addClass('curr');
	$('#businessType0').addClass('curr');
	$('#transType0').addClass('curr');
	pageObj.exeQuery();
}

pageObj.pagePrev = function() {
	pageObj.page--;
	pageObj.exeQuery();
}

pageObj.pageNext = function() {
	pageObj.page++;
	pageObj.exeQuery();
}

pageObj.exeQuery = function() {
	var cmd = new Command();
	cmd.module = "bidder";
	cmd.service = "TransLicenseMine";
	cmd.method = "transTargetlist";
	cmd.page = pageObj.page;
	$('.curr').each(function(index, ele) {
		var type = $(ele).attr('type');
		if (type) {
			var id2 = $(ele).attr('id').substr(type.length);
			if ('0' != id2) {
				cmd[type + 'Id'] = id2;
			}
		}
	});
	if(pageObj.cantonName!=undefined && pageObj.cantonName!=null && pageObj.cantonName!=""){
		cmd.cantonId=pageObj.cantonName;
		$("dl").each(
			function(){
				if($(this).attr("class")=="fore"){
					$(this).hide();
				}
			}
		);
	}
	cmd.success = function(data) {
		var html = '';
		var arr = data.Rows;
		for ( var i = 0; i < arr.length; i++) {
			var obj = arr[i];
			html += '<div class="targetItem"  >';
			html += '<div class="targetItem_unselect" onmouseover="pageObj.targetItem_onmouseover(this);" onmouseout="pageObj.targetItem_onmouseout(this);">';
			html += '<div class="t-head">';
			html += '	<div class="text">' + obj.business_type_name + '</div>';
			html += '	<div class="more" title="多币种" style="display:none"></div>';
			html += '</div>';
			html += '	<div class="t-body">';
			html += '	<div class="t-body-name" onclick="javascript:pageTradeCommonObj.viewTarget(\'' + obj.id + '\');">' + obj.no + '</div>';
			html += '	<ul class="t-body-cont">';
			html += '		<li class="first-li">';
			html += '			<div class="name">起始价</div>';
			html += '			<div class="value">' + obj.begin_price.toWanYuan() + '万</div>';
			html += '		</li>';
			html += '		<li class="sec-li">';
			html += '			<div class="name">增价幅度</div>';
			html += '			<div class="value">'+obj.price_step.toWanYuan()+'万</div>';
			html += '		</li>';
			html += '		<li class="third-li">';
			html += '			<div class="name">保证金</div>';
			if(obj.target_earnest_money[0].currency=="CNY" || obj.target_earnest_money[0].currency=="cny" || obj.target_earnest_money[0].currency=="RMB"){
				html += '			<div class="value">'+obj.target_earnest_money[0].amount.toWanYuan()+'万</div>';
			}else{
				html += '			<div class="value">'+obj.target_earnest_money[0].amount+comObj.Currency[obj.target_earnest_money[0].currency]+'</div>';
			}
			
			html += '		</li>';
			html += '	</ul>';
			html += '	</div>';
			html += '	<div class="t-footer tc">';
			if (obj.is_bid == 1) {
				html += '	<span class="f-gm" title="直接购买" onclick="pageObj.addApply(\'' + obj.id + '\',\'' + obj.notice_name + '\',\'' + obj.no + '\',\'' + obj.notice_no + '\',\'' + obj.allow_trust + '\');"></span>';
//				html += '	<span class="f-gwc" title="加入购物车" onclick="pageObj.addToCar(\'' + obj.id + '\');"></span>';
			} else {
				html += '	<div class="t-footer-sel">' + obj.bid_message + '</div>';
			}
			html += '	</div>';
			html += '	</div>';
			html += '</div>';
		}
		$('#productDiv').html(html);
		Utils.autoIframeSize();
		// 分页信息
		html = data.page + '/' + data.pageTotal;
		$('#pageInfo1').html(html);
		$('#pageInfo2').html(html);
		if (data.page > 1) {
			$('#pagePrev1').show();
			$('#pagePrev2').show();
		} else {
			$('#pagePrev1').hide();
			$('#pagePrev2').hide();
		}
		if (data.page < data.pageTotal) {
			$('#pageNext1').show();
			$('#pageNext2').show();
		} else {
			$('#pageNext1').hide();
			$('#pageNext2').hide();
		}

	}
	cmd.execute();
}

pageObj.targetItem_onmouseover = function(obj) {
	// $(obj).removeClass('targetItem_unselect');
	// $(obj).addClass('targetItem_select');
}

pageObj.targetItem_onmouseout = function(obj) {
	// $(obj).removeClass('targetItem_select');
	// $(obj).addClass('targetItem_unselect');
}

pageObj.image_onerror = function(ele) {
	$(ele).attr('src', 'images/base/btn/000.jpg');
}

pageObj.addToCar = function(targetId) {
	var cmd = new Command();
	cmd.module = "bidder";
	cmd.service = "TransLicenseMine";
	cmd.method = 'addToCar';
	cmd.targetId = targetId;
	cmd.u = pageObj.u;
	cmd.success = function(data) {
		alert(data.message);
		if (data.state == 1) {
			pageObj.exeQuery();
			parent.pageObj.refreshCar();
		}
	}
	cmd.execute();
}

pageObj.addApply = function(targetId, noticeName, targetName,noticeNo,allowTrust) {
	var cmd1 = new Command();
	cmd1.module = "sysman";
	cmd1.service = "User";
	cmd1.method = "checkBlackUser";
	cmd1.u = pageObj.u;
	cmd1.goodsType = pageObj.goodsType;
	cmd1.success = function(data1) {
		if(data1.state!=1){
			DialogAlert(data1.message);
			return;
		}
		var cmd = new Command();
		cmd.module = "bidder";
		cmd.service = "Apply";
		cmd.method = 'checkCarTarget';
		cmd.targetId = targetId;
		cmd.u = pageObj.u;
		cmd.success = function(data) {
			if (data.state == 1) {
				var obj = {};
				// 地址
				obj.url = "applyMain.html";
				// 参数
				obj.param = {
					u : pageObj.u,
					targetId : targetId,
					noticeName : noticeName,
					targetName : targetName,
					goodsType : pageObj.goodsType,
					tnname : noticeNo,
					allowTrust : allowTrust,
					userName : getUserInfoObj().displayName
				};
				// 窗口参数
				obj.feature = "dialogWidth=1000px;dialogHeight=600px";
				var returnValue = DialogModal(obj);
				if (returnValue) {
					pageObj.exeQuery();
					parent.pageObj.refreshCar();
				}
			} else {
				DialogAlert(data.message);
			}
		}
		cmd.execute();
	}
	cmd1.execute();
}

// ---------------------------------
// 初始化查询条件
// ---------------------------------
pageObj.initPage = function() {
	var cmd = new Command();
	cmd.module = "bidder";
	cmd.service = "TransLicenseMine";
	cmd.method = "initProductListPageParam";
	cmd.u = pageObj.u;
	cmd.success = function(data) {
		var html = '<div><a id="canton0" type="canton"  href="javascript:pageObj.query(\'canton' + 0 + '\');" class="curr">不限</a></div>';
		var arr = data.cantons;
		for ( var i = 0; i < arr.length; i++) {
			html += '<div><a id="canton' + arr[i].id + '" type="canton" href="javascript:pageObj.query(\'canton' + arr[i].id + '\');">' + arr[i].name + '</a></div>';
		}
		$('#cantonDiv').html(html);
		//
		html = '<div><a id="businessType0" type="businessType"  href="javascript:pageObj.query(\'businessType' + 0 + '\');" class="curr">不限</a></div>';
		arr = data.businessTypes;
		for ( var i = 0; i < arr.length; i++) {
			html += '<div><a id="businessType' + arr[i].id + '" type="businessType" href="javascript:pageObj.query(\'businessType' + arr[i].id + '\');">' + arr[i].name + '</a></div>';
		}
		$('#businessTypeDiv').html(html);
		//
		html = '<div><a id="transType0" type="transType"  href="javascript:pageObj.query(\'transType' + 0 + '\');" class="curr">不限</a></div>';
		arr = data.transTypes;
		for ( var i = 0; i < arr.length; i++) {
			html += '<div><a id="transType' + arr[i].id + '" type="transType" href="javascript:pageObj.query(\'transType' + arr[i].id + '\');">' + arr[i].name + '</a></div>';
		}
		$('#transTypeDiv').html(html);

	}
	cmd.execute();
}

// ---------------------------------
// 页面初始化
// ---------------------------------
$(document).ready(function() {
	pageObj.u = getUserId();
	pageObj.initPage();
	pageObj.cantonName=Utils.getUrlParamValue(window.parent.document.location.href, "pa");
	if(pageObj.cantonName!=undefined && pageObj.cantonName!=null && pageObj.cantonName!=""){
		pageObj.cantonName=decodeURIComponent(encodeURIComponent(pageObj.cantonName).replace("%2F%2F123","").replace("%2F%2F123","")).split(":")[0];
		alert(pageObj.cantonName);
	}
	$('#queryAll').attr('href', 'javascript:pageObj.queryAll();');
	$('#pagePrev1').attr('href', 'javascript:pageObj.pagePrev();');
	$('#pagePrev2').attr('href', 'javascript:pageObj.pagePrev();');
	$('#pageNext1').attr('href', 'javascript:pageObj.pageNext();');
	$('#pageNext2').attr('href', 'javascript:pageObj.pageNext();');
	pageObj.goodsType= Utils.getUrlParamValue(document.location.href, "goodsType");
	pageObj.queryAll();
});
