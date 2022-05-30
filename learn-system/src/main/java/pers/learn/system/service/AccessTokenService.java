package pers.learn.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import pers.learn.system.entity.AccessToken;

@Service
public interface AccessTokenService extends IService<AccessToken> {
    public AccessToken getBySessionId(String sessionId);
}
