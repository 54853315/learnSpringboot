package pers.learn.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pers.learn.common.constant.Auth;
import pers.learn.common.exception.ApiException;
import pers.learn.common.response.CommonResponse;
import pers.learn.common.util.security.JwtUtils;
import pers.learn.framework.shiro.service.ShiroTokenService;
import pers.learn.framework.shiro.token.BearerToken;
import pers.learn.framework.shiro.token.PasswordToken;
import pers.learn.system.dto.BackendUserLoginBodyDto;
import pers.learn.system.dto.UserLoginBodyDto;
import pers.learn.system.entity.BackendUser;
import pers.learn.system.entity.User;
import pers.learn.system.service.impl.BackendUserServiceImpl;
import pers.learn.system.service.impl.UserServiceImpl;

@RestController
public class AuthController {
    @Autowired
    private BackendUserServiceImpl backendUserService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ShiroTokenService shiroTokenService;

    @PostMapping(value = "/admin/auth/login")
    public CommonResponse<Object> backendUserLogin(@RequestBody BackendUserLoginBodyDto requestBody) {
        PasswordToken userToken = new PasswordToken(requestBody.username, requestBody.password, Auth.BACKEND_USER);
        String accessToken = this.authorization(userToken);
        // 当authorization中不返回token时，则用下面的写法
//        BackendUser user = (BackendUser) SecurityUtils.getSubject().getPrincipal();
//        String accessToken = JwtUtils.generateToken(user.getName());
//        shiroTokenService.saveToken(accessToken, user);
        return CommonResponse.returnResult("userToken", accessToken);
    }

    @PostMapping("/user/auth/login")
    public CommonResponse<Object> userLogin(@RequestBody UserLoginBodyDto requestBody) {
        PasswordToken userToken = new PasswordToken(requestBody.username, requestBody.password, Auth.USER);
        String accessToken = this.authorization(userToken);
        // 当authorization中不返回token时，则用下面的写法
//        User user = (User) SecurityUtils.getSubject().getPrincipal();
//        String accessToken = JwtUtils.generateToken(user.getName());
//        shiroTokenService.saveToken(accessToken, user);
        return CommonResponse.returnResult("userToken", accessToken);
    }

    /**
     * 处理登录认证
     * 使用Shiro进行认证
     *
     * @param authenticationToken
     * @return
     */
    private String authorization(AuthenticationToken authenticationToken) {
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(authenticationToken);
        } catch (AuthenticationException e) {
            throw new ApiException("登录失败，请检查账号密码");
        }
        // 如果不希望由authorization处理token生成，则注释掉此处所有代码
        Object principal = SecurityUtils.getSubject().getPrincipal();
        String accessToken;
        if (principal instanceof BackendUser) {
            BackendUser backendUser = (BackendUser) principal;
            accessToken = JwtUtils.generateToken(backendUser.getName(), Auth.BACKEND_USER);
            shiroTokenService.saveToken(accessToken, backendUser);
        } else {
            User user = (User) principal;
            accessToken = JwtUtils.generateToken(user.getName(), Auth.USER);
            shiroTokenService.saveToken(accessToken, user);
        }
        return accessToken;
    }

    /**
     * @return CommonResponse<BackendUser>
     * @example: {
     * "code": "200 OK",
     * "message": "success",
     * "data": {
     * "id": 1,
     * "name": "看报纸的小老头",
     * "email": "abc@qq.com",
     * "roleId": 3,
     * "createTime": "2022-05-23 16-06-03",
     * "updateTime": null
     * },
     * "timestamp": "2022-05-23 21:16:11"
     * }
     */
    @GetMapping(value = "/admin/auth/info")
    public CommonResponse<BackendUser> currentUserInfo() {
        BackendUser user = (BackendUser) SecurityUtils.getSubject().getPrincipal();
        return CommonResponse.returnResult(user);
    }

    @GetMapping(value = {"/admin/auth/logout", "/auth/logout"})
    public CommonResponse<String> logout() {
        SecurityUtils.getSubject().logout();
        return CommonResponse.fail("成功退出");
    }

    @GetMapping(value = "/notLogin")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse<String> notLogin() {
        return CommonResponse.fail("您尚未登录");
    }

    @GetMapping(value = "/unauthorized")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse<String> unauthorized() {
        return CommonResponse.fail("您没有权限");
    }

}
