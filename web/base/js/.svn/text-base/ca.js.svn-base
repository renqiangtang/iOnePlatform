// --------------------------------------------
// CA数据签名的模式，默认为CA.MODEL_Complex,为复杂签名
// --------------------------------------------
CA.MODEL_Complex = 1;
// --------------------------------------------
// CA数据签名的模式，为纯签名
// --------------------------------------------
CA.MODEL_Pure = 2;

// --------------------------------------------
// CA基类
// --------------------------------------------
function CA() {
}

// --------------------------------------------
// CA是否已经安装驱动,没有安装则自动安装驱动
// --------------------------------------------
CA.prototype.installed = function() {
}

// --------------------------------------------
// 登录
// .同时有id，pwd,检查id,pwd与Key的一致
// --------------------------------------------
CA.prototype.check = function(pwd) {
}

// --------------------------------------------
// CA读取ID
// --------------------------------------------
CA.prototype.readId = function() {
}
// --------------------------------------------
// CA编码
// --------------------------------------------
CA.prototype.encode = function(readId, inputStr) {
}
// --------------------------------------------
// CA解码
// --------------------------------------------
CA.prototype.decode = function(inputStr) {
}
// --------------------------------------------
// CA签名
// --------------------------------------------
CA.prototype.sign = function(inputStr, model) {
}
// --------------------------------------------
// CA验证
// --------------------------------------------
CA.prototype.verify = function(oldStr, signStr, model) {
}
CA.prototype.addCookie=function(name,value,expireHours){ 
	      var cookieString=name+"="+escape(value); 
         //判断是否设置过期时间 
         if(expireHours>0){ 
                var date=new Date(); 
                date.setTime(date.getTime+expireHours*3600*1000); 
                cookieString=cookieString+"; expire="+date.toGMTString(); 
         } 
         document.cookie=cookieString; 
}
CA.prototype.getCookie = function(cookie_name) {
	    var results = document.cookie.match('(^|;) ?' + cookie_name + '=([^;]*)(;|$)'); 
	    if (results) 
	        return (unescape(results[2])); 
	    else 
	        return null; 
}
CA.prototype.delCookie= function(name){
    var exp = new Date(0);
    var cval=this.getCookie(name);
    if(cval!=null) document.cookie= name + "="+cval+";expires="+exp.toGMTString();
    


}
// --------------------------------------------
// NetCA继承CA类
// 并且需要覆盖必要的方法
// --------------------------------------------
function NetCA() {
	var oHead = document.getElementsByTagName('HEAD').item(0);
	var oScript = document.createElement("script");
	oScript.type = "text/javascript";
	oScript.src = "js/netca/secuInter.js";
	oHead.appendChild(oScript);
}

NetCA.prototype = new CA();

// --------------------------------------------
// 判断是否安装和插入key
// --------------------------------------------
NetCA.prototype.installed = function() {
	var flag;
	try {
		flag = isHasNetCACert();
	} catch (e) {
		return false;
	}
	return flag;
}

// --------------------------------------------
// 获取证书
// --------------------------------------------
NetCA.prototype.readId = function() {
	var thumprint = null;
	try {
		if (this.installed()) {
			thumprint = getCertThumprint();
		}
	} catch (e) {
		return null;
	}
	return thumprint;
}

// --------------------------------------------
// 验证密码是否匹配
// --------------------------------------------
NetCA.prototype.check = function(pwd) {
	if (!pwd) {
		alert("密码不能为空，请先输入密码。");
		return null;
	}
	try {
		if (this.installed()) {
			var oCerts = getCert();
			var oSigner;
			if (oCerts == null) {
				alert("未选择证书!");
				return null;
			} else {
				var oCert = oCerts.SelectCertificate();// 选择证书
				if (oCert == null) {
					alert("未选择证书!");
					return null;
				}
				try {
					oSigner = new ActiveXObject("SecuInter.Signer");
				} catch (e) {
					alert("控件未安装好!");
					return null;
				}
				try {
					oSigner.Certificate = oCert;
				} catch (e) {
					alert("证书有误或已过期!");
					return null;
				}
				oSigner.HashAlgorithm = SECUINTER_SHA1_ALGORITHM;
				oSigner.UseSigningCertificateAttribute = false;
				oSigner.UseSigningTime = false;
				var ok = false;
				try {
					ok = oSigner.SetUserPIN(pwd);
				} catch (e) {
					alert("密码有误！");
					return null;
				}
				if (ok) {
					return this.readId();
				} else {
					alert("密码有误！");
					return null;
				}
			}
		}
	} catch (e) {
		return null;
	}
}

