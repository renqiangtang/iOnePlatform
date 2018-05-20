var pageObj = {};
pageObj.valueTag = 'applyUnit';

pageObj.initPage1 = function() {
	$('#corpInfoTable').hide();
	$('#dtlTable').hide();
	$('#unionTable').hide();
	$('input:radio[name="style"]').click(function() {
		var radio = $('input:radio[name="style"]:checked');
		$('#corpInfoTable').hide();
		$('#dtlTable').hide();
		$('#unionTable').hide();
		$('#style0').removeClass("status_on");
		$('#style1').removeClass("status_on");
		$('#style2').removeClass("status_on");
		switch (radio.val()) {
		case '0':
			$('#style0').addClass("status_on");
			break;
		case '1':
			$('#style1').addClass("status_on");
			$('#unionTable').show();
			break;
		case '2':
			$('#style2').addClass("status_on");
			$('#corpInfoTable').show();
			$('#dtlTable').show();
			break;
		}
		Utils.autoIframeSize();
	});
	$('#btnUnionAdd')
			.click(
					function() {
						var html = '';
						var rowNum = $("#unionTableTBody").children("tr")
								.size();
						html += '<tr>';
						html += '  <td align="center" class="numClass">'
								+ (rowNum + 1) + '</td>';
						html += '	 <td><input type="text"  value="" class="input_long"/></td>';
						html += '  <td><input type="text"  value="" class="input_short" onkeyup="pageObj.unionKeyDown();"/>';
						html += '    ％</td>';
						html += '  <td align="center"><input type="button" value="删除" onclick="pageObj.trDel(this);" /></td>';
						html += '</tr>';
						$('#unionTableTBody').append(html);
						Utils.autoIframeSize();
					});
	$('#btnAddCorp')
			.click(
					function() {
						var html = '';
						html += '<tr>';
						html += '	 <td><input type="text"  value="" class="input_long" tag="name" memo="股东名称"/></td>';
						html += '	 <td><input type="text"  value="" class="input_long" tag="idno" memo="身份证号码/营业执照编号"/></td>';
						html += '    <td><input type="text"  value="" class="input_short" tag="percent" memo="股份比例" onkeyup="pageObj.corpKeyDown();"/>';
						html += '    ％</td>';
						html += '  <td align="center"><input type="button" value="删除" onclick="pageObj.trDel(this);" /></td>';
						html += '</tr>';
						$('#projectTableTBody').append(html);
						Utils.autoIframeSize();
					});
}
pageObj.allowUnion = function(){
	var cmd = new Command();
	cmd.module = "bidder";
	cmd.service = "Apply";
	cmd.method = 'getAllowUnion';
	cmd.u = pageObj.u;
	cmd.targetId = pageObj.targetId;
	cmd.success = function(data) {
		var allowUnion = data.allowUnion;
		if(allowUnion!=null && allowUnion!="" && allowUnion=="0"){
			$("#style1").hide();
		}
	}
	cmd.execute();
}
pageObj.initPage2 = function(data) {
	$('input:radio[name="style"]').hide();
	$('#style0').hide();
	$('#style1').hide();
	$('#style11').hide();
	$('#style2').hide();
	var cmd = new Command();
	cmd.module = "bidder";
	cmd.service = "Apply";
	cmd.method = 'getBillInfo';
	cmd.valueTag = pageObj.valueTag;
	cmd.licenseId = pageObj.licenseId;
	cmd.success = function(data) {
		switch (data.style) {
		case "0":
			$('#style0').show();
			break;
		case "1":
			$('#style1').show();
			var html = '';
			html += '<thead><tr>';
			html += '  <td align="center" class="numClass">序号</td>';
			html += '  <td>竞买人</td>';
			html += '  <td>交易份额(百分比)</td>';
			html += '</tr></thead>';
			for ( var i = 0; i < data.unit.length; i++) {
				html += '<tr>';
				html += '  <td align="center" class="numClass">' + (i + 1)
						+ '</td>';
				html += '	 <td>' + data.unit[i].name + '</td>';
				html += '    <td>' + data.unit[i].percent;
				html += '    ％</td>';
				html += '</tr>';
			}
			$('#unionTable').html(html);
			$('#unionTable').show();
			break;
		case "2":
			$('#style2').show();
			var company = data.company;
			var owners = company.owners;
			var inputs = $('#corpInfoTable input');
			inputs.each(function(i, ele) {
				$(ele).val(company[ele.id]);
			});
			var html = '';
			for ( var i = 0; i < owners.length; i++) {
				html += '<tr>';
				html += '	 <td>' + owners[i].name + '</td>';
				html += '	 <td>' + owners[i].idno + '</td>';
				html += '    <td>' + owners[i].percent + '％</td>';
				html += '</tr>';
			}
			$('#projectTableTBody').html(html);
			$('#corpTotal').html('100％');
			$('#opTd').hide();
			$('#corpInfoTable').show();
			$('#dtlTable').show();
			$('#corpInfoMsg1').hide();
			break;
		}
		Utils.autoIframeSize();
	};
	cmd.execute();
}

