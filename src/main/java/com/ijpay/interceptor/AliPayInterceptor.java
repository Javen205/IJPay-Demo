package com.ijpay.interceptor;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ijpay.controller.alipay.AliPayApiController;
import com.jpay.alipay.AliPayApiConfigKit;

public class AliPayInterceptor implements HandlerInterceptor {

	private static final Logger log = LoggerFactory.getLogger(AliPayInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object handler) throws Exception {
		log.info("preHandle 现在时间：" + new Date());
		if (HandlerMethod.class.equals(handler.getClass())) {
			HandlerMethod method = (HandlerMethod) handler;
			Object controller = method.getBean();
			if (controller instanceof AliPayApiController == false) {
				throw new RuntimeException("控制器需要继承 AliPayApiController");
			}
			
			try {
				AliPayApiConfigKit.setThreadLocalAliPayApiConfig(((AliPayApiController)controller).getApiConfig());
				log.info("xxxx true");
				return true;
			}
			finally {
				log.info("xxxx finally");
			}
		}
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
			ModelAndView modelAndView) throws Exception {
		log.info("postHandle 现在时间：" + new Date());
	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, Exception e) throws Exception {
		log.info("afterCompletion 现在时间：" + new Date());
	}
}
