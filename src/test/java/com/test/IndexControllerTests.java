package com.test;


import com.DemoApplication;
import com.ijpay.controller.IndexController;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DemoApplication.class)
public class IndexControllerTests {

    //mock api 模拟http请求
    private MockMvc mockMvc;

    //初始化工作
    @Before
    public void setUp() {
        //独立安装测试
        mockMvc = MockMvcBuilders.standaloneSetup(new IndexController()).build();
        //集成Web环境测试（此种方式并不会集成真正的web环境，而是通过相应的Mock API进行模拟测试，无须启动服务器）
        //mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    //测试
    @Test
    public void index() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/xxx")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("id>2")));
    }



}