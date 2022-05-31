package pers.learn.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import pers.learn.system.entity.AccessToken;

import java.util.Date;
import java.util.List;

@Service
public interface AccessTokenService extends IService<AccessToken> {
    /**
     * 根据session id获取会话数据
     * @param sessionId
     * @return
     */
    public AccessToken getBySessionId(String sessionId);

    /**
     * 清理用户缓存
     * @param loginName
     * @param sessionId
     */
    void removeUserCache(String loginName, String sessionId);

    /**
     * 查询过期的会话集合
     * @param expiredDate
     * @return
     */
    List<AccessToken> selectTokenByExpired(Date expiredDate);
}
