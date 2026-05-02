package com.campusmarket.vo;

import lombok.Data;

/**
 * 登录响应
 */
@Data
public class LoginVO {

    /**
     * JWT令牌
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色 0-普通用户 1-管理员
     */
    private Integer role;
}
