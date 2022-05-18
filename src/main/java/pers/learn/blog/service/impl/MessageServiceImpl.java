package pers.learn.blog.service.impl;

import pers.learn.blog.mapper.MessageMapper;
import pers.learn.blog.service.MessageService;

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
