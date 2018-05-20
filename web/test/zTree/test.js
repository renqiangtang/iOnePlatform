// -------------------------------
//
// -------------------------------
(function($) {
	$.fn.extend({
				tree : function(setting, zNodes) {
					$.fn.zTree.init(this, setting, zNodes);
					if (!this.hasClass('ztree'))
						this.addClass('ztree');
				}
			});
})(jQuery);

var pageObj = {};
pageObj.initPage = function() {
	var setting = {
		data : {
			key : {
				title : "t"
			},
			simpleData : {
				enable : true
			}
		},
		view : {
			fontCss : function getFontCss(treeId, treeNode) {
				return (!!treeNode.highlight) ? {
					color : "#A60000",
					"font-weight" : "bold"
				} : {
					color : "#333",
					"font-weight" : "normal"
				};
			}
		}
	};
	var zNodes = [{
				id : 1,
				pId : 0,
				name : "节点搜索演示 1",
				t : "id=1",
				open : true,
				iconOpen : "diy/1_open.png",
				iconClose : "diy/1_close.png"
			}, {
				id : 11,
				pId : 1,
				name : "关键字可以是名字",
				t : "id=11"
			}, {
				id : 12,
				pId : 1,
				name : "关键字可以是level",
				t : "id=12"
			}, {
				id : 13,
				pId : 1,
				name : "关键字可以是id",
				t : "id=13"
			}, {
				id : 14,
				pId : 1,
				name : "关键字可以是各种属性",
				t : "id=14"
			}, {
				id : 2,
				pId : 0,
				name : "节点搜索演示 2",
				t : "id=2",
				open : true
			}, {
				id : 21,
				pId : 2,
				name : "可以只搜索一个节点",
				t : "id=21"
			}, {
				id : 22,
				pId : 2,
				name : "可以搜索节点集合",
				t : "id=22"
			}, {
				id : 23,
				pId : 2,
				name : "搜我吧",
				t : "id=23"
			}, {
				id : 3,
				pId : 0,
				name : "节点搜索演示 3",
				t : "id=3",
				open : true
			}, {
				id : 31,
				pId : 3,
				name : "我的 id 是: 31",
				t : "id=31"
			}, {
				id : 32,
				pId : 31,
				name : "我的 id 是: 32",
				t : "id=32"
			}, {
				id : 33,
				pId : 32,
				name : "我的 id 是: 33",
				t : "id=33"
			}];
	$('#treeDiv').tree(setting, zNodes);
}
$(document).ready(pageObj.initPage);