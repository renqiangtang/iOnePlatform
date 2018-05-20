<!--
/*
 * 文件修改记录 2011-04-29 修改者：王伟 根据secuInter当前最新版本(V4.1)修改创建对象的方式，解决WIN7下多次弹出的问题。
 * 先尝试使用新的创建方式创建对象，不成功则用旧的方式创建，目的是做到兼容V4.1以前的secuInter版本
 */

var SECUINTER_LOCAL_MACHINE_STORE = 0;
var SECUINTER_CURRENT_USER_STORE= 1;

var SECUINTER_MY_STORE = 0;
var SECUINTER_OTHER_STORE= 1;
var SECUINTER_CA_STORE= 2;
var SECUINTER_ROOT_STORE= 3;

var SECUINTER_CERTTYPE_ALL= 0;
var SECUINTER_CERTTYPE_SIGN= 1;
var SECUINTER_CERTTYPE_ENV= 2;

var SECUINTER_NETCA_ALL= 0;
var SECUINTER_NETCA_YES= 1;
var SECUINTER_NETCA_NO= 2;
/* NETCA和其他CA */
var SECUINTER_NETCA_OTHER= 3;



var SECUINTER_SHA1_ALGORITHM= 1;
var SECUINTER_ALGORITHM_RC2=0;
var SECUINTER_ALGORITHM_DES = 6;
var SECUINTER_SHA1WithRSA_ALGORITHM=2;


/* CA证书的CN */
var CASTR=new Array("CN=NETCA","CN=GDCA","CN=WGCA");


/* 作用是在WIN7下载入JS时就提示加载控件 */
var initialObj=null;

$(document).ready(function() {
      try {
    	  if($.browser.msie) {
    		  initialObj=new ActiveXObject("SecuInter.Utilities");
    		}
      } catch (e) {
      }
    })


/** ******************* 判断控件是否安装成功 2010-01-28***************** */
function isSecuInterInstalled()
{
	try
	{ 
		var oUtil = new ActiveXObject("SecuInter.Utilities");
		if(typeof(oUtil) == "object")
	    {
		    // if( (oUtil.object != null) )
		    // {
			    return true;
		    // }
	    }
		return false;
	}
	catch (e)
	{   
		// alert("安装不成功!"+e.description);
		return false;
	}
	return false;
}
/** ********* 判断是否有网证通证书2010.09.02 *********** */
function isHasNetCACert()
{
	if(!isSecuInterInstalled()){
		alert("控件未安装!");
		return false;
  	}
	var MyCerts = getX509Certificates(SECUINTER_CURRENT_USER_STORE,SECUINTER_MY_STORE,SECUINTER_CERTTYPE_ALL,SECUINTER_NETCA_YES);
	if (MyCerts==null||MyCerts.Count<=0){
   		alert("找不到证书，请插好密钥!");
   		return false;
	}
	return true;  
}
/** ********* 判断是否有网证通证书链2010.09.02 *********** */
function isHasNetCAChain()
{
	if(!isSecuInterInstalled()){
		alert("控件未安装!");
		return false;
  	}
	var MyCerts = getX509Certificates(SECUINTER_CURRENT_USER_STORE,SECUINTER_ROOT_STORE,SECUINTER_CERTTYPE_ALL,SECUINTER_NETCA_YES);
	if (MyCerts==null||MyCerts.Count<=0){
   		alert("未安装网证通证书链,请到www.cnca.net下载证书链,并安装!");
   		return false;
	}
	return true;  
}
/** ********* 选择一张网证通证书2010.09.02 *********** */
/*
 * 返回:X509Certificate 选择证书,本地计算机
 * storeType:SECUINTER_MY_STORE=0(个人);SECUINTER_OTHER_STORE:1(其他人)
 * isSignCert:true(sign cert);false(env cert)
 */
function getNetCACert(storeType,isSignCert)
{	
	if(!isSecuInterInstalled){
		alert("安装不成功!");
		return null;
    	}
    	var certType;
    	if(isSignCert==true){
    		certType=SECUINTER_CERTTYPE_SIGN;
    	}
    	else{
    		certType=SECUINTER_CERTTYPE_ENV;
    	}
    	var MyCerts = getX509Certificates(SECUINTER_CURRENT_USER_STORE,storeType,certType,SECUINTER_NETCA_YES);


	if(MyCerts.Count>0){
	 return MyCerts.SelectCertificate();
	}
	window.status="";
	return null;

}

