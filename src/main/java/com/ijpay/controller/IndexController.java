package com.ijpay.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;

public class IndexController extends Controller {

	public void index() {
		renderText("欢迎您使用IJPay -By Javen.");
	}

	// 跳转到授权页面
	public void toOauth() {
		String state = getPara("state");
		String calbackUrl = PropKit.get("domain") + "/oauth";
		System.out.println("calbackUrl>"+calbackUrl);
		String url = SnsAccessTokenApi.getAuthorizeURL(PropKit.get("appId"), calbackUrl, state, true);
		redirect(url);
	}
	
	public void qrcode() {

	}

	public void success() {

	}
	public void towxpay() {
		render("wxpay.html");
	}
	public void towxsubpay() {
		render("wxsubpay.html");
	}
}
