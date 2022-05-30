package pers.learn.framework.shiro.session;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.shiro.session.mgt.SimpleSession;
import pers.learn.common.enums.OnlineStatus;

@Data
@NoArgsConstructor
public class DbSession extends SimpleSession {
    // 用户id
    private Long userId;
    // 登录名
    private String loginName;
    // 登录ip
//    private String host;
    // 在线状态
    private OnlineStatus status = OnlineStatus.online;
}
