package com.before.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.service.BaseDownloadService;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.sysman.dao.AttachDao;
import com.sysman.dao.PlowExtendDao;
import com.tradeplow.engine.EngineDao;

/**
 * 指标公告Dao
 * 
 * @author SAMONHUA 2013-01-15
 */
public class PlowNoticeDao extends BaseDao {
	public ParaMap getNoticeListData(String moduleId, String dataSetNo,
			ParaMap sqlParams) throws Exception {
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_notice");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryNoticeListData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

	public ParaMap getNoticeData(String moduleId, String dataSetNo, String id)
			throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_notice");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryNoticeData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}

	public ParaMap getNoticeTrustData(String moduleId, String dataSetNo,
			String id) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_notice");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryNoticeTrustData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}

	public ParaMap getNoticeTargetData(String moduleId, String dataSetNo,
			String id) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_notice");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryNoticeTargetData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}

	public ParaMap getNoticeTrustTargetData(String moduleId, String dataSetNo,
			String id, String trustId, String targetId) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_notice");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryNoticeTrustTargetData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		sqlParams.put("trust_id", trustId);
		if (StrUtils.isNull(targetId))
			sqlParams.put("target_id", null);
		else
			sqlParams.put("target_id", targetId);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}

	public ParaMap getNoticeTrustTargetData(String moduleId, String dataSetNo,
			String id, String trustId) throws Exception {
		return getNoticeTrustTargetData(moduleId, dataSetNo, id, trustId, null);
	}

	public ParaMap updateNoticeData(ParaMap noticeData) throws Exception {
		ParaMap result = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap data = new ParaMap();
		String businessType = noticeData.containsKey("business_type") ? noticeData
				.getString("business_type") : "501005";
		String userId = noticeData.getString("u");
		// 新增/更新公告
		data.clear();
		String id = noticeData.getString("id");
		data.put("id", id);
		data.put("business_type", businessType);
		data.put("notice_type", noticeData.getInt("notice_type"));
		data.put("status", 1);
		data.put("no", noticeData.getString("no"));
		data.put("name", noticeData.getString("name"));
		data.put("notice_date", noticeData.getString("notice_date"));
		if (StrUtils.isNull(id)) {
			data.put("create_user_id", userId);
		}
		ParaMap format = new ParaMap();
		format.put("notice_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		result = dataSetDao.updateData("trans_notice", "id", data, format);
		if (result.getInt("state") == 1) {
			if (StrUtils.isNull(id))
				id = result.getString("id");
		} else
			throw new Exception("保存公告数据失败，操作取消。");
		PlowExtendDao extendDao = new PlowExtendDao();
		data.clear();
		data.put("end_apply_time", noticeData.getString("end_apply_time"));
		data.put("trans_type", noticeData.getString("trans_type"));
		data.put("begin_focus_time", noticeData.getString("begin_focus_time"));
		data.put("end_focus_time", noticeData.getString("end_focus_time"));
		data.put("begin_limit_time", noticeData.getString("begin_limit_time"));
		data.put("first_wait_time", noticeData.getString("first_wait_time"));
		data.put("limit_wait_time", noticeData.getString("limit_wait_time"));
		data.put("random_wait_time", noticeData.getString("random_wait_time"));
		data.put("is_online", noticeData.getString("is_online"));

		result = extendDao.updateExtend("trans_notice", id, data);
		if (result.getInt("state") != 1)
			throw new Exception("保存公告交易时间失败，操作取消。");
		// 保存公告与委托关联
		String deleteTrustIds = noticeData.getString("deleteTrustIds");
		if (StrUtils.isNotNull(deleteTrustIds)) {
			data.clear();
			data.put("moduleNo", "trans_notice");
			data.put("dataSetNo", "deleteNoticeTrustTargetData");
			data.put("id", id);
			data.put("trust_id", deleteTrustIds);
			result = dataSetDao.executeSQL(data);
			if (result.getInt("state") != 1)
				throw new Exception("保存公告删除的委托数据失败，操作取消。");
		}
		String deleteTargetIds = noticeData.getString("deleteTargetIds");
		if (StrUtils.isNotNull(deleteTargetIds)) {
			List deleteTargetIdList = StrUtils.getSubStrs(deleteTargetIds, ";");
			if (deleteTargetIdList.size() > 0) {
				data.clear();
				data.put("notice_id", id);
				data.put("target_id", deleteTargetIdList);
				result = dataSetDao.deleteSimpleData("trans_notice_target_rel",
						data);
				if (result.getInt("state") != 1)
					throw new Exception("保存公告删除的标的数据失败，操作取消。");
			}
		}
		String addTrustIds = noticeData.getString("addTrustIds");
		if (StrUtils.isNotNull(addTrustIds)) {
			List<String> trustIdList = StrUtils.getSubStrs(addTrustIds, ";");
			if (trustIdList.size() > 0) {
				data.clear();
				data.put("moduleNo", "trans_notice");
				data.put("dataSetNo", "updateTrustNotNoticeTargetData");
				data.put("id", id);
				for (int i = 0; i < trustIdList.size(); i++) {
					data.put("trust_id", trustIdList.get(i));
					result = dataSetDao.executeSQL(data);
					if (result.getInt("state") != 1)
						throw new Exception("添加委托标的到公告失败，操作取消。");
				}

			}
		}
		result.clear();
		result.put("state", 1);
		result.put("id", id);
		return result;
	}

	public ParaMap deleteNoticeData(String moduleId, String dataSetNo, String id)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_notice");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "deleteNoticeData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		return dataSetDao.executeSQL(sqlParams);
	}

