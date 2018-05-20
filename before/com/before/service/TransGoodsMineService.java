package com.before.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.DateUtils;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.before.dao.TransGoodsMineDao;
import com.before.dao.TransNoticeMineDao;
import com.sysman.dao.ConfigDao;
import com.sysman.dao.ParamsDao;
import com.trademine.engine.EngineDao;

/**
 * 交易前土地管理Service
 * 
 * @author chenl 2012-05-30
 */
public class TransGoodsMineService extends BaseService {
	
	private static int updateStatus = 2;
	private static String updateStatusName = "公告";

	/**
	 * 获取宗地信息列表
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	
	public ParaMap getTransGoodsList(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String organId = this.getOrganId(userId);
		String goods_type = inMap.getString("goods_type");
		String canton_id = inMap.getString("canton_id");
		String no = inMap.getString("no");
		String address = inMap.getString("address");
		String create_user_id = inMap.getString("create_user_id");
		String goods_use = inMap.getString("goods_use");
		String sortField = inMap.getString("sortname");
		String sortDir = inMap.getString("sortorder");
		String mode = inMap.getString("mode");
		String targetId=inMap.getString("targetId");
		String sql = " ";
		ParaMap sqlParam = new ParaMap();
		sqlParam.put("organId", organId);
		sqlParam.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParam.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		if (StringUtils.isNotEmpty(sortField)
				&& StringUtils.isNotEmpty(sortDir))
			sortField = sortField + " " + sortDir;
		if (StringUtils.isEmpty(sortField))
			sqlParam.put(DataSetDao.SQL_ORDER_BY, " create_date desc");
		else
			sqlParam.put(DataSetDao.SQL_ORDER_BY, sortField);

		if (StrUtils.isNotNull(goods_type) && !"-1".equals(goods_type)) {
			if(goods_type.indexOf(",")!=-1){
				sql = sql+" and tg.goods_type in ('301','401') ";
			}else{
				sql = sql+" and tg.goods_type in ('"+goods_type+"') ";
			}
		}
		if (StrUtils.isNotNull(canton_id) && !"-1".equals(canton_id)) {
			sql = sql + " and tg.canton_id = :canton_id ";
			sqlParam.put("canton_id", canton_id);
		}
		if (StrUtils.isNotNull(no)) {
			sql = sql + " and tg.no like '%" + no + "%' ";
			sqlParam.put("no", no);
		}
		if (StrUtils.isNotNull(create_user_id)) {
			sql = sql + " and tg.create_user_id = :create_user_id ";
			sqlParam.put("create_user_id", create_user_id);
		}
		if (StrUtils.isNotNull(goods_use) && !"-1".equals(goods_use)) {
			sql = sql + " and tg.goods_use like '%' || :goods_use || '%' ";
			sqlParam.put("goods_use", goods_use);
		}
		if (StrUtils.isNotNull(mode)) {
			sqlParam.put("mode", mode);
		}
		if (StrUtils.isNotNull(address)) {
			sql = sql + " and tg.address like '%' || :address || '%' ";
			sqlParam.put("address", address);
		}
		if (StrUtils.isNotNull(targetId)) {
			sqlParam.put("targetId", targetId);
		}
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		ParaMap out = transGoodsDao.getTransGoodsList(sql, sqlParam);
		int totalRowCount = out.getInt("totalRowCount");

		List items = this.changeToObjList(out, null);

		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	/**
	 * 更新或保存交易物数据
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap saveTransGoods(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		String mode = inMap.getString("mode");
		String userId = inMap.getString("u");
		String goodsId = inMap.getString("goodsId");
		String targetId = inMap.getString("targetId");
		if (StrUtils.isNull(mode)) {
			out.put("state", 0);
			out.put("goodsId", goodsId);
			out.put("targetId", targetId);
			out.put("message", "保存失败！操作类型不存在。");
			return out;
		} else {
			if (",new,modify,view,update,".indexOf("," + mode.trim() + ",") < 0) {
				out.put("state", 0);
				out.put("targetId", targetId);
				out.put("message", "保存失败！操作类型不存在。");
				return out;
			}
		}

		if (mode.equals("view")) {
			out.put("state", 0);
			out.put("goodsId", goodsId);
			out.put("targetId", targetId);
			out.put("message", "保存失败！操作类型不允许保存。");
			return out;
		} else if (mode.equals("new")) {
			if (StrUtils.isNotNull(targetId)) {
				ParaMap targetMap = transGoodsDao.getTargetInfo(targetId);
				if (targetMap != null && targetMap.getSize() > 0) {
					int status = targetMap.getRecordInteger(0, "status");
					if (status > updateStatus) {
						out.put("state", 0);
						out.put("goodsId", goodsId);
						out.put("targetId", targetId);
						out.put("message", "保存失败！标的已经" + updateStatusName + "。");
						return out;
					}
				} else {
					out.put("state", 0);
					out.put("goodsId", goodsId);
					out.put("targetId", targetId);
					out.put("message", "保存失败！标的信息错误。");
					return out;
				}
			}
		} else if (mode.equals("modify")) {
			if (StrUtils.isNull(goodsId)) {
				out.put("state", 0);
				out.put("goodsId", goodsId);
				out.put("targetId", targetId);
				out.put("message", "保存失败！操作类型错误或宗地信息错误。");
				return out;
			}
			// 得到宗地关联的最近的一个标的的状态
			ParaMap targetMap = transGoodsDao.getGoodsTargetList(goodsId);
			if (targetMap.getSize() > 0) {
				String tId = targetMap.getRecordString(0, "id");
				if (StrUtils.isNotNull(targetId) && !tId.equals(targetId)) {
					out.put("state", 0);
					out.put("goodsId", goodsId);
					out.put("targetId", targetId);
					out.put("message", "保存失败！标的信息错误。");
					return out;
				}
				String statusStr = String.valueOf(targetMap.getRecordValue(0,
						"status"));
				if (StrUtils.isNotNull(statusStr)
						&& Integer.parseInt(statusStr) > updateStatus) {
					out.put("state", 0);
					out.put("goodsId", goodsId);
					out.put("targetId", targetId);
					out.put("message", "保存失败！交易物关联的标的已" + updateStatusName
							+ "。");
					return out;
				}
				ParaMap judgeNoticeMap = this.judgeModifyByNotice(tId, goodsId,
						null, null);
				if (judgeNoticeMap != null
						&& !"1".equals(judgeNoticeMap.getString("state"))) {
					out.put("state", 0);
					out.put("goodsId", goodsId);
					out.put("targetId", targetId);
					out.put("message",
							"保存失败！" + judgeNoticeMap.getString("message"));
					return out;
				}
			}
		} else if (mode.equals("update")) {
			if (StrUtils.isNull(goodsId)) {
				out.put("state", 0);
				out.put("goodsId", goodsId);
				out.put("targetId", targetId);
				out.put("message", "保存失败！操作类型错误或宗地信息错误。");
				return out;
			}
			// 得到宗地关联的最近的一个标的的状态
			ParaMap targetMap = transGoodsDao.getGoodsTargetList(goodsId);
			if (targetMap.getSize() > 0) {
				String tId = targetMap.getRecordString(0, "id");
				if (StrUtils.isNotNull(targetId) && !tId.equals(targetId)) {
					out.put("state", 0);
					out.put("goodsId", goodsId);
					out.put("targetId", targetId);
					out.put("message", "保存失败！标的信息错误。");
					return out;
				}
				String statusStr = String.valueOf(targetMap.getRecordValue(0,
						"status"));
				if (StrUtils.isNotNull(statusStr)
						&& Integer.parseInt(statusStr) > 4) {
					out.put("state", 0);
					out.put("goodsId", goodsId);
					out.put("targetId", targetId);
					out.put("message", "保存失败！交易物关联的标的已交易结束");
					return out;
				}
			}
		}

		if (StrUtils.isNotNull(targetId)) {
			ParaMap judgeNoticeMap = this.judgeModifyByNotice(targetId,
					goodsId, null, null);
			if (judgeNoticeMap != null
					&& !"1".equals(judgeNoticeMap.getString("state"))) {
				return judgeNoticeMap;
			}
		}

		String relId = inMap.getString("relId");
		String multiTradeId = inMap.getString("multiTradeId");// 多指标ID
		String goods_type = inMap.getString("goods_type");
		if (StrUtils.isNull(goods_type)) {
			out.put("state", 0);
			out.put("goodsId", goodsId);
			out.put("targetId", targetId);
			out.put("message", "保存失败！交易物类型不能为空。");
			return out;
		}
		// goods_type = StrUtils.isNull(goods_type)?"101":goods_type;
		String no = inMap.getString("no");
		String name = no;
		String code = inMap.getString("code");
		String canton_id = inMap.getString("canton_id");
		String address = inMap.getString("address");// ------- 位置:
		String goods_use = inMap.getString("goods_use");// 用途
		String remark = inMap.getString("remark");// 其它说明
		String organId = this.getOrganId(userId);
		String storage = inMap.getString("storage");
		
		//矿产
		String mineral_area = inMap.getString("mineral_area");// 面积 
		String mineral_use_years = inMap.getString("mineral_use_years");// 使用年限
		String old_geological_work = inMap.getString("old_geological_work");// 以往地质工作情况
		String mineral_situation = inMap.getString("mineral_situation");// 矿（勘查）区基本情况
		String mineral_coordinate = inMap.getString("mineral_coordinate");// 矿（勘查）区坐标
		
		String mineral_level1 = inMap.getString("mineral_level1");// 开采标高开始
		String mineral_level2 = inMap.getString("mineral_level2");// 开采标高结束
		String mineral_storage = inMap.getString("mineral_storage");// 总存储量
		String mineral_storageUnit = inMap.getString("mineral_storageUnit");// 总存储量单位
		String mineral_methods = inMap.getString("mineral_methods");// 开采方式
		String mineral_production_scale = inMap.getString("mineral_production_scale");//生产规模
		String mineral_production_scaleUnit = inMap.getString("mineral_production_scaleUnit");//生产规模单位
		String mineral_requirements = inMap.getString("mineral_requirements");// 技术要求
		String aggregateInvestment=inMap.getString("aggregateInvestment");//投资总额
		String abandonTerm=inMap.getString("abandonTerm");//交付条件
		String projectTerm=inMap.getString("projectTerm");//规划设计条件
		
		// 第一步，先保存trans_goods
		ParaMap goodsMap = new ParaMap();
		if (mode.equals("new")) {
			goodsMap.put("id", null);
		} else if (mode.equals("modify")) {
			if (StrUtils.isNotNull(goodsId)) {
				goodsMap.put("id", goodsId);
			} else {
				goodsMap.put("id", null);
			}
		} else if (mode.equals("update")) {
			out.put("mode", "update");
			if (StrUtils.isNotNull(goodsId)) {
				goodsMap.put("id", goodsId);
			} else {
				out.put("state", 0);
				out.put("goodsId", goodsId);
				out.put("targetId", targetId);
				out.put("message", "保存失败！update参数错误。");
				return out;
			}
		} else {
			out.put("state", 0);
			out.put("goodsId", goodsId);
			out.put("targetId", targetId);
			out.put("message", "保存失败！操作类型错误。");
			return out;
		}
		goodsMap.put("no", no);
		goodsMap.put("name", name);
		goodsMap.put("code", code);
		goodsMap.put("goods_use", goods_use);
		goodsMap.put("address", address);
		goodsMap.put("canton_id", canton_id);
		goodsMap.put("remark", remark);
		goodsMap.put("goods_type", goods_type);
		goodsMap.put("storage", storage);
		goodsMap.put("create_user_id", userId);
		goodsMap.put("create_organ_id", organId);

		goodsMap = transGoodsDao.updateTransGoods(goodsMap);
		if (mode.equals("new") || StringUtils.isEmpty(goodsId)) {
			goodsId = goodsMap.getString("id");
		}

		// 第二步，保存GOODS的扩展信息
		if (StringUtils.isNotEmpty(goodsId)) {
			ParaMap extendMap = new ParaMap();
			extendMap.put("ref_table_name", "trans_goods");
			extendMap.put("ref_id", goodsId);
			DataSetDao dataSetDao = new DataSetDao();
			dataSetDao.deleteSimpleData("sys_extend", extendMap);
			//
			if(goods_type.equals("301")||goods_type.equals("401")){
				transGoodsDao.updateExtend("trans_goods", goodsId,"mineral_area", mineral_area, "矿区面积");
				transGoodsDao.updateExtend("trans_goods", goodsId,"mineral_use_years", mineral_use_years, "矿区使用年限");
				transGoodsDao.updateExtend("trans_goods", goodsId,"old_geological_work", old_geological_work, "以往地质工作情况");
				transGoodsDao.updateExtend("trans_goods", goodsId,"mineral_situation", mineral_situation, "矿（勘查）区基本情况");
				transGoodsDao.updateExtend("trans_goods", goodsId,"mineral_coordinate", mineral_coordinate, "矿（勘查）区坐标");
				
				transGoodsDao.updateExtend("trans_goods", goodsId,"mineral_level1", mineral_level1, "开采标高开始");
				transGoodsDao.updateExtend("trans_goods", goodsId,"mineral_level2", mineral_level2, "开采标高结束");
				transGoodsDao.updateExtend("trans_goods", goodsId,"mineral_storage", mineral_storage, "总存储量");
				transGoodsDao.updateExtend("trans_goods", goodsId,"mineral_storageUnit", mineral_storageUnit, "总存储量单位");
				transGoodsDao.updateExtend("trans_goods", goodsId,"mineral_methods", mineral_methods, "开采方式");
				transGoodsDao.updateExtend("trans_goods", goodsId,"mineral_production_scale", mineral_production_scale, "生产规模");
				transGoodsDao.updateExtend("trans_goods", goodsId,"mineral_production_scaleUnit", mineral_production_scaleUnit, "生产规模单位");
				transGoodsDao.updateExtend("trans_goods", goodsId,"mineral_requirements", mineral_requirements, "技术要求");
				transGoodsDao.updateExtend("trans_goods", goodsId,"aggregateInvestment", aggregateInvestment, "投资总额");
				transGoodsDao.updateExtend("trans_goods", goodsId,"abandonTerm", abandonTerm, "其它条件");
				transGoodsDao.updateExtend("trans_goods", goodsId,"projectTerm", projectTerm, "规划设计条件");
			}

		}

		// 第三步，保存trans_target
		if (mode.equals("new")) {// 新增
			if (StrUtils.isNotNull(targetId)) {// 标的id不为null 的新增
				int turn = transGoodsDao.getNewTargetGoodsRelTurn(targetId);
				ParaMap transTargetGoodsRelMap = new ParaMap();
				transTargetGoodsRelMap.put("id", "");
				transTargetGoodsRelMap.put("target_id", targetId);
				transTargetGoodsRelMap.put("goods_id", goodsId);
				transTargetGoodsRelMap.put("turn", turn);
				transTargetGoodsRelMap.put("create_user_id", userId);
				transTargetGoodsRelMap = transGoodsDao
						.updateTransTargetGoodsRel(transTargetGoodsRelMap);
				// 修改标的名称
				targetId = transGoodsDao.updateTargetNoNameByGoodsId(goodsId);
			}
		} else if (mode.equals("modify") || mode.equals("update")) {// 修改
			targetId = transGoodsDao.updateTargetNoNameByGoodsId(goodsId);
		}
		out.put("state", 1);
		out.put("goodsId", goodsId);
		out.put("targetId", targetId);
		out.put("message", "保存成功！");
		return out;
	}

	/**
	 * 获取宗地所关联的标的信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getGoodsTargetList(ParaMap inMap) throws Exception {
		String goodsId = inMap.getString("goodsId");
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		ParaMap out = transGoodsDao.getGoodsTargetList(goodsId);
		int totalRowCount = out.getInt("totalRowCount");
		List items = this.changeToObjList(out, null);
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	/**
	 * 获取标的信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransTargetList(ParaMap inMap) throws Exception {
		ParaMap sqlParams = getPageInfo(inMap);
		String moduleId = inMap.getString("moduleId");
		String dataSetNo = inMap.getString("dataSetNo");
		getQueryCondition(sqlParams);
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		ParaMap out = transGoodsDao.getTransTargetList(moduleId, dataSetNo,
				sqlParams);
		int totalRowCount = out.getInt("totalRowCount");
		//List items = getDataInfo(out);
		List items = out.getListObj();
		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}
	/**
	 * 设置自定义查询条件
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	private ParaMap getQueryCondition(ParaMap inMap) throws Exception {
		String organId = getOrganId(inMap.getString("u"));
		ParaMap sqlParams = inMap;
		StringBuffer customCondition = new StringBuffer("");
		sqlParams.put("date_format", "yyyy-MM-dd hh24:mi:ss");
		sqlParams.put("organId", organId);
		customCondition
				.append(" and ( tt.organ_id = :organId or ( tt.trans_organ_id = :organId and tt.status>=2 )) ");

		if (inMap.containsKey("trans_organ_id")
				&& !"-1".equals(inMap.getString("trans_organ_id"))) {
			String trans_organ_id = inMap.getString("trans_organ_id");
			if (StrUtils.isNotNull(trans_organ_id)) {
				if (trans_organ_id.equals(organId)) {
					customCondition
							.append(" and ( tt.trans_organ_id is null or tt.trans_organ_id='' or tt.trans_organ_id ='"
									+ trans_organ_id + "'  )");
				} else {
					customCondition.append(" and tt.trans_organ_id ='"
							+ trans_organ_id + "' ");
				}
			}
		}
		if (inMap.containsKey("canton_id")
				&& !"-1".equals(inMap.getString("canton_id"))) {
			String canton_id = inMap.getString("canton_id");
			if (StrUtils.isNotNull(canton_id)) {
				sqlParams.put("canton_id", canton_id);
				customCondition.append(" and tg.canton_id = :canton_id");
			}
		}
		if (inMap.containsKey("goods_type")
				&& !"-1".equals(inMap.getString("goods_type"))) {
			String goods_type = inMap.getString("goods_type");
			if (StrUtils.isNotNull(goods_type)) {
				sqlParams.put("goods_type", goods_type);
				if(goods_type.indexOf(",")!=-1){
					customCondition
					.append(" and ( tg.goods_type in ('301','401') or substr(tt.business_type, 1, 3) in ('301','401') ) ");
				}else{
					customCondition
					.append(" and ( tg.goods_type in('"+goods_type+"') or substr(tt.business_type, 1, 3) in('"+goods_type+"')) ");
				}
			}
		}
		if (inMap.containsKey("status")
				&& !"-1".equals(inMap.getString("status"))) {
			String status = inMap.getString("status");
			if (StrUtils.isNotNull(status)) {
				sqlParams.put("status", status);
				customCondition.append(" and tt.status=:status");
			}
		}
		if (inMap.containsKey("create_user_id")
				&& !"-1".equals(inMap.getString("create_user_id"))) {
			String create_user_id = inMap.getString("create_user_id");
			if (StrUtils.isNotNull(create_user_id)) {
				sqlParams.put("create_user_id", create_user_id);
				customCondition
						.append(" and tt.create_user_id=:create_user_id");
			}
		}
		if (inMap.containsKey("no")) {
			String no = inMap.getString("no");
			if (StrUtils.isNotNull(no)) {
				sqlParams.put("no", no);
				customCondition.append(" and tt.no like '%'||:no||'%'");
			}
		}
		if (inMap.containsKey("name")) {
			String name = inMap.getString("name");
			if (StrUtils.isNotNull(name)) {
				sqlParams.put("name", name);
				customCondition.append(" and tg.name like '%'||:name||'%'");
			}
		}

		if (inMap.containsKey("address")) {
			String address = inMap.getString("address");
			if (StrUtils.isNotNull(address)) {
				sqlParams.put("address", address);
				customCondition.append(" and tg.address like '%'||:address||'%'");
			}
		}
		if (inMap.containsKey("goods_use")
				&& !"-1".equals(inMap.getString("goods_use"))) {
			String goods_use = inMap.getString("goods_use");
			if (StrUtils.isNotNull(goods_use)) {
				sqlParams.put("goods_use", goods_use);
				customCondition
						.append(" and tg.goods_use like '%'||:goods_use||'%'");
			}
		}
		if (inMap.containsKey("is_net_trans")
				&& !"-1".equals(inMap.getString("is_net_trans"))) {
			String is_net_trans = inMap.getString("is_net_trans");
			if (StrUtils.isNotNull(is_net_trans)) {
				sqlParams.put("is_net_trans", is_net_trans);
				customCondition.append(" and tt.is_net_trans =:is_net_trans");
			}
		}

		if (inMap.containsKey("trans_type")
				&& !"-1".equals(inMap.getString("trans_type"))) {
			String trans_type = inMap.getString("trans_type");
			if (StrUtils.isNotNull(trans_type)) {
				customCondition.append(" and tt.trans_type ='" + trans_type
						+ "' ");
			}
		}
		if (inMap.containsKey("business_type")
				&& !"-1".equals(inMap.getString("business_type"))) {
			String businessType = inMap.getString("business_type");
			if (StrUtils.isNotNull(businessType)) {
				String str[] = businessType.split(",");
				if (str != null && str.length > 0) {
					customCondition.append(" and  tt.business_type in('");
					for (int i = 0; i < str.length; i++) {
						if (i == str.length - 1) {
							customCondition.append(str[i] + "')");
						} else {
							customCondition.append(str[i] + "','");
						}
					}
				}
			}
		}else{
			customCondition
					.append(" and ( tg.goods_type in ('301','401') or substr(tt.business_type, 1, 3) in ('301','401') ) ");
		}

		ParaMap customConditions = new ParaMap();
		customConditions.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(DataSetDao.SQL_CUSTOM_CONDITIONS, customConditions);
		return sqlParams;
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
		if (StringUtils.isNotEmpty(sortField)
				&& StringUtils.isNotEmpty(sortDir))
			sortField = sortField + " " + sortDir;
		sqlParams.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParams.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		if (StringUtils.isEmpty(sortField))
			sqlParams.put(DataSetDao.SQL_ORDER_BY, " id desc");
		else
			sqlParams.put(DataSetDao.SQL_ORDER_BY, sortField);
		// sqlParams.put("id", sqlParams.getString("u"));
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
		fields.put("id", "id");
		fields.put("no", "no");
		fields.put("name", "name");
		fields.put("status", "status");
		fields.put("begin_price", "begin_price");
		fields.put("earnest_money", "earnest_money");
		fields.put("business_name", "business_name");
		fields.put("organ_name", "organ_name");
		fields.put("trans_organ_name", "trans_organ_name");
		fields.put("user_name", "user_name");
		fields.put("create_date", "create_date");
		custom.put(MakeJSONData.CUSTOM_FIELDS, fields);
		List items = MakeJSONData.makeItems(out, custom);
		return items;
	}

	/**
	 * 显示查询列表页面按钮
	 */
	public ParaMap showBtnByType(ParaMap inMap) throws Exception {
		ParaMap returnMap = new ParaMap();
		ParaMap btnMap = new ParaMap();
		ParaMap selMap = new ParaMap();
		// ParaMap titleMap=new ParaMap();
		Set targetNameSet = new HashSet();
		Set noLabelSet = new HashSet();
		Set nameLabelSet = new HashSet();
		ParaMap targetNameMap = new ParaMap();
		ParaMap noLabelMap = new ParaMap();
		ParaMap nameLabelMap = new ParaMap();

		String businessType = inMap.getString("businessType");
		if (StringUtils.isNotEmpty(businessType)) {// 在配置文件中查找businessType，查询有效的类型
			ConfigDao configDao = new ConfigDao();
			String[] str = businessType.split(",");
			if (str != null && str.length > 0) {
				for (int i = 0; i < str.length; i++) {
					String id = str[i];
					if (StringUtils.isNotEmpty(id)) {
						// 处理新增按钮
						ParaMap businessTypeData = configDao
								.getTransRuleConfig();
						if (businessTypeData != null) {
							businessTypeData = (ParaMap) businessTypeData
									.getDataByPath("businessTypes." + id);
							String isValid = businessTypeData
									.getString("isValid");
							String btnName = businessTypeData
									.getString("shortName");// 显示按钮名称
							String targetName = businessTypeData
									.getString("targetName");
							String noLabel = businessTypeData
									.getString("noLabel");
							String nameLabel = businessTypeData
									.getString("nameLabel");
							// 处理列表页显示列和列名
							if (StringUtils.isNotEmpty(targetName)) {
								targetNameSet.add(targetName);
							}
							if (StringUtils.isNotEmpty(noLabel)) {
								noLabelSet.add(noLabel);
							}
							if (StringUtils.isNotEmpty(nameLabel)) {
								nameLabelSet.add(nameLabel);
							}

							if ("1".equals(isValid)) {// 处理列表显示新增按钮名称
								btnMap.put(id, btnName);
							}
						}
						// 处理查询条件交易方式
						ParaMap transTypeTrans = configDao.getTransTypes(id);
						if (transTypeTrans != null) {
							Iterator key = transTypeTrans.keySet().iterator();
							while (key.hasNext()) {
								String label = (String) key.next();
								if (StringUtils.isNotEmpty(label)) {
									ParaMap obj = (ParaMap) transTypeTrans
											.get(label);
									if (obj != null) {
										String name = obj.getString("name");
										selMap.put(label, name);
									}
								}
							}
						}
					}
				}
			}
		}
		returnMap.put("btn", btnMap);
		returnMap.put("sel", selMap);
		returnMap.put("targetName", targetNameSet);
		returnMap.put("noLabel", noLabelSet);
		returnMap.put("nameLabel", nameLabelSet);
		return returnMap;
	}

