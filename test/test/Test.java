package test;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import com.base.utils.IDGenerator;

public class Test {

	static String[] hz = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十" };

	public static void main(String[] args) throws Exception {
		String dir = "C:\\Photoshop经典案例完美表现200例\\";
		List list = FileUtils.readLines(new File(dir + "data\\movie.xml"));
		for (int i = 0; i < list.size(); i++) {
			String line = (String) list.get(i);
			line = StringUtils.trim(line);
			if (line.indexOf("data=") >= 0) {
				String s1 = "<node label=\"";
				int end = line.indexOf("\"", s1.length() + 1);
				String label = line.substring(s1.length(), end);

				s1 = "data=\"";
				int begin = line.indexOf(s1, 10);
				end = line.lastIndexOf("\"");
				String data = line.substring(begin + s1.length(), end);

				String f = dir + data + ".swf";
				File srcFile = new File(f);

				f = f.replaceAll("movie", "movie2");
				f = f.replaceAll(srcFile.getName(), label + ".swf");

				File destFile = new File(f);

				if (!destFile.getParentFile().exists()) {
					destFile.getParentFile().mkdir();
				}

				FileUtils.copyFile(srcFile, destFile);

				System.out.println(srcFile.getAbsolutePath());
				System.out.println(f);
			}
		}
	}

	public static void main3(String[] args) throws Exception {
		String dir = "E:\\media\\金鹰CorelDraw视频教程\\";
		String dir2 = "E:\\media\\aaa\\";
		String txt = "E:\\media\\coreldraw.txt";
		String fileDir = "";
		List list = FileUtils.readLines(new File(txt));
		for (int i = 0; i < list.size(); i++) {
			String title = String.valueOf(list.get(i));
			System.out.println(title);
			if (title.indexOf("第") >= 0) {
				fileDir = title.trim();
				FileUtils.forceMkdir(new File(dir2 + fileDir));
			} else {
				String[] ss = title.split(". ");
				File from = new File(dir + ss[0] + ".swf");
				File to = new File(dir2 + fileDir + "//" + title + ".swf");
				System.out.println("from:" + from.getAbsolutePath());
				System.out.println("to:" + to.getAbsolutePath());
				FileUtils.copyFile(from, to);
			}
		}
	}

	public static void main2(String[] args) throws Exception {
		HashSet<String> set = new HashSet<String>();
		for (int i = 0; i < 1000; i++) {
			String s = IDGenerator.local();
			if (set.contains(s))
				System.out.println(s);
		}
	}

	public static void main1(String[] args) throws Exception {
		byte[] s1 = FileUtils.readFileToByteArray(new File(
				"c:/深圳壹平台信息技术有限公司[77]交易记录.lic"));
		String s2 = new String(s1);
		s2 = s2.replaceAll("\\\\n", "\n");
		FileUtils.writeStringToFile(new File("c:/aa.lic"), s2);
		System.out.println(s2);
	}
}