// --------------------------------------------
// 加密成功返回加密数据，失败返回null
// --------------------------------------------
NetCA.prototype.encode = function(readId, inputStr) {
//	if (!readId) {
//		alert("请先输入证书序列号。");
//		return null;
//	}
	if (!inputStr) {
		alert("需加密数据不能为空，请先输入需加密的信息。");
		return null;
	}
	try {
//		if (this.installed()) {
	//	alert('ca readId:'+this.readId()+' db readId:'+readId);
		if (this.installed()) {
			if(readId != this.readId()){
				var ss='请插入正确的Key';
				alert(ss);
				throw ss;
			}
			var oCert = getX509Certificate(SECUINTER_CURRENT_USER_STORE,
					SECUINTER_MY_STORE, SECUINTER_CERTTYPE_ENV,
					SECUINTER_NETCA_YES);
			if (oCert == null) {
				throw "未选择证书!";
			}
			return envPKCS7(inputStr, oCert);
		} else {
			throw "未安装Key";
		}
	} catch (e) {
		throw e;
	}
}

// --------------------------------------------
// 解密成功返回加密前的数据，失败返回null
// --------------------------------------------
NetCA.prototype.decode = function(inputStr) {
	if (!inputStr) {
		alert("解密数据不能为空，请先输入解密信息。");
		return null;
	}
	try {
		if (this.installed()) {
			var rt = devPKCS7(inputStr);
			if (rt == null) {
				alert("安装不成功!");
				return null;
			} else {
				return rt;
			}
		} else {
			return null;
		}
	} catch (e) {
		return null;
	}
}

// --------------------------------------------
// 数据签名，如果签名成功返回签名的数据，签名失败返回null，model=1是签名(默认)，model=2是纯签名
// --------------------------------------------
NetCA.prototype.sign = function(inputStr, model) {
	if (!inputStr) {
		alert("需签名数据不能为空，请先输入需签名的信息。");
		return null;
	}
	if (!model)
		model = 2;
	try {
		if (this.installed()) {
			if (CA.MODEL_Complex == model)
				return signPKCS7(inputStr, false, false);
			else if (CA.MODEL_Pure == model)
				return signPKCS1(inputStr);
		}
	} catch (e) {
		alert("签名失败！");
		return null;
	}
}

// --------------------------------------------
// 签名验证，如果验证成功返回true，验证失败返回false
// --------------------------------------------
NetCA.prototype.verify = function(oldStr, signStr, model) {
	if (!oldStr) {
		alert("原签名数据不能为空，请先输入原签名信息。");
		return false;
	}
	if (!signStr) {
		alert("签名数据不能为空，请先输入签名信息。");
		return false;
	}
	if (!model)
		model = 2;
	try {
		if (this.installed()) {
			if (CA.MODEL_Complex == model) {
				var rt = verifyPKCS7(oldStr, signStr);
				if (rt == 0) {
					return true;
				} else {
					return false;
				}
			} else if (CA.MODEL_Pure == model) {
				var rt = verifyPKCS1(oldStr, signStr);
				if (rt == 0) {
					return true;
				} else {
					return false;
				}
			}
		}
	} catch (e) {
		alert("签名验证失败！");
		return false;
	}
}

// --------------------------------------------
// GDCA继承CA类
// 并且需要覆盖必要的方法
// --------------------------------------------
function GDCA() {
	var oHead = document.getElementsByTagName('HEAD').item(0);
	var oScript = document.createElement("script");
	oScript.type = "text/javascript";
	oScript.src = "js/gdca/gdca.js";
	oHead.appendChild(oScript);
}

GDCA.prototype = new CA();

GDCA.prototype.installed = function() {
	var flag = true;
	try {
		var UseCom = new ActiveXObject("Atl_com.Gdca");// GDCACom;
		var KeyType = UseCom.GDCA_GetDevicType();
		if (KeyType < 0) {
			alert("请在USB口插入证书介质！");
			return false;
		}
		var errorCode = GDCAKeyInit(UseCom, KeyType);
		if (errorCode != 0) {
			UseCom.GDCA_Finalize();
			return false;
		}
	} catch (e) {
		alert("请先安装数字证书！");
		flag = false;
	}
	return flag;
}

