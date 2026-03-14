package com.gwc.controller.user;

import com.gwc.vo.LoginVO;
import com.gwc.entity.Result;
import com.gwc.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/login")
@Tag(name = "登录相关操作", description = "忘记密码,登录等")
public class LoginControlller {
    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    @Operation(description = "账号登录")
    public Result login(@RequestBody LoginVO loginVO) {
        return  userService.login(loginVO);
    }
}
