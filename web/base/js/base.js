var approot = "/heyuan";
var Utils = {};
Utils.debug = 1;
Utils.md = 1;
Utils.event = {};
Date.prototype.type = 'date';
//
$$ = function(str) {
	var eleId = str;
	if (str.indexOf("#") == 0) {
		eleId = str.substr(1);
	}
	return document.getElementById(eleId);
};


// -----------------------------------------
// 获取页面Object对象
// ------------------------------------------
Utils.getObj = function (id){
	if(window.document[id])
		return window.document[id];
	else if (navigator.appName.indexOf("Microsoft")==-1){
		if(document.embeds && document.embebs[id])
			return document.embeds[id];
		else
			return document.getElementById(id);
	}else
		return null;
}

// --------------------------------------------
// 将字符串转换为javascript对象
// --------------------------------------------
Utils.parse = function (str){
	var obj=null;
	if(str){
		obj = eval("("+str+")"); 
	}
	return obj;
}



// --------------------------------------------
// 大写
// --------------------------------------------
Number.prototype.toUpcase = function() {
	var num = Math.abs(this);
	var strOutput = "";
	var strUnit = "仟佰拾亿仟佰拾万仟佰拾元角分";
	num += "00";
	var intPos = num.indexOf(".");
	if (intPos >= 0) {
		num = num.substring(0, intPos) + num.substr(intPos + 1, 2);
	}
	strUnit = strUnit.substr(strUnit.length - num.length);
	for ( var i = 0; i < num.length; i++) {
		strOutput += "零壹贰叁肆伍陆柒捌玖".substr(num.substr(i, 1), 1)
				+ strUnit.substr(i, 1);
	}
	return strOutput.replace(/零角零分$/, "整").replace(/零[仟佰拾]/g, "零").replace(
			/零{2,}/g, "零").replace(/零([亿|万])/g, "$1").replace(/零+元/, "元")
			.replace(/亿零{0,3}万/, "亿").replace(/^元/, "零元");
};
// --------------------------------------------
// 元转换成万元
// --------------------------------------------
Number.prototype.toWanYuan=function(){
	var value=this;
	var v=value.div(10000);
	return v;
}

// --------------------------------------------
// 万元转换成元
// --------------------------------------------
Number.prototype.toYuan=function(){
	var value=this;
	var v=value.mul(10000);
	return v;
}


// --------------------------------------------
// 按进制格式化
// --------------------------------------------
Number.prototype.format = function(decimal) {
	var number = this;
	return number / Math.pow(10, bidInfo.decimal);
}
Number.prototype.formatStr = function(decimal) {
	var number = this;
	var s1 = number / Math.pow(10, bidInfo.decimal);
	return s1.addComma();
}
// --------------------------------------------
// 添加千分点
// --------------------------------------------
Number.prototype.addComma = function() {
	var number = this;
	if (number == 0) {
		return 0;
	}
	var num = number + "";
	num = num.replace(new RegExp(",", "g"), "");
	// 正负号处理
	var symble = "";
	if (/^([-+]).*$/.test(num)) {
		symble = num.replace(/^([-+]).*$/, "$1");
		num = num.replace(/^([-+])(.*)$/, "$2");
	}
	if (/^[0-9]+(\.[0-9]+)?$/.test(num)) {
		var num = num.replace(new RegExp("^[0]+", "g"), "");
		if (/^\./.test(num)) {
			num = "0" + num;
		}
		var decimal = num.replace(/^[0-9]+(\.[0-9]+)?$/, "$1");
		var integer = num.replace(/^([0-9]+)(\.[0-9]+)?$/, "$1");
		var re = /(\d+)(\d{3})/;
		while (re.test(integer)) {
			integer = integer.replace(re, "$1,$2");
		}
		return symble + integer + decimal;
	} else {
		return number;
	}
};

// --------------------------------------------
// 检测保留n位小数
// --------------------------------------------
Number.prototype.nDigits = function(n) {
	var b = this.toFixed(n);
	var bn = parseFloat(b);
	if (this == bn)
		return true;
	else
		return false;
}

// --------------------------------------------
// 加法
// --------------------------------------------
Number.prototype.add = function(value) {
	var arg1 = this;
	var arg2 = value;
	var r1, r2, m;
	try {
		r1 = arg1.toString().split(".")[1].length;
	} catch (e) {
		r1 = 0;
	}
	try {
		r2 = arg2.toString().split(".")[1].length;
	} catch (e) {
		r2 = 0;
	}
	m = Math.pow(10, Math.max(r1, r2));
	return (arg1.mul(m) + arg2.mul(m)).div(m);
};

// --------------------------------------------
// 减法
// --------------------------------------------
Number.prototype.sub = function(value) {
	return this.add(-value);
};

// --------------------------------------------
// 除法
// --------------------------------------------
Number.prototype.div = function(value) {
	var arg1 = this;
	var arg2 = value;
	var t1 = 0, t2 = 0, r1, r2;
	try {
		t1 = arg1.toString().split(".")[1].length;
	} catch (e) {
	}
	try {
		t2 = arg2.toString().split(".")[1].length;
	} catch (e) {
	}
	with (Math) {
		r1 = Number(arg1.toString().replace(".", ""));
		r2 = Number(arg2.toString().replace(".", ""));
		return (r1 / r2) * pow(10, t2 - t1);
	}
};

// --------------------------------------------
// 乘法
// --------------------------------------------
Number.prototype.mul = function(value) {
	var arg1 = this;
	var arg2 = value;
	var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
	try {
		m += s1.split(".")[1].length;
	} catch (e) {
	}
	try {
		m += s2.split(".")[1].length;
	} catch (e) {
	}
	return Number(s1.replace(".", "")) * Number(s2.replace(".", ""))
			/ Math.pow(10, m);
};

String.prototype.isNumber = function() {
	if (this.trim().length == 0)
		return false;
	return !isNaN(this);
}

String.prototype.toDate = function() {
	var arr1 = this.split(" ")[0].split("-");
	var arr2 = this.split(" ")[1].split(":");
	var year = parseFloat(arr1[0]);
	var month = parseFloat(arr1[1]);
	var date = parseFloat(arr1[2]);
	var hours = parseFloat(arr2[0]);
	var minutes = parseFloat(arr2[1]);
	var seconds = parseFloat(arr2[2]);
	var myDate = new Date(year, month - 1, date, hours, minutes, seconds);
	return myDate;
};

String.prototype.addDays = function(days) {
	var d1 = this.toDate().getTime();
	var v = d1 + days * 1000 * 60 * 60 * 24;
	var d2 = new Date(v);
	return d2.toString();
}

String.prototype.toObject = function() {
	var obj = eval("(" + this + ")");
	return obj;
};

// --------------------------------------------
// 删除千分点
// --------------------------------------------
String.prototype.removeComma = function() {
	var num = this.replace(new RegExp(",", "g"), "");
	return num;
};

// --------------------------------------------
// 子字符串出现位置（可忽略大小写），如："abcdefg".indexOfIgnoreCase("cd", 1, true)
// --------------------------------------------
String.prototype.indexOfIgnoreCase = function() {
	if (typeof (arguments[arguments.length - 1]) != 'boolean')
		return this.indexOf.apply(this, arguments);
	else {
		var bi = arguments[arguments.length - 1];
		var thisObj = this;
		var idx = 0;
		if (typeof (arguments[arguments.length - 2]) == 'number') {
			idx = arguments[arguments.length - 2];
			thisObj = this.substr(idx);
		}
		var re = new RegExp(arguments[0], bi ? 'i' : '');
		var r = thisObj.match(re);
		return r == null ? -1 : r.index + idx;
	}
}

