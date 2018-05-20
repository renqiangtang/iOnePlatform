package com.before.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.trade.utils.Constants;
import com.tradeland.engine.EngineDao;
import com.tradeland.service.TradeService;

/**
 * 土地管理DAO
 * 
 * @author chenl 2012-05-15
 */
public class TransGoodsLandDao extends BaseDao {

	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return 获取所有有效的交易物主信息
	 * @throws Exception
	 */
	public ParaMap getTransGoodsList(String sql, ParaMap sqlMap)
			throws Exception {
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_land_manage");
		sqlParams.put("dataSetNo", "get_trans_goods_list");

		// 如果是view 则查询没有交易数据的结果集
		// （不和trans_target_goods_rel关联，或者 关联的最后一个标的的状态是已流拍 或 已终止）
		String mode = sqlMap.getString("mode");
		if (StrUtils.isNotNull(mode) && "view".equals(mode)) {
			sqlParams.put("dataSetNo", "get_trans_goods_no_transaction_list");
		}
		if(StrUtils.isNotNull(mode) && "relView".equals(mode)){//标地信息处点击宗地信息
			sqlParams.put("dataSetNo", "get_trans_goods_transaction_list");
			sqlParams.put("targetId",sqlMap.getString("targetId"));//查询相对应标的的宗地
		}
		sqlParams.putAll(sqlMap);
		if (StrUtils.isNotNull(sql)) {
			ParaMap customConditions = new ParaMap();
			customConditions.put("CUSTOM_CONDITION", sql);
			sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		}
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

	/**
	 * 查询交易物所关联的标的(按关联关系的倒序排列)
	 * 
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 * 
	 */
	public ParaMap getGoodsTargetList(String goodsId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_land_manage");
		sqlParams.put("dataSetNo", "get_goods_target_list");
		sqlParams.put("goodsId", goodsId);
		return dataSetDao.queryData(sqlParams);
	}

	/**
	 * 获取所有有效的标的主信息
	 * 
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransTargetList(String moduleId, String dataSetNo,
			ParaMap sqlParams) throws Exception {
		sqlParams.put("moduleId", moduleId);
		if (StringUtils.isEmpty(moduleId)) {
			sqlParams.put("moduleNo", "trans_prepare_land_manage");
		}
		if (StringUtils.isEmpty(dataSetNo))
			sqlParams.put("dataSetNo", "query_trans_target_list");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return 获取所有有效的交易物主信息
	 * @throws Exception
	 */
	public ParaMap getTransTargetGoodsList(String targetId, String sql)
			throws Exception {
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_land_manage");
		sqlParams.put("dataSetNo", "query_trans_target_goods_list");
		sqlParams.put("targetId", targetId);
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", StrUtils.isNotNull(sql) ? sql
				: "");
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return 获取所有有效的交易物主信息
	 * @throws Exception
	 */
	public ParaMap getTransAllGoodsList(String organId, String targetId,
			String sql, ParaMap sqlMap) throws Exception {
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_land_manage");
		sqlParams.put("dataSetNo", "query_trans_all_other_goods_list");
		sqlParams.put("organId", organId);
		sqlParams.put("targetId", targetId);
		sqlParams.putAll(sqlMap);
		String conditionsSql = " ";
		conditionsSql = StrUtils.isNotNull(sql) ? conditionsSql + sql
				: conditionsSql;
		if (StrUtils.isNotNull(targetId)) {
			conditionsSql = conditionsSql
					+ " and not exists (select * from trans_target_goods_rel ttgr , trans_target tt "
					+ " where ttgr.target_id = tt.id and tt.status>0 and ttgr.is_valid = 1 and tt.is_valid = 1 and ttgr.goods_id = tg.id)  ";
		}

		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", conditionsSql);
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);

		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}

	/**
	 * @param moduleId
	 * @param dataSetNo
	 * @param sqlParams
	 * @return 获取标的信息
	 * @throws Exception
	 */
	public ParaMap getTargetInfo(String targetId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", targetId);
		keyData.put("is_valid", 1);
		ParaMap result = dataSetDao.querySimpleData("trans_target", keyData);
		return result;
	}