GDCA.prototype.readId = function() {
	try {
		var flag = this.installed()
		if (flag) {
			var UseCom = new ActiveXObject("Atl_com.Gdca");// GDCACom;
			var KeyType = UseCom.GDCA_GetDevicType();
			var Tureid = new Array;
			var ReadOutData = UseCom.GDCA_ReadLabel("LAB_USERCERT_SIG", 7);
			if (KeyType != 40) {
				Tureid[0] = UseCom.GDCA_GetInfoByOID(ReadOutData, 2,
						"1.2.86.21.1.1", 0);
				Tureid[1] = UseCom.GDCA_GetInfoByOID(ReadOutData, 2,
						"1.2.86.21.1.3", 0);
				UseCom.GDCA_Finalize();
				return Tureid[1] + Tureid[0];
			} else {
				var netCAsn = UseCom.GDCA_GetCertificateInfo(ReadOutData, 3);
				UseCom.GDCA_Finalize();
				return netCAsn;
			}
			UseCom.GDCA_Finalize();
		}
	} catch (e) {
		alert("请在USB口插入证书介质！");
		return null;
	}
	return null;
}

GDCA.prototype.check = function(pwd) {
	if (!pwd) {
		alert("密码不能为空，请先输入密码。");
		return null;
	} else {
		try {
			var flag = this.installed()
			var i_Com = null;
			if (flag) {
				i_Com = new ActiveXObject("Atl_com.Gdca");
				i_Com.GDCA_Login(2, pwd);
				var errorCode = i_Com.GetError();
				if (errorCode != 0) {
					GDCAFinalize(i_Com);
					return null;
				}
				return this.readId();
			}
		} catch (objError) {
			GDCAFinalize(i_Com);
			return null;
		}
	}
}

GDCA.prototype.encode = function(readId, inputStr) {
	try {
		var flag = this.installed()
		var outData = new Array;
		var encCertData = null;
		var ret = 0;
		if (!inputStr) {
			alert("加密数据不能为空，请先输入加密信息。");
			return null;
		} else if (flag) {
			var UseCom = new ActiveXObject("Atl_com.Gdca");
			var KeyType = UseCom.GDCA_GetDevicType();
			// 设置key的类型到控件中
			ret = UseCom.GDCA_SetDeviceType(KeyType);
			// 控件初始化
			ret = UseCom.GDCA_Initialize();
			var encCertData = UseCom.GDCA_ReadLabel(LBL_USERCERT_ENC,
					GDCA_LBL_ENCCERT);
			ret = UseCom.GetError();
			if (ret != 0 || encCertData == "" || encCertData == null) {
				GDCAFinalize(UseCom);
				return null;
			}
			ret = GDCASealEnvelope(UseCom, KeyType, encCertData,
					'GDCA_ALGO_3DES', inputStr, outData);
			if (ret != 0) {
				GDCAFinalize(UseCom);
				return null;
			}
			return outData[0];
		}
	} catch (e) {
		return null;
	}
}

GDCA.prototype.decode = function(inputStr) {
	try {
		var flag = this.installed();
		var outData = new Array;
		var encCertData = null;
		var ret = 0;
		if (!inputStr) {
			alert("解密数据不能为空，请先输入解密信息。");
			return null;
		} else if (flag) {
			var UseCom = new ActiveXObject("Atl_com.Gdca");
			var KeyType = UseCom.GDCA_GetDevicType();
			try {
				ret = GDCAOpenEnvelope(UseCom, KeyType, null, inputStr, outData);
			} catch (e) {
				var value = window
						.showModalDialog("gdcakey.html", null,
								"dialogWidth:300px;dialogHeight:80px;scroll:no;status:no");
				if (value) {
					GDCA.prototype.check(value);
					GDCAFinalize(UseCom);
					ret = decode(inputStr);
				}
				// $.ligerDialog.prompt('请输入key的访问密码', function(yes, value) {
				// if (yes && value != null && value != "") {
				// GDCA.prototype.check(value);
				// GDCAFinalize(UseCom);
				// ret = decode(inputStr);
				// }
				// });
			}
			if (ret == -500) {
				var value = window
						.showModalDialog("gdcakey.html", null,
								"dialogWidth:300px;dialogHeight:80px;scroll:no;status:no");
				if (value) {
					GDCA.prototype.check(value);
					GDCAFinalize(UseCom);
					ret = decode(inputStr);
				}
				// $.ligerDialog.prompt('请输入key的访问密码', function(yes, value) {
				// if (yes && value != null && value != "") {
				// GDCA.prototype.check(value);
				// GDCAFinalize(UseCom);
				// ret = decode(inputStr);
				// }
				// });
			}
			if (ret != 0) {
				GDCAFinalize(UseCom);
				return null;
			}
			return outData[0]; // 加密（需要加密的内容）
		}
	} catch (e) {
		return null;
	}
}
// --------------------------------------------
// GDCA继承CA类
// 并且需要覆盖必要的方法
// --------------------------------------------
function GDCA() {
	var oHead = document.getElementsByTagName('HEAD').item(0);
	var oScript = document.createElement("script");
	oScript.type = "text/javascript";
	oScript.src = "js/gdca/gdca.js";
	oHead.appendChild(oScript);
}

