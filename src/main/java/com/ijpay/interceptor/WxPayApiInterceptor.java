package com.ijpay.interceptor;
import com.ijpay.controller.weixin.WxPayApiController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jpay.weixin.api.WxPayApiConfigKit;
/**
 * @Email javen205@126.com
 * @author Javen
 */
public class WxPayApiInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		if (controller instanceof WxPayApiController == false)
			throw new RuntimeException("控制器需要继承 WxPayApiController");
		
		try {
			WxPayApiConfigKit.setThreadLocalWxPayApiConfig(((WxPayApiController)controller).getApiConfig());
			inv.invoke();
		}
		finally {
		}
	}

}