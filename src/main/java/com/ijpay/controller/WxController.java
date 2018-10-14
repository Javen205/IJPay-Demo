package com.ijpay.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;

import net.dreamlu.weixin.annotation.WxMsgController;

@WxMsgController("/weixin/ijpay")
public class WxController {

   


    @RequestMapping("/toOauth")
    public void toOauth(HttpServletResponse response){
        try {
            String url=SnsAccessTokenApi.getAuthorizeURL(ApiConfigKit.getApiConfig().getAppId(),
                    "http://mac.javen.1mfy.cn/oauth",
                    "123",
                    false);
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/oauth",method = RequestMethod.GET)
    public ModelAndView oauth(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code, @RequestParam("state") String state){
        try {
            System.out.println("state:"+state+" code:"+code);
            SnsAccessToken snsAccessToken=SnsAccessTokenApi.getSnsAccessToken(ApiConfigKit.getApiConfig().getAppId(),ApiConfigKit.getApiConfig().getAppSecret(),code);
            String openId=snsAccessToken.getOpenid();
            request.getSession().setAttribute("openId", openId);
            return new ModelAndView("redirect:/towxpay");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
