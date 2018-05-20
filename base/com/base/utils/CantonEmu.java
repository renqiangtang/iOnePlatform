package com.base.utils;

/**
 * 
 * @author 行政区划枚举类
 * 
 */
public enum CantonEmu
{
	SHENGZ("省直", "00", ""), SHIZ("市直", "02", "370102"), LXQ("历下区", "02",
	        "370102"), SZQ("市中区", "03", "370103"), PYQ("平阴县", "17", "370124"), GYQ(
	        "槐荫区", "04", "370104"), TQQ("天桥区", "05", "370105"), GXQ("", "", ""), LCQ(
	        "历城区", "12", "370112"), CQQ("长清区", "13", "370113"), SW("", "", ""), BSWS(
	        "", "", ""), SQS("章丘市", "14", "370181"), JYX("济阳县", "15", "370125"), SHX(
	        "商河县", "16", "370126");

	public String name;
	public String value;
	public String value1;

	private CantonEmu(String name, String value, String value1)
	{
		this.name = name;
		this.value = value;
		this.value1 = value1;
	}

	/**
	 * 通过系统内编码得到接口需要的编码
	 * 
	 * @param value1
	 * @return
	 */
	public static String getValue(String value1)
	{
		CantonEmu[] c = CantonEmu.values();
		for (int i = 0; i < c.length; i++)
		{
			if (value1.equals(c[i].value1))
			{
				return c[0].value;

			}

		}
		return null;

	}

}
