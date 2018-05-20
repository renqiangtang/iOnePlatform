package com.before.service;


import java.util.ArrayList;
import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.before.dao.TransGoodsMineDao;
import com.before.dao.TransNoticeMineDao;

/**
 * 交易前土地管理Service
 * 
 * @author chenl 2012-05-30
 */
public class TransNoticeMineService extends BaseService {
	
	/**
	 * 公告列表查询条件
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap initNoticeListPageParam(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String organId = getOrganId(inMap.getString("u"));
		String goodsType = inMap.getString("goodsType");
		//业务类型
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		out.put("businessType", this.changeToObjList(transGoodsDao.getOrganBusinessList(organId,goodsType),null));
		return out;
	}
	
	/**
	 * @param inMap
	 * @return 获取所有公告信息
	 * @throws Exception
	 */

	public ParaMap getNoticeListData(ParaMap inMap) throws Exception {
		ParaMap sqlParams = getPageInfo(inMap);
		getQueryCondition(sqlParams);
		TransNoticeMineDao noticeDao = new TransNoticeMineDao();
		ParaMap out = noticeDao.getNoticeListData(sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		List items = getDataInfo(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             设置和读取分页信息
	 */
	private ParaMap getPageInfo(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		String sortField = inMap.getString("sortname");
		String sortDir = inMap.getString("sortorder");
		if (sortField != null && !sortField.equals("")
				&& !sortField.equalsIgnoreCase("null") && sortDir != null
				&& !sortDir.equals("") && !sortDir.equalsIgnoreCase("null"))
			sortField = sortField + " " + sortDir;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		if (sortField == null || sortField.equals("")
				|| sortField.equalsIgnoreCase("null"))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		return sqlParams;
	}

	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             设置自定义查询条件
	 */
	private ParaMap getQueryCondition(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		StringBuffer customCondition = new StringBuffer("");
		String userId = inMap.getString("u");
		String organId = getOrganId(userId);
		sqlParams.put("organ_id", organId);
		customCondition.append(" and tn.organ_id = :organ_id ");
		String business_type = inMap.getString("business_type");
		if (StrUtils.isNotNull(business_type) && !"-1".equals(business_type)) {
			if (StrUtils.isNotNull(business_type)) {
				sqlParams.put("business_type", business_type);
				customCondition.append(" and tn.business_type = :business_type ");
			}
		}else{
			String goodsType = inMap.getString("goodsType");
			TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
			ParaMap businessTypeMap = transGoodsDao.getOrganBusinessList(organId,goodsType);
			List btList = new ArrayList();
			if(businessTypeMap.getSize()>0){
				customCondition.append(" and tn.business_type in (  ");
				for(int i = 0 ; i < businessTypeMap.getSize() ; i ++ ){
					String temp_str = businessTypeMap.getRecordString(i, "id");
					sqlParams.put("business_type_" + i, temp_str);
					customCondition.append(":business_type_" + i + ",");
				}
				customCondition.deleteCharAt(customCondition.length() - 1);
				customCondition.append(" ) ");
			}
		}
		if (inMap.containsKey("name")) {
			String name = inMap.getString("name");
			if (StrUtils.isNotNull(name)) {
				sqlParams.put("name", name);
				customCondition.append(" and tn.name like '%'||:name||'%'");
			}
		}
		if (inMap.containsKey("no")) {
			String no = inMap.getString("no");
			if (StrUtils.isNotNull(no)) {
				sqlParams.put("no", no);
				customCondition.append(" and tn.no like '%'||:no||'%'");
			}
		}
		if (inMap.containsKey("notice_type")&& !"-1".equals(inMap.getString("notice_type"))) {
			String notice_type = inMap.getString("notice_type");
			if (StrUtils.isNotNull(notice_type)) {
				String str[]=notice_type.split(",");
				if(str!=null&&str.length>0){
					customCondition.append(" and  tn.notice_type in('");
					for(int i=0;i<str.length;i++){
						if(i==str.length-1){
							customCondition.append(str[i]+"')");
						}else{
							customCondition.append(str[i]+"','");
						}
					}
				}
			}
		}
		if(inMap.containsKey("isCon")){//如果是审核列表
			if (inMap.containsKey("status") && !"-1".equals(inMap.getString("status"))) {
				String status = inMap.getString("status");
				if (StrUtils.isNotNull(status)) {
					sqlParams.put("status", status);
					customCondition.append(" and tn.status=:status");
				}else{
					customCondition.append(" and (tn.status=2 or tn.status=3   or tn.status=5)");
				}
			}else{
				customCondition.append(" and (tn.status=2 or tn.status=3 or tn.status=5)");
				
			}
			customCondition.append(" and tn.notice_type=0 ");
		}else if (inMap.containsKey("status") && !"-1".equals(inMap.getString("status"))) {
			String status = inMap.getString("status");
			if (StrUtils.isNotNull(status)) {
				sqlParams.put("status", status);
				customCondition.append(" and tn.status=:status");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return sqlParams;
	}
	
	
	/**
	 * @param out
	 * @return
	 * @throws Exception
	 *             获取所有数据后解析成集合
	 */
	private List getDataInfo(ParaMap out) throws Exception {
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("no", "no");
		fields.put("name", "name");
		fields.put("parent_id", "parent_id");
		fields.put("notice_type", "notice_type");
		fields.put("notice_date", "notice_date");
		fields.put("rarget_reject", "rarget_reject");
		fields.put("is_valid", "is_valid");
		fields.put("status", "status");
		fields.put("remark", "remark");
		fields.put("business_type", "business_type");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		return items;
	}
	
	/**
	 * 公告信息
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getNoticeInfo(ParaMap inMap)throws Exception{
		ParaMap out = new ParaMap();
		String noticeId = inMap.getString("noticeId");
		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
		ParaMap result = transNoticeDao.getNoticeInfo(noticeId);
		List noticeList = this.changeToObjList(result, null);
		out.put("notice", noticeList!=null && noticeList.size() > 0 ? noticeList.get(0) : null);
		out.put("targets", this.changeToObjList(transNoticeDao.getNoticeTargetData(noticeId,null,null), null));
		return out;
	}
	
	
	/**
	 * @param inMap
	 * @return  关联的标的
	 * @throws Exception
	 */
	public ParaMap getNoticeTargetData(ParaMap inMap) throws Exception {
		String noticeId = inMap.getString("noticeId");
		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
		String page = inMap.getString("page");
		String pagesize = inMap.getString("pagesize");
		ParaMap out = transNoticeDao.getNoticeTargetData(noticeId,page,pagesize);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		List items = this.changeToObjList(out,null);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	
	/**
	 * @param inMap
	 * @return  未关联的标的
	 * @throws Exception
	 */
	public ParaMap getNoticeOtherTargetData(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		ParaMap sqlParams = inMap;
		String noticeId = inMap.getString("noticeId");
		if (StrUtils.isNull(noticeId)) {
		   out.put("message", "请先创建公告！");
		   return out;
		}
		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
		ParaMap noticeMap = transNoticeDao.getNoticeInfo(noticeId);
		if(noticeMap == null || noticeMap.getSize()<=0){
			out.put("message", "请先创建公告！");
			return out;
		}
		String business_type = String.valueOf(noticeMap.getRecordValue(0, "business_type"));
		//分页
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		//查询条件
		String userId = inMap.getString("u");
		String organId = getOrganId(userId);
		sqlParams.put("transOrganId", organId);
		sqlParams.put("businessType", business_type);
		StringBuffer customCondition = new StringBuffer("");
		customCondition.append(" and tt.id not in (select target_id from trans_notice_target_rel where notice_id= :noticeId ) ");
		customCondition.append(" and tt.business_type = :businessType  ");
		if (inMap.containsKey("create_organ_id")) {
			String create_organ_id = inMap.getString("create_organ_id");
			if(StrUtils.isNotNull(create_organ_id)){
				customCondition.append(" and tt.organ_id =:create_organ_id ");
			}
		}
		
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		
		out = transNoticeDao.getNoticeOtherTargetData(sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		List items = this.changeToObjList(out,null);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	/**
	 * @param inMap
	 * @return 公告数据更新
	 * @throws Exception
	 */
	public ParaMap saveNotice(ParaMap inMap) throws Exception {
		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
		String userId = inMap.getString("u");
		inMap.put("create_user_id", userId);
		inMap.put("organ_id", this.getOrganId(userId));
		ParaMap out = transNoticeDao.saveNotice(inMap);
		if(!out.containsKey("message")){
			if("1".equals(out.getString("state"))){
				out.put("message", "保存成功！");
			}else{
				out.put("message", "保存失败！");
			}
		}
		return out;
	}
	
//	/**
//	 * @param inMap
//	 * @return 审核公告
//	 * @throws Exception
//	 */
//	public ParaMap checkNotice(ParaMap inMap) throws Exception {
//		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
//		String noticeIds = inMap.getString("noticeIds");
//		String userId = inMap.getString("u");
//		ParaMap out = transNoticeDao.checkNotice(noticeIds,userId);
//		return out;
//	}
	
	/**
	 * @param inMap
	 * @return 审核公告 tyw
	 * @throws Exception
	 */
	public ParaMap checkNotice(ParaMap inMap) throws Exception {
		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
		String noticeIds = inMap.getString("noticeIds");
		String userId = inMap.getString("u");
		int isCon= inMap.getInt("isCon");
		ParaMap out = transNoticeDao.checkNotice(noticeIds,userId,isCon);
		return out;
	}
	
	/**
	 * 撤销提交公告
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap cancelNotice(ParaMap inMap) throws Exception {
		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
		String noticeIds = inMap.getString("noticeIds");
		ParaMap out = transNoticeDao.cancelNotice(noticeIds);
		return out;
	}
	
	
	/**
	 * @param inMap
	 * @return 关联标的
	 * @throws Exception
	 */
	public ParaMap doBoundTargets(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String noticeId = inMap.getString("noticeId");
		String targetIds = inMap.getString("targetIds");
		String userId = inMap.getString("u");
		if(StrUtils.isNull(noticeId) || StrUtils.isNull(targetIds) ){
			out.put("state", 0);
			out.put("message", "添加标的失败！参数错误");
		}else{
			TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
			out = transNoticeDao.doBoundTargets(noticeId , targetIds , userId);
		}
		return out;
	}
	
	
	/**
	 * @param inMap
	 * @return 解除关联标的
	 * @throws Exception
	 */
	public ParaMap undoBoundTargets(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String noticeId = inMap.getString("noticeId");
		String targetIds = inMap.getString("targetIds");
		String userId = inMap.getString("u");
		if(StrUtils.isNull(noticeId) || StrUtils.isNull(targetIds) ){
			out.put("state", 0);
			out.put("message", "删除标的失败！参数错误");
		}else{
			TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
			out = transNoticeDao.undoBoundTargets(noticeId , targetIds);
		}
		return out;
	}
	
	
	/**
	 * @param inMap
	 * @return 删除公告
	 * @throws Exception
	 */
	public ParaMap deleteNotice(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String noticeIds = inMap.getString("noticeIds");
		String userId = inMap.getString("u");
		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
		out = transNoticeDao.deleteNotice(noticeIds);
		return out;
	}
	
	/**
	 * 公告账号列表
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getNoticeBankList(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String noticeId = inMap.getString("noticeId");
		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
		out = transNoticeDao.getNoticeAccountList(noticeId);
		int totalRowCount = out.getInt("totalRowCount");
		List items = this.changeToObjList(out, null);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	/**
	 * 公告未关联账号列表
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getOtherBankList(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String noticeId = inMap.getString("noticeId");
		if (StrUtils.isNull(noticeId)) {
		   out.put("message", "请先创建公告！");
		   return out;
		}
		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
		ParaMap noticeMap = transNoticeDao.getNoticeInfo(noticeId);
		if(noticeMap == null || noticeMap.getSize()<=0){
			out.put("message", "请先创建公告！");
			return out;
		}
		String business_type = String.valueOf(noticeMap.getRecordValue(0, "business_type"));
		String userId = inMap.getString("u");
		String organId = this.getOrganId(userId);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_notice_manage01");
		sqlParams.put("dataSetNo", "notice_other_bank_account_list");
		sqlParams.put("noticeId", noticeId);
		sqlParams.put("organId", organId);
		sqlParams.put("businessType", business_type);
		out = dataSetDao.queryData(sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		List items = this.changeToObjList(out, null);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	
	/**
	 * @param inMap
	 * @return 关联银行
	 * @throws Exception
	 */
	public ParaMap doBoundAccount(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String noticeId = inMap.getString("noticeId");
		String accountIds = inMap.getString("accountIds");
		String userId = inMap.getString("u");
		if(StrUtils.isNull(noticeId) || StrUtils.isNull(accountIds) ){
			out.put("state", 0);
			out.put("message", "添加银行账号失败！参数错误");
		}else{
			TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
			out = transNoticeDao.doBoundAccount(noticeId , accountIds , userId);
		}
		return out;
	}
	
	/**
	 * @param inMap
	 * @return 删除银行
	 * @throws Exception
	 */
	public ParaMap undoBoundAccount(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String noticeId = inMap.getString("noticeId");
		String accountIds = inMap.getString("accountIds");
		String userId = inMap.getString("u");
		if(StrUtils.isNull(noticeId) || StrUtils.isNull(accountIds) ){
			out.put("state", 0);
			out.put("message", "删除银行账号失败！参数错误");
		}else{
			TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
			out = transNoticeDao.undoBoundAccount(noticeId , accountIds);
		}
		return out;
	}
	
	
	public ParaMap publishNotice(ParaMap inMap) throws Exception{
		ParaMap out = new ParaMap();
		String noticeIds = inMap.getString("noticeIds");
		String userId = inMap.getString("u");
		int isCon= inMap.getInt("isCon");
		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
		out = transNoticeDao.publishNotice(noticeIds,userId,isCon);
		return out;
	}
	
	public ParaMap noticeTreeList(ParaMap inMap) throws Exception{
		ParaMap out = new ParaMap();
		String goodsType = inMap.getString("goodsType");
		String userId = inMap.getString("u");
		String organId = this.getOrganId(userId);
		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
		out = transNoticeDao.noticeTreeList(organId,goodsType);
		List list = transNoticeDao.getNoticeTreeList(out.getListObj());
		out.clear();
		out.put("Rows", list);
		return out;
	}
	
	public ParaMap getSupplementInfo(ParaMap inMap) throws Exception{
		ParaMap out = new ParaMap();
		String noticeId = inMap.getString("noticeId");
		String parentId = inMap.getString("parentId");
		String mode = inMap.getString("mode");
		String organId = getOrganId(inMap.getString("u"));
		String goodsType = inMap.getString("goodsType");
		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
		if("new".equals(mode) || StrUtils.isNull(noticeId)){
			mode = "new";
		}
		if("new".equals(mode)){
			ParaMap parent = transNoticeDao.getNoticeInfo(parentId);
			List parentList = parent.getListObj();
			if(parentList != null && parentList.size()>0){
				ParaMap parentMap = (ParaMap)parentList.get(0);
				out.put("parent", parentMap);
				goodsType = parentMap.getString("business_type").substring(0, 3);
				
				ParaMap notice = new ParaMap();
				notice.put("business_type", parentMap.getString("business_type"));
				out.put("notice", notice);
			}
		}else{
			ParaMap notice = transNoticeDao.getNoticeInfo(noticeId);
			List noticeList = notice.getListObj();
			if(noticeList !=null && noticeList.size()>0){
				ParaMap noticeMap = (ParaMap)noticeList.get(0);
				out.put("notice", noticeMap);
				goodsType = noticeMap.getString("business_type").substring(0, 3);
				parentId = noticeMap.getString("parent_id");
				if(StrUtils.isNotNull(parentId)){
					ParaMap parent = transNoticeDao.getNoticeInfo(parentId);
					List parentList = parent.getListObj();
					if(parentList != null && parentList.size()>0){
						ParaMap parentMap = (ParaMap)parentList.get(0);
						out.put("parent", parentMap);
					}
				}
			}
		}
		
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		out.put("businessType", this.changeToObjList(transGoodsDao.getOrganBusinessList(organId,goodsType),null));
		out.put("mode", mode);
		return out;
	}
	
	public ParaMap getSupplementNoticeTargetData(ParaMap inMap)throws Exception{
		String noticeId = inMap.getString("noticeId");
		String parentId = inMap.getString("parentId");
		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
		ParaMap out = transNoticeDao.getSupplementNoticeTargetData(noticeId,parentId);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		List items = this.changeToObjList(out,null);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	
	public String getOrganId(String userId) throws Exception {
		String organId = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_user_manager");
		sqlParams.put("dataSetNo", "getUserOrganInfo");
		sqlParams.put("id", userId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		if(result.getSize()>0){
			List filds = result.getFields();
			List record = (List)result.getRecords().get(0);
			if(filds != null && filds.size()>0 ){
				for(int i = 0 ; i < filds.size() ; i ++ ){
					if(filds.get(i).equals("id")){
						organId = (String)record.get(i);
						break;
					}
				}
			}
		}
		return organId;
	}
	
	public List changeToObjList(ParaMap in,String firstId) throws Exception{
		List resultList = new ArrayList();
		if(StrUtils.isNotNull(firstId)){
			ParaMap fMap = new ParaMap();
			fMap.put("id",firstId);
			resultList.add(fMap);
		}
		if(in.getSize()>0){
			List filds = in.getFields();
			for(int i = 0 ; i < in.getSize() ; i ++){
				ParaMap out = new ParaMap();
				List record = (List)in.getRecords().get(i);
				String id = "";
				if(filds != null && filds.size()>0 ){
					for(int j = 0 ; j < filds.size() ; j ++ ){
						out.put((String)filds.get(j), record.get(j));
						if("id".equals((String)filds.get(j))){
							id = (String)record.get(j);
						}
					}
				}
				if(StrUtils.isNotNull(firstId) && StrUtils.isNotNull(id) && id.equals(firstId)){
					resultList.set(0, out);
				}else{
					resultList.add(out);
				}
			}
		}
		return resultList;
	}
	
	

}
