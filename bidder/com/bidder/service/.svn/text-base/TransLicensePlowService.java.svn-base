package com.bidder.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.bidder.dao.TransLicenseMineDao;
import com.bidder.dao.TransLicensePlowDao;
import com.sysman.dao.UserDao;

public class TransLicensePlowService extends BaseService {

	public ParaMap getEarnestMoney(ParaMap inMap) throws Exception {
		TransLicensePlowDao licenseDao = new TransLicensePlowDao();
		String id = inMap.getString("targetId");
		String userId = inMap.getString("u");
		ParaMap targetMap = new ParaMap();
		UserDao userDao = new UserDao();
		ParaMap bidderMap = userDao.getBidderData(null, null, userId);
		String bidderId = null;
		if(bidderMap !=null && bidderMap.getSize()>0){
			ParaMap bidder = (ParaMap)(bidderMap.getListObj().get(0));
			targetMap.put("bidder",bidder);
			bidderId = bidder.getString("id");
		}
		ParaMap target = licenseDao.getTransTarget(id);
		if(target !=null && target.getSize()>0){
			targetMap.put("beginListTime",target.getRecordString(0, "begin_list_time"));
			targetMap.put("endListTime",target.getRecordString(0, "end_list_time"));
			targetMap.put("earnestMoney", target.getRecordString(0, "earnest_money"));
			targetMap.put("status",target.getRecordString(0, "status"));
		}
		ParaMap goods = licenseDao.getTransGoodsByTarget(id);
		if(goods !=null && goods.getSize()>0){
			targetMap.put("area",goods.getRecordString(0, "area"));
		}
		ParaMap license = licenseDao.getLicenseByTargetBidder(id, bidderId);
		if(license !=null && license.getSize()>0){
			targetMap.put("licenseId",license.getRecordString(0, "id"));
		}
		targetMap.put("stdTime", DateUtils.nowStr());
		return targetMap;
	}

	

}
