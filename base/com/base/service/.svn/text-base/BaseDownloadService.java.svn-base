package com.base.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.FsUtils;
import com.base.utils.ParaMap;
import com.base.utils.Pdf2SwfUtils;
import com.base.utils.StrUtils;
import com.base.web.ContentTypes;
import com.base.web.ResourceUtils;

public class BaseDownloadService extends BaseService {
	private final static String moduleNo = "sys_lob_manager";

	/**
	 * 下载文件,先根据lobId在附件主表中查询,如果filePath不为空,则在文件系统中下载,如果为空则在数据库中下载
	 * 
	 * @param inMap
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] downloadFile(ParaMap inMap) throws Exception {
		DataSetDao dao = new DataSetDao();
		String lobId = inMap.getString("lobId");
		// 先根据lobId在附件主表中查询
		ParaMap keyData = new ParaMap();
		keyData.put("id", lobId);
		ParaMap sysLob = dao.querySimpleData("sys_lob", keyData);
		String filePath = (String) sysLob.getRecordValue(0, "file_path");
		log.debug("file_path:" + filePath);

		String downLoadName = "";// 下载文件名称
		String extendType = "";// 文件扩展名
		// 先根据lobId在附件主表中查询
		List rsList = sysLob.getList("rs");
		if (rsList != null && rsList.size() > 0) {
			String fileName = (String) sysLob.getRecordValue(0, "file_name");// 文件名
			extendType = fileName.substring(fileName.lastIndexOf("."),
					fileName.length());// 得到文件类型
			String name = (String) sysLob.getRecordValue(0, "name");// 文件逻辑名称，也就是重命名
			if (name != null && !"".equals(name) && !"null".equals(name)) {
				downLoadName = name + extendType;
			} else {
				downLoadName = fileName;
			}
		}
		String type = ContentTypes.getContentType(extendType.substring(1,
				extendType.length()));
		if (StrUtils.isNull(type)) {
			type = "application/x-msdownload";
		}
		this.getResponse().reset();
		this.getResponse().setContentType(type + "; charset=UTF-8");
		downLoadName = new String(downLoadName.getBytes("gb2312"), "ISO8859-1");
		this.getResponse().addHeader("Content-Disposition",
				"attachment; filename=\"" + downLoadName + "\";");
		this.getResponse().flushBuffer();

		if (StringUtils.isNotEmpty(filePath)) {
			byte[] fileByte = null;
			File file = FsUtils.get(filePath);
			fileByte = FileUtils.readFileToByteArray(file);
			return fileByte;
		} else {
			// 查询子表
			byte[] fileByte = null;
			ParaMap sysBlobDtl = new ParaMap();// 用于存放附件子表信息
			sysBlobDtl.put("lobId", lobId);
			sysBlobDtl.put("moduleNo", moduleNo);
			sysBlobDtl.put("dataSetNo", "query_sys_blob_dtl");
			sysBlobDtl = dao.queryData(sysBlobDtl);
			fileByte = this.combinationFile(sysBlobDtl);
			return fileByte;
		}
	}

	/**
	 * 合并文件,如果附件是存在数据库中的,则根据分割的存储记录合并成一个文件下载
	 * 
	 * @param inMap
	 * @return byte[]
	 * @throws Exception
	 */
	private byte[] combinationFile(ParaMap sysBlobDtl) throws Exception {
		BaseDao upLoadDao = new BaseDao();
		byte[] fileByte = null;
		List list = sysBlobDtl.getList("rs");
		if (list != null && list.size() > 0) {
			long alreadyWrite = 0;
			byte[] bt = new byte[1024];
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			for (int i = 0; i < list.size(); i++) {
				String id = (String) sysBlobDtl.getRecordValue(i, "id");
				int len = 0;
				if (id != null && !"".equals(id) && !"null".equals(id)) {
					BufferedInputStream bs = null;
					bs = upLoadDao.getBlobContent("sys_blob_dtl", "id", id,
							"content");
					if (bs != null) {
						while ((len = bs.read(bt)) > 0) {
							out.write(bt, 0, len);
						}
						bs.close();
					}
				}
			}
			fileByte = out.toByteArray();
			out.flush();
			out.close();
		}
		return fileByte;
	}

