package com.ijpay.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by Javen on 2017/2/26.
 */
@Controller
public class TemplateController {
    private static final Logger log = LoggerFactory.getLogger(TemplateController.class);

    @GetMapping
    public String template(RedirectAttributes redirectAttributes){
        System.out.println("11111111");
        log.error("sdfsfasf");
        redirectAttributes.addAttribute("message","test");

        return "index";
    }
}
