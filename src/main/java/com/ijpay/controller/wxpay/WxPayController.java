package com.ijpay.controller.wxpay;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ijpay.entity.H5ScencInfo;
import com.ijpay.entity.H5ScencInfo.H5;
import com.ijpay.entity.WxPayBean;
import com.jpay.ext.kit.HttpKit;
import com.jpay.ext.kit.IpKit;
import com.jpay.ext.kit.PaymentKit;
import com.jpay.ext.kit.StrKit;
import com.jpay.ext.kit.ZxingKit;
import com.jpay.secure.RSAUtils;
import com.jpay.vo.AjaxResult;
import com.jpay.weixin.api.WxPayApi;
import com.jpay.weixin.api.WxPayApi.TradeType;
import com.jpay.weixin.api.WxPayApiConfig;
import com.jpay.weixin.api.WxPayApiConfig.PayModel;
import com.jpay.weixin.api.WxPayApiConfigKit;

@Controller
@RequestMapping("/wxpay")
public class WxPayController extends WxPayApiController {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private AjaxResult result = new AjaxResult();

	@Autowired
	WxPayBean wxPayBean;

	String notify_url;

	@Override
	public WxPayApiConfig getApiConfig() {
		notify_url = wxPayBean.getDomain().concat("/wxpay/pay_notify");
		return WxPayApiConfig.New()
				.setAppId(wxPayBean.getAppId())
				.setMchId(wxPayBean.getMchId())
				.setPaternerKey(wxPayBean.getPartnerKey())
				.setPayModel(PayModel.BUSINESSMODEL);
	}

	@RequestMapping("")
	@ResponseBody
	public String index() {
		log.info("欢迎使用IJPay,商户模式下微信支付 - by Javen");
		log.info(wxPayBean.toString());
		return ("欢迎使用IJPay 商户模式下微信支付  - by Javen");
	}

	@RequestMapping("/test")
	@ResponseBody
	public String test() {
		return wxPayBean.toString();
	}

	@RequestMapping("/getKey")
	@ResponseBody
	public String getKey() {
		return WxPayApi.getsignkey(wxPayBean.getAppId(), wxPayBean.getPartnerKey());
	}

	@RequestMapping("/ctp")
	@ResponseBody
	public String ctp(HttpServletRequest request) {
		return request.getSession().getServletContext().getRealPath("/");
	}

	/**
	 * 微信H5 支付 注意：必须再web页面中发起支付且域名已添加到开发配置中
	 */
	@RequestMapping(value = "/wapPay", method = { RequestMethod.POST, RequestMethod.GET })
	public void wapPay(HttpServletRequest request, HttpServletResponse response) {
		String ip = IpKit.getRealIp(request);
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}

		H5ScencInfo sceneInfo = new H5ScencInfo();

		H5 h5_info = new H5();
		h5_info.setType("Wap");
		// 此域名必须在商户平台--"产品中心"--"开发配置"中添加

		h5_info.setWap_url("https://pay.qq.com");
		h5_info.setWap_name("腾讯充值");
		sceneInfo.setH5_info(h5_info);

		Map<String, String> params = WxPayApiConfigKit
				.getWxPayApiConfig()
				.setAttach("IJPay H5支付测试  -By Javen")
				.setBody("IJPay H5支付测试  -By Javen")
				.setSpbillCreateIp(ip).setTotalFee("520")
				.setTradeType(TradeType.MWEB)
				.setNotifyUrl(notify_url)
				.setOutTradeNo(String.valueOf(System.currentTimeMillis()))
				.setSceneInfo(h5_info.toString())
				.build();

		String xmlResult = WxPayApi.pushOrder(false, params);
		log.info(xmlResult);
		Map<String, String> result = PaymentKit.xmlToMap(xmlResult);

		String return_code = result.get("return_code");
		String return_msg = result.get("return_msg");
		if (!PaymentKit.codeIsOK(return_code)) {
			log.error("return_code>" + return_code + " return_msg>" + return_msg);
			throw new RuntimeException(return_msg);
		}
		String result_code = result.get("result_code");
		if (!PaymentKit.codeIsOK(result_code)) {
			log.error("result_code>" + result_code + " return_msg>" + return_msg);
			throw new RuntimeException(return_msg);
		}
		// 以下字段在return_code 和result_code都为SUCCESS的时候有返回