GDCA.prototype = new CA();

GDCA.prototype.installed = function() {
	var flag = true;
	try {
		var UseCom = new ActiveXObject("Atl_com.Gdca");// GDCACom;
		var KeyType = UseCom.GDCA_GetDevicType();
		if (KeyType < 0) {
			alert("请在USB口插入证书介质！");
			return false;
		}
		var errorCode = GDCAKeyInit(UseCom, KeyType);
		if (errorCode != 0) {
			UseCom.GDCA_Finalize();
			return false;
		}
	} catch (e) {
		alert("请先安装数字证书！");
		flag = false;
	}
	return flag;
}

GDCA.prototype.readId = function() {
	try {
		var flag = this.installed()
		if (flag) {
			var UseCom = new ActiveXObject("Atl_com.Gdca");// GDCACom;
			var KeyType = UseCom.GDCA_GetDevicType();
			var Tureid = new Array;
			var ReadOutData = UseCom.GDCA_ReadLabel("LAB_USERCERT_SIG", 7);
			if (KeyType != 40) {
				Tureid[0] = UseCom.GDCA_GetInfoByOID(ReadOutData, 2,
						"1.2.86.21.1.1", 0);
				Tureid[1] = UseCom.GDCA_GetInfoByOID(ReadOutData, 2,
						"1.2.86.21.1.3", 0);
				UseCom.GDCA_Finalize();
				return Tureid[1] + Tureid[0];
			} else {
				var netCAsn = UseCom.GDCA_GetCertificateInfo(ReadOutData, 3);
				UseCom.GDCA_Finalize();
				return netCAsn;
			}
			UseCom.GDCA_Finalize();
		}
	} catch (e) {
		alert("请在USB口插入证书介质！");
		return null;
	}
	return null;
}

GDCA.prototype.check = function(pwd) {
	if (!pwd) {
		alert("密码不能为空，请先输入密码。");
		return null;
	} else {
		try {
			var flag = this.installed()
			var i_Com = null;
			if (flag) {
				i_Com = new ActiveXObject("Atl_com.Gdca");
				i_Com.GDCA_Login(2, pwd);
				var errorCode = i_Com.GetError();
				if (errorCode != 0) {
					GDCAFinalize(i_Com);
					return null;
				}
				return this.readId();
			}
		} catch (objError) {
			GDCAFinalize(i_Com);
			return null;
		}
	}
}

GDCA.prototype.encode = function(readId, inputStr) {
	try {
		var flag = this.installed()
		var outData = new Array;
		var encCertData = null;
		var ret = 0;
		if (!inputStr) {
			alert("加密数据不能为空，请先输入加密信息。");
			return null;
		} else if (flag) {
			var UseCom = new ActiveXObject("Atl_com.Gdca");
			var KeyType = UseCom.GDCA_GetDevicType();
			// 设置key的类型到控件中
			ret = UseCom.GDCA_SetDeviceType(KeyType);
			// 控件初始化
			ret = UseCom.GDCA_Initialize();
			var encCertData = UseCom.GDCA_ReadLabel(LBL_USERCERT_ENC,
					GDCA_LBL_ENCCERT);
			ret = UseCom.GetError();
			if (ret != 0 || encCertData == "" || encCertData == null) {
				GDCAFinalize(UseCom);
				return null;
			}
			ret = GDCASealEnvelope(UseCom, KeyType, encCertData,
					'GDCA_ALGO_3DES', inputStr, outData);
			if (ret != 0) {
				GDCAFinalize(UseCom);
				return null;
			}
			return outData[0];
		}
	} catch (e) {
		return null;
	}
}

