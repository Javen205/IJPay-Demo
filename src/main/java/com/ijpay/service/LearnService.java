package com.ijpay.service;

import com.ijpay.entity.LearnResouce;

import java.util.List;
import java.util.Map;

public interface LearnService {
    int add(LearnResouce learnResouce);
    int update(LearnResouce learnResouce);
    int deleteByIds(String[] ids);
    LearnResouce queryLearnResouceById(Long learnResouce);
    List<LearnResouce> queryLearnResouceList(Map<String, Object> params);
}