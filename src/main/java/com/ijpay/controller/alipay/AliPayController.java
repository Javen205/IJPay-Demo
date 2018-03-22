package com.ijpay.controller.alipay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayCommerceCityfacilitatorVoucherGenerateModel;
import com.alipay.api.domain.AlipayDataDataserviceBillDownloadurlQueryModel;
import com.alipay.api.domain.AlipayFundAuthOrderFreezeModel;
import com.alipay.api.domain.AlipayFundCouponOrderAgreementPayModel;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.domain.AlipayOpenAuthTokenAppModel;
import com.alipay.api.domain.AlipayOpenAuthTokenAppQueryModel;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeCancelModel;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeCreateModel;
import com.alipay.api.domain.AlipayTradeOrderSettleModel;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayFundAuthOrderFreezeResponse;
import com.alipay.api.response.AlipayFundCouponOrderAgreementPayResponse;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jpay.alipay.AliPayApi;
import com.jpay.alipay.AliPayApiConfig;
import com.jpay.util.StringUtils;
import com.jpay.vo.AjaxResult;
/**
 * @Email javen205@126.com
 * @author Javen
 */
public class AliPayController extends AliPayApiController {
	private Log log = Log.getLog(AliPayController.class);
	
	private  final Prop prop = PropKit.use("alipay.properties");
	private  String charset = "UTF-8";
	private  String private_key = prop.get("privateKey");
	private  String alipay_public_key = prop.get("publicKey");
	private  String service_url = prop.get("serverUrl");
	private  String app_id = prop.get("appId");
	private  String sign_type = "RSA2";
	private  String notify_domain = prop.get("domain");
	
	private AjaxResult result = new AjaxResult();

	@Override
	public AliPayApiConfig getApiConfig() {
		AliPayApiConfig aliPayApiConfig = AliPayApiConfig.New()
		.setAppId(app_id)
		.setAlipayPublicKey(alipay_public_key)
		.setCharset(charset)
		.setPrivateKey(private_key)
		.setServiceUrl(service_url)
		.setSignType(sign_type)
		.build();
		return aliPayApiConfig;
	}
	
	public void index() {
		renderText("欢迎使用IJPay 中的支付宝支付 -By Javen");
	}

	/**
	 * app支付
	 */
	public void appPay() {
		try {
			AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
			model.setBody("我是测试数据-By Javen");
			model.setSubject("App支付测试-By Javen");
			model.setOutTradeNo(StringUtils.getOutTradeNo());
			model.setTimeoutExpress("30m");
			model.setTotalAmount("0.01");
			model.setPassbackParams("callback params");
			model.setProductCode("QUICK_MSECURITY_PAY");
			String orderInfo = AliPayApi.startAppPay(model, notify_domain + "/alipay/app_pay_notify");
			result.success(orderInfo);
			renderJson(result);

		} catch (AlipayApiException e) {
			e.printStackTrace();
			result.addError("system error");
		}
	}

