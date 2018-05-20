package com.sysman.service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.base.service.BaseService;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.utils.StreamUtils;
import com.sysman.dao.ExchangeDataDao;

public class ExchangeDataService extends BaseService {

	public ParaMap syncRemoteData(ParaMap inMap) throws Exception {
		ExchangeDataDao dao = new ExchangeDataDao();
		ParaMap out = dao.syncRemoteData(inMap);
		return out;
	}

	public byte[] downloadSyncData(ParaMap inMap) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		Date now = DateUtils.now();
		String downloadFileName = "SyncData" + sdf.format(now) + ".xml";
		// downloadFileName = new String(downloadFileName.getBytes("gb2312"),
		// "ISO8859-1");
		this.getResponse().reset();
		this.getResponse().setContentType(
				"application/octet-stream; charset=UTF-8");
		this.getResponse().addHeader("Content-Disposition",
				"attachment; filename=\"" + downloadFileName + "\";");
		ExchangeDataDao dao = new ExchangeDataDao();
		byte[] buf = StreamUtils.InputStreamToByte(dao.getSyncData(inMap));
		return buf;
	}

	public ParaMap receiveSyncData(ParaMap inMap) throws Exception {
		InputStream ins = this.getRequest().getInputStream();
		String xml = StreamUtils.InputStreamToString(ins);
		ParaMap out = new ParaMap();
		if (StrUtils.isNull(xml)) {
			out.put("state", 0);
			out.put("message", "传入的同步数据XML为空!");
			return out;
		}
		ExchangeDataDao dao = new ExchangeDataDao();
		out = dao.syncData(xml);
		return out;
	}

	public ParaMap uploadSyncDataFile(ParaMap inMap) throws Exception {
		byte[] buf = inMap.getBytes("txtUploadFile");
		// ServletFileUpload upload = new ServletFileUpload(new
		// DiskFileItemFactory());
		// List<FileItem> items = upload.parseRequest(this.getRequest());
		// for (int i = 0; i < items.size(); i++) {
		// FileItem item = items.get(i);
		// if (item.getFieldName().equals("txtUploadFile")) {
		// buf = item.get();
		// break;
		// }
		// }
		String xml = new String(buf);
		ExchangeDataDao dao = new ExchangeDataDao();
		ParaMap out = dao.syncData(xml);
		String promptMessage = null;
		if (out == null)
			promptMessage = "上传同步数据文件并导入操作失败，请重新打开页面重试。";
		else {
			promptMessage = out.getString("message");
			if (StrUtils.isNull(promptMessage))
				if (out.getInt("state") == 1) {
					promptMessage = "上传同步数据文件并导入成功完成。";
				} else {
					promptMessage = "上传同步数据文件并导入操作失败。";
				}
		}
		out = new ParaMap();
		out.put("message", promptMessage);
		return out;
	}

}
