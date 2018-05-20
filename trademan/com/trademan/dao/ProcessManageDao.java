package com.trademan.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.trade.utils.Constants;
import com.tradeland.engine.EngineDao;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.before.dao.TransGoodsLandDao;


public class ProcessManageDao extends BaseDao {
	private static String bankRoleId = null;
	
	public ParaMap processList(ParaMap sqlParams)throws Exception{
		sqlParams.put("moduleNo", "target_process_manage");
		sqlParams.put("dataSetNo", "get_target_process_list");
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		return result;
	}
	
	public List getPhaseTimesList(String templateId)throws Exception{
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("moduleNo", "target_process_manage");
		sqlParams.put("dataSetNo", "get_wf_node_times");
		sqlParams.put("templateId", templateId );
		sqlParams.put("beginNode", Constants.NodeStart);
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap result = dataSetDao.queryData(sqlParams);
		List list = new ArrayList();
		if(result.getSize()>0){
			for(int i = 0 ; i < result.getSize() ; i ++ ){
				String nodeId = String.valueOf(result.getRecordValue(i, "id"));
				String name = String.valueOf(result.getRecordValue(i, "name"));
				String ptype = String.valueOf(result.getRecordValue(i, "ptype"));
				String qtype = String.valueOf(result.getRecordValue(i, "qtype"));
				String beginTime = String.valueOf(result.getRecordValue(i, "begintime"));
				String endTime = String.valueOf(result.getRecordValue(i, "endtime"));
				if(!ptype.equals(Constants.NodeStart)&&!ptype.equals(Constants.NodeEnd)){
					ParaMap temp = new ParaMap();
					temp.put("nodeId", nodeId);
					temp.put("name", name);
					temp.put("ptype", ptype);
					temp.put("qtype", qtype);
					temp.put("beginTime", beginTime);
					temp.put("endTime", endTime);
					list.add(temp);
				}
			}
		}
		return list;
	}
	
	public ParaMap stopProcess(String processId)throws Exception{
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", processId);
		ParaMap processMap = dataSetDao.querySimpleData("wf_process", sqlParams);
		if(processMap!=null && processMap.getSize()>0){
			String templateId = String.valueOf(processMap.getRecordValue(0, "templateid"));
			String tasknodeId = String.valueOf(processMap.getRecordValue(0, "tasknodeid"));
			String state = String.valueOf(processMap.getRecordValue(0, "state"));
			if( "1".equals(state) ){
				//修改标的信息
				sqlParams.clear();
				sqlParams.put("id", templateId);
				sqlParams.put("is_suspend", 1);
				out = dataSetDao.updateData("trans_target", "id", sqlParams, null);
				if(!"1".equals(out.getString("state"))){
					throw new Exception("操作失败！修改标的信息失败。");
				}
				//修改流程信息
				sqlParams.clear();
				sqlParams.put("id", processId);
				sqlParams.put("state", Constants.ProcessStop);
				out = dataSetDao.updateData("wf_process", "id", sqlParams, null);
				if(!"1".equals(out.getString("state"))){
					throw new Exception("操作失败！修改流程信息失败。");
				}
				//保存停止时间
				sqlParams.clear();
				sqlParams.put("id", null);
				sqlParams.put("refid", tasknodeId);
				sqlParams.put("name", Constants.pstopTime);
				sqlParams.put("value", DateUtils.getStr(DateUtils.now()));
				ParaMap result=dataSetDao.updateData("wf_parameter", "id","refid,name", sqlParams, null);
			}else{
				throw new Exception("操作失败！流程状态错误。");
			}
		}else{
			throw new Exception("操作失败！请选择有效的标的流程。");
		}
		return out;
	}
	
