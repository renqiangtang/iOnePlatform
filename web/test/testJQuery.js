var pageObj = {};

function sleep(numberMillis) {
	var now = new Date();
	var exitTime = now.getTime() + numberMillis;
	while (true) {
		now = new Date();
		if (now.getTime() > exitTime)
			return;
	}
}

function sleep2() {
	while (true) {
		var childNodes = $$('#notice_type').childNodes;
		if (childNodes.length > 1)
			return;
	}
}

//$.fn.extend({
//	　　html2:function(text){
//			var outerHtml=$(this)[0].outerHTML;
//			var re=/\>[^\<]*\</g;
//			var sss=outerHtml.replace(re,'>'+text+'<');
//			$(this).replaceWith(sss);
//		}
//	});

pageObj.initPage = function() {
	// var orginHtml=$$('#notice_type').outerHTML;
	var html='';
	for(var i=0;i<100;i++)
		html += '<option value="'+i+'">'+i+'</option>';
	
	$('#notice_type').html2(html);
	//$("#notice_type").val('78');
	
	$$('notice_type').options[5].selected='selected';
	
	
	

}

pageObj.initPage1 = function() {
	for ( var i = 0; i < 10; i++) {
		var option = document.createElement('option');
		option.value = i + "";
		option.innerText = i + "" + i;
		$$('#notice_type').appendChild(option);
	}
	$$("#notice_type").value = '5';
	// $("#notice_type").val('3');
}

$(document).ready(function() {
	//pageObj.initPage();
	var arr = ['aaa','bbb',{'name':'name1','age':12}];
	var str = JSON.stringify(arr);
	alert(str);
});