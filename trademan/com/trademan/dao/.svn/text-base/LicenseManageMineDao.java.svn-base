package com.trademan.dao;

import java.util.ArrayList;
import java.util.List;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.bidder.dao.TransLicenseMineDao;
import com.sms.dao.SmsDao;
import com.sysman.dao.FieldChangeDao;
import com.sysman.dao.OperateLogDao;

/**
 * 竞买申请管理DAO
 * @author SAMONHUA
 * 2012-05-11
 */
public class LicenseManageMineDao extends BaseDao {
	public ParaMap getLicenseListData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_license_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryLicenseListData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getBidderLicenseListData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_license_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryBidderLicenseListData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getBidderLicenseListData(String dataSetNo, ParaMap sqlParams) throws Exception {
		return getBidderLicenseListData(null, dataSetNo, sqlParams);
	}
	
	public ParaMap getBidderLicenseListData(ParaMap sqlParams) throws Exception {
		return getBidderLicenseListData(null, null, sqlParams);
	}
	
	public ParaMap getConfirmLicenseListData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_license_confirm");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryConfirmLicenseList");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	/**
	 * 检查竞买人是指定竞买申请的主竞买人、被联合竞买人或者无关
	 * @param licenseId 竞买申请ID
	 * @param bidderId 竞买人ID
	 * @return 返回值中键state值：0查询失败，1不存在竞买人，2主竞买人，3联合竞买人
	 * @throws Exception
	 */
	public ParaMap checkLicenseBidder(String licenseId, String bidderId) throws Exception {
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_license_list");
		sqlParams.put("dataSetNo", "checkLicenseBidder");
		sqlParams.put("id", licenseId);
		sqlParams.put("bidder_id", bidderId);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getInt("state") == 1) {
			int checkState = result.getRecordInteger(0, 0);
			result.remove("fs");
			result.remove("rs");
			result.put("state", checkState + 1);
		}
		return result;
	}
	
	/**
	 * 检查竞买人是指定竞买申请的主竞买人、被联合竞买人或者无关
	 * @param licenseId 竞买申请ID
	 * @param userId 竞买人登录用户ID
	 * @return 返回值中键state值：0查询失败，1不存在竞买人，2主竞买人，3联合竞买人
	 * @throws Exception
	 */
	public ParaMap checkLicenseBidderByUserId(String licenseId, String userId) throws Exception {
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_license_list");
		sqlParams.put("dataSetNo", "checkLicenseBidderByUserId");
		sqlParams.put("id", licenseId);
		sqlParams.put("user_id", userId);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getInt("state") == 1) {
			int checkState = result.getRecordInteger(0, 0);
			result.remove("fs");
			result.remove("rs");
			result.put("state", checkState + 1);
		}
		return result;
	}
	
	/**
	 * 检查被联合竞买申请的竞买人能否确认通过
	 * @param licenseId 竞买申请ID
	 * @param bidderId 竞买人ID
	 * @return 返回值中键state值：1可以确认通过，其它值不能确认通过
	 * @throws Exception
	 */
	public ParaMap checkAllowConfirmUnionLicense(String licenseId, String bidderId) throws Exception {
		//首先竞买人必须是指定竞买申请的被联合竞买人
		ParaMap result = checkLicenseBidder(licenseId, bidderId);
		if (result.getInt("state") == 3) {
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap keyData = new ParaMap();
			keyData.put("id", licenseId);
			result = dataSetDao.querySimpleData("trans_license", keyData);
			if (result.getInt("state") == 1 && result.getInt("totalRowCount") > 0) {
				String targetId = result.getRecordString(0, "target_id");
				//检查竞买人不能对指定标的有独立竞买申请或者作为主申请人的联合竞买申请
				keyData.clear();
				keyData.put("target_id", targetId);
				keyData.put("bidder_id", bidderId);
				result = dataSetDao.querySimpleData("trans_license", keyData);
				if (result.getInt("state") == 1 && result.getInt("totalRowCount") > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "竞买人已经对指定标的有独立竞买申请或者作为主申请人进行了联合竞买申请，故指定标的以被联合竞买人身份不能确认通过");
				} else {
					//检查竞买人对指定标的是否有其它的被联合身份的竞买申请已经确认
					keyData.clear();
					keyData.put("moduleNo", "trans_license_list");
					keyData.put("dataSetNo", "queryBidderUnionLicense");
					keyData.put("target_id", targetId);
					keyData.put("bidder_id", bidderId);
					result = dataSetDao.queryData(keyData);
					if (result.getInt("state") == 1 && result.getInt("totalRowCount") > 0) {
						boolean blnChecked = false;
						for(int i = 0; i < result.getSize(); i++) {
							if (!result.getRecordString(i, "license_id").equalsIgnoreCase(licenseId)
									&& result.getRecordString(i, "status").equals("1")) {
								result.clear();
								result.put("state", 0);
								result.put("message", "竞买人已经对指定标的有被联合竞买申请并且已经确认通过");
								blnChecked = true;
								break;
							}
						}
						if (!blnChecked) {
							result.clear();
							result.put("state", 1);
						}
					}
				}
			}
		} else {
			result.clear();
			result.put("state", 0);
			result.put("message", "竞买人并非指定竞买申请的联合竞买人");
		}
		return result;
	}
	
	/**
	 * 检查被联合竞买申请的竞买人能否确认通过
	 * @param licenseId 竞买申请ID
	 * @param userId 竞买人登录用户ID
	 * @return 返回值中键state值：1可以确认通过，其它值不能确认通过
	 * @throws Exception
	 */
	public ParaMap checkAllowConfirmUnionLicenseByUserId(String licenseId, String userId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", userId);
		keyData.put("user_type", 1);
		ParaMap result = dataSetDao.querySimpleData("sys_user", keyData);
		if (result.getInt("state") == 1) {
			if (result.getInt("totalRowCount") == 0) {
				result.clear();
				result.put("state", 0);
				result.put("message", "当前用户不存在或者非竞买人");
				return result;
			} else {
				return checkAllowConfirmUnionLicense(licenseId, result.getRecordString(0, "ref_id"));
			}
		} else {
			return result;
		}
	}
	
	/**
	 * 检查竞买人能否对指定标的进行竞买申请
	 * @param targetId 准备竞买申请的标的ID
	 * @param bidderId 竞买人ID
	 * @return 返回值中键state值：1可以申请，其它值不能申请
	 * @throws Exception
	 */
	public ParaMap checkAllowApplyLicense(String targetId, String bidderId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		//首先检查竞买是否已经对指定标的提交了竞买申请
		ParaMap keyData = new ParaMap();
		keyData.put("target_id", targetId);
		keyData.put("bidder_id", bidderId);
		ParaMap result = dataSetDao.querySimpleData("trans_license", keyData);
		if (result.getInt("state") == 1) {
			if (result.getInt("totalRowCount") == 0) {
				//检查竞买人对指定标的是否有其它的被联合身份的竞买申请已经确认
				boolean blnUnionConfirmed = false;
				keyData.clear();
				keyData.put("moduleNo", "trans_license_list");
				keyData.put("dataSetNo", "queryBidderUnionLicense");
				keyData.put("target_id", targetId);
				keyData.put("bidder_id", bidderId);
				result = dataSetDao.queryData(keyData);
				if (result.getInt("state") == 1 && result.getInt("totalRowCount") > 0) {
					for(int i = 0; i < result.getSize(); i++) {
						if (result.getRecordValue(i, "status").equals("1")) {
							result.clear();
							result.put("state", 0);
							result.put("message", "竞买人已经对指定标的有被联合竞买申请并且已经确认通过");
							blnUnionConfirmed = true;
							break;
						}
					}
				}
				if (!blnUnionConfirmed) {
					keyData.clear();
					keyData.put("id", targetId);
					result = dataSetDao.querySimpleData("trans_target", keyData, null, "id, status, is_valid, end_apply_time");
					if (result.getInt("state") == 1 && result.getInt("totalRowCount") > 0) {
						String targetIsValid = result.getRecordString(0, "is_valid");
						String targetStatus = result.getRecordString(0, "status");
						String targetEndApplyTime = result.getRecordString(0, "end_apply_time");
						result.clear();
						if (StrUtils.isNull(targetIsValid) || !targetIsValid.equals("1")) {
							result.put("state", 0);
							result.put("message", "标的处于无效状态");
						} else if (StrUtils.isNull(targetStatus) || !targetStatus.equals("3")) {
							result.put("state", 0);
							result.put("message", "标的未处于可竞买申请的公告状态");
						} else if (StrUtils.isNull(targetEndApplyTime) || !StrUtils.isDateTime(targetEndApplyTime)
								|| StrUtils.stringToDate(targetEndApplyTime, "yyyy-MM-dd HH:mm:ss").before(new java.util.Date())) {
							result.put("state", 0);
							result.put("message", "标的已过竞买申请截止时间");
						} else
							result.put("state", 1);
					} else {
						result.clear();
						result.put("state", 0);
						result.put("message", "标的不存在");
					}
				}
			} else {
				result.clear();
				result.put("state", 0);
				result.put("message", "竞买人已经对指定标的提交了竞买申请");
			}
		}
		return result;
	}
	
	/**
	 * 检查竞买人能否对指定标的进行竞买申请
	 * @param targetId 准备竞买申请的标的ID
	 * @param userId 竞买人登录用户ID
	 * @return 返回值中键state值：1可以申请，其它值不能申请
	 * @throws Exception
	 */
	public ParaMap checkAllowApplyLicenseByUserId(String targetId, String userId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", userId);
		keyData.put("user_type", 1);
		ParaMap result = dataSetDao.querySimpleData("sys_user", keyData);
		if (result.getInt("state") == 1) {
			if (result.getInt("totalRowCount") == 0) {
				result.clear();
				result.put("state", 0);
				result.put("message", "当前用户不存在或者非竞买人");
				return result;
			} else {
				return checkAllowApplyLicense(targetId, result.getRecordString(0, "ref_id"));
			}
		} else {
			return result;
		}
	}
	
	/**
	 * 联合竞买人确认联合竞买申请
	 * @param moduleId 模块ID
	 * @param dataSetNo 数据集ID
	 * @param licenseData 竞买申请数据
	 * @return 返回值中键state值：1执行成功，0检查message
	 * @throws Exception
	 */
	public ParaMap confirmUnionLicense(String moduleId, String dataSetNo, ParaMap licenseData) throws Exception {
		String confirmed = licenseData.containsKey("confirmed") ? licenseData.getString("confirmed") : "2";
		String licenseId = licenseData.getString("id");
		if(confirmed.equals("1")){
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap keyData = new ParaMap();
			keyData.put("id", licenseId);
			ParaMap licenseMap = dataSetDao.querySimpleData("trans_license", keyData);
			if(licenseMap!=null && licenseMap.getSize()>0){
				String targetId = licenseMap.getRecordString(0, "target_id");
				keyData.clear();
				keyData.put("id", targetId);
				ParaMap targetMap = dataSetDao.querySimpleData("trans_target", keyData);
				if(targetMap!=null && targetMap.getSize()>0){
					int isStop = targetMap.getRecordInteger(0, "is_stop");
					if(isStop == 1){
						ParaMap result = new ParaMap();
						result.put("state", 0);
						result.put("message", "该标的已终止！");
						return result;
					}
					int isSuspend = targetMap.getRecordInteger(0, "is_suspend");
					if(isSuspend == 1){
						ParaMap result = new ParaMap();
						result.put("state", 0);
						result.put("message", "该标的已中止！");
						return result;
					}
				}
			}
		}
		ParaMap result = null;
		if (confirmed.equals("1"))
			result = licenseData.containsKey("bidder_id")
					? checkAllowConfirmUnionLicense(licenseId, licenseData.getString("bidder_id"))
					: checkAllowConfirmUnionLicenseByUserId(licenseId, licenseData.getString("u"));
		if (!confirmed.equals("1") || result.getInt("state") == 1) {
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap keyData = new ParaMap();
			String bidderId = licenseData.getString("bidder_id");
			if (StrUtils.isNull(bidderId)) {
				String userId = licenseData.getString("u");
				keyData.put("id", userId);
				keyData.put("user_type", 1);
				result = dataSetDao.querySimpleData("sys_user", keyData);
				if (result.getInt("state") == 1) {
					if (result.getInt("totalRowCount") == 0) {
						result.clear();
						result.put("state", 0);
						result.put("message", "当前用户不存在或者非竞买人");
						return result;
					} else {
						bidderId = result.getRecordString(0, "ref_id");
					}
				} else {
					return result;
				}
			}
			keyData.clear();
			keyData.put("license_id", licenseId);
			keyData.put("bidder_id", bidderId);
			if (confirmed.equals("1"))
				keyData.put("status", 1);
			else
				keyData.put("status", 2);
			result = dataSetDao.updateData("trans_license_union", "", "license_id;bidder_id", keyData);
			//尝试通过竞买申请
			if (result.getInt("state") == 1) {
				if (confirmed.equals("1")) {
					ParaMap setPassResult = setLicenseStatusPass(null, null, licenseId);
					if (setPassResult.getInt("state") == 1) {
						result.put("licenseStatus", 1);
					}
				}
				//发送短信
				ParaMap bidderSmsData = getLicenseData(licenseId, bidderId);
				SmsDao smsDao = new SmsDao();
				smsDao.sendMessage(SmsDao.CATEGORY_TransUnionConfirm, getLicenseBidder(licenseId, "mobile"),
						bidderSmsData.getRecordString(0, "bidder_name") + "：您好，您申请的标的“"
						+ bidderSmsData.getRecordString(0, "target_name") + "”的竞买申请("
						+ bidderSmsData.getRecordString(0, "license_no") + ")联合竞买人"
						+ bidderSmsData.getRecordString(0, "union_bidder_name") + "已经"
						+ (confirmed.equals("1") ? "确认通过" : "拒绝") + "。谢谢！");
				smsDao.sendMessage(SmsDao.CATEGORY_TransUnionConfirm1, getLicenseUnionBidder(licenseId, "mobile"),
						"竞买人您好，您同" + bidderSmsData.getRecordString(0, "bidder_name") + "联合申请的标的“"
						+ bidderSmsData.getRecordString(0, "target_name") + "”的竞买申请("
						+ bidderSmsData.getRecordString(0, "license_no") + ")联合竞买人"
						+ bidderSmsData.getRecordString(0, "union_bidder_name") + "已经"
						+ (confirmed.equals("1") ? "确认通过" : "拒绝") + "。谢谢！");
				OperateLogDao.writeInfo("联合竞买申请（查看ref_field_value）"
						+ (licenseData.containsKey("u") ? "由登录用户（" + licenseData.getString("u") + "）" : "")
						+ (confirmed.equals("1") ? "确认" : "拒绝") + "。",
						"confirmUnionLicense", "trans_license_union", "license_id;bidder_id", licenseId + ";" + bidderId);
			}
		}
		return result;
	}
	
	/**
	 * 检查竞买申请的竞买人
	 * @param moduleId 模块ID
	 * @param dataSetNo 数据集编号
	 * @param id 竞买申请ID
	 * @return 返回值中键state值，0是查询失败，其它值参考键message
	 * @throws Exception
	 */
	public ParaMap checkLicenseStatusPass(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_license_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "checkLicenseStatusPass");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getInt("state") == 1) {
			int checkState = result.getRecordInteger(0, 0);
			if (checkState >= 0)
				checkState += 1;
			result.remove("fs");
			result.remove("rs");
			result.put("state", checkState);
			switch(checkState) {
			case 2: break;
			case 1: result.put("message", "竞买申请自身状态已经为已通过"); break;
			case -1: result.put("message", "竞买申请自身状态已经结束交易"); break;
			case -2: result.put("message", "保证金未交或者未交足"); break;
			case -3: result.put("message", "有竞买条件但暂未审批通过"); break;
			case -4: result.put("message", "联合竞买有未确认、拒绝或者未通过的"); break;
			case -5: result.put("message", "标的状态为开始交易或者交易结束"); break;
			case -6: result.put("message", "交易时间已过"); break;
			default: result.put("message", "其它原因不允许通过");
			}
		}
		return result;
	}
	
	/**
	 * 竞买申请确认通过(trans_license.status=1)
	 * @param moduleId 模块ID
	 * @param dataSetNo 数据集ID
	 * @param id 竞买申请ID
	 * @return 返回值中键state值：1执行成功，0检查message
	 * @throws Exception
	 */
	public ParaMap setLicenseStatusPass(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap result = checkLicenseStatusPass(null, null, id);
		int state = result.getInt("state");
		if (state == 2) {
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap licenseData = new ParaMap();
			//获取竞买申请信息
			licenseData.put("id", id);
			licenseData = dataSetDao.querySimpleData("trans_license", licenseData);
			String targetId = licenseData.getRecordString(0, "target_id");
			String bidderId = licenseData.getRecordString(0, "bidder_id");
			//获取牌号
			ParaMap cardNoData = getNewCardNo(null, null, targetId);
			int cardNo = cardNoData.getInt("no");
			licenseData.clear();
			licenseData.put("id", id);
			licenseData.put("status", 1);
			licenseData.put("care_no", cardNo);
			result = dataSetDao.updateData("trans_license", "", "id", licenseData);
			if (result.getInt("state") == 1) {
				//获取竞买人登录用户信息
				ParaMap userData = new ParaMap();
				userData.put("user_type", 1);
				userData.put("ref_id", bidderId);
				userData = dataSetDao.querySimpleData("sys_user", userData);
				String userId = null;
				if (userData.getInt("state") == 1 && userData.getInt("totalRowCount") > 0) {
					userId = userData.getRecordString(0, "id");
					//获取标的businessType
					ParaMap targetData = new ParaMap();
					targetData.put("id", targetId);
					targetData.put("is_valid", 1);
					targetData = dataSetDao.querySimpleData("trans_target", targetData);
					if(targetData.getInt("state") == 1 && targetData.getInt("totalRowCount") > 0){
						String businessType = targetData.getRecordString(0, "business_type");
						if(businessType.startsWith("101")){
							com.tradeland.engine.EngineDao engineDao = new com.tradeland.engine.EngineDao();
							engineDao.addUserId(targetId, userId);
						}else if(businessType.startsWith("301") ||businessType.startsWith("401")  ){
							com.trademine.engine.EngineDao engineDao = new com.trademine.engine.EngineDao();
							engineDao.addUserId(targetId, userId);
						}else if(businessType.startsWith("501")){
							com.tradeplow.engine.EngineDao engineDao = new com.tradeplow.engine.EngineDao();
							engineDao.addUserId(targetId, userId);
						}
						
					}
				}
				//发送短信
				ParaMap bidderSmsData = getLicenseData(id);
				SmsDao smsDao = new SmsDao();
				smsDao.sendMessage(SmsDao.CATEGORY_TransLicensePass, getLicenseBidder(id, "mobile"),
						bidderSmsData.getRecordString(0, "bidder_name") + "：您好，您申请的标的“"
						+ bidderSmsData.getRecordString(0, "target_name") + "”的竞买申请("
						+ bidderSmsData.getRecordString(0, "license_no") + ")已经通过，请在规定的时间参与报价。谢谢！");
				smsDao.sendMessage(SmsDao.CATEGORY_TransLicensePass1, getLicenseUnionBidder(id, "mobile"),
						"竞买人您好，您同" + bidderSmsData.getRecordString(0, "bidder_name") + "联合申请的标的“"
						+ bidderSmsData.getRecordString(0, "target_name") + "”的竞买申请("
						+ bidderSmsData.getRecordString(0, "license_no") + ")已经通过。谢谢！");
			}
		} else if (state <= 0) {
			result.put("state", 0);
		}
		return result;
	}
	
	public ParaMap getLicenseCertData(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap result = new ParaMap();
		ParaMap queryData;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		queryData = dataSetDao.querySimpleData("trans_license", keyData);
		if (queryData.getInt("state") == 1) {
			if (queryData.getInt("totalRowCount") == 0) {
				result.put("state", 0);
				result.put("message", "竞买申请数据不存在");
			} else if ((Integer) queryData.getRecordValue(0, "is_valid") != 1) {
				result.put("state", 0);
				result.put("message", "竞买申请数据无效");
			} else if ((Integer) queryData.getRecordValue(0, "status") == 0) {
				result.put("state", 0);
				result.put("message", "竞买申请资格未通过");
			} else {
				result.put("id", id);
				result.put("state", 1);
				ParaMap sqlParams = new ParaMap();
				if (StrUtils.isNull(moduleId))
					sqlParams.put("moduleNo", "trans_license_list01");
				else
					sqlParams.put("moduleId", moduleId);
				sqlParams.put("id", id);
				//查询竞买人、标的数据
				sqlParams.put("dataSetNo", "queryLicenseData");
				queryData = dataSetDao.queryData(sqlParams);
				result.put("bidderName", queryData.getRecordValue(0, "bidder_name"));
				result.put("targetName", queryData.getRecordValue(0, "target_name"));
				result.put("applyDate", queryData.getRecordValue(0, "apply_date"));
				//查询联合竞买人数据
				sqlParams.put("dataSetNo", "queryLicenseUnionBidderData");
				queryData = dataSetDao.queryData(sqlParams);
				result.put("unionBidderCount", queryData.getInt("totalRowCount"));
				for(int i = 0; i < queryData.getRecords().size(); i++) {
					result.put("unionBidderName" + i, queryData.getRecordValue(i, "bidder_name"));
				}
			}
		}
		return result;
	}
	
	public ParaMap getLicenseTransCertData(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap result = new ParaMap();
		ParaMap queryData;
		DataSetDao dataSetDao = new DataSetDao();
		/*ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		queryData = dataSetDao.querySimpleData("trans_license", keyData);
		if (queryData.getInt("state") == 1) {
			if (queryData.getInt("totalRowCount") == 0) {
				result.put("state", 0);
				result.put("message", "竞买申请数据不存在");
			} else if ((Integer) queryData.getRecordValue(0, "is_valid") != 1) {
				result.put("state", 0);
				result.put("message", "竞买申请数据无效");
			} else if ((Integer) queryData.getRecordValue(0, "status") == 3 || (Integer) queryData.getRecordValue(0, "status") == 4) {*/
				result.put("id", id);
				result.put("state", 1);
				ParaMap sqlParams = new ParaMap();
				if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
					sqlParams.put("moduleNo", "trans_license_list01");
				else
					sqlParams.put("moduleId", moduleId);
				sqlParams.put("id", id);
				//查询竞买人、标的数据
				sqlParams.put("dataSetNo", "queryLicenseData");
				queryData = dataSetDao.queryData(sqlParams);
				result.put("bidderName", queryData.getRecordValue(0, "bidder_name"));
				result.put("allUnionBidderName", queryData.getRecordValue(0, "all_union_bidder_name"));
				result.put("bidderCorporation", queryData.getRecordValue(0, "bidder_corporation"));
				result.put("bidderAddress", queryData.getRecordValue(0, "bidder_address"));
				result.put("endTransTime", queryData.getRecordValue(0, "end_trans_time"));
				result.put("targetName", queryData.getRecordValue(0, "target_name"));
				result.put("applyDate", queryData.getRecordValue(0, "apply_date"));
				result.put("transPrice", queryData.getRecordValue(0, "trans_price"));
				result.put("transDate", queryData.getRecordValue(0, "trans_date"));
				result.put("unit", queryData.getRecordValue(0, "unit"));
				result.put("bidderTell", queryData.getRecordValue(0, "bidder_tell"));
				
				if( queryData.getRecordValue(0, "trans_type").equals("0")){//按照需求挂牌修改为集中竞价结束时间，拍卖修改为拍卖开始时间
					result.put("beginListTime", queryData.getRecordValue(0, "end_focus_time"));
				}else{
					result.put("beginListTime", queryData.getRecordValue(0, "begin_limit_time"));//按照需求修改为集中竞价结束时间
				}
//				result.put("beginListTime", queryData.getRecordValue(0, "begin_list_time"));
				result.put("endListTime", queryData.getRecordValue(0, "begin_notice_time"));
				result.put("postCode", queryData.getRecordValue(0, "post_code"));
				String target_id = (String) queryData.getRecordValue(0, "target_id");
				int targetTransType = queryData.getRecordInteger(0, "trans_type");
				int targetIsLimitTrans = queryData.getRecordInteger(0, "is_limit_trans");
				if (targetTransType == 1 || targetIsLimitTrans == 1)
					result.put("limitDate", queryData.getRecordValue(0, "begin_limit_time"));
				else
					result.put("limitDate", queryData.getRecordValue(0, "begin_focus_time"));
				//查询联合竞买人数据
				sqlParams.put("dataSetNo", "queryLicenseUnionBidderData");
				queryData = dataSetDao.queryData(sqlParams);
				result.put("unionBidderCount", queryData.getInt("totalRowCount"));
				for(int i = 0; i < queryData.getRecords().size(); i++) {
					result.put("unionBidderName" + i, queryData.getRecordValue(i, "bidder_name"));
					result.put("unionBidderCorporation" + i, queryData.getRecordValue(i, "bidder_corporation"));
					result.put("unionBidderAddress" + i, queryData.getRecordValue(i, "bidder_address"));
				}
				//根据targetid找trans_goods表信息和扩展表信息,一个标的有可能捆绑多个矿区
				
				ParaMap goodsParamMap = new ParaMap();
				ParaMap goodsData = new ParaMap();
				if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
					goodsParamMap.put("moduleNo", "trans_license_list01");
				else
					goodsParamMap.put("moduleId", moduleId);
				goodsParamMap.put("dataSetNo", "queryMoreTransgoods");
				goodsParamMap.put("target_id", target_id);
				goodsData = dataSetDao.queryData(goodsParamMap);
				//多条数据进行处理
				String goodsAddress = "";
				String goodsName = "";
				String mineral_area = "";
				String mineral_use_years = "";
				for(int i = 0; i < goodsData.getRecords().size(); i++) {
					if(goodsData.getRecords().size()==1){
						goodsAddress = (String) goodsData.getRecordValue(i, "address");
						goodsName = (String) goodsData.getRecordValue(i, "name");
						mineral_area = (String) goodsData.getRecordValue(i, "mineral_area")+"平方公里";
						mineral_use_years = (String) goodsData.getRecordValue(i, "mineral_use_years")+"年";
					}else{
						if(i==0){
							goodsAddress = (String) goodsData.getRecordValue(i, "address");
							goodsName = (String) goodsData.getRecordValue(i, "name");
							mineral_area = (String) goodsData.getRecordValue(i, "mineral_area") + "平方公里";
							mineral_use_years = (String) goodsData.getRecordValue(i, "mineral_use_years") + "年";
						}else{
							goodsAddress = goodsAddress +";"+ (String) goodsData.getRecordValue(i, "address");
							goodsName = goodsName +";"+ (String) goodsData.getRecordValue(i, "name");
							mineral_area = mineral_area +";"+ (String) goodsData.getRecordValue(i, "mineral_area") + "平方公里";
							mineral_use_years = mineral_use_years +";"+ (String) goodsData.getRecordValue(i, "mineral_use_years") + "年";
						}
					}
				}
				result.put("goodsAddress",goodsAddress);
				result.put("goodsName",goodsName);
				result.put("mineral_area",mineral_area);
				result.put("mineral_use_years",mineral_use_years);
		return result;
	}
	
	//为新的竞买申请生成竞买号
	public ParaMap getNewLicenseNo(String moduleId, String dataSetNo, String targetId) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_license_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryNewLicenseNo");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getInt("state") == 1 && result.getInt("totalRowCount") > 0) {
			String newNo = result.getRecordString(0, 0);
			result.clear();
			result.put("state", 1);
			result.put("no", newNo);
		} else {
			result.clear();
			result.put("state", 0);
			result.put("message", "获取新的竞买申请号失败");
		}
		return result;
	}
	
	//为新的竞买申请生成资格确认书编号
	public ParaMap getNewApplyNo(String moduleId, String dataSetNo, String targetId) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_license_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryNewApplyNo");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getInt("state") == 1 && result.getInt("totalRowCount") > 0) {
			String newNo = result.getRecordString(0, 0);
			result.clear();
			result.put("state", 1);
			result.put("no", newNo);
		} else {
			result.clear();
			result.put("state", 0);
			result.put("message", "获取新的竞买申请资格确认书编号失败");
		}
		return result;
	}
	
	//为新的竞买申请生成入账申请单流水号
	public ParaMap getNewOrderNo(String moduleId, String dataSetNo, String targetId) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_license_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryNewOrderNo");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getInt("state") == 1 && result.getInt("totalRowCount") > 0) {
			String newNo = result.getRecordString(0, 0);
			result.clear();
			result.put("state", 1);
			result.put("no", newNo);
		} else {
			result.clear();
			result.put("state", 0);
			result.put("message", "获取新的竞买申请入账申请单流水号失败");
		}
		return result;
	}
	
	//为新的竞买申请生成成交确认书编号
	public ParaMap getNewTransNo(String moduleId, String dataSetNo, String targetId) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_license_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryNewTransNo");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getInt("state") == 1 && result.getInt("totalRowCount") > 0) {
			String newNo = result.getRecordString(0, 0);
			result.clear();
			result.put("state", 1);
			result.put("no", newNo);
		} else {
			result.clear();
			result.put("state", 0);
			result.put("message", "获取新的竞买申请成交确认书编号失败");
		}
		return result;
	}
	
	//为新的竞买申请获取可用的牌号(并发会出现重复牌号)
	public ParaMap getNewCardNo(String moduleId, String dataSetNo, String targetId) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_license_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryNewCardNo");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getInt("state") == 1 && result.getInt("totalRowCount") > 0) {
			String newNo = result.getRecordString(0, 0);
			int cardNo = StrUtils.isInteger(newNo) ? Integer.parseInt(newNo) : -1;
			result.clear();
			result.put("state", 1);
			result.put("no", cardNo);
		} else {
			result.clear();
			result.put("state", 0);
			result.put("message", "获取新的竞买申请牌号失败");
		}
		return result;
	}
	
	//竞买标的审核（仅当有标的有资格条件时）
	public ParaMap confirmLicense(String moduleId, String dataSetNo, ParaMap confirmData) throws Exception {
		String id = confirmData.getString("id");
		int confirmStatus = confirmData.getInt("confirmStatus");
		String confirmOpinion = confirmData.getString("confirmOpinion");
		ParaMap result = new ParaMap();
		if (StrUtils.isNull(id)) {
			result.put("state", 0);
			result.put("message", "竞买申请无效");
			return result;
		}
		if (confirmStatus != 0 && confirmStatus != 1 && confirmStatus != 2) {
			result.put("state", 0);
			result.put("message", "竞买申请审批不合法");
			return result;
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap licenseData = new ParaMap();
		licenseData.put("id", id);
		licenseData.put("confirmed", confirmStatus);
		licenseData.put("confirmed_opinion", confirmOpinion);
		licenseData.put("confirmed_date", null);
		ParaMap format = new ParaMap();
		format.put("confirmed_date", "sysdate");
		result = dataSetDao.updateData("trans_license", null, "id", licenseData, format);
		if (result.getInt("state") == 1) {
			//查询竞买申请数据
			licenseData = getLicenseData(id);
			String targetId = licenseData.getRecordString(0, "target_id");
			String targetName = licenseData.getRecordString(0, "target_name");
			String bidderId = licenseData.getRecordString(0, "bidder_id");
			String bidderName = licenseData.getRecordString(0, "bidder_name");
			String licenseNo = licenseData.getRecordString(0, "license_no");
			result.put("confirmState", 1);
			if (confirmStatus == 1) {//审批通过，尝试通过竞买申请
				ParaMap setPassResult = setLicenseStatusPass(moduleId, dataSetNo, id);
				if (setPassResult.getInt("state") == 1) {
					result.put("licenseStatus", 1);
				}
			} else {
				//如果竞买申请状态为1则修改回0
				ParaMap userData = new ParaMap();
				userData.put("user_type", 1);
				userData.put("ref_id", bidderId);
				userData = dataSetDao.querySimpleData("sys_user", userData);
				String userId = null;
				if (userData.getInt("state") == 1 && userData.getInt("totalRowCount") > 0) {
					userId = userData.getRecordString(0, "id");
					//清除报价引擎中的相关数据
					ParaMap targetData = new ParaMap();
					targetData.put("id", targetId);
					targetData.put("is_valid", 1);
					targetData = dataSetDao.querySimpleData("sys_target", userData);
					if(targetData.getInt("state") == 1 && targetData.getInt("totalRowCount") > 0){
						String businessType = licenseData.getRecordString(0, "business_type");
						if(businessType.startsWith("101")){
							com.tradeland.engine.EngineDao engineDao = new com.tradeland.engine.EngineDao();
							engineDao.removeUserId(targetId, userId);
						}else if(businessType.startsWith("301") ||businessType.startsWith("401")  ){
							com.trademine.engine.EngineDao engineDao = new com.trademine.engine.EngineDao();
							engineDao.removeUserId(targetId, userId);
						}else if(businessType.startsWith("501")){
							com.tradeplow.engine.EngineDao engineDao = new com.tradeplow.engine.EngineDao();
							engineDao.removeUserId(targetId, userId);
						}
						
					}
				}
				//如果竞买申请处于“已通过”状态，则还原为未通过
				if (licenseData.getRecordString(0, "status").equals("1")) {
					licenseData.clear();
					licenseData.put("id", id);
					licenseData.put("status", 0);
					result = dataSetDao.updateData("trans_license", null, "id", licenseData);
				}
			}
			//发送短信
			SmsDao smsDao = new SmsDao();
			smsDao.sendMessage(SmsDao.CATEGORY_TransLicensePass, getLicenseBidder(id, "mobile"),
					bidderName + "：您好，您申请的标的“"
					+ targetName + "”的竞买申请("
					+ licenseNo + ")已经通过资格审核。谢谢！");
			smsDao.sendMessage(SmsDao.CATEGORY_TransLicensePass1, getLicenseUnionBidder(id, "mobile"),
					"竞买人您好，您同" + bidderName + "联合申请的标的“"
					+ targetName + "”的竞买申请("
					+ licenseNo + ")已经通过资格审核。谢谢！");
		}
		return result;
	}
	
	public ParaMap getConfirmLicenseData(String moduleId, String dataSetNo, String id) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_license_confirm");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryLicenseData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap deleteLicenseData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		String bidderId = null;
		String targetId = null;
		ParaMap data = new ParaMap();
		data.put("id", id);
		data = dataSetDao.querySimpleData("trans_license", data);
		if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
			if (data.getRecordInteger(0, "status") != 0) {
				ParaMap result = new ParaMap();
				result.put("state", 0);
				result.put("message", "指定竞买申请处于已经通过或者其它不允许删除的状态。");
				return result;
			}
			if (data.getRecordInteger(0, "earnest_money_pay") != 0) {
				ParaMap result = new ParaMap();
				result.put("state", 0);
				result.put("message", "指定竞买申请已交保证金款。");
				return result;
			}
			bidderId = data.getRecordString(0, "bidder_id");
			targetId = data.getRecordString(0, "target_id");
			data.clear();
			data.put("id", targetId);
			data = dataSetDao.querySimpleData("trans_target", data);
			if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
				if (data.getRecordInteger(0, "status") >= 4) {
					ParaMap result = new ParaMap();
					result.put("state", 0);
					result.put("message", "指定竞买申请标的已经开始交易或者交易结束，不能删除。");
					return result;
				}
			}
		} else {
			ParaMap result = new ParaMap();
			result.put("state", 0);
			result.put("message", "未查询到竞买申请。");
			return result;
		}
		data.clear();
		data.put("apply_type", 0);
		data.put("ref_id", id);
		data = dataSetDao.querySimpleData("trans_account_bill_apply", data);
		if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
			String applyId = data.getRecordString(0, "id");
			String childAccountNo = data.getRecordString(0, "id");
			List statusList = new ArrayList();
			statusList.add(1);
			statusList.add(2);
			data.clear();
			data.put("apply_id", applyId);
			data.put("status", statusList);
			data = dataSetDao.querySimpleRowCount("trans_account_bill", data);
			if (data.getInt("state") == 1 && data.getInt("rowCount") > 0) {
				ParaMap result = new ParaMap();
				result.put("state", 0);
				result.put("message", "已经有保证金或者其它款项转入当前竞买申请子账号，不允许删除竞买申请。");
				return result;
			}
			//销户，先判断不是基本账号
			/*data.clear();
			data.put("no", childAccountNo);
			data = dataSetDao.querySimpleRowCount("trans_account", data);
			if (data.getInt("state") == 1 && data.getInt("rowCount") == 0) {
				
			}*/
		}
		//删除竞买申请
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_license_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "deleteLicenseData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		ParaMap result = dataSetDao.executeSQL(sqlParams);
		if (result.getInt("state") == 1) {
			TransLicenseMineDao transLicenseDao = new TransLicenseMineDao();
			ParaMap cardtlMap=transLicenseDao.updateCartDtl(bidderId , targetId , 2, 1 , null);
			if(cardtlMap.getInt("state")==1){
				result.put("cart_dtl_id", cardtlMap.getString("cart_dtl_id"));
			}
		}
		return result;
	}
	
	public String getLicenseBidder(String id, String returnFieldName) throws Exception {
		if (StrUtils.isNull(id))
			return null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap data = new ParaMap();
		data.put("id", id);
		data = dataSetDao.querySimpleData("trans_license", data);
		if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
			String bidderId = data.getRecordString(0, "bidder_id");
			data.clear();
			data.put("id", bidderId);
			data = dataSetDao.querySimpleData("trans_license", data);
			if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
				if (StrUtils.isNotNull(returnFieldName)) {
					String fieldName = returnFieldName.toLowerCase();
					if (data.getFields().contains(fieldName))
						return data.getRecordString(0, fieldName);
					else
						return data.getRecordString(0, "id");
				} else
					return data.getRecordString(0, "id");
				
			} else
				return null;
		} else
			return null;
	}
	
	public String getLicenseUnionBidder(String id, String returnFieldName) throws Exception {
		if (StrUtils.isNull(id))
			return null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap data = new ParaMap();
		data.put("id", id);
		data = dataSetDao.querySimpleData("trans_license", data);
		if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
			data.clear();
			data.put("moduleNo", "trans_license_list");
			data.put("dataSetNo", "queryLicenseUnionBidderData1");
			data.put("id", id);
			data = dataSetDao.queryData(data);
			if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
				boolean blnFieldNameValid = false;
				if (StrUtils.isNotNull(returnFieldName)) {
					String fieldName = returnFieldName.toLowerCase();
					if (data.getFields().contains(fieldName))
						blnFieldNameValid = true;
				}
				String fieldName = null;
				if (blnFieldNameValid)
					fieldName = returnFieldName.toLowerCase();
				StringBuffer result = new StringBuffer();
				for(int i = 0; i < data.getSize(); i++) {
					if (blnFieldNameValid)
						result.append(data.getRecordString(0, fieldName) + ";");
					else
						result.append(data.getRecordString(0, "id") + ";");
				}
				if (result.length() > 0)
					result.deleteCharAt(result.length() - 1);
				return result.toString();
			} else
				return null;
		} else
			return null;
	}
	
	public String getFavoriteBidder(String targetId, String returnFieldName) throws Exception {
		if (StrUtils.isNull(targetId))
			return null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap data = new ParaMap();
		data.put("id", targetId);
		data = dataSetDao.querySimpleData("trans_target", data);
		if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
			data.clear();
			data.put("moduleNo", "trans_license_list");
			data.put("dataSetNo", "queryFavoritesBidderData");
			data.put("target_id", targetId);
			data = dataSetDao.queryData(data);
			if (data.getInt("state") == 1 && data.getInt("totalRowCount") > 0) {
				boolean blnFieldNameValid = false;
				if (StrUtils.isNotNull(returnFieldName)) {
					String fieldName = returnFieldName.toLowerCase();
					if (data.getFields().contains(fieldName))
						blnFieldNameValid = true;
				}
				String fieldName = null;
				if (blnFieldNameValid)
					fieldName = returnFieldName.toLowerCase();
				StringBuffer result = new StringBuffer();
				for(int i = 0; i < data.getSize(); i++) {
					if (blnFieldNameValid)
						result.append(data.getRecordString(0, fieldName) + ";");
					else
						result.append(data.getRecordString(0, "id") + ";");
				}
				if (result.length() > 0)
					result.deleteCharAt(result.length() - 1);
				return result.toString();
			} else
				return null;
		} else
			return null;
	}
	
	public ParaMap getLicenseData(String id, String unionBidderId) throws Exception {
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_license_list");
		sqlParams.put("dataSetNo", "queryLicenseData");
		sqlParams.put("id", id);
		sqlParams.put("union_bidder_id", unionBidderId);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap getLicenseData(String id) throws Exception {
		return getLicenseData(id, null);
	}
	
	 public ParaMap getSysUser(String userId)throws Exception{
    	    DataSetDao dao = new DataSetDao();
	    	ParaMap sqlParam = new ParaMap();
	    	sqlParam.put("id", userId);
	    	sqlParam.put("is_valid", 1);
	    	ParaMap result= dao.querySimpleData("sys_user", sqlParam);
	    	return result;
    }
	 
	 public ParaMap getConfirmLicenseListDataAfter(ParaMap sqlParams) throws Exception {
			sqlParams.put("moduleNo", "trans_license_confirm");
			sqlParams.put("dataSetNo", "queryConfirmLicenseListAfter");
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap result = dataSetDao.queryData(sqlParams);
			return result;
	}
	 
	/**
	 * 交易后资格审核
	 * @param confirmData
	 * @return
	 * @throws Exception
	 */
	public ParaMap confirmLicenseAfter(ParaMap confirmData) throws Exception {
		String userId = confirmData.getString("u");
		String id = confirmData.getString("id");
		String targetId = confirmData.getString("targetId");
		int confirmStatus = confirmData.getInt("confirmStatus");
		String confirmOpinion = confirmData.getString("confirmOpinion");
		ParaMap result = new ParaMap();
		if (StrUtils.isNull(targetId)) {
			result.put("state", 0);
			result.put("message", "标的无效");
			return result;
		}
		if (StrUtils.isNull(id)) {
			result.put("state", 0);
			result.put("message", "竞买申请无效");
			return result;
		}
		if (confirmStatus != 7 && confirmStatus != 5 && confirmStatus != 6) {
			result.put("state", 0);
			result.put("message", "竞买申请审批不合法");
			return result;
		}
		String abort_reson = "";
		int after_check = 0;
		if(confirmStatus == 7){
			abort_reson = "尚未审核";
			after_check = 0;
		}else if(confirmStatus == 5){
			abort_reson = "交易后资格审核通过";
			after_check = 1;
		}else if(confirmStatus == 6){
			abort_reson = this.getOkBidder(targetId)+"交易后资格审核不通过";
			after_check = -1;
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap paraData = new ParaMap();
		paraData.put("id", targetId);
		ParaMap target = dataSetDao.querySimpleData("trans_target", paraData);
		String oldStatus = target!=null && target.getSize()>0 ? target.getRecordString(0, "status") : "";
		paraData.clear();
		paraData.put("id", targetId);
		paraData.put("status", confirmStatus);
		paraData.put("abort_reson", abort_reson);
		paraData.put("after_check", after_check);
		if(confirmStatus==6){
			paraData.put("trans_price", "");
		}
		paraData.put("after_check_date", null);
		ParaMap format = new ParaMap();
		format.put("after_check_date", "sysdate");
		result = dataSetDao.updateData("trans_target", null, "id", paraData, format);
		if (result.getInt("state") == 1) {
			if(confirmStatus==6){
				paraData.clear();
				paraData.put("id", id);
				paraData.put("status", 2);
				result = dataSetDao.updateData("trans_license","id", paraData);
			}
			//保存到变更表中
			ParaMap change = new ParaMap();
			change.put("ref_table_name", "trans_target");
			change.put("ref_id", targetId);
			change.put("field_name", "status");
			change.put("old_value", oldStatus);
			change.put("new_value", confirmStatus);
			change.put("change_cause", confirmOpinion);
			change.put("change_user_id", userId);
			FieldChangeDao fcDao = new FieldChangeDao();
			fcDao.updateFieldChange(change);
		}
		return result;
	}
	
	public String getOkBidder(String targetId) throws Exception{
		//标的信息
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_trade");
		sqlParams.put("dataSetNo", "get_ok_bidder");
		sqlParams.put("targetId", targetId);
		ParaMap out = dataSetDao.queryData(sqlParams);
		if(out!=null && out.getSize()>0){
			return "竞价竞得人："+out.getRecordString(0, "succbiddername")+" " ;
		}else{
			return "竞价竞得人";
		}
	}

	
	public static void main(String[] args) throws Exception {
		LicenseManageDao licenseManageDao = new LicenseManageDao();
		ParaMap out = licenseManageDao.checkAllowApplyLicense("00230000000000000000000000000388", "t01");
		System.out.println(out);
	}
}