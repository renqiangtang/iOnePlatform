package com.before.service;


import java.util.ArrayList;
import java.util.List;

import com.trademine.engine.EngineDao;
import com.base.dao.DataSetDao;
import com.base.service.BaseService;
import com.base.utils.MakeJSONData;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.before.dao.TransGoodsMineDao;
import com.before.dao.TransNoticeMineDao;
import com.trademan.dao.BankManageDao;

/**
 * 交易前土地管理Service
 * 
 * @author chenl 2012-05-30
 */
public class TransModifyMineService extends BaseService {
	
	/**
	 * 已发布未结束的公告标的宗地列表
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ParaMap getDatas(ParaMap in)throws Exception{
		String userId = in.getString("u");
		String goodsType = in.getString("goodsType");
		String organId = this.getOrganId(userId);
		ParaMap noticesMap = this.transNotices(organId,goodsType);
		List noticeList = new ArrayList();
		if(noticesMap!=null && noticesMap.getSize()>0){
			for(int i = 0 ; i < noticesMap.getSize() ; i ++ ){
				ParaMap notice = this.getNoticeMap(i, noticesMap);
				ParaMap targetsMap = this.noticeTargets(notice.getString("id"));
				List targetList = new ArrayList();
				if(targetsMap!=null && targetsMap.getSize()>0){
					for(int j = 0 ; j <targetsMap.getSize() ; j ++ ){
						ParaMap target = this.getTargetMap(j, targetsMap);
						ParaMap goodsMap = this.targetGoods(target.getString("id"));
						List goodsList = new ArrayList();
						if(goodsMap != null && goodsMap.getSize()>0){
							for(int k = 0 ; k < goodsMap.getSize() ; k++ ){
								ParaMap goods = this.getGoodsMap(k, goodsMap);
								goodsList.add(goods);
							}
						}
						if(goodsList!=null && goodsList.size()>0){
							target.put("children", goodsList);
						}
						targetList.add(target);
					}
				}
				if(targetList != null && targetList.size()>0){
					notice.put("children", targetList);
					noticeList.add(notice);
				}
			}
		}
		ParaMap out = new ParaMap();
		out.put("Rows", noticeList);
		return out;
	}
	
	/**
	 * 公告列表
	 * @param organId
	 * @return
	 * @throws Exception
	 */
	public ParaMap transNotices(String organId , String goodsType)throws Exception{
		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
		ParaMap out = transNoticeDao.transNormalPublishNoticeList(organId,goodsType);
		return out;
	}
	
	/**
	 * 公告下标的列表
	 * @param noticeId
	 * @return
	 * @throws Exception
	 */
	public ParaMap noticeTargets(String noticeId)throws Exception{
		TransNoticeMineDao transNoticeDao = new TransNoticeMineDao();
		ParaMap out = transNoticeDao.getNoticeTargetData(noticeId," and tt.status>2 and tt.status < 5 ");
		return out;
	}
	
	/**
	 * 标的下宗地列表
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public ParaMap targetGoods(String targetId)throws Exception{
		TransGoodsMineDao transGoodsDao = new TransGoodsMineDao();
		ParaMap out = transGoodsDao.getTransTargetGoodsList(targetId , null);
		return out;
	}
	
	/**
	 * 得到公告类型
	 * @param noticeType
	 * @return
	 * @throws Exception
	 */
	public String getNoticeTypeName(int noticeType)throws Exception{
		String result = String.valueOf(noticeType);
		switch (noticeType){
		case 0 :
			result = "普通公告";
			break;
		case 1 :
			result = "补充公告";
			break;
		case 3 :
			result = "紧急公告";
			break;
		}
		return result;
	}
	
