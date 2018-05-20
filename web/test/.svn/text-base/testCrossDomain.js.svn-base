var pageObj = {};
pageObj.initPage = function() {
	// Response.AddHeader("Access-Control-Allow-Origin", "expertmeme.com");
	var url = 'http://expertmeme.com/cgi-bin/ci/getCompanyContactInfo.py?loc=%E6%88%90%E9%83%BD&kw=%E7%94%B5%E8%AF%9D%E5%AE%A2%E6%9C%8D&time=2012-09-01T00%3A00%3A00&&updated=1346976000&u=400&intro=1';
	// $.ajax({
	// type:'get',
	// url:url,
	// dataType:'json',
	// success:function(json){
	// alert(json.query.loc);
	// }
	// });
	$.getJSON(url, function(data) {
		alert(data.query.loc);
	});
}

$(document).ready(pageObj.initPage);