GDCA.prototype.decode = function(inputStr) {
	try {
		var flag = this.installed();
		var outData = new Array;
		var encCertData = null;
		var ret = 0;
		if (!inputStr) {
			alert("解密数据不能为空，请先输入解密信息。");
			return null;
		} else if (flag) {
			var UseCom = new ActiveXObject("Atl_com.Gdca");
			var KeyType = UseCom.GDCA_GetDevicType();
			try {
				ret = GDCAOpenEnvelope(UseCom, KeyType, null, inputStr, outData);
			} catch (e) {
				var value = window
						.showModalDialog("gdcakey.html", null,
								"dialogWidth:300px;dialogHeight:80px;scroll:no;status:no");
				if (value) {
					GDCA.prototype.check(value);
					GDCAFinalize(UseCom);
					ret = decode(inputStr);
				}
				// $.ligerDialog.prompt('请输入key的访问密码', function(yes, value) {
				// if (yes && value != null && value != "") {
				// GDCA.prototype.check(value);
				// GDCAFinalize(UseCom);
				// ret = decode(inputStr);
				// }
				// });
			}
			if (ret == -500) {
				var value = window
						.showModalDialog("gdcakey.html", null,
								"dialogWidth:300px;dialogHeight:80px;scroll:no;status:no");
				if (value) {
					GDCA.prototype.check(value);
					GDCAFinalize(UseCom);
					ret = decode(inputStr);
				}
				// $.ligerDialog.prompt('请输入key的访问密码', function(yes, value) {
				// if (yes && value != null && value != "") {
				// GDCA.prototype.check(value);
				// GDCAFinalize(UseCom);
				// ret = decode(inputStr);
				// }
				// });
			}
			if (ret != 0) {
				GDCAFinalize(UseCom);
				return null;
			}
			return outData[0]; // 加密（需要加密的内容）
		}
	} catch (e) {
		return null;
	}
}
GDCA.prototype.addCookie=function(name,value,expireHours){ 
         var cookieString=name+"="+escape(value); 
         //判断是否设置过期时间 
         if(expireHours>0){ 
                var date=new Date(); 
                date.setTime(date.getTime+expireHours*3600*1000); 
                cookieString=cookieString+"; expire="+date.toGMTString(); 
         } 
         document.cookie=cookieString; 
} 


// --------------------------------------------
// SDCA继承CA类
// 并且需要覆盖必要的方法
// --------------------------------------------
function SDCA() {
	
	var oHead = document.getElementsByTagName('BODY').item(0);
	var oScript = document.createElement("OBJECT");
	oScript.id = "sdcaOcx";
	oScript.codeBase = "JITSecurityTool.cab#version=1,0,1,2";
	oScript.classid = "clsid:F1FDD7D2-0192-4F66-A015-4FC6235E8B74";
	oScript.height = "1";
	oScript.width = "1";
	oHead.appendChild(oScript);


	var oScript1 = document.createElement("OBJECT");
	oScript1.id = "JITDSignOcx";
	oScript1.classid = "clsid:06CA9432-D9BD-4867-8475-770B131E1759";
	oScript1.height = "1";
	oScript1.width = "1";
	oHead.appendChild(oScript1);
	
	this.installed();
}

SDCA.prototype = new CA();

// --------------------------------------------
// 判断是否安装和插入key
// --------------------------------------------
SDCA.prototype.installed = function() {
	var flag;
	if(sdcaOcx.initcontrol()==0){
		flag=true;
	}else
	    flag=false;
	return flag;
}

// --------------------------------------------
// 获取证书
// --------------------------------------------
SDCA.prototype.readId = function() {
	var key;
	var ca_pwd=this.getCookieValue();
	try{
		
		if(ca_pwd==null||!this.check(ca_pwd)){
		   return null;
		}
		rst=sdcaOcx.readCert("USBCSP://.2CER",7,ca_pwd,"USBCSP://.2CER",7,ca_pwd);
		if(rst!=0){
			    this.delCookie('ca_pwd');
				alert("证书加载失败，请确认密码是否正确！");
				
				key=null;
		}else
			    key=sdcaOcx.EncCertSN();
      return key;
	}catch(e){
		alert("请在USB口插入证书介质！");
		this.delCookie('ca_pwd');
		return null;
		
	}

}