	/**
	 * 把公告信息放入对象
	 * @param index
	 * @param noticesMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getNoticeMap(int index , ParaMap noticesMap)throws Exception{
		ParaMap notice = new ParaMap();
		String noticeId = noticesMap.getRecordString(index, "id");
		String name = noticesMap.getRecordString(index, "name");
		String no =  noticesMap.getRecordString(index, "no");
		String busienssName = noticesMap.getRecordString(index, "business_type_name");
		int noticeType = noticesMap.getRecordInteger(index, "notice_type");
		String noticeTypeName = getNoticeTypeName(noticeType);
		int licenseNum = noticesMap.getRecordInteger(index, "license_num");
		String goodsType = noticesMap.getRecordString(index, "goods_type");
		notice.put("id", noticeId);
		notice.put("infoType", "公告");
		notice.put("goodsType", goodsType);
		notice.put("name", name+"("+no+")");
		notice.put("type", noticeTypeName+"("+busienssName+")");
		notice.put("operate", licenseNum==0?0:1);
		notice.put("status", 2);
		notice.put("is_stop", 0);
		notice.put("is_suspend", 0);
		return notice;
	}
	
	/**
	 * 把标的信息放入对象
	 * @param index
	 * @param targetsMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getTargetMap(int index , ParaMap targetsMap)throws Exception{
		ParaMap target = new ParaMap();
		String targetId = targetsMap.getRecordString(index, "id");
		String no =  targetsMap.getRecordString(index, "no");
		String busienssName = targetsMap.getRecordString(index, "business_name");
		String transTypeName = targetsMap.getRecordString(index, "trans_type_label");
		String goodsType = targetsMap.getRecordString(index, "goods_type");
		int status = targetsMap.getRecordInteger(index, "status");
		int is_stop = targetsMap.getRecordInteger(index, "is_stop");
		int is_suspend = targetsMap.getRecordInteger(index, "is_suspend");
		target.put("id", targetId);
		target.put("infoType", "标的");
		target.put("goodsType", goodsType);
		target.put("name", no);
		target.put("type", transTypeName+"("+busienssName+")");
		target.put("operate",1);
		target.put("status", status);
		target.put("is_stop", is_stop);
		target.put("is_suspend", is_suspend);
		return target;
	}
	
	/**
	 * 把宗地信息放入对象
	 * @param index
	 * @param goodsMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getGoodsMap(int index , ParaMap goodsMap)throws Exception{
		ParaMap goods = new ParaMap();
		String goodsId = goodsMap.getRecordString(index, "id");
		String no =  goodsMap.getRecordString(index, "no");
		String cantonName = goodsMap.getRecordString(index, "canton_name");
		String goodsTypeName = goodsMap.getRecordString(index, "goods_type_name");
		String goodsTypeGoodsName = goodsMap.getRecordString(index, "goods_type_goods_name");
		String goodsType = goodsMap.getRecordString(index, "goods_type");
		goods.put("id", goodsId);
		goods.put("infoType", goodsTypeGoodsName);
		goods.put("goodsType", goodsType);
		goods.put("name", no);
		goods.put("type", cantonName+"("+goodsTypeName+")");
		goods.put("operate",1);
		goods.put("status", 1);
		goods.put("is_stop", 0);
		goods.put("is_suspend", 0);
		return goods;
	}
	
	/**
	 * 撤销公告
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ParaMap cancelNotice(ParaMap in)throws Exception{
		ParaMap out = new ParaMap();
		String noticeId = in.getString("noticeId");
		if(StrUtils.isNull(noticeId)){
			out.put("state", 0);
			out.put("message", "撤销公告失败！参数错误。");
			return out;
		}
		
		TransNoticeMineDao noticeDao = new TransNoticeMineDao();
		EngineDao engineDao = new EngineDao();
		TransGoodsMineDao goodsDao = new TransGoodsMineDao();
		DataSetDao dataSetDao = new DataSetDao();
		
		ParaMap noticeMap = noticeDao.getNoticeInfo(noticeId);
		if(noticeMap!=null && noticeMap.getSize()>0){
			int noticeType = noticeMap.getRecordInteger(0, "notice_type");
			if(noticeType==0){//如果是普通公告需要删除流程信息和修改标的状态，其他公告不需要
				//校验是否已经有了竞买申请了
				boolean isHaveLicense = noticeDao.isNoticeHaveLicense(noticeId);
				if(isHaveLicense){
					out.put("state", 0);
					out.put("message", "撤销公告失败！该公告下的标的已经有竞买人申请了。");
					return out;
				}
				//得到公告下所有的标的
				ParaMap targetsMap = noticeDao.getNoticeTargetData(noticeId,null,null);
				//循环删除标的的流程信息，修改标的状态
				if(targetsMap!=null && targetsMap.getSize()>0){
					for(int i = 0 ; i < targetsMap.getSize() ; i ++ ){
						String targetId = targetsMap.getRecordString(i, "id");
						//删除流程信息
						engineDao.deleteTemplateProcess(targetId);
						//修改标的状态
						ParaMap target = new ParaMap();
						target.put("id", targetId);
						target.put("status", 2);
						goodsDao.updateTransTarget(target, null);
						//删除购物车
						ParaMap keyData = new ParaMap();
						keyData.put("target_id", targetId);
						dataSetDao.deleteSimpleData("trans_cart_dtl", keyData);
					}
				}
			}
			//修改公告状态
			ParaMap notice = new ParaMap();
			notice.put("id", noticeId);
			notice.put("status", 0);
			noticeDao.updateTransNotice(notice, null);
		}else{
			out.put("state", 0);
			out.put("message", "撤销公告失败！未找到有效的公告。");
			return out;
		}
		
		out.put("state", 1);
		out.put("message", "撤销公告成功！");
		return out;
		
	}
	
	
	
	public String getOrganId(String userId) throws Exception {
		String organId = null;
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "sys_user_manager");
		sqlParams.put("dataSetNo", "getUserOrganInfo");
		sqlParams.put("id", userId);
		ParaMap result = dataSetDao.queryData(sqlParams);
		if(result.getSize()>0){
			List filds = result.getFields();
			List record = (List)result.getRecords().get(0);
			if(filds != null && filds.size()>0 ){
				for(int i = 0 ; i < filds.size() ; i ++ ){
					if(filds.get(i).equals("id")){
						organId = (String)record.get(i);
						break;
					}
				}
			}
		}
		return organId;
	}
	
	
	
	

}