	/**
	 * 下载多个文件，然后压缩，压缩后下载
	 * 
	 * @param inMap
	 * @return byte[]
	 * @throws Exception
	 */

	public byte[] downloadZipFile(ParaMap inMap) throws Exception {
		// 查询数据
		DataSetDao dao = new DataSetDao();
		String lobId = inMap.getString("lobId");
		String refId = inMap.getString("refId");
		StringBuffer customCondition = new StringBuffer("");
		if (lobId != null && !"".equals(lobId) && !"null".equals(lobId)) {
			customCondition.append(" and id in('");
			String ids[] = lobId.split(",");
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					if (i == ids.length - 1) {
						customCondition.append(ids[i] + "')");
					} else {
						customCondition.append(ids[i] + "','");
					}
				}
			}
		}
		if (refId != null && !"".equals(refId) && !"null".equals(refId)) {
			customCondition.append(" and ref_id='" + refId + "'");
		}
		ParaMap sqlParams = new ParaMap();
		ParaMap map = new ParaMap();
		map.put("CUSTOM_CONDITION", customCondition.toString());
		sqlParams.put(dao.SQL_CUSTOM_CONDITIONS, map);
		sqlParams.put("moduleNo", moduleNo);
		sqlParams.put("dataSetNo", "query_more_sys_lob");
		ParaMap sysLob = dao.queryData(sqlParams);
		List list = sysLob.getList("rs");
		// 压缩，下载附件
		byte[] fileByte = null;
		if (list != null && list.size() > 0) {
			// String rootPath = AppConfig.getPro("fileRoot");// 得到配置文件中的配置路径
			List fileList = new ArrayList();
			List fileNameList = new ArrayList();
			List typeList = new ArrayList();
			Set s = new HashSet();
			for (int i = 0; i < list.size(); i++) {
				String id = (String) sysLob.getRecordValue(i, "id");
				String filePath = (String) sysLob
						.getRecordValue(i, "file_path");
				String name = (String) sysLob.getRecordValue(i, "name");// 文件逻辑名称，也就是重命名
				String fileName = (String) sysLob
						.getRecordValue(i, "file_name");
				String extendType = fileName.substring(
						fileName.lastIndexOf("."), fileName.length());// 得到文件类型
				if (name != null && !"".equals(name) && !"null".equals(name)) {
					fileName = name + extendType;
				} else {
					fileName = fileName;
				}
				boolean b = s.add(fileName);

				if (filePath != null && !"".equals(filePath)
						&& !"null".equals(filePath)) {// 说明存放在硬盘
					// filePath = rootPath + filePath;
					File file = new File(FsUtils.get(filePath)
							.getAbsolutePath());
					FileInputStream in = new FileInputStream(file);
					if (b == false) {// 没有插入，说明有重名的
						fileName = fileName.substring(0,
								fileName.lastIndexOf("."))
								+ "(" + i + ")" + extendType;
						fileNameList.add(fileName);
					} else {
						fileNameList.add(fileName);
					}
					fileList.add(in);
					typeList.add("fileInput");
				} else {
					byte[] dbByte = null;
					ParaMap sysBlobDtl = new ParaMap();// 用于存放附件子表信息
					sysBlobDtl.put("lobId", id);
					sysBlobDtl.put("moduleNo", moduleNo);
					sysBlobDtl.put("dataSetNo", "query_sys_blob_dtl");
					sysBlobDtl = dao.queryData(sysBlobDtl);
					dbByte = this.combinationFile(sysBlobDtl);
					if (dbByte != null && dbByte.length > 0) {
						ByteArrayInputStream in = new ByteArrayInputStream(
								dbByte);
						if (b == false) {// 没有插入，说明有重名的
							fileName = fileName.substring(0,
									fileName.lastIndexOf("."))
									+ "(" + i + ")" + extendType;
							fileNameList.add(fileName);
						} else {
							fileNameList.add(fileName);
						}
						fileList.add(in);
						typeList.add("byteArrayInput");
					}
				}
			}

			// 压缩文件
			fileByte = this.zipFiles(fileList, fileNameList, typeList);
		}

		// 设置下载类型
		String downLoadName = "";// 下载文件名称
		String extendType = "";// 文件扩展名
		String zipName = "";// 如果单个文件要压缩,文件名用这个来保存
		// 先根据lobId在附件主表中查询

		List rsList = sysLob.getList("rs");
		if (rsList != null && rsList.size() == 1) {
			String fileName = (String) sysLob.getRecordValue(0, "file_name");// 文件名
			extendType = fileName.substring(fileName.lastIndexOf("."),
					fileName.length());// 得到文件类型
			String name = (String) sysLob.getRecordValue(0, "name");// 文件逻辑名称，也就是重命名
			if (name != null && !"".equals(name) && !"null".equals(name)) {
				downLoadName = name + extendType;
				zipName = name;
			} else {
				downLoadName = fileName;
				zipName = fileName;
			}
		}

		String zipFileName = inMap.getString("zipFileName");
		extendType = ".zip";
		if (StrUtils.isNotNull(zipFileName)) {// 如果传了下载文件名
			downLoadName = zipFileName + ".zip";
			downLoadName = new String(downLoadName.getBytes("ISO-8859-1"),
					"UTF-8");
		} else {
			if (StrUtils.isNotNull(zipName)) {// 获取数据库中的文件名
				downLoadName = zipName + ".zip";
			} else {
				downLoadName = new String("压缩文件.zip");
			}
		}
		String type = ContentTypes.getContentType(extendType.substring(1,
				extendType.length()));
		if (StrUtils.isNull(type)) {
			type = "application/x-msdownload";
		}
		this.getResponse().reset();
		this.getResponse().setContentType(type + "; charset=UTF-8");
		downLoadName = new String(downLoadName.getBytes("gb2312"), "ISO8859-1");
		this.getResponse().addHeader("Content-Disposition",
				"attachment; filename=\"" + downLoadName + "\";");
		this.getResponse().flushBuffer();
		// 设置完毕
		return fileByte;
	}

	/**
	 * 压缩文件
	 * 
	 * @param inMap
	 * @return byte[]
	 * @throws Exception
	 */

	private byte[] zipFiles(List fileList, List fileNameList, List typeList)
			throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		ZipOutputStream zout = new ZipOutputStream(out);
		for (int i = 0; i < fileList.size(); i++) {
			String fileName = (String) fileNameList.get(i);
			String type = (String) typeList.get(i);
			if ("fileInput".equals(type)) {
				FileInputStream in = (FileInputStream) fileList.get(i);
				zout.putNextEntry(new ZipEntry(fileName));
				zout.setEncoding("gb2312");
				int len;
				while ((len = in.read(buf)) > 0) {
					zout.write(buf, 0, len);
				}
				in.close();
			} else if ("byteArrayInput".equals(type)) {
				ByteArrayInputStream in = (ByteArrayInputStream) fileList
						.get(i);
				zout.putNextEntry(new ZipEntry(fileName));
				zout.setEncoding("gb2312");
				int len;
				while ((len = in.read(buf)) > 0) {
					zout.write(buf, 0, len);
				}
				in.close();
			}

		}
		zout.close();
		out.close();
		return out.toByteArray();
	}

	/**
	 * 直接下载文件，文件位置在tomcat根目录下的doc目录
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public byte[] directDownLoad(ParaMap inMap) throws Exception {
		// 需要重命名的特殊文件请在此处命名
		Map renameFiles = new HashMap();
		renameFiles.put("SDCASetup.rar", "SDCA驱动.rar");
		renameFiles.put("OneKeyInstall.rar", "一键安装.rar");
		renameFiles.put("JMXZYB.doc", "竞买须知样本.doc");
		renameFiles.put("CJQRSYB.doc", "成交确认书样本.doc");
		renameFiles.put("JMSQSYB.doc", "竞买申请书样本.doc");
		String file = inMap.getString("file");
		log.debug("directDownLoad file>>>" + file);
		String downloadFileName = renameFiles.containsKey(file) ? (String) renameFiles
				.get(file) : file;
		downloadFileName = new String(downloadFileName.getBytes("gb2312"),
				"ISO8859-1");
		this.getResponse().reset();
		this.getResponse().setContentType(
				"application/octet-stream; charset=UTF-8");
		this.getResponse().addHeader("Content-Disposition",
				"attachment; filename=\"" + downloadFileName + "\";");
		String actualFileName = FsUtils.get("doc/" + file).getAbsolutePath();
		log.debug("actualFileName>>>" + actualFileName);
		byte[] buf = ResourceUtils.getBytes(actualFileName);
		return buf;
	}

	/**
	 * 获取附件,如果附件是保存在数据库中，则把附件保存到文件夹，然后返回路径，此方法用户公告发布
	 */
	public String getFilePath(String lobId) throws Exception {
		String filePath = "";
		if (StringUtils.isNotEmpty(lobId)) {
			DataSetDao dao = new DataSetDao();
			ParaMap keyData = new ParaMap();
			keyData.put("id", lobId);
			ParaMap sysLob = dao.querySimpleData("sys_lob", keyData);
			filePath = (String) sysLob.getRecordValue(0, "file_path");
			String fileName = (String) sysLob.getRecordValue(0, "file_name");// 文件名
			String name = (String) sysLob.getRecordValue(0, "name");// 文件重命名
			String eName = "";// 文件扩展名
			if (StringUtils.isNotEmpty(fileName)) {// 获取文件扩展名
				eName = fileName.substring(fileName.lastIndexOf("."),
						fileName.length());
			}
			if (StringUtils.isNotEmpty(name)) {
				fileName = name + "." + eName;
			}

			if (StringUtils.isEmpty(filePath)) {// 如果查询的文件路径为空，说明附件保存在数据库中
				byte[] fileByte = null;
				ParaMap sysBlobDtl = new ParaMap();// 用于存放附件子表信息
				sysBlobDtl.put("lobId", lobId);
				sysBlobDtl.put("moduleNo", moduleNo);
				sysBlobDtl.put("dataSetNo", "query_sys_blob_dtl");
				sysBlobDtl = dao.queryData(sysBlobDtl);
				fileByte = this.combinationFile(sysBlobDtl);
				// 把获取到的内容保存到指定的文件夹
				String saveFilePath = FsUtils.get(FsUtils.noticePath)
						.getAbsolutePath();
				saveFilePath = saveFilePath + "/" + fileName;// 把数据库中的文件写入此路径
				File fil = new File(saveFilePath);

				byte[] bt = new byte[1024];
				int len = 0;
				ByteArrayInputStream is = new ByteArrayInputStream(fileByte);
				FileOutputStream fos = new FileOutputStream(saveFilePath);
				while ((len = is.read(bt)) != -1) {
					fos.write(bt, 0, len);
				}
				fos.close();
				is.close();
				filePath = FsUtils.noticePath + "/" + fileName;
			}
		}
		return filePath;
	}

	/**
	 * 切割公告，把PDF的文件切割
	 */

	public void apartNotice(String lobId, String noticeId, String filePath)
			throws Exception {
		DataSetDao dao = new DataSetDao();
		String noticePath = FsUtils.get(filePath).getAbsolutePath();
		String swfFullDir = FsUtils.get(FsUtils.swfFullDir + "/" + noticeId)
				.getAbsolutePath();// 切割的公告
		Pdf2SwfUtils pdf2swf = new Pdf2SwfUtils();
		pdf2swf.pdf2swf(noticePath, swfFullDir);
		// 统计切割的文件个数
		File d = new File(swfFullDir);
		File list[] = d.listFiles();
		int fileCount = 0;
		for (int i = 0; i < list.length; i++) {
			if (list[i].isFile()) {
				fileCount++;
			}
		}
		// 把文件个数保存到扩展表中
		if (fileCount > 0) {
			ParaMap keyData = new ParaMap();
			keyData.put("id", null);
			keyData.put("ref_table_name", "sys_lob");
			keyData.put("ref_id", lobId);
			keyData.put("field_no", "page_count");
			keyData.put("field_value", fileCount);
			dao.updateData("sys_extend", "id",
					"ref_table_name,ref_id,field_no", keyData, null);
		}

	}

}
