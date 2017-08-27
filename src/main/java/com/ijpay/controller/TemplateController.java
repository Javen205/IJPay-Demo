package com.ijpay.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class TemplateController {
    private static final Logger log = LoggerFactory.getLogger(TemplateController.class);

    @GetMapping(value = "/template")
    public String template(RedirectAttributes redirectAttributes) {
        System.out.println("11111111");
        log.error("sdfsfasf");
        redirectAttributes.addAttribute("message", "test");

        return "index";
    }
    @GetMapping(value = "/tt")
    public String tt(){
        return "test";
    }
}
