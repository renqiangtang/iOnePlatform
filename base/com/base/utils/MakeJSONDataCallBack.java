package com.base.utils;

/**
 * 通用生成树结构JSON数据时的回调接口
 * huafc
 * 2012-04-20
 */
public interface MakeJSONDataCallBack {
	//生成Item项时回调，生成树时有特殊需求时实现此方法，参考MenuService中的调用
	public void makeItem(ParaMap itemsMap, int record, ParaMap item);
}
