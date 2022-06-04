package pers.learn.system.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pers.learn.system.entity.BackendUser;
import pers.learn.system.entity.Role;
import pers.learn.system.mapper.BackendUserMapper;
import pers.learn.system.mapper.RoleMapper;
import pers.learn.system.service.BackendUserService;

@Service
public class BackendUserServiceImpl extends ServiceImpl<BackendUserMapper, BackendUser> implements BackendUserService {
    @Autowired
    private BackendUserMapper backendUserMapper;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleMapper roleMapper;

    public void register(BackendUser user){
        // 生成随机salt
        String saltString = "";
        // 给用户设置salt
//        user.setSalt(saltString);
        //明文密码进行散列算法1024次，并加盐
        Md5Hash md5Hash = new Md5Hash(user.getPassword(), saltString, 1024);
        //将转成16进制的密码封装到user对象中
        user.setPassword(md5Hash.toHex());
        backendUserMapper.insert(user);
    }

    public Role getRoleByUser(BackendUser user) {
        return roleMapper.selectById(user.getRoleId());
    }

}
