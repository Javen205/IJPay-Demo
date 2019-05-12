package com.ijpay.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ErrorPageConfig implements ErrorPageRegistrar{

	@Override
	public void registerErrorPages(ErrorPageRegistry registry) {
		ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/static/error/401.html");
		ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/static/error/404.html");
		ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/static/error/500.html");
		registry.addErrorPages(error401Page,error404Page,error500Page);
	}

}
