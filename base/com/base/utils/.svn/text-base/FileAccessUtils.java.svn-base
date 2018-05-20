package com.base.utils;

import java.io.File;

import com.base.web.AppConfig;

public class FileAccessUtils {

	public static void checkDownload(File file) throws Exception {
		String path = file.getCanonicalPath();
		String fileRoot = AppConfig.getPro("fileRoot");
		if (!path.startsWith(fileRoot))
			throw new Exception("非法操作:[" + path + "]不能访问");
	}

	public static void checkUpload(File file) throws Exception {
		File dir = new File(".");
		String path = file.getCanonicalPath();
		if (path.startsWith(dir.getCanonicalPath()))
			throw new Exception("非法操作:[" + path + "]不能上传到应用服务器");
		if (path.endsWith(".jsp") || path.endsWith(".class") || path.endsWith(".js") || path.endsWith(".css") || path.endsWith(".exe") || path.endsWith(".bat"))
			throw new Exception("非法操作:[" + path + "]为非法上传文件");
	}

}
