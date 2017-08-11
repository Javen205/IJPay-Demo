package com.ijpay.controller.weixin;
import com.ijpay.interceptor.WxPayApiInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jpay.weixin.api.WxPayApiConfig;
/**
 * @Email javen205@126.com
 * @author Javen
 */
@Before(WxPayApiInterceptor.class)
public abstract class WxPayApiController extends Controller {
	public abstract WxPayApiConfig getApiConfig();
}