	/**
	 * 生成新的公告号，按businessType每天流水号(日期+四位流水号)，不支持并发。如“201211200001”
	 * 
	 * @param moduleId
	 * @param dataSetNo
	 * @param businessType
	 *            业务类别
	 * @return
	 * @throws Exception
	 */
	public String getNewNoticeNo(String moduleId, String dataSetNo,
			int businessType, int noticeType) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String noPrefix = sdf.format(DateUtils.now());
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_notice");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryMaxNoticeNo");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("business_type", businessType);
		sqlParams.put("notice_type", noticeType);
		sqlParams.put("no_prefix", noPrefix);
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getInt("state") == 1) {
			if (result.getInt("totalRowCount") == 0) {
				return noPrefix + "0001";
			} else {
				String maxNo = result.getRecordString(0, 0);
				if (StrUtils.isNull(maxNo))
					return noPrefix + "0001";
				else {
					int intMaxNo = StrUtils.strToInt(
							maxNo.substring(noPrefix.length()), 0);
					return noPrefix
							+ StrUtils.getRightString("0000" + (intMaxNo + 1),
									4);
				}
			}
		} else
			return null;
	}

	/**
	 * 获取指定委托中未公告的标的数据
	 * 
	 * @param moduleId
	 * @param dataSetNo
	 * @param trustId
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTrustNotNoticeTargetData(String moduleId,
			String dataSetNo, String trustId) throws Exception {
		ParaMap sqlParams = new ParaMap();
		if (StrUtils.isNull(moduleId))
			sqlParams.put("moduleNo", "trans_notice");
		else
			sqlParams.put("moduleId", moduleId);
		if (StrUtils.isNull(dataSetNo))
			sqlParams.put("dataSetNo", "queryTrustNotNoticeTargetData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("trust_id", trustId);
		DataSetDao dataSetDao = new DataSetDao();
		return dataSetDao.queryData(sqlParams);
	}

	/**
	 * 公告发布。需要检测所有标的的公告期是否足够，即发布日期到拍卖时间必须大于或者等于公告期
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ParaMap publishNoticeData(String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = null;
		ParaMap data = new ParaMap();
		// data.put("moduleNo", "trans_notice");
		// data.put("dataSetNo", "publishNotice");
		// data.put("id", id);
		// result = dataSetDao.executeSQL(data);
		List procParams = new ArrayList();
		procParams.add(id);
		result = dataSetDao.callStoredProc("publish_notice_plow", procParams);
		if (result.getInt("state") == 1) {
			// 发布公告文件，转换PDF为SWF文件
			String businessType = null;
			data.clear();
			data.put("id", id);
			data = dataSetDao.querySimpleData("trans_notice", data);
			if (data.getInt("state") == 1 && data.getRecordCount() > 0)
				businessType = data.getRecordString(0, "business_type");
			else
				throw new Exception("查询当前公告数据失败，操作取消。");
			AttachDao attachDao = new AttachDao();
			List ownerList = new ArrayList();
			ParaMap owner = new ParaMap();
			owner.put("refId", id);
			owner.put("refTableName", "trans_notice");
			owner.put("templetNo", "businessType" + businessType
					+ "NoticeAttach");
			owner.put("file_no", "gg");
			owner.put("fileNo", "gg");
			owner.put("category", "");
			ownerList.add(owner);
			ParaMap lobData = attachDao.getAttachLobData(null, null, ownerList);
			if (lobData.getInt("state") == 1 && lobData.getRecordCount() > 0) {
				String lobId = lobData.getRecordString(0, "id");
				String fileName = lobData.getRecordString(0, "file_name");
				String fileExt = fileName
						.substring(fileName.lastIndexOf(".") + 1);
				if (!fileExt.equalsIgnoreCase("pdf")) {
					throw new Exception("公告文件只能上传PDF文件且只能上传一个文件，请修改后重新发布，操作取消。");
				}
				BaseDownloadService baseDownloadService = new BaseDownloadService();
				String filePath = baseDownloadService.getFilePath(lobId);
				baseDownloadService.apartNotice(lobId, id, filePath);
			} else {
				throw new Exception("当前公告无公告文件（PDF），操作取消。");
			}
			// 写入交易引擎信息(此调用中有事务提交，不能放到前面调用)
			data.clear();
			data.put("moduleNo", "trans_notice");
			data.put("dataSetNo", "queryNoticeTargetData");
			data.put("id", id);
			data = dataSetDao.queryData(data);
			if (data.getInt("state") == 1) {
				if (data.getRecordCount() == 0)
					throw new Exception("当前公告下无标的数据，发布公告失败，操作取消。");
				else {
					for (int i = 0; i < data.getRecordCount(); i++) {
						EngineDao dao = new EngineDao();
						dao.createTemplateInfo(data.getRecordString(i, "id"));
					}
				}
			}
		} else
			throw new Exception("发布公告失败，操作取消。");
		return result;
	}

	/**
	 * 查询公告是否已有竞买申请
	 * 
	 * @param noticeId
	 * @return
	 * @throws Exception
	 */
	public ParaMap checkCancleNotice(String noticeId) throws Exception {
		ParaMap sqlParam = new ParaMap();
		sqlParam.put("moduleNo", "trans_notice");
		sqlParam.put("dataSetNo", "checkNoticeLicenseCount");
		sqlParam.put("notice_id", noticeId);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap out = dataSetDao.queryData(sqlParam);
		return out;
	}
	/**
	 * 撤销公告
	 * @param noticeId
	 * @return
	 * @throws Exception
	 */
	public ParaMap cancleNotice(String noticeId) throws Exception{
		ParaMap sqlParam = new ParaMap();
		sqlParam.put("moduleNo", "trans_notice");
		sqlParam.put("dataSetNo", "cancleNotice");
		sqlParam.put("notice_id", noticeId);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap out = dataSetDao.executeSQL(sqlParam);
		return out;
		
	}
}