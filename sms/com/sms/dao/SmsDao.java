package com.sms.dao;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.AppConfig;
import com.sysman.dao.ExchangeDataDao;

/**
 * 短信Dao huafc 2012-05-30
 */
public class SmsDao extends BaseDao {
	// 系统设置是否发送短信，通过appConfig.properties设置。1写数据库，2通过http发送，其它值不发送
	private static int sms_config = -1;

	public static final String CATEGORY_TransBankInputBill = "TransBankInputBill"; // 入账单通知竞买人
	public static final String CATEGORY_TransBankInputBill1 = "TransBankInputBill1"; // 入账单通知联合竞买人
	public static final String CATEGORY_TransBankInputBill2 = "TransBankInputBill2"; // 入账单通知其它人员
	public static final String CATEGORY_TransBeforeInLimitTime = "TransBeforeInLimitTime"; // 即将进入标的限时竞价时间通知竞买人
	public static final String CATEGORY_TransBeforeInLimitTime1 = "TransBeforeInLimitTime1"; // 即将进入标的限时竞价时间通知联合竞买人
	public static final String CATEGORY_TransBeforeInLimitTime2 = "TransBeforeInLimitTime2"; // 即将进入标的限时竞价时间通知收藏人员
	public static final String CATEGORY_TransBeforeInLimitTime3 = "TransBeforeInLimitTime3"; // 即将进入标的限时竞价时间通知其它人员
	public static final String CATEGORY_TransBeforeInSceneTime = "TransBeforeInSceneTime"; // 即将进入标的现场竞价时间通知竞买人
	public static final String CATEGORY_TransBeforeInSceneTime1 = "TransBeforeInSceneTime1"; // 即将进入标的现场竞价时间通知联合竞买人
	public static final String CATEGORY_TransBeforeInSceneTime2 = "TransBeforeInSceneTime2"; // 即将进入标的现场竞价时间通知收藏人员
	public static final String CATEGORY_TransBeforeInSceneTime3 = "TransBeforeInSceneTime3"; // 即将进入标的现场竞价时间通知其它人员
	public static final String CATEGORY_TransInLimitTime = "TransInLimitTime"; // 进入标的限时竞价时间通知竞买人
	public static final String CATEGORY_TransInLimitTime1 = "TransInLimitTime1"; // 进入标的限时竞价时间通知联合竞买人
	public static final String CATEGORY_TransInLimitTime2 = "TransInLimitTime2"; // 进入标的限时竞价时间通知收藏人员
	public static final String CATEGORY_TransInLimitTime3 = "TransInLimitTime3"; // 进入标的限时竞价时间通知其它人员
	public static final String CATEGORY_TransInSceneTime = "TransInSceneTime"; // 进入标的现场竞价时间通知竞买人
	public static final String CATEGORY_TransInSceneTime1 = "TransInSceneTime1"; // 进入标的现场竞价时间通知联合竞买人
	public static final String CATEGORY_TransInSceneTime2 = "TransInSceneTime2"; // 进入标的现场竞价时间通知收藏人员
	public static final String CATEGORY_TransInSceneTime3 = "TransInSceneTime3"; // 进入标的现场竞价时间通知其它人员
	public static final String CATEGORY_TransInTransTime = "TransInTransTime"; // 进入标的交易时间通知竞买人
	public static final String CATEGORY_TransInTransTime1 = "TransInTransTime1"; // 进入标的交易时间通知联合竞买人
	public static final String CATEGORY_TransInTransTime2 = "TransInTransTime2"; // 进入标的交易时间通知收藏人员
	public static final String CATEGORY_TransInTransTime3 = "TransInTransTime3"; // 进入标的交易时间通知其它人员
	public static final String CATEGORY_TransLicense = "TransLicense"; // 竞买申请通知竞买人
	public static final String CATEGORY_TransLicense1 = "TransLicense1"; // 竞买申请通知联合竞买人
	public static final String CATEGORY_TransLicenseBack = "TransLicenseBack"; // 互斥地块取消资格通知竞买人
	public static final String CATEGORY_TransLicenseBack1 = "TransLicenseBack1"; // 互斥地块取消资格通知联合竞买人
	public static final String CATEGORY_TransLicenseBack2 = "TransLicenseBack2"; // 互斥地块取消资格通知其它人员
	public static final String CATEGORY_TransLicenseConfirm = "TransLicenseConfirm"; // 竞买申请审核通知竞买人
	public static final String CATEGORY_TransLicenseConfirm1 = "TransLicenseConfirm1"; // 竞买申请审核通知联合竞买人
	public static final String CATEGORY_TransLicensePass = "TransLicensePass"; // 竞买申请通过通知竞买人
	public static final String CATEGORY_TransLicensePass1 = "TransLicensePass1"; // 竞买申请通过通知联合竞买人
	public static final String CATEGORY_TransMeet = "TransMeet"; // 会议通知
	public static final String CATEGORY_TransMeetModify = "TransMeetModify"; // 会议变更通知
	public static final String CATEGORY_TransNewOffer = "TransNewOffer"; // 新报价时通知竞买人
	public static final String CATEGORY_TransNewOffer1 = "TransNewOffer1"; // 新报价时通知联合竞买人
	public static final String CATEGORY_TransNewOffer2 = "TransNewOffer2"; // 新报价时通知收藏人员
	public static final String CATEGORY_TransNewOffer3 = "TransNewOffer3"; // 新报价时通知其它人员
	public static final String CATEGORY_TransNoticeAdditional = "TransNoticeAdditional"; // 补充公告通知竞买竞买人
	public static final String CATEGORY_TransNoticeAdditional1 = "TransNoticeAdditional1"; // 补充公告通知竞买联合竞买人
	public static final String CATEGORY_TransNoticeAdditional2 = "TransNoticeAdditional2"; // 补充公告通知收藏人员
	public static final String CATEGORY_TransNoticeAdditional3 = "TransNoticeAdditional3"; // 补充公告通知其它人员
	public static final String CATEGORY_TransRegister = "TransRegister"; // 网上交易系统注册通知申请人
	public static final String CATEGORY_TransTargetComplete = "TransTargetComplete"; // 标的交易结束通知竞买人
	public static final String CATEGORY_TransTargetComplete1 = "TransTargetComplete1"; // 标的交易结束通知联合竞买人
	public static final String CATEGORY_TransTargetComplete2 = "TransTargetComplete2"; // 标的交易结束通知收藏人员
	public static final String CATEGORY_TransTargetComplete3 = "TransTargetComplete3"; // 标的交易结束通知其它人员
	public static final String CATEGORY_TransTrust = "TransTrust"; // 转让交易委托通知
	public static final String CATEGORY_TransTrustModify = "TransTrustModify"; // 转让交易委托变更通知
	public static final String CATEGORY_TransUnionConfirm = "TransUnionConfirm"; // 联合申请人确认通知申请人
	public static final String CATEGORY_TransUnionConfirm1 = "TransUnionConfirm1"; // 联合申请人确认通知其它联合申请人
	public static final String CATEGORY_Workflow = "Workflow"; // 工作流通知

