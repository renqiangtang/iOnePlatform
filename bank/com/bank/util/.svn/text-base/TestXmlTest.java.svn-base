package com.bank.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.base.ds.DataSourceManager;
import com.base.utils.ParaMap;


public class TestXmlTest  {
	XmlUtil dao = new XmlUtil();
	
	@Before
	public void setUp() throws Exception {
			}

	@After
	public void tearDown() throws Exception {
	
	}

	
	
	@Test
	public void test_importTemplate() throws Exception {
		String xmlStr = "<?xml version='1.0' encoding='utf-8' ?>" +
				"<datas>" +
				"<data>" +
				"<goodses id=''><goods id='' /></goodses>" +
				"<target id='001'></target>" +
				"<template id='cccc'>" +
				"	<StartNode id='n01' nextId='a1' />" +
				"	<Node id='a1' type='L01' nextId='a2' beginTime='2012-4-25 12:00:00' endTime='2012-4-25 13:00:00' />" +
				"	<Node id='a2' type='L02' nextId='a3' beginTime='2012-4-25 13:00:00' endTime='2012-4-26 13:00:00'>" +
				"		<Node id='b2' type='Q01' nextId='b3' />" +
				"		<Node id='b4' type='Q01' />" +
				"		<Node id='b1' type='Q01' nextId='b2' />" +
				"		<Node id='b3' type='Q01' nextId='b4' /></Node>" +
				"	<Node id='a3' type='L03' nextId='n02' beginTime='2012-4-25 17:00:00'>" +
				"		<Node id='c1' type='Q01' nextId='c2' />" +
				"		<Node id='c2' type='Q01' />" +
				"	</Node>" +
				"	<EndNode id='n02' />" +
				"</template>" +
				"</data>" +
				"<data>" +
				"<goodses id=''><goods id='' /></goodses>" +
				"<target id='001'></target>" +
				"<template id='dddd'>" +
				"	<StartNode id='n01' nextId='a1' />" +
				"	<Node id='a1' type='L01' nextId='a2' beginTime='2012-4-25 12:00:00' endTime='2012-4-25 13:00:00' />" +
				"	<Node id='a2' type='L02' nextId='a3' beginTime='2012-4-25 13:00:00' endTime='2012-4-26 13:00:00'>" +
				"		<Node id='b2' type='Q01' nextId='b3' />" +
				"		<Node id='b4' type='Q01' />" +
				"		<Node id='b1' type='Q01' nextId='b2' />" +
				"		<Node id='b3' type='Q01' nextId='b4' /></Node>" +
				"	<Node id='a3' type='L03' nextId='n02' beginTime='2012-4-25 17:00:00'>" +
				"		<Node id='c1' type='Q01' nextId='c2' />" +
				"		<Node id='c2' type='Q01' />" +
				"	</Node>" +
				"	<EndNode id='n02' />" +
				"</template>" +
				"</data>" +
				"<data>" +
				"<goodses id=''><goods id='' /></goodses>" +
				"<target id='001'></target>" +
				"<template id='eee'>" +
				"	<StartNode id='n01' nextId='a0' />" +
				"	<Node id='a1' type='L01' nextId='a2' beginTime='2012-4-25 12:00:00' endTime='2012-4-25 13:00:00' />" +
				"	<Node id='a4' type='L01' nextId='a5' beginTime='2012-4-25 12:00:00' endTime='2012-4-25 13:00:00' />" +
				"	<Node id='a2' type='L02' nextId='a3' beginTime='2012-4-25 13:00:00' endTime='2012-4-26 13:00:00'>" +
				"		<Node id='b2' type='Q01' nextId='b3' />" +
				"		<Node id='b4' type='Q01' />" +
				"		<Node id='b1' type='Q01' nextId='b2' />" +
				"		<Node id='b3' type='Q01' nextId='b4' /></Node>" +
				"	<Node id='a5' type='L01' nextId='n02' beginTime='2012-4-25 12:00:00' endTime='2012-4-25 13:00:00' />" +
				"	<Node id='a3' type='L03' nextId='a4' beginTime='2012-4-25 17:00:00'>" +
				"		<Node id='c1' type='Q01' nextId='c2' />" +
				"		<Node id='c2' type='Q01' />" +
				"	</Node>" +
				"	<Node id='a0' type='L02' nextId='a1' beginTime='2012-4-25 13:00:00' endTime='2012-4-26 13:00:00'>" +
				"		<Node id='b2' type='Q01' nextId='b3' />" +
				"		<Node id='b4' type='Q01' nextId='b5' />" +
				"		<Node id='b1' type='Q01' nextId='b2' />" +
				"		<Node id='b5' type='Q01' />" +
				"		<Node id='b3' type='Q01' nextId='b4' /></Node>" +
				"	<EndNode id='n02' />" +
				"</template>" +
				"</data>" +
				"</datas>";
		
		InputStreamReader reader = null;
        StringWriter writer = new StringWriter();
        File file = new File("c:\\test_xml.xml");
        reader = new InputStreamReader(new FileInputStream(file));
        
        //将输入流写入输出流
        char[] buffer = new char[1024];
        int n = 0;
        while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
        }
        xmlStr = writer.toString(); 
        System.out.println(xmlStr);
		dao.readXML(xmlStr);	
	}
	
	
}
