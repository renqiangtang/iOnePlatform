package com.trademan.service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;

import com.base.service.BaseService;
import com.base.utils.CharsetUtils;
import com.base.utils.FileAccessUtils;
import com.base.utils.FsUtils;
import com.base.utils.ParaMap;

public class DocConfigService extends BaseService {

	public File getFile(String home, String path) {
		return new File(FsUtils.getFileRoot() + "/" + home + "/" + path);
	}

	public ParaMap getFileInfo(String home, File file) {
		ParaMap info = new ParaMap();
		info.put("name", file.getName());
		String path = file.getAbsolutePath();
		int index = path.indexOf("fileRoot");
		path = path.substring(index + "fileRoot".length() + 1);
		index = path.indexOf(home);
		path = path.substring(index + home.length() + 1);
		info.put("date", file.lastModified());
		info.put("size", file.length());
		if (file.isDirectory()) {// 目录
			info.put("iconSkin", "dir");
			info.put("isParent", true);
		} else {// 文件
			info.put("iconSkin", "file");
			info.put("isParent", false);
		}
		path = path.replace("\\", "/");
		info.put("path", path);
		return info;
	}

	public ParaMap getList(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String home = inMap.getString("home");
		String path = inMap.getString("path");
		System.out.println(">>>>"+path+" "+CharsetUtils.getEncoding(path));
		if (CharsetUtils.iso.equals(CharsetUtils.getEncoding(path)))
			path = CharsetUtils.getString(path);
		if (CharsetUtils.iso.equals(CharsetUtils.getEncoding(path)))
			home = CharsetUtils.getString("home");
		int type = inMap.getInt("fsType");
		List list = new ArrayList();
		File[] files = getFile(home, path).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				int index = name.indexOf(".svn");
				return index < 0;
			}
		});
		for (int i = 0; i < files.length && files != null
				&& (type == 0 || type == 1); i++) {// 目录
			if (files[i].isDirectory())
				list.add(getFileInfo(home, files[i]));
		}
		for (int i = 0; i < files.length && files != null
				&& (type == 0 || type == 2); i++) {// 文件
			if (files[i].isFile())
				list.add(getFileInfo(home, files[i]));
		}
		out.put("rs", list);
		out.put("state", 1);
		return out;
	}

	public ParaMap getContent(ParaMap inMap) throws Exception {
		String home = inMap.getString("home");
		String path = inMap.getString("path");
		File file = getFile(home, path);
		String content = FileUtils.readFileToString(file);
		ParaMap out = new ParaMap();
		out.put("state", 1);
		out.put("content", content);
		return out;
	}

	public ParaMap updateContent(ParaMap inMap) throws Exception {
		String home = inMap.getString("home");
		String path = inMap.getString("path");
		File file = getFile(home, path);
		String content = inMap.getString("content");
		FileUtils.writeStringToFile(file, content);
		ParaMap out = new ParaMap();
		out.put("state", 1);
		return out;
	}

	public ParaMap newFile(ParaMap inMap) throws Exception {
		String home = inMap.getString("home");
		String newName = inMap.getString("newName");
		String path = inMap.getString("path");
		boolean isDir = inMap.getBoolean("isDir");
		File file = getFile(home, path + "/" + newName);
		System.out.println("newDir[file]:" + isDir + "  "
				+ file.getAbsolutePath());
		if (isDir)
			FileUtils.forceMkdir(file);
		else
			FileUtils.writeStringToFile(file, "");
		ParaMap out = new ParaMap();
		out.put("fileInfo", getFileInfo(home, file));
		out.put("state", 1);
		return out;
	}

	public ParaMap delFile(ParaMap inMap) throws Exception {
		String home = inMap.getString("home");
		String path = inMap.getString("path");
		File file = getFile(home, path);
		System.out.println("delFile[file]:" + file.getAbsolutePath());
		FileUtils.forceDelete(file);
		ParaMap out = new ParaMap();
		out.put("state", 1);
		return out;
	}

	public ParaMap moveFile(ParaMap inMap) throws Exception {
		String home = inMap.getString("home");
		String file = inMap.getString("file");
		String toDir = inMap.getString("toDir");
		File srcFile = getFile(home, file);
		File destFile = getFile(home, toDir + "/" + srcFile.getName());
		if (srcFile.isDirectory())
			FileUtils.moveDirectory(srcFile, destFile);
		else
			FileUtils.moveFile(srcFile, destFile);
		ParaMap out = new ParaMap();
		out.put("state", 1);
		return out;
	}

	public ParaMap reName(ParaMap inMap) throws Exception {
		String home = inMap.getString("home");
		String newName = inMap.getString("newName");
		String oldName = inMap.getString("oldName");
		String path = inMap.getString("path");
		File srcFile = getFile(home, path + "/" + oldName);
		File destFile = getFile(home, path + "/" + newName);
		log.debug("reName[src]:" + srcFile.getAbsolutePath());
		log.debug("reName[dest]:" + destFile.getAbsolutePath());
		if (srcFile.isDirectory())
			FileUtils.moveDirectory(srcFile, destFile);
		else
			FileUtils.moveFile(srcFile, destFile);
		ParaMap out = getFileInfo(home, destFile);
		out.put("state", 1);
		return out;
	}

	public ParaMap upload(ParaMap inMap) throws Exception {
		ParaMap out = new ParaMap();
		String fileName = inMap.getString("txtFile_Name");
		FileItem fileItem = inMap.getFileItem("txtFile_Item");
		String home = inMap.getString("home");
		String path = inMap.getString("path");
		File file = getFile(home, path + "/" + fileName);
		if (!file.exists())
			FileUtils.writeStringToFile(file, "");
		fileItem.write(file);
		out.put("state", 1);
		return out;
	}

	public byte[] directDownload(ParaMap inMap) throws Exception {
		String path = inMap.getString("path");
		log.debug("directDownLoad file>>>" + path);
		path = new String(path.getBytes(CharsetUtils.iso), CharsetUtils.utf);
		if(path.indexOf("???")!=-1){//出现？？？的乱码
			path = inMap.getString("path");
		}
		File file = getFile("", path);
		log.debug("directDownload path>>>" + file.getAbsolutePath());
		String downloadFileName = new String(file.getName().getBytes("gb2312"),
				"ISO8859-1");
		byte[] buf = FileUtils.readFileToByteArray(file);
		this.getResponse().reset();
		this.getResponse().setContentType(
				"application/octet-stream; charset=UTF-8");
		this.getResponse().addHeader("Content-Disposition",
				"attachment; filename=\"" + downloadFileName + "\";");
		return buf;
	}
	public byte[] directDownloadPdf(ParaMap inMap) throws Exception {
		String path = inMap.getString("path");
		String name = inMap.getString("name");
		path = new String(path.getBytes(CharsetUtils.iso), CharsetUtils.utf);
		File file = getFile("", path);
		FileAccessUtils.checkDownload(file);
		byte[] buf = FileUtils.readFileToByteArray(file);
		this.getResponse().reset();
		this.getResponse().setContentType("application/octet-stream; charset=UTF-8");
		this.getResponse().addHeader("Content-Disposition", "attachment; filename=\"" + name + "\";");
		return buf;
	}


}