// --------------------------------------------
// 验证密码是否匹配
// --------------------------------------------
SDCA.prototype.check = function(pwd) {
	var rst;
	var key;
	try{
		if(pwd==null)
		   return false;
		rst=sdcaOcx.VerifyPin(pwd,pwd.length);
		if(rst!=0){
			this.delCookie('ca_pwd');
			return false;
		}else{ 
		    this.addCookie('ca_pwd',pwd,0);
			rst=sdcaOcx.readCert("USBCSP://.2CER",7,pwd,"USBCSP://.2CER",7,pwd);
			if(rst!=0){
				//alert("证书加载失败，请确认密码是否正确！");
				this.delCookie('ca_pwd');
				return false;
			}else
				key=sdcaOcx.EncCertSN();
		}
		return key;
	}catch(e){
		//alert("密码不正确！");
		this.delCookie('ca_pwd');
		return false;
	}
	
}

// --------------------------------------------
// 加密成功返回加密数据，失败返回null
// 山东CA是不使用readId参数
// --------------------------------------------
SDCA.prototype.encode = function(readId, inputStr) {
	var rst;
	try{
		var ca_pwd=this.getCookieValue();
		// 读取证书
		if(ca_pwd==null)
		   return;
		this.installed();
		rst=sdcaOcx.readCert("USBCSP://.2CER",7,ca_pwd,"USBCSP://.2CER",7,ca_pwd);
		if(rst!=0){
			this.delCookie('ca_pwd');  
			alert('读取证书失败！'+rst); 
			return;
		}  
		//获取加密证书
		var cryptcert=sdcaOcx.getcryptcert();
		// 加密数据
		rst=sdcaOcx.encEnvelop(cryptcert,inputStr);
		if(rst!=0){
			alert("加密数据失败,错误码为" + rst);
			return;
		}
		//获取密文
		var EncryptData = sdcaOcx.getconten();
		return EncryptData;
	}catch(e){
		alert("加密数据失败,错误信息"+e);
		return 
	}

}

// --------------------------------------------
// 解密成功返回加密前的数据，失败返回null
// --------------------------------------------
SDCA.prototype.decode = function(inputStr) {
	var rst;
	try{
		var ca_pwd=this.getCookieValue();
		// 解密数据
		if(ca_pwd==null)
		   return ;
		rst = sdcaOcx.decEnvelop("USBCSP://.2CER",ca_pwd,inputStr);
		if(rst!=0){
			this.delCookie('ca_pwd');  
			alert("解密数据失败,错误码为" + rst);
			return;
		}
		return sdcaOcx.getconten();

	}catch(e){
		alert("加密数据失败,错误信息"+e);
		return 
	}
}
SDCA.prototype.getOU = function() {
	var val={};
	var map = new Map();
	try {
		JITDSignOcx.SetCertChooseType(1);
	} catch (e) {
		alert("客户端安全控件没有安装或安装失败,请安装客户端安全控件后登录！");
		return map;
	}
	JITDSignOcx.SetCert("SC", "", "", "", "", "");
	certSubject = JITDSignOcx.getCertInfo("SC", 0, "");
    var v2=certSubject.split(",");
    /**
     证书主题项:C=CN, S=SD, L=jinan, O=河源市公共资源交易管理工作领导小组办公室, 
     * OU=IDliming --登陆ID,ID, 
     * OU=IXXXXXXXX--身份证,I, 
     * CN=XXXXX--名称
     * OU=JYPOXXXX--移动电话，JYPO；
     * OU=JYCOXXXX--联系人，JYCO；
     * OU=JYRExxxx--境内外，JYRE；
     * OU=JYLPXXXX--法人，JYLP；
     * OU=JYPPXXXX--是否上市公司，JYPP
     * OU=JYICXXXX--是否法人身份证号，JYIC
    **/
   try {
	    for(var i=0;i<v2.length;i++){
	    	var v3=v2[i].split("=");
	    	if(v2[i].indexOf("OU=JY")>=0){
	    		var id=v3[1].substring(0,4);
	    		var value=v3[1].substring(4);
	    		map.put(id,value);
	    	}else if(v2[i].indexOf("OU=ID")>=0){
	    		var id=v3[1].substring(0,2);
	    		var value=v3[1].substring(2);
	    		map.put(id,value);
	    	}else  if(v2[i].indexOf("OU=I")>=0){
	    		var id=v3[1].substring(0,1);
	    		var value=v3[1].substring(1);
	    		map.put(id,value);
	    	}else if(v2[i].indexOf("CN=")>=0){
	    		map.put('name',v3[1]);
	    		//val['name']=value;
	    	}
	    	
	    }
	    map.put('key',this.readId());
	    //map.put('JYPP','是');
	    //map.put('JYRE','境外');
	    //alert(val.ID);
	    return map;
   }catch (e){
      return map;
   }
}
// --------------------------------------------
// 数据签名，如果签名成功返回签名的数据，签名失败返回null，model=1是签名(默认)，model=2是纯签名
// --------------------------------------------
SDCA.prototype.sign = function(inputStr, model) {
	return true;
}

