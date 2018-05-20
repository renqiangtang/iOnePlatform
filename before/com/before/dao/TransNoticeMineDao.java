package com.before.dao;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.trademine.engine.EngineDao;
import com.base.service.BaseDownloadService;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.sysman.dao.ConfigDao;

/**
 * 土地管理DAO
 * 
 * @author chenl 2012-05-15
 */
public class TransNoticeMineDao extends BaseDao {
	
	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return 获取公告信息列表
	 * @throws Exception
	 */
	public ParaMap getNoticeListData(ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleNo", "trans_prepare_notice_manage01");
		sqlParams.put("dataSetNo", "get_trans_notice_list");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	
	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return 获取公告信息
	 * @throws Exception
	 */
	public ParaMap getNoticeInfo(String noticeId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", noticeId);
		keyData.put("is_valid", 1);
		ParaMap noticeMap = dataSetDao.querySimpleData("trans_notice", keyData);
		return noticeMap;
	}
	
	
	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return  公告关联标的
	 * @throws Exception
	 */
	public ParaMap getNoticeTargetData(String noticeId,String page , String pagesize) throws Exception {
		ParaMap keyData = new ParaMap();
		keyData.put("moduleNo", "trans_prepare_notice_manage01");
		keyData.put("dataSetNo", "query_notice_target_list");
		keyData.put("noticeId",noticeId);
		if(StrUtils.isNotNull(page) && StrUtils.isNotNull(pagesize)){
			keyData.put(DataSetDao.SQL_PAGE_INDEX, Integer.parseInt(page));
			keyData.put(DataSetDao.SQL_PAGE_ROW_COUNT, Integer.parseInt(pagesize));
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(keyData);
		return result;
	}
	
	
	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return  公告关联标的
	 * @throws Exception
	 */
	public ParaMap getNoticeTargetData(String noticeId,String sql) throws Exception {
		ParaMap keyData = new ParaMap();
		keyData.put("moduleNo", "trans_prepare_notice_manage01");
		keyData.put("dataSetNo", "query_notice_target_list_condition");
		keyData.put("noticeId",noticeId);
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", sql);
		keyData.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(keyData);
		return result;
	}
	
	
	public ParaMap getNoticeByTargetId(String targetId , String noticeType)throws Exception{
		ParaMap keyData = new ParaMap();
		keyData.put("moduleNo", "trans_prepare_notice_manage01");
		keyData.put("dataSetNo", "query_notice_by_targetId");
		keyData.put("targetId",targetId);
		keyData.put("noticeType",noticeType);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(keyData);
		return result;
	}
	
	
	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return  公告关联标的
	 * @throws Exception
	 */
	public ParaMap getNoticeOtherTargetData(ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleNo", "trans_prepare_notice_manage01");
		sqlParams.put("dataSetNo", "query_notice_other_target_list");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getNoticeAccountList(String noticeId) throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_notice_manage01");
		sqlParams.put("dataSetNo", "notice_bank_account_list");
		sqlParams.put("noticeId", noticeId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	
	public ParaMap saveNotice(ParaMap sqlParams) throws Exception{
		ParaMap result = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap format = new ParaMap();
		format.put("notice_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		String id = sqlParams.getString("noticeId");
		String mode = sqlParams.getString("mode");
		int noticeStatus = 0;
		if(StrUtils.isNotNull(id)){
			sqlParams.put("id", id);
			ParaMap noticeMap = getNoticeInfo(id);
			if(noticeMap.getSize()>0){
				noticeStatus = Integer.parseInt(String.valueOf(noticeMap.getRecordValue(0, "status")));
				String business_type = String.valueOf(noticeMap.getRecordValue(0, "business_type"));
				if(noticeStatus !=0 && noticeStatus !=4){
					if(StrUtils.isNull(mode) || !"update".equals(mode)){
						result.put("id", id);
						result.put("state",0);
						result.put("message", "公告已审核或已发布，不能修改。");
						result.put("id", id);
						return result;
					}
				}
				if(sqlParams.get("business_type")!=null && !business_type.equals(sqlParams.getString("business_type"))){
					result.put("id", id);
					result.put("state",0);
					result.put("message", "公告不能修改交易类型。");
					result.put("id", id);
					return result;
				}
				result=dataSetDao.updateData("TRANS_NOTICE", "id", sqlParams,format);
				if(result.get("id")==null){
					result.put("id", id);
				}
			}else{
				sqlParams.put("id", null);
				result=dataSetDao.updateData("TRANS_NOTICE", "id", sqlParams,format);
			}
		}else{
			sqlParams.put("id", null);
			result=dataSetDao.updateData("TRANS_NOTICE", "id", sqlParams,format);
			id = result.getString("id");
		}
		if(StrUtils.isNotNull(sqlParams.getString("notice_type")) && sqlParams.getString("notice_type").equals("1")){
			String targetIds = sqlParams.getString("targetIds");
			this.saveSupplementTarget(id , targetIds , sqlParams.getString("create_user_id"));
		}
		return result;
	}
	
	
//	public ParaMap checkNotice(String noticeIds , String userId) throws Exception{
//		ParaMap result = new ParaMap();
//		String message = "";
//		List noticeIdList = new ArrayList();
//		if(StrUtils.isNull(noticeIds)){
//			result.put("state", 0);
//			result.put("message", "审核失败！请选择有效的公告。");
//			return result;
//		}
//		String[] ids = noticeIds.split(",");
//		if(ids==null || ids.length<=0){
//			result.put("state", 0);
//			result.put("message", "审核失败！请选择有效的公告。");
//			return result;
//		}
//		//循环校验是否所有的公告都可以审核
//		for(int i = 0 ; i< ids.length ; i ++ ){
//			String noticeId = ids[i];
//			if(StrUtils.isNull(noticeId)){
//				continue;
//			}
//			//得到公告信息
//			ParaMap noticeMap = getNoticeInfo(noticeId);
//			if(noticeMap.getSize()>0){
//				int status = Integer.parseInt(String.valueOf(noticeMap.getRecordValue(0, "status")));
//				int noticeType = Integer.parseInt(String.valueOf(noticeMap.getRecordValue(0, "notice_type")));
//				String name = String.valueOf(noticeMap.getRecordValue(0, "name"));
//				if(status==0){
//					if(noticeType==0){
//						ParaMap judgeMap = this.judgeCheckNormalNotice(noticeId, name);
//						if(!"1".equals(judgeMap.getString("state"))){
//							message = message + judgeMap.getString("message")+"<br>";
//						}else{
//							noticeIdList.add(noticeId);
//						}
//					}else{
//						noticeIdList.add(noticeId);
//					}
//				}else{
//					message = message + "公告：'"+name+"'状态错误。<br>";
//				}
//			}
//		}
//		if(StrUtils.isNotNull(message)){
//			result.put("state", 0);
//			result.put("message", message);
//		}else{
//			if(noticeIdList!=null && noticeIdList.size()>0){
//				for(int i = 0 ; i < noticeIdList.size() ; i ++ ){
//					result = this.doCheckNotice((String)noticeIdList.get(i), userId);
//					if(!"1".equals(result.getString("state"))){
//						throw new Exception("公告审核失败！");
//					}else{
//						result.put("state", 1);
//						result.put("message", "公告审核成功！");
//					}
//				}
//			}else{
//				result.put("state", 0);
//				result.put("message", "公告审核失败！请选择有效的公告。");
//			}
//		}
//		
//		return result;
//	}
	
	/**
	 * 
	 * @param noticeIds
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public ParaMap checkNotice(String noticeIds , String userId,int isCon) throws Exception{
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		String message = "";
		String shtext="审核";
		if(isCon==2){
			shtext="发布";
		}
		if(StrUtils.isNotNull(noticeIds)){
			String ids[] = noticeIds.split(",");
			if(ids!=null && ids.length > 0){
				for(int i = 0 ; i < ids.length ; i ++){
					String noticeId = ids[i];
					if(StrUtils.isNull(noticeId)){
						throw new Exception( shtext+"公告失败！公告参数错误。");
					}
					keyData.clear();
					keyData.put("id", noticeId);
					keyData.put("is_valid", 1);
					ParaMap noticeMap = dataSetDao.querySimpleData("trans_notice", keyData);
					if(noticeMap == null || noticeMap.getSize()<=0){
						throw new Exception(shtext+"公告失败！公告参数错误。");
					}
					
					ParaMap judgeMap = judgeNoticePublish(noticeMap,isCon);
					if(!"1".equals(judgeMap.getString("state"))){
						if(StrUtils.isNotNull(judgeMap.getString("message"))){
							throw new Exception(judgeMap.getString("message"));
						}else{
							throw new Exception(shtext+"公告失败！");
						}
					}
					String id = noticeMap.getRecordString(0, "id");
					int status = Integer.parseInt(String.valueOf(noticeMap.getRecordValue(0, "status")));
					if(isCon==2 && status!=5){//进行发布，但是不是已通过的公告是不允许发布的
						throw new Exception(shtext+"公告失败！必须审核通过的公告才能发布！");
					}
					if(status!=3 && isCon!=2){//审核必须是状态为3的公告
						throw new Exception(shtext+"公告失败！公告状态必须是审核中！");
					}
					String name = String.valueOf(noticeMap.getRecordValue(0, "name"));
					String businessType = String.valueOf(noticeMap.getRecordValue(0, "business_type"));
					String noticeType = String.valueOf(noticeMap.getRecordValue(0, "notice_type"));
					ParaMap result = this.doCheckNotice(noticeId,name,businessType,userId,noticeType,isCon);
					if(!"1".equals(result.getString("state"))){
						throw new Exception(result.getString("message"));
					}else{
						out.put("state", 1);
						out.put("message", shtext+"成功！");
					}
				}
			}
		}else{
			out.put("state", 0);
			out.put("message", shtext+"公告失败！未找到有效的公告。");
		}
		return out;
	}
	
	/**
	 * 撤销提交公告
	 * @param noticeIds
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public ParaMap cancelNotice(String noticeIds) throws Exception{
		//查询公告状态
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap out=new ParaMap();
		if(StrUtils.isNotNull(noticeIds)){
			String ids[] = noticeIds.split(",");
			if(ids!=null && ids.length > 0){
				for(int i = 0 ; i < ids.length;i++){
					ParaMap sqlMap=new ParaMap();
					sqlMap.put("id", ids[i]);
					ParaMap pm=dataSetDao.querySimpleData("trans_notice", sqlMap);
					if(pm.getRecordInt(0, "status")==3){//只有状态为3的状态可以撤销提交
						ParaMap updateMap=new ParaMap();
						updateMap.put("id", ids[i]);
						updateMap.put("status", 0);
						out=dataSetDao.updateData("trans_notice", "id", updateMap);
						if(out.getInt("state")!=1){
							throw new Exception("撤销提交公告失败！修改状态错误");
						}
					}else{
						throw new Exception("撤销提交公告失败！公告状态错误！");
					}
							
				}
			}
		}
		out.clear();
		out.put("state", 1);
		return out;
	}
	
	/**
	 * 审核公告
	 * @param noticeId
	 * @param name
	 * @param noticeType
	 * @return
	 * @throws Exception
	 */
	public ParaMap doCheckNotice(String noticeId , String name , String businessType,String userId , String noticeType,int isCon)throws Exception{
		ParaMap out = new ParaMap();
		String shtext="审核";
		if(isCon==2){
			shtext="发布";
		}
		//获取lobid,filename
		ParaMap syslob = getNoticeSysLobId(noticeId,businessType);
		List list=new ArrayList();
		list=syslob.getList("rs");
		List syslobList=new ArrayList();
		String lobid="";
		String filename="";
		if(list.size()>0){
			syslobList=(List)list.get(0);
			lobid=syslobList.get(0).toString();
			filename=syslobList.get(1).toString();
		}else{
			out.put("state", 0);
			out.put("message", "公告：'"+name+"'"+shtext+"失败！无有效附件。");
			return out;
		}
		String pdfFileName=filename.substring(filename.lastIndexOf(".")+1);
		if(!pdfFileName.equalsIgnoreCase("pdf")){
			out.put("state", 0);
			out.put("message", "公告：'"+name+"'"+shtext+"审核失败！公告不是pdf格式。");
			return out;
		}
		BaseDownloadService baseDownloadService=new BaseDownloadService();
		String filePath=baseDownloadService.getFilePath(lobid);
		baseDownloadService.apartNotice(lobid, noticeId, filePath);
		
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", noticeId);
//		keyData.put("status", 2);
		keyData.put("status", isCon);//要求增加审核环节，不通过4，通过5
		keyData.put("publish_user_id", userId);
		keyData.put("publish_date", DateUtils.getStr(DateUtils.now()));
		ParaMap formatMap = new ParaMap();
		formatMap.put("publish_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		ParaMap result = dataSetDao.updateData("trans_notice", "id", keyData,formatMap);
		
		keyData.clear();
		keyData.put("moduleNo", "trans_prepare_notice_manage01");
		keyData.put("dataSetNo", "query_notice_target_list");
		keyData.put("noticeId",noticeId);
		ParaMap targetMap = dataSetDao.queryData(keyData);
		if(isCon==2){
			if(noticeType!=null && noticeType.equals("0")){
				if(targetMap.getSize()>0){
					for(int i = 0 ; i < targetMap.getSize() ; i++){
						String targetId = (String)targetMap.getRecordValue(i, "id");
						keyData.clear();
						keyData.put("status", "3");
						keyData.put("id", targetId);
						dataSetDao.updateData("TRANS_TARGET", "id", keyData,null);
						//流程
						EngineDao dao = new EngineDao();
						dao.createTemplateInfo(targetId);
						
					}
				}
			}
		}
		
		out.put("state", 1);
		if(isCon==2){
			out.put("message", "公告：'"+name+"'已发布成功！");
		}else{
			out.put("message", "公告：'"+name+"'已提交审核！");
		}
		return out;
	}
	
	
	/**
	 * 普通公告审核条件
	 * @param noticeId
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public ParaMap judgeCheckNormalNotice(String noticeId , String name)throws Exception{
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		//公告所选账号
		ParaMap accontMap = getNoticeAccountList(noticeId);
		if(accontMap.size()<=0){
			out.put("message", "公告：'"+name+"'未选择银行账号。") ;
			out.put("state", 0);
			return out;
		}
		//标的信息
		ParaMap targetMap = getNoticeTargetData(noticeId,null,null);
		if(targetMap.getSize()<=0){
			out.put("message", "公告：'"+name+"'缺失标的信息或者银行信息。") ;
			out.put("state", 0);
			return out;
		}
		for(int k = 0 ; k < targetMap.getSize() ; k++){
			String targetId = (String)targetMap.getRecordValue(k, "id");
			String targetNo = (String)targetMap.getRecordValue(k, "no");
			ParaMap keyData = new ParaMap();
			
			String result = targetMustIsNull(targetMap , k);
			if(StrUtils.isNotNull(result)){
				out.put("message", "公告：'"+name+"'关联的标的'"+targetNo+"'竞买时间不能为空。") ;
				out.put("state", 0);
				return out;
			}
			
//			keyData.clear();
//			keyData.put("target_id", targetId);
//			ParaMap tradeMap=dataSetDao.querySimpleData("trans_target_multi_trade", keyData , " turn asc");
//			if(tradeMap==null ||  tradeMap.getSize()<=0){
//				out.put("message", "公告：'"+name+"'审核失败！关联的标的'"+targetNo+"'没有交易属性信息。") ;
//				out.put("state", "0");
//				return out;
//			}
			keyData.clear();
			keyData.put("target_id", targetId);
			keyData.put("is_valid", 1);
			ParaMap earnesMap=dataSetDao.querySimpleData("trans_target_earnest_money", keyData);
			if(earnesMap.getSize()<=0){
				out.put("message", "公告：'"+name+"'关联的标的'"+targetNo+"'没有保证金信息。") ;
				out.put("state", 0);
				return out;
			}
			for(int i = 0 ; i < earnesMap.getSize() ; i++){
				String crrency = String.valueOf(earnesMap.getRecordValue(i, "currency"));
				boolean isIn = false;
				for(int j = 0 ; j<accontMap.getSize() ; j ++){
					String accCurrency = String.valueOf(accontMap.getRecordValue(j, "currency"));
					if(crrency.equals(accCurrency)){
						isIn = true;
						break;
					}
				}
				if(!isIn){
					out.put("message", "公告：'"+name+"'未找到关联标的'"+targetNo+"'"+crrency+"保证金的可入款账号。") ;
					out.put("state", 0);
					return out;
				}
			}
			
		}
		out.put("state", 1);
		return out;
		
	}
	
	
	public String targetMustIsNull(ParaMap targetMap , int i){
		String result = null;
		if(targetMap.getRecordValue(i, "business_type")==null){
			result =  "业务类型;";
			return result;
		}
		if(targetMap.getRecordValue(i, "trans_type")==null){
			result =  "交易方式;";
			return result;
		}
		if(targetMap.getRecordValue(i, "begin_price")==null){
			result =  "起始价;";
			return result;
		}
		if(targetMap.getRecordValue(i, "price_step")==null){
			result =  "增价幅度;";
			return result;
		}
		if(targetMap.getRecordValue(i, "begin_notice_time")==null){
			result =  "公告开始时间;";
			return result;
		}
		if(targetMap.getRecordValue(i, "end_notice_time")==null){
			result =  "公告截止时间;";
			return result;
		}
		if(targetMap.getRecordValue(i, "begin_apply_time")==null){
			result =  "竞买申请开始时间;";
			return result;
		}
		if(targetMap.getRecordValue(i, "end_apply_time")==null){
			result =  "竞买申请截止时间;";
			return result;
		}
		if(targetMap.getRecordValue(i, "begin_earnest_time")==null){
			result =  "保证金开始时间;";
			return result;
		}
		if(targetMap.getRecordValue(i, "end_earnest_time")==null){
			result =  "保证金截止时间;";
			return result;
		}
		if(targetMap.getRecordValue(i, "begin_list_time")==null){
			result =  "挂牌开始时间;";
			return result;
		}
		if(targetMap.getRecordValue(i, "end_list_time")==null){
			result =  "挂牌截止时间;";
			return result;
		}
		if(targetMap.getRecordValue(i, "begin_focus_time")==null){
			result =  "集中报价开始时间;";
			return result;
		}
		if(targetMap.getRecordValue(i, "end_focus_time")==null){
			result =  "集中报价截止时间;";
			return result;
		}
		if(targetMap.getRecordValue(i, "begin_limit_time")==null){
			result =  "拍卖(限时竞价、现场拍卖)开始时间;";
			return result;
		}
		return result ;
	}
	
	
	public ParaMap doBoundTargets(String noticeId , String targetIds , String userId )throws Exception{
		ParaMap out = new ParaMap();
		ParaMap notice = this.getNoticeInfo(noticeId);
		if(notice == null || notice.getSize()<=0){
			out.put("state", 0);
			out.put("message", "添加标的失败！请选择有效的公告。");
			return out;
		}
		int status = Integer.parseInt(String.valueOf(notice.getRecordValue(0, "status")));
		String noticeOrganId = String.valueOf(notice.getRecordValue(0, "organ_id"));
		if(status == 0 || status==4){
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap keyData = new ParaMap();
			String message = "";
			keyData.clear();
			ParaMap relMap = dataSetDao.querySimpleData("trans_notice_target_rel", keyData,null,"target_id");
			List targetList = relMap.getList("rs");
			if(StrUtils.isNotNull(targetIds)){
				String[] ids = targetIds.split(",");
				for (int i = 0; i < ids.length; i++) {
					String targetId = ids[i];
					if (StrUtils.isNotNull(targetId)) {
						keyData.clear();
						keyData.put("id", targetId);
						keyData.put("is_valid", 1);
						ParaMap targetMap = dataSetDao.querySimpleData("trans_target", keyData);
						if(targetMap.getSize()>0){
							int target_status = Integer.parseInt(String.valueOf(targetMap.getRecordValue(0, "status")));
							String targetName = String.valueOf(targetMap.getRecordValue(0, "no"));
							String targetTransOrganId = String.valueOf(targetMap.getRecordValue(0, "trans_organ_id"));
							if(noticeOrganId == null || targetTransOrganId == null || !targetTransOrganId.equals(noticeOrganId)){
								throw new Exception("标的：'"+targetName+"'添加失败！该标的发布单位不是本单位。");
							}
							if(target_status!=2){
								throw new Exception("标的：'"+targetName+"'添加失败！状态错误。");
							}
							if(targetList.indexOf(targetId)>=0){
								throw new Exception("标的：'"+targetName+"'添加失败！已关联公告。");
							}
							keyData.clear();
							keyData.put("id", null);
							keyData.put("notice_id", noticeId);
							keyData.put("target_id", targetId);
							keyData.put("create_user_id", userId);
							ParaMap result = dataSetDao.updateData("TRANS_NOTICE_TARGET_REL", "id", keyData);
							if("1".equals(result.getString("state"))){
								out.put("state", 1);
								out.put("message", "标的添加成功！");
							}else{
								throw new Exception("标的：'"+targetName+"'添加失败！");
							}
						}
					}
				}
			}
		}else if(status == -1){
			out.put("state", 0);
			out.put("message", "添加标的失败！请选择有效的公告。");
		}else if(status == 1){
			out.put("state", 0);
			out.put("message", "添加标的失败！公告已审核，不能添加标的。");
		}else if(status == 2){
			out.put("state", 0);
			out.put("message", "添加标的失败！公告已发布，不能添加标的。");
		}else{
			out.put("state", 0);
			out.put("message", "添加标的失败！公告状态错误，不能添加标的。");
		}
		return out;
	}
	
	
	public ParaMap undoBoundTargets(String noticeId , String targetIds)throws Exception{
		ParaMap out = new ParaMap();
		int status = this.getNoticeStatus(noticeId);
		if(status == 0 || status == 4){
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap keyData = new ParaMap();
			String message = "";
			if(StrUtils.isNotNull(targetIds)){
				String[] ids = targetIds.split(",");
				for (int i = 0; i < ids.length; i++) {
					String targetId = ids[i];
					if (StrUtils.isNotNull(targetId)) {
						keyData.clear();
						keyData.put("id", targetId);
						keyData.put("is_valid", 1);
						ParaMap targetMap = dataSetDao.querySimpleData("trans_target", keyData);
						String targetName = targetMap.getSize()>0?String.valueOf(targetMap.getRecordValue(0, "no")):"";
						keyData.clear();
						keyData.put("notice_id", noticeId);
						keyData.put("target_id", targetId);
						ParaMap result = dataSetDao.deleteSimpleData("TRANS_NOTICE_TARGET_REL", keyData);
						if("1".equals(result.getString("state"))){
							out.put("state", 1);
							out.put("message", "标的删除成功！");
						}else{
							throw new Exception("标的：'"+targetName+"'删除失败！");
						}
					}
				}
			}
		}else if(status == -1){
			out.put("state", 0);
			out.put("message", "删除标的失败！请选择有效的公告。");
		}else if(status == 1){
			out.put("state", 0);
			out.put("message", "删除标的失败！公告已审核，不能删除标的。");
		}else if(status == 2){
			out.put("state", 0);
			out.put("message", "删除标的失败！公告已发布，不能删除标的。");
		}else{
			out.put("state", 0);
			out.put("message", "删除标的失败！公告状态错误，不能删除标的。");
		}
		return out;
	}
	
	
	
//	public ParaMap doBoundTargets(String noticeId , String targetIds , String userId )throws Exception{
//		ParaMap out = new ParaMap();
//		int status = this.getNoticeStatus(noticeId);
//		if(status == 0){
//			DataSetDao dataSetDao = new DataSetDao();
//			ParaMap keyData = new ParaMap();
//			String message = "";
//			keyData.clear();
//			ParaMap relMap = dataSetDao.querySimpleData("trans_notice_target_rel", keyData,null,"target_id");
//			List targetList = relMap.getList("rs");
//			if(StrUtils.isNotNull(targetIds)){
//				String[] ids = targetIds.split(",");
//				for (int i = 0; i < ids.length; i++) {
//					String targetId = ids[i];
//					if (StrUtils.isNotNull(targetId)) {
//						keyData.clear();
//						keyData.put("id", targetId);
//						keyData.put("is_valid", 1);
//						ParaMap targetMap = dataSetDao.querySimpleData("trans_target", keyData);
//						if(targetMap.getSize()>0){
//							int target_status = Integer.parseInt(String.valueOf(targetMap.getRecordValue(0, "status")));
//							String targetName = String.valueOf(targetMap.getRecordValue(0, "no"));
//							if(target_status!=2){
//								message = message+"标的：'"+targetName+"'添加失败！状态错误。\n";
//								continue;
//							}
//							if(targetList.indexOf(targetId)>=0){
//								message = message+"标的：'"+targetName+"'添加失败！已关联公告的。\n";
//								continue;
//							}
//							keyData.clear();
//							keyData.put("id", null);
//							keyData.put("notice_id", noticeId);
//							keyData.put("target_id", targetId);
//							keyData.put("create_user_id", userId);
//							ParaMap result = dataSetDao.updateData("TRANS_NOTICE_TARGET_REL", "id", keyData);
//							if("1".equals(result.getString("state"))){
//								message = message+"标的：'"+targetName+"'添加成功！\n";
//							}else{
//								message = message+"标的：'"+targetName+"'添加失败！\n";
//							}
//						}
//					}
//				}
//			}
//			if(StrUtils.isNotNull(message)){
//				out.put("state", "1");
//				out.put("message", message);
//			}else{
//				out.put("state", "0");
//				out.put("message", "添加失败！");
//			}
//		}else if(status == -1){
//			out.put("state", "0");
//			out.put("message", "添加标的失败！请选择有效的公告。");
//		}else if(status == 1){
//			out.put("state", "0");
//			out.put("message", "添加标的失败！公告已审核，不能添加标的。");
//		}else if(status == 2){
//			out.put("state", "0");
//			out.put("message", "添加标的失败！公告已发布，不能添加标的。");
//		}else{
//			out.put("state", "0");
//			out.put("message", "添加标的失败！公告状态错误，不能添加标的。");
//		}
//		return out;
//	}
	
//	
//	public ParaMap undoBoundTargets(String noticeId , String targetIds)throws Exception{
//		ParaMap out = new ParaMap();
//		int status = this.getNoticeStatus(noticeId);
//		if(status == 0){
//			DataSetDao dataSetDao = new DataSetDao();
//			ParaMap keyData = new ParaMap();
//			String message = "";
//			if(StrUtils.isNotNull(targetIds)){
//				String[] ids = targetIds.split(",");
//				for (int i = 0; i < ids.length; i++) {
//					String targetId = ids[i];
//					if (StrUtils.isNotNull(targetId)) {
//						keyData.clear();
//						keyData.put("id", targetId);
//						keyData.put("is_valid", 1);
//						ParaMap targetMap = dataSetDao.querySimpleData("trans_target", keyData);
//						String targetName = targetMap.getSize()>0?String.valueOf(targetMap.getRecordValue(0, "no")):"";
//						keyData.clear();
//						keyData.put("notice_id", noticeId);
//						keyData.put("target_id", targetId);
//						ParaMap result = dataSetDao.deleteSimpleData("TRANS_NOTICE_TARGET_REL", keyData);
//						if("1".equals(result.getString("state"))){
//							message = message+"标的：'"+targetName+"'删除成功！\n";
//						}else{
//							message = message+"标的：'"+targetName+"'删除失败！\n";
//						}
//					}
//				}
//			}
//			if(StrUtils.isNotNull(message)){
//				out.put("state", "1");
//				out.put("message", message);
//			}else{
//				out.put("state", "0");
//				out.put("message", "删除标的失败！");
//			}
//		}else if(status == -1){
//			out.put("state", "0");
//			out.put("message", "删除标的失败！请选择有效的公告。");
//		}else if(status == 1){
//			out.put("state", "0");
//			out.put("message", "删除标的失败！公告已审核，不能删除标的。");
//		}else if(status == 2){
//			out.put("state", "0");
//			out.put("message", "删除标的失败！公告已发布，不能删除标的。");
//		}else{
//			out.put("state", "0");
//			out.put("message", "删除标的失败！公告状态错误，不能删除标的。");
//		}
//		return out;
//	}
	
	
	
	public ParaMap deleteNotice(String noticeIds) throws Exception{
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		String message = "";
		if(StrUtils.isNull(noticeIds)){
			out.put("state", 0);
			out.put("message", "删除失败！未找到有效的公告。");
		}
		String ids[] = noticeIds.split(",");
		if(ids!=null && ids.length > 0){
			for(int i = 0 ; i < ids.length ; i ++){
				String noticeId = ids[i];
				keyData.clear();
				keyData.put("id", noticeId);
				keyData.put("is_valid", 1);
				ParaMap noticeMap = dataSetDao.querySimpleData("trans_notice", keyData);
				if(noticeMap.getSize()>0){
					int status = Integer.parseInt(String.valueOf(noticeMap.getRecordValue(0, "status")));
					String name = String.valueOf(noticeMap.getRecordValue(0, "name"));
					if(status>0){
						throw new Exception("公告：'"+name+"'删除失败！公告已审核或已发布。");
					}else{
						keyData.clear();
						keyData.put("notice_id", noticeId);
						ParaMap relMap = dataSetDao.deleteSimpleData("trans_notice_target_rel", keyData);
						if("1".equals(relMap.getString("state"))){
							keyData.clear();
							keyData.put("id", noticeId);
							ParaMap result = dataSetDao.deleteSimpleData("trans_notice", keyData);
							if("1".equals(result.getString("state"))){
								out.put("state", 1);
								out.put("message", "公告删除成功！");
							}else{
								throw new Exception("公告：'"+name+"'删除失败！删除主信息失败。");
							}
						}else{
							throw new Exception( "公告：'"+name+"'删除失败！删除关联标的失败。");
						}
					}
				}
			}
		}
			
		return out;
	}
	
	
	public ParaMap doBoundAccount(String noticeId , String accountIds , String userId )throws Exception{
		ParaMap out = new ParaMap();
		int status = this.getNoticeStatus(noticeId);
		if(status == 0 || status == 4){
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap keyData = new ParaMap();
			String message = "";
			if(StrUtils.isNotNull(accountIds)){
				String[] ids = accountIds.split(",");
				for (int i = 0; i < ids.length; i++) {
					String accountId = ids[i];
					if (StrUtils.isNotNull(accountId)) {
						keyData.clear();
						keyData.put("id", accountId);
						keyData.put("is_valid", 1);
						ParaMap accountMap = dataSetDao.querySimpleData("trans_account", keyData);
						if(accountMap.getSize()>0){
							String bankName = String.valueOf(accountMap.getRecordValue(0, "bank_name"));
							String no = String.valueOf(accountMap.getRecordValue(0, "no"));
							String noName = bankName+no;
							keyData.clear();
							keyData.put("id", null);
							keyData.put("notice_id", noticeId);
							keyData.put("account_id", accountId);
							keyData.put("create_user_id", userId);
							keyData.put("is_valid", 1);
							ParaMap result=dataSetDao.updateData("trans_notice_account_rel", "id","notice_id,account_id", keyData, null);
							if("1".equals(result.getString("state"))){
								out.put("state", 1);
								out.put("message", "添加账号成功！");
							}else{
								throw new Exception("添加账号失败！");
							}
						}
					}
				}
			}
		}else if(status == -1){
			out.put("state", 0);
			out.put("message", "添加银行失败！请选择有效的公告。");
		}else if(status == 1){
			out.put("state", 0);
			out.put("message", "添加银行失败！公告已审核，不能添加银行。");
		}else if(status == 2){
			out.put("state", 0);
			out.put("message", "添加银行失败！公告已发布，不能添加银行。");
		}else{
			out.put("state", 0);
			out.put("message", "添加银行失败！公告状态错误，不能添加银行。");
		}
		return out;
	}
	
	
	public ParaMap undoBoundAccount(String noticeId , String accountIds )throws Exception{
		ParaMap out = new ParaMap();
		int status = this.getNoticeStatus(noticeId);
		if(status == 0 || status == 4){
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap keyData = new ParaMap();
			String message = "";
			if(StrUtils.isNotNull(accountIds)){
				String[] ids = accountIds.split(",");
				for (int i = 0; i < ids.length; i++) {
					String accountId = ids[i];
					if (StrUtils.isNotNull(accountId)) {
						keyData.clear();
						keyData.put("id", accountId);
						keyData.put("is_valid", 1);
						ParaMap accountMap = dataSetDao.querySimpleData("trans_account", keyData);
						if(accountMap.getSize()>0){
							String bankName = String.valueOf(accountMap.getRecordValue(0, "bank_name"));
							String no = String.valueOf(accountMap.getRecordValue(0, "no"));
							String noName = bankName+no;
							keyData.clear();
							keyData.put("notice_id", noticeId);
							keyData.put("account_id", accountId);
							ParaMap result=dataSetDao.deleteSimpleData("trans_notice_account_rel", keyData);
							if("1".equals(result.getString("state"))){
								out.put("state", 1);
								out.put("message", "删除账号成功！");
							}else{
								throw new Exception("删除账号失败！");
							}
						}
					}
				}
			}
		}else if(status == -1){
			out.put("state", 0);
			out.put("message", "删除银行失败！请选择有效的公告。");
		}else if(status == 1){
			out.put("state", 0);
			out.put("message", "删除银行失败！公告已审核，不能删除银行。");
		}else if(status == 2){
			out.put("state", 0);
			out.put("message", "删除银行失败！公告已发布，不能删除银行。");
		}else{
			out.put("state", 0);
			out.put("message", "删除银行失败！公告状态错误，不能删除银行。");
		}
		return out;
	}
	
	
	public int getNoticeStatus(String noticeId)throws Exception{
		int status = -1;
		DataSetDao dataSetDao = new DataSetDao();
		if(StrUtils.isNotNull(noticeId)){
			ParaMap keyData = new ParaMap();
			keyData.put("id", noticeId);
			keyData.put("is_valid", 1);
			ParaMap noticeMap = dataSetDao.querySimpleData("trans_notice", keyData);
			if(noticeMap.getSize()>0){
				status = Integer.parseInt(String.valueOf(noticeMap.getRecordValue(0, "status")));
			}
		}
		return status;
	}
	
	
	
	/**
	 * 获取syslob表id,FILE_NAME
	 * @param moduleId
	 * @param dataSetNo
	 * @param noticeId
	 * @param businessType
	 * @return
	 * @throws Exception
	 */
	public ParaMap getNoticeSysLobId(String noticeId,String businessType) throws Exception{
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_notice_manage01");
		sqlParams.put("dataSetNo", "getSysLobId");
		sqlParams.put("notice_id", noticeId);
		sqlParams.put("business_type", "businessType"+businessType+"NoticeAttach");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	
	
	/**
	 * 发布公告
	 * @param sqlParams
	 * @throws Exception
	 */
	public ParaMap publishNotice (String noticeIds,String userId,int isCon) throws Exception {
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		String message = "";
		if(StrUtils.isNotNull(noticeIds)){
			String ids[] = noticeIds.split(",");
			if(ids!=null && ids.length > 0){
				for(int i = 0 ; i < ids.length ; i ++){
					String noticeId = ids[i];
					if(StrUtils.isNull(noticeId)){
						throw new Exception( "提交失败！公告参数错误。");
					}
					keyData.clear();
					keyData.put("id", noticeId);
					keyData.put("is_valid", 1);
					ParaMap noticeMap = dataSetDao.querySimpleData("trans_notice", keyData);
					if(noticeMap == null || noticeMap.getSize()<=0){
						throw new Exception("提交失败！公告参数错误。");
					}
					
					ParaMap judgeMap = judgeNoticePublish(noticeMap,isCon);
					if(!"1".equals(judgeMap.getString("state"))){
						if(StrUtils.isNotNull(judgeMap.getString("message"))){
							throw new Exception(judgeMap.getString("message"));
						}else{
							throw new Exception("提交失败！");
						}
					}
					String id = noticeMap.getRecordString(0, "id");
					int status = Integer.parseInt(String.valueOf(noticeMap.getRecordValue(0, "status")));
					if(status==3){//提交
						throw new Exception("提交失败！已经是审核中的公告！");
					}
					if(status==5){//提交
						throw new Exception("提交失败！已经是审核通过的公告！");
					}
//					if(status==4){//提交
//						throw new Exception("提交失败！已经是审核不通过的公告！");
//					}
					
					
					String name = String.valueOf(noticeMap.getRecordValue(0, "name"));
					String businessType = String.valueOf(noticeMap.getRecordValue(0, "business_type"));
					String noticeType = String.valueOf(noticeMap.getRecordValue(0, "notice_type"));
					ParaMap result = this.doPublish(noticeId,name,businessType,userId,noticeType,isCon);
					if(!"1".equals(result.getString("state"))){
						throw new Exception(result.getString("message"));
					}else{
						out.put("state", 1);
						if(isCon==2){
							out.put("message", "发布成功！");
						}else{
							out.put("message", "已提交审核！");
						}
					}
				}
			}
		}else{
			out.put("state", 0);
			out.put("message", "提交失败！未找到有效的公告。");
		}
		return out;
	}
	
	public ParaMap judgeNoticePublish(ParaMap noticeMap,int isCon)throws Exception{
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		ParaMap keyData = new ParaMap();
		String id = noticeMap.getRecordString(0, "id");
		int status = Integer.parseInt(String.valueOf(noticeMap.getRecordValue(0, "status")));
		String name = String.valueOf(noticeMap.getRecordValue(0, "name"));
		String businessType = String.valueOf(noticeMap.getRecordValue(0, "business_type"));
		int noticeType = Integer.parseInt(String.valueOf(noticeMap.getRecordValue(0, "notice_type")));
		String stext="提交";
		if(isCon==2){
			stext="发布";
		}
		if(status==2){
			out.put("state", 0);
			out.put("message", "公告：'"+name+"'发布失败！公告已发布。");
			return out;
		}else if(status==4){
			out.put("state", 0);
			if(isCon==2){
				out.put("message", "公告：'"+name+"'状态为审核不通过,不能"+stext+"。");
				return out;
			}
		}
		
		if(noticeType == 0){ // 普通公告
			
			ParaMap accontMap = getNoticeAccountList(id);
			if(accontMap.size()<=0){
				out.put("message", "公告：'"+name+"'"+stext+"失败！未选择银行账号。") ;
				out.put("state", 0);
				return out;
			}
			ParaMap targetMap = getNoticeTargetData(id,null,null);
			if(targetMap.getSize()<=0){
				out.put("message", "公告：'"+name+"'"+stext+"失败！缺失标的信息或银行信息。") ;
				out.put("state", 0);
				return out;
			}
			
			for(int k = 0 ; k < targetMap.getSize() ; k++){
				String targetId = (String)targetMap.getRecordValue(k, "id");
				String targetNo = (String)targetMap.getRecordValue(k, "no");
				
				String result = targetMustIsNull(targetMap , k );
				if(StrUtils.isNotNull(result)){
					out.put("message", "公告：'"+name+"'"+stext+"失败！关联的标的'"+targetNo+"'竞买时间不能为空。") ;
					out.put("state", 0);
					return out;
				}
				
				keyData.clear();
				keyData.put("target_id", targetId);
				ParaMap tradeMap=dataSetDao.querySimpleData("trans_target_multi_trade", keyData , " turn asc");
				if(tradeMap==null ||  tradeMap.getSize()<=0){
					out.put("message", "公告：'"+name+"'"+stext+"失败！关联的标的'"+targetNo+"'没有交易属性信息。") ;
					out.put("state", "0");
					return out;
				}
				keyData.clear();
				keyData.put("target_id", targetId);
				keyData.put("is_valid", 1);
				ParaMap earnesMap=dataSetDao.querySimpleData("trans_target_earnest_money", keyData);
				if(earnesMap.getSize()<=0){
					out.put("message", "公告：'"+name+"'"+stext+"失败！关联的标的'"+targetNo+"'没有保证金信息。") ;
					out.put("state", 0);
					return out;
				}
				for(int i = 0 ; i < earnesMap.getSize() ; i++){
					String crrency = String.valueOf(earnesMap.getRecordValue(i, "currency"));
					boolean isIn = false;
					for(int j = 0 ; j<accontMap.getSize() ; j ++){
						String accCurrency = String.valueOf(accontMap.getRecordValue(j, "currency"));
						if(crrency.equals(accCurrency)){
							isIn = true;
							break;
						}
					}
					if(!isIn){
						out.put("message", "公告：'"+name+"'"+stext+"失败！未找到关联标的'"+targetNo+"'"+crrency+"保证金的可入款账号。") ;
						out.put("state", 0);
						return out;
					}
				}
				
				ParaMap goodsMap = transGoodsDao.getTransTargetGoodsList(targetId, "");
				if(goodsMap==null || goodsMap.getSize()<=0){
					out.put("message", "公告：'"+name+"'"+stext+"失败！标的'"+targetNo+"'未关联宗地。") ;
					out.put("state", 0);
					return out;
				}
				
			}
			
		}
		out.put("state", 1);
		return out;
	}
	
	
	public ParaMap doPublish(String noticeId , String name , String businessType,String userId , String noticeType,int isCon) throws Exception{
		ParaMap out = new ParaMap();
		String stext="提交";
		if(isCon==2){
			stext="发布";
		}
		//获取lobid,filename
		ParaMap syslob = getNoticeSysLobId(noticeId,businessType);
		List list=new ArrayList();
		list=syslob.getList("rs");
		List syslobList=new ArrayList();
		String lobid="";
		String filename="";
		if(list.size()>0){
			syslobList=(List)list.get(0);
			lobid=syslobList.get(0).toString();
			filename=syslobList.get(1).toString();
		}else{
			out.put("state", 0);
			out.put("message", "公告：'"+name+"'"+stext+"失败！无有效附件。");
			return out;
		}
		String pdfFileName=filename.substring(filename.lastIndexOf(".")+1);
		if(!pdfFileName.equalsIgnoreCase("pdf")){
			out.put("state", 0);
			out.put("message", "公告：'"+name+"'"+stext+"失败！公告不是pdf格式。");
			return out;
		}
		BaseDownloadService baseDownloadService=new BaseDownloadService();
		String filePath=baseDownloadService.getFilePath(lobid);
		baseDownloadService.apartNotice(lobid, noticeId, filePath);
		
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", noticeId);
//		keyData.put("status", 2);
		if(noticeType!=null && noticeType.equals("0")){//普通公告需要审核
			keyData.put("status", 3);//要求增加审核环节，所以先设置为3
		}else{
			keyData.put("status", 2);
		}
		keyData.put("publish_user_id", userId);
		keyData.put("publish_date", DateUtils.getStr(DateUtils.now()));
		ParaMap formatMap = new ParaMap();
		formatMap.put("publish_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		ParaMap result = dataSetDao.updateData("trans_notice", "id", keyData,formatMap);
		
		keyData.clear();
		keyData.put("moduleNo", "trans_prepare_notice_manage01");
		keyData.put("dataSetNo", "query_notice_target_list");
		keyData.put("noticeId",noticeId);
		ParaMap targetMap = dataSetDao.queryData(keyData);
		
//		if(noticeType!=null && noticeType.equals("0")){
//			if(targetMap.getSize()>0){
//				for(int i = 0 ; i < targetMap.getSize() ; i++){
//					String targetId = (String)targetMap.getRecordValue(i, "id");
//					keyData.clear();
//					keyData.put("status", "3");
//					keyData.put("id", targetId);
//					dataSetDao.updateData("TRANS_TARGET", "id", keyData,null);
//					//流程
//					EngineDao dao = new EngineDao();
//					dao.createTemplateInfo(targetId);
//					
//				}
//			}
//		}
		
		out.put("state", 1);
		out.put("message", "公告：'"+name+"'已提交审核！");
		return out;
	}
	
	
	public ParaMap transNormalPublishNoticeList(String organId , String goodsType)throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_info_modify01");
		sqlParams.put("dataSetNo", "trans_publich_notices");
		sqlParams.put("organId", organId);
		if(StrUtils.isNotNull(goodsType)){
			ParaMap customConditions = new ParaMap();
			if(goodsType.indexOf(",")!=-1){
				customConditions.put("CUSTOM_CONDITION", " and exists (select * from trans_notice_target_rel tntr , trans_target tt where tntr.target_id = tt.id and substr(tt.business_type,1,3) in ('301','401') and tt.is_stop=0 and tt.is_suspend=0 and tntr.notice_id = tn.id ) ");
			}else{
				customConditions.put("CUSTOM_CONDITION", " and exists (select * from trans_notice_target_rel tntr , trans_target tt where tntr.target_id = tt.id and substr(tt.business_type,1,3) in ('"+goodsType+"') and tt.is_stop=0 and tt.is_suspend=0 and tntr.notice_id = tn.id ) ");
			}
			sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		}
		ParaMap out = dataSetDao.queryData(sqlParams);
		return out;
	}
	
	public ParaMap noticeLicenseList(String noticeId)throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_info_modify01");
		sqlParams.put("dataSetNo", "trans_notice_license_list");
		sqlParams.put("noticeId", noticeId);
		ParaMap out = dataSetDao.queryData(sqlParams);
		return out;
	}
	
	
	public boolean isNoticeHaveLicense(String noticeId)throws Exception{
		ParaMap out = this.noticeLicenseList(noticeId);
		if(out!=null && out.getSize()>0){
			return true;
		}else{
			return false;
		}
	}
	
	public ParaMap updateTransNotice(ParaMap notice,ParaMap formatMap) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("trans_notice", "id",notice, formatMap);
		return result;
	}
	
	public ParaMap noticeTreeList(String organId , String goodsType) throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_supplement_manage01");
		sqlParams.put("dataSetNo", "trans_notice_tree_list");
		
		String existsSql = "and exists (select * from trans_notice_target_rel tntr , trans_target tt where tntr.target_id = tt.id and tt.status in (3,4)  " +
				           "and tntr.notice_id in (select n.id from trans_notice n start with n.id = tn.id connect by prior n.parent_id = n.id ) )" ; 
		StringBuffer customCondition = new StringBuffer(" "+existsSql+" ");
		if (StrUtils.isNotNull(organId)) {
			customCondition.append(" and tn.organ_id = :organId ");
			sqlParams.put("organId", organId);
		}
		
		if (StrUtils.isNotNull(goodsType)) {
			if(goodsType.indexOf(",")!=-1){
				customCondition.append(" and  substr(tn.business_type, 1, 3) in ('301','401')");
			}else{
				customCondition.append(" and  substr(tn.business_type, 1, 3) in ('"+goodsType+ "')");
			}
			sqlParams.put("goodsType", goodsType);
		}
		
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		
		ParaMap out = dataSetDao.queryData(sqlParams);
		return out;
	}
	
	
	public ParaMap getSupplementNoticeTargetData(String noticeId , String parentId) throws Exception {
		ParaMap keyData = new ParaMap();
		keyData.put("moduleNo", "trans_supplement_manage01");
		keyData.put("dataSetNo", "query_supplement_notice_target_list");
		keyData.put("noticeId",noticeId);
		keyData.put("parentId",parentId);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(keyData);
		return result;
	}
	
	public ParaMap saveSupplementTarget(String noticeId , String ids , String userId)throws Exception{
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("notice_id", noticeId);
		ParaMap out=dataSetDao.deleteSimpleData("trans_notice_target_rel", keyData);
		if(StringUtils.isNotEmpty(ids)){
			String[] id = ids.split(",");
			for(int i= 0 ; i < id.length ; i++){
				if(StringUtils.isNotEmpty(id[i])){
					keyData.clear();
					keyData.put("id", "");
					keyData.put("notice_id", noticeId);
					keyData.put("target_id", id[i]);
					keyData.put("turn", i+1);
					keyData.put("create_user_id", userId);
					out=dataSetDao.updateData("trans_notice_target_rel","id", keyData);
				}
			}
		}
		return out;
	}
	
	public List getNoticeTreeList(List list)throws Exception{
		List resultList = new ArrayList();
		for(int i = 0 ; i < list.size(); i ++ ){
			ParaMap obj = (ParaMap)list.get(i);
			if(StrUtils.isNull(obj.getString("parent_id"))){
				ParaMap item = createItem(list, i);
				resultList.add(item);
			}
		}
		return resultList;
	}
	
	public ParaMap createItem(List list ,int i)throws Exception{
		ParaMap item = (ParaMap)list.get(i);
		String id = item.getString("id");
		List childItemList = getChildItem(list , id );
		if (childItemList.size() > 0){
			item.put("children", childItemList);
		}
		return item;
	}
	
	public List getChildItem(List list , String parentId) throws Exception{
		List resultList = new ArrayList(); 
		for(int i = 0;i < list.size(); i++) {
			ParaMap obj = (ParaMap)list.get(i);
			if (obj.getString("parent_id").equals(parentId)) {
				ParaMap item = createItem(list, i);
				List childItemList = getChildItem(list, obj.getString("id"));
				if (childItemList.size() > 0){
					item.put("children", childItemList);
				}
				resultList.add(item);
			}
		}
		return resultList;
	}
	
	
}


