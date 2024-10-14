package com.example.news.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.example.news.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/usr", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping("/doLogin")
    public SaResult doLogin(@RequestParam String userId, @RequestParam String pwd) {
        if (userService.loginCheck(userId, pwd)) {
            StpUtil.login(userId);
            return SaResult.ok("登录成功");
        }
        return SaResult.error("登录失败");
    }

    @PostMapping("/register")
    public SaResult register(@RequestParam String userId, @RequestParam String pwd) {
        if (userService.register(userId, pwd)) {
            return doLogin(userId, pwd);
        } else {
            return SaResult.error("注册失败");
        }
    }

    @GetMapping("logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok("退出登录成功");
    }
}