pageObj.getValue = function(cmd) {
	var obj = {};
	var radio = $('input:radio[name="style"]:checked');
	obj.style = radio.val();
	switch (obj.style) {
	case '0':
		break;
	case '1':
		var trs = $("#unionTableTBody").children("tr");
		var total = 0;
		obj.proportions = [];
		for ( var i = 0; i < trs.length; i++) {
			var nameEle = $(trs[i]).children().children('.input_long');
			var valueEle = $(trs[i]).children().children('.input_short');
			var name = nameEle.val().trim();
			var value = valueEle.val().trim();
			//
			if (!name) {
				DialogAlert("第" + (i + 1) + "行：联合竞买人为空!", parent);
				nameEle.focus();
				return false;
			}

			if (name == pageObj.userName) {
				DialogAlert("第" + (i + 1) + "行：无效联合竞买人!", parent);
				nameEle.focus();
				return false;
			}
			//
			if (!value) {
				DialogAlert("第" + (i + 1) + "行：联合竞买交易份额为空!", parent);
				valueEle.focus();
				return false;
			}
			//
			if (isNaN(value)) {
				DialogAlert("第" + (i + 1) + "行：联合竞买交易份额不为数字!", parent);
				valueEle.focus();
				return false;
			} else {
				var valuef = parseFloat(value);
				if (!valuef.nDigits(2)) {
					DialogAlert("第" + (i + 1) + "行：联合竞买交易份额最多保留两位小数!", parent);
					valueEle.focus();
					return false;
				}
				total = total.add(valuef);
				var tobj = {};
				tobj[name] = valuef;
				obj.proportions.push(tobj);
			}

		}
		if (total.sub(100) >= 0) {
			DialogAlert("联合竞买交易份额之和不等于100%!", parent);
			return false;
		}
		break;
	case '2':
		var inputs = $('#corpInfoTable input');
		inputs.each(function(i, ele) {
			obj[ele.id] = $(ele).val();
		});
		var trs = $("#projectTableTBody").children("tr");
		var total = 0;
		obj.proportions = [];
		for ( var i = 0; i < trs.length; i++) {
			inputs = $(trs[i]).children().children("input[tag]");
			var corp = {};
			for ( var j = 0; j < inputs.length; j++) {
				var ele = $(inputs[j]);
				var tag = ele.attr("tag");
				var value = ele.val().trim();
				var memo = ele.attr("memo");
				if (!value) {
					DialogAlert("第" + (i + 1) + "行：" + memo + "为空!", parent);
					return false;
				}
				if (tag == 'percent')
					if (isNaN(value)) {
						DialogAlert("第" + (i + 1) + "行：" + memo + "不为数字!",
								parent);
						ele.focus();
						return false;
					} else {
						var valuef = parseFloat(value);
						if (!valuef.nDigits(2)) {
							DialogAlert("第" + (i + 1) + "行：" + memo
									+ "最多保留两位小数!", parent);
							ele.focus();
							return false;
						}
						total = total.add(valuef);
					}
				corp[tag] = value;
			}
			obj.proportions.push(corp);
		}
		if (total.sub(100) != 0) {
			DialogAlert("股份比例之和不等于100%!", parent);
			return false;
		}
		break;
	}
	cmd[pageObj.valueTag] = JSON.stringify(obj);
	// alert(cmd[pageObj.valueTag]);
	return true;
}

pageObj.corpKeyDown = function() {
	var total = 0;
	$('#projectTableTBody tr td').children("input[tag=percent]").each(
			function(i, ele) {
				try {
					var cv = $(ele).val().trim();
					if (cv && !isNaN(cv)) {
						total = total.add(parseFloat(cv));
					}
				} catch (ex) {
				}
			});
	$('#corpTotal').html('合计:' + total + '％');
}

pageObj.unionKeyDown = function() {
	var total = 0;
	$('#unionTableTBody tr td').children(".input_short").each(function(i, ele) {
		try {
			var cv = $(ele).val().trim();
			if (cv && !isNaN(cv)) {
				total = total.add(parseFloat(cv));
			}
		} catch (ex) {
		}
	});
	$('#unionTotal').html('合计:' + total + '％');
	var initv = 100;
	$('#proportions0').html(initv.sub(total));
}

pageObj.trDel = function(ele) {
	$(ele).parents('tr').remove();
	$('#unionTableTBody tr').children(".numClass").each(function(i, ele) {
		$(ele).html(i + 1);
	});
	pageObj.unionKeyDown();
	pageObj.corpKeyDown();
	Utils.autoIframeSize();
}

$(document).ready(function() {
	pageObj.mode = Utils.getPageValue('mode');
	pageObj.licenseId = Utils.getPageValue('licenseId');
	pageObj.userName = parent.pageObj.userName;
	pageObj.u = getUserId();
	pageObj.targetId = window.parent.pageObj.targetId;
	switch (pageObj.mode) {
	case 'edit':
		pageObj.allowUnion();
		pageObj.initPage1();
		break;
	case 'view':
		pageObj.initPage2();
		break;
	}
	Utils.autoIframeSize();
	$('#testBtn').click(function() {
		pageObj.getValue({});
	});
});