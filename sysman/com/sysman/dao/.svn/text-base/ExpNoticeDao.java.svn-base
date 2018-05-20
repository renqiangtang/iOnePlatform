package com.sysman.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.base.dao.DataSetDao;
import com.base.utils.ParaMap;

/**
 * 
 * @author danqingluo
 *
 */
public class ExpNoticeDao
{

	private Connection con;
	DataSetDao dataDao = new DataSetDao();

	public ExpNoticeDao()
	{

	}

	/**
	 * 查询交易公告信息
	 * 
	 * @param inMap
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public ResultSet expNoticefore(ParaMap inMap) throws SQLException
	{
		StringBuffer sb = new StringBuffer();
		sb.append("select tn.* , ntr.target_id , decode(tn.notice_type , 0 , '普通公告' ,1 ,'补充公告' , 2,'紧急公告' ) notice_type_chs  from TRANS_NOTICE tn , TRANS_NOTICE_TARGET_REL ntr  where ntr.notice_id  =  tn.id ");
		String noticeId = inMap.getString("noticeId");
		if (noticeId != null && !noticeId.equals(""))
			sb.append("and  tn.id").append("=").append(" ? ");
		con = dataDao.getComCon();
		PreparedStatement pres;
		ResultSet result = null;
		try
		{
			pres = con.prepareStatement(sb.toString());
			pres.setString(1, noticeId);
			result = pres.executeQuery();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			con.close();

		}
		return result;
	}

	/**
	 * 查询交易规则
	 * 
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getTransTransactionTypeByTransType(String type)
	        throws SQLException
	{
		StringBuffer sb = new StringBuffer();
		sb.append("select * from trans_transaction_type where trans_type  = ? and is_net_trans= 1 ");
		con = dataDao.getComCon();
		PreparedStatement pres;
		ResultSet result = null;

		try
		{
			pres = con.prepareStatement(sb.toString());
			pres.setString(1, type);
			result = pres.executeQuery();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{

			con.close();
		}

		return result;
	}

	/**
	 * 查詢交易公告关联的标的信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ResultSet expTargetBefore(ParaMap inMap) throws Exception
	{
		StringBuffer sb = new StringBuffer();
		sb.append("select tt.*,tnr.notice_id , decode(tt.trans_type , 1 , '拍卖' , 0,'挂牌' , 2 ,'招标') trans_type_chs , ( select name from sys_organ where id = tt.trans_organ_id ) trans_organ_name  , (select user_name from sys_user where id = tt.create_user_id) create_user from trans_target tt , TRANS_NOTICE_TARGET_REL tnr   where 1 =1   and  tt.id = tnr.target_id  and tt.is_valid= 1");
		String noticeId = inMap.getString("noticeId");
		String status = inMap.getString("status");
		if (noticeId != null && !noticeId.equals(""))
			sb.append(" and  tnr.notice_id  = ? ");
		if (status != null && !status.equals(""))
			sb.append(" and status >= 5 ").append( " " );
		con = dataDao.getComCon();
		
		PreparedStatement pres;
		ResultSet result = null;
		try
		{
			pres = con.prepareStatement(sb.toString());
			pres.setString(1, noticeId);
			result = pres.executeQuery();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			con.close();

		}

		return result;
	}

	/**
	 * 查询标的关联的宗地信息
	 * 
	 * @param inMap
	 * @return
	 * @throws Exception
	 */
	public ResultSet expGoodsBefore(ParaMap inMap) throws Exception
	{
		StringBuffer sb = new StringBuffer();
		sb.append("select  tgr.target_id , tg.* from trans_target_goods_rel tgr , trans_goods tg where tgr.goods_id = tg.id ");
		String targetId = inMap.getString("target_id");
		if (targetId != null && !targetId.equals(""))
			sb.append(" and  tgr.target_id  =  ? ");
		con = dataDao.getComCon();
		PreparedStatement pres;
		ResultSet result = null;
		try
		{
			pres = con.prepareStatement(sb.toString());
			pres.setString(1, targetId);
			result = pres.executeQuery();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			con.close();

		}

		return result;
	}

	/**
	 * 获取宗地扩展信息
	 * 
	 * @param inMap
	 * @return
	 * @throws SQLException
	 */
	public Map expGoodsInfoBefore(String ref_id) throws SQLException
	{
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sys_extend  where 1 = 1 ");
		if (ref_id != null && !ref_id.equals(""))
			sb.append(" and  ref_id  = ?");
		PreparedStatement pres;
		ResultSet result = null;
		Map map = new HashMap();
		try
		{
			con = dataDao.getComCon();
			pres = con.prepareStatement(sb.toString());
			pres.setString(1, ref_id);
			result = pres.executeQuery();
			ResultSetMetaData meta = result.getMetaData();
			while (result.next())
			{
				map.put(result.getString("FIELD_NO"),
				        result.getString("FIELD_VALUE"));

			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			con.close();

		}
		// System.out.println(map);
		return map;
	}

	/**
	 * 查询公示中所有成功竞买人的信息
	 * 
	 * @param noticeId
	 * @return
	 * @throws SQLException
	 */
    public ResultSet expTargetLicenseByNoticeId(String noticeId)
	        throws SQLException
	{
		StringBuffer sb = new StringBuffer();
		sb.append("    select tb.*,tl.target_id , decode(tb.is_company,'0','个人','1','企业') is_company_chs  from trans_license tl , trans_bidder tb  where tl.target_id in (select target_id from trans_notice_target_rel where notice_id = ? ) and tl.status = '4' and tl.bidder_id = tb.id ");
		PreparedStatement pres;
		ResultSet result = null;
		Map map = new HashMap();
		try
		{
			con = dataDao.getComCon();
			pres = con.prepareStatement(sb.toString());
			pres.setString(1, noticeId);
			result = pres.executeQuery();
		}
		catch (Exception e)
		{
			e.printStackTrace();

		}
		finally
		{
			con.close();

		}
		return result;
	}
}