	public ParaMap restartProcess(ParaMap in)throws Exception{
		ParaMap out = new ParaMap();
		String targetId = in.getString("id");
		String setType = in.getString("setType");
		if(StrUtils.isNull(targetId) || StrUtils.isNull(setType)){
			throw new Exception("操作失败！参数错误。");
		}
		if(setType.equals("1")){
			String delayDay = in.getString("delayDay");
			try{
				int delay = Integer.parseInt(delayDay);
				if(delay<0){
					throw new Exception("操作失败！推迟天数不能为负数。");
				}
			}catch(Exception ex){
				throw new Exception("操作失败！推迟天数必须为整数。");
			}
		}
		
		//得到标的信息
		TransGoodsLandDao goodsDao = new TransGoodsLandDao();
		ParaMap targetMap = goodsDao.getTargetInfo(targetId);
		if(targetMap==null || targetMap.getSize()<1){
			throw new Exception("操作失败！未找到有效标的。");
		}
		//得到当前上下文信息
		EngineDao engineDao = new EngineDao();
		String processId = engineDao.getProcessIdByTargetId(targetId);
		if(StrUtils.isNull(processId)){
			throw new Exception("操作失败！未找到有效流程。");
		}
		ParaMap tasknodeMap = engineDao.curTaskNodeInfo(processId);
		String tasknodeId = tasknodeMap.getString("tasknodeid");
		
		if(setType.equals("1")){//推迟时间
			out = restartProcessByDelay(in,targetMap,tasknodeMap);
		}else if(setType.equals("2")){//设置时间
			out = restartProcessByTimes(in,targetMap,tasknodeMap);
		}else{
			throw new Exception("操作失败！参数错误。");
		}
		if(!"1".equals(out.getString("state"))){
			throw new Exception(StrUtils.isNull(out.getString("message"))?"操作失败！修改时间失败。":out.getString("message"));
		}
		
		//启动
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		//修改标的中止位
		sqlParams.put("id", targetId);
		sqlParams.put("is_suspend",0);
		out = dataSetDao.updateData("trans_target", "id", sqlParams, null);
		if(!"1".equals(out.getString("state"))){
			throw new Exception("操作失败！修改标的信息失败。");
		}
		//修改流程状态
		sqlParams.clear();
		sqlParams.put("id", processId);
		sqlParams.put("state", Constants.ProcessRunning);
		out = dataSetDao.updateData("wf_process", "id", sqlParams, null);
		if(!"1".equals(out.getString("state"))){
			throw new Exception("操作失败！修改流程信息失败。");
		}
		//写入启动时间
		sqlParams.clear();
		sqlParams.put("id", null);
		sqlParams.put("refid", tasknodeId);
		sqlParams.put("name", Constants.pRestartTime);
		sqlParams.put("value", DateUtils.getStr(DateUtils.now()));
		dataSetDao.updateData("wf_parameter", "id","refid,name", sqlParams, null);
		out.put("state", 1);
		return out;
	}
	
	
	public ParaMap restartProcessByDelay(ParaMap in , ParaMap target , ParaMap nodeTask)throws Exception{
		ParaMap out = new ParaMap();
		String targetId = in.getString("id");
		String delayDay = in.getString("delayDay");
		int dalay = Integer.parseInt(delayDay);
		
		String nodeId = nodeTask.getString("nodeid");
		String tasknodeId = nodeTask.getString("tasknodeid");
		String ptype = nodeTask.getString("ptype");
		String qtype = nodeTask.getString("qtype");
		String endPhaseTime = nodeTask.getString(Constants.PendTime);
		
		int trans_type = Integer.parseInt(String.valueOf(target.getRecordValue(0, "trans_type")));
		Date begin_notice_time = getTargetDate(target , "begin_notice_time");
		Date end_notice_time = getTargetDate(target , "end_notice_time");
		Date begin_apply_time = getTargetDate(target , "begin_apply_time");
		Date end_apply_time = getTargetDate(target , "end_apply_time");
		Date begin_earnest_time = getTargetDate(target , "begin_earnest_time");
		Date end_earnest_time = getTargetDate(target , "end_earnest_time");
		Date begin_list_time = getTargetDate(target , "begin_list_time");
		Date end_list_time = getTargetDate(target , "end_list_time");
		Date begin_focus_time = getTargetDate(target , "begin_focus_time");
		Date end_focus_time = getTargetDate(target , "end_focus_time");
		Date begin_limit_time = getTargetDate(target , "begin_limit_time");
		if(ptype.equals(Constants.LNotice)){
			end_notice_time = dateAddDelay(end_notice_time , dalay);
			end_apply_time = dateAddDelay(end_apply_time , dalay);
			end_earnest_time = dateAddDelay(end_earnest_time , dalay);
			end_list_time = dateAddDelay(end_list_time , dalay);
			begin_focus_time = dateAddDelay(begin_focus_time , dalay);
			end_focus_time = dateAddDelay(end_focus_time , dalay);
			begin_limit_time = dateAddDelay(begin_limit_time , dalay);
			if(trans_type == 0){//挂牌
				if(begin_focus_time.getTime()<=(DateUtils.now()).getTime()){
					out.put("state", 0);
					out.put("message", "操作失败！当前时间已经超过了集中报价开始时间。");
					return out;
				}
			}else if(trans_type == 1){//拍卖
				if(begin_limit_time.getTime()<=(DateUtils.now()).getTime()){
					out.put("state", 0);
					out.put("message", "操作失败！当前时间已经超过了限时竞拍开始时间。");
					return out;
				}
			}
		}else if(ptype.equals(Constants.LFocus)){
			end_apply_time = dateAddDelay(end_apply_time , dalay);
			end_earnest_time = dateAddDelay(end_earnest_time , dalay);
			end_list_time = dateAddDelay(end_list_time , dalay);
			end_focus_time = dateAddDelay(end_focus_time , dalay);
			begin_limit_time = dateAddDelay(begin_limit_time , dalay);
			if(begin_limit_time.getTime()<=(DateUtils.now()).getTime()){
				out.put("state", 0);
				out.put("message", "操作失败！当前时间已经超过了限时竞拍开始时间。");
				return out;
			}
		}
		
		//修改标的时间
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", targetId);
		sqlParams.put("end_notice_time", dateToString(end_notice_time));
		sqlParams.put("begin_apply_time", dateToString(begin_apply_time));
		sqlParams.put("end_apply_time", dateToString(end_apply_time));
		sqlParams.put("begin_earnest_time", dateToString(begin_earnest_time));
		sqlParams.put("end_earnest_time", dateToString(end_earnest_time));
		sqlParams.put("begin_list_time", dateToString(begin_list_time));
		sqlParams.put("end_list_time", dateToString(end_list_time));
		sqlParams.put("begin_focus_time", dateToString(begin_focus_time));
		sqlParams.put("end_focus_time", dateToString(end_focus_time));
		sqlParams.put("begin_limit_time", dateToString(begin_limit_time));
		out = this.updateTargetTimes(sqlParams);
		if(!"1".equals(out.getString("state"))){
			out.put("state", 0);
			out.put("message", "操作失败！修改标的信息失败。");
			return out;
		}
		
		//修改流程时间
		List phaseList =  getPhaseTimesList(targetId);
		if(phaseList!=null && phaseList.size()>0){
			int isEnd = 1 ;//1环节已结束 ， 0 未结束 
			for(int i = 0 ; i < phaseList.size(); i ++ ){
				ParaMap temp = (ParaMap) phaseList.get(i);
				String nid = temp.getString("nodeId");
				String bTime = temp.getString("beginTime");
				String eTime = temp.getString("endTime");
				if(nid.equals(nodeId)){//循环到当前的环节
					isEnd = 0;
					if(StrUtils.isNotNull(endPhaseTime)){
						if((this.dateAddDelay(DateUtils.getDate(endPhaseTime), dalay)).getTime()<=(DateUtils.now()).getTime()){
							out.put("state", 0);
							out.put("message", "操作失败！当前时间已经超过了限时竞拍开始时间。");
							return out;
						}
					}
				}
				if(isEnd == 0){//环节未结束
					if(nid.equals(nodeId)){//当前环节
						if(StrUtils.isNotNull(eTime)){
							//修改当前环节tasknode结束时间
							out = updateProcessTimes(tasknodeId ,Constants.PendTime , dateStrAddDelay(eTime, dalay) );
							if(!"1".equals(out.getString("state"))){
								return out;
							}
							//修改当前环节node结束时间
							out = updateProcessTimes(nid ,Constants.PendTime , dateStrAddDelay(eTime, dalay) );
							if(!"1".equals(out.getString("state"))){
								return out;
							}
						}
					}else{//未来环节
						//修改未来环节node开始时间
						if(StrUtils.isNotNull(bTime)){
							out = updateProcessTimes(nid ,Constants.PbeginTime , dateStrAddDelay(bTime, dalay) );
							if(!"1".equals(out.getString("state"))){
								return out;
							}
						}
						//修改未来环节node结束时间
						if(StrUtils.isNotNull(eTime)){
							out = updateProcessTimes(nid ,Constants.PendTime , dateStrAddDelay(eTime, dalay) );
							if(!"1".equals(out.getString("state"))){
								return out;
							}
						}
					}
				}
			}
		}
		out.put("state" , 1);
		return out;
	}
	
