package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import com.ijpay.config.StartupRunner;
import com.ijpay.config.TaskRunner;
import com.jfinal.template.ext.spring.JFinalViewResolver;

@SpringBootApplication
@EnableCaching
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public StartupRunner startupRunner() {
		return new StartupRunner();
	}

	@Bean
	public TaskRunner taskRunner() {
		return new TaskRunner();
	}

	@Bean(value = "jfinalViewResolver")
	public JFinalViewResolver getJFinalViewResolver() {
		JFinalViewResolver jf = new JFinalViewResolver();
		jf.setDevMode(true);
		jf.setCache(false);
		// jf.setSourceFactory(new ClassPathSourceFactory());
		jf.setPrefix("/WEB-INF/_views/");
		// jf.setSuffix(".html");
		jf.setContentType("text/html;charset=UTF-8");
		jf.setOrder(0);
		return jf;
	}
}
