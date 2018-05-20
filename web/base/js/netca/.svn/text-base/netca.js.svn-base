<!--
var SECUINTER_LOCAL_MACHINE_STORE = 0;
var SECUINTER_CURRENT_USER_STORE= 1;
var SECUINTER_MY_STORE = 0;
var SECUINTER_OTHER_STORE= 1;
var SECUINTER_CA_STORE= 2;
var SECUINTER_ROOT_STORE= 3;
var SECUINTER_SHA1_ALGORITHM= 1;
var SECUINTER_ALGORITHM_RC2=0;
var SECUINTER_ALGORITHM_DES = 6;
var SECUINTER_SHA1WithRSA_ALGORITHM = 2

function IsSecuInterInstalled(oUtil)
{
	if(typeof(oUtil) == "object")
	{
		if( (oUtil.object != null) )
		{
			return true;
		}
	}

	return false;
}

function IsSecuInter()
{
	var oUtil;
	try
	{
           oUtil = new ActiveXObject("SecuInter.Utilities");
           return 0;
	}
	catch (e)
	{
	   return -1;
	}
	return -1;
}

function IsHasNetCACert()
{
	var oUtil;
	var MyStore;
	try
	{
		oUtil = new ActiveXObject("SecuInter.Utilities");
		MyStore = new ActiveXObject("SecuInter.Store");
	}
	catch (e)
	{
		alert("如果您现在无法正常使用系统，请先检查是否安装USB KEY控件，再检查IE设置中是否禁用了ACTIVEX组件，详细信息请查看登录页面左下角的系统使用说明。");
		return null;
	}
	if(!IsSecuInterInstalled){
		alert("控件未安装!");
		return null;
        }

	try
	{
		MyStore.Open(SECUINTER_CURRENT_USER_STORE,SECUINTER_MY_STORE);
	}
	catch (e)
	{
		alert("打开证书库失败");
		return null;
	}
	var certs=MyStore.X509Certificates;
	MyStore.Close();
	MyStore = null;	

	var MyCerts = new ActiveXObject("SecuInter.X509Certificates");
	for(i=0;i<certs.Count;i++)
	{	
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
   return MyCerts;  //返回证书
}
function getCert(base64)
{
  var oCert = new ActiveXObject("SecuInter.X509Certificate");
  oCert.Decode(base64);
  return oCert.SerialNumber;
}
/*选择证书,返回 X509Certificate
选择证书,本地计算机
storeType:SECUINTER_MY_STORE=0(个人);SECUINTER_OTHER_STORE:1(其他人)
isSignCert:true(sign cert);false(env cert)
*/
function getNetCACert(storeType,isSignCert)
{	
	var oUtil;
	var MyStore;
	try
	{
		oUtil = new ActiveXObject("SecuInter.Utilities");
		MyStore = new ActiveXObject("SecuInter.Store");
	}
	catch (e)
	{
		alert("安装不成功!");
		return null;
	}
	if(!IsSecuInterInstalled){
		alert("安装不成功!");
		return null;
        }

	try
	{
		MyStore.Open(SECUINTER_CURRENT_USER_STORE,storeType);
	}
	catch (e)
	{
		alert("打开证书库失败");
		return null;
	}
	var certs=MyStore.X509Certificates;
	MyStore.Close();
	MyStore = null;	
	
	var MyCerts = new ActiveXObject("SecuInter.X509Certificates");
	//alert('certs.Count='+certs.Count);
	for(i=0;i<certs.Count;i++)
	{	
		issuer=certs.Item(i).Issuer;
	        if(issuer.indexOf("CN=NETCA")<0){
	        }
	        else{
	        	var iKeyUsage=certs.Item(i).KeyUsage;
	                if(iKeyUsage==-1){
	                	MyCerts.add(certs.Item(i));	
	                }
	                else{
		        				if(isSignCert==true){
		        						if(iKeyUsage%2==1&&iKeyUsage%4>=2){
		        								MyCerts.add(certs.Item(i));
		        						}
		                }
		                else{
		                	if(iKeyUsage%8>=4){
		                		MyCerts.add(certs.Item(i));	
		                	}
		                }
		              }
	        	}
	}
//alert('MyCerts.Count='+MyCerts.Count);
	if(MyCerts.Count>0){
	 return MyCerts.SelectCertificate();
	}
	window.status="";
	return null;

}

//取证书微缩图
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
	
	var oCert=getNetCACert(SECUINTER_MY_STORE,true);//选择证书
	if(oCert==null){
		return;
  }
  return oUtil.BinaryToHex(oCert.thumbprint);
}
//取证书微缩图
function checkCaObj()
{
	var oUtil;
	try
	{
		oUtil = new ActiveXObject("SecuInter.Utilities");
	}
	catch (e)
	{
		return -1;
	}

  return 1;
}

