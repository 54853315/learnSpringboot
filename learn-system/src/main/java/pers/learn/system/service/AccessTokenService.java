package pers.learn.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.learn.system.entity.AccessToken;
import pers.learn.system.entity.OnlineSession;

import java.util.Date;
import java.util.List;

public interface AccessTokenService extends IService<AccessToken> {
    public AccessToken selectByToken(String token);

    void removeUserCache(String loginName, String token);

    List<AccessToken> selectTokenByExpired(Date expiredDate);
}
