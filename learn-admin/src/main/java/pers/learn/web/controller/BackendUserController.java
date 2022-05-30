package pers.learn.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pers.learn.system.entity.BackendUser;
import pers.learn.system.dto.BackendUserLoginBodyDto;
import pers.learn.common.response.CommonResponse;
import pers.learn.system.service.impl.BackendUserServiceImpl;

@RestController
@RequestMapping("/admin/auth")
public class BackendUserController {
    @Autowired
    private BackendUserServiceImpl backendUserServiceImpl;

    @PostMapping(value = "/login")
    @RequiresGuest
    public CommonResponse<String> login(@RequestBody BackendUserLoginBodyDto requestBody) {
        UsernamePasswordToken token = new UsernamePasswordToken(requestBody.username, requestBody.password);
        Subject subject = SecurityUtils.getSubject();
        
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            return CommonResponse.fail("登录失败，请检查账号密码");
        }
        System.out.println("登陆完成" + subject);
        return CommonResponse.success();
    }

    /**
     * @example:
     *           {
     *           "code": "200 OK",
     *           "message": "success",
     *           "data": {
     *           "id": 1,
     *           "name": "看报纸的小老头",
     *           "email": "abc@qq.com",
     *           "roleId": 3,
     *           "createTime": "2022-05-23 16-06-03",
     *           "updateTime": null
     *           },
     *           "timestamp": "2022-05-23 21:16:11"
     *           }
     * @return CommonResponse<BackendUser>
     */
    @GetMapping(value = "/info")
    public CommonResponse<BackendUser> currentUserInfo() {
        BackendUser user = (BackendUser) SecurityUtils.getSubject().getPrincipal();
        return CommonResponse.returnResult(user);
    }

    @GetMapping(value = "/notLogin")
    public CommonResponse<String> notLogin() {
        return CommonResponse.fail("您尚未登录");
    }

    @GetMapping(value = "/unauthorized")
    public CommonResponse<String> unauthorized() {
        return CommonResponse.fail("您没有权限");
    }

    @GetMapping(value = "/logout")
    public CommonResponse<String> logout() {
        SecurityUtils.getSubject().logout();
        return CommonResponse.fail("成功退出");
    }
}
