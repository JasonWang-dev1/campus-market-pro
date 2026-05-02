package com.campusmarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campusmarket.common.BusinessException;
import com.campusmarket.common.ResultCode;
import com.campusmarket.dto.LoginDTO;
import com.campusmarket.entity.User;
import com.campusmarket.mapper.UserMapper;
import com.campusmarket.service.UserService;
import com.campusmarket.security.JwtTokenUtil;
import com.campusmarket.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(User user) {
        // 检查用户名是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (count > 0) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 默认值
        user.setRole(0);
        user.setStatus(0);

        userMapper.insert(user);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        if (user.getStatus() == 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }

        // 生成token
        String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername());

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        return vo;
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(User user) {
        User exist = userMapper.selectById(user.getId());
        if (exist == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 不允许通过这些接口修改密码和用户名
        user.setUsername(null);
        user.setPassword(null);
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR, "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }
}
