package com.ijpay.controller.weixin;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ijpay.weixin.entity.H5ScencInfo;
import com.ijpay.weixin.entity.H5ScencInfo.H5;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jpay.ext.kit.IpKit;
import com.jpay.ext.kit.PaymentKit;
import com.jpay.ext.kit.ZxingKit;
import com.jpay.secure.RSAUtils;
import com.jpay.vo.AjaxResult;
import com.jpay.weixin.api.WxPayApi;
import com.jpay.weixin.api.WxPayApi.TradeType;
import com.jpay.weixin.api.WxPayApiConfig;
import com.jpay.weixin.api.WxPayApiConfig.PayModel;
import com.jpay.weixin.api.WxPayApiConfigKit;

/**
 * @author Javen
 * 商户模式下微信支付
 */
public class WxPayController extends WxPayApiController {
	static Log log=Log.getLog(WxPayController.class);
	private AjaxResult ajax = new AjaxResult();
	private static final Prop prop = PropKit.use("wxpay.properties");
	//商户相关资料
	String appid = prop.get("appId");
	String mch_id = prop.get("mchId");
	String partnerKey = prop.get("partnerKey");
	String certPath = prop.get("certPath");
	String notify_url = prop.get("domain")+"/wxpay/pay_notify";
	
	public WxPayApiConfig getApiConfig() {
		WxPayApiConfig apiConfig = WxPayApiConfig.New()
				.setAppId(appid)
				.setMchId(mch_id)
				.setPaternerKey(partnerKey)
				.setPayModel(PayModel.BUSINESSMODEL);
		return apiConfig;
	}
	
	public void index(){
		log.info("欢迎使用IJPay,商户模式下微信支付 - by Javen");
		renderText("欢迎使用IJPay 商户模式下微信支付  - by Javen");
	}
	
	public void getKey(){
		String getsignkey = WxPayApi.getsignkey(mch_id, partnerKey);
		renderText(getsignkey);
	}
	
	/**
	 * 微信H5 支付
	 * 注意：必须再web页面中发起支付且域名已添加到开发配置中
	 */
	public void wapPay(){
		String ip = IpKit.getRealIp(getRequest());
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}
		
		H5ScencInfo sceneInfo = new H5ScencInfo();
		
		H5 h5_info = new H5();
		h5_info.setType("Wap");
		//此域名必须在商户平台--"产品中心"--"开发配置"中添加
		h5_info.setWap_url("https://pay.qq.com");
		h5_info.setWap_name("腾讯充值");
		sceneInfo.setH5_info(h5_info);
		
		Map<String, String> params = WxPayApiConfigKit.getWxPayApiConfig()
				.setAttach("IJPay H5支付测试  -By Javen")
				.setBody("IJPay H5支付测试  -By Javen")
				.setSpbillCreateIp(ip)
				.setTotalFee("520")
				.setTradeType(TradeType.MWEB)
				.setNotifyUrl(notify_url)
				.setOutTradeNo(String.valueOf(System.currentTimeMillis()))
				.setSceneInfo(h5_info.toString())
				.build();
		
		String xmlResult = WxPayApi.pushOrder(false,params);
log.info(xmlResult);
		Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
		
		String return_code = result.get("return_code");
		String return_msg = result.get("return_msg");
		if (!PaymentKit.codeIsOK(return_code)) {
			ajax.addError(return_msg);
			renderJson(ajax);
			return;
		}
		String result_code = result.get("result_code");
		if (!PaymentKit.codeIsOK(result_code)) {
			ajax.addError(return_msg);
			renderJson(ajax);
			return;
		}
		// 以下字段在return_code 和result_code都为SUCCESS的时候有返回
		
		String prepay_id = result.get("prepay_id");
		String mweb_url = result.get("mweb_url");
		
