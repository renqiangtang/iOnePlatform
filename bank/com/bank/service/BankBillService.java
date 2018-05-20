package com.bank.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.bank.dao.BankBillDao;
import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.trademan.dao.LicenseManageDao;


public class BankBillService extends BaseService {
	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             银行入账单列表,包括未提交，未结束的标的
	 */
	public ParaMap getBankBillListData(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = "";;
		getPageInfo(inMap);
		getQueryCondition(inMap);
		BankBillDao bankBillDao = new BankBillDao();
		ParaMap out = bankBillDao.getBankBillListData(moduleId, dataSetNo,
				sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		List items = getDataInfo(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}

	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             入账单更新
	 */
	public ParaMap updateBankBillData(ParaMap inMap) throws Exception {
		BankBillDao bankBillDao = new BankBillDao();
		ParaMap out = bankBillDao.updateBankBillData(inMap);
		return out;
	}

	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             银行入账单
	 */
	public ParaMap getBankBillData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		BankBillDao bankBillDao = new BankBillDao();
		ParaMap out = bankBillDao.getBankBillData(moduleId, dataSetNo, id);
		String inAccountModel = inMap.getString("inAccountMode");
		ParaMap snSubMap = new ParaMap();
		snSubMap.put("moduleId", moduleId);
		snSubMap.put("accountNo", inMap.getString("accountNo"));
		snSubMap.put("accountName", inMap.getString("accountName"));
		snSubMap.put("accountBank", inMap.getString("accountBank"));
		if ("0".equals(inAccountModel) || "1".equals(inAccountModel)) {
			snSubMap = getBankSNListData(inMap);
		} else if ("2".equals(inAccountModel) || "3".equals(inAccountModel)) {
			snSubMap = getBankSubListData(inMap);
		}
		out.put("snsub", snSubMap);
		return out;
	}

	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             银行入账单删除
	 */
	public ParaMap deleteBankBillData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		BankBillDao bankBillDao = new BankBillDao();
		ParaMap out = bankBillDao.deleteBankBillData(moduleId, dataSetNo, id);
		return out;
	}

	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             银行子账号转主账号
	 */
	public ParaMap transferBankBillData(ParaMap inMap) throws Exception {
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		String id = inMap.getString("id");
		sqlParams.put("id", id);
		sqlParams.put("status", 2);
		ParaMap result = dataSetDao.updateData("trans_account_bill", "id",
				sqlParams,null);
		return result;
	}



	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             提交银行入账单
	 */
	public ParaMap submitBankBillData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		String id = inMap.getString("id");
		String applyId = inMap.getString("applyId");
		String name = inMap.getString("name");
		String bidderAccountName = inMap.getString("bidderAccountName");
		String end = inMap.getString("end");
		String earnest_money_pay = inMap.getString("earnest_money_pay");
		boolean flag = true;
		if (name != null && !name.equals("")
				&& !name.equalsIgnoreCase("null")&&name.equals(bidderAccountName))
			flag = false;
		if (end != null && !end.equals("")
				&& !end.equalsIgnoreCase("null")&&!end.equals("end"))
			flag = false;
		if (earnest_money_pay != null && !earnest_money_pay.equals("")
				&& !earnest_money_pay.equalsIgnoreCase("null")&&!earnest_money_pay.equals("1"))
			flag = false;
		BankBillDao bankBillDao = new BankBillDao();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		ParaMap result = null;
		if (flag) {
			sqlParams.put("id", id);
			sqlParams.put("status", 1);
			sqlParams.put("bill_type", 6);
			result = dataSetDao.updateData("trans_account_bill", "id",
					sqlParams);
		} else {
			// 先汇总金额再来判断总金额是否大于或等于保证金
			sqlParams.put("id", applyId);
			result = bankBillDao.amountEarnestMoney(moduleId, dataSetNo,
					sqlParams);
			List list = (List) ((List) result.get("rs")).get(0);
			Integer amount = (Integer) list.get(0);
			Integer earnestMoney = (Integer) list.get(1);
			if (amount >= earnestMoney) {
				// 设置竞买申请单为通过
				ParaMap license = new ParaMap();
				sqlParams.clear();
				sqlParams.put("id", id);
				license = bankBillDao.transLicenseUpdate(moduleId, dataSetNo,
						sqlParams);
				List licenseList = (List) ((List) license.get("rs")).get(0);
				sqlParams.clear();
				sqlParams.put("id", licenseList.get(0));
				sqlParams.put("earnest_money_pay", 1);
				dataSetDao.updateData("trans_license", "id", sqlParams);
				// 查找竞买申请通过id
				sqlParams.clear();
				sqlParams.put("id", id);
				ParaMap tlMap = bankBillDao.queryTLid(moduleId, dataSetNo,
						sqlParams);
				List tlList = (List) ((List) tlMap.get("rs")).get(0);
				String licenseId = (String) tlList.get(0);
				LicenseManageDao licenseDao = new LicenseManageDao();
				licenseDao.setLicenseStatusPass(null, null, licenseId);
			}
			// 正常转款
			sqlParams.clear();
			sqlParams.put("id", id);
			sqlParams.put("status", 1);
			result = dataSetDao.updateData("trans_account_bill", "id",
					sqlParams);
		}
		return result;
	}

	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             是否是错转款的判断
	 */
	public ParaMap submitCheckBankBillData(ParaMap inMap) throws Exception {
		BankBillDao bankBillDao = new BankBillDao();
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		//判断是否是错转款
		ParaMap out = bankBillDao.submitCheckBankBillData(moduleId, dataSetNo,
				inMap);
		//out.put("sysdate", bankBillDao.querySysdate(moduleId,dataSetNo,inMap));
		ParaMap sd = bankBillDao.querySysdate(moduleId,dataSetNo,inMap);
		List li = sd.getList("rs");
		List list = (List) li.get(0);
		String da = (String) list.get(0);
		System.out.println(da);
		out.put("sysdate", list.get(0));
		return out;
	}

	/**
	 * 
	 * 新增入账单判断交款户名与入账申请单户名是否一至
	 */
	public ParaMap saveBankBillData(ParaMap inMap) throws Exception {
		BankBillDao bankBillDao = new BankBillDao();
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		ParaMap out = bankBillDao.saveBankBillData(moduleId, dataSetNo,
				inMap);
		return out;
	}
	
	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             子账号转主账号,包括已提交、已结束的标的、没银行接口的
	 */
	public ParaMap getBankBillSubAccountListData(ParaMap inMap)
			throws Exception {
		ParaMap sqlParams = inMap;
		getPageInfo(inMap);
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		// 子账号转主账户月底了所有的已提交的但标的未结束和已结束的都在入账单记录都需要出现
		StringBuffer customCondition = new StringBuffer("");
		 if (inMap.containsKey("u")) {
				String u = inMap.getString("u");
				if (u != null && !u.equals("")
						&& !u.equalsIgnoreCase("null")) {
					sqlParams.put("id", u);
					customCondition
							.append(" and su.id = :id");
				}
			}
		 String days = inMap.getString("days");
		 String status = "5";
		 if (days != null && !days.equals("") &&
		 !days.equalsIgnoreCase("null")) {
		 int day = Integer.valueOf(days);
			 if (day > 0 && isEndMonth(day,moduleId)) {
			 status = "0";
			 }
		 }
		 sqlParams.put("status", status);
		 customCondition.append(" and tt.status >= :status");
		String inAccountMode = inMap.getString("inAccountModel");
		// 有银行接口不需要做子账号转主账号
		if ("1".equals(inAccountMode) || "3".equals(inAccountMode))
			customCondition.append(" and 1=2");
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		BankBillDao bankBillDao = new BankBillDao();
		ParaMap out = bankBillDao.getBankBillSubAccountListData(moduleId,
				dataSetNo, sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		List items = getDataInfo(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}

	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             银行所有入账单，包括已提交、已结束的标的
	 */
	public ParaMap getBankBillAllListData(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		getPageInfo(inMap);
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = "";
		getQueryCondition(sqlParams);
		BankBillDao bankBillDao = new BankBillDao();
		ParaMap out = bankBillDao.getBankBillListData(moduleId, dataSetNo,
				sqlParams);
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("u")) {
			String u = inMap.getString("u");
			if (u != null && !u.equals("")
					&& !u.equalsIgnoreCase("null")) {
				sqlParams.put("id", u);
				customCondition
						.append(" and su.id = :id");
			}
		}
		String days = inMap.getString("days");
		 String status = "5";
		 if (days != null && !days.equals("") &&
		 !days.equalsIgnoreCase("null")) {
		 int day = Integer.valueOf(days);
			 if (day > 0 && isEndMonth(day,moduleId)) {
			 status = "0";
			 }
		 }
		sqlParams.put("status", status);
		customCondition.append(" and tt.status>=:status");
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		ParaMap out1 = bankBillDao.getBankBillAllListData(moduleId, dataSetNo,
				sqlParams);
		int totalRowCount = out.getInt("totalRowCount")+out1.getInt("totalRowCount");
		List items = getDataInfo(out);
		List items1 = getDataInfo(out1);
		items.addAll(items1);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}

	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             获取银行登录者的银行信息
	 */
	public ParaMap getBankInfoData(ParaMap inMap) throws Exception {
		ParaMap returnMap = new ParaMap();
		String moduleId = inMap.getString("moduleId");
		BankBillDao bankBillDao = new BankBillDao();
		ParaMap out = bankBillDao.getBankInfoData(moduleId, inMap);
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("accountNo", "no");
		fields.put("accountName", "name");
		fields.put("accountBank", "bank_name");
		fields.put("inAccountMode", "in_account_mode");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		if (items != null && !items.isEmpty())
			returnMap = (ParaMap) items.get(0);
		return returnMap;
	}

	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             银行流水号
	 */
	public ParaMap getBankSNListData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		BankBillDao bankBillDao = new BankBillDao();
		ParaMap out = bankBillDao.getBankSNListData(moduleId, dataSetNo, inMap);
		return out;
	}

	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             银行子账号
	 */
	public ParaMap getBankSubListData(ParaMap inMap) throws Exception {
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		BankBillDao bankBillDao = new BankBillDao();
		ParaMap out = bankBillDao
				.getBankSubListData(moduleId, dataSetNo, inMap);
		return out;
	}

	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             设置和读取分页信息
	 */
	private ParaMap getPageInfo(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		String sortField = inMap.getString("sortname");
		String sortDir = inMap.getString("sortorder");
		if (sortField != null && !sortField.equals("")
				&& !sortField.equalsIgnoreCase("null") && sortDir != null
				&& !sortDir.equals("") && !sortDir.equalsIgnoreCase("null"))
			sortField = sortField + " " + sortDir;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		if (sortField == null || sortField.equals("")
				|| sortField.equalsIgnoreCase("null"))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, " create_date desc");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		sqlParams.put("id", sqlParams.getString("u"));
		return sqlParams;
	}

	/**
	 * @param inMap
	 * @return
	 * @throws Exception
	 *             设置自定义查询条件
	 */
	private ParaMap getQueryCondition(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("bidderAccountName")) {
			String licenseNO = inMap.getString("bidderAccountName");
			if (licenseNO != null && !licenseNO.equals("")
					&& !licenseNO.equalsIgnoreCase("null")) {
				sqlParams.put("bidderAccountName", licenseNO);
				customCondition
						.append(" and tab.bidder_account_name like '%' || :bidderAccountName || '%'");
			}
		}
		if (inMap.containsKey("bidderAccountNo")) {
			String name = inMap.getString("bidderAccountNo");
			if (name != null && !name.equals("")
					&& !name.equalsIgnoreCase("null")) {
				sqlParams.put("bidderAccountNo", name);
				customCondition
						.append(" and tab.bidder_account_no like '%' || :bidderAccountNo || '%'");
			}
		}
		if (inMap.containsKey("startTime")) {
			String beginListTime = inMap.getString("startTime");
			if (beginListTime != null && !beginListTime.equals("")
					&& !beginListTime.equalsIgnoreCase("null")) {
				sqlParams.put("startTime", beginListTime);
				customCondition
						.append(" and to_char(tab.bank_business_date, 'yyyy-mm-dd') >= :startTime");
			}
		}
		if (inMap.containsKey("endTime")) {
			String endListTime = inMap.getString("endTime");
			if (endListTime != null && !endListTime.equals("")
					&& !endListTime.equalsIgnoreCase("null")) {
				sqlParams.put("endTime", endListTime);
				customCondition
						.append(" and to_char(tab.bank_business_date, 'yyyy-mm-dd') <= :endTime");
			}
		}
		ParaMap uParams = new ParaMap();
		 if (inMap.containsKey("u")) {
			String u = inMap.getString("u");
			if (u != null && !u.equals("")&& !u.equalsIgnoreCase("null")) {
				uParams.put("id", u);
				customCondition.append(" and su.id = :id");
			}
		}
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return sqlParams;
	}

	/**
	 * @param out
	 * @return
	 * @throws Exception
	 *             获取所有数据后解析成集合
	 */
	private List getDataInfo(ParaMap out) throws Exception {
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.put("bidder_account_no", "bidder_account_no");
		fields.put("bidder_account_name", "bidder_account_name");
		fields.put("bidder_account_bank", "bidder_account_bank");
		fields.put("account_bank", "account_bank");
		fields.put("account_no", "account_no");
		fields.put("account_name", "account_name");
		fields.put("child_account_no", "child_account_no");
		fields.put("child_account_name", "child_account_name");
		fields.put("child_account_bank", "child_account_bank");
		fields.put("bill_type", "bill_type");
		fields.put("amount", "amount");
		fields.put("bank_business_date", "bank_business_date");
		fields.put("status", "status");
		fields.put("id", "id");
		fields.put("is_bank_input", "is_bank_input");
		fields.put("apply_id", "apply_id");
		fields.put("name", "name");
		fields.put("remark", "remark");
		fields.put("currency", "currency");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		return items;
	}

	/**
	 * @param out
	 * @param map
	 * @return
	 * @throws Exception
	 *             获取银行数据,可支持liger ui和tokeninput
	 */
	private List getBankDataInfo(ParaMap out, Map map) throws Exception {
		ParaMap custom = new ParaMap();
		custom.put(MakeJSONData.CUSTOM_RECURSE, 0);
		ParaMap fields = new ParaMap();
		fields.putAll(map);
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		return items;
	}

	/**
	 * @param day
	 * @return 根据给定的天数判断今天是否属于月底
	 */
	private boolean isEndMonth(int day,String moduleId) throws Exception {//day设定每月倒数几天为月末
		boolean flag = false;
		ParaMap sqlParams = new ParaMap();
		Calendar cal = Calendar.getInstance();
		BankBillDao bbd = new BankBillDao();
		ParaMap pm = bbd.querySysdate(moduleId, "", sqlParams);
		List li = pm.getList("rs");
		List list = (List) li.get(0);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		dateFormat.parse(list.get(0).toString());
		System.out.println(dateFormat.parse(list.get(0).toString()));
		cal.setTime(dateFormat.parse(list.get(0).toString()));
		int currentDay = cal.get(Calendar.DAY_OF_MONTH);//今天是第几天
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		int maxDay = cal.get(Calendar.DAY_OF_MONTH);//本月多少天
		if (maxDay - currentDay <= day - 1)
			flag = true;
		return flag;
	}

	/**
	 * 根据流水号查找转账账户
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap queryTabaAccount(ParaMap inMap) throws Exception {
		ParaMap keyData = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		keyData.put("id", inMap.getString("id"));
		keyData = dataSetDao.querySimpleData("trans_account_bill_apply",
				keyData);
		return keyData;
	}

	/**
	 * 银行入账查询
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap queryBankBill(ParaMap inMap) throws Exception {
		ParaMap sqlParams = inMap;
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		getPageInfo(inMap);
		getBankBill(sqlParams);
		BankBillDao bankBillDao = new BankBillDao();
		ParaMap out = bankBillDao.getBankBill(moduleId, dataSetNo, sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		List items = getDataInfo(out);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}

	/**
	 * 银行入账自定义查询
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getBankBill(ParaMap inMap) throws Exception {
		String billtype = inMap.getString("billtype");
		String status = inMap.getString("status");
		String startTime = inMap.getString("startTime");
		String endTime = inMap.getString("endTime");
		ParaMap sqlParams = inMap;
		StringBuffer customCondition = new StringBuffer("");
		if (inMap.containsKey("billtype")) {
			if (billtype != null && !billtype.equals("")
					&& !billtype.equalsIgnoreCase("null")) {
				sqlParams.put("billtype", billtype);
				customCondition.append(" and bill_type = :billtype");
			} else {
				customCondition
						.append(" and (bill_type = 1 or bill_type = 6) ");
			}
		} else {
			customCondition.append(" and (bill_type = 1 or bill_type = 6) ");
		}
		if (inMap.containsKey("startTime")) {
			if (startTime != null && !startTime.equals("")
					&& !startTime.equalsIgnoreCase("null")) {
				sqlParams.put("startTime", startTime + " 00:00:00");
				sqlParams.put("date_time_format", "yyyy-mm-dd hh24:mi:ss");
				customCondition
						.append(" and to_char(bank_business_date, :date_time_format) >= :startTime");
			}
		}
		if (inMap.containsKey("endTime")) {
			if (endTime != null && !endTime.equals("")
					&& !endTime.equalsIgnoreCase("null")) {
				sqlParams.put("endTime", endTime + " 23:59:59");
				sqlParams.put("date_time_format", "yyyy-mm-dd hh24:mi:ss");
				customCondition
						.append(" and to_char(bank_business_date, :date_time_format) <= :endTime");
			}
		}

		customCondition.append(" and is_valid=1 and status > 0 ");
		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return sqlParams;
	}
}
