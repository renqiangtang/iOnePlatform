package com.bidder.service;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.ParaMap;


public class PlowCommonService extends BaseService {

	public ParaMap getBusinessTypeList(ParaMap inMap)throws Exception{
		String goodsType = inMap.getString("goodsType");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("goods_type_id", goodsType);
		ParaMap result = dataSetDao.querySimpleData("trans_business_type_rel", sqlParams);
		ParaMap out = new ParaMap();
		out.put("businessType", result.getListObj());
		return out;
	}

}
