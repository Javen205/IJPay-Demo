package com.ijpay.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.wechat.utils.JsonUtils;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;


@Controller
public class IndexController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private WxMpService wxService;
    
    @RequestMapping("")
    @ResponseBody
    public String index(){
    	logger.info("欢迎使用IJPay 开发加群148540125交流 -By Javen");
    	return "欢迎使用IJPay 开发加群148540125交流 -By Javen";
    }
    @RequestMapping("/toOauth")
    public void toOauth(HttpServletResponse response){
    	try {
        	String url = wxService.oauth2buildAuthorizationUrl("http://qy.javen.1mfy.cn/oauth", WxConsts.OAUTH2_SCOPE_USER_INFO, "123");
			response.sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @RequestMapping(value = "/oauth",method = RequestMethod.GET)
    public ModelAndView oauth(HttpServletRequest request,HttpServletResponse response,@RequestParam("code") String code,@RequestParam("state") String state){
    	try {
			WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxService.oauth2getAccessToken(code);
			WxMpUser wxMpUser = wxService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
			logger.info("授权获取到的用户信息："+JsonUtils.toJson(wxMpUser));
			String openId = wxMpUser.getOpenId();
			request.getSession().setAttribute("openId", openId);
			return new ModelAndView("redirect:/towxpay");
		} catch (WxErrorException e) {
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
