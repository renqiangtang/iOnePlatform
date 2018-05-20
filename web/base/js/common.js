var comObj = {};
// 交易物类别 字典表trans_goods_type
comObj.goods_type = {};
comObj.goods_type['000'] = '所有';
comObj.goods_type['101'] = '土地';
comObj.goods_type['201'] = '房产';
comObj.goods_type['301'] = '探矿权';
comObj.goods_type['401'] = '采矿权';
comObj.goods_type['501'] = '耕地指标';
// 交易物路径
comObj.urlPath = {};
comObj.urlPath['101'] = 'land';
comObj.urlPath['301'] = 'mine';
comObj.urlPath['501'] = 'plow';
// 价格修改状态 :1 待审核 ， 2 审核通过（并写入业务表），3审核不通过
comObj.priceStatus = {};
comObj.priceStatus['1'] = '待审核';
comObj.priceStatus['2'] = '审核已通过';
comObj.priceStatus['3'] = '审核不通过';
// 业务类别 字典表trans_business_type
comObj.business_type = {};
comObj.business_type['000'] = '所有';
comObj.business_type['001'] = '出让';
comObj.business_type['002'] = '转让';
comObj.business_type['003'] = '拍卖';
comObj.business_type['004'] = '挂牌';
comObj.business_type['005'] = '出售';
comObj.business_type['006'] = '购买';
// 交易物的交易类别 字典表 trans_business_type_rel
comObj.business_type_rel = {};
comObj.business_type_rel['101001'] = '土地出让';
comObj.business_type_rel['101002'] = '土地转让';
comObj.business_type_rel['301001'] = '探矿权出让';
comObj.business_type_rel['301002'] = '探矿权转让';
comObj.business_type_rel['401001'] = '采矿权出让';
comObj.business_type_rel['401002'] = '采矿权转让';
comObj.business_type_rel['501005'] = '耕地指标出售';
comObj.business_type_rel['501006'] = '耕地指标购买';
// 标的状态
comObj.targetStatusObj = {};
//comObj.targetStatusObj['0'] = '受理中(录件)';
//comObj.targetStatusObj['1'] = '审核中';
//comObj.targetStatusObj['2'] = '已审核';
//comObj.targetStatusObj['3'] = '已公告';
//comObj.targetStatusObj['4'] = '交易中';
//comObj.targetStatusObj['5'] = '已成交';
//comObj.targetStatusObj['6'] = '未成交';
//comObj.targetStatusObj['7'] = '竞得后审核';
//comObj.targetStatusObj['8'] = '成交资格审核不通过';
//comObj.targetStatusObj['9'] = '资格审核通过';


comObj.targetStatusObj['0'] = '受理中(录件)';
comObj.targetStatusObj['1'] = '审核中';
comObj.targetStatusObj['2'] = '已审核';
comObj.targetStatusObj['3'] = '公告期';
comObj.targetStatusObj['4'] = '挂牌期';
comObj.targetStatusObj['5'] = '已成交';
comObj.targetStatusObj['6'] = '流拍';
comObj.targetStatusObj['7'] = '竞得后审核';
comObj.targetStatusObj['8'] = '成交资格审核不通过';
comObj.targetStatusObj['9'] = '资格审核通过';

comObj.plowTargetStatusObj = {};
comObj.plowTargetStatusObj['0'] = '受理中(录件)';
comObj.plowTargetStatusObj['1'] = '审核中';
comObj.plowTargetStatusObj['2'] = '已审核';
comObj.plowTargetStatusObj['3'] = '已公告';
comObj.plowTargetStatusObj['4'] = '交易中';
comObj.plowTargetStatusObj['5'] = '竞得待审核';
comObj.plowTargetStatusObj['6'] = '未成交';
comObj.plowTargetStatusObj['8'] = '未成交';
comObj.plowTargetStatusObj['7'] = '已成交';
comObj.plowTargetStatusObj['9'] = '已签订合同';

comObj.plowTargetStatusObjSel = {};
comObj.plowTargetStatusObjSel['5'] = '竞得待审核';
comObj.plowTargetStatusObjSel['6,8'] = '未成交';
comObj.plowTargetStatusObjSel['7'] = '已成交';
comObj.plowTargetStatusObjSel['9'] = '已签订合同';

