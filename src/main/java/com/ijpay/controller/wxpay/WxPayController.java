package com.ijpay.controller.wxpay;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.ijpay.entity.H5ScencInfo;
import com.ijpay.entity.H5ScencInfo.H5;
import com.ijpay.entity.WxPayBean;
import com.jpay.ext.kit.IpKit;
import com.jpay.ext.kit.PaymentKit;
import com.jpay.ext.kit.StrKit;
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
	
	@RequestMapping("/getKey")
	@ResponseBody
	public String getKey(){
		return WxPayApi.getsignkey(wxPayBean.getAppId(), wxPayBean.getPartnerKey());
	}
	
	
	/**
	 * 微信H5 支付
	 * 注意：必须再web页面中发起支付且域名已添加到开发配置中
	 */
	@RequestMapping("/wapPay")
	public void wapPay(HttpServletRequest request,HttpServletResponse response){
		String ip = IpKit.getRealIp(request);
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
			log.error("return_code>"+return_code+" return_msg>"+return_msg);
			throw new RuntimeException(return_msg);
		}
		String result_code = result.get("result_code");
		if (!PaymentKit.codeIsOK(result_code)) {
			log.error("result_code>"+result_code+" return_msg>"+return_msg);
			throw new RuntimeException(return_msg);
		}
		// 以下字段在return_code 和result_code都为SUCCESS的时候有返回
		
		String prepay_id = result.get("prepay_id");
		String mweb_url = result.get("mweb_url");
		
		log.info("prepay_id:"+prepay_id+" mweb_url:"+mweb_url);
		try {
			response.sendRedirect(mweb_url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 公众号支付
	 */
	@RequestMapping("/webPay")
	@ResponseBody
	public AjaxResult webPay(HttpServletRequest request,HttpServletResponse response,
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

}