	/**
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 *             更新TransGoods
	 */
	public ParaMap updateTransGoods(ParaMap transGoodsMap) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("trans_goods", "id",
				transGoodsMap, null);
		return result;
	}

	/**
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 *             更新trans_target
	 */
	public ParaMap updateTransTarget(ParaMap transTargetsMap, ParaMap formatMap)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("trans_target", "id",
				transTargetsMap, formatMap);
		return result;
	}

	/**
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 *             更新trans_target_goods_rel
	 */

	public ParaMap updateTransTargetGoodsRel(ParaMap transTargetGoodsRelMap)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("trans_target_goods_rel", "id",
				"target_id,goods_id", transTargetGoodsRelMap, null);
		return result;
	}

	/**
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 *             更新sys_extend
	 */
	public ParaMap updateExtend(String ref_table_name, String ref_id,
			String field_no, String field_value, String remark)
			throws Exception {
		ParaMap result = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap extendMap = new ParaMap();
		extendMap.put("id", null);
		extendMap.put("ref_table_name", ref_table_name);
		extendMap.put("ref_id", ref_id);
		extendMap.put("field_no", field_no);
		extendMap.put("field_value", field_value);
		extendMap.put("remark", remark);
		result = dataSetDao.updateData("sys_extend", "id",
				"ref_table_name,ref_id,field_no", extendMap, null);
		return result;
	}

	/**
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 *             更新trans_target_multi_trade
	 */
	public ParaMap updateTransTargetMultiTrade(ParaMap transMap)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("trans_target_multi_trade",
				"id", transMap);
		return result;
	}

	/**
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 *             更新trans_target_earnest_money
	 */
	public ParaMap updateTransTargetEarnestMoney(ParaMap transMap)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.updateData("trans_target_earnest_money",
				"id", transMap);
		return result;
	}

	/*
	 * 得到即将创建的trans_target_goods_rel表的序号和是否是主交易物
	 */
	public int getNewTargetGoodsRelTurn(String targetId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParam = new ParaMap();
		sqlParam.put("target_id", targetId);
		sqlParam.put("is_valid", 1);
		ParaMap result = dataSetDao.querySimpleData("trans_target_goods_rel",
				sqlParam, " turn desc ", null);
		int turn = 1;
		if (result.getSize() > 0) {
			int goods_trun = (int) Math.ceil(Double.parseDouble(String
					.valueOf(result.getRecordValue(0, "turn"))));
			turn = goods_trun + 1;
		}
		return turn;
	}

	/*
	 * 根据交易物id修改标的名称
	 */
	public String updateTargetNoNameByGoodsId(String goodsId) throws Exception {
		String targetId = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_land_manage");
		sqlParams.put("dataSetNo", "query_goods_rel_goods_List");
		sqlParams.put("goodsId", goodsId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getSize() > 0) {
			String no = null;
			String name = null;
			for (int i = 0; i < result.getSize(); i++) {
				targetId = (String) result.getRecordValue(i, "target_id");
				String tempNo = (String) result.getRecordValue(i, "no");
				String tempName = (String) result.getRecordValue(i, "name");
				if (StrUtils.isNotNull(tempNo)) {
					no = StrUtils.isNull(no) ? tempNo : no + "," + tempNo;
				}
				if (StrUtils.isNotNull(tempName)) {
					name = StrUtils.isNull(name) ? tempName : name + ","
							+ tempName;
				}
			}
			if (StrUtils.isNotNull(targetId)) {
				sqlParams.clear();
				sqlParams.put("id", targetId);
				sqlParams.put("no", no);
				sqlParams.put("name", name);
				dataSetDao.updateData("trans_target", "id", sqlParams);
				;
			}
		}
		return targetId;
	}

	/*
	 * 根据标的id修改标的名称
	 */
	public void updateTargetNoNameByTargetId(String targetId) throws Exception {
		ParaMap sqlParams = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = getTransTargetGoodsList(targetId, null);
		if (result.getSize() > 0) {
			String no = null;
			String name = null;
			for (int i = 0; i < result.getSize(); i++) {
				String tempNo = (String) result.getRecordValue(i, "no");
				String tempName = (String) result.getRecordValue(i, "name");
				if (StrUtils.isNotNull(tempNo)) {
					no = StrUtils.isNull(no) ? tempNo : no + "," + tempNo;
				}
				if (StrUtils.isNotNull(tempName)) {
					name = StrUtils.isNull(name) ? tempName : name + ","
							+ tempName;
				}
			}
			if (StrUtils.isNotNull(targetId)) {
				sqlParams.clear();
				sqlParams.put("id", targetId);
				sqlParams.put("no", no);
				sqlParams.put("name", name);
				dataSetDao.updateData("trans_target", "id", sqlParams);
				;
			}
		}
	}

	/*
	 * 删除标的多指标信息
	 */
	public void deleteTrandTargetMultiTrade(String targetId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("target_id", targetId);
		dataSetDao.deleteSimpleData("trans_target_multi_trade", sqlParams);
	}

	/*
	 * 删除多币种
	 */
	public void deleteTargetEarnestMoney(String targetId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("target_id", targetId);
		dataSetDao.deleteSimpleData("trans_target_earnest_money", sqlParams);
	}

	/*
	 * 本单位交易方式列表
	 */
	public ParaMap getOrganCreateUserList(String organId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_land_manage");
		sqlParams.put("dataSetNo", "query_organ_trans_goods_user");
		sqlParams.put("organId", organId);
		return dataSetDao.queryData(sqlParams);
	}

	/*
	 * 本单位行政区
	 */
	public ParaMap getOrganCantonList(String organId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_land_manage");
		sqlParams.put("dataSetNo", "query_organ_trans_goods_canton");
		sqlParams.put("organId", organId);
		return dataSetDao.queryData(sqlParams);
	}

	/*
	 * 本单位交易类型列表
	 */
	public ParaMap getOrganBusinessList(String organId, String goodsType)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_land_manage");
		sqlParams.put("dataSetNo", "getOrganBusinessTypeList");
		sqlParams.put("organId", organId);
		sqlParams.put("goodsType", goodsType);
		return dataSetDao.queryData(sqlParams);
	}

	/*
	 * 本单位交易方式列表
	 */
	public ParaMap getOrganTransTypeList(String organId, String goodsType)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_land_manage");
		sqlParams.put("dataSetNo", "getOrganTransTypeList");
		sqlParams.put("organId", organId);
		ParaMap customConditions = new ParaMap();
		String sql = "";
		if (StrUtils.isNotNull(goodsType)) {
			sqlParams.put("goodsType", goodsType);
			sql = sql + " and tbtr.goods_type_id  = :goodsType ";
		}
		customConditions.put("CUSTOM_CONDITION", StrUtils.isNotNull(sql) ? sql
				: "");
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return dataSetDao.queryData(sqlParams);
	}

	/*
	 * 本单位交易物类型列表
	 */
	public ParaMap getOrganGoodsTypeList(String organId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_land_manage");
		sqlParams.put("dataSetNo", "getOrganGoodsTypeList");
		sqlParams.put("organId", organId);
		return dataSetDao.queryData(sqlParams);
	}

	/*
	 * 发布单位
	 */
	public ParaMap getAllTransOrgan() throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_land_manage");
		sqlParams.put("dataSetNo", "query_all_trans_organ");
		return dataSetDao.queryData(sqlParams);
	}

	/**
	 * 查询标的交易物关系
	 * 
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 * 
	 */
	public ParaMap queryTransTargetGoodsRel(String targetId, String goodsId)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		if (StrUtils.isNotNull(targetId)) {
			keyData.put("target_id", targetId);
		}
		if (StrUtils.isNotNull(goodsId)) {
			keyData.put("goods_id", goodsId);
		}
		keyData.put("is_valid", 1);
		ParaMap result = dataSetDao.querySimpleData("trans_target_goods_rel",
				keyData);
		return result;
	}

	public ParaMap deleteTransTargetGoodsRel(String targetId, String goodsId)
			throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("target_id", targetId);
		keyData.put("goods_id", goodsId);
		ParaMap result = dataSetDao.deleteSimpleData("trans_target_goods_rel",
				keyData);
		return result;
	}

	/**
	 * 删除标的
	 * 
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public ParaMap deleteTransTarget(String targetId) throws Exception {
		ParaMap out = new ParaMap();

		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();

		// 删除多指标
		keyData.put("target_id", targetId);
		ParaMap result = dataSetDao.deleteSimpleData(
				"trans_target_multi_trade", keyData);
		if (!"1".equals(result.getString("state"))) {
			out.put("state", 0);
			out.put("message", "删除标的交易指标信息出错!");
			return out;
		}

		// 删除保证金
		keyData.clear();
		keyData.put("target_id", targetId);
		result = dataSetDao.deleteSimpleData("trans_target_earnest_money",
				keyData);
		if (!"1".equals(result.getString("state"))) {
			out.put("state", 0);
			out.put("message", "删除标的保证金信息出错!");
			return out;
		}

		// 删除所有的关联关系
		keyData.clear();
		keyData.put("target_id", targetId);
		result = dataSetDao.deleteSimpleData("trans_target_goods_rel", keyData);
		if (!"1".equals(result.getString("state"))) {
			out.put("state", 0);
			out.put("message", "删除标的交易物信息出错!");
			return out;
		}

		// 删除主信息
		keyData.clear();
		keyData.put("id", targetId);
		result = dataSetDao.deleteSimpleData("trans_target", keyData);
		if (!"1".equals(result.getString("state"))) {
			out.put("state", 0);
			out.put("message", "删除标的信息出错!");
			return out;
		}

		out.put("state", 1);
		out.put("message", "删除标的成功");
		return out;
	}

	/**
	 * 删除交易物信息
	 * 
	 * @param goodsId
	 * @return
	 * @throws Exception
	 */
	public ParaMap deleteTransGoods(String goodsId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();

		// 删除扩展信息
		keyData.put("ref_table_name", "trans_goods");
		keyData.put("ref_id", goodsId);
		ParaMap result = dataSetDao.deleteSimpleData("sys_extend", keyData);
		if (!"1".equals(result.getString("state"))) {
			result.put("state", 0);
			result.put("message", "删除交易物扩展信息失败!");
			return result;
		}

		// 删除所有的关联关系
		keyData.clear();
		keyData.put("goods_id", goodsId);
		result = dataSetDao.deleteSimpleData("trans_target_goods_rel", keyData);
		if (!"1".equals(result.getString("state"))) {
			result.put("state", 0);
			result.put("message", "删除交易物标的关系失败!");
			return result;
		}

		// 删除主信息
		keyData.clear();
		keyData.put("id", goodsId);
		result = dataSetDao.deleteSimpleData("trans_goods", keyData);
		if (!"1".equals(result.getString("state"))) {
			result.put("state", 0);
			result.put("message", "删除交易物标信息失败!");
			return result;
		}
		result.put("state", 1);
		result.put("message", "删除交易物标信息成功!");
		return result;
	}

	public ParaMap getTargetTransOrgan(String targetId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_land_manage");
		sqlParams.put("dataSetNo", "get_target_trans_organ");
		sqlParams.put("targetId", targetId);
		return dataSetDao.queryData(sqlParams);
	}

	public ParaMap getTargetTypeInfo(String targetId) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_prepare_land_manage");
		sqlParams.put("dataSetNo", "get_target_type_info");
		sqlParams.put("targetId", targetId);
		return dataSetDao.queryData(sqlParams);
	}

	public ParaMap updateProcessInfo(String targetId, String step)
			throws Exception {
		ParaMap out = new ParaMap();
		// 得到当前上下文信息
		EngineDao engineDao = new EngineDao();
		String processId = engineDao.getProcessIdByTargetId(targetId);
		ParaMap tasknodeMap = engineDao.curTaskNodeInfo(processId);
		String nodeId = tasknodeMap.getString("nodeid");
		String tasknodeId = tasknodeMap.getString("tasknodeid");
		String ptype = tasknodeMap.getString("ptype");
		String qtype = tasknodeMap.getString("qtype");
		if (ptype.equals(Constants.LNotice)) {// 公告期
			// 修改tasknode信息
			out = updateProcessParameter(tasknodeId, Constants.Pstep, step);
			if (!"1".equals(out.getString("state"))) {
				return out;
			}
			// 修改node信息
			out = updateProcessParameter(nodeId, Constants.Pstep, step);
			if (!"1".equals(out.getString("state"))) {
				return out;
			}
			ParaMap nodeMap = engineDao.getNodeInfo(nodeId);
			ParaMap nextMap = engineDao
					.getNodeInfo(nodeMap.getString("nextid")); // 得到下一个节点
			if (nextMap.getString("ptype").equals(Constants.LFocus)) { // 下一个节点是集中报价
				out = updateProcessParameter(nextMap.getString("id"),
						Constants.Pstep, step);
				if (!"1".equals(out.getString("state"))) {
					return out;
				}
				if (StrUtils.isNotNull(nextMap.getString("nextid"))) {
					ParaMap nextNextMap = engineDao.getNodeInfo(nextMap
							.getString("nextid"));// 下一结点是限时报价
					if (nextNextMap.getString("ptype").equals(Constants.LLimit)
							&& nextNextMap.getString("qtype").equals(
									Constants.QPrice)) {
						out = updateProcessParameter(
								nextNextMap.getString("id"), Constants.Pstep,
								step);
						if (!"1".equals(out.getString("state"))) {
							return out;
						}
					}
				}
			} else if (nextMap.getString("ptype").equals(Constants.LLimit)
					&& nextMap.getString("qtype").equals(Constants.QPrice)) {
				out = updateProcessParameter(nextMap.getString("id"),
						Constants.Pstep, step);
				if (!"1".equals(out.getString("state"))) {
					return out;
				}
			}
		} else if (ptype.equals(Constants.LFocus)) {// 集中报价期
			// 修改tasknode信息
			out = updateProcessParameter(tasknodeId, Constants.Pstep, step);
			if (!"1".equals(out.getString("state"))) {
				return out;
			}
			// 修改node信息
			out = updateProcessParameter(nodeId, Constants.Pstep, step);
			if (!"1".equals(out.getString("state"))) {
				return out;
			}
			ParaMap nodeMap = engineDao.getNodeInfo(nodeId);
			ParaMap nextMap = engineDao
					.getNodeInfo(nodeMap.getString("nextid")); // 得到下一个节点
			if (nextMap.getString("ptype").equals(Constants.LLimit)
					&& nextMap.getString("qtype").equals(Constants.QPrice)) {
				out = updateProcessParameter(nextMap.getString("id"),
						Constants.Pstep, step);
				if (!"1".equals(out.getString("state"))) {
					return out;
				}
			}
		} else if (ptype.equals(Constants.LLimit)) {// 限时竞价
			if (qtype.equals(Constants.QPrice)) {
				// 修改tasknode信息
				out = updateProcessParameter(tasknodeId, Constants.Pstep, step);
				if (!"1".equals(out.getString("state"))) {
					return out;
				}
				// 修改node信息
				out = updateProcessParameter(nodeId, Constants.Pstep, step);
				if (!"1".equals(out.getString("state"))) {
					return out;
				}
			}
		}
		TradeService.invalidateTargetInfoMap();
		out.put("state", 1);
		return out;
	}

	public ParaMap updateProcessParameter(String id, String name, String value)
			throws Exception {
		ParaMap out = new ParaMap();
		DataSetDao dao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", null);
		sqlParams.put("refid", id);
		sqlParams.put("name", name);
		sqlParams.put("value", value);
		out = dao.updateData("wf_parameter", "id", "refid,name", sqlParams,
				null);
		return out;
	}

	DataSetDao dao = new DataSetDao();

	public ParaMap getGoodsInfoByTargetId(String targetId) throws Exception {
		ParaMap out = new ParaMap();
		ParaMap sqlParams = new ParaMap();
		List goodsList = new ArrayList();
		String sql = "select t.goods_id as goodsid from TRANS_TARGET_GOODS_REL t where t.target_id=:targetId";
		sqlParams.put("targetId", targetId);
		ParaMap out1 = dao.queryData(sql, sqlParams);
		for (int i = 0; i < out1.getSize(); i++) {
			String goodsId = out1.getRecordString(i, 0);
			ParaMap out2 = getGoodsInfoByGoodsId(goodsId);
			goodsList.add(out2);
		}
		out.put("goodsList", goodsList);
		return out;
	}

	public ParaMap getGoodsInfoByGoodsId(String goodsId) throws Exception {
		ParaMap out = new ParaMap();
		String sql = "select * from TRANS_GOODS t where t.id=:goodsId";
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("goodsId", goodsId);
		ParaMap out2 = dao.queryData(sql, sqlParams);
		List filds = out2.getFields();
		for (int i = 0; i < filds.size(); i++) {
			out.put(filds.get(i), out2.getRecordValue(0, i));
		}
		//
		sql = "select t.field_no no,t.field_value value from sys_extend t where t.ref_table_name='trans_goods' and t.ref_id=:goodsId";
		ParaMap out3 = dao.queryData(sql, sqlParams);
		filds = out3.getFields();
		for (int i = 0; i < out3.getSize(); i++) {
			String no = out3.getRecordString(i, "no");
			String value = out3.getRecordString(i, "value");
			out.put(no, value);
		}
		return out;
	}

}