// --------------------------------------------
// 竞买人状态：0申请中，1已激活(此状态为正常可用状态)，2冻结，3待年审，4注销
// --------------------------------------------
comObj.bidderStatus = {};
comObj.bidderStatus[0] = '申请中';
comObj.bidderStatus[1] = '正常';
comObj.bidderStatus[2] = '冻结';
comObj.bidderStatus[3] = '待年审';
comObj.bidderStatus[4] = '注销';
// 用途
comObj.use_type = {};
comObj.use_type["批发零售用地"] = "批发零售用地";
comObj.use_type["住宿餐饮用地"] = "住宿餐饮用地";
comObj.use_type["商务金融用地"] = "商务金融用地";
comObj.use_type["其他商服用地"] = "其他商服用地";
comObj.use_type["工业用地"] = "工业用地";
comObj.use_type["采矿用地"] = "采矿用地";
comObj.use_type["仓储用地"] = "仓储用地";
comObj.use_type["其他普通商品住房用地"] = "其他普通商品住房用地";
comObj.use_type["公共租赁住房用地"] = "公共租赁住房用地";
comObj.use_type["新闻出版用地"] = "新闻出版用地";
comObj.use_type["教科用地"] = "教科用地";
comObj.use_type["医卫慈善用地"] = "医卫慈善用地";
comObj.use_type["文体娱乐用地"] = "文体娱乐用地";
comObj.use_type["公共设施用地"] = "公共设施用地";
comObj.use_type["公园与绿地"] = "公园与绿地";
comObj.use_type["街巷用地"] = "街巷用地";
comObj.use_type["商住混合用地"] = "商住混合用地";
comObj.use_type["酒店用地"] = "酒店用地";
comObj.use_type["商业"] = "商业";
comObj.use_type["居住用地"] = "居住用地";
comObj.use_type["居住用地(宿舍)"] = "居住用地(宿舍)";
comObj.use_type["加油站用地"] = "加油站用地";
comObj.use_type["地下车库"] = "地下车库";
comObj.use_type["商业性办公"] = "商业性办公";
comObj.use_type["酒店、商业、办公"] = "酒店、商业、办公";
comObj.use_type["商业服务业设施用"] = "商业服务业设施用";
comObj.use_type["幼儿园用地"] = "幼儿园用地";
comObj.use_type["自用停车场"] = "自用停车场";
comObj.use_type["旅馆业用地"] = "旅馆业用地";
comObj.use_type["商业办公"] = "商业办公";
comObj.use_type["居住、商业、服务业"] = "居住、商业、服务业";
comObj.use_type["居住 商业性办公"] = "居住 商业性办公";
comObj.use_type["政策性住房用地"] = "政策性住房用地";
comObj.use_type["建筑用花岗岩"] = "建筑用花岗岩";
comObj.use_type["机场用地"] = "机场用地";
comObj.use_type["仓储用地"] = "仓储用地";
comObj.use_type["工业配套宿舍用地"] = "工业配套宿舍用地";
comObj.use_type["货运交通设施用地"] = "货运交通设施用地";
comObj.use_type["堆放场"] = "堆放场";

comObj.use_type_years = {};
comObj.use_type_years["批发零售用地"] = "10";
comObj.use_type_years["住宿餐饮用地"] = "10";
comObj.use_type_years["商务金融用地"] = "10";
comObj.use_type_years["其他商服用地"] = "10";
comObj.use_type_years["工业用地"] = "10";
comObj.use_type_years["采矿用地"] = "10";
comObj.use_type_years["仓储用地"] = "10";
comObj.use_type_years["其他普通商品住房用地"] = "10";
comObj.use_type_years["公共租赁住房用地"] = "10";
comObj.use_type_years["新闻出版用地"] = "10";
comObj.use_type_years["教科用地"] = "10";
comObj.use_type_years["医卫慈善用地"] = "10";
comObj.use_type_years["文体娱乐用地"] = "10";
comObj.use_type_years["公共设施用地"] = "10";
comObj.use_type_years["公园与绿地"] = "10";
comObj.use_type_years["街巷用地"] = "10";
comObj.use_type_years["商住混合用地"] = "10";
comObj.use_type_years["酒店用地"] = "10";
comObj.use_type_years["商业"] = "10";
comObj.use_type_years["居住用地"] = "10";
comObj.use_type_years["居住用地(宿舍)"] = "10";
comObj.use_type_years["加油站用地"] = "10";
comObj.use_type_years["地下车库"] = "10";
comObj.use_type_years["商业性办公"] = "10";
comObj.use_type_years["酒店、商业、办公"] = "10";
comObj.use_type_years["商业服务业设施用"] = "10";
comObj.use_type_years["幼儿园用地"] = "10";
comObj.use_type_years["自用停车场"] = "10";
comObj.use_type_years["旅馆业用地"] = "10";
comObj.use_type_years["商业办公"] = "10";
comObj.use_type_years["居住、商业、服务业"] = "10";
comObj.use_type_years["居住 商业性办公"] = "10";
comObj.use_type_years["政策性住房用地"] = "10";
comObj.use_type_years["建筑用花岗岩"] = "10";
comObj.use_type_years["机场用地"] = "10";
comObj.use_type_years["仓储用地"] = "10";
comObj.use_type_years["工业配套宿舍用地"] = "10";
comObj.use_type_years["货运交通设施用地"] = "10";
comObj.use_type_years["堆放场"] = "10";

comObj.cantonName = {};
comObj.cantonName['441601']='市辖区';
comObj.cantonName['441625']='东源县';
comObj.cantonName['441623']='连平县';
comObj.cantonName['441621']='紫金县';
comObj.cantonName['441622']='龙川县';
comObj.cantonName['441624']='和平县';
comObj.mode = {};
comObj.mode['new'] = "new";// 新建
comObj.mode['modify'] = "modify";// 修改
comObj.mode['update'] = "update";// 更新
comObj.mode['view'] = "view";// 查看
comObj.Currency = {};
comObj.Currency['CNY'] = '人民币';
//comObj.Currency['RMB'] = '人民币';
comObj.Currency['HKD'] = '港元';
comObj.Currency['USD'] = '美元';
//comObj.Currency['EUR'] = '欧元';
//comObj.Currency['JPY'] = '日元';
//comObj.Currency['GBP'] = '英镑';
//comObj.Currency['CHF'] = '瑞郎';
//comObj.Currency['CAD'] = '加元';
//comObj.Currency['SGD'] = '新加坡元';
//comObj.Currency['NZD'] = '新西兰元';
comObj.CurrencyUnit = {};
comObj.CurrencyUnit['CNY'] = '万元';
//comObj.CurrencyUnit['RMB'] = '万元';
comObj.CurrencyUnit['HKD'] = '港元';
comObj.CurrencyUnit['USD'] = '美元';
//comObj.CurrencyUnit['EUR'] = '欧元';
//comObj.CurrencyUnit['JPY'] = '日元';
//comObj.CurrencyUnit['GBP'] = '英镑';
//comObj.CurrencyUnit['CHF'] = '瑞郎';
//comObj.CurrencyUnit['CAD'] = '加元';
//comObj.CurrencyUnit['SGD'] = '新加坡元';
//comObj.CurrencyUnit['NZD'] = '新西兰元';