function getCert(){
  var oUtil;
  var MyStore;
  try {
    oUtil = new ActiveXObject("SecuInter.Utilities");
    MyStore = new ActiveXObject("SecuInter.Store");
  } catch (e){
    alert("如果您现在无法正常使用系统，请先检查是否安装USB KEY控件，再检查IE设置中是否禁用了ACTIVEX组件，详细信息请查看登录页面左下角的系统使用说明。");
    return null;
  }
  if(!isSecuInterInstalled){
    alert("控件未安装!");
    return null;
  }
  try{
    MyStore.Open(SECUINTER_CURRENT_USER_STORE,SECUINTER_MY_STORE);
  }catch (e){
    alert("打开证书库失败");
    return null;
  }
  var certs=MyStore.X509Certificates;
  MyStore.Close();
  MyStore = null; 

  var MyCerts = new ActiveXObject("SecuInter.X509Certificates");
  for(var i=0;i<certs.Count;i++){ 
    var iKeyUsage=certs.Item(i).KeyUsage;
    var issuer=certs.Item(i).Issuer;
    if(issuer.indexOf("CN=NETCA")<0){
    }else if(iKeyUsage==-1){
      MyCerts.add(certs.Item(i)); 
    }else{
      if(iKeyUsage%2==1&&iKeyUsage%4>=2){
        MyCerts.add(certs.Item(i));
      }
    }   
  }
   return MyCerts;  // 返回证书
}

/** ********* 根据姆印得到一张网证通签名证书2010.09.02 *********** */
/* 返回:X509Certificate */
function getCertByThumbprint(szThumbprint)
{	
	 var oUtil= new ActiveXObject("SecuInter.Utilities");
	if (szThumbprint.length < 32)
	{
		alert("证书的姆印太短!");
		return null;
	}
	if(!isSecuInterInstalled()){
		alert("安装不成功!");
		return null;
        }
  var MyCerts = getX509Certificates(SECUINTER_CURRENT_USER_STORE,SECUINTER_MY_STORE,SECUINTER_CERTTYPE_SIGN,SECUINTER_NETCA_YES);
       
	for(i=0;i<MyCerts.Count;i++)
	{	
		if(oUtil.BinaryToHex(MyCerts.Item(i).Thumbprint(SECUINTER_SHA1_ALGORITHM)).toLowerCase() ==szThumbprint.toLowerCase() )
		{
		        return MyCerts.Item(i);
		}
	}
	window.status="";
	return null;

}

function getCertThumprint()
{
  var oUtil;
  try
  {
    oUtil = new ActiveXObject("SecuInter.Utilities");
  }
  catch (e)
  {
    alert("SecuInter控件对象创建不成功");
    return null;
  }
  
  var oCert=getNetCACert(SECUINTER_MY_STORE,true);// 选择证书
  if(oCert==null){
    return;
  }
  return oUtil.BinaryToHex(oCert.thumbprint);
}


/*
 * 选择证书 //2010-03-01 修改 whereType:SECUINTER_LOCAL_MACHINE_STORE =
 * 0(本地计算机);SECUINTER_CURRENT_USER_STORE= 1(当前用户);
 * storeType:SECUINTER_MY_STORE=0(个人);SECUINTER_OTHER_STORE:1(其他人);SECUINTER_CA_STORE=
 * 2;SECUINTER_ROOT_STORE= 3; certType: SECUINTER_CERTTYPE_ALL=
 * 0(所有);SECUINTER_CERTTYPE_SIGN= 1(签名);SECUINTER_CERTTYPE_ENV= 2(加密);
 * netcaType:SECUINTER_NETCA_ALL= 0(所有);SECUINTER_NETCA_YES=
 * 1(网证通);SECUINTER_NETCA_NO= 2(非网证通);
 */