		String prepay_id = result.get("prepay_id");
		String mweb_url = result.get("mweb_url");

		log.info("prepay_id:" + prepay_id + " mweb_url:" + mweb_url);
		try {
			response.sendRedirect(mweb_url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 公众号支付
	 */
	@RequestMapping(value = "/webPay", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public AjaxResult webPay(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("total_fee") String total_fee) {
		// openId，采用 网页授权获取 access_token API：SnsAccessTokenApi获取
		String openId = (String) request.getSession().getAttribute("openId");

		if (StrKit.isBlank(openId)) {
			result.addError("openId is null");
			return result;
		}
		if (StrKit.isBlank(total_fee)) {
			result.addError("请输入数字金额");
			return result;
		}

		String ip = IpKit.getRealIp(request);
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}

		Map<String, String> params = WxPayApiConfigKit
				.getWxPayApiConfig()
				.setAttach("IJPay 公众号支付测试  -By Javen")
				.setBody("IJPay 公众号支付测试  -By Javen")
				.setOpenId(openId)
				.setSpbillCreateIp(ip)
				.setTotalFee(total_fee)
				.setTradeType(TradeType.JSAPI).setNotifyUrl(notify_url)
				.setOutTradeNo(String.valueOf(System.currentTimeMillis()))
				.build();

		String xmlResult = WxPayApi.pushOrder(false, params);
		log.info(xmlResult);
		Map<String, String> resultMap = PaymentKit.xmlToMap(xmlResult);

		String return_code = resultMap.get("return_code");
		String return_msg = resultMap.get("return_msg");
		if (!PaymentKit.codeIsOK(return_code)) {
			result.addError(return_msg);
			return result;
		}
		String result_code = resultMap.get("result_code");
		if (!PaymentKit.codeIsOK(result_code)) {
			result.addError(return_msg);
			return result;
		}
		// 以下字段在return_code 和result_code都为SUCCESS的时候有返回

		String prepay_id = resultMap.get("prepay_id");

		Map<String, String> packageParams = PaymentKit.prepayIdCreateSign(prepay_id);

		String jsonStr = JSON.toJSONString(packageParams);
		result.success(jsonStr);
		return result;
	}

	/**
	 * 生成支付二维码（模式一）并在页面上显示
	 */
	@RequestMapping(value = "/scanCode1", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public AjaxResult scanCode1(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("productId") String product_id) {
		try {
			if (StrKit.isBlank(product_id)) {
				result.addError("productId is null");
				return result;
			}
			WxPayApiConfig config = WxPayApiConfigKit.getWxPayApiConfig();
			// 获取扫码支付（模式一）url

			String qrCodeUrl = WxPayApi.getCodeUrl(config.getAppId(), config.getMchId(), product_id,
					config.getPaternerKey(), true);
			log.info(qrCodeUrl);
			// 生成二维码保存的路径

			String name = "payQRCode1.png";
			System.out.println(request.getSession().getServletContext().getRealPath("/") + File.separator + name);
			Boolean encode = ZxingKit.encode(qrCodeUrl, BarcodeFormat.QR_CODE, 3, ErrorCorrectionLevel.H, "png", 200,
					200, request.getSession().getServletContext().getRealPath("/") + File.separator + name);
			if (encode) {
				// 在页面上显示
				result.success(name);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.addError("系统异常：" + e.getMessage());
			return result;
		}

		return null;
	}

	/**
	 * 扫码支付模式一回调 已测试
	 */
	@RequestMapping(value = "/wxpay", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String wxpay(HttpServletRequest request, HttpServletResponse response) {
		try {

			String result = HttpKit.readData(request);
			System.out.println("callBack_xml>>>" + result);
			/**
			 * 
			 * 获取返回的信息内容中各个参数的值
			 * 
			 */
			Map<String, String> map = PaymentKit.xmlToMap(result);
			for (String key : map.keySet()) {
				System.out.println("key= " + key + " and value= " + map.get(key));
			}

			String appid = map.get("appid");
			String openid = map.get("openid");
			String mch_id = map.get("mch_id");
			String is_subscribe = map.get("is_subscribe");
			String nonce_str = map.get("nonce_str");
			String product_id = map.get("product_id");
			String sign = map.get("sign");
			Map<String, String> packageParams = new HashMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("openid", openid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("is_subscribe", is_subscribe);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("product_id", product_id);

			String packageSign = PaymentKit.createSign(packageParams,
					WxPayApiConfigKit.getWxPayApiConfig().getPaternerKey());
			// 统一下单文档地址：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1

			String ip = IpKit.getRealIp(request);
			if (StrKit.isBlank(ip)) {
				ip = "127.0.0.1";
			}

			Map<String, String> params = WxPayApiConfigKit
					.getWxPayApiConfig()
					.setAttach("IJPay 扫码模式一测试  -By Javen")
					.setBody("IJPay 扫码模式一测试  -By Javen")
					.setOpenId(openid)
					.setSpbillCreateIp(ip)
					.setTotalFee("100")
					.setTradeType(TradeType.NATIVE)
					.setNotifyUrl(notify_url)
					.setOutTradeNo(String.valueOf(System.currentTimeMillis()))
					.build();

			String xmlResult = WxPayApi.pushOrder(false, params);
			log.info("prepay_xml>>>" + xmlResult);

			/**
			 * 
			 * 发送信息给微信服务器
			 * 
			 */
			Map<String, String> payResult = PaymentKit.xmlToMap(xmlResult);

			String return_code = payResult.get("return_code");
			String result_code = payResult.get("result_code");

			if (StrKit.notBlank(return_code) && StrKit.notBlank(result_code) && return_code.equalsIgnoreCase("SUCCESS")
					&& result_code.equalsIgnoreCase("SUCCESS")) {
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
				} else {
					prepayParams.put("result_code", "FAIL");
					prepayParams.put("err_code_des", "订单失效"); // result_code为FAIL时，添加该键值对，value值是微信告诉客户的信息

				}
				prepaySign = PaymentKit.createSign(prepayParams,
						WxPayApiConfigKit.getWxPayApiConfig().getPaternerKey());
				prepayParams.put("sign", prepaySign);
				String xml = PaymentKit.toXml(prepayParams);
				log.error(xml);
				return xml;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 扫码支付模式二 已测试
	 */
	@RequestMapping(value = "/scanCode2", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public AjaxResult scanCode2(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("total_fee") String total_fee) {

		// String openId="o5NJx1dVRilQI6uUVSaBDuLnM3iM";

		String openId = (String) request.getSession().getAttribute("openId");

		if (StrKit.isBlank(openId)) {
			result.addError("openId is null");
			return result;
		}
		if (StrKit.isBlank(total_fee)) {
			result.addError("支付金额不能为空");
			return result;
		}

		String ip = IpKit.getRealIp(request);
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}

		Map<String, String> params = WxPayApiConfigKit
				.getWxPayApiConfig()
				.setAttach("IJPay 测试  -By Javen")
				.setBody("IJPay 扫码支付2测试  -By Javen")
				.setOpenId(openId)
				.setSpbillCreateIp(ip)
				.setTotalFee(total_fee)
				.setTradeType(TradeType.NATIVE)
				.setNotifyUrl(notify_url)
				.setOutTradeNo(String.valueOf(System.currentTimeMillis()))
				.build();

		String xmlResult = WxPayApi.pushOrder(false, params);

		log.info(xmlResult);
		Map<String, String> resultMap = PaymentKit.xmlToMap(xmlResult);

		String return_code = resultMap.get("return_code");
		String return_msg = resultMap.get("return_msg");
		if (!PaymentKit.codeIsOK(return_code)) {
			System.out.println(xmlResult);
			result.addError("error:" + return_msg);
			return result;
		}
		String result_code = resultMap.get("result_code");
		if (!PaymentKit.codeIsOK(result_code)) {
			System.out.println(xmlResult);
			result.addError("error:" + return_msg);
			return result;
		}
		// 生成预付订单success

		String qrCodeUrl = resultMap.get("code_url");
		String name = "payQRCode2.png";

		Boolean encode = ZxingKit.encode(qrCodeUrl, BarcodeFormat.QR_CODE, 3, ErrorCorrectionLevel.H, "png", 200, 200,
				request.getSession().getServletContext().getRealPath("/") + File.separator + name);
		if (encode) {
			// renderQrCode(qrCodeUrl, 200, 200);

			// 在页面上显示

			result.success(name);
			return result;
		}
		return null;
	}

	/**
	 * 刷卡支付 已测试
	 */
	@RequestMapping(value = "/micropay", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public AjaxResult micropay(HttpServletRequest request, HttpServletResponse response) {
		String auth_code = request.getParameter("auth_code");
		String total_fee = request.getParameter("total_fee");

		if (StrKit.isBlank(total_fee)) {
			result.addError("支付金额不能为空");
			return result;
		}
		if (StrKit.isBlank(auth_code)) {
			result.addError("auth_code参数错误");
			return result;
		}

		String ip = IpKit.getRealIp(request);
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}

		Map<String, String> params = WxPayApiConfigKit
				.getWxPayApiConfig()
				.setAttach("IJPay 测试  -By Javen")
				.setBody("IJPay 刷卡支付测试 -By Javen")
				.setSpbillCreateIp(ip)
				.setTotalFee(total_fee)
				.setAuthCode(auth_code)
				.setTradeType(TradeType.MICROPAY)
				.setNotifyUrl(notify_url)
				.setOutTradeNo(String.valueOf(System.currentTimeMillis()))
				.build();

		String xmlResult = WxPayApi.micropay(false, params);

		// 同步返回结果

		log.info("xmlResult:" + xmlResult);

		Map<String, String> resultMap = PaymentKit.xmlToMap(xmlResult);
		String return_code = resultMap.get("return_code");
		String return_msg = resultMap.get("return_msg");
		if (!PaymentKit.codeIsOK(return_code)) {
			// 通讯失败

			String err_code = resultMap.get("err_code");
			if (StrKit.notBlank(err_code)) {
				// 用户支付中，需要输入密码

				if (err_code.equals("USERPAYING")) {
					// 等待5秒后调用【查询订单API】https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=9_2

				}
			}
			log.info("提交刷卡支付失败>>" + xmlResult);
			result.addError(return_msg);
			return result;
		}

		String result_code = resultMap.get("result_code");
		if (!PaymentKit.codeIsOK(result_code)) {
			// 支付失败
			log.info("支付失败>>" + xmlResult);
			String err_code_des = resultMap.get("err_code_des");

			result.addError(err_code_des);
			return result;
		}
		// 支付成功
		result.success(xmlResult);
		return result;
	}

	/**
	 * 微信APP支付
	 */
	@RequestMapping(value = "/appPay", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public AjaxResult appPay(HttpServletRequest request) {
		// 不用设置授权目录域名
		// 统一下单地址 https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_1#

		String ip = IpKit.getRealIp(request);
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}

		Map<String, String> params = WxPayApiConfigKit
				.getWxPayApiConfig()
				.setAttach("IJPay 测试  -By Javen")
				.setBody("IJPay App付测试  -By Javen")
				.setSpbillCreateIp(ip).setTotalFee("100")
				.setTradeType(WxPayApi.TradeType.APP)
				.setNotifyUrl(notify_url)
				.setOutTradeNo(String.valueOf(System.currentTimeMillis()))
				.build();

		String xmlResult = WxPayApi.pushOrder(false, params);

		log.info(xmlResult);
		Map<String, String> resultMap = PaymentKit.xmlToMap(xmlResult);

		String return_code = resultMap.get("return_code");
		String return_msg = resultMap.get("return_msg");
		if (!PaymentKit.codeIsOK(return_code)) {
			log.info(xmlResult);
			result.addError(return_msg);
			return result;
		}
		String result_code = resultMap.get("result_code");
		if (!PaymentKit.codeIsOK(result_code)) {
			log.info(xmlResult);
			result.addError(return_msg);
			return result;
		}
		// 以下字段在return_code 和result_code都为SUCCESS的时候有返回

		String prepay_id = resultMap.get("prepay_id");
		// 封装调起微信支付的参数 https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_12

		Map<String, String> packageParams = new HashMap<String, String>();
		packageParams.put("appid", WxPayApiConfigKit.getWxPayApiConfig().getAppId());
		packageParams.put("partnerid", WxPayApiConfigKit.getWxPayApiConfig().getMchId());
		packageParams.put("prepayid", prepay_id);
		packageParams.put("package", "Sign=WXPay");
		packageParams.put("noncestr", System.currentTimeMillis() + "");
		packageParams.put("timestamp", System.currentTimeMillis() / 1000 + "");
		String packageSign = PaymentKit.createSign(packageParams,
				WxPayApiConfigKit.getWxPayApiConfig().getPaternerKey());
		packageParams.put("sign", packageSign);

		String jsonStr = JSON.toJSONString(packageParams);
		log.info("最新返回apk的参数:" + jsonStr);
		result.success(jsonStr);
		return result;
	}

	/**
	 * 微信小程序支付
	 */
	@RequestMapping(value = "/aappPay", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public AjaxResult aappPay(HttpServletRequest request) {

		String ip = IpKit.getRealIp(request);
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}

		Map<String, String> params = WxPayApiConfigKit
				.getWxPayApiConfig()
				.setAttach("IJPay 测试  -By Javen")
				.setBody("IJPay 小程序支付测试  -By Javen")
				.setSpbillCreateIp(ip).setTotalFee("100")
				.setTradeType(WxPayApi.TradeType.JSAPI)
				.setNotifyUrl(notify_url)
				.setOutTradeNo(String.valueOf(System.currentTimeMillis()))
				.build();

		String xmlResult = WxPayApi.pushOrder(false, params);

		log.info(xmlResult);
		Map<String, String> resultMap = PaymentKit.xmlToMap(xmlResult);

		String return_code = resultMap.get("return_code");
		String return_msg = resultMap.get("return_msg");
		if (!PaymentKit.codeIsOK(return_code)) {
			result.addError(return_msg);
			return result;
		}
		String result_code = resultMap.get("result_code");
		if (!PaymentKit.codeIsOK(result_code)) {
			result.addError(return_msg);
			return result;
		}
		// 以下字段在return_code 和result_code都为SUCCESS的时候有返回
		String prepay_id = resultMap.get("prepay_id");
		// 封装调起微信支付的参数https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=7_7&index=5
		Map<String, String> packageParams = new HashMap<String, String>();
		packageParams.put("appId", WxPayApiConfigKit.getWxPayApiConfig().getAppId());
		packageParams.put("timeStamp", System.currentTimeMillis() / 1000 + "");
		packageParams.put("nonceStr", System.currentTimeMillis() + "");
		packageParams.put("package", "prepay_id=" + prepay_id);
		packageParams.put("signType", "MD5");
		String packageSign = PaymentKit.createSign(packageParams,
				WxPayApiConfigKit.getWxPayApiConfig().getPaternerKey());
		packageParams.put("paySign", packageSign);

		String jsonStr = JSON.toJSONString(packageParams);
		log.info("最新返回小程序支付的参数:" + jsonStr);
		result.success(jsonStr);
		return result;
	}

	/**
	 * 企业付款到零钱
	 */
	@RequestMapping(value = "/transfers", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String transfers(HttpServletRequest request, @RequestParam("openId") String openId) {
		// openId = "oRMVFv8zhSH--EhJiu3z9G3kNX-o";
		Map<String, String> params = new HashMap<String, String>();
		params.put("mch_appid", wxPayBean.getAppId());
		params.put("mchid", wxPayBean.getMchId());
		String nonceStr = String.valueOf(System.currentTimeMillis());
		params.put("nonce_str", nonceStr);
		String partnerTradeNo = String.valueOf(System.currentTimeMillis());
		params.put("partner_trade_no", partnerTradeNo);
		params.put("openid", openId);
		params.put("check_name", "NO_CHECK");
		params.put("amount", "100");
		params.put("desc", "IJPay提现测试-By Javen");
		String ip = IpKit.getRealIp(request);
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}
		params.put("spbill_create_ip", ip);

		params.put("sign", PaymentKit.createSign(params, wxPayBean.getPartnerKey()));
		System.out.println("certPath>" + wxPayBean.getCertPath());
		// 提现
		String transfers = WxPayApi.transfers(params, wxPayBean.getCertPath(), wxPayBean.getMchId());

		log.info("提现结果:" + transfers);
		System.out.println("提现结果:" + transfers);

		Map<String, String> map = PaymentKit.xmlToMap(transfers);
		String return_code = map.get("return_code");
		String result_code = null;
		if (("SUCCESS").equals(return_code)) {
			result_code = map.get("result_code");
			if (("SUCCESS").equals(result_code)) {
				// 提现成功
			} else {
				// 提现失败
			}
		}
		return transfers;
	}

	/**
	 * 查询企业付款到零钱
	 */
	@RequestMapping(value = "/transferInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String transferInfo(@RequestParam("partner_trade_no") String partner_trade_no) {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("nonce_str", System.currentTimeMillis() + "");
			params.put("partner_trade_no", partner_trade_no);
			params.put("mch_id", wxPayBean.getMchId());
			params.put("appid", wxPayBean.getAppId());
			params.put("sign", PaymentKit.createSign(params, wxPayBean.getPartnerKey()));

			return WxPayApi.getTransferInfo(params, wxPayBean.getCertPath(), wxPayBean.getMchId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取RSA加密公钥 接口默认输出PKCS#1格式的公钥，商户需根据自己开发的语言选择公钥格式 Java RSA加密需要使用PKCS#8
	 * 不然会出现异常algid parse error, not a sequence​ PKCS#1 转 PKCS#8: openssl rsa
	 * -RSAPublicKey_in -in <filename> -pubout
	 */
	@RequestMapping(value = "/getPublicKey", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String getPublicKey() {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("mch_id", wxPayBean.getMchId());
			params.put("nonce_str", String.valueOf(System.currentTimeMillis()));
			params.put("sign_type", "MD5");
			String createSign = PaymentKit.createSign(params, wxPayBean.getPartnerKey());
			params.put("sign", createSign);
			String publicKeyStr = WxPayApi.getPublicKey(params, wxPayBean.getCertPath(), wxPayBean.getMchId());
			return publicKeyStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 企业付款到银行卡
	 */
	@RequestMapping(value = "/payBank", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String payBank() {
		try {
			// 通过WxPayApi.getPublicKey接口获取RSA加密公钥
			// 假设获取到的RSA加密公钥为PUBLIC_KEY(PKCS#8格式)
			final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6Bl76IwSvBTiibZ+CNRUA6BfahMshZ0WJpHD1GpmvcQjeN6Yrv6c9eIl6gB4nU3isN7bn+LmoVTpH1gHViaV2YyG/zXj4z4h7r+V+ezesMqqorEg38BCNUHNmhnw4/C0I4gBAQ4x0SJOGnfKGZKR9yzvbkJtvEn732JcEZCbdTZmaxkwlenXvM+mStcJaxBCB/h5xJ5VOF5nDbTPzLphIpzddr3zx/Jxjna9QB1v/YSKYXn+iuwruNUXGCvvxBWaBGKrjOdRTRy9adWOgNmtuYDQJ2YOfG8PtPe06ELKjmr2CfaAGrKKUroyaGvy3qxAV0PlT+UQ4ADSXWt/zl0o5wIDAQAB";

			Map<String, String> params = new HashMap<String, String>();
			params.put("mch_id", wxPayBean.getMchId());
			params.put("partner_trade_no", System.currentTimeMillis() + "");
			params.put("nonce_str", System.currentTimeMillis() + "");
			params.put("enc_bank_no", RSAUtils.encryptByPublicKeyByWx("银行卡号", PUBLIC_KEY));// 收款方银行卡号
			params.put("enc_true_name", RSAUtils.encryptByPublicKeyByWx("银行卡持有人姓名", PUBLIC_KEY));// 收款方用户名
			params.put("bank_code", "1001");// 收款方开户行
			params.put("amount", "1");
			params.put("desc", "IJPay 测试付款到银行卡-By Javen");
			params.put("sign", PaymentKit.createSign(params, wxPayBean.getPartnerKey()));
			String payBank = WxPayApi.payBank(params, wxPayBean.getCertPath(), wxPayBean.getMchId());
			return payBank;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询企业付款到银行
	 */
	@RequestMapping(value = "/queryBank", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String queryBank(@RequestParam("partner_trade_no") String partner_trade_no) {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("mch_id", wxPayBean.getMchId());
			params.put("partner_trade_no", partner_trade_no);
			params.put("nonce_str", System.currentTimeMillis() + "");
			params.put("sign", PaymentKit.createSign(params, wxPayBean.getPartnerKey()));
			String queryBank = WxPayApi.queryBank(params, wxPayBean.getCertPath(), wxPayBean.getMchId());
			return queryBank;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 微信退款
	 */
	@RequestMapping(value = "/refund", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String refund(@RequestParam("transactionId") String transactionId,
			@RequestParam("out_trade_no") String out_trade_no) {

		if (StrKit.isBlank(out_trade_no) && StrKit.isBlank(transactionId)) {
			return "transactionId、out_trade_no二选一";
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", wxPayBean.getAppId());
		params.put("mch_id", wxPayBean.getMchId());
		params.put("nonce_str", System.currentTimeMillis() + "");
		if (StrKit.notBlank(transactionId)) {
			params.put("transaction_id", transactionId);
		} else {
			params.put("out_trade_no", out_trade_no);
		}
		params.put("out_refund_no", System.currentTimeMillis() + "");
		params.put("total_fee", "1");
		params.put("refund_fee", "1");
		params.put("sign", PaymentKit.createSign(params, wxPayBean.getPartnerKey()));
		String refund = WxPayApi.orderRefund(false, params, wxPayBean.getCertPath(), wxPayBean.getMchId());
		return refund;
	}

	/**
	 * 微信退款查询
	 */
	@RequestMapping(value = "/refundQuery", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String refundQuery(@RequestParam("transactionId") String transactionId,
			@RequestParam("out_trade_no") String out_trade_no, @RequestParam("out_refund_no") String out_refund_no,
			@RequestParam("refund_id") String refund_id) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", wxPayBean.getAppId());
		params.put("mch_id", wxPayBean.getMchId());
		params.put("nonce_str", System.currentTimeMillis() + "");
		params.put("transaction_id", transactionId);
		params.put("out_trade_no", out_trade_no);
		params.put("out_refund_no", out_refund_no);
		params.put("refund_id", refund_id);
		params.put("out_refund_no", System.currentTimeMillis() + "");
		params.put("sign", PaymentKit.createSign(params, wxPayBean.getPartnerKey()));
		String refund = WxPayApi.orderRefundQuery(false, params);
		return refund;
	}

	@RequestMapping(value = "/pay_notify", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String pay_notify(HttpServletRequest request) {
		// 支付结果通用通知文档: https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7

		String xmlMsg = HttpKit.readData(request);
		System.out.println("支付通知=" + xmlMsg);
		Map<String, String> params = PaymentKit.xmlToMap(xmlMsg);
		// String appid = params.get("appid");
		// //商户号
		// String mch_id = params.get("mch_id");
		String result_code = params.get("result_code");
		// String openId = params.get("openid");
		// //交易类型
		// String trade_type = params.get("trade_type");
		// //付款银行
		// String bank_type = params.get("bank_type");
		// // 总金额
		// String total_fee = params.get("total_fee");
		// //现金支付金额
		// String cash_fee = params.get("cash_fee");
		// // 微信支付订单号
		// String transaction_id = params.get("transaction_id");
		// // 商户订单号
		// String out_trade_no = params.get("out_trade_no");
		// // 支付完成时间，格式为yyyyMMddHHmmss
		// String time_end = params.get("time_end");

		///////////////////////////// 以下是附加参数///////////////////////////////////

		String attach = params.get("attach");
		// String fee_type = params.get("fee_type");
		// String is_subscribe = params.get("is_subscribe");
		// String err_code = params.get("err_code");
		// String err_code_des = params.get("err_code_des");
		// 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态
		// 避免已经成功、关闭、退款的订单被再次更新
		// Order order = Order.dao.getOrderByTransactionId(transaction_id);
		// if (order==null) {
		if (PaymentKit.verifyNotify(params, WxPayApiConfigKit.getWxPayApiConfig().getPaternerKey())) {
			if (("SUCCESS").equals(result_code)) {
				// 更新订单信息
				log.warn("更新订单信息:" + attach);
				// 发送通知等
				Map<String, String> xml = new HashMap<String, String>();
				xml.put("return_code", "SUCCESS");
				xml.put("return_msg", "OK");
				return PaymentKit.toXml(xml);
			}
		}
		// }

		return null;
	}

}
