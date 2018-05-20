pageViewImagesObj = {}
pageViewImagesObj.imgUrl = null;
pageViewImagesObj.imgArr = null;
pageViewImagesObj.timer = null;
pageViewImagesObj.offset = 115000;
pageViewImagesObj.index = 0;
pageViewImagesObj.maxWidth = 772; // 图片最大宽度
pageViewImagesObj.maxHeight = 434; // 图片最大高度
pageViewImagesObj.ratio = 0; // 缩放比例
pageViewImagesObj.width = null; // 图片改变后宽度
pageViewImagesObj.height = null; // 图片改变后高度
pageViewImagesObj.target = new Array();
pageViewImagesObj.startPage=0;//列表目前第一张图片
pageViewImagesObj.endIndex=0;//列表目前最后一张图片
pageViewImagesObj.styleleft=0;//已经移动的left

// 大图交替轮换
pageViewImagesObj.slideImage = function(i) {
	var id = 'image_' + pageViewImagesObj.target[i];
	$('#' + id).animate({
				opacity : 1
			}, 800, function() {
				$(this).find('.word').animate({
							height : 'show'
						}, 'slow');
			}).show().siblings(':visible').find('.word').animate({
				height : 'hide'
			}, 'fast', function() {
				$(this).parent().animate({
							opacity : 0
						}, 800).hide();
			});
}

pageViewImagesObj.hookThumb = function() {
	$('#thumbs li a').bind('click', function() {
				if (pageViewImagesObj.timer) {
					clearTimeout(pageViewImagesObj.timer);
				}
				var id = this.id;
				pageViewImagesObj.index = pageViewImagesObj.getIndex(id.substr(6));
				pageViewImagesObj.rechange(pageViewImagesObj.index);
				pageViewImagesObj.slideImage(pageViewImagesObj.index);
//				pageViewImagesObj.timer = window.setTimeout(pageViewImagesObj.auto, pageViewImagesObj.offset);
				this.blur();
				return false;
			});
}

pageViewImagesObj.hookBtn = function() {
	$('#thumbs div img').filter('#play_prev,#play_next').bind('click', function() {
				if (pageViewImagesObj.timer) {
					clearTimeout(pageViewImagesObj.timer);
				}
				var id = this.id;
				if (id == 'play_prev') {
					pageViewImagesObj.moveImageList("up");
					pageViewImagesObj.index--;
					if (pageViewImagesObj.index < 0)
						pageViewImagesObj.index = 0;
					
					
				} else {
					pageViewImagesObj.moveImageList("next");
					pageViewImagesObj.index++;
					if (pageViewImagesObj.index >= pageViewImagesObj.imgArr.length)
						pageViewImagesObj.index = pageViewImagesObj.imgArr.length;
				}
//				pageViewImagesObj.selectIndex=pageViewImagesObj.index;//目前选中的图片
//				
//				pageViewImagesObj.rechange(pageViewImagesObj.index);
//				pageViewImagesObj.slideImage(pageViewImagesObj.index);
//				pageViewImagesObj.timer = window.setTimeout(pageViewImagesObj.auto, pageViewImagesObj.offset);
			});
}

pageViewImagesObj.bighookBtn = function() {
	$('#bigpicarea p span').filter('#big_play_prev,#big_play_next').bind('click', function() {
				if (pageViewImagesObj.timer) {
					clearTimeout(pageViewImagesObj.timer);
				}
				var id = this.id;
				if (id == 'big_play_prev') {
					pageViewImagesObj.index--;
					if (pageViewImagesObj.index < 0)
						pageViewImagesObj.index = 0;
					if(pageViewImagesObj.startPage*3%3==0 && pageViewImagesObj.startPage!=0){
						pageViewImagesObj.moveImageList("up");
					}
				} else {
					pageViewImagesObj.index++;
					if (pageViewImagesObj.index >= pageViewImagesObj.imgArr.length)
						pageViewImagesObj.index = pageViewImagesObj.imgArr.length;
					if(pageViewImagesObj.index==7  || (pageViewImagesObj.index-pageViewImagesObj.startPage*3)%7==0){
						pageViewImagesObj.moveImageList("next");
					}
//					if(pageViewImagesObj.index==);
				}
				pageViewImagesObj.rechange(pageViewImagesObj.index);
				pageViewImagesObj.slideImage(pageViewImagesObj.index);
//				pageViewImagesObj.timer = window.setTimeout(pageViewImagesObj.auto, pageViewImagesObj.offset);
			});
}

pageViewImagesObj.getIndex = function(v) {
	for (var i = 0; i < pageViewImagesObj.target.length; i++) {
		if (pageViewImagesObj.target[i] == v)
			return i;
	}
}

pageViewImagesObj.rechange = function(loop) {
	if (!pageViewImagesObj.target)
		return;
	var id = 'thumb_' + pageViewImagesObj.target[loop];
	$('#thumbs li a.current').removeClass('current');
	$('#' + id).addClass('current');
}

pageViewImagesObj.auto = function() {
	pageViewImagesObj.index++;
	if (pageViewImagesObj.index >= pageViewImagesObj.imgArr.length) {
		pageViewImagesObj.index = 0;
	}
	pageViewImagesObj.rechange(pageViewImagesObj.index);
	pageViewImagesObj.slideImage(pageViewImagesObj.index);
//	pageViewImagesObj.timer = window.setTimeout(pageViewImagesObj.auto, pageViewImagesObj.offset);
}