function getX509Certificate(whereType,storeType,certType,netcaType)
{	
	var MyCerts = getX509Certificates(whereType,storeType,certType,netcaType);
	if(MyCerts==null){
	    return null;
	}
	if(MyCerts.Count>0){
	 return MyCerts.SelectCertificate();
	}
	window.status="";
	return null;

}
/*
 * 选择证书集, 2010-03-01 修改 whereType:SECUINTER_LOCAL_MACHINE_STORE =
 * 0;SECUINTER_CURRENT_USER_STORE= 1;
 * storeType:SECUINTER_MY_STORE=0(个人);SECUINTER_OTHER_STORE:1(其他人);SECUINTER_CA_STORE=
 * 2;SECUINTER_ROOT_STORE= 3; certType: SECUINTER_CERTTYPE_ALL=
 * 0;SECUINTER_CERTTYPE_SIGN= 1;SECUINTER_CERTTYPE_ENV= 2;
 * netcaType:SECUINTER_NETCA_ALL= 0;SECUINTER_NETCA_YES= 1;SECUINTER_NETCA_NO=
 * 2;
 */

function getX509Certificates(whereType,storeType,certType,netcaType)
{	
	var oMyStore;
	var MyCerts;
	var oUtil;
	if(!isSecuInterInstalled()){
		alert("安装不成功!");
		return null;
        }
		oUtil= new ActiveXObject("SecuInter.Utilities");
		try{
			 oMyStore=oUtil.CreateStoreObject();
		}catch(e){
			 oMyStore=new ActiveXObject("SecuInter.Store");
		}
	// var oMyStore= new ActiveXObject("SecuInter.Store");
	try
	{
		oMyStore.Open(whereType,storeType);
	}
	catch (e)
	{
		alert("打开证书库失败");
		return null;
	}
	var certs=oMyStore.X509Certificates;
	oMyStore.Close();
	oMyStore = null;	
	
	// var MyCerts = new ActiveXObject("SecuInter.X509Certificates");
	try{
		try{
			MyCerts=oUtil.CreateX509CertificatesObject();
		}catch(e){
			MyCerts = new ActiveXObject("SecuInter.X509Certificates");
		}
	}catch(e){
		alert("安装失败");
		return; 
	}
	
	for(i=0;i<certs.Count;i++)
	{	
		if(certType==SECUINTER_CERTTYPE_ALL){
			issuer=certs.Item(i).Issuer;
			if(netcaType==SECUINTER_NETCA_ALL){
				MyCerts.add(certs.Item(i));	
				}
			else if(netcaType==SECUINTER_NETCA_YES){
				if(issuer.indexOf("CN=NETCA")>=0){
					MyCerts.add(certs.Item(i));
				}
			}
		        else if(netcaType==SECUINTER_NETCA_NO){
		        	if(issuer.indexOf("CN=NETCA")<0){
					MyCerts.add(certs.Item(i));
				}
			}
			// 限制可以使用NETCA证书和其他CA证书
			else if(netcaType==SECUINTER_NETCA_OTHER){
				for(var j=0;j<CASTR.length;j++){
						if(issuer.indexOf(CASTR[j])>=0){
							MyCerts.add(certs.Item(i));
				}
				}
			}	
		}
	        else if(certType==SECUINTER_CERTTYPE_SIGN){
	        	issuer=certs.Item(i).Issuer;
			if(netcaType==SECUINTER_NETCA_ALL){
				if(certs.Item(i).KeyUsage==3){
	        		MyCerts.add(certs.Item(i));
	        		}
	        		if(certs.Item(i).KeyUsage==-1){
	        		MyCerts.add(certs.Item(i));
	        		}
					
			}
			else if(netcaType==SECUINTER_NETCA_YES){
				if(issuer.indexOf("CN=NETCA")>=0){
					if(certs.Item(i).KeyUsage==3){
		        		MyCerts.add(certs.Item(i));
		        		}
		        		if(certs.Item(i).KeyUsage==-1){
		        		MyCerts.add(certs.Item(i));
		        		}
				}
			}
		        else if(netcaType==SECUINTER_NETCA_NO){
		        	if(issuer.indexOf("CN=NETCA")<0){
					if(certs.Item(i).KeyUsage==3){
		        		MyCerts.add(certs.Item(i));
		        		}
		        		if(certs.Item(i).KeyUsage==-1){
		        		MyCerts.add(certs.Item(i));
		        		}
				}
			}		
			// 限制可以使用NETCA证书和其他CA证书
			else if(netcaType==SECUINTER_NETCA_OTHER){
				for(var j=0;j<CASTR.length;j++){
						if(issuer.indexOf(CASTR[j])>=0){
						if(certs.Item(i).KeyUsage==3){
		        		MyCerts.add(certs.Item(i));
		        		}
		        		if(certs.Item(i).KeyUsage==-1){
		        		MyCerts.add(certs.Item(i));
		        		}
				}
				}
			
			}	
	        	
	        }
	        else if(certType==SECUINTER_CERTTYPE_ENV){
	        	issuer=certs.Item(i).Issuer;
			if(netcaType==SECUINTER_NETCA_ALL){
				if(certs.Item(i).KeyUsage==12){
	        		MyCerts.add(certs.Item(i));
	        		}
	        		if(certs.Item(i).KeyUsage==-1){
	        		MyCerts.add(certs.Item(i));
	        		}
					
			}
			else if(netcaType==SECUINTER_NETCA_YES){
				if(issuer.indexOf("CN=NETCA")>=0){
					if(certs.Item(i).KeyUsage==12){
		        		MyCerts.add(certs.Item(i));
		        		}
		        		if(certs.Item(i).KeyUsage==-1){
		        		MyCerts.add(certs.Item(i));
		        		}
				}
			}
		        else if(netcaType==SECUINTER_NETCA_NO){
		        	if(issuer.indexOf("CN=NETCA")<0){
					if(certs.Item(i).KeyUsage==12){
		        		MyCerts.add(certs.Item(i));
		        		}
		        		if(certs.Item(i).KeyUsage==-1){
		        		MyCerts.add(certs.Item(i));
		        		}
				}
			}	
						// 限制可以使用NETCA证书和其他CA证书
			else if(netcaType==SECUINTER_NETCA_OTHER){
			for(var j=0;j<CASTR.length;j++){
				if(issuer.indexOf(CASTR[j])>=0){
						// alert(certs.Item(i).Subject+":"+certs.Item(i).KeyUsage);
							if(certs.Item(i).KeyUsage==12){
		        		MyCerts.add(certs.Item(i));
		        		}
		        		if(certs.Item(i).KeyUsage==-1){
		        		MyCerts.add(certs.Item(i));
		        		}
				}
			}
			}		        	
	        }
	}// END FOR
	return MyCerts;

}
/* 选择证书,返回 X509Certificate */
function getX509CertificateThumbprint(oCert)
{	
	if (oCert==null){
		return "";
	}
	if(!isSecuInterInstalled()){
		alert("安装不成功!");
		return "";
        }
	var oUtil= new ActiveXObject("SecuInter.Utilities");
        return oUtil.BinaryToHex(oCert.Thumbprint(SECUINTER_SHA1_ALGORITHM)).toUpperCase();
}

	
	