		System.out.println("prepay_id:"+prepay_id+" mweb_url:"+mweb_url);
		redirect(mweb_url);
	}
	
	/**
	 * 公众号支付
	 * @TODO 待测试 o5NJx1dVRilQI6uUVSaBDuLnM3iM
	 */
	public void webPay() {
		
		// openId，采用 网页授权获取 access_token API：SnsAccessTokenApi获取
		String openId = (String) getSession().getAttribute("openId");
		
		String total_fee=getPara("total_fee");
		
		if (StrKit.isBlank(openId)) {
			ajax.addError("openId is null");
			renderJson(ajax);
			return;
		}
		if (StrKit.isBlank(total_fee)) {
			ajax.addError("请输入数字金额");
			renderJson(ajax);
			return;
		}
		
		String ip = IpKit.getRealIp(getRequest());
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}
		
		Map<String, String> params = WxPayApiConfigKit.getWxPayApiConfig()
				.setAttach("IJPay 公众号支付测试  -By Javen")
				.setBody("IJPay 公众号支付测试  -By Javen")
				.setOpenId(openId)
				.setSpbillCreateIp(ip)
				.setTotalFee(total_fee)
				.setTradeType(TradeType.JSAPI)
				.setNotifyUrl(notify_url)
				.setOutTradeNo(String.valueOf(System.currentTimeMillis()))
				.build();
		
		String xmlResult = WxPayApi.pushOrder(false,params);
