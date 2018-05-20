package com.base.ant;

import java.io.File;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ExportDataSetXml
{
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    String str1 = "jdbc:oracle:thin:@192.168.1.201:1521:ORCL";
    String str2 = "transaction_heyuan";
    String str3 = "transaction_heyuan";
    String str4 = "release/web/WEB-INF/classes/ds.xml";
    if (paramArrayOfString.length == 4)
    {
      str1 = paramArrayOfString[0];
      str2 = paramArrayOfString[1];
      str3 = paramArrayOfString[2];
      str4 = paramArrayOfString[3];
    }
    System.out.println("开始导出数据集...");
    System.out.println("数据库配置:" + str1);
    System.out.println("用户名:" + str2);
    System.out.println("密码:" + str3);
    System.out.println("输出文件:" + str4);
    Class.forName("oracle.jdbc.driver.OracleDriver");
    Connection localConnection = DriverManager.getConnection(str1, str2, str3);
    HashMap localHashMap = new HashMap();
    localHashMap.put("syncModuleData", "1");
    localHashMap.put("syncDataSetData", "1");
    localHashMap.put("syncResourceData", "0");
    ExportDataSetXml localExportDataSetXml = new ExportDataSetXml();
    String str5 = localExportDataSetXml.getSyncDataXml(localConnection, localHashMap);
    FileUtils.writeStringToFile(new File(str4), str5,"UTF-8");
    System.out.println("成功导出数据集...");
  }

  public String getSyncDataXml(Connection paramConnection, HashMap paramHashMap)
    throws Exception
  {
    String str1 = (String)paramHashMap.get("syncModuleId");
    String str2 = (String)paramHashMap.get("syncDataSetId");
    String str3 = (String)paramHashMap.get("syncResourceId");
    boolean bool1 = paramHashMap.get("syncModuleData").equals("1");
    boolean bool2 = paramHashMap.get("syncDataSetData").equals("1");
    boolean bool3 = paramHashMap.get("syncResourceData").equals("1");
    if ((!bool1) && (!bool2) && (!bool3))
      return null;
    String str4 = null;
    String str5 = null;
    String str6 = null;
    HashMap localHashMap1 = null;
    Object localObject1 = null;
    Object localObject2 = null;
    if ((isNotNull(str1)) || (isNotNull(str2)) || (isNotNull(str3)))
    {
      if (isNotNull(str1))
      {
        str4 = "select * from (select sys_module.*, '0_' || (1000 - level) as module_level from sys_module CONNECT BY id = PRIOR parent_id start with id = '" + str1 + "'" + " union select sys_module.*, '1_' || level as module_level from sys_module CONNECT BY parent_id = PRIOR id start with parent_id = '" + str1 + "'" + ") order by module_level, turn";
        str5 = "select * from sys_dataset where module_id = '" + str1 + "' or module_id in (select id from sys_module CONNECT BY parent_id = PRIOR id start with parent_id = '" + str1 + "')";
        str6 = "select * from sys_resource where module_id = '" + str1 + "' or module_id in (select id from sys_module CONNECT BY parent_id = PRIOR id start with parent_id = '" + str1 + "')";
        localHashMap1 = new HashMap();
      }
      else if (isNotNull(str2))
      {
        str4 = "select * from (select sys_module.*, level as module_level from sys_module CONNECT BY id = PRIOR parent_id start with id in (select module_id from sys_dataset where id = '" + str2 + "')" + ") order by module_level desc, turn";
        str5 = "select * from sys_dataset where id = '" + str2 + "'";
      }
      else if (isNotNull(str3))
      {
        str4 = "select * from (select sys_module.*, level as module_level from sys_module CONNECT BY id = PRIOR parent_id start with id in (select module_id from sys_resource where id = '" + str3 + "')" + ") order by module_level desc, turn";
        str6 = "select * from sys_resource where id = '" + str3 + "'";
      }
    }
    else
    {
      str4 = "select sys_module.*, level as module_level from sys_module CONNECT BY parent_id = PRIOR id start with parent_id is null order by module_level, turn";
      str5 = "select * from sys_dataset";
      str6 = "select * from sys_resource";
    }
    HashMap localHashMap2 = queryData(str4, paramConnection);
    HashMap localHashMap3 = (bool2) && (isNotNull(str5)) ? queryData(str5, paramConnection) : null;
    HashMap localHashMap4 = (bool3) && (isNotNull(str6)) ? queryData(str6, paramConnection) : null;
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Document localDocument = DocumentHelper.createDocument();
    localDocument.setXMLEncoding("utf-8");
    Element localElement1 = localDocument.addElement("datas");
    String str7 = "select sysdate nowdate from dual";
    String str8 = (String)getRecordValue(queryData(str7, paramConnection), 0, "nowdate");
    localElement1.addAttribute("sync_date", str8);
    Element localElement2 = localElement1.addElement("modules");
    localElement2.addAttribute("sync_module", bool1 ? "1" : "0");
    localElement2.addAttribute("sync_dataset", bool2 ? "1" : "0");
    localElement2.addAttribute("sync_resource", bool3 ? "1" : "0");
    for (int i = 0; i < ((List)localHashMap2.get("rs")).size(); i++)
    {
      String str9 = String.valueOf(getRecordValue(localHashMap2, i, "id"));
      Element localElement3 = localElement2.addElement("module");
      localElement3.addAttribute("id", str9);
      localElement3.addAttribute("no", String.valueOf(getRecordValue(localHashMap2, i, "no")));
      localElement3.addAttribute("name", String.valueOf(getRecordValue(localHashMap2, i, "name")));
      localElement3.addAttribute("class_name", String.valueOf(getRecordValue(localHashMap2, i, "class_name")));
      localElement3.addAttribute("parent_id", String.valueOf(getRecordValue(localHashMap2, i, "parent_id")));
      localElement3.addAttribute("is_valid", String.valueOf(getRecordValue(localHashMap2, i, "is_valid")));
      localElement3.addAttribute("remark", String.valueOf(getRecordValue(localHashMap2, i, "remark")));
      Element localElement5;
      if (localHashMap3 != null)
      {
    	Element localElement4 = localElement3.addElement("dataSets");
        for (int j = 0; j < ((List)localHashMap3.get("rs")).size(); j++)
        {
          if (!String.valueOf(getRecordValue(localHashMap3, j, "module_id")).equalsIgnoreCase(str9))
            continue;
          localElement5 = addTextElement(localElement4, "dataSet", String.valueOf(getRecordValue(localHashMap3, j, "sql")), true);
          localElement5.addAttribute("id", String.valueOf(getRecordValue(localHashMap3, j, "id")));
          localElement5.addAttribute("no", String.valueOf(getRecordValue(localHashMap3, j, "no")));
          localElement5.addAttribute("name", String.valueOf(getRecordValue(localHashMap3, j, "name")));
          localElement5.addAttribute("module_id", String.valueOf(getRecordValue(localHashMap3, j, "module_id")));
          localElement5.addAttribute("is_valid", String.valueOf(getRecordValue(localHashMap3, j, "is_valid")));
          localElement5.addAttribute("remark", String.valueOf(getRecordValue(localHashMap3, j, "remark")));
        }
      }
      if (localHashMap4 == null)
        continue;
      Element localElement4 = localElement3.addElement("resources");
      for (int j = 0; j < ((List)localHashMap4.get("rs")).size(); j++)
      {
        if (!String.valueOf(getRecordValue(localHashMap4, j, "module_id")).equalsIgnoreCase(str9))
          continue;
        localElement5 = addTextElement(localElement4, "resource", String.valueOf(getRecordValue(localHashMap4, j, "content")), true);
        localElement5.addAttribute("id", String.valueOf(getRecordValue(localHashMap4, j, "id")));
        localElement5.addAttribute("no", String.valueOf(getRecordValue(localHashMap4, j, "no")));
        localElement5.addAttribute("name", String.valueOf(getRecordValue(localHashMap4, j, "name")));
        localElement5.addAttribute("module_id", String.valueOf(getRecordValue(localHashMap4, j, "module_id")));
        localElement5.addAttribute("is_valid", String.valueOf(getRecordValue(localHashMap4, j, "is_valid")));
        localElement5.addAttribute("remark", String.valueOf(getRecordValue(localHashMap4, j, "remark")));
      }
    }
    System.out.println(str8 + "导出数据集XML");
    return localDocument.asXML();
  }

  private Element addTextElement(Element paramElement, String paramString1, String paramString2, boolean paramBoolean)
  {
    Element localElement = paramElement.addElement(paramString1);
    if ((paramString2 != null) && (!paramString2.equals("")) && (!paramString2.equalsIgnoreCase("null")))
      if (paramBoolean)
        localElement.addCDATA(paramString2);
      else
        localElement.setText(paramString2);
    return localElement;
  }

  private Element addTextElement(Element paramElement, String paramString1, String paramString2)
  {
    return addTextElement(paramElement, paramString1, paramString2, false);
  }

  private HashMap queryData(String paramString, Connection paramConnection)
  {
    Statement localStatement = null;
    ResultSet localResultSet = null;
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    try
    {
      localStatement = paramConnection.createStatement();
      localResultSet = localStatement.executeQuery(paramString);
      ResultSetMetaData localResultSetMetaData = localResultSet.getMetaData();
      int i = localResultSetMetaData.getColumnCount();
      for (int j = 1; j <= i; j++)
        localArrayList1.add(localResultSetMetaData.getColumnName(j).toLowerCase());
      while (localResultSet.next())
      {
        ArrayList localArrayList3 = new ArrayList();
        for (int k = 1; k <= i; k++)
          localArrayList3.add(getValue(localResultSet, k));
        localArrayList2.add(localArrayList3);
      }
      localHashMap.put("fs", localArrayList1);
      localHashMap.put("rs", localArrayList2);
      localResultSet.close();
      localStatement.close();
    }
    catch (SQLException localSQLException)
    {
      localSQLException.printStackTrace();
    }
    return localHashMap;
  }

  public Object getRecordValue(HashMap paramHashMap, int paramInt, String paramString)
  {
    List localList1 = (List)paramHashMap.get("fs");
    int i = localList1.indexOf(paramString);
    if (i >= 0)
    {
      List localList2 = (List)((List)paramHashMap.get("rs")).get(paramInt);
      return localList2.get(i);
    }
    return null;
  }

  public Object getValue(ResultSet paramResultSet, int paramInt)
    throws SQLException
  {
    Object localObject1 = paramResultSet.getObject(paramInt);
    if (localObject1 != null)
    {
      int i = paramResultSet.getMetaData().getColumnType(paramInt);
      Object localObject2;
      Object localObject3;
      switch (i)
      {
      case -7:
      case -6:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
        double d = paramResultSet.getDouble(paramInt);
        try
        {
          int j = new Double(d).intValue();
          localObject2 = new BigDecimal(d);
          localObject3 = new BigDecimal(j);
          int k = ((BigDecimal)localObject2).compareTo((BigDecimal)localObject3);
          if (k == 0)
            localObject1 = Integer.valueOf(j);
          else
            localObject1 = Double.valueOf(d);
        }
        catch (Exception localException)
        {
          localObject1 = Double.valueOf(d);
        }
      case -1:
      case 1:
      case 12:
        localObject1 = paramResultSet.getString(paramInt);
        break;
      case 91:
      case 92:
        Timestamp localTimestamp = paramResultSet.getTimestamp(paramInt);
        localObject2 = new Date(localTimestamp.getTime());
        localObject1 = getStr((Date)localObject2);
        break;
      case 93:
        localObject3 = paramResultSet.getTimestamp(paramInt);
        localObject1 = ((Timestamp)localObject3).toString();
      }
    }
    return localObject1;
  }

  public static String getStr(Date paramDate)
  {
    if (paramDate == null)
      return null;
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return localSimpleDateFormat.format(paramDate);
  }

  public static boolean isNull(String paramString)
  {
    return (paramString == null) || (paramString.equals("")) || (paramString.equalsIgnoreCase("null")) || (paramString.equalsIgnoreCase("undefined"));
  }

  public static boolean isNotNull(String paramString)
  {
    return !isNull(paramString);
  }
}