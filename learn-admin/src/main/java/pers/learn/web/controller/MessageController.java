package pers.learn.web.controller;

import java.util.List;

import pers.learn.system.entity.Message;
import pers.learn.system.mapper.MessageMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 整个Message功能都是基于springboot+mybatis实现
@RestController
public class MessageController {
    @Autowired
    private MessageMapper messageMapper;

    @GetMapping(value = "/home")
    public void index() {
        System.out.println("homepage");
    }

    // @RequestParam将请求参数区数据映射到功能处理方法的参数上
    @RequestMapping("/detail")
    public Object detail(@RequestParam(value = "message_id") Integer id) {
        return messageMapper.getMessageById(id);
    }

    @PostMapping("/add")
    public String addMessage(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "url", required = false) String url) {
        messageMapper.insertMessage(name, content, url);
        return "success";
    }

    @RequestMapping("/list")
    public List<Message> getMessageList(@RequestParam(value = "name", required = false) String name) {
        if (name != null) {
            return messageMapper.listByName(name);
        } else {
            return messageMapper.list();
        }
    }
}
