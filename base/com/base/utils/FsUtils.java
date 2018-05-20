package com.base.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.base.web.AppConfig;

public class FsUtils {

	private static Logger log = Logger.getLogger(FsUtils.class);

	public static final String receiveData = "receiveData";
	public static final String noticePath = "temp";
	public static final String swfFullDir = "/swf/notice/";
	public static final String configPath = "config";
	public static final String commonPath = "公共";

	public static String getFileRoot() {// 得到配置文件中的配置路径
		return AppConfig.getPro("fileRoot");
	}

	public static File get(String path) throws Exception {
		String tempPath = path;
		if (!path.startsWith("/"))
			tempPath = "/" + path;
		File file = new File(getFileRoot() + tempPath);
		if (!file.exists())
			FileUtils.forceMkdir(file);
		return file;
	}

	/**
	 * 
	 * @param path
	 * @param recursion
	 *            递归在上层的 公共 目录查找
	 * @return
	 * @throws Exception
	 */
	public static File getFile(String path, boolean recursion) throws Exception {
		File file = new File(path);
		if (!file.exists() && recursion) {
			String parentPath = file.getParent();
			String fileName = file.getName();
			if (parentPath.equals(getFileRoot()))
				return null;
			String p = file.getParentFile().getParent() + "/" + fileName;
			file = getFile(p, recursion);
		}
		return file;
	}

	/**
	 * 生成文件名
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static String genFileName(String fileName) throws Exception {
		int index = fileName.lastIndexOf(".");
		String fileN = null;
		String fileE = null;
		if (index >= 0) {
			fileN = fileName.substring(0, index);
			fileE = fileName.substring(index + 1);
		} else {
			fileN = fileName;
		}
		if (StringUtils.isNotEmpty(fileE)) {
			return fileN + "[" + IDGenerator.newGUID() + "]." + fileE;
		} else {
			return fileN + "[" + IDGenerator.newGUID() + "]";
		}
	}

	/**
	 * 获取config目录指定文本文件内容 不递归
	 * 
	 * @param path1
	 * @return
	 * @throws Exception
	 */
	public static String getConfigFileString(String path1) throws Exception {
		return getConfigFileString(path1, false);
	}

	/**
	 * 获取config目录指定文本文件内容
	 * 
	 * @param path1
	 * @param recursion
	 *            递归选项
	 * @return
	 * @throws Exception
	 */
	public static String getConfigFileString(String path1, boolean recursion)
			throws Exception {
		File file = getFile(getFileRoot() + "/" + configPath + "/" + path1,
				recursion);
		String content = null;
		if (file.exists())
			content = FileUtils.readFileToString(file, CharsetUtils.utf);
		return content;
	}

	/**
	 * 获取config目录指定二进制文件内容 不递归
	 * 
	 * @param path1
	 * @param recursion
	 * @return
	 * @throws Exception
	 */
	public static byte[] getConfigFileByte(String path1) throws Exception {
		return getConfigFileByte(path1, false);
	}

	/**
	 * 获取config目录指定二进制文件内容
	 * 
	 * @param path1
	 * @param recursion
	 *            递归选项
	 * @return
	 * @throws Exception
	 */
	public static byte[] getConfigFileByte(String path1, boolean recursion)
			throws Exception {
		File file = getFile(getFileRoot() + "/" + configPath + "/" + path1,
				recursion);
		byte[] buf = null;
		if (file.exists()) {
			buf = FileUtils.readFileToByteArray(file);
		}
		return buf;
	}

	public static void main(String[] args) throws Exception {
	}
}
