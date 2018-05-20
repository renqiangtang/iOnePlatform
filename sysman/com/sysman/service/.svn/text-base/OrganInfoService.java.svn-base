package com.sysman.service;

import java.util.Iterator;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;

public class OrganInfoService extends BaseService {
	
	public ParaMap submitInfo(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		try{
			String organId = inMap.getString("organId");
			if(StrUtils.isNull(organId)){
				out.put("message", "提交失败");
				return out;
			}
			byte[] logo = StrUtils.isNotNull(inMap.getString("logo"))?inMap.getBytes("logo"):null;
			byte[] head_left = StrUtils.isNotNull(inMap.getString("head_left"))?inMap.getBytes("head_left"):null;
			String cnabbr = inMap.getString("cnabbr");
			String enabbr = inMap.getString("enabbr");
			String bidderMainHtml = inMap.getString("bidderMainHtml");
			String bankMainHtml = inMap.getString("bankMainHtml");
			String userMainHtml = inMap.getString("userMainHtml");
			String copyRight = inMap.getString("copyRight");
			
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap sqlParams = new ParaMap();
			
			//删除logo文件
			String logo_path = "";
			sqlParams.clear();
			sqlParams.put("ref_table_name", "sys_organ");
			sqlParams.put("ref_id", organId);
			sqlParams.put("field_no", "logo");
			ParaMap logoMap = dataSetDao.querySimpleData("sys_extend", sqlParams);
			if(logoMap.getSize()>0){
				logo_path = (String)logoMap.getRecordValue(0, "field_value");
				if(StrUtils.isNotNull(logo_path) && logo!=null && logo.length>0)
						this.delFileFromFileRoot(logo_path);
			}
		
			//删除左边横幅文件
			String head_left_path = "";
			sqlParams.clear();
			sqlParams.put("ref_table_name", "sys_organ");
			sqlParams.put("ref_id", organId);
			sqlParams.put("field_no", "head_left");
			ParaMap head_leftMap = dataSetDao.querySimpleData("sys_extend", sqlParams);
			if(head_leftMap.getSize()>0){
				head_left_path = (String)head_leftMap.getRecordValue(0, "field_value");
				if(StrUtils.isNotNull(head_left_path) && head_left!=null && head_left.length>0)
					this.delFileFromFileRoot(head_left_path);
			}
			
			//删除表设置
			sqlParams.clear();
			sqlParams.put("ref_table_name", "sys_organ");
			sqlParams.put("ref_id", organId);
			dataSetDao.deleteSimpleData("sys_extend", sqlParams);
			
			
			//保存入库
			if(logo!=null && logo.length>0){
				String logo_file = organId+"/logo";
				this.writeFileToFileRoot(logo_file, logo);
				this.saveOrganInfo(dataSetDao, organId, "logo", logo_file,"file");
			}else{
				this.saveOrganInfo(dataSetDao, organId, "logo", logo_path,"file");
			}
			
			if(head_left!=null && head_left.length>0){
				String head_left_file = organId+"/head_left";
				this.writeFileToFileRoot(head_left_file, head_left);
				this.saveOrganInfo(dataSetDao, organId, "head_left", head_left_file,"file");
			}else{
				this.saveOrganInfo(dataSetDao, organId, "head_left", head_left_path,"file");
			}
			
			Iterator it = inMap.keySet().iterator();
			while (it.hasNext()){
				   String key=(String)it.next();
				   if (!key.equals("organId") && !key.equals("logo")&& !key.equals("head_left")&& !key.equals("module")&& !key.equals("service")&& !key.equals("method")){ 
					   String value = (String)inMap.get(key);
					   out = this.saveOrganInfo(dataSetDao, organId, key, value,"String");
				   }
			}
			
			if(out.getString("state")!=null && "1".equals(out.getString("state"))){
				out.put("message", "提交成功");
			}else{
				out.put("message", "提交失败");
			}
		}catch(Exception e){
			e.printStackTrace();
			out.put("message", "提交失败");
		}
		return out;
		
	}
	
	public ParaMap saveOrganInfo(DataSetDao dataSetDao,String organId,String no , String value , String valuesType)throws Exception {
		ParaMap keyData = new ParaMap();
		keyData.put("id", "");
		keyData.put("ref_table_name", "sys_organ");
		keyData.put("ref_id", organId);
		keyData.put("field_no", no);
		keyData.put("field_value", value);
		return dataSetDao.updateData("sys_extend","id", keyData);
	}
	
	public ParaMap getOrganInfo(ParaMap inMap) throws Exception {
		String organId = inMap.getString("organId");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("ref_table_name", "sys_organ");
		sqlParams.put("ref_id", organId);
		ParaMap out = dataSetDao.querySimpleData("sys_extend", sqlParams);
		return out;
	}
}
