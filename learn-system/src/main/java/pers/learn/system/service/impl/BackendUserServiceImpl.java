package pers.learn.system.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

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

    public Role getRoleByUser(BackendUser user) {
        return roleMapper.selectById(user.getRoleId());
    }

}