	/**
	 * @param inMap
	 * @return 获取经办人和当前系统单位的所属的行政区
	 * @throws Exception
	 */
	public ParaMap initQueryParam(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		String organId = getOrganId(inMap.getString("u"));
		String goodsType = inMap.getString("goodsType");
		out.put("user",
				this.changeToObjList(
						transGoodsDao.getOrganCreateUserList(organId), null));
		out.put("canton", this.changeToObjList(
				transGoodsDao.getOrganCantonList(organId), null));
		out.put("transOrgan",
				this.changeToObjList(transGoodsDao.getAllTransOrgan(), null));
		out.put("transType", this.changeToObjList(
				transGoodsDao.getOrganTransTypeList(organId, goodsType), null));
		return out;
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

	/**
	 * 获取标的信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransTargetGoodsList(ParaMap inMap) throws Exception {
		ParaMap sqlParams = getPageInfo(inMap);
		String userId = inMap.getString("u");
		String targetId = inMap.getString("targetId");
		String symbol = inMap.getString("symbol");
		String dm = inMap.getString("dm");
		String organId = this.getOrganId(userId);

		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		ParaMap out = transGoodsDao.getTransTargetGoodsList(targetId, null);
		int totalRowCount = out.getInt("totalRowCount");

		List items = this.changeToObjList(out, null);

		out.clear();
		dm = StrUtils.isNull(dm) ? "goods" : dm;
		out.put(dm, items);
		return out;
	}

	/**
	 * 获取标的信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getOtherTargetGoodsList(ParaMap inMap) throws Exception {
		String userId = inMap.getString("u");
		String targetId = inMap.getString("targetId");
		String organId = this.getOrganId(userId);
		String goods_type = inMap.getString("goods_type");
		String canton_id = inMap.getString("canton_id");
		String no = inMap.getString("no");
		String address = inMap.getString("address");
		String create_user_id = inMap.getString("create_user_id");
		String goods_use = inMap.getString("goods_use");
		String sql = " ";
		ParaMap sqlParam = new ParaMap();
		String sortField = inMap.getString("sortname");
		String sortDir = inMap.getString("sortorder");
		sqlParam.put(DataSetDao.SQL_PAGE_INDEX, inMap.getInt("page"));
		sqlParam.put(DataSetDao.SQL_PAGE_ROW_COUNT, inMap.getInt("pagesize"));
		if (StringUtils.isNotEmpty(sortField)
				&& StringUtils.isNotEmpty(sortDir))
			sortField = sortField + " " + sortDir;
		if (StringUtils.isEmpty(sortField))
			sqlParam.put(DataSetDao.SQL_ORDER_BY, " create_date desc");
		else
			sqlParam.put(DataSetDao.SQL_ORDER_BY, sortField);

		if (StrUtils.isNotNull(goods_type)) {
			if(goods_type.indexOf(",")!=-1){
				sql = " and tg.goods_type in ('301','401') ";
			}else{
				sql = " and tg.goods_type in ('"+goods_type+"') ";
			}
			sqlParam.put("goods_type", goods_type);
		}
		if (StrUtils.isNotNull(canton_id)) {
			sql = " and tg.canton_id = :canton_id ";
			sqlParam.put("canton_id", canton_id);
		}
		if (StrUtils.isNotNull(no)) {
			sql = " and tg.no like '%" + no + "%' ";
			sqlParam.put("no", no);
		}
		if (StrUtils.isNotNull(address)) {
			sql = " and tg.address like '%" + address + "%' ";
			sqlParam.put("address", address);
		}
		if (StrUtils.isNotNull(create_user_id)) {
			sql = " and tg.create_user_id = :create_user_id ";
			sqlParam.put("create_user_id", create_user_id);
		}
		if (StrUtils.isNotNull(goods_use)) {
			sql = " and tg.goods_use = :goods_use ";
			sqlParam.put("goods_use", goods_use);
		}
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		ParaMap out = transGoodsDao.getTransAllGoodsList(organId, targetId,
				sql, sqlParam);
		int totalRowCount = out.getInt("totalRowCount");

		List items = this.changeToObjList(out, null);

		out.clear();
		out.put("Rows", items);
		out.put("Total", totalRowCount);
		return out;
	}

	/*
	 * 查询行政区
	 */
	public ParaMap cantonList(ParaMap inMap) throws Exception {
		ParaMap returnMap = new ParaMap();
		String organId = getOrganId(inMap.getString("u"));
		String goodsId = inMap.getString("goodsId");
		String moduleId = inMap.getString("moduleId");

		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();

		if (StrUtils.isNotNull(goodsId)) {
			sqlParams.clear();
			sqlParams.put("id", goodsId);
			ParaMap goodsMap = dataSetDao.querySimpleData("trans_goods",
					sqlParams);
			if (goodsMap.getSize() > 0) {
				organId = String.valueOf(goodsMap.getRecordValue(0,
						"create_organ_id"));
			}
		}

		// 查询行政区
		sqlParams.clear();
		sqlParams.put("moduleId", moduleId);
		if (StringUtils.isEmpty(moduleId)) {
			sqlParams.put("moduleNo", "trans_prepare_land_manage01");
		}
		//sqlParams.put("dataSetNo", "query_organ_trans_goods_canton");
		sqlParams.put("dataSetNo", "query_canton");
		sqlParams.put("organId", organId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		returnMap.put("canton", result.getList("rs"));

		return returnMap;
	}

	/**
	 * 获取trans_goods表中的数据
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTransGoodsData(ParaMap inMap) throws Exception {
		ParaMap data = new ParaMap();
		String goodsId = inMap.getString("goodsId");
		String cantonid = "";
		if (StringUtils.isNotEmpty(goodsId)) {
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap keyData = new ParaMap();
			keyData.put("id", goodsId);
			ParaMap goodsMap = dataSetDao.querySimpleData("trans_goods",
					keyData);
			List goodsList = this.changeToObjList(goodsMap, null);

			if (goodsList != null && goodsList.size() > 0) {
				data = (ParaMap) goodsList.get(0);
				cantonid = (String) data.get("canton_id");
			}
			if(StringUtils.isNotEmpty(cantonid)){
				String parentId = "";
				String canton = "";
				keyData.put("id", cantonid);
				ParaMap cantonMap = dataSetDao.querySimpleData("sys_canton",
						keyData);
				if (cantonMap.getSize() > 0) {
					List filds = cantonMap.getFields();
					for (int i = 0; i < cantonMap.getSize(); i++) {
						ParaMap out = new ParaMap();
						List record = (List) cantonMap.getRecords().get(i);
						if (filds != null && filds.size() > 0) {
							for (int j = 0; j < filds.size(); j++) {
								out.put((String) filds.get(j), record.get(j));
								if ("name".equals((String) filds.get(j))) {
									canton = (String) record.get(j);
								}
								if ("parent_id".equals((String) filds.get(j))) {
									parentId = (String) record.get(j);
								}
							}
						}
					}
				}
				if(!parentId.equals("440000")){
					keyData.clear();
					keyData.put("id", parentId);
					cantonMap = dataSetDao.querySimpleData("sys_canton",
							keyData);
					if (cantonMap.getSize() > 0) {
						List filds = cantonMap.getFields();
						for (int i = 0; i < cantonMap.getSize(); i++) {
							ParaMap out = new ParaMap();
							List record = (List) cantonMap.getRecords().get(i);
							if (filds != null && filds.size() > 0) {
								for (int j = 0; j < filds.size(); j++) {
									out.put((String) filds.get(j), record.get(j));
									if ("name".equals((String) filds.get(j))) {
										canton =  (String) record.get(j) + ">" + canton;
									}
								}
							}
							
						}
					}
				}
				data.put("canton", canton);
			}
			// returnMap.put("goods", goodsMap);

			// 扩展信息
			keyData.clear();
			keyData.put("ref_id", goodsId);
			keyData.put("ref_table_name", "trans_goods");
			ParaMap extendMap = dataSetDao.querySimpleData("sys_extend",
					keyData);
			if (extendMap.getSize() > 0) {
				List filds = extendMap.getFields();
				for (int i = 0; i < extendMap.getSize(); i++) {
					ParaMap out = new ParaMap();
					List record = (List) extendMap.getRecords().get(i);
					String field_no = "";
					String field_value = "";
					if (filds != null && filds.size() > 0) {
						for (int j = 0; j < filds.size(); j++) {
							out.put((String) filds.get(j), record.get(j));
							if ("field_no".equals((String) filds.get(j))) {
								field_no = (String) record.get(j);
							}
							if ("field_value".equals((String) filds.get(j))) {
								field_value = (String) record.get(j);
							}
						}
					}
					data.put(field_no, field_value);
				}
			}
		}
		return data;
	}

	/**
	 * 初始化标的修改页面下拉列表框
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap initTargetEditPageParam(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		String organId = getOrganId(inMap.getString("u"));
		String goodsType = inMap.getString("goodsType");

		// 业务类型
		out.put("businessType", this.changeToObjList(
				transGoodsDao.getOrganBusinessList(organId, goodsType), null));

		// 交易方式
		out.put("transType", this.changeToObjList(
				transGoodsDao.getOrganTransTypeList(organId, goodsType), null));

		// 多指标信息
		ConfigDao configDao = new ConfigDao();
		List tradeRuleXMLConfig = (List) configDao.getTradeRuleXMLConfig(true);
		List quotasList = new ArrayList();
		if (tradeRuleXMLConfig != null && tradeRuleXMLConfig.size() > 0) {
			for (int i = 0; i < tradeRuleXMLConfig.size(); i++) {
				ParaMap rMap = (ParaMap) tradeRuleXMLConfig.get(i);
				String rId = rMap.getString("id");
				if (rId.startsWith("Q") && !rId.equals("Q01")) {
					rMap.put("name", rMap.getString("name").replace("指标", ""));
					quotasList.add(rMap);
				}
			}
		}
		out.put("quotasType", quotasList);

		// 交易单位
		out.put("transOrgan",
				this.changeToObjList(transGoodsDao.getAllTransOrgan(), organId));

		// 配置项
		String configStr = configDao.getBusinessType(6);
		out.put("config", ParaMap.fromString(configStr));

		return out;
	}

	/**
	 * 初始化标的修改页面标的信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap initTargetEditData(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("targetId");
		if (StringUtils.isNotEmpty(targetId)) {
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap keyData = new ParaMap();

			keyData.put("id", targetId);
			ParaMap targetMap = dataSetDao.querySimpleData("trans_target",
					keyData);
			List targetList = this.changeToObjList(targetMap, null);
			out.put("target",
					targetList != null && targetList.size() > 0 ? targetList
							.get(0) : null);
			out.putAll(this.getTransTargetGoodsList(inMap));
			keyData.clear();
			keyData.put("ref_id", targetId);
			keyData.put("ref_table_name", "trans_target");
			ParaMap extendMap = dataSetDao.querySimpleData("sys_extend",
					keyData);
			if (extendMap.getSize() > 0) {
				List filds = extendMap.getFields();
				for (int i = 0; i < extendMap.getSize(); i++) {
					ParaMap kzMap = new ParaMap();
					List record = (List) extendMap.getRecords().get(i);
					String field_no = "";
					String field_value = "";
					if (filds != null && filds.size() > 0) {
						for (int j = 0; j < filds.size(); j++) {
							kzMap.put((String) filds.get(j), record.get(j));
							if ("field_no".equals((String) filds.get(j))) {
								field_no = (String) record.get(j);
							}
							if ("field_value".equals((String) filds.get(j))) {
								field_value = (String) record.get(j);
							}
						}
					}
					out.put(field_no, field_value);
				}
			}
		}
		return out;
	}

	/**
	 * 初始化标的修改页面多指标
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap initTargetEditMultiTrade(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("targetId");
		if (StringUtils.isNotEmpty(targetId)) {
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap keyData = new ParaMap();
			keyData.put("target_id", targetId);
			ParaMap tradeMap = dataSetDao.querySimpleData(
					"trans_target_multi_trade", keyData, " turn asc");
			// 转换json格式
			// int totalRowCount = out.getInt("totalRowCount");
			List resultList = new ArrayList();
			if (tradeMap.getSize() > 0) {
				List filds = tradeMap.getFields();
				for (int i = 1; i < tradeMap.getSize(); i++) {
					ParaMap pm = new ParaMap();
					List record = (List) tradeMap.getRecords().get(i);
					if (filds != null && filds.size() > 0) {
						for (int j = 0; j < filds.size(); j++) {
							pm.put((String) filds.get(j), record.get(j));
							if ("enter_flag".equals((String) filds.get(j))) {
								String enter_flag = (String) record.get(j);
								if (StrUtils.isNotNull(enter_flag)) {
									String[] efArray = enter_flag.split("#");
									pm.put("enter_flag1",
											efArray.length > 0 ? efArray[0]
													: null);
									pm.put("enter_flag2",
											efArray.length > 1 ? efArray[1]
													: null);
									pm.put("enter_flag3",
											efArray.length > 2 ? efArray[2]
													: null);
								}

							}
							if ("turn".equals((String) filds.get(j))) {
								String turn = String.valueOf(record.get(j));
								pm.put("turn", Integer.parseInt(turn) - 1);
							}
						}
					}
					resultList.add(pm);

				}
			}
			out.clear();
			out.put("Rows", resultList);
			out.put("Total", tradeMap.getSize());
		}
		return out;
	}

	/**
	 * 初始化标的修改页面多币种
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap initTargetEditEarnestMoney(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String targetId = inMap.getString("targetId");
		if (StringUtils.isNotEmpty(targetId)) {
			DataSetDao dataSetDao = new DataSetDao();
			ParaMap keyData = new ParaMap();
			keyData.put("target_id", targetId);
			keyData.put("is_valid", 1);
			ParaMap targetEarnestMoneyMap = dataSetDao.querySimpleData(
					"trans_target_earnest_money", keyData);

			// 转换json格式
						// int totalRowCount = out.getInt("totalRowCount");
			List resultList = new ArrayList();
			if (targetEarnestMoneyMap.getSize() > 0) {
				List filds = targetEarnestMoneyMap.getFields();
				for (int i = 0; i < targetEarnestMoneyMap.getSize(); i++) {
					ParaMap pm = new ParaMap();
					List record = (List) targetEarnestMoneyMap.getRecords()
							.get(i);
					if (filds != null && filds.size() > 0) {
						for (int j = 0; j < filds.size(); j++) {
							pm.put((String) filds.get(j), record.get(j));
							if ("currency".equals((String) filds.get(j))) {
								String currency = (String) record.get(j);
								pm.put("type", currency);
							}
							
						}
					}
					
					if(pm.get("type").equals("CNY") || pm.get("type").equals("RMB")){
						float amount=(Float.parseFloat(String.valueOf(pm.get("amount"))))/10000;
						pm.put("amount", amount);
					}
					resultList.add(pm);
				}
			}
			out.clear();
			out.put("Rows", resultList);
			out.put("Total", targetEarnestMoneyMap.getSize());
		}
		return out;
	}

	/**
	 * 修改标的信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap saveTransTarget(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		String userId = inMap.getString("u");
		String targetId = inMap.getString("targetId");
		String transTypeId = inMap.getString("trans_type_id");
		String business_type = inMap.getString("business_type");
		String mode = inMap.getString("mode");

		if (StrUtils.isNull(mode)) {
			out.put("state", 0);
			out.put("targetId", targetId);
			out.put("message", "保存失败！操作类型不存在。");
			return out;
		} else {
			if (",new,modify,view,".indexOf("," + mode.trim() + ",") < 0) {
				out.put("state", 0);
				out.put("targetId", targetId);
				out.put("message", "保存失败！操作类型不存在。");
				return out;
			}
		}

		if (mode.equals("view")) {
			out.put("state", 0);
			out.put("targetId", targetId);
			out.put("message", "保存失败！操作类型不允许保存。");
			return out;
		} else if (mode.equals("modify")) {
			if (StrUtils.isNull(targetId)) {
				out.put("state", 0);
				out.put("targetId", targetId);
				out.put("message", "保存失败！标的参数错误。");
				return out;
			}
		}

		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", transTypeId);
		ParaMap transactionMap = dataSetDao.querySimpleData(
				"trans_transaction_type", keyData);
		String enter_flag = null;
		String trans_type = null;
		String is_net_trans = null;
		String is_limit_trans = null;
		String trans_type_name = null;
		String trans_type_label = null;
		if (transactionMap.getSize() > 0) {
			enter_flag = transactionMap.getRecordValue(0, "enter_flag_0") + "#"
					+ transactionMap.getRecordValue(0, "enter_flag_1") + "#"
					+ transactionMap.getRecordValue(0, "enter_flag_2");
			trans_type_name = String.valueOf(transactionMap.getRecordValue(0,
					"name"));
			trans_type_label = String.valueOf(transactionMap.getRecordValue(0,
					"name"));
			trans_type = String.valueOf(transactionMap.getRecordValue(0,
					"trans_type"));
			is_net_trans = String.valueOf(transactionMap.getRecordValue(0,
					"is_net_trans"));
			is_limit_trans = String.valueOf(transactionMap.getRecordValue(0,
					"is_limit_trans"));
		}
		keyData.clear();
		keyData.put("trans_type_id", transTypeId);
		keyData.put("business_type_rel_id", business_type);
		keyData.put("is_valid", 1);
		ParaMap transactionRelMap = dataSetDao.querySimpleData(
				"trans_transaction_type_rel", keyData);
		if (transactionRelMap.getSize() > 0) {
			trans_type_label = String.valueOf(transactionRelMap.getRecordValue(
					0, "name"));
		}
		trans_type_label = StrUtils.isNotNull(trans_type_label) ? trans_type_label
				: trans_type_name;

		String allow_live = inMap.getString("allow_live");
		String allow_union = inMap.getString("allow_union");
		String allow_trust = inMap.getString("allow_trust");
		String begin_price = inMap.getString("begin_price");
		String price_step = inMap.getString("price_step");
		String earnest_money = inMap.getString("earnest_money");
		String reserve_price = inMap.getString("reserve_price");
		String final_price = inMap.getString("final_price");
		String unit = inMap.getString("unit");
		String begin_notice_time = inMap.getString("begin_notice_time");
		String end_notice_time = inMap.getString("end_notice_time");
		String begin_apply_time = inMap.getString("begin_apply_time");
		String end_apply_time = inMap.getString("end_apply_time");
		String begin_earnest_time = inMap.getString("begin_earnest_time");
		String end_earnest_time = inMap.getString("end_earnest_time");
		String begin_list_time = inMap.getString("begin_list_time");
		String end_list_time = inMap.getString("end_list_time");
		String begin_focus_time = inMap.getString("begin_focus_time");
		String end_focus_time = inMap.getString("end_focus_time");
		String begin_limit_time = inMap.getString("begin_limit_time");
		String trans_condition = inMap.getString("trans_condition");
		String condition_organ_name = inMap.getString("condition_organ_name");
		String trans_organ_id = inMap.getString("trans_organ_id");
		String unit_price = inMap.getString("unit_price");
		String unit_price2 = inMap.getString("unit_price2");
		String building_price = inMap.getString("building_price");
		int is_after_check = inMap.getInt("is_after_check");
		String organ_id = this.getOrganId(userId);
		trans_organ_id = StrUtils.isNotNull(trans_organ_id) ? trans_organ_id : organ_id;

		//扩展信息
		String presentSituation = inMap.getString("presentSituation");//按现状出让
		String dissension = inMap.getString("dissension");//解决纠纷方案
		String questionData = inMap.getString("questionData");//答疑会召开时间
		String payMode =  inMap.getString("payMode");//付款方式
		String old_step = null;
		// 保存trans_target
		// 有标的Id说明是修改，标的状态不能为已发布(直接修改，非发布后修改)
		ParaMap targetMap = new ParaMap();

		if (StrUtils.isNotNull(targetId)) {
			ParaMap sqlParam = new ParaMap();
			sqlParam.put("id", targetId);
			ParaMap ttMap = dataSetDao
					.querySimpleData("trans_target", sqlParam);
			if (ttMap.getSize() > 0) {
				int is_stop = ttMap.getRecordInteger(0, "is_stop");
				if(is_stop == 1){
					out.put("state", 0);
					out.put("targetId", targetId);
					out.put("message", "修改标的信息失败！该标的已终止。");
					return out;
				}
				int status = Integer.parseInt(String.valueOf(ttMap
						.getRecordValue(0, "status")));
				String old_trans_organ_id = String.valueOf(ttMap
						.getRecordValue(0, "trans_organ_id"));
				old_step = String
						.valueOf(ttMap.getRecordValue(0, "price_step"));
				if (old_trans_organ_id.equals(organ_id)) {// 当前单位是发布单位
					if (status == 2) {
						ParaMap judgeNoticeMap = this.judgeModifyByNotice(
								targetId, null, old_trans_organ_id,
								old_trans_organ_id);
						if (judgeNoticeMap != null
								&& !"1".equals(judgeNoticeMap
										.getString("state"))) {
							return judgeNoticeMap;
						}

					} else if (status > updateStatus) {
						out.put("state", 0);
						out.put("targetId", targetId);
						out.put("message", "修改标的信息失败！该标的已" + updateStatusName
								+ "。");
						return out;
					}
				} else {
					if (status >= 2) {
						out.put("state", 0);
						out.put("targetId", targetId);
						out.put("message", "修改标的信息失败！该标的已审核。");
						return out;
					}
				}
			}
			targetMap.put("id", targetId);
		} else {
			targetMap.put("id", null);
			targetMap.put("no", "新建标的" + DateUtils.getStr(DateUtils.now()));
			targetMap.put("name", "新建标的" + DateUtils.getStr(DateUtils.now()));
			targetMap.put("organ_id", organ_id);
			targetMap.put("create_user_id", userId);
		}
		ParaMap formatMap = new ParaMap();
		targetMap.put("business_type", business_type);
		targetMap.put("trans_type", trans_type);
		targetMap.put("begin_price", begin_price);
		targetMap.put("earnest_money", earnest_money);
		targetMap.put("trans_condition", trans_condition);
		targetMap.put("is_net_trans", is_net_trans);
		targetMap.put("allow_live", allow_live);
		targetMap.put("allow_union", allow_union);
		targetMap.put("allow_trust", allow_trust);
		targetMap.put("is_limit_trans", is_limit_trans);
		targetMap.put("unit", unit);
		targetMap.put("final_price", final_price);
		targetMap.put("condition_organ_name", condition_organ_name);
		targetMap.put("begin_notice_time", begin_notice_time);
		targetMap.put("end_notice_time", end_notice_time);
		targetMap.put("begin_apply_time", begin_apply_time);
		targetMap.put("end_apply_time", end_apply_time);
		targetMap.put("begin_earnest_time", begin_earnest_time);
		targetMap.put("end_earnest_time", end_earnest_time);
		targetMap.put("begin_list_time", begin_list_time);
		targetMap.put("end_list_time", end_list_time);
		targetMap.put("begin_focus_time", begin_focus_time);
		targetMap.put("end_focus_time", end_focus_time);
		targetMap.put("begin_limit_time", begin_limit_time);
		targetMap.put("trans_type_name", trans_type_name);
		targetMap.put("trans_type_label", trans_type_label);
		targetMap.put("trans_type_id", transTypeId);
		targetMap.put("trans_organ_id", trans_organ_id);
		formatMap.put("begin_notice_time",
				"to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		formatMap.put("end_notice_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		formatMap
				.put("begin_apply_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		formatMap.put("end_apply_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		formatMap.put("begin_earnest_time",
				"to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		formatMap
				.put("end_earnest_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		formatMap.put("begin_list_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		formatMap.put("end_list_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		formatMap
				.put("begin_focus_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		formatMap.put("end_focus_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		formatMap
				.put("begin_limit_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");

		targetMap.put("reserve_price", reserve_price);
		targetMap.put("price_step", price_step);
		//
		targetMap.put("unit_price", unit_price);
		targetMap.put("unit_price2", unit_price2);
		targetMap.put("building_price", building_price);
		targetMap.put("is_after_check", is_after_check);
		//
		targetMap = transGoodsDao.updateTransTarget(targetMap, formatMap);
		if (StrUtils.isNull(targetId)) {
			targetId = targetMap.getString("id");
		}
		//保存target扩展信息
		if (StringUtils.isNotEmpty(targetId)) {
			ParaMap extendMap = new ParaMap();
			extendMap.put("ref_table_name", "trans_target");
			extendMap.put("ref_id", targetId);
			dataSetDao.deleteSimpleData("sys_extend", extendMap);
			transGoodsDao.updateExtend("trans_target", targetId,"presentSituation", presentSituation, "按现状出让");
			transGoodsDao.updateExtend("trans_target", targetId,"dissension", dissension, "解决纠纷方案");
			transGoodsDao.updateExtend("trans_target", targetId,"questionData", questionData, "答疑会召开时间");
			transGoodsDao.updateExtend("trans_target", targetId,"payMode", payMode, "答疑会召开时间");
		}
		if (!"1".equals(targetMap.getString("state"))) {
			out.put("state", 0);
			out.put("targetId", targetId);
			out.put("message", "保存标的信息出错！");
			return out;
		}

		// 保存保证金
		if (StrUtils.isNotNull(targetId) && !mode.equals("update")) {
			// 第一步 先删除标的所有的保证金
			transGoodsDao.deleteTargetEarnestMoney(targetId);

			// 第二步 添加多币种
			String currencyList = inMap.getString("bailsJson");
			if (currencyList != null && !"".equals(currencyList)
					&& !"null".equals(currencyList)) {
				JSONArray json = JSONArray.fromObject(currencyList);
				ParaMap earnestMoneyMap = new ParaMap();
				if (json != null && json.size() > 0) {
					for (int i = 0; i < json.size(); i++) {
						JSONObject jsonObj = json.getJSONObject(i);
						earnestMoneyMap.clear();
						earnestMoneyMap.put("id", null);
						earnestMoneyMap.put("target_id", targetId);
						earnestMoneyMap.put("currency",
								String.valueOf(jsonObj.get("type")));
						earnestMoneyMap.put("amount",
								String.valueOf(jsonObj.get("amount")));
						earnestMoneyMap.put("is_valid", 1);
						earnestMoneyMap.put("create_user_id", userId);
						transGoodsDao
								.updateTransTargetEarnestMoney(earnestMoneyMap);
					}
				}
			}
		}

		// 处理多指标
		if (StrUtils.isNotNull(targetId)) {
			// 第一步 先删除标的所有的多指标
			transGoodsDao.deleteTrandTargetMultiTrade(targetId);

			if (("1".equals(is_net_trans) || "2".equals(is_net_trans))
					&& (("0".equals(trans_type) && "1".equals(is_limit_trans)) || "1"
							.equals(trans_type))) {
				// 第二步 无论是否有没有多指标，都要给多指标表中添加一条记录，如果有多指标，价格指标不让录入
				EngineDao engineDao = new EngineDao();
				ParaMap rule = (ParaMap) engineDao.getRuleMap().get("Q01");
				
				ParaMap multiTradeMap = new ParaMap();

				multiTradeMap.put("id", null);
				multiTradeMap.put("target_id", targetId);
				multiTradeMap.put("name", rule.getString("name"));
				multiTradeMap.put("type", "Q01");
				multiTradeMap.put("unit", unit);
				multiTradeMap.put("init_value", begin_price);
				multiTradeMap.put("final_value", final_price);
				multiTradeMap.put("step", price_step);

				multiTradeMap.put("enter_flag", enter_flag);
				multiTradeMap.put("turn", 1);
				multiTradeMap = transGoodsDao
						.updateTransTargetMultiTrade(multiTradeMap);

				if (!"1".equals(multiTradeMap.getString("state"))) {
					out.put("state", 0);
					out.put("targetId", targetId);
					out.put("message", "保存标的指标信息出错！");
					return out;
				}

				// 第三步，保存除了价格指标以外的多指标
				String tradeData = inMap.getString("quotasJson");
				if (tradeData != null && !"".equals(tradeData)
						&& !"null".equals(tradeData)) {
					JSONArray json = JSONArray.fromObject(tradeData);
					if (json != null && json.size() > 0) {
						for (int i = 0; i < json.size(); i++) {
							JSONObject jsonObj = json.getJSONObject(i);
							String type = (String) jsonObj.get("type");
							if (type != null && !type.equals("Q01")) {
								multiTradeMap.clear();
								String enter_flag1 = String.valueOf(jsonObj
										.getInt("enter_flag1"));
								String enter_flag2 = String.valueOf(jsonObj
										.getInt("enter_flag2"));
								String enter_flag3 = String.valueOf(jsonObj
										.getInt("enter_flag3"));
								String trandeEnterFlag = enter_flag1 + "#"
										+ enter_flag2 + "#" + enter_flag3;
								multiTradeMap.put("id", null);
								multiTradeMap.put("target_id", targetId);
								multiTradeMap.put("name",
										(String) jsonObj.get("name"));
								multiTradeMap.put("type",
										(String) jsonObj.get("type"));
								multiTradeMap.put("unit", jsonObj.get("unit"));
								multiTradeMap.put("init_value",
										jsonObj.get("init_value"));
								multiTradeMap.put("final_value",
										jsonObj.get("final_value"));
								multiTradeMap.put("step", jsonObj.get("step"));
								multiTradeMap
										.put("enter_flag", trandeEnterFlag);
								multiTradeMap.put("turn", Integer
										.parseInt(String.valueOf(jsonObj
												.get("turn"))) + 1);
								multiTradeMap.put("first_wait",
										jsonObj.get("first_wait"));
								multiTradeMap.put("limit_wait",
										jsonObj.get("limit_wait"));
								multiTradeMap.put("last_wait",
										jsonObj.get("last_wait"));
								transGoodsDao
										.updateTransTargetMultiTrade(multiTradeMap);
							}
						}
					}
				}
			} else {
				String tradeData = inMap.getString("quotasJson");
				if (tradeData != null && !"".equals(tradeData)
						&& !"null".equals(tradeData)) {
					JSONArray json = JSONArray.fromObject(tradeData);
					if (json != null && json.size() > 0) {
						out.put("state", 1);
						out.put("targetId", targetId);
						out.put("message", "标的保存成功！该交易方式不会进入网上限时竞价，多指标信息不保存。");
						return out;
					}
				}
			}
		}
		// 如果是公告后修改需要修改流程信息
		if (mode.equals("update")) {
			// 目前只修改增价幅度
			if (old_step != null && !old_step.equals(price_step)) {
				transGoodsDao.updateProcessInfo(targetId, price_step);
			}
		}
		out.put("state", 1);
		out.put("targetId", targetId);
		out.put("message", "保存成功");
		return out;
	}
	/**
	 * 修改已公告的标的信息
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ParaMap updateBidingTarget(ParaMap inMap) throws Exception {

		ParaMap out = new ParaMap();
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		DataSetDao dataSetDao = new DataSetDao();
		String targetId = inMap.getString("targetId");
		String mode = inMap.getString("mode");

		if (StrUtils.isNull(mode) || !mode.equals("update")) {
			out.put("state", 0);
			out.put("targetId", targetId);
			out.put("message", "保存失败！操作类型不存在。");
			return out;
		}
		String price_step = inMap.getString("price_step");
		String reserve_price = inMap.getString("reserve_price");
		String old_step = null;

		ParaMap targetMap = new ParaMap();
		if (StrUtils.isNotNull(targetId)) {
			ParaMap sqlParam = new ParaMap();
			sqlParam.put("id", targetId);
			ParaMap ttMap = dataSetDao
					.querySimpleData("trans_target", sqlParam);
			if (ttMap.getSize() > 0) {
				old_step = String
						.valueOf(ttMap.getRecordValue(0, "price_step"));
				int oldStatus = ttMap.getRecordInteger(0, "status");
				if (oldStatus > 4) {
					out.put("state", 0);
					out.put("targetId", targetId);
					out.put("message", "保存失败！标的已交易结束。");
					return out;
				}
			}
			targetMap.put("id", targetId);
		} else {
			out.put("state", 0);
			out.put("targetId", targetId);
			out.put("message", "保存失败！未找到有效标的。");
			return out;
		}
		ParaMap formatMap = new ParaMap();
		targetMap.put("reserve_price", reserve_price);
		targetMap.put("price_step", price_step);
		targetMap = transGoodsDao.updateTransTarget(targetMap, formatMap);

		if (StrUtils.isNull(targetId)) {
			targetId = targetMap.getString("id");
		}
		if (!"1".equals(targetMap.getString("state"))) {
			out.put("state", 0);
			out.put("targetId", targetId);
			out.put("message", "保存标的信息出错！");
			return out;
		}

		if (old_step != null && !old_step.equals(price_step)) {
			ParaMap multiTradeMap = new ParaMap();
			multiTradeMap.put("id", null);
			multiTradeMap.put("target_id", targetId);
			multiTradeMap.put("name", "价格指标");
			multiTradeMap.put("type", "Q01");
			multiTradeMap.put("step", price_step);
			multiTradeMap = dataSetDao.updateData("trans_target_multi_trade",
					"id", "target_id,name,type", multiTradeMap);

			transGoodsDao.updateProcessInfo(targetId, price_step);
		}
		out.put("state", 1);
		out.put("targetId", targetId);
		out.put("message", "保存成功");
		return out;
	}

	/**
	 * 捆绑宗地
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ParaMap doBoundGoods(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		String targetId = in.getString("targetId");
		String goodsIds = in.getString("goodsIds");
		String userId = in.getString("u");
		ParaMap judgeNoticeMap = this.judgeModifyByNotice(targetId, goodsIds,
				null, null);
		if (judgeNoticeMap != null
				&& !"1".equals(judgeNoticeMap.getString("state"))) {
			throw new Exception(judgeNoticeMap.getString("message"));
		}

		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", targetId);
		ParaMap ttMap = dataSetDao.querySimpleData("trans_target", keyData);
		if (ttMap.getSize() > 0) {
			int status = Integer.parseInt(String.valueOf(ttMap.getRecordValue(
					0, "status")));
			if (status > updateStatus) {
				throw new Exception("捆绑失败！标的已" + updateStatusName + "。");
			}
		}

		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		if (StrUtils.isNotNull(targetId) && StrUtils.isNotNull(goodsIds)) {
			String[] str = goodsIds.split(",");
			if (str.length > 0) {
				for (int i = 0; i < str.length; i++) {
					if (StrUtils.isNull(str[i])) {
						continue;
					}
					String goodsId = str[i];
					ParaMap relMap = transGoodsDao.getGoodsTargetList(goodsId);
					String status = "";
					String isStop = "";
					if (relMap.getSize() > 0) {
						status = String.valueOf(relMap.getRecordValue(0,
								"status"));
						isStop = String.valueOf(relMap.getRecordValue(0,
								"is_stop"));
					}
					// 没有关联或者是关联的最后的一个 标的为已流拍
					if (relMap.getSize() <= 0 || status == null
							|| "".equals(status) || "6".equals(status)
							|| "1".equals(isStop)) {
						keyData.clear();
						keyData.put("id", null);
						keyData.put("target_id", targetId);
						keyData.put("goods_id", str[i]);
						keyData.put("create_user_id", userId);
						keyData.put("turn", transGoodsDao
								.getNewTargetGoodsRelTurn(targetId));
						ParaMap result = transGoodsDao
								.updateTransTargetGoodsRel(keyData);
						if (!"1".equals(result.getString("state"))) {
							throw new Exception("交易物捆绑失败！");
						} else {
							out.put("state", 1);
							out.put("message", "交易物捆绑成功！");
						}
					} else {
						throw new Exception("交易物捆绑失败！交易物已捆绑其他标的。");
					}
				}
			}
			// 修改标的名称
			transGoodsDao.updateTargetNoNameByTargetId(targetId);
			
			//改为已审核状态
			ParaMap checkMap=new ParaMap();
			checkMap.put("targetIds", targetId);
			doCheckTransTarget(checkMap);
			out.put("state", 1);
			out.put("message", "交易物捆绑成功！");
		} else {
			out.put("state", 0);
			out.put("message", "交易物捆绑失败！参数错误。");
		}
		return out;
	}
	/**
	 * 解除捆绑
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ParaMap doUnBoundGoods(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		String targetId = in.getString("targetId");
		String goodsIds = in.getString("goodsIds");

		ParaMap judgeNoticeMap = this.judgeModifyByNotice(targetId, goodsIds,
				null, null);
		if (judgeNoticeMap != null
				&& !"1".equals(judgeNoticeMap.getString("state"))) {
			return judgeNoticeMap;
		}

		DataSetDao dataSetDao = new DataSetDao();
		ParaMap keyData = new ParaMap();
		keyData.put("id", targetId);
		ParaMap ttMap = dataSetDao.querySimpleData("trans_target", keyData);
		if (ttMap.getSize() > 0) {
			int status = Integer.parseInt(String.valueOf(ttMap.getRecordValue(
					0, "status")));
			if (status > updateStatus) {
				out.put("state", 0);
				out.put("targetId", targetId);
				out.put("message", "解除捆绑失败！标的已" + updateStatusName + "。");
				return out;
			}
		}

		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		if (StrUtils.isNotNull(targetId) && StrUtils.isNotNull(goodsIds)) {
			String[] str = goodsIds.split(",");
			if (str.length > 0) {
				for (int i = 0; i < str.length; i++) {
					if (StrUtils.isNull(str[i])) {
						continue;
					}
					ParaMap result = transGoodsDao.deleteTransTargetGoodsRel(
							targetId, str[i]);
					if (!"1".equals(result.getString("state"))) {
						throw new Exception("解除捆绑失败！");
					}
				}
			}
			ParaMap relMap = transGoodsDao.queryTransTargetGoodsRel(targetId,
					null);
			if (relMap.getSize() > 0) {
				// 修改标的名称
				transGoodsDao.updateTargetNoNameByTargetId(targetId);
			} else {
				keyData.clear();
				keyData.put("id", targetId);
				keyData.put("no", "新建标的" + DateUtils.getStr(DateUtils.now()));
				keyData.put("name", "新建标的" + DateUtils.getStr(DateUtils.now()));
				dataSetDao.updateData("trans_target", "id", keyData);
				;
				// //删除标的信息
				// ParaMap result = transGoodsDao.deleteTransTarget(targetId);
				// if(!"1".equals(result.getString("state"))){
				// out.put("state", 0);
				// out.put("message", "解除捆绑失败！");
				// throw new Exception();
				// }
			}
			out.put("state", 1);
			out.put("message", "解除捆绑成功！");

		} else {
			out.put("state", 0);
			out.put("message", "解除捆绑失败！");
		}
		return out;
	}
	/**
	 * 删除宗地信息
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ParaMap deleteTransGoodses(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		String goodsIds = in.getString("goodsIds");
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		if (StrUtils.isNull(goodsIds)) {
			out.put("state", 0);
			out.put("message", "删除交易物失败！请选择有效的交易物。");
			return out;
		}
		String[] goodsIdArray = goodsIds.split(",");
		if (goodsIdArray != null && goodsIdArray.length > 0) {
			for (int i = 0; i < goodsIdArray.length; i++) {
				String goodsId = goodsIdArray[i];
				if (StrUtils.isNull(goodsId)) {
					continue;
				}
				ParaMap gtrMap = transGoodsDao.getGoodsTargetList(goodsId);
				if (gtrMap.getSize() > 0) {
					String goodsNo = String.valueOf(gtrMap.getRecordValue(0,
							"no"));
					out.put("state", 0);
					out.put("message", "删除交易物失败！ 交易物'" + goodsNo + "'已经有交易信息。");
					throw new Exception("删除交易物失败！ 交易物'" + goodsNo + "'已经有交易信息。");
				}
				if (gtrMap.getSize() > 0) {
					String statusStr = String.valueOf(gtrMap.getRecordValue(0,
							"status"));
					int status = Integer
							.parseInt(StrUtils.isNotNull(statusStr) ? statusStr
									: "0");
					if (status > updateStatus) {
						String goodsNo = String.valueOf(gtrMap.getRecordValue(
								0, "no"));
						throw new Exception("删除交易物失败！交易物'" + goodsNo
								+ "'关联的标的已" + updateStatusName + "。");
					}
				}
				ParaMap result = transGoodsDao.deleteTransGoods(goodsId);
				if (!"1".equals(result.getString("state"))) {
					throw new Exception("删除交易物失败！");
				}
			}
			out.put("state", 1);
			out.put("message", "删除交易物成功！");
		} else {
			out.put("state", 0);
			out.put("message", "删除交易物失败！请选择有效的交易物。");
		}
		return out;
	}

	/**
	 * 删除标的信息
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ParaMap deleteTransTargets(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		String targetIds = in.getString("targetIds");
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		ParaMap keyData = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		if (StrUtils.isNull(targetIds)) {
			out.put("state", 0);
			out.put("message", "删除标的失败！请选择有效的标的。");
			return out;
		}
		String[] targetIdArray = targetIds.split(",");
		if (targetIdArray != null && targetIdArray.length > 0) {
			for (int i = 0; i < targetIdArray.length; i++) {
				String targetId = targetIdArray[i];
				if (StrUtils.isNull(targetId)) {
					continue;
				}
				keyData.clear();
				keyData.put("id", targetId);
				ParaMap targetMap = dataSetDao.querySimpleData("trans_target",
						keyData);
				if (targetMap.getSize() > 0) {
					int status = Integer.parseInt(String.valueOf(targetMap
							.getRecordValue(0, "status")));
					if (status > 0) {
						throw new Exception("标的删除失败！标的'"
								+ String.valueOf(targetMap.getRecordValue(0,
										"no")) + "'已提交。");
					} else {
						ParaMap result = transGoodsDao
								.deleteTransTarget(targetId);
						if (!"1".equals(result.getString("state"))) {
							throw new Exception("标的删除失败！");
						}
					}
				}
			}
			out.put("state", 1);
			out.put("message", "标的删除 成功！");
		} else {
			out.put("state", 0);
			out.put("message", "删除标的失败！请选择有效的标的。");
		}
		return out;
	}
	/**
	 * 标的审核
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap doCheckTransTarget(ParaMap inMap) throws Exception {
		String[] statusArray = new String[7];
		statusArray[0] = "受理中";
		statusArray[1] = "审核中";
		statusArray[2] = "已审核";
		statusArray[3] = "已公告";
		statusArray[4] = "交易中";
		statusArray[5] = "已成交";
		statusArray[6] = "未成交(流拍)";
		DataSetDao dataSetDao = new DataSetDao();
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		ParaMap out = new ParaMap();
		String targetIds = inMap.getString("targetIds");
		if (StrUtils.isNull(targetIds)) {
			out.put("state", 0);
			out.put("message", "提交失败！请选择有效的标的。");
			return out;
		}
		String[] str = targetIds.split(",");
		if (str != null && str.length > 0) {
			for (int i = 0; i < str.length; i++) {
				String targetId = str[i];
				if (StrUtils.isNull(targetId)) {
					continue;
				}
				ParaMap keyData = new ParaMap();
				keyData.put("id", targetId);
				ParaMap targetMap = dataSetDao.querySimpleData("trans_target",
						keyData);
				if (targetMap == null || targetMap.getSize() <= 0) {
					continue;
				}
				String targetNo = String.valueOf(targetMap.getRecordValue(0,
						"no"));
				String statusStr = String.valueOf(targetMap.getRecordValue(0,
						"status"));
				int status = Integer.parseInt(statusStr);
				if (status > 2) {
					throw new Exception("提交失败！ 标的'" + targetNo + "'状态错误。");
				}
				ParaMap goodsMap = transGoodsDao.getTransTargetGoodsList(
						targetId, null);
				if (goodsMap == null || goodsMap.getSize() <= 0) {
					throw new Exception("提交失败！标的'" + targetNo + "'没有捆绑矿区。");
				}
				keyData.clear();
				keyData.put("id", targetId);
				keyData.put("status", 2);
				targetMap = transGoodsDao.updateTransTarget(keyData, null);
				if (!"1".equals(targetMap.getString("state"))) {
					throw new Exception("提交失败！标的'" + targetNo + "'审核失败。");
				}
			}
			out.put("state", 1);
			out.put("message", "提交成功！");
		} else {
			out.put("state", 0);
			out.put("message", "提交失败！请选择有效的标的。");
		}

		return out;
	}

	/**
	 * 初始化交易物查询修改页面下拉列表框
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap initGoodsListQueryParam(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String organId = getOrganId(inMap.getString("u"));
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		out.put("goodsType",
				this.changeToObjList(
						transGoodsDao.getOrganGoodsTypeList(organId), null));
		out.put("user",
				this.changeToObjList(
						transGoodsDao.getOrganCreateUserList(organId), null));
//		out.put("canton", this.changeToObjList(
//				transGoodsDao.getOrganCantonList(organId), null));
		return out;
	}
	
	public List changeToObjList(ParaMap in, String firstId) throws Exception {
		List resultList = new ArrayList();
		boolean isHaveFist = true;
		if (StrUtils.isNotNull(firstId)) {
			ParaMap fMap = new ParaMap();
			fMap.put("id", firstId);
			resultList.add(fMap);
			isHaveFist = false;
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
					isHaveFist = true;
				} else {
					resultList.add(out);
				}
			}
			if (!isHaveFist) {
				resultList.remove(0);
			}
		}
		return resultList;
	}

	
	public ParaMap judgeModifyByNotice(String targetId, String goodsId,
			String old_trans_organ_id, String trans_organ_id) throws Exception {
		ParaMap out = null;
		TransNoticeMineDao noticeDao = new TransNoticeMineDao();
		ParaMap noticeMap = noticeDao.getNoticeByTargetId(targetId, "0");
		if (noticeMap.getSize() > 0) {
			if (StrUtils.isNotNull(old_trans_organ_id)
					&& StrUtils.isNotNull(trans_organ_id)
					&& !old_trans_organ_id.equals(trans_organ_id)) {
				out = new ParaMap();
				out.put("state", 0);
				out.put("targetId", targetId);
				out.put("goodsId", goodsId);
				out.put("message", "操作失败！该标的已经关联公告，不能修改交易单位。");
				return out;
			}
			int noticeStatus = Integer.parseInt(String.valueOf(noticeMap
					.getRecordValue(0, "status")));
			if (noticeStatus !=0 && noticeStatus !=4 ) {
				out = new ParaMap();
				out.put("state", 0);
				out.put("targetId", targetId);
				out.put("goodsId", goodsId);
				String stext="";
				if(noticeStatus==2){
					stext="已发布";
				}else if(noticeStatus==3){
					stext="正在审核中";
				}else if(noticeStatus==5){
					stext="已审核通过，不能修改";
				}
				out.put("message", "操作失败！该标的所属的公告"+stext+"。");
				return out;
			}
		}
		return out;
	}
	
	public ParaMap getGoodsUseListData(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		List use = new ArrayList();
		ParamsDao paramsDao = new ParamsDao();
		use=MakeJSONData.makeItems(paramsDao.getParamsRecurseDataByParentNo(ConfigDao.root_kz));
		out.put("goodsuse",use);
		return out;
	}
}