function getCertInfo(cert)
{	
	if(cert==null)
	{
		return;
    	}
   // cert.Display();
   	alert("证书颁发者::"+cert.Issuer);
   	alert("证书序列号::"+cert.SerialNumber);
   	alert("证书主题::"+cert.Subject);
   	alert("KeyUsage:   "+cert.KeyUsage);
   	alert("Subject的Email:   "+cert.GetInfo(2));
   	alert("Subject的名字:   "+cert.GetInfo(0));
   	alert("Subject的UPN:   "+cert.GetInfo(2));
}
/** *********************************************************************** */
/** ********************** 签名功能 2009.11.13 ************************* */
/** *********************************************************************** */

var SECUINTER_CMS_ENCODE_BASE64=1;
/** * 使用证书进行PKCS7签名 2009-12-21 ** */
function signPKCS7ByCert(bContent,oCert,IsNotHasSource,pwd)
{	
	 	var oSigner ;
   	var oSignedData ;
   	var oUtil;
	if(bContent=="")
	{   alert("原文内容为空!");
		return null;
    	}
	if(!isSecuInterInstalled()){
		alert("安装不成功!");
		return null;
        }
   /*
	 * var oSigner = new ActiveXObject("SecuInter.Signer"); var oSignedData =
	 * new ActiveXObject("SecuInter.SignedData");
	 */
	try
	{
		oUtil = new ActiveXObject("SecuInter.Utilities");
		try{
			 oSigner = oUtil.CreateSignerObject();
		}catch(e){
			oSigner = new ActiveXObject("SecuInter.Signer");
		}
		
		try{
			 oSignedData=oUtil.CreateSignedDataObject();
		}catch(e){
		
			oSignedData = new ActiveXObject("SecuInter.SignedData");
		}
		
	}
	catch (e)
	{
		alert("安装不成功!");
		return null;
	}
    	oSigner.Certificate = oCert;
    	oSigner.HashAlgorithm = SECUINTER_SHA1_ALGORITHM ;
    	oSigner.UseSigningCertificateAttribute = false;
    	oSigner.UseSigningTime = false;
    	if(pwd!=""){
        	var ok = oSigner.SetUserPIN(pwd);
        	if (!ok){
                     alert("密码有误！");
                     return null;
               }
	}
    	oSignedData.content = bContent;
    	oSignedData.Detached = IsNotHasSource;
    
    	var arrRT = oSignedData.sign(oSigner, SECUINTER_CMS_ENCODE_BASE64);
    	oSignedData = null;
    	oSigner = null;
    	return arrRT;
}
/** * PKCS7签名 对应以前的sign函数 2009-12-21 ** */
function signPKCS7ByPwd(bContent,IsNotHasSource,pwd)
{	
	var oCert=getX509Certificate(SECUINTER_CURRENT_USER_STORE,SECUINTER_MY_STORE,SECUINTER_CERTTYPE_SIGN,SECUINTER_NETCA_YES);
	if(oCert==null){
		alert("未选择证书,请检查是否插入密钥!");
		return null;
   	}
   	
    	return signPKCS7ByCert(bContent,oCert,IsNotHasSource,pwd);
}
function signPKCS7(bContent,IsNotHasSource)
{	
    	return signPKCS7ByPwd(bContent,IsNotHasSource,"");
}
/** * PKCS7签名,兼容以前 2009-12-21 ** */
function signNetCA(bContent,IsNotHasSource)
{	
   	return signPKCS7(bContent,IsNotHasSource);
}
/** * PKCS7时间戳签名 2010-09-02 ** */
function signPKCS7WithTSA(bContent,tsaUrl,IsNotHasSource)
{	
	
	var oSigner;
	var oSignedData;
	var oX509Certificate;
	var oUtil;
	if(bContent=="")
	{   alert("原文内容为空!");
		return null;
    	}
	if(tsaUrl=="")
	{   alert("时间戳URL为空!");
		return null;
    	}
	if(!isSecuInterInstalled()){
		alert("安装不成功!");
		return null;
        }
	var oCert=getX509Certificate(SECUINTER_CURRENT_USER_STORE,SECUINTER_MY_STORE,SECUINTER_CERTTYPE_SIGN,SECUINTER_NETCA_YES);
	if(oCert==null)
	{   alert("未选择证书!");
		return null;
    	}
    	try
	{
		oUtil = new ActiveXObject("SecuInter.Utilities");
		try{
			 oSigner = oUtil.CreateSignerObject();
		}catch(e){
			oSigner = new ActiveXObject("SecuInter.Signer");
		}
		
		try{
			 oSignedData=oUtil.CreateSignedDataObject();
		}catch(e){
			oSignedData = new ActiveXObject("SecuInter.SignedData");
		}
		
		try{
			oX509Certificate=oUtil.CreateX509CertificateObject();
		}catch(e){
			oX509Certificate=new ActiveXObject("SecuInter.X509Certificate");
			}
		
	}
	catch (e)
	{
		alert("安装不成功!");
		return null;
	}
		/*
		 * var oSigner = new ActiveXObject("SecuInter.Signer"); var oSignedData =
		 * new ActiveXObject("SecuInter.SignedData");
		 */
    	oSigner.Certificate = oCert;
    	oSigner.HashAlgorithm = SECUINTER_SHA1_ALGORITHM ;
    	oSigner.UseSigningCertificateAttribute = false;
    	oSigner.UseSigningTime = true;
    	oSignedData.content = bContent;
    	oSignedData.Detached = IsNotHasSource;
        
    	var arrRT = oSignedData.SignWithTSATimeStamp(oSigner, tsaUrl, "",oX509Certificate , SECUINTER_CMS_ENCODE_BASE64);
    	oSignedData = null;
    	oSigner = null;
    	return arrRT;
}
/** * PKCS7多人签名 2010-09-02 ** */
function coSignPKCS7(bContent,bProcContent,IsNotHasSource)
{	
	var	oUtil;
	var	oSigner;
	var	oSignedData;
	if(!isSecuInterInstalled()){
		alert("安装不成功!");
		return null;
        }
  /*
	 * var oUtil = new ActiveXObject("SecuInter.Utilities"); var oSigner = new
	 * ActiveXObject("SecuInter.Signer"); var oSignedData = new
	 * ActiveXObject("SecuInter.SignedData");
	 */
		try
	{
		oUtil = new ActiveXObject("SecuInter.Utilities");
		try{
			 oSigner = oUtil.CreateSignerObject();
		}catch(e){
			oSigner = new ActiveXObject("SecuInter.Signer");
		}
		
		try{
			 oSignedData=oUtil.CreateSignedDataObject();
		}catch(e){
			oSignedData = new ActiveXObject("SecuInter.SignedData");
		}	
	}
	catch (e)
	{
		alert("安装不成功!");
		return null;
	}
	if( IsNotHasSource ){
   		oSignedData.content = bContent
	}
   	if( !oSignedData.verify(bProcContent, 0) ){
   		alert("原始签名信息验证没通过!");
		return null;
	}
	var oCert=getX509Certificate(SECUINTER_CURRENT_USER_STORE,SECUINTER_MY_STORE,SECUINTER_CERTTYPE_SIGN,SECUINTER_NETCA_YES);
	if(oCert==null){
		alert("未选择证书!");
		return null;
   	}
	
   	oSigner.Certificate = oCert;
        oSigner.HashAlgorithm = SECUINTER_SHA1_ALGORITHM ;
        oSigner.UseSigningCertificateAttribute = false;
        oSigner.UseSigningTime = false;
        // oSignedData.content = bContent;
        // oSignedData.Detached = IsNotHasSource;//加后出错.注意
    
        var arrRT = oSignedData.sign(oSigner, SECUINTER_CMS_ENCODE_BASE64)
    	oSignedData = null;
    	oSigner = null;
        return arrRT;
        
}
/** * 修改名字,原名verify ,修改验证显示时间戳的方法 2009-12-21 ** */
function verifyPKCS7(bContent,bSignData)
{	
   	var oSigner ;
   	var oSignedData ;
   	var oUtil;
	
	try
	{	
		/*
		 * oSigner = new ActiveXObject("SecuInter.Signer"); oSignedData = new
		 * ActiveXObject("SecuInter.SignedData"); oUtil = new
		 * ActiveXObject("SecuInter.Utilities");
		 */
		oUtil = new ActiveXObject("SecuInter.Utilities");
		try{
			 oSigner = oUtil.CreateSignerObject();
		}catch(e){
			oSigner = new ActiveXObject("SecuInter.Signer");
		}
		
		try{
			 oSignedData=oUtil.CreateSignedDataObject();
		}catch(e){
			oSignedData = new ActiveXObject("SecuInter.SignedData");
		}	
		
	}
	catch (e)
	{
		return -1;
	}
   	if( !oSignedData.verify(bSignData, 0) ){
   		return -2;
	}
   	if(oUtil.ByteArraytoString(bContent) != oUtil.ByteArraytoString(oSignedData.content))
   	{
   		return -3;
   	}
   	
	var iCertCount = oSignedData.Signers.Count;
	
	if(iCertCount == 1){
	    	oSignedData.Signers.Item(0).Certificate.Display();
	     	if( oSignedData.hasTSATimestamp(0) ){
             		alert( "签名时间="+oSignedData.getTSATimeStamp(0)); 
		}
	}
	else{
	
	      	for(var i = 0 ;i<iCertCount;i++){
	        	oSignedData.Signers.Item(i).Certificate.Display();
			if( oSignedData.hasTSATimestamp(i) ){
             			alert( "签名时间="+oSignedData.getTSATimeStamp(i)); 
			}
		}
     	}
	
    	oSignedData = null;
    	oSigner = null;
    	oUtil=null;
    	return 0;
}
/** * 多次PKCS7签名及验证,循环测试 2009-12-21 ** */

