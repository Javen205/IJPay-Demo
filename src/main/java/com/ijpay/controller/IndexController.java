package com.ijpay.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;

import net.dreamlu.weixin.annotation.WxMsgController;
import net.dreamlu.weixin.properties.DreamWeixinProperties;


@WxMsgController("/weixin/wx")
public class IndexController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DreamWeixinProperties weixinProperties;
    
    @RequestMapping("")
    @ResponseBody
    public String index(){
    	logger.info("欢迎使用IJPay 开发加群148540125交流 -By Javen");
    	return "欢迎使用IJPay 开发加群148540125交流 -By Javen";
    }
    @RequestMapping("/toOauth")
    public void toOauth(HttpServletResponse response){
    	try {
    		String url=SnsAccessTokenApi.getAuthorizeURL(weixinProperties.getWxaConfig().getAppId(), "http://mac.javen.1mfy.cn/weixin/wx/oauth", "123",false);	
        	response.sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @RequestMapping(value = "/oauth",method = RequestMethod.GET)
    public ModelAndView oauth(HttpServletRequest request,HttpServletResponse response,@RequestParam("code") String code,@RequestParam("state") String state){
    	try {
    			SnsAccessToken snsAccessToken=SnsAccessTokenApi.getSnsAccessToken(weixinProperties.getWxaConfig().getAppId(),weixinProperties.getWxaConfig().getAppSecret(),code);
			String openId=snsAccessToken.getOpenid();
			request.getSession().setAttribute("openId", openId);
			return new ModelAndView("redirect:/towxpay");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
    @RequestMapping("/toWxH5Pay")
    public String toWxH5Pay(){
		return "wxh5pay.html";
	}
    
    @RequestMapping("/towxpay")
    public String towxpay() {
		return "wxpay.html";
	}
    @RequestMapping("/towxsubpay")
	public String towxsubpay() {
		return "wxsubpay.html";
	}
    
    
    
    @RequestMapping(value = "/pay_input_money")
    public ModelAndView pay_input_money(){
    	 ModelAndView mav = new ModelAndView("pay_input_money.html");
         mav.addObject("content", "xxx");
         return mav;
    }
    
    @RequestMapping(value = "/pay_keyboard")
    public String pay_keyboard(){
    	return "pay_keyboard.html";
    }
    
    @RequestMapping(value = "/pay_select_money")
    public String pay_select_money(){
    	return "pay_select_money.html";
    }
    

    @RequestMapping("/success")
	public String success() {
		return "success.html";
	}
    
    
    @RequestMapping(value = "/ss/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String pa(@PathVariable("id") Integer id){
        return  "id>" + id;
    }

    //获取参数
    @RequestMapping(value = "/xx",method = RequestMethod.GET)
    @ResponseBody
    public String param(@RequestParam("id") Integer xx){
        return  "id>"+xx;
    }
    //设置默认值
    @RequestMapping(value = "/xxx",method = RequestMethod.GET)
    @ResponseBody
    public String param2(@RequestParam(value = "id",required = false ,defaultValue = "2") Integer xx){
        return  "id>"+xx;
    }



}