comObj.licenseStatusObj = {};
comObj.licenseStatusObj['0'] = '申请中';
comObj.licenseStatusObj['1'] = '已审核并且交足保证金';
comObj.licenseStatusObj['2'] = '未成交';
comObj.licenseStatusObj['3'] = '竞价成交';
comObj.licenseStatusObj['4'] = '确认成交';
comObj.licenseStatusObj['5'] = '撤销成交';

//多指标单位
comObj.quotasUnit=[];
comObj.quotasUnit.push({
	'id' : '平方米',
	'name' : '平方米'
});
comObj.quotasUnit.push({
	'id' : '元/平方米',
	'name' : '元/平方米'
});
comObj.quotasUnit.push({
	'id' : '万元',
	'name' : '万元'
});




comObj.CurrencyList = [];
for ( var pro in comObj.Currency) {
	var obj = {};
	obj.enName = pro;
	obj.cnName = comObj.Currency[pro];
	comObj.CurrencyList.push(obj);
}
comObj.getGoodsUrlPath = function() {
	var goodsType = Utils.getPageValue('goodsType');
	return comObj.urlPath[goodsType];
}

// --------------------------------------------
// 选择区域
// --------------------------------------------
comObj.cantonSelect = function() {
	var obj = {};
	obj.url = approot + '/mine/before/cantonSelect.html';
	obj.feature = "dialogWidth=300px;dialogHeight=450px";
	var valueObj = DialogModal(obj);
	if (typeof (valueObj) != "undefined") {// 修改了此处的判断，如果写成valueObj!="undefined",在IE9下会报错
		if (arguments.length > 0) {
			$('#' + arguments[0]).val(valueObj.id);
			if (arguments.length > 1)
				$('#' + arguments[1]).val(valueObj.fullName);
			if (arguments.length > 2)
				$('#' + arguments[2]).val(valueObj.name);
		}
		$('#canton').val(valueObj.name);
		$('#cantonFullName').val(valueObj.fullName);
		$('#canton_id').val(valueObj.id);
		comObj.cantonId = valueObj.id;
	}
	return valueObj;
}

comObj.plowCantonSelect = function() {
	var obj = {};
	obj.url = approot + '/plow/portal/cantonSelect.html';
	obj.feature = "dialogWidth=300px;dialogHeight=450px";
	var valueObj = DialogModal(obj);
	if (typeof (valueObj) != "undefined") {
		if (arguments.length > 0) {
			$('#' + arguments[0]).val(valueObj.id);
			if (arguments.length > 1)
				$('#' + arguments[1]).val(valueObj.fullName);
			if (arguments.length > 2)
				$('#' + arguments[2]).val(valueObj.name);
		}
		$('#canton').val(valueObj.name);
		$('#cantonFullName').val(valueObj.fullName);
		$('#canton_id').val(valueObj.id);
		comObj.cantonId = valueObj.id;
	}
	return valueObj;
}

// --------------------------------------------
// 格式化金额信息 unit, flag, amount
// --------------------------------------------
comObj.cf = function(obj) {
	var amount = obj.amount;
	if (!amount)
		return '';
	var flag = '';
	if (obj.flag)
		flag = obj.flag;
	else
		flag = 'CNY';
	//
	if (flag == 'CNY' && obj.unit == '万元') {
		var m = amount / 10000;
		amount = parseFloat(m.toFixed(4));
	}
	//
	var unit = '';
	if (flag == 'CNY')
		unit = obj.unit;
	else
		unit = comObj.CurrencyUnit[flag];
	//
	return comObj.Currency[flag] + ":" + amount.addComma() + unit;
}

// 公告类别
comObj.notice_type = {};
comObj.notice_type['0'] = '普通公告';
comObj.notice_type['1'] = '补充公告';
comObj.notice_type['2'] = '紧急公告';
// 公告状态
comObj.notice_status = {};
comObj.notice_status['0'] = '未发布';
// comObj.notice_status['1'] = '已审核';
comObj.notice_status['2'] = '已发布';
comObj.notice_status['3'] = '审核中';
comObj.notice_status['4'] = '审核不通过';
comObj.notice_status['5'] = '审核通过';
comObj.target_reject = {};
comObj.target_reject['1'] = '有标的互斥';
comObj.target_reject['0'] = '无标的互斥';
// 宗地编号
comObj.target_name = {};
comObj.target_name['101'] = '宗地编号';
comObj.target_name['201'] = '房产名称';
comObj.target_name['301'] = '矿区名称';
comObj.target_name['401'] = '矿区名称';
comObj.goods_name = {};
comObj.goods_name['101'] = '宗地编号';
comObj.goods_name['301'] = '矿区';
comObj.goods_name['401'] = '矿区';
comObj.goodsUser = {};
comObj.goodsUser['101'] = '用途';
comObj.goodsUser['301'] = '矿种';
comObj.goodsUser['401'] = '矿种';
comObj.rbuserType = {};
comObj.rbuserType['100'] = '白名单';
comObj.rbuserType['200'] = '黑名单';
comObj.opt = [];
comObj.opt.push({
	'id' : 1,
	'name' : '是'
});
comObj.opt.push({
	'id' : 0,
	'name' : '否'
});
// --------------------------------------------
// 交易信息
// --------------------------------------------
comObj.getTransOrganByTargetId = function(targetId) {
	var cmd = new Command("bidder", "Apply", "getTransOrganByTargetId");
	cmd.targetId = targetId;
	cmd.success = function(data) {
		pageObj.transOrganInfo = {};
		pageObj.transOrganInfo['organId'] = data.organId;
		pageObj.transOrganInfo['organName'] = data.transOrganName;
		pageObj.transOrganInfo['cantonId'] = data.cantonId;
		pageObj.transOrganInfo['cantonName'] = data.cantonName;
		pageObj.transOrganInfo['businessType'] = data.businessName;
		pageObj.transOrganInfo['goodsType'] = data.goodsTypeName;
		pageObj.transOrganInfo['transTypeName'] = data.transTypeName;

	}
	cmd.execute();
}