function multiSignPKCS7(bContent,maxcount)
{	
   	var bSignData;
   	var icount;
 	var oCert=getX509Certificate(SECUINTER_CURRENT_USER_STORE,SECUINTER_MY_STORE,SECUINTER_CERTTYPE_SIGN,SECUINTER_NETCA_YES);
	if(oCert==null){
		alert("未选择证书,请检查是否插入密钥!");
		return null;
   	}
   	
   	for(icount=0;icount<maxcount;icount++)
   	{
	        bSignData = signPKCS7ByCert(bContent,oCert,false,"");
	        var thisRT=verifyPKCS7(bContent,bSignData);
	   	if(thisRT!=0)
	   	{
	   		alert("签名验证未通过");
	   		return;
	   	}
	}
    	alert("签名验证通过");
    	return ;
}
/** * 纯签名 ** */
function signPKCS1ByCert(bContent,oCert)
{	
   	var oUtil ;
   	var oSignature ;
	try
	{
		/*
		 * oUtil=new ActiveXObject("SecuInter.Utilities"); oSignature = new
		 * ActiveXObject("SecuInter.Signature");
		 */
			oUtil = new ActiveXObject("SecuInter.Utilities");
		try{
			 oSignature = oUtil.CreateSignatureObject();
		}catch(e){
			 oSignature = new ActiveXObject("SecuInter.Signature");
		}
		
		
	}
	catch (e)
	{
		alert("安装不成功!");
		return null;
	}
   	
        oSignature.Certificate = oCert;
        oSignature.Algorithm = SECUINTER_SHA1WithRSA_ALGORITHM ;
        var arrRT = oSignature.sign(bContent)
        
        var rt=oUtil.Base64Encode(arrRT);
    	oSignature = null;
    	oUtil=null;
        return rt;
}

