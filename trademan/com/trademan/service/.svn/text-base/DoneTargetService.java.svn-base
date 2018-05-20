package com.trademan.service;

import java.util.ArrayList;
import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.trademan.dao.DoneTargetDao;

/**
 * 成交公示service
 * @author SAMONHUA
 * 2012-09-28
 */
public class DoneTargetService extends BaseService {
	public ParaMap getDoneTargetListData(ParaMap inMap) throws Exception {
		ParaMap result = null;
		System.out.println(inMap.getString("targetNo"));
		String sortField = inMap.getString("sortField");
		String sortDir = inMap.getString("sortDir");
		if (StrUtils.isNotNull(sortField) && StrUtils.isNotNull(sortDir))
			sortField = sortField + " " + sortDir;
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		ParaMap sqlParams = new ParaMap();
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("pageIndex"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pageRowCount"));
		if (StrUtils.isNull(sortField))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, "create_date desc, target_id");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		StringBuffer customCondition = new StringBuffer("");
		boolean blnHasBusinessType = false;
		if (inMap.containsKey("businessType")) {
			String businessType = inMap.getString("businessType");
			if (StrUtils.isNotNull(businessType)) {
				sqlParams.put("business_type", businessType);
				customCondition.append(" and t.business_type = :business_type");
				blnHasBusinessType = true;
			}
		}
		if (inMap.containsKey("allowGoodsTypes") || inMap.containsKey("goodsUse")) {
			boolean blnHasGoodsTypes = inMap.containsKey("allowGoodsTypes");
			String allowGoodsTypes = inMap.getString("allowGoodsTypes");
			String goodsUse = inMap.getString("goodsUse");
			if ((!blnHasBusinessType && blnHasGoodsTypes) || StrUtils.isNotNull(goodsUse)) {
				customCondition.append(" and exists(select * from trans_target_goods_rel where target_id = t.id" +
						" and exists(select * from trans_goods where id = trans_target_goods_rel.goods_id ");
				if (blnHasGoodsTypes) {
					List<String> allowGoodsTypeList = StrUtils.getSubStrs(allowGoodsTypes, ";");
					if (allowGoodsTypeList == null || allowGoodsTypeList.size() == 0)
						customCondition.append(" and 1 = 2");
					else if (allowGoodsTypeList.indexOf("000") == -1) {//000为所有交易物类别
						customCondition.append(" and goods_type in (");
						for(int i = 0; i < allowGoodsTypeList.size(); i++) {
							String goodsType = allowGoodsTypeList.get(i);
							sqlParams.put("goods_type_" + i, goodsType);
							customCondition.append(" :goods_type_" + i + ",");
						}
						customCondition.deleteCharAt(customCondition.length() - 1);
						customCondition.append(")");
					}
				}
				if (StrUtils.isNotNull(goodsUse)) {
					sqlParams.put("goods_use", goodsUse);
					customCondition.append(" and goods_use like '%' || :goods_use || '%'");
				}
				customCondition.append("))");
			}
		}
		if (inMap.containsKey("transType")) {
			String transType = inMap.getString("transType");
			if (StrUtils.isNotNull(transType)) {
				List<String> transTypeList = StrUtils.getSubStrs(transType, ";", true);
				if (transTypeList != null || transTypeList.size() > 0) {
					customCondition.append(" and t.trans_type_label in (");
					for(int i = 0; i < transTypeList.size(); i++) {
						sqlParams.put("trans_type" + i, transTypeList.get(i));
						customCondition.append(":trans_type" + i + ",");						
					}
					customCondition.deleteCharAt(customCondition.length() - 1);
					customCondition.append(")");
				}
			}
		}
		if (inMap.containsKey("targetNo")) {
			String targetNo = inMap.getString("targetNo");
			if (StrUtils.isNotNull(targetNo)) {
				sqlParams.put("no", targetNo);
				customCondition.append(" and t.no like '%' || :no || '%'");
			}
		}
		if (inMap.containsKey("targetName")) {
			String targetName = inMap.getString("targetName");
			if (StrUtils.isNotNull(targetName)) {
				sqlParams.put("name", targetName);
				customCondition.append(" and t.name like '%' || :name || '%'");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);	
		//查询数据
		DoneTargetDao doneTargetDao = new DoneTargetDao();
		result = doneTargetDao.getDoneTargetListData(moduleId, dataSetNo, sqlParams);
		int totalRowCount = result.getInt("totalRowCount");
//		//转换格式
//		ParaMap custom = new ParaMap();
//		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
//		custom.put(MakeJSONData.CUSTOM_FIELDS, null);
//		List items = MakeJSONData.makeItems(result, custom);
		List resultList = new ArrayList();
		List filds = result.getFields();
		for (int i = 0; i < result.getSize(); i++) {
			ParaMap out = new ParaMap();
			String business_type = null;
			String land_area = null;
			String mineral_area = null;
			for (int j = 0; j < filds.size(); j++) {
				if(filds.get(j).equals("business_type")){
					business_type = result.getRecordString(i, j);
				}
				if(filds.get(j).equals("land_area")){
					land_area = result.getRecordString(i, j);
				}
				if(filds.get(j).equals("mineral_area")){
					mineral_area = result.getRecordString(i, j);
				}
				out.put(filds.get(j), result.getRecordValue(i, j));
			}
			if(StrUtils.isNotNull(business_type) && business_type.startsWith("101")){
				out.put("goods_size", land_area);
			}else{
				out.put("goods_size", mineral_area);
			}
			resultList.add(out);
		}
		result.clear();
		result.put("Rows", resultList);
		result.put("Total", totalRowCount);
		return result;
	}
}