// --------------------------------------------
// 格式化字符串，如"abcd{0}efg{1}hijk{2}".format("111", "222",
// "333")-->"abcd111efg222hijk333"
// --------------------------------------------
String.prototype.format = function() {
	if (arguments.length == 0)
		return this;
	for ( var s = this, i = 0; i < arguments.length; i++)
		s = s.replace(new RegExp("\\{" + i + "\\}", "g"), arguments[i]);
	return s;
};

// --------------------------------------------
// 字符串替换全部
// --------------------------------------------
String.prototype.replaceAll = function(s1, s2) {
	// return this.replace(new RegExp(s1, "gm"), s2); //特殊字符失败
	var r = new RegExp(s1.replace(/([\(\)\[\]\{\}\^\$\+\-\*\?\.\"\'\|\/\\])/g,
			"\\$1"), "ig");
	return this.replace(r, s2);
}

// --------------------------------------------
// 去除左右空格
// --------------------------------------------
String.prototype.trim = function() {
	return this.replace(/(^\s*)|(\s*$)/g, "");
}

// --------------------------------------------
// 去除左空格
// --------------------------------------------
String.prototype.ltrim = function() {
	return this.replace(/^\s*/g, "");
}

// --------------------------------------------
// 去除右空格
// --------------------------------------------
String.prototype.rtrim = function() {
	return this.replace(/\s*$/g, "");
}

Date.prototype.toString = function() {
	var s = "";
	s += this.getFullYear();
	if (this.getMonth() < 9) {
		s += "-0" + (this.getMonth() + 1);
	} else {
		s += "-" + (this.getMonth() + 1);
	}
	if (this.getDate() < 10) {
		s += "-0" + this.getDate();
	} else {
		s += "-" + this.getDate();
	}
	if (this.getHours() < 10) {
		s += " 0" + this.getHours();
	} else {
		s += " " + this.getHours();
	}
	if (this.getMinutes() < 10) {
		s += ":0" + this.getMinutes();
	} else {
		s += ":" + this.getMinutes();
	}
	if (this.getSeconds() < 10) {
		s += ":0" + this.getSeconds();
	} else {
		s += ":" + this.getSeconds();
	}
	return s;
};

// --------------------------------------------
// 调式信息
// --------------------------------------------
Utils.log = function(ex) {
	if (Utils.debug) {
		alert(ex.name+":"+ex.message);
	}
};
// --------------------------------------------
// 获取记录字段值
// --------------------------------------------
Utils.getRecordValue = function(data, recordNo, fieldName) {
	if (data.fs && data.rs) {
		var index = $.inArray(fieldName, data.fs);
		if (index > -1 && recordNo < data.rs.length) {
			return data.rs[recordNo][index];
		}
	}
	return null;
};
Utils.now = function() {
	return (new Date()).getTime();
};
// --------------------------------------------
// 获取地址栏参数对象
// --------------------------------------------
Utils.getLocationParObj = function() {
	var str = location.href;
	var index = str.indexOf("?");
	var obj = {};
	if (index > 0) {
		str = str.substring(index + 1);
		var arr = str.split("&");
		for ( var i = 0; i < arr.length; i++) {
			var index = arr[i].indexOf("=");
			var key = arr[i].substring(0, index);
			var value = arr[i].substring(index + 1);
			obj[key] = value;
		}
	}
	return obj;
};
// --------------------------------------------
// 获取地址栏参数
// --------------------------------------------
Utils.getLocationPara = function(param) {
	var obj = Utils.getLocationParObj();
	if (typeof (obj[param]) != "undefined") {
		return obj[param];
	} else {
		return null;
	}
};
Utils.event.getEvent = function(event) {
	return event || window.event;
};
Utils.event.getSrcEle = function(event) {
	var e = Utils.event.getEvent(event);
	return e.srcElement || e.target;
};
Utils.event.getKeyNum = function(event) {
	var e = Utils.event.getEvent(event);
	return e.charCode || e.keyCode;
};
Utils.event.endEvent = function(event) {
	try {
		var e = Utils.event.getEvent(event);
		e.keyCode = 0;
		e.cancelBubble = true;
		e.returnValue = false;
	} catch (e) {
	} finally {
		return false;
	}
};
Utils.event.keyHandler = function(event) {
	var e = Utils.event.getEvent(event);
	// if (e.ctrlKey || e.shiftKey || e.altKey) {
	// return Utils.event.endEvent(e);
	// }
	if (Utils.event.getKeyNum(event) == 116) {
		var b = Utils.event.endEvent(e);
		return b;
	}
};
Utils.event.mouseHandler = function(event) {
	var e = Utils.event.getEvent(event);
	if (e.button == 2) {
		document.oncontextmenu = new Function("return false;");
	}
};

// --------------------------------------------
// 获取页面参数
// --------------------------------------------
Utils.getPageValue = function(paramName) {
	var paraValue = null;
	var url=location.href;
	if(url.indexOf(paramName+'=')>=0){
		paraValue = Utils.getUrlParamValue(location.href, paramName);
	} else if(window.dialogArguments && paramName in window.dialogArguments){
		paraValue = window.dialogArguments[paramName];
	}
	try{
		if(paraValue)
			paraValue = decodeURIComponent(paraValue);
	}catch(ex){}
	return paraValue;
}

// --------------------------------------------
// 判断是否有页面参数
// --------------------------------------------
Utils.hasPageValue = function(paramName) {
	var result = false;
	if (window.dialogArguments && paramName in window.dialogArguments)
		result = true;
	else if (Utils.paramExists(document.location.href, paramName, false, '=', '&'))
		result = true;
	return result;
}

// --------------------------------------------
// 为url添加参数，params格式：a=1&b=2&c=3...或者对象格式
// url为null或者undefined直接返回，为空则返回：?a=1&b=2&c=3...
// 如果params中的参数在url中存在，则将值覆盖。参数名区分大小写
// --------------------------------------------
Utils.urlAddParams = function (url, params) {
	if (params) {
		url = url.trim();
		var urlBase = null;
		var urlParams = "";
		if (url.indexOf("?") >= 0) {
			urlBase = url.substr(0, url.indexOf("?"));
			urlParams = url.substr(url.indexOf("?") + 1);
		} else {
			urlBase = url;
		}
		if (typeof(params) == "object") {
			for(key in params) {
				var paramValue = params[key];
				if (paramValue)
					if (Utils.paramExists(urlParams, key, false, "=", "&"))
						urlParams = Utils.setParamValue(urlParams, key, paramValue, false, "=", "&");
					else
						urlParams += "&" + key + "=" + paramValue;
			}
			if (urlParams.substr(0, 1) == "&")
				urlParams = urlParams.substr(1);
			url = urlBase + "?" + urlParams;
		} else {
			params = params.trim();
			if (params.substr(0, 1) == "&")
				params = params.substr(1);
			if (urlParams) {
				var paramsObj = Utils.getParams(params, false, "=", "&");
				return Utils.urlAddParams(url, paramsObj);
			} else
				url = urlBase + "?" + params;
		}
	}
	return url;
}

// --------------------------------------------
// 模仿oracle中的decode函数
// 示例：decode(x, 0, "a", 1, "b", 2, "c",
// "d")，x=0返回a，x=1返回b，类推。全部不匹配则返回d，如果没有d则返回null
// --------------------------------------------
Utils.decode = function () {
	if (arguments.length <= 3)
		return null;
	var expression = arguments[0];
	var compares = new Array();
	var values = new Array();
	for(var i = 1; i < arguments.length; i = i + 2) {
		if (i + 1 == arguments.length) {
			values.push(arguments[i]);
		} else {
			compares.push(arguments[i]);
			values.push(arguments[i + 1]);
		}
	}
	for(var i = 0; i < compares.length; i++) {
		if (expression == compares[i])
			return values[i];
	}
	if (values.length > compares.length)
		return values[values.length - 1];
	else
		return null;
}

// --------------------------------------------
// 模仿oracle中的case语句
// 示例：switchCase(a, 0, b, 1, c, 2, d, 3,
// 4)，a条件成立返回0，b返回1，类推。所有条件不匹配则返回4，如果没有4则返回null
// --------------------------------------------
Utils.switchCase = function () {
	if (arguments.length <= 1)
		return null;
	var expressions = new Array();
	var values = new Array();
	for(var i = 0; i < arguments.length; i = i + 2) {
		if (i + 1 == arguments.length) {
			values.push(arguments[i]);
		} else {
			expressions.push(arguments[i]);
			values.push(arguments[i + 1]);
		}
	}
	for(var i = 0; i < expressions.length; i++) {
		if (expressions[i])
			return values[i];
	}
	if (values.length > expressions.length)
		return values[values.length - 1];
	else
		return null;
}

// --------------------------------------------
// 转换父字符串中的参数名、参数值键值对对象
// 如"abc=123;xyz=666;mnq=888"字符串,得到"{abc: "123", xyz: "666", mnq: "888"}"
// 可自行指定分隔体等于符号和分隔符号，ignoreCase缺省为false，为true时则key全部转换为小写
// --------------------------------------------
Utils.getParams = function(parent, ignoreCase, equals, split) {
	if (parent == undefined || parent == null)
		return {};
	var strEquals = equals;
	if (strEquals == undefined || strEquals == null || strEquals == ""
			|| strEquals.toLowerCase() == "null")
		strEquals = "=";
	var strSplit = split;
	if (strSplit == undefined || strSplit == null || strSplit == ""
			|| strSplit.toLowerCase() == "null")
		strSplit = ";";
	var aryParams = parent.split(strSplit);
	var strParam;
	var paramsMap = {};
	for ( var i = 0; strParam = aryParams[i]; i++) {
		if (strParam.indexOf(strEquals) == -1) {
			paramsMap[strParam] = "";
		} else {
			if (ignoreCase == undefined || !ignoreCase)
				paramsMap[strParam.substring(0, strParam.indexOf(strEquals))] = strParam
						.substring(strParam.indexOf(strEquals)
								+ strEquals.length, strParam.length);
			else
				paramsMap[strParam.substring(0, strParam.indexOf(strEquals))
						.toLowerCase()] = strParam.substring(strParam
						.indexOf(strEquals)
						+ strEquals.length, strParam.length);
		}
	}
	return paramsMap;
}

// --------------------------------------------
// 获取父字符串中的指定参数名的参数值
// 如"abc=123;xyz=666;mnq=888"字符串中的某项的值,paramName="xyz"返回"666"。不存在时返回""，而非null或者undefined以便于调用
// 可自行指定分隔体等于符号和分隔符号，ignoreCase缺省为false
// --------------------------------------------
Utils.getParamValue = function(parent, paramName, ignoreCase, equals, split) {
	var paramsMap = Utils.getParams(parent, ignoreCase, equals, split);
	var returnValue;
	if (ignoreCase == undefined || !ignoreCase)
		returnValue = paramsMap[paramName];
	else
		returnValue = paramsMap[paramName.toLowerCase()];
	if (typeof (returnValue) == "undefined") {
		return "";
	} else {
		return returnValue;
	}
}

// --------------------------------------------
// 判断父字符串中的指定参数名的子参数是否存在
// 存在返回true
// --------------------------------------------
Utils.paramExists = function(parent, paramName, ignoreCase, equals, split) {
	var paramsMap = Utils.getParams(parent, ignoreCase, equals, split);
	if (ignoreCase == undefined || !ignoreCase)
		return paramName in paramsMap;
	else
		return paramName.toLowerCase() in paramsMap;
}

// --------------------------------------------
// 写入父字符串中的指定参数名的参数值
// 如写入"abc=123;xyz=666;mnq=888"字符串中的某项的值,如paramName="xyz",paramValue="XXX"
// 返回"abc=123;xyz=XXX;mnq=888"
// 可自行指定分隔体等于符号和分隔符号，ignoreCase缺省为false。本方法目前有BUG，即返回串参数顺序发生了变化，如果对此无要求可调用
// --------------------------------------------
Utils.setParamValue = function(parent, paramName, paramValue, ignoreCase,
		equals, split) {
	var paramsMap = Utils.getParams(parent, false, equals, split);
	var blnReplace = false;
	// 遍历替换，不存在则添加
	for ( var key in paramsMap) {
		if ((ignoreCase != undefined && ignoreCase && key.toLowerCase() == paramName
				.toLowerCase())
				|| key == paramName) {
			paramsMap[key] = paramValue;
			blnReplace = true;
			break;
		}
	}
	if (!blnReplace)
		paramsMap[paramName] = paramValue;
	var returnValue = "";
	var strEquals = equals;
	if (strEquals == undefined || strEquals == null || strEquals == ""
			|| strEquals.toLowerCase() == "null")
		strEquals = "=";
	var strSplit = split;
	if (strSplit == undefined || strSplit == null || strSplit == ""
			|| strSplit.toLowerCase() == "null")
		strSplit = ";";
	for ( var key in paramsMap) {
		if (returnValue == "")
			returnValue = key + strEquals + paramsMap[key];
		else
			returnValue = returnValue + strSplit + key + strEquals
					+ paramsMap[key];
	}
	return returnValue;
}

// --------------------------------------------
// 子字符串出现次数
// --------------------------------------------
Utils.getSubStrCount = function(s, child, ignoreCase) {
	var count = 0;
	var start = 0;
	while (s.indexOfIgnoreCase(child, start, ignoreCase) >= 0
			&& start < s.length) {
		count++;
		start = s.indexOfIgnoreCase(child, start, ignoreCase) + child.length;
	}
	return count;
}

// --------------------------------------------
// 获取子字符串数组
// --------------------------------------------
Utils.getSubStrs = function(s, split, ignoreCase) {
	if (s == undefined || s == null || s == "") {
		return null;
	}
	var result = new Array();
	if (split == undefined || split == null || split == "") {
		result.push(s);
		return result;
	}
	// aaa###bbb###ccc###ddd###eee
	var strParent = s + split;
	var intPos;
	var intStart = 0;
	do {
		intPos = strParent.indexOfIgnoreCase(split, intStart, ignoreCase);
		if (intPos >= 0) {
			var strSub = strParent.substring(intStart, intPos);
			result.push(strSub);
		}
		intStart = intPos + split.length;
	} while (intPos >= 0);
	return result;
}

// --------------------------------------------
// 获取指定索引位置子字符串
// --------------------------------------------
Utils.getSubStr = function(s, split, splitIndex, ignoreCase) {
	if (s == undefined || s == null || s == "" || splitIndex < 0) {
		return "";
	}
	var subStrList = Utils.getSubStrs(s, split, ignoreCase);
	return subStrList.length > splitIndex ? subStrList[splitIndex] : "";
}

// --------------------------------------------
// 获取指定URL中指定参数名的值
// --------------------------------------------
Utils.getUrlParamValue = function(url, paramName) {
	
	return Utils.getParamValue(url.substring(url.indexOf('?') + 1, url.length),
			paramName, true, '=', '&');
}

Utils.initTokens = function(responseObj) {
	if ("$ts" in responseObj) {
		var mainWin = getMainFrame(window);
		if (mainWin) {
			mainWin['$ts'] = responseObj.ts;
			$(mainWin['$usedTs']).each(function(index, ele) {
				var index2 = $.inArray(ele, mainWin['$ts']);
				if (index2 >= 0) {
					mainWin['$ts'].splice(index2, 1);
				}
			});
			delete responseObj.ts;
		}
	}
}

Utils.initUrl = function(url) {
	var content = url;
	if (content.indexOf("&u=") == -1) {
		var userId = getUserId();
		if (userId)
			content += '&u=' + userId;
		else if(Utils.md==1)
			content +='&u=portal';
	}
	if (content.indexOf("&t=") == -1) {
		var token = getTokenId();
		if (token)
			content += '&t=' + token;
	}
	if (content.indexOf('&s=') == -1) {
		var sId = getSId();
		if (sId)
			content += '&s=' + sId;
	}
	if (content.indexOf('&moduleId=') == -1) {
		var moduleId = (window.dialogArguments && window.dialogArguments.moduleId)
				|| Utils.getUrlParamValue(document.location.href, "moduleId")
		if (moduleId)
			content += '&moduleId=' + moduleId;
	}
	if (content.indexOf("&ms=") == -1) {
		content += "&ms=" + Utils.now();
	}

	if (content.indexOf('&') == 0)
		content = content.substr(1);
	return content;
}

// ------------------------------
// 只能输入数值
// ------------------------------
Utils.keyPressNumberOnly = function() {
	var key = window.event.keyCode;
	if ((key > 47 && key < 58) || (key == 8) || (key == 9) || (key == 13)
			|| (key == 37) || (key == 38) || (key == 39) || (key == 40)
			|| (key == 46) || (key > 95 && key < 106) || (event.keyCode == 110)
			|| (event.keyCode == 190)) {
		window.event.returnValue = true;
	} else {
		window.event.returnValue = false;
	}
}

Utils.isInteger = function(str) {
	if (isNaN(str)) {
		return (false);
	} else {
		if (str.search("[.*]") != -1) {
			return (false);
		}
	}
	return (true);
}
// ------------------------------
// 收集界面输入值
// ------------------------------
Utils.getForm = function(dataObj) {
	// var texts = $(":text");
	var texts = $("input[type=text]");
	texts.each(function(index, ele) {
		var id = $(ele).attr("id");
		var value = $(ele).val();
		if (id)
			dataObj[id] = value;
	});
	var selects = $("select");
	selects.each(function(index, ele) {
		var id = $(ele).attr("id");
		var value = $(ele).val();
		if (id)
			dataObj[id] = value;
	});
	var textareas = $("textarea");
	textareas.each(function(index, ele) {
		var id = $(ele).attr("id");
		var value = $(ele).val();
		if (id)
			dataObj[id] = value;
	});
	var radios = $(":radio[checked='checked']");
	radios.each(function(index, ele) {
		var name = $(ele).attr('name');
		var value = $(ele).val();
		if (name)
			dataObj[name] = value;
	});
	var checkboxs = $(":checkbox[checked='checked']");
	checkboxs.each(function(index, ele) {
		var id = $(ele).attr("id");
		var value = $(ele).val();
		if (id)
			dataObj[id] = value;
	});
}
// ------------------------------
// 设置界面输入值
// ------------------------------
Utils.setForm = function(dataObj) {
	var texts = $("input[type=text]");
	texts.each(function(index, ele) {
		var id = $(ele).attr("id");
		if (id){
			$(ele).val(dataObj[id]);
		}
	});
	var selects = $("select");
	selects.each(function(index, ele) {
		var id = $(ele).attr("id");
		 if (id)
			 $(ele).val(dataObj[id])
	});
	var textareas = $("textarea");
	textareas.each(function(index, ele) {
		var id = $(ele).attr("id");
		if (id)
			$(ele).val(dataObj[id])
	});
	var checkboxs = $(":checkbox");
	checkboxs.each(function(index, ele) {
		var id = $(ele).attr("id");
		var eleValue = $(ele).val();
		var value = dataObj[id];
		if (eleValue == value)
			$(ele).attr("checked", "checked");
	});
	var radios = $(":radio");
	radios.each(function(index, ele) {
		var name = $(ele).attr('name');
		var eleValue = $(ele).val();
		var value = dataObj[name];
		if (eleValue == value)
			$(ele).attr("checked", "checked");
	});
	
}

Utils.addOption = function(id,value,title){
	$$(id).add(new Option(title,value));
}


Utils.requiredCheckForGrid = function(grid, title) {
	var columns = grid.options.columns;
	var rows = grid.getData(null, true);
	for ( var i = 0; i < columns.length; i++) {
		var column = columns[i];
		for ( var j = 0; j < rows.length; j++) {
			var value = (rows[j][column.name] + '').trim();
			if ("requireValue" in column
					&& (value.length == 0 || value == 'undefined')) {
				var msg = column["requireValue"];
				msg = title + '第' + (j + 1) + '行:' + msg;
				DialogAlert(msg);
				return false;
			}
			if ("requireNumber" in column && !value.isNumber()
					&& value.length > 0) {
				var msg = column["requireNumber"];
				msg = title + '第' + (j + 1) + '行:' + msg;
				DialogAlert(msg);
				return false;
			}
		}
	}
	return true;
}

Utils.requiredCheck = function() {
	//
	var eles = $('*[requireValue]');
	for ( var i = 0; i < eles.length; i++) {
		var ele = $(eles[i]);
		var value = ele.val();
		if (!value) {
			var msg = ele.attr("requireValue");
			DialogAlert(msg);
			ele.focus();
			return false;
		}
	}
	//
	eles = $('*[requireNumber]');
	var re = /^\d+(?=\.{0,1}\d+$|$)/;
	for ( var i = 0; i < eles.length; i++) {
		var ele = $(eles[i]);
		var value = ele.val();
		if (value) {
			if (!value.isNumber()) {
				var msg = ele.attr("requireNumber");
				DialogAlert(msg);
				ele.focus();
				return false;
			}
		}
	}
	//
	eles=$('*[requireDigits]');
	for(var i=0;i<eles.length;i++){
		var ele = $(eles[i]);
		var value = ele.val().trim();
		value=parseFloat(value);
		var digits = ele.attr("requireDigits");
		var msg=ele.attr("requireDigitsMsg");
		digits=parseFloat(digits);
		if(!value.nDigits(digits)){
			DialogAlert(msg);
			ele.focus();
			return false;
		}
	}
	//
	eles = $('*[requireFloat]');
	for ( var i = 0; i < eles.length; i++) {
		var ele = $(eles[i]);
		DialogAlert(ele);
	}
	return true;
}
// --------------------------------------------
// 获取提示信息
// example: getLabelByKey('tip1',{s1:'value1',s2:'value2'});
// --------------------------------------------

function getLabelByKey(key, opt) {
	var tipObj = window['tipObj'];
	if (tipObj) {
		var value = tipObj[key];
		if (value) {
			for ( var pro in opt) {
				value = value.replace(pro, opt[pro]);
			}
			return value;
		}
	}
	return null;
}
// --------------------------------------------
// encoding url
// --------------------------------------------
Utils.encodingUrl = function(url) {
	if (!url || url.indexOf('?') < 0)
		return url;
	var arr1 = url.split("?");
	var arr2 = arr1[1].split("&");
	var newArr2 = [];
	for ( var i = 0; i < arr2.length; i++) {
		var arr3 = arr2[i].split("=");
		newArr2.push(arr3[0] + '=' + encodeURIComponent(arr3[1]));
	}
	var temp = '';
	temp += arr1[0];
	temp += '?' + newArr2.join('&');
	return temp;
}

// --------------------------------------------
// 代替$.ajax
// --------------------------------------------
Utils.ajax = function(ajaxOptions) {
	var xhttp;
	try {
		if (window.XMLHttpRequest) {
			xhttp = new XMLHttpRequest()
		} else {
			xhttp = new ActiveXObject("Microsoft.XMLHTTP")
		}
		if (ajaxOptions.beforeSend)
			ajaxOptions.beforeSend(xhttp);
		var url = ajaxOptions.url;
		var content = '';
		if ($.isPlainObject(ajaxOptions.data)) {
			var arr = [];
			for ( var pro in ajaxOptions.data) {
				arr.push(encodeURIComponent(pro) + '='
						+ encodeURIComponent(ajaxOptions.data[pro]));
			}
			content = arr.join('&');
		} else if ($.isArray(ajaxOptions.data)) {
			var arr = [];
			for ( var i = 0; i < ajaxOptions.data.length; i++) {
				var obj = ajaxOptions.data[i];
				arr.push(encodeURIComponent(obj.name) + '='
						+ encodeURIComponent(obj.value));
			}
			content = arr.join('&');
		} else {
			content = ajaxOptions.data;
		}
		xhttp.open('post', url, false);
		content = Utils.initUrl(content);
		var b = Utils.checkLegalUrl(content);
		if (!b) {
			// alert("非法数据访问!");
			var protocol = window.location.protocol + "//";
			var host = window.location.host;
			location.href = protocol + host + approot + "/error.html";
			return;
		}
		xhttp.setRequestHeader('Content-type',
				'application/x-www-form-urlencoded');
		xhttp.send(content);
		if (ajaxOptions.success) {
			var data = Utils.parse(xhttp.responseText);
			Utils.initTokens(data);
			ajaxOptions.success(data);
		}
	} catch (ex) {
		if (ajaxOptions.error) {
			var textStatus = "";
			var errorThrown = "";
			ajaxOptions.error(xhttp, textStatus, errorThrown)
		}
	} finally {
		if (ajaxOptions.complete) {
			ajaxOptions.complete(xhttp);
		}
	}
}

// --------------------------------------------
// 往后端取数据，检查url是否含有u
// --------------------------------------------
Utils.checkLegalUrl = function(url) {
	var arr = [ 'getLoginUser', 'getCode', 'updateBidderData',
			'updateBidderUser' ];
	var b = false;
	var re1 = /method=(\w+)/;
	var arr1 = url.match(re1);
	if (arr1 && $.inArray(arr1[1], arr) >= 0)
		return true;
	var re2 = /u=(\w+)/;
	var arr2 = url.match(re2);
	if (arr2 && arr2[1])
		b = true;
	return b;
}

// --------------------------------------------
// 命令对象
// --------------------------------------------
function Command(module, service, method) {
	this.module = module;
	this.service = service;
	this.method = method;
}

// --------------------------------------------
// 向后端获取数据同步
// --------------------------------------------
Command.prototype.execute = function() {
	var cmdObj = this;
	var type = "/data";
	if (cmdObj.stype) {
		type = cmdObj.stype;
		delete cmdObj.stype;
	}
	var xhttp;
	try {
		var arr = [];
		for ( var pro in cmdObj) {
			var obj = cmdObj[pro];
			var value = obj;
			if ($.isArray(obj)) {
				value = obj.join(",");
			}
			if (obj && obj.type == "date") {
				value = obj.toString();
			}
			if ($.isPlainObject(obj) || $.isFunction(obj) || pro == "type") {
				continue;
			}
			arr.push(pro + "=" + encodeURIComponent(value));
		}
		if (window.XMLHttpRequest) {
			xhttp = new XMLHttpRequest()
		} else {
			xhttp = new ActiveXObject("Microsoft.XMLHTTP")
		}
		if (cmdObj.beforeSend)
			cmdObj.beforeSend(xhttp);
		var url = location.protocol + "//" + location.host + approot + type;
		var content = arr.join('&');
		content = Utils.initUrl(content);
		switch (type) {
		case "/data":
			xhttp.open('post', url, false);// 同步方式请求
			xhttp.setRequestHeader('Content-type',
					'application/x-www-form-urlencoded');
			xhttp.send(content);
			break;
		case "/download":
			url += '?' + content;
			window.open(url);
			break;
		}
		if (cmdObj.success) {
			var dataStr=xhttp.responseText;
			var dataObj=null;
			try{
				dataObj = Utils.parse(dataStr);
				Utils.initTokens(dataObj);
			}catch(ex){}
			if(dataObj&&('state' in dataObj)&&dataObj.state==0&&dataObj.message){
				if(Utils.debug || dataObj.message.indexOf('0x')!=0){
					alert(dataObj.message);
				}
			}else
			cmdObj.success(dataObj,dataStr);
			
		}
	} catch (ex) {
		if (cmdObj.error) {
			var textStatus = "";
			var errorThrown = "";
			cmdObj.error(xhttp, textStatus, errorThrown)
		}
		Utils.log(ex);
	} finally {
		if (cmdObj.complete) {
			cmdObj.complete(xhttp);
		}
	}
}

// --------------------------------------------
// 向后端获取数据 异步
// --------------------------------------------
var fff = {};
Command.prototype.executeAsync = function() {
	var cmdObj = this;
	var xhttp;
	try {
		var arr = [];
		for (var pro in cmdObj) {
			var obj = cmdObj[pro];
			var value = obj;
			if ($.isArray(obj)) {
				value = obj.join(",");
			}
			if (obj && obj.type == "date") {
				value = obj.toString();
			}
			if ($.isPlainObject(obj) || $.isFunction(obj) || pro == "type") {
				continue;
			}
			arr.push(encodeURIComponent(pro) + "=" + encodeURIComponent(value));
		}
		if (window.XMLHttpRequest) {
			xhttp = new XMLHttpRequest()
		} else {
			xhttp = new ActiveXObject("Microsoft.XMLHTTP")
		}
		if (cmdObj.beforeSend)
			cmdObj.beforeSend(xhttp);
		var url = location.protocol + "//" + location.host + approot + "/data";
		var content = arr.join('&');
		content = Utils.initUrl(content);
		xhttp.open('post', url, true);// 异步方法
		xhttp.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
		xhttp.send(content);
		xhttp.onreadystatechange = function() {
			if (xhttp.readyState == 4 && xhttp.status == 200) {
				if (cmdObj.success) {
					try {
						var dataStr=xhttp.responseText;
						var dataObj = Utils.parse(dataStr);
						Utils.initTokens(dataObj);
						var key = cmdObj.module + "." + cmdObj.service + "." + cmdObj.method;
						if (!fff[key])
							fff[key] = -1;
						if (!(dataObj.st && dataObj.st < fff[key])) {
							if(dataObj&&('state' in dataObj)&&dataObj.state==0){
								if(dataObj.message)
									alert(dataObj.message);
							}else
							cmdObj.success(dataObj,dataStr);
							fff[key] = data.st;
						}
					} catch (e) {
					}

				}

			}
		}
		if (cmdObj.complete) {
			cmdObj.complete(xhttp);
		}
	} catch (ex) {
		if (cmdObj.error) {
			cmdObj.error(xhttp);
		}
	}

}

// --------------------------------------------
// 获取主框对象
// --------------------------------------------

function getMainFrame(win) {
	if (win.dialogArguments && "mainFrame" in win.dialogArguments)
		return win.dialogArguments['mainFrame'];
	if (typeof (win['pageName']) != 'undefined'
			&& 'mainframe' == win['pageName'])
		return win;
	else if (win.parent == null || win.parent == win)
		return null;
	else
		return getMainFrame(win.parent);
}

// --------------------------------------------
// 获取Iframe window
// --------------------------------------------
function getIframeWin() {
	var mainFrame = getMainFrame(window);
	return mainFrame.document.getElementById('framePage').contentWindow;
}

// --------------------------------------------
// 获取userInfo对象
// --------------------------------------------
function getUserInfoObj() {
	var mainFrame = getMainFrame(window);
	if (mainFrame && '$userInfo' in mainFrame) {
		if (arguments.length > 0)
			return cloneObj(mainFrame['$userInfo'][arguments[0]]);
		else
			return cloneObj(mainFrame['$userInfo']);
	} else
		return {};
}

function getSysInfoObj() {
	var mainFrame = getMainFrame(window);
	if (mainFrame && '$sysInfo' in mainFrame)
		return cloneObj(mainFrame['$sysInfo']);
	else
		return {};
}

// --------------------------------------------
// 获取userId
// --------------------------------------------
function getUserId() {
	return Utils.getPageValue('u') || getUserInfoObj().userId;
}

// --------------------------------------------
// 获取userId
// --------------------------------------------
function getOrganId() {
	return getUserInfoObj().organId;
}

// --------------------------------------------
// 获取CaKey
// --------------------------------------------
function getUserCakey() {
	return getUserInfoObj().caKey;
}
// --------------------------------------------
// 获取Session Id
// --------------------------------------------
function getSId() {
	var mainFrame = getMainFrame(window);
	if (mainFrame && ('$userInfo' in mainFrame)
			&& ('sid' in mainFrame['$userInfo']))
		return mainFrame['$userInfo']['sid'];
	else
		return null;
}
// --------------------------------------------
// 获取令牌Id
// --------------------------------------------
function getTokenId() {
	var mainFrame = getMainFrame(window);
	if (mainFrame && ('$userInfo' in mainFrame)
			&& ('ts' in mainFrame['$userInfo'])) {
		var arr = mainFrame['$userInfo']['ts'];
		var t = arr.shift();
		if (!("$usedTs" in mainFrame))
			mainFrame['$usedTs'] = [];
		mainFrame['$usedTs'].push(t);
		if (mainFrame['$usedTs'].length > 10) {
			mainFrame['$usedTs'].splice(0, mainFrame['$usedTs'].length - 10);
		}
		return t;
	}
	return null;
}

// --------------------------------------------
// 返回GUID，secure值为true则返回值中不包含“-”，不传同true
// --------------------------------------------
function newGUID(secure) {
	var guid = "";
	var splitChar = "";
	if (secure != undefined && secure == false)
		splitChar = "-";
	for ( var i = 0; i < 8; i++) {
		var randomValue = (((1 + Math.random()) * 0x10000) | 0).toString(16)
				.substring(1);
		guid += randomValue;
		if (i == 1 || i == 2 || i == 3 || i == 4)
			guid += splitChar;
	}
	return guid.toUpperCase();
}

// --------------------------------------------
// 克隆对象
// --------------------------------------------
function cloneObj(Obj) {
	var newObj;
	if (Obj instanceof Array) {
		newObj = [];// 创建一个空的数组
		var i = Obj.length;
		while (i--) {
			newObj[i] = cloneObj(Obj[i]);
		}
		return newObj;
	} else if (Obj instanceof Object) {
		newObj = {};// 创建一个空对象
		for ( var k in Obj) {
			newObj[k] = cloneObj(Obj[k]);
		}
		return newObj;
	} else {
		return Obj;
	}
}

// --------------------------------------------
// 判断对象有无属性，即是否是“{}”
// --------------------------------------------
function objIsEmpty(obj) {
	if (!obj)
		return true;
	for ( var key in obj)
		return false;
	return true;
}

// --------------------------------------------
// 写全局属性
// --------------------------------------------
function setGlobalAttribute(key, value) {
	var mainFrame = getMainFrame(window);
	if (!mainFrame)
		// 无法找到公共的主界面就找当前界面的顶层父界面
		mainFrame = window.top;
	if (mainFrame) {
		if (mainFrame.__globalAttributes == undefined || mainFrame.__globalAttributes == null)
			mainFrame.__globalAttributes = {};
		mainFrame.__globalAttributes[key] = value;
		return true;
	} else
		return false;
}

// --------------------------------------------
// 读全局属性
// --------------------------------------------
function getGlobalAttribute(key) {
	var mainFrame = getMainFrame(window);
	if (!mainFrame)
		// 无法找到公共的主界面就找当前界面的顶层父界面
		mainFrame = window.top;
	if (mainFrame && mainFrame.__globalAttributes && key in mainFrame.__globalAttributes)
		return mainFrame.__globalAttributes[key];
	else
		return undefined;
}

// --------------------------------------------
// 移除全局属性
// --------------------------------------------
function removeGlobalAttribute(key) {
	var mainFrame = getMainFrame(window);
	if (!mainFrame)
		// 无法找到公共的主界面就找当前界面的顶层父界面
		mainFrame = window.top;
	if (mainFrame && mainFrame.__globalAttributes && key in mainFrame.__globalAttributes) {
		delete mainFrame.__globalAttributes[key];
		return true;
	} else
		return false;
}

Utils.getIframe = function() {
	var eles = parent.document.getElementsByTagName('iframe');
	for ( var i = 0; i < eles.length; i++) {
		if (eles[i].contentWindow == window) {
			return eles[i];
		}
	}
	return null;
}
// --------------------------------------------
// 动态 设置IFrame高度 由IFrame 调用
// --------------------------------------------
Utils.autoIframeSize = function() {
	var frameObj = window;
	var eles = parent.document.getElementsByTagName('iframe');
	var frame = null;
	for ( var i = 0; i < eles.length; i++) {
		if (eles[i].contentWindow == frameObj) {
			frame = eles[i];
			break;
		}
	}
	if (frame) {
		frame.style.display = "block";
		var h = 0;
		$(frameObj.document.body).children().each(function(i, ele) {
			var h1 = ele.scrollHeight;
			var v = $(ele).css('margin-top');
			if (!isNaN(v)) {
				h += parseFloat(v);
			}
			v = $(ele).css('margin-bottom');
			if (!isNaN(v)) {
				h += parseFloat(v);
			}
			h += h1;
		});
		frame.height = h + 10;
	}
}

// --------------------------------------------
// 对话框
// --------------------------------------------
function getCurrentWin(win) {
	return win.top;
}

function DialogAlert(content, parentWin) {
	var mainFrame = parentWin ? parentWin : getCurrentWin(window);
	var manager = mainFrame.$.ligerDialog.alert(content);
	return manager;
}

function DialogWarn(content, func, parentWin) {
	var mainFrame = parentWin ? parentWin : getCurrentWin(window);
	var manager = mainFrame.$.ligerDialog.warning(content, func);
	return manager
}

function DialogError(content, parentWin) {
	var mainFrame = parentWin ? parentWin : getCurrentWin(window);
	var manager = mainFrame.$.ligerDialog.error(content);
	return manager;
}

function DialogSuccess(content, parentWin) {
	var mainFrame = parentWin ? parentWin : getCurrentWin(window);
	var manager = mainFrame.$.ligerDialog.success(content);
	return manager;
}

function DialogConfirm(content, func, parentWin) {
	var mainFrame = parentWin ? parentWin : getCurrentWin(window);
	var manager = mainFrame.$.ligerDialog.confirm(content, func);
	return manager;
}

function DialogPrompt(content, initContent, func, parentWin) {
	var mainFrame = parentWin ? parentWin : getCurrentWin(window);
	var manager = mainFrame.$.ligerDialog.prompt(content, initContent, func);
	return manager;
}

function DialogWaitting(content, parentWin) {
	var mainFrame = parentWin ? parentWin : getCurrentWin(window);
	var manager = mainFrame.$.ligerDialog.waitting(content);
	return manager;
}

function DialogOpen(opt, parentWin) {
	opt.showMax = false;
	opt.showToggle = false;
	opt.showMin = false;
	opt.isResize = true;
	opt.modal = true;
	opt.isHidden = false;
	var mainFrame = parentWin ? parentWin : getCurrentWin(window);
	var manager = mainFrame.$.ligerDialog.open(opt);
	mainFrame.$('.l-taskbar').hide();
	return manager;
}

// --------------------------------------------
// 模态弹出对话框
// var obj={};
// obj.url="";//地址
// obj.param={};//参数
// obj.feature="dialogWidth=200px;dialogHeight=100px";//窗口参数
// DialogModal(obj);
// --------------------------------------------
function DialogModal(opt) {
	var param = {};
	if ("param" in opt)
		param = opt.param;
	var win = getMainFrame(window);
	if (window.dialogArguments && "mainFrame" in window.dialogArguments)
		win = window.dialogArguments["mainFrame"];
	param.mainFrame = win;
	param.parent = window;
	var returnValue = showModalDialog(opt.url, param, opt.feature);
	return returnValue;
}
// --------------------------------------------
// 所有页面初始化事件
// --------------------------------------------
$(document)
		.ready(
				function() {
					$(document).keydown(function(event) {
						var b = Utils.event.keyHandler(event);
						return b;
					});

					$(document).mousedown(function(event) {
						// return Utils.event.mouseHandler(event);
					});

					$(document)
							.click(
									function() {
										var mainFrame = getMainFrame(window);
										if (typeof (pageName) == 'undefined'
												&& mainFrame
												&& "mainframeObj" in mainFrame) {
											if (mainFrame.mainframeObj.mainMenu
													&& "actionMenu" in mainFrame.mainframeObj.mainMenu)
												mainFrame.mainframeObj.mainMenu.actionMenu
														.hide();
											if (mainFrame.mainframeObj.roleMenu
													&& "actionMenu" in mainFrame.mainframeObj.roleMenu)
												mainFrame.mainframeObj.roleMenu.actionMenu
														.hide();
										}
									});

					var inputs = $('.i-input_off');
					inputs.each(function(index, input) {
						$(input).focusin(function() {
							input.className = "i-input_on";
						});
						$(input).focusout(function() {
							input.className = "i-input_off";
						});
					});
					var btns = $('.i-btn_off');
					btns.each(function(index, btn) {
						$(btn).hover(function() {
							btn.className = "i-btn_on";
						}, function() {
							btn.className = "i-btn_off";
						});
					});
					var trade_inputs = $('.o_btn');
					trade_inputs.each(function(index, input) {
						$(input).mousedown(function() {
							input.className = "c_btn";
						});
						$(input).mouseover(function() {
							input.className = "s_btn";
						});
						$(input).mouseout(function() {
							input.className = "o_btn";
						});
					});
					/**
					 * 刷新表格
					 * urlPath:/data?module=before&service=TransGoodsLand&method=getTransTargetList
					 * queryObj：{u:'0001'}
					 */
					if ($.ligerui) {
						// 刷新表格 回到第一页
						$.ligerui.controls.Grid.prototype.refresh = function(
								urlPath, paraObj) {
							var url = this.options.url;
							if (urlPath)
								url = urlPath;
							var queryObj = {};
							for ( var key in paraObj) {
								var value = paraObj[key];
								value = encodeURIComponent(value);
								url += '&' + key + '=' + value;
							}
							var url2=url;
							if (this.options.usePager) {
								var pagesizeName = this.options.pagesizeParmName;
								var pageName = this.options.pageParmName;
								queryObj[pagesizeName] = this.options['pageSize'];
								url += '&' + pageName + '=' + 1;
							}
							this.options.url = url;
							var page = this.options['page'];
							if (page == 1)
								this.loadServerData(queryObj);
							else
								this.changePage("first");
							this.options.url=url2;
						};
						// 刷新本页
						$.ligerui.controls.Grid.prototype.refreshPage = function() {
							var oldUrl = this.options.url;
							var queryObj={};
							if(this.options.usePager){
								var pagesizeName = this.options.pagesizeParmName;
								var pageName = this.options.pageParmName;
								queryObj[pagesizeName] = this.options['pageSize'];
								queryObj[pageName] = this.options['page'];
							}
							this.loadServerData(queryObj);
							this.options.url=oldUrl;
						};
						// 获取当前选中行
						$.ligerui.controls.Grid.prototype.getCheckedArr = function(
								flag) {
							var arr = [];
							var rows = this.getCheckedRows();
							var key = flag;
							if (!key)
								key = 'id';
							$(rows).each(function(index, row) {
								arr.push(row[key]);
							});
							return arr;
						};
					}
					$.fn.extend({
						tree : function(setting, zNodes) {
							var tree = $.fn.zTree.init(this, setting, zNodes);
							if (!this.hasClass('ztree'))
								this.addClass('ztree');
							return tree;
						}
					});
				});

// --------------------------------------------
// 页面检查数据是否修改组件
// --------------------------------------------
function FieldModifiedChecker(owner, initOwnerFields) {
	if (owner)
		this.owner = owner;
	else
		this.owner = document;
	this.modified = false;
	this.modifiedElements = null;
	this.verifyElements = null;
	if (initOwnerFields)
		this.initFileds();
}

FieldModifiedChecker.prototype.initFileds = function() {
	this.modified = false;
	if (this.modifiedElements != null) {
		this.modifiedElements.splice(0, this.modifiedElements.length);
	}
	var inputs = $(this.owner).find("input");
	var textareas = $(this.owner).find("textarea");
	var selects = $(this.owner).find("select");
	for ( var i = 0; i < inputs.length; i++) {
		if ("checked" in inputs[i]) {
			$(inputs[i]).data("oldChecked", inputs[i].checked);
		}
		if ("value" in inputs[i]) {
			$(inputs[i]).data("oldValue", inputs[i].value);
		}
		$(inputs[i]).data("modified", false);
	}
	for ( var i = 0; i < textareas.length; i++) {
		$(textareas[i]).data("oldValue", textareas[i].value);
		$(textareas[i]).data("modified", false);
	}
	for ( var i = 0; i < selects.length; i++) {
		$(selects[i]).data("oldValue", selects[i].value);
		$(selects[i]).data("modified", false);
	}
}

FieldModifiedChecker.prototype.checkModified = function(immediateReturn) {
	this.modified = false;
	var inputs = $(this.owner).find("input");
	var textareas = $(this.owner).find("textarea");
	var selects = $(this.owner).find("select");
	this.modifiedElements = new Array();
	for ( var i = 0; i < inputs.length; i++) {
		if ("checked" in inputs[i] && $.hasData(inputs[i])
				&& $(inputs[i]).data("oldChecked") != undefined) {
			$(inputs[i]).data("modified",
					$(inputs[i]).data("oldChecked") != inputs[i].checked);
		}
		if (!$(inputs[i]).data("modified") && "value" in inputs[i]
				&& $.hasData(inputs[i])
				&& $(inputs[i]).data("oldValue") != undefined) {
			$(inputs[i]).data("modified",
					$(inputs[i]).data("oldValue") != inputs[i].value);
		}
		if ($(inputs[i]).data("modified")) {
			this.modified = true;
			if (immediateReturn)
				return this.modified;
			this.modifiedElements.push(inputs[i]);
		}
	}
	for ( var i = 0; i < textareas.length; i++) {
		if ("value" in textareas[i] && $.hasData(textareas[i])
				&& $(textareas[i]).data("oldValue") != undefined) {
			$(textareas[i]).data("modified",
					$(textareas[i]).data("oldValue") != textareas[i].value);
		}
		if ($(textareas[i]).data("modified")) {
			this.modified = true;
			if (immediateReturn)
				return this.modified;
			this.modifiedElements.push(textareas[i]);
		}
	}
	for ( var i = 0; i < selects.length; i++) {
		if ("value" in selects[i] && $.hasData(selects[i])
				&& $(selects[i]).data("oldValue") != undefined) {
			$(selects[i]).data("modified",
					$(selects[i]).data("oldValue") != selects[i].value);
		}
		if ($(selects[i]).data("modified")) {
			this.modified = true;
			if (immediateReturn)
				return this.modified;
			this.modifiedElements.push(selects[i]);
		}
	}
	return this.modified;
}

FieldModifiedChecker.prototype.checkElementModified = function(element) {
	if (!element)
		return false;
	if ("checked" in element && $.hasData(element) && $(element).data("oldChecked") != undefined
			&& $(element).data("oldChecked") != element.checked)
		return true;
	if ("value" in element && $.hasData(element) && $(element).data("oldValue") != undefined
			&& $(element).data("oldValue") != element.value)
		return true;
	return false;
}

FieldModifiedChecker.prototype.verifyFileds = function(owner, elements) {
	if (this.verifyElements != null) {
		this.verifyElements.splice(0, this.verifyElements.length);
	}
	var inputs = owner ? $(owner).find("input, textarea") : (elements ? elements : $(this.owner).find("input, textarea"));
	for ( var i = 0; i < inputs.length; i++) {
		if (typeof($(inputs[i]).attr("required")) != "undefined"
			|| typeof($(inputs[i]).attr("minLength")) != "undefined"
			|| typeof($(inputs[i]).attr("maxLength")) != "undefined"
			|| typeof($(inputs[i]).attr("minLen")) != "undefined"
			|| typeof($(inputs[i]).attr("maxLen")) != "undefined"
			|| typeof($(inputs[i]).attr("minValue")) != "undefined"
			|| typeof($(inputs[i]).attr("maxValue")) != "undefined"
			|| typeof($(inputs[i]).attr("minVal")) != "undefined"
			|| typeof($(inputs[i]).attr("maxVal")) != "undefined") {
			if (!this.verifyElements)
				this.verifyElements = new Array();
			this.verifyElements.push(inputs[i]);
		}
	}
}

FieldModifiedChecker.prototype.verify = function(owner, elements) {
	this.verifyFileds(owner, elements);
	if (!this.verifyElements || this.verifyElements.length == 0)
		return true;
	var error = false;
	var errorMessages = new Array();
	for(var i = 0; i < this.verifyElements.length; i++) {
		var element = $(this.verifyElements[i]);
		var value = element.val();
		// 验证是否为空
		if (typeof(element.attr("required")) != "undefined") {
			if (value == null || value == undefined || value == "") {
				error = true;
				var message = element.attr("requiredMessage");
				if (!message)
					message = "必须输入值";
				if ($.inArray(message, errorMessages) == -1)
					errorMessages.push(message);
			}
		}
		if ((typeof(element.attr("minLength")) != "undefined" && element.attr("minLength") > 0)
			|| (typeof(element.attr("minLen")) != "undefined" && element.attr("minLen") > 0)) {
			var minLength = element.attr("minLength") || element.attr("minLen");
			if (value == null || value == undefined || value == "" || value.length < minLength) {
				error = true;
				var message = element.attr("lengthMessage") || element.attr("lenMessage");
				if (!message)
					message = "长度不能小于" + minLength + "个字符";
				if ($.inArray(message, errorMessages) == -1)
					errorMessages.push(message);
			}
		}
		if ((typeof(element.attr("maxLength")) != "undefined" && element.attr("maxLength") > 0)
			|| (typeof(element.attr("maxLen")) != "undefined" && element.attr("maxLen") > 0)) {
			var maxLength = element.attr("maxLength") || element.attr("maxLen");
			if (value && value.length > maxLength) {
				error = true;
				var message = element.attr("lengthMessage") || element.attr("lenMessage");
				if (!message)
					message = "长度不能大于" + maxLength + "个字符";
				if ($.inArray(message, errorMessages) == -1)
					errorMessages.push(message);
			}
		}
		if (typeof(element.attr("minValue")) != "undefined" || typeof(element.attr("minVal")) != "undefined") {
			var minValue = element.attr("minValue") || element.attr("minVal");
			if (!value || value < minValue) {
				error = true;
				var message = element.attr("valueMessage") || element.attr("valMessage");
				if (!message)
					message = "值不能小于" + minValue;
				if ($.inArray(message, errorMessages) == -1)
					errorMessages.push(message);
			}
		}
		if (typeof(element.attr("maxValue")) != "undefined" || typeof(element.attr("maxVal")) != "undefined") {
			var maxValue = element.attr("maxValue") || element.attr("maxVal");
			if (!value || value > maxValue) {
				error = true;
				var message = element.attr("valueMessage") || element.attr("valMessage");
				if (!message)
					message = "值不能大于" + maxValue;
				if ($.inArray(message, errorMessages) == -1)
					errorMessages.push(message);
			}
		}
	}
	if (error) {
		if (errorMessages.length > 0) {
			var showMessage = "";
			for(var i = 0; i < errorMessages.length; i++)
				showMessage += errorMessages[i] + "\n";
			alert(showMessage);
		}
		return false;
	} else
		return true;
}

// add by luodanqing
// --------------------------------------------
// 日期加，减请用负数
// add: 加减量
// addType:
// 加减量类型。0,y,yy,year：年;1,m,mm,month：月;3,h,hh,hour：小时;4,mi,nn,minute：分;5,s,ss,second：秒;6,w,ww,week：星期;其它值为天
// 返回日期
// --------------------------------------------
Date.dateAdd = function(date, add, addType) {
	// var d = new Date(date);
	var d = $.type(date) == "date" ? date : date.toString().toDate();
	h = d.valueOf();
	if (addType) {
		var strAddType = addType.toString().toLowerCase();
		if (strAddType == 0 || strAddType == "year" || strAddType == "y"
				|| strAddType == "yy") { // 加年
			d = new Date(d.getFullYear() + parseInt(add), d.getMonth(), d
					.getDate(), d.getHours(), d.getMinutes(), d.getSeconds());
			h = d.valueOf();
		} else if (strAddType == 1 || strAddType == "month"
				|| strAddType == "m" || strAddType == "mm") { // 加月
			d = new Date(d.getFullYear(), d.getMonth() + parseInt(add), d
					.getDate(), d.getHours(), d.getMinutes(), d.getSeconds());
			h = d.valueOf();
		} else if (strAddType == 3 || strAddType == "hour" || strAddType == "h"
				|| strAddType == "hh") // 加小时
			h = h + add * 60 * 60 * 1000;
		else if (strAddType == 4 || strAddType == "minute"
				|| strAddType == "mi" || strAddType == "nn") // 加分钟
			h = h + add * 60 * 1000;
		else if (strAddType == 5 || strAddType == "second" || strAddType == "s"
				|| strAddType == "ss") // 加秒
			h = h + add * 1000;
		else if (strAddType == 6 || strAddType == "week" || strAddType == "w"
				|| strAddType == "ww") // 加周
			h = h + add * 7 * 24 * 60 * 60 * 1000;
		else
			// 加日（缺省）
			h = h + add * 24 * 60 * 60 * 1000;
	} else
		h = h + add * 24 * 60 * 60 * 1000;
	d = new Date(h);
	return d;
}


Utils.checkNumberKey = function(key, allowNegative) {
	return Utils.checkIntegerKey(key, allowNegative) || key == 110// 小键盘小数点
			|| key == 190;// 小数点
}

Utils.checkIntegerKey = function(key, allowNegative) {
	return (key >= 48 && key <= 57)// 0..9
			|| (key >= 96 && key <= 105)// 小键盘0..9
			|| (allowNegative && key == 189);// 负号
}

Utils.gtPa = function(data){
	if(data.vli!=null && data.un!=null && data.pd!=null){
		pageObj.noca=1;
		pageObj.vli=data.vli;
		pageObj.un=data.un;
		pageObj.pd=data.pd;
		pageObj.newCode=data.newCode;
		pageObj.login();
	}
} 
