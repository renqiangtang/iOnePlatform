package com.sysman.service;

import java.sql.ResultSet;

import com.base.service.BaseService;
import com.base.utils.ParaMap;
import com.base.utils.XMLUtils;
import com.sysman.dao.ExpNoticeDao;

public class ExpNotcieService extends BaseService
{

	/**
	 * 拼装交易前公告信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public String expNoticeBefore(String noticeID) throws Exception
	{
		ExpNoticeDao expNotice = new ExpNoticeDao();
		ParaMap paraMap = new ParaMap();
		paraMap.put("noticeId", noticeID);
		ResultSet result = expNotice.expNoticefore(paraMap);
		String resultStr = XMLUtils.ParaMapToXMLNoticeBefore(result);
		System.out.println(resultStr);
		return "";// XMLUtils.sendWebServiceXml(resultStr, "before");
	}

	/**
	 * 拼装交易后公示信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public String expNoticeAfter(String noticeID) throws Exception
	{
		ExpNoticeDao expNoticeDao = new ExpNoticeDao();
		ParaMap paraMap = new ParaMap();
		paraMap.put("noticeId", noticeID);
		ResultSet result = expNoticeDao.expNoticefore(paraMap);
		String resultStr = XMLUtils.paraMapToXMLNoticeAfter(result);
		System.out.println(resultStr);
		return "";// XMLUtils.sendWebServiceXml(resultStr, "after");

	}

	public static void main(String[] args)
	{
		ExpNotcieService ens = new ExpNotcieService();
		ParaMap pa = new ParaMap();
		pa.put("noticeId", "20130425095014578480");
		try
		{
			ens.expNoticeBefore("20130425095014578480");
			pa.put("noticeId", "20130425100407859446");
			ens.expNoticeAfter("20130425095014578480");
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