// ---------------------------------
// 页面初始化
// ---------------------------------
pageViewImagesObj.initHtml = function() {
	
	pageViewImagesObj.htmlCode = "";
	// 传入的图片路径清单
	pageViewImagesObj.imgUrl = Utils.getUrlParamValue(document.location.href, "imgUrl");
	pageViewImagesObj.imgUrlRoot = decodeURIComponent(Utils.getUrlParamValue(document.location.href, "imgUrlRoot"));
	if (!pageViewImagesObj.imgUrl){
		pageViewImagesObj.htmlCode = "<h3 style='text-align:center'>暂无图片!</h3>";
		$('#focusImg').html(pageViewImagesObj.htmlCode);
		return;
	}
	pageViewImagesObj.imgArr = pageViewImagesObj.imgUrl.split(",");
	if (pageViewImagesObj.imgUrlRoot) {
		for (var i = 0; i < pageViewImagesObj.imgArr.length; i++) {
			pageViewImagesObj.imgArr[i] = pageViewImagesObj.imgUrlRoot + pageViewImagesObj.imgArr[i];
		}
	}
	pageViewImagesObj.htmlCode = "<div class='picshow'>" + "<div id=picarea>"
			+ "<div style='margin: 0px auto; width: 774px; height: 400px; overflow: hidden'>"
			+ "<div style='margin: 0px auto; width: 774px; height: 400px; overflow: hidden' id=bigpicarea>"
			+ "<P class=bigbtnPrev><SPAN id=big_play_prev></SPAN></P>"

	for (var i = 0; i < pageViewImagesObj.imgArr.length; i++) {
		pageViewImagesObj.htmlCode += "<div id=image_zdimg-" + i + " class=image><a href='#' target=_self><img rel='宗地图' " + "src='"
				+ pageViewImagesObj.imgArr[i] + "' width='772' height='398' onclick=pageViewImagesObj.showFullImage(this.src)></a> " + "<div class=word>"
				+ "<H3></H3></div></div>"
	}

	pageViewImagesObj.htmlCode += "<P class=bigbtnNext><SPAN id=big_play_next></SPAN></P></div></div>" + "<div id=smallpicarea>"
			+ "<div id=thumbs style='margin-left:42px;'>" + "<div style='padding:27px 35px 0 0; float:left;' class='first btnPrev'><img id=play_prev src='../base/skin/defalt/images/base/left.png'></div><div style='float:left;width:723px;overflow:hidden;position:relative;height:80px;'><UL id='imageUL' style='width:9999px;position:absolute;left:-0px;padding-left:0;'>";

	for (var ii = 0; ii < pageViewImagesObj.imgArr.length; ii++) {
		pageViewImagesObj.htmlCode += "<LI class=slideshowItem>" + "<A id=thumb_zdimg-" + ii + " href='#'><img src='"
				+ pageViewImagesObj.imgArr[ii] + "' width=90 height=60></A>" + "</LI>"
	}

	pageViewImagesObj.htmlCode += ""
			+ "</UL></div><div style='padding:27px 0 0 0; float:left;padding-left:35px;' class='last btnNext'><img id=play_next src='../base/skin/defalt/images/base/right.png'></div></div></div></div>" + "<script type='text/javascript'>"
			+ "pageViewImagesObj.target.splice(0, pageViewImagesObj.target.length);";
	for (var iii = 0; iii < pageViewImagesObj.imgArr.length; iii++) {
		pageViewImagesObj.htmlCode += "pageViewImagesObj.target.push('zdimg-" + iii + "');"
	}
	pageViewImagesObj.htmlCode += "</script>" + "</div>";
	$('#focusImg').html(pageViewImagesObj.htmlCode);
	
	pageViewImagesObj.startPage=0;//列表目前第一张图片
	if(pageViewImagesObj.imgArr.length>7){
		pageViewImagesObj.endIndex=0;//列表目前最后一张图片
	}else{
		pageViewImagesObj.endIndex=pageViewImagesObj.imgArr.length;
	}
	pageViewImagesObj.start();
}

//图片列表翻动显示没有显示出来的图片 uon=up 表示上翻，next表示下翻
pageViewImagesObj.moveImageList=function(uon){
	if (uon == "next") {
		if (pageViewImagesObj.imgArr.length * 105 - pageViewImagesObj.styleleft <= 5 * 105) {
		} else {
			$("#imageUL").css("left",
					"-" + (pageViewImagesObj.styleleft + 3 * 105) + "px");
			pageViewImagesObj.styleleft = pageViewImagesObj.styleleft + 3 * 105;
			pageViewImagesObj.startPage++;
		}
	}else{
		if ( pageViewImagesObj.styleleft ==0) {
		} else {
			$("#imageUL").css("left",
					"-" + (pageViewImagesObj.styleleft - 3 * 105) + "px");
			pageViewImagesObj.styleleft = pageViewImagesObj.styleleft - 3 * 105;
			pageViewImagesObj.startPage--;
		}
	}
}


//显示正确比例的图片
pageViewImagesObj.showFullImage = function(url) {
	var obj = {};
	// 地址
	obj.url = approot+"/showImage.html"
	// 参数
	obj.param = {
		imageUrl:url
	};
	// 窗口参数
	obj.feature = "dialogWidth=1024px;dialogHeight=768px";
	var returnValue = DialogModal(obj);
}


pageViewImagesObj.start = function() {
	$('div.word').css({
				opacity : 0.85
			});
//	pageViewImagesObj.auto();
	pageViewImagesObj.hookThumb();
	pageViewImagesObj.hookBtn();
	pageViewImagesObj.bighookBtn()
}

$(document).ready(function() {
	pageViewImagesObj.initHtml();
});


