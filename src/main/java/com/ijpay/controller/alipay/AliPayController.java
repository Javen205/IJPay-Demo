package com.ijpay.controller.alipay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.ijpay.entity.AliPayBean;
import com.jpay.alipay.AliPayApi;
import com.jpay.alipay.AliPayApiConfig;
import com.jpay.alipay.AliPayApiConfigKit;
import com.jpay.util.StringUtils;


@Controller
@RequestMapping("/alipay")
public class AliPayController extends AliPayApiController {
    private static final Logger log = LoggerFactory.getLogger(AliPayController.class);

	@Autowired
	private AliPayBean aliPayBean;

	@Override
	public AliPayApiConfig getApiConfig() {
		AliPayApiConfig aliPayApiConfig = AliPayApiConfig.New()
				.setAppId(aliPayBean.getAppId())
				.setAlipayPublicKey(aliPayBean.getPublicKey())
				.setCharset("UTF-8")
				.setPrivateKey(aliPayBean.getPrivateKey())
				.setServiceUrl(aliPayBean.getServerUrl())
				.setSignType("RSA2")
				.build();
		
		return aliPayApiConfig;
	}
	
	@RequestMapping("/")
	@ResponseBody
	public String index() {
		return "欢迎使用IJPay 中的支付宝支付 -By Javen";
	}
	@RequestMapping("/test")
	@ResponseBody
	public String test(){
		String charset = AliPayApiConfigKit.getAliPayApiConfig().getCharset();
		log.info("charset>"+charset);
		return aliPayBean.toString();
	}
	
	
	@RequestMapping(value = "/wapPay") 
	@ResponseBody
	public void wapPay(HttpServletRequest request , HttpServletResponse response) {
		String body = "我是测试数据-By Javen";
		String subject = "Javen Wap支付测试";
		String totalAmount = "1";
		String passbackParams = "1";
		String returnUrl = aliPayBean.getDomain() + "/alipay/return_url";
		String notifyUrl = aliPayBean.getDomain() + "/alipay/notify_url";

		AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
		model.setBody(body);
		model.setSubject(subject);
		model.setTotalAmount(totalAmount);
		model.setPassbackParams(passbackParams);
		String outTradeNo = StringUtils.getOutTradeNo();
		System.out.println("wap outTradeNo>"+outTradeNo);
		model.setOutTradeNo(outTradeNo);
		model.setProductCode("QUICK_WAP_PAY");

		try {
			AliPayApi.wapPay(response, model, returnUrl, notifyUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * PC支付
	 */
	@RequestMapping(value = "/pcPay") 
	@ResponseBody
	public void pcPay(HttpServletRequest request , HttpServletResponse response){
		try {
			String totalAmount = "88.88"; 
			String outTradeNo =StringUtils.getOutTradeNo();
			log.info("pc outTradeNo>"+outTradeNo);
			
			String returnUrl = aliPayBean.getDomain() + "/alipay/return_url";
			String notifyUrl = aliPayBean.getDomain() + "/alipay/notify_url";
			AlipayTradePayModel model = new AlipayTradePayModel();
			
			model.setOutTradeNo(outTradeNo);
			model.setProductCode("FAST_INSTANT_TRADE_PAY");
			model.setTotalAmount(totalAmount);
			model.setSubject("Javen PC支付测试");
			model.setBody("Javen IJPay PC支付测试");
			
			AliPayApi.tradePage(response,model , notifyUrl, returnUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