	/**
	 * 发送短信
	 * 
	 * @param messageData
	 *            短信内容<br/>
	 *            &nbsp;&nbsp; <b>必须包含的内容有：</b><br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp; category:
	 *            短信分类，参考pf_phone_message_category表。空或者不存在的分类则直接发送<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp; phoneNo: 接收短信号码，多个号码可使用半角逗号或分号分隔<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp; content: 短信内容<br/>
	 *            &nbsp;&nbsp; <b>可包含的内容有：</b><br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp; sourceTableName: 短信来源表<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp; sourceKey: 短信来源表键值<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp; sendTime: 发送时间，缺省为立刻<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp; overdueTime: 过期时间，缺省为sendTime的半小时后<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp; sendCount: 重复发送次数，缺省1次<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp; sendInterval:
	 *            发送时间间隔，单位分钟，缺省30分钟，仅当sendCount>1时有效<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp; multiSendMode:
	 *            多次发送时的模式，仅当sendCount>1有效。0发送sendCount次，1对方有收到即停止发送，2对方有回复停止发送<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp; replyNo: 短信回复标识符<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp; sendPhoneNo: 发送短信号码<br/>
	 *            &nbsp;&nbsp;&nbsp;&nbsp; forceSend:
	 *            true强制发送，缺省值为false。当系统设置category为可强制发送时设置此值有效<br/>
	 * @return 返回发送状态：<br/>
	 *         &nbsp;&nbsp; state: 1全部成功，2部分成功，其它值失败<br/>
	 *         &nbsp;&nbsp; message: 发送失败时的错误信息<br/>
	 *         &nbsp;&nbsp; id: 短信记录id<br/>
	 *         &nbsp;&nbsp; no: 短信回执编号<br/>
	 * @throws Exception
	 */
	public ParaMap sendMessage(ParaMap messageData) throws Exception {
		if (messageData == null) {
			ParaMap result = new ParaMap();
			result.put("state", 0);
			result.put("message", "未传入信息任何内容");
			return result;
		}
		String phoneNos = messageData.getString("phoneNo");
		log.debug("准备发送短信...");
		if (StrUtils.isNull(phoneNos)) {
			ParaMap result = new ParaMap();
			result.put("state", 0);
			result.put("message", "未传入接收信息的电话号码");
			log.debug("未传入接收信息的电话号码");
			return result;
		}
		String content = messageData.getString("content");
		if (StrUtils.isNull(content)) {
			ParaMap result = new ParaMap();
			result.put("state", 0);
			result.put("message", "未传入发送信息的内容");
			log.debug("未传入发送信息的内容");
			return result;
		}
		log.debug("发送短信...");
		if (sms_config == -1) {
			String system_sms = AppConfig.getPro("sms");
			if (StrUtils.isNull(system_sms))
				sms_config = 0;
			else if (StrUtils.isInteger(system_sms)) {
				sms_config = Integer.valueOf(system_sms);
				if (sms_config < 0 || sms_config > 2)
					sms_config = 0;
			} else {
				sms_config = 0;
			}
		}
		if (sms_config <= 0 || sms_config > 2) {
			log.debug("系统设置为不需要发送信息(sms=" + sms_config + ")，操作取消并返回");
			return null;
		}
		try {
			if (sms_config == 1)
				return sendMessageByDB(messageData);
			else if (sms_config == 2)
				return sendMessageByHttp(messageData);
			else
				return null;
		} catch (Exception e) {
			log.error(e.getMessage());
			log.error("发送短信内容失败");
			return null;
		}
	}

