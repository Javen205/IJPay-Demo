package com.ijpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ijpay.entity.BoyProperties;


@RestController
public class HelloController {
    @Value("${age}")
    private Integer age;

    @Value("${name}")
    private String name;

    @Value("${content}")
    private String content;


    @Autowired
    private BoyProperties boyProperties;

    @RequestMapping("/")
    public String hello(){
        return "Spring Boot Hello word!";
    }

    @RequestMapping(value = {"/say","/hi"},method = RequestMethod.GET)
    public String say(){
        return "Hello Spring Boot!";
    }

    @GetMapping(value = "/getYml")
    public String getYml(){
        return "name:"+name+" age:"+age;
    }

    @GetMapping(value = "/getYml2")
    public String getYml2(){
        return content;
    }

    @GetMapping(value = "/getPorperties")
    public String getProperties(){
        return boyProperties.getName();
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