log.info(xmlResult);
		Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
		
		String return_code = result.get("return_code");
		String return_msg = result.get("return_msg");
		if (!PaymentKit.codeIsOK(return_code)) {
			ajax.addError(return_msg);
			renderJson(ajax);
			return;
		}
		String result_code = result.get("result_code");
		if (!PaymentKit.codeIsOK(result_code)) {
			ajax.addError(return_msg);
			renderJson(ajax);
			return;
		}
		// 以下字段在return_code 和result_code都为SUCCESS的时候有返回
		String prepay_id = result.get("prepay_id");
		
		Map<String, String> packageParams = PaymentKit.prepayIdCreateSign(prepay_id);
		
		String jsonStr = JsonKit.toJson(packageParams);
		ajax.success(jsonStr);
		renderJson(ajax);
	}
	
	
	
	
	/**
	 * 生成支付二维码（模式一）并在页面上显示
	 * 已测试
	 */
	public void scanCode1(){
		try {
			String product_id = getPara("productId");
			if (StrKit.isBlank(product_id)) {
				ajax.addError("productId is null");
				renderJson(ajax);
				return;
			}
			WxPayApiConfig config = WxPayApiConfigKit.getWxPayApiConfig();
			//获取扫码支付（模式一）url
			String qrCodeUrl=WxPayApi.getCodeUrl(config.getAppId(), config.getMchId(),product_id, config.getPaternerKey(), true);
			log.info(qrCodeUrl);
			//生成二维码保存的路径
			String name = "payQRCode1.png";
			Boolean encode = ZxingKit.encode(qrCodeUrl, BarcodeFormat.QR_CODE, 3, ErrorCorrectionLevel.H, "png", 200, 200,
					PathKit.getWebRootPath()+File.separator+name );
			if (encode) {
				//在页面上显示
				ajax.success(name);
				renderJson(ajax);
			}
		} catch (Exception e) {
			ajax.addError("系统异常："+e.getMessage());
			renderJson(ajax);
			e.printStackTrace();
		}
	}

	/**
	 * 扫码支付模式一回调
	 * 已测试
	 */
	public void wxpay(){
		try {
		
			String result  = HttpKit.readData(getRequest());
			System.out.println("callBack_xml>>>"+result);
			/**
			 * 获取返回的信息内容中各个参数的值
			 */
			Map<String, String> map = PaymentKit.xmlToMap(result);
			for (String key : map.keySet()) {
				   System.out.println("key= "+ key + " and value= " + map.get(key));
			}
			
			String appid=map.get("appid");
			String openid = map.get("openid");
			String mch_id = map.get("mch_id");
			String is_subscribe = map.get("is_subscribe");
			String nonce_str = map.get("nonce_str");
			String product_id = map.get("product_id");
			String sign = map.get("sign");
			Map<String, String> packageParams = new HashMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("openid", openid);
			packageParams.put("mch_id",mch_id);
			packageParams.put("is_subscribe",is_subscribe);
			packageParams.put("nonce_str",nonce_str);
			packageParams.put("product_id", product_id);
			
			String packageSign = PaymentKit.createSign(packageParams, WxPayApiConfigKit.getWxPayApiConfig().getPaternerKey());
			// 统一下单文档地址：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1

			
			String ip = IpKit.getRealIp(getRequest());
			if (StrKit.isBlank(ip)) {
				ip = "127.0.0.1";
			}
			
			Map<String, String> params = WxPayApiConfigKit.getWxPayApiConfig()
					.setAttach("IJPay 扫码模式一测试  -By Javen")
					.setBody("IJPay 扫码模式一测试  -By Javen")
					.setOpenId(openid)
					.setSpbillCreateIp(ip)
					.setTotalFee("100")
					.setTradeType(TradeType.NATIVE)
					.setNotifyUrl(notify_url)
					.setOutTradeNo(String.valueOf(System.currentTimeMillis()))
					.build();
			
			String xmlResult = WxPayApi.pushOrder(false,params);
			log.info("prepay_xml>>>"+xmlResult);
			
			/**
	         * 发送信息给微信服务器
	         */
			Map<String, String> payResult = PaymentKit.xmlToMap(xmlResult);
			
			String return_code = payResult.get("return_code");
			String result_code = payResult.get("result_code");
			
			if (StrKit.notBlank(return_code) && StrKit.notBlank(result_code) && return_code.equalsIgnoreCase("SUCCESS")&&result_code.equalsIgnoreCase("SUCCESS")) {
				// 以下字段在return_code 和result_code都为SUCCESS的时候有返回
				String prepay_id = payResult.get("prepay_id");
				
				Map<String, String> prepayParams = new HashMap<String, String>();
				prepayParams.put("return_code", "SUCCESS");
				prepayParams.put("appId", appid);
				prepayParams.put("mch_id", mch_id);
				prepayParams.put("nonceStr", System.currentTimeMillis() + "");
				prepayParams.put("prepay_id", prepay_id);
				String prepaySign = null;
				if (sign.equals(packageSign)) {
					prepayParams.put("result_code", "SUCCESS");
				}else {
					prepayParams.put("result_code", "FAIL");
					prepayParams.put("err_code_des", "订单失效");   //result_code为FAIL时，添加该键值对，value值是微信告诉客户的信息
				}
				prepaySign = PaymentKit.createSign(prepayParams, WxPayApiConfigKit.getWxPayApiConfig().getPaternerKey());
				prepayParams.put("sign", prepaySign);
				String xml = PaymentKit.toXml(prepayParams);
				log.error(xml);
				renderText(xml);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 扫码支付模式二
	 * 已测试
	 */
	public void scanCode2() {
		
//		String openId="o5NJx1dVRilQI6uUVSaBDuLnM3iM";
		String openId = (String) getSession().getAttribute("openId");
		
		String total_fee=getPara("total_fee");
		
		if (StrKit.isBlank(openId)) {
			ajax.addError("openId is null");
			renderJson(ajax);
			return;
		}
		if (StrKit.isBlank(total_fee)) {
			ajax.addError("支付金额不能为空");
			renderJson(ajax);
			return;
		}
		
		String ip = IpKit.getRealIp(getRequest());
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}
		
		Map<String, String> params = WxPayApiConfigKit.getWxPayApiConfig()
				.setAttach("IJPay 测试  -By Javen")
				.setBody("IJPay 扫码支付2测试  -By Javen")
				.setOpenId(openId)
				.setSpbillCreateIp(ip)
				.setTotalFee(total_fee)
				.setTradeType(TradeType.NATIVE)
				.setNotifyUrl(notify_url)
				.setOutTradeNo(String.valueOf(System.currentTimeMillis()))
				.build();
		
		String xmlResult = WxPayApi.pushOrder(false,params);
		
log.info(xmlResult);
		Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
		
		String return_code = result.get("return_code");
		String return_msg = result.get("return_msg");
		if (!PaymentKit.codeIsOK(return_code)) {
			System.out.println(return_msg);
			renderText(xmlResult);
			return;
		}
		String result_code = result.get("result_code");
		if (!PaymentKit.codeIsOK(result_code)) {
			System.out.println(return_msg);
			renderText(xmlResult);
			return;
		}
		//生成预付订单success
		
		String qrCodeUrl = result.get("code_url");
		String name = "payQRCode2.png";
		
		Boolean encode = ZxingKit.encode(qrCodeUrl, BarcodeFormat.QR_CODE, 3, ErrorCorrectionLevel.H, "png", 200, 200,
				PathKit.getWebRootPath()+File.separator+name);
		if (encode) {
//			renderQrCode(qrCodeUrl, 200, 200);
			//在页面上显示
			ajax.success(name);
			renderJson(ajax);
		}
	}

	/**
	 * 刷卡支付
	 * 已测试
	 */
	public void micropay(){
		String auth_code = getPara("auth_code");
		String total_fee = getPara("total_fee");
		
		if (StrKit.isBlank(total_fee)) {
			ajax.addError("支付金额不能为空");
			renderJson(ajax);
			return;
		}
		if (StrKit.isBlank(auth_code)) {
			ajax.addError("auth_code参数错误");
			renderJson(ajax);
			return;
		}
		
		String ip = IpKit.getRealIp(getRequest());
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}
		
		Map<String, String> params = WxPayApiConfigKit.getWxPayApiConfig()
				.setAttach("IJPay 测试  -By Javen")
				.setBody("IJPay 刷卡支付测试 -By Javen")
				.setSpbillCreateIp(ip)
				.setTotalFee(total_fee)
				.setAuthCode(auth_code)
				.setTradeType(TradeType.MICROPAY)
				.setNotifyUrl(notify_url)
				.setOutTradeNo(String.valueOf(System.currentTimeMillis()))
				.build();
				
		String xmlResult =  WxPayApi.micropay(false,params);
		
		//同步返回结果
		log.info("xmlResult:"+xmlResult);
		
		Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
		String return_code = result.get("return_code");
		String return_msg = result.get("return_msg");
		if (!PaymentKit.codeIsOK(return_code)) {
			//通讯失败 
			String err_code = result.get("err_code");
			if (StrKit.notBlank(err_code)) {
				//用户支付中，需要输入密码
				if (err_code.equals("USERPAYING")) {
					//等待5秒后调用【查询订单API】https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=9_2
					
				}
			}
			log.info("提交刷卡支付失败>>"+xmlResult);
			ajax.addError(return_msg);
			renderJson(ajax);
			return;
		}
		
		String result_code = result.get("result_code");
		if (!PaymentKit.codeIsOK(result_code)) {
			//支付失败
			//支付失败
			log.info("支付失败>>"+xmlResult);
			String err_code_des = result.get("err_code_des");
			
			ajax.addError(err_code_des);
			renderJson(ajax);
			return;
		}
		
		//支付成功 
		
		renderText(xmlResult);
	}
	
	/**
	 * 微信APP支付
	 * 已测试
	 */
	public void appPay(){
		//不用设置授权目录域名
		//统一下单地址 https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_1#
		
		String ip = IpKit.getRealIp(getRequest());
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}
		
		Map<String, String> params = WxPayApiConfigKit.getWxPayApiConfig()
				.setAttach("IJPay 测试  -By Javen")
				.setBody("IJPay App付测试  -By Javen")
				.setSpbillCreateIp(ip)
				.setTotalFee("100")
				.setTradeType(WxPayApi.TradeType.APP)
				.setNotifyUrl(notify_url)
				.setOutTradeNo(String.valueOf(System.currentTimeMillis()))
				.build();
				
		String xmlResult =  WxPayApi.pushOrder(false,params);
		
log.info(xmlResult);
		Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
		
		String return_code = result.get("return_code");
		String return_msg = result.get("return_msg");
		if (!PaymentKit.codeIsOK(return_code)) {
			ajax.addError(return_msg);
			renderJson(ajax);
			return;
		}
		String result_code = result.get("result_code");
		if (!PaymentKit.codeIsOK(result_code)) {
			ajax.addError(return_msg);
			renderJson(ajax);
			return;
		}
		// 以下字段在return_code 和result_code都为SUCCESS的时候有返回
		String prepay_id = result.get("prepay_id");
		//封装调起微信支付的参数 https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_12
		Map<String, String> packageParams = new HashMap<String, String>();
		packageParams.put("appid", WxPayApiConfigKit.getWxPayApiConfig().getAppId());
		packageParams.put("partnerid", WxPayApiConfigKit.getWxPayApiConfig().getMchId());
		packageParams.put("prepayid", prepay_id);
		packageParams.put("package", "Sign=WXPay");
		packageParams.put("noncestr", System.currentTimeMillis() + "");
		packageParams.put("timestamp", System.currentTimeMillis() / 1000 + "");
		String packageSign = PaymentKit.createSign(packageParams, WxPayApiConfigKit.getWxPayApiConfig().getPaternerKey());
		packageParams.put("sign", packageSign);
		
		String jsonStr = JsonKit.toJson(packageParams);
log.info("最新返回apk的参数:"+jsonStr);
		renderJson(jsonStr);
	}
	
	
	/**
	 * 微信小程序支付
	 */
	public void aappPay(){
		
		String ip = IpKit.getRealIp(getRequest());
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}
		
		Map<String, String> params = WxPayApiConfigKit.getWxPayApiConfig()
				.setAttach("IJPay 测试  -By Javen")
				.setBody("IJPay 小程序支付测试  -By Javen")
				.setSpbillCreateIp(ip)
				.setTotalFee("100")
				.setTradeType(WxPayApi.TradeType.JSAPI)
				.setNotifyUrl(notify_url)
				.setOutTradeNo(String.valueOf(System.currentTimeMillis()))
				.build();
				
		String xmlResult =  WxPayApi.pushOrder(false,params);
		
