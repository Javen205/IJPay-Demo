package com.ijpay;

import com.ijpay.controller.IndexController;
import com.ijpay.controller.alipay.AliPayController;
import com.ijpay.controller.unionpay.UnionPayController;
import com.ijpay.controller.weixin.WxOauthController;
import com.ijpay.controller.weixin.WxPayController;
import com.ijpay.controller.weixin.WxSubPayController;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jpay.unionpay.SDKConfig;


/**
 * @author Javen
 */
public class APPConfig extends JFinalConfig {
	static Log log = Log.getLog(APPConfig.class);

	/**
	 * 如果生产环境配置文件存在，则优先加载该配置，否则加载开发环境配置文件
	 * 
	 * @param pro
	 *            生产环境配置文件
	 * @param dev
	 *            开发环境配置文件
	 */
	public void loadProp(String pro, String dev) {
		try {
			PropKit.use(pro);
		} catch (Exception e) {
			PropKit.use(dev);
		}
	}

	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		// 加载少量必要配置，随后可用PropKit.get(...)获取值
		loadProp("config_pro.properties", "config.properties");
		me.setDevMode(PropKit.getBoolean("devMode", false));
		me.setEncoding("utf-8");
		me.setError404View("/WEB-INF/error/404.html");
	    me.setError500View("/WEB-INF/error/500.html");
	    me.setViewType(ViewType.JFINAL_TEMPLATE);
	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.setBaseViewPath("/WEB-INF/_views");
		me.add("/", IndexController.class);
		me.add("/oauth", WxOauthController.class);
		me.add("/alipay", AliPayController.class);
		me.add("/wxpay", WxPayController.class);
		me.add("/wxsubpay", WxSubPayController.class);
		me.add("/unionpay", UnionPayController.class);
		
	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {

	}
	
	

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {

	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
    	me.add(new ContextPathHandler("ctxPath"));
	}
	
	@Override
	public void beforeJFinalStop() {
		log.info("beforeJFinalStop");
		super.beforeJFinalStop();
	}
	
	
	@Override
	public void afterJFinalStart() {
		log.info("afterJFinalStart");
		ApiConfigKit.putApiConfig(getApiConfig());
		//银联加载配置
		SDKConfig.getConfig().loadPropertiesFromSrc();// 从classpath加载acp_sdk.properties文件
	}

	public ApiConfig getApiConfig() {
		ApiConfig ac = new ApiConfig();
		
		// 配置微信 API 相关常量
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("appSecret"));
		
		/**
		 *  是否对消息进行加密，对应于微信平台的消息加解密方式：
		 *  1：true进行加密且必须配置 encodingAesKey
		 *  2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));
		return ac;
	}
	/**
	 * 建议使用 JFinal 手册推荐的方式启动项目 运行此 main
	 * 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
//		JFinal.start("src/main/webapp", 8080, "/", 5);// 启动配置项
		/**
		 * 特别注意：
		 * 1、IDEA 推荐使用Plugins中的jetty插件启动
		 * 2、如果想直接运行APPConfig需要做如下配置
		 * IDEA 下直接启动需要去掉最后一个参数并将pom.xml中jetty-server的scope去掉
		 */
		 JFinal.start("src/main/webapp", 8080, "/");
	}

	@Override
	public void configEngine(Engine arg0) {
		
	}

}