function signPKCS1(bContent)
{	
	var oCert=getX509Certificate(SECUINTER_CURRENT_USER_STORE,SECUINTER_MY_STORE,SECUINTER_CERTTYPE_SIGN,SECUINTER_NETCA_YES);
	if(oCert==null){
		alert("未选择证书!");
		return null;
   	
   	}
    return signPKCS1ByCert(bContent,oCert);
}
function verifyPKCS1(bContent,bSignData)
{	
	var oCert=getX509Certificate(SECUINTER_CURRENT_USER_STORE,SECUINTER_MY_STORE,SECUINTER_CERTTYPE_SIGN,SECUINTER_NETCA_YES);
	if(oCert==null){
		alert("未选择证书!");
		return null;
   	
   	}
   	var oUtil ;
   	var oSignature ;
	try
	{
		/*
		 * oUtil=new ActiveXObject("SecuInter.Utilities"); oSignature = new
		 * ActiveXObject("SecuInter.Signature");
		 */
			oUtil = new ActiveXObject("SecuInter.Utilities");
		try{
			 oSignature = oUtil.CreateSignatureObject();
		}catch(e){
			 oSignature = new ActiveXObject("SecuInter.Signature");
		}
		
	}
	catch (e)
	{
		return -1;
	}
        oSignature.Certificate = oCert;
        oSignature.Algorithm = SECUINTER_SHA1WithRSA_ALGORITHM ;

	if(oSignature.Verify(bContent,oUtil.Base64Decode(bSignData) ) )
	{return 0;
		}
	else{
	return -3;
	}
    	
    	oSignedData = null;
    	oCert = null;
    	oUtil=null;
    	return 0;
}


