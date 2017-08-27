package com.ijpay.controller;

import com.ijpay.entity.AliPayBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


@RestController
public class HelloController {
    private static final Logger log = LoggerFactory.getLogger(TemplateController.class);


    @Value("${age}")
    private Integer age;

    @Value("${name}")
    private String name;

    @Value("${content}")
    private String content;

    @Value("${random}")
    private String random;


    @Autowired
    private AliPayBean aliPayBean;

    @RequestMapping("/")
    public String hello(){
        log.error("测试日志...");
        return "Spring Boot Hello word!";
    }

    @RequestMapping(value = {"/say","/hi"},method = RequestMethod.GET)
    public String say(){
        return "Hello Spring Boot!";
    }

    @GetMapping(value = "/random")
    public String random(){
        return random;
    }

    @GetMapping(value = "/alipay")
    public String alipay(){
        return aliPayBean.toString();
    }

    @GetMapping(value = "/getYml")
    public String getYml(){
        return "name:"+name+" age:"+age;
    }

    @GetMapping(value = "/getYml2")
    public String getYml2(){
        return content;
    }

    @RequestMapping(value = "/ss/{id}",method = RequestMethod.GET)
    public String pa(@PathVariable("id") Integer id){
        return  "id>" + id;
    }

    //获取参数
    @RequestMapping(value = "/xx",method = RequestMethod.GET)
    public String param(@RequestParam("id") Integer xx){
        return  "id>"+xx;
    }
    //设置默认值
    @RequestMapping(value = "/xxx",method = RequestMethod.GET)
    public String param2(@RequestParam(value = "id",required = false ,defaultValue = "2") Integer xx){
        return  "id>"+xx;
    }



}