	public ParaMap restartProcessByTimes(ParaMap in , ParaMap target , ParaMap nodeTask)throws Exception{
		ParaMap out = new ParaMap();
		EngineDao engineDao = new EngineDao();
		String targetId = in.getString("id");
		String nodeId = nodeTask.getString("nodeid");
		String tasknodeId = nodeTask.getString("tasknodeid");
		String ptype = nodeTask.getString("ptype");
		String qtype = nodeTask.getString("qtype");
		String endPhaseTime = nodeTask.getString(Constants.PendTime);
		int trans_type = Integer.parseInt(String.valueOf(target.getRecordValue(0, "trans_type")));
		int is_limit_trans = Integer.parseInt(String.valueOf(target.getRecordValue(0, "is_limit_trans")));
		if(ptype.equals(Constants.LLimit)){//已经到了限时竞价，只要修改tasknode的结束时间信息就行了
			String endTime = in.getString("phase_end_time");
			if(StrUtils.isNull(endTime)){
				out.put("state", 0);
				out.put("message", "操作失败！阶段结束时间不能为空。");
				return out;
			}
			if((DateUtils.getDate(endTime)).getTime() <= (DateUtils.now()).getTime() ){
				out.put("state", 0);
				out.put("message", "操作失败！当前时间已经超过了本阶段的结束时间。");
				return out;
			}
			//修改当前环节tasknode结束时间
			out = updateProcessTimes(tasknodeId ,Constants.PendTime , endTime );
			if(!"1".equals(out.getString("state"))){
				return out;
			}
		}else{
			//修改标的相关时间
			out = this.updateTargetTimes(in);
			if(!"1".equals(out.getString("state"))){
				out.put("state", 0);
				out.put("message", "操作失败！修改标的信息失败。");
				return out;
			}
			//修改node和tasknode时间
			ParaMap nodeMap = engineDao.getNodeInfo(nodeId);
			if(ptype.equals(Constants.LNotice)){//公告期
				ParaMap nextMap = engineDao.getNodeInfo(nodeMap.getString("nextid")); //得到下一个节点
				if(nextMap.getString("ptype").equals(Constants.LFocus)){ //如果下一个节点是集中报价
					String end_notice_time = in.getString("begin_focus_time");
					String begin_focus_time = in.getString("begin_focus_time");
					String end_focus_time = in.getString("end_focus_time");
					if(StrUtils.isNull(begin_focus_time) || StrUtils.isNull(end_focus_time)){
						out.put("state", 0);
						out.put("message", "操作失败！集中报价期时间错误。");
						return out;
					}
					//修改公告环节结束时间
					out = updateProcessTimes(tasknodeId ,Constants.PendTime , end_notice_time );
					if(!"1".equals(out.getString("state"))){
						return out;
					}
					out = updateProcessTimes(nodeId ,Constants.PendTime , end_notice_time );
					if(!"1".equals(out.getString("state"))){
						return out;
					}
					//修改集中报价时间
					out = updateProcessTimes(nextMap.getString("id") ,Constants.PbeginTime , begin_focus_time );
					if(!"1".equals(out.getString("state"))){
						return out;
					}
					out = updateProcessTimes(nextMap.getString("id") ,Constants.PendTime , end_focus_time );
					if(!"1".equals(out.getString("state"))){
						return out;
					}
					//修改限时竞拍开始时间
					if(StrUtils.isNotNull(nextMap.getString("nextid"))){
						ParaMap nextNextMap = engineDao.getNodeInfo(nextMap.getString("nextid"));
						if(nextNextMap.getString("ptype").equals(Constants.LLimit)){
							String begin_limit_time = in.getString("begin_limit_time");
							if(StrUtils.isNull(begin_limit_time)){
								out.put("state", 0);
								out.put("message", "操作失败！限时竞价期时间错误。");
								return out;
							}
							out = updateProcessTimes(nextNextMap.getString("id") ,Constants.PbeginTime , begin_limit_time );
							if(!"1".equals(out.getString("state"))){
								return out;
							}
						}
					}
				}else if(nextMap.getString("ptype").equals(Constants.LLimit)){ //如果下一个节点是限时竞价
					String end_notice_time = in.getString("begin_limit_time");
					String begin_limit_time = in.getString("begin_limit_time");
					if(StrUtils.isNull(begin_limit_time)){
						out.put("state", 0);
						out.put("message", "操作失败！限时竞价期时间错误。");
						return out;
					}
					//修改公告环节结束时间
					end_notice_time =  in.getString("end_notice_time");
					if(StrUtils.isNotNull(end_notice_time) && StrUtils.isNotNull(begin_limit_time) ){
						if(DateUtils.getDate(end_notice_time).getTime() > DateUtils.getDate(begin_limit_time).getTime()){
							end_notice_time = in.getString("begin_limit_time");
						}else{
							end_notice_time = in.getString("end_notice_time");
						}
					}else{
						end_notice_time =  in.getString("begin_limit_time");
					}
					out = updateProcessTimes(tasknodeId ,Constants.PendTime , end_notice_time );
					if(!"1".equals(out.getString("state"))){
						return out;
					}
					out = updateProcessTimes(nodeId ,Constants.PendTime , end_notice_time );
					if(!"1".equals(out.getString("state"))){
						return out;
					}
					//修改限时竞拍开始时间
					out = updateProcessTimes(nextMap.getString("id") ,Constants.PbeginTime , begin_limit_time );
					if(!"1".equals(out.getString("state"))){
						return out;
					}
				}
			}else if(ptype.equals(Constants.LFocus)){//集中报价期
				String end_focus_time = in.getString("end_focus_time"); 
				if(StrUtils.isNull(end_focus_time)){
					out.put("state", 0);
					out.put("message", "操作失败！集中报价期时间错误。");
					return out;
				}
				//修改集中报价环节结束时间
				out = updateProcessTimes(tasknodeId ,Constants.PendTime , end_focus_time );
				if(!"1".equals(out.getString("state"))){
					return out;
				}
				out = updateProcessTimes(nodeId ,Constants.PendTime , end_focus_time );
				if(!"1".equals(out.getString("state"))){
					return out;
				}
				//修改限时竞拍开始时间
				if(StrUtils.isNotNull(nodeMap.getString("nextid"))){
					ParaMap nextMap = engineDao.getNodeInfo(nodeMap.getString("nextid"));
					if(nextMap.getString("ptype").equals(Constants.LLimit)){
						String begin_limit_time = in.getString("begin_limit_time");
						if(StrUtils.isNull(begin_limit_time)){
							out.put("state", 0);
							out.put("message", "操作失败！限时竞价期时间错误。");
							return out;
						}
						out = updateProcessTimes(nextMap.getString("id") ,Constants.PbeginTime , begin_limit_time );
						if(!"1".equals(out.getString("state"))){
							return out;
						}
					}
				}
			}
		}
		out.put("state" , 1);
		return out;
	}
	