log.info(xmlResult);
		Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
		
		String return_code = result.get("return_code");
		String return_msg = result.get("return_msg");
		if (!PaymentKit.codeIsOK(return_code)) {
			ajax.addError(return_msg);
			renderJson(ajax);
			return;
		}
		String result_code = result.get("result_code");
		if (!PaymentKit.codeIsOK(result_code)) {
			ajax.addError(return_msg);
			renderJson(ajax);
			return;
		}
		// 以下字段在return_code 和result_code都为SUCCESS的时候有返回
		String prepay_id = result.get("prepay_id");
		//封装调起微信支付的参数https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=7_7&index=5
		Map<String, String> packageParams = new HashMap<String, String>();
		packageParams.put("appId", WxPayApiConfigKit.getWxPayApiConfig().getAppId());
		packageParams.put("timeStamp", System.currentTimeMillis() / 1000 + "");
		packageParams.put("nonceStr", System.currentTimeMillis() + "");
		packageParams.put("package", "prepay_id="+prepay_id);
		packageParams.put("signType", "MD5");
		String packageSign = PaymentKit.createSign(packageParams, WxPayApiConfigKit.getWxPayApiConfig().getPaternerKey());
		packageParams.put("paySign", packageSign);
		
		String jsonStr = JsonKit.toJson(packageParams);
