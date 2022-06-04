package pers.learn.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pers.learn.common.enums.OnlineStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table
public class OnlineSession implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    // 使用mysql的自增字段
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "online_session")
    private Long id;

    // 用户Id
    private Long userId;

    // 登录名
    private String loginName;

    //登录IP地址
    private String ipaddr;

    // 用户会话id
    private String sessionId;

    //session创建时间
    @NonNull
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    @Column(columnDefinition = "datetime")
    private Date createTime;

    //session最后访问时间
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    @Column(columnDefinition = "datetime")
    private Date lastAccessTime;

    // 超时时间，单位为分钟
    private Long expireTime;

    @Column(columnDefinition = "int default 0", nullable = false)
    private OnlineStatus status = OnlineStatus.online;

    public OnlineSession(Long userId, String loginName, String ipaddr, String sessionId, @NonNull Date createTime, Date lastAccessTime, Long expireTime, OnlineStatus status) {
        this.userId = userId;
        this.loginName = loginName;
        this.ipaddr = ipaddr;
        this.sessionId = sessionId;
        this.createTime = createTime;
        this.lastAccessTime = lastAccessTime;
        this.expireTime = expireTime;
        this.status = status;
    }
}