comObj.getPlowTargetStatus = function(status) {

	return comObj.plowTargetStatusObj[status];
}
// --------------------------------------------
// 交易信息
// --------------------------------------------
comObj.getTransOrganByLicenseId = function(licenseId) {
	var cmd = new Command("bidder", "Apply", "getTransOrganByLicenseId");
	cmd.licenseId = licenseId;
	cmd.success = function(data) {
		pageObj.transOrganInfo = {};
		pageObj.transOrganInfo['organId'] = data.organId;
		pageObj.transOrganInfo['organName'] = data.transOrganName;
		pageObj.transOrganInfo['cantonId'] = data.cantonId;
		pageObj.transOrganInfo['cantonName'] = data.cantonName;
		pageObj.transOrganInfo['noticeName'] = data.noticeName;
		pageObj.transOrganInfo['businessType'] = data.businessName;
		pageObj.transOrganInfo['goodsType'] = data.goodsTypeName;
		pageObj.transOrganInfo['transTypeName'] = data.transTypeName;
	}
	cmd.execute();
}
// --------------------------------------------
// 获取配置文档信息
// --------------------------------------------
comObj.getConfigDoc = function(htmlName, param) {
	var url = 'config/' + pageObj.transOrganInfo['organName'] + "/" + pageObj.transOrganInfo['goodsType'] + "/" + pageObj.transOrganInfo['transTypeName'] + "/" + pageObj.transOrganInfo['businessType'] + "/";
	url += htmlName + ".html?r=true";
	for ( var pro in pageObj.transOrganInfo) {
		var v = pageObj.transOrganInfo[pro];
		url += '&' + pro + '=' + v;
	}
	if (param)
		url += '&' + param;
	return url;
}

comObj.convertDateToCh = function (date){
	if(date.length==10){
		return date.replace("-","年").replace("-","月")+"日";
	}else{
		return date;
	}
}

comObj.getGzConfigDoc = function(htmlName, param) {
	var url = "耕指/";
	url += htmlName + ".html?r=true";
	for ( var pro in pageObj.transOrganInfo) {
		var v = pageObj.transOrganInfo[pro];
		url += '&' + pro + '=' + v;
	}
	if (param)
		url += '&' + param;
	return url;
}
// -----------------------------
// 渲染交易物-宗地信息
// ----------------------------
comObj.goodsNoRender = function(row, rowNamber) {
	var html = '<a href="javascript:comObj.viewGoods(\'' + row.id + '\')">' + row.no + '</a>';
	return html;
}
comObj.getBusinessType = function(goodsType) {
	var arr = [];
	var cmd = new Command('bidder', 'PlowCommon', 'getBusinessTypeList');
	cmd.goodsType = goodsType;
	cmd.success = function(data) {
		arr = data.businessType;
	};
	cmd.execute();
	return arr;
}
// -----------------------------
// 查看交易物
// ----------------------------
comObj.viewGoods = function(goodsId,noticestatus,conFirm) {
	var obj = {};
	var linkpage = "";
	if (pageObj.goodsType == "301") {
		linkpage = "prospect301"
	} else if (pageObj.goodsType == "401") {
		linkpage = "mine301";
	} else {
		linkpage = pageObj.goodsType;
	}
	obj.url = linkpage + ".html";
	// 参数
	obj.param = {
		mode : 'modify',
		u : pageObj.u,
		goodsId : goodsId,
		goodsType : pageObj.goodsType,
		noticestatus : noticestatus,
		conFirm : conFirm
	};
	// 窗口参数
	obj.feature = "dialogWidth=800px;dialogHeight=600px";
	DialogModal(obj);
}

comObj.initBusinessTypeSelect = function(selectId, goodsType) {
	var html = '<option value="-1">--所有--</option>';
	var arr = comObj.getBusinessType(goodsType);
	comObj.businessTypeObj = {};
	for ( var i = 0; i < arr.length; i++) {
		html += '<option value="' + arr[i].id + '">' + arr[i].name + '</option>';
		comObj.businessTypeObj[arr[i].id] = arr[i].name;
	}
	$('#' + selectId).html(html);
}

comObj.initTargetStatus = function(selectId) {
	var html = '<option value="-1">--所有--</option>';
	for ( var pro in comObj.targetStatusObj) {
		var value = comObj.targetStatusObj[pro];
		html += '<option value="' + pro + '">' + value + '</option>';
	}
	$('#' + selectId).html(html);
}
comObj.initPlowTargetStatus = function(selectId) {
	var html = '<option value="-1">--所有--</option>';
	for ( var pro in comObj.plowTargetStatusObj) {
		var value = comObj.plowTargetStatusObj[pro];
		html += '<option value="' + pro + '">' + value + '</option>';
	}
	$('#' + selectId).html(html);
}
comObj.initPlowTargetStatusSel = function(selectId) {
	var html = '<option value="-1">--所有--</option>';
	for ( var pro in comObj.plowTargetStatusObjSel) {
		var value = comObj.plowTargetStatusObjSel[pro];
		html += '<option value="' + pro + '">' + value + '</option>';
	}
	$('#' + selectId).html(html);
}
comObj.attachObj = function(id, name, tableName, templetNo, title, category, grant) {
	var obj = {};
	obj.id = id;
	obj.name = name;
	obj.tableName = tableName;
	obj.templetNo = templetNo;
	obj.title = title;
	obj.category = category;
	if (grant)
		obj.grant = grant;
	return obj;
}