// --------------------------------------------
// 签名验证，如果验证成功返回true，验证失败返回false
// --------------------------------------------
SDCA.prototype.verify = function(oldStr, signStr, model) {
	if (!oldStr) {
		alert("原签名数据不能为空，请先输入原签名信息。");
		return false;
	}
	if (!signStr) {
		alert("签名数据不能为空，请先输入签名信息。");
		return false;
	}
	
}
SDCA.prototype.getCookieValue =function(){
	var ca_pwd=this.getCookie("ca_pwd");
	if(ca_pwd==null){//||this.check(cw_pwd)
		var val=prompt("请输入UKey密码","password");
		if(val.length==0){
			alert('key密码不能为空');
			return null
		}else
		   this.addCookie("ca_pwd",val,0);
		ca_pwd=val;
	}
	return ca_pwd;
}


//--------------------------------------------
//ZZCA继承CA类
//并且需要覆盖必要的方法
//--------------------------------------------
function ZZCA() {
	var oHead = document.getElementsByTagName('BODY').item(0);
	var oScript = document.createElement("OBJECT");
	oScript.id = "zzOcx";
	oScript.classid = "CLSID:06CA9432-D9BD-4867-8475-770B131E1759";
	oScript.height = "1";
	oScript.width = "1";
	oHead.appendChild(oScript);
}

ZZCA.prototype = new CA();


ZZCA.prototype.getCookieValue =function(){
	var key=this.getCookie("thumprint");
	if(key==undefined)
	  key=false;
	return key;
}

//--------------------------------------------
//判断是否安装和插入key
//--------------------------------------------
ZZCA.prototype.installed = function() {
	try
	{	 		
		//初始化客户端签名证书
		zzOcx.SetCert("SC","","","","","");
		//判断初始化是否成功
		if(zzOcx.GetErrorCode()!=0){
			alert("初始化客户端证书错误：" + zzOcx.GetErrorMessage());
			return false;
		}
	}
	catch(e) {
	  alert("控件没有安装，请下载安装互诚通程序。" +e);
	  return false;
	}
	
	return true;
}

ZZCA.prototype.CertTools_AttachSign = function(srcData){
	try
	{	 		
		var tmp, certdn;
		certdn = zzOcx.GetCertInfo("SC", 0, "");
		tmp = zzOcx.AttachSign(certdn,srcData);
		
		if(tmp==null || tmp.length == 0){ // 失败
			strErrMsg = zzOcx.GetErrorMessage(); // 获取错误信息
			alert("请选择正确的证书！");
			return;
		}
		return tmp;
	}
	catch(e) {
		throw e;
	}
}

ZZCA.prototype.CertTools_GetSignCertBase64=function(){
	try
	{	 		
		var Cert = "";
		Cert = zzOcx.GetCertInfo("SC", 8, "");
		return Cert;
	}
	catch(e) {
	}
	return "";
}	

