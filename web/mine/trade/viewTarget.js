// ---------------------------------
// 页对象
// ---------------------------------
var pageObj = {};
// ---------------------------------
// 竞价记录已加载
// ---------------------------------
pageObj.targetOfferLogLoaded = false;
// ---------------------------------
// 公告已加载
// ---------------------------------
pageObj.lastNoticeId = "";
// ---------------------------------
// 相关下载已加载
// ---------------------------------
pageObj.targetAttachLoaded = false;
pageObj.targetImagesLoaded = false;
// ---------------------------------
// 公告切换显示标识
// ---------------------------------
pageObj.noticePanalVisible = 1;
// ---------------------------------
// 公告缓存
// ---------------------------------
pageObj.noticeList = null;
pageObj.othernoticeList = null;
pageObj.lastNoticeId = "";
// ---------------------------------
// 交易物图片附件
// ---------------------------------
pageObj.goodsImages = null;
pageObj.targetExists = false;

pageObj.u = null;

// ---------------------------------
// 标签页切换
// ---------------------------------

pageObj.changedTab = function(paramA, paramB, paramC) {
	if (paramC == "subA") {// 加载公告
		pageObj.divDis(0);
		pageObj.loadTargetNotice("");
	} else if (paramC == "subB") {// 图片
		pageObj.divDis(1);
		pageObj.loadTargetImages();
	} else if (paramC == "subC") {// 加载相关下载
		pageObj.divDis(2);
		pageObj.queryTargetAttachData();
	} else if (paramC == "subD") {// 加载竞价记录
		pageObj.divDis(3);
		pageObj.loadTargetOfferLog();
	} else if (paramC == "subE") {// 加载竞价记录
		pageObj.divDis(4);
	}else if (paramC == "subF") {// 加载补充公告
		pageObj.divDis(5);
		pageObj.loadTargetOtherNotice();
	}
}

pageObj.convertFloatValue = function(value, convertUnit) {
	return comObj.cf({unit:convertUnit,amount:value}); 
	
//	
//	
//	if (value == null || isNaN(value))
//		return "";
//	else {
//		var fltValue = parseFloat(value);
//		if (convertUnit && convertUnit == "万元")
//			fltValue = parseFloat((fltValue / 10000).toFixed(6));
//		return fltValue.addComma();
//	}
}

pageObj.subStrValue = function(value, begin, end) {
	if (value == null || value == "")
		return "";
	else {
		return value.substr(begin, end);
	}
}

