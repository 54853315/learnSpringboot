package pers.learn.system.service.impl;

import pers.learn.system.mapper.MessageMapper;
import pers.learn.system.service.MessageService;

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