comObj.attachGrant = {
	add : 0,
	edit : 0,
	del : 0,
	addDtl : 0,
	editDtl : 0,
	delDtl : 0,
	uploadFile : 0,
	downloadFile : 2,
	delFile : 0
};

comObj.attach = function(title, owners) {
	var obj = {};
	obj.url = approot + '/sysman/attachList.html';
	obj.param = {};
	obj.param.u = getUserId();
	obj.param.title = title;
	obj.param.owners = encodeURIComponent(JSON.stringify(owners));
	obj.feature = "dialogWidth=800px;dialogHeight=500px";
	DialogModal(obj);
}

comObj.attach501501Template = comObj.attachObj('licenseShareFile501005', '样本文件', '', 'businessType501005Template', null, null, comObj.attachGrant);
comObj.attach501601Template = comObj.attachObj('licenseShareFile501006', '样本文件', '', 'businessType501006Template', null, null, comObj.attachGrant);

// ---------------------------------
// 设置或者读取按钮(span包裹的按钮)可用状态
// ---------------------------------
comObj.spanBtnDisabled = function(spanBtnId, disabled) {
	if (disabled == undefined)// 读取按钮可用状态
		return ($("#" + spanBtnId).find("input").attr("disabled"))
	else {// 设置按钮可用状态
		if (disabled) {
			$("#" + spanBtnId).find("input").attr('disabled', true);
			$("#" + spanBtnId).find("input").addClass("disable_btn");
		} else {
			$("#" + spanBtnId).find("input").attr('disabled', false);
			$("#" + spanBtnId).find("input").removeClass("disable_btn");
		}
	}
}

comObj.clickBody = function(e,refId,tabName){
		if($(e.target).attr("class")=="h_bk s_rb tr" || $(e.target).attr("class")=="s_b fb h_bk_url pl10" ){
			if($(e.target).html().trim()!="" && $(e.target).html().trim()!="&nbsp;"){
				var patt = new RegExp("<span class=\"wrong\"></span>");//要查找的字符串为''
				var patt1 = new RegExp("<span class=wrong></span>");//要查找的字符串为''
				var str = $(e.target).html().toLowerCase();
				alert
				if(pageObj.isrr==0){
					if(patt.test(str)==true){//字符串存在返回true否则返回false
						$(e.target).html($(e.target).html().toLowerCase().replace("<span class=\"wrong\"></span>",""));//与下面相比，上一句代码和下一句代码需要放反
						comObj.saveConfirmAttribute($(e.target).html().toLowerCase(),0,refId,tabName);
					}else if(patt1.test(str)==true){
						$(e.target).html($(e.target).html().toLowerCase().replace("<span class=wrong></span>",""));//与下面相比，上一句代码和下一句代码需要放反
						comObj.saveConfirmAttribute($(e.target).html().toLowerCase(),0,refId,tabName);
					}
				}else if(pageObj.isrr==1){
					if(patt.test(str)==false && patt1.test(str)==false){//字符串存在返回true否则返回false
						comObj.saveConfirmAttribute($(e.target).html().toLowerCase(),1,refId,tabName);
						if($(e.target).attr("class")=="h_bk s_rb tr"){
							$(e.target).html("<span class=\"wrong\"></span>"+$(e.target).html().toLowerCase());
						}else if($(e.target).attr("class")=="s_b fb h_bk_url pl10" ){
							$(e.target).html($(e.target).html().toLowerCase()+"<span class=\"wrong\"></span>");
						}
					}
				}
			}
		}
}

comObj.setConfirm = function(confirmData){
	$("td").each(function () {        
		if($(this).attr("class")=="h_bk s_rb tr" || $(this).attr("class")=="s_b fb h_bk_url pl10"){
			for(var key in confirmData){  
//				alert("this.html"+$(this).html()+":key"+key);
				if($(this).html().toLowerCase()==key.toLowerCase()){
					if(confirmData[key]==0){
						$(this).html($(this).html())
					}else if(confirmData[key]==1){
						if($(this).attr("class")=="h_bk s_rb tr"){
							$(this).html("<span class=\"wrong\"></span>"+$(this).html());
						}else if($(this).attr("class")=="s_b fb h_bk_url pl10" ){
							$(this).html($(this).html().toLowerCase()+"<span class=\"wrong\"></span>");
						}
					}
					
				}
			}
		}
	});
}

//---------------------------------
//保存打勾打叉 name:将打勾或打叉的html内容当作name, value：对or错
//---------------------------------
comObj.saveConfirmAttribute = function(name,value,refId,tabName){
	var cmd = new Command();
	cmd.module = "before";
	cmd.service = "TransNotice";
	cmd.method = "saveConfirmAttribute";
	cmd.name=name;
	cmd.value=value;
	cmd.tabName=tabName;
	cmd.refId=refId;
	cmd.success = function(data) {
		return data.state;
	}
	cmd.execute();
}


// 原有的tradeCommon.js内容

var pageTradeCommonObj = {};
// ---------------------------------
// 系统所有的交易类型
// 格式如：0=土地出让;1=土地转让;2=房产拍卖;3=房产挂牌;4=探矿权出让;5=探矿权转让;6=采矿权出让;7=采矿权转让
// ---------------------------------
pageTradeCommonObj.allBusinessType = "";
// ---------------------------------
// 系统所有的交易类型
// 格式如：0;1;2;3
// ---------------------------------
pageTradeCommonObj.businessType = "";
// ---------------------------------
// 系统所有的交易类型详细信息，格式参考template.txt
// 格式如：{0:{}}
// ---------------------------------
pageTradeCommonObj.allBusinessTypeFull = null;
// ---------------------------------
// 系统所有的交易方式，格式参考template.txt
// 格式如：{交易方式名称:{name: '', trans_type: 0, is_net_trans: 0, ...}}
// ---------------------------------
pageTradeCommonObj.allTransType = null;

