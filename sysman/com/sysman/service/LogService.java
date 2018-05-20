package com.sysman.service;

import java.io.File;
import java.io.FileFilter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import com.base.service.BaseService;
import com.base.utils.DateUtils;
import com.base.utils.ParaMap;
import com.base.web.ResourceUtils;

public class LogService extends BaseService {

	public ParaMap getFiles(ParaMap in) throws Exception {
		ParaMap out = new ParaMap();
		List arr = new ArrayList();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		final long beginDate = df.parse(in.getString("beginDate")).getTime();
		final long endDate = df.parse(in.getString("endDate")).getTime() + 24
				* 60 * 60 * 1000L;
		File dir = new File("logs");
		ServletContext sc = this.getRequest().getSession().getServletContext();
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				if (file.getName().startsWith("log.txt")) {
					long d = file.lastModified();
					if (beginDate <= d && d <= endDate)
						return true;
				}
				return false;
			}
		});
		for (int i = 0; i < files.length; i++) {
			ParaMap map = new ParaMap();
			String btn = "<a href='"
					+ this.getRequest().getContextPath()
					+ "/download?module=sysman&service=Log&method=directDownLoad&file="
					+ files[i].getName() + "'>下载</a>";
			map.put("name", files[i].getName());
			double f = files[i].length() / (1024 * 1024d);
			BigDecimal b = new BigDecimal(f);
			double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			map.put("length", f1);
			map.put("op", btn);
			arr.add(map);
		}
		out.put("rows", arr);
		out.put("now", DateUtils.nowStr());
		return out;
	}

	/**
	 * 直接下载文件
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public byte[] directDownLoad(ParaMap inMap) throws Exception {
		String file = inMap.getString("file");
		String downloadFileName = file;
		downloadFileName = new String(downloadFileName.getBytes("gb2312"),
				"ISO8859-1");
		this.getResponse().reset();
		this.getResponse().setContentType(
				"application/octet-stream; charset=UTF-8");
		this.getResponse().addHeader("Content-Disposition",
				"attachment; filename=\"" + downloadFileName + "\";");
		String actualFileName = "logs/" + file;
		log.debug("actualFileName>>>" + actualFileName);
		byte[] buf = ResourceUtils.getBytes(actualFileName);
		return buf;
	}

}
