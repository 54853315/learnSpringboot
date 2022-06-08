package pers.learn.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.learn.common.util.security.CipherUtils;
import pers.learn.system.dto.UserRegisterBodyDto;
import pers.learn.system.entity.User;
import pers.learn.system.mapper.UserMapper;
import pers.learn.system.service.UserService;

import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    public void register(UserRegisterBodyDto requestBody) {
        User user = new User();
        user.setName(requestBody.getUsername());
        user.setEmail(requestBody.getEmail());
        // 生成随机salt
        Map<Object, String> saltMap = CipherUtils.generateSlat(requestBody.getPassword());
        // 给用户设置密码和salt
        user.setPassword(saltMap.get("password")).setSalt(saltMap.get("salt"));
        userMapper.insert(user);
    }
}
