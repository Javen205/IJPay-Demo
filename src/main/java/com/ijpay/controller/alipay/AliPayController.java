package com.ijpay.controller.alipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.ijpay.entity.AliPayBean;
import com.jpay.alipay.AliPayApi;
import com.jpay.alipay.AliPayApiConfig;
import com.jpay.alipay.AliPayApiConfigKit;
import com.jpay.util.StringUtils;
import com.jpay.vo.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Controller
@RequestMapping("/alipay")
public class AliPayController extends AliPayApiController {
    private static final Logger log = LoggerFactory.getLogger(AliPayController.class);

	@Autowired
	private AliPayBean aliPayBean;

	private AjaxResult result = new AjaxResult();

	@Override
	public AliPayApiConfig getApiConfig() {
		return AliPayApiConfig.New()
				.setAppId(aliPayBean.getAppId())
				.setAlipayPublicKey(aliPayBean.getPublicKey())
				.setCharset("UTF-8")
				.setPrivateKey(aliPayBean.getPrivateKey())
				.setServiceUrl(aliPayBean.getServerUrl())
				.setSignType("RSA2")
				.build();
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


	/**
	 * app支付
	 */
	@RequestMapping(value = "/appPay")
	@ResponseBody
	public AjaxResult appPay() {
		try {
			AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
			model.setBody("我是测试数据-By Javen");
			model.setSubject("App支付测试-By Javen");
			model.setOutTradeNo(StringUtils.getOutTradeNo());
			model.setTimeoutExpress("30m");
			model.setTotalAmount("0.01");
			model.setPassbackParams("callback params");
			model.setProductCode("QUICK_MSECURITY_PAY");
			String orderInfo = AliPayApi.startAppPayStr(model, aliPayBean.getDomain() + "/alipay/notify_url");
			result.success(orderInfo);
		} catch (AlipayApiException e) {
			e.printStackTrace();
			result.addError("system error:"+e.getMessage());
		}
		return result;
	}
	
	
	@RequestMapping(value = "/wapPay") 
	@ResponseBody
	public void wapPay(HttpServletResponse response) {
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
	public void pcPay(HttpServletResponse response){
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


	/**
	 * 条形码支付
	 * https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.Yhpibd&
	 * treeId=194&articleId=105170&docType=1#s4
	 */
	@RequestMapping(value ="/tradePay")
	@ResponseBody
	public String  tradePay(@RequestParam("auth_code") String authCode) {
		String subject = "Javen 支付宝条形码支付测试";
		String totalAmount = "100";
		String notifyUrl = aliPayBean.getDomain() + "/alipay/notify_url";

		AlipayTradePayModel model = new AlipayTradePayModel();
		model.setAuthCode(authCode);
		model.setSubject(subject);
		model.setTotalAmount(totalAmount);
		model.setOutTradeNo(StringUtils.getOutTradeNo());
		model.setScene("bar_code");
		try {
			return AliPayApi.tradePay(model,notifyUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 声波支付
	 * https://doc.open.alipay.com/docs/doc.htm?treeId=194&articleId=105072&docType=1#s2
	 */
	@RequestMapping(value ="/tradeWavePay")
	@ResponseBody
	public String tradeWavePay(@RequestParam("auth_code") String authCode) {
		String subject = "Javen 支付宝声波支付测试";
		String totalAmount = "100";
		String notifyUrl = aliPayBean.getDomain() + "/alipay/notify_url";

		AlipayTradePayModel model = new AlipayTradePayModel();
		model.setAuthCode(authCode);
		model.setSubject(subject);
		model.setTotalAmount(totalAmount);
		model.setOutTradeNo(StringUtils.getOutTradeNo());
		model.setScene("wave_code");
		try {
			return AliPayApi.tradePay(model,notifyUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  null;
	}

	/**
	 * 扫码支付
	 */
	@RequestMapping(value ="/tradePrecreatePay")
	@ResponseBody
	public String tradePrecreatePay() {
		String subject = "Javen 支付宝扫码支付测试";
		String totalAmount = "86";
		String storeId = "123";
		String notifyUrl = aliPayBean.getDomain() + "/alipay/notify_url";

		AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
		model.setSubject(subject);
		model.setTotalAmount(totalAmount);
		model.setStoreId(storeId);
		model.setTimeoutExpress("5m");
		model.setOutTradeNo(StringUtils.getOutTradeNo());
		try {
			String resultStr = AliPayApi.tradePrecreatePay(model, notifyUrl);
			JSONObject jsonObject = JSONObject.parseObject(resultStr);
			return jsonObject.getJSONObject("alipay_trade_precreate_response").getString("qr_code");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 单笔转账到支付宝账户
	 * https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.54Ty29&
	 * treeId=193&articleId=106236&docType=1
	 */
	@RequestMapping(value = "/transfer")
	@ResponseBody
	public boolean transfer() {
		boolean isSuccess = false;
		String total_amount = "66";
		AlipayFundTransToaccountTransferModel model = new AlipayFundTransToaccountTransferModel();
		model.setOutBizNo(StringUtils.getOutTradeNo());
		model.setPayeeType("ALIPAY_LOGONID");
		model.setPayeeAccount("abpkvd0206@sandbox.com");
		model.setAmount(total_amount);
		model.setPayerShowName("测试退款");
		model.setPayerRealName("沙箱环境");
		model.setRemark("javen测试单笔转账到支付宝");

		try {
			isSuccess = AliPayApi.transfer(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSuccess;
	}

	/**
	 * 下载对账单
	 */
	@RequestMapping(value = "/dataDataserviceBill")
	@ResponseBody
	public String dataDataserviceBill(@RequestParam("billDate") String billDate) {
		try {
			AlipayDataDataserviceBillDownloadurlQueryModel model = new AlipayDataDataserviceBillDownloadurlQueryModel();
			model.setBillType("trade");
			model.setBillDate(billDate);
			return AliPayApi.billDownloadurlQuery(model);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 退款
	 */
	@RequestMapping(value = "/tradeRefund")
	@ResponseBody
	public String tradeRefund() {

		try {
			AlipayTradeRefundModel model = new AlipayTradeRefundModel();
//			model.setOutTradeNo("042517111114931");
//			model.setTradeNo("2017042521001004200200236813");
			model.setOutTradeNo("081014283315023");
			model.setTradeNo("2017081021001004200200273870");
			model.setRefundAmount("86.00");
			model.setRefundReason("正常退款");
			return AliPayApi.tradeRefund(model);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 交易查询
	 */
	@RequestMapping(value = "/tradeQuery")
	@ResponseBody
	public boolean tradeQuery() {
		boolean isSuccess = false;
		try {
			AlipayTradeQueryModel model = new AlipayTradeQueryModel();
			model.setOutTradeNo("081014283315023");
			model.setTradeNo("2017081021001004200200273870");

			isSuccess = AliPayApi.isTradeQuery(model);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return isSuccess;
	}
	@RequestMapping(value = "/tradeQueryByStr")
	@ResponseBody
	public String  tradeQueryByStr(@RequestParam("out_trade_no") String out_trade_no) {
		AlipayTradeQueryModel model = new AlipayTradeQueryModel();
		model.setOutTradeNo(out_trade_no);

		try {
			return AliPayApi.tradeQuery(model).getBody();
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 创建订单
	 * {"alipay_trade_create_response":{"code":"10000","msg":"Success","out_trade_no":"081014283315033","trade_no":"2017081021001004200200274066"},"sign":"ZagfFZntf0loojZzdrBNnHhenhyRrsXwHLBNt1Z/dBbx7cF1o7SZQrzNjRHHmVypHKuCmYifikZIqbNNrFJauSuhT4MQkBJE+YGPDtHqDf4Ajdsv3JEyAM3TR/Xm5gUOpzCY7w+RZzkHevsTd4cjKeGM54GBh0hQH/gSyhs4pEN3lRWopqcKkrkOGZPcmunkbrUAF7+AhKGUpK+AqDw4xmKFuVChDKaRdnhM6/yVsezJFXzlQeVgFjbfiWqULxBXq1gqicntyUxvRygKA+5zDTqE5Jj3XRDjVFIDBeOBAnM+u03fUP489wV5V5apyI449RWeybLg08Wo+jUmeOuXOA=="}
	 */
	@RequestMapping(value = "/tradeCreate")
	@ResponseBody
	public String tradeCreate(@RequestParam("out_trade_no") String outTradeNo){

		String notifyUrl = aliPayBean.getDomain()+ "/alipay/notify_url";

		AlipayTradeCreateModel model = new AlipayTradeCreateModel();
		model.setOutTradeNo(outTradeNo);
		model.setTotalAmount("88.88");
		model.setBody("Body");
		model.setSubject("Javen 测试统一收单交易创建接口");
		model.setBuyerLogonId("abpkvd0206@sandbox.com");//买家支付宝账号，和buyer_id不能同时为空
		try {

			AlipayTradeCreateResponse response = AliPayApi.tradeCreate(model,notifyUrl);
			return response.getBody();
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 撤销订单
	 */
	@RequestMapping(value = "/tradeCancel")
	@ResponseBody
	public boolean tradeCancel() {
		boolean isSuccess = false;
		try {
			AlipayTradeCancelModel model = new AlipayTradeCancelModel();
			model.setOutTradeNo("081014283315033");
			model.setTradeNo("2017081021001004200200274066");

			isSuccess = AliPayApi.isTradeCancel(model);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return isSuccess;
	}

	/**
	 * 关闭订单
	 */
	@RequestMapping(value = "/tradeClose")
	@ResponseBody
	public String tradeClose(@RequestParam("out_trade_no") String outTradeNo,@RequestParam("trade_no") String tradeNo){
		try {
			AlipayTradeCloseModel model = new AlipayTradeCloseModel();
			model.setOutTradeNo(outTradeNo);

			model.setTradeNo(tradeNo);

			return AliPayApi.tradeClose(model).getBody();
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 结算
	 */
	@RequestMapping(value = "/tradeOrderSettle")
	@ResponseBody
	public String  tradeOrderSettle(@RequestParam("trade_no") String tradeNo){
		try {
			AlipayTradeOrderSettleModel model = new AlipayTradeOrderSettleModel();
			model.setOutRequestNo(StringUtils.getOutTradeNo());
			model.setTradeNo(tradeNo);

			return AliPayApi.tradeOrderSettle(model ).getBody();
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/return_url")
	@ResponseBody
	public String return_url(HttpServletRequest request) {
		try {
			// 获取支付宝GET过来反馈信息
			Map<String, String> map = AliPayApi.toMap(request);
			for (Map.Entry<String, String> entry : map.entrySet()) {
				System.out.println(entry.getKey() + " = " + entry.getValue());
			}

			boolean verify_result = AlipaySignature.rsaCheckV1(map, aliPayBean.getPublicKey(), "UTF-8",
					"RSA2");

			if (verify_result) {// 验证成功
				// TODO 请在这里加上商户的业务逻辑程序代码
				System.out.println("return_url 验证成功");

				return "success";
			} else {
				System.out.println("return_url 验证失败");
				// TODO
				return "failure";
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			return "failure";
		}
	}



	@RequestMapping(value = "/notify_url")
	@ResponseBody
	public String  notify_url(HttpServletRequest request) {
		try {
			// 获取支付宝POST过来反馈信息
			Map<String, String> params = AliPayApi.toMap(request);

			for (Map.Entry<String, String> entry : params.entrySet()) {
				System.out.println(entry.getKey() + " = " + entry.getValue());
			}

			boolean verify_result = AlipaySignature.rsaCheckV1(params, aliPayBean.getPublicKey(), "UTF-8",
					"RSA2");

			if (verify_result) {// 验证成功
				// TODO 请在这里加上商户的业务逻辑程序代码 异步通知可能出现订单重复通知 需要做去重处理
				System.out.println("notify_url 验证成功succcess");
				return "success";
			} else {
				System.out.println("notify_url 验证失败");
				// TODO
				return "failure";
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			return "failure";
		}
	}
}