	public ParaMap sendMessage(String category, String phoneNo, String content,
			String sourceTableName, String sourceKey) throws Exception {
		ParaMap messageData = new ParaMap();
		messageData.put("category", category);
		messageData.put("phoneNo", phoneNo);
		messageData.put("content", content);
		messageData.put("sourceTableName", sourceTableName);
		messageData.put("sourceKey", sourceKey);
		return sendMessage(messageData);
	}

	public ParaMap sendMessage(String category, String phoneNo, String content)
			throws Exception {
		return sendMessage(category, phoneNo, content, null, null);
	}

	private ParaMap sendMessageByDB(ParaMap messageData) throws Exception {
		ParaMap result = null;
		String phoneNos = messageData.getString("phoneNo");
		ParaMap format = new ParaMap();
		format.put("overdue_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		ParaMap data = new ParaMap();
		data.put("id", null);
		data.put("category", messageData.getInt("category"));
		data.put("content", messageData.getString("content"));
		data.put("source_table_name", messageData.getString("sourceTableName"));
		data.put("source_key", messageData.getString("sourceKey"));
		if (messageData.containsKey("sendTime")) {
			data.put("send_time", messageData.getString("sendTime"));
			format.put("send_time", "to_date(?, 'yyyy-mm-dd hh24:mi:ss')");
		} else {
			data.put("send_time", null);
			format.put("send_time", "sysdate");
		}
		data.put("overdue_time", messageData.getString("overdueTime"));
		data.put("send_count", messageData.getInt("sendCount"));
		data.put("send_interval", messageData.getInt("sendInterval"));
		data.put("resend_level", messageData.getInt("multiSendMode"));
		data.put("reply_id", messageData.getString("replyNo"));
		DataSetDao dataSetDao = new DataSetDao();
		List<String> phoneNoList = null;
		if (phoneNos.indexOf(";") >= 0)
			phoneNoList = StrUtils.getSubStrs(phoneNos, ";", true);
		else
			phoneNoList = StrUtils.getSubStrs(phoneNos, ",", true);
		int intSendCount = 0;
		String strMessageIds = "";
		for (int i = 0; i <= phoneNoList.size(); i++) {
			String phoneNo = parsePhoneNo(phoneNoList.get(i));
			if (StrUtils.isNotNull(phoneNo)) {
				data.put("phone_no", phoneNo);
				result = dataSetDao.updateData("pf_phone_message", "id", data,
						format);
				if (result.getInt("state") == 1) {
					intSendCount++;
					strMessageIds += result.getString("id") + ";";
				}
			}
		}
		if (StrUtils.isNotNull(strMessageIds))
			strMessageIds = strMessageIds.substring(0, strMessageIds.length());
		if (intSendCount == 0) {
			result.clear();
			result.put("state", 0);
			if (phoneNoList.size() > 0)
				result.put("message", "接收到目标电话号码为" + phoneNoList.size()
						+ "个，但全部发送失败");
			else
				result.put("message", "未传入接收信息的电话号码");
		} else {
			result.clear();
			if (intSendCount == phoneNoList.size()) {
				result.put("state", 1);
				result.put("messageIds", strMessageIds);
			} else {
				result.put("state", 2);
				result.put("messageIds", strMessageIds);
				result.put("message", "接收到目标电话号码为" + phoneNoList.size()
						+ "个，成功发送" + intSendCount + "个");
			}
		}
		return result;
	}

	private ParaMap sendMessageByHttp(ParaMap messageData) throws Exception {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("utf-8");
		Element rootElement = document.addElement("messages");
		Element messageElement = rootElement.addElement("message");
		messageElement.addAttribute("phone_no",
				messageData.getString("phoneNo"));
		messageElement.addAttribute("category",
				messageData.getString("category"));
		messageElement.addAttribute("source_table_name",
				messageData.getString("sourceTableName"));
		messageElement.addAttribute("source_key",
				messageData.getString("sourceKey"));
		messageElement.addAttribute("send_time",
				messageData.getString("sendTime"));
		messageElement.addAttribute("overdue_time",
				messageData.getString("overdueTime"));
		messageElement.addAttribute("send_count",
				messageData.getString("sendCount"));
		messageElement.addAttribute("send_interval",
				messageData.getString("sendInterval"));
		messageElement.addAttribute("resend_level",
				messageData.getString("multiSendMode"));
		messageElement.addAttribute("reply_id",
				messageData.getString("replyNo"));
		messageElement.addCDATA(messageData.getString("content"));
		ParaMap urlParams = new ParaMap();
		urlParams.put("dataType", "phoneMessage");
		ExchangeDataDao exchangeDataDao = new ExchangeDataDao();
		String sendResult = "";
		return ParaMap.fromString(sendResult);
	}

	private String parsePhoneNo(String phoneNo) {
		return phoneNo;
	}

	public static void main(String[] args) {
		ParaMap messageData = new ParaMap();
		messageData.put("category", SmsDao.CATEGORY_TransBankInputBill);
		messageData.put("phoneNo", "13510563127;13510291680");
		messageData.put("content", "测试新交易系统短信数据发送到旧系统并发送");
		SmsDao smsDao = new SmsDao();
		try {
			System.out.println(smsDao.sendMessage(messageData));
			System.out.println("测试发送短信结束....");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}