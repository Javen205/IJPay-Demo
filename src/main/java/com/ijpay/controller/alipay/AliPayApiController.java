package com.ijpay.controller.alipay;

import com.ijpay.interceptor.AliPayApiInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jpay.alipay.AliPayApiConfig;

@Before(AliPayApiInterceptor.class)
public abstract class AliPayApiController extends Controller {
	public abstract AliPayApiConfig getApiConfig();
}