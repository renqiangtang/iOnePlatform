package com.sysman.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.ParaMap;
import com.sysman.dao.MenuDao;
import com.base.utils.StrUtils;
import com.base.utils.MakeJSONData;
import com.base.utils.MakeJSONDataCallBack;
import com.sysman.dao.ParamsDao;

public class MenuService extends BaseService {
	private static int MENU_ITEM_CLICK_CHECK_CAKEY = 0; //菜单点击时是否检查cakey，0未获取设置，1需要检查，-1不需要检查
	
	public ParaMap getMenuRecurseTreeData(ParaMap inMap) throws Exception {
		MenuDao menuDao = new MenuDao();
		ParaMap out = menuDao.getMenuRecurseTreeData(inMap);
		//生成前台需要的JSON数据格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 1);
		custom.put(MakeJSONData.CUSTOM_CHILDREN_KEY_NAME, "children");
		custom.put(MakeJSONData.CUSTOM_LEVEL_FIELD_NAME, "menu_level");
		custom.put(MakeJSONData.CUSTOM_ID_FIELD_NAME, "id");
		custom.put(MakeJSONData.CUSTOM_PARENT_ID_FIELD_NAME, "parent_id");
		ParaMap fields = new ParaMap();
		fields.put("text", "name");
		fields.put("id", "id");
		fields.put("no", "no");
		fields.put("moduleId", "module_id");
		fields.put("parentId", "parent_id");
		//fields.put("menuPath", "menu_name_path");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List menu = MakeJSONData.makeItems(out, custom, new MenuTreeCallBack());
		out.clear();
		out.put("menu", menu);
		return out;
	}
	
