package com.ijpay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ijpay.entity.Boy;
import com.ijpay.model.BoyRepository;

@Service
public class BoyService {
    @Autowired
    private BoyRepository boyRepository;

    @Transactional
    public void insertTwo(){
        Boy boy = new Boy();
        boy.setAge(23);
        boy.setName("mimi");
        boyRepository.save(boy);

        int i = 1/0;
        System.out.println(i);

        Boy boy2 = new Boy();
        boy2.setAge(24);
        boy2.setName("JJ");
        boyRepository.save(boy2);
    }
}
