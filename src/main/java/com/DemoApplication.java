package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.ijpay.config.StartupRunner;
import com.ijpay.config.TaskRunner;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
//		SpringApplication springApplication = new SpringApplication(DemoApplication.class);
//		//禁止命令行设置参数
//		springApplication.setAddCommandLineProperties(false);
//		springApplication.run(args);
	}
	
	 @Bean
     public StartupRunner startupRunner(){
         return new StartupRunner();
     }

     @Bean
     public TaskRunner taskRunner(){
         return new TaskRunner();
     }
}
