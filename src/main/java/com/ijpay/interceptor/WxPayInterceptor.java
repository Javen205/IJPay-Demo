package com.ijpay.interceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ijpay.controller.wxpay.WxPayApiController;
import com.ijpay.controller.wxpay.WxPayController;
import com.jpay.weixin.api.WxPayApiConfigKit;

public class WxPayInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object handler) throws Exception {
		if (HandlerMethod.class.equals(handler.getClass())) {
			HandlerMethod method = (HandlerMethod) handler;
			Object controller = method.getBean();
			if (controller instanceof WxPayApiController == false) {
				throw new RuntimeException("控制器需要继承 WxPayApiController");
			}
			
			try {
				WxPayApiConfigKit.setThreadLocalWxPayApiConfig(((WxPayController)controller).getApiConfig());
				return true;
			}
			finally {
			}
		}
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, Exception e) throws Exception {
	}
}