var SECUINTER_CMS_ENCODE_BASE64=1;
//签名（签名证书,签名内容，KEY密码）
function signNetCA(oCerts,bContent,pwd)
{	
	 	var oSigner ;
   	var oSignedData ;
   	var oCert=oCerts.SelectCertificate();//选择证书
   	if(oCert==null){
		  alert("未选择证书!");
		  return null;   	
   	}
	try
	{
		oSigner = new ActiveXObject("SecuInter.Signer");
		oSignedData = new ActiveXObject("SecuInter.SignedData");
	}
	catch (e)
	{
		alert("控件未安装好!");
		return null;
	}
   	try
   	{
            oSigner.Certificate = oCert;
	}catch (e){
	    alert("证书有误或已过期!");
	    return null;
	}
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
        oSignedData.Detached = false;
    
        var arrRT = oSignedData.sign(oSigner, SECUINTER_CMS_ENCODE_BASE64)
       
    	oSignedData = null;
    	oSigner = null;
	
        return arrRT;
}

//签名校验（签名原文，签名结果）
function verify(bContent,bSignData)
{	
   	var oSigner ;
   	var oSignedData ;
   	var oUtil;
	
	try
	{
		oSigner = new ActiveXObject("SecuInter.Signer");
		oSignedData = new ActiveXObject("SecuInter.SignedData");
		oUtil = new ActiveXObject("SecuInter.Utilities");
	}
	catch (e)
	{
		return -1;
	}
   	if( !oSignedData.verify(bSignData, 1) ){
   		return -2;
	}
	
   	if(oUtil.ByteArraytoString(bContent) != oUtil.ByteArraytoString(oSignedData.content))
   	{
   		 	return -3;
   	}
   	
	var thum = "";
	thum = oUtil.BinaryToHex(oSignedData.Signers.Item(0).Certificate.thumbprint);
    	
    	oSignedData = null;
    	oSigner = null;
    	oUtil=null;
    	return thum;
}

