var pageDecodeOfferLogListObj = {};
pageDecodeOfferLogListObj.manageGrid = null;

// --------------------------------
// 上传文件
// --------------------------------
pageDecodeOfferLogListObj.uploadFile = function() {
	var form = $$("#decodeOfferLogForm");
	if (form.file.value == '') {
		alert('请上传附件!');
		return false;
	}
	form.action = approot + "/upload?encode=true&module=tradeland&service=Trade&method=getContent";
	form.method = "post";
	form.target = "hidden_frame";
	form.submit();
}

// --------------------------------
// 重置
// --------------------------------
pageDecodeOfferLogListObj.reset = function() {
	var form = $$("#decodeOfferLogForm");
	form.file.value = '';
}

// --------------------------------
// 回调方法
// --------------------------------
function formCallback(dataObj) {
	pageDecodeOfferLogListObj.reloadGrid(dataObj);
	var form = $$("#decodeOfferLogForm");
	form.file.value = '';
}

// --------------------------------
// 重新加载表格
// --------------------------------
pageDecodeOfferLogListObj.reloadGrid = function(dataObj) {
	var gridData = {};
	gridData.Rows = [];
	if (dataObj != null && dataObj.length > 0) {
		for ( var i = 0; i < dataObj.length; i++) {
			var edStr = dataObj[i];
			var decodeStr = '';
			try {
				if(typeof(edStr)=="string"){
					edStr = edStr.replace(/MMnnMM/g, "\n");
					decodeStr = pageDecodeOfferLogListObj.decode(edStr);
					if (decodeStr) {
						gridData.Rows[i] = JSON.parse(decodeStr);
						gridData.Rows[i].enCode='已加密';
						gridData.Rows[i].sortNum=i+1;
					} 
				}else{
					gridData.Rows[i] = edStr;
					gridData.Rows[i].enCode='无加密';
					gridData.Rows[i].sortNum=i+1;
				}
				
			} catch (ex) {
				alert(i);
				alert("异常");
			}

		}
	} else {
		DialogAlert("出价记录无效!");
		return;
	}
	var manager = pageDecodeOfferLogListObj.manageGrid;
	manager.loadData(gridData);
}

// --------------------------------
// ca解密
// --------------------------------
pageDecodeOfferLogListObj.decode = function(enStr) {
	var decodeStr = ca.decode(enStr);
	return decodeStr;
}

$(document).ready(function() {
	// --------------------------------
	// 初始化表格
	// --------------------------------
	pageDecodeOfferLogListObj.manageGrid = $("#offerLogGrid").ligerGrid({
		columns : [ {
			display : '序号',
			name : 'sortNum',
			isSort : false,
			width : '6%',
			align : 'center'
		},{
			display : '标的名称',
			name : 'targetName',
			isSort : false,
			width : '25%',
			align : 'center'
		}, {
			display : '竞买(卖)人',
			name : 'name',
			isSort : false,
			width : '25%',
			align : 'center'
		}, {
			display : '出价',
			name : 'price',
			isSort : false,
			width : '17%',
			align : 'right'
		}, {
			display : '时间',
			name : 'time',
			isSort : false,
			width : '17%',
			align : 'center'
		},
		{
			display : '加密情况',
			name : 'enCode',
			isSort : false,
			width : '10%',
			align : 'center'
		}],
		isScroll : true, // 是否滚动
		frozen : false,// 是否固定列
		usePager : false,
		showTitle : false,
		fixedCellHeight : false,
		width : '970'
	});
})