// ---------------------------------
// 查询交易类型
// 返回系统所有交易类型和系统支持的交易类型
// ---------------------------------
pageTradeCommonObj.getBusinessType = function() {
	var userInfo = getUserInfoObj();
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Config";
	cmd.method = "getBusinessType";
	cmd.returnType = 5;
	cmd.userType = userInfo.userType;
	cmd.refId = userInfo.refId;
	cmd.organId = userInfo.organId;
	cmd.success = function(data) {
		if (data.state == "1") {
			var businessTypeData = data.businessType;
			pageTradeCommonObj.allBusinessType = businessTypeData.substr(0, businessTypeData.indexOf("|"));
			pageTradeCommonObj.businessType = businessTypeData.substr(businessTypeData.indexOf("|") + 1, businessTypeData.length);
		}
	};
	cmd.execute();
}

// ---------------------------------
// 查询交易类型
// 返回系统所有交易类型和系统支持的交易类型
// ---------------------------------
pageTradeCommonObj.getBusinessTypeFull = function() {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Config";
	cmd.method = "getBusinessType";
	cmd.returnType = 6;
	cmd.success = function(data) {
		if (data.state == "1") {
			var businessTypeData = data.businessType;
			pageTradeCommonObj.allBusinessTypeFull = eval('(' + businessTypeData + ')');
		}
	};
	cmd.execute();
}

// ---------------------------------
// 查询交易方式
// 返回系统所有的交易方式
// ---------------------------------
pageTradeCommonObj.getTransTypes = function() {
	var cmd = new Command();
	cmd.module = "sysman";
	cmd.service = "Config";
	cmd.method = "getTransTypes";
	cmd.success = function(data) {
		if (data.state == "1") {
			pageTradeCommonObj.allTransType = data.transType;
		}
	};
	cmd.execute();
}

// ---------------------------------
// 返回指定交易类别的交易方式列表，供查询条件下拉框使用
// businessTypeFull: 完整的业务规则对象，由getBusinessTypeFull返回的对象
// businessTypes: 需要返回的业务规则数组
// ---------------------------------
pageTradeCommonObj.getTransTypeList = function(businessTypeFull, businessTypes, distinctName) {
	if (!(businessTypes && $.isArray(businessTypes) && businessTypes.length > 0))
		return null;
	var allBusinessType = null;
	if (businessTypeFull)
		allBusinessType = businessTypeFull;
	else {
		if (!pageTradeCommonObj.allBusinessTypeFull)
			pageTradeCommonObj.getBusinessTypeFull();
		allBusinessType = pageTradeCommonObj.allBusinessTypeFull;
	}
	var result = {};
	var transTypeList = new Array();
	for ( var i = 0; i < businessTypes.length; i++) {
		if (businessTypes[i] && businessTypes[i] in allBusinessType) {
			var businessTypeConfig = allBusinessType[businessTypes[i]];
			for ( var key in businessTypeConfig) {
				var value = businessTypeConfig[key];
				if (typeof (value) == "object") {
					var transTypeObj = value;
					if (transTypeObj) {
						if (distinctName == undefined || distinctName == true) {
							if ($.inArray(transTypeObj.name, transTypeList) == -1) {
								transTypeList.push(transTypeObj.name);
								result[transTypeObj.trans_type_name] = transTypeObj.name;
							}
						} else
							result[transTypeObj.trans_type_name] = transTypeObj.name;
					}
				}
			}
		}
	}
	return result;
}

// ---------------------------------
// 获取通用的对话框参数
// ---------------------------------
pageTradeCommonObj.getDialogParam = function(param) {
	var dialogParam = {}
	dialogParam.paramType = 0;
	dialogParam.type = 0;
	dialogParam.width = 0;
	dialogParam.height = 0;
	dialogParam.title = "";
	dialogParam.url = "";
	if (typeof (param) == "string") {
		dialogParam.param = param;
	} else if (typeof (param) == "object" && param) {
		dialogParam.paramType = 1;
		for ( var p in param) {
			if (typeof (param[p]) != "function")
				dialogParam[p] = param[p];
		}
	}
	if (!dialogParam.width || dialogParam.width == 0 || dialogParam.width > screen.availWidth)
		dialogParam.width = screen.availWidth;
	dialogParam.left = (screen.availWidth - dialogParam.width) / 2;
	if (!dialogParam.height || dialogParam.height == 0)
		dialogParam.height = $(getMainFrame(window)).height();
	if (dialogParam.height > screen.availHeight)
		dialogParam.height = screen.availHeight;
	dialogParam.top = (screen.availHeight - dialogParam.height) / 2;
	return dialogParam;
}