//--------------------------------------------
//获取证书
//--------------------------------------------
ZZCA.prototype.readId = function(type) {
   //授权时使用读取其他的key
   if(type=='getOtherKey'){
	  return this.readOtherId();
	}
	var key=this.getCookieValue();
	var thumprint = null;
	try {
		if(key==false){
			if (this.installed()) {
				thumprint = zzOcx.GetCertInfo("SC", 2, "");	//得到证书序列号
				//this.addCookie('thumprint',thumprint,0);
			}
		}else
		   thumprint=key;
	} catch (e) {
		throw e;
	}
	return thumprint;
}

ZZCA.prototype.readOtherId=function(){
	var thumprint = null;
	try {
			if (this.installed()) {
				thumprint = zzOcx.GetCertInfo("SC", 2, "");	//得到证书序列号
			}
	} catch (e) {
		throw e;
	}
	return thumprint;
} 
//--------------------------------------------
//获取证书
//--------------------------------------------
ZZCA.prototype.readIdNoIn = function() {
	var thumprint = null;
	try {
		thumprint = zzOcx.GetCertInfo("SC", 2, "");	//得到证书序列号
		this.addCookie('thumprint',thumprint,0);
	} catch (e) {
		throw e;
	}
	return thumprint;
}

//--------------------------------------------
//验证密码是否匹配
//--------------------------------------------
ZZCA.prototype.check = function(ra) {
	try {
		if(this.installed()){
			var pwd = this.CertTools_AttachSign(ra);
			if(pwd){
				return this.readIdNoIn();
			}else{
				return false;
			}
		}else{
			alert("请插入KEY");
		}
		
	} catch (e) {
		throw e;
	}
}

//--------------------------------------------
//加密成功返回加密数据，失败返回null
//--------------------------------------------
ZZCA.prototype.encode = function(readId, inputStr) {
	if (!inputStr) {
		alert("需加密数据不能为空，请先输入需加密的信息。");
		return null;
	}
	try {
                var key=this.getCookieValue();
                if(key!=false){
			return zzOcx.GetBase64Encode(inputStr);
                }else if (this.installed()) {
			if(readId != this.readId()){
				var ss='请插入正确的Key';
				alert(ss);
				throw ss;
			}
			return zzOcx.GetBase64Encode(inputStr);
		} else {
			throw "未安装Key";
		}
	} catch (e) {
		throw e;
	}
}

//--------------------------------------------
//解密成功返回加密前的数据，失败返回null
//--------------------------------------------
ZZCA.prototype.decode = function(inputStr) {
	if (!inputStr) {
		alert("解密数据不能为空，请先输入解密信息。");
		return null;
	}
	try {
		if (this.installed()) {
			return zzOcx.GetBase64Decode(inputStr);
		} else {
			throw "未安装Key";
		}
	} catch (e) {
		throw e;
	}
}

//--------------------------------------------
//数据签名，如果签名成功返回签名的数据，签名失败返回null，model=1是签名(默认)，model=2是纯签名
//--------------------------------------------
ZZCA.prototype.sign = function(inputStr, model) {
	if (!inputStr) {
		alert("需签名数据不能为空，请先输入需签名的信息。");
		return null;
	}
	try {
		return this.CertTools_AttachSign(inputStr);
	} catch (e) {
		throw e;
	}
}

//--------------------------------------------
//签名验证，如果验证成功返回true，验证失败返回false
//--------------------------------------------
//ZZCA.prototype.verify = function(oldStr, signStr, model) {
//	if (!oldStr) {
//		alert("原签名数据不能为空，请先输入原签名信息。");
//		return false;
//	}
//	if (!signStr) {
//		alert("签名数据不能为空，请先输入签名信息。");
//		return false;
//	}
//	if (!model)
//		model = 2;
//	try {
//		if (this.installed()) {
//			if (CA.MODEL_Complex == model) {
//				var rt = verifyPKCS7(oldStr, signStr);
//				if (rt == 0) {
//					return true;
//				} else {
//					return false;
//				}
//			} else if (CA.MODEL_Pure == model) {
//				var rt = verifyPKCS1(oldStr, signStr);
//				if (rt == 0) {
//					return true;
//				} else {
//					return false;
//				}
//			}
//		}
//	} catch (e) {
//		alert("签名验证失败！");
//		return false;
//	}
//}


// --------------------------------------------
// 全局CA实例
// --------------------------------------------
var ca = null;

$(document).ready(function() {
			try {
				ca = new GDCA();
				//if(!ca.installed()){
					//window.location.href="./error.html"; 
					//}
			} catch (e) {
				
			}
		})