	public ParaMap updateTargetTimes(ParaMap target)throws Exception{
		DataSetDao dao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		ParaMap formatMap = new ParaMap();
		sqlParams.put("id", target.getString("id"));
		if(target.get("end_notice_time")!=null && StrUtils.isNotNull(target.getString("end_notice_time")) ){
			sqlParams.put("end_notice_time", target.getString("end_notice_time"));
			formatMap.put("end_notice_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		}
		if(target.get("begin_apply_time")!=null && StrUtils.isNotNull(target.getString("begin_apply_time")) ){
			sqlParams.put("begin_apply_time", target.getString("begin_apply_time"));
			formatMap.put("begin_apply_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		}
		if(target.get("end_apply_time")!=null && StrUtils.isNotNull(target.getString("end_apply_time")) ){
			sqlParams.put("end_apply_time", target.getString("end_apply_time"));
			formatMap.put("end_apply_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		}
		if(target.get("begin_earnest_time")!=null && StrUtils.isNotNull(target.getString("begin_earnest_time")) ){
			sqlParams.put("begin_earnest_time", target.getString("begin_earnest_time"));
			formatMap.put("begin_earnest_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		}
		if(target.get("end_earnest_time")!=null && StrUtils.isNotNull(target.getString("end_earnest_time")) ){
			sqlParams.put("end_earnest_time", target.getString("end_earnest_time"));
			formatMap.put("end_earnest_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		}
		if(target.get("begin_list_time")!=null && StrUtils.isNotNull(target.getString("begin_list_time")) ){
			sqlParams.put("begin_list_time", target.getString("begin_list_time"));
			formatMap.put("begin_list_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		}
		if(target.get("end_list_time")!=null && StrUtils.isNotNull(target.getString("end_list_time")) ){
			sqlParams.put("end_list_time", target.getString("end_list_time"));
			formatMap.put("end_list_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		}
		if(target.get("begin_focus_time")!=null && StrUtils.isNotNull(target.getString("begin_focus_time")) ){
			sqlParams.put("begin_focus_time", target.getString("begin_focus_time"));
			formatMap.put("begin_focus_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		}
		if(target.get("end_focus_time")!=null && StrUtils.isNotNull(target.getString("end_focus_time")) ){
			sqlParams.put("end_focus_time", target.getString("end_focus_time"));
			formatMap.put("end_focus_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		}
		if(target.get("begin_limit_time")!=null && StrUtils.isNotNull(target.getString("begin_limit_time")) ){
			sqlParams.put("begin_limit_time", target.getString("begin_limit_time"));
			formatMap.put("begin_limit_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		}
		ParaMap out = dao.updateData("trans_target", "id", sqlParams , formatMap);
		return out;
	}
	
	public ParaMap updateProcessTimes(String id , String name , String value) throws Exception{
		ParaMap out = new ParaMap();
		DataSetDao dao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", null);
		sqlParams.put("refid", id);
		sqlParams.put("name", name);
		sqlParams.put("value", value);
		out = dao.updateData("wf_parameter", "id","refid,name", sqlParams, null);
		return out;
	}
	
	public Date getTargetDate(ParaMap target, String datename) throws Exception{
		Date date = null;
		String dateStr = String.valueOf(target.getRecordValue(0, datename));
		if(StrUtils.isNotNull(dateStr)){
			date = DateUtils.getDate(dateStr);
		}
		return date;
	}
	
	public String dateToString(Date date)throws Exception{
		String result = null;
		if(date == null){
			return result;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			result = sdf.format(date);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	public Date dateAddDelay(Date beginDate , int dalay)throws Exception{
		if(beginDate != null){
			return new Date(beginDate.getTime() + 1000l* 24 * 60 * 60 * dalay);
		}else{
			return null;
		}
	}
	
	public String dateStrAddDelay(String beginDate , int dalay)throws Exception{
		if(StrUtils.isNotNull(beginDate)){
			return DateUtils.getStr( new Date( (DateUtils.getDate(beginDate)).getTime() + 1000l* 24 * 60 * 60 * dalay) );
		}else{
			return null;
		}
	}
	
	
	
	public ParaMap cancelProcess(String processId)throws Exception{
		ParaMap out = new ParaMap();
		DataSetDao dataSetDao = new DataSetDao();
		ParaMap sqlParams = new ParaMap();
		sqlParams.put("id", processId);
		ParaMap processMap = dataSetDao.querySimpleData("wf_process", sqlParams);
		if(processMap!=null && processMap.getSize()>0){
			String templateId = String.valueOf(processMap.getRecordValue(0, "templateid"));
			String tasknodeId = String.valueOf(processMap.getRecordValue(0, "tasknodeid"));
			String state = String.valueOf(processMap.getRecordValue(0, "state"));
			if( "1".equals(state) || "3".equals(state)){
				//修改标的信息
				sqlParams.clear();
				sqlParams.put("id", templateId);
				sqlParams.put("is_stop", 1);
				out = dataSetDao.updateData("trans_target", "id", sqlParams, null);
				if(!"1".equals(out.getString("state"))){
					out.put("state", 0);
					out.put("message", "操作失败！修改标的信息失败。");
					return out;
				}
				//修改流程信息
				sqlParams.clear();
				sqlParams.put("id", processId);
				sqlParams.put("state", Constants.ProcessOff);
				out = dataSetDao.updateData("wf_process", "id", sqlParams, null);
				if(!"1".equals(out.getString("state"))){
					out.put("state", 0);
					out.put("message", "操作失败！修改流程信息失败。");
					return out;
				}
				//保存停止时间
				sqlParams.clear();
				sqlParams.put("id", null);
				sqlParams.put("refid", tasknodeId);
				sqlParams.put("name", Constants.pstopTime);
				sqlParams.put("value", DateUtils.getStr(DateUtils.now()));
				ParaMap result=dataSetDao.updateData("wf_parameter", "id","refid,name", sqlParams, null);
			}else{
				out.put("state", 0);
				out.put("message", "操作失败！流程状态错误。");
			}
		}else{
			out.put("state", 0);
			out.put("message", "操作失败！请选择有效的标的流程。");
		}
		return out;
	}
	
	
}
