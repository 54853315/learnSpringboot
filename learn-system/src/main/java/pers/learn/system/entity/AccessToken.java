package pers.learn.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import pers.learn.common.enums.OnlineStatus;
import pers.learn.common.util.DateAdopter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Accessors(chain = true)
@Table
public class AccessToken implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    // 使用mysql的自增字段
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "access_token")
    // 普通ID自增而非雪花
    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户Id
    private Long userId;

    // 登录名
    private String loginName;

    // 用户token
    private String accessToken;

    // 端 admin(E:BackendUser)|api(E:User)
    private String endpoint;

    //创建时间
    @NonNull
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = DateAdopter.PATTERN_DATETIME)
    @Column(columnDefinition = "datetime")
    private LocalDateTime createTime;

    //最后访问时间
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = DateAdopter.PATTERN_DATETIME)
    @Column(columnDefinition = "datetime")
    private LocalDateTime lastAccessTime;

    // 超时时间，单位为分钟
    private Long expireTime;

}