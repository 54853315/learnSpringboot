package com.example.ME.DEMO.service.impl;

import com.example.ME.DEMO.mapper.MessageMapper;
import com.example.ME.DEMO.service.MessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Override
    @Transactional
    public void tranfor() {
        
    }
}
