var pageMenu = {};
pageMenu.menuObj = {};
pageMenu.timer = null;

pageMenu.setCur = function(id) {
	$('#' + id).siblings().removeClass();
	$('#' + id).addClass('cur');
}

pageMenu.menu1MouseOver = function(ele) {
	clearTimeout(pageMenu.timer);
	pageMenu.setCur(ele.id);
	var menu1 = pageMenu.menuObj[ele.id];
	if (menu1) {
		var children = menu1.children;
		var html = '';
		for ( var i = 0; i < children.length; i++) {
			var ele = children[i];
			pageMenu.menuObj[ele.id] = ele;
			html += '<li id="' + ele.id + '" onmouseover="pageMenu.menu11MouseOver(this);"  onclick="pageMenu.menuItemClick(this);">' + ele.text + '</li>';
		}
		$('#menu11').html(html);
		pageMenu.showMenu11();
	} else {
		pageMenu.hideMenu11();
	}
}

pageMenu.menu11MouseOver = function(ele) {
	pageMenu.setCur(ele.id);
}

pageMenu.menuItemClick = function(ele) {
	pageObj.menuObj = pageMenu.menuObj;
	pageObj.menuItemClick(ele.id);
	pageMenu.hideMenu11();
}

pageMenu.showMenu11 = function() {
	$('.sub_nav').show();
}

pageMenu.hideMenu11 = function() {
	$('.sub_nav').hide();
}

// --------------------------------------------
// 初始化
// --------------------------------------------
$(document).ready(function(event) {
	$('.sub_nav').mouseout(function() {
		pageMenu.timer = setTimeout("pageMenu.hideMenu11();", 1000);
	});

	$('.sub_nav').mouseover(function() {
		clearTimeout(pageMenu.timer);
	});
});