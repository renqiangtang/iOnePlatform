package com.trademan.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.DateUtils;
import com.base.utils.JsonUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.sysman.dao.AttachDao;
import com.sysman.dao.CantonDao;
import com.sysman.dao.ExtendDao;
import com.sysman.dao.FieldChangeDao;

/**
 * 竞买人DAO
 * @author SAMONHUA
 * 2012-04-26
 */
public class BidderDao extends BaseDao {
	private static String landBidderRoleId = null;
	private static String houseBidderRoleId = null;
	private static String mineralBidderRoleId = null;
	private static String plowBidderRoleId = null;
	private static String businessRoleNoMap = "land=td_bidder;house=fc_bidder;mineral=kc_bidder;plow=gz_bidder";
	//缓存竞买人关联的单位列表
	private static Map<Integer, Map<String, String>> BIDDER_ORGAN_IDS = null;
	
	public ParaMap getBidderListData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_bidder_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryBidderListData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getBidderData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_bidder_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryBidderData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		return dataSetDao.queryData(sqlParams);
	}

	public ParaMap updateBidderData(ParaMap bidderData) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = null;
		String id = bidderData.getString("id");
		boolean newBidder = StrUtils.isNull(id);
		String createUserId = bidderData.getString("u");
		int bidderType = bidderData.containsKey("bidder_type") ? bidderData.getInt("bidder_type") : -1;
		if (!(bidderType == -1 || bidderType == 0 || bidderType == 1||bidderType==3))
			bidderType = -1;
		String bidderTypeLabel = bidderType == 2 ? "矿产" : (bidderType == 3 ? "耕指" : "土地");
		int isCompany = bidderData.containsKey("is_company") ? bidderData.getInt("is_company") : -1;
		if (!(isCompany == -1 || isCompany == 0 || isCompany == 1))
			isCompany = -1;
		String companyLabel = isCompany == 0 ? "个人" : (isCompany == 1 ? "企业" : "");
		if(bidderType == 3){
			companyLabel = isCompany == 0 ? " 土地储备机构" : (isCompany == 1 ? "企业" : "");
			
		}
		String userId = bidderData.getString("user_id");
		String cakeyId = bidderData.getString("cakey_id");
		String updateMode = bidderData.getString("updateMode");
		//检查是否只是审核操作，无数据更新
		boolean confirmOperate = false;
		if (StrUtils.isNotNull(id))
			if (bidderData.containsKey("confirm_opinion"))
				confirmOperate = true;
		//修正竞买人角色标识
		boolean setBidderRelRole = false;
		//查询竞买人所有旧的数据，检查数据使用
		ParaMap originalData = StrUtils.isNull(id) ? null : getBidderData(null, null, id);
		//记录需要清除附件数据的id列表
		Map<String, String> deleteAttachIds = new HashMap<String, String>();
		if (originalData != null && originalData.getRecordCount() > 0) {
			if (isCompany != -1 && isCompany != originalData.getRecordInteger(0, "is_company"))
				deleteAttachIds.put(id, "trans_bidder");
		}
				
		//检查竞买人名称
		ParaMap checkBidderData = new ParaMap();
		String name = bidderData.getString("name");
		if (StrUtils.isNotNull(name)) {
			checkBidderData.put("id", id);
			checkBidderData.put("name", name);
			checkBidderData.put("bidder_type", bidderType);
		
				checkBidderData.put("is_company", isCompany);
				if(bidderType != 3){
					checkBidderData.put("certificate_no", bidderData.get("certificate_no"));
				}
			
			result = checkBidderExists(null, null, checkBidderData);
			if (result.getInt("state") == 1) {
				if (result.getRecordInteger(0, "row_count") > 0) {
					result.clear();
					result.put("state", 0);
					if (bidderData.getInt("is_company") == 1||(bidderType ==3))
						result.put("message", bidderTypeLabel + companyLabel
								+ "竞买人 " + name + " 已存在，不能重复");
					else
						result.put("message", bidderTypeLabel + companyLabel
								+ "竞买人 " + name + " 的身份证号码 " + bidderData.get("certificate_no") + " 已存在，不能重复");
					return result;
				}
			} else
				return result;
		}
		//检查CAKEY
		String cakey = bidderData.getString("cakey");
		if (StrUtils.isNotNull(cakey)) {
			checkBidderData.clear();
			checkBidderData.put("id", id);
			checkBidderData.put("cakey", cakey);
			result = checkBidderExists(null, null, checkBidderData);
			if (result.getInt("state") == 1) {
				if (result.getRecordInteger(0, "row_count") > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "CAKEY 已经被使用");
					return result;
				}
			} else
				return result;
		}
		//保存竞买人
		String userName = bidderData.getString("user_name");
		if (StrUtils.isNotNull(userName)) {
			//检查用户自行录入的用户名是否存在
			ParaMap data = new ParaMap();
			data.clear();
			data.put("user_name", userName);
			ParaMap checkUserData = dataSetDao.querySimpleData("sys_user", data);
			if (checkUserData.getInt("state") == 1) {
				if (checkUserData.getInt("totalRowCount") > 0) {
					if (StrUtils.isNull(userId) || !userId.equals(checkUserData.getRecordString(0, "id"))) {
						result = new ParaMap();
						result.put("state", 0);
						result.put("message", "输入的竞买人登录用户名 " + userName + " 已存在，操作取消。");
						return result;
					}
				}
			}
		}
		if (StrUtils.isNull(id)) {
			if (!bidderData.containsKey("create_user_id"))
				bidderData.put("create_user_id", createUserId);
			//获取竞买人行政区
			if (!bidderData.containsKey("canton_id"))
				bidderData.put("canton_id", CantonDao.getSystemCantonId(false));
		} else if (confirmOperate) {
			if (!bidderData.containsKey("confirm_user_id"))
				bidderData.put("confirm_user_id", createUserId);
			if (!bidderData.containsKey("confirm_date"))
				bidderData.put("confirm_date", StrUtils.dateToString(DataSetDao.getDBServerDate()));
		}
		//保存前记录审核、冻结、年审、激活、注销等操作历史
		if (confirmOperate) {
			FieldChangeDao fieldChangeDao = new FieldChangeDao();
			ParaMap change = new ParaMap();
			change.put("ref_table_name", "trans_bidder");
			change.put("ref_id", id);
			change.put("field_name", "status");
			change.put("new_value", bidderData.getInt("status"));
			change.put("change_cause", bidderData.getString("confirm_opinion"));
			fieldChangeDao.updateFieldChange(change);
		}
		ParaMap format = new ParaMap();
		format.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		format.put("valid_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		format.put("confirm_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		result = dataSetDao.updateData("trans_bidder", "id", bidderData, format);
		if (result.getInt("state") == 1) {
			if (StrUtils.isNull(id))//新增数据
				id = result.getString("id");
			//激活竞买人时整理关联角色
			if (confirmOperate && updateMode.equals("active")) {
				int currentBidderType = -1;
				if (bidderType != -1)
					currentBidderType = bidderType;
				else if (originalData != null && originalData.getRecordCount() > 0)
					currentBidderType = originalData.getRecordInt(0, "bidder_type");
				if (currentBidderType != -1) {
					removeBidderRole(userId, StrUtils.deleteParam("land;house;mineral;plow", currentBidderType));
					addBidderRole(userId, currentBidderType);
					setBidderRelRole = true;
				}
			}
			//保存护展字段，代理人
			ExtendDao etd=new ExtendDao();
			etd.updateExtend("trans_bidder", id, "txtAgentName",
					bidderData.getString("txtAgentName"), "代理人姓名");
			etd.updateExtend("trans_bidder", id, "txtAgentNo",
					bidderData.getString("txtAgentNo"), "代理人身份证");
			etd.updateExtend("trans_bidder", id, "txtAgentPhone",
					bidderData.getString("txtAgentPhone"), "代理人电话号码");
		} else
			throw new Exception("保存竞买人数据失败，操作取消。");
		ParaMap data = new ParaMap();
		//保存竞买人登录用户数据
		if (StrUtils.isNull(userId) && StrUtils.isNull(userName) && bidderData.containsKey("user_name")) {
			userName = "BIDDER_" + name;
			//检查即将新增的用户名是否重复
			int existsUserIndex = 0;
			while (true) {
				data.clear();
				String checkBidderUserName;
				if (existsUserIndex == 0)
					checkBidderUserName = userName;
				else
					checkBidderUserName = userName + existsUserIndex;
				data.put("user_name", checkBidderUserName);
				ParaMap checkBidderUserData = dataSetDao.querySimpleData("sys_user", data);
				if (checkBidderUserData.getInt("state") == 1) {
					if (checkBidderUserData.getInt("totalRowCount") == 0) {
						userName = checkBidderUserName;
						break;
					}
				}
				existsUserIndex++;
			}
		}
		if (StrUtils.isNotNull(userId) || StrUtils.isNotNull(userName)) {
			data.clear();
			data.put("id", userId);
			if (StrUtils.isNotNull(userName))
				data.put("user_name", userName);
			if (bidderData.containsKey("user_password"))
				data.put("password",  bidderData.getString("user_password"));
			if (StrUtils.isNull(userId)) {
				data.put("user_type", 1);
				data.put("status", 1);
				data.put("ref_table_name", "trans_bidder");
				data.put("ref_id", id);
				data.put("validity", 365);
				data.put("create_user_id", createUserId);
				
			}
			if (data.size() > 1) {
				result = dataSetDao.updateData("sys_user", "id", data, format);
				if (result.getInt("state") == 1) {
					if (StrUtils.isNull(userId))
						userId = result.getString("id");
				} else
					throw new Exception("保存竞买人登录数据失败，操作取消。");
			}
		}
		if (!setBidderRelRole && bidderType != -1 && StrUtils.isNotNull(userId)) {
			//新增竞买人或者修改竞买人类别时整理关联角色
			if (newBidder) {
				addBidderRole(userId, bidderType);
				setBidderRelRole = true;
			} else if (originalData != null && originalData.getInt("state") == 1 && originalData.getRecordCount() > 0
					&& bidderType != originalData.getRecordInt(0, "bidder_type")) {
				removeBidderRole(userId, StrUtils.deleteParam("land;house;mineral;plow", bidderType));
				addBidderRole(userId, bidderType);
				setBidderRelRole = true;
			}
		}
			
		//保存竞买人CAKEY
		if ((StrUtils.isNotNull(cakeyId) || bidderData.containsKey("cakey")) && StrUtils.isNotNull(userId)) {
			data.clear();
			data.put("id", cakeyId);
			data.put("user_id", userId);
			data.put("key", cakey);
			if (StrUtils.isNull(cakeyId))
				data.put("create_user_id", createUserId);
			result = dataSetDao.updateData("sys_cakey", "id", data);
			if (result.getInt("state") == 1) {
				if (StrUtils.isNull(cakeyId))
					cakeyId = result.getString("id");
			} else
				throw new Exception("保存竞买人CAKEY数据失败，操作取消。");
		}
		List updateDatas = new ArrayList();
		//删除所以有生变化需要重置或者清除竞买人资料的
		if (deleteAttachIds.size() > 0) {
			AttachDao attachDao = new AttachDao();
			Iterator it = deleteAttachIds.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				String refTableName = deleteAttachIds.get(key);
				attachDao.clearAttachTemplet(key, refTableName);
			}
		}
		result.clear();
		result.put("state", 1);
		result.put("id", id);
		result.put("cakeyId", cakeyId);
		result.put("userId", userId);
		return result;
	}
	
	private ParaMap removeBidderRole(String userId, List<String> businessList) throws Exception {
		ParaMap result = null;
		if (StrUtils.isNull(userId) || businessList == null || businessList.size() == 0)
			return null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("ref_type", 0);
		keyData.put("ref_id", userId);
		List<String> roleIds = new ArrayList<String>();
		for(int i = 0; i < businessList.size(); i++) {
			String businessNo = businessList.get(i);
			if (StrUtils.isNull(businessNo))
				continue;
			String businessRoleNo = StrUtils.getParamValue(businessRoleNoMap, businessNo);
			if (StrUtils.isNull(businessRoleNo))
				continue;
			String roleId = null;
			if (businessRoleNo.equals("td_bidder"))
				roleId = landBidderRoleId;
			else if (businessRoleNo.equals("fc_bidder"))
				roleId = houseBidderRoleId;
			else if (businessRoleNo.equals("kc_bidder"))
				roleId = mineralBidderRoleId;
			else if (businessRoleNo.equals("gz_bidder"))
				roleId = plowBidderRoleId;
			if (StrUtils.isNull(roleId)) {
				ParaMap data = new ParaMap();
				data.put("no", businessRoleNo);
				ParaMap bidderRoleData = dataSetDao.querySimpleData("sys_role", data);
				if (bidderRoleData.getInt("state") == 1 && bidderRoleData.getInt("totalRowCount") > 0) {
					roleId = String.valueOf(bidderRoleData.getRecordValue(0, "id"));
					if (businessRoleNo.equals("td_bidder"))
						landBidderRoleId = roleId;
					else if (businessRoleNo.equals("fc_bidder"))
						houseBidderRoleId = roleId;
					else if (businessRoleNo.equals("kc_bidder"))
						mineralBidderRoleId = roleId;
					else if (businessRoleNo.equals("gz_bidder"))
						plowBidderRoleId = roleId;
				} else
					continue;
			}
			if (StrUtils.isNotNull(roleId))
				roleIds.add(roleId);
		}
		if (roleIds.size() > 0) {
			keyData.put("role_id", roleIds);
			result = dataSetDao.deleteSimpleData("sys_role_rel", keyData);
		} else {
			result = new ParaMap();
			result.put("state", 1);
		}
		return result;
	}
	
	private ParaMap removeBidderRole(String userId, int bidderType) throws Exception {
		if (!(bidderType == 0 || bidderType == 1))
			return null;
		List<String> businessList = new ArrayList();
		if (bidderType == 0)
			businessList.add("land");
		else if (bidderType == 1)
			businessList.add("house");
		return removeBidderRole(userId, businessList);
	}
	
	private ParaMap removeBidderRole(String userId, String businesses) throws Exception {
		if (StrUtils.isNull(businesses))
			return null;
		List<String> businessList = StrUtils.getSubStrs(businesses, ";");
		return removeBidderRole(userId, businessList);
	}
	
	//添加竞买人角色
	private ParaMap addBidderRole(String userId, List<String> businessList) throws Exception {
		ParaMap result = null;
		if (StrUtils.isNull(userId) || businessList == null || businessList.size() == 0)
			return null;
		DataSetDao dataSetDao = new DataSetDao();
		for(int i = 0; i < businessList.size(); i++) {
			String businessNo = businessList.get(i);
			if (StrUtils.isNull(businessNo))
				continue;
			String businessRoleNo = StrUtils.getParamValue(businessRoleNoMap, businessNo);
			if (StrUtils.isNull(businessRoleNo))
				continue;
			String roleId = null;
			if (businessRoleNo.equals("td_bidder"))
				roleId = landBidderRoleId;
			else if (businessRoleNo.equals("fc_bidder"))
				roleId = houseBidderRoleId;
			else if (businessRoleNo.equals("kc_bidder"))
				roleId = mineralBidderRoleId;
			else if (businessRoleNo.equals("gz_bidder"))
				roleId = plowBidderRoleId;
			if (StrUtils.isNull(roleId)) {
				ParaMap data = new ParaMap();
				data.put("no", businessRoleNo);
				ParaMap bidderRoleData = dataSetDao.querySimpleData("sys_role", data);
				if (bidderRoleData.getInt("state") == 1 && bidderRoleData.getInt("totalRowCount") > 0) {
					roleId = String.valueOf(bidderRoleData.getRecordValue(0, "id"));
					if (businessRoleNo.equals("td_bidder"))
						landBidderRoleId = roleId;
					else if (businessRoleNo.equals("fc_bidder"))
						houseBidderRoleId = roleId;
					else if (businessRoleNo.equals("kc_bidder"))
						mineralBidderRoleId = roleId;
					else if (businessRoleNo.equals("gz_bidder"))
						plowBidderRoleId = roleId;
				} else
					continue;
			}
			ParaMap data = new ParaMap();
			data.put("id", null);
			data.put("role_id", roleId);
			data.put("ref_type", 0);
			data.put("ref_id", userId);
			result = dataSetDao.updateData("sys_role_rel", "id", "role_id;ref_type;ref_id", data);
		}
		return result;
	}
	
	private ParaMap addBidderRole(String userId, int bidderType) throws Exception {
		if (!(bidderType == 0 || bidderType == 1||bidderType==3 ||bidderType==2))
			return null;
		List<String> businessList = new ArrayList();
		if (bidderType == 0)
			businessList.add("land");
		else if (bidderType == 1)
			businessList.add("house");
		else if (bidderType == 2)
			businessList.add("mineral");
		else if (bidderType == 3)
			businessList.add("plow");
		return addBidderRole(userId, businessList);
	}
	
	private ParaMap addBidderRole(String userId, String businesses) throws Exception {
		if (StrUtils.isNull(businesses))
			return null;
		List<String> businessList = StrUtils.getSubStrs(businesses, ";");
		return addBidderRole(userId, businessList);
	}
	
	private ParaMap addBidderRole(String userId) throws Exception {
		return addBidderRole(userId, 0);
	}
	
	public ParaMap deleteBidderData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_bidder_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "deleteBidderData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		return dataSetDao.executeSQL(sqlParams);
	}
	
	public ParaMap checkBidderExists(String moduleId, String dataSetNo, ParaMap bidderData) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_bidder_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryBidderExists");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		String id = bidderData.getString("id");
		StringBuffer customCondition = new StringBuffer("");
		if (StrUtils.isNotNull(id)) {
			sqlParams.put("id", id);
			customCondition.append(" and id <> :id");
		}
		if (bidderData.containsKey("cakey")) {//单独检查cakey
			String bidderCakey = bidderData.getString("cakey");
			if (StrUtils.isNotNull(bidderCakey)) {
				sqlParams.put("cakey", bidderCakey);
				customCondition.append(" and exists(select * from sys_user where 1 = 1 ");
				if (StrUtils.isNotNull(id))
					customCondition.append(" and (user_type <> 1 or ref_id <> :id)");
				customCondition.append(" and exists(select * from sys_cakey where user_id = sys_user.id and key = :cakey))");
			}
		} else {//检查竞买人是否存在
			int bidderType = bidderData.getInt("bidder_type");
			sqlParams.put("bidder_type", bidderType);
			customCondition.append(" and bidder_type = :bidder_type ");
			int isCompany = bidderData.getInt("is_company");
			sqlParams.put("is_company", isCompany);
			customCondition.append(" and is_company = :is_company ");
			if(bidderType == 3){
				String name = bidderData.getString("name");
				sqlParams.put("name", name);
				customCondition.append(" and name = :name");
			}
			if (isCompany == 1) {//企业检查名称
				String name = bidderData.getString("name");
				sqlParams.put("name", name);
				customCondition.append(" and name = :name");
			} else {//个人检查身份证
				String certificateNo = bidderData.getString("certificate_no");
				if(StrUtils.isNotNull(certificateNo)){
				sqlParams.put("certificate_no", certificateNo);
				customCondition.append(" and certificate_no = :certificate_no");
				}
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

	/**
	 * 获取指定标的指定类型的关联单位ID列表
	 * @param bidderId 竞买人ID
	 * @param refType 关联单位类型，参考trans_bidder_organ_rel.ref_type
	 * @return
	 * @throws Exception
	 */
	public String getBidderRelOrganIds(String bidderId, int refType) throws Exception{
		if (StrUtils.isNull(bidderId))
			return null;
		if (BIDDER_ORGAN_IDS == null)
			BIDDER_ORGAN_IDS = new HashMap<Integer, Map<String, String>>();
		Map<String, String> map = null;
		if (BIDDER_ORGAN_IDS.containsKey(refType)) {
			map = BIDDER_ORGAN_IDS.get(refType);
		} else {
			map = new HashMap<String, String>();
			BIDDER_ORGAN_IDS.put(refType, map);
		}
		if (map.containsKey(bidderId))
			return map.get(bidderId);
		ParaMap data = new ParaMap();
		data.put("id", bidderId);
		data.put("ref_type", refType);
		DataSetDao dataSetDao = new DataSetDao();
		data = dataSetDao.querySimpleData("trans_bidder_organ_rel", data);
		StringBuffer organIds = new StringBuffer();
		for(int i = 0; i < data.getSize(); i++) {
			organIds.append(String.valueOf(data.getRecordValue(i, "organ_id")) + ";");
		}
		if (organIds.length() > 0)
			organIds.deleteCharAt(organIds.length() - 1);
		map.put("bidderId", organIds.toString());
		return organIds.toString();
	}
	
	/**
	 * 外网注册，修改ukey；
	 * @param bidderData
	 * @return
	 * @throws Exception
	 */
	public ParaMap updateBidderUser(ParaMap bidderData) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result=new ParaMap();
		Date date=new Date();
		int state;
		//检查竞买人名称
		ParaMap q = new ParaMap();
		String name = "BIDDER_"+bidderData.getString("name");
		q.put("user_name", name);
		ParaMap returnData=dataSetDao.querySimpleData("sys_user", q);
		List l=returnData.getRecords();
		if(l.size()>1){//只适用公司
			result.clear();
			result.put("state", 0);
			result.put("message", "数据异常，请联系管理员（user_n）");
			return result;
		}else{
			bidderData.put("userid", returnData.getRecordString(0,"id"));
			bidderData.put("ref_id", returnData.getRecordString(0,"ref_id"));//biderId
			bidderData.put("id", returnData.getRecordString(0,"ref_id"));//biderId
		}
		ParaMap data = new ParaMap();
		ParaMap format = new ParaMap();
		format.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		format.put("active_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		
        //修改竞买人信息
		dataSetDao.updateData("trans_bidder", "id", bidderData, format);
		//新增竞买人用户，如果存在则不需要处理
		String userId = bidderData.getString("userid");
		if (bidderData.containsKey("userid")&&bidderData.containsKey("ref_id")) {
			    
				data.clear();
				data.put("id", userId);
				data.put("user_name", name);
				data.put("password", "");
				data.put("user_type", 1);
				data.put("status", 1);
				data.put("ref_table_name", "trans_bidder");
				data.put("ref_id", bidderData.getString("ref_id"));
				data.put("validity", 730);
				data.put("active_date",DateUtils.getStr(date));
				data.put("create_date",DateUtils.getStr(date));
				dataSetDao.updateData("sys_user", "id", data, format);
			}
			//保存竞买人CAKEY
		    String cakey=bidderData.getString("cakey");
			if (bidderData.containsKey("cakey")) {
				if ( StrUtils.isNotNull(cakey)) {
					data.clear();
					data.put("user_id", userId);
					if (StrUtils.isNotNull(cakey))
						data.put("key", cakey);
					dataSetDao.updateData("sys_cakey", "user_id", data, null);
				}
			}
	
		result.clear();
		result.put("state", 1);
		return result;
	}
	
	public ParaMap getBidderContactData(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_bidder_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryBidderContactData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("bidder_id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
}
