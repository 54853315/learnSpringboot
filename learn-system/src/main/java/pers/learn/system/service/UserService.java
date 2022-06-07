package pers.learn.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import pers.learn.system.entity.User;
import pers.learn.system.mapper.UserMapper;

@Service
public interface UserService extends IService<User> {
}
