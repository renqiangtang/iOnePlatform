package com.after.service;
public class Change {
	/**
	 * 数字金额大写转换，思想先写个完整的然后将如零拾替换成零
	 * 要用到正则表达式
	 */
	public static String digitUppercase(double n){
		String fraction[] = {"角", "分"};
	    String digit[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
	    String unit[][] = {{"元", "万", "亿"},
	                 {"", "拾", "佰", "仟"}};

	    String head = n < 0? "负": "";
	    n = Math.abs(n);
	    String s = "";
	    for (int i = 0; i < fraction.length; i++) {
	        s += (digit[(int)(Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
	    }
	    if(s.length()<1){
		    s = "整";	
	    }
	    int integerPart = (int)Math.floor(n);

	    for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
	        String p ="";
	        for (int j = 0; j < unit[1].length && n > 0; j++) {
	            p = digit[integerPart%10]+unit[1][j] + p;
	            integerPart = integerPart/10;
	        }
	        s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
	    }
	    return head + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
	}
	public static String stringToChineseSmall(String dateStr) {
		  String resultStr = "";
		  String[] ss = dateStr.split("-");

		  for (int j = 0; j < ss[0].length(); j++) {
		   switch (ss[0].charAt(j)) {
		   case '0':
		    resultStr += "〇";
		    break;
		   case '1':
		    resultStr += "一";
		    break;
		   case '2':
		    resultStr += "二";
		    break;
		   case '3':
		    resultStr += "三";
		    break;
		   case '4':
		    resultStr += "四";
		    break;
		   case '5':
		    resultStr += "五";
		    break;
		   case '6':
		    resultStr += "六";
		    break;
		   case '7':
		    resultStr += "七";
		    break;
		   case '8':
		    resultStr += "八";
		    break;
		   case '9':
		    resultStr += "九";
		    break;
		   }
		  }
		  resultStr = resultStr + "年";

		  if (ss[1].equals("01")) {
		   resultStr += "一";
		  } else if (ss[1].equals("02")) {
		   resultStr += "二";
		  } else if (ss[1].equals("03")) {
		   resultStr += "三";
		  } else if (ss[1].equals("04")) {
		   resultStr += "四";
		  } else if (ss[1].equals("05")) {
		   resultStr += "五";
		  } else if (ss[1].equals("06")) {
		   resultStr += "六";
		  } else if (ss[1].equals("07")) {
		   resultStr += "七";
		  } else if (ss[1].equals("08")) {
		   resultStr += "八";
		  } else if (ss[1].equals("09")) {
		   resultStr += "九";
		  } else if (ss[1].equals("10")) {
		   resultStr += "十";
		  } else if (ss[1].equals("11")) {
		   resultStr += "十一";
		  } else {
		   resultStr += "十二";
		  }
		  resultStr += "月";

		  if (ss[2].equals("01")) {
		   resultStr += "一";
		  } else if (ss[2].equals("02")) {
		   resultStr += "二";
		  } else if (ss[2].equals("03")) {
		   resultStr += "三";
		  } else if (ss[2].equals("04")) {
		   resultStr += "四";
		  } else if (ss[2].equals("05")) {
		   resultStr += "五";
		  } else if (ss[2].equals("06")) {
		   resultStr += "六";
		  } else if (ss[2].equals("07")) {
		   resultStr += "七";
		  } else if (ss[2].equals("08")) {
		   resultStr += "八";
		  } else if (ss[2].equals("09")) {
		   resultStr += "九";
		  } else if (ss[2].equals("10")) {
		   resultStr += "十";
		  } else if (ss[2].equals("11")) {
		   resultStr += "十一";
		  } else if (ss[2].equals("12")) {
		   resultStr += "十二";
		  } else if (ss[2].equals("13")) {
		   resultStr += "十三";
		  } else if (ss[2].equals("14")) {
		   resultStr += "十四";
		  } else if (ss[2].equals("15")) {
		   resultStr += "十五";
		  } else if (ss[2].equals("16")) {
		   resultStr += "十六";
		  } else if (ss[2].equals("17")) {
		   resultStr += "十七";
		  } else if (ss[2].equals("18")) {
		   resultStr += "十八";
		  } else if (ss[2].equals("19")) {
		   resultStr += "十九";
		  } else if (ss[2].equals("20")) {
		   resultStr += "二十";
		  } else if (ss[2].equals("21")) {
		   resultStr += "二十一";
		  } else if (ss[2].equals("22")) {
		   resultStr += "二十二";
		  } else if (ss[2].equals("23")) {
		   resultStr += "二十三";
		  } else if (ss[2].equals("24")) {
		   resultStr += "二十四";
		  } else if (ss[2].equals("25")) {
		   resultStr += "二十五";
		  } else if (ss[2].equals("26")) {
		   resultStr += "二十六";
		  } else if (ss[2].equals("27")) {
		   resultStr += "二十七";
		  } else if (ss[2].equals("28")) {
		   resultStr += "二十八";
		  } else if (ss[2].equals("29")) {
		   resultStr += "二十九";
		  } else if (ss[2].equals("30")) {
		   resultStr += "三十";
		  } else if (ss[2].equals("31")) {
		   resultStr += "三十一";
		  }
		  resultStr += "日";
		  return resultStr;
		 }
}