// ---------------------------------
// 查询标的信息
// ---------------------------------
pageObj.queryTargetData = function() {
	pageObj.targetExists = false;
	var cmd = new Command();
	cmd.module = "tradeland";
	cmd.service = "ViewTarget";
	cmd.method = "getTargetData";
	cmd.id = pageObj.targetId;
	cmd.success = function(data) {
		if (data.state == 1) {
			pageObj.businessTypeConfig = data.businessTypeConfig;
			pageObj.targetExists = data.rs.length > 0;
			if (pageObj.targetExists) {
				pageObj.businessType = Utils.getRecordValue(data, 0, 'business_type');
				$("#targetName").html(Utils.getRecordValue(data, 0, 'target_name'));
				$("#goodsLabel").html((pageObj.businessTypeConfig ? pageObj.businessTypeConfig.goods_name : "交易物")
						+ "信息");
				pageObj.transType = Utils.getRecordValue(data, 0, 'trans_type');
				pageObj.isNetTrans = Utils.getRecordValue(data, 0, 'is_net_trans');
				pageObj.priceUnit = Utils.getRecordValue(data, 0, 'unit');
				$("#transType").html(Utils.getRecordValue(data, 0, 'trans_type_label'));
				$("#businessType").html(pageObj.businessTypeConfig ? pageObj.businessTypeConfig.name : "其它");
				$("#allowUnion").html(Utils.getParamValue("0=否;1=是", Utils.getRecordValue(data, 0, 'allow_union')));
				var multi_earnestmoney = Utils.getRecordValue(data, 0, 'multi_earnestmoney');
				var earnestmoney_show = pageObj.formatEarnestMoney(multi_earnestmoney,pageObj.priceUnit);
				$("#earnestMoney").html(earnestmoney_show);
				$("#beginPrice").html(pageObj.convertFloatValue(Utils.getRecordValue(data, 0, 'begin_price'),
						pageObj.priceUnit));
				$("#priceStep").html(pageObj.convertFloatValue(Utils.getRecordValue(data, 0, 'price_step'),
						pageObj.priceUnit));
				// $("#priceUnit").html(pageObj.priceUnit);
				$("#priceUnit").html('');
				$("#priceUnit1").html(pageObj.priceUnit);
				$("#priceUnit2").html(pageObj.priceUnit);
				$("#beginApplyTime")
						.html(pageObj.subStrValue(Utils.getRecordValue(data, 0, 'begin_apply_time'), 0, 16));
				$("#endApplyTime").html(pageObj.subStrValue(Utils.getRecordValue(data, 0, 'end_apply_time'), 0, 16));
				$("#beginFocusTime")
						.html(pageObj.subStrValue(Utils.getRecordValue(data, 0, 'begin_focus_time'), 0, 19));
				$("#endFocusTime").html(pageObj.subStrValue(Utils.getRecordValue(data, 0, 'end_focus_time'), 0, 19));
				$("#beginLimitTime")
						.html(pageObj.subStrValue(Utils.getRecordValue(data, 0, 'begin_limit_time'), 0, 19));
				$("#endEarnestTime").html(pageObj.subStrValue(Utils.getRecordValue(data, 0, 'end_earnest_time'), 0, 19));
				
				if (pageObj.transType == "1") {
					$("#targetHasListTime").hide();
					$("#targetHasLimitTime").show();
					$("#focusTime").hide();
				} else {
					$("#targetHasListTime").show();
					var isLimitTrans = Utils.getRecordValue(data, 0, 'is_limit_trans');
					if (isLimitTrans = "1") {
						$("#targetHasLimitTime").show();
					} else {
						$("#targetHasLimitTime").hide();
					}
				}
				var goodsViewerUrl = null;
				// if (pageObj.businessTypeConfig
				// && (pageObj.businessTypeConfig.goodsViewModuleUrl ||
				// pageObj.businessTypeConfig.goodsViewModuleId)) {
				// if (pageObj.businessTypeConfig.goodsViewModuleId) {
				// if (pageObj.businessTypeConfig.modules &&
				// pageObj.businessTypeConfig.goodsViewModuleId in
				// pageObj.businessTypeConfig.modules)
				// goodsViewerUrl =
				// pageObj.businessTypeConfig.modules[pageObj.businessTypeConfig.goodsViewModuleId];
				// } else {
				// goodsViewerUrl =
				// pageObj.businessTypeConfig.goodsViewModuleUrl;
				// }
				// }
				if (goodsViewerUrl) {
					$("#goodsPanel").html("<iframe id='goodsViewer' width='100' height='100' frameborder='0' src='"
							+ goodsViewerUrl + "'></iframe>");
				} else {
					$("#buildArea").html(Utils.getRecordValue(data, 0, 'build_area'));
					$("#landArea").html(Utils.getRecordValue(data, 0, 'land_area'));
					$("#cubage").html(Utils.getRecordValue(data, 0, 'cubage'));
					$("#goodsUse").html(Utils.getRecordValue(data, 0, 'goods_use'));
					$("#address").html(Utils.getRecordValue(data, 0, 'address'));
				}
				pageObj.targetStatus = Utils.getRecordValue(data, 0, 'status');
				if (pageObj.targetStatus == 5 || pageObj.targetStatus == 6 ||  pageObj.targetStatus==7) {
					$("#tabTransNotice").show();
					$("#status").html(Utils.getParamValue("5=已成交;6=未成交", pageObj.targetStatus));
					if (pageObj.targetStatus == 5) {
						$("#transPrice").html(Utils.getRecordValue(data, 0, 'trans_final_price').replace("价格：","").replace("价格指标:"));
						$("#landPrice").html(pageObj.convertFloatValue(Utils.getRecordValue(data, 0, 'unit_price')).replace("价格：",""));
						$("#bulidPrice").html(pageObj.convertFloatValue(Utils.getRecordValue(data, 0, 'building_price')).replace("价格：",""));
						$("#transBidder").html(Utils.getRecordValue(data, 0, 'bidder_name'));
						$("#lblEndTtransTime").html("成交时间");
					}else if(pageObj.targetStatus == 7) {
						$("#subE").html("<div class=' f14 fb tc' style='line-height:35px;padding-top:250px;'>网上竞价程序结束。<br/>待审查竞得资格后，系统直接公布具体成交公示情况，敬请关注。</div>");
					} else {
						$("#transPrice").html("-");
						$("#landPrice").html("-");
						$("#bulidPrice").html("-");
						$("#transBidder").html("-");
						$("#lblEndTtransTime").html("流拍时间");
					}
					$("#priceUnit3").html(pageObj.priceUnit);
					$("#endTtransTime").html(pageObj
							.subStrValue(Utils.getRecordValue(data, 0, 'end_trans_time'), 0, 19));
				} else {
					$("#tabTransNotice").hide();
				}
			}
		} else {
			DialogError('查询标的信息失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

// ---------------------------------
// 格式化保证金
// ---------------------------------
pageObj.formatEarnestMoney = function(multi_earnestmoney,unit) {
	if (multi_earnestmoney == null || multi_earnestmoney == "") {
		return "";
	}
	var earnestmoney_array = multi_earnestmoney.split("&nbsp;");
	var earnestmoney_show = "";
	if (earnestmoney_array != null && earnestmoney_array.length > 0) {
		for (var i = 0; i < earnestmoney_array.length; i++) {
			var earnestmoney_temp = earnestmoney_array[i];
			var temp_array = earnestmoney_temp.split(":");
			if (temp_array != null && temp_array != "" && temp_array.length == 2) {
				var cfObj={};
				cfObj.unit=unit;
				cfObj.flag=temp_array[0];
				cfObj.amount=parseFloat(temp_array[1]);
				earnestmoney_show += comObj.cf(cfObj) + "&nbsp;";
			}
		}
	}
	if (earnestmoney_show == null || earnestmoney_show == "") {
		earnestmoney_show = multi_earnestmoney;
	}
	return earnestmoney_show;
}

// ---------------------------------
// 查询标的公告
// ---------------------------------
pageObj.notice_templet_no_array = {};
pageObj.notice_templet_no_array['101001'] = 'businessType101001NoticeAttach';
pageObj.notice_templet_no_array['101002'] = 'businessType101002NoticeAttach';
pageObj.notice_templet_no_array['201003'] = 'businessType201003NoticeAttach';
pageObj.notice_templet_no_array['201004'] = 'businessType201004NoticeAttach';
pageObj.notice_templet_no_array['301001'] = 'businessType301001NoticeAttach';
pageObj.notice_templet_no_array['301002'] = 'businessType301002NoticeAttach';
pageObj.notice_templet_no_array['401001'] = 'businessType401001NoticeAttach';
pageObj.notice_templet_no_array['401002'] = 'businessType401002NoticeAttach';

pageObj.queryTargetNoticeData = function() {
	var cmd = new Command();
	cmd.module = "tradeland";
	cmd.service = "ViewTarget";
	cmd.method = "getTargetNoticeData";
	cmd.target_id = pageObj.targetId;
	cmd.templet_no = pageObj.notice_templet_no_array[pageObj.businessType];
	cmd.success = function(data) {
		if (data.state == 1) {
			if (pageObj.noticeList == null)
				pageObj.noticeList = new Array();
			pageObj.noticeList.splice(0, pageObj.noticeList.length);
			if (data.rs.length == 0) {
				$("#multiNoticePanal").hide();
				$("#notice").html("无公告内容。");
			} else {
				var blnNoticeType1 = false;
				var blnNoticeType2 = false;
				var multiNoticesHtml = "";
				var otherNoticeCount=0;//计算非普通公告的数量，如果等于 0则隐藏其它公告标签
				for (var i = 0; i < data.rs.length; i++) {
					var notice = {};
					notice.id = Utils.getRecordValue(data, i, 'id');
					notice.no = Utils.getRecordValue(data, i, 'no');
					notice.name = Utils.getRecordValue(data, i, 'name');
					notice.notice_type = Utils.getRecordValue(data, i, 'notice_type');
					notice.notice_date = Utils.getRecordValue(data, i, 'notice_date');
					notice.lob_id = Utils.getRecordValue(data, i, 'lob_id');
					notice.page_count = Utils.getRecordValue(data, i, 'page_count');
					var noticeTitle = notice.name + " " 
							+ (notice.notice_date != null ? "(" + notice.notice_date.substr(0, 10) + ")" : "");
					if (notice.notice_type == 0) {
						$("#targetNotice").html(noticeTitle);
						$("#targetNotice").data("noticeId", notice.id);
						$("#targetNotice").bind("click",pageObj.loadTargetNotice('" + notice.id + "'));
						pageObj.noticeList.push(notice);
					}else{
						otherNoticeCount=1;
					}
				}
				$("#multiNotices").html(multiNoticesHtml);
				if(otherNoticeCount==0){
					$("#tabOtherNotice").hide();
				}
				if (data.rs.length == 1) {
					$("#multiNoticePanal").hide();
				}
			}
		
		} else {
			DialogError('查询标的公告信息失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}


//---------------------------------
//查询标的补充公告
//---------------------------------
pageObj.queryTargetOtherNoticeData = function() {
	pageObj.othernoticeList=null;
	var cmd = new Command();
	cmd.module = "tradeland";
	cmd.service = "ViewTarget";
	cmd.method = "getTargetNoticeData";
	cmd.target_id = pageObj.targetId;
	cmd.templet_no = pageObj.notice_templet_no_array[pageObj.businessType];
	cmd.success = function(data) {
		$("#multiNotices").html("");
		if (data.state == 1) {
			if (pageObj.othernoticeList == null)
				pageObj.othernoticeList = new Array();
			pageObj.othernoticeList.splice(0, pageObj.othernoticeList.length);
			if (data.rs.length == 0) {
				//$("#multiNoticePanal").hide();
				$("#multiNotices").html("没有其它公告");
			} else {
				var blnNoticeType1 = false;
				var blnNoticeType2 = false;
				var multiNoticesHtml = "";
				for (var i = 0; i < data.rs.length; i++) {
					var notice = {};
					notice.id = Utils.getRecordValue(data, i, 'id');
					notice.no = Utils.getRecordValue(data, i, 'no');
					notice.name = Utils.getRecordValue(data, i, 'name');
					notice.notice_type = Utils.getRecordValue(data, i, 'notice_type');
					notice.notice_date = Utils.getRecordValue(data, i, 'notice_date');
					notice.lob_id = Utils.getRecordValue(data, i, 'lob_id');
					notice.page_count = Utils.getRecordValue(data, i, 'page_count');
					var noticeTitle = notice.name + " " 
							+ (notice.notice_date != null ? "(" + notice.notice_date.substr(0, 10) + ")" : "");
					 if (notice.notice_type == 1) {
						multiNoticesHtml += "<li>[补充公告]";
						multiNoticesHtml += "<a href='javascript:void(0)' onclick='javascript:pageObj.loadTargetOtherNotice(\"" + notice.id+ "\");return false;' id='N_" + notice.id + "'>" + noticeTitle + "</a></li>";
						pageObj.othernoticeList.push(notice);
					} else if (notice.notice_type == 2) {
						multiNoticesHtml += "<li>[紧急公告]";
						multiNoticesHtml += "<a href='javascript:void(0)' onclick='javascript:pageObj.loadTargetNotice(\"" + notice.id
								+ "\");return false;' id='N_" + notice.id + "'>" + noticeTitle + "</a></li>";
						pageObj.othernoticeList.push(notice);
					}
				}
				$("#multiOtherNotices").html(multiNoticesHtml);
			}
		
		} else {
			DialogError('查询标的补充公告信息失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}


// ---------------------------------
// 加载公告内容
// ---------------------------------
pageObj.loadTargetNotice = function(noticeId) {
	var currentNoticeId = noticeId;
	if (pageObj.noticeList == null) {// 未查询公告则先查询
		pageObj.queryTargetNoticeData();
	}
	if (pageObj.noticeList.length == 0) {// 无公告
		return;
	}
	if (currentNoticeId == undefined || currentNoticeId == null || currentNoticeId == "") {
		currentNoticeId = pageObj.noticeList[0].id;
	}
	var currentLobId = "";
	var currentPageCount = "0";
	for (var i = 0; i < pageObj.noticeList.length; i++) {
		if (pageObj.noticeList[i].id == currentNoticeId) {
			currentLobId = pageObj.noticeList[i].lob_id;
			currentPageCount = pageObj.noticeList[i].page_count;
			break;
		}
	}
	var params = {
		SwfFile : encodeURI(approot + "/swf/notice/" + currentNoticeId + "/{Paper[*,0].swf," + currentPageCount + "}"),
		EncodeURI : true,
		Scale : 1.0
	}
	if (pageObj.lastNoticeId != currentNoticeId) {
		swfobject.embedSWF("flash/paperView.swf", "notice", "1023", "600", "9.0.0", "flash/expressInstall.swf", params);
		pageObj.lastNoticeId = currentNoticeId;
	}
}


//---------------------------------
//加载其它公告内容
//---------------------------------
pageObj.loadTargetOtherNotice = function(noticeId) {
	var currentNoticeId = noticeId;
	if (pageObj.othernoticeList == null) {// 未查询公告则先查询
		pageObj.queryTargetOtherNoticeData();
	}
	if (pageObj.othernoticeList.length == 0) {// 无公告
		return;
	}
	if (currentNoticeId == undefined || currentNoticeId == null || currentNoticeId == "") {
		currentNoticeId = pageObj.othernoticeList[0].id;
	}
	var currentLobId = "";
	var currentPageCount = "0";
	for (var i = 0; i < pageObj.othernoticeList.length; i++) {
		if (pageObj.othernoticeList[i].id == currentNoticeId) {
			currentLobId = pageObj.othernoticeList[i].lob_id;
			currentPageCount = pageObj.othernoticeList[i].page_count;
			break;
		}
	}
	var params = {
		SwfFile : encodeURI(approot + "/swf/notice/" + currentNoticeId + "/{Paper[*,0].swf," + currentPageCount + "}"),
		EncodeURI : true,
		Scale : 1.0
	}
	if (pageObj.lastNoticeId != currentNoticeId) {
		swfobject.embedSWF(approot + "/base/flash/paperView.swf?v=1.1d6", "othernotice", "968", "600", "9.0.0", approot
						+ "/base/flash/PaperView.swf/expressInstall.swf", params);
		pageObj.lastNoticeId = currentNoticeId;
	}
}


// ---------------------------------
// 设置表格样式
// ---------------------------------
pageObj.setGridClass = function() {
	$('.i-grid-panel-default tbody tr:odd').addClass('odd');
	$('.i-grid-panel-default tbody tr:even').addClass('even');
	$('.i-grid-panel-default tbody tr').bind({
				mouseover : function() {
					$(this).addClass('curr');
				},
				mouseout : function() {
					$(this).removeClass('curr');
				}
			});
}

// ---------------------------------
// 查询相关下载内容
// ---------------------------------
pageObj.queryTargetAttachData = function() {
	if (!pageObj.targetAttachLoaded) {
		var cmd = new Command();
		cmd.module = "tradeland";
		cmd.service = "ViewTarget";
		cmd.method = "getTargetAttachData";
		cmd.id = pageObj.targetId;
		cmd.notice_templet_no = "businessType" + pageObj.businessType + "NoticeAttach";
		cmd.target_templet_no = "businessType" + pageObj.businessType + "TargetAttach";
		cmd.success = function(data) {
			if (data.state == 1) {
				var attachBody = "";
				if (data.rs.length == 0) {
					attachBody += "<div style='width:100%;text-align:center;padding-top:50px;font-weight:700'>无相关资料下载</div>";
				} else {
					attachBody += "<table width='100%' align='center' class='i-grid-panel-default'>"
							+ "<thead><tr><td class='tc'>序号</td><td class='t2'>标题</td><td class='tc'>操作</td></tr></thead>";
					for (var i = 0; i < data.rs.length; i++) {
						var name = Utils.getRecordValue(data, i, 'name');
						var nt=Utils.getRecordValue(data, i, 'nt');
						var nname=Utils.getRecordValue(data, i, 'nname');
						if(nt==0){
							name='普通'+name+"："+nname;
						}else if(nt==1){
							name='补充'+name+"："+nname;
						}
						var lobIds = Utils.getRecordValue(data, i, 'lob_ids');
						attachBody += "<tr><td width='60' height='26' class='tc'>" + (i + 1)
								+ "</td><td width='700' class='t2'>";
						if (lobIds) {
							var downloadUrl = approot + "/download?module=base&service=BaseDownload&method=";
							if (lobIds.indexOf(",") != -1) {
								downloadUrl += "downloadZipFile&lobId=" + lobIds + "&isZip=true&zipFileName=" + name;
							} else {
								downloadUrl += "downloadFile&lobId=" + lobIds;
							}
							attachBody += "<a href='" + downloadUrl + "'>" + name + "</a></td><td class='tc'>"
									+ "<a href='" + downloadUrl + "'>下载</a>";
						} else
							attachBody += name + "&nbsp;&nbsp;<span style='color:gray'>(暂无)</span></td><td>";
						attachBody += "</td></tr>";
					}
					attachBody += "</table>";
				}
				$("#subC").html(attachBody);
				pageObj.setGridClass();
			} else {
				DialogError('查询标的相关下载信息失败,错误原因：' + data.message);
				return false;
			}
		};
		cmd.execute();
		pageObj.targetAttachLoaded = true;
	}
}

// ---------------------------------
// 查询交易物附件内容（宗地图、位置图）
// ---------------------------------
pageObj.goods_templet_no_array = {};
pageObj.goods_templet_no_array['101001'] = 'businessType101001GoodsAttach';
pageObj.goods_templet_no_array['101002'] = 'businessType101001GoodsAttach';
pageObj.goods_templet_no_array['201003'] = 'businessType201003GoodsAttach';
pageObj.goods_templet_no_array['201004'] = 'businessType201004GoodsAttach';
pageObj.goods_templet_no_array['301001'] = 'businessType301001GoodsAttach';
pageObj.goods_templet_no_array['301002'] = 'businessType301001GoodsAttach';
pageObj.goods_templet_no_array['401001'] = 'businessType401001GoodsAttach';
pageObj.goods_templet_no_array['401002'] = 'businessType401001GoodsAttach';
pageObj.queryGoodsAttachData = function() {
	var cmd = new Command();
	cmd.module = "tradeland";
	cmd.service = "ViewTarget";
	cmd.method = "getGoodsAttachData";
	cmd.target_id = pageObj.targetId;
	cmd.templet_no = pageObj.goods_templet_no_array[pageObj.businessType];
	cmd.success = function(data) {
		// if (pageObj.goodsImages == null)
		// pageObj.goodsImages = new Array();
		// pageObj.goodsImages.splice(0, pageObj.goodsImages.length);
		pageObj.goodsImages = [];
		if (data.state == 1) {
			if (data.rs.length > 0) {
				for (var i = 0; i < data.rs.length; i++) {
					var v = Utils.getRecordValue(data, i, 'lob_id');
					if (v)
						pageObj.goodsImages.push(v);
				}
			}
		} else {
			DialogError('查询交易物附件内容失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

// ---------------------------------
// 查询交易物图片(宗地图)等
// ---------------------------------
pageObj.loadTargetImages = function() {
	if (!pageObj.targetImagesLoaded) {
		pageObj.queryGoodsAttachData();
		var imgRoot = encodeURIComponent(approot
				+ "/download?module=base&service=BaseDownload&method=downloadFile&lobId=");
		var imgs = pageObj.goodsImages.join(",");
		var url = approot + "/mine/trade/viewImages.html?imgUrlRoot=" + imgRoot + "&imgUrl=" + imgs;
		$("#subB").html("<iframe width='100%' height='600px' frameborder='0' id='targetImages'" + " src='" + url
				+ "'></iframe>");
		pageObj.targetImagesLoaded = true;
	}
}

// ---------------------------------
// 查询竞买记录
// ---------------------------------
pageObj.loadTargetOfferLog = function() {
	if (!pageObj.targetOfferLogLoaded) {
		$("#subD").html("<iframe width='100%' height='710px' frameborder='0' id='offerLog' src='" + approot
				+ "/mine/trade/offerLogList.html?hideQueryCondition=no&targetId=" + pageObj.targetId + "&gridWidth=965&gridHeight=700'></iframe>");
		pageObj.targetOfferLogLoaded = true;
	}
}

// ---------------------------------
// 控制公告导航标签
// ---------------------------------
pageObj.controlNoticeTab = function() {
	if (pageObj.noticePanalVisible == 1) {
		$('#noticeList').hide();
		$('.o-c-tab').css("background-position", "0 -480px");
		pageObj.noticePanalVisible = 0;
	} else {
		$('#noticeList').show();
		$('.o-c-tab').css("background-position", "0 -465px");
		pageObj.noticePanalVisible = 1;
	}
}

// ---------------------------------
// 收藏标的
// ---------------------------------
pageObj.addFavorites = function() {
	var cmd = new Command();
	cmd.module = "bidder";
	cmd.service = "TargetFavorites";
	cmd.method = "addTarget";
	cmd.target_id = pageObj.targetId;
	cmd.success = function(data) {
		if (data.state == 1) {
			alert('收藏当前标的成功');
		} else {
			DialogError('收藏当前标的失败,错误原因：' + data.message);
			return false;
		}
	};
	cmd.execute();
}

pageObj.divDis = function(tab){
	if(tab==0){
		$("#subA").show();
		$("#subB").hide();
		$("#subC").hide();
		$("#subD").hide();
		$("#subE").hide();
		$("#subF").hide();
		
		$("#tabNotice").attr('class', 'l-selected');
		$("#tabImg").attr('class', '');
		$("#tabDown").attr('class', '');
		$("#tabOfferLog").attr('class', '');
		$("#tabTransNotice").attr('class', '');
		$("#tabOtherNotice").attr('class', '');
	}else if(tab==1){
		$("#subA").hide();
		$("#subB").show();
		$("#subC").hide();
		$("#subD").hide();
		$("#subE").hide();
		$("#subF").hide();
		
		$("#tabNotice").attr('class', '');
		$("#tabImg").attr('class', 'l-selected');
		$("#tabDown").attr('class', '');
		$("#tabOfferLog").attr('class', '');
		$("#tabTransNotice").attr('class', '');
		$("#tabOtherNotice").attr('class', '');
	}else if(tab==2){
		$("#subA").hide();
		$("#subB").hide();
		$("#subC").show();
		$("#subD").hide();
		$("#subE").hide();
		$("#subF").hide();
		
		$("#tabNotice").attr('class', '');
		$("#tabImg").attr('class', '');
		$("#tabDown").attr('class', 'l-selected');
		$("#tabOfferLog").attr('class', '');
		$("#tabTransNotice").attr('class', '');
		$("#tabOtherNotice").attr('class', '');
	}else if(tab==3){
		$("#subA").hide();
		$("#subB").hide();
		$("#subC").hide();
		$("#subD").show();
		$("#subE").hide();
		$("#subF").hide();

		$("#tabNotice").attr('class', '');
		$("#tabImg").attr('class', '');
		$("#tabDown").attr('class', '');
		$("#tabOfferLog").attr('class', 'l-selected');
		$("#tabTransNotice").attr('class', '');
		$("#tabOtherNotice").attr('class', '');
	}else if(tab==4){
		$("#subA").hide();
		$("#subB").hide();
		$("#subC").hide();
		$("#subD").hide();
		$("#subE").show();
		$("#subF").hide();
		
		$("#tabNotice").attr('class', '');
		$("#tabImg").attr('class', '');
		$("#tabDown").attr('class', '');
		$("#tabOfferLog").attr('class', '');
		$("#tabTransNotice").attr('class', 'l-selected');
		$("#tabOtherNotice").attr('class', '');
	}else if(tab==5){
		$("#subA").hide();
		$("#subB").hide();
		$("#subC").hide();
		$("#subD").hide();
		$("#subE").hide();
		$("#subF").show();
		
		$("#tabNotice").attr('class', '');
		$("#tabImg").attr('class', '');
		$("#tabDown").attr('class', '');
		$("#tabOfferLog").attr('class', '');
		$("#tabTransNotice").attr('class', '');
		$("#tabOtherNotice").attr('class', 'l-selected');
	}
}

// ---------------------------------
// 初始化页面
// ---------------------------------
pageObj.initHtml = function() {
	pageObj.divDis(0);
	pageObj.moduleId = Utils.getPageValue("moduleId");
	pageObj.userId = getUserId();
	pageObj.targetId = Utils.getPageValue("targetId");
	pageObj.queryTargetData();
	pageObj.tabId = Utils.getPageValue("tabId");
	if(pageObj.tabId!=null && typeof(pageObj.tabId)!="undefined"){
		if(pageObj.tabId==3 || pageObj.tabId==4){
			pageObj.changedTab('i-bidInfoMain', $("#tabNotice")[0], 'subA');
		}else if(pageObj.tabId==-1){//补充公告
			pageObj.changedTab('i-bidInfoMain', $("#tabOtherNotice")[0], 'subF');
		}else if(pageObj.tabId==7 || pageObj.tabId==6 || pageObj.tabId==5){
			pageObj.queryTargetNoticeData();
			pageObj.changedTab('i-bidInfoMain', $("#tabTransNotice")[0], 'subE');
			pageObj.setGridClass();
		}
	}else{
		if (pageObj.targetExists) {
			if (pageObj.targetStatus == 5 || pageObj.targetStatus == 6 || pageObj.targetStatus == 7 ) {// 交易结束打开成交公示TAB
				pageObj.changedTab('i-bidInfoMain', $("#tabTransNotice")[0], 'subE');
				pageObj.setGridClass();
			} else {
				pageObj.changedTab('i-bidInfoMain', $("#tabNotice")[0], 'subA')
			}
		} else {
			$('#targetBody').html("无标的");
		}
	}
	$("#goodsFrame").attr("src", "viewGoods.html?targetId=" + pageObj.targetId+"&u="+pageObj.userId);
	//根据不同类型改变TAB页显示的文字
	if(pageObj.businessType=='301001'||pageObj.businessType=='401001'){
		$("#tabNotice").html("<a>出让公告</a><div class='l-tab-links-item-left'></div><div class='l-tab-links-item-right'></div>");
	}else if(pageObj.businessType=='301002'||pageObj.businessType=='401002'){
		$("#tabNotice").html("<a>转让公告</a><div class='l-tab-links-item-left'></div><div class='l-tab-links-item-right'></div>");
	}
	if(pageObj.transType==0){
		if(pageObj.businessType=='301001'||pageObj.businessType=='401001'){
			$("#tabDown").html("<a>挂牌出让文件</a><div class='l-tab-links-item-left'></div><div class='l-tab-links-item-right'></div>");
		}else if(pageObj.businessType=='301002'||pageObj.businessType=='401002'){
			$("#tabDown").html("<a>挂牌转让文件</a><div class='l-tab-links-item-left'></div><div class='l-tab-links-item-right'></div>");
		}
	}else if(pageObj.transType==1){
		if(pageObj.businessType=='301001'||pageObj.businessType=='401001'){
			$("#tabDown").html("<a>拍卖出让文件</a><div class='l-tab-links-item-left'></div><div class='l-tab-links-item-right'></div>");
		}else if(pageObj.businessType=='301002'||pageObj.businessType=='401002'){
			$("#tabDown").html("<a>拍卖转让文件</a><div class='l-tab-links-item-left'></div><div class='l-tab-links-item-right'></div>");
		}
	}
}

$(document).ready(pageObj.initHtml);