	public ParaMap getMenuTreeData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String parentId = inMap.getString("parentId");
		int intRecurse = 0;
		if (inMap.containsKey("recurse"))
			intRecurse = inMap.getInt("recurse");
		MenuDao menuDao = new MenuDao();
		ParaMap out = null;
		out = menuDao.getMenuTreeData(moduleId, dataSetNo, parentId);
		//转换为前台需要的JSON数据格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, intRecurse);
		custom.put(MakeJSONData.CUSTOM_CHILDREN_KEY_NAME, "children");
		custom.put(MakeJSONData.CUSTOM_LEVEL_FIELD_NAME, "menu_level");
		custom.put(MakeJSONData.CUSTOM_ID_FIELD_NAME, "id");
		custom.put(MakeJSONData.CUSTOM_PARENT_ID_FIELD_NAME, "parent_id");
		custom.put(MakeJSONData.CUSTOM_CHILD_COUNT_FIELD_NAME, "child_count");
		ParaMap fields = new ParaMap();
		fields.put("text", "name");
		fields.put("id", "id");
		fields.put("no", "no");
		fields.put("moduleId", "module_id");
		fields.put("parentId", "parent_id");
		//fields.put("menuPath", "menu_name_path");
		if (inMap.containsKey("isexpand"))
			fields.put("isexpand", inMap.getString("isexpand"));
		else if (intRecurse == 1)
			fields.put("isexpand", "true");
		else
			fields.put("isexpand", "false");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List menu = MakeJSONData.makeItems(out, custom, new MenuTreeCallBack());
		out.clear();
		out.put("menu", menu);
		return out;
	}
	
	class MenuTreeCallBack implements MakeJSONDataCallBack {
		public void makeItem(ParaMap itemsMap, int record, ParaMap item) {
			String url = String.valueOf(itemsMap.getRecordValue(record, "url"));
			if (url != null && !url.equals("") && !url.equals("null")) {
				if (url.indexOf('?') >= 0)
					url += "&";
				else
					url += "?";
				url += "menuId=" + String.valueOf(itemsMap.getRecordValue(record, "id"))
					+ "&moduleId=" + String.valueOf(itemsMap.getRecordValue(record, "module_id"));
					//+ "&ms=" + System.currentTimeMillis();
			} else
				url = "";
			//将菜单中定义的模块参数添加到链接上去
			if (url != null && !url.equals("") && !url.equals("null")) {
				String moduleParams = String.valueOf(itemsMap.getRecordValue(record, "module_params"));
				if (!StrUtils.isNull(moduleParams)) {
					if (url.indexOf('?') >= 0) {
						Map moduleParamsMap = StrUtils.getParams(moduleParams, false, "=", "&");
						Iterator it = moduleParamsMap.keySet().iterator();
						while(it.hasNext()){
							String key = it.next().toString();
							url = StrUtils.setParamValue(url, key, String.valueOf(moduleParamsMap.get(key)), false, "=", "&");  
						}
					} else {
						url+= "?" + moduleParams;
					}
				}
			}
			item.put("url", url);
			if (itemsMap.getFields().indexOf("menu_name_path") >= 0) {
				String menuPath = String.valueOf(itemsMap.getRecordValue(record, "menu_name_path"));
				if (menuPath == null || menuPath.equals("") || menuPath.equalsIgnoreCase("null"))
					item.put("menuPath", "");
				else {
					menuPath = menuPath.substring(1, menuPath.length());
					item.put("menuPath", menuPath);
				}
			} else {
				item.put("menuPath", "");
			}
			if (!item.containsKey("children") && url.equals("")) {
				item.put("disable", true);
			}
			if (MenuService.MENU_ITEM_CLICK_CHECK_CAKEY == 0) {
				ParamsDao paramsDao = new ParamsDao();
				try {
					ParaMap paramsData = paramsDao.getParamsDataByNo("transCheckCABeforeOpenModule");
					if (paramsData.getInt("state") == 1 && paramsData.getInt("totalRowCount") >= 1
							&& String.valueOf(paramsData.getRecordValue(0, "lvalue")) == "0")
						MenuService.MENU_ITEM_CLICK_CHECK_CAKEY = -1;
					else
						MenuService.MENU_ITEM_CLICK_CHECK_CAKEY = 1;
				} catch(Exception e) {
					MenuService.MENU_ITEM_CLICK_CHECK_CAKEY = 1;
				}
			}
			if (MenuService.MENU_ITEM_CLICK_CHECK_CAKEY == 1)
				item.put("checkCa", "1");
		}
		
	}
	/**
	 * 得到菜单数据
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	
	public ParaMap getMenuData(ParaMap inMap)throws Exception{
		String parentId=inMap.getString("parentId");
		String moduleId=inMap.getString("moduleId");
		String dataSetNo=inMap.getString("dataSetNo");
		MenuDao dao = new MenuDao();
		ParaMap out = dao.getMenuData(moduleId,dataSetNo,parentId,"");
		List list=new ArrayList();
		ParaMap out1=new ParaMap();
		if(out!=null&&out.size()>0){//封装成树的JSON格式
			out1=this.getJson(out);
		}
		return out1;
	}
	/**
	 * 把获得的菜单数据封装成需要的JSON格式
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getJson(ParaMap inMap){
		ParaMap out=new ParaMap ();
		if(inMap!=null&&inMap.size()>0){
			List rsList=inMap.getList("rs");
			List list=new ArrayList();
			if(rsList!=null&&rsList.size()>0){//如果返回的结果集List不为空
				for(int a=0;a<rsList.size();a++){
					List recordsList=(List) rsList.get(a);
					if(recordsList!=null&&recordsList.size()>0){//获取每个字段的值
						String id="";
						String no="";
						String text="";
						String moduleId="";
						String parentId="";
						int childCount=0;
						for(int b=0;b<recordsList.size();b++){
							id=String.valueOf(recordsList.get(0));
							no=String.valueOf(recordsList.get(1));
							text=String.valueOf(recordsList.get(2));
							moduleId=String.valueOf(recordsList.get(3));
							parentId=String.valueOf(recordsList.get(4));
							if(String.valueOf(recordsList.get(5))!=null&&!"".equals(String.valueOf(recordsList.get(5)))){
								childCount=(int) Math.round(Double.parseDouble(String.valueOf(recordsList.get(5))));
							}
						}
						ParaMap out1=new ParaMap ();
						List childrenList=new ArrayList();
						out1.put("id", id);
						out1.put("no", no);
						out1.put("text", text);
						out1.put("moduleId", moduleId);
						out1.put("pid", parentId);
						out1.put("isexpand", false);
						if(childCount>0){//如果有字节，则加一个空的List,没有则是子节点的图标
							out1.put("children", childrenList);
							out1.put("isChildren", true);//用于在前台判断是否有子节点
						}else{
							out1.put("isChildren", false);
						}
						list.add(out1);
					}
				}
			}
			out.put("json", list);
		}
		return out;
	}
	
	/**
	 * 初始化新增菜单页面
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap initOperMenu(ParaMap inMap)throws Exception{
		String id=inMap.getString("id");
		String operType=inMap.getString("operType");//判断是新增还是修改
		String moduleId=inMap.getString("moduleId");
		String dataSetNo=inMap.getString("dataSetNo");
		ParaMap out=new ParaMap();
		MenuDao dao = new MenuDao();
		if("update".equals(operType)){
			out = dao.getMenuData(moduleId,dataSetNo,"",id);
		}else{
			out= dao.getMenuData(moduleId,"query_meun_max_turn","","");//查询最大排序码
			out.put("id", id);
		}
		out.put("moduleId", moduleId);
		out.put("operType", operType);
		return out;
	}
	/**
	 * 保存菜单数据
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap operMenu(ParaMap inMap)throws Exception{
		String moduleId=inMap.getString("moduleId");//从父页面传入的moduleId，用于查询SQL语句时用
		String id=inMap.getString("id");
		String parent_id=inMap.getString("parent_id");
		String no=inMap.getString("no");
		String name=inMap.getString("name");
		String module_id=inMap.getString("module_id");//和上边的moduleId不同，用于新增sys_menu时给sys_menu表中的module_Id插入的值
		String turn=inMap.getString("turn");
		String operType=inMap.getString("operType");//判断是新增还是修改
		ParaMap out=new ParaMap();
		out.put("id", id);
		out.put("parent_id", parent_id);
		out.put("no", no);
		out.put("name", name);
		out.put("module_id", module_id);
		out.put("module_params", inMap.getString("module_params"));
		out.put("turn", turn);
		DataSetDao dao=new DataSetDao();
		ParaMap returnOut=new ParaMap();//用于返回保存或新增的值
		returnOut = dao.updateData("sys_menu", "id", out, null);
		if("add".equals(operType)){//新增后的处理
			String addId=returnOut.getString("id");//新增后返回的ID
			String state=returnOut.getString("state");//新增后的状态值
			if("1".equals(state)&&addId!=null&&!"".equals(addId)&&!"null".equals(addId)){
				MenuDao menuDao = new MenuDao();
				returnOut=menuDao.getMenuData(moduleId,"select_meun","",addId);//根据ID查询数据
				returnOut.put("state", state);//把状态值继续往前传
			}
		}else if("update".equals(operType)&&id!=null&&!"".equals(id)&&!"null".equals(id)){
			String state=returnOut.getString("state");//修改后的状态值
			MenuDao menuDao = new MenuDao();
			returnOut=menuDao.getMenuData(moduleId,"select_meun","",id);//根据ID查询数据
			returnOut.put("state", state);//把状态值继续往前传
		}
		returnOut.put("operType", operType);
		return returnOut;
	}
	/**
	 * 删除菜单数据
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap delMenu(ParaMap inMap)throws Exception{
		ParaMap sqlParams = new ParaMap();
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "sys_menu_manager");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "del_meun");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		String id = inMap.getString("id");
		sqlParams.put("id", id);
		DataSetDao dao = new DataSetDao();
		ParaMap out = dao.executeSQL(sqlParams);
		return out;
	}
	/**
	 * 获取下拉模块树数据
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getModuleBox(ParaMap inMap)throws Exception{
		ParaMap out=new ParaMap();
		out.put("moduleNo", inMap.get("moduleNo"));
		out.put("dataSetNo", inMap.get("dataSetNo"));
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap returnOut=new ParaMap();
		returnOut=dataSetDao.queryData(out);
		//转换为前台需要的JSON数据格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 1);
		custom.put(MakeJSONData.CUSTOM_CHILDREN_KEY_NAME, "children");
		custom.put(MakeJSONData.CUSTOM_LEVEL_FIELD_NAME, "tree_level");
		custom.put(MakeJSONData.CUSTOM_ID_FIELD_NAME, "id");
		custom.put(MakeJSONData.CUSTOM_PARENT_ID_FIELD_NAME, "parent_id");
		custom.put(MakeJSONData.CUSTOM_CHILD_COUNT_FIELD_NAME, "child_count");
		ParaMap fields = new ParaMap();
		fields.put("text", "name");
		fields.put("id", "id");
		fields.put("parentId", "parent_id");
		fields.put("childCount", "child_count");
		fields.put("isexpand", "false");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(returnOut, custom);
		returnOut.clear();
		returnOut.put("data", items);
		return returnOut;
	}
	
	
}