	/**
	 * Wap支付
	 */
	public void wapPay() {
		String body = "我是测试数据-By Javen";
		String subject = "Javen Wap支付测试";
		String totalAmount = getPara("totalAmount");
		String passbackParams = "1";
		String returnUrl = notify_domain + "/alipay/return_url";
		String notifyUrl = notify_domain + "/alipay/notify_url";

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
			AliPayApi.wapPay(getResponse(), model, returnUrl, notifyUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderNull();
	}

	
	/**
	 * PC支付
	 */
	public void pcPay(){
		try {
			String totalAmount = "88.88"; 
			String outTradeNo =StringUtils.getOutTradeNo();
			log.info("pc outTradeNo>"+outTradeNo);
			
			String returnUrl = notify_domain + "/alipay/return_url";
			String notifyUrl = notify_domain + "/alipay/notify_url";
			AlipayTradePayModel model = new AlipayTradePayModel();
			
			model.setOutTradeNo(outTradeNo);
			model.setProductCode("FAST_INSTANT_TRADE_PAY");
			model.setTotalAmount(totalAmount);
			model.setSubject("Javen PC支付测试");
			model.setBody("Javen IJPay PC支付测试");
			//花呗分期相关的设置
			/**
			 * 测试环境不支持花呗分期的测试
			 * hb_fq_num代表花呗分期数，仅支持传入3、6、12，其他期数暂不支持，传入会报错；
			 * hb_fq_seller_percent代表卖家承担收费比例，商家承担手续费传入100，用户承担手续费传入0，仅支持传入100、0两种，其他比例暂不支持，传入会报错。
			 */
//			ExtendParams extendParams = new ExtendParams();
//			extendParams.setHbFqNum("3");
//			extendParams.setHbFqSellerPercent("0");
//			model.setExtendParams(extendParams);
			
			AliPayApi.tradePage(getResponse(),model , notifyUrl, returnUrl);
			renderNull();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	

	/**
	 * 条形码支付
	 * https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.Yhpibd&
	 * treeId=194&articleId=105170&docType=1#s4
	 */
	public void tradePay() {
		String authCode = getPara("auth_code");
		String subject = "Javen 支付宝条形码支付测试";
		String totalAmount = "100";
		String notifyUrl = notify_domain + "/alipay/notify_url";

		AlipayTradePayModel model = new AlipayTradePayModel();
		model.setAuthCode(authCode);
		model.setSubject(subject);
		model.setTotalAmount(totalAmount);
		model.setOutTradeNo(StringUtils.getOutTradeNo());
		model.setScene("bar_code");
		try {
			String resultStr = AliPayApi.tradePay(model,notifyUrl);
			renderText(resultStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 声波支付
	 * https://doc.open.alipay.com/docs/doc.htm?treeId=194&articleId=105072&docType=1#s2
	 */
	public void tradeWavePay() {
		String authCode = getPara("auth_code");
		String subject = "Javen 支付宝声波支付测试";
		String totalAmount = "100";
		String notifyUrl = notify_domain + "/alipay/notify_url";

		AlipayTradePayModel model = new AlipayTradePayModel();
		model.setAuthCode(authCode);
		model.setSubject(subject);
		model.setTotalAmount(totalAmount);
		model.setOutTradeNo(StringUtils.getOutTradeNo());
		model.setScene("wave_code");
		try {
			String resultStr = AliPayApi.tradePay(model,notifyUrl);
			renderText(resultStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 扫码支付
	 */
	public void tradePrecreatePay() {
		String subject = "Javen 支付宝扫码支付测试";
		String totalAmount = "86";
		String storeId = "123";
		String notifyUrl = notify_domain + "/alipay/precreate_notify_url";

		AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
		model.setSubject(subject);
		model.setTotalAmount(totalAmount);
		model.setStoreId(storeId);
		model.setTimeoutExpress("5m");
		model.setOutTradeNo(StringUtils.getOutTradeNo());
		try {
			String resultStr = AliPayApi.tradePrecreatePay(model, notifyUrl);
			JSONObject jsonObject = JSONObject.parseObject(resultStr);
			String qr_code = jsonObject.getJSONObject("alipay_trade_precreate_response").getString("qr_code");
			renderText(qr_code);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 单笔转账到支付宝账户
	 * https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.54Ty29&
	 * treeId=193&articleId=106236&docType=1
	 */
	public void transfer() {
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
		renderJson(isSuccess);
	}
	
	/**
	 * 资金授权冻结接口
	 */
	public void authOrderFreeze(){
		try {
			String authCode = getPara("auth_code");
			AlipayFundAuthOrderFreezeModel model = new AlipayFundAuthOrderFreezeModel();
			model.setOutOrderNo(StringUtils.getOutTradeNo());
			model.setOutRequestNo(StringUtils.getOutTradeNo());
			model.setAuthCode(authCode);
			model.setAuthCodeType("bar_code");
			model.setOrderTitle("资金授权冻结-By IJPay");
			model.setAmount("36");
//			model.setPayTimeout("");
			model.setProductCode("PRE_AUTH");
			
			AlipayFundAuthOrderFreezeResponse response = AliPayApi.authOrderFreezeToResponse(model);
			renderJson(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 红包协议支付接口
	 * https://docs.open.alipay.com/301/106168/
	 */
	public void agreementPay(){
		try {
			AlipayFundCouponOrderAgreementPayModel model = new AlipayFundCouponOrderAgreementPayModel();
			model.setOutOrderNo(StringUtils.getOutTradeNo());
			model.setOutRequestNo(StringUtils.getOutTradeNo());
			model.setOrderTitle("红包协议支付接口-By IJPay");
			model.setAmount("36");
			model.setPayerUserId("2088102180432465");
			
			
			AlipayFundCouponOrderAgreementPayResponse response = AliPayApi.fundCouponOrderAgreementPayToResponse(model);
			renderJson(response);
		} catch (Exception e) {
			e.printStackTrace();
			renderText("有异常哦!!!");
		}
	}

	/**
	 * 下载对账单
	 */
	public void dataDataserviceBill() {
		String para = getPara("billDate");
		try {
			AlipayDataDataserviceBillDownloadurlQueryModel model = new AlipayDataDataserviceBillDownloadurlQueryModel();
			model.setBillType("trade");
			model.setBillDate(para);
			String resultStr = AliPayApi.billDownloadurlQuery(model);
			renderText(resultStr);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 退款
	 */
	public void tradeRefund() {

		try {
			AlipayTradeRefundModel model = new AlipayTradeRefundModel();
//			model.setOutTradeNo("042517111114931");
//			model.setTradeNo("2017042521001004200200236813");
			model.setOutTradeNo("081014283315023");
			model.setTradeNo("2017081021001004200200273870");
			model.setRefundAmount("86.00");
			model.setRefundReason("正常退款");
			String resultStr = AliPayApi.tradeRefund(model);
			renderText(resultStr);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}

	

	/**
	 * 交易查询
	 */
	public void tradeQuery() {
		try {
			AlipayTradeQueryModel model = new AlipayTradeQueryModel();
			model.setOutTradeNo("081014283315023");
			model.setTradeNo("2017081021001004200200273870");

			boolean isSuccess = AliPayApi.isTradeQuery(model);
			renderJson(isSuccess);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}

	public void tradeQueryByStr() {
		String out_trade_no = getPara("out_trade_no");
		// String trade_no = getPara("trade_no");

		AlipayTradeQueryModel model = new AlipayTradeQueryModel();
		model.setOutTradeNo(out_trade_no);

		try {
			String resultStr = AliPayApi.tradeQueryToResponse(model).getBody();
			renderText(resultStr);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建订单
	 * {"alipay_trade_create_response":{"code":"10000","msg":"Success","out_trade_no":"081014283315033","trade_no":"2017081021001004200200274066"},"sign":"ZagfFZntf0loojZzdrBNnHhenhyRrsXwHLBNt1Z/dBbx7cF1o7SZQrzNjRHHmVypHKuCmYifikZIqbNNrFJauSuhT4MQkBJE+YGPDtHqDf4Ajdsv3JEyAM3TR/Xm5gUOpzCY7w+RZzkHevsTd4cjKeGM54GBh0hQH/gSyhs4pEN3lRWopqcKkrkOGZPcmunkbrUAF7+AhKGUpK+AqDw4xmKFuVChDKaRdnhM6/yVsezJFXzlQeVgFjbfiWqULxBXq1gqicntyUxvRygKA+5zDTqE5Jj3XRDjVFIDBeOBAnM+u03fUP489wV5V5apyI449RWeybLg08Wo+jUmeOuXOA=="}
	 */
	public void tradeCreate(){
		String outTradeNo = getPara("out_trade_no");
		
		String notifyUrl = notify_domain+ "/alipay/notify_url";
		
		AlipayTradeCreateModel model = new AlipayTradeCreateModel();
		model.setOutTradeNo(outTradeNo);
		model.setTotalAmount("88.88");
		model.setBody("Body");
		model.setSubject("Javen 测试统一收单交易创建接口");
		model.setBuyerLogonId("abpkvd0206@sandbox.com");//买家支付宝账号，和buyer_id不能同时为空
		try {
			
			AlipayTradeCreateResponse response = AliPayApi.tradeCreateToResponse(model,notifyUrl);
			renderJson(response.getBody());
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 撤销订单
	 */
	public void tradeCancel() {
		try {
			AlipayTradeCancelModel model = new AlipayTradeCancelModel();
			model.setOutTradeNo("081014283315033");
			model.setTradeNo("2017081021001004200200274066");

			boolean isSuccess = AliPayApi.isTradeCancel(model);
			renderJson(isSuccess);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭订单
	 */
	public void tradeClose(){
		String outTradeNo = getPara("out_trade_no");
		String tradeNo = getPara("trade_no");
		try {
			AlipayTradeCloseModel model = new AlipayTradeCloseModel();
			model.setOutTradeNo(outTradeNo);
			
			model.setTradeNo(tradeNo);
			
			String resultStr = AliPayApi.tradeCloseToResponse(model).getBody();
			renderText(resultStr);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 结算
	 */
	public void tradeOrderSettle(){
		String tradeNo = getPara("trade_no");//支付宝订单号
		try {
			AlipayTradeOrderSettleModel model = new AlipayTradeOrderSettleModel();
			model.setOutRequestNo(StringUtils.getOutTradeNo());
			model.setTradeNo(tradeNo);
			
			String resultStr = AliPayApi.tradeOrderSettleToResponse(model ).getBody();
			renderText(resultStr);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取应用授权URL并授权
	 */
	public void toOauth() {
		try {
			String redirectUri = notify_domain+ "/alipay/redirect_uri";
			System.out.println(app_id);
			String oauth2Url = AliPayApi.getOauth2Url(app_id, redirectUri);
			redirect(oauth2Url);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 应用授权回调
	 */
	public void redirect_uri() {
		try {
			String app_id = getPara("app_id");
			String app_auth_code = getPara("app_auth_code");
			System.out.println("app_id:"+app_id);
			System.out.println("app_auth_code:"+app_auth_code);
			//使用app_auth_code换取app_auth_token
			AlipayOpenAuthTokenAppModel model = new AlipayOpenAuthTokenAppModel();
			model.setGrantType("authorization_code");
			model.setCode(app_auth_code);
			String result = AliPayApi.openAuthTokenApp(model);
			renderText(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 查询授权信息
	 */
	public void openAuthTokenAppQuery() {
		try {
			String app_auth_token = getPara("app_auth_token");
			AlipayOpenAuthTokenAppQueryModel model = new AlipayOpenAuthTokenAppQueryModel();
			model.setAppAuthToken(app_auth_token);
			String result = AliPayApi.openAuthTokenAppQuery(model);
			renderText(result);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 批量付款到支付宝账户有密接口
	 */
	public  void batchTrans() {
		try {
			String sign_type = "MD5";
			String notifyUrl = notify_domain+ "/alipay/notify_url";;
			Map<String, String> params = new HashMap<>();
			params.put("partner", "PID");
			params.put("sign_type", sign_type);
			params.put("notify_url", notifyUrl);
			params.put("account_name", "xxx");
			params.put("detail_data", "流水号1^收款方账号1^收款账号姓名1^付款金额1^备注说明1|流水号2^收款方账号2^收款账号姓名2^付款金额2^备注说明2");
			params.put("batch_no",String.valueOf(System.currentTimeMillis()));
			params.put("batch_num", 1+"");
			params.put("batch_fee", 10.00+"");
			params.put("email", "xx@xxx.com");
			
			AliPayApi.batchTrans(params, private_key, sign_type, getResponse());
		} catch (IOException e) {
			e.printStackTrace();
		}
		renderNull();
	}
	/**
	 * 地铁购票核销码发码
	 */
	public void voucherGenerate() {
		try {
			//需要支付成功的订单号
			String tradeNo = getPara("tradeNo");
			
			AlipayCommerceCityfacilitatorVoucherGenerateModel model = new AlipayCommerceCityfacilitatorVoucherGenerateModel();
			model.setCityCode("440300");
			model.setTradeNo(tradeNo);
			model.setTotalFee("8");
			model.setTicketNum("2");
			model.setTicketType("oneway");
			model.setSiteBegin("001");
			model.setSiteEnd("002");
			model.setTicketPrice("4");
			String result = AliPayApi.voucherGenerate(model);
			renderText(result);
		} catch (AlipayApiException e) {
			e.printStackTrace();
			renderText(e.getMessage());
		}
		
	}
	
	public void return_url() {
		try {
			// 获取支付宝GET过来反馈信息
			Map<String, String> map = AliPayApi.toMap(getRequest());
			for (Map.Entry<String, String> entry : map.entrySet()) {
				System.out.println(entry.getKey() + " = " + entry.getValue());
			}

			boolean verify_result = AlipaySignature.rsaCheckV1(map, alipay_public_key, charset,
					sign_type);

			if (verify_result) {// 验证成功
				// TODO 请在这里加上商户的业务逻辑程序代码
				System.out.println("return_url 验证成功");
				renderText("success");
				return;
			} else {
				System.out.println("return_url 验证失败");
				// TODO
				renderText("failure");
				return;
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			renderText("failure");
		}
	}

	public void notify_url() {
		try {
			// 获取支付宝POST过来反馈信息
			Map<String, String> params = AliPayApi.toMap(getRequest());

			for (Map.Entry<String, String> entry : params.entrySet()) {
				System.out.println(entry.getKey() + " = " + entry.getValue());
			}

			boolean verify_result = AlipaySignature.rsaCheckV1(params, alipay_public_key, charset,
					sign_type);

			if (verify_result) {// 验证成功
				// TODO 请在这里加上商户的业务逻辑程序代码 异步通知可能出现订单重复通知 需要做去重处理
				System.out.println("notify_url 验证成功succcess");
				renderText("success");
				return;
			} else {
				System.out.println("notify_url 验证失败");
				// TODO
				renderText("failure");
				return;
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			renderText("failure");
		}
	}
	//=======其实异步通知实现的方法都一样  但是通知中无法区分支付的方式(没有提供支付方式的参数)======================================================================
	/**
	 * App支付支付回调通知
	 * https://doc.open.alipay.com/docs/doc.htm?treeId=54&articleId=106370&
	 * docType=1#s3
	 */
	public void app_pay_notify() {
		try {
			// 获取支付宝POST过来反馈信息
			Map<String, String> params = AliPayApi.toMap(getRequest());
			for (Map.Entry<String, String> entry : params.entrySet()) {
				System.out.println(entry.getKey() + " = " + entry.getValue());
			}
			// 切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
			// boolean AlipaySignature.rsaCheckV1(Map<String, String> params,
			// String publicKey, String charset, String sign_type)
			boolean flag = AlipaySignature.rsaCheckV1(params, alipay_public_key, charset,
					sign_type);
			if (flag) {
				// TODO
				System.out.println("success");
				renderText("success");
				return;
			} else {
				// TODO
				System.out.println("failure");
				renderText("failure");
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			renderText("failure");
		}
	}
	/**
	 * 扫码支付通知
	 */
	public void precreate_notify_url(){
		try {
			Map<String, String> map = AliPayApi.toMap(getRequest());
			for (Map.Entry<String, String> entry : map.entrySet()) {
				System.out.println(entry.getKey()+" = "+entry.getValue());
			}
			boolean flag = AlipaySignature.rsaCheckV1(map, alipay_public_key, charset,
					sign_type);
			if (flag) {
				// TODO
				System.out.println("precreate_notify_url success");
				renderText("success");
				return;
			} else {
				// TODO
				System.out.println("precreate_notify_url failure");
				renderText("failure");
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			renderText("failure");
		}
	}

	
}