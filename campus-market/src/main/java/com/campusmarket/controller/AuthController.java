package com.campusmarket.controller;

import com.campusmarket.common.Result;
import com.campusmarket.dto.LoginDTO;
import com.campusmarket.entity.User;
import com.campusmarket.service.UserService;
import com.campusmarket.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody User user) {
        userService.register(user);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.success(vo);
    }
}