function CheckKeyLogin(pws) {
	var oUSBKey;
	var isPass;
	try
	{
		oUSBKey = new ActiveXObject("USBKeyLite.Key");
	}
	catch (e)
	{
		alert("oUSBKeyLite控件未安装!");
		return false;
	}
	
	try{
		oUSBKey.open();
  } catch (err){
  	alert(err.description+"||"+err.number);
  	return false;
  }
  
  isPass = true;
  var isok = oUSBKey.VerifyPin(pws);
  if(!isok){
   var i = oUSBKey.GetRetryNumber();
   alert("密码错误，还剩"+i+",此电子密钥将会锁死！");
   isPass = false;
  }
  oUSBKey.close();
  return isPass;
}
//加密（加密内容，加密证书）
function envNetCA(bContent,oCert)
{	
	var oEnv ;
	try
	{
		oEnv = new ActiveXObject("SecuInter.EnvelopedData");
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

//解密（密文）
function devNetCA(bEnvData)
{	
   	var oEnv ;
   	var oUtil;
	try
	{
		oEnv = new ActiveXObject("SecuInter.EnvelopedData");
		oUtil = new ActiveXObject("SecuInter.Utilities");
	}
	catch (e)
	{
		alert("安装不成功!");
		return null;
	}
   	oEnv.decrypt(bEnvData);
	  //var arrRt=oEnv.content;
    var arrRt = oUtil.ByteArrayToString(oEnv.content); 	
    	oEnv = null;
    	
    	return arrRt;
}

//时间戳签名（签名原文，时间戳服务器地址）
function signStampNetCACommon(bContent,timestampURL)
{
	var utilobj;
	var oSigner ;
    var oSignedData ;
	
	var oCert=getNetCACert(SECUINTER_MY_STORE,true);//选择证书
	if(oCert==null){
		alert("未选择证书!");
		return 'null';
   	
   	}
   	
	try
	{
		utilobj     =new ActiveXObject("SecuInter.Utilities");
		oSigner     =new ActiveXObject("SecuInter.Signer");
		oSignedData =new ActiveXObject("SecuInter.SignedData");
	}
	catch(e)
	{
		alert("安装不成功!");
		return 'null';
	}

  oSigner.Certificate = oCert;
  oSigner.HashAlgorithm = SECUINTER_SHA1_ALGORITHM ;
  oSigner.UseSigningCertificateAttribute = false;
  oSigner.UseSigningTime = true;
  oSigner.Options=1;
  //oSigner.SetUserPIN("12345678");
  oSignedData.content = bContent;
  oSignedData.Detached = false;
        
  var url=timestampURL;
  try{
	   var arrRT = oSignedData.SignWithTSATimeStamp(oSigner,url,"",utilobj.Nothing(),SECUINTER_CMS_ENCODE_BASE64);
	   return arrRT;
  }
  catch(e)
	{
		alert("时间戳签名不成功!");
		return null;
	}
  
}

function signVStampCommon(data,textId) {
   try{
	var texts=document.getElementById(textId);
   	texts.value=signStampNetCACommon(data,"https://classatsa.cnca.net/tsa.asp");   //签名（需要签名的内容,时间戳地址https://classatsa.cnca.net/tsa.asp)
   	return 0;
   }
   catch (e)
   {
		alert("请先盖章!"+e.toString());
		return 'null';
   }
}

function verifyVStampCommon(data,textId) {
   try{
   	   var oldData = data;    //当前明文数据
	   var texts=document.getElementById(textId);
	   var rt=verifyStamp(oldData,texts.value);   //校验签名（当前明文数据，当时的签名结果）
	   
	   if(rt==-1){
	   	alert("安装不成功!");
	   }
	   else if(rt==-2){
	   	alert("签名信息不对，验证不通过");
	   }
	   else if(rt==-3){
	   	alert("签名信息和原文不一致，验证不通过");
	   }
	   else if(rt==0){
	   	alert("验证通过");
	   }
   }
   catch (e)
   {
		alert("测试不成功!"+e.toString());
		return null;
   }
}

//签名校验（签名原文，签名结果）
function verifyStamp(bContent,bSignData)
{	
   	var oSigner ;
   	var oSignedData ;
   	var oUtil;
	
	try
	{
		oSigner = new ActiveXObject("SecuInter.Signer");
		oSignedData = new ActiveXObject("SecuInter.SignedData");
		oUtil = new ActiveXObject("SecuInter.Utilities");
	}
	catch (e)
	{
		return -1;
	}
   	if( !oSignedData.verify(bSignData, 1) ){
   		return -2;
	}
	   //alert(oUtil.ByteArraytoString(bContent));
   	if(oUtil.ByteArraytoString(bContent) != oUtil.ByteArraytoString(oSignedData.content))
   	{
   		 	return -3;
   	}
   	
	var iCertCount = oSignedData.Signers.Count;
	if(iCertCount = 1){
		  //显示签名证书与时间戳时间（此为演示Demo，具体是否需要显示有系统需求决定）
	    oSignedData.Signers.Item(0).Certificate.Display();
	    if(oSignedData.hasTSATimeStamp(0))
	      alert("时间戳服务器返回的时间为" + oSignedData.getTSATimeStamp(0)+"!加密内容:"+oUtil.ByteArraytoString(bContent));
	}
	else{
	      for(var i = 0 ;i<iCertCount-1;i++){
	        //显示签名证书与时间戳时间（此为演示Demo，具体是否需要显示有系统需求决定）
	        oSignedData.Signers.Item(i).Certificate.Display(); 
	        if(oSignedData.hasTSATimeStamp(i))
					   alert("时间戳服务器返回的时间为" + oSignedData.getTSATimeStamp(i));
	      }
  }
    	
  oSignedData = null;
  oSigner = null;
  oUtil=null;
  return 0;
}


//对数据进行签名（待加密数据，加密后数据存放位置(表单隐含字段id)，密钥密码）
//例子：signVCommon('<data>xxxx<data1><data2>eeee<data2>','textId','12345678')
function signVCommon(envdata,formTextName,password) {
   try{
   	var oCerts = IsHasNetCACert();
	if(oCerts.Count<=0){
		alert("找不到证书!");
		return -1;
	}
	var texts=document.getElementById(formTextName);
   	texts.value=signNetCA(oCerts,envdata,password);   //签名（需要签名的内容）
   	return 0;
   }
   catch (e)
   {
		alert("请先盖章!"+e.toString());
		return -1;
   }
}
//对签名数据进行验证（待验证数据数据，加密后数据，密钥密码）
//例子：verifyVCommon('<data>xxxx<data1><data2>eeee<data2>','textId','12345678')
function verifyVCommon(oldData,envData,password) {
   try{
   	 var oldData = oldData;    //当前明文数据
   	//alert(oldData);
   	
	   var rt=verify(oldData,envData,password);   //校验签名（当前明文数据，当时的签名结果）
	   if(rt==-1){
	   		alert("安装不成功!");
	   		return -1;
	   }
	   else if(rt==-2){
	   		alert("签名信息不对，验证不通过");
	   		return -2;
	   }
	   else if(rt==-3){
	   		alert("签名信息和原文不一致，验证不通过");
	   		return -3;
	   }
	   else if(rt==0||rt==getcertthumprint()){
	   	    alert("验证通过");
	   	    return 0;
	   }
   }
   catch (e)
   {
			alert("测试不成功!"+e.toString());
			return null;
   }
}

//对数据进行加密（待加密数据，加密后数据存放位置(表单隐含字段id)）
//例子：envCommon('<data>xxxx<data1><data2>eeee<data2>','textId')
function envCommon(envdata,formTextName) {
   try{
   	  if(envdata==''||envdata==null)
   	     return '';
   	  var oCert=getNetCACert(SECUINTER_MY_STORE,false); //SECUINTER_OTHER_STORE
   	  //alert('----1==-->');
	    if(oCert==null){
			   alert("未选择证书!");
			   return 'null';
	    }
      var envdata_ = envdata; //需要加密的内容组合
      //alert('envCommon='+envdata_);
       var texts=null;
      if(document.getElementById(formTextName)==null){
         texts=formTextName;
      }else{
      	texts=document.getElementById(formTextName)
      }
	 // var texts=document.getElementById(formTextName);
	  texts.value=envNetCA(envdata_,oCert);   //加密（需要加密的内容）
	 
   }
   catch (e)
   {
		alert("加密不成功!"+e.toString());
		return 'null';
   }
}
//对数据进行解密（加密数据，加密后数据存放位置(表单文本字段id)）
//例子：envCommon('WECSDFG@FG$124DF','textId')
function devCommon(envdata,formTextName) {
   try{
   	  var texts=null;
      if(document.getElementById(formTextName)==null){
         texts=formTextName;
      }else{
      	texts=document.getElementById(formTextName)
      }
   var envdata_ = envdata;
    //alert('envCommon='+envdata_);
    //var texts=document.getElementById(formTextName);
	   texts.value =devNetCA(envdata_);   //解密（密文）
   }
   catch (e)
   {
		alert("解密不成功!"+e.toString());
		return null;
   }
   
}
//-->