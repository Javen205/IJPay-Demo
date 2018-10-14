package com.ijpay.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class IndexController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @RequestMapping("")
    @ResponseBody
    public String index(){
	    	logger.info("欢迎使用IJPay -By Javen <br/><br>  交流群：148540125");
	    	return "欢迎使用IJPay -By Javen <br/><br>  交流群：148540125";
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