// ---------------------------------
// 通用的对话框
// ---------------------------------
pageTradeCommonObj.openDialog = function(param) {
	var dialogParam = pageTradeCommonObj.getDialogParam(param);
	if (dialogParam.urlParams && (dialogParam.type == 1 || dialogParam.type == 2)) {
		if (typeof (dialogParam.urlParams) == "string") {
			if (dialogParam.urlParams in dialogParam)
				dialogParam.url = dialogParam.url.format(dialogParam[dialogParam.urlParams]);
			else
				dialogParam.url = dialogParam.url.format("");
		} else if (typeof (dialogParam.urlParams) == "object") {
			if (dialogParam.urlParams.length) {// 传入数组
				var userParamsExec = "dialogParam.url = dialogParam.url.format(";
				for ( var i = 0; i < dialogParam.urlParams.length; i++)
					userParamsExec += "'" + dialogParam.urlParams + "',";
				userParamsExec = userParamsExec.substr(0, userParamsExec.length - 1);
				userParamsExec += ")";
				eval(userParamsExec);
			} else {// 传入对象
				for ( var p in dialogParam.urlParams) {
					if (dialogParam.url.indexOf("?") == -1)
						dialogParam.url += "?1=1";
					if (typeof (dialogParam.urlParams[p]) != "function")
						dialogParam.url += "&" + p + "=" + dialogParam.urlParams[p];
				}
			}
		}
	}
	if (dialogParam.type == 1) {
		delete dialogParam.top;
		delete dialogParam.left;
		DialogOpen(dialogParam);
		// DialogOpen({
		// height: dialogParam.height,
		// width: dialogParam.width,
		// url: dialogParam.url,
		// title: dialogParam.title
		// });
	} else if (dialogParam.type == 2) {
		// 此方式慎用，无法获取框架的内容
		var viewWin = window.open(dialogParam.url, "", "");// location=no,status=no
		viewWin.moveTo(dialogParam.left, dialogParam.top);
		viewWin.resizeTo(dialogParam.width, dialogParam.height);
	} else {
		// dialogParam.url不要带参数
		// dialogParam.urlParams请传入json对象
		DialogModal({
			url : dialogParam.url,
			param : dialogParam.urlParams,
			feature : "location=no;status=no;dialogLeft=" + dialogParam.left + ";dialogTop=" + dialogParam.top + ";dialogWidth=" + dialogParam.width + "px;dialogHeight=" + dialogParam.height + "px"
		});
	}
}

// ---------------------------------
// 查看标的详情
// ---------------------------------
pageTradeCommonObj.viewTarget = function(targetParam) {
	var param = {};
	if (typeof (targetParam) == "string") {
		param.targetId = targetParam;
	} else if (typeof (targetParam) == "object" && targetParam) {
		param = targetParam;
	}
	if (!("width" in param))
		param.width = 1050;
	// param.type = 1;
	param.url = approot + "/" + comObj.getGoodsUrlPath() + "/trade/viewTarget.html";
	param.urlParams = {
		targetId : param.targetId
	};
	param.title = "标的详情";
	pageTradeCommonObj.openDialog(param);
}

// ---------------------------------
// 查看资格确认书
// ---------------------------------
pageTradeCommonObj.viewCert = function(licenseParam) {
	var param = {};
	if (typeof (licenseParam) == "string") {
		param.licenseId = licenseParam;
	} else if (typeof (licenseParam) == "object" && licenseParam) {
		param = licenseParam;
	}
	if (!("width" in param))
		param.width = 800;
	if (!("height" in param))
		param.height = 578;
	comObj.getTransOrganByLicenseId(param.licenseId);
	param.type = 1;
	param.url = comObj.getConfigDoc('资格确认书');
	param.urlParams = {
		licenseId : param.licenseId
	};
	param.buttons = [ {
		text : '关闭',
		onclick : function(item, dialog) {
			dialog.close();
		}
	} ];
	param.title = "查看竞价通知书";
	pageTradeCommonObj.openDialog(param);
	// window.open(param.url + "?licenseId=" + param.licenseId);
}

// ---------------------------------
// 查看成交确认书
// ---------------------------------
pageTradeCommonObj.viewTransCert = function(licenseParam) {
	var param = {};
	if (typeof (licenseParam) == "string") {
		param.licenseId = licenseParam;
	} else if (typeof (licenseParam) == "object" && licenseParam) {
		param = licenseParam;
	}
	if (!("width" in param))
		param.width = 800;
	if (!("height" in param))
		param.height = 750;
	param.type = 1;
	comObj.getTransOrganByLicenseId(param.licenseId);
	param.url = comObj.getConfigDoc('成交确认书');
	;
	param.urlParams = {
		licenseId : param.licenseId
	};
	param.buttons = [ {
		text : '关闭',
		onclick : function(item, dialog) {
			dialog.close();
		}
	} ];
	param.title = "成交确认书";
	pageTradeCommonObj.openDialog(param);
}

// ---------------------------------
// 查看成交通知书
// ---------------------------------
pageTradeCommonObj.viewTransCertNotice = function(licenseParam) {
	var tradeEndObject = {};
	tradeEndObject.transOrganInfo = {};
	var param = {};
	if (typeof (licenseParam) == "string") {
		param.licenseId = licenseParam;
	} else if (typeof (licenseParam) == "object" && licenseParam) {
		param = licenseParam;
	}
	if (!("width" in param))
		param.width = 800;
	if (!("height" in param))
		param.height = 750;
	param.type = 1;
	var cmd = new Command("bidder", "Apply", "getTransOrganByLicenseId");
	cmd.licenseId = licenseParam;
	cmd.success = function(data) {
		tradeEndObject.transOrganInfo['organId'] = data.organId;
		tradeEndObject.transOrganInfo['organName'] = data.transOrganName;
		tradeEndObject.transOrganInfo['cantonId'] = data.cantonId;
		tradeEndObject.transOrganInfo['cantonName'] = data.cantonName;
		tradeEndObject.transOrganInfo['noticeName'] = data.noticeName;
		tradeEndObject.transOrganInfo['businessType'] = data.businessName;
		tradeEndObject.transOrganInfo['goodsType'] = data.goodsTypeName;
		tradeEndObject.transOrganInfo['transTypeName'] = data.transTypeName;
	}
	cmd.execute();

	var htmlName = '网上竞得证明';
	var url = 'config/' + tradeEndObject.transOrganInfo['organName'] + "/" + tradeEndObject.transOrganInfo['goodsType'] + "/" + tradeEndObject.transOrganInfo['transTypeName'] + "/" + tradeEndObject.transOrganInfo['businessType'] + "/";
	url += htmlName + ".html?r=true";
	for ( var pro in tradeEndObject.transOrganInfo) {
		var v = tradeEndObject.transOrganInfo[pro];
		url += '&' + pro + '=' + v;
	}
	if (param)
		url += '&' + param;

	param.url = url;
	param.urlParams = {
		licenseId : param.licenseId
	};
	param.buttons = [ {
		text : '关闭',
		onclick : function(item, dialog) {
			dialog.close();
		}
	} ];
	param.title = "查看网上竞得证明";
	pageTradeCommonObj.openDialog(param);

}

