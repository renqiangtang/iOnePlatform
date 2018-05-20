var tradeMain = {};
tradeMain.timer = null; // 刷新定时器
tradeMain.intervalTime = 1000; // 刷新频率为1000ms
tradeMain.curTarget = null; // 当前id
tradeMain.targetsObj = {};
tradeMain.loading = true;
tradeMain.firstTab = 0;
tradeMain.module = 'tradeland';

tradeMain.html_refresh = function() {
	var cmd = new Command();
	cmd.module = tradeMain.module;
	cmd.service = "Trade";
	cmd.method = tradeMain.method;
	cmd.goodsType = '101';
	cmd.targetId = Utils.getLocationPara('targetId');
	cmd.success = function(data) {
		var newdata = {};
		for ( var i = 0; i < data.targetList.length; i++) {
			var obj = data.targetList[i];
			newdata[obj.targetId] = obj;
		}
		tradeMain.html_load(newdata);
	};
	cmd.executeAsync();
}

tradeMain.html_load = function(data) {
	var html = '';
	var hasTarget = false;
	for (targetId in data) {// add
		if (!(targetId in tradeMain.targetsObj)) {
			var target = data[targetId];
			var className = "i-unselectTag"
			if (tradeMain.curTarget == null) {
				className = "i-selectTag";
				tradeMain.curTarget = target;
				tradeMain.displayHtml();
			}
			var targetName = target.targetName;
			if (!targetName)
				targetName = target.targetNo;
			html += '<li class="' + className + '"  id="' + target.targetId + '" onclick="tradeMain.targetChange(\'' + target.targetId + '\')">';
			html += targetName;
			html += '</li>';
		}
		hasTarget = true;
	}
	if (!hasTarget) {
		$('#L00').show();
	} else {
		$('#L00').hide();
	}
	$('#tags').append(html);
	for (targetId in tradeMain.targetsObj) {// remove
		if (!(targetId in data)) {
			$('#' + targetId).remove();
		}
	}
	tradeMain.targetsObj = data;
	if (tradeMain.curTarget) {
		var newTarget = data[tradeMain.curTarget.targetId];
		if (newTarget.ptype != tradeMain.curTarget.ptype)
			tradeMain.targetChange(newTarget.targetId);
	}
	//
	tradeMain.tab();

}

// --------------------------------------------
// 更换标的Tab
// --------------------------------------------
tradeMain.targetChange = function(targetId) {
	var newTarget = tradeMain.targetsObj[targetId];
	if ($$(tradeMain.curTarget.targetId)) {
		$$(tradeMain.curTarget.targetId).className = 'i-unselectTag';
		// 错误数据包
		// tradeCommon.pretargetId = tradeMain.curTarget.targetId;
	}
	$$(newTarget.targetId).className = 'i-selectTag';
	var ptype = tradeMain.curTarget.ptype;
	window['trade' + ptype + 'Obj'].destory();
	tradeMain.curTarget = newTarget;
	tradeMain.displayHtml();

}
// --------------------------------------------
// 显示相关页面
// --------------------------------------------
tradeMain.displayHtml = function() {
	tradeMain.hide();
	var ptype = tradeMain.curTarget.ptype;
	$('#' + ptype).show();
	window['trade' + ptype + 'Obj'].start(tradeMain.curTarget.targetId);
}


tradeMain.getWidth = function(ele, str) {
	var w = ele.css(str);
	var p = parseFloat(w);
	p = parseFloat(p);
	if (isNaN(p))
		return 0;
	else
		return p;
}

// --------------------------------------------
// 刷新Tab页面
// --------------------------------------------
tradeMain.tab = function() {
	var totalW = $('#tagsDiv').width();
	var actualW = 0;
	var lis = $("#tags > li");
	lis.each(function(index, ele) {
		$(ele).hide();
	});
	var f = tradeMain.firstTab;
	while (1) {
		var o = $(lis[f]);
		var w = o.width();
		actualW += w;
		actualW += tradeMain.getWidth(o, 'border-left-width');
		actualW += tradeMain.getWidth(o, 'border-right-width');
		actualW += tradeMain.getWidth(o, 'padding-left');
		actualW += tradeMain.getWidth(o, 'padding-right');
		actualW += tradeMain.getWidth(o, 'margin-left');
		actualW += tradeMain.getWidth(o, 'margin-right');
		if ((actualW < totalW) && (f < lis.length)) {
			o.show();
			f++;
		} else
			break;
	}
	$('#toLeft').show();
	$('#toRight').show();
	if (tradeMain.firstTab <= 0) {
		$('#toLeft').hide();
	}
	if (f >= lis.length) {
		$('#toRight').hide();
	}
}

tradeMain.hide = function() {
	$('#L01').hide();
	$('#L02').hide();
	$('#L03').hide();
	$('#Ok').hide();
	$('#Wait').hide();
	$('#Abort').hide();
	$('#L00').hide();
	$('#Pause').hide();
}

// --------------------------------------------
// 页面初始化
// --------------------------------------------
$(document).ready(function() {
	$('#tags').empty();
	tradeMain.hide();
	tradeMain.method = 'getTargets';
	var method = Utils.getPageValue('method');
	if (method)
		tradeMain.method = method;
	tradeMain.html_refresh();
	tradeMain.timer = setInterval("tradeMain.html_refresh()", tradeMain.intervalTime);
	$('#toLeft').click(function(event) {
		tradeMain.firstTab--;
		tradeMain.tab();
	});
	$('#toRight').click(function(event) {
		tradeMain.firstTab++;
		tradeMain.tab();
	});

});
