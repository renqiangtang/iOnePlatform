package com.base.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.base.dao.BaseDao;
import com.base.dao.DataSetDao;
import com.base.utils.FsUtils;
import com.base.utils.ParaMap;
import com.base.utils.StrUtils;
import com.base.web.AppConfig;

public class BaseUploadService extends BaseService {
	private final static String QUERY_MAX_DATA_INDEX = "query_max_data_index";
	private final static String moduleNo = "sys_lob_manager";
	public final static long blockSize = new Long("1048576");// 每个块的大小

	/**
	 * 上传文件
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap uploadFile(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String fileName = inMap.getString("txtFile");
		byte[] buf = inMap.getBytes(fileName);
		String storeType = AppConfig.getPro("fileStoreType").toLowerCase();
		// 保存文件
		if ("disk".equals(storeType)) {
			// 如果路径不为空,就保存到硬盘,把路径记录到sys_lob表中
			out = savaToDisk(inMap);
		} else {
			// 保存到数据库
			String filePath = FsUtils.get("/temp").getAbsolutePath();
			File file = new File(filePath + "/" + fileName);
			FileUtils.writeByteArrayToFile(file, buf);
			out = savaToDb(file, inMap, fileName);
		}
		return out;
	}

	/**
	 * 获取文件分块数
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public int getBlockNum(File file) {
		long fileSize = file.length();
		if (fileSize <= blockSize) {// 如果文件大小小于或等于每块的大小,只能够分一块
			return 1;
		} else {
			if (fileSize % blockSize > 0) {
				return (int) (fileSize / blockSize + 1);
			} else {
				return (int) (fileSize / blockSize);
			}
		}
	}

	/**
	 * 把文件分割保存到数据库
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap savaToDb(File file, ParaMap inMap, String fileName)
			throws Exception {
		ParaMap result = new ParaMap();// 存放返回信息
		DataSetDao dao = new DataSetDao();
		BaseDao upLoadDao = new BaseDao();
		// 先保存主表
		String refId = inMap.getString("refId");// 数据来源表ID
		if (refId == null || "".equals(refId) || "null".equals(refId)) {
			result.put("state", 0);
			result.put("message", "数据来源为空,不能上传附件!");
			return result;
		}
		String lobId = "";// 主表ID
		ParaMap saveSysLobMap = this.saveSysLob(inMap, "", "");
		if (saveSysLobMap.getString("message") != null
				&& !"".equals(saveSysLobMap.getString("message"))
				&& !"null".equals(saveSysLobMap.getString("message"))) {
			result.put("state", 0);
			result.put("message", saveSysLobMap.getString("message"));
			return result;
		}
		lobId = saveSysLobMap.getString("lobId");
		// 通过定义的分割大小分割文件，然后保存到SYS_BLOB_DTL
		long fileSize = file.length();
		int blockNum = this.getBlockNum(file);// 得到分块数
		long writerSize = 0;// 每次写入的字节
		long writerTotel = 0;// 已经写入的字节
		for (int i = 0; i < blockNum; i++) {
			// 先保存SYS_BLOB_DTL信息，然后在把BLOB字段更新进去
			ParaMap sysBlobDtlMap = new ParaMap();
			sysBlobDtlMap.put("id", "");
			sysBlobDtlMap.put("lob_id", lobId);
			sysBlobDtlMap.put("part", i);
			ParaMap saveMap = new ParaMap();
			saveMap = dao.updateData("sys_blob_dtl", "id", sysBlobDtlMap, null);
			String dtlId = "";// 子表ID
			if (!"1".equals(saveMap.getString("state"))) {
				result.put("state", 0);
				result.put("message", "保存BLOB子表错误");
				return result;
			} else {
				dtlId = saveMap.getString("id");
				if (dtlId == null || "".equals(dtlId) || "null".equals(dtlId)) {
					result.put("state", 0);
					result.put("message", "BLOB子表ID为空");
					return result;
				}
			}
			// 把BLOB更新到刚才保存的这条子记录中
			if (i < blockNum) {
				writerSize = blockSize;
			} else {
				writerSize = fileSize - writerTotel;
			}
			if (blockNum == 1) {// 一次性保存
				FileInputStream inputStream = new FileInputStream(file);
				upLoadDao.updateBlobContent("sys_blob_dtl", "id", dtlId,
						"content", inputStream);
				inputStream.close();
			} else {
				byte[] bt = new byte[1024];
				int len = 0;
				long writeByte = 0;
				RandomAccessFile raf = null;
				raf = new RandomAccessFile(file, "r");
				raf.seek(writerTotel);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				while ((len = raf.read(bt)) > 0) {
					if (writeByte < writerSize) {// 如果当前块还没有写满
						writeByte = writeByte + len;
						if (writeByte <= writerSize) {
							out.write(bt, 0, len);
						} else {
							len = (int) (len - (writeByte - writerSize));
							out.write(bt, 0, len);
						}
					}
				}
				ByteArrayInputStream is = new ByteArrayInputStream(
						out.toByteArray());
				upLoadDao.updateBlobContent("sys_blob_dtl", "id", dtlId,
						"content", is);
				out.close();
				raf.close();
				is.close();
				writerTotel = writerTotel + writerSize;
			}

		}
		result.put("state", 1);
		result.put("message", "上传成功!");
		return result;
	}

	/**
	 * 保存到硬盘
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap savaToDisk(ParaMap inMap) throws Exception {
		ParaMap result = new ParaMap();
		String subDir = inMap.getString("subDir");
		String fileName = inMap.getString("txtFile" + "_Name");
		byte[] fileBuf = inMap.getBytes("txtFile");
		String filePath = subDir + "/" + FsUtils.genFileName(fileName);
		File toFile = new File(AppConfig.getPro("fileRoot") + "/" + filePath);
		FileUtils.writeByteArrayToFile(toFile, fileBuf);
		ParaMap saveMap = this.saveSysLob(inMap, fileName, filePath);
		if (saveMap.getInt("state") == 0) {
			result.put("state", 0);
			result.put("message", saveMap.getString("message"));
		} else {
			result.put("state", 1);
			result.put("message", "上传成功");
		}
		return result;
	}

	/**
	 * 保存附件主表
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap saveSysLob(ParaMap inMap, String fileName, String filePath)
			throws Exception {
		ParaMap returnMap = new ParaMap();
		// 把上传的文件保存到附件表中sys_lob
		DataSetDao dao = new DataSetDao();
		String reName = inMap.getString("reName");
		String info = inMap.getString("info");
		String refTableName = inMap.getString("refTableName");// 数据来源表名
		String refId = inMap.getString("refId");// 数据来源表ID为空，不让上传附件
		// 根据数据来源表名和数据来源表ID查询最大的DATA_INDEX(数据索引，指一个ref_id值对应多个大字段时，从1开始)
		ParaMap maxIndexMap = new ParaMap();
		maxIndexMap.put("moduleNo", moduleNo);
		maxIndexMap.put("dataSetNo", QUERY_MAX_DATA_INDEX);
		maxIndexMap.put("refId", refId);
		maxIndexMap.put("refTableName", refTableName);
		maxIndexMap = dao.queryData(maxIndexMap);
		if (!"1".equals(maxIndexMap.getString("state"))) {
			returnMap.put("state", 0);
			returnMap.put("message", "查询最大数据索引出错!");
			return returnMap;
		}
		Integer curIndex = (Integer) maxIndexMap
				.getRecordValue(0, "data_index");
		// 基数从1开始
		int maxIndex = 1;
		if (curIndex != null) {
			// 最大数据索引
			maxIndex = curIndex + 1;
		}
		// 保存主表
		ParaMap saveLobMap = new ParaMap();
		saveLobMap.put("id", "");
		saveLobMap.put("ref_table_name", refTableName);
		saveLobMap.put("ref_id", refId);
		saveLobMap.put("data_type", 0);
		saveLobMap.put("name", reName);
		saveLobMap.put("file_name", fileName);
		saveLobMap.put("information", info);
		saveLobMap.put("data_index", maxIndex);
		saveLobMap.put("file_path", filePath);
		ParaMap sysLobMap = dao.updateData("sys_lob", "id", saveLobMap, null);
		if ("1".equals(sysLobMap.getString("state"))) {
			String lobId = sysLobMap.getString("id");
			if (lobId != null && !"".equals(lobId) && !"null".equals(lobId)) {
				returnMap.put("state", 1);
				returnMap.put("lobId", lobId);
			} else {
				returnMap.put("state", 0);
				returnMap.put("message", "主表ID为空!");
			}
		} else {
			returnMap.put("state", 0);
			returnMap.put("message", "保存附件主表错误!");
		}
		return returnMap;
	}

	/**
	 * 查询附件
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getSysLob(ParaMap inMap) throws Exception {
		DataSetDao dao = new DataSetDao();
		ParaMap out = new ParaMap();
		String dataSetNo = "query_sys_lob";// 数据集编号
		String refTableName = inMap.getString("refTableName");
		String refId = inMap.getString("refId");
		ParaMap sysLobMap = new ParaMap();
		sysLobMap.put("moduleNo", moduleNo);
		sysLobMap.put("dataSetNo", dataSetNo);
		sysLobMap.put("refTableName", refTableName);
		sysLobMap.put("refId", refId);
		sysLobMap = dao.queryData(sysLobMap);
		int state = sysLobMap.getInt("state");
		if (state == 1) {// 如果执行成功
			out = this.getJson(sysLobMap);
		}
		return out;
	}

	/**
	 * 封装成JSON格式
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap getJson(ParaMap inMap) {
		List fsList = (List) inMap.get("fs");
		List rsList = inMap.getList("rs");// 所有的记录集
		ParaMap out = new ParaMap();// 用于存放封装好的记录,返回
		List list = new ArrayList();// 用于存放每条记录
		if (fsList != null && rsList != null) {
			for (int a = 0; a < rsList.size(); a++) {
				List rowsList = (List) rsList.get(a);// 取到每条记录
				if (rowsList != null) {
					ParaMap out1 = new ParaMap();// 用于分装每条记录
					for (int b = 0; b < rowsList.size(); b++) {
						if ("file_name".equals(String.valueOf(fsList.get(b)))) {// 把扩展名也返回到前台
							String extendName = String.valueOf(rowsList.get(b));
							String extendType = extendName.substring(
									extendName.lastIndexOf("."),
									extendName.length());
							out1.put("extendType", extendType);
						}
						out1.put(String.valueOf(fsList.get(b)),
								String.valueOf(rowsList.get(b)));
					}
					list.add(out1);
				}
			}
		}
		out.put("Rows", list);
		return out;
	}

	/**
	 * 删除附件
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ParaMap delSysLob(ParaMap inMap) throws Exception {
		DataSetDao dao = new DataSetDao();
		ParaMap returnMap = new ParaMap();
		String lobId = inMap.getString("lobId");// 如果是上传到数据库的，就用附件主表ID来删除
		if (lobId == null || "".equals(lobId) || "null".equals(lobId)) {
			returnMap.put("state", 0);
			returnMap.put("message", "附件主表ID为空!");
			return returnMap;
		}
		// 查询主表
		ParaMap keyData = new ParaMap();
		keyData.put("id", lobId);
		ParaMap sysLob = dao.querySimpleData("sys_lob", keyData);
		String filePath = (String) sysLob.getRecordValue(0, "file_path");
		// 删除主表记录
		ParaMap delData = new ParaMap();
		delData.put("id", lobId);
		delData = dao.deleteSimpleData("sys_lob", delData);
		if (delData.getInt("state") == 1) {
			if (filePath != null && !"".equals(filePath)
					&& !"null".equals(filePath)) {// 说明附件存储在硬盘,把硬盘上的附件删除
				String rootPath = AppConfig.getPro("fileRoot");// 得到配置文件中的配置路径
				if (StrUtils.isNotNull(rootPath)) {
					if (rootPath.indexOf("\\") >= 0) {
						if (!rootPath.endsWith("\\"))
							rootPath += "\\";
					} else if (rootPath.indexOf("/") >= 0) {
						if (!rootPath.endsWith("/"))
							rootPath += "/";
					}
				}
				if (filePath.startsWith("/") || filePath.startsWith("\\"))
					filePath = filePath.substring(1);
				filePath = rootPath + filePath;
				File file = new File(filePath);
				boolean b = file.delete();
				if (b) {
					returnMap.put("state", 1);
					returnMap.put("message", "删除成功!");
				} else {
					returnMap.put("state", 0);
					returnMap.put("message", "删除失败,没有找到文件!");
				}
			} else {
				returnMap.put("state", 1);
				returnMap.put("message", "删除成功!");// 说明存储在附件表中的附件删除成那个
			}
		} else {
			returnMap.put("state", 0);
			returnMap.put("message", delData.getString("message"));
		}
		return returnMap;
	}

}
