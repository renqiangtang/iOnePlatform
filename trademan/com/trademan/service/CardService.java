package com.trademan.service;

import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.trademan.dao.CardDao;

/**
 * 牌号service
 * @author SAMONHUA
 * 2012-05-21
 */
public class CardService extends BaseService {
	public ParaMap getCardListData(ParaMap inMap) throws Exception {
		//组织查询条件及分页信息
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (sortField != null && !sortField.equals("") && !sortField.equalsIgnoreCase("null")
				&& sortDir != null && !sortDir.equals("") && !sortDir.equalsIgnoreCase("null"))
			sortField = sortField + " " + sortDir;
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		ParaMap sqlParams = inMap;//new ParaMap();
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pageRowCount"));
		if (sortField == null || sortField.equals("") || sortField.equalsIgnoreCase("null"))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		//自定义查询条件
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("cardName")) {
			String cardName = inMap.getString("cardName");
			if (cardName != null && !cardName.equals("") && !cardName.equalsIgnoreCase("null")) {
				sqlParams.put("name", cardName);
				customCondition.append(" and name like '%' || :name || '%'");
			}
		}
		if (inMap.containsKey("cardNo")) {
			String cardNo = inMap.getString("cardNo");
			if (cardNo != null && !cardNo.equals("") && !cardNo.equalsIgnoreCase("null")) {
				sqlParams.put("no", cardNo);
				customCondition.append(" and no like '%' || :no || '%'");
			}
		}
		if (inMap.containsKey("cardTel")) {
			String cardTel = inMap.getString("cardTel");
			if (cardTel != null && !cardTel.equals("") && !cardTel.equalsIgnoreCase("null")) {
				sqlParams.put("tel", cardTel);
				customCondition.append(" and tel like '%' || :tel || '%'");
			}
		}
		if (inMap.containsKey("cardAddress")) {
			String cardAddress = inMap.getString("cardAddress");
			if (cardAddress != null && !cardAddress.equals("") && !cardAddress.equalsIgnoreCase("null")) {
				sqlParams.put("address", cardAddress);
				customCondition.append(" and address like '%' || :address || '%'");
			}
		}
		if (inMap.containsKey("cardHasCakey")) {
			String cardHasCakey = inMap.getString("cardHasCakey");
			if (cardHasCakey.equals("1"))
				customCondition.append(" and exists(select * from sys_user where user_type = 2 and ref_id = trans_card.id" +
						"  and exists(select * from sys_cakey where user_id = sys_user.id and key is not null))");
			else if (cardHasCakey.equals("0"))
				customCondition.append(" and (not exists(select * from sys_user where user_type = 2 and ref_id = trans_card.id) " +
						"or exists(select * from sys_user where user_type = 2 and ref_id = trans_card.id" +
						"  and (not exists(select * from sys_cakey where user_id = sys_user.id)" +
						"    or exists(select * from sys_cakey where user_id = sys_user.id and key is null))))");
		}
		if (inMap.containsKey("cardCakey")) {
			String cardCakey = inMap.getString("cardCakey");
			if (cardCakey != null && !cardCakey.equals("") && !cardCakey.equalsIgnoreCase("null")) {
				sqlParams.put("cakey", cardCakey);
				customCondition.append(" and exists(select * from sys_user where user_type = 2 and ref_id = trans_card.id" +
						"  and exists(select * from sys_cakey where user_id = sys_user.id and key = :cakey))");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		//查询数据
		CardDao cardDao = new CardDao();
		ParaMap out = cardDao.getCardListData(moduleId, dataSetNo, sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		//转换格式
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("id", "id");
		fields.put("name", "name");
		fields.put("no", "no");
		fields.put("tel", "tel");
		fields.put("contact", "contact");
		fields.put("address", "address");
		fields.put("cantonName", "canton_name");
		fields.put("createDate", "create_date");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	
	public ParaMap getCardData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		CardDao cardDao = new CardDao();
		ParaMap out = cardDao.getCardData(moduleId, dataSetNo, id);
		return out;
	}
	
	public ParaMap updateCardData(ParaMap cardData) throws Exception {
		CardDao cardDao = new CardDao();
		ParaMap out = cardDao.updateCardData(cardData);
		return out;
	}

	public ParaMap deleteCardData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		CardDao cardDao = new CardDao();
		ParaMap out = cardDao.deleteCardData(moduleId, dataSetNo, id);
		return out;
	}
}
