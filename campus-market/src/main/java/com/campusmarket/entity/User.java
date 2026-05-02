package com.campusmarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    private String avatar;

    /**
     * 角色 0-普通用户 1-管理员
     */
    private Integer role;

    /**
     * 状态 0-正常 1-禁用
     */
    private Integer status;

    /**
     * 信用评分
     */
    private BigDecimal creditScore;

    /**
     * 发布商品数量
     */
    private Integer productCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
