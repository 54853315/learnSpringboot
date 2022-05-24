package pers.learn.blog.entity;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.lang.Nullable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Table
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
public class Role extends Model<BackendUser> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "role")
    private Long id;
    private String name, sign, description;
    @Column(columnDefinition = "int default 0", nullable = false)
    private Integer sort;
    @Column(columnDefinition = "int default 1", nullable = false)
    private Integer status;

    // 权限，可能为null
    @Nullable
    @TableField(exist = false)
    @Transient
    // 关联权限信息字段，如果为null则不显示此字段
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Permission> permissions;
}
