package com.ijpay.controller.oppo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jpay.ext.kit.PaymentKit;
import com.jpay.oppo.OppoPayApi;
import com.jpay.secure.RSAUtils;
import com.jpay.vo.AjaxResult;

public class OppoController extends Controller {
	private AjaxResult ajaxResult = new AjaxResult();
	
	public void index() {
		renderText("欢迎使用IJPay 中的OPPO支付 -By Javen");
	}
	
	public void preOrder() {
		try {
			// 公钥
			// MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBnG5djzlGN1SKhZ0MC+nkqHIWLh2FVhObHB8C1A8DQHS0L/YclGejmdHQzvOc/BitUHxZicVk7WoER3yIFXd1jSewx+Ug+VkW1V7KAA6S+DjtIbP1AR640fcoQyP2wPwDldJ0nHRjmvRd6A2GQm4InoGaSRbhmUUmUQdpKF09ZwIDAQAB
			// 私钥
			String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIGcbl2POUY3VIqFnQwL6eSochYuHYVWE5scHwLUDwNAdLQv9hyUZ6OZ0dDO85z8GK1QfFmJxWTtagRHfIgVd3WNJ7DH5SD5WRbVXsoADpL4OO0hs/UBHrjR9yhDI/bA/AOV0nScdGOa9F3oDYZCbgiegZpJFuGZRSZRB2koXT1nAgMBAAECgYBLwcV4hhhyBDEz7jkHK4eAkTxt3nJ+1vsKtHsjyhMkUW5lS4tM1SlmpKfQpsYZwfgihWLJaw+nmGVZLveJCxMRGx6Na87v+bruKBmRVf92LBno47tJjx9jmgwC2YUfNtRZ7zN1FS+p9dn7FJOEm701MFsg5PFktr0pQAyyToAzMQJBAMHvDoAFUEFoct3jp+KBK+r1fX47gEK9hqi6wA/dKfRpcKxTAFlFmiukd+0mAZIia+2kOi5EVNzMsRSxOMC6Jo0CQQCrF23UeCm7YeRN09j3R68f3lC2P6exPAiR1133DKBDhRvxoOTk06muKOBqhTCRU9N0WsEFg9pMbHtnGFQf0WDDAkB0usZ4ijo3VH4nTrsjm+pqX18s1vWptlhSQS4Pnmh9Y+Xc44zHLxzP/gvtYz34zt01ye73IEwQJujDcrSdVK6VAkA6+JddnZFcyfAEN1nYWbft9xXAy44EU9nsUtxLc+mlfDLWhQuAJ8ibg/FeJcxIuyjuOP3A5eKPZvDd5w/P6747AkEAvh2MxDVrbpW2xI/3r3FJNlWj7b5AXSqrKujNPUQSwp2b5z9OnzbBU3ukLDwblkI9bDXQA/ZV2t3tr2vaS1/H6w==";
			Map<String, String> params = new HashMap<String, String>();
			params.put("appId", "123456");
			params.put("openId", "123456");
			params.put("timestamp", System.currentTimeMillis() + "");
			params.put("productName", "IJPay OPPO测试");
			params.put("productDesc", "IJPay 让支付触手可及");
			params.put("count", "1");
			params.put("price", "1");
			params.put("currency", "CNY");
			params.put("cpOrderId", System.currentTimeMillis() + "");
			params.put("appVersion", "1.0");
			params.put("engineVersion", "1.0");
			String result = OppoPayApi.preOrder(params, privateKey);
			System.out.println(result);
			JSONObject obj = JSONObject.parseObject(result);
			String code = obj.getString("code");
			String msg = obj.getString("msg");
			if(code.equals("200")) {
				JSONObject dataObj = obj.getJSONObject("data");
//				String appId = dataObj.getString("appId");
//				String cpOrderId = dataObj.getString("cpOrderId");
				String orderNo = dataObj.getString("orderNo");
				String timestamp = System.currentTimeMillis()+"";
				Map<String, String> prepayParams = new HashMap<String, String>();
				prepayParams.put("appKey", "1111");
				prepayParams.put("orderNo", orderNo);
				prepayParams.put("timestamp", timestamp);
				String paySign = RSAUtils.encryptByPrivateKey(PaymentKit.packageSign(prepayParams, false), privateKey);
				
				prepayParams.clear();
				prepayParams.put("timestamp", timestamp);
				prepayParams.put("orderNo", orderNo);
				prepayParams.put("paySign", paySign);
				ajaxResult.success(prepayParams);
				renderJson(ajaxResult);
			}else {
				ajaxResult.addError(code+">"+msg);
				renderJson(ajaxResult);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void pay_notify() {
		try {
			String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBnG5djzlGN1SKhZ0MC+nkqHIWLh2FVhObHB8C1A8DQHS0L/YclGejmdHQzvOc/BitUHxZicVk7WoER3yIFXd1jSewx+Ug+VkW1V7KAA6S+DjtIbP1AR640fcoQyP2wPwDldJ0nHRjmvRd6A2GQm4InoGaSRbhmUUmUQdpKF09ZwIDAQAB";
			Map<String, String> params = new HashMap<>();
			Map<String, String[]> map = getParaMap();
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
			    String key = iterator.next();
			    String value = map.get(key)[0];
			    params.put(key, value);
			}
			String sign = params.get("sign");
			params.remove("sign");
			String data  =PaymentKit.packageSign(params, false);
			boolean isOK = RSAUtils.checkByPublicKey(data, sign, publicKey);
			System.out.println("签名校验:"+isOK);
			if(isOK) {
				//处理业务逻辑
				renderText("result=OK&resultMsg=成功");
			}else {
				renderText("result=FAIL&resultMsg=签名校验失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderText("result=FAIL&resultMsg=系统异常");
		}
	}
}
