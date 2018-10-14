package com.ijpay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.jfinal.template.ext.spring.JFinalViewResolver;

@Configuration
public class SpringBootConfig {
	@Bean(value = "jfinalViewResolver")
	public JFinalViewResolver getJFinalViewResolver(){
		JFinalViewResolver jf = new JFinalViewResolver();
		jf.setDevMode(true);
		jf.setCache(false);
//		jf.setSourceFactory(new ClassPathSourceFactory());
		jf.setPrefix("/WEB-INF/_views/");
//		jf.setSuffix(".html");
		jf.setContentType("text/html;charset=UTF-8");
		jf.setOrder(0);
//		jf.setSessionInView(true);
//		jf.setRequestContextAttribute("ctx");
		return jf;
				
	}
}
