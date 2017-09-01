package com.ijpay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
//		SpringApplication.run(DemoApplication.class, args);
		SpringApplication springApplication = new SpringApplication(DemoApplication.class);
		//禁止命令行设置参数
		springApplication.setAddCommandLineProperties(false);
		springApplication.run(args);
	}
}
