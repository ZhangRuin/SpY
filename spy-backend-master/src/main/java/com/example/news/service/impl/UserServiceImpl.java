package com.example.news.service.impl;

import com.example.news.dao.UserRepository;
import com.example.news.pojo.entity.mysql.User;
import com.example.news.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Boolean loginCheck(String userId, String password) {
        // 验证输入
        if (userId == null || userId.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        // 查找用户
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;  // 用户不存在
        }

        // 验证密码
        if (passwordEncoder.matches(password, user.getPassword())) {
            return true;  // 登录成功
        } else {
            return false;  // 密码不匹配
        }
    }

    @Override
    public Boolean register(String userId, String password) {
        // 验证输入
        if (userId == null || userId.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        // 检查用户是否存在
        if (userRepository.existsByUserId(userId)) {
            return false;  // 用户已存在
        }

        // 密码加密
        String encodedPassword = passwordEncoder.encode(password);

        // 创建用户
        User user = new User();
        user.setUserId(userId);
        user.setPassword(encodedPassword);

        // 存储用户信息
        userRepository.save(user);

        return true;  // 注册成功
    }


}
