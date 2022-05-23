package pers.learn.blog.controller;

import java.security.Security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pers.learn.blog.request.BackendUserLoginBodyDto;
import pers.learn.blog.response.CommonResponse;
import pers.learn.blog.service.BackendUserService;
import pers.learn.blog.service.impl.BackendUserServiceImpl;

@RestController
public class BackendUserController {
    @Autowired
    private BackendUserServiceImpl backendUserServiceImpl;

    @PostMapping(value = "/backend/login")
    public CommonResponse<String> login(@RequestBody BackendUserLoginBodyDto requestBody) {
        UsernamePasswordToken token = new UsernamePasswordToken(requestBody.username, requestBody.password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            return CommonResponse.fail(e.getMessage());
        }
        System.out.println("登陆完成" + subject);
        return CommonResponse.success();
    }

    @GetMapping(value = "/backend/user/info")
    public CommonResponse<Object> currentUserInfo() {
        Subject subject = SecurityUtils.getSubject();
        System.out.println(subject);
        return CommonResponse.returnResult(subject.getPrincipal());
    }
}
