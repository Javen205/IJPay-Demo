package com.ijpay.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ijpay.entity.Boy;
import com.ijpay.model.BoyRepository;
import com.ijpay.service.BoyService;

@RestController
public class BoyContrller {
    @Autowired
    private BoyRepository boyRepository;

    @Autowired
    private BoyService boyService;

    /**
     * 查新所有数据
     * @return
     */
    @RequestMapping(value = "/getAllBoys" , method = RequestMethod.GET)
    public List<Boy> getAllBoys(){
        return boyRepository.findAll();
    }
    @RequestMapping(value = "/saveBoy" , method = RequestMethod.GET)
    public void saveBoy(){
        Boy boy = new Boy();
        boy.setAge(23);
        boy.setName("mimi");
        boyRepository.save(boy);

    }

    /**
     * 测试事务
     */
    @RequestMapping(value = "/save" , method = RequestMethod.GET)
    public void save(){
        boyService.insertTwo();
    }
}
