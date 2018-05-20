package com.bidder.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.bank.dao.BankBaseDao;
import com.bank.util.BankUtil;
import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.trademine.engine.EngineDao;
import com.base.utils.DateUtils;
import com.base.utils.IDGenerator;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.AppConfig;
import com.sms.dao.SmsDao;
import com.sysman.dao.ConfigDao;
import com.trademan.dao.LicenseManageMineDao;

public class TransLicensePlowDao extends BaseDao {
	
	public ParaMap getTransTarget(String targetId) throws Exception {
		DataSetDao dao = new DataSetDao();
    	ParaMap sqlParam = new ParaMap();
    	sqlParam.put("id", targetId);
    	sqlParam.put("is_valid",1);
		ParaMap out= dao.querySimpleData("trans_target", sqlParam);
		return out;
	}
	
	
	public ParaMap getTransGoodsByTarget(String targetId) throws Exception {
		ParaMap out = new ParaMap();
		DataSetDao dao = new DataSetDao();
    	ParaMap sqlParam = new ParaMap();
    	sqlParam.put("target_id", targetId);
    	sqlParam.put("is_valid",1);
		ParaMap result= dao.querySimpleData("trans_target_goods_rel", sqlParam);
		if(result!=null && result.getSize()>0){
			String goodsId = result.getRecordString(0, "goods_id");
			sqlParam.clear();
	    	sqlParam.put("id", goodsId);
	    	out= dao.querySimpleData("trans_goods", sqlParam);
		}
		return out;
	}
	
	
	
	public ParaMap getLicenseByTargetBidder(String targetId,String bidderId) throws Exception {
		if(StrUtils.isNull(targetId) || StrUtils.isNull(bidderId) ){
			return null;
		}
		DataSetDao dao = new DataSetDao();
    	ParaMap sqlParam = new ParaMap();
    	sqlParam.put("target_id", targetId);
    	sqlParam.put("bidder_id", bidderId);
    	sqlParam.put("is_valid",1);
		ParaMap out= dao.querySimpleData("trans_license", sqlParam);
		return out;
	}
	
	
}
