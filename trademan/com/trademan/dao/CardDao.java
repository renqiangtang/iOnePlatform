package com.trademan.dao;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;

/**
 * 牌号DAO
 * @author SAMONHUA
 * 2012-05-21
 */
public class CardDao extends BaseDao {
	public ParaMap getCardListData(String moduleId, String dataSetNo, ParaMap sqlParams) throws Exception {
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "trans_card_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryCardListData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public ParaMap getCardData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "trans_card_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryCardData");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		sqlParams.put("id", id);
		return dataSetDao.queryData(sqlParams);
	}
	
	public ParaMap updateCardData(ParaMap cardData) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result;
		int state;
		//检查牌号
		ParaMap checkCardData = new ParaMap();
		String name = cardData.getString("name");
		if (name != null && !name.equals("") && !name.equalsIgnoreCase("null")) {
			checkCardData.clear();
			checkCardData.put("id", cardData.getString("id"));
			checkCardData.put("name", name);
			result = checkCardExists(null, null, checkCardData);
			state = result.getInt("state");
			if (state == 1) {
				if (Math.ceil(Double.parseDouble(String.valueOf(result.getRecordValue(0, "row_count")))) > 0) {
					result.clear();
					result.put("state", 0);
					result.put("message", "银行名称" + cardData.getString("name") + "已存在，不能重复");
					return result;
				}
			} else
				return result;
		}
		//保存牌号
		cardData.put("create_user_id", cardData.getString("u"));
		ParaMap format = new ParaMap();
		format.put("create_date", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		result = dataSetDao.updateData("trans_card", "id", cardData, format);
		return result;
	}
	
	public ParaMap deleteCardData(String moduleId, String dataSetNo, String id) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", id);
		return dataSetDao.deleteSimpleData("trans_card", keyData);
	}
	
	public ParaMap checkCardExists(String moduleId, String dataSetNo, ParaMap cardData) throws Exception{
		ParaMap sqlParams = new ParaMap();
		if (moduleId == null || moduleId.equals("") || moduleId.equalsIgnoreCase("null"))
			sqlParams.put("moduleNo", "trans_card_list");
		else
			sqlParams.put("moduleId", moduleId);
		if (dataSetNo == null || dataSetNo.equals("") || dataSetNo.equalsIgnoreCase("null"))
			sqlParams.put("dataSetNo", "queryCardExists");
		else
			sqlParams.put("dataSetNo", dataSetNo);
		String id = cardData.getString("id");
		StringBuffer customCondition = new StringBuffer("");
		if (id != null && !id.equals("") && !id.equalsIgnoreCase("null")) {
			sqlParams.put("id", id);
			customCondition.append(" and id <> :id");
		}
		customCondition.append(" and (1 = 2");
		if (cardData.containsKey("no")) {
			String no = cardData.getString("no");
			if (no != null && !no.equals("") && !no.equalsIgnoreCase("null")) {
				sqlParams.put("no", no);
				customCondition.append(" or no = :no");
			}
		}
		customCondition.append(")");
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
}
