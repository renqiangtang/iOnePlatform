var pageObj = {};

pageObj.initPage = function() {
	$('#add').click(pageObj.add);
	$('#sub').click(pageObj.sub);
	var frame = document.getElementById('iframe');
	pageObj.h = $(frame).height();
	$('#v').val(pageObj.h);
}

pageObj.add = function() {
	//pageObj.h += 100;
	//pageObj.refresh();
	var t1='2012-09-25 02:01:29';
	var t2=t1.addDays(30);
	alert(t2);
}

pageObj.sub = function() {
	pageObj.h -= 100;
	pageObj.refresh();
}

pageObj.refresh = function() {
	$('#v').val(pageObj.h);
	var frame = document.getElementById('iframe');
	$(frame).height(pageObj.h);
}

$(document).ready(pageObj.initPage);