log.info("最新返回小程序支付的参数:"+jsonStr);
		renderJson(jsonStr);
	}
	/**
	 * 企业付款到零钱
	 */
	public void transfers() {
		String openId = getSessionAttr("openId");
		openId = "oRMVFv8zhSH--EhJiu3z9G3kNX-o";
		Map<String, String> params = new HashMap<String, String>();
		params.put("mch_appid", appid);
		params.put("mchid", mch_id);
		String nonceStr = String.valueOf(System.currentTimeMillis());
		params.put("nonce_str", nonceStr);
		String partnerTradeNo = String.valueOf(System.currentTimeMillis());
		params.put("partner_trade_no", partnerTradeNo);
		params.put("openid", openId);
		params.put("check_name", "NO_CHECK");
		params.put("amount", "100");
		params.put("desc", "IJPay提现测试-By Javen");
		String ip = IpKit.getRealIp(getRequest());
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}
		params.put("spbill_create_ip", ip);

		params.put("sign", PaymentKit.createSign(params, partnerKey));
System.out.println("certPath>"+certPath);
		// 提现
		String transfers = WxPayApi.transfers(params, certPath, mch_id);
		
		log.info("提现结果:" + transfers);
		System.out.println("提现结果:" + transfers);
		
		Map<String, String> map = PaymentKit.xmlToMap(transfers);
		String return_code = map.get("return_code");
		String result_code = null;
		if (("SUCCESS").equals(return_code)) {
			result_code = map.get("result_code");
			if (("SUCCESS").equals(result_code)) {
				//提现成功
			} else {
				//提现失败
			}
		}
		renderText(transfers);
	}
	/**
	 * 查询企业付款到零钱
	 */
	public void transferInfo() {
		try {
			String partner_trade_no = getPara("partner_trade_no");
			Map<String, String> params = new HashMap<String, String>();
			params.put("nonce_str", System.currentTimeMillis()+"");
			params.put("partner_trade_no", partner_trade_no);
			params.put("mch_id", mch_id);
			params.put("appid", appid);
			params.put("sign", PaymentKit.createSign(params, partnerKey));
			
			String transferInfo = WxPayApi.getTransferInfo(params , certPath, mch_id);
			renderText(transferInfo);
		} catch (Exception e) {
			e.printStackTrace();
			renderText(e.getMessage());
		}
	}
	
	
	/**
	 * 获取RSA加密公钥
	 * 接口默认输出PKCS#1格式的公钥，商户需根据自己开发的语言选择公钥格式
	 * Java RSA加密需要使用PKCS#8 不然会出现异常algid parse error, not a sequence​
	 * PKCS#1 转 PKCS#8:
	 * openssl rsa -RSAPublicKey_in -in <filename> -pubout
	 */
	public void getPublicKey(){
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("mch_id", mch_id);
			params.put("nonce_str", String.valueOf(System.currentTimeMillis()));
			params.put("sign_type", "MD5");
			String createSign = PaymentKit.createSign(params, partnerKey);
			params.put("sign", createSign);
			String publicKeyStr = WxPayApi.getPublicKey(params , certPath, mch_id);
			renderText(publicKeyStr);
		} catch (Exception e) {
			e.printStackTrace();
			renderText(e.getMessage());
		}
	}
	/**
	 * 企业付款到银行卡
	 */
	public void payBank() {
		try {
			//通过WxPayApi.getPublicKey接口获取RSA加密公钥
			//假设获取到的RSA加密公钥为PUBLIC_KEY(PKCS#8格式)  
			final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6Bl76IwSvBTiibZ+CNRUA6BfahMshZ0WJpHD1GpmvcQjeN6Yrv6c9eIl6gB4nU3isN7bn+LmoVTpH1gHViaV2YyG/zXj4z4h7r+V+ezesMqqorEg38BCNUHNmhnw4/C0I4gBAQ4x0SJOGnfKGZKR9yzvbkJtvEn732JcEZCbdTZmaxkwlenXvM+mStcJaxBCB/h5xJ5VOF5nDbTPzLphIpzddr3zx/Jxjna9QB1v/YSKYXn+iuwruNUXGCvvxBWaBGKrjOdRTRy9adWOgNmtuYDQJ2YOfG8PtPe06ELKjmr2CfaAGrKKUroyaGvy3qxAV0PlT+UQ4ADSXWt/zl0o5wIDAQAB";	
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("mch_id", mch_id);
			params.put("partner_trade_no", System.currentTimeMillis()+"");
			params.put("nonce_str", System.currentTimeMillis()+"");
			params.put("enc_bank_no", RSAUtils.encryptByPublicKeyByWx("银行卡号", PUBLIC_KEY));//收款方银行卡号
			params.put("enc_true_name", RSAUtils.encryptByPublicKeyByWx("银行卡持有人姓名", PUBLIC_KEY));//收款方用户名	
			params.put("bank_code", "1001");//收款方开户行		
			params.put("amount", "1");
			params.put("desc", "IJPay 测试付款到银行卡-By Javen");
			params.put("sign", PaymentKit.createSign(params, partnerKey));
			String payBank = WxPayApi.payBank(params , certPath, mch_id);
			renderText(payBank);
		} catch (Exception e) {
			e.printStackTrace();
			renderText(e.getMessage());
		}
	}
	/**
	 * 查询企业付款到银行
	 */
	public void queryBank() {
		try {
			String partner_trade_no = getPara("partner_trade_no");
			Map<String, String> params = new HashMap<String, String>();
			params.put("mch_id", mch_id);
			params.put("partner_trade_no", partner_trade_no);
			params.put("nonce_str", System.currentTimeMillis()+"");
			params.put("sign", PaymentKit.createSign(params, partnerKey));
			String queryBank = WxPayApi.queryBank(params, certPath, mch_id);
			renderText(queryBank);
		} catch (Exception e) {
			e.printStackTrace();
			renderText(e.getMessage());
		}
		
	}
	
	/**
	 * 微信退款
	 */
	public void refund() {
		String transaction_id = getPara("transactionId");
		String out_trade_no = getPara("out_trade_no");
		
		if (StrKit.isBlank(out_trade_no) && StrKit.isBlank(transaction_id)) {
			renderText("transactionId、out_trade_no二选一");
			return;
		}
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", appid);
		params.put("mch_id", mch_id);
		params.put("nonce_str", System.currentTimeMillis()+"");
		if (StrKit.notBlank(transaction_id)) {
			params.put("transaction_id", transaction_id);
		}else {
			params.put("out_trade_no", out_trade_no);
		}
		params.put("out_refund_no", System.currentTimeMillis()+"");
		params.put("total_fee", "1");
		params.put("refund_fee", "1");
		params.put("sign", PaymentKit.createSign(params, partnerKey));
		String refund = WxPayApi.orderRefund(false, params , certPath, mch_id);
		renderText(refund);
	}
	
	/**
	 * 微信退款查询
	 */
	public void refundQuery() {
		String transaction_id = getPara("transactionId");
		String out_trade_no = getPara("out_trade_no");
		String out_refund_no = getPara("out_refund_no");
		String refund_id = getPara("refund_id");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", appid);
		params.put("mch_id", mch_id);
		params.put("nonce_str", System.currentTimeMillis()+"");
		params.put("transaction_id", transaction_id);
		params.put("out_trade_no", out_trade_no);
		params.put("out_refund_no", out_refund_no);
		params.put("refund_id", refund_id);
		params.put("out_refund_no", System.currentTimeMillis()+"");
		params.put("sign", PaymentKit.createSign(params, partnerKey));
		String refund = WxPayApi.orderRefundQuery(false, params);
		renderText(refund);
	}
	
	
	public void pay_notify() {
		//获取所有的参数
		StringBuffer sbf=new StringBuffer();
				 
		Enumeration<String>  en=getParaNames();
		while (en.hasMoreElements()) {
			Object o= en.nextElement();
			sbf.append(o.toString()+"="+getPara(o.toString()));
		}
		
		log.error("支付通知参数："+sbf.toString());
		
		// 支付结果通用通知文档: https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7
		String xmlMsg = HttpKit.readData(getRequest());
		System.out.println("支付通知="+xmlMsg);
		Map<String, String> params = PaymentKit.xmlToMap(xmlMsg);
		
//		String appid  = params.get("appid");
//		//商户号
//		String mch_id  = params.get("mch_id");
		String result_code  = params.get("result_code");
//		String openId      = params.get("openid");
//		//交易类型
//		String trade_type      = params.get("trade_type");
//		//付款银行
//		String bank_type      = params.get("bank_type");
//		// 总金额
//		String total_fee     = params.get("total_fee");
//		//现金支付金额
//		String cash_fee     = params.get("cash_fee");
//		// 微信支付订单号
//		String transaction_id      = params.get("transaction_id");
//		// 商户订单号
//		String out_trade_no      = params.get("out_trade_no");
//		// 支付完成时间，格式为yyyyMMddHHmmss
//		String time_end      = params.get("time_end");
		
		/////////////////////////////以下是附加参数///////////////////////////////////
		
		String attach      = params.get("attach");
//		String fee_type      = params.get("fee_type");
//		String is_subscribe      = params.get("is_subscribe");
//		String err_code      = params.get("err_code");
//		String err_code_des      = params.get("err_code_des");
		
		
		// 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态
		// 避免已经成功、关闭、退款的订单被再次更新
//		Order order = Order.dao.getOrderByTransactionId(transaction_id);
//		if (order==null) {
			if(PaymentKit.verifyNotify(params, WxPayApiConfigKit.getWxPayApiConfig().getPaternerKey())){
				if (("SUCCESS").equals(result_code)) {
					//更新订单信息
					log.warn("更新订单信息:"+attach);
					
					//发送通知等
					
					Map<String, String> xml = new HashMap<String, String>();
					xml.put("return_code", "SUCCESS");
					xml.put("return_msg", "OK");
					renderText(PaymentKit.toXml(xml));
					return;
				}
			}
//		}
		renderText("");
	}
}