// ---------------------------------
// 查看指定竞买人出价记录
// ---------------------------------
pageTradeCommonObj.viewBidderOfferLog = function(bidderId) {
	DialogOpen({
		height : 500,
		width : 700,
		url : approot + '/' + comObj.getGoodsUrlPath() + '/trade/offerLogList.html?bidderId=' + bidderId + '&gridWidth=680&gridHeight=385',
		title : '查看出价记录',
		buttons : [ {
			text : '关闭',
			onclick : function(item, dialog) {
				dialog.close();
			}
		} ]
	});
}

// ---------------------------------
// 查看指定标的出价记录
// ---------------------------------
pageTradeCommonObj.viewTargetOfferLog = function(targetId) {
	DialogOpen({
		height : 500,
		width : 700,
		url : approot + '/' + comObj.getGoodsUrlPath() + '/trade/offerLogList.html?targetId=' + targetId + '&gridWidth=680&gridHeight=385',
		title : '查看出价记录',
		buttons : [ {
			text : '关闭',
			onclick : function(item, dialog) {
				dialog.close();
			}
		} ]
	});
}

// ---------------------------------
// 查看指定竞买申请出价记录
// ---------------------------------
pageTradeCommonObj.viewLicenseOfferLog = function(licenseId) {
	DialogOpen({
		height : 510,
		width : 700,
		url : approot + '/' + comObj.getGoodsUrlPath() + '/trade/offerLogList.html?licenseId=' + licenseId,
		title : '查看出价记录',
		buttons : [ {
			text : '关闭',
			onclick : function(item, dialog) {
				dialog.close();
			}
		} ]
	});
}

// ---------------------------------
// 查看竞买申请
// ---------------------------------
pageTradeCommonObj.viewApply = function(licenseId) {
	var dialogWidth = 1020;
	DialogOpen({
		height : 600,
		width : dialogWidth,
		url : approot + '/' + comObj.getGoodsUrlPath() + '/bidder/applyView.html?licenseId=' + licenseId + '&u=' + getUserId(),
		title : '查看竞买申请',
		buttons : [ {
			text : '关闭',
			onclick : function(item, dialog) {
				dialog.close();
			}
		} ]
	});
}

// ---------------------------------
// 进入竞价主界面
// ---------------------------------
pageTradeCommonObj.viewTradeMain = function(targetId) {
	var obj = {};
	if (targetId)
		obj.url = approot + '/' + comObj.getGoodsUrlPath() + '/trade/tradeMain.html?targetId=' + targetId;
	else
		obj.url = approot + '/' + comObj.getGoodsUrlPath() + '/trade/tradeMain.html';
	obj.feature = "dialogWidth=1000px;dialogHeight=625px";
	obj.param = {};
	obj.param.goodsType = Utils.getPageValue('goodsType');
	obj.param.mainFrame = window;
	DialogModal(obj);
}

// ---------------------------------
// 进入交易公告页面
// ---------------------------------
pageTradeCommonObj.viewNoticeList = function() {
	var mainFrame = getMainFrame(window);
	if (mainFrame) {
		mainFrame = mainFrame.document.getElementById('framePage');
		if (mainFrame) {
			mainFrame.src = approot + "/" + comObj.getGoodsUrlPath() + "/trade/noticeList.html";
		}
	}
}

// ---------------------------------
// 打开银行联系方式
// ---------------------------------
pageTradeCommonObj.viewContactBank = function() {
	// var obj={};
	// obj.url=approot + '/trade/contactBank.html';
	// obj.feature="dialogWidth=450px;dialogHeight=150px";
	// DialogModal(obj);

	var param = {};
	param.width = 450;
	param.height = 230;
	param.type = 1;
	param.url = approot + "/" + comObj.getGoodsUrlPath() + "/trade/contactBank.html";
	param.title = "联系银行";
	pageTradeCommonObj.openDialog(param);
}
OFC = {}

OFC.none = {

	name : "pure DOM",

	version : function(src) {
		return document.getElementById(src).get_version()
	},

	rasterize : function(src, dst) {

		var _dst = document.getElementById(dst)

		e = document.createElement("div")

		e.innerHTML = Control.OFC.image(src)

		_dst.parentNode.replaceChild(e, _dst);

	},

	image : function(src) {
		return "<img src='data:image/png;base64," + document.getElementById(src).get_img_binary() + "' />"
	},

	popup : function(src) {

		var img_win = window.open('', 'Image')
		with (img_win.document) {

			write("<html>"
					+

					"<head>"
					+ "<title>图片打印</title></head><body><table align='center'><tr><td clospan='2'>"
					+ Control.OFC.image(src)
					+ "</td></tr><tr><td align='center'><input type=\"button\" value=\"打印\" id='printId' onclick='javascript:document.getElementById(\"printId\").style.display=\"none\";document.getElementById(\"closeBnt\").style.display=\"none\";window.print();' /><input type=\"button\" value=\"关闭\"  id='closeBnt' onclick='window.close();'/></td></tr></table></body></html>");
			img_win.document.close();

		}

	}

}

if (typeof (Control == "undefined")) {
	var Control = {
		OFC : OFC.none
	}
}


