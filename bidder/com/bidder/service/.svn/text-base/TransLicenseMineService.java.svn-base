package com.bidder.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.DateUtils;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.bidder.dao.TransLicenseMineDao;
import com.sysman.dao.ExtendDao;
import com.sysman.dao.UserDao;

public class TransLicenseMineService extends BaseService {

	/**
	 * 购物车标的数量
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getCartTargetNum(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String bidderId = this.getBidderId(userId);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "trans_bidder_main");
		sqlParams.put("dataSetNo", "get_cart_target_num");
		sqlParams.put("bidderId", bidderId);
		sqlParams.put("cartType", 0);
		ParaMap result = dataSetDao.queryData(sqlParams);
		List list = this.changeToObjList(result, null);
		ParaMap out = new ParaMap();
		if (list != null && list.size() > 0) {
			out = (ParaMap) list.get(0);
		} else {
			out.put("num", 0);
		}
		return out;
	}

	/**
	 * 购物车标的列表
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap carTargetList(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String type = inMap.getString("type");
		int page = 0;
		if (StrUtils.isNotNull(inMap.getString("page"))) {
			page = inMap.getInt("page");
		}
		int pagesize = 0;
		if (StrUtils.isNotNull(inMap.getString("pagesize"))) {
			page = inMap.getInt("pagesize");
		}
		type = StrUtils.isNull(type) ? "1" : type;
		String bidderId = this.getBidderId(userId);
		TransLicenseMineDao licenseDao = new TransLicenseMineDao();
		ParaMap result = licenseDao.getCartTargetList(bidderId, type, page,
				pagesize);
		int totalRowCount = result.getInt("totalRowCount");
		ParaMap out = new ParaMap();
		out.put("Rows", this.changeToObjList(result, null));
		out.put("Total", totalRowCount);
		return out;
	}

	/**
	 * 标的保证金列表
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap earnestMoneyList(ParaMap inMap) throws Exception {
		String targetId = inMap.getString("targetId");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("target_id", targetId);
		keyData.put("is_valid", 1);
		ParaMap result = dataSetDao.querySimpleData("trans_target_earnest_money", keyData);
		int totalRowCount = result.getInt("totalRowCount");
		ParaMap out = new ParaMap();
		out.put("Rows", this.changeToObjList(result, null));
		out.put("Total", totalRowCount);
		return out;
	}

	/**
	 * 加入购物车
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap addToCar(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String userId = inMap.getString("u");
		String bidderId = this.getBidderId(userId);
		String targetId = inMap.getString("targetId");
		TransLicenseMineDao licenseDao = new TransLicenseMineDao();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		// 校验当前标的状态及是否已经购买
		ParaMap result = licenseDao.isCanBuy(bidderId, targetId);
		if (!"1".equals(result.getString("state"))) {
			out.put("state", 0);
			out.put("message", "加入购物车失败！" + result.getString("message"));
			return out;
		}
		// 判断当前有无正在使用的购物车
		String cartId = null;
		sqlParams.clear();
		sqlParams.put("bidder_id", bidderId);
		sqlParams.put("status", 0);
		ParaMap cartMap = dataSetDao.querySimpleData("trans_cart", sqlParams,
				" create_date desc ");
		if (cartMap.getSize() > 0) {
			cartId = (String) cartMap.getRecordValue(0, "id");
		} else {
			cartId = licenseDao.saveCart(bidderId, userId);
		}
		// 判断是否在购物车明细里
		sqlParams.clear();
		sqlParams.put("cart_id", cartId);
		sqlParams.put("target_id", targetId);
		ParaMap dtlMap = dataSetDao.querySimpleData("trans_cart_dtl",
				sqlParams, " create_date desc ");
		if (dtlMap.getSize() > 0) {
			String dtlId = String.valueOf(dtlMap.getRecordValue(0, "id"));
			int status = Integer.parseInt(String.valueOf(dtlMap.getRecordValue(
					0, "status")));
			if (status == 1) {
				out.put("state", 0);
				out.put("message", "加入购物车失败！该标的已经在购物车中了。");
			} else if (status == 2) {
				out.put("state", 0);
				out.put("message", "加入购物车失败！该标的您已申请竞买了。");
			} else {
				sqlParams.clear();
				sqlParams.put("id", dtlId);
				sqlParams.put("status", 1);
				ParaMap rdMap = dataSetDao.updateData("trans_cart_dtl", "id",
						sqlParams);
				if ("1".equals(rdMap.getString("state"))) {
					out.put("state", 1);
					out.put("message", "加入购物车成功！");
				} else {
					out.put("state", 0);
					out.put("message", "加入购物车失败！");
				}
			}
		} else {
			String dtlId = licenseDao.saveCartDtl(cartId, targetId, userId);
			if (StrUtils.isNotNull(dtlId)) {
				out.put("state", 1);
				out.put("message", "加入购物车成功！");
			} else {
				out.put("state", 0);
				out.put("message", "加入购物车失败！");
			}
		}
		return out;
	}

	/**
	 * 从购物车中删除
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap removeFromCar(ParaMap inMap) throws Exception {
		String dtlId = inMap.getString("dtlId");
		TransLicenseMineDao licenseDao = new TransLicenseMineDao();
		ParaMap out = licenseDao.removeCartDtl(dtlId);
		return out;
	}

	/**
	 * 从购物车中删除
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap deleteCarTarget(ParaMap inMap) throws Exception {
		String dtlId = inMap.getString("dtlId");
		TransLicenseMineDao licenseDao = new TransLicenseMineDao();
		ParaMap out = licenseDao.deleteCarTarget(dtlId);
		return out;
	}

	/**
	 * 购物列表页面查询条件
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap initProductListPageParam(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String bidderId = this.getBidderId(userId);
		TransLicenseMineDao licenseDao = new TransLicenseMineDao();
		ParaMap organMap = licenseDao.getTransOrganList(bidderId);
		String organIds = licenseDao.getOrganIdsByMap(organMap);
		ParaMap out = new ParaMap();
		if (StrUtils.isNotNull(organIds)) {
			ParaMap cantonMap = licenseDao.getTransCantonList(organIds);
			ParaMap businessTypeMap = licenseDao
					.getTransBusinessTypeList(organIds);
			ParaMap transTypeMap = licenseDao
					.getTransTransactionTypeList(organIds);
			out.put("cantons", this.changeToObjList(cantonMap, null));
			out.put("businessTypes",
					this.changeToObjList(businessTypeMap, null));
			out.put("transTypes", this.changeToObjList(transTypeMap, null));
		}
		return out;
	}

	public ParaMap getEarnestMoney(ParaMap inMap) throws Exception {
		TransLicenseMineDao licenseDao = new TransLicenseMineDao();
		String id = inMap.getString("targetId");
		String userId = inMap.getString("u");
		ParaMap targetMap = new ParaMap();
		//targetMap.put("target_earnest_money",licenseDao.getTargetEarnestMoneyList(id));
		UserDao userDao = new UserDao();
		ParaMap bidderMap = userDao.getBidderData(null, null, userId);
		if(bidderMap !=null && bidderMap.getSize()>0){
			targetMap.put("bidder",(ParaMap)(bidderMap.getListObj().get(0)));
		}
		ParaMap target = licenseDao.getTransTarget(id);
		if(target !=null && target.getSize()>0){
			targetMap.put("beginNoticeTime",target.getRecordString(0, "begin_notice_time"));
//			targetMap.put("endListTime",target.getRecordString(0, "end_list_time"));
			if(target.getRecordString(0, "trans_type").equals("0")){//按照需求挂牌修改为集中竞价结束时间，拍卖修改为拍卖开始时间
				targetMap.put("endFocusTime",target.getRecordString(0, "end_focus_time"));
			}else{
				targetMap.put("endFocusTime",target.getRecordString(0, "begin_limit_time"));//按照需求拍卖开始时间
			}
		}
		targetMap.put("stdTime", DateUtils.nowStr());
		//以下查询，扩展字段，委托代理人信息
		ExtendDao etd=new ExtendDao();
		ParaMap etdMap=etd.getExtendData("trans_bidder", ((ParaMap)(bidderMap.getListObj().get(0))).getString("id"));
		List liEtd=MakeJSONData.makeItems(etdMap);
		if(liEtd.size()>0){
			for(int i=0;i<liEtd.size();i++){
				ParaMap map=(ParaMap)liEtd.get(i); 
				if(map.getString("field_no").equals("txtAgentName")){
					targetMap.put("txtAgentName", map.getString("field_value"));
				}else if(map.getString("field_no").equals("txtAgentNo")){
					targetMap.put("txtAgentNo", map.getString("field_value"));
				}else if(map.getString("field_no").equals("txtAgentPhone")){
					targetMap.put("txtAgentPhone", map.getString("field_value"));
				}
			}
		}
		return targetMap;
	}

	/**
	 * 标的列表
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap transTargetlist(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String cantonId = inMap.getString("cantonId");
		String businessTypeId = inMap.getString("businessTypeId");
		String transTypeId = inMap.getString("transTypeId");
		String pageStr = inMap.getString("page");
		int page = 1;
		if (StrUtils.isNotNull(pageStr)) {
			try {
				page = Integer.parseInt(pageStr);
				page = page < 1 ? 1 : page;
			} catch (Exception e) {
				page = 1;
			}
		}
		inMap.put(DataSetDao.SQL_PAGE_INDEX, page);
		inMap.put(DataSetDao.SQL_PAGE_ROW_COUNT, 6);
		String bidderId = this.getBidderId(userId);
		inMap.put("bidderId", bidderId);
		String sql = "";
		if (StrUtils.isNotNull(cantonId)) {
			sql = sql + " and tg.canton_id = :cantonId ";
		}
		if (StrUtils.isNotNull(businessTypeId)) {
			sql = sql + " and tt.business_type = :businessTypeId ";
		}
		if (StrUtils.isNotNull(transTypeId)) {
			sql = sql + " and tt.trans_type_label = :transTypeId ";
		}
		sql=sql+" and tg.goods_type in ('301','401')";
		TransLicenseMineDao licenseDao = new TransLicenseMineDao();
		ParaMap out = licenseDao.transTargetlist(inMap, sql);
		int totalRowCount = out.getInt("totalRowCount");
		List resultList = new ArrayList();
		if (out.getSize() > 0) {
			List filds = out.getFields();
			for (int i = 0; i < out.getSize(); i++) {
				ParaMap targetMap = new ParaMap();
				List record = (List) out.getRecords().get(i);
				String id = "";
				if (filds != null && filds.size() > 0) {
					for (int j = 0; j < filds.size(); j++) {
						targetMap.put((String) filds.get(j), record.get(j));
						if ("id".equals((String) filds.get(j))) {
							id = (String) record.get(j);
						}
					}
				}
				targetMap.put("target_earnest_money",licenseDao.getTargetEarnestMoneyList(id));
				int is_cart = Integer.parseInt(targetMap.getString("is_cart"));
				int is_union = Integer.parseInt(targetMap.getString("is_union"));
				int is_suspend = Integer.parseInt(targetMap.getString("is_suspend"));
				Date begin_apply_time = null;
				String begin_apply_str = targetMap.getString("begin_apply_time");
				if (StrUtils.isNotNull(begin_apply_str)) {
					begin_apply_time = targetMap.getDate("begin_apply_time");
				}
				Date end_apply_time = null;
				String end_apply_str = targetMap.getString("end_apply_time");
				if (StrUtils.isNotNull(end_apply_str)) {
					end_apply_time = targetMap.getDate("end_apply_time");
				}
				
				if(is_suspend==1){
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "标的已中止");
				}else if(is_cart == 1) {
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "标的已选入购物车");
				} else if (is_cart == 2) {
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "标的已做竞买申请");
				} else if (is_union >0) {
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "标的被联合竞买人选择");
				} else if (begin_apply_time == null || end_apply_time == null) {
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "该标的竞买申请时间错误");
				} else if ((DateUtils.now()).getTime() < begin_apply_time
						.getTime()) {
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "未到该标的竞买申请时间");
				} else if ((DateUtils.now()).getTime() > end_apply_time
						.getTime()) {
					targetMap.put("is_bid", 0);
					targetMap.put("bid_message", "已超过该标的竞买申请时间");
				} else {
					targetMap.put("is_bid", 1);
					targetMap.put("bid_message", "能够购买");
				}
				resultList.add(targetMap);
			}
		}
		out.clear();
		out.put("Rows", resultList);
		out.put("rowTotal", totalRowCount);
		out.put("page", page);
		out.put("pageTotal", this.getPageTotal(totalRowCount, 6));
		return out;

	}

	public String getBidderId(String userId) throws Exception {
		String bidderId = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", userId);
		ParaMap result = dataSetDao.querySimpleData("sys_user", sqlParams);
		String user_type = null;
		String ref_id = null;
		if (result.getSize() > 0) {
			List filds = result.getFields();
			List record = (List) result.getRecords().get(0);
			if (filds != null && filds.size() > 0) {
				for (int i = 0; i < filds.size(); i++) {
					if (filds.get(i).equals("user_type")) {
						user_type = String.valueOf(record.get(i));
					}
					if (filds.get(i).equals("ref_id")) {
						ref_id = (String) record.get(i);
					}
				}
			}
		}
		if (StrUtils.isNotNull(user_type) && "1".equals(user_type)) {
			bidderId = ref_id;
		}
		return bidderId;
	}

	/**
	 * 得到单位id
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getOrganId(String userId) throws Exception {
		String organId = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_user_manager");
		sqlParams.put("dataSetNo", "getUserOrganInfo");
		sqlParams.put("id", userId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		if (result.getSize() > 0) {
			List filds = result.getFields();
			List record = (List) result.getRecords().get(0);
			if (filds != null && filds.size() > 0) {
				for (int i = 0; i < filds.size(); i++) {
					if (filds.get(i).equals("id")) {
						organId = (String) record.get(i);
						break;
					}
				}
			}
		}
		return organId;
	}

	public List changeToObjList(ParaMap in, String firstId) throws Exception {
		List resultList = new ArrayList();
		if (StrUtils.isNotNull(firstId)) {
			ParaMap fMap = new ParaMap();
			fMap.put("id", firstId);
			resultList.add(fMap);
		}
		if (in.getSize() > 0) {
			List filds = in.getFields();
			for (int i = 0; i < in.getSize(); i++) {
				ParaMap out = new ParaMap();
				List record = (List) in.getRecords().get(i);
				String id = "";
				if (filds != null && filds.size() > 0) {
					for (int j = 0; j < filds.size(); j++) {
						out.put((String) filds.get(j), record.get(j));
						if ("id".equals((String) filds.get(j))) {
							id = (String) record.get(j);
						}
					}
				}
				if (StrUtils.isNotNull(firstId) && StrUtils.isNotNull(id)
						&& id.equals(firstId)) {
					resultList.set(0, out);
				} else {
					resultList.add(out);
				}
			}
		}
		return resultList;
	}

	public int getPageTotal(int rowCount, int pageSize) {
		return (rowCount + pageSize - 1) / pageSize;
	}

}
