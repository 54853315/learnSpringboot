package pers.learn.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pers.learn.blog.entity.BackendUser;
import pers.learn.blog.mapper.BackendUserMapper;
import pers.learn.blog.service.BackendUserService;

@Service
public class BackendUserServiceImpl extends ServiceImpl<BackendUserMapper, BackendUser> implements BackendUserService {
    @Autowired
    private BackendUserMapper backendUserMapper;
    @Autowired
    private ModelMapper modelMapper;

}