function envPKCS7(bContent,oCert)
{	
		var oUtil;
   	var oEnv ;
	try
	{
		oUtil = new ActiveXObject("SecuInter.Utilities");
		try{
					oEnv=oUtil.CreateEnvelopedDataObject();
		}catch(e){
					oEnv = new ActiveXObject("SecuInter.EnvelopedData");
			}
	
	}
	catch (e)
	{
		alert("安装不成功!");
		return null;
	}
   	
        oEnv.Algorithm = SECUINTER_ALGORITHM_RC2;
        oEnv.Recipients.Add(oCert);
        oEnv.content = bContent ;
        var arrRT = oEnv.encrypt(SECUINTER_CMS_ENCODE_BASE64);
    	oEnv = null;
        return arrRT;
}
/** ********* 修改原先编码错误2009.12.21 *********** */
function devPKCS7(bEnvData)
{	
		var oUtil;
   	var oEnv ;
	try
	{
		oUtil = new ActiveXObject("SecuInter.Utilities");
		try{
					oEnv=oUtil.CreateEnvelopedDataObject();
		}catch(e){
					oEnv = new ActiveXObject("SecuInter.EnvelopedData");
			}
	}
	catch (e)
	{
		alert("安装不成功!");
		return null;
	}
   	oEnv.decrypt(bEnvData);
	// oEnv.Recipients.Item(0).Display();
	var arrRt=oEnv.content;
   	oEnv = null;
    	return oUtil.ByteArraytoString(arrRt);
}

// -->
