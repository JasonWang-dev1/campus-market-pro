package com.campusmarket.service;

import com.campusmarket.dto.LoginDTO;
import com.campusmarket.entity.User;
import com.campusmarket.vo.LoginVO;

public interface UserService {

    /**
     * 用户注册
     */
    void register(User user);

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto);

    /**
     * 根据ID查询用户
     */
    User getById(Long id);

    /**
     * 更新用户信息
     */
    void update(User user);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);
}
